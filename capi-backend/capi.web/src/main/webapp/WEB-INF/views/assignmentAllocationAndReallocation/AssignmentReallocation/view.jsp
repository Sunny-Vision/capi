<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>
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
		<script>
			$(function(){
				var $dataTable = $("#dataList");			
				var $quotationRecordTable = $("#quotationRecordList");
				
				$dataTable.DataTable({
					"order": [[ 0, "desc" ]],
					"searching": true,
					"ordering": true,
					"buttons": [],
					"processing": true,
	                "serverSide": true,
	                "ajax": {
	                	url: "<c:url value='/assignmentAllocationAndReallocation/AssignmentReallocation/queryAssignmentReallocationList'/>",
	                	data: function(d) {
	                		d.assignmentReallocationId = $('[name="assignmentReallocationId"]').val();
	                	},
	                	method: "post"
	                },
	                "columns": [
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
	                            	"data": "collectionDate",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            }
	                        ]
				});
				
				$quotationRecordTable.DataTable({
					"order": [[ 0, "desc" ]],
					"searching": true,
					"ordering": true,
					"buttons": [],
					"processing": true,
	                "serverSide": true,
	                "ajax": {
	                	url: "<c:url value='/assignmentAllocationAndReallocation/AssignmentReallocation/queryQuotationRecordReallocationList'/>",
	                	data: function(d) {
	                		d.assignmentReallocationId = $('[name="assignmentReallocationId"]').val();
	                	},
	                	method: "post"
	                },
	                "columns": [
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
	                            	"data": "collectionDate",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            }
	                        ]
				});
				
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
			<div class="box">
				<input name="assignmentReallocationId" value="<c:out value="${model.assignmentReallocationId}" />" type="hidden" />
				<input name="assignmentIdList" value="<c:forEach var="id" items="${model.assignmentIds}">${id},</c:forEach>" type="hidden" />
	        	<input name="quotationRecordIdList" value="<c:forEach var="id" items="${model.quotationRecordIds}">${id},</c:forEach>" type="hidden" />
				<div class="box-header with-border">
					<a class="btn btn-default"
						href="<c:url value='/assignmentAllocationAndReallocation/AssignmentReallocation/home'/>">Back</a>
				</div>
				<div class="box-body">
					<div class="row">
						<div class="col-md-12">
							<label class="col-sm-1 control-label">Original Field Officer</label>
							<div class="col-md-3">
								<p>${model.originalUser}</p>
								<input id="originalUserId" name="originalUserId" value="<c:out value="${model.originalUserId}" />" type="hidden" />
							</div>
							<label class="col-sm-1 control-label">Target Field Officer</label>
							<div class="col-md-3">
								<p>${model.targetUser}</p>
								<input id="targetUserId" name="targetUserId" value="<c:out value="${model.targetUserId}" />" type="hidden" />
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-12">
							<label class="col-sm-1 control-label" style="color:red">Remark</label>
							<div class="col-md-3">
								<p>${model.rejectReason}</p>
							</div>
						</div>
					</div>
					<hr/>
					<div class="row" style="margin-top: 10px;">
						<div class="col-sm-12">
							<div class="clearfix">&nbsp;</div>
							<table class="table table-striped table-bordered table-hover" id="dataList">
								<caption><span><strong>Selection of Assignments</strong></span></caption>
								<thead>
									<tr>
										<th>Firm</th>
										<th>District</th>
										<th>TPU</th>
										<th>Batch Code</th>
										<th>No. of Quotation</th>
										<th>Start Date</th>
										<th>End Date</th>
										<th>Collection Date</th>
									</tr>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>
					</div>
					<hr/>
					<div class="row" style="margin-top: 10px;">
						<div class="col-sm-12">
							<div class="clearfix">&nbsp;</div>
							<table class="table table-striped table-bordered table-hover" id="quotationRecordList">
								<caption><span><strong>Selection of Quotations</strong></span></caption>
								<thead>
									<tr>
										<th>Firm</th>
										<th>District</th>
										<th>TPU</th>
										<th>Batch Code</th>
										<th>Display Name</th>
										<th>Category</th>
										<th>Start Date</th>
										<th>End Date</th>
										<th>Collection Date</th>
									</tr>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>
					</div>
				</div>
			</div>
		</section>
	</jsp:body>
</t:layout>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	