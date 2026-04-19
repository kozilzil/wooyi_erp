# M01-member-core-crud

## 1. Card Info
- Card ID: M01
- Title: Member core CRUD
- Priority: P0
- Module: Member
- Prerequisites: B01, B02

## 2. Goal
Provide create/read/update/delete (soft delete) for member core profile data.

## 3. Scope
### In
- Member list
- Member create
- Member detail
- Member update
- Member soft delete
- Basic search/filter

### Out
- Family relation editor
- Photo upload
- Education/visitation features

## 4. Implementation Requirements
### Backend
- Implement `members` entity and CRUD APIs
- Search filters: name, phone, status, department
- Use soft delete (`deleted_at`), no hard delete

### Frontend
- Member list screen
- Member create/update screen
- Member detail screen

### Docs
- Update API contracts
- Update ERD
- Update member feature spec

## 5. Business Rules
1. Name is required.
2. Registration date defaults to current date.
3. Delete must be soft delete via `deleted_at`.

## 6. Exception Rules
1. Missing required fields returns validation error.
2. Not found member returns 404.

## 7. Acceptance Criteria
- [ ] Member create works.
- [ ] Member list search/filter works.
- [ ] Member detail/update works.
- [ ] Member delete is soft delete.
- [ ] Created/updated metadata is persisted.

## 8. Tests
- [ ] Create test
- [ ] Search test
- [ ] Update test
- [ ] Soft delete test
