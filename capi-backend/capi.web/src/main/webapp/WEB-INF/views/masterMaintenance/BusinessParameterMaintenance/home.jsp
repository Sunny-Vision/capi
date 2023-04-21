<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page session="false"%>
<jsp:useBean id="niceDate" class="java.util.Date" />
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/select2-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/timepicker-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/jstree-css.jsp" %>
	</jsp:attribute>
	<jsp:attribute name="header">
		<style>

.btn.pull-right {
	margin-left: 10px;
}
.color-palette-set {
    margin-bottom: 15px;
    min-width: 40px;
}
.color-palette {
    height: 10px;
    line-height: 10px;
    text-align: center;
}
</style>
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp"%>
		<%@include file="/WEB-INF/views/includes/timepicker.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker.jsp"%>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/moment.jsp" %>
		<%@include file="/WEB-INF/views/includes/jstree.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/unitLookup.jsp" %>
		<script>
			$(function() {
				var $newCriteria = 0;
				var $newReason = 0;
				var $newSession = 0;
				var $generalForm = $('#generalForm');
				var $peForm = $('#peForm');
				var $itineraryForm = $('#itineraryForm');
				var $reasonForm = $('#reasonForm');
				var $sessionForm = $('#sessionForm');
				var $onSpotForm = $('#onSpotForm');
				
				jQuery.validator.addClassRules({
					numbers: {
						number: true
					},
					digits: {
						digits: true
					},
					delinkPeriod: {
						digits: true,
						min: 1
					}
			    });

				$generalForm.validate({
				});
				
				$peForm.validate({
				})
				
				$reasonForm.validate({
				})
				
				$sessionForm.validate({})
				
				$itineraryForm.validate({})
				
				$onSpotForm.validate({})
				
				Datepicker();
				$(".select2").select2({closeOnSelect: false, 
				 	width: '100%'});
				$(".select2-addable").select2({
				 	tags: true,
				 	multiple: true,
				 	closeOnSelect: false, 
				 	width: '100%'
				});
				
				$('.select2ajax').select2ajax({	width: '100%'});
				
				// General Parameters				
				$(".sync-calendar").on("click", function(){
					$("[name='syncCalendar']", $('#generalForm')).val(1);
				});
				
				
				// PE parameters
				$(".select2ajaxMultiple").select2ajax({	
						width: '100%',
						closeOnSelect: false
					});
				
				$('.searchUnitId').each(function(){
					var $select = $(this).closest('.input-group').find('select');
					var getKeyValueByIdsUrl = $select.data('getKeyValueByIdsUrl');
					
					$(this).unitLookup({
						selectedIdsCallback: function(selectedId) {
							$select.empty();
							$.post(getKeyValueByIdsUrl, { id: selectedId }, function(data) {
								//console.log(data);
								for (i = 0; i < data.length; i++){
									var option = new Option(data[i].value, data[i].key);
									option.selected = true;
									$select.append(option);
								}
								$select.trigger('change');
							});
						},
						bottomEntityClass: 'Item',
						/*queryDataCallback: function(model) {
							model.bottomEntityClass = "Item";
						},*/
						multiple: true
					});
					$select.hide();
				});
				
				
				

				var values= "${excludedOutletTypeIds}" ;
				$("[name='excludedOutletType'] option").prop("selected", false);
				$.each(values.split(","), function(i,e){
				    $("[name='excludedOutletType'] option[value='"+e+"']").prop("selected", true);
				    $("[name='excludedOutletType']").trigger("change");
				});
				
				$(".timepicker").timepicker({
						showInputs: false,
						showMeridian: false,
						defaultTime: false,
						minuteStep: 1
					});
				
				$(".addCriteria").on("click", function(){
					var $html = 
					$('<div class="box box-primary" data-newid="'+$newCriteria+'">'+
						'<div class="box-body">'+
							'<div class="form-group">'+
								'<label class="col-sm-4 control-label">Item</label>'+
								'<div class="col-sm-6">'+
									'<div class="input-group">'+									
										'<input type="hidden" class="form-control" name="newUnitCriteria['+$newCriteria+'].unitCriteriaId" value="0" required="">'+
										'<select multiple name="newUnitCriteria['+$newCriteria+'].itemIds" class="select2ajaxMultiple filters"'+
													'data-allow-clear="true"'+
													'data-placeholder=""'+
													'data-ajax-url="<c:url value="/masterMaintenance/BusinessParameterMaintenance/queryItemSelect2"/>"'+
													'data-get-key-value-by-ids-url="<c:url value="/masterMaintenance/BusinessParameterMaintenance/queryItemSelectMutiple"/>"'+
													'>'+
										'</select>'+
										'<div class="input-group-addon searchUnitId" data-related-id="'+$newCriteria+'" data-bottom-entity-class="Unit">'+
											'<i class="fa fa-search"></i>'+
										'</div>'+
									'</div>'+
								'</div>'+
							'</div>'+
							'<div class="form-group">'+
								'<label class="col-sm-4 control-label">No of months</label>'+
								'<div class="col-sm-6">'+
									'<input type="text" class="form-control numbers" name="newUnitCriteria['+$newCriteria+'].noOfMonth" value="" required="">	'+
								'</div>'+
							'</div>'+
							'<div class="form-group">'+
								'<label class="col-sm-4 control-label">Percentage of quotation</label>'+
								'<div class="col-sm-6">'+
									'<input type="text" class="form-control numbers" name="newUnitCriteria['+$newCriteria+'].percentageOfQuotation" value="" required="" >	'+
								'</div>'+
							'</div>'+
							'<div class="form-group">'+
								'<label class="col-sm-4 control-label">PR</label>'+
								'<div class="col-sm-2">'+
										'<select class="form-control" name="newUnitCriteria['+$newCriteria+'].prOperator" required>'+
											'<option value="&gt;">&gt;</option>'+
											'<option value="&lt;">&lt;</option>'+
											'<option value="&gt;=">&gt;=</option>'+
											'<option value="&lt;=">&lt;=</option>'+
											'<option value="=">=</option>'+
										'</select>'+
								'</div>'+
								'<div class="col-sm-4">'+
								'<input type="text" class="form-control numbers" name="newUnitCriteria['+$newCriteria+'].prPercentage" value="" required="">'+
								'</div>'+
							'</div>'+
						'</div>'+
						'<div class="box-footer">'+
							'<button type="button" class="btn btn-info pull-right" onclick="deleteCriteria($(this))" data-newid="'+$newCriteria+'">Delete</button>'+
						'</div>'+
					'</div>');
					
					$("#unitCriteriaList").append($html);

					$('.select2ajaxMultiple', $html).select2ajax({	width: '100%', closeOnSelect:false});
					
					/*
					$('.searchUnitId', $html).each(function(){
						var $select = $(this).closest('.input-group').find('select');
						var getKeyValueByIdsUrl = $select.data('getKeyValueByIdsUrl');
						
						$(this).unitLookup({
							selectedIdsCallback: function(selectedId) {
								$select.empty();
								$.get(getKeyValueByIdsUrl, { id: selectedId[0] }, function(data) {
									console.log(data);
									var option = new Option(data, selectedId[0]);
									option.selected = true;
									$select.append(option);
									$select.trigger('change');
								});
							},
							queryDataCallback: function(model) {
								model.authorityLevel = 256;
							},
							multiple: false
						});
						$select.hide();
					});
					*/
					$('.searchUnitId', $html).each(function(){
						var $select = $(this).closest('.input-group').find('select');
						var getKeyValueByIdsUrl = $select.data('getKeyValueByIdsUrl');
						
						$(this).unitLookup({
							selectedIdsCallback: function(selectedId) {
								$select.empty();
								$.post(getKeyValueByIdsUrl, { id: selectedId }, function(data) {
									//console.log(data);
									for (i = 0; i < data.length; i++){
										var option = new Option(data[i].value, data[i].key);
										option.selected = true;
										$select.append(option);
									}
									$select.trigger('change');
								});
							},
							bottomEntityClass: 'Item',
							/*queryDataCallback: function(model) {
								model.bottomEntityClass = "Item";
							},*/
							multiple: true
						});
						$select.hide();
					});
					$newCriteria++;
				});
				
				
				
				// Reason for report
				$(".addReasonForReport").on("click", function(){
					var $html = 
					$('<div class="form-group" data-newid="'+$newReason+'">'+
							'<div class="col-sm-6 col-sm-offset-2">'+
							'<input type="hidden" name="newFieldworkAssignmentReason['+$newReason+'].reportReasonSettingId" value="0" class="form-control"/>'+
							'<input type="text" name="newFieldworkAssignmentReason['+$newReason+'].reason" class="form-control" required/>'+
						'</div>'+
						'<div class="col-sm-2">'+
							'<button type="button" class="btn btn-info pull-right" onclick="deleteFieldworkAssignmentReason($(this))" data-newid="'+$newReason+'">Delete</button>'+
						'</div>'+
					'</div>');
					
					$("#fieldworkAssignmentReasonList").append($html);
					
					$newReason++;
				});
				
				
				// working session
				$(".addWorkingSession").on("click", function(){
					var $html = 
					$('<div class="form-group" data-newid="'+$newSession+'">'+
							'<label class="col-sm-2 control-label">From:</label>'+
							'<div class="col-sm-3">'+
								'<input name="newWorkingSessions['+$newSession+'].id" type="hidden" class="form-control" value="0"/>'+
								'<div class="input-group bootstrap-timepicker">'+
									'<input name="newWorkingSessions['+$newSession+'].fromTime" type="text" class="form-control timepicker"/>'+
									'<div class="input-group-addon">'+
										'<i class="fa fa-clock-o"></i>'+
									'</div>'+
								'</div>'+
							'</div>'+
							'<label class="col-sm-2 control-label">To:</label>'+
							'<div class="col-sm-3">'+
								'<div class="input-group bootstrap-timepicker">'+
									'<input name="newWorkingSessions['+$newSession+'].toTime" type="text" class="form-control timepicker"/>'+
									'<div class="input-group-addon">'+
										'<i class="fa fa-clock-o"></i>'+
									'</div>'+
								'</div>'+
							'</div>'+
							'<div class="col-sm-2">'+
								'<button type="button" class="btn btn-info pull-right" onclick="deleteWorkingSession($(this))" data-newid="'+$newSession+'">Delete</button>'+
							'</div>'+
						'</div>');
					
					$("#workingSessionList").append($html);
					
					$(".timepicker", $html).timepicker({
						showInputs: false,
						showMeridian: false,
						defaultTime: false,
						minuteStep: 1
					});
					
					$newSession++;
				});
				
				<sec:authorize access="!hasPermission(#user, 256)">
					viewOnly();
				</sec:authorize>
			});
			
			function deleteCriteria($elem){
				if($elem.data("id") != undefined){
					var eventId = $elem.data("id");
					
					bootbox.confirm({
					     title:"Confirmation",
					     message: "<spring:message code='W00001' />",
					     callback: function(result){
					         if (result){
					        	 $("div.box[data-id='"+eventId+"']", $("#peForm")).remove();
					         }
					     }
					});
				}else if($elem.data("newid") != undefined){
					
					var eventId = $elem.data("newid");
					
					bootbox.confirm({
					     title:"Confirmation",
					     message: "<spring:message code='W00001' />",
					     callback: function(result){
					         if (result){
									$("div.box[data-newid='"+eventId+"']", $("#peForm")).remove();
					         };
					     }
					})
				}
			}
			
			function deleteFieldworkAssignmentReason ($elem){
				if($elem.data("id") != undefined){
					var eventId = $elem.data("id");
					
					bootbox.confirm({
					     title:"Confirmation",
					     message: "<spring:message code='W00001' />",
					     callback: function(result){
					         if (result){
					        	 $("div.form-group[data-id='"+eventId+"']", $("#reasonForm")).remove();
					         };
					     }
					});
				}else if($elem.data("newid") != undefined){
					
					var eventId = $elem.data("newid");
					
					bootbox.confirm({
					     title:"Confirmation",
					     message: "<spring:message code='W00001' />",
					     callback: function(result){
					         if (result){
									$("div.form-group[data-newid='"+eventId+"']", $("#reasonForm")).remove();
					         };
					     }
					})
				}
			}
			
			function deleteWorkingSession ($elem){
				if($elem.data("id") != undefined){
					var eventId = $elem.data("id");
					
					bootbox.confirm({
					     title:"Confirmation",
					     message: "<spring:message code='W00001' />",
					     callback: function(result){
					         if (result){
					        	 $("div.form-group[data-id='"+eventId+"']", $("#sessionForm")).remove();
					         };
					     }
					});
				}else if($elem.data("newid") != undefined){
					
					var eventId = $elem.data("newid");
					
					bootbox.confirm({
					     title:"Confirmation",
					     message: "<spring:message code='W00001' />",
					     callback: function(result){
					         if (result){
									$("div.form-group[data-newid='"+eventId+"']", $("#sessionForm")).remove();
					         };
					     }
					})
				}
			}
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Business Parameter Maintenance</h1>
          <c:if test="${act != 'add'}">
				<div class="breadcrumb form-horizontal" style="width: 240px">
			        <div class="form-group" style="margin-bottom: 0px">
			         	<div class="col-sm-5">Last Modified:</div>
			         	<div class="col-sm-7">${displayModel.commonLastModify}</div>
			         </div>
		      	</div>
	      	</c:if>
        </section>
        
        <section class="content">
        	<div class="row">
        		<div class="col-md-12">
        			<!-- general form elements -->
        			<div class="box box-primary">
       					<div class="box-body">
       						<div class="nav-tabs-custom">
								<ul class="nav nav-tabs">
									<li class="<c:if test="${ACTIVE_TAB eq 'general' or empty ACTIVE_TAB}">active</c:if>"><a href="#tab_1" data-toggle="tab" aria-expanded="true">General Parameters</a></li>
									<li class="<c:if test="${ACTIVE_TAB eq 'PE'}">active</c:if>"><a href="#tab_2" data-toggle="tab" aria-expanded="false">PE Parameters</a></li>
									<li class="<c:if test="${ACTIVE_TAB eq 'itinerary'}">active</c:if>"><a href="#tab_3" data-toggle="tab" aria-expanded="false">Itinerary Checking</a></li>
									<li class="<c:if test="${ACTIVE_TAB eq 'report'}">active</c:if>"><a href="#tab_4" data-toggle="tab" aria-expanded="false">Reason for reports</a></li>
									<li class="<c:if test="${ACTIVE_TAB eq 'workingSession'}">active</c:if>"><a href="#tab_5" data-toggle="tab" aria-expanded="false">Working Session</a></li>
									<li class="<c:if test="${ACTIVE_TAB eq 'onSpot'}">active</c:if>"><a href="#tab_6" data-toggle="tab" aria-expanded="false">On-Spot Validation Message</a></li>
								</ul>
					            <div class="tab-content">
									<div class="tab-pane <c:if test="${ACTIVE_TAB eq 'general' or empty ACTIVE_TAB}">active</c:if>" id="tab_1">
										<%@include file="/WEB-INF/views/masterMaintenance/BusinessParameterMaintenance/partial/generalForm.jsp"%>
									</div>
									<div class="tab-pane <c:if test="${ACTIVE_TAB eq 'PE'}">active</c:if>" id="tab_2">
										<%@include file="/WEB-INF/views/masterMaintenance/BusinessParameterMaintenance/partial/peMaintenance.jsp"%>
									</div>
									<div class="tab-pane <c:if test="${ACTIVE_TAB eq 'itinerary'}">active</c:if>" id="tab_3">
										<%@include file="/WEB-INF/views/masterMaintenance/BusinessParameterMaintenance/partial/itineraryChecking.jsp"%>
									</div>
									<div class="tab-pane <c:if test="${ACTIVE_TAB eq 'report'}">active</c:if>" id="tab_4">
										<%@include file="/WEB-INF/views/masterMaintenance/BusinessParameterMaintenance/partial/reasonForReport.jsp"%>
									</div>
									<div class="tab-pane <c:if test="${ACTIVE_TAB eq 'workingSession'}">active</c:if>" id="tab_5">
										<%@include file="/WEB-INF/views/masterMaintenance/BusinessParameterMaintenance/partial/workingSession.jsp"%>
									</div>
									<div class="tab-pane <c:if test="${ACTIVE_TAB eq 'onSpot'}">active</c:if>" id="tab_6">
										<%@include file="/WEB-INF/views/masterMaintenance/BusinessParameterMaintenance/partial/onSpotValidationMessage.jsp"%>
									</div>
								<!-- /.tab-pane -->
								</div>
				            <!-- /.tab-content -->
							</div>
						</div>
					</div>
        		</div>
        	</div>
        </section>
	</jsp:body>
</t:layout>