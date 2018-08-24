package com.ctrip.framework.idgen.client.generator;

import com.ctrip.framework.idgen.client.strategy.DefaultStrategy;
import com.ctrip.platform.dal.sharding.idgen.IdGenerator;
import com.ctrip.framework.idgen.client.strategy.PrefetchStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DynamicIdGenerator implements IdGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicIdGenerator.class);

    private final String sequenceName;
    private final Deque<IdGenerator> idGeneratorQueue = new ConcurrentLinkedDeque<>();
    private final PrefetchStrategy strategy;

    public DynamicIdGenerator(String sequenceName) {
        this(sequenceName, new DefaultStrategy());
    }

    public DynamicIdGenerator(String sequenceName, PrefetchStrategy strategy) {
        this.sequenceName = sequenceName;
        this.strategy = (strategy != null) ? strategy : new DefaultStrategy();
    }

    public void initialize() {
        IdGenerator idGenerator = new StaticIdGenerator(sequenceName, strategy);
        ((StaticIdGenerator) idGenerator).initialize();
        idGeneratorQueue.addLast(idGenerator);
        ((DefaultStrategy) strategy).initialize(idGeneratorQueue);
    }

    @Override
    public Number nextId() {
        Number id = null;
        Iterator<IdGenerator> iterator = idGeneratorQueue.iterator();
        while (iterator.hasNext()) {
            IdGenerator staticIdGenerator = iterator.next();
            id = staticIdGenerator.nextId();
            if (id != null) {
                break;
            } else {
                iterator.remove();
            }
        }
        postProcess(id != null);
        return id;
    }

    private void postProcess(boolean successFlag) {
        if (successFlag) {
            ((DefaultStrategy) strategy).decreaseRemainedSize();
        }
        if (strategy.checkIfNeedPrefetch()) {
            ExecutorService es = Executors.newSingleThreadExecutor();
            es.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        IdGenerator idGenerator = new StaticIdGenerator(sequenceName, strategy);
                        ((StaticIdGenerator) idGenerator).initialize();
                        idGeneratorQueue.addLast(idGenerator);
                        ((DefaultStrategy) strategy).initialize(idGeneratorQueue);
                        LOGGER.info("Prefetch Id pool successfully, fetch size: " + ((StaticIdGenerator) idGenerator).getRemainedSize());
//                        System.out.println("Prefetch Id pool successfully, fetch size: " + ((StaticIdGenerator) idGenerator).getRemainedSize());
                    } catch (Throwable t) {
                        LOGGER.error("Prefetch Id pool failed", t);
//                        System.out.println("Prefetch Id pool failed");
                    }
                }
            });
            es.shutdown();
        }
    }

}
