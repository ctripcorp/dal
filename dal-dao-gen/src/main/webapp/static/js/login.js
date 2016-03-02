(function(window, undefined) {
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
			Cookies.set("userno", $("#user_login").val(), {
				expires : 7
			});
		} else {
			Cookies.remove('userno');
		}
	}

	$(function() {
		var cookieUserNo = Cookies.get("userno");
		if (cookieUserNo != undefined) {
			$("#user_login").val(cookieUserNo);
			$("#user_remember_me").attr("checked", true);
		}

		$("#signin").click(function() {
			var userNo = $("#user_login");
			if (!checkInput(userNo)) {
				return;
			}
			var password = $("#user_password");
			if (!checkInput(password)) {
				return;
			}

			var parameters = {
				userNo : userNo.val(),
				password : password.val()
			};
			$.post("/rest/user/signin", parameters, function(data) {
				if (data.code == "OK") {
					processCookies();
					window.location.href = "index.jsp";
				} else if (data.code == "Error") {
					showTooltip(userNo, data.info);
				}
			});
		});

		$("#signup").click(function() {
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
				userNo : userNo.val()
			};

			$.post("/rest/user/exist", values, function(data) {
				if (data.code == "OK") {
					var parameters = {
						userNo : userNo.val(),
						userName : userName.val(),
						userEmail : email.val(),
						password : password.val()
					};
					$.post("/rest/user/signup", parameters, function(result) {
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
		});
	});
})(window);