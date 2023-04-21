<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<form class="form-horizontal" action="<c:url value='/assignmentAllocationAndReallocation/SurveyMonth/updatePartial'/>" method="post" role="form" id="mainForm">
<div class="row">
	<div class="col-md-12" >
		<table class="table table-striped table-bordered table-hover data-table">
		<thead>
			<tr>
				<th>Field Officer</th>
				<th>No. of Assignments</th>
				<th>Responsible District</th>
				<th>Available Man-Day</th>
				<th>Man-Day Required for responsible district</th>
				<th>Manual Adjustment</th>
				<th>Adjusted Man-day Required for responsible district</th>
				<th>Man day of transfer<br/>in(+) / out (-)</th>
				<th>Man-day Balance</th>
				<th></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${districtHeadList}" var="dh">
			<tr>
				<td class="text-center">
					<c:out value="${dh.getUserName()}"></c:out>
					<input type="hidden" name="districtHead[${dh.getUserId()}].userId" value="${dh.getUserId()}"/>
					<input type="hidden" name="districtHead[${dh.getUserId()}].userName" value="${dh.getUserName()}"/>
				</td>
				<td class="text-center">
					<c:out value="${dh.getTotalAssignment()}"></c:out>(<c:out value="${dh.getNoOfAssignment()}"></c:out>)
					<input type="hidden" name="districtHead[${dh.getUserId()}].totalAssignment" value="${dh.getTotalAssignment()}"/>
					<input type="hidden" name="districtHead[${dh.getUserId()}].noOfAssignment" value="${dh.getNoOfAssignment()}"/>
				</td>
				<td class="text-center">
					<c:out value="${dh.getDistricts()}"></c:out>
					<input type="hidden" name="districtHead[${dh.getUserId()}].districts" value="${dh.getDistricts()}"/>
				</td>
				<td class="text-center">
					<c:out value="${dh.getAvailableManDays()}"></c:out>
					<input class="availableManDays" data-user="${dh.getUserId()}" type="hidden" name="districtHead[${dh.getUserId()}].availableManDays" value="${dh.getAvailableManDays()}"/>
				</td>
				<td class="text-center">
					<c:out value="${dh.getManDayRequiredForResponsibleDistricts()}"></c:out>
					<input data-user="${dh.getUserId()}" type="hidden" name="districtHead[${dh.getUserId()}].manDayRequiredForResponsibleDistricts" value="${dh.getManDayRequiredForResponsibleDistricts()}"/>
					<input class="manDayRequired" data-user="${dh.getUserId()}" type="hidden" name="districtHead[${dh.getUserId()}].manDayRequired" value="${dh.getManDayRequired()}"/>
				</td>
				<td class="text-center">
					<input class="form-control manualAdjustment" name="districtHead[${dh.getUserId()}].manualAdjustment" value="<c:out value="${dh.getManualAdjustment()}"></c:out>">
				</td>
				<td class="text-center">
					<span class="adjustedManDay" data-districtHeadId="${dh.getUserId()}">${dh.getAdjustedManDayRequiredForResponsibleDistricts()}</span>
					<input class="adjustedManDay" data-user="${dh.getUserId()}" data-original="${dh.getManDayRequired()}" type="hidden" name="districtHead[${dh.getUserId()}].adjustedManDayRequiredForResponsibleDistricts" value="${dh.getAdjustedManDayRequiredForResponsibleDistricts()}"/>
				</td>
				<td class="text-center">
					<span class="manDayTransfer" data-districtHeadId="${dh.getUserId()}">${dh.getManDayOfTransferInOut()==0 ? 0.0 : dh.getManDayOfTransferInOut()*-1}</span>
					<input class="manDayTransfer" data-user="${dh.getUserId()}" type="hidden" name="districtHead[${dh.getUserId()}].manDayOfTransferInOut" value="${dh.getManDayOfTransferInOut()}"/>
				</td>
				<td class="text-center">
					<span class="manDayOfBalance" data-districtHeadId="${dh.getUserId()}">${dh.getManDayOfBalance()}</span>
					<input class="manDayOfBalance" data-user="${dh.getUserId()}" type="hidden" name="districtHead[${dh.getUserId()}].manDayOfBalance" value="${dh.getManDayOfBalance()}"/>
				</td>
				<td class="text-center">
					<a href="#" onclick="javascript:addAdjustment(${dh.getUserId()})">Add Adjustment Record</a>
				</td>
			</tr>
			</c:forEach>
		</tbody>
		</table>
	</div>
