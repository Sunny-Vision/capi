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
		</style>
		<script>
		function deleteRecordsWithConfirm(data) {
			bootbox.confirm({
				title:"Confirmation",
				message: "<spring:message code='W00001' />",
				callback: function(result){
					if (result){
						$.post("<c:url value='/qualityControlManagement/SupervisoryVisitPlanMaintenance/delete'/>",
							data,
							function(response) {
								$("#dataList").DataTable().ajax.reload();
								$("#MessageRibbon").html(response);
							}
						);
					}
				}
			})
// 			if (!confirm('<spring:message code="W00001" />')) return;
// 			$.post("<c:url value='/qualityControlManagement/SupervisoryVisitPlanMaintenance/delete'/>",
// 				data,
// 				function(response) {
// 					$("#dataList").DataTable().ajax.reload();
// 					$("#MessageRibbon").html(response);
// 				}
// 			);
		}
	
			$(document).ready(function(){
				var $dataTable = $("#dataList");
				
				$dataTable.DataTable({
					"order": [[ 1, "desc" ]],
					"searching": true,
					"ordering": true,
					<sec:authorize access="!(hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256))">
					"buttons": [],
					</sec:authorize>
					<sec:authorize access="hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)">
					"buttons": [
						{
							"text": "Add",
							"action": function( nButton, oConfig, flash ) {
								window.location = "<c:url value='/qualityControlManagement/SupervisoryVisitPlanMaintenance/edit?act=add'/>";
							}
						},
						{
							"text": "Delete",
							"action": function( nButton, oConfig, flash ) {
								var data = $dataTable.find(':checked').serialize();
								if (data == '') {
									bootbox.alert({
										title: "Alert",
										message: "<spring:message code='E00009' />"
									});
									return;
								}
								
								deleteRecordsWithConfirm(data);
							}
						}
					],
					</sec:authorize>
					"processing": true,
	                "serverSide": true,
	                "ajax": {
	                	url: "<c:url value='/qualityControlManagement/SupervisoryVisitPlanMaintenance/query'/>",
	                	method: "post",
	                	data: function(d) {
	                		d.qcType = $('#qcType').val();
	                	}
	                },
	                "columns": [
								{
									"data": "id",
									"orderable": false,
									"searchable": false,
									"render" : function(data, type, full, meta){
										if (full.deletable){
											return "<input type='checkbox' value='"+data+"' name='id' class='tblChk' />";
										} else {
											return "";
										}
									},
									"className" : "discount text-center"
								},
	                            {
	                            	"data": "visitDate",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "staffCode",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "chineseName",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "englishName",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "team",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "supervisor",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "checker",
	                            	"orderable": true,
	                            	"searchable": false,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "qcType",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "id",
	                            	"orderable": false,
	                            	"searchable": false,
	                            	"render" : function(data, type, full, meta){
	                            		if (!full.mandatoryPlan){
		                            		var html = "<a href='<c:url value='/qualityControlManagement/SupervisoryVisitPlanMaintenance/edit?act=edit&id='/>"+data+"' class='table-btn'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a>";
		                            		<sec:authorize access="hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)">
		                            		if (full.deletable){
		                            			html += " &nbsp;<a href='javascript:void(0)' onclick='deleteRecordsWithConfirm(\"id="+data+"\")' class='table-btn btn-delete' data-id='"+data+"'><span class='fa fa-times' aria-hidden='true'></span></a>";
		                            		}
		                					</sec:authorize>
		                            		return html;
	                            		}
	                            		return "";
                            		},
	                            	"className" : "text-center"
                            	}
	                        ]
				});
				
				$('.filters').change(function(){
					$dataTable.DataTable().ajax.reload();
				});
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>SC/SV Plan Maintenance</h1>
        </section>
        
        <section class="content">
        	<div class="row">
				<div class="col-md-12" >
				<!-- content -->
					<div class="box" >
						<div class="box-body">
							<div class="clearfix">&nbsp;</div><form class="form-inline table-filter">
								<div class="form-group">
									<label>QC Type</label>
									<select class="form-control filters" id="qcType">
										<option value=""></option>
										<option value="1">SC</option>
										<option value="2">SV</option>
									</select>
								</div>
							</form>
							<hr/>
							<table class="table table-striped table-bordered table-hover" id="dataList">
								<thead>
								<tr>
									<th class="text-center action" ><input class="select_all" type="checkbox" /></th>
									<th>SC/SV Date</th>
									<th>Staff Code</th>
									<th>Chinese Name</th>
									<th>English Name</th>
									<th>Team</th>
									<th>Supervisor</th>
									<th>Checker</th>
									<th>QC Type</th>
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