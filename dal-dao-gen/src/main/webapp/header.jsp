<%@page pageEncoding="UTF-8"%>
<%@page
	import="com.ctrip.platform.dal.daogen.utils.AssertionHolderManager"%>
<nav class="navbar navbar-inverse navbar-embossed navbar-fixed-top"
	role="navigation">
	<div class="navbar-header">
		<a href="index.jsp"> <img class="logo"
			src="/static/images/logo.png" style="padding: 5px; float: left;">
		</a>
	</div>
	<div class="collapse navbar-collapse" id="navbar-collapse-01">
		<ul class="nav navbar-nav">
			<li id="indexjsp"><a href="index.jsp">代码生成器</a></li>
			<li id="codeviewjsp"><a href="codeview.jsp">项目一览</a></li>
			<li id="membermanagejsp"><a href="membermanage.jsp">组员管理</a></li>
			<li id="dbmanagejsp" class="dropdown"><a
				href="javascript:void(0);" class="dropdown-toggle"
				data-toggle="dropdown" data-hover="dropdown"
				data-close-others="true"> <span class="username">数据库管理</span> <i
					class="glyphicon glyphicon-menu-down"> </i>
			</a> <span class="dropdown-arrow dropdown-arrow-inverse"
				style="margin-top: 1px"></span>
				<ul class="dropdown-menu dropdown-inverse"
					style="margin-top: 8px !important">
					<li><a href="dbview.jsp">数据库一览</a></li>
					<li><a href="dbmanage.jsp">数据库管理</a></li>
					<li><a href="dbsetsmanage.jsp">逻辑数据库管理</a></li>
				</ul></li>
			<li id="eventmanagejsp"><a href="eventmanage.jsp">审批管理</a></li>
			<li id="groupmanagejsp"><a href="groupmanage.jsp">组管理</a></li>
			<li id="usermanagejsp" style="display: none;"><a
				href="usermanage.jsp">用户管理</a></li>
		</ul>
		<ul class="nav navbar-nav navbar-right">
			<li id="welcomejsp" style="display: none"><a href="welcome.jsp">Tutorial</a></li>
			<li class="dropdown"><a href="javascript:;"
				class="dropdown-toggle" data-toggle="dropdown" data-hover="dropdown"
				data-close-others="true"> <span class="username"> 帮助 </span> <i
					class="glyphicon glyphicon-menu-down"> </i>
			</a> <span class="dropdown-arrow dropdown-arrow-inverse"
				style="margin-top: 1px"></span>
				<ul class="dropdown-menu dropdown-inverse"
					style="margin-top: 8px !important">
					<li><a href="javascript:;"
						onclick="window.open('http://conf.ctripcorp.com/display/FRAM/DAL+Code+Generator', '_blank');">Code
							Gen使用说明</a></li>
					<li><a href="javascript:;"
						onclick="window.open('http://conf.ctripcorp.com/pages/viewpage.action?pageId=32081284', '_blank');">DAL框架使用说明</a>
					</li>
					<li><a href="javascript:;"
						onclick="window.open('http://conf.ctripcorp.com/pages/viewpage.action?pageId=74094596', '_blank');">SQL
							Server使用规范</a></li>
					<li><a href="javascript:;"
						onclick="window.open('http://conf.ctripcorp.com/pages/viewpage.action?pageId=55383965', '_blank');">MySQL开发规范详解</a></li>
					<li><a href="mailto:rdfxdal@Ctrip.com">联系我们</a></li>
				</ul></li>
			<li class="dropdown user"><a href="javascript:;"
				class="dropdown-toggle" data-toggle="dropdown" data-hover="dropdown"
				data-close-others="true"> <span class="username"> <%=request.getSession().getAttribute("loginUserName")%>
						<%-- AssertionHolderManager.getName() --%>
				</span> <i class="glyphicon glyphicon-menu-down"> </i>
			</a> <span class="dropdown-arrow dropdown-arrow-inverse"
				style="margin-top: 1px"></span>
				<ul class="dropdown-menu dropdown-inverse"
					style="margin-top: 8px !important">
					<li><a href="/logout.jsp"> <i
							class="glyphicon glyphicon-log-out"> </i> 注销
					</a></li>
				</ul></li>
			<li style="margin-right: 28px !important"></li>
		</ul>

	</div>
	<!-- /.navbar-collapse -->
