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
				$('#referenceMonth').datepicker({
					autoclose: true
				})
				
				$('.date-picker:not([readonly])').datepicker("setDate", new Date());
				
				if ('<c:out value='${referenceMonth}'/>' != '') {
					$("#referenceMonth").datepicker("update", "<c:out value='${referenceMonth}'/>");
				}
				
						
				var $dataTable = $("#dataList");
				
				$dataTable.DataTable({
					"order": [[ 0, "desc" ]],
					"buttons": [],
					"searching": true,
					"ordering": true,
					"processing": true,
	                "serverSide": true,
	                "ajax": {
	                	"url": "<c:url value='/qualityControlManagement/PostEnumerationCheckListMaintenance/query'/>",
	                	method: "post",
	                	data: function(d) {
	                		d.search["referenceMonth"] = $('#referenceMonth').val();
	                	}
	                },
	                "columns": [
								{
									"data": "team",
									"orderable": true,
									"searchable": true,
									"className" : "text-center"
								},
								{
									"data": "officerCode",
									"orderable": true,
									"searchable": true,
									"className" : "text-center"
								},
	                            {
	                            	"data": "officerName",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "total",
	                            	"orderable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "excluded",
	                            	"orderable": false,
	                            	"className" : "text-center"
                            	},
	                            {
	                            	"data": "checked",
	                            	"orderable": true,
	                            	"render" : function(data, type, full, meta){
	                            		
	                            		var percentage = 0;
	                            		if (full.approved != 0){
	                            			percentage = Math.round(full.checked/full.approved*1000)/10
	                            		}
	                            		
	                            		var html = full.checked+" (" + percentage+ "%)";
	                            		return html;
                            		},
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "selected",
	                            	"orderable": true,
	                            	"render" : function(data, type, full, meta){
	                            		var percentage = 0;
	                            		if (full.approved != 0){
	                            			percentage = Math.round(full.selected/full.approved*1000)/10
	                            		}
	                            		var html = full.selected+" (" + percentage + "%)";
	                            	                            		return html;
                            		},
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "approved",
	                            	"orderable": true,
	                            	"className" : "text-center"
                            	},
	                            {
	                            	"data": "nonContact",
	                            	"orderable": true,
	                            	"className" : "text-center"
                            	},
	                            {
	                            	"data": "userId",
	                            	"orderable": false,
	                                "render" : function(data, type, full, meta){
	                                   var html = "<a href='javascript:void(0)' onclick='editPECheckList("+data+")'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a> ";
	                                   return html;
		                            },
		                            "width" : "60px",
	                            	"className" : "text-center"
                            	}        	
	                        ],
					"rowCallback": function( row, data ) {
						if ( data[ 'isFieldTeamHead' ] == 1 ) {
							$('td', row).css('background-color', 'LightPink');
						} else if ( data[ 'isSectionHead' ] == 1 ) {
							$('td', row).css('background-color', 'LightSkyBlue');
						}
					} 
				});
				
				
				$('.date-picker:not([readonly])').on("change", function() {
					$dataTable.DataTable().ajax.reload();
				});
				
			});
			

			function editPECheckList(userId){
				var referenceMonth = $("#referenceMonth").val();
				
				window.location.href="<c:url value='/qualityControlManagement/PostEnumerationCheckListMaintenance/edit?userId='/>"+userId+"&referenceMonth="+referenceMonth
				
			}
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Post-Enumeration Check List Maintenance</h1>
        </section>
        <section class="content">
        	<div class="row">
				<div class="col-md-12" >
					<!-- content -->
					<div class="box" >
						<div class="box-body">
							<div class="clearfix">&nbsp;</div>
							<div class="row">
								<div class="col-md-4">
									<form class="form-inline filter">
										<div class="row row-eq-height">
											<div class="col-md-4">
												<label class="control-label">Survey Month:</label>
											</div>
											<div class="col-md-8">
	       							   			<div class="input-group">
													<input type="text" class="form-control date-picker" maxlength="7" data-date-min-view-mode="months" data-date-format="mm-yyyy"
														id="referenceMonth" name="referenceMonth" required />
													<div class="input-group-addon">
														<i class="fa fa-calendar"></i>
													</div>
												</div>												
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
									<th>Team</th>
									<th>Field Officer</th>
									<th>Chinese Name</th>
									<th>Allocated Assignments</th>
									<th>Base Assignments</th>
									<th>Checked Assignments</th>
									<th>Selected Assignments</th>
									<th>Approved Base Assignments</th>
									<th>No. Of Non Contact</th>
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
