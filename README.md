# 🎭 공연 예매 서비스 (Performance Booking Service)

> 문화예술 공연을 검색하고 예매할 수 있는 결제 통합 서비스입니다.

---

## 📌 프로젝트 개요

| 항목 | 내용                      |
|------|-------------------------|
| 프로젝트명 | Ticketing Project       |
| 개발 기간 | 2026.03.05 ~ 2025.03.25 |
| 개발 인원 | 5명                      |
| 데이터베이스 | MySQL 8.0               |
| 아키텍처 | Monolithic REST API     |

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

# ⚡ Database Performance Optimization (Indexing)
## 🚀 대규모 공연 데이터셋 인덱스 최적화 및 확장성 검증 보고서

### 1. 프로젝트 개요 (Background)
본 작업은 대규모 공연 데이터 조회 성능 문제를 해결하기 위한 데이터베이스 인덱스 최적화 실험입니다. 초기 환경에서 발생하던 Full Table Scan 문제를 진단하고, 600만 건 이상의 데이터 환경에서도 안정적인 성능을 유지하는 구조를 설계/검증했습니다.

---

### 📊 2. 실험 환경
| 항목 | 내용 |
| :--- | :--- |
| **Database** | MySQL 8.x |
| **데이터 종류** | 공연 정보 데이터 |
| **초기 데이터** | 약 100만 건 |
| **확장 테스트** | +500만 건 (총 약 600만 건) |

---

### 📊 3. 성능 개선 결과 (Summary)
| 구분 | 최적화 전 (Before) | 인덱스 적용 후 (After) | 500만건 추가 후 |
| :--- | :--- | :--- | :--- |
| **실행 시간** | **396 ms** | **381 ms** | **379 ms** |
| **조회 방식** | Full Table Scan (ALL) | **Range Scan** | **Range Scan 유지** |
| **정렬 방식** | Using filesort | **인덱스 정렬 활용** | **인덱스 정렬 유지** |
| **인덱스 전략** | 없음 | **Covering Index** | **성능 방어 성공** |

> **핵심 결과:** 데이터 규모가 6배 증가했음에도 실행 시간을 380ms 수준으로 유지하며 강력한 **확장성(Scalability)**을 확보함.

---

### 🔍 4. 문제 분석 (Before Optimization)
인덱스가 없는 상태에서 `WHERE` 필터링과 `ORDER BY`를 동시에 수행할 경우, 전체 테이블을 스캔하고 메모리 내에서 강제 정렬이 발생함을 확인했습니다.

- **EXPLAIN 분석 결과:** `type: ALL`, `Using filesort` 발생
- **위험 요소:** 데이터 증가 시 성능이 선형적으로 저하될 가능성 높음

<img width="100%" alt="Before 실행 계획" src="https://github.com/user-attachments/assets/dacd843c-2884-4cc3-881e-8395aaf91388" />
*그림 1: 최적화 전 실행 계획 (Full Table Scan)*

---

### 🏗️ 5. 해결 방법 (Index Optimization)
조회 조건과 정렬 조건을 동시에 최적화하기 위해 다음과 같은 **복합 인덱스**를 설계했습니다.

**[설계된 인덱스]**
`idx_perf_main (venue_id, start_date, season)`

- **WHERE 필터링:** `venue_id`, `start_date`를 인덱스에서 즉시 필터링
- **ORDER BY 최적화:** 인덱스의 정렬 순서를 활용하여 `filesort` 제거
- **Covering Index:** `SELECT` 절의 컬럼을 인덱스에 포함시켜 테이블 접근 최소화(`Using index`)


---

### 📈 6. 인덱스 적용 결과 (After Optimization)
- **EXPLAIN 분석 결과:** `type: range`, `Using index` 확인
- **개선 효과:** Full Table Scan 제거, 디스크 I/O 감소, 조회 성능 안정화

<img width="100%" alt="After 실행 계획" src="https://github.com/user-attachments/assets/f976c011-b033-419c-b07b-2fa2bf1c9186" />
*그림 2: 인덱스 최적화 후 실행 계획 (Covering Index)*

---

### 📈 7. 대규모 데이터 확장 테스트
실제 서비스 운영 환경을 가정하여 **500만 건의 더미 데이터**를 추가 적재하고 재측정했습니다.

- **테스트 결과:** 데이터가 600만 건으로 증가했음에도 실행 계획과 속도(379ms)가 안정적으로 유지됨.

<img width="100%" alt="Post-Insert 검증" src="https://github.com/user-attachments/assets/f547ad11-e883-4b13-912c-5105d79ff36d" />
*그림 3: 500만 건 데이터 추가 후 확장성 검증 결과*

---

