package DRTestOnMysql;

import com.ctrip.framework.dal.datasourceswitch.ConnectionStringSwitch;
import com.ctrip.framework.dal.datasourceswitch.PoolPropertiesSwitch;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by lilj on 2018/3/4.
 */
public class QPSTestFat16 {
    private static DRTestDao dao;

    private static AtomicInteger exceptionCount = new AtomicInteger();
    private static Logger log = LoggerFactory.getLogger(QPSTestFat16.class);
    private static ConnectionStringSwitch connectionStringSwitch = new ConnectionStringSwitch();
    private static PoolPropertiesSwitch poolPropertiesSwitch=new PoolPropertiesSwitch();
    private static String DATA_BASE_1 = "noShardTestOnMysql_111";
    private static String DATA_BASE_2 = "noShardTestOnMysql_122";
    private static String hostname;
    private static int countInFat1868;
    private static int countInFat1869;
    private static Boolean isPro=false;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        dao=new DRTestDao();
        poolPropertiesSwitch.resetPoolProperties();
    }

    @Before
    public void setUp() throws Exception {
        connectionStringSwitch.resetConnectionString(isPro);
        Thread.sleep(5000);
//        poolPropertiesSwitch.resetPoolProperties();
        hostname = dao.selectHostname(null);
        log.info(String.format("current hostname is : %s", hostname));
    }

    @After
    public void tearDown() throws Exception {
//        connectionStringSwitch.resetConnectionString();
//        poolPropertiesSwitch.resetPoolProperties();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception{
        connectionStringSwitch.resetConnectionString(isPro);
        Thread.sleep(5000);
    }

    public void switchDatasource() throws Exception {
        hostname = dao.selectHostname(null);
        boolean toBeContinued = true;
        connectionStringSwitch.postByMHA(isPro);
        while (toBeContinued) {
            try {
                String currentHostname = dao.selectHostname(null);
                if (currentHostname.equalsIgnoreCase(hostname)) {
                    toBeContinued = true;
                    log.info(String.format("try again"));
                } else {
                    toBeContinued = false;
                    log.info(String.format("MHA switch done"));
                    hostname = currentHostname;
                }
            } catch (Exception e) {
                e.printStackTrace();
                toBeContinued = true;
            }
            Thread.sleep(1000);
        }
    }

    public void clearData() throws Exception {
        dao.test_def_update(null);
        for (int i = 0; i < 2; i++) {
            if (dao.count(null) == 0) {
                switchDatasource();
                dao.test_def_update(null);
            } else {
                dao.test_def_update(null);
            }
            if (dao.count(null) != 0)
                throw new Exception("count is not zero");
        }
    }

    public void getCountInTable() throws Exception {
        DalClientFactory.shutdownFactory();
        DalClientFactory.initClientFactory(this.getClass().getClassLoader().getResource(".").getPath() + "DalConfig/Dal.config");
        DRTestDao dao111 = new DRTestDao(DATA_BASE_1);
        countInFat1868 = dao111.count(null);
        log.info(String.format("count in FAT1868.testtable: %d", countInFat1868));
        DRTestDao dao122 = new DRTestDao(DATA_BASE_2);
        countInFat1869 = dao122.count(null);
        log.info(String.format("count in FAT1869.testtable: %d", countInFat1869));

//        int totalCountInTable=count111+count122;
//        log.info(String.format("total count in table : %d",totalCountInTable));

        DalClientFactory.shutdownFactory();
//        return totalCountInTable;
    }

    //    public static void main(String[] args) throws Exception {
    @Test
    public void highQPSSwitchTest() throws Exception {
        final AtomicInteger totalCount = new AtomicInteger();
        final List<FailExceptionAndID> failIds = Collections.synchronizedList(new ArrayList<FailExceptionAndID>());
        final List<Integer> successIds = Collections.synchronizedList(new ArrayList<Integer>());

        dao = new DRTestDao();

        clearData();

        int threadCount = 200;
        final CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            new Thread(new Runnable() {
                int successCount = 0;
                int failCount = 0;

                @Override
                public void run() {
                    try {
                        log.info(String.format("start thread"));
                        for (int j = 0; j < 100; j++) {
                            int currentId = totalCount.incrementAndGet();
                            try {
                                DRTestPojo pojo = new DRTestPojo();
                                pojo.setID(currentId);
                                dao.insert(new DalHints().enableIdentityInsert(), pojo);
                                successCount++;
                                successIds.add(currentId);
                                Thread.sleep(100);
                            } catch (Exception e) {
                                e.printStackTrace();
                                failCount++;
                                exceptionCount.incrementAndGet();
                                failIds.add(new FailExceptionAndID(currentId, e));
//                                System.out.println("fail id: "+currentId);
                            }
                        }
                    } finally {
                        log.info(String.format("%s Success:%d, fail:%d", Thread.currentThread().getName(), successCount, failCount));
                        if (successCount + failCount != 100) {
                            log.info(String.format("Notice %s", Thread.currentThread().getName()));
                        }
                        latch.countDown();
                    }
                }
            }).start();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    switchDatasource();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        latch.await();

       /* int failButExistCount = 0;
        int successButNonExistCount = 0;

        log.info(String.format("start query failButExist data"));
//        System.setOut(new PrintStream(new File("failButExistDataIds")));
        for (int i = 0; i < failIds.size(); i++) {
            DRTestPojo pojo = dao.queryByPk(failIds.get(i).getId(), null);
//            log.info(String.format("query failButExist index: %d ", i));
            if (pojo != null) {
                failButExistCount++;
                log.info(String.format("fail but exist in table:%d, %s", failIds.get(i).getId(), failIds.get(i).getE()));
//                System.out.println(String.format("fail but exist in table:%d, %s", failIds.get(i).getId(), failIds.get(i).getE()));
            }
        }

        log.info(String.format("start query successButNonExist data"));
//        System.setOut(new PrintStream(new File("successButNonExistDataIds")));
        for (int i = 0; i < successIds.size(); i++) {
            DRTestPojo pojo = dao.queryByPk(successIds.get(i), null);
//            log.info(String.format("query successButNonExist data index: %d", i));
            if (pojo == null) {
                successButNonExistCount++;
                log.info(String.format("success but non-exist in table:%d", successIds.get(i)));
//                System.out.println(String.format("success but non-exist in table:%d", successIds.get(i)));
            }
        }*/

        getCountInTable();
        log.info(String.format("exceptionNum: %d", exceptionCount.get()));
        if (hostname.equalsIgnoreCase("FAT1868")) {
            try {
                log.info(String.format("data count in new master table: %d", countInFat1868));
                Assert.assertEquals(20000, exceptionCount.get() + countInFat1868);
            } catch (Throwable e) {
                Assert.assertEquals(20000, exceptionCount.get() + countInFat1868 + countInFat1869);
            }
        } else {
            try {
                log.info(String.format("data count in new master table: %d", countInFat1869));
                Assert.assertEquals(20000, exceptionCount.get() + countInFat1869);
            } catch (Throwable e) {
                Assert.assertEquals(20000, exceptionCount.get() + countInFat1868 + countInFat1869);
            }
        }
//        Assert.assertEquals(20000, countInTable + exceptionCount.get());
       /* log.info(String.format("fail but exist count: %d", failButExistCount));
        Assert.assertEquals(0,failButExistCount);
        log.info(String.format("success but non-exist count: %d ", successButNonExistCount));
        Assert.assertEquals(0,successButNonExistCount);*/
//        if ((exceptionCount.get() + countInTable) != (20000 + failButExistCount))
//            System.out.println("exceptionCount.get()+countInTable!=20000+failButexistCount");
//        System.exit(-1);
    }

    class FailExceptionAndID {
        private int id;
        private Exception e;

        public FailExceptionAndID(int id, Exception e) {
            this.id = id;
            this.e = e;
        }

        public int getId() {
            return id;
        }

        public Exception getE() {
            return e;
        }
    }

}
