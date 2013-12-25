$(document).ready(function () {

    var whereKeyValuemap = {};

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

    var user_change = true;

    //Get all tables/views of a selected database
    $("#databases").change(function (event, callback) {
        if (event.originalEvent) {
            user_change = true;
        }
        var el = $(this).closest(".portlet").children(".portlet-body");
        App.blockUI(el);
        $.get("/rest/daogen/db/tables?db_name=" + $(this).val(), function (data) {
            //data = JSON.parse(data);
            var html_data = "";
            $.each(data.ids, function (index, value) {
                html_data += "<option>" + value + "</option>";
            });
            $("#tables").html(html_data);
            App.unblockUI(el);

            if (user_change) {
                $("#tables").trigger('change');
            }

            if (typeof callback == "function") {
                callback.apply($(this));
            }
        });
    });

    //Get all fields of a selected table/view
    $("#tables").change(function (event, callback) {

        $("#auto_dao_name").val(sprintf("%sDAO", $(this).val()));

        var el = $(this).closest(".portlet").children(".portlet-body");
        App.blockUI(el);

        var url = sprintf("/rest/daogen/db/fields?table_name=%s&db_name=%s", $(this).val(), $("#databases").val());

        $.get(url, function (data) {
            //data = JSON.parse(data);
            var html_data = "";
            var operator = "<div class='span6' style='float: right;'><select style='height:30px;' class='span6'><option value='-1'>Null</option><option value='0'>=</option><option value='1'>!=</option><option value='2'>></option><option value='3'><</option><option value='4'>>=</option><option value='5'><=</option><option value='6'>Between</option><option value='7'>Like</option><option value='8'>In</option></select></div>";
            var where_condition = '';

            var localFieldTypeMap = {};

            $.each(data, function (index, value) {

                var indexedMark = "";
                var indexedText = "";
                var primaryMark = "";
                var primaryText = "";
                if(value.indexed){
                    indexedMark = "*";
                    indexedText = "(indexed)";
                }
                if(value.primary){
                    primaryMark = "+";
                    primaryText = "(Primary)";
                }

                html_data = sprintf("%s<option value='%s'>%s%s%s</option>", 
                    html_data, value.name, value.name, indexedMark, primaryMark);
                where_condition = sprintf(
                        "%s<li><div class='task-title'><span style='float: left;' class='task-title-sp' real_name='%s'>%s%s%s</span>%s</div></li>", where_condition, value.name, value.name,indexedText,primaryText, operator);

            });

            $("#left_select").html(html_data);
            $("#where_condition").html(where_condition);

            App.unblockUI(el);

            if (typeof callback == "function") {
                callback.apply($(this));
            }

        });
    });

    //Get all stored procedures of a selected database
    $("#sp_databases").change(function (event, callback) {

        if (event.originalEvent) {
            user_change = true;
        }

        var el = $(this).closest(".portlet").children(".portlet-body");
        App.blockUI(el);
        $.get("/rest/daogen/db/sps?db_name=" + $(this).val(), function (data) {
            //data = JSON.parse(data);
            $.each(data.ids, function (index, value) {
                $('#sp_names').append($('<option>', {
                    value: value,
                    text: value
                }));
            });
            App.unblockUI(el);

            if (user_change) {
                $("#sp_names").trigger('change');
            }

            if (typeof callback == "function") {
                callback.apply($(this));
            }

        });
    });

    //Get the code of a stored procedure
    $("#sp_names").change(function (event) {

        $("#sp_dao_name").val(sprintf("%sDAO", $(this).val()));

        var el = $(this).closest(".portlet").children(".portlet-body");
        App.blockUI(el);
        // $.get("/rest/daogen/db/sp_code?sp_name=" + $(this).val() + "&db_name=" + $("#sp_databases").val(), function (data) {
        //     //data = JSON.parse(data);
        //     ace.edit("sp_editor").setValue(data);
        //     App.unblockUI(el);
        // });
        $.ajax({
            type: "GET",
            url: "/rest/daogen/db/sp_code?sp_name=" + $(this).val() + "&db_name=" + $("#sp_databases").val(),
            headers: {
                "Range": 'bytes=0-3200'
            }
        }).done(function (data) {
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

        $("#left_select option").each(function () {
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
        $("#right_select option").each(function () {
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

        // var post_data = {};

        var task_object = {};

        var task_type = $("li.active > a[data-toggle='tab']").attr("task_type");

        if (task_type == "autosql") {

            function pack_sql_params() {
                var fields = [];

                $("#right_select > option").each(function () {
                    fields.push(this.value);
                });

                //task_object["fields"] = fields;
                task_object["fields"] = JSON.stringify(fields);

                var where_condition = "";

                var selected_condition = $("#where_condition > li > div > div > select > option[value!='-1']:selected");

                var where_fields = {};

                if ($(selected_condition).length > 0) {
                    where_condition = "WHERE ";

                    $(selected_condition).each(function () {
                        var condition = $(this).val();
                        var field = $(this).closest("div[class='task-title']").children(".task-title-sp").attr("real_name");

                        where_fields[field] = condition;

                        var current_where_clause = "";
                        //means BETWEEN
                        if (condition == 6) {
                            current_where_clause = sprintf("BETWEEN @%s_start AND @%s_end", field, field);
                        } else {
                            current_where_clause = sprintf("%s @%s", whereKeyValuemap[condition], field);
                        }

                        where_condition = sprintf(" %s %s %s AND", where_condition, field, current_where_clause);
                    });

                    if (where_condition.substring(where_condition.length - 3, where_condition.length) == "AND") {
                        where_condition = where_condition.substring(0, where_condition.length - 3);
                    }
                }

                task_object["condition"] = JSON.stringify(where_fields);

                switch (task_object["crud"]) {
                case "select":
                    task_object["sql_spname"] = sprintf("SELECT %s FROM %s %s", fields.join(","), task_object["table"], where_condition);
                    break;
                case "insert":
                    var place_holder = [];
                    for (var i = 0; i < fields.length; i++) {
                        place_holder.push(sprintf("@%s", fields[i]));
                    }
                    task_object["sql_spname"] = sprintf("INSERT INTO %s (%s) VALUES (%s)", task_object["table"], fields.join(","), place_holder.join(","));
                    break;
                case "update":
                    var place_holder = [];
                    for (var i = 0; i < fields.length; i++) {
                        place_holder.push(sprintf("%s = @%s", fields[i], fields[i]));
                    }
                    task_object["sql_spname"] = sprintf("UPDATE %s SET %s %s", task_object["table"], place_holder.join(","), where_condition);
                    break;
                case "delete":
                    task_object["sql_spname"] = sprintf("DELETE FROM %s %s", task_object["table"], where_condition);
                    break;
                }

            };

            function pack_sp_params() {
                var fields = [];
                $("#left_select > option").each(function () {
                    fields.push(this.value);
                });

                $("#right_select > option").each(function () {
                    fields.push(this.value);
                });

                task_object["fields"] = JSON.stringify(fields);
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
                    task_object["sql_spname"] = sprintf("%s_%s_%s", task_object["cud"], task_object["table"], cud_shortcut[task_object["crud"]]);
                    pack_sp_params();
                }
            }

        } else if (task_type == "sp") {
            task_object["func_name"] = $("#sp_func_name").val();

            task_object["database"] = $("#sp_databases").val();

            task_object["sql_spname"] = $("#sp_names").val();

            task_object["crud"] = $("#sp_action").val();
        } else {
            task_object["dao_name"] = $("#sql_dao_name").val();

            task_object["func_name"] = $("#sql_func_name").val();

            task_object["database"] = $("#sql_databases").val();

            task_object["sql_spname"] = editor.getValue().replace(/\n/g, " ");
        }

        task_object["project_id"] = project_id;
        task_object["task_type"] = task_type;
        task_object["action"] = "insert";

        $.post("/rest/daogen/task", task_object, function (data) {
            $("#reload_tasks").trigger('click');
        });

    });

    //Get all tasks of the project
    $("#reload_tasks").click(function () {
        // return;

        var id = $("#proj_id").attr("project");

        if(undefined == id || id == ""){
            return;
        }

        $.get("/rest/daogen/project/project?id="+id, function(data){

            $("#project_desc").html(data.name + "对应的DAO");

        });

        if ($('#main_area').children().length > 0) {
            $('#dao_tasks').dataTable().fnClearTable();
        }

        var el = $(this).closest(".portlet").children(".portlet-body");
        App.blockUI(el);
        $.get("/rest/daogen/task?project_id=" + id, function (data) {

            //data = JSON.parse(data);

            $.each(data, function (index, value) {
                var dao_name = "";
                var func_name = "";

                $.data(document.body, value._id.$oid, value);

                if (value.dao_name != undefined) {
                    dao_name = value.dao_name;
                } else {
                    if (value.task_type == "sp") {
                        dao_name = sprintf("%sSPDAO", value.database);
                    } else {
                        dao_name = sprintf("%sDAO", value.table);
                    }
                }

                if (value.func_name != undefined) {
                    func_name = value.func_name;
                } else {
                    func_name = value.sql_spname.substring(
                        value.sql_spname.indexOf(".") + 1, value.sql_spname.length);
                }

                var title = "";
                if (value.task_type == "sp") {
                    title = sprintf("EXEC %s", value.sql_spname);
                } else {
                    title = value.sql_spname;
                }

                $('#dao_tasks').dataTable().fnAddData(
                    [value.database, dao_name, func_name,
                        sprintf("<a rel='tooltip' title='%s' class='btn'><i class='icon-question'></i></a>&nbsp;<button type='button' class='btn btn-success modify'>修改</button><input type='hidden' value='%s'>&nbsp;<button type='button' class='btn btn-danger delete'>删除</button>", title, value._id.$oid)
                    ]
                );
            });

            $(".delete.btn-danger").click(function () {
                if (confirm("Are you sure?")) {
                    var id = $(this).prev().val();
                    var post_data = {};
                    post_data["action"] = "delete";
                    post_data["id"] = id;
                    $.post("/rest/daogen/task", post_data, function (data) {
                        $("#reload_tasks").trigger('click');
                    });
                }
            });

            $(".modify.btn-success").click(function () {
                if (confirm("Are you sure?")) {
                    var id = $(this).next().val();
                    var value = $.data(document.body, id);

                    var current_tab = $("#task_tab > ul > li > a[task_type='" + value.task_type + "']").parent();

                    if (!$(current_tab).hasClass("active")) {
                        //$("#task_tab > ul > li.active").toggleClass("active");
                        //$(current_tab).addClass("active");
                        $(current_tab).children().trigger('click');
                    }

                    switch (value.task_type) {
                    case "autosql":
                        $("#databases").val(value.database);
                        $("#auto_func_name").val(value.func_name);
                        user_change = false;
                        $("#databases").trigger('change', function () {
                            $("#tables").val(value.table);
                            $("#tables").trigger('change', function () {
                                $("#right_select").html("");
                                if (undefined != value.fields) {
                                    $.each(value.fields, function (index, field) {
                                        $("#right_select").append($(sprintf("#left_select option[value='%s']", field)));
                                    });
                                }

                                if (undefined != value.condition) {
                                    $.each(value.condition, function (name, value) {
                                        $(sprintf(".task-title-sp[real_name='%s']", name)).next().children().val(value);
                                    });
                                }

                            });
                        });
                        break;
                    case "sp":
                        $("#sp_databases").val(value.database);
                        $("#sp_action").val(value.sp_action);
                        user_change = false;
                        $("#sp_databases").trigger('change', function () {
                            $("#sp_names").val(value.sql_spname);
                            $("#sp_names").trigger('change');
                        });
                        break;
                    case "freesql":
                        $("#sql_databases").val(value.database);
                        $("#sql_dao_name").val(value.dao_name);
                        $("#sql_func_name").val(value.func_name);
                        editor.setValue(value.sql);
                        break;
                    }


                }
            });

            App.unblockUI(el);

        });
    });

    $('#reload_ops').click(function () {
        $.get("/rest/daogen/db/dbs", function (data) {
            //data = JSON.parse(data);

            $.each(data.ids, function (index, value) {
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
        post_data["language"] = $("#gen_language").val();

        var el = $(document.body);
        App.blockUI(el);
        $.post("/rest/daogen/project/generate", post_data, function (data) {
            App.unblockUI(el);
            window.location.replace("/daogen/file.html");
        });
    });

    $("#save_sp").click(function () {
        var db_name = $("#sp_databases").val();
        var sp_code = sp_editor.getValue();

        var post_data = {};

        post_data["db_name"] = db_name;
        post_data["sp_code"] = JSON.stringify(sp_code);

        $.post("/database/save_sp", post_data, function (data) {
            user_change = true;
            $("#sp_databases").trigger('change');
        });
    });



    $('#dao_tasks').dataTable({
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

    $("#daogen").toggleClass('open');

    $("#daogen .sub-menu").show();

    $("#proj_id").attr("project", getUrlVar("project_id"));

    $(".icon-refresh").trigger('click');

});

var getUrlVar = function (key) {
    var result = new RegExp(key + "=([^&]*)", "i").exec(window.location.search);
    var returnResult =  result && unescape(result[1]) || "";
    if(undefined == returnResult || returnResult == ""){
        alert("没有找到对应的Project，请从\"我的项目\"中的\"对应DAO\"Drill Through到此处！");
        window.location.href = "/daogen/project.html";
    }
    return returnResult;
};