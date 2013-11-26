
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

        $(document.body).removeData(); 
        
         if($('#main_area').children().length > 0){
            $('#configs').dataTable().fnClearTable();    
        }

        $.get("/console/dal/das/configure/db", function (data) {
            //data = JSON.parse(data);
            $.each(data, function (index, value) {
                $('#configs').dataTable().fnAddData( 
                    [
                    '<a href="javascript:;" class="icon-plus" onclick="toggleTr(this);" title="Slaves"></a>',
                    value.name, value.setting.driver, value.setting.jdbcUrl,
                    sprintf("<button type='button' class='btn btn-success modify' onclick='mod_db(this);' mod_id='%s'>修改</button>&nbsp;<button type='button' class='btn btn-danger delete' onclick='del_db(\"%s\");'>删除</button>",
                        value.name, value.name)]
                    );
                $.data(document.body, value.name, value);
            });  
        });



    });

     $("#save_db").click(function(){

        var postData = {
                "name": $("#physic_db").val(),
                "driver": $("#driver_class").val(),
                "jdbcUrl": $("#connect_str").val()
            };

        if($.data(document.body, "modify") == $("#physic_db").val()){
            var url = sprintf("/console/dal/das/configure/db/%s", postData["name"]);
            if($("#db_type").val() == "Slave"){
                url = sprintf("/console/dal/das/configure/db/%s/slave/%s", 
                    $("#physic_db").val(), $("#slave_name").val());
                 postData["name"] = $("#slave_name").val();
            }
            $.ajax({
                type: 'PUT',
                url: url,
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
            var url = "/console/dal/das/configure/db";
            if($("#db_type").val() == "Slave"){
                url = sprintf("/console/dal/das/configure/db/%s/slave", $("#physic_db").val());
                postData["name"] = $("#slave_name").val();
            }
            $.post(url, postData, function (data, status, event) {
                    if(data.code == 'OK'){
                        $(".icon-refresh").trigger('click');
                    }
            });
        }
    });

    $("#db_type").change(function(){
        if($("#db_type").val() == "Slave"){
            $("#slaves").show();
        }else{
            $("#slaves").hide();
        }
    });

    $("#slaves").hide();

    $(".icon-refresh").trigger('click');

});

//如果删除Slave，则传入两个参数，第一个为Master名，第二个为Slave的名字
//如果删除Master，则传入一个参数obj, 为Master的名字
var del_db = function(name, slave){

    var url = sprintf('/console/dal/das/configure/db/%s', name); 

    if(slave != undefined){
        url = sprintf('/console/dal/das/configure/db/%s/slave/%s', name, slave);
    }
    

    $.ajax({
        type: 'DELETE',
        url: url,
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

//如果修改Slave，则传入两个参数，第一个为Slave所对应的行，第二个为Slave的名字
//如果修改Master，则传入一个参数obj, 为Master所对应的行
var mod_db = function(obj, name){
    var value = $.data(document.body, $(obj).attr('mod_id'));
    $("#physic_db").val(value.name);
    $("#driver_class").val(value.setting.driver);
    $("#connect_str").val(value.setting.jdbcUrl);
    if(name != undefined){
        $("#db_type").val("Slave");
        $("#slave_name").val(name);
    }else{
        $("#db_type").val("Master");
    }
    $("#db_type").trigger('change');
    $.data(document.body, "modify", value.name);
};

var toggleTr = function(obj){
    var nTr = $(obj).parents('tr')[0];
    if ($(obj).hasClass('icon-plus'))
    {
        /* Open this row */
        $(obj).removeClass('icon-plus').addClass('icon-minus');

        var mod_id = $(obj).parent().next().text();

        var html_data = $.data(document.body, mod_id+"slave");

        if(html_data == undefined){
            html_data = fnFormatDetails(nTr, mod_id); 
        }else{
            $('#configs').dataTable().fnOpen( 
            nTr, html_data, 'details' );
        }        
    }
    else
    { 
        $(obj).removeClass('icon-minus').addClass('icon-plus');
        $('#configs').dataTable().fnClose( nTr );
    }
};

var fnFormatDetails = function(nTr, name)
{
    var trdata = "";

    $.get(sprintf('/console/dal/das/configure/db/%s/slave', name),function(data){
        $.each(data, function (index, value) {
            trdata = sprintf("%s<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>", 
                trdata, value.name, value.setting.driver, value.setting.jdbcUrl,
            sprintf(
                '<button type="button" class="btn btn-success modify" onclick="mod_db(this, &quot;%s&quot;);" mod_id="%s">修改</button>&nbsp;<button type="button" class="btn btn-danger delete" onclick="del_db(&quot;%s&quot;, &quot;%s&quot;);">删除</button>'
            ,value.name, name, name, value.name));
        });

        var html_data = sprintf(
        '<table class="table table-striped table-bordered table-hover dataTable" aria-describedby="configs_info">%s</table>',
        trdata);


        $.data(document.body, name+"slave", html_data);

        $('#configs').dataTable().fnOpen( 
            nTr, html_data, 'details' );
    });
     
};