use tokio_tungstenite::connect_async;
use futures_util::{SinkExt, StreamExt};
use serde::Serialize;
use sysinfo::System;
use tokio_tungstenite::tungstenite::Message;
use crate::data_fetcher::fetch_data;

pub mod models;
pub mod data_fetcher;

#[tokio::main]
async fn main() {
    println!("Connecting to websocket...");

    let (mut socket, _) =
        connect_async("ws://localhost:8080/monitor")
            .await
            .unwrap();

    println!("Connected!");


    let (mut write, mut read) = socket.split();

    // Write packets
    let writer = tokio::spawn(async move {

        loop {
            let data = fetch_data();

            let json = serde_json::to_string(&data);
            let mut message = String::from("GPULSE_SYSSTATS:");
            message.push_str(json.unwrap().as_str());

            let text = tokio_tungstenite::tungstenite::Utf8Bytes::from(message);

            write.send(Message::Text(text)).await.expect("Error: Cannot send stats message to server!");

            tokio::time::sleep(std::time::Duration::from_secs(5)).await;
        }
    });

    // Read packets
    let reader = tokio::spawn(async move {
        while let Some(msg) = read.next().await {
            println!("msg: {:?}", msg);
        }
    });

    tokio::try_join!(writer, reader).unwrap();
}