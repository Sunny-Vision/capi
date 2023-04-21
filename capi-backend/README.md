## backend

The Java backend application of CAPI in Census and Statistic Department

Development Site
	
	Development site: http://192.0.0.123:8080/capi
	CI server: http://192.0.0.123:8081
	
	Server (Windows Server with Tomcat 8):
	Hostname: 192.0.0.123
	Username: .\administrator
	Password: kinetix
	
	Database (SQL Server 2012):
	Address: 192.0.0.86
	Login: capiuser
	password: kinetix@2015
	
Google developer console account (for development)
	
	Account: capi.census@gmail.com // abcd!@#$
	Server API Key: AIzaSyB8W-ch7u2FpbIYHQRywsISZEA6YMmYY2o
	Broswer API Key: AIzaSyBlkSHi3pt2Ufx_osi1iWvp2q3m82GzaYY
	專案 ID: capi-1053	
	專案 ID: 711780150719

Authority Level
	
	(1): Section head
	(2): Field Team head
	(4): Field Supervisor
	(8): Field Allocation Coordinator
	(16): Field Officers
	(32): Indoor  Allocation Coordinator
	(64): Indoor Review
	(128): Indoor Data Conversion
	(256): Business Data Administrator
	(512): System Administrator
	(1024): Indoor Supervisor
	(2048): Business Data Viewer

Method Security

	* Role access right
	@Secured("UF1001") // function code without '-'
	@PreAuthorize(“hasRole(‘UF1001’)”)	
	
	* Permission access right
	@PreAuthorize("hasPermission(#user, 2)")	
	Reference: http://docs.spring.io/spring-security/site/docs/3.0.x/reference/el-access.html


Web Security

	* Include taglib
	<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
	Check Role
	<sec:authorize access="hasRole('UF2201')">Authorized Message</sec:authorize>	
	
	* Check Permission
	<sec:authorize access="hasPermission(#user, 4)"> Authorized Message</sec:authorize>	
	Reference: http://docs.spring.io/spring-security/site/docs/current/reference/html/taglibs.html


System Message
	
	* In Java
	@Resource(name="messageSource")
	MessageSource messageSource;
	...
	String message = messageSource.getMessage("E00018", null, locale)	
	
	* In JSP
	<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
	...
	<sprng:message code="E00017" />
	
Point to note
	
	* Since obfuscate is required in project requirement, all beans should define the bean name explicitly. 
	- Therefore the value in the in annotation should not be empty
	* For the sake of package scanning and obfuscate, all classes should be defined in delegated package.
	* The content under capi.entity package should always remain unchanged unless there is any bug existed
	* Most important beans are configured when the application start up. Therefore, always use dependency injection to get the configured bean. 
	* Beans hosted in spring container are singleton by default. Pay attention not to keep state in your singleton bean	
	* Always use log4j to log the message and catch the exception
	* Always use hibernate method to insert, update and delete the record. Otherwise, audit log should be done manually
	* Default account admin // admin

Application Configuration
	
	8 profiles have been setup in Maven to build for different environment
	dev – development environment (dev.properties)
	sal - SAL development environment (sal.properties)
	sit – system integration testing environment(CAPI) (sit.properties)
	uat – user acceptance testing environment (uat.properties)
	prod – production environment (prod.properties)
	dr - DR site environment (dr.properties)
	
	sit2 - system integration testing environment(devcapi) (sit2.properties)
	uat2 - user acceptance testing environment (uatcapi) (uat2.properties)

Build with profile	
	
	Right click on capi.master project and select:
	RunAs > Maven build …
	In the dialog, input “clean install” in Goals
	Input Profiles (sit, uat, prod) if you want to build other than development environment
	Click Run
	After the build is finished successfully, you should be able to find the war file in capi.master\capi.web\target
	capi-{profile}-1.0.0-BUILD-SNAPSHOT.war is the original build
	capi-{profile}-1.0.0-BUILD-SNAPSHOT-obfuscated.war is obfuscated build

	