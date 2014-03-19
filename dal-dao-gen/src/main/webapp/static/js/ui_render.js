//向导注释
//step1
//step2
//step3-1 -> step3-2
(function (window, undefined) {

    var Render = function () {

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
                    type: 'main',
                    style: 'background-color: white;'
                },{ 
                    type: 'preview', 
                    size: '50%', 
                    resizable: true
                }]
            });
        },
        render_sidebar: function () {
            //Begin tree side bar
            w2ui['main_layout'].content('left', $().w2sidebar({
                name: 'sidebar',
                img: null,
                topHTML: '<div style="background-color: #eee; padding: 10px 5px 10px 20px; border-bottom: 1px solid silver">'
                +'<a id="addProj" href="javascript:;">'
                +'<i class="fa fa-plus"></i>添加</a>'
                +'&nbsp;&nbsp;<a id="editProj" href="javascript:;">'
                +'<i class="fa fa-edit"></i>修改</a>'
                +'&nbsp;&nbsp;<a id="delProj" href="javascript:;">'
                +'<i class="fa fa-times"></i>删除</a>'
                +'&nbsp;&nbsp;<a id="shareProj" href="javascript:;">'
                +'<i class="fa fa-twitter"></i>共享</a></div>',
                nodes: [{
                    id: 'all_projects',
                    text: '所有项目',
                    icon: 'fa fa-folder-o',
                    // plus: true,
                    group: true,
                }],
            }));
        },
        render_grid: function () {
            var existsGrid = w2ui['grid'];
            if (existsGrid != undefined) {
                return;
            }

            $().w2layout({
                name: 'sub_layout',
                panels: [{ 
                        type: 'top',  
                        size: 45, 
                        resizable: true,
                    },{ 
                        type: 'left', 
                        size: 271, 
                        resizable: true
                    },{ 
                        type: 'main'
                    }]
            });

            w2ui['main_layout'].content('preview', w2ui['sub_layout']);

            w2ui['sub_layout'].content('top', 
                '<div style="background-color: #eee; padding: 10px 5px 10px 20px; border-bottom: 1px solid silver"><a id="refreshFiles" href="javascript:;"><i class="fa fa-refresh"></i>刷新</a>&nbsp;&nbsp;<a id="downloadFiles" href="javascript:;"><i class="fa fa-download"></i>下载Zip包</a>&nbsp;&nbsp;<select id="viewCode"><option value="cs">C#</option><option value="java">Java</option></select></div>')

             //Begin tree side bar
            w2ui['sub_layout'].content('left', $().w2sidebar({
                name: 'sub_sidebar',
                img: null,
                nodes: [{
                    id: 'code_preview',
                    text: '代码预览',
                    icon: 'fa fa-folder-o',
                    // plus: true,
                    group: true,
                }]
            }));


            w2ui['sub_layout'].content('main',
             '<div id="code_editor" class="code_edit" style="height:100%"></div>');
            //End tree side bar

            var editor = ace.edit("code_editor");
            editor.setTheme("ace/theme/monokai");
            editor.getSession().setMode("ace/mode/csharp");

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
                        id: 'code',
                        caption: '生成代码',
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
                            $.get("/rest/task?project_id=" + current_project + "&rand=" + Math.random(), function (data) {
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
                                    value.class_name = "/";
                                    value.method_name = "/";
                                    allTasks.push(value);
                                });
                                $.each(data.autoTasks, function (index, value) {
                                    value.recid = allTasks.length + 1;
                                    value.task_type = "auto";
                                    value.task_desc = "SQL构建";
                                    value.class_name= value.table_name;
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
                                if(allTasks.length == 0){
                                    w2ui['grid_toolbar'].click('addDAO', null);
                                }
                            }).fail(function(data){
                                 alert("获取所有DAO失败!");
                            });
                            break;
                        case 'addDAO':
                            window.wizzard.clear();
                            $(".step1").show();
                            $(".step2-1").hide();
                            $(".step2-2").hide();
                            $(".step2-3").hide();
                            $(".step2-2-1").hide();
                            $(".step2-2-1-1").hide();
                            $(".step2-2-1-2").hide();
                            $(".step2-3").hide();
                            $(".step2-3-1").hide();
                            $("#page1").attr('is_update', '0');
                            $("#page1").modal({
                                "backdrop": "static"
                            });
                            window.ajaxutil.reload_dbservers();
                            break;
                        case 'editDAO':
                            window.wizzard.clear();
                            var records = w2ui['grid'].getSelection();
                            if(records.length > 0){
                                window.render.editDAO(records[0]);    
                            }
                            break;
                        case 'delDAO':
                            if (confirm("Are you sure to delete?")) {
                                var records = w2ui['grid'].getSelection();
                                var record = w2ui['grid'].get(records[0]);
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
                                    },
                                    function (data) {
                                        //$("#page1").modal('hide');
                                        w2ui["grid_toolbar"].click('refreshDAO', null);
                                    }).fail(function(data){
                                         alert("删除失败!");
                                    });
                            }
                            break;
                        case 'code':
                            //window.ajaxutil.generate_code("java");
                            $("#generateCode").modal();
                            break;
                        // case 'csharpCode':
                        //     window.ajaxutil.generate_code("csharp");
                        //     break;
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
                }, {
                    field: 'sql_content',
                    caption: '预览',
                    size: '50%'
                }, ],
                records: [],
                onDblClick: function (target, data) {
                    window.render.editDAO(data.recid);
                },
            }));

        },
        editDAO: function(recid){
            var record = w2ui['grid'].get(recid);
            if (record != undefined) {
                $(".step1").show();
                $(".step2-1").hide();
                $(".step2-2").hide();
                $(".step2-3").hide();
                $(".step2-2-1").hide();
                $(".step2-2-1-1").hide();
                $(".step2-2-1-2").hide();
                $(".step2-3").hide();
                $(".step2-3-1").hide();
                window.ajaxutil.reload_dbservers(function () {
                    $("#databases")[0].selectize.setValue(record.db_name);
                });
                $("#page1").attr('is_update', '1');
                $("#gen_style").val(record.task_type);
                $("#page1").modal({
                    "backdrop": "static"
                });
            }
            
        },
    };

    window.render = new Render();

})(window);