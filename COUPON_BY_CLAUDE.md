# 쿠폰 시스템 개선 요구사항

## 추가 요구사항
1. **정률 쿠폰 (PERCENTAGE)** 추가
2. **다회권 쿠폰 (Multi-use Coupon)** 추가

---

## 현재 상태 분석

### 현재 구현된 기능 (정액 쿠폰 단일 사용만 지원)

#### 1. Domain Layer (개념 객체)
- **Coupon.java** (core-api/domain/Coupon.java:8-14)
  ```java
  record Coupon(Long id, String name, CouponType type, BigDecimal discount, LocalDateTime expiredAt)
  ```
  - `type`: 쿠폰 타입 (현재 FIXED_AMOUNT만 사용)
  - `discount`: 할인 금액 (정액만 지원)

- **OwnedCoupon.java** (core-api/domain/OwnedCoupon.java:5-10)
  ```java
  record OwnedCoupon(Long id, Long userId, OwnedCouponState state, Coupon coupon)
  ```
  - `state`: DOWNLOADED 또는 USED (단일 사용만 지원)

#### 2. Enum
- **CouponType.java** (core-enum/CouponType.java:3-5)
  ```java
  enum CouponType { FIXED_AMOUNT }
  ```
  - 정액 쿠폰만 정의됨

- **OwnedCouponState.java** (core-enum/OwnedCouponState.java:3-6)
  ```java
  enum OwnedCouponState { DOWNLOADED, USED }
  ```
  - 단일 사용 전제 (다회권 부분 사용 상태 없음)

#### 3. Entity Layer (DB)
- **CouponEntity.java** (storage/CouponEntity.java:16-24)
  - 필드: `name`, `type`, `discount`, `expiredAt`
  - 다회권 관련 필드 없음

- **OwnedCouponEntity.java** (storage/OwnedCouponEntity.java:23-31)
  - 필드: `userId`, `couponId`, `state`, `version`
  - 사용 횟수 관련 필드 없음
  - `@Version`: 낙관적 락 지원 (동시성 제어)

#### 4. Logic Layer
- **PaymentDiscount.java** (core-api/domain/PaymentDiscount.java:33-42)
  ```java
  private static BigDecimal calculateCouponDiscount(List<OwnedCoupon> ownedCoupons, Long useOwnedCouponId) {
      // ...
      return ownedCoupon.coupon().discount(); // 정액 할인만 처리
  }
  ```
  - 정액 할인만 계산 (정률 로직 없음)

- **OwnedCouponReader.java** (core-api/domain/OwnedCouponReader.java)
  - `getOwnedCoupons()`: 사용자 소유 쿠폰 조회
  - `findOwnedForCheckout()`: 체크아웃 가능 쿠폰 조회
  - 기본 필드만 매핑 (다회권 필드 없음)

- **CouponReader.java** (core-api/domain/CouponReader.java:17-29)
  - `findActiveByIds()`: 활성 쿠폰 조회
  - 기본 필드만 매핑

- **CouponDownloader.java** (core-api/domain/CouponDownloader.java:19-31)
  - 쿠폰 다운로드 처리
  - 다회권 관련 로직 없음

- **OwnedCouponAdder.java** (core-api/domain/OwnedCouponAdder.java:16-24)
  - `addIfNotExists(userId, couponId)`: 소유 쿠폰 추가
  - 사용 횟수 초기화 로직 없음

### 요구사항 대비 누락 사항

#### 1. 정률 쿠폰 관련
- [ ] `CouponType.PERCENTAGE` enum 값 없음
- [ ] 정률 할인 계산 로직 없음 (PaymentDiscount)
- [ ] 정률 쿠폰 검증 로직 없음

#### 2. 다회권 쿠폰 관련
- [ ] 쿠폰 사용 횟수 필드 없음 (`usageLimit`, `isMultiUse`)
- [ ] 소유 쿠폰 사용 카운트 필드 없음 (`usedCount`, `remainingCount`)
- [ ] 다회권 사용 로직 없음
- [ ] 다회권 부분 사용 상태 표현 불가

