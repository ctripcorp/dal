(function ($, window, document, undefined) {
    $(function () {
        bindViewButton();
    });

    function getDynamicDSData() {
        var table = $("#divTable");
        table.hide();
        $.getJSON("/rest/dynamicDS/executeCheckDynamicDS", function (data) {
            if (data == undefined || data == null) {
                return;
            }
            var tableBody = "";
            $.each(data, function (i, n) {
                var rowTemplate = "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>";
                tableBody += sprintf(rowTemplate, n.titanKey, n.appIds, n.hostIps, n.switchCount, n.successCount);
            });
            $("#tableDynamicDS tbody").html(tableBody);
            table.show();
        });
    }

    function bindViewButton() {
        $(document.body).on("click", "#viewButton", function () {
            getDynamicDSData();
        });
    }

})(jQuery, window, document);