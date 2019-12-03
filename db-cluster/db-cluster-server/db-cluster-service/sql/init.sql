-- done
DROP TABLE IF EXISTS `cluster_info`;
CREATE TABLE `cluster_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `cluster_name` varchar(64) NOT NULL DEFAULT '' COMMENT 'cluster名称',
  `db_category` varchar(64) NOT NULL DEFAULT 'mysql' COMMENT '数据库类型',
  `enabled` tinyint NOT NULL DEFAULT 1 COMMENT '是否可用, 0:禁用; 1:启用',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除, 0:否; 1:是',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `release_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `release_version` int(11) NOT NULL DEFAULT 0 COMMENT '版本号',
  `datachange_lasttime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `index_datachange_lasttime` (`datachange_lasttime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='cluster表';

-- done
DROP TABLE IF EXISTS `cluster_set`;
CREATE TABLE `cluster_set` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `cluster_id` int(11) NOT NULL DEFAULT 0 COMMENT 'cluster_id',
  `set_id` varchar(16) NOT NULL DEFAULT '' COMMENT '所属set, e.g:SHAJQ',
  `region` varchar(64) NOT NULL DEFAULT '' COMMENT '所属区域, e.g:上海',
  `enabled` tinyint NOT NULL DEFAULT 1 COMMENT '是否可用, 0:禁用; 1:启用',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除, 0:否; 1:是',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `datachange_lasttime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `index_datachange_lasttime` (`datachange_lasttime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='cluster_set表';

