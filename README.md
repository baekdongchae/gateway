# gateway
1. API 로깅 흐름
   목적: 사용자의 API 호출 정보를 MongoDB api_request 컬렉션에 저장
   주요 위치: RequestIdInterceptor, ApiLogContext, @ControllerAdvice, AOP 응답 로깅

📌 흐름 요약

[1] Client → [2] Interceptor → [3] ApiLogContext 저장 → [4] Controller → Service
↓
[5] 응답 시점에 ApiLogContext + 응답정보 로그로 저장 (MongoDB)

📦 상세 흐름
1. 사용자 요청 (Postman 등)

X-Request-ID, X-USER-CODE, Authorization, X-Device-* 등 커스텀 헤더 포함 가능

2. RequestIdInterceptor.preHandle() 실행

UUID 생성 or 클라이언트 제공 ID 사용

헤더/요청바디 파싱 → ApilogData 객체 생성

ApiLogContext.init(logData)로 ThreadLocal 저장

3. 요청이 Controller → Service로 진입

4. 응답 시점에 AOP 또는 ResponseBodyAdvice 등으로 응답 바디 추출

5. MongoDB api_request 컬렉션에 저장

요청 시점의 정보 + 응답 바디가 하나의 문서로 기록됨

2. DB 변경 로깅 흐름 (UPDATE 중심)

목적: 사용자에 의한 데이터베이스 변경 내용을 추적
대상: @Transactional + update*() 메서드
저장소: MongoDB db_change 컬렉션

📌 흐름 요약

[1] Controller → [2] Service의 update 메서드 → [3] AOP (DbChangeLoggingAspect)
↓
[4] update 전 기존 데이터 조회 → joinPoint.proceed() → 변경 후 데이터 조회
↓
[5] 변경 내용 diff 형태로 MongoDB 저장

📦 상세 흐름
1. Service의 update 메서드에 진입

예: UserService.updateUser(Long id, ...)

2. AOP (DbChangeLoggingAspect)가 감지

@Transactional + 메서드명에 update 포함되어야 작동

3. 변경 전 데이터 조회

예: userRepository.findById(id) → previousData

4. 실제 update 수행 → joinPoint.proceed()

5. 변경 후 데이터 조회

다시 userRepository.findById(id) → changedData

6. MongoDB db_change 컬렉션에 저장



| 항목 | API 로깅 (`api_request`)         | DB 변경 로깅 (`db_change`) |
| -- | ------------------------------ | ---------------------- |
| 시점 | 모든 요청/응답 시                     | DB update 등 데이터 변경 시   |
| 수단 | Interceptor + Context + 응답 AOP | AOP + JPA 데이터 조회       |
| 목적 | API 호출 전체 이력                   | 실제 DB 변경 추적            |
| 필드 | 사용자, 디바이스, 요청, 응답 등            | 테이블명, 변경 전/후 데이터, 사용자  |
| 예시 | 로그인, 목록 조회 등                   | 비밀번호 변경, 회원정보 수정 등     |