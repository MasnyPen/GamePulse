# GamePulse Dashboard

## Local setup

```bash
cd dashboard
npm install
npm run dev
```

The dashboard assumes the Spring Boot backend runs on `http://localhost:8080`.
To override it, create `.env`:

```bash
VITE_API_BASE_URL=http://localhost:8080
```

## Checks

```bash
npm run lint
npm run build
```
