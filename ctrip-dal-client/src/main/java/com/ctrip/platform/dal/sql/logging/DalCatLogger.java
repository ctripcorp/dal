package com.ctrip.platform.dal.sql.logging;

import org.apache.commons.lang3.StringUtils;

import com.dianping.cat.Cat;
import com.dianping.cat.CatConstants;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;

public class DalCatLogger {
	private static final String RECORD_COUNT = "DAL.recordCount";
	
	public static void start(CtripLogEntry entry) {
		try {
			String sqlType = entry.getDao() + "." + entry.getMethod();
			Transaction catTransaction = Cat.newTransaction(CatConstants.TYPE_SQL, sqlType);
			entry.setCatTransaction(catTransaction);
			
			String method = entry.getEvent() == null ? "dal_test" : CatInfo.getTypeSQLInfo(entry.getEvent());
			if(entry.getPramemters() != null){
				Cat.logEvent(CatConstants.TYPE_SQL_METHOD, method, Message.SUCCESS, entry.getEncryptParameters(DalCLogger.isEncryptLogging(), entry).replaceAll(",", "&"));
			} else {
				Cat.logEvent(CatConstants.TYPE_SQL_METHOD, method, Message.SUCCESS, "");
			}
			
			catTransaction.addData(entry.getSqls() == null ? "" : StringUtils.join(entry.getSqls(), ";"));
			if (entry.getCallString() != null && entry.getCallString().length() > 0)
				catTransaction.addData(entry.getCallString());
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static void catTransactionSuccess(CtripLogEntry entry){
		try {
			Cat.logEvent(CatConstants.TYPE_SQL_DATABASE, entry.getDbUrl());
			Cat.logSizeEvent(RECORD_COUNT, entry.getResultCount());
			entry.getCatTransaction().setStatus(Transaction.SUCCESS);
			entry.getCatTransaction().complete();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static void catTransactionFailed(CtripLogEntry entry, Throwable e){
		try {
			Cat.logEvent(CatConstants.TYPE_SQL_DATABASE, entry.getDbUrl());
			Cat.logSizeEvent(RECORD_COUNT, entry.getResultCount());
			entry.getCatTransaction().setStatus(e);
			Cat.logError(e);
			entry.getCatTransaction().complete();
		} catch (Throwable e1) {
			e1.printStackTrace();
		}
	}
	
}
