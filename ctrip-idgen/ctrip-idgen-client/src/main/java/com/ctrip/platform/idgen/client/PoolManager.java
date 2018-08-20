package com.ctrip.platform.idgen.client;

import com.ctrip.platform.idgen.service.api.IdGenRequestType;
import com.ctrip.platform.idgen.service.api.IdGenResponseType;
import com.ctrip.platform.idgen.service.api.IdGenerateService;
import com.ctrip.platform.idgen.service.api.IdSegment;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PoolManager {

    private static final ConcurrentMap<String, IdPool> poolCache = new ConcurrentHashMap<String, IdPool>();

    public static IdPool getPool(String sequenceName) {
        if (!validate(sequenceName)) {
            return null;
        }

        IdPool idPool = poolCache.get(sequenceName);

        if (null == idPool) {

            synchronized (PoolManager.class) {
                idPool = poolCache.get(sequenceName);
                if (null == idPool) {
                    idPool = createPool(sequenceName);
                    if (idPool != null) {
                        IdPool previous = poolCache.putIfAbsent(sequenceName, idPool);
                        if (previous != null) {
                            idPool = previous;
                        }
                    }
                }
            }

        }

        postProcess(idPool);

        return idPool;
    }

    private static boolean validate(String sequenceName) {
        return sequenceName != null && !"".equals(sequenceName.trim());
    }

    private static IdPool createPool(String sequenceName) {
        IdGenRequestType request = new IdGenRequestType(sequenceName,
                PoolManageStrategy.DEFAULT_REQUEST_SIZE, PoolManageStrategy.DEFAULT_TIMEOUT_MILLIS);
        IdGenerateService service = ServiceManager.getIdGenServiceInstance();
        IdGenResponseType response = service.fetchIdPool(request);
        if (null == response) {
            return null;
        }
        List<IdSegment> idSegments = response.getIdSegments();
        if (null == idSegments) {
            return null;
        }
        return new IdPool(idSegments, sequenceName);
    }

    private static void postProcess(IdPool pool) {
        if (pool != null) {
            if (pool.getManageStrategy().ifNeedExtendPool()) {
                extendPool(pool);
            }
        }
    }

    private static boolean extendPool(IdPool pool) {
        if (null == pool) {
            return false;
        }

        PoolManageStrategy strategy = pool.getManageStrategy();
        IdGenRequestType request = new IdGenRequestType(pool.getSequenceName(),
                strategy.getSuggestedRequestSize(), strategy.getSuggestedTimeoutMillis());
        IdGenerateService service = ServiceManager.getIdGenServiceInstance();
        IdGenResponseType response = service.fetchIdPool(request);

        if (null == response) {
            return false;
        }

        List<IdSegment> idSegments = response.getIdSegments();
        if (null == idSegments) {
            return false;
        }

        pool.extendPool(idSegments);
        return true;
    }

}
