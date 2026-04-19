# Church ERP Monorepo

Church ERP internal-network project baseline.

## Stack

- Backend: Spring Boot 3 + Maven Wrapper
- Frontend: React + Vite (responsive web)
- Database: MariaDB 11 (Docker Compose)

## Directories

```text
backend/   Spring Boot API
frontend/  React web app
infra/     local infra (docker compose)
docs/      product and backlog docs
```

## Prerequisites

- Docker Desktop
- JDK 17+
- Node.js 20+

## Environment

```bash
cp .env.example .env
```

## Run MariaDB

```bash
docker compose up -d
docker compose ps
```

## Run Backend

```bash
cd backend
./mvnw spring-boot:run
```

Windows PowerShell:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Health check:

- `GET http://localhost:8080/api/health`
- `GET http://localhost:8080/actuator/health`

## Run Frontend

```bash
cd frontend
npm install
npm run dev
```

Windows PowerShell (execution policy fallback):

```powershell
cd frontend
npm.cmd install
npm.cmd run dev
```

URL: `http://localhost:5173`

## Auth Bootstrap (B01)

Implemented endpoints:

- `POST /api/auth/login`
- `GET /api/auth/me`
- `POST /api/auth/logout`

Default seeded admin account:

- `loginId`: `admin`
- `password`: `password`

## Common Code / Organization Bootstrap (B02)

Implemented APIs:

- `GET/POST/PUT/DELETE /api/organizations`
- `GET/POST/PUT/DELETE /api/common-codes`
- `GET /api/common-codes/groups/{groupCode}`

Frontend:

- login 이후 조직/공통코드 관리 화면 제공 (검색/등록/수정/비활성 처리)

## Build and Test

Backend:

```bash
cd backend
./mvnw clean test
./mvnw clean package
```

Frontend:

```bash
cd frontend
npm run lint
npm run build
```

PowerShell:

```powershell
cd frontend
npm.cmd run lint
npm.cmd run build
```

## Finance Period / Account Bootstrap (F01)

Implemented APIs:

- `GET/POST/PUT/DELETE /api/finance/periods`
- `GET/POST/PUT/DELETE /api/finance/accounts`

Frontend:

- finance period management tab (search/register/update/delete)
- finance account management tab (search/register/update/delete)
