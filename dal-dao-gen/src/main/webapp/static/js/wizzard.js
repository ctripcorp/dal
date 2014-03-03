
(function (window, undefined) {

    var wizzard = function () {

    };

    var variableHtml = '<div class="row-fluid"><input type="text" class="span3" value="%s">';
    var variable_typesHtml = '<select %s class="span3">'
                              +"<option value='_please_select'>--参数类型--</option>"
                              +"<option value='-7'>Bit</option>"
                              +"<option value='16'>Boolean</option>"
                              +"<option value='-6'>TinyInt</option>"
                              +"<option value='5'>SmallInt</option>"
                              +"<option value='4'>Integer</option>"
                              +"<option value='-5'>BigInt</option>"
                              +"<option value='6'>Float</option>"
                              +"<option value='7'>Real</option>"
                              +"<option value='8'>Double</option>"
                              +"<option value='2'>Numeric</option>"
                              +"<option value='3'>Decimal</option>"
                              +"<option value='1'>Char</option>"
                              +"<option value='12'>Varchar</option>"
                              +"<option value='-1'>LongVarchar</option>"
                              +"<option value='-15'>Nchar</option>"
                              +"<option value='-9'>NVarchar</option>"
                              +"<option value='-16'>LongNVarchar</option>"
                              +"<option value='91'>Date</option>"
                              +"<option value='92'>Time</option>"
                              +"<option value='93'>Timestamp</option>"
                              +"<option value='-2'>Binary</option>"
                              +"<option value='-3'>Varbinary</option>"
                              +"<option value='-4'>LongVarbinary</option>"
                              +"<option value='0'>Null</option>"
                              +"<option value='1111'>Other</option>"
                              +"<option value='2000'>JavaObject</option>"
                              +"<option value='2001'>Distinct</option>"
                              +"<option value='2002'>Struct</option>"
                              +"<option value='2003'>Array</option>"
                              +"<option value='2004'>Blob</option>"
                              +"<option value='2005'>Clob</option>"
                              +"<option value='2006'>Ref</option>"
                              +"<option value='70'>DataLink</option>"
                              +"<option value='-8'>Rowid</option>"
                              +"<option value='2011'>NClob</option>"
                              +"<option value='2009'>SqlXml</option>"
                           +"</select>";
    var variable_valuesHtml = '<input type="text" class="span4" value="%s"></div><br>';

    wizzard.prototype = {

        next: function (current) {
            //首先获取当前Grid选中的行,records是id数组
            var records = w2ui['grid'].getSelection();
            var record = null;
            if (records.length > 0)
                record = w2ui['grid'].get(records[0]);

            //向导首先显示所有数据库服务器，点击下一步后，获取此服务器所有的数据库列表 
            if (current.hasClass("step0")) {

                if($("#servers").val() == "_please_select"){
                    $("#error_msg").text("请选择或者添加一个服务器！");
                    return;
                }

                $("#error_msg").text("");

                //首先蒙板化整个body,在后续get成功或者失败时，取消蒙板化
                cblock($("body"));

                $("select[id$=databases] > option:gt(0)").remove();

                $.get("/rest/db/dbs?server=" + $("#servers").val()).done(function (data) {
                    var results = [];
                    $.each(data, function (index, value) {
                        results.push($('<option>', {
                            value: value,
                            text: value
                        }));
                    });
                    $("#databases").append(results);

                    if ($("#page1").attr('is_update') == "1") {
                        $("#databases").val(record.db_name);
                    }else if(data.length > 0){
                        $("#databases").val(data[0]);
                    }
                    current.hide();
                    $(".step1").show();
                    $("body").unblock();
                }).fail(function (data) {
                    $("#error_msg").text("获取数据库列表失败，请单击上一步后重试！");
                    $("body").unblock();
                });
            }
            //选择数据库之后，选择自动生成、存储过程或者自己写查询
            else if (current.hasClass("step1")) {

                if($("#databases").val() == "_please_select"){
                    $("#error_msg").text("请选择一个数据库！");
                    return;
                }
                $("#error_msg").text("");

                //在显示下一页之前，清空下一页的信息
                if ($("#page1").attr('is_update') == "1") {
                    var parentToActive = $(sprintf(".gen_style > input[value='%s']", record.task_type)).parent();
                    if (!parentToActive.hasClass("active")) {
                        $(".gen_style.active").removeClass("active");
                        parentToActive.addClass("active");
                    }
                }else{
                    var defaultActive = $(".gen_style > input[value='table_view_sp']").parent();
                    if (!defaultActive.hasClass("active")) {
                        $(".gen_style.active").removeClass("active");
                        defaultActive.addClass("active");
                    }
                }
                current.hide();
                $(".step2").show();
            }
            //最为复杂的一步
            else if (current.hasClass("step2")) {
                var gen_style = $(".gen_style.active").children().val();
                switch (gen_style) {
                case "table_view_sp":
                    cblock($("body"));
                    $.get(sprintf("/rest/db/table_sps?server=%s&db_name=%s",
                        $("#servers").val(),$("#databases").val())).done(function (data) {
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
                        if ($("#page1").attr('is_update') == "1" 
                            && record != undefined 
                            && record.task_type == "table_view_sp") {
                            if(record.table_names != undefined)
                                $('#table_list').multipleSelect('setSelects', record.table_names.split(","));
                            if(record.view_names != undefined)
                                $('#view_list').multipleSelect('setSelects', record.view_names.split(","));
                            if(record.sp_names != undefined)
                                $('#sp_list').multipleSelect('setSelects', record.sp_names.split(","));
                        }
                        current.hide();
                        $(".step2-1-0").show();
                        $("body").unblock();
                    }).fail(function(data){
                        $("body").unblock();
                    });
                    break;
                case "auto":
                    //在显示下一页之前，清空下一页的信息
                    $("select[id$=tables] > option:gt(0)").remove();
                    $("#method_name").val("");
                    var defaultActive = $(".op_type > input[value='select']").parent();
                    if (!defaultActive.hasClass("active")) {
                        $(".op_type.active").removeClass("active");
                        defaultActive.addClass("active");
                    }
                    
                    if ($("#page1").attr('is_update') == "1" 
                        && record != undefined 
                        && record.task_type == "auto") {
                        $('#tables').append($('<option>', {
                            value: record.table_name,
                            text: record.table_name
                        }));
                        $("#tables").val(record.table_name);
                        $("#method_name").val(record.method_name);
                        var parentToActive = $(sprintf(".op_type > input[value='%s']", record.crud_type)).parent();
                        if (!parentToActive.hasClass("active")) {
                            $(".op_type.active").removeClass("active");
                            parentToActive.addClass("active");
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
                                if(data.length > 0){
                                    $("#tables").val(data[0]);
                                }
                                current.hide();
                                $(".step2-1-1").show();
                                $("body").unblock();
                            });
                    }
                    break;
                case "sql":
                    $("#sql_editor").height(200);
                    var editor = ace.edit("sql_editor");
                    editor.setTheme("ace/theme/monokai");
                    editor.getSession().setMode("ace/mode/sql");

                    $("select[id$=sql_class_name_select] > option:gt(0)").remove();
                    $("select[id$=sql_pojo_name_select] > option:gt(0)").remove();

                    $.get("/rest/task/sql_class?project_id="
                        +w2ui['grid'].current_project 
                        + "&server_id="+$("#servers").val()
                        + "&db_name="+$("#databases").val(), function(data){
                        $.each(data.classes, function(index, value){
                            $("#sql_class_name_select").append($('<option>', {
                                text: value, 
                                value: value
                            }));
                        });
                        $.each(data.pojos, function(index, value){
                            $("#sql_pojo_name_select").append($('<option>', {
                                text: value, 
                                value: value
                            }));
                        });
                    }).fail(function(data){

                    });

                    //在显示下一页之前，清空下一页的信息
                    $("#sql_class_name").val("");
                    $("#sql_pojo_name").val("");
                    $("#sql_method_name").val("");

                    if ($("#page1").attr('is_update') == "1" && record != undefined && record.task_type == "sql") {
                        $("#sql_class_name").val(record.class_name);
                        $("#sql_method_name").val(record.method_name);
                        editor.setValue(record.sql_content);
                    } else {
                        editor.setValue("");
                    }
                    current.hide();
                    $(".step2-3-1").show();
                    break;
                }
            }
            //选择基础数据
            else if (current.hasClass("step2-1-1")) {

                if($("#tables").val() == "_please_select"){
                    $("#error_msg").text("请选择一个表！");
                    return;
                }
                if($("#method_name").val() == ""){
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
                    "/rest/db/fields?server=%s&table_name=%s&db_name=%s",
                    $("#servers").val(),
                    $("#tables").val(),
                    $("#databases").val());

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
                        if(record.condition != undefined && record.condition != ""){
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
                    
                    $(".step_fields").show();
                    if (op_type == "select") {
                        $(".step2-1-2").show();
                        $(".step2-1-3").show();
                    } else if (op_type == "update") {
                        $(".step2-1-2").show();
                        $(".step2-1-3").show();
                    } else if (op_type == "insert") {
                        $(".step2-1-2").show();
                        $(".step2-1-3").hide();
                    } else {
                        $(".step2-1-2").hide();
                        $(".step2-1-3").show();
                    }
                    $("body").unblock();
                });
                
            } else if (current.hasClass("step_fields")) {
                if($("#operation_type").val() != "delete" && $('#fields').multipleSelect('getSelects').length < 1){
                    $("#error_msg").text("请选择至少一个字段！");
                    return;
                }
                $("#error_msg").text("");
                //$(".step_fields").hide();
                current.hide();
                $(".step3").show();
                $(".step3").attr('from', current.attr('class'));
            }else if (current.hasClass("step2-1-0")) {
                current.hide();
                $(".step3").show();
                $(".step3").attr('from', current.attr('class'));
            } else if(current.hasClass("step2-3-1")){
                if ($("#page1").attr('is_update') == "1") {
                    var splitedParams = record.parameters.split(",");
                    var htmls = "";
                    var i = 0;
                    var id_values = {};
                    $.each(splitedParams, function(index, value){
                        var resultParams = value.split("_");
                        htmls = htmls 
                         + sprintf(variableHtml, resultParams[0])
                         + sprintf(variable_typesHtml, sprintf("id='db_type_%s'", ++i))
                         + sprintf(variable_valuesHtml, resultParams[2]);
                        id_values["db_type_"+i] = resultParams[1];
                    });

                    if(htmls.length == 0){
                        current.hide();
                        $(".step3").show();
                        $(".step3").attr('from', current.attr('class'));
                        return;
                    }
                    
                    $("#param_list").html(htmls);
                    $.each(id_values, function(key, value){
                        $("#"+key).val(value);
                    });
                }else{
                    //首先解析Sql语句，提取出参数
                    var regexIndex = /(\?{1})/igm;
                    var regexNames = /[@:](\w+)/igm;
                    var sqlContent = ace.edit("sql_editor").getValue(), result;
                    var htmls = "";
                    var i = 0;
                    while((result = regexIndex.exec(sqlContent))){
                        htmls = htmls 
                        + sprintf(variableHtml, sprintf("param%s",++i))
                        + sprintf(variable_typesHtml, "") 
                        + sprintf(variable_valuesHtml, "");
                    }
                    if(htmls.length == 0){
                        while((result = regexNames.exec(sqlContent))){
                            htmls = htmls 
                            + sprintf(variableHtml, result[1]) 
                            + sprintf(variable_typesHtml, "") 
                            + sprintf(variable_valuesHtml, "");
                        }
                    }

                    if(htmls.length == 0){
                        current.hide();
                        $(".step3").show();
                        $(".step3").attr('from', current.attr('class'));
                        return;
                    }

                    $("#param_list").html(htmls);
                }
                
                current.hide();
                $(".step2-3-2").show();
            }
            else if (current.hasClass("step2-2")) {
                current.hide();
                $(".step3").show();
                $(".step3").attr('from', current.attr('class'));
            } else if (current.hasClass("step2-3-2")) {
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
                    postData["table_name"] = $("#tables").val();
                    postData["method_name"] = $("#method_name").val();
                    //C#风格或者Java风格，@Name or ?
                    postData["sql_style"] = $("#sql_style").val();
                    postData["crud_type"] = $(".op_type.active").children().val();;
                    var selectedConditions = [];

                    $.each($("#selected_condition option"), function (index, value) {
                        selectedConditions.push($(value).val());
                    });

                    postData["fields"] = $('#fields').multipleSelect('getSelects').join(",");
                    postData["condition"] = selectedConditions.join(",");

                    $.post("/rest/task/auto", postData, function (data) {
                        $("#page1").modal('hide');
                        w2ui["grid_toolbar"].click('refreshDAO', null);
                    });

                } else if ($(".gen_style.active").children().val() == "sql") {
                    if($("#sql_class_name").is(":visible")){
                        postData["class_name"] = $("#sql_class_name").val();    
                    }else{
                        postData["class_name"] = $("#sql_class_name_select").val();    
                    }
                    if($("#sql_pojo_name").is(":visible")){
                        postData["pojo_name"] = $("#sql_pojo_name").val();    
                    }else{
                        postData["pojo_name"] = $("#sql_pojo_name_select").val();    
                    }
                    
                    postData["method_name"] = $("#sql_method_name").val();
                    postData["crud_type"] = "select";
                    postData["sql_content"] = ace.edit("sql_editor").getValue();
                    var paramList = [];
                    $.each($("#param_list").children("div"), function(index, value){
                        var first = $(value).children("input").eq(0);
                        var second = $(value).children("select").eq(0);
                        var third = $(value).children("input").eq(1);
                        paramList.push(sprintf("%s_%s_%s", $(first).val(), $(second).val(), $(third).val()));
                    });
                    postData["params"] = paramList.join(",");

                    $.post("/rest/db/test_sql", postData).done(function(data){
                        if(data.code == "OK"){
                            $.post("/rest/task/sql", postData, function (data) {
                                $("#page1").modal('hide');
                                w2ui["grid_toolbar"].click('refreshDAO', null);
                            });
                        }else{
                            $("#error_msg").text("执行异常，请检查sql及对应参数！");
                        }
                    });
                }else if ($(".gen_style.active").children().val() == "table_view_sp") {
                    postData["table_names"] = $('#table_list').multipleSelect('getSelects').join(",");
                    postData["view_names"] = $('#view_list').multipleSelect('getSelects').join(",");
                    postData["sp_names"] = $('#sp_list').multipleSelect('getSelects').join(",");
                    postData["prefix"] = $("#prefix").val();
                    postData["suffix"] = $("#suffix").val();
                    postData["cud_by_sp"] = $("#cud_by_sp").is(":checked");
                    postData["pagination"] = $("#pagination").is(":checked");
                    $.post("/rest/task/table", postData, function (data) {
                        $("#page1").modal('hide');
                        w2ui["grid_toolbar"].click('refreshDAO', null);
                    });
                }
            }
        },
        previous: function (current) {
            $("#error_msg").text("");
            if (current.hasClass("step0")) {
                return;
            }
            current.hide();
            var from_class = current.attr("from");
            if (from_class != undefined && from_class != "") {
                // if(from_class.indexOf("step2-1-2") != -1 || from_class.indexOf("step2-1-3") != -1){
                //     $(".step_fields").show();
                // }
                var classes = from_class.split(" ").join(".");
                $("." + classes).show();
                return;
            }

            if (current.hasClass("step1")) {
                $(".step0").show();
            } else if (current.hasClass("step2")) {
                $(".step1").show();
            } else if (current.hasClass("step2-1-0")) {
                $(".step2").show();
            } else if (current.hasClass("step2-1-1")) {
                $(".step2").show();
            } else if (current.hasClass("step_fields")) {
                $(".step2-1-1").show();
            } else if (current.hasClass("step2-2")) {
                $(".step2").show();
            } else if (current.hasClass("step2-3-1")) {
                $(".step2").show();
            }else if (current.hasClass("step2-3-2")) {
                $(".step2-3-1").show();
            }
        }
    };

    window.wizzard = new wizzard();

})(window);