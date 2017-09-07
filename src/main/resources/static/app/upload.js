const app = angular.module('uploadApp', []);

let validateForm = () => {
    let forms = document.forms["multiUpload"]["belegNr"];

    forms.forEach(element => {
        if (element.value.includes(" ")) {
            alert("Keine Leerzeichen in der Belegnummer")
            return false;
        }
    });
}

app.controller('UploadController', ($scope) => {

    $scope.filesSelected = false;
    $scope.uploadStatus = 'Dateien auswählen';

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