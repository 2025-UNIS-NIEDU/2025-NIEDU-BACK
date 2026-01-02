# NIEdu 📚
뉴스 기반 개인 맞춤형 문해력·시사 상식 학습 서비스

---

## 📌 프로젝트 소개
NIEdu는 뉴스 콘텐츠를 활용하여 사용자의 **문해력**과 **시사 상식**을 확장하는 교육 서비스입니다.  
AI를 활용한 개인화 학습과 게이미피케이션 요소를 통해, 뉴스 소비를 학습 루틴으로 만들 수 있도록 돕습니다.

---

## 🛠 Tech Stack
- **Language**: Java 21
- **Framework**: Spring Boot 3.x
- **Database**: PostgreSQL
- **Infrastructure**: AWS
- **DevOps / CI-CD**: Github Actions

---
## Repository Structure
```
2025-NIEDU-BACK/
├── .github/                    # GitHub 협업 및 배포 자동화 설정
│   ├── ISSUE_TEMPLATE/          # 이슈 템플릿 (bug / feature / chore)
│   ├── pull_request_template.md
│   └── workflows/
│       └── deploy.yml           # GitHub Actions 기반 배포 파이프라인
│
├── Dockerfile                   # Spring Boot 애플리케이션 Docker 빌드
├── build.gradle                 # Gradle 빌드 및 의존성 관리
├── settings.gradle
├── README.md
│
├── src/
    ├── main/
    │   ├── java/com/niedu/
    │   │   ├── NieduApplication.java
    │   │   │
    │   │   ├── config/          # 전역 설정 (Security, Swagger 등)
    │   │   ├── security/        # JWT / OAuth2 인증·인가 로직
    │   │   │   ├── jwt/
    │   │   │   └── oauth/
    │   │   │
    │   │   ├── controller/      # API 엔드포인트 계층
    │   │   │   ├── auth/         # 로그인, 토큰 발급
    │   │   │   ├── edu/          # 학습(코스/세션/스텝) API
    │   │   │   ├── home/         # 홈 화면 데이터
    │   │   │   ├── my/           # 마이페이지
    │   │   │   └── search/       # 검색 및 히스토리
    │   │   │
    │   │   ├── service/         # 비즈니스 로직
    │   │   │   ├── edu/          # 학습 도메인 핵심 로직 + AI 연동
    │   │   │   ├── auth/
    │   │   │   ├── home/
    │   │   │   └── user/
    │   │   │
    │   │   ├── entity/          # JPA 엔티티 (도메인 모델)
    │   │   │   ├── course/       # 코스 / 세션 / 스텝
    │   │   │   ├── content/      # 학습 콘텐츠(퀴즈, 읽기 자료 등)
    │   │   │   ├── topic/        # Topic / SubTopic / 사용자 선호
    │   │   │   └── user/         # 사용자, 출석, 구독
    │   │   │
    │   │   ├── repository/      # JPA Repository 계층
    │   │   ├── dto/             # API 요청/응답 DTO
    │   │   ├── global/          # 공통 응답, 예외 처리
    │   │   └── scheduler/       # 주기적 AI 데이터 처리
    │   │
    │   └── resources/
    │       ├── application.properties
    │       └── application-prod.properties
    │
    └── test/                    # 테스트 코드
```
