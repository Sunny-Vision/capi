<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%--
/shared/General/queryUomSelect2
--%>
<div class="form-horizontal quotation-record-form ${quotationRecord.backNo ? 'back-no' : 'original active'}"
	<c:if test="${quotationRecord.reminderForPricingCycleMessage != null}">
		data-reminder-for-pricing-cycle-message="<c:out value="${quotationRecord.reminderForPricingCycleMessage}" />"
	</c:if>
	>
	<%@include file="/WEB-INF/views/shared/quotationRecord/subPriceDialog.jsp"%>
	<div class="quotation-record-main-form">
	<input type="hidden" class="is-back-no" value="<c:out value="${quotationRecord.backNo}"/>"/>
	<input name="${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}quotationRecordId" value="<c:out value="${quotationRecord.id}" />" type="hidden" ${model.readonly ? 'disabled': ''}/>
	<input name="${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}discountRemark" value="<c:out value="${quotationRecord.discountRemark}" />" type="hidden" ${model.readonly ? 'disabled': ''}/>
	<input name="${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}version" value="<c:out value="${quotationRecord.version}" />" type="hidden" ${model.readonly ? 'disabled': ''}/>
	<input name="outletTypeShortCodeCSV" type="hidden" value="<c:out value="${quotationRecord.outletTypeShortCodeCSV}" />" disabled/>
	<input name="provideRemarkForNotAvailableQuotation" type="hidden" value="<c:out value="${originalQuotationRecord.provideRemarkForNotAvailableQuotation}"/>" disabled/>
	<input name="unitId" type="hidden" value="<c:out value="${originalQuotationRecord.quotation.unit.id}"/>" disabled/>
	
	<div class="form-group hide extra-row">
		<div class="col-md-12">
			<button class="btn btn-box-tool" type="button" style="visibility: hidden">
				<i class="fa fa-copy"> Copy Original</i>
			</button>
		</div>
	</div>
	
	<c:if test="${quotationRecord.backNo and not model.readonly}">
	<div class="form-group">
		<div class="col-md-12">
			<button class="btn btn-box-tool btn-copy-original" type="button">
				<i class="fa fa-copy"> Copy Original</i>
			</button>
		</div>
	</div>
	</c:if>
	<div class="form-group ${quotationRecord.NPriceMandatory ? 'required' : ''}">
		<label class="col-md-4 control-label">Final N price</label>
		<div class="col-md-3">
			<input
				name="${model.readonly ? 'readonly.' : ''}${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}nPrice" type="text" class="form-control" value="<c:out value="${quotationRecord.nPrice}" />"
				data-rule-number="true"
				${quotationRecord.SPricePeculiar or not empty quotationRecord.subPrices or not empty quotationRecord.discount ? 'readonly' : ''} ${model.readonly ? 'disabled': ''}/>
		</div>
		<c:if test="${not model.history}">
		<div class="col-md-2">
			<p class="form-control-static">
				(<span class="nPricePercent">${quotationRecord.historyNPricePercent}%</span>)
			</p>
		</div>
		</c:if>
		<c:if test="${not model.readonly}">
		<div class="col-md-3">
			<p class="form-control-static">
			<a href="javascript:;" class="btn-copy-n-to-sprice" style="color: #000000">
				<i class="fa fa-copy"> Copy</i>
			</a>
			</p>
		</div>
		</c:if>
	</div>
	<div class="form-group ${quotationRecord.SPriceMandatory ? 'required' : ''}">
		<label class="col-md-4 control-label">Final S price</label>
		<div class="col-md-3">
			<input
				name="${model.readonly ? 'readonly.' : ''}${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}sPrice" type="text" class="form-control" value="<c:out value="${quotationRecord.sPrice}" />"
				data-rule-number="true"
				${quotationRecord.SPricePeculiar or not empty quotationRecord.subPrices or not empty quotationRecord.discount ? 'readonly' : ''} ${model.readonly ? 'disabled': ''}/>
		</div>
		<c:if test="${not model.history}">
		<div class="col-md-2">
			<p class="form-control-static">
				(<span class="sPricePercent">${quotationRecord.historySPricePercent}%</span>)
			</p>
		</div>
		</c:if>
		<div class="col-md-3">
			<div class="checkbox">
				<label>
					<input
						name="${model.readonly ? 'readonly.' : ''}${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}SPricePeculiar" type="checkbox" value="True" ${quotationRecord.SPricePeculiar ? "checked" : ""} ${model.readonly ? 'disabled': ''}/>
					Not Applicable
				</label>
			</div>
		</div>
	</div>
	<c:if test="${originalQuotationRecord.showUomInput}">
	<div class="form-group">
		<label class="col-md-4 control-label">UOM</label>
		<div class="col-md-3">
			<input
				name="${model.readonly ? 'readonly.' : ''}${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}uomValue" type="text" class="form-control" value="<c:out value="${quotationRecord.uomValue}" />"
				data-rule-number="true"
				${model.readonly ? 'disabled': ''}/>
		</div>
		<div class="col-md-5">
			<select
				name="${model.readonly ? 'readonly.' : ''}${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}uomId" class="form-control" ${model.readonly ? 'disabled': ''}
				data-category-ids="${quotationRecord.uomCategoryIdCSV}"
				data-ajax-url="<c:url value='/shared/General/queryUomSelect2'/>"
				data-allow-clear="true"
				data-placeholder="">
				<c:if test="${quotationRecord.uom != null}">
					<option value="<c:out value="${quotationRecord.uom.id}" />" selected>${quotationRecord.uom.chineseName}</option>
				</c:if>
			</select>
		</div>
	</div>
	</c:if>
	
	<div class="form-group">
		<label class="col-md-4 control-label">Sub-Price
			<c:if test="${quotationRecord.subPriceTypeId != null || not empty quotationRecord.subPrices}">
				<i class="fa fa-star"></i>
			</c:if>
		</label>
		<div class="col-md-2">
			<p class="form-control-static">
				<a href="#" style="color: #000" class="sub-price-lookup" ${model.readonly ? 'disabled': ''}><i class="fa fa-search"></i></a>
			</p>
		</div>
		<label class="col-md-2 control-label">Discount</label>
		<div class="col-md-4 btn-show-discount-calculator" ${model.readonly ? 'disabled': ''}>
			<div class="input-group">
				<input
					type="text" name="${model.readonly ? 'readonly.' : ''}${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}discount" class="form-control" value="<c:out value="${quotationRecord.discount}" />" readonly ${model.readonly ? 'disabled': ''}/>
				
				<div class="input-group-addon text-green discount-found-msg hide" title="Formula found">
					<i class="fa fa-check"></i>
				</div>
				<div class="input-group-addon text-red discount-not-found-msg hide" title="Formula not found">
					<i class="fa fa-times"></i>
				</div>
				<div class="input-group-addon">
					<i class="fa fa-search"></i>
				</div>
			</div>
		</div>
	</div>
	
	<c:if test="${!quotationRecord.backNo && originalQuotationRecord.FRRequired }">
		<c:if test="${originalQuotationRecord.collectFR}">
			<div class="form-group frFields" <c:if test="${originalQuotationRecord.useFRAdmin && originalQuotationRecord.collectFR && !quotationRecord.consignmentCounter }">style="display:none"</c:if>>
				<label class="col-md-4 control-label">FR</label>
				<div class="col-md-3">
					<input
						name="${model.readonly ? 'readonly.' : ''}${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}fr" type="text" class="form-control" value="<c:out value="${quotationRecord.fr}" />" ${model.readonly ? 'disabled': ''}/>
				</div>
				<div class="col-md-3">
					<label class="radio-inline">
						<input
							type="radio" name="${model.history ? 'history.' : ''}${model.readonly ? 'readonly.' : ''}${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}FRPercentage" value="true" ${quotationRecord.FRPercentage ? "checked" : ""} ${model.readonly ? 'disabled': ''}>
						%
					</label>
					<label class="radio-inline">
						<input
							type="radio" name="${model.history ? 'history.' : ''}${model.readonly ? 'readonly.' : ''}${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}FRPercentage" value="false" ${!quotationRecord.FRPercentage ? "checked" : ""} ${model.readonly ? 'disabled': ''}>
						$
					</label>
				</div>
			</div>
		</c:if>
		
		<c:if test="${(originalQuotationRecord.collectFR && !originalQuotationRecord.useFRAdmin) || originalQuotationRecord.useFRAdmin}">
			<div class="form-group">
				<label class="col-md-7 control-label">Consignment Counter</label>
				<input type="hidden" value="<c:out value="${originalQuotationRecord.useFRAdmin? '1':'0'}"/>" class="useFRAdmin" />
				<div class="col-md-1">
					<div class="checkbox">
						<label><input
							name="${model.readonly ? 'readonly.' : ''}${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}consignmentCounter" type="checkbox" value="True" ${quotationRecord.consignmentCounter ? "checked" : ""} ${model.readonly ? 'disabled': ''}/></label>
					</div>
				</div>
				<div class="col-md-4">
					<input type="text" class="form-control"
						name="${model.readonly ? 'readonly.' : ''}${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}consignmentCounterName"
						value="<c:out value="${quotationRecord.consignmentCounterName}" />"
						${quotationRecord.consignmentCounter ? '' : 'readonly'} ${model.readonly ? 'disabled': ''} />
				</div>
			</div>
			<div class="form-group consignmentCounterRemark-container">
				<label class="col-md-7 control-label">Consignment Counter Remark</label>
				<div class="col-md-5">
					<input
						name="${model.readonly ? 'readonly.' : ''}${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}consignmentCounterRemark" type="text" class="form-control" value="<c:out value="${quotationRecord.consignmentCounterRemark}" />"
						${quotationRecord.consignmentCounter ? '' : 'readonly'} ${model.readonly ? 'disabled': ''}/>
				</div>
			</div>
		</c:if>
	</c:if>
	<div class="form-group">
		<label class="col-md-4 control-label">Reason</label>
		<div class="col-md-8">
			<c:choose>
			<c:when test="${not model.readonly}">
			<div class="input-group">
				<input
					name="${model.readonly ? 'readonly.' : ''}${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}reason" type="text" class="form-control" value="<c:out value="${quotationRecord.reason}" />" ${model.readonly ? 'disabled': ''}/>
				<div class="input-group-addon btn-reason-lookup">
					<i class="fa fa-search"></i>
				</div>
			</div>
			</c:when>
			<c:otherwise>
				<p class="form-control-static break-text" name="${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}reason">${quotationRecord.reason}</p>
			</c:otherwise>
			</c:choose>
		</div>
	</div>
	<div class="form-group">
		<label class="col-md-4 control-label">Price Remark</label>
		<div class="col-md-8">
			<c:choose>
			<c:when test="${not model.readonly}">
				<input
					name="${model.readonly ? 'readonly.' : ''}${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}remark" type="text" class="form-control" value="<c:out value="${quotationRecord.remark}" />" ${model.readonly ? 'disabled': ''}/>
			</c:when>
			<c:otherwise>
				<p class="form-control-static break-text" name="${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}remark">${quotationRecord.remark}</p>
			</c:otherwise>
			</c:choose>
		</div>
	</div>
	<div class="form-group">
		<label class="col-md-4 control-label">Availability</label>
		<input type="hidden" name="${model.readonly ? 'readonly.' : ''}${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}availability"
			value="${quotationRecord.availability == null ? 1 : quotationRecord.availability}" ${model.readonly ? 'disabled': ''} />
		<div class="col-md-6">
			<select
				class="form-control availability-select"
				${quotationRecord.availability == 2 ? 'disabled' : ''}
				${model.readonly ? 'disabled': ''}>
				
			<c:if test="${not empty quotationRecord.quotationRecordId or quotationRecord.backNo}">
				
				<c:choose>
				<c:when test="${quotationRecord.newRecruitment} && ${not quotationRecord.backNo}">
				<option value="1">Available</option>
				</c:when>
				
				<c:otherwise>
				<option value="1"
					${quotationRecord.availability == 1 ||
					quotationRecord.availability == 3 && !(!quotationRecord.SPricePeculiar && quotationRecord.sPrice == null && quotationRecord.nPrice == null) ? "selected" : ""}>Available</option>
