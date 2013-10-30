
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

    
    $.get("/console/dal/das/monitor/timeCosts", function(data){

        $.each(data.ids, function(index, value){
            $("#tasks").append($('<option>', {
                    value: value,
                    text: value
                }));
        });

        $("#tasks").trigger('change');

    });

    $("#tasks").change(function(){

        var selectedTask = $(this).val();

        if(undefined == selectedTask || "" == selectedTask){
            return;
        }

        $.get("/console/dal/das/monitor/timeCosts/"+selectedTask,function(data){

            var entries = data.entries;

            var totalTime = 0;
            var decodeResponseTime = 0;
            var encodeRequestTime = 0;
            var decodeRequestTime = 0;
            var dbTime = 0;
            var encodeResponseTime = 0;

            if(undefined == entries){
                return;
            }

            $.each(entries, function(index, value){
                switch(value.stage){
                    case "totalTime":
                    totalTime = value.cost;
                    break;
                   case "decodeResponseTime":
                    decodeResponseTime = value.cost;
                    break;
                    case "encodeRequestTime":
                    encodeRequestTime = value.cost;
                    break;
                    case "decodeRequestTime":
                    decodeRequestTime = value.cost;
                    break;
                    case "dbTime":
                    dbTime = value.cost;
                    break;
                    case "encodeResponseTime":
                    encodeResponseTime = value.cost;
                    break;
                }
            });

            var otherTime = totalTime -encodeRequestTime - decodeResponseTime -dbTime - encodeResponseTime - decodeRequestTime;

            if(totalTime <= 0 || encodeRequestTime < 0 ||
                decodeResponseTime < 0 || dbTime < 0||
                encodeResponseTime < 0 || decodeRequestTime < 0){
                return;
            }

            $('#pie_container').highcharts({
                chart: {
                    plotBackgroundColor: null,
                    plotBorderWidth: null,
                    plotShadow: false
                },
                title: {
                    text: sprintf('Total time of task: %s milliseconds', totalTime)
                },
                tooltip: {
                    pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
                },
                plotOptions: {
                    pie: {
                        allowPointSelect: true,
                        cursor: 'pointer',
                        dataLabels: {
                            enabled: true,
                            color: '#000000',
                            connectorColor: '#000000',
                            format: '<b>{point.name}</b>: {point.percentage:.1f} %'
                        }
                    }
                },
                series: [{
                    type: 'pie',
                    name: 'Time cost percentage',
                    data: [
                        [sprintf('Client Encode Request: %s milliseconds', encodeRequestTime),   encodeRequestTime/totalTime],
                        [sprintf('Server Decode Request: %s milliseconds', decodeRequestTime),    decodeRequestTime/totalTime],
                        // {
                        //     name: sprintf('Other time: %s milliseconds', 
                        //         ),
                        //     y: 12.8,
                        //     sliced: true,
                        //     selected: true
                        // },
                        [sprintf('Other time: %s milliseconds', otherTime), otherTime / totalTime],
                        [sprintf('DbTime: %s milliseconds', dbTime),    dbTime/totalTime],
                        [sprintf('Server Encode Response: %s milliseconds', encodeResponseTime),    encodeResponseTime/totalTime],
                        [sprintf('Client Decode Response: %s milliseconds', decodeResponseTime),   decodeResponseTime/totalTime]
                    ]
                }]
            });
        });

    });

});