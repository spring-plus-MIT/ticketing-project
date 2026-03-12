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

## ⚡ 성능 최적화 (Database Indexing)

> 대용량 데이터 환경에서의 검색 효율성을 극대화하기 위해 실행 계획(EXPLAIN) 분석 및 복합 인덱스(Composite Index)를 설계했습니다.

### 1️⃣ 문제 상황 및 최적화 대상
- **데이터 규모**: 약 14만 건의 기본 데이터 적재 후, 대량의 더미 데이터 적재를 통해 총 **3,800만 건** 규모의 극한 환경 조성
- **대상 쿼리**: `work_id`, `start_date`, `venue_id` 조건을 포함한 공연 검색 쿼리
- **현상**: 인덱스 부재 시 `work_id + 0` 등의 연산으로 인해 인덱스가 무력화되며 **전수 조사(Full Table Scan)** 발생

### 2️⃣ 해결 전략: 복합 인덱스 설계
- **Composite Index 생성**: `INDEX idx_perf_optimized (work_id, start_date, venue_id)`
- **선정 이유**: 조회 빈도가 높고 결합도가 높은 컬럼들을 묶어 **카디널리티(Cardinality)**를 높이고 탐색 범위를 최소화하여 서버 부하 경감
- **카디널리티란 ? : **"데이터의 중복도가 낮고, 종류가 얼마나 다양한가"**를 나타내는 지표입니다.

### 3️⃣ 실제 개선 결과 (EXPLAIN 데이터 기반)

| 지표 | 인덱스 미적용 (Before) | 복합 인덱스 적용 (After) | 개선 성과 |
|:---:|:---:|:---:|:---:|
| **스캔 범위 (Rows)** | **38,285,970 건** | **231,772 건** | **약 165배 탐색 범위 압축** |
| **실행 방식 (type)** | `ALL` (Full Scan) | **`ref` (Index Scan)** | **DB 연산 효율 최적화** |
| **응답 시간 (ms)** | 343 ms | 348 ~ 365 ms | 데이터 전송 부하로 인해 유사 |

### 4️⃣ 결과 분석 및 검증
- **탐색 범위 99.4% 감소**: 탐색해야 할 데이터 행을 3,800만 개에서 23만 개로 줄여 CPU 연산 비용을 획기적으로 절감했습니다.
- **응답 시간(ms)의 병목 지점 파악**: 스캔 범위의 차이에도 응답 시간이 유사한 원인은 `SELECT *`로 인한 **23만 건의 대량 데이터 전송(Network I/O)** 시간임을 확인했습니다.

---

# 동시성 시나리오 - 마지막 좌석 1개 동시 예매 100명

### 1. 문제 상황

**특정 좌석에 대해 동시에 여러 사용자가 예약을 시도하는 상황**

예시)

남은 좌석 : 1석 (A1)

동시에 100명의 사용자가 해당 좌석을 예매 요청

동시성 제어가 없는 경우 다음과 같은 문제가 발생

```
User1 → 좌석 조회 → AVAILABLE
User2 → 좌석 조회 → AVAILABLE
User3 → 좌석 조회 → AVAILABLE
...
User100 → 좌석 조회 → AVAILABLE
```

이후 모든 요청이 예약을 생성하게 되면

A1 좌석에 대해 100개의 예약이 생성되는 Double Booking 문제가 발생


### 2. 목표

**동시 요청 발생 시, 단 하나의 요청건만 성공, 나머지 요청은 모두 실패 처리**

예시)

```
User1 → 예약 성공
User2 ~ User100 → 예약 실패
```


### 3. 해결 전략

**Redis 기반 분산 락(Distributed Lock) 을 이용하여 동시성을 제어**

- **Distributed Lock**을 선택한 이유

1. **비관적 락 (Pessimistic Lock)**
    - 특징 : 데이터를 읽을 때 바로 락을 걸어 다른 트랜잭션 차단
    - 장점 : 충돌을 완전 차단 → 무결성 보장
    - 단점 : 동시성 낮음, 데드락 가능성
    - 사용 예시 : 은행 계좌 이체, 주문 재고 차감, 결제 로직 등
    - 충돌 비용이 큰 경우
    - 단일 DB 환경 + 충돌 비용이 큰 경우

2. **낙관적 락 (Optimistic Lock)**
    - 특징 : 데이터를 읽을 때 락을 걸지 않고, 수정 시점에 버전 필드 등을 확인 해 충돌 검증
    - 장점 : 동시성 높음, 성능 우수
    - 단점 : 충돌 발생 시 재시도 필요 → 사용자 경험 저하 가능
    - 사용 예시 : SNS 좋아요, 조회수 증가, 로그 기록 등
    - 충돌 비용이 작은 경우
    - 단일 DB 환경 + 충돌 비용이 작은 경우

