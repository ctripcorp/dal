package idgentest;

import com.ctrip.framework.idgen.client.IdGeneratorFactory;
import com.ctrip.platform.dal.sharding.idgen.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by lilj on 2018/8/24.
 */
public class StableTest {
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    private int requireQps = Integer.parseInt(System.getProperty("qps", "50000"));
    private int setCleanTime = Integer.parseInt(System.getProperty("setCleanTimeSeconds", "30"));
    private Logger logger = LoggerFactory.getLogger(getClass());

    private AtomicLong count = new AtomicLong(0);
    private long lastCount = 0, lastTime = System.currentTimeMillis();
    private AtomicReference<Set<Long>> allIds = new AtomicReference<Set<Long>>(new HashSet<Long>());

    public static void main(String []argc){

        new StableTest().start();

    }

    private void start() {

        int waitUs = 1000000/requireQps;
        logger.info("QPS:{}, setCleanTime:{}", requireQps, setCleanTime);

        startQpsChecker();
        startSetChanger();

        final IdGenerator idGenerator = IdGeneratorFactory.getInstance().getOrCreateLongIdGenerator("testName1");
        long start = System.currentTimeMillis();
        executor.scheduleAtFixedRate(new Runnable() {

            public void run() {
                try {
                    idGenerator.nextId().longValue();
                    long id = count.incrementAndGet();
                    synchronized (allIds){
                        boolean add = allIds.get().add(id);
                        if(!add){
                            logger.error("id already exists" + id);
                        }
                    }
                }catch (Exception e){
                    logger.error("error fetch id", e);
                }
            }
        }, 0, waitUs, TimeUnit.MICROSECONDS);
    }

    private void startSetChanger() {
        executor.scheduleWithFixedDelay(new Runnable() {

            public void run() {

                logger.info("change id set");
                allIds.set(new HashSet<Long>());
            }
        }, setCleanTime, setCleanTime, TimeUnit.SECONDS);


    }

    private void startQpsChecker() {

        executor.scheduleAtFixedRate(new Runnable() {

            public void run() {

                long currentCount = count.get();
                long currentTime = System.currentTimeMillis();

                logger.info("QPS:{}", (currentCount - lastCount)/((currentTime - lastTime)/1000));

                lastCount = currentCount;
                lastTime = currentTime;

            }
        }, 5, 5, TimeUnit.SECONDS);
    }
}

