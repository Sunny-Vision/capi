<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div class="row">
	<form method="post" action="<c:url value='/masterMaintenance/BusinessParameterMaintenance/saveReasonForReports'/>" id="reasonForm" >
	<div class="col-md-12">
		<div class="form-horizontal">
			<h4>Reason for the report "Record of the allocation/reallocation of field work assignments"</h4>
			<div class="col-sm-12" id="fieldworkAssignmentReasonList">
				<c:forEach items="${displayModel.getReportReasonList()}" var="reportReason">
				<div class="form-group" data-id="${reportReason.getId()}">
					<div class="col-sm-6 col-sm-offset-2">
						<input type="hidden" name="fieldworkAssignmentReason[<c:out value="${reportReason.getId()}" />].reportReasonSettingId" value="<c:out value="${reportReason.getId()}" />" class="form-control"/>
						<input type="text" name="fieldworkAssignmentReason[${reportReason.getId()}].reason" class="form-control" value="<c:out value="${reportReason.getReason()}" />" required/>
					</div>
					<div class="col-sm-2">
						<button type="button" class="btn btn-info pull-right" onclick="deleteFieldworkAssignmentReason($(this))" data-id="${reportReason.getId()}">Delete</button>
					</div>
				</div>
				</c:forEach>
			</div>
			<button type="button" class="btn btn-info addReasonForReport">Add Reason</button>
		</div>
		<sec:authorize access="hasPermission(#user, 256)">
		<hr/>
		<div class="form-group">
			<button type="submit" class="btn btn-info">Submit</button>
		</div>
		</sec:authorize>
	</div>
	</form>
</div>