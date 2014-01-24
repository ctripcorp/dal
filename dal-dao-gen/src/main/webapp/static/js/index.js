jQuery(document).ready(function () {

    $('#main_layout').height($(document).height() - 50);

    $('#main_layout').w2layout({
        name: 'main_layout',
        panels: [{
            type: 'left',
            size: 270,
            resizable: true,
            style: 'border-right: 1px solid silver;'
        }, {
            type: 'main',
            style: 'background-color: white;'
        }]
    });

    $(window).resize(function () {
        $('#main_layout').height($(document).height() - 50);
    });

    //Begin tree side bar
    w2ui['main_layout'].content('left', $().w2sidebar({
        name: 'sidebar',
        img: null,
        topHTML: '<div style="background-color: #eee; padding: 10px 5px 10px 20px; border-bottom: 1px solid silver"><a id="addProj" href="javascript:;"><i class="fa fa-plus"></i>添加项目</a>&nbsp;&nbsp;<a href="javascript:;" onclick="reloadProjects();"><i class="fa fa-refresh"></i>刷新</a></div>',
        menu: [{
            id: "gen_code",
            text: 'C# Code',
            icon: 'fa fa-play'
        }, {
            id: "java_code",
            text: 'Java Code',
            icon: 'fa fa-play'
        }, {
            id: "edit_proj",
            text: 'Edit',
            icon: 'fa fa-edit'
        }, {
            id: "del_proj",
            text: 'Delete',
            icon: 'fa fa-times'
        }],
        onMenuClick: function (event) {
            switch (event.menuItem.id) {
            case "gen_code":
                $.post("/rest/project/generate", {
                    "project_id": event.target,
                    "language": "csharp"
                }, function (data) {
                    window.location.href = "/file.html";
                });
                break;
            case "java_code":
                $.post("/rest/project/generate", {
                    "project_id": event.target,
                    "language": "java"
                }, function (data) {
                    window.location.href = "/file.html";
                });
                break;
            case "edit_proj":
                $("#project_id").val(event.target);
                var project = w2ui['sidebar'].get(event.target);
                if (project != undefined) {
                    $("#name").val(project.text);
                    $("#namespace").val(project.namespace);
                }
                $("#projectModal").attr("is_update", "1");
                $("#projectModal").modal();
                break;
            case "del_proj":
                if (confirm("Are you sure to delete this project?")) {
                    var post_data = {};

                    post_data["id"] = event.target;
                    post_data["action"] = "delete";
                    $.post("/rest/project", post_data, function (data) {
                        reloadProjects();
                    });
                }
                break;
            }
        },
        nodes: [{
            id: 'all_projects',
            text: '所有项目',
            icon: 'fa fa-folder-o',
            // plus: true,
            group: true,
        }],
    }));
    //End tree side bar



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

    $(document.body).on('change', '#tables', function (event) {
        $("#class_name").val($(this).val());
    });

    $("button.move").click(function () {
        $.each($('#fields_left').find(":selected"), function (index, value) {
            $("#fields_right").append(value);
        });
    });

    $("button.moveall").click(function () {
        $("#fields_left option").each(function () {
            $("#fields_right").append($(this));
        });
    });

    $("button.remove").click(function () {
        $.each($('#fields_right').find(":selected"), function (index, value) {
            $("#fields_left").append(value);
        });
    });

    $("button.removeall").click(function () {
        $("#fields_right option").each(function () {
            $("#fields_left").append($(this));
        });
    });

    $("#add_condition").click(function () {
        var selectedField = $("#fields_condition").val();
        var selectedCondition = $("#condition_values").val();
        if (selectedField != "-1" && selectedCondition != "-1") {
            $("#selected_condition").append($('<option>', {
                value: sprintf("%s_%s", selectedField, selectedCondition),
                text: sprintf("%s %s", $("#fields_condition").find(":selected").text(), $("#condition_values").find(":selected").text())
            }));
        }
    });

    $("#del_condition").click(function () {
        $("#selected_condition").find(":selected").remove();
    });

    $(document.body).on('change', "#only_template", function (event) {
        if ($("#only_template").is(":checked")) {
            $("#method_name").attr("disabled", "disabled");
        } else {
            $("#method_name").attr("disabled", false);
        }
    });

    $(document.body).on('click', "#view_sp_code", function (event) {
        $.get(sprintf("/rest/db/sp_code?db_name=%s&sp_name=%s",
            $("#databases").val(), $("#sps").val()), function (data) {
            $("#sp_editor").height(300);
            var editor = ace.edit("sp_editor");
            editor.setTheme("ace/theme/monokai");
            editor.getSession().setMode("ace/mode/sql");
            editor.setValue(data);
        });
    });

    $(document.body).on('click', "#next_step", function (event) {
        var current_step = $("div.steps:visible");
        current_step.hide();
        if (current_step.hasClass("step1")) {
            //在显示下一页之前，清空下一页的信息
            var defaultActive = $(".gen_style > input[value='auto']").parent();
            if (!defaultActive.hasClass("active")) {
                $(".gen_style.active").removeClass("active");
                defaultActive.addClass("active");
            }

            if ($("#page1").attr('is_update') == "1") {
                var records = w2ui['grid'].getSelection();
                var record = w2ui['grid'].get(records[0]);
                var parentToActive = $(sprintf(".gen_style > input[value='%s']", record.task_type)).parent();
                if (!parentToActive.hasClass("active")) {
                    $(".gen_style.active").removeClass("active");
                    parentToActive.addClass("active");
                }
            }
            $(".step2").show();
        } else if (current_step.hasClass("step2")) {
            var gen_style = $(".gen_style.active").children().val();
            switch (gen_style) {
            case "auto":
                //在显示下一页之前，清空下一页的信息
                $("select[id$=tables] > option:gt(0)").remove();
                $("#only_template").prop("checked", false);
                $("#cud_by_sp").prop("checked", true);
                $("#class_name").val("");
                $("#method_name").val("");
                var defaultActive = $(".op_type > input[value='select']").parent();
                if (!defaultActive.hasClass("active")) {
                    $(".op_type.active").removeClass("active");
                    defaultActive.addClass("active");
                }

                if ($("#page1").attr('is_update') == "1") {
                    var records = w2ui['grid'].getSelection();
                    var record = w2ui['grid'].get(records[0]);
                    $('#tables').append($('<option>', {
                        value: record.table_name,
                        text: record.table_name
                    }));
                    $("#tables").val(record.table_name);
                    $("#class_name").val(record.class_name);
                    $("#cud_by_sp").prop('checked', record.sql_type == "spa_sp3");
                    if (record.method_name == undefined || record.method_name == "") {
                        $("#only_template").prop('checked', true);
                    } else {
                        $("#method_name").val(record.method_name);
                        var parentToActive = $(sprintf(".op_type > input[value='%s']", record.crud_type)).parent();
                        if (!parentToActive.hasClass("active")) {
                            $(".op_type.active").removeClass("active");
                            parentToActive.addClass("active");
                        }
                    }

                    $(".step2-1-1").show();
                } else {
                    cblock($("body"));
                    $.get("/rest/db/tables?db_name=" + $("#databases").val(), function (data) {
                        $.each(data.ids, function (index, value) {
                            $('#tables').append($('<option>', {
                                value: value,
                                text: value
                            }));
                        });
                        $(".step2-1-1").show();
                        $("body").unblock();
                    });
                }
                break;
            case "sp":
                $("select[id$=sps] > option:gt(0)").remove();
                if ($("#page1").attr('is_update') == "1") {
                    var records = w2ui['grid'].getSelection();
                    var record = w2ui['grid'].get(records[0]);
                    $('#sps').append($('<option>', {
                        value: sprintf("%s.%s", record.sp_schema, record.sp_name),
                        text: sprintf("%s.%s", record.sp_schema, record.sp_name)
                    }));
                    $("#sps").val(sprintf("%s.%s", record.sp_schema, record.sp_name));

                    $("#sp_type").val(record.crud_type);
                    $(".step2-2").show();
                } else {
                    cblock($("body"));
                    $.get("/rest/db/sps?db_name=" + $("#databases").val(), function (data) {
                        $.each(data.ids, function (index, value) {
                            $('#sps').append($('<option>', {
                                value: value,
                                text: value
                            }));
                        });
                        $(".step2-2").show();
                        $("body").unblock();
                    });
                }
                break;
            case "sql":
                $(".step2-3").show();
                $("#sql_editor").height(300);
                var editor = ace.edit("sql_editor");
                editor.setTheme("ace/theme/monokai");
                editor.getSession().setMode("ace/mode/sql");

                //在显示下一页之前，清空下一页的信息
                $("#sql_class_name").val("");
                $("#sql_method_name").val("");

                if ($("#page1").attr('is_update') == "1") {
                    var records = w2ui['grid'].getSelection();
                    var record = w2ui['grid'].get(records[0]);
                    $("#sql_class_name").val(record.class_name);
                    $("#sql_method_name").val(record.method_name);
                    editor.setValue(record.sql_content);
                } else {
                    editor.setValue("SELECT * FROM Table");
                }
                break;
            }
        } else if (current_step.hasClass("step2-1-1")) {
            if ($("#only_template").is(":checked")) {
                $(".step3").show();
                $(".step3").attr('from', current_step.attr('class'));
                return;
            }
            var op_type = $(".op_type.active").children().val();
            cblock($("body"));

            $("select[id$=fields_left] > option").remove();
            $("select[id$=fields_right] > option").remove();

            $("select[id$=selected_condition] > option").remove();

            $("select[id$=fields_condition] > option:gt(0)").remove();

            var url = sprintf("/rest/db/fields?table_name=%s&db_name=%s", $("#tables").val(), $("#databases").val());

            $.get(url, function (data) {
                $.each(data, function (index, value) {
                    $("#fields_left").append($('<option>', {
                        value: value.name,
                        text: sprintf("%s%s%s",
                            value.name, value.indexed ? "*" : "",
                            value.primary ? "+" : "")
                    }));
                    $("#fields_condition").append($('<option>', {
                        value: value.name,
                        text: value.name
                    }));
                });
                if ($("#page1").attr('is_update') == "1") {
                    var records = w2ui['grid'].getSelection();
                    var record = w2ui['grid'].get(records[0]);
                    var selectedFields = record.fields.split(",");
                    $.each(selectedFields, function (index, value) {
                        $("#fields_right").append(
                            $(sprintf("#fields_left > option[value='%s']", value)));
                    });
                    var selectedConditions = record.condition.split(",");
                    $.each(selectedConditions, function (index, value) {
                        $("#selected_condition").append($('<option>', {
                            value: value,
                            text: sprintf("%s %s", value.split('_')[0],
                                $(sprintf("#condition_values > option[value='%s']", value.split('_')[1])).text())
                        }));
                    });
                }
                $("body").unblock();
            });

            if (op_type == "select") {
                $(".step2-1-3").show();
                $(".step2-1-3-add").show();
            } else {
                if ($("#cud_by_sp").is(":checked")) {
                    $(".step3").show();
                    $(".step3").attr('from', current_step.attr('class'));
                } else {
                    if (op_type == "update") {
                        $(".step2-1-3").show();
                        $(".step2-1-3-add").show();
                    } else if (op_type == "insert") {
                        $(".step2-1-3").show();
                        $(".step2-1-3-add").hide();
                    } else {
                        $(".step2-1-3").hide();
                        $(".step2-1-3-add").show();
                    }
                }
            }
        } else if (current_step.hasClass("step2-1-3") || current_step.hasClass("step2-1-3-add")) {
            $(".step3").show();
            $(".step3").attr('from', current_step.attr('class'));
        } else if (current_step.hasClass("step2-2") || current_step.hasClass("step2-3")) {
            $(".step3").show();
            $(".step3").attr('from', current_step.attr('class'));
        } else if (current_step.hasClass("step3")) {
            addDao();
        }
    });

    $(document.body).on('click', "#prev_step", function (event) {
        var current_step = $("div.steps:visible");
        if (current_step.hasClass("step1")) {
            return;
        }
        current_step.hide();
        var from_class = current_step.attr("from");
        if (from_class != undefined && from_class != "") {
            var classes = from_class.split(" ").join(".");
            $("." + classes).show();
            return;
        }

        if (current_step.hasClass("step2")) {
            $(".step1").show();
        } else if (current_step.hasClass("step2-1-1")) {
            $(".step2").show();
        } else if (current_step.hasClass("step2-1-3") || current_step.hasClass("step2-1-3-add")) {
            $(".step2-1-1").show();
        } else if (current_step.hasClass("step2-2")) {
            $(".step2").show();
        } else if (current_step.hasClass("step2-3")) {
            $(".step2").show();
        }
    });

    reloadProjects();

});


