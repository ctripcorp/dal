(function ($, window, document, undefined) {
    $(function () {
        bindViewButton();
    });

    function getDynamicDSData(settingDate) {
        var table = $("#divTable");
        table.hide();
        $.getJSON("/rest/dynamicDS/executeCheckDynamicDS", {settingDate: settingDate}, function (data) {
            if (data === undefined || data === null) {
                return;
            }
            var tableBody = "";
            $.each(data, function (i, n) {
                var rowTemplate = "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>";
                tableBody += sprintf(rowTemplate, n.titanKey, n.titanKeySwitchCount, n.appIds, n.hostIps, n.hostSwitchCount, n.hostSuccessCount);
            });
            $("#tableDynamicDS tbody").html(tableBody);
            table.show();
        });
    }

    function bindViewButton() {
        $(document.body).on("click", "#viewButton", function () {
            var settingDate = $("#settingDate").val();
            if (settingDate === null) {
                return;
            }
            getDynamicDSData(settingDate);
        });
    }

})(jQuery, window, document);