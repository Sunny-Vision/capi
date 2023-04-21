<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page session="false" %>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/jstree-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/unitLookup-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp"%>
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
		<%@include file="/WEB-INF/views/includes/moment.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker.jsp"%>
		<%@include file="/WEB-INF/views/includes/commonDialog/userLookup.jsp" %>
		<script>
		$(document).ready(function(){
			var $dataTable = $("#dataList");
			
			$("#rejectForm").validate({ ignore: [] });
			$("#rejectForm").submit( function(e){
				if(!$("#rejectForm").valid() )return;
				var data = $dataTable.find(':checked').serialize();
				var form = $(this).serialize();
				data = data+"&"+form+"&approval=0";
				bootbox.confirm({
				    title: "Confirmation",
				    message: "<spring:message code='W00035' />",
				    callback: function(result){
				    	if(result === true){
				    		//console.log(data);
				    		$.post("<c:url value='/QuotationRecordVerificationApproval/approvalSelected'/>",
									data,
									function(response) {
				    					$('#rejectDialog').modal('hide');
										$("#dataList").DataTable().ajax.reload(null,false);
										$("#MessageRibbon").html(response);
									}
								);
				    	}
				    }
				});
				
				return false;
			});
			
			//$(".date-picker:not([readonly])").datepicker();
			
			$('.searchUserId').userLookup({
				selectedIdsCallback: function(selectedIds, singleRowData) {
					var id = selectedIds[0];
					this.$element.find('input[name="userDisplay"]').val(singleRowData.staffCode
							+ " - " + singleRowData.englishName);
					this.$element.find('input[name="indoorUserId"]').val(id);
					$dataTable.DataTable().ajax.reload();
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
			
			var buttons = [{
				"text": "Approve Selected",
				"action": function( nButton, oConfig, flash ) {
					var data = $dataTable.find(':checked').serialize();
					if (data == '' ) {
						bootbox.alert({
							title: "Alert",
							message: "<spring:message code='E00009' />"
						});
						//alert('<spring:message code="E00009" />');
						return;
					}
					data = data+"&approval=1";
					bootbox.confirm({
					    title: "Confirmation",
					    message: "<spring:message code='W00033' />",
					    callback: function(result){
					    	if(result === true){
								$.post("<c:url value='/QuotationRecordVerificationApproval/approvalSelected'/>",
										data,
										function(response) {
											$("#dataList").DataTable().ajax.reload(null,false);
											$("#MessageRibbon").html(response);
										}
									);
					    	}
					    }
					})
				}
			},{
				"text": "Approve All",
				"action": function( nButton, oConfig, flash ) {
					data = "approval=1";
					bootbox.confirm({
					    title: "Confirmation",
					    message: "<spring:message code='W00034' />",
					    callback: function(result){
					    	if(result === true){
								$.post("<c:url value='/QuotationRecordVerificationApproval/approvalSelected'/>",
										data,
										function(response) {
											$("#dataList").DataTable().ajax.reload(null,false);
											$("#MessageRibbon").html(response);
										}
									);
					    	}
					    }
					})
				}
			},{
				"text": "Reject Selected",
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
					
					$('#rejectDialog').modal('show');
				}
			},{
				"text": "Reject All",
				"action": function( nButton, oConfig, flash ) {
					$('#rejectDialog').modal('show');
				}
			},
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
                	url: "<c:url value='/QuotationRecordVerificationApproval/query'/>",
                	data: function(d) {
                		d.search["subGroupId"] = $('[name="subGroupId"]').val();
                		d.search["outletId"] = $('[name="outletId"]').val();
                		d.search["unitId"] = $('[name="unitId"]').val() == null ? "" : $('[name="unitId"]').val().join();
                		d.search["purposeId"] = $('[name="purposeId"]').val();
                		d.search["indoorUserId"] = $('[name="indoorUserId"]').val();
                		d.search["referenceMonthStr"] = $('[name="referenceMonthStr"]').val();
                		d.search["isVerify"] = $('[name="isVerify"]').val();
                	},
                	method: 'post'
                },
                "columns": [
                            { "data": "indoorQuotationRecordId",
			                	"render" : function(data, type, full, meta){
			                		var html = '<input name="id" value="'+data+'" type="checkbox" class="tblChk"]>';
			                		return html;
			                		}
			                },
			                { "data": "referenceDate" },
			                { "data": "referenceMonth" },
			                { "data": "firmName" },
			                { "data": "unitCode" },
                            { "data": "unitEnglishName" },
                            { "data": "unitChineseName" },
                            { "data": "subGroupEnglishName" },
                            { "data": "subGroupChineseName" },
                            { "data": "fieldOfficerName" },
                            { "data": "indoorOfficerName" },
                            { "data": "firmVerifyRemark" },
                            { "data": "categoryVerifyRemark" },
                            { "data": "quotationVerifyRemark" },
                            { "data": "indoorQuotationRecordId",
                            	"render" : function(data, type, full, meta){
                            		var html = "<a href='<c:url value='/QuotationRecordVerificationApproval/edit?id='/>"+data+"' class='table-btn'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a>";
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
			
			Datepicker($(".date-picker:not([readonly])"));
			
			/*
			$("#btnReset").click(function(){
				$dataTable.DataTable().ajax.reload();
			})
			*/
			
			$('button[type="button"]').on('click', function() {
				console.log('Reset Button clicked.');
				
				$('.criteria').select2ajax('destroy');
				
				$('[name="isVerify"]').val("");
				$('.criteria').find("option").remove();
				$('[name="unitId"]').find("option").remove();
				$('[name="referenceMonthStr"]').val("");
				$('[name="indoorUserId"]').val("");
				$('[name="userDisplay"]').val("");
				
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
			
		})
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Quotation Record Verification Approval</h1>
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
									<label class="col-md-1 control-label">Variety</label>
									<div class="col-md-2">
	   									<div class="input-group">
	   										<select name="unitId" class="form-control filters" id="unitId" multiple
	   										data-close-on-select="false"
	   										data-allow-clear="true"
											data-placeholder=""
											data-ajax-url="<c:url value='/QuotationRecordVerificationApproval/queryUnitSelect2'/>"
											data-single-url="<c:url value='/QuotationRecordVerificationApproval/queryUnitSelectSingle'/>"/></select>	
											<div id="unitLookup" class="input-group-addon searchUnitId">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
									<label class="col-md-1 control-label">Firm</label>
									<div class="col-md-2">
										<select name="outletId" class="form-control select2ajax filters criteria"
											data-allow-clear="true"
											data-placeholder=""
											data-ajax-url="<c:url value='/QuotationRecordVerificationApproval/queryOutletSelect2'/>"></select>
									</div>
									<label class="col-md-1 control-label">Sub Group</label>
									<div class="col-md-2">
										<select name="subGroupId" class="form-control select2ajax filters criteria" id="subGroupId"
											data-ajax-url="<c:url value='/QuotationRecordVerificationApproval/querySubGroupSelect2'/>" /></select>
									</div>
									<label class="col-md-1 control-label">Purpose</label>
									<div class="col-md-2">
										<select name="purposeId" class="form-control select2ajax filters criteria" id="purposeId"
											data-ajax-url="<c:url value='/QuotationRecordVerificationApproval/queryPurposeSelect2'/>"></select>								
									</div>
								</div>
								<div class="row" style="margin-top: 10px;">
									<label class="col-sm-1 control-label">Indoor Staff Code</label>
									<div class="col-sm-2">
										<div class="input-group searchUserId">
											<input type="hidden" name="indoorUserId" class="form-control" readonly>
											<input type="text" name="userDisplay" class="form-control" readonly>
											<div class="input-group-addon">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
									<label class="col-sm-1 control-label">Reference Month</label>
									<div class="col-sm-2">
										<div class="input-group ">
											<input type="text" class="filters form-control date-picker" data-orientation="top" data-date-minviewmode="months" data-date-format="mm-yyyy"
												name="referenceMonthStr" required value="" />
											<div class="input-group-addon">
												<i class="fa fa-calendar"></i>
											</div>
										</div>
									</div>
									<label class="col-sm-1 control-label">Verify</label>
									<div class="col-sm-2">
										<div class="input-group ">
											<select class="form-control filters" name="isVerify" id="isVerify">
												<option value=""></option>
												<option value="1">Firm Verify</option>
												<option value="2">Category Verify</option>
												<option value="3">Quotation Verify</option>
											</select>
										</div>
									</div>
								</div>
								<div class="row" style="margin-top: 10px;">
									<div class="col-md-offset-1 col-md-2">
										<button type="button" class="btn btn-info" id="btnReset">Clear</button>
									</div>
									<div class="col-md-offset-7 col-md-2">
										
									</div>
								</div>
							</form>
							<input type="hidden" name="referenceMonthStr" value="${referenceMonthStr}" readonly disabled/>
							<input type="hidden" name="purposeId" value="${purpose.purposeId}" readonly disabled/>
							<hr/>
							<table class="table table-striped table-bordered table-hover responsive" id="dataList">
								<thead>
								<tr>
									<th class="text-center action" data-priority="1"></th>
									<th>Reference Date</th>
									<th>Reference Month</th>
									<th>Firm</th>
<%-- 									<th>Unit Code</th> --%>
									<th>Variety Code</th>
									<th>Variety English Name</th>
									<th>Variety Chinese Name</th>
									<th>Sub-Group English Name</th>
									<th>Sub-Group Chinese Name</th>
									<th>Field Officer</th>
									<th>Indoor Officer</th>
									<th>Firm Verify Remark</th>
									<th>Category Verify Remark</th>
									<th>Quotation Verify Remark</th>
									<th class="text-center action" data-priority="1"></th>
								</tr>
								</thead>
							</table>
						</div>
					</div>
				</div>
			</div>
			<div class="modal fade" id="rejectDialog" tabindex="-1" role="dialog" aria-labelledby="dialogLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content">
						<div  class="clearfix overlay" data-bind="loading:isLoading, visible:isLoading" ></div>
						<form action="<c:url value='/QuotationRecordVerificationApproval/approval'/>" method="post" id="rejectForm" >
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
								<h4 class="modal-title" id="dialogLabel" data-bind="text:formTitle">Reject</h4>
							</div>
							<div class="modal-body form-horizontal">
								<div class="form-group">
									<div class="col-md-4 control-label">Reason</div>
									<div class="col-md-6">
										<input name="reason" class="form-control" required/>
									</div>
								</div>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
								<button type="submit" name="btnSubmit" value="reject" class="btn btn-primary" data-loading-text="Loading...">Submit</button>
							</div>
						</form>
					</div>
				</div>
			</div>
        </section>
	</jsp:body>
</t:layout>

		