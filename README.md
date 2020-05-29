# seckill
springboot+mybatis+redis+rabbitmq+mysql

该项目是高并发的秒杀系统，提供对促销商品秒杀服务

本项目使用springboot+mysql+mybatis+redis+rabbitmq技术栈。

我们使用thymeleaf作为模板引擎，利用接口封装了一个result结果集，方便在方法返回时返回我们需要的信息。
利用redis作为我们的缓存中间件，当用户查询个人信息数据的时候，
首先会检查缓存中是否存在，如果存在，首先查缓存中的数据，其次再查数据库的数据。
我们还利用缓存作为我们的页面缓存，URL缓存。

使用 HashMap+Redis 做一二级缓存，进行预减库存

我们进行了前后端分离，前端使用纯html代码，后端提供接口数据，我们使用ajax进行前后端的数据传输。

使用 Redis 缓存 token 令牌，便于用户进行身份验证

使用 RabbitMq 消息队列异步下单削峰，减少秒杀高峰时的压力

使用 Jmeter 工具进行压测，实时记录 QPS 进行对比

最后使用秒杀接口进行地址隐藏，验证码防止机器人，接口限流防刷




