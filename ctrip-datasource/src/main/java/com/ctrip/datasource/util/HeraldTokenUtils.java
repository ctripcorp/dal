package com.ctrip.datasource.util;

import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.platform.dal.dao.log.DalLogTypes;
import com.ctrip.sysdev.herald.tokenlib.Token;
import com.ctrip.sysdev.herald.tokenlib.exception.HeraldTokenException;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.client.plugin.HeaderPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author c7ch23en
 */
public class HeraldTokenUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeraldTokenUtils.class);

    public static final String HERALD_TOKEN_HEADER = "client-herald-token";

    private static final AtomicReference<TokenValue> tokenRef = new AtomicReference<>();

    public static void registerHeraldToken(String groupId, String configId) {
        try {
            String heraldToken = tryGetHeraldToken();
            if (!StringUtils.isEmpty(heraldToken)) {
                Map<String, String> headers = new HashMap<>();
                headers.put(HERALD_TOKEN_HEADER, heraldToken);
                HeaderPlugin.prepareHeaders(groupId, configId, headers);
                String msg = String.format("groupId=%s, configId=%s", groupId, configId);
                LOGGER.info("Herald token registered: " + msg);
                Cat.logEvent(DalLogTypes.DAL_VALIDATION, "HeraldTokenRegistered", Event.SUCCESS, msg);
            }
        } catch (Throwable t) {
            LOGGER.warn("registerHeraldToken error", t);
        }
    }

    public static String tryGetHeraldToken() {
        TokenValue value = tokenRef.get();
        if (value == null) {
            synchronized (tokenRef) {
                value = tokenRef.get();
                if (value == null) {
                    value = new TokenValue(pTryGetHeraldToken());
                    tokenRef.set(value);
                }
            }
        }
        return value.get();
    }

    private static String pTryGetHeraldToken() {
        String appId = Foundation.app().getAppId();
        try {
            String token = Token.getTokenStringByAppID(appId);
            if (!StringUtils.isEmpty(token)) {
                LOGGER.info("Herald token obtained: " + token);
                Cat.logEvent(DalLogTypes.DAL_VALIDATION, "HeraldTokenObtained", Event.SUCCESS, token);
            }
            return token;
        } catch (HeraldTokenException e) {
            Cat.logEvent(DalLogTypes.DAL_VALIDATION, "HeraldTokenNotFound");
            LOGGER.info("HeraldTokenNotFound, appId=" + appId);
            return null;
        }
    }

    static class TokenValue {
        String value;

        TokenValue(String value) {
            this.value = value;
        }

        String get() {
            return value;
        }
    }

}
