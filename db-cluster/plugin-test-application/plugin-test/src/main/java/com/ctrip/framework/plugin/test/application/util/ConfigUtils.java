package com.ctrip.framework.plugin.test.application.util;

import qunar.tc.qconfig.client.Feature;
import qunar.tc.qconfig.client.TypedConfig;
import qunar.tc.qconfig.client.util.AppStoreUtil;

import java.io.File;
import java.io.IOException;

/**
 * Created by shenjie on 2019/3/5.
 */
public class ConfigUtils {

    public static String getConfig(String groupName, String fileName) {
        // delete qconfig cache
//        File file = AppStoreUtil.getAppStore();
//        delete(file);

        Feature feature = Feature.create().setHttpsEnable(true).build();
        TypedConfig<String> typedConfig = TypedConfig.get(groupName, fileName, feature, new TypedConfig.Parser<String>() {
            @Override
            public String parse(String data) throws IOException {
                return data;
            }
        });
        String fileResult = typedConfig.current();

        return fileResult;
    }

    public static void delete(File file) {
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
