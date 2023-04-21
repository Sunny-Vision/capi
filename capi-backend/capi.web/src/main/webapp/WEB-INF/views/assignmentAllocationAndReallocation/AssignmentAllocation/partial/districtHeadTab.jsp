<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<form class="form-horizontal" action="<c:url value='/assignmentAllocationAndReallocation/SurveyMonth/updatePartial'/>" method="post" role="form" id="mainForm">
<div class="row">
	<div class="col-md-offset-3 col-md-6" >
		<table class="table table-striped table-bordered table-hover">
		<thead>
			<tr>
				<th>District</th>
				<th>Field Officer</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${districtList}" var="district">
			<tr>
				<th><c:out value="${district.getCode()}"></c:out></th>
				<th>
					<div class="input-group" style="width:100%">
						<input type="hidden" value="${district.getDistrictId()}" name="districtHeadTabModel[${district.getDistrictId()}].districtId" required>						
						<select class="form-control select2 officer district-officerId" name="districtHeadTabModel[${district.getDistrictId()}].userId" required>
							<option value=""></option>
							<c:forEach items="${officerList}" var="officer">
								<option value="<c:out value="${officer.userId}"/>" <c:if test="${district.getUserId()== officer.getUserId()}">selected</c:if> >
								${officer.staffCode} - <c:choose><c:when test="${empty officer.chineseName}">${officer.englishName}</c:when><c:otherwise>${officer.chineseName}</c:otherwise></c:choose> (${officer.destination}) 
								</option>
							</c:forEach>
						</select>
						<div class="input-group-addon searchOfficer">
							<i class="fa fa-search"></i>
						</div>
					</div>
				</th>
			</tr>
		</c:forEach>
		</tbody>
		</table>
	</div>
</div>
</form>