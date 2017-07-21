<%@page pageEncoding="UTF-8" %>
<%@page import="com.ctrip.platform.dal.daogen.utils.Configuration" %>
<%
    String version = Configuration.get("version");
    request.setAttribute("version", version);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <!-- Meta, title, CSS, favicons, etc. -->
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>Ctrip DAO Generator</title>
    <!-- Favicons -->
    <link href="/static/images/favicon.ico" rel="shortcut icon">
    <link href="/static/css/common.css?codegen=${version}" rel="stylesheet">
    <!-- Bootstrap core CSS -->
    <link href="/static/bootstrap/css/bootstrap.min.css?codegen=${version}" rel="stylesheet">
    <link href="/static/bootstrap/css/bootstrap-responsive.min.css?codegen=${version}" rel="stylesheet">
    <link href="/static/w2ui/w2ui-1.3.2.min.css?codegen=${version}" rel="stylesheet"/>
    <link href="/static/jstree/themes/default/style.min.css?codegen=${version}" rel="stylesheet"/>
    <link href="/static/css/multiple-select.css?codegen=${version}" rel="stylesheet">
    <link href="/static/css/selectize.bootstrap3.css?codegen=${version}" rel="stylesheet">
    <!-- Loading Flat UI -->
    <link href="/static/Flat-UI-master/css/flat-ui.css?codegen=${version}" rel="stylesheet">
    <link href="/static/Flat-UI-master/css/demo.css?codegen=${version}" rel="stylesheet">
</head>
<body>
<!-- Docs master nav -->
<%@ include file="header.jsp" %>

<div id="main_layout"></div>
<!--Begin modal-->
<div class="modal fade" id="memberModal" tabindex="-1" role="dialog"
     aria-labelledby="addUserLabel" aria-hidden="true" is_update="0">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="addUserLabel">添加组员</h4>
            </div>
            <div class="modal-body">
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label">用户列表：</label> <select
                            id="members" class="span8">
                    </select>&nbsp; <a href="#" class="ctip" data-toggle="tooltip"
                                       data-placement="bottom" title="不在列表中的用户，请先登录系统。"> <span
                            class="glyphicon glyphicon-question-sign"></span>
                    </a>
                    </div>
                </div>
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label">组员角色：</label> <select
                            id="user_role" class="span8">
                        <option value="1">Admin</option>
                        <option value="2">Limited</option>
                    </select>&nbsp; <a href="#" class="ctip" data-toggle="tooltip"
                                       data-placement="bottom"
                                       title="1、Admin 权限的用户可以完全使用组内资源。<br/>
								2、Limited 权限的用户，可以使用组内的资源，但是生成代码需要通过审批。">
								<span class="glyphicon glyphicon-question-sign"
                                      aria-hidden="true"></span>
                    </a>
                    </div>
                </div>
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label">允许管理组员：</label> <input
                            id="allowAddUser" type="checkbox" checked="true">
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <label id="error_msg" class="control-label popup_label"
                       style="color: red;"></label>
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button id="save_member" type="button" class="btn btn-primary">保存</button>
            </div>
        </div>
    </div>
</div>
<!--End modal-->

<!--Begin modal-->
<div class="modal fade" id="addDalTeamModal" tabindex="-1"
     role="dialog" aria-labelledby="addDalTeamLabel" aria-hidden="true"
     is_update="0">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="addDalTeamLabel">添加 DAL Team</h4>
            </div>
            <div class="modal-body">
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label">DAL Team列表：</label> <select
                            id="group_list" class="span8">
                    </select>
                    </div>
                </div>
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label">DAL Team角色：</label> <select
                            id="group_role" class="span8">
                        <option value="1">Admin</option>
                        <option value="2">Limited</option>
                    </select>&nbsp; <a href="#" class="ctip" data-toggle="tooltip"
                                       data-placement="bottom"
                                       title="1、Admin 权限的用户组可以完全使用组内资源。<br/>
								2、Limited 权限的用户组，可以使用组内的资源，但是生成代码需要通过审批。">
								<span class="glyphicon glyphicon-question-sign"
                                      aria-hidden="true"></span>
                    </a>
                    </div>
                </div>
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label" style="width: 129px">允许管理组员：</label>
                        <input id="allowGroupAddUser" type="checkbox" checked="true">
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <label id="add_group_error_msg" class="control-label popup_label"
                       style="color: red;"></label>
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button id="save_add_group" type="button" class="btn btn-primary">保存</button>
            </div>
        </div>
    </div>
