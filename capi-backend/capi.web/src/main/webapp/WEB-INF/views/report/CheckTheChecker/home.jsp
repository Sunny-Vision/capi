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
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker.jsp"%>
		<%@include file="/WEB-INF/views/includes/datejs.jsp"%>
		<script>
			$(document).ready(function(){
				var $dataTable = $("#dataList");
				var svDates = [];
				
				$dataTable.DataTable({
					"order": [[ 0, "desc" ]],
					"searching": true,
					"ordering": true,
					"buttons": [],
					"processing": true,
	                "serverSide": true,
	                "ajax": "<c:url value='/report/CheckTheChecker/query'/>",
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
	                            			return "<a href='<c:url value='/report/CheckTheChecker/downloadFile?id='/>"+full.reportTaskId+"' class='table-btn'><span class='glyphicon glyphicon-download-alt' aria-hidden='true'></span></a>";
	                            		}
	                            		else{
	                            			return data;
	                            		}
                            		}
	                            }
	                        ]
				});
				
				$(".select2").select2({
					placeholder: "",
					allowClear: true,
					closeOnSelect: false
				})
				$('.select2ajax').select2ajax({
					placeholder: "",
					allowClear: true,
					closeOnSelect: false
				});
				
				$('.month-picker').datepicker({
					format: monthFormat,
					startView: "months", 
				    minViewMode: "months"
				});
				
				$("#criteriaForm").validate({
					rules: {
						noOfCases: {
							number: true,
							min: 1
						},
						telNo: {
							number: true
						},
						faxNo: {
							number: true
						}
					}
				});
				
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Check the Checker</h1>
        </section>
        
        <section class="content">
        	<div class="row">
				<div class="col-md-12" >
				<!-- content -->
					<div class="box" >
						<div class="box-body">
							<div class="clearfix">&nbsp;</div>
							<div id="filters">
								<form class="form-horizontal" action="<c:url value="/report/CheckTheChecker/submitReportRequest" />" id="criteriaForm" method="post">
										
										<div class="form-group">
											<label class="col-md-2 control-label">Period:</label>
											<div class="col-md-2">
												<div class="input-group">
													<input type="text" name="refMonth" class="form-control month-picker" value="" required />
													<div class="input-group-addon">
														<i class="fa fa-calendar"></i>
													</div>
												</div>
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-2 control-label">No. of Random Case(s):</label>
											<div class="col-md-5">
												<div class="input-group">
													<input type="text" name="noOfCases" class="form-control" value="" required />
												</div>
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-2 control-label">Tel. No.:</label>
											<div class="col-md-5">
												<div class="input-group">
													<input type="text" name="telNo" class="form-control" value="${previousCriteria.telNo}" />
												</div>
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-2 control-label">Fax No.:</label>
											<div class="col-md-5">
												<div class="input-group">
													<input type="text" name="faxNo" class="form-control" value="${previousCriteria.faxNo}" />
												</div>
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-2 control-label">Name of Signatory:</label>
											<div class="col-md-5">
												<input type="text" name="signatory" class="form-control" value="${previousCriteria.signatory}" />
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-2 control-label">Office Address(In Chinese):</label>
											<div class="col-md-5">
												<textarea class="form-control" rows="5" name="chineseOfficeAddress">${previousCriteria.chineseOfficeAddress}</textarea>
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-2 control-label">Office Address(In English):</label>
											<div class="col-md-5">
												<textarea class="form-control" rows="5" name="englishOfficeAddress">${previousCriteria.englishOfficeAddress}</textarea>
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