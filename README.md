# Spring Boot를 활용한 게시판의 제작
### 목차
1. [프로젝트 이름](#1-프로젝트-이름)
2. [기술 스택](#2-기술-스택)
3. [프로젝트 구조](#3-프로젝트-구조)
4. [MySQL 연동](#4-mysql-연동)
5. [게시글 CRUD 처리](#5-게시글-crud-처리)
6. [게시글 등록(수정) 구현](#6-게시글-등록수정-구현)
7. [게시글 리스트 구현](#7-게시글-리스트-구현)
8. [게시글 조회 구현](#8-게시글-조회-구현)
9. [게시글 삭제 구현](#9-게시글-삭제-구현)
10. [경고 메시지 처리](#10-경고-메시지-처리)
11. [인터셉터 적용](#11-인터셉터-적용)
12. [AOP 적용](#12-aop-적용)
13. [트랜잭션 적용](#13-트랜잭션-적용)
14. [페이징 처리](#14-페이징-처리)

---
### 1. 프로젝트 이름
* **게시글 등록, 조회, 수정, 삭제가 가능한 게시판**

---
### 2. 기술 스택
* IDE
```
- IntelliJ
```
* Framework
```
- Spring Boot
- MyBatis
```
* Library
```
- Lombok
```
* Template Engine
```
- Thymeleaf
```
* Database
```
- MySQL
```

---
### 3. 프로젝트 구조
<img src="https://user-images.githubusercontent.com/61148914/124562382-ecc9f680-de79-11eb-8863-e4560bec3570.JPG" width="35%">
</br>

**1) src/main/java 디렉터리**   
ㆍ 클래스, 인터페이스 등 자바 파일이 위치하는 디렉터리   
</br>

**2) BoardApplication 클래스**   
ㆍ 해당 클래스 내의 main 메서드는 SpringApplication.run 메서드를 호출해서 웹 애플리케이션을 실행하는 역할을 함   
ㆍ "@SpringBootApplication"은 다음 3가지 애너테이션으로 구성   
|구성 요소|설명|
|---|---|
|@EnableAutoConfiguration|다양한 설정들의 일부가 자동으로 완료됨|
|@ComponentScan|자동으로 컴포넌트 클래스를 검색하고 스프링 애플리케이션 콘텍스트에 빈으로 등록함|
|@Configuration|해당 애너테이션이 선언된 클래스는 자바 기반의 설정 파일로 인식함|
</br>

**3) src/main/resources 디렉터리**   
|구성 요소|설명|
|---|---|
|templates|템플릿 엔진을 활용한 동적 리소스 파일이 위치|
|static|css, fonts, images, plugin, scripts와 같은 정적 리소스 파일이 위치|
|application.properties|WAS의 설정이나, 데이터베이스 관련 설정 등을 지정해서 처리 가능|
</br>

**4) src/test/java 디렉터리**   
ㆍ BoardApplicationTest 클래스를 이용해서 개발 단계에 따라 테스트를 진행할 수 있음   
</br>

**5) build.gradle 파일**   
ㆍ 빌드에 사용이 될 애플리케이션의 버전, 각종 라이브러리 등 다양한 항목을 설정하고 관리할 수 있는 파일   
</br>

---
### 4. MySQL 연동
**1) 데이터 소스 설정**   
ㆍ 스프링 부트에서 데이터 소스 설정 방법은 두 가지가 존재   
ㆍ "@Bean" 애너테이션 또는 "application.properties" 파일 이용 가능 (이번 프로젝트에서는 후자의 방법을 사용)   
ㆍ src/main/resources 디렉터리의 application.properties 파일에 아래 코드를 입력
<details>
    <summary><b>코드 보기</b></summary>

```
spring.datasource.hikari.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.hikari.jdbc-url=jdbc:mysql://localhost:3306/board?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&useSSL=false
spring.datasource.hikari.username=username
spring.datasource.hikari.password=password
spring.datasource.hikari.connection-test-query=SELECT NOW() FROM dual
```
</details>

|구성 요소|설명|
|---|---|
|jdbc-url|데이터베이스의 주소를 의미하며, 포트 번호뒤의 board는 생성한 스키마의 이름|
|username|MySQL의 아이디|
|password|MySQL의 패스워드|
|connection-test-query|데이터베이스와의 연결이 정상적으로 이루어졌는지 확인하기 위한 SQL 쿼리문|
</br>

**2) 데이터베이스 설정**   
ㆍ configuration 패키지 내 DBConfiguration 클래스를 통해 데이터베이스 설정 완료
<details>
    <summary><b>코드 보기</b></summary>
	
```java
@Configuration
@PropertySource("classpath:/application.properties")
public class DBConfiguration {

	@Autowired
	private ApplicationContext applicationContext;

	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.hikari")
	public HikariConfig hikariConfig() {
		return new HikariConfig();
	}

	@Bean
	public DataSource dataSource() {
		return new HikariDataSource(hikariConfig());
	}

	@Bean
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(dataSource());
    		factoryBean.setMapperLocations(applicationContext.getResources("classpath:/mappers/**/*Mapper.xml"));
		return factoryBean.getObject();
	}

	@Bean
	public SqlSessionTemplate sqlSession() throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory());
	}
}
```
</details>

|애너테이션 및 메서드|설명|
|---|---|
|@Configuration|해당 애너테이션이 지정된 클래스를 자바 기반의 설정 파일로 인식|
|@PropertySource|해당 클래스에서 참조할 properties 파일의 위치를 지정|
|@Autowired|빈으로 등록된 객체를 클래스에 주입하는 데 사용|
|ApplicationContext|스프링 컨테이너 중 하나로써 빈의 생성과 사용, 관계, 생명 주기 등을 관리|
|@Bean|해당 애너테이션으로 지정된 객체는 컨테이너에 의해 관리되는 빈으로 등록됨|
|@ConfigurationProperties|@PropertySource에 지정된 파일에서 prefix에 해당하는 설정을 읽어들여 메서드에 매핑함|
|hikariConfig( )|커넥션 풀 라이브러리 중 하나인 히카리CP 객체를 생성하는 메서드|
|dataSource( )|커넥션 풀을 지원하기 위한 인터페이스인 데이터 소스 객체를 생성하는 메서드|
|sqlSessionFactory( )|데이터베이스 커넥션과 SQL 실행에 대한 중요한 역할을 하는 SqlSessionFactory 객체를 생성하는 메서드|
|setMapperLocations( )|getResources 메서드의 인자로 지정된 패턴에 포함하는 XML Mapper를 인식하는 메서드|
|sqlSession( )|마이바티스와 스프링 연동 모듈의 핵심인 sqlSessionTemplate 객체를 생성하는 메서드|
</br>

---
### 5. 게시글 CRUD 처리
**1) 게시판 테이블 생성**   
ㆍ 게시판 테이블은 데이터베이스에 저장될 게시글에 대한 정보를 정의한 것   
ㆍ MySQL Workbench을 실행하고 스키마를 생성한 후 아래에 스크립트를 실행
<details>
    <summary><b>코드 보기</b></summary>
	
```sql
CREATE TABLE tb_board (
    idx INT NOT NULL AUTO_INCREMENT COMMENT '번호 (PK)',
    title VARCHAR(100) NOT NULL COMMENT '제목',
    content VARCHAR(3000) NOT NULL COMMENT '내용',
    writer VARCHAR(20) NOT NULL COMMENT '작성자',
    view_cnt INT NOT NULL DEFAULT 0 COMMENT '조회 수',
    notice_yn ENUM('Y', 'N') NOT NULL DEFAULT 'N' COMMENT '공지글 여부',
    secret_yn ENUM('Y', 'N') NOT NULL DEFAULT 'N' COMMENT '비밀글 여부',
    delete_yn ENUM('Y', 'N') NOT NULL DEFAULT 'N' COMMENT '삭제 여부',
    insert_time DATETIME NOT NULL DEFAULT NOW() COMMENT '등록일',
    update_time DATETIME NULL COMMENT '수정일',
    delete_time DATETIME NULL COMMENT '삭제일',
    PRIMARY KEY (idx)
)  COMMENT '게시판';
```
</details>
</br>

**2) 도메인 클래스 생성**   
ㆍ 도메인 클래스는 위에서 생성한 게시판 테이블에 대한 구조화 역할을 하는 클래스   
ㆍ 보통 도메인 클래스는 읽기 전용을 의미하는 xxxVO와 데이터의 저장 및 전송을 의미하는 xxxDTO로 이름을 지음   
ㆍ domain 패키지에 BoardDTO 클래스를 추가하고 아래에 코드를 작성
<details>
    <summary><b>코드 보기</b></summary>
	
```java
@Getter
@Setter
public class BoardDTO {

	private Long idx;

	private String title;

	private String content;

	private String writer;

	private int viewCnt;

	private String noticeYn;

	private String secretYn;

	private String deleteYn;

	private LocalDateTime insertTime;

	private LocalDateTime updateTime;

	private LocalDateTime deleteTime;
}
```
</details>
</br>

**3) Mapper 인터페이스 생성**      
ㆍ Mapper 인터페이스는 데이터베이스와 통신 역할을 함   
ㆍ mapper 패키지에 BoardMapper 인터페이스를 생성하고 아래 코드를 작성
<details>
    <summary><b>코드 보기</b></summary>
	
```
@Mapper
public interface BoardMapper {

	public int insertBoard(BoardDTO params);

	public BoardDTO selectBoardDetail(Long idx);

	public int updateBoard(BoardDTO params);

	public int deleteBoard(Long idx);

	public List<BoardDTO> selectBoardList();

	public int selectBoardTotalCount();
}
```
</details>
	
|구성 요소|설명|
|---|---|
|@Mapper|마이바티스는 인터페이스에 @Mapper만 지정을 해주면 XML Mapper에서 메서드의 이름과 일치하는 SQL 문을 찾아 실행해 줌|
|insertBoard( )|게시글을 생성하는 INSERT 쿼리를 호출하는 메서드|
|selectBoardDetail( )|하나의 게시글을 조회하는 SELECT 쿼리를 호출하는 메서드|
|updateBoard( )|게시글을 수정하는 UPDATE 쿼리를 호출하는 메서드|
|deleteBoard( )|UPDATE 쿼리를 호출하여, delete_yn 컬럼의 상태를 'Y'로 지정하는 메서드|
|selectBoardList( )|게시글 목록을 조회하는 SELECT 쿼리를 호출하는 메서드|
|selectBoardTotalCount( )|삭제 여부가 'N'으로 지정된 게시글의 개수를 조회하는 SELECT 쿼리를 호출하는 메서드|
</br>

**4) 마이바티스 XML Mapper 생성**   
ㆍ XML Mapper는 BoardMapper 인터페이스와 SQL문의 연결을 위한 역할을 하며, 실제 SQL 쿼리 문이 정의됨     
ㆍ src/main/resources 디렉터리에 mappers 폴더 생성 후 BoardMapper.xml 파일을 추가   
ㆍ BoardMapper.xml 파일에 아래에 소스코드를 작성
<details>
    <summary><b>코드 보기</b></summary>
	
```sql
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.Board.mapper.BoardMapper">

	<sql id="boardColumns">
		  idx
		, title
		, content
		, writer
		, view_cnt
		, notice_yn
		, secret_yn
		, delete_yn
		, insert_time
		, update_time
		, delete_time
	</sql>

	<insert id="insertBoard" parameterType="BoardDTO">
		INSERT INTO tb_board (
			<include refid="boardColumns" />
		) VALUES (
			  #{idx}
			, #{title}
			, #{content}
			, #{writer}
			, 0
			, IFNULL(#{noticeYn}, 'N')
			, IFNULL(#{secretYn}, 'N')
			, 'N'
			, NOW()
			, NULL
			, NULL
		)
	</insert>

	<select id="selectBoardDetail" parameterType="long" resultType="BoardDTO">
		SELECT
			<include refid="boardColumns" />
		FROM
			tb_board
		WHERE
			delete_yn = 'N'
		AND
			idx = #{idx}
	</select>

	<update id="updateBoard" parameterType="BoardDTO">
		UPDATE tb_board
		SET
			  update_time = NOW()
			, title = #{title}
			, content = #{content}
			, writer = #{writer}
			, notice_yn = IFNULL(#{noticeYn}, 'N')
			, secret_yn = IFNULL(#{secretYn}, 'N')
		WHERE
			idx = #{idx}
	</update>

	<update id="deleteBoard" parameterType="long">
		UPDATE tb_board
		SET
			  delete_yn = 'Y'
			, delete_time = NOW()
		WHERE
			idx = #{idx}
	</update>

	<select id="selectBoardList" parameterType="BoardDTO" resultType="BoardDTO">
		SELECT
			<include refid="boardColumns" />
		FROM
			tb_board
		WHERE
			delete_yn = 'N'
		ORDER BY
			notice_yn ASC,
			idx DESC,
			insert_time DESC
	</select>

	<select id="selectBoardTotalCount" parameterType="BoardDTO" resultType="int">
		SELECT
			COUNT(*)
		FROM
			tb_board
		WHERE
			delete_yn = 'N'
	</select>

</mapper>
```
</details>

|구성 요소|설명|
|---|---|
|&lt;mapper&gt;|해당 태그 namespace 속성에는 SQL 쿼리문과 매핑을 위한 BoardMapper 인터페이스의 경로가 지정|
|&lt;sql&gt;|공통으로 사용되거나 반복적으로 사용되는 테이블의 컬럼을 SQL 조각으로 정의하여 boardColumns라는 이름으로 사용|
|&lt;include&gt;|<sql> 태그에 정의한 boardColumns의 참조를 위해 사용되는 태그|
|parameterType|쿼리문 실행에 필요한 파라미터의 타입을 해당 속성에 지정|
|resultType|쿼리문의 실행 결과에 해당하는 타입을 지정|
|파라미터 표현식|전달받은 파라미터는 #{} 표현식을 사용해서 처리|
</br>

**5) 마이바티스 SELECT 컬럼과 DTO 멤버 변수의 매핑**   
ㆍ BoardMapper.xml의 boardColumns SQL 조각은 스네이크 케이스를 사용하고 있고, BoardDTO 클래스의 멤버 변수는 카멜 케이스를 사용   
ㆍ 서로 다른 표현식의 사용은 추가 설정을 통해 자동으로 매칭이 되도록 처리가 가능   
ㆍ application.properties 파일 하단에 아래 설정을 추가
<details>
    <summary><b>코드 보기</b></summary>
	
```
mybatis.configuration.map-underscore-to-camel-case=true
```
</details>
</br>

**6) DBConfiguration 클래스 처리**   
ㆍ application.properies 파일에 마이바티스 설정을 추가하였으니, 해당 설정을 처리할 빈을 정의해야 함   
ㆍ DBConfiguration 클래스에 아래 코드를 추가
<details>
    <summary><b>코드 보기</b></summary>
	
```java
@Configuration
@PropertySource("classpath:/application.properties")
public class DBConfiguration {

	@Autowired
	private ApplicationContext applicationContext;

	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.hikari")
	public HikariConfig hikariConfig() {
		return new HikariConfig();
	}

	@Bean
	public DataSource dataSource() {
		return new HikariDataSource(hikariConfig());
	}

	@Bean
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(dataSource());
		factoryBean.setMapperLocations(applicationContext.getResources("classpath:/mappers/**/*Mapper.xml"));
		factoryBean.setTypeAliasesPackage("com.Board.domain");
		factoryBean.setConfiguration(mybatisConfg());
		return factoryBean.getObject();
	}

	@Bean
	public SqlSessionTemplate sqlSession() throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory());
	}

	@Bean
	@ConfigurationProperties(prefix = "mybatis.configuration")
	public org.apache.ibatis.session.Configuration mybatisConfg() {
		return new org.apache.ibatis.session.Configuration();
	}
}
```
</details>

|구성 요소|설명|
|---|---|
|setTypeAliasesPackage( )|BoardMapper XML의 parameterType과 resultType은 클래스의 풀 패키지 경로가 포함이 되어야함, 해당 메서드를 사용함으로써 풀 패키지 경로 생략 가능|
|setConfiguration( )|마이바티스 설정과 관련된 빈을 설정 파일로 지정하는 메서드|
|mybatisConfig( )|application.properties 파일에서 mybatis.configuration으로 시작하는 모든 설정을 읽어 들여 빈으로 등록하는 메서드|
</br>

---
### 6. 게시글 등록(수정) 구현
**1) Service 영역**   
ㆍ Service 영역은 비즈니스 로직을 담당   
ㆍ service 패키지에 BoardService 인터페이스를 생성하고 아래 코드를 작성   
<details>
	<summary><b>코드 보기</b></summary>
	
