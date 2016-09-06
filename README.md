# 简介
Ctrip DAL是携程框架部开发的数据库访问框架，支持分库分表操作。该框架包括代码生成器和客户端。

### 代码生成器简介
代码生成器允许用户创建Dal团队，组织开发人员，管理数据库，创建DAO并生成代码。

### 客户端简介
客户端配合代码生成器生成的代码来完成用户的数据库访问操作。

通过DAL可以做到

1. 在公司范围提供统一的数据库访问方式以降低总体的开发成本
2. 避免由于技术人员水平不同而造成的相同功能实现方式，标准，质量不一至
3. 集中优势人员，将相关的功能做深，做好，做稳定，做全面
4. 出现技术问题的时候有专门的技术团队及时支持。减低总体的风险

# 使用概况
目前携程超过117个独立DAL团队通过Dal Code Gen管理数据库和创建DAO。

2000多个应用在使用DAL框架，占携程所有数据库应用总数超过90%。

支持2种主流编程语言：Java和C#。

支持2种主流数据库Mysql和MS SqlServer。

# 开发团队
## 发起人
该框架最初的构想由前携程系统研发部总监陈绍明先生（Simon Chen）提出，为支持携程10X提速战略规划。陈绍明同时组建了开发团队并决定了该框架的路线图和重大设计决策。
## 当前开发人员
* 赫杰辉。主要负责DAL产品的总体需求。Java客户端总体设计与开发，代码生成器Java模板
* 王晔楠。主要负责代码生成器，C#客户端
* 李龙娇。测试负责人，自动化测试开发

## 历史贡献者
* 原酒店部门Dal开发团队。最初版本的C#客户端和桌面版代码生成器的开发
* 吴广安。早期版本的C#客户端重构和网页版代码生成器的创建
* 袁王成。Java客户端HA，Markdown，AppInternal模块和代码生成器开发
* 夏光智。代码生成器，数据源管理
* 万国新。C#客户端
* 张钰。总体测试

## 常用参考文档
[简介](https://github.com/ctripcorp/dal/wiki)

[代码生成器(CodeGen)安装说明](https://github.com/ctripcorp/dal/wiki/%E4%BB%A3%E7%A0%81%E7%94%9F%E6%88%90%E5%99%A8(CodeGen)%E5%AE%89%E8%A3%85%E8%AF%B4%E6%98%8E)

[代码生成器(CodeGen)使用说明](https://github.com/ctripcorp/dal/wiki/%E4%BB%A3%E7%A0%81%E7%94%9F%E6%88%90%E5%99%A8(CodeGen)%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E)

[Java客户端 集成说明](https://github.com/ctripcorp/dal/wiki/Java%E5%AE%A2%E6%88%B7%E7%AB%AF-%E9%9B%86%E6%88%90%E8%AF%B4%E6%98%8E)

[Java客户端 配置与扩展](https://github.com/ctripcorp/dal/wiki/Java%E5%AE%A2%E6%88%B7%E7%AB%AF-%E9%85%8D%E7%BD%AE%E4%B8%8E%E6%89%A9%E5%B1%95)

## 技术支持
[携程框架DAL团队](mailto:rdfxdal@Ctrip.com)
