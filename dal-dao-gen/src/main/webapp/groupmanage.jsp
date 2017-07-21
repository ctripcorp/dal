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
</head>
<body>
<!-- Docs master nav -->
<%@ include file="header.jsp" %>

<div id="main_layout"></div>
<!--Begin modal-->
<div class="modal fade" id="groupModal" tabindex="-1" role="dialog"
     aria-labelledby="myModalLabel" aria-hidden="true" is_update="0">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="myModalLabel">添加 DAL Team</h4>
            </div>
            <div class="modal-body">
                <div class="row-fluid">
                    <div class="control-group">
                        <input id="group_id" type="hidden" value=""> <label
                            class="control-label popup_label" style="width: 100px;">Team
                        名称:</label> <input id="name" class="span7 input-sm" type="text">&nbsp;
                        <a href="#" class="ctip" data-toggle="tooltip"
                           data-placement="right" title="一个业务线DAL Team对应一个Group Name。">
								<span class="glyphicon glyphicon-question-sign"
                                      aria-hidden="true"></span>
                        </a>
                    </div>
                </div>
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label" style="width: 100px;">备
                            注:</label> <input id="comment" class="span7 input-sm" type="text">
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <label id="error_msg" class="control-label popup_label"
                       style="color: red;"></label>
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button id="save_group" type="button" class="btn btn-primary">保存</button>
            </div>
        </div>
    </div>
</div>
<!--End modal-->

<!--Begin modal-->
<div class="modal fade" id="groupModal2" tabindex="-1" role="dialog"
     aria-labelledby="updateDalTeamLabel" aria-hidden="true" is_update="0">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="updateDalTeamLabel">更新 DAL Team</h4>
            </div>
            <div class="modal-body">
                <div class="row-fluid">
                    <div class="control-group">
                        <label
                                class="control-label popup_label" style="width: 100px;">Team
                            名称:</label> <input id="name2" class="span7 input-sm" type="text">&nbsp;
                    </div>
                </div>
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label" style="width: 100px;">备
                            注:</label> <input id="comment2" class="span7 input-sm" type="text">
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <label id="error_msg2" class="control-label popup_label"
                       style="color: red;"></label>
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button id="update_group" type="button" class="btn btn-primary">更新</button>
            </div>
        </div>
    </div>
</div>
<!--End modal-->

<div class="modal fade" id="errorNoticeDiv" tabindex="-1" role="dialog"
     aria-labelledby="generateCodeProcessLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-hidden="true">&times;</button>
                <h4 class="modal-title">错误提示</h4>
            </div>
            <div class="modal-body">
                <div class="row-fluid">
                    <div class="control-group">
                        <label id="errorNoticeDivMsg" class="control-label popup_label"></label>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
            </div>
        </div>
    </div>
</div>

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
<script src="/static/js/groupmanage.js?codegen=${version}"></script>
</body>
</html>
