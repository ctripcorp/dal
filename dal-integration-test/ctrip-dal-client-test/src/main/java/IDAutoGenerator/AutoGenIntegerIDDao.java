package IDAutoGenerator;


import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaMapper;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeSelectSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeUpdateSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.MatchPattern;
import com.ctrip.platform.dal.dao.sqlbuilder.SelectSqlBuilder;

import java.sql.SQLException;
import java.sql.Types;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class AutoGenIntegerIDDao {
	private static final boolean ASC = true;
	private DalTableDao<TableWithIntegerIdentity> client;
	private DalQueryDao queryDao = null;
	private DalRowMapper<TableWithIntegerIdentity> personGenRowMapper = null;

	public AutoGenIntegerIDDao(String DATA_BASE) throws SQLException {
		this.client = new DalTableDao<>(new DalDefaultJpaParser<>(TableWithIntegerIdentity.class, DATA_BASE));
		this.personGenRowMapper = new DalDefaultJpaMapper<>(TableWithIntegerIdentity.class);
		this.queryDao = new DalQueryDao(DATA_BASE);
	}

	public AutoGenIntegerIDDao(String DATA_BASE, String tableName) throws SQLException {
		this.client = new DalTableDao<>(new DalDefaultJpaParser<>(TableWithIntegerIdentity.class, DATA_BASE,tableName));
		this.personGenRowMapper = new DalDefaultJpaMapper<>(TableWithIntegerIdentity.class);
		this.queryDao = new DalQueryDao(DATA_BASE);
	}

//	/**
//	 * Query PersonGen by the specified ID
//	 * The ID must be a number
//	**/
//	public TableWithIntegerIdentity queryByPk(Number id, DalHints hints)
//			throws SQLException {
//		hints = DalHints.createIfAbsent(hints);
//		return client.queryByPk(id, hints);
//	}

	public List<TableWithIntegerIdentity> queryTop(Integer age, Integer count, DalHints hints) throws SQLException{
		hints= DalHints.createIfAbsent(hints);
		StatementParameters statementParameters=new StatementParameters();
		statementParameters.set(1,"age",Types.INTEGER,age);
		return client.queryTop("age=?",statementParameters,hints,count);
	}

	public List<TableWithIntegerIdentity> queryFromWithOrderBy(Integer age, Integer start, Integer count, DalHints hints) throws SQLException{
		hints= DalHints.createIfAbsent(hints);
		StatementParameters statementParameters=new StatementParameters();
		statementParameters.set(1,"age",Types.INTEGER,age);
		return client.queryFrom("age=? order by id",statementParameters,hints,start,count);
	}

	public List<TableWithIntegerIdentity> queryFromWithoutOrderBy(Integer age, Integer start, Integer count, DalHints hints) throws SQLException{
		hints= DalHints.createIfAbsent(hints);
		StatementParameters statementParameters=new StatementParameters();
		statementParameters.set(1,"age",Types.INTEGER,age);
		return client.queryFrom("age=?",statementParameters,hints,start,count);
	}

	/**
	 * Query Person by complex primary key
	**/
	public TableWithIntegerIdentity queryByPk(Integer iD, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		TableWithIntegerIdentity pk = new TableWithIntegerIdentity();
		pk.setID(iD);
		return client.queryByPk(pk, hints);
	}

	/**
	 * Query PersonGen by PersonGen instance which the primary key is set
	**/
	public TableWithIntegerIdentity queryByPk(TableWithIntegerIdentity pk, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(pk, hints);
	}

	/**
	 * Query against sample pojo. All not null attributes of the passed in pojo
	 * will be used as search criteria.
	**/
	public List<TableWithIntegerIdentity> queryLike(TableWithIntegerIdentity sample, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryLike(sample, hints);
	}

	public List<TableWithIntegerIdentity> queryBy(TableWithIntegerIdentity sample, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryBy(sample, hints);
	}

	/**
	 * Get the all records count
	 */
	public int count(DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder().selectCount();
		return client.count(builder, hints).intValue();
	}
	
	public int countWhereCondition(String RightsName, DalHints hints) throws SQLException {
		String whereCondition ="1=1 and Age in (select Age from person where Name like ?)";
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.set(i++, Types.VARCHAR, "%" + RightsName + "%");  
		return client.count(whereCondition, parameters, hints).intValue();
	}

	/**
	 * Query Person with paging function
	 * The pageSize and pageNo must be greater than zero.
	 */
	public List<TableWithIntegerIdentity> queryAllByPage(int pageNo, int pageSize, DalHints hints)  throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.selectAll().atPage(pageNo, pageSize).orderBy("ID,Name,Age", ASC);

		return client.query(builder, hints);
	}
	
	/**
	 * Get all records from table
	 */
	public List<TableWithIntegerIdentity> queryAll(DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		
		SelectSqlBuilder builder = new SelectSqlBuilder().selectAll().orderBy("ID,Name,Age", ASC);
		
		return client.query(builder, hints);
	}
	
