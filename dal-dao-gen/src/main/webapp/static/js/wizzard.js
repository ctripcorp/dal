//向导注释
//step1
//step2
//step3-1 -> step3-2
(function (window, undefined) {

    var wizzard = function () {

    };

    var variableHtml = '<div class="row-fluid"><input type="text" class="span3" value="%s">';
    var variable_typesHtml = '<select %s class="span5">' 
    + "<option value='_please_select'>--参数类型--</option>" 
    + "<option value='-7'>Bit----Boolean</option>"  
    + "<option value='-6'>TinyInt----Byte</option>" 
    + "<option value='5'>SmallInt----Short</option>" 
    + "<option value='4'>Integer----int</option>" 
    + "<option value='-5'>BigInt----long</option>" 
    +  "<option value='7'>Real----Float</option>" 
    + "<option value='8'>Double</option>" 
    + "<option value='3'>Decimal</option>" 
    + "<option value='1'>Char</option>" 
    + "<option value='12'>Varchar----String</option>" 
    + "<option value='-1'>LongVarchar----String</option>" 
    + "<option value='-15'>Nchar----String</option>" 
    + "<option value='-9'>NVarchar----String</option>" 
    + "<option value='-16'>LongNVarchar/String</option>" 
    + "<option value='91'>Date</option>" 
    + "<option value='92'>Time----TimeSpan</option>" 
    + "<option value='93'>Timestamp----DateTime</option>"  
    +   "</select></div><br>";

    wizzard.prototype = {

        next: function (current) {
            //首先获取当前Grid选中的行,records是id数组
            var records = w2ui['grid'].getSelection();
            var record = null;
            if (records.length > 0)
                record = w2ui['grid'].get(records[0]);

            //向导首先显示所有数据库服务器，点击下一步后，获取此服务器所有的数据库列表 
            if (current.hasClass("step1")) {

                if ($("#servers").val() == "") {
                    $("#error_msg").text("请选择或者添加一个服务器！");
                    return;
                }

                $("#error_msg").text("");

                //首先蒙板化整个body,在后续get成功或者失败时，取消蒙板化
                cblock($("body"));

                if ($("#databases")[0] != undefined && $("#databases")[0].selectize != undefined) {
                    $("#databases")[0].selectize.clearOptions();
                } else {
                    $("#databases").selectize({
                        //maxItems: null,
                        valueField: 'id',
                        labelField: 'title',
                        searchField: 'title',
                        options: [],
                        create: false
                    });
                }

                $.get("/rest/db/dbs?server=" + $("#servers").val() + "&rand=" + Math.random()).done(function (data) {
                    var results = [];
                    $.each(data, function (index, value) {
                        results.push({
                            id: value,
                            title: value
                        });
                    });
                    $("#databases")[0].selectize.addOption(results);
                    $("#databases")[0].selectize.refreshOptions(false);

                    if ($("#page1").attr('is_update') == "1") {
                        $("#databases")[0].selectize.setValue(record.db_name);
                        var parentToActive = $(sprintf(".gen_style > input[value='%s']", record.task_type)).parent();
                        if (!parentToActive.hasClass("active")) {
                            $(".gen_style.active").removeClass("active");
                            parentToActive.addClass("active");
                        }
                    } else if (data.length > 0) {
                        $("#databases")[0].selectize.setValue(data[0]);
                        var defaultActive = $(".gen_style > input[value='table_view_sp']").parent();
                        if (!defaultActive.hasClass("active")) {
                            $(".gen_style.active").removeClass("active");
                            defaultActive.addClass("active");
                        }
                    }

                    current.hide();
                    $(".step2").show();
                    $("body").unblock();
                }).fail(function (data) {
                    $("#error_msg").text("获取数据库列表失败，请上一步后重试！");
                    $("body").unblock();
                });
            }
            //选择数据库之后，选择自动生成、存储过程或者自己写查询
            else if (current.hasClass("step2")) {

                if ($("#databases").val() == "") {
                    $("#error_msg").text("请选择一个数据库！");
                    return;
                }
                $("#error_msg").text("");

                var gen_style = $(".gen_style.active").children().val();
                switch (gen_style) {
                case "table_view_sp":

                    cblock($("body"));
                    $.get(sprintf("/rest/db/table_sps?server=%s&db_name=%s&rand=%s",
                        $("#servers").val(), $("#databases").val(), Math.random())).done(function (data) {
                        $("select[id$=table_list] > option").remove();
                        $("select[id$=view_list] > option").remove();
                        $("select[id$=sp_list] > option").remove();
                        var tableList = [];
                        var viewList = [];
                        var spList = [];
                        $.each(data.tables, function (index, value) {
                            tableList.push($('<option>', {
                                value: value,
                                text: value
                            }));
                        });
                        $.each(data.views, function (index, value) {
                            viewList.push($('<option>', {
                                value: value,
                                text: value
                            }));
                        });
                        $.each(data.sps, function (index, value) {
                            spList.push($('<option>', {
                                value: value.schema + "." + value.name,
                                text: value.schema + "." + value.name
                            }));
                        });
                        $("#table_list").append(tableList).multipleSelect("refresh");
                        $("#view_list").append(viewList).multipleSelect("refresh");
                        $("#sp_list").append(spList).multipleSelect("refresh");

                        var currentOption = $("#servers")[0].selectize.options[$("#servers").val()];
                        if (currentOption.serverType == "mysql") {
                            $("#cud_by_sp").attr("checked", false);
                            $(".mysql_hide").hide();
                        } else {
                            $("#cud_by_sp").attr("checked", true);
                            $(".mysql_hide").show();
                        }

                        if ($("#page1").attr('is_update') == "1" && record != undefined && record.task_type == "table_view_sp") {
                            if (record.table_names != undefined)
                                $('#table_list').multipleSelect('setSelects', record.table_names.split(","));
                            if (record.view_names != undefined)
                                $('#view_list').multipleSelect('setSelects', record.view_names.split(","));
                            if (record.sp_names != undefined)
                                $('#sp_list').multipleSelect('setSelects', record.sp_names.split(","));
                        }
                        current.hide();
                        $("#suffix").val("Gen");
                        $(".step3-1").show();
                        $("body").unblock();
                    }).fail(function (data) {
                        $("body").unblock();
                    });
                    break;
                case "auto":
                    //在显示下一页之前，清空下一页的信息
                    if ($("#tables")[0] != undefined && $("#tables")[0].selectize != undefined) {
                        $("#tables")[0].selectize.clearOptions();
                    } else {
                        $("#tables").selectize({
                            //maxItems: null,
                            valueField: 'id',
                            labelField: 'title',
                            searchField: 'title',
                            options: [],
                            create: false
                        });
                    }
                    $("#method_name").val("");
                    var defaultActive = $(".op_type > input[value='select']").parent();
                    if (!defaultActive.hasClass("active")) {
                        $(".op_type.active").removeClass("active");
                        defaultActive.addClass("active");
                    }

                    cblock($("body"));
                    $.get(
                        sprintf("/rest/db/tables?server=%s&db_name=%s&rand=%s",
                            $("#servers").val(),
                            $("#databases").val(), Math.random()), function (data) {
                            var results = [];
                            $.each(data, function (index, value) {
                                results.push({
                                    id: value,
                                    title: value
                                });
                            });
                            $("#tables")[0].selectize.addOption(results);
                            $("#tables")[0].selectize.refreshOptions(false);
                            if ($("#page1").attr('is_update') == "1" && record != undefined && record.task_type == "auto") {
                                $("#tables")[0].selectize.setValue(record.table_name);
                                $("#method_name").val(record.method_name);
                                var parentToActive = $(sprintf(".op_type > input[value='%s']", record.crud_type)).parent();
                                if (!parentToActive.hasClass("active")) {
                                    $(".op_type.active").removeClass("active");
                                    parentToActive.addClass("active");
                                }
                            } else {
                                if (data.length > 0) {
                                    $("#tables")[0].selectize.setValue(data[0]);
                                }
                            }
                            current.hide();
                            $(".step3-2").show();
                            $("body").unblock();
                        });

                    break;
                case "sql":
                    $("#sql_editor").height(200);
                    var editor = ace.edit("sql_editor");
                    editor.setTheme("ace/theme/monokai");
                    editor.getSession().setMode("ace/mode/sql");

                    if ($("#sql_class_name")[0] != undefined && $("#sql_class_name")[0].selectize != undefined) {
                        $("#sql_class_name")[0].selectize.clearOptions();
                    } else {
                        $("#sql_class_name").selectize({
                            //maxItems: null,
                            valueField: 'value',
                            labelField: 'title',
                            searchField: 'title',
                            options: [],
                            create: true
                        });
                    }
                    if ($("#sql_pojo_name")[0] != undefined && $("#sql_pojo_name")[0].selectize != undefined) {
                        $("#sql_pojo_name")[0].selectize.clearOptions();
                    } else {
                        $("#sql_pojo_name").selectize({
                            //maxItems: null,
                            valueField: 'value',
                            labelField: 'title',
                            searchField: 'title',
                            options: [],
                            create: true
                        });
                    }

                    //在显示下一页之前，清空下一页的信息
                    $("#sql_class_name").val("");
                    $("#sql_pojo_name").val("");
                    $("#sql_method_name").val("");

                    $.get("/rest/task/sql_class?project_id=" + w2ui['grid'].current_project + "&server_id=" + $("#servers").val() + "&db_name=" + $("#databases").val() + "&rand=" + Math.random(), function (data) {

                        var update = $("#page1").attr('is_update') == "1" 
                            && record != undefined 
                            && record.task_type == "sql";
                        var clazz = [];
                        var pojos = [];
                        $.each(data.classes, function (index, value) {
                            clazz.push({
                                value: value,
                                title: value
                            });
                        });
                        $.each(data.pojos, function (index, value) {
                            pojos.push({
                                value: value,
                                title: value
                            });
                        });
                        $("#sql_class_name")[0].selectize.addOption(clazz);
                        $("#sql_pojo_name")[0].selectize.addOption(pojos);

                        $("#sql_class_name")[0].selectize.refreshOptions(false);
                        $("#sql_pojo_name")[0].selectize.refreshOptions(false);

                        if (update) {
                            $("#sql_class_name")[0].selectize.setValue(record.class_name);
                            $("#sql_pojo_name")[0].selectize.setValue(record.pojo_name);
                            $("#sql_method_name").val(record.method_name);
                            editor.setValue(record.sql_content);
                        } else {
                            editor.setValue("");
                        }

                    }).fail(function (data) {

                    });
                    
                    current.hide();
                    $(".step3-3").show();
                    break;
                }
            }
            //选择基础数据
            else if (current.hasClass("step3-2")) {

                if ($("#tables").val() == "") {
                    $("#error_msg").text("请选择一个表！");
                    return;
                }
                if ($("#method_name").val() == "") {
                    $("#error_msg").text("请填写方法名！");
                    return;
                }
                $("#error_msg").text("");

                var op_type = $(".op_type.active").children().val();
                cblock($("body"));

                $("select[id$=fields] > option").remove();

                $("select[id$=selected_condition] > option").remove();

                $("select[id$=conditions] > option:gt(0)").remove();

                var url = sprintf(
                    "/rest/db/fields?server=%s&table_name=%s&db_name=%s&rand=%s",
                    $("#servers").val(),
                    $("#tables").val(),
                    $("#databases").val(), Math.random());

                $.get(url, function (data) {
                    var fieldList = [];
                    $.each(data, function (index, value) {
                        fieldList.push($('<option>', {
                            value: value.name,
                            text: sprintf("%s%s%s",
                                value.name, value.indexed ? "*" : "",
                                value.primary ? "+" : "")
                        }));

                        $("#conditions").append($('<option>', {
                            value: value.name,
                            text: value.name
                        }));
                    });
                    $("#fields").append(fieldList).multipleSelect("refresh");
                    if ($("#page1").attr('is_update') == "1") {
                        $('#fields').multipleSelect('setSelects', record.fields.split(","));
                        if (record.condition != undefined && record.condition != "") {
                            var selectedConditions = record.condition.split(",");
                            $.each(selectedConditions, function (index, value) {
                                $("#selected_condition").append($('<option>', {
                                    value: value,
                                    text: sprintf("%s %s", value.split('_')[0],
                                        $(sprintf("#condition_values > option[value='%s']",
                                            value.split('_')[1])).text())
                                }));
                            });
                        }
                    }
                    current.hide();

                    $(".step3-2-1").show();
                    if (op_type == "select") {
                        $(".step3-2-1-1").show();
                        $(".step3-2-1-2").show();
                    } else if (op_type == "update") {
                        $(".step3-2-1-1").show();
                        $(".step3-2-1-2").show();
                    } else if (op_type == "insert") {
                        $(".step3-2-1-1").show();
                        $(".step3-2-1-2").hide();
                    } else {
                        $(".step3-2-1-1").hide();
                        $(".step3-2-1-2").show();
                    }
                    $("body").unblock();
                });

            } else if (current.hasClass("step3-2-1")) {
                if ($(".op_type.active").children().val() != "delete" && $('#fields').multipleSelect('getSelects').length < 1) {
                    $("#error_msg").text("请选择至少一个字段！");
                    return;
                }
                $("#error_msg").text("");
                //$(".step_fields").hide();
                current.hide();
                $(".step3-2-2").show();
            } else if (current.hasClass("step3-1")) {
                window.ajaxutil.post_task();
            } else if (current.hasClass("step3-3")) {
                if ($("#page1").attr('is_update') == "1") {
                    var splitedParams = record.parameters.split(";");
                    var htmls = "";
                    var i = 0;
                     var id_values = {};
                    $.each(splitedParams, function (index, value) {
                        if (value != "") {
                            var resultParams = value.split(",");
                            htmls = htmls + sprintf(variableHtml, resultParams[0]) + sprintf(variable_typesHtml, sprintf("id='db_type_%s'", ++i));
                             id_values["db_type_"+i] = resultParams[1];
                        }

                    });

                    if (htmls.length == 0) {
                        window.ajaxutil.post_task();
                        return;
                    }

                    $("#param_list").html(htmls);
                     $.each(id_values, function(key, value){
                        $("#"+key).val(value);
                    });

                } else {
                    //首先解析Sql语句，提取出参数
                    var regexIndex = /(\?{1})/igm;
                    var regexNames = /[@:](\w+)/igm;
                    var sqlContent = ace.edit("sql_editor").getValue(),
                        result;
                    var htmls = "";
                    var i = 0;
                    while ((result = regexIndex.exec(sqlContent))) {
                        htmls = htmls + sprintf(variableHtml, sprintf("param%s", ++i)) + variable_typesHtml;
                    }
                    if (htmls.length == 0) {
                        while ((result = regexNames.exec(sqlContent))) {
                            htmls = htmls + sprintf(variableHtml, result[1]) + variable_typesHtml;
                        }
                    }

                    if (htmls.length == 0) {
                        window.ajaxutil.post_task();
                        return;
                    }

                    $("#param_list").html(htmls);
                }

                current.hide();
                $(".step3-3-1").show();
            } else if (current.hasClass("step3-2-2")) {
                window.ajaxutil.post_task();
            } else if (current.hasClass("step3-3-1")) {
                window.ajaxutil.post_task();
            }
        },
        previous: function (current) {
            $("#error_msg").text("");
            if (current.hasClass("step1")) {
                return;
            }
            current.hide();

            if (current.hasClass("step2")) {
                $(".step1").show();
            } else if (current.hasClass("step3-1") || 　current.hasClass("step3-2") || 　current.hasClass("step3-3")) {
                $(".step2").show();
            } else if (current.hasClass("step3-2-1")) {
                $(".step3-2").show();
            } else if (current.hasClass("step3-2-2")) {
                $(".step3-2-1").show();
            } else if (current.hasClass("step3-3-1")) {
                $(".step3-3").show();
            }
        }
    };

    window.wizzard = new wizzard();

})(window);