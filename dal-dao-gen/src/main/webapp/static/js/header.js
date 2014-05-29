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

})();