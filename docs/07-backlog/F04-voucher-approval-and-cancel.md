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
- [ ] Approve posts ledger entries.
- [ ] Reject does not post ledger entries.
- [ ] Cancel updates status/history correctly.
- [ ] Closed period blocks restricted actions.
- [ ] Approval history is persisted.

## 7. Tests
- [ ] Approve test
- [ ] Reject test
- [ ] Cancel test
- [ ] Period-close restriction test
- [ ] Reopen permission test
