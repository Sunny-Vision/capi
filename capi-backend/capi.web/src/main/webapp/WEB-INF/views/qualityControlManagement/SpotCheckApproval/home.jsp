<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
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
		<style>
		#dataList .discount span {
			display: inline-block;
			margin: 0 2px;
		}
		#dataList .discount span.number {
			border: solid 1px #d2d6de;
			padding: 0 20px;
		}
		.filter {
			margin-bottom: 10px;
		}
		.filter .form-group {
			margin-right: 10px;
		}
		.filter .form-control {
			margin-left: 10px;
		}
		.filter .form-control.select2 {
			width: 250px;
		}
		</style>
		<script>
			function approveRecordsWithConfirm(data) {
				bootbox.confirm({
					title:"Confirmation",
					message: "<spring:message code='W00004' />",
					callback: function(result){
						if (result){
							$.post("<c:url value='/qualityControlManagement/SpotCheckApproval/approveAtHome'/>",
								data,
								function(response) {
									$("#dataList").DataTable().ajax.reload();
									$("#MessageRibbon").html(response);
								}
							);
						}
					}
				});
			}
			
			function reject(data){
				$("input[name='id']").val(data);
	      		$('#rejectReasonForm .error').removeClass('error');
	      		$("#rejectReasonForm").modal('show');
	      	}
			
			function rejectRecordsWithConfirm(data) {
				bootbox.confirm({
					title:"Confirmation",
					message: "<spring:message code='W00005' />",
					callback: function(result){
						if (result){
							reject(data);
						}
					}
				});
			}
			
			$(document).ready(function(){
				$("#chkAll").on('change', function(){
		    		if (this.checked){
		    			$('.tblChk').prop('checked', true);
		    		}
		    		else{
		    			$('.tblChk').prop('checked', false);
		    		}
		    	});
				
				var $rejectReasonInputForm = $('#rejectReasonInputForm');
				
				$rejectReasonInputForm.validate();
				
				var $dataTable = $("#dataList");				
				
				$dataTable.DataTable({
					"order": [[ 1, "desc" ]],
					"searching": true,
					"ordering": true,
					<sec:authorize access="!(hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 256))">
					"buttons": [],
					</sec:authorize>
					<sec:authorize access="hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 256)">
					"buttons": [
						{
							"text": "Approve",
							"action": function( nButton, oConfig, flash ) {
								var data = $dataTable.find(':checked').serialize();
								if (data == '') {
									bootbox.alert({
										title: "Alert",
										message: "<spring:message code='E00009' />"
									});
									return;
								}
								approveRecordsWithConfirm(data);
							}
						},
						{
							"text": "Reject",
							"action": function( nButton, oConfig, flash ) {
								//var data = $dataTable.find(':checked').serialize();
								var data = [];
								$dataTable.find('[name="id"]:checked').each(function() {
									data.push($(this).val());
								});
								if (data == '') {
									bootbox.alert({
										title: "Alert",
										message: "<spring:message code='E00009' />"
									});
									return;
								}
								rejectRecordsWithConfirm(data);
							}
						}
					],
					</sec:authorize>
					"processing": true,
	                "serverSide": true,
	                //"ajax": "<c:url value='/qualityControlManagement/SpotCheckApproval/query'/>",
	                "ajax": {
	                	url: "<c:url value='/qualityControlManagement/SpotCheckApproval/query'/>",
	                	method: "post"
	                },
	                "columns": [
								{
									"data": "spotCheckFormId",
									"orderable": false,
									"searchable": false,
									"render" : function(data, type, full, meta){
										return "<input type='checkbox' value='"+data+"' name='id' class='tblChk' />";
									},
									"className" : "discount text-center"
								},
								{
									"data": "officerCode",
									"orderable": true,
									"searchable": true,
									"className" : "text-center"
								},
	                            {
	                            	"data": "officerName",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "officerTeam",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "survey",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "supervisorCode",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "checkerCode",
	                            	"orderable": true,
	                            	"searchable": false,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "spotCheckDate",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "spotCheckFormId",
	                            	"orderable": false,
	                            	"searchable": false,
	                            	"render" : function(data, type, full, meta){
	                            		var html = "<a href='<c:url value='/qualityControlManagement/SpotCheckApproval/edit'/>?act=edit&id="+data+"&officerId="+full.officerId+"&officerCode="+full.officerCode+"&spotCheckDate="+full.spotCheckDate+"' class='table-btn'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a>";
	                            		return html;
                            		},
	                            	"className" : "text-center"
                            	}
	                        ]
				});

				/*$('.form-control').change(function(){
					$dataTable.DataTable().ajax.reload();
				});*/
			});
			
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Spot Check Approval</h1>
        </section>
        
        <section class="content">
        	<div class="row">
				<div class="col-md-12" >
				<!-- content -->
						<!-- Itinerary Planning Table -->
						<div class="box" >
							<div class="box-body">
								<div class="clearfix">&nbsp;</div>
								<table class="table table-striped table-bordered table-hover" id="dataList">
									<thead>
									<tr>
										<th><input type="checkbox" id="chkAll" /></th>
										<th>Field Officer</th>
										<th>Chinese Name</th>
										<th>Team</th>
										<th>Survey</th>
										<th>Field Supervisor</th>
										<th>Checker</th>
										<th>Spot Check Date</th>
										<th class="text-center action"></th>
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
        
        <div class="modal fade" id="rejectReasonForm" tabindex="-1" role="dialog" aria-labelledby="dialogLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div  class="clearfix overlay" data-bind="loading:isLoading, visible:isLoading" ></div>
					<form action="<c:url value='/qualityControlManagement/SpotCheckApproval/rejectAtHome'/>" method="post" id="rejectReasonInputForm">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
							<h4 class="modal-title" id="dialogLabel" data-bind="text:formTitle"></h4>
						</div>
						<div class="modal-body form-horizontal">
							<div class="form-group">
								<div class="col-md-3 control-label">Reject Reason : </div>
							</div>
							<div class="form-group">
								<div class="col-md-1"></div>
								<div class="col-md-10">
									<input name="rejectReason" type="text" class="form-control" value="" maxlength="4000" required />
								</div>
							</div>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
							<button type="submit" class="btn btn-primary" data-loading-text="Loading..." id="rejectSubmitBtn">Submit</button>
						</div>
						<input name="id" value="" type="hidden" />
					</form>
				</div>
			</div>
		</div>
        
	</jsp:body>
</t:layout>
