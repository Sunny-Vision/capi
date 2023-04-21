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
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/jstree-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/unitLookup-css.jsp" %>
	</jsp:attribute>
	<jsp:attribute name="header">
		<style>
			.input-group-addon:last-child {
				border-radius: 4px;
				border-left: 1px solid #d2d6de;
				width: 0%;
			}
		</style>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/jstree.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/unitLookup.jsp" %>
		<script>
			$(document).ready(function(){
				var $dataTable = $("#dataList");
				
				$('.select2ajax').select2ajax({
					width: "100%"
				});
				
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
				$.fn.dataTable.addResponsiveButtons(buttons);
				
				$dataTable.DataTable({
					"ordering": true, 
					"order": [[ 0, "desc" ]],
					"searching": true,
					"buttons": buttons,
					"processing": true,
		            "serverSide": true,
		            "ajax": {
		            	url: "<c:url value='/assignmentManagement/QuotationRecordMaintenance/query'/>",
		            	data: function(dataModel) {
		            		dataModel.outletId = $('[name="outletId"]').val();
		            		var outletTypeIds = $('[name="outletTypeId"]').val();
		            		if (outletTypeIds != null) {
		            			for (var i = 0; i < outletTypeIds.length; i++) {
		            				outletTypeIds[i] = outletTypeIds[i];
		            			}
		            			dataModel.outletTypeId = outletTypeIds;
		            		}
		            		dataModel.unitId = $('[name="unitId"]').val();
		            		dataModel.districtId = $('[name="districtId"]').val();
		            		dataModel.tpuId = $('[name="tpuId"]').val();
		            		dataModel.status = $('[name="status"]').val();
		            		dataModel.userId = $('[name="userIds"]').val();
		            	},
		            	method: 'post'
		            },
		            "columns": [
		                        { "data": "id" },
		                        { "data": "quotationId" },
		                        { "data": "officer" },
		                        { "data": "deadline" },
		                        { "data": "unitCode" },
		                        { "data": "unitName" },
		                        { "data": "firmName" },
		                        { "data": "outletType" },
		                        { "data": "districtCode" },
		                        { "data": "tpu" },
		                        { "data": "productAttribute1" },
		                        { "data": "productAttribute2" },
		                        { "data": "productAttribute3" },
		                        { "data": "status" },
		                        { "data": "nPrice" },
		                        { "data": "sPrice" },
		                        { "data": "discount" },
		                        { "data": "subPrice",
		                        	"render": function(data) {
		                        		return data ? 'Y' : 'N';
		                        	}
		                        },
		                        { "data": "remark" },
		                        { "data": "mapAddress" },
		                        { "data": "detailAddress" },
		                        { "data": "isBackTrack",
		                        	"render": function(data) {
		                        		return data ? 'Y' : 'N';
		                        	}
		                        },
		                        { "data": "productAttribute4" },
		                        { "data": "productAttribute5" },
		                        { "data": "firmId" },
		                        { "data": "id",
		                        	"render": function(data) {
		                        		return "<a href='<c:url value='/assignmentManagement/QuotationRecordMaintenance/edit?id='/>"+data+"' class='table-btn'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a>";
		                        	}
		                        }
								],
		            "columnDefs": columnDefs
				});
				
				$('.lookup').each(function() {
					var bottomEntityClass = $(this).data('bottomEntityClass');
					var $select = $(this).closest('.input-group').find('select');
					var getKeyValueByIdsUrl = $select.data('getKeyValueByIdsUrl');
					$(this).unitLookup({
						selectedIdsCallback: function(selectedIds) {
							$select.empty();
							
							if(selectedIds.length == 0) {
								$(".input-group-addon:last-child").css("color", "#555");
								$(".input-group-addon:last-child").css("background-color", "");
								$(".input-group-addon:last-child").css("border-color", "#d2d6de");
								$dataTable.DataTable().ajax.reload();
								return;
							}
							/*$.post(getKeyValueByIdsUrl, { id: selectedIds }, function(data) {
								for (var i = 0; i < data.length; i++) {
									var option = new Option(data[i].value, data[i].key);
									option.selected = true;
									$select.append(option);
								}
								$select.trigger('change');
							});*/
							for (var i = 0; i < selectedIds.length; i++) {
								var option = new Option(selectedIds[i], selectedIds[i]);
								option.selected = true;
								$select.append(option);
							}
							$select.trigger('change');
							
							$(".input-group-addon:last-child").css("color", "#ffffff");
							$(".input-group-addon:last-child").css("background-color", "#f0ad4e");
							$(".input-group-addon:last-child").css("border-color", "#eea236");
						},
						multiple: true,
						bottomEntityClass: bottomEntityClass
					});
					$select.hide();
				});
				
				$('[name="userIds"]').select2ajax({
					placeholder: "",
					allowClear: true,
					closeOnSelect: false
				});
				
				$('.filters').change(function(){
					$dataTable.DataTable().ajax.reload();
				});
				
				$('button[type="button"]').on('click', function() {
					console.log('Reset Button clicked.');
					
					$('.criteria').select2ajax('destroy');
					$('[name="userIds"]').select2ajax('destroy');
					
					$('.criteria').find("option").remove();
					$('[name="unitId"]').find("option").remove();
					$('[name="userIds"]').find("option").remove();
					$('[name="status"]').val("");
					
					$('.criteria').select2ajax({
						width: "100%"
					});
					$('[name="userIds"]').select2ajax({
						placeholder: "",
						allowClear: true,
						closeOnSelect: false
					});
					
					var $jstree = $('div.jstree:hidden');
					$jstree.jstree('deselect_all');
					$jstree.jstree('close_all');
					$(".input-group-addon:last-child").css("color", "#555");
					$(".input-group-addon:last-child").css("background-color", "");
					$(".input-group-addon:last-child").css("border-color", "#d2d6de");
					
					$dataTable.DataTable().ajax.reload();
				});
				
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Quotation Record Maintenance</h1>
        </section>
        
        <section class="content">
        	<div class="row">
				<div class="col-md-12">
				<!-- content -->
					<!-- Itinerary Planning Table -->
					<div class="box" >
        				<div class="box-header with-border">
							<a class="btn btn-default" href="<c:url value='/assignmentManagement/QuotationRecordMaintenance/home'/>">Back</a>
						</div>
						<div class="box-body">
							<form class="form-horizontal">
								<div class="row">
									<label class="col-md-1 control-label">Outlet</label>
									<div class="col-md-3">
										<select name="outletId" class="form-control select2ajax filters criteria"
											data-allow-clear="true"
											data-placeholder=""
											data-ajax-url="<c:url value='/assignmentManagement/QuotationRecordMaintenance/queryOutletSelect2'/>"></select>
									</div>
									<label class="col-md-1 control-label">Outlet Type</label>
									<div class="col-md-3">
										<select name="outletTypeId" class="form-control select2ajax filters criteria"
											data-close-on-select="false"
											data-ajax-url="<c:url value='/assignmentManagement/QuotationRecordMaintenance/queryOutletTypeSelect2'/>" multiple></select>
									</div>
									<label class="col-md-1 control-label">Variety</label>
									<div class="col-md-1">
										<div class="input-group">
											<select name="unitId" class="form-control filters"
												data-allow-clear="true"
												data-placeholder=""
												data-ajax-url="<c:url value='/assignmentManagement/QuotationRecordMaintenance/queryUnitSelect2'/>"
												data-get-key-value-by-ids-url="<c:url value='/assignmentManagement/QuotationRecordMaintenance/getKeyValueByIds'/>"
												data-close-on-select="false"
												multiple></select>
											<div class="input-group-addon lookup"
												data-bottom-entity-class="Unit">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								<div class="row" style="margin-top: 10px;">
									<label class="col-md-1 control-label">District</label>
									<div class="col-md-3">
										<select name="districtId" class="form-control select2ajax filters criteria"
											data-allow-clear="true"
											data-placeholder=""
											data-ajax-url="<c:url value='/assignmentManagement/QuotationRecordMaintenance/queryDistrictSelect2'/>"
											data-close-on-select="false"
											multiple></select>
									</div>
									<label class="col-md-1 control-label">TPU</label>
									<div class="col-md-3">
										<select name="tpuId" class="form-control select2ajax filters criteria"
											data-allow-clear="true"
											data-placeholder=""
											data-ajax-url="<c:url value='/assignmentManagement/QuotationRecordMaintenance/queryTpuSelect2'/>"
											data-close-on-select="false"
											multiple></select>
									</div>
									<label class="col-md-1 control-label">Status</label>
									<div class="col-md-3">
										<select name="status" class="form-control filters">
											<option></option>
											<option>Blank</option>
											<option>Draft</option>
											<option>Submitted</option>
											<option>Rejected</option>
											<option>Approved</option>
										</select>
									</div>
								</div>
								<div class="row" style="margin-top: 10px;">
									<label class="col-md-1 control-label">Field Officer</label>
									<div class="col-md-6">
										<select name="userIds" class="form-control filters" multiple
											data-ajax-url="<c:url value='/assignmentManagement/QuotationRecordMaintenance/queryOfficerSelect2'/>"></select>
									</div>
								</div>
								<div class="row" style="margin-top: 10px;">
									<div class="col-md-3 col-md-offset-1">
										<button type="button" class="btn btn-info">Clear</button>
									</div>
								</div>
							</form>
							<hr/>
							<table class="table table-striped table-bordered table-hover responsive" id="dataList">
								<thead>
								<tr>
									<th>Quotation Record ID</th>
									<th>Quotation ID</th>
									<th>Field Officer</th>
									<th>Date of data collection / Deadline</th>
									<th>Variety Code</th>
									<th>Variety Name</th>
									<th>Firm Name</th>
									<th>Outlet Type</th>
									<th>District</th>
									<th>TPU</th>
									<th>Product Attritube 1</th>
									<th>Product Attritube 2</th>
									<th>Product Attritube 3</th>
									<th>Status</th>
									<th>N Price</th>
									<th>S Price</th>
									<th>Discount</th>
									<th>Sub-price</th>
									<th>Remarks</th>
									<th>Map Address</th>
									<th>Detail Address</th>
									<th>Is Backtrack</th>
									<th>Product Attritube 4</th>
									<th>Product Attritube 5</th>
									<th>Firm ID</th>
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

