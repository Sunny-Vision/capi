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

<div class="row">
	<div class="col-md-6">
		<div class="quotation-current-container">
			<%@include file="/WEB-INF/views/shared/indoorQuotationRecord/partial/quotationRecord/editForm.jsp"%>
		</div>
	</div>
	<div class="col-md-6 quotation-history-container">
		<div class="nav-tabs-custom">
			<ul class="nav nav-tabs">
				<c:forEach items="${model.histories}" var="history" varStatus="historyLoop">    
				<li class="history-date ${historyLoop.index == 0 ? 'active' : ''}" data-id="${history.id}">
					<a href="javascript:;" style="${history.differentProduct ? 'color:#ff0000; min-height:52px;' : ''}">${history.date}</a>
				</li>
				</c:forEach>
				<c:if test="${model.histories.size() > 0}">
					<button type="button" class="btn btn-default pull-right quotationRecordHistory" >More...</button>
				</c:if>
			</ul>
			<ul class="nav nav-pills">
			</ul>
			<c:if test="${model.backTracks.size() > 0 }">
			<ul class="nav nav-tabs">
				<c:forEach items="${model.backTracks}" var="backTrack" varStatus="backTrackLoop">   
				<li class="history-date " data-id="${backTrack.quotationRecordId}">
					<a href="javascript:;" style="color:#0000ff; min-height:52px;">${backTrack.date}</a>
				</li>
				</c:forEach>
			</ul>
			<ul class="nav nav-pills">
			</ul>
			</c:if>
			<div class="tab-content" style="padding: 10px 0 0 0">
				<div class="tab-pane active"></div>
				<!-- /.tab-pane -->
			</div>
			<!-- /.tab-content -->
		</div>
	</div>
</div>