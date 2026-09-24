# 文件用途说明

按模块列出源文件的功能与职责。

## 工程

| 文件 | 用途 |
| --- | --- |
| [.gitignore](.gitignore) | 排除构建目录、数据库、日志和本地环境文件 |
| [pom.xml](pom.xml) | 管理Maven多模块与统一依赖版本 |
| [README.md](README.md) | 说明架构、配置、启动、接口及实际验证结果 |

## common

| 文件 | 用途 |
| --- | --- |
| [common/pom.xml](common/pom.xml) | 声明common模块依赖及打包方式 |
| [common/src/main/java/com/b2cmall/common/auth/AuthenticatedEmployee.java](common/src/main/java/com/b2cmall/common/auth/AuthenticatedEmployee.java) | 保存已验证的店铺、员工ID和账号 |
| [common/src/main/java/com/b2cmall/common/auth/JwtTokenService.java](common/src/main/java/com/b2cmall/common/auth/JwtTokenService.java) | 签发HS256令牌并校验签名、有效期和身份声明 |
| [common/src/main/java/com/b2cmall/common/exception/BusinessException.java](common/src/main/java/com/b2cmall/common/exception/BusinessException.java) | 定义携带业务状态码的异常 |
| [common/src/main/java/com/b2cmall/common/exception/GlobalExceptionHandler.java](common/src/main/java/com/b2cmall/common/exception/GlobalExceptionHandler.java) | 统一处理业务异常及参数错误 |
| [common/src/main/java/com/b2cmall/common/response/BaseResponseVO.java](common/src/main/java/com/b2cmall/common/response/BaseResponseVO.java) | 定义status、message、data统一响应结构 |

## employee

