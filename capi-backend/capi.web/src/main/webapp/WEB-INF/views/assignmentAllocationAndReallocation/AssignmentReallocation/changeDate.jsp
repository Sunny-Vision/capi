<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
<jsp:useBean id="niceDate" class="java.util.Date"/>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/timepicker-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp"%>
	</jsp:attribute>
	<jsp:attribute name="header">
		<style>
			#map { height: 500px }
			#attachmentContainer {
				min-height: 300px;
			}
			#attachmentContainer .attachment {
				margin-bottom: 10px;
			}
			.btn.pull-right {
				margin-left: 10px;
			}
		</style>
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/timepicker.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker.jsp"%>
		<%@include file="/WEB-INF/views/includes/commonDialog/outstandingAssignmentLookup.jsp" %>
		<script src="<c:url value='/resources/js/underscore-min.js'/>" ></script>
		<script>
			$(function() {
				var $mainForm = $('#mainForm');
				
				$mainForm.validate({
					submitHandler: function($form) {
						if ($("#dataList").DataTable().data().length === 0){							
							bootbox.alert({
								title:"Confirmation",
								message: "<spring:message code='E00009' />"								
							});
						}
						else{
							Modals.startLoading();
							$form.submit();
						}
						return false;
					}
				});
				
				var $dataTable = $("#dataList");				
				
				$dataTable.DataTable({
					"order": [[ 0, "desc" ]],
					"searching": false,
					"ordering": true,
					<sec:authorize access="!(hasPermission(#user, 8) or hasPermission(#user, 256))">
					"buttons": [],
					</sec:authorize>
					<sec:authorize access="hasPermission(#user, 8) or hasPermission(#user, 256)">
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
	                            	"data": "seDate",
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
	                            	"data": "pic",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "id",
	                            	"orderable": false,
	                            	"searchable": false,
	                            	"render" : function(data, type, full, meta){
	                            		return "<a href='javascript:void(0)' target='_blank'><i class='fa fa-list'></i></a>";
                            		},
	                            	"className" : "text-center"
                            	},
                            	{
    								"data": "id",
    								"orderable": false,
	                            	"searchable": false,
    								"render" : function(data){
    									var html = "<a href='#' class='table-btn btn-delete' ><span class='fa fa-times' aria-hidden='true'></span></a>";
    									html += "<input name='assignmentIds' type='hidden' value='" + data + "'/>";
    									return html;
    								}
    							}
	                        ]
				});
				
				$('.searchOutstandingAssignment').outstandingAssignmentLookup({
					selectedIdsCallback: function(selectedIds) {
						var existingIds = $($dataTable.DataTable().rows().data()).map(function(){
							  return this.id;
						}).get();
						
						var differenceIds =  _.difference(selectedIds, existingIds);
						var newIds = $.merge(differenceIds, existingIds);
						
						$('[name="assignmentIdList"]').val(newIds);
						
						$.post("<c:url value='/assignmentAllocationAndReallocation/AssignmentReallocation/getOutstandingAssignmentDetail'/>",
							{assignmentIds: newIds},
							function(data) {
								$dataTable.DataTable().rows().remove();
								$dataTable.DataTable().rows.add(data).draw();
							}
						).always(function() {
							Modals.endLoading();
						});
					},
					queryDataCallback: function(model) {
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
				
				var d = new Date();
				$('[name="newDate"]').datepicker({
					startDate: d
				});
				
				
				$('[name="startDate"]').datepicker({
					startDate: d
				});
				
				$('[name="endDate"]').datepicker({
					startDate: d
				});
				
				$('[name="newDate"]').on('change', function() {
					var value = $(this).val();
					if(value != "") {
						$('[name="startDate"]').attr('disabled', true);
						$('[name="endDate"]').attr('disabled', true);
					} else {
						$('[name="startDate"]').attr('disabled', false);
						$('[name="endDate"]').attr('disabled', false);
					}
				});
				
				$('[name="startDate"],[name="endDate"]').on('change', function() {
					var start = $('[name="startDate"]').val();
					var end = $('[name="endDate"]').val();
					if(start != "" || end != "") {
						$('[name="newDate"]').attr('disabled', true);
					} else if (start == "" && end == "") {
						$('[name="newDate"]').attr('disabled', false);
					}
				});
				
				<sec:authorize access="!(hasPermission(#user, 8) or hasPermission(#user, 256))">
					viewOnly();
				</sec:authorize>
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Assignment Reallocation</h1>
          <%--<c:if test="${act != 'add'}">
				<div class="breadcrumb form-horizontal" style="width:240px">
					<div class="form-group" style="margin-bottom:0px">
			        	<div class="col-sm-5">Created Date:</div>
			        	<div class="col-sm-7">${commonService.formatDateTime(model.createdDate)}</div>
			        </div>
			        <div class="form-group" style="margin-bottom:0px">
			         	<div class="col-sm-5">Last Modified:</div>
			         	<div class="col-sm-7">${commonService.formatDateTime(model.modifiedDate)}</div>
			         </div>
		      	</div>
	      	</c:if>--%>
        </section>
        
        <section class="content">
        	<form id="mainForm" action="<c:url value='/assignmentAllocationAndReallocation/AssignmentReallocation/save?act='/>${act}" method="post" role="form" >
        		<input name="assignmentReallocationId" value="<c:out value="${model.assignmentReallocationId}" />" type="hidden" />
        		<input name="assignmentIdList" value="" type="hidden" />

	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/assignmentAllocationAndReallocation/AssignmentReallocation/home'/>">Back</a>
							</div>
	       					<div class="box-body">
	       						<div class="form-horizontal">
									<div class="form-group">
										<label class="col-sm-2 control-label">Collection Date</label>
										<div class="col-md-3">
											<div class="input-group">
												<input type="text" name="newDate" class="form-control date-picker" >
												<div class="input-group-addon">
													<i class="fa fa-calendar"></i>
												</div>
											</div>
										</div>
									</div>
									<div class="form-group">
										<label class="col-sm-2 control-label">Period</label>
										<div class="col-md-2">
											<div class="input-group">
												<input type="text" name="startDate" class="form-control date-picker" >
												<div class="input-group-addon">
													<i class="fa fa-calendar"></i>
												</div>
											</div>
										</div>
										<div class="col-md-2">
											<div class="input-group">
												<input type="text" name="endDate" class="form-control date-picker" >
												<div class="input-group-addon">
													<i class="fa fa-calendar"></i>
												</div>
											</div>
										</div>
									</div>
								</div>
								<hr/>
								<div class="col-sm-12">
									<div class="clearfix">&nbsp;</div>
									<table class="table table-striped table-bordered table-hover" id="dataList">
										<thead>
										<tr>
											<th>Collection Date</th>
											<th>Start Date / End Date</th>
											<th>Firm</th>
											<th>District</th>
											<th>TPU</th>
											<th>Batch Code</th>
											<th>No. of Quotation</th>
											<th>PIC</th>
											<th></th>
											<th class="text-center action"></th>
										</tr>
										</thead>
										<tbody>
										</tbody>
									</table>
									<sec:authorize access="hasPermission(#user, 8) or hasPermission(#user, 256)">
										<button class="searchOutstandingAssignment" 
											type="button" class="btn btn-default btn-add"><i class="fa fa-plus"></i> Add Assignment Reallocation</button>
									</sec:authorize>
								</div>
							</div>
							<sec:authorize access="hasPermission(#user, 8) or hasPermission(#user, 256)">
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
