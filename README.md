# 🎭 ST 통합예매관리시스템 - Backend

Spring Boot 기반의 공연 예매 시스템 백엔드 API 서버입니다.

## 📋 프로젝트 개요

이 프로젝트는 **Spring Boot** 백엔드와 **React** 프론트엔드로 구성된 통합 예매 관리 시스템의 백엔드 부분입니다.

### 🛠 기술 스택

- **Backend**: Spring Boot 3.x, Spring Security, Spring Data JPA
- **Database**: MySQL 8.0
- **Authentication**: JWT (JSON Web Token)
- **Build Tool**: Maven
- **Documentation**: Swagger/OpenAPI
- **Container**: Docker 지원

### ✨ 주요 기능

- 🔐 **인증 및 권한**: JWT 기반 인증, 역할별 접근 제어 (USER, ADMIN)
- 👥 **사용자 관리**: 회원가입, 로그인, 사용자 정보 관리
- 🎪 **공연 관리**: 공연 CRUD, 검색, 정렬, 페이징
- 🏛 **공연장 관리**: 공연장 및 좌석 배치 관리
- 🪑 **예매 시스템**: 실시간 좌석 예매, 예매 내역 관리
- 📊 **관리자 기능**: 통계, 예매 현황, 사용자 관리

## 🚀 시작하기

### 필수 조건

- Java 17 이상
- Maven 3.6 이상
- MySQL 8.0

### 데이터베이스 설정

```sql
-- MySQL 데이터베이스 생성
CREATE DATABASE st_booking CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 사용자 생성 및 권한 부여 (선택사항)
CREATE USER 'st_user'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON st_booking.* TO 'st_user'@'localhost';
FLUSH PRIVILEGES;
```

### 애플리케이션 설정

`src/main/resources/application.properties` 파일에서 데이터베이스 연결 정보를 확인하세요:

```properties
# 데이터베이스 설정
spring.datasource.url=jdbc:mysql://localhost:3306/st_booking?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=hklim0510!

# 서버 설정
server.port=8080
```

### 실행 방법

```bash
# Maven을 사용한 실행
./mvnw spring-boot:run

# 또는 JAR 파일 빌드 후 실행
./mvnw clean package
java -jar target/st-booking-system-backend-1.0.0.jar
```

### Docker 실행

```bash
# Docker Compose 사용
docker-compose up -d

# 또는 개별 컨테이너 실행
docker build -t st-booking-backend .
docker run -p 8080:8080 st-booking-backend
```

## 🎯 시연용 계정 정보

애플리케이션 시작 시 자동으로 생성되는 테스트 계정:

### 👤 일반 사용자 계정

**사용자 1:**
- 이메일: `user1@test.com`
- 비밀번호: `user123`
- 이름: 김사용자
- 권한: ROLE_USER

**사용자 2:**
- 이메일: `user2@test.com`
- 비밀번호: `user123`
- 이름: 이고객
- 권한: ROLE_USER

### 👨‍💼 관리자 계정

- 이메일: `admin@st-booking.com`
- 비밀번호: `admin123`
- 이름: 관리자
- 권한: ROLE_ADMIN

## 📚 API 문서

서버 실행 후 다음 URL에서 API 문서를 확인할 수 있습니다:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **API Docs**: `http://localhost:8080/api-docs`

## 🛣 주요 API 엔드포인트

### 인증 관련
- `POST /auth/login` - 로그인
- `POST /auth/register` - 회원가입
- `POST /auth/logout` - 로그아웃

### 공연 관리
- `GET /performances` - 공연 목록 조회
- `GET /performances/{id}` - 공연 상세 조회
- `POST /performances` - 공연 등록 (ADMIN)
- `PUT /performances/{id}` - 공연 수정 (ADMIN)
- `DELETE /performances/{id}` - 공연 삭제 (ADMIN)

### 예매 관리
- `GET /bookings` - 예매 목록 조회
- `POST /bookings` - 예매 생성
- `GET /bookings/{id}` - 예매 상세 조회
- `DELETE /bookings/{id}` - 예매 취소

### 관리자 기능
- `GET /admin/users` - 사용자 목록 조회
- `GET /admin/bookings` - 전체 예매 내역 조회
- `GET /admin/statistics` - 예매 통계

## 🏗 프로젝트 구조

```
src/main/java/com/springproject/stbookingsystem/
├── config/             # 설정 클래스
├── controller/         # REST 컨트롤러
├── dto/               # 데이터 전송 객체
├── entity/            # JPA 엔티티
├── repository/        # 데이터 접근 계층
├── service/           # 비즈니스 로직
├── security/          # 보안 설정
└── exception/         # 예외 처리
```

## 🗃 데이터베이스 스키마

주요 테이블:
- `users` - 사용자 정보
- `performances` - 공연 정보
- `venues` - 공연장 정보
- `seat_layouts` - 좌석 배치
- `seats` - 좌석 정보
- `bookings` - 예매 정보

## 🔒 보안 설정

- **JWT 토큰**: 상태 비저장(stateless) 인증
- **비밀번호 암호화**: BCrypt 해싱
- **CORS 설정**: 프론트엔드 도메인 허용
- **Role 기반 접근 제어**: USER, ADMIN 권한

## 📊 초기 데이터

애플리케이션 시작 시 `DataLoader`를 통해 다음 데이터가 자동 생성됩니다:

- 테스트 사용자 계정 (일반 사용자 2명, 관리자 1명)
- 샘플 공연장 3곳 (세종문화회관, 블루스퀘어, 예술의전당)
- 샘플 공연 3개 (클래식, 뮤지컬, 발레)
- 각 공연장의 좌석 배치 및 공연별 좌석 정보

## 🧪 테스트

```bash
# 단위 테스트 실행
./mvnw test

# 통합 테스트 실행
./mvnw verify
```

## 📝 로깅

로그 레벨 설정:
- `com.springproject.stbookingsystem`: INFO
- `org.springframework.security`: WARN
- `org.hibernate.SQL`: DEBUG

## 🌍 환경별 설정

- **개발 환경**: `application.properties`
- **운영 환경**: `application-prod.properties`

```bash
# 운영 환경으로 실행
java -jar -Dspring.profiles.active=prod target/app.jar
```

## 🔧 트러블슈팅

### 일반적인 문제들

1. **데이터베이스 연결 오류**
   - MySQL 서버 상태 확인
   - 연결 정보 및 권한 확인

2. **포트 충돌**
   - 8080 포트 사용 중인 프로세스 확인
   - application.properties에서 포트 변경

3. **JWT 토큰 오류**
   - 토큰 만료 시간 확인
   - 시크릿 키 설정 확인

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스를 따릅니다.

---

## 🔗 관련 프로젝트

- **프론트엔드**: `st-booking-system-front` (React)

## 📞 지원

문제가 발생하거나 질문이 있으시면 이슈를 생성해주세요.
