
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

     $('#configs').dataTable({
        "aoColumns": [{
            "bSortable": false
        }, {
            "bSortable": false
        }, {
            "bSortable": false
        }, {
            "bSortable": false
        }, {
            "bSortable": false
        }, {
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

    $('#reload_worker').click(function () {

        
         if($('#main_area').children().length > 0){
            $('#configs').dataTable().fnClearTable();    
        }


        $.get("/rest/console/monitor/timeCosts", function(data){

            $.each(data.ids, function(index, value){

                $.get(sprintf("/rest/console/monitor/timeCosts/%s", value), function(data){

                    var totalBytes = 0;
                        var totalCount = 0;
                        var decodeResponseTime = 0;
                        var encodeResponseTime = 0;
                        // var totalTime = 0;
                        var dbTime = 0;
                        var decodeRequest = 0;
                        var encodeRequest = 0;


                    $.each(data.entries, function (index, value) {

                        switch(value.stage){
                            case "totalBytes":
                            totalBytes = value.cost;
                            break;
                            case "totalCount":
                            totalCount = value.cost;
                            break;
                            case "decodeResponseTime":
                            decodeResponseTime = value.cost;
                            break;
                            case "encodeResponseTime":
                            encodeResponseTime = value.cost;
                            break;
                            case "decodeRequest":
                            decodeRequest = value.cost;
                            break;
                            case "encodeRequest":
                            encodeRequest = value.cost;
                            break;
                            // case "totalTime":
                            // totalTime = value.cost;
                            // break;
                            case "dbTime":
                            dbTime = value.cost;
                            break;
                        }

                         
                });

                $('#configs').dataTable().fnAddData( 
                        [totalBytes, totalCount, 
                        //totalTime, 
                        encodeRequest,decodeRequest,
                        encodeResponseTime,decodeResponseTime, dbTime
                        //, 
                        // totalTime - encodeResponseTime - decodeResponseTime - dbTime
                        ]
                        );
                    }); 

            });


        });

    });

    $(".icon-refresh").trigger('click');

});