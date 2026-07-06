# GamePulse

GamePulse is a real-time hardware monitoring platform designed for gaming tournaments and LAN events.

It consists of a lightweight Rust agent that collects system metrics from each computer, a Spring Boot backend that processes and distributes telemetry, and a modern React (Vite) dashboard for live monitoring.

### Features

* ⚡ Real-time hardware monitoring
* 🖥️ CPU, GPU, RAM, disk, and network statistics
* 🌡️ Temperature and utilization tracking
* 📊 Live dashboard with automatic updates
* 🔔 Configurable alerts for abnormal hardware conditions
* 📈 Historical performance data
* 🔐 Secure communication between agents and server
* 🎮 Designed for esports tournaments, gaming cafés, and LAN events

### Technology Stack

* **Agent:** Rust
* **Server:** Java + Spring Boot
* **Dashboard:** React + Vite
* **Communication:** REST/WebSocket
* **Database:** PostgreSQL

GamePulse helps tournament organizers and administrators quickly identify hardware issues, overheating, performance bottlenecks, or offline machines before they impact players.
