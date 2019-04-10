package com.ctrip.framework.dal.dbconfig.plugin.service.validator;

import com.ctrip.framework.dal.dbconfig.plugin.constant.CommonConstants;
import com.ctrip.framework.dal.dbconfig.plugin.entity.AppIdIpCheckEntity;
import com.ctrip.framework.dal.dbconfig.plugin.service.AppIdIpManager;
import com.ctrip.framework.dal.dbconfig.plugin.util.CommonHelper;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Created by lzyan on 2018/10/24.
 */
public class AppIdIpCheckCache {
    private static Logger logger = LoggerFactory.getLogger(AppIdIpCheckCache.class);

    private static class AppIdIpCheckCacheSingletonHolder {
        private static final AppIdIpCheckCache instance = new AppIdIpCheckCache();
    }

    private Cache<AppIdIpCheckEntity, Integer> normalCache = null;      //常驻缓存, 存放的是状态正常的关系, return_code=0
    private LoadingCache<AppIdIpCheckEntity, Integer> tmpCache = null;  //临时缓存, 主要存放的是状态异常的关系(可能也有部分状态正常的数据)
    private AppIdIpManager appIdIpManager;

    //constructor - private
    private AppIdIpCheckCache() {
        appIdIpManager = new AppIdIpManager();
    }

    //Expose method
    public static AppIdIpCheckCache getInstance() {
        return AppIdIpCheckCacheSingletonHolder.instance;
    }


    /**
     * Build normal cache (general cache)
     * [1] rebuild timely
     * [2] key not expired
     *
     * @return
     */
    private <K, V> Cache<K, V> buildNormalCache() {
        Cache<K, V> cache = CacheBuilder
                .newBuilder()
                .maximumSize(100000)    // 10W
                .build();
        return cache;
    }

    /**
     * Build tmp cache (loadingCache)
     * [1] not rebuild
     * [2] key will expired after 1 min
     *
     * @return
     */
    private <K, V> LoadingCache<K, V> buildTmpCache(CacheLoader<K, V> cacheLoader) {
        LoadingCache<K, V> cache = CacheBuilder
                .newBuilder()
                .maximumSize(10000) // 1W
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build(cacheLoader);
        return cache;
    }

    //get normal status cache
    public Cache<AppIdIpCheckEntity, Integer> getNormalCache() {
        if (normalCache == null) {
            synchronized (this) {
                if (normalCache == null) {
                    normalCache = buildNormalCache();
                }
            }
        }
        return normalCache;
    }

    //get tmp cache
    public LoadingCache<AppIdIpCheckEntity, Integer> getTmpCache() {
        if (tmpCache == null) {
            synchronized (this) {
                if (tmpCache == null) {
                    tmpCache = buildTmpCache(new CacheLoader<AppIdIpCheckEntity, Integer>() {
                        @Override
                        public Integer load(AppIdIpCheckEntity key) throws Exception {
                            Cat.logEvent("TitanPlugin.AppIdIpCheck.Cache.Miss", "TmpCache", Event.SUCCESS, "key=" + key);
                            return appIdIpManager.checkAppIdIp(key);
                        }
                    });
                }
            }
        }
        return tmpCache;
    }


    /**
     * 从缓存中获取数据
     * [1] 首先从 normalCache 中获取
     * [2] 如果没有再从 tmpCache 中获取, tmpCache 中如果没有会自动发送请求向PaaS实时获取
     * [2.1] 如果获取到了，且return_code=0则放回到 normalCache 中。若是其它code则仅存放在 tmpCache 中
     * [2.2] 如果没有获取到(获取过程中有异常), 则将默认的 return_code=4(内部异常)放在 tmpCache 中
     *
     * @param appIdIpCheckEntity
     * @return
     */
    private Integer getValueInCache(AppIdIpCheckEntity appIdIpCheckEntity) {
        Integer returnCode = getNormalCache().getIfPresent(appIdIpCheckEntity);
        if (returnCode == null) {
            Cat.logEvent("TitanPlugin.AppIdIpCheck.Cache.Miss", "NormalCache", Event.SUCCESS, "appIdIpCheckEntity=" + appIdIpCheckEntity);
            try {
                returnCode = getTmpCache().get(appIdIpCheckEntity);
                if (returnCode != null && returnCode.intValue() == CommonConstants.PAAS_RETURN_CODE_SUCCESS) {
                    getNormalCache().put(appIdIpCheckEntity, returnCode);
                }
            } catch (Exception e) {
                logger.warn("getValueInCache(): not found in tmpCache!", e);
            }
        }
        return returnCode;
    }


    /**
     * check match or not
     * [1] 如果请求中有异常, 默认放过
     * [2] 如果 returnCode 在配置范围内的则认为通过
     *
     * @param appIdIpCheckEntity
     * @return
     */
    public boolean isAppIdIpMatch(AppIdIpCheckEntity appIdIpCheckEntity) {
        if (appIdIpCheckEntity == null) {
            return false;
        }
        boolean match = true;
        try {
            Integer returnCode = getValueInCache(appIdIpCheckEntity);
            List<String> passCodeList = appIdIpCheckEntity.getPassCodeList();
            if (passCodeList == null) {
                passCodeList = Lists.newArrayList();
            }
            if (returnCode != null && passCodeList.contains(returnCode.toString())) {
                match = true;
            } else {
                match = false;
                Cat.logEvent("AppIdIpCheckServiceValidator", "APPID_IP_CHECK_FAIL", Event.SUCCESS, "appId-Ip check fail! returnCode=" + returnCode);
            }
        } catch (Exception e) {
            Cat.logError("check appId-Ip error, appIdIpCheckEntity=" + appIdIpCheckEntity, e);
        }
        return match;
    }


    //get normalCache size
    public long getNormalCacheSize() {
        return getNormalCache().size();
    }

    //cache rebuild
    public void reBuildNormalCache() {
        logger.info("reBuildCache(): build new normal cache begin ...");

        Cache<AppIdIpCheckEntity, Integer> newNormalCache = buildNormalCache();
        // preWarm data
        preWarmCache(newNormalCache);
        logger.info("reBuildCache(): newNormalCache.size()=" + newNormalCache.size());
        logger.info("reBuildCache(): tmpCache.size()=" + getTmpCache().size());

        synchronized (this) {
            Cache<AppIdIpCheckEntity, Integer> oldNormalCache = getNormalCache();
            normalCache = newNormalCache;
            oldNormalCache.invalidateAll();
        }

        logger.info("reBuildCache(): build new normal cache end ...");
    }

    // preWarm cache and fill data
    private void preWarmCache(Cache<AppIdIpCheckEntity, Integer> cache) {
        if (cache != null) {
            String env = CommonHelper.getEnv().getEnvFamily().getName();
            // env 统一使用小写
            if (!Strings.isNullOrEmpty(env)) {
                env = env.toLowerCase();
            }
            List<AppIdIpCheckEntity> appIdIpCheckEntityList = appIdIpManager.fetchAllExistAppIdIp(env);
            logger.info("preWarmCache():env=" + env + ", appIdIpCheckEntityList.size()=" + (appIdIpCheckEntityList == null ? 0 : appIdIpCheckEntityList.size()));
            if (appIdIpCheckEntityList != null && !appIdIpCheckEntityList.isEmpty()) {
                for (AppIdIpCheckEntity entity : appIdIpCheckEntityList) {
                    if (entity != null) {
                        //all prewarm data is success code (return_code=0)
                        cache.put(entity, CommonConstants.PAAS_RETURN_CODE_SUCCESS);
                    }
                }
            }
        }
    }


}
