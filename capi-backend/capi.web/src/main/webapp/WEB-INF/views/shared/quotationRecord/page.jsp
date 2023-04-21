<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%--
/shared/General/getOutletImage
/shared/General/getHistoryQuotationRecordUrl
/shared/General/queryUomSelect2
/shared/General/getProductHasPhoto
/shared/General/getProductImage
/shared/General/querySubPriceTypeSelect2
/shared/General/getSubPriceFieldByType
/shared/General/getAllEnabledFormula
/shared/General/getSubPriceType
/commonDialog/DiscountFormulaLookup
/commonDialog/DiscountCalculator
--%>
<c:if test="${not model.hideOutlet}">
	<c:set var="outlet" value="${model.outlet}" scope="request" />
	<%@include file="/WEB-INF/views/shared/quotationRecord/outlet.jsp"%>
</c:if>

<div class="row">
	<div class="col-md-2">
		<c:set var="product" value="${model.product}" scope="request" />
		<%@include file="/WEB-INF/views/shared/quotationRecord/product.jsp"%>
	</div>
	<div class="col-md-5">
		<div class="row quotation-current-container">
			<%@include file="/WEB-INF/views/shared/quotationRecord/editForm.jsp"%>
		</div>
	</div>
	<div class="col-md-5 quotation-history-container">
		<div class="nav-tabs-custom">
			<ul class="nav nav-tabs">
				<c:forEach items="${model.histories}" var="history" varStatus="historyLoop">    
				<li class="history-date ${historyLoop.index == 0 ? 'active' : ''}" data-id="${history.id}" data-isHiddenFieldOfficer="${history.isHiddenFieldOfficer}"><a href="javascript:;" style="${history.differentProduct ? 'color:#ff0000' : ''}">${history.date}</a></li>
				</c:forEach>
			</ul>
			<div class="tab-content" style="padding: 10px 0 0 0">
				<div class="tab-pane active"></div>
				<!-- /.tab-pane -->
			</div>
			<!-- /.tab-content -->
		</div>
	</div>
</div>