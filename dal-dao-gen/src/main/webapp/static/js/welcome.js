(function(){
    $("#welcome-ver-list").click(function(event){
        $("li[class='todo-done']").removeClass("todo-done");
        $(event.target).addClass("todo-done");
    });

})();