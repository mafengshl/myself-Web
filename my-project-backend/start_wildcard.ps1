$jarDir = "C:\Users\admin\.m2\repository"
$classpath = "d:\project\个人网站\--GodplaceBlog---main\my-project-backend\target\classes"

Get-ChildItem -Path $jarDir -Recurse -Filter "*.jar" | ForEach-Object {
    $classpath += ";$($_.FullName)"
}

Write-Host "Classpath length: $($classpath.Length)"
Write-Host "Starting application..."

& "D:\Dev\JavaJdk\bin\java.exe" -cp $classpath org.example.MyProjectBackendApplication