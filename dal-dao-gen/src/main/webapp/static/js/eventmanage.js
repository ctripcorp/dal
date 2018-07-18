(function (window, undefined) {
    var Render = function () {
    };

    var refreshAllApproveDAO = function () {
        w2ui['grid'].clear();
        w2ui['previewgrid'].clear();
        cblock($("body"));
        $.get("/rest/task/getMyApproveTask", {rand: Math.random()}, function (data) {
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

    var refreshApproveTaskDetail = function () {
        w2ui['previewgrid'].clear();
        var records = w2ui['grid'].getSelection();
        var record = w2ui['grid'].get(records[0]);
        if (record == null || record == '') {
            alert('请先选择一个待审批事件!');
            return;
        }
        cblock($("body"));
        $.get("/rest/task/getMyApproveTaskDetail", {
            taskId: record['task_id'],
            taskType: record['task_type'],
            rand: Math.random()
        }, function (data) {
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

    var approveOk = function () {
        var records = w2ui['grid'].getSelection();
        var record = w2ui['grid'].get(records[0]);
        if (record == null || record == '') {
            alert('请先选择一个待审批事件!');
            return;
        }
        cblock($("body"));
        $.get("/rest/task/taskApproveOperation", {
            taskId: record['task_id'],
            taskType: record['task_type'],
            approveFlag: 2,
            rand: Math.random()
        }, function (data) {
            if (data['code'] != 'OK') {
                alert(data['info']);
            } else {
                alert("审批操作成功!");
                w2ui['grid_toolbar'].click('refreshAllApproveDAO', null);
            }
            $("body").unblock();
        }).fail(function (data) {
            alert("审批操作失败!");
            $("body").unblock();
        });
    };

    var approveRefuse = function () {
        $("#error_msg").empty();
        var records = w2ui['grid'].getSelection();
        var record = w2ui['grid'].get(records[0]);
        if (record == null || record == '') {
            alert('请先选择一个待审批事件!');
            return;
        }
        $("#refuseModal").modal({"backdrop": "static"});
    };

    Render.prototype = {
        renderAll: function () {
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
                }, {
                    type: 'preview',
                    size: '70%',
                    resizable: true
                }],
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
                        icon: 'glyphicon glyphicon-refresh'
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
                    caption: 'DAO 类型',
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
                    resizable: true
                }, {
                    field: 'task_desc',
                    caption: '待审批事件类型',
                    size: '25%',
                    sortable: true,
                    resizable: true
                }, {
                    field: 'create_user_name',
                    caption: '审批发起人',
                    size: '35%',
                    sortable: true,
                    resizable: true
                }, {
                    field: 'str_create_time',
                    caption: '审批发起时间',
                    size: '15%',
                    sortable: true,
                    resizable: true
                }],
                records: [],
                onSelect: function () {
                    var grid = this;
                    setTimeout(function () {
                        refreshApproveTaskDetail();
                    }, 200);
                }
            }));
        },
        render_preview: function () {
            var existsGrid = w2ui['previewgrid'];
            if (existsGrid != undefined) {
                return;
            }

            w2ui['main_layout'].content('preview', $().w2grid(
                {
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
                        items: [
                            {
                                type: 'break'
                            },
                            {
                                type: 'button',
                                id: 'refreshApproveTaskDetail',
                                caption: '刷新',
                                icon: 'glyphicon glyphicon-refresh'
                            },
                            {
                                type: 'button',
                                id: 'approveOk',
                                caption: '同意',
                                icon: 'glyphicon glyphicon-ok'
                            },
                            {
                                type: 'button',
                                id: 'approveRefuse',
                                caption: '拒绝',
                                icon: 'glyphicon glyphicon-remove'
                            }],
                        onClick: function (target, data) {
                            switch (target) {
                                case 'refreshApproveTaskDetail':
                                    refreshApproveTaskDetail();
                                    break;
                                case 'approveOk':
                                    approveOk();
                                    break;
                                case 'approveRefuse':
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
                    columns: [
                        {
                            field: 'attrName',
                            caption: '属性名称',
                            size: '20%',
                            sortable: true,
                            resizable: true,
                            style: 'background-color: #efefef; border-bottom: 1px solid white; padding-right: 5px;',
                            attr: "align=right"
                        },
                        {
                            field: 'attrValue',
                            caption: '属性值',
                            size: '80%',
                            sortable: true,
                            resizable: true
                        }],
                    records: [],
                    onDblClick: function (target, data) {
                    }
                }));
        }
    };

    window.render = new Render();
    render.renderAll();

    $(function () {
        $(document.body).on("click", "#refuse_dao", function () {
            var records = w2ui['grid'].getSelection();
            var record = w2ui['grid'].get(records[0]);
            if (record == null || record == '') {
                alert('请先选择一个待审批事件!');
                return;
            }
            var approveMsg = $("#approveMsg").val();
            if (approveMsg == null || $.trim(approveMsg) == '') {
                $("#error_msg").html('请输入审批意见！');
                return;
            }
            cblock($("body"));
            $.get("/rest/task/taskApproveOperation", {
                    taskId: record['task_id'],
                    taskType: record['task_type'],
                    approveFlag: 3,
                    approveMsg: $("#approveMsg").val(),
                    rand: Math.random()
                },
                function (data) {
                    if (data['code'] != 'OK') {
                        $("#error_msg").html(data['info']);
                    } else {
                        $("#refuseModal").modal('hide');
                        w2ui['grid_toolbar'].click('refreshAllApproveDAO', null);
                    }
                    $("body").unblock();
                }
            ).fail(function (data) {
                    alert("审批操作失败!");
                    $("#refuseModal").modal('hide');
                    $("body").unblock();
                });
        });
    });
})(window);