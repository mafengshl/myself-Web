$classpathFile = "d:\project\个人网站\--GodplaceBlog---main\my-project-backend\classpath.txt"

# 创建classpath文件
@("d:\project\个人网站\--GodplaceBlog---main\my-project-backend\target\classes") | Out-File -FilePath $classpathFile -Encoding UTF8

# 添加所有jar文件
Get-ChildItem -Path "C:\Users\admin\.m2\repository" -Recurse -Filter "*.jar" | ForEach-Object {
    Add-Content -Path $classpathFile -Value $_.FullName
}

Write-Host "Classpath file created with $(@(Get-Content $classpathFile).Count) entries"

# 使用@file方式启动
& "D:\Dev\JavaJdk\bin\java.exe" -cp "@$classpathFile" org.example.MyProjectBackendApplication