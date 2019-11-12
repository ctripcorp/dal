package com.ctrip.framework.dal.dbconfig.plugin.ignite;

import com.ctrip.framework.dal.dbconfig.plugin.constant.CommonConstants;
import com.ctrip.framework.dal.dbconfig.plugin.service.validator.AppIdIpCheckCache;
import com.ctrip.framework.dal.dbconfig.plugin.util.CommonHelper;
import com.ctrip.framework.vi.IgniteManager;
import com.ctrip.framework.vi.annotation.Ignite;
import com.ctrip.framework.vi.ignite.AbstractCtripIgnitePlugin;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by lzyan on 2018/11/5.
 */
//点火插件id是必须的且全局唯一， 使用bu base package code（参照02-03 各BU Base Package命名规范）作为前缀（例如fx.soa）
//可以用before,after决定点火插件的执行顺序（更多详情请参考点火文档）
@Ignite(id = "arch.titan.plugin.ignite", auto = true)
public class PluginIgnite extends AbstractCtripIgnitePlugin {
    private static Logger logger = LoggerFactory.getLogger(PluginIgnite.class);


    //返回一个组件或应用的帮助页面链接，帮助页面里最好包含联系人和基本介绍以及一些如何利用点火日志排错的信息
    @Override
    public String helpUrl() {
        return "";
    }

    //返回一些对组件或应用极为重要的配置
    @Override
    public Map<String, String> coreConfigs() {
        Map<String, String> configs = new HashMap<>();
        return configs;
    }

    //可以将一些组件或应用初始化动作放在里面,初始化中有致命问题时，可返回false，导致点火失败并终止点火，点火失败的应用不会被拉入集群
    @Override
    public boolean warmUP(IgniteManager.SimpleLogger simpleLogger) {
        logger.info("warmUP(): begin ...");
        TimerManager timerManager = TimerManager.getInstance();

        String ignitePrewarmCacheEnabled = PluginIgniteConfig.getInstance().getIgniteParamValue(CommonConstants.IGNITE_PREWARM_CACHE_ENABLED);
        String ignitePrewarmCacheAppIds = PluginIgniteConfig.getInstance().getIgniteParamValue(CommonConstants.IGNITE_PREWARM_CACHE_APPIDS);
        StringBuilder sb = new StringBuilder("warmUP(): ");
        sb.append("ignitePrewarmCacheEnabled=").append(ignitePrewarmCacheEnabled);
        sb.append(", ignitePrewarmCacheAppIds=").append(ignitePrewarmCacheAppIds);
        String parmInfo = sb.toString();
        simpleLogger.info(parmInfo);
        logger.info(parmInfo);
        if (!Strings.isNullOrEmpty(ignitePrewarmCacheEnabled) && !Strings.isNullOrEmpty(ignitePrewarmCacheAppIds)) {
            boolean ignitePrewarmCache = Boolean.parseBoolean(ignitePrewarmCacheEnabled);
            Splitter splitter = Splitter.on(",").omitEmptyStrings().trimResults();
            List<String> prewarmAppIdList = splitter.splitToList(ignitePrewarmCacheAppIds);
            String currentAppId = CommonHelper.getAppId();
            if (ignitePrewarmCache && prewarmAppIdList.contains(currentAppId)) {
                //--启动缓存定时更新任务
                simpleLogger.info("Begin to startup timer for appId-Ip cache");
                boolean cacheRegister = timerManager.registerAppIdIpCacheTimer();
                simpleLogger.info("End to startup timer for appId-Ip cache");
                if (!cacheRegister) {
                    logger.error("warmUP(): warm up fail since startup appId-Ip cache timer fail!");
                    return cacheRegister;
                }
            } else {
                simpleLogger.info("jump and ignore to prewarm cache ...");
            }
        }

        logger.info("warmUP(): over ...");
        return true;
    }

    //在此方法里执行组件或应用的自检操作
    @Override
    public boolean selfCheck(IgniteManager.SimpleLogger simpleLogger) {
        logger.info("selfCheck(): begin ...");

        boolean step1Pass = true;
        //日志里记录每一次check 的开始和结束，并对check 做简单描述
        simpleLogger.info("Begin check health");
        try {
            AppIdIpCheckCache appIdIpCheckCache = AppIdIpCheckCache.getInstance();
            long normalCacheSize = appIdIpCheckCache.getNormalCacheSize();
            simpleLogger.info("normalCacheSize=" + normalCacheSize);

        } catch (Exception e) {
            logger.error("selfCheck(): check health error!", e);
        }
        //每部分check 完成后，需记录结果
        simpleLogger.info("End check health, result is " + step1Pass);
        //如果check 失败是致命的，直接返回false,导致点火失败并终止点火，点火失败的应用不会被拉入集群
        if (!step1Pass) {
            return false;
        }

        logger.info("selfCheck(): over ...");
        return true;
    }

}

