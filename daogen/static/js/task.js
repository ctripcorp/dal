$(document).ready(function () {

    var whereKeyValuemap = {};

    var filedTypeMap = {};

    var cud_shortcut = {};

    whereKeyValuemap["0"] = "=";
    whereKeyValuemap["1"] = "!=";
    whereKeyValuemap["2"] = ">";
    whereKeyValuemap["3"] = "<";
    whereKeyValuemap["4"] = ">=";
    whereKeyValuemap["5"] = "<=";
    whereKeyValuemap["6"] = "Between";
    whereKeyValuemap["7"] = "Like";
    whereKeyValuemap["8"] = "In";

    cud_shortcut["insert"] = "i";
    cud_shortcut["update"] = "u";
    cud_shortcut["delete"] = "d";

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

            $(".icon-check-empty, .icon-check").each(function () {
                $(this).parent().bind('click', function (event) {
                    var current_el = $(this).children().get(0);
                    if (!$(current_el).hasClass(".icon-check")) {
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
            $.each(data, function (index, value) {
                $('#sp_names').append($('<option>', {
                    value: value,
                    text: value
                }));
            });
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

        $("#left_select option").each(function(){
            $("#right_select").append($(this));
        });

        // $("#right_select").html($("#left_select").html());
        // $("#left_select").html("");
    });

    $("button.btn.remove").click(function () {
        $.each($('#right_select').find(":selected"), function (index, value) {
            $("#left_select").append(value);
            //$("#right_select").remove(value);
        });
    });

    $("button.btn.removeall").click(function () {
         $("#right_select option").each(function(){
            $("#left_select").append($(this));
        });
        // $("#left_select").html($("#right_select").html());
        // $("#right_select").html("");
    });

    //Show or hide something according to current selected information
    $(".btn-group > button.btn-primary").click(function () {
        var btn_type = $(this).attr("btn_type");
        switch (btn_type) {
        case "select":
            $("#select_fields").show();
            $("#where_fields").show();
            $("#cud_type").hide();
            $("#auto_func_name").val("get");
            break;
        case "insert":
            $("#auto_func_name").val("insert");
            $("#cud_type > button[btn_type='spa']").trigger('click');
            $("#select_fields").hide();
            $("#where_fields").hide();
            $("#cud_type").show();
            break;
        case "update":
            $("#auto_func_name").val("set");
            $("#cud_type > button[btn_type='spa']").trigger('click');
            $("#select_fields").hide();
            $("#where_fields").hide();
            $("#cud_type").show();
            break;
        case "delete":
            $("#auto_func_name").val("delete");
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
            if (current_action == "select") {
                $("#select_fields").show();
                $("#where_fields").show();
            } else if (current_action == "insert") {
                $("#select_fields").show();
                $("#where_fields").hide();
            } else if (current_action == "update") {
                $("#select_fields").show();
                $("#where_fields").show();
            } else {
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

            function pack_sql_params() {
                var fields = [];

                $("#right_select > option").each(function () {
                    fields.push(this.value);
                });

                var where_condition = "";

                var selected_condition = $("a[value!='-1'] > .icon-check");

                if ($(selected_condition).length > 0) {
                    where_condition = "WHERE ";

                    $("a[value!='-1'] > .icon-check").each(function () {
                        var condition = $(this).parent().attr("value");
                        var field = $(this).closest("div[class='task-config']").prev().children().text();

                        var current_where_clause = "";
                        //means BETWEEN
                        if (condition == 6) {
                            current_where_clause = "BETWEEN ? AND ?";
                        } else {
                            current_where_clause = sprintf("%s ?", whereKeyValuemap[condition]);
                        }

                        where_condition = sprintf(" %s %s %s AND", where_condition, field, current_where_clause);
                    });

                    if (where_condition.substring(where_condition.length - 3, where_condition.length) == "AND") {
                        where_condition = where_condition.substring(0, where_condition.length - 3);
                    }
                }

                task_object["field_type"] = filedTypeMap;

                switch (task_object["crud"]) {
                case "select":
                    task_object["sql"] = sprintf("SELECT %s FROM %s %s", fields.join(","), task_object["table"], where_condition);
                    break;
                case "insert":
                    var place_holder = [];
                    for (var i = 0; i < fields.length; i++) {
                        place_holder.push("?");
                    }
                    task_object["sql"] = sprintf("INSERT INTO %s (%s) VALUES (%s)", task_object["table"], fields.join(","), place_holder.join(","));
                    break;
                case "update":
                    task_object["sql"] = sprintf("UPDATE %s SET %s %s", task_object["table"], sprintf("%s = ?", fields.join(" = ?, ")), where_condition);
                    break;
                case "delete":
                    task_object["sql"] = sprintf("DELETE FROM %s %s", task_object["table"], where_condition);
                    break;
                }

            };

            task_object["func_name"] = $("#auto_func_name").val();

            task_object["database"] = $("#databases").val();

            task_object["table"] = $("#tables").val();

            task_object["crud"] = $("#crud_action > button.active").attr("btn_type");

            if (task_object["crud"] == "select") {
                pack_sql_params();
            } else {
                task_object["cud"] = $("#cud_type > button.active").attr("btn_type");
                if (task_object["cud"] == "sql") {
                    pack_sql_params();
                } else {
                    task_object["sp_name"] = sprintf("%s_%s_%s", task_object["cud"], task_object["table"], cud_shortcut[task_object["crud"]]);
                }
            }

        } else if (task_type == "sp") {
            task_object["func_name"] = $("#sp_func_name").val();

            task_object["database"] = $("#sp_databases").val();

            task_object["sp_name"] = $("#sp_names").val();

            task_object["sp_action"] = $("#sp_action").val();
        } else {
            task_object["dao_name"] = $("#sql_dao_name").val();

            task_object["func_name"] = $("#sql_func_name").val();

            task_object["database"] = $("#sql_databases").val();

            task_object["sql"] = editor.getValue().replace(/\n/g, " ");
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
                var result_sql = sprintf("USE %s ", value.database);

                if (value.task_type == "autosql") {
                    if (value.crud == "select" || value.cud == "sql") {
                        result_sql = sprintf("%s %s", result_sql, value.sql);
                    } else {
                        result_sql = sprintf("%s EXEC %s", result_sql, value.sp_name);
                    }
                } else if (value.task_type == "sp") {
                    result_sql += " EXEC " + value.sp_name;
                } else {
                    result_sql += " " + value.sql;
                }

                html_data += '<li><div class="task-title"><span id="' + value._id + '" class="task-title-sp">' + result_sql + '</span>' + suffix + '</li>';
            });

            $("#all_tasks").html(html_data);

            $(".icon-trash").each(function () {
                $(this).parent().bind('click', function (event) {
                    if (confirm("Are you sure?")) {
                        var id = $(this).closest('div[class="task-config"]').prev().children().attr("id");
                        $.get("/task/delete?task_id=" + id, function (data) {
                            $("#reload_tasks").trigger('click');
                        });
                    }
                });
            });

            App.unblockUI(el);

        });
    });

    $('#reload_ops').click(function () {
        $.get("/database/databases", function (data) {
            data = JSON.parse(data);
            $.each(data, function (index, value) {
                $('#databases').append($('<option>', {
                    value: value,
                    text: value
                }));
                $('#sp_databases').append($('<option>', {
                    value: value,
                    text: value
                }));
                $('#sql_databases').append($('<option>', {
                    value: value,
                    text: value
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

    $("#save_sp").click(function () {
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