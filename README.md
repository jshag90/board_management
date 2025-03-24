[알서포트] 백엔드 개발자 과제전형

**핵심 문제해결 전략 및 실행 방법**

**핵심 문제해결**
1) OCP 관련
- 요구사항은 공지사항이었지만 다른 종류의 게시판이 등록될 가능성을 고려해 확장가능하게 설계하였습니다.(**전략패턴**)
- 이를 위해서 게시판 게시글, 첨부파일 엔티티를 분리하고 이름 매핑해주는 엔티티를 생성하였습니다.
- BoardType이라는 엔티티, enum클래스를 활용해서 client가 url에 notice로 요청(@PathVariable("type"))하여 게시판에 대한 CRUD가 되게 처리하였습니다.

2) 대용량 트래픽 관련
- 페이징 성능 향상을 위해서 db의 **클러스터 key**를 활용한 커버링 인덱스 개념을 적용해서 페이지 조회 속도를 향상 시켰습니다.
- 대용량 트래픽을 위해서 **local cache(caffeine)**를 적용하고 첫 조회시 caching을 하고 게시물이 수정/삭제될 경우 CacheEvict를 합니다.

3) 리팩터링 관련 
- 목록 조회 api에서 where절에 가독성을 높이기 위해서 **정적 팩터리 메서드** 활용해서 리팩터링하였습니다.
- api의 요청 파라미터 유효성 검증을 위해서 **jakarta.validation**에서 제공하는 어노테이션을 활용하였습니다.
- CustomException, AdviceController를 활용해서 로직 예외 처리를 하였습니다.

4) 테스트 관련
- **jUnit**을 활용하여 Controller Layer에 대한 테스트 코드를 작성하였습니다.

Note) 로그인 기능은 추가 개발이 필요하므로 임시로 Member 테이블을 만들어 서버기동시 관리자 계정을 추가하고 
공지사항 등록시 해당 계정 이름으로 등록되게 처리하였습니다.(writer)

**실행 방법**
1) MariaDB에 db_board_management이름으로 데이터베이스를 생성합니다.

2) db접속 정보 설정파일을 c:/rs/install.properties에 위치시킵니다.
   (리눅스 : /rs/install.properties)

3) 내장 톰캣 jar파일을 실행합니다.
   - profiles별로 설정파일 경로가 다릅니다.
리눅스 일 경우)
```aiignore
 java -Dspring.profiles.active=dev -jar board-0.0.1-SNAPSHOT.jar
```

windows일 경우)
```aiignore
 java -Dspring.profiles.active=prod -jar board-0.0.1-SNAPSHOT.jar
```

4) 스웨거 API 문서 웹 화면에 접속합니다.
   - 스웨거 접속 주소 : http://localhost:8080/swagger-ui/index.html