Resource 目录, 主要记录数据迁移过程中，遇到问题，还包含那些网上比较难找到的资源。

### Sybase jdbc 驱动
jconn4.jar 
Sybase ase 16.0的jdbc 驱动程序，程序中使用ok

jTDS3
这个是个开源的jdbc驱动，适配Sybase，SqlServer等多种数据库，程序中没测试过。


### Sybase测试环境的搭建(docker)
Sybase的镜像有点大，有5G
执行以下命令：

1、 docker pull nguoianphu/docker-sybase

2、docker run -d -p 8000:5000 -p 8001:5001 --name sybase nguoianphu/docker-sybase

3、 docker exec -it sybase /bin/bash

4、source /opt/sybase/SYBASE.sh

5、默认账号:sa, 默认密码:myPassword,  默认服务名:MYSYBASE
```shell
[root@6281c1e2ae9c sybase]# isql -U sa -P myPassword -S MYSYBASE
1> select @@version
2> go
2>
3> exit
[root@6281c1e2ae9c sybase]#
```
这里默认的数据库的表空间都太小，master只有300M， 需要对master表空间和temp数据库进行扩容。

```sql 
sp_helpdevice

use master
go

disk resize name ='master', size='10240m'
go

disk resize name ='tempdbdev', size='1024m'
go

alter database master on master='10240m'
go

```

### Sybase java 连接信息

```text
db {
    driver-class=com.sybase.jdbc4.jdbc.SybDriver
    url="jdbc:sybase:Tds:192.168.203.161:8000/MYSYBASE"
    user=sa
    password=myPassword
}
```
这里有参考的文章。
(Sybase数据库 概述部署命令)[https://blog.csdn.net/whatday/article/details/84964690]


### Sybase与PostgreSQL的常用数据类型比较 
Data Type

| No.  | Sybase          | Sybase max (B) | PostgreSQL   | PG max (B) | Comments                                                            |
| :--- | :-------------- | -------------: | :----------- | :--------- | :------------------------------------------------------------------ |
| 1    | tinyint         |              1 | smallint     | 2          |                                                                     |
| 2    | Smallint        |              2 | smallint     | 2          |                                                                     |
| 3    | Int             |              4 | int          | 4          |                                                                     |
| 4    | Numeric(p,s)    |             17 | numeric(p,s) | No limit   | max(s) = 16383, max(p) > 10w                                        |
| 5    | Decimal(p,s)    |             17 | numeric(p,s) | No limit   | max(s) = 16383, max(p) > 10w                                        |
| 6    | Money           |              8 | money        | 8          |                                                                     |
| 7    | Small Money     |              4 | money        | 8          |                                                                     |
| 8    | Datetime        |              8 | timestamp    | 8          | Sybase precision (1/300 s) , PG 1ms, PG default[without time zone ] |
| 9    | Date            |              4 | DATE         | 4          |                                                                     |
| 10   | TEXT            |        2^31 -1 | TEXT         | No limit   |                                                                     |
| 11   | VARCHAR(n)      |            255 | VARCHAR(n)   | 1G         |                                                                     |
| 12   | CHAR(N)NCHAR(N) |            255 | CHAR(n)      | 1G         |                                                                     |
| 13   | BIT             |             1b | BOOLEAN      | 1          |                                                                     |


### Sybase的bcp数据导入导出
0. 导出表结构
```shell
docker exec -it sybase /bin/bash

ddlgen -U sa -P myPassword -S MYSYBASE -TU -Nmaster.dbo.t_order -F%
```

```sql
create table t_order (
	id                              int                              identity,
	sequence_no                     varchar(50)                      not null,
	customer_id                     int                              not null,
	product_id                      int                              not null,
	order_dt                        datetime                         not null,
	status                          varchar(32)                      not null,
	create_dt                       datetime                         not null,
	last_update_dt                  datetime                         not null,
	raw_string                      text                             not null 
)

```
   

1. 先使用docker -it 登录sybase 容器, 然后使用bcp 命令导出想要的表。
```shell
docker exec -it sybase /bin/bash

bcp master.dbo.t_order out ~/t_order.DAT -t '$@' -U sa -P myPassword -S MYSYBASE -b 1000 -c

```


2. copy出 想要导出的dat文件，docker 的文件上传与下载

//上传
docker cp /home/jack/test.txt sybase:

//下载
docker cp sybase:/root/t_order.DAT /home/jack/


3. 改写sql 变成postgresl的格式，并创建表和索引。

4. 使用copy的文本加载的方式，postgres，默认只能使用单字节字符。

postgres=# copy t_order from '/var/lib/pgsql/t_order.DAT' (DELIMITER('|'));

日期格式的问题，sybase的datetime格式，postgres 无法识别
可以通过创建视图，把timestamp转换成字符。

```sql

create view v_t_order 
as
select id, sequence_no, customer_id, product_id
,  convert(varchar(4),year(order_dt)) + '-' +convert(varchar(2),month(order_dt)) + '-' + convert(varchar(2),day(order_dt)) + ' ' +convert(varchar(24),order_dt,108) as order_dt
, status
,  convert(varchar(4),year(create_dt)) + '-' +convert(varchar(2),month(create_dt)) + '-' + convert(varchar(2),day(create_dt)) + ' ' +convert(varchar(24),create_dt,108) as create_dt
,  convert(varchar(4),year(last_update_dt)) + '-' +convert(varchar(2),month(last_update_dt)) + '-' + convert(varchar(2),day(last_update_dt)) + ' ' +convert(varchar(24),last_update_dt,108) as last_update_dt
,   raw_string  
 from t_order 
```