| 文件 | 用途 |
| --- | --- |
| [employee/pom.xml](employee/pom.xml) | 声明employee模块依赖及打包方式 |
| [employee/src/main/java/com/b2cmall/employee/config/EmployeeAuthConfig.java](employee/src/main/java/com/b2cmall/employee/config/EmployeeAuthConfig.java) | 组装密码编码器和JWT登录配置 |
| [employee/src/main/java/com/b2cmall/employee/dao/mapper/EmployeeMapper.java](employee/src/main/java/com/b2cmall/employee/dao/mapper/EmployeeMapper.java) | 声明员工初始化、查询和登录统计的MyBatis方法 |
| [employee/src/main/java/com/b2cmall/employee/dao/mapper/LoginAuditMapper.java](employee/src/main/java/com/b2cmall/employee/dao/mapper/LoginAuditMapper.java) | 声明登录审计记录的MyBatis方法 |
| [employee/src/main/java/com/b2cmall/employee/dao/po/EmployeePO.java](employee/src/main/java/com/b2cmall/employee/dao/po/EmployeePO.java) | 映射员工账号、密码哈希和登录状态 |
| [employee/src/main/java/com/b2cmall/employee/dao/po/LoginAuditPO.java](employee/src/main/java/com/b2cmall/employee/dao/po/LoginAuditPO.java) | 映射登录结果、来源地址及原因 |
| [employee/src/main/java/com/b2cmall/employee/EmployeeApplication.java](employee/src/main/java/com/b2cmall/employee/EmployeeApplication.java) | 启动employee服务及所需Spring配置 |
| [employee/src/main/java/com/b2cmall/employee/service/EmployeeLoginService.java](employee/src/main/java/com/b2cmall/employee/service/EmployeeLoginService.java) | 校验密码、签发会话、查询员工与退出登录 |
| [employee/src/main/java/com/b2cmall/employee/service/EmployeeService.java](employee/src/main/java/com/b2cmall/employee/service/EmployeeService.java) | 定义员工初始化服务契约 |
| [employee/src/main/java/com/b2cmall/employee/service/impl/EmployeeServiceImpl.java](employee/src/main/java/com/b2cmall/employee/service/impl/EmployeeServiceImpl.java) | 保存管理员并按店铺和账号处理重复初始化 |
| [employee/src/main/java/com/b2cmall/employee/service/LoginAuditService.java](employee/src/main/java/com/b2cmall/employee/service/LoginAuditService.java) | 保存登录审计并更新登录次数和时间 |
| [employee/src/main/java/com/b2cmall/employee/service/RedisTokenStore.java](employee/src/main/java/com/b2cmall/employee/service/RedisTokenStore.java) | 保存当前token并原子删除匹配会话 |
| [employee/src/main/java/com/b2cmall/employee/web/EmployeeController.java](employee/src/main/java/com/b2cmall/employee/web/EmployeeController.java) | 提供员工初始化、登录、token校验与退出接口 |
| [employee/src/main/java/com/b2cmall/employee/web/request/AddEmployeeRequestVO.java](employee/src/main/java/com/b2cmall/employee/web/request/AddEmployeeRequestVO.java) | 传递内部初始化所需店铺、账号与BCrypt哈希 |
| [employee/src/main/java/com/b2cmall/employee/web/request/LoginRequestVO.java](employee/src/main/java/com/b2cmall/employee/web/request/LoginRequestVO.java) | 校验登录的店铺ID、用户名和密码 |
| [employee/src/main/java/com/b2cmall/employee/web/response/EmployeeInfoVO.java](employee/src/main/java/com/b2cmall/employee/web/response/EmployeeInfoVO.java) | 返回身份、头像及登录统计 |
| [employee/src/main/resources/application.yml](employee/src/main/resources/application.yml) | 配置employee服务的Nacos导入与运行参数 |
| [employee/src/main/resources/mapper/EmployeeMapper.xml](employee/src/main/resources/mapper/EmployeeMapper.xml) | 实现员工初始化、查询和登录统计的SQL和字段映射 |
| [employee/src/main/resources/mapper/LoginAuditMapper.xml](employee/src/main/resources/mapper/LoginAuditMapper.xml) | 实现登录审计记录的SQL和字段映射 |
| [employee/src/main/resources/schema.sql](employee/src/main/resources/schema.sql) | 建立employee独立数据库表、索引和约束 |
| [employee/src/main/resources/static/employee/images/default-avatar.svg](employee/src/main/resources/static/employee/images/default-avatar.svg) | 提供员工默认头像资源 |

## shop

