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
			.btn.pull-left {
				margin-right: 10px;
			}
		</style>
		<script src="<c:url value='/resources/js/jquery.tmpl.min.js'/>"></script>
		<script src="<c:url value='/resources/js/jquery.validate.custom.js'/>"></script>
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp"%>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp"%>
		<%@include file="/WEB-INF/views/includes/select2.jsp"%>
		<%@include file="/WEB-INF/views/includes/commonDialog/imageViewer.jsp" %>

		
		<script>
			
			$(function() {
				var $mainForm = $('#mainForm');

				$mainForm.validate({
					rules : {
						ignore: [],  
						countryOfOrigin: {
							required: true,
						},
						productGroupId: {
							required: true,
						},	
					},
					messages: {
						countryOfOrigin: {
							required: "<spring:message code='E00010' />",
						},
						productGroupId: {
							required: "<spring:message code='E00010' />",
						}
					}
				});
				
				<sec:authorize access="!hasPermission(#user, 256)">
					viewOnly();
				</sec:authorize>
			});	  	    
		    
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
				    $("#trTemplate").tmpl(specifications[index]).appendTo(".specificationContainer");
				    
			    	var options = [];
			    	
			    	if (specifications[index].value != null && specifications[index].value != "") {
			    		options.push({ id: specifications[index].value, text: specifications[index].value});
			    	} else {
			    		options.push({ id: "", text : ""});
			    	}
			    	
			    	if (specifications[index].option != null) {
				    	var data = specifications[index].option.split(';');
				        for (var j = 0; j < data.length; j++) {
				        	var new_options = {};
				        	new_options["id"] = data[j].trim();
				        	new_options["text"] = data[j].trim();
				        	if (specifications[index].value != data[j].trim()) {
				        		options.push(new_options);
				        	}
				        }
			    	}


				    if (specifications[index].isMandatory == "true") {
				    	$(":input[id='productSpecificationEditModels"+index+".value']").rules('add',"required");
				    } else {
				    	$(":input[id='productSpecificationEditModels"+index+".value']").rules('remove',"required");
				    }
				    
				    var productAttributeId = specifications[index].productAttributeId
		        	switch (specifications[index].attributeType) {
		        	case 1 :
				    	$(":input[id='productSpecificationEditModels"+index+".value']").select2ajax({
							placeholder: "Input a " + specifications[index].name ,
					        width: "100%",
					        tags: true,
					        allowClear: true,
				        	//minimumInputLength: 1,
					        ajax: {
					        	productAttributeId: productAttributeId,
					            quietMillis: 150,
					            url: "<c:url value='/productMaintenance/ProductMaintenance/queryProdAttrValueSelect2'/>",
					            dataType: "json",
					            /*data: function (term, page ) {
					            	return {
					            		productGroupId: productGroupId,
					              		//productAttributeId: productAttributeId,
					              		productAttributeId: this.productAttributeId,
				                    	term: term["term"], 
				                    	page: page
					              	};
					            },*/
					            data: function (term) {
					            	return $.extend(term, 
					            					{
					            						productGroupId: productGroupId,
					            						productAttributeId: this.productAttributeId
					            					});
					            }
					   	  	},
					        createSearchChoice: function(term, data) { 
			                    if ($(data).filter(function() { return this.text.localeCompare(term)===0; }).length===0) {
			                    return {id:term, text:term};}
					        },
					        initSelection: function(element, callback) {
					        	callback(options);
					        }
						});
		        		break;
		        	case 2 :
				    	$(":input[id='productSpecificationEditModels"+index+".value']").select2({
							placeholder: "Select a " + specifications[index].name ,
							data: options,
							allowClear: true,
							width: "100%",
						});
				    	break;
		        	case 3 :
				    	$(":input[id='productSpecificationEditModels"+index+".value']").select2({
							placeholder: "Input a " + specifications[index].name ,
							data: options,
					        width: "100%",
					        tags: true,
					        allowClear: true,
					        createSearchChoice: function(term, data) { 
			                    if ($(data).filter(function() { return this.text.localeCompare(term)===0; }).length===0) {
			                    return {id:term, text:term};}
					        }
						});
				    	break;	    
					}
				}
			}
			$(document).ready(function() {	
				
				if ( "${act}" == "edit") {
					loadSpecficationTable("${model.productId}","${model.productGroupId}");
					$('#productGroupId').select2({
						placeholder: "Select a Product Type"
					});
					$.get($('#productGroupId').data('singleUrl'), { id: "${model.productGroupId}" }, 
						function(data) {
						var option = new Option(data, "${model.productGroupId}");
						option.selected = true;
						$('#productGroupId').append(option);
						$('#productGroupId').trigger('change');
					});
					$('#productGroupId').prop("disabled", true);
				}	else {
					
					$('#productGroupId').select2ajax({
						placeholder: "Select a Product Type"
					});	
					
			    	$('#productGroupId').on("change", function(e) {
			    		$(".specificationContainer").empty();
			    		loadSpecficationTable(0, $('#productGroupId').val());
			    	});
				}		
				/*
		    	var country = [];
		    	var countryString = "${countryList}"
		    	
		    	if ("${model.countryOfOrigin}" != "") {
		    		country.push({ id: "${model.countryOfOrigin}", text: "${model.countryOfOrigin}"});
		    	} else {
		    		country.push({ id: "", text : ""});
		    	}
		    	
		    	if (countryString != "") {
			    	var data = countryString.split(';');
			        for (var j = 0; j < data.length; j++) {
			        	var new_country = {};
			        	new_country["id"] = data[j].trim();
			        	new_country["text"] = data[j].trim();
			        	if ("${model.countryOfOrigin}" != data[j].trim()) {
			        		country.push(new_country);
			        	}
			        }
		    	}
		    	
		    	$("#countryOfOrigin").select2({
					placeholder: "Select a Country of Origin" ,
					data: country,
					allowClear: true,
					width: "100%",
					tags: true
				});
		    	*/
		    	var countryString = "";
		    	<c:if test="${not empty country}">
		    		countryString = ${country};
		    	</c:if>
		    	
		    	var countryListArray = [];
		    	<c:if test="${not empty countryList}">
		    		countryListArray = ${countryList};
		    	</c:if>
		    	
		    	var selectCountryList = [];
		    	selectCountryList.push({"id":"", "text":""});
		    	
		    	if(countryListArray.length > 0) {
		    		if(countryString.length > 0) {
			    		if(jQuery.inArray(countryString, countryListArray) == -1) {
			    			countryListArray.push(countryString);
			    		}
		    		}
		    		for(var i = 0; i < countryListArray.length; i++) {
		    			if(countryListArray[i] == countryString) {
		    				// append to select
		    				//selectCountryList.push(new Option(countryListArray[i], countryListArray[i], true));
		    				// insert into select2 as data
		    				selectCountryList.push({"id":countryListArray[i], "text":countryListArray[i], "selected":"selected"});
		    			} else {
		    				//selectCountryList.push(new Option(countryListArray[i], countryListArray[i]));
		    				selectCountryList.push({"id":countryListArray[i], "text":countryListArray[i]});
		    			}
		    		}
		    	} else {
		    		if(countryString.length > 0) {
		    			//selectCountryList.push(new Option(countryString, countryString, true));
		    			selectCountryList.push({"id":countryString, "text":countryString, "selected":"selected"});
		    		}
		    	}
		    	
		    	$("#countryOfOrigin").select2({
					placeholder: "Select a Country of Origin" ,
					data: selectCountryList,
					allowClear: true,
					width: "100%",
					tags: true
				});
		    	
		    	<sec:authorize access="!hasPermission(#user, 256)">
					viewOnly();
				</sec:authorize>
			});
			
			function deleteImg(i){       
			    var photopath = $("[name='photo"+ i +"Path']").val();
			    $("#photo"+i).attr("src","<c:url value='/resources/images/dummyphoto.png'/>");
			    $("#deletePhoto"+i).hide();
			    $("[name='photo"+ i +"Path']").val(photopath + '/del'); 
			}

			function resetPhotoPath(i, photopath){
			    $("[name='photo"+ i +"Path']").val(photopath);
			}
			
		</script>
        <script id="trTemplate" type="text/x-jquery-tmpl">
			<tr>
				<td>															
					<label class="control-label">\${name}</label>
				</td>
				<td>
					<input  id="productSpecificationEditModels\${index}.productAttributeId" name="productSpecificationEditModels[\${index}].productAttributeId" type="hidden" value="\${productAttributeId}">			
					{{if attributeType > 0}}
						<select id="productSpecificationEditModels\${index}.value" name="productSpecificationEditModels[\${index}].value"
						class="form-control select2" >
						<option selected>\${value}<option>
						</select>
					{{else}}
						<input id="productSpecificationEditModels\${index}.value" name="productSpecificationEditModels[\${index}].value" 
						type="text" class="form-control select2" value="\${value}" />
					{{/if}}
				</td>
			</tr>
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Product Maintenance</h1>
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
        	<form id="mainForm" action="<c:url value='/productMaintenance/ProductMaintenance/save?act='/>${act}" method="post" role="form"
        		enctype="multipart/form-data">
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/productMaintenance/ProductMaintenance/home'/>">Back</a>
							</div>
	       					<div class="box-body">
	       						<div class="form-horizontal">
	       							<div class="form-group">
	       								<label class="col-sm-2 control-label">Product ID</label>
	       								<div class="col-sm-4">
	       									<c:choose>
		       									<c:when test="${act eq 'add'}">
													<p class="form-control-static" >auto-generated</p>
												</c:when>
												<c:otherwise>
													<p class="form-control-static" >${model.productId}</p>
													<input id="productId" name="productId" value="<c:out value="${model.productId}" />" type="hidden" />
												</c:otherwise>
											</c:choose>
										</div>
									</div>
	       							<div class="form-group">
	       								<label class="col-sm-2 control-label">Country of Origin</label>
	       								<div class="col-sm-4">
											<select id="countryOfOrigin" name="countryOfOrigin" class="form-control"/></select>
										</div>
									</div>
									<div class="form-group">
	       								<label class="col-sm-2 control-label">Product Type</label>
	       								<div class="col-sm-4">
	       									<select name="productGroupId" class="form-control select2" id="productGroupId"
													data-ajax-url="<c:url value='/productMaintenance/ProductMaintenance/queryProductGroupSelect2'/>"
													data-single-url="<c:url value='/productMaintenance/ProductMaintenance/queryProductGroupSelectSingle'/>"
													/></select>
										</div>
									</div>
									<div class="form-group">
	       								<label class="col-sm-2 control-label">Barcode</label>
	       								<div class="col-sm-4">
											<input name="barcode" type="text" class="form-control" value="<c:out value="${model.barcode}" />" />
										</div>
									</div>
									<div class="form-group row">
	       								<div class="col-md-offset-1 col-sm-12 col-md-10" >
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
											<c:when test="${model.photo1Path != null }">
												<input name="photo1Path" id="photo1Path" type="hidden" value="<c:out value="${model.photo1Path}" />"/>
