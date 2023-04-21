<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
<sec:authentication property="details" var="userDetail" />
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-fixedHeader-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/jstree-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/unitLookup-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp"%>
	</jsp:attribute>
	<jsp:attribute name="header">
		<style>
.sticky-intersect{
	display: none;
}
.sticky-col th[rowspan="6"]{
	outline: thin solid white;
	padding: 8px;
}
.sticky-col th[rowspan="2"]{
	outline: thin solid white;
	padding: 8px;
}
.sticky-enabled{
	table-layout: auto;
}
.sticky-thead{
	table-layout: fixed;
}
.dateCol{
	width:3%
}
.staffHeadCol{
	/*max-width:88px;*/
	min-width:88px;
	width:8%;
}
.sessionCol{
	width:1%
}

/*
@media screen and (max-width: 1280px){
	td.dateCol, th.dateCol{
		min-width: 32px;
	}
}
@media screen and (min-width: 1920px){
	td.dateCol, th.dateCol{
		min-width: 48px;
	}
}*/
.sticky-thead {
	max-width: none;
}

.table-bordered > thead > tr > td, .table-bordered > tbody > tr > td, .table-bordered > tfoot > tr > td{
	border: 1px solid #cccccc;
    vertical-align: top;
}		
table.sticky-enabled th.holiday, table.sticky-enabled th.holiday, table.sticky-col th.holiday, table.sticky-intersect th.holiday, table.sticky-thead th.holiday{
	background-color: #CC3333;
}
table.sticky-enabled td.holiday, table.sticky-enabled td.holiday, table.sticky-col td.holiday, table.sticky-intersect td.holiday, table.sticky-thead td.holiday{
	background-color: #CC7777;
}
table.sticky-enabled td, table.sticky-enabled td, table.sticky-col td, table.sticky-intersect td, table.sticky-thead td{
	padding: 0px !important;
	height: 28px;
	text-align: center;
}
table.sticky-intersect th{
	display: none;
}
table#staffCalendarTable button, table#staffCalendarTable a.btn{
	width: 100%;
	text-align: center;
}
table#staffCalendarTable div.buttonGroup{
	white-space: nowrap;
	width: 100%;
}
table#staffCalendarTable div.buttonGroup:not(:first-child) {
	margin-top: 1px;
}
table#staffCalendarTable div.buttonGroup .${displayModel.getCalendarEventColor()} {
	padding-right: 16px;
	padding-left: 4px;
}
table#staffCalendarTable div.buttonGroup .btn-delete-cuz {
	width: 14px;
	z-index:1;
	padding: 2px 2px 2px 2px;
	margin-left: -18px;
}
table#staffCalendarTable .btn-add {
	min-width: 45px;
}


table.hideHoliday th.dateCol.holiday div.weekdayStr{
	display: none;
}

table.hideHoliday th.dateCol.holiday{
	padding:0px;
	min-width: 0px;
	width:2%
	/*max-width:10px*/
}
table.hideHoliday td.dateCol.holiday button.btn-add{
	display: none;
}
.table>thead>tr>th, .table>tbody>tr>th, .table>tfoot>tr>th, .table>thead>tr>td, .table>tbody>tr>td, .table>tfoot>tr>td{
	padding: 3px;
}

.staffCodeCol{
	width: 4%;
	text-align: center;
}
.staffCodeCol p{
	white-space: normal;
}

