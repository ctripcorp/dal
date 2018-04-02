package DRTestOnMysql; /**
 * Created by lilj on 2017/12/4.
 */

import com.google.common.base.Optional;
import qunar.tc.qconfig.client.UploadResult;
import qunar.tc.qconfig.client.Uploader;
import qunar.tc.qconfig.client.impl.ConfigUploader;
import qunar.tc.qconfig.client.impl.Snapshot;
import qunar.tc.qconfig.client.impl.VersionProfile;

import java.io.StringReader;
import java.util.Map;
import java.util.Properties;

/**
     * properties配置使用Properties load，再上传修改会导致注释的内容消失；
     *
     */
    public class TestUpload {
        public static void main(String[] args) throws Exception {
            Uploader uploader = ConfigUploader.getInstance();//获取uploader实例
            Optional<Snapshot<String>> result1 =  uploader.getCurrent("test.properties");//拿到当前最新版本
            if (result1.isPresent()) {
                String oldContent = result1 .get().getContent();//如果返回数据是properties文件
                Properties properties = new Properties();
                if (oldContent!=null && oldContent.length()>0) {
                    properties.load(new StringReader(oldContent ));
                }
                properties.put("connectionProperties", "rewriteBatchedStatements=false;");//更新配置
                UploadResult result2 = uploader.uploadAtVersion(result1.get().getVersion(), "test.properties", parseProperties2String(properties), true);//如果已经存在，上传最新文件，并且设置为public文件
                System.out.println(result2);
            } else {
                UploadResult result2 = uploader.uploadAtVersion(VersionProfile.ABSENT, "test.properties", "connectionProperties=rewriteBatchedStatements=true;");//如果不存在，上传最新文件
                System.out.println(result2);
            }
            System.exit(-1);
        }

        private static String parseProperties2String(Properties properties) {
            if (properties == null) {
                return "";
            }
            StringBuilder result = new StringBuilder();
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                result.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
            }
            return result.toString();
        }
    }

