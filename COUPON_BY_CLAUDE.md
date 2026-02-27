# 쿠폰 추가 요구사항 분석

## 현재 구조 요약

| 파일 | 설명 |
|------|------|
| `CouponType` | `FIXED_AMOUNT` 단일 타입 |
| `Coupon` | `id`, `name`, `type`, `discount(BigDecimal)`, `expiredAt` |
| `OwnedCoupon` | `id`, `userId`, `state(OwnedCouponState)`, `coupon` |
| `OwnedCouponState` | `DOWNLOADED`, `USED` |
| `OwnedCouponEntity` | `userId`, `couponId`, `state`, `version(낙관적 락)`, unique index `(userId, couponId)` |

---

## 요구사항 1: 정률 쿠폰 (`FIXED_RATE`)

### 작업 목록

- [ ] **[core-enum]** `CouponType`에 `FIXED_RATE` 값 추가
- [ ] **[core-api]** `Coupon` record 또는 별도 도메인 객체에 정률 할인 계산 메서드 추가
  - `discount` 필드가 정액(`FIXED_AMOUNT`)에서는 원화 금액, 정률(`FIXED_RATE`)에서는 비율(%) 의미가 달라지므로 계산 책임 위치 결정 필요
- [ ] **[core-api]** `CouponEntity.create()` - 정률 쿠폰 생성 시 유효성 검증 추가 (0 < discount ≤ 100)
- [ ] **[db-core]** `coupon` 테이블에 `max_discount_amount` 컬럼 추가 여부 결정 및 반영
  - 정률 쿠폰은 가격이 높을수록 할인액이 커지므로 상한선 설정 여부 확인 필요
- [ ] **[db-core]** DB 마이그레이션 스크립트 작성 (필요 시 컬럼 추가)

---

## 요구사항 2: 다회권 쿠폰

### 작업 목록

- [ ] **[core-enum]** `CouponType`에 `MULTI_USE_FIXED_AMOUNT`, `MULTI_USE_FIXED_RATE` 추가
  - 또는 `CouponEntity`에 `totalUseCount` 컬럼 추가로 다회권 여부를 표현하는 방식 결정 (설계 검토 후 진행)
- [ ] **[db-core]** `coupon` 테이블에 `total_use_count` 컬럼 추가 (다회권 최대 사용 횟수)
- [ ] **[db-core]** `owned_coupon` 테이블에 `remaining_count` 컬럼 추가 (잔여 사용 횟수)
- [ ] **[db-core]** `OwnedCouponEntity`에 `remainingCount` 필드 및 차감 로직 추가
  - `use()` 메서드: 다회권은 state를 `USED`로 변경하는 대신 `remainingCount` 감소, 잔여 횟수 0 도달 시 `USED` 전환
- [ ] **[core-api]** `OwnedCouponAdder.addIfNotExists()` 변경
  - 다회권 쿠폰 다운로드 시: 이미 존재하면 예외 발생 대신 `remainingCount` 증가 또는 정책 결정
- [ ] **[db-core]** `OwnedCouponRepository.findOwnedCouponIds()` JPQL 수정
  - 다회권의 경우 `state = 'DOWNLOADED'` 조건 외에 `remainingCount > 0` 조건 추가
- [ ] **[core-api]** `OwnedCouponService` - 다회권 사용 처리 메서드 추가 또는 변경
- [ ] **[db-core]** DB 마이그레이션 스크립트 작성

---

## 검토 사항

### 정률 쿠폰

1. **`discount` 필드 의미 이중성**
   - `FIXED_AMOUNT`: 원화 금액 (예: 3000.00)
   - `FIXED_RATE`: 할인율 (예: 10.00 → 10%)
   - 같은 `discount(BigDecimal)` 필드를 재사용 vs. `discountAmount`, `discountRate` 분리 여부 결정 필요

2. **최대 할인 금액 캡 (`maxDiscountAmount`)**
   - 정률 쿠폰은 고가 상품 적용 시 할인액이 무제한으로 커질 수 있음
   - "최대 N원까지만 할인" 제한 여부 비즈니스 결정 필요
   - 필요하다면 `coupon` 테이블에 컬럼 추가 및 `CouponEntity`, `Coupon` record 변경 범위 확인

3. **할인 계산 책임 위치**
   - 현재 `Coupon` record에 계산 로직 없음
   - 타입별 분기(`if FIXED_AMOUNT ... else if FIXED_RATE ...`)를 Service 레이어에서 처리할 경우 응집도 저하
   - `Coupon` 도메인에 `calculateDiscount(BigDecimal price)` 메서드 추가 권장

4. **정률 쿠폰 유효성 검증**
   - `discount` 값 범위: 0 초과 100 이하 (또는 0.0 ~ 1.0 소수 표현으로 할지) 기준 통일 필요
   - 저장 시점(`CouponEntity.create`) vs. 도메인 레이어(`Coupon`) 중 어디서 검증할지 결정

---

### 다회권 쿠폰

5. **다회권의 `USED` 상태 전환 시점**
   - 잔여 횟수가 0이 되는 시점에 `state = USED`로 전환하는 방식이 자연스러우나,
   - 현재 `findOwnedCouponIds` 쿼리가 `state = 'DOWNLOADED'`만 조회하므로 전환 시점을 명확히 정의해야 함

6. **고유 제약 조건 `unique(userId, couponId)` 유지 여부**
   - 현재 unique index로 동일 쿠폰 중복 다운로드를 DB 레벨에서 차단
   - 다회권도 1인 1장 원칙 유지 시 → 동일 레코드에 `remainingCount` 증가 방식 적합
   - 동일 쿠폰을 여러 장 소유 가능하게 할 경우 → unique index 제거 및 구조 변경 필요

7. **다회권 사용 단위 정의**
   - 주문 1건 = 1회 사용인지, 주문 내 상품 N개 = N회 사용인지 비즈니스 규칙 확인 필요

8. **다회권 동시성 제어**
   - `OwnedCouponEntity`에 이미 `@Version` 낙관적 락 적용 중
   - `remainingCount` 차감 시 동일하게 낙관적 락으로 커버 가능한지 확인
   - 동시 사용 요청이 많은 경우 재시도 정책 필요 여부 검토

9. **다회권의 `totalUseCount` 관리 위치**
   - `coupon` 테이블: 쿠폰 정의 수준에서 "이 쿠폰은 최대 N회 사용 가능"
   - `owned_coupon` 테이블: 개인 소유 수준에서 잔여 횟수 관리
   - 둘 다 필요한지, 한쪽만으로 충분한지 결정 필요

10. **API 응답 변경 범위**
    - 쿠폰 조회 API에서 타입별로 응답 필드가 달라질 경우 (`maxDiscountAmount`, `remainingCount` 등)
    - 기존 API 스펙 하위 호환성 유지 방법 검토 필요
