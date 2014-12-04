<%@page pageEncoding="UTF-8"%>
<%
	String codegenpageflag = "1.3.2";
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
      <div class="modal fade" id="addDbModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" is_update="0">
         <div class="modal-dialog">
            <div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-hidden="true">&times;</button>
					<h4 class="modal-title" id="myModalLabel">Add New DataBase</h4>
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
								<label class="control-label popup_label" style="width: 130px;">DB Address:</label> 
								<input id="dbaddress" class="span8 input-sm" type="text">
							</div>
						</div>
						<div class="row-fluid">
							<div class="control-group">
								<label class="control-label popup_label" style="width: 130px;">DB Port:</label> 
								<input id="dbport" class="span8 input-sm" type="text" value="28747">
							</div>
						</div>
						<div class="row-fluid">
							<div class="control-group">
								<label class="control-label popup_label" style="width: 130px;">DB User:</label> 
								<input id="dbuser" class="span8 input-sm" type="text">
							</div>
						</div>
						<div class="row-fluid">
							<div class="control-group">
								<label class="control-label popup_label" style="width: 130px;">DB Password:</label> 
								<input id="dbpassword" class="span8 input-sm" type="text">
							</div>
						</div>
				    </div>
					<div id="add_new_db_step2" class="row-fluid">
						<div class="row-fluid">
							<div class="control-group">
								<label class="control-label popup_label" style="width: 130px;">DB Catalog:</label> 
								<select id="dbcatalog" class="span8"></select>
							</div>
						</div>
						<div class="row-fluid">
							<div class="control-group">
								<label class="control-label popup_label" style="width: 130px;">All-In-One Name:</label> 
								<input id="allinonename" class="span8 input-sm" type="text">
							</div>
						</div>
					</div>
					<div class="row-fluid">
						<div class="control-group">
							<label id="error_msg" class="control-label popup_label" style="color:red;"></label>
						</div>
					</div>
				</div>
				<div class="modal-footer">
               	  <button id="conn_test"  type="button" class="btn btn-success">连接测试</button>
                  <button id="add_new_db_next" type="button" class="btn btn-primary">下一步</button>
                  <button id="add_new_db_prev" type="button" class="btn btn-info">上一步</button>
                  <button id="add_new_db_save" type="button" class="btn btn-primary">保存</button>
               </div>
            </div>
         </div>
      </div>
      <!--End modal-->
      
      <!--Begin modal-->
      <div class="modal fade" id="updateDbModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" is_update="0">
         <div class="modal-dialog">
            <div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-hidden="true">&times;</button>
					<h4 class="modal-title" id="myModalLabel">Update DataBase</h4>
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
								<label class="control-label popup_label" style="width: 130px;">DB Address:</label> 
								<input id="dbaddress_up" class="span8 input-sm" type="text">
							</div>
						</div>
						<div class="row-fluid">
							<div class="control-group">
								<label class="control-label popup_label" style="width: 130px;">DB Port:</label> 
								<input id="dbport_up" class="span8 input-sm" type="text" value="28747">
							</div>
						</div>
						<div class="row-fluid">
							<div class="control-group">
								<label class="control-label popup_label" style="width: 130px;">DB User:</label> 
								<input id="dbuser_up" class="span8 input-sm" type="text">
							</div>
						</div>
						<div class="row-fluid">
							<div class="control-group">
								<label class="control-label popup_label" style="width: 130px;">DB Password:</label> 
								<input id="dbpassword_up" class="span8 input-sm" type="text">
							</div>
						</div>
				    </div>
					<div id="update_db_step2" class="row-fluid">
						<div class="row-fluid">
							<div class="control-group">
								<label class="control-label popup_label" style="width: 130px;">DB Catalog:</label> 
								<select id="dbcatalog_up" class="span8"></select>
							</div>
						</div>
						<div class="row-fluid">
							<div class="control-group">
								<label class="control-label popup_label" style="width: 130px;">All-In-One Name:</label> 
								<input id="allinonename_up" class="span8 input-sm" type="text">
							</div>
						</div>
					</div>
					<div class="row-fluid">
						<div class="control-group">
							<label id="update_error_msg" class="control-label popup_label" style="color:red;"></label>
						</div>
					</div>
				</div>
				<div class="modal-footer">
               	  <button id="update_conn_test"  type="button" class="btn btn-success">连接测试</button>
                  <button id="update_db_next" type="button" class="btn btn-primary">下一步</button>
                  <button id="update_db_prev" type="button" class="btn btn-info">上一步</button>
                  <button id="update_db_save" type="button" class="btn btn-primary">保存</button>
               </div>
            </div>
         </div>
      </div>
      <!--End modal-->
      
      <div class="modal fade" id="errorNoticeDiv" tabindex="-1" role="dialog" aria-labelledby="generateCodeProcessLabel" aria-hidden="true">
         <div class="modal-dialog">
            <div class="modal-content">
               <div class="modal-header">
                  <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
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
	<script src="/static/js/ajaxutil.js?codegen=${codegenpageflag}"></script>

	<script src="/static/js/header.js?codegen=${codegenpageflag}"></script>
	<script src="/static/js/dbview.js?codegen=${codegenpageflag}"></script>
	
   </body>
</html>
