use std::process::exit;
use tokio_tungstenite::connect_async;
use futures_util::{SinkExt, StreamExt};
use sysinfo::System;
use tokio_tungstenite::tungstenite::Message;
use crate::data_fetcher::fetch_data;
use crate::models::PacketType;

pub mod models;
pub mod data_fetcher;

#[tokio::main]
async fn main() {
    println!("Connecting to websocket...");

    let (mut socket, _) =
        connect_async("ws://localhost:8080/monitor")
            .await
            .unwrap();

    socket.send(Message::text("GPULSE_AUTH;token")).await.unwrap();

    let (mut write, mut read) = socket.split();

    // Read packets
    let reader = tokio::spawn(async move {
        while let Some(msg) = read.next().await {
            let msg = msg.unwrap().to_string();
            let mut parts = msg.split(";");

            let packet_type_str = parts.next().unwrap();
            let packet_type = PacketType::from_str(packet_type_str).unwrap();

            let data = parts.next().unwrap();

            if packet_type == PacketType::GPULSE_AUTH_ACK {
                println!("Connected!");
            }

            if packet_type == PacketType::GPULSE_AUTH_ERR {
                println!("Auth Error. Token invalid");
                exit(0)
            }
        }
    });

    // Write packets
    let writer = tokio::spawn(async move {

        loop {
            let data = fetch_data();

            let json = serde_json::to_string(&data);
            let mut message = String::from(PacketType::GPULSE_SYSSTATS.as_str());
            message.push_str(";");
            message.push_str(json.unwrap().as_str());

            let text = tokio_tungstenite::tungstenite::Utf8Bytes::from(message);

            write.send(Message::Text(text)).await.expect("Error: Cannot send stats message to server!");

            tokio::time::sleep(std::time::Duration::from_secs(5)).await;
        }
    });

    tokio::try_join!(reader, writer).unwrap();
}