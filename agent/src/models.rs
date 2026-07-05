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
    pub total: u64,
    pub used: u64,
    pub total_swap: u64,
    pub used_swap: u64,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct CPUData {
    pub name: String,
    pub threads: u8,
    pub temperature: f64,
    pub frequency: u64,
    pub used_percentage: f64,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct DiskData {
    pub device: String,
    pub filesystem: String,
    pub total_space: u64,
    pub used_space: u64,
    pub mounted_on: String
}

#[derive(Debug, Serialize, Deserialize)]
pub struct GPUData {
    pub vendor: String,
    pub model: String,
    pub total_vram: u64,
    pub used_vram: u64,
    pub load_percentage: f64,
    pub temperature: f64,
}

// Packet Type
#[derive(PartialEq, Debug)]
pub enum PacketType {
    GPulseAuth,
    GPulseAuthACK,
    GPulseAuthErr,
    GPulseDeny,
    GPulseSysStats,
}

impl PacketType {
    pub fn as_str(&self) -> &'static str {
        match self {
            PacketType::GPulseAuth => "GPULSE_AUTH",
            PacketType::GPulseAuthACK => "GPULSE_AUTH_ACK",
            PacketType::GPulseAuthErr => "GPULSE_AUTH_ERR",
            PacketType::GPulseDeny => "GPULSE_DENY",
            PacketType::GPulseSysStats => "GPULSE_SYSSTATS",
        }
    }
}

impl PacketType {
    pub fn from_str(s: &str) -> Option<Self> {
        match s {
            "GPULSE_AUTH" => Some(PacketType::GPulseAuth),
            "GPULSE_AUTH_ACK" => Some(PacketType::GPulseAuthACK),
            "GPULSE_AUTH_ERR" => Some(PacketType::GPulseAuthErr),
            "GPULSE_DENY" => Some(PacketType::GPulseDeny),
            "GPULSE_SYSSTATS" => Some(PacketType::GPulseSysStats),
            _ => None,
        }
    }
}

#[derive(Debug, Serialize, Deserialize)]
pub struct AuthPacketData {
    pub token: String,
    pub agent_id: String,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct AuthACKPacketData {
    pub agent_id: String
}

#[derive(Debug, Serialize, Deserialize)]
pub struct AuthERRPacketData {
    pub error: String
}

#[test]
fn packet_type_roundtrip() {
    let variants = [
        PacketType::GPulseAuth,
        PacketType::GPulseAuthACK,
        PacketType::GPulseAuthErr,
        PacketType::GPulseDeny,
        PacketType::GPulseSysStats,
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