| 文件 | 用途 |
| --- | --- |
| [shop/pom.xml](shop/pom.xml) | 声明shop模块依赖及打包方式 |
| [shop/src/main/java/com/b2cmall/shop/config/ShopInitializationConfig.java](shop/src/main/java/com/b2cmall/shop/config/ShopInitializationConfig.java) | 配置受Spring管理的初始化线程池与异步事件总线 |
| [shop/src/main/java/com/b2cmall/shop/dao/mapper/DatabaseProbeMapper.java](shop/src/main/java/com/b2cmall/shop/dao/mapper/DatabaseProbeMapper.java) | 声明SQLite版本和时间探测的MyBatis方法 |
| [shop/src/main/java/com/b2cmall/shop/dao/mapper/MessageMapper.java](shop/src/main/java/com/b2cmall/shop/dao/mapper/MessageMapper.java) | 声明欢迎消息的MyBatis方法 |
| [shop/src/main/java/com/b2cmall/shop/dao/mapper/ShopMapper.java](shop/src/main/java/com/b2cmall/shop/dao/mapper/ShopMapper.java) | 声明店铺与初始化任务的MyBatis方法 |
| [shop/src/main/java/com/b2cmall/shop/dao/po/MessagePO.java](shop/src/main/java/com/b2cmall/shop/dao/po/MessagePO.java) | 映射店铺欢迎消息字段 |
| [shop/src/main/java/com/b2cmall/shop/dao/po/ShopInitializationTaskPO.java](shop/src/main/java/com/b2cmall/shop/dao/po/ShopInitializationTaskPO.java) | 保存任务编号、截止时间与员工创建进度 |
| [shop/src/main/java/com/b2cmall/shop/dao/po/ShopPO.java](shop/src/main/java/com/b2cmall/shop/dao/po/ShopPO.java) | 映射店铺信息及初始化状态 |
| [shop/src/main/java/com/b2cmall/shop/feign/EmployeeFeignClient.java](shop/src/main/java/com/b2cmall/shop/feign/EmployeeFeignClient.java) | 从店铺服务调用员工内部初始化 |
| [shop/src/main/java/com/b2cmall/shop/feign/request/AddEmployeeRequestVO.java](shop/src/main/java/com/b2cmall/shop/feign/request/AddEmployeeRequestVO.java) | 传递内部初始化所需店铺、账号与BCrypt哈希 |
| [shop/src/main/java/com/b2cmall/shop/service/event/handler/InitEmployeeEventHandler.java](shop/src/main/java/com/b2cmall/shop/service/event/handler/InitEmployeeEventHandler.java) | 通过Feign初始化管理员并记录成功进度 |
| [shop/src/main/java/com/b2cmall/shop/service/event/handler/InitShopMessageEventHandler.java](shop/src/main/java/com/b2cmall/shop/service/event/handler/InitShopMessageEventHandler.java) | 在管理员创建成功后生成欢迎消息 |
| [shop/src/main/java/com/b2cmall/shop/service/event/ShopRegisterEvent.java](shop/src/main/java/com/b2cmall/shop/service/event/ShopRegisterEvent.java) | 封装注册事件并约束处理阶段 |
| [shop/src/main/java/com/b2cmall/shop/service/event/ShopRegisterEventPublisher.java](shop/src/main/java/com/b2cmall/shop/service/event/ShopRegisterEventPublisher.java) | 发布异步事件并衔接完成和失败回调 |
| [shop/src/main/java/com/b2cmall/shop/service/event/ShopRegisterObserver.java](shop/src/main/java/com/b2cmall/shop/service/event/ShopRegisterObserver.java) | 定义注册事件观察者契约 |
| [shop/src/main/java/com/b2cmall/shop/service/impl/MessageServiceImpl.java](shop/src/main/java/com/b2cmall/shop/service/impl/MessageServiceImpl.java) | 创建欢迎消息并处理重复写入 |
| [shop/src/main/java/com/b2cmall/shop/service/impl/ShopServiceImpl.java](shop/src/main/java/com/b2cmall/shop/service/impl/ShopServiceImpl.java) | 原子保存店铺任务并校验同名重放凭据 |
| [shop/src/main/java/com/b2cmall/shop/service/MessageService.java](shop/src/main/java/com/b2cmall/shop/service/MessageService.java) | 定义欢迎消息业务契约 |
| [shop/src/main/java/com/b2cmall/shop/service/ShopInitializationRecovery.java](shop/src/main/java/com/b2cmall/shop/service/ShopInitializationRecovery.java) | 扫描超时初始化任务并记录失败 |
| [shop/src/main/java/com/b2cmall/shop/service/ShopInitializationService.java](shop/src/main/java/com/b2cmall/shop/service/ShopInitializationService.java) | 编排任务提交、进度查询和失败重试 |
| [shop/src/main/java/com/b2cmall/shop/service/ShopInitializationStateService.java](shop/src/main/java/com/b2cmall/shop/service/ShopInitializationStateService.java) | 持久化执行编号、超时与初始化进度 |
| [shop/src/main/java/com/b2cmall/shop/service/ShopRegisterAdapter.java](shop/src/main/java/com/b2cmall/shop/service/ShopRegisterAdapter.java) | 统一V1/V2输入并补充来源和邀请码演示字段 |
| [shop/src/main/java/com/b2cmall/shop/service/ShopRegisterDTO.java](shop/src/main/java/com/b2cmall/shop/service/ShopRegisterDTO.java) | 承接两个版本适配后的注册数据 |
| [shop/src/main/java/com/b2cmall/shop/service/ShopService.java](shop/src/main/java/com/b2cmall/shop/service/ShopService.java) | 定义店铺注册业务入口 |
| [shop/src/main/java/com/b2cmall/shop/ShopApplication.java](shop/src/main/java/com/b2cmall/shop/ShopApplication.java) | 启动shop服务及所需Spring配置 |
| [shop/src/main/java/com/b2cmall/shop/web/request/RetryInitializationRequestVO.java](shop/src/main/java/com/b2cmall/shop/web/request/RetryInitializationRequestVO.java) | 接收重试时验证的原管理员凭据 |
| [shop/src/main/java/com/b2cmall/shop/web/request/ShopRegisterRequestVO.java](shop/src/main/java/com/b2cmall/shop/web/request/ShopRegisterRequestVO.java) | 校验注册基础字段和密码字节限制 |
| [shop/src/main/java/com/b2cmall/shop/web/request/ShopRegisterV2RequestVO.java](shop/src/main/java/com/b2cmall/shop/web/request/ShopRegisterV2RequestVO.java) | 扩展source和invaliCode并校验格式 |
| [shop/src/main/java/com/b2cmall/shop/web/ShopController.java](shop/src/main/java/com/b2cmall/shop/web/ShopController.java) | 提供两个注册版本、状态查询和重试接口 |
| [shop/src/main/java/com/b2cmall/shop/web/TestController.java](shop/src/main/java/com/b2cmall/shop/web/TestController.java) | 查询服务标识和真实SQLite探测结果 |
| [shop/src/main/resources/application.yml](shop/src/main/resources/application.yml) | 配置shop服务的Nacos导入与运行参数 |
| [shop/src/main/resources/mapper/DatabaseProbeMapper.xml](shop/src/main/resources/mapper/DatabaseProbeMapper.xml) | 实现SQLite版本和时间探测的SQL和字段映射 |
| [shop/src/main/resources/mapper/MessageMapper.xml](shop/src/main/resources/mapper/MessageMapper.xml) | 实现欢迎消息的SQL和字段映射 |
| [shop/src/main/resources/mapper/ShopMapper.xml](shop/src/main/resources/mapper/ShopMapper.xml) | 实现店铺与初始化任务的SQL和字段映射 |
| [shop/src/main/resources/schema.sql](shop/src/main/resources/schema.sql) | 建立shop独立数据库表、索引和约束 |

