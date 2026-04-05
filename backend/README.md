# Backend

Spring Boot API for Church ERP.

## Main endpoints

- `GET /api/health`
- `POST /api/auth/login`
- `GET /api/auth/me`
- `POST /api/auth/logout`

## Default admin (seed)

- loginId: `admin`
- password: `password`

## Run

```powershell
.\mvnw.cmd spring-boot:run
```

## Test / Build

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd clean package
```
