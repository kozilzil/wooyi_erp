# 05-api-contracts

이 문서는 1차 API 계약 초안이다.
실제 구현 시 OpenAPI로 확장하는 것을 권장한다.

## 1. 공통 규칙

### 1.1 응답 형식
```json
{
  "success": true,
  "data": {},
  "message": "OK",
  "errorCode": null
}
```

### 1.2 목록 조회 형식
```json
{
  "success": true,
  "data": {
    "items": [],
    "page": 0,
    "size": 20,
    "totalElements": 0,
    "totalPages": 0
  },
  "message": "OK",
  "errorCode": null
}
```

### 1.3 에러 형식
```json
{
  "success": false,
  "data": null,
  "message": "Validation failed",
  "errorCode": "VALIDATION_ERROR"
}
```

---

## 2. 인증

### POST /api/auth/login
입력:
```json
{
  "loginId": "admin",
  "password": "******"
}
```

출력:
```json
{
  "success": true,
  "data": {
    "accessToken": "token",
    "user": {
      "id": 1,
      "name": "관리자"
    }
  },
  "message": "OK",
  "errorCode": null
}
```

### POST /api/auth/logout
- 현재 세션 또는 토큰 무효화

### GET /api/auth/me
- 현재 로그인 사용자 정보
- 메뉴 권한 정보 포함 가능

---

## 3. 회원

### GET /api/members
쿼리:
- keyword
- departmentId
- districtId
- status
- page
- size

### POST /api/members
입력:
```json
{
  "name": "홍길동",
  "gender": "M",
  "birthDate": "1990-01-01",
  "phone": "010-0000-0000",
  "address": "서울시 ...",
  "registerDate": "2026-04-05",
  "departmentId": 10,
  "districtId": 100,
  "positionCode": "DEACON"
}
```

### GET /api/members/{id}
### PUT /api/members/{id}
### DELETE /api/members/{id}

---

## 4. 출석

### GET /api/attendance
### POST /api/attendance
입력:
```json
{
  "memberId": 1,
  "worshipCode": "SUNDAY_MAIN",
  "attendanceDate": "2026-04-05",
  "attendanceStatus": "PRESENT",
  "note": ""
}
```

---

## 5. 재정

### GET /api/finance/periods
### POST /api/finance/periods

### GET /api/finance/accounts
### POST /api/finance/accounts

### GET /api/finance/vouchers
쿼리:
- periodId
- voucherType
- bookkeepingMode
- status
- fromDate
- toDate

### POST /api/finance/vouchers
입력:
```json
{
  "voucherType": "GENERAL",
  "bookkeepingMode": "DOUBLE",
  "periodId": 1,
  "voucherDate": "2026-04-05",
  "departmentId": 3,
  "description": "사무용품 구입",
  "lines": [
    {
      "lineNo": 1,
      "dcType": "DEBIT",
      "accountId": 101,
      "amount": 100000,
      "description": "사무용품비"
    },
    {
      "lineNo": 2,
      "dcType": "CREDIT",
      "accountId": 201,
      "amount": 100000,
      "description": "보통예금"
    }
  ]
}
```

### GET /api/finance/vouchers/{id}
### PUT /api/finance/vouchers/{id}
### POST /api/finance/vouchers/{id}/request-approval
### POST /api/finance/vouchers/{id}/approve
### POST /api/finance/vouchers/{id}/reject
### POST /api/finance/vouchers/{id}/cancel

### POST /api/finance/periods/{id}/close
### POST /api/finance/periods/{id}/reopen

---

## 6. 비품

### GET /api/assets
쿼리:
- keyword
- categoryId
- departmentId
- locationId
- status

### POST /api/assets
입력:
```json
{
  "assetNo": "A-2026-0001",
  "categoryId": 1,
  "name": "노트북",
  "purchaseDate": "2026-04-05",
  "purchaseAmount": 1500000,
  "departmentId": 2,
  "locationId": 5,
  "status": "NORMAL",
  "vendorName": "ABC상사"
}
```

### GET /api/assets/{id}
### PUT /api/assets/{id}
### POST /api/assets/{id}/repairs
### GET /api/assets/{id}/repairs

### POST /api/assets/{id}/qr-export
출력:
- PNG 또는 PDF 또는 CSV 다운로드 링크

---

## 7. 파일

### POST /api/files/upload
### GET /api/files/{id}/download

---

## 8. 감사로그

### GET /api/audit-logs
쿼리:
- moduleName
- action
- actorId
- fromDate
- toDate

---

## 9. Bootstrap Health Check

### GET /api/health
- Purpose: backend process health check for local bootstrap

Response example:
```json
{
  "success": true,
  "data": {
    "status": "UP",
    "service": "church-erp-backend",
    "timestamp": "2026-04-05T12:00:00Z"
  },
  "message": "OK",
  "errorCode": null
}
```

---

## 10. Auth Bootstrap Notes (B01)

- Token type: `Bearer <accessToken>`
- Required header for `/api/auth/me` and `/api/auth/logout`:
  - `Authorization: Bearer {token}`
- Default bootstrap account:
  - `loginId`: `admin`
  - `password`: `password`
