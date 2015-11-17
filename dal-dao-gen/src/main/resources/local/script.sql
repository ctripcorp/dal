CREATE TABLE IF NOT EXISTS `alldbs` (
	`id` INT (11) NOT NULL AUTO_INCREMENT
	,`dbname` VARCHAR(100) NULL DEFAULT NULL COLLATE 'utf8_bin'
	,`comment` TEXT NULL
	,`dal_group_id` INT (11) NULL DEFAULT NULL
	,`db_address` VARCHAR(200) NULL DEFAULT NULL
	,`db_port` VARCHAR(20) NULL DEFAULT NULL
	,`db_user` VARCHAR(100) NULL DEFAULT NULL
	,`db_password` VARCHAR(200) NULL DEFAULT NULL
	,`db_catalog` VARCHAR(200) NULL DEFAULT NULL
	,`db_providerName` VARCHAR(100) NULL DEFAULT NULL
	,PRIMARY KEY (`id`)
	,UNIQUE INDEX `unique_key`(`dbname`)
	,INDEX `FK_Reference_3`(`dal_group_id`)
	);

CREATE TABLE IF NOT EXISTS `api_list` (
	`id` INT (11) NOT NULL AUTO_INCREMENT
	,`language` VARCHAR(45) NULL DEFAULT NULL
	,`db_type` VARCHAR(45) NULL DEFAULT NULL
	,`crud_type` VARCHAR(45) NULL DEFAULT NULL
	,`method_declaration` VARCHAR(200) NULL DEFAULT NULL
	,`method_description` TEXT NULL
	,`sp_type` VARCHAR(45) NULL DEFAULT NULL
	,PRIMARY KEY (`id`)
	);

CREATE TABLE IF NOT EXISTS `approve_task` (
	`id` INT (11) NOT NULL AUTO_INCREMENT
	,`task_id` INT (11) NULL DEFAULT NULL
	,`task_type` VARCHAR(45) NULL DEFAULT NULL
	,`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
	,`create_user_id` INT (11) NULL DEFAULT NULL
	,`approve_user_id` INT (11) NULL DEFAULT NULL
	,PRIMARY KEY (`id`)
	);

CREATE TABLE IF NOT EXISTS `dal_group` (
	`id` INT (11) NOT NULL AUTO_INCREMENT
	,`group_name` VARCHAR(100) NULL DEFAULT NULL
	,`group_comment` TEXT NULL
	,`create_user_no` VARCHAR(45) NULL DEFAULT NULL
	,`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
	,PRIMARY KEY (`id`)
	);

CREATE TABLE IF NOT EXISTS `databaseset` (
	`id` INT (11) NOT NULL AUTO_INCREMENT
	,`name` VARCHAR(150) NOT NULL
	,`provider` VARCHAR(100) NULL DEFAULT NULL
	,`shardingStrategy` TEXT NULL
	,`groupId` INT (11) NULL DEFAULT NULL
	,`update_user_no` VARCHAR(45) NULL DEFAULT NULL
	,`update_time` TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
	,PRIMARY KEY (`id`)
	,UNIQUE INDEX `AK_unique_key`(`name`)
	,INDEX `FK_Reference_4`(`groupId`)
	,CONSTRAINT `FK_Reference_4` FOREIGN KEY (`groupId`) REFERENCES `dal_group`(`id`)
	);

CREATE TABLE IF NOT EXISTS `databasesetentry` (
	`id` INT (11) NOT NULL AUTO_INCREMENT
	,`name` TEXT NOT NULL
	,`databaseType` VARCHAR(50) NOT NULL
	,`sharding` VARCHAR(50) NULL DEFAULT NULL
	,`connectionString` VARCHAR(100) NOT NULL
	,`databaseSet_Id` INT (11) NULL DEFAULT NULL
	,`update_user_no` VARCHAR(45) NULL DEFAULT NULL
	,`update_time` TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
	,PRIMARY KEY (`id`)
	,INDEX `FK_Reference_5`(`databaseSet_Id`)
	,CONSTRAINT `FK_Reference_5` FOREIGN KEY (`databaseSet_Id`) REFERENCES `databaseset`(`id`)
	);

