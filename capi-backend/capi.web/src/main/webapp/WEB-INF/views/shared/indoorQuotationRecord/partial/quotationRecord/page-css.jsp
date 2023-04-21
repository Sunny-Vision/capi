<%@include file="/WEB-INF/views/includes/commonDialog/discountCalculator-css.jsp" %>
<%@include file="/WEB-INF/views/includes/commonDialog/priceReasonLookup-css.jsp" %>
<%@include file="/WEB-INF/views/includes/commonDialog/discountFormulaLookup-css.jsp"%>
<style>
.quotation-record-form {
	display: none;
}
.quotation-record-form.active {
	display: block;
}

.btn-copy {
	position: absolute;
}
.btn-copy i {
	display: inline-block;
	width: 25px;
	height: 25px;
	margin-top: 5px;
	margin-left: 10px;
	font-size: 25px;
	position: absolute;
	z-index: 1;
	cursor: pointer;
}
.break-text {
	word-wrap: break-word;
}
</style>