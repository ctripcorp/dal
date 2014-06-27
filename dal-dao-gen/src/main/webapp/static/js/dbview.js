
(function (window, undefined) {

    var Render = function () {

    };

    var refreshAllDB = function () {
        w2ui['grid'].clear();
        cblock($("body"));
        $.get("/rest/groupdb/allgroupdbs?rand=" + Math.random(),function (data) {
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

    var addDB = function(){
        $("#error_msg").html('');
        $("#add_new_db_step1").show();
        $("#add_new_db_step2").hide();
        $("#conn_test").show();
        $("#add_new_db_next").show();
        $("#add_new_db_prev").hide();
        $("#add_new_db_save").hide();
        if ($("#dbcatalog")[0] != undefined && $("#dbcatalog")[0].selectize != undefined) {
            $("#dbcatalog")[0].selectize.clearOptions();
        } else {
            $("#dbcatalog").selectize({
                valueField: 'id',
                labelField: 'title',
                searchField: 'title',
                sortField: 'title',
                options: [],
                create: false
            });
        }
        $("#addDbModal").modal({
            "backdrop": "static"
        });
    };

    var editDB = function(){

    };

    var delDB = function(){
        var records = w2ui['grid'].getSelection();
        var record = w2ui['grid'].get(records[0]);
        if(record!=null){
            if (confirm("Are you sure to delete?")) {
                $.post("/rest/db/deleteAllInOneDB", {
                    allinonename : record['dbname']
                },function (data) {
                    if (data.code == "OK") {
                        refreshAllDB();
                    } else {
                        $("#errorMess").html(data.info);
                        $("#errorNoticeDiv").modal({
                            "backdrop": "static"
                        });
                    }
                }).fail(function (data) {
                        alert("执行异常");
                    });
            }
        }else{
            alert('请选择一个database！');
        }
    };

    Render.prototype = {
        render_layout: function (render_obj) {
            $(render_obj).w2layout({
                name: 'main_layout',
                panels: [{
                    type: 'main'
                }]
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
                        icon: 'fa fa-refresh'
                    }, {
                        type: 'button',
                        id: 'addDB',
                        caption: '添加DB',
                        icon: 'fa fa-plus'
                    }, {
                        type: 'button',
                        id: 'editDB',
                        caption: '修改DB',
                        icon: 'fa fa-edit'
                    }, {
                        type: 'button',
                        id: 'delDB',
                        caption: '删除DB',
                        icon: 'fa fa-times'
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
                    caption: '所属DAL Team',
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
                    resizable:true
                }, {
                    field: 'comment',
                    caption: '所属DAL Team',
                    size: '15%',
                    attr: 'align=center',
                    sortable: true,
                    resizable:true
                }, {
                    field: 'db_address',
                    caption: 'DB Address',
                    size: '15%',
                    attr: 'align=center',
                    sortable: true,
                    resizable:true
                }, {
                    field: 'db_port',
                    caption: 'DB Port',
                    size: '5%',
                    attr: 'align=center',
                    sortable: true,
                    resizable:true
                }, {
                    field: 'db_user',
                    caption: 'DB User',
                    size: '10%',
                    attr: 'align=center',
                    sortable: true,
                    resizable:true
                }, {
                    field: 'db_password',
                    caption: 'DB Password',
                    size: '10%',
                    attr: 'align=center',
                    sortable: true,
                    resizable:true
                }, {
                    field: 'db_catalog',
                    caption: 'DB Catalog',
                    size: '15%',
                    attr: 'align=center',
                    sortable: true,
                    resizable:true
                }, {
                    field: 'db_providerName',
                    caption: '数据库类型',
                    size: '10%',
                    attr: 'align=center',
                    sortable: true,
                    resizable:true
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

    jQuery(document).ready(function(){

        var setDefaultAddDbVal = function(){
            $("#error_msg").html(" ");

            var dbType = $.trim($("#dbtype").val());

            if("MySQL"==dbType){
                $("#dbaddress").val('pub.mysql.db.dev.sh.ctripcorp.com');
                $("#dbuser").val('uws_dbticket');
                $("#dbpassword").val('kgd8v5CenyoMjtg1uwzj');
            }else if("SQLServer"==dbType){
                $("#dbaddress").val('devdb.dev.sh.ctriptravel.com');
                $("#dbuser").val('uws_AllInOneKey_dev');
                $("#dbpassword").val('!QAZ@WSX1qaz2wsx');
            }else{
                $("#dbaddress").val('');
                $("#dbuser").val('');
                $("#dbpassword").val('');
            }
        };

        var getAllCatalog = function(successInfo){
            $("#error_msg").html("正在连接数据库，请稍等...");
            var dbType = $("#dbtype").val();
            var dbAddress = $("#dbaddress").val();
            var dbPort = $("#dbport").val();
            var dbUser = $("#dbuser").val();
            var dbPassword = $("#dbpassword").val();
            cblock($("body"));
            $.post("/rest/db/connectionTest", {
                "dbtype": dbType,
                "dbaddress": dbAddress,
                "dbport": dbPort,
                "dbuser": dbUser,
                "dbpassword": dbPassword
            }, function(data){
                if(data.code == "OK"){
                    var allCatalog = [];
                    $.each($.parseJSON(data.info), function (index, value) {
                        allCatalog.push({
                            id: value,
                            title: value
                        });
                    });
                    $("#dbcatalog")[0].selectize.clearOptions();
                    $("#dbcatalog")[0].selectize.addOption(allCatalog);
                    $("#dbcatalog")[0].selectize.refreshOptions(false);
                    $("#error_msg").html(successInfo);
                }else{
                    $("#error_msg").html(data.info);
                }
                $("body").unblock();
            }).fail(function(data){
                    $("#error_msg").text(data);
                    $("body").unblock();
                });
        }

        $(document.body).on('change', "#dbtype", function(event){
            setDefaultAddDbVal();
        });

        $(document.body).on('click', "#add_new_db_next", function(event){
            var dbType = $("#dbtype").val();
            var dbAddress = $("#dbaddress").val();
            var dbPort = $("#dbport").val();
            var dbUser = $("#dbuser").val();
            var dbPassword = $("#dbpassword").val();

            if("no"==dbType){
                $("#error_msg").html("请选择数据库类型");
                return;
            }
            if(dbAddress==null || dbAddress==""){
                $("#error_msg").html("请选择数据库");
                return;
            }
            if(dbPort==null || dbPort==""){
                $("#error_msg").html("请输入数据库端口");
                return;
            }
            if(dbUser==null || dbUser==""){
                $("#error_msg").html("请输入数据库登陆用户");
                return;
            }
            if(dbPassword==null || dbPassword==""){
                $("#error_msg").html("请输入数据库登陆用户密码");
                return;
            }
            $("#add_new_db_step1").hide();
            $("#add_new_db_step2").show();
            $("#conn_test").hide();
            $("#add_new_db_next").hide();
            $("#add_new_db_prev").show();
            $("#add_new_db_save").show();
            getAllCatalog("");
        });

        $(document.body).on('click', "#add_new_db_prev", function(event){
            $("#add_new_db_step1").show();
            $("#add_new_db_step2").hide();
            $("#conn_test").show();
            $("#add_new_db_next").show();
            $("#add_new_db_prev").hide();
            $("#add_new_db_save").hide();
            $("#error_msg").html(" ");
        });

        $(document.body).on('click', "#conn_test", function(event){
            getAllCatalog("connection successful");
        });

        $(document.body).on('click', "#add_new_db_save", function(event){

            var dbType = $("#dbtype").val();
            var all_In_One_Name = $("#allinonename").val();
            var dbAddress = $("#dbaddress").val();
            var dbPort = $("#dbport").val();
            var dbUser = $("#dbuser").val();
            var dbPassword = $("#dbpassword").val();
            var dbCatalog = $("#dbcatalog").val();

            if("no"==dbType){
                $("#error_msg").html("请选择数据库类型");
                return;
            }
            if(""==all_In_One_Name || null==all_In_One_Name){
                $("#error_msg").html("请输入All-In-One Name");
                return;
            }
            if(dbAddress==null || dbAddress==""){
                $("#error_msg").html("请选择数据库");
                return;
            }
            if(dbPort==null || dbPort==""){
                $("#error_msg").html("请输入数据库端口");
                return;
            }
            if(dbUser==null || dbUser==""){
                $("#error_msg").html("请输入数据库登陆用户");
                return;
            }
            if(dbPassword==null || dbPassword==""){
                $("#error_msg").html("请输入数据库登陆用户密码");
                return;
            }
            if(dbCatalog==null || dbCatalog==""){
                $("#error_msg").html("请输入数据库");
                return;
            }

            cblock($("body"));
            $.post("/rest/db/addNewAllInOneDB", {
                "dbtype": dbType,
                "allinonename": all_In_One_Name,
                "dbaddress": dbAddress,
                "dbport": dbPort,
                "dbuser": dbUser,
                "dbpassword": dbPassword,
                "dbcatalog": dbCatalog
            }, function(data){
                if(data.code == "OK"){
                    $("#error_msg").html("保存成功.");
                    refreshAllDB();
                }else{
                    $("#error_msg").html(data.info);
                }
                $("body").unblock();
            }).fail(function(data){
                    $("#error_msg").text(data);
                    $("body").unblock();
                });
        });


    });

})(window);