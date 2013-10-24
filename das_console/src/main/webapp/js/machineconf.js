
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
        }, {
            "bSortable": false
        }, {
            "bSortable": false
        },
        {
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

    $('#reload_machine').click(function () {

        if($('#main_area').children().length > 0){
            $('#configs').dataTable().fnClearTable();    
        }

        $.get("/console/dal/das/configure/node", function (data) {
            //data = JSON.parse(data);
            $.each(data, function (index, value) {
                $('#configs').dataTable().fnAddData( 
                    [value.name, value.setting.directory, value.setting.maxHeapSize, value.setting.startingHeapSize, 
                    sprintf("<button type='button' class='btn btn-success modify' onclick='mod_machine(this);' mod_id='%s'>修改</button>&nbsp;<button type='button' class='btn btn-danger delete' onclick='del_machine(\"%s\");'>删除</button>",
                        value.name, value.name)]
                    );
                $.data(document.body, value.name, value);
            });    
        });
    });

    $("#save_machine").click(function(){

        var postData = {
                "name": $("#machine_ip").val(),
                "directory": $("#workspace").val(),
                "maxHeapSize": $("#max_heap").val(),
                "startingHeapSize": $("#default_heap").val()
            };

        if($.data(document.body, "modify") == $("#machine_ip").val()){
            $.ajax({
                type: 'PUT',
                url: "/console/dal/das/configure/node",
                //dataType: 'json',
                data: postData,
                success: function(data, status, event) {
                    if(data.code == 'OK'){
                        $(".icon-refresh").trigger('click');
                    }
                },
                error: function (data, status, event) {
                }
            });
        }else{
            $.post("/console/dal/das/configure/node",
            postData, 
            function (data, status, event) {
                if(data.code == 'OK'){
                    $(".icon-refresh").trigger('click');
                }
            });
        }
    });

    $(".icon-refresh").trigger('click');

});

var del_machine = function(ip){
    $.ajax({
        type: 'DELETE',
        url: sprintf('/console/dal/das/configure/node/%s', ip),
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

var mod_machine = function(obj){
    var value = $.data(document.body, $(obj).attr('mod_id'));
    $("#machine_ip").val(value.name);
    $("#workspace").val(value.setting.directory);
    $("#max_heap").val(value.setting.maxHeapSize);
    $("#default_heap").val(value.setting.startingHeapSize);
    $.data(document.body, "modify", value.name);
    // $.ajax({
    //     type: 'PUT',
    //     url: '/console/dal/das/configure/node/',
    //     data: {},
    //     //dataType: 'json',
    //     success: function(data, status, event) {
    //         if(data.code == 'OK'){
    //             $(".icon-refresh").trigger('click');
    //         }
    //     },
    //     error: function (data, status, event) {
    //     }
    // });
};