<%@page pageEncoding="UTF-8" %>
<%@page import="com.ctrip.platform.dal.daogen.utils.Configuration" %>
<%
    String version = Configuration.get("version");
    request.setAttribute("version", version);
%>
<!DOCTYPE html>
<html lang='en'>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>Ctrip DAO Generator</title>
    <link href="/static/images/favicon.ico" rel="shortcut icon" type="image/vnd.microsoft.icon"/>
    <link href="/static/assets/application.css?codegen=${version}" media="all" rel="stylesheet"/>
    <link href="/static/assets/print.css?codegen=${version}" media="print" rel="stylesheet"/>
    <link href="/static/bootstrap/css/bootstrap-responsive.min.css?codegen=${version}" rel="stylesheet">
    <link href="/static/w2ui/w2ui-1.3.2.min.css?codegen=${version}" rel="stylesheet"/>
    <link href="/static/css/selectize.bootstrap3.css?codegen=${version}" rel="stylesheet">
    <link href="/static/css/common.css?codegen=${version}" rel="stylesheet">
</head>
<body class='ui_mars login-page application'>
<header class='navbar navbar-fixed-top navbar-gitlab'>
    <div class='navbar-inner'>
        <div class='container'>
            <div class='app_logo'>
                <a class="home" href=""><img alt="Logo white"
                                             src="/static/images/logo.png"/> </a>
            </div>
            <h1 class='title'></h1>
            <button class='navbar-toggle' data-target='.navbar-collapse'
                    data-toggle='collapse' type='button'>
                <span class='sr-only'>Toggle navigation</span> <i
                    class='fa fa-bars'></i>
            </button>
        </div>
    </div>
</header>
<div class='container navless-container'>
    <div class='content'>
        <div class='flash-container'>
            <div class='flash-alert'>请先登录。</div>
        </div>
        <div class='row prepend-top-20'>
            <div class='col-sm-5'>
                <div>
                    <div class='login-box'>
                        <div class='login-heading'>
                            <h3>登录</h3>
                        </div>
                        <div class='login-body'>
                            <div class="new_user" id="signin_user">
                                <input id="user_login" class="form-control top"
                                       placeholder="工号" required="required" autofocus="autofocus"
                                       type="text" data-toggle="tooltip" data-placement="top"/> <input
                                    id="user_password" class="form-control bottom"
                                    placeholder="密码" required="required" type="password"
                                    data-toggle="tooltip" data-placement="top"/>

                                <div class='remember-me checkbox'>
                                    <label for='user_remember_me'> <input
                                            id="user_remember_me" type="checkbox"/> <span>记住我</span>
                                    </label>
                                </div>
                                <div>
                                    <input type="button" id="signin" name="commit" value="登录"
                                           class="btn btn-save"/>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class='prepend-top-20'>
                        <div class='login-box'>
                            <div class='login-heading'>
                                <h3>创建帐号</h3>
                            </div>
                            <div class='login-body'>
                                <div class="new_user" id="user_signup">
                                    <div>
                                        <input id="user_no_sign_up" class="form-control top"
                                               placeholder="工号" required="required" type="text"
                                               data-toggle="tooltip" data-placement="top"/>
                                    </div>
                                    <div>
                                        <input id="user_name_sign_up" class="form-control middle"
                                               placeholder="姓名" required="required" type="text"
                                               data-toggle="tooltip" data-placement="top"/>
                                    </div>
                                    <div>
                                        <input id="user_email_sign_up" class="form-control middle"
                                               placeholder="Email" required="required" type="email"
                                               data-toggle="tooltip" data-placement="top"/>
                                    </div>
                                    <div class='form-group append-bottom-20'
                                         id='password-strength'>
                                        <input id="user_password_sign_up" class="form-control bottom"
                                               placeholder="密码" required="required" type="password"
                                               data-toggle="tooltip" data-placement="top"/>
                                    </div>
                                    <div></div>
                                    <div>
                                        <input type="button" id="signup" name="commit" value="创建"
                                               class="btn-create btn"/>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<!--Begin modal-->
