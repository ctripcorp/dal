(function(){
    $("ul[class*='ctrip-nav-action']").click(function(event){
//        $("li[class='active']").removeClass("active");
//        $(event.target.parentNode).addClass("active");
//        event.preventDefault();
//        location.href = event.target.href;
    });

    $(document).ready(function(){
        var href = location.href;
        $("li[class='active']").removeClass("active");
        if(href.indexOf("codeview")!=-1){
            $("#codeviewjsp").addClass("active");
        }else if(href.indexOf("dbmanage")!=-1){
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

    });
})();