```java
public interface BoardService {

	public boolean registerBoard(BoardDTO params);

	public BoardDTO getBoardDetail(Long idx);

	public boolean deleteBoard(Long idx);

	public List<BoardDTO> getBoardList();
}
```
</details>
	
ㆍ service 패키지에 BoardServiceImpl 클래스 생성   
ㆍ BoardServiceImpl 클래스는 BoardService 인터페이스의 구현 클래스 역할을 함   
ㆍ BoardServiceImpl 클래스에 아래 코드를 작성   
<details>
	<summary><b>코드 보기</b></summary>
	
```java
@Service
public class BoardServiceImpl implements BoardService {

	@Autowired
	private BoardMapper boardMapper;

	@Override
	public boolean registerBoard(BoardDTO params) {
		int queryResult = 0;

		if (params.getIdx() == null) {
			queryResult = boardMapper.insertBoard(params);
		} else {
			queryResult = boardMapper.updateBoard(params);
		}

		return (queryResult == 1) ? true : false;
	}

	@Override
	public BoardDTO getBoardDetail(Long idx) {
		return boardMapper.selectBoardDetail(idx);
	}

	@Override
	public boolean deleteBoard(Long idx) {
		int queryResult = 0;

		BoardDTO board = boardMapper.selectBoardDetail(idx);

		if (board != null && "N".equals(board.getDeleteYn())) {
			queryResult = boardMapper.deleteBoard(idx);
		}

		return (queryResult == 1) ? true : false;
	}

	@Override
	public List<BoardDTO> getBoardList() {
		List<BoardDTO> boardList = Collections.emptyList();

		int boardTotalCount = boardMapper.selectBoardTotalCount();

		if (boardTotalCount > 0) {
			boardList = boardMapper.selectBoardList();
		}

		return boardList;
	}
}
```
</details>
	
|구성 요소|설명|
|---|---|
|@Service|해당 클래스가 비즈니스 로직을 담당하는 서비스 클래스임을 지정|
|registerBoard( )|1. params의 idx가 null이라면, insertBoard 메서드가 실행 </br>2. params의 idx가 null이 아니라면, updateBoard 메서드가 실행</br>3. queryResult 변수에는 쿼리를 실행한 횟수 1이 저장</br>4. 쿼리의 실행 결과를 판단해 true 또는 false를 반환|
|getBoardDetail( )|하나의 게시글을 조회하는 selectBoardDetail 메서드의 결과값을 반환|
|deleteBoard( )|1. 파라미터로 입력받은 idx에 해당하는 게시물을 조회</br>2. 해당 게시물이 null이 아니거나, 이미 삭제된 게시물이 아니라면 deleteBoard 메서드 실행</br>3. queryResult 변수에는 쿼리를 실행한 횟수 1이 저장</br>4. 쿼리의 실행 결과를 판단해 true 또는 false를 반환|
|getBoardList( )|1. 비어있는 리스트를 선언</br>2. 삭제되지 않은 게시글들을 비어있는 리스트에 삽입</br>3. 해당 리스트를 반환|
</br>

**2) Controller 영역**   
ㆍ Controller 영역은 Model 영역과 View 영역을 연결해주고, 사용자의 요청과 응답을 처리해 줌   
ㆍ controller 패키지에 BoardController 클래스를 생성하고 아래에 코드를 작성
<details>
	<summary><b>코드 보기</b></summary>
	
```java
@Controller
public class BoardController {

	@Autowired
	private BoardService boardService;

	@GetMapping(value = "/board/write.do")
	public String openBoardWrite(@RequestParam(value = "idx", required = false) Long idx, Model model) {
		if (idx == null) {
			model.addAttribute("board", new BoardDTO());
		} else {
			BoardDTO board = boardService.getBoardDetail(idx);
			if (board == null) {
				return "redirect:/board/list.do";
			}
			model.addAttribute("board", board);
		}

		return "board/write";
	}
	
	@PostMapping(value = "/board/register.do")
	public String registerBoard(final BoardDTO params) {
		try {
			boolean isRegistered = boardService.registerBoard(params);
			if (isRegistered == false) {
				// 게시글 등록에 실패하였다는 메시지 전달
			}
		} catch (DataAccessException e) {
			// 데이터베이스 처리 과정에 문제가 발생하였다는 메시지 전달

		} catch (Exception e) {
			// 시스템에 문제가 발생하였다는 메시지 전달
		}
		return "redirect:/board/list.do";
	}
}
```
</details>

