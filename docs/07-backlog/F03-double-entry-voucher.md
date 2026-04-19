# F03-double-entry-voucher

## 1. Card Info
- Card ID: F03
- Title: Double-entry voucher create/query
- Priority: P0
- Module: Finance
- Prerequisites: F01

## 2. Goal
Allow creating and querying double-entry vouchers with debit/credit lines.

## 3. Scope
### In
- Double-entry voucher create
- List/detail query
- Update/delete (before approval only)
- Request approval

### Out
- Approval processing
- Ledger posting

## 4. Implementation Requirements
### Backend
- Implement `vouchers` + `voucher_lines`
- Support `bookkeeping_mode=DOUBLE`
- Validate debit total equals credit total

### Frontend
- Double-entry line input UI
- Debit/credit total summary UI

### Docs
- Update API contracts
- Update finance rules

## 5. Business Rules
1. Debit total must equal credit total.
2. At least 2 lines are required.
3. Closed periods cannot accept voucher changes.

## 6. Acceptance Criteria
- [ ] Double-entry voucher create works.
- [ ] Debit/credit mismatch fails validation.
- [ ] List/detail query works.
- [ ] Request approval works.

## 7. Tests
- [ ] Normal create test
- [ ] Total mismatch fail test
- [ ] Minimum line count test
- [ ] Closed period block test
