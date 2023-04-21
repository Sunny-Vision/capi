<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
	</jsp:attribute>
	<jsp:attribute name="header">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker.jsp"%>
		<script>
			$(document).ready(function(){
				function initTabPane(tabPane, tableUrl, columns) {
					var $mainForm = $('form', tabPane);
					
					var lastQueryDataModel = null;
					
					// fix select2 not clear bug
					$mainForm.on('reset', function() {
						setTimeout(function() {
							$mainForm.find('select').val(null);
						});
					});
	
					var $dataTable = $(".data-table", tabPane);
					
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
					<sec:authorize access="hasPermission(#user, 4)">
					buttons.push({
						"text": "Approve",
						"action": function( nButton, oConfig, flash ) {
							if ($('[name="id[]"]:checked').length == 0) return;
							$('[name="approveRejectBtn"]').val("approve");
							$('#frmSubmit').submit();
						}
					});
					buttons.push({
						"text": "Reject",
						"action": function( nButton, oConfig, flash ) {
							if ($('[name="id[]"]:checked').length == 0) return;
							$('[name="approveRejectBtn"]').val("reject");
							$('#rejectReasonDialog').modal('show');
						}
					});
					</sec:authorize>
					
					$dataTable.DataTable({
						"ordering": true, 
						"order": [[ 1, "asc" ]],
						"searching": true,
						"buttons": buttons,
						"processing": true,
			            "serverSide": true,
			            "ajax": {
			            	url: tableUrl,
			            	data: function(dataModel) {
			            		dataModel.outletTypeId = $('[name="outletTypeId"]', $mainForm).val();
			            		dataModel.personInChargeId = $('[name="personInChargeId"]', $mainForm).val();
			            		dataModel.districtId = $('[name="districtId"]', $mainForm).val();
			            		dataModel.tpuId = $('[name="tpuId"]', $mainForm).val();
			            		
			            		lastQueryDataModel = dataModel;
			            	},
			            	method: 'post'
			            },
			            "columns": columns,
			            "columnDefs": columnDefs,
			            "drawCallback": function() {
			            	$('.btn-outlet-tooltip', this).tooltip({
			            		title: function() {
			            			var $btn = $(this);
			            			
			            			if ($btn.data('title') != null) {
			            				return $btn.data('title');
			            			}
			            			
			            			var id = $btn.data('id');
			            			$.get('<c:url value="/assignmentManagement/RUACaseApproval/getOutletTypesDisplayByOutletId"/>?id=' + id, function(list) {
			            				var title = 'empty';
			            				if (list != null) {
			            					title = list.join("<br/>");
			            				}
			            				$btn.data('title', title);
			            				$btn.tooltip('show');
			            			});
			            		},
			            		html: true
			            	});
		    				$(".tblChk").click(function (evt){
		    					evt.stopPropagation();
		    				});
			            }
					});
					
					$('.filters', $mainForm).change(function(){
						$dataTable.DataTable().ajax.reload();
					});
					
					$('.select2ajax', $mainForm).select2ajax();
					$('.date-picker', $mainForm).datepicker();
				}
				
				initTabPane($('.box-primary'), '<c:url value="/assignmentManagement/RUACaseApproval/query"/>',
						[
						 	{ "data": "assignmentId",
	                        	"render": function(data, type, row) {
	                        		return "<input type='checkbox' name='id[]' value='"+data+"' class='tblChk'/>";
	                        	}
						 	},
	                        { "data": "firm",
	                        	"render" : function(data, type, row) {
	                        		if (data == null) return '';
	                        		return data
	                        			+ '<button type="button" class="btn btn-xs btn-outlet-tooltip" data-id="' + row.outletId + '" data-trigger="click"><i class="fa fa-info"></i></button>';
	                        	}
	                        },
	                        { "data": "outletType" },
	                        { "data": "noOfOutletType" },
	                        { "data": "district" },
	                        { "data": "tpu" },
	                        { "data": "noOfQuotation" },
	                        { "data": "noOfRUA" },
	                        { "data": "personInCharge" },
	                        { "data": "assignmentStatus" },
	                        { "data": "assignmentId",
	                        	"render": function(data, type, row) {
	                        		return '<a href="<c:url value="/assignmentManagement/RUACaseApproval/edit?assignmentId="/>'+data + '" class="table-btn"><span class="glyphicon glyphicon-pencil" aria-hidden="true"></span></a>';
	                        	}
	                        }
						]);
				
				$('#rejectReasonDialog form').validate();
				
				$('#rejectReasonDialog form').submit(function() {
					if (!$(this).valid()) return false;
					var rejectReason = $('#rejectReasonDialog [name="rejectReason"]').val();
					$('#frmSubmit [name="rejectReason"]').val(rejectReason);
					$('#frmSubmit').submit();
					return false;
				});
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>RUA Case Approval</h1>
        </section>
        
        <section class="content">
        	<div class="row">
				<div class="col-md-12">
				<!-- content -->
        			<div class="box box-primary">
       					<div class="box-body">
							<form class="form-horizontal" id="mainForm">
								<div class="row">
									<label class="col-md-1 control-label">Outlet Type</label>
									<div class="col-md-2">
										<select name="outletTypeId" class="form-control select2ajax filters"
											data-allow-clear="true"
											data-ajax-url="<c:url value='/assignmentManagement/RUACaseApproval/queryOutletTypeSelect2'/>"></select>
									</div>
									<label class="col-md-1 control-label">Person in charge</label>
									<div class="col-md-2">
										<select name="personInChargeId" class="form-control select2ajax filters"
											data-allow-clear="true"
											data-placeholder=""
											data-ajax-url="<c:url value='/assignmentManagement/RUACaseApproval/queryOfficerSelect2'/>"></select>
									</div>
									<label class="col-md-1 control-label">District</label>
									<div class="col-md-2">
										<select name="districtId" class="form-control select2ajax filters"
											data-allow-clear="true"
											data-placeholder=""
											data-ajax-url="<c:url value='/assignmentManagement/RUACaseApproval/queryDistrictSelect2'/>"
											data-close-on-select="false"
											multiple></select>
									</div>
									<label class="col-md-1 control-label">TPU</label>
									<div class="col-md-2">
										<select name="tpuId" class="form-control select2ajax filters"
											data-allow-clear="true"
											data-placeholder=""
											data-ajax-url="<c:url value='/assignmentManagement/RUACaseApproval/queryTpuSelect2'/>"
											data-close-on-select="false"
											multiple></select>
									</div>
								</div>
								<div class="row" style="margin-top: 10px;">
									<div class="col-md-3 col-md-offset-1">
										<button type="reset" class="btn btn-info">Clear</button>
									</div>
								</div>
							</form>
							<hr/>
							<form id="frmSubmit" method="post" action="<c:url value='/assignmentManagement/RUACaseApproval/approveRejectAssignments'/>">
								<input name="approveRejectBtn" type="hidden"/>
								<input name="rejectReason" type="hidden"/>
							<table class="table table-striped table-bordered table-hover data-table">
								<thead>
								<tr>
									<th class="text-center action" ><input class="select_all" type="checkbox" /></th>
									<th>Firm</th>
									<th>Outlet Type</th>
									<th>No. of outlet type</th>
									<th>District</th>
									<th>TPU</th>
									<th>No. of quotation</th>
									<th>No. of RUA</th>
									<th class="person-in-charge">Field Officer</th>
									<th>Assignment Status</th>
									<th class="text-center action" data-priority="1"></th>
								</tr>
								</thead>
							</table>
							</form>
						</div>
					</div>
				</div>
			</div>
        </section>
        
        <%@include file="/WEB-INF/views/assignmentManagement/shared/rejectReasonDialog.jsp"%>
	</jsp:body>
</t:layout>

