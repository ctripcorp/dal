$(document).ready(function () {

    $('#dao_tasks').dataTable({
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

    $("#insert_data").click(function () {
        var post_data = {};
        post_data["address"] = $("#address").val();
        post_data["name"] = $("#name").val();
        post_data["telephone"] = $("#telephone").val();
        post_data["age"] = $("#age").val();
        post_data["gender"] = $("#gender").val();
        post_data["birth"] = $("#birth").val();
        $.post("/DemoInsert.aspx", post_data, function (data) {
            ttt();
        });
    });

    $("#change_port").click(function () {
        $.get("/Default.aspx?port=" + $("#port_num").val(), function (data) {
            ttt();
        });
    });

    $("#change_db").click(function () {
        $.get("/Default.aspx?db=" + $("#db_name").val(), function (data) {
            ttt();
        });
    });

    ttt();

});

var ttt = function () {
    if ($('#main_area').children().length > 0) {
        $('#dao_tasks').dataTable().fnClearTable();
    }

    $.get("DemoGet.aspx", function (data, status) {
        //var real_data = JSON.parse(data);
        var real_data = data;

        $.each(real_data, function (index, value) {
            $('#dao_tasks').dataTable().fnAddData(
                    [value.Address, value.Name, value.Telephone, value.Age, value.Gender, value.Birth,
                    sprintf("<button type='button' class='btn btn-danger delete' value_id='%s'>删除</button>"
                        , value.ID)]
                    );
        });

        $(".delete.btn-danger").click(function () {
            if (confirm("Are you sure?")) {
                var id = $(this).attr("value_id");
                $.get("/DemoDelete.aspx?task_id=" + id, function (data) {
                    ttt();
                });
            }
        });
    });
};