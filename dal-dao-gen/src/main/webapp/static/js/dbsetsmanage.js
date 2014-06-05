
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
        w2ui['previewgrid'].clear();
        var current_group = w2ui['grid'].current_group;
        if (current_group == undefined) {
            if (w2ui['sidebar'].nodes.length < 1 || w2ui['sidebar'].nodes[0].nodes.length < 1)
                return;
            current_group = w2ui['sidebar'].nodes[0].nodes[0].id;
        }
        cblock($("body"));
        $.get("/rest/groupdbset/getDbset?groupId=" + current_group + "&rand=" + Math.random(),function (data) {
            var allGroupDbset = [];
            $.each(data, function (index, value) {
                value.recid = allGroupDbset.length + 1;
                allGroupDbset.push(value);
            });
            w2ui['grid'].add(allGroupDbset);
            $("body").unblock();
        }).fail(function (data) {
                alert("获取组内所有DatabaseSet失败!");
            });
    };

    var refreshDbSetEntry = function(){
        w2ui['previewgrid'].clear();
        var records = w2ui['grid'].getSelection();
        var record = w2ui['grid'].get(records[0]);
        if(record==null || record==''){
            return;
        }
        cblock($("body"));
        $.get("/rest/groupdbset/getDbsetEntry?dbsetId=" + record['id'] + "&rand=" + Math.random(),function (data) {
            var allGroupDbsetEnty = [];
            $.each(data, function (index, value) {
                value.recid = allGroupDbsetEnty.length + 1;
                allGroupDbsetEnty.push(value);
            });
            w2ui['previewgrid'].add(allGroupDbsetEnty);
            $("body").unblock();
        }).fail(function (data) {
                alert("获取组内所有DatabaseSetEntry失败!");
            });
    };

    var addDbSet = function () {
        $("#adddbset_error_msg").html('');
        var current_group = w2ui['grid'].current_group;
        if (current_group == null || current_group == '') {
            alert('请先选择Group');
            return;
        }
        $("#dbsetname").val('');
        $("#addDbsetModal").modal({
            "backdrop": "static"
        });
    };

    var editDbSet = function(){
        $("#updatedbset_error_msg").html('');
        var current_group = w2ui['grid'].current_group;
        if (current_group == null || current_group == '') {
            alert('请先选择Group');
            return;
        }

        var records = w2ui['grid'].getSelection();
        var record = w2ui['grid'].get(records[0]);
        if(record==null || record==''){
            alert("请先选择一个databaseSet");
            return;
        }

        $("#dbsetname2").val(record['name']);
        $("#provider2").val(record['provider']);
        $("#shardingStrategy2").val(record['shardingStrategy']);
        $("#updateDbsetModal").modal({
            "backdrop": "static"
        });
    };

    var delDbSet = function(){
        var current_group = w2ui['grid'].current_group;
        if (current_group == null || current_group == '') {
            alert('请先选择Group');
            return;
        }

        var records = w2ui['grid'].getSelection();
        var record = w2ui['grid'].get(records[0]);
        if(record==null || record==''){
            alert("请先选择一个databaseSet");
            return;
        }

        if (confirm("Are you sure to delete?")) {
            $.post("/rest/groupdbset/deletedbset", {
                groupId : w2ui['grid'].current_group,
                dbsetId : record['id']
            },function (data) {
                if (data.code == "OK") {
                    refreshDbSet();
                } else {
                    alert(data.info);
                }
            }).fail(function (data) {
                    alert("执行异常");
                });
        }
    };

    var addDbSetEntry = function(){
        $("#adddbsetentry_error_msg").html('');
        var records = w2ui['grid'].getSelection();
        var record = w2ui['grid'].get(records[0]);
        if(record==null || record==''){
            alert("请先选择一个databaseSet");
            return;
        }
        window.ajaxutil.reload_dbservers(null,true);
        $("#addDbsetEntryModal").modal({
            "backdrop": "static"
        });
    };

    var editDbSetEntry = function(){
        var records = w2ui['previewgrid'].getSelection();
        var record = w2ui['previewgrid'].get(records[0]);
        if(record==null || record==''){
            alert("请先选择一个databaseSet Entry");
            return;
        }
        $("#dbsetentryname2").val(record['name']);
        $("#databaseType2").val(record['databaseType']);
        $("#sharding2").val(record['sharding']);
        cblock($("body"));

        $.get("/rest/db/dbs?rand=" + Math.random()+"&groupDBs=true").done(function (data) {

            if ($("#databases2")[0] != undefined && $("#databases2")[0].selectize != undefined) {
                $("#databases2")[0].selectize.clearOptions();
            } else {
                $("#databases2").selectize({
                    valueField: 'id',
                    labelField: 'title',
                    searchField: 'title',
                    sortField: 'title',
                    options: [],
                    create: false
                });
            }

            var allServers = [];
            $.each(data, function (index, value) {
                allServers.push({
                    id: value,
                    title: value
                });
            });
            $("#databases2")[0].selectize.addOption(allServers);
            $("#databases2")[0].selectize.refreshOptions(false);

            $("#databases2")[0].selectize.setValue(record['connectionString']);

            $("body").unblock();
        }).fail(function (data) {
                $("body").unblock();
                alert(data);
            });
        $("#updateDbsetEntryModal").modal({
            "backdrop": "static"
        });
    };

    var delDbSetEntry = function(){
        var current_group = w2ui['grid'].current_group;
        if (current_group == null || current_group == '') {
            alert('请先选择Group');
            return;
        }

        var records1 = w2ui['grid'].getSelection();
        var record1 = w2ui['grid'].get(records1[0]);
        if(record1==null || record1==''){
            alert("请先选择一个databaseSet");
            return;
        }

        var records2 = w2ui['previewgrid'].getSelection();
        var record2 = w2ui['previewgrid'].get(records2[0]);
        if(record2==null || record2==''){
            alert("请先选择一个databaseSet Entry");
            return;
        }

        if (confirm("Are you sure to delete?")) {
            $.post("/rest/groupdbset/deletedbsetEntry", {
                groupId : w2ui['grid'].current_group,
                dbsetEntryId : record2['id']
            },function (data) {
                if (data.code == "OK") {
                    refreshDbSetEntry();
                } else {
                    alert(data.info);
                }
            }).fail(function (data) {
                    alert("执行异常");
                });
        }

    };

    Render.prototype = {
        renderAll: function(){
            $('#main_layout').height($(document).height() - 55);

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
                +'ALL DAL Team'
                +"</div>"
                +'<div id="jstree_groups"></div>');

            $('#jstree_groups').on('select_node.jstree', function (e, obj) {
                w2ui['grid'].current_group = obj.node.id;
                w2ui['grid_toolbar'].click('refreshDbSet', null);
            }).jstree({ 
                'core' : {
                    'check_callback' : true,
                    'multiple': false,
                    'data' : {
                      'url' : function (node) {
                        return node.id == "#" ? "/rest/groupdbset?root=true&rand=" + Math.random() : "/rest/groupdbset?rand=" + Math.random();
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
                    toolbarEdit: false,
                    selectColumn: true
                },
                multiSelect: false,
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
                            case 'refreshDbSet':
                                refreshDbSet();
                                break;
                            case 'addDbSet':
                                addDbSet();
                                break;
                            case 'editDbSet':
                                editDbSet();
                                break;
                            case 'delDbSet':
                                delDbSet();
                                break;
                            case 'showDalConfigDemo':
                                showDalConfigDemo();
                                break;
                        }
                    }
                },
                searches: [{
                    field: 'name',
                    caption: 'Name',
                    type: 'text'
                }, {
                    field: 'provider',
                    caption: 'provider',
                    type: 'text'
                }, {
                    field: 'shardingStrategy',
                    caption: 'shardingStrategy',
                    type: 'text'
                }],
                columns: [{
                    field: 'name',
                    caption: 'databaseSet Name',
                    size: '25%',
                    sortable: true,
                    resizable:true
                }, {
                    field: 'provider',
                    caption: 'provider',
                    size: '15%',
                    sortable: true,
                    resizable:true
                }, {
                    field: 'shardingStrategy',
                    caption: 'shardStrategy',
                    size: '60%',
                    sortable: true,
                    resizable:true
                }],
                records: [],
                onSelect: function (event) {
                    var grid = this;
                    setTimeout(function () {
                        refreshDbSetEntry();
                    }, 200);
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
                    toolbarAdd: false,
                    toolbarDelete: false,
                    toolbarEdit: false,
                    selectColumn: true
                },
                multiSelect: false,
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
                            case 'refreshDbSetEntry':
                                refreshDbSetEntry();
                                break;
                            case 'addDbSetEntry':
                                addDbSetEntry();
                                break;
                            case 'editDbSetEntry':
                                editDbSetEntry();
                                break;
                            case 'delDbSetEntry':
                                delDbSetEntry();
                                break;
                        }
                    }
                },
                searches: [{
                    field: 'name',
                    caption: 'name',
                    type: 'text'
                }, {
                    field: 'databaseType',
                    caption: 'databaseType',
                    type: 'text'
                }, {
                    field: 'sharding',
                    caption: 'sharding',
                    type: 'text'
                }, {
                    field: 'connectionString',
                    caption: 'connectionString',
                    type: 'text'
                }],
                columns: [{
                    field: 'name',
                    caption: 'databaseSet Entry Name',
                    size: '25%',
                    sortable: true,
                    resizable:true
                }, {
                    field: 'databaseType',
                    caption: 'databaseType',
                    size: '15%',
                    sortable: true,
                    resizable:true
                }, {
                    field: 'sharding',
                    caption: 'sharding',
                    size: '15%',
                    sortable: true,
                    resizable:true
                }, {
                    field: 'connectionString',
                    caption: 'connectionString',
                    size: '45%',
                    sortable: true,
                    resizable:true
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

        $(document.body).on('click', '#save_adddbset', function (event) {
            var dbsetname = $("#dbsetname").val();
            var provider = $("#provider").val();
            var shardingStrategy = $("#shardingStrategy").val();
            if (dbsetname == null || dbsetname == "") {
                $("#adddbset_error_msg").html('databaseSet name 不能为空!');
                return;
            }
            $.post("/rest/groupdbset/addDbset", {
                "name": dbsetname,
                "provider":provider,
                "shardingStrategy":shardingStrategy,
                "groupId":w2ui['grid'].current_group
            }, function (data) {
                if (data.code == "OK") {
                    $("#addDbsetModal").modal('hide');
                    refreshDbSet();
                } else {
                    $("#adddbset_error_msg").html(data.info);
                }
            });
        });

        $(document.body).on('click', '#save_adddbsetentry', function (event) {
            var dbsetentryname = $("#dbsetentryname").val();
            var databaseType = $("#databaseType").val();
            var sharding = $("#sharding").val();
            var  connectionString = $("#databases").val();
            if (dbsetentryname == null || dbsetentryname == "") {
                $("#adddbsetentry_error_msg").html('databaseSet Entry name 不能为空!');
                return;
            }
            if (connectionString == null || connectionString == "") {
                $("#adddbsetentry_error_msg").html('请选择connectionString!');
                return;
            }
            var records = w2ui['grid'].getSelection();
            var record = w2ui['grid'].get(records[0]);
            $.post("/rest/groupdbset/addDbsetEntry", {
                "name": dbsetentryname,
                "databaseType":databaseType,
                "sharding":sharding,
                "connectionString":connectionString,
                "dbsetId":record['id'],
                "groupId":w2ui['grid'].current_group
            }, function (data) {
                if (data.code == "OK") {
                    $("#addDbsetEntryModal").modal('hide');
                    refreshDbSetEntry();
                } else {
                    $("#adddbsetentry_error_msg").html(data.info);
                }
            });
        });

        $(document.body).on('click', '#save_updatedbset', function (event) {
            var dbsetname = $("#dbsetname2").val();
            var provider = $("#provider2").val();
            var shardingStrategy = $("#shardingStrategy2").val();
            if (dbsetname == null || dbsetname == "") {
                $("#updatedbset_error_msg").html('databaseSet name 不能为空!');
                return;
            }
            var records = w2ui['grid'].getSelection();
            var record = w2ui['grid'].get(records[0]);
            $.post("/rest/groupdbset/updateDbset", {
                "id":record['id'],
                "name": dbsetname,
                "provider":provider,
                "shardingStrategy":shardingStrategy,
                "groupId":w2ui['grid'].current_group
            }, function (data) {
                if (data.code == "OK") {
                    $("#updateDbsetModal").modal('hide');
                    refreshDbSet();
                } else {
                    $("#updatedbset_error_msg").html(data.info);
                }
            });
        });

        $(document.body).on('click', '#save_updatedbsetentry', function (event) {
            $("#updatedbsetentry_error_msg").html('');
            var dbsetentryname = $("#dbsetentryname2").val();
            var databaseType = $("#databaseType2").val();
            var sharding = $("#sharding2").val();
            var connectionString = $("#databases2").val();
            if (dbsetentryname == null || dbsetentryname == "") {
                $("#updatedbsetentry_error_msg").html('databaseSet entry name 不能为空!');
                return;
            }
            if (connectionString == null || connectionString == "") {
                $("#updatedbsetentry_error_msg").html('请选择connectionString');
                return;
            }
            var records = w2ui['previewgrid'].getSelection();
            var record = w2ui['previewgrid'].get(records[0]);
            $.post("/rest/groupdbset/updateDbsetEntry", {
                "id":record['id'],
                "name": dbsetentryname,
                "databaseType":databaseType,
                "sharding":sharding,
                "connectionString":connectionString,
                "dbsetId":record['databaseSet_Id'],
                "groupId":w2ui['grid'].current_group
            }, function (data) {
                if (data.code == "OK") {
                    $("#updateDbsetEntryModal").modal('hide');
                    refreshDbSetEntry();
                } else {
                    $("#updatedbsetentry_error_msg").html(data.info);
                }
            });
        });

        $(document.body).on('change', '#databases', function (event) {
            $("#dbsetentryname").val($("#databases").val());
        });

        $(document.body).on('change', '#databases2', function (event) {
            $("#dbsetentryname2").val($("#databases2").val());
        });

    });

})(window);