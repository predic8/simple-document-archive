'use strict';

// TODO: change toggle logic

angular.module('archiveApp', ['tableSort'])

    .controller('AppController', ($scope, $http, $interval, $timeout) => {

        $scope.showVerifyTrue = false;
        $scope.showVerifyFalse = false;
        $scope.showVerifyAlert = false;

        $scope.reload = () => {
            $http.get('/rest/archive')
                .then((response) => {
                    $scope.files = response.data;
                });
        }

        $scope.reload();
        $interval($scope.reload, 5000);

        $scope.verify = () => {
            $scope.showVerifyAlert = true;
            $http.get('/rest/archive/verify')
                .then((response) => {
                    if (response.data.valid) {
                        $scope.showVerifyAlert = false;
                        $scope.showVerifyTrue = true;
                    } else if (!response.data.valid && !response.data.fileIsMissing) {
                        $scope.errorMessage = 'Files Corrupted !!!';
                        $scope.corruptedFile = response.data.corruptedFile;
                        $scope.showVerifyAlert = false;
                        $scope.showVerifyFalse = true;
                    } else if (!response.data.valid && response.data.fileIsMissing) {
                        $scope.errorMessage = 'File not found !!!';
                        $scope.corruptedFile = response.data.corruptedFile;
                        $scope.showVerifyAlert = false;
                        $scope.showVerifyFalse = true;
                    }
                });
        }

        $scope.gotoFile = (id) => {
            window.location = '/rest/archive/file/download/' + id;
        }

        $scope.uploadFile = () => {
            $http.post()
        }

        $scope.hideVerifyTrue = () => {
            $scope.showVerifyTrue = false;
        }

        $scope.hideVerifyFalse = () => {
            $scope.showVerifyFalse = false;
        }
    });

