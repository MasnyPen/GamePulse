pub struct SysStats {
    system: SystemData,
    memory: MemoryData,
    cpu: CPUData,
    disks: Vec<DiskData>,
    gpu: GPUData,
}

pub struct SystemData {
    name: String,
    kernel: String,
    os_version: String,
    host_name: String
}

pub struct MemoryData {
    total: f64,
    used: f64,
    total_swap: f64,
    used_swap: f64,
}

pub struct CPUData {
    threads: u8,
    temperature: u8,
    used_percentage: f64,
}

pub struct DiskData {
    device: String,
    filesystem: String,
    disk_type: DiskType,
    total_space: f64,
    used_space: f64,
    mounted_on: String
}

pub enum DiskType {
    SSD,
    HDD
}


pub struct GPUData {
    used_percentage: f64,
    temperature: u8,
}