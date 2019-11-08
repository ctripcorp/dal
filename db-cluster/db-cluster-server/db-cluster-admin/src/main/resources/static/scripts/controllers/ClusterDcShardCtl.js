index_module.controller('ClusterCtl', ['$rootScope', '$scope', '$stateParams', '$window','$interval', '$location', 'toastr', 'AppUtil', 'ClusterService', 'ShardService', 'HealthCheckService', 'ProxyService',
    function ($rootScope, $scope, $stateParams, $window, $interval, $location, toastr, AppUtil, ClusterService, ShardService, HealthCheckService, ProxyService) {

        $scope.zones, $scope.shards;
        $scope.clusterName = $stateParams.clusterName;

        $scope.switchZone = switchZone;
        $scope.loadCluster = loadCluster;
        $scope.loadShards = loadShards;
        $scope.existsRoute = existsRoute;
        
        if ($scope.clusterName) {
            loadCluster();
        }
        
        function switchZone(zone) {
            $scope.currentZoneId = zone.zoneId;
            existsRoute($scope.activeDcName, $scope.currentDcName);
            loadShards($scope.clusterName, dc.dcName);
        }

        function loadCluster() {
            ClusterService.findClusterZones($scope.clusterName)
                .then(function (result) {
                    if (!result || result.length === 0) {
                        $scope.zones = [];
                        $scope.shards = [];
                        return;
                    }
                    $scope.zones = result;

                    if($scope.zones && $scope.zones.length > 0) {
	                    $scope.zones.forEach(function(zones){
	                    	if(zones.zoneId === $stateParams.currentZoneId) {
			                    $scope.currentZoneId = zones.zoneId;
	                    	}
	                    });
	                    
	                    if(!$scope.currentZoneId) {
                            $scope.currentZoneId = $scope.zones[0].zoneId;
                            loadShards($scope.clusterName, $scope.currentZoneId);
	                    } else {
                            loadShards($scope.clusterName, $scope.currentZoneId);
                        }
                    }

                }, function (result) {
                    toastr.error(AppUtil.errorMsg(result));
                });


        }

        function loadShards(clusterName, dcName) {
            ShardService.findClusterDcShards(clusterName, dcName)
                .then(function (result) {
                    $scope.shards = result;
                }, function (result) {
                    toastr.error(AppUtil.errorMsg(result));
                });
        }



        function existsRoute(activeDcName, backupDcName) {
            ProxyService.existsRouteBetween(activeDcName, backupDcName)
                .then(function (result) {
                    $scope.routeAvail = (result.state === 0);
                }, function (result) {
                    $scope.routeAvail = false;
                });
        }

    }]);