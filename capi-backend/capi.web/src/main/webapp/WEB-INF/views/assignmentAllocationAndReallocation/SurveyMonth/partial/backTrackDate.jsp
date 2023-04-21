<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<div class="row">
	<div class="col-md-12" >
		<form class="form-horizontal" action="" method="post" role="form" id="backTrackDateForm">
			<table class="table table-striped table-bordered table-hover">
				<thead>
				<tr>
					<th width="32%">Batch Code</th>
					<th width="32%">Collection Date</th>
					<th width="4%">BackTrack</th>
					<th width="32%">BackTrackDate</th>
				</tr>
				</thead>
				<tbody>
					<c:forEach items="${displayList}" var="displayModel">
					<c:if test="${fn:length(displayModel.getBackTrackDayList()) gt 0}">
						<c:set var="first" value="${true}"/>
						<c:set var="idx" value="1" />
						<c:forEach items="${displayModel.getBackTrackDayList()}" var="data">
							<c:choose>
								<c:when test="${idx == fn:length(displayModel.getBackTrackDayList())}">
									<tr style="border-bottom: solid;" >
								</c:when>
								<c:otherwise>
									<tr>
								</c:otherwise>
							</c:choose>
							<c:if test="${first == true}">
							<c:set var="first" value="${false}"/>
							<td rowspan="${fn:length(displayModel.getBackTrackDayList())}"><h3><c:out value="${displayModel.getBatchCode()}" /></h3></td>
							</c:if>
							<td>
								<c:out value="${data.getReferenceCollectionDateStr()}" />
							</td>
							<td>
								<input name="backTrackDate[<c:out value='${data.getReferenceId()}' />].referenceId" type="hidden" value="<c:out value='${data.getReferenceId()}'/>"/>
								<input name="backTrackDate[<c:out value='${data.getReferenceId()}' />].batchCode" type="hidden" value="<c:out value="${displayModel.getBatchCode()}" />"/>
								<input name="backTrackDate[<c:out value='${data.getReferenceId()}' />].isBackTrack" class="backTrackDateTrigger" type="checkbox" value="1" <c:if test="${data.getHasBackTrack()}">checked</c:if> 
									<c:if test="${readonly}">readonly disabled</c:if> />
							</td>
							<td>
								<div class="input-group date <c:if test="${data.getHasBackTrack() && readonly == false}">date-picker</c:if> backTrackDatePicker" 
									data-multidate="3" data-orientation='top' 
									data-date-startdate="<c:out value="${data.getBackTrackDateAvailableFromString()}"/>"
									data-date-enddate="<c:out value="${data.getBackTrackDateAvailableToString()}"/>"
									data-date-collectiondate="<c:out value="${data.getReferenceCollectionDateStr()}"/>"
									data-date-datesdisabled="<c:out value="${data.getBackTrackDateAvailableSkipString()}"/>"
									data-date-prevworkingdate="<c:out value="${data.getPrevBackTrackDateString()}"/>"
									data-target="[name='backTrackDate[<c:out value='${data.getReferenceId()}' />].backTrackDateDisplay']" 
									data-input="[name='backTrackDate[<c:out value='${data.getReferenceId()}' />].backTrackDateStr']">
									<input type="hidden" name='backTrackDate[<c:out value='${data.getReferenceId()}' />].backTrackDateStr' class='backTrack-backTrackDates' 
										style="width: 0; height: 0; padding: 0; margin: 0; border: none;"
										value="<c:out value="${data.getBackTrackDateString()}"/>"/>
									<select class="select2" name="backTrackDate[<c:out value='${data.getReferenceId()}' />].backTrackDateDisplay" disabled multiple required style="width: 0; height: 0;">
										<c:forEach items="${data.getBackTrackDateList()}" var="backTrackDate">
										<option selected value="<fmt:formatDate pattern="dd-MM-yyyy" value="${backTrackDate}" />"><fmt:formatDate pattern="dd-MM-yyyy" value="${backTrackDate}" /></option>
										</c:forEach>
									</select>
									<div class="input-group-addon <c:if test="${!data.getHasBackTrack()}">disabled</c:if>">
										<i class="fa fa-calendar"></i>
									</div>
								</div>
							</td>
						</tr>
						<c:set var="idx" value="${idx + 1}" />
						</c:forEach>
					</tr>
					</c:if>
					</c:forEach>
				</tbody>
			</table>
		</form>
	</div>
</div>