<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
<sec:authentication property="details" var="userDetail" />
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp"%>
	</jsp:attribute>
	<jsp:attribute name="header">
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp"%>
		<script>
		
			function deleteRecordsWithConfirm(data) {
				bootbox.confirm({
					title:"Confirmation",
					message: "<spring:message code='W00001' />",
					callback: function(result){
						if (result){
							$.post("<c:url value='/timeLogManagement/TimeLogMaintenance/delete'/>",
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
				
				$dataTable.DataTable({
					"ordering": true, 
					"order": [[ 0, "desc" ]],
					"searching": true,
					"buttons": [
						<sec:authorize access="hasPermission(#user, 4) or hasPermission(#user, 16) or hasPermission(#user, 256)">
						{
							"text": "Add",
							"action": function( nButton, oConfig, flash ) {
								window.location = "<c:url value='/timeLogManagement/TimeLogMaintenance/edit'/>";
							}
						},
						</sec:authorize>
					],
					"processing": true,
	                "serverSide": true,
	                "ajax": {
	                	"url": "<c:url value='/timeLogManagement/TimeLogMaintenance/query'/>",
	                	method: "post",
	                	data: function(d) {
	                		<sec:authorize access="hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)">
	                		d.search["userId"] = $('#userId').val();
	                		</sec:authorize>
	                	}
	                },
	                "columns": [
	                            { "data": "date" },
	                            { "data": "officerCode" },
	                            { "data": "officerChineseName" },
	                            { "data": "status" },
	                            { "data": "approvedByCode" },
	                            { "data": "id" }
	                        ],
	                "columnDefs": [
                            	   <sec:authorize access='!(hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256))'>
                            	   {
                            		   	"targets" : [1],
                            	  		"visible" : false,
                            	   },
                            	   {
                           		   		"targets" : [2],
                           	  			"visible" : false,
                           	   		},
                                   </sec:authorize>
        	                        { 	"targets": [4],
        	                        	"render" : function(data, type, full, meta){
                                    		if (full.status == "Approved"){
                                    			return data;
                                    		}
                                    		else{
                                    			return "";
                                    		}
                                   		}
        	                       	},
	                               {
	                                   "targets": [5],
	                                   "orderable": false,
	                                   "searchable": false,
	                                   "render" : function(data, type, full, meta){
	                                	   var html = "<a href='<c:url value='/timeLogManagement/TimeLogMaintenance/view?id='/>"+data+"'><span class='glyphicon glyphicon-list' aria-hidden='true'></span></a> ";
	                                	   var staffCode = "${userDetail.staffCode}";
	                                		   
	                                	   <sec:authorize access="hasPermission(#user, 1) or hasPermission(#user, 2)">
		                                	   	if ((full.status != "Draft" || full.officerCode == staffCode) && full.status != "Voilated") {
			                                	   	html += "<a href='<c:url value='/timeLogManagement/TimeLogMaintenance/edit?id='/>"+data+"'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a> ";
			                                	 }
		                                	   	return html;
	                                	   </sec:authorize>
	                                	   <sec:authorize access="hasPermission(#user, 4)">
	                                	   		if((full.status == 'Submitted' && full.officerCode == staffCode) || (full.status == 'Approved' && full.officerCode == staffCode)){
	                                	   			
	                                	   		}
	                                	   		else if ((full.status != "Draft" || full.officerCode == staffCode) && full.status != "Voilated") {
		                                	   		html += "<a href='<c:url value='/timeLogManagement/TimeLogMaintenance/edit?id='/>"+data+"'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a> ";
		                                		 }
	                                	  		 return html;
	                                	   
	                                	   </sec:authorize>
	                                	   <sec:authorize access="hasPermission(#user, 16)">
		                                	   if (full.status != "Approved" && full.status != "Submitted" && full.status != "Voilated") {
			                                	   	html += "<a href='<c:url value='/timeLogManagement/TimeLogMaintenance/edit?id='/>"+data+"'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a> ";
			                                	   	
			                                	   	if (full.status == "Draft" || full.status == "Rejected") {
			                                			html += " &nbsp;<a href='javascript:void(0)' onclick='deleteRecordsWithConfirm(\"id="+data+"\")' class='table-btn btn-delete' data-id='"+data+"'><span class='fa fa-times' aria-hidden='true'></span></a> ";
				                            		}
			                                	   	
		    									}
		                                	   return html;
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
	                               }],
	                               createdRow: function (row, data, index) {
	       			                if (data.status == 'Rejected') {
	       			                    $(row).addClass('label-danger');
	       			               }
	       			            }
	                          
				});
				
				<sec:authorize access="hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)">
					$('#userId').select2ajax({
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
          <h1>Time Log Maintenance</h1>
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
								<sec:authorize access="hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)">
								<div class="col-md-4">
									<form class="form-inline filter">
										<div class="row row-eq-height">
											<div class="col-md-4">
												<label class="control-label">Field Officer</label>
											</div>
											<div class="col-md-8">
												<div class="select2">
													<select class="form-control form-control select2 filters" id="userId" name="userId"
															data-ajax-url="<c:url value='/timeLogManagement/TimeLogMaintenance/queryOfficerSelect2'/>"></select>
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
									<th>Date</th>
									<th>Field Officer</th>
									<th>Chinese Name</th>
									<th>Status</th>
									<th>Approved By</th>
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