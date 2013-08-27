

jQuery(document).ready(function(){  

	App.init(); // initlayout and core plugins
				   
	// Tasks.initDashboardWidget();

	jQuery('body').on('click', '.portlet > .portlet-title > .tools > .icon-collapse, .portlet .portlet-title > .tools > .icon-collapse-top', function (e) {
		// e.preventDefault();
        if (jQuery(this).hasClass("icon-collapse")) {
            jQuery(this).removeClass("icon-collapse").addClass("icon-collapse-top");
        } else {
            jQuery(this).removeClass("icon-collapse-top").addClass("icon-collapse");
        }
	});

	var editor = ace.edit("sql_editor");
	editor.setTheme("ace/theme/monokai");
	editor.getSession().setMode("ace/mode/mysql");

	var sp_editor = ace.edit("sp_editor");
	sp_editor.setTheme("ace/theme/monokai");
	sp_editor.getSession().setMode("ace/mode/mysql");

	$("#databases").change(function(event){
		var el = jQuery(this).closest(".portlet").children(".portlet-body");
		App.blockUI(el);
		$.get("/metadata?meta_type=tables&meta_value="+ $(this).val(), function(data){
			data = JSON.parse(data);
			var html_data = "";
			$.each(data, function(index, value) {
  				html_data += "<option>" + value + "</option>";
			});
			$("#tables").html(html_data);
			table_change();
			//App.unblockUI(el);
		});
	});

	$("#tables").change(function(event){
		var el = jQuery(this).closest(".portlet").children(".portlet-body");
		App.blockUI(el);
		table_change();
	});

	$("#sp_databases").change(function(event){
		var el = jQuery(this).closest(".portlet").children(".portlet-body");
		App.blockUI(el);
		$.get("/metadata?meta_type=sp&meta_value="+ $(this).val(), function(data){
			data = JSON.parse(data);
			var html_data = "";
			$.each(data, function(index, value) {
  				html_data += "<option>" + value + "</option>";
			});
			$("#sp_names").html(html_data);
			sp_change();
			//App.unblockUI(el);
		});
	});

	$("#sp_names").change(function(event){
		var el = jQuery(this).closest(".portlet").children(".portlet-body");
		App.blockUI(el);
		sp_change();
	});

	$("button.btn.move").click(function(){

		$.each($('#left_select').find(":selected"), function(index, value){
			$("#right_select").append(value);
			//$("#left_select").removeOption(index);
		});
	});

	$("button.btn.moveall").click(function(){
		$("#right_select").html($("#left_select").html());
		$("#left_select").html("");
	});

	$("button.btn.remove").click(function(){
		$.each($('#right_select').find(":selected"), function(index, value){
			$("#left_select").append(value);
			//$("#right_select").remove(value);
		});
	});

	$("button.btn.removeall").click(function(){
		$("#left_select").html($("#right_select").html());
		$("#right_select").html("");
	});

	$("button.btn-primary").click(function(){
		var btn_type = $(this).attr("btn_type");
		if(btn_type == "select"){
			$("#select_fields").show();
			$("#where_fields").show();
		}else if(btn_type == "insert"){
			$("#select_fields").show();
			$("#where_fields").hide();
		}else if(btn_type == "update"){
			$("#select_fields").show();
			$("#where_fields").show();
		}else if(btn_type == "delete"){
			$("#select_fields").hide();
			$("#where_fields").show();
		}
	});

});

var table_change = function(){
	var event_obj = $("#tables");
	var el = jQuery(event_obj).closest(".portlet").children(".portlet-body");
	$.get("/metadata?meta_type=fields&meta_value="
		+ $(event_obj).val() + "&db_name="+$("#databases").val(), function(data){
		data = JSON.parse(data);
		var html_data = "";
		var operator  = '<div class="task-config">'
		+'<div class="task-config-btn btn-group">'
		+'<a class="btn mini blue" href="#" data-toggle="dropdown" data-hover="dropdown" data-close-others="true">Operator<i class="icon-angle-down"></i></a>'
		+'<ul class="dropdown-menu pull-right">'
		+'<li><a href="#"><i class="icon-ok"></i> None</a></li>'
		+'<li><a href="#"><i></i> Equal</a></li>'	
		+'<li><a href="#"><i></i> Not Equal</a></li>'
		+'<li><a href="#"><i></i> Greater Than</a></li>'
		+'<li><a href="#"><i></i> Less Than</a></li>'
		+'<li><a href="#"><i></i> Greater Equal Than</a></li>'
		+'<li><a href="#"><i></i> Less Equal Than</a></li>'
		+'<li><a href="#"><i></i> Between</a></li>'
		+'<li><a href="#"><i></i> Like</a></li>'
		+'<li><a href="#"><i></i> In</a></li>'
		+'</ul></div></div>';
		var where_condition = '';
		$.each(data, function(index, value) {
				html_data += "<option>" + value + "</option>";
				where_condition += "<li><div class='task-title'><span class='task-title-sp'>"+value+"</span></div>"+operator+"</li>";
		});
		$("#left_select").html(html_data);
		$("#where_condition").html(where_condition);

		$(".task-config > .task-config-btn > .dropdown-menu  > li > a").click(function(){
			$(this).parent().parent().find("li > a > i.icon-ok").toggleClass("icon-ok");
			$(this).find("i").toggleClass("icon-ok");
		});

		// $(".multiselect").multiselect();
		App.unblockUI(el);
	});

};

var sp_change = function(){
	var event_obj = $("#sp_names");
	var el = jQuery(event_obj).closest(".portlet").children(".portlet-body");
	$.get("/metadata?meta_type=sp_code&meta_value="
		+ $(event_obj).val() + "&db_name="+$("#sp_databases").val(), function(data){
		data = JSON.parse(data);
		ace.edit("sp_editor").setValue(data);
		App.unblockUI(el);
	});
};

jQuery.fn.multiselect = function() {
    $(this).each(function() {
        var checkboxes = $(this).find("input:checkbox");
        checkboxes.each(function() {
            var checkbox = $(this);
            // Highlight pre-selected checkboxes
            if (checkbox.attr("checked"))
                checkbox.parent().addClass("multiselect-on");
 
            // Highlight checkboxes that the user selects
            checkbox.click(function() {
                // if (checkbox.attr("checked"))
                //     checkbox.parent().addClass("multiselect-on");
                // else
                //     checkbox.parent().removeClass("multiselect-on");
            });
        });
    });
};