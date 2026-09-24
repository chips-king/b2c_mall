# b2c_mall

Maven 多模块商城教学工程，基础包名 `com.b2cmall`。

逐文件用途见 [文件说明](FILE_GUIDE.md)。

## 基础工程

| 模块 | 职责 | 端口 |
| --- | --- | --- |
| common | 统一响应、业务异常、JWT与身份数据 | 无 |
| gateway | Nacos 服务发现路由、请求日志、统一入口鉴权 | 8080 |
| shop | 店铺注册、AsyncEventBus、状态查询与重试、欢迎消息 | 8081 |
| employee | 管理员初始化、登录、Redis会话、退出与登录审计 | 8084 |
| product | 两种商品模板、创建与查询、上架及商品日志 | 8082 |
| order | 商品成交快照、LiteFlow支付、失败重试、签名回调、订单状态机 | 8083 |

Spring Statemachine 3.2.1、LiteFlow 2.11.4.2、Guava EventBus 30.1、Hutool JWT 5.8.35。

Java 17、Maven 3.9.8；Spring Boot 2.7.18、Spring Cloud 2021.0.5、MyBatis Starter 2.3.2、SQLite JDBC 3.42.0.0。父 POM 统一管理版本，并通过 Boot BOM 对齐基础依赖。

第三阶段使用 Spring Cloud Alibaba 2021.0.5.0，显式指定 Nacos Client 1.4.7，与课堂 Nacos Server 1.4.8 对接。配置通过 `application.yml` 中的 `spring.config.import` 加载。

## Nacos 配置

| 项目 | 值 |
| --- | --- |
| 控制台 | http://47.100.22.158:8848/nacos |
| 连接地址 | `47.100.22.158:8848` |
| Namespace ID / 名称 | `ZhangHaijin` |
| Group | `DEFAULT_GROUP` |
| Data ID | `employee-service.yml`、`shop-service.yml`、`product-service.yml`、`order-service.yml`、`gateway-service.yml` |

在控制台选择 `ZhangHaijin`，配置管理中可以查看五个 YAML 配置。命名空间在启动前创建；服务实例由后端启动后自动注册。

`employee-service.yml` 内容：

```yaml
server:
  port: 8084
```

`shop-service.yml` 内容：

```yaml
server:
  port: 8081
```

`product-service.yml` 内容：

```yaml
server:
  port: 8082
```

`order-service.yml` 内容：

```yaml
server:
  port: 8083
```

`gateway-service.yml` 内容：

```yaml
server:
  port: 8080
spring:
  cloud:
    gateway:
      routes:
        - id: shop-discovery
          uri: lb://shop-service
          predicates:
            - Path=/api/shop/**
          filters:
            - StripPrefix=2
        - id: employee-discovery
          uri: lb://employee-service
          predicates:
            - Path=/employee/**
        - id: product-discovery
          uri: lb://product-service
          predicates:
            - Path=/api/product/**
          filters:
            - StripPrefix=1
        - id: order-discovery
          uri: lb://order-service
          predicates:
            - Path=/api/order/**
          filters:
            - StripPrefix=1
```

服务名和 Nacos 连接参数保留在本地 `application.yml`；端口及网关路由来自远程配置。发现客户端与配置客户端使用相同地址、命名空间和 Group。凭据仅从进程环境变量读取。

## 构建与启动

所有命令从工程根目录执行。构建会向 Maven 本地仓库下载依赖。

```powershell
mvn clean verify
```

在五个 PowerShell 7 终端中分别设置以下进程环境变量。密码输入使用掩码，内容不会写入项目文件：

```powershell
$env:NACOS_SERVER_ADDR = '47.100.22.158:8848'
$env:NACOS_NAMESPACE = 'ZhangHaijin'
$env:NACOS_GROUP = 'DEFAULT_GROUP'
$env:NACOS_USERNAME = 'nacos'
$env:NACOS_PASSWORD = Read-Host 'Nacos 密码' -MaskInput
```

在 Employee 和 Shop 两个终端中再设置相同的内部调用密钥。自行选择一个足够长的随机值，两次输入必须一致；该值用于服务间调用鉴权：

```powershell
$env:INTERNAL_SERVICE_TOKEN = Read-Host 'Shop 与 Employee 共用的内部调用密钥' -MaskInput
```

仅在 Employee 终端配置 Redis 和 JWT。当前指定 Redis 的无认证连接已验证；以下变量只影响本终端启动的进程：

```powershell
$env:REDIS_HOST = '47.100.22.158'
$env:REDIS_PORT = '6379'
$env:REDIS_DATABASE = '0'
$env:REDIS_USERNAME = ''
$env:REDIS_PASSWORD = ''
$env:REDIS_KEY_PREFIX = 'b2c_mall:ZhangHaijin:'
# 首次教学启动生成32字节随机密钥，直接进入环境变量，不打印密钥。
$env:JWT_SECRET_BASE64 = [Convert]::ToBase64String([System.Security.Cryptography.RandomNumberGenerator]::GetBytes(32))
```

