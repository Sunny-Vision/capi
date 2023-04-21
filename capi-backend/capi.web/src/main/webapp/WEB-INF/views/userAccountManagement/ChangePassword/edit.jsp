<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
<jsp:useBean id="niceDate" class="java.util.Date"/>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/timepicker-css.jsp" %>
	</jsp:attribute>
	<jsp:attribute name="header">
		<style>
			#map { height: 500px }
			#attachmentContainer {
				min-height: 300px;
			}
			#attachmentContainer .attachment {
				margin-bottom: 10px;
			}
			.btn.pull-right {
				margin-left: 10px;
			}
			
			.class-valid {color:#15b203 !important}
			.class-invalid {color:#f00 !important}
			.pwdesp {color:#aaa}
			#ic-error{display:none} 
			.suberror {padding-left:20px; font-weight:normal}
		</style>
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/timepicker.jsp" %>
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
						
						
						if (validLength==1) {
							$("#checkLength").removeClass("class-invalid");
							$("#checkLength").addClass("class-valid");
							$("#ic-ok").show();
							$("#ic-error").hide();
						} else {
							$("#checkLength").removeClass("class-valid");
							$("#checkLength").addClass("class-invalid");
							$("#ic-ok").hide();
							$("#ic-error").show();
						}
						
						validTotal = validUpper+validLower+validDigit+validSpecial;
						
						
						if (validUpper==1) {
							$("#checkUpper").addClass("class-valid");
						} else {
							$("#checkUpper").removeClass("class-valid");
						}
						
						if (validLower==1) {
							$("#checkLower").addClass("class-valid");
						} else {
							$("#checkLower").removeClass("class-valid");
						}
						
						if (validDigit==1) {
							$("#checkDigit").addClass("class-valid");
						} else {
							$("#checkDigit").removeClass("class-valid");
						}
						
						if (validSpecial==1) {
							$("#checkSpecial").addClass("class-valid");
						} else {
							$("#checkSpecial").removeClass("class-valid");
						}
						
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
					if(/[^0-9A-Za-z]/.test(value)){
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
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Change Password</h1>
        </section>
        
        <section class="content">
        	<form id="mainForm" action="<c:url value='/userAccountManagement/ChangePassword/save'/>" method="post" role="form"
        		enctype="multipart/form-data">
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<div class="box box-primary">
	       					<div class="box-body">
	       						<div class="form-horizontal">
									<div class="col-sm-12">
		       							<div class="form-group">
		       								<label class="col-sm-2 control-label">Old Password</label>
		       								<div class="col-sm-6">
												<input id="oldPw" name="oldPassword" type="password" class="form-control" maxlength="50" required />
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-2 control-label">New Password</label>
		       								<div class="col-sm-6">
												<input id="newPw" name="newPassword" type="password" class="form-control" maxlength="50"  />
											</div>
											<!--  TODO CR6 -->
											<div class="col-sm-10 col-sm-offset-2">
											<div id="checkLength" class="pwdesp"><i class="fa fa-check" id="ic-ok" aria-hidden="true"></i> <i class="fa fa-times-circle" id="ic-error" aria-hidden="true"></i> <spring:message code='E00162' arguments='${pwMinLength}' /></div>
											<div id="checkUpper" class="pwdesp"><i class="fa fa-check" aria-hidden="true"></i> Uppercase letters of English</div>
											<div id="checkLower" class="pwdesp"><i class="fa fa-check" aria-hidden="true"></i> Lowercase letters of English</div>
											<div id="checkDigit" class="pwdesp"><i class="fa fa-check" aria-hidden="true"></i> Base 10 digits</div>
											<div id="checkSpecial" class="pwdesp"><i class="fa fa-check" aria-hidden="true"></i> Non-alphanumeric characters (special characters)</div>
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-2 control-label">Confirm Password</label>
		       								<div class="col-sm-6">
												<input id="confirmPw" name="confirmPassword" type="password" class="form-control" maxlength="50" required />
											</div>
										</div>
									</div>
	       						</div>
	       					</div>
	       					<div class="box-footer">
	        					<button type="submit" class="btn btn-info">Submit</button>
	       					</div>
	        			</div>
	        		</div>
	        	</div>
        	</form>
        </section>
	</jsp:body>
</t:layout>
