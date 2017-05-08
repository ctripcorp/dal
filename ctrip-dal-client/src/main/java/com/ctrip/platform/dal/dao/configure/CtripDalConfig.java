package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.exceptions.DalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.client.TypedConfig;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static oracle.net.aso.C01.e;

public class CtripDalConfig implements DalConfigLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(CtripDalConfig.class);
    private static final String DAL_CONFIG = "dal.config";
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    @Override
    public DalConfigure load() throws Exception {
        DalConfigure configure = null;
        try {
            URL url = DalConfigureFactory.getDalConfigUrl();
            if (url != null) {
                configure = DalConfigureFactory.load(url);
                LOGGER.info("从本地读取dal.config");
            } else {
                configure = getConfigure();
                LOGGER.info("从QConfig读取dal.config");
            }
        } catch (Throwable e) {
            throw new DalException(e.getMessage());
        }

        return configure;
    }

    private DalConfigure getConfigure() throws Exception {
        DalConfigure configure = null;
        try {
            TypedConfig<String> config = TypedConfig.get(DAL_CONFIG, new TypedConfig.Parser<String>() {
                public String parse(String s) {
                    return s;
                }
            });

            String content = config.current();
            if (content != null && content.length() > 0) {
                InputStream stream = new ByteArrayInputStream(content.getBytes(CHARSET));
                configure = DalConfigureFactory.load(stream);
            }
        } catch (Throwable e) {
            String msg = "从QConfig读取dal.config配置时发生异常:";
            LOGGER.error(msg + e.getMessage(), e);
            throw new DalException(msg + e.getMessage());
        }
        return configure;
    }

}
