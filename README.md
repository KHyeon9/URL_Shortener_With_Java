# URL_Shortener_With_Java
## 설명
단축 URL 생성하는 사이트

## 개발환경
* Intellij IDEA Ultimate 2023.2.2
* Java 21
* Gradle 8.14
* Spring Boot 3.5.0

## 기술 스택
* Spring Boot
* Spring Web
* PostgreSQL
* Spring Data JPA
  * 생산성을 높일 수 있고 유지 보수하기 편하기 때문에 채용
* Spring Security
  * 로그인 유무나 Http Method, Role에 따라서 쉽게 차단과 같은 보안 처리가 가능
* Lombok
  * 중복되는 코드들을 줄이기 위해서 사용
* Redis
  * URL 생성에 User를 조회할 때, 사용하므로 중복되는 인증을 줄이기 위해 사용
* Flyway
  * DB를 버전으로 관리하기 위해서 사용
* Thymeleaf
  * View에 데이터를 쉽게 사용하기 위해 사용
* Docker
  * 개발 환경을 동일하게 유지하기 위해 사용

## 그외
* Bootstrap
