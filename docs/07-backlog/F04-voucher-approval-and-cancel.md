# F04-voucher-approval-and-cancel

## 1. Card Info
- Card ID: F04
- Title: Voucher approve/reject/cancel and ledger posting
- Priority: P0
- Module: Finance
- Prerequisites: F02, F03

## 2. Goal
Post to ledger only approved vouchers and enforce approval/cancel/period-close rules.

## 3. Scope
### In
- Approve
- Reject
- Cancel
- Approval history
- `ledger_entries` creation
- Period close/reopen basics

### Out
- Multi-step approval flow
- Advanced reporting

## 4. Implementation Requirements
### Backend
- Implement command services for approve/reject/cancel
- Implement `voucher_approval_histories` and `ledger_entries`
- Implement finance period close/reopen APIs

### Frontend
- Pending approval list
- Approve/reject/cancel action UI
- Period close/reopen UI

### Docs
- Update finance rules, API contracts, and ERD

## 5. Business Rules
1. Approval creates `ledger_entries`.
2. Rejected vouchers do not post ledger.
3. Cancel requires cancel reason.
4. Closed period restricts update/approval/cancel.
5. Reopen is admin-only.

## 6. Acceptance Criteria
- [x] Approve posts ledger entries.
- [x] Reject does not post ledger entries.
- [x] Cancel updates status/history correctly.
- [x] Closed period blocks restricted actions.
- [x] Approval history is persisted.

## 7. Tests
- [x] Approve test
- [x] Reject test
- [x] Cancel test
- [x] Period-close restriction test
- [x] Reopen permission test

## 8. Implementation Notes (2026-05-03)
- Added migration `V8__voucher_approval_history_and_ledger.sql`.
- Added voucher actions API:
  - `POST /api/finance/vouchers/{id}/approve`
  - `POST /api/finance/vouchers/{id}/reject`
  - `POST /api/finance/vouchers/{id}/cancel`
- Added finance period control API:
  - `POST /api/finance/periods/{id}/close`
  - `POST /api/finance/periods/{id}/reopen` (`X-User-Role: ADMIN` required)
- Added status flow:
  - `DRAFT -> REQUESTED -> APPROVED`
  - `REQUESTED -> REJECTED`
  - `APPROVED -> CANCELED`
