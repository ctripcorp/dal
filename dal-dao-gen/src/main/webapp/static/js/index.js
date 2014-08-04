
jQuery(document).ready(function () {

    window.render.renderAll();

    $(window).resize(function () {
        $('#main_layout').height($(document).height() - 50);
    });

    $(document.body).on('click', '#addProj', function (event) {
        $("#projectModal").attr("is_update", "0");
        $("#name").val("");
        $("#namespace").val("");
        $("#dalconfigname").val("");
        $("#projectModal").modal();
    });

    $(document.body).on('click', '#editProj', function (event) {
        var selectedProject = $.jstree.reference("#jstree_projects").get_selected();
        if(selectedProject == undefined || selectedProject.length < 1 ||
            selectedProject[0] == -1){
            alert("请单击一个项目，再操作！");
            return;
        }
        $("#project_id").val(selectedProject[0]);
        var project = $.jstree.reference("#jstree_projects").get_node(selectedProject[0]).original;
        if (project != undefined) {
            $("#name").val(project.text);
            $("#namespace").val(project.namespace);
            $("#dalconfigname").val(project['dal_config_name']);
        }
        $("#projectModal").attr("is_update", "1");
        $("#projectModal").modal();
    });

    $(document.body).on('click', '#delProj', function (event) {
        var selectedProject = $.jstree.reference("#jstree_projects").get_selected();
        if(selectedProject == undefined || selectedProject.length < 1 ||
            selectedProject[0] == -1){
            alert("请单击一个项目，再操作！");
            return;
        }
        if (confirm("Are you sure to delete this project?")) {
            var post_data = {};

            post_data["id"] = selectedProject[0];
            post_data["action"] = "delete";
            $.post("/rest/project", post_data, function (data) {
                window.ajaxutil.reload_projects();
                w2ui['grid'].clear();
                ace.edit("code_editor").setValue("");
                $($("#jstree_files").children()[0]).html("");
            }).fail(function(data){
                    alert("删除失败！");
                });
        }
    });

    $(document.body).on('click', '#shareProj', function (event) {
        var selectedProject = $.jstree.reference("#jstree_projects").get_selected();
        if(selectedProject == undefined || selectedProject.length < 1 ||
            selectedProject[0] == -1){
            alert("请单击一个项目，再操作！");
            return;
        }

        $("#users > option:gt(0)").remove();
        $.get("/rest/project/users?rand=" + Math.random(), function (data) {
            var allUsers = [];
            $.each(data, function (index, value) {
                allUsers.push($('<option>', {
                    text: value.userName + "(" + value.userNo + ")",
                    value: value.userNo
                }));
            });
            $("#users").append(allUsers);
            $("#shareProject").modal();
        }).fail(function(data){
                alert("加载用户列表失败，请重试！");
            });
    });

    $(document.body).on('click', '#save_proj', function (event) {
        $("#proj_error_msg").html('');
        var post_data = {};
        var currentid = $("#project_id").val();
        if ($("#projectModal").attr("is_update") == "1" &&
            currentid != undefined && currentid != "") {
            post_data["action"] = "update";
            post_data["id"] = currentid;
        } else {
            post_data["action"] = "insert";
        }
        post_data["name"] = $("#name").val();
        post_data["namespace"] = $("#namespace").val();
        post_data["dalconfigname"] = $("#dalconfigname").val();


        if(post_data["name"]==null || post_data["name"]==''){
            $("#proj_error_msg").html('请输入项目名称！');
            return;
        }

        if(post_data["namespace"]==null || post_data["namespace"]==''){
            $("#proj_error_msg").html('请输入命名空间！');
            return;
        }

        if(post_data["dalconfigname"]==null || post_data["dalconfigname"]==''){
            $("#proj_error_msg").html('请输入Dal.config配置文件根节点name！');
            return;
        }

        $.post("/rest/project", post_data, function (data) {
            if(data.code == "OK"){
                $("#projectModal").modal('hide');
                window.ajaxutil.reload_projects();
            }else{
                $("#proj_error_msg").html(data.info);
            }
        }).fail(function(data){
                alert("保存失败！");
            });
    });

    $(document.body).on('click', '#share_proj', function (event) {
        if($("#users").val() != "_please_select"){
            $.post("/rest/project/share_proj", {
                "id": $.jstree.reference("#jstree_projects").get_selected()[0],
                "userNo": $("#users").val()
            }, function (data) {
                if (data.code == "OK") {
                    alert("分享成功！");
                } else {
                    alert("分享失败，此用户可能已经可以操作该项目了!");
                }

                $("#shareProject").modal("hide");
            }).fail(function(data){
                    alert("分享失败!");
                });
        }
    });

    $(document.body).on('click', '#regen_language', function (event) {
        if($("#regen_language").val() == "cs"){
            $(".useNewPojo").show();
        }else{
            $(".useNewPojo").hide();
        }
    });

    $("#add_condition").click(function () {
        var selectedField = $("#conditions").val();
        var selectedCondition = $("#condition_values").val();
        if (selectedField != "-1" && selectedCondition != "-1") {
            $("#selected_condition").append($('<option>', {
                value: sprintf("%s,%s", selectedField, selectedCondition),
                text: sprintf("%s %s", $("#conditions").find(":selected").text(), $("#condition_values").find(":selected").text())
            }));
            window.sql_builder.build();
        }
    });

    $("#del_condition").click(function () {
        $("#selected_condition").find(":selected").remove();
        window.sql_builder.build();
    });

    $("#orderby_field").change(function(){
        window.sql_builder.build();
    });

    $("#orderby_sort").change(function(){
        window.sql_builder.build();
    });

    $(document.body).on('click', "#next_step", function (event) {
        var current_step = $("div.steps:visible");
        window.wizzard.next(current_step);
    });

    $(document.body).on('click', "#prev_step", function (event) {
        var current_step = $("div.steps:visible");
        window.wizzard.previous(current_step);
    });

    $("#layout_main_layout_resizer_preview").mouseleave(function(){
        ace.edit("code_editor").resize();
    });

    //一键添加缺失dbset和db
    $(document.body).on('click', "#add_lack_dbset", function (event) {
        var current_project = w2ui['grid'].current_project;
        $.post("/rest/project/addLackDbset", {
            "project_id": current_project
        }, function (data) {
            $("#generateCodeProcessErrorMess").html(data.info);
        }).fail(function(data){
                $("#generateCodeProcessErrorMess").html("一键补全失败!");
            });
    });

    $("#gen_style").click(function(event){
        $("#error_msg").css("color","black");
        if($("#gen_style").val()=="table_view_sp"){
            $("#error_msg").html("在这种模式下面，我们只需要选择数据库、表、视图、存储过程、视图，之后将生成对应的增、删、改、查的代码。");
        }
        if($("#gen_style").val()=="auto"){
            $("#error_msg").html("在这种模式下面，我们需要选择数据库、表，以及将要生成DAO类型（增、删、改、查之一），再选择对应的字段，最后构建出一个SQL语句。");
        }
        if($("#gen_style").val()=="sql"){
            $("#error_msg").html("在这种模式下面，我们可以自定义查询SQL语句，指定生成的DAO类名、实体类名、方法名。");
        }
    });

    window.ajaxutil.reload_projects();
});
