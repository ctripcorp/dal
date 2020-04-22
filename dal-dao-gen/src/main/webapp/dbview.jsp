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
<div class="modal fade" id="addDbModal" tabindex="-1" role="dialog"
     aria-labelledby="addDbLabel" aria-hidden="true" is_update="0">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="addDbLabel">添加数据库</h4>
            </div>
            <div class="modal-body">
                <div id="add_new_db_step1" class="row-fluid">
                    <div class="row-fluid">
                        <div class="control-group">
                            <label class="control-label popup_label" style="width: 130px;">数据库类型:</label>
                            <select id="dbtype" class="span8">
                                <option value="no">请选择</option>
                                <option value="MySQL">MySQL</option>
                                <option value="SQLServer">SQLServer</option>
                            </select>
                        </div>
                    </div>

                    <div class="row-fluid">
                        <div class="control-group">
                            <label class="control-label popup_label" style="width: 130px;">DB
                                Catalog:</label> <select id="dbcatalog" class="span8"></select>
                        </div>
                    </div>

                    <div class="row-fluid">
                        <div class="control-group">
                            <label class="control-label popup_label" style="width: 130px;">DB
                                Address:</label> <input id="dbaddress" class="span8 input-sm"
                                                        type="text">
                        </div>
                    </div>
                    <div class="row-fluid">
                        <div class="control-group">
                            <label class="control-label popup_label" style="width: 130px;">DB
                                Port:</label> <input id="dbport" class="span8 input-sm" type="text">
                        </div>
                    </div>
                    <div class="row-fluid">
                        <div class="control-group">
                            <label class="control-label popup_label" style="width: 130px;">DB
                                User:</label> <input id="dbuser" class="span8 input-sm" type="text">
                        </div>
                    </div>
                    <div class="row-fluid">
                        <div class="control-group">
                            <label class="control-label popup_label" style="width: 130px;">DB
                                Password:</label> <input id="dbpassword" class="span8 input-sm"
                                                         type="text">
                        </div>
                    </div>
                </div>
                <div id="add_new_db_step2" class="row-fluid">

                    <div class="row-fluid">
                        <div class="control-group">
                            <label class="control-label popup_label" style="width: 130px;">All-In-One
                                Name:</label> <input id="allinonename" class="span5 input-sm"
                                                     type="text"><a id="validateKeyname"
                                                                    class="ctip"
                                                                    data-toggle="tooltip" data-placement="right"
                                                                    html="1"
                                                                    title="检查All-In-One Name有效性" style="display: none;">
                            &nbsp;检查有效性</a>
                        </div>
                    </div>
                    <div class="row-fluid" style="margin-top: 12px">
                        <div class="control-group">
                            <label class="popup_label">
                                <input id="add_to_group" type="checkbox" checked="checked"> 保存时添加到我的DAL Team中
                            </label>&nbsp;
                            <span id="dalgroupspan"><select id="dalgroup" class="span6"></select></span>
                        </div>
                    </div>
                    <div class="row-fluid" style="margin-top: 12px">
                        <div class="control-group">
                            <label class="popup_label"><input id="gen_default_dbset" type="checkbox" checked="checked">
                                保存时生成默认的逻辑数据库（databaseSet
                                和 databaseSet Entry)
                                <a href="#" class="ctip" data-toggle="tooltip" data-placement="bottom" html="1" title="
									&lt; databaseSet name='XXX' provider='sqlProvider' shardStrategy=' ' &gt; </br>
							            &lt; add  name='XXX' databaseType='Master' sharding=' ' connectionString='XXX'/ &gt; </br>
							        &lt; /databaseSet &gt;</br>其中XXX即为所选择的数据库名">
                                    <span class="glyphicon glyphicon-question-sign" aria-hidden="true"></span>
                                </a>
                            </label>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <label id="error_msg" class="control-label popup_label" style="color: red;"></label>
                <button id="conn_test" type="button" class="btn btn-success">连接测试</button>
                <button id="add_new_db_next" type="button" class="btn btn-primary">下一步</button>
                <button id="add_new_db_prev" type="button" class="btn btn-info">上一步</button>
                <button id="add_new_db_save" type="button" class="btn btn-primary">保存</button>
            </div>
        </div>
    </div>
</div>
<!--End modal-->

<!--Begin modal-->
<div class="modal fade" id="updateDbModal" tabindex="-1" role="dialog"
     aria-labelledby="updateDbLabel" aria-hidden="true" is_update="0">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="updateDbLabel">更新数据库</h4>
            </div>
            <div class="modal-body">
                <div id="update_db_step1" class="row-fluid">
                    <div class="row-fluid">
                        <div class="control-group">
                            <label class="control-label popup_label" style="width: 130px;">数据库类型:</label>
                            <select id="dbtype_up" class="span8">
                                <option value="no">请选择</option>
                                <option value="MySQL">MySQL</option>
                                <option value="SQLServer">SQLServer</option>
                            </select>
                        </div>
                    </div>
                    <div class="row-fluid">
                        <div class="control-group">
                            <label class="control-label popup_label" style="width: 130px;">DB
                                Address:</label> <input id="dbaddress_up" class="span8 input-sm"
                                                        type="text">
                        </div>
                    </div>
                    <div class="row-fluid">
                        <div class="control-group">
                            <label class="control-label popup_label" style="width: 130px;">DB
                                Port:</label> <input id="dbport_up" class="span8 input-sm" type="text"
                                                     value="28747">
                        </div>
                    </div>
                    <div class="row-fluid">
                        <div class="control-group">
                            <label class="control-label popup_label" style="width: 130px;">DB
                                User:</label> <input id="dbuser_up" class="span8 input-sm" type="text">
                        </div>
                    </div>
                    <div class="row-fluid">
                        <div class="control-group">
                            <label class="control-label popup_label" style="width: 130px;">DB
                                Password:</label> <input id="dbpassword_up" class="span8 input-sm"
                                                         type="text">
                        </div>
                    </div>
                </div>
                <div id="update_db_step2" class="row-fluid">
                    <div class="row-fluid">
                        <div class="control-group">
                            <label class="control-label popup_label" style="width: 130px;">DB
                                Catalog:</label> <select id="dbcatalog_up" class="span8"></select>
                        </div>
                    </div>
                    <div class="row-fluid">
                        <div class="control-group">
                            <label class="control-label popup_label" style="width: 130px;">All-In-One
                                Name:</label> <input id="allinonename_up" class="span8 input-sm"
                                                     type="text">
                        </div>
                    </div>
                </div>
                <div class="row-fluid">
                    <div class="control-group">
                        <label id="update_error_msg" class="control-label popup_label"
                               style="color: red;"></label>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button id="update_conn_test" type="button" class="btn btn-success">连接测试</button>
                <button id="update_db_next" type="button" class="btn btn-primary">下一步</button>
                <button id="update_db_prev" type="button" class="btn btn-info">上一步</button>
                <button id="update_db_save" type="button" class="btn btn-primary">保存</button>
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
                        <label id="errorMess" class="control-label popup_label"></label>
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
<script src="/static/js/ajaxutil.js?codegen=${version}"></script>
<script src="/static/js/header.js?codegen=${version}"></script>
<script src="/static/js/dbview.js?codegen=${version}"></script>
</body>
</html>
