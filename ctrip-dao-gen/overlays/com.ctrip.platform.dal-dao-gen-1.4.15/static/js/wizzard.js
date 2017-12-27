//向导注释
//step1
//step2
//step3-1 -> step3-2
(function (window, undefined) {
    var wizzard = function () {
    };

    var variableHtmlOrigin = "<div class='row-fluid'><input type='text' class='span3' value='%s'>";

    var variableHtml = "<div class='row-fluid'><input type='text' class='span3' value='%s'>"
        + " <label class='popup_label'>&nbsp;<input type='checkbox' %s >允许NULL值</label>";

    var variableHtml_ForJAVA = "<div class='row-fluid'><input type='text' class='span3' value='%s'>"
        + " <label class='popup_label'>&nbsp;<input type='checkbox' %s >允许NULL值</label>"
        + " <label class='popup_label'>&nbsp;&nbsp;<input type='checkbox' %s >参数敏感</label>";

    var variable_typesHtml_ForJAVA = "<select %s class='span5'>"
        + "<option value='_please_select'>--参数类型--</option>"
        + "<option value='-7'>Bit----Boolean</option>"
        + "<option value='-6'>TinyInt----Byte</option>"
        + "<option value='5'>SmallInt----Short</option>"
        + "<option value='4'>Integer----int</option>"
        + "<option value='-5'>BigInt----long</option>"
        + "<option value='7'>Real----Float</option>"
        + "<option value='8'>Double</option>"
        + "<option value='3'>Decimal</option>"
        + "<option value='2'>Numeric</option>"
        + "<option value='1'>Char----String</option>"
        + "<option value='12'>Varchar----String</option>"
        + "<option value='-1'>LongVarchar----String</option>"
        + "<option value='-15'>Nchar----String</option>"
        + "<option value='-9'>NVarchar----String</option>"
        + "<option value='-16'>LongNVarchar----String</option>"
        + "<option value='91'>Date</option>"
        + "<option value='92'>Time</option>"
        + "<option value='93'>Timestamp</option>"
        + "<option value='-2'>binary----byte[]</option>"
        + "<option value='-3'>VarBinary----byte[]</option>"
        + "<option value='-4'>LongVarBinary----byte[]</option>"
        + "<option value='10001'>uniqueidentifier----Guid</option>"
        + "</select><div>&nbsp;&nbsp;&nbsp;&nbsp;参数敏感：<input type='checkbox' checked='checked'></div></div><br>";

    var variable_typesHtml = "<select %s class='span5'>"
        + "<option value='_please_select'>--参数类型--</option>"
        + "<option value='-7'>Bit----Boolean</option>"
        + "<option value='-6'>TinyInt----Byte</option>"
        + "<option value='5'>SmallInt----Short</option>"
        + "<option value='4'>Integer----int</option>"
        + "<option value='-5'>BigInt----long</option>"
        + "<option value='7'>Real----Float</option>"
        + "<option value='8'>Double</option>"
        + "<option value='3'>Decimal</option>"
        + "<option value='2'>Numeric</option>"
        + "<option value='1'>Char----String</option>"
        + "<option value='12'>Varchar----String</option>"
        + "<option value='-1'>LongVarchar----String</option>"
        + "<option value='-15'>Nchar----String</option>"
        + "<option value='-9'>NVarchar----String</option>"
        + "<option value='-16'>LongNVarchar----String</option>"
        + "<option value='91'>Date</option>"
        + "<option value='92'>Time</option>"
        + "<option value='93'>Timestamp</option>"
        + "<option value='-2'>binary----byte[]</option>"
        + "<option value='-3'>VarBinary----byte[]</option>"
        + "<option value='-4'>LongVarBinary----byte[]</option>"
        + "<option value='10001'>uniqueidentifier----Guid</option>"
        + "</select></div><br>";

    var step1 = function (record, current) {
        var errorMsg = $("#error_msg");

        if ($("#databases").val().length == 0) {
            errorMsg.text("请选择逻辑数据库！");
            return;
        }
        if ($("#comment").val().length == 0) {
            errorMsg.text("请输入方法功能描述！");
            return;
        }
        errorMsg.text("");

        // 首先蒙板化整个body,在后续get成功或者失败时，取消蒙板化
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

    var step1_table_view_sp = function (record, current) {
        cblock($("body"));
        var nextStep = $("#next_step");
        nextStep.attr("disabled", "true");
        nextStep.text("正在加载...");
        nextStep.removeClass("btn-primary");
        $.get("/rest/db/table_sps", {
            db_name: $("#databases").val(),
            rand: Math.random()
        }).done(function (result) {
            if (result.code != 'OK') {
                $("#error_msg").text(result.info);
                nextStep.removeAttr("disabled");
                nextStep.addClass("btn-primary");
                nextStep.text("下一步");
                $("body").unblock();
                return;
            }
            var data = $.parseJSON(result.info);
            $("select[id$=table_list] > option").remove();
            $("select[id$=view_list] > option").remove();
            $("select[id$=sp_list] > option").remove();
            var tableArray = [];
            var viewArray = [];
            var spArray = [];
            $.each(data.tables, function (index, value) {
                tableArray.push($("<option>", {
                    value: value, text: value
                }));
            });
            $.each(data.views, function (index, value) {
                viewArray.push($("<option>", {
                    value: value, text: value
                }));
            });
            $.each(data.sps, function (index, value) {
                spArray.push($("<option>", {
                    value: value.schema + "." + value.name, text: value.schema + "." + value.name
                }));
            });

            var cudBySp = $("#cud_by_sp");
            var mysqlHide = $(".mysql_hide");
            if (data.dbType == "MySQL") {
                cudBySp.prop("checked", false);
                mysqlHide.hide();
            } else {
                $.get("/rest/user/isDefaultUser", {rand: Math.random()}, function (data) {
                    if (data == "true") {
                        cudBySp.prop("checked", false);
                        mysqlHide.hide();
                    }
                    else {
                        cudBySp.prop("checked", true);
                        mysqlHide.show();
                    }
                });
            }

            var step = $(".step2-1");
            var tableList = $("#table_list");
            tableList.append(tableArray).multipleSelect({
                "refresh": true,
                onOpen: function () {
                    step.height(330);
                },
                onClose: function () {
                    step.height(245);
                }
            });

            var viewList = $("#view_list");
            viewList.append(viewArray).multipleSelect({
                "refresh": true,
                onOpen: function () {
                    step.height(330);
                },
                onClose: function () {
                    step.height(245);
                }
            });

            var spList = $("#sp_list");
            spList.append(spArray).multipleSelect({
                "refresh": true,
                onOpen: function () {
                    step.height(330);
                },
                onClose: function () {
                    step.height(245);
                }
            });

            if ($("#page1").attr("is_update") == "1"
                && record != undefined
                && record.task_type == "table_view_sp") {
                if (record.table_names != undefined) {
                    tableList.multipleSelect("setSelects", record.table_names.split(","));
                }
                if (record.view_names != undefined) {
                    viewList.multipleSelect("setSelects", record.view_names.split(","));
                }
                if (record.sp_names != undefined) {
                    spList.multipleSelect("setSelects", record.sp_names.split(","));
                }
                if (record.suffix != undefined) {
                    $("#prefix").val(record.prefix);
                }
                if (record.suffix != undefined) {
                    $("#suffix").val(record.suffix);
                }
                if (record.pagination != undefined) {
                    $("#pagination").prop("checked", record.pagination);
                }
                if (record.length != undefined) {
                    $("#standard_length_property").prop("checked", record.length);
                }
            }

            var divLength = $("#divStandardLength");
            if ($("#sql_style").val() == "csharp") {
                divLength.hide();
            }
            else {
                divLength.show();
            }

            current.hide();
            step.show();
            nextStep.removeAttr("disabled");
            nextStep.addClass("btn-primary");
            nextStep.text("下一步");
            $("body").unblock();
        }).fail(function (data) {
            $("#error_msg").text("获取表/视图列表失败, 请检查是否有权限, 或者数据库已被删除!");
            nextStep.removeAttr("disabled");
            nextStep.addClass("btn-primary");
            nextStep.text("下一步");
            $("body").unblock();
        });
    };

    var step1_auto = function (record, current) {
        var table = $("#tables")[0];

        // 在显示下一页之前，清空下一页的信息
        if (table != undefined && table.selectize != undefined) {
            table.selectize.clearOptions();
        } else {
            $("#tables").selectize({
                valueField: "id",
                labelField: "title",
                searchField: "title",
                sortField: "title",
                options: [],
                create: false
            });
        }

        var step = $(".step2-2");
        table.selectize.on("dropdown_open", function (dropdown) {
            step.height(240);
        });

        table.selectize.on("dropdown_close", function (dropdown) {
            step.height(145);
        });

        cblock($("body"));
        $.get("/rest/db/tables", {
            db_name: $("#databases").val(),
            rand: Math.random()
        }, function (data) {
            var results = [];
            $.each(data, function (index, value) {
                results.push({
                    id: value,
                    title: value
                });
            });
            table.selectize.addOption(results);
            table.selectize.refreshOptions(false);
            if ($("#page1").attr("is_update") == "1"
                && record != undefined
                && record.task_type == "auto") {
                table.selectize.setValue(record.table_name);
                $("#method_name").val(record.method_name);
                $("#crud_option").val(record.crud_type);
            }
            current.hide();
            step.show();
            $("body").unblock();
        }).fail(function (data) {
            $("#error_msg").text("获取所有表失败!");
            $("body").unblock();
        });
        window.sql_builder.getDatabaseCategory();
    };

    var step1_sql = function (record, current) {
        var sqlClassName = $("#sql_class_name")[0];
        if (sqlClassName != undefined && sqlClassName.selectize != undefined) {
            sqlClassName.selectize.clearOptions();
        } else {
            $("#sql_class_name").selectize({
                valueField: "value",
                labelField: "title",
                searchField: "title",
                sortField: "value",
                options: [],
                create: true
            });
        }

        var sqlPojoName = $("#sql_pojo_name")[0];
        if (sqlPojoName != undefined && sqlPojoName.selectize != undefined) {
            sqlPojoName.selectize.clearOptions();
        } else {
            $("#sql_pojo_name").selectize({
                valueField: "value",
                labelField: "title",
                searchField: "title",
                sortField: "value",
                options: [],
                create: true
            });
        }
        // 在显示下一页之前，清空下一页的信息
        $.get("/rest/task/sql_class", {
            project_id: w2ui["grid"].current_project,
            db_name: $("#databases").val(),
            rand: Math.random()
        }, function (data) {
            var update = $("#page1").attr("is_update") == "1"
                && record != undefined
                && record.task_type == "sql";
            var clazz = [];
            var pojos = [{
                value: "简单类型",
                title: "简单类型"
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
            sqlClassName.selectize.addOption(clazz);
            sqlClassName.selectize.refreshOptions(false);
            sqlPojoName.selectize.addOption(pojos);
            sqlPojoName.selectize.refreshOptions(false);

            if (update) {
                var className = record.class_name;
                if (data.classes.length != 0) {
                    sqlClassName.selectize.setValue(className);
                } else {
                    sqlClassName.selectize.addOption({
                        value: className,
                        title: className
                    });
                    sqlClassName.selectize.setValue(className);
                }

                var pojoName = record.pojo_name;
                if (data.pojos.length != 0) {
                    sqlPojoName.selectize.setValue(pojoName);
                } else {
                    sqlPojoName.selectize.addOption({
                        value: pojoName,
                        title: pojoName
                    });
                    sqlPojoName.selectize.setValue(pojoName);
                }

                $("#sql_method_name").val(record.method_name);
                var editor = ace.edit("sql_editor");
                editor.setTheme("ace/theme/monokai");
                editor.getSession().setMode("ace/mode/sql");
                editor.setValue(record.sql_content);
                if (record.scalarType) {
                    $("#free_sql_scalarType").val(record.scalarType);
                } else {
                    $("#free_sql_scalarType").val("List");
                }

                $("#free_sql_pagination").prop("checked", record.pagination);
                $("#free_length_property").prop("checked", record.length);
                $("#free_sql_crud_option").val(record.crud_type);
                $("#free_sql_crud_option").trigger("change");

                if (record.crud_type == "select") {
                    $("#free_sql_scalarType").trigger("change");
                }
            }

            var divLength = $("#divFreeLength");
            if ($("#sql_style").val() == "csharp") {
                divLength.hide();
            }
            else {
                divLength.show();
            }

        }).fail(function (data) {
            $("#error_msg").text("获取历史记录失败");
        });

        current.hide();
        $(".step2-3-1").show();
        window.sql_builder.getDatabaseCategory();
    };

    var append_api = function (id, value) {
        $("#" + id).append($("<input>", {
            type: "checkbox",
            id: "dal_api_" + value.id,
            checked: "checked"
        }));
        var temp = "<a href='#' class='ctip' data-toggle='tooltip' data-placement='top' style='float:right'"
            + " title='' data-original-title='"
            + value['method_description']
            + "'>"
            + "<span class='glyphicon glyphicon-question-sign'></span></a>";

        $("#dal_api_" + value.id).wrap("<label class='popup_label'></label>").after(value['method_declaration']);
        $("#dal_api_" + value.id).parent().append($(temp));
        $("#" + id + " > label[class='popup_label']").wrapAll("<div class='row-fluid'></div>");
    };

    /**
     *
     * @param daoName
     */
    var checkDaoNameConflict = function (daoName, data) {
        var checkDaoNameConflictResult = false;
        cblock($("body"));
        var current_project = w2ui["grid"].current_project;
        var postData = {
            "prefix": "",
            "suffix": "",
            "dao_id": "-1"
        };
        postData = $.extend(postData, data);
        postData.project_id = current_project;
        postData.db_set_name = $("#databases").val();
        postData.daoName = daoName;
        postData.is_update = $("#page1").attr("is_update");
        if (postData["is_update"] == "1") {
            var records = w2ui["grid"].getSelection();
            var record = w2ui["grid"].get(records[0]);
            postData["dao_id"] = record.id;
        }
        $.ajax({
            type: "POST",
            async: false,
            url: "/rest/task/checkDaoNameConflict",
            data: postData,
            dataType: "json",
            success: function (data) {
                if (data.code != "OK") {
                    $.showMsg("error_msg", data.info);
                    checkDaoNameConflictResult = true;
                }
                $("body").unblock();
            }
        });
        return checkDaoNameConflictResult;
    };

    var step2_1 = function (record, current) {
        var tableList = $("#table_list").multipleSelect("getSelects");
        var viewList = $("#view_list").multipleSelect("getSelects");
        if (tableList.length == 0 && viewList.length == 0) {
            $.showMsg("error_msg", "请选择表或视图!");
            return;
        }

        if (checkDaoNameConflict(tableList.join(","), {
                "prefix": $("#prefix").val(),
                "suffix": $("#suffix").val()
            })) {
            return;
        }
        cblock($("body"));
        var nextStep = $("#next_step");
        nextStep.attr("disabled", "true");
        nextStep.text("正在加载...");
        nextStep.removeClass("btn-primary");
        var data = undefined;
        $.get("/rest/task/table/apiList", {
            db_name: $("#databases").val(),
            table_names: tableList.join(","),
            sql_style: $("#sql_style").val(),
            rand: Math.random()
        }).done(function (result) {
            if (result.code != 'OK') {
                $("#error_msg").text(result.info);
                nextStep.removeAttr("disabled");
                nextStep.addClass("btn-primary");
                nextStep.text("下一步");
                $("body").unblock();
                return;
            }

            data = $.parseJSON(result.info);
            $("#retrieveMethodListDiv").empty();
            $("#updateMethodListDiv").empty();
            $("#deleteMethodListDiv").empty();
            $("#createMethodListDiv").empty();
            $.each(data, function (index, value) {
                var crudType = value.crud_type.toLowerCase();
                if (crudType == "select") {
                    append_api("retrieveMethodListDiv", value);
                } else if (crudType == "update") {
                    append_api("updateMethodListDiv", value);
                } else if (crudType == "delete") {
                    append_api("deleteMethodListDiv", value);
                } else if (crudType == "insert") {
                    append_api("createMethodListDiv", value);
                }
            });

            if ($("#page1").attr("is_update") == "1"
                && record != undefined
                && record.task_type == "table_view_sp"
                && record.api_list != "all"
                && record.api_list != ""
                && record.api_list != undefined
                && data != undefined) {
                var tempArr = record.api_list.split(',');
                $.each(data, function (index, value) {
                    if ($.inArray("dal_api_" + value.id, tempArr) == -1) {
                        $("#dal_api_" + value.id).prop("checked", false);
                    }
                });
            }

            methodApiClickHandler();
            current.hide();

            $.showMsg("error_msg", "");
            $(".step2-1-2").show();
            nextStep.removeAttr("disabled");
            nextStep.addClass("btn-primary");
            nextStep.text("下一步");
            $("body").unblock();
        }).fail(function (data) {
            $("#error_msg").text("获取表/视图列表失败, 请检查是否有权限, 或者数据库已被删除!");
            nextStep.removeAttr("disabled");
            nextStep.addClass("btn-primary");
            nextStep.text("下一步");
            $("body").unblock();
        });
    };

    var methodApiClickHandler = function () {
        bindSelectAllCheck("createMethodListDiv", "selectAllCreateMethodAPIChk");
        bindSelectAllCheck("retrieveMethodListDiv", "selectAllRetrieveMethodAPIChk");
        bindSelectAllCheck("updateMethodListDiv", "selectAllUpdateMethodAPIChk");
        bindSelectAllCheck("deleteMethodListDiv", "selectAllDeleteMethodAPIChk");
    };

    var apiMethodChkValidate = function (methodListDiv, selectAllMethodCheckbox) {
        var totalCkbx = $("#" + methodListDiv + " :checkbox").length;
        var checkedCkbx = 0;
        $.each($("#" + methodListDiv + " :checkbox"), function (index, value) {
            if ($(value).is(":checked") == true) {
                checkedCkbx++;
            }
        });
        if (totalCkbx == checkedCkbx) {
            $("#" + selectAllMethodCheckbox).prop("checked", true);
        } else {
            $("#" + selectAllMethodCheckbox).attr("checked", false);
        }
    };

    var bindSelectAllCheck = function (methodListDiv, selectAllMethodCheckbox) {
        apiMethodChkValidate(methodListDiv, selectAllMethodCheckbox);
        $("#" + methodListDiv + " :checkbox").bind("click", function () {
            var totalCkbx = $("#" + methodListDiv + " :checkbox").length;
            var checkedCkbx = 0;
            $.each($("#" + methodListDiv + " :checkbox"),
                function (index, value) {
                    if ($(value).is(":checked") == true) {
                        checkedCkbx++;
                    }
                });
            if (totalCkbx == checkedCkbx) {
                $("#" + selectAllMethodCheckbox).prop("checked", true);
            } else {
                $("#" + selectAllMethodCheckbox).prop("checked", false);
            }
        });

        $("#" + selectAllMethodCheckbox).bind("click", function () {
            if ($("#" + selectAllMethodCheckbox).is(":checked") == true) {
                $("#" + methodListDiv + " :checkbox").prop("checked", true);
            } else {
                $("#" + methodListDiv + " :checkbox").prop("checked", false);
            }
        });
    };

    var step2_1_2 = function () {
        $("#error_msg").empty();
        var api_list = new Array();
        $.each($(".step2-1-2 input:checked"), function (index, value) {
            api_list.push($(value).attr("id"));
        });
        if (api_list.length < 1) {
            $("#error_msg").text("请选择至少一个API！");
            return;
        }
        window.ajaxutil.post_task();
    };

    var step2_2 = function (record, current) {
        var tables = $("#tables");
        var errorMsg = $("#error_msg");
        if (tables.val().length == 0) {
            errorMsg.text("请选择一个表！");
            return;
        }
        if (checkDaoNameConflict(tables.val())) {
            return;
        }
        if ($("#method_name").val().length == 0) {
            errorMsg.text("请填写方法名！");
            return;
        }
        errorMsg.text("");

        $("select[id$=fields] > option").remove();
        $("select[id$=selected_condition] > option").remove();
        $("select[id$=conditions] > option:gt(0)").remove();
        $("select[id$=orderby_field] > option:gt(0)").remove();

        cblock($("body"));
        $.get("/rest/db/fields", {
            table_name: tables.val(),
            db_name: $("#databases").val(),
            rand: Math.random()
        }, function (data) {
            var fieldList = [];
            var values = [];
            $.each(data, function (index, value) {
                values.push(value.name);
                fieldList.push(
                    $("<option>", {
                        value: value.name,
                        text: sprintf("%s%s%s", value.name, value.indexed ? "(索引)" : "", value.primary ? "(主键)" : "")
                    }));
                $("#conditions").append(
                    $("<option>", {
                        value: value.name,
                        text: value.name
                    }));
                $("#orderby_field").append(
                    $("<option>", {
                        value: value.name,
                        text: sprintf("%s%s%s", value.name, value.indexed ? "(索引)" : "", value.primary ? "(主键)" : "")
                    }));
            });

            $("#fields").append(fieldList).multipleSelect({
                "refresh": true,
                onCheckAll: function () {
                    window.sql_builder.build();
                },
                onUncheckAll: function () {
                    ace.edit("sql_builder").setValue("");
                },
                onClick: function (option) {
                    window.sql_builder.build();
                }
            });

            if ($("#page1").attr("is_update") == "1") {
                var orderBy = record.orderby;
                if (!$.isEmpty(orderBy)) {
                    $("#orderby_field").val(orderBy.split(",")[0]);
                    $("#orderby_sort").val(orderBy.split(",")[1]);
                }
                if (record.scalarType) {
                    $("#auto_sql_scalarType").val(record.scalarType);
                } else {
                    $("#auto_sql_scalarType").val("List");
                }

                $("#auto_sql_pagination").prop("checked", record.pagination);
                $("#build_length_property").prop("chcked", record.length);
                $("#fields").multipleSelect("setSelects", record.fields.split(","));

                if (record.condition != undefined && record.condition.length > 0) {
                    var selectedConditions = record.condition.split(";");
                    if (selectedConditions != undefined && selectedConditions.length > 0) {
                        var length = selectedConditions.length;
                        for (var i = 0; i < length; i++) {
                            var array = selectedConditions[i].split(",");
                            getOption(selectedConditions[i], array);
                            //fix legacy conditions
                            var next = i + 1;
                            if (next < length) {
                                var nextArray = selectedConditions[next].split(",");
                                getLegacyOption(array, nextArray);
                            }
                        }
                    }
                }
                var sql_builder = ace.edit("sql_builder");
                sql_builder.setTheme("ace/theme/monokai");
                sql_builder.getSession().setMode("ace/mode/sql");
                sql_builder.setValue(record["sql_content"]);
                sql_builder.setReadOnly(true);
            } else {
                window.sql_builder.build();
            }

            var divLength = $("#divBuildLength");
            if ($("#sql_style").val() == "csharp") {
                divLength.hide();
            }
            else {
                divLength.show();
            }

            current.hide();
            $(".step2-2-1").show();

            var step1 = $(".step2-2-1-1");
            var step2 = $(".step2-2-1-2");
            var op_type = $("#crud_option").val();
            $("#auto_sql_scalarTypeDiv").hide();
            $("#orderby").hide();
            if (op_type == "select") {
                step1.show();
                step2.show();
                $("#auto_sql_scalarTypeDiv").show();
                $("#auto_sql_scalarType").trigger("change");
                $("#orderby").show();
                if ($("#sql_style").val() == "csharp") {
                    $("#auto_sql_scalarType option[value='Single']").hide();
                } else {
                    $("#auto_sql_scalarType option[value='Single']").show();
                }
            } else if (op_type == "update") {
                step1.show();
                step2.show();
            } else if (op_type == "insert") {
                step1.show();
                step2.hide();
            } else {
                step1.hide();
                step2.show();
            }
            $("body").unblock();
        }).fail(function (data) {
            $("#error_msg").text("获取表的信息失败，请检查是否有权限！");
            $("body").unblock();
        });
    };

    var getLegacyOption = function (array, nextArray) {
        if (array != undefined && array.length > 1) {
            if (nextArray != undefined && nextArray.length > 1) {
                var value = "11";
                $("#selected_condition").append(
                    $('<option>', {
                        value: value, //"And"
                        text: $(sprintf("#condition_values > option[value='%s']", value)).text()
                    }));
            }
        }
    };

    var getOption = function (value, array) {
        if (array != undefined) {
            if (array.length == 1) {
                $("#selected_condition").append(
                    $("<option>", {
                        value: value,
                        text: $(sprintf("#condition_values > option[value='%s']", value)).text()
                    }));
            } else if (array.length > 1) {
                $("#selected_condition").append(
                    $("<option>", {
                        value: value,
                        text: sprintf("%s %s", array[0], $(sprintf("#condition_values > option[value='%s']", array[1])).text())
                    }));
            }
        }
    };

    var step2_2_1 = function (record, current) {
        var crud_option = $("#crud_option").val();
        if (crud_option != "delete" && $("#fields").multipleSelect("getSelects").length < 1) {
            $("#error_msg").text("请选择至少一个字段！");
            return;
        }
        if ($("#auto_sql_pagination").is(":checked") == true
            && $("#orderby_field").val() == "-1"
            && $("#crud_option").val() == "select") {
            $("#error_msg").html("请选择排序(Order by)的字段");
            return;
        }
        $("#error_msg").empty();

        var paramHtml = "";
        if ($("#sql_style").val() == "csharp") {
            paramHtml = step2_2_1_csharp(record, current, crud_option);
            $("#buildJavaHints").hide();
        } else {
            if (crud_option == "insert") {
                paramHtml = step2_2_1_java_insert(record, current, crud_option);
            } else {
                paramHtml = step2_2_1_java(record, current, crud_option);
            }
        }

        var paramListAuto = $("#param_list_auto");
        if (paramHtml.length == 0) {
            $("#param_list_auto_div").hide();
            paramListAuto.empty();
        } else {
            $("#param_list_auto_div").show();
            paramListAuto.show();
            paramListAuto.html(paramHtml);
        }

        // bind java hints
        if ($("#page1").attr("is_update") == "1") {
            if (record.hints != undefined && record.hints.length > 0) {
                var array = record.hints.split(";");
                if (array != undefined && array.length > 0) {
                    // clear checkboxes
                    var cbHints = $("#buildJavaHints :checkbox");
                    if (cbHints != undefined && cbHints.length > 0) {
                        $.each(cbHints, function (index, value) {
                            $(cbHints[index]).prop("checked", false);
                        });
                    }
                    $.each(array, function (index, value) {
                        $("#chk_build_" + value).prop("checked", true);
                    });
                }
            }
        }

        window.sql_builder.buildPagingSQL(function () {
            $("#error_msg").empty();
            current.hide();
            $(".step2-2-2").show();
        });
    };

    var step2_2_1_getSelectedConditionParamCount = function () {
        var count = 0;
        $("#selected_condition>option").each(function (index, value) {
            var array = value.value.split(",");
            if (array.length == 1) {
                return; // continue
            }
            count++;
            var operator = array[1];
            if (operator == "6") {
                count++;
            } else if (operator == "9" || operator == "10") {
                count--; // is null、is notnull don't need param
            }
        });
        return count;
    };

    var step2_2_1_java = function (record, current, crud_option) {
        // 解析Sql语句，提取出参数
        var regexIndex = /(\?{1})/igm;
        var content = ace.edit("sql_builder").getValue(), result;
        var html = "";

        var count = step2_2_1_getSelectedConditionParamCount();
        var names = new Array();
        var nullables = new Array();
        var sensitives = new Array();
        if ($("#page1").attr('is_update') == "1") {
            // 模式：
            // Age,6,aa,bb,nullable,sensitive;Name,1,param2,nullable,sensitive;
            var conditions = record.condition.split(";");
            for (var j = 0; j < conditions.length; j++) {
                var condition = conditions[j];
                var array = condition.split(",");
                if (array.length == 1) {
                    continue;
                }
                var operator = array[1];
                // Between类型的操作符需要特殊处理
                if (operator == "6") {
                    names.push(array[2]);
                    names.push(array[3]);
                    nullables.push(array[4]);
                    nullables.push(array[4]);
                    sensitives.push(array[5]);
                    sensitives.push(array[5]);
                } else if (operator == "9" || operator == "10") {
                    continue;// is null、is not null don't need param
                } else {
                    names.push(array[2]);
                    nullables.push(array[3]);
                    sensitives.push(array[4]);
                }
            }
        }

        var i = 0;
        while ((result = regexIndex.exec(content))) { // 按照java风格解析
            if (count == i && crud_option == "update") {
                break;
            }
            i++;
            var name = names.shift();
            var nullable = nullables.shift();
            nullable = nullable != undefined ? nullable : "false";
            nullable = nullable != "false" ? "checked='checked'" : "";
            var sensitive = sensitives.shift();
            sensitive = sensitive != undefined ? sensitive : "false";
            sensitive = sensitive != "false" ? "checked='checked'" : "";
            if (name != null && name != "") {
                html = html + sprintf(variableHtml_ForJAVA, name, nullable, sensitive) + "</div><br/>";
            } else {
                html = html + sprintf(variableHtml_ForJAVA, sprintf("param%s", i), nullable, sensitive) + "</div><br/>";
            }
        }
        return html;
    };

    var variableHtml_ForJAVA_Insert = "<div class='row-fluid'><input type='text' class='span3' value='%s' disabled>"
        + " <input type='checkbox' style='display:none'>&nbsp;&nbsp;参数敏感：<input type='checkbox' %s ></div><br/>";

    var step2_2_1_java_insert = function (record, current, crud_option) {
        var html = "";
        var names = new Array();
        var nullables = new Array();
        var sensitives = new Array();
        if ($("#page1").attr("is_update") == "1") {
            // 模式： Age,6,aa,nullable,sensitive;Name,1,param2,nullable,sensitive;
            var conditions = record.condition.split(";");
            for (var j = 0; j < conditions.length; j++) {
                var condition = conditions[j];
                var array = condition.split(",");
                names.push(array[2]);
                nullables.push(array[3]);
                sensitives.push(array[4]);
            }
        }

        var fields = $("#fields").multipleSelect("getSelects");
        for (var i = 0; i < fields.length; i++) {
            var name = names.shift();
            var sensitive = sensitives.shift();
            sensitive = sensitive != undefined ? sensitive : "false";
            sensitive = sensitive != "false" ? "checked='checked'" : "";
            if (name != null && name != "" && name != "undefined") {
                html = html + sprintf(variableHtml_ForJAVA_Insert, name, sensitive);
            } else {
                html = html + sprintf(variableHtml_ForJAVA_Insert, fields[i], sensitive);
            }
        }
        return html;
    };

    var step2_2_1_csharp = function (record, current, crud_option) {
        // 解析Sql语句，提取出参数
        var regexNames = /[@:](\w+)/igm;
        var content = ace.edit("sql_builder").getValue(), result;
        var html = "";

        var count = step2_2_1_getSelectedConditionParamCount();
        var names = new Array();
        var nullables = new Array();
        if ($("#page1").attr("is_update") == "1") {
            // 模式： Age,6,aa,bb;Name,1,param2;
            var conditions = record.condition.split(";");
            for (var j = 0; j < conditions.length; j++) {
                var condition = conditions[j];
                var array = condition.split(",");
                if (array.length == 1) {
                    continue;
                }
                // Between类型的操作符需要特殊处理
                if (array[1] == "6") {
                    names.push(array[2]);
                    names.push(array[3]);
                    nullables.push(array[4]);
                    nullables.push(array[4]);
                } else if (array[1] == "9" || array[1] == "10") {
                    continue;// is null、is not null don't need param
                } else {
                    names.push(array[2]);
                    nullables.push(array[3]);
                }
            }
        }

        var sqlTemp = content;
        var namesStack = new Array();
        while ((result = regexNames.exec(sqlTemp))) {
            if (crud_option == "update") {
                namesStack.push(result[1]);
            }
        }
        var delCount = namesStack.length - count;
        for (var si = 0; si < delCount; si++) {
            namesStack.shift();
        }
        var i = 0;
        while ((result = regexNames.exec(content))) {// 按照c#风格解析
            if (count == i && crud_option == "update") {
                break;
            }
            i++;
            var temp = names.shift();
            var nullable = nullables.shift();
            nullable = nullable != undefined ? nullable : "false";
            nullable = nullable != "false" ? "checked='checked'" : "";
            if (temp != null && temp != "") {
                namesStack.shift();
                html = html + sprintf(variableHtml, temp, nullable) + "</div><br/>";
            } else {
                var realName = "update" == crud_option ? namesStack.shift() : result[1];
                html = html + sprintf(variableHtml, realName, nullable) + "</div><br/>";
            }
        }
        return html;
    };

    var step2_2_2 = function (record, current) {
        var postData = {};
        postData.db_name = $("#databases").val();
        postData.table_name = $("#tables").val();
        postData.crud_type = $("#crud_option").val();
        postData.fields = $('#fields').multipleSelect("getSelects").join(",");
        postData.pagination = $("#auto_sql_pagination").is(":checked");

        var paramValues = [];
        $.each($("#param_list_auto").children("div"), function (i, n) {
            var first = $(n).children("input").eq(0);
            paramValues.push($(first).val());
        });
        var selectedConditions = [];
        var count = 0;
        $.each($("#selected_condition option"), function (i, n) {
            var temp = $(n).val().split(",");
            if (temp.length == 1) {
                return; // equals continue
            }
            if (temp[1] == "6") {// between
                selectedConditions.push(sprintf("%s,%s,%s,%s", temp[0], temp[1], paramValues[count], paramValues[count + 1]));
                count += 2;
            } else if (temp[1] == "9" || temp[1] == "10") {
                // is null 、is not null do not get mock value
            } else {
                selectedConditions.push(sprintf("%s,%s,%s", temp[0], temp[1], paramValues[count]));
                count++;
            }
        });

        postData.condition = selectedConditions.join(";");
        var mockValueHtml = "<div class='row-fluid'>"
            + "<label class='control-label popup_label span3 text-right'>&nbsp;%s&nbsp;</label>"
            + "<input type='text' class='span4' value='%s'>"
            + "</div><br>";
        $("#auto_sql_mock_value").html(" ");
        $.post("/rest/task/auto/getMockValue", postData, function (data) {
            if (data.code == "OK") {
                var mockValue = $.parseJSON(data.info);
                var paramNameList = getAutoSqlParamName();
                $.each(mockValue, function (index, value) {
                    $("#auto_sql_mock_value").append(sprintf(mockValueHtml, paramNameList[index], value));
                });

                var editor = ace.edit("step2_2_3_sql_editor");
                editor.setTheme("ace/theme/monokai");
                editor.getSession().setMode("ace/mode/sql");
                editor.setValue(ace.edit("step2_2_2_sql_editor").getValue());
                editor.setReadOnly(true);
                current.hide();
                $(".step2-2-3").show();
            } else {
                $.showMsg("error_msg", data.info);
            }
        }).fail(function (data) {
            alert("获取Mock Value出错！");
        });
    };

    var getAutoSqlParamName = function () {
        var paramList = [];
        var crudOption = $("#crud_option").val();
        if (crudOption == "select" || crudOption == "delete") {
            $.each($("#param_list_auto").children("div"), function (index, value) {
                var first = $(value).children("input").eq(0);
                paramList.push($(first).val());
            });
        } else if (crudOption == "insert") {
            paramList = $("#fields").multipleSelect("getSelects");
        } else if (crudOption == "update") {
            paramList = $("#fields").multipleSelect("getSelects");
            $.each($("#param_list_auto").children("div"), function (index, value) {
                var first = $(value).children("input").eq(0);
                paramList.push($(first).val());
            });
        }
        return paramList;
    };

    var step2_2_3 = function (record, current) {
        var postData = {};
        postData.db_name = $("#databases").val();
        postData.table_name = $("#tables").val();
        postData.crud_type = $("#crud_option").val();
        postData.fields = $('#fields').multipleSelect("getSelects").join(",");

        var paramValues = [];
        $.each($("#param_list_auto").children("div"), function (index, value) {
            var first = $(value).children("input").eq(0);
            paramValues.push($(first).val());
        });

        var selectedConditions = [];
        var count = 0;
        $.each($("#selected_condition option"), function (i, n) {
            var temp = $(n).val().split(",");
            if (temp.length == 1) {
                return;
            }
            if (temp[1] == "6") {// between
                selectedConditions.push(sprintf("%s,%s,%s,%s", temp[0], temp[1], paramValues[count], paramValues[count + 1]));
                count += 2;
            } else if (temp[1] == "9" || temp[1] == "10") {
                // is null 、is not null do not have mock value
            } else {
                selectedConditions.push(sprintf("%s,%s,%s", temp[0], temp[1], paramValues[count]));
                count++;
            }
        });
        postData.condition = selectedConditions.join(";");
        postData.sql_content = ace.edit("sql_builder").getValue();

        if (postData.crud_type == "select") {
            postData.pagination = $("#auto_sql_pagination").is(":checked");
        } else {
            postData.pagination = false;
        }

        var mockValues = [];
        $.each($("#auto_sql_mock_value").children("div"), function (index, value) {
            var first = $(value).children("input").eq(0);
            mockValues.push($(first).val());
        });
        postData["mockValues"] = mockValues.join(";");
        $("#auto_sql_validate_result").empty();
        $.post("/rest/task/auto/sqlValidate", postData, function (data) {
            if (data.code == "OK") {
                $("#error_msg").empty();
                if ($("#crud_option").val() == "select" && $(".step2-2-1").attr("dbCatalog") == "MySql") {
                    $("#auto_sql_validate_result").html(data.info + ". And below is the sql execution plan.");

                    var explanJson = $.parseJSON(data.explanJson);
                    $("#auto_select_type").html(explanJson[0]["select_type"]);
                    $("#auto_type").html(explanJson[0]["type"]);
                    $("#auto_possible_keys").html(explanJson[0]["possible_keys"]);
                    $("#auto_key").html(explanJson[0]["key"]);
                    $("#auto_rows").html(explanJson[0]["rows"]);
                    $("#auto_extra").html(explanJson[0]["extra"]);
                    $("#auto_sql_validate_result_div > table").show();
                } else {
                    $("#auto_sql_validate_result").html(data.info);
                    $("#auto_sql_validate_result_div > table").hide();
                }

                var editor = ace.edit("step2_2_4_sql_editor");
                editor.setTheme("ace/theme/monokai");
                editor.getSession().setMode("ace/mode/sql");
                editor.setValue(ace.edit("step2_2_2_sql_editor").getValue());
                editor.setReadOnly(true);
                current.hide();
                $(".step2-2-4").show();
            } else {
                $.showMsg("error_msg", "SQL测试执行异常，请检查sql及对应参数！" + data.info);
            }
        }).fail(function (data) {
            alert("保存出错！");
        });
    };

    var step2_2_4 = function () {
        window.ajaxutil.post_task();
    };

    var step2_3_1 = function (record, current) {
        var sql_method_name = $.trim($("#sql_method_name").val());
        if (sql_method_name == null || sql_method_name.length == 0) {
            $("#error_msg").html("请输入方法名");
            return;
        }
        current.hide();
        $(".step2-3-2").show();
        if ($("#sql_style").val() == "csharp") {
            $("#free_sql_scalarType option[value='Single']").hide();
        } else {
            $("#free_sql_scalarType option[value='Single']").show();
        }
    };

    var existKeyword_Nolock = function () {
        if ($("#free_sql_crud_option").val() == "select"
            && ace.edit("sql_editor").getValue().toLowerCase().indexOf("nolock") == -1
            && $(".step2-2-1").attr("dbCatalog") != "MySql") {
            $.showMsg("error_msg", "select语句中必须含有with (nolock)");
            return false;
        }
        return true;
    };

    var step2_3_2 = function (record, current) {
        if (!existKeyword_Nolock()) {
            return;
        }
        if ($("#sql_class_name").val() == "") {
            $("#error_msg").html("请输入DAO类名.");
            return;
        }
        if (checkDaoNameConflict($("#sql_class_name").val())) {
            return;
        }
        // 首先解析Sql语句，提取出参数
        var regexIndex = /(\?{1})/igm;
        var regexNames = /[@:](\w+)/igm;
        var sqlContent = ace.edit("sql_editor").getValue(), result;
        var htmls = "";
        var i = 0;

        var conVal = new Array();
        /*
         if ($("#page1").attr("is_update") == "1") {
         var splitedParams = record.parameters.split(";");
         $.each(splitedParams, function (index, value) {
         var resultParams = value.split(",");
         conVal.push(resultParams[0]);
         });
         }
         */

        while ((result = regexIndex.exec(sqlContent))) {
            i++;
            var temp = conVal.shift();
            if ($("#sql_style").val() == "csharp") {
                if (temp != null && temp != "") {
                    htmls = htmls + sprintf(variableHtmlOrigin, temp) + sprintf(variable_typesHtml, sprintf("id='db_type_%s'", sprintf("param%s", i)));
                } else {
                    htmls = htmls + sprintf(variableHtmlOrigin, sprintf("param%s", i)) + sprintf(variable_typesHtml, sprintf("id='db_type_%s'", sprintf("param%s", i)));
                }
            } else {
                if (temp != null && temp != "") {
                    htmls = htmls + sprintf(variableHtmlOrigin, temp) + sprintf(variable_typesHtml_ForJAVA, sprintf("id='db_type_%s'", sprintf("param%s", i)));
                } else {
                    htmls = htmls + sprintf(variableHtmlOrigin, sprintf("param%s", i)) + sprintf(variable_typesHtml_ForJAVA, sprintf("id='db_type_%s'", sprintf("param%s", i)));
                }
            }
        }

        if (htmls.length == 0) {
            var paramArray = new Array();
            while ((result = regexNames.exec(sqlContent))) {
                i++;
                var temp = conVal.shift();
                if ($("#sql_style").val() == "csharp") {
                    if (temp != null && temp != "") {
                        if ($.inArray(temp, paramArray) == -1) {
                            paramArray.push(temp);
                        }

                        htmls = htmls + sprintf(variableHtmlOrigin, temp) + sprintf(variable_typesHtml, sprintf("id='db_type_%s'", sprintf("param%s", i)));
                    } else {
                        var realName = result[1];
                        if ($.inArray(realName, paramArray) > -1) {
                            continue;
                        }
                        else {
                            paramArray.push(realName);
                        }

                        htmls = htmls + sprintf(variableHtmlOrigin, realName) + sprintf(variable_typesHtml, sprintf("id='db_type_%s'", realName));
                    }
                } else {
                    if (temp != null && temp != "") {
                        htmls = htmls + sprintf(variableHtmlOrigin, temp) + sprintf(variable_typesHtml_ForJAVA, sprintf("id='db_type_%s'", sprintf("param%s", i)));
                    } else {
                        var realName = result[1];
                        htmls = htmls + sprintf(variableHtmlOrigin, realName) + sprintf(variable_typesHtml_ForJAVA, sprintf("id='db_type_%s'", realName));
                    }
                }
            }
        }

        if (htmls.length == 0) {
            $("#param_list_free_div").hide();
            $("#param_list").empty();
        } else {
            $("#param_list_free_div").show();
            $("#param_list").html(htmls);
        }

        if ($("#sql_style").val() == "csharp") {
            $("#customJavaHints").hide();
        }

        if ($("#page1").attr("is_update") == "1") {
            splitedParams = record.parameters.split(";");
            $.each(splitedParams, function (index, value) {
                if (index <= i) {
                    var resultParams = value.split(",");
                    var paramIndex = index + 1;
                    $("#db_type_param" + paramIndex).val(resultParams[1]);
                    if ($("#sql_style").val() != "csharp") {
                        var chk = $("#db_type_param" + paramIndex).siblings("div").children(":checkbox");
                        var sensitive = resultParams[2];
                        sensitive = sensitive != undefined ? sensitive : "false";
                        if (sensitive == "false") {
                            chk.prop("checked", false);
                        }
                    }
                }
            });

            if (record.hints != undefined && record.hints.length > 0) {
                var array = record.hints.split(";");
                if (array != undefined && array.length > 0) {
                    // clear checkboxes
                    var cbHints = $("#customJavaHints :checkbox");
                    if (cbHints != undefined && cbHints.length > 0) {
                        $.each(cbHints, function (i, n) {
                            $(cbHints[i]).prop("checked", false);
                        });
                    }
                    $.each(array, function (i, n) {
                        $("#chk_custom_" + n).prop("checked", true);
                    });
                }
            }
        }

        window.sql_builder.buildPagingSQL(function () {
            $("#error_msg").html(" ");
            current.hide();
            $(".step2-3-3").show();
        });
    };

    var step2_3_3 = function (record, current) {
        if (checkDuplicateParamName()) {
            return;
        }
        var postData = {};
        var paramList = [];
        $.each($("#param_list").children("div"), function (index, value) {
            var first = $(value).children("input").eq(0);
            var second = $(value).children("select").eq(0);
            paramList.push(sprintf("%s,%s", $(first).val(), $(second).val()));
        });
        postData.params = paramList.join(";");

        var mockValueHtml = "<div class='row-fluid'>"
            + "<label class='control-label popup_label span3 text-right'>&nbsp;%s&nbsp;</label>"
            + "<input type='text' class='span4' value='%s'>"
            + "</div><br>";

        $("#free_sql_mock_value").html(" ");
        $.post("/rest/task/sql/getMockValue", postData, function (data) {
            if (data.code == "OK") {
                var mockValue = $.parseJSON(data.info);
                var paramNameList = getFreeSqlParamName();
                $.each(mockValue, function (index, value) {
                    $("#free_sql_mock_value").append(sprintf(mockValueHtml, paramNameList[index], value));
                });
                var editor = ace.edit("step2_3_4_sql_editor");
                editor.setTheme("ace/theme/monokai");
                editor.getSession().setMode("ace/mode/sql");
                editor.setValue(ace.edit("step2_3_1_sql_editor").getValue());
                editor.setReadOnly(true);
                current.hide();
                $(".step2-3-4").show();
            } else {
                $.showMsg("error_msg", data.info);
            }
        }).fail(function (data) {
            $.showMsg("error_msg", "获取Mock Value出错！请检查参数类型选择是否正确!");
        });
    };

    var getFreeSqlParamName = function () {
        var paramList = [];
        $.each($("#param_list").children("div"), function (index, value) {
            var first = $(value).children("input").eq(0);
            paramList.push($(first).val());
        });
        return paramList;
    };

    var checkDuplicateParamName = function () {
        $("#error_msg").html(" ");
        var paramName = [];
        var msg = [];
        $.each($("#param_list input[type='text']"), function (index, value) {
            $.each(paramName, function (index, name) {
                if ($(value).val() == name) {
                    msg.push($(value).val());
                }
            });
            paramName.push($(value).val());
        });
        if (msg.length > 0) {
            $("#error_msg").html("以下参数名重复,请重新命名.<br/>" + msg.join(",") + " ");
            return true;
        }
        return false;
    };

    var step2_3_4 = function (record, current) {
        var crudOption = $("#free_sql_crud_option").val();
        var postData = {};
        postData.db_name = $("#databases").val();
        postData.crud_type = crudOption;
        postData.sql_content = ace.edit("sql_editor").getValue();

        var paramList = [];
        $.each($("#param_list").children("div"), function (index, value) {
            var first = $(value).children("input").eq(0);
            var second = $(value).children("select").eq(0);
            paramList.push(sprintf("%s,%s", $(first).val(), $(second).val()));
        });
        postData.params = paramList.join(";");

        if (crudOption == "select") {
            postData.pagination = $("#free_sql_pagination").is(":checked");
        } else {
            postData.pagination = false;
        }

        var mockValues = [];
        $.each($("#free_sql_mock_value").children("div"), function (index, value) {
            var first = $(value).children("input").eq(0);
            mockValues.push($(first).val());
        });
        postData.mockValues = mockValues.join(";");

        $("#free_sql_validate_result").empty();
        $.post("/rest/task/sql/sqlValidate", postData).done(function (data) {
            if (data.code == "OK") {
                $("#error_msg").empty();
                if ($("#free_sql_crud_option").val() == "select" && $(".step2-2-1").attr("dbCatalog") == "MySql") {
                    $("#free_sql_validate_result").html(data.info + ". And below is the sql execution plan.");

                    var explanJson = $.parseJSON(data.explanJson);
                    $("#free_select_type").html(explanJson[0]['select_type']);
                    $("#free_type").html(explanJson[0]['type']);
                    $("#free_possible_keys").html(explanJson[0]['possible_keys']);
                    $("#free_key").html(explanJson[0]['key']);
                    $("#free_rows").html(explanJson[0]['rows']);
                    $("#free_extra").html(explanJson[0]['extra']);
                    $("#free_sql_validate_result_div > table").show();
                } else {
                    $("#free_sql_validate_result").html(data.info);
                    $("#free_sql_validate_result_div > table").hide();
                }
                var editor = ace.edit("step2_3_5_sql_editor");
                editor.setTheme("ace/theme/monokai");
                editor.getSession().setMode("ace/mode/sql");
                editor.setValue(ace.edit("step2_3_1_sql_editor").getValue());
                editor.setReadOnly(true);
                current.hide();
                $(".step2-3-5").show();
            } else {
                $.showMsg("error_msg", "SQL测试执行异常，请检查sql及对应参数！" + data.info);
            }
        }).fail(function (data) {
            alert("执行异常，请检查sql及对应参数！");
        });
    };

    var step2_3_5 = function () {
        window.ajaxutil.post_task();
    };

    wizzard.prototype = {
        clear: function () {
            $("#comment").val("");
            $("#error_msg").html("");

            $("#prefix").val("");
            // 默认后缀
            $("#suffix").val("");

            $("#sql_class_name").val("");
            $("#sql_pojo_name").val("");
            $("#sql_method_name").val("");

            var sql_builder = ace.edit("sql_builder");
            sql_builder.setTheme("ace/theme/monokai");
            sql_builder.getSession().setMode("ace/mode/sql");
            sql_builder.setValue(null);

            $("#method_name").val("");

            var editor = ace.edit("sql_editor");
            editor.setTheme("ace/theme/monokai");
            editor.getSession().setMode("ace/mode/sql");
            editor.setValue(null);

            $("#free_sql_pagination").prop("checked", false);
            $("#auto_sql_pagination").prop("checked", false);
        },
        next: function (current) {
            // 首先获取当前Grid选中的行,records是id数组
            var records = w2ui["grid"].getSelection();
            var record = null;
            if (records.length > 0) {
                record = w2ui["grid"].get(records[0]);
            }

            $("#error_msg").css("color", "red");

            // 向导首先显示所有数据库服务器，点击下一步后，获取此服务器所有的数据库列表
            if (current.hasClass("step1")) {
                step1(record, current);
            } else if (current.hasClass("step2-1")) {
                step2_1(record, current);
            } else if (current.hasClass("step2-1-2")) {
                step2_1_2();
            } else if (current.hasClass("step2-2")) {
                step2_2(record, current);
            } else if (current.hasClass("step2-2-1")) {
                step2_2_1(record, current);
            } else if (current.hasClass("step2-2-2")) {
                step2_2_2(record, current);
            } else if (current.hasClass("step2-2-3")) {
                step2_2_3(record, current);
            } else if (current.hasClass("step2-2-4")) {
                step2_2_4();
            } else if (current.hasClass("step2-3-1")) {
                step2_3_1(record, current);
            } else if (current.hasClass("step2-3-2")) {
                step2_3_2(record, current);
            } else if (current.hasClass("step2-3-3")) {
                step2_3_3(record, current);
            } else if (current.hasClass("step2-3-4")) {
                step2_3_4(record, current);
            } else if (current.hasClass("step2-3-5")) {
                step2_3_5();
            }
        },
        previous: function (current) {
            $("#error_msg").text("");
            if (current.hasClass("step1")) {
                return;
            }
            current.hide();

            if (current.hasClass("step2-1") || current.hasClass("step2-2") || current.hasClass("step2-3-1")) {
                $(".step1").show();
            } else if (current.hasClass("step2-1-2")) {
                $(".step2-1").show();
            } else if (current.hasClass("step2-2-1")) {
                $(".step2-2").show();
            } else if (current.hasClass("step2-2-2")) {
                $(".step2-2-1").show();
            } else if (current.hasClass("step2-2-3")) {
                $(".step2-2-2").show();
            } else if (current.hasClass("step2-2-4")) {
                $(".step2-2-3").show();
            } else if (current.hasClass("step2-3-2")) {
                $(".step2-3-1").show();
            } else if (current.hasClass("step2-3-3")) {
                $(".step2-3-2").show();
            } else if (current.hasClass("step2-3-4")) {
                $(".step2-3-3").show();
            } else if (current.hasClass("step2-3-5")) {
                $(".step2-3-4").show();
            }
        }
    };

    window.wizzard = new wizzard();
})(window);
