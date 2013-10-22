var projects_array = {};

jQuery(document).ready(function () {

    App.init(); // initlayout and core plugins

    // Tasks.initDashboardWidget();

    jQuery('body').on('click', '.portlet > .portlet-title > .tools > .icon-collapse, .portlet .portlet-title > .tools > .icon-collapse-top', function (e) {
        // e.preventDefault();
        if (jQuery(this).hasClass("icon-collapse")) {
            jQuery(this).removeClass("icon-collapse").addClass("icon-collapse-top");
        } else {
            jQuery(this).removeClass("icon-collapse-top").addClass("icon-collapse");
        }
    });

    $("#configCenter").toggleClass('open');

    $("#configCenter .sub-menu").show();

     $('#configs').dataTable({
        "aoColumns": [{
            "bSortable": false
        }, {
            "bSortable": false
        }],
        "aLengthMenu": [
            [5, 15, 20, -1],
            [5, 15, 20, "所有"] // change per page values here
        ],
        // set the initial value
        "iDisplayLength": 5,
        // "sDom": "<'row-fluid'<'span6'l><'span6'f>r>t<'row-fluid'<'span6'i><'span6'p>>",
        "sPaginationType": "bootstrap",
        "oLanguage": {
            "sLengthMenu": "每页显示_MENU_",
            "sZeroRecords": "抱歉-未找到任何数据",
            "sInfo": "共 _TOTAL_ 条数据，当前显示 _START_ 到 _END_ ",
            "sInfoEmpty": "共 0 条数据，当前显示 0 到 0 ",
            "sSearch": "查找",
            "oPaginate": {
                "sPrevious": "上一页",
                "sNext": "下一页"
            }
        },
        "aoColumnDefs": [{
            'bSortable': false,
            'aTargets': [0]
        }]
    });

    $('#reload_db').click(function () {
        // $.get("http://localhost:8080/console/dal/das/configure/db", function (data) {
        //     //data = JSON.parse(data);
        //     console.log(data);
        // });
        $.ajax({
            type: 'GET',
            url: "http://localhost:8080/console/dal/das/configure/db",
            dataType: "jsonp",
            crossDomain: true,
        }).done(function(){
            console.log(arguments.length);
        }).fail(function(error){
            console.log(arguments.length);
        });
    });

});