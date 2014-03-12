jQuery(document).ready(function () {

    $('#main_layout').height($(document).height() - 50);

    window.render.render_layout($('#main_layout'));

    window.render.render_sidebar();

    $(window).resize(function () {
        $('#main_layout').height($(document).height() - 50);
    });

    $(document.body).on('click', '#addProj', function (event) {
        $("#projectModal").attr("is_update", "0");
        $("#projectModal").modal();
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
            reloadProjects();
        });
    });

    $(document.body).on('click', '#share_proj', function (event) {
        $.post("/rest/project/share_proj", {
            "id": w2ui['grid'].current_project,
            "userNo": $("#users").val()
        }, function (data) {
            if (data.code == "OK") {
                alert("分享成功！");
            } else {
                alert("分享失败，此用户可能已经可以操作该项目了!");
            }

            $("#shareProject").modal("hide");
        });
    });

    $("#add_condition").click(function () {
        var selectedField = $("#conditions").val();
        var selectedCondition = $("#condition_values").val();
        if (selectedField != "-1" && selectedCondition != "-1") {
            $("#selected_condition").append($('<option>', {
                value: sprintf("%s,%s", selectedField, selectedCondition),
                text: sprintf("%s %s", $("#conditions").find(":selected").text(), $("#condition_values").find(":selected").text())
            }));
        }
    });

    $("#del_condition").click(function () {
        $("#selected_condition").find(":selected").remove();
    });

    $(document.body).on('click', "#next_step", function (event) {
        var current_step = $("div.steps:visible");
        window.wizzard.next(current_step);
    });

    $(document.body).on('click', "#prev_step", function (event) {
        var current_step = $("div.steps:visible");
        window.wizzard.previous(current_step);
    });

    $(document.body).on('click', "#add_server", function (event) {
        var postData = {};
        postData["server"] = $("#server").val();
        if ($("#port").val() == "") {
            postData["port"] = 0;
        } else {
            postData["port"] = $("#port").val();
        }
        postData["user"] = $("#username").val();
        postData["password"] = $("#password").val();
        postData["db_type"] = $("#db_types").val();
        postData["action"] = "insert";
        if ($("#db_types").val() == "sqlserver" && $("#use_ntlm").is(":checked")) {
            postData["domain"] = $("#domains").val();
        } else {
            postData["domain"] = "";
        }
        $.post("/rest/db/servers", postData, function (data) {
            if (data.code == "OK") {
                alert("保存成功！");
                window.ajaxutil.reload_dbservers();
            } else {
                alert("保存失败，请检查连接信息是否合法!");
            }
        });
    });

    $(document.body).on('change', "#db_types", function (event) {
        if ($("#db_types").val() == "sqlserver") {
            $(".domain_verify").show();
        } else {
            $(".domain_verify").hide();
        }
    });

    $('#use_ntlm').click(function () {
        var $this = $(this);
        // $this will contain a reference to the checkbox   
        if ($this.is(':checked')) {
            $("#domains").prop("disabled", false);
        } else {
            $("#domains").prop("disabled", "disabled");
        }
    });

    $(document.body).on('click', "#toggle_add_server", function (event) {
        if ($("#add_server_row").is(":visible")) {
            $("#toggle_add_server").text("添加数据库服务器");
        } else {
            $("#toggle_add_server").text("取消添加服务器");
        }
        $("#add_server_row").toggle();
        $("#add_server").toggle();
    });

    $(document.body).on('click', "#del_server", function (event) {
        var currentServer = $("#servers").val();
        if (currentServer == "_please_select") {
            return;
        }

        if (confirm("确认要删除此服务器信息吗？")) {
            var postData = {};
            postData["action"] = "delete";
            postData["id"] = currentServer;
            $.post("/rest/db/servers", postData, function (data) {
                if (data.code == "OK") {
                    alert("删除成功！");
                    window.ajaxutil.reload_dbservers();
                } else {
                    alert("删除失败!");
                }
            });
        }
    });

    window.ajaxutil.reload_projects();
});