package com.ctrip.framework.dal.dbconfig.plugin.handler.titan;


import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfig;
import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfigManager;
import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.exception.DbConfigPluginException;
import com.ctrip.framework.dal.dbconfig.plugin.handler.BaseAdminHandler;
import com.ctrip.framework.dal.dbconfig.plugin.util.CommonHelper;
import com.ctrip.framework.dal.dbconfig.plugin.util.PermissionCheckUtil;
import com.ctrip.framework.dal.dbconfig.plugin.util.QconfigServiceUtils;
import com.ctrip.framework.dal.dbconfig.plugin.util.TitanUtils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.plugin.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * This handler is to delete whiteList from 'permission' field for titan key.
 * <p>
 * Sample:
 * [POST]   http://qconfig.fat16.qa.nt.ctripcorp.com/plugins/titan/whitelist/delete?
 * &env=pro
 * &env=pro,uat,fat
 * <p>
 * [body] sample as bellow:
 * titanKey=siccdb_w,siccdb_r&clientAppId=100001681,100001680
 * <p>
 * <p>
 * 说明:
 * env: 可以没有(用默认值), 可以是单个或多个(以逗号分隔)
 * titanKey: 可以是单个或多个(以逗号分隔)
 * clientAppId: 可以是单个或多个(以逗号分隔)
 * <p>
 * Created by lzyan on 2018/09/26, 2018/12/27.
 */
public class WhiteListDeleteHandler extends BaseAdminHandler implements TitanConstants {
    private static Logger logger = LoggerFactory.getLogger(WhiteListDeleteHandler.class);

    public WhiteListDeleteHandler(QconfigService qconfigService, PluginConfigManager pluginConfigManager) {
        super(qconfigService,pluginConfigManager);
    }

    @Override
    public String getUri() {
        return "/plugins/titan/whitelist/delete";
    }

    @Override
    public String getMethod() {
        return "POST";
    }

    @Override
    public PluginResult preHandle(HttpServletRequest request) {
        String env = request.getParameter(REQ_PARAM_ENV);
        if (Strings.isNullOrEmpty(env)) {    //默认: 所有环境
            env = "PRO,UAT,FAT";    //CommonConstant.ENV_PRO
        }
        String titanKey = request.getParameter("titanKey");

        //format <titankey>
        titanKey = TitanUtils.formatTitanFileName(titanKey);
        request.setAttribute(REQ_ATTR_TITAN_KEY, titanKey);

        //format <envList>
        Splitter splitter = Splitter.on(",").omitEmptyStrings().trimResults();
        List<String> envList = splitter.splitToList(env);
        request.setAttribute(REQ_ATTR_TITAN_ENV_LIST, envList);

        //format <clientAppId>
        String clientAppId = request.getParameter("clientAppId");
        request.setAttribute(REQ_ATTR_TITAN_CLIENT_APPID, clientAppId);

        return PluginResult.oK();
    }

