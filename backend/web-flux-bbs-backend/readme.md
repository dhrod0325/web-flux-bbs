# WebFlux 게시판 

## 디렉토리 상세 설명

- config: WebFlux, Spring Security, CORS, 데이터베이스 설정 파일.
- controller: REST API 엔드포인트 정의. 예: PostController.java.
- service: 비즈니스 로직 처리. 예: PostService.java.
- repository: 데이터베이스 액세스 계층. Spring Data JPA 사용.
- domain: JPA 엔터티 클래스. 예: Post, Comment.
- dto: 클라이언트와 데이터 송수신을 위한 DTO 클래스.
- exception: 예외 처리와 커스텀 예외 정의. 예: GlobalExceptionHandler.
- util: 공통으로 사용하는 헬퍼 클래스. 예: 날짜 변환기, 상수.
