var filedTypeMap = {};

var whereKeyValuemap = {};

$(document).ready(function () {

    whereKeyValuemap["0"] = "=";
    whereKeyValuemap["1"] = "!=";
    whereKeyValuemap["2"] = ">";
    whereKeyValuemap["3"] = "<";
    whereKeyValuemap["4"] = ">=";
    whereKeyValuemap["5"] = "<=";
    whereKeyValuemap["6"] = "Between";
    whereKeyValuemap["7"] = "Like";
    whereKeyValuemap["8"] = "In";

    App.init(); // initlayout and core plugins

    // Tasks.initDashboardWidget();

    //initialize the sql editor
    var editor = ace.edit("sql_editor");
    editor.setTheme("ace/theme/monokai");
    editor.getSession().setMode("ace/mode/mysql");

    //initialize the stored procedure editor
    var sp_editor = ace.edit("sp_editor");
    sp_editor.setTheme("ace/theme/monokai");
    sp_editor.getSession().setMode("ace/mode/mysql");

    //collapse or expand the portlet
    $('body').on('click', '.portlet > .portlet-title > .tools > .icon-collapse, .portlet .portlet-title > .tools > .icon-collapse-top', function (e) {
        // e.preventDefault();
        if ($(this).hasClass("icon-collapse")) {
            $(this).removeClass("icon-collapse").addClass("icon-collapse-top");
        } else {
            $(this).removeClass("icon-collapse-top").addClass("icon-collapse");
        }
    });

    //Get all tables/views of a selected database
    $("#databases").change(function (event) {
        var el = $(this).closest(".portlet").children(".portlet-body");
        App.blockUI(el);
        $.get("/database/tables?db_name=" + $(this).val(), function (data) {
            data = JSON.parse(data);
            var html_data = "";
            $.each(data, function (index, value) {
                html_data += "<option>" + value + "</option>";
            });
            $("#tables").html(html_data);
            App.unblockUI(el);
            $("#tables").trigger('change');
        });
    });

    //Get all fields of a selected table/view
    $("#tables").change(function (event) {

        $("#auto_dao_name").val(sprintf("%sDAO", $(this).val()));

        var el = $(this).closest(".portlet").children(".portlet-body");
        App.blockUI(el);

        var url = sprintf("/database/fields?table_name=%s&db_name=%s", $(this).val(), $("#databases").val());

        $.get(url, function (data) {
            data = JSON.parse(data);
            var html_data = "";
            var operator = '<div class="task-config"><div class="task-config-btn btn-group"><a class="btn mini blue" href="#" data-toggle="dropdown" data-hover="dropdown" data-close-others="true">Operator<i class="icon-angle-down"></i></a><ul class="dropdown-menu pull-right"><li><a href="#" value="-1"><i class="icon-check"></i> None</a></li><li><a href="#" value="0"><i class="icon-check-empty"></i> Equal</a></li><li><a href="#" value="1"><i class="icon-check-empty"></i> Not Equal</a></li><li><a href="#" value="2"><i class="icon-check-empty"></i> Greater Than</a></li><li><a href="#" value="3"><i class="icon-check-empty"></i> Less Than</a></li><li><a href="#" value="4"><i class="icon-check-empty"></i> Greater Equal Than</a></li><li><a href="#" value="5"><i class="icon-check-empty"></i> Less Equal Than</a></li><li><a href="#" value="6"><i class="icon-check-empty"></i> Between</a></li><li><a href="#" value="7"><i class="icon-check-empty"></i> Like</a></li><li><a href="#" value="8"><i class="icon-check-empty"></i> In</a></li></ul></div></div>';
            var where_condition = '';
            $.each(data, function (index, value) {

                filedTypeMap[value.name] = value.type;

                html_data += "<option>" + value.name + "</option>";

                where_condition = sprintf(
                    "%s<li><div class='task-title'><span class='task-title-sp'>%s</span></div>%s</li>", where_condition, value.name, operator);
            });
            $("#left_select").html(html_data);
            $("#where_condition").html(where_condition);

            $(".icon-check-empty, .icon-check").each(function(){
                $(this).parent().bind('click', function(event){
                    var current_el = $(this).children().get(0);
                    if(!$(current_el).hasClass(".icon-check")){
                        $(current_el).closest('ul').find("li > a > i.icon-check").removeClass("icon-check").addClass("icon-check-empty");
                        $(current_el).removeClass("icon-check-empty").addClass("icon-check");
                    }
                });
            });

            // $("#where_condition > li > .task-config > .task-config-btn > .dropdown-menu  > li > a").click(function () {
            //     $(this).parent().parent().find("li > a > i.icon-ok").toggleClass("icon-ok");
            //     $(this).find("i").toggleClass("icon-ok");
            // });

            App.unblockUI(el);
        });
    });
    
    //Get all stored procedures of a selected database
    $("#sp_databases").change(function (event) {
        var el = $(this).closest(".portlet").children(".portlet-body");
        App.blockUI(el);
        $.get("/database/sps?db_name=" + $(this).val(), function (data) {
            data = JSON.parse(data);
            var html_data = "";
            $.each(data, function (index, value) {
                html_data += "<option>" + value + "</option>";
            });
            $("#sp_names").html(html_data);
            App.unblockUI(el);
            $("#sp_names").trigger('change');
        });
    });

    //Get the code of a stored procedure
    $("#sp_names").change(function (event) {

         $("#sp_dao_name").val(sprintf("%sDAO", $(this).val()));

        var el = $(this).closest(".portlet").children(".portlet-body");
        App.blockUI(el);
        $.get("/database/sp_code?sp_name=" + $(this).val() + "&db_name=" + $("#sp_databases").val(), function (data) {
            data = JSON.parse(data);
            ace.edit("sp_editor").setValue(data);
            App.unblockUI(el);
        });
    });

    //Move selected fields to query selection
    $("button.btn.move").click(function () {

        $.each($('#left_select').find(":selected"), function (index, value) {
            $("#right_select").append(value);
            //$("#left_select").removeOption(index);
        });
    });

    //Move all fields as query selection
    $("button.btn.moveall").click(function () {
        $("#right_select").html($("#left_select").html());
        $("#left_select").html("");
    });

    $("button.btn.remove").click(function () {
        $.each($('#right_select').find(":selected"), function (index, value) {
            $("#left_select").append(value);
            //$("#right_select").remove(value);
        });
    });

    $("button.btn.removeall").click(function () {
        $("#left_select").html($("#right_select").html());
        $("#right_select").html("");
    });

    //Show or hide something according to current selected information
    $(".btn-group > button.btn-primary").click(function () {
        var btn_type = $(this).attr("btn_type");
        switch(btn_type){
            case "select":
                $("#select_fields").show();
                $("#where_fields").show();
                $("#cud_type").hide();
                break;
            case "insert":
            case "update":
            case "delete":
                $("#cud_type > button[btn_type='spa']").trigger('click');
                $("#select_fields").hide();
                $("#where_fields").hide();
                $("#cud_type").show();
                break;
            case "spa":
            case "sp3":
                $("#select_fields").hide();
                $("#where_fields").hide();
                break;
            case "sql":
                var current_action = $("#crud_action > button.active").attr("btn_type");
                if(current_action == "select"){
                    $("#select_fields").show();
                    $("#where_fields").show();
                }else if(current_action == "insert"){
                    $("#select_fields").show();
                    $("#where_fields").hide();
                }else if(current_action == "update"){
                    $("#select_fields").show();
                    $("#where_fields").show();
                }else{
                    $("#select_fields").hide();
                    $("#where_fields").show()();
                }
                
                break;
        }
    });

    //Add the sql to current project
    $("#add_task").click(function () {
        var project_id = $("#proj_id").attr("project");

        var post_data = {};

        var task_object = {};

        var task_type = $("li.active > a[data-toggle='tab']").attr("task_type");

        if (task_type == "autosql") {

            function pack_sql_params(){
                var fields = [];

                $("#right_select > option").each(function () {
                    fields.push(this.value);
                });

                task_object["fields"] = fields;

                var where_condition = {};

                // $("#where_condition > li > .task-config > .task-config-btn > .dropdown-menu  > li > a[value!='-1'] > i.icon-ok").each(function () {
                //     var condition = $(this).parent().attr("value");
                //     var field = $(this).parent().parent().parent().parent().parent().parent().find(".task-title > .task-title-sp").text();
                //     where_condition[field] = condition;
                // });

                $("a[value!='-1'] > .icon-check").each(function(){
                    var condition = $(this).parent().attr("value");
                    var field = $(this).closest("div[class='task-config']").prev().children().text();
                    where_condition[field] = condition;
                });

                task_object["where"] = where_condition;

                task_object["field_type"] = filedTypeMap;
            };

            task_object["dao_name"] = $("#auto_dao_name").val();

            task_object["func_name"] = $("#auto_func_name").val();

            task_object["database"] = $("#databases").val();

            task_object["table"] = $("#tables").val();

            task_object["crud"] = $("#crud_action > button.active").attr("btn_type");

            switch(task_object["crud"]){
                case "select":
                    pack_sql_params();
                    break;
                case "insert":
                case "update":
                case "delete": {
                        task_object["cud"] = $("#cud_type > button.active").attr("btn_type");
                        if(task_object["cud"] == "sql"){
                            pack_sql_params();
                        }else{
                            if(task_object["crud"] == "insert"){
                                task_object["param_count"] = $("#left_select option").length
                                + $("#right_select option").length - 1;
                            }else{
                                task_object["param_count"] = 1;
                            }
                        }
                    }
                    break;
            }
        } else if (task_type == "sp") {

            task_object["dao_name"] = $("#sp_dao_name").val();

            task_object["func_name"] = $("#sp_func_name").val();

            task_object["database"] = $("#sp_databases").val();

            task_object["sp_name"] = $("#sp_names").val();

        } else {

            task_object["dao_name"] = $("#sql_dao_name").val();

            task_object["func_name"] = $("#sql_func_name").val();

            task_object["database"] = $("#sql_databases").val();

            task_object["sql"] = editor.getValue();
        }

        post_data["project_id"] = project_id;
        post_data["task_type"] = task_type;
        post_data["task_object"] = JSON.stringify(task_object);

        $.post("/task/add", post_data, function (data) {
            $("#reload_tasks").trigger('click');
        });

    });
    
    //Get all tasks of the project
    $("#reload_tasks").click(function () {
        var el = $(this).closest(".portlet").children(".portlet-body");
        App.blockUI(el);
        $.get("/task/tasks?project_id=" + $("#proj_id").attr("project"), function (data) {

            data = JSON.parse(data);

            var suffix = '</div><div class="task-config"><div class="task-config-btn btn-group"><a class="btn mini blue" href="#" data-toggle="dropdown" data-hover="dropdown" data-close-others="true">More <i class="icon-angle-down"></i></a><ul class="dropdown-menu pull-right"><li><a href="#"><i class="icon-twitter"></i> To another project</a></li><li><a href="#"><i class="icon-pencil"></i> Edit</a></li><li><a href="#"><i class="icon-trash"></i> Delete</a></li></ul></div></div>';

            var html_data = "";
            $.each(data, function (index, value) {
                var meaningful = sprintf("USE %s ", value.database);

                if (value.task_type == "autosql") {

                    var where_meaningful = "";

                    if(value.where != undefined){
                        where_meaningful = " WHERE ";
                        for(var key in value.where){
                            where_meaningful = sprintf("%s %s %s ? AND "
                                , where_meaningful
                                , key
                                , whereKeyValuemap[value.where[key]]);
                        }
                        if(where_meaningful.substring(where_meaningful.length-4, where_meaningful.length) == "AND "){
                            where_meaningful= where_meaningful.substring(0, where_meaningful.length - 4);
                        }
                    }

                    switch(value.crud){
                        case "select":
                            meaningful = sprintf("%s SELECT %s FROM %s %s"
                                ,meaningful
                                ,value.fields.join(",")
                                ,value.table
                                ,where_meaningful);
                            break;
                        case "insert":
                            if(value.cud == "sql"){
                                var place_holder = "";
                                for(var i=0;i<value.fields.length;i++){
                                    place_holder = sprintf("%s ?,", place_holder);
                                }
                                place_holder = place_holder.substring(0, place_holder.length-1);
                                meaningful = sprintf("%s INSERT INTO %s (%s) VALUES (%s)"
                                    ,meaningful
                                    ,value.table
                                    ,value.fields.join(",")
                                    ,place_holder);
                            }else{
                                meaningful = sprintf("%s EXEC %s_%s_i"
                                    ,meaningful
                                    ,value.cud
                                    ,value.table);
                            }
                            break;
                        case "update":
                            if(value.cud == "sql"){
                                meaningful = sprintf("%s UDPATE %s SET %s %s"
                                    ,meaningful
                                    ,value.table
                                    ,sprintf("%s = ?",value.fields.join(" = ?, "))
                                    ,where_meaningful);
                            }else{
                                meaningful = sprintf("%s EXEC %s_%s_u"
                                    ,meaningful
                                    ,value.cud
                                    ,value.table);
                            }
                            break;
                        case "delete":
                            if(value.cud == "sql"){
                                meaningful = sprintf("%s DELETE FROM %s %s"
                                    ,meaningful
                                    ,value.table
                                    ,where_meaningful);
                            }else{
                                meaningful = sprintf("%s EXEC %s_%s_d"
                                    ,meaningful
                                    ,value.cud
                                    ,value.table);
                            }
                            break;
                    }

                } else if (value.task_type == "sp") {

                    meaningful += " EXEC " + value.sp_name;

                } else {

                    meaningful += " " + value.sql;
                }

                html_data += '<li><div class="task-title"><span id="' + value._id + '" class="task-title-sp">' + meaningful + '</span>' + suffix + '</li>';
            });

            $("#all_tasks").html(html_data);

            $(".icon-trash").each(function(){
                $(this).parent().bind('click', function(event){
                    if (confirm("Are you sure?")) {
                        var id = $(this).closest('div[class="task-config"]').prev().children().attr("id");
                        $.get("/task/delete?task_id="+id, function(data){
                            $("#reload_tasks").trigger('click');
                        });
                    }
                });
            });

            App.unblockUI(el);

        });
    });

    $('#reload_ops').click(function(){
        $.get("/database/databases", function(data){
            data = JSON.parse(data);

            $.each(data, function(index, value){
                $('#databases').append($('<option>', { 
                    value: value,
                    text : value
                }));
                $('#sp_databases').append($('<option>', { 
                    value: value,
                    text : value
                }));
                $('#sql_databases').append($('<option>', { 
                    value: value,
                    text : value
                }));
            });
        });
    });
    
    //Generate code of current project according to the language selection
    $("#generate_code").click(function () {
        var post_data = {};
        var project_id = $("#proj_id").attr("project");
        post_data["project_id"] = project_id;

        var el = $(document.body);
        App.blockUI(el);
        $.post("/file/generate", post_data, function (data) {
            App.unblockUI(el);
            window.location.replace("/file/");
        });
    });

    $("#save_sp").click(function(){
        var db_name = $("#sp_databases").val();
        var sp_code = sp_editor.getValue();

        var post_data = {};

        post_data["db_name"] = db_name;
        post_data["sp_code"] = JSON.stringify(sp_code);

        $.post("/database/save_sp", post_data, function (data) {
            $("#sp_databases").trigger('change');
        });
    });

    $(".icon-refresh").trigger('click');

});