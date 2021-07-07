# Spring Boot를 활용한 게시판의 제작
### 목차 :book:
1. [프로젝트 이름](#1-프로젝트-이름-memo)

2. [프로젝트 일정](#2-프로젝트-일정-calendar)

3. [기술 스택](#3-기술-스택-computer)

---
### 1. 프로젝트 이름 :memo:
* **게시물 등록, 조회, 수정, 삭제가 가능한 게시판**

---
### 2. 프로젝트 일정 :calendar:


---
### 3. 기술 스택 :computer:
* IDE
```
- IntelliJ
```
* Framework
```
- Spring Boot
- MyBatis
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

**1) src/main/java 디렉터리**   
ㆍ 클래스, 인터페이스 등 자바 파일이 위치하는 디렉터리   

**2) BoardApplication 클래스**   
ㆍ 해당 클래스 내의 main 메서드는 SpringApplication.run 메서드를 호출해서 웹 애플리케이션을 실행하는 역할을 함   
ㆍ "@SpringBootApplication"은 다음 3가지 애너테이션으로 구성   
|애너테이션|설명|
|---|---|
|@EnableAutoConfiguration|다양한 설정들의 일부가 자동으로 완료됨|
|@ComponentScan|자동으로 컴포넌트 클래스를 검색하고 스프링 애플리케이션 콘텍스트에 빈으로 등록함|
|@Configuration|해당 애너테이션이 선언된 클래스는 자바 기반의 설정 파일로 인식함|

**3) src/main/resources 디렉터리**   
|폴더 및 파일|설명|
|---|---|
|templates|템플릿 엔진을 활용한 동적 리소스 파일이 위치|
|static|css, fonts, images, plugin, scripts와 같은 정적 리소스 파일이 위치|
|application.properties|WAS의 설정이나, 데이터베이스 관련 설정 등을 지정해서 처리 가능|

**4) src/test/java 디렉터리**   
ㆍ BoardApplicationTest 클래스를 이용해서 개발 단계에 따라 테스트를 진행할 수 있음   

**5) build.gradle 파일**   
ㆍ 빌드에 사용이 될 애플리케이션의 버전, 각종 라이브러리 등 다양한 항목을 설정하고 관리할 수 있는 파일   

---
### 5. MySQL 연동
**1) 데이터 소스 설정**   
ㆍ 스프링 부트에서 데이터 소스 설정 방법은 두 가지가 존재   
ㆍ "@Bean" 애너테이션 또는 "application.properties" 파일을 이용 가능 (이번 프로젝트에서는 후자의 방법을 사용)   
```
spring.datasource.hikari.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.hikari.jdbc-url=jdbc:mysql://localhost:3306/board?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&useSSL=false
spring.datasource.hikari.username=username
spring.datasource.hikari.password=password
spring.datasource.hikari.connection-test-query=SELECT NOW() FROM dual
```
ㆍ src/main/resources 디렉터리의 application.properties 파일에 위 코드를 입력
|속성|설명|
|---|---|
|jdbc-url|데이터베이스의 주소를 의미하며, 포트 번호뒤의 board는 생성한 스키마의 이름|
|username|MySQL의 아이디를 의미|
|password|MySQL의 패스워드를 의미|
|connection-test-query|데이터베이스와의 연결이 정상적으로 이루어졌는지 확인하기 위한 SQL 쿼리문|

**2) 데이터베이스 설정**
ㆍ configuration 패키지 내 DBConfiguration 클래스를 통해 데이터베이스 설정 완료
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
|애너테이션 및 객체|설명|
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
|sqlSession|마이바티스와 스프링 연동 모듈의 핵심인 sqlSessionTemplate 객체를 생성|
