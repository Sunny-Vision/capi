<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div class="box-body quotation-record-list-container">
	<div class="form-horizontal">
		<div class="form-group">
			<c:if test="${model.normalAssignment}">
				<div class="hide">
				<label class="col-md-1 control-label">Dates</label>
				<div class="col-md-2">
					<input name="dateSelectedAssignmentId" value="<c:out value="${model.assignmentId}"/>"/>
				</div>
				</div>
			</c:if>
			<div class="consignment-counter-container hide">
				<label class="col-md-2 control-label">Consignment Counter</label>
				<div class="col-md-2">
					<select name="consignmentCounter" class="form-control filters select2ajax"
						data-allow-clear="true"
						data-ajax-url="<c:url value='/assignmentManagement/AssignmentMaintenance/queryNormalRevisitVerifyConsignmentSelect2'/>?userId=${model.userId}&quotationState=Normal"></select>
				</div>
			</div>
			<div class="unit-category-container hide">
				<label class="col-md-2 control-label">Unit Category</label>
				<div class="col-md-2">
					<select name="unitCategory" class="form-control filters select2ajax"
						data-allow-clear="true"
						data-ajax-url="<c:url value='/assignmentManagement/AssignmentMaintenance/queryNormalRevisitVerifyDistinctUnitCategorySelect2'/>?userId=${model.userId}&quotationState=Normal"></select>
				</div>
			</div>
		</div>
		
		<div class="form-group">
			<div class="col-md-12">
				<button class="btn btn-info btn-clear" type="button">Clear</button>
				<button type="button" class="btn btn-info assignmentUnitCategoryInfoDialog">View Unit Category</button>
				<sec:authorize access="hasPermission(#user, 256) or hasPermission(#user, 16)">
				<button type="button" class="btn btn-info btn-start-time-log">Start Time Log</button>
				<button type="button" class="btn btn-info btn-update-time-log">Update Time Log</button>
				</sec:authorize>
			</div>
		</div>
	</div>
	
	<table class="table table-striped table-bordered table-hover quotation-record-table"
		data-url="<c:url value="/assignmentManagement/AssignmentMaintenance/queryNormalRevisitVerifyQuotationRecord"/>?quotationState=Normal">
		<thead>
		<tr>
			<th>Form Type</th>
			<th>IsICP</th>
			<th>Variety</th>
			<th>NPrice</th>
			<th>SPrice</th>
			<th>Discount</th>
			<th>Status</th>
			<th>Availability</th>
			<th class="text-center action" data-priority="1"></th>
		</tr>
		</thead>
	</table>
</div>