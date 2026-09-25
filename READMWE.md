# 实验室 / 教室预约系统 lab-reserve

Spring Boot 3.4.5 + Java 17 + Spring Data JPA + MySQL。

第 1 版功能：教室 CRUD、学生按日期+时间段预约、同一教室同一时段不能重复预约、查看/取消我的预约、学生/管理员登录。

## 环境

- JDK 17
- Maven（可用仓库自带 `./mvnw`）
- MySQL 8

## 启动

```bash
mysql -u root -p < src/main/resources/db/init.sql