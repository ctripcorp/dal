(function (window, undefined) {
    var sql_builder = function () {
    };

    // make it global
    whereCondition = {
        "0": "=",
        "1": "!=",
        "2": ">",
        "3": "<",
        "4": ">=",
        "5": "<=",
        "6": "Between",
        "7": "Like",
        "8": "In",
        "9": "IS NULL",
        "10": "IS NOT NULL",
        "11": "And",
        "12": "Or",
        "13": "Not",
        "14": "LeftBracket",
        "15": "RightBracket"
    };

    var wrapColumn = function (value) {
        var mySQLDelimiter = '`';
        var sqlServerDelimiter = ['[', ']'];
        if ($(".step2-2-1").attr("dbCatalog") == "MySql") {
            value = sprintf("%s%s%s", mySQLDelimiter, value, mySQLDelimiter);
        } else {
            value = sprintf("%s%s%s", sqlServerDelimiter[0], value, sqlServerDelimiter[1]);
        }
        return value;
    };

    var wrapTable = function (value) {
        if ($(".step2-2-1").attr("dbCatalog") == "MySql") {
            return value;
        } else {
            return value + " WITH (NOLOCK)";
        }
    };

    var getConditionsSQL = function () {
        var options = [];
        var array = [];
        var conditions = [];
        // push all select options
        $("#selected_condition > option").each(function () {
            if ($(this).val().indexOf(",") > -1) {
                options.push($(this).val());
            } else {
                options.push($(this).text());
            }
        });
        // fix AND operator
        var length = options.length;
        $.each(options, function (i, n) {
            array.push(n);
            var index = i + 1;
            if (index < length) {
                if (options[i].indexOf(",") > -1 && options[index].indexOf(",") > -1) {
                    array.push("AND");
                }
            }
        });
        $.each(array, function (i, n) {
            var splited = n.split(",");
            if (splited.length == 1) {
                // "And", "Or", "Not", "(",")"
                conditions.push(splited[0]);
            } else if (splited.length >= 2) {
                // between
                if (splited[1] == "6") {// between
                    if ($("#sql_style").val() == "csharp") {
                        conditions.push(sprintf(" %s BETWEEN @%s_start AND @%s_end ", wrapColumn(splited[0]), splited[0], splited[0]));
                    } else {
                        conditions.push(sprintf(" %s BETWEEN ? AND ? ", wrapColumn(splited[0])));
                    }
                } else if (splited[1] == "8") { // in
                    if ($("#sql_style").val() == "csharp") {
                        conditions.push(sprintf(" %s in (@%s) ", wrapColumn(splited[0]), splited[0]));
                    } else {
                        conditions.push(sprintf(" %s in (?) ", wrapColumn(splited[0])));
                    }
                } else if (splited[1] == "9" || splited[1] == "10") {// is
                    // null、is
                    // not
                    // null
                    conditions.push(sprintf(" %s %s ", wrapColumn(splited[0]), whereCondition[splited[1]]));
                } else {
                    if ($("#sql_style").val() == "csharp") {
                        conditions.push(sprintf(" %s %s @%s ", wrapColumn(splited[0]), whereCondition[splited[1]], splited[0]));
                    } else {
                        conditions.push(sprintf(" %s %s ? ", wrapColumn(splited[0]), whereCondition[splited[1]]));
                    }
                }
            }
        });

        return conditions;
    };

    var buildSelectSQL = function (conditions) {
        var select_sql_builder = '';
        var wrapedFields = [];
        $.each($('#fields').multipleSelect('getSelects'),
            function (index, value) {
                wrapedFields.push(wrapColumn(value));
            });

        var tableName = wrapColumn($("#tables").val());
        if (conditions != undefined && conditions.length > 0) {
            select_sql_builder = sprintf("SELECT %s FROM %s WHERE %s", wrapedFields.join(","), wrapTable(tableName), conditions.join(" "));
        } else {
            select_sql_builder = sprintf("SELECT %s FROM %s", wrapedFields.join(","), wrapTable(tableName));
        }

        if ($("#orderby_field").val() != '-1') {
            select_sql_builder = sprintf("%s ORDER BY %s %s", select_sql_builder, wrapColumn($("#orderby_field").val()), $("#orderby_sort").val());
        }
        ace.edit("sql_builder").setValue(select_sql_builder);
        ace.edit("sql_builder").setReadOnly(true);
    };

    var buildInsertSQL = function () {
        var placeHodler = [];
        $.each($('#fields').multipleSelect('getSelects'),
            function (index, value) {
                if ($("#sql_style").val() == "csharp") {
                    placeHodler.push(sprintf(" @%s ", value));
                } else {
                    placeHodler.push(" ? ");
                }
            });
        var wrapedFields = [];
        $.each($('#fields').multipleSelect('getSelects'),
            function (index, value) {
                wrapedFields.push(wrapColumn(value));
            });

        var tableName = wrapColumn($("#tables").val());
        ace.edit("sql_builder").setValue(sprintf("INSERT INTO %s (%s) VALUES (%s)", tableName, wrapedFields.join(","), placeHodler.join(",")));
        ace.edit("sql_builder").setReadOnly(true);
    };

    var buildUpdateSQL = function (conditions) {
        var placeHodler = [];
        $.each($('#fields').multipleSelect('getSelects'),
            function (index, value) {
                if ($("#sql_style").val() == "csharp") {
                    placeHodler.push(sprintf(" %s = @%s ", wrapColumn(value), value));
                } else {
                    placeHodler.push(sprintf(" %s = ? ", wrapColumn(value)));
                }
            });

        var tableName = wrapColumn($("#tables").val());
        if (conditions.length > 0) {
            ace.edit("sql_builder").setValue(sprintf("UPDATE %s SET %s WHERE %s", tableName, placeHodler.join(","), conditions.join(" ")));
        } else {
            ace.edit("sql_builder").setValue(sprintf("UPDATE %s SET %s ", tableName, placeHodler.join(",")));
        }
        ace.edit("sql_builder").setReadOnly(true);
    };

    var buildDeleteSQL = function (conditions) {
        var tableName = wrapColumn($("#tables").val());
        if (conditions.length > 0) {
            ace.edit("sql_builder").setValue(sprintf("DELETE FROM %s WHERE %s", tableName, conditions.join(" ")));
        } else {
            ace.edit("sql_builder").setValue(sprintf("DELETE FROM %s", tableName));
        }
        ace.edit("sql_builder").setReadOnly(true);
    };

    sql_builder.prototype = {
        build: function () {
            var conditions = getConditionsSQL();
            if ($("#crud_option").val() == "select") {
                buildSelectSQL(conditions);
            } else if ($("#crud_option").val() == "insert") {
                buildInsertSQL();
            } else if ($("#crud_option").val() == "update") {
                buildUpdateSQL(conditions);
            } else if ($("#crud_option").val() == "delete") {
                buildDeleteSQL(conditions);
            }
        },
        buildPagingSQL: function (callable) {
            var postData = {};
            postData["db_name"] = $("#databases").val();
            postData["sql_style"] = $("#sql_style").val();
            if ($("#gen_style").val() == "auto") { // 构建SQL（生成的代码绑定到模板）
                postData["sql_content"] = ace.edit("sql_builder").getValue();
                if ($("#auto_sql_pagination").is(":checked") == false || $("#crud_option").val() != "select") {
                    showSQL("step2_2_2_sql_editor", postData["sql_content"]);
                    callable();
                    return;
                }
                cblock($("body"));
                $.post("/rest/task/auto/buildPagingSQL", postData, function (data) {
                    if (data.code == "OK") {
                        showSQL("step2_2_2_sql_editor", data.info);
                        callable();
                    } else {
                        $("#error_msg").html(data.info);
                    }
                    $("body").unblock();
                }).fail(function (data) {
                    alert("构建分页SQL语句出错,请刷新页面重试!");
                    $("body").unblock();
                });
            } else if ($("#gen_style").val() == "sql") {// 复杂查询（额外生成实体类）
                postData["sql_content"] = ace.edit("sql_editor").getValue();
                if ($("#free_sql_pagination").is(":checked") == false || $("#free_sql_crud_option").val() != "select") {
                    showSQL("step2_3_1_sql_editor", postData["sql_content"]);
                    callable();
                    return;
                }
                cblock($("body"));
                $.post("/rest/task/sql/buildPagingSQL", postData, function (data) {
                    if (data.code == "OK") {
                        showSQL("step2_3_1_sql_editor", data.info);
                        callable();
                    } else {
                        $("#error_msg").html(data.info);
                    }
                    $("body").unblock();
                }).fail(function (data) {
                    alert("构建分页SQL语句出错,请刷新页面重试!");
                    $("body").unblock();
                });
            }
        },
        getDatabaseCategory: function () {
            var postData = {};
            postData["db_set_name"] = $("#databases").val();
            $.post("/rest/task/auto/getDatabaseCategory", postData, function (data) {
                if (data.code == "OK") {
                    $(".step2-2-1").attr("dbCatalog", data.info);
                } else {
                    $("#error_msg").html(data.info);
                }
            }).fail(function (data) {
                alert("获取当前选择的逻辑数据库的类型失败!");
            });
        }
    };

    var showSQL = function (id, sql) {
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