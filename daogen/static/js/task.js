var filedTypeMap = {};

jQuery(document).ready(function () {

    App.init(); // initlayout and core plugins

    // Tasks.initDashboardWidget();

    //collapse or expand the portlet
    jQuery('body').on('click', '.portlet > .portlet-title > .tools > .icon-collapse, .portlet .portlet-title > .tools > .icon-collapse-top', function (e) {
        // e.preventDefault();
        if (jQuery(this).hasClass("icon-collapse")) {
            jQuery(this).removeClass("icon-collapse").addClass("icon-collapse-top");
        } else {
            jQuery(this).removeClass("icon-collapse-top").addClass("icon-collapse");
        }
    });

    //initialize the sql editor
    var editor = ace.edit("sql_editor");
    editor.setTheme("ace/theme/monokai");
    editor.getSession().setMode("ace/mode/mysql");

    //initialize the stored procedure editor
    var sp_editor = ace.edit("sp_editor");
    sp_editor.setTheme("ace/theme/monokai");
    sp_editor.getSession().setMode("ace/mode/mysql");

    $("#databases").change(function (event) {
        var el = jQuery(this).closest(".portlet").children(".portlet-body");
        App.blockUI(el);
        $.get("/metadata?meta_type=tables&meta_value=" + $(this).val(), function (data) {
            data = JSON.parse(data);
            var html_data = "";
            $.each(data, function (index, value) {
                html_data += "<option>" + value + "</option>";
            });
            $("#tables").html(html_data);
            table_change();
            //App.unblockUI(el);
        });
    });

    $("#tables").change(function (event) {
        var el = jQuery(this).closest(".portlet").children(".portlet-body");
        App.blockUI(el);
        table_change();
    });

    $("#sp_databases").change(function (event) {
        var el = jQuery(this).closest(".portlet").children(".portlet-body");
        App.blockUI(el);
        $.get("/metadata?meta_type=sp&meta_value=" + $(this).val(), function (data) {
            data = JSON.parse(data);
            var html_data = "";
            $.each(data, function (index, value) {
                html_data += "<option>" + value + "</option>";
            });
            $("#sp_names").html(html_data);
            sp_change();
            //App.unblockUI(el);
        });
    });

    $("#sp_names").change(function (event) {
        var el = jQuery(this).closest(".portlet").children(".portlet-body");
        App.blockUI(el);
        sp_change();
    });

    $("button.btn.move").click(function () {

        $.each($('#left_select').find(":selected"), function (index, value) {
            $("#right_select").append(value);
            //$("#left_select").removeOption(index);
        });
    });

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

    $("button.btn-primary").click(function () {
        var btn_type = $(this).attr("btn_type");
        if (btn_type == "select") {
            $("#select_fields").show();
            $("#where_fields").show();
        } else if (btn_type == "insert") {
            $("#select_fields").show();
            $("#where_fields").hide();
        } else if (btn_type == "update") {
            $("#select_fields").show();
            $("#where_fields").show();
        } else if (btn_type == "delete") {
            $("#select_fields").hide();
            $("#where_fields").show();
        }
    });

    $("#add_task").click(function () {
        var project_id = $("#proj_id").attr("project");

        var post_data = {};

        var task_object = {};

        var task_type = $("li.active > a[data-toggle='tab']").attr("task_type");

        task_object["func_name"] = $("#func_name").val();

        if (task_type == "autosql") {

            task_object["database"] = $("#databases").val();

            task_object["table"] = $("#tables").val();

            task_object["crud"] = $("button.btn-primary").attr("btn_type");

            var fields = [];

            $("#right_select > option").each(function () {
                fields.push(this.value);
            });

            task_object["fields"] = fields;

            var where_condition = {};

            $("#where_condition > li > .task-config > .task-config-btn > .dropdown-menu  > li > a[value!='-1'] > i.icon-ok").each(function () {
                var condition = $(this).parent().attr("value");
                var field = $(this).parent().parent().parent().parent().parent().parent().find(".task-title > .task-title-sp").text();
                where_condition[field] = condition;
            });

            task_object["where"] = where_condition;

            task_object["field_type"] = filedTypeMap;

        } else if (task_type == "sp") {

            task_object["database"] = $("#sp_databases").val();

            task_object["sp_name"] = $("#sp_names").val();

        } else {
            task_object["database"] = $("#sql_databases").val();

            task_object["sql"] = editor.getValue();
        }

        post_data["project_id"] = project_id;
        post_data["task_type"] = task_type;
        post_data["task_object"] = JSON.stringify(task_object);

        $.post("/task", post_data, function (data) {
            getTasks();
        });

    });

    $("#reload_tasks").click(function () {
        getTasks();
    });

    $("#generate_code").click(function () {
        var post_data = {};
        var project_id = $("#proj_id").attr("project");
        post_data["project_id"] = project_id;

        var el = $(document.body);
        App.blockUI(el);
        $.post("/generate", post_data, function (data) {
            App.unblockUI(el);
            window.location.replace("/file");
        });
    });

    getTasks();

});

