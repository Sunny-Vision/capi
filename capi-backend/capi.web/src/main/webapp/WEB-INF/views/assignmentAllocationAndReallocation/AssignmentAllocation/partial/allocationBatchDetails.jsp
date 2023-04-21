<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="form-group">
	<label for="" class="col-sm-2 control-label">Survey Month Start Date</label>
	<div class="col-sm-4" style="padding-top: 7px;">
		<span><c:out value="${displayModel.getSurveyMonthStartDateStr()}"></c:out></span>
	</div>
</div>
<div class="form-group">
	<label for="" class="col-sm-2 control-label">Survey Month End Date</label>
	<div class="col-sm-4" style="padding-top: 7px;">
		<span><c:out value="${displayModel.getSurveyMonthEndDateStr()}"></c:out></span>
	</div>
</div>
<div class="form-group">
	<label for="" class="col-sm-2 control-label">Allocation Batch Start Date</label>
	<div class="col-sm-4" style="padding-top: 7px;">
		<span><c:out value="${displayModel.getAllocationBatchStartDateStr()}"></c:out></span>
	</div>
</div>
<div class="form-group">
	<label for="" class="col-sm-2 control-label">Allocation Batch End Date</label>
	<div class="col-sm-4" style="padding-top: 7px;">
		<span><c:out value="${displayModel.getAllocationBatchEndDateStr()}"></c:out></span>
	</div>
</div><div class="form-group">
	<label for="" class="col-sm-2 control-label">No. of Assignment</label>
	<div class="col-sm-4" style="padding-top: 7px;">
		<span><c:out value="${displayModel.getNoOfAssignment()}"></c:out></span>
	</div>
</div>
<div class="form-group">
	<label for="" class="col-sm-2 control-label">No. of Quotation Record</label>
	<div class="col-sm-4" style="padding-top: 7px;">
		<span><c:out value="${displayModel.getNoOfQuotation()}"></c:out></span>
	</div>
</div>
<div class="form-group">
	<label for="" class="col-sm-2 control-label">No. of Man-day required</label>
	<div class="col-sm-4" style="padding-top: 7px;">
		<span><c:out value="${displayModel.getNoOfManDayRequired()}"></c:out></span>
	</div>
</div>
<div class="form-group">
	<label for="" class="col-sm-2 control-label">Total Available Man-day</label>
	<div class="col-sm-4" style="padding-top: 7px;">
		<span><c:out value="${displayModel.getTotalAvailableManDay()}"></c:out></span>
	</div>
</div>