|구성 요소|설명|
|---|---|
|@Controller|해당 클래스가 컨트롤러 클래스임을 지정|
|@GetMapping|1. get 방식으로 매핑을 처리할 수 있는 애너테이션</br>2. get 방식은 파라미터가 주소창에 노출이되며, 주로 데이터를 조회할 때 사용|
|@RequestParam|1. 화면에서 전달받은 파라미터를 처리하는 데 사용</br>2. required 속성이 false라면 반드시 필요한 파라미터가 아니라는 의미|
|Model|메서드의 파라미터로 지정된 Model 객체는 데이터를 뷰로 전달하는 데 사용|
|리턴 타입|1. 컨트롤러 메서드의 리턴타입은 String으로 사용자에게 보여줄 화면의 경로를 반환</br>2. 반환된 경로를 자동으로 연결하여 사용자에게 제공|
|@PostMapping|1. post 방식으로 매핑을 처리할 수 있는 애너테이션</br>2. post 방식은 파라미터가 주소창에 노출되지 않으며, 주로 데이터를 생성할 때 사용|
|params|BoardDTO의 멤버 변수명과 사용자 입력 필드의 name 속성 값이 동일하면, params의 각 멤버 변수에 전달된 값들이 자동으로 매핑됨|
</br>

---
### 7. 게시글 리스트 구현
**1) Controller 영역**   
ㆍ 게시글 목록을 보여줄 리스트 페이지에 대한 Controller 영역의 처리가 필요   
ㆍ BoardController 클래스에 아래의 코드를 작성   
<details>
	<summary><b>코드 보기</b></summary>
	
```java
@GetMapping(value = "/board/list.do")
public String openBoardList(Model model) {
	List<BoardDTO> boardList = boardService.getBoardList();
	model.addAttribute("boardList", boardList);

	return "board/list";
}
```
</details>
	
|구성 요소|설명|
|---|---|
|boardList|BoardService에서 호출한 getBoardList 메서드의 실행 결과를 담아 View 영역으로 전달하는데 사용|
</br>

---
### 8. 게시글 조회 구현
**1) Controller 영역**   
ㆍ 특정 게시물을 조회해 출력해 주는 Controller 영역의 처리가 필요   
ㆍ BoardController 클래스에 아래의 코드를 작성
<details>
	<summary><b>코드 보기</b></summary>
	
```java
@GetMapping(value = "/board/view.do")
public String openBoardDetail(@RequestParam(value = "idx", required = false) Long idx, Model model) {
	if (idx == null) {
		// 올바르지 않은 접근이라는 메시지를 전달하고, 게시글 리스트로 이동
		return "redirect:/board/list.do";
	}

	BoardDTO board = boardService.getBoardDetail(idx);
	if (board == null || "Y".equals(board.getDeleteYn())) {
		// 없는 게시글이거나, 이미 삭제된 게시글이라는 메시지를 전달하고, 게시글 리스트로 이동
		return "redirect:/board/list.do";
	}
	model.addAttribute("board", board);

	return "board/view";
}
```
</details>
	
|구성 요소|설명|
|---|---|
|board|getBoardDetail 메서드의 인자로 idx를 전달해서 게시글 정보를 담아 View 영역으로 전달|
</br>
	
---
### 9. 게시글 삭제 구현
**1) Controller 영역**   
ㆍ 특정 게시물을 삭제해 주는 Controller 영역의 처리가 필요   
ㆍ BoardController 클래스에 아래의 코드를 작성   
<details>
	<summary><b>코드 보기</b></summary>
	
```java
@PostMapping(value = "/board/delete.do")
public String deleteBoard(@RequestParam(value = "idx", required = false) Long idx) {
	if (idx == null) {
		// 올바르지 않은 접근이라는 메시지를 전달하고, 게시글 리스트로 이동
		return "redirect:/board/list.do";
	}

	try {
		boolean isDeleted = boardService.deleteBoard(idx);
		if (isDeleted == false) {
			// 게시글 삭제에 실패하였다는 메시지를 전달
		}
	} catch (DataAccessException e) {
		// 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
	} catch (Exception e) {
		// 시스템에 문제가 발생하였다는 메시지를 전달
	}
	return "redirect:/board/list.do";
}
```
</details>

|구성 요소|설명|
|---|---|
|isDeleted|deletedBoard 메서드의 인자로 idx를 전달해서 해당 게시글을 삭제 후 true 또는 false 값을 저장|
</br>

---
### 10. 경고 메시지 처리
**1) Enum 클래스**   
ㆍ constatnt 패키지를 추가한 후, Method라는 이름으로 다음의 Enum 클래스를 추가   
ㆍ Enum 클래스는 상수를 처리하는 목적으로 사용  
<details>
	<summary><b>코드 보기</b></summary>
	
```java
public enum Method {
    GET, POST, PUT, PATCH, DELETE
}
```
</details>
</br>

**2) 공통 컨트롤러 생성**   
ㆍ util 패키지를 생성한 후 UiUtils 클래스를 추가   
ㆍ UiUtils 클래스에 아래 코드를 작성   
<details>
	<summary><b>코드 보기</b></summary>
	
```java
@Controller
public class UiUtils {

	public String showMessageWithRedirect(@RequestParam(value = "message", required = false) String message,
										  @RequestParam(value = "redirectUri", required = false) String redirectUri,
										  @RequestParam(value = "method", required = false) Method method,
										  @RequestParam(value = "params", required = false) Map<String, Object> params, Model model) {

		model.addAttribute("message", message);
		model.addAttribute("redirectUri", redirectUri);
		model.addAttribute("method", method);
		model.addAttribute("params", params);

		return "utils/message-redirect";
	}

}
```
</details>
	
|구성 요소|설명|
|---|---|
|message|사용자에게 전달할 메시지|
|redirectUri|이동할 페이지의 URI|
|method|Enum 클래스에 선언한 HTTP 요청 메서드|
|params|View 영역으로 전달할 파라미터|
</br>

**3) BoardController 변경**   
ㆍ BoardController에 사용자에게 출력할 메시지에 대한 처리 필요   
ㆍ BoardController는 UiUtils 클래스를 상속 받음   
ㆍ 경고 메시지에 대한 주석 처리 부분에 아래 코드 추가   
<details>
	<summary><b>코드 보기</b></summary>
	
```java
@Controller
public class BoardController extends UiUtils {

	@Autowired
	private BoardService boardService;

	@GetMapping(value = "/board/write.do")
	public String openBoardWrite(@RequestParam(value = "idx", required = false) Long idx, Model model) {
		if (idx == null) {
			model.addAttribute("board", new BoardDTO());
		} else {
			BoardDTO board = boardService.getBoardDetail(idx);
			if (board == null) {
				return "redirect:/board/list.do";
			}
			model.addAttribute("board", board);
		}

		return "board/write";
	}

	@PostMapping(value = "/board/register.do")
	public String registerBoard(final BoardDTO params, Model model) {
		try {
			boolean isRegistered = boardService.registerBoard(params);
			if (isRegistered == false) {
				return showMessageWithRedirect("게시글 등록에 실패하였습니다.", "/board/list.do", Method.GET, null, model);
			}
		} catch (DataAccessException e) {
			return showMessageWithRedirect("데이터베이스 처리 과정에 문제가 발생하였습니다.", "/board/list.do", Method.GET, null, model);

		} catch (Exception e) {
			return showMessageWithRedirect("시스템에 문제가 발생하였습니다.", "/board/list.do", Method.GET, null, model);
		}

		return showMessageWithRedirect("게시글 등록이 완료되었습니다.", "/board/list.do", Method.GET, null, model);
	}

	@GetMapping(value = "/board/list.do")
	public String openBoardList(Model model) {
		List<BoardDTO> boardList = boardService.getBoardList();
		model.addAttribute("boardList", boardList);

		return "board/list";
	}

	@GetMapping(value = "/board/view.do")
	public String openBoardDetail(@RequestParam(value = "idx", required = false) Long idx, Model model) {
		if (idx == null) {
			// 올바르지 않은 접근이라는 메시지를 전달하고, 게시글 리스트로 이동
			return "redirect:/board/list.do";
		}

		BoardDTO board = boardService.getBoardDetail(idx);
		if (board == null || "Y".equals(board.getDeleteYn())) {
			// 없는 게시글이거나, 이미 삭제된 게시글이라는 메시지를 전달하고, 게시글 리스트로 이동
			return "redirect:/board/list.do";
		}
		model.addAttribute("board", board);

		return "board/view";
	}

	@PostMapping(value = "/board/delete.do")
	public String deleteBoard(@RequestParam(value = "idx", required = false) Long idx, Model model) {
		if (idx == null) {
			return showMessageWithRedirect("올바르지 않은 접근입니다.", "/board/list.do", Method.GET, null, model);
		}

		try {
			boolean isDeleted = boardService.deleteBoard(idx);
			if (isDeleted == false) {
				return showMessageWithRedirect("게시글 삭제에 실패하였습니다.", "/board/list.do", Method.GET, null, model);
			}
		} catch (DataAccessException e) {
			return showMessageWithRedirect("데이터베이스 처리 과정에 문제가 발생하였습니다.", "/board/list.do", Method.GET, null, model);

		} catch (Exception e) {
			return showMessageWithRedirect("시스템에 문제가 발생하였습니다.", "/board/list.do", Method.GET, null, model);
		}

		return showMessageWithRedirect("게시글 삭제가 완료되었습니다.", "/board/list.do", Method.GET, null, model);
	}
}
```
</details>
</br>
	
---
### 11. 인터셉터 적용
**1) 인터셉터란?**   
ㆍ 인터셉터(Interceptor)의 의미는 "가로챈다." 라는 의미가 있음   
ㆍ 컨트롤러의 URI에 접근하는 과정에서 무언가를 제어할 필요가 있을 때 사용   
ㆍ 예를 들어, 특정 페이지에 접근할 때 로그인이나 계정의 권한과 관련된 처리를 인터셉터를 통해 효율적으로 해결 가능   
</br>

**2) 인터셉터 구현**   
ㆍ 스프링에서 인터셉터는 "HandlerInterceptor" 인터페이스를 상속받아 구현할 수 있음   
ㆍ 해당 인터페이스는 preHandle, postHandle, afterCompletion, afterConcurrentHandlingStarted 총 네 개의 메서드를 포함   
ㆍ interceptor 패키지에 LoggerInterceptor 클래스를 추가한 후 다음의 코드를 작성   
<details>
	<summary><b>코드 보기</b></summary>
	
```java
@Slf4j
public class LoggerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.debug("");
        log.debug("==================== BEGIN ====================");
        log.debug("Request URI ===> " + request.getRequestURI());

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.debug("==================== END ======================");
        log.debug("");
    }
}
```
</details>

|구성 요소|설명|
|---|---|
|preHandle|컨트롤러의 메서드에 매핑된 특정 URI를 호출했을 때 컨트롤러에 접근하기 전에 실행되는 메서드|
|postHandle|컨트롤러를 경유한 다음 화면으로 결과를 전달하기 전에 실행되는 메서드|
</br>

