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
				var events = [];
				
// 				$.fn.userLookup.defaults.queryUrl = "<c:url value='/commonDialog/ReportUserLookup/query2'/>";
// 				$.fn.userLookup.defaults.selectAllUrl = "<c:url value='/commonDialog/ReportUserLookup/getLookupTableSelectAll2'/>";
				
				$('.select2ajax').select2ajax({
					allowClear: true,
					placeholder: '',
					width:"100%",
					closeOnSelect: false
				});
				
				$('.select2ajax').hide();
				
				$dataTable.DataTable({
					"order": [[ 0, "desc" ]],
					"searching": true,
					"ordering": true,
					"buttons": [],
					"processing": true,
	                "serverSide": true,
	                "ajax": "<c:url value='/report/SummaryOfVerificationCases/query'/>",
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
	                            			return "<a href='<c:url value='/report/SummaryOfVerificationCases/downloadFile?id='/>"+full.reportTaskId+"' class='table-btn'><span class='glyphicon glyphicon-download-alt' aria-hidden='true'></span></a>";
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
				
				$('[name="officerId"]').select2ajax({
					allowClear: true,
					placeholder: '',
					width:"100%",
					closeOnSelect: false,
					ajax: {
					    data: function (params) {
					    	//console.log('Start Date : ' + $('[name="startMonthStr"]').val() + ', End Date: ' + $('[name="endMonthStr"]').val());
					    	return $.extend(params, {teamId: $('[name="teamId"]').val()}, {staffType: 1}, {authorityLevel:16}, {startDate:$('[name="startMonthStr"]').val()},{endDate:$('[name="endMonthStr"]').val()});
						},
						method: 'GET',
						contentType: 'application/json'
					}
				});
				
				$('[name="surveyId"]').select2ajax({
					allowClear: true,
					placeholder: '',
					width:"100%",
					closeOnSelect: false,
					ajax: {
					    data: function (params) {
					    	return $.extend(params, {purposeId: $('[name="purposeId"]').val()});
						},
						method: 'GET',
						contentType: 'application/json'
					}
				});
				
				$('[name="purposeId"]').on("change", function(){
					$('[name="surveyId"]').val(null).trigger("change");
				})
				/* 
				$("#pdfBtn").click(function(){
					$("#taskType").val(1);
				}); */
				$("#excelBtn").click(function(){
					$("#taskType").val(2);
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
						var ids = [];
						model.authorityLevel = 16;
						model.staffType = 1;
						$("#userId option:selected").each(function(){
							ids.push(this.value)
						});
						model.excludedIds = ids;
						model.endDate = $('[name="endMonthStr"]').val();
						model.startDate = $('[name="startMonthStr"]').val();
					},
					multiple: true
				});
				$('.date-picker').on('change', function(e){
					$('#userId').empty().trigger('change');
					if($('[name="refMonth"]').val() == ""){
						$('#userId').attr('disabled','disabled');
						$('#userLookup').addClass('hide');
					} else {
						$('#userId').removeAttr('disabled');
						$('#userLookup').removeClass('hide');
					}
				});
				
				$('#userId').attr('disabled','disabled');
				$('#userLookup').addClass('hide');
				
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Summary of Verification Cases Report</h1>
        </section>
        
        <section class="content">
        	<div class="row">
				<div class="col-md-12" >
				<!-- content -->
					<div class="box" >
						<div class="box-body">
							<div class="clearfix">&nbsp;</div>
							<div id="filters">
								<form class="form-horizontal" action="<c:url value="/report/SummaryOfVerificationCases/submitReportRequest"/>" id="criteriaForm" method="post">
<!-- 										<div class="form-group"> -->
<!-- 											<label class="col-md-2 control-label">Team</label> -->
<!-- 											<div class="col-md-4"> -->
<!-- 		   										<select name="teamId" class="form-control select2ajax filters" -->
<!-- 													data-allow-clear="true" -->
<!-- 													data-placeholder="" multiple -->
<%-- 													data-ajax-url="<c:url value='/report/SummaryOfVerificationCases/queryTeamSelect2'/>"></select> --%>
<!-- 											</div> -->
<!-- 										</div> -->
										<div class="form-group">
											<label class="col-md-2 control-label">Officer</label>
											<div class="col-md-4">
												<div class="input-group">
			   										<select name="officerId" id="userId" class="form-control select2ajax filters"
														data-allow-clear="true"
														data-placeholder="" multiple
														data-ajax-url="<c:url value='/report/SummaryOfVerificationCases/queryOfficerSelect3'/>"
														data-get-key-value-by-ids-url="<c:url value='/report/SummaryOfVerificationCases/queryUserSelectMutiple'/>
														"></select>
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
		   											multiple
													data-allow-clear="true"
													data-placeholder=""
													data-ajax-url="<c:url value='/report/SummaryOfVerificationCases/queryPurposeSelect2'/>"></select>
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-2 control-label">Period:</label>
											<div class="col-md-2">
												<div class="input-group">
													<input type="text" class="form-control date-picker" data-orientation="top" id="startMonthStr" name="startMonthStr" required data-date-minviewmode="months" data-date-format="mm-yyyy"/>
													<div class="input-group-addon">
														<i class="fa fa-calendar"></i>
													</div>
												</div>
											</div>
											<div class="col-md-2">
												<div class="input-group">
													<input type="text" class="form-control date-picker" data-orientation="top" id="endMonthStr" name="endMonthStr" required data-date-minviewmode="months" data-date-format="mm-yyyy"/>
													<div class="input-group-addon">
														<i class="fa fa-calendar"></i>
													</div>
												</div>
											</div>
										</div>
										<div class="form-group">											
											<div class="col-md-offset-2 col-md-10">
												<input type="hidden" name="taskType" value="" id="taskType" />
<!-- 												<button type="submit" class="btn btn-info" id="pdfBtn">Export PDF</button> -->
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