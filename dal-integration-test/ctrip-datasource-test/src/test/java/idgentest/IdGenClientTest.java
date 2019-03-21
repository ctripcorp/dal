package idgentest;

import com.ctrip.framework.idgen.client.IdGeneratorFactory;
import com.ctrip.platform.dal.sharding.idgen.IdGenerator;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by lilj on 2018/8/24.
 */
public class IdGenClientTest {
    private static Logger logger = LoggerFactory.getLogger(IdGenClientTest.class);
    @Test
    public void testIdGen() throws Exception {
        IdGenerator idGenerator = IdGeneratorFactory.getInstance().getOrCreateLongIdGenerator("testName1");
        List<Long> idList = new ArrayList<>();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 50000; i++)
            idList.add(idGenerator.nextId().longValue());
        long end = System.currentTimeMillis();
        long cost = end - start;
        System.out.println("cost: " + cost + " ms");
//        Assert.assertTrue(cost <= 1000);
        Set<Long> idSet = new HashSet<>();
        idSet.addAll(idList);
        Assert.assertEquals(50000, idList.size());
        Assert.assertEquals(idList.size(), idSet.size());
    }

    @Test
    public void testIdGenConcurrent() throws Exception {
            final IdGenerator idGenerator = IdGeneratorFactory.getInstance().getOrCreateLongIdGenerator("testName1");
            final List<Long> idList = Collections.synchronizedList(new ArrayList<Long>());
            int requireSize = 50000;
            final CountDownLatch latch = new CountDownLatch(requireSize);
            final AtomicBoolean result = new AtomicBoolean(true);
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
            long start = System.currentTimeMillis();
            for (int i = 0; i < requireSize; i++) {
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (int j = 0; j < 100; j++) {
                                idList.add(idGenerator.nextId().longValue());
                            }
                        } catch (Exception e) {
                            logger.info(e.toString());
                            result.set(false);
                        } finally {
                            latch.countDown();
                        }
                    }
                });
            }
            latch.await();
            Assert.assertTrue(result.get());
            long end = System.currentTimeMillis();
            long cost = end - start;
            int idSize = idList.size();
            logger.info(idSize + " cost " + cost + " ms");
            Assert.assertEquals(requireSize*100, idSize);
//            check duplicate
            Set<Long> idSet = new HashSet<>();
            idSet.addAll(idList);
            Assert.assertEquals(idSize, idSet.size());
    }

   /* @Test
    public void testCdubboTimeout() {
        IdGenRequestType request = new IdGenRequestType("testName1", 2000, 1000);

        long start = System.currentTimeMillis();
        try {
            logger.info("start get service");
            IdGenerateService service = ServiceManager.getInstance().getIdGenServiceInstance();
            logger.info("start fetch pool");
            service.fetchIdPool(request);
            logger.info(" success cost: " + (System.currentTimeMillis() - start));
        } catch (Exception e) {
            logger.error(" fail cost: " + (System.currentTimeMillis() - start));
            e.printStackTrace();
        }
    }*/

   /* @Test
    public void testIdGenServiceShutdown() throws Exception {
        final IdGenerator idGenerator = IdGeneratorFactory.getInstance().getOrCreateIdGenerator("testName1");
        final List<Long> idList = Collections.synchronizedList(new ArrayList<Long>());

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
//        long start = System.currentTimeMillis();
        for (int i = 0; ; i++) {
            executor.schedule(new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        idList.add(idGenerator.nextId().longValue());
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {

                    }
                }
            }), 0, TimeUnit.MILLISECONDS);
        }
    }*/

}
