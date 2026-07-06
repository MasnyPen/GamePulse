# GamePulse Server

The GamePulse Server is a Spring Boot backend responsible for receiving, processing, and storing telemetry from all agents. It exposes REST and WebSocket endpoints used by the dashboard and handles authentication, agent registration, and snapshot retention. The server aggregates hardware data, triggers alerts for abnormal conditions, and provides historical performance insights. PostgreSQL is used as the primary database for storing metrics and agent metadata.

## ENV Variables
These variables configure authentication, database access, and server‑side behavior.

```
# Auth Token
GAMEPULSE_AUTH_TOKEN=token

# Database
PSQL_URI=jdbc:postgresql://localhost:5432/gamepulse
PSQL_USER=gamepulse
PSQL_PASS=gamepulse

# App Config
GAMEPULSE_OLD_SNAPSHOTS_THRESHOLD=1d
```

## Variable Overview
- `GAMEPULSE_AUTH_TOKEN`  
Token used by agents to authenticate against the server.

- `PSQL_URI`
JDBC connection string pointing to the PostgreSQL instance.

- `PSQL_USER`  
Database username for the GamePulse schema.

- `PSQL_PASS`  
Database password.

- `GAMEPULSE_OLD_SNAPSHOTS_THRESHOLD`
Defines how old snapshots must be before they are considered stale and eligible for cleanup.
Example: 1d → snapshots older than 1 day.

## Local setup

```bash
cd server
./gradlew bootRun
```

This launches the Spring Boot server with your local configuration.

## Checks

```bash
./gradlew test
```

Runs all unit and integration tests.

## Build Project

```bash
./gradlew build
```

Produces the application JAR and performs full compilation + test cycle.