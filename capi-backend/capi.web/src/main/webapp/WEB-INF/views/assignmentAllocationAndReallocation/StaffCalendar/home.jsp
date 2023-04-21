<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page session="false"%>

<fmt:formatDate value="${displayModel.fromDate}" var="currMonthStr" type="date" pattern="MM" />
<fmt:formatDate value="${displayModel.fromDate}" var="currMonthYearStr" type="date" pattern="YYYY" />
<fmt:formatDate value="${displayModel.nextMonth}" var="nextMonthStr" type="date" pattern="MM" />
<fmt:formatDate value="${displayModel.nextMonth}" var="nextMonthYearStr" type="date" pattern="YYYY" />
<fmt:formatDate value="${displayModel.previousMonth}" var="prevMonthStr" type="date" pattern="MM" />
<fmt:formatDate value="${displayModel.previousMonth}" var="prevMonthYearStr" type="date" pattern="YYYY" />
<fmt:formatDate value="${displayModel.fromDate}" var="monthStr" type="date" pattern="MMMM YYYY" />
					
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/select2-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-fixedHeader-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
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
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp"%>
		<%@include file="/WEB-INF/views/includes/select2.jsp"%>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-fixedHeader.jsp"%>
		<%@include file="/WEB-INF/views/includes/datepicker.jsp"%>
