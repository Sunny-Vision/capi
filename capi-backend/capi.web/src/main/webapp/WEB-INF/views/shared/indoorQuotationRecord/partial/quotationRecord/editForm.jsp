<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div class="${model.history ? '' : 'box box-primary'} ${model.readonly ? 'readonly-' : ''}quotation-record-form-container">
	<c:if test="${not model.history}">
	<div class="box-header with-border">
		<div class="form-inline">
			<div class="">
				<label class="control-label">Reference Date:</label>
				<label class="" style="padding-right: 10px;"><fmt:formatDate pattern="dd-MM-yyyy"  value="${model.quotationRecord.referenceDate}" /></label>
				&nbsp;
				<label class="control-label">Collection Date</label>
				<div class="input-group" style="width: 150px">
					<input type="text" class="form-control date-picker"
						style="height:30px"
						data-orientation="top" name="quotationRecord.collectionDate" value="<fmt:formatDate pattern="dd-MM-yyyy"  value="${model.quotationRecord.collectionDate}" />"
						data-date-end-date="<fmt:formatDate pattern="dd-MM-yyyy"  value="${model.quotationRecord.assignedCollectionDate != null ? model.quotationRecord.assignedCollectionDate : model.quotationRecord.assignedStartDate}" />" ${model.readonly ? 'disabled': ''} />
					<div class="input-group-addon">
						<i class="fa fa-calendar"></i>
					</div>
				</div>
			</div>
		</div>
	</div>
	</c:if>
	<div class="box-body">
		<input name="quotationRecord.productChange" value="<c:out value="${model.quotationRecord.productChange}"/>" type="hidden" />
		<input name="quotationRecord.formDisplay" value="<c:out value="${model.quotationRecord.formDisplay}"/>" type="hidden" disabled />
		<input name="quotationRecord.backdateRequired" value="<c:out value="${model.quotationRecord.quotation.unit.backdateRequired}"/>" type="hidden" disabled />
		
		<c:set var="originalQuotationRecord" value="${model.quotationRecord}" scope="request" />
		<c:set var="quotationRecord" value="${model.quotationRecord}" scope="request" />
		<c:choose>
		<c:when test="${quotationRecord.formDisplay == 1}">
			<%@include file="/WEB-INF/views/shared/indoorQuotationRecord/partial/quotationRecord/editQuotationRecordForm.jsp"%>
			
			<c:set var="quotationRecord" value="${model.backNoQuotationRecord}" scope="request" />
			<%@include file="/WEB-INF/views/shared/indoorQuotationRecord/partial/quotationRecord/editQuotationRecordForm.jsp"%>
		</c:when>
		<c:otherwise>
			<%@include file="/WEB-INF/views/shared/indoorQuotationRecord/partial/quotationRecord/editQuotationRecordTourForm.jsp"%>
		</c:otherwise>
		</c:choose>
		
	</div>
	<div class="box box-default btn-back-no-container" style="display: none">
		<div class="box-footer text-center">
			<button type="button" class="btn btn-default btn-original active">Original</button>
			<button type="button" class="btn btn-default btn-back-no">Back. No</button>
		</div>
	</div>
</div>