同一环境后续重启应沿用同一 JWT 密钥；生成新密钥会使先前签发的 token 无法通过验签。已有密钥可通过 `Read-Host 'JWT Base64密钥' -MaskInput` 赋给上述变量。密钥与密码均不写入仓库。

四个数据库必须相互独立。Employee、Shop 和 Order 要求显式设置数据库路径。首次运行新版时，在各服务启动前执行下面对应的一行：

```powershell
# 先创建一个新的、空的课堂数据目录；名称自行选择。
$mallDataDir = Read-Host '新的课堂数据目录绝对路径'
New-Item -ItemType Directory -Path $mallDataDir -ErrorAction Stop
# 创建目录仅在一个终端执行；其他终端先用Read-Host设置同一mallDataDir，再执行对应变量行。
$env:EMPLOYEE_DB_PATH = Join-Path $mallDataDir 'mall_employee.db'
$env:SHOP_DB_PATH = Join-Path $mallDataDir 'mall_shop.db'
$env:PRODUCT_DB_PATH = Join-Path $mallDataDir 'mall_product.db'
$env:ORDER_DB_PATH = Join-Path $mallDataDir 'mall_order.db'
```

四个库由各自服务启动时建表。后续重启将变量指向这组数据库即可；创建目录只执行一次。启动时使用该目录中的四个独立数据库。

先在第一个终端启动 Employee，等待启动完成：

```powershell
$nacosRuntimeHome = Join-Path (Get-Location) 'employee/target/runtime'
java "-Duser.home=$nacosRuntimeHome" -jar employee/target/employee-1.0-SNAPSHOT.jar
```

再在第二个终端启动 Shop，等待启动完成：

```powershell
$nacosRuntimeHome = Join-Path (Get-Location) 'shop/target/runtime'
java "-Duser.home=$nacosRuntimeHome" -jar shop/target/shop-1.0-SNAPSHOT.jar
```

在第三个终端启动 Product：

```powershell
$nacosRuntimeHome = Join-Path (Get-Location) 'product/target/runtime'
java "-Duser.home=$nacosRuntimeHome" -jar product/target/product-1.0-SNAPSHOT.jar
```

在第四个终端中，为Order配置模拟支付回调密钥，再启动Order。首次生成随机密钥，后续重启沿用同一个值；该变量仅供Order使用：

```powershell
$env:PAYMENT_CALLBACK_SECRET_BASE64 = [Convert]::ToBase64String([System.Security.Cryptography.RandomNumberGenerator]::GetBytes(32))
# 已有密钥时，通过Read-Host '支付回调Base64密钥' -MaskInput赋给同一变量。
```

启动 Order：

```powershell
$nacosRuntimeHome = Join-Path (Get-Location) 'order/target/runtime'
java "-Duser.home=$nacosRuntimeHome" -jar order/target/order-1.0-SNAPSHOT.jar
```

最后在第五个终端启动 Gateway：

```powershell
$nacosRuntimeHome = Join-Path (Get-Location) 'gateway/target/runtime'
java "-Duser.home=$nacosRuntimeHome" -jar gateway/target/gateway-1.0-SNAPSHOT.jar
```

在各自终端按 Ctrl+C 停止服务。上述 `user.home` 仅影响本次 Java 进程，使 Nacos 的运行日志与客户端缓存留在对应模块的 `target/runtime` 中。

## 当前接口

所有业务请求优先经过 `http://localhost:8080`。登录返回的 `data` 是 token 字符串，请求头为 `Authorization: 原始token`。

| 方法与网关路径 | 请求或行为 |
| --- | --- |
| POST `/api/shop/register` | `shopName`、`adminAccount`、`adminPassword` |
| POST `/api/shop/v2/register` | 上述字段，另可传 `source`、`invaliCode` |
| GET `/api/shop/registration/status?shopId=实际ID` | 返回实际初始化进度 |
| POST `/api/shop/registration/retry?shopId=实际ID` | 用原管理员账号和密码重试失败任务 |
| POST `/employee/login` | `shopId`、`username`、`password`，返回 token |
| POST `/employee/checkToken` | 校验 JWT 与 Redis 当前会话 |
| GET `/employee/me` | 当前员工、默认头像、登录次数、最后登录时间 |
| POST `/employee/logout` | 删除当前会话 |
| GET `/api/shop/test`、`/api/shop/test/db` | 受保护的服务转发及数据库测试 |
| POST `/api/product/create` | `cateName`、`skuName`、`sellPoint`、`stock`、`price`、`type` |
| GET `/api/product/{id}` | 查询当前店铺商品 |
| POST `/api/order/create` | `skuId`，可选 `skuPrice` 仅作报价一致性检查 |
| GET `/api/order/{id}` | 查询本人订单及成交快照 |
| POST `/api/order/pay` | `orderId`、`payType`（`ALIPAY` / `WECHAT`） |
| POST `/api/order/callback` | 提交模拟渠道生成的签名通知 |
| POST `/api/order/sent` | `orderId`，已支付订单发货 |
| POST `/api/order/complete` | `orderId`，已发货订单完成 |

### 注册与适配器

