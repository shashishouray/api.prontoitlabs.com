var appModule = angular.module('myApp', []);

appModule.controller('MainCtrl', ['mainService','$scope','$http',
        function(mainService, $scope, $http) {
	$scope.form={};
            $scope.greeting = 'JSON Web Token / AngularJS / Spring example!';
            $scope.token = null;
            $scope.error = null;
            $scope.roleUser = false;
            $scope.roleAdmin = false;
            $scope.roleFoo = false;

            $scope.login = function() {
                $scope.error = null;
                mainService.login($scope.userName,$scope.password).then(function(token) {
                    $scope.token = token;
                    $http.defaults.headers.common.Authorization = 'Bearer ' + token;
                    $scope.checkRoles();
                    if($scope.token !== null)
            		{
                $scope.isregister=false;    	
            	$scope.users();
            		}
                },
                function(error){
                    $scope.error = error
                    $scope.userName = '';
                    $scope.password = '';
                });
            }
            
            $scope.register = function() {
            	$scope.form;
					$http(
							{
								url : 'api.prontoitlabs.com/api/v1/user',
								headers: { 'Content-Type': undefined},
								method : 'POST', 
								transformRequest: function (data) {
					                var formData = new FormData();
					                //need to convert our json object to a string version of json otherwise
					                // the browser will do a 'toString()' on the object which will result 
					                // in the value '[Object object]' on the server.
					                //formData.append("model", angular.toJson(data.model));
					                for (var key in data.model) {
					                	formData.append(key, data.model[key]);
					                }
					               
					                return formData;
					            },
					            data: { model: $scope.form}
//									data : $scope.form 
							})
							.then(
									function(res) {
									console.log(res);
									$scope.regstatus=res.data.status;
									});
				
            }
            
            
            
           

            $scope.checkRoles = function() {
                mainService.hasRole('user').then(function(user) {$scope.roleUser = user});
                mainService.hasRole('admin').then(function(admin) {$scope.roleAdmin = admin});
                mainService.hasRole('foo').then(function(foo) {$scope.roleFoo = foo});
            }

            $scope.logout = function() {
                $scope.userName = '';
                $scope.password = '';
                $scope.token = null;
                $http.defaults.headers.common.Authorization = '';
            }

            $scope.loggedIn = function() {
            	
                return $scope.token !== null;
                
            }
            
            $scope.users = function()
            {	  
          	  $http.get("/api.prontoitlabs.com/api/v1/allusers?pn="+$scope.pageno+"&nor="+$scope.nor).then(function(res) {
          			$scope.userlist=res.data;							
          		});
            }
            
            
           
        } ]);

 



appModule.service('mainService', function($http) {
    return {
        login : function(username,password) {
            return $http.post('/api.prontoitlabs.com/api/v1/user/login', {userName: username,password: password}).then(function(response) {
                return response.data.token;
            });
        },

        hasRole : function(role) {
            return $http.get('/api/role/' + role).then(function(response){
                console.log(response);
                return response.data;
            });
        }
    };
});



