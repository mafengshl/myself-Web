Set-Location "d:\project\个人网站\--GodplaceBlog---main\my-project-backend"

$springBootJars = "C:\Users\admin\.m2\repository\org\springframework\boot\spring-boot\3.4.3\*"
$springBootAutoConfigJars = "C:\Users\admin\.m2\repository\org\springframework\boot\spring-boot-autoconfigure\3.4.3\*"
$springJars = "C:\Users\admin\.m2\repository\org\springframework\*\6.1.4\*"
$securityJars = "C:\Users\admin\.m2\repository\org\springframework\security\*\6.2.2\*"
$mybatisPlusJars = "C:\Users\admin\.m2\repository\com\baomidou\mybatis-plus\*\3.5.5\*"
$mybatisJars = "C:\Users\admin\.m2\repository\org\mybatis\*\*"
$mysqlJars = "C:\Users\admin\.m2\repository\mysql\mysql-connector-j\8.0.33\*"
$jjwtJars = "C:\Users\admin\.m2\repository\io\jsonwebtoken\*\0.12.5\*"
$jacksonJars = "C:\Users\admin\.m2\repository\com\fasterxml\jackson\*\2.16.1\*"
$lombokJar = "C:\Users\admin\.m2\repository\org\projectlombok\lombok\1.18.30\*"

$classpath = ".\target\classes;$springBootJars;$springBootAutoConfigJars;$springJars;$securityJars;$mybatisPlusJars;$mybatisJars;$mysqlJars;$jjwtJars;$jacksonJars;$lombokJar"

Write-Host "Classpath: $classpath"

& "D:\Dev\JavaJdk\bin\java.exe" -cp $classpath org.example.MyProjectBackendApplication