---

## 작업 목록

### Phase 1: 정률 쿠폰 구현

#### 1.1 Enum 수정
- [ ] **CouponType.java** (core-enum/CouponType.java:3-5)
  - `PERCENTAGE` 추가
  ```java
  public enum CouponType {
      FIXED_AMOUNT,    // 정액 할인
      PERCENTAGE       // 정률 할인
  }
  ```

#### 1.2 Logic Layer - 할인 계산 로직 수정
- [ ] **PaymentDiscount.java** (core-api/domain/PaymentDiscount.java:33-42)
  - `calculateCouponDiscount()` 메서드 수정
  - `orderPrice` 파라미터 추가 필요
  - switch 문으로 쿠폰 타입별 계산 분기
  ```java
  private static BigDecimal calculateCouponDiscount(
      List<OwnedCoupon> ownedCoupons,
      Long useOwnedCouponId,
      BigDecimal orderPrice  // 추가
  ) {
      // ...
      Coupon coupon = ownedCoupon.coupon();
      return switch (coupon.type()) {
          case FIXED_AMOUNT -> coupon.discount();
          case PERCENTAGE -> orderPrice
              .multiply(coupon.discount())
              .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
      };
  }
  ```
  - 생성자에서 `orderPrice` 전달하도록 수정 필요 (PaymentDiscount.java:28)

#### 1.3 테스트
- [ ] 정률 쿠폰 할인 계산 테스트
  - 10% 할인 시 계산 검증
  - 소수점 반올림 검증

### Phase 2: 다회권 쿠폰 구현

#### 2.1 Domain 객체 수정
- [ ] **Coupon.java** (core-api/domain/Coupon.java:8-14)
  - 필드 추가
  ```java
  public record Coupon(
      Long id,
      String name,
      CouponType type,
      BigDecimal discount,
      LocalDateTime expiredAt,
      Integer usageLimit,     // 추가: 사용 가능 횟수 (null이면 단일 사용)
      Boolean isMultiUse      // 추가: 다회권 여부
  ) {
  }
  ```

- [ ] **OwnedCoupon.java** (core-api/domain/OwnedCoupon.java:5-10)
  - 필드 추가
  ```java
  public record OwnedCoupon(
      Long id,
      Long userId,
      OwnedCouponState state,
      Coupon coupon,
      Integer usedCount,        // 추가: 사용한 횟수
      Integer remainingCount    // 추가: 남은 횟수
  ) {
  }
  ```

#### 2.2 Entity 수정
- [ ] **CouponEntity.java** (storage/CouponEntity.java:16-34)
  - 필드 추가
  ```java
  private Integer usageLimit;    // nullable
  private Boolean isMultiUse;    // nullable, default: false
  ```
  - `create()` 메서드 시그니처에 파라미터 추가

- [ ] **OwnedCouponEntity.java** (storage/OwnedCouponEntity.java:23-41)
  - 필드 추가
  ```java
  private Integer usedCount;       // not null, default: 0
  private Integer remainingCount;  // nullable
  ```
  - `create()` 메서드 수정: `usageLimit` 파라미터 받아서 `remainingCount` 초기화
  - `use()` 메서드 수정: 다회권 고려
  - `revert()` 메서드 수정: 다회권 고려

#### 2.3 DB 마이그레이션
- [ ] **마이그레이션 스크립트 작성** (migrations/)
  - `coupon` 테이블
    ```sql
    ALTER TABLE coupon ADD COLUMN usage_limit INT NULL;
    ALTER TABLE coupon ADD COLUMN is_multi_use BOOLEAN NULL DEFAULT false;
    ```
  - `owned_coupon` 테이블
    ```sql
    ALTER TABLE owned_coupon ADD COLUMN used_count INT NOT NULL DEFAULT 0;
    ALTER TABLE owned_coupon ADD COLUMN remaining_count INT NULL;
    ```
  - 기존 데이터 마이그레이션
    ```sql
    -- 기존 쿠폰은 모두 단일 사용 쿠폰으로 처리
    UPDATE coupon SET is_multi_use = false, usage_limit = 1 WHERE is_multi_use IS NULL;

    -- 기존 소유 쿠폰 상태별 초기화
    UPDATE owned_coupon SET used_count = 0, remaining_count = 1 WHERE state = 'DOWNLOADED';
    UPDATE owned_coupon SET used_count = 1, remaining_count = 0 WHERE state = 'USED';
    ```

