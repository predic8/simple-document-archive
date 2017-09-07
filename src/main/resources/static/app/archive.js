'use strict';

// TODO: change toggles

let app = angular.module('archiveApp', ['tableSort']);

let validateForm = () => {
    let form = document.forms["singleUpload"]["belegNr"].value;
    if (form.includes(" ")) {
        alert("Keine Leerzeichen in der Belegnummer")
        return false;
    }
}

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

