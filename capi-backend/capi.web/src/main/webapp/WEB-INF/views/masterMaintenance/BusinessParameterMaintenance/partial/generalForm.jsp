<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div class="row">
	<form method="post" action="<c:url value='/masterMaintenance/BusinessParameterMaintenance/saveGerneral'/>" id="generalForm" >
	<input type="hidden" class="form-control numbers" name="syncCalendar" required value="0"/>
	<div class="col-md-12">
		<div class="form-horizontal">
			<div class="col-sm-12">
				<div class="form-group">
					<label class="col-sm-2 control-label">Summer</label>
					<div class="col-sm-4">
						<div class="input-group">	
							<select name="summerStartDate" class="form-control" required>
								<c:forEach var="i" begin="1" end="12">
								   <option value="<c:out value="${i}" />" <c:if test="${displayModel.getCommonSummerStartDate() eq  i}">selected</c:if>>${i}</option>
								</c:forEach>
							</select>	
							<div class="input-group-addon filter-officer">
								to
							</div>
							<select name="summerEndDate" class="form-control" required>
								<c:forEach var="i" begin="1" end="12">
								   <option value="<c:out value="${i}" />" <c:if test="${displayModel.getCommonSummerEndDate() eq  i}">selected</c:if>>${i}</option>
								</c:forEach>
							</select>
						</div>
					</div>
					
					<button type="submit" class="col-sm-2 btn btn-danger sync-calendar pull-right">Update Calendar (PHs)</button>
					
					
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label">Winter</label>
					<div class="col-sm-4">
						<div class="input-group">		
							<select name="winterStartDate" class="form-control" required>
								<c:forEach var="i" begin="1" end="12">
								   <option value="<c:out value="${i}" />" <c:if test="${displayModel.getCommonWinterStartDate() eq  i}">selected</c:if>>${i}</option>
								</c:forEach>
							</select>							
							<div class="input-group-addon filter-officer">
								to
							</div>
							<select name="winterEndDate" class="form-control" required>
								<c:forEach var="i" begin="1" end="12">
								   <option value="<c:out value="${i}" />" <c:if test="${displayModel.getCommonWinterEndDate() eq  i}">selected</c:if>>${i}</option>
								</c:forEach>
							</select>							
						</div>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label">Country of Origins</label>
					<div class="col-sm-4">
						<select class="form-control select2-addable col-sm-2" name="countryOfOrigins" multiple>
							<c:forEach items="${displayModel.getCommonCountryOfOrigins()}" var="origin">
							<option value="<c:out value="${origin}" />" selected="selected">${origin}</option>
							</c:forEach>
						</select>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label">Public Holiday Url</label>
					<div class="col-sm-4">
						<input type="text" class="form-control" name="publicHolidayUrl" required value="<c:out value="${displayModel.getCommonPublicHolidayUrl()}" />"/>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label">RUA Ratio</label>
					<div class="col-sm-4">
						<input type="text" class="form-control numbers" name="ruaRatio" required value="<c:out value="${displayModel.getCommonRuaRatio()}" />"/>
					</div>
				</div>
				<div class="form-group radio-group">
					<label for="" class="col-sm-2 control-label">Calendar Event Color</label>
					<div class="col-sm-6">
						<div class="col-sm-2">
							<label class="radio-inline"> 
								<input type="radio" name="calendarEventColor" value="primary" required <c:if test="${displayModel.getCommonCalendarEventColor() == 'primary'}">checked</c:if>/>
								<div>
					              <div class="color-palette-set">
					                <div class="bg-light-blue disabled color-palette"><span></span></div>
					                <div class="bg-light-blue color-palette"><span></span></div>
					                <div class="bg-light-blue-active color-palette"><span></span></div>
					              </div>
					            </div>
							</label>
						</div>
						<div class="col-sm-2">
							<label class="radio-inline"> 
								<input type="radio" name="calendarEventColor" value="info" required <c:if test="${displayModel.getCommonCalendarEventColor() == 'info'}">checked</c:if>/>
								<div>
					              <div class="color-palette-set">
					                <div class="bg-aqua disabled color-palette"><span></span></div>
					                <div class="bg-aqua color-palette"><span></span></div>
					                <div class="bg-aqua-active color-palette"><span></span></div>
					              </div>
					            </div>
							</label>
						</div>
						<div class="col-sm-2">
							<label class="radio-inline"> 
								<input type="radio" name="calendarEventColor" value="success" required <c:if test="${displayModel.getCommonCalendarEventColor() == 'success'}">checked</c:if>/>
								<div>
					              <div class="color-palette-set">
					                <div class="bg-green disabled color-palette"><span></span></div>
					                <div class="bg-green color-palette"><span></span></div>
					                <div class="bg-green-active color-palette"><span></span></div>
					              </div>
					            </div>
							</label>
						</div>
						<div class="col-sm-2">
							<label class="radio-inline"> 
								<input type="radio" name="calendarEventColor" value="warning" required <c:if test="${displayModel.getCommonCalendarEventColor() == 'warning'}">checked</c:if>/>
								<div>
					              <div class="color-palette-set">
					                <div class="bg-yellow disabled color-palette"><span></span></div>
					                <div class="bg-yellow color-palette"><span></span></div>
					                <div class="bg-yellow-active color-palette"><span></span></div>
					              </div>
					            </div>
							</label>
						</div>
						<div class="col-sm-2">
							<label class="radio-inline"> 
								<input type="radio" name="calendarEventColor" value="danger" required <c:if test="${displayModel.getCommonCalendarEventColor() == 'danger'}">checked</c:if>/>
								<div>
					              <div class="color-palette-set">
					                <div class="bg-red disabled color-palette"><span></span></div>
					                <div class="bg-red color-palette"><span></span></div>
					                <div class="bg-red-active color-palette"><span></span></div>
					              </div>
					            </div>
							</label>
						</div>
						<div class="col-sm-2">
							<label class="radio-inline"> 
								<input type="radio" name="calendarEventColor" value="default" required <c:if test="${displayModel.getCommonCalendarEventColor() == 'default'}">checked</c:if>/>
								<div>
					              <div class="color-palette-set">
					                <div class="bg-gray disabled color-palette"><span></span></div>
					                <div class="bg-gray color-palette"><span></span></div>
					                <div class="bg-gray-active color-palette"><span></span></div>
					              </div>
					            </div>
							</label>
						</div>
						<div class="col-sm-2">
							<label class="radio-inline"> 
								<input type="radio" name="calendarEventColor" value="navy" required <c:if test="${displayModel.getCommonCalendarEventColor() == 'navy'}">checked</c:if>/>
								<div>
					              <div class="color-palette-set">
					                <div class="bg-navy disabled color-palette"><span></span></div>
					                <div class="bg-navy color-palette"><span></span></div>
					                <div class="bg-navy-active color-palette"><span></span></div>
					              </div>
					            </div>
							</label>
						</div>
						<div class="col-sm-2">
							<label class="radio-inline"> 
								<input type="radio" name="calendarEventColor" value="teal" required <c:if test="${displayModel.getCommonCalendarEventColor() == 'teal'}">checked</c:if>/>
								<div>
					              <div class="color-palette-set">
					                <div class="bg-teal disabled color-palette"><span></span></div>
					                <div class="bg-teal color-palette"><span></span></div>
					                <div class="bg-teal-active color-palette"><span></span></div>
					              </div>
					            </div>
							</label>
						</div>
						<div class="col-sm-2">
							<label class="radio-inline"> 
								<input type="radio" name="calendarEventColor" value="purple" required <c:if test="${displayModel.getCommonCalendarEventColor() == 'purple'}">checked</c:if>/>
								<div>
					              <div class="color-palette-set">
					                <div class="bg-purple disabled color-palette"><span></span></div>
					                <div class="bg-purple color-palette"><span></span></div>
					                <div class="bg-purple-active color-palette"><span></span></div>
					              </div>
					            </div>
							</label>
						</div>
						<div class="col-sm-2">
							<label class="radio-inline"> 
								<input type="radio" name="calendarEventColor" value="orange" required <c:if test="${displayModel.getCommonCalendarEventColor() == 'orange'}">checked</c:if>/>
								<div>
					              <div class="color-palette-set">
					                <div class="bg-orange disabled color-palette"><span></span></div>
					                <div class="bg-orange color-palette"><span></span></div>
					                <div class="bg-orange-active color-palette"><span></span></div>
					              </div>
					            </div>
							</label>
						</div>
						<div class="col-sm-2">
							<label class="radio-inline"> 
								<input type="radio" name="calendarEventColor" value="maroon" required <c:if test="${displayModel.getCommonCalendarEventColor() == 'maroon'}">checked</c:if>/>
								<div>
					              <div class="color-palette-set">
					                <div class="bg-maroon disabled color-palette"><span></span></div>
					                <div class="bg-maroon color-palette"><span></span></div>
					                <div class="bg-maroon-active color-palette"><span></span></div>
					              </div>
					            </div>
							</label>
						</div>
						<div class="col-sm-2">
							<label class="radio-inline"> 
								<input type="radio" name="calendarEventColor" value="black" required <c:if test="${displayModel.getCommonCalendarEventColor() == 'black'}">checked</c:if>/>
								<div>
					              <div class="color-palette-set">
					                <div class="bg-black disabled color-palette"><span></span></div>
					                <div class="bg-black color-palette"><span></span></div>
					                <div class="bg-black-active color-palette"><span></span></div>
					              </div>
					            </div>
							</label>
						</div>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label">Assignment Event
						Color</label>
					<div class="col-sm-6">
						<div class="col-sm-2">
							<label class="radio-inline"> 
								<input type="radio" name="assignmentEventColor" value="primary" required <c:if test="${displayModel.getCommonAssignmentEventColor() == 'primary'}">checked</c:if>/>
								<div>
					              <div class="color-palette-set">
					                <div class="bg-light-blue disabled color-palette"><span></span></div>
					                <div class="bg-light-blue color-palette"><span></span></div>
					                <div class="bg-light-blue-active color-palette"><span></span></div>
					              </div>
					            </div>
							</label>
						</div>
						<div class="col-sm-2">
							<label class="radio-inline"> 
								<input type="radio" name="assignmentEventColor" value="info" required <c:if test="${displayModel.getCommonAssignmentEventColor() == 'info'}">checked</c:if>/>
								<div>
					              <div class="color-palette-set">
					                <div class="bg-aqua disabled color-palette"><span></span></div>
					                <div class="bg-aqua color-palette"><span></span></div>
					                <div class="bg-aqua-active color-palette"><span></span></div>
					              </div>
					            </div>
							</label>
						</div>
						<div class="col-sm-2">
							<label class="radio-inline"> 
								<input type="radio" name="assignmentEventColor" value="success" required <c:if test="${displayModel.getCommonCalendarEventColor() == 'success'}">checked</c:if>/>
								<div>
					              <div class="color-palette-set">
					                <div class="bg-green disabled color-palette"><span></span></div>
					                <div class="bg-green color-palette"><span></span></div>
					                <div class="bg-green-active color-palette"><span></span></div>
					              </div>
					            </div>
							</label>
						</div>
						<div class="col-sm-2">
							<label class="radio-inline"> 
								<input type="radio" name="assignmentEventColor" value="warning" required <c:if test="${displayModel.getCommonCalendarEventColor() == 'warning'}">checked</c:if>/>
								<div>
					              <div class="color-palette-set">
					                <div class="bg-yellow disabled color-palette"><span></span></div>
					                <div class="bg-yellow color-palette"><span></span></div>
					                <div class="bg-yellow-active color-palette"><span></span></div>
					              </div>
					            </div>
							</label>
						</div>
						<div class="col-sm-2">
							<label class="radio-inline"> 
								<input type="radio" name="assignmentEventColor" value="danger" required <c:if test="${displayModel.getCommonAssignmentEventColor() == 'danger'}">checked</c:if>/>
								<div>
					              <div class="color-palette-set">
					                <div class="bg-red disabled color-palette"><span></span></div>
					                <div class="bg-red color-palette"><span></span></div>
					                <div class="bg-red-active color-palette"><span></span></div>
					              </div>
					            </div>
							</label>
						</div>
						<div class="col-sm-2">
							<label class="radio-inline"> 
								<input type="radio" name="assignmentEventColor" value="default" required <c:if test="${displayModel.getCommonAssignmentEventColor() == 'default'}">checked</c:if>/>
								<div>
					              <div class="color-palette-set">
					                <div class="bg-gray disabled color-palette"><span></span></div>
					                <div class="bg-gray color-palette"><span></span></div>
					                <div class="bg-gray-active color-palette"><span></span></div>
					              </div>
					            </div>
							</label>
						</div>
						<div class="col-sm-2">
							<label class="radio-inline"> 
								<input type="radio" name="assignmentEventColor" value="navy" required <c:if test="${displayModel.getCommonAssignmentEventColor() == 'navy'}">checked</c:if>/>
								<div>
					              <div class="color-palette-set">
					                <div class="bg-navy disabled color-palette"><span></span></div>
					                <div class="bg-navy color-palette"><span></span></div>
					                <div class="bg-navy-active color-palette"><span></span></div>
					              </div>
					            </div>
							</label>
						</div>
						<div class="col-sm-2">
							<label class="radio-inline"> 
								<input type="radio" name="assignmentEventColor" value="teal" required <c:if test="${displayModel.getCommonAssignmentEventColor() == 'teal'}">checked</c:if>/>
								<div>
					              <div class="color-palette-set">
					                <div class="bg-teal disabled color-palette"><span></span></div>
					                <div class="bg-teal color-palette"><span></span></div>
					                <div class="bg-teal-active color-palette"><span></span></div>
					              </div>
					            </div>
							</label>
						</div>
						<div class="col-sm-2">
							<label class="radio-inline"> 
								<input type="radio" name="assignmentEventColor" value="purple" required <c:if test="${displayModel.getCommonAssignmentEventColor() == 'purple'}">checked</c:if>/>
								<div>
					              <div class="color-palette-set">
					                <div class="bg-purple disabled color-palette"><span></span></div>
					                <div class="bg-purple color-palette"><span></span></div>
					                <div class="bg-purple-active color-palette"><span></span></div>
					              </div>
					            </div>
							</label>
						</div>
						<div class="col-sm-2">
							<label class="radio-inline"> 
								<input type="radio" name="assignmentEventColor" value="orange" required <c:if test="${displayModel.getCommonAssignmentEventColor() == 'orange'}">checked</c:if>/>
								<div>
					              <div class="color-palette-set">
					                <div class="bg-orange disabled color-palette"><span></span></div>
					                <div class="bg-orange color-palette"><span></span></div>
					                <div class="bg-orange-active color-palette"><span></span></div>
					              </div>
					            </div>
							</label>
						</div>
						<div class="col-sm-2">
							<label class="radio-inline"> 
								<input type="radio" name="assignmentEventColor" value="maroon" required <c:if test="${displayModel.getCommonAssignmentEventColor() == 'maroon'}">checked</c:if>/>
								<div>
					              <div class="color-palette-set">
					                <div class="bg-maroon disabled color-palette"><span></span></div>
					                <div class="bg-maroon color-palette"><span></span></div>
					                <div class="bg-maroon-active color-palette"><span></span></div>
					              </div>
					            </div>
							</label>
						</div>
						<div class="col-sm-2">
							<label class="radio-inline"> 
								<input type="radio" name="assignmentEventColor" value="black" required <c:if test="${displayModel.getCommonAssignmentEventColor() == 'black'}">checked</c:if>/>
								<div>
					              <div class="color-palette-set">
					                <div class="bg-black disabled color-palette"><span></span></div>
					                <div class="bg-black color-palette"><span></span></div>
					                <div class="bg-black-active color-palette"><span></span></div>
					              </div>
					            </div>
							</label>
						</div>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label">Freeze Survey Month</label>
					<div class="col-sm-4">
						<input type="checkbox" class="" name="freezeSurveyMonth" value="1" <c:if test="${displayModel.getCommonFreezeSurveyMonth() == 1}">checked</c:if>>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label">Mobile Synchronization Period (mins)</label>
					<div class="col-sm-4">
						<input type="text" class="form-control digits" name="mobileSynchronizationPeriod" value="<c:out value="${displayModel.getCommonMobileSynchronizationPeriod()}" />" required>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label">Delink Period(Months)</label>
					<div class="col-sm-4">
						<input type="text" class="form-control delinkPeriod" name="delinkPeriod" value="<c:out value="${displayModel.getDelinkPeriod()}" />" required>
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