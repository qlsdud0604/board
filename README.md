# Spring Boot를 활용한 간단한 기능을 제공하는 게시판의 제작
### 목차 :book:
1. [프로젝트 이름](#1-프로젝트-이름-memo)

2. [프로젝트 일정](#2-프로젝트-일정-calendar)

3. [기술 스택](#3-기술-스택-computer)

-----
### 1. 프로젝트 이름 :memo:
* **게시물 등록, 조회, 수정, 삭제가 가능한 게시판**

-----
### 2. 프로젝트 일정 :calendar:


-----
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

-----
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