var table_change = function () {
    var event_obj = $("#tables");
    var el = jQuery(event_obj).closest(".portlet").children(".portlet-body");

    var url = sprintf("/metadata?meta_type=fields&meta_value=%s&db_name=%s", $(event_obj).val(), $("#databases").val());

    $.get(url, function (data) {
        data = JSON.parse(data);
        var html_data = "";
        var operator = '<div class="task-config"><div class="task-config-btn btn-group"><a class="btn mini blue" href="#" data-toggle="dropdown" data-hover="dropdown" data-close-others="true">Operator<i class="icon-angle-down"></i></a><ul class="dropdown-menu pull-right"><li><a href="#" value="-1"><i class="icon-ok"></i> None</a></li><li><a href="#" value="0"><i></i> Equal</a></li><li><a href="#" value="1"><i></i> Not Equal</a></li><li><a href="#" value="2"><i></i> Greater Than</a></li><li><a href="#" value="3"><i></i> Less Than</a></li><li><a href="#" value="4"><i></i> Greater Equal Than</a></li><li><a href="#" value="5"><i></i> Less Equal Than</a></li><li><a href="#" value="6"><i></i> Between</a></li><li><a href="#" value="7"><i></i> Like</a></li><li><a href="#" value="8"><i></i> In</a></li></ul></div></div>';
        var where_condition = '';
        $.each(data, function (index, value) {

            filedTypeMap[value.name] = value.type;

            html_data += "<option>" + value.name + "</option>";

            where_condition = sprintf(
                "%s<li><div class='task-title'><span class='task-title-sp'>%s</span></div>%s</li>", where_condition, value.name, operator);
        });
        $("#left_select").html(html_data);
        $("#where_condition").html(where_condition);

        $("#where_condition > li > .task-config > .task-config-btn > .dropdown-menu  > li > a").click(function () {
            $(this).parent().parent().find("li > a > i.icon-ok").toggleClass("icon-ok");
            $(this).find("i").toggleClass("icon-ok");
        });

        // $(".multiselect").multiselect();
        App.unblockUI(el);
    });

};

var getTasks = function () {
    var el = $("#reload_tasks").closest(".portlet").children(".portlet-body");
    App.blockUI(el);
    $.get("/task/tasks?project_id=" + $("#proj_id").attr("project"), function (data) {

        data = JSON.parse(data);

        var suffix = '</div><div class="task-config"><div class="task-config-btn btn-group"><a class="btn mini blue" href="#" data-toggle="dropdown" data-hover="dropdown" data-close-others="true">More <i class="icon-angle-down"></i></a><ul class="dropdown-menu pull-right"><li><a href="#"><i class="icon-twitter"></i> To another project</a></li><li><a href="#"><i class="icon-pencil"></i> Edit</a></li><li><a href="#"><i class="icon-trash"></i> Delete</a></li></ul></div></div>';

        var html_data = "";
        $.each(data, function (index, value) {
            var meaningful = "USE " + value.database;

            if (value.task_type == "autosql") {
                if (value.crud == "select") {
                    meaningful += " SELECT " + value.fields.join(",") +
                        " FROM " + value.table;
                } else if (value.crud == "insert") {
                    meaningful += " INSERT INTO " +
                        value.table;
                } else if (value.crud == "update") {
                    meaningful += " UPDATE " + value.table;
                } else {
                    meaningful += " DELETE FROM " + value.table;
                }
            } else if (value.task_type == "sp") {
                meaningful += " EXEC " + value.sp_name;
            } else {
                meaningful += " " + value.sql;
            }

            html_data += '<li><div class="task-title"><span id="' + value._id + '" class="task-title-sp">' + meaningful + '</span>' + suffix + '</li>';
        });

        $("#all_tasks").html(html_data);

        App.unblockUI(el);

    });

};

var sp_change = function () {
    var event_obj = $("#sp_names");
    var el = jQuery(event_obj).closest(".portlet").children(".portlet-body");
    $.get("/metadata?meta_type=sp_code&meta_value=" + $(event_obj).val() + "&db_name=" + $("#sp_databases").val(), function (data) {
        data = JSON.parse(data);
        ace.edit("sp_editor").setValue(data);
        App.unblockUI(el);
    });
};

jQuery.fn.multiselect = function () {
    $(this).each(function () {
        var checkboxes = $(this).find("input:checkbox");
        checkboxes.each(function () {
            var checkbox = $(this);
            // Highlight pre-selected checkboxes
            if (checkbox.attr("checked"))
                checkbox.parent().addClass("multiselect-on");

            // Highlight checkboxes that the user selects
            checkbox.click(function () {
                // if (checkbox.attr("checked"))
                //     checkbox.parent().addClass("multiselect-on");
                // else
                //     checkbox.parent().removeClass("multiselect-on");
            });
        });
    });
};