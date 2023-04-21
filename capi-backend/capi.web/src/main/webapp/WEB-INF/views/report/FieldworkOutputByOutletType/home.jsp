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
		<%@include file="/WEB-INF/views/includes/commonDialog/reportUserLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker.jsp"%>
		<%@include file="/WEB-INF/views/includes/datejs.jsp"%>
		<script>
			$(document).ready(function(){
				var $dataTable = $("#dataList");
				var events = [];
				
				$('.select2ajax').select2ajax({
					allowClear: true,
					placeholder: '',
					width:"100%",
					closeOnSelect: false,
				});
				
				$('.select2ajax').hide();
				
				$dataTable.DataTable({
					"order": [[ 0, "desc" ]],
					"searching": true,
					"ordering": true,
					"buttons": [],
					"processing": true,
	                "serverSide": true,
	                "ajax": "<c:url value='/report/FieldworkOutputByOutletType/query'/>",
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
	                            			return "<a href='<c:url value='/report/FieldworkOutputByOutletType/downloadFile?id='/>"+full.reportTaskId+"' class='table-btn'><span class='glyphicon glyphicon-download-alt' aria-hidden='true'></span></a>";
	                            		}
	                            		else{
	                            			return data;
	                            		}
                            		}
	                            }
	                        ]
				});
				

				Datepicker($(".date-picker:not([readonly])"));
				
				$("#criteriaForm").validate();
				
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
							return $.extend(term, {authorityLevel:${authorityLevel}}, {startDate:$('[name="refMonth"]').val()}
								, {endDate:$('[name="refMonth"]').val()}, {refMonth:$('[name="refMonth"]').val()}, {staffType: 1}, {userIds: ${userIds}});
						}
					}
				});
				
				$('.searchUserId').userLookup({
					selectedIdsCallback: function(selectedId) {
						var $select = $('#userId');
						var getKeyValueByIdsUrl = $select.data('getKeyValueByIdsUrl');
						
						$.post(getKeyValueByIdsUrl, { id: selectedId }, function(data) {
							for (i = 0; i < data.length; i++){
								var option = new Option(data[i].value, data[i].key);
								option.selected = true;
								$select.append(option);								
							}
							$select.trigger('change');
						});
					},
					queryDataCallback: function(model) {
						model.authorityLevel = ${authorityLevel};
						var ids = [];
						$("#userId option:selected").each(function(){
							ids.push(this.value)
						});
						model.excludedIds = ids;
						model.startDate = $('[name="refMonth"]').val();
						model.endDate = $('[name="refMonth"]').val();
						model.staffType = 1;
						model.userIds = ${userIds};
					},
					multiple: true
				});
				
				$('[name="allocationBatchId"]').select2ajax({
					allowClear: true,
					closeOnSelect: false,
					placeholder: '',
					width:"100%",
					ajax: {
					    data: function (params) {
					    	return $.extend(params, {referenceMonthStr: $('[name="refMonth"]').val()});
						},
						method: 'GET',
						contentType: 'application/json'
					}
				});
				
				$('[name="refMonth"]').on('change', function(e){
					$('#userId').empty().trigger('change');
					$('[name="allocationBatchId"]').empty().trigger('change');
					if($('[name="refMonth"]').val() == ""){
						$('#userId').attr('disabled','disabled');
						$('#userLookup').addClass('hide');
						$('[name="allocationBatchId"]').attr('disabled','disabled');
					} else {
						$('#userId').removeAttr('disabled');
						$('#userLookup').removeClass('hide');
						$('[name="allocationBatchId"]').removeAttr('disabled');
					}
				});
				
				$('[name="outletTypeShortCode"]').select2ajax({
					allowClear: true,
					placeholder: '',
					width:"100%",
					closeOnSelect: false,
					maximumSelectionLength: 2000,
			        language: {
						maximumSelected: function () {
							return '<spring:message code='W00036' />';
						}
					}
				}).on("change", function(e) {
				    if(e.target.length >= 2000) {
				    	$('[name="outletTypeShortCode"]').select2("close");
				    }
				});
				
				$('#userId').attr('disabled','disabled');
				$('#userLookup').addClass('hide');
				$('[name="allocationBatchId"]').attr('disabled','disabled');
				
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Fieldwork Output Report ( by Outlet Type )</h1>
        </section>
        
        <section class="content">
        	<div class="row">
				<div class="col-md-12" >
				<!-- content -->
					<div class="box" >
						<div class="box-body">
							<div class="clearfix">&nbsp;</div>
							<div id="filters">
								<form class="form-horizontal" action="<c:url value="/report/FieldworkOutputByOutletType/submitReportRequest"/>" id="criteriaForm" method="post">
										<div class="form-group">
											<label class="col-md-2 control-label">Field Officer:</label>
											<div class="col-md-5">
												<div class="input-group">
													<input type="hidden" class="form-control" name="officers"/>
													<select multiple name="officerId" id="userId" class="select2ajax filters"
														data-allow-clear="true"
														data-placeholder=""
														data-ajax-url="<c:url value='/report/FieldworkOutputByOutletType/queryOfficerSelect2'/>"
														data-get-key-value-by-ids-url="<c:url value='/report/FieldworkOutputByOutletType/queryUserSelectMutiple'/>"
													></select>
													<div id="userLookup" class="input-group-addon searchUserId">
														<i class="fa fa-search"></i>
													</div>
												</div>
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-2 control-label">Purpose</label>
											<div class="col-md-4">
		   										<select name="purposeId" class="form-control select2ajax filters"
													data-allow-clear="true"
													data-placeholder="" multiple
													data-ajax-url="<c:url value='/report/FieldworkOutputByOutletType/queryPurposeSelect2'/>"></select>
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-2 control-label">Outlet Type</label>
											<div class="col-md-4">
		   										<select name="outletTypeShortCode" class="form-control filters"
													data-allow-clear="true"
													data-placeholder="" multiple
													data-ajax-url="<c:url value='/report/FieldworkOutputByOutletType/queryOutletTypeSelect2'/>"></select>
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-2 control-label">Period:</label>
											<div class="col-md-2">
												<div class="input-group">
													<input type="text" class="form-control date-picker" data-orientation="top" name="refMonth" required data-date-minviewmode="months" data-date-format="mm-yyyy"/>
													<div class="input-group-addon">
														<i class="fa fa-calendar"></i>
													</div>
												</div>
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-2 control-label">Allocation Batch</label>
											<div class="col-md-4">
		   										<select name="allocationBatchId" class="form-control filters"
													data-allow-clear="true"
													data-placeholder="" multiple
													data-ajax-url="<c:url value='/report/FieldworkOutputByOutletType/queryAllocationBatchSelect2'/>"></select>
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