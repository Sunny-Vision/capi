<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page session="false"%>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp"%>
	</jsp:attribute>

	<jsp:attribute name="header">
	
		<style>
			#attachmentContainer {
				min-height: 300px;
			}
			#attachmentContainer .attachment {
				margin-bottom: 10px;
			} 
			.btn.pull-right {
				margin-left: 10px;
			}
		</style>
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp"%>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp"%>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp"%>
		<%@include file="/WEB-INF/views/includes/commonDialog/imageViewer.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/productLookup.jsp" %>
		<script src="<c:url value='/resources/js/jquery.tmpl.min.js'/>"></script>
		<script>  	    
			var lastRowId = 0;
			
			var selectedIds = [];
			
			function loadSpecficationTable(productId, productGroupId) {
				var specificatoins = [];
				
			    $.ajax({
			        url:'<c:url value='/productMaintenance/ProductMaintenance/queryProductSpecificationList/'/>',
			        async: false,
			        type:'get',
			        data:'productId='+productId+'&productGroupId='+productGroupId,
			        success:function(data){;
			            specifications = data;
			        }
			    });
			    
				for (index = 0, len = specifications.length; index < len; ++index) {
			
				    // Render the specification using the template
				    $("#trSpecTemplate").tmpl(specifications[index]).appendTo(".specificationContainer");
				    
				}
				
			}
		    
			function deleteSelectedIds(obj) {
				var productId = $(obj).parent().parent().find('td:first-child').find('input').attr('value')
				selectedIds = jQuery.grep(selectedIds, function(value) {
					return value != productId;
				});
	    		$(obj).parent().parent().remove();
			}
			
			$(document).ready(function() {	

				loadSpecficationTable("${model.productId}","${model.productGroupId}");
				
				Modals.init();
				
				$('#add').productLookup({
					selectedIdsCallback: function(productIds) {
						
						//productIds = productIdList.split(",");
						//var table = $(".datatable").dataTable().fnGetData();

						var table = [];
						
					    $.ajax({
					        url:'<c:url value='/commonDialog/ProductLookup/queryAll'/>',
					        async: false,
					        type: 'post',
					        data: {productIds : productIds},
					        success:function(data){
					        	table = data;
					        	for(var i = 0; i < data.length; i++) {
					        		selectedIds.push(data[i].id);
					        	}
					        }
					    });

						for (i in productIds) {
							var productId = productIds[i];
							console.log("productId:" +productId);
							
							var product = {};
					    	product["index"] = lastRowId;
					    	product["productId"] = productId;
							
							for (var i=0 ; i < table.length ; i++) {
								if ( table[i]["id"] == productId){
									product["noOfQuotation"] = table[i]["numberOfQuotations"];
									product["remark"] = table[i]["remark"];
									
	                        		if (table[i]["productAttribute1"] != null && table[i]["productAttribute1"] != '')
	                        			product["attribute1"] = table[i]["productAttribute1"] ;
	                        		if (table[i]["productAttribute2"] != null && table[i]["productAttribute2"] != '')
	                        			product["attribute2"] = table[i]["productAttribute2"] ;
	                        		if (table[i]["productAttribute3"] != null && table[i]["productAttribute3"] != '')
	                        			product["attribute3"] = table[i]["productAttribute3"] ;
	                        		break;
								}
							}

							if (($("[id='productId']").val() == productId) || ($("[id^='selectedProductId']").val() && 
								$.inArray(productId, $("[id^='selectedProductId']").map(function(){return eval($(this).val());}).get()) > -1)){
								bootbox.alert({
								    title: "Alert",
								    message: "<spring:message code='E00115' />"
								});
							} else {
								$("#trProdTemplate").tmpl(product).appendTo(".productContainer");
						    	$(":input[id='productRemove"+lastRowId+"']").click(function () {
						    		$(this).parent().parent().remove();
								});
					            lastRowId++;
							}
						}

					},
					multiple: true,
					queryDataCallback: function(model) {
						model.productGroupId = "${model.productGroupId}";
						model.skipProductId = "${model.productId}";
						model.selectedIds = function() {
							return selectedIds;
						};
					}
				});
				
				var $mainForm = $('#mainForm');
				
				$mainForm.on('submit', function() {
					if (!$("[id^='selectedProductId']").val()) {
						bootbox.alert({
						    title: "Alert",
						    message: "<spring:message code='E00098' />"
						});
						return false;						
					}
				});
				
				<sec:authorize access="!hasPermission(#user, 256)">
					viewOnly();
				</sec:authorize>
			});	
			
			
		</script>
        <script id="trSpecTemplate" type="text/x-jquery-tmpl">
			<tr>
				<td class="text-center">	
					<p class="form-control-static">\${name}</p>														
				</td>
				<td class="text-center">
					<p class="form-control-static">\${value}</p>	
				</td>
			</tr>
		</script>
		<script id="trProdTemplate" type="text/x-jquery-tmpl">
			<tr>
				<td class="table-bordered text-center">	
					<input id="selectedProductId\${index}" type="hidden" value="\${productId}" name="selectedProductId[]"/>
					<p class="form-control-static">\${productId}</p>														
				</td>
				<td class="table-bordered text-center">
					<p class="form-control-static">\${noOfQuotation}</p>	
				</td>
				<td class="table-bordered text-center">
					<div class="col-xs-12">\${attribute1}</div>
					<div class="col-xs-12">\${attribute2}</div>	
					<div class="col-xs-12">\${attribute3}</div>	
				</td>
				<td class="table-bordered text-center">
					<p class="form-control-static">\${remark}</p>	
				</td>
				<td class="table-bordered text-center">
					<a href="javascript:void(0)" id="productRemove\${index}" name="productRemove[\${index}]" onclick="deleteSelectedIds(this);" class=" btn-delete" title='Delete'><span class='fa fa-times' aria-hidden='true'></span>
					</a>	
				</td>
			</tr>
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Product Cleansing</h1>
          <c:if test="${not empty model.productId}">
				<div class="breadcrumb form-horizontal" style="width:240px">
					<div class="form-group" style="margin-bottom:0px">
			        	<div class="col-sm-5">Created Date:</div>
			        	<div class="col-sm-7">${commonService.formatDateTime(model.createdDateDisplay)}</div>
			        </div>
			        <div class="form-group" style="margin-bottom:0px">
			         	<div class="col-sm-5">Last Modified:</div>
			         	<div class="col-sm-7">${commonService.formatDateTime(model.modifiedDateDisplay)}</div>
			         </div>
		      	</div>
	      	</c:if>
        </section>
        <section class="content">
        	<form id="mainForm" action="<c:url value='/productMaintenance/ProductMaintenance/cleaningSave'/>" method="get" role="form">
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/productMaintenance/ProductMaintenance/home'/>">Back</a>
							</div>
	       					<div class="box-body">
	       						<div class="form-horizontal">
	       							<input id="productId" name="productId" value="<c:out value="${model.productId}" />" type="hidden" />
									<div class="form-group row">
	       								<div class="col-md-offset-1 col-sm-12 col-md-10" >
	       									<h4>Original Product</h4>
											<table class='table table-hover dataTable table-bordered' id='attribute_list'>
												<thead>
													<tr>
														<th width="50%">
															<label class="control-label">Specification Name</label>
														</th>
														<th width="50%">
															<label class="control-label">Specification</label>
														</th>
													</tr>
												</thead>
											    <tbody class="specificationContainer">
											    </tbody>
											</table>
										</div>
									</div>
									<div class="row form-group">
	       								<div class="col-md-offset-1 col-md-5 col-sm-6">
	       								<c:choose>
											<c:when test="${model.photo1Path != null}">
												<input name="photo1Path" id="photo1Path" type="hidden" value="<c:out value="${model.photo1Path}" />"/>
												<img class="img-responsive viewer" src="<c:url value='/productMaintenance/ProductMaintenance/getImage'/>?productId=${model.productId}&amp;photoIndex=1&amp;bust=${niceDate.time}"/>
											</c:when>
											<c:otherwise>
												<img class="img-responsive" src="<c:url value='/resources/images/dummyphoto.png'/>"/>
											</c:otherwise>
										</c:choose>
	       								</div>
	       								<div class="col-md-5 col-sm-6">
	       								<c:choose>
											<c:when test="${model.photo2Path != null}">
												<input name="photo2Path" id="photo2Path" type="hidden" value="<c:out value="${model.photo2Path}" />"/>
												<img class="img-responsive viewer" src="<c:url value='/productMaintenance/ProductMaintenance/getImage'/>?productId=${model.productId}&amp;photoIndex=2&amp;bust=${niceDate.time}"/>
											</c:when>
											<c:otherwise>
												<img class="img-responsive" src="<c:url value='/resources/images/dummyphoto.png'/>"/>
											</c:otherwise>
										</c:choose>
	       								</div>
       								</div>
	       							<div class="row form-group">
	       								<label class="col-sm-2 control-label">Remarks</label>
	       								<div class="col-sm-4">
	       									<p class="form-control-static" >${model.remark}</p>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="box box-primary">
							<div class="form-group row">
      							<div class="col-md-offset-1 col-sm-12 col-md-10" >
      								<h4>Selected</h4>
									<table class='table table-hover dataTable' id='attribute_list'>
										<thead>
											<tr class="table-bordered">
												<th class="table-bordered">
													<label class="control-label">Product ID</label>
												</th>
												<th class="table-bordered">
													<label class="control-label">No. of quotations</label>
												</th>
												<th class="table-bordered">
													<label class="control-label">Attributes</label>
												</th>												
												<th class="table-bordered">
													<label class="control-label">Remarks</label>
												</th>
												<th class="table-bordered">
													<label class="control-label"></label>
												</th>																								
											</tr>
										</thead>
									    <tbody class="productContainer">
									    </tbody>
									    <tfoot>
									    	<tr>
									    		<td>
									    			<p>
														<button id="add" type="button" class='btn btn-sm btn-primary addnewrow pull-left'><span class="glyphicon glyphicon-plus"></span></button><span class="sr-only">Add</span>
													</p>
												</td>
											</tr>
									    </tfoot>    
									</table>
									<div class="clearfix">&nbsp;</div>
								</div>
							</div>
							<div class="box-footer">
								<sec:authorize access="hasPermission(#user, 256)">
									<button type="submit" class="btn btn-info pull-left">Submit</button>
								</sec:authorize>
							</div>
						</div>
					</div>

        		</div>
        	</form>
        </section>
	</jsp:body>
</t:layout>