-- done
DROP TABLE IF EXISTS `shard_info`;
CREATE TABLE `shard_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `shard_index` int(11) NOT NULL DEFAULT 0 COMMENT 'shard索引',
  `cluster_id` int(11) NOT NULL DEFAULT 0 COMMENT 'cluster_id',
  `set_id` varchar(16) NOT NULL DEFAULT '' COMMENT '所属set, e.g:SHAJQ',
  `db_name` varchar(64) NOT NULL DEFAULT '' COMMENT '数据库名称',
  `master_domain` varchar(128) NOT NULL DEFAULT '' COMMENT 'Master域名',
  `master_port` int(11) NOT NULL DEFAULT 0 COMMENT 'Master端口',
  `slave_domain` varchar(128) NOT NULL DEFAULT '' COMMENT 'Slave域名',
  `slave_port` int(11) NOT NULL DEFAULT 0 COMMENT 'Slave端口',
  `read_domain` varchar(128) NOT NULL DEFAULT '' COMMENT 'read域名',
  `read_port` int(11) NOT NULL DEFAULT 0 COMMENT 'read端口',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除, 0:否; 1:是',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `datachange_lasttime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `index_datachange_lasttime` (`datachange_lasttime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='shard表';

-- done
DROP TABLE IF EXISTS `instance_info`;
CREATE TABLE `instance_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `ip` varchar(64) NOT NULL DEFAULT '' COMMENT 'ip地址',
  `port` int(11) NOT NULL DEFAULT 0 COMMENT '端口',
  `idc` varchar(64) NOT NULL DEFAULT '' COMMENT '所属机房',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除, 0:否; 1:是',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `datachange_lasttime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `index_datachange_lasttime` (`datachange_lasttime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='instance表';

-- done
DROP TABLE IF EXISTS `shard_instance`;
CREATE TABLE `shard_instance` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `shard_id` int(11) NOT NULL DEFAULT 0 COMMENT 'shard_id',
  `instance_id` int(11) NOT NULL DEFAULT 0 COMMENT 'instance_id',
  `role` varchar(64) NOT NULL DEFAULT '' COMMENT '角色',
  `read_weight` tinyint NOT NULL DEFAULT 1 COMMENT '读权重',
  `tags` varchar(128) NOT NULL DEFAULT '' COMMENT '标签',
  `member_status` tinyint NOT NULL DEFAULT 1 COMMENT '人为控制是否可用, 0:禁用; 1:启用',
  `health_status` tinyint NOT NULL DEFAULT 1 COMMENT '健康检测结果控制是否可用, 0:禁用; 1:启用',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除, 0:否; 1:是',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `datachange_lasttime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `index_datachange_lasttime` (`datachange_lasttime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='shard_instance表';

-- done
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `cluster_id` int(11) NOT NULL COMMENT 'cluster_id',
  `shard_index` int(11) NOT NULL COMMENT 'shard_index',
  `username` varchar(128) NOT NULL COMMENT '用户名',
  `password` varchar(128) NOT NULL COMMENT '密码',
  `permission` varchar(64) NOT NULL COMMENT '账号权限',
  `tag` varchar(128) NOT NULL DEFAULT '' COMMENT '标签',
  `titan_key` varchar(128) NULL COMMENT 'titan_key',
  `enabled` tinyint NOT NULL DEFAULT 1 COMMENT '是否可用, 0:禁用; 1:启用',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除, 0:否; 1:是',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `datachange_lasttime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `index_datachange_lasttime` (`datachange_lasttime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='user_info表';

-- done
DROP TABLE IF EXISTS `cluster_extension_config`;
CREATE TABLE `cluster_extension_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `cluster_id` int(11) NOT NULL COMMENT 'cluster_id',
  `content` text NOT NULL COMMENT '扩展配置内容, maxsize=64KB',
  `type` tinyint NOT NULL COMMENT '扩展配置类型, 0:ShardStrategies; 1:IdGenerators',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除, 0:否; 1:是',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `datachange_lasttime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `index_datachange_lasttime` (`datachange_lasttime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='集群扩展配置';

-- done
DROP TABLE IF EXISTS `titan_key`;
CREATE TABLE `titan_key` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(128) NOT NULL COMMENT 'titan key名称',
  `sub_env` varchar(20) NOT NULL DEFAULT '' COMMENT '子环境',
  `enabled` tinyint NOT NULL DEFAULT 1 COMMENT '是否可用, 0:禁用; 1:启用',
  `provider_name` varchar(128) NOT NULL COMMENT '驱动名称',
  `create_user` varchar(128) COMMENT '创建用户',
  `update_user` varchar(128) COMMENT '最后修改用户',
  `permissions` varchar(10240) COMMENT '权限允许的应用',
  `free_verify_ips` varchar(2048) COMMENT '免校验的ip地址',
  `free_verify_apps` varchar(2048) COMMENT '免cms关系校验的应用',
  `mha_last_update_time` varchar(64) COMMENT '最后切换时间',
  `domain` varchar(128) NOT NULL DEFAULT '' COMMENT '域名',
  `ip` varchar(64) COMMENT 'ip地址',
  `port` int(11) COMMENT '端口',
  `username` varchar(128) COMMENT '用户名',
  `password` varchar(128) COMMENT '密码',
  `db_name` varchar(64) NOT NULL COMMENT '数据库名称',
  `ext_params` varchar(2048) COMMENT '其他参数',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `datachange_lasttime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_name_sub_env` (`name`, `sub_env`),
  KEY `index_name` (`name`),
  KEY `index_domain` (`domain`),
  KEY `index_datachange_lasttime` (`datachange_lasttime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='titan_key表';

ALTER TABLE `fxdalclusterdb`.`cluster_info` ADD `type` tinyint NOT NULL DEFAULT 0 COMMENT '集群类型, e.g,0:普通集群; 1:DRC集群' AFTER `cluster_name`;
ALTER TABLE `fxdalclusterdb`.`cluster_info` ADD `zone_id` varchar(16) NOT NULL DEFAULT "" COMMENT '集群类型为普通类型时, zoneId对应的zone是该集群所有应用都在使用的zone' AFTER `type`;
ALTER TABLE `fxdalclusterdb`.`cluster_info` ADD `unit_strategy_id` int(11) NOT NULL DEFAULT 0 COMMENT '单元化策略id' AFTER `zone_id`;
ALTER TABLE `fxdalclusterdb`.`cluster_info` ADD `unit_strategy_name` varchar(64) NOT NULL DEFAULT "" COMMENT '单元化策略名称' AFTER `unit_strategy_id`;
ALTER TABLE `fxdalclusterdb`.`cluster_info` ADD `shard_strategies` text NULL COMMENT '分片策略' AFTER `unit_strategy_name`;
ALTER TABLE `fxdalclusterdb`.`cluster_info` ADD `id_generators` text NULL COMMENT 'id生成策略' AFTER `shard_strategies`;

DROP TABLE IF EXISTS `table_config`;
CREATE TABLE `table_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `cluster_id` int(11) NOT NULL DEFAULT 0 COMMENT 'cluster_id',
  `table_name` varchar(128) NOT NULL DEFAULT "" COMMENT '表名',
  `unit_shard_column` varchar(128) NOT NULL DEFAULT "" COMMENT '单元化字段名',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除, 0:否; 1:是',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `datachange_lasttime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `index_datachange_lasttime` (`datachange_lasttime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='table_config表';