#### 2.4 Logic Layer 수정
- [ ] **CouponReader.java** (core-api/domain/CouponReader.java:22-28)
  - Coupon 생성 시 `usageLimit`, `isMultiUse` 필드 매핑 추가

- [ ] **OwnedCouponReader.java** (core-api/domain/OwnedCouponReader.java)
  - `getOwnedCoupons()` (37-48행): OwnedCoupon 생성 시 `usedCount`, `remainingCount` 매핑 추가
  - `findOwnedForCheckout()` (74-85행): OwnedCoupon 생성 시 `usedCount`, `remainingCount` 매핑 추가

- [ ] **OwnedCouponAdder.java** (core-api/domain/OwnedCouponAdder.java:16-24)
  - `addIfNotExists()` 메서드 시그니처 변경
  ```java
  public void addIfNotExists(Long userId, Long couponId, Integer usageLimit) {
      // ...
      ownedCouponRepository.save(
          OwnedCouponEntity.create(userId, couponId, OwnedCouponState.DOWNLOADED, usageLimit)
      );
  }
  ```

- [ ] **CouponDownloader.java** (core-api/domain/CouponDownloader.java:30)
  - `ownedCouponAdder.addIfNotExists()` 호출 시 `coupon.getUsageLimit()` 전달
  ```java
  ownedCouponAdder.addIfNotExists(userId, coupon.getId(), coupon.getUsageLimit());
  ```

#### 2.5 다회권 사용 로직 구현 (신규)
- [ ] **OwnedCouponUsageManager.java** 생성 (core-api/domain/) - Logic Layer
  - `@Component` 사용
  - `@Transactional` 적용 (쓰기 작업)
  ```java
  @Component
  @RequiredArgsConstructor
  public class OwnedCouponUsageManager {
      private final OwnedCouponRepository ownedCouponRepository;

      /**
       * 쿠폰 사용 가능 여부 검증
       */
      public boolean canUse(OwnedCoupon ownedCoupon) {
          if (ownedCoupon.state() == OwnedCouponState.USED) {
              return false;
          }

          Coupon coupon = ownedCoupon.coupon();
          if (Boolean.TRUE.equals(coupon.isMultiUse())) {
              // 다회권: 남은 횟수 확인
              return ownedCoupon.remainingCount() != null && ownedCoupon.remainingCount() > 0;
          } else {
              // 일반 쿠폰: DOWNLOADED 상태만 사용 가능
              return ownedCoupon.state() == OwnedCouponState.DOWNLOADED;
          }
      }

      /**
       * 쿠폰 사용 처리
       */
      @Transactional
      public void use(Long ownedCouponId) {
          OwnedCouponEntity entity = ownedCouponRepository.findById(ownedCouponId)
              .orElseThrow(() -> new CoreException(ErrorType.OWNED_COUPON_NOT_FOUND));

          entity.use(); // Entity의 use() 메서드 호출
      }

      /**
       * 쿠폰 사용 취소 (결제 실패/취소 시)
       */
      @Transactional
      public void revert(Long ownedCouponId) {
          OwnedCouponEntity entity = ownedCouponRepository.findById(ownedCouponId)
              .orElseThrow(() -> new CoreException(ErrorType.OWNED_COUPON_NOT_FOUND));

          entity.revert(); // Entity의 revert() 메서드 호출
      }
  }
  ```

