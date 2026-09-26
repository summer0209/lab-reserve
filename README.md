# 教室预约系统

学生按日期和时段预约教室，管理员审核后生效。

技术栈：Spring Boot 3.4.5 · Java 17 · Spring Data JPA · MySQL 8 · Redis · 单页前端

---

## 功能

- 注册 / 登录，学生与管理员分角色
- 浏览、搜索教室并提交预约（默认 `PENDING`）
- 管理员审核通过或拒绝；学生可取消本人预约
- 同一教室时段交叉的预约会被拒绝
- 管理员维护教室（新增、修改、下架）
- 教室列表使用 Redis 缓存，教室变更后失效

---

## 架构

    浏览器 → Spring Boot :8080
               ├─ MySQL   用户、教室、预约
               └─ Redis   教室列表缓存（rooms::all，10 分钟）

预约数据只写 MySQL。Redis 只缓存教室列表，不参与防超订。

**预约状态**

`PENDING` → `APPROVED` / `REJECTED`。学生可将未拒绝的预约改为 `CANCELLED`。待审核记录计入时段占用。

**并发控制**

预约与审核通过在同一事务内完成：对教室行加悲观锁（`SELECT ... FOR UPDATE`），再检查时间重叠（忽略 `CANCELLED`、`REJECTED`），无重叠才写入或改状态。同一教室的并发请求会串行化。这是数据库行锁，不是 Redis 锁。

**缓存**

`GET /api/rooms` 使用 Spring Cache，键为 `rooms::all`，TTL 10 分钟。教室新增、修改、下架时清空。Redis 不可用时，可将 `spring.cache.type` 设为 `none`，改为每次查库。

---

## 运行

### 方式一：Docker（推荐）

只需安装 Docker Desktop。不需要本机单独安装 MySQL、Redis、JDK。

    git clone <仓库地址>
    cd lab-reserve
    docker compose up --build

首次构建会下载镜像并编译，可能需要几分钟。日志出现 Started LabReserveApplication 后，浏览器打开：

http://localhost:8080

健康检查：

    curl http://localhost:8080/api/health

应返回 `{"status":"ok"}`。

本机 8080 已被占用时，先关掉占用该端口的程序，或把 docker-compose.yml 中的 `"8080:8080"` 改成 `"8081:8080"`，然后访问 http://localhost:8081 。

停止：

    docker compose down

连数据库数据一起清除：

    docker compose down -v

### 方式二：本地启动

依赖：JDK 17、Maven（或仓库自带 `./mvnw`）、MySQL 8、Redis（`127.0.0.1:6379`）。

    mysql -u root -p < src/main/resources/db/init.sql

连接信息见 `src/main/resources/application.properties`，可用未被提交的 `application-local.properties` 覆盖账号和密码。

启动 Redis 后：

    ./mvnw spring-boot:run

打开 http://localhost:8080

---

## 演示账号

首次启动且库中无对应记录时自动写入。

| 学号 | 密码 | 角色 |
|---|---|---|
| admin | admin123 | 管理员 |
| 2024001 | 123456 | 学生 |

同时写入示例教室 A101、B203、C301。

---

## 主要接口

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/api/auth/register` | 注册 |
| POST | `/api/auth/login` | 登录 |
| DELETE | `/api/auth/me?userId=` | 注销 |
| GET / POST | `/api/rooms` | 教室列表 / 新增 |
| GET / PUT / DELETE | `/api/rooms/{id}` | 查询 / 修改 / 下架 |
| POST | `/api/reservations` | 提交预约 |
| GET | `/api/reservations/me?userId=` | 我的预约 |
| GET | `/api/reservations/pending` | 待审核列表 |
| POST | `/api/reservations/{id}/cancel?userId=` | 取消 |
| POST | `/api/reservations/{id}/approve?adminId=` | 通过 |
| POST | `/api/reservations/{id}/reject?adminId=` | 拒绝 |

---

## 项目结构

    src/main/java/com/labreserve/
      user/           注册登录
      room/           教室与列表缓存
      reservation/    预约、审核、行锁
      common/         缓存与异常处理
    src/main/resources/static/index.html
    docker-compose.yml
    Dockerfile

---

## 现状

当前版本面向本地演示：

- 密码明文存储
- 无 Session / JWT，接口使用请求中的 `userId`、`adminId` 识别身份
- 并发场景以手工验证为主