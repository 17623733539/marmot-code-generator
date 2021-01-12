[TOC]
# 项目介绍
用于生成项目中的CRUD环节代码

# 项目使用介绍
因为考虑到后期扩展从外部加载,目前jar包未上传Maven仓库，测试版本
运行test generator ｜ new CodeBuilder("外部配置文件绝对路径").generator("使用项目中的实体");

配置文件介绍：
module.entity.api
module.entity
module.repository
module.service
module.controller
#对应各层次的相对路径
repository.package
service.package
controller.package
#对应生成代码的位置
controller.prefix
#controller mapping前缀

# 后期更新
1.开发项目中引入jar包，调用generator在该项目中生成代码
2.添加策略模式对不同持久层生成不同代码（mybatis|mybatis_plus|beetlSql）
3.终极版本:传入数据库链接生成项目


#注意事项
entity层麻烦对应分层，不要直接放在entity下，生成代码会取上级目录