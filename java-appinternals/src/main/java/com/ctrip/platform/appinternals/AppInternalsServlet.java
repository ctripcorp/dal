package com.ctrip.platform.appinternals;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.appinternals.appinfo.AppInfo;
import com.ctrip.platform.appinternals.appinfo.AppInfoBuilder;
import com.ctrip.platform.appinternals.configuration.ConfigBeanBase;
import com.ctrip.platform.appinternals.configuration.ConfigBeanManager;
import com.ctrip.platform.appinternals.helpers.Helper;
import com.ctrip.platform.appinternals.permission.Permission;

public class AppInternalsServlet extends HttpServlet {
	private Logger logger = LoggerFactory.getLogger(AppInternalsServlet.class);
	private static final String URL_TEMPLATE = "请输入正确的RUL格式， 格式如下：<br /> http://{host}/[{virtualDir}]/AppInternals/?(.*)";
	private static final String NOPERMISSION = "Sorry,You do not have permission.";
	private static final String APPINTERNALS = "appinternals";
	private static final String CONFIGURATION = "configurations";
	private static final String preAddr = "cfx:";
	private static final String APPINFOURL = "http://ws.fx.fws.qa.nt.ctripcorp.com/appinfo/appinfo/Send";

	private static final long serialVersionUID = 1L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		try {
			ServletContext sct = config.getServletContext();
			AppInfo info = new AppInfo();
			AppInfoBuilder infoBuilder = new AppInfoBuilder(info);
			infoBuilder.setVirtualDirectory(sct.getContextPath());
			infoBuilder.setPhyDirectory(sct.getRealPath("/"));
			infoBuilder.setStartTime();
			infoBuilder.setAssemblyInfos(sct.getRealPath("/WEB-INF/lib"));
			infoBuilder.setDomain(config.getInitParameter("domain"));
			infoBuilder.setPost(config.getInitParameter("port"));
			infoBuilder.setIPV4();
			infoBuilder.setHostName();
			infoBuilder.setOS();
			infoBuilder.setIs64BitOS();
			infoBuilder.setProcessorCount();

			logger.debug(infoBuilder.getJsonAppInfo());
			String result = Helper.sendPost(APPINFOURL, infoBuilder.getJsonAppInfo());
			logger.info(String.format("Initialize register app internals completed: %s", result));
		} catch (Exception e) {
			logger.error("Initialize register app internals failed", e);
		}

		try {
			String reads = config.getInitParameter("permissions.read");
			String[] tokens = null;
			if(reads != null && !reads.isEmpty()){
				tokens = reads.split(",");
				for (String token : tokens) {
					Permission.getInstance().addUser(token, 0);
				}
			}
			String writes = config.getInitParameter("permissions.write");
			if(writes != null && !writes.isEmpty()){
			tokens = writes.split(",");
				for (String token : tokens) {
					Permission.getInstance().addUser(token, 1);
				}
			}
			logger.info(String.format("Initialize appinternals read[%s], write[%s] permissions: ", reads, writes));
		} catch (Exception e) {
			logger.error("Initialize appinternals write permissions failed", e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		AppInternalsContext ctx = new AppInternalsContext(request);
		if (!Helper.validateURL(request.getRequestURL().toString())) {
			ctx.getContent().append(URL_TEMPLATE);
			this.doResponse(response, ctx.getContent());
			return;
		}

		if (Permission.getInstance().hasRead(ctx.getRemoteip())
				|| Permission.getInstance().hasWrite(ctx.getRemoteip())) {
			if (ctx.getCategory(APPINTERNALS).equals(CONFIGURATION)) {
				try {
					this.doConfiguration(ctx);
				} catch (Exception e) {
					ctx.getContent().append(e.getMessage());
				}
			}
		} else {
			ctx.getContent().append(NOPERMISSION);
		}

		this.doResponse(response, ctx.getContent());
	}

	
	private void doResponse(HttpServletResponse response, AppResponse msg)
			throws IOException {
		response.setContentType(msg.getContextType());
		response.setCharacterEncoding(msg.getEncode());
		response.getWriter().write(msg.getMessage().toString());
	}

	private void doConfiguration(AppInternalsContext ctx) throws Exception {
		String configs = ctx.getCategory(CONFIGURATION);
		String action = ctx.getParameters().containsKey("action") ? ctx
				.getParameters().get("action") : "view";
		if (configs.isEmpty() && action.equalsIgnoreCase("view")) {
			ctx.getContent().append(
					ctx.getSerializer().serializer(
							ConfigBeanManager.getBeans().values()));
			return;
		}
		String beanKey = ctx.getCategory("beans");
		if (beanKey.isEmpty() && action.equalsIgnoreCase("view")) {
			ctx.getContent().append(
					ctx.getSerializer().serializer(
							ConfigBeanManager.getBeans().values()));
			return;
		}
		if (!beanKey.isEmpty()) {
			ConfigBeanBase bean = ConfigBeanManager.getBean(beanKey);
			if (action.equalsIgnoreCase("view")) {
				ctx.getContent().append(
						ctx.getSerializer().serializer(
								ConfigBeanManager.getBean(beanKey)));
				return;
			}
			if (action.equalsIgnoreCase("change")) {
				Result result = new Result();
				boolean isCfx = ctx.getParameters().containsKey("u") &&
						ctx.getParameters().get("u").equalsIgnoreCase(preAddr+ctx.getRemoteip());
				if (isCfx || Permission.getInstance().hasWrite(ctx.getRemoteip())) {
					try {
						for (String key : ctx.getParameters().keySet()) {
							if (key.equalsIgnoreCase("action")
									|| key.equalsIgnoreCase("format") || key.equalsIgnoreCase("u"))
								continue;
							bean.set(key, ctx.getParameters().get(key));
						}
						result.setSueccess(true);
						result.setMessage("Success");
					} catch (Exception e) {
						result.setMessage(e.getMessage());
					}
				} else {
					result.setMessage(NOPERMISSION);
				}
				ctx.getContent().append(Helper.toChangeResultJson(result));
			}
		}
	}
}