3. **분산 락 (Distributed Lock)**
    - 특징 : Redis, Zookeeper 등을 이용해 여러 서버/프로세스가 공유 자원에 접근할 때 락을 관리
    - 장점 : 멀티 서버 환경에서도 일관성 보장, 비즈니스 로직 전체 보호 가능
    - 단점 : TTL, UUID, Lua Script 등 안전장치 필요, 네트워크 장애 고려
    - 사용 예시 : 콘서트 티켓 예매, 공용 회의실 예약 등, 마이크로서비스 환경에서 주문 처리, 외부 API 호출, 파일 처리 등 DB 밖의 로직까지 보호해야 하는 경우
    - 멀티 서버/분산 환경

4. **기존 DB Lock 방식의 한계**
    - DB Pessimistic Lock - DB 트랜잭션 범위만 보호  
    - Optimistic Lock - 충돌 발생 시 재시도 필요

5. **티켓팅 로직 흐름**
    - 좌석 조회 -> 예약 가능 여부 검증 -> 예약 생성 -> 좌석 상태 변경
    - 이 전체 비즈니스 로직을 보호하려면 DB 트랜잭션 범위를 넘어서는 락이 필요
    - **따라서 Redis 분산 락을 사용**


### 4. Redis Lock 구현 방식

**Redisson을 사용하지 않고 Lettuce 기반으로 직접 Redis Lock을 구현**

구조

```
LockRedisRepository → LockService → ReservationService
```

비즈니스 로직에서는 Redis에 직접 접근하지 않도록 설계

```
ReservationService → LockService → LockRedisRepository → Redis
```

비즈니스 로직과 락 구현을 분리


### 5. Redis Lock Key 설계

**좌석 단위로 동시성을 제어하기 위한 Key 구조 사용**

```
lock:seat:{seatId}
```

현재 프로젝트는 좌석 단위로만 동시성 충돌이 발생

```
Seat A1 → 하나의 Lock
Seat A2 → 별도의 Lock
```


### 6. Lock 획득 방식

**Redis의 SETNX (Set If Not Exists)를 이용해 락 획득**

```
SET lock:seat:101 UUID NX PX 3000
```

- NX : key가 없을 때만 설정
- PX : TTL 설정


### 7. TTL 설정

**락을 생성할 때 TTL(Time to Live)을 함께 설정**

TTL = 3초

서버 장애 또는 사용자 이탈 시 락이 영구적으로 남는 문제 방지

예시)

```
Lock 획득 → 서버 장애 발생 → Lock 해제 코드 실행 안됨
```

TTL이 없으면 좌석이 영구적으로 잠김


### 8. Lock 해제 방식

**본인이 획득한 락만 해제하도록 UUID를 사용**

현재 value == UUID 일 때만 삭제를 위한 Lua Script를 사용해 원자적으로 삭제

예시)

```lua
if redis.call("get", KEYS[1]) == ARGV[1] then
    return redis.call("del", KEYS[1])
else
    return 0
end
```

다른 요청이 획득한 락을 실수로 삭제하는 문제를 방지


### 9. Lock 실패 시 처리

**락 획득에 실패했을 때 대표적 처리 방법**

- Fail Fast - 즉시 실패
- Retry - 일정 시간 후 재시도
- Blocking - 락이 해제될 때까지 대기

현재 프로젝트는 **Fail Fast 전략을 사용**

- 티켓팅 시스템에서는 빠른 실패 응답이 사용자에게 더 좋음?
- 락 획득 실패 → 즉시 예약 실패 응답


### 10. 동시 예매 처리 흐름

```
사용자 요청 → Redis Lock 획득 시도 → Lock 성공
→ 좌석 상태 검증 → 예약 생성 → 좌석 상태 변경(RESERVED) → Lock 해제
→ 결제 완료 → 좌석 상태 변경(SOLD)
```

```
사용자 요청 → Redis Lock 획득 시도 → Lock 실패 → 즉시 예약 실패
```


### 11. 기대 결과

**문제 상황에 대한 Double Booking 문제를 방지**

```
1명 → 예약 성공
99명 → 예약 실패
```


### 12. 추가 고려 사항

**동시성 제어 시스템에 중요한 요소**

- 락 TTL 설정
- 락 해제 안정성
- 락 충돌 시 처리 전략
- Deadlock 방지

현재 프로젝트에서 구조

- SETNX + TTL
- UUID 기반 락 식별
- Lua Script 기반 원자적 해제
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
