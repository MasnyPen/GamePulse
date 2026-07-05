use std::{env, fs};
use std::path::PathBuf;
use serde::Deserialize;

const DEFAULT_CONFIG: &str = include_str!("../assets/config.toml");
const DEFAULT_AGENT_ID_FILE: &str = include_str!("../assets/agent.id");

fn config_path() -> PathBuf {
    let exe_dir = env::current_exe()
        .expect("cannot get exe path")
        .parent()
        .expect("no parent dir")
        .to_path_buf();

    exe_dir.join("config.toml")
}

pub fn load_config() -> Result<Config, Box<dyn std::error::Error>> {
    let path = config_path();

    if !path.exists() {
        fs::write(&path, DEFAULT_CONFIG)?;
        println!("Created default config at {:?}", path)
    }

    let content = fs::read_to_string(&path)?;

    let config: Config = toml::from_str(&content)?;

    Ok(config)
}

#[derive(Debug, Deserialize)]
pub struct Config {
    pub websocket: WebSocketConfig,
    pub sysstats: SysStatsConfig
}

#[derive(Debug, Deserialize)]
pub struct WebSocketConfig {
    pub url: String,
    pub timeout_ms: u64,
    pub tls: bool,
    pub accept_danger_cert: bool,
}

#[derive(Debug, Deserialize)]
pub struct SysStatsConfig {
    pub packet_send_interval: u64
}

fn agent_id_path() -> PathBuf {
    let exe_dir = env::current_exe()
        .expect("cannot get exe path")
        .parent()
        .expect("no parent dir")
        .to_path_buf();

    exe_dir.join("agent.id")
}

pub fn load_agent_id_file() -> Result<String, Box<dyn std::error::Error>> {
    let path = agent_id_path();
    if !path.exists() {
        fs::write(&path, DEFAULT_AGENT_ID_FILE)?;
        println!("Created default AgentId file at {:?}", path)
    }

    let content = fs::read_to_string(&path)?;

    Ok(content)
}

pub fn save_agent_id(agent_id: String) -> Result<bool, Box<dyn std::error::Error>> {
    let path = agent_id_path();

    fs::write(&path, agent_id).expect("Cannot write agentId to agent.id file!");

    Ok(true)
}