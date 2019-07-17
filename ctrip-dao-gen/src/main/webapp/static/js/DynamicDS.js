(function ($, window, document, undefined) {
    $(function () {
        init();
        bindViewButton();
        bindTitanKeySwitchData();
    });

    function getDynamicDSData(settingDate, checkTitanKeys) {
        var table = $("#divTable");
        var loading = $("#loadingSpan");
        loading.html("正在加载中。。。");
        table.hide();
        $.getJSON("/rest/dynamicDS/executeCheckDynamicDS", {
            settingDate: settingDate,
            checkTitanKeys: checkTitanKeys
        }, function (data) {
            if (data === undefined || data === null) {
                return;
            }
            if (data.statusCode === 1) {
                loading.html(sprintf("当前查询时间数据正在统计中...TitanKey总数%s，当前已完成%s", data.switchTitanKeyCount, data.statisticProgress));
                return;
            }
            else if (data.statusCode === 2) {
                loading.html(sprintf("目前系统正在统计%s时间数据...TitanKey总数%s，当前已完成%s", data.statisticTime, data.switchTitanKeyCount, data.statisticProgress));
                return;
            }
            var tableBody = "";
            var tableIndex = 0;
            $.each(data.dynamicDSDataList, function (i, n) {
                var rows = n.appIds.length;
                if (rows > 1) {
                    var rowContent1 = "<tr><td rowspan=\"%s\" id=\"%s\" data-titanKey=\"%s\">%s</td><td rowspan=\"%s\">%s</td><td rowspan=\"%s\" id=\"showDetails\"><a href = \"javascript:void(0)\">详情</a></td></tr>";
                    rows = rows + 1;
                    var titanKeyId = sprintf("titanKey%s",tableIndex);
                    tableBody += sprintf(rowContent1, rows, titanKeyId, n.titanKey, n.titanKey, rows, n.titanKeySwitchCount, rows);
                    tableIndex = tableIndex + rows;
                    $.each(n.appIds, function (j, m) {
                        // var rowContent2 = "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>";
                        // tableBody += sprintf(rowContent2, m.appID, m.hostIPs, m.hostSwitchCount, m.hostSuccessCount);
                        var rowContent2 = "<tr><td>%s</td><td>%s</td><td>%s</td></tr>";
                        tableBody += sprintf(rowContent2, m.appID, m.hostIPCount, m.appIDSwitchCount);
                    });
                    //tableBody += "<tr><td rowspan=\"2\" id=\"showDetails\"><a href = \"javascript:void(0)\">详情</a></td></tr>";
                    //tableBody += sprintf("<tr><td rowspan=\"%s\" id=\"showDetails\"><a href = \"javascript:void(0)\">详情</a></td></tr>", rows);
                }
                else if (rows === 1) {
                    var rowTemplate = "<tr><td id=\"%s\" data-titanKey=\"%s\">%s</td><td>%s</td><td id=\"showDetails\"><a href = \"javascript:void(0)\">详情</a></td><td>%s</td><td>%s</td><td>%s</td></tr>";
                    var appIDInfo = null;
                    $.each(n.appIds, function (j, m) {
                        appIDInfo = m;
                    });
                    var titanKeyId = sprintf("titanKey%s",tableIndex);
                    tableBody += sprintf(rowTemplate, titanKeyId, n.titanKey, n.titanKey, n.titanKeySwitchCount, appIDInfo.appID,  appIDInfo.hostIPCount,
                        appIDInfo.appIDSwitchCount);
                    tableIndex = tableIndex + 1;
                }
                else {
                    var rowTemplate = "<tr><td id=\"%s\" data-titanKey=\"%s\">%s</td><td>%s</td><td id=\"showDetails\"><a href = \"javascript:void(0)\">详情</a></td><td>%s</td><td>%s</td><td>%s</td></tr>";
                    var titanKeyId = sprintf("titanKey%s",tableIndex);
                    tableBody += sprintf(rowTemplate, titanKeyId, n.titanKey, n.titanKey, n.titanKeySwitchCount, "",  0, 0);
                    tableIndex = tableIndex + 1;
                }
            });
            //window.alert(tableBody);
            $("#tableDynamicDS tbody").html(tableBody);
            table.show();
            loading.html("");
        });
    }

    function bindViewButton() {
        $(document.body).on("click", "#viewButton", function () {
            var settingDate = $("#settingDate").val();
            if (settingDate === null) {
                return;
            }
            var date = formatDateTimeLoacl(settingDate);
            var limitDate = getNowTimeHour();

            if (date >= limitDate) {
                alert("设置事件不能大于" + limitDate);
                return;
            }
            var checkTitanKeys = $("#checkTitanKey").val();
            getDynamicDSData(settingDate, checkTitanKeys);
            var settime = $("#settingDate").val();
            var toDate = getFromDate(settime) + " to " + getToDate(settime);
            $("#settingDate").data("checkTime", getCheckTime(settime));
            $("#toDate").html(toDate);
        });
    }

    function bindTitanKeySwitchData() {
        $(document.body).on("click", "#showDetails", function () {

                var tr = $(this).closest("tr");
                var td = tr.find("td");
                var titanKey = td[0].innerHTML;
                var checkTime = $("#settingDate").data("checkTime");
                var titanKeySwitchUrl = sprintf("/rest/dynamicDS/getTitanKeySwitchTime?titanKey=%s&checkTime=%s", titanKey, checkTime);
                window.open(titanKeySwitchUrl, '_blank');
                // $.getJSON("/rest/dynamicDS/getTitanKeySwitchTime", {
                //     titanKey: titanKey
                // }, function (data) {
                //     window.alert("32423");
                //     window.alert(data);
                //     if (data === undefined || data === null) {
                //         return;
                //     }
                //     $.each(data, function (i, n) {
                //         $("#details").append(n);
                //         $("#details").html(data);
                //         this.alert(n);
                //         window.alert(n);
                //     });
                //     $("#details").append(data);
                // });
        });
    }

    function init(){
        var nowtime = new Date();
        nowtime.setHours(nowtime.getHours() - 1);
        var timeString = timeToString(nowtime);
        $("#settingDate").val(timeString);
        $("#settingDate").data("checkTime", getCheckTime(nowtime));
        var toDate = getFromDate(nowtime) + " to " + getToDate(nowtime);
        $("#toDate").html(toDate);
    }
    function timeToString(date) {
        var y = date.getFullYear();
        var m = date.getMonth() + 1;
        var d = date.getDate();
        var hour = date.getHours().toString();
        var minutes = 0;
        if (hour < 10) {
            hour = "0" + hour;
        }
        if (minutes < 10) {
            minutes = "0" + minutes;
        }
        return  y + '-' + (m < 10 ? ('0' + m) : m) + '-' + (d < 10 ? ('0' + d) : d) + "T" + hour + ":" + minutes ;
    }
    function getFromDate(time) {
        var date = new Date(time);
        var y = date.getFullYear();
        var m = date.getMonth() + 1;
        var d = date.getDate();
        var hour = date.getHours();
        var minutes = 0;
        if (hour < 10) {
            hour = "0" + hour;
        }
        if (minutes < 10) {
            minutes = "0" + minutes;
        }
        return  y + '/' + (m < 10 ? ('0' + m) : m) + '/' + (d < 10 ? ('0' + d) : d) + " " + hour + ":" + minutes ;
    }
    function getToDate(time) {
        var date = new Date(time);
        var y = date.getFullYear();
        var m = date.getMonth() + 1;
        var d = date.getDate();
        var hour = date.getHours() + 1;
        var minutes = 0;
        if (hour < 10) {
            hour = "0" + hour;
        }
        if (minutes < 10) {
            minutes = "0" + minutes;
        }
        return  y + '/' + (m < 10 ? ('0' + m) : m) + '/' + (d < 10 ? ('0' + d) : d) + " " + hour + ":" + minutes ;
    }

    function getCheckTime(time) {
        var date = new Date(time);
        var y = date.getFullYear();
        var m = date.getMonth() + 1;
        var d = date.getDate();
        var hour = date.getHours();
        if (hour < 10) {
            hour = "0" + hour;
        }
        return  y + (m < 10 ? ('0' + m) : m) + (d < 10 ? ('0' + d) : d) + hour;
    }

    function formatDateTimeLoacl(time) {
        var date = new Date();
        date.setFullYear(parseInt(time.substring(0, 4)));
        date.setMonth(parseInt(time.substring(5, 7)) - 1);
        date.setDate(parseInt(time.substring(8, 10)));
        date.setHours(parseInt(time.substring(11, 13)));
        date.setMinutes(parseInt(time.substring(14, 16)));
        return date;
    }

    function getNowTimeHour() {
        var date = new Date();
        date.setMinutes(0);
        date.setSeconds(0);
        date.setMilliseconds(0);
        return date;
    }
})(jQuery, window, document);