**3)LoggerInterceptor 클래스를 빈으로 등록**   
ㆍ configuration 패키지에 MvcConfiguration 클래스를 생성 후, 아래 코드를 작성   
<details>
	<summary><b>코드 보기</b></summary>
	
```java
@Configuration
public class MvcConfiguration implements WebMvcConfigurer {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LoggerInterceptor())
		.excludePathPatterns("/css/**", "/fonts/**", "/plugin/**", "/scripts/**");
	}
}
```
</details>

|구성 요소|설명|
|---|---|
|addInterceptor|특정 인터셉터를 빈으로 등록하기 위한 메서드|
|excludePathPatterns|특정 패턴의 주소를 인터셉터에서 제외하는 메서드|
</br>

---
### 12. AOP 적용
**1) AOP란?**   
ㆍ AOP는 Aspect Oriented Programming의 약자   
ㆍ 관점 지향 프로그래밍으로써 자바와 같은 객체 지향 프로그래밍을 더욱 객체 지향스럽게 사용할 수 있도록 도와줌   
ㆍ 핵심 비즈니스 로직 외에 공통으로 처리해야 하는 로그 출력, 보안 처리, 예외 처리와 같은 코드를 별도로 분리하는 모듈화의 개념   
ㆍ AOP에서 관점을 핵심적인 관점과 부가적인 관점으로 나눌 수 있음   
ㆍ 핵심적인 관점은 핵심 비즈니스 로직을 의미하고, 부가적인 관점은 핵심 비즈니스 로직 외에 공통으로 처리해야 하는 부분을 의미   
<img src="https://blog.kakaocdn.net/dn/pD57t/btqDLEZKQib/1KOdMZKJgFY06WMwxNydkk/img.png" width="50%">   
ㆍ 위 사진은 일반적인 객체 지향 프로그래밍의 동작과정을 보여줌   
ㆍ 각각의 화살표는 하나의 기능을 구현하는 데 필요한 작업을 의미   
ㆍ 로그 출력, 보안 처리와 같은 부가적인 기능들이 각각의 작업에 추가됨으로써 코드가 복잡해지고, 생산성이 낮아짐   
<img src="https://blog.kakaocdn.net/dn/DWbbY/btqGfN6LnPh/DVVAcqmplI6UEqZVjkhnyK/img.png" width="50%">   
ㆍ 위 사진은 관점 지향 프로그래밍의 동작과정을 보여줌   
ㆍ 객체 지향 프로그래밍과 달리 부가적인 기능들이 핵심 비즈니스 로직 바깥에서 동작   
ㆍ 이와 같이 공통으로 처리해야하는 기능들을 별도로 분리하여 중복되는 코드를 제거하고, 재사용성을 극대화 할 수 있음   
</br>

**2) AOP 용어**   
|구성 요소|설명|
|---|---|
|Aspect|1. 공통으로 적용될 기능</br>2. 부가적인 기능을 정의한 코드인 Advice와 Advice를 어느 곳에 적용할지 결정하는 Pointcut의 조합으로 만들어짐|
|Advice|실제로 부가적인 기능을 구현한 객체|
|JoinPoint|Advice를 적용할 위치|
|Pointcut|1. Advice를 적용할 JoinPoint를 선별하는 과정이나, 그 기능을 정의한 모듈</br>2. 어떤 JoinPoint를 사용할지 결정|
|Target|1. 실제로 비즈니스 로직을 수행하는 개체</br>2. 즉, Advice를 적용할 대상을 의미|
|Proxy|Advice가 적용되었을 때 생성되는 객체|
|Introduction|Target에는 없는 새로운 메서드나 인스턴스 변수를 추가하는 기능|
|Weaving|Pointcut에 의해 결정된 Target의 JoinPoint에 Advice를 적용하는 것|
</br>

**3) AOP 구현**   
ㆍ aop 패키지를 추가하고 LoggerAspect 클래스 생성 후 아래 코드를 작성   
<details>
	<summary><b>코드 보기</b></summary>
	
```java
@Component
@Aspect
public class LoggerAspect {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Around("execution(* com.Board..controller.*Controller.*(..)) or execution(* com.Board..service.*Impl.*(..)) or execution(* com.Board..mapper.*Mapper.*(..))")
	public Object printLog(ProceedingJoinPoint joinPoint) throws Throwable {

		String type = "";
		String name = joinPoint.getSignature().getDeclaringTypeName();

		if (name.contains("Controller") == true) {
			type = "Controller ===> ";

		} else if (name.contains("Service") == true) {
			type = "ServiceImpl ===> ";

		} else if (name.contains("Mapper") == true) {
			type = "Mapper ===> ";
		}

		logger.debug(type + name + "." + joinPoint.getSignature().getName() + "()");
		return joinPoint.proceed();
	}
}
```
</details>

|구성 요소|설명|
|---|---|
|@Component|1. 스프링 컨테이너에 빈으로 등록하기 위한 애너테이션</br>2. @Bean은 개발자가 제어할 수 없는 외부 라이브러리를 빈으로 등록할 때 사용</br>3. @Component는 개발자가 직접 정의한 클래스를 빈으로 등록할 때 사용|
|@Aspect|AOP 기능을 하는 클래스에 지정하는 애너테이션|
|@Around|Advice의 종류 중 한 가지로 Target 메서드 호출 이전과 이후에 모두 적용됨을 의미|
|execution|1. Pointcut을 지정하는 문법</br>2. 즉, 어떤 위치에 공통 기능을 적용할 것인지 정의|
|getSignature( )|실행되는 대상 객체 메서드에 대한 정보를 가지고 옴|
</br>

---
### 13. 트랜잭션 적용
**1) 트랜잭션이란?**   
ㆍ 트랜잭션은 일련의 작업들이 모두 하나의 논리적 작업으로 취급되는 것을 말함   
ㆍ 즉, 하나의 작업에 여러 개의 작업이 같이 묶여 있는 것   
ㆍ 논리적 작업을 취소하게 되면, 내부에 포함된 일련의 작업들이 모두 취소됨   
ㆍ 이렇게 함으로써 데이터의 무결성을 보장할 수 있음   
</br>

**2) 트랜잭션의 기본 원칙**   
|특성|설명|
|---|---|
|원자성(Atomicity)|1. 하나의 트랜잭션은 모두 하나의 작업 단위로 처리되어야 하는 특성</br>2. 트랜잭션이 A, B, C로 구성된다면 A, B, C의 처리 결과는 모두 동일해야 함</br>3. 또한 A, B, C의 처리 중 하나라도 실패했다면 세 가지 모두 처음 상태로 되돌아가야 함|
|일관성(Consistency)|트랜잭션이 실행을 성공적으로 완료했다면 언제나 일관성 있는 데이터베이스 상태를 유지해야 함|
|고립성(Isolation)|트랜잭션은 독립적으로 처리되며, 처리되는 중간에 외부에서의 간섭은 없어야 함|
|지속성(Durability)|트랜잭션의 실행 결과는 지속적으로 유지되어야 함|
</br>

**3) 트랜잭션 설정**   
ㆍ 트랜잭션 설정 방법은 XML 설정, 애너테이션 설정, AOP 설정으로 나눌 수 있음   
ㆍ 이번 프로젝트에서는 AOP 설정을 사용   
ㆍ DBConfiguration 클래스에 아래 사진에 표시된 코드를 작성   
<details>
	<summary><b>코드 보기</b></summary>
	<img src="https://user-images.githubusercontent.com/61148914/125553148-f61516e3-564b-4cd8-8fa0-a4a9f993016a.png" width="60%">
</details>

|구성 요소|설명|
|---|---|
|@EnableTransactionManagement|1. 스프링에서 제공하는 애너테이션 기반 트랜잭션을 활성화</br>2. 이번 프로젝트에서는 애너테이션이 아닌 AOP를 이용한 트랜잭션을 사용할 예정|
|transactionManager( )|스프링에서 제공해주는 트랜잭션 매니저를 빈으로 등록해주는 메서드|
</br>

**4) 트랜잭션 구현**   
ㆍ aop 패키지에 TransactionAspect 클래스를 추가하고 아래 코드를 작성   
<details>
	<summary><b>코드 보기</b></summary>
	
```java
@Configuration
public class TransactionAspect {

	@Autowired
	private PlatformTransactionManager transactionManager;

	private static final String EXPRESSION = "execution(* com.Board..service.*Impl.*(..))";

	@Bean
	public TransactionInterceptor transactionAdvice() {
		List<RollbackRuleAttribute> rollbackRules = Collections.singletonList(new RollbackRuleAttribute(Exception.class));

		RuleBasedTransactionAttribute transactionAttribute = new RuleBasedTransactionAttribute();
		transactionAttribute.setRollbackRules(rollbackRules);
		transactionAttribute.setName("*");

		MatchAlwaysTransactionAttributeSource attributeSource = new MatchAlwaysTransactionAttributeSource();
		attributeSource.setTransactionAttribute(transactionAttribute);

		return new TransactionInterceptor(transactionManager, attributeSource);
	}

	@Bean
	public Advisor transactionAdvisor() {
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression(EXPRESSION);

		return new DefaultPointcutAdvisor(pointcut, transactionAdvice());
	}
}
```
</details>
	
|구성 요소|설명|
|---|---|
|transactionManager|DBConfiguration 클래스에 빈으로 등록한 PlatformTransactionManager 객체|
|EXPRESSION|포인트컷으로써, 비즈니스 로직을 수행하는 xxxImpl 클래스의 모든 메서드를 의미|
|rollbackRules|1. 트랜잭션에서 롤백을 수행하는 규칙</br>2. RollbackRuleAttibute 생성자의 인자로 Exception 클래스를 지정</br>3. 어떠한 예외가 발생하던 무조건 롤백이 수행되도록 설정|
|pointcut|1. AOP의 포인트컷을 설정하기 위한 객체</br>2. EXPRESSION에 지정한 xxxImpl 클래스의 모든 메서드를 대상으로 설정|
</br>

---
### 14. 페이징 처리
**1) 페이징이란?**   
ㆍ 사용자가 어떠한 데이터를 필요로 할 때 전체 데이터 중 일부를 보여주는 방식   
</br>

**2) 공통 페이징 파라미터 치리용 클래스 생성**   
ㆍ paging 패키지를 추가하고, Criteria 클래스를 생성한 후 아래 코드를 작성   
<details>
	<summary><b>코드 보기</b></summary>
	
```java
@Getter
@Setter
public class Criteria {

	private int currentPageNo;
	
	private int recordsPerPage;

	private int pageSize;

	private String searchKeyword;

	private String searchType;

	public Criteria() {
		this.currentPageNo = 1;
		this.recordsPerPage = 10;
		this.pageSize = 10;
	}

	public int getStartPage() {
		return (currentPageNo - 1) * recordsPerPage;
	}
}
```
</details>

