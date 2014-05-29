
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
                    }],
                    onClick: function (target, data) {
                        switch (target) {
                            case 'refreshAllDB':
                                refreshAllDB();
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
                }],
                columns: [{
                    field: 'dbname',
                    caption: 'DB Name',
                    size: '50%',
                    sortable: true,
                    attr: 'align=center'
                }, {
                    field: 'comment',
                    caption: '所属DAL Team',
                    size: '50%',
                    sortable: true
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

})(window);