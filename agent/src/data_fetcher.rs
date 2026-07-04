use gfxinfo::active_gpu;
use sysinfo::{Components, Cpu, Disks, System};
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
        total: round2(bytes_to_gib(sys.total_memory())),
        used: round2(bytes_to_gib(sys.used_memory())),
        total_swap: round2(bytes_to_gib(sys.total_swap())),
        used_swap: round2(bytes_to_gib(sys.used_swap())),
    };

    // CPU
    let mut highest_frequency = 0;

    for (i, cpu) in sys.cpus().iter().enumerate() {
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
        temperature: round1(pkg_temp.unwrap().unwrap() as f64),
        frequency: mhz_to_ghz(highest_frequency),
        used_percentage: round2(sys.global_cpu_usage() as f64),
    };


    // GPU
    let gpu = active_gpu().unwrap();

    let gpu_data = GPUData {
        vendor: gpu.vendor().parse().unwrap(),
        model: gpu.model().parse().unwrap(),
        total_vram: round2(bytes_to_gib(gpu.info().total_vram())),
        used_vram: round2(bytes_to_gib(gpu.info().used_vram())),
        load_percentage: round2(gpu.info().load_pct() as f64),
        temperature: round1(gpu.info().temperature() as f64),
    };


    // Disks
    let disks_list = Disks::new_with_refreshed_list();

    let mut disks: Vec<DiskData> = Vec::new();

    for disk in &disks_list {
        disks.push(
            DiskData {
                device: disk.name().to_os_string().into_string().unwrap(),
                filesystem: disk.file_system().to_os_string().into_string().unwrap(),
                total_space: round2(bytes_to_gib(disk.total_space())),
                used_space: round2(bytes_to_gib(disk.usage().total_written_bytes)),
                mounted_on: disk.mount_point().to_str().unwrap().to_owned(),
            }
        )
    }


    return SysStats {
        system: system_data,
        memory: memory_data,
        cpu: cpu_data,
        disks: disks,
        gpu: gpu_data
    };
}

// ====== ROUND HELPERS ======

#[inline]
pub fn round2(x: f64) -> f64 {
    (x * 100.0).round() / 100.0
}

#[inline]
pub fn round1(x: f64) -> f64 {
    (x * 10.0).round() / 10.0
}

#[inline]
pub fn round0(x: f64) -> f64 {
    x.round()
}

// ====== CONVERSIONS ======

pub fn bytes_to_gib(bytes: u64) -> f64 {
    round2(bytes as f64 / 1024f64.powi(3))
}

pub fn mhz_to_ghz(mhz: u64) -> f64 {
    round2(mhz as f64 / 1000f64)
}
