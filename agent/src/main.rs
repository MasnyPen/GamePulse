use std::process::exit;
use std::sync::Arc;
use std::time::Duration;
use tokio_tungstenite::connect_async;
use futures_util::{SinkExt, StreamExt};
use sysinfo::System;
use tokio::sync::Notify;
use tokio::time::timeout;
use tokio_tungstenite::tungstenite::Message;
use crate::config_mgmt::{load_agent_id_file, load_config, save_agent_id};
use crate::data_fetcher::fetch_data;
use crate::models::{AuthACKPacketData, AuthERRPacketData, AuthPacketData, PacketType};

pub mod models;
pub mod data_fetcher;
pub mod config_mgmt;

#[tokio::main]
async fn main() {
    println!("Starting GamePulse Agent...");

    // -------------------------
    // Reading config files
    // -------------------------

    println!("Reading config...");

    let config = load_config().expect("Error loading config");

    println!("Reading agent.id...");

    let agent_id = load_agent_id_file().unwrap();

    println!("Reading complete!");

    // -------------------------
    // Connect to server
    // -------------------------

    println!("Connecting to websocket...");

    let mut conn_url = String::from("ws://");
    conn_url.push_str(config.websocket.url.as_str());

    let result = timeout(
        Duration::from_millis(config.websocket.timeout_ms),
        connect_async(conn_url)
    ).await;

    let (mut socket, _) = match result {
        Ok(Ok(conn)) => conn,
        Ok(Err(e)) => panic!("Failed to connect to websocket: {e}"),
        Err(e) => panic!("Websocket timeout"),
    };

    // -------------------------
    // AUTH
    // -------------------------

    let token = std::env::var("GAMEPULSE_AUTH_TOKEN")
        .expect("GAMEPULSE_AUTH_TOKEN not set");

    let auth_packet_data = AuthPacketData {
        token,
        agent_id,
    };
    let json = serde_json::to_string(&auth_packet_data);

    let mut packet_message = String::from("GPULSE_AUTH;");
    packet_message.push_str(json.unwrap().as_str());

    socket.send(Message::text(packet_message)).await.unwrap();

    // -------------------------
    // SPLIT SOCKET
    // -------------------------

    let (mut write, mut read) = socket.split();

    // -------------------------
    // Notify
    // -------------------------
    let notify = Arc::new(Notify::new());
    let notify_reader = notify.clone();
    let notify_writer = notify.clone();

    // -------------------------
    // Read Packets
    // -------------------------
    let reader = tokio::spawn(async move {
        while let Some(msg) = read.next().await {
            let msg = msg.unwrap().to_string();
            let mut parts = msg.splitn(2, ";");

            let packet_type_str = parts.next().unwrap();
            let packet_type = PacketType::from_str(packet_type_str).unwrap();

            let data = parts.next().unwrap();

            if packet_type == PacketType::GPULSE_AUTH_ACK {
                let json: AuthACKPacketData = match serde_json::from_str::<AuthACKPacketData>(data) {
                    Ok(packet) => { packet }
                    Err(e) => {
                        eprintln!("Invalid packet: {e}");
                        continue;
                    }
                };

                if let Err(_) = save_agent_id(json.agent_id) {
                    panic!("Error saving agentId");
                }

                println!("Connected!");
                notify_reader.notify_one();
            }

            if packet_type == PacketType::GPULSE_AUTH_ERR {
                let json: AuthERRPacketData = match serde_json::from_str::<AuthERRPacketData>(&data) {
                    Ok(packet) => { packet }
                    Err(e) => {
                        eprintln!("Invalid packet: {e}");
                        continue;
                    }
                };

                match json.error.as_str() {
                    "invalid_token" => eprintln!("Invalid token"),
                    "invalid_agent_id" => eprintln!("Invalid agent id"),
                    _ => eprintln!("Unknown error {}", json.error)
                }
                exit(0)
            }
        }
    });

    // -------------------------
    // Write Packets
    // -------------------------
    let writer = tokio::spawn(async move {
        notify_writer.notified().await;
        loop {
            let data = fetch_data();

            let json = serde_json::to_string(&data);
            let mut message = String::from(PacketType::GPULSE_SYSSTATS.as_str());
            message.push_str(";");
            message.push_str(json.unwrap().as_str());

            let text = tokio_tungstenite::tungstenite::Utf8Bytes::from(message);

            write.send(Message::Text(text)).await.expect("Error: Cannot send stats message to server!");

            tokio::time::sleep(std::time::Duration::from_secs(config.sysstats.packet_send_interval)).await;
        }
    });

    tokio::try_join!(reader, writer).unwrap();
}