<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="form-horizontal quotation-record-form ${quotationRecord.backNo ? 'back-no' : 'original active'}" ${model.readonly ? 'disabled': ''}>
	<%@include file="/WEB-INF/views/shared/quotationRecord/subPriceDialog.jsp"%>
	<div class="quotation-record-main-form">
	<input type="hidden" class="is-back-no" value="<c:out value="${quotationRecord.backNo}"/>"/>
	<input name="${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}quotationRecordId" value="<c:out value="${quotationRecord.id}" />" type="hidden" ${model.readonly ? 'disabled': ''}/>
	<input name="${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}version" value="<c:out value="${quotationRecord.version}" />" type="hidden" ${model.readonly ? 'disabled': ''}/>
	<input name="outletTypeShortCodeCSV" type="hidden" value="<c:out value="${quotationRecord.outletTypeShortCodeCSV}" />" disabled/>
	<input name="provideRemarkForNotAvailableQuotation" type="hidden" value="<c:out value="${originalQuotationRecord.provideRemarkForNotAvailableQuotation}"/>" disabled/>
	<table class="table table-striped table-bordered table-hover price-table">
		<thead>
			<tr>
				<th style="width: 50%"></th>
				<th>Price</th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td class="text-center"><label class="control-label">1</label></td>
				<td><input name="quotationRecord.tourRecord.day1Price" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.day1Price}" />" data-rule-number="true" ${model.readonly ? 'disabled': ''}/></td>
			</tr>
			<tr>
				<td class="text-center"><label class="control-label">2</label></td>
				<td><input name="quotationRecord.tourRecord.day2Price" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.day2Price}" />" data-rule-number="true" ${model.readonly ? 'disabled': ''}/></td>
			</tr>
			<tr>
				<td class="text-center"><label class="control-label">3</label></td>
				<td><input name="quotationRecord.tourRecord.day3Price" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.day3Price}" />" data-rule-number="true" ${model.readonly ? 'disabled': ''}/></td>
			</tr>
			<tr>
				<td class="text-center"><label class="control-label">4</label></td>
				<td><input name="quotationRecord.tourRecord.day4Price" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.day4Price}" />" data-rule-number="true" ${model.readonly ? 'disabled': ''}/></td>
			</tr>
			<tr>
				<td class="text-center"><label class="control-label">5</label></td>
				<td><input name="quotationRecord.tourRecord.day5Price" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.day5Price}" />" data-rule-number="true" ${model.readonly ? 'disabled': ''}/></td>
			</tr>
			<tr>
				<td class="text-center"><label class="control-label">6</label></td>
				<td><input name="quotationRecord.tourRecord.day6Price" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.day6Price}" />" data-rule-number="true" ${model.readonly ? 'disabled': ''}/></td>
			</tr>
			<tr>
				<td class="text-center"><label class="control-label">7</label></td>
				<td><input name="quotationRecord.tourRecord.day7Price" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.day7Price}" />" data-rule-number="true" ${model.readonly ? 'disabled': ''}/></td>
			</tr>
			<tr>
				<td class="text-center"><label class="control-label">8</label></td>
				<td><input name="quotationRecord.tourRecord.day8Price" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.day8Price}" />" data-rule-number="true" ${model.readonly ? 'disabled': ''}/></td>
			</tr>
			<tr>
				<td class="text-center"><label class="control-label">9</label></td>
				<td><input name="quotationRecord.tourRecord.day9Price" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.day9Price}" />" data-rule-number="true" ${model.readonly ? 'disabled': ''}/></td>
			</tr>
			<tr>
				<td class="text-center"><label class="control-label">10</label></td>
				<td><input name="quotationRecord.tourRecord.day10Price" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.day10Price}" />" data-rule-number="true" ${model.readonly ? 'disabled': ''}/></td>
			</tr>
			<tr>
				<td class="text-center"><label class="control-label">11</label></td>
				<td><input name="quotationRecord.tourRecord.day11Price" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.day11Price}" />" data-rule-number="true" ${model.readonly ? 'disabled': ''}/></td>
			</tr>
			<tr>
				<td class="text-center"><label class="control-label">12</label></td>
				<td><input name="quotationRecord.tourRecord.day12Price" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.day12Price}" />" data-rule-number="true" ${model.readonly ? 'disabled': ''}/></td>
			</tr>
			<tr>
				<td class="text-center"><label class="control-label">13</label></td>
				<td><input name="quotationRecord.tourRecord.day13Price" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.day13Price}" />" data-rule-number="true" ${model.readonly ? 'disabled': ''}/></td>
			</tr>
			<tr>
				<td class="text-center"><label class="control-label">14</label></td>
				<td><input name="quotationRecord.tourRecord.day14Price" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.day14Price}" />" data-rule-number="true" ${model.readonly ? 'disabled': ''}/></td>
			</tr>
			<tr>
				<td class="text-center"><label class="control-label">15</label></td>
				<td><input name="quotationRecord.tourRecord.day15Price" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.day15Price}" />" data-rule-number="true" ${model.readonly ? 'disabled': ''}/></td>
			</tr>
			<tr>
				<td class="text-center"><label class="control-label">16</label></td>
				<td><input name="quotationRecord.tourRecord.day16Price" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.day16Price}" />" data-rule-number="true" ${model.readonly ? 'disabled': ''}/></td>
			</tr>
			<tr>
				<td class="text-center"><label class="control-label">17</label></td>
				<td><input name="quotationRecord.tourRecord.day17Price" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.day17Price}" />" data-rule-number="true" ${model.readonly ? 'disabled': ''}/></td>
			</tr>
			<tr>
				<td class="text-center"><label class="control-label">18</label></td>
				<td><input name="quotationRecord.tourRecord.day18Price" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.day18Price}" />" data-rule-number="true" ${model.readonly ? 'disabled': ''}/></td>
			</tr>
			<tr>
				<td class="text-center"><label class="control-label">19</label></td>
				<td><input name="quotationRecord.tourRecord.day19Price" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.day19Price}" />" data-rule-number="true" ${model.readonly ? 'disabled': ''}/></td>
			</tr>
			<tr>
				<td class="text-center"><label class="control-label">20</label></td>
				<td><input name="quotationRecord.tourRecord.day20Price" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.day20Price}" />" data-rule-number="true" ${model.readonly ? 'disabled': ''}/></td>
			</tr>
			<tr>
				<td class="text-center"><label class="control-label">21</label></td>
				<td><input name="quotationRecord.tourRecord.day21Price" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.day21Price}" />" data-rule-number="true" ${model.readonly ? 'disabled': ''}/></td>
			</tr>
			<tr>
				<td class="text-center"><label class="control-label">22</label></td>
				<td><input name="quotationRecord.tourRecord.day22Price" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.day22Price}" />" data-rule-number="true" ${model.readonly ? 'disabled': ''}/></td>
			</tr>
			<tr>
				<td class="text-center"><label class="control-label">23</label></td>
				<td><input name="quotationRecord.tourRecord.day23Price" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.day23Price}" />" data-rule-number="true" ${model.readonly ? 'disabled': ''}/></td>
			</tr>
			<tr>
				<td class="text-center"><label class="control-label">24</label></td>
				<td><input name="quotationRecord.tourRecord.day24Price" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.day24Price}" />" data-rule-number="true" ${model.readonly ? 'disabled': ''}/></td>
			</tr>
			<tr>
				<td class="text-center"><label class="control-label">25</label></td>
				<td><input name="quotationRecord.tourRecord.day25Price" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.day25Price}" />" data-rule-number="true" ${model.readonly ? 'disabled': ''}/></td>
			</tr>
			<tr>
				<td class="text-center"><label class="control-label">26</label></td>
				<td><input name="quotationRecord.tourRecord.day26Price" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.day26Price}" />" data-rule-number="true" ${model.readonly ? 'disabled': ''}/></td>
			</tr>
			<tr>
				<td class="text-center"><label class="control-label">27</label></td>
				<td><input name="quotationRecord.tourRecord.day27Price" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.day27Price}" />" data-rule-number="true" ${model.readonly ? 'disabled': ''}/></td>
			</tr>
			<tr>
				<td class="text-center"><label class="control-label">28</label></td>
				<td><input name="quotationRecord.tourRecord.day28Price" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.day28Price}" />" data-rule-number="true" ${model.readonly ? 'disabled': ''}/></td>
			</tr>
			<c:if test="${quotationRecord.numberOfDaysOfSurveyMonth >= 29}">
			<tr>
				<td class="text-center"><label class="control-label">29</label></td>
				<td><input name="quotationRecord.tourRecord.day29Price" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.day29Price}" />" data-rule-number="true" ${model.readonly ? 'disabled': ''}/></td>
			</tr>
			</c:if>
			<c:if test="${quotationRecord.numberOfDaysOfSurveyMonth >= 30}">
			<tr>
				<td class="text-center"><label class="control-label">30</label></td>
				<td><input name="quotationRecord.tourRecord.day30Price" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.day30Price}" />" data-rule-number="true" ${model.readonly ? 'disabled': ''}/></td>
			</tr>
			</c:if>
			<c:if test="${quotationRecord.numberOfDaysOfSurveyMonth >= 31}">
			<tr>
				<td class="text-center"><label class="control-label">31</label></td>
				<td><input name="quotationRecord.tourRecord.day31Price" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.day31Price}" />" data-rule-number="true" ${model.readonly ? 'disabled': ''}/></td>
			</tr>
			</c:if>
			<tr>
				<td class="text-center"><label class="control-label">Average</label></td>
				<td><input type="text" class="form-control" style="width: 100%" value="<c:out value="${average}" />" data-rule-number="true" ${model.readonly ? 'disabled': ''}/></td>
			</tr>
		</tbody>
	</table>
	<br/>
	<table class="table table-striped table-bordered table-hover">
		<tbody>
			<tr>
				<td><input name="${model.readonly ? 'readonly.' : ''}quotationRecord.tourRecord.extraPrice1Name" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.extraPrice1Name}" />"
						${model.readonly ? 'disabled': ''}/></td>
				<td>
					<input name="${model.readonly ? 'readonly.' : ''}quotationRecord.tourRecord.extraPrice1Value" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.extraPrice1Value}" />"
						data-rule-number="true" ${model.readonly ? 'disabled': ''}/><br/>
					<label><input name="quotationRecord.tourRecord.extraPrice1Count" type="checkbox" value="true" ${quotationRecord.tourRecord.extraPrice1Count ? "checked" : ""} ${model.readonly ? 'disabled': ''}/> Price included</label>
				</td>
			</tr>
			<tr>
				<td><input name="${model.readonly ? 'readonly.' : ''}quotationRecord.tourRecord.extraPrice2Name" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.extraPrice2Name}" />"
						${model.readonly ? 'disabled': ''}/></td>
				<td>
					<input name="${model.readonly ? 'readonly.' : ''}quotationRecord.tourRecord.extraPrice2Value" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.extraPrice2Value}" />"
						data-rule-number="true" ${model.readonly ? 'disabled': ''}/><br/>
					<label><input name="quotationRecord.tourRecord.extraPrice2Count" type="checkbox" value="true" ${quotationRecord.tourRecord.extraPrice2Count ? "checked" : ""} ${model.readonly ? 'disabled': ''}/> Price included</label>
				</td>
			</tr>
			<tr>
				<td><input name="${model.readonly ? 'readonly.' : ''}quotationRecord.tourRecord.extraPrice3Name" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.extraPrice3Name}" />"
						${model.readonly ? 'disabled': ''}/></td>
				<td>
					<input name="${model.readonly ? 'readonly.' : ''}quotationRecord.tourRecord.extraPrice3Value" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.extraPrice3Value}" />"
						data-rule-number="true" ${model.readonly ? 'disabled': ''}/><br/>
					<label><input name="quotationRecord.tourRecord.extraPrice3Count" type="checkbox" value="true" ${quotationRecord.tourRecord.extraPrice3Count ? "checked" : ""} ${model.readonly ? 'disabled': ''}/> Price included</label>
				</td>
			</tr>
			<tr>
				<td><input name="${model.readonly ? 'readonly.' : ''}quotationRecord.tourRecord.extraPrice4Name" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.extraPrice4Name}" />"
						${model.readonly ? 'disabled': ''}/></td>
				<td>
					<input name="${model.readonly ? 'readonly.' : ''}quotationRecord.tourRecord.extraPrice4Value" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.extraPrice4Value}" />"
						data-rule-number="true" ${model.readonly ? 'disabled': ''}/><br/>
					<label><input name="quotationRecord.tourRecord.extraPrice4Count" type="checkbox" value="true" ${quotationRecord.tourRecord.extraPrice4Count ? "checked" : ""} ${model.readonly ? 'disabled': ''}/> Price included</label>
				</td>
			</tr>
			<tr>
				<td><input name="${model.readonly ? 'readonly.' : ''}quotationRecord.tourRecord.extraPrice5Name" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.extraPrice5Name}" />"
						${model.readonly ? 'disabled': ''}/></td>
				<td>
					<input name="${model.readonly ? 'readonly.' : ''}quotationRecord.tourRecord.extraPrice5Value" type="text" class="form-control" style="width: 100%" value="<c:out value="${quotationRecord.tourRecord.extraPrice5Value}" />"
						data-rule-number="true" ${model.readonly ? 'disabled': ''}/><br/>
					<label><input name="quotationRecord.tourRecord.extraPrice5Count" type="checkbox" value="true" ${quotationRecord.tourRecord.extraPrice5Count ? "checked" : ""} ${model.readonly ? 'disabled': ''}/> Price included</label>
				</td>
			</tr>
		</tbody>
	</table>
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
				<c:when test="${quotationRecord.newRecruitment}">
				<option value="1">Available</option>
				</c:when>
				
				<c:otherwise>
				<option value="1"
					${quotationRecord.availability == 1 ? "selected" : ""}>Available</option>
					<c:if test="${originalQuotationRecord.quotation.unit.ruaAllowed}">
					<option value="5"
						${quotationRecord.availability == 5 ? "selected" : ""}>Not Suitable</option>
					</c:if>
				<option value="6"
					${quotationRecord.availability == 6 ? "selected" : ""}>回倉</option>
				<option value="7"
					${quotationRecord.availability == 7 ? "selected" : ""}>無團</option>
				</c:otherwise>
				</c:choose>
				
			</c:if>
			
			</select>
		</div>
		<c:if test="${originalQuotationRecord.quotationState != 'Verify'}">
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
		<div class="col-md-8">
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
		<label class="col-md-4 control-label">Category Remark</label>
		<div class="col-md-8">
			<p class="form-control-static break-text">${quotationRecord.assignmentCategoryRemark}</p>
		</div>
	</div>
	<div class="form-group">
		<label class="col-md-4 control-label">Contact Person</label>
		<div class="col-md-8">
			<p class="form-control-static">${quotationRecord.assignmentContactPerson}</p>
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
		<label class="col-md-4 control-label">Field Officer</label>
		<div class="col-md-8">
			<p class="form-control-static">${quotationRecord.fieldOfficer}</p>
		</div>
	</div>
	
	<c:if test="${not quotationRecord.backNo}">
		<c:if test="${quotationRecord.quotationState == 'Verify'}">
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
					${model.readonly ? 'disabled': ''}
					class="form-control" style="height:100px"><c:out value="${quotationRecord.verificationReply}"/></textarea>
			</div>
		</div>
		</c:if>
		<c:if test="${quotationRecord.quotationState == 'Revisit'}">
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
	</div>
</div>