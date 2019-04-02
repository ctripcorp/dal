package com.ctrip.framework.dal.dbconfig.plugin.util;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.google.common.base.Strings;
import qunar.tc.qconfig.common.bean.PaginationResult;
import qunar.tc.qconfig.common.exception.QServiceException;
import qunar.tc.qconfig.plugin.ConfigDetail;
import qunar.tc.qconfig.plugin.ConfigField;
import qunar.tc.qconfig.plugin.QconfigService;

import java.util.List;

/**
 * Created by lzyan on 2019/1/14.
 */
public class QconfigServiceUtils {
    private static final String TITAN_PLUGIN_TRANSACTION_WRITE = "TitanQconfigPlugin.Qconfig.Write";
    private static final String TITAN_PLUGIN_TRANSACTION_READ = "TitanQconfigPlugin.Qconfig.Read";

    // batch save
    public static int batchSave(QconfigService qconfigService, String handlerName, List<ConfigDetail> configDetails, boolean isPublic, String operator, String ip) throws QServiceException {
        checkParameter(qconfigService, handlerName);
        int result = 0;
        if(configDetails != null && !configDetails.isEmpty()) {
            Transaction t = Cat.newTransaction(TITAN_PLUGIN_TRANSACTION_WRITE, handlerName);
            try {
                t.addData("*count", configDetails.size());
                if(!Strings.isNullOrEmpty(operator) && !Strings.isNullOrEmpty(ip)) {
                    result = qconfigService.batchSave(configDetails, isPublic, operator, ip);
                } else {
                    result = qconfigService.batchSave(configDetails, isPublic);
                }
                t.setStatus(Message.SUCCESS);
            } catch (Exception e) {
                t.setStatus(e);
                Cat.logError(e);
                throw e;
            } finally {
                t.complete();
            }
        }
        return result;
    }

    // batch save
    public static int batchSave(QconfigService qconfigService, String handlerName, List<ConfigDetail> configDetails, boolean isPublic) throws QServiceException {
        return batchSave(qconfigService, handlerName, configDetails, isPublic, null, null);
    }

    public static PaginationResult<ConfigDetail> query(QconfigService qconfigService, String handlerName, ConfigField configField, int currentPage, int pageSize) throws QServiceException {
        checkParameter(qconfigService, handlerName);
        PaginationResult<ConfigDetail> paginationResult = null;
        Transaction t = Cat.newTransaction(TITAN_PLUGIN_TRANSACTION_READ, handlerName);
        try {
            paginationResult = qconfigService.query(configField, currentPage, pageSize);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            throw e;
        } finally {
            t.complete();
        }
        return paginationResult;
    }

    public static List<ConfigDetail> currentConfigWithoutPriority(QconfigService qconfigService, String handlerName, List<ConfigField> configFields) throws QServiceException {
        checkParameter(qconfigService, handlerName);
        List<ConfigDetail> configDetailList = null;
        Transaction t = Cat.newTransaction(TITAN_PLUGIN_TRANSACTION_READ, handlerName);
        try {
            t.addData("*count", configFields.size());
            configDetailList = qconfigService.currentConfigWithoutPriority(configFields);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            throw e;
        } finally {
            t.complete();
        }
        return configDetailList;
    }

    public static List<ConfigDetail> currentConfigWithPriority(QconfigService qconfigService, String handlerName, List<ConfigField> configFields) throws QServiceException {
        checkParameter(qconfigService, handlerName);
        List<ConfigDetail> configDetailList = null;
        Transaction t = Cat.newTransaction(TITAN_PLUGIN_TRANSACTION_READ, handlerName);
        try {
            t.addData("*count", configFields.size());
            configDetailList = qconfigService.currentConfigWithPriority(configFields);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            throw e;
        } finally {
            t.complete();
        }
        return configDetailList;
    }

    public static List<ConfigDetail> getLatestConfigs(QconfigService qconfigService, String handlerName, String group, String profile, String dateTimeStr) throws QServiceException {
        checkParameter(qconfigService, handlerName);
        List<ConfigDetail> configDetailList = null;
        Transaction t = Cat.newTransaction(TITAN_PLUGIN_TRANSACTION_READ, handlerName);
        try {
            configDetailList = qconfigService.getLatestConfigs(group, profile, dateTimeStr);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            throw e;
        } finally {
            t.complete();
        }
        return configDetailList;
    }


    // check parameter
    private static void checkParameter(QconfigService qconfigService, String handlerName) {
        if(qconfigService == null) {
            throw new IllegalArgumentException("qconfigService=null, can't operate ...");
        }
        if(Strings.isNullOrEmpty(handlerName)) {
            throw new IllegalArgumentException("handlerName is null or empty, can't operate ...");
        }
    }

}
