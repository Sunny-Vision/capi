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
			function deleteRecordsWithConfirm(data) {
				var tpusCount = 0;
				var id = [];
				
				$.each(data, function(i, o){
					tpusCount +=  Number(o.tpus);
					id.push(o.id);
				});
				
				var message = ( tpusCount > 0 ) ? "<spring:message code='W00041' />".replace('{0}', tpusCount) : "<spring:message code='W00001' />"
				
				bootbox.confirm({
					title:"Confirmation",
					message: message,
					callback: function(result){
						if (result){
							$.post("<c:url value='/masterMaintenance/DistrictMaintenance/delete'/>",
								{id: id},
								function(response) {
									$("#dataList").DataTable().ajax.reload();
									$("#MessageRibbon").html(response);
								}
							);
						}
					}
				})
				/*if (!confirm('<spring:message code="W00001" />')) return;
				$.post("<c:url value='/masterMaintenance/DistrictMaintenance/delete'/>",
					data,
					function(response) {
						$("#dataList").DataTable().ajax.reload();
						$("#MessageRibbon").html(response);
					}
				);*/
			}
		
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
					"order": [[ 1, "desc" ]],
					"searching": true,
					"ordering": true,
					<sec:authorize access="!hasPermission(#user, 256)">
					"buttons": [],
					</sec:authorize>
					<sec:authorize access="hasPermission(#user, 256)">
					"buttons": [
						{
							"text": "Add",
							"action": function( nButton, oConfig, flash ) {
								window.location = "<c:url value='/masterMaintenance/DistrictMaintenance/edit?act=add'/>";
							}
						},
						{
							"text": "Delete",
							"action": function( nButton, oConfig, flash ) {
								var dataSet = $dataTable.find(':checked').map(function(i, o){
									var id = $(o).val();
									var tpus = $(o).parents('tr').children('.tpus').html();
									if( $(o).attr('id') === "chkAll" ) { 
										return; 
									}
									
									return {id: id, tpus: tpus};
								})

								if (dataSet.length === 0) {
									bootbox.alert({
										title: "Alert",
										message: "<spring:message code='E00009' />"
									});
									return;
								}
								deleteRecordsWithConfirm(dataSet);
							}
						}
					],
					</sec:authorize>
					"processing": true,
	                "serverSide": true,
	                //"ajax": "<c:url value='/masterMaintenance/DistrictMaintenance/query'/>",
	                "ajax": {
	                	url: "<c:url value='/masterMaintenance/DistrictMaintenance/query'/>",
	                	method: "post"
	                },
	                "columns": [
								{
									"data": "districtId",
									"orderable": false,
									"searchable": false,
									"render" : function(data, type, full, meta){
										return "<input type='checkbox' value='"+data+"' name='id' class='tblChk' />";
									},
									"className" : "discount text-center"
								},
	                            {
	                            	"data": "code",
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
	                            	"data": "coverage",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "tpus",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "tpus text-center"
	                            },
	                            {
	                            	"data": "districtId",
	                            	"orderable": false,
	                            	"searchable": false,
	                            	"render" : function(data, type, full, meta){
	                            		var html = "<a href='<c:url value='/masterMaintenance/DistrictMaintenance/edit?act=edit&id='/>"+data+"' class='table-btn'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a>";
	                            		<sec:authorize access="hasPermission(#user, 256)">
	                            		html += " &nbsp;<a href='javascript:void(0)' onclick='deleteRecordsWithConfirm([{id:" +data+ ",tpus:" +full.tpus+ "}])' class='table-btn btn-delete' data-id='"+data+"'><span class='fa fa-times' aria-hidden='true'></span></a>";
	                					</sec:authorize>
	                            		return html;
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
          <h1>District Maintenance</h1>
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
										<th><input type="checkbox" id="chkAll" /></th>
										<th>District Code</th>
										<th>Chinese Name</th>
										<th>English Name</th>
										<th>Coverage</th>
										<th>No. of TPU</th>
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