- [ ] **OwnedCouponEntity.use()** 메서드 수정 (storage/OwnedCouponEntity.java:43-45)
  ```java
  public void use() {
      // 다회권인 경우
      if (this.remainingCount != null && this.remainingCount > 0) {
          this.usedCount++;
          this.remainingCount--;

          // 남은 횟수가 0이 되면 상태를 USED로 변경
          if (this.remainingCount == 0) {
              this.state = OwnedCouponState.USED;
          }
      } else {
          // 일반 쿠폰인 경우
          this.state = OwnedCouponState.USED;
          this.usedCount = 1;
          this.remainingCount = 0;
      }
  }
  ```

- [ ] **OwnedCouponEntity.revert()** 메서드 수정 (storage/OwnedCouponEntity.java:47-49)
  ```java
  public void revert() {
      // 다회권인 경우
      if (this.usedCount != null && this.usedCount > 0) {
          this.usedCount--;
          this.remainingCount++;
          this.state = OwnedCouponState.DOWNLOADED;
      } else {
          // 일반 쿠폰인 경우
          this.state = OwnedCouponState.DOWNLOADED;
          this.usedCount = 0;
          this.remainingCount = 1;
      }
  }
  ```

- [ ] **OwnedCouponEntity.create()** 메서드 수정 (storage/OwnedCouponEntity.java:33-41)
  ```java
  public static OwnedCouponEntity create(Long userId, Long couponId, OwnedCouponState state, Integer usageLimit) {
      OwnedCouponEntity ownedCoupon = new OwnedCouponEntity();
      ownedCoupon.userId = userId;
      ownedCoupon.couponId = couponId;
      ownedCoupon.state = state;
      ownedCoupon.version = 0L;
      ownedCoupon.usedCount = 0;
      ownedCoupon.remainingCount = usageLimit;  // usageLimit로 초기화

      return ownedCoupon;
  }
  ```

#### 2.6 Repository 쿼리 수정
- [ ] **OwnedCouponRepository.java** (storage/OwnedCouponRepository.java:16-32)
  - `findOwnedCouponIds()` 쿼리 수정
  - 다회권 쿠폰 조건 추가: `remainingCount > 0` 또는 `state = 'DOWNLOADED'`
  ```sql
  SELECT DISTINCT ownedCoupon FROM OwnedCouponEntity ownedCoupon
      JOIN CouponEntity coupon
          ON ownedCoupon.couponId = coupon.id
          AND ownedCoupon.userId = :userId
          AND (
              (coupon.isMultiUse = true AND ownedCoupon.remainingCount > 0)
              OR (coupon.isMultiUse = false AND ownedCoupon.state = 'DOWNLOADED')
          )
          AND ownedCoupon.status = 'ACTIVE'
  WHERE
      coupon.id IN :couponIds
      AND coupon.status = 'ACTIVE'
      AND coupon.expiredAt > :expiredAtAfter
  ```

#### 2.7 Business Layer 검토
- [ ] **OwnedCouponService.java** (core-api/domain/OwnedCouponService.java)
  - 쿠폰 사용 관련 메서드 추가 검토
  - 필요 시 `OwnedCouponUsageManager`에 위임하는 메서드 추가

#### 2.8 테스트
- [ ] 다회권 쿠폰 다운로드 테스트
  - `remainingCount = usageLimit` 초기화 검증
- [ ] 다회권 쿠폰 사용 테스트
  - 첫 사용: `usedCount = 1`, `remainingCount = n-1`, `state = DOWNLOADED`
  - 마지막 사용: `usedCount = n`, `remainingCount = 0`, `state = USED`
- [ ] 다회권 쿠폰 사용 취소 테스트
  - `usedCount` 감소, `remainingCount` 증가 검증
- [ ] 동시성 테스트
  - 동일 다회권 쿠폰 동시 사용 시 `@Version` 낙관적 락 동작 검증

---

## 검토 해야 할 사항

### 1. 비즈니스 정책

#### 1.1 정률 쿠폰 상세 정책
**결정 필요:**
- [ ] **discount 값 표현 방식**
  - 방안 A: 백분율 그대로 저장 (10% → `discount = 10`)
  - 방안 B: 소수점으로 저장 (10% → `discount = 0.1`)
  - **권장**: 방안 A (백분율 그대로 저장) - PaymentDiscount 구현 예시에서 `discount / 100` 계산

