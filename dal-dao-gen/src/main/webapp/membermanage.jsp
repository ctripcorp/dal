<%@page pageEncoding="UTF-8"%>
<%
	String codegenpageflag = Long.toString(System.currentTimeMillis());
	request.setAttribute("codegenpageflag", codegenpageflag);
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
      <!-- Bootstrap core CSS -->
      <link href="/static/bootstrap/css/bootstrap.min.css?codegen=${codegenpageflag}" rel="stylesheet">
      <link href="/static/bootstrap/css/bootstrap-responsive.min.css?codegen=${codegenpageflag}" rel="stylesheet">
      <link href="/static/w2ui/w2ui-1.3.2.min.css?codegen=${codegenpageflag}" rel="stylesheet"/>
      <link rel="stylesheet" href="/static/jstree/themes/default/style.min.css?codegen=${codegenpageflag}" />
      <link href="/static/font-awesome/css/font-awesome.css?codegen=${codegenpageflag}" rel="stylesheet">
      <link href="/static/css/multiple-select.css?codegen=${codegenpageflag}" rel="stylesheet">
      <link href="/static/css/selectize.bootstrap3.css?codegen=${codegenpageflag}" rel="stylesheet">
      <link href="/static/css/common.css?codegen=${codegenpageflag}" rel="stylesheet">
     
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
      	<!-- Loading Flat UI -->
	    <link href="/static/Flat-UI-master/css/flat-ui.css?codegen=${codegenpageflag}" rel="stylesheet">
	    <link href="/static/Flat-UI-master/css/demo.css?codegen=${codegenpageflag}" rel="stylesheet">
	
	    <!-- HTML5 shim, for IE6-8 support of HTML5 elements. All other JS at the end of file. -->
	    <!--[if lt IE 9]>
	      <script src="/static/Flat-UI-master/js/html5shiv.js"></script>
	      <script src="/static/Flat-UI-master/js/respond.min.js"></script>
	    <![endif]-->
   </head>
   <body>
      <!-- Docs master nav -->
      <%@ include file="header.jsp"%>
      
      <div id="main_layout"></div>
      <!--Begin modal-->
      <div class="modal fade" id="memberModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" is_update="0">
         <div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-hidden="true">&times;</button>
					<h4 class="modal-title" id="myModalLabel">添加组员</h4>
				</div>
				<div class="modal-body">
					<div class="row-fluid">
						<div class="control-group">
							<label class="control-label popup_label">用户列表：</label>
							<select id="members" class="span8">
							</select>&nbsp;
							<a href="#" class="ctip" data-toggle="tooltip"
								data-placement="bottom" title=""
								data-original-title="不在列表中的User，请先登录系统。"> <img
								class="helpicon" src="/static/images/help.jpg">
							</a>
						</div>
					</div>
					<div class="row-fluid">
						<div class="control-group">
							<label class="control-label popup_label">组员角色：</label>
							<select id="user_role" class="span8">
								<option value="1">Admin</option>
								<option value="2">Limited</option>
							</select>&nbsp;
							<a href="#" class="ctip" data-toggle="tooltip"
								data-placement="bottom" title=""
								data-original-title="1、Admin权限的用户可以完全使用组内资源.<br/>
								2、Limited权限的用户，可以使用组内的资源，但是生成代码需要通过审批."> 
								<img class="helpicon" src="/static/images/help.jpg"/>
							</a>
						</div>
					</div>
					<div class="row-fluid">
						<div class="control-group">
							<label class="control-label popup_label">允许管理组员：</label>
							<input id="allowAddUser" type="checkbox" checked="true">
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<label id="error_msg" class="control-label popup_label"
						style="color: red;"></label>
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button id="save_member" type="button" class="btn btn-primary">保存</button>
				</div>
			</div>
		</div>
      </div>
      <!--End modal-->
      
      <div class="modal fade" id="updateUserModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" is_update="0">
         <div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-hidden="true">&times;</button>
					<h4 class="modal-title" id="myModalLabel">权限修改</h4>
				</div>
				<div class="modal-body">
					<div class="row-fluid">
						<div class="control-group">
							<label class="control-label popup_label">组员姓名：</label>
							<span id="user_name" class="label label-info" style="margin-left:15px">Info</span>
						</div>
					</div>
					<div class="row-fluid">
						<div class="control-group">
							<label class="control-label popup_label">组员角色：</label>
							<select id="up_user_role" class="span8">
								<option value="1">Admin</option>
								<option value="2">Limited</option>
							</select>&nbsp;
							<a href="#" class="ctip" data-toggle="tooltip"
								data-placement="bottom" title=""
								data-original-title="1、Admin权限的用户可以完全使用组内资源.<br/>
								2、Limited权限的用户，可以使用组内的资源，但是生成代码需要通过审批."> 
								<img class="helpicon" src="/static/images/help.jpg"/>
							</a>
						</div>
					</div>
					<div class="row-fluid">
						<div class="control-group">
							<label class="control-label popup_label">允许管理组员：</label>
							<input id="up_allowAddUser" type="checkbox" checked="true">
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<label id="up_error_msg" class="control-label popup_label"
						style="color: red;"></label>
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button id="save_up_member" type="button" class="btn btn-primary">保存</button>
				</div>
			</div>
		</div>
      </div>
      
    <!-- JS and analytics only. -->
	<!-- Bootstrap core JavaScript================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	<script src="/static/jquery/jquery-1.10.2.min.js?codegen=${codegenpageflag}"></script>
	<script src="/static/bootstrap/js/bootstrap.min.js?codegen=${codegenpageflag}"></script>
	<script src="/static/w2ui/w2ui-1.3.2.min.js?codegen=${codegenpageflag}"></script>
	<script src="/static/jstree/jstree.js?codegen=${codegenpageflag}"></script>
	<script src="/static/jquery/jquery.blockui.min.js?codegen=${codegenpageflag}"></script>
	<script src="/static/js/sprintf.js?codegen=${codegenpageflag}"></script>
	<script src="/static/jquery/jquery.multiple.select.js?codegen=${codegenpageflag}"></script>
	<script src="/static/js/selectize.min.js?codegen=${codegenpageflag}"></script>
	<script src="/static/js/cblock.js?codegen=${codegenpageflag}"></script>

	<script src="/static/js/header.js?codegen=${codegenpageflag}"></script>
	<script src="/static/js/membermanage.js?codegen=${codegenpageflag}"></script>
   </body>
</html>
