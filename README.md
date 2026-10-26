# MyBookShelf Backend

2025 캡스톤디자인 프로젝트 **MyBookShelf**의 백엔드(Spring Boot) 저장소입니다.
프론트엔드는 [mybookshelf-frontend](https://github.com/TUK-CE-capstone-2025-MyBookShelf/mybookshelf-frontend)에서 관리합니다.

책장을 만들어 읽은 책을 모으고, 책장에 담긴 책을 바탕으로 다음에 읽을 책을 추천받는 서비스입니다.

> 이 프로젝트는 하나의 저장소에서 프론트엔드와 백엔드를 함께 관리하다가 두 저장소로 분리했습니다.
> 분리 전 전체 이력은 [원본 저장소](https://github.com/Thedume/TUK-CE-capstone-2025-MyBookShelf)에 보관되어 있습니다.

## 주요 기능

- 회원: 이메일 인증 회원가입, 로그인/로그아웃, 내 정보
- 도서 검색과 상세 정보 (알라딘 오픈API)
- 책장: 생성/수정/삭제, 책 등록/삭제, 카테고리별 조회
- 도서 추천: 책장에 담긴 책을 바탕으로 장르 유사도, 선호 장르, 가중 평점, 대출, 베스트셀러 점수를 합산해 정렬
- 커뮤니티: 게시글, 댓글, 좋아요
- 독후감과 리뷰 API

## 기술 스택

- Java 17, Spring Boot 3.4.2
- Spring Data JPA (Hibernate), Spring Security, Spring Mail
- MySQL 8.0 (Docker Compose로 실행)
- Gradle

## 외부 서비스

| 서비스 | 용도 | 필요한 값 |
|---|---|---|
| 알라딘 오픈API | 도서 검색, 상세, 평점, 베스트셀러 | TTBKey |
| 도서관 정보나루 | 대출 정보, 인기 도서 | 인증키(authKey) |
| Gmail SMTP | 회원가입 이메일 인증번호 발송 | Gmail 계정과 앱 비밀번호 |

## 실행 방법

### 준비물

- JDK 17
- Docker Desktop (Docker Compose)

### 1. 환경변수 파일 만들기

```bash
cp .env.example .env
```

`.env`를 열어 값을 채웁니다. 이 파일은 `.gitignore`에 포함되어 있어 커밋되지 않습니다.

| 변수 | 설명 | 비고 |
|---|---|---|
| `DB_NAME` | 데이터베이스 이름 | 필수 |
| `DB_USERNAME` | DB 계정 | 필수 |
| `DB_PASSWORD` | DB 비밀번호 | 필수 |
| `DB_ROOT_PASSWORD` | MySQL root 비밀번호 | Docker Compose에서만 사용 |
| `DB_HOST` | DB 주소 | 기본값 `localhost` |
| `DB_PORT` | DB 포트 | 기본값 `3307` |
| `MAIL_USERNAME` | 인증 메일을 보낼 Gmail 주소 | 비우면 인증 메일 발송 실패 |
| `MAIL_PASSWORD` | Gmail 앱 비밀번호 | 공백 없이 입력 |
| `LIBRARY_API_KEY` | 도서관 정보나루 인증키 | 비우면 대출 점수 계산 불가 |
| `ALADIN_TTB_KEY` | 알라딘 TTBKey | 비우면 도서 검색 불가 |

값에는 `;`, `$`, `#`, 따옴표를 넣지 마세요. Gmail 앱 비밀번호는 Google 계정에서 2단계 인증을 켠 뒤 발급합니다.

### 2. DB 실행

```bash
docker compose up -d
docker compose ps
```

상태가 `healthy`가 되면 준비된 것입니다. MySQL은 로컬 `3307` 포트(컨테이너 내부 3306)로 열립니다.
테이블은 서버를 처음 실행할 때 자동으로 생성됩니다(`ddl-auto=update`).

### 3. 서버 실행

Spring 앱은 `.env`를 자동으로 읽지 않습니다. 같은 터미널에서 환경변수로 불러온 뒤 실행하세요.

```bash
sed -i 's/\r$//' .env        # Windows에서 받은 파일의 줄바꿈 변환
set -a; source .env; set +a
./gradlew bootRun
```

로그에 `Started BeMyBookShelfApplication`이 나오면 성공입니다. 서버는 `http://localhost:8081`에서 실행됩니다.
IDE에서 실행한다면 실행 구성의 Environment variables에 같은 이름의 변수를 넣으면 됩니다.

DB 데이터까지 지우고 처음부터 시작하려면 `docker compose down -v`를 사용합니다.

## API 구성

| 경로 | 설명 |
|---|---|
| `/api/user` | 회원가입, 로그인/로그아웃, 내 정보, 이메일 중복 확인 |
| `/api/email` | 인증번호 발송과 확인 |
| `/api/books` | 도서 검색 (알라딘, 도서관 정보나루) |
| `/api/book/{isbn}` | 도서 상세 |
| `/api/bookshelf` | 책장과 책 관리 |
| `/api/recommend` | 도서 추천 |
| `/api/posts` | 커뮤니티 게시글, 댓글, 좋아요 |
| `/api/reports`, `/api/review` | 독후감과 리뷰 |

## 알려진 제한 사항

- **GPT 기능은 꺼져 있습니다.** 원래 키워드 보완과 키워드 유사도 점수에 OpenAI API를 사용했으나 `openai.enabled=false`가 기본값이라 추천 결과의 키워드 점수는 항상 0입니다. 다시 켜려면 `openai.enabled=true`, API 키, 현재 사용 가능한 모델명으로의 교체가 필요합니다(코드의 `gpt-3.5-turbo`, `gpt-4-turbo`는 종료되었습니다).
- 이메일 인증 여부를 서버에서 확인하는 코드가 주석 처리되어 있어, 현재는 프론트엔드의 검사에만 의존합니다. 외부에 공개하기 전에 활성화해야 합니다.
- CORS가 `http://localhost:8080`만 허용하도록 설정되어 있습니다. 프론트엔드를 다른 주소에 배포하려면 `WebConfig.java`를 수정해야 합니다.
- `/api/books/random`은 구현되어 있지 않습니다(프론트엔드 홈 화면이 호출하지만 화면에는 사용하지 않습니다).
- 추천은 외부 API를 순차 호출해서 응답에 1분 정도 걸릴 수 있습니다.
- 소설 카테고리가 도서관 분류(KDC) `80`(문학 전체)으로 매핑되어 있어, 인기 도서 후보에 소설이 아닌 문학 도서가 섞일 수 있습니다.