- [ ] **정률 쿠폰 최대/최소 할인 금액 정책**
  - 예: 10% 할인이지만 최대 5,000원까지만
  - 필요 시 Coupon에 `maxDiscountAmount`, `minDiscountAmount` 필드 추가
  - Phase 3 (선택적)로 분류

- [ ] **정률 쿠폰 적용 대상 금액**
  - 상품 원가?
  - 배송비 포함?
  - 다른 할인 적용 후 금액?

#### 1.2 다회권 쿠폰 상세 정책
**결정 필요:**
- [ ] **동일 주문에서 다회권 여러 번 사용 가능 여부**
  - 예: 3회권 쿠폰을 한 주문에서 2회 사용
  - 불가능하다면 체크아웃 시 검증 로직 필요
  - 현재 구현: 한 주문당 하나의 쿠폰만 사용 (PaymentDiscount.java:14 - `useOwnedCouponId` 단수)

- [ ] **다회권 만료 정책**
  - 방안 A: 다운로드 후 만료일까지 여러 번 사용 가능 (현재 구조)
  - 방안 B: 첫 사용 후 N일 이내에 모두 사용해야 함
  - **현재 구현**: 방안 A (Coupon.expiredAt 기반)

- [ ] **usageLimit 값 범위**
  - 최소값: 2? (1이면 일반 쿠폰과 동일)
  - 최대값: 제한 필요 여부

- [ ] **남은 횟수 0인 쿠폰 처리**
  - `state = USED`로 변경?
  - 그대로 `DOWNLOADED` 유지하고 `remainingCount`로만 판단?
  - **권장**: `state = USED`로 변경 (OwnedCouponEntity.use() 구현 예시 참고)

#### 1.3 쿠폰 적용 정책
**결정 필요:**
- [ ] **한 주문에 여러 쿠폰 적용 가능 여부**
  - 현재: 단일 쿠폰만 사용 (PaymentDiscount.java:14 - `useOwnedCouponId`)
  - 변경 필요 시 대규모 리팩토링 필요

- [ ] **정액/정률 쿠폰 혼용 시 적용 순서**
  - 정액 먼저 → 정률 (할인 적게 됨)
  - 정률 먼저 → 정액 (할인 많이 됨)
  - 현재는 한 주문당 하나의 쿠폰만 사용하므로 해당 없음

#### 1.4 쿠폰 사용 취소 정책
**결정 필요:**
- [ ] **결제 실패/취소 시 자동 복원 여부**
  - 자동 복원: 결제 실패 시 자동으로 `revert()` 호출
  - 수동 복원: 별도 API 호출 필요

- [ ] **부분 취소 시 쿠폰 처리**
  - 예: 3개 상품 주문 중 1개 취소 시
  - 쿠폰 할인 재계산 필요 여부

### 2. 데이터 정합성

#### 2.1 기존 데이터 마이그레이션
**검증 필요:**
- [ ] **기존 쿠폰 데이터**
  - `isMultiUse = false`, `usageLimit = 1`로 초기화
  - 운영 DB에 쿠폰이 몇 개나 있는지 확인

- [ ] **기존 소유 쿠폰 데이터**
  - `state = DOWNLOADED` → `usedCount = 0`, `remainingCount = 1`
  - `state = USED` → `usedCount = 1`, `remainingCount = 0`
  - 데이터 정합성 검증 쿼리 작성

#### 2.2 NULL 처리
**검증 필요:**
- [ ] **Coupon.isMultiUse가 NULL인 경우**
  - `Boolean.TRUE.equals(coupon.isMultiUse())` 사용하여 NULL-safe 처리
  - 또는 DB 제약 조건으로 NOT NULL DEFAULT false

- [ ] **OwnedCoupon.remainingCount가 NULL인 경우**
  - 일반 쿠폰(단일 사용)은 `remainingCount = NULL` 허용?
  - 아니면 모두 숫자 값으로 관리?
  - **권장**: 모두 숫자 값으로 관리 (일반 쿠폰: `remainingCount = 1`)

