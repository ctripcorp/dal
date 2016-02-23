(function(window, undefined) {
	function checkInput(obj, text) {
		if ($.trim(obj.val()).length == 0) {
			obj.tooltip("hide");
			obj.attr("data-original-title", "");
			obj.attr("data-original-title", text);
			obj.tooltip("show");
			obj.focus();
			return false;
		}
		return true;
	}

	$(function() {
		$("#signin").click(function() {

		});

		$("#signup").click(function() {
			var userNo = $("#user_no_sign_up");
			if (!checkInput(userNo, "工号不能为空")) {
				return;
			}
			var userName = $("#user_name_sign_up");
			if (!checkInput(userName, "姓名不能为空")) {
				return;
			}
			var email = $("#user_email_sign_up");
			if (!checkInput(email, "Email 不能为空")) {
				return;
			}
			var password = $("#user_password_sign_up");
			if (!checkInput(password, "密码不能为空")) {
				return;
			}
			var parameters = {
				userNo : userNo.val()
			};

			$.post("/rest/user/exist", parameters, function(data) {
				if (data.code == "Error") {
					userNo.tooltip("hide");
					userNo.attr("data-original-title", "");
					userNo.attr("data-original-title", data.info);
					userNo.tooltip("show");
					userNo.focus();
				} else if (true) {

				}
			});
		});
	});
})(window);