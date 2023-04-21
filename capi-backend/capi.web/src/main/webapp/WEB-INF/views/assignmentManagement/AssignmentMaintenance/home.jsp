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
				var cacheLastSearch = null;
				
				var tableOrder = [[ 0, "desc" ]];
				if ($('#lastRequestModelOrderColumn').val() != '') {
					tableOrder = [[ $('#lastRequestModelOrderColumn').val(), $('#lastRequestModelOrderDir').val() ]];
				}
				
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
					try{
						$dataTable.DataTable({
							"ordering": true, 
							"order": tableOrder,
							"iDisplayLength": 25,
							"searching": true,
							"search" : {
			                	"search" : $('#lastRequestModelSearch').val()
			                },
							"buttons": [],
							"processing": true,
				            "serverSide": true,
				            "ajax": {
				            	url: tableUrl,
				            	data: function(dataModel) {
				            		dataModel.personInChargeId = $('[name="personInChargeId"]', $mainForm).val();
				            		dataModel.surveyMonthId = $('[name="surveyMonthId"]', $mainForm).val();
				            		dataModel.assignmentStatus = $('[name="assignmentStatus"]', $mainForm).val();
				            		dataModel.quotationState = $('[name="quotationState"]', $mainForm).val();
				            		dataModel.deadline = $('[name="deadline"]', $mainForm).val();
				            		var outletTypeIds = $('[name="outletTypeId"]', $mainForm).val();
				            		if (outletTypeIds != null) {
				            			for (var i = 0; i < outletTypeIds.length; i++) {
				            				outletTypeIds[i] = outletTypeIds[i];
				            			}
				            			dataModel.outletTypeId = outletTypeIds;
				            		}
				            		dataModel.districtId = $('[name="districtId"]', $mainForm).val();
				            		dataModel.search["personInChargeId"] = $('[name="personInChargeId"]', $mainForm).val();
				            		dataModel.search["surveyMonthId"] = $('[name="surveyMonthId"]', $mainForm).val();
				            		dataModel.search["assignmentStatus"] = $('[name="assignmentStatus"]', $mainForm).val();
				            		dataModel.search["quotationState"] = $('[name="quotationState"]', $mainForm).val();
				            		dataModel.search["deadline"] = $('[name="deadline"]', $mainForm).val();
				            		dataModel.search["districtId"] = $('[name="districtId"]', $mainForm).val() == null ? "" : $('[name="districtId"]', $mainForm).val().join();
				            		dataModel.search["outletTypeId"] = $('[name="outletTypeId"]', $mainForm).val() == null ? "" : $('[name="outletTypeId"]', $mainForm).val().join();
				            		
				            		if ($mainForm.selector.includes("original")){
				            			cacheLastSearch = dataModel;
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
				            			$.get('<c:url value="/assignmentManagement/AssignmentMaintenance/getOutletTypesDisplayByOutletId"/>?id=' + id, function(list) {
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
					} catch(e) {
						//alert(e);
						console.log("Error", e.message);
					}
					
					
					$('.filters', $mainForm).change(function(){
						$dataTable.DataTable().ajax.reload();
					});
					
					$('.select2ajax', $mainForm).select2ajax();
					$('.date-picker', $mainForm).datepicker();
					
					$('[name="clearBtn"]', $mainForm).on('click', function() {
						$mainForm[0].reset();
						/* $('[name="quotationState"]').find("option").remove(); */
						$('[name="quotationState"]').find("option")[0].selected = true;
						$('.criteria', $mainForm).select2ajax('destroy');
						$('.criteria', $mainForm).find("option").remove();
						$('.criteria', $mainForm).select2ajax();
						$dataTable.DataTable().ajax.reload();
					});
					
				}
				
				initTabPane($('#original'), '<c:url value="/assignmentManagement/AssignmentMaintenance/query"/>',
						[
						 	{ "data": "referenceMonth" },
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
	                        		return '<button type="button" data-href="<c:url value="/assignmentManagement/AssignmentMaintenance/edit?assignmentId="/>'+data
	                        				+ (row.userId != null ? '&userId=' + row.userId : '') + '" class="table-btn btn-link"><span class="glyphicon glyphicon-pencil" aria-hidden="true"></span></button>';
	                        	}
	                        }
						]);
				initTabPane($('#imported'), '<c:url value="/assignmentManagement/AssignmentMaintenance/queryImported"/>',
						[
							{ "data": "referenceMonth" },
							{ "data": "survey" },
	                        { "data": "referenceNo" },
	                        { "data": "firm",
	                        	"render" : function(data, type, row) {
	                        		if (data == null) return '';
	                        		return data
	                        			+ '<button type="button" class="btn btn-xs btn-outlet-tooltip" data-id="' + row.outletId + '"><i class="fa fa-info"></i></button>';
	                        	}
	                        },
	                        { "data": "district" },
	                        { "data": "tpu" },
	                        { "data": "address" },
	                        { "data": "noOfForms" },
	                        { "data": "personInCharge" }
	                        <sec:authorize access="hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)">
	                        , { "data": "assignmentId",
	                        	"render": function(data) {
	                        		return '<a href="#" class="table-btn btn-delete" data-id="' + data + '"><span class="fa fa-times" aria-hidden="true"></span></a>';
	                        	}
	                        }
	                        </sec:authorize>
						]);
				
				$('#imported').on('click', '.btn-delete', function(e) {
					e.preventDefault();
					var id = $(this).data('id');
					bootbox.confirm({
						title:"Confirmation",
						message: "<spring:message code='W00001' />",
						callback: function(result){
							if (result){
								$.post("<c:url value='/assignmentManagement/AssignmentMaintenance/deleteAssignment'/>",
									{ id : id },
									function(response) {
										$("#imported .data-table").DataTable().ajax.reload();
										$("#MessageRibbon").html(response);
									}
								);
							}
						}
					});
				});
				
				$('.nav-tabs li a').click(function(e) {
					e.preventDefault();
					var showPanelId = $(this).attr('href');
					$('.tab-pane').removeClass('active');
					$(showPanelId).addClass('active');
					
					$('.nav-tabs li').removeClass('active');
					$(this).closest('li').addClass('active');
				});
				
				$('.tab-content').on('click', 'button[data-href]', function() {
					var d = cacheLastSearch;
					
					var href = $(this).data('href');
					
					Modals.startLoading();

					$.post("<c:url value='/assignmentManagement/AssignmentMaintenance/saveLastRequestModel'/>", d, function(){
						location = href;
					});
					
					
				});
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Assignment Maintenance</h1>
        </section>
        
        <section class="content">
        	<div class="row">
				<div class="col-md-12">
				<!-- content -->
					<div class="nav-tabs-custom">
						<ul class="nav nav-tabs">
							<li class="active"><a href="#original">Original</a></li>
							<sec:authorize access="hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 16) or hasPermission(#user, 256)">
							<li><a href="#imported">Imported</a></li>
							</sec:authorize>
						</ul>
						<div class="tab-content">
							<div id="original" class="tab-pane active">
								<form class="form-horizontal" id="mainForm">
									<input id="lastRequestModelSearch" value="<c:out value="${viewModel.search}"/>" type="hidden" />
									<input id="lastRequestModelOrderColumn" value="<c:out value="${viewModel.orderColumn}"/>" type="hidden" />
									<input id="lastRequestModelOrderDir" value="<c:out value="${viewModel.orderDir}"/>" type="hidden" />
									<div class="row">
										<label class="col-md-2 control-label">Person in charge</label>
										<div class="col-md-2">
											<select name="personInChargeId" class="form-control select2ajax filters criteria"
												data-allow-clear="true"
												data-placeholder=""
												data-ajax-url="<c:url value='/assignmentManagement/AssignmentMaintenance/queryOfficerSelect2'/>">
												<c:if test="${viewModel.personInChargeSelected != null}">
													<option value="<c:out value="${viewModel.personInChargeSelected.value}"/>" selected>
														<c:out value="${viewModel.personInChargeSelected.key}"/>
													</option>
												</c:if>
											</select>
										</div>
										<label class="col-md-2 control-label">Survey Month</label>
										<div class="col-md-3">
											<select name="surveyMonthId" class="form-control select2ajax filters criteria"
												data-allow-clear="true"
												data-placeholder=""
												data-ajax-url="<c:url value='/assignmentManagement/AssignmentMaintenance/querySurveyMonthSelect2'/>">
												<c:if test="${viewModel.surveyMonthSelected != null}">
													<option value="<c:out value="${viewModel.surveyMonthSelected.value}"/>" selected>
														<c:out value="${viewModel.surveyMonthSelected.key}"/>
													</option>
												</c:if>	
											</select>
										</div>
										<label class="col-md-1 control-label">Status</label>
										<div class="col-md-2">
											<select name="assignmentStatus" class="form-control select2 filters"
												data-allow-clear="true"
												data-placeholder="">
												<option></option>
												<option value="1" ${viewModel.assignmentStatus == "1" ? "selected" : ""}>In Progress</option>
												<option value="2" ${viewModel.assignmentStatus == "2" ? "selected" : ""}>Not Start</option>
											</select>
										</div>
									</div>
									<div class="row" style="margin-top: 10px;">
										<label class="col-md-2 control-label">Deadline/collection date</label>
										<div class="col-md-2">
											<div class="input-group">
												<input type="text" name="deadline" class="form-control date-picker filters" 
													value="${!empty deadline? deadline : viewModel.deadline}">
												<div class="input-group-addon">
													<i class="fa fa-calendar"></i>
												</div>
											</div>
										</div>
										<label class="col-md-2 control-label">Outlet Type</label>
										<div class="col-md-3">
											<select name="outletTypeId" class="form-control select2ajax filters criteria"
												data-close-on-select="false"
												data-ajax-url="<c:url value='/assignmentManagement/AssignmentMaintenance/queryOutletTypeSelect2'/>" multiple>
												<c:forEach items="${viewModel.outletTypeSelected}" var="outletTypeSelected">
											    	<option value="<c:out value="${outletTypeSelected.value}"/>" selected><c:out value="${outletTypeSelected.key}"/></option>
												</c:forEach>	
											</select>
										</div>
										<label class="col-md-1 control-label">District</label>
										<div class="col-md-2">
											<select name="districtId" class="form-control select2ajax filters criteria"
												data-close-on-select="false"
												data-ajax-url="<c:url value='/assignmentManagement/AssignmentMaintenance/queryDistrictSelect2'/>" multiple>
												<c:forEach items="${viewModel.districtSelected}" var="districtSelected">
											    	<option value="<c:out value="${districtSelected.value}"/>" selected><c:out value="${districtSelected.key}"/></option>
												</c:forEach>	
											</select>
										</div>
									</div>
									<div class="row" style="margin-top: 10px;">
										<label class="col-md-2 control-label">Quotation State</label>
										<div class="col-md-2">
											<select name="quotationState" class="form-control select2 filters"
												data-allow-clear="true"
												data-placeholder="">
												<option value=""></option>
												<option value="Revisit" ${viewModel.quotationState == 'Revisit' ? "selected" : "" }>Revisit</option>
												<option value="Verify" ${viewModel.quotationState == 'Verify' ? "selected" : "" }>Verify</option>
												<option value="IP" ${viewModel.quotationState == 'IP' ? "selected" : "" }>IP</option>
											</select>
										</div>
									</div>
									<div class="row" style="margin-top: 10px;">
										<div class="col-md-11 col-md-offset-1">
											<button name="clearBtn" type="button" class="btn btn-info">Clear</button>
											<a class="btn btn-info" href="<c:url value='/assignmentManagement/QuotationMaintenance/home?selectRUA=true'/>">RUA Case</a>
										</div>
									</div>
								</form>
								<hr/>
								<table class="table table-striped table-bordered table-hover data-table">
									<thead>
									<tr>
										<th>Reference Month</th>
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
							
							<sec:authorize access="hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 16) or hasPermission(#user, 256)">
							<div id="imported" class="tab-pane">
								<form class="form-horizontal" id="mainForm2">
									<div class="row">
										<label class="col-md-2 control-label">Person in charge</label>
										<div class="col-md-2">
											<select name="personInChargeId" class="form-control select2ajax filters criteria"
												data-allow-clear="true"
												data-placeholder=""
												data-ajax-url="<c:url value='/assignmentManagement/AssignmentMaintenance/queryOfficerForImportedSelect2'/>"></select>
										</div>
										<label class="col-md-2 control-label">Survey Month</label>
										<div class="col-md-3">
											<select name="surveyMonthId" class="form-control select2ajax filters criteria"
												data-allow-clear="true"
												data-placeholder=""
												data-ajax-url="<c:url value='/assignmentManagement/AssignmentMaintenance/querySurveyMonthSelect2'/>"></select>
										</div>
									</div>
									<div class="row" style="margin-top: 10px;">
										<div class="col-md-3 col-md-offset-1">
											<button name="clearBtn" type="button" class="btn btn-info">Clear</button>
										</div>
									</div>
								</form>
								<hr/>
								<table class="table table-striped table-bordered table-hover data-table">
									<thead>
									<tr>
										<th>Reference Month</th>
										<th>Survey</th>
										<th>Reference No</th>
										<th>Firm</th>
										<th>District</th>
										<th>TPU</th>
										<th>Address</th>
										<th>No. of forms</th>
										<th class="person-in-charge">Person in charge</th>
										<th class="text-center action" data-priority="1"></th>
									</tr>
									</thead>
								</table>
							</div>
							</sec:authorize>
							<!-- /.tab-pane -->
						</div>
						<!-- /.tab-content -->
					</div>
				</div>
			</div>
        </section>
	</jsp:body>
</t:layout>

