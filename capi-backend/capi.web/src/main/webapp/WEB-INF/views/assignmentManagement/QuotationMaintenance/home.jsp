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
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/jstree.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/unitLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/productLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/outletLookup.jsp" %>
		<style>
		#dataList .discount span {
			display: inline-block;
			margin: 0 2px;
		}
		#dataList .discount span.number {
			border: solid 1px #d2d6de;
			padding: 0 20px;
		}
		.btn-group {
			margin: 2px 0 2px 0;
		}
		#unitLookup.input-group-addon:last-child {
			border-radius: 4px;
			border-left: 1px solid #d2d6de;
			width: 0%;
		}
		</style>
		<script>
		
			function deleteRecordsWithConfirm(data) {
				bootbox.confirm({
				     title:"Confirmation",
				     message: "<spring:message code='W00001' />",
				     callback: function(result){
				         if (result){
							$.post("<c:url value='/assignmentManagement/QuotationMaintenance/delete'/>",
								data,
								function(response) {
									$("#dataList").DataTable().ajax.reload();
									$("#MessageRibbon").html(response);
								}
							);
				         }
				     }
				})
			}
			
			function ruaWithConfirm(data) {
				bootbox.confirm({
				     title:"Confirmation",
				     message: "<spring:message code='W00003' />",
				     callback: function(result){
				         if (result){
							$.post("<c:url value='/assignmentManagement/QuotationMaintenance/toRUA'/>",
								data,
								function(response) {
									$("#dataList").DataTable().ajax.reload();
									$("#MessageRibbon").html(response);
								}
							);
				         }
				     }
				})
			}
			
			$(document).ready(function(){
				var $dataTable = $("#dataList");

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
				
				var dataTableURL = '';
				<c:choose>
					<c:when test="${isRUAMode}">
					dataTableURL = '<c:url value='/assignmentManagement/QuotationMaintenance/queryRUA'/>';
					</c:when>
					<c:otherwise>
					dataTableURL = '<c:url value='/assignmentManagement/QuotationMaintenance/query'/>';
					</c:otherwise>
				</c:choose>
				
				var table = $dataTable.DataTable({
					"order": [[ 1, "desc" ]],
					"searching": true,
					"ordering": true,
					<sec:authorize access="!hasPermission(#user, 256)">
					"buttons": [],
					</sec:authorize>
					<sec:authorize access="hasPermission(#user, 256)">
					"buttons": [
								{
									"text": "Export FR(Admin)",
									"action": function ( nButton, oConfig, flash ) {
										window.location.assign("<c:url value='/dataImportExport/DataExport/home?dataType=20'/>");
									}
								},
								{
									"text": "Import FR(Admin)",
									"action": function ( nButton, oConfig, flash ) {
										window.location.assign("<c:url value='/dataImportExport/DataImport/home?dataType=20'/>");
									}
								},
								{
									"text": "Export Indoor Allocation Code",
									"action": function ( nButton, oConfig, flash ) {
										window.location.assign("<c:url value='/dataImportExport/DataExport/home?dataType=19'/>");
									}
								},
								{
									"text": "Import Indoor Allocation Code",
									"action": function ( nButton, oConfig, flash ) {
										window.location.assign("<c:url value='/dataImportExport/DataImport/home?dataType=19'/>");
									}
								},

					],
					</sec:authorize>
					"processing": true,
	                "serverSide": true,
	                "ajax": {
	                	url: dataTableURL,
	                	data: function(d) {
	                		d.search["purposeId"] = $('[name="purposeId"]').val();
	                		//d.search["unitId"] = $('[name="unitId"]').val();
	                		d.search["unitId"] = $('[name="unitId"]').val() == null ? "" : $('[name="unitId"]').val().join();
	                		d.search["productId"] = $('[name="productId"]').val();
	                		d.search["outletId"] = $('[name="outletId"]').val();
	                		d.search["batchId"] = $('[name="batchId"]').val();
	                		d.search["pricingFrequencyId"] = $('[name="pricingFrequencyId"]').val();
	                		d.search["status"] = $('[name="status"]').val();
	                		d.search["isICP"] = $('[name="isICP"]').val();
	                		d.search["indoorAllocationCode"] = $('[name="indoorAllocationCode"]').val();
	                	},
	                	method: 'post'
	                },
	                "columns": [
	                            { "data": "id",
                                   "render" : function(data, type, full, meta){
                                       return "<input type='checkbox' name='id' value='"+data+"' class='tblChk'/>";
                                   },
                            	},
                            	{ "data": "id" },
	                            { "data": "purpose" },
	                            { "data": "cpiBasePeriod" },
	                            { "data": "unitCode" },
	                            { "data": "englishName" ,
	                              	"render" : function(data, type, full, meta){
	                            		//return "<span title="+data+">"+full.englishName+"</span>";
	                            		
	                            		if(data == null) {
	                            			return "<span title='"+full.unitName+"'></span>";
	                            		} else {
	                            			return "<span title='"+full.unitName+"'>"+data+"</span>";
	                            		}
                            		}
	                            },
	                            { "data": "productAttribute1" },
	                            { "data": "productAttribute2" },
	                            { "data": "productAttribute3" },
	                            { "data": "status" },
	                            { "data": "firmName" },
	                            { "data": "batchCode" },	                            
	                            /*{ "data": "cpiQuotationType" ,
	                              	"render" : function(data, type, full, meta){
	                            		switch (data){
		                            		case 1: return 'Market';
		                            		case 2: return 'Supermarket';
		                            		case 3: return 'Batch';
		                            		default: return 'Others';
	                            		}
                            		}
	                            },*/
	                            { "data": "cpiQuotationType" },
	                            { "data": "formType" },
	                            { "data": "pricingFrequency" },
	                            { "data": "frAdmin" },
	                            { "data": "frField" },
	                            { "data": "lastFRAppliedDate"},
	                            { "data": "seasonalWithdrawal"},
	                            { "data": "seasonality",
	                              	"render" : function(data, type, full, meta){
	                            		switch (data) {
		                            		case 1:
		                            			return "All-time";
		                            		case 2:
		                            			return "Summer";
		                            		case 3:	
		                            			return "Winter";
		                            		case 4:
		                            			return "Occasional";
		                            		default:
		                            			return "";
	                            		}
                            		}
	                            },
	                            { "data": "ruaDate" },
	                            { "data": "icp",
	                              	"render" : function(data, type, full, meta){
	                            		switch (data) {
		                            		case true:
		                            			return "Y";
		                            		default:
		                            			return "N";
	                            		}
                            		}
	                            },
	                            { "data": "outletId" },
	                            { "data": "productId" },
	                            { "data": "productAttribute4" },
	                            { "data": "productAttribute5" },  
	                            { "data": "indoorAllocationCode" },
	                            {
	                            	"data": "id",
	                            	"render" : function(data, type, full, meta){
	                            		var html = "<a href='<c:url value='/assignmentManagement/QuotationMaintenance/edit?id='/>"+data+"' class='table-btn'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a>";
	                            		return html;
                            		}
                            	}
	                        ],
	                "columnDefs": columnDefs,
	                "drawCallback": function() {
	    				$(".tblChk").click(function (evt){
	    					evt.stopPropagation();
	    				})
	    				
		            }
				});
				
				var buttons = [];
				<sec:authorize access="hasPermission(#user, 256)">
				var buttons = [
								{
									"text": "Add",
									"action": function( nButton, oConfig, flash ) {
										window.location = "<c:url value='/assignmentManagement/QuotationMaintenance/edit'/>";
									}
								},
								{
									"text": "Delete",
									"action": function( nButton, oConfig, flash ) {
										var data = $dataTable.find(':checked').serialize();
										if (data == '') {
											bootbox.alert({
											    title: "Alert",
											    message: "<spring:message code='E00009' />"
											});
											return;
										}
										
										deleteRecordsWithConfirm(data);
									}
								},
								{
									"text": "Change to RUA",
									"action": function( nButton, oConfig, flash ) {
										var data = $dataTable.find(':checked').serialize();
										if (data == '') {
											bootbox.alert({
											    title: "Alert",
											    message: "<spring:message code='E00009' />"
											});
											return;
										}
										ruaWithConfirm(data);
									}
								},
			        ];
				</sec:authorize>
				
				$.fn.dataTable.addResponsiveButtons(buttons);
			   new $.fn.dataTable.Buttons( table, {
			        buttons: buttons
			    } );
			 
			    table.buttons( 1, null ).container().prependTo(
			        table.table().container()
			    );
				
				$('.filters').change(function(){
					$dataTable.DataTable().ajax.reload();
				});
				
				$('.select2ajax').select2ajax({
					allowClear: true,
					placeholder: '',
					width:"100%"
				});
				
				$('.select2ajax').hide();
				
				$('#unitId,.searchUnitId').unitLookup({
					/*selectedIdsCallback: function(selectedIds) {
						var singleUrl = $('#unitId').data('singleUrl');
						$('#unitId').empty();
						$.get(singleUrl, { id: selectedIds[0] }, 
							function(data) {
							var option = new Option(data, selectedIds[0]);
							option.selected = true;
							$('#unitId').append(option);
							$('#unitId').trigger('change');
						});
					},*/
					selectedIdsCallback: function(selectedIds) {
						var singleUrl = $('#unitId').data('singleUrl');
						var $select = $('#unitId');
						$('#unitId').empty();
						
						if(selectedIds.length == 0) {
							$("#unitLookup.input-group-addon:last-child").css("color", "#555");
							$("#unitLookup.input-group-addon:last-child").css("background-color", "");
							$("#unitLookup.input-group-addon:last-child").css("border-color", "#d2d6de");
							$dataTable.DataTable().ajax.reload();
							return;
						}
						
						for (var i = 0; i < selectedIds.length; i++) {
							var option = new Option(selectedIds[i], selectedIds[i]);
							option.selected = true;
							$select.append(option);
						}
						$select.trigger('change');
						$("#unitLookup.input-group-addon:last-child").css("color", "#ffffff");
						$("#unitLookup.input-group-addon:last-child").css("background-color", "#f0ad4e");
						$("#unitLookup.input-group-addon:last-child").css("border-color", "#eea236");
					},
					multiple: true
				});
				$('#unitId').hide();
				
				$('#outletId,.searchOutletId').outletLookup({
					selectedIdsCallback: function(selectedIds) {
						var singleUrl = $('#outletId').data('singleUrl');
						$('#outletId').empty();
						$.post(singleUrl, { id: selectedIds[0] }, 
							function(data) {
							var option = new Option(data, selectedIds[0]);
							option.selected = true;
							$('#outletId').append(option);
							$('#outletId').trigger('change');
						});
					},
					multiple: false
				});
				
				$('.searchProductId').productLookup({
					selectedIdsCallback: function(selectedIds) {
						var singleUrl = $('#productId').data('singleUrl');
						$('#productId').empty();
						var option = new Option(selectedIds[0], selectedIds[0]);
						option.selected = true;
						$('#productId').append(option);
						$('#productId').trigger('change');
					},
					multiple: false
				});
				
				$(".select_all").change(function() {
					var checkboxes = $(':checkbox.tblChk');
					if($(this).is(':checked')) {
						checkboxes.prop('checked', true);
					} else {
						checkboxes.prop('checked', false);
					}
				});
				
				$('#btnReset').on('click', function() {
					console.log('Reset Button clicked.');
					
					$('.criteria').select2ajax('destroy');
					
					$('.criteria').find("option").remove();
					$('[name="unitId"]').find("option").remove();
					$('[name="status"]').val("");
					$('[name="isICP"]').val("");
					
					$('.criteria').select2ajax({
						width: "100%"
					});
					
					var $jstree = $('div.jstree:hidden');
					$jstree.jstree('deselect_all');
					$jstree.jstree('close_all');
					$("#unitLookup.input-group-addon:last-child").css("color", "#555");
					$("#unitLookup.input-group-addon:last-child").css("background-color", "");
					$("#unitLookup.input-group-addon:last-child").css("border-color", "#d2d6de");
					
					$dataTable.DataTable().ajax.reload();
				});
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Quotation Maintenance</h1>
        </section>
        
        <section class="content">
        	<div class="row">
				<div class="col-md-12">
				<!-- content -->
					<!-- Quotation Table -->
					<div class="box" >
						<div class="box-body">
							<form class="form-horizontal">
								<div class="row">
									<label class="col-md-1 control-label">Purpose</label>
									<div class="col-md-2">
										<select name="purposeId" class="form-control select2ajax filters criteria" id="purposeId"
											data-ajax-url="<c:url value='/assignmentManagement/QuotationMaintenance/queryPurposeSelect2'/>"></select>								
									</div>
									<label class="col-md-1 control-label">Variety</label>
									<div class="col-md-2">
										<div class="col-md-0">
		   									<div class="input-group">
		   										<select name="unitId" class="form-control filters" id="unitId" multiple
												data-ajax-url="<c:url value='/assignmentManagement/QuotationMaintenance/queryUnitSelect2'/>"
												data-single-url="<c:url value='/assignmentManagement/QuotationMaintenance/queryUnitSelectSingle'/>"></select>	
												<div id="unitLookup" class="input-group-addon searchUnitId">
													<i class="fa fa-search"></i>
												</div>
											</div>
										</div>
									</div>
									<label class="col-md-1 control-label">Product</label>
									<div class="col-md-2">
											<div class="input-group">
												<select name="productId" class="form-control select2ajax filters criteria" id="productId"
													data-ajax-url="<c:url value='/assignmentManagement/QuotationMaintenance/queryProductSelect2'/>"
													data-single-url="<c:url value='/assignmentManagement/QuotationMaintenance/queryProductSelectSingle'/>"></select>	
												<div class="input-group-addon searchProductId">
													<i class="fa fa-search"></i>
												</div>
											</div>							
									</div>
									<label class="col-md-1 control-label">Firm</label>
									<div class="col-md-2">
										<div class="input-group">
											<select name="outletId" class="form-control select2ajax filters criteria" id="outletId"
												data-ajax-url="<c:url value='/assignmentManagement/QuotationMaintenance/queryOutletSelect2'/>"
												data-single-url="<c:url value='/assignmentManagement/QuotationMaintenance/queryOutletSelectSingle'/>"></select>	
											<div class="input-group-addon searchOutletId">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
									</div>
								<div class="row" style="margin-top: 10px;">
									<label class="col-md-1 control-label">Batch</label>
									<div class="col-md-2">
										<select name="batchId" class="form-control select2ajax filters criteria" id="batchId"
											data-ajax-url="<c:url value='/assignmentManagement/QuotationMaintenance/queryBatchSelect2'/>"></select>
									</div>
									<label class="col-md-1 control-label">Pricing Frequency</label>
									<div class="col-md-2">
										<select name="pricingFrequencyId" class="form-control select2ajax filters criteria"
											data-ajax-url="<c:url value='/assignmentManagement/QuotationMaintenance/queryPricingFrequencySelect2'/>"></select>
									</div>
									<c:if test="${not isRUAMode}">
									<label class="col-md-1 control-label">Status</label>
									<div class="col-md-2">
										<select name="status" class="form-control filters">
											<option></option>
											<option ${selectRUA ? "selected" : ""}>RUA</option>
											<option>Active</option>
											<option>Inactive</option>
										</select>
									</div>
									</c:if>
									<label class="col-md-1 control-label">ICP</label>
									<div class="col-md-2">
										<select name="isICP" class="form-control filters">
											<option></option>
											<option>Y</option>
											<option>N</option>
										</select>
									</div>
								</div>
								<div class="row" style="margin-top: 10px;">
									<label class="col-md-1 control-label">Indoor Allocation Code</label>
									<div class="col-md-2">
										<select name="indoorAllocationCode" class="form-control select2ajax filters criteria"
											data-ajax-url="<c:url value='/assignmentManagement/QuotationMaintenance/queryIndoorAllocationCodeSelect2'/>"></select>
									</div>
									<label class="col-md-1 control-label"></label>
									<div class="col-md-2">
										<button id="btnReset" type="button" class="btn btn-info">Clear</button>
									</div>
								</div>
							</form>
							<hr/>
							<table class="table table-striped table-bordered table-hover responsive" id="dataList">
								<thead>
								<tr>
									<th class="text-center action" ><input class="select_all" type="checkbox" /></th>
									
									<th>Quotation ID</th>
									<th>Purpose</th>
									<th>CPI Base Period</th>
									<th>Variety code</th>
									<th>Variety Name</th>
									
									<th>Product Attribute 1</th>
									
									<th>Product Attribute 2</th>
									<th>Product Attribute 3</th>
									
									<th>status</th>
									<th>Firm Name</th>
									<th>Batch code</th>
									
									<th>CPI Quotation Type</th>
									<th>CPI Form Type</th>
									<th>Pricing frequency</th>
									<th>FR (admin)</th>
									<th>FR (field)</th>
									<th>Last FR applied</th>
									<th>Seasonal withdrawal</th>
									<th>Seasonality</th>
									<th>RUA Month</th>
									
									<th>ICP (Y/N)</th>
									<th>Firm ID</th>
									<th>Product ID</th>
									
									<th>Product Attribute 4</th>
									<th>Product Attribute 5</th>
									
									
									<th>Indoor allocation code</th>
									
									
									
									<!-- 
									<th>Purpose</th>
									<th>Variety code</th>
									<th>Variety Name</th>
									<th>Product ID</th>
									<th>Product Attribute 1</th>
									<th>Firm ID</th>
									<th>Firm Name</th>
									<th>Batch code</th>
									<th>Pricing frequency</th>
									<th>status</th>
									<th>ICP</th>
									<th>Indoor allocation code</th>
									<th>CPI Form Type</th>
									<th>Seasonal withdrawal</th>
									<th>FR (admin)</th>
									<th>FR (field)</th>
									<th>Last FR applied</th>
									<th>Seasonality</th>
									<th>RUA Date</th>
									 -->
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

