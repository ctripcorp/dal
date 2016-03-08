(function () {
    $(document.body).on("click", "#welcome-ver-list", function (event) {
        $("li[class='todo-done']").removeClass("todo-done");
        $(event.target).addClass("todo-done");
        $("div[class='vjs-poster']").html(" ").append("<img class=\"vjs-poster\" src=\"/static/Flat-UI-master/images/video/poster.jpg\" tabindex=\"-1\">");
    });

    $(window).load(function () {
        $("div[class='vjs-poster']").html(" ").append("<img class=\"vjs-poster\" src=\"/static/Flat-UI-master/images/video/poster.jpg\" tabindex=\"-1\">");
    });
})();