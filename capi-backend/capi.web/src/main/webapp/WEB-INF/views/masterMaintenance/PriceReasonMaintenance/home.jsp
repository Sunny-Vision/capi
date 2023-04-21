<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
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
				bootbox.confirm({
					title:"Confirmation",
					message: "<spring:message code='W00001' />",
					callback: function(result){
						if (result){
							$.post("<c:url value='/masterMaintenance/PriceReasonMaintenance/delete'/>",
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
				$.post("<c:url value='/masterMaintenance/PriceReasonMaintenance/delete'/>",
					data,
					function(response) {
						$("#dataList").DataTable().ajax.reload();
						$("#MessageRibbon").html(response);
					}
				);
				*/
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
								window.location = "<c:url value='/masterMaintenance/PriceReasonMaintenance/edit?act=add'/>";
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
									//alert('<spring:message code="E00009" />');
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
	                	url: "<c:url value='/masterMaintenance/PriceReasonMaintenance/query'/>",
	                	method: "post",
	                	data: function(d) {
	                		d.outletTypeId = $('#outletTypeId').val();
	                		d.reasonType = $('#reasonType').val();
	                	}
	                },
	                "columns": [
								{
									"data": "priceReasonId",
									"orderable": false,
									"searchable": false,
									"render" : function(data, type, full, meta){
										return "<input type='checkbox' value='"+data+"' name='id' class='tblChk' />";
									},
									"className" : "discount text-center"
								},
	                            {
	                            	"data": "priceReasonId",
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
	                            	"data": "reasonTypeLabel",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "sequence",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "priceReasonId",
	                            	"orderable": false,
	                            	"searchable": false,
	                            	"render" : function(data, type, full, meta){
	                            		var html = "<a href='<c:url value='/masterMaintenance/PriceReasonMaintenance/edit?act=edit&id='/>"+data+"' class='table-btn'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a>";
	                            		<sec:authorize access="hasPermission(#user, 256)">
	                            		html += " &nbsp;<a href='javascript:void(0)' onclick='deleteRecordsWithConfirm(\"id="+data+"\")' class='table-btn btn-delete' data-id='"+data+"'><span class='fa fa-times' aria-hidden='true'></span></a>";
	                					</sec:authorize>
	                            		return html;
                            		},
	                            	"className" : "text-center"
                            	}
	                        ]
				});

				$('#outletTypeId').select2ajax({
					placeholder: "",
					allowClear: true,
					closeOnSelect: false
				});

				$('.filters').change(function(){
					$dataTable.DataTable().ajax.reload();
				});
			});
			
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Price Reason Maintenance</h1>
        </section>
        
        <section class="content">
        	<div class="row">
				<div class="col-md-12" >
				<!-- content -->
					<form action="<c:url value="/masterMaintenance/PriceReasonMaintenance/delete" />" id="dataForm" method="post">
						<!-- Itinerary Planning Table -->
						<div class="box" >
							<div class="box-body">
								<div class="clearfix">&nbsp;</div>
								<div class="form-horizontal">
									<div class="row">
										<label class="control-label col-md-1">Outlet Type</label>
										<div class="col-md-2">
											<select class="form-control select2 filters" id="outletTypeId"
											data-ajax-url="<c:url value="/masterMaintenance/PriceReasonMaintenance/queryOutletTypeSelect2" />"
											 multiple="multiple">
											<%--
												<c:forEach var="outletType" items="${outletTypes}">
													<option value="<c:out value="${outletType.id}" />">${outletType.shortCode} - ${outletType.englishName}</option>
												</c:forEach>
											 --%>
											</select>
										</div>
										
										<label class="control-label col-md-1">Reason Type</label>
										<div class="col-md-2">
											<select class="form-control filters" id="reasonType">
												<option value=""></option>
												<option value="Price">Price</option>
												<option value="Discount">Discount</option>
											</select>
										</div>
									</div>
								</div>
								
								<hr/>
								<table class="table table-striped table-bordered table-hover" id="dataList">
									<thead>
									<tr>
										<th><input type="checkbox" id="chkAll" /></th>
										<th>Price Reason Id</th>
										<th>Description</th>
										<th>Reason Type</th>
										<th>Sequence</th>
										<th class="text-center action"></th>
									</tr>
									</thead>
									<tbody>
									</tbody>
								</table>
							</div>
						</div>
					</form>
				</div>
			</div>
        </section>
	</jsp:body>
</t:layout>

