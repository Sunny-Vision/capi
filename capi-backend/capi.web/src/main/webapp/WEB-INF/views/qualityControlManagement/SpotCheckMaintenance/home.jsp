<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/select2-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
	</jsp:attribute>
	<jsp:attribute name="header">
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<style>
		#dataList .discount span {
			display: inline-block;
			margin: 0 2px;
		}
		#dataList .discount span.number {
			border: solid 1px #d2d6de;
			padding: 0 20px;
		}
		.filter {
			margin-bottom: 10px;
		}
		.filter .form-group {
			margin-right: 10px;
		}
		.filter .form-control {
			margin-left: 10px;
		}
		.filter .form-control.select2 {
			width: 250px;
		}
		</style>
		<script>
			$(document).ready(function(){
				var $dataTable = $("#dataList");				
				
				$dataTable.DataTable({
					"order": [[ 0, "desc" ]],
					"searching": true,
					"ordering": true,
					<sec:authorize access="!(hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256))">
					"buttons": [],
					</sec:authorize>
					<sec:authorize access="hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)">
					"buttons": [],
					</sec:authorize>
					"processing": true,
	                "serverSide": true,
	                //"ajax": "<c:url value='/qualityControlManagement/SpotCheckMaintenance/query'/>",
	                "ajax": {
	                	url: "<c:url value='/qualityControlManagement/SpotCheckMaintenance/query'/>",
	                	method: "post"
	                },
	                "columns": [
								{
									"data": "officerCode",
									"orderable": true,
									"searchable": true,
									"className" : "discount text-center"
								},
	                            {
	                            	"data": "officerName",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            <sec:authorize access="hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 256) or hasPermission(#user, 2048)">
	                            {
	                            	"data": "supervisorCode",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            </sec:authorize>
	                            {
	                            	"data": "spotCheckDate",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "checkerCode",
	                            	"orderable": true,
	                            	"searchable": false,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "status",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "spotCheckFormId",
	                            	"orderable": false,
	                            	"searchable": false,
	                            	"render" : function(data, type, full, meta){
	                            		<sec:authorize access="!(hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 256))">
	                            		if("Draft" == full.status || "Rejected" == full.status || full.status == null) {
		                            		var html = "<a href='<c:url value='/qualityControlManagement/SpotCheckMaintenance/edit'/>?act=edit&id="+data+"&officerId="+full.officerId+"&officerCode="+full.officerCode+"&spotCheckDate="+full.spotCheckDate+"' class='table-btn'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a>";
		                            		return html;
	                            		} else {
	                            			return null;
	                            		}
	                            		</sec:authorize>
	                            		<sec:authorize access="hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 256)">
	                            		if("Approved" == full.status || "Submitted" == full.status || "Draft" == full.status || "Rejected" == full.status || full.status == null) {
		                            		var html = "<a href='<c:url value='/qualityControlManagement/SpotCheckMaintenance/edit'/>?act=edit&id="+data+"&officerId="+full.officerId+"&officerCode="+full.officerCode+"&spotCheckDate="+full.spotCheckDate+"' class='table-btn'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a>";
		                            		return html;
	                            		} else {
	                            			return null;
	                            		}
	                            		</sec:authorize>
	                            		
                            		},
	                            	"className" : "text-center"
                            	}
	                        ]
				});

				/*$('.form-control').change(function(){
					$dataTable.DataTable().ajax.reload();
				});*/
			});
			
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Spot Check Maintenance</h1>
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
										<th>Field Officer</th>
										<th>Chinese Name</th>
										<sec:authorize access="hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 256) or hasPermission(#user, 2048)">
											<th>Field Supervisor</th>
										</sec:authorize>
										<th>Spot Check Date</th>
										<th>Checker</th>
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
