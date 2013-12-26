
jQuery(document).ready(function () {

    App.init(); // initlayout and core plugins

    // Tasks.initDashboardWidget();

    jQuery('body').on('click', '.portlet > .portlet-title > .tools > .icon-collapse, .portlet .portlet-title > .tools > .icon-collapse-top', function (e) {
        // e.preventDefault();
        if (jQuery(this).hasClass("icon-collapse")) {
            jQuery(this).removeClass("icon-collapse").addClass("icon-collapse-top");
        } else {
            jQuery(this).removeClass("icon-collapse-top").addClass("icon-collapse");
        }
    });

    $("[data-dismiss='modal'").click(function (event) {
        if ($("#myModal").hasClass("in")) {
            $("#myModal").toggleClass("in").attr("aria-hidden", true).
            css("display", "none");
        } else {
            $("#myModal").toggleClass("in").attr("aria-hidden", false).
            css("display", "block");
        }
    });

    $("#save_project").click(function () {

        var post_data = {};

        var currentid = $("#project_id").val();
        if(currentid!= undefined && currentid != ""){
            post_data["action"] = "update";
            post_data["id"] = currentid;
        }else{
            post_data["action"] = "insert";
        }
        post_data["name"] = $("#project_name").val();
        post_data["namespace"] = $("#namespace").val();
        

        $.post("/rest/daogen/project", post_data, function (data) {

            $("[data-dismiss='modal'").trigger('click');

            $(".icon-refresh").trigger('click');
        });

    });

    $(".icon-refresh").click(function () {
        var el = $(this).closest(".portlet").children(".portlet-body");
        App.blockUI(el);
        $.get("/rest/daogen/project", function (data) {

            //data = JSON.parse(data);

            var suffix = '</div><div class="task-config">' 
            + '<div class="task-config-btn btn-group">' 
            + '<a class="btn mini blue" href="#" data-toggle="dropdown" data-hover="dropdown" data-close-others="true">操作 <i class="icon-angle-down"></i></a>' 
            + '<ul class="dropdown-menu pull-right" project_id="%s">'
            + '<li><a href="javascript:;" onclick="proj_dao(this);"><i class="icon-sun"></i> 编辑DAO信息</a></li>' 
            + '<li><a href="javascript:;" onclick="proj_file(this);"><i class="icon-twitter"></i> 项目文件</a></li>' 
            + '<li><a href="javascript:;" onclick="edit_proj(this);"><i class="icon-pencil"></i> 编辑项目信息</a></li>' 
            + '<li><a href="javascript:;" onclick="del_proj(this);"><i class="icon-trash"></i> 删除项目</a></li>' 
            + '</ul>' 
            + '</div>' 
            + '</div>';

            var html_data = "";
            $.each(data, function (index, value) {

                var meaningful = sprintf("%s(%s)", value.name, value.namespace);

                var format_suffix = sprintf(suffix, value._id.$oid);

                html_data = sprintf(
                    "%s<li><div class='task-title'><span class='task-title-sp'>%s</span>%s</li>"
                    , html_data, meaningful, format_suffix);

                $.data(document.body, value._id.$oid, value);
            });

            $(".scroller > .task-list").html(html_data);

            App.unblockUI(el);
        });
    });

    $(".icon-refresh").trigger('click');

    $("#daogen").toggleClass('open');

    $("#daogen .sub-menu").show();

});

var del_proj = function(obj){
    var id = $(obj).parent().parent().attr("project_id");
    if(confirm("Are you sure to delete this project?")){
        var post_data = {};

        post_data["id"] = id;
        post_data["name"] = $("#project_name").val();
        post_data["namespace"] = $("#namespace").val();
        post_data["action"] = "delete";
        $.post("/rest/daogen/project", post_data, function(data){
            $(".icon-refresh").trigger('click');
        });
    }
};

var edit_proj = function(obj){
    var id = $(obj).parent().parent().attr("project_id");
    $("#project_id").val(id);
    var value = $.data(document.body, id);
    $("#project_name").val(value.name);
    $("#namespace").val(value.namespace);

    $("[data-dismiss='modal'").trigger('click');
};

var proj_dao = function(obj){
    var id = $(obj).parent().parent().attr("project_id");
    window.location.href = "/daogen/task.html?project_id="+id;
};

var proj_file = function(obj){
    var id = $(obj).parent().parent().attr("project_id");
    window.location.href = "/daogen/file.html?project_id="+id;
};