(function(window, undefined) {

	var Render = function() {

	};

	var refreshAllDB = function() {
		w2ui['grid'].clear();
		cblock($("body"));
		$.get("/rest/groupdb/allgroupdbs?rand=" + Math.random(),
				function(data) {
					var allGroupDbs = [];
					$.each(data, function(index, value) {
						value.recid = allGroupDbs.length + 1;
						allGroupDbs.push(value);
					});
					w2ui['grid'].add(allGroupDbs);
					$("body").unblock();
				}).fail(function(data) {
			$("body").unblock();
			alert("获取所有Group失败!");
		});
	};

	var addDB = function() {
		$.get("/rest/project/userGroups?root=true&rand=" + Math.random()).done(
				function(data) {
					if (data.length > 0 && data[0]['id'] > 0) {
						$("#error_msg").html('');
						$("#add_new_db_step1").show();
						$("#add_new_db_step2").hide();
						$("#conn_test").show();
						$("#add_new_db_next").show();
						$("#add_new_db_prev").hide();
						$("#add_new_db_save").hide();
						if ($("#dbcatalog")[0] != undefined
								&& $("#dbcatalog")[0].selectize != undefined) {
							$("#dbcatalog")[0].selectize.clearOptions();
						} else {
							$("#dbcatalog").selectize({
								valueField : 'id',
								labelField : 'title',
								searchField : 'title',
								sortField : 'title',
								options : [],
								create : false
							});
						}
						$("#addDbModal").modal({
							"backdrop" : "static"
						});
					} else {
						alert("请先加入一个DAL Team.");
					}
					$("body").unblock();
				}).fail(function(data) {
			alert('获取用户加入的所有DAL Team失败.');
			$("body").unblock();
		});
	};

	var editDB = function() {
		$("#update_error_msg").html('');
		$("#update_db_step1").show();
		$("#update_db_step2").hide();
		$("#update_conn_test").show();
		$("#update_db_next").show();
		$("#update_db_prev").hide();
		$("#update_db_save").hide();
		var records = w2ui['grid'].getSelection();
		var record = w2ui['grid'].get(records[0]);
		if (record == null) {
			alert('请先选择一个database');
			return;
		}
		cblock($("body"));
		$
				.post(
						"/rest/db/getOneDB",
						{
							allinonename : record['dbname']
						},
						function(data) {
							if (data.code == "OK") {
								var db = $.parseJSON(data.info);
								$("#dbtype_up").val(db['db_providerName']);
								$("#dbaddress_up").val(db['db_address']);
								$("#dbport_up").val(db['db_port']);
								$("#dbuser_up").val(db['db_user']);
								$("#dbpassword_up").val(db['db_password']);
								$("#allinonename_up").val(db['dbname']);
								if ($("#dbcatalog_up")[0] != undefined
										&& $("#dbcatalog_up")[0].selectize != undefined) {
									$("#dbcatalog_up")[0].selectize
											.clearOptions();
								} else {
									$("#dbcatalog_up").selectize({
										valueField : 'id',
										labelField : 'title',
										searchField : 'title',
										sortField : 'title',
										options : [],
										create : false
									});
								}
								$("#updateDbModal").modal({
									"backdrop" : "static"
								});
							} else {
								$("#errorMess").html(data.info);
								$("#errorNoticeDiv").modal({
									"backdrop" : "static"
								});
							}
							$("body").unblock();
						}).fail(function(data) {
					alert("执行异常");
					$("body").unblock();
				});

	};

	var delDB = function() {
		var records = w2ui['grid'].getSelection();
		var record = w2ui['grid'].get(records[0]);
		if (record != null) {
			if (confirm("Are you sure to delete?")) {
				$.post("/rest/db/deleteAllInOneDB", {
					allinonename : record['dbname']
				}, function(data) {
					if (data.code == "OK") {
						refreshAllDB();
					} else {
						$("#errorMess").html(data.info);
						$("#errorNoticeDiv").modal({
							"backdrop" : "static"
						});
					}
				}).fail(function(data) {
					alert("执行异常");
					$("body").unblock();
				});
			}
		} else {
			alert('请选择一个database！');
		}
	};

	Render.prototype = {
		render_layout : function(render_obj) {
			$(render_obj).w2layout({
				name : 'main_layout',
				panels : [ {
					type : 'main'
				} ]
			});
		},
		render_grid : function() {
			var existsGrid = w2ui['grid'];
			if (existsGrid != undefined) {
				return;
			}

			w2ui['main_layout'].content('main', $().w2grid({
				name : 'grid',
				show : {
					toolbar : true,
					footer : true,
					toolbarReload : false,
					toolbarColumns : false,
					toolbarAdd : false,
					toolbarDelete : false,
					toolbarEdit : false
				},
				toolbar : {
					items : [ {
						type : 'break'
					}, {
						type : 'button',
						id : 'refreshAllDB',
						caption : '刷新',
						icon : 'glyphicon glyphicon-refresh'
					}, {
						type : 'button',
						id : 'addDB',
						caption : '添加DB',
						icon : 'glyphicon glyphicon-plus'
					}, {
						type : 'button',
						id : 'editDB',
						caption : '修改DB',
						icon : 'glyphicon glyphicon-edit'
					}, {
						type : 'button',
						id : 'delDB',
						caption : '删除DB',
						icon : 'glyphicon glyphicon-remove'
					} ],
					onClick : function(target, data) {
						switch (target) {
						case 'refreshAllDB':
							refreshAllDB();
							break;
						case 'addDB':
							addDB();
							break;
						case 'editDB':
							editDB();
							break;
						case 'delDB':
							delDB();
							break;
						}
					}
				},
				searches : [ {
					field : 'dbname',
					caption : 'DB Name',
					type : 'text'
				}, {
					field : 'comment',
					caption : '所属DAL Team',
					type : 'text'
				}, {
					field : 'db_address',
					caption : 'DB Address',
					type : 'text'
				}, {
					field : 'db_catalog',
					caption : 'DB Catalog',
					type : 'text'
				}, {
					field : 'db_providerName',
					caption : '数据库类型',
					type : 'text'
				} ],
				columns : [ {
					field : 'dbname',
					caption : 'DB All-In-One Name',
					size : '20%',
					attr : 'align=center',
					sortable : true,
					resizable : true
				}, {
					field : 'comment',
					caption : '所属DAL Team',
					size : '15%',
					attr : 'align=center',
					sortable : true,
					resizable : true
				}, {
					field : 'db_address',
					caption : 'DB Address',
					size : '15%',
					attr : 'align=center',
					sortable : true,
					resizable : true
				}, {
					field : 'db_port',
					caption : 'DB Port',
					size : '5%',
					attr : 'align=center',
					sortable : true,
					resizable : true
				}, {
					field : 'db_user',
					caption : 'DB User',
					size : '10%',
					attr : 'align=center',
					sortable : true,
					resizable : true
				}, {
					field : 'db_password',
					caption : 'DB Password',
					size : '10%',
					attr : 'align=center',
					sortable : true,
					resizable : true
				}, {
					field : 'db_catalog',
					caption : 'DB Catalog',
					size : '15%',
					attr : 'align=center',
					sortable : true,
					resizable : true
				}, {
					field : 'db_providerName',
					caption : '数据库类型',
					size : '10%',
					attr : 'align=center',
					sortable : true,
					resizable : true
				} ],
				records : []
			}));

			refreshAllDB();
		}
	};

	window.render = new Render();

	$('#main_layout').height($(document).height() - 50);

	window.render.render_layout($('#main_layout'));

	window.render.render_grid();

	$(window).resize(function() {
		$('#main_layout').height($(document).height() - 50);
	});

	jQuery(document)
			.ready(
					function() {

						var setDefaultAddDbVal = function() {
							$("#error_msg").html(" ");

							var dbType = $.trim($("#dbtype").val());

							if ("MySQL" == dbType) {
								$("#dbaddress").val(
										'pub.mysql.db.dev.sh.ctripcorp.com');
								$("#dbuser").val('uws_dbticket');
								$("#dbpassword").val('kgd8v5CenyoMjtg1uwzj');
							} else if ("SQLServer" == dbType) {
								$("#dbaddress").val(
										'devdb.dev.sh.ctriptravel.com');
								$("#dbuser").val('uws_AllInOneKey_dev');
								$("#dbpassword").val('!QAZ@WSX1qaz2wsx');
							} else {
								$("#dbaddress").val('');
								$("#dbuser").val('');
								$("#dbpassword").val('');
							}
						};

						var getAllCatalog = function(successInfo) {
							$("#error_msg").html("正在连接数据库，请稍等...");
							var dbType = $("#dbtype").val();
							var dbAddress = $("#dbaddress").val();
							var dbPort = $("#dbport").val();
							var dbUser = $("#dbuser").val();
							var dbPassword = $("#dbpassword").val();
							cblock($("body"));
							$.post(
									"/rest/db/connectionTest",
									{
										"dbtype" : dbType,
										"dbaddress" : dbAddress,
										"dbport" : dbPort,
										"dbuser" : dbUser,
										"dbpassword" : dbPassword
									},
									function(data) {
										if (data.code == "OK") {
											var allCatalog = [];
											$.each($.parseJSON(data.info),
													function(index, value) {
														allCatalog.push({
															id : value,
															title : value
														});
													});
											$("#dbcatalog")[0].selectize
													.clearOptions();
											$("#dbcatalog")[0].selectize
													.addOption(allCatalog);
											$("#dbcatalog")[0].selectize
													.refreshOptions(false);
											$("#error_msg").html(successInfo);
										} else {
											$("#error_msg").html(data.info);
										}
										$("body").unblock();
									}).fail(function(data) {
								$("#error_msg").text(data);
								$("body").unblock();
							});
						};

						$(document.body).on('change', "#dbtype",
								function(event) {
									setDefaultAddDbVal();
								});

						$(document.body)
								.on(
										'click',
										"#add_new_db_next",
										function(event) {
											var dbType = $("#dbtype").val();
											var dbAddress = $("#dbaddress")
													.val();
											var dbPort = $("#dbport").val();
											var dbUser = $("#dbuser").val();
											var dbPassword = $("#dbpassword")
													.val();

											if ("no" == dbType) {
												$("#error_msg")
														.html("请选择数据库类型");
												return;
											}
											if (dbAddress == null
													|| dbAddress == "") {
												$("#error_msg").html("请选择数据库");
												return;
											}
											if (dbPort == null || dbPort == "") {
												$("#error_msg")
														.html("请输入数据库端口");
												return;
											}
											if (dbUser == null || dbUser == "") {
												$("#error_msg").html(
														"请输入数据库登陆用户");
												return;
											}
											if (dbPassword == null
													|| dbPassword == "") {
												$("#error_msg").html(
														"请输入数据库登陆用户密码");
												return;
											}
											$("#add_new_db_step1").hide();
											$("#add_new_db_step2").show();
											$("#conn_test").hide();
											$("#add_new_db_next").hide();
											$("#add_new_db_prev").show();
											$("#add_new_db_save").show();
											getAllCatalog("");
										});

						$(document.body).on('click', "#add_new_db_prev",
								function(event) {
									$("#add_new_db_step1").show();
									$("#add_new_db_step2").hide();
									$("#conn_test").show();
									$("#add_new_db_next").show();
									$("#add_new_db_prev").hide();
									$("#add_new_db_save").hide();
									$("#error_msg").html(" ");
								});

						$(document.body).on('click', "#conn_test",
								function(event) {
									getAllCatalog("connection successful");
								});

						$(document.body)
								.on(
										'click',
										"#add_new_db_save",
										function(event) {

											var dbType = $("#dbtype").val();
											var all_In_One_Name = $(
													"#allinonename").val();
											var dbAddress = $("#dbaddress")
													.val();
											var dbPort = $("#dbport").val();
											var dbUser = $("#dbuser").val();
											var dbPassword = $("#dbpassword")
													.val();
											var dbCatalog = $("#dbcatalog")
													.val();

											if ("no" == dbType) {
												$("#error_msg")
														.html("请选择数据库类型");
												return;
											}
											if ("" == all_In_One_Name
													|| null == all_In_One_Name) {
												$("#error_msg").html(
														"请输入All-In-One Name");
												return;
											}
											if (dbAddress == null
													|| dbAddress == "") {
												$("#error_msg").html("请选择数据库");
												return;
											}
											if (dbPort == null || dbPort == "") {
												$("#error_msg")
														.html("请输入数据库端口");
												return;
											}
											if (dbUser == null || dbUser == "") {
												$("#error_msg").html(
														"请输入数据库登陆用户");
												return;
											}
											if (dbPassword == null
													|| dbPassword == "") {
												$("#error_msg").html(
														"请输入数据库登陆用户密码");
												return;
											}
											if (dbCatalog == null
													|| dbCatalog == "") {
												$("#error_msg").html("请输入数据库");
												return;
											}

											cblock($("body"));
											$
													.post(
															"/rest/db/addNewAllInOneDB",
															{
																"dbtype" : dbType,
																"allinonename" : all_In_One_Name,
																"dbaddress" : dbAddress,
																"dbport" : dbPort,
																"dbuser" : dbUser,
																"dbpassword" : dbPassword,
																"dbcatalog" : dbCatalog
															},
															function(data) {
																if (data.code == "OK") {
																	$(
																			"#error_msg")
																			.html(
																					"保存成功.<br/>请到<a href='dbmanage.jsp' target='_blank'>数据库管理</a>界面将此数据库加入你的组里");
																	refreshAllDB();
																} else {
																	$(
																			"#error_msg")
																			.html(
																					data.info);
																}
																$("body")
																		.unblock();
															})
													.fail(
															function(data) {
																$("#error_msg")
																		.text(
																				data);
																$("body")
																		.unblock();
															});
										});

						$(document.body)
								.on(
										'change',
										"#dbtype_up",
										function(event) {
											$("#error_msg").html(" ");

											var dbType = $.trim($("#dbtype_up")
													.val());

											if ("MySQL" == dbType) {
												$("#dbaddress_up")
														.val(
																'pub.mysql.db.dev.sh.ctripcorp.com');
												$("#dbuser_up").val(
														'uws_dbticket');
												$("#dbpassword_up").val(
														'kgd8v5CenyoMjtg1uwzj');
											} else if ("SQLServer" == dbType) {
												$("#dbaddress_up")
														.val(
																'devdb.dev.sh.ctriptravel.com');
												$("#dbuser_up").val(
														'uws_AllInOneKey_dev');
												$("#dbpassword_up").val(
														'!QAZ@WSX1qaz2wsx');
											} else {
												$("#dbaddress_up").val('');
												$("#dbuser_up").val('');
												$("#dbpassword_up").val('');
											}
										});

						var getUpdateCatalog = function(successInfo) {
							$("#update_error_msg").html("正在连接数据库，请稍等...");
							var dbType = $("#dbtype_up").val();
							var dbAddress = $("#dbaddress_up").val();
							var dbPort = $("#dbport_up").val();
							var dbUser = $("#dbuser_up").val();
							var dbPassword = $("#dbpassword_up").val();
							cblock($("body"));
							$
									.post(
											"/rest/db/connectionTest",
											{
												"dbtype" : dbType,
												"dbaddress" : dbAddress,
												"dbport" : dbPort,
												"dbuser" : dbUser,
												"dbpassword" : dbPassword
											},
											function(data) {
												if (data.code == "OK") {
													var allCatalog = [];
													$
															.each(
																	$
																			.parseJSON(data.info),
																	function(
																			index,
																			value) {
																		allCatalog
																				.push({
																					id : value,
																					title : value
																				});
																	});
													$("#dbcatalog_up")[0].selectize
															.clearOptions();
													$("#dbcatalog_up")[0].selectize
															.addOption(allCatalog);
													$("#dbcatalog_up")[0].selectize
															.refreshOptions(false);
													$("#update_error_msg")
															.html(successInfo);
													var records = w2ui['grid']
															.getSelection();
													var record = w2ui['grid']
															.get(records[0]);
													$
															.post(
																	"/rest/db/getOneDB",
																	{
																		allinonename : record['dbname']
																	},
																	function(
																			data) {
																		if (data.code == "OK") {
																			var db = $
																					.parseJSON(data.info);
																			$("#dbcatalog_up")[0].selectize
																					.setValue(db['db_catalog']);
																		}
																	});
												} else {
													$("#update_error_msg")
															.html(data.info);
												}
												$("body").unblock();
											}).fail(function(data) {
										$("#update_error_msg").text(data);
										$("body").unblock();
									});
						};

						$(document.body).on('click', "#update_conn_test",
								function(event) {
									getUpdateCatalog("connection successful");
								});

						$(document.body)
								.on(
										'click',
										"#update_db_next",
										function(event) {
											var dbType = $("#dbtype_up").val();
											var dbAddress = $("#dbaddress_up")
													.val();
											var dbPort = $("#dbport_up").val();
											var dbUser = $("#dbuser_up").val();
											var dbPassword = $("#dbpassword_up")
													.val();

											if ("no" == dbType) {
												$("#update_error_msg").html(
														"请选择数据库类型");
												return;
											}
											if (dbAddress == null
													|| dbAddress == "") {
												$("#update_error_msg").html(
														"请选择数据库");
												return;
											}
											if (dbPort == null || dbPort == "") {
												$("#update_error_msg").html(
														"请输入数据库端口");
												return;
											}
											if (dbUser == null || dbUser == "") {
												$("#update_error_msg").html(
														"请输入数据库登陆用户");
												return;
											}
											if (dbPassword == null
													|| dbPassword == "") {
												$("#update_error_msg").html(
														"请输入数据库登陆用户密码");
												return;
											}
											$("#update_db_step1").hide();
											$("#update_db_step2").show();
											$("#update_conn_test").hide();
											$("#update_db_next").hide();
											$("#update_db_prev").show();
											$("#update_db_save").show();
											getUpdateCatalog("");
										});

						$(document.body).on('click', "#update_db_prev",
								function(event) {
									$("#update_db_step1").show();
									$("#update_db_step2").hide();
									$("#update_conn_test").show();
									$("#update_db_next").show();
									$("#update_db_prev").hide();
									$("#update_db_save").hide();
									$("#error_msg").html(" ");
								});

						$(document.body)
								.on(
										'click',
										"#update_db_save",
										function(event) {

											var dbType = $("#dbtype_up").val();
											var all_In_One_Name = $(
													"#allinonename_up").val();
											var dbAddress = $("#dbaddress_up")
													.val();
											var dbPort = $("#dbport_up").val();
											var dbUser = $("#dbuser_up").val();
											var dbPassword = $("#dbpassword_up")
													.val();
											var dbCatalog = $("#dbcatalog_up")
													.val();

											if ("no" == dbType) {
												$("#update_error_msg").html(
														"请选择数据库类型");
												return;
											}
											if ("" == all_In_One_Name
													|| null == all_In_One_Name) {
												$("#update_error_msg").html(
														"请输入All-In-One Name");
												return;
											}
											if (dbAddress == null
													|| dbAddress == "") {
												$("#update_error_msg").html(
														"请选择数据库");
												return;
											}
											if (dbPort == null || dbPort == "") {
												$("#update_error_msg").html(
														"请输入数据库端口");
												return;
											}
											if (dbUser == null || dbUser == "") {
												$("#update_error_msg").html(
														"请输入数据库登陆用户");
												return;
											}
											if (dbPassword == null
													|| dbPassword == "") {
												$("#update_error_msg").html(
														"请输入数据库登陆用户密码");
												return;
											}
											if (dbCatalog == null
													|| dbCatalog == "") {
												$("#update_error_msg").html(
														"请输入数据库");
												return;
											}

											cblock($("body"));
											var records = w2ui['grid']
													.getSelection();
											var record = w2ui['grid']
													.get(records[0]);
											$
													.post(
															"/rest/db/updateDB",
															{
																"id" : record['id'],
																"dbtype" : dbType,
																"allinonename" : all_In_One_Name,
																"dbaddress" : dbAddress,
																"dbport" : dbPort,
																"dbuser" : dbUser,
																"dbpassword" : dbPassword,
																"dbcatalog" : dbCatalog
															},
															function(data) {
																if (data.code == "OK") {
																	$(
																			"#update_error_msg")
																			.html(
																					"更新成功.");
																	refreshAllDB();
																} else {
																	$(
																			"#update_error_msg")
																			.html(
																					data.info);
																}
																$("body")
																		.unblock();
															})
													.fail(
															function(data) {
																$(
																		"#update_error_msg")
																		.text(
																				data);
																$("body")
																		.unblock();
															});
										});

					});

})(window);