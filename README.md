## **프로젝트 개요**

**🔹 주요 기능**
- **회원가입 & 로그인**: JWT 기반 인증 및 허가된 사용자만 가입 가능
- **연말정산 데이터 스크래핑**: 외부 API를 통해 소득 및 공제 정보 조회
- **결정세액 계산**: 과세표준에 따른 산출세액 및 세액공제 반영
- **API 문서 제공**: Swagger UI를 통해 API 명세 확인 및 테스트 가능

**🔹 기술 스택**

- Java 17
- Spring Boot 3.4.3
- Spring Security
- JPA
- H2 Database
- Lombok
- SpringDoc
- Junit & Mockito

## 프로젝트 구조

```
├── common                   # 공통 유틸 및 전역
│   ├── AesEncryptor.java
│   ├── AllowedUsers.java       # 회원가입 허용하는 사용자 목록
│   ├── NumberFormatUtil.java
│   ├── TokenProvider.java      # 토큰관리
│   └── response                # 공통응답객체
├── configuration            # Spring 설정관련 클래스
│   ├── AppConfig.java
│   ├── JwtTokenValidatorFilter.java
│   ├── SecurityConfig.java
│   ├── SpringDocConfig.java
│   └── TaxRateConfig.java
├── controller               # API 엔드포인트
│   ├── SzsController.java
├── domain                   # 핵심 도메인 모델
│   ├── TaxBracket.java         # 세율구간
│   ├── User.java               # 사용자 
│   └── UserIncome.java         # 사용자 연말정산
├── exception                # 예외처리 관련 클래스
│   ├── BaseException.java
│   ├── ErrorCode.java
│   └── GlobalExceptionHandler.java
├── infrastructure          # 외부 API 및 데이터 레이어
│   ├── ScrapingApiCaller.java
│   ├── ScrapingHttpApiCaller.java
│   ├── UserIncomeRepository.java
│   ├── UserRepository.java
└── service                 # 핵심 비즈니스 로직
    ├── LoginService.java
    ├── RefundService.java
    ├── ScrapingService.java
    ├── SignupService.java
    └── TaxCalculator.java  # 산출세액 계산기

```

**🔹 주요 패키지 설명**

- **common** → 전역 유틸리티 및 보안 관련 설정 관리
- **configuration** → Spring Security 및 설정 관련 클래스 관리
- **controller** → API 요청을 처리하는 컨트롤러 계층
- **domain** → 핵심 도메인 모델 정의
- **exception** → 커스텀 예외 및 예외 처리 로직 관리
- **infrastructure** → DB 및 외부 API 호출 등 데이터 액세스 계층
- **service** → 주요 비즈니스 로직 처리

## API 문서

### **Swagger 연동**

