#foreach($task in ${sqlDao})
  <tr>
    <td>${task.getDatabaseSetName()}</td>
    <td>${task.getClass_name()}</td>
    <td>${task.getMethod_name()}</td>
    <td>自定义SQL</td>
    <td>${task.getSql_content()}</td>
    <td>${task.getComment()}</td>
	<td><a href="${approveUrl}taskId=${task.getId()}&taskType=sql&approveFlag=2">同意</a></td>
    <td><a href="${approveUrl}taskId=${task.getId()}&taskType=sql&approveFlag=3">拒绝</a></td>
  </tr>
#end