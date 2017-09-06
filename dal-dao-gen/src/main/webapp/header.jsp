<%@page pageEncoding="UTF-8" %>
<%@page import="com.ctrip.platform.dal.daogen.Consts" %>
<%@page import="com.ctrip.platform.dal.daogen.utils.Configuration" %>
<%
    String codegenManual = Configuration.get("codegen_manual");
    String dalManual = Configuration.get("dal_manual");
    String sqlServerSpec = Configuration.get("sql_server_spec");
    String mySqlSpec = Configuration.get("mysql_spec");
    String dalTeamEmail = Configuration.get("dal_team_email");
    String reportPage = Configuration.get("report_page");
    String decryptionPage = Configuration.get("decryption_page");
    request.setAttribute("codegenManual", codegenManual);
    request.setAttribute("dalManual", dalManual);
    request.setAttribute("sqlServerSpec", sqlServerSpec);
    request.setAttribute("mySqlSpec", mySqlSpec);
    request.setAttribute("dalTeamEmail", dalTeamEmail);
    request.setAttribute("reportPage", reportPage);
    request.setAttribute("decryptionPage", decryptionPage);
%>
<nav class="navbar navbar-inverse navbar-embossed navbar-fixed-top" role="navigation">
    <div class="navbar-header">
        <a href="index.jsp"> <img class="logo" src="/static/images/logo.png" style="padding: 5px; float: left;">
        </a>
    </div>
    <div class="collapse navbar-collapse" id="navbar-collapse-01">
        <ul class="nav navbar-nav">
            <li id="indexjsp"><a href="index.jsp">代码生成器</a></li>
            <li id="codeviewjsp"><a href="codeview.jsp">项目一览</a></li>
            <li id="membermanagejsp"><a href="membermanage.jsp">组员管理</a></li>
            <li id="dbmanagejsp" class="dropdown"><a href="javascript:;" class="dropdown-toggle" data-toggle="dropdown"
                                                     data-hover="dropdown" data-close-others="true"> <span
                    class="username">数据库管理</span> <i class="glyphicon glyphicon-menu-down"> </i>
            </a> <span class="dropdown-arrow dropdown-arrow-inverse" style="margin-top: 1px"></span>
                <ul class="dropdown-menu dropdown-inverse" style="margin-top: 8px !important">
                    <li><a href="dbview.jsp">数据库一览</a></li>
                    <li><a href="dbmanage.jsp">数据库管理</a></li>
                    <li><a href="dbsetsmanage.jsp">逻辑数据库管理</a></li>
                </ul>
            </li>
            <li id="eventmanagejsp"><a href="eventmanage.jsp">审批管理</a></li>
            <li id="groupmanagejsp"><a href="groupmanage.jsp">组管理</a></li>
            <li id="usermanagejsp" style="display: none;"><a href="usermanage.jsp">用户管理</a></li>
        </ul>
        <ul class="nav navbar-nav navbar-right">
            <li class="dropdown"><a href="javascript:;" class="dropdown-toggle" data-toggle="dropdown"
                                    data-hover="dropdown" data-close-others="true"> <span class="username">帮助</span>
                <i class="glyphicon glyphicon-menu-down"> </i>
            </a> <span class="dropdown-arrow dropdown-arrow-inverse"
                       style="margin-top: 1px"></span>
                <ul class="dropdown-menu dropdown-inverse"
                    style="margin-top: 8px !important">
                    <li><a href="javascript:;"
                           onclick="window.open('${codegenManual}', '_blank');">Code
                        Gen 使用说明</a></li>
                    <li><a href="javascript:;"
                           onclick="window.open('${dalManual}', '_blank');">DAL
                        框架使用说明</a>
                    </li>
                    <li><a href="javascript:;"
                           onclick="window.open('${sqlServerSpec}', '_blank');">SQL
                        Server 使用规范</a></li>
                    <li><a href="javascript:;"
                           onclick="window.open('${mySqlSpec}', '_blank');">MySQL
                        开发规范详解</a>
                    </li>
                    <li><a href="javascript:;" onclick="window.open('${decryptionPage}', '_blank');">Java DAL 参数解密</a>
                    </li>
                    <li><a href="javascript:;" onclick="window.open('${reportPage}', '_blank');">Java DAL 版本统计</a></li>
                    <li><a href="mailto:${dalTeamEmail}"><span
                            class="glyphicon glyphicon-envelope"></span>&nbsp;联系我们</a>
                    </li>
                </ul>
            </li>
            <li class="dropdown user"><a href="javascript:;" class="dropdown-toggle" data-toggle="dropdown"
                                         data-hover="dropdown" data-close-others="true"> <span
                    class="username"> <%=request.getSession().getAttribute(Consts.USER_NAME)%>
				</span> <i class="glyphicon glyphicon-menu-down"> </i>
            </a> <span class="dropdown-arrow dropdown-arrow-inverse" style="margin-top: 1px"></span>
                <ul class="dropdown-menu dropdown-inverse" style="margin-top: 8px !important">
                    <li id="menu_password" style="display: none"><a id="password" href="javascript:;"><span
                            class="glyphicon glyphicon-user"></span>&nbsp;更改密码</a></li>
                    <li><a id="logout" href="/logout.jsp"><span class="glyphicon glyphicon-log-out"></span>&nbsp;注销
                    </a></li>
                </ul>
            </li>
            <li style="margin-right: 28px !important"></li>
        </ul>
    </div>
    <!-- /.navbar-collapse -->
</nav>
<div class="modal fade" id="overrideAlertErrorNoticeDiv" tabindex="-1" role="dialog"
     aria-labelledby="generateCodeProcessLabel" aria-hidden="true" style="z-index: 999999 !important">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
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
<div class="modal fade" id="passwordModal" tabindex="-1" role="dialog"
     aria-labelledby="myModalLabel" aria-hidden="true" is_update="0">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="myModalLabel">更改密码</h4>
            </div>
            <div class="modal-body">
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label" style="width: 100px;">旧密码:</label>
                        <input id="oldPassword" class="span7 input-sm" type="password">&nbsp;
                    </div>
                </div>
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label" style="width: 100px;">新密码:</label>
                        <input id="newPassword" class="span7 input-sm" type="password">&nbsp;
                    </div>
                </div>
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label" style="width: 100px;">确认新密码:</label>
                        <input id="confirmPassword" class="span7 input-sm" type="password">
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <label id="password_error_msg" class="control-label popup_label"
                       style="color: red;"></label>
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button id="change_password" type="button" class="btn btn-primary">更改</button>
            </div>
        </div>
    </div>
</div>