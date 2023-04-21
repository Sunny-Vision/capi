<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>		
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp"%>
	</jsp:attribute>
	<jsp:attribute name="header">
		<style>
			#backTrackDateForm td{
				text-align: center;
				vertical-align: middle;
			}
			#assignmentForm td{
				text-align: center;
			}
			.input-group-addon.disabled{
				background-color:#eeeeee;
			}
			.select2-container--disabled .select2-search__field{
				display: none;
			}
			.assignmentAttr-officerIds {
				visibility: hidden;
			}
			tr.row-error {
				border-style : solid;
				border-color : red;
			}
			
			tr.row-error-batchId {
				border-style : solid;
				border-color : red;
			}
			
			tr.row-error-startDate {
				border-style : solid;
				border-color : red;
			}
			
			tr.row-error-endDate {
				border-style : solid;
				border-color : red;
			}
			
			tr.row-error-collectionDate {
				border-style : solid;
				border-color : red;
			}
			
			tr.row-error-session {
				border-style : solid;
				border-color : red;
			}
			
			tr.row-error-officerIds {
				border-style : solid;
				border-color : red;
			}
			
			tr.row-error-allocationBatch {
				border-style : solid;
				border-color : red;
			}
			
		</style>
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-wizard.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker.jsp"%>
		<%@include file="/WEB-INF/views/includes/select2.jsp"%>
		<%@include file="/WEB-INF/views/includes/datejs.jsp"%>
		<%@include file="/WEB-INF/views/includes/commonDialog/userLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/staffCalendarUserLookup.jsp" %>
		<script src="<c:url value='/resources/js/underscore-min.js'/>"></script>
		<script src="<c:url value='/resources/js/promise.js' />"></script>
		
		<script>			
			var forcePass = false;
			$(document).ready(function(){
				var pageReadonly = $('#pageReadonly').val() == 'true';
				var pageId = $('#pageId').val() == '' ? 0 : +$('#pageId').val();
				
				var newBatchAllocationId = 0;
				var newAlloactionAttributeId = 0;
				var newBackTrackDateDisplayModelId = 0;
				var fromDate = "";
				var endDate = "";
				
				var tab1 = false;
				var tab2 = false;
				var tab3 = false;
				var tab4 = false;
				var readonly = false;
				var tabChanged = false;
				
				function tabLoadingStart($tab) {
					$tab.children().hide();
					var $init = $('<div class="tabLoading">Initializing. Please wait. <progress></progress></div>');
					$tab.append($init);
				}
				
				function tabLoadingEnd($tab) {
					$('.tabLoading').remove();
					$tab.children().show();
					Modals.endLoading();
				}
				
				// For Specified Collection Date & Officer batch codes, no Allocation Batch input is needed.
				$.validator.addMethod('allocationBatchRefIdRequired', function (value, element, param) {
					var $collectionDatePickerButton = $(element).closest('tr').find('.collectionDatePicker .input-group-addon');
					if (!$collectionDatePickerButton.hasClass('disabled')) {
						return true;
					}
					/*
					if($(element).parents('form').data('submitStatus') == false) {
						return true;
					}
					*/
					if (value == '')
						return false;
					else
						return true;
				}, '<spring:message code='E00010' />');

				// For Start / End date batch codes, no Session input is needed.
				$.validator.addMethod('sessionRequired', function (value, element, param) {
					var $collectionDatePickerButton = $(element).closest('tr').find('.collectionDatePicker .input-group-addon');
					if ($collectionDatePickerButton.hasClass('disabled')) {
						return true;
					}
					/*
					if($(element).parents('form').data('submitStatus') == false) {
						return true;
					}
					*/
					if (value == undefined)
						return false;
					else
						return true;
				}, '<spring:message code='E00010' />');

				// Collection date required only when enabled
				$.validator.addMethod('collectionDateRequired', function (value, element, param) {
					var $collectionDatePickerButton = $(element).closest('tr').find('.collectionDatePicker .input-group-addon');
					if ($collectionDatePickerButton.hasClass('disabled')) {
						return true;
					}
					/*
					if($(element).parents('form').data('submitStatus') == false) {
						return true;
					}
					*/
					if (value == '')
						return false;
					else
						return true;
				}, '<spring:message code='E00010' />');
				
				
				$('#rootwizard').bootstrapWizard({
					onTabShow: function(tab, navigation, index) {
						var $total = navigation.find('li').length;
						var $current = index+1;
						var $percent = ($current/$total) * 100;
						
						$('#rootwizard').find('.bar').css({width:$percent+'%'});
						
						if(readonly){
							$("#saveBtn").hide();
						} else if(!readonly){
							$('#saveBtn').show();
						}
						
						if($current >= $total) {
							$('#rootwizard').find('.pager .next').hide();
							$('#rootwizard').find('.pager .finish').show();
							$('#rootwizard').find('.pager .finish').removeClass('disabled');
						} else {
							$('#rootwizard').find('.pager .next').show();
							$('#rootwizard').find('.pager .finish').hide();
						}
						
						$.ajax({
							type: 'POST',
							url: "<c:url value='/assignmentAllocationAndReallocation/SurveyMonth/getTabItem'/>",
							data: {index: $current},
							async:false,
							success: function(partialView){
								$html = $(partialView);
								$("#tab"+$current).html($html); 
								
								$("input, select").on("change", function(){
									tabChanged = true;
								})
								switch ($current){
									case 1:
										$('[name="surveyMonth.referenceMonthStr"]:not([readonly])').datepicker({minViewMode: "months", autoclose: true}).on("changeDate", function(){
											
											$.ajax({
												type: 'POST',
												url: "<c:url value='/assignmentAllocationAndReallocation/SurveyMonth/getClosingDate'/>",
												data: {monthStr: $(this).val()},
												async:false,
												success: function(result){
													if(result.startDate == '' && result.endDate == '' && result.closingDate == ''){
														bootbox.alert({
						        						    title: "Alert",
						        						    message: "<spring:message code='E00132' />"
						        						});
													}else{
													
														$('[name="surveyMonth.startDateStr"]', $html).val(result.startDate).trigger("change").datepicker('update');
														$('[name="surveyMonth.endDateStr"]', $html).val(result.endDate).trigger("change").datepicker('update');
														$('[name="surveyMonth.closingDateStr"]', $html).val(result.closingDate).trigger("change").datepicker('update');
														
														tabChanged = true;
													}
												}
											});
										});
										
										$("form", $html).validate({ignore: ""});
										Datepicker($(".date-picker:not([readonly])", $html));
										
										tabLoadingEnd($("#tab"+$current));
									break;
									case 2:
										$("#addAllocationBatch").on("click", function(){
											var allocationBatchRow = 
												"<tr data-newid='"+newBatchAllocationId+"' class='allocationBatchRow'>"+
													"<td>"+
														"<input type='hidden' name='newAllocationBatch["+newBatchAllocationId+"].id' value='"+newBatchAllocationId+"'>"+
														"<input name='newAllocationBatch["+newBatchAllocationId+"].numberOfBatch' class='form-control' required/>"+
													"</td>"+
													"<td>"+
														"<div class='input-group'>"+
															"<input name='newAllocationBatch["+newBatchAllocationId+"].startDateStr' class='form-control date-picker' data-orientation='top' required data-date-startdate='"+fromDate+"' data-date-enddate='"+endDate+"' />"+
															"<div class='input-group-addon'>"+
																"<i class='fa fa-calendar'></i>"+
															"</div>"+
														"</div>"+
													"</td>"+
													"<td>"+
														"<div class='input-group'>"+
															"<input name='newAllocationBatch["+newBatchAllocationId+"].endDateStr' class='form-control date-picker' data-orientation='top' required data-date-startdate='"+fromDate+"' data-date-enddate='"+endDate+"' />"+
															"<div class='input-group-addon'>"+
																"<i class='fa fa-calendar'></i>"+
															"</div>"+
														"</div>"+
													"</td>"+
													"<td>"+
														"<a href='javascript:void(0)' class='table-btn btn-delete' data-newid='"+newBatchAllocationId+"'><span class='fa fa-times fa-2' aria-hidden='true'></span></a>"+
													"</td>"+
												"</tr>";

											var $rowHtml = $(allocationBatchRow);
											$(".btn-delete", $rowHtml).on("click", deleteBatchAllocationWithConfirm);
											
											$("#allocationBatchBody").append($rowHtml);
											Datepicker($(".date-picker:not([readonly])", $html), {
												beforeShowDay: function(d){													
													return !Date.today().isAfter(d)
												}
											});
											newBatchAllocationId++;
											
											tabChanged = true;
										});
										
										$("form", $html).validate({ignore: ""});
										Datepicker($(".date-picker:not([readonly])", $html), {
											beforeShowDay: function(d){
												return !Date.today().isAfter(d)
											}
										});
										$(".btn-delete", $html).on("click", deleteBatchAllocationWithConfirm);
										
										tabLoadingEnd($("#tab"+$current));
										break;
									case 3:
										tabLoadingStart($("#tab"+$current));
										$(".select2", $html).select2({width: "100%"});
										Datepicker($(".date-picker:not([readonly])", $html), {todayHighlight: false, todayBtn: false});
										//$("form", $html).validate({ignore:""});
										/*$html.validate({
											//ignore: "",
											highlight: function(element, errorClass, validClass) {
												$(element).parents('tr').css('border-style', 'solid');
												$(element).parents('tr').css('border-color', 'red');
											}
										});*/
										
										$(".btn-delete", $html).on("click", deleteAssignmentAllocationWithConfirm);
										
										$(".assignmentAttr-allocationBatch", $html).on("change", function(){
											var startDate = $("option:selected", $(this)).data("start-date");
											var endDate = $("option:selected", $(this)).data("end-date");
											
											var elemStartDate = $(this).parents("tr").find(".assignmentAttr-startDate:not([disabled])");
											var elemEndDate = $(this).parents("tr").find(".assignmentAttr-endDate:not([disabled])");
											var elemCollectionDate = $(this).parents("tr").find(".collectionDatePicker");
											
											if(elemStartDate != undefined) {
												elemStartDate.datepicker("setStartDate", startDate).datepicker("setEndDate", endDate).datepicker('setDates', elemStartDate.datepicker('getDates'));
												elemStartDate.datepicker('setDate', startDate).datepicker('update');
											}
											
											if(elemEndDate != undefined) {
												elemEndDate.datepicker("setStartDate", startDate).datepicker("setEndDate", endDate).datepicker('setDates', elemEndDate.datepicker('getDates'));
												elemEndDate.datepicker('setDate', endDate).datepicker('update');
											}
											
											if(elemCollectionDate.find("div.disabled").length == 0) {
												elemCollectionDate.datepicker("setStartDate", startDate).datepicker("setEndDate", endDate).datepicker('setDates', elemCollectionDate.datepicker('getDates'));
											}

											tabChanged = true;

											$(this).trigger('afterChange');
										});
										
										$('.batchOfficerUpdate', $html).each(function(){
											var categoryForRendFieldOfficePicker = $(this);
											//$(this).userLookup({
											$(this).staffCalendarUserLookup({
												selectedIdsCallback: function(selectedIds) {
													$.post("<c:url value='/assignmentAllocationAndReallocation/SurveyMonth/getStaffsName'/>", {ids: selectedIds}, function(result){
														var options = [];
														$(result).each(function(){
															options.push(this.userId);
														});
														$('[name="batchOfficerUpdate"][data-category="'+categoryForRendFieldOfficePicker.data("category")+'"]').val(options.join());
														$('[name="batchOfficerUpdate-userId"][data-category="'+categoryForRendFieldOfficePicker.data("category")+'"]').val(options).select2({data:[options], width: "100%"}).trigger("change");
														tabChanged = true;
													});
													
												},
												queryDataCallback: function(model) {
													model.authorityLevel = 16;
												},
												multiple: false,
												alreadySelectedIdsCallback: function() {
													return $('[name="batchOfficerUpdate"][data-category="'+categoryForRendFieldOfficePicker.data("category")+'"]').val();
												}
											});
										});
										
										$('.unCatBatchOfficerUpdate', $html).each(function(){
											var categoryForRendFieldOfficePicker = $(this);
											//$(this).userLookup({
											$(this).staffCalendarUserLookup({
												selectedIdsCallback: function(selectedIds) {
													$.post("<c:url value='/assignmentAllocationAndReallocation/SurveyMonth/getStaffsName'/>", {ids: selectedIds}, function(result){
														var options = [];
														$(result).each(function(){
															options.push(this.userId);
														});
														$('[name="unCatBatchOfficerUpdate"][data-category="'+categoryForRendFieldOfficePicker.data("category")+'"]').val(options.join());
														$('[name="unCatBatchOfficerUpdate-userId"][data-category="'+categoryForRendFieldOfficePicker.data("category")+'"]').val(options).select2({data:[options], width: "100%"}).trigger("change");
														tabChanged = true;
													});
													
												},
												queryDataCallback: function(model) {
													model.authorityLevel = 16;
												},
												multiple: false,
												alreadySelectedIdsCallback: function() {
													return $('[name="unCatBatchOfficerUpdate"][data-category="'+categoryForRendFieldOfficePicker.data("category")+'"]').val();
												}
											});
										});
										
										$('.newOfficer', $html).each(function(){
											//$(this).userLookup({
											$(this).staffCalendarUserLookup({
												selectedIdsCallback: function(selectedIds) {
													var $categoryForRendFieldOfficePicker = this.$element;
													var $parent = this.$element.parent();
													$.post("<c:url value='/assignmentAllocationAndReallocation/SurveyMonth/getStaffsName'/>", {ids: selectedIds}, function(result){
														var options = [];
														$(result).each(function(){
															options.push(this.userId);
														});
														$parent.find('[name$=".officers"]:not(:disabled)').val(options.join());
														$parent.find('[name$=".officerIds"]:not(:disabled)').val(options).select2({data:[options], width: "100%"}).trigger("change");
														tabChanged = true;
													});
													
												},
												queryDataCallback: function(model) {
													model.authorityLevel = 16;
												},
												multiple: false,
												alreadySelectedIdsCallback: function() {
													var $categoryForRendFieldOfficePicker = this.$element;
													var $parent = this.$element.parent();
													return $parent.find('[name$=".officers"]').val();
												}
											});
										});
										
										$('.batch-row', $html).each(function() {
											var selectedBatchType = $('[name$=".selectedBatchType"]', this).val();
											
											if (selectedBatchType == 1) {
												$('.assignmentAttr-allocationBatch', this).prop('disabled', true);
											} else {
												$('.assignmentAttr-session', this).prop('disabled', true);
											}
										});
										
										$("select.batchOfficerUpdate").on("change", function(){
											$(this).siblings("input").val($(this).val());
											tabChanged = true;
										});
										
										$("select.assignmentAttr-officerIds").on("change", function(){
											$(this).siblings("input").val($(this).val());
											tabChanged = true;
										});
										
										$("select.unCatBatchOfficerUpdate").on("change", function(){
											$(this).siblings("input").val($(this).val());
											tabChanged = true;
										});
										
										function createAssignmentAttrRow(newId, category, newDisplayModelId, refObj, selectedDates, fieldOfficer){
											var p = new promise.Promise();
										
											$.post("<c:url value='/assignmentAllocationAndReallocation/SurveyMonth/addAssignmentAttrRow'/>", {category: category, referenceId: newId, displayModelId: newBackTrackDateDisplayModelId}, function(partialView){
												var $html = $(partialView);
												$(".select2", $html).select2({width: "100%"});
												Datepicker($(".date-picker:not([readonly])", $html));
												
												if (!$('.collectionDatePicker .input-group-addon', $html).hasClass('disabled')) {
													$('.assignmentAttr-allocationBatch', $html).prop('disabled', true);
												} else {
													$('.assignmentAttr-session', $html).prop('disabled', true);
												}
												$(".reallocate", $html).each(function(){
													$(this).on("click", function(){
														$tr = $(this).parents('tr:first');
														var selectedValues = $tr.find('.collectionDate').val();
														//console.log(selectedValues);
														if (selectedValues != null && selectedValues.length > 0){
															$('.collectionDate-select2').empty();
															
															selectedValues.sort(function(a,b){
															  // Turn your strings into dates, and then subtract them
															  // to get a value that is either negative, positive, or zero.
															  //return new Date(b) - new Date(a);
															  return parseDate(a) - parseDate(b);
															});
															
															for (i = 0; i < selectedValues.length; i++){
																$('.collectionDate-select2').append("<option value='"+selectedValues[i]+"'>"+selectedValues[i]+"</option>");
															}	
															$('.collectionDate-select2').trigger('change')
										
														}
														$(".officer-select2").val("").trigger('change')
														
														$("#reallocateModalForm").data("validator").resetForm();
														$("#reallocateModalForm").data("validator").settings.submitHandler = function(form) {
															  var newId = newAlloactionAttributeId;
															  newAlloactionAttributeId++;
															  var newDisplayModelId = newBackTrackDateDisplayModelId;
															  newBackTrackDateDisplayModelId++;
															  
															  var selectedDates = $('.collectionDate-select2').val();
															  createAssignmentAttrRow(newId, category, newDisplayModelId, $tr, selectedDates, $(".officer-select2").val());
															  
															  var oldSelected = $tr.find('.collectionDate').val();
															  var updatedDates = _.difference(oldSelected, selectedDates);
															  $tr.find('.assignmentAttr-collectionDate').val(updatedDates.join(',')).trigger('change');
															  
															  $("#reallocateModal").modal('hide');
														};
														/*
														$("#reallocateModalForm").validate({
															  submitHandler: function(form) {
																  var newId = newAlloactionAttributeId;
																  newAlloactionAttributeId++;
																  var selectedDates = $('.collectionDate-select2').val();
																  createAssignmentAttrRow(newId, category, $tr, selectedDates, $(".officer-select2").val());
																  
																  var oldSelected = $tr.find('.collectionDate').val();
																  var updatedDates = _.difference(oldSelected, selectedDates);
																  $tr.find('.assignmentAttr-collectionDate').val(updatedDates.join(',')).trigger('change');
																  
																  $("#reallocateModal").modal('hide');
															  }
														}).resetForm();
														*/
														$("#reallocateModal").modal('show');
													})
												})
												
												$(".assignmentAttr-allocationBatch", $html).on("change", function(){
													var startDate = $("option:selected", $(this)).data("start-date");
													var endDate = $("option:selected", $(this)).data("end-date");
													
													var elemStartDate = $(this).parents("tr").find(".assignmentAttr-startDate:not([disabled])");
													var elemEndDate = $(this).parents("tr").find(".assignmentAttr-endDate:not([disabled])");
													var elemCollectionDate = $(this).parents("tr").find(".collectionDatePicker");
													
													if(elemStartDate != undefined) {
														elemStartDate.datepicker("setStartDate", startDate).datepicker("setEndDate", endDate).datepicker('setDates', elemStartDate.datepicker('getDates'));
														elemStartDate.datepicker('setDate', startDate).datepicker('update');
													}
													
													if(elemEndDate != undefined) {
														elemEndDate.datepicker("setStartDate", startDate).datepicker("setEndDate", endDate).datepicker('setDates', elemEndDate.datepicker('getDates'));
														elemEndDate.datepicker('setDate', endDate).datepicker('update');
													}
													
													if(elemCollectionDate.find("div.disabled").length == 0)
														elemCollectionDate.datepicker("setStartDate", startDate).datepicker("setEndDate", endDate).datepicker('setDates', elemCollectionDate.datepicker('getDates'));
													
													tabChanged = true;

												});
												
												$('.newOfficer', $html).each(function(){
													//$(this).userLookup({
													$(this).staffCalendarUserLookup({
														selectedIdsCallback: function(selectedIds) {
															var $categoryForRendFieldOfficePicker = this.$element;
															var $parent = this.$element.parent();
															$.post("<c:url value='/assignmentAllocationAndReallocation/SurveyMonth/getStaffsName'/>", {ids: selectedIds}, function(result){
																var options = [];
																$(result).each(function(){
																	options.push(this.userId);
																});
																$parent.find('[name$=".officers"]:not(:disabled)').val(options.join());
																$parent.find('[name$=".officerIds"]:not(:disabled)').val(options).select2({data:[options], width: "100%"}).trigger("change");
																tabChanged = true;
															});
															
														},
														queryDataCallback: function(model) {
															model.authorityLevel = 16;
														},
														multiple: false,
														alreadySelectedIdsCallback: function() {
															var $categoryForRendFieldOfficePicker = this.$element;
															var $parent = this.$element.parent();
															return $parent.find('[name$=".officers"]').val();
														}
													});
												});
												
												
												if (refObj == null){
													$("tbody", "table[data-category='"+category+"']").append($html);
												}
												else{
													$(refObj).after($html);													
												}
												
												$("select.batchId", $html).on("change", function(){
													var elem = $(this);
													$.post("<c:url value='/assignmentAllocationAndReallocation/SurveyMonth/getBatchType'/>", {batchId: $(this).val()}, function(type){
														tabChanged = true;
														type = parseInt(type);
														if(type == 1){
															$(".assignmentAttr-startDate", $("tr[data-newid='"+elem.data("newid")+"']")).attr("disabled", "disabled").val("").datepicker("remove").siblings("div.input-group-addon").addClass("disabled");
															$(".assignmentAttr-endDate", $("tr[data-newid='"+elem.data("newid")+"']")).attr("disabled", "disabled").val("").datepicker("remove").siblings("div.input-group-addon").addClass("disabled");
															
															$(".collectionDatePicker div.input-group-addon", $("tr[data-newid='"+elem.data("newid")+"']")).removeClass("disabled");
															$(".assignmentAttr-collectionDate", $("tr[data-newid='"+elem.data("newid")+"']")).removeAttr("disabled");
															Datepicker($(".collectionDatePicker", $("tr[data-newid='"+elem.data("newid")+"']")));
															
															$(".assignmentAttr-officerIds", $("tr[data-newid='"+elem.data("newid")+"']")).removeAttr("disabled").trigger("change");
															
															$(".assignmentAttr-officerIds", $("tr[data-newid='"+elem.data("newid")+"']")).siblings(".input-group-addon.newOfficer").removeClass("hidden");
														}else if(type == 2){
															$(".assignmentAttr-startDate", $("tr[data-newid='"+elem.data("newid")+"']")).removeAttr("disabled").siblings("div.input-group-addon").removeClass("disabled");
															$(".assignmentAttr-endDate", $("tr[data-newid='"+elem.data("newid")+"']")).removeAttr("disabled").siblings("div.input-group-addon").removeClass("disabled");
															Datepicker($(".assignmentAttr-startDate", $("tr[data-newid='"+elem.data("newid")+"']")));
															Datepicker($(".assignmentAttr-endDate", $("tr[data-newid='"+elem.data("newid")+"']")));
															
															$(".collectionDatePicker, .collectionDatePicker div.input-group-addon", $("tr[data-newid='"+elem.data("newid")+"']")).datepicker("remove");
															$(".collectionDatePicker div.input-group-addon", $("tr[data-newid='"+elem.data("newid")+"']")).addClass("disabled");
															$(".assignmentAttr-collectionDate", $("tr[data-newid='"+elem.data("newid")+"']")).attr("disabled", "disabled").datepicker("remove").val("").siblings("select").val("").trigger("change");
															
															$(".assignmentAttr-officerIds", $("tr[data-newid='"+elem.data("newid")+"']")).removeAttr("disabled").trigger("change");
															
															$(".assignmentAttr-officerIds", $("tr[data-newid='"+elem.data("newid")+"']")).siblings(".input-group-addon.newOfficer").removeClass("hidden");
														
														}else if(type == 3){
														
															$(".assignmentAttr-startDate", $("tr[data-newid='"+elem.data("newid")+"']")).removeAttr("disabled").siblings("div.input-group-addon").removeClass("disabled");
															$(".assignmentAttr-endDate", $("tr[data-newid='"+elem.data("newid")+"']")).removeAttr("disabled").siblings("div.input-group-addon").removeClass("disabled");
															Datepicker($(".assignmentAttr-startDate", $("tr[data-newid='"+elem.data("newid")+"']")));
															Datepicker($(".assignmentAttr-endDate", $("tr[data-newid='"+elem.data("newid")+"']")));
															
															$(".collectionDatePicker, .collectionDatePicker div.input-group-addon", $("tr[data-newid='"+elem.data("newid")+"']")).datepicker("remove");
															$(".collectionDatePicker div.input-group-addon", $("tr[data-newid='"+elem.data("newid")+"']")).addClass("disabled");
															$(".assignmentAttr-collectionDate", $("tr[data-newid='"+elem.data("newid")+"']")).attr("disabled", "disabled").datepicker("remove").val("").siblings("select").val("").trigger("change");
															
															$(".assignmentAttr-officerIds", $("tr[data-newid='"+elem.data("newid")+"']")).val("").attr("disabled", "disabled").trigger("change");
															
															$(".assignmentAttr-officerIds", $("tr[data-newid='"+elem.data("newid")+"']")).siblings(".input-group-addon.newOfficer").addClass("hidden");
														}

														if (type == 1) {
															$('.assignmentAttr-allocationBatch', $("tr[data-newid='"+elem.data("newid")+"']")).val('').trigger('change').prop('disabled', true);
															$('.assignmentAttr-session', $("tr[data-newid='"+elem.data("newid")+"']")).prop('disabled', false);
														} else {
															$('.assignmentAttr-allocationBatch', $("tr[data-newid='"+elem.data("newid")+"']")).prop('disabled', false);
															$('.assignmentAttr-session', $("tr[data-newid='"+elem.data("newid")+"']")).prop('disabled', true);
														}
														
														elem.trigger('afterChange');
													});
												});

												$("select.assignmentAttr-officerIds", $html).on("change", function(){
													$(this).siblings("input").val($(this).val());
													tabChanged = true;
												});
												
												$(".btn-delete", $html).on("click", deleteAssignmentAllocationWithConfirm);
												
												var $category = $("table[data-category='"+category+"']").closest('.category-container');
												if ($category.data('isCreatingBatchCodeRows')) {
													$category.data('lastCreatedBatchCodeRowIndex', $category.data('lastCreatedBatchCodeRowIndex') + 1);
													var option = $("select.batchId", $html).find('option').get($category.data('lastCreatedBatchCodeRowIndex'));
													if (option != null) {
														// other fields may trigger change after batch selection, unknown processing time
														$("select.batchId", $html).one('afterChange', function() {
															tabChanged = false;
														});
														$("select.batchId", $html).val($(option).val()).trigger('change');
													}

													if ($("select.batchId", $html).find('option').length > ($category.data('lastCreatedBatchCodeRowIndex') + 1)) {
														$('.addBatchCategoryItem', $category).click();
													} else {
														$category.data('isCreatingBatchCodeRows', false);
													}
												}

												if ($('.category-container').filter(function() {
													return $(this).data('isCreatingBatchCodeRows');
												}).length == 0) {
													tabLoadingEnd($("#tab"+$current));
												}
												

												if (refObj != null){
													$html.find('.batchId').val($tr.find('.batchId').val()).trigger('change');
													$html.find('.assignmentAttr-collectionDate').val(selectedDates.join(',')).trigger('change');	
													$html.find('.officer').val(fieldOfficer).trigger('change');
												}
												
												p.done(null);
											});// end of ajax
											
											return p;
										}// end of function
										
										$(".reallocate", $html).each(function(){
											$(this).on("click", function(){
												$tr = $(this).parents('tr:first');
												$table = $(this).parents('table:first');
												var category = $table.data('category')
												var selectedValues = $tr.find('.collectionDate').val();
												//console.log($tr.find('.collectionDate'));
												//console.log($tr.find('.collectionDate'));
												if (selectedValues != null && selectedValues.length > 0){
													$('.collectionDate-select2').empty();
													for (i = 0; i < selectedValues.length; i++){
														$('.collectionDate-select2').append("<option value='"+selectedValues[i]+"'>"+selectedValues[i]+"</option>");
													}	
													$('.collectionDate-select2').trigger('change')
								
												}
												$(".officer-select2").val("").trigger('change')
												
												$("#reallocateModalForm").data("validator").resetForm();
												$("#reallocateModalForm").data("validator").settings.submitHandler = function(form) {
													  var newId = newAlloactionAttributeId;
													  newAlloactionAttributeId++;
													  var newDisplayModelId = newBackTrackDateDisplayModelId;
													  newBackTrackDateDisplayModelId++;
													  var selectedDates = $('.collectionDate-select2').val();
													  console.log("selectedDates: " + selectedDates);
													  createAssignmentAttrRow(newId, category, newDisplayModelId, $tr, selectedDates, $(".officer-select2").val());
													  
													  var oldSelected = $tr.find('.collectionDate').val();
													  var updatedDates = _.difference(oldSelected, selectedDates);
													  $tr.find('.assignmentAttr-collectionDate').val(updatedDates.join(',')).trigger('change');
													  
													  $("#reallocateModal").modal('hide');
												  };
												  
												  
												  /*
												$("#reallocateModalForm").validate({
													  submitHandler: function(form) {
														  var newId = newAlloactionAttributeId;
														  newAlloactionAttributeId++;
														  var selectedDates = $('.collectionDate-select2').val();
														  createAssignmentAttrRow(newId, category, $tr, selectedDates, $(".officer-select2").val());
														  
														  var oldSelected = $tr.find('.collectionDate').val();
														  var updatedDates = _.difference(oldSelected, selectedDates);
														  $tr.find('.assignmentAttr-collectionDate').val(updatedDates.join(',')).trigger('change');
														  
														  $("#reallocateModal").modal('hide');
													  }
												}).resetForm();
												  
												  */
												$("#reallocateModal").modal('show');
											});
										})
										
										$(".addBatchCategoryItem", $html).on("click", function(){
											var category = $(this).data("category");
											tabChanged = true;
											var newId = newAlloactionAttributeId;
											newAlloactionAttributeId++;
											var newDisplayModelId = newBackTrackDateDisplayModelId;
											newBackTrackDateDisplayModelId++;
											
											createAssignmentAttrRow(newId, category, newDisplayModelId);
										});
										
										$("select.batchId", $html).on("change", function(){
											var elem = $(this);
											$.post("<c:url value='/assignmentAllocationAndReallocation/SurveyMonth/getBatchType'/>", {batchId: $(this).val()}, function(type){
												tabChanged = true;
												type = parseInt(type);
												if(type == 1){
													$(".assignmentAttr-startDate", $("tr[data-newid='"+elem.data("newid")+"']")).attr("disabled", "disabled").val("").datepicker("remove").siblings("div.input-group-addon").addClass("disabled");
													$(".assignmentAttr-endDate", $("tr[data-newid='"+elem.data("newid")+"']")).attr("disabled", "disabled").val("").datepicker("remove").siblings("div.input-group-addon").addClass("disabled");
													
													$(".collectionDatePicker div.input-group-addon", $("tr[data-newid='"+elem.data("newid")+"']")).removeClass("disabled");
													$(".assignmentAttr-collectionDate", $("tr[data-newid='"+elem.data("newid")+"']")).removeAttr("disabled");
													Datepicker($(".collectionDatePicker", $("tr[data-newid='"+elem.data("newid")+"']")));
													
													$(".assignmentAttr-officerIds", $("tr[data-newid='"+elem.data("newid")+"']")).val("").removeAttr("disabled").trigger("change");
													$(".assignmentAttr-officerIds", $("tr[data-newid='"+elem.data("newid")+"']")).siblings(".input-group-addon.newOfficer").removeClass("hidden");
													
												}else if(type == 2){
													$(".assignmentAttr-startDate", $("tr[data-newid='"+elem.data("newid")+"']")).removeAttr("disabled").siblings("div.input-group-addon").removeClass("disabled");
													$(".assignmentAttr-endDate", $("tr[data-newid='"+elem.data("newid")+"']")).removeAttr("disabled").siblings("div.input-group-addon").removeClass("disabled");
													Datepicker($(".assignmentAttr-startDate", $("tr[data-newid='"+elem.data("newid")+"']")));
													Datepicker($(".assignmentAttr-endDate", $("tr[data-newid='"+elem.data("newid")+"']")));
													
													$(".collectionDatePicker, .collectionDatePicker div.input-group-addon", $("tr[data-newid='"+elem.data("newid")+"']")).datepicker("remove");
													$(".collectionDatePicker div.input-group-addon", $("tr[data-newid='"+elem.data("newid")+"']")).addClass("disabled");
													$(".assignmentAttr-collectionDate", $("tr[data-newid='"+elem.data("newid")+"']")).attr("disabled", "disabled").datepicker("remove").val("").siblings("select").val("").trigger("change");
													
													$(".assignmentAttr-officerIds", $("tr[data-newid='"+elem.data("newid")+"']")).val("").removeAttr("disabled").trigger("change");
													$(".assignmentAttr-officerIds", $("tr[data-newid='"+elem.data("newid")+"']")).siblings(".input-group-addon.newOfficer").removeClass("hidden");
												
												}else if(type == 3){
												
													$(".assignmentAttr-startDate", $("tr[data-newid='"+elem.data("newid")+"']")).removeAttr("disabled").siblings("div.input-group-addon").removeClass("disabled");
													$(".assignmentAttr-endDate", $("tr[data-newid='"+elem.data("newid")+"']")).removeAttr("disabled").siblings("div.input-group-addon").removeClass("disabled");
													Datepicker($(".assignmentAttr-startDate", $("tr[data-newid='"+elem.data("newid")+"']")));
													Datepicker($(".assignmentAttr-endDate", $("tr[data-newid='"+elem.data("newid")+"']")));
													
													$(".collectionDatePicker, .collectionDatePicker div.input-group-addon", $("tr[data-newid='"+elem.data("newid")+"']")).datepicker("remove");
													$(".collectionDatePicker div.input-group-addon", $("tr[data-newid='"+elem.data("newid")+"']")).addClass("disabled");
													$(".assignmentAttr-collectionDate", $("tr[data-newid='"+elem.data("newid")+"']")).attr("disabled", "disabled").datepicker("remove").val("").siblings("select").val("").trigger("change");
													
													$(".assignmentAttr-officerIds", $("tr[data-newid='"+elem.data("newid")+"']")).val("").attr("disabled", "disabled").trigger("change");
													$(".assignmentAttr-officerIds", $("tr[data-newid='"+elem.data("newid")+"']")).siblings(".input-group-addon.newOfficer").addClass("hidden");
													
												}

												if (type == 1) {
													$('.assignmentAttr-allocationBatch', $("tr[data-newid='"+elem.data("newid")+"']")).val('').trigger('change').prop('disabled', true);
													$('.assignmentAttr-session', $("tr[data-newid='"+elem.data("newid")+"']")).prop('disabled', false);
												} else {
													$('.assignmentAttr-allocationBatch', $("tr[data-newid='"+elem.data("newid")+"']")).prop('disabled', false);
													$('.assignmentAttr-session', $("tr[data-newid='"+elem.data("newid")+"']")).prop('disabled', true);
												}
											});
										});
										
										$(".btn-copy", $html).on("click", function(){
											tabChanged = true;
											var dataFrom = $(this).data("copy");
											var refCategory = $(this).data("category");
											var data = $('[name="'+dataFrom+'"][data-category="'+refCategory+'"]').val();;
											switch(dataFrom){
												case "batchStartDateUpdate":
													data = $(this).siblings(".input-group").find(".date-picker").datepicker('getDates');
													var $elem = $(this).parents("table").find(".assignmentAttr-startDate:not([disabled])");
													$elem.each(function(){
														if($(this).val() == ""){
															$(this).datepicker("setDates", data[0]);
														}
													});
												break;
												case "batchEndDateUpdate":
													data = $(this).siblings(".input-group").find(".date-picker").datepicker('getDates');
													var $elem = $(this).parents("table").find(".assignmentAttr-endDate:not([disabled])");
													$elem.each(function(){
														if($(this).val() == ""){
															$(this).datepicker("setDates", data[0]);
														}
													});
												break;
												case "batchCollectionDateUpdate":
													data = $(this).siblings(".date-picker").datepicker('getDates');
													var $elem = $(this).parents("table").find(".collectionDatePicker");
													$elem.each(function(){
														if($(this).find("div.disabled").length == 0 && $(this).datepicker("getDates").length == 0){
															$(this).datepicker("setDates", data);
														}
													});
													
												break;
												case "batchSessionUpdate":
													data = $('[name="'+dataFrom+'"][data-category="'+refCategory+'"]:checked').val();
													$(".assignmentAttr-session[data-category='"+refCategory+"']").each(function(){
														var disabled = $(this).attr('disabled');
														var name = $(this).attr("name");
														
														if( (typeof disabled === typeof undefined || disabled === false) && $("[name='"+name+"']:checked").length == 0){
															if($(this).val() == data){
																$(this).attr("checked", "checked");
																$(this).trigger('change');
																//$(this).valid();
															}
															
														}
													});
												break;
												case "batchOfficerUpdate":
													$("input.assignmentAttr-officerIds[data-category='"+refCategory+"']").each(function(){
														var disabled = $(this).attr('disabled');
														if( (typeof disabled === typeof undefined || disabled === false) && $(this).val().length == 0 ){
															$(this).val(data);
														}
													});
													$("select.assignmentAttr-officerIds[data-category='"+refCategory+"']").each(function(){
														var disabled = $(this).attr('disabled');
														if( (typeof disabled === typeof undefined || disabled === false) && $(this).val().length == 0 ){
															$(this).val(data.split(",")).select2({data:[data.split(",")], width: "100%"}).trigger("change");
														}
													});
												break;
												case "batchAllocationUpdate":
													$(".assignmentAttr-allocationBatch[data-category='"+refCategory+"']").each(function(){
														var disabled = $(this).attr('disabled');
														if( (typeof disabled === typeof undefined || disabled === false) && $(this).val().length == 0 ){
															$(this).val(data).trigger("change");
														}
													});
												break;
											}
										});
										
										$(".btn-copy-unCat", $html).on("click", function(){
											tabChanged = true;
											var dataFrom = $(this).data("copy");
											var refCategory = $(this).data("category");
											var data = $('[name="'+dataFrom+'"][data-category="'+refCategory+'"]').val();
											switch(dataFrom){
												case "unCatBatchStartDateUpdate":
													data = $(this).siblings(".input-group").find(".date-picker").datepicker('getDates');
													var $elem = $(this).parents("table").find(".assignmentAttr-startDate:not([disabled])");
													$elem.each(function(){
														if($(this).val() == ""){
															$(this).datepicker("setDates", data[0]);
														}
													});
												break;
												case "unCatBatchEndDateUpdate":
													data = $(this).siblings(".input-group").find(".date-picker").datepicker('getDates');
													var $elem = $(this).parents("table").find(".assignmentAttr-endDate:not([disabled])");
													$elem.each(function(){
														if($(this).val() == ""){
															$(this).datepicker("setDates", data[0]);
														}
													});
													
												break;
												case "unCatBatchCollectionDateUpdate":
													data = $(this).siblings(".date-picker").datepicker('getDates');
													var $elem = $(this).parents("table").find(".collectionDatePicker");
													$elem.each(function(){
														if($(this).find("div.disabled").length == 0 && $(this).datepicker("getDates").length == 0){
															$(this).datepicker("setDates", data);
														}
													});
													
												break;
												case "unCatBatchSessionUpdate":
													data = $('[name="'+dataFrom+'"][data-category="'+refCategory+'"]:checked').val();
													$(".assignmentAttr-session[data-category='"+refCategory+"']").each(function(){
														var disabled = $(this).attr('disabled');
														var name = $(this).attr("name");
														
														if( (typeof disabled === typeof undefined || disabled === false) && $("[name='"+name+"']:checked").length == 0){
															if($(this).val() == data){
																$(this).attr("checked", "checked");
																$(this).trigger('change');
																//$(this).valid();
															}
														}
													});
												break;
												case "unCatBatchOfficerUpdate":
													$("input.assignmentAttr-officerIds[data-category='"+refCategory+"']").each(function(){
														var disabled = $(this).attr('disabled');
														if( (typeof disabled === typeof undefined || disabled === false) && $(this).val().length == 0 ){
															$(this).val(data);
														}
													});
													$("select.assignmentAttr-officerIds[data-category='"+refCategory+"']").each(function(){
														var disabled = $(this).attr('disabled');
														if( (typeof disabled === typeof undefined || disabled === false) && $(this).val().length == 0 ){
															$(this).val(data.split(",")).select2({data:[data.split(",")], width: "100%"}).trigger("change");
														}
													});
												break;
												case "unCatBatchAllocationUpdate":
													$(".assignmentAttr-allocationBatch[data-category='"+refCategory+"']").each(function(){
														var disabled = $(this).attr('disabled');
														if( (typeof disabled === typeof undefined || disabled === false) && $(this).val().length == 0 ){
															$(this).val(data).trigger("change");
														}
													});
												break;
											}
										});
										
										$(".assignmentAttr-allocationBatch", $html).one('afterChange', function() {
											tabChanged = false;
										})
										
										$(".assignmentAttr-allocationBatch", $html).trigger("change");
										
										var requests = [];
										
										// All Batch Codes under Batch Categories should be added by default in "Set Assignment Attributes"
										if (pageId == 0 || !readonly) {
											var isSomeCategoryEmpty = false;
											$('.category-container', $html).each(function() {
												var $category = $(this);
												$category.data('isCreatingBatchCodeRows', true);
												$category.data('lastCreatedBatchCodeRowIndex', 0); // skip first option in select.batchId because it is empty
												if ($('#allocationBatchBody tr', $category).length == 0) {
													isSomeCategoryEmpty = true;
													//$(".addBatchCategoryItem", $category).click();
													//var category = $(this).data("category");
													var category = $(".addBatchCategoryItem", $category).data("category");
													tabChanged = true;
													var newId = newAlloactionAttributeId;
													newAlloactionAttributeId++;
													var newDisplayModelId = newBackTrackDateDisplayModelId;
													newBackTrackDateDisplayModelId++;
													//createAssignmentAttrRow(newId, category);
													
													requests.push(createAssignmentAttrRow(newId, category, newDisplayModelId));
													
												} else {
													$category.data('isCreatingBatchCodeRows', false);
												}
											});
											
											promise.join(requests).then(
												function(){
													//console.log('Promise P join.');
													/*$html.validate({
														highlight: function(element, errorClass, validClass) {
															$(element).parents('tr').css('border-style', 'solid');
															$(element).parents('tr').css('border-color', 'red');
														}
													});*/
												}
											);
											
											if (!isSomeCategoryEmpty) {
												tabLoadingEnd($("#tab"+$current));
											}
										} else {
											tabLoadingEnd($("#tab"+$current));
											
											/*$html.validate({
												//ignore: "",
												highlight: function(element, errorClass, validClass) {
													$(element).parents('tr').css('border-style', 'solid');
													$(element).parents('tr').css('border-color', 'red');
												}
											})*/
										}
										
										break;
									case 4:
										Datepicker($(".date-picker", $html), {todayHighlight: false, todayBtn: false});
										$("form", $html).validate({ignore: ""});
										$(".select2", $html).select2({ width: "100%"});
										$(".backTrackDateTrigger", $html).on("change", function(){
											tabChanged = true;
// 											//console.log($(this));
											if ($(this).is(":checked")) {
												//$(this).parents("tr").find(".select2").removeAttr("disabled");
												$(this).parents("tr").find(".input-group-addon").removeClass("disabled");
												Datepicker($(this).parents("tr").find(".backTrackDatePicker"));
												// automatically select previous working day.
												var prevWD = $(this).parents('tr').find('div.date').data('datePrevworkingdate');
												$(this).parents('tr').find('input.backTrack-backTrackDates').val(prevWD).trigger('change');
												$(this).parents("tr").find(".backTrackDatePicker").datepicker('update');
											}else{
												$(this).parents("tr").find(".backTrackDatePicker").datepicker("remove");
												$(this).parents("tr").find(".input-group-addon").addClass("disabled");
												$(this).parents("tr").find(".select2").val("").attr("disabled", "disabled").trigger("change");
												$(this).parents("tr").find(".backTrack-backTrackDates").val("");
												$(this).parents("tr").find(".help-block").remove();
											}
										});
										tabLoadingEnd($("#tab"+$current));
										break;

								}
							}
							
						});
						
					},
					onTabClick: function(tab, navigation, current, index, elem, forcePass) {
						var currentTab = current+1;
						var triggerTab = index+1;
//						console.log(tab1, tab2, tab3, tab4);
//						console.log(currentTab, triggerTab);
						current++;
//						console.log(forcePass, tabChanged);
						if(triggerTab > currentTab){
							if(!readonly){
								switch(triggerTab){
								case 2:
									if(tab1){
										if((forcePass == undefined || forcePass == false ) && tabChanged == true){
											bootbox.confirm({
				    						    title: "Confirmation",
				    						    message: "<spring:message code='W00022' />",
				    						    callback: function(result){
				    						    	if(result === true){
				    						    		tabChanged = false;
				    						    		//console.log(triggerTab);
				    						    		$('#rootwizard').bootstrapWizard('show', triggerTab-1);
				    						    	}
				    						    }
				    						});
											return false;
										}
									}
									return tab1;
								case 3:
									if(tab2){
										if((forcePass == undefined || forcePass == false ) && tabChanged == true){
											wizard = $(this)[0];
											bootbox.confirm({
				    						    title: "Confirmation",
				    						    message: "<spring:message code='W00022' />",
				    						    callback: function(result){
				    						    	if(result === true){
				    						    		tabChanged = false;
				    						    		//console.log(triggerTab);
				    						    		$('#rootwizard').bootstrapWizard('show', triggerTab-1);
				    						    	}
				    						    }
				    						});
											return false;
										}
									}
									return tab2;
								case 4:
									if(tab3){
										if((forcePass == undefined || forcePass == false ) && tabChanged == true){
											wizard = $(this)[0];
											bootbox.confirm({
				    						    title: "Confirmation",
				    						    message: "<spring:message code='W00022' />",
				    						    callback: function(result){
				    						    	if(result === true){
				    						    		tabChanged = false;
				    						    		//console.log(triggerTab);
				    						    		$('#rootwizard').bootstrapWizard('show', triggerTab-1);
				    						    	}
				    						    }
				    						});
											return false;
										}
									}
									return tab3;
								}
							}else{
								$(this)[0].onNext(tab, navigation, current);
							}
						}else if(triggerTab < currentTab){
							
							if((forcePass == undefined || forcePass == false ) && tabChanged == true){
								wizard = $(this)[0];
								bootbox.confirm({
	    						    title: "Confirmation",
	    						    message: "<spring:message code='W00022' />",
	    						    callback: function(result){
	    						    	if(result === true){
	    						    		tabChanged = false;
	    						    		//console.log(triggerTab);
	    						    		$('#rootwizard').bootstrapWizard('show', triggerTab-1);
	    						    	}
	    						    }
	    						});
								return false;
							}
							$('#rootwizard').bootstrapWizard('show', triggerTab-1);
						}
					},
					onNext: function(tab, navigation, index, manualTrigger) {
						//console.log(index);
						//console.log("triggered next");
						
						$form =  $("form", "#tab"+index);
						
						$form.data('submitStatus', true);
						
						tabChanged = false;
						if (index == 3){
							var validator = $form.data('validator');
							if (validator == null){
								validator = $form.validate({
									highlight: function(element, errorClass, validClass) {										
										$(element).parents('tr:first').addClass($(element).data('rowErrorClass'))
									},
									unhighlight: function(element, errorClass, validClass) {
										$(element).parents('tr:first').removeClass($(element).data('rowErrorClass'));
									},
									invalidHandler: function(event, validator) {
										var errors = validator.numberOfInvalids();
										if(errors) {
											$('html, body').animate({
												scrollTop: $(validator.errorList[0].element).offset().top
											}, 1000);
										} else {
										}
									}
								});
								$form.data('validator', validator);
							}
						}
						valid = $form.valid();
						if(valid){
							if(index == 1){
								fromDate = $("[name='surveyMonth.startDateStr']", $form).val();
								endDate = $("[name='surveyMonth.endDateStr']", $form).val();
							}
							if(index == 2){
								if($(".allocationBatchRow").length == 0){
									valid = false;
									bootbox.alert({
	        						    title: "Alert",
	        						    message: "<spring:message code='E00010' />"
	        						});
									return valid;
								}
							}
							
							var data = readonly ? serializeWithDisabledInput(index) : $form.serialize();
							Modals.startLoading();
							
							$.ajax({
								type: 'POST',
								url: "<c:url value='/assignmentAllocationAndReallocation/SurveyMonth/submitTabForm'/>",
								data: "completeTab=true&index="+index+"&"+data,
								async:false,
								success: function(result){
									result = parseInt(result);
									if(result == 0){
										valid = true;
									}else if(result == 1){
										Modals.endLoading();
										valid = false;
									}else{
										switch(result){
											case 10:
												Modals.endLoading();
												bootbox.alert({
				        						    title: "Alert",
				        						    message: "<spring:message code='E00010' />"
				        						});
												valid = false;	
												break;
											case 124:
												Modals.endLoading();
												bootbox.alert({
				        						    title: "Alert",
				        						    message: "<spring:message code='E00124' />"
				        						});
												valid = false;	
												break;
											case 125:
												Modals.endLoading();
												bootbox.alert({
				        						    title: "Alert",
				        						    message: "<spring:message code='E00125' />"
				        						});
												valid = false;	
												break;
											case 15:
												if(forcePass == 15){
													valid = true;
													forcePass = 0;
													break;
												}else{
													Modals.endLoading();
													bootbox.confirm({
					        						    title: "Confirmation",
					        						    message: "<spring:message code='W00015' />",
					        						    callback: function(result){
					        						    	if(result === true){
						        						    	forcePass = 15;
						        						    	$('#rootwizard').bootstrapWizard("next");
					        						    	}
					        						    }
					        						});
													valid = false;
													break;
												}
										}
										
									}
									
								}
							});
//							console.log("valid: "+valid);
//							console.log(manualTrigger);
							if(valid && !readonly && !manualTrigger){
								switch(index){
									case 1:
										tab1 = true;
										tab2 = false;
										tab3 = false;
										tab4 = false;
										break;
									case 2:
										tab1 = true;
										tab2 = true;
										tab3 = false;
										tab4 = false;
										break;
									case 3:
										tab1 = true;
										tab2 = true;
										tab3 = true;
										tab4 = false;
										break;
									case 4:
										tab1 = true;
										tab2 = true;
										tab3 = true;
										tab4 = true;
										break;
								}
							}
							//console.log(tab1, tab2, tab3, tab4)
							if(index == 4 && valid == true){
								$form =  $("form", "#tab4");
								
								var data = readonly ? serializeWithDisabledInput(index) : $form.serialize();
								
								$.ajax({
									type: 'POST',
									url: "<c:url value='/assignmentAllocationAndReallocation/SurveyMonth/submitTabForm'/>",
									data: "completeTab=false&index=4&"+ data,
									async:false,
									success: function(result){
										window.location =  "<c:url value='/assignmentAllocationAndReallocation/SurveyMonth/saveSession?isDraft=0'/>";
									}
								});	
								
							}
							
							return valid;
							
						}else{
							return valid;
						}
						
					},
					onPrevious: function(tab, navigation, index) {
						
						if(index<0){
							return;
						}
						//console.log("triggered previous", index);
						tabChanged = false;
						
						Modals.startLoading();
						
						$form =  $("form", "#tab"+(index+2));
						
						var data = readonly ? serializeWithDisabledInput(index) : $form.serialize();
						
						$.ajax({
							type: 'POST',
							url: "<c:url value='/assignmentAllocationAndReallocation/SurveyMonth/submitTabForm'/>",
							data: "completeTab=false&index="+(parseInt(index)+2)+"&"+ data,
							async:false,
							success: function(result){
								return result;
							}
						});
						
						//$.post("<c:url value='/assignmentAllocationAndReallocation/SurveyMonth/submitTabForm'/>","completeTab=false&index="+(parseInt(index)+2)+"&"+$form.serialize(), function(result){
						//	return result;
						//});
					},
					onInit: function(){
						$.ajax({
							type: 'POST',
							url: "<c:url value='/assignmentAllocationAndReallocation/SurveyMonth/initSession'/>",
							data: "id=<c:out value='${id}'/>",
							async:false,
							success: function(result){
								newBatchAllocationId = result.newBatchAllocationId;
								newAlloactionAttributeId = result.newAlloactionAttributeId;
								newBackTrackDateDisplayModelId = result.newBackTrackDateDisplayModelId;
								readonly = result.readonly;
								tab1 = result.tab1;
								tab2 = result.tab2;
								tab3 = result.tab3;
								tab4 = result.tab4;
							}
						});
					}
				});
				
				$('#rootwizard .finish').click(function() {
				
				});
				
				$(".collectionDate-select2").select2({
					closeOnSelect:false
				});
				
				$(".officer-select2").select2();
				
				$("#reallocateModalForm").validate({
					submitHandler:function(){}
				});
				//$("#reallocateModalForm").validate();
				
				<sec:authorize access="!(hasPermission(#user, 2) or hasPermission(#user, 8) or hasPermission(#user, 256))">
					viewOnly();
				</sec:authorize>
			});
										
			function saveSurveyMonth(){
				$('.clearfix').addClass("overlay");
				
				var currentIndex = $('#rootwizard').bootstrapWizard('currentIndex') + 1;
				$form = $("form", "#tab"+currentIndex);
				var data = serializeWithDisabledInput(currentIndex);
				$.ajax({
					type: 'POST',
					url: "<c:url value='/assignmentAllocationAndReallocation/SurveyMonth/saveSurveyMonth'/>",
					data: "completeTab=true&tabIndex=" + currentIndex + "&" + data,
					async:false,
					success: function (isSuccess) {
						var message = isSuccess ? "<spring:message code='I00001' />" : "<spring:message code='E00012' />"
								
						bootbox.alert({
						    title: 'Information',
						    message: message
						});
						$('.clearfix').removeClass("overlay");
						$('#rootwizard a[href="#tab'+currentIndex+'"]').tab('show');
						tabChanged = false;
						if(currentIndex != undefined && currentIndex == 4){
							forcePass = true;
						}
					}
				});
			}
			
			function deleteBatchAllocationWithConfirm(event){
				var $elem = $(this);
				bootbox.confirm({
				     title:"Confirmation",
				     message: "<spring:message code='W00001' />",
				     callback: function(result){
				         if (result){
							if($elem.data("newid") != undefined){
								var newId = $elem.data("newid");
								$(".allocationBatchRow").remove("[data-newid='"+newId+"']");
							}else{
								var id = $elem.data("id");
								$(".allocationBatchRow").remove("[data-id='"+id+"']");
							}
				         }
				     }
				});
			}
			
			function deleteAssignmentAllocationWithConfirm(event){
				var $elem = $(this);
				bootbox.confirm({
				     title:"Confirmation",
				     message: "<spring:message code='W00001' />",
				     callback: function(result){
				         if (result){
							if($elem.data("newid") != undefined){
								var newId = $elem.data("newid");
								$("#assignmentForm tr[data-newid='"+newId+"']").remove();
							}else{
								var id = $elem.data("id");
								$("#assignmentForm tr[id='"+id+"']").remove();
							}
				         }
				     }
				});
			}
			
			function serializeWithDisabledInput(index){
				var data
				$form =  $("form", "#tab"+index);
				$form.find(':input').each(function(){
			        var name = $(this).attr('name');
			        var val = $(this).val();
			    	if(typeof name !== typeof undefined && name !== false && typeof val !== typeof undefined)
			        {
			            if( !$(this).is("input[type=radio]") || $(this).prop('checked'))
			            	data += (data==""?"":"&")+encodeURIComponent(name)+"="+encodeURIComponent(val);
			        }
			    });
				return data;
			}
			
		</script>
	</jsp:attribute>
	<jsp:body>
		<input id="pageId" value="${id}" type="hidden" />
		<input id="readonly" value="${readonly}" type="hidden" />
		<input id="isDraft" value="${isDraft}" type="hidden" />
		<section class="content-header">
          <h1>Survey Month Maintenance</h1>
        </section>
        
        <section class="content">
        
        <div class="box box-primary">
        	<div class="clearfix"></div>
			<div class="box-header with-border">
				<a class="btn btn-default" href="<c:url value='/assignmentAllocationAndReallocation/SurveyMonth/home'/>">Back</a>
			</div>		
        	
        	<div class="row">
				<div class="col-md-12" >
				<!-- content -->
					<div id="rootwizard" class="nav-tabs-custom">
						<div class="navbar ">
							<div class="navbar-inner nav-tabs-custom">
						    	<div class="container nav nav-tabs">
									<ul>
									  	<li><a href="#tab1" data-toggle="tab">Set Survey Month</a></li>
										<li><a href="#tab2" data-toggle="tab">Set Allocation Batch</a></li>
										<li><a href="#tab3" data-toggle="tab">Set Assignments Attributes</a></li>
										<li><a href="#tab4" data-toggle="tab">Set BackTrack Date</a></li>
									</ul>
								</div>
							</div>
						</div>
						<div id="bar" class="progress progress-striped active">
						  <div class="bar progress-bar progress-bar-primary progress-bar-striped"></div>
						</div>
						<div class="tab-content">
						    <div class="tab-pane" id="tab1">
						      
						    </div>
						    <div class="tab-pane" id="tab2">
						      
						    </div>
							<div class="tab-pane" id="tab3">
							
						    </div>
							<div class="tab-pane" id="tab4">
							
						    </div>
							<ul class="pager wizard">
								<li class="previous"><a href="javascript:;">Previous</a></li>
							  	<li class="next"><a href="javascript:;">Next</a></li>
							  	<c:if test="${readonly == false}">
