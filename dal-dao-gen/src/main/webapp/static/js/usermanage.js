(function (window, undefined) {
    var Render = function () {
    };

    var refreshUser = function () {
        w2ui['grid'].clear();
        cblock($("body"));
        $.get("/rest/user/get", {rand: Math.random()}, function (data) {
            var allUser = [];
            $.each(data, function (index, value) {
                value.recid = allUser.length + 1;
                allUser.push(value);
            });
            w2ui['grid'].add(allUser);
            $("body").unblock();
        }).fail(function (data) {
            alert("获取所有用户失败!");
        });
    };

    var addUser = function () {
        $("#error_msg").html('');
        $("#userNo").val('');
        $("#userName").val('');
        $("#userEmail").val('');
        $("#userModal").modal({"backdrop": "static"});
    };

    var editUser = function () {
        $("#error_msg2").html('');
        var records = w2ui['grid'].getSelection();
        var record = w2ui['grid'].get(records[0]);
        if (record == null || record == '') {
            alert("请先选择一个用户");
            return;
        }
        $("#userNo2").val(record["userNo"]);
        $("#userNo2").prop("readonly", true);
        $("#userName2").val(record["userName"]);
        $("#userEmail2").val(record["userEmail"]);
        $("#userModal2").modal({"backdrop": "static"});
    };

    var delUser = function () {
        var records = w2ui['grid'].getSelection();
        var record = w2ui['grid'].get(records[0]);
        if (record != null) {
            if (confirm("您确定要删除吗?")) {
                $.post("/rest/user/delete", {userId: record["id"]}, function (data) {
                    if (data.code == "OK") {
                        $("#userModal").modal('hide');
                        refreshUser();
                    } else {
                        $("#errorNoticeDivMsg").html(data.info);
                        $("#errorNoticeDiv").modal({"backdrop": "static"});
                    }
                }).fail(function (data) {
                    alert("执行异常:" + data);
                });
            }
        } else {
            alert('请选择一个用户！');
        }
    };

    Render.prototype = {
        render_layout: function (render_obj) {
            $(render_obj).w2layout({
                name: 'main_layout',
                panels: [{
                    type: 'main'
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
                        id: 'refreshUser',
                        caption: '刷新',
                        icon: 'glyphicon glyphicon-refresh'
                    }, {
                        type: 'button',
                        id: 'addUser',
                        caption: '添加用户',
                        icon: 'glyphicon glyphicon-plus'
                    }, {
                        type: 'button',
                        id: 'editUser',
                        caption: '修改用户',
                        icon: 'glyphicon glyphicon-edit'
                    }, {
                        type: 'button',
                        id: 'delUser',
                        caption: '删除用户',
                        icon: 'glyphicon glyphicon-remove'
                    }],
                    onClick: function (target, data) {
                        switch (target) {
                            case 'refreshUser':
                                refreshUser();
                                break;
                            case 'addUser':
                                addUser();
                                break;
                            case 'editUser':
                                editUser();
                                break;
                            case 'delUser':
                                delUser();
                                break;
                        }
                    }
                },
                searches: [{
                    field: 'userNo',
                    caption: '工号',
                    type: 'text'
                }, {
                    field: 'userName',
                    caption: '用户名',
                    type: 'text'
                }, {
                    field: 'userEmail',
                    caption: '电子邮件',
                    type: 'text'
                }],
                columns: [{
                    field: 'userNo',
                    caption: '工号',
                    size: '30%',
                    sortable: true,
                    attr: 'align=center'
                }, {
                    field: 'userName',
                    caption: '用户名',
                    size: '30%',
                    sortable: true,
                    attr: 'align=center'
                }, {
                    field: 'userEmail',
                    caption: '电子邮件',
                    size: '40%',
                    sortable: true
                }],
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
    w2ui['grid_toolbar'].click('refreshUser', null);

    $(function () {
        $(document.body).on("click", "#save_user", function () {
            var userNo = $("#userNo").val();
            var userName = $("#userName").val();
            var userEmail = $("#userEmail").val();
            var defaultPass = $("#defaultPass").val();
            var postData = {
                userNo: userNo,
                userName: userName,
                userEmail: userEmail,
                password: defaultPass
            };

            if (userNo == null || $.trim(userNo) == '') {
                $("#error_msg").html('请输入工号!');
            } else if (userName == null || $.trim(userName) == '') {
                $("#error_msg").html('请输入用户名!');
            } else if (userEmail == null || $.trim(userEmail) == '') {
                $("#error_msg").html('请输入电子邮件!');
            } else {
                $.post("/rest/user/add", postData, function (data) {
                    if (data.code == "OK") {
                        $("#userModal").modal('hide');
                        refreshUser();
                    } else {
                        $("#error_msg").html(data.info);
                    }
                }).fail(function (data) {
                    $("#error_msg").html("执行异常:" + data);
                });
            }
        });

        $(document.body).on("click", "#update_user", function () {
            var records = w2ui['grid'].getSelection();
            var record = w2ui['grid'].get(records[0]);
            var postData = {
                userId: record['id'],
                userNo: $("#userNo2").val(),
                userName: $("#userName2").val(),
                userEmail: $("#userEmail2").val()
            };
            if (record != null) {
                $.post("/rest/user/update", postData, function (data) {
                    if (data.code == "OK") {
                        $("#userModal2").modal('hide');
                        refreshUser();
                    } else {
                        $("#error_msg2").html(data.info);
                    }
                }).fail(function (data) {
                    $("#error_msg2").html("执行异常:" + data);
                });
            } else {
                alert('请选择一个用户！');
            }
        });
    });

    $(window).resize(function () {
        $('#main_layout').height($(document).height() - 50);
    });

})(window);