(function (window, undefined) {
    var Render = function () {
    };

    /**
     * reload current dal team user
     */
    var refreshMember = function () {
        w2ui['grid'].clear();
        var current_group = w2ui['grid'].current_group;
        if (current_group == undefined) {
            if (w2ui['sidebar'].nodes.length < 1 || w2ui['sidebar'].nodes[0].nodes.length < 1)
                return;
            current_group = w2ui['sidebar'].nodes[0].nodes[0].id;
        }
        cblock($("body"));
        $.get("/rest/member/groupuser", {groupId: current_group, rand: Math.random()}, function (data) {
            var allMember = [];
            $.each(data, function (index, value) {
                value.recid = allMember.length + 1;
                allMember.push(value);
            });
            w2ui['grid'].add(allMember);
            $("body").unblock();
        }).fail(function (data) {
            alert("获取所有用户列表失败!");
        });
    };

    var addMember = function () {
        $("#error_msg").empty();
        var current_group = w2ui['grid'].current_group;
        if (current_group == null || current_group == '') {
            alert('请先选择一个 DAL Team');
            return;
        }
        reload_all_members();
        $("#memberModal").modal({
            "backdrop": "static"
        });
    };

    var reload_all_groups = function () {
        cblock($("body"));

        $.get("/rest/group/get", {rand: Math.random()}).done(function (data) {
            if ($("#group_list")[0] != undefined && $("#group_list")[0].selectize != undefined) {
                $("#group_list")[0].selectize.clearOptions();
            } else {
                $("#group_list").selectize({
                    valueField: 'id',
                    labelField: 'title',
                    searchField: 'title',
                    sortField: 'title',
                    options: [],
                    create: false
                });
            }

            var allDalTeam = [];
            $.each(data, function (index, value) {
                allDalTeam.push({
                    id: value['id'], title: value['group_name']
                });
            });

            $("#group_list")[0].selectize.addOption(allDalTeam);
            $("#group_list")[0].selectize.refreshOptions(false);
            $("body").unblock();
        }).fail(function (data) {
            $("body").unblock();
        });
    };

    var addDalTeam = function () {
        $("#add_group_error_msg").empty();
        var current_group = w2ui['grid'].current_group;
        if (current_group == null || current_group == '') {
            alert('请先选择一个 DAL Team');
            return;
        }

        reload_all_groups();
        $("#addDalTeamModal").modal({"backdrop": "static"});
    };

    var updateUserPermision = function () {
        $("#up_error_msg").empty();
        $("#up_group_error_msg").empty();
        var current_group = w2ui['grid'].current_group;
        if (current_group == null || current_group == '') {
            alert('请先选择一个 DAL Team');
            return;
        }
        var records = w2ui['grid'].getSelection();
        var record = w2ui['grid'].get(records[0]);
        if (record == null) {
            alert('请选择一个用户！');
        } else if (record['dalTeam'] === true) {
            if (record['role'] == 'Limited') {
                $("#up_group_role").val('2');
            } else {
                $("#up_group_role").val('1');
            }
            if (record['adduser'] == '允许') {
                $("#up_allowGroupAddUser").prop("checked", true);
            } else {
                $("#up_allowGroupAddUser").prop("checked", false);
            }

            $("#group_name").html(record['userName']);
            $("#updateDALTeamModal").modal({"backdrop": "static"});
        } else {
            if (record['role'] == 'Limited') {
                $("#up_user_role").val('2');
            } else {
                $("#up_user_role").val('1');
            }
            if (record['adduser'] == '允许') {
                $("#up_allowAddUser").prop("checked", true);
            } else {
                $("#up_allowAddUser").prop("checked", false);
            }

            $("#user_name").html(record['userName']);
            $("#updateUserModal").modal({"backdrop": "static"});
        }
    };

    var delMember = function () {
        var records = w2ui['grid'].getSelection();
        var record = w2ui['grid'].get(records[0]);
        var current_group = w2ui['grid'].current_group;
        if (record != null) {
            if (confirm("您确定要删除吗?")) {
                $.post("/rest/member/delete", {
                    userId: record["id"],
                    groupId: current_group,
                    isDalTeam: record['dalTeam']
                }, function (data) {
                    if (data.code == "OK") {
                        $("#memberModal").modal('hide');
                        refreshMember();
                    } else {
                        alert(data.info);
                    }
                }).fail(function (data) {
                    alert("执行异常:" + data);
                });
            }
        } else {
            alert('请选择一个用户！');
        }
    };

    /**
     * reload the user exits in codegen system
     */
    var reload_all_members = function () {
        cblock($("body"));

        $.get("/rest/member/all", {rand: Math.random()}).done(function (data) {
            if ($("#members")[0] != undefined && $("#members")[0].selectize != undefined) {
                $("#members")[0].selectize.clearOptions();
            } else {
                $("#members").selectize({
                    valueField: 'id',
                    labelField: 'title',
                    searchField: 'title',
                    sortField: 'title',
                    options: [],
                    create: false
                });
            }

            var allMembers = [];
            $.each(data, function (index, value) {
                allMembers.push({
                    id: value['id'], title: value['userName']
                });
            });

            $("#members")[0].selectize.addOption(allMembers);
            $("#members")[0].selectize.refreshOptions(false);
            $("body").unblock();
        }).fail(function (data) {
            $("body").unblock();
        });
    };

    var applyAdd = function () {
        var current_group = w2ui['grid'].current_group;
        if (current_group == null || current_group == '') {
            alert('请先选择一个 DAL Team');
            return;
        }
        cblock($("body"));
        var email = "";
        $.ajax({
            type: "GET",
            url: "/rest/group/getDalTeamEmail",
            data: {rand: Math.random()},
            async: false,
            success: function (data) {
                if (data != null && data != undefined) {
                    email = "mailto:" + data;
                }
            }
        });

        $.get("/rest/member/groupuser", {groupId: current_group, rand: Math.random()}, function (data) {
            if (data != null && data.length > 0) {
                email = 'mailto:' + data[0]['userEmail'];
            }
            window.location.href = email;
        }).fail(function (data) {
            alert(data);
        });
        $("body").unblock();
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
                    type: 'main'
                }],
                onResizing: function (event) {
                    // ace.edit("code_editor").resize();
                }
            });
        },
        render_sidebar: function () {
            w2ui['main_layout'].content(
                'left',
                '<div style="color: #34495E !important;font-size: 15px;background-color: #eee; padding: 7px 5px 6px 20px; border-bottom: 1px solid silver">'
                + '所有 DAL Team'
                + "</div>"
                + '<div id="jstree_groups"></div>');

            $('#jstree_groups').on('select_node.jstree', function (e, obj) {
                window.render.render_grid();
                w2ui['grid'].current_group = obj.node.id;
                w2ui['grid_toolbar'].click('refreshMember', null);
            }).jstree({
                'core': {
                    'check_callback': true,
                    'multiple': false,
                    'data': {
                        'url': function (node) {
                            return node.id == "#" ? "/rest/member?root=true&rand=" + Math.random() : "/rest/member?rand=" + Math.random();
                        }
                    }
                }
            });
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
                        id: 'refreshMember',
                        caption: '刷新',
                        icon: 'glyphicon glyphicon-refresh'
                    }, {
                        type: 'button',
                        id: 'addMember',
                        caption: '添加组员',
                        icon: 'glyphicon glyphicon-user'
                    }, {
                        type: 'button',
                        id: 'addDalTeam',
                        caption: '添加Dal Team',
                        icon: 'glyphicon glyphicon-th'
                    }, {
                        type: 'button',
                        id: 'delMember',
                        caption: '删除组员',
                        icon: 'glyphicon glyphicon-remove'
                    }, {
                        type: 'button',
                        id: 'upMember',
                        caption: '权限修改',
                        icon: 'glyphicon glyphicon-edit'
                    }, {
                        type: 'button',
                        id: 'applyAdd',
                        caption: '申请加入DAL Team',
                        icon: 'glyphicon glyphicon-envelope'
                    }],
                    onClick: function (target, data) {
                        switch (target) {
                            case 'refreshMember':
                                refreshMember();
                                break;
                            case 'addMember':
                                addMember();
                                break;
                            case 'addDalTeam':
                                addDalTeam();
                                break;
                            case 'delMember':
                                delMember();
                                break;
                            case 'applyAdd':
                                applyAdd();
                                break;
                            case 'upMember':
                                updateUserPermision();
                                break;
                        }
                    }
                },
                searches: [{
                    field: 'userName',
                    caption: '组员姓名',
                    type: 'text'
                }, {
                    field: 'userEmail',
                    caption: '组员电子邮件',
                    type: 'text'
                }],
                columns: [{
                    field: 'userName',
                    caption: '组员姓名',
                    size: '25%',
                    sortable: true,
                    attr: 'align=center'
                }, {
                    field: 'userEmail',
                    caption: '组员电子邮件',
                    size: '25%',
                    sortable: true,
                    attr: 'align=center'
                }, {
                    field: 'role',
                    caption: '组员角色',
                    size: '25%',
                    sortable: true,
                    attr: 'align=center'
                }, {
                    field: 'adduser',
                    caption: '管理组员',
                    size: '25%',
                    sortable: true,
                    attr: 'align=center'
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
    window.render.render_sidebar();
    window.render.render_grid();
    $(window).resize(function () {
        $('#main_layout').height($(document).height() - 50);
    });

    $(function () {
        $(document.body).on("click", "#save_member", function () {
            var id = $("#members").val();
            if (id == null) {
                $("#error_msg").html('请选择用户!');
            } else {
                var current_group = w2ui['grid'].current_group;
                $.post("/rest/member/addUser", {
                    groupId: current_group,
                    userId: id,
                    user_role: $("#user_role").val(),
                    allowAddUser: $("#allowAddUser").prop("checked")
                }, function (data) {
                    if (data.code == "OK") {
                        $("#memberModal").modal('hide');
                        refreshMember();
                    } else {
                        $("#error_msg").html(data.info);
                    }
                }).fail(function (data) {
                    $("#error_msg").html(data.info);
                });
            }
        });

        $(document.body).on("click", "#save_up_member", function () {
            var records = w2ui['grid'].getSelection();
            var record = w2ui['grid'].get(records[0]);
            var user_id = record['id'];
            $.post("/rest/member/update", {
                groupId: w2ui['grid'].current_group,
                userId: user_id,
                user_role: $("#up_user_role").val(),
                allowAddUser: $("#up_allowAddUser").prop("checked")
            }, function (data) {
                if (data.code == "OK") {
                    $("#updateUserModal").modal('hide');
                    refreshMember();
                } else {
                    $("#up_error_msg").html(data.info);
                }
            }).fail(function (data) {
                $("#up_error_msg").html(data.info);
            });
        });

        $(document.body).on("click", "#save_add_group", function () {
            var id = $("#group_list").val();
            if (id == null) {
                $("#add_group_error_msg").html('请选择一个DAL Team!');
            } else {
                var current_group = w2ui['grid'].current_group;
                $.post("/rest/member/addGroup", {
                    currentGroupId: current_group,
                    childGroupId: id,
                    child_group_role: $("#group_role").val(),
                    allowGroupAddUser: $("#allowGroupAddUser").prop("checked")
                }, function (data) {
                    if (data.code == "OK") {
                        $("#addDalTeamModal").modal('hide');
                        refreshMember();
                    } else {
                        $("#add_group_error_msg").html(data.info);
                    }
                }).fail(function (data) {
                    $("#add_group_error_msg").html(data.info);
                });
            }
        });

        $(document.body).on("click", "#save_up_group", function () {
            var records = w2ui['grid'].getSelection();
            var record = w2ui['grid'].get(records[0]);
            var child_group_id = record['id'];
            $.post("/rest/member/updateGroup", {
                currentGroupId: w2ui['grid'].current_group,
                child_group_id: child_group_id,
                child_group_role: $("#up_group_role").val(),
                allowGroupAddUser: $("#up_allowGroupAddUser").prop("checked")
            }, function (data) {
                if (data.code == "OK") {
                    $("#updateDALTeamModal").modal('hide');
                    refreshMember();
                } else {
                    $("#up_group_error_msg").html(data.info);
                }
            }).fail(function (data) {
                $("#up_group_error_msg").html(data.info);
            });
        });
    });
})(window);