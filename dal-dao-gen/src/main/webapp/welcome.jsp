

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
<link rel="shortcut icon" href="/static/images/favicon.ico">
<!-- Bootstrap core CSS -->
<link href="/static/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<link href="/static/bootstrap/css/bootstrap-responsive.min.css" rel="stylesheet">
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
	<div class="container">
		<h1 class="demo-section-title mbl pbl">Samples</h1>
		<div class="row demo-samples">
			<div class="col-xs-4">
				<div class="todo">
					<div class="todo-search">
						<span style="font-weight: bold;font-size: 18px">Video List</span>
					</div>
					<ul>
						<li class="todo-done">
							<div class="todo-icon fui-user"></div>
							<div class="todo-content">
								<h4 class="todo-name">
									Meet <strong>Adrian</strong> at <strong>6pm</strong>
								</h4>
								Times Square
							</div>
						</li>
						<li>
							<div class="todo-icon fui-list"></div>
							<div class="todo-content">
								<h4 class="todo-name">
									Chat with <strong>V.Kudinov</strong>
								</h4>
								Skype conference an 9 am
							</div>
						</li>
						<li>
							<div class="todo-icon fui-eye"></div>
							<div class="todo-content">
								<h4 class="todo-name">
									Watch <strong>Iron Man</strong>
								</h4>
								1998 Broadway
							</div>
						</li>
						<li>
							<div class="todo-icon fui-time"></div>
							<div class="todo-content">
								<h4 class="todo-name">
									Fix bug on a <strong>Website</strong>
								</h4>
								As soon as possible
							</div>
						</li>
					</ul>
				</div>
			</div>
			<!-- /todo list -->
			<div class="col-xs-8 demo-video">
		      <!--[if !IE]> -->
		        <video class="video-js" controls preload="auto" width="620" height="349" poster="/static/Flat-UI-master/images/video/poster.jpg" data-setup="{}">
		          <source src="./static/movie/big_buck_bunny.mp4" type="video/mp4">
		        </video>
		      <!-- <![endif]-->
		
		      <!--[if IE]>
		        <video class="video-js" controls preload="auto" width="620" height="349" poster="http://video-js.zencoder.com/oceans-clip.jpg" data-setup="{}">
		          <source src="http://video-js.zencoder.com/oceans-clip.mp4" type='video/mp4'/>
		          <source src="http://video-js.zencoder.com/oceans-clip.webm" type='video/webm'/>
		        </video>
		      <![endif]-->
		    </div> <!-- /video -->
		</div>
	
    </div>

	<!-- JS and analytics only. -->
	<!-- Bootstrap core JavaScript================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	<script src="/static/jquery/jquery-1.10.2.min.js"></script>
	<script src="/static/bootstrap/js/bootstrap.min.js"></script>
	<script src="/static/Flat-UI-master/js/jquery-ui-1.10.3.custom.min.js"></script>
    <script src="/static/Flat-UI-master/js/jquery.ui.touch-punch.min.js"></script>
    <script src="/static/Flat-UI-master/js/bootstrap-select.js"></script>
    <script src="/static/Flat-UI-master/js/bootstrap-switch.js"></script>
    <script src="/static/Flat-UI-master/js/flatui-checkbox.js"></script>
    <script src="/static/Flat-UI-master/js/flatui-radio.js"></script>
    <script src="/static/Flat-UI-master/js/jquery.tagsinput.js"></script>
    <script src="/static/Flat-UI-master/js/jquery.placeholder.js"></script>
    <script src="/static/Flat-UI-master/js/jquery.stacktable.js"></script>
    <script src="http://vjs.zencdn.net/4.3/video.js"></script>
    <script src="/static/Flat-UI-master/js/application.js"></script>
    
    <script src="/static/Flat-UI-master/js/jquery-ui-1.10.3.custom.min.js"></script>
    <script src="/static/Flat-UI-master/js/jquery.ui.touch-punch.min.js"></script>
    
    <script src="/static/js/header.js"></script>
</body>
</html>
