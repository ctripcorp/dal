
jQuery(document).ready(function () {

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

    
    // $.get("/console/dal/das/monitor/timeCosts", function(data){

    //     $.each(data.ids, function(index, value){

    //         $.get(sprintf("/console/dal/das/monitor/timeCosts/%s", value), function(data){
                
    //         });

    //     });


    // });

   

});