# 🎭 공연 예매 서비스 (Performance Booking Service)

> 문화예술 공연을 검색하고 예매할 수 있는 결제 통합 서비스입니다.

---

## 📌 프로젝트 개요

| 항목 | 내용                     |
|------|------------------------|
| 프로젝트명 | Ticketing Project      |
| 개발 기간 | 2026.03.05 ~ 2025.03.25 |
| 개발 인원 | 5명                     |
| 데이터베이스 | MySQL 8.4              |
| 아키텍처 | Monolithic REST API    |

---

## 👥 팀원 소개

| 이름  | 역할 | 담당 기능        |
|-----|------|--------------|
| 김세현 | 팀장 | 배포 + CI/CD   |
| 윤민기 | 팀원 | 실시간 채팅 + 인프라 |
| 김규범 | 팀원 | 인덱스          |
| 이준연 | 팀원 | 캐시           |
| 배주원 | 팀원 | 동시성 제어       |

---

## 🛠 기술 스택

| 분류 | 기술                   |
|------|----------------------|
| Language | Java 17              |
| Framework | Spring Boot 3.3.5    |
| ORM | Spring Data JPA      |
| Database | MySQL 8.4            |
| Security | Spring Security, JWT |
| Build Tool | Gradle               |
| API Docs | RestDocs             |

---

## 📁 프로젝트 구조

```
src
└── main
    └── java
        └── example
            └── ticketingproject
                ├── auth
                │   ├── controller
                │   ├── dto
                │   ├── exception
                │   └── service
                ├── common
                │   ├── config
                │   ├── dto
                │   ├── entity
                │   ├── enums
                │   └── exception
                ├── domain
                │   ├── cashcharge          # 캐시 충전
                │   ├── castmember          # 캐스팅 멤버
                │   ├── like                # 찜
                │   ├── payment             # 결제
                │   ├── performance         # 공연
                │   ├── performancesession  # 공연 회차
                │   ├── reservation         # 예약
                │   ├── review              # 리뷰
                │   ├── seat                # 좌석
                │   ├── seatgrade           # 좌석 등급
                │   ├── user                # 유저
                │   ├── venue               # 장소
                │   └── work                # 작품
                └── security
                    ├── exception
                    └── jwt
```

---

## ⚙️ ERD

![MIT-ERD](MIT-ERD.png)

---

## 🔐 권한 체계

| 역할 | 설명 |
|------|------|
| `SUPER_ADMIN` | 슈퍼 관리자. 다른 관리자 승인/삭제 가능 |
| `ADMIN` | 일반 관리자. 공연 등록, 캐시 충전 등 가능 |
| `USER` | 일반 고객. 예매, 리뷰, 찜 가능 |

### 관리자 상태 전이
```
PENDING → ACTIVE → DELETED
(가입)   (슈퍼관리자 승인)  (슈퍼관리자 삭제)
```

---

## 📡 API 명세

### 🔑 인증 `/auth`

| Method | URI | 설명 | 권한 |
|--------|-----|------|------|
| POST | `/auth/signup` | 회원가입 | - |
| POST | `/auth/login` | 로그인 | - |
| POST | `/auth/logout` | 로그아웃 | USER |

### 👥 유저 `/users`

| Method | URI | 설명 | 권한 |
|--------|-----|------|------|
| GET | `/users` | 유저 목록 조회 | ADMIN |
| GET | `/users/{userId}` | 유저 단건 조회 | USER, ADMIN |
| PUT | `/users/{userId}` | 유저 정보 수정 | USER |
| PUT | `/users/{userId}` | (관리자) 유저 정보 수정 | ADMIN |
| DELETE | `/users/{userId}` | 유저 탈퇴 | USER |
| DELETE | `/users/{userId}` | (관리자) 유저 탈퇴 처리 | ADMIN |

### 🎭 작품 `/works`

| Method | URI | 설명 | 권한 |
|--------|-----|------|------|
| POST | `/works` | 작품 생성 | ADMIN |
| GET | `/works/{workId}` | 작품 조회 | - |
| PUT | `/works/{workId}` | 작품 수정 | ADMIN |

### 🎬 공연 `/performances`

| Method | URI | 설명 | 권한 |
|--------|-----|------|------|
| POST | `/performances` | 공연 생성 | ADMIN |
| GET | `/performances` | 공연 목록 조회 | - |
| GET | `/performances/{performanceId}` | 공연 단건 조회 | - |
| PUT | `/performances/{performanceId}` | 공연 정보 수정 | ADMIN |
| PATCH | `/performances/{performanceId}/status` | 공연 상태 수정 (STATUS) | ADMIN |

### 🗓 공연 회차 `/performances/{performanceId}/sessions`

| Method | URI | 설명 | 권한 |
|--------|-----|------|------|
| POST | `/performances/{performanceId}/sessions` | 공연 회차 생성 | ADMIN |
| GET | `/performances/{performanceId}/sessions` | 공연 회차 목록 조회 | - |
| GET | `/performances/{performanceId}/sessions/{sessionId}` | 공연 회차 단건 조회 | - |
| PUT | `/performances/{performanceId}/sessions/{sessionId}` | 공연 회차 정보 수정 | ADMIN |
| DELETE | `/performances/{performanceId}/sessions/{sessionId}` | 공연 회차 삭제 | ADMIN |

### 📍 장소 `/venues`

| Method | URI | 설명 | 권한 |
|--------|-----|------|------|
| POST | `/venues` | 장소 등록 | ADMIN |
| GET | `/venues` | 장소 목록 조회 | - |
| GET | `/venues/{venueId}` | 장소 상세 조회 | - |
| PUT | `/venues/{venueId}` | 장소 수정 | ADMIN |
| DELETE | `/venues/{venueId}` | 장소 삭제 | ADMIN |

