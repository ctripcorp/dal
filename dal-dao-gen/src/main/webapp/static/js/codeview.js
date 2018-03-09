(function (window, undefined) {
    var Render = function () {
    };

    var refreshDAO = function () {
        w2ui['grid'].clear();
        var current_project = w2ui['grid'].current_project;
        if (current_project == undefined) {
            if (w2ui['sidebar'].nodes.length < 1 || w2ui['sidebar'].nodes[0].nodes.length < 1)
                return;
            current_project = w2ui['sidebar'].nodes[0].nodes[0].id;
        }
        cblock($("body"));
        $.get("/rest/task?project_id=" + current_project + "&rand=" + Math.random(), function (data) {
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

    Render.prototype = {
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
                }],
                onResizing: function (event) {
                    // ace.edit("code_editor").resize();
                }
            });
        },
        render_sidebar: function () {
            w2ui['main_layout'].content(
                'left',
                '<div style="color: #34495E !important;font-size: 15px;background-color: #eee; padding: 7px 5px 6px 20px; border-bottom: 1px solid silver">'
                + '所有 DAL 项目'
                + "</div>"
                + '<div id="jstree_groups"></div>');

            $('#jstree_groups').on('select_node.jstree', function (e, obj) {
                if (obj.node.id != -1 && obj.node.original.namespace != undefined) {
                    window.render.render_grid();
                    w2ui['grid'].current_project = obj.node.id;
                    w2ui['grid_toolbar'].click('refreshDAO', null);
                }
            }).jstree({
                'core': {
                    'check_callback': true,
                    'multiple': false,
                    'data': {
                        'url': function (node) {
                            var url = "/rest/projectview?root=true&rand=" + Math.random();
                            if (node.id != "#") {
                                url = "/rest/projectview/groupprojects?groupId=" + node.id + "&rand=" + Math.random();
                            }
                            return url;
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
                toolbar: {
                    items: [{
                        type: 'break'
                    }, {
                        type: 'button',
                        id: 'refreshDAO',
                        caption: '刷新',
                        icon: 'glyphicon glyphicon-refresh'
                    }],
                    onClick: function (target, data) {
                        switch (target) {
                            case 'refreshDAO':
                                refreshDAO();
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
                    field: 'databaseSetName',
                    caption: '逻辑数据库',
                    size: '15%',
                    sortable: true,
                    attr: 'align=center',
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
                    size: '20%',
                    resizable: true
                }, {
                    field: 'comment',
                    caption: '方法描述',
                    size: '20%',
                    resizable: true
                }, {
                    field: 'sql_style',
                    caption: 'SQL 风格',
                    size: '5%',
                    resizable: true
                }, {
                    field: 'update_user_no',
                    caption: '最后修改',
                    size: '10%',
                    resizable: true
                }],
                records: [],
                onDblClick: function (target, data) {
                }
            }));
        }
    };

    window.render = new Render();
    $('#main_layout').height($(document).height() - 50);
    window.render.render_layout($('#main_layout'));
    window.render.render_sidebar();
    window.render.render_grid();

    $(window).resize(function () {
        $('#main_layout').height($(document).height() - 50);
    });

})(window);