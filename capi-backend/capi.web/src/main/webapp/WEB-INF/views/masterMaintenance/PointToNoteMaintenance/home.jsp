<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
	</jsp:attribute>
	<jsp:attribute name="header">
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<script>
			$(document).ready(function(){
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
							"text": "Expiry Now",
							"action": function( nButton, oConfig, flash ) {
								var selectedIds = $('input[name="id"]:checked', $dataTable).map(function() {
									return $(this).val();
								}).get();
								if(selectedIds.length == 0) {
									bootbox.alert({
										title: "Alert",
										message: "<spring:message code='E00009' />"
									});
									//alert('<spring:message code="E00009" />');
									return;
								}
								
								normalFormPost("<c:url value='/masterMaintenance/PointToNoteMaintenance/expiryNow'/>", {ids: selectedIds});
							}
						},
						{
							"text": "Add",
							"action": function( nButton, oConfig, flash ) {
								window.location = "<c:url value='/masterMaintenance/PointToNoteMaintenance/edit'/>";
							}
						},
						{
							"text": "Delete",
							"action": function( nButton, oConfig, flash ) {
								var selectedIds = $('input[name="id"]:checked', $dataTable).map(function() {
									return $(this).val();
								}).get();
								if(selectedIds.length == 0) {
									bootbox.alert({
										title: "Alert",
										message: "<spring:message code='E00009' />"
									});
									//alert('<spring:message code="E00009" />');
									return;
								}
								bootbox.confirm({
									title:"Confirmation",
									message: "<spring:message code='W00001' />",
									callback: function(result){
										if (result){																		
											normalFormPost("<c:url value='/masterMaintenance/PointToNoteMaintenance/delete'/>", {ids: selectedIds});
										}
									}
								})
								
								/*
								if (!confirm('<spring:message code="W00001" />')) return;
								
								normalFormPost("<c:url value='/masterMaintenance/PointToNoteMaintenance/delete'/>", {ids: selectedIds});
								*/
							}
						}
					],
					</sec:authorize>
					"processing": true,
	                "serverSide": true,
	                "ajax": {
	                	url: "<c:url value='/masterMaintenance/PointToNoteMaintenance/query'/>",
	                	method: "post"
	                },
	                "columns": [
	                            {
	                            	"data": "id",
	                            	"render": function(data) {
	                            		return '<input name="id" type="checkbox" value="' + data + '" />';
	                            	}
	                            },
	                            {
	                            	"data": "effectiveDate"
	                            },
	                            {
	                            	"data": "expiryDate"
	                            },
	                            {
	                            	"data": "createdDate"
	                            },
	                            {
	                            	"data": "createdBy"
	                            },
	                            {
	                            	"data": "noOfQuotation"
	                            },
	                            {
	                            	"data": "noOfUnit"
	                            },
	                            {
	                            	"data": "noOfFirm"
	                            },
	                            {
	                            	"data": "noOfProduct"
	                            },
	                            {
	                            	"data": "pointToNote"
	                            },
	                            {
	                            	"data": "id",
	                            	"render" : function(data, type, full, meta){
	                            		var html = "<a href='<c:url value='/masterMaintenance/PointToNoteMaintenance/edit?id='/>"+data+"' class='table-btn'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a>";
	                            		html += " <a href='#' data-id='"+data+"' class='table-btn btn-delete'><span class='fa fa-times' aria-hidden='true'></span></a>";
	                            		return html;
                            		},
	                            	"className" : "text-center"
                            	}
	                        ],
	                "columnDefs": columnDefs
				});
				
				$dataTable.on('click', '.btn-delete', function(e) {
					e.preventDefault();
					var self = this;
					bootbox.confirm({
						title:"Confirmation",
						message: "<spring:message code='W00001' />",
						callback: function(result){
							if (result){
								var id = $(self).data('id');								
								normalFormPost("<c:url value='/masterMaintenance/PointToNoteMaintenance/delete'/>", {ids: [id]});
							}
						}
					})
					
					/*
					if (!confirm('<spring:message code="W00001" />'))
						return;
					
					var id = $(this).data('id');
					
					normalFormPost("<c:url value='/masterMaintenance/PointToNoteMaintenance/delete'/>", {ids: [id]});
					*/
				});
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Point To Note Maintenance</h1>
        </section>
        
        <section class="content">
        	<div class="row">
				<div class="col-md-12" >
				<!-- content -->
				
					<!-- Itinerary Planning Table -->
					<div class="box" >
						<div class="box-body">
							<table class="table table-striped table-bordered table-hover" id="dataList">
								<thead>
								<tr>
									<th class="action"><input class="select_all" type="checkbox" /></th>
									<th>Effective Date</th>
									<th>Expiry Date</th>
									<th>Created Date</th>
									<th>Created By</th>
									<th>No. of Quotation</th>
									<th>No. of Variety</th>
									<th>No. of Firm</th>
									<th>No. of Product</th>
									<th>Point to Note</th>
									<th class="action"></th>
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

