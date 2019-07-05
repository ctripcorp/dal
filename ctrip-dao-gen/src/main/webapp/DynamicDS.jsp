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
    <title>Title</title>
</head>
<body>
    <ul class="nav nav-tabs" role="tablist">
        <li role="presentation" class="active">
            <a href="#dynamicDS" aria-controls="dynamicDS" role="tab" data-toggle="tab">DAL 数据源切换统计</a>
        </li>
    </ul>
    <div class="tab-content">
        <div role="tabpanel" class="tab-pane active" id="dynamicDS">
            <div class="panel">
                <div class="panel-heading">
                    <div id="divLoading" class="ld ld-ring ld-cycle"></div>
                    <div class="panel-body">
                        <div id="divTable" class="display-none">
                            <p class="bg-success padding-left-right">
                                <span id="spanCount"></span>
                                <span id="spanLastUpdate" class="float-right"></span>
                            </p>
                            <div class="scroll table-size">
                                <table id="tableDynamicDS" class="table table-striped table-bordered">
                                    <thead>
                                    <tr>
                                        <th id="">TitanKey</th>
                                        <th>App Ids</th>
                                        <th>Host IPs</th>
                                        <%--<th>Related modules</th>--%>
                                        <%--<th>Key Point</th>--%>
                                        <%--<th>Start Time</th>--%>
                                        <%--<th>End Time</th>--%>
                                        <th>Switch Count</th>
                                        <th>Success Count</th>
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
