# 소개
블로그

# 기능
- 회원가입/회원정보/회원수정/회원탈퇴 비밀번호 수정 사용자 조회/목록/수정/삭제/검색
- 로그인/로그아웃 OAuth 2.0 로그인(네이버/구글, 발급)
- 포스트 작성/조회/목록/수정/삭제/검색
- 댓글 작성/조회/목록/수정/삭제/답장/검색
- 카테고리 생성/조회/목록/수정/삭제

# 기술
| Tech             | Stack                                     |
|:----------------:|:-----------------------------------------:|
| Language         | Java 17                                   |
| Framework        | Spring Boot, Spring Security, Spring Data |
| Database         | MySQL                                     |
| DevOps           | Docker, Docker Compose                    |
| MISC             | Elasticsearch                             |

# 실행 방법
- Docker 설치 후 docker compose up --build 명령 실행
- OAuth 2.0 로그인 페이지 /oauth2.html

# ERD
![Blog](https://github.com/user-attachments/assets/9e610703-1910-4bef-a1ac-60a923a7cca3)

# API 문서
https://documenter.getpostman.com/view/18098390/2sAY4sj4m2

# 기록
- [jlink로 Spring Boot 애플리케이션 Docker 이미지 크기 줄이기](https://whooa27.blogspot.com/2024/10/jlink-spring-boot-docker.html)
- [Elasticsearch와 데이터 베이스 동기화 및 전체 텍스트 검색 쿼리와 페이네이션](https://whooa27.blogspot.com/2024/10/spring-boot-elasticsearch.html)
- [QueryDSL의 2개 컬렉션 페치 조인과 페이지네이션](https://whooa27.blogspot.com/2024/10/querydsl.html)
- [업로드 파일 Bulk Insert](https://whooa27.blogspot.com/2024/10/jdbctemplate-bulk-insert.html)
