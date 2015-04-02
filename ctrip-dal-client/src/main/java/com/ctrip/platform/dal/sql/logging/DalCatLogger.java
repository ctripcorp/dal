package com.ctrip.platform.dal.sql.logging;

import org.apache.commons.lang3.StringUtils;

import com.ctrip.platform.dal.catlog.CatInfo;
import com.dianping.cat.Cat;
import com.dianping.cat.CatConstants;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;

public class DalCatLogger {
	public static void start(CtripLogEntry entry) {
		try {
			String sqlType = entry.getDao() + "." + entry.getMethod();
			Transaction catTransaction = Cat.newTransaction(CatConstants.TYPE_SQL, sqlType);
			entry.setCatTransaction(catTransaction);
			catTransaction.addData(entry.getSqls() == null ? "" : StringUtils.join(entry.getSqls(), ";"));
			catTransaction.addData("\n");
			if(entry.getPramemters() != null){
				catTransaction.addData(entry.getEncryptParameters(DalCLogger.isEncryptLogging()));
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static void catTransactionSuccess(CtripLogEntry entry){
		try {
			String method = entry.getEvent() == null ? "dal_test" : CatInfo.getTypeSQLInfo(entry.getEvent());
			Cat.logEvent("DAL.version", "java-" + entry.getClientVersion());
			Cat.logEvent(CatConstants.TYPE_SQL_METHOD, method, Message.SUCCESS, "");
			Cat.logEvent(CatConstants.TYPE_SQL_DATABASE, entry.getDbUrl());
			entry.getCatTransaction().setStatus(Transaction.SUCCESS);
			entry.getCatTransaction().complete();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static void catTransactionFailed(CtripLogEntry entry, Throwable e){
		try {
			entry.getCatTransaction().setStatus(e);
			Cat.logError(e);
			entry.getCatTransaction().complete();
		} catch (Throwable e1) {
			e1.printStackTrace();
		}
	}
	
}
