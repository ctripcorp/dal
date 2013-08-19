

jQuery(document).ready(function() {    

	$(".sidebar").click(function(event){
		$("li.sidebar.active").toggleClass("active");
		$(this).toggleClass("active");
		var sidename = $(this).attr("sidename");
		$.get("body/"+sidename, function(data){
			$("#main_container").html(data);
			if(sidename == "task"){
				var editor = ace.edit("editor");
    			editor.setTheme("ace/theme/monokai");
    			editor.getSession().setMode("ace/mode/mysql");
			}
		});
	});

	App.init(); // initlayout and core plugins
		   
	Tasks.initDashboardWidget();

	$("li.sidebar.active").toggleClass("active");
	$("li.sidebar[sidename=project]").toggleClass("active");
	$.get("body/project", function(data){
		$("#main_container").html(data);
	});

	jQuery('body').on('click', '.portlet > .portlet-title > .tools > .icon-collapse, .portlet .portlet-title > .tools > .icon-collapse-top', function (e) {
		// e.preventDefault();
        if (jQuery(this).hasClass("icon-collapse")) {
            jQuery(this).removeClass("icon-collapse").addClass("icon-collapse-top");
        } else {
            jQuery(this).removeClass("icon-collapse-top").addClass("icon-collapse");
        }
	});

});

var Tasks = function () {


    return {

        //main function to initiate the module
        initDashboardWidget: function () {
			$('input.liChild').change(function() {
				if ($(this).is(':checked')) { 
					$(this).parents('li').addClass("task-done"); 
				} else { 
					$(this).parents('li').removeClass("task-done"); 
				}
			}); 
        }

    };

}();