package com.ctrip.framework.dal.dbconfig.plugin.util;

import qunar.tc.qconfig.client.Feature;
import qunar.tc.qconfig.client.TypedConfig;
import qunar.tc.qconfig.client.util.AppStoreUtil;

import java.io.File;
import java.io.IOException;

/**
 * Created by shenjie on 2019/3/5.
 */
public class ConfigUtils {

    public static String getTitanFileResult(String titanKey) {
        // delete qconfig cache
        File file = AppStoreUtil.getAppStore();
        delete(file);

        Feature feature = Feature.create().setHttpsEnable(true).build();
        TypedConfig<String> typedConfig = TypedConfig.get("100010061", titanKey, feature, new TypedConfig.Parser<String>() {
            @Override
            public String parse(String data) throws IOException {
                return data;
            }
        });
        String fileResult = typedConfig.current();

        return fileResult;
    }

    public static String getMongoFileResult(String clusterName) {
        // delete qconfig cache
        File file = AppStoreUtil.getAppStore();
        delete(file);

        Feature feature = Feature.create().setHttpsEnable(true).build();
        TypedConfig<String> typedConfig = TypedConfig.get("100019648", clusterName, feature, new TypedConfig.Parser<String>() {
            @Override
            public String parse(String data) throws IOException {
                return data;
            }
        });

        String fileResult = typedConfig.current();

        return fileResult;
    }

    private static void delete(File file) {
        File[] files = file.listFiles();
        if (files != null && files.length > 0) {
            for (File file1 : files) {
                if (file1.isDirectory()) {

                    delete(file1);
                } else {
                    file1.delete();
                }
            }
        }
    }

}
