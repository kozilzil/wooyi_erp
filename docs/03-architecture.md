# 03-architecture

## 1. 전체 아키텍처

```text
[Browser / Responsive React UI]
            |
            v
[Spring Boot API Server]
            |
            +--> [MariaDB]
            +--> [File Storage]
            +--> [PDF/Excel Export]
            +--> [QR Data Export]
```

## 2. 구성 요소

### 2.1 Frontend
- React
- React Router
- Form validation
- Responsive layout
- API client layer 분리

### 2.2 Backend
- Spring Boot
- Spring Web
- Spring Security
- Spring Data JPA
- Validation
- Global Exception Handler
- Audit Logging Module

### 2.3 Database
- MariaDB
- InnoDB
- 마이그레이션 도구는 Flyway 또는 Liquibase 중 택 1
- 운영 전 모든 스키마 변경은 마이그레이션 파일로 관리

## 3. 모듈 경계

### 3.1 공통
- auth
- user
- role
- organization
- common-code
- file
- audit
- notice

### 3.2 교적
- member
- household
- attendance
- visitation
- prayer-note
- education

### 3.3 재정
- finance-period
- finance-account
- budget
- voucher
- ledger
- approval
- report
- donation-receipt

### 3.4 비품
- asset
- asset-category
- asset-location
- asset-repair
- asset-audit
- qr-export

## 4. 권한 구조

권한은 2단계로 나눈다.

1. 메뉴 접근 권한
2. 데이터 처리 권한

예:
- 전표 메뉴 접근 가능
- 하지만 승인 권한은 별도

## 5. 주요 설계 원칙

- 재정 승인/취소/마감은 command 성격의 서비스로 구현
- 회원/비품 CRUD는 standard service 패턴 사용
- 보고서 조회는 조회 전용 query service로 분리 가능
- 논리 삭제 필드 사용
- 생성/수정 메타데이터 공통화
- 감사로그는 비동기 저장을 고려하되, 핵심 업무 실패를 숨기지 않음

## 6. 파일 저장 원칙

첨부파일은 DB BLOB 직저장보다 파일 스토리지 + 메타정보 DB 저장을 우선한다.

저장 정보:
- 원본 파일명
- 저장 파일명
- MIME type
- 업로드 사용자
- 업로드 일시
- 참조 모듈
- 참조 ID

## 7. QR 연동 원칙

시스템은 QR 코드 그 자체를 생성하거나, 라벨 인쇄용 데이터를 파일로 생성한다.
하지만 라벨 프린터 제어는 시스템 책임 범위가 아니다.

출력 포맷 후보:
- PNG
- PDF
- CSV

## 8. 배포 권장 구조

```text
[Internal User]
    |
[Nginx]
    |
[Spring Boot]
    |
[MariaDB]
[File Storage]
```

## 9. 개발 환경 권장 구조

- `backend/` : Spring Boot
- `frontend/` : React
- `infra/` : docker compose, reverse proxy, DB init
- `docs/` : Codex 작업 문서
