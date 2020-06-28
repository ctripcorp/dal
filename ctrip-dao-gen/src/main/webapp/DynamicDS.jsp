<%--
  Created by IntelliJ IDEA.
  User: taochen
  Date: 2019/7/2
  Time: 12:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="com.ctrip.platform.dal.daogen.utils.Configuration" %>
<%
    String version = Configuration.get("version");
    request.setAttribute("version", version);
%>
<!DOCTYPE html>
<html lang="zh-CN">

<head>
    <style type="text/css">

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
    <ul class="nav nav-tabs" role="tablist">
        <li role="presentation" class="active" id="titanKeyReportTab">
            <a href="#titanKeyReport" aria-controls="titanKeyReport" role="tab" data-toggle="tab">TitanKey IP直连统计</a>
        </li>
        <li role="presentation" id="weekReport">
            <a href="#switchReport" aria-controls="switchReport" role="tab" data-toggle="tab">TitanKey切换报表</a>
        </li>
        <%--<li role="presentation" id="hourReport">--%>
            <%--<a href="#dynamicDS" aria-controls="dynamicDS" role="tab" data-toggle="tab">DAL 动态数据源切换统计</a>--%>
        <%--</li>--%>
    </ul>

    <div class="tab-content">
        <div role="tabpanel" class="tab-pane active" id="titanKeyReport">
            <div class="panel">
                <div class="panel-heading">
                    <%--<p style="font-size: 20px;color:#FF0000;margin-left: 20px">由于Cat限流策略，统计数据显示可能会比较慢！！！</p>--%>
                    <%--<div id="viewOneWeekData" >--%>
                        <%--<button id="viewTitanKeyButton" style="margin-left: 12px" class="font-size">查看数据</button>--%>
                    <%--</div>--%>
                    <div style="margin-left: 20px;margin-top: 10px">
                        数据更新日期：<span id="statisticsDate" class="bg-success"></span>
                    </div>
                    <div class="panel-body">
                        <div><span id="loadingSpan3" style="font-size: 20px;margin-left: 20px"></span></div>
                        <%--<div id="divTable3" class="display-none">--%>
                            <%--<div class="scroll table-size">--%>
                                <%--<table id="tableTitanKeyOneDay" class="table table-striped table-bordered">--%>
                                    <%--<thead>--%>
                                    <%--<tr>--%>
                                        <%--<th>TitanKey总数</th>--%>
                                        <%--<th>使用MySql数量</th>--%>
                                        <%--<th>使用SqlServer数量</th>--%>
                                        <%--<th>IP直连数量</th>--%>
                                        <%--<th>IP直连MySql数量</th>--%>
                                        <%--<th>IP直连SqlServer数量</th>--%>
                                    <%--</tr>--%>
                                    <%--</thead>--%>
                                    <%--<tbody></tbody>--%>
                                <%--</table>--%>
                            <%--</div>--%>

                            <div class="scroll table-size">
                                <table id="tableTitanKeyDirectConnect" class="table table-striped table-bordered" style="width: 600px">
                                    <thead>
                                    <tr>
                                        <th style="width: 120px">TitanKey总数</th>
                                        <th style="width: 150px">IP直连数量</th>
                                        <th style="width: 150px">IP直连占比</th>
                                    </tr>
                                    </thead>
                                    <tbody></tbody>
                                </table>
                            </div>
                            <div class="scroll table-size">
                                <table id="tableTitanKeyDirectConnectMySql" class="table table-striped table-bordered" style="width: 600px">
                                    <thead>
                                    <tr>
                                        <th style="width: 120px">MySql总数</th>
                                        <th style="width: 150px">IP直连MySql数量</th>
                                        <th style="width: 150px">IP直连MySql占比</th>
                                    </tr>
                                    </thead>
                                    <tbody></tbody>
                                </table>
                            </div>
                            <div class="scroll table-size">
                                <table id="tableTitanKeyDirectConnectSqlServer" class="table table-striped table-bordered" style="width: 600px">
                                    <thead>
                                    <tr>
                                        <th style="width: 120px">SqlServer总数</th>
                                        <th style="width: 150px">IP直连SqlServer数量</th>
                                        <th style="width: 150px">IP直连SqlServer占比</th>
                                    </tr>
                                    </thead>
                                    <tbody></tbody>
                                </table>
                            </div>
                            <div class="scroll table-size">
                                <div><span id="abnormalTitanKeyTableTitle" style="font-size: 20px">TitanKey配置异常统计(TitanKey配置serverIp不是ip或者serverName不是域名)</span></div>
                                <table id="tableAbnormalTitanKey" class="table table-striped table-bordered" style="width: 400px">
                                    <thead>
                                    <tr>
                                        <th>TitanKey     </th>
                                        <th>ServerIp</th>
                                        <th>ServerName   </th>
                                    </tr>
                                    </thead>
                                    <tbody></tbody>
                                </table>
                            </div>
                        <div class="scroll table-size">
                            <div><span id="unUseDynamicDSTitanKeyTableTitle" style="font-size: 20px">未使用Dal动态数据源的TitanKey统计(使用域名访问数据库)</span></div>
                            <table id="tableUnUseDynamicDSTitanKey" class="table table-striped table-bordered" style="width: 400px">
                                <thead>
                                <tr>
                                    <th>序号</th>
                                    <th>TitanKey     </th>
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

    <div class="tab-content">
        <div role="tabpanel" class="tab-pane" id="switchReport">
            <div class="panel">
                <div class="panel-heading">
                    <%--<p style="font-size: 20px;color:#FF0000;margin-left: 20px">由于Cat限流策略，统计数据显示可能会比较慢！！！</p>--%>
                    <div id="viewOneWeekData" >
                        <input type="datetime-local" id="settingStartDate" style="margin-left: 20px" data-checkTime=""/>
                        <span style="margin-left: 10px">-</span>
                        <input type="datetime-local" id="settingEndDate" style="margin-left: 10px" data-checkTime=""/>
                        <button id="viewRangeButton" style="margin-left: 10px" class="font-size">查看数据</button>
                    </div>
                    <div>
                        <span id="dateRange" style="margin-left: 20px;margin-top: 50px" class="bg-success"></span>
                    </div>
                    <div class="panel-body">
                        <div><span id="loadingSpan2" style="font-size: 20px;margin-left: 20px"></span></div>
                        <div id="divTable2" class="display-none">
                            <div class="scroll table-size">
                                <table id="tableDynamicDSWeek" class="table table-striped table-bordered">
                                    <thead>
                                    <tr>
                                        <th>序号</th>
                                        <th>TitanKey</th>
                                        <th>TitanKey切换次数</th>
                                        <th>客户端AppId数量</th>
                                        <th>客户端IP总数</th>
                                        <th>客户端切换总次数</th>
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

    <div class="tab-content">
        <div role="tabpanel" class="tab-pane" id="dynamicDS">
            <div class="panel">
                <div class="panel-heading">
                    <%--<p style="font-size: 20px;color:#FF0000;margin-left: 20px">由于Cat限流策略，统计数据显示可能会比较慢！！！</p>--%>
                    <div id="viewLatestOneHourData" >
                        <input type="datetime-local" id="settingDate" style="margin-left: 20px" data-checkTime=""/>
                        <input id="checkTitanKey" style="margin-left: 20px;width: 250px" placeholder="titanKey1,titanKey2,titanKey3..."/>
                        <button id="viewButton" style="margin-left: 10px" class="font-size">查看数据</button>
                    </div>
                    <div>
                        <span id="toDate" style="margin-left: 20px;margin-top: 50px" class="bg-success"></span>
                    </div>
                    <div class="panel-body">
                        <div><span id="loadingSpan" style="font-size: 20px;margin-left: 20px"></span></div>
                        <div id="divTable" class="display-none">
                            <p class="bg-success padding-left-right">
                                <span id="spanCount"></span>
                                <span id="spanLastUpdate" class="float-right"></span>
                            </p>
                            <p>
                                <span id="startTime"></span>
                                <span id="endTime"></span>
                            </p>

                            <div class="scroll table-size">
                                <%--<div id="divLoading" class="ld ld-ring ld-cycle" style="margin-left: 50%;visibility:hidden"></div>--%>
                                <table id="tableDynamicDS" class="table table-striped table-bordered">
                                    <thead>
                                    <tr>
                                        <th id="">TitanKey</th>
                                        <th>TitanKey SwitchCount</th>
                                        <th>Operation</th>
                                        <th>Client AppId</th>
                                        <th>Client IP Count</th>
                                        <%--<th>Related modules</th>--%>
                                        <%--<th>Key Point</th>--%>
                                        <%--<th>Start Time</th>--%>
                                        <%--<th>End Time</th>--%>
                                        <th>AppID SwitchCount</th>
                                        <th>AppID SuccessCount</th>
                                    </tr>
                                    </thead>
                                    <tbody></tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                    <div id="details">
                    </div>
                </div>
            </div>
        </div>
    </div>


    <script src="/static/jquery/jquery-1.10.2.min.js?codegen=${version}"></script>
    <script src="/static/bootstrap/js/bootstrap.min.js?codegen=${version}"></script>
    <script src="/static/js/DynamicDS.js?codegen=${version}"></script>
    <script src="/static/js/echarts.min.js?codegen=${version}"></script>
    <script src="/static/js/sprintf.js?codegen=${version}"></script>
</body>
</html>
