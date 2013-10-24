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

    $('#reload_port').click(function () {

        if($('#main_area').children().length > 0){
            $('#configs').dataTable().fnClearTable();    
        }

        $.get("/console/dal/das/configure/port", function (data) {
            //data = JSON.parse(data);
            $.each(data.ports, function (index, value) {
                $('#configs').dataTable().fnAddData( 
                    [value, 
                    sprintf("<button type='button' class='btn btn-danger delete' del_value='%s'>删除</button>",
                        value)]
                    );
            }); 
            $(".delete").click(function(){
                var number = $(this).attr('del_value');
                $.ajax({
                    type: 'DELETE',
                    url: sprintf('/console/dal/das/configure/port/%s', number),
                    //dataType: 'json',
                    success: function(responseData, textStatus, jqXHR) {
                        console.log("success");
                    },
                    error: function (responseData, textStatus, errorThrown) {
                        console.log("fail");
                    }
                });
            });       
        });
    });

    $("#save_port").click(function(){
        $.post("/console/dal/das/configure/port",
            {"number": $("#port").val()}, function (data, status, event) {
                if(data.code == 'OK'){
                    $(".icon-refresh").trigger('click');
                }
        });
    });


    $(".icon-refresh").trigger('click');

});