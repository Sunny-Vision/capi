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
			.table caption{
			    border: inherit; 
			    color: #000000;
			    
			}
			.table caption span{
			    margin-left: 10px;
			}
			.row .reject-container{
				color: #ff0000;
				margin-left: 0;
				margin-right: 0;
			}
			.reject-label{
				text-align: right;
			}
		</style>
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/timepicker.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker.jsp"%>
		<%@include file="/WEB-INF/views/includes/commonDialog/userLookup.jsp"%>
		<script>
			$(function() {
				var $mainForm = $('#mainForm');
				
				$mainForm.validate({
					rules : {
						submitToApprove : { 
							required : {
								depends : function (element) { 
									return $('[name="recommendReject"]').val() == 'recommend';
								}
							}
						},
						rejectReason : {
							required : {
								depends : function (element) {
									return $('[name="recommendReject"]').val() == 'reject';
								}
							}
						}
					},
				
					submitHandler: function(form) {
					    form.submit();
					}
				});
				
				$('[name="recommendRejectBtn"]').click(function() {
					var btnValue = $(this).val();
					$('[name="recommendReject"]').val(btnValue);
				});
				
				var $dataTable = $("#dataList");				
				
				$dataTable.DataTable({
					"order": [[ 9, "desc" ]],
					"searching": true,
					"ordering": true,
					<sec:authorize access="!(hasPermission(#user, 4) or hasPermission(#user, 256))">
					"buttons": [],
					</sec:authorize>
					<sec:authorize access="hasPermission(#user, 4) or hasPermission(#user, 256)">
					"buttons": [],
					</sec:authorize>
					"processing": true,
	                "serverSide": true,
	                "ajax": {
	                	url: "<c:url value='/assignmentAllocationAndReallocation/AssignmentReallocationRecommendation/queryAssignmentReallocationRecommendationList'/>",
	                	data: function(d) {
	                		d.assignmentReallocationId = $('[name="assignmentReallocationId"]').val();
	                		d.originalUserId = $('[name="originalUserId"]').val();
	                		d.targetUserId = $('[name="targetUserId"]').val();
	                		d.tpuIds = $('[name="tpuIds"]').val();
	                		d.outletTypeId = $('[name="outletTypeId"]').val();
	                		d.districtId = $('[name="districtId"]').val();
	                		d.batchId = $('[name="batchId"]').val();
	                		d.collectionDate = $('[name="collectionDate"]').val();
	                		d.selected = $('#selected').val();
	                		d.assignmentStatus = $('#assignmentStatus').val();
	                		d.surveyMonthId = $('#surveyMonthId').val();
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
	                            },
	                            {
	                            	"data": "assignmentStatus",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "selected",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "rejectReason",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "assignmentId",
	                            	"orderable": false,
	                            	"searchable": false,
	                            	"render" : function(data, type, full, meta){
	                            		//var html = "<a href='<c:url value='/masterMaintenance/DistrictMaintenance/edit?act=edit&id='/>"+data+"' class='table-btn'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a>";
	                            		//<sec:authorize access="hasPermission(#user, 256)">
	                            		//html += " &nbsp;<a href='javascript:void(0)' onclick='deleteRecordsWithConfirm(\"id="+data+"\")' class='table-btn btn-delete' data-id='"+data+"'><span class='fa fa-times' aria-hidden='true'></span></a>";
	                					//</sec:authorize>
	                            		//return html;
	                            		//return "<a href='<c:url value="/productMaintenance/ProductMaintenance/edit"/>?id=" + data + "' target='_blank'><i class='fa fa-list'></i></a>";
	                            		return "<a href='javascript:void(0)' target='_blank'><i class='fa fa-list'></i></a>";
                            		},
	                            	"className" : "text-center"
                            	}
	                        ],
	                "fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
	                	switch(aData.selected) {
	                		case "Y": $(nRow).css('background-color', 'yellow');
	                			break;
	                	}
	                }
				});
				
				var $quotationRecordTable = $("#quotationRecordList");				
				
				$quotationRecordTable.DataTable({
					"order": [[ 10, "desc" ]],
					"searching": true,
					"ordering": true,
					<sec:authorize access="!(hasPermission(#user, 4) or hasPermission(#user, 256))">
					"buttons": [],
					</sec:authorize>
					<sec:authorize access="hasPermission(#user, 4) or hasPermission(#user, 256)">
					"buttons": [],
					</sec:authorize>
					"processing": true,
	                "serverSide": true,
	                "ajax": {
	                	url: "<c:url value='/assignmentAllocationAndReallocation/AssignmentReallocationRecommendation/queryQuotationRecordReallocationRecommendationList'/>",
	                	data: function(d) {
	                		d.assignmentReallocationId = $('[name="assignmentReallocationId"]').val();
	                		d.originalUserId = $('[name="originalUserId"]').val();
	                		d.targetUserId = $('[name="targetUserId"]').val();
	                		d.tpuIds = $('[name="tpuIds2"]').val();
	                		d.outletTypeId = $('[name="outletTypeId2"]').val();
	                		d.districtId = $('[name="districtId2"]').val();
	                		d.batchId = $('[name="batchId2"]').val();
	                		d.collectionDate = $('[name="collectionDate2"]').val();
	                		d.selected = $('#selected2').val();
	                		d.category = $('[name="category2"]').val();
	                		d.quotationStatus = $('[name="quotationStatus2"]').val();
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
	                            	"data": "selected",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            }
	                        ],
	                "fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
	                	switch(aData.selected) {
	                		case "Y": $(nRow).css('background-color', 'yellow');
	                			break;
	                	}
	                }
				});
				
				$('[name="submitToApprove"],.searchSubmitToApproveId').userLookup({
					selectedIdsCallback: function(selectedIds, singleRowData) {
						var id = selectedIds[0];
						$('[name="submitToApprove"]').val(singleRowData.staffCode + " - "
								+ singleRowData.chineseName);
						$('[id="submitToApproveId"]').val(id);
					},
					queryDataCallback: function(model) {
						model.authorityLevel = (1 | 2);
					},
					multiple: false
				});
				
				$('.select2').select2({
					closeOnSelect: false
				});
				
				$('.select2ajax').select2ajax({
					allowClear: true,
					placeholder: '',
					width:'100%'
				});
				
				$('.select2ajax').hide();
				
				Datepicker();
				
				$('.filters').change(function(){
					$dataTable.DataTable().ajax.reload();
					//$quotationRecordTable.DataTable().ajax.reload();
				});
				
				$('.filters2').change(function(){
					//$dataTable.DataTable().ajax.reload();
					$quotationRecordTable.DataTable().ajax.reload();
				});
				
				<sec:authorize access="!(hasPermission(#user, 4) or hasPermission(#user, 256))">
					viewOnly();
				</sec:authorize>
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Assignment Reallocation Recommendation</h1>
          <c:if test="${act != 'add'}">
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
	      	</c:if>
        </section>
        
        <section class="content">
        	<form id="mainForm" action="<c:url value='/assignmentAllocationAndReallocation/AssignmentReallocationRecommendation/recommendReject'/>" method="post" role="form" >
        		<input name="assignmentReallocationId" value="<c:out value="${model.assignmentReallocationId}" />" type="hidden" />
        		<input name="assignmentIds" value="" type="hidden" />
        		<input name="recommendReject" type="hidden" />
				<input name="submitToUserId" value="<c:out value="${model.submitToUserId}" />" type="hidden" />
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
	        					<div class="row reject-container">
	        						<div class="col-md-1">
										<a class="btn btn-default" href="<c:url value='/assignmentAllocationAndReallocation/AssignmentReallocationRecommendation/home'/>">Back</a>
									</div>
									<label class="col-md-1 control-label reject-label">Remarks</label>
									<div class="form-group">
										<div class="col-md-3">
											<input name="rejectReason" type="text" class="form-control" value="${model.rejectReason}" maxlength="4000"  />
										</div>
									</div>
								</div>
							</div>
	       					<div class="box-body">
	       						<div class="form-horizontal">
									<div class="row">
										<label class="col-sm-1 control-label">Requester</label>
										<div class="col-md-3">
											<div class="input-group">
												<input name="creator" type="text" class="form-control" value="<c:out value="${model.creator}" />" readonly />
												<input id="creatorId" name="creatorId" value="<c:out value="${model.creatorId}" />" type="hidden" />
												<%--<div class="input-group-addon searchCreatorId">
													<i class="fa fa-search"></i>
												</div>--%>
											</div>
										</div>
										<label class="col-sm-1 control-label">Original Field Officer</label>
										<div class="col-md-3">
											<div class="input-group">
												<input name="originalUser" type="text" class="form-control" value="<c:out value="${model.originalUser}" />" readonly />
												<input id="originalUserId" name="originalUserId" value="<c:out value="${model.originalUserId}" />" type="hidden" />
												<%--<div class="input-group-addon searchOriginalUserId">
													<i class="fa fa-search"></i>
												</div>--%>
											</div>
										</div>
										<label class="col-sm-1 control-label">Target Field Officer</label>
										<div class="col-md-3">
											<div class="input-group">
												<input name="targetUser" type="text" class="form-control" value="<c:out value="${model.targetUser}" />" required readonly />
												<input id="targetUserId" name="targetUserId" value="<c:out value="${model.targetUserId}" />" type="hidden" />
												<%--<div class="input-group-addon searchTargetUserId">
													<i class="fa fa-search"></i>
												</div>--%>
											</div>
										</div>
									</div>
									<div class="row" style="margin-top: 10px;">
										<label class="col-sm-1 control-label">Submit To</label>
										<div class="col-md-3">
											<div class="input-group">
												<%--<select class="form-control" id="submitToApproveId" name="submitToApproveId" required >
													<option value=""></option>
													<c:forEach var="head" items="${heads}">
														<option value="<c:out value="${head.userId}" />">${head.staffCode} - ${head.chineseName}</option>
													</c:forEach>
												</select>--%>
												<input name="submitToApprove" type="text" class="form-control"
													value="<c:out value="${model.submitToApprove}" />" 
													readonly />
												<input id="submitToApproveId" name="submitToApproveId"
													value="<c:out value="${model.submitToApproveId}" />"
													type="hidden" />
												<div class="input-group-addon searchSubmitToApproveId">
													<i class="fa fa-search"></i>
												</div>
											</div>
										</div>
									</div>
									<div class="row" style="margin-top: 10px;">
										<label class="col-md-1 control-label">TPU</label>
										<div class="col-md-2">
											<select class="form-control select2 filters" id="tpuIds" name="tpuIds" multiple="multiple">
												<c:forEach var="tpu" items="${tpus}">
													<option value="<c:out value="${tpu.tpuId}" />">${tpu.code}</option>
												</c:forEach>
											</select>
										</div>
										<label class="col-md-1 control-label">Outlet Type</label>
										<div class="col-md-2">
											<select name="outletTypeId" class="form-control select2ajax filters"
												data-ajax-url="<c:url value='/assignmentAllocationAndReallocation/AssignmentReallocationRecommendation/queryOutletTypeSelect2'/>"
											></select>
										</div>
										<label class="col-md-1 control-label">District</label>
										<div class="col-md-2">
											<select name="districtId" class="form-control select2ajax filters" id="districtId"
												data-ajax-url="<c:url value='/assignmentAllocationAndReallocation/AssignmentReallocationRecommendation/queryDistrictSelect2'/>"/></select>
										</div>
									</div>
									<div class="row" style="margin-top: 10px;">
										<label class="col-md-1 control-label">Batch Code</label>
										<div class="col-md-2">
											<select name="batchId" class="form-control select2ajax filters" id="batchId"
												data-ajax-url="<c:url value='/assignmentAllocationAndReallocation/AssignmentReallocationRecommendation/queryBatchSelect2'/>"/></select>
										</div>
										<label class="col-md-1 control-label">Collection Date</label>
										<div class="col-md-2">
											<div class="input-group">
												<input type="text" name="collectionDate" class="form-control date-picker filters">
												<div class="input-group-addon">
													<i class="fa fa-calendar"></i>
												</div>
											</div>
										</div>
										<label class="col-md-1 control-label">Assignment Status</label>
										<div class="col-md-2">
											<select name="assignmentStatus" class="form-control filters" id="assignmentStatus">
												<option value=""></option>
												<option value="1">In Progress</option>
												<option value="2">Not Start</option>
											</select>
										</div>
									</div>
									<div class="row" style="margin-top: 10px;">
										<label class="col-md-1 control-label">Survey Month</label>
										<div class="col-md-2">
											<select name="surveyMonthId" class="form-control select2ajax filters" id="surveyMonthId"
												data-ajax-url="<c:url value='/assignmentAllocationAndReallocation/AssignmentReallocationRecommendation/querySurveyMonthSelect2'/>"/></select>
										</div>
										<label class="col-md-1 control-label">Selected</label>
										<div class="col-md-2">
											<div class="input-group">
												<select class="form-control filters" id="selected">
													<option value=""></option>
													<option value="Y">Y</option>
													<option value="N">N</option>
												</select>
											</div>
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
												<th>Assignment Status</th>
												<th>Selected(Y/N)</th>
												<th>Reject Reason</th>
												<th class="text-center action"></th>
											</tr>
											</thead>
											<tbody>
											</tbody>
										</table>
									</div>
								</div>
								<br/>
								<div class="form-horizontal">
									<div class="row" style="margin-top: 10px;">
										<label class="col-md-1 control-label">TPU</label>
										<div class="col-md-2">
											<select class="form-control select2 filters2" id="tpuIds2" name="tpuIds2" multiple="multiple">
												<c:forEach var="tpu" items="${tpus}">
													<option value="<c:out value="${tpu.tpuId}" />">${tpu.code}</option>
												</c:forEach>
											</select>
										</div>
										<label class="col-md-1 control-label">Outlet Type</label>
										<div class="col-md-2">
											<select name="outletTypeId2" class="form-control select2ajax filters2"
												data-ajax-url="<c:url value='/assignmentAllocationAndReallocation/AssignmentReallocationRecommendation/queryOutletTypeSelect2'/>"
											></select>
										</div>
										<label class="col-md-1 control-label">District</label>
										<div class="col-md-2">
											<select name="districtId2" class="form-control select2ajax filters2" id="districtId2"
												data-ajax-url="<c:url value='/assignmentAllocationAndReallocation/AssignmentReallocationRecommendation/queryDistrictSelect2'/>"/></select>
										</div>
									</div>
									<div class="row" style="margin-top: 10px;">
										<label class="col-md-1 control-label">Batch Code</label>
										<div class="col-md-2">
											<select name="batchId2" class="form-control select2ajax filters2" id="batchId2"
												data-ajax-url="<c:url value='/assignmentAllocationAndReallocation/AssignmentReallocationRecommendation/queryBatchSelect2'/>"/></select>
										</div>
										<label class="col-md-1 control-label">Collection Date</label>
										<div class="col-md-2">
											<div class="input-group">
												<input type="text" name="collectionDate2" class="form-control date-picker filters2">
												<div class="input-group-addon">
													<i class="fa fa-calendar"></i>
												</div>
											</div>
										</div>
										<label class="col-md-1 control-label">Category</label>
										<div class="col-md-2">
											<select name="category2" class="form-control select2ajax filters2" id="category2"
												data-ajax-url="<c:url value='/assignmentAllocationAndReallocation/AssignmentReallocationRecommendation/queryCategorySelect2'/>"/></select>
										</div>
									</div>
									<div class="row" style="margin-top: 10px;">
										<label class="col-md-1 control-label">Quotation Status</label>
										<div class="col-md-2">
											<select name="quotationStatus2" class="form-control filters2" id="quotationStatus2">
												<option value=""></option>
												<option value="Blank">Blank</option>
												<option value="Draft">Draft</option>
												<option value="Submitted">Submitted</option>
												<option value="Approved">Approved</option>
												<option value="Rejected">Rejected</option>
											</select>
										</div>
										<label class="col-md-1 control-label">Selected</label>
										<div class="col-md-2">
											<div class="input-group">
												<select class="form-control filters2" id="selected2">
													<option value=""></option>
													<option value="Y">Y</option>
													<option value="N">N</option>
												</select>
											</div>
										</div>
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
												<th>Collection Date</th>
												<th>Start Date</th>
												<th>End Date</th>
												<th>Display Name</th>
												<th>Category</th>
												<th>Quotation Status</th>
												<th>Selected(Y/N)</th>
											</tr>
											</thead>
											<tbody>
											</tbody>
										</table>
									</div>
								</div>
							</div>
							<sec:authorize access="hasPermission(#user, 4) or hasPermission(#user, 256)">
								<div class="box-footer">
									<div class="col-md-2">
		        						<button type="submit" class="btn btn-info" name="recommendRejectBtn" value="recommend">Recommend</button>
		        					</div>
		        					<div class="col-md-2">
		        						<button type="submit" class="btn btn-info" name="recommendRejectBtn" value="reject">Reject</button>
		        					</div>
		       					</div>
	       					</sec:authorize>
						</div>
	        		</div>
	        	</div>
        	</form>
        	
        	
        </section>
	</jsp:body>
</t:layout>

