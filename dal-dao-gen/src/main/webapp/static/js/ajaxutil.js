//向导注释
//step1
//step2
//step3-1 -> step3-2
(function (window, undefined) {
    var AjaxUtil = function () {
    };

    /**
     * 标准DAO(包含基础的增删改查操作)
     *
     * @param postData
     */
    var post_task_table_view_sp = function (postData) {
        postData["table_names"] = $("#table_list").multipleSelect("getSelects").join(",");
        postData["view_names"] = $("#view_list").multipleSelect("getSelects").join(",");
        postData["sp_names"] = $("#sp_list").multipleSelect("getSelects").join(",");
        postData["prefix"] = $("#prefix").val();
        postData["suffix"] = $("#suffix").val();
        postData["cud_by_sp"] = $("#cud_by_sp").is(":checked");
        postData["pagination"] = $("#pagination").is(":checked");
        postData["length"] = $("#standard_length_property").is(":checked");
        var api_list = new Array();
        $.each($(".step2-1-2 input:checked"), function (index, value) {
            api_list.push($(value).attr("id"));
        });
        postData["api_list"] = api_list.join(",");
        $.post("/rest/task/table", postData, function (data) {
            $("#page1").modal("hide");
            w2ui["grid_toolbar"].click("refreshDAO", null);
            if ($("#gen_on_save").is(":checked")) {
                $("#generateCode").modal({
                    "backdrop": "static"
                });
            }
        }).fail(function (data) {
            alert("保存出错！");
        });
    };

    /**
     * 构建SQL（生成的代码绑定到模板）
     *
     * @param postData
     */
    var post_task_auto = function (postData) {
        postData["table_name"] = $("#tables").val();
        postData["method_name"] = $("#method_name").val();
        postData["crud_type"] = $("#crud_option").val();

        if ("select" == postData["crud_type"]) {
            postData["scalarType"] = $("#auto_sql_scalarType").val();
            postData["pagination"] = $("#auto_sql_pagination").is(":checked");
            postData["orderby"] = sprintf("%s,%s", $("#orderby_field").val(), $("#orderby_sort").val());
        } else {
            postData["scalarType"] = "";
            postData["pagination"] = false;
            postData["orderby"] = "";
        }

        postData["fields"] = $("#fields").multipleSelect("getSelects").join(",");
        postData["sql_content"] = ace.edit("sql_builder").getValue();
        postData["length"] = $("#build_length_property").is(":checked");

        var paramList = [];
        var paramValues = [];
        var paramNullable = [];
        var paramSensitive = [];
        $.each($("#param_list_auto").children("div"), function (i, n) {
            var first = $(n).find("input").eq(0);
            var second = $(n).find(":checkbox").eq(0);
            paramList.push($(first).val());
            paramValues.push($(first).val());
            paramNullable.push($(second).is(":checked"));
            if (postData["sql_style"] == "java") {
                var third = $(n).find(":checkbox").eq(1);
                paramSensitive.push($(third).is(":checked"));
            }
        });

        var selectedConditions = [];
        var idx = 0;
        if (postData["sql_style"] == "csharp") {
            $.each($("#selected_condition option"), function (i, n) {
                var temp = $(n).val().split(",");
                if (temp.length == 1) {
                    selectedConditions.push(temp[0]);
                } else if (temp.length > 1) {
                    if (temp[1] == "6") {
                        // between
                        selectedConditions.push(sprintf("%s,%s,%s,%s,%s", temp[0], temp[1], paramValues[idx], paramValues[idx + 1], paramNullable[idx] || paramNullable[idx + 1]));
                        idx += 2;
                    } else if (temp[1] == "9" || temp[1] == "10") {
                        // is null
                        // is not null
                        selectedConditions.push(sprintf("%s,%s,%s", temp[0], temp[1], temp[0]));
                    } else {
                        selectedConditions.push(sprintf("%s,%s,%s,%s", temp[0], temp[1], paramValues[idx], paramNullable[idx]));
                        idx++;
                    }
                }
            });
        } else {
            $.each($("#selected_condition option"), function (i, n) {
                var temp = $(n).val().split(",");
                if (temp.length == 1) {
                    selectedConditions.push(temp[0]);
                } else if (temp.length > 1) {
                    if (temp[1] == "6") {
                        // between
                        selectedConditions.push(sprintf("%s,%s,%s,%s,%s,%s", temp[0], temp[1], paramValues[idx], paramValues[idx + 1], paramNullable[idx] || paramNullable[idx + 1], paramSensitive[idx]));
                        idx += 2;
                    } else if (temp[1] == "9" || temp[1] == "10") {
                        // is null
                        // is not null
                        selectedConditions.push(sprintf("%s,%s,%s", temp[0], temp[1], temp[0]));
                    } else {
                        selectedConditions.push(sprintf("%s,%s,%s,%s,%s", temp[0], temp[1], paramValues[idx], paramNullable[idx], paramSensitive[idx]));
                        idx++;
                    }
                }
            });

            // java hints
            var hints = [];
            var cbHints = $("#buildJavaHints :checkbox:checked");
            if (cbHints != undefined && cbHints.length > 0) {
                $.each(cbHints, function (i, n) {
                    hints.push($(cbHints[i]).val());
                });
            }
            postData["hints"] = hints.join(";");
        }

        if ($("#crud_option").val() == "insert") {
            selectedConditions = [];
            $.each($("#param_list_auto").children("div"),
                function (index, value) {
                    // 模式：
                    // Age,6,aa,nullable,sensitive;Name,1,param2,nullable,sensitive;
                    selectedConditions.push(sprintf("%s,%s,%s,%s,%s", paramValues[index], 1, paramValues[index], paramNullable[index], paramSensitive[index]));
                });
        }

        postData["condition"] = selectedConditions.join(";");
        postData["params"] = paramList.join(";");

        $.post("/rest/task/auto", postData, function (data) {
            if (data.code == "OK") {
                $("#page1").modal("hide");
                w2ui["grid_toolbar"].click("refreshDAO", null);
                if ($("#gen_on_save").is(":checked")) {
                    // window.ajaxutil.generate_code($("#gen_language").val());
                    $("#generateCode").modal({"backdrop": "static"});
                }
            } else {
                $.showMsg("error_msg", data.info);
            }
        }).fail(function (data) {
            alert("保存出错！");
        });
    };

    /**
     * 自定义SQL（额外生成实体类）
     *
     * @param postData
     */
    var post_task_free_sql = function (postData) {
        postData["class_name"] = $("#sql_class_name").val();
        postData["pojo_name"] = $("#sql_pojo_name").val();
        postData["method_name"] = $("#sql_method_name").val();

        if ($("#free_sql_crud_option").val() == "select") {
            postData["scalarType"] = $("#free_sql_scalarType").val();
            postData["pagination"] = $("#free_sql_pagination").is(":checked");
            if (postData["class_name"] == "" || postData["pojo_name"] == "" || postData["method_name"] == "") {
                $("#error_msg").text("DAO类名，实体类名以及方法名需要填写！");
                return;
            }
        } else {
            postData["scalarType"] = "";
            postData["pagination"] = false;
            if (postData["class_name"] == "" || postData["method_name"] == "") {
                $("#error_msg").text("DAO类名以及方法名需要填写！");
                return;
            }
        }

        $("#error_msg").text("");

        postData["length"] = $("#free_length_property").is(":checked");
        postData["crud_type"] = $("#free_sql_crud_option").val();
        postData["sql_content"] = ace.edit("sql_editor").getValue();
        var paramList = [];
        $.each($("#param_list").children("div"),
            function (index, value) {
                var first = $(value).children("input").eq(0);
                var second = $(value).children("select").eq(0);
                if (postData["sql_style"] == "csharp") {
                    paramList.push(sprintf("%s,%s", $(first).val(), $(second).val()));
                } else {
                    var third = $(value).children("div").eq(0).children(":checkbox").eq(0);
                    paramList.push(sprintf("%s,%s,%s", $(first).val(), $(second).val(), $(third).is(":checked")));
                }
            });
        postData["params"] = paramList.join(";");

        if (postData["sql_style"] == "java") {
            var hints = [];
            var cbHints = $("#customJavaHints :checkbox:checked");
            if (cbHints != undefined && cbHints.length > 0) {
                $.each(cbHints, function (i, n) {
                    hints.push($(cbHints[i]).val());
                });
            }
            postData["hints"] = hints.join(";");
        }

        $.post("/rest/task/sql", postData, function (data) {
            if (data.code == "OK") {
                $("#page1").modal('hide');
                w2ui["grid_toolbar"].click('refreshDAO', null);
                if ($("#gen_on_save").is(":checked")) {
                    $("#generateCode").modal({"backdrop": "static"});
                }
            } else {
                $.showMsg("error_msg", "SQL测试执行异常，请检查sql及对应参数！" + data.info);
            }
        }).fail(function (data) {
            alert("执行异常，请检查sql及对应参数！");
        });
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
            // C#风格或者Java风格，@Name or ?
            postData["sql_style"] = $("#sql_style").val();
            postData["comment"] = $("#comment").val();
            if ($("#page1").attr('is_update') == "1") {
                postData["action"] = "update";
                var records = w2ui['grid'].getSelection();
                var record = w2ui['grid'].get(records[0]);
                postData["id"] = record.id;
                postData["version"] = record.version;
            } else {
                postData["action"] = "insert";
            }

            if ($("#gen_style").val() == "auto") { // 构建SQL（生成的代码绑定到模板）
                post_task_auto(postData);
            } else if ($("#gen_style").val() == "sql") {// 自定义SQL（额外生成实体类）
                post_task_free_sql(postData);
            } else if ($("#gen_style").val() == "table_view_sp") { // 标准DAO(包含基础的增删改查操作)
                post_task_table_view_sp(postData);
            }
        },
        reload_dbservers: function (callback, groupDBs, groupId) {
            cblock($("body"));

            var url = "/rest/db/dbs?rand=" + Math.random();
            if (groupDBs != null && groupDBs != '') {
                url += "&groupDBs=true";
            }
            if (groupId != undefined && groupId != null && groupId != '') {
                url += "&groupId=" + groupId;
            } else {
                url += "&groupId=-1";
            }
            $.get(url).done(function (data) {
                // $("select[id$=servers] > option:gt(0)").remove();

                if ($("#databases")[0] != undefined && $("#databases")[0].selectize != undefined) {
                    $("#databases")[0].selectize.clearOptions();
                } else {
                    $("#databases").selectize({
                        // maxItems: null,
                        valueField: 'id',
                        labelField: 'title',
                        searchField: 'title',
                        sortField: 'title',
                        options: [],
                        create: false
                    });
                }

                $("#databases")[0].selectize.on('dropdown_open', function (dropdown) {
                    $(".step1").height(240);
                });

                $("#databases")[0].selectize.on('dropdown_close', function (dropdown) {
                    $(".step1").height(74);
                });

                var allServers = [];
                $.each(data, function (index, value) {
                    allServers.push({
                        id: value,
                        title: value
                        // serverType: value.db_type
                    });
                });
                $("#databases")[0].selectize.addOption(allServers);
                $("#databases")[0].selectize.refreshOptions(false);

                if (callback != undefined) {
                    callback();
                }

                $("body").unblock();
            }).fail(function (data) {
                $("body").unblock();
            });
        },
        reload_projects: function () {
            $.jstree.reference("#jstree_projects").refresh();
        },
        reload_dbsets: function (callback) {
            var selectedProject = $.jstree.reference("#jstree_projects").get_selected();
            if (selectedProject == undefined || selectedProject.length < 1 || selectedProject[0] == -1) {
                alert("请单击一个项目，再操作！");
                return;
            }
            var project = $.jstree.reference("#jstree_projects").get_node(selectedProject[0]).original;
            cblock($("body"));
            $.get("/rest/groupdbset/getDbset?rand=" + Math.random() + "&daoFlag=true&groupId=" + project['dal_group_id']).done(function (data) {
                if ($("#databases")[0] != undefined && $("#databases")[0].selectize != undefined) {
                    $("#databases")[0].selectize.clearOptions();
                } else {
                    $("#databases").selectize({
                        // maxItems: null,
                        valueField: 'id',
                        labelField: 'title',
                        searchField: 'title',
                        sortField: 'title',
                        options: [],
                        create: false
                    });
                }

                $("#databases")[0].selectize.on('dropdown_open', function (dropdown) {
                    $(".step1").height(240);
                });

                $("#databases")[0].selectize.on('dropdown_close', function (dropdown) {
                    $(".step1").height(74);
                });

                var allServers = [];
                $.each(data, function (index, value) {
                    allServers.push({
                        id: value['name'],
                        title: value['name']
                    });
                });
                $("#databases")[0].selectize.addOption(allServers);
                $("#databases")[0].selectize.refreshOptions(false);

                if (callback != undefined) {
                    callback();
                }

                $("body").unblock();
            }).fail(function (data) {
                $("body").unblock();
            });
        },
        generate_code: function () {
            $("#generateCode").modal("hide");
            var random = new Date().valueOf();
            progress.start($("#generateCodeProcessDiv"), random);
            var language = $("#regen_language").val();
            $.post("/rest/project/generate", {
                "project_id": w2ui['grid'].current_project,
                "regenerate": $("#regenerate").val() == "regenerate",
                "language": language,
                "newPojo": $("#newPojo").is(":checked"),
                "random": random
            }, function (data) {
                if (data.code == "OK") {
                    $("#viewCode").val(data.info);
                }
                else {
                    $("#generateCodeProcessErrorMess").html(data.info.replace(/\r\n/g, "<br />"));
                    $("#generateCodeProcessErrorDiv").modal();
                    progress.reportException("generate success return but not ok");
                }
            }).fail(function (data) {
                $("#generateCodeProcessErrorMess").html("生成异常：" + data.info);
                $("#generateCodeProcessErrorDiv").modal();
                progress.reportException("exception");
            });
        }
    };

    window.ajaxutil = new AjaxUtil();
})(window);