### 🎯 8. 최종 성과
1. **쿼리 성능 개선:** Full Scan 및 Filesort 제거를 통한 연산 효율화
2. **디스크 I/O 감소:** 커버링 인덱스 적용으로 테이블 접근 최소화
3. **확장성 확보:** 대규모 데이터 환경에서도 고정된 응답 속도 보장

---

### 🗂 9. 작업 진행 과정 (Work Log)
1. **분석:** 검색 쿼리 수집 및 EXPLAIN 기반 병목 분석
2. **설계:** 복합 인덱스 초안 설계 및 실행 계획 비교 시뮬레이션
3. **검증:** 인덱스 적용 전/후 성능 측정 및 5만 건 샘플 테스트
4. **확장:** 500만 건 데이터 적재를 통한 대규모 환경 최종 테스트
5. **문서화:** 성능 비교 보고서 작성 및 팀 기술 공유

---

### 📌 핵심 기술 키워드
`MySQL` `Indexing` `Covering Index` `Composite Index` `Query Optimization` `EXPLAIN Analysis` `Scalability`



---
# Concurrency Scenarios

1. 마지막 좌석 동시 예매 (100명)
2. 좌석 제한 초과 생성 방지 (200 요청)


## 1. 마지막 좌석 1개 동시 예매 100명

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

1. **기존 DB Lock 방식의 한계**
    - DB Pessimistic Lock - DB 트랜잭션 범위만 보호  
    - Optimistic Lock - 충돌 발생 시 재시도 필요

2. **티켓팅 로직 흐름**
    - 좌석 조회 -> 예약 가능 여부 검증 -> 예약 생성 -> 좌석 상태 변경
    - 이 전체 비즈니스 로직을 보호하려면 DB 트랜잭션 범위를 넘어서는 락이 필요
    - **따라서 Redis 분산 락을 사용**

| 구분 | 비관적 락 (Pessimistic) | 낙관적 락 (Optimistic) | 분산 락 (Distributed) |
|-----|-----|-----|-----|
| 특징 | 조회 시점에 즉시 락 획득 | 수정 시점에 버전으로 충돌 검증 | Redis 등 외부 시스템으로 락 관리 |
| 장점 | 충돌 완전 차단, 무결성 보장 | 높은 동시성, 성능 우수 | 멀티 서버 환경에서도 일관성 보장 |
| 단점 | 동시성 낮음, 데드락 가능 | 충돌 시 재시도 필요 | TTL, 네트워크 장애 등 고려 필요 |
| 사용 예시 | 계좌 이체, 재고 차감 | 좋아요, 조회수 | 티켓 예매, 예약 시스템 |
| 환경 | 단일 DB + 충돌 비용 큼 | 단일 DB + 충돌 비용 작음 | 멀티 서버 / 분산 환경 |



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

TTL = 10초

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

- Lock TTL 설정
- Lock 해제 안정성
- Lock 충돌 시 처리 전략
- Deadlock 방지

현재 프로젝트에서 구조

- SETNX + TTL
- UUID 기반 락 식별
- Lua Script 기반 원자적 해제
---

## 2. 좌석 제한 20개, 200개 생성 요청

### 1. 문제 상황

**좌석 생성 API가 동시에 여러 요청을 받을 때 상황**

예시)

공연장 좌석 수 제한 : 20석

관리자가 동시에 좌석 생성 요청을 보냄

요청 수 : 200개  
좌석 제한 : 20개

동시성 제어가 없는 경우 다음과 같은 상황 발생

```
Thread1 → 현재 좌석 수 조회 → 0
Thread2 → 현재 좌석 수 조회 → 0
Thread3 → 현재 좌석 수 조회 → 0
...
Thread200 → 현재 좌석 수 조회 → 0
```

이후 모든 요청이 좌석을 생성하면

```
좌석 제한 = 20
실제 생성 좌석 = 22
```

또는 환경에 따라 더 많은 좌석이 생성될 수 있음

Race Condition으로 인해 좌석 제한 검증 로직이 동시에 통과하면서 발생

---

### 2. 목표

**동시 요청이 발생해도 좌석 제한을 절대 초과하지 않도록 보장**

예시)

요청 수 = 200  
좌석 제한 = 20

결과

```
20개 생성 성공
180개 생성 실패
```

좌석 수는 항상 20개 이하로 유지

---

### 3. 해결 전략

**Redis 기반 분산 락(Distributed Lock)을 이용하여 동시성을 제어**

좌석 생성 로직은 다음과 같은 과정을 거친다.

```
현재 좌석 수 조회
→ 좌석 제한 검증
→ 좌석 생성
```

이 과정이 동시에 실행되면 Race Condition이 발생한다.

좌석 생성 전체 로직을 하나의 Lock으로 보호

