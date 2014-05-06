
jQuery(document).ready(function () {

    $('#main_layout').height($(document).height() - 50);

    window.render.render_layout($('#main_layout'));

    window.render.render_sidebar();

    $(window).resize(function () {
        $('#main_layout').height($(document).height() - 50);
    });

    $(document.body).on('click', '#addProj', function (event) {
        $("#projectModal").attr("is_update", "0");
        $("#name").val("");
        $("#namespace").val("");
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


        $.post("/rest/project", post_data, function (data) {
            $("#projectModal").modal('hide');
            window.ajaxutil.reload_projects();
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

    $(document.body).on('click', '#generate_code', function (event) {
        window.ajaxutil.generate_code();
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

    $(document.body).on('click', "#next_step", function (event) {
        var current_step = $("div.steps:visible");
        window.wizzard.next(current_step);
    });

    $(document.body).on('click', "#prev_step", function (event) {
        var current_step = $("div.steps:visible");
        window.wizzard.previous(current_step);
    });

    $(document.body).on('click', "#refreshFiles", function (event) {
        ace.edit("code_editor").setValue(null);
        $.jstree.reference("#jstree_files").refresh();
    });

    $(document.body).on('click', "#downloadFiles", function (event) {
        cblock($("body"));
        $.get("/rest/file/download?id=" + w2ui['grid'].current_project+
            "&language=" + $("#viewCode").val(), function (data) {
            $("body").unblock();
            window.location.href = data;
        }).fail(function(data){
                alert("下载失败!");
            });
    });

    $(document.body).on('change', "#viewCode", function (event) {
        $("#refreshFiles").trigger('click');
    });

    $(document.body).on('click', "#add_db", function(event){
        $.post("/rest/db/all_in_one", {"data": $("#all_in_one").val()}, function(data){
            if(data.code == "OK"){
                $("#manageDb").modal('hide');
                window.ajaxutil.reload_dbservers();
                $("#page1").modal();
            }else{
                alert(data.info);
            }

        });
    });

    $("#layout_main_layout_resizer_preview").mouseleave(function(){
          ace.edit("code_editor").resize();
    });

    window.ajaxutil.reload_projects();
});
