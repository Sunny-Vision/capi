<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="dashboard-section info-box ${bgColor}">
	<span class="info-box-icon"><i class="fa fa-th-list"></i></span>
	
	<div class="info-box-content">
		<div class="metric-container">
			<c:if test="${sectionModel.showMRPS}">
			<div class="upper-right-metric">
				<span class="metric">MRPS: ${sectionModel.mrpsCount} / ${sectionModel.mrpsTotal}</span><br/>
				<span class="metric">Others: ${sectionModel.othersCount} / ${sectionModel.othersTotal}</span>
			</div>
			</c:if>
			<div>
				<span class="info-box-text">${sectionTitle}</span>
				<c:if test="${sectionModel.showMetric}">
				<span class="info-box-number">${sectionModel.count}
				<c:if test="${sectionModel.total != null}"> / ${sectionModel.total}</c:if>
				</span>
				</c:if>
			</div>
		</div>
		
		<c:if test="${sectionModel.showProgress}">
		<div class="progress">
			<div class="progress-bar" style="width: ${sectionModel.percent}%;"></div>
		</div>
		<span class="progress-description">
			${sectionModel.percent}% Finished
		</span>
		</c:if>
		
		<c:if test="${sectionModel.showDeadlineTable}">
		<div class="child-section table-section">
			Deadline:
			<div class="table-container">
				<table class="table table-condensed">
					<c:forEach items="${sectionModel.deadlines}" var="deadline">
					<tr>
						<td>${deadline.date}</td>
						<td>${deadline.count}/${deadline.total}</td>
						<c:if test="${sectionModel.showDeadlineTableViewButton}">
						<sec:authorize access="hasRole('UF1405')">
						<td><a href="<c:url value="/assignmentManagement/AssignmentMaintenance/home"/>?deadline=${deadline.date}" class="btn btn-xs btn-info">View</a></td>
						</sec:authorize>
						</c:if>
					</tr>
					</c:forEach>
				</table>
			</div>
		</div>
		</c:if>
		
		<c:if test="${sectionModel.showVerficationRevisitCount}">
		<div class="child-section">
			<span>Verification: ${sectionModel.verification}</span>
			<span class="revisit-count">Revisit: ${sectionModel.revisit}</span>
		</div>
		</c:if>
		
		<c:if test="${sectionModel.showPendingApprovalList}">
		<div class="child-section table-section">
			<table class="table table-condensed">
				<tr>
					<td>Itinerary Plan</td>
					<td>${sectionModel.itineraryPlan}</td>
					<sec:authorize access="hasRole('UF1702')">
					<td><a href="<c:url value='/itineraryPlanning/ItineraryPlanApproval/home'/>" class="btn btn-xs btn-info">View</a></td>
					</sec:authorize>
				</tr>
				<tr>
					<td>Submitted Quotations</td>
					<td>${sectionModel.submittedAssignment}</td>
					<sec:authorize access="hasRole('UF1406')">
					<td><a href="<c:url value='/assignmentManagement/AssignmentApproval/home'/>" class="btn btn-xs btn-info">View</a></td>
					</sec:authorize>
				</tr>
				<tr>
					<td>Itinerary Check</td>
					<td>${sectionModel.itineraryCheck}</td>
					<sec:authorize access="hasRole('UF1802')">
					<td><a href="<c:url value='/timeLogManagement/ItineraryCheckingApproval/home'/>" class="btn btn-xs btn-info">View</a></td>
					</sec:authorize>
				</tr>
				<tr>
					<td>RUA</td>
					<td>${sectionModel.rua}</td>
					<sec:authorize access="hasRole('UF1410')">
					<td><a href="<c:url value='/assignmentManagement/RUACaseApproval/home'/>" class="btn btn-xs btn-info">View</a></td>
					</sec:authorize>
				</tr>
				<tr>
					<td>New Recruitment</td>
					<td>${sectionModel.newRecruitment}</td>
					<sec:authorize access="hasRole('UF2602')">
					<td><a href="<c:url value='/assignmentManagement/NewRecruitmentApproval/home'/>" class="btn btn-xs btn-info">View</a></td>
					</sec:authorize>
				</tr>
			</table>
		</div>
		</c:if>
		
		<c:if test="${sectionModel.showOutstandingQCAssignmentList}">
		<div class="child-section table-section">
			<table class="table table-condensed">
				<tr>
					<td>Spot Check</td>
					<td>${sectionModel.spotCheck}</td>
					<sec:authorize access="hasRole('RF2001')">
					<td><a href="<c:url value='/qualityControlManagement/SpotCheckMaintenance/home'/>" class="btn btn-xs btn-info">View</a></td>
					</sec:authorize>
				</tr>
				<tr>
					<td>Supervisory Visit</td>
					<td>${sectionModel.supervisoryCheck}</td>
					<sec:authorize access="hasRole('RF2002')">
					<td><a href="<c:url value='/qualityControlManagement/SupervisoryVisitMaintenance/home'/>" class="btn btn-xs btn-info">View</a></td>
					</sec:authorize>
				</tr>
				<tr>
					<td>PE Check</td>
					<td>${sectionModel.peCheck}</td>
					<sec:authorize access="hasRole('RF2003')">
					<td><a href="<c:url value='/qualityControlManagement/PostEnumerationCheckMaintenance/home'/>" class="btn btn-xs btn-info">View</a></td>
					</sec:authorize>
				</tr>
			</table>
		</div>
		</c:if>
		
		<c:if test="${sectionModel.showRUAViewAllButton}">
		<sec:authorize access="hasRole('UF1401')">
		<div class="child-section text-right">
			<a class="btn btn-info" href="<c:url value="/assignmentManagement/QuotationMaintenance/home"/>?status=RUA">View all</a>
		</div>
		</sec:authorize>
		</c:if>
		
		<c:if test="${sectionModel.showOutstandingViewAllButton}">
		<sec:authorize access="hasRole('UF2601')">
		<div class="child-section text-right">
			<a class="btn btn-info" href="<c:url value="/assignmentManagement/NewRecruitmentMaintenance/home"/>">View all</a>
		</div>
		</sec:authorize>
		</c:if>
	</div>
	<!-- /.info-box-content -->
</div>