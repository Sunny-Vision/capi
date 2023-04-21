<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<fmt:formatDate value="${displayModel.fromDate}" var="currMonthStr" type="date" pattern="MM" />
<fmt:formatDate value="${displayModel.fromDate}" var="currMonthYearStr" type="date" pattern="YYYY" />
<fmt:formatDate value="${displayModel.nextMonth}" var="nextMonthStr" type="date" pattern="MM" />
<fmt:formatDate value="${displayModel.nextMonth}" var="nextMonthYearStr" type="date" pattern="YYYY" />
<fmt:formatDate value="${displayModel.previousMonth}" var="prevMonthStr" type="date" pattern="MM" />
<fmt:formatDate value="${displayModel.previousMonth}" var="prevMonthYearStr" type="date" pattern="YYYY" />
<fmt:formatDate value="${displayModel.fromDate}" var="monthStr" type="date" pattern="MMMM YYYY" />

<div class="box">
	<div class="box-body">
		<div class="row">
			<a name="staffCalendar"></a>
			<div class="col-md-4">
				<div class="clearfix">&nbsp;</div>
				<a class="btn btn-info" href="<c:url value='/' />?selectedUserId=${model.selectedUserId}&showCalendar=0">Hide Calendar</a>
				<sec:authorize access="hasRole('UF1303')">
				<a class="btn btn-info" href="<c:url value='/assignmentAllocationAndReallocation/StaffCalendar/home'/>?clearSession=1">Staff Calendar</a>
				</sec:authorize>
			</div>
			<div class="col-md-4 text-center">
				<H2>${monthStr}</H2>
			</div>
			<div class="col-md-4">
				<div class="clearfix">&nbsp;</div>
				<a class="btn btn-info pull-right" href="<c:url value='/nevigateCalendar'/>?year=${nextMonthYearStr}&month=${nextMonthStr}&selectedUserId=${model.selectedUserId}&showCalendar=${showCalendar}">&gt;</a>
				<span class="pull-right">&nbsp;</span>
				<a class="btn btn-info pull-right" href="<c:url value='/nevigateCalendarCurrent'/>?selectedUserId=${model.selectedUserId}&showCalendar=${showCalendar}">Current</a>
				<span class="pull-right">&nbsp;</span>
				<a class="btn btn-info pull-right" href="<c:url value='/nevigateCalendar'/>?year=${prevMonthYearStr}&month=${prevMonthStr}&selectedUserId=${model.selectedUserId}&showCalendar=${showCalendar}">&lt;</a>
			</div>
		</div>
		
								<div class="row">
				        			<div class="col-md-12">
				        				<table class="table table-bordered overflow-x" id="staffCalendarTable">
				        					<thead>
				        					<tr>
				        						<th colspan="2" class="staffHeadCol">
				        							Staff Code
				        						</th>
				        						<c:forEach var="i" items="${displayModel.dateList}">
				        							<fmt:formatDate value="${i}" var="dateStr" type="date" pattern="dd" />
				        							<fmt:formatDate value="${i}" var="dayOfweek" type="date" pattern="E" />
				        							<c:set var="isHoliday" value="0"/>
				        							<c:set var="holidayName" value=""/>
				        							<c:if test="${fn:toLowerCase(dayOfweek) == 'sun' || fn:toLowerCase(dayOfweek) == 'sat'}">
				        								<c:set var="isHoliday" value="1"/>
				        								<c:set var="holidayName" value="${dayOfweek}"/>
				        							</c:if>
				        							<c:if test="${fn:toLowerCase(dayOfweek) != 'sun' && fn:toLowerCase(dayOfweek) != 'sat'}">
														<c:forEach var="j" items="${displayModel.calendarHolidayList}">
															<fmt:formatDate value="${j.eventDate}" var="holidayDateStr" type="date" pattern="dd" />
					        								<c:if test="${dateStr == holidayDateStr}">
					        									<c:set var="isHoliday" value="1"/>
					        									<c:set var="holidayName" value="${j.remark}"/>
					        								</c:if>
					        							</c:forEach>
				        							</c:if>
				        							<c:if test="${isHoliday == 0}">
					        							<th class="dateCol">${dateStr}<br/><div class="weekdayStr">${dayOfweek}</div></th>
				        							</c:if>
				        							<c:if test="${isHoliday == 1}">
					        							<th class="dateCol holiday" title="${holidayName}">${dateStr}<br/><div class="weekdayStr">${dayOfweek}</div></th>
				        							</c:if>
												</c:forEach>
				        					</tr>
				        					</thead>
				        					<tbody>
				        						<c:forEach items="${displayModel.userList}" var="staff">
					        						<tr>
					        							<th
					        								<sec:authorize access="hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 8)">
					        								<c:if test="${displayModel.showEdit == 1}">
					        								rowspan="6"
					        								</c:if>
					        								<c:if test="${displayModel.showEdit == 0}">
					        								rowspan="3"
					        								</c:if>
					        								</sec:authorize>
					        								<sec:authorize access="!hasPermission(#user, 2) and !hasPermission(#user, 4) and !hasPermission(#user, 8)">
					        								rowspan="3"
					        								</sec:authorize>
					        								class="staffCodeCol">
							        						<p class="text-center">${staff.staffCode}<br/>${staff.chineseName}</p>
						        						</th>
						        						<th class="sessionCol"
						        							<sec:authorize access="hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 8)">
						        								<c:if test="${displayModel.showEdit == 1}">
						        								rowspan="2"
						        								</c:if>
					        								</sec:authorize>
						        						>
							        						<div class="text-center">A</div>
						        						</th>
						        						<c:forEach var="i" items="${displayModel.dateList}">
						        							<fmt:formatDate value="${i}" var="dayOfweek" type="date" pattern="E" />
				        									<fmt:formatDate value="${i}" var="dateStr" type="date" pattern="dd" />
															<fmt:formatDate value="${i}" var="dayStr" type="date" pattern="dd-MM-yyyy" />
															
															<c:set var="isHoliday" value="0"/>
						        							<c:set var="holidayName" value=""/>
						        							<c:if test="${fn:toLowerCase(dayOfweek) == 'sun' || fn:toLowerCase(dayOfweek) == 'sat'}">
						        								<c:set var="isHoliday" value="1"/>
						        								<c:set var="holidayName" value="${dayOfweek}"/>
						        							</c:if>
						        							<c:if test="${fn:toLowerCase(dayOfweek) != 'sun' && fn:toLowerCase(dayOfweek) != 'sat'}">
																<c:forEach var="j" items="${displayModel.calendarHolidayList}">
																	<fmt:formatDate value="${j.eventDate}" var="holidayDateStr" type="date" pattern="dd" />
							        								<c:if test="${dateStr == holidayDateStr}">
							        									<c:set var="isHoliday" value="1"/>
							        									<c:set var="holidayName" value="${j.remark}"/>
							        								</c:if>
							        							</c:forEach>
						        							</c:if>
						        							<c:if test="${isHoliday == 0}">
							        							<td class="dateCol" data-officer="${staff.userId}" data-date="${dayStr}" data-session="A">
						        							</c:if>
						        							<c:if test="${isHoliday == 1}">
							        							<td class="dateCol holiday" title="${holidayName}" data-officer="${staff.userId}" data-date="${dayStr}" data-session="A">
						        							</c:if>
																<c:forEach items="${displayModel.calendarEventList}" var="calendarEvent">
				        											<fmt:formatDate value="${calendarEvent.eventDate}" var="eventDateStr" type="date" pattern="dd" />
																	<c:if test="${eventDateStr == dateStr && calendarEvent.session == 'A' && calendarEvent.user.userId == staff.userId }">
																		<c:if test="${calendarEvent.activityType == 1}">
																			<sec:authorize access="!hasPermission(#user, 2) and !hasPermission(#user, 4) and !hasPermission(#user, 8)">
																				<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)">OT</a>
																			</sec:authorize>
																			<sec:authorize access="hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 8)">
																				<c:if test="${displayModel.showEdit == 1}">
																					<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" onclick="editEvent($(this))">OT</a>
																				</c:if>
																				<c:if test="${displayModel.showEdit == 0}">
																					<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)">OT</a>
																				</c:if>
																			</sec:authorize>
																		</c:if>
																		<c:if test="${calendarEvent.activityType == 2}">
																			<sec:authorize access="!hasPermission(#user, 2) and !hasPermission(#user, 4) and !hasPermission(#user, 8)">
																				<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" title="TimeOff">TimeO..</a>
																			</sec:authorize>
																			<sec:authorize access="hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 8)">
																				<c:if test="${displayModel.showEdit == 1}">
																					<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" onclick="editEvent($(this))" title="TimeOff">TimeO..</a>
																				</c:if>
																				<c:if test="${displayModel.showEdit == 0}">
																					<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" title="TimeOff">TimeO..</a>
																				</c:if>
																			</sec:authorize>
																		</c:if>
																		<c:if test="${calendarEvent.activityType == 3}">
																			<sec:authorize access="!hasPermission(#user, 2) and !hasPermission(#user, 4) and !hasPermission(#user, 8)">
																				<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" title="${calendarEvent.activityCode.code}">
																					<c:if test="${fn:length(calendarEvent.activityCode.code) > 5}">${fn:substring(calendarEvent.activityCode.code, 0, 5)}...</c:if>
																					<c:if test="${fn:length(calendarEvent.activityCode.code) <= 5}">${calendarEvent.activityCode.code}</c:if>
																				</a>
																			</sec:authorize>
																			<sec:authorize access="hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 8)">
																				<c:if test="${displayModel.showEdit == 1}">
																					<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" onclick="editEvent($(this))" title="${calendarEvent.activityCode.code}">
																						<c:if test="${fn:length(calendarEvent.activityCode.code) > 5}">${fn:substring(calendarEvent.activityCode.code, 0, 5)}...</c:if>
																						<c:if test="${fn:length(calendarEvent.activityCode.code) <= 5}">${calendarEvent.activityCode.code}</c:if>
																					</a>
																				</c:if>
																				<c:if test="${displayModel.showEdit == 0}">
																					<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" title="${calendarEvent.activityCode.code}">
																					<c:if test="${fn:length(calendarEvent.activityCode.code) > 5}">${fn:substring(calendarEvent.activityCode.code, 0, 5)}...</c:if>
																					<c:if test="${fn:length(calendarEvent.activityCode.code) <= 5}">${calendarEvent.activityCode.code}</c:if>
																					</a>
																				</c:if>
																			</sec:authorize>
																		</c:if>
																	</c:if>
																</c:forEach>
																<c:forEach items="${displayModel.assignmentList}" var="assignment">
																	<fmt:formatDate value="${assignment.getEventDate()}" var="assignDateStr" type="date" pattern="dd" />
																	<c:if test="${assignDateStr == dateStr && assignment.getSession() == 'A' && assignment.getUserId() == staff.userId }">
																			<a type="button" class="btn btn-small ${displayModel.getAssignmentEventColor()}" href="javascript:void(0)">${assignment.getCode()}</a>
																	</c:if>
																</c:forEach>
															</td>
														</c:forEach>
					        						</tr>
					        						<sec:authorize access="hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 8)">
					        						<c:if test="${displayModel.showEdit == 1}">
					        						<tr>
					        							<c:forEach var="i" items="${displayModel.dateList}">
						        							<fmt:formatDate value="${i}" var="dayOfweek" type="date" pattern="E" />
				        									<fmt:formatDate value="${i}" var="dateStr" type="date" pattern="dd" />
															<fmt:formatDate value="${i}" var="dayStr" type="date" pattern="dd-MM-yyyy" />
															<c:set var="isHoliday" value="0"/>
						        							<c:set var="holidayName" value=""/>
						        							<c:if test="${fn:toLowerCase(dayOfweek) == 'sun' || fn:toLowerCase(dayOfweek) == 'sat'}">
						        								<c:set var="isHoliday" value="1"/>
						        								<c:set var="holidayName" value="${dayOfweek}"/>
						        							</c:if>
						        							<c:if test="${fn:toLowerCase(dayOfweek) != 'sun' && fn:toLowerCase(dayOfweek) != 'sat'}">
																<c:forEach var="j" items="${displayModel.calendarHolidayList}">
																	<fmt:formatDate value="${j.eventDate}" var="holidayDateStr" type="date" pattern="dd" />
							        								<c:if test="${dateStr == holidayDateStr}">
							        									<c:set var="isHoliday" value="1"/>
							        									<c:set var="holidayName" value="${j.remark}"/>
							        								</c:if>
							        							</c:forEach>
						        							</c:if>
						        							<c:if test="${isHoliday == 0}">
							        							<td class="dateCol" >
						        							</c:if>
						        							<c:if test="${isHoliday == 1}">
							        							<td class="dateCol holiday" title="${holidayName}">
						        							</c:if>
																<button type="button" class="btn btn-small btn-grey btn-add" data-officer="${staff.userId}" data-date="${dayStr}" data-session="A" onclick="selectEvent($(this));">Add</button>
															</td>
														</c:forEach>
					        						</tr>
					        						</c:if>
													</sec:authorize>
					        						<tr>
						        						<th class="sessionCol"
															<sec:authorize access="hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 8)">
						        								<c:if test="${displayModel.showEdit == 1}">
						        								rowspan="2"
						        								</c:if>
					        								</sec:authorize>
					        								>
							        						<div class="text-center">P</div>
						        						</th>
					        							<c:forEach var="i" items="${displayModel.dateList}">
						        							<fmt:formatDate value="${i}" var="dayOfweek" type="date" pattern="E" />
				        									<fmt:formatDate value="${i}" var="dateStr" type="date" pattern="dd" />
															<fmt:formatDate value="${i}" var="dayStr" type="date" pattern="dd-MM-yyyy" />
															
															<c:set var="isHoliday" value="0"/>
						        							<c:set var="holidayName" value=""/>
						        							<c:if test="${fn:toLowerCase(dayOfweek) == 'sun' || fn:toLowerCase(dayOfweek) == 'sat'}">
						        								<c:set var="isHoliday" value="1"/>
						        								<c:set var="holidayName" value="${dayOfweek}"/>
						        							</c:if>
						        							<c:if test="${fn:toLowerCase(dayOfweek) != 'sun' && fn:toLowerCase(dayOfweek) != 'sat'}">
																<c:forEach var="j" items="${displayModel.calendarHolidayList}">
																	<fmt:formatDate value="${j.eventDate}" var="holidayDateStr" type="date" pattern="dd" />
							        								<c:if test="${dateStr == holidayDateStr}">
							        									<c:set var="isHoliday" value="1"/>
							        									<c:set var="holidayName" value="${j.remark}"/>
							        								</c:if>
							        							</c:forEach>
						        							</c:if>
						        							<c:if test="${isHoliday == 0}">
							        							<td class="dateCol" data-officer="${staff.userId}" data-date="${dayStr}" data-session="P">
						        							</c:if>
						        							<c:if test="${isHoliday == 1}">
							        							<td class="dateCol holiday" title="${holidayName}" data-officer="${staff.userId}" data-date="${dayStr}" data-session="P">
						        							</c:if>
																<c:forEach items="${displayModel.calendarEventList}" var="calendarEvent">
				        											<fmt:formatDate value="${calendarEvent.eventDate}" var="eventDateStr" type="date" pattern="dd" />
																	<c:if test="${eventDateStr == dateStr && calendarEvent.session == 'P' && calendarEvent.user.userId == staff.userId }">
																		<c:if test="${calendarEvent.activityType == 1}">
																			<sec:authorize access="!hasPermission(#user, 2) and !hasPermission(#user, 4) and !hasPermission(#user, 8)">
																				<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)">OT</a>
																			</sec:authorize>
																			<sec:authorize access="hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 8)">
																				<c:if test="${displayModel.showEdit == 1}">
																					<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" onclick="editEvent($(this))">OT</a>
																				</c:if>
																				<c:if test="${displayModel.showEdit == 0}">
																					<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)">OT</a>
																				</c:if>
																			</sec:authorize>
																		</c:if>
																		<c:if test="${calendarEvent.activityType == 2}">
																			<sec:authorize access="!hasPermission(#user, 2) and !hasPermission(#user, 4) and !hasPermission(#user, 8)">
																				<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" title="TimeOff">TimeO..</a>
																			</sec:authorize>
																			<sec:authorize access="hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 8)">
																				<c:if test="${displayModel.showEdit == 1}">
																					<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" onclick="editEvent($(this))" title="TimeOff">TimeO..</a>
																				</c:if>
																				<c:if test="${displayModel.showEdit == 0}">
																					<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" title="TimeOff">TimeO..</a>
																				</c:if>
																			</sec:authorize>
																		</c:if>
																		<c:if test="${calendarEvent.activityType == 3}">
																			<sec:authorize access="!hasPermission(#user, 2) and !hasPermission(#user, 4) and !hasPermission(#user, 8)">
																				<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" title="${calendarEvent.activityCode.code}">
																					<c:if test="${fn:length(calendarEvent.activityCode.code) > 5}">${fn:substring(calendarEvent.activityCode.code, 0, 5)}...</c:if>
																					<c:if test="${fn:length(calendarEvent.activityCode.code) <= 5}">${calendarEvent.activityCode.code}</c:if>
																				</a>
																			</sec:authorize>
																			<sec:authorize access="hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 8)">
																				<c:if test="${displayModel.showEdit == 1}">
																					<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" onclick="editEvent($(this))" title="${calendarEvent.activityCode.code}">
																						<c:if test="${fn:length(calendarEvent.activityCode.code) > 5}">${fn:substring(calendarEvent.activityCode.code, 0, 5)}...</c:if>
																						<c:if test="${fn:length(calendarEvent.activityCode.code) <= 5}">${calendarEvent.activityCode.code}</c:if>
																					</a>
																				</c:if>
																				<c:if test="${displayModel.showEdit == 0}">
																					<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" title="${calendarEvent.activityCode.code}">
																					<c:if test="${fn:length(calendarEvent.activityCode.code) > 5}">${fn:substring(calendarEvent.activityCode.code, 0, 5)}...</c:if>
																					<c:if test="${fn:length(calendarEvent.activityCode.code) <= 5}">${calendarEvent.activityCode.code}</c:if>
																					</a>
																				</c:if>
																			</sec:authorize>
																		</c:if>
																	</c:if>
																</c:forEach>
																<c:forEach items="${displayModel.assignmentList}" var="assignment">
																	<fmt:formatDate value="${assignment.getEventDate()}" var="assignDateStr" type="date" pattern="dd" />
																	<c:if test="${assignDateStr == dateStr && assignment.getSession() == 'P' && assignment.getUserId() == staff.userId }">
																			<a type="button" class="btn btn-small ${displayModel.getAssignmentEventColor()}" href="javascript:void(0)">${assignment.getCode()}</a>
																	</c:if>
																</c:forEach>
															</td>
														</c:forEach>
					        						</tr>
					        						<sec:authorize access="hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 8)">
					        						<c:if test="${displayModel.showEdit == 1}">
					        						<tr>
					        							<c:forEach var="i" items="${displayModel.dateList}">
						        							<fmt:formatDate value="${i}" var="dayOfweek" type="date" pattern="E" />
				        									<fmt:formatDate value="${i}" var="dateStr" type="date" pattern="dd" />
															<fmt:formatDate value="${i}" var="dayStr" type="date" pattern="dd-MM-yyyy" />
															<c:set var="isHoliday" value="0"/>
						        							<c:set var="holidayName" value=""/>
						        							<c:if test="${fn:toLowerCase(dayOfweek) == 'sun' || fn:toLowerCase(dayOfweek) == 'sat'}">
						        								<c:set var="isHoliday" value="1"/>
						        								<c:set var="holidayName" value="${dayOfweek}"/>
						        							</c:if>
						        							<c:if test="${fn:toLowerCase(dayOfweek) != 'sun' && fn:toLowerCase(dayOfweek) != 'sat'}">
																<c:forEach var="j" items="${displayModel.calendarHolidayList}">
																	<fmt:formatDate value="${j.eventDate}" var="holidayDateStr" type="date" pattern="dd" />
							        								<c:if test="${dateStr == holidayDateStr}">
							        									<c:set var="isHoliday" value="1"/>
							        									<c:set var="holidayName" value="${j.remark}"/>
							        								</c:if>
							        							</c:forEach>
						        							</c:if>
						        							<c:if test="${isHoliday == 0}">
							        							<td class="dateCol" >
						        							</c:if>
						        							<c:if test="${isHoliday == 1}">
							        							<td class="dateCol holiday" title="${holidayName}">
						        							</c:if>
																<button type="button" class="btn btn-small btn-grey btn-add" data-officer="${staff.userId}" data-date="${dayStr}" data-session="P" onclick="selectEvent($(this));">Add</button>
															</td>
														</c:forEach>
					        						</tr>
					        						</c:if>
													</sec:authorize>
					        						<tr>
						        						<th class="sessionCol"
															<sec:authorize access="hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 8)">
						        								<c:if test="${displayModel.showEdit == 1}">
						        								rowspan="2"
						        								</c:if>
					        								</sec:authorize>
					        								>
							        						<div class="text-center">E</div>
						        						</th>
					        							<c:forEach var="i" items="${displayModel.dateList}">
						        							<fmt:formatDate value="${i}" var="dayOfweek" type="date" pattern="E" />
				        									<fmt:formatDate value="${i}" var="dateStr" type="date" pattern="dd" />
															<fmt:formatDate value="${i}" var="dayStr" type="date" pattern="dd-MM-yyyy" />
															
															<c:set var="isHoliday" value="0"/>
						        							<c:set var="holidayName" value=""/>
						        							<c:if test="${fn:toLowerCase(dayOfweek) == 'sun' || fn:toLowerCase(dayOfweek) == 'sat'}">
						        								<c:set var="isHoliday" value="1"/>
						        								<c:set var="holidayName" value="${dayOfweek}"/>
						        							</c:if>
						        							<c:if test="${fn:toLowerCase(dayOfweek) != 'sun' && fn:toLowerCase(dayOfweek) != 'sat'}">
																<c:forEach var="j" items="${displayModel.calendarHolidayList}">
																	<fmt:formatDate value="${j.eventDate}" var="holidayDateStr" type="date" pattern="dd" />
							        								<c:if test="${dateStr == holidayDateStr}">
							        									<c:set var="isHoliday" value="1"/>
							        									<c:set var="holidayName" value="${j.remark}"/>
							        								</c:if>
							        							</c:forEach>
						        							</c:if>
						        							<c:if test="${isHoliday == 0}">
							        							<td class="dateCol" data-officer="${staff.userId}" data-date="${dayStr}" data-session="E">
						        							</c:if>
						        							<c:if test="${isHoliday == 1}">
							        							<td class="dateCol holiday" title="${holidayName}" data-officer="${staff.userId}" data-date="${dayStr}" data-session="E">
						        							</c:if>
																<c:forEach items="${displayModel.calendarEventList}" var="calendarEvent">
				        											<fmt:formatDate value="${calendarEvent.eventDate}" var="eventDateStr" type="date" pattern="dd" />
																	<c:if test="${eventDateStr == dateStr && calendarEvent.session == 'E' && calendarEvent.user.userId == staff.userId }">
																		<c:if test="${calendarEvent.activityType == 1}">
																			<sec:authorize access="!hasPermission(#user, 2) and !hasPermission(#user, 4) and !hasPermission(#user, 8)">
																				<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)">OT</a>
																			</sec:authorize>
																			<sec:authorize access="hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 8)">
																				<c:if test="${displayModel.showEdit == 1}">
																					<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" onclick="editEvent($(this))">OT</a>
																				</c:if>
																				<c:if test="${displayModel.showEdit == 0}">
																					<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)">OT</a>
																				</c:if>
																			</sec:authorize>
																		</c:if>
																		<c:if test="${calendarEvent.activityType == 2}">
																			<sec:authorize access="!hasPermission(#user, 2) and !hasPermission(#user, 4) and !hasPermission(#user, 8)">
																				<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" title="TimeOff">TimeO..</a>
																			</sec:authorize>
																			<sec:authorize access="hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 8)">
																				<c:if test="${displayModel.showEdit == 1}">
																					<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" onclick="editEvent($(this))" title="TimeOff">TimeO..</a>
																				</c:if>
																				<c:if test="${displayModel.showEdit == 0}">
																					<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" title="TimeOff">TimeO..</a>
																				</c:if>
																			</sec:authorize>
																		</c:if>
																		<c:if test="${calendarEvent.activityType == 3}">
																			<sec:authorize access="!hasPermission(#user, 2) and !hasPermission(#user, 4) and !hasPermission(#user, 8)">
																				<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" title="${calendarEvent.activityCode.code}">
																					<c:if test="${fn:length(calendarEvent.activityCode.code) > 5}">${fn:substring(calendarEvent.activityCode.code, 0, 5)}...</c:if>
																					<c:if test="${fn:length(calendarEvent.activityCode.code) <= 5}">${calendarEvent.activityCode.code}</c:if>
																				</a>
																			</sec:authorize>
																			<sec:authorize access="hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 8)">
																				<c:if test="${displayModel.showEdit == 1}">
																					<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" onclick="editEvent($(this))" title="${calendarEvent.activityCode.code}">
																						<c:if test="${fn:length(calendarEvent.activityCode.code) > 5}">${fn:substring(calendarEvent.activityCode.code, 0, 5)}...</c:if>
																						<c:if test="${fn:length(calendarEvent.activityCode.code) <= 5}">${calendarEvent.activityCode.code}</c:if>
																					</a>
																				</c:if>
																				<c:if test="${displayModel.showEdit == 0}">
																					<a class="btn btn-small ${displayModel.getCalendarEventColor()}" data-eventid="${calendarEvent.calendarEventId}" href="javascript:void(0)" title="${calendarEvent.activityCode.code}">
																					<c:if test="${fn:length(calendarEvent.activityCode.code) > 5}">${fn:substring(calendarEvent.activityCode.code, 0, 5)}...</c:if>
																					<c:if test="${fn:length(calendarEvent.activityCode.code) <= 5}">${calendarEvent.activityCode.code}</c:if>
																					</a>
																				</c:if>
																			</sec:authorize>
																		</c:if>
																	</c:if>
																</c:forEach>
																<c:forEach items="${displayModel.assignmentList}" var="assignment">
																	<fmt:formatDate value="${assignment.getEventDate()}" var="assignDateStr" type="date" pattern="dd" />
																	<c:if test="${assignDateStr == dateStr && assignment.getSession() == 'E' && assignment.getUserId() == staff.userId }">
																			<a type="button" class="btn btn-small ${displayModel.getAssignmentEventColor()}" href="javascript:void(0)">${assignment.getCode()}</a>
																	</c:if>
																</c:forEach>
															</td>
														</c:forEach>
					        						</tr>
					        						<sec:authorize access="hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 8)">
					        						<c:if test="${displayModel.showEdit == 1}">
					        						<tr>
					        							<c:forEach var="i" items="${displayModel.dateList}">
						        							<fmt:formatDate value="${i}" var="dayOfweek" type="date" pattern="E" />
				        									<fmt:formatDate value="${i}" var="dateStr" type="date" pattern="dd" />
															<fmt:formatDate value="${i}" var="dayStr" type="date" pattern="dd-MM-yyyy" />
															<c:set var="isHoliday" value="0"/>
						        							<c:set var="holidayName" value=""/>
						        							<c:if test="${fn:toLowerCase(dayOfweek) == 'sun' || fn:toLowerCase(dayOfweek) == 'sat'}">
						        								<c:set var="isHoliday" value="1"/>
						        								<c:set var="holidayName" value="${dayOfweek}"/>
						        							</c:if>
						        							<c:if test="${fn:toLowerCase(dayOfweek) != 'sun' && fn:toLowerCase(dayOfweek) != 'sat'}">
																<c:forEach var="j" items="${displayModel.calendarHolidayList}">
																	<fmt:formatDate value="${j.eventDate}" var="holidayDateStr" type="date" pattern="dd" />
							        								<c:if test="${dateStr == holidayDateStr}">
							        									<c:set var="isHoliday" value="1"/>
							        									<c:set var="holidayName" value="${j.remark}"/>
							        								</c:if>
							        							</c:forEach>
						        							</c:if>
						        							<c:if test="${isHoliday == 0}">
							        							<td class="dateCol" >
						        							</c:if>
						        							<c:if test="${isHoliday == 1}">
							        							<td class="dateCol holiday" title="${holidayName}">
						        							</c:if>
																<button type="button" class="btn btn-small btn-grey btn-add" data-officer="${staff.userId}" data-date="${dayStr}" data-session="E" onclick="selectEvent($(this));">Add</button>
															</td>
														</c:forEach>
					        						</tr>
					        						</c:if>
					        						</sec:authorize>
					        					</c:forEach>
				        					</tbody>
				        				</table>
				        			</div>
				        		</div>
	</div>
</div>