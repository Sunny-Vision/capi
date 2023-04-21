<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp" %>		
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp"%>
	</jsp:attribute>
	<jsp:attribute name="header">
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>		
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker.jsp"%>
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
				
				$('.date-picker:not([readonly])').datepicker({
					autoclose: true
				});		
				var $dataTable = $("#dataList");
				
				$dataTable.DataTable({
					"order": [[ 0, "desc" ]],
					"buttons": [],
					"searching": true,
					"ordering": true,
					"processing": true,
	                "serverSide": true,
	                "ajax": {
	                	"url": "<c:url value='/qualityControlManagement/PostEnumerationCheckMaintenance/query'/>",
	                	method: "post",
	                	data: function(d) {
	                		d.referenceMonth = $('#referenceMonth').val();
	                		d.certaintyCase = $('#certaintyCase').val();
	                		d.roleHeader = $('#roleHeader').val();
	                	}
	                },
	                "columns": [
								{
									"data": "surveyMonth",
									"orderable": true,
									"searchable": true,
									"className" : "text-center"
								},
								{
									"data": "referenceNo",
									"orderable": true,
									"searchable": true,
									"className" : "text-center"
								},
								{
									"data": "outletName",
									"orderable": true,
									"searchable": true,
									"className" : "text-center"
								},
								{
									"data": "userStaffCode",
									"orderable": true,
									"searchable": true,
									"className" : "text-center"
								},
								{
									"data": "userChineseName",
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
	                            	"data": "modifiedDate",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "modifiedBy",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            { "data": "isCertaintyCase",
	                            	"orderable": true,
		                            "searchable": false,
		                            "render": function(data) {
		                            	if(data == true){
		                            		return '<i class="fa fa-check"></i>';
		                            	}
		                        		return '';
		                            },
		                            "className" : "text-center"
		                        },
		                        { "data": "isSectionHead",
		                        	"orderable": true,
		                            "searchable": false,
		                            "render": function(data) {
		                            	if(data == true){
		                            		return '<i class="fa fa-check"></i>';
		                            	}
		                        		return '';
		                            },
		                            "className" : "text-center"
		                        },
		                        { "data": "isFieldTeamHead",
		                        	"orderable": true,
		                            "searchable": false,
		                            "render": function(data) {
		                            	if(data == true){
		                            		return '<i class="fa fa-check"></i>';
		                            	}
		                        		return '';
		                            },
		                            "className" : "text-center"
		                        },
	                            {
	                            	"data": "peCheckFormId",
	                            	"orderable": false,
	                                "render" : function(data, type, full, meta){
                                	   var html = "<a href='<c:url value='/qualityControlManagement/PostEnumerationCheckMaintenance/edit?peCheckFormId='/>" + data + "'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a> ";
                                	   return html;
	                                },
	                                "width" : "60px",
	                            	"className" : "text-center"
                            	}
	                        ]
				});
				
				$('.date-picker:not([readonly])').on("changeDate", function() {
					$dataTable.DataTable().ajax.reload();
				});
				
				$('.filters').change(function(){
					$dataTable.DataTable().ajax.reload();
				});
				
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Post-Enumeration Check Maintenance</h1>
        </section>
        <section class="content">
        	<div class="row">
				<div class="col-md-12" >
					<!-- content -->
					<div class="box" >
						<div class="box-body">
							<div class="clearfix">&nbsp;</div>
							<div class="row">
								<div class="col-md-12">
									<form class="form-horizontal">
										<div class="form-group">
											<label class="col-sm-2 control-label">Survey Month:</label>
											<div class="col-md-2">
												<div class="input-group">
													<input type="text" class="form-control date-picker filters" maxlength="7" data-date-min-view-mode="months" data-date-format="mm-yyyy"
														id="referenceMonth" name="referenceMonth" />
													<div class="input-group-addon">
														<i class="fa fa-calendar"></i>
													</div>
												</div>												
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-2 control-label">Is Certainty Case</label>
											<div class="col-md-3">
												<select class="form-control filters" id="certaintyCase" name="certaintyCase">
													<option value=""></option>
													<option value="Y">Y</option>
													<option value="N">N</option>
												</select>
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-2 control-label">Conducted by Section Head / Field Team Head</label>
											<div class="col-md-3">
												<select class="form-control filters" id="roleHeader" name="roleHeader">
													<option value=""></option>
													<option value="1">Section Head</option>
													<option value="2">Field Team Head</option>
												</select>
											</div>
										</div>
									</form>
								</div>
							</div>
							<hr/>
							<div class="clearfix">&nbsp;</div>
							<table class="table table-striped table-bordered table-hover" id="dataList">
								<thead>
								<tr>
									<th>Survey Month</th>
									<th>Reference No.</th>
									<th>Firm Name</th>
									<th>Responsible Officer</th>
									<th>Chinese Name</th>
									<th>PE Check Status</th>
									<th>Last Modified Time</th>
									<th>Modified By</th>
									<th>Certainty Case</th>
									<th>Section Head</th>
									<th>Field Team Head</th>
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
