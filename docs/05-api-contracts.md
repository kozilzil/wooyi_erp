# 05-api-contracts

??ë¬¸ì„œ??1ì°?API ê³„ì•½ ì´ˆì•ˆ?´ë‹¤.
?¤ì œ êµ¬í˜„ ??OpenAPIë¡??•ì¥?˜ëŠ” ê²ƒì„ ê¶Œì¥?œë‹¤.

## 1. ê³µí†µ ê·œì¹™

### 1.1 ?‘ë‹µ ?•ì‹
```json
{
  "success": true,
  "data": {},
  "message": "OK",
  "errorCode": null
}
```

### 1.2 ëª©ë¡ ì¡°íšŒ ?•ì‹
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

### 1.3 ?ëŸ¬ ?•ì‹
```json
{
  "success": false,
  "data": null,
  "message": "Validation failed",
  "errorCode": "VALIDATION_ERROR"
}
```

---

## 2. ?¸ì¦

### POST /api/auth/login
?…ë ¥:
```json
{
  "loginId": "admin",
  "password": "******"
}
```

ì¶œë ¥:
```json
{
  "success": true,
  "data": {
    "accessToken": "token",
    "user": {
      "id": 1,
      "name": "ê´€ë¦¬ì"
    }
  },
  "message": "OK",
  "errorCode": null
}
```

### POST /api/auth/logout
- ?„ì¬ ?¸ì…˜ ?ëŠ” ? í° ë¬´íš¨??

### GET /api/auth/me
- ?„ì¬ ë¡œê·¸???¬ìš©???•ë³´
- ë©”ë‰´ ê¶Œí•œ ?•ë³´ ?¬í•¨ ê°€??

---

## 3. ?Œì›

### GET /api/members
ì¿¼ë¦¬:
- keyword
- departmentId
- districtId
- status
- page
- size

### POST /api/members
?…ë ¥:
```json
{
  "name": "?ê¸¸??,
  "gender": "M",
  "birthDate": "1990-01-01",
  "phone": "010-0000-0000",
  "address": "?œìš¸??...",
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

## 4. ì¶œì„

### GET /api/attendance
### POST /api/attendance
?…ë ¥:
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

## 5. ?¬ì •

### GET /api/finance/periods
### POST /api/finance/periods

### GET /api/finance/accounts
### POST /api/finance/accounts

### GET /api/finance/vouchers
ì¿¼ë¦¬:
- periodId
- voucherType
- bookkeepingMode
- status
- fromDate
- toDate

### POST /api/finance/vouchers
?…ë ¥:
```json
{
  "voucherType": "GENERAL",
  "bookkeepingMode": "DOUBLE",
  "periodId": 1,
  "voucherDate": "2026-04-05",
  "departmentId": 3,
  "description": "?¬ë¬´?©í’ˆ êµ¬ì…",
  "lines": [
    {
      "lineNo": 1,
      "dcType": "DEBIT",
      "accountId": 101,
      "amount": 100000,
      "description": "?¬ë¬´?©í’ˆë¹?
    },
    {
      "lineNo": 2,
      "dcType": "CREDIT",
      "accountId": 201,
      "amount": 100000,
      "description": "ë³´í†µ?ˆê¸ˆ"
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

## 6. ë¹„í’ˆ

### GET /api/assets
ì¿¼ë¦¬:
- keyword
- categoryId
- departmentId
- locationId
- status

### POST /api/assets
?…ë ¥:
```json
{
  "assetNo": "A-2026-0001",
  "categoryId": 1,
  "name": "?¸íŠ¸ë¶?,
  "purchaseDate": "2026-04-05",
  "purchaseAmount": 1500000,
  "departmentId": 2,
  "locationId": 5,
  "status": "NORMAL",
  "vendorName": "ABC?ì‚¬"
}
```

### GET /api/assets/{id}
### PUT /api/assets/{id}
### POST /api/assets/{id}/repairs
### GET /api/assets/{id}/repairs

### POST /api/assets/{id}/qr-export
ì¶œë ¥:
- PNG ?ëŠ” PDF ?ëŠ” CSV ?¤ìš´ë¡œë“œ ë§í¬

---

## 7. ?Œì¼

### POST /api/files/upload
### GET /api/files/{id}/download

---

## 8. ê°ì‚¬ë¡œê·¸

### GET /api/audit-logs
ì¿¼ë¦¬:
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
  "name": "±³À°ºÎ",
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
  "name": "ÆÀ",
  "sortOrder": 2,
  "active": true,
  "description": "Á¶Á÷ À¯Çü"
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
  "description": "ÁÖÀÏÇå±İ",
  "lines": [
    {
      "accountId": 1,
      "amount": 100000,
      "description": "Çå±İ ¼öÀÔ"
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
- closed period rejects create/update/request`r`n## 15. F04 API Additions (Voucher Approval/Cancel + Period Close/Reopen)

### Voucher Approval Actions
- POST /api/finance/vouchers/{id}/approve
- POST /api/finance/vouchers/{id}/reject
- POST /api/finance/vouchers/{id}/cancel

Request body (approve/reject/cancel):
{
  "comment": "optional comment or cancel reason"
}

Rules:
- only REQUESTED can be approved/rejected
- only APPROVED can be canceled
- cancel requires non-empty reason
- approve creates ledger_entries
- reject does not create ledger_entries

### Period Close/Reopen
- POST /api/finance/periods/{id}/close
- POST /api/finance/periods/{id}/reopen
- required header for reopen: X-User-Role: ADMIN

Rules:
- period close blocked when period has DRAFT or REQUESTED vouchers
- closed period blocks voucher update/approval/cancel`r`n