좌석 생성 Lock

```
lock:venue:{venueId}:seat:create
```

동일 공연장에 대한 좌석 생성 요청은 한 번에 하나만 실행

---

### 4. Redis Lock 구현 방식

**Redisson을 사용하지 않고 Lettuce 기반으로 직접 Redis Lock을 구현**

구조

```
LockRedisRepository → LockService → AdminSeatService
```

비즈니스 로직에서 Redis에 직접 접근하지 않도록 설계

```
AdminSeatService → LockService → LockRedisRepository → Redis
```

비즈니스 로직과 락 구현을 분리

---

### 5. Redis Lock Key 설계

**공연장 단위로 좌석 제한이 존재, 공연장 기준으로 Lock Key 생성**

```
lock:venue:{venueId}:seat:create
```

예시)

```
lock:venue:1:seat:create
```

동일 공연장에 대한 좌석 생성 요청은 모두 동일 Lock을 사용

---

### 6. Lock 획득 방식

**Redis의 SETNX (Set If Not Exists)를 이용해 락 획득**

```
SET lock:seat:101 UUID NX PX 3000
```

- NX : key가 없을 때만 설정
- PX : TTL 설정

---

### 7. TTL 설정

**락을 생성할 때 TTL(Time to Live)을 함께 설정**

TTL = 10초

서버 장애 또는 사용자 이탈 시 락이 영구적으로 남는 문제 방지

예시)

```
Lock 획득 → 서버 장애 발생 → Lock 해제 코드 실행 안됨
```

TTL이 없으면 좌석 생성 기능이 영구적으로 막힐 수 있음

---

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

---

### 9. Lock 실패 시 처리

**좌석 생성은 Fail Fast 전략이 아닌 Retry 전략을 사용**

이유

좌석 생성은 관리자 기능이며

동시에 요청이 들어와도 최대한 좌석을 생성하는 것이 목표

Lock 획득 실패 → 일정 시간 후 재시도

Retry 전략

```
Retry 횟수 : 10회
Backoff : Exponential Backoff + Jitter
```

예시)

```
13ms → 28ms → 45ms → 82ms ...
```

락 경쟁을 완화하기 위한 전략

---

### 10. 동시 좌석 생성 처리 흐름

```
좌석 생성 요청 → Redis Lock 획득 시도 → Lock 성공 → 현재 좌석 수 조회
→ 좌석 제한 검증 → 좌석 생성 → Lock 해제
```

```
좌석 생성 요청 → Redis Lock 획득 실패 → Backoff 후 재시도 → Retry 초과 시 좌석 생성 실패
```

---

### 11. 기대 결과

**문제 상황에 대한 제한 초과 생성 문제를 방지**

```
좌석 제한 = 20
동시 요청 = 200

좌석 생성 성공 : 20
좌석 생성 실패 : 180
```

---

### 12. 추가 고려 사항

동시성 제어에서 중요한 요소

- Lock TTL 설정
- Lock 해제 안정성
- Retry 전략
- Backoff 전략
- Deadlock 방지

현재 프로젝트 적용 구조

- SET NX + TTL
- UUID 기반 Lock 식별
- Lua Script Unlock
- Retry + Exponential + jitter Backoff



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

### Spring Profile 설정

`application.yml`에 기본 프로파일이 지정되어 있지 않으므로, 로컬 실행 시 반드시 `local` 프로파일을 명시해야 합니다.

**IntelliJ 실행 설정**
```
Run/Debug Configurations → Active profiles → local
```

**VM options으로 지정**
```
-Dspring.profiles.active=local
```

## 로컬 실행 방법

### 방법 1. CLI로 직접 실행
```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

### 방법 2. Docker Compose로 실행

**[실행]**
1. 파일 수정 (필요 시)
2. `./gradlew bootJar`
3. `docker-compose up --build`
4. Postman으로 `localhost:8080` 테스트

**[종료]**
1. `docker-compose down` → 컨테이너 + 네트워크 삭제, DB 유지
2. `docker-compose down -v` → 컨테이너 + 네트워크 + DB 볼륨까지 삭제

**[이미지 삭제]**
1. `docker images` → 이미지 ID 확인
2. `docker rmi {이미지ID}` → 이미지 삭제 (컨테이너 중지 상태여야 함)
3. `docker rmi -f {이미지ID}` → 실행 중인 컨테이너가 있어도 강제 삭제

| 환경 | 프로파일 | 설정 파일 |
|------|---------|---------|
| 로컬 | `local` | `application-local.yml` + `.env` |
| 테스트 | (자동) | `src/test/resources/application.yml` (H2) |
| 운영 | `prod` | `application-prod.yml` (GitHub Secrets로 주입) |

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
