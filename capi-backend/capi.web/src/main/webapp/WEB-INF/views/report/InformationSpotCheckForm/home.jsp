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
				var scDates = [];
				
				$dataTable.DataTable({
					"order": [[ 0, "desc" ]],
					"searching": true,
					"ordering": true,
					"buttons": [],
					"processing": true,
	                "serverSide": true,
	                "ajax": "<c:url value='/report/InformationSpotCheckForm/query'/>",
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
	                            			return "<a href='<c:url value='/report/InformationSpotCheckForm/downloadFile?id='/>"+full.reportTaskId+"' class='table-btn'><span class='glyphicon glyphicon-download-alt' aria-hidden='true'></span></a>";
	                            		}
	                            		else{
	                            			return data;
	                            		}
                            		}
	                            }
	                        ]
				});
				
				$('.month-picker').datepicker({
					format: monthFormat,
					startView: "months", 
				    minViewMode: "months"
				}).on('changeDate', function(e) {
					$.get("<c:url value='/report/InformationSpotCheckForm/getSCDates' />", {date: $('.month-picker').val()}, function (data){
						scDates = [];
						for (i = 0; i < data.length; i++){
							scDates.push(parseDate(data[i]));
						}
						$('.date-picker').datepicker('update', '');
					});
				});
				
				$(".select2").select2({
					placeholder: "",
					allowClear: true,
					closeOnSelect: false
				});
				
				
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
							return $.extend(term, {authorityLevel:16}, {startDate:$('[name="refMonth"]').val()}
								, {endDate:$('[name="refMonth"]').val()}, {staffType: 1});
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
						model.authorityLevel = 16;
						var ids = [];
						$("#userId option:selected").each(function(){
							ids.push(this.value)
						});
						model.excludedIds = ids;
						model.startDate = $('[name="refMonth"]').val();
						model.endDate = $('[name="refMonth"]').val();
						model.staffType = 1;
					},
					multiple: true
				});
				
				$('[name="refMonth"]').on('change', function(e){
					$('#userId').empty().trigger('change');
					if($('[name="refMonth"]').val() == ""){
						$('#userId').attr('disabled','disabled');
						$('#userLookup').addClass('hide');
					} else {
						$('#userId').removeAttr('disabled');
						$('#userLookup').removeClass('hide');
					}
				});
				
				$(".date-picker .select2").select2();
				Datepicker(".date-picker",{
					multidate: true,
					beforeShowDay: function(date) {
	            		for (var i = 0; i < scDates.length; i++) {
	            			var str_date = scDates[i];
							if (str_date!=null && str_date.equals(date)) {
								return true;
							}
						}
						return false;
					},
					todayBtn: false
				});
				
				$("#pdfBtn").click(function(){
					$("#taskType").val(1)
				});
				$("#excelBtn").click(function(){
					$("#taskType").val(2)
				});
				
				$('#userId').attr('disabled','disabled');
				$('#userLookup').addClass('hide');
				
				$("#criteriaForm").validate({
					ignore: ""
				});
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Report of information of Spot Check forms</h1>
        </section>
        
        <section class="content">
        	<div class="row">
				<div class="col-md-12" >
				<!-- content -->
					<div class="box" >
						<div class="box-body">
							<div class="clearfix">&nbsp;</div>
							<div id="filters">
								<form class="form-horizontal" action="<c:url value="/report/InformationSpotCheckForm/submitReportRequest" />" id="criteriaForm" method="post">
										
										<div class="form-group">
											<label class="col-md-2 control-label">Period:</label>
											<div class="col-md-2">
												<div class="input-group">
													<input type="text" name="refMonth" class="form-control month-picker" value="" required>
													<div class="input-group-addon">
														<i class="fa fa-calendar"></i>
													</div>
												</div>
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-2 control-label">Survey:</label>
											<div class="col-md-5">
												<select name="survey" class="form-control select2 filters" multiple>
													<option value="MRPS">MRPS</option>
													<option value="GHS">GHS</option>
													<option value="BMWPS">BMWPS</option>
													<option value="Others">Others</option>
												</select>
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-2 control-label">Field Officer:</label>
											<div class="col-md-5">
												<div class="input-group">
													<input type="hidden" class="form-control" name="officers"/>
													<select multiple name="fieldOfficerId" id="userId" class="select2ajax filters"
														data-allow-clear="true"
														data-placeholder=""
														data-ajax-url="<c:url value='/report/InformationSpotCheckForm/queryOfficerSelect2'/>"
														data-get-key-value-by-ids-url="<c:url value='/report/InformationSpotCheckForm/queryUserSelectMutiple'/>"
														required
													></select>
													<div id="userLookup" class="input-group-addon searchUserId">
														<i class="fa fa-search"></i>
													</div>
												</div>
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-2 control-label">Date:</label>
											<div class="col-md-2">
												<div class="input-group date date-picker" data-orientation="top" data-multidate="true"
													 data-target="[name=dateDisplay]" data-input="#spotCheckDates" >
													<input type="text" class="form-control" style="display: none;" id="spotCheckDates" name="spotCheckDates" value="" required readonly />
			       									<select class="form-control select2" disabled name="dateDisplay" multiple style="display:none"></select>
													<div id="spotCheckDatesIcon" class="input-group-addon">
														<i class="fa fa-calendar"></i>
													</div>
												</div>
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