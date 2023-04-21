<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page session="false"%>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/select2-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/timepicker-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
	</jsp:attribute>
<jsp:attribute name="header">
	<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
	<%@include file="/WEB-INF/views/includes/select2.jsp"%>
	<%@include file="/WEB-INF/views/includes/timepicker.jsp" %>
	<%@include file="/WEB-INF/views/includes/datepicker.jsp"%>
	<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
	<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
	<%@include file="/WEB-INF/views/includes/moment.jsp" %>
	<script>

$(function() {

	var $mainForm = $('#mainForm');

	$("[name='startTime'], [name='endTime']").timepicker({
		showInputs: false,
		showMeridian: false,
		defaultTime: false,
		minuteStep: 1
    });

	$("[name='duration']").timepicker({
		showInputs: false,
		showMeridian: false,
		defaultTime: false,
		minuteStep: 15
    });

	$(".select2-addable").select2({
		 	tags: true,
		 	multiple: false,
	});

	jQuery.validator.addMethod(
		"activity_code_validate",
		function(value, element) {
			var activityCode = $("input[name='activityType']").val();
			if(activityCode != 3 ){
				if(value == ""){
					return false;
				}
			}
			return true;
		}, "<spring:message code='E00010' />");
});

</script>
</jsp:attribute>
	<jsp:body>
		<section class="content-header">
		<h1>Staff Calendar Event</h1>
		<c:if test="${model.id != null}">
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
							<a class="btn btn-default" href="<c:url value='/assignmentAllocationAndReallocation/StaffCalendar/home'/>">Back</a>
						</div>
						<!-- /.box-header -->
						<!-- form start -->
						<form class="form-horizontal" action="<c:url value='/assignmentAllocationAndReallocation/StaffCalendar/save'/>" method="post" role="form" id="mainForm">
							<div class="box-body">
								<input type="hidden" class="form-control" name="userId" required value="<c:out value="${model.user.userId}" />" readonly />
								<sec:authorize access="hasPermission(#user, 256) or hasPermission(#user, 4) or hasPermission(#user, 8)">
								<div class="form-group">
									<label for="" class="col-sm-2 control-label">Staff Code</label>
									<div class="col-sm-4">
										<div class="input-group">
											<input type="hidden" class="form-control" name="calendarEventId" value="<c:out value="${model.calendarEventId == null ? 0 : model.calendarEventId}" />" required />
											
											<input type="text" class="form-control" name="userDisplay" value="<c:if test="${modelUser != null}">${modelUser.staffCode} - ${modelUser.chineseName} ( <c:out value='${modelUser.destination}'/> )</c:if>" required readonly/>											
											<input type="hidden" class="form-control" name="userId" value="<c:out value="${model.user.userId}" />" required />
											<div class="input-group-addon searchUserId" style="visibility: hidden;">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								</sec:authorize>
								<div class="form-group radio-group">
									<label for="" class="col-sm-2 control-label" style="color:#333">Activity Code</label>
									<div class="col-sm-1">
										<label class="radio-inline"> 
											<input type="radio" name="activityType" value="1" <c:if test="${model.activityType == 1}">checked</c:if> required disabled /> OT Time
										</label>
									</div>
									<div class="col-sm-1">
										<label class="radio-inline">
											<input type="radio" name="activityType" value="2" <c:if test="${model.activityType == 2}">checked</c:if> required disabled /> Time Off
										</label>
									</div>
									<div class="col-sm-1">
										<label class="radio-inline">
											<input type="radio" name="activityType" value="3" <c:if test="${model.activityType == 3}">checked</c:if> required disabled /> Others
										</label>
									</div>
									<div class="col-sm-2">
										<select class="form-control select2-addable col-sm-2" name="activityCodeId" required <c:if test="${model.activityType != 3}">disabled</c:if> disabled >
											<option value=""></option>
											<c:forEach items="${activityList}" var="activity">
												<option value="<c:out value="${activity.activityCodeId}" />" data-manday="${activity.manDay}" <c:if test="${model.activityCode.activityCodeId != null && model.activityCode.activityCodeId == activity.activityCodeId}">selected</c:if>>${activity.code} - ${activity.description}</option>
											</c:forEach>
										</select>
									</div>
								</div>
								<div class="form-group">
									<label for="" class="col-sm-2 control-label">Date</label>
									<div class="col-sm-2">
										<div class="input-group">
											<fmt:formatDate value="${model.eventDate}" var="eventDateStr" type="date" pattern="dd-MM-yyyy" />
											<input type="text" class="form-control date-picker" data-orientation="top"
												name="eventDate" required value="<c:out value="${eventDateStr}" />" readonly />
											<div class="input-group-addon" style="visibility: hidden;">
												<i class="fa fa-calendar"></i>
											</div>
										</div>
									</div>
								</div>
								<div class="form-group OTTO-others " <c:if test="${model.activityType != 3}">style="display: none"</c:if>>
									<label for="" class="col-sm-2 control-label">Man-day</label>
									<div class="col-sm-2">
										<input name="manDay" type="text" class="form-control" value="<c:out value="${model.manDay}" />" required readonly />
									</div>
								</div>
								<div class="form-group radio-group">
									<label for="" class="col-sm-2 control-label" style="color:#333">Session</label>
									<div class="col-sm-1">
										<label class="radio-inline"> 
											<input type="radio" name="session" value="A" <c:if test="${model.session == 'A'}">checked</c:if> required disabled /> A
										</label>
									</div>
									<div class="col-sm-1">
										<label class="radio-inline">
											<input type="radio" name="session" value="P" <c:if test="${model.session == 'P'}">checked</c:if> required disabled /> P
										</label>
									</div>
									<div class="col-sm-1">
										<label class="radio-inline">
											<input type="radio" name="session" value="E" <c:if test="${model.session == 'E'}">checked</c:if> required disabled /> E
										</label>
									</div>
								</div>
								<div class="form-group OTTO" <c:if test="${model.activityType == 3}">style="display: none"</c:if>>
									<label for="" class="col-sm-2 control-label">Start Time</label>
									<div class="col-sm-2">
										<div class="input-group bootstrap-timepicker">
											<input name="startTime" type="text" class="form-control timepicker" value="<c:out value="${model.startTime}" />" data-rule-activity_code_validate="true" readonly />
											<div class="input-group-addon" style="visibility: hidden;">
												<i class="fa fa-clock-o"></i>
											</div>
										</div>
									</div>
								</div>
								<div class="form-group OTTO" <c:if test="${model.activityType == 3}">style="display: none"</c:if>>
									<label for="" class="col-sm-2 control-label">End Time</label>
									<div class="col-sm-2">
										<div class="input-group bootstrap-timepicker">
											<input name="endTime" type="text" class="form-control timepicker" value="<c:out value="${model.endTime}" />" data-rule-activity_code_validate="true" readonly />
											<div class="input-group-addon" style="visibility: hidden;">
												<i class="fa fa-clock-o"></i>
											</div>
										</div>
									</div>
								</div>
								<div class="form-group OTTO" <c:if test="${model.activityType == 3}">style="display: none"</c:if>>
									<label for="" class="col-sm-2 control-label">Duration</label>
									<div class="col-sm-2">
										<div class="input-group bootstrap-timepicker">
											<input name="duration" type="text" class="form-control timepicker" value="<c:out value="${model.duration}" />" data-rule-activity_code_validate="true" readonly />
											<div class="input-group-addon" style="visibility: hidden;">
												<i class="fa fa-clock-o"></i>
											</div>
										</div>
									</div>
								</div>
								<div class="form-group">
									<label for="" class="col-sm-2 control-label">Remarks</label>
									<div class="col-sm-6">
										<textarea name="remarks" class="form-control" readonly >${model.remark}</textarea>
									</div>
								</div>
							</div>
						<!-- /.box-body -->
						<!-- /.box-footer -->
						</form>
					</div>
				</div>
			</div>
		</section>		
	</jsp:body>
</t:layout>
