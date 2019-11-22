(function ($, window, document, undefined) {
    $(function () {
        init();
        bindViewButton();
        bindTitanKeySwitchData();
        bindTabSwitch();
        bindViewRangeButton();
        bindTitanKeyButton();
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
                        var rowContent2 = "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>";
                        tableBody += sprintf(rowContent2, m.appID, m.hostIPCount, m.appIDSwitchCount, m.appIDSuccessCount);
                    });
                    //tableBody += "<tr><td rowspan=\"2\" id=\"showDetails\"><a href = \"javascript:void(0)\">详情</a></td></tr>";
                    //tableBody += sprintf("<tr><td rowspan=\"%s\" id=\"showDetails\"><a href = \"javascript:void(0)\">详情</a></td></tr>", rows);
                }
                else if (rows === 1) {
                    var rowTemplate = "<tr><td id=\"%s\" data-titanKey=\"%s\">%s</td><td>%s</td><td id=\"showDetails\"><a href = \"javascript:void(0)\">详情</a></td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>";
                    var appIDInfo = null;
                    $.each(n.appIds, function (j, m) {
                        appIDInfo = m;
                    });
                    var titanKeyId = sprintf("titanKey%s",tableIndex);
                    tableBody += sprintf(rowTemplate, titanKeyId, n.titanKey, n.titanKey, n.titanKeySwitchCount, appIDInfo.appID,  appIDInfo.hostIPCount,
                        appIDInfo.appIDSwitchCount, appIDInfo.appIDSuccessCount);
                    tableIndex = tableIndex + 1;
                }
                else {
                    var rowTemplate = "<tr><td id=\"%s\" data-titanKey=\"%s\">%s</td><td>%s</td><td id=\"showDetails\"><a href = \"javascript:void(0)\">详情</a></td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>";
                    var titanKeyId = sprintf("titanKey%s",tableIndex);
                    tableBody += sprintf(rowTemplate, titanKeyId, n.titanKey, n.titanKey, n.titanKeySwitchCount, "",  0, 0, 0);
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
                alert("设置时间不能大于" + limitDate);
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

    function bindTabSwitch() {
        $(document.body).on("click", "#hourReport", function () {
            $("#dynamicDS").show();
            $("#switchReport").hide();
            $("#titanKeyReport").hide();
        });
        $(document.body).on("click", "#weekReport", function () {
            $("#dynamicDS").hide();
            $("#switchReport").show();
            $("#titanKeyReport").hide();
        });
        $(document.body).on("click", "#titanKeyReportTab", function () {
            $("#dynamicDS").hide();
            $("#switchReport").hide();
            $("#titanKeyReport").show();
        });
    }

    function bindViewRangeButton() {
        $(document.body).on("click", "#viewRangeButton", function () {
            var settingStartDate = $("#settingStartDate").val();
            var settingEndDate = $("#settingEndDate").val();
            if (settingStartDate === null || settingEndDate === null) {
                return;
            }
            var startDate = formatDateTimeLoacl(settingStartDate);
            var endDate = formatDateTimeLoacl(settingEndDate);
            if (startDate >= endDate) {
                alert("设置开始时间不能大于结束时间");
                return;
            }
            getDynamicDSDataRange(settingStartDate, settingEndDate);
        });
    }

    function getDynamicDSDataRange(settingStartDate, settingEndDate) {
        var table = $("#divTable2");
        var loading = $("#loadingSpan2");
        loading.html("正在加载中。。。");
        table.hide();
        $.getJSON("/rest/dynamicDS/getSwitchDSDataOneWeek", {
            startCheckTime: settingStartDate,
            endCheckTime: settingEndDate
        }, function (data) {
            if (data === undefined || data === null) {
                return;
            }
            var tableBody = "";
            var titanKeySum = 0;
            var titanKeySwitchCountSum = 0;
            var clientSwitchCountSum = 0;
            var rowTemplate = "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>";
            $.each(data, function (i, n) {
                tableBody += sprintf(rowTemplate, i + 1, n.titanKey, n.switchCount, n.appIDCount, n.ipCount, n.switchCount * n.ipCount);
                titanKeySum++;
                titanKeySwitchCountSum += n.switchCount;
                clientSwitchCountSum += n.switchCount * n.ipCount;
            });
            tableBody = sprintf(rowTemplate, "总数", titanKeySum, titanKeySwitchCountSum, "", "", clientSwitchCountSum) + tableBody;
            $("#tableDynamicDSWeek tbody").html(tableBody);
            table.show();
            loading.html("");
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

    function getTitanKeyInfo() {
        var table1 = $("#tableTitanKeyOneDay");
        var table2 = $("#tableAbnormalTitanKey");
        var table3 = $("#tableUnUseDynamicDSTitanKey");
        var tableDB = $("#tableTitanKeyDirectConnect");
        var tableMySql = $("#tableTitanKeyDirectConnectMySql");
        var tableSqlServer = $("#tableTitanKeyDirectConnectSqlServer");
        var loading = $("#loadingSpan3");
        var title = $("#abnormalTitanKeyTableTitle");
        var titleUnUse = $("#unUseDynamicDSTitanKeyTableTitle");
        var statisticsDate = $("#statisticsDate");
        loading.html("正在加载中。。。");
        title.html("");
        titleUnUse.html("");
        table1.hide();
        table2.hide();
        table3.hide();
        tableDB.hide();
        tableMySql.hide();
        tableSqlServer.hide();
        $.getJSON("/rest/titanKeyInfoReport/getTitanKeyInfoReport", function (data) {
            if (data === undefined || data === null) {
                return;
            }
            var rowTemplate = "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>";
            var tableBody = sprintf(rowTemplate, data.titanKeyCount, data.useMysqlCount, data.useSqlServerCount,
                data.directConnectDBCount, data.directConnectMysqlCount, data.directConnectSqlServerCount);

            var rowTemplateDB = "<tr><td>%s</td><td>%s</td><td>%s</td></tr>";
            var tableBodyDB = sprintf(rowTemplateDB, data.titanKeyCount, data.directConnectDBCount, toPercent(data.directConnectDBCount / data.titanKeyCount));

            var rowTemplateMySql = "<tr><td>%s</td><td>%s</td><td>%s</td></tr>";
            var tableBodyMySql = sprintf(rowTemplateMySql, data.useMysqlCount, data.directConnectMysqlCount, toPercent(data.directConnectMysqlCount / data.useMysqlCount));

            var rowTemplateSqlServer = "<tr><td>%s</td><td>%s</td><td>%s</td></tr>";
            var tableBodySqlServer = sprintf(rowTemplateSqlServer, data.useSqlServerCount, data.directConnectSqlServerCount, toPercent(data.directConnectSqlServerCount / data.useSqlServerCount));


            var tableBody1 = "";
            $.each(data.abnormalTitanKeyList, function (i, n) {
                var rowTemplate1 = "<tr><td>%s</td><td>%s</td><td>%s</td></tr>";
                var serverIp = "";
                var serverName = "";
                if (n.serverIp !== undefined && n.serverIp !== null) {
                    serverIp = n.serverIp;
                }
                if (n.serverName !== undefined && n.serverName !== null) {
                    serverName = n.serverName;
                }
                tableBody1 += sprintf(rowTemplate1, n.titanKey, serverIp, serverName);
            });

            var tableBody2 = "";
            $.each(data.unUseDynamicDSTitanKey, function (i, n) {
                var rowTemplate1 = "<tr><td>%s</td><td>%s</td></tr>";

                tableBody2 += sprintf(rowTemplate1, i + 1, n.name);
            });

            $("#tableTitanKeyOneDay tbody").html(tableBody);
            $("#tableAbnormalTitanKey tbody").html(tableBody1);
            $("#tableUnUseDynamicDSTitanKey tbody").html(tableBody2);
            $("#tableTitanKeyDirectConnect tbody").html(tableBodyDB);
            $("#tableTitanKeyDirectConnectMySql tbody").html(tableBodyMySql);
            $("#tableTitanKeyDirectConnectSqlServer tbody").html(tableBodySqlServer);
            statisticsDate.html(data.statisticsDate);
            table1.show();
            table2.show();
            table3.show();
            tableDB.show();
            tableMySql.show();
            tableSqlServer.show();
            loading.html("");
            title.html("TitanKey配置异常统计(TitanKey配置serverIp不是ip或者serverName不是域名)");
            titleUnUse.html("未使用Dal动态数据源的TitanKey统计(使用域名访问数据库)");
        });
    }

    function bindTitanKeyButton() {
        $(document.body).on("click", "#viewTitanKeyButton", function () {

        });
    }

    function init(){
        getTitanKeyInfo();
        var nowtime = new Date();
        nowtime.setHours(nowtime.getHours() - 1);
        var timeString = timeToString(nowtime);
        $("#settingDate").val(timeString);
        $("#settingDate").data("checkTime", getCheckTime(nowtime));
        $("#settingStartDate").val(timeString);
        $("#settingEndDate").val(timeString);
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

    function toPercent(point){
        var str=Number(point*100).toFixed(2);
        str+="%";
        return str;
    }
})(jQuery, window, document);