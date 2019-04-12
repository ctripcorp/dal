package com.ctrip.framework.dal.dbconfig.plugin.handler.titan;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfig;
import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.entity.FreeVerifyInputEntity;
import com.ctrip.framework.dal.dbconfig.plugin.exception.DbConfigPluginException;
import com.ctrip.framework.dal.dbconfig.plugin.handler.BaseAdminHandler;
import com.ctrip.framework.dal.dbconfig.plugin.util.CommonHelper;
import com.ctrip.framework.dal.dbconfig.plugin.util.GsonUtils;
import com.ctrip.framework.dal.dbconfig.plugin.util.QconfigServiceUtils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import qunar.tc.qconfig.plugin.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Properties;

/**
 * This handler is to delete free verify info(appId/ip) to field of titan key.
 * [freeVerifyIpList, freeVerifyAppIdList]
 * <p>
 * Sample:
 * [POST]   http://qconfig.fat16.qa.nt.ctripcorp.com/plugins/titan/freeverify/delete?&env=fat&subenv=
 * <p>
 * [body] sample as bellow:
 * {
 * "titanKeyList":"siccdb_w,siccdb_r",
 * "freeVerifyIpList":"10.1.1.1,10.1.1.2",
 * "freeVerifyAppIdList":"100001680,100001681"
 * }
 * <p>
 * <p>
 * Created by lzyan on 2018/10/10.
 */
public class FreeVerifyDeleteHandler extends BaseAdminHandler implements TitanConstants {

    public FreeVerifyDeleteHandler(QconfigService qconfigService) {
        super(qconfigService);
    }

    @Override
    public String getUri() {
        return "/plugins/titan/freeverify/delete";
    }

    @Override
    public String getMethod() {
        return "POST";
    }

    @Override
    public PluginResult preHandle(HttpServletRequest request) {
        String env = request.getParameter(REQ_PARAM_ENV);
        String subEnv = request.getParameter(REQ_PARAM_SUB_ENV);

        //format <profile>
        EnvProfile profile = new EnvProfile(env, subEnv);
        request.setAttribute(REQ_ATTR_ENV_PROFILE, profile);

        return PluginResult.oK();
    }

    @Override
    public PluginResult postHandle(HttpServletRequest request) {
        PluginResult pluginResult = PluginResult.oK();
        Transaction t = Cat.newTransaction("TitanQconfigPlugin", "FreeVerifyDeleteHandler");
        try {
            t.addData("running class=" + getClass().getSimpleName());

            String body = CommonHelper.getBody(request, true);
            /**
             * Sample body
             {
             "titanKeyList":"siccdb_w,siccdb_r",
             "freeVerifyIpList":"10.1.1.1,10.1.1.2",
             "freeVerifyAppIdList":"100001680,100001681"
             }
             */
            if (body != null) {
                FreeVerifyInputEntity freeVerifyInputEntity = GsonUtils.json2T(body, FreeVerifyInputEntity.class);
                if (freeVerifyInputEntity != null) {

                    //检查参数
                    paramCheck(request, freeVerifyInputEntity, t);

                    //AdminSite白名单检查
                    EnvProfile profile = (EnvProfile) request.getAttribute(REQ_ATTR_ENV_PROFILE);
                    Preconditions.checkArgument(profile != null && profile.formatProfile() != null,
                            "profile参数不能为空");
                    PluginConfig config = new PluginConfig(getQconfigService(), profile);
                    String clientIp = (String) request.getAttribute(PluginConstant.REMOTE_IP);
                    boolean sitePermission = checkPermission(clientIp, profile);
                    if (sitePermission) {
                        Splitter splitter = Splitter.on(",").omitEmptyStrings().trimResults();
                        // get available pro subEnv from config item 'permission.pro.subenv.list'
                        String proSubEnvs = config.getParamValue(PERMISSION_PRO_SUBENV_LIST);
                        List<String> subEnvList = Lists.newArrayList(splitter.splitToList(proSubEnvs));
                        subEnvList.add(""); // parent env

                        // update for each key in titanKeyList
                        List<ConfigDetail> updateCdList = Lists.newArrayList();
                        String titanKeyListStr = freeVerifyInputEntity.getTitanKeyList();
                        List<String> titanKeyList = splitter.splitToList(titanKeyListStr);
                        for (String titanKey : titanKeyList) {
                            //process key, if end with "_SH", remove it. eg: abc_SH -> abc
                            titanKey = CommonHelper.trimSH(titanKey);
                            //获取欲更新TitanKey ('freeVerifyIpList', 'freeVerifyAppIdList'字段)
                            List<ConfigDetail> cdList = buildCdListPerKey(request, freeVerifyInputEntity, titanKey, subEnvList);
                            if (cdList != null && !cdList.isEmpty()) {
                                updateCdList.addAll(cdList);
                            }
                        }

                        // get final update titanKey
                        String debugInfo = buildDebugInfo(updateCdList);
                        t.addData(debugInfo);
                        Cat.logEvent("FreeVerifyDeleteHandler", "SUCCESS", Event.SUCCESS, debugInfo);

                        // batch save
                        QconfigServiceUtils.batchSave(getQconfigService(), "FreeVerifyDeleteHandler", updateCdList, true);
                    } else {
                        t.addData("postHandleDetail(): sitePermission=false, not allow to delete!");
                        Cat.logEvent("FreeVerifyDeleteHandler", "NO_PERMISSION", Event.SUCCESS, "sitePermission=false, not allow to delete! clientIp=" + clientIp);
                        pluginResult = new PluginResult(PluginStatusCode.TITAN_KEY_CANNOT_WRITE, "Access ip whitelist check fail! clientIp=" + clientIp);
                    }

                } else {
                    t.addData("postHandleDetail(): freeVerifyInputEntity=null, no need to delete!");
                }
            } else {
                t.addData("postHandleDetail(): freeVerifyInputEntity body is null or empty, no need to delete!");
            }

            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            throw new DbConfigPluginException(e);
        } finally {
            t.complete();
        }
        return pluginResult;
    }

