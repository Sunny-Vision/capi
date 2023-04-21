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
		<style>
		</style>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker.jsp"%>
		<%@include file="/WEB-INF/views/includes/select2.jsp"%>
		<script>
			function cancelRecordsWithConfirm(data) {
				bootbox.confirm({
					title:"Confirmation",
					message: "<spring:message code='W00006' />",
					callback: function(result){
						if (result){
							$.post("<c:url value='/itineraryPlanning/QCItineraryPlan/delete'/>",
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
				var button = []
				<sec:authorize access="hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)">	
				button= [
							{
								"text": "Add",
								"action": function( nButton, oConfig, flash ) {
									window.location = "<c:url value='/itineraryPlanning/QCItineraryPlan/edit'/>";
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
						];
				</sec:authorize>
				
				$dataTable.DataTable({
					"ordering": true, 
					"order": [[ 1, "desc" ]],
					"searching": true,
					"buttons": button,
					"processing": true,
	                "serverSide": true,
	                "ajax": {
	                	"url": "<c:url value='/itineraryPlanning/QCItineraryPlan/query'/>",
	                	method: "post"
	                },
	                "columns": [
	                            { "data": "id" },
	                            { "data": "date" },
	                            { "data" : "svCount" },
	                            { "data" : "scCount" },
	                            { "data" : "peCount" },
	                            { "data": "status" },
	                            { "data": "id" }
	                        ],
	                "columnDefs": [

	                               {
	                                   "targets": [0],
	                                   "orderable": false,
	                                   "searchable": false,
	                                   "render" : function(data, type, full, meta){
	                                       return "<input type='checkbox' name='id' value='"+data+"'/>";
	                                   },
	                                   "className" : "text-center"
	                               },
	                               {

	                            	   "targets": [6],
	                                   "orderable": false,
	                                   "searchable": false,
	                                   "render" : function(data, type, full, meta){
	                                	   var html = "";
	                                	  	
	                                		<sec:authorize access="hasPermission(#user, 1) or hasPermission(#user, 2)">
		                                		html += "<a href='<c:url value='/itineraryPlanning/QCItineraryPlan/edit?id='/>"+data+"'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a> ";
		                                		if (full.status != "Approved") {
		                                			html += " &nbsp;<a href='javascript:void(0)' onclick='cancelRecordsWithConfirm(\"id="+data+"\")' class='table-btn btn-delete' data-id='"+data+"'><span class='fa fa-times' aria-hidden='true'></span></a> ";
		                                		}
		                            		</sec:authorize>
		                            		<sec:authorize access="!hasPermission(#user, 1) && !hasPermission(#user, 2) && hasPermission(#user, 4)">
		                            		if (full.status == 'Draft' || full.status == 'Rejected' || full.status == 'Submitted') {
	                                			html += "<a href='<c:url value='/itineraryPlanning/QCItineraryPlan/edit?id='/>"+data+"'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a> ";
	                                		}
		                            		if (full.status != 'Approved'){
		                            			html += " &nbsp;<a href='javascript:void(0)' onclick='cancelRecordsWithConfirm(\"id="+data+"\")' class='table-btn btn-delete' data-id='"+data+"'><span class='fa fa-times' aria-hidden='true'></span></a> ";
		                            		} else {
		                            			html += "<a href='<c:url value='/itineraryPlanning/QCItineraryPlan/view?id='/>"+data+"'><span class='glyphicon glyphicon-list' aria-hidden='true'></span></a> ";
		                            		}
	                            		</sec:authorize>
	                                	   return html;
	                                   },
	                                   "className" : "text-left ",
	                                   "width": "100px"
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
				
				<sec:authorize access="hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)">
					$('#fieldOfficerId').select2({
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
          <h1>Field Supervisor Itinerary Plan Maintenance</h1>
        </section>   
        <section class="content">
        	<div class="row">
				<div class="col-md-12" >
				<!-- content -->
					<!-- Itinerary Planning Table -->
					<div class="box" >
						<div class="box-body">
							<table class="table table-striped table-bordered table-hover" id="dataList">
								<thead>
								<tr>
									<th class="text-center action" ><input class="select_all" type="checkbox" /></th>
									<th>Date</th>
									<th>No. of Supervisory Visit</th>
									<th>No. of Spot Check</th>
									<th>No. of PE Check</th>
									<th>Status</th>
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