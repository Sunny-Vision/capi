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
		<script>
			function deleteRecordsWithConfirm(data) {
				bootbox.confirm({
					title:"Confirmation",
					message: "<spring:message code='W00001' />",
					callback: function(result){
						if (result){
							$.post("<c:url value='/productMaintenance/ProductGroupMaintenance/delete'/>",
								data,
								function(response) {
									$("#dataList").DataTable().ajax.reload();
									$("#MessageRibbon").html(response);
								}
							);
						}
					}
				})
				/*
				if (!confirm('<spring:message code="W00001" />')) return;
				$.post("<c:url value='/productMaintenance/ProductGroupMaintenance/delete'/>",
					data,
					function(response) {
						$("#dataList").DataTable().ajax.reload();
						$("#MessageRibbon").html(response);
					}
				);
				*/
			}
		
			$(document).ready(function(){
				var $dataTable = $("#dataList");
				
				
				$dataTable.DataTable({
					"ordering": true, 
					"order": [[ 1, "asc" ]],
					"searching": true,
					<sec:authorize access="!hasPermission(#user, 256)">
					"buttons": [],
					</sec:authorize>
					<sec:authorize access="hasPermission(#user, 256)">
					"buttons": [
						{
							"text": "Add",
							"action": function( nButton, oConfig, flash ) {
								window.location = "<c:url value='/productMaintenance/ProductGroupMaintenance/edit'/>";
							}
						},
						{
							"text": "Delete",
							"action": function( nButton, oConfig, flash ) {
								var data = $dataTable.find(':checked').serialize();
								if (data == '') {
									//alert('<spring:message code="E00009" />');
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
	                	url: "<c:url value='/productMaintenance/ProductGroupMaintenance/query'/>",
	                	method: "post"
	                },
	                "columns": [
	                            { "data": "productGroupId" },
	                            { "data": "code" },
	                            { "data": "chineseName" },
	                            { "data": "englishName" },
	                            { "data": "noOfProduct" },
	                            { "data": "noOfUnit" },
	                            { "data": "noOfAttribute" },
	                            { "data": "status" },
	                            { "data": "productGroupId" }
	                        ],
	                "columnDefs": [

	                               {
	                                   "targets": [0],
	                                   "orderable": false,
	                                   "searchable": false,
	                                   "render" : function(data, type, full, meta){
	                                       return "<input type='checkbox' name='id' value='"+data+"'/>";
	                                   },
	                                   "className" : "text-center"
	                               },
	                               {
	                                   "targets": [8],
	                                   "orderable": false,
	                                   "searchable": false,
	                                   "render" : function(data, type, full, meta){
	                                	   var html = "<a href='<c:url value='/productMaintenance/ProductGroupMaintenance/edit?id='/>"+data+"'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a>";
	                                		<sec:authorize access="hasPermission(#user, 256)">
		                            		html += " &nbsp;<a href='javascript:void(0)' onclick='deleteRecordsWithConfirm(\"id="+data+"\")' class='table-btn btn-delete' data-id='"+data+"'><span class='fa fa-times' aria-hidden='true'></span></a>";
		                					</sec:authorize>
	                                	   return html;
	                                   },
	                                   "className" : "text-center"
	                               },
	                               {
	                            		"targets": "_all",
		                            	"orderable": true,
	                               		"searchable": true,
	                               		"className" : "text-center"
	                            	   
	                               }
	                               
	                           ]
				});
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
        	<h1>Product Type  Maintenance</h1>
        </section>
        <section class="content">
        	<div class="row">
				<div class="col-md-12" >
				<!-- content -->
					<!-- Product Group Table -->
					<div class="box" >
						<div class="box-body">
							<div class="clearfix">&nbsp;</div>
							<table class="table table-striped table-bordered table-hover" id="dataList">
								<thead>
								<tr>
									<th class="text-center action" ><input class="select_all" type="checkbox" /></th>
									<th>Product Type Code</th>
									<th>Product Type Chinese Name</th>
									<th>Product Type English Name</th>
									<th>No. of Product included</th>
									<th>No. of Variety included</th>
									<th>No. of Product Attributes</th>
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