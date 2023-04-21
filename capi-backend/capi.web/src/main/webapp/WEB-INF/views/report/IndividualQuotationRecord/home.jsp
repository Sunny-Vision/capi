<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/jstree-css.jsp" %>
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
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker.jsp"%>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/jstree.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/unitLookup.jsp" %>		
		<%@include file="/WEB-INF/views/includes/commonDialog/reportAssignmentLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/quotationLookup.jsp" %>
		<script>
			$(document).ready(function(){
				
				// Change queryUrl and selectAllUrl to accept Assignment Form parameter
				$.fn.quotationLookup.defaults.queryUrl = "<c:url value='/commonDialog/QuotationLookup/queryWithAssignmentIds'/>";
				$.fn.quotationLookup.defaults.selectAllUrl = "<c:url value='/commonDialog/QuotationLookup/getLookupTableSelectAllWithAssignmentIds'/>";
				$.fn.reportAssignmentLookup.defaults.selectAllUrl = "<c:url value='/commonDialog/ReportAssignmentLookup/getLookupTableSelectAllWithReferMonth'/>";
				$.fn.reportAssignmentLookup.defaults.queryUrl = "<c:url value='/commonDialog/ReportAssignmentLookup/individualQuotationRecordReportAssignment'/>";
				
				var $dataTable = $("#dataList");
				
				$dataTable.DataTable({
					"order": [[ 0, "desc" ]],
					"searching": true,
					"ordering": true,
					"buttons": [],
					"processing": true,
	                "serverSide": true,
	                "ajax": "<c:url value='/report/IndividualQuotationRecord/query'/>",
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
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center",
	                            	"render" : function(data, type, full, meta){
	                            		if (data == "Finished"){
	                            			return "<a href='<c:url value='/report/IndividualQuotationRecord/downloadFile?id='/>"+full.reportTaskId+"' class='table-btn'><span class='glyphicon glyphicon-download-alt' aria-hidden='true'></span></a>";
	                            		}
	                            		else{
	                            			return data;
	                            		}
                            		}
	                            }
	                        ]
				});
				
				$('.date-picker').datepicker({
					format: monthFormat,
					startView: "months", 
				    minViewMode: "months",
				    autoclose: true				    
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
						//method: 'POST',
						contentType: 'application/json'
					}
				});
				*/
				
				$('[name="purpose"]').change(function() {
					$('[name="unitId"]').val(null).trigger('change');
				});
				
				$('[name="unitId"]').change(function() {
					$('[name="quotationId"]').val(null).trigger('change');
					$('[name="quotationRecordId"]').val(null).trigger('change');
				})
				
				$('[name="quotationId"]').change(function() {
					$('[name="quotationRecordId"]').val(null).trigger('change');
				})
				
				$('[name="assignmentIds"]').change(function() {
					$('[name="quotationId"]').val(null).trigger('change');
					$('[name="quotationRecordId"]').val(null).trigger('change');
				})
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
					},
					queryDataCallback: function(data) {
						data.unitId = $('[name="unitId"]').val();
						//data.purposeId = $('[name="purpose"]').val();
						data.assignmentIds = $('[name="assignmentIds"]').val();
					}
				});
				
				
				$('[name="quotationId"]').select2ajax({
					ajax: {
					    data: function (term) {
					    	return $.extend(term, {unitId: $('[name="unitId"]').val(), purposeId: $('[name="purpose"]').val(), assignmentIds: $('[name="assignmentIds"]').val()});
						},
						method: 'GET',
						contentType: 'application/json'
					}
				});
				
				$('[name="assignmentIds"]').select2ajax({
					ajax: {
					    data: function (term) {
					    	return $.extend(term, {referenceMonth: $('[name="referenceMonth"]').val()});
						},
						//method: 'POST',
						contentType: 'application/json'
					},
					allowClear: true,
					closeOnSelect: false
				});
				
				$('[name="quotationRecordId"]').select2ajax({
					ajax: {
					    data: function (term) {
					    	return $.extend(term, {quotationId: $('[name="quotationId"]').val(), assignmentIds: $('[name="assignmentIds"]').val(), referenceMonth: $('[name="referenceMonth"]').val()});
					    	//return $.extend(term, {quotationId: $('[name="quotationId"]').val()});
						},
						method: 'GET',
						contentType: 'application/json'
					}
				});
				
				$('#assignmentLookup').reportAssignmentLookup({
					selectedIdsCallback: function(selectedIds) {
						// this.$element.find('input').val(selectedIds.join());
						var $select = $("[name=assignmentIds]");
						$select.empty();
						for (var i = 0; i < selectedIds.length; i++) {
							var option = new Option(selectedIds[i], selectedIds[i]);
							option.selected = true;
							$select.append(option);
						}
						$select.trigger('change');
						
					},
					alreadySelectedIdsCallback: function() {
						return $('[name=assignmentIds]').val();
					},
					queryDataCallback: function(data) {
						data.referenceMonth = $("[name=referenceMonth]").val();
					}
				});
				
				$("#criteriaForm").validate();
							
				
				
			});
				/* 
				$('.searchQuotationRecordIds').quotationLookup({
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
					},
					queryDataCallback: function(data) {
						data.quotationId = $('[name="quotationId"]').val();
						data.assignmentIds = $('[name="assignmentIds"]').val();
					}
				});
				
				
				$('[name="quotationRecordId"]').select2ajax({
					ajax: {
					    data: function (term) {
					    	return $.extend(term, {quotationId: $('[name="quotationId"]').val(), assignmentIds: $('[name="assignmentIds"]').val()});
						},
						method: 'GET',
						contentType: 'application/json'
					}
				});				
				 */
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Information of Individual Quotation Records</h1>
        </section>
        
        <section class="content">
        	<div class="row">
				<div class="col-md-12" >
				<!-- content -->
					<div class="box" >
						<div class="box-body">
							<div class="clearfix">&nbsp;</div>
							<div id="filters">
								<form class="form-horizontal" action="<c:url value="/report/IndividualQuotationRecord/submitReportRequest" />" id="criteriaForm" method="post">
									
										<div class="form-group">
											<label class="col-md-2 control-label">Reference Month:</label>
											<div class="col-md-2">
												<div class="input-group">
													<input type="text" class="form-control date-picker" data-orientation="top" name="referenceMonth" required  />
													<div class="input-group-addon">
														<i class="fa fa-calendar"></i>
													</div>
												</div>		
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-2 control-label">Assignment:</label>
											<div class="col-md-5">
												<div class="input-group searchAssignmentIds"> 
													<select class="form-control select2ajax filters" multiple  name="assignmentIds" style="display:none"
														data-ajax-url="<c:url value='/report/IndividualQuotationRecord/queryAssignmentSelect2'/>" ></select>											
													<div class="input-group-addon" id="assignmentLookup" >														
														<i class="fa fa-search"></i>
													</div>
												</div>
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-2 control-label">Purpose:</label>
											<div class="col-md-5">
												<select name="purpose" class="form-control select2ajax filters" multiple
													data-ajax-url="<c:url value='/report/IndividualQuotationRecord/queryPurposeSelect2'/>"></select>
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-2 control-label">Classification:</label>
											<div class="col-md-5">
												<div class="input-group">
													<select name="unitId" class="form-control filters"
														data-allow-clear="true"
														data-placeholder=""
														data-ajax-url="<c:url value='/report/IndividualQuotationRecord/queryUnitSelect2'/>"
														data-get-key-value-by-ids-url="<c:url value='/report/IndividualQuotationRecord/getKeyValueByIds'/>"
														data-close-on-select="false"
														required
														multiple></select>
													<div id="unitLookup" class="input-group-addon lookup"
														data-bottom-entity-class="Unit">
														<i class="fa fa-search"></i>
													</div>
												</div>
											</div>
										</div>

										<div class="form-group">
											<label class="col-md-2 control-label">Quotation:</label>
											<div class="col-md-5">
												<div class="input-group">
													<select name="quotationId" class="form-control select2ajax filters"
														style="display:none"
														data-ajax-url="<c:url value='/report/IndividualQuotationRecord/queryQuotationSelect2'/>"
														data-allow-clear="true"
														data-placeholder=""
														data-close-on-select="false"
														multiple></select>
													<div class="input-group-addon searchQuotationIds">
														<i class="fa fa-search"></i>
													</div>
												</div>
											</div>
										</div>
										
										<%-- <div class="form-group">
											<label class="col-md-2 control-label">Quotation Record ID:</label>
											<div class="col-md-5">
												<div class="input-group">
													<select name="quotationRecordId" class="form-control filters"
														style="display:none"
														data-ajax-url="<c:url value='/report/IndividualQuotationRecord/queryQuotationRecordSelect2'/>"
														data-allow-clear="true"
														data-placeholder=""
														data-close-on-select="false"
														multiple></select>
													<div class="input-group-addon searchQuotationRecordIds">
														<i class="fa fa-search"></i>
													</div>
												</div>
											</div>
										</div>		 --%>
										
										<div class="form-group">
											<label class="col-md-2 control-label">Quotation Record ID:</label>
											<div class="col-md-5">
												<select name="quotationRecordId" class="form-control select2ajax filters" multiple
													data-ajax-url="<c:url value='/report/IndividualQuotationRecord/queryQuotationRecordSelect2'/>"></select>
											</div>
										</div>
										
										<div class="form-group">
											<label class="col-md-2 control-label">Include photo :</label>
											
											<div class="input-group">
													<input type="checkbox" name="isgetImage"/>
											</div>		

										</div>
										
										<div class="form-group">											
											<div class="col-md-offset-2 col-md-10">
												<button type="submit" class="btn btn-info" id="pdfBtn">Export</button>
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