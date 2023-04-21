<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>CAPI | Log in</title>
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
    	$(document).ready(function(){
    		$("#loginForm").validate({
    		  	rules:{
    		  		username : 'required',
    		  		password : 'required'
    			},
   			  	messages: {
   			  		username: "<spring:message code='E00010' />",
   			 		password: "<spring:message code='E00010' />"
				  }    			
    		})
    	})
    </script>
    <style>
    .message-success {text-align:center; color:#00c150}
    </style>
  </head>
<body class="hold-transition login-page">
	<div class="login-box">
      <div class="login-logo">
      	<img src="<c:url value='/resources/images/logo.png'/>" style="border:0px" />
        <a href="#">CAPI</a>
      </div><!-- /.login-logo -->
      <div class="login-box-body">
	     <c:if test="${not empty error}">
	     	<p class="login-box-msg" style="color:red"><spring:message code="${error}" arguments="${lockoutDuration}" /></p>
		</c:if>
		<c:if test="${SUCCESS_MESSAGE != null}">
			<p class="message-success">${SUCCESS_MESSAGE}</p>
		</c:if>
		
        <p class="login-box-msg"></p>
        <form action="<c:url value='/j_spring_security_check'/>" method="POST" id="loginForm">
          <div class="form-group has-feedback">
            <input type="text" class="form-control" name="username" placeholder="Username">
            <span class="glyphicon glyphicon-user form-control-feedback"></span>
          </div>
          <div class="form-group has-feedback">
            <input type="password" class="form-control" name="password" placeholder="Password">
            <span class="glyphicon glyphicon-lock form-control-feedback"></span>
          </div>
          <div class="row">            
            <div class="col-xs-offset-8 col-xs-4">
              <button type="submit" class="btn btn-primary btn-block btn-flat">Log in</button>
            </div><!-- /.col -->
          </div>
        </form>
        
      </div><!-- /.login-box-body -->
    </div><!-- /.login-box -->
</body>
</html>
