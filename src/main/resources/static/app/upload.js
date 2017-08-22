const app = angular.module('uploadApp', []);

app.controller('UploadController', ($scope) => {

    $scope.filesSelected = false;
    $scope.uploadStatus = 'Datei auswählen';

    $scope.belegNummer = (element) => {

        let fileNames = [];

        for (let i = 0; i < element.files.length; i++) {

            fileNames.push(element.files[i].name);

        }
        $scope.fileCount = fileNames.length;
        $scope.uploadStatus = 'Dateien ausgewählt';
        $scope.files = fileNames;
        $scope.filesSelected = true;
        $scope.$apply();
    }

});