### 3. 성능 및 동시성

#### 3.1 동시성 제어
**검증 필요:**
- [ ] **다회권 쿠폰 동시 사용 시나리오**
  - 같은 사용자가 동시에 여러 주문에서 동일 다회권 쿠폰 사용
  - `@Version` 낙관적 락으로 제어 (OwnedCouponEntity.java:30-31)
  - 실패 시 재시도 로직 필요 여부

- [ ] **락 실패 처리**
  - `OptimisticLockException` 발생 시 사용자에게 어떤 메시지?
  - "쿠폰이 이미 사용되었습니다" vs "다시 시도해주세요"

#### 3.2 조회 성능
**검증 필요:**
- [ ] **OwnedCouponRepository.findOwnedCouponIds() 쿼리 성능**
  - 다회권 조건 추가 시 인덱스 활용 가능 여부
  - `remainingCount`에 인덱스 필요 여부

- [ ] **N+1 문제**
  - OwnedCouponReader는 일괄 조회 후 Map 매핑 → 문제없음 (검증 완료)

### 4. 아키텍처 및 설계

#### 4.1 OwnedCouponState의 역할
**검토 사항:**
- 현재: `DOWNLOADED`, `USED` 2가지
- 다회권 부분 사용 상태를 state로 표현할 필요가 있는가?
  - **방안 A**: `PARTIALLY_USED` 상태 추가
    - 장점: 명시적 상태 표현
    - 단점: 상태 전이 복잡도 증가
  - **방안 B**: `remainingCount`로만 판단 (권장)
    - 장점: 단순함
    - 단점: state 필드의 의미가 약해짐
- **권장**: 방안 B
  - `state`는 완전 소진 여부만 표현 (DOWNLOADED/USED)
  - 사용 가능 여부는 `remainingCount > 0`으로 판단

#### 4.2 트랜잭션 범위
**검토 사항:**
- [ ] **OwnedCouponUsageManager 메서드별 트랜잭션**
  - `canUse()`: 읽기 전용 → `@Transactional` 불필요
  - `use()`: 쓰기 → `@Transactional` 필요
  - `revert()`: 쓰기 → `@Transactional` 필요

- [ ] **결제 플로우 전체 트랜잭션**
  - 주문 생성 + 쿠폰 사용 + 포인트 차감을 하나의 트랜잭션으로?
  - 별도 트랜잭션으로 분리?
  - **권장**: 별도 트랜잭션 (각 Logic Layer 컴포넌트에서 관리)

#### 4.3 컴포넌트 분리
**검토 사항:**
- [ ] **CouponValidator 분리 필요 여부**
  - 쿠폰 사용 가능 여부 검증 로직이 여러 곳에서 필요한가?
  - 필요 시 별도 Validator 클래스로 분리
  - Phase 3 (선택적)로 분류

### 5. 테스트 전략

#### 5.1 통합 테스트 시나리오
**작성 필요:**
- [ ] 쿠폰 생성 → 다운로드 → 사용 → 결제 → 취소 전체 플로우
- [ ] 다회권 3회권 쿠폰: 3번 사용 후 사용 불가 검증
- [ ] 정률 쿠폰: 주문 금액별 할인 금액 계산 검증

#### 5.2 동시성 테스트
**작성 필요:**
- [ ] 동일 다회권 쿠폰을 동시에 여러 스레드에서 사용
  - `@Version` 낙관적 락 동작 검증
  - 실패한 스레드의 예외 처리 검증

#### 5.3 경계값 테스트
**작성 필요:**
- [ ] 정률 쿠폰 0% 할인
- [ ] 정률 쿠폰 100% 할인
- [ ] 다회권 쿠폰 `usageLimit = 1` (일반 쿠폰과 동일)
- [ ] 주문 금액이 0원일 때 정률 쿠폰 적용

### 6. 마이그레이션 관련

