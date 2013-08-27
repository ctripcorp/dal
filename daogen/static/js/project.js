

var projects_array = {};

jQuery(document).ready(function(){  

	$("[data-dismiss='modal'").click(function(event){
		toggleModal();
	});

	$("#save_project").click(function(){

		var post_data = {};

		post_data["product_line"] = $("#product_line").val();
		post_data["domain"] = $("#domain").val();
		post_data["service"] = $("#service").val();
		post_data["alias"] = $("#alias").val();

		$.post("/project/projects",post_data, function(data){

			toggleModal();

			getProjects();
		});


	});

	$(".icon-refresh").click(function(){
		getProjects();
	});

	getProjects();

});

var toggleModal = function(){
	if($("#myModal").hasClass("in")){
			$("#myModal").toggleClass("in").attr("aria-hidden", true).
			css("display", "none");
	}else{
			$("#myModal").toggleClass("in").attr("aria-hidden", false).
			css("display", "block");
	}
};

var getProjects = function(){
	var el = $(".icon-refresh").closest(".portlet").children(".portlet-body");
    App.blockUI(el);
	$.get("/project/projects", function(data){

		data = JSON.parse(data);

		var suffix = '</div><div class="task-config">'
		+'<div class="task-config-btn btn-group">'
		+'<a class="btn mini blue" href="#" data-toggle="dropdown" data-hover="dropdown" data-close-others="true">More <i class="icon-angle-down"></i></a>'
		+'<ul class="dropdown-menu pull-right">'
		+'<li><a href="#"><i class="icon-sun"></i> Tasks</a></li>'
		+'<li><a href="#"><i class="icon-twitter"></i> Files</a></li>'
		+'<li><a href="#"><i class="icon-pencil"></i> Edit</a></li>'
		+'<li><a href="#"><i class="icon-trash"></i> Delete</a></li>'
		+'</ul>'
		+'</div>'
		+'</div>';

		var html_data = "";
		$.each(data, function(index, value) {
			var meaningful = value.alias 
			+ "(" 
			+ value.product_line 
			+"."
			+value.domain
			+"."
			+value.service
			+")";

			html_data +=  '<li><div class="task-title"><span id="'
			+value._id
			+'" class="task-title-sp">'
			+meaningful
			+'</span>'
			+suffix 
			+'</li>';
			projects_array[value._id] = value;
		});

		$(".scroller > .task-list").html(html_data);

		App.unblockUI(el);

	});

};