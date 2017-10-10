DROP TABLE IF EXISTS `alldbs`;
DROP TABLE IF EXISTS `api_list`;
DROP TABLE IF EXISTS `approve_task`;
DROP TABLE IF EXISTS `databasesetentry`;
DROP TABLE IF EXISTS `databaseset`;
DROP TABLE IF EXISTS `group_relation`;
DROP TABLE IF EXISTS `dal_group`;
DROP TABLE IF EXISTS `login_users`;
DROP TABLE IF EXISTS `project`;
DROP TABLE IF EXISTS `task_auto`;
DROP TABLE IF EXISTS `task_sql`;
DROP TABLE IF EXISTS `task_table`;
DROP TABLE IF EXISTS `user_group`;
DROP TABLE IF EXISTS `user_project`;
DROP TABLE IF EXISTS `config_template`;

CREATE TABLE `alldbs` (
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

CREATE TABLE `api_list` (
	`id` INT (11) NOT NULL AUTO_INCREMENT
	,`language` VARCHAR(45) NULL DEFAULT NULL
	,`db_type` VARCHAR(45) NULL DEFAULT NULL
	,`crud_type` VARCHAR(45) NULL DEFAULT NULL
	,`method_declaration` VARCHAR(200) NULL DEFAULT NULL
	,`method_description` TEXT NULL
	,`sp_type` VARCHAR(45) NULL DEFAULT NULL
	,PRIMARY KEY (`id`)
	);

CREATE TABLE `approve_task` (
	`id` INT (11) NOT NULL AUTO_INCREMENT
	,`task_id` INT (11) NULL DEFAULT NULL
	,`task_type` VARCHAR(45) NULL DEFAULT NULL
	,`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
	,`create_user_id` INT (11) NULL DEFAULT NULL
	,`approve_user_id` INT (11) NULL DEFAULT NULL
	,PRIMARY KEY (`id`)
	);

CREATE TABLE `dal_group` (
	`id` INT (11) NOT NULL AUTO_INCREMENT
	,`group_name` VARCHAR(100) NULL DEFAULT NULL
	,`group_comment` TEXT NULL
	,`create_user_no` VARCHAR(45) NULL DEFAULT NULL
	,`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
	,PRIMARY KEY (`id`)
	);

CREATE TABLE `databaseset` (
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

CREATE TABLE `databasesetentry` (
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

CREATE TABLE `group_relation` (
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

CREATE TABLE `login_users` (
	`id` INT (11) NOT NULL AUTO_INCREMENT
	,`user_no` VARCHAR(45) NULL DEFAULT NULL
	,`user_name` VARCHAR(45) NULL DEFAULT NULL
	,`user_email` VARCHAR(45) NULL DEFAULT NULL
	,`password` VARCHAR(128) NULL DEFAULT NULL
	,PRIMARY KEY (`id`)
	,UNIQUE INDEX `user_no_UNIQUE`(`user_no`)
	);

CREATE TABLE `project` (
	`id` INT (11) NOT NULL AUTO_INCREMENT
	,`name` VARCHAR(45) NULL DEFAULT NULL
	,`namespace` VARCHAR(100) NULL DEFAULT NULL
	,`dal_group_id` INT (11) NULL DEFAULT NULL
	,`dal_config_name` VARCHAR(100) NULL DEFAULT NULL
	,`update_user_no` VARCHAR(45) NULL DEFAULT NULL
	,`update_time` TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
	,PRIMARY KEY (`id`)
	)
	AUTO_INCREMENT=10000;

CREATE TABLE `task_auto` (
	`id` INT (11) NOT NULL AUTO_INCREMENT
	,`project_id` INT (11) NULL DEFAULT NULL
	,`db_name` VARCHAR(45) NULL DEFAULT NULL
	,`table_name` VARCHAR(45) NULL DEFAULT NULL
	,`class_name` VARCHAR(45) NULL DEFAULT NULL
	,`method_name` VARCHAR(100) NULL DEFAULT NULL
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
	,`hints` VARCHAR(200) NULL DEFAULT NULL
  ,`length` TINYINT(1) NOT NULL DEFAULT '0'
	,PRIMARY KEY (`id`)
	);

CREATE TABLE `task_sql` (
	`id` INT (11) NOT NULL AUTO_INCREMENT
	,`db_name` VARCHAR(45) NULL DEFAULT NULL
	,`class_name` VARCHAR(45) NULL DEFAULT NULL
	,`pojo_name` VARCHAR(45) NULL DEFAULT NULL
	,`method_name` VARCHAR(100) NULL DEFAULT NULL
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
	,`hints` VARCHAR(200) NULL DEFAULT NULL
  ,`length` TINYINT(1) NOT NULL DEFAULT '0'
	,PRIMARY KEY (`id`)
	);

CREATE TABLE `task_table` (
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
	,`length` TINYINT(1) NOT NULL DEFAULT '0'
	,PRIMARY KEY (`id`)
	);

CREATE TABLE `user_group` (
	`id` INT (11) NOT NULL AUTO_INCREMENT
	,`user_id` INT (11) NULL DEFAULT NULL
	,`group_id` INT (11) NULL DEFAULT NULL
	,`role` INT (11) NULL DEFAULT '1'
	,`adduser` INT (11) NULL DEFAULT '1'
	,PRIMARY KEY (`id`)
	);

CREATE TABLE `user_project` (
	`id` INT (11) NOT NULL AUTO_INCREMENT
	,`project_id` INT (11) NULL DEFAULT NULL
	,`user_no` VARCHAR(45) NULL DEFAULT NULL
	,PRIMARY KEY (`id`)
	);

CREATE TABLE `config_template` (
	`id` INT (2) NOT NULL AUTO_INCREMENT
	,`config_type` INT (2) NULL DEFAULT NULL
	,`lang_type` INT (2) NULL DEFAULT NULL
	,`template` VARCHAR(2048) NULL DEFAULT NULL
	,PRIMARY KEY (`ID`)
	,INDEX `config_type`(`config_type`)
	,INDEX `lang_type`(`lang_type`)
	);

INSERT INTO `config_template` (`config_type`,`lang_type`,`template`) VALUES
	(1,1,''),(2,1,''),(3,1,''),(1,2,''),(2,2,''),(3,2,'');

INSERT INTO `api_list` (`id`, `language`, `db_type`, `crud_type`, `method_declaration`, `method_description`, `sp_type`) VALUES
	(1, 'java', 'MySQL', 'select', 'T queryByPk(Number id, DalHints hints)', 'Query according to the primary keys', 'not sp'),
	(2, 'java', 'MySQL', 'select', 'T queryByPk(Key1, key2, key3... DalHints hints)', 'Query according to the multiple primary keys', 'not sp'),
	(3, 'java', 'MySQL', 'select', 'T queryByPk(T pk, DalHints hints)', 'Query according to the Entity which contains primary keys', 'not sp'),
	(4, 'java', 'MySQL', 'select', 'int count(DalHints hints)', 'Get the count of records', 'not sp'),
	(5, 'java', 'MySQL', 'select', 'List<T> queryByPage(int pageSize, int pageNo, DalHints hints)', 'Query by pagination on the whole table', 'not sp'),
	(6, 'java', 'MySQL', 'select', 'List<T> getAll(DalHints hints)', 'Get the records of the table', 'not sp'),
	(7, 'java', 'MySQL', 'insert', 'int insert(DalHints hints, T daoPojos)', 'Insert pojo into table', 'not sp'),
	(9, 'java', 'MySQL', 'insert', 'int insert(DalHints hints, KeyHolder keyHolder, T daoPojo)', 'Insert pojo and get the generated PK back in keyHolder', 'not sp'),
	(10, 'java', 'MySQL', 'delete', 'int delete(DalHints hints, T daoPojos)', 'Delete POJOs from table according to primary key', 'not sp'),
	(12, 'java', 'MySQL', 'update', 'int update(DalHints hints, T daoPojo)', 'Update the given pojo', 'not sp'),
	(13, 'java', 'SQLServer', 'select', 'T queryByPk(Number id, DalHints hints)', 'Query according to the primary keys', 'spa sp3 notSp'),
	(14, 'java', 'SQLServer', 'select', 'T queryByPk(Key1, key2, key3... DalHints hints)', 'Query according to the multiple primary keys', 'spa sp3 notSp'),
	(15, 'java', 'SQLServer', 'select', 'T queryByPk(T pk, DalHints hints)', 'Query according to the Entity which contains primary keys', 'spa sp3 notSp'),
	(16, 'java', 'SQLServer', 'select', 'int count(DalHints hints)', 'Get the count of records', 'spa sp3 notSp'),
	(17, 'java', 'SQLServer', 'select', 'List<T> queryByPage(int pageSize, int pageNo, DalHints hints)', 'Query by pagination on the whole table', 'spa sp3 notSp'),
	(18, 'java', 'SQLServer', 'select', 'List<T> getAll(DalHints hints)', 'Ge the records of the table', 'spa notSp'),
	(19, 'java', 'SQLServer', 'insert', 'int insert(DalHints hints, T daoPojo)', 'Insert one POJO into table using insert SPA', 'spa'),
	(31, 'java', 'SQLServer', 'delete', 'int delete(DalHints hints, T daoPojo)', 'Delete POJO from table according to primary key', 'not sp'),
	(33, 'java', 'SQLServer', 'update', 'int update(DalHints hints, T daoPojo)', 'Update the given pojo', 'not sp'),
	(42, 'csharp', 'MySQL', 'select', 'T FindByPk(int id )', '根据主键获取T信息', 'not sp'),
	(43, 'csharp', 'MySQL', 'select', 'IList<T> GetAll()', '获取所有T信息', 'not sp'),
	(44, 'csharp', 'MySQL', 'select', 'long Count()', '取得总记录数', 'not sp'),
	(45, 'csharp', 'MySQL', 'select', 'IList<T> GetListByPage(T obj, int pagesize, int pageNo)', '检索T，带翻页', 'not sp'),
	(46, 'csharp', 'MySQL', 'insert', 'int InsertT(T t)', '插入T', 'not sp'),
	(47, 'csharp', 'MySQL', 'update', 'int UpdateT(T t)', '修改T', 'not sp'),
	(48, 'csharp', 'MySQL', 'delete', 'int DeleteT(T t)', '删除T', 'not sp'),
	(49, 'csharp', 'SQLServer', 'select', 'T FindByPk(int id)', '根据主键获取T信息', 'not sp'),
	(50, 'csharp', 'SQLServer', 'select', 'IList<T> GetAll()', '获取所有T信息', 'not sp'),
	(51, 'csharp', 'SQLServer', 'select', 'long Count()', '取得总记录数', 'not sp'),
	(52, 'csharp', 'SQLServer', 'select', 'IList<T> GetListByPage(T obj, int pagesize, int pageNo)', '检索T，带翻页', 'not sp'),
	(53, 'csharp', 'SQLServer', 'insert', 'int InsertT(T t)', '插入T', 'not sp'),
	(54, 'csharp', 'SQLServer', 'insert', 'int BulkInsertT(IList<T> tList)', '批量插入T', 'not sp'),
	(55, 'csharp', 'SQLServer', 'update', 'int UpdateT(T t)', '修改T', 'not sp'),
	(56, 'csharp', 'SQLServer', 'update', 'int BulkUpdateT(IList<T> tList)', '批量修改T', 'not sp'),
	(57, 'csharp', 'SQLServer', 'delete', 'int DeleteT(T t)', '删除T', 'not sp'),
	(58, 'csharp', 'SQLServer', 'delete', 'int DeleteT(int id)', '删除T', 'not sp'),
	(59, 'csharp', 'SQLServer', 'delete', 'int BulkDelete(IList<T> tList)', '批量删除T', 'not sp'),
	(73, 'java', 'SQLServer', 'insert', 'int insert(DalHints hints, KeyHolder keyHolder, T daoPojo)', 'Insert one POJO into table  and get the generated PK back in keyHolder', 'spa'),
	(74, 'csharp', 'MySQL', 'insert', 'bool BulkInsertT(IList<T> tList)', '批量插入T', 'not sp'),
	(75, 'java', 'MySQL', 'insert', 'int[] insert(DalHints hints, List<T> daoPojos)', 'Insert pojos one by one', 'not sp'),
	(77, 'java', 'SQLServer', 'insert', 'int[] insert(DalHints hints, List<T> daoPojos)', 'Insert pojos', 'not sp'),
	(78, 'java', 'MySQL', 'insert', 'int[] insert(DalHints hints, KeyHolder keyHolder, List<T> daoPojos)', 'Insert pojos and get the generated PK back in keyHolder', 'not sp'),
	(79, 'java', 'SQLServer', 'insert', 'int[] insert(DalHints hints, KeyHolder keyHolder, List<T> daoPojos)', 'Insert pojos and get the generated PK back in keyHolder', 'not sp'),
	(80, 'java', 'MySQL', 'insert', 'int[] batchInsert(DalHints hints, List<T> daoPojos)', 'Insert pojos in batch mode', 'not sp'),
	(81, 'java', 'SQLServer', 'insert', 'int[] batchInsert(DalHints hints, List<T> daoPojos)', 'Insert pojos in batch mode', 'not sp'),
	(82, 'java', 'MySQL', 'insert', 'int combinedInsert(DalHints hints, List<T> daoPojos)', 'Insert multiple pojos in one INSERT SQL and get the generated PK back in keyHolder', 'not sp'),
	(83, 'java', 'SQLServer', 'insert', 'int combinedInsert(DalHints hints, List<T> daoPojos)', 'Insert multiple pojos in one INSERT SQL and get the generated PK back in keyHolder', 'not sp'),
	(84, 'java', 'MySQL', 'insert', 'int combinedInsert(DalHints hints,KeyHolder keyHolder,List<T> pojos)', 'Insert multiple pojos in one INSERT SQL and get the generated PK back in keyHolder', 'not sp'),
	(85, 'java', 'SQLServer', 'insert', 'int combinedInsert(DalHints hints,KeyHolder keyHolder,List<T> pojos)', 'Insert multiple pojos in one INSERT SQL and get the generated PK back in keyHolder', 'not sp'),
	(86, 'java', 'MySQL', 'delete', 'int[] delete(DalHints hints, List<T> daoPojos)', 'Delete the given pojos list one by one', 'not sp'),
	(87, 'java', 'SQLServer', 'delete', 'int[] delete(DalHints hints, List<T> daoPojos)', 'Delete the given pojos list one by one', 'not sp'),
	(88, 'java', 'MySQL', 'delete', 'int[] batchDelete(DalHints hints, List<T> daoPojos)', 'Delete the given pojo list in batch.', 'not sp'),
	(89, 'java', 'SQLServer', 'delete', 'int[] batchDelete(DalHints hints, List<T> daoPojos)', 'Delete the given pojo list in batch.', 'not sp'),
	(90, 'java', 'MySQL', 'update', 'int[] update(DalHints hints, List<T> daoPojos)', 'Update the given pojos one by one', 'not sp'),
	(91, 'java', 'SQLServer', 'update', 'int[] update(DalHints hints, List<T> daoPojos)', 'Update the given pojos', 'not sp'),
	(96, 'java', 'MySQL', 'update', 'int[] batchUpdate(DalHints hints, List<T> daoPojos)', 'Update the given pojo list in batch', 'not sp'),
	(97, 'java', 'SQLServer', 'update', 'int[] batchUpdate(DalHints hints, List<T> daoPojos)', 'Update the given pojo list in batch', 'not sp');