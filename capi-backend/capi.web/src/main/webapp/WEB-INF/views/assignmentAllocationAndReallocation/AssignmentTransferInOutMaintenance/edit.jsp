<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/select2-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp"%>
	</jsp:attribute>
	<jsp:attribute name="header">
    	<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker.jsp"%>
		<%@include file="/WEB-INF/views/includes/commonDialog/assignmentAllocationLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/userLookup.jsp" %>
		<script>
			$(document).ready(function(){
				$('#mainForm').validate();
				
				var $dataTable;
				var $tableContainer;
				
				function addAssignmentsToModel(ids) {
					var url = '<c:url value='/assignmentAllocationAndReallocation/AssignmentTransferInOutMaintenance/addAssignmentsToModel'/>';
					$.post(url, {
						newIds: ids
					}, function() {
						refreshTableAndMetric();
					});
				}
				function deleteAssignmentFromModel(id) {
					var url = '<c:url value='/assignmentAllocationAndReallocation/AssignmentTransferInOutMaintenance/deleteAssignmentFromModel'/>';
					$.post(url, {
						deleteId: id
					}, function() {
						refreshTableAndMetric();
					});
				}
				function alreadySelectedIdsCallback() {
					return [];
				}
				function updateActualReleaseManDayColor() {
					var targetReleaseManDays = +$('#targetReleaseManDays').text();
					var actualReleaseManDays = +$('#actualReleaseManDays').text();
					var diff = targetReleaseManDays - actualReleaseManDays;
					
					if (Math.abs(diff) <= 0.5) {
						$('#actualReleaseManDays').css('color', 'black');
					} else if (diff < 0) {
						$('#actualReleaseManDays').css('color', 'green');
					} else if (diff > 0) {
						$('#actualReleaseManDays').css('color', 'red');
					}
				}
				function selectionChangedCallback(ids) {
					if (ids == undefined) ids = null;
					var plugin = this;
					var url = '<c:url value='/assignmentAllocationAndReallocation/AssignmentTransferInOutMaintenance/metricForAssignmentSelectionPopup'/>';
					$.post(url, {
						newIds: ids
					}, function(html) {
						var $container = plugin.$popupModal.find('.metric-container');
						if ($container.length == 0) {
							plugin.$popupModal.find('hr').after('<div class="metric-container"></div>');
							$container = plugin.$popupModal.find('.metric-container');
						}
						var $html = $(html);
						$container.html($html);

						var targetReleaseManDays = +$('#targetReleaseManDays').text();
						var actualReleaseManDays = +$('.actual-release-man-days', $html).text();
						var diff = targetReleaseManDays - actualReleaseManDays;
						
						if (Math.abs(diff) <= 0.5) {
							$('.actual-release-man-days', $html).css('color', 'black');
						} else if (diff < 0) {
							$('.actual-release-man-days', $html).css('color', 'green');
						} else if (diff > 0) {
							$('.actual-release-man-days', $html).css('color', 'red');
						}
					});
				}
				
				function initTable() {
					$dataTable = $("#dataList");
					
					var columnDefs = [
			                           {
			                               "targets": "action",
			                               "orderable": false,
			                               "searchable": false
			                           },
			                           {
			                        	   "targets": "_all",
			                        	   "className" : "text-center"
			                           }
										];
					
					var buttons = [
						{
							"text": "Add",
							"init": function(dt, node, config) {
								var $btn = node;
								$btn.assignmentAllocationLookup({
									queryUrl: '<c:url value='/assignmentAllocationAndReallocation/AssignmentTransferInOutMaintenance/queryAssignment'/>',
									selectAllUrl: '<c:url value='/assignmentAllocationAndReallocation/AssignmentTransferInOutMaintenance/queryAssignmentSelectAll'/>',
									selectedIdsCallback: addAssignmentsToModel,
									alreadySelectedIdsCallback: alreadySelectedIdsCallback,
									selectionChangedCallback: selectionChangedCallback,
									onloadCallback: selectionChangedCallback
								});
							}
						}
					];
					
					$dataTable.DataTable({
						"ordering": true, 
						"order": [[ 0, "asc" ]],
						"searching": true,
						"buttons": buttons,
						"processing": false,
			            "serverSide": false,
			            "columnDefs": columnDefs
					});
					$($dataTable).on('click', '.btn-delete', function() {
						var id = $(this).data('id');
						bootbox.confirm({
							title:"Confirmation",
							message: "<spring:message code='W00001' />",
							callback: function(result){
								if (result){
									deleteAssignmentFromModel(id);
								}
							}
						});
					});
				}
				
				function refreshTableAndMetric() {
					$.get('<c:url value='/assignmentAllocationAndReallocation/AssignmentTransferInOutMaintenance/editTable'/>',
						function(html) {
							var $html = $(html);
							$tableContainer.empty().append($html);
							initTable();
							$('#selectedAssignments').text($('#selectedAssignmentsHidden').val());
							$('#selectedQuotations').text($('#selectedQuotationsHidden').val());
							$('#actualReleaseManDays').text($('#actualReleaseManDaysHidden').val());
							updateActualReleaseManDayColor();
						}
					);
				}
				
				$tableContainer = $('#tableContainer');
				
				refreshTableAndMetric();

				$('[name="submitToApproveUserId"]').select2();
				
				<sec:authorize access="!(hasPermission(#user, 16) or hasPermission(#user, 256))">
					viewOnly();
				</sec:authorize>
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Assignment Transfer-in/out Maintenance</h1>
        </section>
        
        <section class="content">
        	<form id="mainForm" action="<c:url value='/assignmentAllocationAndReallocation/AssignmentTransferInOutMaintenance/submit'/>" method="post" role="form"
        		enctype="multipart/form-data">
        		<div class="row">
					<div class="col-md-12">
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/assignmentAllocationAndReallocation/AssignmentTransferInOutMaintenance/home'/>">Back</a>
							</div>
	       					<div class="box-body">
								<!-- content -->
								<div class="row">
									<div class="col-md-12" style="color:red">
										Remark: <c:out value="${model.remark}"/>
									</div>
								</div>
								
								<c:if test="${model.status == 'Rejected'}">
									<div class="row">
										<div class="col-md-12" style="color:red">
											Rejected Reason: <c:out value="${model.rejectReason}" escapeXml="false"/>
										</div>
									</div>
								</c:if>
								<div class="row">
									<div class="col-md-6 col-md-offset-6">
										<div class="row">
											<div class="col-md-4">
												Target Field Officer:
											</div>
											<div class="col-md-8"><c:out value="${model.targetFieldOfficer}"/></div>
										</div>
										<div class="row">
											<div class="col-md-4">
												Total Assignment:
											</div>
											<div class="col-md-2"><c:out value="${model.totalAssignments}"/></div>
											<div class="col-md-4">
												Selected Assignments:
											</div>
											<div class="col-md-2" id="selectedAssignments"></div>
										</div>
										<div class="row">
											<div class="col-md-4">
												Total Quotations:
											</div>
											<div class="col-md-2"><c:out value="${model.totalQuotations}"/></div>
											<div class="col-md-4">
												Selected Quotations:
											</div>
											<div class="col-md-2" id="selectedQuotations"></div>
										</div>
										<div class="row">
											<div class="col-md-4">
												Target Release Man-Day:
											</div>
											<div class="col-md-2" id="targetReleaseManDays"><c:out value="${model.manDay}"/></div>
											<div class="col-md-4">
												Actual Release Man-Day:
											</div>
											<div class="col-md-2" id="actualReleaseManDays"></div>
										</div>
									</div>
								</div>
								<br/>
								<div class="row">
									<div class="col-md-12" id="tableContainer"></div>
								</div>
								
								<div class="form-horizontal">
									<div class="form-group">
										<label class="col-md-2 control-label">Submit To</label>
										<div class="col-md-3">
											<select name="submitToApproveUserId" class="form-control">
												<c:forEach items="${model.usersForSelection}" var="u">
												<option value="${u.id}"
													${u.id == model.preSelectSupervisorId ? "selected" : ""}
												>${u.staffCode} - ${u.englishName}</option>
												</c:forEach>
											</select>
										</div>
									</div>
								</div>
							</div>
						</div>
						
	        			<div class="box box-default">
	       					<div class="box-footer">
	       						<sec:authorize access="hasPermission(#user, 16) or hasPermission(#user, 256)">
	        						<button name="btnSubmit" value="submit" type="submit" class="btn btn-info">Submit</button>
	       						</sec:authorize>
	       					</div>
	        			</div>
					</div>
				</div>
			</form>
        </section>
	</jsp:body>
</t:layout>