<div class="modal fade" id="setupDbModal" tabindex="-1" role="dialog" aria-labelledby="setupModalLabel"
     aria-hidden="true" is_update="0">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="setupModalLabel">Setup Database</h4>
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
                            <label class="control-label popup_label"
                                   style="width: 130px;text-align: right;" title="数据库地址">DB Address:</label> <input
                                id="setupdbaddress" class="span8 input-sm" type="text">
                        </div>
                    </div>
                    <div class="row-fluid">
                        <div class="control-group">
                            <label class="control-label popup_label"
                                   style="width: 130px;text-align: right;" title="数据库端口">DB Port:</label> <input
                                id="setupdbport" class="span8 input-sm" type="text">
                        </div>
                    </div>
                    <div class="row-fluid">
                        <div class="control-group">
                            <label class="control-label popup_label"
                                   style="width: 130px;text-align: right;" title="数据库登录用户">DB User:</label> <input
                                id="setupdbuser" class="span8 input-sm" type="text">
                        </div>
                    </div>
                    <div class="row-fluid">
                        <div class="control-group">
                            <label class="control-label popup_label"
                                   style="width: 130px;text-align: right;" title="数据库登录用户密码">DB Password:</label> <input
                                id="setupdbpassword" class="span8 input-sm" type="text">
                        </div>
                    </div>
                </div>
                <div id="setup_db_step2" class="row-fluid">
                    <div class="row-fluid">
                        <div class="control-group">
                            <label class="control-label popup_label" style="width: 130px;text-align: right;"
                                   title="数据库">DB
                                Catalog:</label> <select id="setupdbcatalog" class="span8"></select>
                            <img src="/static/images/ajax-loading.gif" id="loading"
                                 style="height: 20px;width: 20px;">
                        </div>
                        <div class="control-group">
                            <label class="control-label popup_label" style="width: 130px;text-align: right;">
                                &nbsp;</label>
                            <button id="setup_db_use" type="button" class="btn btn-primary">使用已有资源</button>
                        </div>
                    </div>
                    <hr/>
                    <div class="row-fluid">
                        <div class="control-group">
                            <label class="control-label popup_label" style="width: 130px;text-align: right;" title="组名">Team
                                Name:</label> <input
                                id="setupdbgroupname" class="span8 input-sm" type="text">
                        </div>
                    </div>
                    <div class="row-fluid">
                        <div class="control-group">
                            <label class="control-label popup_label"
                                   style="width: 130px;text-align: right;" title="备注">Comment:</label>
                            <input id="setupdbcomment" class="span8 input-sm" type="text">
                        </div>
                    </div>
                    <hr/>
                    <div class="row-fluid">
                        <div class="control-group">
                            <label class="control-label popup_label" style="width: 130px;text-align: right;"
                                   title="管理员工号">Admin
                                No:</label> <input
                                id="setupdbadminno" class="span8 input-sm" type="text">
                        </div>
                    </div>
                    <div class="row-fluid">
                        <div class="control-group">
                            <label class="control-label popup_label"
                                   style="width: 130px;text-align: right;" title="管理员姓名">Name:</label> <input
                                id="setupdbadminname" class="span8 input-sm" type="text">
                        </div>
                    </div>
                    <div class="row-fluid">
                        <div class="control-group">
                            <label class="control-label popup_label"
                                   style="width: 130px;text-align: right;" title="管理员邮件地址">Email:</label> <input
                                id="setupdbadminemail" class="span8 input-sm" type="text">
                        </div>
                    </div>
                    <div class="row-fluid">
                        <div class="control-group">
                            <label class="control-label popup_label"
                                   style="width: 130px;text-align: right;" title="管理员密码">Password:</label> <input
                                id="setupdbadminpass" class="span8 input-sm" type="password">
                        </div>
                    </div>
                </div>
                <div class="row-fluid">
                    <div class="control-group">
                        <label id="setup_error_msg" class="control-label popup_label"
                               style="color: red;width: 200px;"></label>
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
<script src="/static/assets/application.js?codegen=${version}"></script>
<script src="/static/w2ui/w2ui-1.3.2.min.js?codegen=${version}"></script>
<script src="/static/js/js.cookie.min.js?codegen=${version}"></script>
<script src="/static/js/cblock.js?codegen=${version}"></script>
<script src="/static/js/selectize.min.js?codegen=${version}"></script>
<script src="/static/js/login.js?codegen=${version}"></script>
</body>
</html>
