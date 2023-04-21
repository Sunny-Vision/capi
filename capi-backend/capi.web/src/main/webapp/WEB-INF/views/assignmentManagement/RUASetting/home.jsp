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
		
		<%@include file="/WEB-INF/views/includes/jstree-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/unitLookup-css.jsp" %>
	</jsp:attribute>
	<jsp:attribute name="header">
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		
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
			function deleteRecordsWithConfirm(data) {
				bootbox.confirm({
					title:"Confirmation",
					message: "<spring:message code='W00001' />",
					callback: function(result){
						if (result){
							$.post("<c:url value='/assignmentManagement/RUASetting/delete'/>",
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
				/*$("#chkAll").on('change', function(){
		    		if (this.checked){
		    			$('.tblChk').prop('checked', true);
		    		}
		    		else{
		    			$('.tblChk').prop('checked', false);
		    		}
		    	});*/
				
				var $dataTable = $("#dataList");				
				
				$dataTable.DataTable({
					"order": [[ 0, "desc" ]],
					"searching": true,
					"ordering": true,
					<sec:authorize access="!(hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256))">
					"buttons": [],
					</sec:authorize>
					<sec:authorize access="(hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256))">
					"buttons": [
						/*{
							"text": "Add",
							"action": function( nButton, oConfig, flash ) {
								window.location = "<c:url value='/assignmentManagement/RUASetting/edit?act=add'/>";
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
						}*/
					],
					</sec:authorize>
					"processing": true,
	                "serverSide": true,
	                "ajax": {
	                	url: "<c:url value='/assignmentManagement/RUASetting/query'/>",
	                	method: "post",
	                	data: function(d) {
	                		d.search["purposeId"] = $('#purposeId').val();
	                		d.search["unitId"] = $('[name="unitId"]').val();
	                		d.search["productId"] = $('[name="productId"]').val();
	                		d.search["outletId"] = $('[name="outletId"]').val();
	                		d.search["batchId"] = $('#batchId').val();
	                		d.search["pricingFrequencyId"] = $('#pricingFrequencyId').val();
	                		d.search["cpiQoutationType"] = $('#cpiQoutationType').val();
	                	}
	                },
	                "columns": [
								{
	                            	"data": "quotationId",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "purpose",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "unitCode",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "unitName",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "productId",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "productAttribute",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "firmId",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "firmName",
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
	                            	"data": "pricingFrequency",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "cpiFormType",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "ruaDate",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "quotationId",
	                            	"orderable": false,
	                            	"searchable": false,
	                            	"render" : function(data, type, full, meta){
	                            		var html = "<a href='<c:url value='/assignmentManagement/RUASetting/edit?act=edit&id='/>"+data+"' class='table-btn'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a>";
	                            		/*<sec:authorize access="(hasPermission(#user, 2) or hasPermission(#user, 4))">
	                            		html += " &nbsp;<a href='javascript:void(0)' onclick='deleteRecordsWithConfirm(\"id="+data+"\")' class='table-btn btn-delete' data-id='"+data+"'><span class='fa fa-times' aria-hidden='true'></span></a>";
	                					</sec:authorize>*/
	                            		return html;
                            		},
	                            	"className" : "text-center"
                            	}
	                        ]
				});

				$('#unitId,.searchUnitId').unitLookup({
					selectedIdsCallback: function(selectedIds) {
						var singleUrl = $('#unitId').data('singleUrl');
						$('#unitId').empty();
						$.get(singleUrl, { id: selectedIds[0] }, 
							function(data) {
							var option = new Option(data, selectedIds[0]);
							option.selected = true;
							$('#unitId').append(option);
							$('#unitId').trigger('change');
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
				
				$('.select2ajax').select2ajax({
					allowClear: true,
					placeholder: '',
					width: '100%'
				});
				
				$('.select2ajax').hide();
				
				$('.filters').change(function(){
					$dataTable.DataTable().ajax.reload();
				});
			});
			
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>RUA Setting</h1>
        </section>
        
        <section class="content">
        	<div class="row">
				<div class="col-md-12" >
				<!-- content -->
					<form action="<c:url value="/assignmentManagement/RUASetting/delete" />" id="dataForm" method="post">
						<!-- Itinerary Planning Table -->
						<div class="box" >
							<div class="box-body">
								<div class="clearfix">&nbsp;</div>
								<div class="form-horizontal">
									<div class="row">
										<label class="col-md-1 control-label">Purpose</label>
										<div class="col-md-3">
											<select name="purposeId" class="form-control select2ajax filters" id="purposeId"
													data-ajax-url="<c:url value='/assignmentManagement/RUASetting/queryPurposeSelect2'/>"/></select>
										</div>
										<label class="col-sm-1 control-label">Unit</label>
										<div class="col-md-3">
											<div class="input-group">
												<select name="unitId" class="form-control select2ajax filters" id="unitId"
													data-ajax-url="<c:url value='/assignmentManagement/RUASetting/queryUnitSelect2'/>"
													data-single-url="<c:url value='/assignmentManagement/RUASetting/queryUnitSelectSingle'/>"/></select>
												<div class="input-group-addon searchUnitId">
													<i class="fa fa-search"></i>
												</div>
											</div>
										</div>
									</div>
									<div class="row" style="margin-top: 10px;">
										<label class="col-md-1 control-label">Product</label>
										<div class="col-md-3">
											<div class="input-group">
												<select name="productId" class="form-control select2ajax filters" id="productId"
													data-ajax-url="<c:url value='/assignmentManagement/RUASetting/queryProductSelect2'/>"
													data-single-url="<c:url value='/assignmentManagement/RUASetting/queryProductSelectSingle'/>"/></select>
												<div class="input-group-addon searchProductId">
													<i class="fa fa-search"></i>
												</div>
											</div>
										</div>
										<label class="col-md-1 control-label">Outlet</label>
										<div class="col-md-3">
											<div class="input-group">
												<select name="outletId" class="form-control select2ajax filters" id="outletId"
													data-ajax-url="<c:url value='/assignmentManagement/RUASetting/queryOutletSelect2'/>"
													data-single-url="<c:url value='/assignmentManagement/RUASetting/queryOutletSelectSingle'/>"/></select>
												<div class="input-group-addon searchOutletId">
													<i class="fa fa-search"></i>
												</div>
											</div>
										</div>
									</div>
									<div class="row" style="margin-top: 10px;">
										<label class="col-md-1 control-label">Batch</label>
										<div class="col-md-2">
											<select name="batchId" class="form-control select2ajax filters" id="batchId"
												data-ajax-url="<c:url value='/assignmentManagement/RUASetting/queryBatchSelect2'/>"/></select>
										</div>
										<label class="col-md-1 control-label">Pricing Frequency</label>
										<div class="col-md-2">
											<select name="pricingFrequencyId" class="form-control select2ajax filters" id="pricingFrequencyId"
												data-ajax-url="<c:url value='/assignmentManagement/RUASetting/queryPricingFrequencySelect2'/>"></select>
										</div>
										<label class="col-md-1 control-label">CPI Form Type</label>
										<div class="col-md-2">
											<select class="form-control filters" id="cpiQoutationType">
												<option value=""></option>
												<option value="1">Market</option>
												<option value="2">Supermarket</option>
												<option value="3">Batch</option>
												<option value="4">Others</option>
											</select>
										</div>
									</div>
								</div>
								<hr/>
								<table class="table table-striped table-bordered table-hover" id="dataList">
									<thead>
									<tr>
										<th>Quotation Id</th>
										<th>Purpose</th>
										<th>Variety Code</th>
										<th>Variety Name</th>
										<th>Product Id</th>
										<th>Product Attribute</th>
										<th>Firm Id</th>
										<th>Firm Name</th>
										<th>Batch Code</th>
										<th>Pricing Frequency</th>
										<th>CPI Form Type</th>
										<th>RUA Since</th>
										<th class="text-center action"></th>
									</tr>
									</thead>
									<tbody>
									</tbody>
								</table>
							</div>
						</div>
					</form>
				</div>
			</div>
        </section>
	</jsp:body>
</t:layout>

