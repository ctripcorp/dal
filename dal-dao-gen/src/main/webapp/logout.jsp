<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="org.jasig.cas.client.authentication.AttributePrincipal" %>
<%@ page import="com.ctrip.platform.dal.common.util.Configuration" %>
 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
 
<%
        session.invalidate();
        String redirectURL =Configuration.get("cas_url") + "/caso/logout?service=" + Configuration.get("codegen_url"); 
        response.sendRedirect(redirectURL);
%>