- API 테스트를 위해 Swagger UI 제공
- JWT 토큰 요청이 가능하도록 함
- **접속 경로**: [localhost:8080/3o3/swagger.html](http://localhost:8080/3o3/swagger.html)

### API 상세 설명

- 회원가입
    - 기등록된 사용자 목록에 있는 사용자만 가입 가능
    - 요청 예시

        ```
        {
          "userId": "user123",
          "password": "password123",
          "name": "동탁",
          "regNo": "921108-1582816"
        }
        
        ```

    - 응답 반환 없음
    - 예외 케이스
        - `400 BAD_REQUEST` → 유효하지 않은 요청 값 (중복 아이디, 기등록된 사용자가 아님)

- 로그인
    - Id, Password 로 로그인 후, JWT Access Token 발급
    - 요청 예시

        ```
        {
          "userId": "user123",
          "password": "password123"
        }
        ```

    - 응답 예시

        ```
        {
          "data": {
            "accessToken": "eyJbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
          }
        }
        ```

    - 예외 케이스
        - `401 UNAUTHORIZED` → 존재하지 않는 사용자, 비밀번호 불일치

- 연말정산 데이터 스크레핑
    - 로그인한 사용자의 주민등록번호를 복호화하여, 외부 API를 통해 연말정산 데이터를 가져옴
    - 요청 헤더

        ```json
        Authorization: Bearer {accessToken}
        ```

    - 응답 반환 없음
    - 예외 케이스
        - `401 UNAUTHORIZED` → 인증 실패
        - `500 INTERNAL_SERVER_ERROR` → 외부 API 오류

- 결정세액 조회
    - 사용자 연말정산 데이터를 바탕으로 결정세액 조회
    - 없는 경우 비어있는 리스트 반환
    - 요청 헤더

        ```json
        Authorization: Bearer {accessToken}
        ```

    - 응답 예시

        ```json
        {
          "data": [
            {
              "year": 2023,
              "결정세액": "150,000"
            },
            {
              "year": 2022,
              "결정세액": "-50,000"
            }
          ]
        }
        
        ```

    - 예외 케이스
        - `401 UNAUTHORIZED` → 인증 실패

## ✅ **필수 요구 사항**
- ✔️ **Java 17, Spring Boot 3.x, JPA, H2, Gradle**을 활용합니다.
- ✔️ 프로젝트 **문자 집합은 `UTF-8`**로 설정합니다.
- ✔️ 서버 포트는 기본값 **8080**을 사용합니다.
- ✔️ **DB는 H2 Database**, 메모리 모드로 실행합니다.
- ✔️ **회원가입, 로그인, 스크래핑, 환급액 계산 API**를 구현합니다.
- ✔️ 모든 요청, 응답에 대해 **`application/json` 타입**으로 구현합니다.
- ✔️ **Swagger UI 경로**는 `/3o3/swagger.html`을 사용합니다.
- ✔️ **민감정보(주민등록번호, 비밀번호 등)은 암호화하여 저장**합니다.

## 🛠 **작업 목록 및 구현 방법**

### 🔹 **회원가입 및 사용자 등록**

- **사전 등록된 사용자만 가입 가능하도록 구현**
  → 전역 변수(`AllowedUsers`)로 사용자 목록을 관리 (간단하지만, 사용자 변경 시 클래스 수정 및 배포 필요)
- **공통 응답 규격 적용 (`CommonResponse`)**
  → API 응답 형식을 통일하여 일관성 유지

### 🔹 **보안 및 데이터 보호**

- **로그인 방식 Spring Security 표준 인증 흐름 활용**
  → UserDetailsService와 AuthenticationProvider를 활용하여, 이후 OAuth2, JWT Refresh Token 등을 추가할 때도 기존 인증 흐름을 변경하지 않고 확장 가능
- **비밀번호는 `BCryptPasswordEncoder`를 사용하여 단방향 암호화**
  → Spring Security의 `PasswordEncoder`를 사용하여 보안성을 강화하고, `salt`가 적용된 암호화를 통해 동일한 비밀번호라도 다른 해시값이 생성되도록 처리
- **주민등록번호는 양방향 암호화 (`AES`) 사용**
  → 복호화가 필요한 주민등록번호는 AES로 암호화 후 저장, 사용 시 복호화 가능하도록 처리
  → API 응답에는 주민등록번호가 포함되지 않도록 주의
- **Spring Security 기반의 인증/인가 적용**
  → `JwtTokenValidatorFilter`를 통해 토큰 검증 및 사용자 인증 수행

### 🔹 **외부 API 호출 (`Scraping`)**

- **클린 아키텍처 적용하여 인터페이스와 구현 분리**
  → API 호출을 `ScrapingApiCaller` 인터페이스로 추상화하고, `ScrapingHttpApiCaller`에서 구현
- **응답 데이터를 `ScrapingData` 객체로 변환하여 저장**
  → 불필요한 원본 데이터는 제외하고 필요한 데이터 필드만 추출
- API 호출 중 발생하는 예외 처리를 강화하여 오류 발생 시 원인 추적 가능
- 주민등록번호는 로그에 마스킹하여 보안 강화

```
[Scraping API 요청] 요청 데이터: ScrapingRequest(name=동탁, regNo=921108--*******)
[Scraping API 호출 실패] 예외 발생: 401 Unauthorized on POST request for "https://codetest-v4.3o3.co.kr/scrap": [no body]
```

### 🔹 **연말정산 결정세액 계산 (`Refund`)**

- **`ScrapingData`를 `UserIncome`으로 변환하여 저장**
  → 유지보수성을 위해 데이터 변환 로직은 `ScrapingData` 내부에서 처리하여 서비스레이어 가독성을 높임
- **산출세액 계산 방식 변경 가능성을 고려하여 `yml` 설정을 활용**
  → 추후 세율 변경 시 코드 수정 없이 `application.yml`에서 조정 가능

  → 추후 yml에서 db로 저장소를 옮기면 변경해도 배포없이 반영이 가능


```yaml
tax:
  brackets:
    - lowerBound: 0
      upperBound: 14000000
      taxRate: 0.06
      progressiveDeduction: 0
    - lowerBound: 14000001
      upperBound: 50000000
      taxRate: 0.15
      progressiveDeduction: 840000
    - lowerBound: 50000001
      upperBound: 88000000
      taxRate: 0.24
      progressiveDeduction: 6240000
    - lowerBound: 88000001
      upperBound: 150000000
      taxRate: 0.35
      progressiveDeduction: 15360000
    - lowerBound: 150000001
      upperBound: 300000000
      taxRate: 0.38
      progressiveDeduction: 19400000
    - lowerBound: 300000001
      upperBound: 500000000
      taxRate: 0.40
      progressiveDeduction: 25400000
    - lowerBound: 500000001
      upperBound: 1000000000
      taxRate: 0.42
      progressiveDeduction: 37406000
    - lowerBound: 1000000001
      upperBound: 999999999999
      taxRate: 0.45
      progressiveDeduction: 38406000
```

- **산출세액 = 누진공제 + (과세표준 - 기준) * 세율**
  → 소득세율 기준이 변경될 경우에도 유연하게 대처할 수 있도록 `TaxRateConfig`에 정의

    ```java
    private final TaxRateConfig taxRateConfig;
    public BigDecimal calculateTax(BigDecimal taxableIncome) {
      for (TaxRateConfig.TaxBracket bracket : taxRateConfig.getBrackets()) {
        if (taxableIncome.compareTo(bracket.getLowerBound()) >= 0 &&
            taxableIncome.compareTo(bracket.getUpperBound()) <= 0) {
          return calculateTax(taxableIncome, bracket.getLowerBound(), bracket.getTaxRate(), bracket.getProgressiveDeduction());
        }
      }
      return BigDecimal.ZERO;
    }
      
    /**
     * // 산출세액 = 누진공제 + (과세표준 - 기준) * 세율
     *
     * @param taxableIncome        과세표준
     * @param lowerBound           기준
     * @param taxRate              세율
     * @param progressiveDeduction 누진공제
     * @return 산출세액
     */
    public static BigDecimal calculateTax(BigDecimal taxableIncome, BigDecimal lowerBound, BigDecimal taxRate, BigDecimal progressiveDeduction) {
      return taxableIncome.subtract(lowerBound) // 기준값 차감
        .multiply(taxRate) //  세율 적용
          .add(progressiveDeduction) // 누진공제 추가
          .setScale(0, RoundingMode.HALF_UP); // 최종 결과에서 반올림 적용
    }
    ```

- **Spring Security를 이용한 API 접근 제어**
  → `/szs/refund` API 호출 시 `Bearer Token`을 검증하여 사용자 정보를 확인한 후, 해당 사용자의 환급 정보를 반환

## 데이터 모델

### UserIncome

- 설명
    - Scraping 후 가져온 사용자의 소득정보를 저장
    - 결정세액 조회 시 필요한 데이터들 저장
    - 연말정산 데이터는 년도별 저장되는 구조를 가지며, 사용자의 수입과 공제정보가 연도단위로 관리될 수 있음
    - 👉 따라서, 연도를 포함한 복합 키(`userId + taxYear`)를 통해 사용자의 연말정산 데이터를 구분
- taxYear을 포함한 이유
    - 연말정산은 매년 기준이 변경될 수 있으므로 각 연도의 데이터를 독립정으로 저장
    - 중복 데이터 저장 방지
    - 이전 연도 데이터 조회 가능
    - 최근 연도 조회 확률이 높으므로 캐싱 및 최적화에 유리

## 성능최적화 전략

### **1. 비동기 처리 (`@Async`)**

- **`/szs/scrap` 요청은 API 응답을 기다릴 필요 없음**
- 스크래핑 외부 API 호출시 1~20초의 응답이 소요될 수 있으므로, 비동기처리를 통해 클라이언트에 빠른 응답 보장

### **2. Timeout 설정 (API 호출 지연 방지)**

- 스크래핑 API 응답이 **최소 1초 ~ 최대 20초까지 걸릴 수 있음을 고려하여**
- **Timeout을 최대 25초 이내로 설정하여 장시간 대기 방지**

### 3. 사용자 소득 정보 캐싱

- 배경
    - 연말정산 데이터는 한 번 스크래핑하면 자주 변경되지 않으며, 이후의 대부분의 요청은 단순히 저장된 데이터를 조회하는 형태임
    - 스크래핑 API는 호출 시 1~20초의 응답 시간이 소요될 수 있으므로, 불필요한 API 호출을 방지하기 위해 캐시를 사용하여 최적화함
    - 캐시는 **최대 24시간 동안 유지**, 한 번 저장된 데이터는 해당 기간 동안 재활용됨
- 로직

    ```
    📌 Scrap API 요청 흐름 (/szs/scrap) 
    1️⃣ 스크래핑 요청을 받음
    2️⃣ 캐시에 데이터가 있으면 API 요청을 생략하고 기존 데이터 활용
    3️⃣ DB에 데이터가 있으면 API 요청을 생략하고 기존 데이터 활용
    4️⃣ 캐시 & DB에 데이터가 없는 경우만 API 호출하여 데이터를 가져옴
    5️⃣ 스크래핑한 데이터를 DB에 저장 후, 캐시 동기화
    
    📌 Refund API 요청 흐름 (/szs/refund) 
    1️⃣ 환급 조회 요청을 받음
    2️⃣ 캐시에 데이터가 있으면 API 호출 없이 캐시 데이터 활용
    3️⃣ 캐시에 데이터가 없으면 DB에서 조회하여 반환
    ```

- 장점
    - API 호출을 최소화하여 **트래픽과 응답 시간 절감**
    - **스크래핑 시점에서만** 새로운 데이터를 받아 캐시를 갱신하여 불필요한 데이터 오염 방지
    - **환급 조회 시에는 캐시 데이터 우선 활용**하여 빠른 응답 제공
    - 연말정산 데이터는 자주 변경되지 않기 때문에, 일정 주기로 **스케줄링(Scheduler) 기반 동기화**를 하면 데이터 일관성 유지 가능(이것은 저의 추측입니다)
- 해결해야 하는 문제점(데이터 불일치)
    - 캐시에 저장된 데이터가 최신이 아닐 수 있음
    - db에 저장된 데이터도 최신이 아닐 수 있음
- 방안
    - 스케줄러 활용
        - 매일 특정 시간에 캐시를 무효화하고 최신데이터로 갱신
        - 대량의 데이터가 한번에 업데이트 되는 경우 유용함
    - 요청이 올때마다 비동기로 최신 데이터 동기화
        - 클라이언트는 캐싱된 데이터를 즉시 받아가지만
        - 백그라운드에서 비동기로 API를 호출하여 최신 데이터를 갱신
        - 데이터 변경이 빈번할 때는 유용하지만, 동기화 부담이 증가
    - 캐시(TTL)을 단축화 하여 최신 데이터 유지
        - 현재 24시간이지만, 추후 TTL을 단축하여 최신 데이터 유지 가능
        - 하지만 캐시 재사용률이 떨어지고 DB 부하가 증가할 수 있음

## 테스트 코드

1. 회원가입(SIgnup)

| 테스트 항목 | 설명 |
| --- | --- |
| `회원가입 성공` | 정상적인 회원가입 요청 시, 데이터가 저장되는지 검증 |
| `이미 존재하는 사용자 가입 예외` | 중복된 userId로 가입 시 예외 발생 여부 확인 |
| `허용되지 않은 사용자 가입 예외` | 사전 등록된 사용자 목록에 없는 경우 예외 발생 |
| `비밀번호 암호화 저장 검증` | 회원가입 시 암호화된 비밀번호가 저장되는지 확인 |
2. 로그인

| 테스트 항목 | 설명 |
| --- | --- |
| `로그인 성공` | 올바른 userId, password로 로그인 시 accessToken 반환 검증 |
| `존재하지 않는 사용자 로그인 예외` | 없는 userId로 로그인 시 예외 발생 |
| `잘못된 비밀번호 로그인 예외` | 올바르지 않은 비밀번호 입력 시 예외 발생 |
3. 연말정산 데이터 스크래핑

| 테스트 항목 | 설명 |
| --- | --- |
| `스크래핑 성공` | 정상적인 요청 시 연말정산 데이터가 DB에 저장되는지 확인 |
| `존재하지 않는 사용자 요청 예외` | 존재하지 않는 userId로 요청 시 예외 발생 |
| `외부 API 호출 여부 검증` | 외부 API가 정상적으로 호출되었는지 검증 |
| `DB 저장 확인` | 변환된 ScrapingData가 UserIncome으로 저장되는지 검증 |
4. 결정세액 조회

| `환급액 조회 성공` | 정상 요청 시 환급액이 정확히 계산되는지 검증 |
| --- | --- |
| `존재하지 않는 사용자 요청 예외` | 존재하지 않는 사용자일 경우 예외 발생 |
| `스크래핑 데이터 없는 경우 빈 리스트 반환` | 데이터가 없을 경우 빈 리스트 반환 여부 확인 |
| `산출세액 6% 적용 확인` | 과세표준이 1,400만 원 이하일 경우 6% 적용 검증 |
| `세액공제 반영 확인` | 세액공제 적용 후 올바른 결정세액 반환 검증 |
| `결정세액이 음수일 경우 (-) 값 반환` | 세액공제 후 음수 발생 시 올바른 값 반환 |
5. 세율 구간별 산출세액 계산식 검증(TaxCaculator)

| 테스트 항목 | 설명 |
| --- | --- |
| `과세표준 1,400만 원 이하 (6%)` | 기본 세율 6% 적용 검증 |
| `과세표준 5,000만 원 이하 (15%)` | 세율 15% 및 누진공제 840,000원 적용 검증 |
| `과세표준 8,800만 원 이하 (24%)` | 세율 24% 및 누진공제 6,240,000원 적용 검증 |
| `과세표준 1억 5천만 원 이하 (35%)` | 세율 35% 및 누진공제 15,360,000원 적용 검증 |
| `과세표준 3억 원 이하 (38%)` | 세율 38% 및 누진공제 19,400,000원 적용 검증 |
| `과세표준 5억 원 이하 (40%)` | 세율 40% 및 누진공제 25,400,000원 적용 검증 |
| `과세표준 10억 원 이하 (42%)` | 세율 42% 및 누진공제 37,406,000원 적용 검증 |
| `과세표준 10억 원 초과 (45%)` | 세율 45% 및 누진공제 38,406,000원 적용 검증 |
| `올바른 반올림 적용 여부 검증` | 소수점이 발생하는 경우 반올림이 올바르게 적용되는지 검증 |
| `산출세액 음수 여부 검증` | 과세표준 - 공제액이 음수일 경우 올바른 값 반환 |

## ❗ 추가 고려사항 및 한계점

- 결정세액 계산을 검증하기가 어렵다
- 현재 **결정세액을 계산하는 과정에서 예상 결과값이 명확하지 않아** 테스트 검증이 어려운 상황.
- 과세표준, 산출세액, 세액공제 계산식은 정의되어 있지만, **실제 세법 적용 및 소수점 반올림 등 다양한 요소로 인해 정확한 기대값을 산출하기 어려움**.
- 특히 **세액공제 항목이 여러 개 적용될 경우, 최종 결과를 검증할 정확한 기대값을 명확히 정의해야 함**.