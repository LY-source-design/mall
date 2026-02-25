# Mall 电商系统

一个基于 Spring Boot 3 的完整电商系统，包含用户管理、商品管理、购物车、订单系统和秒杀功能。

## 项目简介

本项目是一个功能完善的电商平台后端系统，采用前后端分离架构，提供 RESTful API 接口。系统集成了多种主流技术，包括 Redis 缓存、RabbitMQ 消息队列、Elasticsearch 全文搜索、JWT 认证、阿里云 OSS 文件存储等，能够支持高并发场景下的业务需求。

## 技术栈

### 后端框架
- **Spring Boot 3.0.5** - 基础框架
- **Spring Web** - RESTful API 开发
- **Spring AOP** - 面向切面编程

### 数据持久层
- **MyBatis-Plus 3.5.14** - ORM 框架
- **MySQL** - 关系型数据库

### 缓存与消息队列
- **Redis** - 缓存与分布式锁
- **Redisson 3.45.1** - 分布式 Redis 客户端
- **RabbitMQ** - 消息队列（支持延迟队列）

### 搜索引擎
- **Elasticsearch 7.12.1** - 全文搜索引擎

### 安全与认证
- **JJWT 0.12.6** - JWT 令牌生成与解析
- **Spring Security Crypto** - 密码加密

### 文件存储
- **阿里云 OSS 3.17.4** - 对象存储服务

### 工具库
- **Hutool 5.8.40** - Java 工具类库
- **Lombok 1.18.30** - 简化 Java 代码
- **Knife4j 4.5.0** - API 文档生成

## 项目结构

```
mall/
├── src/main/
│   ├── java/pers/ly/mall/
│   │   ├── MallApplication.java          # 启动类
│   │   ├── common/                       # 公共模块
│   │   │   ├── annotation/               # 自定义注解
│   │   │   ├── config/                   # 配置类
│   │   │   ├── constant/                 # 常量定义
│   │   │   ├── context/                  # 上下文管理
│   │   │   ├── entity/                   # 实体类
│   │   │   ├── exception/                # 异常类
│   │   │   ├── handler/                  # 处理器
│   │   │   ├── interceptor/              # 拦截器
│   │   │   ├── properties/               # 属性配置
│   │   │   └── utils/                    # 工具类
│   │   ├── good/                         # 商品模块
│   │   ├── order/                        # 订单模块
│   │   ├── seckill/                      # 秒杀模块
│   │   ├── shoppingcar/                  # 购物车模块
│   │   └── user/                         # 用户模块
│   └── resources/
│       ├── application.yaml              # 主配置文件
│       ├── application-dev.yaml          # 开发环境配置
│       ├── lua/                          # Lua 脚本
│       └── mapper/                       # MyBatis Mapper XML
└── pom.xml                                # Maven 配置文件
```

## 核心功能模块

### 1. 用户模块
- 用户注册与登录
- JWT 双令牌认证（Access Token + Refresh Token）
- 令牌刷新机制
- 个人信息管理
- 头像上传（阿里云 OSS）
- 账号锁定功能（管理员）

### 2. 商品模块
- 商品增删改查
- 商品分类管理
- 全文搜索（Elasticsearch）
- 自动补全建议
- 商品上下架管理
- 销量统计

### 3. 购物车模块
- 添加商品到购物车
- 修改商品数量
- 移除商品
- 基于 Redis 的高性能存储

### 4. 订单模块
- 订单生成
- 订单支付
- 订单状态管理
- 历史订单查询
- 订单超时自动取消（延迟队列）

### 5. 秒杀模块
- 秒杀商品管理
- 抢购资格获取
- Lua 脚本保证原子性
- 防止超卖
- 异步订单生成

## 快速开始

### 环境要求
- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- RabbitMQ 3.8+
- Elasticsearch 7.12+

### 安装步骤

1. **克隆项目**
   ```bash
   git clone [项目地址]
   cd mall
   ```

2. **修改配置文件**
   
   编辑 `src/main/resources/application-dev.yaml`，配置数据库、Redis、RabbitMQ 等连接信息：
   ```yaml
   mall:
     datasource:
       url: jdbc:mysql://localhost:3306/db_mall
       username: root
       password: your_password
     redis:
       host: localhost
       port: 6379
       password: your_redis_password
     rabbitmq:
       host: localhost
       port: 5672
       username: admin
       password: your_rabbitmq_password
   ```

