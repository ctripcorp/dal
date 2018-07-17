package util;

import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.client.UploadResult;
import qunar.tc.qconfig.client.Uploader;
import qunar.tc.qconfig.client.impl.ConfigUploader;
import qunar.tc.qconfig.client.impl.Snapshot;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.fail;

/**
 * Created by lilj on 2018/3/4.
 */
public class PoolPropertiesSwitch {
    private static Logger log = LoggerFactory.getLogger(PoolPropertiesSwitch.class);

    public boolean uploadProperties(Map<String, String> map) throws Exception {
        Uploader uploader = ConfigUploader.getInstance();//获取uploader实例
        Optional<Snapshot<String>> result1 = uploader.getCurrent("datasource.properties");//拿到当前最新版本
        if (result1.isPresent()) {
            String oldContent = result1.get().getContent();//如果返回数据是properties文件
            Properties properties = new Properties();
            if (oldContent != null && oldContent.length() > 0) {
                properties.load(new StringReader(oldContent));
            }
            properties.clear();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                properties.put(entry.getKey(), entry.getValue());//更新配置
            }
            UploadResult result2 = uploader.uploadAtVersion(result1.get().getVersion(), "datasource.properties", parseProperties2String(properties), true);//如果已经存在，上传最新文件，并且设置为public文件
            System.out.println(result2);
            return true;
        } else {
//            UploadResult result2 = uploader.uploadAtVersion(VersionProfile.ABSENT, "datasource.properties", "connectionProperties=rewriteBatchedStatements=true;");//如果不存在，上传最新文件
//            System.out.println(result2);
            log.warn("datasource.properties不存在，请先添加");
            return false;
        }
    }


    public void testUpload(Map<String, String> map) throws Exception {
        Uploader uploader = ConfigUploader.getInstance();//获取uploader实例
        Optional<Snapshot<String>> result1 = uploader.getCurrent("test.properties");//拿到当前最新版本
        if (result1.isPresent()) {
            String oldContent = result1.get().getContent();//如果返回数据是properties文件
            Properties properties = new Properties();
            if (oldContent != null && oldContent.length() > 0) {
                properties.load(new StringReader(oldContent));
            }
            properties.clear();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                properties.put(entry.getKey(), entry.getValue());
            }
            UploadResult result2 = uploader.uploadAtVersion(result1.get().getVersion(), "test.properties", parseProperties2String(properties), true);//如果已经存在，上传最新文件，并且设置为public文件
            System.out.println(result2);
        } else {
//            UploadResult result2 = uploader.uploadAtVersion(VersionProfile.ABSENT, "datasource.properties", "connectionProperties=rewriteBatchedStatements=true;");//如果不存在，上传最新文件
//            System.out.println(result2);
            log.warn("test.properties不存在，请先添加");
        }
    }


    private String parseProperties2String(Properties properties) {
        if (properties == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            result.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
        }
        return result.toString();
    }

    public void resetPoolProperties() throws Exception {
        log.info(String.format("清空除开关外的所有属性配置"));
        //先把开关以外的配置清空，开关值为true，以使得清空生效
        Map<String, String> map = new HashMap<>();
        map.put("enableDynamicPoolProperties", "true");
        modifyPoolProperties(map);
        log.info(String.format("清空属性配置后等待35秒生效"));
        Thread.sleep(35000);

        //把开关信息删除，恢复到默认状态
        log.info(String.format("清除开关"));
        map.clear();
        modifyPoolProperties(map);
        log.info(String.format("清除开关后等待35秒"));
        Thread.sleep(35000);
    }

    public void modifyPoolProperties(Map<String, String> map) {
        try {
            boolean isSuccess = uploadProperties(map);
            if (isSuccess)
                log.info("修改文件成功");
            else {
                log.info("DatasourceProperties文件不存在");
             fail();
            }
        } catch (Exception e) {
            log.error("修改DatasourceProperties文件发生异常", e);
            fail();
        }
    }
}
