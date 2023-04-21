<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div class="box-body quotation-record-list-container">
	<div class="form-horizontal">
		<label class="col-md-2 control-label">Consignment Counter</label>
		<div class="col-md-2">
			<select name="consignmentCounter" class="form-control filters select2ajax"
				data-allow-clear="true"
				data-ajax-url="<c:url value='/assignmentManagement/NewRecruitmentMaintenance/queryNormalConsignmentSelect2'/>?assignmentId=${model.assignmentId}&userId=${model.userId}"></select>
		</div>
		<div class="form-group">
			<label class="col-md-2 control-label">Unit Category</label>
			<div class="col-md-2">
				<select name="unitCategory" class="form-control filters select2ajax"
					data-allow-clear="true"
					data-ajax-url="<c:url value='/assignmentManagement/NewRecruitmentMaintenance/queryNormalDistinctUnitCategorySelect2'/>?assignmentId=${model.assignmentId}&userId=${model.userId}"></select>
			</div>
		</div>
		
		<div class="form-group">
			<div class="col-md-12">
				<button class="btn btn-info btn-clear" type="button">Clear</button>
				<button type="button" class="btn btn-info assignmentUnitCategoryInfoDialog" data-assignmentunitcategoryinfodialog-allow-sorting="false">View Unit Category</button>
				<sec:authorize access="hasPermission(#user, 16) or hasPermission(#user, 256)">
				<button type="button" class="btn btn-info btn-start-time-log">Start Time Log</button>
				<button type="button" class="btn btn-info btn-update-time-log">Update Time Log</button>
				<button type="button" class="btn btn-danger btn-delete">Delete</button>
				</sec:authorize>
			</div>
		</div>
	</div>
	
	<form id="frmSubmit" method="post" action="<c:url value='/assignmentManagement/NewRecruitmentMaintenance/delete'/>">
		<table class="table table-striped table-bordered table-hover quotation-record-table"
			data-url="<c:url value="/assignmentManagement/NewRecruitmentMaintenance/queryNormalQuotationRecord"/>">
			<thead>
			<tr>
				<th class="text-center action"><input class="select_all" type="checkbox" /></th>
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
	</form>
</div>