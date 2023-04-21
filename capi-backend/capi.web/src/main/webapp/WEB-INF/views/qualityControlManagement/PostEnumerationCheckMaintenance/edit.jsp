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
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/timepicker-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>	
	</jsp:attribute>
	<jsp:attribute name="header">
		<%@include file="/WEB-INF/views/includes/moment.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>	
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker.jsp"%>
		<%@include file="/WEB-INF/views/includes/timepicker.jsp" %>		
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/shared/quotationRecord/outletEdit-js.jsp"%>		
		<script src="<c:url value='/resources/js/underscore-min.js'/>" ></script>
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
		.glyphicon-pencil, .glyphicon-remove {
    		cursor: pointer;
		}
		</style>
		<script>
			
			function addPEComment(comment) {
				$('#peCheckRemark').val($('#peCheckRemark').val() + unescape(comment) +'\n');
			}
			
			var checkingDate = '<c:out value="${model.checkingDateText}"/>';
			
			$(document).ready(function(){
				$(".select2ajax").select2ajax();
				$('#mainForm').validate({
					rules : {
						contactPerson: {
							required: {
								depends: function(element) {
									return !($("#nonContact").is(":checked")) && $('#status').val() == "Submitted";
								}
							}
						},
						otherRemark: {
							required: {
								depends: function(element) {
									return ($("#nonContact").is(":checked") || $("#peCheckRemark").val() == '') && $('#status').val() == "Submitted";
								}
							}
						},
						peCheckRemark:{
							required: {
								depends: function(element) {
									return $("#otherRemark").val() == '' && $('#status').val() == "Submitted";
								}
							}
						},
						checkingMode: {
							required: {
								depends: function (element){
									 return $('#status').val() == "Submitted";
								}
							}
						},
						checkingDateText :{
							required: {
								depends: function (element){
									 return $('#status').val() == "Submitted";
								}
							}
						},
						checkingTimeText :{
							required: {
								depends: function (element){
									 return $('#status').val() == "Submitted";
								}
							}
						}
					},
					messages: {
						contactPerson: {
							required: "<spring:message code='E00010' />",
						},
						otherRemark: {
							required: "<spring:message code='E00010' />",
						},
						checkingMode: {
							required: "<spring:message code='E00010' />",
						}
					},
					highlight: function (element) {
			           
			        }
				});
				
				$('.btn-attachment-lookup, .btn-save').addClass('hide');
				
				$('.date-picker:not([readonly])').datepicker('setDate', new Date());
				
				if (checkingDate == "") {
					$('.date-picker:not([readonly])').datepicker('setDate', new Date());
				} else {
					$('.date-picker:not([readonly])').datepicker('setDate', checkingDate);
				}
				
				$('#getCurrentTime').on('click', function() {
					var currentdate = new Date();
					$('#checkingTimeText').timepicker('setTime', currentdate.getHours() + ":" + currentdate.getMinutes());
				})
				
				$('.timepicker').timepicker({
					showInputs: false,
					showMeridian: false,
					defaultTime: false,
					minuteStep: 1
		        });
				
				$('.btn-quotationRecord-collapse', this.$element).click(function() {
					var $btn = $(this);
					var $box = $btn.closest('.box');
				});
				
				$('#recordNonContact').on('click', function() {
					var currentdate = new Date();
					var otherRemark = $('#otherRemark').val();
					if (otherRemark.length > 0 && otherRemark.charAt(otherRemark.length - 1) != '\n') {
						otherRemark += '\n';
					}
					$('#otherRemark').val(otherRemark + ('0' + currentdate.getDate()).slice(-2) + "-" 
							+ ('0' + (currentdate.getMonth()+1)).slice(-2) + "-" + currentdate.getFullYear() + " " 
							+ ('0' + currentdate.getHours()).slice(-2) + ":" + ('0' + currentdate.getMinutes()).slice(-2) + ":" + ('0' + currentdate.getSeconds()).slice(-2) + " non contact");
				})
				
				$("#nonContact").change(function() {
   					 if(this.checked) {
   						$(".verificationResult").attr('disabled',true);  	
    					
    				} else {
  						$(".verificationResult").attr('disabled',false);
         			 					
    				}
				});
				
				var $dataTable = $("#dataList");
				$dataTable.DataTable({
					"order": [[ 0, "desc" ]],
					"buttons": [],
					"searching": true,
					"ordering": true,
					"processing": true,
	                "serverSide": true,
	                "ajax": {
	                	"url": "<c:url value='/qualityControlManagement/PostEnumerationCheckMaintenance/queryQuotationRecord'/>",
	                	data: function(d) {
	                		d.search["assignmentId"] = $('#assignmentId').val();
	                	},
	                	method: "post"
	                },
	                "columns": [
								{
									"data": "quotationId",
									"orderable": false,
									"searchable": true,
									"className" : "text-center"
								},
	                            {
	                            	"data": "unitName",
	                            	"orderable": false,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "productionSpecification",
	                            	"orderable": false,
	                            	"searchable": true,
	                            	"render" : function(data, type, full, meta){
	                            		var html = "";
	                            		if(full.productAttribute1 != null) {
	                            			html += full.productAttribute1 + "</br>"
	                            		} 
	                            		if(full.productAttribute2 != null) {
	                            			html += full.productAttribute2 +"</br>";
	                            		} 
	                            		if(full.productAttribute3 != null) {
	                            		    html += full.productAttribute3 + "</br>";
	                            		}
	                            		return html;
                            		},
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "nPrice",
	                            	"orderable": false,
	                            	"searchable": true,
	                            	"className" : "text-center"
                            	},
	                            {
	                            	"data": "sPrice",
	                            	"orderable": false,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "discount",
	                            	"orderable": false,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "subPrice",
	                            	"orderable": false,
	                            	"searchable": true,
	                            	"className" : "text-center"
                            	},
	                            {
	                            	"data": "discountReason",
	                            	"orderable": false,
	                            	"searchable": true,
	                            	"className" : "text-center"
                            	},
	                            {
	                            	"data": "quotationRecordId",
	                            	"orderable": false,
	                                "render" : function(data, type, full, meta){
	                                   var html = "<a href='<c:url value='/assignmentManagement/QuotationRecordPEEdit/home?id='/>"+data+"' target='_blank'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a> ";
	                                   html += "<a onclick='addPEComment(\""+escape(full.quotationId)+" - " + escape(full.unitName) +"checked\")' style='cursor:pointer' target='_blank'><span class='glyphicon glyphicon-tag' aria-hidden='true'></span></a> ";
	                                   return html;
		                            },
		                            "width" : "60px",
	                            	"className" : "text-center"
                            	},                          	
	                        ],
				});
				
				$('#submitBtn').on('click', function() {
					$('#status').val('Submitted');
					$("#outletForm").submit();
					
				})
				$('#saveBtn').on('click', function() {
					$('#status').val('Draft');
					$("#outletForm").submit();
				})				
				
				$('#nonContact').trigger('change');
				
			    $("#outletForm").submit(function(event) {
			    	var isvalid = $("#outletForm").valid();
			    	
			    	if(isvalid){
			    		/* stop form from submitting normally */
				        event.preventDefault();

				        /* get some values from elements on the page: */
				        var $form = $( this ),
				            url = $form.attr( 'action' );

				        /* Send the data using post */
				        var posting = $.ajax({
				        	url: url,
				        	data: new FormData(this),
				        	processData: false,
				        	contentType: false,
				        	type: 'POST'
				        });

				        /* Alerts the results */
				        posting.done(function( data ) {
				        	if (data != '') {
				        		bootbox.alert({
									title:"Save Fail",
									message: data
								});
				        		return;
				        	}
				        	$('#mainForm').submit();
				        });
			    	}
			      });
			    
			    var $outletForm = $('#outletForm');
			    $outletForm.validate();
			    
			    function setFirmStatusToOutlet(firmStatus) {
					$outletForm.find('[name="firmStatus"]').val(firmStatus);
				}
			    
			    var firmStatus = $('#firmStatus').val();
			    setFirmStatusToOutlet(firmStatus);
			    
			    <sec:authorize access="!(hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256))">
					viewOnly();
				</sec:authorize>
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
        	<h1>Post-Enumeration Check Maintenance</h1>
        	<c:if test="${not empty model.peCheckFormId}">
				<div class="breadcrumb form-horizontal" style="width:240px">
					<div class="form-group" style="margin-bottom:0px">
			        	<div class="col-sm-5">Created Date:</div>
			        	<div class="col-sm-7"><c:out value="${commonService.formatDateTime(model.createdDate)}" /></div>
			        </div>
			        <div class="form-group" style="margin-bottom:0px">
			         	<div class="col-sm-5">Last Modified:</div>
			         	<div class="col-sm-7"><c:out value="${commonService.formatDateTime(model.modifiedDate)}" /></div>
			         </div>
		      	</div>
	      	</c:if>
        </section>
        <section class="content">
        	<div class="row">
				<div class="col-md-12" >
					<!-- content -->
					<div class="box" >
						<div class="box-header with-border">
							<a class="btn btn-default" href="<c:url value='/qualityControlManagement/PostEnumerationCheckMaintenance/home'/>">Back</a>
						</div>
						<div class="box-body">
							<div class="clearfix">&nbsp;</div>
								<div class="row">
									<form id="mainForm" action="<c:url value='/qualityControlManagement/PostEnumerationCheckMaintenance/save'/>" method="post" role="form"
								      	enctype="multipart/form-data">
								      	<input type='hidden' id='assignmentId' name='assignmentId' value='<c:out value="${model.assignmentId}" />' />
								      	<input type='hidden' id='status' name='status' value='<c:out value="${model.status}" />' />
								      	<input type='hidden' id='firmStatus' value='<c:out value="${model.firmStatus}" />' />
										<div class="col-md-12">
									<div class="row row-eq-height">
										<div class="col-md-2">
											<label class="control-label">Contact Person:</label>
										</div>
										<div class="col-md-2">
       							   			<input id="contactPerson" name="contactPerson" class='form-control' type='text' value='<c:out value="${model.contactPerson}"/>'>											
										</div>
										<div class="col-md-2">
										</div>
										<div class="col-md-2">
											<label class="control-label">Enumeration Date:</label>												
										</div>
										<div class="col-md-2">
											<c:out value="${model.enumerationDate}" />									
										</div>
									</div>
									<div class="row row-eq-height">
										<div class="col-md-2">
											<label class="control-label">Checking Date:</label>
										</div>
										<div class="col-md-2">
	      							   		<div class="input-group">
												<input type="text" class="form-control date-picker" data-date="dateToday"
													id="checkingDateText" name="checkingDateText" size="10" />
												<div class="input-group-addon">
													<i class="fa fa-calendar"></i>
												</div>
											</div>										
										</div>
										<div class="col-md-2">
										</div>
										<div class="col-md-2">
											<label class="control-label">Collection Method:</label>
										</div>
										<div class="col-md-2">
       							   			<c:out value="${model.collectionMethod}"/>											
										</div>
									</div>
									<div class="row row-eq-height">
										<div class="col-md-2">
											<label class="control-label">Checking Time:</label>
										</div>
										<div class="col-md-2">
							                <div class='input-group bootstrap-timepicker othertime' id='sessionStartTime'>
							                    <input id='checkingTimeText' name='checkingTimeText' type='text' class="form-control timepicker" value="${model.checkingTimeText}" />
							                    <span class="input-group-addon">
							                        <span class="glyphicon glyphicon-time"></span>
							                    </span>
							                </div>												
										</div>
										<div class="col-md-2">
											<button type='button' id='getCurrentTime' class='btn'>Get Current Time</button>
										</div>
										<div class="col-md-2">
											<label class="control-label">Field Officer: </label>
										</div>
										<div class="col-md-2">
   							   				<c:out value="${model.officerText}"/>												
										</div>										
									</div>
									<div class="row row-eq-height">
										<div class="col-md-2">
											<label class="control-label">Checking Mode:</label>
										</div>
										<div class="col-md-4">
  							   				<label class = "checkbox-inline">
												<input type = "radio" name = "checkingMode" id = "checkingMode" value = "1" <c:if test="${model.checkingMode == 1}">checked</c:if>> Field
											</label>
											<label class = "checkbox-inline">
												<input type = "radio" name = "checkingMode" id = "checkingMode" value = "2" <c:if test="${model.checkingMode == 2}">checked</c:if>> Telephone
											</label>											
										</div>									
									</div>
									<div class="row row-eq-height">
										<div class="col-md-6">
											<table class="table text-center">
											<thead>
											<tr>
											<th colspan='4' >Result Of Verification</th>
											</tr>
											<tr>
											<th width='25%'></th>
											<th width='25%'>All Matched</th>
											<th width='25%'>Not Matched</th>
											<th width='25%'>N.A.</th>
											</tr>
											<tr>
											<td>Contact Date</td>
											<td><input name='contactDateResult' type='radio' value='1' class='verificationResult' <c:if test="${model.contactDateResult == 1}">checked</c:if>></td>
											<td><input name='contactDateResult' type='radio' value='2' class='verificationResult' <c:if test="${model.contactDateResult == 2}">checked</c:if>></td>
											<td><input name='contactDateResult' type='radio' value='3' class='verificationResult' <c:if test="${model.contactDateResult == 3}">checked</c:if>></td>
											</tr>
											<tr>
											<td>Contact Time</td>
											<td><input name='contactTimeResult' type='radio' value='1' class='verificationResult' <c:if test="${model.contactTimeResult == 1}">checked</c:if>></td>
											<td><input name='contactTimeResult' type='radio' value='2' class='verificationResult' <c:if test="${model.contactTimeResult == 2}">checked</c:if>></td>
											<td><input name='contactTimeResult' type='radio' value='3' class='verificationResult' <c:if test="${model.contactTimeResult == 3}">checked</c:if>></td>
											</tr>
											<tr>
											<td>Contact Duration</td>
											<td><input name='contactDurationResult' type='radio' value='1' class='verificationResult' <c:if test="${model.contactDurationResult == 1}">checked</c:if>></td>
											<td><input name='contactDurationResult' type='radio' value='2' class='verificationResult' <c:if test="${model.contactDurationResult == 2}">checked</c:if>></td>
											<td><input name='contactDurationResult' type='radio' value='3' class='verificationResult' <c:if test="${model.contactDurationResult == 3}">checked</c:if>></td>
											</tr>
											<tr>
											<td>Contact Mode</td>
											<td><input name='contactModeResult' type='radio' value='1' class='verificationResult' <c:if test="${model.contactModeResult == 1}">checked</c:if>></td>
											<td><input name='contactModeResult' type='radio' value='2' class='verificationResult' <c:if test="${model.contactModeResult == 2}">checked</c:if>></td>
											<td><input name='contactModeResult' type='radio' value='3' class='verificationResult' <c:if test="${model.contactModeResult == 3}">checked</c:if>></td>
											</tr>
											<tr>
											<td>Data Collected</td>
											<td><input name='dateCollectedResult' type='radio' value='1' class='verificationResult' <c:if test="${model.dateCollectedResult == 1}">checked</c:if>></td>
											<td><input name='dateCollectedResult' type='radio' value='2' class='verificationResult' <c:if test="${model.dateCollectedResult == 2}">checked</c:if>></td>
											<td><input name='dateCollectedResult' type='radio' value='3' class='verificationResult' <c:if test="${model.dateCollectedResult == 3}">checked</c:if>></td>
											</tr>	
											<tr>
											<td>Others</td>
											<td><input name='othersResult' type='radio' value='1' class='verificationResult' <c:if test="${model.othersResult == 1}">checked</c:if>></td>
											<td><input name='othersResult' type='radio' value='2' class='verificationResult' <c:if test="${model.othersResult == 2}">checked</c:if>></td>
											<td><input name='othersResult' type='radio' value='3' class='verificationResult' <c:if test="${model.othersResult == 3}">checked</c:if>></td>
											</tr>																					
											</table>
										</div>								
									</div>
									<div class="row row-eq-height">
	       							    <div class="checkbox col-md-1">
											  <label><input id="nonContact" name="nonContact" type="checkbox" value="true" <c:if test="${model.nonContact}">checked</c:if>>Non Contact</label>
										</div>
										<div class="col-md-5">
       							   				<button id='recordNonContact' type='button' class='btn btn-default pull-right'>Record Non Contact</button>											
										</div>									
									</div>		
									<div class="row row-eq-height">
										<div class="col-md-1">
											<label for="peCheckRemark">Comment:</label>
										</div>
										<div class="col-md-3">	   			  
  												<textarea class="form-control" rows="5" id="peCheckRemark" name="peCheckRemark"><c:out value="${model.peCheckRemark}"/></textarea>										
										</div>
										<div class="col-md-1">
											<label for="otherRemark">Other Result:</label>
										</div>
										<div class="col-md-4">
       											<textarea class="form-control" rows="5" id="otherRemark" name="otherRemark"><c:out value="${model.otherRemark}"/></textarea>
														
										</div>									
									</div>				
									<input type="hidden" name="surveyMonthId" value="<c:out value="${model.surveyMonthId}"/>">
									<input type="hidden" name="userId" value="<c:out value="${model.userId}"/>">
									<input type="hidden" name="peCheckFormId" value="<c:out value="${model.peCheckFormId}"/>">
								</div>
								</form>
							</div>			
							<hr/>
							<div class="clearfix">&nbsp;</div>
							<c:set var="outlet" value="${model.outlet}" scope="request" />
							<form id="outletForm" action="<c:url value='/qualityControlManagement/PostEnumerationCheckMaintenance/saveOutlet'/>" method="post" role="form" enctype="multipart/form-data">
								<input name="tab" type="hidden" value="Normal" />
								<input name="dateSelectedAssignmentId" type="hidden" value="" />
								<input name="dateSelected" type="hidden" value="" />
								<input name="unitCategory" type="hidden" value="" />
								<input name="consignmentCounter" type="hidden" value="" />
								<input name="verificationType" type="hidden" value="" />
								<input name="userId" type="hidden" value="<c:out value="${model.userId}"/>" />
								<input name="assignmentId" type="hidden" value="<c:out value="${model.assignmentId}"/>" />								
								<input name="peCheckFormId" type="hidden" value="<c:out value="${model.peCheckFormId}"/>">
								<%@include file="/WEB-INF/views/shared/quotationRecord/outletEdit.jsp"%>
							</form>
							<div class="clearfix">&nbsp;</div>
							<div class="box box-primary quotationRecord-edit">
								<div class="box-header with-border">
									<h3 class="box-title">
										List of Quotation Record
									</h3>
									<div class="box-tools pull-right">
										<button class="btn btn-box-tool btn-quotationRecord-collapse" type="button" data-widget="collapse">
											<i class="fa fa-plus"></i>
										</button>
									</div>
								</div>
								<div class="box-body">
										<div class="form-horizontal">
											<div class="row">
												<div class="col-md-12">
												<table class="table table-striped table-bordered table-hover" id="dataList">
													<thead>
														<tr>
															<th>Quotation ID</th>
															<th>Unit Name</th>
															<th>Product Specification</th>
															<th>NPrice</th>
															<th>SPrice</th>
															<th>Discount</th>
															<th>Sub-Price</th>
															<th>Discount Reason</th>
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
							</div>		
						</div>
						<div id="hiddenData"></div>
						<sec:authorize access="hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)">
							<div class="box-footer">
								<button id="submitBtn" type="button" class="btn btn-info pull-right">Submit</button>
	       						<button id="saveBtn" type="submit" class="btn btn-info">Save and Back</button>
	      					</div>
	      				</sec:authorize>
					</div>
				</div>
			</div>
        </section>
	</jsp:body>
</t:layout>
