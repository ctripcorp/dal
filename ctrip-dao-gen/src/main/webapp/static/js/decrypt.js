(function ($, window, document, undefined) {
    $(function () {
        bindDecrypt();
        bindDecryptNetClog();
        bindDecryptNetCat();

        bindClear();
        bindClearNet();
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

    function bindDecryptNetClog() {
        $(document.body).on("click", "#btnDecryptNet", function () {
            var encryptVal = $("#encryptNet").val();
            if (encryptVal == null || encryptVal.length == 0) {
                return;
            }

            $.getJSON("/rest/decryption/decryptNetClog", {encrypt: encryptVal}, function (data) {
                if (data.errorMsg != null && data.errorMsg.length > 0) {
                    alert(data.errorMsg);
                }
                else {
                    $("#decryptNet").val(data.decryptMsg);
                }
            });
        });
    }

    function bindDecryptNetCat() {
        $(document.body).on("click", "#btnDecryptNet2", function () {
            var encryptVal = $("#encryptNet").val();
            if (encryptVal == null || encryptVal.length == 0) {
                return;
            }

            $.getJSON("/rest/decryption/decryptNetCat", {encrypt: encryptVal}, function (data) {
                if (data.errorMsg != null && data.errorMsg.length > 0) {
                    alert(data.errorMsg);
                }
                else {
                    $("#decryptNet").val(data.decryptMsg);
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

    function bindClearNet() {
        $(document.body).on("click", "#btnClearNet", function () {
            $("#encryptNet").val("");
            $("#decryptNet").val("");
        });
    }

})(jQuery, window, document);