### 🎤 멤버(캐스팅) `/cast-members`

| Method | URI | 설명 | 권한 |
|--------|-----|------|------|
| POST | `/cast-members` | 멤버 등록 | ADMIN |
| GET | `/cast-members` | 멤버 목록 조회 | - |
| GET | `/cast-members/{castMemberId}` | 멤버 상세 조회 | - |
| PUT | `/cast-members/{castMemberId}` | 멤버 수정 | ADMIN |
| DELETE | `/cast-members/{castMemberId}` | 멤버 삭제 | ADMIN |

### 💺 좌석 `/seats`

| Method | URI | 설명 | 권한 |
|--------|-----|------|------|
| POST | `/seats` | 좌석 생성 | ADMIN |
| GET | `/seats` | 좌석 목록 조회 | - |
| GET | `/seats/{seatId}` | 좌석 단건 조회 | - |

### 🏷 좌석 등급 `/seat-grades`

| Method | URI | 설명 | 권한 |
|--------|-----|------|------|
| POST | `/seat-grades` | 좌석 등급 생성 | ADMIN |
| GET | `/seat-grades` | 좌석 등급 목록 조회 | - |
| GET | `/seat-grades/{seatGradeId}` | 좌석 등급 단건 조회 | - |
| PUT | `/seat-grades/{seatGradeId}` | 좌석 등급 수정 | ADMIN |
| DELETE | `/seat-grades/{seatGradeId}` | 좌석 등급 삭제 | ADMIN |

### 🎟 예약 `/reservations`

| Method | URI | 설명 | 권한 |
|--------|-----|------|------|
| POST | `/reservations` | 예약 생성 | USER |
| GET | `/reservations` | (관리자) 전체 예약 목록 조회 | ADMIN |
| GET | `/reservations?userId={userId}` | (관리자) 유저별 예약 목록 조회 | ADMIN |
| GET | `/reservations/{reservationId}` | (관리자) 예약 단건 조회 | ADMIN |
| GET | `/reservations/{reservationId}` | (고객) 예약 단건 조회 | USER |
| PUT | `/reservations/{reservationId}` | 예약 정보 수정 | USER |
| PUT | `/reservations/{reservationId}` | (관리자) 예약 정보 수정 | ADMIN |
| PATCH | `/reservations/{reservationId}/cancel` | 예약 취소 | USER |
| PATCH | `/reservations/{reservationId}/cancel` | (관리자) 예약 취소 | ADMIN |

### 💳 결제 `/payments`

| Method | URI | 설명 | 권한 |
|--------|-----|------|------|
| POST | `/payments` | 결제 내역 생성 (잔액 차감) | USER |
| GET | `/payments/{paymentId}` | 결제 내역 단건 조회 | USER, ADMIN |
| GET | `/payments` | 결제 내역 목록 조회 | USER, ADMIN |

### 💰 캐시 충전 `/cash-charges`

| Method | URI | 설명 | 권한 |
|--------|-----|------|------|
| POST | `/cash-charges` | 캐시 충전 내역 생성 | ADMIN |
| GET | `/cash-charges/{chargeId}` | 캐시 충전 내역 단건 조회 | ADMIN |
| GET | `/cash-charges/me` | (고객) 나의 캐시 충전 내역 조회 | USER |

### ⭐ 리뷰 `/reviews`

| Method | URI | 설명 | 권한 |
|--------|-----|------|------|
| POST | `/reviews` | 리뷰 생성 | USER |
| GET | `/reviews/{reviewId}` | 리뷰 조회 | - |
| GET | `/reviews` | 리뷰 목록 조회 | - |
| PUT | `/reviews/{reviewId}` | 리뷰 수정 (본인 것만) | USER |
| DELETE | `/reviews/{reviewId}` | 내 리뷰 삭제 | USER |
| DELETE | `/reviews/{reviewId}` | (관리자) 리뷰 삭제 | ADMIN |

### 🩷 찜 `/wishlists`

| Method | URI | 설명 | 권한 |
|--------|-----|------|------|
| POST | `/wishlists` | 찜 생성 | USER |
| DELETE | `/wishlists/{wishlistId}` | 찜 삭제 | USER |

---

## 💡 핵심 기능 및 구현 전략

### 소프트 딜리트
- 고객/관리자 탈퇴 시 `deleted_at` 기록 및 `status = DELETED` 처리
- 고객 탈퇴 시 이메일·전화번호 마스킹 처리

### 예외 처리
| 예외 클래스 | 발생 조건            | HTTP 상태 |
|------------|------------------|-----------|
| `BaseExceptionHandler` | 런타임 에러           | ErrorStatus |

---

## 🚀 로컬 실행 방법

```bash
# 1. 저장소 클론
git clone https://github.com/spring-plus-MIT/ticketing-project.git

# 2. 환경변수 설정
cp .env.example .env
# .env 파일 내 DB 정보, JWT Secret 등 입력

# 3. 빌드 및 실행
./gradlew bootRun
```

### 환경변수 목록 (`.env`)
```
DB_USERNAME=root
DB_PASSWORD=secret
JWT_SECRET=your-jwt-secret
```

---

## 🔎 트러블슈팅

> 개발 중 마주친 문제와 해결 방법을 기록합니다.

| 문제 | 원인 | 해결 방법 |
|------|------|-----------|
| 동시 결제 시 잔액 불일치 | 동시성 이슈 | 비관적 락 적용 |
| (추가 예정) | | |

---

## 📎 참고 자료

- [Spring Boot 공식 문서](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Security JWT 인증](https://docs.spring.io/)
- [DBDiagram](https://dbdiagram.io/)