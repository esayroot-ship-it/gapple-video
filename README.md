# 影视平台系统说明文档 (演示地址 `lingyao31.top`)


## 1. 项目概览

本项目是基于 Spring Boot 3.2.11 的多模块后端工程，主要用于影视内容管理与用户侧访问。（个人练习项目）😊

模块划分如下：

1. `ap-common`
- 公共实体、DAO、DTO、通用工具类。
- RedisTemplate、全局异常处理、拼音工具等基础能力。

2. `ap-user`
- 用户侧业务接口与服务实现。
- 包含搜索、互动、收藏、订阅、反馈、历史记录等能力。
- 包含 RabbitMQ 消费者与 Meilisearch 相关实现。

3. `ap-admin`
- 管理侧接口与主启动模块。
- 当前启动类为 `ApAdminApplication`，会扫描 `ap-admin`、`ap-user`、`ap-common` 包。
- 即：项目运行时由 `ap-admin` 启动，统一暴露管理端和用户端接口。

## 2. 已实现功能（按模块）

### 2.1 管理端（`com.ap.apadmin.controller`）

1. 管理员与用户管理
- 管理员登录、创建、删除、列表查询。
- 用户创建/更新（含头像）、按用户名查询、封禁/解封。

2. 内容管理
- 分类管理：创建、删除、更新、查询。
- 剧集管理：创建、更新、删除、查询。
- 分集管理：创建、删除、按剧集查询。
- 视频管理：新增（含文件上传）、更新、删除、查询。

3. 运营管理
- 轮播图管理（Banner）：增删改查、状态更新。
- 公告管理（Announcement）：增删改查、状态更新。
- 热门榜单（Hot）：查询、新增、删除。

4. 反馈处理
- 反馈列表查询、状态更新、删除。

### 2.2 用户端（`com.ap.apservice.controller`）

1. 账号与资料
- 用户注册、登录。
- 设置昵称、设置头像、按 ID 获取用户信息。

2. 内容浏览与搜索
- 分类、轮播图、公告、热搜列表查询。
- 视频查询：按分类、按剧集、按 ID、按标题搜索。
- 剧集查询：全部、按分类、按 ID、按标题搜索。
- 标签/话题列表查询。

3. 互动与个人中心
- 评论与评论点赞。
- 视频评分（新增、删除、按用户查询、按视频查询）。
- 收藏管理（新增、列表、删除）。
- 订阅管理（新增、取消、列表、是否已订阅）。
- 观看历史（新增、列表）。
- 用户反馈提交与查询/删除。

## 3. 中间件与基础设施

1. MySQL + MyBatis-Plus
- 业务数据持久化与 ORM 映射。

2. Redis
- 使用自定义 `RedisTemplate<String, Object>`（Jackson 序列化，支持时间类型）。
- `DataLoader` 在启动时预热部分数据到 Redis（公告、用户信息、Banner）。

3. RabbitMQ
- 交换机：`feedback.exchange`（Direct）。
- 队列：`feedback.queue`、`video.sync.queue`、`series.sync.queue`。
- 路由键：`feedback.save`、`video.sync`、`series.sync`。
- 用途：
  - 用户反馈异步入库。
  - 视频/剧集变更消息驱动搜索索引增量同步。

4. Knife4j / OpenAPI
- 已配置 `admin` 与 `user` 两个接口分组，便于联调与文档查看。

## 4. Meilisearch 中间件说明（新增）

### 4.1 接入位置

Meilisearch 相关逻辑位于 `ap-user` 模块：

1. `config/MeiliSearchConfig.java`
- 注入配置项：
  - `meilisearch.host`（默认 `http://localhost:7700`）
  - `meilisearch.api-key`（默认 `masterKey`）
- 启动时初始化索引：`videos`、`series`（主键 `id`）。
- 设置可搜索字段：`title`、`titlePinyin`、`description`。

2. 搜索调用
- `videoIm.getVideoByTitle()`：先查 Meilisearch，再按命中 ID 回表过滤 `status=1`。
- `seriesIm.getSeriesByTitle()`：同样先走 Meilisearch，再回表过滤状态。

3. 全量同步接口
- `SearchSyncController`：`GET /u/search/sync/full`
- 从数据库读取上架数据（`status=1`）批量写入 `videos`、`series` 索引。

4. 增量同步消费者
- `SearchSyncConsumer`
- 监听 `video.sync.queue`、`series.sync.queue`。
- 上架/更新：写入 Meilisearch 文档。
- 下架/删除：删除 Meilisearch 文档。

### 4.2 同步链路

1. 管理端新增/更新/删除视频或剧集。
2. `ap-admin` 通过 RabbitTemplate 发送 `video.sync` 或 `series.sync` 消息。
3. `ap-user` 的 `SearchSyncConsumer` 消费消息并更新 Meilisearch 索引。
4. 用户端搜索接口优先走 Meilisearch。

### 4.3 故障降级

当 Meilisearch 查询异常时：

- 视频搜索回退为数据库 `title like` 查询。
- 剧集搜索回退为数据库 `title like` 查询。

该策略保证搜索服务在搜索引擎异常时仍可用，但查询性能和相关性会下降。
