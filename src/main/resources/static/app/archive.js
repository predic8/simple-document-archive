'use strict';

angular.module('archiveApp', ['base64'])
    /*
    .config(($httpProvider, $base64) => {
        let auth = $base64.encode("");
        $httpProvider.defaults.headers.common['Authorization'] = 'Basic ' + auth;
    })
    */
    .controller('AppController', ($scope, $http, $interval, $timeout) => {

        $scope.showVerifyTrue = false;
        $scope.showVerifyFalse = false;
        $scope.showVerifyAlert = false;

        $scope.reload = () => {
            $http.get('https://da.predic8.de/archive')
                .then((response) => {
                    $scope.files = response.data;
                });
        };

        $scope.reload();
        $interval($scope.reload, 5000);

        $scope.verify = () => {
            $scope.showVerifyAlert = true;
            $http.post('https://da.predic8.de/archive/verify')
                .then((response) => {

                    if (response.data.success === true) {
                        $scope.showVerifyAlert = false;
                        $scope.showVerifyTrue = true;
                        //$timeout(() => {
                          //  $scope.showVerifyTrue = false;
                        //}, 3000);
                    } else {
                        $scope.showVerifyAlert = false;
                        $scope.showVerifyFalse = true;
                        //$timeout(() => {
                          //  $scope.showVerifyFalse = false;
                        //}, 3000);
                    }
                });
        };

        $scope.hideVerifyTrue = () => {
            $scope.showVerifyTrue = false;
        };

        $scope.hideVerifyFalse = () => {
            $scope.showVerifyFalse = false;
        };

        $scope.mail = () => {
            $http.post('https://da.predic8.de/archive/mail');
        };
    });