#### 6.1 마이그레이션 도구 확인
**확인 필요:**
- [ ] migrations/ 디렉토리가 untracked 상태 (gitStatus)
- [ ] 마이그레이션 도구 사용 여부 (Flyway, Liquibase 등)
- [ ] 스크립트 네이밍 규칙
- [ ] 로컬/개발/스테이징/운영 환경별 적용 전략

#### 6.2 롤백 전략
**준비 필요:**
- [ ] 마이그레이션 실패 시 롤백 스크립트
- [ ] 데이터 정합성 검증 쿼리
- [ ] 배포 전 스테이징 환경 테스트

---

## 작업 우선순위 및 순서

### Phase 1: 정률 쿠폰 구현 (1-2일)
**목표**: 정액 + 정률 쿠폰 지원 (단일 사용)

1. `CouponType.PERCENTAGE` 추가
2. `PaymentDiscount.calculateCouponDiscount()` 수정
3. 테스트 작성 및 검증
4. 비즈니스 정책 확정 (discount 표현 방식, 최대/최소 금액)

**체크포인트**: 정률 쿠폰으로 결제 가능

### Phase 2: 다회권 쿠폰 구현 (3-5일)
**목표**: 정액/정률 + 단일/다회권 모두 지원

1. Domain, Entity 필드 추가
2. DB 마이그레이션 스크립트 작성 및 실행
3. Logic Layer 매핑 수정 (Reader, Adder, Downloader)
4. `OwnedCouponUsageManager` 구현
5. Entity의 `use()`, `revert()`, `create()` 메서드 수정
6. Repository 쿼리 수정
7. 통합 테스트 작성 및 검증
8. 동시성 테스트 작성 및 검증

**체크포인트**: 3회권 쿠폰 다운로드 후 3번 사용 가능

### Phase 3: 정책 검증 및 보완 (선택적, 1-2일)
**목표**: 엣지 케이스 처리 및 성능 최적화

1. 정률 쿠폰 최대/최소 할인 금액 정책 (필요 시)
2. CouponValidator 분리 (필요 시)
3. 동시성 제어 강화 (재시도 로직 등)
4. 성능 모니터링 및 최적화
5. 문서화 (API 스펙, 비즈니스 정책)

---

## 참고 파일 경로

### Domain Layer
- `core/core-api/src/main/java/io/dodn/commerce/core/domain/Coupon.java`
- `core/core-api/src/main/java/io/dodn/commerce/core/domain/OwnedCoupon.java`
- `core/core-api/src/main/java/io/dodn/commerce/core/domain/PaymentDiscount.java`

### Logic Layer
- `core/core-api/src/main/java/io/dodn/commerce/core/domain/CouponReader.java`
- `core/core-api/src/main/java/io/dodn/commerce/core/domain/OwnedCouponReader.java`
- `core/core-api/src/main/java/io/dodn/commerce/core/domain/CouponDownloader.java`
- `core/core-api/src/main/java/io/dodn/commerce/core/domain/OwnedCouponAdder.java`
- `core/core-api/src/main/java/io/dodn/commerce/core/domain/OwnedCouponUsageManager.java` (신규)

### Business Layer
- `core/core-api/src/main/java/io/dodn/commerce/core/domain/CouponService.java`
- `core/core-api/src/main/java/io/dodn/commerce/core/domain/OwnedCouponService.java`

### Enum
- `core/core-enum/src/main/java/io/dodn/commerce/core/enums/CouponType.java`
- `core/core-enum/src/main/java/io/dodn/commerce/core/enums/OwnedCouponState.java`

### Entity & Repository
- `storage/db-core/src/main/java/io/dodn/commerce/storage/db/core/CouponEntity.java`
- `storage/db-core/src/main/java/io/dodn/commerce/storage/db/core/OwnedCouponEntity.java`
- `storage/db-core/src/main/java/io/dodn/commerce/storage/db/core/OwnedCouponRepository.java`
- `storage/db-core/src/main/java/io/dodn/commerce/storage/db/core/CouponRepository.java`

### Migration
- `migrations/` (신규 스크립트 추가 예정)
