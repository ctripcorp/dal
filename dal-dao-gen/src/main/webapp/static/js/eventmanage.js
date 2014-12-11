
(function (window, undefined) {

    var Render = function () {

    };

    var refreshAllApproveDAO = function () {
        w2ui['grid'].clear();
        w2ui['previewgrid'].clear();
        cblock($("body"));
        $.get("/rest/task/getMyApproveTask?rand=" + Math.random(),function (data) {
            var allApproveDAO = [];
            $.each(data, function (index, value) {
                value.recid = allApproveDAO.length + 1;
                if (value.task_type == "table_view_sp") {
                    value.task_desc = "标准DAO";
                } else if (value.task_type == "auto") {
                    value.task_desc = "SQL构建";
                } else {
                    value.task_desc = "自定义SQL";
                }
                allApproveDAO.push(value);
            });
            w2ui['grid'].add(allApproveDAO);
            $("body").unblock();
        }).fail(function (data) {
            alert("获取所有待审批DAO失败!");
            $("body").unblock();
        });
    };

    var refreshApproveTaskDetail = function(){
        w2ui['previewgrid'].clear();
        var records = w2ui['grid'].getSelection();
        var record = w2ui['grid'].get(records[0]);
        if(record==null || record==''){
            alert('请先选择一个待审批事件!');
            return;
        }
        cblock($("body"));
        $.get("/rest/task/getMyApproveTaskDetail?taskId=" + record['task_id'] + "&taskType=" + record['task_type'] + "&rand=" + Math.random(),function (data) {
            var allApproveTaskDetail = [];
            $.each(data, function (index, value) {
                value.recid = allApproveTaskDetail.length + 1;
                allApproveTaskDetail.push(value);
            });
            w2ui['previewgrid'].add(allApproveTaskDetail);
            $("body").unblock();
        }).fail(function (data) {
            alert("获取待审批事件Detail失败!");
            $("body").unblock();
        });
    };

    var approveOk = function() {

    };

    var approveRefuse = function() {

    };

    Render.prototype = {
        renderAll: function(){
            $('#main_layout').height($(document).height() - 55);

            window.render.render_layout($('#main_layout'));

            window.render.render_grid();

            window.render.render_preview();

            $(window).resize(function () {
                $('#main_layout').height($(document).height() - 50);
            });

            w2ui['grid_toolbar'].click('refreshAllApproveDAO', null);
        },
        render_layout: function (render_obj) {
            $(render_obj).w2layout({
                name: 'main_layout',
                panels: [{
                    type: 'main'
                },{
                    type: 'preview',
                    size: '70%',
                    resizable: true
                }],
                onResizing: function(event) {
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
                    toolbarAdd: false,
                    toolbarDelete: false,
                    toolbarEdit: false
                },
                multiSelect: false,
                toolbar: {
                    items: [{
                        type: 'break'
                    }, {
                        type: 'button',
                        id: 'refreshAllApproveDAO',
                        caption: '刷新',
                        icon: 'fa fa-refresh'
                    }],
                    onClick: function (target, data) {
                        switch (target) {
                            case 'refreshAllApproveDAO':
                                refreshAllApproveDAO();
                                break;
                        }
                    }
                },
                searches: [{
                    field: 'task_desc',
                    caption: 'DAO类型',
                    type: 'text'
                }, {
                    field: 'create_user_name',
                    caption: '审批发起人',
                    type: 'text'
                }, {
                    field: 'str_create_time',
                    caption: '审批发起时间',
                    type: 'text'
                }],
                columns: [{
                    field: 'recid',
                    caption: '待审批事件编号',
                    size: '25%',
                    sortable: true,
                    resizable:true
                }, {
                    field: 'task_desc',
                    caption: '待审批事件类型',
                    size: '25%',
                    sortable: true,
                    resizable:true
                }, {
                    field: 'create_user_name',
                    caption: '审批发起人',
                    size: '35%',
                    sortable: true,
                    resizable:true
                }, {
                    field: 'str_create_time',
                    caption: '审批发起时间',
                    size: '15%',
                    sortable: true,
                    resizable:true
                }],
                records: [],
                onSelect: function (event) {
                    var grid = this;
                    setTimeout(function () {
                        refreshApproveTaskDetail();
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
                    toolbarEdit: false
                },
                multiSelect: false,
                toolbar: {
                    items: [{
                        type: 'break'
                    }, {
                        type: 'button',
                        id: 'refreshApproveTaskDetail',
                        caption: '刷新',
                        icon: 'fa fa-refresh'
                    }, {
                        type: 'button',
                        id: 'approveOk',
                        caption: '同意',
                        icon: 'fa fa-check'
                    }, {
                        type: 'button',
                        id: 'approveRefuse',
                        caption: '拒绝',
                        icon: 'fa fa-times'
                    }],
                    onClick: function (target, data) {
                        switch (target) {
                            case 'refreshApproveTaskDetail':
                                refreshApproveTaskDetail();
                                break;
                            case 'addDbSetEntry':
                                approveOk();
                                break;
                            case 'editDbSetEntry':
                                approveRefuse();
                                break;
                        }
                    }
                },
                searches: [{
                    field: 'attrName',
                    caption: '属性名称',
                    type: 'text'
                }, {
                    field: 'attrValue',
                    caption: '属性值',
                    type: 'text'
                }],
                columns: [{
                    field: 'attrName',
                    caption: '属性名称',
                    size: '20%',
                    sortable: true,
                    resizable:true,
                    style: 'background-color: #efefef; border-bottom: 1px solid white; padding-right: 5px;',
                    attr: "align=right"
                }, {
                    field: 'attrValue',
                    caption: '属性值',
                    size: '80%',
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