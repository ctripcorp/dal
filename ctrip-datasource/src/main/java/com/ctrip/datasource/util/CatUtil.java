package com.ctrip.datasource.util;

import com.dianping.cat.message.Transaction;
import com.dianping.cat.message.internal.DefaultTransaction;

public class CatUtil {
    public static void completeTransaction(Transaction tx, long startTime) {
        if (tx != null) {
            if (tx instanceof DefaultTransaction) {
                ((DefaultTransaction) tx).setTimestamp(startTime);
                ((DefaultTransaction) tx)
                        .setDurationStart(System.nanoTime() - (System.currentTimeMillis() - startTime) * 1000000L);
            }
            tx.complete();
        }
    }

}
