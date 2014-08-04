

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
      <link href="/static/bootstrap/css/bootstrap.css" rel="stylesheet">
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
	
	    <!-- HTML5 shim, for IE6-8 support of HTML5 elements. All other JS at the end of file. -->
	    <!--[if lt IE 9]>
	      <script src="/static/Flat-UI-master/js/html5shiv.js"></script>
	      <script src="/static/Flat-UI-master/js/respond.min.js"></script>
	    <![endif]-->
   </head>
  
   <body>
   
      <!-- Docs master nav -->
      <%@ include file="header.jsp"%>
      <div id="main_layout">
      </div>

      <!--Begin project modal-->
      <div class="modal fade" id="projectModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" is_update="0">
         <div class="modal-dialog">
            <div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-hidden="true">&times;</button>
					<h4 class="modal-title" id="myModalLabel">项目管理</h4>
				</div>
				<div class="modal-body">
                  <div class="row-fluid">
                     <div class="control-group">
                        <input id="project_id" type="hidden" value="">
                        <label class="control-label popup_label" style="width: 110px;">项目名称</label>
                        <input id="name" class="span9 input-sm" type="text">
                     </div>
                  </div>
                  <div class="row-fluid">
                     <div class="control-group">
                        <label class="control-label popup_label" style="width: 110px;">命名空间</label>
                        <input id="namespace" class="span9 input-sm" type="text">
                     </div>
                  </div>
                  <div class="row-fluid">
                     <div class="control-group">
                        <label class="control-label popup_label" style="width: 110px;">Dal.config Name</label>
                        <input id="dalconfigname" class="span9 input-sm" type="text">
                     </div>
                  </div>
               </div>
               <div class="modal-footer">
               		<label id="proj_error_msg" class="control-label popup_label" style="color:red;"></label>
               	   <a href="#" class="ctip" data-toggle="tooltip"
						data-placement="top" title=""
						data-original-title="代码生成器按照项目来组织代码，因此，同一个用户可以新建多个项目，每个项目生成的代码相互独立，互不干扰。"> <img class="helpicon"
						src="/static/images/help.jpg">
					</a>
                  <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                  <button id="save_proj" type="button" class="btn btn-primary">保存</button>
               </div>
            </div>
         </div>
      </div>
      <!--End project modal-->

      <div class="modal fade" id="generateCode" tabindex="-1" role="dialog" aria-labelledby="generateCodeLabel" aria-hidden="true">
         <div class="modal-dialog">
            <div class="modal-content">
               <div class="modal-header">
                  <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                  <h4 class="modal-title">生成方式，如果有删除，请选择重新生成</h4>
               </div>
               <div class="modal-body">
                  <div class="row-fluid">
                     <div class="control-group">
                           <label class="control-label popup_label">语言：</label>
                       <select id="regen_language" class="span9 pupup_text">
                           <option value="cs">C#代码</option>
                           <option value="java">Java代码</option>
                        </select>
                     </div>
                  </div>
                  <div class="row-fluid"> 
                     <div class="control-group">
                           <label class="control-label popup_label">方式：</label>
                       <select id="regenerate" class="span9 pupup_text">
                       		<option value="regenerate">重新生成</option>
                            <option value="increment_gen">增量生成，仅生成被修改的DAO</option>
                        </select>
                     </div>
                  </div>
                  <div class="row-fluid useNewPojo">
                     <label class="popup_label"><input id="newPojo" type="checkbox" checked="checked">生成代码时附带数据库中的类型（DAL Fx>V1.2.0.6）</label>
                  </div>
               </div>
               <div class="modal-footer">
                  <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                  <button id="generate_code" type="button" class="btn btn-primary">生成</button>
               </div>
            </div>
            <!-- /.modal-content -->
         </div>
         <!-- /.modal-dialog -->
      </div>
	  <div class="modal fade" id="generateCodeProcessDiv" tabindex="-1" role="dialog" aria-labelledby="generateCodeProcessLabel" aria-hidden="true">
         <div class="modal-dialog">
            <div class="modal-content">
               <div class="modal-header">
                  <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                  <h4 class="modal-title">代码生成进度</h4>
               </div>
               <div class="modal-body">
                  <div class="progress progress-info progress-striped active">
				  	<div class="progress-bar" role="progressbar" style="width:0%;"></div>
				  </div>
				  <div id="generateCodeProcessMess" style="font-size:16px;padding-top:10px">
				  		正在初始化...
				  </div>
               </div>
            </div>
         </div>
      </div>
      <!-- /.modal -->
      
      <div class="modal fade" id="generateCodeProcessErrorDiv" tabindex="-1" role="dialog" aria-labelledby="generateCodeProcessLabel" aria-hidden="true">
         <div class="modal-dialog">
            <div class="modal-content">
               <div class="modal-header">
                  <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                  <h4 class="modal-title">错误提示</h4>
               </div>
               <div class="modal-body">
				  <div class="row-fluid">
						<div class="control-group">
							<label id="generateCodeProcessErrorMess" class="control-label popup_label"></label>
						</div>
					</div>
               </div>
               <div class="modal-footer">
               	  <a href="#" class="ctip" data-toggle="tooltip"
						data-placement="top" title="" html="1"
						data-original-title="一键补全，功能描述：<br/>
						1、将当前Project缺少的数据库自动添加到所属DAL Team。<br/>
						2、将当前Project缺少的逻辑数据库自动新增并添加到所属DAL Team。<br/>
						"> <img class="helpicon"
						src="/static/images/help.jpg">
					</a>
               	  <button id="add_lack_dbset" type="button" class="btn btn-primary">一键补全</button>
                  <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
               </div>
            </div>
         </div>
      </div>
      
      <!--Begin wizard-->
      
      <div class="modal fade" id="page1" tabindex="-1" role="dialog" aria-labelledby="page1_label" aria-hidden="true" is_update="0">
         <div class="modal-dialog">
            <div class="modal-content">
               <div class="modal-header">
                  <label class="modal-title" id="page1_label" style = "font-size: 25px;font-weight: bold" >DAO生成向导</label>
                 <a style="margin-left:359px !important;margin-right:0px !important" href="#" class="ctip" data-toggle="tooltip" data-placement = "bottom"
							data-original-title="1、如果在列表中没有找到你需要的逻辑数据库，请到逻辑数据库管理界面追加。<br/>
							2、目前，DAO代码生成方式有三种：<br/>
							&nbsp;2.1、标准DAO：在这种模式下面，我们只需要选择数据库、表、视图、存储过程、视图，之后将生成对应的增、删、改、查的代码。<br/>
							&nbsp;2.2、构建SQL（生成的代码绑定到模板）：在这种模式下面，我们需要选择数据库、表，以及将要生成DAO类型（增、删、改、查之一），再选择对应的字段，最后构建出一个SQL语句。<br/>
							&nbsp;2.3、自定义SQL：在这种模式下面，我们可以自定义查询SQL语句，指定生成的DAO类名、实体类名、方法名。<br/>
							"> <img style="margin-bottom:15px;height:25px;width:25px" class="helpicon" id="help"
							src="/static/images/help.jpg">
					</a>
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true" style="margin-top:3px">&times;</button>
               </div>
               <div class="modal-body" style="position: relative;overflow: auto;width: auto;">
                  <div class="steps step1 row-fluid">
                     <div class="row-fluid">
                        <div class="control-group">
                           <label class="control-label popup_label" style="width:130px;">逻辑数据库：</label>
                           <select id="databases" class="span8">
                           </select>
                        </div>
                     </div>
                     
                     <div class="row-fluid">
                        <div class="control-group">
                           <label class="control-label popup_label" style="width:130px;">DAO代码生成方式:</label>
						   <select id="gen_style" class="span8">
                              <option value="table_view_sp">标准DAO</option>
                              <option value="auto">构建SQL</option>
                              <option value="sql"> 自定义SQL</option>
                           </select>
                        </div>
					 </div>
                  <div class="row-fluid">
                     	<div class="control-group">
                           <label class="control-label popup_label" style="width:130px;">方法功能描述：</label>
                           <textarea id="comment" class="span12 popup_text input-sm" rows="4"></textarea>
                        </div>
                     </div>
                  </div>
                  
                  <div class="steps step2-1 row-fluid">
                     <div class="row-fluid">
                        <div class="control-group">
                           <label class="control-label popup_label">选择表：</label>
                           <select id="table_list" multiple="multiple" class="popup_text" style="width:420px;">
                           </select>
                        </div>
                     </div>
                     <div class="row-fluid">
                        <div class="control-group">
                           <label class="control-label popup_label">选择视图：</label>
                           <select id="view_list" multiple="multiple" class="popup_text" style="width:420px;">
                           </select>
                        </div>
                     </div>
                     <div class="row-fluid mysql_hide">
                        <div class="control-group">
                           <label class="control-label popup_label">选择存储过程：</label>
                           <select id="sp_list" multiple="multiple" class="popup_text" style="width:420px;">
                           </select>
                        </div>
                     </div>
                     <div class="row-fluid">
                        <div class="control-group">
                           <label class="control-label popup_label">生成时移除前缀：</label>
                           <input type="text" id="prefix" class="span9 popup_text input-sm">
                        </div>
                     </div>
                     <div class="row-fluid">
                        <div class="control-group">
                           <label class="control-label popup_label">生成时加上后缀：</label>
                           <input type="text" id="suffix" class="span9 popup_text input-sm">
                        </div>
                     </div>
                     <div class="row-fluid mysql_hide">
                        <label class="popup_label"><input id="cud_by_sp" type="checkbox" checked="true">增删改使用SPA或SP3（Sql Server请勾选，MySql请去除）</label>
                     </div>
                     <div class="row-fluid">
                        <label class="popup_label"><input id="pagination" type="checkbox" checked="true">增加分页方法</label>
                     </div>
                  </div>
                  <div class="steps step2-2 row-fluid">
                     <div class="row-fluid">
                        <div class="control-group">
                           <label class="control-label popup_label">选择一个表：</label>
                           <select id="tables" class="span9 popup_text">
                           </select>
                        </div>
                     </div>
                     
                     <div class="row-fluid">
                        <div class="control-group">
                           <label class="control-label popup_label">操作类型：</label>
                           <select id="crud_option" class="span9 popup_text">
                              <option value="select">查询</option>
                              <option value="insert">新增</option>
                              <option value="update">更新</option>
                              <option value="delete">删除</option>
                           </select>
                        </div>
                     </div>
                     <div class="row-fluid">
                        <div class="control-group">
	                        <label class="control-label popup_label">选择SQL风格：</label>
	                        <select id="sql_style" class="span9 popup_text">
	                           <option value="csharp">C#风格(参数形式为@Name)</option>
	                           <option value="java">JAVA风格(参数形式为?)</option>
	                        </select>
	                     </div>
                     </div>
                     <div class="row-fluid method_name_class">
                        <div class="control-group">
                           <label class="control-label popup_label">生成的方法名：</label>
                           <input id="method_name" class="span9  popup_text" style="height:32px;line-height:32px">
                        </div>
                     </div>
                  </div>
                  <div class="row-fluid steps step2-2-1" style="height:368px;" dbCatalog="SqlServer">
	                  <div id="operation_fields" class="step2-2-1-1 row-fluid">
	                     <div class="row-fluid">
	                        <div class="control-group">
	                           <label class="control-label popup_label">选择字段：</label>
	                           <select id="fields" multiple="multiple" class="popup_text" style="width:434px;">
	                           </select>
	                        </div>
	                     </div>
	                  </div>
	                  <br>
	                  <div id="where_condition" class="step2-2-1-2">
	                     <div class="row-fluid">
	                        <div class="control-group">
	                           <label class="control-label popup_label">Where条件：</label>
	                           <select id="conditions" class="span4">
	                              <option value='-1'>--请选择--</option>
	                           </select>
	                           <select id="condition_values" class='span3'>
	                              <option value='-1'>--请选择--</option>
	                              <option value='0'>=</option>
	                              <option value='1'>!=</option>
	                              <option value='2'>&gt;</option>
	                              <option value='3'>&lt;</option>
	                              <option value='4'>&ge;</option>
	                              <option value='5'>&le;</option>
	                              <option value='6'>Between</option>
	                              <option value='7'>Like</option>
	                              <option value='8'>In</option>
	                           </select>
	                           <input id="add_condition" type="button" class="span2 btn btn-primary popup_text input-sm" value="添加">
	                        </div>
	                     </div>
	                     <br>
	                     <div class="row-fluid">
	                           <select class="span10" id="selected_condition" multiple="multiple" style="height:120px;">
	                           </select>
	                           <input id="del_condition" type="button" class="span2 btn btn-danger popup_text input-sm" value="删除">
	                     </div>
	                  </div>
	                  <br>
	                  <div id="orderby" class="step2-2-1-2">
	                     <div class="row-fluid">
	                        <div class="control-group">
	                           <label class="control-label popup_label">Order by：</label>
	                           <select id="orderby_field" class="span5">
	                              <option value='-1'>--请选择--</option>
	                           </select>
	                           <select id="orderby_sort" class='span3'>
	                              <option value='asc'>ASC</option>
	                              <option value='desc'>DESC</option>
	                           </select>
	                        </div>
	                     </div>
	                  </div>
	                  <div class="row-fluid" id="auto_sql_scalarTypeDiv">
	                  		<div class="control-group">
	                           <label class="control-label popup_label">返回形式：</label>
	                           <select id="auto_sql_scalarType" class='span5'>
	                              <option value='List'>列表(List)</option>
	                              <option value='Single'>唯一的(Single)</option>
	                              <option value='First'>第一个(First)</option>
	                           </select>	
	                           <label class="popup_label" style="padding-left:15px">
	                           	   <input id="auto_sql_pagination" type="checkbox" checked="true">
	                           	     增加分页方法
	                           </label>                           
	                        </div>
	                  </div>
	                  <br>
	                  <div class="row-fluid">
	                        <div id="sql_builder" class="span12" style="height:100px;">
	                        </div>
	                  </div>
	               </div>
               	  <div class="steps step2-2-2 row-fluid" from="">
                     <div class="row-fluid" id="param_list_auto_div">
                           <label class="control-label popup_label">填写条件参数名</label>
                     </div>  
                     <div id="param_list_auto" class="row-fluid input-sm">      
                     </div>
                     <div class="row-fluid"><br/><br/>
                     	<div style="clear:both">以下SQL语句为只读，如果需要修改，请点击上一步，进行修改。</div>
                     </div>
                     <div class="row-fluid">
                        <div id="step2_2_2_sql_editor" class="span12" style="height:200px;">
                        </div>
                     </div>
                  </div>
               
                  <div class="steps step2-3 row-fluid" from="" style="height:348px;">
                     <div class="row-fluid">
                        <div class="control-group">
                           <label class="control-label popup_label" style="width:90px;">DAO类名：</label>
                            <select id="sql_class_name" class="span9"></select>
                        </div>
                     </div>
                     <div class="row-fluid">
                        <div class="control-group">
                           <label class="control-label popup_label" style="width:90px;">实体类名：</label>
                            <select id="sql_pojo_name" class="span9"></select>
                        </div>
                     </div>
                     <div class="row-fluid">
                        <div class="control-group">
                           <label class="control-label popup_label" style="width:90px;">方法名：</label>
                           <input  id="sql_method_name" class="span9 input-sm" type="text">
                        </div>
                     </div>
                      <div class="row-fluid" id="free_sql_scalarTypeDiv" style="margin-top:12px">
	                  		<div class="control-group">
	                           <label class="control-label popup_label" style="width:90px;">返回形式：</label>
	                           <select id="free_sql_scalarType" class='span5'>
	                              <option value='List'>列表(List)</option>
	                              <option value='Single'>唯一的(Single)</option>
	                              <option value='First'>第一个(First)</option>
	                           </select>	 
	                           <label class="popup_label" style="padding-left:20px">
	                           	   <input id="free_sql_pagination" type="checkbox" checked="true">
	                           	     增加分页方法
	                           </label>                           
	                        </div>
	                  </div>
                     <label class="control-label popup_label">输入查询SQL，占位符：Java请使用?或者:Name形式，c#请使用@Name形式</label>
                     <div class="row-fluid">
                        <div id="sql_editor" class="span12" style="height:200px;">
                        </div>
                     </div>
                  </div>
                  <div class="steps step2-3-1 row-fluid" from="">
                     <div class="row-fluid" id="param_list_free_div">
                           <label class="control-label popup_label">填写参数名/参数Index，并选择数据类型</label>
                     </div>  
                     <div id="param_list" class="row-fluid input-sm">      
                     </div>
                     <div class="row-fluid"><br/><br/>
                     	 <div style="clear:both">以下SQL语句为只读，如果需要修改，请点击上一步，进行修改。</div>
                     </div>
                     <div class="row-fluid">
                        <div id="step2_3_1_sql_editor" class="span12" style="height:200px;">
                        </div>
                     </div>
                  </div>
               </div>
               <div class="modal-footer">
               		<div class="row-fluid">
                      <label id="error_msg" class="control-label popup_label" style="color:red; word-break:normal; text-align:left;"></label>
                  	</div>
                   	<div class="row-fluid">
	                  	<button id="prev_step"  type="button" class="btn btn-default">上一步</button>
	                  	<button id="next_step"  type="button" class="btn btn-primary">下一步</button>
	                  	<label class="popup_label"><input id="gen_on_save" type="checkbox">保存时生成代码</label>
	                 </div>
               </div>
            </div>
         </div>
         <!-- /.modal-content -->
      </div>
      <!-- /.modal-dialog -->
      
      <div id="main_layout2" style="display: none; padding-top: 1px;">
	      	
      </div>
      
      <!--End wizard-->
      <!-- JS and analytics only. -->
      <!-- Bootstrap core JavaScript================================================== -->
      <!-- Placed at the end of the document so the pages load faster -->
      <script src="/static/jquery/jquery-1.10.2.min.js"></script>
      <script src="/static/bootstrap/js/bootstrap.min.js"></script>
      <script src="/static/w2ui/w2ui-1.3.2.min.js"></script>
      <script src="/static/jstree/jstree.js"></script>
      <script src="/static/jquery/jquery.blockui.min.js"></script>
      <script src="/static/js/sprintf.js"></script>
      <script src="/static/ace/ace.js"></script>
      <script src="/static/jquery/jquery.multiple.select.js"></script>
      <script src="/static/js/selectize.min.js"></script>
      <script src="/static/js/cblock.js"></script>
      <script src="/static/js/ajaxutil.js"></script>
      <script src="/static/js/sql_builder.js"></script>
      <script src="/static/js/wizzard.js"></script>
      <script src="/static/js/ui_render.js"></script>
      <script src="/static/js/index.js"></script>
      <script src="/static/js/progress.js"></script>
      
      <script src="/static/js/header.js"></script>

   </body>
</html>
