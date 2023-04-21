<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page session="false"%>
<jsp:useBean id="niceDate" class="java.util.Date" />
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp"%>
	</jsp:attribute>
	<jsp:attribute name="header">
		<style>
			.btn.pull-left {
				margin-right: 10px;
			}
		</style>
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp"%>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp"%>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp"%>
		<%@include file="/WEB-INF/views/includes/commonDialog/imageViewer.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/productAttributeLookup.jsp" %>
		<script src="<c:url value='/resources/js/jquery.tmpl.min.js'/>"></script>
		<script>	    
			var lastRowId = 0;
			var specifications = [];
			var options = [];
		
			function loadAttributeList(productGroupId) {

				function productAttributeSelectOnChange() {
					$(".productContainer").html("");
			    	loadAttributeTable($(this).select2('data')[0].id);
				}
				
				options.length=0;

			    $.ajax({
			        /*url:'<c:url value='/productMaintenance/ProductMaintenance/queryProductSpecificationList/'/>',*/
			        url:'<c:url value='/productMaintenance/ProductMaintenance/queryProductAttributeList/'/>',
			        async: false,
			        type:'get',
			        data:/*'productGroupId='+productGroupId*/{
			        	productGroupId: productGroupId/*,
			        	productId: $('#productId').val()*/
			        },
			        success:function(data){;
			            specifications = data;
			        }
			    });
			    
		    	options.push({ id: "", text : ""});
		    	
				for (index = 0, len = specifications.length; index < len; ++index) {
					
			    	if (specifications[index].name != null && specifications[index].name != "") {
			    		options.push({ id: index, text: specifications[index].name});
			    	} else {
			    		options.push({ id: "", text : ""});
			    	}
				}

				if ($("#productAttributeSelect").data('select2') ) {
					$("#productAttributeSelect").select2('destroy');
					$("#productAttributeSelect").html('');
				}
				
			    $("#productAttributeSelect").select2({
						placeholder: "Select a Attribute",
						data: options,
				        width: "100%"    
				}).on("change", productAttributeSelectOnChange).each(productAttributeSelectOnChange);
			}
			
			function loadAttributeTable(index){
				if (index != null && index != ""){
					var values = [{ id: "", text : ""}];
					
			    	if (specifications[index].option != null) {
				    	var data = specifications[index].option.split(';');
				        for (var j = 0; j < data.length; j++) {
				        	var new_options = {};
				        	new_options["id"] = data[j].trim();
				        	new_options["text"] = data[j].trim();
				        	if (specifications[index].value != data[j].trim()) {
				        		values.push(new_options);
				        	}
				        }
			    	}
	
				    if (specifications[index].isMandatory == "true") {
				    	$("#attributeValue").rules('add',"required");
				    } else {
				    	$("#attributeValue").rules('remove',"required");
				    }
				    
				    $("#productAttributeId").val(function() { 
				    	return specifications[index].productAttributeId;
				    });
				    $("#productAttributeName").val(specifications[index].name);
				    
					if ($("#attributeValue").data('select2') ) {
						$("#attributeValue").select2('destroy');
						$("#attributeValue").html('');
					}
					
		        	if (specifications[index].attributeType != 2) {
				    	$("#attributeValue").select2({
							placeholder: "Input a " + specifications[index].name ,
							data: values,
					        width: "100%",
					        tags: true,
					        allowClear: true,
					        createSearchChoice: function(term, data) { 
			                    if ($(data).filter(function() { return this.text.localeCompare(term)===0; }).length===0) {
			                    return {id:term, text:term};}
					        }
						});
				    	
		        	} else {
			    	
				    	$("#attributeValue").select2({
							placeholder: "Select a " + specifications[index].name ,
							data: values,
							allowClear: true,
							width: "100%",
						});
			   		}		   
		        	 $('#attributeValue').trigger('change')
		        	 $("#Selection").show();
				}
			}

			$(document).ready(function() {	

				var $mainForm = $('#mainForm');

				$mainForm.validate({
					rules : {
						ignore: [],  
						productGroupSelect: {
							required: true,
						},						
						productAttributeSelect: {
							required: true,
						},
						attributeValue: {
							required: true,
						}
					},
					messages: {
						productGroupSelect: {
							required: "<spring:message code='E00010' />",
						},
						productAttributeSelect: {
							required: "<spring:message code='E00010' />",
						},
						attributeValue: {
							required: "<spring:message code='E00010' />",
						}	
					}
				});	

				$("#Selection").hide();


				
				$('#productGroupSelect').select2ajax({
					placeholder: "Select a Product Type",
					initSelection: function (element, callback) {
			            callback({ id: "${model.productGroupId}", text: "${model.productGroupText}" });
			            var option = new Option( "${model.productGroupText}", "${model.productGroupId}");
						option.selected = true;
						$('#productGroupSelect').append(option);
					}
				}).on("change", function(e) {
			        $("#Selection").hide();
			        loadAttributeList($(this).select2('data')[0]['id']);
			        $("#productGroupId").val($(this).select2('data')[0]['id'])
			        $("#productGroupName").val($(this).select2('data')[0]['text']);
				});
				$('#productGroupSelect').trigger('change');
				
				Modals.init();

				var selectedIds = [];
				
				$('#add').productAttributeLookup({
					queryDataCallback: function(model) {
						model.productGroupId = function() {
							return $('#productGroupId').val();
						};
						model.productAttributeId = function() { 
							return $('#productAttributeId').val();
						};
						model.productGroupText = function() {
							return $('#productGroupName').val();
						};
						model.productAttributeText = function() { 
							return $('#productAttributeName').val();
						};
						model.selectedIds = function() {
							return selectedIds;
						}
					},
					selectedIdsCallback: function(productIds) {
						
						
						console.log("productIds:" + productIds);
						//var table = $(".datatable").dataTable().fnGetData();	
						var table = [];
						
					    $.ajax({
					        url:'<c:url value='/commonDialog/ProductAttributeLookup/queryAll'/>',
					        async: false,
					        type: 'post',
					        data: {productIds : productIds, productAttributeId : $('#productAttributeId').val() },
					        success:function(data){
					        	table = data;
					        	for(var i = 0; i < data.length; i++) {
					        		selectedIds.push(data[i].productId);
					        	}
					        }
					    });
					    
				    	for (var idx in productIds) {		    	
				    		var product = {};
				    		product["index"] = lastRowId;
							for (var i=0 ; i < table.length ; i++) {
								if ( table[i]["productId"] == productIds[idx]){	
									product["productId"] = table[i]["productId"];
									product["attributeValue"] = table[i]["attributeValue"];
	                        		break;
								}
							}

							if (!$("[name^=selectedProductId]").val() || $.inArray(productIds[idx], $("[name^=selectedProductId]").map(function(){return eval($(this).val());}).get()) == -1){
								$("#trProdTemplate").tmpl(product).appendTo(".productContainer");
							}
						}

				    	$(":input[id='productRemove"+lastRowId+"']").click(function () {
				    		var self=this;
							bootbox.confirm({
								title:"Confirmation",
								message: "<spring:message code='W00001' />",
								callback: function(result){
									if (result){
										var productId = $(self).parent().parent().find('td:first-child').find('input').attr('value')
										selectedIds = jQuery.grep(selectedIds, function(value) {
											return value != productId;
										});
										$(self).parent().parent().remove();
									}
								}
							})
						});	
			            lastRowId++;
					}
				});
				
				<sec:authorize access="!hasPermission(#user, 256)">
					viewOnly();
				</sec:authorize>
		    });
		</script>
		<script id="trProdTemplate" type="text/x-jquery-tmpl">
			<tr>
				<td class="table-bordered text-center">	
					<input id="selectedProductId\${index}" type="hidden" value="\${productId}" name="selectedProductId[]"/>
					<p class="form-control-static">\${productId}</p>														
				</td>
				<td class="table-bordered text-center">
					<p class="form-control-static">\${attributeValue}</p>	
				</td>
				<td class="table-bordered text-center">
					<button id="productRemove\${index}" name="productRemove\${index}" type="button" class="close" ><span aria-hidden="true">&times;</span><span class="sr-only">Remove</span>
					</button>	
				</td>
			</tr>
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Bulk update</h1>
        </section>
        <section class="content">
        	<form id="mainForm" action="<c:url value='/productMaintenance/ProductMaintenance/bulkSave'/>" method="get" role="form">
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
									<div class="form-group">
	       								<label class="col-sm-2 control-label">Product Type</label>
	       								<div class="col-sm-4">
	       									<input name="productGroupId" id="productGroupId" type="hidden"/>
	       									<input name="productGroupName" id="productGroupName" type="hidden"/>
	       									<select name="productGroupSelect" class="form-control select2" id="productGroupSelect"
													data-ajax-url="<c:url value='/productMaintenance/ProductMaintenance/queryProductGroupSelect2'/>"></select>
										</div>
									</div>
									<div class="form-group">
	       								<label class="col-sm-2 control-label">Attribute Name</label>
	       								<div class="col-sm-4">
	       									<input name="productAttributeId" id="productAttributeId" type="hidden"/>
	       									<input name="productAttributeName" id="productAttributeName" type="hidden"/>
	       									<select name="productAttributeSelect" class="form-control select2" id="productAttributeSelect"></select>
										</div>
									</div>
								</div>
							</div>
							<div id="Selection" class="box-body">
	       						<div class="form-horizontal">
									<div class="row form-group">
	       								<label class="col-sm-2 control-label">New Value</label>
	       								<div class="col-sm-4">
											<select id="attributeValue" name="attributeValue" class="form-control"></select>
										</div>
									</div>
									<div class="form-group row">
	       								<div class="col-md-offset-1 col-sm-12 col-md-10" >
											<table class='table table-hover dataTable table-bordered' id='attribute_list'>
												<thead>
													<tr>
														<th class="col-md-3">
															<label class="control-label">Product ID</label>
														</th>
														<th class="col-md-8">
															<label class="control-label">Attribute</label>
														</th>
														<th class="col-md-1">
															<label class="control-label"></label>
														</th>
													</tr>
												</thead>
											    <tbody class="productContainer">
											    </tbody>
											    <tfoot>
											    	<tr>
											    		<td colspan="3">
											    			<p>
																<label class="control-label"><button id="add" type="button" class='btn btn-sm btn-primary pull-left'><span class="glyphicon glyphicon-plus"></span></button><span class="sr-only">Add</span>&nbsp; Attribute Lookup </label>
															</p>
														</td>
													</tr>
											    </tfoot>
											</table>
										</div>
									</div>
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

