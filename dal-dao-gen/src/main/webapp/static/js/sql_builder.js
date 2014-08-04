
(function (window, undefined) {
    var sql_builder = function () {

    };

    var whereCondition = {
        "0": "=",
        "1": "!=",
        "2": ">",
        "3": "<",
        "4": ">=",
        "5": "<=",
        "6": "Between",
        "7": "Like",
        "8": "In"
    };

    var wrapColumn = function(value){
        var MySQLDelimiter = '`';
        var SQLServerDelimiter = ['[',']'];
        if($(".step2-2-1").attr("dbCatalog")=="MySql"){
            value = sprintf("%s%s%s", MySQLDelimiter, value, MySQLDelimiter);
        }else{
            value = sprintf("%s%s%s", SQLServerDelimiter[0], value, SQLServerDelimiter[1]);
        }
        return value;
    };

    var wrapTable = function(value){
        if($(".step2-2-1").attr("dbCatalog")=="MySql"){
            return value;
        }else{
            return value+" WITH (NOLOCK)";
        }
    };

    var buildFormatedConditionsSQL = function(){
        var formatedConditions = [];
        $("#selected_condition > option").each(function () {
            var splited = this.value.split(",");
            if (splited.length >= 2) {
                //between
                if (splited[1] == "6") {
                    if ($("#sql_style").val() == "csharp") {
                        formatedConditions.push(sprintf(" %s BETWEEN @%s_start AND @%s_end ",wrapColumn(splited[0]), splited[0], splited[0]));
                    } else {
                        formatedConditions.push(sprintf(" %s BETWEEN ? AND ? ",wrapColumn(splited[0])));
                    }
                } else {
                    if ($("#sql_style").val() == "csharp") {
                        formatedConditions.push(sprintf(" %s %s @%s ", wrapColumn(splited[0]), whereCondition[splited[1]], splited[0]));
                    } else {
                        formatedConditions.push(sprintf(" %s %s ? ", wrapColumn(splited[0]), whereCondition[splited[1]]));
                    }
                }
            }
        });
        return formatedConditions;
    };

    var buildSelectSQL = function(formatedConditions){
        var select_sql_builder = '';
        var wrapedFields = [];
        $.each($('#fields').multipleSelect('getSelects'),function(index,value){
            wrapedFields.push(wrapColumn(value));
        });
        if("java"==$("#sql_style").val()){
            if (formatedConditions.length > 0) {
                select_sql_builder = sprintf("SELECT %s FROM %s WHERE %s", wrapedFields.join(","),
                    wrapTable($("#tables").val()), formatedConditions.join(" AND "));
            } else {
                select_sql_builder = sprintf("SELECT %s FROM %s", wrapedFields.join(","),
                    wrapTable($("#tables").val()));
            }
        }else{
            if (formatedConditions.length > 0) {
                select_sql_builder = sprintf("SELECT %s FROM %s WHERE %s", wrapedFields.join(","),
                    wrapTable($("#tables").val()), formatedConditions.join(" AND "));
            } else {
                select_sql_builder = sprintf("SELECT %s FROM %s", wrapedFields.join(","),
                    wrapTable($("#tables").val()));
            }
        }
        if($("#orderby_field").val() != '-1'){
            select_sql_builder = sprintf("%s ORDER BY %s %s",select_sql_builder,$("#orderby_field").val(),$("#orderby_sort").val());
        }
        ace.edit("sql_builder").setValue(select_sql_builder);
        ace.edit("sql_builder").setReadOnly(true);
    };

    var buildInsertSQL = function(){
        var placeHodler = [];
        $.each($('#fields').multipleSelect('getSelects'), function (index, value) {
            if ($("#sql_style").val() == "csharp") {
                placeHodler.push(sprintf(" @%s ", value));
            } else {
                placeHodler.push(" ? ");
            }
        });
        var wrapedFields = [];
        $.each($('#fields').multipleSelect('getSelects'),function(index,value){
            wrapedFields.push(wrapColumn(value));
        });
        ace.edit("sql_builder").setValue(sprintf("INSERT INTO %s (%s) VALUES (%s)",
            $("#tables").val(), wrapedFields.join(","), placeHodler.join(",")));
        ace.edit("sql_builder").setReadOnly(true);
    };

    var buildUpdateSQL = function(formatedConditions){
        var placeHodler = [];
        $.each($('#fields').multipleSelect('getSelects'), function (index, value) {
            if ($("#sql_style").val() == "csharp") {
                placeHodler.push(sprintf(" %s = @%s ", wrapColumn(value), value));
            } else {
                placeHodler.push(sprintf(" %s = ? ", wrapColumn(value)));
            }
        });

        if (formatedConditions.length > 0) {
            ace.edit("sql_builder").setValue(sprintf("UPDATE %s SET %s WHERE %s",
                $("#tables").val(),
                placeHodler.join(","),
                formatedConditions.join(" AND ")));
        } else {
            ace.edit("sql_builder").setValue(sprintf("UPDATE %s SET %s ",
                $("#tables").val(),
                placeHodler.join(",")));
        }
        ace.edit("sql_builder").setReadOnly(true);
    };

    var buildDeleteSQL = function(formatedConditions){
        if (formatedConditions.length > 0) {
            ace.edit("sql_builder").setValue(sprintf("Delete FROM %s WHERE %s",
                $("#tables").val(),
                formatedConditions.join(" AND ")));
        } else {
            ace.edit("sql_builder").setValue(sprintf("Delete FROM %s", $("#tables").val()));
        }
        ace.edit("sql_builder").setReadOnly(true);
    };

    sql_builder.prototype = {
        build: function () {
            var formatedConditions = buildFormatedConditionsSQL();
            if($("#crud_option").val() == "select") {
                buildSelectSQL(formatedConditions);
            } else if($("#crud_option").val() == "insert") {
                buildInsertSQL();
            } else if($("#crud_option").val() == "update") {
                buildUpdateSQL(formatedConditions);
            } else if($("#crud_option").val() == "delete") {
                buildDeleteSQL(formatedConditions);
            }
        },
        buildPagingSQL : function(callable){
            var postData = {};
            postData["db_name"] = $("#databases").val();
            if ($("#gen_style").val() == "auto") { //构建SQL（生成的代码绑定到模板）
                postData["sql_style"] = $("#sql_style").val();
                postData["sql_content"] = ace.edit("sql_builder").getValue();
                if($("#auto_sql_pagination").is(":checked")==false){
                    showSQL("step2_2_2_sql_editor",postData["sql_content"]);
                    callable();
                    return;
                }
                cblock($("body"));
                $.post("/rest/task/auto/buildPagingSQL", postData, function (data) {
                    if (data.code == "OK") {
                        showSQL("step2_2_2_sql_editor",data.info);
                        callable();
                    }else{
                        $("#error_msg").html(data.info);
                    }
                    $("body").unblock();
                }).fail(function (data) {
                        alert("构建分页SQL语句出错,请刷新页面重试!");
                        $("body").unblock();
                    });
            } else if ($("#gen_style").val() == "sql") {//复杂查询（额外生成实体类）
                postData["sql_content"] = ace.edit("sql_editor").getValue();
                if($("#free_sql_pagination").is(":checked")==false){
                    showSQL("step2_3_1_sql_editor",postData["sql_content"]);
                    callable();
                    return;
                }
                cblock($("body"));
                $.post("/rest/task/sql/buildPagingSQL", postData, function (data) {
                    if (data.code == "OK") {
                        showSQL("step2_3_1_sql_editor",data.info);
                        callable();
                    }else{
                        $("#error_msg").html(data.info);
                    }
                    $("body").unblock();
                }).fail(function (data) {
                        alert("构建分页SQL语句出错,请刷新页面重试!");
                        $("body").unblock();
                    });
            }
        },
        getDatabaseCategory : function(){
            var postData = {};
            postData["db_set_name"] = $("#databases").val();
            $.post("/rest/task/auto/getDatabaseCategory", postData, function (data) {
                if (data.code == "OK") {
                    $(".step2-2-1").attr("dbCatalog",data.info);
                }else{
                    $("#error_msg").html(data.info);
                }
            }).fail(function (data) {
                    alert("获取当前选择的逻辑数据库的类型失败!");
                });
        }
    };

    var showSQL = function(id,sql){
        var editor = ace.edit(id);
        editor.setTheme("ace/theme/monokai");
        editor.getSession().setMode("ace/mode/sql");
        editor.setValue(sql);
        editor.setReadOnly(true);
    };

    /**
     * export to either browser or node.js
     */
    window.sql_builder = new sql_builder();

})(window);