|구성 요소|설명|
|---|---|
|currentPageNo|1. 현재 페이지 번호</br>2. 화면을 처리할 때 페이징 정보를 계산하는 용도로 사용|
|recordsPerPage|1. 페이지마다 출력할 데이터의 개수</br>2. 화면을 처리할 때 페이징 정보를 계산하는 데 사용|
|pageSize|1. 화면 하단에 출력할 페이지의 크기</br>2. 5로 지정하면 1부터 5까지의 페이지가 보임|
|searchKeyword|검색 키워드를 의미|
|searchType|1. 검색 유형을 의미</br>2. 제목, 내용, 작성자 등</br>3. searchKeyword와 함께 사용|
</br>

**3) Mapper 인터페이스와 XML의 변경**   
ㆍ BoardMapper 인터페이스의 selectBoardList와 selectBoardTotalCount 메서드를 아래에 코드처럼 변경   
<details>
	<summary><b>코드 보기</b></summary>
	
```java
@Mapper
public interface BoardMapper {

	public int insertBoard(BoardDTO params);

	public BoardDTO selectBoardDetail(Long idx);

	public int updateBoard(BoardDTO params);

	public int deleteBoard(Long idx);

	public List<BoardDTO> selectBoardList(Criteria criteria);

	public int selectBoardTotalCount(Criteria criteria);
}
```
</details>
	
ㆍ BoardMapper XML 파일의 selectBoardList와 selectBoardTotalCount 쿼리를 아래 코드처럼 변경   
<details>
	<summary><b>코드 보기</b></summary>
	
```sql
<select id="selectBoardList" parameterType="Criteria" resultType="BoardDTO">
	SELECT
		<include refid="boardColumns" />
	FROM
		tb_board
	WHERE
		delete_yn = 'N'
	ORDER BY
		notice_yn ASC,
		idx DESC,
		insert_time DESC
	LIMIT
		#{startPage}, #{recordsPerPage}
</select>

<select id="selectBoardTotalCount" parameterType="Criteria" resultType="int">
	SELECT
		COUNT(*)
	FROM
		tb_board
	WHERE
		delete_yn = 'N'
</select>
```
</details>
	
|구성 요소|설명|
|---|---|
|LIMIT|1. MySQL에서 LIMIT 구문은 데이터를 원하는 만큼 가져오고 싶을 때 사용</br>2. LIMIT의 첫 번째 파라미터는 시작 위치를 지정</br>3. 두 번째 파라미터는 시작 위치를 기준으로 가지고 올 데이터의 개수를 지정|
|#{startPage}|1. 마이바티스에서 #{파라미터}는 여러 멤버를 가진 객체의 경우 Getter에 해당</br>2. startPage는 Criteria 클래스의 getStartPage 메서드의 리턴 값을 의미|
|#{recordsPerPage}|페이지당 출력할 데이터의 개수를 의미|
</br>

**4) Service 영역의 변경**   
ㆍ BoardMapper 인터페이스의 메서드가 변경되었기 때문에 서비스 영역도 수정 작업이 필요   
ㆍ BoardService 인터페이스의 getBoardList 메서드를 아래 코드와 같이 변경   
<details>
	<summary><b>코드 보기</b></summary>
	
```java
public interface BoardService {

	public boolean registerBoard(BoardDTO params);

	public BoardDTO getBoardDetail(Long idx);

	public boolean deleteBoard(Long idx);

	public List<BoardDTO> getBoardList(Criteria criteria);
}
```
</details>

ㆍ BoardServiceImpl 클래스의 getBoardList 메서드 또한 아래 코드와 같이 변경   
<details>
	<summary><b>코드 보기</b></summary>
	
```java
@Override
public List<BoardDTO> getBoardList(Criteria criteria) {
	List<BoardDTO> boardList = Collections.emptyList();

	int boardTotalCount = boardMapper.selectBoardTotalCount(criteria);

	if (boardTotalCount > 0) {
		boardList = boardMapper.selectBoardList(criteria);
	}

	return boardList;
}
```
</details>
</br>

**5) Controller 영역의 변경**   
ㆍ BoardController 클래스의 openBoardList 메서드를 아래 코드와 같이 변경   
<details>
	<summary><b>코드 보기</b></summary>
	
```java
@GetMapping(value = "/board/list.do")
public String openBoardList(@ModelAttribute("criteria") Criteria criteria, Model model) {
	List<BoardDTO> boardList = boardService.getBoardList(criteria);
	model.addAttribute("boardList", boardList);

	return "board/list";
}
```
</details>

|구성 요소|설명|
|---|---|
|@ModelAttribute|해당 애너테이션을 사용하면 파라미터로 전달받은 객체를 자동으로 뷰 영역까지 전달 |
</br>
	
**6) DBConfiguration 변경**   
ㆍ 현재는 sqlSessionFactory 빈의 setTypeAliasesPackage가 "com.Board.domain" 으로 지정   
ㆍ 따라서 BoardMapper XML 파일에서 파라미터 타입으로 지정한 Criteria를 인식하지 못하는 문제가 발생   
ㆍ setTypeAliasesPackage를 아래 사진과 같이 "com.Board.*"로 변경   
<img src="https://user-images.githubusercontent.com/61148914/126029263-ef03b35d-b6c0-4132-8493-c2fca750e0f3.png" width="60%">   
</br>

**7) 페이징 정보 계산용 클래스 생성**   
ㆍ 전체 데이터의 개수를 기준으로 화면 하단에 페이지 개수를 계산하는 용도의 클래스 필요   
ㆍ paging 패키지 안에 PaginationInfo 클래스를 추가하고, 아래의 코드를 작성
<details>
	<summary><b>코드 보기</b></summary>
	
```java
@Getter
@Setter
public class PaginationInfo {

	private Criteria criteria;

	private int totalRecordCount;

	private int totalPageCount;

	private int firstPage;

	private int lastPage;

	private int firstRecordIndex;

	private int lastRecordIndex;

	private boolean hasPreviousPage;

	private boolean hasNextPage;

	public PaginationInfo(Criteria criteria) {
		if (criteria.getCurrentPageNo() < 1) {
			criteria.setCurrentPageNo(1);
		}
		if (criteria.getRecordsPerPage() < 1 || criteria.getRecordsPerPage() > 100) {
			criteria.setRecordsPerPage(10);
		}
		if (criteria.getPageSize() < 5 || criteria.getPageSize() > 20) {
			criteria.setPageSize(10);
		}

		this.criteria = criteria;
	}

	public void setTotalRecordCount(int totalRecordCount) {
		this.totalRecordCount = totalRecordCount;

		if (totalRecordCount > 0) {
			calculation();
		}
	}

	private void calculation() {

		totalPageCount = ((totalRecordCount - 1) / criteria.getRecordsPerPage()) + 1;
		if (criteria.getCurrentPageNo() > totalPageCount) {
			criteria.setCurrentPageNo(totalPageCount);
		}
	
		firstPage = ((criteria.getCurrentPageNo() - 1) / criteria.getPageSize()) * criteria.getPageSize() + 1;

		lastPage = firstPage + criteria.getPageSize() - 1;
		if (lastPage > totalPageCount) {
			lastPage = totalPageCount;
		}

		firstRecordIndex = (criteria.getCurrentPageNo() - 1) * criteria.getRecordsPerPage();

		lastRecordIndex = criteria.getCurrentPageNo() * criteria.getRecordsPerPage();

		hasPreviousPage = firstPage != 1;

		hasNextPage = (lastPage * criteria.getRecordsPerPage()) < totalRecordCount;
	}
}
```
</details>
	
|구성 요소|설명|
|---|---|
|criteria|페이지 번호 계산에 필요한 파라미터들이 담긴 클래스|
|totalRecordCount|전체 데이터의 개수|
|totalPageCount|전체 페이지의 개수|
|firstPage|페이지 리스트의 첫 페이지 번호|
|lastPage|페이지 리스트의 마지막 페이지 번호|
|firstRecordIndex|1. Criteria 클래스의 getStartPage 메서드를 대체해서 LIMIT 구문의 첫 번째 값에 사용되는 변수</br>2. Criteria 클래스의 getStartPage 메서드는  삭제|
|lastRecordIndex|1. 오라클과 같이 LIMIT 구문이 존재하지 않고, 인라인 뷰를 사용해야 하는 데이터베이스에서 사용</br>2. 이번 프로젝트는 MySQL을 기반으로 진행하기 때문에 사용하지 않음|
|hasPreviousPage|1. 이전 페이지가 존재하는 지를 구분하는 용도로 사용</br>2. 예를 들어, currentPageNo가 13이라면 이전 페이지에 해당하는 1\~10까지의 페이지가 존재하기 때문에 true가 됨</br>3. 만약, currentPageNo이 1\~10 사이라면 false가 됨|
|hasNextPage|1. 다음 페이지가 존재하는 지를 구분하는 용도로 사용</br>2. 예를 들어, pageSize가 10일 때 lastPage가 25이고, currentPageNo이 13이라면, 11\~20 사이에 있기 때문에 true가 됨</br>3. 만약, currentPageNo이 21\~25 사이라면 false가 됨|
|setTotalRecordCount( )|파라미터로 넘어온 전체 데이터 개수를 totalRecordCount 변수에 저장</br>2. totalRecordCount가 1 이상이면 calculation 메서드를 실행|
|calcaulation( )|PaginationInfo 클래스의 각 멤버변수의 값을 구하고, 페이지 번호를 계산하는 메서드|
</br>

**8) 전체 영역의 변경**   
ㆍ 이제까지, 게시글 리스트를 호출하는 모든 메서드를 Controller부터 Mapper 영역까지 모두 Criteria 클래스를 파라미터로 받도록 처리하였음   
ㆍ 위 과정처럼 Controller 영역에서 페이징 정보와 같은 비즈니스 로직을 처리하는 것은 문제가 있음 (Controller 영역은 Service 영역에서 가공한 데이터를 View 영역으로 전달하는 작업만을 해야하기 때문)   
ㆍ 따라서, Service 영역에서 페이징 정보를 계산할 수 있도록 처리해 줘야 함   
ㆍ domain 패키지에 CommonDTO 클래스를 추가하고 아래 코드를 작성   
<details>
	<summary><b>코드 보기</b></summary>
	
```java
@Getter
@Setter
public class CommonDTO extends Criteria {

	private PaginationInfo paginationInfo;

	private String deleteYn;

	private LocalDateTime insertTime;

	private LocalDateTime updateTime;

	private LocalDateTime deleteTime;
}
```
</details>

