# PICK & GO (Spring Boot 버전)

> 1학기 HTML/CSS 프로젝트 → 기말 React 프로젝트 → **Spring Boot로 재구현**

가구를 "정리하기(폐기)", "맡겨두기(보관)", "판매하기(중고거래)" 세 가지 방식으로
처리할 수 있는 통합 플랫폼입니다.

## 기술 스택
- Java 17
- Spring Boot 3.2.5
- Spring MVC (Controller-Service-Repository 3계층 구조)
- Spring Data JPA + H2 Database (파일 기반)
- Thymeleaf (서버사이드 렌더링)
- HTML/CSS (Vanilla JS - 드래그앤드롭 업로드)

## 실행 방법 (IntelliJ)
1. IntelliJ에서 `File > Open`으로 이 폴더(pickandgo)를 연다.
2. Maven 프로젝트로 자동 인식되면 의존성을 다운로드할 때까지 기다린다.
3. `PickAndGoApplication.java`를 우클릭 → Run
4. 브라우저에서 http://localhost:8080 접속

또는 터미널에서:
```bash
./mvnw spring-boot:run
```
(mvnw 래퍼가 없다면 `mvn spring-boot:run`)

## 페이지 구성

| 경로 | 설명 | React 원본 대응 |
|---|---|---|
| `/` | 랜딩페이지 (서비스 소개 + 최근 등록 상품) | 1학기 HTML 랜딩페이지 |
| `/products` | 중고판매 - 지역/카테고리/가격 필터 + 검색 + 페이지네이션 | SellPage.jsx |
| `/products/{id}` | 상품 상세 | ProductDetailModal |
| `/organize` | 정리하기 - 이미지 드래그앤드롭 업로드 + 상품 등록 | OrganizePage.jsx |
| `/storage` | 맡겨두기 - 보관 서비스 신청 폼 | StorePage.jsx |
| `/register`, `/login`, `/mypage` | 회원가입 / 로그인 / 마이페이지 | 회원가입 폼 |
| `/cart` | 장바구니 (신규 추가) | - |
| `/contact` | 문의하기 (신규 추가) | - |

## React → Spring Boot 변환 포인트

| React (useState/useMemo) | Spring Boot |
|---|---|
| `useState`로 상품 목록 관리 | `Product` Entity + H2 DB |
| `useMemo`로 필터링 결과 캐싱 | `ProductRepository`의 JPQL `@Query` (DB가 직접 조건 연산) |
| 드래그앤드롭 `onDrop` 핸들러 | 동일한 JS 이벤트 + `multipart/form-data`로 서버 전송 |
| 모달(Modal) 컴포넌트 | 별도 상세 페이지(Controller + Thymeleaf) |
| `useState`로 폼 데이터 관리 | `@RequestParam`으로 폼 바인딩 → Service → Entity 저장 |
| 컴포넌트 재사용 (Star, X 아이콘) | Thymeleaf fragment (`header.html`, `footer.html`) |

## 디렉토리 구조
```
src/main/java/com/pickandgo/
 ├─ PickAndGoApplication.java
 ├─ controller/   # 화면/요청 처리
 ├─ domain/       # JPA Entity
 ├─ repository/   # Spring Data JPA
 ├─ service/      # 비즈니스 로직
 └─ config/       # 초기 데이터, 예외 처리
src/main/resources/
 ├─ templates/    # Thymeleaf 화면
 ├─ static/css/   # 스타일시트
 └─ application.properties
```
