# 实验室 / 教室预约系统 lab-reserve

Spring Boot 3.4.5 + Java 17 + Spring Data JPA + MySQL。

预约默认 PENDING，管理员审核后变为 APPROVED / REJECTED
并发预约：事务 + 教室行锁 + 时间段重叠查询
教室列表走 Redis 缓存，新增/修改/下架会清缓存


## 环境

- JDK 17
- Maven（可用仓库自带 `./mvnw`）
- MySQL 8

## 启动

```bash
mysql -u root -p < src/main/resources/db/init.sql