CREATE TABLE IF NOT EXISTS `group_relation` (
	`id` INT (11) NOT NULL AUTO_INCREMENT
	,`current_group_id` INT (11) NULL DEFAULT NULL
	,`child_group_id` INT (11) NULL DEFAULT NULL
	,`child_group_role` INT (11) NULL DEFAULT NULL
	,`adduser` INT (11) NULL DEFAULT NULL
	,`update_user_no` VARCHAR(45) NULL DEFAULT NULL
	,`update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
	,PRIMARY KEY (`id`)
	,UNIQUE INDEX `uq_Index_1`(`current_group_id`, `child_group_id`)
	);

CREATE TABLE IF NOT EXISTS `login_users` (
	`id` INT (11) NOT NULL AUTO_INCREMENT
	,`user_no` VARCHAR(45) NULL DEFAULT NULL
	,`user_name` VARCHAR(45) NULL DEFAULT NULL
	,`user_email` VARCHAR(45) NULL DEFAULT NULL
	,PRIMARY KEY (`id`)
	,UNIQUE INDEX `user_no_UNIQUE`(`user_no`)
	);

CREATE TABLE IF NOT EXISTS `project` (
	`id` INT (11) NOT NULL AUTO_INCREMENT
	,`name` VARCHAR(45) NULL DEFAULT NULL
	,`namespace` VARCHAR(45) NULL DEFAULT NULL
	,`dal_group_id` INT (11) NULL DEFAULT NULL
	,`dal_config_name` VARCHAR(100) NULL DEFAULT NULL
	,`update_user_no` VARCHAR(45) NULL DEFAULT NULL
	,`update_time` TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
	,PRIMARY KEY (`id`)
	);

CREATE TABLE IF NOT EXISTS `task_auto` (
	`id` INT (11) NOT NULL AUTO_INCREMENT
	,`project_id` INT (11) NULL DEFAULT NULL
	,`db_name` VARCHAR(45) NULL DEFAULT NULL
	,`table_name` VARCHAR(45) NULL DEFAULT NULL
	,`class_name` VARCHAR(45) NULL DEFAULT NULL
	,`method_name` VARCHAR(45) NULL DEFAULT NULL
	,`sql_style` VARCHAR(45) NULL DEFAULT NULL
	,`crud_type` VARCHAR(45) NULL DEFAULT NULL
	,`fields` TEXT NULL
	,`where_condition` TEXT NULL
	,`sql_content` TEXT NULL
	,`generated` TINYINT (1) NULL DEFAULT NULL
	,`version` INT (11) NULL DEFAULT NULL
	,`update_user_no` VARCHAR(45) NULL DEFAULT NULL
	,`update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
	,`comment` TEXT NULL
	,`scalarType` VARCHAR(45) NULL DEFAULT NULL
	,`pagination` TINYINT (1) NULL DEFAULT NULL
	,`orderby` VARCHAR(45) NULL DEFAULT NULL
	,`approved` INT (11) NULL DEFAULT NULL
	,`approveMsg` LONGTEXT NULL
	,PRIMARY KEY (`id`)
	);

