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
		</style>
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/timepicker.jsp" %>		
		<%@include file="/WEB-INF/views/includes/datepicker.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/userLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/assignmentLookupSurveyMonth.jsp" %>
		<script src="<c:url value='/resources/js/jquery.validate.custom.js'/>" ></script>
		<script>
			var supervisoryVisitDetailListLength = ${fn:length(model.supervisoryVisitDetailList)};
			
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
				
				$('.select2ajax').select2ajax({
					allowClear: true,
					placeholder: '',
					width:'100%',
					multiple: false,
					closeOnSelect: true
				});
				
				$('.select2ajax').hide();
				
				<sec:authorize access="!(hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 256))">
					viewOnly();
				</sec:authorize>
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Supervisory Visit Approval</h1>
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
        	<form id="mainForm" action="<c:url value='/qualityControlManagement/SupervisoryVisitApproval/approve'/>" method="post" role="form">
        		<input name="supervisoryVisitFormId" value="<c:out value="${model.supervisoryVisitFormId}" />" type="hidden" />
        		<%--<input name="surveyMonthId" value="<c:out value="${model.surveyMonthId}" />" type="hidden" />--%>
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/qualityControlManagement/SupervisoryVisitMaintenance/home'/>">Back</a>
							</div>
	       					<div class="box-body">
	       						<div class="form-horizontal">
	       							<%--<div class="form-group">
										<label class="col-sm-2 control-label"></label>
										<div class="col-sm-2 pull-right">
											<label class="radio-inline">
												<input type="radio" name="session" value="1" <c:if test="${model.session == '1'}">checked</c:if>> Day
											</label>
											<label class="radio-inline">
												<input type="radio" name="session" value="2" <c:if test="${model.session == '2'}">checked</c:if>> Night
											</label>
											<p id="sessionRequired"></p>
										</div>
										<label class="col-sm-2 control-label pull-right"></label>
										<br style="clear:both" />
									</div>--%>
	       							<div class="form-group">
										<label class="col-sm-2 control-label">Field Officer:</label>
										<p class="col-sm-2 control-label">${model.fieldOfficer}</p>
										<%--<label class="col-sm-3 control-label">Post:</label>
										<p class="col-sm-2 control-label">${model.fieldOfficerPost}</p>--%>
									</div>
									<%--<div class="form-group">
										<label class="col-sm-2 control-label">Supervisor:</label>
										<p class="col-sm-2 control-label">${model.supervisor}</p>
										<label class="col-sm-3 control-label">Post:</label>
										<p class="col-sm-2 control-label">${model.supervisorPost}</p>
									</div>--%>
									<br/><br/>
									<div class="form-group">
										<label class="col-sm-3 control-label">Date / Time of supervisory visit:</label>
										<div class="col-sm-3">
											<div class="input-group">
												<input type="text" name="visitDate" class="form-control date-picker" maxlength="10" value="<c:out value="${model.visitDate}" />" disabled >
												<div class="input-group-addon">
													<i class="fa fa-calendar"></i>
												</div>
											</div>
										</div>
									</div>
									<div class="form-group">
										<label class="col-sm-3 control-label">From:</label>
										<div class="col-sm-2">
											<div class="input-group bootstrap-timepicker">
												<input name="fromTime" type="text" class="form-control timepicker" value="<c:out value="${model.fromTime}" />" disabled />
												<div class="input-group-addon">
													<i class="fa fa-clock-o"></i>
												</div>
											</div>
										</div>
										<label class="col-sm-2 control-label">To:</label>
										<div class="col-sm-2">
											<div class="input-group bootstrap-timepicker">
												<input name="toTime" type="text" class="form-control timepicker" value="<c:out value="${model.toTime}" />" disabled />
												<div class="input-group-addon">
													<i class="fa fa-clock-o"></i>
												</div>
											</div>
										</div>
										<div class="col-sm-1">(Time)</div>
									</div>
									<%--<c:if test="${not empty model.rejectReason}">
										<div class="form-group">
		       								<label class="col-sm-2 control-label">Reject Reason: </label>
		       								<p class="col-sm-5 control-label" style="text-align:left;">${model.rejectReason}</p>
										</div>
									</c:if>--%>
									<div class="form-group">
	       								<label class="col-sm-4 control-label">A. Details of establishments / living quarters visited</label>
									</div>
									<div class="col-sm-12">
		       							<div class="box" style="padding-top:5px;">
		       								
		       								<div class="box-body">
												<table class="table table-striped table-bordered table-hover" id="supervisoryVisitDetailList">
													<thead>
														<tr>
															<th></th>
															<th class="text-center action"></th>
														</tr>
													</thead>
													<tbody>
														<c:if test="${fn:length(model.supervisoryVisitDetailList) gt 0}" >
															<c:forEach var="supervisoryVisitDetail" items="${model.supervisoryVisitDetailList}" varStatus="status">
																<tr>
																	<td>
																		<div class="form-group">
									       									<label class="col-sm-2 control-label">Assignment:</label>
									       									<div class="col-sm-3 " name="div${status.index}_1">
																				<div class="input-group">
																					<%--<select class="form-control select2-addable col-sm-2" name="supervisoryVisitDetailList[${status.index}].assignmentId" required>
																						<option value=""></option>
																						<c:forEach items="${model.detailAssignmentIdList}" var="detailAssignmentId">
																							<option value="<c:out value="${detailAssignmentId.assignmentId}" />" <c:if test="${supervisoryVisitDetail.rowNumber == (status.index+1) && supervisoryVisitDetail.assignmentId != null && supervisoryVisitDetail.assignmentId == detailAssignmentId.assignmentId}">selected</c:if>>${detailAssignmentId.referenceNo}</option>
																						</c:forEach>
																					</select>--%>
																					<select name="supervisoryVisitDetailList[${status.index}].assignmentId" class="form-control select2ajax"
																						data-ajax-url="<c:url value='/qualityControlManagement/SupervisoryVisitMaintenance/queryAssignmentWithSurveyMonthSelect2'/>?surveyMonthId=${model.surveyMonthId}"
																						data-single-url="<c:url value='/qualityControlManagement/SupervisoryVisitMaintenance/getAssignmentWithSurveyMonthById'/>" disabled >
																							<c:if test="${supervisoryVisitDetail.rowNumber == (status.index+1) && supervisoryVisitDetail.assignmentId != null}" >
																								<option value="<c:out value="${supervisoryVisitDetail.assignmentId}" />" selected >${supervisoryVisitDetail.referenceNo}</option>
																							</c:if>
																					</select>
																					<div class="input-group-addon searchAssignmentId_${status.index}">
																						<i class="fa fa-search"></i>
																					</div>
																				</div>
																			</div>
																			<label class="col-sm-2 control-label">Survey:</label>
									       									<div class="col-sm-3"name="div${status.index}_2">
										       									<select class="form-control select2-addable col-sm-2" name="supervisoryVisitDetailList[${status.index}].survey" disabled>
																					<option value=""></option>
																					<c:set var="varCheck" value="false" />
																					<c:forEach items="${model.detailSurveyList}" var="survey">
																						<option value="<c:out value="${survey}" />" 
																							<c:if test="${supervisoryVisitDetail.rowNumber == (status.index+1) && supervisoryVisitDetail.survey != null && supervisoryVisitDetail.survey == survey}">
																							<c:set var="varCheck" value="true" />
																							selected
																							</c:if>>
																							${survey}
																						</option>
																					</c:forEach>
																					<c:if test="${(not varCheck) && supervisoryVisitDetail.survey != null}">
																						<option value="<c:out value="${supervisoryVisitDetail.survey}" />" selected >${supervisoryVisitDetail.survey}</option>
																					</c:if>
																				</select>
																			</div>
									       								</div>
									       								<div class="form-group">
																			<div class="col-sm-12">
																				<c:if test="${supervisoryVisitDetail.rowNumber == (status.index + 1)}">
																					<div class="col-sm-2">
																						<label class="radio-inline">
																							<input type="radio" name="supervisoryVisitDetailList[${status.index}].result" value="1" <c:if test="${supervisoryVisitDetail.result == '1'}">checked</c:if> disabled> Enumerated
																						</label>
																					</div>
																					<div class="col-sm-2">
																						<label class="radio-inline">
																							<input type="radio" name="supervisoryVisitDetailList[${status.index}].result" value="2" <c:if test="${supervisoryVisitDetail.result == '2'}">checked</c:if> disabled> Non-contacted
																						</label>
																					</div>
																					<div class="col-sm-2">
																						<label class="radio-inline">
																							<input type="radio" name="supervisoryVisitDetailList[${status.index}].result" value="3" <c:if test="${supervisoryVisitDetail.result == '3'}">checked</c:if> disabled> Others
																						</label>
																					</div>
																					<div class="col-sm-6">
												       									<input name="supervisoryVisitDetailList[${status.index}].otherRemark" type="text" class="form-control" value="<c:out value="${supervisoryVisitDetail.otherRemark}" />" disabled />
																					</div>
																				</c:if>
																			</div>
									       								</div>
																	</td>
																	<td>
																		<%--<input type="hidden" value="<c:out value="${supervisoryVisitDetail.supervisoryVisitDetailId}" />" name="supervisoryVisitDetailList[${status.index}].supervisoryVisitDetailId" />
																		<c:if test="${status.index > 0}">
																			<a href="javascript:void(0)" onclick="deleteSupervisoryVisitDetailRow(this)" class="table-btn btn-delete">
																			<span class="fa fa-times" aria-hidden="true"></span></a>
																		</c:if>--%>
																	</td>
																</tr>
															</c:forEach>
														</c:if>
													</tbody>
												</table>
											</div>
											<%--<div style="margin-left: 15px;">
												<a href="javascript:void(0)" class="addSupervisoryVisitDetail"><span class="glyphicon glyphicon-plus"></span>&nbsp;&nbsp;Add</a>															
											</div>--%>
		       								
		       						</div>
		       						<div class="form-group">
	       								<label class="col-sm-4 control-label">B. Record of discussion after supervisory visit</label>
									</div>
									<div class="form-group">
										<label class="col-sm-1 control-label">Date:</label>
	       								<div class="col-sm-3">
											<div class="input-group">
												<input type="text" name="discussionDate" class="form-control date-picker" maxlength="10" value="<c:out value="${model.discussionDate}" />" required disabled>
												<div class="input-group-addon">
													<i class="fa fa-calendar"></i>
												</div>
											</div>
										</div>
									</div>
									<div class="form-group">
										<label class="col-sm-1 control-label">Remark:</label>
										<div class="col-sm-4">
											<input name="remark" type="text" class="form-control" value="<c:out value="${model.remark}" />" disabled />
										</div>
									</div>
									<div class="form-group">
	       								<label class="col-sm-3 control-label">1. Knowledge of work</label>
									</div>
									<div class="col-sm-12">
										<div class="box" style="padding:1px;">
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
			       								<label class="radio-inline">
													<input type="radio" name="knowledgeOfWorkResult" value="1" <c:if test="${model.knowledgeOfWorkResult == '1'}">checked</c:if> disabled> Merits observed / Areas for attention / Other remarks
												</label>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<div class="input-group col-sm-5">
													<input name="knowledgeOfWorkRemark" type="text" class="form-control" value="<c:out value="${model.knowledgeOfWorkRemark}" />" disabled />
												</div>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<label class="radio-inline">
													<input type="radio" name="knowledgeOfWorkResult" value="2" <c:if test="${model.knowledgeOfWorkResult == '2'}">checked</c:if> disabled> No comment
												</label>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<label class="radio-inline">
													<input type="radio" name="knowledgeOfWorkResult" value="3" <c:if test="${model.knowledgeOfWorkResult == '3'}">checked</c:if> disabled> N / A
												</label>
											</div>
	       								</div>
	       							</div>
	       							<div class="form-group">
	       								<label class="col-sm-3 control-label">2. Interviewing techniques</label>
									</div>
									<div class="col-sm-12">
										<div class="box" style="padding:1px;">
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
			       								<label class="radio-inline">
													<input type="radio" name="interviewTechniqueResult" value="1" <c:if test="${model.interviewTechniqueResult == '1'}">checked</c:if> disabled> Merits observed / Areas for attention / Other remarks
												</label>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<div class="input-group col-sm-5">
													<input name="interviewTechniqueRemark" type="text" class="form-control" value="<c:out value="${model.interviewTechniqueRemark}" />" disabled />
												</div>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<label class="radio-inline">
													<input type="radio" name="interviewTechniqueResult" value="2" <c:if test="${model.interviewTechniqueResult == '2'}">checked</c:if> disabled> No comment
												</label>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<label class="radio-inline">
													<input type="radio" name="interviewTechniqueResult" value="3" <c:if test="${model.interviewTechniqueResult == '3'}">checked</c:if> disabled> N / A
												</label>
											</div>
	       								</div>
	       							</div>
	       							<div class="form-group">
	       								<label class="col-sm-4 control-label">3. Tact in handling difficult interviews</label>
									</div>
									<div class="col-sm-12">
										<div class="box" style="padding:1px;">
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
			       								<label class="radio-inline">
													<input type="radio" name="handleDifficultInterviewResult" value="1" <c:if test="${model.handleDifficultInterviewResult == '1'}">checked</c:if> disabled> Merits observed / Areas for attention / Other remarks
												</label>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<div class="input-group col-sm-5">
													<input name="handleDifficultInterviewRemark" type="text" class="form-control" value="<c:out value="${model.handleDifficultInterviewRemark}" />" disabled />
												</div>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<label class="radio-inline">
													<input type="radio" name="handleDifficultInterviewResult" value="2" <c:if test="${model.handleDifficultInterviewResult == '2'}">checked</c:if> disabled> No comment
												</label>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<label class="radio-inline">
													<input type="radio" name="handleDifficultInterviewResult" value="3" <c:if test="${model.handleDifficultInterviewResult == '3'}">checked</c:if> disabled> N / A
												</label>
											</div>
	       								</div>
	       							</div>
	       							<div class="form-group">
	       								<label class="col-sm-3 control-label">4. Data recording / Map reading</label>
									</div>
									<div class="col-sm-12">
										<div class="box" style="padding:1px;">
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
			       								<label class="radio-inline">
													<input type="radio" name="dataRecordingResult" value="1" <c:if test="${model.dataRecordingResult == '1'}">checked</c:if> disabled> Merits observed / Areas for attention / Other remarks
												</label>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<div class="input-group col-sm-5">
													<input name="dataRecordingRemark" type="text" class="form-control" value="<c:out value="${model.dataRecordingRemark}" />" disabled />
												</div>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<label class="radio-inline">
													<input type="radio" name="dataRecordingResult" value="2" <c:if test="${model.dataRecordingResult == '2'}">checked</c:if> disabled> No comment
												</label>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<label class="radio-inline">
													<input type="radio" name="dataRecordingResult" value="3" <c:if test="${model.dataRecordingResult == '3'}">checked</c:if> disabled> N / A
												</label>
											</div>
	       								</div>
	       							</div>
	       							<div class="form-group">
	       								<label class="col-sm-3 control-label">5. Knowledge of local geography</label>
									</div>
									<div class="col-sm-12">
										<div class="box" style="padding:1px;">
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
			       								<label class="radio-inline">
													<input type="radio" name="localGeographyResult" value="1" <c:if test="${model.localGeographyResult == '1'}">checked</c:if> disabled> Merits observed / Areas for attention / Other remarks
												</label>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<div class="input-group col-sm-5">
													<input name="localGeographyRemark" type="text" class="form-control" value="<c:out value="${model.localGeographyRemark}" />" disabled />
												</div>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<label class="radio-inline">
													<input type="radio" name="localGeographyResult" value="2" <c:if test="${model.localGeographyResult == '2'}">checked</c:if> disabled> No comment
												</label>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<label class="radio-inline">
													<input type="radio" name="localGeographyResult" value="3" <c:if test="${model.localGeographyResult == '3'}">checked</c:if> disabled> N / A
												</label>
											</div>
	       								</div>
	       							</div>
	       							<div class="form-group">
	       								<label class="col-sm-3 control-label">6. Manner with the public, attire</label>
									</div>
									<div class="col-sm-12">
										<div class="box" style="padding:1px;">
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
			       								<label class="radio-inline">
													<input type="radio" name="mannerWithPublicResult" value="1" <c:if test="${model.mannerWithPublicResult == '1'}">checked</c:if> disabled> Merits observed / Areas for attention / Other remarks
												</label>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<div class="input-group col-sm-5">
													<input name="mannerWithPublicRemark" type="text" class="form-control" value="<c:out value="${model.mannerWithPublicRemark}" />" disabled />
												</div>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<label class="radio-inline">
													<input type="radio" name="mannerWithPublicResult" value="2" <c:if test="${model.mannerWithPublicResult == '2'}">checked</c:if> disabled> No comment
												</label>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<label class="radio-inline">
													<input type="radio" name="mannerWithPublicResult" value="3" <c:if test="${model.mannerWithPublicResult == '3'}">checked</c:if> disabled> N / A
												</label>
											</div>
	       								</div>
	       							</div>
	       							<div class="form-group">
	       								<label class="col-sm-2 control-label">7. Judgment</label>
									</div>
									<div class="col-sm-12">
										<div class="box" style="padding:1px;">
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
			       								<label class="radio-inline">
													<input type="radio" name="judgmentResult" value="1" <c:if test="${model.judgmentResult == '1'}">checked</c:if> disabled> Merits observed / Areas for attention / Other remarks
												</label>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<div class="input-group col-sm-5">
													<input name="judgmentRemark" type="text" class="form-control" value="<c:out value="${model.judgmentRemark}" />" disabled />
												</div>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<label class="radio-inline">
													<input type="radio" name="judgmentResult" value="2" <c:if test="${model.judgmentResult == '2'}">checked</c:if> disabled> No comment
												</label>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<label class="radio-inline">
													<input type="radio" name="judgmentResult" value="3" <c:if test="${model.judgmentResult == '3'}">checked</c:if> disabled> N / A
												</label>
											</div>
	       								</div>
	       							</div>
	       							<div class="form-group">
	       								<label class="col-sm-3 control-label">8. Organization of work</label>
									</div>
									<div class="col-sm-12">
										<div class="box" style="padding:1px;">
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
			       								<label class="radio-inline">
													<input type="radio" name="organizationOfWorkResult" value="1" <c:if test="${model.organizationOfWorkResult == '1'}">checked</c:if> disabled> Merits observed / Areas for attention / Other remarks
												</label>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<div class="input-group col-sm-5">
													<input name="organizationOfWorkRemark" type="text" class="form-control" value="<c:out value="${model.organizationOfWorkRemark}" />" disabled />
												</div>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<label class="radio-inline">
													<input type="radio" name="organizationOfWorkResult" value="2" <c:if test="${model.organizationOfWorkResult == '2'}">checked</c:if> disabled> No comment
												</label>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<label class="radio-inline">
													<input type="radio" name="organizationOfWorkResult" value="3" <c:if test="${model.organizationOfWorkResult == '3'}">checked</c:if> disabled> N / A
												</label>
											</div>
	       								</div>
	       							</div>
	       							<div class="form-group">
	       								<label class="col-sm-2 control-label">9. Others</label>
									</div>
									<div class="col-sm-12">
										<div class="box" style="padding:1px;">
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
			       								<label class="radio-inline">
													<input type="radio" name="otherResult" value="1" <c:if test="${model.otherResult == '1'}">checked</c:if> disabled> Merits observed / Areas for attention / Other remarks
												</label>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<div class="input-group col-sm-5">
													<input name="otherRemark" type="text" class="form-control" value="<c:out value="${model.otherRemark}" />" disabled />
												</div>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<label class="radio-inline">
													<input type="radio" name="otherResult" value="2" <c:if test="${model.otherResult == '2'}">checked</c:if> disabled> No comment
												</label>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<label class="radio-inline">
													<input type="radio" name="otherResult" value="3" <c:if test="${model.otherResult == '3'}">checked</c:if> disabled> N / A
												</label>
											</div>
	       								</div>
	       							</div>
	       							<hr/>
	       							<%--<div class="form-group">
	       								<label class="col-sm-2 control-label">Submit To: </label>
	       								<div class="col-sm-3">
											<div class="input-group">
												<input name="submitTo" type="text" class="form-control" value="<c:out value="${model.submitTo}" />" required readonly />
												<input id="submitToId" name="submitToId" value="<c:out value="${model.submitToId}" />" type="hidden" />
												<div class="input-group-addon searchSubmitToId">
													<i class="fa fa-search"></i>
												</div>
											</div>
										</div>
									</div>--%>
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
	        	</div>
        	</form>
        	
        	<div class="modal fade" id="rejectReasonForm" tabindex="-1" role="dialog" aria-labelledby="dialogLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content">
						<div  class="clearfix overlay" data-bind="loading:isLoading, visible:isLoading" ></div>
						<form action="<c:url value='/qualityControlManagement/SupervisoryVisitApproval/reject'/>" method="post" id="rejectReasonInputForm">
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
							<input name="supervisoryVisitFormId" value="<c:out value="${model.supervisoryVisitFormId}" />" type="hidden" />
						</form>
					</div>
				</div>
			</div>
        	
        </section>
	</jsp:body>
</t:layout>
