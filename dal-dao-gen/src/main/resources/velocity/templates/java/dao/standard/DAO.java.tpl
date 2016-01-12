package ${host.getPackageName()};

#foreach( $field in ${host.getDaoImports()} )
import ${field};
#end

import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;

public class ${host.getPojoClassName()}Dao {
    private static final String DATA_BASE = "${host.getDbSetName()}";
	private static DatabaseCategory dbCategory = null;
#if($host.getDatabaseCategory().name() == "MySql")
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from ${host.getTableName()}";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM ${host.getTableName()}";
	private static final String PAGE_MYSQL_PATTERN = "SELECT * FROM ${host.getTableName()} LIMIT ?, ?";
#else
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from ${host.getTableName()} WITH (NOLOCK)";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM ${host.getTableName()} WITH (NOLOCK)";
	private static final String PAGE_SQL_PATTERN = "select * from ${host.getTableName()} (nolock) order by ${host.getOverColumns()} desc OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";	
#end
	private DalParser<${host.getPojoClassName()}> parser = null;	
	private DalScalarExtractor extractor = new DalScalarExtractor();
	private DalTableDao<${host.getPojoClassName()}> client;
	private DalQueryDao queryDao = null;
	private DalClient baseClient;
	
#parse("templates/java/dao/standard/method.constructor.tpl")
#parse("templates/java/dao/standard/method.queryByPk.tpl")
#parse("templates/java/dao/standard/method.count.tpl")
#parse("templates/java/dao/standard/method.queryByPage.tpl")
#parse("templates/java/dao/standard/method.getAll.tpl")
#parse("templates/java/dao/standard/method.Insert.notSp.tpl")
#parse("templates/java/dao/standard/method.Delete.notSp.tpl")
#parse("templates/java/dao/standard/method.Update.notSp.tpl")
#parse("templates/java/dao/autosql/DAO.java.tpl")

}