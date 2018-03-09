(function ($, window, document, undefined) {
    function checkDefaultUser() {
        cblock($("body"));
        $.get("/rest/user/isDefaultUser", {rand: Math.random()}, function (data) {
            if (data == "true") {
                $("#menu_password").show();
            }
            $("body").unblock();
        });
    }

    function checkDefaultSuperUser() {
        cblock($("body"));
        $.get("/rest/user/isDefaultSuperUser", {rand: Math.random()}, function (data) {
            if (data == "true") {
                $("#usermanagejsp").show();
            }
            $("body").unblock();
        });
    }

    function changePassword() {
        $("#oldPassword").val("");
        $("#newPassword").val("");
        $("#confirmPassword").val("");
        $("#password_error_msg").html("");
        $("#passwordModal").modal({"backdrop": "static"});
    }

    function savePassword() {
        var errorMsg = $("#password_error_msg");
        var oldPassword = $("#oldPassword");
        var oldPasswordVal = $.trim(oldPassword.val());
        if (oldPasswordVal.length == 0) {
            errorMsg.html("请输入旧密码!");
            oldPassword.focus();
            return false;
        }
        cblock($("body"));
        $.ajax({
            type: "POST",
            url: "/rest/user/checkPassword",
            data: {password: oldPasswordVal, rand: Math.random()},
            async: false,
            success: function (data) {
                if (data == "false") {
                    errorMsg.html("密码不正确!");
                    oldPassword.focus();
                    return false;
                }
                $("body").unblock();
            }
        });

        var newPassword = $("#newPassword");
        var newPasswordVal = $.trim(newPassword.val());
        if (newPasswordVal.length == 0) {
            errorMsg.html("请输入新密码!");
            newPassword.focus();
            return false;
        }

        var confirmPassword = $("#confirmPassword");
        var confirmPasswordVal = $.trim(confirmPassword.val());
        if (confirmPasswordVal.length == 0) {
            errorMsg.html("请确认密码!");
            confirmPassword.focus();
            return false;
        }
        if (newPasswordVal != confirmPasswordVal) {
            errorMsg.html("密码不匹配!");
            confirmPassword.focus();
            return false;
        }

        var result = false;
        cblock($("body"));
        //sync
        $.ajax({
            type: "POST",
            url: "/rest/user/changePassword",
            data: {password: newPasswordVal, rand: Math.random()},
            async: false,
            success: function (data) {
                if (data == "true") {
                    $("#passwordModal").modal("hide");
                    result = true;
                }
            }
        });

        if (result == true) {
            $.post("/rest/user/logOut", {rand: Math.random()}, function () {
                window.location.href = "login.jsp";
            });
        }

        $("body").unblock();
    }

    $(function () {
        var Sys = {};
        var ua = navigator.userAgent.toLowerCase();
        var s;
        var scan;
        (s = ua.match(/msie ([\d.]+)/)) ? Sys.ie = s[1] :
            (s = ua.match(/firefox\/([\d.]+)/)) ? Sys.firefox = s[1] :
                (s = ua.match(/chrome\/([\d.]+)/)) ? Sys.chrome = s[1] :
                    (s = ua.match(/opera.([\d.]+)/)) ? Sys.opera = s[1] :
                        (s = ua.match(/version\/([\d.]+).*safari/)) ? Sys.safari = s[1] : 0;

        // 以下进行测试
        if (Sys.ie) {
            scan = "您使用的ie内核" + Sys.ie + "浏览器，建议您使用chrome浏览器";
            alert(scan);
        }
        if (Sys.firefox) {
            scan = "您使用的是firefox内核" + Sys.firefox + "浏览器，建议您使用chrome浏览器";
            alert(scan);
        }
        if (Sys.chrome) {
            scan = "您使用的是chrome内核" + Sys.chrome + "浏览器，建议您使用chrome浏览器";
        }
        if (Sys.opera) {
            scan = "您使用的是opera内核" + Sys.opera + "浏览器，建议您使用chrome浏览器";
            alert(scan);
        }
        if (Sys.safari) {
            scan = "您使用的是safari内核" + Sys.safari + "浏览器，建议您使用chrome浏览器";
            alert(scan);
        }

        var href = window.location.href;
        $("li[class='active']").removeClass("active");
        if (href.indexOf("codeview") != -1) {
            $("#codeviewjsp").addClass("active");
        } else if (href.indexOf("membermanage") != -1) {
            $("#membermanagejsp").addClass("active");
        } else if (href.indexOf("dbmanage") != -1 || href.indexOf("dbsetsmanage") != -1 || href.indexOf("dbview") != -1) {
            $("#dbmanagejsp").addClass("active");
        } else if (href.indexOf("eventmanage") != -1) {
            $("#eventmanagejsp").addClass("active");
        } else if (href.indexOf("groupmanage") != -1) {
            $("#groupmanagejsp").addClass("active");
        } else if (href.indexOf("usermanage") != -1) {
            $("#usermanagejsp").addClass("active");
        } else {
            $("#indexjsp").addClass("active");
        }

        var options = {
            animation: true,
            trigger: "hover",
            html: true
        };
        $('[data-toggle="tooltip"]').tooltip(options);

        $(document.body).on("click", "#password", function () {
            changePassword();
        });

        $(document.body).on("click", "#change_password", function () {
            savePassword();
        });

        checkDefaultUser();
        checkDefaultSuperUser();

        //jQuery
        $.extend({
            isEmpty: function (data) {
                return data == undefined || data == null || data.length == 0;
            },
            showMsg: function (id, msg) {
                $("#" + id).html(msg);
            }
        });

        window.alert = function (data) {
            $("#overrideAlertErrorNoticeDivMsg").html(data);
            $("#overrideAlertErrorNoticeDiv").modal({"backdrop": "static"});
        };

        var keepSession = function () {
            $.post("/rest/group/keepSession", {id: 1});
        };

        setInterval(keepSession, 20 * 60 * 1000);
    });
})(jQuery, window, document);
