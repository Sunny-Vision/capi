<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>		
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
	</jsp:attribute>
	<jsp:attribute name="header">
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<script>
		
			$(document).ready(function(){
				var $dataTable = $("#dataList");
				
				
				$dataTable.DataTable({
					"ordering": true, 
					"order": [[ 0, "desc" ]],
					"searching": true,
					"buttons": [
						<sec:authorize access="(hasPermission(#user, 2) or hasPermission(#user, 8) or hasPermission(#user, 256))">
						{
							"text": "Add",
							"action": function( nButton, oConfig, flash ) {
								window.location = "<c:url value='/assignmentAllocationAndReallocation/AssignmentAllocation/edit'/>";
							}
						},

						</sec:authorize>
					],
					"processing": true,
	                "serverSide": true,
	                //"ajax": "<c:url value='/assignmentAllocationAndReallocation/AssignmentAllocation/query'/>",
	                "ajax": {
	                	url: "<c:url value='/assignmentAllocationAndReallocation/AssignmentAllocation/query'/>",
	                	method: "post"
	                },
	                "columns": [
	                            { "data": "referenceMonthStr" },
	                            { "data": "batchName" },
	                            { "data": function(data){
	                            	var html = "<a href='<c:url value='/assignmentAllocationAndReallocation/AssignmentAllocation/view'/>?id="+data.allocationBatchId+"'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a>";
	                            	
	                            	if (data.status != 2){
	                            		html += " &nbsp;<a href='javascript:void(0)' onclick='deleteRecordsWithConfirm(this)' class='table-btn btn-delete' data-id='"+data.allocationBatchId+"'><span class='fa fa-times' aria-hidden='true'></span></a>";

		                            	<sec:authorize access="(hasPermission(#user, 2))">
		                            		html += "&nbsp;<button type='button' class='btn btn-primary' onclick='approveAllocationBatch(\""+data.allocationBatchId+"\")' >Approve</button>";
		                            	</sec:authorize>
	                            	}
                            	   return html;
	                            } }
	                        ],
	                "columnDefs": [
	                               {
	                                   "targets": [0,1],
	                                   "className" : "text-center"
	                               },
	                               {
	                                   "targets": [2],
	                                   "orderable": false,
	                                   "searchable": false,
	                                   "className" : "text-center"
	                               }
	                               
	                           ]
				});
			});
			
			function deleteRecordsWithConfirm(button){
				var $button = $(button);
				var id = $button.data("id");
				console.log(id);
				bootbox.confirm({
				    title: "Confirmation",
				    message: "<spring:message code='W00001' />",
				    callback: function(result){
				    	if (result === true) {
					    	$.post("<c:url value='/assignmentAllocationAndReallocation/AssignmentAllocation/delete'/>",
								{id: id},
								function(response){
									$("#dataList").DataTable().ajax.reload();
									$("#MessageRibbon").html(response);
								});
				    	}
				    }
				});
			}
			
			function approveAllocationBatch(id){
				$.post("<c:url value='/assignmentAllocationAndReallocation/AssignmentAllocation/approveAssignmentAlllocation'/>",
						{id: id},
						function(result){
							$("#dataList").DataTable().ajax.reload();
							$("#MessageRibbon").html(result);
						});
			}
			
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Assignment Allocation</h1>
        </section>
        
        <section class="content">
        	<div class="row">
				<div class="col-md-12" >
				<!-- content -->
				
						<!-- Itinerary Planning Table -->
						<div class="box" >
							<div class="box-body">
								<div class="clearfix">&nbsp;</div>
								<table class="table table-striped table-bordered table-hover" id="dataList">
									<thead>
									<tr>
										<th>Reference Month</th>
										<th>Allocation Batch</th>
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