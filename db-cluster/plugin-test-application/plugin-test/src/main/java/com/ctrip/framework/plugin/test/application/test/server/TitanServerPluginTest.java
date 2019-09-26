package com.ctrip.framework.plugin.test.application.test.server;

import com.ctrip.framework.plugin.test.application.service.ConfigService;
import com.ctrip.framework.plugin.test.application.util.RC4;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Transaction;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qunar.tc.qconfig.client.Configuration;
import qunar.tc.qconfig.client.Feature;
import qunar.tc.qconfig.client.TypedConfig;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by shenjie on 2019/7/2.
 */
@Slf4j
@Component
public class TitanServerPluginTest {

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private String TITAN_QCONFIG_CONTENT_LINE_SPLITTER = "\n";
    private String TITAN_QCONFIG_CONTENT_SPLITTER = ";";

    @Autowired
    private ConfigService configService;

    @PostConstruct
    private void init() throws Exception {
        try {
            addListener();
            addTask();
        } catch (Exception e) {
            Cat.logError("TitanServerPluginTest init failed", e);
            log.error("TitanServerPluginTest init failed", e);
            throw new Exception("TitanServerPluginTest init failed", e);
        }
    }

    private void addTask() {
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                String titanKeyName = configService.getTitanKeyName();
                Transaction transaction = Cat.newTransaction("TitanServerPluginTest.GetConnectingString", titanKeyName);
                log.info("TitanServerPluginTest get connectingString begin...");
                try {
                    String connectingString = getConnectingString();
                    Preconditions.checkArgument(StringUtils.isNotBlank(connectingString), "connecting string is null or empty.");

                    Splitter splitter1 = Splitter.on(TITAN_QCONFIG_CONTENT_LINE_SPLITTER).omitEmptyStrings().trimResults();
                    Splitter splitter2 = Splitter.on(TITAN_QCONFIG_CONTENT_SPLITTER).omitEmptyStrings().trimResults();
                    List<String> connectionStrings = splitter1.splitToList(connectingString);

                    if (connectionStrings != null) {
                        Cat.logEvent("ConnectingStrings.count", String.valueOf(connectionStrings.size()));
                        log.info("ConnectingStrings count is {}.", connectionStrings.size());
                    }

                    String result = "";
                    for (String connection : connectionStrings) {
                        List<String> entities = splitter2.splitToList(connection);
                        String tempResult = "";
                        for (String attr : entities) {
                            if (attr.startsWith("password")) {
                                tempResult = tempResult + RC4.encrypt(attr, titanKeyName) + TITAN_QCONFIG_CONTENT_SPLITTER;
                            } else {
                                tempResult = tempResult + attr + TITAN_QCONFIG_CONTENT_SPLITTER;
                            }
                        }
                        tempResult = tempResult.substring(0, tempResult.length() - 1);
                        result = result + tempResult + TITAN_QCONFIG_CONTENT_LINE_SPLITTER;
                    }
                    Cat.logEvent("ConnectingString", "all", Event.SUCCESS, result);
                    log.info("ConnectingString is {}.", result);
                    List<String> cStrs = splitter1.splitToList(result);
                    if (cStrs != null && cStrs.size() > 0) {
                        Cat.logEvent("ConnectingString", "normal", Event.SUCCESS, splitter1.splitToList(result).get(0));
                        log.info("Normal connectingString is {}.", splitter1.splitToList(result).get(0));
                    }
                    if (cStrs != null && cStrs.size() > 1) {
                        Cat.logEvent("ConnectingString", "failover", Event.SUCCESS, splitter1.splitToList(result).get(1));
                        log.info("Failover connectingString is {}.", splitter1.splitToList(result).get(1));
                    }
                    transaction.setStatus(Transaction.SUCCESS);
                } catch (Exception e) {
                    Cat.logError("Get connecting string from qconfig failed.", e);
                    log.error("Get connecting string from qconfig failed.", e);
                    transaction.setStatus(e);
                } finally {
                    transaction.complete();
                    log.info("TitanServerPluginTest get connectingString end...");
                }
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    private String getConnectingString() {
        Cat.logEvent("TitanServerPluginTest.GetConnectingString.httpsEnable", String.valueOf(configService.isEnableHttps()));
        log.info("TitanServerPluginTest httpsEnable is {}.", configService.isEnableHttps());
        Feature feature = configService.isEnableHttps() ? Feature.create().setHttpsEnable(true).build() : Feature.DEFAULT;
        TypedConfig<String> typedConfig = TypedConfig.get("100010061", configService.getTitanKeyName(), feature, new TypedConfig.Parser<String>() {
            @Override
            public String parse(String data) throws IOException {
                return data;
            }
        });
        String connectingString = typedConfig.current();

        return connectingString;
    }

    private void addListener() {
        Cat.logEvent("TitanServerPluginTest.AddListener.httpsEnable", String.valueOf(configService.isEnableHttps()));
        log.info("TitanServerPluginTest addListener httpsEnable is {}.", configService.isEnableHttps());
        Feature feature = configService.isEnableHttps() ? Feature.create().setHttpsEnable(true).build() : Feature.DEFAULT;
        TypedConfig<String> typedConfig = TypedConfig.get("100010061", configService.getTitanKeyName(), feature, new TypedConfig.Parser<String>() {
            @Override
            public String parse(String data) throws IOException {
                return data;
            }
        });

        typedConfig.addListener(new Configuration.ConfigListener<String>() {
            @Override
            public void onLoad(String content) {
                Cat.logEvent("TitanServerPluginTest.AddListener", content);
                log.info("TitanServerPluginTest.AddListener");
            }
        });
    }
}
