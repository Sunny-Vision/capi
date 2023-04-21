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
					"order": [[ 2, "desc" ]],
					"searching": true,
					"buttons": [
								<c:if test="${!isFreezedSurveyMonth}">
						{
							
							"text": "Add",
							"action": function( nButton, oConfig, flash ) {
								window.location = "<c:url value='/assignmentAllocationAndReallocation/SurveyMonth/edit'/>";
							}

						},
						</c:if>
					],
					"processing": true,
	                "serverSide": true,
	                //"ajax": "<c:url value='/assignmentAllocationAndReallocation/SurveyMonth/query'/>",
	                "ajax": {
	                	url: "<c:url value='/assignmentAllocationAndReallocation/SurveyMonth/query'/>",
	                	method: "post"
	                },
	                "columns": [
	                            { "data": "referenceMonthStr" },
	                            { "data": "startDateStr" },
	                            { "data": "endDateStr" },
	                            { "data": "closingDateStr" },
	                            { "data": function(data){
	                            	switch(parseInt(data.status)){	                            		
		                            	case 1:
		                            		return "Pending";
		                            	case 2:
		                            		return "In Progress";
		                            	case 3:
		                            		return "Finished";		                            		
		                            	case 4:
		                            		return "Failed";
		                            	case 5:
		                            		return "Approved";
		                            	case 6:
		                            		return "Created";
		                            	case 7:
		                            		return "Draft";
		                            	default:
	                            			return "";
	                            	}
	                            } },
	                            { "data": function(data){
	                            	var html = ""
                            		if (data.status == 6) {
                            			<c:if test="${!isFreezedSurveyMonth}">
                            			html += "<a href='<c:url value='/assignmentAllocationAndReallocation/SurveyMonth/edit?id='/>"+data.surveyMonthId+"'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a>";

		                            	
		                            	if(data.removable){
	                            			html += " &nbsp;<a href='javascript:void(0)' onclick='deleteRecordsWithConfirm(this)' class='table-btn btn-delete' data-id='"+data.surveyMonthId+"'><span class='fa fa-times' aria-hidden='true'></span></a>";
		                            	}
		                            	</c:if>
		                            	
                            			<sec:authorize access="hasPermission(#user, 2)">
		                            		html += "&nbsp;<button type='button' class='btn btn-primary' onclick='approveSurveyMonth(\""+data.surveyMonthId+"\")' >Approve</button>";
		                            	</sec:authorize>
	                            	}
	                            	if (data.status == 4) {
	                            		<c:if test="${!isFreezedSurveyMonth}">
                            			html += "<a href='<c:url value='/assignmentAllocationAndReallocation/SurveyMonth/edit?id='/>"+data.surveyMonthId+"'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a>";

		                            	
		                            	if(data.removable){
	                            			html += " &nbsp;<a href='javascript:void(0)' onclick='deleteRecordsWithConfirm(this)' class='table-btn btn-delete' data-id='"+data.surveyMonthId+"'><span class='fa fa-times' aria-hidden='true'></span></a>";
		                            	}
		                            	</c:if>
	                            		
	                            		<sec:authorize access="hasPermission(#user, 2)">
		                            		html += "&nbsp;<button type='button' class='btn btn-primary' onclick='approveSurveyMonth(\""+data.surveyMonthId+"\")' >Approve</button>";
	                            		</sec:authorize>
	                            	}
	                            	if (data.status == 5){
	                            		<c:if test="${!isFreezedSurveyMonth}">
                            			html += "<a href='<c:url value='/assignmentAllocationAndReallocation/SurveyMonth/edit?id='/>"+data.surveyMonthId+"'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a>";
                            			</c:if>
	                            	}
                            		if (data.status == 7) {
                            			<c:if test="${!isFreezedSurveyMonth}">
                            			html += "<a href='<c:url value='/assignmentAllocationAndReallocation/SurveyMonth/edit?id='/>"+data.surveyMonthId+"'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a>";

		                            	if(data.removable){
	                            			html += " &nbsp;<a href='javascript:void(0)' onclick='deleteRecordsWithConfirm(this)' class='table-btn btn-delete' data-id='"+data.surveyMonthId+"'><span class='fa fa-times' aria-hidden='true'></span></a>";
		                            	}
		                            	</c:if>
	                            	}
                            	   return html;
	                            } }
	                        ],
	                "columnDefs": [
	                               {
	                            	   "targets": [0],
	                                   "className" : "text-center"
	                               },
	                               {
	                                   "targets": [1],
	                                   "className" : "text-center"
	                               },
	                               {
	                                   "targets": [2],
	                                   "className" : "text-center"
	                               },
	                               {
	                                   "targets": [3],
	                                   "className" : "text-center"
	                               },
	                               {
	                                   "targets": [4],
	                                   "className" : "text-center"
	                               },
	                               {
	                                   "targets": [5],
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
				    	if(result === true){
					    	window.location = "<c:url value='/assignmentAllocationAndReallocation/SurveyMonth/delete'/>"+"?id="+id;
				    	}
				    }
				});	
			}
			
			function approveSurveyMonth(id){
				bootbox.confirm({
				    title: "Confirmation",
				    message: "<spring:message code='W00038' />",
				    callback: function(result){
				    	if(result === true){
							$.post("<c:url value='/assignmentAllocationAndReallocation/SurveyMonth/approveSurveyMonth'/>",
								{id: id},
								function(response) {
									$("#dataList").DataTable().ajax.reload();
									$("#MessageRibbon").html(response);
								}
							);
				    	}
				    }
				});
			}
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Survey Month Maintenance</h1>
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
										<th>Start Date</th>
										<th>End Date</th>
										<th>Closing Date</th>
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