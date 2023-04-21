<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<jsp:useBean id="niceDate" class="java.util.Date"/>
<t:layout plainLayout="true">
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/timepicker-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
	</jsp:attribute>
	<jsp:attribute name="header">
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
		<%@include file="/WEB-INF/views/includes/changeListener.jsp"%>
		<%@include file="/WEB-INF/views/includes/commonDialog/productSpecDialog.jsp"%>
		<%@include file="/WEB-INF/views/shared/quotationRecord/outletEdit-js.jsp"%>
		<%@include file="/WEB-INF/views/assignmentManagement/PEViewAssignmentMaintenance/assignmentUnitCategoryInfoDialog-js.jsp"%>
		<script src="<c:url value='/resources/js/Sortable.js'/>"></script>
		<script>
			$(function() {
				var $mainForm = $('#mainForm');
				
				var assignmentId = $('#assignmentId').val();
				var userId = $('#userId').val();
				var lockFirmStatus = $('#lockFirmStatus').val() == 'true';
				
				$(".timepicker").timepicker({
					showInputs: false,
					showMeridian: false,
					defaultTime: false,
					minuteStep: 1
		        });
				
				var $outletForm = $('#outletForm');
				$outletForm.validate();
				
				var hasPermission = false;
				<sec:authorize access="hasPermission(#user, 16)">
				hasPermission = true;
				</sec:authorize>
				
				$outletForm.outletEdit(
						{readonly: true, isNewRecruitment: false});
				
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
					
					sessionObj.dateSelectedAssignmentId = $('[name="dateSelectedAssignmentId"]', $tabPane).val();
					var dateSelectedAssignmentIdData = null;
					if ($('[name="dateSelectedAssignmentId"]', $tabPane).length > 0) {
						dateSelectedAssignmentIdData = $('[name="dateSelectedAssignmentId"]', $tabPane).select2('data');
					}
					sessionObj.dateSelected = null;
					if (dateSelectedAssignmentIdData != null && dateSelectedAssignmentIdData.length > 0) {
						sessionObj.dateSelected = dateSelectedAssignmentIdData[0].text;
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
						"order": [[ 0, "asc" ]],
						"searching": true,
						"buttons": [],
						"processing": true,
			            "serverSide": true,
			            "ajax": {
			            	url: $dataTable.data('url'),
			            	data: function(dataModel) {
			            		dataModel.assignmentId = assignmentId;
			            		dataModel.userId = userId;
			            		dataModel.unitCategory = $unitCategory.val();
			            		dataModel.dateSelectedAssignmentId = $('[name="dateSelectedAssignmentId"]', $tabPane).val();
			            		dataModel.consignmentCounter = $('[name="consignmentCounter"]', $tabPane).val();
			            		dataModel.verificationType = $('[name="verificationType"]', $tabPane).val();
			            	},
			            	method: 'post'
			            },
			            "columns": [
			                        { "data": "unitDisplayName",
			                        	"render": function(data, type, row) {
			                        		var productSpecBtn = '<button class="btn btn-info btn-xs btn-product-spec" type="button" title="Open Product Specification" data-productspecdialog-product-id="' + row.productId + '"><i class="fa fa-list"></i></button>';
			                        		return productSpecBtn + (row.flag ? '<span class="fa-stack"><i class="fa fa-square fa-stack-2x text-black"></i><i class="fa fa-star fa-stack-1x text-highlight"></i></span> ' : '') + (data != null ? data : '');
			                        	}
			                        },
			                        { "data": "nPrice" },
			                        { "data": "sPrice" },
			                        { "data": "discount" },
			                        { "data": "status",
			                        	"render": function(data, type, row) {
			                        		return data + (row.passValidation ? '' : ' <i class="fa fa-cog" title="not pass validation"></i>');
			                        	}
			                        },
			                        { "data": "availability",
			                        	"render": function(data, type, row) {
			                        		var availability = 0;
			                        		if (data == 3){
			                        			availability = (data == 3 && (row.nPrice == null || row.nPrice == "") && (row.sPrice == null || row.sPrice == "") && !row.isSPricePeculiar) ? 4 : 1
			                        		}
			                        		return availabilityDisplay((availability != 0) ? availability : data);
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
			            	if (!data.passValidation && data.status != 'Blank') {
			                    $(row).addClass('label-danger');
			                }
			            },
			            drawCallback: function() {
			            	$('.btn-product-spec', this).productSpecDialog();
			            }
					});
					
					$('.filters', $tabPane).change(function() {
						$dataTable.DataTable().ajax.reload();
					})
					
					$dataTable.on('click', '.btn-redirect', function(e) {
						e.preventDefault();
						$(this).prop('disabled', true);
						
						var $btn = $(this);
						var id = $btn.data('id');
						
						var sessionObj = createObjectToCacheInSession($tabPane);
						
						$.post('<c:url value="/assignmentManagement/PEViewAssignmentMaintenance/cacheQuotationRecordSearchFilterAndResult"/>',
							sessionObj,
							function() {
								location = '<c:url value="/assignmentManagement/PEViewAssignmentMaintenance/editQuotationRecord"/>?quotationRecordId=' + id;
							});
					});
					
					$('.assignmentUnitCategoryInfoDialog', $tabPane).assignmentUnitCategoryInfoDialog({
						getContentUrl: function() {
							if (tabName == 'Normal')
								return '<c:url value='/assignmentManagement/PEViewAssignmentMaintenance/assignmentUnitCategoryInfoDialogNormalContent'/>';
						},
						getContentQueryParam: function() {
							var param = createObjectToCacheInSession($tabPane);
							
							return param;
						}
					});
					
					$('[name="dateSelectedAssignmentId"]', $tabPane).change(function() {
						var selectedAssignmentId = $(this).val();
						setSelectedAssignmentIdToOutlet(selectedAssignmentId);
						
						var dateSelectedAssignmentIdData = null;
						if ($('[name="dateSelectedAssignmentId"]', $tabPane).length > 0) {
							dateSelectedAssignmentIdData = $('[name="dateSelectedAssignmentId"]', $tabPane).select2('data');
						}
						if (dateSelectedAssignmentIdData != null && dateSelectedAssignmentIdData.length > 0) {
							setDateSelectedToOutlet(dateSelectedAssignmentIdData[0].text);
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
					
					$('.select2ajax', $tabPane).select2ajax();
					
					$('.btn-clear', $tabPane).click(function() {
						$('.filters', $tabPane).val(null).trigger('change');
					});
					
					$('.btn-set-visited', $tabPane).click(function() {
						var params = createObjectToCacheInSession($tabPane);
						normalFormPost('<c:url value="/assignmentManagement/PEViewAssignmentMaintenance/setVisited"/>', params);
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
						
						setFirmStatusToOutlet($('#normalAssignmentStatus').val());
					}
					
					setTabNameToOutlet(tabName);
					
					if ($('[name="dateSelectedAssignmentId"]', $tabPane).length > 0)
						setSelectedAssignmentIdToOutlet($('[name="dateSelectedAssignmentId"]', $tabPane).val());
					else
						setSelectedAssignmentIdToOutlet(null);
					
					setSelectedUnitCategoryToOutlet($('[name="unitCategory"]', $tabPane).val());
				});
				var $firstTabPane = $('.tab-pane.active');
				initTabPane($firstTabPane);
				$('.nav-tabs-custom').trigger('shown.bs.tab');
				
				var preSelectTab = $('#preSelectTab').val();
				$('[href="#tab_' + preSelectTab + '"]').click();
				
				$('.btn-header-collapse').click(function() {
					var $btn = $(this);
					var $box = $btn.closest('.box-tools');
					if ($btn.find('.fa-plus').length > 0)
						$box.find('.header-mark').hide();
					else
						$box.find('.header-mark').show();
				});
				
				$('#btnBigSubmit').click(function() {
					var checkUrl = '<c:url value='/assignmentManagement/PEViewAssignmentMaintenance/checkAllPassValidation'/>';
					var messageE00101 = '<spring:message code='E00101' javaScriptEscape='true' />';
					
					$.post(checkUrl, {
						assignmentId: assignmentId,
						userId: userId
					}, function(result) {
						if (result) {
							$('#frmBigSubmit').submit();
						} else {
							bootbox.confirm({
								title:"Confirmation",
								message: messageE00101,
								callback: function(result){
									if (result){
										$('#frmBigSubmit').submit();
									}
								}
							});
						}
					});
					
					return false;
				});
			});
		</script>
	</jsp:attribute>
	<jsp:body>
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
		<section class="content-header">
          <h1>View Assignment</h1>
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
        					<h3 class="box-title" style="visibility: hidden">title</h3>
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
					<form id="outletForm">
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
							<li class="pull-left header">Quotation Records</li>
						</ul>
						<div class="tab-content" style="padding: 10px 0 0 0">
							<div id="tab_Normal" class="tab-pane active">
								<%@include file="/WEB-INF/views/assignmentManagement/PEViewAssignmentMaintenance/editQuotationRecordListNormal.jsp"%>
							</div>
							<!-- /.tab-pane -->
						</div>
						<!-- /.tab-content -->
					</div>
        		</div>
        	</div>
        </section>
	</jsp:body>
</t:layout>

