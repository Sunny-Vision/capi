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
			#unitLookup.input-group-addon:last-child {
				border-radius: 4px;
				border-left: 1px solid #d2d6de;
				width: 0%;
			}
		</style>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/jstree.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/unitLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/productLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/outletLookup.jsp" %>
		<script>
		$(document).ready(function(){
			var $dataTable = $("#dataList");
			var cacheLastSearch = null;
			
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
			var buttons = [
				{
					"text": "Flag",
					"action": function( nButton, oConfig, flash ) {
						var data = $dataTable.find(':checked').serialize()
						if (data == '') {
							bootbox.alert({
								title: "Alert",
								message: "<spring:message code='E00009' />"
							});
							//alert('<spring:message code="E00009" />');
							return;
						}
						data = data+"&flag=true";
						$.post("<c:url value='/dataConversion/QuotationRecordDataConversion/flag'/>",
								data,
								function(response) {
									$("#dataList").DataTable().ajax.reload(null,false);
									$("#MessageRibbon").html(response);
								}
							);
					}
				},{
					"text": "UnFlag",
					"action": function( nButton, oConfig, flash ) {
						var data = $dataTable.find(':checked').serialize();
						if (data == '') {
							bootbox.alert({
								title: "Alert",
								message: "<spring:message code='E00009' />"
							});
							//alert('<spring:message code="E00009" />');
							return;
						}
						data = data+"&flag=false";
						
						$.post("<c:url value='/dataConversion/QuotationRecordDataConversion/flag'/>",
								data,
								function(response) {
									$("#dataList").DataTable().ajax.reload(null,false);
									$("#MessageRibbon").html(response);
								}
							);
					}
				}];
			
			$.fn.dataTable.addResponsiveButtons(buttons);
			
			var tableOrder = [[ 1, "desc" ]];
			if ($('#lastRequestModelOrderColumn').val() != '') {
				tableOrder = [[ $('#lastRequestModelOrderColumn').val(), $('#lastRequestModelOrderDir').val() ]];
			}
			
			var table = $dataTable.DataTable({
				"order": tableOrder,
				"searching": true,
				"ordering": true,				
				"buttons": buttons,
				"processing": true,
                "serverSide": true,
                "search" : {
                	"search" : $('#lastRequestModelSearch').val()
                },
                "ajax": {
                	url: "<c:url value='/dataConversion/QuotationRecordDataConversion/query'/>",
                	data: function(d) {
                		d.search["purposeId"] = $('[name="purposeId"]').val();
                		d.search["referenceMonthStr"] = $('[name="referenceMonthStr"]').val();
                		d.search["unitId"] = $('[name="unitId"]').val() == null ? "" : $('[name="unitId"]').val().join();
                		d.search["subGroupId"] = $('[name="subGroupId"]').val();
                		d.search["outletId"] = $('[name="outletId"]').val();
                		d.search["outletTypeId"] = $('[name="outletTypeId"]').val() == null ? "" : $('[name="outletTypeId"]').val().join();
                		d.search["outletCategory"] = $('[name="outletCategory"]').val();
                		d.search["subPrice"] = $('[name="subPrice"]').val();
                		d.search["priceRemark"] = $('[name="priceRemark"]').val();
                		d.search["seasonalItem"] = $('[name="seasonalItem"]').val();
                		d.search["outletCategoryRemark"] = $('[name="outletCategoryRemark"]').val();
                		d.search["surveyForm"] = $('[name="surveyForm"]').val();
                		d.search["availability"] = $('[name="availability"]').val();
                		d.search["firmStatus"] = $('[name="firmStatus"]').val();
                		d.search["isPRNull"] = $('[name="isPRNull"]').val();
                		d.search["quotationId"] = $('[name="quotationId"]').val();
                		cacheLastSearch = d;
                	},
                	method: 'post'
                },
                "columns": [
                            { "data": "indoorQuotationRecordId",
                            	"render" : function(data, type, full, meta){
                            		var html = '<input name="id" class="tblChk" value="'+data+'" type="checkbox" />';
                            		return html;
                            		}
                            },
                            { "data": "referenceDate" },
                            { "data": "indoorQuotationRecordId" },
                            { "data": "quotationId" },
                            { "data": "isFlag",
                            	"render" : function(data, type, full, meta){
                            		var html = data ? '<span class="glyphicon glyphicon-star" style="color:rgb(255, 204, 0)">&nbsp;</span>' : "";
                            		return html;
                            		} },
                            { "data": "subGroupEnglishName" },
                            { "data": "subGroupChineseName" },
                            { "data": "unitEnglishName" },
                            { "data": "unitChineseName" },
                            { "data": "outletType" },
                            { "data": "outletName" },
                            { "data": "productAttribute1" },
                            { "data": "editedCurrentSPrice" },
                            { "data": "editedPreviousSPrice" },
                            { "data": "pr" },
                            { "data": "subPrice",
                            	"render" : function(data, type, full, meta){
                            		if(data){
                            			html = "<span class='glyphicon glyphicon-ok' aria-hidden='true'>";
                            		}else{
                            			html = "<span class='glyphicon glyphicon-remove' aria-hidden='true'>";
                            		}
                            		return html;
                            	}
                            },
                            { "data": "seasonalItem" },
                            { "data": "quotationRecordStatus" },
                            { "data": "priceRemarks" },
                            { "data": "productRemarks" },
                            { "data": "otherRemarks" },
                            { "data": "indoorQuotationRecordId",
                            	"render" : function(data, type, full, meta){
                            		var html = "<a href='javascript:void(0)' data-id='"+data+"' class='table-btn btn-edit'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a>";
                            		return html;
                        	}}
                        ],
                "columnDefs": columnDefs,
                "drawCallback": function() {
    				$(".tblChk").click(function (evt){
    					evt.stopPropagation();
    				})
    				
	            }
			});
			
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
					
					/*$.post(singleUrl, { id: selectedIds }, 
						function(data) {
							$select.empty();
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
			
			$('button[type="button"]').on('click', function() {
				$('.criteria').select2ajax('destroy');
				
				$('.criteria').find("option").remove();
				$('[name="unitId"]').find("option").remove();
				$('[name="surveyForm"]').val("");
				$('[name="seasonalItem"]').val("");
				$('[name="availability"]').val("")
				$('[name="firmStatus"]').val("");
				$('[name="isPRNull"]').val("");
				$('[name="quotationId"]').val("");
				
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
			

			$dataTable.on('click', '.btn-edit', function() {
				var id = $(this).data("id");
				var d = cacheLastSearch;
				Modals.startLoading();

				$.post("<c:url value='/dataConversion/QuotationRecordDataConversion/fetchIdList'/>", d, function(){
					window.location = "<c:url value='/dataConversion/QuotationRecordDataConversion/edit?id='/>" + id;
				});
			});
		});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Quotation Record Data Conversion</h1>
        </section>
        
        <section class="content">
        	<div class="row">
				<div class="col-md-12">
				<!-- content -->
					<!-- Quotation Table -->
					<div class="box" >
						<div class="box-header with-border">
							<a class="btn btn-default btn-back" href="<c:url value="/dataConversion/QuotationRecordDataConversion/home"/>">Back</a>
							
						</div>
						<div class="box-body">
						<%--
						<a class="btn btn-default btn-back" href="<c:url value="/dataConversion/QuotationRecordDataConversion/home"/>">Back</a>
							 --%>
							<form class="form-horizontal">
								<input id="lastRequestModelSearch" value="<c:out value="${viewModel.search}"/>" type="hidden" />
								
								<input id="lastRequestModelOrderColumn" value="<c:out value="${viewModel.orderColumn}"/>" type="hidden" />
								<input id="lastRequestModelOrderDir" value="<c:out value="${viewModel.orderDir}"/>" type="hidden" />
								<div class="row">
								<%--
									<label class="col-md-1 control-label">Sub Group</label>
									<div class="col-md-2">
										<select name="subGroupId" class="form-control select2ajax filters" id="subGroupId"
											data-ajax-url="<c:url value='/dataConversion/QuotationRecordDataConversion/querySubGroupSelect2'/>" /></select>
									</div>
								 --%>
									<label class="col-md-1 control-label">Outlet Type</label>
									<div class="col-md-2">
										<select name="outletTypeId" class="form-control select2ajax filters criteria" id="outletTypeId"  data-close-on-select="false" 
											multiple
											data-ajax-url="<c:url value='/dataConversion/QuotationRecordDataConversion/queryOutletTypeSelect2'/>">
											<c:forEach items="${viewModel.outletTypeSelected}" var="outletTypeSelected">
											   <option value="<c:out value="${outletTypeSelected.value}"/>" selected><c:out value="${outletTypeSelected.key}"/></option>
											</c:forEach>
										</select>
									</div>
									<label class="col-md-1 control-label">Variety Category</label>
									<div class="col-md-2">
										<select name="outletCategory" class="form-control select2ajax filters criteria" id="outletCategory"
											data-ajax-url="<c:url value='/dataConversion/QuotationRecordDataConversion/queryValidDistinctUnitCategorySelect2'/>" />
											<c:if test="${viewModel.unitCategorySelected != null}">
												<option value="<c:out value="${viewModel.unitCategorySelected}"/>" selected><c:out value="${viewModel.unitCategorySelected}"/></option>
											</c:if>
										</select>
									</div>
									<label class="col-md-1 control-label">Outlet</label>
									<div class="col-md-2">
	   									<div class="input-group">
	   										<select name="outletId" class="form-control select2ajax filters criteria" id="outletId"
											data-ajax-url="<c:url value='/dataConversion/QuotationRecordDataConversion/queryOutletSelect2'/>"
											data-single-url="<c:url value='/dataConversion/QuotationRecordDataConversion/queryOutletSelectSingle'/>">
												<c:if test="${viewModel.outletSelected != null}">
													<option value="<c:out value="${viewModel.outletSelected.value}"/>" selected><c:out value="${viewModel.outletSelected.key}"/></option>
												</c:if>
											</select>	
											<div class="input-group-addon searchOutletId">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								<div class="row" style="margin-top: 10px;">
									<label class="col-md-1 control-label">Variety</label>
									<div class="col-md-2">
	   									<div class="input-group">
	   										<select name="unitId" class="form-control filters" id="unitId" multiple
	   										data-close-on-select="false"
	   										data-allow-clear="true"
											data-placeholder=""
											data-ajax-url="<c:url value='/dataConversion/QuotationRecordDataConversion/queryUnitSelect2'/>"
											data-single-url="<c:url value='/dataConversion/QuotationRecordDataConversion/queryUnitSelectSingle'/>"/></select>	
											<div id="unitLookup" class="input-group-addon searchUnitId">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
									
									<%--
									<label class="col-md-1 control-label">Sub Price</label>
									<div class="col-md-1">
										<select name="subPrice" class="form-control select2 filters" id="subPrice" >
											<option value=""> </option>
											<option value="1">Exist</option>
											<option value="0">Non-exist</option>
										</select>								
									</div>
									<label class="col-md-1 control-label">Price Remark</label>
									<div class="col-md-1">
										<select name="priceRemark" class="form-control select2 filters" id="priceRemark" >
											<option value=""> </option>
											<option value="1">Exist</option>
											<option value="0">Non-exist</option>
										</select>								
									</div>
									 --%>
									<label class="col-md-1 control-label">Survey Form</label>
									<div class="col-md-2">
										<select name="surveyForm" class="form-control select2 filters" id="surveyForm" >
											<option value=""> </option>
											<c:forEach items="${surveyForms}" var="surveyForm">
												<option value="<c:out value='${surveyForm}' />" ${viewModel.surveyFormSelected == surveyForm ? "selected" : ""}><c:out value='${surveyForm}' /></option>
											</c:forEach>
										</select>
									</div>
									<label class="col-md-1 control-label">Seasonal Item</label>
									<div class="col-md-2">
										<select name="seasonalItem" class="form-control select2 filters" id="seasonalItem" >
											<option value=""> </option>
											<option value="1" ${viewModel.seasonalItemSelected == "1" ? "selected" : ""}>All-time</option>
											<option value="2" ${viewModel.seasonalItemSelected == "2" ? "selected" : ""}>Summer</option>
											<option value="3" ${viewModel.seasonalItemSelected == "3" ? "selected" : ""}>Winter</option>
											<option value="4" ${viewModel.seasonalItemSelected == "4" ? "selected" : ""}>Occasional</option>
										</select>								
									</div>
								</div>
								<div class="row" style="margin-top: 10px;">
									<label class="col-md-1 control-label">Availability</label>
									<div class="col-md-2">
										<select name="availability" class="form-control select2 filters" id="availability" >
											<option value=""> </option>
											<option value="1" ${viewModel.availabilitySelected == "1" ? "selected" : ""}>Available</option>
<!-- 											<option value="2" ${viewModel.availabilitySelected == "2" ? "selected" : ""}></option> -->
<!-- 											<option value="3" ${viewModel.availabilitySelected == "3" ? "selected" : ""}>有價無貨</option> -->
											<option value="4" ${viewModel.availabilitySelected == "4" ? "selected" : ""}>缺貨</option>
											<option value="5" ${viewModel.availabilitySelected == "5" ? "selected" : ""}>Not Suitable</option>
											<option value="6" ${viewModel.availabilitySelected == "6" ? "selected" : ""}>回倉</option>
											<option value="7" ${viewModel.availabilitySelected == "7" ? "selected" : ""}>無團</option>
											<option value="8" ${viewModel.availabilitySelected == "8" ? "selected" : ""}>未返</option>
											<option value="9" ${viewModel.availabilitySelected == "9" ? "selected" : ""}>New</option>
										</select>
									</div>
									<label class="col-md-1 control-label">Firm status</label>
									<div class="col-md-2">
										<select name="firmStatus" class="form-control select2 filters" id="firmStatus">
											<option value=""> </option>
											<option value="1" ${viewModel.firmStatusSelected == "1" ? "selected" : ""}>Enumeration (EN)</option>
											<option value="2" ${viewModel.firmStatusSelected == "2" ? "selected" : ""}>Closed (CL)</option>
											<option value="3" ${viewModel.firmStatusSelected == "3" ? "selected" : ""}>Move (MV)</option>
											<option value="4" ${viewModel.firmStatusSelected == "4" ? "selected" : ""}>Not Suitable (NS)</option>
											<option value="5" ${viewModel.firmStatusSelected == "5" ? "selected" : ""}>Refusal (NR)</option>
											<option value="6" ${viewModel.firmStatusSelected == "6" ? "selected" : ""}>Wrong Outlet (WO)</option>
											<option value="7" ${viewModel.firmStatusSelected == "7" ? "selected" : ""}>Door Lock (DL)</option>
											<option value="8" ${viewModel.firmStatusSelected == "8" ? "selected" : ""}>Non-contact (NC)</option>
											<option value="9" ${viewModel.firmStatusSelected == "9" ? "selected" : ""}>In Progress (IP)</option>
											<option value="10" ${viewModel.firmStatusSelected == "10" ? "selected" : ""}>Duplication (DU)</option>
										</select>
									</div>
									<label class="col-md-1 control-label">PR=null</label>
									<div class="col-md-2">
										<select name="isPRNull" class="form-control select2 filters" id="isPRNull" >
											<option value=""> </option>
											<option value="1" ${viewModel.isPRNullSelected == "1" ? "selected" : ""}>Y</option>
											<option value="0" ${viewModel.isPRNullSelected == "0" ? "selected" : ""}>N</option>
										</select>
									</div>
								</div>
								<div class="row" style="margin-top: 10px;">
									<label class="col-md-1 control-label">Quotation Id</label>
									<div class="col-md-2">
										<select name="quotationId" class="form-control select2ajax filters criteria" id="quotationId" 
											data-ajax-url="<c:url value='/dataConversion/QuotationRecordDataConversion/queryQuotationIdSelect2'/>">
											<c:if test="${viewModel.quotationIdSelected != null}">
												<option value="<c:out value="${viewModel.quotationIdSelected}"/>" selected><c:out value="${viewModel.quotationIdSelected}"/></option>
											</c:if>	
										</select>
									</div>
								</div>
									<%--
									<label class="col-md-2 control-label">Outlet Category Remark</label>
									<div class="col-md-1">
										<select name="outletCategoryRemark" class="form-control select2 filters" id="outletCategoryRemark" >
											<option value=""> </option>
											<option value="1">Exist</option>
											<option value="0">non-Exist</option>
										</select>								
									</div>
									--%>
								</div>
								<div class="row" style="margin-top: 10px;">
									<div class="col-md-offset-1 col-md-1">
										<button type="button" class="btn btn-info" <%--onclick="$('#dataList').DataTable().ajax.reload();"--%>>Clear</button>
									</div>
									<div class="col-md-offset-8 col-md-2">
										<span>
											Reference Month: ${referenceMonthStr}<br/>
											Purpose: ${purpose.name}<br/>
											Total no. of Outlets: ${totalNoOfOutlet}<br/>
											Total no. of Quotations: ${totalNoOfQuotation}
										</span>
									</div>
								</div>
							</form>
							<input type="hidden" name="referenceMonthStr" value="${referenceMonthStr}" readonly disabled/>
							<input type="hidden" name="purposeId" value="${purpose.purposeId}" readonly disabled/>
							<hr/>
							<table class="table table-striped table-bordered table-hover responsive" id="dataList">
								<thead>
								<tr>
									<th class="text-center action"></th>
									<th>Reference Date</th>
									<th>Indoor Quotation Record ID</th>
									<th>Quotation ID</th>
									<th class="text-center">Flag</th>
									<th>Sub-Group English Name</th>
									<th>Sub-Group Chinese Name</th>
									<th>Variety English Name</th>
									<th>Variety Chinese Name</th>
									<th>Outlet Type</th>
									<th>Outlet Name</th>
									<th>Product Attribute 1</th>
									<th>Edited Current S Price</th>
									<th>Edited Previous S Price</th>
									<th>PR</th>
									<th>Sub Price</th>
									<th>Seasonal Item</th>
									<th>Quotation Record Status</th>
									<th>Field Record Price Reason</th>
									<th>Product Remarks</th>
									<th>Field Record Price Remark</th>
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

		