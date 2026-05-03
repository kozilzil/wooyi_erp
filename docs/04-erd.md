# 04-erd

이 문서는 1차 개발용 ERD 초안이다.
실제 구현 시 상세 컬럼명은 팀 규칙에 맞게 조정하되, 아래 관계는 유지한다.

## 1. 공통 엔터티

### users
- id
- login_id
- password_hash
- name
- email
- phone
- status
- organization_id
- created_at
- created_by
- updated_at
- updated_by
- deleted_at

### roles
- id
- code
- name
- description

### permissions
- id
- code
- name
- module
- action

### user_roles
- user_id
- role_id

### role_permissions
- role_id
- permission_id

### organizations
- id
- code
- name
- parent_id
- type

### common_codes
- id
- group_code
- code
- name
- sort_order
- active

### files
- id
- module_name
- reference_id
- original_name
- stored_name
- mime_type
- size
- uploaded_by
- uploaded_at

### audit_logs
- id
- module_name
- entity_name
- entity_id
- action
- actor_id
- payload_before
- payload_after
- created_at

---

## 2. 교적 엔터티

### members
- id
- member_no
- name
- gender
- birth_date
- phone
- email
- address
- register_date
- baptism_date
- status
- department_id
- district_id
- position_code
- household_id
- created_at
- created_by
- updated_at
- updated_by
- deleted_at

### households
- id
- household_no
- head_member_id
- address
- phone

### member_family_relations
- id
- household_id
- member_id
- relation_code

### attendance_records
- id
- member_id
- worship_code
- attendance_date
- attendance_status
- note

### visitations
- id
- member_id
- visit_date
- visitor_user_id
- summary
- detail
- visibility_scope

### prayer_notes
- id
- member_id
- writer_user_id
- content
- created_at
- visibility_scope

---

## 3. 재정 엔터티

### finance_periods
- id
- fiscal_year
- period_no
- start_date
- end_date
- status

### finance_accounts
- id
- account_code
- account_name
- account_type
- parent_id
- active

### budgets
- id
- period_id
- department_id
- account_id
- amount
- status

### vouchers
- id
- voucher_no
- voucher_type
- bookkeeping_mode
- period_id
- voucher_date
- department_id
- status
- description
- requester_id
- approver_id
- approved_at
- canceled_at
- cancel_reason
- source_member_id
- source_vendor_name
- total_amount

### voucher_lines
- id
- voucher_id
- line_no
- dc_type
- account_id
- amount
- description

### ledger_entries
- id
- period_id
- voucher_id
- voucher_line_id
- entry_date
- account_id
- dc_type
- amount

### voucher_approval_histories
- id
- voucher_id
- action
- actor_id
- comment
- created_at

### donation_receipts
- id
- member_id
- issue_year
- total_amount
- receipt_no
- issued_at
- issued_by

---

## 4. 비품 엔터티

### assets
- id
- asset_no
- category_id
- name
- specification
- purchase_date
- purchase_amount
- department_id
- location_id
- status
- vendor_name
- finance_voucher_id
- qr_token

### asset_categories
- id
- code
- name
- parent_id

### asset_locations
- id
- code
- name
- parent_id

### asset_repairs
- id
- asset_id
- request_date
- repair_date
- vendor_name
- cost
- detail
- status

### asset_audits
- id
- asset_id
- audit_round
- audit_date
- auditor_id
- result_status
- note

---

## 5. 핵심 관계

- users N:M roles
- roles N:M permissions
- organizations 1:N users
- households 1:N members
- members 1:N attendance_records
- members 1:N visitations
- members 1:N prayer_notes
- finance_periods 1:N budgets
- finance_periods 1:N vouchers
- vouchers 1:N voucher_lines
- vouchers 1:N voucher_approval_histories
- vouchers 1:N ledger_entries
- finance_accounts 1:N voucher_lines
- members 1:N donation_receipts
- assets 1:N asset_repairs
- assets 1:N asset_audits

## 6. 필수 인덱스 제안

- members(member_no)
- members(name, phone)
- vouchers(voucher_no)
- vouchers(period_id, voucher_date, status)
- voucher_lines(voucher_id, line_no)
- ledger_entries(period_id, account_id, entry_date)
- assets(asset_no)
- assets(department_id, location_id, status)

---

## 7. B02 Physical Schema Notes

Implemented tables:

### organizations
- id (PK)
- code (UNIQUE)
- name
- parent_id (self FK)
- type
- active
- created_at
- updated_at
- deleted_at

### common_codes
- id (PK)
- group_code
- code
- name
- sort_order
- active
- description
- created_at
- updated_at
- deleted_at
- UNIQUE(group_code, code)

### audit_logs
- id (PK)
- module_name
- entity_name
- entity_id
- action
- actor_id
- payload_before
- payload_after
- created_at

## 8. F01 Physical Schema Notes

Implemented tables:

### finance_periods
- id (PK)
- fiscal_year
- period_no
- start_date
- end_date
- status (OPEN, CLOSED)
- active
- created_at
- updated_at
- deleted_at
- UNIQUE(fiscal_year, period_no)

### finance_accounts
- id (PK)
- account_code (UNIQUE)
- account_name
- account_type
- parent_id (self FK)
- active
- created_at
- updated_at
- deleted_at

## 9. F02 Physical Schema Notes

Implemented tables:

### vouchers
- id (PK)
- voucher_no (UNIQUE)
- voucher_type (INCOME, EXPENSE)
- bookkeeping_mode (SINGLE)
- period_id (FK -> finance_periods)
- voucher_date
- status (DRAFT, REQUESTED)
- description
- total_amount
- created_at
- updated_at
- deleted_at

### voucher_lines
- id (PK)
- voucher_id (FK -> vouchers)
- line_no
- account_id (FK -> finance_accounts)
- amount (positive integer)
- description
- created_at

## 10. F03 Physical Schema Notes

Changes:

### voucher_lines
- added `dc_type` (`DEBIT` or `CREDIT`) for DOUBLE bookkeeping mode

### vouchers
- `bookkeeping_mode` now used for both `SINGLE` and `DOUBLE`