    @Override
    public PluginResult postHandle(HttpServletRequest request) {
        PluginResult pluginResult = PluginResult.oK();
        Stopwatch stopwatch = Stopwatch.createStarted();
        Transaction t = Cat.newTransaction("TitanQconfigPlugin", "WhiteListDeleteHandler");
        try {
            t.addData("running class=" + getClass().getSimpleName());

            //检查参数
            paramCheck(request, t);

            //AdminSite白名单检查
            List<String> envList = (List<String>) request.getAttribute(REQ_ATTR_TITAN_ENV_LIST);
            String highEnv = CommonHelper.getHighEnv(envList);
            String profile = CommonHelper.formatProfileFromEnv(highEnv, "");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(profile), "profile参数不能为空");
            PluginConfig config = getPluginConfigManager().getPluginConfig(new EnvProfile(highEnv));
            String adminSiteWhiteIps = config.getParamValue(TITAN_ADMIN_SERVER_LIST);
            String clientIp = (String) request.getAttribute(PluginConstant.REMOTE_IP);
            boolean sitePermission = PermissionCheckUtil.checkSitePermission(adminSiteWhiteIps, clientIp);
            if (sitePermission) {
                Splitter splitter = Splitter.on(",").omitEmptyStrings().trimResults();
                // get available pro subEnv from config item 'permission.pro.subenv.list'
                String proSubEnvs = config.getParamValue(PERMISSION_PRO_SUBENV_LIST);
                List<String> subEnvList = Lists.newArrayList(splitter.splitToList(proSubEnvs));
                subEnvList.add(""); // parent env

                // update for each key in titanKeys
                List<ConfigDetail> updateCdList = Lists.newArrayList();
                String titanKeys = (String) request.getAttribute(REQ_ATTR_TITAN_KEY);
                List<String> titanKeyList = splitter.splitToList(titanKeys);
                for (String titanKey : titanKeyList) {
                    //获取欲更新TitanKey ('permissions'字段)
                    List<ConfigDetail> cdList = buildCdListPerKey(request, titanKey, subEnvList);
                    if (cdList != null && !cdList.isEmpty()) {
                        updateCdList.addAll(cdList);
                    }
                }

                // get final update titanKey
                String debugInfo = buildDebugInfo(updateCdList);
                t.addData(debugInfo);
                Cat.logEvent("WhiteListDeleteHandler", "SUCCESS", Event.SUCCESS, debugInfo);

                // batch save
                if (updateCdList != null && !updateCdList.isEmpty()) {
                    QconfigServiceUtils.batchSave(getQconfigService(), "WhiteListDeleteHandler", updateCdList, true);
                }
            } else {
                t.addData("postHandleDetail(): sitePermission=false, not allow to add!");
                Cat.logEvent("WhiteListDeleteHandler", "NO_PERMISSION", Event.SUCCESS, "sitePermission=false, not allow to add! clientIp=" + clientIp);
                pluginResult = new PluginResult(PluginStatusCode.TITAN_KEY_CANNOT_WRITE, "Access ip whitelist check fail! clientIp=" + clientIp);
            }

            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            throw new DbConfigPluginException(e);
        } finally {
            t.complete();
            stopwatch.stop();
            long cost = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        }
        return pluginResult;
    }

    //parameter check
    private void paramCheck(HttpServletRequest request, Transaction t) {
        String titanKey = (String) request.getAttribute(REQ_ATTR_TITAN_KEY);
        List<String> envList = (List<String>) request.getAttribute(REQ_ATTR_TITAN_ENV_LIST);
        String clientAppId = (String) request.getAttribute(REQ_ATTR_TITAN_CLIENT_APPID);
        t.addData("titanKey=" + titanKey);
        t.addData("envList=" + envList);
        t.addData("clientAppId=" + clientAppId);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(titanKey), "titanKey参数不能为空");
        Preconditions.checkArgument(envList != null && !envList.isEmpty(), "envList参数不能为空");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(clientAppId), "clientAppId参数不能为空");
    }

    // build update configDetail list per key ==> (key1, ""), (key1, "FRA-AWS"), (key1_SH, ""), (key1_SH, "FRA-AWS")
    private List<ConfigDetail> buildCdListPerKey(HttpServletRequest request, String titanKey, List<String> subEnvList) throws Exception {
        List<ConfigDetail> cdList = Lists.newArrayList();
        List<String> envList = (List<String>) request.getAttribute(REQ_ATTR_TITAN_ENV_LIST);
        for (String env : envList) {
            List<String> suffixList = Lists.newArrayList();
            suffixList.add(""); //current key, xxx
            if (ENV_PRO.equalsIgnoreCase(env)) {
                suffixList.add("_SH");  //xxx_SH
            }
            String finalKeyName = null;
            String profile = null;
            ConfigDetail cd = null;
            for (String suffix : suffixList) {
                finalKeyName = titanKey + suffix;
                for (String subEnv : subEnvList) {
                    profile = CommonHelper.formatProfileFromEnv(env, subEnv);
                    cd = buildUpdateConfigDetail(request, finalKeyName, profile);
                    if (cd != null) {
                        cdList.add(cd);
                    }
                }
            }
        }
        return cdList;
    }


    // build update configDetail (append clientAppId to 'permissions' of titanKey)
    private ConfigDetail buildUpdateConfigDetail(HttpServletRequest request, String dataId, String profile) throws Exception {
        ConfigDetail cd = null;
        String group = TITAN_QCONFIG_KEYS_APPID;
        String clientAppId = (String) request.getAttribute(REQ_ATTR_TITAN_CLIENT_APPID);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(dataId), "dataId参数不能为空");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(profile), "profile参数不能为空");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(clientAppId), "clientAppId参数不能为空");

        //get current config from qconfig
        ConfigField cm = new ConfigField(group, dataId, profile);
        List<ConfigField> configFieldList = Lists.newArrayList(cm);
        List<ConfigDetail> configDetailList = QconfigServiceUtils.currentConfigWithoutPriority(getQconfigService(), "WhiteListDeleteHandler", configFieldList);
        if (configDetailList != null && !configDetailList.isEmpty()) {
            //已经存在，更新
            ConfigDetail configDetail = configDetailList.get(0);    //get first
            String encryptOldConf = configDetail.getContent();
            Properties prop = CommonHelper.parseString2Properties(encryptOldConf);
            // 从 permissions 中删除 clientAppId
            String permissionsOld = prop.getProperty(PERMISSIONS);
            if (permissionsOld == null) {
                permissionsOld = "";
            }
            Splitter splitter = Splitter.on(",").omitEmptyStrings().trimResults();
            List<String> clientAppIdList = splitter.splitToList(clientAppId);
            String permissionsNew = CommonHelper.removeWithComma(permissionsOld, clientAppIdList);
            prop.put(PERMISSIONS, permissionsNew);
            boolean permissionIdempotent = CommonHelper.idempotentCheck(permissionsNew, permissionsOld);

            // 如果 permissionIdempotent 这个幂等为true, 则返回null(不save)
            if (permissionIdempotent) {
                //keep cd=null and log
                String env = CommonHelper.formatEnvFromProfile(profile);
                String subEnv = CommonHelper.getSubEnvFromProfile(profile);
                String eventName = String.format("%s:%s:%s", dataId, env, subEnv);
                Cat.logEvent("TitanPlugin.WhiteListDelete.Idempotent.Ignore.Save", eventName, Event.SUCCESS, clientAppId);

                StringBuilder sb = new StringBuilder("buildUpdateConfigDetail(): idempotent=true, not save! ");
                sb.append("dataId=").append(dataId).append(", ");
                sb.append("profile=").append(profile).append(", ");
                sb.append("clientAppId=").append(clientAppId);
                logger.info(sb.toString());
            } else {
                String text = CommonHelper.parseProperties2String(prop);
                configDetail.setVersion(-2);    //修改
                configDetail.setContent(text);
                cd = configDetail;
            }

        } else {
            //此(dataId, profile)不存在, 忽略
        }
        return cd;
    }

    // build debug info
    private String buildDebugInfo(List<ConfigDetail> updateCdList) {
        StringBuilder sb = new StringBuilder("updateCdList=[");
        if (updateCdList != null) {
            boolean isFirst = true;
            for (ConfigDetail cd : updateCdList) {
                ConfigField cf = cd.getConfigField();
                String dataId = cf.getDataId();
                String pf = cf.getProfile();
                if (isFirst) {
                    isFirst = false;
                } else {
                    sb.append(", ");
                }
                sb.append("(").append(dataId).append(",").append(pf).append(")");
            }
        }
        sb.append("]");
        return sb.toString();
    }


}
