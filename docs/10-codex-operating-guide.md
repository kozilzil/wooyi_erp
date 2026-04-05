# 10-codex-operating-guide

## 1. Codex에 작업을 줄 때 좋은 형식

아래 템플릿을 사용한다.

```text
작업 카드: docs/07-backlog/F02-single-entry-voucher.md

해야 할 일:
- 백엔드 엔터티/리포지토리/서비스/API 구현
- 프런트 등록 화면과 목록 화면 구현
- 테스트 추가
- 관련 문서 업데이트

완료 조건:
- backlog 카드의 acceptance criteria 충족
- backend test 통과
- frontend lint/build 통과
- 변경 내용 요약 작성
```

## 2. 피해야 할 지시

- "ERP 전체를 다 만들어"
- "알아서 좋은 구조로 바꿔"
- "필요한 건 전부 추가해"

## 3. 좋은 단위
- 1개 화면
- 1개 API 묶음
- 1개 도메인 규칙
- 1개 승인/마감 흐름

## 4. 권장 실행 순서
1. 인증/권한
2. 공통코드/조직
3. 회원 기본 CRUD
4. 출석
5. 회계기수/계정과목
6. 단식부기 전표
7. 복식부기 전표
8. 승인/취소/마감
9. 비품 CRUD
10. QR 내보내기

## 5. 문서 갱신 규칙
- 엔터티 추가 -> ERD 갱신
- API 추가 -> API 계약 갱신
- 권한 변경 -> 권한표 갱신
- 회계 규칙 변경 -> finance-rules 갱신
