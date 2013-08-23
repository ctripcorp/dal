

jQuery(document).ready(function(){  

	$("[data-dismiss='modal'").click(function(event){
		if($("#myModal").hasClass("in")){
			$("#myModal").toggleClass("in").attr("aria-hidden", true).
			css("display", "none");
		}else{
			$("#myModal").toggleClass("in").attr("aria-hidden", false).
			css("display", "block");
		}
	});

});