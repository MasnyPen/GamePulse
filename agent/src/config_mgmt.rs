use std::{env, fs};
use std::path::PathBuf;
use serde::Deserialize;

const DEFAULT_CONFIG: &str = include_str!("../assets/config.toml");

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
    pub timeout_ms: u64
}

#[derive(Debug, Deserialize)]
pub struct SysStatsConfig {
    pub packet_send_interval: u64
}