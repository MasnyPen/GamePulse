use gfxinfo::active_gpu;
use sysinfo::{Components, Disks, System};
use crate::models::{CPUData, DiskData, GPUData, MemoryData, SysStats, SystemData};

pub fn fetch_data() -> SysStats {
    // Sys Init
    let mut sys = System::new_all();

    // Refresh system information:
    sys.refresh_all();

    // System
    let system_data = SystemData {
        name: System::name().unwrap(),
        kernel: System::kernel_version().unwrap(),
        os_version: System::os_version().unwrap(),
        hostname: System::host_name().unwrap(),
    };

    // Memory
    let memory_data = MemoryData {
        total: sys.total_memory(),
        used: sys.used_memory(),
        total_swap: sys.total_swap(),
        used_swap: sys.used_swap(),
    };

    // CPU
    let mut highest_frequency = 0;

    for (_, cpu) in sys.cpus().iter().enumerate() {
        if highest_frequency < cpu.frequency() {
            highest_frequency = cpu.frequency();
        }
    }

    let pkg_temp = Components::new_with_refreshed_list()
        .iter()
        .find(|c| c.label().contains("Package id"))
        .map(|c| c.temperature());

    let cpu_data = CPUData {
        name: sys.cpus()[0].brand().parse().unwrap(),
        threads: sys.cpus().len() as u8,
        temperature: pkg_temp.unwrap().unwrap() as f64,
        frequency: highest_frequency,
        used_percentage: sys.global_cpu_usage() as f64,
    };


    // GPU
    let gpu = active_gpu().unwrap();

    let gpu_data = GPUData {
        vendor: gpu.vendor().parse().unwrap(),
        model: gpu.model().parse().unwrap(),
        total_vram: gpu.info().total_vram(),
        used_vram: gpu.info().used_vram(),
        load_percentage: gpu.info().load_pct() as f64,
        temperature: gpu.info().temperature() as f64,
    };


    // Disks
    let disks_list = Disks::new_with_refreshed_list();

    let mut disks: Vec<DiskData> = Vec::new();

    for disk in &disks_list {
        disks.push(
            DiskData {
                device: disk.name().to_os_string().into_string().unwrap(),
                filesystem: disk.file_system().to_os_string().into_string().unwrap(),
                total_space: disk.total_space(),
                used_space: disk.usage().total_written_bytes,
                mounted_on: disk.mount_point().to_str().unwrap().to_owned(),
            }
        )
    }


    SysStats {
        system: system_data,
        memory: memory_data,
        cpu: cpu_data,
        disks,
        gpu: gpu_data
    }
}