ㆍ 다음으로 BoardDTO 클래스가 CommonDTO 클래스를 상속받도록 아래 코드와 같이 변경   
ㆍ 공통 멤버 변수는 CommonDTO에 추가되었기 때문에 BoardDTO 클래스에서는 제거됨   
<details>
	<summary><b>코드 보기</b></summary>
	
```java
@Getter
@Setter
public class BoardDTO extends CommonDTO {

	private Long idx;

	private String title;

	private String content;

	private String writer;

	private int viewCnt;

	private String noticeYn;

	private String secretYn;
}
```
</details>

ㆍ BoardMapper 인터페이스에서 selectBoardList, selectBoardTotalCount 메서드가 BoardDTO 클래스를 파라미터로 받도록 아래 코드와 같이 변경   
<details>
	<summary><b>코드 보기</b></summary>
	
```java
@Mapper
public interface BoardMapper {

	public int insertBoard(BoardDTO params);

	public BoardDTO selectBoardDetail(Long idx);

	public int updateBoard(BoardDTO params);

	public int deleteBoard(Long idx);

	public List<BoardDTO> selectBoardList(BoardDTO params);

	public int selectBoardTotalCount(BoardDTO params);
}
```
</details>

ㆍ BoardMapper XML의 selectBoardList와 selectBoardTotalCount 쿼리를 아래 코드와 같이 변경   
<details>
	<summary><b>코드 보기</b></summary>
	
```sql
<select id="selectBoardList" parameterType="BoardDTO" resultType="BoardDTO">
	SELECT
		<include refid="boardColumns" />
	FROM
		tb_board
	WHERE
		delete_yn = 'N'
	ORDER BY
		notice_yn ASC,
		idx DESC,
		insert_time DESC
	LIMIT
		#{paginationInfo.firstRecordIndex}, #{recordsPerPage}
</select>

<select id="selectBoardTotalCount" parameterType="BoardDTO" resultType="int">
	SELECT
		COUNT(*)
	FROM
		tb_board
	WHERE
		delete_yn = 'N'
</select>
```
</details>
	
ㆍ BoardService 인터페이스 중 getBoardList 메서드의 파라미터를 BoardDTO 클래스로 받을 수 있도록 아래 코드와 같이 변경   
<details>
	<summary><b>코드 보기</b></summary>
	
```java
public interface BoardService {

	public boolean registerBoard(BoardDTO params);

	public BoardDTO getBoardDetail(Long idx);

	public boolean deleteBoard(Long idx);

	public List<BoardDTO> getBoardList(BoardDTO params);
}
```
</details>
	
ㆍ BoardServiceImpl 클래스의 getBoardList 메서드를 다음 코드와 같이 변경   
ㆍ Controller 영역에서 PaginationInfo 객체를 처리하지 않고 Service 영역에서 처리하도록 하는 방식   
<details>
	<summary><b>코드 보기</b></summary>
	
```java
@Override
public List<BoardDTO> getBoardList(BoardDTO params) {
	List<BoardDTO> boardList = Collections.emptyList();

	int boardTotalCount = boardMapper.selectBoardTotalCount(params);

	PaginationInfo paginationInfo = new PaginationInfo(params);
	paginationInfo.setTotalRecordCount(boardTotalCount);

	params.setPaginationInfo(paginationInfo);

	if (boardTotalCount > 0) {
		boardList = boardMapper.selectBoardList(params);
	}

	return boardList;
}
```
</details>
	
ㆍ BoardController 클래스의 openBoardList 메서드를 아래 코드와 같이 변경   
ㆍ Controller 영역은 단순히 View 영역으로 데이터를 전달하는 역할만 하도록 설정   
<details>
	<summary><b>코드 보기</b></summary>
	
```java
@GetMapping(value = "/board/list.do")
public String openBoardList(@ModelAttribute("params") BoardDTO params, Model model) {
	List<BoardDTO> boardList = boardService.getBoardList(params);
	model.addAttribute("boardList", boardList);

	return "board/list";
}
```
</details>
	
ㆍ 마지막으로 Criteria 클래스에 아래 코드와 같이 makeQueryString 메서드를 추가   
<details>
	<summary><b>코드 보기</b></summary>
	
```java
@Getter
@Setter
public class Criteria {

	private int currentPageNo;

	private int recordsPerPage;

	private int pageSize;

	private String searchKeyword;

	private String searchType;

	public Criteria() {
		this.currentPageNo = 1;
		this.recordsPerPage = 10;
		this.pageSize = 10;
	}

	public String makeQueryString(int pageNo) {

		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.queryParam("currentPageNo", pageNo)
				.queryParam("recordsPerPage", recordsPerPage)
				.queryParam("pageSize", pageSize)
				.queryParam("searchType", searchType)
				.queryParam("searchKeyword", searchKeyword)
				.build()
				.encode();

		return uriComponents.toUriString();
	}
}
```
</details>
	
|구성 요소|설명|
|---|---|
|makeQueryString( )|1. Criteria 클래스의 멤버 변수들을 쿼리 스트링 형태로 반환해주는 역할</br>2. 스프링에서 제공해주는 UriComponents 클래스를 이용하면 URI를 효율적으로 처리할 수 있음|
</br>

---
### 15. 검색 처리
**1) 공통 Mapper XML 생성**   
ㆍ 검색 기능은 공통으로 사용되는 기능이기 때문에 하나의 Mapper XML에 검색을 처리하는 SQL 문을 선언하고 사용하는 것이 좋음   
ㆍ src/main/resources 디렉터리의 mappers 폴더에 CommonMapper.xml 파일을 추가하고 아래의 코드를 작성   
<details>
	<summary><b>코드 보기</b></summary>
	
```sql
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="CommonMapper">

	<!-- MySQL 페이징 -->
	<sql id="paging">
		LIMIT
			#{paginationInfo.firstRecordIndex}, #{recordsPerPage}
	</sql>

	<!-- MySQL 검색 -->
	<sql id="search">
		<!-- 검색 키워드가 있을 때 -->
		<if test="searchKeyword != null and searchKeyword != ''">
			<choose>
				<!-- 검색 유형이 있을 때 -->
				<when test="searchType != null and searchType != ''">
					<choose>
						<when test="'title'.equals(searchType)">
							AND title LIKE CONCAT('%', #{searchKeyword}, '%')
						</when>
						<when test="'content'.equals(searchType)">
							AND content LIKE CONCAT('%', #{searchKeyword}, '%')
						</when>
						<when test="'writer'.equals(searchType)">
							AND writer LIKE CONCAT('%', #{searchKeyword}, '%')
						</when>
					</choose>
				</when>
				<!-- 검색 유형이 없을 때 -->
				<otherwise>
					AND
						(
							   title LIKE CONCAT('%', #{searchKeyword}, '%')
							OR content LIKE CONCAT('%', #{searchKeyword}, '%')
							OR writer LIKE CONCAT('%', #{searchKeyword}, '%')
						)
				</otherwise>
			</choose>
		</if>
	</sql>

</mapper>
```
</details>
	
|구성 요소|설명|
|---|---|
|paging|공통으로 사용되는 기능인 페이징 기능을 SQL 조각으로 만들어 사용|
|search|1. 조건문을 통해 검색 키워드가 파라미터로 넘어온 경우에만 쿼리를 실행하도록 설정</br>2. 검색 유형이 파라미터로 넘어오면 <choose> 태그 안에 있는 각각의 <when> 조건에 알맞은 검색 유형을 기준으로 LIKE 쿼리를 실행|
</br>
	
**2) BoardMapper XML 변경**   
ㆍ 기존의 BoardMapper XML 파일을 위에서 작성한 CommonMapper XML의 SQL 문을 인클루드 하는 형태로 변경할 필요가 있음   
ㆍ BoardMapper XML의 selectBoardList, selectBoardTotalCount를 아래 코드와 같이 변경   
<details>
	<summary><b>코드 보기</b></summary>
	
```sql
<select id="selectBoardList" parameterType="BoardDTO" resultType="BoardDTO">
	SELECT
		<include refid="boardColumns" />
	FROM
		tb_board
	WHERE
		delete_yn = 'N'
	<include refid="CommonMapper.search" />
	ORDER BY
		notice_yn ASC,
		idx DESC,
		insert_time DESC
	<include refid="CommonMapper.paging" />
</select>

<select id="selectBoardTotalCount" parameterType="BoardDTO" resultType="int">
	SELECT
		COUNT(*)
	FROM
		tb_board
	WHERE
		delete_yn = 'N'
	<include refid="CommonMapper.search" />
</select>
```
</details>
</br>

---
### 16. REST 방식으로 댓글 CRUD 처리   
**1) REST란?**   
ㆍ REST는 Representational State Tranfer의 약자이고, 하나의 URI는 하나의 고유한 리소스를 대표하도록 설계된다는 개념   
ㆍ 디바이스의 종류에 상관없이 공통으로 데이터를 처리할 수 있도록 하는 방식을 뜻함   
ㆍ 지금까지의 게시판 구현 방식은 Controller 영역에서 비즈니스 로직을 호출하고 필요한 결과를 View 영역으로 전달한 다음 HTML 파일을 리턴해주는 방식으로 진행   
ㆍ REST API를 이용하면 HTML 파일을 리턴해주는 방식이 아닌, 사용자가 필요로 하는 데이터만을 리턴해주는 방식으로 구현이 가능   
</br>

**2) 댓글 테이블 생성**   
ㆍ MySQL Workbench 내에서, 아래의 스크립트를 실행한 후 댓글 테이블을 생성   
<details>
	<summary><b>코드 보기</b></summary>
	
```sql
CREATE TABLE tb_comment (
    idx INT NOT NULL AUTO_INCREMENT COMMENT '번호 (PK)',
    board_idx INT NOT NULL COMMENT '게시글 번호 (FK)',
    content VARCHAR(3000) NOT NULL COMMENT '내용',
    writer VARCHAR(20) NOT NULL COMMENT '작성자',
    delete_yn ENUM('Y', 'N') NOT NULL DEFAULT 'N' COMMENT '삭제 여부',
    insert_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
    update_time DATETIME DEFAULT NULL COMMENT '수정일',
    delete_time DATETIME DEFAULT NULL COMMENT '삭제일',
    PRIMARY KEY (idx)
) COMMENT '댓글';
```
</details>
	
ㆍ 특정 게시글에 댓글을 등록하거나, 등록된 댓글을 조회하기 위해서는 게시판 테이블의 게시글 번호(idx)와 댓글 테이블의 게시글 번호(board_idx)가 연결되어야 함   
ㆍ MySQL Workbench 내에서, 아래의 스크립트를 실행한 후 게시판 테이블의 게시글 번호(idx)를 참조해서 댓글 테이블의 게시글 번호(board_idx)를 외래키로 지정하는 제약 조건을 추가   
<details>
	<summary><b>코드 보기</b></summary>
	
