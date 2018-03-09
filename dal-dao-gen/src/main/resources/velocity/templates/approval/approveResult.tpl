<!doctype html>
<html>
<head>
<meta charset="utf-8">
<title>Dao审批</title>
<style type="text/css">
table.gridtable {
	font-family: verdana, arial, sans-serif;
	font-size: 11px;
	color: #333333;
	border-width: 1px;
	border-color: #666666;
	border-collapse: collapse;
}
table.gridtable th {
	border-width: 1px;
	padding: 8px;
	border-style: solid;
	border-color: #666666;
	background-color: #dedede;
}
table.gridtable td {
	border-width: 1px;
	padding: 8px;
	border-style: solid;
	border-color: #666666;
	background-color: #ffffff;
}
</style>
</head>

<body>
${msg}
<br/>你提交的DAO信息如下：<br/>
<table class="gridtable">
  <tr>
    <th>逻辑数据库</th>
    <th>类名</th>
    <th>方法名</th>
    <th>类型</th>
    <th>预览</th>
    <th>方法描述</th>
  </tr>
#foreach($task in ${standardDao})
  <tr>
    <td>${task.getDatabaseSetName()}</td>
    <td>/</td>
    <td>/</td>
    <td>标准DAO</td>
    <td>${task.getApprovePreview()}</td>
    <td>${task.getComment()}</td>
  </tr>
#end
#foreach($task in ${autoDao})
  <tr>
    <td>${task.getDatabaseSetName()}</td>
    <td>${task.getTable_name()}</td>
    <td>${task.getMethod_name()}</td>
    <td>SQL构建</td>
    <td>${task.getSql_content()}</td>
    <td>${task.getComment()}</td>
  </tr>
#end
#foreach($task in ${sqlDao})
  <tr>
    <td>${task.getDatabaseSetName()}</td>
    <td>${task.getClass_name()}</td>
    <td>${task.getMethod_name()}</td>
    <td>自定义SQL</td>
    <td>${task.getSql_content()}</td>
    <td>${task.getComment()}</td>
  </tr>
#end
</table>
</body>
</html>
