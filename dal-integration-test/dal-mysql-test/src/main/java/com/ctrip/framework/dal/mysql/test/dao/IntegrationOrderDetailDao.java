package com.ctrip.framework.dal.mysql.test.dao;

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import com.ctrip.framework.dal.mysql.test.entity.IntegrationOrderDetail;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.ctrip.platform.dal.dao.sqlbuilder.DeleteSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.SelectSqlBuilder;

public class IntegrationOrderDetailDao {
  private static final boolean ASC = true;
  private DalTableDao<IntegrationOrderDetail> client;

  public IntegrationOrderDetailDao() throws SQLException {
    this.client = new DalTableDao<>(new DalDefaultJpaParser<>(IntegrationOrderDetail.class));
  }

  /**
   * Query IntegrationOrderDetail by the specified ID
   * The ID must be a number
   **/
  public IntegrationOrderDetail queryByPk(Number id)
      throws SQLException {
    return queryByPk(id, null);
  }

  /**
   * Query IntegrationOrderDetail by the specified ID
   * The ID must be a number
   **/
  public IntegrationOrderDetail queryByPk(Number id, DalHints hints)
      throws SQLException {
    hints = DalHints.createIfAbsent(hints);
    return client.queryByPk(id, hints);
  }

  /**
   * Query IntegrationOrderDetail by IntegrationOrderDetail instance which the primary key is set
   **/
  public IntegrationOrderDetail queryByPk(IntegrationOrderDetail pk)
      throws SQLException {
    return queryByPk(pk, null);
  }

  /**
   * Query IntegrationOrderDetail by IntegrationOrderDetail instance which the primary key is set
   **/
  public IntegrationOrderDetail queryByPk(IntegrationOrderDetail pk, DalHints hints)
      throws SQLException {
    hints = DalHints.createIfAbsent(hints);
    return client.queryByPk(pk, hints);
  }

  /**
   * Query against sample pojo. All not null attributes of the passed in pojo
   * will be used as search criteria.
   **/
  public List<IntegrationOrderDetail> queryLike(IntegrationOrderDetail sample)
      throws SQLException {
    return queryLike(sample, null);
  }

  /**
   * Query against sample pojo. All not null attributes of the passed in pojo
   * will be used as search criteria.
   **/
  public List<IntegrationOrderDetail> queryLike(IntegrationOrderDetail sample, DalHints hints)
      throws SQLException {
    hints = DalHints.createIfAbsent(hints);
    return client.queryLike(sample, hints);
  }

  /**
   * Get the all records count
   */
  public int count() throws SQLException {
    return count(null);
  }

  /**
   * Get the all records count
   */
  public int count(DalHints hints) throws SQLException {
    hints = DalHints.createIfAbsent(hints);
    SelectSqlBuilder builder = new SelectSqlBuilder().selectCount();
    return client.count(builder, hints).intValue();
  }

  /**
   * Query IntegrationOrderDetail with paging function
   * The pageSize and pageNo must be greater than zero.
   */
  public List<IntegrationOrderDetail> queryAllByPage(int pageNo, int pageSize)  throws SQLException {
    return queryAllByPage(pageNo, pageSize, null);
  }

  /**
   * Query IntegrationOrderDetail with paging function
   * The pageSize and pageNo must be greater than zero.
   */
  public List<IntegrationOrderDetail> queryAllByPage(int pageNo, int pageSize, DalHints hints)  throws SQLException {
    hints = DalHints.createIfAbsent(hints);

    SelectSqlBuilder builder = new SelectSqlBuilder();
    builder.selectAll().atPage(pageNo, pageSize).orderBy("Id", ASC);

    return client.query(builder, hints);
  }

  /**
   * Get all records from table
   */
  public List<IntegrationOrderDetail> queryAll() throws SQLException {
    return queryAll(null);
  }

  /**
   * Get all records from table
   */
  public List<IntegrationOrderDetail> queryAll(DalHints hints) throws SQLException {
    hints = DalHints.createIfAbsent(hints);

    SelectSqlBuilder builder = new SelectSqlBuilder().selectAll().orderBy("Id", ASC);

    return client.query(builder, hints);
  }

  /**
   * Insert single pojo
   *
   * @param daoPojo
   *            pojo to be inserted
   * @return how many rows been affected
   * @throws SQLException
   */
  public int insert(IntegrationOrderDetail daoPojo) throws SQLException {
    return insert(null, daoPojo);
  }

