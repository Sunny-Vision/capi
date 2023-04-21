<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<tr class="batch-row" data-newid="${referenceId}">
<c:set var="allOne" value="${allAssignmentTypeOne}" />
<c:set var="allNotOne" value="${allAssignmentTypeNotOne}" />
	<td>
		<input type="hidden" name="newAssignmentAttr[${referenceId}].backTrackDateDisplayModelId" value="${backTrackDateDisplayModelId}">
		<select class="form-control select2 batchId" name="newAssignmentAttr[${referenceId}].batchId" required data-newId="${referenceId}" style="display:none" data-category="${category}" required data-row-error-class="row-error-batchId" >
			<option value=""></option>
			<c:forEach items="${batchList}" var="batch">
				<option value="<c:out value="${batch.batchId}" />">${batch.code}</option>
			</c:forEach>
		</select>
		<button type="button" class="btn btn-primary reallocate" >Reallocate</button>
		<input type="hidden" name="newAssignmentAttr[${referenceId}].referenceId" value="new-${referenceId}">
		<input type="hidden" name="newAssignmentAttr[${referenceId}].category" value="<c:out value="${category}" />">
	</td>
	<c:if test="${allOne == false}">
	<td>
		<div class="input-group">
			<input name='newAssignmentAttr[${referenceId}].startDateStr' class='assignmentAttr-startDate form-control date-picker' data-category="${category}"
				data-orientation='top' required data-date-startdate="${surveyMonth.getStartDateStr()}" data-date-enddate="${surveyMonth.getEndDateStr()}"
				 data-row-error-class="row-error-startDate" />
			<div class="input-group-addon">
				<i class="fa fa-calendar"></i>
			</div>
		</div>
	</td>
	<td>
		<div class="input-group">
			<input name='newAssignmentAttr[${referenceId}].endDateStr' class='assignmentAttr-endDate form-control date-picker' data-category="${category}"
				data-orientation='top' required data-date-startdate="${surveyMonth.getStartDateStr()}" data-date-enddate="${surveyMonth.getEndDateStr()}"
				 data-row-error-class="row-error-endDate" />
			<div class="input-group-addon">
				<i class="fa fa-calendar"></i>
			</div>
		</div>
	</td>
	</c:if>
	<c:if test="${allNotOne == false}" >
	<td>
		<div class="input-group date date-picker collectionDatePicker" data-category="${category}" data-multidate="true"
				data-orientation='top' data-date-startdate="${surveyMonth.startDateStr}" data-date-enddate="${surveyMonth.getEndDateStr()}"
				 data-target="[name='newAssignmentAttr[${referenceId}].collectionDateDisplay']" data-input="[name='newAssignmentAttr[${referenceId}].collectionDatesStr']">
			<input type="text" name='newAssignmentAttr[${referenceId}].collectionDatesStr' class='assignmentAttr-collectionDate' style="position: absolute; z-index: -1" data-category="${category}" data-rule-collectionDateRequired="true"
			 data-row-error-class="row-error-collectionDate" 
			<c:if test="${allNotOne == true}">disabled</c:if> />
			<select class="select2 collectionDate" name="newAssignmentAttr[${referenceId}].collectionDateDisplay" disabled multiple required style="width: 0; height: 0;" data-category="${category}"></select>
			<div class="input-group-addon">
				<i class="fa fa-calendar"></i>
			</div>
		</div>
	</td>
	<td>
		<div class="col-md-12 radio-group" >
			<label class="radio-inline"><input class="assignmentAttr-session" type="radio" name="newAssignmentAttr[${referenceId}].session" value="A" data-category="${category}" data-rule-sessionRequired="true" data-row-error-class="row-error-session"  <c:if test="${allNotOne == true}">disabled</c:if> >A</label>
			<label class="radio-inline"><input class="assignmentAttr-session" type="radio" name="newAssignmentAttr[${referenceId}].session" value="P" data-category="${category}" data-rule-sessionRequired="true" data-row-error-class="row-error-session"  <c:if test="${allNotOne == true}">disabled</c:if> >P</label>
		</div>
	</td>
	</c:if>
	<td>
		<div class="input-group" style="width:100%">		
			<input type="hidden" class="form-control assignmentAttr-officerIds" name="newAssignmentAttr[${referenceId}].officers" value="<c:out value="${model.user.userId}" />" required data-category="${category}" data-row-error-class="row-error-officerIds"  />
			<select class="form-control select2 officer assignmentAttr-officerIds" name="newAssignmentAttr[${referenceId}].officerIds" required style="" data-category="${category}"  data-row-error-class="row-error-officerIds" 
			<c:if test="${allNotOne == true}">disabled</c:if> >
				<option value=""></option>
				<c:forEach items="${officerList}" var="officer">
					<option value="<c:out value="${officer.userId}" />">${officer.staffCode} - ${officer.chineseName} (${officer.destination}) </option>
				</c:forEach>
			</select>
			<div class="input-group-addon newOfficer" data-category="${category}">
				<i class="fa fa-search"></i>
			</div>
		</div>
	</td>
	<c:if test="${allOne == false}">
	<td>
		<select class="form-control select2 assignmentAttr-allocationBatch" name="newAssignmentAttr[${referenceId}].allocationBatchRefId" data-category="${category}" data-rule-allocationBatchRefIdRequired="true"
		data-row-error-class="row-error-allocationBatch" >
			<option value=""></option>
			<c:forEach var="allocationBatch" items="${allocationBatchList}">
				<option value="<c:out value="${allocationBatch.getId()}" />" data-start-date="${allocationBatch.getStartDateStr()}" data-end-date="${allocationBatch.getEndDateStr()}">
					${allocationBatch.getNumberOfBatch()} ( ${allocationBatch.getStartDateStr()} - ${allocationBatch.getEndDateStr()} )
				</option>
			</c:forEach>
			<c:forEach var="allocationBatch" items="${newAllocationBatchList}">
				<option value="new-${allocationBatch.getId()}" data-start-date="${allocationBatch.getStartDateStr()}" data-end-date="${allocationBatch.getEndDateStr()}">
					${allocationBatch.getNumberOfBatch()}  ( ${allocationBatch.getStartDateStr()} - ${allocationBatch.getEndDateStr()} )
				</option>
			</c:forEach>
		</select>
	</td>
	</c:if>
	<td class="text-center action">
		<a href='javascript:void(0)' class='table-btn btn-delete' data-newid='${referenceId}'><span class='fa fa-times fa-2' aria-hidden='true'></span></a>
	</td>
</tr>