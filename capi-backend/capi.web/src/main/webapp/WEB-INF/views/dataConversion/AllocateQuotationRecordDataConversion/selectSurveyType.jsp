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
		<%@include file="/WEB-INF/views/includes/datepicker.jsp"%>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/jstree.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/userLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/unitLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/quotationLookup.jsp" %>
		<script>
		
		function clearUser() {
			$('[name="userId"]').val("");
			$('[name="userDisplay"]').val("");
		}
		
		$(document).ready(function(){
			var $dataTable = $("#dataList");
			var selectedIds = [];
			
			function serializeSelectedIds() {
				return $.map(selectedIds, function(val){return "indoorQuotationRecordId=" + val;}).join('&');
			}
			
			var refreshCount = function () {
				var $txtTotalNoOfPending = $('#txtTotalNoOfPending');
				var $txtTotalNoOfRecord = $('#txtTotalNoOfRecord');
				var referenceMonthStr = $('[name="referenceMonthStr"]').val();
				var purposeId = $('[name="purposeId"]').val();
				var url = '<c:url value='/dataConversion/AllocateQuotationRecordDataConversion/getCount'/>';
				
				$.get(url, {
					referenceMonthStr: referenceMonthStr,
					purposeId: purposeId
				}, function (data) {
					$txtTotalNoOfPending.text(data.totalNoOfPending);
					$txtTotalNoOfRecord.text(data.totalNoOfRecord);
				});
			};
			
			$('.searchUserId').userLookup({
				selectedIdsCallback: function(selectedIds, singleRowData) {
					var id = selectedIds[0];
					this.$element.find('input[name="userDisplay"]').val(singleRowData.staffCode + " - " + singleRowData.englishName);
					this.$element.find('input[name="userId"]').val(id);
					
				},
				queryDataCallback: function(model) {
					model.authorityLevel = 128;
				},
				multiple: false
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
			
			var buttons = [
							<sec:authorize access="(hasPermission(#user, 32) or hasPermission(#user, 256))">
							{
								"text": "Allocate Selected",
								"action": function( nButton, oConfig, flash ) {
									var data = serializeSelectedIds();
									var userId = $('[name="userId"]').val();
									if (data == '' ) {
										bootbox.alert({
											title: "Alert",
											message: "<spring:message code='E00009' />"
										});
										//alert('<spring:message code="E00009" />');
										return;
									}
									if(userId == ''){
										bootbox.alert({
											title: "Alert",
											message: "<spring:message code='E00003' />"
										});
										//alert('<spring:message code="E00009" />');
										return;
									}
									data = data+"&userId="+userId;
									
									$.post("<c:url value='/dataConversion/AllocateQuotationRecordDataConversion/allocateSelected'/>",
											data,
											function(response) {
												selectedIds = [];
												$("#dataList").DataTable().ajax.reload(null,false);
												refreshCount();
												$("#MessageRibbon").html(response);
											}
										);
								}
							},
							{
								"text": "Allocate All",
								"action": function( nButton, oConfig, flash ) {
									var $btn = $(nButton.currentTarget);
									var total = $dataTable.DataTable().page.info().recordsDisplay;
									var userId = $('[name="userId"]').val();
									if(userId == ''){
										bootbox.alert({
											title: "Alert",
											message: "<spring:message code='E00003' />"
										});
										//alert('<spring:message code="E00009" />');
										return;
									}
									data = "userId="+userId;
									bootbox.confirm({
									    title: "Confirmation",
									    message: "<spring:message code='W00032' />".replace('{0}', total),
									    callback: function(result){
									    	if(result === true){
									    		$btn.button('loading');
												$.post("<c:url value='/dataConversion/AllocateQuotationRecordDataConversion/allocateAll'/>",
														data,
														function(response) {
															selectedIds = [];
															$("#dataList").DataTable().ajax.reload(null,false);
															refreshCount();
															$("#MessageRibbon").html(response);
															$btn.button('reset');
														}
													);
									    	}
									    }
									});
								}
							},
							{
								"text": "Mark Selected as Complete",
								"action": function( nButton, oConfig, flash ) {
									var data = serializeSelectedIds();
									if (data == '' ) {
										bootbox.alert({
											title: "Alert",
											message: "<spring:message code='E00009' />"
										});
										//alert('<spring:message code="E00009" />');
										return;
									}								
									$.post("<c:url value='/dataConversion/AllocateQuotationRecordDataConversion/markCompleteSelected'/>",
											data,
											function(response) {
												selectedIds = [];
												$("#dataList").DataTable().ajax.reload(null,false);
												refreshCount();
												$("#MessageRibbon").html(response);
											}
										);
									}
							},
							{
								"text": "Mark All as Complete",
								"action": function( nButton, oConfig, flash ) {
									var $btn = $(nButton.currentTarget);
									var total = $dataTable.DataTable().page.info().recordsDisplay;
									bootbox.confirm({
									    title: "Confirmation",
									    message: "<spring:message code='W00031' />".replace('{0}', total),
									    callback: function(result){
									    	if(result === true){
									    		$btn.button('loading');
												$.post("<c:url value='/dataConversion/AllocateQuotationRecordDataConversion/markCompleteAll'/>",
														function(response) {
															selectedIds = [];
															$("#dataList").DataTable().ajax.reload(null,false);
															refreshCount();
															$("#MessageRibbon").html(response);
															$btn.button('reset');
														}
													);
									    	}
									    }
									});
								}
							},
							{
								"text": "Clear Selected “Allocated Indoor Officer”",
								"action": function( nButton, oConfig, flash ) {
									var data = serializeSelectedIds();
									if (data == '' ) {
										bootbox.alert({
											title: "Alert",
											message: "<spring:message code='E00009' />"
										});
										return;
									}

									$.post("<c:url value='/dataConversion/AllocateQuotationRecordDataConversion/clearSelectedIndoorAllocatedOfficer'/>",
											data,
											function(response) {
												selectedIds = [];
												$("#dataList").DataTable().ajax.reload(null,false);
												refreshCount();
												$("#MessageRibbon").html(response);
											}
										);
								}
							},
							{
								"text": "Clear All “Allocated Indoor Officer”",
								"action": function( nButton, oConfig, flash ) {
									var $btn = $(nButton.currentTarget);
									bootbox.confirm({
									    title: "Confirmation",
									    message: "<spring:message code='W00043' />",
									    callback: function(result){
									    	if(result === true){
									    		$btn.button('loading');
												$.post("<c:url value='/dataConversion/AllocateQuotationRecordDataConversion/clearAllIndoorAllocatedOfficer'/>",
														function(response) {
															selectedIds = [];
															$("#dataList").DataTable().ajax.reload(null,false);
															refreshCount();
															$("#MessageRibbon").html(response);
															$btn.button('reset');
														}
													);
									    	}
									    }
									});
								}
							}
						</sec:authorize>
					            ];
			$.fn.dataTable.addResponsiveButtons(buttons);
			
			var table = $dataTable.DataTable({
				"order": [[ 1, "desc" ]],
				"searching": true,
				"ordering": true,				
				"buttons": buttons,
				"processing": true,
                "serverSide": true,
                "ajax": {
                	url: "<c:url value='/dataConversion/AllocateQuotationRecordDataConversion/query'/>",
                	async: false,
                	data: function(d) {
                		d.search["batchCode"] = $('[name="batchCode"]').val() == null ? "" : $('[name="batchCode"]').val().join();
                		d.search["indoorAllocationCode"] = $('[name="indoorAllocationCode"]').val();
                		d.search["purposeId"] = $('[name="purposeId"]').val();
                		d.search["groupId"] = $('[name="groupId"]').val();
                		d.search["subGroupId"] = $('[name="subGroupId"]').val();
                		d.search["itemId"] = $('[name="itemId"]').val();
                		d.search["outletTypeId"] = $('[name="outletTypeId"]').val() == null ? "" : $('[name="outletTypeId"]').val().join();
                		d.search["greaterThan"] = $('[name="greaterThan"]').val();
                		d.search["lessThan"] = $('[name="lessThan"]').val();
                		d.search["equal"] = $('[name="equal"]').val();
                		d.search["ruaQuotationStatus"] = $('[name="ruaQuotationStatus"]').val();
                		d.search["quotationRecordStatus"] = $('[name="quotationRecordStatus"]').val();
                		d.search["seasonalItem"] = $('[name="seasonalItem"]').val();
                		d.search["allocatedIndoorOfficer"] = $('[name="allocatedIndoorOfficer"]').val();
                		d.search["outlectCategoryRemark"] = $('[name="outlectCategoryRemark"]').val();
                		d.search["withPriceRemark"] = $('[name="withPriceRemark"]').val();
                		d.search["withProductRemark"] = $('[name="withProductRemark"]').val();
                		d.search["withDiscountRemark"] = $('[name="withDiscountRemark"]').val();
                		d.search["withOtherRemark"] = $('[name="withOtherRemark"]').val();
                		d.search["newProductCase"] = $('[name="newProductCase"]').val();
                		d.search["changeProductCase"] = $('[name="changeProductCase"]').val();
                		d.search["newRecruitmentCase"] = $('[name="newRecruitmentCase"]').val();
                		d.search["surveyPriceEditPriceNoChange"] = $('[name="surveyPriceEditPriceNoChange"]').val();
                		d.search["spicing"] = $('[name="spicing"]').val();
                		d.search["fr"] = $('[name="fr"]').val();
                		d.search["applicability"] = $('[name="applicability"]').val();
                		d.search["referenceMonthStr"] = $('[name="referenceMonthStr"]').val();
                		d.search["unitId"] = $('[name="unitId"]').val() == null ? "" : $('[name="unitId"]').val().join();
                		d.search["surveyForm"] = $('[name="surveyForm"]').val();
                		d.search["withPriceReason"] = $('[name="withPriceReason"]').val();
                		d.search["consignmentCounter"] = $('[name="consignmentCounter"]').val();
                		d.search["appliedFR"] = $('[name="appliedFR"]').val();
                		d.search["withDiscountPattern"] = $('[name="withDiscountPattern"]').val();
                		d.search["referenceDate"] = $('[name="referenceDate"]').val();
                		d.search["availability"] = $('[name="availability"]').val();
                		d.search["firmStatus"] = $('[name="firmStatus"]').val();
                		d.search["pr"] = $('[name="pr"]').val();
                		d.search["quotationId"] = $('[name="quotationId"]').val() == null ? "" : $('[name="quotationId"]').val().join();
                		d.search["remark"] = $('[name="remark"]').val();
                		},
                	method: 'post'
                },
                "columns": [
							{ "data": "indoorQuotationRecordId",
                            	"render" : function(data, type, full, meta){
                            		var checked = selectedIds.indexOf(data) != -1;
                            		var html = "<input type='checkbox' class='tblChk' name='indoorQuotationRecordId' value='"+data+"' " + (checked ? " checked" : "") + " />";
                            		return html; }
							},
							{ "data": "referenceDate" },
                            { "data": "subGroupEnglishName" },
                            { "data": "subGroupChineseName" },
                            { "data": "unitEnglishName" },
                            { "data": "unitChineseName" },
                            { "data": "outletType" },
                            { "data": "outletName" },
                            { "data": "editedCurrentSPrice"},
                            { "data": "editedPreviousSPrice"},
                            { "data": "pr",
                            	"render" : function(data, type, full, meta){
                            		if(data!=null)
                            			return data.toFixed(3);
                            		return data;
                            	}
                            },
                            { "data": "subPriceUsed",
                            	"render" : function(data, type, full, meta){
                            		if(data){
                            			html = "<span class='glyphicon glyphicon-ok' aria-hidden='true'>";
                            		}else{
                            			html = "<span class='glyphicon glyphicon-remove' aria-hidden='true'>";
                            		}
                            		return html;
                            	}
                            },
                            { "data": "priceRemarks" },
                            { "data": "productRemarks" },
                            { "data": "otherRemarks" },
                            { "data": "allocatedIndoorOfficer" },
                            { "data": "seasonalItem" },
                            { "data": "quotationRecordStatus" },
                            { "data": "indoorQuotationRecordId",
                            	"render" : function(data, type, full, meta){
                            		var html = "<a href='<c:url value='/dataConversion/AllocateQuotationRecordDataConversion/edit?id='/>"+data+"' class='table-btn'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a>";
                            		return html;
                        	}}
                        ],
                "columnDefs": columnDefs,
                "drawCallback": function() {
                	$dataTable.find('.select-all').prop('checked', false);
    				$(".tblChk").click(function (evt){
    					evt.stopPropagation();
    				});
	            }
			});
			
			$('.filters').change(function(){
				selectedIds = [];
				$dataTable.DataTable().ajax.reload();
			});
			
			$('.date-picker').datepicker({
				clearBtn: true
			});	
			
			$('.select2ajax').select2ajax({
				allowClear: true,
				placeholder: '',
				width:"100%"
			});
			$('[name="batchCode"]').select2ajax({
				allowClear: true,
				placeholder: '',
				width: "100%",
				closeOnSelect: false
			});
			$('[name="quotationId"]').select2ajax({
				allowClear: true,
				placeholder: '',
				width: "100%",
				closeOnSelect: false
			});
			
			$('.select2ajax').hide();
			$('[name="batchCode"]').hide();
			$('[name="quotationId"]').hide();
			
			$('#unitId,.searchUnitId').unitLookup({
				selectedIdsCallback: function(selectedIds) {
					var singleUrl = $('#unitId').data('singleUrl');
					var $select = $('#unitId');
					$('#unitId').empty();
					
					if(selectedIds.length == 0) {
						$("#unitLookup.input-group-addon:last-child").css("color", "#555");
						$("#unitLookup.input-group-addon:last-child").css("background-color", "");
						$("#unitLookup.input-group-addon:last-child").css("border-color", "#d2d6de");

						selectedIds = [];
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
			$("#unitId").hide();
			
			$('.searchQuotationIds').quotationLookup({
				selectedIdsCallback: function(selectedIds) {
					
					this.$element.parent().find('select').empty();
					for (var i = 0; i < selectedIds.length; i++) {
						var $html = $('<option value="' + selectedIds[i] + '" selected>' + selectedIds[i] + '</option>');
						this.$element.parent().find('select').append($html);
					}
					this.$element.parent().find('select').trigger('change');
				},
				alreadySelectedIdsCallback: function() {
					return this.$element.parent().find('select').val();
				}
			});
		
			$('button[type="button"]').not($('[name="clearUserBtn"]')).on('click', function() {
				console.log('Reset Button clicked.');
				
				$('.criteria').select2ajax('destroy');
				$('[name="batchCode"]').select2ajax('destroy');
				
				$('.criteria').find("option").remove();
				$('[name="batchCode"]').find("option").remove();
				$('[name="quotationId"]').find("option").remove();
				$('[name="unitId"]').find("option").remove();
				$('[type="text"]').val("");
				$('.select2').val("");
				$('[name="userId"]').val("");
				$('[name="userDisplay"]').val("");
				
				$('.criteria').select2ajax({
					allowClear: true,
					placeholder: '',
					width:"100%"
				});
				$('[name="batchCode"]').select2ajax({
					allowClear: true,
					placeholder: '',
					width: "100%",
					closeOnSelect: false
				});
				$('[name="quotationId"]').select2ajax({
					allowClear: true,
					placeholder: '',
					width: "100%",
					closeOnSelect: false
				});
				
				var $jstree = $('div.jstree:hidden');
				$jstree.jstree('deselect_all');
				$jstree.jstree('close_all');
				$("#unitLookup.input-group-addon:last-child").css("color", "#555");
				$("#unitLookup.input-group-addon:last-child").css("background-color", "");
				$("#unitLookup.input-group-addon:last-child").css("border-color", "#d2d6de");

				selectedIds = [];
				$dataTable.DataTable().ajax.reload();
			});
			
			$dataTable.find('.select-all').click(function() {
				$dataTable.find('[type="checkbox"]').prop('checked', $(this).prop('checked')).trigger('change');
			});
			
			$dataTable.on('change', '.tblChk', function() {
				var id = +$(this).val();
				if ($(this).prop('checked')) {
		    		if (selectedIds.indexOf(id) == -1) {
		    			selectedIds.push(id);
		    		}
		    	} else {
		    		var index = selectedIds.indexOf(id);
		    		if (index != -1) {
		    			selectedIds.splice(index, 1);
		    		}
		    	}
			});
			
			refreshCount();
		})
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Allocate Quotation Record Data Conversion</h1>
        </section>
        
        <section class="content">
        	<div class="row">
				<div class="col-md-12">
				<!-- content -->
					<!-- Quotation Table -->
					<div class="box" >
						<div class="box-header with-border">
							<a class="btn btn-default btn-back" href="<c:url value="/dataConversion/AllocateQuotationRecordDataConversion/home"/>">Back</a>
							
						</div>
						<div class="box-body">
							<form class="form-horizontal">
								<div class="row">
									<label class="col-md-1 control-label">Batch Code</label>
									<div class="col-md-2">
										<select name="batchCode" class="form-control filters" id="batchCode" multiple
											data-ajax-url="<c:url value='/dataConversion/AllocateQuotationRecordDataConversion/queryBatchCodeSelect2'/>" /></select>
									</div>
									<label class="col-md-2 control-label">Indoor Allocation Code</label>
									<div class="col-md-2">
										<select name="indoorAllocationCode" class="form-control select2ajax filters criteria" id="indoorAllocationCode"
											data-ajax-url="<c:url value='/dataConversion/AllocateQuotationRecordDataConversion/queryIndoorAllocationCodeSelect2'/>"></select>
									</div>
									<label class="col-md-2 control-label">Variety</label>
									<div class="col-md-2">
	   									<div class="input-group">
	   										<select name="unitId" class="form-control filters" id="unitId" multiple
		   										data-close-on-select="false"
		   										data-allow-clear="true"
												data-placeholder=""
												data-ajax-url="<c:url value='/dataConversion/AllocateQuotationRecordDataConversion/queryUnitSelect2'/>"
												data-single-url="<c:url value='/dataConversion/AllocateQuotationRecordDataConversion/queryUnitSelectSingle'/>"/></select>	
											<div id="unitLookup" class="input-group-addon searchUnitId">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
									
									<%--
									<label class="col-md-1 control-label">Group</label>
									<div class="col-md-2">
										<select name="groupId" class="form-control select2ajax filters" id="subGroupId"
											data-ajax-url="<c:url value='/dataConversion/AllocateQuotationRecordDataConversion/queryGroupSelect2'/>" /></select>
									</div>
									--%>
								</div>
								
								<%--
								<div class="row">
									<label class="col-md-1 control-label">Sub Group</label>
									<div class="col-md-2">
										<select name="subGroupId" class="form-control select2ajax filters" id="subGroupId"
											data-ajax-url="<c:url value='/dataConversion/AllocateQuotationRecordDataConversion/querySubGroupSelect2'/>" /></select>
									</div>
									<label class="col-md-1 control-label">Item</label>
									<div class="col-md-2">
										<select name="itemId" class="form-control select2ajax filters" id="itemId"
											data-ajax-url="<c:url value='/dataConversion/AllocateQuotationRecordDataConversion/queryItemSelect2'/>" /></select>
									</div>
									<label class="col-md-1 control-label">Outlet Type</label>
									<div class="col-md-2">
										<select name="outletTypeId" class="form-control select2ajax filters" id="outletTypeId"  data-close-on-select="false" multiple
											data-ajax-url="<c:url value='/dataConversion/AllocateQuotationRecordDataConversion/queryOutletTypeSelect2'/>" /></select>
									</div>
								</div>
								 --%>
								<div class="row" style="margin-top: 10px;">
									<label class="col-md-1 control-label">Edited Price Change:</label>
									<div class="col-md-5">
										<div class="input-group">
											<div class="input-group-addon">
												&gt;
											</div>
											<input type="text" class="form-control filters"	name="greaterThan" value="" />
											<div class="input-group-addon">
												Or
											</div>
											<div class="input-group-addon">
												&lt;
											</div>
											<input type="text" class="form-control filters"	name="lessThan" value="" />
											<div class="input-group-addon">
												Or
											</div>
											<div class="input-group-addon">
												=
											</div>
											<input type="text" class="form-control filters"	name="equal" value="" />
										</div>
									</div>
									<label class="col-md-2 control-label">PR = null</label>
									<div class="col-md-2">
										<select name="pr" class="form-control select2 filters" id="pr" >
											<option value=""> </option>
											<option value="1">Y</option>
											<option value="0">N</option>
										</select>								
									</div>
									
									<%--
									<label class="col-md-1 control-label">Quotation Record Status</label>
									<div class="col-md-2">
										<select name="quotationRecordStatus" class="form-control select2 filters" id="allocatedIndoorOfficer" >
											<option value=""> </option>
											<option value="1">Pending</option>
											<option value="0">Not Pending</option>
										</select>								
									</div>
									 --%>
								</div>
								<div class="row" style="margin-top: 10px;">
								<%--
									<label class="col-md-1 control-label">Seasonal Item</label>
									<div class="col-md-2">
										<select name="seasonalItem" class="form-control select2 filters" id="seasonalItem" >
											<option value=""> </option>
											<option value="1">All-time</option>
											<option value="2">Summer</option>
											<option value="3">Winter</option>
											<option value="4">Occasional</option>
										</select>								
									</div>
								 --%>
									<label class="col-md-1 control-label">Allocated Indoor Office</label>
									<div class="col-md-2">
										<select name="allocatedIndoorOfficer" class="form-control select2 filters" id="allocatedIndoorOfficer" >
											<option value=""> </option>											
											<option value="0">Not Exists</option>
											<c:forEach items="${users}" var="user">
											   <option value="${user.userId }"><c:out value="${user.staffCode}"/> - <c:out value="${user.chineseName}"/></option>
											</c:forEach>
											
										</select>
									</div>
									<label class="col-md-2 control-label">Outlet Category Remark</label>
									<div class="col-md-2">
										<select name="outlectCategoryRemark" class="form-control select2 filters" id="quotationCategoryRemark" >
											<option value=""> </option>
											<option value="1">Exists</option>
											<option value="0">Not Exists</option>
										</select>								
									</div>
									<label class="col-md-2 control-label">With Outlet Discount Remark</label>
									<div class="col-md-2">
										<select name="withOtherRemark" class="form-control select2 filters" id="withOtherRemark" >
											<option value=""> </option>
											<option value="1">Exists</option>
											<option value="0">Not Exists</option>
										</select>
									</div>
								</div>
								<div class="row" style="margin-top: 10px;">
									<label class="col-md-1 control-label">With Price Remark</label>
									<div class="col-md-2">
										<select name="withPriceRemark" class="form-control select2 filters" id="withPriceRemark" >
											<option value=""> </option>
											<option value="1">Exists</option>
											<option value="0">Not Exists</option>
										</select>
									</div>
									<label class="col-md-2 control-label">With Product Remark</label>
									<div class="col-md-2">
										<select name="withProductRemark" class="form-control select2 filters" id="withProductRemark" >
											<option value=""> </option>
											<option value="1">Exists</option>
											<option value="0">Not Exists</option>
										</select>
									</div>
									<label class="col-md-2 control-label">With Discount Remark</label>
									<div class="col-md-2">
										<select name="withDiscountRemark" class="form-control select2 filters" id="withDiscountRemark" >
											<option value=""> </option>
											<option value="1">Exists</option>
											<option value="0">Not Exists</option>
										</select>
									</div>
									
								</div>
								<div class="row" style="margin-top: 10px;">
								<%--
									<label class="col-md-1 control-label">New Product Case</label>
									<div class="col-md-2">
										<select name="newProductCase" class="form-control select2 filters" id="newProductCase" >
											<option value=""> </option>
											<option value="1">Exists</option>
											<option value="0">Not Exists</option>
										</select>
									</div>
								 --%>
									<label class="col-md-1 control-label">Change Product Case</label>
									<div class="col-md-2">
										<select name="changeProductCase" class="form-control select2 filters" id="changeProductCase" >
											<option value=""> </option>
											<option value="1">Changed</option>
											<option value="0">No Changes</option>
										</select>
									</div>
									<label class="col-md-2 control-label">New Recruitment Case</label>
									<div class="col-md-2">
										<select name="newRecruitmentCase" class="form-control select2 filters" id="newRecruitmentCase" >
											<option value=""> </option>
											<option value="1">Is New</option>
											<option value="0">Not New</option>
										</select>
									</div>
									<label class="col-md-2 control-label">RUA Quotation Status</label>
									<div class="col-md-2">
										<select name="ruaQuotationStatus" class="form-control select2 filters" id="ruaQuotationStatus" >
											<option value=""> </option>
											<option value="1">RUA only</option>
											<option value="0">non-RUA</option>
										</select>								
									</div>
								</div>
								<div class="row" style="margin-top: 10px;">
									<label class="col-md-1 control-label">Splicing</label>
									<div class="col-md-2">
										<select name="spicing" class="form-control select2 filters" id="spicing" >
											<option value=""> </option>
											<option value="1">Is splicing</option>
											<option value="0">non-splicing</option>
										</select>
									</div>
									<label class="col-md-2 control-label">FR</label>
									<div class="col-md-2">
										<select name="fr" class="form-control select2 filters" id="fr" >
											<option value=""> </option>
											<option value="1">Is FR</option>
											<option value="0">non-FR</option>
										</select>
									</div>
									<label class="col-md-2 control-label">Applicability</label>
									<div class="col-md-2">
										<select name="applicability" class="form-control select2 filters" id="applicability" >
											<option value=""> </option>
											<option value="1">Applicable</option>
											<option value="0">Not Applicable</option>
										</select>
									</div>
								</div>
								
								<div class="row" style="margin-top: 10px;">
									<label class="col-md-1 control-label">With Price Reason</label>
									<div class="col-md-2">
										<select name="withPriceReason" class="form-control select2 filters" id="withPriceReason" >
											<option value=""> </option>
											<option value="1">Exists</option>
											<option value="0">Not Exists</option>
										</select>
									</div>
									<label class="col-md-2 control-label">Survey Form</label>
									<div class="col-md-2">
										<select name="surveyForm" class="form-control select2 filters" id="surveyForm" >
											<option value=""> </option>
											<c:forEach items="${surveyForms}" var="surveyForm">
												<option value="<c:out value='${surveyForm}' />"><c:out value='${surveyForm}' /></option>
											</c:forEach>
										</select>
									</div>
									<label class="col-md-2 control-label">Consignment Counter</label>
									<div class="col-md-2">
										<select name="consignmentCounter" class="form-control select2 filters" id="consignmentCounter" >
											<option value=""> </option>
											<option value="1">Yes</option>
											<option value="0">No</option>
										</select>
									</div>
								</div>
								
								<div class="row" style="margin-top: 10px;">
									<label class="col-md-1 control-label">Applied FR in this season</label>
									<div class="col-md-2">
										<select name="appliedFR" class="form-control select2 filters" id="appliedFR" >
											<option value=""> </option>
											<option value="1">Y</option>
											<option value="0">N</option>
										</select>
									</div>
									<label class="col-md-2 control-label">Discount Pattern</label>
									<div class="col-md-2">
										<select name="withDiscountPattern" class="form-control select2 filters" id="withDiscountPattern" >
											<option value=""> </option>
											<option value="0">Empty</option>
											<option value="1">Not Empty</option>
										</select>
									</div>
									<label class="col-md-2 control-label">Firm Status</label>
									<div class="col-md-2">
										<select name="firmStatus" class="form-control select2 filters" id="firmStatus" >
											<option value=""> </option>
											<option value="1">Enumeration (EN)</option>
											<option value="2">Closed (CL)</option>
											<option value="3">Move (MV)</option>
											<option value="4">Not Suitable (NS)</option>
											<option value="5">Refusal (NR)</option>
											<option value="6">Wrong Outlet (WO)</option>
											<option value="7">Door Lock (DL)</option>
											<option value="8">Non-contact (NC)</option>
											<option value="9">In Progress (IP)</option>
											<option value="10">Duplication (DU)</option>
										</select>
									</div>
								</div>
								
								<div class="row" style="margin-top: 10px;">
									<label class="col-md-1 control-label">Reference Date</label>
									<div class="col-sm-2">
										<div class="input-group">
											<input type="text" name="referenceDate" class="form-control date-picker filters" id="referenceDate"/>
											<div class="input-group-addon">
												<i class="fa fa-calendar"></i>
											</div>
										</div>
									</div>
									<label class="col-md-2 control-label">Availability</label>
									<div class="col-md-2">
										<select id="availability" name="availability" class="form-control select2 filters">
											<option value=""> </option>
											<option value="1">Available</option>
											<option value="2">IP</option>
											<option value="3">有價無貨</option>
											<option value="4">缺貨</option>
											<option value="5">Not Suitable</option>
											<option value="6">回倉</option>
											<option value="7">無團</option>
											<option value="8">未返</option>
											<option value="9">New</option>
										</select>
									</div>
									<label class="col-md-2 control-label">With Indoor Conversion Remarks</label>
									<div class="col-md-2">
										<select name="remark" class="form-control select2 filters" id="remark" >
											<option value=""> </option>
											<option value="1">Y</option>
											<option value="0">N</option>
										</select>
									</div>
								</div>
								
								<div class="row" style="margin-top: 10px;">
									<label class="col-md-1 control-label">Quotation</label>
									<div class="col-md-2">
										<div class="input-group">
											<select name="quotationId" class="form-control filters"
												data-ajax-url="<c:url value='/dataConversion/AllocateQuotationRecordDataConversion/queryQuotationSelect2'/>"
												multiple></select>
											<div id="quotationLookup" class="input-group-addon searchQuotationIds">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
									
								</div>
								
								<div class="row" style="margin-top: 10px;">
									<div class="col-md-offset-1 col-md-1">
										<button type="button" class="btn btn-info" <%--onclick="$('#dataList').DataTable().ajax.reload();"--%>>Clear</button>
									</div>
									<div class="col-md-offset-8 col-md-2">
										<span>
											Reference Month: ${referenceMonthStr}<br/>
											Purpose: ${purpose.name}<br/>
											Total Pending Records: <span id="txtTotalNoOfPending"></span><br/>
											Total Records: <span id="txtTotalNoOfRecord"></span>
										</span>
									</div>
								</div>
							</form>
							<input type="hidden" name="referenceMonthStr" value="${referenceMonthStr}" readonly disabled/>
							<input type="hidden" name="purposeId" value="${purpose.purposeId}" readonly disabled/>
							<hr/>
							<form class="form-horizontal">
								<div class="col-md-12">
									<div class="row" style="margin-top: 10px;">
											<label class="col-sm-1 control-label">Indoor Data Conversion</label>
											<div class="col-md-2">
												<div>
													<div class="input-group searchUserId">
														<input type="hidden" name="userId" class="form-control" readonly>
														<input type="text" name="userDisplay" class="form-control" readonly>
														<div class="input-group-addon">
															<i class="fa fa-search"></i>
														</div>	
													</div>
												</div>
											</div>
											<div class= col-md-1">
												<button type="button" class="btn btn-info clearBtn" name="clearUserBtn" onclick="clearUser()">Clear</button>
											</div>
									</div>
								</div>
								<div class="col-md-12">
									<div class="row" style="margin-top: 10px;">
										<table class="table table-striped table-bordered table-hover responsive" id="dataList">
											<thead>
											<tr>
												<th class="text-center action" data-priority="1"><input class="select-all" type="checkbox" /></th>
												<th>Reference Date</th>
												<th>Sub-Group English Name</th>
												<th>Sub-Group Chinese Name</th>
												<th>Variety English Name</th>
												<th>Variety Chinese Name</th>
												<th>Outlet Type</th>
												<th>Outlet Name</th>
												<th>Edited Current S Price</th>
												<th>Edited Previous S Price</th>
												<th>PR</th>
												<th>Sub Price Used</th>
												<th>Field Record Price Reason</th>
												<th>Product Remarks</th>
												<th>Field Record Price Remark</th>
												<th>Allocated Indoor Officer</th>
												<th>Seasonal Item</th>
												<th>Quotation Record Status</th>
												<th class="text-center action" data-priority="1"></th>
											</tr>
											</thead>
										</table>
									</div>
								</div>
							</form>
						</div>
					</div>
				</div>
			</div>
        </section>
	</jsp:body>
</t:layout>

		
