(function ($, window, document, undefined) {
    $(function () {
        bindDecrypt();
        bindClear();
    });

    function bindDecrypt() {
        $(document.body).on("click", "#btnDecrypt", function () {
            var encryptVal = $("#encrypt").val();
            if (encryptVal == null || encryptVal.length == 0) {
                return;
            }

            $.getJSON("/rest/decryption/decrypt", {encrypt: encryptVal}, function (data) {
                if (data.errorMsg != null && data.errorMsg.length > 0) {
                    alert(data.errorMsg);
                }
                else {
                    $("#decrypt").val(data.decryptMsg);
                }
            });
        });
    }

    function bindClear() {
        $(document.body).on("click", "#btnClear", function () {
            $("#encrypt").val("");
            $("#decrypt").val("");
        });
    }

})(jQuery, window, document);