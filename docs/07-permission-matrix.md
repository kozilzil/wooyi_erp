# 07-permission-matrix

## 1. 역할 정의
- SYS_ADMIN: 시스템 관리자
- MEMBER_ADMIN: 교적 관리자
- PASTOR: 목회자/부서 책임자
- FINANCE_ADMIN: 재정 관리자
- FINANCE_USER: 재정 실무자
- ASSET_ADMIN: 비품 관리자
- GENERAL_USER: 일반 사용자

## 2. 권한 매트릭스

| 기능 | SYS_ADMIN | MEMBER_ADMIN | PASTOR | FINANCE_ADMIN | FINANCE_USER | ASSET_ADMIN | GENERAL_USER |
|---|---|---|---|---|---|---|---|
| 사용자/권한 관리 | C/R/U/D | - | - | - | - | - | - |
| 공통코드 관리 | C/R/U/D | R | - | R | - | R | - |
| 회원 조회 | R | R | R(소속 범위) | R(기부자 참조) | R(기부자 참조) | - | R(본인 제한 가능) |
| 회원 등록/수정 | C/R/U/D | C/R/U/D | U(제한적) | - | - | - | - |
| 출석 등록 | C/R/U/D | C/R/U/D | C/R/U | - | - | - | - |
| 전표 조회 | R | - | R(부서 제한) | R | R | - | - |
| 전표 등록 | C/R/U/D | - | C(부서 신청 한정) | C/R/U/D | C/R/U | - | - |
| 전표 승인 | R | - | - | C/R/U | - | - | - |
| 기간 마감 | R | - | - | C/R/U | - | - | - |
| 비품 조회 | R | R | R(소속 범위) | R | R | R | R(제한 가능) |
| 비품 등록/수정 | C/R/U/D | - | U(소속 범위 제한 가능) | - | - | C/R/U/D | - |
| 비품 수리이력 관리 | C/R/U/D | - | C/R/U(소속 범위) | - | - | C/R/U/D | - |
| 감사로그 조회 | R | - | - | R(재정 범위) | - | R(비품 범위) | - |

## 3. 주의사항
- 메뉴 권한과 데이터 범위 권한을 분리해서 구현한다.
- 목회자 권한은 “전체 수정”이 아니라 “소속 범위 제한”을 기본으로 한다.
- 재정 승인 권한은 재정 등록 권한과 반드시 분리한다.

---

## 4. B02 Permissions Update

- `SYS_ADMIN`: full C/R/U/D on organizations and common codes
- `MEMBER_ADMIN`: read organizations/common codes
- `FINANCE_ADMIN`: read organizations/common codes
- `ASSET_ADMIN`: read organizations/common codes

## 5. F01 Permissions Update

- `SYS_ADMIN`: full C/R/U/D on finance periods and finance accounts
- `FINANCE_ADMIN`: full C/R/U/D on finance periods and finance accounts
- `FINANCE_USER`: read finance periods and finance accounts

## 6. F02 Permissions Update

- `SYS_ADMIN`: full C/R/U/D + request-approval on single-entry vouchers
- `FINANCE_ADMIN`: full C/R/U/D + request-approval on single-entry vouchers
- `FINANCE_USER`: C/R/U on draft voucher and request-approval (approval processing ����)
