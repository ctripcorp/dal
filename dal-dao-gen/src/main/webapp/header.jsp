

<%@page pageEncoding="UTF-8"%>
<%@ page import="org.jasig.cas.client.util.AssertionHolder" %>
<div class="dal-navbar navbar navbar-inverse navbar-fixed-top" role="banner">
   <div class="navbar-header">
      <a href="welcome.jsp">
      <img class="logo" src="/static/images/logo.png" style="padding:5px;float:left;">
      </a>
   </div>
   <div class="collapse navbar-collapse in dal-navbar-collapse" role="navigation">
      <ul class="nav navbar-nav">
         <li class="active">
            <a href="index.jsp">数据访问层生成器</a>
         </li>
         <li>
            <a href="codeview.jsp">DAL Code 一览</a>
         </li>
         <li>
            <a href="membermanage.jsp">Member管理</a>
         </li>
         <li>
            <a href="dbmanage.jsp">DB管理</a>
         </li>
         <li>
            <a href="groupmanage.jsp">DAL Group管理</a>
         </li>
      </ul>
      <ul class="nav navbar-nav pull-right">
         <li>
            <a href="javascript:;" onclick="window.open('http://conf.ctripcorp.com/pages/viewpage.action?pageId=32081284', '_blank');">文档</a>
         </li>
         <li>
            <a href="javascript:;" onclick="window.open('http://conf.ctripcorp.com/display/SysDev/DAL+Code+Generator#DALCodeGenerator-codegenguide', '_blank');">使用说明</a>
         </li>
         <li>
            <a href="mailto:R%26Dsysdev_dal@Ctrip.com">咨询</a>
         </li>
         <li class="dropdown user">
            <a href="javascript:;" class="dropdown-toggle" data-toggle="dropdown" data-hover="dropdown" data-close-others="true">
            <span class="username">
            <%=AssertionHolder.getAssertion().getPrincipal().getAttributes().get("sn")%>
            </span>
            <i class="fa fa-angle-down">
            </i>
            </a>
            <ul class="dropdown-menu">
               <li>
                  <a href="/logout.jsp">
                  <i class="fa fa-power-off">
                  </i>
                  注销
                  </a>
               </li>
            </ul>
         </li>
      </ul>
   </div>
</div>
