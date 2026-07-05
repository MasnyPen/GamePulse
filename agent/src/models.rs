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

// Packet Type
#[derive(PartialEq, Debug)]
pub enum PacketType {
    GPULSE_AUTH,
    GPULSE_AUTH_ACK,
    GPULSE_AUTH_ERR,
    GPULSE_DENY,

    GPULSE_SYSSTATS,
}

impl PacketType {
    pub fn as_str(&self) -> &'static str {
        match self {
            PacketType::GPULSE_AUTH => "GPULSE_AUTH",
            PacketType::GPULSE_AUTH_ACK => "GPULSE_AUTH_ACK",
            PacketType::GPULSE_AUTH_ERR => "GPULSE_AUTH_ERR",
            PacketType::GPULSE_DENY => "GPULSE_DENY",
            PacketType::GPULSE_SYSSTATS => "GPULSE_SYSSTATS",
        }
    }
}

impl PacketType {
    pub fn from_str(s: &str) -> Option<Self> {
        match s {
            "GPULSE_AUTH" => Some(PacketType::GPULSE_AUTH),
            "GPULSE_AUTH_ACK" => Some(PacketType::GPULSE_AUTH_ACK),
            "GPULSE_AUTH_ERR" => Some(PacketType::GPULSE_AUTH_ERR),
            "GPULSE_DENY" => Some(PacketType::GPULSE_DENY),
            "GPULSE_SYSSTATS" => Some(PacketType::GPULSE_SYSSTATS),
            _ => None,
        }
    }
}

#[test]
fn packet_type_roundtrip() {
    let variants = [
        PacketType::GPULSE_AUTH,
        PacketType::GPULSE_AUTH_ACK,
        PacketType::GPULSE_AUTH_ERR,
        PacketType::GPULSE_DENY,
        PacketType::GPULSE_SYSSTATS,
    ];

    for packet in variants {
        assert_eq!(
            PacketType::from_str(packet.as_str()),
            Some(packet)
        );
    }
}

#[test]
fn packet_type_invalid_string() {
    assert_eq!(PacketType::from_str("ABC"), None);
    assert_eq!(PacketType::from_str(""), None);

}