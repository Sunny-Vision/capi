<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<sec:authentication property="details" var="userDetail" />
<input id="selectedAssignmentsHidden" value="<c:out value="${model.selectedAssignments}"/>" type="hidden" />
<input id="selectedQuotationsHidden" value="<c:out value="${model.selectedQuotations}"/>" type="hidden"/>
<input id="actualReleaseManDaysHidden" value="<c:out value="${model.actualReleaseManDaysBD}"/>" type="hidden"/>

<c:forEach var="assignment" items="${model.assignments}">
	<input name="assignmentIds" value="<c:out value="${assignment.id}"/>" type="hidden" />
</c:forEach>
<table class="table table-striped table-bordered table-hover" id="dataList">
	<thead>
	<tr>
		<th>Firm</th>
		<th>District</th>
		<th>TPU</th>
		<th>Address</th>
		<th>Start Date</th>
		<th>End Date</th>
		<th>No. of Quotation</th>
		<th>Required Man-Day</th>
		<th class="text-center action"></th>
	</tr>
	</thead>
	<tbody>
		<c:forEach var="assignment" items="${model.assignments}">
		<tr>
			<td><c:out value="${assignment.firm}"/></td>
			<td><c:out value="${assignment.district}"/></td>
			<td><c:out value="${assignment.tpu}"/></td>
			<td><c:out value="${assignment.address}"/></td>
			<td><c:out value="${assignment.startDate}"/></td>
			<td><c:out value="${assignment.endDate}"/></td>
			<td><c:out value="${assignment.noOfQuotation}"/></td>
			<td><c:out value="${assignment.requiredManDayBD}"/></td>
			<td>
				<a href="<c:url value='/assignmentManagement/PEViewAssignmentMaintenance/edit'/>?assignmentId=${assignment.id}" target="_blank"><i class="fa fa-list"></i></a>
				&nbsp;
				<a href="javascript:void(0)" class="table-btn btn-delete" data-id="<c:out value="${assignment.id}"/>"><span class="fa fa-times"></span></a>
			</td>
		</tr>
		</c:forEach>
	</tbody>
</table>