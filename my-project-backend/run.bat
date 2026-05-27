@echo off
set M2_REPO=C:\Users\admin\.m2\repository
set CLASSPATH=d:\project\个人网站\--GodplaceBlog---main\my-project-backend\target\classes
set CLASSPATH=%CLASSPATH%;%M2_REPO%\org\springframework\boot\spring-boot\3.4.3\spring-boot-3.4.3.jar
set CLASSPATH=%CLASSPATH%;%M2_REPO%\org\springframework\boot\spring-boot-autoconfigure\3.4.3\spring-boot-autoconfigure-3.4.3.jar
set CLASSPATH=%CLASSPATH%;%M2_REPO%\org\springframework\spring-core\6.1.4\spring-core-6.1.4.jar
set CLASSPATH=%CLASSPATH%;%M2_REPO%\org\springframework\spring-context\6.1.4\spring-context-6.1.4.jar
set CLASSPATH=%CLASSPATH%;%M2_REPO%\com\baomidou\mybatis-plus\mybatis-plus-spring-boot3-starter\3.5.5\mybatis-plus-spring-boot3-starter-3.5.5.jar
set CLASSPATH=%CLASSPATH%;%M2_REPO%\mysql\mysql-connector-j\8.0.33\mysql-connector-j-8.0.33.jar
set CLASSPATH=%CLASSPATH%;%M2_REPO%\org\springframework\security\spring-security-web\6.2.2\spring-security-web-6.2.2.jar
set CLASSPATH=%CLASSPATH%;%M2_REPO%\org\springframework\security\spring-security-config\6.2.2\spring-security-config-6.2.2.jar
set CLASSPATH=%CLASSPATH%;%M2_REPO%\io\jsonwebtoken\jjwt-api\0.12.5\jjwt-api-0.12.5.jar
set CLASSPATH=%CLASSPATH%;%M2_REPO%\io\jsonwebtoken\jjwt-impl\0.12.5\jjwt-impl-0.12.5.jar
set CLASSPATH=%CLASSPATH%;%M2_REPO%\io\jsonwebtoken\jjwt-jackson\0.12.5\jjwt-jackson-0.12.5.jar
set CLASSPATH=%CLASSPATH%;%M2_REPO%\org\projectlombok\lombok\1.18.30\lombok-1.18.30.jar

java -cp %CLASSPATH% org.example.MyProjectBackendApplication
pause
