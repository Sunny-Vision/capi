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
	                "ajax": "<c:url value='/report/ListOfQuotationRecords/query'/>",
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
	                            			return "<a href='<c:url value='/report/ListOfQuotationRecords/downloadFile?id='/>"+full.reportTaskId+"' class='table-btn'><span class='glyphicon glyphicon-download-alt' aria-hidden='true'></span></a>";
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
				
// 				$('[name="notEqual"]').change(function() {
// 					if($(this).val() == 'B'){ // or this.value == 'volvo'
// 						$('[name="notEqual"]').val(null).trigger('change');
// 					  }
// 				});
				
				$("#criteriaForm").validate();
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>List of Quotation Records with Specific Filterings for Micro-review</h1>
        </section>
        
        <section class="content">
        	<div class="row">
				<div class="col-md-12" >
				<!-- content -->
					<div class="box" >
						<div class="box-body">
							<div class="clearfix">&nbsp;</div>
							<div id="filters">
								<form class="form-horizontal" action="<c:url value="/report/ListOfQuotationRecords/submitReportRequest" />" id="criteriaForm" method="post">
									<div class="form-group">
										<label class="col-md-2 control-label">Purpose:</label>
										<div class="col-md-5">
											<select name="purpose" class="form-control select2ajax filters" multiple
												data-ajax-url="<c:url value='/report/ListOfQuotationRecords/queryPurposeSelect2'/>"></select>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-2 control-label">Classification:</label>
										<div class="col-md-5">
											<div class="input-group">
												<select name="itemId" class="form-control filters"
													data-allow-clear="true"
													data-placeholder=""
													data-ajax-url="<c:url value='/report/ListOfQuotationRecords/queryUnitSelect2'/>"
													data-get-key-value-by-ids-url="<c:url value='/report/ListOfQuotationRecords/getKeyValueByIds'/>"
													data-close-on-select="false"
													multiple></select>
												<div id="unitLookup" class="input-group-addon lookup"
													data-bottom-entity-class="Item">
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
										<label class="col-md-2 control-label">Data collection:</label>
										<div class="col-md-5">
											<select name="dataCollection" class="form-control select2 filters">
												<option></option>
												<option value="Y">Field</option>
												<option value="N">No Field</option>
												<option value="A">All</option>
											</select>
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
									<br/>
									<div class="form-group">
										<label class="col-md-2 control-label">Imputed Variety's average price:</label>
										<div class="col-md-5">
											<select name="imputedVariety" class="form-control select2 filters">
												<option></option>
												<option value="Y">Not Blank</option>
												<option value="N">Blank</option>
											</select>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-2 control-label">Imputed Quotation's average price:</label>
										<div class="col-md-5">
											<select name="imputedQuotation" class="form-control select2 filters">
												<option></option>
												<option value="Y">Not Blank</option>
												<option value="N">Blank</option>
											</select>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">Previous S Price &lt;&gt; Last S Price:</label>
										<div class="col-md-4">
											<select name="notEqual" class="form-control select2 filters">
												<option></option>
												<option value="B">Blank</option>
												<option value="Y">Y</option>
												<option value="N">N</option>
											</select>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-2 control-label">RUA case:</label>
										<div class="col-md-5">
											<select name="ruaCase" class="form-control select2 filters">
												<option></option>
												<option value="Y">Y</option>
												<option value="N">Blank</option>
											</select>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-4 control-label">Edited price is null and (Survey price is not null or price remarks is not null) and not using Sub-price type:</label>
										<div class="col-md-3">
											<select name="priceCondition" class="form-control select2 filters">
												<option></option>
<!-- 												<option value="B">Blank</option> -->
												<option value="Y">Y</option>
												<option value="N">N</option>
												
											</select>
										</div>
									</div>
									
									<div class="form-group">											
										<div class="col-md-offset-2 col-md-10">
											<button type="submit" class="btn btn-info" id="btn">Export</button>
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