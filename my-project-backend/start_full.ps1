$basePath = "C:\Users\admin\.m2\repository"

$classpath = @(
    "d:\project\个人网站\--GodplaceBlog---main\my-project-backend\target\classes",
    "$basePath\org\springframework\boot\spring-boot\3.4.3\spring-boot-3.4.3.jar",
    "$basePath\org\springframework\boot\spring-boot-autoconfigure\3.4.3\spring-boot-autoconfigure-3.4.3.jar",
    "$basePath\org\springframework\spring-core\6.1.4\spring-core-6.1.4.jar",
    "$basePath\org\springframework\spring-context\6.1.4\spring-context-6.1.4.jar",
    "$basePath\org\springframework\spring-beans\6.1.4\spring-beans-6.1.4.jar",
    "$basePath\org\springframework\spring-expression\6.1.4\spring-expression-6.1.4.jar",
    "$basePath\org\springframework\spring-web\6.1.4\spring-web-6.1.4.jar",
    "$basePath\org\springframework\spring-webmvc\6.1.4\spring-webmvc-6.1.4.jar",
    "$basePath\org\springframework\spring-jcl\6.1.4\spring-jcl-6.1.4.jar",
    "$basePath\org\springframework\security\spring-security-web\6.2.2\spring-security-web-6.2.2.jar",
    "$basePath\org\springframework\security\spring-security-config\6.2.2\spring-security-config-6.2.2.jar",
    "$basePath\org\springframework\security\spring-security-core\6.2.2\spring-security-core-6.2.2.jar",
    "$basePath\com\baomidou\mybatis-plus\mybatis-plus-spring-boot3-starter\3.5.5\mybatis-plus-spring-boot3-starter-3.5.5.jar",
    "$basePath\mysql\mysql-connector-j\8.0.33\mysql-connector-j-8.0.33.jar",
    "$basePath\io\jsonwebtoken\jjwt-api\0.12.5\jjwt-api-0.12.5.jar",
    "$basePath\io\jsonwebtoken\jjwt-impl\0.12.5\jjwt-impl-0.12.5.jar",
    "$basePath\io\jsonwebtoken\jjwt-jackson\0.12.5\jjwt-jackson-0.12.5.jar",
    "$basePath\org\projectlombok\lombok\1.18.30\lombok-1.18.30.jar",
    "$basePath\org\mybatis\mybatis\3.5.15\mybatis-3.5.15.jar",
    "$basePath\com\baomidou\mybatis-plus\mybatis-plus-core\3.5.5\mybatis-plus-core-3.5.5.jar",
    "$basePath\com\baomidou\mybatis-plus\mybatis-plus-annotation\3.5.5\mybatis-plus-annotation-3.5.5.jar",
    "$basePath\org\mybatis\mybatis-spring\3.0.3\mybatis-spring-3.0.3.jar",
    "$basePath\com\fasterxml\jackson\databind\jackson-databind\2.16.1\jackson-databind-2.16.1.jar",
    "$basePath\com\fasterxml\jackson\core\jackson-core\2.16.1\jackson-core-2.16.1.jar",
    "$basePath\com\fasterxml\jackson\annotations\jackson-annotations\2.16.1\jackson-annotations-2.16.1.jar",
    "$basePath\javax\servlet\javax.servlet-api\4.0.1\javax.servlet-api-4.0.1.jar",
    "$basePath\org\springframework\spring-tx\6.1.4\spring-tx-6.1.4.jar",
    "$basePath\org\springframework\spring-aop\6.1.4\spring-aop-6.1.4.jar",
    "$basePath\org\springframework\spring-jdbc\6.1.4\spring-jdbc-6.1.4.jar",
    "$basePath\org\aspectj\aspectjweaver\1.9.22.1\aspectjweaver-1.9.22.1.jar",
    "$basePath\com\baomidou\mybatis-plus\mybatis-plus-spring-boot3-autoconfigure\3.5.5\mybatis-plus-spring-boot3-autoconfigure-3.5.5.jar"
)

$cp = $classpath -join ";"

Write-Host "Total jars: $($classpath.Length)"
Write-Host "Classpath length: $($cp.Length)"

& "D:\Dev\JavaJdk\bin\java.exe" -cp $cp org.example.MyProjectBackendApplication
