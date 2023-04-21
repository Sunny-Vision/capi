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
		</style>
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/timepicker.jsp" %>
		<script>
		$(function() {
			var $mainForm = $('#mainForm');
			
			$.validator.addMethod("greaterThan", function (value, element, param) {
		          var $otherElement = $(param);
		          return parseInt(value, 10) > parseInt($otherElement.val(), 10);
		    }, "<spring:message code='E00166' />");
				
			
			$mainForm.validate({
				rules : {
					unlockDuration:{
    		  			required:true,
    		  			digits:true,
    		  			min: 1
    		  		},
			  		maxAttempt : {
			  			required: true,
			  			digits:true,
			  			min:1
			  		},
			    	resetAttemptDuration:{
			    		required:true,
			    		digits:true,
	    		  		min:1
			    	},
			    	enforcePasswordHistory :{
			    		required:true,
			    		digits:true,
	    		  		min:1
			    	},
			    	maxAge : {
    		  			required:true,
    		  			digits:true,
    		  			min:1,
    		  			greaterThan:"#minAge"
    		  		},
    		  		minAge : {
    		  			required:true,
    		  			digits:true,
    		  		},
    		  		minLength : {
    		  			required:true,
    		  			digits:true,
    		  			min:1
    		  		},
    		  		notificationDate : {
    		  			required:true,
    		  			digits:true,
    		  			min:1
    		  		}
				},
				messages:{
					unlockDuration:{
						digits:"<spring:message code='E00165' />",
						min:"<spring:message code='E00102' arguments='0' />"
					},
					maxAttempt:{
						digits:"<spring:message code='E00165' />",
						min:"<spring:message code='E00102' arguments='0' />"
					},
					resetAttemptDuration:{
						digits:"<spring:message code='E00165' />",
						min:"<spring:message code='E00102' arguments='0' />"
					},
					enforcePasswordHistory:{
						digits:"<spring:message code='E00165' />",
						min:"<spring:message code='E00102' arguments='0' />"
					},
					maxAge:{
						digits:"<spring:message code='E00165' />",
						min:"<spring:message code='E00102' arguments='0' />"
					},
					minAge:{
						digits:"<spring:message code='E00165' />",
						min:"<spring:message code='E00102' arguments='0' />"
					},
					minLength:{
						digits:"<spring:message code='E00165' />",
						min:"<spring:message code='E00102' arguments='0' />"
					},
					notificationDate:{
						digits:"<spring:message code='E00165' />",
						min:"<spring:message code='E00102' arguments='0' />"
					}
				}
			});
			
		});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Password Policy Maintenance</h1>
        </section>
        
        <section class="content">
        	<form  method="post" action="<c:url value='/userAccountManagement/PasswordPolicyMaintenance/saveParameters'/>" id="mainForm">
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<div class="box box-primary">
	       					<div class="box-body">
	       						<div class="form-horizontal">
									<div class="col-sm-12">
		       							<div class="form-group">
		       								<label class="col-sm-3 control-label">Lockout Duration</label>
		       								<div class="col-sm-4">
			       								<div class="input-group">
													<input name="unlockDuration" type="text" class="form-control numbers" maxlength="50" required value="<c:out value="${displayModel.getUnlockDuration()}" />" />
													<div class="input-group-addon filter-officer">
														Minutes
													</div>
												</div>
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-3 control-label">Maximum Attempt</label>
		       								<div class="col-sm-4">
			       								<div class="input-group">
													<input name="maxAttempt" type="text" class="form-control numbers" maxlength="50" required value="<c:out value="${displayModel.getMaxAttempt()}" />" />
													<div class="input-group-addon filter-officer">
														Times
													</div>
												</div>
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-3 control-label">Reset Attempt Counter Duration</label>
		       								<div class="col-sm-4">
			       								<div class="input-group">
													<input name="resetAttemptDuration" type="text" class="form-control numbers" maxlength="50" required value="<c:out value="${displayModel.getResetAttemptDuration()}" />" />
													<div class="input-group-addon filter-officer">
														Minutes
													</div>
												</div>
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-3 control-label">Enforce Password History</label>
		       								<div class="col-sm-4">
			       								<div class="input-group">
													<input name="enforcePasswordHistory" type="text" class="form-control numbers" maxlength="50" required value="<c:out value="${displayModel.getEnforcePasswordHistory()}" />" />
													<div class="input-group-addon filter-officer">
														Recent Passwords
													</div>
												</div>
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-3 control-label">Maximum Password Age </label>
		       								<div class="col-sm-4">
			       								<div class="input-group">
													<input name="maxAge" type="text" class="form-control numbers" maxlength="50" required value="<c:out value="${displayModel.getMaxAge()}" />" />
													<div class="input-group-addon filter-officer">
														Day(s)
													</div>
												</div>
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-3 control-label">Minimum Password Age</label>
		       								<div class="col-sm-4">
			       								<div class="input-group">
													<input name="minAge" id="minAge" type="text" class="form-control numbers" maxlength="50" required value="<c:out value="${displayModel.getMinAge()}" />" />
													<div class="input-group-addon filter-officer">
														Day(s)
													</div>
												</div>
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-3 control-label">Minimum Password Length</label>
		       								<div class="col-sm-4">
		       									<div class="input-group">
													<input name="minLength" type="text" class="form-control numbers" maxlength="50" required value="<c:out value="${displayModel.getMinLength()}" />" />
													<div class="input-group-addon filter-officer">
														Characters
													</div>
												</div>
											</div>
										</div>
										<!-- Notify user -->
										<div class="form-group">
		       								<label class="col-sm-3 control-label">Notify user password will be expired days</label>
		       								<div class="col-sm-4">
		       									<div class="input-group">
													<input name="notificationDate" type="text" class="form-control numbers" maxlength="50" required value="<c:out value="${displayModel.getNotificationDate()}" />" />
													<div class="input-group-addon filter-officer">
														Day(s)
													</div>
												</div>
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