//	/**
//	 * Query PersonGen with paging function
//	 * The pageSize and pageNo must be greater than zero.
//	 */
//	public List<TableWithIntegerIdentity> queryAllByPage(int pageNo, int pageSize, DalHints hints)  throws SQLException {
//		hints = DalHints.createIfAbsent(hints);
//
//		SelectSqlBuilder builder = new SelectSqlBuilder();
//		builder.selectAll().atPage(pageNo, pageSize).orderBy("ID", ASC);
//
//		return client.query(builder, hints);
//	}
//	
//	/**
//	 * Get all records from table
//	 */
//	public List<TableWithIntegerIdentity> queryAll(DalHints hints) throws SQLException {
//		hints = DalHints.createIfAbsent(hints);
//		
//		SelectSqlBuilder builder = new SelectSqlBuilder().selectAll().orderBy("ID", ASC);
//		
//		return client.query(builder, hints);
//	}

	/**
	 * Insert pojo and get the generated PK back in keyHolder. 
	 * If the "set no count on" for MS SqlServer is set(currently set in Ctrip), the operation may fail.
	 * Please don't pass keyholder for MS SqlServer to avoid the failure.
	 * 
	 * @param hints
	 *            Additional parameters that instruct how DAL Client perform database operation.
	 * @param daoPojo
	 *            pojo to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int insert(DalHints hints, TableWithIntegerIdentity daoPojo) throws SQLException {
		if(null == daoPojo)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.insert(hints, daoPojo);
	}

	/**
	 * Insert pojos one by one. If you want to inert them in the batch mode,
	 * user batchInsert instead. You can also use the combinedInsert.
	 * 
	 * @param hints 
	 *            Additional parameters that instruct how DAL Client perform database operation.
	 *            DalHintEnum.continueOnError can be used
	 *            to indicate that the inserting can be go on if there is any
	 *            failure.
	 * @param daoPojos
	 *            list of pojos to be inserted
	 * @return how many rows been affected
	 */
	public int[] insert(DalHints hints, List<TableWithIntegerIdentity> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.insert(hints, daoPojos);
	}

	/**
	 * Insert pojo and get the generated PK back in keyHolder. 
	 * If the "set no count on" for MS SqlServer is set(currently set in Ctrip), the operation may fail.
	 * Please don't pass keyholder for MS SqlServer to avoid the failure.
	 * 
	 * @param hints
	 *            Additional parameters that instruct how DAL Client perform database operation.
	 * @param keyHolder
	 *            holder for generated primary keys
	 * @param daoPojo
	 *            pojo to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int insert(DalHints hints, KeyHolder keyHolder, TableWithIntegerIdentity daoPojo) throws SQLException {
		if(null == daoPojo)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.insert(hints, keyHolder, daoPojo);
	}

	/**
	 * Insert pojos and get the generated PK back in keyHolder. 
	 * If the "set no count on" for MS SqlServer is set(currently set in Ctrip), the operation may fail.
	 * Please don't pass keyholder for MS SqlServer to avoid the failure.
	 * 
	 * @param hints
	 *            Additional parameters that instruct how DAL Client perform database operation.
	 *            DalHintEnum.continueOnError can be used
	 *            to indicate that the inserting can be go on if there is any
	 *            failure.
	 * @param keyHolder
	 *            holder for generated primary keys
	 * @param daoPojos
	 *            list of pojos to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int[] insert(DalHints hints, KeyHolder keyHolder, List<TableWithIntegerIdentity> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.insert(hints, keyHolder, daoPojos);
	}

	/**
	 * Insert pojos in batch mode. 
	 * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the dao.noshardtest.shardtest.
	 * 
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param daoPojos list of pojos to be inserted
	 * @return how many rows been affected for inserting each of the pojo
	 * @throws SQLException
	 */
	public int[] batchInsert(DalHints hints, List<TableWithIntegerIdentity> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.batchInsert(hints, daoPojos);
	}

	/**
	 * Insert multiple pojos in one INSERT SQL and get the generated PK back in keyHolder.
	 * If the "set no count on" for MS SqlServer is set(currently set in Ctrip), the operation may fail.
	 * Please don't pass keyholder for MS SqlServer to avoid the failure.
	 * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the dao.noshardtest.shardtest.
	 * 
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param daoPojos list of pojos to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int combinedInsert(DalHints hints, List<TableWithIntegerIdentity> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.combinedInsert(hints, daoPojos);
	}

	/**
	 * Insert multiple pojos in one INSERT SQL and get the generated PK back in keyHolder.
	 * If the "set no count on" for MS SqlServer is set(currently set in Ctrip), the operation may fail.
	 * Please don't pass keyholder for MS SqlServer to avoid the failure.
	 * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the dao.noshardtest.shardtest.
	 * 
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param keyHolder holder for generated primary keys
	 * @param daoPojos list of pojos to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int combinedInsert(DalHints hints, KeyHolder keyHolder, List<TableWithIntegerIdentity> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.combinedInsert(hints, keyHolder, daoPojos);
	}

	/**
	 * Delete the given pojo.
	 * 
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param daoPojo pojo to be deleted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int delete(DalHints hints, TableWithIntegerIdentity daoPojo) throws SQLException {
		if(null == daoPojo)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.delete(hints, daoPojo);
	}

	/**
	 * Delete the given pojos list one by one.
	 * 
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param daoPojos list of pojos to be deleted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int[] delete(DalHints hints, List<TableWithIntegerIdentity> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.delete(hints, daoPojos);
	}

	/**
	 * Delete the given pojo list in batch. 
	 * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the dao.noshardtest.shardtest.
	 * 
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param daoPojos list of pojos to be deleted
	 * @return how many rows been affected for deleting each of the pojo
	 * @throws SQLException
	 */
	public int[] batchDelete(DalHints hints, List<TableWithIntegerIdentity> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.batchDelete(hints, daoPojos);
	}

	/**
	 * Update the given pojo . By default, if a field of pojo is null value,
	 * that field will be ignored, so that it will not be updated. You can
	 * overwrite this by set updateNullField in hints.
	 * 
	 * @param hints
	 * 			Additional parameters that instruct how DAL Client perform database operation.
	 *          DalHintEnum.updateNullField can be used
	 *          to indicate that the field of pojo is null value will be update.
	 * @param daoPojo pojo to be updated
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int update(DalHints hints, TableWithIntegerIdentity daoPojo) throws SQLException {
		if(null == daoPojo)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.update(hints, daoPojo);
	}

	/**
	 * Update the given pojo list one by one. By default, if a field of pojo is null value,
	 * that field will be ignored, so that it will not be updated. You can
	 * overwrite this by set updateNullField in hints.
	 * 
	 * @param hints
	 * 			Additional parameters that instruct how DAL Client perform database operation.
	 *          DalHintEnum.updateNullField can be used
	 *          to indicate that the field of pojo is null value will be update.
	 * @param daoPojos list of pojos to be updated
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int[] update(DalHints hints, List<TableWithIntegerIdentity> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.update(hints, daoPojos);
	}

	/**
	 * Update the given pojo list in batch. 
	 * 
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int[] batchUpdate(DalHints hints, List<TableWithIntegerIdentity> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.batchUpdate(hints, daoPojos);
	}
	
	/**
	 * 构建，查询
	**/
	public List<String> test_build_query_fieldList_multipleOrderBy(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Name");
		builder.in("Age", Age, Types.INTEGER, false);
        builder.orderBy("Age", false);
        builder.orderBy("ID", true);
		return client.query(builder, hints, String.class);
	}
	
	/**
	 * 构建，查询
	**/
	public List<String> test_build_query_fieldList_multipleOrderByReverse(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Name");
		builder.in("Age", Age, Types.INTEGER, false);
		builder.orderBy("ID", true);
        builder.orderBy("Age", false);       
		return client.query(builder, hints, String.class);
	}

	/**
	 * 构建，查询
	**/
	public List<String> test_build_query_fieldListByPage_multipleOrderBy(List<Integer> Age, int pageNo, int pageSize, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Name");
		builder.in("Age", Age, Types.INTEGER, false);
		 builder.orderBy("Age", false);
	        builder.orderBy("ID", true);
		builder.atPage(pageNo, pageSize);

		return client.query(builder, hints, String.class);
	}
	
	/**
	 * 构建，查询
	**/
	public List<String> test_build_query_fieldListByPage_multipleOrderByReverse(List<Integer> Age, int pageNo, int pageSize, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Name");
		builder.in("Age", Age, Types.INTEGER, false);
	    builder.orderBy("ID", true);
		 builder.orderBy("Age", false);    
		builder.atPage(pageNo, pageSize);

		return client.query(builder, hints, String.class);
	}

	/**
	 * 构建，查询
	**/
	public String test_build_query_field_single(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Name");
		builder.in("Age", Age, Types.INTEGER, false);
		builder.requireSingle();

		return client.queryObject(builder, hints, String.class);
	}

	/**
	 * 构建，查询
	**/
	public String test_build_query_field_first_multipleOrderBy(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Name");
		builder.in("Age", Age, Types.INTEGER, false);
		 builder.orderBy("Age", false);
	        builder.orderBy("ID", true);
		builder.requireFirst();

		return client.queryObject(builder, hints, String.class);
	}
	
	/**
	 * 构建，查询
	**/
	public String test_build_query_field_first_multipleOrderByReverse(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Name");
		builder.in("Age", Age, Types.INTEGER, false);
		 builder.orderBy("ID", true);
		 builder.orderBy("Age", false);
	       
		builder.requireFirst();

		return client.queryObject(builder, hints, String.class);
	}
	
	/**
	 * 构建，查询
	**/
	public List<TableWithIntegerIdentity> test_ClientQueryFrom_list(List<Integer> Age, DalHints hints, int start, int count) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);

		return client.queryFrom("Age in (?)", parameters, hints, start, count);
	}

	public List<TableWithIntegerIdentity> testTableBuilderParameterIndex(String Name, List<Integer> Age, Integer ID, DalHints hints, int start, int count) throws Exception{
		hints = DalHints.createIfAbsent(hints);

		StatementParameters parameters = new StatementParameters();
		int i = 2;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		parameters.set(1,Types.VARCHAR,Name);
		parameters.set(i++,Types.INTEGER,ID);

		return client.queryFrom("Name=? and Age in (?) and ID=?", parameters, hints, start, count);
	}

	public List<TableWithIntegerIdentity> testTableBuilderParameterIndexNoIn(String Name, Integer Age, Integer ID, DalHints hints, int start, int count) throws Exception{
		hints = DalHints.createIfAbsent(hints);

		StatementParameters parameters = new StatementParameters();

		parameters.set(2, "Age", Types.INTEGER, Age);
		parameters.set(1,Types.VARCHAR,Name);
		parameters.set(3,Types.INTEGER,ID);

		return client.queryFrom("Name=? and Age = ? and ID=?", parameters, hints, start, count);
	}



	/**
	 * 构建，查询
	**/
	public List<TableWithIntegerIdentity> test_ClientQueryFromPartialFieldsSet_list(List<Integer> Age, DalHints hints, int start, int count) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);

		Set<String> columns = new HashSet<>();
		columns.add("Age");
		columns.add("Name");
		
		return client.queryFrom("Age in ?", parameters, hints.partialQuery(columns), start, count);
	}
	
	/**
	 * 构建，查询
	**/
	public List<TableWithIntegerIdentity> test_ClientQueryFromPartialFieldsStrings_list(List<Integer> Age, DalHints hints, int start, int count) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);

