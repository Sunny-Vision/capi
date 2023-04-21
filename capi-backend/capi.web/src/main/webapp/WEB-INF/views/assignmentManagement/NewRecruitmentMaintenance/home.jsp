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
			                           <sec:authorize access="not hasPermission(#user, 4)">
			                           ,{
			                        	   "targets": "person-in-charge",
			                        	   "visible": false
			                           }
			                           </sec:authorize>
										];
					
					var buttons = [];
					
					$dataTable.DataTable({
						"ordering": true, 
						"order": [[ 0, "desc" ]],
						"searching": true,
						"buttons": [],
						"processing": true,
			            "serverSide": true,
			            "ajax": {
			            	url: tableUrl,
			            	data: function(dataModel) {
			            		dataModel.personInChargeId = $('[name="personInChargeId"]', $mainForm).val();
			            		dataModel.surveyMonthId = $('[name="surveyMonthId"]', $mainForm).val();
			            		dataModel.assignmentStatus = $('[name="assignmentStatus"]', $mainForm).val();
			            		dataModel.deadline = $('[name="deadline"]', $mainForm).val();
			            		var outletTypeIds = $('[name="outletTypeId"]', $mainForm).val();
			            		if (outletTypeIds != null) {
			            			for (var i = 0; i < outletTypeIds.length; i++) {
			            				outletTypeIds[i] = outletTypeIds[i];
			            			}
			            			dataModel.outletTypeId = outletTypeIds;
			            		}
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
			            			$.get('<c:url value="/assignmentManagement/NewRecruitmentMaintenance/getOutletTypesDisplayByOutletId"/>?id=' + id, function(list) {
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
			            }
					});
					
					$('.filters', $mainForm).change(function(){
						$dataTable.DataTable().ajax.reload();
					});
					
					$('.select2ajax', $mainForm).select2ajax();
					$('.date-picker', $mainForm).datepicker();
				}
				
				initTabPane($('.box-primary'), '<c:url value="/assignmentManagement/NewRecruitmentMaintenance/query"/>',
						[
	                        { "data": "referenceNo" },
	                        { "data": "firm",
	                        	"render" : function(data, type, row) {
	                        		if (data == null) return '';
	                        		return data
	                        			+ '<button type="button" class="btn btn-xs btn-outlet-tooltip" data-id="' + row.outletId + '" data-trigger="click"><i class="fa fa-info"></i></button>';
	                        	}
	                        },
	                        { "data": "district" },
	                        { "data": "tpu" },
	                        { "data": "noOfQuotation" },
	                        { "data": "personInCharge" },
	                        { "data": "firmStatus" },
	                        { "data": "assignmentStatus" },
	                        { "data": "deadline" },
	                        { "data": "assignmentId",
	                        	"render": function(data, type, row) {
	                        		return '<a href="<c:url value="/assignmentManagement/NewRecruitmentMaintenance/edit?assignmentId="/>'+data+'&userId=' + row.userId + '" class="table-btn"><span class="glyphicon glyphicon-pencil" aria-hidden="true"></span></a>';
	                        	}
	                        }
						]);
				
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>New Recruitment Maintenance</h1>
        </section>
        
        <section class="content">
        	<div class="row">
				<div class="col-md-12">
				<!-- content -->
        			<div class="box box-primary">
       					<div class="box-body">
							<form class="form-horizontal" id="mainForm">
								<div class="row">
									<label class="col-md-2 control-label">Person in charge</label>
									<div class="col-md-2">
										<select name="personInChargeId" class="form-control select2ajax filters"
											data-allow-clear="true"
											data-placeholder=""
											data-ajax-url="<c:url value='/assignmentManagement/NewRecruitmentMaintenance/queryOfficerSelect2'/>"></select>
									</div>
									<label class="col-md-2 control-label">Survey Month</label>
									<div class="col-md-3">
										<select name="surveyMonthId" class="form-control select2ajax filters"
											data-allow-clear="true"
											data-placeholder=""
											data-ajax-url="<c:url value='/assignmentManagement/NewRecruitmentMaintenance/querySurveyMonthSelect2'/>"></select>
									</div>
									<label class="col-md-1 control-label">Status</label>
									<div class="col-md-2">
										<select name="assignmentStatus" class="form-control select2 filters"
											data-allow-clear="true"
											data-placeholder="">
											<option></option>
											<option value="1">In Progress</option>
											<option value="2">Not Start</option>
										</select>
									</div>
								</div>
								<div class="row" style="margin-top: 10px;">
									<label class="col-md-2 control-label">Deadline/collection date</label>
									<div class="col-md-2">
										<div class="input-group">
											<input type="text" name="deadline" class="form-control date-picker filters">
											<div class="input-group-addon">
												<i class="fa fa-calendar"></i>
											</div>
										</div>
									</div>
									<label class="col-md-2 control-label">Outlet Type</label>
									<div class="col-md-3">
										<select name="outletTypeId" class="form-control select2ajax filters"
											data-close-on-select="false"
											data-ajax-url="<c:url value='/assignmentManagement/NewRecruitmentMaintenance/queryOutletTypeSelect2'/>" multiple></select>
									</div>
								</div>
								<div class="row" style="margin-top: 10px;">
									<div class="col-md-3 col-md-offset-1">
										<button type="reset" class="btn btn-info">Clear</button>
									</div>
								</div>
							</form>
							<hr/>
							<table class="table table-striped table-bordered table-hover data-table">
								<thead>
								<tr>
									<th>Reference No</th>
									<th>Firm</th>
									<th>District</th>
									<th>TPU</th>
									<th>No. of Quotation</th>
									<th class="person-in-charge">Person in charge</th>
									<th>Firm Status</th>
									<th>Assignment Status</th>
									<th>Deadline / Collection date</th>
									<th class="text-center action" data-priority="1"></th>
								</tr>
								</thead>
							</table>
						</div>
					</div>
				</div>
			</div>
        </section>
	</jsp:body>
</t:layout>

