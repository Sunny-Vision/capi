<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div class="row">
	<form method="post" action="<c:url value='/masterMaintenance/BusinessParameterMaintenance/saveWorkingSessions'/>" id="sessionForm" >
		<div class="col-md-12">
			<div class="form-horizontal">
				<h4>Working Session</h4>
				<div class="col-sm-12" id="workingSessionList">
					<c:forEach items="${displayModel.getWorkingSessionList()}" var="workingSession">
					<div class="form-group" data-id="${workingSession.getId()}">
						<label class="col-sm-2 control-label">From:</label>
						<div class="col-sm-3">
							<input name="workingSessions[${workingSession.getId()}].id" type="hidden" class="form-control" value="${workingSession.getId()}"/>
							<div class="input-group bootstrap-timepicker">
								<input name="workingSessions[${workingSession.getId()}].fromTime" type="text" class="form-control timepicker" value="<c:out value="${workingSession.getFromTime()}" />"/>
								<div class="input-group-addon">
									<i class="fa fa-clock-o"></i>
								</div>
							</div>
						</div>
						<label class="col-sm-2 control-label">To:</label>
						<div class="col-sm-3">
							<div class="input-group bootstrap-timepicker">
								<input name="workingSessions[${workingSession.getId()}].toTime" type="text" class="form-control timepicker" value="<c:out value="${workingSession.getToTime()}" />"/>
								<div class="input-group-addon">
									<i class="fa fa-clock-o"></i>
								</div>
							</div>
						</div>
						<div class="col-sm-2">
							<button type="button" class="btn btn-info pull-right" onclick="deleteWorkingSession($(this))" data-id="${workingSession.getId()}">Delete</button>
						</div>
					</div>
					</c:forEach>
				</div>
				<button type="button" class="btn btn-info addWorkingSession">Add Session</button>
			</div>
			<sec:authorize access="hasPermission(#user, 256)">
			<hr/>
			<div class="form-group">
				<button type="submit" class="btn btn-info">Submit</button>
			</div>
			</sec:authorize>
		</div>
	</form>
</div>