//		Set<String> columns = new HashSet<>();
//		columns.add("Age");
//		columns.add("Name");
		
		return client.queryFrom("Age in ?", parameters, hints.partialQuery("Age","Name"), start, count);
	}

	/**
	 * 构建，查询
	**/
	public List<TableWithIntegerIdentity> test_build_query_list_multipleOrderBy(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Birth","Name","Age","ID");
//		builder.selectAll();
//		builder.equalNullable("1", 1, Types.INTEGER, false);
//		builder.and();
		builder.inNullable("Age", Age, Types.INTEGER, false);
		 builder.orderBy("Age", false);
	        builder.orderBy("ID", true);
//		builder.and();
		return client.query(builder, hints);
		
		
	}
	
	/**
	 * 构建，查询
	**/
	public List<TableWithIntegerIdentity> test_build_query_list_multipleOrderByReverse(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Birth","Name","Age","ID");
//		builder.selectAll();
//		builder.equalNullable("1", 1, Types.INTEGER, false);
//		builder.and();
		builder.inNullable("Age", Age, Types.INTEGER, false);
		builder.orderBy("ID", true);
		 builder.orderBy("Age", false);
	        
//		builder.and();
		return client.query(builder, hints);
	}
	
	/**
	 * 构建，查询部分字段
	**/
	public List<TableWithIntegerIdentity> test_build_queryPartial_list_multipleOrderBy(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Name","ID");
//		builder.selectAll();
		builder.inNullable("Age", Age, Types.INTEGER, false);
		 builder.orderBy("Age", false);
	        builder.orderBy("ID", true);
		return client.query(builder, hints);
	}
	
	/**
	 * 构建，查询部分字段
	**/
	public List<TableWithIntegerIdentity> test_build_queryPartial_list_multipleOrderByReverse(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Name","ID");
//		builder.selectAll();
		builder.inNullable("Age", Age, Types.INTEGER, false);
		builder.orderBy("ID", true);
		 builder.orderBy("Age", false);
	        
		return client.query(builder, hints);
	}

	/**
	 * 构建，查询
	**/
	public List<TableWithIntegerIdentity> test_build_query_listByPage_multipleOrderBy(List<Integer> Age, int pageNo, int pageSize, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Birth","Name","Age","ID");
		builder.inNullable("Age", Age, Types.INTEGER, false);
		builder.orderBy("Age", false);
	    builder.orderBy("ID", true);
		builder.atPage(pageNo, pageSize);
		return client.query(builder, hints);
	}
	
	/**
	 * 构建，查询
	**/
	public List<TableWithIntegerIdentity> test_build_query_listByPage_multipleOrderByReverse(List<Integer> Age, int pageNo, int pageSize, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Birth","Name","Age","ID");
		builder.inNullable("Age", Age, Types.INTEGER, false);
		 builder.orderBy("ID", true);
		 builder.orderBy("Age", false);
	       
//		builder.range(start, count)
		builder.atPage(pageNo, pageSize);
		return client.query(builder, hints);
	}

	/**
	 * 构建，查询部分字段
	**/
	public List<TableWithIntegerIdentity> test_build_queryPartial_listByPage_multipleOrderBy(List<Integer> Age, int pageNo, int pageSize, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Name","Age","ID");
		builder.inNullable("Age", Age, Types.INTEGER, false);
		 builder.orderBy("Age", false);
	        builder.orderBy("ID", true);
//		builder.range(start, count)
		builder.atPage(pageNo, pageSize);
		return client.query(builder, hints);
	}
	
	/**
	 * 构建，查询部分字段
	**/
	public List<TableWithIntegerIdentity> test_build_queryPartial_listByPage_multipleOrderByReverse(List<Integer> Age, int pageNo, int pageSize, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Name","Age","ID");
		builder.inNullable("Age", Age, Types.INTEGER, false);
		 builder.orderBy("ID", true);
		 builder.orderBy("Age", false);
	       
//		builder.range(start, count)
		builder.atPage(pageNo, pageSize);
		return client.query(builder, hints);
	}
	
	/**
	 * 构建，查询
	**/
	public TableWithIntegerIdentity test_build_query_single(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Birth","Name","Age","ID");
		builder.in("Age", Age, Types.INTEGER, false);
		builder.requireSingle();

		return client.queryObject(builder, hints);
	}
	
	/**
	 * 构建，查询部分字段
	**/
	public TableWithIntegerIdentity test_build_queryPartial_single(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Name","ID");
		builder.in("Age", Age, Types.INTEGER, false);
		builder.requireSingle();

		return client.queryObject(builder, hints);
	}

	/**
	 * 构建，查询
	**/
	public TableWithIntegerIdentity test_build_query_first_multipleOrderBy(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Birth","Name","Age","ID");
		builder.inNullable("Age", Age, Types.INTEGER, false);
		builder.orderBy("Age", false);
		builder.orderBy("ID",true);
	    builder.requireFirst();
		return client.queryObject(builder, hints);
	}
	
	/**
	 * 构建，查询
	**/
	public TableWithIntegerIdentity test_build_query_first_multipleOrderByReverse(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Birth","Name","Age","ID");
		builder.inNullable("Age", Age, Types.INTEGER, false);
		builder.orderBy("ID",true);
		builder.orderBy("Age", false);
		
	    builder.requireFirst();
		return client.queryObject(builder, hints);
	}
	
	/**
	 * 构建，查询
	**/
	public TableWithIntegerIdentity test_build_queryPartial_first_multipleOrderBy(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Age","ID","Name");
//		builder.and();
		builder.inNullable("Age", Age, Types.INTEGER, false);
		 builder.orderBy("Age", false);
	        builder.orderBy("ID", true);
	    builder.requireFirst();

		return client.queryObject(builder, hints);
	}
	
	/**
	 * 构建，查询
	**/
	public TableWithIntegerIdentity test_build_queryPartial_first_multipleOrderByReverse(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Age","ID","Name");
//		builder.and();
		builder.inNullable("Age", Age, Types.INTEGER, false);
		 builder.orderBy("ID", true);
		 builder.orderBy("Age", false);
	       
	    builder.requireFirst();

		return client.queryObject(builder, hints);
	}

	/**
	 * 构建，查询
	 **/
	public List<TableWithIntegerIdentity> testBuildQueryLikeWithMatchPattern(String Name, MatchPattern pattern, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Birth","Name","Age","ID");
//		builder.like("Name",Name,pattern,Types.VARCHAR,false);
		builder.like("Name",Name,pattern,Types.VARCHAR);
		builder.orderBy("ID", true);

		return client.query(builder, hints);

	}

	/**
	 * 构建，查询
	 **/
	public List<TableWithIntegerIdentity> testBuildQueryLikeNullableWithMatchPattern(String Name, MatchPattern pattern, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Birth","Name","Age","ID");
//		builder.likeNullable("Name",Name,pattern,Types.VARCHAR,false);
		builder.likeNullable("Name",Name,pattern,Types.VARCHAR);
		builder.orderBy("ID", true);

		return client.query(builder, hints);

	}
	/**
	 * 自定义，查询
	**/
	public List<TableWithIntegerIdentity> test_def_query(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

//		FreeSelectSqlBuilder<List<TableWithIntegerIdentity>> builder = new FreeSelectSqlBuilder<>(dbCategory);
//		builder.setTemplate("select * from person where Age in (?)");
		String sql="select * from person where Age in (?)";
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		
//		builder.mapWith(personGenRowMapper);

		return queryDao.query(sql, parameters, hints, TableWithIntegerIdentity.class);
	}
	
	/**
	 * 自定义，查询
	**/
	public List<TableWithIntegerIdentity> test_def_partialQuery(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

//		FreeSelectSqlBuilder<List<TableWithIntegerIdentity>> builder = new FreeSelectSqlBuilder<>(dbCategory);
//		builder.setTemplate("select * from person where Age in (?)");
		String sql="select * from person where Age in (?)";
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		
//		builder.mapWith(personGenRowMapper);

		return queryDao.query(sql, parameters, hints.partialQuery("Name"), TableWithIntegerIdentity.class);
	}
	
	/**
	 * 自定义，查询
	**/
	public TableWithIntegerIdentity test_def_queryForObject(List<Integer> ID, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		String sql="select * from person where ID in (?)";
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "ID", Types.INTEGER, ID);

		return queryDao.queryForObject(sql, parameters, hints, TableWithIntegerIdentity.class);
	}
	
	/**
	 * 自定义，查询
	**/
	public TableWithIntegerIdentity test_def_partialQueryForObject(List<Integer> ID, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		String sql="select * from person where ID in (?)";
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "ID", Types.INTEGER, ID);

		return queryDao.queryForObject(sql, parameters, hints.partialQuery("Name"), TableWithIntegerIdentity.class);
	}
	
	/**
	 * 自定义，查询
	**/
	public TableWithIntegerIdentity test_def_queryForObjectNullable(List<Integer> ID, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		String sql="select * from person where ID in (?)";
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "ID", Types.INTEGER, ID);

		return queryDao.queryForObjectNullable(sql, parameters, hints, TableWithIntegerIdentity.class);
	}
	
	/**
	 * 自定义，查询
	**/
	public TableWithIntegerIdentity test_def_partialQueryForObjectNullable(List<Integer> ID, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		String sql="select * from person where ID in (?)";
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "ID", Types.INTEGER, ID);

		return queryDao.queryForObjectNullable(sql, parameters, hints.partialQuery("Name"), TableWithIntegerIdentity.class);
	}
	
	/**
	 * 自定义，查询
	**/
	public TableWithIntegerIdentity test_def_queryFirst(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		String sql="select * from person where Age in (?) order by ID desc";
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);

		return queryDao.queryFirst(sql, parameters, hints, TableWithIntegerIdentity.class);
	}
	
	/**
	 * 自定义，查询
	**/
	public TableWithIntegerIdentity test_def_partialQueryFirst(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		String sql="select * from person where Age in (?) order by ID desc";
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);

		return queryDao.queryFirst(sql, parameters, hints.partialQuery("Name"), TableWithIntegerIdentity.class);
	}
	
	/**
	 * 自定义，查询
	**/
	public TableWithIntegerIdentity test_def_queryFirstNullable(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		String sql="select * from person where Age in (?) order by ID desc";
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);

		return queryDao.queryFirstNullable(sql, parameters, hints, TableWithIntegerIdentity.class);
	}
	
	/**
	 * 自定义，查询
	**/
	public TableWithIntegerIdentity test_def_partialQueryFirstNullable(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		String sql="select * from person where Age in (?) order by ID desc";
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);

		return queryDao.queryFirstNullable(sql, parameters, hints.partialQuery("Name"), TableWithIntegerIdentity.class);
	}
	
	/**
	 * 自定义，查询
	**/
	public List<TableWithIntegerIdentity> test_def_queryTop(List<Integer> Age, DalHints hints, int count) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		String sql="select * from person where Age in (?) order by ID desc";
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);

		return queryDao.queryTop(sql, parameters, hints, TableWithIntegerIdentity.class,count);
	}
	
	/**
	 * 自定义，查询
	**/
	public List<TableWithIntegerIdentity> test_def_partialQueryTop(List<Integer> Age, DalHints hints, int count) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		String sql="select * from person where Age in (?) order by ID desc";
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);

		return queryDao.queryTop(sql, parameters, hints.partialQuery("Name"), TableWithIntegerIdentity.class,count);
	}
	
	/**
	 * 自定义，查询
	**/
	public List<TableWithIntegerIdentity> test_def_queryFrom(List<Integer> Age, DalHints hints, int start, int count) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		String sql="select * from person where Age in (?) order by ID desc";
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);

		return queryDao.queryFrom(sql, parameters, hints, TableWithIntegerIdentity.class,start,count);
	}
	
	/**
	 * 自定义，查询
	**/
	public List<TableWithIntegerIdentity> test_def_partialQueryFrom(List<Integer> Age, DalHints hints, int start, int count) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		String sql="select * from person where Age in (?) order by ID desc";
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);

		return queryDao.queryFrom(sql, parameters, hints.partialQuery("Name"), TableWithIntegerIdentity.class,start,count);
	}
	
	/**
	 * mysql, dao.noshardtest
	**/
	public int test_def_update (DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder();
		builder.setTemplate("truncate table testtable");
		StatementParameters parameters = new StatementParameters();
		int i = 1;

		return queryDao.update(builder, parameters, hints);
	}


	/**
	 * mysql, dao.noshardtest
	**/
