package com.dal.sqlserver.test.control;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.status.DalStatusManager;
import com.xross.tools.xunit.Context;
import com.xross.tools.xunit.Processor;

//TODO get from JMX
public class MarkdownOperation implements Processor {

	@Override
	public void process(Context ctx) {
		WebContext context = (WebContext)ctx;
		
		Object value = null;
		try {
			switch (context.getAction()) {
			case "markdownApp":
				DalStatusManager.getMarkdownStatus().setAppMarkdown(true);
				value = "markdown App done";
				break;
			case "markupApp":
				DalStatusManager.getMarkdownStatus().setAppMarkdown(false);
				value = "markup App done";
				break;
			case "enableAutoMarkdown":
				DalStatusManager.getMarkdownStatus().setEnableAutoMarkdown(true);
				value = "enableAutoMarkdown done";
				break;
			case "diableAutoMarkdown":
				DalStatusManager.getMarkdownStatus().setEnableAutoMarkdown(false);
				value = "diableAutoMarkdown done";
				break;
			case "markupAllDb":
				for(String name: DalClientFactory.getDalConfigure().getDataSourceNames()) 
					DalStatusManager.getDataSourceStatus(name).setManualMarkdown(false);
				value = "markupAllDb done";
				break;
			case "markdownDbSet":
				DalStatusManager.getDatabaseSetStatus(context.get("name")).setMarkdown(true);
				value = "markdownDbSet " + context.get("name") + " done";
				break;
			case "markupDbSet":
				DalStatusManager.getDatabaseSetStatus(context.get("name")).setMarkdown(false);
				value = "markupDbSet " + context.get("name") + " done";
				break;
			case "markdownByName":
				DalStatusManager.getDataSourceStatus(context.get("name")).setManualMarkdown(true);
				value = "markdownByName " + context.get("name") + " done";
				break;
			case "markdownBothSimpleShard":
				DalStatusManager.getDataSourceStatus("SimpleShard_0").setManualMarkdown(true);
				DalStatusManager.getDataSourceStatus("SimpleShard_1").setManualMarkdown(true);
				value = "markdownBothSimpleShard done";
				break;
			case "setAutoMarkupDelay":
				DalStatusManager.getMarkdownStatus().setAutoMarkupDelay(context.getInt("delay"));
				value = "setAutoMarkupDelay " + context.getInt("delay") + " done";
				break;
			case "enableTimeoutMarkdown":
				DalStatusManager.getTimeoutMarkdown().setEnabled(context.getBoolean("value"));
				value = "enableTimeoutMarkdown " + context.getBoolean("value") + " done";
				break;
			case "setErrorCountThreshold":
				DalStatusManager.getTimeoutMarkdown().setErrorCountThreshold(context.getInt("value"));
				value = "setErrorCountThreshold " + context.getInt("value") + " done";
				break;
			case "setSamplingDuration":
				DalStatusManager.getTimeoutMarkdown().setSamplingDuration(context.getInt("value"));
				value = "setSamplingDuration " + context.getInt("value") + " done";
				break;
			case "setErrorPercentThreshold":
				DalStatusManager.getTimeoutMarkdown().setErrorPercentThreshold(context.getInt("value"));
				value = "setErrorPercentThreshold " + context.getInt("value") + " done";
				break;
			case "setErrorPercentReferCount":
				DalStatusManager.getTimeoutMarkdown().setErrorPercentReferCount(context.getInt("value"));
				value = "setErrorPercentReferCount " + context.getInt("value") + " done";
				break;
			case "setMySqlErrorCodes":
				DalStatusManager.getTimeoutMarkdown().setMySqlErrorCodes(context.get("value"));
				value = "setMySqlErrorCodes " + context.get("value") + " done";
				break;
			case "setSqlServerErrorCodes":
				DalStatusManager.getTimeoutMarkdown().setSqlServerErrorCodes(context.get("value"));
				value = "setSqlServerErrorCodes " + context.get("value") + " done";
				break;
			default:
				break;
			}
		} catch (Exception e) {
			context.handle(e);
		}
		
		context.setResponsValue(value);
	}
}
