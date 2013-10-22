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
        if ($("#myModal").hasClass("in")) {
            $("#myModal").toggleClass("in").attr("aria-hidden", true).
            css("display", "none");
        } else {
            $("#myModal").toggleClass("in").attr("aria-hidden", false).
            css("display", "block");
        }
    });

    $("#save_project").click(function () {

        var post_data = {};

        post_data["product_line"] = $("#product_line").val();
        post_data["domain"] = $("#domain").val();
        post_data["service"] = $("#service").val();
        post_data["alias"] = $("#alias").val();

        $.post("/project/add", post_data, function (data) {

            $("[data-dismiss='modal'").trigger('click');

            $(".icon-refresh").trigger('click');
        });

    });

    $(".icon-refresh").click(function () {
        var el = $(this).closest(".portlet").children(".portlet-body");
        App.blockUI(el);
        $.get("/project/projects", function (data) {

            data = JSON.parse(data);

            var suffix = '</div><div class="task-config">' + '<div class="task-config-btn btn-group">' + '<a class="btn mini blue" href="#" data-toggle="dropdown" data-hover="dropdown" data-close-others="true">操作 <i class="icon-angle-down"></i></a>' + '<ul class="dropdown-menu pull-right">' + '<li><a href="/task/index?project_id=%s"><i class="icon-sun"></i> 对应DAO</a></li>' + '<li><a href="/file/?project_id=%s"><i class="icon-twitter"></i> 对应文件</a></li>' + '<li><a href="#"><i class="icon-pencil"></i> 编辑</a></li>' + '<li><a href="#"><i class="icon-trash"></i> 删除</a></li>' + '</ul>' + '</div>' + '</div>';

            var html_data = "";
            $.each(data, function (index, value) {

                var meaningful = sprintf("%s(%s.%s.%s)", value.alias, value.product_line, value.domain, value.service);

                var format_suffix = sprintf(suffix, value._id, value._id);

                html_data = sprintf(
                    "%s<li><div class='task-title'><span id='%s' class='task-title-sp'>%s</span>%s</li>", html_data, value._id, meaningful, format_suffix);

                projects_array[value._id] = value;
            });

            $(".scroller > .task-list").html(html_data);

            $(".icon-trash").each(function(){
                $(this).parent().bind('click', function(event){
                    if (confirm("Are you sure?")) {
                        var id = $(this).closest('div[class="task-config"]').prev().children().attr("id");
                        $.get("/project/delete?project_id="+id, function(data){
                            $(".icon-refresh").trigger('click');
                        });
                    }
                });
            });

            App.unblockUI(el);


        });
    });

    $(".icon-refresh").trigger('click');

    $("#daogen").toggleClass('open');

    $("#daogen .sub-menu").show();

});