var projects_array = {};

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

    $("[data-dismiss='modal'").click(function (event) {
        toggleModal();
    });

    $("#save_project").click(function () {

        var post_data = {};

        post_data["product_line"] = $("#product_line").val();
        post_data["domain"] = $("#domain").val();
        post_data["service"] = $("#service").val();
        post_data["alias"] = $("#alias").val();

        $.post("/project/projects", post_data, function (data) {

            toggleModal();

            getProjects();
        });


    });

    $(".icon-refresh").click(function () {
        getProjects();
    });

    getProjects();

});

var toggleModal = function () {
    if ($("#myModal").hasClass("in")) {
        $("#myModal").toggleClass("in").attr("aria-hidden", true).
        css("display", "none");
    } else {
        $("#myModal").toggleClass("in").attr("aria-hidden", false).
        css("display", "block");
    }
};

var getProjects = function () {
    var el = $(".icon-refresh").closest(".portlet").children(".portlet-body");
    App.blockUI(el);
    $.get("/project/projects", function (data) {

        data = JSON.parse(data);

        var suffix = '</div><div class="task-config">' + '<div class="task-config-btn btn-group">' + '<a class="btn mini blue" href="#" data-toggle="dropdown" data-hover="dropdown" data-close-others="true">More <i class="icon-angle-down"></i></a>' + '<ul class="dropdown-menu pull-right">' + '<li><a href="/task?project_id=%s"><i class="icon-sun"></i> Tasks</a></li>' + '<li><a href="/file?project_id=%s"><i class="icon-twitter"></i> Files</a></li>' + '<li><a href="#"><i class="icon-pencil"></i> Edit</a></li>' + '<li><a href="#"><i class="icon-trash"></i> Delete</a></li>' + '</ul>' + '</div>' + '</div>';

        var html_data = "";
        $.each(data, function (index, value) {

            var meaningful = sprintf("%s(%s.%s.%s)", value.alias, value.product_line, value.domain, value.service);

            var format_suffix = sprintf(suffix, value._id, value._id);

            html_data = sprintf(
                "%s<li><div class='task-title'><span id='%s' class='task-title-sp'>%s</span>%s</li>", html_data, value._id, meaningful, format_suffix);

            projects_array[value._id] = value;
        });

        $(".scroller > .task-list").html(html_data);

        App.unblockUI(el);

    });

};