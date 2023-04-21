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
	</jsp:attribute>
	<jsp:attribute name="header">
    	<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/userLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/rejectDialog.jsp" %>
		<script>
			$(document).ready(function(){
				var $form = $('#mainForm').validate();
				
				var $dataTable;
				
				function alreadySelectedIdsCallback() {
					var ids = $('[name="assignmentIds"]', $tableContainer).map(function() {
						return this.value
					}).get();
					return ids;
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
					
					var buttons = [];
					
					$dataTable.DataTable({
						"ordering": true, 
						"order": [[ 8, "desc" ]],
						"searching": true,
						"buttons": buttons,
						"processing": false,
			            "serverSide": true,
			            "ajax": {
			            	url: "<c:url value='/assignmentAllocationAndReallocation/AssignmentTransferInOutApproval/queryAssignment'/>",
			            	data: function(dataModel) {
			            		var assignmentAdjustmentId = $('[name="assignmentAdjustmentId"]').val();
			            		dataModel.assignmentAdjustmentId = assignmentAdjustmentId;
			            		
			            		var tpuId = $('#tpuId').val();
			            		if (tpuId != '' && tpuId != null)
			            			dataModel.tpuId = tpuId;

			            		var outletTypeId = $('#outletTypeId').val();
			            		if (outletTypeId != '' && outletTypeId != null)
			            			dataModel.outletTypeId = outletTypeId;

			            		var districtId = $('#districtId').val();
			            		if (districtId != '' && districtId != null)
			            			dataModel.districtId = districtId;

			            		var batchId = $('#batchId').val();
			            		if (batchId != '' && batchId != null)
			            			dataModel.batchId = batchId;
			            	},
			            	method: 'post'
			            },
			            "columns": [
			                        { "data": "firm" },
			                        { "data": "district" },
			                        { "data": "tpu" },
			                        { "data": "address" },
			                        { "data": "startDate" },
			                        { "data": "endDate" },
			                        { "data": "noOfQuotation" },
			                        { "data": "requiredManDayString" },
			                        { "data": "selected",
			                        	"render": function(data) {
			                        		return data ? 'Y' : 'N';
			                        	}
			                        },
			                        { "data": "id",
			                        	"render": function(data, type, row) {
			                        		return '<a href="<c:url value='/assignmentManagement/PEViewAssignmentMaintenance/edit'/>?assignmentId=' + row.id + '" target="_blank"><i class="fa fa-list"></i></a>';
			                        	}
			                        }
									],
			            "columnDefs": columnDefs,
			            "createdRow": function(row, data, index) {
			            	if (data.selected)
			            		$(row).css('background-color', 'yellow');
			            }
					});
					
					$('.filters').change(function(){
						$dataTable.DataTable().ajax.reload();
					});
				}
				
				initTable();
				
				$('.searchSubmitToId').userLookup({
					selectedIdsCallback: function(selectedIds, singleRowData) {
						var id = selectedIds[0];
						$('[name="submitTo"]', this.$element).val(singleRowData.staffCode + " - "
								+ singleRowData.chineseName);
						$('[name="submitToApproveUserId"]', this.$element).val(id);
					},
					queryDataCallback: function(model) {
						model.authorityLevel = (1 | 2);
					},
					multiple: false
				});
				
				$('#btnReject').rejectDialog({
					submitCallback: function(remark) {
						$('[name="rejectReason"]').val(remark);
						$('[name="btnAction"]').val('Reject');
						if ($form.valid()) {
							Modals.startLoading();
							$('#mainForm').submit();
						}
					}
				});
				
				$('.select2ajax').select2ajax();
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Assignment Transfer-in/out Approval</h1>
        </section>
        
        <section class="content">
        	<form id="mainForm" action="<c:url value='/assignmentAllocationAndReallocation/AssignmentTransferInOutApproval/submit'/>" method="post" role="form"
        		enctype="multipart/form-data">
        		<input name="assignmentAdjustmentId" value="${model.id}" type="hidden" />
        		<div class="row">
					<div class="col-md-12">
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/assignmentAllocationAndReallocation/AssignmentTransferInOutApproval/home'/>">Back</a>
							</div>
	       					<div class="box-body">
								<!-- content -->
								<div class="row">
									<div class="col-md-12" style="color:red">
										Remark: <c:out value="${model.remark}"/>
									</div>
								</div>
								<div class="row">
									<div class="col-md-6 col-md-offset-6">
										<div class="row">
											<div class="col-md-4">
												From Field Officer:
											</div>
											<div class="col-md-2"><c:out value="${model.fromFieldOfficer}"/></div>
											<div class="col-md-4">
												Target Field Officer:
											</div>
											<div class="col-md-2"><c:out value="${model.targetFieldOfficer}"/></div>
										</div>
										<div class="row">
											<div class="col-md-4">
												Total Assignment:
											</div>
											<div class="col-md-2"><c:out value="${model.totalAssignments}"/></div>
											<div class="col-md-4">
												Selected Assignments:
											</div>
											<div class="col-md-2"><c:out value="${model.selectedAssignments}"/></div>
										</div>
										<div class="row">
											<div class="col-md-4">
												Total Quotations:
											</div>
											<div class="col-md-2"><c:out value="${model.totalQuotations}"/></div>
											<div class="col-md-4">
												Selected Quotations:
											</div>
											<div class="col-md-2"><c:out value="${model.selectedQuotations}"/></div>
										</div>
										<div class="row">
											<div class="col-md-4">
												Target Release Man-Day:
											</div>
											<div class="col-md-2"><c:out value="${model.manDay}"/></div>
											<div class="col-md-4">
												Actual Release Man-Day:
											</div>
											<div class="col-md-2"><c:out value="${model.actualReleaseManDaysBD}"/></div>
										</div>
										<div class="row">
											<div class="col-md-4">
												Resultant Man-Day Balance:
											</div>
											<div class="col-md-2"><c:out value="${model.resultantManDayBalanceBD}"/></div>
										</div>
									</div>
								</div>
								<br/>
								<div class="form-horizontal table-filter">
									<div class="row" style="margin-top: 10px;">
										<label class="col-md-2 control-label">TPU</label>
										<div class="col-md-3">
											<select id="tpuId" class="form-control select2ajax filters"
												multiple="multiple"
												data-allow-clear="true"
												data-close-on-select="false"
												data-ajax-url="<c:url value='/assignmentAllocationAndReallocation/AssignmentTransferInOutApproval/queryTpuSelect2'/>"
											></select>
										</div>
										<label class="col-md-2 control-label">Outlet Type</label>
										<div class="col-md-3">
											<select id="outletTypeId" class="form-control select2ajax filters"
												multiple="multiple"
												data-allow-clear="true" 
												data-close-on-select="false"
												data-ajax-url="<c:url value='/assignmentAllocationAndReallocation/AssignmentTransferInOutApproval/queryOutletTypeSelect2'/>"
											></select>
										</div>
									</div>
									<div class="row" style="margin-top: 10px;">
										<label class="col-md-2 control-label">District</label>
										<div class="col-md-3">
											<select id="districtId" class="form-control select2ajax filters"
												data-allow-clear="true"
												data-ajax-url="<c:url value='/assignmentAllocationAndReallocation/AssignmentTransferInOutApproval/queryDistrictSelect2'/>"></select>
										</div>
										<label class="col-md-2 control-label">Batch Code</label>
										<div class="col-md-3">
											<select id="batchId" class="form-control select2ajax filters"
												data-allow-clear="true"
												data-ajax-url="<c:url value='/assignmentAllocationAndReallocation/AssignmentTransferInOutApproval/queryBatchSelect2'/>"></select>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-12">
										<table class="table table-striped table-bordered table-hover" id="dataList">
											<thead>
											<tr>
												<th>Firm</th>
												<th>District</th>
												<th>TPU</th>
												<th>Address</th>
												<th>Start Date</th>
												<th>End Date</th>
												<th>No. of Quotation</th>
												<th>Required Man-Day</th>
												<th>Selected</th>
												<th class="text-center action"></th>
											</tr>
											</thead>
										</table>
									</div>
								</div>
								
								<c:if test="${model.status == 'Rejected'}">
									<div class="form-horizontal">
										<div class="form-group">
											<label class="col-md-2 control-label">Rejected Reason</label>
											<div class="col-md-10">
												<p class="form-control-static"><c:out value="${model.rejectReason}"/></p>
											</div>
										</div>
									</div>
								</c:if>
							</div>
						</div>
						
	        			<div class="box box-default">
	       					<div class="box-footer">
	       						<sec:authorize access="hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 256)">
	        						<button type="submit" class="btn btn-info">Approve</button>
	        						<button id="btnReject" type="button" class="btn btn-info pull-right">Reject</button>
	        						<input name="btnAction" value="" type="hidden" />
	        						<input name="rejectReason" value="" type="hidden" />
	       						</sec:authorize>
	       					</div>
	        			</div>
					</div>
				</div>
			</form>
        </section>
	</jsp:body>
</t:layout>