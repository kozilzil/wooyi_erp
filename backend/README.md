# Backend

Spring Boot API for Church ERP.

## Main endpoints

- `GET /api/health`
- `POST /api/auth/login`
- `GET /api/auth/me`
- `POST /api/auth/logout`
- `GET/POST/PUT/DELETE /api/organizations`
- `GET/POST/PUT/DELETE /api/common-codes`
- `GET /api/common-codes/groups/{groupCode}`

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

## Finance endpoints (F01)

- `GET/POST/PUT/DELETE /api/finance/periods`
- `GET/POST/PUT/DELETE /api/finance/accounts`

- `GET/POST/PUT/DELETE /api/finance/vouchers`
- `POST /api/finance/vouchers/{id}/request-approval`
