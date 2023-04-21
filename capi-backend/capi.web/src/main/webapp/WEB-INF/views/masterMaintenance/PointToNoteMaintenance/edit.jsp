<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
<jsp:useBean id="niceDate" class="java.util.Date"/>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/jstree-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/unitLookup-css.jsp" %>
		<style>
		.table-header {
			font-weight: bold;
			font-size: 20px;
			margin-bottom: 5px;
		}
		</style>
	</jsp:attribute>
	<jsp:attribute name="header">
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker.jsp"%>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/jstree.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/outletLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/productLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/quotationLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/unitLookup.jsp" %>
		<script src="<c:url value='/resources/js/underscore-min.js'/>"></script>
		<script>
			$.fn.pointToNoteChildSection = function(opts) {
				var $section = this;
				var _def = {
					dataTableSetting: {
						"order": [[ 0, "asc" ]],
						"searching": false,
						"ordering": true,
						"buttons": [],
						"processing": true,
		                "serverSide": false,
		                "paging": true,
		                "columnDefs": [
			                           {
			                               "targets": "action",
			                               "orderable": false,
			                               "searchable": false
			                           },
			                           {
			                        	   "targets": "_all",
			                        	   "className" : "text-center"
			                           }
			                           <sec:authorize access="!hasPermission(#user, 256)">
			                           ,
			                           {
			                        	   "targets": "action",
			                        	   "visible": false
			                           }
			                           </sec:authorize>
		                               ]
					},
					getDetailUrl: null,
					lookupPluginName: null,
					uniqueDataProperty: 'id'
				};
				
				opts.dataTableSetting = $.extend(_def.dataTableSetting, opts.dataTableSetting);
				
				var settings = $.extend(_def, opts);
				
				var $table = $section.find('table');
				$table.on('click', '.btn-delete', function(e) {
					e.preventDefault();
					var $dataTableRow = $(this).parents('table').DataTable().row($(this).parents('tr'));
					bootbox.confirm({
						title:"Confirmation",
						message: "<spring:message code='W00001' />",
						callback: function(result){
							if (result){																		
								$dataTableRow.remove().draw();
								$section.find(".hiddenIds").val(_.map($table.DataTable().data(), 
										function(d) {
									if (d.id == undefined) {
										return d.outletId;
									} else {
										return d.id;
									}
								}).join(","));
							}
						}
					});
				});
				
				var oldChildIds = $section.find('.oldChildIds').val().split(',');
				
				$.post(settings.getDetailUrl,
					{ids: oldChildIds},
					function(data) {
						settings.dataTableSetting.data = data;
						if (data != null && data.length > 0){							
							$section.find(".hiddenIds").val(_.map(data, 
									function(d) {
										if (d.id == undefined) {
											return d.outletId;
										} else {
											return d.id;
										}
									}).join(","));
						}
						$table.DataTable(settings.dataTableSetting);
						
						$.fn[settings.lookupPluginName].apply(
							$section.find('.btn-add'),
							[{
								selectedIdsCallback: function(selectedIds) {
									var newIds = selectedIds;
									if ($table.hasClass('Variety')){
										var existingIds = $($table.DataTable().rows().data()).map(function() {
											return this[settings.uniqueDataProperty];
										}).get();
										
										newIds = _.difference(selectedIds, existingIds);
									}
									$section.find(".hiddenIds").val(selectedIds.join(","));
									$.post(settings.getDetailUrl,
										{ids: newIds},
										function(data) {
											if (!$table.hasClass('Variety')){
												$table.DataTable().rows().remove();												
											}
											$table.DataTable().rows.add(data).draw();
										}
									);
								},
								alreadySelectedIdsCallback: function(){
									var ids = [];
									$($("input[name='"+settings.inputName+"']").val().split(',')).each(function(){
										if (+this > 0) {
											ids.push(this);
										}
									})
									return ids;
								}
							}]
						);
					}
				);
				
				$section.find(".btn-remove").click(function(){
					$section.find('table').DataTable().rows().remove().draw();
					$section.find(".hiddenIds").val("");
				})
				
				$section.find(".selectAll").click(function(){
					if (this.checked){
						$section.find('table').DataTable().rows().remove().draw();
						$section.find(".hiddenIds").val("");
						$section.find(".dataDiv").hide();						
					}
					else{
						$section.find(".dataDiv").show();	
					}
				})
				
				return $section;
			};
			
			$(function() {
				var $mainForm = $('#mainForm');
				$mainForm.validate();
				
				Datepicker();
				
				$('#outletSection').pointToNoteChildSection({
					dataTableSetting: {
						columns: [
							{"data": "firmCode"},
							{"data": "name"},
							{"data": "district"},
							{"data": "tpu"},
							{"data": "activeOutlet"},
							{"data": "quotationCount"},
							{"data": "streetAddress"},
							{"data": "detailAddress"},
							{
								"data": "outletId",
								"render" : function(data){
									var html = "<a href='#' class='table-btn btn-delete'><span class='fa fa-times' aria-hidden='true'></span></a>";
									//html += "<input name='outletIds' type='hidden' value='" + data + "'/>";
									return html;
								}
							}
						]
					},
					getDetailUrl: "<c:url value='/masterMaintenance/PointToNoteMaintenance/getOutletDetail'/>",
					lookupPluginName: 'outletLookup',
					uniqueDataProperty: 'outletId',
					inputName: 'outletIds'
				});
				
				$('#productSection').pointToNoteChildSection({
					dataTableSetting: {
						columns: [
							{"data": "id"},
							{"data": "category"},
							{"data": "numberOfQuotations"},
							{"data": function(data) {
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
                        		
                        		return attrs.join('<br/>');
                        	}},
							{"data": "remark"},
							{"data": "status"},
							{
								"data": "id",
								"render" : function(data){
									var html = "<a href='#' class='table-btn btn-delete'><span class='fa fa-times' aria-hidden='true'></span></a>";
									//html += "<input name='productIds' type='hidden' value='" + data + "'/>";
									return html;
								}
							}
						]
					},
					getDetailUrl: "<c:url value='/masterMaintenance/PointToNoteMaintenance/getProductDetail'/>",
					lookupPluginName: 'productLookup',
					inputName: 'productIds'
				});
				
				$('#quotationSection').pointToNoteChildSection({
					dataTableSetting: {
						columns: [
	                        { "data": "id" },
	                        { "data": "survey" },
	                        { "data": "unitCode" },
	                        { "data": "unitName" },
	                        { "data": "productId" },
	                        { "data": "productAttribute" },
	                        { "data": "firmCode" },
	                        { "data": "firmName" },
							{
								"data": "id",
								"render" : function(data){
									var html = "<a href='#' class='table-btn btn-delete'><span class='fa fa-times' aria-hidden='true'></span></a>";
									//html += "<input name='quotationIds' type='hidden' value='" + data + "'/>";
									return html;
								}
							}
						]
					},
					getDetailUrl: "<c:url value='/masterMaintenance/PointToNoteMaintenance/getQuotationDetail'/>",
					lookupPluginName: 'quotationLookup',
					inputName: 'quotationIds'
				});
				
				$('#unitSection').pointToNoteChildSection({
					dataTableSetting: {
						columns: [
	                        { "data": "code" },
	                        { "data": "chineseName" },
	                        { "data": "englishName" },
	                        { "data": "survey" },
							{
								"data": "id",
								"render" : function(data){
									var html = "<a href='#' class='table-btn btn-delete'><span class='fa fa-times' aria-hidden='true'></span></a>";
									//html += "<input name='unitIds' type='hidden' value='" + data + "'/>";
									return html;
								}
							}
						]
					},
					getDetailUrl: "<c:url value='/masterMaintenance/PointToNoteMaintenance/getUnitDetail'/>",
					lookupPluginName: 'unitLookup',
					inputName: 'unitIds'
				});
				
				
				<sec:authorize access="!hasPermission(#user, 256)">
					viewOnly();
				</sec:authorize>
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Point To Note Maintenance</h1>
          <c:if test="${not empty model.id}">
				<div class="breadcrumb form-horizontal" style="width:240px">
					<div class="form-group" style="margin-bottom:0px">
			        	<div class="col-sm-5">Created Date:</div>
			        	<div class="col-sm-7">${model.createdDate}</div>
			        </div>
			        <div class="form-group" style="margin-bottom:0px">
			         	<div class="col-sm-5">Last Modified:</div>
			         	<div class="col-sm-7">${model.modifiedDate}</div>
			         </div>
		      	</div>
      		</c:if>
        </section>
        
        <section class="content">
        	<form id="mainForm" action="<c:url value='/masterMaintenance/PointToNoteMaintenance/save'/>" method="post" role="form" >
        		<input name="id" value="<c:out value="${model.id}" />" type="hidden" />
	        	<div class="row">
	        		<div class="col-md-12">
	        			
	        			<!-- general form elements -->
	        			<div class="box box-primary">
		        			<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/masterMaintenance/PointToNoteMaintenance/home'/>">Back</a>
							</div>
	       					<div class="box-body">
	       						<div class="form-horizontal">
	       							
		       							
			       							<div class="form-group">
			       								<label class="col-sm-2 control-label">Point To Note</label>
			       								<div class="col-sm-7">
													<input name="note" class="form-control" value="<c:out value="${model.note}" />" required />
												</div>
											</div>
										
		       							
			       							<div class="form-group">
			       								<label class="col-sm-2 control-label">Effective Date</label>
			       								<div class="col-sm-5">
													<div class="input-group">
														<input type="text" class="form-control date-picker" data-orientation="top"
															name="effectiveDate" value="<c:out value="${model.effectiveDate}" />" required />
														<div class="input-group-addon">
															<i class="fa fa-calendar"></i>
														</div>
													</div>
												</div>
											</div>
										
		       							
			       							<div class="form-group">
			       								<label class="col-sm-2 control-label">Expiry Date</label>
			       								<div class="col-sm-5">
													<div class="input-group">
														<input type="text" class="form-control date-picker" data-orientation="top"
															name="expiryDate" value="<c:out value="${model.expiryDate}" />" />
														<div class="input-group-addon">
															<i class="fa fa-calendar"></i>
														</div>
													</div>
												</div>
											</div>
										
									
									<div class="row">
										<div class="col-md-12">
										<hr/>
										</div>
									</div>
								</div>
								
								<div class="row" id="outletSection">
									<input class="oldChildIds" type="hidden" value="<c:forEach var="id" items="${model.outletIds}">${id},</c:forEach>"/>
									<div class="col-md-12">
										<div class="table-header">Firm <label style="font-weight: normal; font-size:14px">
										<input name="isAllOutlet" class="selectAll" type="checkbox" ${model.isAllOutlet ? "checked" : "" }/> Select All</label>
										</div>
										<div class="dataDiv" <c:if test="${model.isAllOutlet}">style="display:none"</c:if>>
										<table class="table table-striped table-bordered table-hover">
											<thead>
											<tr>
												<th>Firm Code</th>
												<th>Firm Name</th>
												<th>District Code</th>
												<th>TPU</th>
												<th>Active Outlet</th>
												<th>No. of quotation</th>
												<th>Street Address</th>
												<th>Detail Address</th>
												<th class="action"></th>
											</tr>
											</thead>
										</table>
										<input type="hidden" value="" class="hiddenIds" name="outletIds" />
										<sec:authorize access="hasPermission(#user, 256)">
										<button type="button" class="btn btn-default btn-add"><i class="fa fa-plus"></i> Add Firm</button>
										<button type="button" class="btn btn-default btn-remove"><i class="fa fa-remove"></i> Clear All</button>
										</sec:authorize>
										</div>
									</div>
								</div>
								<hr/>
								
								<div class="row" id="productSection">
									<input class="oldChildIds" type="hidden" value="<c:forEach var="id" items="${model.productIds}">${id},</c:forEach>"/>
									<div class="col-md-12">
										<div class="table-header">Product <label style="font-weight: normal; font-size:14px">
										<input name="isAllProduct" class="selectAll" type="checkbox" ${model.isAllProduct ? "checked" : "" }/> Select All</label>
										</div>
										<div class="dataDiv" <c:if test="${model.isAllProduct}">style="display:none"</c:if>>
										<table class="table table-striped table-bordered table-hover">
											<thead>
											<tr>
												<th>Product ID</th>
												<th>Product Group</th>
												<th>No. of quotations</th>
												<th>Attributes</th>
												<th>Remarks</th>
												<th>Status</th>
												<th class="action"></th>
											</tr>
											</thead>
										</table>
										<input type="hidden" value="" class="hiddenIds" name="productIds" />
										<sec:authorize access="hasPermission(#user, 256)">
										<button type="button" class="btn btn-default btn-add"><i class="fa fa-plus"></i> Add Product</button>
										<button type="button" class="btn btn-default btn-remove"><i class="fa fa-remove"></i> Clear All</button>
										</sec:authorize>
										</div>
									</div>
								</div>
								<hr/>
								
								<div class="row" id="unitSection">
									<input class="oldChildIds" type="hidden" value="<c:forEach var="id" items="${model.unitIds}">${id},</c:forEach>"/>
									<div class="col-md-12">
										<div class="table-header">Variety <label style="font-weight: normal; font-size:14px">
										<input name="isAllUnit" class="selectAll" type="checkbox" ${model.isAllUnit ? "checked" : "" }/> Select All</label>
										</div>
										<div class="dataDiv" <c:if test="${model.isAllUnit}">style="display:none"</c:if>>
										<table class="table table-striped table-bordered table-hover Variety">
											<thead>
											<tr>
												<th>Code</th>
												<th>Chinese Name</th>
												<th>English Name</th>
												<th>Purpose</th>
												<th class="action"></th>
											</tr>
											</thead>
										</table>
										<input type="hidden" value="" class="hiddenIds" name="unitIds" />
										<sec:authorize access="hasPermission(#user, 256)">
										<button type="button" class="btn btn-default btn-add"><i class="fa fa-plus"></i> Add Variety</button>
										<button type="button" class="btn btn-default btn-remove"><i class="fa fa-remove"></i> Clear All</button>
										</sec:authorize>
										</div>
									</div>
								</div>
								<hr/>
								
								<div class="row" id="quotationSection">
									<input class="oldChildIds" type="hidden" value="<c:forEach var="id" items="${model.quotationIds}">${id},</c:forEach>"/>
									<div class="col-md-12">
										<div class="table-header">Quotation <label style="font-weight: normal; font-size:14px">
										<input name="isAllQuotation" class="selectAll" type="checkbox" ${model.isAllQuotation ? "checked" : "" }/> Select All</label>
										</div>
										<div class="dataDiv" <c:if test="${model.isAllOutlet}">style="display:none"</c:if>>
											<table class="table table-striped table-bordered table-hover">
												<thead>
												<tr>
													<th>Quotation ID</th>
													<th>Purpose</th>
													<th>Variety Code</th>
													<th>Variety Name</th>
													<th>Product ID</th>
													<th>Product Attribute 1</th>
													<th>Firm ID</th>
													<th>Firm Name</th>
													<th class="action"></th>
												</tr>
												</thead>
											</table>
											<input type="hidden" value="" class="hiddenIds" name="quotationIds" />
											<sec:authorize access="hasPermission(#user, 256)">
											<button type="button" class="btn btn-default btn-add"><i class="fa fa-plus"></i> Add Quotation</button>
											<button type="button" class="btn btn-default btn-remove"><i class="fa fa-remove"></i> Clear All</button>
											</sec:authorize>
										</div>
									</div>
								</div>
								<hr/>
							</div>
	       					<sec:authorize access="hasPermission(#user, 256)">
		       					<div class="box-footer">	       						
		        					<button type="submit" class="btn btn-info">Submit</button>
		       					</div>
	       					</sec:authorize>
						</div>
	        		</div>
	        	</div>
        	</form>
        </section>
	</jsp:body>
</t:layout>

