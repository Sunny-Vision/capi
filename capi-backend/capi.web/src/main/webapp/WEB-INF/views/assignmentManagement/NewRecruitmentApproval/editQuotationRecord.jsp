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
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/timepicker-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
		<%@include file="/WEB-INF/views/shared/quotationRecord/page-css.jsp" %>
	</jsp:attribute>
	<jsp:attribute name="header">
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker.jsp"%>
		<%@include file="/WEB-INF/views/includes/timepicker.jsp" %>
		<%@include file="/WEB-INF/views/includes/handlebars.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/radioPhotos.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/imageViewer.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/productLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/changeListener.jsp"%>
		<%@include file="/WEB-INF/views/includes/commonDialog/textViewer.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/productSpecDialog.jsp"%>
		<%@include file="/WEB-INF/views/shared/quotationRecord/page-js.jsp" %>
		<script>
			$(function() {
				var $mainForm = $('#mainForm');
				
				var quotationRecordFormContainerPlugin = $('.quotation-current-container .quotation-record-form-container').quotationRecordFormContainer('plugin');
				
				$mainForm.validate({
					ignore: $.proxy(quotationRecordFormContainerPlugin.formValidateIgnore, quotationRecordFormContainerPlugin),
					invalidHandler: $.proxy(quotationRecordFormContainerPlugin.formValidateInvalidHandler, quotationRecordFormContainerPlugin)
				});
				
				Datepicker();
				
				$('.btn-header-collapse').click(function() {
					var $btn = $(this);
					var $box = $btn.closest('.box-tools');
					if ($btn.find('.fa-plus').length > 0)
						$box.find('.header-mark').hide();
					else
						$box.find('.header-mark').show();
				});
				/* 
				var selectBackTrackOptions = {
					escapeMarkup: function (markup) { return markup; },
					templateResult: function(state) {
						if ($(state.element).data('passValidation'))
							return state.text;
						else
							return state.text + ' <i class="fa fa-exclamation-triangle"></i>';
					},
					templateSelection: function(state) {
						if ($(state.element).data('passValidation'))
							return state.text;
						else
							return state.text + ' <i class="fa fa-exclamation-triangle"></i>';
					}
				};
				if (!$('.select-backtrack').data('passValidation')) {
					selectBackTrackOptions.containerCssClass = 'bg-red';
				}
				$('.select-backtrack').select2(selectBackTrackOptions); */
				$('.btn-backtrack').click(function() {
					var url = '<c:url value='/assignmentManagement/NewRecruitmentApproval/editQuotationRecord'/>';
					var quotationRecordId = $(this).val();
					location = url + '?quotationRecordId=' + quotationRecordId;
				});
				
				$('#rejectReasonForm form').validate();
				$('#btnReject,#btnRejectAndNext').click(function() {
					var btnValue = $(this).val();
					$('#rejectReasonForm [name="approveRejectBtn"]').val(btnValue);
					$('#rejectReasonForm').modal('show');
				});
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>New Recruitment Approval</h1>
			<div class="breadcrumb form-horizontal" style="width:240px">
				<div class="form-group" style="margin-bottom:0px">
		        	<div class="col-sm-5">Created Date:</div>
		        	<div class="col-sm-7">${commonService.formatDateTime(model.quotationRecord.createdDate)}</div>
		        </div>
		        <div class="form-group" style="margin-bottom:0px">
		         	<div class="col-sm-5">Last Modified:</div>
		         	<div class="col-sm-7">${commonService.formatDateTime(model.quotationRecord.modifiedDate)}</div>
		         </div>
	      	</div>
        </section>
        
        <section class="content">
        	<form id="mainForm" action="<c:url value='/assignmentManagement/NewRecruitmentApproval/approveRejectQuotationRecord'/>" method="post" role="form"
        		enctype="multipart/form-data">
        		<input name="dirty" value="<c:out value="${model.dirty ? 'true' : 'false'}"/>" type="hidden"/>
        		<input name="quotationRecordId" value="<c:out value="${model.quotationRecord.id}"/>" type="hidden"/>
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<c:set var="show" scope="page" value="false"/>
	        			<c:if test="${not empty model.pointToNote || not empty model.verificationRemark || (model.quotationRecord.status == 'Rejected' && not empty model.quotationRecord.status) || not empty model.peCheckRemark || not empty model.validationSummary || not empty model.quotationRecord.reminderForPricingCycleMessage}">
	        				<c:set var="show" scope="page" value="true"/>
	        			</c:if>
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
								<a class="btn btn-default btn-back" href="<c:url value='/assignmentManagement/NewRecruitmentApproval/edit'/>?assignmentId=${model.assignmentId}&personInChargeId=${sessionModel_personInChargeId}">Back</a>
								<c:if test="${show}">
								<div class="box-tools pull-right">
									<i class="fa fa-exclamation-triangle header-mark" style="display: none"></i>
	        						<button class="btn btn-box-tool btn-header-collapse" type="button" data-widget="collapse"><i class="fa fa-minus"></i></button>
	        					</div>
								</c:if>
								<%-- hide Point to note row when empty, show ! after collapse if have content
	        					<div class="box-tools pull-right">
	        						<button class="btn btn-box-tool" type="button" data-widget="collapse"><i class="fa fa-minus"></i></button>
	        					</div>
	        					--%>
							</div>
							<c:if test="${show}">
	       					<div class="box-body">
	       						<div class="form-horizontal">
       								<div class="form-group">
       									<c:if test="${not empty model.pointToNote}">
	       								<label class="col-md-1 control-label text-danger">Point to note:</label>
	       								<div class="col-md-2">
											<p class="form-control-static text-danger">${model.pointToNote}</p>
										</div>
										</c:if>
										
										<c:if test="${not empty model.verificationRemark}">
	       								<label class="col-md-1 control-label">Verification Remark:</label>
	       								<div class="col-md-2">
											<p class="form-control-static">${model.verificationRemark}</p>
										</div>
										</c:if>
										
										<c:if test="${model.quotationRecord.status == 'Rejected' and not empty model.rejectReason}">
	       								<label class="col-md-1 control-label">Reject Reason:</label>
	       								<div class="col-md-2">
											<p class="form-control-static">${model.rejectReason}</p>
										</div>
										</c:if>
										
										<c:if test="${not empty model.peCheckRemark}">
	       								<label class="col-md-1 control-label">PE Check Remark:</label>
	       								<div class="col-md-2">
											<p class="form-control-static">${model.peCheckRemark}</p>
										</div>
										</c:if>
									</div>
									<c:if test="${not empty model.validationSummary}">
									<div class="form-group">
										<label class="col-md-2 control-label text-danger">Validation Summary:</label>
										<div class="col-md-10">
											<pre class="form-control-static text-danger">${model.validationSummary}</pre>
										</div>
									</div>
									</c:if>
									<c:if test="${not empty model.quotationRecord.reminderForPricingCycleMessage}">
										<div class="form-group">
		       								<p class="col-md-12 form-control-static">${model.quotationRecord.reminderForPricingCycleMessage}</p>
										</div>
									</c:if>
								</div>
							</div>
        					</c:if>
						</div>
						
						<div class="box box-primary">
							<div class="box-body">
								<div class="form-horizontal">
									<div class="form-group">
										<c:if test="${not empty model.backTrackDates}">
										<label class="col-md-1 control-label">BackTrack</label>
										<%--<div class="col-md-2">
											<select class="form-control select-backtrack"
												data-pass-validation="${model.allBackTrackPassValidation}">
												<c:forEach items="${model.backTrackDates}" var="backtrackFilter">
												<option value="<c:out value="${backtrackFilter.quotationRecordId}"/>"
													data-pass-validation="${backtrackFilter.passValidation}"
													${model.quotationRecord.id == backtrackFilter.quotationRecordId ? "selected" : ""}>${backtrackFilter.date}</option>
												</c:forEach>
											</select>
										</div> --%>
										<div class="col-md-6">
											<c:forEach items="${model.backTrackDates}" var="backtrackFilter">
												<button type="button" class="btn btn-backtrack
													${backtrackFilter.passValidation ? "btn-default" : "btn-danger"} 
													${model.quotationRecord.id == backtrackFilter.quotationRecordId ? "active disabled" : ""}"
													
													value = "<c:out value="${backtrackFilter.quotationRecordId}"/>"
													>
													${backtrackFilter.passValidation ? "" : "<i class='fa fa-exclamation-triangle'></i>"} 
													${backtrackFilter.date}</button>
											</c:forEach>
										</div>
										</c:if>
										<div class="col-md-5 custom-page-nav pull-right" style="text-align: right">
											<c:choose>
											<c:when test="${model.previousQuotationRecordId != null}">
											<a class="btn btn-default"
												href="<c:url value='/assignmentManagement/NewRecruitmentApproval/editQuotationRecord'/>?quotationRecordId=${model.previousQuotationRecordId}"><i class="fa fa-caret-left"></i></a>
											</c:when>
											<c:otherwise>
											<a class="btn btn-default" href="#" disabled><i class="fa fa-caret-left"></i></a>
											</c:otherwise>
											</c:choose>
											
											<span>${model.currentQuotationRecordNumber}</span>
											<span>/</span>
											<span>${model.totalQuotationRecords}</span>
											
											<c:choose>
											<c:when test="${model.nextQuotationRecordId != null}">
											<a class="btn btn-default"
												href="<c:url value='/assignmentManagement/NewRecruitmentApproval/editQuotationRecord'/>?quotationRecordId=${model.nextQuotationRecordId}"><i class="fa fa-caret-right"></i></a>
											</c:when>
											<c:otherwise>
											<a class="btn btn-default" href="#" disabled><i class="fa fa-caret-right"></i></a>
											</c:otherwise>
											</c:choose>
										</div>
									</div>
								</div>
							</div>
						</div>
	        			
	        			<%@include file="/WEB-INF/views/shared/quotationRecord/page.jsp" %>
	       				
	        			<div class="box box-default">
	       					<div class="box-footer">
	       						<sec:authorize access="hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)">
	        						<button name="approveRejectBtn" value="approve" type="submit" class="btn btn-info">Approve</button>
	        						<button id="btnReject" value="reject" type="button" class="btn btn-info">Reject</button>
	       							<c:if test="${model.nextQuotationRecordId != null or model.previousQuotationRecordId != null}">
	        						<button name="approveRejectBtn" value="approveAndNext" type="submit" class="btn btn-info">Approve and Next</button>
	        						</c:if>
	       							<c:if test="${model.nextQuotationRecordId != null or model.previousQuotationRecordId != null}">
	        						<button id="btnRejectAndNext" value="rejectAndNext" type="button" class="btn btn-info">Reject and Next</button>
	        						</c:if>
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
						<form action="<c:url value='/assignmentManagement/NewRecruitmentApproval/approveRejectQuotationRecord'/>" method="post" id="rejectReasonInputForm">
							<input name="approveRejectBtn" type="hidden" />
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
								<button type="submit" class="btn btn-primary" data-loading-text="Loading...">Submit</button>
							</div>
							<input name="quotationRecordId" value="<c:out value="${model.quotationRecord.id}" />" type="hidden" />
						</form>
					</div>
				</div>
			</div>
        </section>
	</jsp:body>
</t:layout>

