(function (window, undefined) {

    var loadDbConfig = function () {
        var dbConfig = w2ui['dbConfig'];
        var subDbConfig = w2ui['subDbConfig'];
        if (dbConfig == undefined) {
            dbConfig = $().w2grid({
                name: 'dbConfig',
                multiSelect: false,
                show: {
                    toolbar: true,
                    footer: true,
                    toolbarReload: false,
                    toolbarColumns: false,
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
                        id: 'refreshMaster',
                        caption: '刷新',
                        icon: 'fa fa-refresh'
                    }, {
                        type: 'button',
                        id: 'addMaster',
                        caption: '添加Master',
                        icon: 'fa fa-plus'
                    }, {
                        type: 'button',
                        id: 'editMaster',
                        caption: '修改Master',
                        icon: 'fa fa-edit'
                    }, {
                        type: 'button',
                        id: 'delMaster',
                        caption: '删除Master',
                        icon: 'fa fa-times'
                    } ],
                    onClick: function (target, data) {
                        switch (target) {
                            case 'refreshMaster':
                                w2ui['dbConfig'].clear();
                                $.get("/rest/configure/db", function (data) {
                                    var records = [];
                                    $.each(data, function (index, value) {
                                        records.push({
                                            recid: index,
                                            logicName: value.name,
                                            driverClass: value.setting.driver,
                                            connectStr: value.setting.jdbcUrl
                                        });
                                    });
                                    w2ui['dbConfig'].add(records);
                                });
                                break;
                            case 'addMaster':
                                $('.slave_row').hide();
                                $("#dbModal").attr("is_update", "0");
                                $("#dbModal").modal();
                                break;
                            case 'editMaster':
                                var records = w2ui['dbConfig'].getSelection();
                                var record = w2ui['dbConfig'].get(records[0]);
                                $("#dbModal").attr("is_update", "1");
                                $("#physic_db").val(record.logicName);
                                $("#driver_class").val(record.driverClass);
                                $("#connect_str").val(record.connectStr);
                                $('.slave_row').hide();
                                $("#dbModal").modal();
                                break;
                            case 'delMaster':
                                var records = w2ui['dbConfig'].getSelection();
                                var record = w2ui['dbConfig'].get(records[0]);
                                var url = sprintf('/rest/configure/db/%s', record.logicName);

                                $.ajax({
                                    type: 'DELETE',
                                    url: url,
                                    //dataType: 'json',
                                    success: function (data, status, event) {
                                        if (data.code == 'OK') {
                                            var el = w2ui['dbConfig_toolbar'];
                                            if (el)
                                                el.click('refreshMaster', null);
                                        }
                                    },
                                    error: function (data, status, event) {}
                                });
                                break;
                        }
                    }
                },
                searches: [{
                    field: 'logicName',
                    caption: '逻辑数据库名',
                    type: 'text'
                }, {
                    field: 'driverClass',
                    caption: '驱动类',
                    type: 'text'
                }, {
                    field: 'connectStr',
                    caption: '连接字符串',
                    type: 'text'
                } ],
                columns: [{
                    field: 'logicName',
                    caption: '逻辑数据库名',
                    size: '20%',
                    sortable: true,
                    resizable: true,
                    attr: 'align=center'
                }, {
                    field: 'driverClass',
                    caption: '驱动类',
                    size: '30%',
                    sortable: true,
                    resizable: true
                }, {
                    field: 'connectStr',
                    caption: '连接字符串',
                    size: '50%',
                    sortable: true,
                    resizable: true
                } ],
                onDblClick: function (event) {
                    w2ui['subDbConfig'].clear();
                    var masterName = w2ui['dbConfig'].get(event.recid).logicName;
                    $.get(sprintf("/rest/configure/db/%s/slave", masterName), function (data) {
                        var records = [];
                        $.each(data, function (index, value) {
                            records.push({
                                recid: index,
                                masterName: masterName,
                                logicName: value.name,
                                driverClass: value.setting.driver,
                                connectStr: value.setting.jdbcUrl
                            });
                        });
                        w2ui['subDbConfig'].add(records);
                    });
                },
                records: []
            });
        }
        if (subDbConfig == undefined) {
            subDbConfig = $().w2grid({
                name: 'subDbConfig',
                multiSelect: false,
                show: {
                    toolbar: true,
                    footer: true,
                    toolbarReload: false,
                    toolbarColumns: false,
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
                        id: 'addSlave',
                        caption: '添加Slave',
                        icon: 'fa fa-plus'
                    }, {
                        type: 'button',
                        id: 'editSlave',
                        caption: '修改Slave',
                        icon: 'fa fa-edit'
                    }, {
                        type: 'button',
                        id: 'delSlave',
                        caption: '删除Slave',
                        icon: 'fa fa-times'
                    } ],
                    onClick: function (target, data) {
                        switch (target) {
                            case 'addSlave':
                                $('.slave_row').show();
                                var records = w2ui['dbConfig'].getSelection();
                                if (records.length > 0) {
                                    var record = w2ui['dbConfig'].get(records[0]);
                                    $("#physic_db").val(record.logicName);
                                }
                                $("#dbModal").attr("is_update", "0");
                                $("#dbModal").modal();
                                break;
                            case 'editSlave':
                                var records = w2ui['subDbConfig'].getSelection();
                                var record = w2ui['subDbConfig'].get(records[0]);
                                $("#dbModal").attr("is_update", "1");
                                $("#physic_db").val(record.masterName);
                                $("#driver_class").val(record.driverClass);
                                $("#connect_str").val(record.connectStr);
                                $("#slave_name").val(record.logicName);
                                $('.slave_row').show();
                                $("#dbModal").modal();
                                break;
                            case 'delSlave':
                                var records = w2ui['subDbConfig'].getSelection();
                                var record = w2ui['subDbConfig'].get(records[0]);
                                var url = sprintf('/rest/configure/db/%s/slave/%s', record.masterName, record.logicName);

                                $.ajax({
                                    type: 'DELETE',
                                    url: url,
                                    //dataType: 'json',
                                    success: function (data, status, event) {
                                        if (data.code == 'OK') {
                                            w2ui['subDbConfig'].remove(record.recid);
                                        }
                                    },
                                    error: function (data, status, event) {}
                                });
                                break;
                        }
                    }
                },
                searches: [{
                    field: 'logicName',
                    caption: '逻辑数据库名',
                    type: 'text'
                }, {
                    field: 'driverClass',
                    caption: '驱动类',
                    type: 'text'
                }, {
                    field: 'connectStr',
                    caption: '连接字符串',
                    type: 'text'
                } ],
                columns: [{
                    field: 'logicName',
                    caption: 'Slave名',
                    size: '20%',
                    sortable: true,
                    resizable: true,
                    attr: 'align=center'
                }, {
                    field: 'driverClass',
                    caption: '驱动类',
                    size: '30%',
                    sortable: true,
                    resizable: true
                }, {
                    field: 'connectStr',
                    caption: '连接字符串',
                    size: '50%',
                    sortable: true,
                    resizable: true
                } ],
                records: []
            });
        }
        w2ui['main_layout'].content('main', dbConfig);
        w2ui['main_layout'].content('preview', subDbConfig);
        //dbConfig.reload();
        //$(dbConfig).trigger('reload');
        //$("#refreshMaster").trigger('click');
        var el = w2ui['dbConfig_toolbar'];
        if (el)
            el.click('refreshMaster', null);
    };

    var loadDasNodeConfig = function () {
        var dasNodeConfig = w2ui['dasNodeConfig'];
        var portConfig = w2ui['portConfig'];
        if (dasNodeConfig == undefined) {
            dasNodeConfig = $().w2grid({
                name: 'dasNodeConfig',
                show: {
                    toolbar: true,
                    footer: true,
                    toolbarReload: false,
                    toolbarColumns: false,
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
                        id: 'refreshNode',
                        caption: '刷新',
                        icon: 'fa fa-refresh'
                    }, {
                        type: 'button',
                        id: 'addNode',
                        caption: '添加节点',
                        icon: 'fa fa-plus'
                    }, {
                        type: 'button',
                        id: 'editNode',
                        caption: '修改节点',
                        icon: 'fa fa-edit'
                    }, {
                        type: 'button',
                        id: 'delNode',
                        caption: '删除节点',
                        icon: 'fa fa-times'
                    } ],
                    onClick: function (target, data) {
                        switch (target) {
                            case 'refreshNode':
                                w2ui['dasNodeConfig'].clear();
                                $.get("/rest/configure/node", function (data) {
                                    var records = [];
                                    $.each(data, function (index, value) {
                                        records.push({
                                            recid: index,
                                            ip: value.name,
                                            workDir: value.setting.directory,
                                            maxHeap: value.setting.maxHeapSize,
                                            defaultHeap: value.setting.startingHeapSize
                                        });
                                    });
                                    w2ui['dasNodeConfig'].add(records);
                                });
                                break;
                            case 'addNode':
                                $("#dasModal").attr("is_update", "0");
                                $("#dasModal").modal();
                                break;
                            case 'editNode':
                                var records = w2ui['dasNodeConfig'].getSelection();
                                var record = w2ui['dasNodeConfig'].get(records[0]);
                                $("#dasModal").attr("is_update", "1");
                                $("#ip_addr").val(record.ip);
                                $("#work_dir").val(record.workDir);
                                $("#max_heap").val(record.maxHeap);
                                $("#default_heap").val(record.defaultHeap);
                                $("#dasModal").modal();
                                break;
                            case 'delNode':
                                var records = w2ui['dasNodeConfig'].getSelection();
                                var record = w2ui['dasNodeConfig'].get(records[0]);
                                var url = sprintf('/rest/configure/node/%s', record.ip);

                                $.ajax({
                                    type: 'DELETE',
                                    url: url,
                                    //dataType: 'json',
                                    success: function (data, status, event) {
                                        if (data.code == 'OK') {
                                            w2ui['dasNodeConfig_toolbar'].click('refreshNode', null);
                                        }
                                    },
                                    error: function (data, status, event) {}
                                });
                                break;
                        }
                    }
                },
                searches: [{
                    field: 'ip',
                    caption: 'IP地址',
                    type: 'text'
                }, {
                    field: 'workDir',
                    caption: '工作目录',
                    type: 'text'
                } ],
                columns: [{
                    field: 'ip',
                    caption: 'IP地址',
                    size: '20%',
                    sortable: true,
                    attr: 'align=center'
                }, {
                    field: 'workDir',
                    caption: '工作目录',
                    size: '30%',
                    sortable: true
                }, {
                    field: 'maxHeap',
                    caption: '最大堆大小',
                    size: '25%',
                    sortable: true
                }, {
                    field: 'defaultHeap',
                    caption: '默认堆大小',
                    size: '25%',
                    sortable: true
                } ],
                records: []
            });
        }
        if (portConfig == undefined) {
            portConfig = $().w2grid({
                name: 'portConfig',
                show: {
                    toolbar: true,
                    footer: true,
                    toolbarReload: false,
                    toolbarColumns: false,
                    toolbarAdd: false,
                    toolbarDelete: false,
                    toolbarSearch: false,
                    //toolbarSave: true,
                    toolbarEdit: false
                },
                toolbar: {
                    items: [{
                        type: 'button',
                        id: 'refreshPort',
                        caption: '刷新',
                        icon: 'fa fa-refresh'
                    }, {
                        type: 'button',
                        id: 'addPort',
                        caption: '添加端口',
                        icon: 'fa fa-plus'
                    }, {
                        type: 'button',
                        id: 'delPort',
                        caption: '删除端口',
                        icon: 'fa fa-times'
                    } ],
                    onClick: function (target, data) {
                        switch (target) {
                            case 'refreshPort':
                                w2ui['portConfig'].clear();
                                $.get("/rest/configure/port", function (data) {
                                    var records = [];
                                    $.each(data.ports, function (index, value) {
                                        records.push({
                                            recid: index,
                                            port: value
                                        });
                                    });
                                    w2ui['portConfig'].add(records);
                                });
                                break;
                            case 'addPort':
                                $("#portModal").modal();
                                break;
                            case 'delPort':
                                var records = w2ui['portConfig'].getSelection();
                                var record = w2ui['portConfig'].get(records[0]);
                                var url = sprintf('/rest/configure/port/%s', record.port);

                                $.ajax({
                                    type: 'DELETE',
                                    url: url,
                                    //dataType: 'json',
                                    success: function (data, status, event) {
                                        if (data.code == 'OK') {
                                            w2ui['portConfig_toolbar'].click('refreshPort', null);
                                        }
                                    },
                                    error: function (data, status, event) {}
                                });
                                break;
                        }
                    }
                },
                columns: [{
                    field: 'port',
                    caption: '端口号',
                    size: '50%',
                    sortable: true,
                    resizable: true
                }],
                records: []
            });
        }
        w2ui['main_layout'].content('main', dasNodeConfig);
        w2ui['main_layout'].content('preview', portConfig);
        var el = w2ui['dasNodeConfig_toolbar'];
        if (el)
            el.click('refreshNode', null);
        var el2 = w2ui['portConfig_toolbar'];
        if (el2)
            el2.click('refreshPort', null);
    };

    var loadDasNodeMonitor = function () {
        var dasNodeMonitor = w2ui['dasNodeMonitor'];
        if (dasNodeMonitor == undefined) {
            dasNodeMonitor = $().w2grid({
                name: 'dasNodeMonitor',
                show: {
                    toolbar: true,
                    footer: true,
                    toolbarReload: false,
                    toolbarColumns: false,
                    toolbarAdd: false,
                    toolbarDelete: false,
                    toolbarSearch: false,
                    //toolbarSave: true,
                    toolbarEdit: false
                },
                toolbar: {
                    items: [{
                        type: 'button',
                        id: 'refreshMonitor',
                        caption: '刷新',
                        icon: 'fa fa-refresh'
                    }, {
                        type: 'button',
                        id: 'stopWorker',
                        caption: '停止Worker',
                        icon: 'fa fa-times'
                    } ],
                    onClick: function (target, data) {
                        switch (target) {
                            case 'refreshMonitor':
                                w2ui['dasNodeMonitor'].clear();

                                $.get("/rest/instance/worker", function (data) {
                                    $.each(data, function (index, value) {
                                        var ip = value.ip;
                                        $.each(value.ports.ports, function (port_index, port_value) {

                                            $.get(sprintf("/rest/monitor/performance/%s/%s", ip, port_value), function (data) {
                                                if (undefined != data &&
                                                    undefined != data.performanceHistory && data.performanceHistory.length > 0) {
                                                    var performace = data.performanceHistory[0];
                                                    var totalMemoryUse =
                                                        ((performace.sysTotalMemory - performace.sysFreeMemory) / performace.sysTotalMemory) * 100;
                                                    var jvmMemoryUse =
                                                        ((performace.totalMemory - performace.freeMemory) / performace.totalMemory) * 100;

                                                    w2ui['dasNodeMonitor'].add({
                                                        recid: w2ui['dasNodeMonitor'].total,
                                                        type: "worker",
                                                        ip: ip,
                                                        workPort: port_value,
                                                        machineStatus: sprintf("内存：%s（总%sMB）, CPU：%s",
                                                                totalMemoryUse.toFixed(0) + "%", (performace.sysTotalMemory / 1048576).toFixed(0), (performace.systemCpuUsage * 100).toFixed(0) + "%"),
                                                        jvmStatus: sprintf("内存：%s（总%sMB）, CPU：%s",
                                                                jvmMemoryUse.toFixed(0) + "%", (performace.totalMemory / 1048576).toFixed(0), (performace.processCpuUsage * 100).toFixed(0) + "%")
                                                    });
                                                }

                                            });

                                        });

                                    });
                                });
                                $.get("/rest/instance/controller", function (data) {
                                    $.each(data.ips, function (index, value) {
                                        w2ui['dasNodeMonitor'].add({
                                            recid: w2ui['dasNodeMonitor'].total,
                                            type: "controller",
                                            ip: value,
                                            workPort: null,
                                            machineStatus: null,
                                            jvmStatus: null
                                        });
                                    });
                                });

                                break;
                            case 'stopWorker':
                                var records = w2ui['dasNodeMonitor'].getSelection();
                                var record = w2ui['dasNodeMonitor'].get(records[0]);
                                var url = null;
                                if (record.type == 'worker') {
                                    url = sprintf('/rest/instance/worker/%s/%s', record.ip, record.workPort);
                                } else {
                                    url = sprintf('/rest/instance/controller/%s', record.ip);
                                }


                                $.ajax({
                                    type: 'DELETE',
                                    url: url,
                                    //dataType: 'json',
                                    success: function (data, status, event) {
                                        if (data.code == 'OK') {
                                            w2ui['dasNodeMonitor_toolbar'].click('refreshMonitor', null);
                                        }
                                    },
                                    error: function (data, status, event) {}
                                });
                                break;
                        }
                    }
                },
                searches: [{
                    field: 'type',
                    caption: '类型',
                    type: 'text'
                }, {
                    field: 'ip',
                    caption: 'IP地址',
                    type: 'text'
                } ],
                columns: [{
                    field: 'type',
                    caption: '类型',
                    size: '10%',
                    sortable: true,
                    attr: 'align=center'
                }, {
                    field: 'ip',
                    caption: 'IP地址',
                    size: '20%',
                    sortable: true,
                    attr: 'align=center'
                }, {
                    field: 'workPort',
                    caption: '工作端口',
                    size: '10%',
                    sortable: true
                }, {
                    field: 'machineStatus',
                    caption: '机器性能',
                    size: '30%',
                    sortable: true
                }, {
                    field: 'jvmStatus',
                    caption: 'JVM性能',
                    size: '30%',
                    sortable: true
                } ],
                records: []
            });
        }
        w2ui['main_layout'].content('main', dasNodeMonitor);
        w2ui['main_layout'].content('preview', "We will add monitor graph here!");
        var el = w2ui['dasNodeMonitor_toolbar'];
        if (el)
            el.click('refreshMonitor', null);
    };

    var Render = function () {

    };

    Render.prototype.renderAll = function() {
        $('#main_layout').height($(document).height() - 50);
        $('.slave_row').hide();
        this.render_layout();
        this.render_sidebar();
    };

    Render.prototype.render_layout = function() {
        $('#main_layout').w2layout({
            name: 'main_layout',
            panels: [{
                type: 'left',
                size: 256,
                resizable: true,
                style: 'border-right: 1px solid silver;'
            }, {
                type: 'main'
            }, {
                type: 'preview',
                size: '50%',
                resizable: true
            }]
        });
        $(window).resize(function () {
            $('#main_layout').height($(document).height() - 50);
        });
    };

    Render.prototype.render_sidebar = function() {
        w2ui['main_layout'].content('left', $().w2sidebar({
            name: 'sidebar',
            //icon: 'fa fa-wrench',
            nodes: [{
                id: 'all_confs',
                text: '配置',
                //icon: 'fa fa-wrench',
                group: true,
                expanded: true,
                nodes: [{
                    id: 'db_conf',
                    text: '数据库配置',
                    icon: 'fa fa-archive',
                    onClick: function (event) {
                        loadDbConfig();
                    }
                }, {
                    id: 'node_conf',
                    text: 'DAS节点配置',
                    icon: 'fa fa-bullseye',
                    onClick: function (event) {
                        loadDasNodeConfig();
                    }
                }]
            }, {
                id: 'all_monitors',
                text: '监控',
                // icon: 'fa fa-comment',
                group: true,
                expanded: true,
                nodes: [{
                    id: 'das_monitor',
                    text: 'DAS节点监控',
                    icon: 'fa fa-comment',
                    onClick: function (event) {
                        loadDasNodeMonitor();
                    }
                }, {
                    id: 'sql_monitor',
                    text: 'SQL执行监控',
                    icon: 'fa fa-comments'
                }]
            }]
        }));
    };


    window.render = new Render();


    $(function(){

        $(document.body).on('change', '#db_type', function (e) {
            if (this.value == "Slave") {
                $(".slave_row").show();
            } else {
                $('.slave_row').hide();
            }
            e.preventDefault();
        });

        $(document.body).on('click', '#save_db', function (e) {
            var postData = {
                "name": $("#physic_db").val(),
                "driver": $("#driver_class").val(),
                "jdbcUrl": $("#connect_str").val()
            };
            if ($("#dbModal").attr("is_update") == "1") {
                var url = sprintf("/rest/configure/db/%s", postData["name"]);
                if ($('.slave_row').css('display') == 'block') {
                    url = sprintf("/rest/configure/db/%s/slave/%s",
                        $("#physic_db").val(), $("#slave_name").val());
                    postData["name"] = $("#slave_name").val();
                }
                $.ajax({
                    type: 'PUT',
                    url: url,
                    //dataType: 'json',
                    data: postData,
                    success: function (data, status, event) {
                        if (data.code == 'OK') {
                            $('#dbModal').modal('hide');
                            var el = w2ui['dbConfig_toolbar'];
                            if (el)
                                el.click('refreshMaster', null);
                            w2ui['subDbConfig'].clear();
                        } else {
                            alert('Save Error!');
                        }
                    },
                    error: function (data, status, event) {}
                });
                $("#dbModal").attr("is_update", "0");
            } else {
                var url = "/rest/configure/db";
                if ($('.slave_row').css('display') == 'block') {
                    url = sprintf("/rest/configure/db/%s/slave", $("#physic_db").val());
                    postData["name"] = $("#slave_name").val();
                }
                $.post(url, postData, function (data, status, event) {
                    if (data.code == 'OK') {
                        $('#dbModal').modal('hide');
                        var el = w2ui['dbConfig_toolbar'];
                        if (el)
                            el.click('refreshMaster', null);
                        w2ui['subDbConfig'].clear();
                    } else {
                        alert('Save Error!');
                    }
                });
            }
        });

        $(document.body).on('click', '#save_das', function (e) {
            var postData = {
                "name": $("#ip_addr").val(),
                "directory": $("#work_dir").val(),
                "maxHeapSize": $("#max_heap").val(),
                "startingHeapSize": $("#default_heap").val()
            };
            if ($("#dasModal").attr('is_update') == "1") {
                $.ajax({
                    type: 'PUT',
                    url: sprintf("/rest/configure/node/%s", postData["name"]),
                    //dataType: 'json',
                    data: postData,
                    success: function (data, status, event) {
                        if (data.code == 'OK') {
                            $("#dasModal").modal('hide');
                            w2ui['dasNodeConfig_toolbar'].click('refreshNode', null);
                        }
                    },
                    error: function (data, status, event) {}
                });
            } else {
                $.post("/rest/configure/node",
                    postData,
                    function (data, status, event) {
                        if (data.code == 'OK') {
                            $("#dasModal").modal('hide');
                            w2ui['dasNodeConfig_toolbar'].click('refreshNode', null);
                        }
                    });
            }
        });

        $(document.body).on('click', '#save_port', function (e) {
            $.post("/rest/configure/port", {
                "number": $("#port").val()
            }, function (data, status, event) {
                if (data.code == 'OK') {
                    $("#portModal").modal('hide');
                    w2ui['portConfig_toolbar'].click('refreshPort', null);
                }
            });
        });
    });

})(window);