package com.ctrip.framework.dal.dbconfig.plugin.service.validator;

import com.ctrip.framework.dal.dbconfig.plugin.entity.AppIdIpCheckEntity;
import com.ctrip.framework.dal.dbconfig.plugin.entity.PermissionCheckEnum;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;

import java.util.List;
import java.util.Properties;

import static com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants.*;

/**
 * Step5: AppId-IP校验服务验证
 * [1] 调用第三方接口验证ip是否属于此appId
 * <p>
 * Created by lzyan on 2018/9/27.
 */
public class Step5AppIdIpCheckServiceValidator extends AbstractValidator {
    private Properties pluginProp;
    private Properties keyProp;
    private String clientAppId;
    private String clientIp;
    private String env;

    public Step5AppIdIpCheckServiceValidator(Properties pluginProp, Properties keyProp, String clientAppId, String clientIp, String env) {
        this.pluginProp = pluginProp;
        this.keyProp = keyProp;
        this.clientAppId = clientAppId;
        this.clientIp = clientIp;
        this.env = env;
    }

    @Override
    public PermissionCheckEnum doValid() {
        PermissionCheckEnum result = PermissionCheckEnum.PASS;
        AbstractValidator nextValidator = getNextValidator();

        // appId-Ip match check. First get from cache [2018-10-24]
        AppIdIpCheckEntity appIdIpCheckEntity = buildAppIdIpCheckEntity();
        boolean checkPass = AppIdIpCheckCache.getInstance().isAppIdIpMatch(appIdIpCheckEntity);
        if (!checkPass) {
            result = PermissionCheckEnum.FAIL_APPID_IP_CHECK;
        }

        if (result != PermissionCheckEnum.PASS) {
            String keyName = keyProp.getProperty(CONNECTIONSTRING_KEY_NAME);
            String eventName = String.format("%s:%s:%s", keyName, clientAppId, clientIp);
            String msg = "permissionCheckEnum=" + result;
            Cat.logEvent("TitanPlugin.Permission.Valid.Fail.AppIdIpCheck", eventName, Event.SUCCESS, msg);

            String en = String.format("%s:%s", keyName, clientAppId);
            String enMsg = "permissionCheckEnum=" + result + ", clientIp=" + clientIp;
            Cat.logEvent("TitanPlugin.Permission.Valid.Fail.AppIdIpCheck.Simple", en, Event.SUCCESS, enMsg);

            return result;
        } else if (nextValidator != null) {
            result = nextValidator.doValid();
        }
        return result;
    }


    private AppIdIpCheckEntity buildAppIdIpCheckEntity() {
        String appIdIpCheckServiceUrl = pluginProp.getProperty(APPID_IP_CHECK_SERVICE_URL);
        String serviceToken = pluginProp.getProperty(APPID_IP_CHECK_SERVICE_TOKEN);

        // get timeoutMs
        int timeoutMs = 1000;    //default 1s
        String httpReadTimeoutMs = pluginProp.getProperty(APPID_IP_CHECK_HTTP_READ_TIMEOUT_MS);
        if (!Strings.isNullOrEmpty(httpReadTimeoutMs)) {
            timeoutMs = Integer.parseInt(httpReadTimeoutMs);
        }

        // get passCodeList
        String passCodeListStr = pluginProp.getProperty(APPID_IP_CHECK_SERVICE_PASS_CODELIST);
        Splitter splitter = Splitter.on(",").omitEmptyStrings().trimResults();
        List<String> passCodeList = splitter.splitToList(passCodeListStr);

        AppIdIpCheckEntity appIdIpCheckEntity = new AppIdIpCheckEntity();
        appIdIpCheckEntity.setServiceUrl(appIdIpCheckServiceUrl);
        appIdIpCheckEntity.setClientAppId(clientAppId);
        appIdIpCheckEntity.setClientIp(clientIp);
        appIdIpCheckEntity.setEnv(env);
        appIdIpCheckEntity.setServiceToken(serviceToken);
        appIdIpCheckEntity.setTimeoutMs(timeoutMs);
        appIdIpCheckEntity.setPassCodeList(passCodeList);
        return appIdIpCheckEntity;
    }


}
