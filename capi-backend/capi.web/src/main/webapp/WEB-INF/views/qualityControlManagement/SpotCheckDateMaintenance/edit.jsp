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
		
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/datejs.jsp" %>
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
		<%@include file="/WEB-INF/views/includes/datepicker.jsp" %>
		<script>
			$(function() {
				var $mainForm = $('#mainForm');
				
				$mainForm.validate();
				
				var dateList = ${dateList};
				
				$(".date-picker .select2").select2();
				Datepicker(".date-picker",{
					multidate: true,
					beforeShowDay: function(date) {
	            		for (var i = 0; i < dateList.length; i++) {
	            			var str_date = parseDate(dateList[i]);
							if (str_date!=null && str_date.equals(date)) {
								return false;
							}
						}
						return true;
					}
				});
				
				//$('#spotCheckDates').datepicker();
				
				<sec:authorize access="!(hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 256))">
					viewOnly();
				</sec:authorize>
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Spot Check Date Maintenance</h1>
          <%--
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
	       --%>
        </section>
        
        <section class="content">
        	<form id="mainForm" action="<c:url value='/qualityControlManagement/SpotCheckDateMaintenance/save'/>" method="post" role="form">
        		<input name="spotCheckDateId" value="<c:out value="${model.spotCheckDateId}" />" type="hidden" />
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/qualityControlManagement/SpotCheckDateMaintenance/home'/>">Back</a>
							</div>
	       					<div class="box-body">
	       						<div class="form-horizontal">
	       							<div class="col-sm-12">
		       							<div class="form-group">
		       								<label class="col-sm-3 control-label">Reference Month: </label>
		       								<div class="col-sm-9">
		       									<p class="form-control-static" >${model.referenceMonth}</p>
		       									<input name="surveyMonthId" value="<c:out value="${model.surveyMonthId}" />" type="hidden" />
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-3 control-label">Selected Spot Check Date: </label>
		       								<div class="col-sm-9">
		       									<p class="form-control-static" >${model.selectedSpotCheckDate}</p>
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-3 control-label">Spot Check Date: </label>
		       								<div class="col-sm-3">
		       										<div class="input-group date date-picker" data-orientation="top" data-multidate="true"
														 data-target="[name=dateDisplay]" data-input="#spotCheckDates" >
														<input type="text" class="form-control" style="display: none;" id="spotCheckDates" name="spotCheckDates" value="<c:out value="${model.spotCheckDates}" />" required />
				       									<select class="form-control select2" disabled name="dateDisplay" multiple style="display:none"></select>
														<div class="input-group-addon">
															<i class="fa fa-calendar"></i>
														</div>
													</div>
		       										
		       									<%--		       									
			       									<div class="input-group">
				       									<input id="spotCheckDates" name="spotCheckDates" type="text" class="datepicker form-control" style="min-width:200px" value="<c:out value="${model.spotCheckDates}" />" required />
														<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
													</div>
												 --%>
												
											</div>
										</div>
									</div>
								</div>
							</div>
	       					<sec:authorize access="hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 256)">
								<div class="box-footer">
	        						<button type="submit" class="btn btn-info">Submit</button>	       						
	       						</div>
	       					</sec:authorize>
						</div>
	        		</div>
	        	</div>
        	</form>
        </section>
	</jsp:body>
</t:layout>
