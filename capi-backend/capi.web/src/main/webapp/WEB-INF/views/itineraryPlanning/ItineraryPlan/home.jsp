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
			function cancelRecordsWithConfirm(data) {
				bootbox.confirm({
					title:"Confirmation",
					message: "<spring:message code='W00001' />",
					callback: function(result){
						if (result){
							$.post("<c:url value='/itineraryPlanning/ItineraryPlan/cancel'/>",
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
		
			$(document).ready(function(){
				var $dataTable = $("#dataList");
				var buttons = [];
				<sec:authorize access="hasPermission(#user, 4) || hasPermission(#user, 16) || hasPermission(#user, 256)">
				buttons = [
					{
						"text": "Add",
						"action": function( nButton, oConfig, flash ) {
							window.location = "<c:url value='/itineraryPlanning/ItineraryPlan/edit'/>";
						}
					},
					{
						"text": "Delete",
						"action": function( nButton, oConfig, flash ) {
							var data = $dataTable.find(':checked').serialize();
							if (data == '') {
								//alert('<spring:message code="E00009" />');
								bootbox.alert({
									title: "Alert",
									message: "<spring:message code='E00009' />"
								});
								return;
							}
							
							cancelRecordsWithConfirm(data);
						}
					}
				],
				</sec:authorize>
				
				$dataTable.DataTable({
					"ordering": true, 
					"order": [[ 1, "desc" ]],
					"searching": true,
					"buttons": buttons,
					"processing": true,
	                "serverSide": true,
	                "ajax": {
	                	"url": "<c:url value='/itineraryPlanning/ItineraryPlan/query'/>",
	                	method: "post",
	                	data: function(d) {
	                		<sec:authorize access="hasPermission(#user, 4) || hasPermission(#user, 256)">
	                		d.search["fieldOfficerId"] = $('#fieldOfficerId').val();
	                		</sec:authorize>
	                		d.search["date"] = $('#date').val();
	                	}
	                },
	                "columns": [
	                            { "data": "id" },
	                            { "data": "date" },
	                            { "data": "noOfAssignment" },
	                            { "data": "status" },
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
	                                	   <sec:authorize access="!hasPermission(#user, 4) && !hasPermission(#user, 256) && hasPermission(#user, 16)">
		                                	   if ((full.status == 'Draft') && (full.timeLogId == null || full.timeLogId == '' )) {
		                                		   return "<input type='checkbox' name='id' value='"+full.id+"'/>";
		                                	   }
		                                	   return "";
	                                	   </sec:authorize>
	                                	   return "<input type='checkbox' name='id' value='"+full.id+"'/>";
	                                   },
	                                   "className" : "text-center"
	                               },
                            	   <sec:authorize access='!hasPermission(#user, 2) && !hasPermission(#user, 4) && !hasPermission(#user, 256)'>
                            	   {
                            		   	"targets" : [4],
                            	  		"visible" : false,
                            	   },
                            	   {
                           		   		"targets" : [5],
                           	  			"visible" : false,
                           	   		},
                                   </sec:authorize>
	                               {
	                                   "targets": [6],
	                                   "orderable": false,
	                                   "searchable": false,
	                                   "render" : function(data, type, full, meta){
	                                	   var html = '';
	                                	   <sec:authorize access="!hasPermission(#user, 4) && !hasPermission(#user, 256) && hasPermission(#user, 16)">
	                                	   	if (full.status == 'Submitted'){
	                               	   			//html = "<a href='<c:url value='/itineraryPlanning/ItineraryPlan/undo?id='/>"+data+"'><span class='glyphicon glyphicon-repeat' aria-hidden='true'></span></a> ";
	                               	   		}
	                                	   	if (full.status == 'Draft' || full.status == 'Rejected' || full.status == 'Submitted'){
	                                	   		html += "<a href='<c:url value='/itineraryPlanning/ItineraryPlan/edit?id='/>"+data+"'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a> ";
	                                	   	}
	                                	   	if (full.status != 'Approved'){
	                                	   		html += "<a href='javascript:void(0)' onclick='cancelRecordsWithConfirm(\"id="+data+"\")' class='table-btn btn-delete' data-id='"+data+"'><span class='fa fa-times' aria-hidden='true'></span></a> ";
	                                	   	}
	                                	   	else{
	                                	   		html += "<a href='<c:url value='/itineraryPlanning/ItineraryPlan/view?id='/>"+data+"'><span class='glyphicon glyphicon-list' aria-hidden='true'></span></a> ";
	                                	   	}
	                                	   </sec:authorize>	                                	   
	                                	   	<sec:authorize access="hasPermission(#user, 4) or hasPermission(#user, 256)">
	                                	   	if (full.status == 'Submitted'){
	                               	   			//html = "<a href='<c:url value='/itineraryPlanning/ItineraryPlan/undo?id='/>"+data+"'><span class='glyphicon glyphicon-repeat' aria-hidden='true'></span></a> ";
	                               	   		}
	                                	   	if (full.status != 'Approved' || full.status == 'Approved' && (full.timeLogId == null || full.timeLogId == '')){
	                                	   		html += "<a href='<c:url value='/itineraryPlanning/ItineraryPlan/edit?id='/>"+data+"'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a> ";
	                                	   		html += "<a href='javascript:void(0)' onclick='cancelRecordsWithConfirm(\"id="+data+"\")' class='table-btn btn-delete' data-id='"+data+"'><span class='fa fa-times' aria-hidden='true'></span></a> ";
	                                	   	}
	                                	   	else{
	                                	   		html += "<a href='<c:url value='/itineraryPlanning/ItineraryPlan/view?id='/>"+data+"'><span class='glyphicon glyphicon-list' aria-hidden='true'></span></a> ";
	                                	   	}
		                					</sec:authorize>
		                					<sec:authorize access="!hasPermission(#user, 4) && !hasPermission(#user, 16) && !hasPermission(#user, 256)">
		                						html = "<a href='<c:url value='/itineraryPlanning/ItineraryPlan/view?id='/>"+data+"'><span class='glyphicon glyphicon-list' aria-hidden='true'></span></a> ";
		                					</sec:authorize>
	                                	   return html;
	                                   },
	                                   "width" : "60px",
	                                   "className" : "text-left"
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
				
				$('.filters').change(function(){
					$dataTable.DataTable().ajax.reload();
				});
	
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Itinerary Plan Maintenance</h1>
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
								<sec:authorize access="hasPermission(#user, 4) or hasPermission(#user, 256)">
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
								</sec:authorize>
							</div>
							<hr/>
							<table class="table table-striped table-bordered table-hover" id="dataList">
								<thead>
								<tr>
									<th class="text-center action" ><input class="select_all" type="checkbox" /></th>
									<th>Date</th>
									<th>No. of assignment</th>
									<th>Status</th>
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
        </section>		
	</jsp:body>
</t:layout>