<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page session="false" %>
<jsp:useBean id="niceDate" class="java.util.Date"/>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/timepicker-css.jsp" %>
		
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/datejs.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
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
			#spotCheckPhoneCallList > tbody > tr > td {
				text-align: center;
			}
		</style>
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/timepicker.jsp" %>		
		<%@include file="/WEB-INF/views/includes/datepicker.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/userLookup.jsp" %>
		<script>
			var spotCheckPhoneCallListLength = ${fn:length(model.spotCheckPhoneCallList)};
			var spotCheckResultListLength = ${fn:length(model.spotCheckResultList)};
			
			function reject(){
	      		$('#rejectReasonForm .error').removeClass('error');
	      		$("#rejectReasonForm").modal('show');
	      	}
			
			$(function() {
				
				var $mainForm = $('#mainForm');
				
				$mainForm.validate();
				
				var $rejectReasonInputForm = $('#rejectReasonInputForm');
				
				$rejectReasonInputForm.validate();
				
				$(".select2-addable").select2({
					 	tags: true,
					 	multiple: false,
				});
				
				<sec:authorize access="!(hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 256))">
					viewOnly();
				</sec:authorize>
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Spot Check Approval</h1>
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
        </section>
        
        <section class="content">
        	<form id="mainForm" action="<c:url value='/qualityControlManagement/SpotCheckApproval/approve'/>" method="post" role="form">
        		<input name="spotCheckFormId" value="<c:out value="${model.spotCheckFormId}" />" type="hidden" />
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/qualityControlManagement/SpotCheckApproval/home'/>">Back</a>
							</div>
	       					<div class="box-body">
	       						<div class="form-horizontal">
	       							<div class="form-group">
										<label class="col-sm-2 control-label">Field Officer:</label>
										<label class="col-sm-2 control-label" style="text-align:left;">${model.officerCode}</label>
										<label class="col-sm-2 control-label pull-right" style="text-align:left;">${model.spotCheckDate}</label>
										<label class="col-sm-2 control-label pull-right">Spot Check Date:</label>
										<br style="clear:both" />
									</div>
									<div class="form-group">
	       								<label class="col-sm-2 control-label">Attendance Check:</label>
										<label class="col-sm-2 control-label pull-right" style="text-align:left;">${model.survey}</label>
										<label class="col-sm-2 control-label pull-right">Survey:</label>
										<label class="col-sm-2 control-label pull-right" style="text-align:left;">${model.session == 1 ? "Day" : "Night"}</label>
										<label class="col-sm-2 control-label pull-right">Session:</label>
									</div>
									<div class="col-sm-12">
		       							<div class="box" style="padding:1px;">
		       							
											<div class="box-body">
												<table class="table table-striped table-bordered table-hover" id="spotCheckPhoneCallList">
													<thead>
														<tr>
															<th>Time of phone call</th>
															<th>Contacted</th>
															<th>Voice mail</th>
															<th>Others</th>
														</tr>
													</thead>
													<tbody>
														<c:if test="${fn:length(model.spotCheckPhoneCallList) gt 0}" >
															<c:forEach var="spotCheckPhoneCall" items="${model.spotCheckPhoneCallList}" varStatus="status">
																<tr>
																	<td>
																		<div class="input-group col-sm-2 bootstrap-timepicker">
																		<input type="text" value="<c:out value="${spotCheckPhoneCall.phoneCallTime}" />" name="spotCheckPhoneCallList[${status.index}].phoneCallTime" disabled />
																		<div class="input-group-addon">
																			<i class="fa fa-clock-o"></i>
																		</div>
																		</div>
																	</td>
																	<td><input type="radio" name="spotCheckPhoneCallList[${status.index}].phoneCallResult" value="1" <c:if test="${spotCheckPhoneCall.phoneCallResult == '1'}">checked</c:if> disabled /></td>
																	<td><input type="radio" name="spotCheckPhoneCallList[${status.index}].phoneCallResult" value="2" <c:if test="${spotCheckPhoneCall.phoneCallResult == '2'}">checked</c:if> disabled /></td>
																	<td><input type="radio" name="spotCheckPhoneCallList[${status.index}].phoneCallResult" value="3" <c:if test="${spotCheckPhoneCall.phoneCallResult == '3'}">checked</c:if> disabled /></td>
																</tr>
															</c:forEach>
														</c:if>
													</tbody>
												</table>
											</div>
		       								<div class="form-group">
			       								<label class="col-sm-2 control-label">Time call Back:</label>
												<div class="col-sm-4">
													<label class="radio-inline">
														<input type="radio" name="timeCallback" value="1" disabled <c:if test="${model.timeCallback == '1'}">checked</c:if>> Reasonable
													</label>
													<label class="radio-inline">
														<input type="radio" name="timeCallback" value="2" disabled <c:if test="${model.timeCallback == '2'}">checked</c:if>> Not Reasonable
													</label>
													<label class="radio-inline">
														<input type="radio" name="timeCallback" value="3" disabled <c:if test="${model.timeCallback == '3'}">checked</c:if>> N.A.
													</label>
												</div>
											</div>
											<div class="form-group radio-group">
			       								<label class="col-sm-2 control-label">Activity being performed:</label>
												<div class="col-sm-6">
													<label class="radio-inline">
														<input type="radio" name="activityBeingPerformed" value="1" disabled <c:if test="${model.activityBeingPerformed == '1'}">checked</c:if>> On the way to outlet
													</label>
													<label class="radio-inline">
														<input type="radio" name="activityBeingPerformed" value="2" disabled <c:if test="${model.activityBeingPerformed == '2'}">checked</c:if>> Living quarters
													</label>
													<label class="radio-inline">
														<input type="radio" name="activityBeingPerformed" value="3" disabled <c:if test="${model.activityBeingPerformed == '3'}">checked</c:if>> Conducting interview
													</label>
													&nbsp;&nbsp;&nbsp;(Ref. No.
												</div>
												<div class="col-sm-2">
													<select class="form-control select2-addable col-sm-2" name="interviewReferenceNo" disabled >
														<option value=""></option>
														<c:forEach items="${model.referenceNoList}" var="referenceNo">
															<option value="<c:out value="${referenceNo}" />" <c:if test="${model.interviewReferenceNo != null && model.activityBeingPerformed == '3' && model.interviewReferenceNo == referenceNo}">selected</c:if>>${referenceNo}</option>
														</c:forEach>
													</select>
												</div>
												<div class="col-sm-2">
													)&nbsp;&nbsp;&nbsp;
													<label class="radio-inline">
														<input type="radio" name="activityBeingPerformed" value="4" disabled <c:if test="${model.activityBeingPerformed == '4'}">checked</c:if>> Others
													</label>
												</div>
											</div>
			       							<div class="form-group">
			       								<label class="col-sm-2 control-label">Location: </label>
			       								<div class="col-sm-5">
			       									<input name="location" type="text" class="form-control" value="<c:out value="${model.location}" />" maxlength="4000" disabled />
												</div>
											</div>
											<div class="form-group">
			       								<label class="col-sm-2 control-label">Result:</label>
												<div class="col-sm-8">
													<table class="table table-striped table-bordered table-hover" id="spotCheckResultList">
														<tbody>
															<c:if test="${fn:length(model.spotCheckResultList) gt 0}" >
																<c:forEach var="spotCheckResult" items="${model.spotCheckResultList}" varStatus="status">
																	<tr>
																		<td><label class="radio-inline"><input type="radio" name="spotCheckResultList[${status.index}].result" value="P" disabled <c:if test="${spotCheckResult.result == 'P'}">checked</c:if> /> P</label></td>
																		<td><label class="radio-inline"><input type="radio" name="spotCheckResultList[${status.index}].result" value="C" disabled <c:if test="${spotCheckResult.result == 'C'}">checked</c:if> /> C</label></td>
																		<td><label class="radio-inline"><input type="radio" name="spotCheckResultList[${status.index}].result" value="N" disabled <c:if test="${spotCheckResult.result == 'N'}">checked</c:if> /> N</label></td>
																		<td><label class="radio-inline"><input type="radio" name="spotCheckResultList[${status.index}].result" value="R" disabled <c:if test="${spotCheckResult.result == 'R'}">checked</c:if> /> R</label></td>
																		<td><label class="radio-inline"><input type="radio" name="spotCheckResultList[${status.index}].result" value="D" disabled <c:if test="${spotCheckResult.result == 'D'}">checked</c:if> /> D</label></td>
																		<td><label class="radio-inline"><input type="radio" name="spotCheckResultList[${status.index}].result" value="L" disabled <c:if test="${spotCheckResult.result == 'L'}">checked</c:if> /> L</label></td>
																		<td><label class="radio-inline"><input type="radio" name="spotCheckResultList[${status.index}].result" value="M" disabled <c:if test="${spotCheckResult.result == 'M'}">checked</c:if> /> M</label></td>
																		<td><label class="radio-inline"><input class='enableInput' type="radio" name="spotCheckResultList[${status.index}].result" value="Others" disabled <c:if test="${spotCheckResult.result == 'Others'}">checked</c:if> /> Others(pl. specify)</label></td>
																		<td>
																			<input name="spotCheckResultList[${status.index}].otherRemark" type="text" class="form-control" value="<c:out value="${spotCheckResult.otherRemark}" />" disabled maxlength="4000" />
																		</td>
																		<td style="width:20%">
																			<select class="form-control select2-addable col-sm-2" name="spotCheckResultList[${status.index}].referenceNo" disabled>
																				<option value="<c:out value="${spotCheckResult.referenceNo}" />">${spotCheckResult.referenceNo}</option>
																			</select>
																		</td>
																	</tr>
																</c:forEach>
															</c:if>
														</tbody>
													</table>
												</div>
												
											</div>
											<%--<div class="form-group">
			       								<label class="col-sm-2 control-label">Case ref No: </label>
			       								<div class="col-sm-3">
			       									<select class="form-control select2-addable col-sm-2" name="caseReferenceNo" disabled >
														<option value=""></option>
														<c:forEach items="${model.referenceNoList}" var="referenceNo">
															<option value="<c:out value="${referenceNo}" />" <c:if test="${model.caseReferenceNo != null && model.caseReferenceNo == referenceNo}">selected</c:if>>${referenceNo}</option>
														</c:forEach>
													</select>
												</div>
											</div>--%>
											<div class="form-group">
			       								<label class="col-sm-2 control-label">Remarks for tel. non-contact / late call back: </label>
			       								<div class="col-sm-5">
													<input name="remarksForNonContact" type="text" class="form-control" value="<c:out value="${model.remarksForNonContact}" />" disabled maxlength="4000" />
												</div>
											</div>
											<div class="form-group">
			       								<label class="col-sm-2 control-label">Scheduled Place: </label>
			       								<div class="col-sm-5">
			       									<input name="scheduledPlace" type="text" class="form-control" value="<c:out value="${model.scheduledPlace}" />" disabled maxlength="4000" />
												</div>
											</div>
											<div class="form-group">
			       								<label class="col-sm-2 control-label">Scheduled Time: </label>
			       								<div class="col-sm-3 input-group bootstrap-timepicker">
			       									<input name="scheduledTime" type="text" class="form-control" value="<c:out value="${model.scheduledTime}" />" disabled maxlength="4000" />
													<div class="input-group-addon">
														<i class="fa fa-clock-o"></i>
													</div>
												</div>
											</div>
											<div class="form-group">
			       								<label class="col-sm-2 control-label">Turn up time: </label>
			       								<div class="col-sm-3 input-group bootstrap-timepicker">
			       									<input name="turnUpTime" type="text" class="form-control" value="<c:out value="${model.turnUpTime}" />" disabled maxlength="4000"  />
			       									<div class="input-group-addon">
														<i class="fa fa-clock-o"></i>
													</div>
												</div>
												<div class="col-sm-7">
													hrs&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
													<label class="radio-inline">
														<input type="radio" name="reasonable" value="true" disabled <c:if test="${model.reasonable == 'true'}">checked</c:if>> Reasonable
													</label>
													<label class="radio-inline">
														<input type="radio" name="reasonable" value="false" disabled <c:if test="${model.reasonable == 'false'}">checked</c:if>> Non Reasonable
													</label>
												</div>
											</div>
											<div class="form-group">
			       								<label class="col-sm-2 control-label">Time-log vs Pre-app Itinerary:</label>
												<div class="col-sm-5">
													<label class="radio-inline">
														<input type="radio" name="irregular" value="false" disabled <c:if test="${model.irregular == 'false'}">checked</c:if>> No Irregularities
													</label>
													<label class="radio-inline">
														<input type="radio" name="irregular" value="true" disabled <c:if test="${model.irregular == 'true'}">checked</c:if>> Irregularities found
													</label>
												</div>
											</div>
											<div class="form-group">
			       								<label class="col-sm-2 control-label">
			       									Remarks for turn up time not reasonable (the scheduled meeting > 20 mins.) / irregular found: 
			       								</label>
			       								<div class="col-sm-5">
			       									<input name="remarkForTurnUpTime" type="text" class="form-control" value="<c:out value="${model.remarkForTurnUpTime}" />" disabled maxlength="4000" />
												</div>
											</div>
										</div>
									</div>
									<div class="form-group">
	       								<label class="col-sm-2 control-label">Verification Check:</label>
									</div>
									<div class="col-sm-12">
										<div class="box" style="padding:1px;">
											<div class="form-group">
			       								<label class="col-sm-2 control-label">1)</label>
											</div>
		       								<div class="form-group">
			       								<label class="col-sm-2 control-label">Ref. No.</label>
			       								<div class="col-sm-3">
			       									<select class="form-control select2-addable col-sm-2" name="verCheck1RefenceNo" disabled >
														<option value=""></option>
														<c:forEach items="${model.referenceNoList}" var="referenceNo">
															<option value="<c:out value="${referenceNo}" />" <c:if test="${model.verCheck1RefenceNo != null && model.verCheck1RefenceNo == referenceNo}">selected</c:if>>${referenceNo}</option>
														</c:forEach>
													</select>
												</div>
											</div>
			       							<div class="form-group">
			       								<label class="col-sm-2 control-label">Verification:</label>
												<div class="col-sm-5">
													<label class="radio-inline">
														<input type="radio" name="verCheck1IsIrregular" value="false" disabled <c:if test="${model.verCheck1IsIrregular == 'false'}">checked</c:if>> No irregularities
													</label>
													<label class="radio-inline">
														<input type="radio" name="verCheck1IsIrregular" value="true" disabled <c:if test="${model.verCheck1IsIrregular == 'true'}">checked</c:if>> irregularities found
													</label>
												</div>
											</div>
											<div class="form-group">
			       								<label class="col-sm-2 control-label">Remark: </label>
			       								<div class="col-sm-5">
			       									<input name="verCheck1Remark" type="text" class="form-control" value="<c:out value="${model.verCheck1Remark}" />" disabled maxlength="4000" />
												</div>
											</div>
	       								</div>
	       							</div>
	       							<hr/>
	       							<div class="col-sm-12">
										<div class="box" style="padding:1px;">
											<div class="form-group">
			       								<label class="col-sm-2 control-label">2)</label>
											</div>
		       								<div class="form-group">
			       								<label class="col-sm-2 control-label">Ref. No.</label>
			       								<div class="col-sm-3">
			       									<select class="form-control select2-addable col-sm-2" name="verCheck2RefenceNo" disabled >
														<option value=""></option>
														<c:forEach items="${model.referenceNoList}" var="referenceNo">
															<option value="<c:out value="${referenceNo}" />" <c:if test="${model.verCheck2RefenceNo != null && model.verCheck2RefenceNo == referenceNo}">selected</c:if>>${referenceNo}</option>
														</c:forEach>
													</select>
												</div>
											</div>
			       							<div class="form-group">
			       								<label class="col-sm-2 control-label">Verification:</label>
												<div class="col-sm-5">
													<label class="radio-inline">
														<input type="radio" name="verCheck2IsIrregular" value="false" disabled <c:if test="${model.verCheck2IsIrregular == 'false'}">checked</c:if>> No irregularities
													</label>
													<label class="radio-inline">
														<input type="radio" name="verCheck2IsIrregular" value="true" disabled <c:if test="${model.verCheck2IsIrregular == 'true'}">checked</c:if>> irregularities found
													</label>
												</div>
											</div>
											<div class="form-group">
			       								<label class="col-sm-2 control-label">Remark: </label>
			       								<div class="col-sm-5">
			       									<input name="verCheck2Remark" type="text" class="form-control" value="<c:out value="${model.verCheck2Remark}" />" disabled maxlength="4000" />
												</div>
											</div>
	       								</div>
	       							</div>
	       							<hr/>
									<div class="form-group">
	       								<%--<label class="col-sm-2 control-label"></label>--%>
	       								<div class="col-sm-2" style="text-align:right;">
	       									<label class="radio-inline">
												<input type="radio" name="successful" value="true" disabled <c:if test="${model.successful == 'true'}">checked</c:if>> Successful
											</label>
										</div>
										<div class="col-sm-5"></div>
									</div>
									<div class="form-group">
	       								<%--<label class="col-sm-2 control-label"></label>--%>
	       								<div class="col-sm-2" style="text-align:right;">
	       									<label class="radio-inline">
												<input type="radio" name="successful" value="false" disabled <c:if test="${model.successful == 'false'}">checked</c:if>> Unsuccessful
											</label>
										</div>
										<div class="col-sm-5">
											<input name="unSuccessfulRemark" type="text" class="form-control" value="<c:out value="${model.unSuccessfulRemark}" />" disabled <c:if test="${model.successful != false}">disabled</c:if> maxlength="4000" />
										</div>
									</div>
								</div>
							</div>
	       					<sec:authorize access="hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 256)">
								<div class="box-footer">
									<div class="col-md-2">
										<button type="submit" class="btn btn-info" name="approveRejectBtn" value="approve">Approve</button>
									</div>
									<div class="col-md-2">
	        							<button type="button" class="btn btn-info" name="approveRejectBtn" id="rejectBtn" value="reject" onclick="reject()">Reject</button>
	        						</div>
	       						</div>
	       					</sec:authorize>
						</div>
	        		</div>
	        	</div>
        	</form>
        	
			<div class="modal fade" id="rejectReasonForm" tabindex="-1" role="dialog" aria-labelledby="dialogLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content">
						<div  class="clearfix overlay" data-bind="loading:isLoading, visible:isLoading" ></div>
						<form action="<c:url value='/qualityControlManagement/SpotCheckApproval/reject'/>" method="post" id="rejectReasonInputForm">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
								<h4 class="modal-title" id="dialogLabel" data-bind="text:formTitle"></h4>
							</div>
							<div class="modal-body form-horizontal">
								<div class="form-group">
									<div class="col-md-3 control-label">Reject Reason : </div>
								</div>
								<div class="form-group">
									<div class="col-md-1"></div>
									<div class="col-md-10">
										<input name="rejectReason" type="text" class="form-control" value="" maxlength="4000" required />
									</div>
								</div>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
								<button type="submit" class="btn btn-primary" data-loading-text="Loading..." id="rejectSubmitBtn">Submit</button>
							</div>
							<input name="spotCheckFormId" value="<c:out value="${model.spotCheckFormId}" />" type="hidden" />
						</form>
					</div>
				</div>
			</div>
       		
        </section>
	</jsp:body>
</t:layout>
