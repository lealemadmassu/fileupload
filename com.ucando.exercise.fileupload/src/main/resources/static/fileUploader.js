var app=angular.module('fileUploader', ['ui.bootstrap']);

app.directive('fileToUpload', [ '$parse', function($parse) {
	return {
		restrict : 'A',
		link : function(scope, element, attrs) {
			var model = $parse(attrs.fileToUpload);
			var modelSetter = model.assign;

			element.bind('change', function() {
				scope.$apply(function() {
					modelSetter(scope, element[0].files[0]);
				});
			});
		}
	};
} ]);

app.service('FileReaderService', [ '$http', '$rootScope', function($http, $rootScope) {
	this.search = function(date,name) {
		$http.get("/fileupload/files", {
			params : {
			    date:date,
				name : name
			}
		}).success(function(response) {
			$rootScope.metadataList = response;
		}).error(function() {
		});
	}
}]);

app.service('fileUpload', ['$http','FileReaderService', function($http, FileReaderService) {
	this.uploadFileToUrl = function(uploadUrl, file, name, date) {
		var fd = new FormData();
		fd.append('file', file);
		fd.append('name', name);
		fd.append('date', date);
		
		$http.post(uploadUrl, fd, {
			transformRequest : angular.identity,
			headers : {
				'Content-Type' : undefined
			}
		}).success(function() {
		    FileReaderService.search(null, null);
		}).error(function() {
		});
	}
} ]);

app.controller('DownloadCtrl', ['$scope', function($scope) {
    $scope.downloadFile = function(metadata) {
        console.log('file is ' + JSON.stringify(metadata));
        var downloadUrl="/fileupload/file/"+metadata.id;
        window.open(downloadUrl, '_blank', '');  
    }
} ]);

app.controller('UploadCtrl', [ '$scope', 'fileUpload',function($scope, fileUpload) {
			$scope.uploadFile = function() {
				var file = $scope.myFile;
				var name = $scope.name;
				var date = $scope.date;
				$scope.progressVisible = false
				console.log('file is ' + JSON.stringify(file));
				var uploadUrl = "/fileupload/upload";
				fileUpload.uploadFileToUrl(uploadUrl, file, name, date);
			};
		} ]);

app.controller('ArchiveCtrl', function($scope, $http) {
	$scope.search = function(date,name) {
		$http.get("/fileupload/files", {
			params : {
			    date : date,
				name:name
			}
		}).success(function(response) {
			$scope.fileMetadatas = response;
		});
	};
});

app.controller('DatepickerCtrl', function ($scope) {
    $scope.minDate = 0;
    $scope.clear = function () {
      $scope.date = null;
    };

    $scope.open = function($event) {
      $event.preventDefault();
      $event.stopPropagation();

      $scope.opened = true;
    };

    $scope.dateOptions = {
      formatYear: 'yy',
      startingDay: 1
    };

    $scope.formats = ['dd-MMMM-yyyy','yyyy-MM-dd', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];
    $scope.format = $scope.formats[1];
  });

app.run(function($rootScope, $http) {
	$http.get("/archive/documents").success(
			function(response) {
				$rootScope.fileMetadatas = response;
			});
});
