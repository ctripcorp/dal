#foreach($task in ${autoDao})
  <tr>
    <td>${task.getDatabaseSetName()}</td>
    <td>${task.getTable_name()}</td>
    <td>${task.getMethod_name()}</td>
    <td>SQL构建</td>
    <td>${task.getSql_content()}</td>
    <td>${task.getComment()}</td>
	<td><a href="${approveUrl}taskId=${task.getId()}&taskType=auto&approveFlag=2">同意</a></td>
    <td><a href="${approveUrl}taskId=${task.getId()}&taskType=auto&approveFlag=3">拒绝</a></td>
  </tr>
#end