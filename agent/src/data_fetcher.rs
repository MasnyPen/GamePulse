
use gfxinfo::active_gpu;
use sysinfo::{Components, Disks, System};

use crate::models::{
    CPUData,
    DiskData,
    GPUData,
    MemoryData,
    SysStats,
    SystemData,
};

pub fn fetch_data() -> SysStats {
    // System initialization
    let mut sys = System::new_all();
    sys.refresh_all();

    // System information
    let system_data = SystemData {
        name: System::name().unwrap_or_else(|| "Unknown".to_string()),
        kernel: System::kernel_version().unwrap_or_else(|| "Unknown".to_string()),
        os_version: System::os_version().unwrap_or_else(|| "Unknown".to_string()),
        hostname: System::host_name().unwrap_or_else(|| "Unknown".to_string()),
    };

    // Memory
    let memory_data = MemoryData {
        total: sys.total_memory(),
        used: sys.used_memory(),
        total_swap: sys.total_swap(),
        used_swap: sys.used_swap(),
    };

    // CPU
    let cpus = sys.cpus();

    let highest_frequency = cpus
        .iter()
        .map(|cpu| cpu.frequency())
        .max()
        .unwrap_or(0);

    let cpu_name = cpus
        .first()
        .map(|cpu| cpu.brand().to_string())
        .filter(|name| !name.trim().is_empty())
        .unwrap_or_else(|| "Unknown".to_string());

    let threads = cpus.len().min(u8::MAX as usize) as u8;

    // CPU temperature
    let components = Components::new_with_refreshed_list();

    let pkg_temp = components
        .iter()
        .find(|c| {
            let label = c.label().to_lowercase();

            label.contains("package id")
                || label.contains("tctl")
                || label.contains("tdie")
        })
        .and_then(|c| c.temperature())
        .unwrap_or(0.0);

    let cpu_data = CPUData {
        name: cpu_name,
        threads,
        temperature: pkg_temp as f64,
        frequency: highest_frequency,
        used_percentage: sys.global_cpu_usage() as f64,
    };

    // GPU
    let gpu_data = match active_gpu() {
        Ok(gpu) => {
            let info = gpu.info();

            GPUData {
                vendor: gpu.vendor().to_string(),
                model: gpu.model().to_string(),
                total_vram: info.total_vram(),
                used_vram: info.used_vram(),
                load_percentage: info.load_pct() as f64,
                temperature: info.temperature() as f64,
            }
        }

        Err(err) => {
            eprintln!("GPU not available: {}", err);

            GPUData {
                vendor: "Unknown".to_string(),
                model: "Unknown".to_string(),
                total_vram: 0,
                used_vram: 0,
                load_percentage: 0.0,
                temperature: 0.0,
            }
        }
    };

    // Disks
    let disks_list = Disks::new_with_refreshed_list();

    let disks: Vec<DiskData> = disks_list
        .iter()
        .map(|disk| {
            DiskData {
                device: disk.name().to_string_lossy().into_owned(),
                filesystem: disk.file_system().to_string_lossy().into_owned(),
                total_space: disk.total_space(),
                used_space: disk.usage().total_written_bytes,
                mounted_on: disk.mount_point().to_string_lossy().into_owned(),
            }
        })
        .collect();

    SysStats {
        system: system_data,
        memory: memory_data,
        cpu: cpu_data,
        disks,
        gpu: gpu_data,
    }
}
