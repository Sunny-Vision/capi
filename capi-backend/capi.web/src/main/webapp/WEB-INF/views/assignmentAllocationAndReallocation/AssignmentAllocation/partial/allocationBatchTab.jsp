<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<form class="form-horizontal" action="<c:url value='/assignmentAllocationAndReallocation/SurveyMonth/updatePartial'/>" method="post" role="form" id="mainForm">
	<div class="form-group">
		<label for="" class="col-sm-2 control-label">Reference Month</label>
		<div class="col-sm-4">
			<div class="input-group">
				<input type="text" class="form-control date-picker" data-orientation="top" data-date-minviewmode="months" data-date-format="mm-yyyy"
					data-date-startdate="<c:out value="${currentDateStr}"/>" <c:if test="${readonly}">readonly</c:if>
					
					name="allocationBatchTabModel.referenceMonthStr" required value="<c:out value='${displayModel.getReferenceMonthStr()}' />" />
				<div class="input-group-addon">
					<i class="fa fa-calendar"></i>
				</div>
			</div>
		</div>
	</div>
	<div class="form-group">
		<label for="" class="col-sm-2 control-label">Allocation Batch</label>
		<div class="col-sm-4">
			<select class="select2" name="allocationBatchTabModel.allocationBatchId" required>
			<c:forEach items="${abmList}" var="abm">
				<option value="<c:out value='${abm.getAllocationBatchId()}'/>" <c:if test='${abm.getAllocationBatchId() == displayModel.getAllocationBatchId()}'>selected</c:if> ><c:out value='${abm.getBatchName()}'></c:out></option>	
			</c:forEach>
			</select>
		</div>
	</div>
	<div id="allocationBatchDetails">
	
	</div>
</form>