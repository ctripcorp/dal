
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
        topHTML: '<div style="background-color: #eee; padding: 10px 5px 10px 20px; border-bottom: 1px solid silver"><a id="addProj" href="javascript:;"><i class="fa fa-plus"></i>添加项目</a>&nbsp;&nbsp;<a href="javascript:;" onclick="reloadProjects();"><i class="fa fa-refresh"></i>刷新项目</a></div>',
        menu: [{
            id: "java_code",
            text: 'Generate Java Code',
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
            case "java_code":
                cblock($("body"));
                $.post("/rest/project/generate", {
                    "project_id": event.target,
                    "language": "java"
                }, function (data) {
                    $("body").unblock();
                    window.location.href = "/file.jsp";
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

    $("#add_condition").click(function () {
        var selectedField = $("#conditions").val();
        var selectedCondition = $("#condition_values").val();
        if (selectedField != "-1" && selectedCondition != "-1") {
            $("#selected_condition").append($('<option>', {
                value: sprintf("%s_%s", selectedField, selectedCondition),
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
        if($("#port").val() == ""){
           postData["port"] = 0;
        }else{
           postData["port"] = $("#port").val();
        }
        postData["user"] = $("#username").val();
        postData["password"] = $("#password").val();
        postData["db_type"] = $("#db_types").val();
        postData["action"] = "insert";
        if($("#db_types").val() == "sqlserver" 
            && $("#use_ntlm").is(":checked")){
            postData["domain"] = $("#domains").val();
        }else{
            postData["domain"] = "";
        }
        $.post("/rest/db/servers", postData, function (data) {
            if (data.code == "OK") {
                alert("保存成功！");
                reloadServers();
            } else {
                alert("保存失败，请检查连接信息是否合法!");
            }
        });
    });

    $(document.body).on('change', "#db_types", function (event) {
        if($("#db_types").val() == "sqlserver"){
            $(".domain_verify").show();
        }else{
            $(".domain_verify").hide();
        }
    });

    $('#use_ntlm').click(function() {
        var $this = $(this);
        // $this will contain a reference to the checkbox   
        if ($this.is(':checked')) {
            $("#domains").prop("disabled", false);
        } else {
            $("#domains").prop("disabled", "disabled");
        }
    });

    $(document.body).on('click', "#toggle_add_server", function (event) {
        if($("#add_server_row").is(":visible")){
            $("#toggle_add_server").val("添加数据库服务器");
        }else{
            $("#toggle_add_server").val("取消添加服务器");
        }
        $("#add_server_row").toggle();
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
                    reloadServers();
                } else {
                    alert("删除失败!");
                }
            });
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
    }).fail(function(data){
        alert("超时，请刷新页面重试！");
        $("body").unblock();
    });
};

var reloadServers = function (callback) {
    cblock($("body"));

    $.get("/rest/db/servers").done(function(data){
        $("select[id$=servers] > option:gt(0)").remove();
        $.each(data, function (index, value) {
            $("#servers").append($('<option>', {
                value: value.id,
                text: sprintf("%s:%s",value.server, value.port)
            }));
        });
        if (undefined != data && data.length > 0) {
            $("#servers").val(data[0].id);
        }
        if (callback != undefined) {
            callback();
        }
        $("body").unblock();
    }).fail(function(data){
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
                id: 'javaCode',
                caption: '生成Java代码',
                icon: 'fa fa-play'
            }, {
                type: 'button',
                id: 'csharpCode',
                caption: '生成C#代码',
                icon: 'fa fa-play'
            }],
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
                    cblock($("body"));
                    $.get("/rest/task?project_id=" + current_project, function (data) {
                        var allTasks = [];
                        $.each(data.tableViewSpTasks, function (index, value) {
                            value.recid = allTasks.length + 1;
                            value.task_type = "table_view_sp";
                            value.task_desc = "表/视图/存储过程";
                            if(value.table_names != null && value.table_names != ""){
                                value.sql_content = value.table_names;
                            }
                            if(value.sp_names != null && value.sp_names != ""){
                                if(value.sql_content == null || value.sql_content == "")
                                    value.sql_content = value.sp_names;
                                else
                                    value.sql_content = value.sql_content + "," + value.sp_names;
                            }
                            value.class_name = "/";
                            value.method_name = "/";
                            allTasks.push(value);
                        });
                        $.each(data.autoTasks, function (index, value) {
                            value.recid = allTasks.length + 1;
                            value.task_type = "auto";
                            value.task_desc = "SQL构建";
                            allTasks.push(value);
                        });
                        $.each(data.sqlTasks, function (index, value) {
                            value.recid = allTasks.length + 1;
                            value.task_type = "sql";
                            value.task_desc = "自定义查询";
                            allTasks.push(value);
                        });
                        w2ui['grid'].add(allTasks);
                        $("body").unblock();
                    });
                    break;
                case 'addDAO':
                    $(".step0").show();
                    $(".step1").hide();
                    $(".step2").hide();
                    $(".step2-1-0").hide();
                    $(".step2-1-1").hide();
                    $(".step2-1-2").hide();
                    $(".step2-1-3").hide();
                    $(".step_fields").hide();
                    $(".step2-3-1").hide();
                    $(".step2-3-2").hide();
                    $(".step3").hide();
                    $("#page1").attr('is_update', '0');
                    $("#page1").modal({"backdrop": "static"});
                    reloadServers();
                    break;
                case 'editDAO':
                    var records = w2ui['grid'].getSelection();
                    if (records.length > 0) {
                        var record = w2ui['grid'].get(records[0]);
                        if (record != undefined) {
                            $("select[id$=servers] > option:gt(0)").remove();
                            $(".step0").show();
                            $(".step1").hide();
                            $(".step2").hide();
                            $(".step2-1-0").hide();
                            $(".step2-1-1").hide();
                            $(".step2-1-2").hide();
                            $(".step2-1-3").hide();
                            $(".step_fields").hide();
                            $(".step2-3-1").hide();
                            $(".step2-3-2").hide();
                            $(".step3").hide();
                            reloadServers(function () {
                                $("#servers").val(record.server_id);
                            });
                            $("#page1").attr('is_update', '1');
                            $("#page1").modal({"backdrop": "static"});
                        }
                    }
                    break;
                case 'delDAO':
                    if (confirm("Are you sure to delete?")) {
                        var records = w2ui['grid'].getSelection();
                        var record = w2ui['grid'].get(records[0]);
                        var url = "";
                        if(record.task_type == "table_view_sp"){
                            url = "rest/task/table";
                        }else if(record.task_type == "auto"){
                            url = "rest/task/auto";
                        }else if(record.task_type == "sql"){
                            url = "rest/task/sql";
                        }
                        $.post(url, {
                                "action": "delete",
                                "id": record.id
                            },
                            function (data) {
                                //$("#page1").modal('hide');
                                w2ui["grid_toolbar"].click('refreshDAO', null);
                            });
                    }
                    break;
                case 'javaCode':
                    cblock($("body"));
                    $.post("/rest/project/generate", {
                        "project_id": w2ui['grid'].current_project,
                        "language": "java"
                    }, function (data) {
                        $("body").unblock();
                        window.location.href = "/file.jsp";
                    });
                    break;
                case 'csharpCode':
                    cblock($("body"));
                    $.post("/rest/project/generate", {
                        "project_id": w2ui['grid'].current_project,
                        "language": "csharp"
                    }, function (data) {
                        $("body").unblock();
                        window.location.href = "/file.jsp";
                    }).fail(function(data){
                        $("body").unblock();
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
            caption: '表/视图/存储过程名',
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
            field: 'class_name',
            caption: '类名',
            size: '10%',
            sortable: true
        }, {
            field: 'method_name',
            caption: '方法名',
            size: '10%',
            sortable: true
        }, {
            field: 'task_desc',
            caption: '类型',
            size: '10%',
            sortable: true
        },{
            field: 'sql_content',
            caption: '预览',
            size: '50%'
        }, ],
        records: []
    }));

};