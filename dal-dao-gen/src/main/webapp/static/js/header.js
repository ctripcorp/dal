(function(){

    $(window).load(function(){
        var href = location.href;
        $("li[class='active']").removeClass("active");
        if(href.indexOf("codeview")!=-1){
            $("#codeviewjsp").addClass("active");
        }else if(href.indexOf("dbmanage")!=-1 || href.indexOf("dbsetsmanage")!=-1
            ||href.indexOf("dbview")!=-1){
            $("#dbmanagejsp").addClass("active");
        }else if(href.indexOf("groupmanage")!=-1){
            $("#groupmanagejsp").addClass("active");
        }else if(href.indexOf("membermanage")!=-1){
            $("#membermanagejsp").addClass("active");
        }else if(href.indexOf("welcome")!=-1){
            $("#welcomejsp").addClass("active");
        }else{
            $("#indexjsp").addClass("active");
        }

        var options={
            animation:true,
            trigger:'hover',
            html:true
        }
        $('.ctip').tooltip(options);

    });

    $(function(){
        var Sys = {};
        var ua = navigator.userAgent.toLowerCase();
        var s;
        var scan;
        (s = ua.match(/msie ([\d.]+)/)) ? Sys.ie = s[1] :
            (s = ua.match(/firefox\/([\d.]+)/)) ? Sys.firefox = s[1] :
                (s = ua.match(/chrome\/([\d.]+)/)) ? Sys.chrome = s[1] :
                    (s = ua.match(/opera.([\d.]+)/)) ? Sys.opera = s[1] :
                        (s = ua.match(/version\/([\d.]+).*safari/)) ? Sys.safari = s[1] : 0;

        //以下进行测试

        if (Sys.ie) {
            scan = "您使用的ie内核" + Sys.ie + "浏览器，建议您使用chrome浏览器";
            alert(scan);
        }
        if (Sys.firefox) {
            scan = "您使用的是firefox内核" + Sys.firefox + "浏览器，建议您使用chrome浏览器";
            alert(scan);
        }
        if (Sys.chrome) {
            scan = "您使用的是chrome内核" + Sys.chrome + "浏览器，建议您使用chrome浏览器";
        }
        if (Sys.opera) {
            scan = "您使用的是opera内核" + Sys.opera + "浏览器，建议您使用chrome浏览器";
            alert(scan);
        }
        if (Sys.safari) {
            scan = "您使用的是safari内核" + Sys.safari + "浏览器，建议您使用chrome浏览器";
            alert(scan);
        }

    });

})();

(function($){
    $.extend({
        isEmpty:function(str){
            return str==null || str=='';
        },
        showMsg:function(id,msg){
            $("#"+id).html(msg);
        }
    });
})(jQuery);

(function($, window){
    window.alert = function(data) {
        $("#overrideAlertErrorNoticeDivMsg").html(data);
        $("#overrideAlertErrorNoticeDiv").modal({
            "backdrop": "static"
        });
    };

    var keepSession = function() {
        $.post("/rest/group/keepSession", {id:1}, function (data) {
            // do nothing
        });
    };

    setInterval(keepSession, 20*60*1000);

})(jQuery, window);