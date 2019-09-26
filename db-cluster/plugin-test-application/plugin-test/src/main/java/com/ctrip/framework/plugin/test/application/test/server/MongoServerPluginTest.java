package com.ctrip.framework.plugin.test.application.test.server;

import com.ctrip.framework.plugin.test.application.service.ConfigService;
import com.ctrip.framework.plugin.test.application.util.RC4;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Transaction;
import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qunar.tc.qconfig.client.Configuration;
import qunar.tc.qconfig.client.Feature;
import qunar.tc.qconfig.client.TypedConfig;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.ctrip.framework.plugin.test.application.util.Constants.MONGO_PLUGIN_APP_ID;

/**
 * Created by shenjie on 2019/7/3.
 */
@Slf4j
@Component
public class MongoServerPluginTest {

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    @Autowired
    private ConfigService configService;

    @PostConstruct
    private void init() throws Exception {
        try {
            addListener();
            addTask();
        } catch (Exception e) {
            Cat.logError("MongoServerPluginTest init failed", e);
            log.error("MongoServerPluginTest init failed", e);
            throw new Exception("MongoServerPluginTest init failed", e);
        }
    }

    private void addTask() {
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                String mongoClusterName = configService.getMongoClusterName();
                Transaction transaction = Cat.newTransaction("MongoServerPluginTest.GetClusterConfig", mongoClusterName);
                log.info("MongoServerPluginTest getClusterConfig begin...");
                try {
                    String fileContent = getClusterConfig();
                    Preconditions.checkArgument(StringUtils.isNotBlank(fileContent), "Mongo cluster config is null or empty.");

                    JsonParser parser = new JsonParser();
                    JsonObject jsonObj = parser.parse(fileContent).getAsJsonObject();
                    JsonElement passwordJsonElement = jsonObj.get("password");
                    if (passwordJsonElement != null) {
                        String password = passwordJsonElement.getAsString();
                        fileContent = fileContent.replace(password, RC4.encrypt(password, mongoClusterName));
                    }

                    transaction.addData("clusterConfig", fileContent);
                    Cat.logEvent("MongoServerPluginTest.ClusterConfig", mongoClusterName, Event.SUCCESS, fileContent);
                    log.info("Mongo cluster config is {}", fileContent);

                    transaction.setStatus(Transaction.SUCCESS);
                } catch (Exception e) {
                    Cat.logError("Get mongo cluster config from qconfig failed.", e);
                    log.error("Get mongo cluster config from qconfig failed.", e);
                    transaction.setStatus(e);
                } finally {
                    transaction.complete();
                    log.info("MongoServerPluginTest getClusterConfig end...");
                }
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    private String getClusterConfig() {
        Cat.logEvent("MongoServerPluginTest.HttpsEnable", String.valueOf(configService.isEnableHttps()));
        log.info("MongoServerPluginTest httpsEnable is {}.", configService.isEnableHttps());
        Feature feature = configService.isEnableHttps() ? Feature.create().setHttpsEnable(true).build() : Feature.DEFAULT;
        TypedConfig<String> typedConfig = TypedConfig.get(MONGO_PLUGIN_APP_ID, configService.getMongoClusterName(), feature, new TypedConfig.Parser<String>() {
            @Override
            public String parse(String data) throws IOException {
                return data;
            }
        });
        String configContent = typedConfig.current();

        return configContent;
    }

    private void addListener() {
        Cat.logEvent("MongoServerPluginTest.AddListener.HttpsEnable", String.valueOf(configService.isEnableHttps()));
        log.info("MongoServerPluginTest addListener httpsEnable is {}.", configService.isEnableHttps());
        Feature feature = configService.isEnableHttps() ? Feature.create().setHttpsEnable(true).build() : Feature.DEFAULT;
        TypedConfig<String> typedConfig = TypedConfig.get(MONGO_PLUGIN_APP_ID, configService.getMongoClusterName(), feature, new TypedConfig.Parser<String>() {
            @Override
            public String parse(String data) throws IOException {
                return data;
            }
        });

        typedConfig.addListener(new Configuration.ConfigListener<String>() {
            @Override
            public void onLoad(String content) {
                Cat.logEvent("MongoServerPluginTest.AddListener", content);
                log.info("MongoServerPluginTest.AddListener");
            }
        });
    }
}
