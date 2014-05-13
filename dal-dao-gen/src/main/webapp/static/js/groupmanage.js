(function (window, undefined) {

    var Render = function () {

    };

    var refreshGroup = function () {
        w2ui['grid'].clear();
        var current_project = 80;
        cblock($("body"));
        $.get("/rest/task?project_id=" + current_project + "&rand=" + Math.random(),function (data) {
            var allTasks = [];
            $.each(data.tableViewSpTasks, function (index, value) {
                value.recid = allTasks.length + 1;
                value.task_type = "table_view_sp";
                value.task_desc = "表/视图/存储过程";
                if (value.table_names != null && value.table_names != "") {
                    value.sql_content = value.table_names;
                }
                if (value.sp_names != null && value.sp_names != "") {
                    if (value.sql_content == null || value.sql_content == "")
                        value.sql_content = value.sp_names;
                    else
                        value.sql_content = value.sql_content + "," + value.sp_names;
                }
                if (value.view_names != null && value.view_names != "") {
                    if (value.sql_content == null || value.sql_content == "")
                        value.sql_content = value.view_names;
                    else
                        value.sql_content = value.sql_content + "," + value.view_names;
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
                value.task_desc = "自定义查询";
                allTasks.push(value);
            });
            w2ui['grid'].add(allTasks);
            $("body").unblock();
        }).fail(function (data) {
                alert("获取所有DAO失败!");
            });
    };

    var addGroup = function(){
        $("#groupModal").modal({
            "backdrop": "static"
        });
        $("#save_group").click(function(){
            var group_name = $("name").val();
            var comment = $("comment").val();
            if(group_name==null){
                $("#error_msg").html('请输入Group Name!');
            }
        });
    };

    var editGroup = function(){
        $("#groupModal").modal({
            "backdrop": "static"
        });
    };

    var delGroup = function(){
        var records = w2ui['grid'].getSelection();
        var record = w2ui['grid'].get(records[0]);
        if(record!=null){
            if (confirm("Are you sure to delete?")) {

            }
        }else{
            alert('请选择一个group！');
        }

    };

    Render.prototype = {
        render_layout: function (render_obj) {
            $(render_obj).w2layout({
                name: 'main_layout',
                panels: [
                    {
                        type: 'main'
                    }
                ],
                onResizing: function (event) {
                }
            });
        },
        render_grid: function () {
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
                    items: [
                        {
                            type: 'break'
                        },
                        {
                            type: 'button',
                            id: 'refreshGroup',
                            caption: '刷新',
                            icon: 'fa fa-refresh'
                        },
                        {
                            type: 'button',
                            id: 'addGroup',
                            caption: '添加Group',
                            icon: 'fa fa-plus'
                        },
                        {
                            type: 'button',
                            id: 'editGroup',
                            caption: '修改Group',
                            icon: 'fa fa-edit'
                        },
                        {
                            type: 'button',
                            id: 'delGroup',
                            caption: '删除Group',
                            icon: 'fa fa-times'
                        }
                    ],
                    onClick: function (target, data) {
                        switch (target) {
                            case 'refreshGroup':
                                refreshGroup();
                                break;
                            case 'addGroup':
                                addGroup();
                                break;
                            case 'editGroup':
                                editGroup();
                                break;
                            case 'delGroup':
                                delGroup();
                                break;
                        }
                    }
                },
                searches: [
                    {
                        field: 'db_name',
                        caption: 'Group Name',
                        type: 'text'
                    },
                    {
                        field: 'table_name',
                        caption: '备注',
                        type: 'text'
                    }
                ],
                columns: [
                    {
                        field: 'db_name',
                        caption: 'Group Name',
                        size: '50%',
                        sortable: true,
                        attr: 'align=center'
                    },
                    {
                        field: 'class_name',
                        caption: '备注',
                        size: '50%',
                        sortable: true
                    }
                ],
                records: [],
                onDblClick: function (target, data) {
                }
            }));
        }
    };

    window.render = new Render();

    $('#main_layout').height($(document).height() - 50);

    window.render.render_layout($('#main_layout'));

    window.render.render_grid();

    w2ui['grid_toolbar'].click('refreshGroup', null);

    $(window).resize(function () {
        $('#main_layout').height($(document).height() - 50);
    });

})(window);