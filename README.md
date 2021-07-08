# Spring Boot를 활용한 게시판의 제작
### 목차
1. [프로젝트 이름](#1-프로젝트-이름)

2. [프로젝트 일정](#2-프로젝트-일정)

3. [기술 스택](#3-기술-스택)

---
### 1. 프로젝트 이름
* **게시물 등록, 조회, 수정, 삭제가 가능한 게시판**

---
### 2. 프로젝트 일정


---
### 3. 기술 스택
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
### 4. 프로젝트 구조
<img src="https://user-images.githubusercontent.com/61148914/124562382-ecc9f680-de79-11eb-8863-e4560bec3570.JPG" width="25%">
</br>

**1) src/main/java 디렉터리**   
ㆍ 클래스, 인터페이스 등 자바 파일이 위치하는 디렉터리   
</br>

**2) BoardApplication 클래스**   
ㆍ 해당 클래스 내의 main 메서드는 SpringApplication.run 메서드를 호출해서 웹 애플리케이션을 실행하는 역할을 함   
ㆍ "@SpringBootApplication"은 다음 3가지 애너테이션으로 구성   
|애너테이션|설명|
|---|---|
|@EnableAutoConfiguration|다양한 설정들의 일부가 자동으로 완료됨|
|@ComponentScan|자동으로 컴포넌트 클래스를 검색하고 스프링 애플리케이션 콘텍스트에 빈으로 등록함|
|@Configuration|해당 애너테이션이 선언된 클래스는 자바 기반의 설정 파일로 인식함|
</br>

**3) src/main/resources 디렉터리**   
|폴더 및 파일|설명|
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
### 5. MySQL 연동
**1) 데이터 소스 설정**   
ㆍ 스프링 부트에서 데이터 소스 설정 방법은 두 가지가 존재   
ㆍ "@Bean" 애너테이션 또는 "application.properties" 파일을 이용 가능 (이번 프로젝트에서는 후자의 방법을 사용)   
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

|속성|설명|
|---|---|
|jdbc-url|데이터베이스의 주소를 의미하며, 포트 번호뒤의 board는 생성한 스키마의 이름|
|username|MySQL의 아이디를 의미|
|password|MySQL의 패스워드를 의미|
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
|@ConfigurationProperties|@PropertySource에 지정된 파일에서 prefix에 해당하는 설정을 읽어들여 메서드에 매핑|
|hikariConfig|커넥션 풀 라이브러리 중 하나인 히카리CP 객체를 생성|
|dataSource|커넥션 풀을 지원하기 위한 인터페이스인 데이터 소스 객체를 생성|
|sqlSessionFactory|데이터베이스 커넥션과 SQL 실행에 대한 중요한 역할을 하는 SqlSessionFactory 객체를 생성|
|setMapperLocations|getResources 메서드의 인자로 지정된 패턴에 포함하는 XML Mapper를 인식하는 |
|sqlSession|마이바티스와 스프링 연동 모듈의 핵심인 sqlSessionTemplate 객체를 생성|
</br>

---
### 6. 게시판 CRUD 처리
**1) 게시판 테이블 생성**   
ㆍ 게시판 테이블은 데이터베이스에 저장될 게시물에 대한 정보를 정의한 것   
ㆍ MySQL Workbench을 실행하고 스키마를 생성한 후 아래에 스크립트를 실행
<details>
    <summary><b>코드 보기</b></summary>
	
```
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
ㆍ 도메인 클래스는 위에서 생성한 게시판 테이블에 대한 구조화 역할을 함   
ㆍ 보통 도메인 클래스는 읽기 전용을 의미하는 xxxVO와 데이터의 저장 및 전송은 의미하는 xxxDTO로 네이밍을 함   
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
	
|애너테이션 및 메서드|설명|
|---|---|
|@Mapper|마이바티스는 인터페이스에 @Mapper만 지정을 해주면 XML Mapper에서 메서드의 이름과 일치하는 SQL 문을 찾아 실행해줌|
|insert|게시글을 생성하는 INSERT 쿼리를 호출하는 메소드|
|selectBoardDetail|하나의 게시글을 조회하는 SELECT 쿼리를 호출하는 메소드|
|updateBoard|게시글을 수정하는 UPDATE 쿼리를 호출하는 메소드|
|deleteBoard|게시글을 삭제하는 DELETE 쿼리를 호출하는 메소드|
|selectBoardList|게시글 목록을 조회하는 SELECT 쿼리를 호출하는 메소드|
|selectBoardTotalCount|삭제 여부가 'N'으로 지정된 게시글의 개수를 조회하는 SELECT 쿼리를 호출하는 메소드|
</br>

**4) 마이바티스 XML Mapper 생성**   
ㆍ XML Mapper는 BoardMapper 인터페이스와 SQL문의 연결을 위한 역할이며, 실제 SQL 쿼리 문이 정의됨     
ㆍ src/main/resources 디렉터리에 mappers 폴더 생성 후 BoardMapper.xml 파일을 추가   
ㆍ BoardMapper.xml 파일에 아래에 소스코드를 작성
<details>
    <summary><b>코드 보기</b></summary>
	
```sql
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.board.mapper.BoardMapper">

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

|태그 및 속성|설명|
|---|---|
|&lt;mapper&gt;|해당 태그 namespace 속성에는 SQL 쿼리문과 매핑을 위한 BoardMapper 인터페이스의 경로가 지정|
|&lt;sql&gt;|공통으로 사용되거나 반복적으로 사용되는 테이블의 컬럼을 SQL 조각으로 정의하여 boardColumns라는 이름으로 사용|
|&lt;include&gt;|<sql>태그엥 정의한 boardColumns의 참조를 위해 사용되는 태그|
|parameterType|쿼리문 실행에 필요한 파라미터의 타입을 해당 속성에 지정|
|resultType|쿼리문 실행 결과에 해당하는 타입을 지정|
|파라미터 표현식|전달받은 파라미터는 #{} 표현식을 사용해서 처리|
</br>

**5) 마이바티스 SELECT 컬럼과 DTO 멤버 변수의 매핑**   
ㆍ BoardMapper.xml의 boardColumns SQL 조각은 스네이크 케이스를 사용하고 있고, BoardDTO 클래스의 멤버 변수는 카멜 케이스를 사용   
ㆍ 서로 다른 표현식에 사용은 추가 설정을 통해 자동으로 매칭이 되도로 처리가 가능   
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
		factoryBean.setTypeAliasesPackage("com.board.domain");
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

|메서드|설명|
|---|---|
|setTypeAliasesPackage|BoardMapper XML의 parameterType과 resultTrpe은 클래스의 풀 패키지 경로가 포함이 되어야함, 해당메서드를 사용함으로써 풀 패키지 경로 생략 가능|
|setConfiguration|마이바티스 설정과 관련된 빈을 설정 파일로 지정|
|mybatisConfig|application.properties 파일에서 mybatis.configuration으로 시작하는 모든 설정을 읽어 들여 빈으로 등록|
</br>