//	public int test_def_update (DalHints hints,String tableShardID) throws SQLException {
//		hints = DalHints.createIfAbsent(hints);
//
//		FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
//		builder.setTemplate("truncate person_"+tableShardID);
//		StatementParameters parameters = new StatementParameters();
//		int i = 1;
//
//		return queryDao.update(builder, parameters, hints);
//	}
	
	/**
	 * ss
	**/
//	public List<TableWithIntegerIdentity> test_def_query(DalHints hints) throws SQLException {
//		hints = DalHints.createIfAbsent(hints);
//
//		FreeSelectSqlBuilder<List<TableWithIntegerIdentity>> builder = new FreeSelectSqlBuilder<>(dbCategory);
//		builder.setTemplate("select * from person");
//		StatementParameters parameters = new StatementParameters();
//		builder.mapWith(personGenRowMapper);
//
//		return queryDao.query(builder, parameters, hints);
//	}

	public List<TableWithIntegerIdentity> testFreeSqlBuilderParameterIndex(String Name, List<Integer> Age, Integer ID, DalHints hints) throws Exception{
		hints = DalHints.createIfAbsent(hints);
		FreeSelectSqlBuilder<List<TableWithIntegerIdentity>> builder = new FreeSelectSqlBuilder<>();
		builder.setTemplate("select * from person where Name=? and Age in (?) and ID=?");
		StatementParameters parameters = new StatementParameters();
		int i = 2;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		parameters.set(1,Types.VARCHAR,Name);
		parameters.set(i++,Types.INTEGER,ID);
		builder.mapWith(personGenRowMapper);
		return queryDao.query(builder, parameters, hints);
	}

	public List<TableWithIntegerIdentity> testFreeSqlBuilderMultipleInParams(String Name, List<Integer> Age, List<Integer> ID, DalHints hints) throws Exception{
		hints = DalHints.createIfAbsent(hints);
		FreeSelectSqlBuilder<List<TableWithIntegerIdentity>> builder = new FreeSelectSqlBuilder<>();
		builder.setTemplate("select * from person where Name=? and Age in (?) and ID in(?)");
		StatementParameters parameters = new StatementParameters();
		int i = 2;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		parameters.set(1,Types.VARCHAR,Name);
		i=parameters.setInParameter(i,Types.INTEGER,ID);
		builder.mapWith(personGenRowMapper);
		return queryDao.query(builder, parameters, hints);
	}

	public List<TableWithIntegerIdentity> testFreeSqlBuilderWithDuplicateParameterIndex(String Name, List<Integer> Age, Integer ID, DalHints hints) throws Exception{
		hints = DalHints.createIfAbsent(hints);
		FreeSelectSqlBuilder<List<TableWithIntegerIdentity>> builder = new FreeSelectSqlBuilder<>();
		builder.setTemplate("select * from person where Name=? and Age in (?) and ID=?");
		StatementParameters parameters = new StatementParameters();
		int i = 2;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		parameters.set(2,Types.VARCHAR,Name);
		parameters.set(i++,Types.INTEGER,ID);
		builder.mapWith(personGenRowMapper);
		return queryDao.query(builder, parameters, hints);
	}

	public List<TableWithIntegerIdentity> testFreeSqlBuilderWithDiscontinuedParameterIndex(String Name, List<Integer> Age, Integer ID, DalHints hints) throws Exception{
		hints = DalHints.createIfAbsent(hints);
		FreeSelectSqlBuilder<List<TableWithIntegerIdentity>> builder = new FreeSelectSqlBuilder<>();
		builder.setTemplate("select * from person where Name=? and Age in (?) and ID=?");
		StatementParameters parameters = new StatementParameters();
		int i = 5;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		parameters.set(2,Types.VARCHAR,Name);
		parameters.set(7,Types.INTEGER,ID);
		builder.mapWith(personGenRowMapper);
		return queryDao.query(builder, parameters, hints);
	}

	public List<TableWithIntegerIdentity> testFreeSqlBuilderParameterIndexNotIn(String Name, Integer Age, Integer ID, DalHints hints) throws Exception{
		hints = DalHints.createIfAbsent(hints);
		FreeSelectSqlBuilder<List<TableWithIntegerIdentity>> builder = new FreeSelectSqlBuilder<>();
		builder.setTemplate("select * from person where Name=? and Age = ? and ID=?");
		StatementParameters parameters = new StatementParameters();

		parameters.set(2,Types.INTEGER, Age);
		parameters.set(1,Types.VARCHAR,Name);
		parameters.set(3,Types.INTEGER,ID);
		builder.mapWith(personGenRowMapper);
		return queryDao.query(builder, parameters, hints);
	}

	public List<TableWithIntegerIdentity> testFreeSqlBuilderWithNoParameter(DalHints hints) throws Exception{
		hints = DalHints.createIfAbsent(hints);
		FreeSelectSqlBuilder<List<TableWithIntegerIdentity>> builder = new FreeSelectSqlBuilder<>();
		builder.setTemplate("select * from person where Name='Initial_Shard_02' and Age = 20 and ID=3");
		StatementParameters parameters = new StatementParameters();
		builder.mapWith(personGenRowMapper);
		return queryDao.query(builder, parameters, hints);
	}

	/**
	 * mysql, dao.noshardtest
	**/
	public int test_def_update_ParameterIndex (String Name, Integer Age, Integer ID, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder();
		builder.setTemplate("delete from person where Name=? and Age = ? and ID=?");
		StatementParameters parameters = new StatementParameters();

		parameters.set(2,Types.INTEGER, Age);
		parameters.set(1,Types.VARCHAR,Name);
		parameters.set(3,Types.INTEGER,ID);

		return queryDao.update(builder, parameters, hints);
	}

	/**
	 * mysql, dao.noshardtest
	 **/
	public int test_def_update_InParameterIndex (String Name, List<Integer> Age, Integer ID, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder();
		builder.setTemplate("delete from person where Name=? and Age in (?) and ID=?");
		StatementParameters parameters = new StatementParameters();
		int i = 2;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		parameters.set(1,Types.VARCHAR,Name);
		parameters.set(i++,Types.INTEGER,ID);

		return queryDao.update(builder, parameters, hints);
	}
	
	/**
	 * mysql, dao.noshardtest
	**/
	public int test_def_update (DalHints hints, String tableShardID) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder();
		builder.setTemplate("truncate table testtable_"+tableShardID);
		StatementParameters parameters = new StatementParameters();
		int i = 1;

		return queryDao.update(builder, parameters, hints);
	}

	/**
	 * 自定义，查询
	 **/
	public List<TableWithIntegerIdentity> test_timeout(int delay, DalHints hints) throws Exception {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<TableWithIntegerIdentity>> builder = new FreeSelectSqlBuilder<>();
		builder.setTemplate("select * from person where sleep(?) = 0 limit 1");
		StatementParameters parameters = new StatementParameters();
		parameters.set(1, Types.INTEGER,delay);
		builder.mapWith(personGenRowMapper);

		return queryDao.query(builder, parameters, hints);
	}

	/**
	 * test alias
	 **/
	public List<TableWithIntegerIdentity> testColumnParameter(String colunmName, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<TableWithIntegerIdentity>> builder = new FreeSelectSqlBuilder<>();
		builder.setTemplate("select * from person order by ?");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.setSensitive(i++, "colunmName", Types.VARCHAR, colunmName);
		builder.mapWith(personGenRowMapper);

		return queryDao.query(builder, parameters, hints);
	}

	/**
	 * test alias
	 **//*
	public List<TestAlias> test(Integer id, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<TestAlias>> builder = new FreeSelectSqlBuilder<>();
		builder.setTemplate("select name as myName, count(*) as num from person where id=?");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.setSensitive(i++, "id", Types.INTEGER, id);
		builder.mapWith(testPojoRowMapper);

		return queryDao.query(builder, parameters, hints);
	}*/

	/**
	 * test free sql insert with keyholder
	 **/
	public int testFreeSqlInsertWithKeyHolder (String name, Integer age) throws SQLException {
		return testFreeSqlInsertWithKeyHolder(name, age, null);
	}

	/**
	 * test free sql insert with keyholder
	 **/
	public int testFreeSqlInsertWithKeyHolder (String name, Integer age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder();
		builder.setTemplate("insert into person (name,age) values (?,?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.setSensitive(i++, "name", Types.VARCHAR, name);
		parameters.setSensitive(i++, "age", Types.INTEGER, age);

		return queryDao.update(builder, parameters, hints);
	}

}