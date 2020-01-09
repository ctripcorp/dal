package com.ctrip.platform.dal.dao.client;


import com.ctrip.platform.dal.exceptions.DalRuntimeException;


import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Implements the common logic.
 *
 * @author gzxia
 */
public abstract class LoggerAdapter implements DalLogger {
    public static final String DEFAULT_SECRET_KEY = "dalctripcn";

    private static final String SAMPLING = "sampling";
    private static final String SAMPLINGSTRATEGY = "samplingStrategy";
    private static final String ENCRYPT = "encrypt";
    private static final String SECRETKEY = "secretKey";
    private static final String SIMPLIFIED = "simplified";
    private static final String ASYNCLOGGING = "asyncLogging";

    private static final String CAPACITY = "capacity";
    private static final String SAMPLINGRATE = "samplingRate";

    protected static boolean simplifyLogging = false;
    protected static boolean encryptLogging = true;
    public static String secretKey = DEFAULT_SECRET_KEY;
    protected static boolean samplingLogging = true;
    protected static int samplingRate = 5;
    protected ILogSamplingStrategy logSamplingStrategy;

    protected static boolean asyncLogging = false;

    protected static ExecutorService executor = null;

    /**
     * Helper method to unify async and sync invocation
     *
     * @param task
     */
    public void call(Runnable task) {
        if (asyncLogging) {
            executor.submit(task);
        } else {
            task.run();
        }
    }

    @Override
    public void initialize(Map<String, String> settings) {
        if (settings == null)
            return;

        initSampling(settings);

        if (settings.containsKey(SIMPLIFIED))
            simplifyLogging = Boolean.parseBoolean(settings.get(SIMPLIFIED));

        if (settings.containsKey(ENCRYPT))
            encryptLogging = Boolean.parseBoolean(settings.get(ENCRYPT));

        if (settings.containsKey(SECRETKEY))
            secretKey = settings.get(SECRETKEY);

        initAsyncLogging(settings);
    }

    private void initSampling(Map<String, String> settings) {
        if (settings.containsKey(SAMPLING))
            samplingLogging = Boolean.parseBoolean(settings.get(SAMPLING));

        if (settings.containsKey(SAMPLINGRATE))
            samplingRate = Integer.parseInt(settings.get(SAMPLINGRATE));

        if (samplingLogging) {
            String strategy;
            if (settings.containsKey(SAMPLINGSTRATEGY)) {
                strategy = settings.get(SAMPLINGSTRATEGY).trim();
                if (!strategy.isEmpty())
                    try {
                        logSamplingStrategy = (ILogSamplingStrategy) Class.forName(strategy).newInstance();
                    } catch (Exception e) {
                        throw new DalRuntimeException("An error occurred while creating user-defined log sampling strategy", e);
                    }
            } else
                logSamplingStrategy = new DefaultLogSamplingStrategy();
        }
    }

    private void initAsyncLogging(Map<String, String> settings) {
        if (settings.containsKey(ASYNCLOGGING))
            asyncLogging = Boolean.parseBoolean(settings.get(ASYNCLOGGING));

        if (settings.containsKey(CAPACITY)) {
            executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<Runnable>(Integer.parseInt(settings.get(CAPACITY)), true),
                    new RejectedExecutionHandler() {
                        @Override
                        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                            //do nothing
                        }
                    });
        } else {
            executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        }
    }

    @Override
    public void shutdown() {
        if (executor != null)
            executor.shutdown();
    }
}