</style>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-fixedHeader.jsp"%>
			<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<script>
			$(function() {
				$('.select2ajax').select2ajax();
				$('[name="selectedUserId"]').change(function() {
					if ($(this).val() != null)
						location = '<c:url value='/' />?selectedUserId=' + $(this).val();
				});
				if (window.location.hash == 'staffCalendar') {
					location = window.location.href.replace('#staffCalendar','') + '#staffCalendar';
				}
				
				$("[name='showCalendar']").on('click', function(){
					var selectedUserId = $('[name="selectedUserId"]').val();
					window.location = "<c:url value='/' />?selectedUserId="+selectedUserId+"&showCalendar=1";
				});
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Welcome, ${userDetail.chineseName}</h1>
        </section>
        
        <section class="content">
			<c:if test="${model.showSelectStaff}">
       			<div class="form-horizontal">
       				<div class="form-group">
						<label class="col-md-1 control-label">Staff</label>
						<div class="col-md-3">
							<select name="selectedUserId" class="form-control select2ajax"
								data-ajax-url="<c:url value='/querySubordinateSelect2'/>">
								<option value="${model.selectedUserId}"><c:out value="${model.selectedUserName}"/></option>
							</select>
						</div>
       				</div>
       			</div>
       		</c:if>
       		<c:if test="${not model.showSelectStaff}">
       			<input type="hidden" name="selectedUserId" class="form-control"></input>
       		</c:if>
       		
       		<c:if test="${model.showFieldTeamHead}">
        	<div class="row">
        		<div class="col-md-12">
		        	<div class="box box-primary">
		        		<div class="box-header with-border">
		        			<h3 class="box-title">Field Team Head</h3>
		        		</div>
		        		<div class="box-body">
				        	<div class="row">
				        		<div class="col-md-6">
				        			<c:set var="sectionTitle" value="Team Assignment" scope="request" />
        							<c:set var="bgColor" value="bg-green" scope="request" />
				        			<c:set var="sectionModel" value="${model.teamAssignment}" scope="request" />
				        			<%@include file="/WEB-INF/views/shared/dashboard/section.jsp"%>
								</div>
				        		<div class="col-md-6">
				        			<c:set var="sectionTitle" value="Team Quotation" scope="request" />
        							<c:set var="bgColor" value="bg-yellow" scope="request" />
				        			<c:set var="sectionModel" value="${model.teamQuotation}" scope="request" />
						        	<%@include file="/WEB-INF/views/shared/dashboard/section.jsp"%>
								</div>
							</div>
				        	<div class="row">
				        		<div class="col-md-6">
				        			<c:set var="sectionTitle" value="Pending Approval List" scope="request" />
        							<c:set var="bgColor" value="bg-blue" scope="request" />
				        			<c:set var="sectionModel" value="${model.pendingApprovalList}" scope="request" />
						        	<%@include file="/WEB-INF/views/shared/dashboard/section.jsp"%>
								</div>
				        		<div class="col-md-6">
				        			<c:set var="sectionTitle" value="Outstanding QC Assignment List" scope="request" />
        							<c:set var="bgColor" value="bg-blue" scope="request" />
				        			<c:set var="sectionModel" value="${model.outstandingQCAssignmentList}" scope="request" />
						        	<%@include file="/WEB-INF/views/shared/dashboard/section.jsp"%>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			</c:if>
       		
        	<c:if test="${model.showFieldSupervisor}">
        	<div class="row">
        		<div class="col-md-12">
		        	<div class="box box-primary">
		        		<div class="box-header with-border">
		        			<h3 class="box-title">Field Supervisor</h3>
		        		</div>
		        		<div class="box-body">
				        	<div class="row">
				        		<div class="col-md-6">
				        			<c:set var="sectionTitle" value="Team Assignment" scope="request" />
        							<c:set var="bgColor" value="bg-green" scope="request" />
				        			<c:set var="sectionModel" value="${model.teamAssignment}" scope="request" />
				        			<%@include file="/WEB-INF/views/shared/dashboard/section.jsp"%>
								</div>
				        		<div class="col-md-6">
				        			<c:set var="sectionTitle" value="Team Quotation" scope="request" />
        							<c:set var="bgColor" value="bg-yellow" scope="request" />
				        			<c:set var="sectionModel" value="${model.teamQuotation}" scope="request" />
						        	<%@include file="/WEB-INF/views/shared/dashboard/section.jsp"%>
								</div>
							</div>
				        	<div class="row">
				        		<div class="col-md-6">
				        			<c:set var="sectionTitle" value="Pending Approval List" scope="request" />
        							<c:set var="bgColor" value="bg-blue" scope="request" />
				        			<c:set var="sectionModel" value="${model.pendingApprovalList}" scope="request" />
						        	<%@include file="/WEB-INF/views/shared/dashboard/section.jsp"%>
								</div>
				        		<div class="col-md-6">
				        			<c:set var="sectionTitle" value="Outstanding QC Assignment List" scope="request" />
        							<c:set var="bgColor" value="bg-blue" scope="request" />
				        			<c:set var="sectionModel" value="${model.outstandingQCAssignmentList}" scope="request" />
						        	<%@include file="/WEB-INF/views/shared/dashboard/section.jsp"%>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			</c:if>
			
        	<c:if test="${model.showFieldOfficer}">
        	<div class="row">
        		<div class="col-md-12">
		        	<div class="box box-primary">
		        		<div class="box-header with-border">
		        			<h3 class="box-title">Field Officer</h3>
		        		</div>
		        		<div class="box-body">
				        	<div class="row">
				        		<div class="col-md-6">
				        			<c:set var="sectionTitle" value="Assignment" scope="request" />
        							<c:set var="bgColor" value="bg-green" scope="request" />
				        			<c:set var="sectionModel" value="${model.assignment}" scope="request" />
				        			<%@include file="/WEB-INF/views/shared/dashboard/section.jsp"%>
								</div>
				        		<div class="col-md-6">
				        			<c:set var="sectionTitle" value="Quotation" scope="request" />
        							<c:set var="bgColor" value="bg-yellow" scope="request" />
				        			<c:set var="sectionModel" value="${model.quotation}" scope="request" />
						        	<%@include file="/WEB-INF/views/shared/dashboard/section.jsp"%>
								</div>
							</div>
				        	<div class="row">
				        		<div class="col-md-6">
				        			<c:set var="sectionTitle" value="RUA Case" scope="request" />
        							<c:set var="bgColor" value="bg-blue" scope="request" />
				        			<c:set var="sectionModel" value="${model.ruaCase}" scope="request" />
						        	<%@include file="/WEB-INF/views/shared/dashboard/section.jsp"%>
								</div>
				        		<div class="col-md-6">
				        			<c:set var="sectionTitle" value="Outstanding New Recruitment" scope="request" />
        							<c:set var="bgColor" value="bg-blue" scope="request" />
				        			<c:set var="sectionModel" value="${model.outstandingNewRecruitment}" scope="request" />
						        	<%@include file="/WEB-INF/views/shared/dashboard/section.jsp"%>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			</c:if>
			
        	<c:if test="${model.showIndoorDataConversion}">
        	<div class="row">
        		<div class="col-md-12">
		        	<div class="box box-primary">
		        		<div class="box-header with-border">
		        			<h3 class="box-title">Indoor Data Conversion</h3>
		        		</div>
		        		<div class="box-body">
		        			<div class="row">
		        				<div class="col-md-12">Current Month (${model.currentMonth}):</div>
		        			</div>
				        	<div class="row">
				        		<div class="col-md-6">
				        			<c:set var="sectionTitle" value="Data Conversion (Individual)" scope="request" />
        							<c:set var="bgColor" value="bg-yellow" scope="request" />
				        			<c:set var="sectionModel" value="${model.dataConversionCurrentMonth}" scope="request" />
				        			<%@include file="/WEB-INF/views/shared/dashboard/section.jsp"%>
								</div>
				        		<div class="col-md-6">
				        			<c:set var="sectionTitle" value="Data Verification (Individual)" scope="request" />
				        			<c:set var="bgColor" value="bg-red" scope="request" />
				        			<c:set var="sectionModel" value="${model.dataVerificationCurrentMonth}" scope="request" />
						        	<%@include file="/WEB-INF/views/shared/dashboard/section.jsp"%>
								</div>
							</div>
		        			<div class="row" style="margin-top: 50px">
		        				<div class="col-md-12">Previous Month (${model.previousMonth}):</div>
		        			</div>
				        	<div class="row">
				        		<div class="col-md-6">
				        			<c:set var="sectionTitle" value="Data Conversion (Individual)" scope="request" />
        							<c:set var="bgColor" value="bg-yellow" scope="request" />
				        			<c:set var="sectionModel" value="${model.dataConversionPreviousMonth}" scope="request" />
				        			<%@include file="/WEB-INF/views/shared/dashboard/section.jsp"%>
								</div>
				        		<div class="col-md-6">
				        			<c:set var="sectionTitle" value="Data Verification (Individual)" scope="request" />
				        			<c:set var="bgColor" value="bg-red" scope="request" />
				        			<c:set var="sectionModel" value="${model.dataVerificationPreviousMonth}" scope="request" />
						        	<%@include file="/WEB-INF/views/shared/dashboard/section.jsp"%>
								</div>
							</div>
							<div class="row">
								<div class="col-md-12 text-right">
									<sec:authorize access="hasRole('UF2101')">
									<a href="<c:url value='/dataConversion/QuotationRecordDataConversion/home'/>" class="btn btn-info">Go to data conversion</a>
									</sec:authorize>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			</c:if>
			
        	<c:if test="${model.showIndoorAllocatorSupervisor}">
        	<div class="row">
        		<div class="col-md-12">
		        	<div class="box box-primary">
		        		<div class="box-header with-border">
		        			<h3 class="box-title">Indoor Allocator, Indoor Supervisor</h3>
		        		</div>
		        		<div class="box-body">
		        			<div class="row">
		        				<div class="col-md-12">Current Month (${model.currentMonth}):</div>
		        			</div>
				        	<div class="row">
				        		<div class="col-md-6">
				        			<c:set var="sectionTitle" value="Data Collection" scope="request" />
        							<c:set var="bgColor" value="bg-green" scope="request" />
				        			<c:set var="sectionModel" value="${model.dataCollectionCurrentMonthSupervisor}" scope="request" />
				        			<%@include file="/WEB-INF/views/shared/dashboard/section.jsp"%>
								</div>
				        		<div class="col-md-6">
				        			<c:set var="sectionTitle" value="Data Conversion" scope="request" />
        							<c:set var="bgColor" value="bg-yellow" scope="request" />
				        			<c:set var="sectionModel" value="${model.dataConversionCurrentMonthSupervisor}" scope="request" />
						        	<%@include file="/WEB-INF/views/shared/dashboard/section.jsp"%>
								</div>
							</div>
				        	<div class="row">
				        		<div class="col-md-6">
				        			<c:set var="sectionTitle" value="Waiting for Allocation" scope="request" />
        							<c:set var="bgColor" value="bg-blue" scope="request" />
				        			<c:set var="sectionModel" value="${model.waitingForAllocationCurrentMonthSupervisor}" scope="request" />
				        			<%@include file="/WEB-INF/views/shared/dashboard/section.jsp"%>
								</div>
				        		<div class="col-md-6">
				        			<c:set var="sectionTitle" value="Data Verification" scope="request" />
				        			<c:set var="bgColor" value="bg-red" scope="request" />
				        			<c:set var="sectionModel" value="${model.dataVerificationCurrentMonthSupervisor}" scope="request" />
						        	<%@include file="/WEB-INF/views/shared/dashboard/section.jsp"%>
								</div>
							</div>
		        			<div class="row" style="margin-top: 50px">
		        				<div class="col-md-12">Previous Month (${model.previousMonth}):</div>
		        			</div>
				        	<div class="row">
				        		<div class="col-md-6">
				        			<c:set var="sectionTitle" value="Data Collection" scope="request" />
        							<c:set var="bgColor" value="bg-green" scope="request" />
				        			<c:set var="sectionModel" value="${model.dataCollectionPreviousMonthSupervisor}" scope="request" />
				        			<%@include file="/WEB-INF/views/shared/dashboard/section.jsp"%>
								</div>
				        		<div class="col-md-6">
				        			<c:set var="sectionTitle" value="Data Conversion" scope="request" />
        							<c:set var="bgColor" value="bg-yellow" scope="request" />
				        			<c:set var="sectionModel" value="${model.dataConversionPreviousMonthSupervisor}" scope="request" />
						        	<%@include file="/WEB-INF/views/shared/dashboard/section.jsp"%>
								</div>
							</div>
				        	<div class="row">
				        		<div class="col-md-6">
				        			<c:set var="sectionTitle" value="Waiting for Allocation" scope="request" />
        							<c:set var="bgColor" value="bg-blue" scope="request" />
				        			<c:set var="sectionModel" value="${model.waitingForAllocationPreviousMonthSupervisor}" scope="request" />
				        			<%@include file="/WEB-INF/views/shared/dashboard/section.jsp"%>
								</div>
				        		<div class="col-md-6">
				        			<c:set var="sectionTitle" value="Data Verification" scope="request" />
				        			<c:set var="bgColor" value="bg-red" scope="request" />
				        			<c:set var="sectionModel" value="${model.dataVerificationPreviousMonthSupervisor}" scope="request" />
						        	<%@include file="/WEB-INF/views/shared/dashboard/section.jsp"%>
								</div>
							</div>
							<div class="row">
								<div class="col-md-12 text-right">
									<sec:authorize access="hasRole('UF2201')">
									<a href="<c:url value='/QuotationRecordDataReview/home'/>" class="btn btn-info">Go to data review</a>
									</sec:authorize>
									<sec:authorize access="hasRole('UF2101')">
									<a href="<c:url value='/dataConversion/QuotationRecordDataConversion/home'/>" class="btn btn-info">Go to data conversion</a>
									</sec:authorize>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			</c:if>
			
			<sec:authorize access="hasRole('UF1303')">
			<div class="row">
				<c:choose>
					<c:when test="${showCalendar == 1}">
						<div class="col-md-12">
							<%@include file="/WEB-INF/views/shared/dashboard/staffCalendar.jsp"%>
						</div>
					</c:when>
					<c:otherwise>
						<div class="col-md-12">
							<div class="box">
								<div class="box-body">
									<button type="button" name="showCalendar" class="btn btn-info">Show Calendar</button>
								</div>
							</div>
						</div>
					</c:otherwise>
				</c:choose>
			</div>
			</sec:authorize>
		
        </section>		
	</jsp:body>
</t:layout>
