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
				
				var tableOrder = [[ 2, "asc" ]];
				if ($('#lastRequestModelOrderColumn').val() != '') {
					tableOrder = [[ $('#lastRequestModelOrderColumn').val(), $('#lastRequestModelOrderDir').val() ]];
				}
			
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
					<sec:authorize access="hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)">
					buttons.push({
						"text": "Approve",
						"action": function( nButton, oConfig, flash ) {
							if ($('[name="id[]"]:checked').length == 0) return;
							Modals.startLoading();
							$('[name="approveRejectBtn"]').val("approve");
							$('#frmSubmit').submit();
						}
					});
					
					buttons.push({
						"text": "Approve All",
						"action": function(nButton, oConfig, flash){
							var searchValue = $(this).dataTableSettings[0].oPreviousSearch.sSearch;
							bootbox.confirm({
								title:"Confirmation",
								message: "<spring:message code='W00004' />",
								callback: function(result) {
									if (result){
										Modals.startLoading();
										$.ajax({
											method: 'post',
											url: '<c:url value="/assignmentManagement/AssignmentApproval/approveAllQuotationRecords" />',
											data: {
												search: searchValue,
												outletId: $('[name="outletId"]', $mainForm).val(),
												outletTypeId: $('[name="outletTypeId"]', $mainForm).val(),
												personInChargeId: $('[name="personInChargeId"]', $mainForm).val(),
							            		districtId: $('[name="districtId"]', $mainForm).val(),
							            		unitCategory: $('[name="unitCategory"]', $mainForm).val(),
							            		tpuId: $('[name="tpuId"]', $mainForm).val(),
							            		isProductChange: $('[name="isProductChange"]', $mainForm).val(),
							            		isSPricePeculiar: $('[name="isSPricePeculiar"]', $mainForm).val(),
							            		availability: $('[name="availability"]', $mainForm).val(),
							            		referenceMonth: $('.date-picker').val(),
							            		purposeId: $('[name="purposeId"]', $mainForm).val()
							            	},
											success: function(response) {
												$dataTable.DataTable().ajax.reload();
												$("#MessageRibbon").html(response);
											},
											complete: function() {
												Modals.endLoading();
											}
										});
										
									}
								}
							});
							
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
					$.fn.dataTable.addResponsiveButtons(buttons);
					

					
					$dataTable.DataTable({
						"ordering": true, 
						"order": tableOrder,
						"searching": true,
						"buttons": buttons,
						"processing": true,
			            "serverSide": true,
			            "search" : {
			            	"search" : $('#lastRequestModelSearch').val()
			            },
			            "ajax": {
			            	url: tableUrl,
			            	data: function(dataModel) {
			            		dataModel.outletId = $('[name="outletId"]', $mainForm).val();
			            		dataModel.outletTypeId = $('[name="outletTypeId"]', $mainForm).val();
			            		dataModel.personInChargeId = $('[name="personInChargeId"]', $mainForm).val();
			            		dataModel.districtId = $('[name="districtId"]', $mainForm).val();
			            		dataModel.unitCategory = $('[name="unitCategory"]', $mainForm).val();
			            		dataModel.tpuId = $('[name="tpuId"]', $mainForm).val();
			            		dataModel.isProductChange = $('[name="isProductChange"]', $mainForm).val();
			            		dataModel.isSPricePeculiar = $('[name="isSPricePeculiar"]', $mainForm).val();
			            		dataModel.availability = $('[name="availability"]', $mainForm).val();
			            		dataModel.referenceMonth = $('.date-picker').val();
			            		dataModel.purposeId = $('[name="purposeId"]', $mainForm).val();
			            		
			            		dataModel.search["outletId"] = $('[name="outletId"]', $mainForm).val();
			            		dataModel.search["outletTypeId"] = $('[name="outletTypeId"]', $mainForm).val();
			            		dataModel.search["personInChargeId"] = $('[name="personInChargeId"]', $mainForm).val();
			            		dataModel.search["districtId"] = $('[name="districtId"]', $mainForm).val() == null ? "" : $('[name="districtId"]', $mainForm).val().join();
			            		dataModel.search["unitCategory"] = $('[name="unitCategory"]', $mainForm).val();
			            		dataModel.search["tpuId"] = $('[name="tpuId"]', $mainForm).val() == null ? "" : $('[name="tpuId"]', $mainForm).val().join();
			            		dataModel.search["isProductChange"] = $('[name="isProductChange"]', $mainForm).val();
			            		dataModel.search["isSPricePeculiar"] = $('[name="isSPricePeculiar"]', $mainForm).val();
			            		dataModel.search["availability"] = $('[name="availability"]', $mainForm).val() == null ? "" : $('[name="availability"]', $mainForm).val().join();
			            		dataModel.search["referenceMonth"] = $('[name="referenceMonth"]', $mainForm).val();
			            		dataModel.search["purposeId"] = $('[name="purposeId"]', $mainForm).val() == null ? "" : $('[name="purposeId"]', $mainForm).val().join();
			            		
			            		lastQueryDataModel = dataModel;
			            		cacheLastSearch = dataModel;
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
			            			$.get('<c:url value="/assignmentManagement/AssignmentApproval/getOutletTypesDisplayByOutletId"/>?id=' + id, function(list) {
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
					
					$('#availability', $mainForm).select2();
					$('.select2ajax', $mainForm).select2ajax();
					//$('.date-picker', $mainForm).datepicker();
					
					Datepicker();
					$('.date-picker').datepicker().on('changeDate', function(){						
						$dataTable.DataTable().ajax.reload();						
					});
					
					$("#clearBtn").click(function(){
						$('#availability', $mainForm).select2('destroy');
						$('.select2ajax', $mainForm).select2ajax('destroy');
						$('.select2ajax', $mainForm).each(function(){
							$(this).empty();
						})
						$("#mainForm")[0].reset();
						$dataTable.DataTable().ajax.reload();	
						
						$('#availability', $mainForm).select2();
						$('.select2ajax', $mainForm).select2ajax();
						
					})
					
					tabPane.on('click', '.btn-edit', function(e) {
						var d = cacheLastSearch;
						
						$.post("<c:url value='/assignmentManagement/AssignmentApproval/saveLastRequestModel'/>", d, function(){
							
						});
						
						e.preventDefault();
						var quotationRecordId = $(this).data('id');

						var order = $dataTable.DataTable().order();
						lastQueryDataModel.order = [{
						 column: order[0][0],
						 dir: order[0][1]
						}];
						$.post('<c:url value="/assignmentManagement/AssignmentApproval/cacheQuotationRecordSearchFilterAndResult"/>',
							lastQueryDataModel
							, function(data) {
								location = '<c:url value="/assignmentManagement/AssignmentApproval/editQuotationRecord"/>?quotationRecordId=' + quotationRecordId;
							});
					});
				}
				
				initTabPane($('.box-primary'), '<c:url value="/assignmentManagement/AssignmentApproval/query"/>',
						[
						 	{ "data": "quotationRecordId",
	                        	"render": function(data, type, row) {
	                        		return "<input type='checkbox' name='id[]' value='"+data+"' class='tblChk'/>";
	                        	}
						 	},
						 	{ "data": "purpose" },
	                        { "data": "firm",
	                        	"render" : function(data, type, row) {
	                        		if (data == null) return '';
	                        		return data
	                        			+ '<button type="button" class="btn btn-xs btn-outlet-tooltip" data-id="' + row.outletId + '" data-trigger="click"><i class="fa fa-info"></i></button>';
	                        	}
	                        },
	                        { "data": "outletType" },
	                        { "data": "district" },
	                        { "data": "tpu" },
	                        { "data": "unitDisplayName" },
	                        { "data": "nPrice" },
	                        { "data": "sPrice" },
	                        { "data": "discount" },
	                        { "data": "availability",
	                        	"render": function(data, type, row) {
	                        		var availability = 0;
	                        		if (data==3){
	                        			availability = ((row.nPrice == null || row.nPrice == "") && (row.sPrice == null || row.sPrice == "") && !row.isSPricePeculiar) ? 4 : 1
	                        		} 
	                        		return availabilityDisplay((availability != 0) ? availability : data);
	                        	}
	                        },
	                        { "data": "reason" },
	                        { "data": "isProductChange" },
	                        { "data": "personInCharge" },
	                        { "data": "isSPricePeculiar" },
	                        { "data": "quotationState" },
	                        { "data": "priceRemark" },
	                        { "data": "outletCategoryRemark" },
	                        { "data": "address" },
	                        { "data": "unitCategory" },
	                        { "data": "quotationRecordId",
	                        	"render": function(data, type, row) {
	                        		return '<a class="btn-edit" href="#" data-id="' + data + '" class="table-btn"><span class="glyphicon glyphicon-pencil" aria-hidden="true"></span></a>';
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
          <h1>Assignment Approval</h1>
        </section>
        
        <section class="content">
        	<div class="row">
				<div class="col-md-12">
				<!-- content -->
        			<div class="box box-primary">
       					<div class="box-body">
							<form class="form-horizontal" id="mainForm">
								<input id="lastRequestModelSearch" value="<c:out value="${viewModel.search}"/>" type="hidden" />
								<input id="lastRequestModelOrderColumn" value="<c:out value="${viewModel.orderColumn}"/>" type="hidden" />
								<input id="lastRequestModelOrderDir" value="<c:out value="${viewModel.orderDir}"/>" type="hidden" />
								<div class="row">
									<label class="col-md-2 control-label">Firm</label>
									<div class="col-md-2">
										<select name="outletId" class="form-control select2ajax filters"
											data-allow-clear="true"
											data-placeholder=""
											data-ajax-url="<c:url value='/assignmentManagement/AssignmentApproval/queryOutletSelect2'/>">
											<c:if test="${viewModel.outletSelected != null}">
												<option value="<c:out value="${viewModel.outletSelected.value}"/>" selected>
													<c:out value="${viewModel.outletSelected.key}"/>
												</option>
											</c:if>
										</select>
									</div>
									<label class="col-md-2 control-label">Outlet Type</label>
									<div class="col-md-2">
										<select name="outletTypeId" class="form-control select2ajax filters"
											data-allow-clear="true"
											data-ajax-url="<c:url value='/assignmentManagement/AssignmentApproval/queryOutletTypeSelect2'/>">
											<c:if test="${viewModel.outletTypeSelected != null}">
												<option value="<c:out value="${viewModel.outletTypeSelected.value}"/>" selected>
													<c:out value="${viewModel.outletTypeSelected.key}"/>
												</option>
											</c:if>
										</select>
									</div>
									<label class="col-md-2 control-label">Person in charge</label>
									<div class="col-md-2">
										<select name="personInChargeId" class="form-control select2ajax filters"
											data-allow-clear="true"
											data-placeholder=""
											data-ajax-url="<c:url value='/assignmentManagement/AssignmentApproval/queryOfficerSelect2'/>">
											<c:if test="${viewModel.personInChargeSelected != null}">
												<option value="<c:out value="${viewModel.personInChargeSelected.value}"/>" selected>
													<c:out value="${viewModel.personInChargeSelected.key}"/>
												</option>
											</c:if>	
										</select>
									</div>
								</div>
								<div class="row" style="margin-top: 10px;">
									<label class="col-md-2 control-label">District</label>
									<div class="col-md-2">
										<select name="districtId" class="form-control select2ajax filters"
											data-allow-clear="true"
											data-placeholder=""
											data-ajax-url="<c:url value='/assignmentManagement/AssignmentApproval/queryDistrictSelect2'/>"
											data-close-on-select="false"
											multiple>
											<c:forEach items="${viewModel.districtSelected}" var="districtSelected">
											    <option value="<c:out value="${districtSelected.value}"/>" selected><c:out value="${districtSelected.key}"/></option>
											</c:forEach>	
										</select>
									</div>
									<label class="col-md-2 control-label">Unit Category</label>
									<div class="col-md-2">
										<select name="unitCategory" class="form-control select2ajax filters"
											data-allow-clear="true"
											data-placeholder=""
											data-ajax-url="<c:url value='/assignmentManagement/AssignmentApproval/queryUnitCategorySelect2'/>">
											<c:if test="${viewModel.unitCategory != null}">
												<option value="<c:out value="${viewModel.unitCategory}"/>" selected>
													<c:out value="${viewModel.unitCategory}"/>
												</option>
											</c:if>
										</select>
									</div>
									<label class="col-md-2 control-label">TPU</label>
									<div class="col-md-2">
										<select name="tpuId" class="form-control select2ajax filters"
											data-allow-clear="true"
											data-placeholder=""
											data-ajax-url="<c:url value='/assignmentManagement/AssignmentApproval/queryTpuSelect2'/>"
											data-close-on-select="false"
											multiple>
											<c:forEach items="${viewModel.tpuSelected}" var="tpuSelected">
											    <option value="<c:out value="${tpuSelected.value}"/>" selected><c:out value="${tpuSelected.key}"/></option>
											</c:forEach>	
										</select>
									</div>
								</div>
								<div class="row" style="margin-top: 10px;">
									<label class="col-md-2 control-label">Change Product</label>
									<div class="col-md-1">
										<select name="isProductChange" class="form-control select2 filters"
											data-allow-clear="true"
											data-placeholder="">
											<option></option>
											<option value="true" ${viewModel.isProductChange != null && viewModel.isProductChange ? "selected" : "" }>Y</option>
											<option value="false" ${viewModel.isProductChange != null && !viewModel.isProductChange ? "selected" : "" }>N</option>
										</select>
									</div>
									<label class="col-md-3 control-label">Not Applicable</label>
									<div class="col-md-1">
										<select name="isSPricePeculiar" class="form-control select2 filters"
											data-allow-clear="true"
											data-placeholder="">
											<option></option>
											<option value="true" ${viewModel.isSPricePeculiar != null && viewModel.isSPricePeculiar ? "selected" : "" }>Y</option>
											<option value="false" ${viewModel.isSPricePeculiar != null && !viewModel.isSPricePeculiar ? "selected" : "" }>N</option>
										</select>
									</div>
									<label class="col-md-3 control-label">Availability</label>
									<div class="col-md-2">
										<select id="availability" name="availability" class="form-control select2 filters"
											data-allow-clear="true"
											data-placeholder=""
											data-close-on-select="false"
											multiple>
											<option value="1" ${viewModel.availability != null && viewModel.availability[1] ? "selected" : "" }>Available</option>
											<option value="2" ${viewModel.availability != null && viewModel.availability[2] ? "selected" : "" }>IP</option>
											<option value="3" ${viewModel.availability != null && viewModel.availability[3] ? "selected" : "" }>有價無貨</option>
											<option value="4" ${viewModel.availability != null && viewModel.availability[4] ? "selected" : "" }>缺貨</option>
											<option value="5" ${viewModel.availability != null && viewModel.availability[5] ? "selected" : "" }>Not Suitable</option>
											<option value="6" ${viewModel.availability != null && viewModel.availability[6] ? "selected" : "" }>回倉</option>
											<option value="7" ${viewModel.availability != null && viewModel.availability[7] ? "selected" : "" }>無團</option>
											<option value="8" ${viewModel.availability != null && viewModel.availability[8] ? "selected" : "" }>未返</option>
											<option value="9" ${viewModel.availability != null && viewModel.availability[9] ? "selected" : "" }>New</option>
										</select>
									</div>
								</div>
								<div class="row" style="margin-top: 10px;">
									<label class="col-md-2 control-label">Reference Month</label>
									<div class="col-md-2">
										<div class="input-group">
											<input type="text" name="referenceMonth" class="form-control date-picker" 
												data-date-minviewmode="months" data-date-format="mm-yyyy" 
												value="${viewModel.referenceMonth}"/>
											<div class="input-group-addon">
												<i class="fa fa-calendar"></i>
											</div>
										</div>
									</div>
									<label class="col-md-2 control-label">Purpose</label>
									<div class="col-md-2">
											<select name="purposeId" class="form-control select2ajax filters"
											data-allow-clear="true"
											data-placeholder=""
											data-ajax-url="<c:url value='/assignmentManagement/AssignmentApproval/queryPurposeSelect2'/>"
											data-close-on-select="false"
											multiple>
												<c:forEach items="${viewModel.purposeSelected}" var="purposeSelected">
												    <option value="<c:out value="${purposeSelected.value}"/>" selected><c:out value="${purposeSelected.key}"/></option>
												</c:forEach>	
											</select>
									</div>
								</div>

								<div class="row" style="margin-top: 10px;">
									<div class="col-md-3 col-md-offset-1">
										<button type="button" class="btn btn-info" id="clearBtn" >Clear</button>
									</div>
								</div>
							</form>
							<hr/>
							<form id="frmSubmit" method="post" action="<c:url value='/assignmentManagement/AssignmentApproval/approveRejectQuotationRecords'/>">
								<input name="approveRejectBtn" type="hidden"/>
								<input name="rejectReason" type="hidden"/>
							<table class="table table-striped table-bordered table-hover data-table responsive">
								<thead>
								<tr>
									<th class="text-center action" ><input class="select_all" type="checkbox" /></th>
									<th>Purpose</th>
									<th>Firm</th>
									<th>Outlet Type</th>
									<th>District</th>
									<th>TPU</th>
									<th>Unit Display Name</th>
									<th>N Price</th>
									<th>S Price</th>
									<th>Discount</th>
									<th>Availability</th>
									<th>Reason</th>
									<th>Change Product</th>
									<th class="person-in-charge">Field Officer</th>
									<th>Not Applicable</th>
									<th>Quotation State</th>
									<th>Price Remark</th>
									<th>Outlet Category Remark</th>
									<th>Address</th>
									<th>Unit Category</th>
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

