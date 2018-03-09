#foreach($task in ${standardDao})
  <tr>
    <td>${task.getDatabaseSetName()}</td>
    <td>/</td>
    <td>/</td>
    <td>标准DAO</td>
    <td>${task.getApprovePreview()}</td>
    <td>${task.getComment()}</td>
    <td><a href="${approveUrl}taskId=${task.getId()}&taskType=auto&approveFlag=2">同意</a></td>
    <td><a href="${approveUrl}taskId=${task.getId()}&taskType=auto&approveFlag=3">拒绝</a></td>
  </tr>
#end