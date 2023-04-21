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
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/datejs.jsp" %>
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
		<%@include file="/WEB-INF/views/includes/datepicker.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/peCertaintyCaseLookup.jsp" %>
		<script src="<c:url value='/resources/js/underscore-min.js'/>" ></script>
		<script>
			$(function() {
				$("#chkAll").on('change', function(){
		    		if (this.checked){
		    			$('.tblChk').prop('checked', true);
		    		}
		    		else{
		    			$('.tblChk').prop('checked', false);
		    		}
		    	});
				
				var $mainForm = $('#mainForm');
				
				$mainForm.validate();
				
				$('.select2ajax').select2ajax({
					allowClear: true,
					placeholder: '',
					width:'100%'
				});
				
				$('.select2ajax').hide();
				
				var $dataTable = $("#dataList");
				
				$dataTable.DataTable({
					"order": [[ 0, "desc" ]],
					"searching": false,
					"ordering": true,
					<sec:authorize access="!(hasPermission(#user, 1) or hasPermission(#user, 256))">
					"buttons": [],
					</sec:authorize>
					<sec:authorize access="hasPermission(#user, 1) or hasPermission(#user, 256)">
					"buttons": [],
					</sec:authorize>
					"processing": true,
	                "serverSide": false,
	                "paging": false,
	                "columns": [
								{
									"data": "outletCode",
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
	                            	"data": "batchCode",
	                            	"orderable": false,
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
	                            	"data": "address",
	                            	"orderable": true,
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
	                            	"data": "assignmentId",
	                            	"orderable": false,
	                            	"searchable": false,
	                            	"render" : function(data, type, full, meta){
	                            		/*var html = "<a href='javascript:void(0)' target='_blank'><i class='fa fa-list'></i></a>";
	                            		html += "<input name='assignmentIds' type='hidden' value='" + data + "'/>";
	                            		return html;*/
	                            		
	                            		var html = "<a href='<c:url value='/assignmentManagement/PEViewAssignmentMaintenance/edit?assignmentId='/>"+data+"' target='_blank'><i class='fa fa-list'></i></a>";
	                            		html += "<input name='assignmentIds' type='hidden' value='" + data + "'/>";
	                            		return html;
                            		},
	                            	"className" : "text-center"
                            	},
	                            {
	                            	"data": "assignmentId",
	                            	"orderable": false,
	                            	"searchable": false,
	                            	"render" : function(data, type, full, meta){
	                            		var html = "";
	                            		
	                            		if(!full.isSelected)
	                            			html += "<a href='#' class='table-btn btn-delete' ><span class='fa fa-times' aria-hidden='true'></span></a>";
	                            		else
	                            			html += "";
	                            		
	                            		html += "<input name='assignmentId' type='hidden' value='" + data + "'/>";
	                            		return html;
                            		},
	                            	"className" : "text-center"
                            	}
	                        ],
                       "fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
   	                	//console.log('testing.');
   	                }
				});
				
				var surveyMonthId = $('[name="surveyMonthId"]').val();
				
				var referenceMonth = $('[name="referenceMonth"]').val();
				
				//var oldChildIds = $('[name="peCheckTaskIdList"]').val().split(',');
				
				$.get("<c:url value='/qualityControlManagement/PostEnumerationCertaintyCaseMaintenance/getPECertaintyCaseDetail'/>",
					{surveyMonthId: surveyMonthId},
					function(data) {
						$dataTable.DataTable().rows().remove();
						$dataTable.DataTable().rows.add(data).draw();
					}
				);
				
				$('.searchPECertaintyCase').peCertaintyCaseLookup({
					selectedIdsCallback: function(selectedIds) {
						
						var existingIds = $($dataTable.DataTable().rows().data()).map(function(){
							  return this.assignmentId;
						}).get();
						
						var differenceIds =  _.difference(selectedIds, existingIds);
						var newIds = $.merge(differenceIds, existingIds);
						
						console.log(existingIds);
						
						$('[name="assignmentIdList"]').val(newIds);
						
						$.post("<c:url value='/qualityControlManagement/PostEnumerationCertaintyCaseMaintenance/getPECertaintyCaseDetail'/>",
							{surveyMonthId: surveyMonthId, assignmentIds: newIds},
							function(data) {
								$dataTable.DataTable().rows().remove();
								$dataTable.DataTable().rows.add(data).draw();
								
								$('[name="selectedAssignmentsLabel"]').text(data.length);
							}
						);
					},
					queryDataCallback: function(model) {
						model.referenceMonth = $('[name="referenceMonth"]').val();
						model.assignmentIdList = $('[name="assignmentIdList"]').val();
					}
				});
				
				$dataTable.on('click', '.btn-delete', function(e) {
					e.preventDefault();
					var deleteId = $($dataTable.DataTable().row($(this).parents('tr')).data()).map(function(){
						return this.assignmentId;
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
								
								var finalSelected = $('[name="selectedAssignmentsLabel"]').text();
								$('[name="selectedAssignmentsLabel"]').text((+finalSelected) - 1);
							}
						}
					});
				});
				
				
				<sec:authorize access="!(hasPermission(#user, 1) or hasPermission(#user, 256))">
					viewOnly();
				</sec:authorize>
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Post-Enumeration Certainty Case Maintenance</h1>
          <%--
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
	       --%>
        </section>
        
        <section class="content">
        	<form id="mainForm" action="<c:url value='/qualityControlManagement/PostEnumerationCertaintyCaseMaintenance/confirm'/>" method="post" role="form">
        		<input name="surveyMonthId" value="<c:out value="${model.surveyMonthId}" />" type="hidden" />
        		<input name="referenceMonth" value="<c:out value="${referenceMonth}" />" type="hidden" />
        		<input name="assignmentIdList" value="<c:forEach var="id" items="${model.assignmentIds}">${id},</c:forEach>" type="hidden" />
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/qualityControlManagement/PostEnumerationCertaintyCaseMaintenance/home'/>">Back</a>
							</div>
	       					<div class="box-body">
	       						<div class="form-horizontal">
	       							<div class="col-sm-12">
		       							<div class="form-group">
		       								<label class="col-sm-3 control-label">Reference Month</label>
		       								<div class="col-sm-4">
												<p class="form-control-static" >${referenceMonth}</p>
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-3 control-label">Total No. of Assignments</label>
		       								<div class="col-sm-4">
												<p class="form-control-static" >${model.totalNoOfAssignments}</p>
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-3 control-label">Selected Assignments</label>
		       								<div class="col-sm-4">
												<!--<div class="form-control-static" >${model.selectedAssignments}</div>-->
												<label class="form-control-static" style="font-weight:normal;" name="selectedAssignmentsLabel">
													${model.selectedAssignments}
												</label>
											</div>
										</div>
									</div>
									<hr/>
	       							<div class="col-sm-12">
	       								<div class="clearfix">&nbsp;</div>
										<table class="table table-striped table-bordered table-hover" id="dataList">
											<thead>
											<tr>
												<th>Firm Code</th>
												<th>Firm</th>
												<th>Batch Code</th>
												<th>Collection Date</th>
												<th>District</th>
												<th>TPU</th>
												<th>Address</th>
												<th>No. of Quotation</th>
												<th></th>
												<th class="text-center action"></th>
											</tr>
											</thead>
											<tbody>
											</tbody>
										</table>
										<sec:authorize access="hasPermission(#user, 1) or hasPermission(#user, 256)">
										
												<button type="button" class="btn btn-default btn-add searchPECertaintyCase">
													<i class="fa fa-plus"></i> Add Post-Enumeration Certainty Case
												</button>
										
										</sec:authorize>
									</div>
								</div>
							</div>
	       					<sec:authorize access="hasPermission(#user, 1) or hasPermission(#user, 256)">
	       						<c:if test="${!model.randomCreated}">
									<div class="box-footer">
		        						<button type="submit" class="btn btn-info">Confirm</button>
		       						</div>
	       						</c:if>
	       					</sec:authorize>
						</div>
	        		</div>
	        	</div>
        	</form>
        </section>
	</jsp:body>
</t:layout>
