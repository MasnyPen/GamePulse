use serde::{Deserialize, Serialize};

#[derive(Debug, Serialize, Deserialize)]
pub struct SysStats {
    pub system: SystemData,
    pub memory: MemoryData,
    pub cpu: CPUData,
    pub disks: Vec<DiskData>,
    pub gpu: GPUData,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct SystemData {
    pub name: String,
    pub kernel: String,
    pub os_version: String,
    pub hostname: String
}

#[derive(Debug, Serialize, Deserialize)]
pub struct MemoryData {
    pub total: f64,
    pub used: f64,
    pub total_swap: f64,
    pub used_swap: f64,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct CPUData {
    pub name: String,
    pub threads: u8,
    pub temperature: f64,
    pub frequency: f64,
    pub used_percentage: f64,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct DiskData {
    pub device: String,
    pub filesystem: String,
    pub total_space: f64,
    pub used_space: f64,
    pub mounted_on: String
}

#[derive(Debug, Serialize, Deserialize)]
pub struct GPUData {
    pub vendor: String,
    pub model: String,
    pub total_vram: f64,
    pub used_vram: f64,
    pub load_percentage: f64,
    pub temperature: f64,
}