# GamePulse Agent

The GamePulse Agent is a lightweight Rust application that runs on each monitored machine. It collects real‑time hardware metrics such as CPU load, GPU usage, temperatures, RAM consumption, disk activity, and network throughput. The agent sends telemetry to the GamePulse Server over a secure WebSocket connection and identifies itself using a persistent agent ID. It is optimized for low overhead, making it suitable for esports tournaments and LAN environments.

## ENV Variables

```
GAMEPULSE_AUTH_TOKEN=token
```
This token is used to authenticate the agent with the GamePulse backend.
You can expand this section if you want to describe the auth flow:
authentication flow

## Config files

`config.toml` - This is the main configuration file controlling how the agent connects to the server and how often it sends system statistics.

```toml
[websocket]
url = "localhost:8080/monitor"
timeout_ms = 2000
tls = false
accept_danger_cert = false

[sysstats]
packet_send_interval = 5 # seconds
```

- `websocket.url`
The WebSocket endpoint the agent connects to.
Example: localhost:8080/monitor means the agent connects to a local server on port 8080.

- `websocket.timeout_ms`  
Connection timeout in milliseconds. If the server does not respond within this time, the connection attempt fails.

- `websocket.tls`  
Enables TLS (wss://) when set to true.

    true → encrypted WebSocket

    false → plain WebSocket

- `websocket.accept_danger_cert`  
Allows accepting self‑signed or otherwise unsafe certificates.
Useful for development, not recommended for production.

- `sysstats.packet_send_interval`
Interval (in seconds) between sending system statistics packets (GPULSE_SYSSTATS).
Example: 5 → stats sent every 5 seconds.

`agent.id`

```
None/agentId
```

This file stores the agent’s unique identifier assigned by the GamePulse server.

    - If the file contains None, the agent has no assigned ID yet.

    - After successful authentication, the server sends an agent_id, which is saved here.

    - On subsequent runs, the agent uses this ID to identify itself without creating a new one.

You can expand this topic here:
agent ID

## Local setup

```bash
cd agent
cargo run
```
Runs the agent locally using the current source code.

## Checks

```bash
cargo test
```
Executes all unit tests in the project.

## Build Project

```bash
cargo build
```
Builds the project and produces the binary in target/debug/.