<!--				Hide "有價無貨"(3) in "Availability" and set the availability(if availability = 3 still existing) as following:  -->				
<!--				1. availability = "有價無貨" and Nprice is null and Sprice is null and IsSPricePeculiar = 0 => availability = 4 -->
<!-- 				2. availability = "有價無貨" and others => availability = 1 -->
<%-- 				<c:if test="${not quotationRecord.backNo}"> --%>
<!-- 				<option value="3" -->
<%-- 					${quotationRecord.availability == 3 ? "selected" : ""}>有價無貨</option> --%>
<%-- 				</c:if> --%>
				<option value="4"
					${quotationRecord.availability == 4 ||
					(quotationRecord.availability == 3 && !quotationRecord.SPricePeculiar && quotationRecord.sPrice == null && quotationRecord.nPrice == null) ? "selected" : ""}>缺貨</option>
				
				<c:if test="${not quotationRecord.backNo}">
					<c:if test="${originalQuotationRecord.quotation.unit.ruaAllowed}">
					<option value="5"
						${quotationRecord.availability == 5 ? "selected" : ""}>Not Suitable</option>
					</c:if>
				<option value="6"
					${quotationRecord.availability == 6 ? "selected" : ""}>回倉</option>
				<option value="8"
					${quotationRecord.availability == 8 ? "selected" : ""}>未返</option>
				</c:if>
				
				<c:if test="${quotationRecord.backNo}">
				<option value="9"
					${quotationRecord.availability == 9 ? "selected" : ""}>New</option>
				</c:if>
				</c:otherwise>
				</c:choose>
				
			</c:if>
			
			</select>
		</div>
		
		<c:if test="${not quotationRecord.backNo && originalQuotationRecord.quotationState != 'Verify'}">
		<div class="col-md-2">
			<div class="checkbox">
				<label>
					<input
						type="checkbox" class="availability-checkbox" value="2"
						${quotationRecord.availability == 2 ? 'checked' : ''}
						${model.readonly ? 'disabled': ''} />
						IP
				</label>
			</div>
		</div>
		</c:if>
	</div>
	
	<div class="form-group">
		<label class="col-md-4 control-label">Firm Status</label>
		<div class="col-md-2">
			<input class="firm-status" type="hidden" value="<c:out value="${originalQuotationRecord.assignment.status}"/>" />
			<p class="form-control-static">
				<c:choose>
					<c:when test="${originalQuotationRecord.assignment.status == 1}">Enumeration (EN)</c:when>
					<c:when test="${originalQuotationRecord.assignment.status == 2}">Closed (CL)</c:when>
					<c:when test="${originalQuotationRecord.assignment.status == 3}">Move (MV)</c:when>
					<c:when test="${originalQuotationRecord.assignment.status == 4}">Not Suitable (NS)</c:when>
					<c:when test="${originalQuotationRecord.assignment.status == 5}">Refusal (NR)</c:when>
					<c:when test="${originalQuotationRecord.assignment.status == 6}">Wrong Outlet (WO)</c:when>
					<c:when test="${originalQuotationRecord.assignment.status == 7}">Door Lock (DL)</c:when>
					<c:when test="${originalQuotationRecord.assignment.status == 8}">Non-contact (NC)</c:when>
					<c:when test="${originalQuotationRecord.assignment.status == 9}">In Progress (IP)</c:when>
					<c:when test="${originalQuotationRecord.assignment.status == 10}">Duplication (DU)</c:when>
				</c:choose>
			</p>
		</div>
		<label class="col-md-2 control-label">Field Officer</label>
		<c:set var="fieldOffice" value="${originalQuotationRecord.user}" scope="request"/>
		<label class="col-md-4 form-control-static">${fieldOffice.staffCode} - ${fieldOffice.chineseName} ( ${fieldOffice.destination} )</label>
	</div>
	
	<div class="form-group">
		<label class="col-md-4 control-label">Product remark</label>
		<div class="col-md-8">
			<p class="form-control-static break-text"><c:out value="${model.quotationRecord.productRemark}"/></p>
		</div>
	</div>
	<div class="form-group">
		<label class="col-md-4 control-label">Product position</label>
		<div class="col-md-8">
			<p class="form-control-static break-text"><c:out value="${model.quotationRecord.productPosition}" /></p>
		</div>
	</div>
	
	<c:if test="${not quotationRecord.backNo}">
		<c:if test="${originalQuotationRecord.quotationState == 'Verify'}">
		<div class="form-group">
			<label class="col-md-4 control-label">Completed Verification</label>
			<div class="col-md-8">
				<div class="checkbox">
					<label>
						<input name="${model.readonly ? 'readonly.' : ''}${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}visited" type="checkbox" value="true"
							${quotationRecord.visited ? 'checked' : ''}
							${model.readonly ? 'disabled': ''} />
					</label>
				</div>
			</div>
		</div>
		<div class="form-group">
			<label class="col-md-4 control-label">Verify Reply</label>
			<div class="col-md-8">
				<textarea name="${model.readonly ? 'readonly.' : ''}${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}verificationReply"
					class="form-control" style="height:100px" ${model.readonly ? 'disabled': ''}><c:out value="${quotationRecord.verificationReply}"/></textarea>
			</div>
		</div>
		</c:if>
		<c:if test="${originalQuotationRecord.quotationState == 'Revisit'}">
		<div class="form-group">
			<label class="col-md-4 control-label">Revisit Complete</label>
			<div class="col-md-8">
				<div class="checkbox">
					<label>
						<input name="${model.readonly ? 'readonly.' : ''}${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}visited" type="checkbox" value="true"
							${quotationRecord.visited ? 'checked' : ''}
							${model.readonly ? 'disabled': ''} />
					</label>
				</div>
			</div>
		</div>
		</c:if>
	</c:if>
	
	<div class="form-group">
		<label class="col-md-4 control-label">Category Remark</label>
		<div class="col-md-8">
			<p class="form-control-static break-text">${quotationRecord.assignmentCategoryRemark}</p>
		</div>
	</div>
	<div class="form-group">
		<label class="col-md-4 control-label">Outlet Remark</label>
		<div class="col-md-8">
			<p class="form-control-static break-text">${originalQuotationRecord.outletDiscountRemark}</p>
		</div>
	</div>
	
	<c:if test="${model.history}">
	<div class="form-group">
		<label class="col-md-4 control-label">PE Remark</label>
		<div class="col-md-8">
			<c:if test="${quotationRecord.peCheckRemark != null}">
			<p class="form-control-static">
				<a href="#" style="color: #000" class="text-viewer"><i class="fa fa-search"></i></a>
				<textarea class="hide"><c:out value="${quotationRecord.peCheckRemark}"/></textarea>
			</p>
			</c:if>
		</div>
	</div>
	</c:if>
	
	<div class="form-group">
		<label class="col-md-4 control-label">Contact Person</label>
		<div class="col-md-8">
			<p class="form-control-static">${quotationRecord.assignmentContactPerson}</p>
		</div>
	</div>
	
	<c:if test="${model.history}">
	<div class="form-group">
		<div class="col-md-8 col-md-offset-4">
			<button type="button" class="btn btn-default compareSubPrice">Compare Sub-price</button>
		</div>
	</div>
	</c:if>
	</div>
</div>