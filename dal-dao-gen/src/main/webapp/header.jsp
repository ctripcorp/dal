

<%@page pageEncoding="UTF-8"%>
<%@ page import="org.jasig.cas.client.util.AssertionHolder"%>

<nav class="navbar navbar-inverse navbar-embossed navbar-fixed-top" role="navigation">
	<div class="navbar-header">
		<a href="index.jsp">
			<img class="logo" src="/static/images/logo.png" style="padding: 5px; float: left;">
		</a>
	</div>
	<div class="collapse navbar-collapse" id="navbar-collapse-01">
		<ul class="nav navbar-nav">
			<li id="indexjsp"><a href="index.jsp">代码生成器</a></li>
			<li id="codeviewjsp"><a href="codeview.jsp">项目一览</a></li>
			<li id="membermanagejsp"><a href="membermanage.jsp">组员管理</a></li>
			<li id="dbmanagejsp" class="dropdown">
				<a href="javascript:;" class="dropdown-toggle" data-toggle="dropdown" data-hover="dropdown"
					data-close-others="true"> 
					<span class="username">数据库管理</span> 
					<i class="fa fa-angle-down"> </i>
				</a>
				<span class="dropdown-arrow dropdown-arrow-inverse" style="margin-top:1px"></span>
				<ul class="dropdown-menu dropdown-inverse" style="margin-top:8px !important">
					<li><a href="dbview.jsp">数据库一览</a></li>
					<li><a href="dbmanage.jsp">数据库管理</a></li>
					<li><a href="dbsetsmanage.jsp">逻辑数据库管理</a></li>
				</ul>
			</li>
			<li id="groupmanagejsp"><a href="groupmanage.jsp">组管理</a></li>
		</ul>
		<ul class="nav navbar-nav navbar-right">
            <li id="welcomejsp" style="display:none"><a href="welcome.jsp">Tutorial</a></li>
			<li class="dropdown">
				<a href="javascript:;" class="dropdown-toggle" data-toggle="dropdown" data-hover="dropdown"
					data-close-others="true"> 
					<span class="username"> 
					帮助
					</span> 
					<i class="fa fa-angle-down"> </i>
				</a>
				<span class="dropdown-arrow dropdown-arrow-inverse" style="margin-top:1px"></span>
				<ul class="dropdown-menu dropdown-inverse" style="margin-top:8px !important">
					<li><a href="javascript:;"
						onclick="window.open('http://conf.ctripcorp.com/display/SysDev/DAL+Code+Generator#DALCodeGenerator-codegenguide', '_blank');">Code Gen使用说明</a>
					</li>
					<li><a href="javascript:;"
						onclick="window.open('http://conf.ctripcorp.com/pages/viewpage.action?pageId=32081284', '_blank');">DAL框架使用说明</a>
					</li>
					<li><a href="mailto:R%26Dsysdev_dal@Ctrip.com">联系我们</a></li>
				</ul>
			</li>
			<li class="dropdown user">
				<a href="javascript:;" class="dropdown-toggle" data-toggle="dropdown" data-hover="dropdown"
					data-close-others="true"> 
					<span class="username"> 
					<%=request.getSession().getAttribute("loginUserName")%>
					<%-- AssertionHolder.getAssertion().getPrincipal().getAttributes().get("sn") --%>
					</span> 
					<i class="fa fa-angle-down"> </i>
				</a>
				<span class="dropdown-arrow dropdown-arrow-inverse" style="margin-top:1px"></span>
				<ul class="dropdown-menu dropdown-inverse" style="margin-top:8px !important">
					<li><a href="/logout.jsp"> <i class="fa fa-power-off">
						</i> 注销
					</a></li>
				</ul>
			</li>
			<li style="margin-right:28px !important">
				
			</li>
		</ul>

	</div>
	<!-- /.navbar-collapse -->
</nav>
