# GamePulse Dashboard

The GamePulse Dashboard is a modern React + Vite web application for live monitoring of all connected machines. It displays real‑time charts, temperatures, utilization metrics, alerts, and machine status updates. The dashboard connects to the backend via REST and WebSocket to provide instant updates without manual refresh. It is designed for tournament administrators who need a clear, fast, and responsive overview of hardware health.

## Local setup

```bash
cd dashboard
npm install
npm run dev
```
This starts the Vite development server and serves the dashboard locally.

The dashboard assumes the Spring Boot backend runs on `http://localhost:8080`.
To override it, create `.env`:

```bash
VITE_API_BASE_URL=http://localhost:8080
```

## Quality & Build Checks

Run linting:
```bash
npm run lint
```

Build the production bundle:
```bash
npm run build
```

This generates optimized static assets in dist/, ready for deployment.