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

---

## 11. B02 API Additions (Organization / Common Code)

### Organizations

- `GET /api/organizations?keyword=&active=&page=0&size=20`
- `POST /api/organizations`
- `PUT /api/organizations/{id}`
- `DELETE /api/organizations/{id}` (soft delete)

`POST /api/organizations` request example:
```json
{
  "code": "ORG001",
  "name": "������",
  "parentId": null,
  "type": "DEPARTMENT",
  "active": true
}
```

### Common Codes

- `GET /api/common-codes?groupCode=&keyword=&active=&page=0&size=20`
- `GET /api/common-codes/groups/{groupCode}?activeOnly=true`
- `POST /api/common-codes`
- `PUT /api/common-codes/{id}`
- `DELETE /api/common-codes/{id}` (soft delete)

`POST /api/common-codes` request example:
```json
{
  "groupCode": "ORG_TYPE",
  "code": "TEAM",
  "name": "��",
  "sortOrder": 2,
  "active": true,
  "description": "���� ����"
}
```

### Audit

- organization/common-code create/update/delete writes into `audit_logs`

## 12. F01 API Additions (Finance Period / Account)

### Finance Periods

- `GET /api/finance/periods?fiscalYear=&status=&active=&page=0&size=20`
- `POST /api/finance/periods`
- `PUT /api/finance/periods/{id}`
- `DELETE /api/finance/periods/{id}` (soft delete)

`POST /api/finance/periods` request example:
```json
{
  "fiscalYear": 2026,
  "periodNo": 1,
  "startDate": "2026-01-01",
  "endDate": "2026-12-31",
  "status": "OPEN",
  "active": true
}
```

### Finance Accounts

- `GET /api/finance/accounts?accountType=&keyword=&active=&page=0&size=20`
- `POST /api/finance/accounts`
- `PUT /api/finance/accounts/{id}`
- `DELETE /api/finance/accounts/{id}` (soft delete)

`POST /api/finance/accounts` request example:
```json
{
  "accountCode": "1100",
  "accountName": "Cash",
  "accountType": "ASSET",
  "parentId": null,
  "active": true
}
```

### Audit

- finance period/account create/update/delete writes into `audit_logs`

## 13. F02 API Additions (Single Entry Voucher)

### Vouchers

- `GET /api/finance/vouchers?periodId=&voucherType=&status=&fromDate=&toDate=&page=0&size=20`
- `GET /api/finance/vouchers/{id}`
- `POST /api/finance/vouchers`
- `PUT /api/finance/vouchers/{id}`
- `DELETE /api/finance/vouchers/{id}` (soft delete)
- `POST /api/finance/vouchers/{id}/request-approval`

`POST /api/finance/vouchers` request example:
```json
{
  "voucherType": "INCOME",
  "periodId": 1,
  "voucherDate": "2026-04-19",
  "description": "�������",
  "lines": [
    {
      "accountId": 1,
      "amount": 100000,
      "description": "��� ����"
    }
  ]
}
```

Rules:

- bookkeepingMode fixed: `SINGLE`
- status flow: `DRAFT -> REQUESTED`
- only `DRAFT` can update/delete/request
- closed period rejects create/update/request

## 14. F03 API Additions (Double Entry Voucher)

### Vouchers (DOUBLE)

- `POST /api/finance/vouchers` with `bookkeepingMode=DOUBLE`
- `GET /api/finance/vouchers` (includes DOUBLE vouchers)
- `GET /api/finance/vouchers/{id}`
- `PUT /api/finance/vouchers/{id}` (DRAFT only)
- `DELETE /api/finance/vouchers/{id}` (DRAFT only, soft delete)
- `POST /api/finance/vouchers/{id}/request-approval` (DRAFT only)

`POST /api/finance/vouchers` DOUBLE request example:
```json
{
  "bookkeepingMode": "DOUBLE",
  "voucherType": "GENERAL",
  "periodId": 1,
  "voucherDate": "2026-05-03",
  "description": "office supplies",
  "lines": [
    { "dcType": "DEBIT", "accountId": 101, "amount": 100000, "description": "expense" },
    { "dcType": "CREDIT", "accountId": 201, "amount": 100000, "description": "cash" }
  ]
}
```

Rules:

- `bookkeepingMode=DOUBLE` requires at least 2 lines
- each line requires `dcType` (`DEBIT` or `CREDIT`)
- DEBIT total must equal CREDIT total
- closed period rejects create/update/request