</div>
<!--End modal-->

<!--Begin modal-->
<div class="modal fade" id="updateUserModal" tabindex="-1"
     role="dialog" aria-labelledby="userRightLabel" aria-hidden="true"
     is_update="0">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="userRightLabel">用户权限修改</h4>
            </div>
            <div class="modal-body">
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label">组员姓名：</label><span
                            id="user_name" class="label label-info"
                            style="margin-left: 15px">Info</span>
                    </div>
                </div>
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label">组员角色：</label><select
                            id="up_user_role" class="span8">
                        <option value="1">Admin</option>
                        <option value="2">Limited</option>
                    </select>&nbsp; <a href="#" class="ctip" data-toggle="tooltip"
                                       data-placement="bottom"
                                       title="1、Admin 权限的用户可以完全使用组内资源。<br/>
								2、Limited 权限的用户，可以使用组内的资源，但是生成代码需要通过审批。">
								<span class="glyphicon glyphicon-question-sign"
                                      aria-hidden="true"></span>
                    </a>
                    </div>
                </div>
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label">允许管理组员：</label> <input
                            id="up_allowAddUser" type="checkbox" checked="true">
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <label id="up_error_msg" class="control-label popup_label"
                       style="color: red;"></label>
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button id="save_up_member" type="button" class="btn btn-primary">保存</button>
            </div>
        </div>
    </div>
</div>
<!--End modal-->

<!--Begin modal-->
<div class="modal fade" id="updateDALTeamModal" tabindex="-1"
     role="dialog" aria-labelledby="dalTeamRightLabel" aria-hidden="true"
     is_update="0">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="dalTeamRightLabel">DAL Team 权限修改</h4>
            </div>
            <div class="modal-body">
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label">DAL Team名称：</label> <span
                            id="group_name" class="label label-info"
                            style="margin-left: 15px">Info</span>
                    </div>
                </div>
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label">DAL Team角色：</label> <select
                            id="up_group_role" class="span8">
                        <option value="1">Admin</option>
                        <option value="2">Limited</option>
                    </select>&nbsp; <a href="#" class="ctip" data-toggle="tooltip"
                                       data-placement="bottom"
                                       title="1、Admin权限的用户组可以完全使用组内资源.<br/>
								2、Limited权限的用户组，可以使用组内的资源，但是生成代码需要通过审批.">
								<span class="glyphicon glyphicon-question-sign"
                                      aria-hidden="true"></span>
                    </a>
                    </div>
                </div>
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label" style="width: 129px">允许管理组员：</label>
                        <input id="up_allowGroupAddUser" type="checkbox" checked="true">
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <label id="up_group_error_msg" class="control-label popup_label"
                       style="color: red;"></label>
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button id="save_up_group" type="button" class="btn btn-primary">保存</button>
            </div>
        </div>
    </div>
</div>
<!--End modal-->

<!--[if lt IE 9]>
<script src="./docs-assets/js/ie8-responsive-file-warning.js"></script>
<![endif]-->
<!--[if lt IE 9]>
<script src="/static/Flat-UI-master/js/html5shiv.js"></script>
<script src="/static/Flat-UI-master/js/respond.min.js"></script>
<![endif]-->
<script src="/static/jquery/jquery-1.10.2.min.js?codegen=${version}"></script>
<script src="/static/jquery/jquery.blockui.min.js?codegen=${version}"></script>
<script src="/static/jquery/multiple-select.js?codegen=${version}"></script>
<script src="/static/bootstrap/js/bootstrap.min.js?codegen=${version}"></script>
<script src="/static/w2ui/w2ui-1.3.2.min.js?codegen=${version}"></script>
<script src="/static/jstree/jstree.js?codegen=${version}"></script>
<script src="/static/js/sprintf.js?codegen=${version}"></script>
<script src="/static/js/selectize.min.js?codegen=${version}"></script>
<script src="/static/js/cblock.js?codegen=${version}"></script>
<script src="/static/js/header.js?codegen=${version}"></script>
<script src="/static/js/membermanage.js?codegen=${version}"></script>
</body>
</html>
