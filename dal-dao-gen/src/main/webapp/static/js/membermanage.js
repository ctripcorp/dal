
(function (window, undefined) {

    var Render = function () {

    };

    var refreshMember = function () {
        w2ui['grid'].clear();
        var current_group = w2ui['grid'].current_group;
        if (current_group == undefined) {
            if (w2ui['sidebar'].nodes.length < 1 || w2ui['sidebar'].nodes[0].nodes.length < 1)
                return;
            current_group = w2ui['sidebar'].nodes[0].nodes[0].id;
        }
        cblock($("body"));
        $.get("/rest/member/groupuser?groupId=" + current_group + "&rand=" + Math.random(),function (data) {
            var allMember = [];
            $.each(data, function (index, value) {
                value.recid = allMember.length + 1;
                allMember.push(value);
            });
            w2ui['grid'].add(allMember);
            $("body").unblock();
        }).fail(function (data) {
                alert("获取所有Member失败!");
            });
    };

    var addMember = function(){
        $("#error_msg").html('');
        var current_group = w2ui['grid'].current_group;
        if(current_group==null || current_group==''){
            alert('请先选择Group');
            return;
        }
        reload_all_members();
        $("#memberModal").modal({
            "backdrop": "static"
        });
    };

    var delMember = function(){
        var records = w2ui['grid'].getSelection();
        var record = w2ui['grid'].get(records[0]);
        if(record!=null){
            if (confirm("Are you sure to delete?")) {
                $.post("/rest/member/delete", {
                    userId:record["id"],
                    groupId:record['groupId']
                },function (data) {
                    if (data.code == "OK") {
                        $("#memberModal").modal('hide');
                        refreshMember();
                    } else {
                        alert(data.info);
                    }
                }).fail(function (data) {
                        alert("执行异常:"+data);
                    });
            }
        }else{
            alert('请选择一个member！');
        }

    };

    var reload_all_members = function () {
        cblock($("body"));

        $.get("/rest/member/all?rand=" + Math.random()).done(function (data) {

            if ($("#members")[0] != undefined && $("#members")[0].selectize != undefined) {
                $("#members")[0].selectize.clearOptions();
            } else {
                $("#members").selectize({
                    //maxItems: null,
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
                    id: value['id'],
                    title: value['userName']
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
            alert('请先选择Group');
            return;
        }
        cblock($("body"));
        var emailUrl = 'mailto:R%26Dsysdev_dal@Ctrip.com';
        $.get("/rest/member/groupuser?groupId=" + current_group + "&rand=" + Math.random(),function (data) {
            if(data!=null && data.length>0){
                emailUrl='mailto:'+data[0]['userEmail'];
            }
            window.location.href = emailUrl;
        }).fail(function (data) {

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
                onResizing: function(event) {
                    //ace.edit("code_editor").resize();
                }   
            });
        },
        render_sidebar: function () {
            w2ui['main_layout'].content('left', '<div style="color: #34495E !important;font-size: 15px;background-color: #eee; padding: 7px 5px 6px 20px; border-bottom: 1px solid silver">'
                +'ALL DAL Team'
                +"</div>"
                +'<div id="jstree_groups"></div>');

            $('#jstree_groups').on('select_node.jstree', function (e, obj) {
                window.render.render_grid();
                w2ui['grid'].current_group = obj.node.id;
                w2ui['grid_toolbar'].click('refreshMember', null);
            }).jstree({ 
                'core' : {
                    'check_callback' : true,
                    'multiple': false,
                    'data' : {
                      'url' : function (node) {
                        return node.id == "#" ? "/rest/member?root=true&rand=" + Math.random() : "/rest/member?rand=" + Math.random();
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
                    toolbarEdit: false
//                    selectColumn: true
                },
                multiSelect: false,
                toolbar: {
                    items: [{
                        type: 'break'
                    }, {
                        type: 'button',
                        id: 'refreshMember',
                        caption: '刷新',
                        icon: 'fa fa-refresh'
                    }, {
                        type: 'button',
                        id: 'addMember',
                        caption: '添加组员',
                        icon: 'fa fa-plus'
                    }, {
                        type: 'button',
                        id: 'delMember',
                        caption: '删除组员',
                        icon: 'fa fa-times'
                    }, {
                        type: 'button',
                        id: 'applyAdd',
                        caption: '申请加入DAL Team',
                        icon: 'fa fa-envelope'
                    }],
                    onClick: function (target, data) {
                        switch (target) {
                            case 'refreshMember':
                                refreshMember();
                                break;
                            case 'addMember':
                                addMember();
                                break;
                            case 'delMember':
                                delMember();
                                break;
                            case 'applyAdd':
                                applyAdd();
                                break;
                        }
                    }
                },
                searches: [{
                    field: 'userName',
                    caption: 'Member Name',
                    type: 'text'
                }, {
                    field: 'userEmail',
                    caption: 'Member Email',
                    type: 'text'
                }],
                columns: [{
                    field: 'userName',
                    caption: '组员名字',
                    size: '50%',
                    sortable: true,
                    attr: 'align=center'
                }, {
                    field: 'userEmail',
                    caption: '组员邮件地址',
                    size: '50%',
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

    jQuery(document).ready(function(){
        $("#save_member").click(function(){
            var id = $("#members").val();
            if(id==null){
                $("#error_msg").html('请选择member!');
            }else{
                var current_group = w2ui['grid'].current_group;
                $.post("/rest/member/add", {
                    groupId : current_group,
                    userId : id
                },function (data) {
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
    });

})(window);