</div>
<hr/>
<div class="row">
	<div class="col-md-12">
		<h3>Adjustment</h3>
	</div>
	<div class="col-md-12" >
		<table class="table table-striped table-bordered table-hover" id="adjustmentTable">
		<thead>
			<tr>
				<th>From Field Officer</th>
				<th>To Field Officer</th>
				<th>Man-Day</th>
				<th>Remark</th>
				<th></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${adjustmentModelList}" var="adjustment" varStatus="loop">
			<tr>
				<td class='text-center'><c:out value="${adjustment.fromUserName}"/>
					<input type='hidden' class='adjustment fromUser' data-row='${loop.index}' name='adjustment[${loop.index}].fromUserId' value='${adjustment.fromUserId}'>
					<input type='hidden' name='adjustment[${loop.index}].fromUserName' value='${adjustment.fromUserName}'>
				</td>
				<td class='text-center'><c:out value="${adjustment.toUserName}"/>
					<input type='hidden' class='adjustment toUser' data-row='${loop.index}' name='adjustment[${loop.index}].toUserId' value='${adjustment.toUserId}'>
					<input type='hidden' name='adjustment[${loop.index}].toUserName' value='${adjustment.toUserName}'>
				</td>
				<td class='text-center'>${adjustment.manDay}<input type='hidden' class='adjustment manDay' data-row='${loop.index}' name='adjustment[${loop.index}].manDay' value='${adjustment.manDay}'></td>
				<td class='text-center'>${adjustment.remark}<input type='hidden' class='adjustment remark' name='adjustment[${loop.index}].remark' value='${adjustment.remark}'></td>
				<td class='text-center'><a href='javascript:void(0)' onclick='deleteRecordsWithConfirm(this)' class='table-btn btn-delete'><span class='fa fa-times' aria-hidden='true'></span></a></td>
			</tr>
			</c:forEach>
		</tbody>
		</table>
	</div>
</div>
</form>
<div class="modal fade" id="adjustmentFormModal" tabindex="-1" role="dialog" aria-labelledby="dialogLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<form id="adjustmentForm">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
					<h4 class="modal-title" id="dialogLabel">Add Adjustment</h4>
				</div>
				<div class="modal-body form-horizontal">
					<div class="form-group">
						<label class="col-md-3 control-label">From User</label>
						<div class="col-md-9">
							<div class="input-group">
								<select class="form-control select2 officer fromUser" name="fromUser" required>
									<option value=""></option>
									<c:forEach items="${officerList}" var="officer">
										<option value="<c:out value="${officer.userId}"/>" >
										${officer.staffCode} - <c:choose><c:when test="${empty officer.chineseName}">${officer.englishName}</c:when><c:otherwise>${officer.chineseName}</c:otherwise></c:choose> (${officer.destination}) 
										</option>
									</c:forEach>
								</select>
								<div class="input-group-addon searchOfficer">
									<i class="fa fa-search"></i>
								</div>
							</div>
						</div>
					</div>
					<div class="form-group">
						<label class="col-md-3 control-label">To User</label>
						<div class="col-md-9">
							<div class="input-group">
								<select class="form-control select2 officer toUser UserChecking" name="toUser" required>
									<option value=""></option>
									<c:forEach items="${officerList}" var="officer">
										<option value="<c:out value="${officer.userId}"/>" >
										${officer.staffCode} - <c:choose><c:when test="${empty officer.chineseName}">${officer.englishName}</c:when><c:otherwise>${officer.chineseName}</c:otherwise></c:choose> (${officer.destination}) 
										</option>
									</c:forEach>
								</select>
								<div class="input-group-addon searchOfficer">
									<i class="fa fa-search"></i>
								</div>
							</div>
						</div>
					</div>
					<div class="form-group">
						<label class="col-md-3 control-label">Man Days</label>
						<div class="col-md-9"><input name="manDay" type="text" class="form-control validateNumber" required></div>
					</div>
					<div class="form-group">
						<label class="col-md-3 control-label">Remark</label>
						<div class="col-md-9"><input name="remark" type="text" class="form-control"></div>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
					<button type="submit" class="btn btn-primary" >Submit</button>
				</div>
			</form>
		</div>
	</div>
</div>