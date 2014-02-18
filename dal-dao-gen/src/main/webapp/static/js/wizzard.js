(function (window, undefined) {

    var wizzard = function () {

    };

    wizzard.prototype = {

        next: function (current) {
            //首先获取当前Grid选中的行,records是id数组
            var records = w2ui['grid'].getSelection();
            var record = null;
            if (records.length > 0)
                record = w2ui['grid'].get(records[0]);

            //向导首先显示所有数据库服务器，点击下一步后，获取此服务器所有的数据库列表 
            if (current.hasClass("step0")) {
                //首先蒙板化整个body,在后续get成功或者失败时，取消蒙板化
                cblock($("body"));

                $("select[id$=databases] > option:gt(0)").remove();

                $.get("/rest/db/dbs?server=" + $("#servers").val()).done(function (data) {
                    $.each(data, function (index, value) {
                        $("#databases").append($('<option>', {
                            value: value,
                            text: value
                        }));
                    });
                    if ($("#page1").attr('is_update') == "1") {
                        $("#databases").val(record.db_name);
                    }
                    current.hide();
                    $(".step1").show();
                    $("body").unblock();
                }).fail(function (data) {
                    $("body").unblock();
                });
            }
            //选择数据库之后，选择自动生成、存储过程或者自己写查询
            else if (current.hasClass("step1")) {
                //在显示下一页之前，清空下一页的信息
                var defaultActive = $(".gen_style > input[value='auto']").parent();
                if (!defaultActive.hasClass("active")) {
                    $(".gen_style.active").removeClass("active");
                    defaultActive.addClass("active");
                }

                if ($("#page1").attr('is_update') == "1") {
                    var parentToActive = $(sprintf(".gen_style > input[value='%s']", record.task_type)).parent();
                    if (!parentToActive.hasClass("active")) {
                        $(".gen_style.active").removeClass("active");
                        parentToActive.addClass("active");
                    }
                }
                current.hide();
                $(".step2").show();
            }
            //最为复杂的一步
            else if (current.hasClass("step2")) {
                var gen_style = $(".gen_style.active").children().val();
                switch (gen_style) {
                case "auto":
                    //在显示下一页之前，清空下一页的信息
                    $("select[id$=tables] > option:gt(0)").remove();
                    $("#only_template").prop("checked", true);
                    $("#cud_by_sp").prop("checked", true);
                    $("#class_name").val("");
                    $("#method_name").val("");
                    var defaultActive = $(".op_type > input[value='select']").parent();
                    if (!defaultActive.hasClass("active")) {
                        $(".op_type.active").removeClass("active");
                        defaultActive.addClass("active");
                    }
                    
                    $(".op_type_class").hide();
                    $(".method_name_class").hide();

                    if ($("#page1").attr('is_update') == "1" && record != undefined && record.task_type == "auto") {
                        $('#tables').append($('<option>', {
                            value: record.table_name,
                            text: record.table_name
                        }));
                        $("#tables").val(record.table_name);
                        $("#class_name").val(record.class_name);
                        $("#cud_by_sp").prop('checked', record.sql_type == "spa_sp3");
                        if (record.method_name == undefined || record.method_name == "") {
                            $("#only_template").prop('checked', true);
                        } else {
                            $("#only_template").prop('checked', false);
                            $(".op_type_class").show();
                            $(".method_name_class").show();
                            $("#method_name").val(record.method_name);
                            var parentToActive = $(sprintf(".op_type > input[value='%s']", record.crud_type)).parent();
                            if (!parentToActive.hasClass("active")) {
                                $(".op_type.active").removeClass("active");
                                parentToActive.addClass("active");
                            }
                        }
                        current.hide();
                        $(".step2-1-1").show();
                    } else {
                        cblock($("body"));
                        $.get(
                            sprintf("/rest/db/tables?server=%s&db_name=%s",
                                $("#servers").val(),
                                $("#databases").val()), function (data) {
                                $.each(data, function (index, value) {
                                    $('#tables').append($('<option>', {
                                        value: value,
                                        text: value
                                    }));
                                });
                                current.hide();
                                $(".step2-1-1").show();
                                $("body").unblock();
                            });
                    }
                    break;
                case "sp":
                    $("select[id$=sps] > option:gt(0)").remove();
                    if ($("#page1").attr('is_update') == "1" && record != undefined && record.task_type == "sp") {
                        $('#sps').append($('<option>', {
                            value: sprintf("%s.%s", record.sp_schema, record.sp_name),
                            text: sprintf("%s.%s", record.sp_schema, record.sp_name)
                        }));
                        $("#sps").val(sprintf("%s.%s", record.sp_schema, record.sp_name));

                        $("#sp_type").val(record.crud_type);
                        current.hide();
                        $(".step2-2").show();
                    } else {
                        cblock($("body"));

                        $.get(sprintf("/rest/db/sps?server=%s&db_name=%s",
                            $("#servers").val(),
                            $("#databases").val()), function (data) {
                            $.each(data, function (index, value) {
                                $('#sps').append($('<option>', {
                                    value: value,
                                    text: value
                                }));
                            });
                            current.hide();
                            $(".step2-2").show();
                            $("body").unblock();
                        });
                    }
                    break;
                case "sql":
                    $("#sql_editor").height(250);
                    var editor = ace.edit("sql_editor");
                    editor.setTheme("ace/theme/monokai");
                    editor.getSession().setMode("ace/mode/sql");

                    //在显示下一页之前，清空下一页的信息
                    $("#sql_class_name").val("");
                    $("#sql_method_name").val("");

                    if ($("#page1").attr('is_update') == "1" && record != undefined && record.task_type == "sql") {
                        $("#sql_class_name").val(record.class_name);
                        $("#sql_method_name").val(record.method_name);
                        editor.setValue(record.sql_content);
                    } else {
                        editor.setValue("SELECT * FROM Table");
                    }
                    current.hide();
                    $(".step2-3").show();
                    break;
                }
            }
            //选择基础数据
            else if (current.hasClass("step2-1-1")) {
                if ($("#only_template").is(":checked")) {
                    current.hide();
                    $(".step3").show();
                    $(".step3").attr('from', current.attr('class'));
                    return;
                }
                var op_type = $(".op_type.active").children().val();
                cblock($("body"));

                $("select[id$=fields_left] > option").remove();
                $("select[id$=fields_right] > option").remove();

                $("select[id$=selected_condition] > option").remove();

                $("select[id$=fields_condition] > option:gt(0)").remove();

                var url = sprintf(
                    "/rest/db/fields?server=%s&table_name=%s&db_name=%s",
                    $("#servers").val(),
                    $("#tables").val(),
                    $("#databases").val());

                $.get(url, function (data) {
                    $.each(data, function (index, value) {
                        $("#fields_left").append($('<option>', {
                            value: value.name,
                            text: sprintf("%s%s%s",
                                value.name, value.indexed ? "*" : "",
                                value.primary ? "+" : "")
                        }));
                        $("#fields_condition").append($('<option>', {
                            value: value.name,
                            text: value.name
                        }));
                    });
                    if ($("#page1").attr('is_update') == "1") {
                        var selectedFields = record.fields.split(",");
                        $.each(selectedFields, function (index, value) {
                            $("#fields_right").append(
                                $(sprintf("#fields_left > option[value='%s']", value)));
                        });
                        var selectedConditions = record.condition.split(",");
                        $.each(selectedConditions, function (index, value) {
                            $("#selected_condition").append($('<option>', {
                                value: value,
                                text: sprintf("%s %s", value.split('_')[0],
                                    $(sprintf("#condition_values > option[value='%s']", value.split('_')[1])).text())
                            }));
                        });
                    }
                    current.hide();
                    $("body").unblock();
                });

                if (op_type == "select") {
                    $(".step2-1-3").show();
                    $(".step2-1-3-add").show();
                } else {
                    if ($("#cud_by_sp").is(":checked")) {
                        $(".step3").show();
                        $(".step3").attr('from', current.attr('class'));
                    } else {
                        if (op_type == "update") {
                            $(".step2-1-3").show();
                            $(".step2-1-3-add").show();
                        } else if (op_type == "insert") {
                            $(".step2-1-3").show();
                            $(".step2-1-3-add").hide();
                        } else {
                            $(".step2-1-3").hide();
                            $(".step2-1-3-add").show();
                        }
                    }
                }
            } else if (current.hasClass("step2-1-3") || current.hasClass("step2-1-3-add")) {
                current.hide();
                $(".step3").show();
                $(".step3").attr('from', current.attr('class'));
            } else if (current.hasClass("step2-2") || current.hasClass("step2-3")) {
                current.hide();
                $(".step3").show();
                $(".step3").attr('from', current.attr('class'));
            } else if (current.hasClass("step3")) {
                var postData = {};
                var current_project = w2ui['grid'].current_project;
                if (current_project == undefined) {
                    if (w2ui['sidebar'].nodes.length < 1 || w2ui['sidebar'].nodes[0].nodes.length < 1)
                        return;
                    current_project = w2ui['sidebar'].nodes[0].nodes[0].id;
                }

                postData["project_id"] = current_project;
                postData["db_name"] = $("#databases").val();
                if ($("#page1").attr('is_update') == "1") {
                    postData["action"] = "update";
                    var records = w2ui['grid'].getSelection();
                    var record = w2ui['grid'].get(records[0]);
                    postData["id"] = record.id;
                } else {
                    postData["action"] = "insert";
                }
                postData["server"] = $("#servers").val();

                if ($(".gen_style.active").children().val() == "auto") {
                    postData["task_type"] = "auto";
                    postData["table_name"] = $("#tables").val();
                    postData["class_name"] = $("#class_name").val();
                    postData["method_name"] = $("#method_name").val();
                    //C#风格或者Java风格，@Name or ?
                    postData["sql_style"] = $("#sql_style").val();
                    postData["sql_type"] = $("#cud_by_sp").is(":checked") ? "spa_sp3" : "sql";
                    postData["crud_type"] = $(".op_type.active").children().val();;
                    var selectedFields = [];
                    var selectedConditions = [];

                    $.each($("#fields_right option"), function (index, value) {
                        selectedFields.push($(value).val());
                    });
                    $.each($("#selected_condition option"), function (index, value) {
                        selectedConditions.push($(value).val());
                    });

                    postData["fields"] = selectedFields.join(",");
                    postData["condition"] = selectedConditions.join(",");
                } else if ($(".gen_style.active").children().val() == "sp") {
                    postData["task_type"] = "sp";
                    postData["sql_style"] = $("#sql_style").val();
                    postData["crud_type"] = $("#sp_type").val();
                    postData["sp_name"] = $("#sps").val();
                } else if ($(".gen_style.active").children().val() == "sql") {
                    postData["task_type"] = "sql";
                    postData["class_name"] = $("#sql_class_name").val();
                    postData["method_name"] = $("#sql_method_name").val();
                    postData["crud_type"] = "select";
                    postData["sql_content"] = ace.edit("sql_editor").getValue();
                    postData["params"] = $.makeArray($("#selected_variable>option").map(function() { return $(this).val(); })).join(",");
                }

                $.post("/rest/task", postData, function (data) {
                    $("#page1").modal('hide');
                    w2ui["grid_toolbar"].click('refreshDAO', null);
                });
            }
        },
        previous: function (current) {
            if (current.hasClass("step0")) {
                return;
            }
            current.hide();
            var from_class = current.attr("from");
            if (from_class != undefined && from_class != "") {
                var classes = from_class.split(" ").join(".");
                $("." + classes).show();
                return;
            }

            if (current.hasClass("step1")) {
                $(".step0").show();
            } else if (current.hasClass("step2")) {
                $(".step1").show();
            } else if (current.hasClass("step2-1-1")) {
                $(".step2").show();
            } else if (current.hasClass("step2-1-3") || current.hasClass("step2-1-3-add")) {
                $(".step2-1-1").show();
            } else if (current.hasClass("step2-2")) {
                $(".step2").show();
            } else if (current.hasClass("step2-3")) {
                $(".step2").show();
            }
        }
    };

    window.wizzard = new wizzard();

})(window);