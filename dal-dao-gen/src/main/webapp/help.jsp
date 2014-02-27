<%@page pageEncoding="UTF-8"%>
<%@ page import="org.jasig.cas.client.util.AssertionHolder" %>
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
      <!-- Bootstrap core CSS -->
      <link href="/static/bootstrap/css/bootstrap.min.css" rel="stylesheet">
      <link href="/static/bootstrap/css/bootstrap-responsive.min.css" rel="stylesheet">
      <link href="/static/w2ui/w2ui-1.3.min.css" rel="stylesheet"/>
      <link href="/static/font-awesome/css/font-awesome.css" rel="stylesheet">
      <link href="/static/css/common.css" rel="stylesheet">
      <style type="text/css">
         body {
         /*padding-top: 32px;*/
         }
      </style>
      <!-- Documentation extras -->
      <!-- 
         <link href="../css/docs.css" rel="stylesheet">
         -->
      <!-- 
         <link href="../css/pygments-manni.css" rel="stylesheet">
         -->
      <!--[if lt IE 9]>
      <script src="./docs-assets/js/ie8-responsive-file-warning.js"></script>
      <![endif]-->
      <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
      <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
      <![endif]-->
      <!-- Favicons -->
      <link rel="shortcut icon" href="/static/images/favicon.ico">
   </head>
   <body>
      <!-- Docs master nav -->
      <div class="dal-navbar navbar navbar-inverse navbar-fixed-top" role="banner">
         <div class="navbar-header">
            <a href="/">
            <img class="logo" src="/static/images/logo.png" style="padding:5px;float:left;">
            </a>
         </div>
         <div class="collapse navbar-collapse in dal-navbar-collapse" role="navigation">
            <ul class="nav navbar-nav">
               <li>
                  <a href="index.jsp">数据访问层生成器</a>
               </li>
               <li>
                  <a href="file.jsp">已生成代码预览</a>
               </li>
               <li class="active">
                  <a href="help.jsp">帮助</a>
               </li>
            </ul>
            <ul class="nav navbar-nav pull-right">
               <li class="dropdown user">
                  <a href="javascript:;" class="dropdown-toggle" data-toggle="dropdown" data-hover="dropdown" data-close-others="true">
                  <span class="username">
                  <%=AssertionHolder.getAssertion().getPrincipal().getAttributes().get("sn")%>
                  </span>
                  <i class="fa fa-angle-down">
                  </i>
                  </a>
                  <ul class="dropdown-menu">
                     <li>
                        <a href="/logout.jsp">
                        <i class="fa fa-power-off">
                        </i>
                        Log Out
                        </a>
                     </li>
                  </ul>
               </li>
            </ul>
         </div>
      </div>

      <div id="main_layout">
         <div class="row-fluid">
            <div class="offset3">
         <a href="http://conf.ctripcorp.com/pages/viewpage.action?pageId=32081284">帮助文档</a>
      </div>
      </div>
       <div class="row-fluid">
         <div class="offset3">
         <a href="mailto:gawu@ctrip.com">Dal for .NET（吴广安）</a>
         </div>
         </div>
         <div class="row-fluid">
            <div class="offset3">
         <a href="mailto:jhhe@ctrip.com">Dal for JAVA（赫杰辉）</a>
         </div>
         </div>
      </div>

      
      <!-- JS and analytics only. -->
      <!-- Bootstrap core JavaScript================================================== -->
      <!-- Placed at the end of the document so the pages load faster -->
      <script src="/static/jquery/jquery-1.10.2.min.js"></script>
      <script src="/static/bootstrap/js/bootstrap.min.js"></script>
      <script src="/static/w2ui/w2ui-1.3.js"></script>
      <script src="/static/jquery/jquery.blockui.min.js"></script>
      <script src="/static/js/sprintf.js"></script>
   </body>
</html>
