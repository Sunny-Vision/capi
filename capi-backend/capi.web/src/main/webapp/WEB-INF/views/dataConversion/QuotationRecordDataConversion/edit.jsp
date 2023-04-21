<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/timepicker-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
		<%@include file="/WEB-INF/views/shared/indoorQuotationRecord/partial/quotationRecord/page-css.jsp" %>
		<style>
		.box.box-primary{
			margin-bottom: 0px;
		}
		</style>
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
		$(document).ready(function(){
			
			function updateButtonDisabled() {
				$('[name="btnSubmit"]').prop('disabled', false);
				if ($('.verifications:checked').length > 0) {
					$('[name="btnSubmit"]').filter('[value="submit"],[value="submitNext"]').prop('disabled', true);
				}
			}
			
			$("#mainForm").validate({ ignore: [] });
			
			$('.quotation-history-container').on('historyChanged', function() {
				
					var quotationRecordId = $(".history-date.active", $(this)).data("id");
					$.ajax({
						  type: "POST",
						  url: "<c:url value='/dataConversion/QuotationRecordDataConversion/getProductPartial'/>",
						  data: {quotationRecordId: quotationRecordId},
						  success: function(result){
							  $(".productHistory").html(result);
						  },
						});
					$.ajax({
						  type: "POST",
						  url: "<c:url value='/dataConversion/QuotationRecordDataConversion/getOutletPartial'/>",
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
			
			$('[name="indoorQuotationRecord.isOutlier"]').on("change", function(){
				if($(this).is(":checked")){
					$(this).closest(".form-group").find('[name="indoorQuotationRecord.outlierRemark"]').removeAttr("Disabled");
				}else{
					$(this).closest(".form-group").find('[name="indoorQuotationRecord.outlierRemark"]').attr("Disabled", "Disabled");
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
			
			$('button[value=verification]').click(function() {
				$(this).closest('form').validate().settings.ignore = [];
			});
			$('button[type=submit]').not('[value=verification]').click(function() {
				$(this).closest('form').validate().settings.ignore = '.verifications,.verificationRemarks';
			});
			
			$('[name=verification]').uncheckableRadio();

			<sec:authorize access="!(hasPermission(#user, 128) or hasPermission(#user, 256))">
				viewOnly();
			</sec:authorize>
			
			updateButtonDisabled();
			
			$('#mainForm').changeListener({
				backButtons: ['.btn-back']
			});
		});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Quotation Record Data Conversion</h1>
        </section>
        
        <section class="content">
        	<div class="row">
				<div class="col-md-12" >
				<!-- content -->
					<form class="form-horizontal" action="<c:url value='/dataConversion/QuotationRecordDataConversion/save'/>" method="post" role="form" id="mainForm">
						<div class="box" >
							<div class="box-header with-border">
								<div class="col-md-2">
								<a class="btn btn-default btn-back" href="<c:url value="/dataConversion/QuotationRecordDataConversion/selectSurveyType"/>?purposeId=${purpose.getId()}&referenceMonthStr=${refMonthStr}">Back</a>
								</div>
								<div class="col-md-7 quotation-id-container">
									<span>Quotation ID: ${quotation.id}</span>
									<span>Quotation Record ID: ${indoorQuotationRecord.quotationRecord.id}</span>
									<span>Indoor Quotation Record ID: ${indoorQuotationRecord.id}</span>
								</div>
								<div class="col-md-3 custom-page-nav pull-right" style="text-align: right">
									<c:choose>
									<c:when test="${session.getPreviousId(indoorQuotationRecord.id) != null}">
									<a class="btn btn-default"
										href="<c:url value='/dataConversion/QuotationRecordDataConversion/edit'/>?id=${session.getPreviousId(indoorQuotationRecord.id)}"><i class="fa fa-caret-left"></i></a>
									</c:when>
									<c:otherwise>
									<a class="btn btn-default" href="#" disabled><i class="fa fa-caret-left"></i></a>
									</c:otherwise>
									</c:choose>
									
									<span>${session.getIndex(indoorQuotationRecord.id)}</span>
									<span>/</span>
									<span>${session.getCount()}</span>
									
									<c:choose>
									<c:when test="${session.getNextId(indoorQuotationRecord.id) != null}">
									<a class="btn btn-default"
										href="<c:url value='/dataConversion/QuotationRecordDataConversion/edit'/>?id=${session.getNextId(indoorQuotationRecord.id)}"><i class="fa fa-caret-right"></i></a>
									</c:when>
									<c:otherwise>
									<a class="btn btn-default" href="#" disabled><i class="fa fa-caret-right"></i></a>
									</c:otherwise>
									</c:choose>
								</div>
							</div>
							<div class="box-body">
								<div class="row">
					        		<div class="col-md-12">
					        			<!-- general form elements -->
					        			<c:set var="show" scope="page" value="false"/>
					        			<c:if test="${(model.pointToNote != null && model.pointToNote != '') ||
					        					 (model.verificationRemark != null && model.verificationRemark != '') ||
					        					 (model.quotationRecord.status == 'Rejected' && model.rejectReason != null && model.rejectReason.trim() != '') ||
					        					 (model.peCheckRemark != null && model.peCheckRemark != '') ||
					        					 (indoorQuotationRecord.status == 'Reject Verification')}">
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
														<c:if test="${indoorQuotationRecord.status == 'Reject Verification'}">
					       								<label class="col-md-1 control-label text-red">Reject Reason:</label>
					       								<div class="col-md-2">
															<p class="form-control-static text-red">${indoorQuotationRecord.rejectReason}</p>
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
							</div>
							<div class="box-footer with-border">
								<sec:authorize access="hasPermission(#user, 128) or hasPermission(#user, 256)">
	        						<button name="btnSubmit" value="save" type="submit" class="btn btn-info">Save</button>
	        						<button name="btnSubmit" value="saveNext" type="submit" class="btn btn-info">Save Next</button>
	        						<button name="btnSubmit" value="submit" type="submit" class="btn btn-info">Submit</button>
	        						<button name="btnSubmit" value="submitNext" type="submit" class="btn btn-info">Submit Next</button>
	        						<button name="btnSubmit" value="verification" type="submit" class="btn btn-info">Verification</button>
	       						</sec:authorize>
							</div>
						</div>	
					</form>
				</div>
			</div>
        </section>		
	</jsp:body>
</t:layout>
