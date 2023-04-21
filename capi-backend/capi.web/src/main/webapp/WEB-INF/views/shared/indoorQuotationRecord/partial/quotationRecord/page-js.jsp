<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@include file="/WEB-INF/views/includes/commonDialog/discountCalculator.jsp"%>
<%@include file="/WEB-INF/views/includes/commonDialog/changeProductDialog.jsp"%>
<%@include file="/WEB-INF/views/includes/commonDialog/compareProductDialog.jsp"%>
<%@include file="/WEB-INF/views/includes/commonDialog/priceReasonLookup.jsp"%>
<%@include file="/WEB-INF/views/includes/commonDialog/discountFormulaLookup.jsp"%>
<%@include file="/WEB-INF/views/includes/commonDialog/quotationRecordHistoryDialog.jsp" %>
<%@include file="/WEB-INF/views/shared/quotationRecord/subPriceDialog-js.jsp" %>
<%@include file="/WEB-INF/views/shared/quotationRecord/productForm-js.jsp" %>
<%@include file="/WEB-INF/views/shared/quotationRecord/quotationRecordForm-js.jsp" %>
<%@include file="/WEB-INF/views/shared/quotationRecord/quotationRecordHistory-js.jsp" %>
<script>
$(function() {
	var getHistoryQuotationRecordUrl = "<c:url value='/dataConversion/QuotationRecordDataConversion/getHistoryQuotationRecord'/>";
	
	var $quotationCurrentContainer = $('.quotation-current-container');
	var $currentQuotationRecordForm = $('.quotation-record-form-container,.readonly-quotation-record-form-container', $quotationCurrentContainer);
	
	$('.quotation-record-form-container,.readonly-quotation-record-form-container').quotationRecordFormContainer({
		$quotationHistoryContainer: $('.quotation-history-container')
	});
	
	$('.quotation-history-container').quotationRecordHistory({
		$currentQuotationRecordFormContainer: $('.quotation-record-form-container'),
		getHistoryQuotationRecordUrl: getHistoryQuotationRecordUrl
	});
	
	$('.compareSubPrice').on("click", function(){
		var currentQuotationRecordId = $('[name="quotationRecord.quotationRecordId"]',".quotation-current-container .quotation-record-main-form ").val();
		var historyQuotationRecordId = $('[name="quotationRecord.quotationRecordId"]',".quotation-history-container .quotation-record-main-form ").val();
		
		if(typeof currentQuotationRecordId != "undefined" && typeof currentQuotationRecordId != "undefined"){
			window.open('<c:url value="/commonDialog/SubPriceComparison/home"/>?quotationRecordId1='+currentQuotationRecordId+'&quotationRecordId2='+historyQuotationRecordId, '_blank');
		}
	})
	
	$('.quotationRecordHistory').QuotationRecordHistoryDialog({
		popupUrl: "<c:url value='/commonDialog/QuotationRecordHistoryDialog/home'/>?indoorQuotationRecordId="+$('[name="indoorQuotationRecordId"]').val(),
		queryUrl: "<c:url value='/commonDialog/QuotationRecordHistoryDialog/query'/>?indoorQuotationRecordId="+$('[name="indoorQuotationRecordId"]').val(),
	})
});
</script>