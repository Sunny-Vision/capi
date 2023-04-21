<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
		<%@include file="/WEB-INF/views/shared/indoorQuotationRecord/partial/quotationRecord/page-css.jsp" %>
	</jsp:attribute>
	<jsp:attribute name="header">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker.jsp"%>
		<%@include file="/WEB-INF/views/includes/timepicker.jsp"%>
		<%@include file="/WEB-INF/views/includes/select2.jsp"%>
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<%@include file="/WEB-INF/views/includes/handlebars.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/radioPhotos.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/textViewer.jsp" %>
		<%@include file="/WEB-INF/views/includes/moment.jsp" %>
		<%@include file="/WEB-INF/views/shared/indoorQuotationRecord/partial/quotationRecord\page-js.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/productSpecDialog.jsp"%>
		<%@include file="/WEB-INF/views/includes/commonDialog/outletAttachmentDialog.jsp"%>
		<%@include file="/WEB-INF/views/includes/commonDialog/imageViewer.jsp" %>
		<script src="<c:url value='/resources/js/uncheckableRadio.js'/>"></script>
		<script src="<c:url value='/resources/js/underscore-min.js' />"></script>
		<%@include file="/WEB-INF/views/includes/changeListener.jsp"%>
		<script>
		function updateRevisitDateMode($form) {
			console.log($('[name="collectionDateStr"]', $form).val());
			if ($('[name="collectionDateStr"]', $form).val() != "") {
				$('[name="startDateStr"],[name="endDateStr"]', $form).attr('disabled','disabled');
			} else {
				$('[name="startDateStr"],[name="endDateStr"]', $form).removeAttr('disabled');
			}
			
			if ($('[name="startDateStr"]', $form).val() != "" || $('[name="endDateStr"]', $form).val() != "") {
				$('[name="collectionDateStr"]', $form).attr('disabled','disabled');
			} else {
				$('[name="collectionDateStr"]', $form).removeAttr('disabled');
			}
		}
		
		$(document).ready(function(){
			
			function updateButtonDisabled() {
				$('[name="btnSubmit"]').prop('disabled', false);
				if ($('.verifications:checked').length > 0) {
					$('[name="btnSubmit"]').filter('[value="submit"],[value="submitNext"]').prop('disabled', true);
				}
			}
			
			$("#mainForm").validate({ ignore: [] });
			//Datepicker($(".date-picker:not([readonly])"));
			//$(".date-picker:not([readonly])").datepicker();
			var d = new Date();
			
			$('[name="collectionDateStr"]').datepicker({
				startDate: d
			});
			$('[name="startDateStr"]').datepicker({
				startDate: d
			});
			$('[name="endDateStr"]').datepicker({
				startDate: d
			});
						
			$('.quotation-history-container').on('historyChanged', function() {
					var quotationRecordId = $(".history-date.active", $(this)).data("id");
					$.ajax({
						  type: "POST",
						  url: "<c:url value='/QuotationRecordDataReview/getProductPartial'/>",
						  data: {quotationRecordId: quotationRecordId},
						  success: function(result){
							  $(".productHistory").html(result);
						  },
						});
					$.ajax({
						  type: "POST",
						  url: "<c:url value='/QuotationRecordDataReview/getOutletPartial'/>",
						  data: {quotationRecordId: quotationRecordId},
						  success: function(result){
							  $(".outletHistory").html(result);
						  },
						});
					
					$('.compareSubPrice', this).on("click", function(){
						var currentQuotationRecordId = $('[name="quotationRecord.quotationRecordId"]',".quotation-current-container .quotation-record-main-form ").val();
						var historyQuotationRecordId = $('[name="quotationRecord.quotationRecordId"]',".quotation-history-container .quotation-record-main-form ").val();
						
						if(typeof currentQuotationRecordId != "undefined" && typeof currentQuotationRecordId != "undefined"){
							window.open('<c:url value="/commonDialog/SubPriceComparison/home"/>?quotationRecordId1='+currentQuotationRecordId+'&quotationRecordId2='+historyQuotationRecordId, '_blank');
						}
					});
				
			});
			
			$('.verifications').on("change", function(){
				$(".verificationRemarks").attr("Disabled", "Disabled");
				if($(this).is(":checked")){
					$(this).closest(".form-group").find("textarea").removeAttr("Disabled");
				}
				$('.verifications').filter(':not(:checked)').closest(".form-group").find("textarea").valid();
				updateButtonDisabled();
			});
			
			$('[name="outlier"]').on("change", function(){
				if($(this).is(":checked")){
					$(this).closest(".form-group").find('[name="outlierRemark"]').removeAttr("Disabled");
				}else{
					$(this).closest(".form-group").find('[name="outlierRemark"]').attr("Disabled", "Disabled");
				}
			});
			
			$(".copy-nPrice").on("click", function(){
				var lastNprice = $("span.lastNPrice").html();
				if(lastNprice != ""){
					$(this).closest(".form-group").find("input[type='text']:not(:disabled)").val(lastNprice).trigger('change');
				}
			});
			
			$(".copy-sPrice").on("click", function(){
				var lastSPrice = $("span.lastSPrice").html();
				if(lastSPrice != ""){
					$(this).closest(".form-group").find("input[type='text']:not(:disabled)").val(lastSPrice).trigger('change');
				}
			});
			
			$('.btn-reason-lookup').priceReasonLookup({
				queryDataCallback: function(dataModel) {
					dataModel.reasonType = 'Price';
					var unitId = $input.data("unitid");
					dataModel.outletTypeId = [];
					dataModel.outletTypeId[0] = unitId;
				},
				resultCallback: function(data) {
					var $input = this.$element.closest('.input-group').find('input:first');
					$input.val(data).trigger('change');
				}
			});
			
			$('[name="btnRevisit"]').on("click", function(){
				var model = $('#revisitDialog').modal('show');
				$("#revisitForm", model).validate({ ignore: [] });
			})
			
			$('[name="btnReallocate"]').on("click", function(){
				var data = "id="+$('[name="indoorQuotationRecordId"]').val();
				bootbox.confirm({
				    title: "Confirmation",
				    message: "<spring:message code='W00025' />",
				    callback: function(result){
				    	if(result === true){
				    		window.location = "<c:url value='/QuotationRecordDataReview/reallocateId'/>?"+data;
				    	}
				    }
				});
			});
			
			$('[name="collectionDateStr"],[name="startDateStr"],[name="endDateStr"]').on('change', function(e) {
				updateRevisitDateMode($(this.form));
			});
			
			$('#revisitForm').validate({
				rules: {
					collectionDateStr: {
						required : {
							depends : function(element){
								var $form = $(element).closest("form");
								return $('[name="collectionDateStr"]', $form).val() == '' && 
									$('[name="startDateStr"]', $form).val() == '' && $('[name="endDateStr"]', $form).val() == '';
							}
						}
					},
					startDateStr:{
						required : {
							depends : function(element){
								var $form = $(element).closest("form");
								return $('[name="collectionDateStr"]', $form).val() == '' && $('[name="startDateStr"]', $form).val() == '';
							}
						}
					},
					endDateStr:{
						required : {
							depends : function(element){
								var $form = $(element).closest("form");
								return $('[name="collectionDateStr"]', $form).val() == '' && $('[name="endDateStr"]', $form).val() == '';
							}
						}
					}
				},
				messages: {
					collectionDateStr: "Some Fields cannot be empty",
					startDateStr: "Some Fields cannot be empty",
					endDateStr: "Some Fields cannot be empty"
				}
			});
			
			$("#revisitForm").submit( function(e){
				if($(this).valid()){
					var form = $(this).serialize();
					bootbox.confirm({
					    title: "Confirmation",
					    message: "<spring:message code='W00027' />",
					    callback: function(result){
					    	if(result === true){
					    		//console.log(data);
					    		window.location = "<c:url value='/QuotationRecordDataReview/revisit'/>?"+form;
					    	}
					    }
					});
				}
				return false;
			});
			
			$('button[value=verification]').click(function(e) {
				if ($('#isIP').val() == 'true') {
					bootbox.alert({
					    title: "Alert",
					    message: "<spring:message code='E00156' />"
					});
					
					e.preventDefault();
					return;
				}
				
				$(this).closest('form').validate().settings.ignore = [];
			});
			$('button[type=submit]').not('[value=verification]').click(function() {
				$(this).closest('form').validate().settings.ignore = '.verifications,.verificationRemarks';
			});
			
			$('[name=verification]').uncheckableRadio();
			
			updateButtonDisabled();
			
			$('#mainForm').changeListener({
				backButtons: ['.btn-back']
			});
			
			
		})
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Quotation Record Data Review</h1>
        </section>
        
        <section class="content">
        	<div class="row">
				<div class="col-md-12" >
				<!-- content -->
					<form class="form-horizontal" action="<c:url value='/QuotationRecordDataReview/save'/>" method="post" role="form" id="mainForm">
						<input id="isIP" type="hidden" value="${isIP ? "true" : "false"}"/>
						<div class="box" >
							<div class="box-header with-border">
								<div class="col-md-2">
									<a class="btn btn-default btn-back" href="<c:url value="/QuotationRecordDataReview/selectSurveyType"/>?purposeId=${purpose.getId()}&referenceMonthStr=${refMonthStr}">Back</a>
								</div>
								<div class="col-md-7 quotation-id-container">
									<c:if test="${indoorQuotationRecord.RUA}">
										<span style="color: #ff0000">This is RUA case.</span>
									</c:if>	
									<span>Quotation ID: ${quotation.id}</span>
									<span>Quotation Record ID: ${indoorQuotationRecord.quotationRecord.id}</span>
									<span>Indoor Quotation Record ID: ${indoorQuotationRecord.id}</span>
								</div>
							</div>
							<div class="box-body">
								<div class="row">
					        		<div class="col-md-12">
					        			<!-- general form elements -->
					        			<c:set var="show" scope="page" value="false"/>
					        			<c:if test="${(model.pointToNote != null && model.pointToNote.trim() != '') ||
					        					 (model.verificationRemark != null && model.verificationRemark.trim() != '') ||
					        					 (model.quotationRecord.status == 'Rejected' && model.rejectReason != null && model.rejectReason.trim() != '') ||
					        					 (model.peCheckRemark != null && model.peCheckRemark.trim() != '')}">
					        				<c:set var="show" scope="page" value="true"/>
					        			</c:if>
					        			<div class="box box-primary <c:if test='${!show}'>collapsed-box</c:if>">
					        				<div class="box-header with-border">
												<h4>Point to note and Verification Remarks</h4>
												<div class="box-tools pull-right">
													<c:if test='${show}'>
													<i class="fa fa-exclamation-triangle header-mark"></i>
													</c:if>
					        						<button class="btn btn-box-tool btn-header-collapse" type="button" data-widget="collapse"><c:if test='${!show}'><i class="fa fa-plus"></i></c:if><c:if test='${show}'><i class="fa fa-minus"></i></c:if></button>
					        					</div>
												<%-- hide Point to note row when empty, show ! after collapse if have content
					        					<div class="box-tools pull-right">
					        						<button class="btn btn-box-tool" type="button" data-widget="collapse"><i class="fa fa-minus"></i></button>
					        					</div>
					        					--%>
											</div>
					       					<div class="box-body" <c:if test='${!show}'>style="display: none"</c:if>>
					       						<div class="form-horizontal">
				       								<div class="form-group">
					       								<label class="col-md-1 control-label">Point to note:</label>
					       								<div class="col-md-2">
															<p class="form-control-static">${model.pointToNote}</p>
														</div>
					       								<label class="col-md-1 control-label">Verification Remark:</label>
					       								<div class="col-md-2">
															<p class="form-control-static">${model.verificationRemark}</p>
														</div>
														<c:if test="${model.quotationRecord.status == 'Rejected'}">
					       								<label class="col-md-1 control-label">Reject Reason:</label>
					       								<div class="col-md-2">
															<p class="form-control-static">${model.rejectReason}</p>
														</div>
														</c:if>
					       								<label class="col-md-1 control-label">PE Check Remark:</label>
					       								<div class="col-md-2">
															<p class="form-control-static">${model.peCheckRemark}</p>
														</div>
													</div>
												</div>
											</div>
					        			</div>
					        		</div>
					        	</div>
			        			<%@include file="/WEB-INF/views/shared/indoorQuotationRecord/partial/outlet.jsp" %>
			       				<%@include file="/WEB-INF/views/shared/indoorQuotationRecord/partial/product.jsp" %>
			       				<%@include file="/WEB-INF/views/shared/indoorQuotationRecord/partial/quotation.jsp" %>
			       				<%@include file="/WEB-INF/views/shared/indoorQuotationRecord/partial/unitCodeDescription.jsp" %>
			       				<%@include file="/WEB-INF/views/shared/indoorQuotationRecord/partial/verification.jsp" %>
			       				<%@include file="/WEB-INF/views/shared/indoorQuotationRecord/partial/outlier.jsp" %>
			       				<%@include file="/WEB-INF/views/shared/indoorQuotationRecord/partial/imputation.jsp" %>
							</div>
							<div class="box-footer with-border">
								<sec:authorize access="hasPermission(#user, 64) or hasPermission(#user, 256) or hasPermission(#user, 1024)">
	        						<button name="btnSubmit" value="submit" type="submit" class="btn btn-info">Submit</button>
	        						<button name="btnSubmit" value="verification" type="submit" class="btn btn-info">Verification</button>
	        						<c:if test="${!indoorQuotationRecord.RUA && quotation.status != 'RUA'}">
										<button name="btnRevisit" value="revisit" type="button" class="btn btn-info">Revisit</button>
									</c:if>	
	        						<button name="btnReallocate" value="reallocate" type="button" class="btn btn-info">Reallocate</button>
	       						</sec:authorize>
							</div>
						</div>	
					</form>
				</div>
			</div>
        </section>
		<div class="modal fade" id="revisitDialog" tabindex="-1" role="dialog" aria-labelledby="dialogLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div  class="clearfix overlay" data-bind="loading:isLoading, visible:isLoading" ></div>
					<form action="<c:url value='/QuotationRecordDataReview/revisit'/>" method="post" id="revisitForm" >
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
							<h4 class="modal-title" id="dialogLabel" data-bind="text:formTitle">Revisit</h4>
						</div>
						<input type="hidden" name="indoorAllocationRecordId" value="${indoorQuotationRecord.id}" required>
						<div class="modal-body form-horizontal">
							<div class="form-group">
								<div class="col-md-4 control-label">Start Date</div>
								<div class="col-md-6">
									<div class="input-group date date-picker">
										<input name="startDateStr" class="form-control revisitRequire" />
										<div class="input-group-addon">
											<i class="fa fa-calendar"></i>
										</div>
									</div>
								</div>
							</div>
							<div class="form-group">
								<div class="col-md-4 control-label">End Date</div>
								<div class="col-md-6">
									<div class="input-group date date-picker">
										<input name="endDateStr" class="form-control revisitRequire" />
										<div class="input-group-addon">
											<i class="fa fa-calendar"></i>
										</div>
									</div>
								</div>
							</div>
							<div class="form-group">
								<div class="col-md-4 control-label">Collection Date</div>
								<div class="col-md-6">
									<div class="input-group date date-picker">
										<input name="collectionDateStr" class="form-control revisitRequire" />
										<div class="input-group-addon">
											<i class="fa fa-calendar"></i>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
							<button type="submit" class="btn btn-primary" data-loading-text="Loading...">Submit</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</jsp:body>
</t:layout>