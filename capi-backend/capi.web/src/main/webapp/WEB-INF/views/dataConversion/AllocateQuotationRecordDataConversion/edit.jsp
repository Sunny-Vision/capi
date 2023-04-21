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
		<%@include file="/WEB-INF/views/includes/commonDialog/outletAttachmentDialog.jsp"%>
		<%@include file="/WEB-INF/views/shared/quotationRecord/page-js.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/userLookup.jsp" %>
		<script src="<c:url value='/resources/js/underscore-min.js' />"></script>
		<script>
			$(function() {
				var $mainForm = $('#mainForm');
				
				$('.searchUserId').userLookup({
					selectedIdsCallback: function(selectedIds, singleRowData) {
						var id = selectedIds[0];
						this.$element.find('input[name="userDisplay"]').val(singleRowData.staffCode
								+ "-" + singleRowData.englishName).trigger('change').valid();
						this.$element.find('input[name="userId"]').val(id).trigger('change');
					},
					queryDataCallback: function(model) {
						model.authorityLevel = 128;
					},
					multiple: false
				});
				
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
				
				$('#mainForm').changeListener({
					backButtons: ['.btn-back'],
					changeCallback: function() {
						$('[name=dirty]').val('true');
					}
				});
				if ($('[name=dirty]').val() == 'true') {
					$('#mainForm').changeListener('setChanged');
				}
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Allocate Quotation Record Data Conversion</h1>
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
        	<form id="mainForm" action="<c:url value='/dataConversion/AllocateQuotationRecordDataConversion/save'/>" method="post" role="form"
        		enctype="multipart/form-data">
        		<input name="dirty" value="<c:out value="${model.dirty ? 'true' : 'false'}"/>" type="hidden"/>
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<c:set var="show" scope="page" value="false"/>
	        			<c:if test="${not empty model.pointToNote || not empty model.verificationRemark || (model.quotationRecord.status == 'Rejected' && not empty model.quotationRecord.status) || not empty model.peCheckRemark || not empty model.quotationRecord.reminderForPricingCycleMessage}">
	        				<c:set var="show" scope="page" value="true"/>
	        			</c:if>
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
								<a class="btn btn-default btn-back" href="<c:url value="/dataConversion/AllocateQuotationRecordDataConversion/selectSurveyType"/>?purposeId=${purpose.getId()}&referenceMonthStr=${refMonthStr}">Back</a>
								
								<c:if test="${quotation.status == 'RUA'}">
	       							<label class="control-label" style="color:#ff0000 ">This is RUA Quotation Record.</label>
								</c:if>
								
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
	       								<label class="col-md-1 control-label">Point to note:</label>
	       								<div class="col-md-2">
											<p class="form-control-static">${model.pointToNote}</p>
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
									<c:if test="${not empty model.quotationRecord.reminderForPricingCycleMessage}">
										<div class="form-group">
		       								<p class="col-md-12 form-control-static">${model.quotationRecord.reminderForPricingCycleMessage}</p>
										</div>
									</c:if>
								</div>
							</div>
        					</c:if>
						</div>
	        			
	        			<%@include file="/WEB-INF/views/shared/quotationRecord/page.jsp" %>
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
	        					<h3 class="box-title">Indoor Data Conversion</h3>
	        				</div>
	        				<div class="box-body">
	       						<div class="form-horizontal">
       								<div class="form-group">
       									<label class="col-md-4 control-label">Indoor Data Conversion:</label>
	       								<div class="col-md-4">
	       									<input type="hidden" class="form-control" name="indoorQuotationRecordId" value="${indoorQuotationRecord.id}">
											<div class="input-group searchUserId">
													<input type="hidden" name="userId" class="form-control" readonly <c:if test="${user != null}">value="${user.userId}"</c:if> />
													<input type="text" name="userDisplay" class="form-control" readonly  <c:if test="${user != null}">value="${user.staffCode}-${user.englishName}"</c:if> required />
													<div class="input-group-addon">
														<i class="fa fa-search"></i>
													</div>
												</div>
										</div>
       								</div>
       							</div>
       						</div>
       					</div>
	        				
	        			
	       				
	        			<div class="box box-default">
	       					<div class="box-footer">
	       						<sec:authorize access="hasPermission(#user, 32) or hasPermission(#user, 256)">
	        						<button name="btnSubmit" value="submit" type="submit" class="btn btn-info">Allocate for Data Conversion</button>
	       						</sec:authorize>
	       					</div>
	        			</div>
	        		</div>
	        	</div>
        	</form>
        </section>
	</jsp:body>
</t:layout>

