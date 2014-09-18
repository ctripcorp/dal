package com.ctrip.platform.appinternals;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ctrip.platform.appinternals.configuration.ConfigBeanBase;
import com.ctrip.platform.appinternals.configuration.ConfigBeanManager;
import com.ctrip.platform.appinternals.helpers.Helper;
import com.ctrip.platform.appinternals.permission.Permission;

public class AppInternalsServlet extends HttpServlet{
	
	private static final String URL_TEMPLATE = "请输入正确的RUL格式， 格式如下：<br /> http://{host}/[{virtualDir}]/AppInternals/?(.*)";
	private static final String NOPERMISSION = "Sorry,Your IP Address %s Doesn't Have Read/Write Permission.";
	
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		AppInternalsContext ctx = new AppInternalsContext(request);
		if(!Helper.validateURL(request.getRequestURL().toString())){
			ctx.getContent().append(URL_TEMPLATE);
			this.doResponse(response, ctx.getContent());
			return;
		}
		
		if(Permission.getInstance().hasRead(ctx.getRemoteip()) || 
				Permission.getInstance().hasWrite(ctx.getRemoteip())){
			if(ctx.getCategory(AppConst.APPINTERNALS).equals(AppConst.CONFIGURATION)){
				try {
					this.doConfiguration(ctx);
				} catch (Exception e) {
					ctx.getContent().append(e.getMessage());
					return;
				}
			}
		}else{
			ctx.getContent().append(NOPERMISSION, ctx.getRemoteip());
		}
		
		this.doResponse(response, ctx.getContent());
	}
	
	private void doResponse(HttpServletResponse response, AppResponse msg) throws IOException{
		response.setContentType(msg.getContextType());
		response.setCharacterEncoding(msg.getEncode());
		response.getWriter().write(msg.getMessage().toString());
	}
	
	private void doConfiguration(AppInternalsContext ctx) throws Exception{
		String configs = ctx.getCategory(AppConst.CONFIGURATION);
		String action = ctx.getParameters().containsKey("action") ? 
				ctx.getParameters().get("action") : "view";
		if(configs.isEmpty() && action.equalsIgnoreCase("view")){
			ctx.getContent().append(ctx.getSerializer()
					.serializer(ConfigBeanManager.getBeans().values()));
			return;
		}
		String beanKey = ctx.getCategory("beans");
		if(beanKey.isEmpty() && action.equalsIgnoreCase("view")){
			ctx.getContent().append(ctx.getSerializer()
					.serializer(ConfigBeanManager.getBeans().values()));
			return;
		}
		if(!beanKey.isEmpty()){
			ConfigBeanBase bean = ConfigBeanManager.getBean(beanKey);
			if(action.equalsIgnoreCase("view")){
				ctx.getContent().append(ctx.getSerializer()
						.serializer(ConfigBeanManager.getBean(beanKey)));
				return;
			}
			if(action.equalsIgnoreCase("change")){
				AppMessage result = new AppMessage();
				if(Permission.getInstance().hasWrite(ctx.getRemoteip())){
					try{
						for (String key : ctx.getParameters().keySet()) {
							bean.set(key, ctx.getParameters().get(key));
						}
						result.setSueccess(true);
						result.setMessage("Success");
					}catch(Exception e){
						result.setMessage(e.getMessage());
					}
				}else{
					result.setMessage(String.format(NOPERMISSION, ctx.getRemoteip()));
				}
				ctx.getContent().append(result.toString());
			}
		}
	}
}
