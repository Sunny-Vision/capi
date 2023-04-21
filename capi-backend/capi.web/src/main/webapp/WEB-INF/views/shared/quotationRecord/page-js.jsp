<%@include file="/WEB-INF/views/includes/commonDialog/discountCalculator.jsp"%>
<%@include file="/WEB-INF/views/includes/commonDialog/changeProductDialog.jsp"%>
<%@include file="/WEB-INF/views/includes/commonDialog/compareProductDialog.jsp"%>
<%@include file="/WEB-INF/views/includes/commonDialog/priceReasonLookup.jsp"%>
<%@include file="/WEB-INF/views/includes/commonDialog/discountFormulaLookup.jsp"%>
<%@include file="/WEB-INF/views/shared/quotationRecord/subPriceDialog-js.jsp" %>
<%@include file="/WEB-INF/views/shared/quotationRecord/productForm-js.jsp" %>
<%@include file="/WEB-INF/views/shared/quotationRecord/quotationRecordForm-js.jsp" %>
<%@include file="/WEB-INF/views/shared/quotationRecord/quotationRecordHistory-js.jsp" %>
<script>
$(function() {
	var getHistoryQuotationRecordUrl = "<c:url value='/shared/General/getHistoryQuotationRecordUrl'/>";
	
	var $quotationCurrentContainer = $('.quotation-current-container');
	var $currentQuotationRecordForm = $('.quotation-record-form-container,.readonly-quotation-record-form-container', $quotationCurrentContainer);
	
	$('.product-form').productForm({
		quotationRecordId: +$('[name="quotationRecord.quotationRecordId"]', $currentQuotationRecordForm).val(),
		$quotationCurrentContainer: $quotationCurrentContainer,
		historyQuotationRecordIdCallback: function() {
			if ($('.quotation-history-container .history-date.active').data('id') == null) return 0;
			return +$('.quotation-history-container .history-date.active').data('id');
		},
		productChangeCallback: function(changeModel) {
			$currentQuotationRecordForm.quotationRecordFormContainer('setProductChange', changeModel.productChange);
		}
	});
	
	var $currentQuotationRecordFormContainer = $('.quotation-record-form-container,.readonly-quotation-record-form-container').quotationRecordFormContainer({
		$quotationHistoryContainer: $('.quotation-history-container')
	});
	
	$('.btn-outlet-collapse').click(function() {
		var $btn = $(this);
		var $box = $btn.closest('.box');
		if ($btn.find('.fa-plus').length > 0)
			$box.find('.outlet-header-name').hide();
		else
			$box.find('.outlet-header-name').show();
	});
	
	if ($.fn.outletAttachmentDialog != null) {
		$('.btn-attachment-lookup-readonly').outletAttachmentDialog();
	}
	
	$('.quotation-history-container').quotationRecordHistory({
		$currentQuotationRecordFormContainer: $currentQuotationRecordFormContainer
	});
});
</script>