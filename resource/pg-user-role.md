## Postgresql的权限配置脚本模板 

### 1. Postgres权限管理模板一(单RW，多RO)

这个模板只有一个读写账号，同时该读写账号为数据库的owner. 

#### 1.1 使用超级管理员登录
```bash
$ psql -U postgres -d postgres
```
- 创建**g1_app**读写账号
- 创建**g1_app_ro**只读角色
- 表空间**g1**
- 数据库**g1**
```sql 
-- Read/write role
CREATE USER g1_app CONNECTION LIMIT 100 ENCRYPTED PASSWORD 'md5f376bb249679054b542d003476821328';

-- Read-only role
CREATE ROLE g1_app_ro;

CREATE TABLESPACE g1
  OWNER g1_app
  LOCATION '/data/pg_tablespace/g1';

CREATE DATABASE g1
    WITH 
    OWNER = g1_app
    TEMPLATE = template1
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    TABLESPACE = g1
    CONNECTION LIMIT = 200;
```

#### 1.2 使用超级管理员账号登录g1数据库
```bash
$ psql -U postgres -d g1 
```
- 取消非授权用户对public schema的访问
- 设置public schema的owner为 g1_app
- 创建两个只读账号 **g1_app_ro_01** 和 **g1_app_ro_02**
- 授予只读角色**g1_app_ro**表和序列的只读

```sql 
-- Revoke privileges from 'public' role
REVOKE ALL ON DATABASE g1 FROM PUBLIC;
REVOKE CREATE ON SCHEMA public FROM PUBLIC;
ALTER SCHEMA public OWNER TO g1_app;

--readonly user creation 
CREATE USER g1_app_ro_01 CONNECTION LIMIT 10 ENCRYPTED PASSWORD 'md5f376bb249679054b542d003476821328';
CREATE USER g1_app_ro_02 CONNECTION LIMIT 10 ENCRYPTED PASSWORD 'md5f376bb249679054b542d003476821328';

-- Grant privileges to users
GRANT g1_app_ro TO g1_app_ro_01;
GRANT g1_app_ro TO g1_app_ro_02;

-- Read-only role
GRANT CONNECT ON DATABASE g1 TO g1_app_ro;
GRANT USAGE ON SCHEMA public TO g1_app_ro;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO g1_app_ro;
GRANT SELECT ON ALL SEQUENCES IN SCHEMA public TO g1_app_ro;
```

#### 1.3 使用**g1_app**数据库owner登录g1数据库
```bash
$ psql -U g1_app -d g1 
```
- 给只读角色**g1_app_ro**配置默认的只读权限，以后新加的表和序列就自动只读

```sql 
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO g1_app_ro;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT,USAGE ON SEQUENCES TO g1_app_ro;
```

---

### 2. Postgres权限管理模板二(多RW，多RO)

#### 2.1 使用超级管理员登录
```bash
$ psql -U postgres -d postgres
```
- 创建**sftr_app**读写角色，该角色不能直接登录
- 创建**sftr_app_ro**读写角色，该角色不能直接登录
- 表空间**sftr**
- 数据库**sftr**

```sql
-- Read/write role
CREATE ROLE sftr_app;
-- Read-only role
CREATE ROLE sftr_app_ro;

--tablespace 
CREATE TABLESPACE sftr
OWNER sftr_app
LOCATION '/data/pg_tablespace/sftr';

--database
CREATE DATABASE sftr
WITH
OWNER = sftr_app
TEMPLATE = template1
ENCODING = 'UTF8'
LC_COLLATE = 'en_US.UTF-8'
LC_CTYPE = 'en_US.UTF-8'
TABLESPACE = sftr
CONNECTION LIMIT = 200;

```

#### 2.2 使用超级管理员登录sftr数据库
```bash
$ psql -U postgres -d sftr 
```
- 取消非授权用户对public schema的访问
- 设置public schema的owner为 sftr_app
- 创建两个读写账号 **sftr_app_01** 和 **sftr_app_02**
- 创建两个只读账号 **sftr_app_ro_01** 和 **sftr_app_ro_02**
- 授予只读角色**sftr_app_ro**表和序列的只读
- 授予读写角色**sftr_app**表的增删改查等权限，序列所有权限

```sql 
-- Revoke privileges from 'public' role
REVOKE ALL ON DATABASE sftr FROM PUBLIC;
REVOKE CREATE ON SCHEMA public FROM PUBLIC;
ALTER SCHEMA public OWNER TO sftr_app;

-- Users creation
CREATE USER sftr_app_ro_01 CONNECTION LIMIT 10 ENCRYPTED PASSWORD 'md5f376bb249679054b542d003476821328';
CREATE USER sftr_app_ro_02 CONNECTION LIMIT 10 ENCRYPTED PASSWORD 'md5f376bb249679054b542d003476821328';
CREATE USER sftr_app_01 CONNECTION LIMIT 10 ENCRYPTED PASSWORD 'md5f376bb249679054b542d003476821328';
CREATE USER sftr_app_02 CONNECTION LIMIT 10 ENCRYPTED PASSWORD 'md5f376bb249679054b542d003476821328';

-- Grant privileges to users
GRANT sftr_app_ro TO sftr_app_ro_01;
GRANT sftr_app_ro TO sftr_app_ro_02;

GRANT sftr_app TO sftr_app_01;
GRANT sftr_app TO sftr_app_02;

	
-- Read-only role
GRANT CONNECT ON DATABASE sftr TO sftr_app_ro;
GRANT USAGE ON SCHEMA public TO sftr_app_ro;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO sftr_app_ro;
GRANT SELECT ON ALL SEQUENCES IN SCHEMA public TO sftr_app_ro;


-- Read/write role
GRANT CONNECT ON DATABASE sftr TO sftr_app;
GRANT USAGE, CREATE ON SCHEMA public TO sftr_app;
GRANT SELECT, INSERT, UPDATE, DELETE, TRUNCATE ON ALL TABLES IN SCHEMA public TO sftr_app;
GRANT ALL ON ALL SEQUENCES IN SCHEMA public TO sftr_app;

```
#### 2.3 分别使用两个账号，修改该账号的默认权限

- 修改每个读写账号的默认权限
- 对于读写角色，授予表的增删改查等权限，序列所有权限
- 对于只读角色，授予表的查询权限，序列的使用权限
- 注意，这里每个读写账号需要单独设置，不然无法生效

```bash
$ psql -U sftr_app_01 -d sftr
```
```sql 
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO sftr_app_ro;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT,USAGE ON SEQUENCES TO sftr_app_ro;

ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO sftr_app;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO sftr_app;

```
---
```bash
$ psql -U sftr_app_02 -d sftr
```

```sql
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO sftr_app_ro;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT,USAGE ON SEQUENCES TO sftr_app_ro;

ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO sftr_app;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO sftr_app;
 ```

---

### 3. 附录
```sql
psql -h 192.168.0.111 -U postgres -p 5433 -W -f 'import.sql'


drop table t_test;

create table t_test (
    id int,
    name varchar(32)
);

\copy t_test from '/home/jack/data.dat' (DELIMITER('|'));

select count(0) from t_test;

```

### 4. 参考资料，不断补充中

- [managing-postgresql-users-and-roles](https://aws.amazon.com/cn/blogs/database/managing-postgresql-users-and-roles/#:~:text=The%20recommended%20approach%20for%20setting%20up%20fine-grained%20access,the%20user%20in%20order%20to%20revoke%20the%20permissions.
)