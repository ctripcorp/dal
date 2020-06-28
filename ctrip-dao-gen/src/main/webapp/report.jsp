<%@page pageEncoding="UTF-8" %>
<%@page import="com.ctrip.platform.dal.daogen.utils.Configuration" %>
<%
    String version = Configuration.get("version");
    request.setAttribute("version", version);
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <style type="text/css">
        .span-margin {
            margin-left: 5px !important;
            margin-right: 5px !important;
            margin-top: 5px !important;
            margin-bottom: 5px !important;
        }

        .cursor {
            cursor: pointer;
        }

        .pie {
            width: 100%;
            height: 480px;
        }

        .table-size {
            width: 100%;
            height: 480px;
        }

        .display-none {
            display: none;
        }

        .float-left {
            float: left;
        }

        .float-right {
            float: right;
        }

        .scroll {
            overflow: auto;
        }

        .font-size {
            font-size: 90% !important;
        }

        .margin-left {
            margin-left: 5px;
        }

        .padding-left-right {
            padding-left: 5px;
            padding-right: 5px;
        }
    </style>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>Ctrip DAO Generator</title>
    <link href="/static/images/favicon.ico" rel="shortcut icon" type="image/vnd.microsoft.icon"/>
    <link href="/static/bootstrap/css/bootstrap.min.css?codegen=${version}" rel="stylesheet">
    <link href="/static/bootstrap/css/bootstrap-responsive.min.css?codegen=${version}" rel="stylesheet">
    <link href="/static/css/loading.css?codegen=${version}" rel="stylesheet">
</head>
<body>
<div>
    <ul class="nav nav-tabs" role="tablist">
        <li role="presentation" class="active">
            <a href="#dalVersion" aria-controls="dalVersion" role="tab" data-toggle="tab">DAL 版本统计</a>
        </li>
        <li role="presentation" style="display: none;">
            <a id="anchorLocalDatasource" href="#dalLocalDatasource" aria-controls="dalLocalDatasource" role="tab"
               data-toggle="tab">DAL.local.datasource</a>
        </li>
    </ul>
    <div class="tab-content">
        <div role="tabpanel" class="tab-pane active" id="dalVersion">
            <div class="panel">
                <div class="panel-heading">
                    <div id="divLoading" class="ld ld-ring ld-cycle"></div>
                    <div id="divDept" class="container-fluid bg-info">
                    </div>
                    <div id="divVersion" class="container-fluid bg-info">
                    </div>
                    <div id="divCategory" class="container-fluid bg-info">
                        <div class="checkbox" class="margin-left">
                            数据库类型：
                            <label>
                                <input type="checkbox" id="chkSqlServer" checked="checked">SqlServer
                            </label>
                            &nbsp;
                            <label>
                                <input type="checkbox" id="chkMySql" checked="checked">MySql
                            </label>
                        </div>
                    </div>
                    <div id="divExport" class="container-fluid bg-info">
                        <span id="spanExport" class="label label-success span-margin cursor font-size">导出Excel</span>
                        <span id="spanForceFresh" class="label label-success span-margin cursor font-size"
                              style="display: none;">强制刷新</span>
                    </div>
                </div>
                <div class="panel-body">
                    <div id="divPie" class="pie">
                    </div>
                </div>
                <div class="panel-body">
                    <div id="divTable" class="display-none">
                        <p class="bg-success padding-left-right">
                            <span id="spanCount"></span>
                            <span id="spanLastUpdate" class="float-right"></span>
                        </p>
                        <div class="scroll table-size">
                            <table id="tableVersion" class="table table-striped table-bordered">
                                <thead>
                                <tr>
                                    <th id="thKey"></th>
                                    <th>App Id</th>
                                    <th>App Name</th>
                                    <th>Chinese Name</th>
                                    <th>Owner</th>
                                    <th>Owner Email</th>
                                </tr>
                                </thead>
                                <tbody></tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div role="tabpanel" class="tab-pane" id="dalLocalDatasource">
            <div class="panel">
                <div class="panel-heading">
                    <div id="divExport2" class="container-fluid bg-info">
                        <span id="spanRefresh" class="label label-success span-margin cursor font-size">刷新数据</span>
                        <span id="spanExport2" class="label label-success span-margin cursor font-size">导出Excel</span>
                    </div>
                </div>
                <div class="panel-body">
                    <div id="divLoading2" class="ld ld-ring ld-cycle"></div>
                    <div id="divTable2" class="display-none">
                        <p class="bg-success padding-left-right">
                            <span id="spanCount2"></span>
                        </p>
                        <div>
                            <table id="tableLocal" class="table table-striped table-bordered">
                                <thead>
                                <tr>
                                    <th>App Id</th>
                                    <th>BU</th>
                                    <th>App Name</th>
                                    <th>Chinese Name</th>
                                    <th>Owner</th>
                                    <th>Owner Email</th>
                                </tr>
                                </thead>
                                <tbody></tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="/static/jquery/jquery-1.10.2.min.js?codegen=${version}"></script>
<script src="/static/bootstrap/js/bootstrap.min.js?codegen=${version}"></script>
<script src="/static/js/report.js?codegen=${version}"></script>
<script src="/static/js/echarts.min.js?codegen=${version}"></script>
<script src="/static/js/sprintf.js?codegen=${version}"></script>
</body>
</html>