<%-- 												<img class="img-responsive viewer" src="<c:url value='/productMaintenance/ProductMaintenance/getImage'/>?productId=${model.productId}&amp;photoIndex=1&amp;bust=${niceDate.time}"/> --%>
												<img class="img-responsive viewer" id="photo1"
														src="<c:url value='/productMaintenance/ProductMaintenance/getImage'/>?productId=${model.productId}&amp;photoIndex=1&amp;bust=${niceDate.time}" />
												<button style="width: 40%" type="button" id="deletePhoto1"
														onclick="deleteImg(1)">
												    <span>Remove</span>
												</button>
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
<%-- 												<img class="img-responsive viewer" src="<c:url value='/productMaintenance/ProductMaintenance/getImage'/>?productId=${model.productId}&amp;photoIndex=2&amp;bust=${niceDate.time}"/> --%>
												<img class="img-responsive viewer" id="photo2"
														src="<c:url value='/productMaintenance/ProductMaintenance/getImage'/>?productId=${model.productId}&amp;photoIndex=2&amp;bust=${niceDate.time}" />
												<button style="width: 40%" type="button" id="deletePhoto2"
														onclick="deleteImg(2)">
												    <span>Remove</span>
												</button>
											</c:when>
											<c:otherwise>
												<img class="img-responsive" src="<c:url value='/resources/images/dummyphoto.png'/>"/>
											</c:otherwise>
										</c:choose>
	       								</div>
       								</div>
       								<div class="row form-group">
	       								<div class="col-md-offset-1 col-md-5 col-sm-6">
		       								<input name="photo1" type="file" onchange="resetPhotoPath(1, '${model.photo1Path}')"/>
