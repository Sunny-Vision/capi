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
		<style>
			.btn.pull-right {
				margin-left: 10px;
			}
			
			.highlight {
			    background: #000;
 			    color: #fff
			}
			.datepicker td.day.highlight:hover{
				color:#000
			}
		</style>
	</jsp:attribute>
	<jsp:attribute name="header">
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
<%-- 		<%@include file="/WEB-INF/views/includes/commonDialog/userLookup.jsp" %> --%>
		<%@include file="/WEB-INF/views/includes/commonDialog/reportUserLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker.jsp"%>
		<%@include file="/WEB-INF/views/includes/datejs.jsp"%>
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
	                "ajax": "<c:url value='/report/AllocationTransferInTransferOutReallocationRecords/query'/>",
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
	                            			return "<a href='<c:url value='/report/AllocationTransferInTransferOutReallocationRecords/downloadFile?id='/>"+full.reportTaskId+"' class='table-btn'><span class='glyphicon glyphicon-download-alt' aria-hidden='true'></span></a>";
	                            		}
	                            		else{
	                            			return data;
	                            		}
                            		}
	                            }
	                        ]
				});
				
				$('.select2').select2({
					placeholder: "",
					allowClear: true,
					closeOnSelect: false
				});
				
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
				$("#pdfBtn").click(function(){
					$("#taskType").val(1)
				});
				$("#excelBtn").click(function(){
					$("#taskType").val(2)
				});
				
				$('#teamId').select2({
					multiple : true,
					allowClear : true,
					closeOnSelect:false
				});
				
				teams = function() {
					if($('#teamId').val()!=null && $('#teamId').val()!='')
						return $('#teamId').val();
					else
						return '';
				};
				
				/* $('.officerIds').select2({
					multiple : true,
					closeOnSelect:false,
					allowClear : true,
			      	ajax: {
			            dataType: 'json',
			            cache: true,
			            quietMillis: 2000, // waits 2 seconds before triggers the search
			            //url: "<c:url value='/report/AllocationTransferInTransferOutReallocationRecords/queryOfficerSelect2'/>",
						url: $('#officerIds').data('dataajaxurl'),
			            data: function (term) {
			            	return $.extend(term, {teams:teams});
			            }
			    	},
				}); */
				
				$('#userId').select2({
					multiple : true,
					closeOnSelect: false,
					allowClear: true,
					ajax: {
						dataType: 'json',
						cache: true,
						quietMillis: 2000,
						url: $('#userId').data('dataajaxurl'),
						data: function(term) {
							return $.extend(
									term
									, {authorityLevel: 16}
									, {endDate: $('[name="toMonth"]').val()}
									, {startDate: $('[name="fromMonth"]').val()}
									, {staffType: 1}
									);
						}
					}
				});
				
				$('.searchUserId').userLookup({
					selectedIdsCallback: function(selectedIds) {
						var $select = $('#userId');
						var getKeyValueByIdsUrl = $select.data('getKeyValueByIdsUrl');

						$.post(getKeyValueByIdsUrl, { id: selectedIds }, function(data) {
							for (i = 0; i < data.length; i++){
								var option = new Option(data[i].value, data[i].key);
								option.selected = true;
								$select.append(option);								
							}
							$select.trigger('change');
						});
					},
					queryDataCallback: function(model) {
						model.authorityLevel = 16;
						var ids = [];
						$("#userId option:selected").each(function(){
							ids.push(this.value)
						});
						model.excludedIds = ids;
						model.endDate = $('[name="toMonth"]').val();
						model.startDate = $('[name="fromMonth"]').val();
						model.staffType = 1;
					},
					multiple: true
				});
				
				/* $('.officerIds').on('click', function() {
					console.log("terms:"+terms);
				}); */
				
				refMonth = function(){
					return [$('#fromMonth').val(), $('#toMonth').val()];
				};
				
				$('#allocationBatch').select2({
					multiple : true,
					closeOnSelect:false,
					allowClear : true,
			      	ajax: {
			            dataType: 'json',
			            cache: true,
			            quietMillis: 2000, // waits 2 seconds before triggers the search
			            url: "<c:url value='/report/AllocationTransferInTransferOutReallocationRecords/queryAllocationBatchSelect2'/>",
			            data: function (term) {
			            	return $.extend(term, {refMonth:refMonth});
			            }
			    	},
				});
				$('.date-picker').on('change', function(e){
					$('#userId').empty().trigger('change');
					if($('[name="fromMonth"]').val() == "" || $('[name="toMonth"]').val() == ""){
						$('#userId').attr('disabled','disabled');
						$('#userLookup').addClass('hide');
					} else {
						$('#userId').removeAttr('disabled');
						$('#userLookup').removeClass('hide');
					}
				});
				
				$('#userId').attr('disabled','disabled');
				
				$('#userLookup').addClass('hide');
				
				$('#fromMonth').change(function() {
					$('#allocationBatch').val(null).trigger('change');
				});
				
				$('#toMonth').change(function() {
					$('#allocationBatch').val(null).trigger('change');
				});
				$('#stage').change(function(){
					$('#allocationBatch').empty().trigger('change');
					if($('[name="stage"]').val() == "2"){
						$('#allocationBatch').attr('disabled', 'disabled');
					}
					else{
						$('#allocationBatch').removeAttr('disabled');
					}
				});
				
				
				$("#criteriaForm").validate();
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Allocation/Transfer in/Transfer out/Reallocation Records Report</h1>
        </section>
        
        <section class="content">
        	<div class="row">
				<div class="col-md-12" >
				<!-- content -->
					<div class="box" >
						<div class="box-body">
							<div class="clearfix">&nbsp;</div>
							<div id="filters"> 
								<form class="form-horizontal" action="<c:url value="/report/AllocationTransferInTransferOutReallocationRecords/submitReportRequest"/>" id="criteriaForm" method="post">
									<%-- <div class="form-group">
											<label class="col-md-2 control-label">Team:</label>
											<div class="col-md-4">
												<select class="form-control select2" id="teamId" name="teams" multiple 
													data-ajax-url="<c:url value='/report/AllocationTransferInTransferOutReallocationRecords/queryTeamSelect2'/>"></select>
											</div>
										</div> --%>
										<div class="form-group">
											<label class="col-md-2 control-label">Officer:</label>
											<div class="col-md-5">
											<div class="input-group">
												<select multiple name="officerIds" id="userId" class="select2ajax filters"
													data-allow-clear="true"
													data-placeholder=""
													data-ajax-url="<c:url value='/report/AllocationTransferInTransferOutReallocationRecords/queryOfficerSelect2'/>"
													data-get-key-value-by-ids-url="<c:url value='/report/AllocationTransferInTransferOutReallocationRecords/queryUserSelectMutiple'/>"
												></select>
												<div id="userLookup" class="input-group-addon searchUserId">
													<i class="fa fa-search"></i>
												</div>
											</div>
											</div>
										</div>		
										
										<%-- <div class="form-group">
											<label class="col-md-2 control-label">Purpose:</label>
											<div class="col-md-4">
		   										<select name="purpose" class="form-control select2ajax filters" multiple
													data-ajax-url="<c:url value='/report/AllocationTransferInTransferOutReallocationRecords/queryPurposeSelect2'/>"></select>
											</div>
										</div> --%>
										
										<div class="form-group">
											<label class="col-md-2 control-label">Collection Mode:</label>
											<div class="col-md-4">
												<select name="collectionMode" class="form-control select2 filters" multiple>
													<option value="1">Field Visit</option>
													<option value="2">Telephone</option>
													<option value="3">Fax</option>
													<option value="4">Others</option>
												</select>
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-2 control-label">Period:</label>
											<div class="col-md-2">
												<div class="input-group">
													<input type="text" class="form-control date-picker" data-orientation="top" name="fromMonth" id="fromMonth" required  />
													<div class="input-group-addon">
														<i class="fa fa-calendar"></i>
													</div>
												</div>
											</div>
											<div class="col-md-2">
												<div class="input-group">
													<input type="text" class="form-control date-picker" data-orientation="top" name="toMonth" id="toMonth" required  /> 
													<div class="input-group-addon">
														<i class="fa fa-calendar"></i>
													</div>
												</div>
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-2 control-label">Allocation Batch:</label>
											<div class="col-md-4">
												<select class="form-control select2" name="allocationBatch" id="allocationBatch" multiple ></select>
											</div>
										</div>	
										<div class="form-group">
											<label class="col-md-2 control-label">Stage:</label>
											<div class="col-md-4">
												<select class="form-control" name="stage" id="stage" required>
													<option value="1">Transfer in/out</option>
													<option value="2">Reallocation</option>
												</select>
											</div>
										</div>	
										
										<div class="form-group">											
											<div class="col-md-offset-2 col-md-10">
												<button type="submit" class="btn btn-info" id="excelBtn">Export Excel</button>
											</div>									
										</div>
										
									<input type="hidden" name="taskType" id="taskType" />
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