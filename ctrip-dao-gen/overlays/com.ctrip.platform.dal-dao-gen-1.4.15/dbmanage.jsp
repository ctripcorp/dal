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
<div class="modal fade" id="dbModal" tabindex="-1" role="dialog"
     aria-labelledby="addDbLabel" aria-hidden="true" is_update="0">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="addDbLabel">添加数据库</h4>
            </div>
            <div class="modal-body">
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label" style="width: 80px;">数据库：</label>
                        <select id="databases" class="span8"></select> <a
                            href="dbview.jsp" target="_blank" class="ctip"
                            data-toggle="tooltip" data-placement="right" html="1"
                            title="如果没有找到，请到数据库一览界面添加数据库!">&nbsp;未找到？</a>
                    </div>
                </div>
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label" style="width: 80px;">备
                            注：</label> <input id="comment" class="span8 input-sm" type="text">
                    </div>
                </div>
                <div class="row-fluid" style="margin-top: 12px">
                    <div class="control-group">
                        <label class="popup_label"> <input id="gen_default_dbset"
                                                           type="checkbox" checked="checked">
                            保存时生成默认的逻辑数据库（databaseSet 和 databaseSet Entry) <a href="#"
                                                                              class="ctip" data-toggle="tooltip"
                                                                              data-placement="bottom"
                                                                              html="1"
                                                                              title="
									&lt; databaseSet name='XXX' provider='sqlProvider' shardStrategy=' ' &gt; </br>
							            &lt; add  name='XXX' databaseType='Master' sharding=' ' connectionString='XXX'/ &gt; </br>
							        &lt; /databaseSet &gt;</br>其中XXX即为所选择的数据库名">
									<span class="glyphicon glyphicon-question-sign"
                                          aria-hidden="true"></span>
                            </a>
                        </label>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <label id="error_msg" class="control-label popup_label"
                       style="color: red;text-align: left;"></label>
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button id="save_db" type="button" class="btn btn-primary">保存</button>
            </div>
        </div>
    </div>
</div>
<!--End modal-->

<!--Begin modal-->
<div class="modal fade" id="dbModal2" tabindex="-1" role="dialog"
     aria-labelledby="updateDbLabel" aria-hidden="true" is_update="0">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="updateDbLabel">更新数据库</h4>
            </div>
            <div class="modal-body">
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label" style="width: 80px;">数据库：</label>
                        <input id="databases2" class="span9 input-sm" type="text"
                               disabled="disabled">
                    </div>
                </div>
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label" style="width: 80px;">备
                            注:</label> <input id="comment2" class="span9 input-sm" type="text">
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <label id="error_msg2" class="control-label popup_label"
                       style="color: red;"></label>
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button id="update_db" type="button" class="btn btn-primary">更新</button>
            </div>
        </div>
    </div>
</div>
<!--End modal-->

<!--Begin modal-->
<div class="modal fade" id="transferDbModal" tabindex="-1"
     role="dialog" aria-labelledby="transferDbLabel" aria-hidden="true"
     is_update="0">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="transferDbLabel">转移数据库</h4>
            </div>
            <div class="modal-body">
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label" style="width: 130px;">转入 DAL
                            Team:</label> <select id="transferGroup" class="span8"></select>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <label id="transferdb_error_msg" class="control-label popup_label"
                       style="color: red;"></label>
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button id="transfer_db" type="button" class="btn btn-primary">转移</button>
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
<script src="/static/js/dbmanage.js?codegen=${version}"></script>
</body>
</html>
