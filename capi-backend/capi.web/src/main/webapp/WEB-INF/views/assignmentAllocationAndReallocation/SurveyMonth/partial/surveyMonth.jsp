<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<form class="form-horizontal" action="<c:url value='/assignmentAllocationAndReallocation/SurveyMonth/updatePartial'/>" method="post" role="form" id="mainForm">
	<div class="form-group">
		<label for="" class="col-sm-2 control-label">Reference Month</label>
		<div class="col-sm-4">
			<div class="input-group">
				<input type="text" class="form-control date-picker" data-orientation="top" data-date-minviewmode="months" data-date-format="mm-yyyy"
					data-date-startdate="<c:out value="${currentDateStr}"/>" <c:if test="${model.id != null}">readonly</c:if>
					name="surveyMonth.referenceMonthStr" required value="<c:out value="${model.getReferenceMonthStr()}" />" />
				<div class="input-group-addon">
					<i class="fa fa-calendar"></i>
				</div>
			</div>
		</div>
	</div>
	<div class="form-group">
		<label for="" class="col-sm-2 control-label">Start Date</label>
		<div class="col-sm-4">
			<div class="input-group">
				<input type="text" class="form-control date-picker" data-orientation="top" <c:if test="${readonly}">readonly</c:if>
					data-date-startdate="<c:out value="${currentDateStr}" />"
					name="surveyMonth.startDateStr" required value="<c:out value="${model.getStartDateStr()}" />" />
				<div class="input-group-addon">
					<i class="fa fa-calendar"></i>
				</div>
			</div>
		</div>
	</div>
	<div class="form-group">
		<label for="" class="col-sm-2 control-label">End Date</label>
		<div class="col-sm-4">
			<div class="input-group">
				<input type="text" class="form-control date-picker" data-orientation="top" <c:if test="${readonly}">readonly</c:if>
					name="surveyMonth.endDateStr" required value="<c:out value="${model.getEndDateStr()}" />" />
				<div class="input-group-addon">
					<i class="fa fa-calendar"></i>
				</div>
			</div>
		</div>
	</div>
	<div class="form-group">
		<label for="" class="col-sm-2 control-label">Closing Date</label>
		<div class="col-sm-4">
				<input type="text" class="form-control" name="surveyMonth.closingDateStr" required value="<c:out value="${model.getClosingDateStr()}" />" readonly/>
		</div>
	</div>
</form>