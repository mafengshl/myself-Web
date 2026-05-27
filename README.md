# 📸 个人网站 - Godplace Blog

一个基于 Vue3 + Spring Boot 构建的现代化个人博客网站，包含相册、博客、音乐播放器等功能。

## ✨ 功能特性

- **相册空间** - 支持照片上传、浏览、点赞和删除
- **博客系统** - 文章发布、分类管理、评论功能
- **音乐播放器** - 在线音乐播放
- **用户认证** - 登录、注册、权限管理
- **后台管理** - 用户、文章、消息管理

## 🛠 技术栈

### 前端
- Vue 3 + TypeScript
- Element Plus UI 框架
- Vite 构建工具
- Pinia 状态管理

### 后端
- Spring Boot 3.2.x
- Spring Security + JWT 认证
- MyBatis Plus 数据库框架
- MySQL 数据库

## 🚀 快速开始

### 环境要求
- JDK 21+
- Node.js 20+
- MySQL 8.0+

### 后端启动

```bash
cd my-project-backend
# 使用 IntelliJ IDEA 运行 MyProjectBackendApplication.java
# 或使用 Maven
mvn spring-boot:run
```

### 前端启动

```bash
cd my-project-frontend
npm install
npm run dev
```

### 数据库配置

创建数据库并配置连接：

```sql
CREATE DATABASE suhualin CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

修改 `my-project-backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/suhualin
    username: root
    password: 123456
```

## 📁 项目结构

```
.
├── my-project-backend/        # 后端服务
│   ├── src/main/java/        # Java 源代码
│   ├── src/main/resources/   # 配置文件
│   └── pom.xml               # Maven 依赖
├── my-project-frontend/      # 前端应用
│   ├── src/                  # Vue 源代码
│   ├── package.json          # npm 依赖
│   └── vite.config.ts        # Vite 配置
└── README.md                 # 项目说明
```

## 🔐 默认账号

| 用户名 | 密码 | 权限 |
|--------|------|------|
| admin | 123456 | 管理员 |
| 馬小風 | 123456 | 普通用户 |

## 📝 开发说明

### 前端开发
```bash
# 开发模式
npm run dev

# 生产构建
npm run build

# 预览构建结果
npm run preview
```

### 后端开发
- 使用 IntelliJ IDEA 打开项目
- 配置 MySQL 数据源
- 运行 `MyProjectBackendApplication.java`

## 📄 License

MIT License

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

---

🌟 **感谢访问！**