```sql
alter table tb_comment add constraint fk_comment_board_idx foreign key (board_idx) references tb_board(idx);
```
</details>
</br>

**3) 도메인 클래스 생성**   
ㆍ 위에서 생성한 댓글 테이블의 구조화 역할을 하는 도메인 클래스가 필요   
ㆍ domain 패키지 내에 CommentDTO 클래스를 추가하고, 아래의 코드를 작성   
<details>
	<summary><b>코드 보기</b></summary>
	
```java
@Getter
@Setter
public class CommentDTO extends CommonDTO {

	private Long idx;

	private Long boardIdx;

	private String content;

	private String writer;
}
```
</details>

|구성 요소|설명|
|---|---|
|idx|댓글 번호|
|boardIdx|댓글과 연결되는 게시글 번호|
|content|댓글 내용|
|writer|댓글 작성자|
</br>

**4) Mapper 인터페이스 생성**   
ㆍ 데이터베이스와 통신 역할을 하는 Mapper 인터페이스의 생성 필요   
ㆍ mapper 패키지에 CommentMapper 인터페이스를 생성하고, 아래의 코드를 작성   
<details>
	<summary><b>코드 보기</b></summary>
	
```java
@Mapper
public interface CommentMapper {

	public int insertComment(CommentDTO params);

	public CommentDTO selectCommentDetail(Long idx);

	public int updateComment(CommentDTO params);

	public int deleteComment(Long idx);

	public List<CommentDTO> selectCommentList(CommentDTO params);

	public int selectCommentTotalCount(CommentDTO params);
}
```
</details>
	
|구성 요소|설명|
|---|---|
|@Mapper|해당 인터페이스가 데이터베이스와 통신하는 인터페이스를 의미|
|insertComment( )|댓글을 생성하는 INSERT 쿼리를 호출하는 메서드|
|selectCommentDetail( )|특정 댓글의 상세 내용을 조회하는 SELECT 쿼리를 호출하는 메서드|
|updateComment( )|UPDATE 쿼리를 호출하여, delete_yn 컬럼의 상태를 'Y'로 지정하는 메서드|
|selectCommentList( )|특정 게시글에 포함된 댓글 목록을 조회하는 SELECT 쿼리를 호출하는 메서드|
|selectCommentTotalCount( )|특정 게시글에 포함된 댓글 개수를 조회하는 SELECT 쿼리를 호출하는 메서드|
</br>

**5) 마이바티스 XML Mapper 생성**   
ㆍ src/main/resources 디렉터리의 mappers 폴더에 CommentMapper XML을 추가하고, 아래에 코드를 작성   
<details>
	<summary><b>코드 보기</b></summary>
	
```sql
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.Board.mapper.CommentMapper">

	<sql id="commentColumns">
		  idx
		, board_idx
		, content
		, writer
		, delete_yn
		, insert_time
		, update_time
		, delete_time
	</sql>

	<insert id="insertComment" parameterType="CommentDTO">
		INSERT INTO tb_comment (
			<include refid="commentColumns" />
		) VALUES (
			  #{idx}
			, #{boardIdx}
			, #{content}
			, #{writer}
			, IFNULL(#{deleteYn}, 'N')
			, NOW()
			, NULL
			, NULL
		)
	</insert>

	<select id="selectCommentDetail" parameterType="long" resultType="CommentDTO">
		SELECT
			<include refid="commentColumns" />
		FROM
			tb_comment
		WHERE
			delete_yn = 'N'
		AND
			idx = #{idx}
	</select>

	<update id="updateComment" parameterType="CommentDTO">
		UPDATE tb_comment
		SET
			  update_time = NOW()
			, content = #{content}
			, writer = #{writer}
		WHERE
			idx = #{idx}
	</update>

	<update id="deleteComment" parameterType="long">
		UPDATE tb_comment
		SET
			  delete_yn = 'Y'
			, delete_time = NOW()
		WHERE
			idx = #{idx}
	</update>

	<select id="selectCommentList" parameterType="CommentDTO" resultType="CommentDTO">
		SELECT
			<include refid="commentColumns" />
		FROM
			tb_comment
		WHERE
			delete_yn = 'N'
		AND
			board_idx = #{boardIdx}
		ORDER BY
			idx DESC,
			insert_time DESC
	</select>

	<select id="selectCommentTotalCount" parameterType="CommentDTO" resultType="int">
		SELECT
			COUNT(*)
		FROM
			tb_comment
		WHERE
			delete_yn = 'N'
		AND
			board_idx = #{boardIdx}
	</select>

</mapper>
```
</details>
</br>

**6) Service 영역의 구현**   
ㆍ service 패키지에 CommentService 인터페이스를 추가하고, 아래의 코드를 작성   
<details>
	<summary><b>코드 보기</b></summary>
	
```java
public interface CommentService {

	public boolean registerComment(CommentDTO params);

	public boolean deleteComment(Long idx);

	public List<CommentDTO> getCommentList(CommentDTO params);
}
```
</details>
	
ㆍ CommentService 인터페이스에 대한 구현 클래스가 필요   
ㆍ service 패키지에 CommentServiceImpl 클래스를 추가하고, 아래의 코드를 작성   
<details>
	<summary><b>코드 보기</b></summary>
	
```java
@Service
public class CommentServiceImpl implements CommentService {

	@Autowired
	private CommentMapper commentMapper;

	@Override
	public boolean registerComment(CommentDTO params) {
		int queryResult = 0;

		if (params.getIdx() == null) {
			queryResult = commentMapper.insertComment(params);
		} else {
			queryResult = commentMapper.updateComment(params);
		}

		return (queryResult == 1) ? true : false;
	}

	@Override
	public boolean deleteComment(Long idx) {
		int queryResult = 0;

		CommentDTO comment = commentMapper.selectCommentDetail(idx);

		if (comment != null && "N".equals(comment.getDeleteYn())) {
			queryResult = commentMapper.deleteComment(idx);
		}

		return (queryResult == 1) ? true : false;
	}

	@Override
	public List<CommentDTO> getCommentList(CommentDTO params) {
		List<CommentDTO> commentList = Collections.emptyList();

		int commentTotalCount = commentMapper.selectCommentTotalCount(params);
		if (commentTotalCount > 0) {
			commentList = commentMapper.selectCommentList(params);
		}

		return commentList;
	}

}
```
</details>
	
|구성 요소|설명|
|---|---|
|@Service|해당 클래스가 비즈니스 로직을 수행하는 클래스임을 의미하는 애너테이션|
|commentMapper|@Autowired 애너테이션을 사용해서 빈으로 등록된 CommentMapper 객체를 클래스에 주입|
|registerComment( )|1. 댓글 번호가 파라미터에 포함되어 있지 않으면 댓글 생성 메서드를 실행</br>2. 댓글 번호가 파라미터에 포함되어 있으면 댓글 수정 메서드를 실행|
|deleteComment( )|댓글의 상세 내용을 조회해서 정상적으로 사용 중인 댓글인 경우에 삭제 메서드 실행|
|getCommentList( )|특정 게시글에 포함된 댓글이 1개 이상이면 댓글 목록 리스트를 반환|
</br>

**7) Gson 라이브러리 추가**   
ㆍ 컨트롤러 영역을 구현하기 전에 JSON과 자바 객체의 직렬화(자바 객체 → JSON), 역질렬화(JSON → 자바 객체)를 처리해주는 오픈 소스 자바 라이브러리인 Gson의 추가가 필요   
ㆍ build.gradle에서 dependencis의 가장 하단에 아래 코드를 입력해 Gson 라이브러리를 추가   
<details>
	<summary><b>코드 보기</b></summary>
	
```
compile group: 'com.google.code.gson', name: 'gson', version: '2.8.5'
```
</details>
	
ㆍ 리프레쉬가 완료되면 application.properties에 아래의 코드를 추가하여, Gson 라이브러리에 대한 추가 설정을 완료   
<details>
	<summary><b>코드 보기</b></summary>
	
```
spring.mvc.converters.preferred-json-mapper=gson
```
</details>
</br>

---
### 18. 댓글 리스트 구현  
**1) Controller 영역**   
ㆍ controller 패키지에 CommentController 클래스를 추가하고, 아래의 코드를 작성   
<details>
	<summary><b>코드 보기</b></summary>
	
```java
@RestController
public class CommentController {

	@Autowired
	private CommentService commentService;

	@GetMapping(value = "/comments/{boardIdx}")
	public JsonObject getCommentList(@PathVariable("boardIdx") Long boardIdx, @ModelAttribute("params") CommentDTO params) {

		JsonObject jsonObj = new JsonObject();

		List<CommentDTO> commentList = commentService.getCommentList(params);
	
		if (CollectionUtils.isEmpty(commentList) == false) {
			JsonArray jsonArr = new Gson().toJsonTree(commentList).getAsJsonArray();
			jsonObj.add("commentList", jsonArr);
		}

		return jsonObj;
	}
}
```
</details>

|구성 요소|설명|
|---|---|
|@RestController|해당 애너테이션이 선언된 클래스 내의 모든 메서드는 화면이 아닌, 리턴 타입에 해당하는 데이터 자체를 리턴|
|@PathVariable|1. @RequestParam과 유사한 기능을 하며, REST 방식에서 리소스를 표현하는 데 사용</br>2. 호출된 URI에 파라미터로 전달받을 변수를 지정할 수 있음|
</br>

**2) JSON 날짜 데이터 형식 지정**   
ㆍ CommentController의 getCommentList 메서드가 리턴하는 JSON 데이터를 확인해보면, insertTime 또한 JSON 형태로 이루어졌다는 것을 확인할 수 있음   
ㆍ View 영역에서의 원할한 처리를 위해 형식을 변환할 필요가 있음   
ㆍ adapter 패키지를 추가하고, GsonLocalDateTimeAdapter 클래스를 생성한 후 아래 코드를 작성   
<details>
	<summary><b>코드 보기</b></summary>
	
```java
public class GsonLocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

	@Override
	public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(src));
	}

	@Override
	public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		return LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	}
}
```
</details>
	
ㆍ CommentController의 getCommentList 메서드를 아래 코드와 같이 어댑터 클래스를 포함하여 객체를 생성하는 형태로 변경   
<details>
	<summary><b>코드 보기</b></summary>
	
```java
@RestController
public class CommentController {

	@Autowired
	private CommentService commentService;

	@GetMapping(value = "/comments/{boardIdx}")
	public JsonObject getCommentList(@PathVariable("boardIdx") Long boardIdx, @ModelAttribute("params") CommentDTO params) {

		JsonObject jsonObj = new JsonObject();

		List<CommentDTO> commentList = commentService.getCommentList(params);
		if (CollectionUtils.isEmpty(commentList) == false) {
			Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTimeAdapter()).create();
			JsonArray jsonArr = gson.toJsonTree(commentList).getAsJsonArray();
			jsonObj.add("commentList", jsonArr);
		}

		return jsonObj;
	}
}
```
</details>
</br>

