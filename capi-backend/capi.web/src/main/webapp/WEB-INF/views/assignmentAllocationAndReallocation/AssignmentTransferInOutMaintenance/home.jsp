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
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp"%>
	</jsp:attribute>
	<jsp:attribute name="header">
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker.jsp"%>
		<script>
			$(document).ready(function(){
				$('.month-picker').datepicker({
					format: "mm-yyyy",
					startView: "months", 
				    minViewMode: "months",
				    autoclose: true
				});
				
				var $dataTable = $("#dataList");

				var columnDefs = [
		                           {
		                               "targets": "action",
		                               "orderable": false,
		                               "searchable": false
		                           },
		                           {
		                        	   "targets": "_all",
		                        	   "className" : "text-center"
		                           }
									];
				
				var buttons = [];
				
				$dataTable.DataTable({
					"ordering": true, 
					"order": [[ 0, "asc" ]],
					"searching": true,
					"buttons": [],
					"processing": true,
		            "serverSide": true,
		            "ajax": {
		            	url: "<c:url value='/assignmentAllocationAndReallocation/AssignmentTransferInOutMaintenance/query'/>",
		            	data: function(dataModel) {
		            		dataModel.referenceMonth = $('[name="referenceMonth"]').val();
		            		dataModel.fromUserId = $('[name="fromUserId"]').val();
		            	},
		            	method: 'post'
		            },
		            "columns": [
		                        { "data": "fromFieldOfficer" },
		                        { "data": "targetFieldOfficer" },
		                        { "data": "targetReleaseManDay" },
		                        { "data": "actualReleaseManDayString",
		                        	"render": function(data) {
		                        		if (data == null) data = '0';
		                        		
		                        		var color = '';
		                        		if (+data > 0)
		                        			color = 'red';
		                        		else if (+data > -0.5 && +data < 0)
		                        			color = 'green';
		                        		
		                        		return '<span style="color:' + color + '">' + data + '</span>';
		                        	}
		                        },
		                        { "data": "status" },
		                        { "data": "id",
		                        	"render": function(data) {
		                        		return "<a href='<c:url value='/assignmentAllocationAndReallocation/AssignmentTransferInOutMaintenance/edit?id='/>"+data+"' class='table-btn'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a>";
		                        	}
		                        }
								],
		            "columnDefs": columnDefs
				});
				
				$('[name="fromUserId"]').select2ajax();
				
				$('.filters').change(function(){
					$dataTable.DataTable().ajax.reload();
				});
			});
			
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Assignment Transfer-in/out Maintenance</h1>
        </section>
        
        <section class="content">
        	<div class="row">
				<div class="col-md-12" >
					<!-- content -->
					<div class="box" >
						<div class="box-body">
							<div class="form-horizontal">
								<div class="row">
									<label class="control-label col-md-2">Reference Month</label>
									<div class="col-md-2">
       									<div class="input-group">
       										<input type="text" name="referenceMonth" class="form-control month-picker filters" maxlength="7" required>
       										<div class="input-group-addon">
       											<i class="fa fa-calendar"></i>
       										</div>
       									</div>
									</div>
									
									<sec:authorize access="hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4)">
									<label class="control-label col-md-2">From Field Officer</label>
									<div class="col-md-3">
										<select name="fromUserId" class="form-control select2 filters"
											data-ajax-url="<c:url value='/assignmentAllocationAndReallocation/AssignmentTransferInOutMaintenance/queryOfficerSelect2'/>"
											data-allow-clear="true"
											data-placeholder=""></select>
									</div>
									</sec:authorize>
								</div>
							</div>
							
							<hr/>
							<table class="table table-striped table-bordered table-hover" id="dataList">
								<thead>
								<tr>
									<th>From Field Officer</th>
									<th>Target Field Officer</th>
									<th>Target Release Man-Day</th>
									<th>Actual Release Man-Day</th>
									<th>Status</th>
									<th class="text-center action"></th>
								</tr>
								</thead>
							</table>
						</div>
					</div>
				</div>
			</div>
        </section>
	</jsp:body>
</t:layout>