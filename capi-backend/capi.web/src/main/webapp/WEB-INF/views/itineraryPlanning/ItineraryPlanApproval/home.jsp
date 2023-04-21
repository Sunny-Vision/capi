<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp"%>
	</jsp:attribute>
	<jsp:attribute name="header">
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker.jsp"%>
		<%@include file="/WEB-INF/views/includes/select2.jsp"%>
		<script>
			function approveRecordsWithConfirm(data) {
				bootbox.confirm({
					title:"Confirmation",
					message: "<spring:message code='W00004' />",
					callback: function(result){
						if (result){
							$.post("<c:url value='/itineraryPlanning/ItineraryPlanApproval/approve'/>",
								data,
								function(response) {
									$("#dataList").DataTable().ajax.reload();
									$("#MessageRibbon").html(response);
								}
							);
						}
					}
				})
			}
			function rejectRecordsWithConfirm(data) {
				$("#rejectForm").modal('show');
				$("#rejectBtn").unbind( "click" ).on('click', function() {
					$("#rejectForm").modal('hide');
					var reason = $("#rejectReason").val();
					rejectRecordsWithReason(reason);
					$("#rejectReason").val("");
				});
				
				/*
				bootbox.confirm({
					title:"Confirmation",
					message: "<spring:message code='W00005' />",
					callback: function(result){
						if (result){
							
							$("#rejectForm").modal('show');
							$("#rejectBtn").unbind( "click" ).on('click', function() {
								$("#rejectForm").modal('hide');
								var reason = $("#rejectReason").val();
								rejectRecordsWithReason(reason);
								$("#rejectReason").val("");
							});
						}
					}
				})
				*/
			}
			
			function rejectRecordsWithReason(reason) {
				var data = $("#dataList").find(':checked').serializeArray();
				data.push({name: 'reason', value: reason}) ;
				$.post("<c:url value='/itineraryPlanning/ItineraryPlanApproval/reject'/>",
						data,
						function(response) {
							$("#dataList").DataTable().ajax.reload();
							$("#MessageRibbon").html(response);
						}
					);
			}
		
			$(document).ready(function(){
				var $dataTable = $("#dataList");
				
				var button = [];
				<sec:authorize access="hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)">
				button = [
					{
						"text": "Approve",
						"action": function( nButton, oConfig, flash ) {
							var data = $dataTable.find(':checked').serialize();
							if (data == '') {
								bootbox.alert({
									title: "Alert",
									message: "<spring:message code='E00009' />"
								});
								return;
							}
							approveRecordsWithConfirm(data);
						}
					},
					{
						"text": "Reject",
						"action": function( nButton, oConfig, flash ) {
							var data = $dataTable.find(':checked').serialize();
							if (data == '') {
								bootbox.alert({
									title: "Alert",
									message: "<spring:message code='E00009' />"
								});
								return;
							}
							rejectRecordsWithConfirm(data);
						}
					}
				],
				</sec:authorize>
				
				$dataTable.DataTable({
					"ordering": true, 
					"order": [[ 1, "desc" ]],
					"searching": true,
					"buttons": button,
					"processing": true,
	                "serverSide": true,
	                "ajax": {
	                	"url": "<c:url value='/itineraryPlanning/ItineraryPlanApproval/query'/>",
	                	method: "post",
	                	data: function(d) {
	                		d.search["fieldOfficerId"] = $('#fieldOfficerId').val();
	                		d.search["date"] = $('#date').val();
	                	}
	                },
	                "columns": [
	                            { "data": "id" },
	                            { "data": "date" },
	   					        { "data": "noOfAssignment" },
	                            { "data": "fieldOfficerCode" },
	                            { "data": "chineseName" },
	                            { "data": "id" }
	                        ],
	                "columnDefs": [

	                               {
	                                   "targets": [0],
	                                   "orderable": false,
	                                   "searchable": false,
	                                   "render" : function(data, type, full, meta){
	                                       return "<input type='checkbox' name='id' value='"+full.id+"_"+full.version+"'/>";
	                                   },
	                                   "className" : "text-center"
	                               },
	                               {
	                            	   "targets": [5],
	                                   "orderable": false,
	                                   "searchable": false,
	                                   "render" : function(data, type, full, meta){
	                                	   var html = "<a href='<c:url value='/itineraryPlanning/ItineraryPlanApproval/view?id='/>"+data+"'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a>";
	                                	   return html;
	                                   },
	                                   "className" : "text-center"
	                               },
	                               {
	                            		"targets": "_all",
		                            	"orderable": true,
	                               		"searchable": true,
	                               		"className" : "text-center"
	                            	   
	                               }
	                               
	                           ]
				});
				
				Datepicker();
				<sec:authorize access="hasPermission(#user, 4) or hasPermission(#user, 256)">
				$('#fieldOfficerId').select2ajax({
					width:'100%',
					placeholder: "",
					allowClear: true
				});
				</sec:authorize>
				
				/* $('#fieldOfficerId').select2({
					width:'100%',
					placeholder: "",
					allowClear: true
				}); */
				
				$('.filters').change(function(){
					$dataTable.DataTable().ajax.reload();
				});
	
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Itinerary Plan Approval</h1>
        </section>   
        <section class="content">
        	<div class="row">
				<div class="col-md-12" >
				<!-- content -->
					<!-- Itinerary Planning Table -->
					<div class="box" >
						<div class="box-body">
							<div class="clearfix">&nbsp;</div>
							<div class="row">
								<div class="col-md-4">
									<form class="form-inline filter">
										<div class="row row-eq-height">
											<div class="col-md-2">
												<label class="control-label">Date</label>
											</div>
											<div class="col-md-10">
												<div class="input-group">
													<input type="text" id="date" name="date" class="form-control filters date-picker" maxlength="10" required>
													<div class="input-group-addon">
														<i class="fa fa-calendar"></i>
													</div>
												</div>
											</div>
										</div>
									</form>
								</div>
								<div class="col-md-4">
									<form class="form-inline filter">
										<div class="row row-eq-height">
											<div class="col-md-3">
												<label class="control-label">Field Officer</label>
											</div>
											<div class="col-md-9">
												<div class="select2">
													<select class="form-control form-control select2 filters" id="fieldOfficerId" name="fieldOfficerId"
															data-ajax-url="<c:url value='/itineraryPlanning/ItineraryPlan/queryOfficerSelect2'/>"></select>
												</div>
											</div>
										</div>
									</form>
								</div>
							</div>
							<hr/>
							<table class="table table-striped table-bordered table-hover" id="dataList">
								<thead>
								<tr>
									<th class="text-center action" ><input class="select_all" type="checkbox" /></th>
									<th>Date</th>
									<th>No. of assignment</th>
									<th>Field Officer Code</th>
									<th>Chinese Name</th>
									<th class="text-center action"></th>
								</tr>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>
					</div>
				</div>
			</div>
			<!-- Reject Reason Dialog -->
			<div class="modal fade" id="rejectForm" tabindex="-1" role="dialog" aria-labelledby="dialogLabel" aria-hidden="true">
			  <div class="modal-dialog">
				<div class="modal-content">
					<div  class="clearfix overlay" data-bind="loading:isLoading, visible:isLoading" ></div>
					  <div class="modal-header">
						<h4 class="modal-title" id="dialogLabel" data-bind="text:formTitle">Reject Dialog</h4>
					  </div>
					  <div class="modal-body form-horizontal">
						<div class="form-group">
							<div class="col-md-offset-1 col-md-10 ">Reject Reason</div>
							<div class="col-md-offset-1 col-md-10 ">
								<textarea class="form-control" rows="5" id="rejectReason"></textarea>
							</div>
							<div style="margin-top: 30px;" class="col-md-3 control-label"></div>
							<div style="margin-top: 30px;" class="col-md-3">
								<button type="submit" class="btn btn-primary" data-loading-text="Loading..." id="rejectBtn">Submit</button>
							</div>
							<div style="margin-top: 30px;" class="col-md-6">
								<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
							</div>
						</div>
					  </div>
				</div>
			  </div>
			</div>
        </section>		
	</jsp:body>
</t:layout>