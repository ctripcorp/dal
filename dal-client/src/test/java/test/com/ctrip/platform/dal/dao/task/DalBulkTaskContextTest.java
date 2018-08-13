package test.com.ctrip.platform.dal.dao.task;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.task.*;
import org.junit.Test;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Callable;

import static org.junit.Assert.*;

/**
 * Created by lilj on 2018/8/12.
 */
public class DalBulkTaskContextTest {
    private DalRequestExecutor executor = new DalRequestExecutor();
    private class TestPojo {
        Integer index;
        Integer tableIndex;
        TestPojo(Integer index,Integer tableIndex){
            this.index = index;
            this.tableIndex=tableIndex;
        }
    }

    private class TestBulkTask implements BulkTask<Set<String>, TestPojo> {

        @Override
        public void initialize(DalParser<TestPojo> parser) {
        }

        @Override
        public List<Map<String, ?>> getPojosFields(List<TestPojo> daoPojos) {
            List<Map<String, ?>> daoPojoMaps = new ArrayList<>();
            for(TestPojo pojo: daoPojos) {
                Map<String, Object> value = new HashMap<String, Object>();
                value.put("index", pojo.index);
                value.put("tableIndex", pojo.tableIndex);
                daoPojoMaps.add(value);
            }
            return daoPojoMaps;
        }

        @Override
        public Set<String> getEmptyValue() {
            return Collections.emptySet();
        }

        @Override
        public Set<String> execute(DalHints hints, Map<Integer, Map<String, ?>> shaffled, DalBulkTaskContext<TestPojo> ctx) throws SQLException {
            Set<String> tables = ctx.getTables();
            tables.add("test_table_" + shaffled.size());
            return tables;
        }

        @Override
        public BulkTaskResultMerger<Set<String>> createMerger() {
            return new ShardedSetResultMerger();
        }

        @Override
        public BulkTaskContext<TestPojo> createTaskContext(DalHints hints,
                                                                                  List<Map<String, ?>> daoPojos, List<TestPojo> rawPojos)
                throws SQLException {
            return new BulkTaskContext(rawPojos);
        }
    }

    static{
        try {
            DalClientFactory.initClientFactory();
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testCreateTask() {
        DalBulkTaskRequest<Set<String>, TestPojo> test = null;
        List<TestPojo> pojos = null;
        Callable<Set<String>> task = null;
        try {
            // Empty
            pojos = new ArrayList<TestPojo>();
            test = new DalBulkTaskRequest<>("dao_test_sqlsvr_dbShard", "", new DalHints(), pojos, new TestBulkTask());
            test.validate();
            test.isCrossShard();
            task = test.createTask();
            assertEquals(0, task.call().size());

            // Shuffled in one shard
            pojos = new ArrayList<TestPojo>();
            pojos.add(new TestPojo(0,0));
            pojos.add(new TestPojo(0,0));
            test = new DalBulkTaskRequest<>("dao_test_sqlsvr_dbShard", "", new DalHints(), pojos, new TestBulkTask());
            test.validate();
            test.isCrossShard();
            task = test.createTask();
            assertNotNull(task);
            assertEquals("[test_table_2]", task.call().toString());

            // Do not shuffle
            pojos = new ArrayList<TestPojo>();
            pojos.add(new TestPojo(0,0));
            pojos.add(new TestPojo(0,0));
            pojos.add(new TestPojo(0,0));
            test = new DalBulkTaskRequest<>("dao_test_sqlsvr_dbShard", "", new DalHints().inShard(1), pojos, new TestBulkTask());
            // To create pojos
            test.validate();
            test.isCrossShard();
            task = test.createTask();
            assertNotNull(task);
            assertEquals("[test_table_3]", task.call().toString());

            //single table shard
            pojos = new ArrayList<TestPojo>();
            pojos.add(new TestPojo(0,0));
            pojos.add(new TestPojo(0,0));
            pojos.add(new TestPojo(0,0));
            test = new DalBulkTaskRequest<>("dao_test_sqlsvr_dbTableShard", "dal_client_test", new DalHints(), pojos, new TestBulkTask());
            // To create pojos
            test.validate();
            test.isCrossShard();
            task = test.createTask();
            assertNotNull(task);
            assertEquals("[test_table_3]", task.call().toString());



        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testCreateTasks() {
        DalBulkTaskRequest<Set<String>, TestPojo> test = null;
        List<TestPojo> pojos = null;
        Map<String, Callable<Set<String>>> tasks = null;
        try {
            // Shuffled in two shards and two table shards in each shards
            pojos = new ArrayList<TestPojo>();
            pojos.add(new TestPojo(0,0));
            pojos.add(new TestPojo(1,2));
            pojos.add(new TestPojo(1,3));
            test = new DalBulkTaskRequest<>("dao_test_sqlsvr_dbShard", "dal_client_test", new DalHints(), pojos, new TestBulkTask());
            test.validate();
            test.isCrossShard();
            tasks = test.createTasks();
            assertEquals(2, tasks.size());

            assertEquals("[test_table_1]", tasks.get("0").call().toString());
            assertEquals("[test_table_2]", tasks.get("1").call().toString());

            // Shuffled in two shards
            pojos = new ArrayList<TestPojo>();
            pojos.add(new TestPojo(0,0));
            pojos.add(new TestPojo(0,1));
            pojos.add(new TestPojo(0,2));
            pojos.add(new TestPojo(0,3));
            pojos.add(new TestPojo(1,1));
            test = new DalBulkTaskRequest<>("dao_test_sqlsvr_dbShard", "dal_client_test", new DalHints(), pojos, new TestBulkTask());
            test.validate();
            test.isCrossShard();
            tasks = test.createTasks();
            assertEquals(2, tasks.size());


            assertEquals("[test_table_4]", tasks.get("0").call().toString());
            assertEquals("[test_table_1]", tasks.get("1").call().toString());

            Set<String> total = executor.execute(new DalHints(), test);
            assertEquals(2, total.size());
            assertTrue(total.contains("test_table_4"));
            assertTrue(total.contains("test_table_1"));
        } catch (Exception e) {
            fail();
        }
    }


    public class ShardedSetResultMerger implements BulkTaskResultMerger<Set<String>>{
        private Set<String> total = new HashSet<>();

        public void recordPartial(String shard, Integer[] partialIndex) {
        }

        @Override
        public void addPartial(String shard, Set<String> affected) throws SQLException {
            total.addAll(affected);
        }

        @Override
        public Set<String> merge() throws SQLException {
            return total;
        }
    }
}
