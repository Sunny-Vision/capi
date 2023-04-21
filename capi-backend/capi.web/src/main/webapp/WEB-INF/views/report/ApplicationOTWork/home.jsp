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
				
				$dataTable.DataTable({
					"order": [[ 0, "desc" ]],
					"searching": true,
					"ordering": true,
					"buttons": [],
					"processing": true,
	                "serverSide": true,
	                "ajax": "<c:url value='/report/ApplicationOTWork/query'/>",
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
	                            			return "<a href='<c:url value='/report/ApplicationOTWork/downloadFile?id='/>"+full.reportTaskId+"' class='table-btn'><span class='glyphicon glyphicon-download-alt' aria-hidden='true'></span></a>";
	                            		}
	                            		else{
	                            			return data;
	                            		}
                            		}
	                            }
	                        ]
				});
				
				$('#userId').select2({
					multiple : false,
					closeOnSelect: true,
					allowClear: false,
					ajax: {
						dataType: 'json',
						cache: true,
						quietMillis: 2000,
						url: $('#userId').data('dataajaxurl'),
						data: function(term) {
							return $.extend(term, {authorityLevel:${authorityLevel}}, {userIds: ${userIds} }, {staffType: 1});
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
						model.userIds = ${userIds};
						model.staffType = 1;
					},
					multiple: false
				});

				$('#userId').change(function() {
					$('[name="userId"]').val($(this).val());
					updateDatePicker();
				});
				
				$('.date-picker').datepicker({
				    autoclose: true, 
				    beforeShowDay: function (date){
				    	for (i = 0; i < events.length; i++){
				    		//if (events[i].equals(date)) return 'highlight';
				    		if (events[i].equals(date)){
				    			return { "enabled": true };
				    		}
				    	}
				    	return { "enabled": false };
				    }
				});
				
				function updateDatePicker(){
					var userId = $('[name="userId"]').val();
					
					if (userId == ""){
						return;
					}
					
					$.post("<c:url value='/report/ApplicationOTWork/getStaffOTDates' />", {userId: userId}, function (data){
						events = [];
						for (i = 0; i < data.length; i++){
							events.push(parseDate(data[i]));
						}
						$('.date-picker').datepicker('update', '');
					});
				}
				
				$("#criteriaForm").validate();
				updateDatePicker();
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Application for Overtime Work to be Recompensed by Time-off in Lieu (TOIL)</h1>
        </section>
        
        <section class="content">
        	<div class="row">
				<div class="col-md-12" >
				<!-- content -->
					<div class="box" >
						<div class="box-body">
							<div class="clearfix">&nbsp;</div>
							<div id="filters">
								<form class="form-horizontal" action="<c:url value="/report/ApplicationOTWork/submitReportRequest"/>" id="criteriaForm" method="post">
										<input type="hidden" name="userId" value="${officerId}"/>
										<sec:authorize access="hasPermission(#user, 2) or hasPermission(#user, 4)">
											<div class="form-group">
												<label class="col-md-2 control-label">Officer:</label>
												<div class="col-md-5">
													<div class="input-group">
														<select id="userId" class="select2ajax filters"
															data-allow-clear="false"
															data-placeholder=""
															data-ajax-url="<c:url value='/report/ApplicationOTWork/queryOfficerSelect2'/>"
															data-get-key-value-by-ids-url="<c:url value='/report/ApplicationOTWork/queryUserSelectMutiple'/>"
															required
														></select>
														<div id="userLookup" class="input-group-addon searchUserId">
															<i class="fa fa-search"></i>
														</div>
													</div>
												</div>
											</div>	
										</sec:authorize>
										<div class="form-group">
											<label class="col-md-2 control-label">Date:</label>
											<div class="col-md-2">
												<div class="input-group">
													<input type="text" class="form-control date-picker" data-orientation="top" name="date" required  />
													<div class="input-group-addon">
														<i class="fa fa-calendar"></i>
													</div>
												</div>
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