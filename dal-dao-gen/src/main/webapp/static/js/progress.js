/**
 * ctrip platform progress
 * author : gzxia
 */

(function (window, undefined) {
    var Progress = function () {

    };

    Progress.errorStatus = undefined;

    Progress.progressStatus = undefined;

    Progress.prototype.start = function (el) {
        $(el).modal();
        poll();
    };

    Progress.prototype.stop = function (el) {
        $(el).modal("hide");
        $('.progress-bar').css({'width': "0%"});
        $('#generateCodeProcessMess').html("正在初始化...");
    };

    var poll = function () {
        var newUrl = "/rest/progress/poll?random=" + Math.random();
        $.ajax({
            url: newUrl,
            data : {
                "project_id": w2ui['grid'].current_project,
                "regenerate": $("#regenerate").val() == "regenerate",
                "language": $("#regen_language").val()
            },
            success: function (data) {
                if (data["status"] == "finish" || data["percent"] == "100" || this.errorStatus=="exception") {
                    this.progressStatus = "finish";
                }else{
                    this.progressStatus = "isDoing";
                }
                $('.progress-bar').css({'width': data["percent"]+"%"});
                $('#generateCodeProcessMess').html(data["otherMessage"]);
            },
            dataType: "json",
            complete: function(jqXHR, textStatus){
                if(this.progressStatus == "finish" || textStatus != "success" || this.errorStatus=="exception" ){
                    Progress.progressStatus = undefined;
                    Progress.errorStatus = undefined;
                    progress.stop($("#generateCodeProcessDiv"));
                    $("#viewCode").val($("#regen_language").val());
                    $("#refreshFiles").trigger("click");
                }else{
                    poll();
                }
            },
            timeout: 30000,
            async:true,
            type: "GET"
        });
    };

    window.progress = new Progress();
})(window);

