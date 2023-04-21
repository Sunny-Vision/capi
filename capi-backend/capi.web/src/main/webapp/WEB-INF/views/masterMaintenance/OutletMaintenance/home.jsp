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
	</jsp:attribute>
	<jsp:attribute name="header">
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<style>
		#dataList .discount span {
			display: inline-block;
			margin: 0 2px;
		}
		#dataList .discount span.number {
			border: solid 1px #d2d6de;
			padding: 0 20px;
		}
		</style>
		<script>
			$(document).ready(function(){
				var $dataTable = $("#dataList");
				
				$dataTable.DataTable({
					"order": [[ 0, "desc" ]],
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
								window.location = "<c:url value='/masterMaintenance/OutletMaintenance/edit'/>";
							}
						}
					],
					</sec:authorize>
					"processing": true,
	                "serverSide": true,
	                "ajax": {
	                	url: "<c:url value='/masterMaintenance/OutletMaintenance/query'/>",
	                	method:"post",
	                	data: function(d) {
	                		d.outletTypeId = $('#outletTypeId').val();
	                		d.districtId = $('#districtId').val();
	                		d.tpuId = $('#tpuId').val();
	                		d.activeOutlet = $('#activeOutlet').val();
	                	}
	                },
	                "columns": [
	                            {
	                            	"data": "firmCode",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "discount text-center"
	                            },
	                            {
	                            	"data": "name",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "brCode",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "district",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "tpu",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "activeOutlet",
	                            	"orderable": true,
	                            	"searchable": false,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "quotationCount",
	                            	"orderable": true,
	                            	"searchable": false,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "streetAddress",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "detailAddress",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "outletId",
	                            	"orderable": false,
	                            	"searchable": false,
	                            	"render" : function(data, type, full, meta){
	                            		var html = "<a href='<c:url value='/masterMaintenance/OutletMaintenance/edit?id='/>"+data+"' class='table-btn'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a>";
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
				$('#districtId').select2ajax({
					placeholder: "",
					allowClear: true,
					closeOnSelect: false
				});
				$('#tpuId').select2ajax({
					ajax: {
					    data: function (params) {
					    	params.districtId = $('#districtId').val()
					    	return params;
						},
						method: 'GET',
						contentType: 'application/json'
					},
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
          <h1>Outlet Maintenance</h1>
        </section>
        
        <section class="content">
        	<div class="row">
				<div class="col-md-12" >
				<!-- content -->
				
					<!-- Itinerary Planning Table -->
					<div class="box" >
						<div class="box-body">
							<div class="clearfix">&nbsp;</div>
							<div class="form-horizontal">
								<div class="row">
									<label class="control-label col-md-1">Outlet Type</label>
									<div class="col-md-2">
										<select class="form-control select2 filters" id="outletTypeId" 
										data-ajax-url="<c:url value="/masterMaintenance/OutletMaintenance/queryOutletTypeSelect2"/>" multiple="multiple">
										<%--
											<c:forEach var="outletType" items="${outletTypes}">
												<option value="<c:out value="${outletType.id}" />">${outletType.shortCode} - ${outletType.englishName}</option>
											</c:forEach>
										 --%>
										</select>
									</div>
									
										
									<label class="control-label col-md-1">District</label>
									<div class="col-md-2">
										<select class="form-control select2 filters" id="districtId" multiple="multiple"
										data-ajax-url="<c:url value='/masterMaintenance/OutletMaintenance/queryDistrictSelect2'/>"></select>
									</div>
									
									<label class="control-label col-md-1">TPU</label>
									<div class="col-md-2">
										<select class="form-control select2 filters" id="tpuId" multiple="multiple"
										data-ajax-url="<c:url value='/masterMaintenance/OutletMaintenance/queryTpuSelect2'/>"></select>
									</div>
									
									<label class="control-label col-md-1">Active Outlet</label>
										<div class="col-md-2">
											<select class="form-control filters" id="activeOutlet">
											<option value=""></option>
											<option value="Y">Y</option>
											<option value="N">N</option>
										</select>
									</div>
								</div>
							</div>
							
							
							<hr/>
							<table class="table table-striped table-bordered table-hover" id="dataList">
								<thead>
								<tr>
									<th>Outlet Code</th>
									<th>Outlet Name</th>
									<th>BR Code</th>
									<th>District Code</th>
									<th>TPU</th>
									<th>Active Outlet</th>
									<th>No. of Quotation</th>
									<th>Street Address</th>
									<th>Detail Address</th>
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