<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
<%@include file="/WEB-INF/views/includes/commonDialog/staffCalendarUserLookup.jsp" %>
		<script>
			$(function() {
				$(".select2").select2({closeOnSelect: false, width: "100%"});
				$(".select2Single").select2({ width: "100%"});
				
				$("input[name='showSelf']").on('change', function(){
					if($(this).is(":checked")){
						window.location = "<c:url value='/assignmentAllocationAndReallocation/StaffCalendar/showSelf'/>?showSelf=1";
					}else{
						window.location = "<c:url value='/assignmentAllocationAndReallocation/StaffCalendar/showSelf'/>?showSelf=0";
					}
				});
				
				$('.officer', '.batchForm').staffCalendarUserLookup({
					selectedIdsCallback: function(selectedIds) {
						$.post("<c:url value='/assignmentAllocationAndReallocation/StaffCalendar/getStaffsName'/>", {ids: selectedIds}, function(result){
						//	$('[name="officers"]', '.batchForm').val(selectedIds.join());
							//console.log(result);
							var options = [];
							$('[name="userId"]', '.batchForm').val(options);
							$(result).each(function(){
								//console.log(this.userId);
								options.push(this.userId);
							});
							$('[name="userId"]', '.batchForm').val(options);
							$('[name="userId"]', '.batchForm').trigger("change");
						});
						
					},
					queryDataCallback: function(model) {
						model.authorityLevel = 16;
					},
					multiple: true,
					alreadySelectedIdsCallback: function() {
						return $('[name="officers"]', '.batchForm' ).val();
					}
				});
				

				Datepicker() // <--- here
				
				$("button[type='reset']", '.filterForm').on("click", function(){
					$('[name="officers"]', '.filterForm').val('');
				});
				
				$("button[type='reset']", '.batchForm').on("click", function(){
					$('[name="officers"]', '.batchForm').val('');
				});

				$('.filter-officer', '.filterForm').staffCalendarUserLookup({
					selectedIdsCallback: function(selectedIds) {
						$.post("<c:url value='/assignmentAllocationAndReallocation/StaffCalendar/getStaffsName'/>", {ids: selectedIds}, function(result){
							//$('[name="officers"]', '.filterForm').val(selectedIds.join());
							//console.log(result);
							var options = [];
							$('[name="userId"]', '.filterForm').val(options);
							$(result).each(function(){
								//console.log(this.userId);
								options.push(this.userId);
							});
							$('[name="userId"]', '.filterForm').val(options);
							$('[name="userId"]', '.filterForm').trigger("change");
						});
						
					},
					queryDataCallback: function(model) {
						model.authorityLevel = 16;
					},
					multiple: true,
					alreadySelectedIdsCallback: function() {
						return $('[name="officers"]', '.filterForm' ).val();
					}
				});
				
				$("[name=userId]", '.filterForm').on("change", function(){
					$('[name="officers"]', '.filterForm').val($(this).val());
				})
				
				$("[name=userId]", '.batchForm').on("change", function(){
					$('[name="officers"]', '.batchForm').val($(this).val());
				})
				
				$.post("<c:url value='/assignmentAllocationAndReallocation/StaffCalendar/getStaffsName'/>", {ids: [ ${displayModel.filterStaffIds} ]}, function(result){
					//$('[name="officers"]', '.filterForm').val(${displayModel.filterStaffIds});
					//$('[name="userId"]', '.filterForm').html("");
					var options = [];
					$('[name="userId"]', '.filterForm').val(options);
					$(result).each(function(){
						//console.log(this.userId);
						options.push(this.userId);
					});
					$('[name="userId"]', '.filterForm').val(options);
					$('[name="userId"]', '.filterForm').trigger("change");
				});
				
				Datepicker();
				
				$('[name="userId"]', '.filterForm').on("change", function(){
					//console.log($('[name="userId"]', '.filterForm').val())
					if($(this).val() != null){
						$('[name="officers"]', '.filterForm').val( $(this).val().join() );
					}
				});
				$('[name="userId"]', '.batchForm').on("change", function(){
					if($(this).val() != null){
						$('[name="officers"]', '.batchForm').val($(this).val().join());
					}
				});
			});
			
			var showEdit = ${displayModel.showEdit};
			var hideHoliday = ${displayModel.hideHoliday};
			
			function selectEvent($button){
				var officer = $button.data("officer");
				var date = $button.data("date");
				var session = $button.data("session");
				var activityType = $("select[name='activityType']").val();
				var showSelf = $("input[name='displayModel.showSelf']:checked").length;
				var month = ${currMonthStr};
				var year = ${currMonthYearStr};
				var code = $("select[name='activityType'] option:selected").html();
				/*
				normalFormPost('<c:url value='/assignmentAllocationAndReallocation/StaffCalendar/selectEvent'/>',
						{officer: officer, date: date, activityType: activityType, session:session, showSelf: showSelf, month: month, year:year,} );
				*/
				
				$.post("<c:url value='/assignmentAllocationAndReallocation/StaffCalendar/selectEvent'/>",
							{officer: officer, date: date, activityType: activityType, session:session, showSelf: showSelf, month: month, year:year },
							function(result){
								if(result != "-1"){
									if($("a.btn[data-eventid="+result+"]").length == 0){
										if(code.length > 5){
											//code = code.substr(0,5)+"...";
											code = code.substr(0,5);
										} 
										/*var html = '<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="'+result+'" href="javascript:void(0)" onclick="editEvent($(this), false)" title="'+code+'">'+
													code+
													'</a>';*/
										var html =	'<div class="buttonGroup">'+
													'<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="'+result+'" href="javascript:void(0)" onclick="editEvent($(this), false)" title="'+code+'">'+
													code+
													'</a>'+
													'<button type="button" class="btn btn-xSmall btn-danger btn-delete-cuz" data-eventid="'+result+'" onclick="deleteEvent($(this))">X</button>'+
													'</div>';
													
										$("td.dateCol[data-date='"+date+"'][data-officer="+officer+"][data-session="+session+"]").append(html);
									}else{
										bootbox.alert({
		        						    title: "Alert",
		        						    message: "<spring:message code='E00118' />"
		        						});
									}
								}else{
									bootbox.alert({
	        						    title: "Alert",
	        						    message: "<spring:message code='E00012' />"
	        						});
								};
							});
			}
			/*function deleteEvent($button){
				if (!confirm('<spring:message code="W00001" />')) return;
				var eventId = $button.data("eventid");
				var showSelf = $("input[name='displayModel.showSelf']:checked").length;
				var month = ${currMonthStr};
				var year = ${currMonthYearStr};
				//normalFormPost('<c:url value='/assignmentAllocationAndReallocation/StaffCalendar/deleteEvent'/>', {eventId: eventId, showSelf: showSelf, month: month, year:year,} );
				$.post("<c:url value='/assignmentAllocationAndReallocation/StaffCalendar/deleteEvent'/>",
						{eventId: eventId, showSelf: showSelf, month: month, year:year,},
						function(result){
							if(result == true){
								$("a.btn[data-eventid="+eventId+"]").parent(".buttonGroup").remove();
							}else{
								bootbox.alert({
        						    title: "Alert",
        						    message: "<spring:message code='E00009' />"
        						});
							}
						});
				
			}*/
			function deleteEvent($button){
				var eventId = $button.data("eventid");
				bootbox.confirm({
				     title:"Confirmation",
				     message: "<spring:message code='W00001' />",
				     callback: function(result){
				         if (result){
				        	 //window.location.href="<c:url value='/assignmentAllocationAndReallocation/StaffCalendar/deleteEvent'/>?eventId="+eventId;
				        	 $.post("<c:url value='/assignmentAllocationAndReallocation/StaffCalendar/deleteEventOnHome'/>",
				        				{eventId: eventId},
				        				function(result){
				        					/*if(result == true){
				        						//$("a.btn[data-eventid="+eventId+"]").parent(".buttonGroup").remove();
				        						window.location = "<c:url value='/assignmentAllocationAndReallocation/StaffCalendar/home'/>";
				        					}else{
				        						window.location = "<c:url value='/assignmentAllocationAndReallocation/StaffCalendar/home'/>";
				        						
				        					}*/
				        					if(result == true){
												$("a.btn[data-eventid="+eventId+"]").parent(".buttonGroup").remove();
											}else{
												bootbox.alert({
				        						    title: "Alert",
				        						    message: "<spring:message code='E00009' />"
				        						});
											}
				        				});
				        	
				         };
				     }
				})
			}
			function editEvent($button, viewOnly){
				var eventId = $button.data("eventid");
				//window.location = '<c:url value="/assignmentAllocationAndReallocation/StaffCalendar/edit/?id="/>'+eventId;
				if(!viewOnly) {
					window.location = '<c:url value="/assignmentAllocationAndReallocation/StaffCalendar/edit/?id="/>'+eventId;
				} else {
					window.location = '<c:url value="/assignmentAllocationAndReallocation/StaffCalendar/editViewOnly/?id="/>'+eventId;
				}
			}
			
			function hideHolidayFunc($button){
				if(hideHoliday == 0){
					//$("table").addClass("hideHoliday");
					
					$("th.dateCol.holiday div.weekdayStr").css("display", "none");
					$("th.dateCol.holiday ").css("padding", "0px");
					$("td.dateCol.holiday button.btn-add").css("display", "none");
					$("th.dateCol.holiday, td.dateCol.holiday").css("min-width", "0px");
					$("th.dateCol.holiday, td.dateCol.holiday ").css("width", "1%");
					
					$button.html("Show Holiday");
					hideHoliday = 1;
				}else{
					//$("table").removeClass("hideHoliday");
					
					$("th.dateCol.holiday div.weekdayStr").css("display", "");
					$("th.dateCol.holiday ").css("padding", "");
					$("td.dateCol.holiday button.btn-add").css("display", "");
					$("th.dateCol.holiday, td.dateCol.holiday ").css("min-width", "");
					$("th.dateCol.holiday, td.dateCol.holiday ").css("width", "");
					
					$button.html("Hide Holiday");
					hideHoliday = 0;
				}
			}
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Staff Calendar</h1>
        </section>
        
        <section class="content">
        	<div class="row">
        		<div class="col-md-12">
        			<!-- general form elements -->
        			<div class="box">
        				<%--
        				<div class="box-header with-border">
        					<div class="box-title"></div>
        				</div><!-- /.box-header -->
        				--%>
        				<div class="clearfix">&nbsp;</div>
        					<div class="box-body">
				        		
				        		<div class="row custom-grid">
					        		<form class="form-horizontal filter filterForm" method="post" role="form" action="<c:url value='/assignmentAllocationAndReallocation/StaffCalendar/filter'/>">
					        			<div class="col-md-8">
						        			<h4 class="col-md-12">Calendar Filter</h4>
					        				<label class="col-md-2 control-label">Officer</label>
		       								<div class="col-md-10">
			       								<div class="input-group">		
													<input type="hidden" class="form-control" name="officers" value="<c:out value="${model.user.userId}" />"  />
													<select class="form-control select2 filter-officer" name="userId" multiple  style="display:none">
														<c:forEach items="${userFilterList}" var="userFilter">
															<option value="<c:out value="${userFilter.userId}" />">${userFilter.staffCode} - ${userFilter.chineseName} (${userFilter.destination}) </option>
														</c:forEach>
													</select>
													<div class="input-group-addon filter-officer">
														<i class="fa fa-search"></i>
													</div>
												</div>
											</div>
						        			<div class="clearfix">&nbsp;</div>
											<div class="col-md-8">
						        				<button type="submit" class="btn btn-info">Apply</button>
					        					<button type="reset" class="btn btn-info">Clear</button>
						        			</div>
						        			<sec:authorize access="hasPermission(#user, 16)">
							        			<div class="clearfix">&nbsp;</div>
							        			<div class="clearfix">&nbsp;</div>
							        			<label class="col-md-2 control-label">Show Self Only</label>
							        			<div class="col-md-10">
						        					<label>
						        						<input type="checkbox" name="showSelf" value="1" <c:if test="${displayModel.showSelf != null && displayModel.showSelf == 1}">checked="checked"</c:if>>
						        					</label>
						        				</div>
					        				</sec:authorize>
										</div>
										<div class="col-md-4">
					        				<sec:authorize access="hasPermission(#user, 256) or hasPermission(#user, 4) or hasPermission(#user, 8)">
					        				<div class="col-md-12 text-right">No. of Working days: ${noOfWorkingDayInMonth}</div>
					        				</sec:authorize>
					        			</div>
					        		</form>
					        	</div>
				        		<hr />
				        		<sec:authorize access="hasPermission(#user, 256) or hasPermission(#user, 4) or hasPermission(#user, 8)">
					        	<c:if test="${displayModel.showEdit == 1}">			 
				        		<div class="row custom-grid">
				        		<form class="form-horizontal filter batchForm" method="post" role="form" action="<c:url value='/assignmentAllocationAndReallocation/StaffCalendar/batchUpdate'/>">
				        			<div class="col-md-8">       		
							        	<h4 class="col-md-12">Batch Add Events</h4>
						        		<div class="clearfix">&nbsp;</div>
						        			<div class="row">
						        			<div class="col-md-6">
						        				<label class="col-md-4 control-label">Date</label>
			       								<div class="col-md-8">
													<div class="input-group date date-picker" data-orientation="top" data-multidate="true"
														 data-target="[name=dateDisplay]" data-input="[name='date']" >
														<input type="text" class="form-control" style="display: none;" name="date" required />
				       									<select class="form-control select2" disabled name="dateDisplay" multiple style="display:none"></select>
														<div class="input-group-addon">
															<i class="fa fa-calendar"></i>
														</div>
													</div>
												</div>
						        			</div>
						        			<div class="col-md-6">
						        				<label class="col-md-4 control-label">Session</label>
			       								<div class="col-md-8">
													<select class="form-control select2" name="sessions" required multiple>
														<option value="A">A</option>
														<option value="P">P</option>
														<option value="E">E</option>
													</select>
												</div>
						        			</div>
						        			</div>
						        			
						        			<div class="clearfix">&nbsp;</div>
						        			<div class="row">
						        			<div class="col-md-6">
						        				<label class="col-md-4 control-label">Activity Type</label>
			       								<div class="col-md-8">
													<select class="form-control select2Single" name="activityType"
														required>
														<c:forEach items="${displayModel.activityList}" var="activity">
															<option value="<c:out value="${activity.activityCodeId}" />">${activity.code} - ${activity.description} </option>
														</c:forEach>
													</select>
												</div>
						        			</div>
						        			<div class="col-md-6">
						        				<label class="col-md-4 control-label">Officer</label>
			       								<div class="col-md-8">
				       								<div class="input-group">		
														<input type="hidden" class="form-control" name="officers" value="<c:out value="${model.user.userId}" />" required />
														<select class="form-control select2 officer" name="userId" multiple required style="display:none">
															<c:forEach items="${userFilterList}" var="userFilter">
																<option value="<c:out value="${userFilter.userId}" />">${userFilter.staffCode} - ${userFilter.chineseName} (${userFilter.destination}) </option>
															</c:forEach>
														</select>
														<div class="input-group-addon officer">
															<i class="fa fa-search"></i>
														</div>
													</div>
												</div>
						        			</div>
						        			</div>
						        			
						        			<div class="clearfix">&nbsp;</div>
						        			<div class="col-md-8">
						        				<button type="submit" class="btn btn-info">Apply</button>
					        					<button type="reset" class="btn btn-info">Clear</button>
						        			</div>
						        			<!-- 
						        			<div class="clearfix">&nbsp;</div>
						        			<div class="row">
						        			<div class="col-md-10">
						        				<label class="col-md-2 control-label">Officer</label>
			       								<div class="col-md-10">
													<select class="form-control select2" name="officers" required multiple>
														<c:forEach items="${displayModel.userList}" var="staff">
															<option value="<c:out value="${staff.userId}" />">${staff.staffCode} - ${staff.englishName} </option>
														</c:forEach>
													</select>
												</div>
						        			</div>
						        			</div>
						        			 -->
				        		</div>
				        		</form>
				        		</div>
				        		<hr />
			        			</c:if>
			        			</sec:authorize>
				        		<div class="row">
				        			<div class="col-md-4">
				        				
				        				<c:if test="${displayModel.showEdit == 1}">
				        				<div class="clearfix">&nbsp;</div>
				        				<sec:authorize access="hasPermission(#user, 256) or hasPermission(#user, 4) or hasPermission(#user, 8)">
				        					<a class="btn btn-info" href="<c:url value='/assignmentAllocationAndReallocation/StaffCalendar/edit'/>">Add Event</a>				        				
					        				<!-- <a class="btn btn-info" href="<c:url value='/assignmentAllocationAndReallocation/StaffCalendar/syncPublicHoliday?showEdit=${displayModel.showEdit}&showSelf=${displayModel.showSelf}'/>">Sync Public Holiday</a> -->
					        				<a class="btn btn-info" href="<c:url value='/assignmentAllocationAndReallocation/StaffCalendar/showEdit'/>?showEdit=0">Hide Edit</a>
				        				</sec:authorize>
				        				<a class="btn btn-info" href="javascript:void(0)" onclick="hideHolidayFunc($(this))">Show Holiday</a>
				        				</c:if>
				        				<c:if test="${displayModel.showEdit == 0}">
				        				<div class="clearfix">&nbsp;</div>
				        				<sec:authorize access="hasPermission(#user, 256) or hasPermission(#user, 4) or hasPermission(#user, 8)">
				        					<a class="btn btn-info" href="<c:url value='/assignmentAllocationAndReallocation/StaffCalendar/showEdit'/>?showEdit=1">Show Edit</a>
				        				</sec:authorize>
				        				<a class="btn btn-info" href="javascript:void(0)" onclick="hideHolidayFunc($(this))">Show Holiday</a>
				        				</c:if>
				        				
				        			</div>
				        			<div class="col-md-4 text-center">
				        				<H2>${monthStr}</H2>
				        			</div>
				        			<div class="col-md-4">
				        				<div class="clearfix">&nbsp;</div>
				        				<a class="btn btn-info pull-right" href="<c:url value='/assignmentAllocationAndReallocation/StaffCalendar/nevigateCalendar'/>?year=${nextMonthYearStr}&month=${nextMonthStr}">></a>
				        				<span class="pull-right">&nbsp;</span>
				        				<a class="btn btn-info pull-right" href="<c:url value='/assignmentAllocationAndReallocation/StaffCalendar/nevigateCalendarCurrent'/>">Current</a>
				        				<span class="pull-right">&nbsp;</span>
				        				<a class="btn btn-info pull-right" href="<c:url value='/assignmentAllocationAndReallocation/StaffCalendar/nevigateCalendar'/>?year=${prevMonthYearStr}&month=${prevMonthStr}"><</a>
				        			</div>
				        		</div>
				        		<div class="row">
				        			<div class="col-md-12">
				        				<table class="table table-bordered overflow-x" id="staffCalendarTable">
				        					<thead>
				        					<tr>
				        						<th colspan="2" class="staffHeadCol">
				        							Staff Code
				        						</th>
				        						<c:forEach var="i" items="${displayModel.dateList}">
				        							<fmt:formatDate value="${i}" var="dateStr" type="date" pattern="dd" />
				        							<fmt:formatDate value="${i}" var="dayOfweek" type="date" pattern="E" />
				        							<c:set var="isHoliday" value="0"/>
				        							<c:set var="holidayName" value=""/>
				        							<c:if test="${fn:toLowerCase(dayOfweek) == 'sun' || fn:toLowerCase(dayOfweek) == 'sat'}">
				        								<c:set var="isHoliday" value="1"/>
				        								<c:set var="holidayName" value="${dayOfweek}"/>
				        							</c:if>
				        							<c:if test="${fn:toLowerCase(dayOfweek) != 'sun' && fn:toLowerCase(dayOfweek) != 'sat'}">
														<c:forEach var="j" items="${displayModel.calendarHolidayList}">
															<fmt:formatDate value="${j.eventDate}" var="holidayDateStr" type="date" pattern="dd" />
					        								<c:if test="${dateStr == holidayDateStr}">
					        									<c:set var="isHoliday" value="1"/>
					        									<c:set var="holidayName" value="${j.remark}"/>
					        								</c:if>
					        							</c:forEach>
				        							</c:if>
				        							<c:if test="${isHoliday == 0}">
					        							<th class="dateCol">${dateStr}<br/><div class="weekdayStr">${dayOfweek}</div></th>
				        							</c:if>
				        							<c:if test="${isHoliday == 1}">
					        							<th class="dateCol holiday" title="${holidayName}" style="padding: 0px; min-width: 0px; width: 1%;">${dateStr}<br/>
					        								<div class="weekdayStr" style="display: none;">${dayOfweek}</div>
					        							</th>
				        							</c:if>
												</c:forEach>
				        					</tr>
				        					</thead>
				        					<tbody>
				        						<%--<c:forEach items="${displayModel.userList}" var="staff">--%>
				        						<c:forEach items="${displayModel.userListStaffCalendar}" var="staff">
					        						<tr>
					        							<th
					        								<sec:authorize access="hasPermission(#user, 256) or hasPermission(#user, 4) or hasPermission(#user, 8)">
					        								<c:if test="${displayModel.showEdit == 1}">
					        								rowspan="6"
					        								</c:if>
					        								<c:if test="${displayModel.showEdit == 0}">
					        								rowspan="3"
					        								</c:if>
					        								</sec:authorize>
					        								<sec:authorize access="!hasPermission(#user, 256) and !hasPermission(#user, 4) and !hasPermission(#user, 8)">
					        								rowspan="3"
					        								</sec:authorize>
					        								class="staffCodeCol">
							        						<p class="text-center">${staff.staffCode}<br/>${staff.chineseName}<br/>${staff.district}</p>
						        						</th>
						        						<th class="sessionCol"
						        							<sec:authorize access="hasPermission(#user, 256) or hasPermission(#user, 4) or hasPermission(#user, 8)">
						        								<c:if test="${displayModel.showEdit == 1}">
						        								rowspan="2"
						        								</c:if>
					        								</sec:authorize>
						        						>
							        						<div class="text-center">A</div>
						        						</th>
						        						<c:forEach var="i" items="${displayModel.dateList}">
						        							<fmt:formatDate value="${i}" var="dayOfweek" type="date" pattern="E" />
				        									<fmt:formatDate value="${i}" var="dateStr" type="date" pattern="dd" />
															<fmt:formatDate value="${i}" var="dayStr" type="date" pattern="dd-MM-yyyy" />
															
															<c:set var="isHoliday" value="0"/>
						        							<c:set var="holidayName" value=""/>
						        							<c:if test="${fn:toLowerCase(dayOfweek) == 'sun' || fn:toLowerCase(dayOfweek) == 'sat'}">
						        								<c:set var="isHoliday" value="1"/>
						        								<c:set var="holidayName" value="${dayOfweek}"/>
						        							</c:if>
						        							<c:if test="${fn:toLowerCase(dayOfweek) != 'sun' && fn:toLowerCase(dayOfweek) != 'sat'}">
																<c:forEach var="j" items="${displayModel.calendarHolidayList}">
																	<fmt:formatDate value="${j.eventDate}" var="holidayDateStr" type="date" pattern="dd" />
							        								<c:if test="${dateStr == holidayDateStr}">
							        									<c:set var="isHoliday" value="1"/>
							        									<c:set var="holidayName" value="${j.remark}"/>
							        								</c:if>
							        							</c:forEach>
						        							</c:if>
						        							<c:if test="${isHoliday == 0}">
							        							<td class="dateCol" data-officer="${staff.id}" data-date="${dayStr}" data-session="A">
						        							</c:if>
						        							<c:if test="${isHoliday == 1}">
							        							<td class="dateCol holiday" style="padding: 0px; min-width: 0px; width: 1%;" title="${holidayName}" data-officer="${staff.id}" data-date="${dayStr}" data-session="A">
						        							</c:if>
																<c:forEach items="${displayModel.calendarEventList}" var="calendarEvent">
				        											<fmt:formatDate value="${calendarEvent.eventDate}" var="eventDateStr" type="date" pattern="dd" />
																	<c:if test="${eventDateStr == dateStr && calendarEvent.session == 'A' && calendarEvent.user.userId == staff.id }">
																		<c:if test="${calendarEvent.activityType == 1}">
																			<sec:authorize access="!hasPermission(#user, 256) and !hasPermission(#user, 4) and !hasPermission(#user, 8)">
																				<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)">OT</a>
																			</sec:authorize>
																			<sec:authorize access="hasPermission(#user, 256) or hasPermission(#user, 4) or hasPermission(#user, 8)">
																				<c:if test="${displayModel.showEdit == 1}">
																					<div class="buttonGroup">
																						<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" onclick="editEvent($(this), false)">OT</a>
																						<button type="button" class="btn btn-xSmall btn-danger btn-delete-cuz" data-eventid="${calendarEvent.calendarEventId}" onclick="deleteEvent($(this))">X</button>
																					</div>
																				</c:if>
																				<c:if test="${displayModel.showEdit == 0}">
																					<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" onclick="editEvent($(this), true)">OT</a>
																				</c:if>
																			</sec:authorize>
																		</c:if>
																		<c:if test="${calendarEvent.activityType == 2}">
																			<sec:authorize access="!hasPermission(#user, 256) and !hasPermission(#user, 4) and !hasPermission(#user, 8)">
																				<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" title="TimeOff">Ti...</a>
																			</sec:authorize>
																			<sec:authorize access="hasPermission(#user, 256) or hasPermission(#user, 4) or hasPermission(#user, 8)">
																				<c:if test="${displayModel.showEdit == 1}">
																					<div class="buttonGroup">
																						<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" onclick="editEvent($(this), false)" title="TimeOff">Ti...</a>
																						<button type="button" class="btn btn-xSmall btn-danger btn-delete-cuz" data-eventid="${calendarEvent.calendarEventId}" onclick="deleteEvent($(this))">X</button>
																					</div>
																				</c:if>
																				<c:if test="${displayModel.showEdit == 0}">
																					<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" title="TimeOff" onclick="editEvent($(this), true)">Ti...</a>
																				</c:if>
																			</sec:authorize>
																		</c:if>
																		<c:if test="${calendarEvent.activityType == 3}">
																			<sec:authorize access="!hasPermission(#user, 256) and !hasPermission(#user, 4) and !hasPermission(#user, 8)">
																				<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" title="${calendarEvent.activityCode.code}">
																					<c:if test="${fn:length(calendarEvent.activityCode.code) > 5}">${fn:substring(calendarEvent.activityCode.code, 0, 5)}...</c:if>
																					<c:if test="${fn:length(calendarEvent.activityCode.code) <= 5}">${calendarEvent.activityCode.code}</c:if>
																				</a>
																			</sec:authorize>
																			<sec:authorize access="hasPermission(#user, 256) or hasPermission(#user, 4) or hasPermission(#user, 8)">
																				<c:if test="${displayModel.showEdit == 1}">
																					<div class="buttonGroup">
																						<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" onclick="editEvent($(this), false)" title="${calendarEvent.activityCode.code}">
																							<c:if test="${fn:length(calendarEvent.activityCode.code) > 5}">${fn:substring(calendarEvent.activityCode.code, 0, 2)}...</c:if>
																							<c:if test="${fn:length(calendarEvent.activityCode.code) <= 5}">${calendarEvent.activityCode.code}</c:if>
																						</a>
																						<button type="button" class="btn btn-xSmall btn-danger btn-delete-cuz" data-eventid="${calendarEvent.calendarEventId}" onclick="deleteEvent($(this))">X</button>
																					</div>
																				</c:if>
																				<c:if test="${displayModel.showEdit == 0}">
																					<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" onclick="editEvent($(this), true)" title="${calendarEvent.activityCode.code}">
																					<c:if test="${fn:length(calendarEvent.activityCode.code) > 5}">${fn:substring(calendarEvent.activityCode.code, 0, 2)}...</c:if>
																					<c:if test="${fn:length(calendarEvent.activityCode.code) <= 5}">${calendarEvent.activityCode.code}</c:if>
																					</a>
																				</c:if>
																			</sec:authorize>
																		</c:if>
																	</c:if>
																</c:forEach>
																<c:forEach items="${displayModel.assignmentList}" var="assignment">
																	<fmt:formatDate value="${assignment.getEventDate()}" var="assignDateStr" type="date" pattern="dd" />
																	<c:if test="${assignDateStr == dateStr && assignment.getSession() == 'A' && assignment.getUserId() == staff.id }">
																			<!--<a type="button" class="btn btn-small ${displayModel.getAssignmentEventColor()}" href="javascript:void(0)">${assignment.getCode()}</a>-->
																			<a type="button" class="btn btn-small ${displayModel.getAssignmentEventColor()}" href="javascript:void(0)" title="${assignment.getCode()}">
																				<c:if test="${fn:length(assignment.getCode()) > 5}">${fn:substring(assignment.getCode(), 0, 2)}...</c:if>
																				<c:if test="${fn:length(assignment.getCode()) <= 5}">${assignment.getCode()}</c:if>
																			</a>
																	</c:if>
																</c:forEach>
															</td>
														</c:forEach>
					        						</tr>
					        						<sec:authorize access="hasPermission(#user, 256) or hasPermission(#user, 4) or hasPermission(#user, 8)">
					        						<c:if test="${displayModel.showEdit == 1}">
					        						<tr>
					        							<c:forEach var="i" items="${displayModel.dateList}">
						        							<fmt:formatDate value="${i}" var="dayOfweek" type="date" pattern="E" />
				        									<fmt:formatDate value="${i}" var="dateStr" type="date" pattern="dd" />
															<fmt:formatDate value="${i}" var="dayStr" type="date" pattern="dd-MM-yyyy" />
															<c:set var="isHoliday" value="0"/>
						        							<c:set var="holidayName" value=""/>
						        							<c:if test="${fn:toLowerCase(dayOfweek) == 'sun' || fn:toLowerCase(dayOfweek) == 'sat'}">
						        								<c:set var="isHoliday" value="1"/>
						        								<c:set var="holidayName" value="${dayOfweek}"/>
						        							</c:if>
						        							<c:if test="${fn:toLowerCase(dayOfweek) != 'sun' && fn:toLowerCase(dayOfweek) != 'sat'}">
																<c:forEach var="j" items="${displayModel.calendarHolidayList}">
																	<fmt:formatDate value="${j.eventDate}" var="holidayDateStr" type="date" pattern="dd" />
							        								<c:if test="${dateStr == holidayDateStr}">
							        									<c:set var="isHoliday" value="1"/>
							        									<c:set var="holidayName" value="${j.remark}"/>
							        								</c:if>
							        							</c:forEach>
						        							</c:if>
						        							<c:if test="${isHoliday == 0}">
							        							<td class="dateCol" >
							        								<button type="button" class="btn btn-small btn-grey btn-add" data-officer="${staff.id}" data-date="${dayStr}" data-session="A" onclick="selectEvent($(this));">Add</button>
						        							</c:if>
						        							<c:if test="${isHoliday == 1}">
							        							<td class="dateCol holiday" style="padding: 0px; min-width: 0px; width: 1%;" title="${holidayName}">
							        								<button type="button" class="btn btn-small btn-grey btn-add" style="display:none;" data-officer="${staff.id}" data-date="${dayStr}" data-session="A" onclick="selectEvent($(this));">Add</button>
						        							</c:if>
																<!--<button type="button" class="btn btn-small btn-grey btn-add" data-officer="${staff.id}" data-date="${dayStr}" data-session="A" onclick="selectEvent($(this));">Add</button>-->
															</td>
														</c:forEach>
					        						</tr>
					        						</c:if>
													</sec:authorize>
					        						<tr>
						        						<th class="sessionCol"
															<sec:authorize access="hasPermission(#user, 256) or hasPermission(#user, 4) or hasPermission(#user, 8)">
						        								<c:if test="${displayModel.showEdit == 1}">
						        								rowspan="2"
						        								</c:if>
					        								</sec:authorize>
					        								>
							        						<div class="text-center">P</div>
						        						</th>
					        							<c:forEach var="i" items="${displayModel.dateList}">
						        							<fmt:formatDate value="${i}" var="dayOfweek" type="date" pattern="E" />
				        									<fmt:formatDate value="${i}" var="dateStr" type="date" pattern="dd" />
															<fmt:formatDate value="${i}" var="dayStr" type="date" pattern="dd-MM-yyyy" />
															
															<c:set var="isHoliday" value="0"/>
						        							<c:set var="holidayName" value=""/>
						        							<c:if test="${fn:toLowerCase(dayOfweek) == 'sun' || fn:toLowerCase(dayOfweek) == 'sat'}">
						        								<c:set var="isHoliday" value="1"/>
						        								<c:set var="holidayName" value="${dayOfweek}"/>
						        							</c:if>
						        							<c:if test="${fn:toLowerCase(dayOfweek) != 'sun' && fn:toLowerCase(dayOfweek) != 'sat'}">
																<c:forEach var="j" items="${displayModel.calendarHolidayList}">
																	<fmt:formatDate value="${j.eventDate}" var="holidayDateStr" type="date" pattern="dd" />
							        								<c:if test="${dateStr == holidayDateStr}">
							        									<c:set var="isHoliday" value="1"/>
							        									<c:set var="holidayName" value="${j.remark}"/>
							        								</c:if>
							        							</c:forEach>
						        							</c:if>
						        							<c:if test="${isHoliday == 0}">
							        							<td class="dateCol" data-officer="${staff.id}" data-date="${dayStr}" data-session="P">
						        							</c:if>
						        							<c:if test="${isHoliday == 1}">
							        							<td class="dateCol holiday" style="padding: 0px; min-width: 0px; width: 1%;" title="${holidayName}" data-officer="${staff.id}" data-date="${dayStr}" data-session="P">
						        							</c:if>
																<c:forEach items="${displayModel.calendarEventList}" var="calendarEvent">
				        											<fmt:formatDate value="${calendarEvent.eventDate}" var="eventDateStr" type="date" pattern="dd" />
																	<c:if test="${eventDateStr == dateStr && calendarEvent.session == 'P' && calendarEvent.user.userId == staff.id }">
																		<c:if test="${calendarEvent.activityType == 1}">
																			<sec:authorize access="!hasPermission(#user, 256) and !hasPermission(#user, 4) and !hasPermission(#user, 8)">
																				<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)">OT</a>
																			</sec:authorize>
																			<sec:authorize access="hasPermission(#user, 256) or hasPermission(#user, 4) or hasPermission(#user, 8)">
																				<c:if test="${displayModel.showEdit == 1}">
																					<div class="buttonGroup">
																						<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" onclick="editEvent($(this), false)">OT</a>
																						<button type="button" class="btn btn-xSmall btn-danger btn-delete-cuz" data-eventid="${calendarEvent.calendarEventId}" onclick="deleteEvent($(this))">X</button>
																					</div>
																				</c:if>
																				<c:if test="${displayModel.showEdit == 0}">
																					<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" onclick="editEvent($(this), true)">OT</a>
																				</c:if>
																			</sec:authorize>
																		</c:if>
																		<c:if test="${calendarEvent.activityType == 2}">
																			<sec:authorize access="!hasPermission(#user, 256) and !hasPermission(#user, 4) and !hasPermission(#user, 8)">
																				<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" title="TimeOff">Ti...</a>
																			</sec:authorize>
																			<sec:authorize access="hasPermission(#user, 256) or hasPermission(#user, 4) or hasPermission(#user, 8)">
																				<c:if test="${displayModel.showEdit == 1}">
																					<div class="buttonGroup">
																						<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" onclick="editEvent($(this), false)" title="TimeOff">Ti...</a>
																						<button type="button" class="btn btn-xSmall btn-danger btn-delete-cuz" data-eventid="${calendarEvent.calendarEventId}" onclick="deleteEvent($(this))">X</button>
																					</div>
																				</c:if>
																				<c:if test="${displayModel.showEdit == 0}">
																					<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" title="TimeOff" onclick="editEvent($(this), true)">Ti...</a>
																				</c:if>
																			</sec:authorize>
																		</c:if>
																		<c:if test="${calendarEvent.activityType == 3}">
																			<sec:authorize access="!hasPermission(#user, 256) and !hasPermission(#user, 4) and !hasPermission(#user, 8)">
																				<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" title="${calendarEvent.activityCode.code}">
																					<c:if test="${fn:length(calendarEvent.activityCode.code) > 5}">${fn:substring(calendarEvent.activityCode.code, 0, 2)}...</c:if>
																					<c:if test="${fn:length(calendarEvent.activityCode.code) <= 5}">${calendarEvent.activityCode.code}</c:if>
																				</a>
																			</sec:authorize>
																			<sec:authorize access="hasPermission(#user, 256) or hasPermission(#user, 4) or hasPermission(#user, 8)">
																				<c:if test="${displayModel.showEdit == 1}">
																					<div class="buttonGroup">
																						<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" onclick="editEvent($(this), false)" title="${calendarEvent.activityCode.code}">
																							<c:if test="${fn:length(calendarEvent.activityCode.code) > 5}">${fn:substring(calendarEvent.activityCode.code, 0, 2)}...</c:if>
																							<c:if test="${fn:length(calendarEvent.activityCode.code) <= 5}">${calendarEvent.activityCode.code}</c:if>
																						</a>
																						<button type="button" class="btn btn-xSmall btn-danger btn-delete-cuz" data-eventid="${calendarEvent.calendarEventId}" onclick="deleteEvent($(this))">X</button>
																					</div>
																				</c:if>
																				<c:if test="${displayModel.showEdit == 0}">
																					<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" onclick="editEvent($(this), true)" title="${calendarEvent.activityCode.code}">
																					<c:if test="${fn:length(calendarEvent.activityCode.code) > 5}">${fn:substring(calendarEvent.activityCode.code, 0, 2)}...</c:if>
																					<c:if test="${fn:length(calendarEvent.activityCode.code) <= 5}">${calendarEvent.activityCode.code}</c:if>
																					</a>
																				</c:if>
																			</sec:authorize>
																		</c:if>
																	</c:if>
																</c:forEach>
																<c:forEach items="${displayModel.assignmentList}" var="assignment">
																	<fmt:formatDate value="${assignment.getEventDate()}" var="assignDateStr" type="date" pattern="dd" />
																	<c:if test="${assignDateStr == dateStr && assignment.getSession() == 'P' && assignment.getUserId() == staff.id }">
																			<!--<a type="button" class="btn btn-small ${displayModel.getAssignmentEventColor()}" href="javascript:void(0)">${assignment.getCode()}</a>-->
																			<a type="button" class="btn btn-small ${displayModel.getAssignmentEventColor()}" href="javascript:void(0)" title="${assignment.getCode()}">
																				<c:if test="${fn:length(assignment.getCode()) > 5}">${fn:substring(assignment.getCode(), 0, 2)}...</c:if>
																				<c:if test="${fn:length(assignment.getCode()) <= 5}">${assignment.getCode()}</c:if>
																			</a>
																	</c:if>
																</c:forEach>
															</td>
														</c:forEach>
					        						</tr>
					        						<sec:authorize access="hasPermission(#user, 256) or hasPermission(#user, 4) or hasPermission(#user, 8)">
					        						<c:if test="${displayModel.showEdit == 1}">
					        						<tr>
					        							<c:forEach var="i" items="${displayModel.dateList}">
						        							<fmt:formatDate value="${i}" var="dayOfweek" type="date" pattern="E" />
				        									<fmt:formatDate value="${i}" var="dateStr" type="date" pattern="dd" />
															<fmt:formatDate value="${i}" var="dayStr" type="date" pattern="dd-MM-yyyy" />
															<c:set var="isHoliday" value="0"/>
						        							<c:set var="holidayName" value=""/>
						        							<c:if test="${fn:toLowerCase(dayOfweek) == 'sun' || fn:toLowerCase(dayOfweek) == 'sat'}">
						        								<c:set var="isHoliday" value="1"/>
						        								<c:set var="holidayName" value="${dayOfweek}"/>
						        							</c:if>
						        							<c:if test="${fn:toLowerCase(dayOfweek) != 'sun' && fn:toLowerCase(dayOfweek) != 'sat'}">
																<c:forEach var="j" items="${displayModel.calendarHolidayList}">
																	<fmt:formatDate value="${j.eventDate}" var="holidayDateStr" type="date" pattern="dd" />
							        								<c:if test="${dateStr == holidayDateStr}">
							        									<c:set var="isHoliday" value="1"/>
							        									<c:set var="holidayName" value="${j.remark}"/>
							        								</c:if>
							        							</c:forEach>
						        							</c:if>
						        							<c:if test="${isHoliday == 0}">
							        							<td class="dateCol" >
							        								<button type="button" class="btn btn-small btn-grey btn-add" data-officer="${staff.id}" data-date="${dayStr}" data-session="P" onclick="selectEvent($(this));">Add</button>
						        							</c:if>
						        							<c:if test="${isHoliday == 1}">
							        							<td class="dateCol holiday" style="padding: 0px; min-width: 0px; width: 1%;" title="${holidayName}">
							        								<button type="button" class="btn btn-small btn-grey btn-add" style="display:none;" data-officer="${staff.id}" data-date="${dayStr}" data-session="P" onclick="selectEvent($(this));">Add</button>
						        							</c:if>
																<!--<button type="button" class="btn btn-small btn-grey btn-add" data-officer="${staff.id}" data-date="${dayStr}" data-session="P" onclick="selectEvent($(this));">Add</button>-->
															</td>
														</c:forEach>
					        						</tr>
					        						</c:if>
													</sec:authorize>
					        						<tr>
						        						<th class="sessionCol"
															<sec:authorize access="hasPermission(#user, 256) or hasPermission(#user, 4) or hasPermission(#user, 8)">
						        								<c:if test="${displayModel.showEdit == 1}">
						        								rowspan="2"
						        								</c:if>
					        								</sec:authorize>
					        								>
							        						<div class="text-center">E</div>
						        						</th>
					        							<c:forEach var="i" items="${displayModel.dateList}">
						        							<fmt:formatDate value="${i}" var="dayOfweek" type="date" pattern="E" />
				        									<fmt:formatDate value="${i}" var="dateStr" type="date" pattern="dd" />
															<fmt:formatDate value="${i}" var="dayStr" type="date" pattern="dd-MM-yyyy" />
															
															<c:set var="isHoliday" value="0"/>
						        							<c:set var="holidayName" value=""/>
						        							<c:if test="${fn:toLowerCase(dayOfweek) == 'sun' || fn:toLowerCase(dayOfweek) == 'sat'}">
						        								<c:set var="isHoliday" value="1"/>
						        								<c:set var="holidayName" value="${dayOfweek}"/>
						        							</c:if>
						        							<c:if test="${fn:toLowerCase(dayOfweek) != 'sun' && fn:toLowerCase(dayOfweek) != 'sat'}">
																<c:forEach var="j" items="${displayModel.calendarHolidayList}">
																	<fmt:formatDate value="${j.eventDate}" var="holidayDateStr" type="date" pattern="dd" />
							        								<c:if test="${dateStr == holidayDateStr}">
							        									<c:set var="isHoliday" value="1"/>
							        									<c:set var="holidayName" value="${j.remark}"/>
							        								</c:if>
							        							</c:forEach>
						        							</c:if>
						        							<c:if test="${isHoliday == 0}">
							        							<td class="dateCol" data-officer="${staff.id}" data-date="${dayStr}" data-session="E">
						        							</c:if>
						        							<c:if test="${isHoliday == 1}">
							        							<td class="dateCol holiday" style="padding: 0px; min-width: 0px; width: 1%;" title="${holidayName}" data-officer="${staff.id}" data-date="${dayStr}" data-session="E">
						        							</c:if>
																<c:forEach items="${displayModel.calendarEventList}" var="calendarEvent">
				        											<fmt:formatDate value="${calendarEvent.eventDate}" var="eventDateStr" type="date" pattern="dd" />
																	<c:if test="${eventDateStr == dateStr && calendarEvent.session == 'E' && calendarEvent.user.userId == staff.id }">
																		<c:if test="${calendarEvent.activityType == 1}">
																			<sec:authorize access="!hasPermission(#user, 256) and !hasPermission(#user, 4) and !hasPermission(#user, 8)">
																				<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)">OT</a>
																			</sec:authorize>
																			<sec:authorize access="hasPermission(#user, 256) or hasPermission(#user, 4) or hasPermission(#user, 8)">
																				<c:if test="${displayModel.showEdit == 1}">
																					<div class="buttonGroup">
																						<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" onclick="editEvent($(this), false)">OT</a>
																						<button type="button" class="btn btn-xSmall btn-danger btn-delete-cuz" data-eventid="${calendarEvent.calendarEventId}" onclick="deleteEvent($(this))">X</button>
																					</div>
																				</c:if>
																				<c:if test="${displayModel.showEdit == 0}">
																					<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" onclick="editEvent($(this), true)">OT</a>
																				</c:if>
																			</sec:authorize>
																		</c:if>
																		<c:if test="${calendarEvent.activityType == 2}">
																			<sec:authorize access="!hasPermission(#user, 256) and !hasPermission(#user, 4) and !hasPermission(#user, 8)">
																				<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" title="TimeOff">Ti...</a>
																			</sec:authorize>
																			<sec:authorize access="hasPermission(#user, 256) or hasPermission(#user, 4) or hasPermission(#user, 8)">
																				<c:if test="${displayModel.showEdit == 1}">
																				<div class="buttonGroup">
																						<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" onclick="editEvent($(this), false)" title="TimeOff">Ti...</a>
																						<button type="button" class="btn btn-xSmall btn-danger btn-delete-cuz" data-eventid="${calendarEvent.calendarEventId}" onclick="deleteEvent($(this))">X</button>
																				</div>
																				</c:if>
																				<c:if test="${displayModel.showEdit == 0}">
																					<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" onclick="editEvent($(this), true)" title="TimeOff">Ti...</a>
																				</c:if>
																			</sec:authorize>
																		</c:if>
																		<c:if test="${calendarEvent.activityType == 3}">
																			<sec:authorize access="!hasPermission(#user, 256) and !hasPermission(#user, 4) and !hasPermission(#user, 8)">
																				<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" title="${calendarEvent.activityCode.code}">
																					<c:if test="${fn:length(calendarEvent.activityCode.code) > 5}">${fn:substring(calendarEvent.activityCode.code, 0, 2)}...</c:if>
																					<c:if test="${fn:length(calendarEvent.activityCode.code) <= 5}">${calendarEvent.activityCode.code}</c:if>
																				</a>
																			</sec:authorize>
																			<sec:authorize access="hasPermission(#user, 256) or hasPermission(#user, 4) or hasPermission(#user, 8)">
																				<c:if test="${displayModel.showEdit == 1}">
																					<div class="buttonGroup">
																						<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" onclick="editEvent($(this), false)" title="${calendarEvent.activityCode.code}">
																							<c:if test="${fn:length(calendarEvent.activityCode.code) > 5}">${fn:substring(calendarEvent.activityCode.code, 0, 2)}...</c:if>
																							<c:if test="${fn:length(calendarEvent.activityCode.code) <= 5}">${calendarEvent.activityCode.code}</c:if>
																						</a>
																						<button type="button" class="btn btn-xSmall btn-danger btn-delete-cuz" data-eventid="${calendarEvent.calendarEventId}" onclick="deleteEvent($(this))">X</button>
																					</div>
																				</c:if>
																				<c:if test="${displayModel.showEdit == 0}">
																					<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" onclick="editEvent($(this), true)" title="${calendarEvent.activityCode.code}">
																					<c:if test="${fn:length(calendarEvent.activityCode.code) > 5}">${fn:substring(calendarEvent.activityCode.code, 0, 2)}...</c:if>
																					<c:if test="${fn:length(calendarEvent.activityCode.code) <= 5}">${calendarEvent.activityCode.code}</c:if>
																					</a>
																				</c:if>
																			</sec:authorize>
																		</c:if>
																	</c:if>
																</c:forEach>
																<c:forEach items="${displayModel.assignmentList}" var="assignment">
																	<fmt:formatDate value="${assignment.getEventDate()}" var="assignDateStr" type="date" pattern="dd" />
																	<c:if test="${assignDateStr == dateStr && assignment.getSession() == 'E' && assignment.getUserId() == staff.id }">
																			<!--<a type="button" class="btn btn-small ${displayModel.getAssignmentEventColor()}" href="javascript:void(0)">${assignment.getCode()}</a>-->
																			<a type="button" class="btn btn-small ${displayModel.getAssignmentEventColor()}" href="javascript:void(0)" title="${assignment.getCode()}">
																				<c:if test="${fn:length(assignment.getCode()) > 5}">${fn:substring(assignment.getCode(), 0, 2)}...</c:if>
																				<c:if test="${fn:length(assignment.getCode()) <= 5}">${assignment.getCode()}</c:if>
																			</a>
																	</c:if>
																</c:forEach>
															</td>
														</c:forEach>
					        						</tr>
					        						<sec:authorize access="hasPermission(#user, 256) or hasPermission(#user, 4) or hasPermission(#user, 8)">
					        						<c:if test="${displayModel.showEdit == 1}">
					        						<tr>
					        							<c:forEach var="i" items="${displayModel.dateList}">
						        							<fmt:formatDate value="${i}" var="dayOfweek" type="date" pattern="E" />
				        									<fmt:formatDate value="${i}" var="dateStr" type="date" pattern="dd" />
															<fmt:formatDate value="${i}" var="dayStr" type="date" pattern="dd-MM-yyyy" />
															<c:set var="isHoliday" value="0"/>
						        							<c:set var="holidayName" value=""/>
						        							<c:if test="${fn:toLowerCase(dayOfweek) == 'sun' || fn:toLowerCase(dayOfweek) == 'sat'}">
						        								<c:set var="isHoliday" value="1"/>
						        								<c:set var="holidayName" value="${dayOfweek}"/>
						        							</c:if>
						        							<c:if test="${fn:toLowerCase(dayOfweek) != 'sun' && fn:toLowerCase(dayOfweek) != 'sat'}">
																<c:forEach var="j" items="${displayModel.calendarHolidayList}">
																	<fmt:formatDate value="${j.eventDate}" var="holidayDateStr" type="date" pattern="dd" />
							        								<c:if test="${dateStr == holidayDateStr}">
							        									<c:set var="isHoliday" value="1"/>
							        									<c:set var="holidayName" value="${j.remark}"/>
							        								</c:if>
							        							</c:forEach>
						        							</c:if>
						        							<c:if test="${isHoliday == 0}">
							        							<td class="dateCol" >
							        								<button type="button" class="btn btn-small btn-grey btn-add" data-officer="${staff.id}" data-date="${dayStr}" data-session="E" onclick="selectEvent($(this));">Add</button>
						        							</c:if>
						        							<c:if test="${isHoliday == 1}">
							        							<td class="dateCol holiday" style="padding: 0px; min-width: 0px; width: 1%;" title="${holidayName}">
							        								<button type="button" class="btn btn-small btn-grey btn-add" style="display:none;" data-officer="${staff.id}" data-date="${dayStr}" data-session="E" onclick="selectEvent($(this));">Add</button>
						        							</c:if>
																<!--<button type="button" class="btn btn-small btn-grey btn-add" data-officer="${staff.id}" data-date="${dayStr}" data-session="E" onclick="selectEvent($(this));">Add</button>-->
															</td>
														</c:forEach>
					        						</tr>
					        						</c:if>
					        						</sec:authorize>
					        					</c:forEach>
				        					</tbody>
				        				</table>
				        			</div>
				        		</div>
        					</div>
        			</div>
					<!-- /.box -->
        		</div>
        	</div>
        </section>		
	</jsp:body>
</t:layout>