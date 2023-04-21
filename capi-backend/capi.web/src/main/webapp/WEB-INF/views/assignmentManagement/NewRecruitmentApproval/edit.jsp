<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<jsp:useBean id="niceDate" class="java.util.Date"/>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/timepicker-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
	</jsp:attribute>
	<jsp:attribute name="header">
		<%@include file="/WEB-INF/views/includes/moment.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker.jsp"%>
		<%@include file="/WEB-INF/views/includes/timepicker.jsp" %>
		<%@include file="/WEB-INF/views/includes/handlebars.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/radioPhotos.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/imageViewer.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/productLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/outletAttachmentDialog.jsp"%>
		<%@include file="/WEB-INF/views/includes/commonDialog/outletBatchCodeDialog.jsp"%>
		<%@include file="/WEB-INF/views/includes/changeListener.jsp"%>
		<%@include file="/WEB-INF/views/shared/quotationRecord/outletEdit-js.jsp"%>
		<%@include file="/WEB-INF/views/includes/commonDialog/productSpecDialog.jsp"%>
		<%@include file="/WEB-INF/views/assignmentManagement/NewRecruitmentApproval/assignmentUnitCategoryInfoDialog-js.jsp"%>
		<script src="<c:url value='/resources/js/Sortable.js'/>"></script>
		<script>
			$(function() {
				var $mainForm = $('#mainForm');
				
				var assignmentId = $('#assignmentId').val();
				var userId = $('#userId').val();
				var lockFirmStatus = $('#lockFirmStatus').val() == 'true';
				var isNormalAssignment = $('#isNormalAssignment').val() == 'true';
				var $timeLogDatePickerDialogModal = null;
				
				$(".timepicker").timepicker({
					showInputs: false,
					showMeridian: false,
					defaultTime: false,
					minuteStep: 1
		        });
				
				var $outletForm = $('#outletForm');
				$outletForm.validate();
				
				var hasPermission = false;
				<sec:authorize access="hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)">
				hasPermission = true;
				</sec:authorize>
				
				var isNewOutlet = $('#isNewOutlet').val() == 'true';
				$outletForm.outletEdit(
						{readonly: true, isNewOutlet: isNewOutlet});
				
				function setTabNameToOutlet(tabName) {
					$outletForm.find('[name="tab"]').val(tabName);
				}
				
				function setSelectedAssignmentIdToOutlet(id) {
					$outletForm.find('[name="dateSelectedAssignmentId"]').val(id);
				}
				
				function setSelectedUnitCategoryToOutlet(unitCategory) {
					$outletForm.find('[name="unitCategory"]').val(unitCategory);
				}
				
				function setFirmStatusToOutlet(firmStatus) {
					$outletForm.find('[name="firmStatus"]').val(firmStatus);
				}
				
				function setDateSelectedToOutlet(text) {
					$outletForm.find('[name="dateSelected"]').val(text);
				}
				
				function setVerificationTypeToOutlet(type) {
					$outletForm.find('[name="verificationType"]').val(type);
				}
	
				function setConsignmentCounterToOutlet(type) {
					$outletForm.find('[name="consignmentCounter"]').val(type);
				}
				
				function preSelectTabFilterLogic($tabPane) {
					var preSelectDateSelectedAssignmentId = $('#preSelectDateSelectedAssignmentId').val();
					var preSelectDateSelected = $('#preSelectDateSelected').val();
					if (preSelectDateSelectedAssignmentId != '') {
						$('[name="dateSelectedAssignmentId"]', $tabPane)
							.append('<option value="' + preSelectDateSelectedAssignmentId + '">' + preSelectDateSelected + '</option>');
						$('[name="dateSelectedAssignmentId"]', $tabPane).trigger('change');
					}
					
					var preSelectConsignmentCounter = $('#preSelectConsignmentCounter').val();
					if (preSelectConsignmentCounter != '') {
						$('[name="consignmentCounter"]', $tabPane)
							.append('<option value="' + preSelectConsignmentCounter + '">' + preSelectConsignmentCounter + '</option>');
						$('[name="consignmentCounter"]', $tabPane).trigger('change');
					}
	
					var preSelectVerificationType = $('#preSelectVerificationType').val();
					if (preSelectVerificationType != '') {
						var verificationTypeText = null;
						if (preSelectVerificationType == 1)
							verificationTypeText = 'Verify Category';
						else if (preSelectVerificationType == 2)
							verificationTypeText = 'Verify Firm';
						else if (preSelectVerificationType == 3)
							verificationTypeText = 'Verify Quotation';
						
						$('[name="verificationType"]', $tabPane)
							.append('<option value="' + preSelectVerificationType + '">' + verificationTypeText + '</option>');
						$('[name="verificationType"]', $tabPane).trigger('change');
					}
	
					var $unitCategory = $('[name="unitCategory"]', $tabPane);
					var preSelectUnitCategory = $('#preSelectUnitCategory').val();
					if (preSelectUnitCategory != '') {
						$option = $('<option></option>');
						$unitCategory.append($option);
						$option.attr('value', preSelectUnitCategory).html(preSelectUnitCategory);
						$unitCategory.trigger('change');
					}
				}
				
				function createObjectToCacheInSession($tabPane) {
					var sessionObj = {};
					
					var tabName = $tabPane.attr('id').replace('tab_', '');
					
					var $unitCategory = $('[name="unitCategory"]', $tabPane);
					
					sessionObj.unitCategory = $unitCategory.val();
					
					if (isNormalAssignment) {
						sessionObj.dateSelectedAssignmentId = $('[name="dateSelectedAssignmentId"]', $tabPane).val();
						if ($('[name="dateSelectedAssignmentId"]', $tabPane).data('select2')) {
							var dateSelectedAssignmentIdData = $('[name="dateSelectedAssignmentId"]', $tabPane).select2('data');
							sessionObj.dateSelected = null;
							if (dateSelectedAssignmentIdData != null && dateSelectedAssignmentIdData.length > 0) {
								sessionObj.dateSelected = dateSelectedAssignmentIdData[0].text;
							}
						}
					} else {
						sessionObj.dateSelectedAssignmentId = assignmentId;
					}
					
					sessionObj.consignmentCounter = $('[name="consignmentCounter"]', $tabPane).val();
					sessionObj.verificationType = $('[name="verificationType"]', $tabPane).val();
					
					sessionObj.tab = tabName;

					var order = $('.quotation-record-table', $tabPane).DataTable().order();
					sessionObj.order = [{
						column: order[0][0],
						dir: order[0][1]
					}];
					
					return sessionObj;
				}
				
				function initTabPane($tabPane) {
					if($tabPane.data('tabPane') != null) return;
					
					var tabName = $tabPane.attr('id').replace('tab_', '');
					
					var $unitCategory = $('[name="unitCategory"]', $tabPane);
					var $dataTable = $(".quotation-record-table", $tabPane);
					
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
					
					$dataTable.DataTable({
						"ordering": true, 
						"order": [[ 1, "asc" ]],
						"searching": true,
						"buttons": [],
						"processing": true,
			            "serverSide": true,
			            "ajax": {
			            	url: $dataTable.data('url'),
			            	data: function(dataModel) {
			            		if ($('[name="dateSelectedAssignmentId"]', $tabPane).length > 0)
			            			dataModel.assignmentId = $('[name="dateSelectedAssignmentId"]', $tabPane).val();
			            		else
			            			dataModel.assignmentId = assignmentId;
			            		
			            		dataModel.userId = userId;
			            		dataModel.unitCategory = $unitCategory.val();
			            		dataModel.consignmentCounter = $('[name="consignmentCounter"]', $tabPane).val();
			            		dataModel.verificationType = $('[name="verificationType"]', $tabPane).val();
			            	},
			            	method: 'post'
			            },
			            "columns": [
								 	{ "data": "id",
			                        	"render": function(data, type, row) {
			                        		return "<input type='checkbox' name='id[]' value='"+data+"' class='tblChk'/>";
			                        	}
								 	},
			                        { "data": "unitDisplayName",
			                        	"render": function(data, type, row) {
			                        		var productSpecBtn = '<button class="btn btn-info btn-xs btn-product-spec" type="button" title="Open Product Specification" data-productspecdialog-product-id="' + row.productId + '"><i class="fa fa-list"></i></button>';
			                        		return productSpecBtn + (row.flag ? '<span class="fa-stack"><i class="fa fa-square fa-stack-2x text-black"></i><i class="fa fa-star fa-stack-1x text-highlight"></i></span> ' : '') + (data != null ? data : '');
			                        	}
			                        },
			                        { "data": "nPrice" },
			                        { "data": "sPrice" },
			                        { "data": "discount" },
			                        { "data": "batchCode",
			                        	"render": function(data, type, row) {
			                        		/*if (row.assignmentType == 1) {
			                        			return data;
			                        		}*/
			                        		
			                        		var html = '<select class="select-batch" data-quotation-record-id="' + row.id + '">';
			                        		if (row.batchId != null) {
			                        			html += '<option value="' + row.batchId + '" selected>' + data + '</option>';
			                        		}
			                        		html += '</select>';
			                        		return html;
			                        	}
			                        },
			                        { "data": "id",
			                        	"render": function(data, type, row) {
			                        		var html = '<a href="#" class="btn-redirect" data-id="' + data + '"><i class="fa fa-list"></i></a>';
			                        		var backTrack = row.hasBacktrack ? '<span class="fa-stack" title="with backtrack date records"><i class="fa fa-square fa-stack-2x text-black"></i><i class="fa fa-stack-1x text-white">B</i></span>' : '';
			                        		html += backTrack;
			                        		return html;
			                        	},
			                        	"className": "text-nowrap"
			                        }
								],
			            "columnDefs": columnDefs,
			            createdRow: function (row, data, index) {
			                if (!data.passValidation) {
			                    $(row).addClass('label-danger');
			                }
			            },
			            
			            "drawCallback": function() {
			            	$('.select-batch', this).select2ajax({
			            		ajax: {
			            			url: '<c:url value='/assignmentManagement/NewRecruitmentApproval/queryBatchCodeSelect2'/>',
								    data: function (params) {
								    	params.outletId = $outletForm.find('[name="outletId"]').val();
								    	return params;
									},
									method: 'GET',
									contentType: 'application/json'
								},
								escapeMarkup: function (markup) { return markup; },
								templateResult: function(state) {
									if (state.param1 == 'true'){
										return '<span class="text-danger test">' + state.text + '</span>';
									}
									else
										return state.text;
								},
								templateSelection: function(state) {
									if (state.param1 == 'true')
										return '<span class="text-danger">' + state.text + '</span>';
									else
										return state.text;
								}
			            	});
			            	$('.btn-product-spec', this).productSpecDialog();
			            }
					});
					
					$('.filters', $tabPane).change(function() {
						$dataTable.DataTable().ajax.reload();
					});
					
					$dataTable.on('click', '.btn-redirect', function(e) {
						e.preventDefault();
						$(this).prop('disabled', true);
						
						var $btn = $(this);
						var id = $btn.data('id');
						
						var sessionObj = createObjectToCacheInSession($tabPane);
						
						$.post('<c:url value="/assignmentManagement/NewRecruitmentApproval/cacheQuotationRecordSearchFilterAndResult"/>',
							sessionObj,
							function() {
								location = '<c:url value="/assignmentManagement/NewRecruitmentApproval/editQuotationRecord"/>?quotationRecordId=' + id;
							});
					});
					
					$dataTable.on('click', '.btn-small-submit', function() {
						var $btn = $(this);
						var id = $btn.data('id');
						$btn.prop('disabled', true);
	
						var sessionObj = createObjectToCacheInSession($tabPane);
						
						$.post('<c:url value="/assignmentManagement/NewRecruitmentApproval/cacheQuotationRecordSearchFilterAndResult"/>',
							sessionObj,
							function() {
								location = '<c:url value="/assignmentManagement/NewRecruitmentApproval/smallSubmit"/>?quotationRecordId=' + id;
							});
					})
					
					$('.assignmentUnitCategoryInfoDialog', $tabPane).assignmentUnitCategoryInfoDialog({
						getContentUrl: function() {
							if (tabName == 'Normal')
								return '<c:url value='/assignmentManagement/NewRecruitmentApproval/assignmentUnitCategoryInfoDialogNormalContent'/>';
						},
						getContentQueryParam: function() {
							var param = createObjectToCacheInSession($tabPane);
							
							return param;
						}
					});
					
					$('[name="dateSelectedAssignmentId"]', $tabPane).change(function() {
						var selectedAssignmentId = $(this).val();
						setSelectedAssignmentIdToOutlet(selectedAssignmentId);
						
						if ($('[name="dateSelectedAssignmentId"]', $tabPane).data('select2')) {
							var dateSelectedAssignmentIdData = $('[name="dateSelectedAssignmentId"]', $tabPane).select2('data');
							
							if (dateSelectedAssignmentIdData != null && dateSelectedAssignmentIdData.length > 0) {
								setDateSelectedToOutlet(dateSelectedAssignmentIdData[0].text);
							}
						}
					});
					
					$unitCategory.change(function() {
						var selectedUnitCategory = $(this).val();
						setSelectedUnitCategoryToOutlet(selectedUnitCategory);
					});
					
					$('[name="verificationType"]', $tabPane).change(function() {
						setVerificationTypeToOutlet($(this).val());
					});
					
					$('[name="consignmentCounter"]', $tabPane).change(function() {
						setConsignmentCounterToOutlet($(this).val());
					});
					
					if (tabName == 'Normal') {
						if (isNormalAssignment) {
							$('[name="dateSelectedAssignmentId"]', $tabPane).select2ajax();
							$('[name="dateSelectedAssignmentId"]', $tabPane).change(function() {
								if ($(this).val() != null) {
									$('.consignment-counter-container', $tabPane).removeClass('hide');
									$('.unit-category-container', $tabPane).removeClass('hide');
									
									$('[name="consignmentCounter"]', $tabPane).select2ajax({
										ajax: {
										    data: function (params) {
										    	params.assignmentId = $('[name="dateSelectedAssignmentId"]', $tabPane).val();
										    	return params;
											},
											method: 'GET',
											contentType: 'application/json'
										}
									});
									
									$('[name="consignmentCounter"]', $tabPane).val(null).trigger('change');
									
									$('[name="unitCategory"]', $tabPane).select2ajax({
										ajax: {
										    data: function (params) {
										    	params.assignmentId = $('[name="dateSelectedAssignmentId"]', $tabPane).val();
										    	return params;
											},
											method: 'GET',
											contentType: 'application/json'
										}
									});
									
									$('[name="unitCategory"]', $tabPane).val(null).trigger('change');
									
									var dateSelectedAssignmentId = $('[name="dateSelectedAssignmentId"]', $tabPane).val();
									if (dateSelectedAssignmentId != null) {
										$.get('<c:url value='/assignmentManagement/NewRecruitmentApproval/getStatusByAssignment'/>', 
											{assignmentId : dateSelectedAssignmentId},
											function(status) {
												setFirmStatusToOutlet(status);
											}
										);
									}
								}
							});
						} else {
							$('.consignment-counter-container', $tabPane).removeClass('hide');
							$('[name="consignmentCounter"]', $tabPane).select2ajax({
								ajax: {
								    data: function (params) {
								    	params.assignmentId = assignmentId;
								    	return params;
									},
									method: 'GET',
									contentType: 'application/json'
								}
							});
							$('.unit-category-container', $tabPane).removeClass('hide');
							$('[name="unitCategory"]', $tabPane).select2ajax({
								ajax: {
								    data: function (params) {
								    	params.assignmentId = assignmentId;
								    	return params;
									},
									method: 'GET',
									contentType: 'application/json'
								}
							});
						}
					}
					
					$('.btn-clear', $tabPane).click(function() {
						if (tabName == 'Revisit' || tabName == 'Verify' || tabName == 'IP') {
							$('.filters', $tabPane).not('[name="dateSelectedAssignmentId"]').val(null).trigger('change');
						} else {
							$('.filters', $tabPane).val(null).trigger('change');
						}
					});
					
					$('.btn-set-visited', $tabPane).click(function() {
						var params = createObjectToCacheInSession($tabPane);
						normalFormPost('<c:url value="/assignmentManagement/NewRecruitmentApproval/setVisited"/>', params);
					});
					
					
					$('.btn-start-time-log', $tabPane).click(function() {
						var param = {
							assignmentId: null,
							userId: userId,
							tab: tabName,
							action: 'Start',
							date: moment().format("DD-MM-YYYY")
						};
						
						if (!isNormalAssignment) {
							param.assignmentId = assignmentId;
						} else {
							var selectedAssignmentId = $('[name="dateSelectedAssignmentId"]', $tabPane).val();
							if (selectedAssignmentId == null) return;
							param.assignmentId = selectedAssignmentId;
						}
						
						window.open('<c:url value="/timeLogManagement/TimeLogMaintenance/edit"/>?' + $.param(param));
					});
					
					$('.btn-update-time-log', $tabPane).click(function() {
						var selectedAssignmentId = null;
						if (!isNormalAssignment) {
							selectedAssignmentId = assignmentId;
						} else {
							var selectedAssignmentId = $('[name="dateSelectedAssignmentId"]', $tabPane).val();
							if (selectedAssignmentId == null) return;
							selectedAssignmentId = selectedAssignmentId;
						}
						
						if ($timeLogDatePickerDialogModal == null) {
							$timeLogDatePickerDialogModal = $('#timeLogDatePickerDialog').modal('hide');
							Datepicker($('.date-picker', $timeLogDatePickerDialogModal));
							$('.modal-submit', $timeLogDatePickerDialogModal).click(function() {
								var selectedAssignmentId = $('[name="selectedAssignmentId"]', $timeLogDatePickerDialogModal).val();
								var selectedDate = $('[name="selectedDate"]', $timeLogDatePickerDialogModal).val();
								var tabName = $('[name="tabName"]', $timeLogDatePickerDialogModal).val();
								
								var checkUrl = '<c:url value='/assignmentManagement/NewRecruitmentApproval/checkTimeLogExists'/>';
								
								$.get(checkUrl, {
									assignmentId: selectedAssignmentId,
									selectedDate: selectedDate,
									userId: userId
								}, function(result) {
									if (!result) {
										bootbox.alert({
										    title: "Alert",
										    message: "<spring:message code='E00136' />"
										});
										return;
									}
									$timeLogDatePickerDialogModal.modal('hide');
									
									var param = {
											assignmentId: selectedAssignmentId,
											userId: userId,
											tab: tabName,
											action: 'Update',
											date: selectedDate
										};
									
									window.open('<c:url value="/timeLogManagement/TimeLogMaintenance/edit"/>?' + $.param(param));
								});
							});
						}
						$('.date-picker', $timeLogDatePickerDialogModal).datepicker('setDate', new Date());
						$('[name="selectedAssignmentId"]', $timeLogDatePickerDialogModal).val(selectedAssignmentId);
						$('[name="tabName"]', $timeLogDatePickerDialogModal).val(tabName);
						
						$timeLogDatePickerDialogModal.modal('show');
					});
					
					$('.btn-approve', $tabPane).click(function() {
						if ($('[name="id[]"]:checked').length == 0) return;
						
						$.ajax({
							type: 'POST',
							url: '<c:url value="/assignmentManagement/NewRecruitmentApproval/validateBatch"/>',
							data: {
								outletId: $outletForm.find('[name="outletId"]').val(),
								batchIds : function() {
									var batchIds = [];
									$('[name="id[]"]:checked').map(function(){
										var $input = $(this).closest('tr').find('.select-batch');
										batchIds.push($input.val());
									});
									return batchIds;
								}
							},
							async:false,
							success: function(result){
								if (result == true) {
									bootbox.confirm("<spring:message code='W00042'/>", function(ret) {
										  if (ret){
											  saveBatch();
											  submitForm();
										  }
									});
								} else {
									submitForm();
								}
							}
						});
						
						
						
						
					});
					
					function saveBatch(){
						$('[name="id[]"]:checked').map(function(){
							var $input = $(this).closest('tr').find('.select-batch');
							$quotationRecordId = $input.data('quotationRecordId');
							$batchId = $input.val();
		            		$.post('<c:url value='/assignmentManagement/NewRecruitmentApproval/saveBatch'/>',
	            			{
	            				quotationRecordId: $quotationRecordId,
	            				batchId: $batchId
	            			});
		            	});
					}
					
					function submitForm(){
						Modals.startLoading();
						$('[name="approveRejectBtn"]').val("approve");
						$('#frmSubmit').submit(); 
					}
					
					$('.btn-reject', $tabPane).click(function() {
						if ($('[name="id[]"]:checked').length == 0) return;
						$('[name="approveRejectBtn"]').val("reject");
						$('#rejectReasonDialog').modal('show');
					});
					
					var preSelectTab = $('#preSelectTab').val();
					if (preSelectTab == tabName) {
						preSelectTabFilterLogic($tabPane);
					}
					
					$tabPane.data('tabPane', true);
				}
				
				$('.nav-tabs-custom').on('shown.bs.tab', function() {
					var $tabPane = $('.tab-pane.active',this);
					var tabName = $tabPane.attr('id').replace('tab_', '');
					initTabPane($tabPane);
					
					if (tabName == 'Normal') {
						$outletForm.outletEdit('disableCollectionMethod', false);
						if (lockFirmStatus)
							$outletForm.outletEdit('disableFirmStatus', true);
						else
							$outletForm.outletEdit('disableFirmStatus', false);
	
						var dateSelectedAssignmentId = null;
						if (isNormalAssignment)
							dateSelectedAssignmentId = $('[name="dateSelectedAssignmentId"]', $tabPane).val();
						else
							dateSelectedAssignmentId = assignmentId;
						
						if (dateSelectedAssignmentId != null) {
							$.get('<c:url value='/assignmentManagement/NewRecruitmentApproval/getStatusByAssignment'/>', 
								{assignmentId : dateSelectedAssignmentId},
								function(status) {
									setFirmStatusToOutlet(status);
								}
							);
						}
					}
					
					setTabNameToOutlet(tabName);
					
					if ($('[name="dateSelectedAssignmentId"]', $tabPane).length > 0)
						setSelectedAssignmentIdToOutlet($('[name="dateSelectedAssignmentId"]', $tabPane).val());
					else
						setSelectedAssignmentIdToOutlet(null);
					
					if ($('[name="dateSelectedAssignmentId"]', $tabPane).data('select2')) {
						var dateSelectedAssignmentIdData = $('[name="dateSelectedAssignmentId"]', $tabPane).select2('data');
						
						if (dateSelectedAssignmentIdData != null && dateSelectedAssignmentIdData.length > 0) {
							setDateSelectedToOutlet(dateSelectedAssignmentIdData[0].text);
						}
					}
					
					setSelectedUnitCategoryToOutlet($('[name="unitCategory"]', $tabPane).val());
					
					setVerificationTypeToOutlet($('[name="verificationType"]', $tabPane).val());
					setConsignmentCounterToOutlet($('[name="consignmentCounter"]', $tabPane).val());
				});
				
				var preSelectTab = $('#preSelectTab').val();
				if (preSelectTab == '') {
					$('[href^="#tab_"]').last().click();
				} else {
					$('[href="#tab_' + preSelectTab + '"]').click();
				}
				
				$('.btn-header-collapse').click(function() {
					var $btn = $(this);
					var $box = $btn.closest('.box-tools');
					if ($btn.find('.fa-plus').length > 0)
						$box.find('.header-mark').hide();
					else
						$box.find('.header-mark').show();
				});
				
				$('#rejectReasonDialog form').validate();
				
				$('#rejectReasonDialog form').submit(function() {
					if (!$(this).valid()) return false;
					var rejectReason = $('#rejectReasonDialog [name="rejectReason"]').val();
					$('#frmSubmit [name="rejectReason"]').val(rejectReason);
					$('#frmSubmit').submit();
					return false;
				});
				
				<sec:authorize access="!(hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256))">
					viewOnly();
				</sec:authorize>
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<input id="isNormalAssignment" type="hidden" value="<c:out value="${model.normalAssignment}"/>"/>
		<input id="assignmentId" type="hidden" value="<c:out value="${model.assignmentId}"/>"/>
		<input id="userId" type="hidden" value="<c:out value="${model.userId}"/>"/>
		<input id="lockFirmStatus" type="hidden" value="<c:out value="${model.lockFirmStatus}"/>"/>
		<input id="normalAssignmentStatus" type="hidden" value="<c:out value="${model.normalAssignmentStatus}"/>"/>
		<input id="preSelectTab" type="hidden" value="<c:out value="${model.preSelectTab}"/>"/>
		<input id="preSelectUnitCategory" type="hidden" value="<c:out value="${model.preSelectUnitCategory}"/>"/>
		<input id="preSelectDateSelectedAssignmentId" type="hidden" value="<c:out value="${model.preSelectDateSelectedAssignmentId}"/>"/>
		<input id="preSelectDateSelected" type="hidden" value="<c:out value="${model.preSelectDateSelected}"/>"/>
		<input id="preSelectConsignmentCounter" type="hidden" value="<c:out value="${model.preSelectConsignmentCounter}"/>"/>
		<input id="preSelectVerificationType" type="hidden" value="<c:out value="${model.preSelectVerificationType}"/>"/>
		<input id="isNewOutlet" type="hidden" value="<c:out value="${model.newOutlet}"/>"/>
		<section class="content-header">
          <h1>New Recruitment Approval</h1>
			<div class="breadcrumb form-horizontal" style="width:240px">
				<div class="form-group" style="margin-bottom:0px">
		        	<div class="col-sm-5">Created Date:</div>
		        	<div class="col-sm-7">${commonService.formatDateTime(model.createdDate)}</div>
		        </div>
		        <div class="form-group" style="margin-bottom:0px">
		         	<div class="col-sm-5">Last Modified:</div>
		         	<div class="col-sm-7">${commonService.formatDateTime(model.modifiedDate)}</div>
		         </div>
	      	</div>
        </section>
        
        <section class="content">
        	<div class="row">
        		<div class="col-md-12">
        			<!-- general form elements -->
        			<div class="box box-primary ${empty model.pointToNote ? "collapsed-box" : ""}">
        				<div class="box-header with-border">
							<a class="btn btn-default" href="<c:url value='/assignmentManagement/NewRecruitmentApproval/home'/>">Back</a>
							<c:choose>
							<c:when test="${empty model.pointToNote}">
								<div class="box-tools pull-right">
									<i class="fa fa-exclamation-triangle header-mark"></i>
	        						<button class="btn btn-box-tool btn-header-collapse" type="button" data-widget="collapse"><i class="fa fa-plus"></i></button>
	        					</div>
        					</c:when>
        					<c:otherwise>
								<div class="box-tools pull-right">
									<i class="fa fa-exclamation-triangle header-mark" style="display:none""></i>
	        						<button class="btn btn-box-tool btn-header-collapse" type="button" data-widget="collapse"><i class="fa fa-minus"></i></button>
	        					</div>
        					</c:otherwise>
        					</c:choose>
						</div>
						<div class="box-body">
							<div class="form-horizontal">
			        			<div class="form-group" style="margin-bottom: 0px;">
									<label class="col-md-2 control-label">Survey Month:</label>
									<div class="col-md-2">
										<p class="form-control-static">${model.surveyMonth}</p>
									</div>
									<c:if test="${not empty model.pointToNote}">
										<label class="col-md-2 control-label text-red">Point to note:</label>
										<div class="col-md-2">
											<p class="form-control-static text-red">${model.pointToNote}</p>
										</div>
									</c:if>
									<label class="col-md-2 control-label">Person in charge:</label>
									<div class="col-md-2">
										<p class="form-control-static">${model.personInCharge}</p>
									</div>
								</div>
							</div>
						</div>
					</div>
					
					<c:set var="outlet" value="${model.outlet}" scope="request" />
					<form id="outletForm" action="" method="post" role="form" enctype="multipart/form-data">
						<input name="tab" type="hidden" value="Normal" />
						<input name="dateSelectedAssignmentId" type="hidden" value="" />
						<input name="dateSelected" type="hidden" value="" />
						<input name="unitCategory" type="hidden" value="" />
						<input name="consignmentCounter" type="hidden" value="" />
						<input name="verificationType" type="hidden" value="" />
						<%@include file="/WEB-INF/views/shared/quotationRecord/outletEdit.jsp"%>
					</form>
					
					
					<div class="nav-tabs-custom">
						<ul class="nav nav-tabs pull-right">
							<li class="" style="display:none"><a href="#tab_Normal" data-toggle="tab">Normal</a></li>
							<li class="pull-left header">Quotation Records</li>
						</ul>
						<div class="tab-content" style="padding: 10px 0 0 0">
							<div id="tab_Normal" class="tab-pane">
								<%@include file="/WEB-INF/views/assignmentManagement/NewRecruitmentApproval/editQuotationRecordListNormal.jsp"%>
							</div>
							<!-- /.tab-pane -->
						</div>
						<!-- /.tab-content -->
					</div>
        		</div>
        	</div>
        </section>
        
        <%@include file="/WEB-INF/views/assignmentManagement/shared/rejectReasonDialog.jsp"%>
	</jsp:body>
</t:layout>

