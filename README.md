## 限制IP的高频访问

转载至：https://blog.csdn.net/qq_42595077/article/details/84831004

对高频ip做限制，防止不法ip攻击，去网上看了很多方法，最多的就是自定义注解，然后定义一个注解的实现类利用aop去验证该限制接口的请求ip是否符合，但是缺点就是我们不可能每一个接口都要去加注解。
还有一种方法就是利用过滤器去验证每一个请求的ip

过滤器：统计用户访问次数，记录访问时间、封禁时间

监听器：工程运行时初始化IP存储器（此处用的Map）

然后在SpringBoot启动类添加扫描过滤器
```java
@ServletComponentScan("com.qqlin.ipFilterDemo")
```
