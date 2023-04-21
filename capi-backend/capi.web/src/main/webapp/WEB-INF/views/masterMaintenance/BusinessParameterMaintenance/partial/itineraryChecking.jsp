<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div class="row">
	<form method="post" action="<c:url value='/masterMaintenance/BusinessParameterMaintenance/saveItineraryParameters'/>" id="itineraryForm" >
		<div class="col-md-12">
			<div class="form-horizontal">
				<div class="col-sm-12">
					<div class="form-group">
						<div class="col-sm-6 col-sm-offset-1">
							<label>
							<div class="input-group">
								<div class="checkbox">
									<input type="checkbox" name="assignmentDeviation" value="1" <c:if test="${displayModel.getItineraryNoofAssignmentDeviation() == '1'}">checked</c:if> >
								</div>
								<div class="input-group-addon filter-officer">
									No. Of assignment deviation:
								</div>
								<%--
								<div class="input-group-addon filter-officer">
									+
								</div>	
								--%>
								<input type="text" class="form-control numbers" name="assignmentDeviationPlus" required value="<c:out value="${displayModel.getItineraryNoofAssignmentDeviationPlus()}" />"/>
								<div class="input-group-addon filter-officer">
									%
								</div>
								
								<%--
								<div class="input-group-addon filter-officer">
									-
								</div>	
								<input type="text" class="form-control numbers" name="assignmentDeviationMinus" required value="<c:out value="${displayModel.getItineraryNoofAssignmentDeviationMinus()}" />"/>
								<div class="input-group-addon filter-officer">
									%
								</div>
								 --%>
							</div>
							</label>
						</div>
					</div>
					<div class="form-group">
						<div class="col-sm-6 col-sm-offset-1">
							<label>
							<div class="input-group">
								<div class="checkbox">
									<input type="checkbox" name="sequenceDeviation"  value="1" <c:if test="${displayModel.getItinerarySequenceDeviation() == '1'}">checked</c:if>>
								</div>
								<div class="input-group-addon filter-officer">
									Sequence Deviation:
								</div>
								<input type="text" class="form-control numbers" name="sequenceDeviationPercentage" required value="<c:out value="${displayModel.getItinerarySequencePercents()}" />"/>
								<div class="input-group-addon filter-officer">
									%
								</div>
							</div>
							</label>
						</div>
					</div>
					<div class="form-group">
						<div class="col-sm-6 col-sm-offset-1">
							<label>
							<div class="input-group">
								<div class="checkbox">
									<input type="checkbox" name="tpuSequenceDeviation" value="1" <c:if test="${displayModel.getItineraryTPUSequenceDeviation() == '1'}">checked</c:if>>
								</div>
								<div class="input-group-addon filter-officer">
									TPU Sequence Deviation:
								</div>
								<input type="text" class="form-control digits" name="tpuSequenceDeviationTimes" required value="<c:out value="${displayModel.getItineraryTPUSequenceDeviationTimes()}" />"/>
								<div class="input-group-addon filter-officer">
									times
								</div>
							</div>
							</label>
						</div>
					</div>
					<sec:authorize access="hasPermission(#user, 256)">
						<hr/>
						<div class="form-group">
							<button type="submit" class="btn btn-info">Submit</button>
						</div>
					</sec:authorize>
				</div>
			</div>
		</div>
	</form>
</div>