V1适配器补充 `source=OLD_VER`、`invaliCode=-1`；V2缺省为 `NEW_VER`、`-1`。V2显式值仅允许字母、数字、下划线和连字符，source最多32字符，invaliCode最多64字符。两字段用于课堂输入适配演示，共用同一注册业务。

店铺名按课堂约束最多6字符，账号和名称去除首尾空白；密码保留原文，非空且最多72个UTF-8字节，使用BCrypt哈希。同名同凭据复用shopId，不同凭据返回409。

注册返回202（PENDING）、200（COMPLETED）或503（FAILED）。`data`含 `shopId`、`shopName`、`state`、`employeeInitialized`、`welcomeMessageCreated`、`attempt`，失败时含`failureCode`。状态查询成功固定HTTP 200，任务成败看`data.state`。

AsyncEventBus使用Spring管理的线程池。Shop通过Feign让Employee初始化管理员，成功后生成欢迎消息；两步都有持久化去重。FAILED可携带原凭据重试；PENDING和COMPLETED重复调用复用当前任务。

### 商品模板与订单支付

商品type=1走实物实现，type=2走虚拟实现，统一执行参数校验、类目校验、价格库存校验、内容检查、保存、上架、后置处理。商品、上架和日志同一事务；身份来自登录员工，请求结束清理身份上下文。

每单一个SKU、一件商品，订单金额取Product报价并保存快照。商品按店铺隔离；订单查询、支付、回调、发货和完成要求店铺与创建员工都一致。

LiteFlow `payChain`：价格校验 → 库存校验 → 渠道选择 → 支付宝或微信模拟策略 → 支付记录。控制器检查执行结果，失败抛出并回滚。每次执行拥有独立PaymentContext。

发起支付后订单为`WAIT_PAY`、支付记录为`PENDING`。重复发起复用记录；PENDING时换渠道返回409。返回的`callback`是已签名成功通知，`failureCallback`是已签名失败通知；完整提交到回调接口进行模拟。

合法失败通知将支付标为`FAILED`，订单继续`WAIT_PAY`。重试创建新的支付ID和流水，可换渠道，保留失败历史。重复通知不重复写入；已失败尝试的迟到成功通知返回409。

Spring Statemachine控制 `WAIT_PAY --PAY--> PAID --SENT--> SENT --COMPLETED--> COMPLETED`。合法成功回调才触发PAY。状态与日志同事务；非法跨步返回409，重复发货和完成返回当前状态，不重复记日志。

### 范围说明

这是课堂模拟支付demo，不产生真实扣款。库存只校验，锁定与扣减留待后续课堂。新版接口与新库结构统一使用，不执行旧数据迁移；启动使用显式指定的新数据库。

## 按课堂顺序验证

1. 启动 Employee → Shop → Product → Order → Gateway。
2. 按上方接口表注册店铺，查询到COMPLETED后登录。
3. 携带Authorization创建实物或虚拟商品。
4. 创建订单、发起模拟支付、提交返回的签名回调，然后查询订单、发货和完成。
5. 退出登录，再检查原token失效。

### 上传范围

公开仓库提供业务主源码、配置模板、建表SQL和说明文档。本地测试源码、HTTP请求文件、测试数据、数据库、日志和构建产物不上传。服务器地址、命名空间和Redis项目前缀按项目要求保留；密码、密钥与会话token通过运行环境提供。

## 构建验证记录

2026-09-23执行 `mvn -B -ntp verify`，7个Maven模块全部成功。259个测试通过：Common 10、Gateway 56、Employee 25、Shop 45、Product 38、Order 85。

包含真实SQLite事务、MVC、异步EventBus、LiteFlow节点顺序和两个分支、Spring Statemachine流转、并发幂等、身份隔离及故障回滚测试。模块测试在Feign边界控制远端响应。此为上传前本地完整工程的验证记录。

## 五服务联调记录

2026-09-23运行编号 `08349488bb`，使用指定Nacos、Redis和四个独立新SQLite数据库，验证通过：

- V1/V2注册共用店铺，初始化进度真实可查；错误参数与冲突凭据被拒绝，旧`/shop/register`返回404。
- 数据库触发器使欢迎消息写入失败后，查询到FAILED；恢复后重试到COMPLETED，管理员和消息各自只有一份。
- 登录、Redis会话、网关转发正常；缺失、篡改、过期和退出后的token被拒绝。
- 实物、虚拟商品各创建一件；非法库存和跨店铺商品查询被拒绝。
- 支付宝失败后改微信重试，保留失败历史；重复通知、旧失败尝试的迟到成功回调、金额篡改与跨店铺调用按约定处理。
- 同店铺另一员工在查询、支付、回调、发货和完成入口均返回404。
- 真实接口完成WAIT_PAY→PAID→SENT→COMPLETED；在PAY、SENT、COMPLETED各次状态日志写入注入故障，HTTP 500后状态回滚，恢复后可以继续。
- 另一订单通过支付宝成功回调到PAID。最终2家店铺完成初始化、3名员工（含隔离验证员工）、2件商品、2个订单；支付记录为FAILED 1条、SUCCESS 2条。
