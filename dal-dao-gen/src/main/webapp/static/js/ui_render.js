//向导注释
//step1
//step2
//step3-1 -> step3-2
(function (window, undefined) {
    var Render = function () {
    };

    var refreshDAO = function () {
        w2ui['grid'].clear();
        var current_project = w2ui['grid'].current_project;
        if (current_project == null || current_project == '') {
            return;
        }

        cblock($("body"));
        $.get("/rest/task", {project_id: current_project, rand: Math.random()}, function (data) {
            var allTasks = [];
            $.each(data.tableViewSpTasks, function (index, value) {
                value.recid = allTasks.length + 1;
                value.task_type = "table_view_sp";
                value.task_desc = "标准DAO";
                if (value.table_names != null && value.table_names != "") {
                    value.sql_content = value.table_names;
                }
                if (value.sp_names != null && value.sp_names != "") {
                    if (value.sql_content == null || value.sql_content == "") {
                        value.sql_content = value.sp_names;
                    }
                    else {
                        value.sql_content = value.sql_content + "," + value.sp_names;
                    }
                }
                if (value.view_names != null && value.view_names != "") {
                    if (value.sql_content == null || value.sql_content == "") {
                        value.sql_content = value.view_names;
                    }
                    else {
                        value.sql_content = value.sql_content + "," + value.view_names;
                    }
                }
                value.class_name = "/";
                value.method_name = "/";
                allTasks.push(value);
            });
            $.each(data.autoTasks, function (index, value) {
                value.recid = allTasks.length + 1;
                value.task_type = "auto";
                value.task_desc = "SQL构建";
                value.class_name = value.table_name;
                allTasks.push(value);
            });
            $.each(data.sqlTasks, function (index, value) {
                value.recid = allTasks.length + 1;
                value.task_type = "sql";
                if ("update" == value['crud_type']) {
                    value.task_desc = "自定义SQL(增删改)";
                }
                else {
                    value.task_desc = "自定义SQL(查询)";
                }
                allTasks.push(value);
            });
            w2ui['grid'].add(allTasks);
            $("body").unblock();
        }).fail(function (data) {
            alert("获取所有DAO失败!");
            $("body").unblock();
        });
    };

    var haveUpdateDaoPermision = function () {
        var havePermision = false;
        var current_project = w2ui['grid'].current_project;
        if (current_project == undefined) {
            if (w2ui['sidebar'].nodes.length < 1 || w2ui['sidebar'].nodes[0].nodes.length < 1)
                return;
            current_project = w2ui['sidebar'].nodes[0].nodes[0].id;
        }
        cblock($("body"));
        $.ajax({
            type: "POST",
            async: false,
            url: "/rest/project/projectPermisionCheck",
            data: {
                'prjId': current_project
            },
            dataType: "json",
            success: function (data) {
                if (data.code == "OK") {
                    havePermision = true;
                } else {
                    var selectedProject = $.jstree.reference("#jstree_projects").get_selected();
                    var project = $.jstree.reference("#jstree_projects").get_node(selectedProject[0]).original;
                    var errMsg = '你没有当前Project下的DAO的操作权限.';
                    if (project['update_user_no'] != null && project['update_user_no'] != '') {
                        errMsg += "<br/>当前Dao对应Project的所属User为：" + project['update_user_no'];
                    } else {
                        errMsg += "<br/>当前Dao对应Project的所属User为：Unknown";
                    }
                    if (project['str_update_time'] != null && project['str_update_time'] != '') {
                        errMsg += "<br/>当前Dao对应Project的最后修改时间：" + project['update_user_no'];
                    } else {
                        errMsg += "<br/>当前Dao对应Project的最后修改时间：Unknown";
                        errMsg += "<br/><span style='color:red'>请注意：若Project的所属User为Unknown，则只有具有当前组Admin角色权限的用户可以进行此Project的增删改操作.</span>";
                    }
                    alert(errMsg);
                }
                $("body").unblock();
            }
        });
        return havePermision;
    };

    var addDAO = function () {
        var current_project = w2ui['grid'].current_project;
        if (current_project == null || current_project == '') {
            alert('请先选择 Project');
            return;
        }
        if (!haveUpdateDaoPermision()) {
            return;
        }
        window.wizzard.clear();
        $(".step1").show();
        $(".step2-1").hide();
        $(".step2-1-2").hide();
        $(".step2-2").hide();
        $(".step2-3").hide();
        $(".step2-2-1").hide();
        $(".step2-2-1-1").hide();
        $(".step2-2-1-2").hide();
        $(".step2-2-2").hide();
        $(".step2-2-3").hide();
        $(".step2-2-4").hide();
        $(".step2-3-1").hide();
        $(".step2-3-2").hide();
        $(".step2-3-3").hide();
        $(".step2-3-4").hide();
        $(".step2-3-5").hide();
        setLanguageType(current_project);
        $("#page1").attr('is_update', '0');
        $("#page1").modal({"backdrop": "static"});
        window.ajaxutil.reload_dbsets();
    };

    function setLanguageType(id) {
        $.getJSON("rest/task/getLanguageType", {project_id: id}, function (data) {
            var sql_style = $("#sql_style");
            if (data.code == "OK") {
                sql_style.val(data.info);
                sql_style.attr("readonly", true);
                sql_style.change(function () {
                    sql_style.val(data.info);
                });
            }
            else if (data.code == "Error") {
                recoverLanguageType();
            }
        });
    }

    function recoverLanguageType() {
        $("#sql_style").removeAttr("readonly");
        $("#sql_style").unbind("change");
        sql_style.selectedIndex = 0;
    }

    var editDAO = function () {
        var current_project = w2ui['grid'].current_project;
        if (current_project == null || current_project == '') {
            alert('请先选择 Project');
            return;
        }
        var records = w2ui['grid'].getSelection();
        var record = w2ui['grid'].get(records[0]);
        if (record == null || record == '') {
            alert("请先选择一个 DAO");
            return;
        }
        if (!haveUpdateDaoPermision()) {
            return;
        }
        window.wizzard.clear();
        $(".step1").show();
        $(".step2-1").hide();
        $(".step2-1-2").hide();
        $(".step2-2").hide();
        $(".step2-3").hide();
        $(".step2-2-1").hide();
        $(".step2-2-1-1").hide();
        $(".step2-2-1-2").hide();
        $(".step2-2-2").hide();
        $(".step2-2-3").hide();
        $(".step2-2-4").hide();
        $(".step2-3-1").hide();
        $(".step2-3-2").hide();
        $(".step2-3-3").hide();
        $(".step2-3-4").hide();
        $(".step2-3-5").hide();
        window.ajaxutil.reload_dbsets(function () {
            $("#databases")[0].selectize.setValue(record['databaseSetName']);
        });

        $("#page1").attr('is_update', '1');
        $("#gen_style").val(record.task_type);

        if (record['sql_style']) {
            var sql_style = $("#sql_style");
            recoverLanguageType();
            sql_style.val(record.sql_style);
            sql_style.attr("readonly", true);
            sql_style.change(function () {
                sql_style.val(record.sql_style);
            });
        }

        $("#comment").val(record.comment);
        $("#page1").modal({"backdrop": "static"});
    };

    var delDAO = function () {
        var current_project = w2ui['grid'].current_project;
        if (current_project == null || current_project == '') {
            alert('请先选择 Project');
            return;
        }
        var records = w2ui['grid'].getSelection();
        var record = w2ui['grid'].get(records[0]);
        if (record == null || record == '') {
            alert("请先选择一个 DAO");
            return;
        }
        if (!haveUpdateDaoPermision()) {
            return;
        }
        if (confirm("您确定要删除吗?")) {
            var url = "";
            if (record.task_type == "table_view_sp") {
                url = "rest/task/table";
            } else if (record.task_type == "auto") {
                url = "rest/task/auto";
            } else if (record.task_type == "sql") {
                url = "rest/task/sql";
            }
            $.post(url, {
                "action": "delete",
                "id": record.id
            }, function (data) {
                w2ui["grid_toolbar"].click('refreshDAO', null);
            }).fail(function (data) {
                alert("删除失败!");
            });
        }
    };

    var applyApprove = function () {
        $("#approve_error_msg").empty();
        var current_project = w2ui['grid'].current_project;
        if (current_project == null || current_project == '') {
            alert('请先选择 Project');
            return;
        }
        var records = w2ui['grid'].getSelection();
        var record = w2ui['grid'].get(records[0]);
        if (record == null || record == '') {
            alert("请先选择一个 DAO.如果需要多选，请先按住ctrl键.");
            return;
        }
        $.get("/rest/member/approveuser", {projectId: record['project_id'], rand: Math.random()}).done(function (data) {
            if ($("#approve_user")[0] != undefined && $("#approve_user")[0].selectize != undefined) {
                $("#approve_user")[0].selectize.clearOptions();
            } else {
                $("#approve_user").selectize({
                    valueField: 'id',
                    labelField: 'title',
                    searchField: 'title',
                    sortField: 'title',
                    options: [],
                    create: false
                });
            }
            var allMembers = [];
            $.each(data, function (index, value) {
                allMembers.push({
                    id: value['id'],
                    title: value['userName']
                });
            });
            $("#approve_user")[0].selectize.addOption(allMembers);
            $("#approve_user")[0].selectize.refreshOptions(false);
        }).fail(function (data) {
            alert('获取审批人员列表失败');
        });
        $("#approveModal").modal({"backdrop": "static"});
    };

    var generateCode = function () {
        var current_project = w2ui['grid'].current_project;
        if (current_project == null || current_project == '') {
            alert('请先选择 Project');
            return;
        }
        $("#generateCode").modal({"backdrop": "static"});
    };

    Render.prototype = {
        renderAll: function () {
            $('#main_layout').height($(document).height() - 50);
            window.render.render_layout($('#main_layout'));
            window.render.render_sidebar();
            window.render.render_grid();
            window.render.render_preview();
            w2ui['grid_toolbar'].click('refreshDAO', null);
            $("#refreshFiles").trigger('click');
        },
        render_layout: function (render_obj) {
            $(render_obj).w2layout({
                name: 'main_layout',
                panels: [{
                    type: 'left',
                    size: 271,
                    resizable: true,
                    style: 'border-right: 1px solid silver;'
                }, {
                    type: 'main'
                }, {
                    type: 'preview',
                    size: '50%',
                    resizable: true
                }],
                onResizing: function (event) {
                }
            });
        },
        render_sidebar: function () {
            w2ui['main_layout'].content(
                'left',
                '<div style="color: #34495E !important;font-size: 15px;background-color: #eee; padding: 7px 5px 6px 20px; border-bottom: 1px solid silver">'
                + '<a id="addProj" href="javascript:;">'
                + '<i class="glyphicon glyphicon-plus" ></i>&nbsp;添加</a>'
                + '&nbsp;&nbsp;<a id="editProj" href="javascript:;">'
                + '<i class="glyphicon glyphicon-edit" ></i>&nbsp;修改</a>'
                + '&nbsp;&nbsp;<a id="delProj" href="javascript:;">'
                + '<i class="glyphicon glyphicon-remove" ></i>&nbsp;删除</a>'
                + "</div>"
                + '<div id="jstree_projects"></div>');

            $('#jstree_projects').on('select_node.jstree', function (e, obj) {
                $("#projectModal").attr("is_root", "0");
                w2ui['grid'].current_project = null;
                if (obj.node.id != -1 && obj.node.original.namespace != undefined) {
                    w2ui['grid'].current_project = obj.node.id;
                    $.get("/rest/group/onegroup", {
                        id: obj.node.original['dal_group_id'],
                        rand: Math.random()
                    }).done(function (data) {
                        $("#user_group_pj").html(data['group_name']);
                    }).fail(function () {
                        $("#user_group_pj").html('unknown');
                    });
                    w2ui['grid_toolbar'].click('refreshDAO', null);
                    $("#refreshFiles").trigger('click');
                }
                if (obj.node.id != -1 && obj.node.original.namespace === undefined) {
                    $("#project_group_id").val(obj.node.id);
                    $("#user_group_pj").html(obj.node.text);
                    $("#projectModal").attr("is_root", "1");
                }
            }).jstree({
                'core': {
                    'check_callback': true,
                    'multiple': false,
                    'data': {
                        'url': function (node) {
                            if (node.id == "#") {
                                return "/rest/project/userGroups?root=true&rand=" + Math.random();
                            } else {
                                return "/rest/project/groupprojects?groupId=" + node.id + "&rand=" + Math.random();
                            }
                        }
                    }
                },
                "plugins": ["state"]
            });
        },
        render_grid: function (project_id) {
            var existsGrid = w2ui['grid'];
            if (existsGrid != undefined) {
                return;
            }

            w2ui['main_layout'].content('main', $().w2grid({
                name: 'grid',
                show: {
                    toolbar: true,
                    footer: true,
                    toolbarReload: false,
                    toolbarColumns: false,
                    // toolbarSearch: false,
                    toolbarAdd: false,
                    toolbarDelete: false,
                    // toolbarSave: true,
                    toolbarEdit: false
                },
                multiSelect: true,
                toolbar: {
                    items: [{
                        type: 'break'
                    }, {
                        type: 'button',
                        id: 'refreshDAO',
                        caption: '刷新',
                        icon: 'glyphicon glyphicon-refresh'
                    }, {
                        type: 'button',
                        id: 'addDAO',
                        caption: '添加DAO',
                        icon: 'glyphicon glyphicon-plus'
                    }, {
                        type: 'button',
                        id: 'editDAO',
                        caption: '修改DAO',
                        icon: 'glyphicon glyphicon-edit'
                    }, {
                        type: 'button',
                        id: 'delDAO',
                        caption: '删除DAO',
                        icon: 'glyphicon glyphicon-remove'
                    }, {
                        type: 'break'
                    }, {
                        type: 'button',
                        id: 'applyApprove',
                        caption: '发起审批',
                        icon: 'glyphicon glyphicon-share-alt'
                    }, {
                        type: 'button',
                        id: 'generateCode',
                        caption: '生成代码',
                        icon: 'glyphicon glyphicon-play'
                    }],
                    onClick: function (target, data) {
                        switch (target) {
                            case 'refreshDAO':
                                refreshDAO();
                                break;
                            case 'addDAO':
                                addDAO();
                                break;
                            case 'editDAO':
                                editDAO();
                                break;
                            case 'delDAO':
                                delDAO();
                                break;
                            case 'applyApprove':
                                applyApprove();
                                break;
                            case 'generateCode':
                                generateCode();
                                break;
                        }
                    }
                },
                searches: [{
                    field: 'databaseSetName',
                    caption: '逻辑数据库',
                    type: 'text'
                }, {
                    field: 'table_name',
                    caption: '表/视图/存储过程名',
                    type: 'text'
                }, {
                    field: 'method_name',
                    caption: '方法名',
                    type: 'text'
                }, {
                    field: 'comment',
                    caption: '方法描述',
                    type: 'text'
                }],
                columns: [{
                    field: 'id',
                    caption: 'ID',
                    size: '5%',
                    sortable: true,
                    resizable: true
                }, {
                    field: 'databaseSetName',
                    caption: '逻辑数据库',
                    size: '20%',
                    sortable: true,
                    resizable: true
                }, {
                    field: 'class_name',
                    caption: '类名',
                    size: '10%',
                    sortable: true,
                    resizable: true
                }, {
                    field: 'method_name',
                    caption: '方法名',
                    size: '10%',
                    sortable: true,
                    resizable: true
                }, {
                    field: 'task_desc',
                    caption: '类型',
                    size: '10%',
                    sortable: true,
                    resizable: true
                }, {
                    field: 'sql_content',
                    caption: '预览',
                    size: '10%',
                    resizable: true
                }, {
                    field: 'comment',
                    caption: '方法描述',
                    size: '10%',
                    resizable: true
                }, {
                    field: 'sql_style',
                    caption: 'SQL 风格',
                    size: '5%',
                    resizable: true
                }, {
                    field: 'update_user_no',
                    caption: '最后修改',
                    size: '5%',
                    resizable: true
                }, {
                    field: 'str_update_time',
                    caption: '修改时间',
                    size: '10%',
                    resizable: true
                }, {
                    field: 'str_approved',
                    caption: '审批状态',
                    size: '5%',
                    resizable: true
                }],
                records: [],
                onDblClick: function (target, data) {
                    setTimeout(editDAO, 300);
                }
            }));
        },
        render_preview: function () {
            var existsGrid = w2ui['sub_layout'];
            if (existsGrid != undefined) {
                return;
            }

            $().w2layout({
                name: 'sub_layout',
                panels: [{
                    type: 'left',
                    size: 271,
                    resizable: true
                }, {
                    type: 'main'
                }]
            });

            w2ui['main_layout'].content('preview', w2ui['sub_layout']);
            w2ui['sub_layout'].content(
                'left',
                '<div style="background-color: #eee; padding: 5px 10px 5px 10px; border-bottom: 1px solid silver">'
                + '<a id="refreshFiles" href="javascript:;"><i class="glyphicon glyphicon-refresh"></i>&nbsp;刷新</a>&nbsp;'
                + '<a id="downloadFiles" href="javascript:;"><i class="glyphicon glyphicon-download-alt"></i>&nbsp;下载</a>&nbsp;'
                + '<a id="eraseFiles" href="javascript:;"><i class="glyphicon glyphicon-erase"></i>&nbsp;清除</a>&nbsp;'
                + '<select id="viewCode"><option value="cs">C#</option><option value="java">Java</option></select></div>'
                + '<div id="jstree_files"></div>');

            $('#jstree_files').on('select_node.jstree', function (e, obj) {
                if (obj.node.original.type == "file") {
                    var fileName = obj.node.data;
                    if (fileName.match(/cs$/)) {
                        ace.edit("code_editor").getSession().setMode("ace/mode/csharp");
                    } else if (fileName.match(/java$/)) {
                        ace.edit("code_editor").getSession().setMode("ace/mode/java");
                    }
                    $.get("/rest/file/content", {
                        random: Math.random(),
                        id: w2ui['grid'].current_project,
                        language: $("#viewCode").val(),
                        name: fileName
                    }, function (data) {
                        // var real_data =
                        // JSON.parse(data);
                        ace.edit("code_editor").setValue(data);
                    }).fail(function (data) {
                        alert("获取文件内容失败!");
                    });
                }
            }).jstree({
                'core': {
                    'check_callback': true,
                    'multiple': false,
                    'data': {
                        'url': function (node) {
                            return node.id === '#' ? sprintf("/rest/file?id=%s&language=%s", w2ui['grid'].current_project, $("#viewCode").val())
                                : sprintf("/rest/file?id=%s&language=%s&name=%s", w2ui['grid'].current_project, $("#viewCode").val(), node.data);
                        }
                    }
                }
            });

            var code_editor_html = '<div id="code_editor" class="code_edit" style="height:100%"></div>';
            w2ui['sub_layout'].content('main', code_editor_html);

            var editor = ace.edit("code_editor");
            editor.setTheme("ace/theme/monokai");
            editor.getSession().setMode("ace/mode/csharp");

            editor.getSession().on('change', function () {
                if ($("#code_fullscreen").length <= 0) {
                    $("#code_editor:first-child").prepend('<img id="code_fullscreen" src="/static/images/fullscreen.jpg" alt="全屏" class="code-fullscreen" />');
                    $('#main_layout2').height($(document).height() - 60);
                    $("#main_layout2").w2layout({
                        name: 'main_layout2',
                        panels: [{
                            type: 'main'
                        }]
                    });
                    var code_editor_html = '<div id="code_editor_fullscreen" class="code_edit" style="height:100%"></div>';
                    w2ui['main_layout2'].content('main', code_editor_html);
                    var code_editor_fullscreen = ace.edit("code_editor_fullscreen");
                    code_editor_fullscreen.setTheme("ace/theme/monokai");
                    code_editor_fullscreen.getSession().setMode("ace/mode/csharp");

                    $(document.body).on("click", "#code_fullscreen", function () {
                        if ($("#code_fullscreen_back").length <= 0) {
                            $("#code_editor_fullscreen:first-child").prepend('<img id="code_fullscreen_back" src="/static/images/back.jpg" alt="全屏" class="code-fullscreen" />');
                        }
                        code_editor_fullscreen = ace.edit("code_editor_fullscreen");
                        if ($("#viewCode").val() == "java") {
                            code_editor_fullscreen.getSession().setMode("ace/mode/java");
                        } else {
                            code_editor_fullscreen.getSession().setMode("ace/mode/csharp");
                        }
                        var value = ace.edit("code_editor").getValue();

                        $("#main_layout").hide();
                        $("#main_layout2").show();
                        code_editor_fullscreen.setValue(value);
                        code_editor_fullscreen.resize();
                        $('#main_layout2').resize();
                        code_editor_fullscreen.resize();
                    });

                    $(document.body).on("click", "#code_fullscreen_back", function () {
                        code_editor_fullscreen = ace.edit("code_editor_fullscreen");
                        code_editor_fullscreen.setValue(null);
                        $("#main_layout").show();
                        $("#main_layout2").hide();
                        $('#main_layout').resize();
                    });
                }
            });
        }
    };

    window.render = new Render();

    $(function () {
        $(document.body).on("click", "#generate_code", function () {
            window.ajaxutil.generate_code();
        });

        $(document.body).on("click", "#refreshFiles", function () {
            ace.edit("code_editor").setValue(null);
            $.jstree.reference("#jstree_files").refresh();
        });

        $(document.body).on("click", "#downloadFiles", function () {
            var form = $("<form>");// 定义一个form表单
            form.attr("style", "display:none");
            form.attr("target", "");
            form.attr("method", "get");
            form.attr("action", "/rest/file/download");

            var input1 = $("<input>");
            input1.attr("type", "hidden");
            input1.attr("name", "id");
            input1.attr("value", w2ui['grid'].current_project);

            var input2 = $("<input>");
            input2.attr("type", "hidden");
            input2.attr("name", "language");
            input2.attr("value", $("#viewCode").val());

            $("body").append(form);// 将表单放置在web中
            form.append(input1);
            form.append(input2);
            form.submit();// 表单提交
        });

        $(document.body).on("change", "#viewCode", function () {
            $("#refreshFiles").trigger('click');
        });

        $(document.body).on("click", "#submit_approve", function () {
            var userId = $("#approve_user").val();
            if (userId == null || userId == '') {
                $("#approve_error_msg").html('请选择审批人!');
                return;
            }
            var records = w2ui['grid'].getSelection();
            var taskId = [];
            var taskType = [];
            for (var i = 0; i < records.length; i++) {
                var record = w2ui['grid'].get(records[i]);
                taskId.push(record['id']);
                taskType.push(record['task_type']);
            }

            cblock($("body"));
            $.post("/rest/task/approveTask", {
                taskId: taskId.join(','),
                taskType: taskType.join(','),
                userId: userId
            }, function (data) {
                if (data['code'] != 'OK') {
                    $("#approve_error_msg").html(data['info']);
                } else {
                    $("#approveModal").modal('hide');
                    alert('审批提交成功，邮件已发送.');
                }
                $("body").unblock();
            }).fail(function (data) {
                alert("审批提交失败，邮件不能发送！");
                $("body").unblock();
            });
        });

        $(document.body).on("click", "#eraseFiles", function () {
            var current_project = w2ui['grid'].current_project;
            if (current_project == undefined) {
                if (w2ui['sidebar'].nodes.length < 1 || w2ui['sidebar'].nodes[0].nodes.length < 1)
                    return;
                current_project = w2ui['sidebar'].nodes[0].nodes[0].id;
            }
            if (current_project == null || current_project == '') {
                return;
            }

            cblock($("body"));
            $.post("/rest/project/eraseFiles", {'prjId': current_project}, function (data) {
                $("#refreshFiles").trigger('click');
                if (data['code'] != 'OK') {
                    alert(data['info']);
                }
                $("body").unblock();
            }).fail(function (data) {
                alert("清除代码失败！");
                $("body").unblock();
            });
        });

    });
})(window);