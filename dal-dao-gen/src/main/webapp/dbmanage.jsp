

<%@page pageEncoding="UTF-8"%>
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
      <link href="/static/w2ui/w2ui-1.3.2.min.css" rel="stylesheet"/>
      <link rel="stylesheet" href="/static/jstree/themes/default/style.min.css" />
      <link href="/static/font-awesome/css/font-awesome.css" rel="stylesheet">
      <link href="/static/css/multiple-select.css" rel="stylesheet">
      <link href="/static/css/selectize.bootstrap3.css" rel="stylesheet">
      <link href="/static/css/common.css" rel="stylesheet">
     
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
	    <link href="/static/Flat-UI-master/css/flat-ui.css" rel="stylesheet">
	    <link href="/static/Flat-UI-master/css/demo.css" rel="stylesheet">
	
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
      <div class="modal fade" id="dbModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" is_update="0">
         <div class="modal-dialog">
            <div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-hidden="true">&times;</button>
					<h4 class="modal-title" id="myModalLabel">Add a DataBase</h4>
				</div>
				<div class="modal-body">
					<div class="row-fluid">
						<div class="control-group">
							<label class="control-label popup_label" style="width: 140px;">All-In-One数据库：</label>
							<select id="databases" class="span8">
							</select>
						</div>
					</div>
					<div class="row-fluid">
						<div class="control-group">
							<label class="control-label popup_label" style="width: 140px;">备
								注:</label> <input id="comment" class="span8 input-sm" type="text">
						</div>
					</div>
				</div>
				<div class="modal-footer">
               	  <label id="error_msg" class="control-label popup_label" style="color:red;"></label>
                  <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                  <button id="save_group" type="button" class="btn btn-primary">Save changes</button>
               </div>
            </div>
         </div>
      </div>
      <!--End modal-->

      
      <!-- JS and analytics only. -->
	<!-- Bootstrap core JavaScript================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	<script src="/static/jquery/jquery-1.10.2.min.js"></script>
	<script src="/static/bootstrap/js/bootstrap.min.js"></script>
	<script src="/static/w2ui/w2ui-1.3.2.min.js"></script>
	<script src="/static/jstree/jstree.js"></script>
	<script src="/static/jquery/jquery.blockui.min.js"></script>
	<script src="/static/js/sprintf.js"></script>
	<script src="/static/jquery/jquery.multiple.select.js"></script>
	<script src="/static/js/selectize.min.js"></script>
	<script src="/static/js/cblock.js"></script>
	<script src="/static/js/ajaxutil.js"></script>

	<script src="/static/js/header.js"></script>
	<script src="/static/js/dbmanage.js"></script>
	
   </body>
</html>
