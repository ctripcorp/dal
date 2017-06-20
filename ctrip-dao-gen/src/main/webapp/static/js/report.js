(function ($, window, document, undefined) {
    $(function () {
        getRaw();
        bindExportExcel();
        bindLocalDatasourceAppList();
        bindExportExcel2();
    });

    function getPieByDept(obj) {
        var pie = echarts.init(document.getElementById("divPie"));
        pie.showLoading();
        var table = $("#divTable");
        table.hide();

        $.getJSON("/rest/report/getReportByDept", {dept: obj}, function (data) {
            if (data == undefined || data == null) {
                pie.hideLoading();
                pie.clear();
                return;
            }

            var versions = new Array();
            var apps = new Array();
            var records = new Array();
            $.each(data, function (i, n) {
                //pie
                versions.push(n.version);
                var app = {name: n.version, value: n.apps.length};
                apps.push(app);

                //table
                $.each(n.apps, function (index, value) {
                    var record = {
                        version: n.version,
                        id: value.id,
                        name: value.name,
                        chineseName: value.chineseName,
                        owner: value.owner,
                        ownerEmail: value.ownerEmail
                    };
                    records.push(record);
                });
            });

            versions.sort();
            var option = {
                title: {
                    text: "DAL版本分布统计",
                    subtext: obj,
                    x: "center"
                },
                tooltip: {
                    trigger: "item",
                    formatter: "{a} <br/>{b} : {c} ({d}%)"
                },
                legend: {
                    orient: "vertical",
                    left: "left",
                    data: versions
                },
                series: [
                    {
                        name: "占比",
                        type: "pie",
                        radius: "55%",
                        center: ["50%", "60%"],
                        data: apps,
                        itemStyle: {
                            emphasis: {
                                shadowBlur: 10,
                                shadowOffsetX: 0,
                                shadowColor: "rgba(0, 0, 0, 0.5)"
                            }
                        }
                    }
                ]
            };

            //pie
            pie.hideLoading();
            pie.setOption(option);

            //info
            $("#spanCount").html(sprintf("共%s条记录", records.length));
            $("#spanLastUpdate").html(sprintf("上一次缓存更新时间：%s (缓存每小时更新一次)", data[0].lastUpdate));

            //table
            var tableBody = "";
            var rowTemplate = "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>";
            $.each(records, function (i, n) {
                tableBody += sprintf(rowTemplate, n.version, n.id, n.name, n.chineseName, n.owner, n.ownerEmail);
            });

            $("#thKey").html("版本");
            $("#tableVersion tbody").html(tableBody);
            table.show();
        });
    };

    function getPieByVersion(obj) {
        var pie = echarts.init(document.getElementById("divPie"));
        pie.showLoading();
        var table = $("#divTable");
        table.hide();

        $.getJSON("/rest/report/getReportByVersion", {version: obj}, function (data) {
            if (data == undefined || data == null) {
                pie.hideLoading();
                pie.clear();
                return;
            }

            var depts = new Array();
            var apps = new Array();
            var records = new Array();
            $.each(data, function (i, n) {
                //pie
                depts.push(n.dept);
                var app = {name: n.dept, value: n.apps.length};
                apps.push(app);

                //table
                $.each(n.apps, function (index, value) {
                    var record = {
                        dept: n.dept,
                        id: value.id,
                        name: value.name,
                        chineseName: value.chineseName,
                        owner: value.owner,
                        ownerEmail: value.ownerEmail
                    };
                    records.push(record);
                });
            });

            depts.sort();
            var option = {
                title: {
                    text: "BU分布统计",
                    subtext: obj,
                    x: "center"
                },
                tooltip: {
                    trigger: "item",
                    formatter: "{a} <br/>{b} : {c} ({d}%)"
                },
                legend: {
                    orient: "vertical",
                    left: "left",
                    data: depts
                },
                series: [
                    {
                        name: "占比",
                        type: "pie",
                        radius: "55%",
                        center: ["50%", "60%"],
                        data: apps,
                        itemStyle: {
                            emphasis: {
                                shadowBlur: 10,
                                shadowOffsetX: 0,
                                shadowColor: "rgba(0, 0, 0, 0.5)"
                            }
                        }
                    }
                ]
            };

            //pie
            pie.hideLoading();
            pie.setOption(option);

            //info
            $("#spanCount").html(sprintf("共%s条记录", records.length));
            $("#spanLastUpdate").html(sprintf("上一次缓存更新时间：%s (缓存每小时更新一次)", data[0].lastUpdate));

            //table
            var tableBody = "";
            var rowTemplate = "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>";
            $.each(records, function (i, n) {
                tableBody += sprintf(rowTemplate, n.dept, n.id, n.name, n.chineseName, n.owner, n.ownerEmail);
            });

            $("#thKey").html("BU");
            $("#tableVersion tbody").html(tableBody);
            table.show();
        });
    };

    function getRaw() {
        var divLoading = $("#divLoading");
        $.getJSON("/rest/report/getRaw", function (data) {
            divLoading.hide();
            if (data != null && data != undefined) {
                var depts = data.depts;
                if (depts != null && depts != undefined) {
                    var divDept = $("#divDept");
                    var template = "<span id=\"%s\" class=\"label label-primary span-margin cursor font-size\">%s</span>";
                    $.each(depts, function (i, n) {
                        var id = "spanDept" + i;
                        var html = sprintf(template, id, n);
                        divDept.append(html);
                        $(document.body).on("click", "#" + id, function () {
                            getPieByDept(n);
                        });
                    });
                }
                var versions = data.versions;
                if (versions != null && versions != undefined) {
                    var divVersion = $("#divVersion");
                    var template = "<span id=\"%s\" class=\"label label-default span-margin cursor font-size\">%s</span>";
                    $.each(versions, function (i, n) {
                        var id = "spanVersion" + i;
                        var html = sprintf(template, id, n);
                        divVersion.append(html);
                        $(document.body).on("click", "#" + id, function () {
                            getPieByVersion(n);
                        });
                    });
                }
                $("#divExport").show();
            }
        });
    };

    function bindExportExcel() {
        $(document.body).on("click", "#spanExport", function () {
            var form = $("<form>");
            form.attr("style", "display:none");
            form.attr("target", "");
            form.attr("method", "get");
            form.attr("action", "/rest/report/exportExcel");

            $(document.body).append(form);
            form.submit();
        });
    };

    function bindLocalDatasourceAppList() {
        $(document.body).on("click", "#anchorLocalDatasource,#spanRefresh", function () {
            getLocalDatasourceAppList();
        });
    }

    function getLocalDatasourceAppList() {
        var divLoading = $("#divLoading2");
        var divTable = $("#divTable2");
        divTable.hide();
        divLoading.show();
        $.getJSON("/rest/report/getLocalDatasourceAppList", function (data) {
            divLoading.hide();
            $("#spanCount2").html(sprintf("共%s条记录", data.length));

            var tableBody = "";
            var rowTemplate = "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>";
            $.each(data, function (i, n) {
                tableBody += sprintf(rowTemplate, n.id, n.orgName, n.name, n.chineseName, n.owner, n.ownerEmail);
            });

            $("#tableLocal tbody").html(tableBody);
            $("#divTable2").show();
        });
    }

    function bindExportExcel2() {
        $(document.body).on("click", "#spanExport2", function () {
            var form = $("<form>");
            form.attr("style", "display:none");
            form.attr("target", "");
            form.attr("method", "get");
            form.attr("action", "/rest/report/exportExcel2");

            $(document.body).append(form);
            form.submit();
        });
    };

})(jQuery, window, document);
