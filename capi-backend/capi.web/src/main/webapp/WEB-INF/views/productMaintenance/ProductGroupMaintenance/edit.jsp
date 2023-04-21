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
		<%@include file="/WEB-INF/views/includes/select2-css.jsp"%>
		<!-- Bootstrap styling for Typeahead -->
		<link href="<c:url value='/resources/css/tokenfield-typeahead.css'/>" type="text/css" rel="stylesheet">
		<!-- Tokenfield CSS -->
		<link href="<c:url value='/resources/css/bootstrap-tokenfield.css'/>" type="text/css" rel="stylesheet">		
	</jsp:attribute>

	<jsp:attribute name="header">
		<style>			
			.btn.pull-right {
				margin-left: 10px;
			}
			.ui-sortable tr {
			    cursor:pointer; 
			}   
		</style>
		
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp"%>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp"%>
		<%@include file="/WEB-INF/views/includes/select2.jsp"%>
		<script src="<c:url value='/resources/js/jquery-ui.min.js'/>"></script>
		<script src="<c:url value='/resources/js/jquery.validate.custom.js'/>"></script>
		<script src="<c:url value='/resources/js/bootstrap-tokenfield.min.js'/>"></script>
		<script src="<c:url value='/resources/js/jquery.tmpl.min.js'/>"></script>
		<script>
			$(function() {
				var $mainForm = $('#mainForm');
				$mainForm.validate({
					rules : {
						code: {
							required: true,
							minlength: 13
						},
						'productAttributes[].specificationName': {
							required: true
						}
					},
					messages: {
						code: {
							required: "<spring:message code='E00010' />",
							minlength: "<spring:message code='E00097' arguments='17' />"
						},
						'productAttributes[].specificationName': {
							required: "<spring:message code='E00010' />"
						}		
					}
				});
				
				<sec:authorize access="!hasPermission(#user, 256)">
					viewOnly();
				</sec:authorize>
			});
			
			var lastRowId = ${attributes.size()};
			
			$(window).resize(function() {
				$(":input[name$='option']").tokenfield('destroy');
				$(":input[name$='option']").tokenfield({
					delimiter : ';'
				});
			});
			
			$(document).ready(function () {
			    $('#add').click( function (e) {
			    	var sequence = lastRowId;
			    	var attribute = {};
			    	attribute["index"] = lastRowId;
			    	attribute["sequence"] = eval(lastRowId)+1;
			    	attribute["attributeType"] = 1;

			    	$("#trTemplate").tmpl(attribute).appendTo(".attributeContainer",attribute);

					$(":input[name$='option']").tokenfield({
						delimiter : ';' 
					});

		            $(":input[id='productAttributes"+lastRowId+".attributeType']").change(function(RowId)
		            		{
		            			if (this.checked) {
			            			if (this.value == '1'){
			            				$(":input[id='"+this.id.split(".")[0]+".option']").tokenfield('setTokens', '');
			            				$(":input[id='"+this.id.split(".")[0]+".option']").tokenfield('disable');
			            			}
			            			else {
			            				$(":input[id='"+this.id.split(".")[0]+".option']").tokenfield('enable');
			            			}
		            			}
		            		}
		           	);
		            
		            $(":input[id='productAttributes"+lastRowId+".attributeType']").trigger('change');
		            
					$(":input[id='productAttributes"+lastRowId+".remove']").click(function () {
						var self = this;
						bootbox.confirm({
							title:"Confirmation",
							message: "<spring:message code='W00001' />",
							callback: function(result){
								if (result){
									$(self).parent().parent().remove();
								}
							}
						})
						//$(this).parent().parent().remove();
					});
					
		            lastRowId++;
			    });
			    
			    <sec:authorize access="!hasPermission(#user, 256)">
					viewOnly();
				</sec:authorize>
			});
			
		    // Create an array of attribute
	        /*var attributes = [   
	          	<c:forEach items="${attributes}" var="attribute" varStatus="status">	          	
	            { 	index: "${status.index}", 
	            	productAttributeId: "${attribute.productAttributeId}", 
	            	sequence: "${attribute.sequence}", 
	            	specificationName: "${attribute.specificationName}", 
	            	isMandatory: "${attribute.isMandatory eq true ? 'checked' : ''}", 
	            	attributeType: "${attribute.attributeType}",
	            	option: "${attribute.option}"  },
	            </c:forEach>
	        ];*/
	        var attributes = [];
	        <c:if test="${productAttributes != null && productAttributes != ''}">
	        	attributes = ${productAttributes};
	        </c:if>

			$(document).ready(function() {
				
		        // Render the attributes using the template
		        $("#trTemplate").tmpl(attributes).appendTo(".attributeContainer");

				//Helper function to keep table row from collapsing when being sorted
				var fixHelperModified = function(e, tr) {
					var $originals = tr.children();
					var $helper = tr.clone();
					$helper.children().each(function(index)
					{
					  $(this).width($originals.eq(index).width())
					});
					return $helper;
				};

				//Make table sortable
				$("#attribute_list tbody").sortable({
			    	helper: fixHelperModified,
					stop: function(event,ui) {set_radio();renumber_table("#attribute_list");},
					start: function (e, tr) {
                        var radio_checked= {};                  
                        $(":input[name$='attributeType']").each(function(){
                                if($(this).is(':checked')) {
                                       radio_checked[$(this).attr('name')] = $(this).val();
                                }
                                $(document).data('radio_checked', radio_checked);
                        });                 
                	}
				});
				
				$(":input[name$='option']").tokenfield({
				delimiter : ';'
				});
			
				$(":input[name$='option']").on('tokenfield:createtoken', function (event) {
				    var existingTokens = $(this).tokenfield('getTokens');
				    $.each(existingTokens, function(index, token) {
				        if (token.value === event.attrs.value)
				            event.preventDefault();
				    });
				});
				
				<c:forEach items="${attributes}" var="attribute" varStatus="status">
	            $(":input[id='productAttributes${status.index}.attributeType']").change(function()
	            		{
	            			if (this.checked) {
		            			if (this.value == '1'){
		            				$(":input[id='productAttributes${status.index}.option']").tokenfield('setTokens', '');
		            				$(":input[id='productAttributes${status.index}.option']").tokenfield('disable');
		            			}
		            			else {
		            				$(":input[id='productAttributes${status.index}.option']").tokenfield('enable');
		            			}
	            			}
	            		}
	           	);
	            
				$(":input[id='productAttributes${status.index}.remove']").click(function () {	
					var self = this;
					/*var inUse;
					$.ajax({
						url: "<c:url value='/productMaintenance/ProductGroupMaintenance/attributeInUse'/>" , 
						method: "POST",
						data: { productAttributeId : ${attribute.productAttributeId} },
						datatype: "text",
						async : false,
						success : function(data) { inUse = data}
					});
					if (inUse == "true" && !confirm('<spring:message code="W00001" />')) return;
					*/
					bootbox.confirm({
						title:"Confirmation",
						message: "<spring:message code='W00001' />",
						callback: function(result){
							if (result){
								$(self).parent().parent().remove();
							}
						}
					})
					
					//$(this).parent().parent().remove();
				});
	            
	            </c:forEach>
	            
				$(":input[name$='attributeType']").trigger('change');
				
				<sec:authorize access="!hasPermission(#user, 256)">
					viewOnly();
				</sec:authorize>
			});
			
			//Check Attribute Type Radio
			function check_attribute_type(typeID) {
				if (this.data.attributeType == typeID) {
					return "checked";
				}
				return "";
			}
			
			//Set Radio
			function set_radio() {
				var radio_restore = $(document).data('radio_checked');
				$.each(radio_restore, function(index, value){
                    $('input[name="'+index+'"][value="'+value+'"]').trigger('click');
                });
			}
			
			function deleteRecordsWithConfirm(data) {
				$.post("<c:url value='/productMaintenance/ProductGroupMaintenance/delete'/>",
					data,
					function(response) {
						$("#dataList").DataTable().ajax.reload();
						$("#MessageRibbon").html(response);
					}
				);
			}
			
			//Renumber table rows
			function renumber_table(tableID) {
				$(tableID + " tr").each(function() {
					count = $(this).parent().children().index($(this));

					$(":input[id$='sequence']", this).each(function() {
								$(this).attr('value', count + 1);
					});
				});
			}
				
		</script>
        <script id="trTemplate" type="text/x-jquery-tmpl">
			<tr class="hndle">
				<td width="30%">
					<span>
						<input id="productAttributes\${index}.productAttributeId" type="hidden" value="\${productAttributeId}" name="productAttributes[\${index}].productAttributeId"/>
						<input id="productAttributes\${index}.sequence" type="hidden" value="\${sequence}" name="productAttributes[\${index}].sequence"/>
						<input id="productAttributes\${index}.specificationName" type="text" name="productAttributes[\${index}].specificationName" class="form-control" value="\${specificationName}" maxlength="1024" required/>
					</span>																	
					<label><input id="productAttributes\${index}.isMandatory" type="checkbox" name="productAttributes[\${index}].isMandatory" \${isMandatory} /> Mandatory</label>
				</td>
				<td width="65%">
					<label><input id="productAttributes\${index}.attributeType" type='radio' value="1" name="productAttributes[\${index}].attributeType"
						\${check_attribute_type(1)} />Text</label>
					<label><input id="productAttributes\${index}.attributeType" type='radio' value="2" name="productAttributes[\${index}].attributeType"
						\${check_attribute_type(2)} />Options</label>
					<label><input id="productAttributes\${index}.attributeType" type='radio' value="3" name="productAttributes[\${index}].attributeType"
						\${check_attribute_type(3)} />Options with Others :</label> 
					<input id="productAttributes\${index}.option" type="text" name="productAttributes[\${index}].option"
						class="form-control" value="\${option}"	maxlength="1024" required/>
				</td>
				<td width="5%">
					<button id="productAttributes\${index}.remove" name="productAttributes\${index}.remove" type="button" class="close" ><span aria-hidden="true">&times;</span><span class="sr-only">Remove</span>
					</button>
				</td>
			</tr>
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
        	<h1>Product Type Maintenance</h1>
        	<c:if test="${not empty model.productGroupId}">
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
        	<form id="mainForm" action="<c:url value='/productMaintenance/ProductGroupMaintenance/save?act='/>${act}" method="post" role="form"
        		enctype="multipart/form-data">
        		<input name="productGroupId" value="<c:out value="${model.productGroupId}" />" type="hidden" />
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/productMaintenance/ProductGroupMaintenance/home'/>">Back</a>
							</div>
	       					<div class="box-body">
	       						<div class="form-horizontal">
	       							<div class="col-sm-12">
	       							    <div class="form-group">
	       							    	<div class="col-md-4 col-md-offset-8">
	       							    	    <c:choose>
			       									<c:when test="${act ne 'add'}">
														<p class="text-right"><a href="<c:url value='/productMaintenance/ProductMaintenance/home?productGroupId='/>${model.productGroupId}" target="_blank"><span class="glyphicon glyphicon-search"></span> No. of product included : <span class="badge">${model.noOfProduct}</span></a></p>
	       							    				<p class="text-right">No. of Variety included : <span class="badge">${model.noOfUnit}</span></p>
													</c:when>
												</c:choose>
	       							    	</div>
	       							    </div>
		       							<div class="form-group">
		       								<label class="col-sm-2 control-label">Code</label>
		       								<div class="col-sm-4">
		       									<c:choose>
			       									<c:when test="${act eq 'add'}">
														<input id="code" name="code" type="text" class="form-control" value="<c:out value="${model.code}" />" maxlength="17" />
													</c:when>
													<c:otherwise>
														<p class="form-control-static" >${model.code}</p>
														<input name="code" value="<c:out value="${model.code}" />" type="hidden" />
													</c:otherwise>
												</c:choose>
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-2 control-label">Chinese Name</label>
		       								<div class="col-sm-4">
												<input name="chineseName" type="text" class="form-control" value="<c:out value="${model.chineseName}" />" maxlength="2000" />
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-2 control-label">English Name</label>
		       								<div class="col-sm-4">
												<input name="englishName" type="text" class="form-control" value="<c:out value="${model.englishName}" />" maxlength="2000" />
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-2 control-label">Status</label>
		       								<div class="col-sm-4">
												<label>
													<input type="radio" value="Active" name="status" <c:if test="${model.status != 'Inactive'}">checked</c:if> />Active
												</label>
												&nbsp;
												<label>
													<input type="radio" value="Inactive" name="status" <c:if test="${model.status eq 'Inactive'}">checked</c:if> />Inactive
												</label>
											</div>
										</div>
										
										<div class="form-group">
		       								<label class="col-sm-2 control-label">Attributes</label>
		       								<div class="col-sm-10">
												<table class='table table-hover dataTable no-footer' id='attribute_list'>
											        <tbody class="attributeContainer">
											        </tbody>
												</table>
												<p>
													<button id="add" type="button" class='btn btn-sm btn-primary addnewrow pull-left'><span class="glyphicon glyphicon-plus"></span></button><span class="sr-only">Add</span>
												</p>
											</div>
										</div>
									</div>
								</div>
							</div>
	       					<sec:authorize access="hasPermission(#user, 256)">
								<div class="box-footer">
	        						<button type="submit" class="btn btn-info" onclick="renumber_table('#attribute_list')">Submit</button>	       						
	       						</div>
	       					</sec:authorize>
						</div>
	        		</div>
	        	</div>
        	</form>
        </section>
	</jsp:body>
</t:layout>

