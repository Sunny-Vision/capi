<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page session="false"%>
<jsp:useBean id="niceDate" class="java.util.Date" />
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/timepicker-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp"%>
	</jsp:attribute>
	<jsp:attribute name="header">
		<style>
#map {
	height: 500px
}

#attachmentContainer {
	min-height: 300px;
}

#attachmentContainer .attachment {
	margin-bottom: 10px;
}

.btn.pull-right {
	margin-left: 10px;
}

.table caption{
    border: inherit; 
    color: #000000;
    
}

.table caption span{
    margin-left: 10px;
}

</style>
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp"%>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp"%>
		<%@include file="/WEB-INF/views/includes/select2.jsp"%>
		<%@include file="/WEB-INF/views/includes/timepicker.jsp"%>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp"%>
		<%@include file="/WEB-INF/views/includes/datepicker.jsp"%>
		<%@include file="/WEB-INF/views/includes/commonDialog/userLookup.jsp"%>
		<%@include file="/WEB-INF/views/includes/commonDialog/assignmentReallocationLookup.jsp"%>
		<%@include file="/WEB-INF/views/includes/commonDialog/quotationRecordReallocationLookup.jsp"%>
		<script src="<c:url value='/resources/js/underscore-min.js'/>" ></script>
		<script>
			function cleanOriginalUser() {
				$('[name="originalUser"]').val("");
				$('[id="originalUserId"]').val("");
			}
			
			$(function() {
				
				<c:if test="${model.submitToUserId != null}">
					var submitToId = ${model.submitToUserId};
					$('#submitToUserId').val(submitToId);
				</c:if>
				
				var $mainForm = $('#mainForm');
				
				$mainForm.validate();
				
				var $dataTable = $("#dataList");				
				
				$dataTable.DataTable({
					"order": [[ 0, "desc" ]],
					"searching": false,
					"ordering": true,
					<sec:authorize access="!(hasPermission(#user, 4) or hasPermission(#user, 8) or hasPermission(#user, 16) or hasPermission(#user, 256))">
					"buttons": [],
					</sec:authorize>
					<sec:authorize access="hasPermission(#user, 4) or hasPermission(#user, 8) or hasPermission(#user, 16) or hasPermission(#user, 256)">
					"buttons": [],
					</sec:authorize>
					"processing": true,
					"serverSide": false,
					"paging": false,
	                "columns": [
	                            {
	                            	"data": "collectionDate",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "startDate",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "endDate",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "firm",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "district",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "tpu",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "batchCode",
	                            	"orderable": false,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "noOfQuotation",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "assignmentStatus",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {	"data": "id",
	                            	"orderable": false,
									"searchable": false,
									"render" : function(data){
		                           		return "<a href='javascript:void(0)' target='_blank'><i class='fa fa-list'></i></a>";
									}
								},
	                            {
	                            	"data": "id",
	                            	"orderable": false,
	                            	"searchable": false,
	                            	"render" : function(data, type, full, meta){
	                            		var html = "<a href='#' class='table-btn btn-delete' ><span class='fa fa-times' aria-hidden='true'></span></a>";
	                            		html += "<input name='assignmentIds' type='hidden' value='" + data + "'/>";
	                            		return html;
                            		},
	                            	"className" : "text-center"
                            	}
	                        ]
				});
				
				$('[name="originalUser"],.searchOriginalUserId').userLookup({
					selectedIdsCallback: function(selectedIds, singleRowData) {
						var id = selectedIds[0];
						$('[name="originalUser"]').val(singleRowData.staffCode + " - "
								+ singleRowData.chineseName);
						$('[id="originalUserId"]').val(id);
						
						$.ajax({
							url: "<c:url value='/assignmentAllocationAndReallocation/AssignmentReallocation/targetUserChosen'/>",
							method: 'get',
							dataType: 'json',
							traditional: true,
							data: {targetUserId : id},
							success: function(json) {
								//$('#submitToUserId').val(json);
								var array = json.split(',');
								$('#submitToUserId').val(array[0]);
								$('[name="submitToUser"]').val(array[1]);
					        }
						});
					},
					queryDataCallback: function(model) {
						model.authorityLevel = 16;
						<c:if test="${model.fieldSupervisor}">
							model.teamOnly = true;
						</c:if>
					},
					multiple: false
				});
				
				$('[name="targetUser"],.searchTargetUserId').userLookup({
					selectedIdsCallback: function(selectedIds, singleRowData) {
						var id = selectedIds[0];
						$('[name="targetUser"]').val(singleRowData.staffCode + " - "
								+ singleRowData.chineseName);
						$('[id="targetUserId"]').val(id);
						
						/*
						$.ajax({
							url: "<c:url value='/assignmentAllocationAndReallocation/AssignmentReallocation/targetUserChosen'/>",
							method: 'get',
							dataType: 'json',
							traditional: true,
							data: {targetUserId : id},
							success: function(json) {
								//$('#submitToUserId').val(json);
								var array = json.split(',');
								$('#submitToUserId').val(array[0]);
								$('[name="submitToUser"]').val(array[1]);
					        }
						});*/
					},
					queryDataCallback: function(model) {
						model.authorityLevel = 16;
					},
					multiple: false
				});
				
				$('[name="submitToUser"],.searchSubmitToUserId').userLookup({
					selectedIdsCallback: function(selectedIds, singleRowData) {
						var id = selectedIds[0];
						$('[name="submitToUser"]').val(singleRowData.staffCode + " - "
								+ singleRowData.chineseName);
						$('[id="submitToUserId"]').val(id);
					},
					queryDataCallback: function(model) {
						model.authorityLevel = 4;
					},
					multiple: false
				});
				
				var originalUserId = $('[id="originalUserId"]').val();
				
				var oldChildIds = $('[name="assignmentIdList"]').val().split(',');
				
				if($('#submitToUserId').val() == "" && originalUserId != "") {
					$.ajax({
						url: "<c:url value='/assignmentAllocationAndReallocation/AssignmentReallocation/targetUserChosen'/>",
						method: 'get',
						dataType: 'json',
						traditional: true,
						data: {targetUserId : originalUserId},
						success: function(json) {
							//$('#submitToUserId').val(json);
							var array = json.split(',');
							$('#submitToUserId').val(array[0]);
							$('[name="submitToUser"]').val(array[1]);
				        }
					});
				}
				
				$.get("<c:url value='/assignmentAllocationAndReallocation/AssignmentReallocation/getAssignmentReallocationDetail'/>",
					{originalUserId: originalUserId, assignmentIds: oldChildIds},
					function(data) {
						$dataTable.DataTable().rows().remove();
						$dataTable.DataTable().rows.add(data).draw();
					}
				);
				
				$('.searchAssignmentReallocation').assignmentReallocationLookup({
					selectedIdsCallback: function(selectedIds) {
						var existingIds = $($dataTable.DataTable().rows().data()).map(function(){
							  return this.id;
						}).get();
						
						var differenceIds =  _.difference(selectedIds, existingIds);
						var newIds = $.merge(differenceIds, existingIds);
						
						$('[name="assignmentIdList"]').val(newIds);
						
						var originalUserId = $('[id="originalUserId"]').val();
						
						$.post("<c:url value='/assignmentAllocationAndReallocation/AssignmentReallocation/getAssignmentReallocationDetail'/>",
							{originalUserId: originalUserId, assignmentIds: newIds},
							function(data) {
								$dataTable.DataTable().rows().remove();
								$dataTable.DataTable().rows.add(data).draw();
							}
						);
					},
					queryDataCallback: function(model) {
						model.originalUserId = $('[id="originalUserId"]').val();
						model.assignmentIdList = $('[name="assignmentIdList"]').val();
					}
				});
				
				$dataTable.on('click', '.btn-delete', function(e) {
					e.preventDefault();
					var deleteId = $($dataTable.DataTable().row($(this).parents('tr')).data()).map(function(){
						return this.id;
					}).get();
					var $dataTableRow = $(this).parents('table').DataTable().row($(this).parents('tr'));
					bootbox.confirm({
						title:"Confirmation",
						message: "<spring:message code='W00001' />",
						callback: function(result){
							if (result){
								var ary = $('[name="assignmentIdList"]').val().split(',');
								ary = jQuery.grep(ary, function(value) {
									return value != deleteId;
								});
								$('[name="assignmentIdList"]').val(ary);
								$dataTableRow.remove().draw();
							}
						}
					});
				});
				
				/*$('.filters').change(function(){
					$dataTable.DataTable().ajax.reload();
				});*/
				
				var $quotationRecordTable = $("#quotationRecordList");				
				
				$quotationRecordTable.DataTable({
					"order": [[ 0, "desc" ]],
					"searching": false,
					"ordering": true,
					<sec:authorize access="!(hasPermission(#user, 4) or hasPermission(#user, 8) or hasPermission(#user, 16) or hasPermission(#user, 256))">
					"buttons": [],
					</sec:authorize>
					<sec:authorize access="hasPermission(#user, 4) or hasPermission(#user, 8) or hasPermission(#user, 16) or hasPermission(#user, 256)">
					"buttons": [],
					</sec:authorize>
					"processing": true,
					"serverSide": false,
					"paging": false,
	                "columns": [
	                            {
	                            	"data": "collectionDate",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "startDate",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "endDate",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "firm",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "district",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "tpu",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "batchCode",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "displayName",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "category",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "quotationStatus",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "id",
	                            	"orderable": false,
	                            	"searchable": false,
	                            	"render" : function(data, type, full, meta){
	                            		var html = "<a href='#' class='table-btn btn-delete' ><span class='fa fa-times' aria-hidden='true'></span></a>";
	                            		html += "<input name='quotationRecordIds' type='hidden' value='" + data + "'/>";
	                            		return html;
                            		},
	                            	"className" : "text-center"
                            	}
	                        ]
				});
				
				var oldQuotationRecordIds = $('[name="quotationRecordIdList"]').val().split(',');
				
				$.get("<c:url value='/assignmentAllocationAndReallocation/AssignmentReallocation/getQuotationRecordReallocationDetail'/>",
					{originalUserId: originalUserId, quotationRecordIds: oldQuotationRecordIds},
					function(data) {
						$quotationRecordTable.DataTable().rows().remove();
						$quotationRecordTable.DataTable().rows.add(data).draw();
					}
				);
				
				$('.searchQuotationRecordReallocation').quotationRecordReallocationLookup({
					selectedIdsCallback: function(selectedIds) {
						var existingIds = $($quotationRecordTable.DataTable().rows().data()).map(function(){
							  return this.id;
						}).get();
						
						var differenceIds =  _.difference(selectedIds, existingIds);
						var newIds = $.merge(differenceIds, existingIds);
						
						$('[name="quotationRecordIdList"]').val(newIds);
						
						var originalUserId = $('[id="originalUserId"]').val();
						
						$.post("<c:url value='/assignmentAllocationAndReallocation/AssignmentReallocation/getQuotationRecordReallocationDetail'/>",
							{originalUserId: originalUserId, quotationRecordIds: newIds},
							function(data) {
								$quotationRecordTable.DataTable().rows().remove();
								$quotationRecordTable.DataTable().rows.add(data).draw();
							}
						);
					},
					queryDataCallback: function(model) {
						model.originalUserId = $('[id="originalUserId"]').val();
						model.quotationRecordIdList = $('[name="quotationRecordIdList"]').val();
					}
				});
				
				$quotationRecordTable.on('click', '.btn-delete', function(e) {
					e.preventDefault();
					var deleteId = $($quotationRecordTable.DataTable().row($(this).parents('tr')).data()).map(function(){
						return this.id;
					}).get();
					var $quotationRecordTableRow = $(this).parents('table').DataTable().row($(this).parents('tr'));
					bootbox.confirm({
						title:"Confirmation",
						message: "<spring:message code='W00001' />",
						callback: function(result){
							if (result){
								var ary = $('[name="quotationRecordIdList"]').val().split(',');
								ary = jQuery.grep(ary, function(value) {
									return value != deleteId;
								});
								$('[name="quotationRecordIdList"]').val(ary);
								$quotationRecordTableRow.remove().draw();
							}
						}
					});
				});
				
				<c:if test="${not model.fieldSupervisor && model.fieldOfficer}">
					$('[name="originalUser"]').attr("disabled", true);
					$('.searchOriginalUserId').find('.fa-search').parent().css('visibility', 'hidden');
				</c:if>;
				
				<sec:authorize access="!(hasPermission(#user, 4) or hasPermission(#user, 8) or hasPermission(#user, 16) or hasPermission(#user, 256))">
					viewOnly();
				</sec:authorize>
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Assignment Reallocation</h1>
          <c:if test="${act != 'add'}">
				<div class="breadcrumb form-horizontal" style="width: 240px">
					<div class="form-group" style="margin-bottom: 0px">
			        	<div class="col-sm-5">Created Date:</div>
			        	<div class="col-sm-7">${commonService.formatDateTime(model.createdDate)}</div>
			        </div>
			        <div class="form-group" style="margin-bottom: 0px">
			         	<div class="col-sm-5">Last Modified:</div>
			         	<div class="col-sm-7">${commonService.formatDateTime(model.modifiedDate)}</div>
			         </div>
		      	</div>
	      	</c:if>
        </section>
        
        <section class="content">
        	<form id="mainForm"
				action="<c:url value='/assignmentAllocationAndReallocation/AssignmentReallocation/save'/>?act=changePerson" method="post" role="form">
        		<input name="assignmentReallocationId" value="<c:out value="${model.assignmentReallocationId}" />" type="hidden" />
        		<input name="assignmentIdList" value="<c:forEach var="id" items="${model.assignmentIds}">${id},</c:forEach>" type="hidden" />
        		<input name="quotationRecordIdList" value="<c:forEach var="id" items="${model.quotationRecordIds}">${id},</c:forEach>" type="hidden" />
        		<input name="creatorId" value="<c:out value="${model.creatorId}" />" type="hidden" />
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
								<a class="btn btn-default"
									href="<c:url value='/assignmentAllocationAndReallocation/AssignmentReallocation/home'/>">Back</a>
							</div>
	       					<div class="box-body">
	       						<div class="form-horizontal">
									<div class="row">
										<label class="col-sm-1 control-label">Original Field Officer</label>
										<div class="col-md-3">
											<div class="input-group">
												<input name="originalUser" type="text" class="form-control" value="<c:out value="${model.originalUser}" />" readonly />
												<input id="originalUserId" name="originalUserId" class="filters" value="<c:out value="${model.originalUserId}" />" type="hidden" />
												<div class="input-group-addon searchOriginalUserId" >
													<i class="fa fa-search"></i>
												</div>
											</div>
										</div>
										<label class="col-sm-1 control-label">Target Field Officer</label>
										<div class="col-md-3">
											<div class="input-group">
												<input name="targetUser" type="text" class="form-control"
													value="<c:out value="${model.targetUser}" />" required
													readonly />
												<input id="targetUserId" name="targetUserId"
													value="<c:out value="${model.targetUserId}" />"
													type="hidden" />
												<div class="input-group-addon searchTargetUserId">
													<i class="fa fa-search"></i>
												</div>
											</div>
										</div>
									</div>
									<div class="row" style="margin-top: 10px;">
										<label class="col-sm-1 control-label">Submit To</label>
										<div class="col-md-3">
											<div class="input-group">
												<%--<select class="form-control" id="submitToUserId"
													name="submitToUserId" required>
													<option value=""></option>
													<c:forEach var="supervisor" items="${supervisors}">
														<option value="<c:out value="${supervisor.userId}" />">${supervisor.staffCode} - ${supervisor.chineseName}</option>
													</c:forEach>
												</select>--%>
												<input name="submitToUser" type="text" class="form-control"
													value="<c:out value="${model.submitToUser}" />" required
													readonly />
												<input id="submitToUserId" name="submitToUserId"
													value="<c:out value="${model.submitToUserId}" />"
													type="hidden" />
												<div class="input-group-addon searchSubmitToUserId">
													<i class="fa fa-search"></i>
												</div>
											</div>
										</div>
										<div class="col-md-4">
											<c:if test="${not model.fieldOfficer}">
												<button type="button" class="btn btn-info" name="cleanUser" onclick="cleanOriginalUser()">Clear Original User</button>
											</c:if>
										</div>
									</div>
									<c:if test="${model.rejectReason != null}">
										<div class="row" style="margin-top: 10px;">
											<label class="col-sm-1 control-label" style="color:red">Reject Reason</label>
											<div class="col-md-2">
												<p>${model.rejectReason}</p>
											</div>
										</div>
									</c:if>
								</div>
								<hr />
								<div class="col-sm-12">
									<div class="clearfix">&nbsp;</div>
									<table class="table table-striped table-bordered table-hover"
										id="dataList">
										<caption><span><strong>Selection of Assignments</strong></span></caption>
										<thead>
										<tr>
											<th>Collection Date</th>
											<th>Start Date</th>
											<th>End Date</th>
											<th>Firm</th>
											<th>District</th>
											<th>TPU</th>
											<th>Batch Code</th>
											<th>No. of Quotation</th>
											<th>Assignment Status</th>
											<th></th>
											<th class="text-center action"></th>
										</tr>
										</thead>
										<tbody>
										</tbody>
									</table>
									<sec:authorize
										access="hasPermission(#user, 4) or hasPermission(#user, 8) or hasPermission(#user, 16) or hasPermission(#user, 256)">
										<button class="searchAssignmentReallocation" type="button"
											class="btn btn-default btn-add">
											<i class="fa fa-plus"></i> Add Assignment Reallocation</button>
									</sec:authorize>
								</div>
								<hr />
								<div class="col-sm-12">
									<div class="clearfix">&nbsp;</div>
									<table class="table table-striped table-bordered table-hover"
										id="quotationRecordList">
										<caption><span><strong>Selection of Quotations</strong></span></caption>
										<thead>
										<tr>
											<th>Collection Date</th>
											<th>Start Date</th>
											<th>End Date</th>
											<th>Firm</th>
											<th>District</th>
											<th>TPU</th>
											<th>Batch Code</th>
											<th>Display Name</th>
											<th>Category</th>
											<th>Quotation Status</th>
											<th class="text-center action"></th>
										</tr>
										</thead>
										<tbody>
										</tbody>
									</table>
									<sec:authorize
										access="hasPermission(#user, 4) or hasPermission(#user, 8) or hasPermission(#user, 16) or hasPermission(#user, 256)">
										<button class="searchQuotationRecordReallocation" type="button"
											class="btn btn-default btn-add">
											<i class="fa fa-plus"></i> Add Quotation Record Reallocation</button>
									</sec:authorize>
								</div>
							</div>
							<sec:authorize
								access="hasPermission(#user, 4) or hasPermission(#user, 8) or hasPermission(#user, 16) or hasPermission(#user, 256)">
								<div class="box-footer">
		        					<button type="submit" class="btn btn-info">Submit</button>
		       					</div>
	       					</sec:authorize>
						</div>
	        		</div>
	        	</div>
        	</form>
        </section>
	</jsp:body>
</t:layout>