CREATE TABLE IF NOT EXISTS `task_sql` (
	`id` INT (11) NOT NULL AUTO_INCREMENT
	,`db_name` VARCHAR(45) NULL DEFAULT NULL
	,`class_name` VARCHAR(45) NULL DEFAULT NULL
	,`pojo_name` VARCHAR(45) NULL DEFAULT NULL
	,`method_name` VARCHAR(45) NULL DEFAULT NULL
	,`crud_type` VARCHAR(45) NULL DEFAULT NULL
	,`sql_content` TEXT NULL
	,`project_id` INT (11) NULL DEFAULT NULL
	,`parameters` TEXT NULL
	,`generated` TINYINT (1) NULL DEFAULT NULL
	,`version` INT (11) NULL DEFAULT NULL
	,`update_user_no` VARCHAR(45) NULL DEFAULT NULL
	,`update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
	,`comment` TEXT NULL
	,`scalarType` VARCHAR(45) NULL DEFAULT NULL
	,`pojoType` VARCHAR(15) NULL DEFAULT NULL
	,`pagination` TINYINT (1) NULL DEFAULT NULL
	,`sql_style` VARCHAR(45) NULL DEFAULT NULL
	,`approved` INT (11) NULL DEFAULT NULL
	,`approveMsg` LONGTEXT NULL
	,PRIMARY KEY (`id`)
	);

CREATE TABLE IF NOT EXISTS `task_table` (
	`id` INT (11) NOT NULL AUTO_INCREMENT
	,`project_id` INT (11) NULL DEFAULT NULL
	,`db_name` VARCHAR(45) NULL DEFAULT NULL
	,`table_names` LONGTEXT NULL
	,`view_names` LONGTEXT NULL
	,`sp_names` LONGTEXT NULL
	,`prefix` VARCHAR(45) NULL DEFAULT NULL
	,`suffix` VARCHAR(45) NULL DEFAULT NULL
	,`cud_by_sp` TINYINT (1) NULL DEFAULT NULL
	,`pagination` TINYINT (1) NULL DEFAULT NULL
	,`generated` TINYINT (1) NULL DEFAULT NULL
	,`version` INT (11) NULL DEFAULT NULL
	,`update_user_no` VARCHAR(45) NULL DEFAULT NULL
	,`update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
	,`comment` TEXT NULL
	,`sql_style` VARCHAR(45) NULL DEFAULT NULL
	,`api_list` TEXT NULL
	,`approved` INT (11) NULL DEFAULT NULL
	,`approveMsg` LONGTEXT NULL
	,PRIMARY KEY (`id`)
	);

CREATE TABLE IF NOT EXISTS `user_group` (
	`id` INT (11) NOT NULL AUTO_INCREMENT
	,`user_id` INT (11) NULL DEFAULT NULL
	,`group_id` INT (11) NULL DEFAULT NULL
	,`role` INT (11) NULL DEFAULT '1'
	,`adduser` INT (11) NULL DEFAULT '1'
	,PRIMARY KEY (`id`)
	);

CREATE TABLE IF NOT EXISTS `user_project` (
	`id` INT (11) NOT NULL AUTO_INCREMENT
	,`project_id` INT (11) NULL DEFAULT NULL
	,`user_no` VARCHAR(45) NULL DEFAULT NULL
	,PRIMARY KEY (`id`)
	);

CREATE TABLE IF NOT EXISTS `config_template` (
	`id` INT (2) NOT NULL AUTO_INCREMENT
	,`config_type` INT (2) NULL DEFAULT NULL
	,`lang_type` INT (2) NULL DEFAULT NULL
	,`template` VARCHAR(2048) NULL DEFAULT NULL
	,PRIMARY KEY (`ID`)
	,INDEX `config_type`(`config_type`)
	,INDEX `lang_type`(`lang_type`)
	);

INSERT INTO `config_template` (
	`config_type`
	,`lang_type`
	,`template`
	)
VALUES (
	1
	,1
	,''
	);

INSERT INTO `config_template` (
	`config_type`
	,`lang_type`
	,`template`
	)
VALUES (
	2
	,1
	,''
	);

INSERT INTO `config_template` (
	`config_type`
	,`lang_type`
	,`template`
	)
VALUES (
	3
	,1
	,''
	);

INSERT INTO `config_template` (
	`config_type`
	,`lang_type`
	,`template`
	)
VALUES (
	1
	,2
	,''
	);

INSERT INTO `config_template` (
	`config_type`
	,`lang_type`
	,`template`
	)
VALUES (
	2
	,2
	,''
	);

INSERT INTO `config_template` (
	`config_type`
	,`lang_type`
	,`template`
	)
VALUES (
	3
	,2
	,''
	);