/**
 * ctrip platform progress
 * author : gzxia
 */
(function (window, undefined) {
    var Progress = function () {
    };

    Progress.errorStatus = undefined;
    Progress.progressStatus = undefined;
    Progress.prototype.start = function (el, random) {
        $(el).modal({"backdrop": "static"});
        Progress.random = random;
        poll();
    };

    Progress.prototype.stop = function (el) {
        $(el).modal("hide");
        setTimeout(function () {
            $('.progress-bar').css({'width': "0%"});
            $('#generateCodeProcessMess').css({'font-weight': "normal"});
            $('#generateCodeProcessMess').html("正在初始化...");
        }, 500);
    };

    Progress.prototype.reportException = function (exception) {
        Progress.errorStatus = exception;
    };

    var poll = function () {
        var newUrl = "/rest/progress/poll?randomTag=" + Math.random();
        $.ajax({
            url: newUrl,
            data: {
                project_id: w2ui['grid'].current_project,
                regenerate: $("#regenerate").val() == "regenerate",
                language: $("#regen_language").val(),
                random: Progress.random
            },
            success: function (data) {
                if (data["status"] == "finish" || data["percent"] == "100" || Progress.errorStatus == "exception") {
                    Progress.progressStatus = "finish";
                } else {
                    Progress.progressStatus = "isDoing";
                }
                $('.progress-bar').css({'width': data["percent"] + "%"});
                $('#generateCodeProcessMess').html(data["otherMessage"]);
            },
            dataType: "json",
            complete: function (jqXHR, textStatus) {
                if (Progress.progressStatus == "finish" || textStatus != "success" || Progress.errorStatus == "exception") {
                    if (Progress.progressStatus == "finish" && textStatus == "success" && Progress.errorStatus != "exception") {
                        $('#generateCodeProcessMess').css({'font-weight': "bold"});
                        setTimeout(refreshData, 1000);
                    } else {
                        refreshData();
                    }
                } else {
                    poll();
                }
            },
            timeout: 30000,
            async: true,
            type: "GET"
        });
    };

    var refreshData = function () {
        Progress.progressStatus = undefined;
        Progress.errorStatus = undefined;
        Progress.random = undefined;
        progress.stop($("#generateCodeProcessDiv"));
        //$("#viewCode").val($("#regen_language").val());
        $("#refreshFiles").trigger("click");
    };

    window.progress = new Progress();
})(window);

