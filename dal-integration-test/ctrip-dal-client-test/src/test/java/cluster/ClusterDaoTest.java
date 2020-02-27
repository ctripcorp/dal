package cluster;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.sqlbuilder.DeleteSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.InsertSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.SelectSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.UpdateSqlBuilder;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;
import entity.MysqlPersonTable;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * @author c7ch23en
 */
public class ClusterDaoTest {

    private static final String CLUSTER_NAME = "dal_sharding_cluster";

    private DalTableDao<MysqlPersonTable> tableDao;

    public ClusterDaoTest() throws Exception {
        tableDao = new DalTableDao<>(MysqlPersonTable.class, CLUSTER_NAME);
    }

    @Test
    public void doTests() {
        // normal cluster
        doWriteTests(AssertHandler.NORMAL);
        doReadTests();
        // drc cluster
        doWriteTests(AssertHandler.BLOCKED);
        doReadTests();
    }

    private void doWriteTests(AssertHandler handler) {
        doTest(this::testInsertPojo, handler);
        doTest(this::testInsertPojos, handler);
        doTest(this::testCombinedInsertPojos, handler);
        doTest(this::testBatchInsertPojos, handler);
        doTest(this::testInsertWithBuilder, handler);
        doTest(this::testUpdatePojo, handler);
        doTest(this::testUpdatePojos, handler);
        doTest(this::testBatchUpdatePojos, handler);
        doTest(this::testUpdateWithBuilder, handler);
        doTest(this::testDeletePojo, handler);
        doTest(this::testDeletePojos, handler);
        doTest(this::testBatchDeletePojos, handler);
        doTest(this::testDeleteWithBuilder, handler);
        doTest(this::testQueryByPk, handler);
        doTest(this::testQueryBy, handler);
        doTest(this::testQueryWithBuilder, handler);
    }

    private void doReadTests() {
        doTest(this::testQueryByPk, AssertHandler.NORMAL);
        doTest(this::testQueryBy, AssertHandler.NORMAL);
        doTest(this::testQueryWithBuilder, AssertHandler.NORMAL);
    }

    private void doTest(TestRunner task, AssertHandler handler) {
        try {
            task.run();
            handler.assertPass();
        } catch (Exception e) {
            handler.assertFail(e);
        }
    }

    private void testInsertPojo() throws Exception {
        tableDao.insert(createHints(), createPojo());
    }

    private void testInsertPojos() throws Exception {
        tableDao.insert(createHints(), createPojos());
    }

    private void testCombinedInsertPojos() throws Exception {
        tableDao.combinedInsert(createHints(), createPojos());
    }

    private void testBatchInsertPojos() throws Exception {
        tableDao.batchInsert(createHints(), createPojos());
    }

    private void testInsertWithBuilder() throws Exception {
        tableDao.insert(createInsertBuilder(), createHints());
    }

    private void testUpdatePojo() throws Exception {
        tableDao.update(createHints(), createPojo());
    }

    private void testUpdatePojos() throws Exception {
        tableDao.update(createHints(), createPojos());
    }

    private void testBatchUpdatePojos() throws Exception {
        tableDao.batchUpdate(createHints(), createPojos());
    }

    private void testUpdateWithBuilder() throws Exception {
        tableDao.update(createUpdateBuilder(), createHints());
    }

    private void testDeletePojo() throws Exception {
        tableDao.delete(createHints(), createPojo());
    }

    private void testDeletePojos() throws Exception {
        tableDao.delete(createHints(), createPojos());
    }

    private void testBatchDeletePojos() throws Exception {
        tableDao.batchDelete(createHints(), createPojos());
    }

    private void testDeleteWithBuilder() throws Exception {
        tableDao.delete(createDeleteBuilder(), createHints());
    }

    private void testQueryByPk() throws Exception {
        tableDao.queryByPk(createPojo(), createHints());
    }

    private void testQueryBy() throws Exception {
        tableDao.queryBy(createPojo(), createHints());
    }

    private void testQueryWithBuilder() throws Exception {
        tableDao.query(createSelectSqlBuilder(), createHints());
    }

    private DalHints createHints() {
        return new DalHints();
    }

    private MysqlPersonTable createPojo() {
        return createPojo(10, "testName", 100);
    }

    private List<MysqlPersonTable> createPojos() {
        List<MysqlPersonTable> pojos = new ArrayList<>();
        pojos.add(createPojo(11, "testName1", 101));
        pojos.add(createPojo(12, "testName2", 102));
        return pojos;
    }

    private InsertSqlBuilder createInsertBuilder() {
        InsertSqlBuilder builder = new InsertSqlBuilder();
        builder.set("name", "testName", Types.VARCHAR);
        builder.set("age", 100, Types.INTEGER);
        return builder;
    }

    private UpdateSqlBuilder createUpdateBuilder() throws Exception {
        UpdateSqlBuilder builder = new UpdateSqlBuilder();
        builder.update("age", 100, Types.INTEGER);
        builder.equal("name", "testName", Types.VARCHAR);
        return builder;
    }

    private DeleteSqlBuilder createDeleteBuilder() throws Exception {
        DeleteSqlBuilder builder = new DeleteSqlBuilder();
        builder.equal("name", "testName", Types.VARCHAR);
        return builder;
    }

    private SelectSqlBuilder createSelectSqlBuilder() throws Exception {
        SelectSqlBuilder builder = new SelectSqlBuilder();
        builder.equal("name", "testName", Types.VARCHAR);
        return builder;
    }

    private MysqlPersonTable createPojo(Integer id, String name, Integer age) {
        MysqlPersonTable pojo = new MysqlPersonTable();
        pojo.setID(id);
        pojo.setName(name);
        pojo.setAge(age);
        return pojo;
    }

    interface TestRunner {
        void run() throws Exception;
    }

    enum AssertHandler {
        NORMAL() {
            @Override
            void assertPass() {}
            @Override
            void assertFail(Exception e) {
                throw new RuntimeException(e);
            }
        },
        BLOCKED() {
            @Override
            void assertPass() {
                Assert.fail();
            }
            @Override
            void assertFail(Exception e) {
                if (e instanceof DalException)
                    Assert.assertEquals(((DalException) e).getErrorCode(), ErrorCode.NonLocalRequestBlocked.getCode());
                throw new RuntimeException(e);
            }
        };
        abstract void assertPass();
        abstract void assertFail(Exception e);
    }

}