## product

| 文件 | 用途 |
| --- | --- |
| [product/pom.xml](product/pom.xml) | 声明product模块依赖及打包方式 |
| [product/src/main/java/com/b2cmall/product/component/CreateProductTemplate.java](product/src/main/java/com/b2cmall/product/component/CreateProductTemplate.java) | 固定商品创建步骤并定义类型扩展点 |
| [product/src/main/java/com/b2cmall/product/component/PhysicalProductComponent.java](product/src/main/java/com/b2cmall/product/component/PhysicalProductComponent.java) | 实现实物商品创建模板 |
| [product/src/main/java/com/b2cmall/product/component/VirtualProductComponent.java](product/src/main/java/com/b2cmall/product/component/VirtualProductComponent.java) | 实现虚拟商品创建模板 |
| [product/src/main/java/com/b2cmall/product/context/RequestIdentityContext.java](product/src/main/java/com/b2cmall/product/context/RequestIdentityContext.java) | 保存、读取和清理当前请求的员工身份 |
| [product/src/main/java/com/b2cmall/product/dao/mapper/SkuLogMapper.java](product/src/main/java/com/b2cmall/product/dao/mapper/SkuLogMapper.java) | 声明商品操作日志的MyBatis方法 |
| [product/src/main/java/com/b2cmall/product/dao/mapper/SkuMapper.java](product/src/main/java/com/b2cmall/product/dao/mapper/SkuMapper.java) | 声明商品保存、上架和查询的MyBatis方法 |
| [product/src/main/java/com/b2cmall/product/dao/po/SkuLogPO.java](product/src/main/java/com/b2cmall/product/dao/po/SkuLogPO.java) | 映射商品操作日志 |
| [product/src/main/java/com/b2cmall/product/dao/po/SkuPO.java](product/src/main/java/com/b2cmall/product/dao/po/SkuPO.java) | 映射商品价格、库存、上架状态与操作人 |
| [product/src/main/java/com/b2cmall/product/feign/EmployeeFeignClient.java](product/src/main/java/com/b2cmall/product/feign/EmployeeFeignClient.java) | 从product服务查询已登录员工身份 |
| [product/src/main/java/com/b2cmall/product/feign/response/EmployeeIdentityVO.java](product/src/main/java/com/b2cmall/product/feign/response/EmployeeIdentityVO.java) | 接收员工服务验证后的身份信息 |
| [product/src/main/java/com/b2cmall/product/ProductApplication.java](product/src/main/java/com/b2cmall/product/ProductApplication.java) | 启动product服务及所需Spring配置 |
| [product/src/main/java/com/b2cmall/product/service/ProductService.java](product/src/main/java/com/b2cmall/product/service/ProductService.java) | 按type选择模板并隔离店铺商品查询 |
| [product/src/main/java/com/b2cmall/product/service/SkuLogService.java](product/src/main/java/com/b2cmall/product/service/SkuLogService.java) | 记录商品创建和上架日志 |
| [product/src/main/java/com/b2cmall/product/web/filter/ProductAuthenticationFilter.java](product/src/main/java/com/b2cmall/product/web/filter/ProductAuthenticationFilter.java) | 校验商品请求身份并在结束后清理上下文 |
| [product/src/main/java/com/b2cmall/product/web/ProductController.java](product/src/main/java/com/b2cmall/product/web/ProductController.java) | 提供商品创建与查询接口 |
| [product/src/main/java/com/b2cmall/product/web/request/CreateProductRequestVO.java](product/src/main/java/com/b2cmall/product/web/request/CreateProductRequestVO.java) | 接收类目、名称、卖点、价格、库存和类型 |
| [product/src/main/java/com/b2cmall/product/web/response/ProductResponseVO.java](product/src/main/java/com/b2cmall/product/web/response/ProductResponseVO.java) | 将商品持久化信息转换为接口响应 |
| [product/src/main/resources/application.yml](product/src/main/resources/application.yml) | 配置product服务的Nacos导入与运行参数 |
| [product/src/main/resources/mapper/SkuLogMapper.xml](product/src/main/resources/mapper/SkuLogMapper.xml) | 实现商品操作日志的SQL和字段映射 |
| [product/src/main/resources/mapper/SkuMapper.xml](product/src/main/resources/mapper/SkuMapper.xml) | 实现商品保存、上架和查询的SQL和字段映射 |
| [product/src/main/resources/schema.sql](product/src/main/resources/schema.sql) | 建立product独立数据库表、索引和约束 |