<%-- 							  		<c:if test="${isDraft == true}"> --%>
										<li class="next finish" style="display:none; float:right; margin-right:5px"><a href="javascript:;">Finish</a></li>
										<c:if test="${isCreated == false}">
											<button id="saveBtn" type="button" class="btn btn-primary" style="display: none; float:right; margin-right:5px" data-loading-text="Loading..."onclick='saveSurveyMonth()'>Save</button>
							  			</c:if>
<%-- 							  		</c:if> --%>
							  	</c:if>
							</ul>
						</div>
					</div>
				</div>
			</div>
		</div>		
		
		<!-- Reallocate Dialog -->
<div class="modal fade" id="reallocateModal" tabindex="-1" role="dialog"
	aria-labelledby="dialogLabel" aria-hidden="true" style="display:none">
	<form id="reallocateModalForm">
		<div class="modal-dialog">
			<div class="modal-content">
			 	<div class="modal-header">
					<h4 class="modal-title" >Reallocate</h4>
			  	</div>
			  	<div class="modal-body form-horizontal">
					<div class="form-group">
						<div class="col-md-3 control-label">Collection Date:</div>
						<div class="col-md-7">
							<select class="collectionDate-select2" required multiple data-allow-clear="true" ></select>
						</div>
					</div>								
					<div class="form-group">
						<div class="col-md-3 control-label">Field Officer:</div>
						<div class="col-md-7">
							<select class="form-control officer-select2" required  >
								<option value=""></option>
								<c:forEach items="${officerList}" var="officer">
									<option value="<c:out value="${officer.userId}" />" <c:if test="${uncateAA.getOfficerIds() == officer.userId }">selected</c:if> >${officer.staffCode} - ${officer.chineseName} (${officer.destination})</option>
								</c:forEach>
							</select>
						</div>
					</div>
				</div>
				<div class="modal-footer">
				  <button type="submit" class="btn btn-default btn-default pull-left" >
		          	Submit
		          </button>
		          <button type="button" class="btn btn-default btn-default pull-Right" data-dismiss="modal">
		          	Cancel
		          </button>
		        </div>
			</div>
		</div>
	</form>
</div>
        </section>		
	</jsp:body>
</t:layout>
