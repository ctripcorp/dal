(function() {
	var checkSetupDb = function() {
		cblock($("body"));
		$
				.get("/rest/setupDb/setupDbCheck?rand=" + Math.random())
				.done(
						function(data) {
							if (data.code != "OK") {
								$("#setup_error_msg").html('');
								$("#setup_db_step1").show();
								$("#setup_db_step2").hide();
								$("#setup_conn_test").show();
								$("#setup_db_next").show();
								$("#setup_db_prev").hide();
								$("#setup_db_save").hide();
								if ($("#setupdbcatalog")[0] != undefined
										&& $("#setupdbcatalog")[0].selectize != undefined) {
									$("#setupdbcatalog")[0].selectize
											.clearOptions();
								} else {
									$("#setupdbcatalog").selectize({
										valueField : 'id',
										labelField : 'title',
										searchField : 'title',
										sortField : 'title',
										options : [],
										create : false
									});
								}

								if (data.info == "!jdbc"
										|| data.info == "!valid") {
									$("#setupDbModal").modal({
										"backdrop" : "static"
									});
								}
							} else {
								if (data.info == "initialized") {
									initialized = true;
									window.location.href = "index.jsp";
								}
							}
							$("body").unblock();
						});
	};

	$(window).load(
			function() {
				var href = location.href;
				$("li[class='active']").removeClass("active");
				if (href.indexOf("codeview") != -1) {
					$("#codeviewjsp").addClass("active");
				} else if (href.indexOf("dbmanage") != -1
						|| href.indexOf("dbsetsmanage") != -1
						|| href.indexOf("dbview") != -1) {
					$("#dbmanagejsp").addClass("active");
				} else if (href.indexOf("groupmanage") != -1) {
					$("#groupmanagejsp").addClass("active");
				} else if (href.indexOf("membermanage") != -1) {
					$("#membermanagejsp").addClass("active");
				} else if (href.indexOf("eventmanage") != -1) {
					$("#eventmanagejsp").addClass("active");
				} else if (href.indexOf("welcome") != -1) {
					$("#welcomejsp").addClass("active");
				} else {
					$("#indexjsp").addClass("active");
				}

				var options = {
					animation : true,
					trigger : 'hover',
					html : true
				};

				$('[data-toggle="tooltip"]').tooltip(options);
			});

	$(function() {
		var Sys = {};
		var ua = navigator.userAgent.toLowerCase();
		var s;
		var scan;
		(s = ua.match(/msie ([\d.]+)/)) ? Sys.ie = s[1] : (s = ua
				.match(/firefox\/([\d.]+)/)) ? Sys.firefox = s[1] : (s = ua
				.match(/chrome\/([\d.]+)/)) ? Sys.chrome = s[1] : (s = ua
				.match(/opera.([\d.]+)/)) ? Sys.opera = s[1] : (s = ua
				.match(/version\/([\d.]+).*safari/)) ? Sys.safari = s[1] : 0;

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

		$("#setup_conn_test").click(function() {
			connectionTest("connection successful");
		});

		$("#setup_db_prev").click(function() {
			$("#setup_db_step1").show();
			$("#setup_db_step2").hide();
			$("#setup_conn_test").show();
			$("#setup_db_next").show();
			$("#setup_db_prev").hide();
			$("#setup_db_save").hide();
			$("#setup_error_msg").html(" ");
		});

		$("#setup_db_next").click(function() {
			var dbAddress = $("#setupdbaddress").val();
			var dbPort = $("#setupdbport").val();
			var dbUser = $("#setupdbuser").val();
			var dbPassword = $("#setupdbpassword").val();

			if (dbAddress == null || dbAddress.length == 0) {
				$("#setup_error_msg").html("请输入数据库地址!");
				return;
			}
			if (dbPort == null || dbPort.length == 0) {
				$("#setup_error_msg").html("请输入数据库端口!");
				return;
			}
			if (dbUser == null || dbUser.length == 0) {
				$("#setup_error_msg").html("请输入数据库登录用户!");
				return;
			}
			if (dbPassword == null || dbPassword.length == 0) {
				$("#setup_error_msg").html("请输入数据库登录用户密码!");
				return;
			}
			$("#setup_db_step1").hide();
			$("#setup_db_step2").show();
			$("#setup_conn_test").hide();
			$("#setup_db_next").hide();
			$("#setup_db_prev").show();
			$("#setup_db_save").show();
			connectionTest("");
		});

		$("#setup_db_save").click(function() {
			var dbAddress = $("#setupdbaddress").val();
			var dbPort = $("#setupdbport").val();
			var dbUser = $("#setupdbuser").val();
			var dbPassword = $("#setupdbpassword").val();
			var dbCatalog = $("#setupdbcatalog").val();
			var groupName = $("#setupdbgroupname").val();
			var groupComment = $("#setupdbcomment").val();

			if (dbAddress == null || dbAddress.length == 0) {
				$("#setup_error_msg").html("请输入数据库地址!");
				return;
			}
			if (dbPort == null || dbPort.length == 0) {
				$("#setup_error_msg").html("请输入数据库端口!");
				return;
			}
			if (dbUser == null || dbUser.length == 0) {
				$("#setup_error_msg").html("请输入数据库登录用户!");
				return;
			}
			if (dbPassword == null || dbPassword.length == 0) {
				$("#setup_error_msg").html("请输入数据库登录用户密码!");
				return;
			}
			if (dbCatalog == null || dbCatalog.length == 0) {
				$("#setup_error_msg").html("请选择数据库!");
				return;
			}
			if (groupName == null || groupName.length == 0) {
				$("#setup_error_msg").html("请输入组名!");
				return;
			}
			if (groupComment == null || groupComment.length == 0) {
				$("#setup_error_msg").html("请输入备注!");
				return;
			}
			cblock($("body"));
			$.post("/rest/setupDb/initializeDb", {
				"dbaddress" : dbAddress,
				"dbport" : dbPort,
				"dbuser" : dbUser,
				"dbpassword" : dbPassword,
				"dbcatalog" : dbCatalog,
				"groupName" : groupName,
				"groupComment" : groupComment,
			}, function(data) {
				if (data.code == "OK") {
					$("#setup_error_msg").html("初始化数据库成功.");
					$("body").unblock();
					window.location.href = "index.jsp";
				} else {
					$("#setup_error_msg").html(data.info);
					$("body").unblock();
				}
			}).fail(function(data) {
				$("#setup_error_msg").text(data);
				$("body").unblock();
			});
		});

		var connectionTest = function(successInfo) {
			$("#setup_error_msg").html("正在连接数据库，请稍等...");
			var dbType = $("#setupdbtype").val();
			var dbAddress = $("#setupdbaddress").val();
			var dbPort = $("#setupdbport").val();
			var dbUser = $("#setupdbuser").val();
			var dbPassword = $("#setupdbpassword").val();
			cblock($("body"));
			$.post("/rest/setupDb/connectionTest", {
				"dbtype" : dbType,
				"dbaddress" : dbAddress,
				"dbport" : dbPort,
				"dbuser" : dbUser,
				"dbpassword" : dbPassword
			}, function(data) {
				if (data.code == "OK") {
					var allCatalog = [];
					$.each($.parseJSON(data.info), function(index, value) {
						allCatalog.push({
							id : value,
							title : value
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
			}).fail(function(data) {
				$("#setup_error_msg").text(data);
				$("body").unblock();
			});
		};

		// check setup db
		checkSetupDb();
	});
})();

(function($) {
	$.extend({
		isEmpty : function(str) {
			return str == null || str == '';
		},
		showMsg : function(id, msg) {
			$("#" + id).html(msg);
		}
	});
})(jQuery);

(function($, window) {
	window.alert = function(data) {
		$("#overrideAlertErrorNoticeDivMsg").html(data);
		$("#overrideAlertErrorNoticeDiv").modal({
			"backdrop" : "static"
		});
	};

	var keepSession = function() {
		$.post("/rest/group/keepSession", {
			id : 1
		}, function(data) {
			// do nothing
		});
	};

	setInterval(keepSession, 20 * 60 * 1000);

})(jQuery, window);
