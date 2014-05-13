(function (window, undefined) {

    var Render = function () {

    };

    var refreshGroup = function () {
        w2ui['grid'].clear();
        cblock($("body"));
        $.get("/rest/group/get?rand=" + Math.random(),function (data) {
            var allGroup = [];
            $.each(data, function (index, value) {
                value.recid = allGroup.length + 1;
                allGroup.push(value);
            });
            w2ui['grid'].add(allGroup);
            $("body").unblock();
        }).fail(function (data) {
                alert("获取所有Group失败!");
            });
    };

    var addGroup = function(){
        $("#name").val('');
        $("#comment").val('');
        $("#groupModal").modal({
            "backdrop": "static"
        });
        $("#save_group").click(function(){
            var group_name = $("#name").val();
            var comment = $("#comment").val();
            var postData = {
                groupName:group_name,
                groupComment:comment
            };
            if(group_name==null || $.trim(group_name)==''){
                $("#error_msg").html('请输入Group Name!');
            }else{
                $.post("/rest/group/add", postData,function (data) {
                    if (data.code == "OK") {
                        $("#groupModal").modal('hide');
                        refreshGroup();
                    } else {
                        alert(data.info);
                    }
                }).fail(function (data) {
                        alert("执行异常:"+data);
                    });
            }
        });
    };

    var editGroup = function(){
        var records = w2ui['grid'].getSelection();
        var record = w2ui['grid'].get(records[0]);
        $("#name2").val(record["group_name"]);
        $("#comment2").val(record["group_comment"]);
        $("#groupModal2").modal({
            "backdrop": "static"
        });
        $("#update_group").click(function(){
            var records = w2ui['grid'].getSelection();
            var record = w2ui['grid'].get(records[0]);
            var postData = {
                groupId:record['id'],
                groupName:$("#name2").val(),
                groupComment:$("#comment2").val()
            };
            if(record!=null){
                $.post("/rest/group/update", postData,function (data) {
                    if (data.code == "OK") {
                        $("#groupModal2").modal('hide');
                        refreshGroup();
                    } else {
                        alert(data.info);
                    }
                }).fail(function (data) {
                        alert("执行异常:"+data);
                    });
            }else{
                alert('请选择一个group！');
            }
        });
    };

    var delGroup = function(){
        var records = w2ui['grid'].getSelection();
        var record = w2ui['grid'].get(records[0]);
        if(record!=null){
            if (confirm("Are you sure to delete?")) {
                $.post("/rest/group/delete", {id:record["id"]},function (data) {
                    if (data.code == "OK") {
                        $("#groupModal").modal('hide');
                        refreshGroup();
                    } else {
                        alert(data.info);
                    }
                }).fail(function (data) {
                        alert("执行异常:"+data);
                    });
            }
        }else{
            alert('请选择一个group！');
        }
    };

    Render.prototype = {
        render_layout: function (render_obj) {
            $(render_obj).w2layout({
                name: 'main_layout',
                panels: [
                    {
                        type: 'main'
                    }
                ],
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
                    //toolbarSearch: false,
                    toolbarAdd: false,
                    toolbarDelete: false,
                    //toolbarSave: true,
                    toolbarEdit: false
                },
                toolbar: {
                    items: [
                        {
                            type: 'break'
                        },
                        {
                            type: 'button',
                            id: 'refreshGroup',
                            caption: '刷新',
                            icon: 'fa fa-refresh'
                        },
                        {
                            type: 'button',
                            id: 'addGroup',
                            caption: '添加Group',
                            icon: 'fa fa-plus'
                        },
                        {
                            type: 'button',
                            id: 'editGroup',
                            caption: '修改Group',
                            icon: 'fa fa-edit'
                        },
                        {
                            type: 'button',
                            id: 'delGroup',
                            caption: '删除Group',
                            icon: 'fa fa-times'
                        }
                    ],
                    onClick: function (target, data) {
                        switch (target) {
                            case 'refreshGroup':
                                refreshGroup();
                                break;
                            case 'addGroup':
                                addGroup();
                                break;
                            case 'editGroup':
                                editGroup();
                                break;
                            case 'delGroup':
                                delGroup();
                                break;
                        }
                    }
                },
                searches: [
                    {
                        field: 'group_name',
                        caption: 'Group Name',
                        type: 'text'
                    },
                    {
                        field: 'group_comment',
                        caption: '备注',
                        type: 'text'
                    }
                ],
                columns: [
                    {
                        field: 'group_name',
                        caption: 'Group Name',
                        size: '50%',
                        sortable: true,
                        attr: 'align=center'
                    },
                    {
                        field: 'group_comment',
                        caption: '备注',
                        size: '50%',
                        sortable: true
                    }
                ],
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

    w2ui['grid_toolbar'].click('refreshGroup', null);

    $(window).resize(function () {
        $('#main_layout').height($(document).height() - 50);
    });

})(window);