# 数据回放平台

一个基于 Java + Vue 的数据回放平台，支持文件上传、Arrow 格式转换和数据可视化功能。

## 功能特点

- 支持 CSV 文件上传和转换为 Apache Arrow 格式
- 数据可视化和回放功能
- 支持数据降采样处理
- 支持多列数据同时展示
- 可调节回放速度
- 美观的用户界面

## 技术栈

### 后端
- Java 1.8
- Spring Boot 2.7.x
- Apache Arrow
- Apache Commons CSV

### 前端
- Vue 3
- Element Plus
- ECharts
- Axios

## 快速开始

### 后端启动
1. 进入 backend 目录
```bash
cd backend
```

2. 使用 Maven 构建项目
```bash
mvn clean package
```

3. 运行 JAR 文件
```bash
java -jar target/data-replay-platform-1.0-SNAPSHOT.jar
```

### 前端启动
1. 进入 frontend 目录
```bash
cd frontend
```

2. 安装依赖
```bash
npm install
```

3. 启动开发服务器
```bash
npm run serve
```

## 使用说明

1. 打开浏览器访问 http://localhost:8080
2. 在首页上传 CSV 文件
3. 文件上传成功后，点击"查看数据"进入可视化界面
4. 在可视化界面可以：
   - 选择要显示的数据列
   - 调整采样率
   - 控制数据回放（开始/暂停）
   - 调节回放速度

## 项目结构

```
data-replay-platform/
├── backend/                # 后端项目
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       └── resources/
│   └── pom.xml
├── frontend/              # 前端项目
│   ├── src/
│   │   ├── components/
│   │   ├── router/
│   │   ├── App.vue
│   │   └── main.js
│   └── package.json
└── README.md
```

## 开发说明

### API 接口

1. 文件上传
- URL: `/api/data/upload`
- 方法: POST
- 参数: CSV 文件

2. 获取数据
- URL: `/api/data/{fileId}`
- 方法: GET
- 参数: 
  - fileId: 文件标识
  - samplingRate: 采样率（可选）

3. 获取数据信息
- URL: `/api/data/{fileId}/info`
- 方法: GET
- 参数: 
  - fileId: 文件标识

## 贡献指南

1. Fork 本仓库
2. 创建您的特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交您的更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开一个 Pull Request

## 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件