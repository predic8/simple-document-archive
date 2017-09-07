'use strict';

// TODO: change toggles

let app = angular.module('archiveApp', ['tableSort', 'xeditable']);

app.run((editableOptions) => {
    editableOptions.theme = 'bs3';
});

app.controller('AppController', ($scope, $http, $interval, $timeout) => {

    $scope.showVerifyTrue = false;
    $scope.showVerifyFalse = false;
    $scope.showVerifyAlert = false;

    $scope.fileSelected = false;

    $scope.reload = () => {
        $http.get('/rest/archive')
            .then((response) => {
                $scope.files = response.data;
            });
    }

    $scope.reload();
    //$interval($scope.reload, 5000);

    $scope.verify = () => {
        $scope.reload();
        $scope.showVerifyAlert = true;
        $http.get('/rest/archive/verify')
            .then((response) => {
                if (response.data.valid) {
                    $scope.showVerifyAlert = false;
                    $scope.showVerifyTrue = true;
                } else if (!response.data.valid && !response.data.fileIsMissing) {
                    $scope.errorMessage = 'Datei beschädigt !!!';
                    $scope.corruptedFile = response.data.corruptedFile;
                    $scope.showVerifyAlert = false;
                    $scope.showVerifyFalse = true;
                } else if (!response.data.valid && response.data.fileIsMissing) {
                    $scope.errorMessage = 'Datei nicht gefunden !!!';
                    $scope.corruptedFile = response.data.corruptedFile;
                    $scope.showVerifyAlert = false;
                    $scope.showVerifyFalse = true;
                }
            });
    }

    $scope.belegNummer = (element) => {
        $scope.fileSelected = true;
        let fileName = element.files[0].name;
        $scope.fileName = fileName;
        $scope.$apply();
    }

    $scope.updateBelegNr = (id, data) => {
        $http.put('/update/' + id, data)
            .then((response) => {
                $scope.reload();
            });
    }

    $scope.dlLog = () => {
        window.location = '/rest/archive/log';
    }

    $scope.gotoFile = (id) => {
        window.location = '/rest/archive/file/download/' + id;
    }

    $scope.hideVerifyTrue = () => {
        $scope.showVerifyTrue = false;
    }

    $scope.hideVerifyFalse = () => {
        $scope.showVerifyFalse = false;
    }
});

