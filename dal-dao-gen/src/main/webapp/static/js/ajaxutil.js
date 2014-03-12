//向导注释
//step1
//step2
//step3-1 -> step3-2
(function (window, undefined) {

    var AjaxUtil = function () {

    };

    AjaxUtil.prototype = {
        post_task: function () {
            var postData = {};
            var current_project = w2ui['grid'].current_project;
            if (current_project == undefined) {
                if (w2ui['sidebar'].nodes.length < 1 || w2ui['sidebar'].nodes[0].nodes.length < 1)
                    return;
                current_project = w2ui['sidebar'].nodes[0].nodes[0].id;
            }

            postData["project_id"] = current_project;
            postData["db_name"] = $("#databases").val();
            if ($("#page1").attr('is_update') == "1") {
                postData["action"] = "update";
                var records = w2ui['grid'].getSelection();
                var record = w2ui['grid'].get(records[0]);
                postData["id"] = record.id;
            } else {
                postData["action"] = "insert";
            }
            postData["server"] = $("#servers").val();

            if ($(".gen_style.active").children().val() == "auto") {
                postData["table_name"] = $("#tables").val();
                postData["method_name"] = $("#method_name").val();
                //C#风格或者Java风格，@Name or ?
                postData["sql_style"] = $("#sql_style").val();
                postData["crud_type"] = $(".op_type.active").children().val();;
                var selectedConditions = [];

                $.each($("#selected_condition option"), function (index, value) {
                    selectedConditions.push($(value).val());
                });

                postData["fields"] = $('#fields').multipleSelect('getSelects').join(",");
                postData["condition"] = selectedConditions.join(";");

                $.post("/rest/task/auto", postData, function (data) {
                    $("#page1").modal('hide');
                    w2ui["grid_toolbar"].click('refreshDAO', null);
                    if ($("#gen_on_save").is(":checked")) {
                        window.ajaxutil.generate_code($("#gen_language").val());
                    }
                });

            } else if ($(".gen_style.active").children().val() == "sql") {
                postData["class_name"] = $("#sql_class_name").val();
                postData["pojo_name"] = $("#sql_pojo_name").val();
                postData["method_name"] = $("#sql_method_name").val();

                if(postData["class_name"] == "" 
                    || postData["pojo_name"] == ""
                    || postData["method_name"] == ""){
                    $("#error_msg").text("DAO类名，实体类名以及方法名需要填写！");
                    return;
                }
                $("#error_msg").text("");

                postData["crud_type"] = "select";
                postData["sql_content"] = ace.edit("sql_editor").getValue();
                var paramList = [];
                $.each($("#param_list").children("div"), function (index, value) {
                    var first = $(value).children("input").eq(0);
                    var second = $(value).children("select").eq(0);
                    paramList.push(sprintf("%s,%s", $(first).val(), $(second).val()));
                });
                postData["params"] = paramList.join(";");

                $.post("/rest/db/test_sql", postData).done(function (data) {
                    if (data.code == "OK") {
                        $.post("/rest/task/sql", postData, function (data) {
                            $("#page1").modal('hide');
                            w2ui["grid_toolbar"].click('refreshDAO', null);
                            if ($("#gen_on_save").is(":checked")) {
                                window.ajaxutil.generate_code($("#gen_language").val());
                            }
                        });
                    } else {
                        $("#error_msg").text("执行异常，请检查sql及对应参数！");
                    }
                });
            } else if ($(".gen_style.active").children().val() == "table_view_sp") {
                postData["table_names"] = $('#table_list').multipleSelect('getSelects').join(",");
                postData["view_names"] = $('#view_list').multipleSelect('getSelects').join(",");
                postData["sp_names"] = $('#sp_list').multipleSelect('getSelects').join(",");
                postData["prefix"] = $("#prefix").val();
                postData["suffix"] = $("#suffix").val();
                postData["cud_by_sp"] = $("#cud_by_sp").is(":checked");
                postData["pagination"] = $("#pagination").is(":checked");
                $.post("/rest/task/table", postData, function (data) {
                    $("#page1").modal('hide');
                    w2ui["grid_toolbar"].click('refreshDAO', null);
                    if ($("#gen_on_save").is(":checked")) {
                        window.ajaxutil.generate_code($("#gen_language").val());
                    }
                });
            }
        },
        reload_dbservers: function (callback) {
            cblock($("body"));

            $.get("/rest/db/servers?rand=" + Math.random()).done(function (data) {
                //$("select[id$=servers] > option:gt(0)").remove();

                if ($("#servers")[0] != undefined && $("#servers")[0].selectize != undefined) {
                    $("#servers")[0].selectize.clearOptions();
                } else {
                    $("#servers").selectize({
                        //maxItems: null,
                        valueField: 'id',
                        labelField: 'title',
                        searchField: 'title',
                        options: [],
                        create: false
                    });
                }
                var allServers = [];
                $.each(data, function (index, value) {
                    allServers.push({
                        id: value.id,
                        title: sprintf("%s:%s", value.server, value.port),
                        serverType: value.db_type
                    });
                });
                $("#servers")[0].selectize.addOption(allServers);
                $("#servers")[0].selectize.refreshOptions(false);

                if (undefined != data && data.length > 0) {
                    $("#servers")[0].selectize.setValue(data[0].id);
                }
                if (callback != undefined) {
                    callback();
                }

                $("body").unblock();
            }).fail(function (data) {
                $("body").unblock();
            });
        },
        reload_projects: function () {
            cblock($("body"));
            var currentElement = w2ui['sidebar'];
            var nodes = [];
            $.each(currentElement.nodes[0].nodes, function (index, value) {
                nodes.push(value.id);
            });
            currentElement.remove.apply(currentElement, nodes);
            $.get("/rest/project?rand=" + Math.random(), function (data) {
                var new_nodes = [];
                //data = JSON.parse(data);
                $.each(data, function (index, value) {
                    new_nodes.push({
                        id: value.id,
                        text: value.name,
                        namespace: value.namespace,
                        icon: 'fa fa-tasks',
                        onClick: function (event) {
                            var id = event.target;

                            window.render.render_grid();

                            w2ui['grid'].current_project = id;
                            w2ui['grid_toolbar'].click('refreshDAO', null);
                        }
                    });
                });
                currentElement.add('all_projects', new_nodes);
                currentElement.nodes[0].expanded = true;
                currentElement.refresh();
                $("body").unblock();
            }).fail(function (data) {
                alert("超时，请刷新页面重试！");
                $("body").unblock();
            });
        },
        generate_code: function (language) {
            cblock($("body"));
            $.post("/rest/project/generate", {
                "project_id": w2ui['grid'].current_project,
                "language": language
            }, function (data) {
                $("body").unblock();
                window.location.href = "/file.jsp";
            });
        }
    };

    window.ajaxutil = new AjaxUtil();

})(window);