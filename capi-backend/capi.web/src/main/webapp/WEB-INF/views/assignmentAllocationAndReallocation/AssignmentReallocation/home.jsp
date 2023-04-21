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
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
	</jsp:attribute>
	<jsp:attribute name="header">
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
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
				$("#chkAll").on('change', function(){
		    		if (this.checked){
		    			$('.tblChk').prop('checked', true);
		    		}
		    		else{
		    			$('.tblChk').prop('checked', false);
		    		}
		    	});
		    	
				var $dataTable = $("#dataList");				
				
				$dataTable.DataTable({
					"order": [[ 0, "desc" ]],
					"searching": true,
					"ordering": true,
					<sec:authorize access="!(hasPermission(#user, 4) or hasPermission(#user, 8) or hasPermission(#user, 16) or hasPermission(#user, 256))">
					"buttons": [],
					</sec:authorize>
					<sec:authorize access="hasPermission(#user, 4) or hasPermission(#user, 8) or hasPermission(#user, 16) or hasPermission(#user, 256)">
					"buttons": [
						{
							"text": "Change Person",
							"action": function( nButton, oConfig, flash ) {
								window.location = "<c:url value='/assignmentAllocationAndReallocation/AssignmentReallocation/changePerson'/>";
							}
						}
						<sec:authorize access="hasPermission(#user, 8) or hasPermission(#user, 256)">
						,
						{
							"text": "Change Date",
							"action": function( nButton, oConfig, flash ) {
								window.location = "<c:url value='/assignmentAllocationAndReallocation/AssignmentReallocation/changeDate'/>";
							}
						}
						</sec:authorize>
					],
					</sec:authorize>
					"processing": true,
	                "serverSide": true,
	                //"ajax": "<c:url value='/assignmentAllocationAndReallocation/AssignmentReallocation/query'/>",
	                "ajax": {
	                	url: "<c:url value='/assignmentAllocationAndReallocation/AssignmentReallocation/query'/>",
	                	method: "post"
	                },
	                "columns": [{
			                    	"data": "createdDate",
			                    	"orderable": true,
			                    	"searchable": true,
			                    	"className" : "text-center"
			                    },
	                            {
	                            	"data": "originalUser",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "targetUser",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "status",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "assignmentReallocationId",
	                            	"orderable": false,
	                            	"searchable": false,
	                            	"render" : function(data, type, full, meta){
	                            		if("Rejected" == full.status) {
		                            		var html = "<a href='<c:url value='/assignmentAllocationAndReallocation/AssignmentReallocation/changePerson?act=edit&id='/>"+data+"' class='table-btn'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a>";
		                            		return html;
	                            		} else {
	                            			var html = "<a href='<c:url value='/assignmentAllocationAndReallocation/AssignmentReallocation/view?id='/>"+data+"' class='table-btn'><span class='glyphicon glyphicon-list' aria-hidden='true'></span></a>"
	                            			return html;
	                            		}
                            		},
	                            	"className" : "text-center"
                            	}
	                        ]
				});
			});
			
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Assignment Reallocation</h1>
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
										<th>Created date</th>
										<th>Original User</th>
										<th>Target User</th>
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

