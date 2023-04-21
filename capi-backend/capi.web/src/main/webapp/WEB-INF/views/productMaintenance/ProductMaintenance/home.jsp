<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
	</jsp:attribute>
	<jsp:attribute name="header">
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<style>
		.filter {
			margin-bottom: 10px;
		}
		.filter .form-group {
			margin-right: 10px;
		}
		.filter .form-control .select2-container {
			margin-left: 10px;
		}
		.select2 {
			width: 100%;
		}
		/*
		 * Row with equal height columns
		 * --------------------------------------------------
		 */
		.row-eq-height {
		  display: -webkit-box;
		  display: -webkit-flex;
		  display: -ms-flexbox;
		  display:         flex;
		}



		</style>
		<script>
			function deleteRecordsWithConfirm(data) {
				bootbox.confirm({
				     title:"Confirmation",
				     message: "<spring:message code='W00001' />",
				     callback: function(result){
				         if (result){
							$.post("<c:url value='/productMaintenance/ProductMaintenance/delete'/>",
								data,
								function(response) {
									$("#dataList").DataTable().ajax.reload();
									$("#MessageRibbon").html(response);
								}
							);
				         }
				     }
				})
			}
			
			function productHasAnyLinkage(product) {
				return product.noOfQuotationRecord > 0 || 
					product.noOfQuotationNewProduct > 0 || 
					product.noOfPointToNote > 0;
			}
		
			$(document).ready(function(){
				var $dataTable = $("#dataList");
				
				
				$dataTable.DataTable({
					// "scrollX": true,
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
								window.location = "<c:url value='/productMaintenance/ProductMaintenance/edit'/>";
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
	               		url: "<c:url value='/productMaintenance/ProductMaintenance/query'/>",
	               		method:"post",
	                	data: function(d) {
	                		d.search["productGroupId"] = $('#productGroupFilter').val();
	                		d.search["status"] = $('#statusFilter').val();
	                		d.search["reviewed"] = $('#reviewedFilter').val();
	                		d.search["hasAnyLinkage"] = $('#hasAnyLinkageFilter').val();
	                	}
					},
	                "columns": [
	                            { "data": "productId" },
	                            { "data": "productId" },
	                            { "data": "productGroupCode" },
	                            { "data": "productGroupChineseName" },
	                            { "data": "productGroupEnglishName" },
		                        { "data": function(data) {
	                        		var html = '';
	                        		var attrs = [];
	                        		
	                        		if (data.productAttribute1 != null && data.productAttribute1 != '')
	                        			attrs.push(data.productAttribute1);
	                        		if (data.productAttribute2 != null && data.productAttribute2 != '')
	                        			attrs.push(data.productAttribute2);
	                        		if (data.productAttribute3 != null && data.productAttribute3 != '')
	                        			attrs.push(data.productAttribute3);
	                        		if (data.productAttribute4 != null && data.productAttribute4 != '')
	                        			attrs.push(data.productAttribute4);
	                        		if (data.productAttribute5 != null && data.productAttribute5 != '')
	                        			attrs.push(data.productAttribute5);
	                        		
	                        		return attrs.join('<br/><br/>');
	                        		
		                        	},
		                        	"className" : "text-center nowrap",
		                        	"orderable": false 
	                        	},
	                            { "data": "noOfQuotation"},
	                            { "data": function(data){
	                           	  	function tooltipForCount(count, str) {
	                           	  		return count > 0 ? (count + " " + str + "s") : "";
	                            	}
	                         	    if (productHasAnyLinkage(data)){
		         					   	const linkages = [
			         					   		tooltipForCount(data.noOfQuotationRecord, "quotation record"),
			         					   		tooltipForCount(data.noOfQuotationNewProduct, "quotation (new product)"),
			         					   		tooltipForCount(data.noOfPointToNote, "point-to-note")
		         					   		].filter(function (l) { return l != ""; });
				                  	  	const tooltip = "related to " + linkages.join(", ");
	                         			return '<span title="' + tooltip + '">Yes</span>';
	                         		} else {
	                         			return 'No';
	                         		}
                            	} ,
	                        	"className" : "text-center nowrap",
                        		},
	                            { "data": "remark" },
	                            { "data": "status" },
	                            { "data": "createdMonth" },
	                            { "data": "barcode" },
	                            { "data": "reviewedLabel"},
	                            { "data": "productId" }
	                        ],
	                "columnDefs": [

	                               {
	                                   "targets": [0],
	                                   "orderable": false,
	                                   "searchable": false,
	                                   "render" : function(data, type, full, meta){
	                                	   if (productHasAnyLinkage(full) == false){
	                                		   return "<input type='checkbox' name='id' value='"+data+"'/>";
	                                	   } else {
	                                		   return "";
	                                	   }
	                                       
	                                   },
	                                   "className" : "text-center"
	                               },
	                               {
	                                   "targets": [13],
	                                   "orderable": false,
	                                   "searchable": false,
	                                   "render" : function(data, type, full, meta){
	                                	   var html = "<a href='<c:url value='/productMaintenance/ProductMaintenance/edit?id='/>"+data+"'><span class='glyphicon glyphicon-pencil' aria-hidden='true' title='Edit'></span></a>";
	                                		<sec:authorize access="hasPermission(#user, 256)">
		                					html += " &nbsp;<a href='<c:url value='/productMaintenance/ProductMaintenance/cleaning?id='/>"+data+"' class='table-btn btn-default' title='Product Cleaning' ><span class='fa fa-eraser' aria-hidden='true'></span></a>";
		                					html += " &nbsp;<a href='<c:url value='/productMaintenance/ProductMaintenance/bulk?id='/>"+data+"' class='table-btn btn-default' title='Bulk Update'><span class='fa fa-database' aria-hidden='true'></span></a>";
		                					
	                                	    if (productHasAnyLinkage(full) == false){
		                						html += " &nbsp;<a href='javascript:void(0)' onclick='deleteRecordsWithConfirm(\"id="+data+"\")' class='table-btn btn-delete' data-id='"+data+"' title='Delete'><span class='fa fa-times' aria-hidden='true'></span></a>";
		                            		}
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
				
				$('#productGroupFilter').select2ajax({
					placeholder: "Select a Product Type",
					width: "100%",
					allowClear: true
				});
				
				$('.filter').change(function(){
					$dataTable.DataTable().ajax.reload();
				});
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Product Maintenance</h1>
        </section>
        
        <section class="content">
        	<div class="row">
				<div class="col-md-12" >
				<!-- content -->
					<!-- Product Table -->
					<div class="box" >
						<div class="box-body">
							<div class="clearfix">&nbsp;</div>
								
								<div class="form-horizontal">
									<div class="row">
										<label class="control-label col-md-1">Product Type</label>
										<div class="col-md-2">
											<div class="select2">
												<select class="form-control select2 filter" id="productGroupFilter"
													data-ajax-url="<c:url value='/productMaintenance/ProductMaintenance/queryProductGroupSelect2'/>">
													<c:if test="${productGroupId > 0}">
														<option value="<c:out value="${productGroupId}" />" selected="selected">${productGroupText}</option>
													</c:if>
												</select>
											</div>
										</div>
										<label class="control-label col-md-1" >Status</label>
										<div class="col-md-2">
											<select class="form-control filter" id="statusFilter" >
												<option value="">All</option>
												<option value="Active">Active</option>
												<option value="Inactive">Inactive</option>
											</select>
										</div>
										<label class="control-label col-md-1">Reviewed</label>
										<div class="col-md-2">
											<select  class="form-control col-md-2 filter" id="reviewedFilter" >
												<option value="">All</option>
												<option value="Y">Yes</option>
												<option value="N">No</option>
											</select>
										</div>
										<label class="control-label col-md-1">Has Any Linkage?</label>
										<div class="col-md-2">
											<select  class="form-control col-md-2 filter" id="hasAnyLinkageFilter" >
												<option value="">All</option>
												<option value="Y">Yes</option>
												<option value="N">No</option>
											</select>
										</div>
									</div>
								</div>
							
							<hr/>
							<table class="table table-striped table-bordered table-hover responsive" id="dataList">
								<thead>
								<tr>
									<th class="text-center action" ><input class="select_all" type="checkbox" /></th>
									<th>Product ID</th>
									<th>Product Type Code</th>
									<th>Product Type Chinese Name</th>
									<th>Product Type English Name</th>
									<th>Product Attributes</th>
									<th>No. of Quotation</th>
									<th>Has Any Linkage?</th>
									<th>Remarks</th>
									<th>Status</th>
									<th>Created Month</th>
									<th>Barcode</th>
									<th>Reviewed</th>
									<th class="text-center action" data-priority="1"></th>
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