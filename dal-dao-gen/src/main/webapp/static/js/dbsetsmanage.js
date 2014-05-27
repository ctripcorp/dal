
(function (window, undefined) {

    var Render = function () {

    };

    var showDalConfigDemo = function(){
        $("#dalConfigDemoModal").modal({
            "backdrop": "static"
        });
        var imgW = $("#dalConfigDemoModal img:eq(0)").width();
        while(imgW<=0){
            imgW = 1041;
        }
        $("#dalConfigDemoModal div[class='modal-dialog']").css({'min-width':imgW+40+'px'});
    };

    var refreshDbSet = function () {
        w2ui['grid'].clear();
        var current_group = w2ui['grid'].current_group;
        if (current_group == undefined) {
            if (w2ui['sidebar'].nodes.length < 1 || w2ui['sidebar'].nodes[0].nodes.length < 1)
                return;
            current_group = w2ui['sidebar'].nodes[0].nodes[0].id;
        }
//        cblock($("body"));
//        $.get("/rest/groupdb/groupdb?groupId=" + current_group + "&rand=" + Math.random(),function (data) {
//            var allGroupDBs = [];
//            $.each(data, function (index, value) {
//                value.recid = allGroupDBs.length + 1;
//                allGroupDBs.push(value);
//            });
//            w2ui['grid'].add(allGroupDBs);
//            $("body").unblock();
//        }).fail(function (data) {
//                alert("获取所有Member失败!");
//            });
    };

    Render.prototype = {
        renderAll: function(){
            $('#main_layout').height($(document).height() - 50);

            window.render.render_layout($('#main_layout'));

            window.render.render_sidebar();

            window.render.render_grid();

            window.render.render_preview();

            $(window).resize(function () {
                $('#main_layout').height($(document).height() - 50);
            });
        },
        render_layout: function (render_obj) {
            $(render_obj).w2layout({
                name: 'main_layout',
                panels: [{
                    type: 'left',
                    size: 271,
                    resizable: true,
                    style: 'border-right: 1px solid silver;'
                },{
                    type: 'main'
                },{
                    type: 'preview',
                    size: '50%',
                    resizable: true
                }],
                onResizing: function(event) {
                }
            });
        },
        render_sidebar: function () {
            w2ui['main_layout'].content('left', '<div style="color: #34495E !important;font-size: 15px;background-color: #eee; padding: 7px 5px 6px 20px; border-bottom: 1px solid silver">'
                +'All DAL Team'
                +"</div>"
                +'<div id="jstree_groups"></div>');

            $('#jstree_groups').on('select_node.jstree', function (e, obj) {
                window.render.render_grid();
                w2ui['grid'].current_group = obj.node.id;
                w2ui['grid_toolbar'].click('refreshDB', null);
            }).jstree({ 
                'core' : {
                    'check_callback' : true,
                    'multiple': false,
                    'data' : {
                      'url' : function (node) {
                        return node.id == "#" ? "/rest/groupdb?root=true&rand=" + Math.random() : "/rest/groupdb?rand=" + Math.random();
                      }
                    }
            }});
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
                        id: 'refreshDbSet',
                        caption: '刷新',
                        icon: 'fa fa-refresh'
                    }, {
                        type: 'button',
                        id: 'addDbSet',
                        caption: '添加dbSet',
                        icon: 'fa fa-plus'
                    }, {
                        type: 'button',
                        id: 'editDbSet',
                        caption: '修改dbSet',
                        icon: 'fa fa-edit'
                    }, {
                        type: 'button',
                        id: 'delDbSet',
                        caption: '删除dbSet',
                        icon: 'fa fa-times'
                    }, {
                        type: 'button',
                        id: 'showDalConfigDemo',
                        caption: '配置Demo',
                        icon: 'fa fa-question'
                    }],
                    onClick: function (target, data) {
                        switch (target) {
                            case 'refreshDB':
                                refreshDB();
                                break;
                            case 'addDB':
                                addDB();
                                break;
                            case 'editDB':
                                editDB();
                                break;
                            case 'delDB':
                                delDB();
                                break;
                            case 'showDalConfigDemo':
                                showDalConfigDemo();
                                break;
                        }
                    }
                },
                searches: [{
                    field: 'dbname',
                    caption: 'DB Name',
                    type: 'text'
                }, {
                    field: 'comment',
                    caption: '备注',
                    type: 'text'
                }],
                columns: [{
                    field: 'dbname',
                    caption: 'Name',
                    size: '25%',
                    sortable: true,
                    attr: 'align=center'
                }, {
                    field: 'comment',
                    caption: 'provider',
                    size: '25%',
                    sortable: true
                }, {
                    field: 'comment',
                    caption: 'shardStrategy',
                    size: '50%',
                    sortable: true
                }],
                records: [],
                onDblClick: function (target, data) {
                }
            }));
        },

        render_preview:function(){
            var existsGrid = w2ui['previewgrid'];
            if (existsGrid != undefined) {
                return;
            }

            w2ui['main_layout'].content('preview', $().w2grid({
                name: 'previewgrid',
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
                        id: 'refreshDbSetEntry',
                        caption: '刷新',
                        icon: 'fa fa-refresh'
                    }, {
                        type: 'button',
                        id: 'addDbSetEntry',
                        caption: '添加dbSet entry',
                        icon: 'fa fa-plus'
                    }, {
                        type: 'button',
                        id: 'editDbSetEntry',
                        caption: '修改dbSet entry',
                        icon: 'fa fa-edit'
                    }, {
                        type: 'button',
                        id: 'delDbSetEntry',
                        caption: '删除dbSet entry',
                        icon: 'fa fa-times'
                    }],
                    onClick: function (target, data) {
                        switch (target) {
                            case 'refreshDB':
                                refreshDB();
                                break;
                            case 'addDB':
                                addDB();
                                break;
                            case 'editDB':
                                editDB();
                                break;
                            case 'delDB':
                                delDB();
                                break;
                        }
                    }
                },
                searches: [{
                    field: 'dbname',
                    caption: 'DB Name',
                    type: 'text'
                }, {
                    field: 'comment',
                    caption: '备注',
                    type: 'text'
                }],
                columns: [{
                    field: 'dbname',
                    caption: 'Name',
                    size: '30%',
                    sortable: true,
                    attr: 'align=center'
                }, {
                    field: 'comment',
                    caption: 'databaseType',
                    size: '20%',
                    sortable: true
                }, {
                    field: 'comment',
                    caption: 'sharding',
                    size: '20%',
                    sortable: true
                }, {
                    field: 'comment',
                    caption: 'connectionString',
                    size: '30%',
                    sortable: true
                }],
                records: [],
                onDblClick: function (target, data) {
                }
            }));
        }
    };

    window.render = new Render();
    render.renderAll();

    jQuery(document).ready(function(){
        $(document.body).on('click', "#add_db", function(event){
            $.post("/rest/db/all_in_one", {"data": $("#all_in_one").val()}, function(data){
                if(data.code == "OK"){
                    $("#manageDb").modal('hide');
                    window.ajaxutil.reload_dbservers();
                    $("#page1").modal();
                }else{
                    alert(data.info);
                }

            });
        });

        $("#save_db").click(function(){
            var db_name = $("#databases").val();
            var comment = $("#comment").val();
            if(db_name==null || db_name==''){
                $("#error_msg").html('请选择DB!');
            }else{
                $.post("/rest/groupdb/add", {
                    groupId : w2ui['grid'].current_group,
                    dbname : db_name,
                    comment : comment
                },function (data) {
                    if (data.code == "OK") {
                        $("#dbModal").modal('hide');
                        refreshDB();
                    } else {
                        $("#error_msg").html(data.info);
                    }
                }).fail(function (data) {
                        $("#error_msg").html(data.info);
                    });
            }
        });

        $("#update_db").click(function(){
            var records = w2ui['grid'].getSelection();
            var record = w2ui['grid'].get(records[0]);
            $.post("/rest/groupdb/update", {
                groupId : w2ui['grid'].current_group,
                dbId : record['id'],
                comment : $("#comment2").val()
            },function (data) {
                if (data.code == "OK") {
                    $("#dbModal2").modal('hide');
                    refreshDB();
                } else {
                    $("#error_msg2").html(data.info);
                }
            }).fail(function (data) {
                    $("#error_msg2").html(data.info);
                });
        });

    });

})(window);