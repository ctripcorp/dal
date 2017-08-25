<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="com.ctrip.platform.dal.daogen.utils.Configuration" %>
<%
    String version = Configuration.get("version");
    request.setAttribute("version", version);
%>
<html lang="zh-CN">
<head>
    <style type="text/css">
        .margin-left {
            margin-left: 20px;
        }

        .margin-top {
            margin-top: 10px;
        }

        .text-area-width {
            width: 640px !important;
        }

        .button-margin {
            margin-top: 70px;
            margin-left: 30px;
            margin-right: 30px;
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
</head>
<body>
<div style="display:inline;">
    <div class="margin-left margin-top" style="float: left">
        <label for="encrypt">加密串：</label>
        <textarea id="encrypt" class="form-control text-area-width" rows="6"></textarea>
    </div>
    <div class="button-margin " style="float: left">
        <input id="btnDecrypt" class="btn btn-default" type="button" value="解密">
        <input id="btnClear" class="btn btn-default margin-left" type="button" value="清空">
    </div>
    <div class="margin-top" style="float: left">
        <label for="decrypt">解密串：</label>
        <textarea id="decrypt" class="form-control text-area-width" rows="6"></textarea>
    </div>
</div>
<script src="/static/jquery/jquery-1.10.2.min.js?codegen=${version}"></script>
<script src="/static/bootstrap/js/bootstrap.min.js?codegen=${version}"></script>
<script src="/static/js/decrypt.js?codegen=${version}"></script>
</body>
</html>
