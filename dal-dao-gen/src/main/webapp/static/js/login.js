(function ($, window, undefined) {
    function checkSetupDb() {
        cblock($("body"));
        $.get("/rest/setupDb/setupDbCheck", {rand: Math.random()}).done(function (data) {
            if (data.code != "OK") {
                $("#setup_error_msg").html("");
                $("#setup_db_step1").show();
                $("#setup_db_step2").hide();
                $("#setup_conn_test").show();
                $("#setup_db_next").show();
                $("#setup_db_prev").hide();
                $("#setup_db_save").hide();
                if ($("#setupdbcatalog")[0] != undefined && $("#setupdbcatalog")[0].selectize != undefined) {
                    $("#setupdbcatalog")[0].selectize.clearOptions();
                } else {
                    $("#setupdbcatalog").selectize({
                        valueField: "id",
                        labelField: "title",
                        searchField: "title",
                        sortField: "title",
                        options: [],
                        create: false
                    });
                }

                if (data.info == "!valid") {
                    $("#setupDbModal").modal({
                        "backdrop": "static"
                    });
                }
            } else {
                if (data.info == "initialized") {
                    window.location.href = "login.jsp";
                }
            }
            $("body").unblock();
        });
    }

    function connectionTest(successInfo) {
        $("#setup_error_msg").html("正在连接，请稍等...");
        var dbType = $("#setupdbtype").val();
        var dbAddress = $("#setupdbaddress").val();
        var dbPort = $("#setupdbport").val();
        var dbUser = $("#setupdbuser").val();
        var dbPassword = $("#setupdbpassword").val();
        cblock($("body"));
        $.post("/rest/setupDb/connectionTest", {
            dbtype: dbType,
            dbaddress: dbAddress,
            dbport: dbPort,
            dbuser: dbUser,
            dbpassword: dbPassword,
            rand: Math.random()
        }, function (data) {
            if (data.code == "OK") {
                var allCatalog = [];
                $.each($.parseJSON(data.info), function (index, value) {
                    allCatalog.push({
                        id: value,
                        title: value
                    });
                });
                $("#setupdbcatalog")[0].selectize.clearOptions();
                $("#setupdbcatalog")[0].selectize.addOption(allCatalog);
                $("#setupdbcatalog")[0].selectize.refreshOptions(false);
                $("#setup_error_msg").html(successInfo);
            } else {
                $("#setup_error_msg").html(data.info);
            }
            $("body").unblock();
        }).fail(function (data) {
            $("#setup_error_msg").html(data);
            $("body").unblock();
        });
    }

    function checkTableConsistent() {
        var loading = $("#loading");
        loading.show();
        var disabled = "disabled";
        var prev = $("#setup_db_prev");
        var save = $("#setup_db_save");
        prev.attr(disabled, disabled);
        save.attr(disabled, disabled);
        var using = $("#setup_db_use");

        $.post("/rest/setupDb/tableConsistentCheck", {
                dbaddress: $("#setupdbaddress").val(),
                dbport: $("#setupdbport").val(),
                dbuser: $("#setupdbuser").val(),
                dbpassword: $("#setupdbpassword").val(),
                dbcatalog: $("#setupdbcatalog").val(),
                rand: Math.random()
            },
            function (data) {
                loading.hide();
                prev.removeAttr(disabled);
                save.removeAttr(disabled);
                if (data.code == "OK") {
                    //confirm

                    using.show();
                }
                else {
                    using.hide();
                }
            });
    }

    function usingCurrentCatalog() {
        cblock($("body"));
        $.post("/rest/setupDb/initializeDal", {
            dbaddress: $("#setupdbaddress").val(),
            dbport: $("#setupdbport").val(),
            dbuser: $("#setupdbuser").val(),
            dbpassword: $("#setupdbpassword").val(),
            dbcatalog: $("#setupdbcatalog").val(),
            rand: Math.random()
        }, function (data) {
            if (data.code == "OK") {
                window.location.href = "login.jsp";
            } else {
                $("#setup_error_msg").html(data);
            }
            $("body").unblock();
        });
    }

    function dbNext() {
        var dbAddress = $("#setupdbaddress").val();
        var dbPort = $("#setupdbport").val();
        var dbUser = $("#setupdbuser").val();
        var dbPassword = $("#setupdbpassword").val();

        if (dbAddress == null || dbAddress.length == 0) {
            $("#setup_error_msg").html("请输入数据库地址!");
            return false;
        }
        if (dbPort == null || dbPort.length == 0) {
            $("#setup_error_msg").html("请输入数据库端口!");
            return false;
        }
        if (dbUser == null || dbUser.length == 0) {
            $("#setup_error_msg").html("请输入数据库登录用户!");
            return false;
        }
        if (dbPassword == null || dbPassword.length == 0) {
            $("#setup_error_msg").html("请输入数据库登录用户密码!");
            return false;
        }
        $("#setup_db_step1").hide();
        $("#setup_db_step2").show();
        $("#setup_conn_test").hide();
        $("#setup_db_next").hide();
        $("#setup_db_prev").show();
        $("#setup_db_save").show();
        connectionTest("");
    }

    function saveDb() {
        var dbAddress = $("#setupdbaddress").val();
        var dbPort = $("#setupdbport").val();
        var dbUser = $("#setupdbuser").val();
        var dbPassword = $("#setupdbpassword").val();
        var dbCatalog = $("#setupdbcatalog").val();
        var groupName = $("#setupdbgroupname").val();
        var groupComment = $("#setupdbcomment").val();
        var adminNo = $("#setupdbadminno").val();
        var adminName = $("#setupdbadminname").val();
        var adminEmail = $("#setupdbadminemail").val();
        var adminPass = $("#setupdbadminpass").val();

        if (dbAddress == null || dbAddress.length == 0) {
            $("#setup_error_msg").html("请输入数据库地址!");
            return false;
        }
        if (dbPort == null || dbPort.length == 0) {
            $("#setup_error_msg").html("请输入数据库端口!");
            return false;
        }
        if (dbUser == null || dbUser.length == 0) {
            $("#setup_error_msg").html("请输入数据库登录用户!");
            return false;
        }
        if (dbPassword == null || dbPassword.length == 0) {
            $("#setup_error_msg").html("请输入数据库登录用户密码!");
            return false;
        }
        if (dbCatalog == null || dbCatalog.length == 0) {
            $("#setup_error_msg").html("请选择数据库!");
            return false;
        }
        if (groupName == null || groupName.length == 0) {
            $("#setup_error_msg").html("请输入组名!");
            return false;
        }
        if (groupComment == null || groupComment.length == 0) {
            $("#setup_error_msg").html("请输入备注!");
            return false;
        }
        if (adminNo == null || adminNo.length == 0) {
            $("#setup_error_msg").html("请输入管理员工号!");
            return false;
        }
        if (adminName == null || adminName.length == 0) {
            $("#setup_error_msg").html("请输入管理员姓名!");
            return false;
        }
        if (adminEmail == null || adminEmail.length == 0) {
            $("#setup_error_msg").html("请输入管理员邮件地址!");
            return false;
        }
        if (adminPass == null || adminPass.length == 0) {
            $("#setup_error_msg").html("请输入管理员密码!");
            return false;
        }

        var disabled = "disabled";
        var prev = $("#setup_db_prev");
        var save = $("#setup_db_save");
        prev.attr(disabled, disabled);
        save.attr(disabled, disabled);

        $("#setup_error_msg").html("正在初始化，请稍候...");
        cblock($("body"));
        $.post("/rest/setupDb/initializeDb", {
            dbaddress: dbAddress,
            dbport: dbPort,
            dbuser: dbUser,
            dbpassword: dbPassword,
            dbcatalog: dbCatalog,
            groupName: groupName,
            groupComment: groupComment,
            adminNo: adminNo,
            adminName: adminName,
            adminEmail: adminEmail,
            adminPass: adminPass,
            rand: Math.random()
        }, function (data) {
            if (data.code == "OK") {
                $("#setup_error_msg").html("初始化成功。");
                window.location.href = "login.jsp";
            } else {
                $("#setup_error_msg").html(data.info);
                prev.removeAttr(disabled);
                save.removeAttr(disabled);
            }
            $("body").unblock();
        }).fail(function (data) {
            $("#setup_error_msg").html(data);
            prev.removeAttr(disabled);
            save.removeAttr(disabled);
            $("body").unblock();
        });
    }

    function showTooltip(obj, text) {
        obj.tooltip("hide");
        obj.attr("data-original-title", "");
        obj.attr("data-original-title", text);
        obj.tooltip("show");
        obj.focus();
    }

    function checkInput(obj) {
        if ($.trim(obj.val()).length == 0) {
            showTooltip(obj, obj.attr("placeholder") + "不能为空");
            return false;
        }
        return true;
    }

    function processCookies() {
        if ($("#user_remember_me").is(":checked")) {
            Cookies.set("userno", $("#user_login").val(), {expires: 7});
        } else {
            Cookies.remove('userno');
        }
    }

    function signIn() {
        var userNo = $("#user_login");
        if (!checkInput(userNo)) {
            return;
        }
        var password = $("#user_password");
        if (!checkInput(password)) {
            return;
        }

        var parameters = {
            userNo: userNo.val(),
            password: password.val()
        };
        $.post("/rest/user/signin", parameters, function (data) {
            if (data.code == "OK") {
                processCookies();
                window.location.href = "index.jsp";
            } else if (data.code == "Error") {
                showTooltip(userNo, data.info);
            }
        });
    }

    function signUp() {
        var userNo = $("#user_no_sign_up");
        if (!checkInput(userNo)) {
            return;
        }
        var userName = $("#user_name_sign_up");
        if (!checkInput(userName)) {
            return;
        }
        var email = $("#user_email_sign_up");
        if (!checkInput(email)) {
            return;
        }
        var password = $("#user_password_sign_up");
        if (!checkInput(password)) {
            return;
        }
        var values = {
            userNo: userNo.val()
        };

        $.post("/rest/user/exist", values, function (data) {
            if (data.code == "OK") {
                var parameters = {
                    userNo: userNo.val(),
                    userName: userName.val(),
                    userEmail: email.val(),
                    password: password.val()
                };
                $.post("/rest/user/signup", parameters, function (result) {
                    if (result.code == "OK") {
                        window.location.href = "index.jsp";
                    } else if (result.code == "Error") {
                        showTooltip(userNo, result.info);
                    }
                });
            } else if (data.code == "Error") {
                showTooltip(userNo, data.info);
            }
        });
    }

    $(function () {
        $(document.body).on("keydown", function (event) {
            if (event.keyCode == 13) {
                var isEmpty = $("#user_password_sign_up").val().length > 0;
                if (isEmpty == true) {
                    signUp();
                }
                else {
                    signIn();
                }
            }
        });

        var cookieUserNo = Cookies.get("userno");
        if (cookieUserNo != undefined) {
            $("#user_login").val(cookieUserNo);
            $("#user_remember_me").attr("checked", true);
        }

        var loading = $("#loading");
        loading.hide();
        var using = $("#setup_db_use");
        using.hide();

        $(document.body).on("click", "#setup_conn_test", function () {
            connectionTest("连接成功。");
        });

        $(document.body).on("click", "#setup_db_prev", function () {
            $("#setup_db_step1").show();
            $("#setup_db_step2").hide();
            $("#setup_conn_test").show();
            $("#setup_db_next").show();
            $("#setup_db_prev").hide();
            $("#setup_db_save").hide();
            $("#setup_error_msg").html("");
        });

        $(document.body).on("click", "#setup_db_next", function () {
            connectionTest("");
            dbNext();
        });

        $(document.body).on("click", "#setup_db_save", function () {
            saveDb();
        });

        $(document.body).on("click", "#signin", function () {
            signIn();
        });

        $(document.body).on("click", "#signup", function () {
            signUp();
        });

        $(document.body).on("change", "#setupdbcatalog", function () {
            checkTableConsistent();
        });

        $(document.body).on("click", "#setup_db_use", function () {
            usingCurrentCatalog();
        });

        checkSetupDb();
    });
})(jQuery, window);