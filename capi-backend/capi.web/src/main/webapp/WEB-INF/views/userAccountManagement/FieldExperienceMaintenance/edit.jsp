<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
<jsp:useBean id="niceDate" class="java.util.Date"/>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/select2-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/timepicker-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
	</jsp:attribute>
	<jsp:attribute name="header">
		<style>
			#map { height: 500px }
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
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/timepicker.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker.jsp" %>
		<%@include file="/WEB-INF/views/includes/datejs.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<script>
			$(function() {
				$.validator.addMethod('daterule',function (value, element){
					if ($(element).val() == '') return true;
					$fromDate = $(element).parents('.row:first').find("input.fromDate");
					if ($fromDate.val() == '') return true;
					
					var fromDate = $fromDate.datepicker('getDate');
					var toDate = $(element).datepicker("getDate");
					if (fromDate > toDate)
						return false;
					
					return true;
				}, "<spring:message code='E00039' />")
				
				ko.bindingHandlers.initDateRule = {
					init: function (element, valueAccessor, allBindings, viewModel, bindingContext) {
						allBindings();
				    	$(element).rules('add', {
				    		daterule : true
				    	});
				     }
				}
				
				ko.bindingHandlers.initRequiredRule = {
					init: function (element, valueAccessor, allBindings, viewModel, bindingContext) {
						allBindings();
				    	$(element).rules('add',{
				    		required: true
				    	})
				     }
				}
				
				 ko.bindingHandlers.initDatepicker = {
					 init: function (element, valueAccessor, allBindings, viewModel, bindingContext) {
				    	Datepicker(element);
				    	if (typeof(value) == "string" && value != '') {
			            	value = parseDate(value);
			            	$(element).datepicker("setDate", value);
			            }
				    	
			            //handle disposal (if KO removes by the template binding)
			            ko.utils.domNodeDisposal.addDisposeCallback(element, function() {
			                $(element).datepicker("destroy");
			            });
				     },
			        update: function(element, valueAccessor, allBindings, viewModel, bindingContext) {
			            var value = ko.utils.unwrapObservable(valueAccessor()),
			            $el = $(element);
			            if (typeof(value) == "string" && value != '') {
			            	value = parseDate(value);
			            }

			            var current = $el.datepicker("getDate");

			            if (value != '' && value - current !== 0) {
			                $el.datepicker("setDate", value);
			            }
			        }
				 }
				function StaffReasonListViewModel(){
					this.reasons = ko.observableArray(${reasons});		
					var self = this;
					this.deleteReason = function (element){
						bootbox.confirm({
							title:"Confirmation",
							message: "<spring:message code='W00001' />",
							callback: function(result){
								if (result){
									self.reasons.remove(element);
								}
							}
						});						
					}					
					this.addReason=function(){
						self.reasons.unshift({
							staffReasonId:'',
							fromDate:'',
							toDate:'',
							reason:''
						});
					}
				}
				

				var $mainForm = $('#mainForm');
				
				$mainForm.validate({
					rules:{
						accumulatedOT: "number"
					}
					
				});
				//$('#districtId').select2ajax();
				$('#districtIds').select2({
					width:"100%",
					closeOnSelect: false
				});
				
				var reasonViewModel = new StaffReasonListViewModel();
				
				ko.applyBindings(reasonViewModel, $("#reasonBox")[0]);
				
				<sec:authorize access="!(hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256))">
					viewOnly();
				</sec:authorize>
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Field Officer Profile Maintenance</h1>
			<div class="breadcrumb form-horizontal" style="width:240px">
				<div class="form-group" style="margin-bottom:0px">
		        	<div class="col-sm-5">Created Date:</div>
		        	<div class="col-sm-7">${commonService.formatDateTime(model.createdDate)}</div>
		        </div>
		        <div class="form-group" style="margin-bottom:0px">
		         	<div class="col-sm-5">Last Modified:</div>
		         	<div class="col-sm-7">${commonService.formatDateTime(model.modifiedDate)}</div>
		         </div>
	      	</div>
        </section>
        
        <section class="content">
        	<form id="mainForm" action="<c:url value='/userAccountManagement/FieldExperienceMaintenance/save'/>" method="post" role="form"
        		enctype="multipart/form-data">
        		<input name="userId" value="<c:out value="${model.userId}" />" type="hidden" />
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/userAccountManagement/FieldExperienceMaintenance/home'/>">Back</a>
							</div>
	       					<div class="box-body">
	       						<div class="form-horizontal">
	       							<div class="col-sm-6">
	       								<div class="form-group">
		       								<label class="col-sm-3 control-label">Staff: </label>
		       								<div class="col-sm-7">
												<p class="form-control-static" >${model.staffCode}</p>
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-3 control-label">English Name: </label>
		       								<div class="col-sm-7">
												<p class="form-control-static" >${model.englishName}</p>
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-3 control-label">Chinese Name: </label>
		       								<div class="col-sm-7">
												<p class="form-control-static" >${model.chineseName}</p>
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-3 control-label">Rank: </label>
		       								<div class="col-sm-7">
												<p class="form-control-static" >${model.rankName}</p>
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-3 control-label">Designation: </label>
		       								<div class="col-sm-7">
												<p class="form-control-static" >${model.destination}</p>
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-3 control-label">Supervisor: </label>
		       								<div class="col-sm-7">
												<p class="form-control-static" >${model.supervisorStaffCode} - ${model.supervisorChineseName} (${model.supervisorDestination})</p>
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-3 control-label">Team: </label>
		       								<div class="col-sm-7">
												<input name="team" type="text" class="form-control" value="<c:out value="${model.team}" />" maxlength="255" required />
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-3 control-label">District: </label>
		       								<div class="col-sm-7">
												<select class="form-control select2" id="districtIds" name="districtIds" multiple="multiple">
													<c:forEach var="district" items="${districts}">
														<option value="<c:out value="${district.districtId}" />"
															<c:forEach var="modelDistrictId" items="${model.districtIds}">
																<c:if test="${modelDistrictId == district.districtId}">selected</c:if>
															</c:forEach>
															>${district.code} - ${district.englishName}</option>
													</c:forEach>
												</select>
											</div>
										</div>
										
										<div class="form-group">
		       								<label class="col-sm-3 control-label">Time off balance: </label>
		       								<div class="col-sm-7">
												<input name="accumulatedOT" type="text" class="form-control" value="<c:out value="${model.accumulatedOT}" />" />
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-3 control-label">GIC: </label>
		       								<div class="col-sm-7">
												<input name="gic" type="text" class="form-control" value="<c:out value="${model.gic}" />" maxlength="512" />
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-3 control-label">Home area: </label>
		       								<div class="col-sm-7">
												<input name="homeArea" type="text" class="form-control" value="<c:out value="${model.homeArea}" />" maxlength="512" />
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-3 control-label">OMP: </label>
		       								<div class="col-sm-7">
												<input name="omp" type="text" class="form-control" value="<c:out value="${model.omp}" />" maxlength="512" />
											</div>
										</div>
										
										<div class="form-group">
		       								<label class="col-sm-3 control-label">GHS: </label>
		       								<div class="col-sm-7">
		       									<label class="form-control-static">
													<input name="GHS" type="radio"  value="true" <c:if test="${model.GHS}">checked</c:if> />Yes
												</label>
												&nbsp;
												<label  class="form-control-static">
													<input name="GHS" type="radio"  value="false" <c:if test="${not model.GHS}">checked</c:if> />No
												</label>
											</div>
										</div>
										
									</div>
	       							<div class="col-sm-6">
		       							<div class="form-group">
		       								<label class="col-sm-3 control-label">Office Phone no.: </label>
		       								<div class="col-sm-7">
		       									<p class="form-control-static" >${model.officePhoneNo}</p>
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-3 control-label">Gender: </label>
		       								<div class="col-sm-7">
		       									<p class="form-control-static" >${model.gender}</p>
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-3 control-label">Date of Entry: </label>
		       								<div class="col-sm-7">
		       									<p class="form-control-static" >${model.dateOfEntry}</p>
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-3 control-label">Date of Leaving: </label>
		       								<div class="col-sm-7">
		       									<p class="form-control-static" >${model.dateOfLeaving}</p>
											</div>
										</div>
										<div class="box box-default" id="reasonBox">
											<div class="box-header with-border">
												<h3 class="box-title">
													Reason
												</h3>
												<div class="box-tools pull-right">
													<button class="btn btn-box-tool" type="button" data-bind="click:addReason">
														<i class="fa fa-plus"></i>
														Add Reason
													</button>
												</div>												
											</div>
											<div class="box-body" data-bind="foreach: reasons">
												<div class="row">	
													<input type="hidden" data-bind="value:staffReasonId, attr:{name:'reasons['+$index()+'].staffReasonId'}" />
													<div class="form-group">
														<label class="col-sm-2 control-label">Date:</label>
														<div class="col-sm-3">
															<div class="input-group">
					       										<input type="text" class="form-control date-picker fromDate" data-bind="initDatepicker:fromDate,attr:{name:'reasons['+$index()+'].fromDate'},initRequiredRule:fromDate" />
					       										<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
					       									</div>
														</div>
														<div class="col-sm-3">
															<div class="input-group">
					       										<input type="text" class="form-control date-picker toDate" data-bind="initDatepicker:toDate,attr:{name:'reasons['+$index()+'].toDate'},initRequiredRule:toDate,initDateRule:toDate" />
					       										<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
					       									</div>
														</div>
													</div>
													<div class="form-group">
														<label class="col-sm-2 control-label">Reason: </label>
														<div class="col-sm-6">
															<input type="text" class="form-control" data-bind="value:reason,attr:{name:'reasons['+$index()+'].reason'}" />
														</div>
														<div class="col-sm-4">
															<button class="btn btn-danger" type="button" data-bind="click:$parent.deleteReason">
																<i class="fa fa-close"></i>
															</button>
														</div>
													</div>
													<!-- ko if: $index() != $parent.reasons().length - 1 -->
														<hr>
													<!-- /ko -->
												</div>
												
											</div>
										</div>
									</div>
								</div>
							</div>
							<sec:authorize access="hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)">
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

