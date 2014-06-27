
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
        $("#addDbModal").modal({
            "backdrop": "static"
        });
    };

    var editDB = function(){

    };

    var delDB = function(){

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

        var setDefautAddDbVal = function(){
            $("#error_msg").html(" ");

            var dbType = $.trim($("#dbtype").val());

            if("MySQL"==dbType){
                $("#address").val('pub.mysql.db.dev.sh.ctripcorp.com');
                $("#dbuser").val('uws_dbticket');
                $("#dbpassword").val('kgd8v5CenyoMjtg1uwzj');
            }else if("SQLServer"==dbType){
                $("#address").val('devdb.dev.sh.ctriptravel.com');
                $("#dbuser").val('uws_dbticket');
                $("#dbpassword").val('kgd8v5CenyoMjtg1uwzj');
            }else{
                $("#address").val('');
                $("#dbuser").val('');
                $("#dbpassword").val('');
            }
        };

        $(document.body).on('change', "#dbtype", function(event){
            setDefautAddDbVal();
        });
    });

})(window);