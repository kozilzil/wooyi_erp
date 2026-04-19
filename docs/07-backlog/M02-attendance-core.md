# M02-attendance-core

## 1. Card Info
- Card ID: M02
- Title: Attendance core management
- Priority: P1
- Module: Member
- Prerequisites: M01, B02

## 2. Goal
Allow recording and querying attendance by worship type and date.

## 3. Scope
### In
- Attendance create
- Attendance list query
- Member attendance query
- Worship code based query

### Out
- Mobile check-in optimization
- QR attendance

## 4. Implementation Requirements
### Backend
- Implement `attendance_records` entity and APIs
- Block duplicate records for same member/worship/date

### Frontend
- Attendance entry screen
- Query screen by date/worship/member

### Docs
- Update API contracts
- Update ERD

## 5. Business Rules
1. No duplicates for same member + worship + date.
2. Attendance status uses common code: `PRESENT`, `ABSENT`, `LATE`.

## 6. Acceptance Criteria
- [ ] Attendance create works.
- [ ] Query by date/worship works.
- [ ] Query by member works.
- [ ] Duplicate input is blocked.

## 7. Tests
- [ ] Create test
- [ ] Duplicate-block test
- [ ] Query test