## order

| 文件 | 用途 |
| --- | --- |
| [order/pom.xml](order/pom.xml) | 声明order模块依赖及打包方式 |
| [order/src/main/java/com/b2cmall/order/config/OrderStateMachineConfig.java](order/src/main/java/com/b2cmall/order/config/OrderStateMachineConfig.java) | 定义待支付到已完成的合法状态事件 |
| [order/src/main/java/com/b2cmall/order/context/PaymentContext.java](order/src/main/java/com/b2cmall/order/context/PaymentContext.java) | 隔离每次LiteFlow执行的请求、身份和结果 |
| [order/src/main/java/com/b2cmall/order/context/RequestIdentityContext.java](order/src/main/java/com/b2cmall/order/context/RequestIdentityContext.java) | 保存、读取和清理当前请求的员工身份 |
| [order/src/main/java/com/b2cmall/order/dao/mapper/OrderMapper.java](order/src/main/java/com/b2cmall/order/dao/mapper/OrderMapper.java) | 声明订单写入、归属查询和条件状态更新的MyBatis方法 |
| [order/src/main/java/com/b2cmall/order/dao/mapper/OrderSkuMapper.java](order/src/main/java/com/b2cmall/order/dao/mapper/OrderSkuMapper.java) | 声明订单成交快照的MyBatis方法 |
| [order/src/main/java/com/b2cmall/order/dao/mapper/OrderStatusLogMapper.java](order/src/main/java/com/b2cmall/order/dao/mapper/OrderStatusLogMapper.java) | 声明订单状态事件日志的MyBatis方法 |
| [order/src/main/java/com/b2cmall/order/dao/mapper/PaymentMapper.java](order/src/main/java/com/b2cmall/order/dao/mapper/PaymentMapper.java) | 声明支付尝试与回调条件确认的MyBatis方法 |
| [order/src/main/java/com/b2cmall/order/dao/po/OrderPO.java](order/src/main/java/com/b2cmall/order/dao/po/OrderPO.java) | 映射订单归属、金额和状态 |
| [order/src/main/java/com/b2cmall/order/dao/po/OrderSkuPO.java](order/src/main/java/com/b2cmall/order/dao/po/OrderSkuPO.java) | 保存商品名称、类型与成交价格快照 |
| [order/src/main/java/com/b2cmall/order/dao/po/OrderStatusLogPO.java](order/src/main/java/com/b2cmall/order/dao/po/OrderStatusLogPO.java) | 记录状态前后值、事件和操作员工 |
| [order/src/main/java/com/b2cmall/order/dao/po/PaymentPO.java](order/src/main/java/com/b2cmall/order/dao/po/PaymentPO.java) | 保存支付尝试、流水、金额及确认状态 |
| [order/src/main/java/com/b2cmall/order/enums/OrderEvent.java](order/src/main/java/com/b2cmall/order/enums/OrderEvent.java) | 枚举PAY、SENT和COMPLETED事件 |
| [order/src/main/java/com/b2cmall/order/enums/OrderStatus.java](order/src/main/java/com/b2cmall/order/enums/OrderStatus.java) | 枚举WAIT_PAY、PAID、SENT和COMPLETED |
| [order/src/main/java/com/b2cmall/order/enums/PayTypeEnum.java](order/src/main/java/com/b2cmall/order/enums/PayTypeEnum.java) | 限制并解析支付宝和微信渠道 |
| [order/src/main/java/com/b2cmall/order/feign/EmployeeFeignClient.java](order/src/main/java/com/b2cmall/order/feign/EmployeeFeignClient.java) | 从order服务查询已登录员工身份 |
| [order/src/main/java/com/b2cmall/order/feign/ProductFeignClient.java](order/src/main/java/com/b2cmall/order/feign/ProductFeignClient.java) | 远程查询商品报价与库存供订单使用 |
| [order/src/main/java/com/b2cmall/order/feign/response/EmployeeIdentityVO.java](order/src/main/java/com/b2cmall/order/feign/response/EmployeeIdentityVO.java) | 接收员工服务验证后的身份信息 |
| [order/src/main/java/com/b2cmall/order/feign/response/ProductVO.java](order/src/main/java/com/b2cmall/order/feign/response/ProductVO.java) | 接收商品服务报价和库存供订单流程使用 |
| [order/src/main/java/com/b2cmall/order/liteflowcmp/AlipayCmp.java](order/src/main/java/com/b2cmall/order/liteflowcmp/AlipayCmp.java) | 在流程中调用支付宝模拟策略 |
| [order/src/main/java/com/b2cmall/order/liteflowcmp/OrderStatusCmp.java](order/src/main/java/com/b2cmall/order/liteflowcmp/OrderStatusCmp.java) | 保存待确认支付记录并等待合法回调 |
| [order/src/main/java/com/b2cmall/order/liteflowcmp/PaySwitchCmp.java](order/src/main/java/com/b2cmall/order/liteflowcmp/PaySwitchCmp.java) | 将支付类型映射到支付宝或微信流程分支 |
| [order/src/main/java/com/b2cmall/order/liteflowcmp/SkuPriceCheckCmp.java](order/src/main/java/com/b2cmall/order/liteflowcmp/SkuPriceCheckCmp.java) | 校验成交快照和价格并识别重复支付 |
| [order/src/main/java/com/b2cmall/order/liteflowcmp/SkuStockCheckCmp.java](order/src/main/java/com/b2cmall/order/liteflowcmp/SkuStockCheckCmp.java) | 调用商品服务检查库存和上架状态 |
| [order/src/main/java/com/b2cmall/order/liteflowcmp/WechatCmp.java](order/src/main/java/com/b2cmall/order/liteflowcmp/WechatCmp.java) | 在流程中调用微信模拟策略 |
| [order/src/main/java/com/b2cmall/order/OrderApplication.java](order/src/main/java/com/b2cmall/order/OrderApplication.java) | 启动order服务及所需Spring配置 |
| [order/src/main/java/com/b2cmall/order/service/OrderService.java](order/src/main/java/com/b2cmall/order/service/OrderService.java) | 创建成交快照并处理本人订单查询、发货和完成 |
| [order/src/main/java/com/b2cmall/order/service/OrderStateService.java](order/src/main/java/com/b2cmall/order/service/OrderStateService.java) | 在同一事务中更新订单状态与日志 |
| [order/src/main/java/com/b2cmall/order/service/PaymentService.java](order/src/main/java/com/b2cmall/order/service/PaymentService.java) | 处理支付尝试、重复请求、签名回调和失败重试 |
| [order/src/main/java/com/b2cmall/order/service/PaySignatureService.java](order/src/main/java/com/b2cmall/order/service/PaySignatureService.java) | 生成并验证模拟渠道HMAC回调签名 |
| [order/src/main/java/com/b2cmall/order/service/PayStrategyService.java](order/src/main/java/com/b2cmall/order/service/PayStrategyService.java) | 定义支付渠道策略接口 |
| [order/src/main/java/com/b2cmall/order/service/strategy/AlipayPayStrategyService.java](order/src/main/java/com/b2cmall/order/service/strategy/AlipayPayStrategyService.java) | 实现支付宝模拟支付和交易流水生成 |
| [order/src/main/java/com/b2cmall/order/service/strategy/WechatPayStrategyService.java](order/src/main/java/com/b2cmall/order/service/strategy/WechatPayStrategyService.java) | 实现微信模拟支付和交易流水生成 |
| [order/src/main/java/com/b2cmall/order/web/filter/OrderAuthenticationFilter.java](order/src/main/java/com/b2cmall/order/web/filter/OrderAuthenticationFilter.java) | 校验订单请求身份并清理线程上下文 |
| [order/src/main/java/com/b2cmall/order/web/OrderController.java](order/src/main/java/com/b2cmall/order/web/OrderController.java) | 提供下单、查询、发货和完成接口 |
| [order/src/main/java/com/b2cmall/order/web/PayController.java](order/src/main/java/com/b2cmall/order/web/PayController.java) | 执行LiteFlow、检查结果并接收模拟回调 |
| [order/src/main/java/com/b2cmall/order/web/request/CreateOrderRequestVO.java](order/src/main/java/com/b2cmall/order/web/request/CreateOrderRequestVO.java) | 接收商品ID与可选客户端报价 |
| [order/src/main/java/com/b2cmall/order/web/request/OrderActionRequestVO.java](order/src/main/java/com/b2cmall/order/web/request/OrderActionRequestVO.java) | 校验发货和完成请求的订单ID |
| [order/src/main/java/com/b2cmall/order/web/request/PayCallbackRequestVO.java](order/src/main/java/com/b2cmall/order/web/request/PayCallbackRequestVO.java) | 承接支付流水、金额、结果及签名 |
| [order/src/main/java/com/b2cmall/order/web/request/PayRequestVO.java](order/src/main/java/com/b2cmall/order/web/request/PayRequestVO.java) | 接收订单ID与支付渠道 |
| [order/src/main/java/com/b2cmall/order/web/response/OrderResponseVO.java](order/src/main/java/com/b2cmall/order/web/response/OrderResponseVO.java) | 返回订单状态和商品成交快照 |
| [order/src/main/java/com/b2cmall/order/web/response/PaymentResponseVO.java](order/src/main/java/com/b2cmall/order/web/response/PaymentResponseVO.java) | 返回支付结果及课堂模拟成功与失败通知 |
| [order/src/main/resources/application.yml](order/src/main/resources/application.yml) | 配置order服务的Nacos导入与运行参数 |
| [order/src/main/resources/config/flow.xml](order/src/main/resources/config/flow.xml) | 编排价格、库存、支付分支与支付记录流程 |
| [order/src/main/resources/mapper/OrderMapper.xml](order/src/main/resources/mapper/OrderMapper.xml) | 实现订单写入、归属查询和条件状态更新的SQL和字段映射 |
| [order/src/main/resources/mapper/OrderSkuMapper.xml](order/src/main/resources/mapper/OrderSkuMapper.xml) | 实现订单成交快照的SQL和字段映射 |
| [order/src/main/resources/mapper/OrderStatusLogMapper.xml](order/src/main/resources/mapper/OrderStatusLogMapper.xml) | 实现订单状态事件日志的SQL和字段映射 |
| [order/src/main/resources/mapper/PaymentMapper.xml](order/src/main/resources/mapper/PaymentMapper.xml) | 实现支付尝试与回调条件确认的SQL和字段映射 |
| [order/src/main/resources/schema.sql](order/src/main/resources/schema.sql) | 建立order独立数据库表、索引和约束 |

