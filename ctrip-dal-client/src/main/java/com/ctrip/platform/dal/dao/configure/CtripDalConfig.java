package com.ctrip.platform.dal.dao.configure;

import com.ctrip.framework.foundation.Foundation;
import com.ctrip.platform.dal.exceptions.DalException;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.dianping.cat.status.ProductVersionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.client.TypedConfig;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static oracle.net.aso.C01.e;
import static org.bouncycastle.asn1.x500.style.RFC4519Style.name;

public class CtripDalConfig implements DalConfigLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(CtripDalConfig.class);
    private static final String DAL_CONFIG = "dal.config";
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final String DAL_CONFIG_LOG = "DAL";
    private static final String DAL_CONFIG_LOAD = "dal.config::";
    private static final String DAL_CONFIG_LOCAL = "readLocal";
    private static final String DAL_CONFIG_REMOTE = "readRemote";
    private static final String UNKNOWN = "unknown";
    private static final String DAL_REMOTE_CONFIG = "DAL.remote.config";

    @Override
    public DalConfigure load() throws Exception {
        URL url = DalConfigureFactory.getDalConfigUrl();
        String location = url == null ? DAL_CONFIG_REMOTE : DAL_CONFIG_LOCAL;
        location = DAL_CONFIG_LOAD + location;
        DalConfigure configure = null;
        try {
            String log = null;
            if (url != null) {
                configure = DalConfigureFactory.load(url);
                log = "从本地读取dal.config";
            } else {
                String id = Foundation.app().getAppId();
                String appId = (id == null || id.length() == 0) ? UNKNOWN : id;
                ProductVersionManager.getInstance().register(DAL_REMOTE_CONFIG, appId);
                configure = getConfigure();
                log = "从QConfig读取dal.config";
            }

            Cat.logEvent(DAL_CONFIG_LOG, location, Message.SUCCESS, log);
            LOGGER.info(log);
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
            Cat.logError(e.getMessage(), e);
            throw new DalException(e.getMessage(), e);
        }
        return configure;
    }

    private DalConfigure getConfigure() throws Exception {
        DalConfigure configure = null;
        try {
            String content = getRemoteConfig();
            if (content != null && content.length() > 0) {
                InputStream stream = new ByteArrayInputStream(content.getBytes(CHARSET));
                configure = DalConfigureFactory.load(stream);
            }
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
            Cat.logError(e.getMessage(), e);
            throw new DalException(e.getMessage(), e);
        }
        return configure;
    }

    private String getRemoteConfig() throws Exception {
        Transaction transaction = Cat.newTransaction(DAL_CONFIG_LOG, DAL_CONFIG_LOAD + DAL_CONFIG_REMOTE);
        String content = null;
        try {
            TypedConfig<String> config = TypedConfig.get(DAL_CONFIG, new TypedConfig.Parser<String>() {
                public String parse(String s) {
                    return s;
                }
            });

            content = config.current();
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Throwable e) {
            transaction.setStatus(e);
            String msg = "从QConfig读取dal.config配置时发生异常:" + e.getMessage();
            LOGGER.error(msg, e);
            Cat.logError(msg, e);
            throw new DalException(msg, e);
        } finally {
            transaction.complete();
        }
        return content;
    }

}
