<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@ page import="com.ctrip.platform.dal.daogen.resource.CustomizedResource" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<%
    CustomizedResource.getInstance().logOut(request, response);
%>