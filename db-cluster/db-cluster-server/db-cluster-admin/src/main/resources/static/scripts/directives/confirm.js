/**  确认框 */
directive_module.directive('dbclusterconfirmdialog', function ($compile, $window) {
    return {
        restrict: 'E',
        templateUrl: '../../views/directives/confirm-dialog.html',
        transclude: true,
        replace: true,
        scope: {
            dialogId: '=dbclusterDialogId',
            title: '=dbclusterTitle',
            instances: '=dbclusterInstances',
            showCancelBtn: '=dbclusterShowCancelBtn'
        },
        link: function (scope, element, attrs) {


        }
    }
});
