$cp = "d:\project\个人网站\--GodplaceBlog---main\my-project-backend\target\classes;" +`
    "C:\Users\admin\.m2\repository\org\springframework\boot\spring-boot\3.4.3\spring-boot-3.4.3.jar;" +`
    "C:\Users\admin\.m2\repository\org\springframework\boot\spring-boot-autoconfigure\3.4.3\spring-boot-autoconfigure-3.4.3.jar;" +`
    "C:\Users\admin\.m2\repository\org\springframework\spring-core\6.1.4\spring-core-6.1.4.jar;" +`
    "C:\Users\admin\.m2\repository\org\springframework\spring-context\6.1.4\spring-context-6.1.4.jar;" +`
    "C:\Users\admin\.m2\repository\org\springframework\spring-beans\6.1.4\spring-beans-6.1.4.jar;" +`
    "C:\Users\admin\.m2\repository\org\springframework\spring-expression\6.1.4\spring-expression-6.1.4.jar;" +`
    "C:\Users\admin\.m2\repository\org\springframework\spring-web\6.1.4\spring-web-6.1.4.jar;" +`
    "C:\Users\admin\.m2\repository\org\springframework\spring-webmvc\6.1.4\spring-webmvc-6.1.4.jar;" +`
    "C:\Users\admin\.m2\repository\org\springframework\security\spring-security-web\6.2.2\spring-security-web-6.2.2.jar;" +`
    "C:\Users\admin\.m2\repository\org\springframework\security\spring-security-config\6.2.2\spring-security-config-6.2.2.jar;" +`
    "C:\Users\admin\.m2\repository\com\baomidou\mybatis-plus\mybatis-plus-spring-boot3-starter\3.5.5\mybatis-plus-spring-boot3-starter-3.5.5.jar;" +`
    "C:\Users\admin\.m2\repository\mysql\mysql-connector-j\8.0.33\mysql-connector-j-8.0.33.jar;" +`
    "C:\Users\admin\.m2\repository\io\jsonwebtoken\jjwt-api\0.12.5\jjwt-api-0.12.5.jar;" +`
    "C:\Users\admin\.m2\repository\io\jsonwebtoken\jjwt-impl\0.12.5\jjwt-impl-0.12.5.jar;" +`
    "C:\Users\admin\.m2\repository\io\jsonwebtoken\jjwt-jackson\0.12.5\jjwt-jackson-0.12.5.jar;" +`
    "C:\Users\admin\.m2\repository\org\projectlombok\lombok\1.18.30\lombok-1.18.30.jar;" +`
    "C:\Users\admin\.m2\repository\org\mybatis\mybatis\3.5.15\mybatis-3.5.15.jar;" +`
    "C:\Users\admin\.m2\repository\com\baomidou\mybatis-plus\mybatis-plus-core\3.5.5\mybatis-plus-core-3.5.5.jar;" +`
    "C:\Users\admin\.m2\repository\com\baomidou\mybatis-plus\mybatis-plus-annotation\3.5.5\mybatis-plus-annotation-3.5.5.jar;" +`
    "C:\Users\admin\.m2\repository\org\mybatis\mybatis-spring\3.0.3\mybatis-spring-3.0.3.jar;" +`
    "C:\Users\admin\.m2\repository\com\fasterxml\jackson\databind\jackson-databind\2.16.1\jackson-databind-2.16.1.jar;" +`
    "C:\Users\admin\.m2\repository\com\fasterxml\jackson\core\jackson-core\2.16.1\jackson-core-2.16.1.jar;" +`
    "C:\Users\admin\.m2\repository\com\fasterxml\jackson\annotations\jackson-annotations\2.16.1\jackson-annotations-2.16.1.jar;" +`
    "C:\Users\admin\.m2\repository\javax\servlet\javax.servlet-api\4.0.1\javax.servlet-api-4.0.1.jar"

Write-Host "Classpath ready, starting application..."
java -cp $cp org.example.MyProjectBackendApplication