---
### 19. 댓글 등록(수정) 구현   
**1) Controller 영역**   
ㆍ CommentController 클래스에 아래의 registerComment 메서드에 대한 코드를 작성   
<details>
	<summary><b>코드 보기</b></summary>
	
```java
@RequestMapping(value = { "/comments", "/comments/{idx}" }, method = { RequestMethod.POST, RequestMethod.PATCH })
public JsonObject registerComment(@PathVariable(value = "idx", required = false) Long idx, @RequestBody final CommentDTO params) {

	JsonObject jsonObj = new JsonObject();

	try {
		if (idx != null) {
			params.setIdx(idx);
		}

		boolean isRegistered = commentService.registerComment(params);
		jsonObj.addProperty("result", isRegistered);

	} catch (DataAccessException e) {
		jsonObj.addProperty("message", "데이터베이스 처리 과정에 문제가 발생하였습니다.");

	} catch (Exception e) {
		jsonObj.addProperty("message", "시스템에 문제가 발생하였습니다.");
	}

	return jsonObj;
}
```
</details>
	
|구성 요소|설명|
|---|---|
|@RequestMapping|1. 게시글의 경우 하나의 URI로 생성과 수정 처리가 가능</br>2. REST API는 설계 규칙을 지켜야 하기 때문에 해당 애너테이션을 통해 URI를 구분되게 처리|
|@RequestBody|1. REST 방식의 처리에 사용되는 애너테이션</br>2. 파라미터 앞에 해당 애너테이션이 지정되면, 파라미터로 전달받은 JSON 문자열이 객체로 변환됨|
</br>

---
### 20. 댓글 삭제 구현   
**1) Controller 영역**   
ㆍ CommentController 클래스에 아래 deleteComment 메서드에 대한 코드를 작성   
<details>
	<summary><b>코드 보기</b></summary>
	
```java
@DeleteMapping(value = "/comments/{idx}")
public JsonObject deleteComment(@PathVariable("idx") final Long idx) {

	JsonObject jsonObj = new JsonObject();

	try {
		boolean isDeleted = commentService.deleteComment(idx);
		jsonObj.addProperty("result", isDeleted);

	} catch (DataAccessException e) {
		jsonObj.addProperty("message", "데이터베이스 처리 과정에 문제가 발생하였습니다.");

	} catch (Exception e) {
		jsonObj.addProperty("message", "시스템에 문제가 발생하였습니다.");
	}

	return jsonObj;
}
```
</details>

|구성 요소|설명|
|---|---|
|@DeleteMapping|1. HTTP 요청 메서드 중 DELETE를 의미</br>2. 이번 프로젝트에서는 실제로 댓글을 삭제하지는 않지만, URI의 구분을 위해 해당 애너테이션을 선언|
|@PathVariable|1. @RequestParam과 유사한 기능을 하며, REST 방식에서 리소스를 표현하는 데 사용</br>2. 호출된 URI에 파라미터로 전달받을 변수를 지정할 수 있음|
</br>

---
### 21. 파일 업로드 & 다운로드  
**1) 파일 테이블 생성**   
ㆍ MySQL Workbench를 실행하고, 아래의 스크립트를 실행하여 파일 테이블을 생성   
<details>
	<summary><b>코드 보기</b></summary>
	
```sql
CREATE TABLE tb_file (
    idx INT NOT NULL AUTO_INCREMENT COMMENT '파일 번호 (PK)',
    board_idx INT NOT NULL COMMENT '게시글 번호 (FK)',
    original_name VARCHAR(260) NOT NULL COMMENT '원본 파일명',
    save_name VARCHAR(40) NOT NULL COMMENT '저장 파일명',
    size INT NOT NULL COMMENT '파일 크기',
    delete_yn ENUM('Y', 'N') NOT NULL DEFAULT 'N' COMMENT '삭제 여부',
    insert_time DATETIME NOT NULL DEFAULT NOW() COMMENT '등록일',
    delete_time DATETIME NULL COMMENT '삭제일',
    PRIMARY KEY (idx)
) comment '첨부 파일';
```
</details>

ㆍ 댓글과 마찬가지로 특정 게시글에 파일을 등록하거나 등록된 파일을 조회하려면, 게시판 테이블의 게시글 번호(idx)와 파일 테이블의 게시글 번호(board_idx)가 연결되어야 함   
ㆍ 아래의 스크립트를 실행해 게시판 테이블의 게시글 번호(idx)를 참조해서 첨부 파일 테이블의 게시글 번호(board_idx)를 FK로 지정하는 제약 조건을 추가   
<details>
	<summary><b>코드 보기</b></summary>
	
```sql
alter table tb_attach add constraint fk_attach_board_idx foreign key (board_idx) references tb_board(idx);
```
</details>
</br>

**2) 라이브러리 추가**   
ㆍ 파일 처리와 관련된 여러 가지 기능을 제공해 주는 라이브러리를 추가   
ㆍ build.gradle의 compile group에 아래의 코드를 추가   
<details>
	<summary><b>코드 보기</b></summary>
	
```
compile group: 'commons-io', name: 'commons-io', version: '2.6'
compile group: 'commons-fileupload', name: 'commons-fileupload', version: '1.3.3'
```
</details>
</br>

**3) 파일 처리용 빈 설정**   
ㆍ 스프링에는 파일 업로드 처리를 위한 MultipartResolver 인터페이스가 정의되어 있음   
ㆍ 구현 클래스로 아파치의 CommonsMultipartResolver와 서블릿 3.0 이상의 API를 이용한 StandardServletMultipartResolver가 있음   
ㆍ 이번 프로젝트에서는 CommonsMultipartResolver를 이용해서 파일 업로드를 구현   
ㆍ MvcConfiguration 클래스에 아래 코드와 같이 CommonsMultipartResolver 빈을 추가   
<details>
	<summary><b>코드 보기</b></summary>
	
```java
@Configuration
public class MvcConfiguration implements WebMvcConfigurer {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LoggerInterceptor())
		.excludePathPatterns("/css/**", "/fonts/**", "/plugin/**", "/scripts/**");
	}

	@Bean
	public CommonsMultipartResolver multipartResolver() {
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
		multipartResolver.setDefaultEncoding("UTF-8");   // 파일 인코딩 설정
		multipartResolver.setMaxUploadSizePerFile(5 * 1024 * 1024);   // 파일당 업로드 크기 제한 (5MB)
		return multipartResolver;
	}
}
```
</details>
</br>

**4) 도메인 클래스 생성**   
ㆍ 파일 테이블의 구조화 역할을 하는 도메인 클래스를 생성할 필요가 있음   
ㆍ domain 패키지에 AttachDTO 클래스를 추가하고, 아래의 코드를 작성   
<details>
	<summary><b>코드 보기</b></summary>
	
```java
@Getter
@Setter
public class FileDTO extends CommonDTO {

	private Long idx;

	private Long boardIdx;

	private String originalName;

	private String saveName;

	private long size;
}
```
</details>

|구성 요소|설명|
|---|---|
|idx|파일 번호|
|boardIdx|게시글 번호|
|originalName|원본 파일명|
|saveName|저장 파일명|
|size|파일 크기|
</br>

**5) Mapper 인터페이스 생성**   
ㆍ 데이터베이스와 통신 역할을 하는 Mapper 인터페이스를 생성할 필요가 있음   
ㆍ mapper 패키지에 FileMapper 인터페이스를 생성하고 아래의 코드를 작성   
<details>
	<summary><b>코드 보기</b></summary>
	
```java
@Mapper
public interface FileMapper {

	public int insertFile(List<FileDTO> fileList);

	public FileDTO selectFileDetail(Long idx);

	public int deleteFile(Long boardIdx);

	public List<FileDTO> selectFileList(Long boardIdx);

	public int selectFileTotalCount(Long boardIdx);
}
```
</details>

|구성 요소|설명|
|---|---|
|insertFile( )|파일 정보를 저장하는 INSERT 쿼리를 호출하는 메서드|
|selectFileDetail( )|파라미터로 전달받은 파일 번호에 해당하는 파일의 상세 정보를 조회하는 메서드|
|deleteFile( )|특정 게시글에 포함된 모든 파일의 삭제 여부 상태 값을 'N'으로 변경하는 메서드|
|selectFileList( )|특정 게시글에 포함된 파일 목록으 조회하는 SELECT 쿼리를 호출하는 메서드|
|selectFileTotalCount( )|특정 게시글에 포함된 파일 개수를 조회하는 SELECT 쿼리를 호출하는 메서드|
</br>
	
**6) 마이바티스 XML Mapper 생성**   
ㆍ src/main/resources 디렉터리의 mappers 폴더에 AttachMapper XML을 생성하고 아래의 쿼리를 작성   
<details>
	<summary><b>코드 보기</b></summary>
	
```sql
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.board.mapper.AttachMapper">

	<sql id="fileColumns">
		  idx
		, board_idx
		, original_name
		, save_name
		, size
		, delete_yn
		, insert_time
		, delete_time
	</sql>

	<insert id="insertFile" parameterType="list">
		INSERT INTO tb_file (
			<include refid="fileColumns" />
		) VALUES
		<foreach collection="list" item="item" separator=",">
		(
			  #{item.idx}
			, #{item.boardIdx}
			, #{item.originalName}
			, #{item.saveName}
			, #{item.size}
			, 'N'
			, NOW()
			, NULL
		)
		</foreach>
	</insert>

	<select id="selectFileDetail" parameterType="long" resultType="FileDTO">
		SELECT
			<include refid="FileColumns" />
		FROM
			tb_file
		WHERE
			delete_yn = 'N'
		AND
			idx = #{idx}
	</select>

	<update id="deleteFile" parameterType="long">
		UPDATE tb_file
		SET
			  delete_yn = 'Y'
			, delete_time = NOW()
		WHERE
			board_idx = #{boardIdx}
	</update>

	<select id="selectFileList" parameterType="long" resultType="FileDTO">
		SELECT
			<include refid="fileColumns" />
		FROM
			tb_file
		WHERE
			delete_yn = 'N'
		AND
			board_idx = #{boardIdx}
	</select>

	<select id="selectFileTotalCount" parameterType="long" resultType="int">
		SELECT
			COUNT(*)
		FROM
			tb_file
		WHERE
			delete_yn = 'N'
		AND
			board_idx = #{boardIdx}
	</select>
</mapper>
```
</details>
</br>

**7) 공통 파일 처리용 클래스 생성**   
