
jQuery(document).ready(function () {

    App.init(); // initlayout and core plugins


    $("#configCenter").toggleClass('open');

    $("#configCenter .sub-menu").show();

     $('#configs').dataTable({
        "aoColumns": [{
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

        $.get("/console/dal/das/instance/worker", function (data) {
            //data = JSON.parse(data);
            $.each(data, function (index, value) {
                var ip = value.ip;
                $.each(value.ports.ports, function(index,value){

                    $.get(sprintf("/console/dal/das/monitor/performance/%s/%s", ip,value), function(data){
                        console.log(data.performanceHistory[0]);
                    });

                    $('#configs').dataTable().fnAddData( 
                        [ip, value, "1",
                        sprintf("<button type='button' class='btn btn-danger delete' onclick='delete_worker(\"%s\", %s);'>删除</button>",
                            ip, value)]
                    );
                }); 
            });    
        });
    });

    $(".icon-refresh").trigger('click');

});


var delete_worker = function(ip, port){
    $.ajax({
        type: 'DELETE',
        url: sprintf('/console/dal/das/instance/worker/%s/%s', ip, port),
        //dataType: 'json',
        success: function(data, status, event) {
            if(data.code == 'OK'){
                $(".icon-refresh").trigger('click');
            }
        },
        error: function (data, status, event) {
        }
    });
};