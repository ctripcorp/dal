<%@ Page Title="Home Page" Language="C#" MasterPageFile="~/Site.master" AutoEventWireup="true"
    CodeBehind="Default.aspx.cs" Inherits="platform.demo._Default" %>

<asp:Content ID="HeaderContent" runat="server" ContentPlaceHolderID="HeadContent">
<link href="Styles/bootstrap.min.css" rel="stylesheet" type="text/css" />
<link href="Styles/bootstrap-responsive.min.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" href="Styles/DT_bootstrap.css"/>

<script type="text/javascript" src="Scripts/jquery-1.10.1.min.js"></script>
<script type="text/javascript" src="Scripts/bootstrap.min.js"></script>

<script type="text/javascript" src="Scripts/jquery.dataTables.js"></script>
<script type="text/javascript" src="Scripts/DT_bootstrap.js"></script>
<script src="Scripts/sprintf.js" type="text/javascript"></script>
<script type="text/javascript" src="Scripts/Default.js"></script>
</asp:Content>
<asp:Content ID="BodyContent" runat="server" ContentPlaceHolderID="MainContent">
<div class="span4"><div class="control-group"><label class="control-label">Address</label><input id="address" type="text"/></div></div>
<div class="span4"><div class="control-group"><label class="control-label">Name</label><input id="name" type="text"/></div></div>
<div class="span4"><div class="control-group"><label class="control-label">Telephone</label><input id="telephone" type="text"/></div></div>
<div class="span4"><div class="control-group"><label class="control-label">Age</label><input id="age" type="text"/></div></div>
<div class="span4"><div class="control-group"><label class="control-label">Gender</label><input id="gender" type="text"/></div></div>
<div class="span4"><div class="control-group"><label class="control-label">Birth</label><input id="birth" type="text"/></div></div>
<div class="span1 offset3"><input id="insert_data" type="button" value="Insert"/></div>

<div class="span8">

<table class="table table-striped table-bordered table-hover dataTable" id="dao_tasks" aria-describedby="dao_tasks_info">
                              <thead>
                                 <tr role="row">
                                    <th class="sorting_disabled" role="columnheader" tabindex="0" aria-controls="dao_tasks" rowspan="1" colspan="1" aria-label="Username: activate to sort column ascending" style="width: 155px;">Address</th>
                                    <th class="hidden-480 sorting_disabled" role="columnheader" rowspan="1" colspan="1" aria-label="Email" style="width: 183px;">Name</th>
                                    <th class="hidden-480 sorting_disabled" role="columnheader" tabindex="0" aria-controls="dao_tasks" rowspan="1" colspan="1" aria-label="Points: activate to sort column ascending" style="width: 105px;">Telephone</th>
                                    <th class="sorting_disabled" role="columnheader" rowspan="1" colspan="1" aria-label="" style="width: 257px;">Age</th>
                                    <th class="sorting_disabled" role="columnheader" rowspan="1" colspan="1" aria-label="" style="width: 257px;">Gender</th>
                                    <th class="sorting_disabled" role="columnheader" rowspan="1" colspan="1" aria-label="" style="width: 257px;">Birth</th>
                                    <th class="sorting_disabled" role="columnheader" rowspan="1" colspan="1" aria-label="" style="width: 257px;">Action</th>
                                 </tr>
                              </thead>
                              <tbody id="main_area" role="alert" aria-live="polite" aria-relevant="all">
                                 
                              </tbody>
                           </table>
                           </div>

</asp:Content>
