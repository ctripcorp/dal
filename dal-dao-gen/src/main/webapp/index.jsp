

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
               <li class="active">
                  <a href="index.jsp">数据访问层生成器</a>
               </li>
            </ul>
            <ul class="nav navbar-nav pull-right">
               <li>
                  <a href="javascript:;" onclick="window.open('http://conf.ctripcorp.com/pages/viewpage.action?pageId=32081284', '_blank');">文档</a>
               </li>
               <li>
                  <a href="mailto:R%26Dsysdev_dal@Ctrip.com">咨询</a>
               </li>
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
                        注销
                        </a>
                     </li>
                  </ul>
               </li>
            </ul>
         </div>
      </div>
      <div id="main_layout">
      </div>

      <!--Begin project modal-->
      <div class="modal fade" id="projectModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" is_update="0">
         <div class="modal-dialog">
            <div class="modal-content">
               <div class="modal-header">
                  <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                  <h4 class="modal-title" id="myModalLabel">Add a project</h4>
               </div>
               <div class="modal-body">
                  <div class="row-fluid">
                     <div class="control-group">
                        <input id="project_id" type="hidden" value="">
                        <label class="control-label popup_label">项目名称</label>
                        <input id="name" class="span9" type="text">
                     </div>
                  </div>
                  <div class="row-fluid">
                     <div class="control-group">
                        <label class="control-label popup_label">命名空间</label>
                        <input id="namespace" class="span9" type="text">
                     </div>
                  </div>
               </div>
               <div class="modal-footer">
                  <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                  <button id="save_proj" type="button" class="btn btn-primary">Save changes</button>
               </div>
            </div>
         </div>
      </div>
      <!--End project modal-->

      <!--Begin share project modal-->
      <div class="modal fade" id="shareProject" tabindex="-1" role="dialog" aria-labelledby="shareProjectLabel" aria-hidden="true">
         <div class="modal-dialog">
            <div class="modal-content">
               <div class="modal-header">
                  <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                  <h4 class="modal-title" id="myModalLabel">与他人共享项目</h4>
               </div>
               <div class="modal-body">
                  <div class="row-fluid">
                     <div class="control-group">
                       <select id="users" class="span7">
                           <option value="_please_select">--请选择--</option>
                        </select>
                     </div>
                  </div>
               </div>
               <div class="modal-footer">
                  <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                  <button id="share_proj" type="button" class="btn btn-primary">共享</button>
               </div>
            </div>
         </div>
      </div>
      <!--End share project modal-->

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
                           <option value="increment_gen">增量生成，仅生成被修改的DAO</option>
                            <option value="regenerate">重新生成</option>
                        </select>
                     </div>
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
      <!-- /.modal -->
      <!--Begin wizard-->

       <div class="modal fade" id="manageDb" tabindex="-1" role="dialog" aria-labelledby="generateCodeLabel" aria-hidden="true">
         <div class="modal-dialog">
            <div class="modal-content">
               <div class="modal-header">
                  <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                  <h4 class="modal-title">添加All-In-One配置</h4>
               </div>
               <div class="modal-body">
                                       
                     <div class="row-fluid">
                        <div class="control-group">
                           <label class="control-label popup_label">All-In-One：</label>
                           <textarea id="all_in_one" class="span9" style="height:100px;">&lt;add name="xxx" connectionString="Server=xxx;port=xxx;UID=xxx;password=xxx;database=xxx;" providerName="System.Data.SqlClient" /&gt;</textarea>
                        </div>
                     </div>
               </div>
               <div class="modal-footer">
                  <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                  <button id="add_db" type="button" class="btn btn-primary">保存</button>
               </div>
            </div>
            <!-- /.modal-content -->
         </div>
         <!-- /.modal-dialog -->
      </div>

      <div class="modal fade" id="page1" tabindex="-1" role="dialog" aria-labelledby="page1_label" aria-hidden="true" is_update="0">
         <div class="modal-dialog">
            <div class="modal-content">
               <div class="modal-header">
                  <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                  <h4 class="modal-title" id="page1_label">DAO生成向导</h4>
               </div>
               <div class="modal-body" style="position: relative;overflow: auto;width: auto;max-height:420px;">
                  <div class="steps step1 row-fluid">
                     <div class="row-fluid">
                        <div class="control-group">
                           <label class="control-label popup_label" style="width:130px;">All-In-One数据库：</label>
                           <select id="databases" class="span7">
                           </select>
                           <a href="javascript:;" onclick="$('#page1').modal('hide');$('#manageDb').modal();">没有找到？</a>
                        </div>
                     </div>
                     <div class="row-fluid">
                        <div class="control-group">
                           <label class="control-label popup_label" style="width:130px;">DAO代码生成方式:</label>
                           <select id="gen_style" class="span7">
                              <option value="table_view_sp">生成模板(包含基础的增删改查操作)</option>
                              <option value="auto">构建SQL（生成的代码绑定到模板）</option>
                              <option value="sql">复杂查询（额外生成实体类）</option>
                           </select>
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
                           <input type="text" id="prefix" class="span9 popup_text">
                        </div>
                     </div>
                     <div class="row-fluid">
                        <div class="control-group">
                           <label class="control-label popup_label">生成时加上后缀：</label>
                           <input type="text" id="suffix" class="span9 popup_text">
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
                           <input id="method_name" class="span9 popup_text">
                        </div>
                     </div>
                  </div>
                  <div class="row-fluid steps step2-2-1" style="height:348px;">
                  <div id="operation_fields" class="step2-2-1-1 row-fluid">
                     <div class="row-fluid">
                        <div class="control-group">
                           <label class="control-label popup_label">选择字段：</label>
                           <select id="fields" multiple="multiple" class="popup_text" style="width:440px;">
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
                           <input id="add_condition" type="button" class="span2 btn btn-primary popup_text" value="添加">
                        </div>
                     </div>
                     <br>
                     <div class="row-fluid">
                           <select class="span10" id="selected_condition" multiple="multiple" style="height:120px;">
                           </select>
                           <input id="del_condition" type="button" class="span2 btn btn-danger popup_text" value="删除">
                     </div>
                  </div>
                  <br>
                  <div class="row-fluid">
                        <div id="sql_builder" class="span12" style="height:100px;">
                        </div>
                  </div>
               </div>
               
                  <div class="steps step2-3 row-fluid" from="" style="height:348px;">
                     <div class="row-fluid">
                        <div class="control-group">
                           <label class="control-label popup_label" style="width:80px;">DAO类名：</label>

                            <select id="sql_class_name" class="span9"></select>
                          
                        </div>
                     </div>
                     <div class="row-fluid">
                        <div class="control-group">
                           <label class="control-label popup_label" style="width:80px;">实体类名：</label>
                          
                            <select id="sql_pojo_name" class="span9"></select>
                           
                        </div>
                     </div>
                     <div class="row-fluid">
                        <div class="control-group">
                           <label class="control-label popup_label" style="width:80px;">方法名：</label>
                           <input  id="sql_method_name" class="span9" type="text">
                        </div>
                     </div>
                     <label class="control-label popup_label">输入查询SQL，占位符：Java请使用?或者:Name形式，c#请使用@Name形式</label>
                     <div class="row-fluid">
                        <div id="sql_editor" class="span12" style="height:200px;">
                        </div>
                     </div>
                  </div>
                  <div class="steps step2-3-1 row-fluid" from="">
                     <div class="row-fluid">
                           <label class="control-label popup_label">填写参数名/参数Index，并选择数据类型</label>
                     </div>  
                     <div id="param_list" class="row-fluid">      
                     </div>
                  </div>
               </div>
               <div class="modal-footer">
                  <label id="error_msg" class="control-label popup_label" style="color:red;"></label>
                  <button id="prev_step"  type="button" class="btn btn-default">上一步</button>
                  <button id="next_step"  type="button" class="btn btn-primary">下一步</button>
                  <label class="popup_label"><input id="gen_on_save" type="checkbox">保存时生成代码</label>
               </div>
            </div>
         </div>
         <!-- /.modal-content -->
      </div>
      <!-- /.modal-dialog -->
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
   </body>
</html>
