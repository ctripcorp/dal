# 简介
Ctrip DAL是携程框架部开发的数据库访问框架，支持代码生成和水平扩展。其由携程技术中心框架部DAL团队开发，历经3年不断打磨，并在长期的实际使用中基于大量的用户反馈不断优化。

开源范围包括代码生成器，Java客户端和C#客户端。

**注：C#客户端于2016年9月29日也正式开源了**

# 背景
随着企业规模扩张和业务量的急剧增加，作为系统核心的数据库相关开发也会经历一个由单一团队发展为多团队；由单机扩张到集群；由单数据库发展为多数据库；由采用单一数据库产品到多种数据库产品并存的过程。伴随这一过程的是如何管理数据库扩展，如何规范数据库访问，如何保护数据库投资，如何应对访问量增加，如何预防安全问题等一系列挑战。作为中国在线旅游行业的翘楚，携程也曾经面对同样困扰。为了应对这些挑战，实现企业10倍速发展，携程开发了具有自己特色的数据库访问框架Ctrip DAL

Ctrip DAL支持流行的分库分表操作，支持Java和C#，支持Mysql和MS SqlServer。使用该框架可以在有效地保护企业已有数据库投资的同时，迅速，可靠的为企业提供数据库访问层的横向扩展能力。整个框架包括代码生成器和客户端。工作模式是使用代码生成器在线生成代码和配置，通过DAL客户端完成数据库操作。生成器具有丰富的向导指引，操作简单清晰，即可以批量生成标准DAO，也可以在方法级别定制数据库访问。客户端则可以简单的通过标准的maven方式添加依赖

