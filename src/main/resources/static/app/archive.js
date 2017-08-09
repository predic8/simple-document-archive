'use strict';

angular.module('archiveApp', [])

    .controller('AppController', ($scope, $http, $interval, $timeout) => {

        $scope.showVerifyTrue = false;
        $scope.showVerifyFalse = false;
        $scope.showVerifyAlert = false;

        $scope.reload = () => {
            $http.get('/archive')
                .then((response) => {
                    $scope.files = response.data;
                });
        };

        $scope.reload();
        $interval($scope.reload, 5000);

        $scope.verify = () => {
            $scope.showVerifyAlert = true;
            $http.get('/archive/verify')
                .then((response) => {

                    if (response.data.success) {
                        $scope.showVerifyAlert = false;
                        $scope.showVerifyTrue = true;
                    } else if (!response.data.success && response.data.status === 'hashError') {
                        $scope.errorMessage = 'Files Corrupted !!!';
                        $scope.corruptedFile = response.data.file;
                        $scope.showVerifyAlert = false;
                        $scope.showVerifyFalse = true;
                    } else if (!response.data.success && response.data.status === 'fileNotFound') {
                        $scope.errorMessage = 'File not found !!!';
                        $scope.corruptedFile = response.data.file;
                        $scope.showVerifyAlert = false;
                        $scope.showVerifyFalse = true;
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
            $http.get('/archive/mail');
        };
    });

