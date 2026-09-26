# 教室预约系统 lab-reserve

学生按日期和时段预约教室，管理员审核后生效。

本项目用于练习后端常见能力：业务状态机、数据库事务与行锁、缓存失效，以及一个可运行的 Web 页面。

在线演示：暂无（本地启动见下方）  
演示视频：暂无（建议补一条 60 秒录屏后把链接贴在这里）

---

## 功能

- 注册 / 登录，学生与管理员分角色
- 教室列表、搜索；管理员可新增教室、下架教室
- 学生提交预约，默认进入 `PENDING`
- 管理员审核：`APPROVED` / `REJECTED`
- 学生可取消自己的预约
- 同一教室、时间交叉的预约会被拒绝
- 教室列表使用 Redis 缓存，教室变更后自动失效

---

## 技术栈

| 层 | 选型 |
|---|---|
| 后端 | Spring Boot 3.4.5、Java 17、Spring Web、Spring Data JPA |
| 数据库 | MySQL 8 |
| 缓存 | Redis + Spring Cache |
| 前端 | `src/main/resources/static/index.html` 单页 |

---

## 业务与设计

```text
浏览器  →  Spring Boot :8080
              ├─ MySQL    用户 / 教室 / 预约（真实数据）
              └─ Redis    教室列表缓存 rooms::all（10 分钟）
```

**预约流**

1. 学生提交预约 → 状态 `PENDING`
2. 管理员通过 → `APPROVED`；拒绝 → `REJECTED`
3. 学生可取消未拒绝的预约 → `CANCELLED`

**防超订（MySQL，不是 Redis 锁）**

预约和审核通过时会：

1. 开启事务
2. 对教室行加悲观锁 `SELECT ... FOR UPDATE`
3. 查询是否存在时间重叠（忽略已取消、已拒绝）
4. 无重叠才写入或改状态

因此同一间教室的并发预约会排队；后到的请求能看到先提交的记录，避免两个人同时约上同一时段。

**教室列表缓存（Redis）**

- 第一次访问 `GET /api/rooms` 查 MySQL，写入 Redis，键名 `rooms::all`
- 10 分钟过期
- 新增 / 修改 / 下架教室时清空该缓存
- Redis 未启动时，教室列表接口会失败。可先启动 Redis，或把 `spring.cache.type` 临时设为 `none`

---

## 演示账号

应用第一次启动时，若库中没有这些账号会自动写入：

| 学号 | 密码 | 角色 | 说明 |
|---|---|---|---|
| `admin` | `admin123` | 管理员 | 可新增教室、审核预约 |
| `2024001` | `123456` | 学生 | 可预约、取消 |

空库时还会写入 3 间示例教室：`A101`、`B203`、`C301`。

本地练习可以继续用这组账号。如果以后把系统挂到公网，请先改掉默认密码，并补登录态（当前接口仍信任前端传来的 `userId` / `adminId`）。

---

## 环境

- JDK 17（用 JDK 21 本地启动也可以）
- Maven，或直接用仓库里的 `./mvnw` / `mvnw.cmd`
- MySQL 8，库名 `lab_reserve`
- Redis，默认 `127.0.0.1:6379`

Windows 上 Redis 若装在 WSL 里，需保证 **Windows 的** `127.0.0.1:6379` 能通，应用才能连上。可在 PowerShell 检查：

```powershell
Test-NetConnection 127.0.0.1 -Port 6379
```

`TcpTestSucceeded` 为 `True` 再启动后端。

---

## 启动

### 1. 建库

把账号改成你自己的 MySQL 用户：

```bash
mysql -u root -p < src/main/resources/db/init.sql
```

### 2. 本地配置

仓库已忽略 `src/main/resources/application-local.properties`。复制一份并改密码，例如：

```properties
spring.datasource.username=root
spring.datasource.password=你的MySQL密码
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.cache.type=redis
```

主配置 `application.properties` 里是 `spring.profiles.active=local`，会读取这个文件。

没有 Redis 时，在本地配置里写：

```properties
spring.cache.type=none
```

教室列表会每次查数据库，但页面可以先跑起来。

### 3. 启动 Redis 与应用

```bash
# Redis 已在本机 6379 监听的前提下
./mvnw spring-boot:run
```

Windows 也可用 IDEA 直接运行 `LabReserveApplication`。

### 4. 打开页面

浏览器访问 [http://localhost:8080](http://localhost:8080)

健康检查：

```text
GET http://localhost:8080/api/health
```

应返回 `{"status":"ok"}`。

启动后第一次打开教室列表，Redis 中应出现键 `rooms::all`：

```bash
redis-cli keys '*rooms*'
```

---

## 截图

把下面三张图放到 `docs/` 后取消注释即可在 GitHub 上显示。

```text
docs/login.png     登录 / 注册
docs/rooms.png     教室列表与预约
docs/review.png    管理员审核
```

```markdown
![登录](docs/login.png)
![教室列表](docs/rooms.png)
![审核](docs/review.png)
```

---

## 主要接口

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/api/auth/register` | 注册 |
| POST | `/api/auth/login` | 登录 |
| DELETE | `/api/auth/me?userId=` | 注销账号（会删掉该用户预约） |
| GET | `/api/rooms` | 教室列表（走缓存） |
| POST / PUT / DELETE | `/api/rooms`、`/api/rooms/{id}` | 教室维护，删除为下架 |
| POST | `/api/reservations` | 提交预约 |
| GET | `/api/reservations/me?userId=` | 我的预约 |
| GET | `/api/reservations/pending` | 待审核 |
| POST | `/api/reservations/{id}/cancel?userId=` | 取消 |
| POST | `/api/reservations/{id}/approve?adminId=` | 通过 |
| POST | `/api/reservations/{id}/reject?adminId=` | 拒绝 |

---

## 目录

```text
src/main/java/com/labreserve/
  user/           注册登录、用户
  room/           教室与列表缓存
  reservation/    预约、审核、行锁
  common/         缓存配置、异常处理
src/main/resources/static/index.html
src/main/resources/db/init.sql
```

---

## 已知限制

这些是当前版本有意未做完的部分，面试时可以直接说明：

- 密码明文存储和比对，尚未做哈希
- 没有 Session / JWT，接口通过请求里的 `userId`、`adminId` 识别身份
- 暂无 Docker Compose 与公网部署
- 自动化测试几乎为空，并发超订目前靠手工验证
- Redis 不可用时，带 `@Cacheable` 的教室列表会报连接失败

后续优先：密码哈希、登录态、`docker-compose`、一条并发预约测试、再考虑公网演示。

---

## 简历可写的一句话

教室预约系统（Spring Boot 3 + MySQL + Redis）：实现学生预约与管理员审核；使用事务和教室行级锁避免同一时段并发超订；教室列表用 Redis 缓存，变更时失效。
