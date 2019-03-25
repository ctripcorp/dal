# 项目使用说明

**代码中src/main/java/com.ctrip.framework.db.cluster.controller.WelcomeController以及src/main/resources/templates/welcome.flt是样例Controller以及样例freemarker模板，可以随时参看**

## 新增Controller
1. 在src/main/java/com.ctrip.framework.db.cluster/controller或者其子package中实现自己的Controller，为其添加annotation `@Controller`
2. Controller中可以通过`@Autowired`注入需要的其他服务
3. 如果需要一个freemarker模板，可以在src/main/resources/templates/下添加


## 本地运行调试
- 通过src/test/java/com.ctrip.framework.db.cluster/WebStarter.java进行运行或者调试，启动时自动通过embedded Tomcat进行服务发布

## 测试环境或者生产环境发布
- 直接Maven打包即可

## 备注
- 通过src/test/resources/application.properties中的`server.port`进行本地运行时服务端口修改，测试环境或者生产环境的端口不受此配置控制