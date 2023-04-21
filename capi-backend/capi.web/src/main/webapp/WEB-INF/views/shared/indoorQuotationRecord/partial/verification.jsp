<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%
/**
 * Display outlet section
 * 
 * Controller permission required:
 * /shared/General/getOutletImage
 */
%>
<div class="box box-primary  collapsed-box">
	<div class="box-header with-border">
		<h3 class="box-title">
			Verification
		</h3>
		<div class="box-tools pull-right">
			<button class="btn btn-box-tool btn-header-collapse" type="button" data-widget="collapse">
				<i class="fa fa-plus"></i>
			</button>
		</div>
	</div>
	<div class="box-body" style="display: none;">
		<div class="form-horizontal">
			<div class="row">
				<div class="col-md-12">
					<div class="form-group">
						<label class="col-md-2">
							<input type="radio" class="verifications" required value="isFirmVerify" name="verification" <c:if test="${indoorQuotationRecord.isFirmVerify()}">checked</c:if> /> Firm Verification Remarks
						</label>
						<div class="col-md-6">
							<textarea class="form-control verificationRemarks" name="firmRemark" data-rule-required="[name='verification'][value='isFirmVerify']:checked" <c:if test="${!indoorQuotationRecord.isFirmVerify()}">disabled</c:if>><c:out value="${indoorQuotationRecord.firmRemark}"></c:out></textarea>
						</div>
					</div>
					<div class="form-group">
						<label class="col-md-2">
							<input type="radio" class="verifications" required value="isCategoryVerify" name="verification" <c:if test="${indoorQuotationRecord.isCategoryVerify()}">checked</c:if>/> Category Verification Remarks
						</label>
						<div class="col-md-6">
							<textarea class="form-control verificationRemarks" name="categoryRemark" data-rule-required="[name='verification'][value='isCategoryVerify']:checked" <c:if test="${!indoorQuotationRecord.isCategoryVerify()}">disabled</c:if>><c:out value="${indoorQuotationRecord.categoryRemark}"></c:out></textarea>
						</div>
					</div>
					<div class="form-group">
						<label class="col-md-2">
							<input type="radio" class="verifications" required value="isQuotationVerify" name="verification" <c:if test="${indoorQuotationRecord.isQuotationVerify()}">checked</c:if>/> Quotation Verification Remarks
						</label>
						<div class="col-md-6">
							<textarea class="form-control verificationRemarks" name="quotationRemark" data-rule-required="[name='verification'][value='isQuotationVerify']:checked" <c:if test="${!indoorQuotationRecord.isQuotationVerify()}">disabled</c:if>><c:out value="${indoorQuotationRecord.quotationRemark}"></c:out></textarea>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>