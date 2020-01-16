package com.ctrip.platform.dal.dao.helper;

import com.ctrip.platform.dal.common.enums.DalTransactionStatus;
import com.ctrip.platform.dal.dao.client.DalTransactionStatusWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DalTransactionHelper {
    private volatile static DalTransactionHelper transactionHelper = null;

    public synchronized static DalTransactionHelper getInstance() {
        if (transactionHelper == null) {
            transactionHelper = new DalTransactionHelper();
        }
        return transactionHelper;
    }

    public String getTransactionConflictMessage(Map<Integer, DalTransactionStatusWrapper> transactionStatusWrapperMap) {
        if (transactionStatusWrapperMap == null || transactionStatusWrapperMap.isEmpty())
            return String.format("The state of nesting transaction are conflicted.");

        Set<Integer> keySet = transactionStatusWrapperMap.keySet();
        List<Integer> list = new ArrayList<>(keySet);
        Collections.sort(list);

        StringBuilder sb = new StringBuilder();
        for (Integer level : list) {
            DalTransactionStatusWrapper wrapper = transactionStatusWrapperMap.get(level);
            if (wrapper != null) {
                try {
                    sb.append(String.format("[level %d, status:%s, actual status:%s] ", level,
                            wrapper.getTransactionStatus().toString(), wrapper.getActualStatus().toString()));
                } catch (Throwable e) {
                    String str = e.getMessage();
                    e.printStackTrace();
                }
            }
        }

        String levelConflictedMessage = getLevelConflictedMessage(transactionStatusWrapperMap);
        return String.format("%s, all levels of transaction status:%s.", levelConflictedMessage, sb.toString());
    }

    private String getLevelConflictedMessage(Map<Integer, DalTransactionStatusWrapper> transactionStatusMap) {
        Set<Integer> keySet = transactionStatusMap.keySet();
        List<Integer> list = new ArrayList<>(keySet);
        Collections.sort(list, Collections.<Integer>reverseOrder());
        int index = 1;

        for (Integer level : list) {
            DalTransactionStatusWrapper wrapper = transactionStatusMap.get(level);
            if (wrapper == null)
                continue;

            if (wrapper.getTransactionStatus() == DalTransactionStatus.Conflict) {
                index = level;
                break;
            }
        }

        int errorLevel = index + 1;
        String errorMessage = getActualErrorMessage(errorLevel, transactionStatusMap);
        return String.format("Transaction level %d conflicted with level %d, original error message:[%s] ", index,
                errorLevel, errorMessage);
    }

    private String getActualErrorMessage(int level, Map<Integer, DalTransactionStatusWrapper> map) {
        if (map == null || map.isEmpty())
            return "";

        DalTransactionStatusWrapper wrapper = map.get(level);
        if (wrapper == null)
            return "";

        String errorMessage = wrapper.getErrorMessage();
        return errorMessage == null ? "" : errorMessage;
    }

}