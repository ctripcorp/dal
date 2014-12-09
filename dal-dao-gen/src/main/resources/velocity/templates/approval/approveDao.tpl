
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
<!-- Table goes in the document BODY -->
<table class="gridtable">
  <tr>
    <th>逻辑数据库</th>
    <th>类名</th>
    <th>方法名</th>
    <th>类型</th>
    <th>预览</th>
    <th>方法描述</th>
    <th colspan="2">审批</th>
  </tr>
#parse("templates/approval/standardDao.tpl")
#parse("templates/approval/buildSqlDao.tpl")
#parse("templates/approval/freeSqlDao.tpl")
</table>
</body>
</html>