3. **配置阿里云 OSS**
   
   设置环境变量或在配置文件中填写阿里云 OSS 访问凭证：
   ```bash
   export OSS_ACCESS_KEY_ID=your_access_key_id
   export OSS_ACCESS_KEY_SECRET=your_access_key_secret
   ```

4. **初始化数据库**
   
   创建数据库 `db_mall` 并执行 SQL 脚本（如有）。

5. **启动项目**
   
   使用 Maven 启动：
   ```bash
   mvn spring-boot:run
   ```
   
   或直接运行启动类 `MallApplication.java`

6. **访问 API 文档**
   
   启动成功后，访问 Knife4j 文档：
   ```
   http://localhost:9090/doc.html
   ```

## 核心技术实现

### JWT 认证机制
- 双令牌设计：Access Token（短期）+ Refresh Token（长期）
- 令牌自动刷新
- 拦截器统一处理认证

### 秒杀系统设计
- **Redis 预加载**：秒杀商品提前加载到 Redis
- **Lua 脚本**：保证库存扣减的原子性
- **资格队列**：防止重复抢购
- **延迟消息**：超时未支付自动回滚库存
- **异步订单**：支付成功后异步生成订单，降低数据库压力

### 订单超时处理
- 基于 RabbitMQ 死信队列实现延迟消息
- 订单创建后发送延迟消息
- 超时未支付自动取消订单

### 全文搜索
- 商品数据同步到 Elasticsearch
- 支持商品名称、分类等多字段搜索
- 自动补全功能

## API 接口说明

### 用户相关
- `POST /user/register` - 用户注册
- `POST /user/login` - 用户登录
- `GET /user/me` - 获取个人信息
- `POST /user/avatar/upload` - 上传头像
- `POST /user/me` - 修改个人信息

### 商品相关
- `POST /good` - 添加商品（管理员）
- `POST /good/list` - 查询商品列表
- `GET /good/suggest` - 自动补全
- `GET /good/{id}` - 查询商品详情
- `PUT /good/{id}` - 更新商品上下架状态

### 购物车相关
- `POST /shoppingCar/add` - 添加商品到购物车
- `DELETE /shoppingCar/reduce/{goodId}` - 减少商品数量
- `DELETE /shoppingCar/all/{goodId}` - 移除商品
- `GET /shoppingCar/list` - 查看购物车

### 订单相关
- `GET /order` - 订单核对
- `POST /order` - 生成订单
- `POST /order/pay` - 支付订单
- `GET /order/list` - 查询历史订单
- `POST /order/cancel` - 取消订单

### 秒杀相关
- `POST /seckill/add` - 添加秒杀商品（管理员）
- `GET /seckill/list` - 查询当天秒杀活动
- `POST /seckill` - 抢购购买资格
- `POST /seckill/pay` - 支付秒杀订单

## 配置说明

### 端口配置
- 应用端口：9090
- 可在 `application.yaml` 中修改 `server.port`

### 文件上传限制
- 最大文件大小：2MB
- 支持格式：jpg、jpeg、png、gif

### 日志配置
- Mapper 层日志级别：debug
- MyBatis 日志级别：trace

## 开发规范

### 代码规范
- 使用 Lombok 简化代码
- 统一异常处理
- 统一返回格式（Result）
- 使用 AOP 实现日志记录

### 接口文档
- 使用 Swagger 注解标注接口
- Knife4j 提供增强的文档展示
- 生产环境需关闭文档功能

## 常见问题

### 1. Redis 连接失败
检查 Redis 服务是否启动，确认配置文件中的 host、port、password 是否正确。

### 2. RabbitMQ 连接失败
确认 RabbitMQ 服务是否启动，检查虚拟主机配置。

### 3. Elasticsearch 连接失败
检查 Elasticsearch 服务是否运行，确认版本兼容性。

### 4. 文件上传失败
检查阿里云 OSS 配置是否正确，确认访问凭证有效。

## 许可证

本项目仅供学习交流使用。

## 联系方式

如有问题，请提交 Issue 或联系开发者。

---

**注意：** 请勿在生产环境中使用默认配置，务必修改数据库密码、Redis 密码、JWT 密钥等敏感信息。