var reloadProjects = function () {
    cblock($("body"));
    var currentElement = w2ui['sidebar'];
    var nodes = [];
    $.each(currentElement.nodes[0].nodes, function (index, value) {
        nodes.push(value.id);
    });
    currentElement.remove.apply(currentElement, nodes);
    $.get("/rest/project", function (data) {
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

                    renderGrid();

                    w2ui['grid'].current_project = id;
                    w2ui['grid_toolbar'].click('refreshDAO', null);
                }
            });
        });
        currentElement.add('all_projects', new_nodes);
        currentElement.nodes[0].expanded = true;
        currentElement.refresh();
        $("body").unblock();
    });
};


var renderGrid = function () {

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
            //toolbarSearch: false,
            toolbarAdd: false,
            toolbarDelete: false,
            //toolbarSave: true,
            toolbarEdit: false
        },
        toolbar: {
            items: [{
                type: 'break'
            }, {
                type: 'button',
                id: 'refreshDAO',
                caption: '刷新',
                icon: 'fa fa-refresh'
            }, {
                type: 'button',
                id: 'addDAO',
                caption: '添加DAO',
                icon: 'fa fa-plus'
            }, {
                type: 'button',
                id: 'editDAO',
                caption: '修改DAO',
                icon: 'fa fa-edit'
            }, {
                type: 'button',
                id: 'delDAO',
                caption: '删除DAO',
                icon: 'fa fa-times'
            }, {
                type: 'break'
            }, {
                type: 'button',
                id: 'csharpCode',
                caption: 'c#代码',
                icon: 'fa fa-play'
            }, {
                type: 'button',
                id: 'javaCode',
                caption: 'Java代码',
                icon: 'fa fa-play'
            }, ],
            onClick: function (target, data) {
                switch (target) {
                case 'refreshDAO':
                    w2ui['grid'].clear();
                    var current_project = w2ui['grid'].current_project;
                    if (current_project == undefined) {
                        if (w2ui['sidebar'].nodes.length < 1 || w2ui['sidebar'].nodes[0].nodes.length < 1)
                            return;
                        current_project = w2ui['sidebar'].nodes[0].nodes[0].id;
                    }
                    $.get("/rest/task?project_id=" + current_project, function (data) {
                        var allTasks = [];
                        $.each(data.autoTasks, function (index, value) {
                            value.recid = allTasks.length;
                            value.task_type = "auto";
                            allTasks.push(value);
                        });
                        $.each(data.spTasks, function (index, value) {
                            value.recid = allTasks.length;
                            value.sql_content = value.sp_content;
                            value.task_type = "sp";
                            value.sql_type = "/";
                            allTasks.push(value);
                        });
                        $.each(data.sqlTasks, function (index, value) {
                            value.recid = allTasks.length;
                            value.task_type = "sql";
                            value.sql_type = "/";
                            allTasks.push(value);
                        });
                        w2ui['grid'].add(allTasks);
                    });
                    break;
                case 'addDAO':
                    $("select[id$=databases] > option:gt(0)").remove();
                    $(".step1").show();
                    $(".step2").hide();
                    $(".step2-1-1").hide();
                    $(".step2-1-2").hide();
                    $(".step2-1-3").hide();
                    $(".step2-1-3-add").hide();
                    $(".step2-2").hide();
                    $(".step2-3").hide();
                    $(".step3").hide();
                    $("#page1").attr('is_update', '0');
                    $("#page1").modal();
                    $.get("/rest/db/dbs", function (data) {
                        //data = JSON.parse(data);
                        $.each(data, function (index, value) {
                            $('#databases').append($('<option>', {
                                value: value.name,
                                text: value.name
                            }));
                        });
                    });
                    break;
                case 'editDAO':
                    $("select[id$=databases] > option:gt(0)").remove();
                    $(".step1").show();
                    $(".step2").hide();
                    $(".step2-1-1").hide();
                    $(".step2-1-2").hide();
                    $(".step2-1-3").hide();
                    $(".step2-1-3-add").hide();
                    $(".step2-2").hide();
                    $(".step2-3").hide();
                    $(".step3").hide();
                    var records = w2ui['grid'].getSelection();
                    var record = w2ui['grid'].get(records[0]);
                    $("#databases").append($('<option>', {
                        value: record.db_name,
                        text: record.db_name
                    }));
                    $("#databases").val(record.db_name);
                    $("#page1").attr('is_update', '1');
                    $("#page1").modal();
                    break;
                case 'delDAO':
                    if (confirm("Are you sure to delete?")) {
                        var records = w2ui['grid'].getSelection();
                        var record = w2ui['grid'].get(records[0]);
                        $.post("/rest/task", {
                                "action": "delete",
                                "id": record.id,
                                "task_type": record.task_type
                            },
                            function (data) {
                                //$("#page1").modal('hide');
                                w2ui["grid_toolbar"].click('refreshDAO', null);
                            });
                    }
                    break;
                case 'csharpCode':
                    $.post("/rest/project/generate", {
                        "project_id": w2ui['grid'].current_project,
                        "language": "csharp"
                    }, function (data) {
                        window.location.href = "/file.html";
                    });
                    break;
                case 'javaCode':
                    $.post("/rest/project/generate", {
                        "project_id": w2ui['grid'].current_project,
                        "language": "java"
                    }, function (data) {
                        window.location.href = "/file.html";
                    });
                    break;
                }
            }
        },
        searches: [{
            field: 'db_name',
            caption: '数据库',
            type: 'text'
        }, {
            field: 'table_name',
            caption: '表名',
            type: 'text'
        }, {
            field: 'method_name',
            caption: '方法名',
            type: 'text'
        }, ],
        columns: [{
            field: 'db_name',
            caption: '数据库',
            size: '10%',
            sortable: true,
            attr: 'align=center'
        }, {
            field: 'table_name',
            caption: '表名',
            size: '10%',
            sortable: true
        }, {
            field: 'method_name',
            caption: '方法名',
            size: '10%',
            sortable: true
        }, {
            field: 'sql_type',
            caption: '增删改方式',
            size: '10%'
        }, {
            field: 'crud_type',
            caption: '增删改查',
            size: '10%'
        }, {
            field: 'sql_content',
            caption: '预览',
            size: '50%'
        }, ],
        records: []
    }));

};


