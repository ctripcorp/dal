(function ($, window, document, undefined) {
    var Render = function () {
    };

    var catalogChangeCount_up = 0;

    function refreshAllDB() {
        w2ui['grid'].clear();
        cblock($("body"));
        $.get("/rest/groupdb/allgroupdbs", {rand: Math.random()}, function (data) {
            var allGroupDbs = [];
            $.each(data, function (index, value) {
                value.recid = allGroupDbs.length + 1;
                allGroupDbs.push(value);
            });
            w2ui['grid'].add(allGroupDbs);
            $("body").unblock();
        }).fail(function (data) {
            $("body").unblock();
            alert("获取所有Group失败!");
        });
    };

    //添加DB按钮，填入type之后检索所有db名称（添加dbmodetype参数）
    function addDB() {
        $.get("/rest/project/userGroups", {root: true, rand: Math.random()}).done(function (data) {
            if (data.length > 0 && data[0]['id'] > 0) {
                $("#error_msg").html('');
                $("#add_new_db_step1").show();
                $("#add_new_db_step2").hide();
                $("#conn_test").show();
                $("#add_new_db_next").show();
                $("#add_new_db_prev").hide();
                $("#add_new_db_save").hide();
                var dbcatalog = $("#dbcatalog");
                if (dbcatalog[0] != undefined && dbcatalog[0].selectize != undefined) {
                    dbcatalog[0].selectize.clearOptions();
                } else {
                    dbcatalog.selectize({
                        valueField: 'id',
                        labelField: 'title',
                        searchField: 'title',
                        sortField: 'title',
                        options: [],
                        create: true
                    });
                }
                var dbnamebase = $("#dbnamebase");
                if (dbnamebase[0] != undefined && dbnamebase[0].selectize != undefined) {
                    dbnamebase[0].selectize.clearOptions();
                } else {
                    dbnamebase.selectize({
                        valueField: 'id',
                        labelField: 'title',
                        searchField: 'title',
                        sortField: 'title',
                        options: [],
                        create: true
                    });
                }
                var dalgroup = $("#dalgroup");
                if (dalgroup[0] != undefined && dalgroup[0].selectize != undefined) {
                    dalgroup[0].selectize.clearOptions();
                }
                else {
                    dalgroup.selectize({
                        valueField: 'id',
                        labelField: 'title',
                        searchField: 'title',
                        sortField: 'title',
                        options: [],
                        create: false
                    });
                }
                $("#addDbModal").modal({"backdrop": "static"});
                var dbcatalog = $("#dbcatalog");
                var db_providerName = $.trim($("#dbtype").val());
                var db_mode_type = $.trim($("#dbmodetype").val());
                if (db_providerName !== null || db_providerName.length > 0) {
                    $.post("/rest/db/getAllDB", {
                        dbType: db_providerName,
                        dbModeType: db_mode_type
                    }, function (data) {
                        var allCatalog_up = [];
                        $.each($.parseJSON(data.info), function (index, value) {
                            allCatalog_up.push({
                                id: value, title: value
                            });
                        });
                        dbcatalog[0].selectize.clearOptions();
                        dbcatalog[0].selectize.addOption(allCatalog_up);
                        dbcatalog[0].selectize.refreshOptions(false);
                    });
                }
            } else {
                alert("请先加入一个DAL Team.");
            }
            $("body").unblock();
        }).fail(function (data) {
            alert('获取用户加入的所有DAL Team失败.');
            $("body").unblock();
        });
    };

    var getAllInOneKeyByDbName_up = function (allInOneName) {
        var dbcatalog = $("#dbcatalog_up").val();
        var allinonename = $("#allinonename_up");
        if (allinonename[0] != undefined && allinonename[0].selectize != undefined) {
            allinonename[0].selectize.clearOptions();
        } else {
            allinonename.selectize({
                valueField: 'id',
                labelField: 'title',
                searchField: 'title',
                sortField: 'title',
                options: [],
                create: true
            });
        }
        $.post("/rest/db/getTitanKeyByDBName",{
            dbName: dbcatalog
        }, function (data) {
            var allInOneNames = [];
            if(data.info!="null"){
                $.each($.parseJSON(data.info), function (index, value) {
                    allInOneNames.push({
                        id: value, title: value
                    });
                });
            }
            allinonename[0].selectize.clearOptions();
            allinonename[0].selectize.addOption(allInOneNames);
            allinonename[0].selectize.refreshOptions(false);

            if (allInOneName!=""){
                allinonename[0].selectize.addOption({
                    id : allInOneName,
                    title : allInOneName,
                });
                allinonename[0].selectize.setValue(allInOneName);
            }
        });
    };
    var  record=null;
    function editDB() {
        $("#update_error_msg").html('');
        $("#update_db_step1").show();
        $("#update_db_step2").hide();
        $("#update_conn_test").show();
        $("#update_db_next").show();
        $("#update_db_prev").hide();
        $("#update_db_save").hide();
        var records = w2ui['grid'].getSelection();
        record= w2ui['grid'].get(records[0]);
        if (record == null) {
            alert('请先选择一个 database');
            return;
        }
        cblock($("body"));
        $.post("/rest/db/getOneDB", {allinonename: record['dbname']}, function (data) {
            if (data.code == "OK") {
                var db = $.parseJSON(data.info);
                $("#dbtype_up").val(db['db_providerName']);
                $("#dbaddress_up").val(db['db_address']);
                $("#dbport_up").val(db['db_port']);
                $("#dbuser_up").val(db['db_user']);
                $("#dbpassword_up").val(db['db_password']);
                // $("#allinonename_up").val(db['dbname']);
                if ($("#dbcatalog_up")[0] != undefined && $("#dbcatalog_up")[0].selectize != undefined) {
                    $("#dbcatalog_up")[0].selectize.clearOptions();
                } else {
                    $("#dbcatalog_up").selectize({
                        valueField: 'id',
                        labelField: 'title',
                        searchField: 'title',
                        sortField: 'title',
                        options: [],
                        create: true
                    });
                }
                $("#updateDbModal").modal({"backdrop": "static"});
                var dbcatalog_up = $("#dbcatalog_up");
                $.post("/rest/db/getAllDB", {
                    dbType: db['db_providerName'],
                    dbModeType: db['mode_type']
                }, function (data) {
                    var allCatalog_up = [];
                    $.each($.parseJSON(data.info), function (index, value) {
                        allCatalog_up.push({
                            id: value, title: value
                        });
                    });
                    dbcatalog_up[0].selectize.clearOptions();
                    dbcatalog_up[0].selectize.addOption(allCatalog_up);
                    dbcatalog_up[0].selectize.refreshOptions(false);
                    $("#dbcatalog_up")[0].selectize.addOption({
                        id : db['db_catalog'],
                        title : db['db_catalog']
                    });
                    catalogChangeCount_up = 0;
                    $("#dbcatalog_up")[0].selectize.setValue(db['db_catalog']);
                    getAllInOneKeyByDbName_up(db['dbname']);
                });
            } else {
                $("#errorMess").html(data.info);
                $("#errorNoticeDiv").modal({"backdrop": "static"});
            }
            $("body").unblock();
        }).fail(function (data) {
            alert("执行异常");
            $("body").unblock();
        });
    };

    function delDB() {
        var records = w2ui['grid'].getSelection();
        var record = w2ui['grid'].get(records[0]);
        if (record != null) {
            if (confirm("您确定要删除吗?")) {
                $.post("/rest/db/deleteAllInOneDB", {allinonename: record['dbname']}, function (data) {
                    if (data.code == "OK") {
                        refreshAllDB();
                    } else {
                        $("#errorMess").html(data.info);
                        $("#errorNoticeDiv").modal({"backdrop": "static"});
                    }
                }).fail(function (data) {
                    alert("执行异常");
                    $("body").unblock();
                });
            }
        } else {
            alert('请选择一个database！');
        }
    };

    function isDefaultUser() {
        cblock($("body"));
        $.get("/rest/user/isDefaultUser", {rand: Math.random()}, function (data) {
            if (data == "true") {
                $("#validateKeyname").hide();
            }
            $("body").unblock();
        });
    }

    Render.prototype = {
        render_layout: function (render_obj) {
            $(render_obj).w2layout({
                name: 'main_layout',
                panels: [{type: 'main'}]
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
                toolbar: {
                    items: [{
                        type: 'break'
                    }, {
                        type: 'button',
                        id: 'refreshAllDB',
                        caption: '刷新',
                        icon: 'glyphicon glyphicon-refresh'
                    }, {
                        type: 'button',
                        id: 'addDB',
                        caption: '添加DB',
                        icon: 'glyphicon glyphicon-plus'
                    }, {
                        type: 'button',
                        id: 'editDB',
                        caption: '修改DB',
                        icon: 'glyphicon glyphicon-edit'
                    }, {
                        type: 'button',
                        id: 'delDB',
                        caption: '删除DB',
                        icon: 'glyphicon glyphicon-remove'
                    }],
                    onClick: function (target, data) {
                        switch (target) {
                            case 'refreshAllDB':
                                refreshAllDB();
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
                    caption: '所属 DAL Team',
                    type: 'text'
                }, {
                    field: 'db_address',
                    caption: 'DB Address',
                    type: 'text'
                }, {
                    field: 'db_catalog',
                    caption: 'DB Catalog',
                    type: 'text'
                }, {
                    field: 'mode_type',
                    caption: 'DB Mode Type',
                    type: 'text'
                }, {
                    field: 'db_providerName',
                    caption: '数据库类型',
                    type: 'text'
                }],
                columns: [{
                    field: 'dbname',
                    caption: 'DB All-In-One Name',
                    size: '20%',
                    attr: 'align=center',
                    sortable: true,
                    resizable: true
                }, {
                    field: 'comment',
                    caption: '所属 DAL Team',
                    size: '10%',
                    attr: 'align=center',
                    sortable: true,
                    resizable: true
                }, {
                    field: 'mode_type',
                    caption: 'DB Mode Type',
                    size: '8%',
                    attr: 'align=center',
                    sortable: true,
                    resizable: true
                },{
                    field: 'db_address',
                    caption: 'DB Address',
                    size: '15%',
                    attr: 'align=center',
                    sortable: true,
                    resizable: true
                }, {
                    field: 'db_port',
                    caption: 'DB Port',
                    size: '5%',
                    attr: 'align=center',
                    sortable: true,
                    resizable: true
                }, {
                    field: 'db_user',
                    caption: 'DB User',
                    size: '10%',
                    attr: 'align=center',
                    sortable: true,
                    resizable: true
                }, {
                    field: 'db_password',
                    caption: 'DB Password',
                    size: '10%',
                    attr: 'align=center',
                    sortable: true,
                    resizable: true
                }, {
                    field: 'db_catalog',
                    caption: 'DB Catalog',
                    size: '15%',
                    attr: 'align=center',
                    sortable: true,
                    resizable: true
                }, {
                    field: 'db_providerName',
                    caption: '数据库类型',
                    size: '7%',
                    attr: 'align=center',
                    sortable: true,
                    resizable: true
                }],
                records: []
            }));

            refreshAllDB();
        }
    };

    window.render = new Render();
    $('#main_layout').height($(document).height() - 50);
    window.render.render_layout($('#main_layout'));
    window.render.render_grid();

    $(window).resize(function () {
        $('#main_layout').height($(document).height() - 50);
    });

    $(function () {
        //仅仅在更改数据库类型（mysql/sqlsever）的时候调用
        var setDefaultDbVal = function () {
            var dbcatalog = $("#dbcatalog");
            $("#error_msg").html(" ");
            var dbType = $.trim($("#dbtype").val());
            var dbnamebase = $("#dbnamebase");

            if (dbType == "MySQL") {
                $.get("/rest/db/getAllNamebases", function (data) {
                    var namebases = [];
                    $.each($.parseJSON(data.info), function (index, value) {
                        namebases.push({
                            id: value, title: value
                        });
                    });
                    dbnamebase[0].selectize.clearOptions();
                    dbnamebase[0].selectize.addOption(namebases);
                    dbnamebase[0].selectize.refreshOptions(false);
                });
                $("#dbaddress").val("");
                $("#dbport").val("");
                $("#dbuser").val("");
                $("#dbpassword").val("");
                $("#dbmodetype").val("dalcluster");
                $("#cluster-option").show();
                $("#dbnamebase-control").show();
                $("#dbmodetype-control").show();
                $("#dbcatalog-control").hide();
            } else if (dbType == "SQLServer") {
                $("#dbnamebase-control").hide();
                $("#dbmodetype-control").hide();
                $("#dbcatalog-control").show();
                $.post("/rest/db/getAllDB", {
                    dbType: dbType
                }, function (data) {
                    var allCatalog = [];
                    $.each($.parseJSON(data.info), function (index, value) {
                        allCatalog.push({
                            id: value, title: value
                        });
                    });
                    dbcatalog[0].selectize.clearOptions();
                    dbcatalog[0].selectize.addOption(allCatalog);
                    dbcatalog[0].selectize.refreshOptions(false);
                });
                var dbName = $("#dbcatalog").val();
                $.post("/rest/user/getDefaultDBInfo", {
                    dbType: dbType,
                    dbName: dbName
                }, function (data) {
                    $("#dbaddress").val(data.db_address);
                    $("#dbport").val(data.db_port);
                    $("#dbuser").val(data.db_user);
                    $("#dbpassword").val(data.db_password);
                });
            } else {
                $("#dbmodetype-control").show();
                $("#dbnamebase-control").show();
                $("#dbaddress").val("");
                $("#dbport").val("");
                $("#dbuser").val("");
                $("#dbpassword").val("");
                $("#dbmodetype").val("dalcluster");
                $("#cluster-option").show();
                dbnamebase[0].selectize.clearOptions();
                dbcatalog[0].selectize.clearOptions();
            }

        };

        var getAllCatalog = function (successInfo) {
            var dbType = $("#dbtype").val();
            var dbAddress = $("#dbaddress").val();
            var dbPort = $("#dbport").val();
            var dbUser = $("#dbuser").val();
            var dbPassword = $("#dbpassword").val();
            var dbCatalog = '';

            var error_msg = $("#error_msg");
            error_msg.html("正在连接数据库，请稍等...");
            var result = true;

            var dbmodetype = $("#dbmodetype").val();
            var dbnamebase = $("#dbnamebase").val();

            if (dbmodetype == "dalcluster") {
                dbCatalog = dbnamebase;
            } else {
                dbCatalog = $("#dbcatalog").val();
            }

            $.ajax({
                type: "POST",
                url: "/rest/db/connectionTest",
                data: {
                    dbtype: dbType,
                    dbaddress: dbAddress,
                    dbport: dbPort,
                    dbuser: dbUser,
                    dbpassword: dbPassword,
                    dbName: dbCatalog
                },
                async: false,
                success: function (data) {
                    if (data.code == "OK") {
                        /*var allCatalog = [];
                        $.each($.parseJSON(data.info), function (index, value) {
                            allCatalog.push({
                                id: value, title: value
                            });
                        });
                        dbcatalog[0].selectize.clearOptions();
                        dbcatalog[0].selectize.addOption(allCatalog);
                        dbcatalog[0].selectize.refreshOptions(false);*/
                        error_msg.html(successInfo);
                    } else {
                        error_msg.html("连接失败:" + data.info);
                        result = false;
                    }
                }
            });

            return result;
        };

        //根据 dbname和dbmode获取连接串
        var getAllInOneKeyByDbName = function () {
            var dbcatalog = $("#dbcatalog").val();
            var dbmodetype = $("#dbmodetype").val();
            var allinonename = $("#allinonename");
            if (allinonename[0] != undefined && allinonename[0].selectize != undefined) {
                allinonename[0].selectize.clearOptions();
            } else {
                allinonename.selectize({
                    valueField: 'id',
                    labelField: 'title',
                    searchField: 'title',
                    sortField: 'title',
                    options: [],
                    create: true
                });
            }
            if (dbmodetype == "dalcluster") {
                var dbnamebase = $("#dbnamebase").val().concat("_dalcluster");
                $("#connectionString").val(dbnamebase);
            } else {
                $.post("/rest/db/getTitanKeyByDBName",{
                    dbName: dbcatalog
                }, function (data) {
                    var allInOneNames = [];
                    $.each($.parseJSON(data.info), function (index, value) {
                        allInOneNames.push({
                            id: value, title: value
                        });
                    });
                    allinonename[0].selectize.clearOptions();
                    allinonename[0].selectize.addOption(allInOneNames);
                    allinonename[0].selectize.refreshOptions(false);
                });
            }
        };

        var getUserGroups = function () {
            var dalgroup = $("#dalgroup");
            $.get("/rest/member", function (data) {
                if (data != undefined && data != null) {
                    if (data.length > 1) {
                        var groups = [];
                        $.each(data, function (index, value) {
                            groups.push({
                                id: value.id, title: value.group_name
                            });
                        });
                        dalgroup[0].selectize.clearOptions();
                        dalgroup[0].selectize.addOption(groups);
                        dalgroup[0].selectize.refreshOptions(false);
                    }
                    else {
                        $("#dalgroupspan").hide();
                    }
                }
            });
        };

        $(document.body).on("change", "#dbtype", function () {
            $.get("/rest/user/isDefaultUser", {rand: Math.random()}, function (data) {
                if (data == "false") {
                    setDefaultDbVal();
                }
            });
        });

        //选择dbnamebase修改，dbmodetype及以下联动
        $(document.body).on("change", "#dbnamebase", function () {
            var dbnamebase = $.trim($("#dbnamebase").val());
            var dbmodetype = $.trim($("#dbmodetype").val());
            var dbcatalog = $("#dbcatalog");
            var tempData = "";
            $("#dbcatalog").val("");
            $.ajaxSettings.async = false;
            $.post("/rest/db/hasDalCluster", {
                namebase: dbnamebase
            }, function (data) {
                tempData = data;
            });
            if (tempData.info == "false") {
                if (dbmodetype != "titankey") {
                    $("#dbmodetype").val("titankey");
                }
                $("#dbcatalog-control").show();
                $("#cluster-option").hide();
                $.post("/rest/db/getShardsByNameBase", {
                    namebase: dbnamebase
                }, function (data) {
                    var dbcatalogs = [];
                    $.each($.parseJSON(data.info), function (index, value) {
                        dbcatalogs.push({
                            id: value, title: value
                        });
                    });
                    dbcatalog[0].selectize.clearOptions();
                    dbcatalog[0].selectize.addOption(dbcatalogs);
                    dbcatalog[0].selectize.refreshOptions(false);
                });
            } else {
                if (dbmodetype != "dalcluster") {
                    $("#dbmodetype").val("dalcluster");
                }
                $.post("/rest/user/getDefaultDBInfo", {
                    dbType: "MySQL",
                    dbName: dbnamebase
                }, function (data) {
                    $("#dbaddress").val(data.db_address);
                    $("#dbport").val(data.db_port);
                    $("#dbuser").val(data.db_user);
                    $("#dbpassword").val(data.db_password);
                });
            }
            $.ajaxSettings.async = true;
        });

        //选择dbmodetype改变，dbcatalog及以下联动
        $(document.body).on("change", "#dbmodetype", function () {
            var dbmodetype = $.trim($("#dbmodetype").val());
            var dbnamebase = $.trim($("#dbnamebase").val());
            var dbcatalog = $("#dbcatalog");
            if (dbmodetype == "dalcluster") {
                $("#dbcatalog-control").hide();
            } else {
                $("#dbcatalog-control").show();
                $.post("/rest/db/getShardsByNameBase", {
                    namebase: dbnamebase
                }, function (data) {
                    var dbcatalogs = [];
                    $.each($.parseJSON(data.info), function (index, value) {
                        dbcatalogs.push({
                            id: value, title: value
                        })
                    });
                    dbcatalog[0].selectize.clearOptions();
                    dbcatalog[0].selectize.addOption(dbcatalogs);
                    dbcatalog[0].selectize.refreshOptions(false);
                });
            }
        });

        $(document.body).on("change", "#dbcatalog", function () {
            var dbcatalog = $("#dbcatalog").val();
            var dbType = $.trim($("#dbtype").val());
            if (dbType === "MySQL") {
                $.post("/rest/user/getDefaultDBInfo", {
                    dbType: dbType,
                    dbName: dbcatalog
                }, function (data) {
                    $("#dbaddress").val(data.db_address);
                    $("#dbport").val(data.db_port);
                    $("#dbuser").val(data.db_user);
                    $("#dbpassword").val(data.db_password);
                });
            }
        });

        // 添加数据库--->点击 "下一步" 校验
        $(document.body).on("click", "#add_new_db_next", function () {
            var dbType = $("#dbtype").val();
            var dbModeType = $("#dbmodetype").val();
            var dbnamebase = $("#dbnamebase").val();
            var dbAddress = $("#dbaddress").val();
            var dbPort = $("#dbport").val();
            var dbUser = $("#dbuser").val();
            var dbPassword = $("#dbpassword").val();
            var error_msg = $("#error_msg");

            if (dbType == "no") {
                error_msg.html("请选择数据库类型");
                return;
            }else if (dbType == "MySQL") {
                if (dbModeType == null || dbModeType.length == 0){
                    error_msg.html("请选择数据库连接方式");
                    return;
                }
                if (dbnamebase == null || dbnamebase.length == 0) {
                    error_msg.html("请选择DB Name Base");
                    return;
                }
            }
            if (dbAddress == null || dbAddress.length == 0) {
                error_msg.html("请选择数据库");
                return;
            }
            if (dbPort == null || dbPort.length == 0) {
                error_msg.html("请输入数据库端口");
                return;
            }
            if (dbUser == null || dbUser.length == 0) {
                error_msg.html("请输入数据库登陆用户");
                return;
            }
            if (dbPassword == null || dbPassword.length == 0) {
                error_msg.html("请输入数据库登陆用户密码");
                return;
            }

            var result = getAllCatalog("");
            if (!result) {
                return;
            }

            $("#add_new_db_step1").hide();
            $("#add_new_db_step2").show();
            $("#conn_test").hide();
            $("#add_new_db_next").hide();
            $("#add_new_db_prev").show();
            $("#add_new_db_save").show();
            if (dbModeType == "dalcluster") {
                $("#allinonename-control").hide();
                $("#connectionString-control").show();
            }else {
                $("#allinonename-control").show();
                $("#connectionString-control").hide();
            }
            getUserGroups();

            getAllInOneKeyByDbName();
        });
        // 添加数据库--->点击 "上一步" 回到上个页面
        $(document.body).on("click", "#add_new_db_prev", function () {
            $("#add_new_db_step1").show();
            $("#add_new_db_step2").hide();
            $("#conn_test").show();
            $("#add_new_db_next").show();
            $("#add_new_db_prev").hide();
            $("#add_new_db_save").hide();
            $("#error_msg").html("");
        });

        $(document.body).on("click", "#conn_test", function () {
            getAllCatalog("连接成功");
        });

        $(document.body).on("click", "#add_new_db_save", function () {
            var dbType = $("#dbtype").val();
            var all_In_One_Name = $("#allinonename").val();
            var dbAddress = $("#dbaddress").val();
            var dbPort = $("#dbport").val();
            var dbUser = $("#dbuser").val();
            var dbPassword = $("#dbpassword").val();
            var dbCatalog = $("#dbcatalog").val();
            var dalGroup = $("#dalgroup").val();
            var error_msg = $("#error_msg");
            var dbmodetype = $("#dbmodetype").val();
            var dbnamebase = $("#dbnamebase").val();

            if (dbnamebase == null || dbnamebase.length == 0) {
                error_msg.html("请选择DB Name Base");
                return;
            }

            if (dbmodetype == null || dbmodetype.length == 0) {
                error_msg.html("请选择DB Mode");
                return;
            } else if (dbmodetype == "dalcluster") {
                dbCatalog = dbnamebase;
                all_In_One_Name = $("#connectionString").val();
            }

            if (dbType == "no") {
                error_msg.html("请选择数据库类型");
                return;
            }
            if (all_In_One_Name == "" || null == all_In_One_Name) {
                error_msg.html("请输入All-In-One Name");
                return;
            }

            var result = validateKeyName(all_In_One_Name, dbCatalog, error_msg, dbmodetype);
            if (!result) {
                return;
            }

            if (dbAddress == null || dbAddress.length == 0) {
                error_msg.html("请选择数据库");
                return;
            }
            if (dbPort == null || dbPort.length == 0) {
                error_msg.html("请输入数据库端口");
                return;
            }
            if (dbUser == null || dbUser.length == 0) {
                error_msg.html("请输入数据库登陆用户");
                return;
            }
            if (dbPassword == null || dbPassword.length == 0) {
                error_msg.html("请输入数据库登陆用户密码");
                return;
            }
            if (dbCatalog == null || dbCatalog.length == 0) {
                error_msg.html("请输入数据库");
                return;
            }

            $.post("/rest/db/addNewAllInOneDB", {
                dbtype: dbType,
                allinonename: all_In_One_Name,
                dbaddress: dbAddress,
                dbport: dbPort,
                dbuser: dbUser,
                dbpassword: dbPassword,
                dbcatalog: dbCatalog,
                addtogroup: $("#add_to_group").is(":checked"),
                dalgroup: dalGroup == undefined ? "" : dalGroup,
                gen_default_dbset: $("#gen_default_dbset").is(":checked")
            }, function (data) {
                if (data.code == "OK") {
                    error_msg.html("保存成功");
                    refreshAllDB();
                } else {
                    error_msg.html(data.info);
                }
            });
        });

        $(document.body).on("click", "#add_to_group", function () {
            var flag = $(this).is(":checked");
            var genDefault = $("#gen_default_dbset");
            genDefault.prop({"checked": flag, "disabled": !flag});
        });

        $(document.body).on("change", "#dbtype_up", function () {
            $("#error_msg").html(" ");
            var dbType = $.trim($("#dbtype_up").val());
            var dbCatalog = $("#dbcatalog_up").val();

                $.post("/rest/user/getDefaultDBInfo", {
                    dbType: dbType,
                    dbName: dbCatalog
                }, function (data) {
                    $("#dbaddress_up").val(data.db_address);
                    $("#dbport_up").val(data.db_port);
                    $("#dbuser_up").val(data.db_user);
                    $("#dbpassword_up").val(data.db_password);
                });

        });

        $(document.body).on("change", "#dbcatalog_up", function () {
            var dbcatalog = $("#dbcatalog_up").val();
            var dbType = $.trim($("#dbtype_up").val());
            var dbaddress = $("#dbaddress_up").val();
            catalogChangeCount_up = catalogChangeCount_up + 1;
            if (dbType === "MySQL") {
                $.post("/rest/user/getDefaultDBInfo", {
                    dbType: dbType,
                    dbName: dbcatalog
                }, function (data) {
                    if (catalogChangeCount_up > 1 && data.db_address !== "") {
                        $("#dbaddress_up").val(data.db_address);
                        $("#dbport_up").val(data.db_port);
                        $("#dbuser_up").val(data.db_user);
                        $("#dbpassword_up").val(data.db_password);
                    }
                });

            }
            getAllInOneKeyByDbName_up(record['dbname']);

        });

        var getUpdateCatalog = function (successInfo) {
            $("#update_error_msg").html("正在连接数据库，请稍等...");
            var dbType = $("#dbtype_up").val();
            var dbAddress = $("#dbaddress_up").val();
            var dbPort = $("#dbport_up").val();
            var dbUser = $("#dbuser_up").val();
            var dbPassword = $("#dbpassword_up").val();
            var dbName=$("#dbcatalog_up").val();
            var result = true;
            var dbmodetype = $("#dbmodetype").val();
            var dbnamebase = $("#dbnambase").val();

            if (dbmodetype == "dalcluster") {
                dbName = dbnamebase;
            }

            $.ajax({
                type: "POST",
                url: "/rest/db/connectionTest",
                data: {
                    dbtype: dbType,
                    dbaddress: dbAddress,
                    dbport: dbPort,
                    dbuser: dbUser,
                    dbpassword: dbPassword,
                    dbName:dbName
                },
                async: false,
                success: function (data) {
                    if (data.code == "OK") {
                        /*var allCatalog = [];
                        $.each($.parseJSON(data.info), function (index, value) {
                            allCatalog.push({
                                id: value, title: value
                            });
                        });
                        $("#dbcatalog_up")[0].selectize.clearOptions();
                        $("#dbcatalog_up")[0].selectize.addOption(allCatalog);
                        $("#dbcatalog_up")[0].selectize.refreshOptions(false);*/
                        $("#update_error_msg").html(successInfo);
                        /*var records = w2ui['grid'].getSelection();
                        var record = w2ui['grid'].get(records[0]);
                        $.post("/rest/db/getOneDB", {
                                allinonename: record['dbname']
                            },
                            function (data) {
                                if (data.code == "OK") {
                                    var db = $.parseJSON(data.info);
                                    $("#dbcatalog_up")[0].selectize.setValue(db['db_catalog']);
                                }
                            });*/
                    } else {
                        $("#update_error_msg").html("连接错误:" + data.info);
                        result = false;
                    }
                }
            });

            return result;
        };

        var validateKeyName = function (obj, dbCatalog, msg, dbModeType) {
            var result = true;
            var key = obj;
            if (key.length == 0)
                return false;

            $.ajax({
                type: "GET",
                dataType: "json",
                url: "/rest/db/validation",
                data: {"key": key, "dbName": dbCatalog, "dbmodetype": dbModeType},
                async: false,
                success: function (data) {
                    if (data.info.length > 0) {
                        $(msg).html(data.info);
                        result = false;
                    }
                }
            });
            return result;
        };

        $(document.body).on("click", "#update_conn_test", function () {
            getUpdateCatalog("连接成功");
        });

        $(document.body).on("click", "#update_db_next", function () {
            var dbType = $("#dbtype_up").val();
            var dbAddress = $("#dbaddress_up").val();
            var dbPort = $("#dbport_up").val();
            var dbUser = $("#dbuser_up").val();
            var dbPassword = $("#dbpassword_up").val();
            var dbCatalog = $("#dbcatalog").val();
            var update_error_msg = $("#update_error_msg");

            if (dbType == "no") {
                update_error_msg.html("请选择数据库类型");
                return;
            }
            if (dbAddress == null || dbAddress.length == 0) {
                update_error_msg.html("请选择数据库");
                return;
            }
            if (dbPort == null || dbPort.length == 0) {
                update_error_msg.html("请输入数据库端口");
                return;
            }
            if (dbUser == null || dbUser.length == 0) {
                update_error_msg.html("请输入数据库登陆用户");
                return;
            }
            if (dbPassword == null || dbPassword.length == 0) {
                update_error_msg.html("请输入数据库登陆用户密码");
                return;
            }

            var result = getUpdateCatalog("");
            if (!result) {
                return;
            }
            $("#update_db_step1").hide();
            $("#update_db_step2").show();
            $("#update_conn_test").hide();
            $("#update_db_next").hide();
            $("#update_db_prev").show();
            $("#update_db_save").show();

        });

        $(document.body).on("click", "#update_db_prev", function () {
            $("#update_db_step1").show();
            $("#update_db_step2").hide();
            $("#update_conn_test").show();
            $("#update_db_next").show();
            $("#update_db_prev").hide();
            $("#update_db_save").hide();
            $("#update_error_msg").html("");
        });

        $(document.body).on("click", "#update_db_save", function () {
            var dbType = $("#dbtype_up").val();
            var all_In_One_Name = $("#allinonename_up").val();
            var dbAddress = $("#dbaddress_up").val();
            var dbPort = $("#dbport_up").val();
            var dbUser = $("#dbuser_up").val();
            var dbPassword = $("#dbpassword_up").val();
            var dbCatalog = $("#dbcatalog_up").val();
            var update_error_msg = $("#update_error_msg");

            if (dbType == "no") {
                update_error_msg.html("请选择数据库类型");
                return;
            }
            if (all_In_One_Name == null || all_In_One_Name.length == 0) {
                update_error_msg.html("请输入All-In-One Name");
                return;
            }

            var result = validateKeyName($("#allinonename_up").val(), dbCatalog, update_error_msg, $("#dbmodetype").val());
            if (!result) {
                return;
            }

            if (dbAddress == null || dbAddress.length == 0) {
                update_error_msg.html("请选择数据库");
                return;
            }
            if (dbPort == null || dbPort.length == 0) {
                update_error_msg.html("请输入数据库端口");
                return;
            }
            if (dbUser == null || dbUser.length == 0) {
                update_error_msg.html("请输入数据库登陆用户");
                return;
            }
            if (dbPassword == null || dbPassword.length == 0) {
                update_error_msg.html("请输入数据库登陆用户密码");
                return;
            }
            if (dbCatalog == null || dbCatalog.length == 0) {
                update_error_msg.html("请输入数据库");
                return;
            }

            var records = w2ui['grid'].getSelection();
            var record = w2ui['grid'].get(records[0]);
            $.post("/rest/db/updateDB", {
                "id": record['id'],
                "dbtype": dbType,
                "allinonename": all_In_One_Name,
                "dbaddress": dbAddress,
                "dbport": dbPort,
                "dbuser": dbUser,
                "dbpassword": dbPassword,
                "dbcatalog": dbCatalog
            }, function (data) {
                if (data.code == "OK") {
                    update_error_msg.html("更新成功.");
                    refreshAllDB();
                } else {
                    update_error_msg.html(data.info);
                }
            });
        });

        $(document.body).on("click", "#validateKeyname", function () {
            var dbmodetype = $("#dbmodetype").val();
            var allinonename = $("#allinonename").val();
            if (dbmodetype == "dalcluster") {
                allinonename = $("#connectionString").val();
            }
            validateKeyName(allinonename, $("#dbcatalog").val(), $("#error_msg"), dbmodetype);
        });

        isDefaultUser();
    });
})(jQuery, window, document);