<!-- 	       									<input name="photo1" type="file" /> -->
	       								</div>
	       								<div class="col-md-5 col-sm-6">
<!-- 	       									<input name="photo2" type="file" /> -->
	       									<input name="photo2" type="file" onchange="resetPhotoPath(2, '${model.photo2Path}')"/>
	       								</div>
	       							</div>
	       							<div class="row form-group">
	       								<label class="col-sm-2 control-label">Remarks</label>
	       								<div class="col-sm-4">
											<input name="remark" type="text" class="form-control" value="<c:out value="${model.remark}" />" maxlength="2000" />
										</div>
									</div>
									<div class="form-group  radio-group">
	       								<label class="col-sm-2 control-label"  style="color:#333">Status</label>
	       								<div class="col-sm-4">
	       									<label class="radio-inline">
												<input name="Status" type="radio" required value="Active" ${model.status == "Active" ? "Checked" : ""} /> Active
											</label>
											<label class="radio-inline">
												<input name="Status" type="radio" required value="Inactive" ${model.status == "Inactive" ? "Checked" : ""} /> Inactive
											</label>
										</div>
									</div>
									<div class="form-group">
	       								<label class="col-sm-2 control-label">Reviewed</label>
	       								<div class="col-sm-4">
	       									<label class="checkbox-inline">
											<input name="Reviewed" type="checkbox" value="true" ${model.reviewed ? "Checked" : ""} /> Yes
											</label>
										</div>
									</div>
								</div>
							</div>
							<div class="box-footer">	
	       						<sec:authorize access="hasPermission(#user, 256)">
	       							<button type="submit" class="btn btn-info pull-left">Submit</button>
	       							<c:if test="${model.productId > 0 }">
	       								<a class="btn btn-info pull-left" href='<c:url value='/productMaintenance/ProductMaintenance/cleaning?id='/>${model.productId}' class='btn btn-default' role='button' target='_blank'>Product Cleaning</a>
	       							</c:if>
	       						</sec:authorize>
       						</div>
						</div>
					</div>
        		</div>
        	</form>
        </section>
	</jsp:body>
</t:layout>

