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
<div class="modal fade" id="refuseModal" tabindex="-1" role="dialog"
     aria-labelledby="myModalLabel" aria-hidden="true" is_update="0">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="myModalLabel">审批意见</h4>
            </div>
            <div class="modal-body">
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label" style="width: 140px;">请输入审批意见：</label>
                        <textarea id="approveMsg" class="span12 popup_text input-sm"
                                  rows="4"></textarea>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <label id="error_msg" class="control-label popup_label"
                       style="color: red;"></label>
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button id="refuse_dao" type="button" class="btn btn-primary">保存</button>
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
<script src="/static/js/ajaxutil.js?codegen=${version}"></script>
<script src="/static/js/header.js?codegen=${version}"></script>
<script src="/static/js/eventmanage.js?codegen=${version}"></script>
</body>
</html>
