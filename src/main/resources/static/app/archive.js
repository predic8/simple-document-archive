'use strict';

angular.module('archiveApp', [])
    .controller('AppController', ($scope, $http, $interval, $timeout) => {

        $scope.showVerifyTrue = false;
        $scope.showVerifyFalse = false;
        $scope.showVerifyAlert = false;

        $scope.reload = () => {
            $http.get('http://localhost:8080/archive')
                .then((response) => {
                    $scope.files = response.data;
                });
        };

        $scope.reload();
        $interval($scope.reload, 5000);

        $scope.verify = () => {
            $scope.showVerifyAlert = true;
            $http.post('http://localhost:8080/archive/verify')
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
            $http.post('http://localhost:8080/archive/mail');
        };
    });