## gateway

| 文件 | 用途 |
| --- | --- |
| [gateway/pom.xml](gateway/pom.xml) | 声明gateway模块依赖及打包方式 |
| [gateway/src/main/java/com/b2cmall/gateway/config/GatewayAuthConfig.java](gateway/src/main/java/com/b2cmall/gateway/config/GatewayAuthConfig.java) | 配置网关的员工鉴权客户端 |
| [gateway/src/main/java/com/b2cmall/gateway/filter/AuthFilter.java](gateway/src/main/java/com/b2cmall/gateway/filter/AuthFilter.java) | 执行网关鉴权、路径检查和伪造身份头清理 |
| [gateway/src/main/java/com/b2cmall/gateway/filter/LoggingFilter.java](gateway/src/main/java/com/b2cmall/gateway/filter/LoggingFilter.java) | 记录请求路径、响应状态与耗时 |
| [gateway/src/main/java/com/b2cmall/gateway/filter/WhiteUrlFilter.java](gateway/src/main/java/com/b2cmall/gateway/filter/WhiteUrlFilter.java) | 按方法及完整路径标记公开入口 |
| [gateway/src/main/java/com/b2cmall/gateway/GatewayApplication.java](gateway/src/main/java/com/b2cmall/gateway/GatewayApplication.java) | 启动gateway服务及所需Spring配置 |
| [gateway/src/main/java/com/b2cmall/gateway/service/EmployeeAuthClient.java](gateway/src/main/java/com/b2cmall/gateway/service/EmployeeAuthClient.java) | 调用员工checkToken并区分无效登录和服务故障 |
| [gateway/src/main/resources/application.yml](gateway/src/main/resources/application.yml) | 配置gateway服务的Nacos导入与运行参数 |