var addDao = function () {
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

    if ($(".gen_style.active").children().val() == "auto") {
        postData["task_type"] = "auto";
        postData["table_name"] = $("#tables").val();
        postData["class_name"] = $("#class_name").val();
        postData["method_name"] = $("#method_name").val();
        //C#风格或者Java风格，@Name or ?
        postData["sql_style"] = $("#sql_style").val();
        postData["sql_type"] = $("#cud_by_sp").is(":checked") ? "spa_sp3" : "sql";
        postData["crud_type"] = $(".op_type.active").children().val();;
        var selectedFields = [];
        var selectedConditions = [];

        $.each($("#fields_right option"), function (index, value) {
            selectedFields.push($(value).val());
        });
        $.each($("#selected_condition option"), function (index, value) {
            selectedConditions.push($(value).val());
        });

        postData["fields"] = selectedFields.join(",");
        postData["condition"] = selectedConditions.join(",");
    } else if ($(".gen_style.active").children().val() == "sp") {
        postData["task_type"] = "sp";
        postData["sql_style"] = $("#sql_style").val();
        postData["crud_type"] = $("#sp_type").val();
        postData["sp_name"] = $("#sps").val();
    } else if ($(".gen_style.active").children().val() == "sql") {
        postData["task_type"] = "sql";
        postData["class_name"] = $("#sql_class_name").val();
        postData["method_name"] = $("#sql_method_name").val();
        postData["crud_type"] = "select";
        postData["sql_content"] = ace.edit("sql_editor").getValue();
    }

    $.post("/rest/task", postData, function (data) {
        $("#page1").modal('hide');
        w2ui["grid_toolbar"].click('refreshDAO', null);
    });
};