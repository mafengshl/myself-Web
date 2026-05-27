$m2Repo = "C:\Users\admin\.m2\repository"
$targetDir = "d:\project\个人网站\--GodplaceBlog---main\my-project-backend\target\classes"

$classpath = "$targetDir"

Get-ChildItem -Path $m2Repo -Recurse -Filter "*.jar" | ForEach-Object {
    $classpath += ";$($_.FullName)"
}

Write-Host "Classpath built, starting application..."

java -cp $classpath org.example.MyProjectBackendApplication
