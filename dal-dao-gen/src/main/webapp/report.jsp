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
            width: 600px;
            height: 480px;
        }

        .table-size {
            width: 1000px;
            height: 640px;
        }

        .display-none {
            display: none;
        }

        .float-left {
            float: left;
        }

        .scroll {
            overflow: auto;
        }

        .font-size {
            font-size: 90% !important;
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
<div class="panel">
    <div class="panel-heading">
        <div id="divLoading" class="ld ld-ring ld-cycle"></div>
        <div id="divDept" class="container-fluid bg-info">
        </div>
        <div id="divVersion" class="container-fluid bg-info">
        </div>
    </div>
    <div class="panel-body">
        <div id="divPie" class="pie float-left">
        </div>
        <div id="divTable" class="float-left scroll table-size display-none">
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
<script src="/static/jquery/jquery-1.10.2.min.js?codegen=${version}"></script>
<script src="/static/bootstrap/js/bootstrap.min.js?codegen=${version}"></script>
<script src="/static/js/report.js?codegen=${version}"></script>
<script src="/static/js/echarts.min.js?codegen=${version}"></script>
<script src="/static/js/sprintf.js?codegen=${version}"></script>
</body>
</html>