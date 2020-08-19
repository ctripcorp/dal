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
<div class="modal fade" id="updateDbsetEntryModal" tabindex="-1"
     role="dialog" aria-labelledby="updateDbsetEntryLabel" aria-hidden="true"
     is_update="0">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="updateDbsetEntryLabel">更新 databaseSet
                    entry</h4>
            </div>
            <div class="modal-body">
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label" style="width: 130px;">connectionString:</label>
                        <select id="databases2" class="span8"></select>
                    </div>
                </div>
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label" style="width: 130px;">Entry 名称:</label> <input
                            id="dbsetentryname2" class="span8 input-sm"
                            type="text" style="height: 30px">
                    </div>
                </div>
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label" style="width: 130px;">databaseType:</label>
                        <select id="databaseType2" class="span8">
                            <option value="Master">Master</option>
                            <option value="Slave">Slave</option>
                        </select>
                    </div>
                </div>
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label" style="width: 130px;">sharding:</label>
                        <textarea id="sharding2" class="span8" cols="4"></textarea>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <label id="updatedbsetentry_error_msg"
                       class="control-label popup_label" style="color: red;"></label>
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button id="save_updatedbsetentry" type="button"
                        class="btn btn-primary">保存
                </button>
            </div>
        </div>
    </div>
</div>
<!--End modal-->

<!--Begin modal-->
<div class="modal fade" id="addDbsetEntryModal" tabindex="-1"
     role="dialog" aria-labelledby="addDbsetEntryLabel" aria-hidden="true"
     is_update="0">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="addDbsetEntryLabel">添加 databaseSet entry</h4>
            </div>
            <div class="modal-body">
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label" style="width: 130px;">connectionString</label>
                        <select id="databases" class="span8">
                        </select>
                    </div>
                </div>
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label" style="width: 130px;">Entry 名称:</label> <input
                            id="dbsetentryname" class="span8 input-sm"
                            type="text" style="height: 30px">
                    </div>
                </div>
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label" style="width: 130px;">databaseType</label>
                        <select id="databaseType" class="span8">
                            <option value="Master">Master</option>
                            <option value="Slave">Slave</option>
                        </select>
                    </div>
                </div>
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label" style="width: 130px;">sharding:</label>
                        <textarea id="sharding" class="span8" cols="4"></textarea>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <label id="adddbsetentry_error_msg"
                       class="control-label popup_label" style="color: red;"></label>
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                <button id="save_adddbsetentry" type="button"
                        class="btn btn-primary">Save changes
                </button>
            </div>
        </div>
    </div>
</div>
<!--End modal-->

<!--Begin modal-->
<div class="modal fade" id="addDbsetModal" tabindex="-1" role="dialog"
     aria-labelledby="addDbsetLabel" aria-hidden="true" is_update="0">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="addDbsetLabel">添加 databaseSet</h4>
            </div>
            <div class="modal-body">
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label" style="width: 130px;">DB Mode Type:</label>
                        <select id="dbmodetype_dbsetmanage" class="span8">
                            <option value="no" selected="selected">请选择</option>
                            <option value="dalcluster">DAL Cluster</option>
                            <option value="titankey">TiTtanKey</option>
                        </select>
                    </div>
                </div>
                <div class="row-fluid" id="dbsetname-select-control" style="display: none">
                    <div class="control-group">
                        <label class="control-label popup_label" style="width: 130px;">databaseSet 名称:</label>
                        <select id="dbsetname-select" class="span8"></select>
                    </div>
                </div>
                <div class="row-fluid" id="dbsetname-input-control">
                    <div class="control-group">
                        <label class="control-label popup_label" style="width: 130px;">databaseSet 名称:</label>
                        <input id="dbsetname" class="span8 input-sm" type="text" style="height: 30px;">
                    </div>
                </div>
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label" style="width: 130px;">数据库类型:</label>
                        <select id="provider" class="span8">
                            <option value="">请选择</option>
                            <option value="sqlProvider">SQLServer</option>
                            <option value="mySqlProvider">MySQL</option>
                        </select>
                        <input id="dbtype-dbset-manege" value="MySQL" class="span8 input-sm" type="text" style="height: 30px;display: none" readonly="readonly">
                    </div>
                </div>
                <div class="row-fluid" id="strategy-dbset-manage">
                    <div class="control-group">
                        <label class="control-label popup_label" style="width: 130px;">shardingStrategy:</label>
                        <textarea id="shardingStrategy" class="span12 popup_text" cols="4"></textarea>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <label id="adddbset_error_msg" class="control-label popup_label"
                       style="color: red;"></label>
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button id="save_adddbset" type="button" class="btn btn-primary">保存</button>
            </div>
        </div>
    </div>
</div>
<!--End modal-->

<!--Begin modal-->
<div class="modal fade" id="updateDbsetModal" tabindex="-1"
     role="dialog" aria-labelledby="updateDbsetLabel" aria-hidden="true"
     is_update="0">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="updateDbsetLabel">更新 databaseSet</h4>
            </div>
            <div class="modal-body">
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label" style="width: 130px;">databaseSet
                            名称:</label> <input id="dbsetname2" class="span8 input-sm" type="text"
                                               style="height: 30px">
                    </div>
                </div>
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label" style="width: 130px;">数据库类型:</label>
                        <select id="provider2" class="span8">
                            <option value="sqlProvider">SQLServer</option>
                            <option value="mySqlProvider">MySQL</option>
                        </select>
                    </div>
                </div>
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label" style="width: 130px;">shardingStrategy:</label>
                        <textarea id="shardingStrategy2" class="span12 popup_text"
                                  cols="4"></textarea>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <label id="updatedbset_error_msg" class="control-label popup_label"
                       style="color: red;"></label>
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button id="save_updatedbset" type="button" class="btn btn-primary">保存</button>
            </div>
        </div>
    </div>
</div>
<!--End modal-->

<!--Begin modal-->
<div class="modal fade" id="dalConfigDemoModal" tabindex="-1"
     role="dialog" aria-labelledby="dalConfigDemoLabel" aria-hidden="true"
     is_update="0">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="dalConfigDemoLabel">Dal.config Demo</h4>
            </div>
            <div class="modal-body">
                <div class="row-fluid">
                    <div class="control-group">
                        <img alt="" src="/static/images/dal_config.jpg">
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
            </div>
        </div>
    </div>
</div>
<!--End modal-->

<!--Begin modal-->
<div class="modal fade" id="configTemplateModal" tabindex="-1"
     role="dialog" aria-labelledby="configTemplateLabel" aria-hidden="true"
     is_update="0">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="configTemplateLabel">配置模板</h4>
            </div>
            <div class="modal-body">
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label" style="width: 130px;">配置类型:</label> <select
                            id="configType" class="span8">
                    </select>
                    </div>
                    <div class="control-group">
                        <label class="control-label popup_label" style="width: 130px;">语言:</label>
                        <select id="languageType" class="span8">
                        </select>
                    </div>
                </div>
                <div class="row-fluid">
                    <div class="control-group">
                        <label class="control-label popup_label" style="width: 130px;">配置模板:</label>
                        <textarea id="txt_configtemplate" class="span12 popup_text"
                                  cols="10"></textarea>
                        <input id="txt_id" type="text"/>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <label id="configtemplate_error_msg"
                       class="control-label popup_label" style="color: red;"></label>
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button id="save_configtemplate" type="button"
                        class="btn btn-primary">保存
                </button>
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
<script src="/static/js/dbsetsmanage.js?codegen=${version}"></script>
</body>
</html>
