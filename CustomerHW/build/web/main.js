var app = angular.module('Customer', []);
app.controller('MainController', ['$scope', '$http', function($scope, $http){
    var res = $http({
        method: "GET",
        url: "GetCustomer"
    });

    res.success(function(data, status, header, config) {
        $scope.customers = data;
    });
    res.error(function(data, status, header, config) {
        alert(JSON.stringify({data: data}));
    });


}]);
