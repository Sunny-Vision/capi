<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div class="row">
	<div class="col-md-12" >
		<form class="form-horizontal" action="" method="post" role="form" id="allocationBatch">
			<table class="table table-striped table-bordered table-hover">
				<thead>
				<tr>
					<th width="32%">Number Of Batch</th>
					<th width="32%">Start Date</th>
					<th width="32%">End Date</th>
					<th class="text-center action" width="4%"></th>
				</tr>
				</thead>
				<tbody id="allocationBatchBody">
					<c:forEach var="newEntry" items="${newAllocationBatchList}">
					<c:if test="${newEntry.getId() != null}">
					<tr data-newid='${newEntry.getId()}' class='allocationBatchRow'>
						<td>
							<input type='hidden' name='newAllocationBatch[<c:out value="${newEntry.getId()}" />].id' <c:if test="${readonly}">readonly</c:if> value='<c:out value="${newEntry.getId()}" />' value="<c:out value="${newEntry.getId()}" /> ">
							<input type='hidden' name='newAllocationBatch[<c:out value="${newEntry.getId()}" />].allocationBatchId' <c:if test="${readonly}">readonly</c:if> value='<c:out value="${newEntry.getAllocationBatchId()}" />' value="<c:out value="${newEntry.getId()}" /> ">
							<input name='newAllocationBatch[${newEntry.getId()}].numberOfBatch' <c:if test="${readonly}">readonly</c:if> value="<c:out value="${newEntry.getNumberOfBatch()}" />" class='form-control' required/>
						</td>
						<td>
							<div class="input-group">
								<input name='newAllocationBatch[${newEntry.getId()}].startDateStr' class='form-control date-picker' value="<c:out value="${newEntry.getStartDateStr()}" />"
									<c:if test="${readonly}">readonly</c:if> data-orientation='top' required data-date-startdate="${surveyMonth.getStartDateStr()}" data-date-enddate="${surveyMonth.getEndDateStr()}"/>
								<div class="input-group-addon">
									<i class="fa fa-calendar"></i>
								</div>
							</div>
						</td>
						<td>
							<div class="input-group">
								<input name='newAllocationBatch[${newEntry.getId()}].endDateStr' class='form-control date-picker' value="<c:out value="${newEntry.getEndDateStr()}" />"
									<c:if test="${readonly}">readonly</c:if> data-orientation='top' required data-date-startdate="${surveyMonth.getStartDateStr()}" data-date-enddate="${surveyMonth.getEndDateStr()}"/>
								<div class="input-group-addon">
									<i class="fa fa-calendar"></i>
								</div>
							</div>
						</td>
						<td>
							<c:if test="${readonly == false}">
							<a href='javascript:void(0)' class='table-btn btn-delete' data-newid='${newEntry.getId()}'><span class='fa fa-times fa-2' aria-hidden='true'></span></a>
							</c:if>
						</td>
					</tr>
					</c:if>
					</c:forEach>
				</tbody>
			</table>
		</form>
	</div>
</div>
<div class="row">
	<div class="col-md-12" >
	<c:if test="${readonly == false}">
		<button type="button" class="btn btn-default" id="addAllocationBatch">Add</button>
	</c:if>
	</div>
</div>