    //parameter check
    private void paramCheck(HttpServletRequest request, FreeVerifyInputEntity freeVerifyInputEntity, Transaction t) {
        EnvProfile profile = (EnvProfile) request.getAttribute(REQ_ATTR_ENV_PROFILE);
        String titanKeyList = freeVerifyInputEntity.getTitanKeyList();
        String freeVerifyIpList = freeVerifyInputEntity.getFreeVerifyIpList();
        String freeVerifyAppIdList = freeVerifyInputEntity.getFreeVerifyAppIdList();

        Preconditions.checkArgument(profile != null && profile.formatProfile() != null,
                "profile参数不能为空");

        t.addData("profile=" + profile.formatProfile());
        t.addData("titanKeyList=" + titanKeyList);
        t.addData("freeVerifyIpList=" + freeVerifyIpList);
        t.addData("freeVerifyAppIdList=" + freeVerifyAppIdList);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(titanKeyList), "titanKeyList参数不能为空");
        Preconditions.checkArgument((!Strings.isNullOrEmpty(freeVerifyIpList) || !Strings.isNullOrEmpty(freeVerifyAppIdList)), "[freeVerifyIpList, freeVerifyAppIdList]参数不能同时为空");
    }


    // build update configDetail list per key ==> (key1, ""), (key1, "FRA-AWS"), (key1_SH, ""), (key1_SH, "FRA-AWS")
    private List<ConfigDetail> buildCdListPerKey(HttpServletRequest request, FreeVerifyInputEntity freeVerifyInputEntity, String titanKey, List<String> subEnvList) throws Exception {
        List<ConfigDetail> cdList = Lists.newArrayList();
        List<String> suffixList = Lists.newArrayList();
        suffixList.add(""); //current key, xxx
        EnvProfile initProfile = (EnvProfile) request.getAttribute(REQ_ATTR_ENV_PROFILE);
        String env = initProfile.formatEnv();
        boolean isPro = CommonHelper.checkPro(env);
        if (isPro) {
            suffixList.add("_SH");  //xxx_SH
        }
        String finalKeyName = null;
        String profile = null;
        ConfigDetail cd = null;
        for (String suffix : suffixList) {
            finalKeyName = titanKey + suffix;
            for (String subEnv : subEnvList) {
                // old
//                profile = initProfile.formatProfile();
                EnvProfile envProfile = new EnvProfile(env, subEnv);
                profile = envProfile.formatProfile();
                cd = buildUpdateConfigDetail(finalKeyName, profile, freeVerifyInputEntity);
                if (cd != null) {
                    cdList.add(cd);
                }
            }
        }
        return cdList;
    }

    // build update configDetail (append input to field of titanKey)
    private ConfigDetail buildUpdateConfigDetail(String dataId, String profile, FreeVerifyInputEntity freeVerifyInputEntity) throws Exception {
        ConfigDetail cd = null;
        String group = TITAN_QCONFIG_KEYS_APPID;
        String freeVerifyIpList = freeVerifyInputEntity.getFreeVerifyIpList();
        String freeVerifyAppIdList = freeVerifyInputEntity.getFreeVerifyAppIdList();
        Preconditions.checkArgument(!Strings.isNullOrEmpty(dataId), "dataId参数不能为空");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(profile), "profile参数不能为空");
        Preconditions.checkArgument((!Strings.isNullOrEmpty(freeVerifyIpList) || !Strings.isNullOrEmpty(freeVerifyAppIdList)), "[freeVerifyIpList, freeVerifyAppIdList]参数不能同时为空");

        //get current config from qconfig
        ConfigField cm = new ConfigField(group, dataId, profile);
        List<ConfigField> configFieldList = Lists.newArrayList(cm);
        List<ConfigDetail> configDetailList = QconfigServiceUtils.currentConfigWithoutPriority(getQconfigService(), "FreeVerifyDeleteHandler", configFieldList);
        if (configDetailList != null && !configDetailList.isEmpty()) {
            //已经存在，更新
            ConfigDetail configDetail = configDetailList.get(0);    //get first
            String encryptOldConf = configDetail.getContent();
            Properties prop = CommonHelper.parseString2Properties(encryptOldConf);
            // 合并 freeVerifyIpList, freeVerifyAppIdList
            mergeFreeVerifyField(prop, FREE_VERIFY_IPLIST, freeVerifyIpList);
            mergeFreeVerifyField(prop, FREE_VERIFY_APPID_LIST, freeVerifyAppIdList);


            //field 'version' increase one
            //HelpUtil.increaseVersionInProperties(prop);

            String text = CommonHelper.parseProperties2String(prop);
            configDetail.setVersion(-2);    //修改
            configDetail.setContent(text);
            cd = configDetail;
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

    // merge
    private void mergeFreeVerifyField(Properties prop, String fieldName, String inputDel) {
        if (!Strings.isNullOrEmpty(inputDel)) {
            String oldValue = prop.getProperty(fieldName);
            if (oldValue == null) {
                oldValue = "";
            }
            Splitter splitter = Splitter.on(",").omitEmptyStrings().trimResults();
            List<String> itemList = splitter.splitToList(inputDel);
            String newValue = CommonHelper.removeWithComma(oldValue, itemList);
            prop.put(fieldName, newValue);
        }
    }


}
