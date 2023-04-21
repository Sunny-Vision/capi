<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page session="false" %>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp" %>
	</jsp:attribute>
	<jsp:attribute name="header">
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>		
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>	
		<%@include file="/WEB-INF/views/includes/datepicker.jsp" %>
		<script>
			$(document).ready(function(){
				$("#dataList").DataTable({
					"order": [[ 1, "desc" ]],
					"searching": true,
					"ordering": true,
					"buttons": [],
					"processing": true,
	                "serverSide": true,
	                "ajax": "<c:url value='/dataImportExport/DataImport/query'/>",
	                "columns": [
								
	                            {
	                            	"data": "importExportTaskId",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "startDate",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "taskName",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "finishedDate",
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
	                            	"data": "errorMessage",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            }
	                        ]
				});
				
				$("#dataForm").validate({
					rules : {
						importData : {
							required:true
						},
						taskNo : {
							required:true
						}
					}
				});
				
				
				$(".select2").select2({width: "100%"}).change(function(){
					$(".productCategory").hide();
					$(".surveyMonth").hide();
					$(".effectiveDate").hide();
					$(".cpiBasePeriod").hide();
					if ($(this).val() == 14){
						$(".productCategory").show();
					}
					if ($(this).val() == 20  ||  $(this).val() == 24){
						$(".surveyMonth").show();
					}
					if ($(this).val() == 26){
						$(".effectiveDate").show();
						$(".cpiBasePeriod").show();
					}
					if ($(this).val() == 12){
						$(".cpiBasePeriod").show();
					}
				});
				$(".select2ajax").select2ajax({width: "100%"})
				$('.month-picker').datepicker({
					format: monthFormat,
					startView: "months", 
				    minViewMode: "months",
				    autoclose: true
				});
				$(".date-picker").datepicker({
					autoclose: true
				});
			});
		</script>
		
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Data Import</h1>
        </section>
        
        <section class="content">        	
        	<div class="row">
				<div class="col-md-12">
					<!-- content -->
					<sec:authorize access="hasPermission(#user, 256)">
						<div class="box box-primary">
							<div class="box-body">
								<div class="clearfix">&nbsp;</div>
									<form action="<c:url value="/dataImportExport/DataImport/importFile" />" method="post" enctype="multipart/form-data" class="form-horizontal table-filter" id="dataForm">
										<div class="form-group">
											<label class="col-sm-2 control-label">Data Import Type</label>
											<div class="col-sm-4">
												<select class="form-control select2"  name="taskNo">
													<c:forEach var="item" items="${definitions}">
														<option value="<c:out value="${item.taskNo}" />" <c:if test="${item.taskNo eq dataType}">selected</c:if>>${item.taskName}
													</c:forEach>
												</select>
											</div>
										</div>
										<div class="form-group productCategory" <c:if test="${dataType != 14}">style="display:none"</c:if>>
											<label class="col-sm-2 control-label">Product Type</label>
											<div class="col-sm-4">
												<select class="form-control select2ajax"  name="productGroupId"
													data-ajax-url="<c:url value='/dataImportExport/DataImport/queryProductCategorySelect2'/>" ></select>
											</div>
										</div>
										<div class="form-group effectiveDate" <c:if test="${dataType != 26}">style="display:none"</c:if>>
											<label class="col-sm-2 control-label">Effective Date</label>
											<div class="col-sm-4">
												<div class="input-group">
													<input type="text" class="form-control date-picker" name="effectiveDate" required  />
													<div class="input-group-addon"><i class="fa fa-calendar"></i></div>
												</div>
											</div>
										</div>
										
										<div class="form-group cpiBasePeriod" <c:if test="${dataType != 26 && dataType != 12}">style="display:none"</c:if>>
											<label class="col-sm-2 control-label">CPI Base Period</label>
											<div class="col-sm-4">
												<input type="text" name="cpiBasePeriod" required />
											</div>
										</div>
										
										<div class="form-group surveyMonth" <c:if test="${dataType != 20 && dataType != 24}">style="display:none"</c:if>>
											<label class="col-sm-2 control-label">Reference Month</label>
											<div class="col-sm-4">
												<div class="input-group">
													<input type="text" class="form-control month-picker" name="referenceMonth"  />
													<div class="input-group-addon"><i class="fa fa-calendar"></i></div>
												</div>
											</div>
										</div>
										 
										<div class="form-group">
											<label class="col-sm-2 control-label">File</label>
											<div class="col-sm-4">
												<input type="file" name="importData" />
											</div>
										</div>
										<div class="form-group">
											<div class="col-sm-offset-2 col-sm-4">
												<input type="submit" class="btn btn-primary" value="Import" />
											</div>
										</div>
									</form>
							</div>
						</div>
					</sec:authorize>
					
					<div class="box">
						<!-- /.box-header -->
						<!-- form start -->
						<div class="box-body">
							<div class="clearfix">&nbsp;</div>
							<table class="table table-striped table-bordered table-hover" id="dataList">
								<thead>
									<tr>
										<th>Task Id</th>
										<th>Start Date</th>
										<th>Task Name</th>
										<th>Finished Date</th>
										<th>Status</th>
										<th>Error Message</th>
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
