# seckill
springboot+mybatis+redis+rabbitmq+mysql

本项目使用springboot+mysql+mybatis+redis+rabbitmq技术栈。

我们使用thymeleaf作为模板引擎，利用接口封装了一个result结果集，方便在方法返回时返回我们需要的信息。
利用redis作为我们的缓存中间件，当用户查询个人信息数据的时候，
首先会检查缓存中是否存在，如果存在，首先查缓存中的数据，其次再查数据库的数据。
我们还利用缓存作为我们的页面缓存，URL缓存。

我们进行了前后端分离，前端使用纯html代码，后端提供接口数据，我们使用ajax进行前后端的数据传输。

我们利用rabbitmq进行异步处理消息和流量削峰，当有大量的用户请求时，我们首先将消息发送给rabbitmq服务器，
然后我们在rabbitmq服务器中规定服务器每次处理请求的数量，然后再将请求转发给服务器进行处理。

最后我们还进行了隐藏秒杀地址，接口限流防刷，图形验证码功能。
