<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp"%>
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
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker.jsp"%>
		<%@include file="/WEB-INF/views/includes/jstree.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/unitLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/userLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/batchLookup.jsp" %>
		<script>
			$(document).ready(function(){
				var $dataTable = $("#dataList");
				
				$dataTable.DataTable({
					"order": [[ 0, "desc" ]],
					"searching": true,
					"ordering": true,
					"buttons": [],
					"processing": true,
	                "serverSide": true,
	                "ajax": "<c:url value='/report/QuotationRecordProgress/query'/>",
	                "columns": [
								
	                            {
	                            	"data": "createdDate",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "description",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "createdBy",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data":"exceptionMessage",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "status",
	                            	"orderable": false,
	                            	"searchable": true,
	                            	"className" : "text-center",
	                            	"render" : function(data, type, full, meta){
	                            		if (data == "Finished"){
	                            			return "<a href='<c:url value='/report/QuotationRecordProgress/downloadFile?id='/>"+full.reportTaskId+"' class='table-btn'><span class='glyphicon glyphicon-download-alt' aria-hidden='true'></span></a>";
	                            		}
	                            		else{
	                            			return data;
	                            		}
                            		}
	                            }
	                        ]
				});
				
				$(".select2").select2({
					placeholder: "",
					allowClear: true,
					closeOnSelect: false
				})
				$('.select2ajax').select2ajax({
					placeholder: "",
					allowClear: true,
					closeOnSelect: false
				});
				$('.searchBatchCode').batchLookup({
					selectedIdsCallback: function(selectedIds) {
						var singleUrl = $('#batchCodeIds').data('singleUrl');
						
						$.ajax({
							url:singleUrl,
							data: {ids:selectedIds},
							type: 'post',
							dataType: 'json',
							traditional: true,
							success: function (data){
								$('#batchCodeIds').empty();
								
								for (i = 0; i < data.length; i++){
									var option = new Option(data[i].value, data[i].key);
									option.selected = true;
									$('#batchCodeIds').append(option);
								}

								$('#batchCodeIds').trigger('change');
							}
							
						})
						
					},
					alreadySelectedIdsCallback: function(){
						var ids = [];
						$('#batchCodeIds').find('option:selected').each(function(){
							ids.push(this.value);
						})
						return ids;
					},
					multiple: true
				});
				$('#batchCodeIds').hide()
				
				$('.date-picker').datepicker({
					format: monthFormat,
					startView: "months", 
				    minViewMode: "months",
				    autoclose: true
				});
				
				$('.lookup').each(function() {
					var bottomEntityClass = $(this).data('bottomEntityClass');
					var $select = $(this).closest('.input-group').find('select');
					var getKeyValueByIdsUrl = $select.data('getKeyValueByIdsUrl');
					$(this).unitLookup({
						selectedIdsCallback: function(selectedIds) {
							$select.empty();
							
							if(selectedIds.length == 0) {
								$("#unitLookup.input-group-addon:last-child").css("color", "#555");
								$("#unitLookup.input-group-addon:last-child").css("background-color", "");
								$("#unitLookup.input-group-addon:last-child").css("border-color", "#d2d6de");
								return;
							}
							
							$.post(getKeyValueByIdsUrl, { id: selectedIds }, function(data) {
								for (var i = 0; i < data.length; i++) {
									var option = new Option(data[i].value, data[i].key);
									option.selected = true;
									$select.append(option);
								}
								$select.trigger('change');
							});
							
							$("#unitLookup.input-group-addon:last-child").css("color", "#ffffff");
							$("#unitLookup.input-group-addon:last-child").css("background-color", "#f0ad4e");
							$("#unitLookup.input-group-addon:last-child").css("border-color", "#eea236");
						},
						multiple: true,
						bottomEntityClass: bottomEntityClass,
						queryDataCallback: function(data) {
							data.purposeIds = $('[name="purpose"]').val();
						}
					});
					$select.hide();
				});
				
				/*
				$('[name="unitId"]').select2ajax({
					ajax: {
					    data: function (term) {
					    	return $.extend(term, {purposeIds: $('[name="purpose"]').val()});
						},
						method: 'GET',
						contentType: 'application/json'
					}
				});
				*/
				
				$('[name="purpose"]').change(function() {
					$('[name="unitId"]').val(null).trigger('change');
				});
				
				$("#pdfBtn").click(function(){
					$("#taskType").val(1)
				});
				$("#excelBtn").click(function(){
					$("#taskType").val(2)
				});
				$("#criteriaForm").validate();
				
				$("select[name='batch']").select2ajax({
					placeholder: "",
					allowClear: true,
					closeOnSelect: false,
					ajax: {
						url: "<c:url value='/report/QuotationRecordProgress/queryBatchSelect2'/>",
						traditional: true,
						data: function(params) {
							return $.extend(params, {
								cpiQuotationTypes : $("select[name='cpiSurveyForm']").val()
							})
							/*
							return {
								cpiQuotationTypes : $("select[name='cpiSurveyForm']").val()
							};*/
						}
					}
				});
				
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Quotation Record Progress report</h1>
        </section>
        
        <section class="content">
        	<div class="row">
				<div class="col-md-12" >
				<!-- content -->
					<div class="box" >
						<div class="box-body">
							<div class="clearfix">&nbsp;</div>
							<div id="filters">
								<form class="form-horizontal" action="<c:url value="/report/QuotationRecordProgress/submitReportRequest" />" id="criteriaForm" method="post">
									
										<div class="form-group">
											<label class="col-md-2 control-label">Purpose:</label>
											<div class="col-md-5">
												<select name="purpose" class="form-control select2ajax filters" multiple
													data-ajax-url="<c:url value='/report/QuotationRecordProgress/queryPurposeSelect2'/>"></select>
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-2 control-label">Classification:</label>
											<div class="col-md-5">
												<div class="input-group">
													<select name="unitId" class="form-control filters"
														data-allow-clear="true"
														data-placeholder=""
														data-ajax-url="<c:url value='/report/QuotationRecordProgress/queryUnitSelect2'/>"
														data-get-key-value-by-ids-url="<c:url value='/report/QuotationRecordProgress/getKeyValueByIds'/>"
														data-close-on-select="false"
														multiple></select>
													<div id="unitLookup" class="input-group-addon lookup"
														data-bottom-entity-class="Unit">
														<i class="fa fa-search"></i>
													</div>
												</div>
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-2 control-label">CPI Survey Form:</label>
											<div class="col-md-5">
												<select name="cpiSurveyForm" class="form-control select2 filters" multiple>
													<option value="1">Market</option>
													<option value="2">Supermarket</option>
													<option value="3">Batch</option>
													<option value="4">Others</option>
												</select>
											</div>
										</div>										
										<div class="form-group">
											<label class="col-md-2 control-label">Batch Code:</label>
											<div class="col-md-5">
												<div class="input-group">
													<select name="batch" class="form-control filters" multiple id="batchCodeIds"
														data-single-url="<c:url value="/report/QuotationRecordProgress/getBatchCodes" />"
														<%--data-ajax-url="<c:url value='/report/QuotationRecordProgress/queryBatchSelect2'/>"--%>></select>
													<div class="input-group-addon searchBatchCode">
														<i class="fa fa-search"></i>
													</div>
												</div>
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-2 control-label">Period:</label>
											<div class="col-md-2">
												<div class="input-group">
													<input type="text" class="form-control date-picker" data-orientation="top" name="startMonth" required  />
													<div class="input-group-addon">
														<i class="fa fa-calendar"></i>
													</div>
												</div>
											</div>
											<div class="col-md-2">
												<div class="input-group">
													<input type="text" class="form-control date-picker" data-orientation="top" name="endMonth" required  />
													<div class="input-group-addon">
														<i class="fa fa-calendar"></i>
													</div>
												</div>
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-2 control-label">Group:</label>
											<div class="col-md-5">
												<select name="group" class="form-control select2ajax filters" multiple
													data-ajax-url="<c:url value='/report/QuotationRecordProgress/queryGroupSelect2'/>"></select>
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-2 control-label">Sub Group:</label>
											<div class="col-md-5">
												<select name="subGroup" class="form-control select2ajax filters" multiple
													data-ajax-url="<c:url value='/report/QuotationRecordProgress/querySubGroupSelect2'/>"></select>
											</div>
										</div>
										
										<div class="form-group">
											<label class="col-md-2 control-label">CPI Base Period:</label>
											<div class="col-md-5">
												<select name="cpiBasePeriods" class="form-control select2ajax filters" multiple
													data-ajax-url="<c:url value='/report/QuotationRecordProgress/queryCPIBasePeriodSelect2'/>"></select>
											</div>
										</div>
										<div class="form-group">											
											<div class="col-md-offset-2 col-md-10">
												<input type="hidden" name="taskType" value="" id="taskType" />
												<button type="submit" class="btn btn-info" id="pdfBtn">Export PDF</button>
												<button type="submit" class="btn btn-info" id="excelBtn">Export Excel</button>
											</div>											
										</div>
																				
									
								</form>
							</div>
						
						
							<div class="clearfix">&nbsp;</div>
							<table class="table table-striped table-bordered table-hover" id="dataList">
								<thead>
								<tr>
									<th>Created Date</th>
									<th>Description</th>
									<th>Created By</th>
									<th>Exception</th>
									<th>Status</th>
								</tr>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>
					</div>
				</div>
			</div>
        </section>		
	</jsp:body>
</t:layout>