  /**
   * Insert single pojo
   *
   * @param hints
   *            Additional parameters that instruct how DAL Client perform database operation.
   * @param daoPojo
   *            pojo to be inserted
   * @return how many rows been affected
   * @throws SQLException
   */
  public int insert(DalHints hints, IntegrationOrderDetail daoPojo) throws SQLException {
    if(null == daoPojo)
      return 0;
    hints = DalHints.createIfAbsent(hints);
    return client.insert(hints, daoPojo);
  }

  /**
   * Insert pojos one by one. If you want to inert them in the batch mode,
   * user batchInsert instead. You can also use the combinedInsert.
   *
   * @param daoPojos
   *            list of pojos to be inserted
   * @return how many rows been affected
   */
  public int[] insert(List<IntegrationOrderDetail> daoPojos) throws SQLException {
    return insert(null, daoPojos);
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
  public int[] insert(DalHints hints, List<IntegrationOrderDetail> daoPojos) throws SQLException {
    if(null == daoPojos || daoPojos.size() <= 0)
      return new int[0];
    hints = DalHints.createIfAbsent(hints);
    return client.insert(hints, daoPojos);
  }

  /**
   * Insert pojo and get the generated PK back in keyHolder.
   * If the "set no count on" for MS SqlServer is set, the operation may fail.
   * Please don't pass keyholder for MS SqlServer to avoid the failure in such case.
   *
   * @param keyHolder
   *            holder for generated primary keys
   * @param daoPojo
   *            pojo to be inserted
   * @return how many rows been affected
   * @throws SQLException
   */
  public int insertWithKeyHolder(KeyHolder keyHolder, IntegrationOrderDetail daoPojo) throws SQLException {
    return insert(null, keyHolder, daoPojo);
  }

  /**
   * Insert pojo and get the generated PK back in keyHolder.
   * If the "set no count on" for MS SqlServer is set, the operation may fail.
   * Please don't pass keyholder for MS SqlServer to avoid the failure in such case.
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
  public int insert(DalHints hints, KeyHolder keyHolder, IntegrationOrderDetail daoPojo) throws SQLException {
    if(null == daoPojo)
      return 0;
    hints = DalHints.createIfAbsent(hints);
    return client.insert(hints, keyHolder, daoPojo);
  }

  /**
   * Insert pojos and get the generated PK back in keyHolder.
   * If the "set no count on" for MS SqlServer is set, the operation may fail.
   * Please don't pass keyholder for MS SqlServer to avoid the failure in such case.
   *
   * @param keyHolder
   *            holder for generated primary keys
   * @param daoPojos
   *            list of pojos to be inserted
   * @return how many rows been affected
   * @throws SQLException
   */
  public int[] insertWithKeyHolder(KeyHolder keyHolder, List<IntegrationOrderDetail> daoPojos) throws SQLException {
    return insert(null, keyHolder, daoPojos);
  }

  /**
   * Insert pojos and get the generated PK back in keyHolder.
   * If the "set no count on" for MS SqlServer is set, the operation may fail.
   * Please don't pass keyholder for MS SqlServer to avoid the failure in such case.
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
  public int[] insert(DalHints hints, KeyHolder keyHolder, List<IntegrationOrderDetail> daoPojos) throws SQLException {
    if(null == daoPojos || daoPojos.size() <= 0)
      return new int[0];
    hints = DalHints.createIfAbsent(hints);
    return client.insert(hints, keyHolder, daoPojos);
  }

  /**
   * Insert pojos in batch mode.
   * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the shard.
   *
   * @param daoPojos list of pojos to be inserted
   * @return how many rows been affected for inserting each of the pojo
   * @throws SQLException
   */
  public int[] batchInsert(List<IntegrationOrderDetail> daoPojos) throws SQLException {
    return batchInsert(null, daoPojos);
  }

  /**
   * Insert pojos in batch mode.
   * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the shard.
   *
   * @param hints Additional parameters that instruct how DAL Client perform database operation.
   * @param daoPojos list of pojos to be inserted
   * @return how many rows been affected for inserting each of the pojo
   * @throws SQLException
   */
  public int[] batchInsert(DalHints hints, List<IntegrationOrderDetail> daoPojos) throws SQLException {
    if(null == daoPojos || daoPojos.size() <= 0)
      return new int[0];
    hints = DalHints.createIfAbsent(hints);
    return client.batchInsert(hints, daoPojos);
  }

  /**
   * Insert multiple pojos in one INSERT SQL
   * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the shard.
   *
   * @param daoPojos list of pojos to be inserted
   * @return how many rows been affected
   * @throws SQLException
   */
  public int combinedInsert(List<IntegrationOrderDetail> daoPojos) throws SQLException {
    return combinedInsert(null, daoPojos);
  }

  /**
   * Insert multiple pojos in one INSERT SQL
   * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the shard.
   *
   * @param hints Additional parameters that instruct how DAL Client perform database operation.
   * @param daoPojos list of pojos to be inserted
   * @return how many rows been affected
   * @throws SQLException
   */
  public int combinedInsert(DalHints hints, List<IntegrationOrderDetail> daoPojos) throws SQLException {
    if(null == daoPojos || daoPojos.size() <= 0)
      return 0;
    hints = DalHints.createIfAbsent(hints);
    return client.combinedInsert(hints, daoPojos);
  }

  /**
   * Insert multiple pojos in one INSERT SQL and get the generated PK back in keyHolder.
   * If the "set no count on" for MS SqlServer is set, the operation may fail.
   * Please don't pass keyholder for MS SqlServer to avoid the failure in such case.
   * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the shard.
   *
   * @param keyHolder holder for generated primary keys
   * @param daoPojos list of pojos to be inserted
   * @return how many rows been affected
   * @throws SQLException
   */
  public int combinedInsertWithKeyHolder(KeyHolder keyHolder, List<IntegrationOrderDetail> daoPojos) throws SQLException {
    return combinedInsert(null, keyHolder, daoPojos);
  }

  /**
   * Insert multiple pojos in one INSERT SQL and get the generated PK back in keyHolder.
   * If the "set no count on" for MS SqlServer is set, the operation may fail.
   * Please don't pass keyholder for MS SqlServer to avoid the failure in such case.
   * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the shard.
   *
   * @param hints Additional parameters that instruct how DAL Client perform database operation.
   * @param keyHolder holder for generated primary keys
   * @param daoPojos list of pojos to be inserted
   * @return how many rows been affected
   * @throws SQLException
   */
  public int combinedInsert(DalHints hints, KeyHolder keyHolder, List<IntegrationOrderDetail> daoPojos) throws SQLException {
    if(null == daoPojos || daoPojos.size() <= 0)
      return 0;
    hints = DalHints.createIfAbsent(hints);
    return client.combinedInsert(hints, keyHolder, daoPojos);
  }

  /**
   * Delete the given pojo.
   *
   * @param daoPojo pojo to be deleted
   * @return how many rows been affected
   * @throws SQLException
   */
  public int delete(IntegrationOrderDetail daoPojo) throws SQLException {
    return delete(null, daoPojo);
  }

  /**
   * Delete the given pojo.
   *
   * @param hints Additional parameters that instruct how DAL Client perform database operation.
   * @param daoPojo pojo to be deleted
   * @return how many rows been affected
   * @throws SQLException
   */
  public int delete(DalHints hints, IntegrationOrderDetail daoPojo) throws SQLException {
    if(null == daoPojo)
      return 0;
    hints = DalHints.createIfAbsent(hints);
    return client.delete(hints, daoPojo);
  }

  /**
   * Delete the given pojos list one by one.
   *
   * @param daoPojos list of pojos to be deleted
   * @return how many rows been affected
   * @throws SQLException
   */
  public int[] delete(List<IntegrationOrderDetail> daoPojos) throws SQLException {
    return delete(null, daoPojos);
  }

  /**
   * Delete the given pojos list one by one.
   *
   * @param hints Additional parameters that instruct how DAL Client perform database operation.
   * @param daoPojos list of pojos to be deleted
   * @return how many rows been affected
   * @throws SQLException
   */
  public int[] delete(DalHints hints, List<IntegrationOrderDetail> daoPojos) throws SQLException {
    if(null == daoPojos || daoPojos.size() <= 0)
      return new int[0];
    hints = DalHints.createIfAbsent(hints);
    return client.delete(hints, daoPojos);
  }

  /**
   * Delete the given pojo list in batch.
   * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the shard.
   *
   * @param daoPojos list of pojos to be deleted
   * @return how many rows been affected for deleting each of the pojo
   * @throws SQLException
   */
  public int[] batchDelete(List<IntegrationOrderDetail> daoPojos) throws SQLException {
    return batchDelete(null, daoPojos);
  }

  /**
   * Delete the given pojo list in batch.
   * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the shard.
   *
   * @param hints Additional parameters that instruct how DAL Client perform database operation.
   * @param daoPojos list of pojos to be deleted
   * @return how many rows been affected for deleting each of the pojo
   * @throws SQLException
   */
  public int[] batchDelete(DalHints hints, List<IntegrationOrderDetail> daoPojos) throws SQLException {
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
   * @param daoPojo pojo to be updated
   * @return how many rows been affected
   * @throws SQLException
   */
  public int update(IntegrationOrderDetail daoPojo) throws SQLException {
    return update(null, daoPojo);
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
  public int update(DalHints hints, IntegrationOrderDetail daoPojo) throws SQLException {
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
   * @param daoPojos list of pojos to be updated
   * @return how many rows been affected
   * @throws SQLException
   */
  public int[] update(List<IntegrationOrderDetail> daoPojos) throws SQLException {
    return update(null, daoPojos);
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
  public int[] update(DalHints hints, List<IntegrationOrderDetail> daoPojos) throws SQLException {
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
  public int[] batchUpdate(List<IntegrationOrderDetail> daoPojos) throws SQLException {
    return batchUpdate(null, daoPojos);
  }

  /**
   * Update the given pojo list in batch.
   *
   * @return how many rows been affected
   * @throws SQLException
   */
  public int[] batchUpdate(DalHints hints, List<IntegrationOrderDetail> daoPojos) throws SQLException {
    if(null == daoPojos || daoPojos.size() <= 0)
      return new int[0];
    hints = DalHints.createIfAbsent(hints);
    return client.batchUpdate(hints, daoPojos);
  }

  /**
   * query order detail by user id
   **/
  public List<IntegrationOrderDetail> queryByOrderId(Long orderId) throws SQLException {
    return queryByOrderId(orderId, null);
  }

  /**
   * query order detail by user id
   **/
  public List<IntegrationOrderDetail> queryByOrderId(Long orderId, DalHints hints) throws SQLException {
    hints = DalHints.createIfAbsent(hints);

    SelectSqlBuilder builder = new SelectSqlBuilder();
    builder.select("DataChange_LastTime","DataChange_CreatedTime","Quantity","Item","OrderId","Id");
    builder.equal("OrderId", orderId, Types.BIGINT, false);

    return client.query(builder, hints);
  }

  /**
   * query order detail by order id list
   **/
  public List<IntegrationOrderDetail> queryByOrderIdList(List<Long> orderIdList) throws SQLException {
    return queryByOrderIdList(orderIdList, null);
  }

  /**
   * query order detail by order id list
   **/
  public List<IntegrationOrderDetail> queryByOrderIdList(List<Long> orderIdList, DalHints hints) throws SQLException {
    hints = DalHints.createIfAbsent(hints);

    SelectSqlBuilder builder = new SelectSqlBuilder();
    builder.select("DataChange_LastTime","DataChange_CreatedTime","Quantity","Item","OrderId","Id");
    builder.in("OrderId", orderIdList, Types.BIGINT, false);

    return client.query(builder, hints);
  }

  /**
   * delete by order id
   **/
  public int deleteByOrderId(Long orderId) throws SQLException {
    return deleteByOrderId(orderId, null);
  }

  /**
   * delete by order id
   **/
  public int deleteByOrderId(Long orderId, DalHints hints) throws SQLException {
    hints = DalHints.createIfAbsent(hints);

    DeleteSqlBuilder builder = new DeleteSqlBuilder();
    builder.equal("OrderId", orderId, Types.BIGINT, false);

    return client.delete(builder, hints);
  }

  /**
   * delete by order id list
   **/
  public int deleteByOrderIdList(List<Long> orderIdList) throws SQLException {
    return deleteByOrderIdList(orderIdList, null);
  }

  /**
   * delete by order id list
   **/
  public int deleteByOrderIdList(List<Long> orderIdList, DalHints hints) throws SQLException {
    hints = DalHints.createIfAbsent(hints);

    DeleteSqlBuilder builder = new DeleteSqlBuilder();
    builder.in("OrderId", orderIdList, Types.BIGINT, false);

    return client.delete(builder, hints);
  }
}
