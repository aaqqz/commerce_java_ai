# 프로젝트 가이드라인

## 모듈 구조
- 루트: Gradle 멀티모듈, Java 21, Spring Boot 3.5.x 기반
- core
  - core-enum: 공통 열거형 정의(`io.dodn.commerce.core.enums`)
  - core-api: API 서버 모듈(컨트롤러/도메인/서포트)
- storage
  - db-core: JPA 엔티티/리포지토리/DB 설정
- support
  - logging: 로그 설정 리소스(`logback-*.xml`, `logging.yml`)
  - monitoring: 모니터링 설정 리소스(`monitoring.yml`)

의존 관계(중요): `core:core-api` → `core:core-enum`, `storage:db-core`, `support:logging`, `support:monitoring`

## 패키지 구조
- 기본 패키지: `io.dodn.commerce`
- core-api
  - `core.api.controller`: REST API 컨트롤러
    - `v1`: 버전별 엔드포인트
    - `request`/`response`: 요청/응답 DTO
  - `core.domain`: 비즈니스 도메인 모델/서비스(Record, Service 등)
  - `core.support`: 공통 지원(응답 래퍼, 에러, 인증/리졸버)
- core-enum
  - `core.enums`: 도메인/상태 열거형
- storage:db-core
  - `storage.db.core`: JPA 엔티티/리포지토리
  - `storage.db.core.config`: 데이터소스/JPA 설정
  - `storage.db.core.converter`: 암복호화 컨버터

## 코드 스타일
- Java 21 표준 사용, 모듈 빌드 스크립트는 Kotlin DSL(`.kts`)
- Lombok 사용(@RequiredArgsConstructor, @Getter 등)으로 보일러플레이트 최소화
- 도메인/응답 모델은 `record`로 불변 데이터 표현을 선호
- 컨트롤러는 `ApiResponse<T>`로 일관된 응답 형식 유지
- 리포지토리는 Spring Data JPA 인터페이스 형태로 작성
- 테스트 태그 사용(`develop`, `context`) 및 Gradle 테스트 태스크 분리

## 설계 스타일
- 계층 구조: API(Controller) → Domain(Service/Record) → Storage(JPA Entity/Repository)
- 도메인 모델과 영속 엔티티를 분리(도메인은 Record, DB는 Entity)
- 비즈니스 로직은 `core.domain` 서비스에 집중, 컨트롤러는 얇게 유지
- 에러 처리는 `CoreException` + `ErrorType` 기반으로 통일
- 모듈별 책임 분리: core는 비즈니스/API, storage는 영속성, support는 공통 설정
