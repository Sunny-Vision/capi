<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<form class="form-horizontal" action="<c:url value='/assignmentAllocationAndReallocation/SurveyMonth/updatePartial'/>" method="post" role="form" id="assignmentForm" data-submit-status="false">
	<c:forEach var="batchCategory" items="${batchCategoryList}">
	<div class="category-container">
	<c:set var="allOne" value="${batchCategory.allAssignmentTypeOne}" />
	<c:set var="allNotOne" value="${batchCategory.allAssignmentTypeNotOne}" />
	<c:if test="${fn:length(batchCategory.getBatchCategoryName()) > 0}">
	<div class="row">
		<div class="col-md-12" >
			<table class="table table-striped table-bordered table-hover" data-category="<c:out value="${batchCategory.getBatchCategoryName()}" />">
				<thead>
				<tr>
					<th rowspan="2" width="14%"><c:out value="${batchCategory.getBatchCategoryName()}" /></th>
					<c:if test="${allOne == false}" >
						<th width="14%">Start Date</th>
						<th width="14%">End Date</th>
					</c:if>
					<c:if test="${allNotOne == false}" >
					<th width="14%">Collection Date</th>
					<th width="14%">Session</th>
					</c:if>
					<th width="14%">Field Officer Id</th>
					<c:if test="${allOne == false}" >
						<th width="14%">Allocation Batch</th>
					</c:if>
					<th class="text-center action" width="14%"></th>
				</tr>
				<tr>
					<c:if test="${allOne == false}" >
					<th>
						<div class="input-group">
							<input name='batchStartDateUpdate' class='form-control date-picker' data-category="<c:out value="${batchCategory.getBatchCategoryName()}" />"
								data-orientation='top'  data-date-startdate="${surveyMonth.getStartDateStr()}" data-date-enddate="${surveyMonth.getEndDateStr()}"/>
							<div class="input-group-addon">
								<i class="fa fa-calendar"></i>
							</div>
						</div>
						<button type="button" class="btn btn-default btn-copy" data-copy="batchStartDateUpdate" data-category="<c:out value="${batchCategory.getBatchCategoryName()}" />">Copy to below</button>
					</th>
					<th>
						<div class="input-group">
							<input name='batchEndDateUpdate' class='form-control date-picker' data-category="<c:out value="${batchCategory.getBatchCategoryName()}" />"
								data-orientation='top'  data-date-startdate="${surveyMonth.getStartDateStr()}" data-date-enddate="${surveyMonth.getEndDateStr()}"/>
							<div class="input-group-addon">
								<i class="fa fa-calendar"></i>
							</div>
						</div>
						<button type="button" class="btn btn-default btn-copy" data-copy="batchEndDateUpdate" data-category="<c:out value="${batchCategory.getBatchCategoryName()}" />">Copy to below</button>
					</th>
					</c:if>
					<c:if test="${allNotOne == false}" >
					<th>
						<div class="input-group date date-picker" data-category="<c:out value="${batchCategory.getBatchCategoryName()}" />" data-multidate="true"
								data-orientation='top' data-date-startdate="${surveyMonth.startDateStr}" data-date-enddate="${surveyMonth.getEndDateStr()}"
								 data-target="[name='dateDisplay'][data-category='<c:out value="${batchCategory.getBatchCategoryName()}" />']" data-input="[name='batchCollectionDateUpdate'][data-category='<c:out value="${batchCategory.getBatchCategoryName()}" />']">
							<input name='batchCollectionDateUpdate' data-category='<c:out value="${batchCategory.getBatchCategoryName()}" />' class='form-control assignmentAttr-collectionDate'  style="display: none" />
							<select class="form-control select2 assignmentAttr-collectionDate" name="dateDisplay" data-category='<c:out value="${batchCategory.getBatchCategoryName()}" />' disabled multiple style="display:none"></select>
							<div class="input-group-addon">
								<i class="fa fa-calendar"></i>
							</div>
						</div>
						<button type="button" class="btn btn-default btn-copy " data-copy="batchCollectionDateUpdate" data-category="<c:out value="${batchCategory.getBatchCategoryName()}" />">Copy to below</button>
					</th>
					<th>
						<div class="col-md-12" >
							<label class="radio-inline"><input type="radio" name="batchSessionUpdate" value="A" data-category="<c:out value="${batchCategory.getBatchCategoryName()}" />">A</label>
							<label class="radio-inline"><input type="radio" name="batchSessionUpdate" value="P" data-category="<c:out value="${batchCategory.getBatchCategoryName()}" />">P</label>
						</div>
						<button type="button" class="btn btn-default btn-copy" data-copy="batchSessionUpdate" data-category="<c:out value="${batchCategory.getBatchCategoryName()}" />">Copy to below</button>
					</th>
					</c:if>
					<th>
						<div class="input-group">		
							<input type="hidden" class="form-control" name="batchOfficerUpdate" value="<c:out value="${model.user.userId}" />"  data-category="<c:out value="${batchCategory.getBatchCategoryName()}" />"/>
							<select class="form-control select2 batchOfficerUpdate" name="batchOfficerUpdate-userId"  style="display:none" data-category="<c:out value="${batchCategory.getBatchCategoryName()}" />">
								<option value=""></option>
								<c:forEach items="${officerList}" var="officer">
									<option value="<c:out value="${officer.userId}" />">${officer.staffCode} - ${officer.chineseName} (${officer.destination}) </option>
								</c:forEach>
							</select>
							<div class="input-group-addon batchOfficerUpdate" data-category="<c:out value="${batchCategory.getBatchCategoryName()}" />">
								<i class="fa fa-search"></i>
							</div>
						</div>
						<button type="button" class="btn btn-default btn-copy" data-copy="batchOfficerUpdate" data-category="<c:out value="${batchCategory.getBatchCategoryName()}" />">Copy to below</button>
					</th>
					<c:if test="${allOne == false}" >
					<th>
						<select class="form-control select2" name="batchAllocationUpdate" data-category="<c:out value="${batchCategory.getBatchCategoryName()}" />">
							<c:forEach var="allocationBatch" items="${allocationBatchList}">
								<option value="<c:out value="${allocationBatch.getId()}" />">${batch.getNumberOfBatch()} ( ${allocationBatch.getStartDateStr()} - ${allocationBatch.getEndDateStr()} )</option>
							</c:forEach>
							<c:forEach var="allocationBatch" items="${newAllocationBatchList}">
								<option value="new-${allocationBatch.getId()}">${allocationBatch.getNumberOfBatch()} ( ${allocationBatch.getStartDateStr()} - ${allocationBatch.getEndDateStr()} )</option>
							</c:forEach>
						</select>
						<button type="button" class="btn btn-default btn-copy" data-copy="batchAllocationUpdate" data-category="<c:out value="${batchCategory.getBatchCategoryName()}" />">Copy to below</button>
					</th>
					</c:if>
					<th class="text-center action"></th>
				</tr>
				</thead>
				<tbody id="allocationBatchBody">
					<c:forEach items="${newAssignmentAllocationList}" var="newAssignmentAlloc">
					<c:if test="${newAssignmentAlloc.getCategory() == batchCategory.getBatchCategoryName()}">
						<tr class="batch-row" data-newid="${fn:replace(newAssignmentAlloc.getReferenceId(), 'new-', '')}">
							<td>
								<c:if test="${readonly}">
									<c:forEach items="${batchCategory.getBatchList()}" var="batch">
										<c:if test="${batch.assignmentAttributeId == newAssignmentAlloc.getAssignmentAttributeId()}">${batch.code}</c:if>	
									</c:forEach>
									<input type="hidden" name="newAssignmentAttr[${fn:replace(newAssignmentAlloc.getReferenceId(), 'new-', '')}].batchId" value="${newAssignmentAlloc.getBatchId()}">
								</c:if>
								<c:if test="${isDraft}">
									<input type="hidden" name="newAssignmentAttr[${fn:replace(newAssignmentAlloc.getReferenceId(), 'new-', '')}].backTrackDateDisplayModelId" value="${newAssignmentAlloc.getBackTrackDateDisplayModelId()}">
									<c:forEach items="${batchCategory.getBatchList()}" var="batch">
										<c:if test="${batch.assignmentAttributeId == newAssignmentAlloc.getAssignmentAttributeId()}">
											<input type="hidden" name="newAssignmentAttr[${fn:replace(newAssignmentAlloc.getReferenceId(), 'new-', '')}].assignmentAttributeId" value="${batch.assignmentAttributeId}">
										</c:if>	
									</c:forEach>
								</c:if>
								<c:if test="${readonly == false}">
									<select class="select2 batchId" <c:if test="${readonly}">readonly</c:if> name="newAssignmentAttr[${fn:replace(newAssignmentAlloc.getReferenceId(), 'new-', '')}].batchId" required data-newId="${fn:replace(newAssignmentAlloc.getReferenceId(), 'new-', '')}" data-category="${newAssignmentAlloc.getCategory()}" required 
									data-row-error-class="row-error-batchId" >
										<option value=""></option>
										<c:forEach items="${batchCategory.getBatchList()}" var="batch">
											<option value="<c:out value="${batch.batchId}" />" <c:if test="${batch.batchId == newAssignmentAlloc.getBatchId()}">selected</c:if> >${batch.code}</option>
										</c:forEach>
									</select>
								</c:if>
								<button type="button" class="btn btn-primary reallocate" >Reallocate</button>
								<input type="hidden" name="newAssignmentAttr[${fn:replace(newAssignmentAlloc.getReferenceId(), 'new-', '')}].referenceId" value="<c:out value="${newAssignmentAlloc.getReferenceId()}" />">
								<input type="hidden" name="newAssignmentAttr[${fn:replace(newAssignmentAlloc.getReferenceId(), 'new-', '')}].category" value="<c:out value="${newAssignmentAlloc.getCategory()}" />">
								<input type="hidden" name="newAssignmentAttr[${fn:replace(newAssignmentAlloc.getReferenceId(), 'new-', '')}].selectedBatchType" value="<c:out value='${newAssignmentAlloc.getSelectedBatchType()}'/>"/>
							</td>
							<c:if test="${allOne == false}" >
							<td>
								<div class="input-group">
									<input name='newAssignmentAttr[${fn:replace(newAssignmentAlloc.getReferenceId(), 'new-', '')}].startDateStr' class='assignmentAttr-startDate form-control <c:if test="${newAssignmentAlloc.getSelectedBatchType() != 1}">date-picker</c:if> ' data-category="${newAssignmentAlloc.getCategory()}"
										<c:if test="${readonly}">readonly disabled</c:if> data-orientation='top' required data-date-startdate="${surveyMonth.getStartDateStr()}" data-date-enddate="${surveyMonth.getEndDateStr()}" value="${newAssignmentAlloc.getStartDateStr() }"
										data-row-error-class="row-error-startDate" 
										<c:if test="${newAssignmentAlloc.getSelectedBatchType() == 1}">disabled</c:if>/>
									<div class="input-group-addon <c:if test="${newAssignmentAlloc.getSelectedBatchType() == 1}">disabled</c:if>">
										<i class="fa fa-calendar"></i>
									</div>
								</div>
							</td>
							<td>
								<div class="input-group">
									<input name='newAssignmentAttr[${fn:replace(newAssignmentAlloc.getReferenceId(), 'new-', '')}].endDateStr' class='assignmentAttr-endDate form-control <c:if test="${newAssignmentAlloc.getSelectedBatchType() != 1}">date-picker</c:if>' data-category="${newAssignmentAlloc.getCategory()}"
										<c:if test="${readonly}">readonly disabled</c:if> data-orientation='top' required data-date-startdate="${surveyMonth.getStartDateStr()}" data-date-enddate="${surveyMonth.getEndDateStr()}"  value="${newAssignmentAlloc.getEndDateStr() }"
										 data-row-error-class="row-error-endDate" 
										<c:if test="${newAssignmentAlloc.getSelectedBatchType() == 1}">disabled</c:if>/>
									<div class="input-group-addon <c:if test="${newAssignmentAlloc.getSelectedBatchType() == 1}">disabled</c:if>">
										<i class="fa fa-calendar"></i>
									</div>
								</div>
							</td>
							</c:if>
							<c:if test="${allNotOne == false}" >
							<td>
								<div class="input-group date <c:if test="${newAssignmentAlloc.getSelectedBatchType() == 1}">date-picker</c:if> collectionDatePicker" data-category="${newAssignmentAlloc.getCategory()}" data-multidate="true"
										data-orientation='top' data-date-startdate="${surveyMonth.startDateStr}" data-date-enddate="${surveyMonth.getEndDateStr()}"
										<c:if test="${readonly}">readonly disabled</c:if> data-target="[name='newAssignmentAttr[${fn:replace(newAssignmentAlloc.getReferenceId(), 'new-', '')}].collectionDateDisplay']" data-input="[name='newAssignmentAttr[${fn:replace(newAssignmentAlloc.getReferenceId(), 'new-', '')}].collectionDatesStr']">
									<input type="text" name='newAssignmentAttr[${fn:replace(newAssignmentAlloc.getReferenceId(), 'new-', '')}].collectionDatesStr' class='assignmentAttr-collectionDate' style="position: absolute; z-index: -1" data-category="${newAssignmentAlloc.getCategory()}" value="<c:out value="${newAssignmentAlloc.getCollectionDatesStr()}" />" data-rule-collectionDateRequired="true" data-row-error-class="row-error-collectionDate"  />
									<c:set var="collectionDateList" value="${fn:split(newAssignmentAlloc.getCollectionDatesStr(), ',')}" />
									<select class="select2 collectionDate" name="newAssignmentAttr[${fn:replace(newAssignmentAlloc.getReferenceId(), 'new-', '')}].collectionDateDisplay" disabled multiple required style="width: 0; height: 0;" data-category="${newAssignmentAlloc.getCategory()}">
										<c:forEach items="${collectionDateList}" var="date">
										<c:if test="${date != '' }">
										<option value="<c:out value="${date}" />" selected>${date}</option>
										</c:if>
										</c:forEach>
									</select>
									<div class="input-group-addon <c:if test="${newAssignmentAlloc.getSelectedBatchType() != 1}">disabled</c:if>" >
										<i class="fa fa-calendar"></i>
									</div>
								</div>
							</td>
							<td>
								<div class="col-md-12 radio-group" >
									<label class="radio-inline"><input <c:if test="${readonly}">readonly disabled</c:if> class="assignmentAttr-session" type="radio" name="newAssignmentAttr[${fn:replace(newAssignmentAlloc.getReferenceId(), 'new-', '')}].session" value="A" data-category="${newAssignmentAlloc.getCategory()}" data-rule-sessionRequired="true"  data-row-error-class="row-error-session"   <c:if test="${newAssignmentAlloc.getSession() == 'A'}">checked</c:if>>A</label>
									<label class="radio-inline"><input <c:if test="${readonly}">readonly disabled</c:if> class="assignmentAttr-session" type="radio" name="newAssignmentAttr[${fn:replace(newAssignmentAlloc.getReferenceId(), 'new-', '')}].session" value="P" data-category="${newAssignmentAlloc.getCategory()}" data-rule-sessionRequired="true"  data-row-error-class="row-error-session"  <c:if test="${newAssignmentAlloc.getSession() == 'P'}">checked</c:if>>P</label>
								</div>
							</td>
							</c:if>
							<td>
								<div class="input-group" style="width:100%">		
									<input <c:if test="${readonly}">readonly disabled</c:if> type="hidden" class="form-control assignmentAttr-officerIds" name="newAssignmentAttr[${fn:replace(newAssignmentAlloc.getReferenceId(), 'new-', '')}].officers" value="<c:out value="${newAssignmentAlloc.getOfficerIds()}" />" required data-row-error-class="row-error-officerIds" data-category="${newAssignmentAlloc.getCategory()}" <c:if test="${newAssignmentAlloc.getSelectedBatchType() == 3}">disabled</c:if>/>
									<select <c:if test="${readonly}">readonly disabled</c:if> class="form-control select2 officer assignmentAttr-officerIds" name="newAssignmentAttr[${fn:replace(newAssignmentAlloc.getReferenceId(), 'new-', '')}].officerIds" required data-row-error-class="row-error-officerIds" style="" data-category="${newAssignmentAlloc.getCategory()}" <c:if test="${newAssignmentAlloc.getSelectedBatchType() == 3}">disabled</c:if>>
										<option value=""></option>
										<c:forEach items="${officerList}" var="officer">
											<option value="<c:out value="${officer.userId}" />" <c:if test="${newAssignmentAlloc.getOfficerIds() == officer.userId }">selected</c:if> >${officer.staffCode} - ${officer.chineseName} (${officer.destination})</option>
										</c:forEach>
									</select>
									<div class="input-group-addon <c:if test="${readonly == false}">newOfficer</c:if> <c:if test="${uncateAA.getSelectedBatchType() == 3}">hidden</c:if>" data-category="${newAssignmentAlloc.getCategory()}">
										<i class="fa fa-search"></i>
									</div>
								</div>
							</td>
							<c:if test="${allOne == false}" >
							<td>
								<select <c:if test="${readonly}">readonly disabled</c:if> class="form-control select2 assignmentAttr-allocationBatch" name="newAssignmentAttr[${fn:replace(newAssignmentAlloc.getReferenceId(), 'new-', '')}].allocationBatchRefId" data-category="${newAssignmentAlloc.getCategory()}" data-row-error-class="row-error-allocationBatch"   data-rule-allocationBatchRefIdRequired="true">
									<option value=""></option>
									<c:forEach var="allocationBatch" items="${allocationBatchList}">
										<option value="<c:out value="${allocationBatch.getId()}" />" data-start-date="${allocationBatch.getStartDateStr()}" data-end-date="${allocationBatch.getEndDateStr()}"
											<c:if test="${allocationBatch.getId() == newAssignmentAlloc.getAllocationBatchRefId()}">selected</c:if>>
											${allocationBatch.getNumberOfBatch()} ( ${allocationBatch.getStartDateStr()} - ${allocationBatch.getEndDateStr()} )
										</option>
									</c:forEach>
									<c:forEach var="allocationBatch" items="${newAllocationBatchList}">
										<option value="new-${allocationBatch.getId()}" data-start-date="${allocationBatch.getStartDateStr()}" data-end-date="${allocationBatch.getEndDateStr()}"
											<c:if test="${allocationBatch.getId() == fn:replace(newAssignmentAlloc.getAllocationBatchRefId(), 'new-', '')}">selected</c:if>>
											${allocationBatch.getNumberOfBatch()}  ( ${allocationBatch.getStartDateStr()} - ${allocationBatch.getEndDateStr()} )
										</option>
									</c:forEach>
								</select>
							</td>
							</c:if>
							<td class="text-center action">
								<c:if test="${readonly == false}">
								<a href='javascript:void(0)' class='table-btn btn-delete' data-newid='${fn:replace(newAssignmentAlloc.getReferenceId(), 'new-', '')}'><span class='fa fa-times fa-2' aria-hidden='true'></span></a>
								</c:if>
							</td>
						</tr>
					</c:if>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
	<div class="row">
		<div class="col-md-12" >
			<c:if test="${readonly == false}">
				<button type="button" class="btn btn-default addBatchCategoryItem" data-category="<c:out value="${batchCategory.getBatchCategoryName()}" />">Add</button>
			</c:if>
		</div>
	</div>
	<hr/>
	</c:if>
	<c:if test="${fn:length(batchCategory.getBatchCategoryName()) == 0}">
	<div class="row">
		<div class="col-md-12" >
			<span>Uncategorized:</span>
		</div>
	</div>
	<div class="row">
		<div class="col-md-12" >
			<table class="table table-striped table-bordered table-hover">
				<thead>
				<tr>
					<th rowspan="2" width="14%">Batch Code</th>
					<c:if test="${allOne == false}" >
						<th width="14%">Start Date</th>
						<th width="14%">End Date</th>
					</c:if>
					<c:if test="${allNotOne == false}" >
					<th width="14%">Collection Date</th>
					<th width="14%">Session</th>
					</c:if>
					<th width="14%">Field Officer Id</th>
					<c:if test="${allOne == false}" >
						<th width="14%">Allocation Batch</th>
					</c:if>
					<th class="text-center action" width="14%"></th>
				</tr>
				<tr>
					
					<c:if test="${allOne == false}" >
					<th>
						<div class="input-group">
							<input name='unCatBatchStartDateUpdate' class='form-control date-picker' data-category="<c:out value="" />"
								data-orientation='top'  data-date-startdate="${surveyMonth.getStartDateStr()}" data-date-enddate="${surveyMonth.getEndDateStr()}"/>
							<div class="input-group-addon">
								<i class="fa fa-calendar"></i>
							</div>
						</div>
						<button type="button" class="btn btn-default btn-copy-unCat" data-copy="unCatBatchStartDateUpdate" data-category="<c:out value="" />">Copy to below</button>
					</th>
					<th>
						<div class="input-group">
							<input name='unCatBatchEndDateUpdate' class='form-control date-picker' data-category="<c:out value="" />"
								data-orientation='top'  data-date-startdate="${surveyMonth.getStartDateStr()}" data-date-enddate="${surveyMonth.getEndDateStr()}"/>
							<div class="input-group-addon">
								<i class="fa fa-calendar"></i>
							</div>
						</div>
						<button type="button" class="btn btn-default btn-copy-unCat" data-copy="unCatBatchEndDateUpdate" data-category="<c:out value="" />">Copy to below</button>
					</th>
					</c:if>
					<c:if test="${allNotOne == false}" >
					<th>
						<div class="input-group date date-picker" data-category="<c:out value="" />" data-multidate="true"
								data-orientation='top' data-date-startdate="${surveyMonth.startDateStr}" data-date-enddate="${surveyMonth.getEndDateStr()}"
								 data-target="[name='unCatDateDisplay'][data-category='<c:out value="" />']" data-input="[name='unCatBatchCollectionDateUpdate'][data-category='<c:out value="" />']">
							<input name='unCatBatchCollectionDateUpdate' data-category='<c:out value="" />' class='form-control assignmentAttr-collectionDate'  style="display: none" />
							<select class="form-control select2 assignmentAttr-collectionDate" name="unCatDateDisplay" data-category='<c:out value="" />' disabled multiple style="display:none"></select>
							<div class="input-group-addon">
								<i class="fa fa-calendar"></i>
							</div>
						</div>
						<button type="button" class="btn btn-default btn-copy-unCat" data-copy="unCatBatchCollectionDateUpdate" data-category="<c:out value="" />">Copy to below</button>
					</th>
					<th>
						<div class="col-md-12" >
							<label class="radio-inline"><input type="radio" name="unCatBatchSessionUpdate" value="A" data-category="<c:out value="" />">A</label>
							<label class="radio-inline"><input type="radio" name="unCatBatchSessionUpdate" value="P" data-category="<c:out value="" />">P</label>
						</div>
						<button type="button" class="btn btn-default btn-copy-unCat" data-copy="unCatBatchSessionUpdate" data-category="<c:out value="" />">Copy to below</button>
					</th>
					</c:if>
					<th>
						<div class="input-group">		
							<input type="hidden" class="form-control" name="unCatBatchOfficerUpdate" value="<c:out value="${model.user.userId}" />"  data-category="<c:out value="" />"/>
							<select class="form-control select2 unCatBatchOfficerUpdate" name="unCatBatchOfficerUpdate-userId"  style="display:none" data-category="<c:out value="" />">
								<option value=""></option>
								<c:forEach items="${officerList}" var="officer">
									<option value="<c:out value="${officer.userId}" />">${officer.staffCode} - ${officer.chineseName} (${officer.destination}) </option>
								</c:forEach>
							</select>
							<div class="input-group-addon unCatBatchOfficerUpdate" data-category="<c:out value="" />">
								<i class="fa fa-search"></i>
							</div>
						</div>
						<button type="button" class="btn btn-default btn-copy-unCat" data-copy="unCatBatchOfficerUpdate" data-category="<c:out value="" />">Copy to below</button>
					</th>
					<c:if test="${allOne == false}" >
					<th>
						<select class="form-control select2" name="unCatBatchAllocationUpdate" data-category="<c:out value="" />">
							<c:forEach var="allocationBatch" items="${allocationBatchList}">
								<option value="<c:out value="${allocationBatch.getId()}" />">${batch.getNumberOfBatch()} ( ${allocationBatch.getStartDateStr()} - ${allocationBatch.getEndDateStr()} )</option>
							</c:forEach>
							<c:forEach var="allocationBatch" items="${newAllocationBatchList}">
								<option value="new-${allocationBatch.getId()}">${allocationBatch.getNumberOfBatch()} ( ${allocationBatch.getStartDateStr()} - ${allocationBatch.getEndDateStr()} )</option>
							</c:forEach>
						</select>
						<button type="button" class="btn btn-default btn-copy-unCat" data-copy="unCatBatchAllocationUpdate" data-category="<c:out value="" />">Copy to below</button>
					</th>
					<th class="text-center action"></th>
					</c:if>
					
				</tr>
				</thead>
				<tbody id="allocationBatchBody">
					<c:if test="${fn:length(nonCateAssignmentAttr) gt 0}">
					<c:forEach var="uncateAA" items="${nonCateAssignmentAttr}">
					<tr class="batch-row">
						<td>
							<c:forEach items="${batchCategory.getBatchList()}" var="batch">
								<c:if test="${batch.batchId == uncateAA.getBatchId()}">${batch.code}</c:if>
							</c:forEach>
							<input type="hidden" name="nonCateAssignmentAttr[${uncateAA.getReferenceId()}].batchId" value="<c:out value="${uncateAA.getBatchId()}" />">
							<input type="hidden" name="nonCateAssignmentAttr[<c:out value="${uncateAA.getReferenceId()}" />].referenceId" value="<c:out value="${uncateAA.getReferenceId()}" />">
							<input type="hidden" name="nonCateAssignmentAttr[<c:out value="${uncateAA.getReferenceId()}" />].selectedBatchType" value="<c:out value='${uncateAA.getSelectedBatchType()}'/>"/>
						</td>
						<c:if test="${allOne == false}" >
						<td>
							<div class="input-group">
								<input name='nonCateAssignmentAttr[${uncateAA.getReferenceId()}].startDateStr' data-category="${uncateAA.getCategory()}" class='assignmentAttr-startDate form-control <c:if test="${uncateAA.getSelectedBatchType() != 1}">date-picker</c:if>'
									data-row-error-class="row-error-startDate" 
									<c:if test="${readonly}">readonly disabled</c:if> data-orientation='top' required data-date-startdate="${surveyMonth.getStartDateStr()}" data-date-enddate="${surveyMonth.getEndDateStr()}" value="${uncateAA.getStartDateStr()}"
									<c:if test="${uncateAA.getSelectedBatchType() == 1}">disabled</c:if>/>
								<div class="input-group-addon <c:if test="${uncateAA.getSelectedBatchType() == 1}">disabled</c:if>">
									<i class="fa fa-calendar"></i>
								</div>
							</div>
						</td>
						<td>
							<div class="input-group">
								<input name='nonCateAssignmentAttr[${uncateAA.getReferenceId()}].endDateStr' data-category="${uncateAA.getCategory()}" class='assignmentAttr-endDate form-control <c:if test="${uncateAA.getSelectedBatchType() != 1}">date-picker</c:if>'
								data-row-error-class="row-error-endDate" 
									<c:if test="${readonly}">readonly disabled</c:if> data-orientation='top' required data-date-startdate="${surveyMonth.getStartDateStr()}" data-date-enddate="${surveyMonth.getEndDateStr()}" value="${uncateAA.getEndDateStr()}"
									<c:if test="${uncateAA.getSelectedBatchType() == 1}">disabled</c:if>/>
								<div class="input-group-addon <c:if test="${uncateAA.getSelectedBatchType() == 1}">disabled</c:if>">
									<i class="fa fa-calendar"></i>
								</div>
							</div>
						</td>
						</c:if>
						<c:if test="${allNotOne == false}" >
						<td>
							<div class="input-group date <c:if test="${uncateAA.getSelectedBatchType() == 1}">date-picker</c:if> collectionDatePicker" data-category="${uncateAA.getCategory()}" data-multidate="true"
									<c:if test="${readonly}">readonly disabled</c:if>	data-orientation='top' data-date-startdate="${surveyMonth.startDateStr}" data-date-enddate="${surveyMonth.getEndDateStr()}"
										 data-target="[name='nonCateAssignmentAttr[${uncateAA.getReferenceId()}].collectionDateDisplay']" data-input="[name='nonCateAssignmentAttr[${uncateAA.getReferenceId()}].collectionDatesStr']">
								<input type="text" <c:if test="${readonly}">readonly disabled</c:if> name='nonCateAssignmentAttr[${uncateAA.getReferenceId()}].collectionDatesStr' class='assignmentAttr-collectionDate' style="position: absolute; z-index: -1" data-category="${uncateAA.getCategory()}" value="<c:out value="${uncateAA.getCollectionDatesStr()}" />"  data-row-error-class="row-error-collectionDate"  data-rule-collectionDateRequired="true"/>
								<c:set var="collectionDateList" value="${fn:split(uncateAA.getCollectionDatesStr(), ',')}" />
								<select <c:if test="${readonly}">readonly disabled</c:if> class="select2" name="nonCateAssignmentAttr[${uncateAA.getReferenceId()}].collectionDateDisplay" disabled multiple required style="width: 0; height: 0;" data-category="${uncateAA.getCategory()}">
									<c:forEach items="${collectionDateList}" var="date">
									<c:if test="${date != '' }">
									<option value="<c:out value="${date}" />" selected>${date}</option>
									</c:if>
									</c:forEach>
								</select>
								<div class="input-group-addon <c:if test="${uncateAA.getSelectedBatchType() != 1}">disabled</c:if>" >
									<i class="fa fa-calendar"></i>
								</div>
							</div>
						</td>
						<td>
							<div class="col-md-12 radio-group" >
								<label class="radio-inline"><input class="assignmentAttr-session" type="radio" name="nonCateAssignmentAttr[${uncateAA.getReferenceId()}].session" value="A" data-category="${uncateAA.getCategory()}" data-rule-sessionRequired="true" data-row-error-class="row-error-session" <c:if test="${uncateAA.getSession() == 'A'}">checked</c:if>>A</label>
								<label class="radio-inline"><input class="assignmentAttr-session" type="radio" name="nonCateAssignmentAttr[${uncateAA.getReferenceId()}].session" value="P" data-category="${uncateAA.getCategory()}" data-rule-sessionRequired="true" data-row-error-class="row-error-session" <c:if test="${uncateAA.getSession() == 'P'}">checked</c:if>>P</label>
							</div>
						</td>
						</c:if>
						<td>
							<div class="input-group" style="width:100%">		
								<input <c:if test="${readonly}">readonly disabled</c:if> type="hidden" class="form-control assignmentAttr-officerIds" name="nonCateAssignmentAttr[${uncateAA.getReferenceId()}].officers"  data-row-error-class="row-error-officerIds"  value="<c:out value="${uncateAA.getOfficerIds()}" />" required data-category="${uncateAA.getCategory()}" <c:if test="${uncateAA.getSelectedBatchType() == 3}">disabled</c:if>/>
								<select <c:if test="${readonly}">readonly disabled</c:if> class="form-control select2 officer assignmentAttr-officerIds" name="nonCateAssignmentAttr[${uncateAA.getReferenceId()}].officerIds"  data-row-error-class="row-error-officerIds"  required style="" data-category="${uncateAA.getCategory()}" <c:if test="${uncateAA.getSelectedBatchType() == 3}">disabled</c:if>>
									<option value=""></option>
									<c:forEach items="${officerList}" var="officer">
										<option value="<c:out value="${officer.userId}" />" <c:if test="${uncateAA.getOfficerIds() == officer.userId }">selected</c:if> >${officer.staffCode} - ${officer.chineseName} (${officer.destination})</option>
									</c:forEach>
								</select>
								<div class="input-group-addon <c:if test="${readonly == false}">newOfficer</c:if> <c:if test="${uncateAA.getSelectedBatchType() == 3}">hidden</c:if>" data-category="${uncateAA.getCategory()}">
									<i class="fa fa-search"></i>
								</div>
							</div>
						</td>
						<c:if test="${allOne == false}" >
						<td>
							<select <c:if test="${readonly}">readonly disabled</c:if> class="form-control select2 assignmentAttr-allocationBatch" name="nonCateAssignmentAttr[${uncateAA.getReferenceId()}].allocationBatchRefId" data-category="${uncateAA.getCategory()}" data-row-error-class="row-error-allocationBatch"  data-rule-allocationBatchRefIdRequired="true">
								<option value=""></option>
								<c:forEach var="allocationBatch" items="${allocationBatchList}">
									<option value="<c:out value="${allocationBatch.getId()}" />" data-start-date="${allocationBatch.getStartDateStr()}" data-end-date="${allocationBatch.getEndDateStr()}"
										<c:if test="${allocationBatch.getId() == uncateAA.getAllocationBatchRefId()}">selected</c:if>>
										${allocationBatch.getNumberOfBatch()} ( ${allocationBatch.getStartDateStr()} - ${allocationBatch.getEndDateStr()} )
									</option>
								</c:forEach>
								<c:forEach var="allocationBatch" items="${newAllocationBatchList}">
									<option value="new-${allocationBatch.getId()}" data-start-date="${allocationBatch.getStartDateStr()}" data-end-date="${allocationBatch.getEndDateStr()}"
										<c:if test="${allocationBatch.getId() == fn:replace(uncateAA.getAllocationBatchRefId(), 'new-', '') && not empty fn:replace(uncateAA.getAllocationBatchRefId(), 'new-', '')}">selected</c:if>>
										${allocationBatch.getNumberOfBatch()}  ( ${allocationBatch.getStartDateStr()} - ${allocationBatch.getEndDateStr()} ) 
									</option>
								</c:forEach>
							</select>
						</td>
						</c:if>
						<td class="text-center action"></td>
					</tr>
					</c:forEach>
					</c:if>
					<c:if test="${fn:length(nonCateAssignmentAttr) eq 0}">
						<tr>
							<td colspan="8">No data available in table</td>
						</tr>
					</c:if>
				</tbody>
			</table>
		</div>
	</div>
	</c:if>
	</div>
	</c:forEach>
</form>