</nav>
<div class="modal fade" id="overrideAlertErrorNoticeDiv" tabindex="-1"
	role="dialog" aria-labelledby="generateCodeProcessLabel"
	aria-hidden="true" style="z-index: 999999 !important">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-hidden="true">&times;</button>
				<h4 class="modal-title">提示信息</h4>
			</div>
			<div class="modal-body">
				<div class="row-fluid">
					<div class="control-group">
						<label id="overrideAlertErrorNoticeDivMsg"></label>
					</div>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
			</div>
		</div>
	</div>
</div>
<!--Begin modal-->
<div class="modal fade" id="setupDbModal" tabindex="-1" role="dialog"
	aria-labelledby="setupModalLabel" aria-hidden="true" is_update="0">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="setupModalLabel">Setup DataBase</h4>
			</div>
			<div class="modal-body">
				<div id="setup_db_step1" class="row-fluid">
					<div class="row-fluid" style="display: none;">
						<div class="control-group">
							<label class="control-label popup_label" style="width: 130px;">数据库类型:</label>
							<select id="setupdbtype" class="span8">
								<option value="no">请选择</option>
								<option value="MySQL" selected="selected">MySQL</option>
								<option value="SQLServer">SQLServer</option>
							</select>
						</div>
					</div>
					<div class="row-fluid">
						<div class="control-group">
							<label class="control-label popup_label" style="width: 130px;">DB
								Address:</label> <input id="setupdbaddress" class="span8 input-sm"
								type="text">
						</div>
					</div>
					<div class="row-fluid">
						<div class="control-group">
							<label class="control-label popup_label" style="width: 130px;">DB
								Port:</label> <input id="setupdbport" class="span8 input-sm" type="text"
								value="28747">
						</div>
					</div>
					<div class="row-fluid">
						<div class="control-group">
							<label class="control-label popup_label" style="width: 130px;">DB
								User:</label> <input id="setupdbuser" class="span8 input-sm" type="text">
						</div>
					</div>
					<div class="row-fluid">
						<div class="control-group">
							<label class="control-label popup_label" style="width: 130px;">DB
								Password:</label> <input id="setupdbpassword" class="span8 input-sm"
								type="text">
						</div>
					</div>
				</div>
				<div id="setup_db_step2" class="row-fluid">
					<div class="row-fluid">
						<div class="control-group">
							<label class="control-label popup_label" style="width: 130px;">DB
								Catalog:</label> <select id="setupdbcatalog" class="span8"></select>
						</div>
					</div>
					<div class="row-fluid">
						<div class="control-group">
							<label class="control-label popup_label" style="width: 130px;">Team
								Name:</label> <input id="setupdbgroupname" class="span8 input-sm"
								type="text">
						</div>
					</div>
					<div class="row-fluid">
						<div class="control-group">
							<label class="control-label popup_label" style="width: 130px;">Comment:</label>
							<input id="setupdbcomment" class="span8 input-sm" type="text">
						</div>
					</div>
				</div>
				<div class="row-fluid">
					<div class="control-group">
						<label id="setup_error_msg" class="control-label popup_label"
							style="color: red;"></label>
					</div>
				</div>
			</div>
			<div class="modal-footer">
				<button id="setup_conn_test" type="button" class="btn btn-success">连接测试</button>
				<button id="setup_db_next" type="button" class="btn btn-primary">下一步</button>
				<button id="setup_db_prev" type="button" class="btn btn-info">上一步</button>
				<button id="setup_db_save" type="button" class="btn btn-primary">保存</button>
			</div>
		</div>
	</div>
</div>
<!--End modal-->