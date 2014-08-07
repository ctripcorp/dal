
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
        + "<option value='-16'>LongNVarchar----String</option>"
        + "<option value='91'>Date</option>"
        + "<option value='92'>Time----TimeSpan</option>"
        + "<option value='93'>Timestamp----DateTime</option>"
        +   "</select></div><br>";

    var step1 = function(record, current){
        if ($("#databases").val() == "") {
            $("#error_msg").text("请选择逻辑数据库！");
            return;
        }
        if($("#comment").val() == ""){
            $("#error_msg").text("请输入方法功能描述！");
            return;
        }
        $("#error_msg").text("");

        //首先蒙板化整个body,在后续get成功或者失败时，取消蒙板化
        var gen_style = $("#gen_style").val();
        switch (gen_style) {
            case "table_view_sp":
                step1_table_view_sp(record, current);
                break;
            case "auto":
                step1_auto(record, current);
                break;
            case "sql":
                step1_sql(record, current);
                break;
        }
    };

    var step1_table_view_sp = function(record, current){
        cblock($("body"));
        $("#next_step").attr("disabled","true");
        $("#next_step").text("正在加载...");
        $("#next_step").removeClass("btn-primary");
        $.get(sprintf("/rest/db/table_sps?db_name=%s&rand=%s",
                $("#databases").val(), Math.random())).done(function (retValue) {
                if(retValue.code!='OK'){
                    $("#error_msg").text(retValue.info);
                    $("#next_step").removeAttr("disabled");
                    $("#next_step").addClass("btn-primary");
                    $("#next_step").text("下一步");
                    $("body").unblock();
                    return;
                }
                var data = $.parseJSON(retValue.info);
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
                if(data.dbType == "MySQL"){
                    $("#cud_by_sp").prop("checked", false);
                    $(".mysql_hide").hide();
                }else{
                    $("#cud_by_sp").prop("checked", true);
                    $(".mysql_hide").show();
                }

                $("#table_list").append(tableList).multipleSelect({
                    "refresh": true,
                    onOpen: function() {
                        $(".step2-1").height(330);
                    },
                    onClose: function() {
                        $(".step2-1").height(245);
                    }
                });
                $("#view_list").append(viewList).multipleSelect({
                    "refresh": true,
                    onOpen: function() {
                        $(".step2-1").height(330);
                    },
                    onClose: function() {
                        $(".step2-1").height(245);
                    }
                });
                $("#sp_list").append(spList).multipleSelect({
                    "refresh": true,
                    onOpen: function() {
                        $(".step2-1").height(330);
                    },
                    onClose: function() {
                        $(".step2-1").height(245);
                    }
                });

                if ($("#page1").attr('is_update') == "1" && record != undefined && record.task_type == "table_view_sp") {
                    if (record.table_names != undefined){
                        $('#table_list').multipleSelect('setSelects', record.table_names.split(","));
                    }
                    if (record.view_names != undefined){
                        $('#view_list').multipleSelect('setSelects', record.view_names.split(","));
                    }
                    if (record.sp_names != undefined){
                        $('#sp_list').multipleSelect('setSelects', record.sp_names.split(","));
                    }
                    if(record.suffix != undefined){
                        $('#suffix').val(record.suffix);
                    }
                }
                current.hide();

                $(".step2-1").show();
                $("#next_step").removeAttr("disabled");
                $("#next_step").addClass("btn-primary");
                $("#next_step").text("下一步");
                $("body").unblock();
            }).fail(function (data) {
                $("#error_msg").text("获取表/视图列表失败, 请检查是否有权限, 或者数据库已被删除!");
                $("#next_step").removeAttr("disabled");
                $("#next_step").addClass("btn-primary");
                $("#next_step").text("下一步");
                $("body").unblock();
            });
    };

    var step1_auto = function(record, current){
        //在显示下一页之前，清空下一页的信息
        if ($("#tables")[0] != undefined && $("#tables")[0].selectize != undefined) {
            $("#tables")[0].selectize.clearOptions();
        } else {
            $("#tables").selectize({
                //maxItems: null,
                valueField: 'id',
                labelField: 'title',
                searchField: 'title',
                sortField: 'title',
                options: [],
                create: false
            });
        }

        $("#tables")[0].selectize.on('dropdown_open', function(dropdown){
            $(".step2-2").height(240);
        });

        $("#tables")[0].selectize.on('dropdown_close', function(dropdown){
            $(".step2-2").height(145);
        });

        cblock($("body"));
        $.get(
            sprintf("/rest/db/tables?db_name=%s&rand=%s",
                $("#databases").val(),
                Math.random()), function (data) {
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
                    $("#crud_option").val(record.crud_type);
                }
                current.hide();
                $(".step2-2").show();
                $("body").unblock();
            }).fail(function(data){
                 $("#error_msg").text("获取所有表失败!");
                $("body").unblock();
            });
        window.sql_builder.getDatabaseCategory();
    };

    var step1_sql = function(record, current){
        if ($("#sql_class_name")[0] != undefined && $("#sql_class_name")[0].selectize != undefined) {
            $("#sql_class_name")[0].selectize.clearOptions();
        } else {
            $("#sql_class_name").selectize({
                //maxItems: null,
                valueField: 'value',
                labelField: 'title',
                searchField: 'title',
                sortField: 'value',
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
                sortField: 'value',
                options: [],
                create: true
            });
        }
        //在显示下一页之前，清空下一页的信息
        $.get("/rest/task/sql_class?project_id="
            + w2ui['grid'].current_project
            + "&db_name=" + $("#databases").val()
            + "&rand=" + Math.random(), function (data) {
            var update = $("#page1").attr('is_update') == "1"
                && record != undefined
                && record.task_type == "sql";
            var clazz = [];
            var pojos = [{
                value: '简单类型',
                title: '简单类型'
            }];
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
                if(data.classes.length!=0){
                    $("#sql_class_name")[0].selectize.setValue(record['class_name']);
                }else{
                    $("#sql_class_name")[0].selectize.addOption({
                        value: record['class_name'],
                        title: record['class_name']
                    });
                    $("#sql_class_name")[0].selectize.setValue(record['class_name']);
                }
                if(data.pojos.length!=0){
                    $("#sql_pojo_name")[0].selectize.setValue(record['pojo_name']);
                }else{
                    $("#sql_pojo_name")[0].selectize.addOption({
                        value: record['pojo_name'],
                        title: record['pojo_name']
                    });
                    $("#sql_pojo_name")[0].selectize.setValue(record['pojo_name']);
                }

                $("#sql_method_name").val(record.method_name);
                var editor = ace.edit("sql_editor");
                editor.setTheme("ace/theme/monokai");
                editor.getSession().setMode("ace/mode/sql");
                editor.setValue(record.sql_content);
                $("#free_sql_scalarType").val(record['scalarType']);
                $("#free_sql_pagination").attr('checked',record['pagination']);
            }
        }).fail(function (data) {
                $("#error_msg").text("获取历史记录失败");
            });

        current.hide();
        $(".step2-3").show();
    };

    var step2_1 = function(){
        window.ajaxutil.post_task();
    };

    var step2_2 = function(record,current){
        if ($("#tables").val() == "") {
            $("#error_msg").text("请选择一个表！");
            return;
        }
        if ($("#method_name").val() == "") {
            $("#error_msg").text("请填写方法名！");
            return;
        }
        $("#error_msg").text("");

        $("select[id$=fields] > option").remove();
        $("select[id$=selected_condition] > option").remove();
        $("select[id$=conditions] > option:gt(0)").remove();
        $("select[id$=orderby_field] > option:gt(0)").remove();

        var url = sprintf(
            "/rest/db/fields?table_name=%s&db_name=%s&rand=%s",
            $("#tables").val(),
            $("#databases").val(), Math.random());

        cblock($("body"));
        $.get(url, function (data) {
            var fieldList = [];
            var values = [];
            $.each(data, function (index, value) {
                values.push(value.name);
                fieldList.push($('<option>', {
                    value: value.name,
                    text: sprintf("%s%s%s", value.name, value.indexed ? "(索引)" : "", value.primary ? "(主键)" : "")
                }));
                $("#conditions").append($('<option>', {
                    value: value.name,
                    text: value.name
                }));
                $("#orderby_field").append($('<option>', {
                    value: value.name,
                    text: sprintf("%s%s%s", value.name, value.indexed ? "(索引)" : "", value.primary ? "(主键)" : "")
                }));
            });

            $("#fields").append(fieldList).multipleSelect({
                "refresh": true,
                onCheckAll: function() {
                    window.sql_builder.build();
                },
                onUncheckAll: function() {
                    ace.edit("sql_builder").setValue("");
                },
                onClick: function(option) {
                    var tempValue = $('#fields').multipleSelect('getSelects');
                    if(tempValue.length>1 && $("#crud_option").val() == "select"){
                        alert('只能选择一个字段，或者选择全部字段');
                        $.each(tempValue,function(index,value){
                            if(option['value']!=value){
                                $('#fields').multipleSelect('setSelects', [value]);
                            }
                        });
                    }
                    window.sql_builder.build();
                }
            });
            if ($("#page1").attr('is_update') == "1") {
                if(!$.isEmpty(record["orderby"])){
                    $("#orderby_field").val(record["orderby"].split(',')[0]);
                    $("#orderby_sort").val(record["orderby"].split(',')[1]);
                }
                $("#auto_sql_scalarType").val(record["scalarType"]);
                $("#auto_sql_pagination").attr('checked',record['pagination']);
                $('#fields').multipleSelect('setSelects', record.fields.split(","));
                if (record.condition != undefined && record.condition != "") {
                    var selectedConditions = record.condition.split(";");
                    $.each(selectedConditions, function (index, value) {
                        $("#selected_condition").append($('<option>', {
                            value: value,
                            text: sprintf("%s %s", value.split(',')[0],
                                $(sprintf("#condition_values > option[value='%s']",
                                    value.split(',')[1])).text())
                        }));
                    });
                }
                var sql_builder = ace.edit("sql_builder");
                sql_builder.setTheme("ace/theme/monokai");
                sql_builder.getSession().setMode("ace/mode/sql");
                sql_builder.setValue(record["sql_content"]);
                sql_builder.setReadOnly(true);
            }else{
                window.sql_builder.build();
            }

            current.hide();
            $(".step2-2-1").show();

            var op_type = $("#crud_option").val();
            $("#auto_sql_scalarTypeDiv").hide();
            $("#orderby").hide();
            if (op_type == "select") {
                $(".step2-2-1-1").show();
                $(".step2-2-1-2").show();
                $("#auto_sql_scalarTypeDiv").show();
                $("#orderby").show();
                if($("#sql_style").val()=="csharp"){
                    $("#auto_sql_scalarType option[value='Single']").hide();
                }else{
                    $("#auto_sql_scalarType option[value='Single']").show();
                }
            } else if (op_type == "update") {
                $(".step2-2-1-1").show();
                $(".step2-2-1-2").show();
            } else if (op_type == "insert") {
                $(".step2-2-1-1").show();
                $(".step2-2-1-2").hide();
            } else {
                $(".step2-2-1-1").hide();
                $(".step2-2-1-2").show();
            }
//            if("java"==$("#sql_style").val() && "select"==$("#crud_option").val()){
//                $('#fields').multipleSelect('setSelects', values);
//                $('#fields').multipleSelect('disable');
//            }else{
//                $('#fields').multipleSelect('enable');
//            }
//            if ($("#page1").attr('is_update') != "1") {
//                window.sql_builder.build();
//            }
            $("body").unblock();
        }).fail(function(data){
                $("#error_msg").text("获取表的信息失败，是否有权限");
                $("body").unblock();
            });
    };

    var step2_2_1 = function(record,current){
        var crud_option = $("#crud_option").val();
        if (crud_option != "delete" &&  $('#fields').multipleSelect('getSelects').length < 1 ) {
            $("#error_msg").text("请选择至少一个字段！");
            return;
        }
        $("#error_msg").text("");
        if(crud_option=="insert"){
            window.ajaxutil.post_task();
            return;
        }

        //解析Sql语句，提取出参数
        var regexIndex = /(\?{1})/igm;
        var regexNames = /[@:](\w+)/igm;
        var sqlContent = ace.edit("sql_builder").getValue(),
            result;
        var htmls = "";
        var i = 0;

        var conditionParamCount = 0;
        $('#selected_condition>option').each(function(index,value){
            if(value["value"].split(",")[1]=="6"){
                conditionParamCount++;
            }
            conditionParamCount++;
        });

        var condition;
        var conditions;
        var conVal = new Array();
        if ($("#page1").attr('is_update') == "1") {
            condition = record['condition'];
            // 模式： Age,6,aa,bb;Name,1,param2;
            conditions = condition.split(";");
            for(var j=0;j<conditions.length;j++){
                var con = conditions[j];
                var keyValue = con.split(",");
                // Between类型的操作符需要特殊处理
                if (keyValue[1]=="6"){
                    conVal.push(keyValue[2]);
                    conVal.push(keyValue[3]);
                }else{
                    conVal.push(keyValue[2]);
                }
            }
        }

        while ((result = regexIndex.exec(sqlContent))) { //按照java风格解析
            if(conditionParamCount == i && "update"==crud_option){
                break;
            }
            i++;
            var temp = conVal.shift();
            if(temp!=null && temp!=""){
                htmls = htmls + sprintf(variableHtml, temp)+"</div><br/>";
            }else{
                htmls = htmls + sprintf(variableHtml, sprintf("param%s", i))+"</div><br/>";
            }
        }
        if (htmls.length == 0) {
            var sqlTemp = sqlContent;
            var namesStack = new Array();
            while ((result = regexNames.exec(sqlTemp))){
                if("update"==crud_option){
                    namesStack.push(result[1]);
                }
            }
            var delCount = namesStack.length-conditionParamCount;
            for(var si=0;si<delCount;si++){
                namesStack.shift();
            }
            while ((result = regexNames.exec(sqlContent))) {//按照c#风格解析
                if(conditionParamCount == i && "update"==crud_option){
                    break;
                }
                i++;
                var temp = conVal.shift();
                if(temp!=null && temp!=""){
                    namesStack.shift();
                    htmls = htmls + sprintf(variableHtml, temp)+"</div><br/>";
                }else{
                    var realName = "update"==crud_option? namesStack.shift():result[1];
                    htmls = htmls + sprintf(variableHtml, realName) + "</div><br/>";
                }

            }
        }

        if(htmls.length==0){
            $("#param_list_auto_div").hide();
            $("#param_list_auto").empty();
        }else{
            $("#param_list_auto_div").show();
            $("#param_list_auto").html(htmls);
        }

        if($("#auto_sql_pagination").is(":checked")==true && $("#orderby_field").val()=='-1'
            && $("#crud_option").val()=="select"){
            $("#error_msg").html("请选择排序(Order by)的字段");
            return;
        }

        window.sql_builder.buildPagingSQL(function(){
            $("#error_msg").html(" ");
            current.hide();
            $(".step2-2-2").show();
        });

    };

    var step2_2_2 = function(){
        window.ajaxutil.post_task();
    };

    var step2_3 = function(record,current){
        //首先解析Sql语句，提取出参数
        var regexIndex = /(\?{1})/igm;
        var regexNames = /[@:](\w+)/igm;
        var sqlContent = ace.edit("sql_editor").getValue(),
            result;
        var htmls = "";
        var i = 0;

        var conVal = new Array();
        if ($("#page1").attr('is_update') == "1") {
            var splitedParams = record.parameters.split(";");

            $.each(splitedParams, function (index, value) {
                var resultParams = value.split(",");
                conVal.push(resultParams[0]);
            });
        }

        while ((result = regexIndex.exec(sqlContent))) {
            i++;
            var temp = conVal.shift();
            if(temp!=null && temp!=""){
                htmls = htmls
                    + sprintf(variableHtml, temp)
                    + sprintf(variable_typesHtml,
                    sprintf("id='db_type_%s'", sprintf("param%s", i)));
            }else{
                htmls = htmls
                    + sprintf(variableHtml, sprintf("param%s", i))
                    + sprintf(variable_typesHtml,
                    sprintf("id='db_type_%s'", sprintf("param%s", i)));
            }
        }
        if (htmls.length == 0) {
            while ((result = regexNames.exec(sqlContent))) {
                i++;
                var temp = conVal.shift();
                if(temp!=null && temp!=""){
                    htmls = htmls
                        + sprintf(variableHtml, temp)
                        + sprintf(variable_typesHtml,
                        sprintf("id='db_type_%s'", sprintf("param%s", i)));
                }else{
                    var realName = result[1];
                    htmls = htmls
                        + sprintf(variableHtml, realName)
                        + sprintf(variable_typesHtml,
                        sprintf("id='db_type_%s'", realName));
                }
            }
        }

        if(htmls.length==0){
            $("#param_list_free_div").hide();
            $("#param_list").empty();
        }else{
            $("#param_list_free_div").show();
            $("#param_list").html(htmls);
        }

        if ($("#page1").attr('is_update') == "1") {
            splitedParams = record.parameters.split(";");
            $.each(splitedParams, function (index, value) {
                if(index<=i){
                    var resultParams = value.split(",");
                    var paramIndex = index+1;
                    $("#db_type_param"+paramIndex).val(resultParams[1]);
                }
            });
        }

        window.sql_builder.buildPagingSQL(function(){
            $("#error_msg").html(" ");
            current.hide();
            $(".step2-3-1").show();
        });

    };

    var step2_3_1 = function(){
        window.ajaxutil.post_task();
    };

    wizzard.prototype = {

        clear: function(){
            $("#error_msg").html("");
            //默认后缀
            $("#suffix").val("Gen");

            $("#sql_class_name").val("");
            $("#sql_pojo_name").val("");
            $("#sql_method_name").val("");

            var sql_builder = ace.edit("sql_builder");
            sql_builder.setTheme("ace/theme/monokai");
            sql_builder.getSession().setMode("ace/mode/sql");
            sql_builder.setValue("");

            $("#method_name").val("");

            var editor = ace.edit("sql_editor");
            editor.setTheme("ace/theme/monokai");
            editor.getSession().setMode("ace/mode/sql");
            editor.setValue(null);
        },
        next: function (current) {
            //首先获取当前Grid选中的行,records是id数组
            var records = w2ui['grid'].getSelection();
            var record = null;
            if (records.length > 0)
                record = w2ui['grid'].get(records[0]);

            $("#error_msg").css("color","red");

            //向导首先显示所有数据库服务器，点击下一步后，获取此服务器所有的数据库列表
            if (current.hasClass("step1")) {
                step1(record, current);
            } else if (current.hasClass("step2-1")) {
                step2_1();
            } else if (current.hasClass("step2-2")) {
                step2_2(record, current);
            } else if (current.hasClass("step2-2-1")) {
                step2_2_1(record, current);
            } else if (current.hasClass("step2-2-2")) {
                step2_2_2();
            } else if (current.hasClass("step2-3")) {
                step2_3(record, current);
            } else if (current.hasClass("step2-3-1")) {
                step2_3_1();
            }
        },
        previous: function (current) {
            $("#error_msg").text("");
            if (current.hasClass("step1")) {
                return;
            }
            current.hide();

            if (current.hasClass("step2-1")
                || current.hasClass("step2-2")
                || current.hasClass("step2-3")) {
                $(".step1").show();
            } else if (current.hasClass("step2-2-1")) {
                $(".step2-2").show();
            } else if (current.hasClass("step2-2-2")) {
                $(".step2-2-1").show();
            }  else if (current.hasClass("step2-3-1")) {
                $(".step2-3").show();
            }
        }
    };

    window.wizzard = new wizzard();
})(window);