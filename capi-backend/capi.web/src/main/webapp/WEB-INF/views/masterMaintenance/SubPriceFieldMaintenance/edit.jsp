<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page session="false"%>
<t:layout>
	<jsp:attribute name="header">
<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
<script>

$(function() {
	var $mainForm = $('#mainForm');
	$mainForm.validate();
	
	<sec:authorize access="!hasPermission(#user, 256)">
		viewOnly();
	</sec:authorize>
});
</script>
</jsp:attribute>
	<jsp:body>
		<section class="content-header">
			<h1>Sub-Price Field Maintenance</h1>
			<c:if test="${not empty model.subPriceFieldId}">
				<div class="breadcrumb form-horizontal" style="width:240px">
					<div class="form-group" style="margin-bottom:0px">
			        	<div class="col-sm-5">Created Date:</div>
			        	<div class="col-sm-7">${commonService.formatDateTime(model.createdDate)}</div>
			        </div>
			        <div class="form-group" style="margin-bottom:0px">
			         	<div class="col-sm-5">Last Modified:</div>
			         	<div class="col-sm-7">${commonService.formatDateTime(model.modifiedDate)}</div>
			         </div>
		      	</div>
	      	</c:if>
		</section>
		<section class="content">
		    <div class="row">
				<div class="col-md-12">
					<!-- content -->
				
					<!-- Itinerary Planning Table -->
					<div class="box box-primary">
						<div class="box-header with-border">
							<a class="btn btn-default" href="<c:url value='/masterMaintenance/SubPriceFieldMaintenance/home'/>">Back</a>
						</div>
						<!-- /.box-header -->
						<!-- form start -->
						<form class="form-horizontal" action="<c:url value='/masterMaintenance/SubPriceFieldMaintenance/save'/>" method="post" role="form" id="mainForm">
							<div class="box-body">
								<div class="form-group">
									<label for="inputEmail3" class="col-sm-2 control-label">Field Name</label>
									<div class="col-sm-6">
										<input type="hidden" name="subPriceFieldId" value="<c:out value="${model.subPriceFieldId}" />" required>
										<input type="text" name="fieldName" class="form-control" placeholder="Field Name" value="<c:out value="${model.fieldName}" />" required>
									</div>
								</div>
								<div class="form-group">
									<label for="inputPassword3" class="col-sm-2 control-label">Field Type</label>
									<div class="col-sm-6">
										<select class="form-control" name="fieldType" required>
											<option value="Text" <c:if test="${model.fieldType == 'Text'}">Selected</c:if>>Text</option>
											<option value="Date" <c:if test="${model.fieldType == 'Date'}">Selected</c:if>>Date</option>
											<option value="Number" <c:if test="${model.fieldType == 'Number'}">Selected</c:if>>Number</option>
											<option value="Time" <c:if test="${model.fieldType == 'Time'}">Selected</c:if>>Time</option>
											<option value="Checkbox" <c:if test="${model.fieldType == 'Checkbox'}">Selected</c:if>>Check box</option>
										</select>
									</div>
								</div>
							</div>
						<!-- /.box-body -->						
							<sec:authorize access="hasPermission(#user, 256)">
								<div class="box-footer">
									<button type="submit" class="btn btn-info">Submit</button>
								</div>
							</sec:authorize>
						<!-- /.box-footer -->
						</form>
					</div>
				</div>
			</div>
		</section>		
	</jsp:body>
</t:layout>
