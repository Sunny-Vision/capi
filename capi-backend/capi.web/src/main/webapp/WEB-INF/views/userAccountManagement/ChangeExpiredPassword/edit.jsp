<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>CAPI | Change Expired Password</title>
    <!-- Tell the browser to be responsive to screen width -->
     <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <!-- Bootstrap 3.3.5 -->
    <link rel="stylesheet" href="<c:url value='/resources/css/bootstrap.min.css'/>" />
	<!-- Theme style -->
    <link rel="stylesheet" href="<c:url value='/resources/css/AdminLTE.css'/>">
    <!-- AdminLTE Skins. Choose a skin from the css/skins
         folder instead of downloading all of them to reduce the load. -->
    <link rel="stylesheet" href="<c:url value='/resources/css/skins/_all-skins.min.css'/>">
	<!-- Font Awesome -->
    <link rel="stylesheet" href="<c:url value='/resources/css/font-awesome.min.css'/>" >
    <!-- Ionicons -->
    <link rel="stylesheet" href="<c:url value='/resources/css/ionicons.min.css'/>" >
	<link rel="stylesheet" href="<c:url value='/resources/css/common.css'/>" >
	
     <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
        <script src="<c:url value='/resources/js/html5shiv.min.js'/>"></script>
        <script src="<c:url value='/resources/js/respond.min.js'/>"></script>
    <![endif]-->
    <script src="<c:url value='/resources/js/jquery-1.11.1.min.js'/>"></script>
    <!-- Bootstrap 3.3.5 -->
    <script src="<c:url value='/resources/js/bootstrap.min.js'/>"></script>
    <%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
    <script>
			$(function() {
				var $mainForm = $('#mainForm');
				var pwMinLength = ${pwMinLength};
				var msgMinLength = "<spring:message code='E00162' arguments='${pwMinLength}' />";
				var validLength=0;
				var validUpper=0;
				var validLower=0;
				var validDigit=0;
				var validSpecial=0;
				var validTotal=0;

		    	 
		    	 function resetNewPasswordCheck() {
		    		validLength=0;
					validUpper=0;
					validLower=0;
					validDigit=0;
					validSpecial=0;
					refreshStatus();
		    	 }
		    	 
		    	 function refreshStatus() {
						console.log(
								"validLength:"+validLength+
								", validUpper:"+validUpper+
								", validLower:"+validLower+
								", validDigit:"+validDigit+
								", validSpecial:"+validSpecial);
						
						validTotal = validUpper+validLower+validDigit+validSpecial;
						
						
		    	 }
		    	
					 
		    	 
				$.validator.addMethod("pwcheckvalidclass", function(value, element, param) {
					 var $otherElement = $(param);
				    if ($otherElement == '') {
				    	resetNewPasswordCheck();
				    };

				    console.log("value.length:"+value.length);
				    if (value.length >= pwMinLength) {
				    	validLength=1;
				    } else {
				    	validLength=0;
				    }
				    
					
					if (/[A-Z]/.test(value)) {
						validUpper=1;
					} else {
						validUpper=0;
					}
					
					
					if (/[a-z]/.test(value)) {
						validLower=1
					} else {
						validLower=0;
					}
					
					if (/\d/.test(value)) {
						validDigit=1;
					} else {
						validDigit=0;
					}
					
					
					//if (/[`~!@#$%^&*()_=\[\]{};':"\\|,.<>\/?+-]/.test(value)) {
					if (/[^A-Za-z0-9]/.test(value)) {
						validSpecial=1;
					} else {
						validSpecial=0;
					}

					refreshStatus();					
					if (validTotal >=3 && validLength==1) {
						return true;
					}
					
					return false;
					
				}, function(params, element) {
					  
					var strengthMessage="";
		    		
					console.log("validLength:"+validLength);
		    		if (validLength==0) {
		    			strengthMessage+="- "+msgMinLength+"<br>";
		    		}
		    		
		    		if (validTotal <3) {
		    			strengthMessage+="- <spring:message code='E00163' /><br>";
		    		}

		    		/*
		    		if (validTotal <3 && validUpper==0) {
		    			strengthMessage+="<span class=\"suberror\">- <spring:message code='E00164' /></span><br>";
		    		}
		    		
		    		if (validTotal <3 && validLower==0) {
		    			strengthMessage+="<span class=\"suberror\">- <spring:message code='E00165' /></span><br>";
		    		}
		    		
		    		if (validTotal <3 && validDigit==0) {
		    			strengthMessage+="<span class=\"suberror\">- <spring:message code='E00166' /></span><br>";
		    		}
		    		
		    		if (validTotal <3 && validSpecial==0) {
		    			strengthMessage+="<span class=\"suberror\">- <spring:message code='E00167' /></span>";
		    		}
		    		*/
		    		return strengthMessage;
					
				});
		    	 
				
				$mainForm.validate({
					rules : {
						oldPassword:{
							required:true
						},
						newPassword : {
	    		  			pwcheckvalidclass: true,
	    		  			required:true
	    		  		},
						confirmPassword : {
							equalTo : "#newPw",
							required:true
						}
					},
					messages:{
						confirmPassword:{
							equalTo : "<spring:message code='E00164' />"
						}
					}
				});
			});
		</script>
    <style>
    .title-change-password {text-align:center; margin-bottom:10px}
	.suberror {padding-left:20px; font-weight:normal}
	
    </style>
  </head>
<body class="hold-transition login-page">
	<div class="login-box">
      <div class="login-logo">
      	<img src="<c:url value='/resources/images/logo.png'/>" style="border:0px" />
        <a href="#">CAPI</a>
      </div><!-- /.login-logo -->
      <div class="login-box-body">
       <div class="title-change-password">Your Password Expired, please change</div>
	     
	     <c:if test="${FAIL_MESSAGE != null}">
	     	<p class="login-box-msg" style="color:red">${FAIL_MESSAGE}</p>
		</c:if>

        <p class="login-box-msg"></p>
       
	     <form action="<c:url value='/userAccountManagement/ChangeExpiredPassword/save'/>" method="POST" id="mainForm">
          <div class="form-group has-feedback">
            <input type="password" id="oldPw" class="form-control" name="oldPassword" placeholder="Old Password">
            <span class="glyphicon glyphicon-lock form-control-feedback"></span>
          </div>
                    <div class="form-group has-feedback">
            <input type="password" id="newPw" class="form-control" name="newPassword" placeholder="New Password">
            <span class="glyphicon glyphicon-lock form-control-feedback"></span>
          </div>
                    <div class="form-group has-feedback">
            <input type="password" id="confirmPw" class="form-control" name="confirmPassword" placeholder="Confirm Password">
            <span class="glyphicon glyphicon-lock form-control-feedback"></span>
          </div>
          <div class="row">            
            <div class="col-xs-offset-8 col-xs-4">
              <button type="submit" class="btn btn-primary btn-block btn-flat">Save</button>
            </div><!-- /.col -->
          </div>
        </form>
        
      </div><!-- /.login-box-body -->
    </div><!-- /.login-box -->
</body>
</html>
