
//向导注释
//step1
//step2
//step3-1 -> step3-2
(function (window, undefined) {

    var Render = function () {

    };

    Render.prototype = {
    	render_layout : function(render_obj){
            $(render_obj).w2layout({
                name: 'main_layout',
                panels: [{
                    type: 'left',
                    size: 271,
                    resizable: true,
                    style: 'border-right: 1px solid silver;'
                }, {
                    type: 'main',
                    style: 'background-color: white;'
                }]
            });
        },
        render_sidebar : function(){
            //Begin tree side bar
            w2ui['main_layout'].content('left', $().w2sidebar({
                name: 'sidebar',
                img: null,
                topHTML: '<div style="background-color: #eee; padding: 10px 5px 10px 20px; border-bottom: 1px solid silver"><a id="addProj" href="javascript:;"><i class="fa fa-plus"></i>添加项目</a>&nbsp;&nbsp;<a href="javascript:;" onclick="window.ajaxutil.reload_projects();;"><i class="fa fa-refresh"></i>刷新项目</a></div>',
                menu: [{
                    id: "share",
                    text: '与他人共享',
                    icon: 'fa fa-twitter'
                },{
                    id: "csharpCode",
                    text: '生成C#代码',
                    icon: 'fa fa-play'
                }, {
                    id: "javaCode",
                    text: '生成Java代码',
                    icon: 'fa fa-play'
                },{
                    id: "edit_proj",
                    text: '编辑名称',
                    icon: 'fa fa-edit'
                }, {
                    id: "del_proj",
                    text: '删除项目',
                    icon: 'fa fa-times'
                }],
                onMenuClick: function (event) {
                    switch (event.menuItem.id) {
                    case "share":
                        $("#users > option:gt(0)").remove();
                        $.get("/rest/project/users?rand="+Math.random(), function(data){
                            $.each(data, function(index, value){
                                $("#users").append($('<option>',{
                                    text: value.userName + "(" + value.userNo + ")",
                                    value: value.userNo
                                }));
                            });
                            $("#shareProject").modal();
                        });
                        break;
                    case "csharpCode":
                        window.ajaxutil.generate_code("csharp");
                        break;
                    case "javaCode":
                        window.ajaxutil.generate_code("java");
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
                                window.ajaxutil.reload_projects();
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
        },
        render_grid : function () {
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
                            $.get("/rest/task?project_id=" + current_project+"&rand="+Math.random(), function (data) {
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
                            $(".step1").show();
                            $(".step2").hide();
                            $(".step3-1").hide();
                            $(".step3-2").hide();
                            $(".step3-2-1").hide();
                            $(".step3-2-2").hide();
                            $(".step3-2-1-1").hide();
                            $(".step3-2-1-2").hide();
                            $(".step3-3").hide();
                            $(".step3-3-1").hide();
                            $("#page1").attr('is_update', '0');
                            $("#page1").modal({"backdrop": "static"});
                            window.ajaxutil.reload_dbservers();
                            break;
                        case 'editDAO':
                            var records = w2ui['grid'].getSelection();
                            if (records.length > 0) {
                                var record = w2ui['grid'].get(records[0]);
                                if (record != undefined) {
                                    $("select[id$=servers] > option:gt(0)").remove();
                                    $(".step1").show();
                                    $(".step2").hide();
                                    $(".step3-1").hide();
                                    $(".step3-2").hide();
                                    $(".step3-2-2").hide();
                                    $(".step3-2-1").hide();
                                    $(".step3-2-1-1").hide();
                                    $(".step3-2-1-2").hide();
                                    $(".step3-3").hide();
                                    $(".step3-3-1").hide();
                                    window.ajaxutil.reload_dbservers(function () {
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
                            window.ajaxutil.generate_code("java");
                            break;
                        case 'csharpCode':
                           window.ajaxutil.generate_code("csharp");
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

        },
    };

    window.render = new Render();

})(window);