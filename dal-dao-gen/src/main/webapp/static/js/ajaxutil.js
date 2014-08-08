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
            //postData["server"] = $("#servers").val();

            if ($("#gen_style").val() == "auto") { //构建SQL（生成的代码绑定到模板）
                postData["table_name"] = $("#tables").val();
                postData["method_name"] = $("#method_name").val();
                //C#风格或者Java风格，@Name or ?
                postData["sql_style"] = $("#sql_style").val();
                postData["crud_type"] = $("#crud_option").val();
                postData["scalarType"] = $("#auto_sql_scalarType").val();
                if("select" == postData["crud_type"]){
                    postData["pagination"] = $("#auto_sql_pagination").is(":checked");
                    postData["orderby"] = sprintf("%s,%s",$("#orderby_field").val(),$("#orderby_sort").val());
                }else{
                    postData["pagination"] = false;
                    postData["orderby"] = "";
                }

                postData["fields"] = $('#fields').multipleSelect('getSelects').join(",");
                postData["sql_content"] = ace.edit("sql_builder").getValue();

                var paramList = [];
                var paramValues = [];
                $.each($("#param_list_auto").children("div"), function (index, value) {
                    var first = $(value).children("input").eq(0);
                    var second = $(value).children("select").eq(0);
                    paramList.push(sprintf("%s,%s", $(first).val(), $(second).val()));
                    paramValues.push($(first).val());
                });

                var selectedConditions = [];
                var index2 = 0;
                $.each($("#selected_condition option"), function (index, value) {
                    var temp = $(value).val().split(",");
                    if(temp[1]=="6"){//between
                        selectedConditions.push(sprintf("%s,%s,%s,%s", temp[0], temp[1], paramValues[index2], paramValues[index2+1]));
                        index2+=2;
                    }else{
                        selectedConditions.push(sprintf("%s,%s,%s", temp[0], temp[1], paramValues[index2]));
                        index2++;
                    }
                });

                postData["condition"] = selectedConditions.join(";");
                postData["params"] = paramList.join(";");

                $.post("/rest/task/auto", postData,function (data) {
                    if (data.code == "OK") {
                        $("#page1").modal('hide');
                        w2ui["grid_toolbar"].click('refreshDAO', null);
                        if ($("#gen_on_save").is(":checked")) {
                            //window.ajaxutil.generate_code($("#gen_language").val());
                            $("#generateCode").modal({"backdrop": "static"});
                        }
                    } else {
                        $.showMsg("error_msg",data.info);
                    }
                }).fail(function (data) {
                        alert("保存出错！");
                    });

            } else if ($("#gen_style").val() == "sql") {//复杂查询（额外生成实体类）
                postData["class_name"] = $("#sql_class_name").val();
                postData["pojo_name"] = $("#sql_pojo_name").val();
                postData["method_name"] = $("#sql_method_name").val();
                postData["scalarType"] = $("#free_sql_scalarType").val();
                postData["pagination"] = $("#free_sql_pagination").is(":checked");

                if (postData["class_name"] == ""
                    || postData["pojo_name"] == ""
                    || postData["method_name"] == "") {
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

                $.post("/rest/task/sql/test_sql", postData).done(function (data) {
                    if (data.code == "OK") {
                        $.post("/rest/task/sql", postData,function (data) {
                            if (data.code == "OK") {
                                $("#page1").modal('hide');
                                w2ui["grid_toolbar"].click('refreshDAO', null);
                                if ($("#gen_on_save").is(":checked")) {
                                    $("#generateCode").modal({"backdrop": "static"});
                                }
                            } else {
                                $.showMsg("error_msg","SQL测试执行异常，请检查sql及对应参数！"+data.info);
                            }
                        }).fail(function (data) {
                                alert("执行异常，请检查sql及对应参数！");
                            });
                    } else {
                        $.showMsg("error_msg","SQL测试执行异常，请检查sql及对应参数！"+data.info);
                    }
                }).fail(function (data) {
                        alert("执行异常，请检查sql及对应参数！");
                    });
            } else if ($("#gen_style").val() == "table_view_sp") { //生成模板(包含基础的增删改查操作)
                postData["table_names"] = $('#table_list').multipleSelect('getSelects').join(",");
                postData["view_names"] = $('#view_list').multipleSelect('getSelects').join(",");
                postData["sp_names"] = $('#sp_list').multipleSelect('getSelects').join(",");
                postData["prefix"] = $("#prefix").val();
                postData["suffix"] = $("#suffix").val();
                postData["cud_by_sp"] = $("#cud_by_sp").is(":checked");
                postData["pagination"] = $("#pagination").is(":checked");
                $.post("/rest/task/table", postData,function (data) {
                    $("#page1").modal('hide');
                    w2ui["grid_toolbar"].click('refreshDAO', null);
                    if ($("#gen_on_save").is(":checked")) {
                        //window.ajaxutil.generate_code($("#gen_language").val());
                        $("#generateCode").modal({"backdrop": "static"});
                    }
                }).fail(function (data) {
                        alert("保存出错！");
                    });
            }
        },
        reload_dbservers: function (callback,groupDBs) {
            cblock($("body"));

            var url = "/rest/db/dbs?rand=" + Math.random();
            if(groupDBs!=null && groupDBs!=''){
                url+="&groupDBs=true";
            }
            $.get(url).done(function (data) {
                //$("select[id$=servers] > option:gt(0)").remove();

                if ($("#databases")[0] != undefined && $("#databases")[0].selectize != undefined) {
                    $("#databases")[0].selectize.clearOptions();
                } else {
                    $("#databases").selectize({
                        //maxItems: null,
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
                        //serverType: value.db_type
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
            // cblock($("body"));
            // var currentElement = w2ui['sidebar'];
            // var nodes = [];
            // $.each(currentElement.nodes[0].nodes, function (index, value) {
            //     nodes.push(value.id);
            // });
            // currentElement.remove.apply(currentElement, nodes);
            // $.get("/rest/project?rand=" + Math.random(), function (data) {
            //     var new_nodes = [];
            //     //data = JSON.parse(data);
            //     $.each(data, function (index, value) {
            //         new_nodes.push({
            //             id: value.id,
            //             text: value.name,
            //             namespace: value.namespace,
            //             icon: 'fa fa-tasks',
            //             onClick: function (event) {
            //                 var id = event.target;

            //                 window.render.render_grid();

            //                 w2ui['grid'].current_project = id;
            //                 window.render.render_preview();
            //                 w2ui['grid_toolbar'].click('refreshDAO', null);
            //                 $("#refreshFiles").trigger('click');
            //             }
            //         });
            //     });
            //     currentElement.add('all_projects', new_nodes);
            //     currentElement.nodes[0].expanded = true;
            //     currentElement.refresh();
            //     $("body").unblock();
            // }).fail(function (data) {
            //     alert("超时，请刷新页面重试！");
            //     $("body").unblock();
            // });
            $.jstree.reference("#jstree_projects").refresh();
        },
        reload_dbsets: function (callback) {
            var selectedProject = $.jstree.reference("#jstree_projects").get_selected();
            if(selectedProject == undefined || selectedProject.length < 1 ||
                selectedProject[0] == -1){
                alert("请单击一个项目，再操作！");
                return;
            }
            var project = $.jstree.reference("#jstree_projects").get_node(selectedProject[0]).original;
            cblock($("body"));
            $.get("/rest/groupdbset/getDbset?rand=" + Math.random()+"&daoFlag=true&groupId="+project['dal_group_id']).done(function (data) {

                if ($("#databases")[0] != undefined && $("#databases")[0].selectize != undefined) {
                    $("#databases")[0].selectize.clearOptions();
                } else {
                    $("#databases").selectize({
                        //maxItems: null,
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
            progress.start($("#generateCodeProcessDiv"),random);
            $.post("/rest/project/generate", {
                "project_id": w2ui['grid'].current_project,
                "regenerate": $("#regenerate").val() == "regenerate",
                "language": $("#regen_language").val(),
                "newPojo": $("#newPojo").is(":checked"),
                "random":random
            },function (data) {
                if (data.code != "OK") {
                    $("#generateCodeProcessErrorMess").html(data.info);
                    $("#generateCodeProcessErrorDiv").modal();
                    progress.reportException("generate success return but not ok");
                }
            }).fail(function (data) {
                    $("#generateCodeProcessErrorMess").html("生成异常！");
                    $("#generateCodeProcessErrorDiv").modal();
                    progress.reportException("exception");
                });
        }
    };

    window.ajaxutil = new AjaxUtil();

})(window);