![model](https://github.com/ctripcorp/dal/blob/master/doc/codegen_work_model.png)

Ctrip DAL与一般数据库框架最大的不同是从企业跨部门的角度，统一管理数据库相关资源。通过部署代码生成器，企业可以做到有效的管理全公司的DAL开发团队，明确数据库归属和定制数据库访问。通过代码生成器生成的标准DAO代码与客户端配合使用，可以大幅提高工作效率，保证代码质量。解决了业内常见的伴随业务成长而带来的系统维护困难，开发效率低下，代码风格五花八门，代码质量参差不齐等痛点问题。

![overview](https://github.com/ctripcorp/dal/blob/master/doc/codegen_overview.png)

### 代码生成器简介
代码生成器允许用户创建Dal团队，组织开发人员，管理数据库，创建DAO并生成代码和配置。与一般基于JDBC driver的DB sharding产品不同的是，代码生成器生成的代码和配置可以直接拿来实用，完全无需用户写一行代码和配置。做到了只需开发人员关心业务逻辑，而把繁琐的数据库相关的编码和配置任务全部交给DAL。由于Ctrip DAL完全在DAO这层工作，也没有什么这种SQL语句不支持，那种SQL语句不能用的情况。同时传递hints的方式也非常自然，每个方法都自带hints的接口，需要DAL额外做什么可以直接按给定的已有名字来设置，无需改写原始的sql来添加怪异的注释。

### 客户端简介
客户端配合代码生成器生成的代码来完成用户的数据库访问操作。通过Dev和QA两方面双重自动化测试来保障质量，覆盖率达到99％，并经过生产实际实用的的长期严格检验。为了适应不同公司的实际情况，DAL客户端定义了丰富的扩展接口，覆盖了从数据源管理，数据库映射，连接串读取到自定义访问方式等等方方面面的功能。同时为了方便系统监控还内置了系统状态，日志和统计模块。

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
为了支持携程前CTO叶亚明先生（现首席科学家）主导的10X提速战略规划，由前携程系统研发部总监陈绍明先生（Simon Chen）提出该框架最初的构想，陈绍明同时组建了开发团队并决定了该框架的路线图和重大设计决策。
## 产品经理
孟文超。主要负责DAL产品的总体需求

## 当前开发人员
* 赫杰辉。Java客户端总体设计与开发，代码生成器Java模板
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

[C#客户端 集成说明](https://github.com/ctripcorp/dal/wiki/C%23%E5%AE%A2%E6%88%B7%E7%AB%AF-%E9%9B%86%E6%88%90%E8%AF%B4%E6%98%8E)

[C#客户端 配置文件](https://github.com/ctripcorp/dal/wiki/C%23%E5%AE%A2%E6%88%B7%E7%AB%AF-%E9%85%8D%E7%BD%AE%E6%96%87%E4%BB%B6)

[Java客户端 Demo](https://github.com/ctripcorp/dal/wiki/Java%E5%AE%A2%E6%88%B7%E7%AB%AF-%E9%9B%86%E6%88%90%E8%AF%B4%E6%98%8E#demo-project)

## 技术支持
[携程框架DAL团队](mailto:rdfxdal@Ctrip.com)

![](https://github.com/ctripcorp/dal/blob/master/doc/Tech_Support_QQ.png)

## 已接入公司
请在[这里](https://github.com/ctripcorp/dal/issues/22)提供您的公司名称和网址

![ctrip](https://github.com/ctripcorp/dal/blob/master/doc/known-users/ctrip.png)
![1hai](https://github.com/ctripcorp/dal/blob/master/doc/known-users/1hai.png)
![dj](https://github.com/ctripcorp/dal/blob/master/doc/known-users/dj.png)
![imedmaster](https://github.com/ctripcorp/dal/blob/master/doc/known-users/imedmaster.png)
![yiguo](https://github.com/ctripcorp/dal/blob/master/doc/known-users/yiguo.png)
=======
# 简介
Ctrip DAL是携程框架部开发的数据库访问框架，支持代码生成和水平扩展。其由携程技术中心框架部DAL团队开发，历经3年不断打磨，并在长期的实际使用中基于大量的用户反馈不断优化。

开源范围包括代码生成器，Java客户端和C#客户端。

**注：C#客户端于2016年9月29日也正式开源了**

# 背景
随着企业规模扩张和业务量的急剧增加，作为系统核心的数据库相关开发也会经历一个由单一团队发展为多团队；由单机扩张到集群；由单数据库发展为多数据库；由采用单一数据库产品到多种数据库产品并存的过程。伴随这一过程的是如何管理数据库扩展，如何规范数据库访问，如何保护数据库投资，如何应对访问量增加，如何预防安全问题等一系列挑战。作为中国在线旅游行业的翘楚，携程也曾经面对同样困扰。为了应对这些挑战，实现企业10倍速发展，携程开发了具有自己特色的数据库访问框架Ctrip DAL

Ctrip DAL支持流行的分库分表操作，支持Java和C#，支持Mysql和MS SqlServer。使用该框架可以在有效地保护企业已有数据库投资的同时，迅速，可靠的为企业提供数据库访问层的横向扩展能力。整个框架包括代码生成器和客户端。工作模式是使用代码生成器在线生成代码和配置，通过DAL客户端完成数据库操作。生成器具有丰富的向导指引，操作简单清晰，即可以批量生成标准DAO，也可以在方法级别定制数据库访问。客户端则可以简单的通过标准的maven方式添加依赖

![model](https://github.com/ctripcorp/dal/blob/master/doc/codegen_work_model.png)

Ctrip DAL与一般数据库框架最大的不同是从企业跨部门的角度，统一管理数据库相关资源。通过部署代码生成器，企业可以做到有效的管理全公司的DAL开发团队，明确数据库归属和定制数据库访问。通过代码生成器生成的标准DAO代码与客户端配合使用，可以大幅提高工作效率，保证代码质量。解决了业内常见的伴随业务成长而带来的系统维护困难，开发效率低下，代码风格五花八门，代码质量参差不齐等痛点问题。

![overview](https://github.com/ctripcorp/dal/blob/master/doc/codegen_overview.png)

### 代码生成器简介
代码生成器允许用户创建Dal团队，组织开发人员，管理数据库，创建DAO并生成代码和配置。与一般基于JDBC driver的DB sharding产品不同的是，代码生成器生成的代码和配置可以直接拿来实用，完全无需用户写一行代码和配置。做到了只需开发人员关心业务逻辑，而把繁琐的数据库相关的编码和配置任务全部交给DAL。由于Ctrip DAL完全在DAO这层工作，也没有什么这种SQL语句不支持，那种SQL语句不能用的情况。同时传递hints的方式也非常自然，每个方法都自带hints的接口，需要DAL额外做什么可以直接按给定的已有名字来设置，无需改写原始的sql来添加怪异的注释。

### 客户端简介
客户端配合代码生成器生成的代码来完成用户的数据库访问操作。通过Dev和QA两方面双重自动化测试来保障质量，覆盖率达到99％，并经过生产实际实用的的长期严格检验。为了适应不同公司的实际情况，DAL客户端定义了丰富的扩展接口，覆盖了从数据源管理，数据库映射，连接串读取到自定义访问方式等等方方面面的功能。同时为了方便系统监控还内置了系统状态，日志和统计模块。

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
为了支持携程前CTO叶亚明先生（现首席科学家）主导的10X提速战略规划，由前携程系统研发部总监陈绍明先生（Simon Chen）提出该框架最初的构想，陈绍明同时组建了开发团队并决定了该框架的路线图和重大设计决策。
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

[C#客户端 集成说明](https://github.com/ctripcorp/dal/wiki/C%23%E5%AE%A2%E6%88%B7%E7%AB%AF-%E9%9B%86%E6%88%90%E8%AF%B4%E6%98%8E)

[C#客户端 配置文件](https://github.com/ctripcorp/dal/wiki/C%23%E5%AE%A2%E6%88%B7%E7%AB%AF-%E9%85%8D%E7%BD%AE%E6%96%87%E4%BB%B6)

[Java客户端 Demo](https://github.com/ctripcorp/dal/wiki/Java%E5%AE%A2%E6%88%B7%E7%AB%AF-%E9%9B%86%E6%88%90%E8%AF%B4%E6%98%8E#demo-project)

## 技术支持
[携程框架DAL团队](mailto:rdfxdal@Ctrip.com)

![](https://github.com/ctripcorp/dal/blob/master/doc/Tech_Support_QQ.png)
