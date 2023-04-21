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
			.input-group .select2-hidden-accessible{
				display: none;
			}
		</style>
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-wizard.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker.jsp"%>
		<%@include file="/WEB-INF/views/includes/select2.jsp"%>
		<%@include file="/WEB-INF/views/includes/commonDialog/userLookup.jsp" %>
		<script>			
			var forcePass = false;
			var adjustmentId = 0;
			var tabChanged = false;
			var tab1 = false;
			var tab2 = false;
			var tab3 = false;
			var row = null;
			var editFromUserId = 0;
			var editToUserId = 0;
			$(document).ready(function(){
				
				jQuery.validator.addMethod("UserChecking", function(value, element) {
					$fromUser = $(element).parents("form").find('[name="fromUser"]').val();
					if($(element).val() == $fromUser){
						return false;
					}else{
						return true;
					}
					}, "<spring:message code='E00138' />");
				$.validator.addClassRules("UserChecking", { UserChecking: true });
				
				jQuery.validator.addMethod("validateNumber", $.validator.methods.number, "<spring:message code='E00071' />");
				$.validator.addClassRules("validateNumber", {required: true, validateNumber: true});
				
				var readonly = false;
				
				$('#rootwizard').bootstrapWizard({
					onTabShow: function(tab, navigation, index) {
						var $total = navigation.find('li').length;
						var $current = index+1;
						var $percent = ($current/$total) * 100;
						tabChanged = false;
						$('#rootwizard').find('.bar').css({width:$percent+'%'});
						
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
							url: "<c:url value='/assignmentAllocationAndReallocation/AssignmentAllocation/getTabItem'/>",
							data: {index: $current},
							async:false,
							success: function(partialView){
								$html = $(partialView);
								$("#tab"+$current).html($html); 
								switch ($current){
									case 1:
										var $referenceMonthStr = $('[name="allocationBatchTabModel.referenceMonthStr"]', $html);
										$('[name="allocationBatchTabModel.referenceMonthStr"]', $html).on("change", function(){
											$.post("<c:url value='/assignmentAllocationAndReallocation/AssignmentAllocation/getAllocationBatch'/>", {monthStr: $(this).val()}, function(result){
												//console.log(result);
												$('[name="allocationBatchTabModel.allocationBatchId"]').html("");
												$('[name="allocationBatchTabModel.allocationBatchId"]').append("<option value=''></option>");
												$(result).each(function(index ){
													$('[name="allocationBatchTabModel.allocationBatchId').append("<option value='"+$(this)[0].allocationBatchId+"'>"+$(this)[0].batchName+"</option>");
												});
												
												$('[name="allocationBatchTabModel.allocationBatchId').trigger("change");
											});
										});
										
										$('[name="allocationBatchTabModel.allocationBatchId"]', $html).on("change", function(){
											$('#allocationBatchDetails').html("<progress></progress>");
											if($(this).val() != "" && $(this).val() != null){
												$.post("<c:url value='/assignmentAllocationAndReallocation/AssignmentAllocation/getAllocationBatchDetails'/>", {allocationBatchId: $(this).val(), monthStr: $referenceMonthStr.val()}, function(result){
													$("#allocationBatchDetails").html(result);
												});
											}else{
												$("#allocationBatchDetails").html("");
											}
										});
										if ($('[name="allocationBatchTabModel.allocationBatchId"]', $html).val() != '' && $('[name="allocationBatchTabModel.allocationBatchId"]', $html).val() != null) {
											$('[name="allocationBatchTabModel.allocationBatchId"]', $html).trigger('change');
										}
										
										if($('[name="allocationBatchTabModel.referenceMonthStr"]', $html).val() != "" && $('[name="allocationBatchTabModel.allocationBatchId"]', $html).val() != ""){
											$.post("<c:url value='/assignmentAllocationAndReallocation/AssignmentAllocation/getAllocationBatchDetails'/>", 
												{allocationBatchId: $('[name="allocationBatchTabModel.allocationBatchId"]', $html).val()}, function(result){
												$("#allocationBatchDetails").html(result);
											});
										}
										
										$("form", $html).validate({ignore: ""});
										Datepicker($(".date-picker:not([readonly])", $html));
										$(".select2", $html).select2({closeOnSelect: true, width: "100%"});
									break;
									case 2:
										
										$('.searchOfficer', $html).userLookup({
											selectedIdsCallback: function(selectedIds, singleRowData) {
												var id = selectedIds[0];
												this.$element.siblings('select').val(id).trigger("change");
												
											},
											queryDataCallback: function(model) {
												model.authorityLevel = 16;
											},
											multiple: false
										});

										$("form", $html).validate({ignore: ""});
										$(".select2", $html).select2({width: "100%"});
										break;
									case 3:
										$('input.adjustedManDay', $html).on("change", function(){
											$(this).siblings("span.adjustedManDay").html($(this).val());
											calculatingManDays($(this).data("user"));
										});
										$('input.manDayOfBalance', $html).on("change", function(){
											$(this).siblings("span.manDayOfBalance").html($(this).val());
											//calculatingManDays($(this).data("user"));
										});
										$('input.manDayTransfer', $html).on("change", function(){
											$(this).siblings("span.manDayTransfer").html($(this).val()*-1);
											//calculatingManDays($(this).data("user"));
										});
										
										$('input.manualAdjustment', $html).on("change", function(){
											$(this).val(Math.round($(this).val() * 10) / 10.0);
											
											var manualAdj = parseFloat($(this).val());
											console.log(manualAdj);
											if(!isNaN(manualAdj)){
												var $AdjustedManDayInputElem = $(this).parents("tr").find("input.adjustedManDay");
												$AdjustedManDayInputElem.val(roundUpto(parseFloat($AdjustedManDayInputElem.data("original"))+manualAdj, 1)).trigger("change");
											}else{
												$(this).val(0).trigger("change");
											}
										});
										$('#adjustmentFormModal', $html).on('hidden', function(){
										    $(this).data('modal', null);
										});
										
										$('.data-table', $html).DataTable({
											"ordering": false, 
											"searching": false,
											"buttons": [],
											"processing": false,
								            "serverSide": false,
								            "paging": false,
								            "scrollY": "420px",
								            "scrollCollapse": true
										});
										
										break;
								}
								$("input, select").on("change", function(){
									tabChanged = true;
								})
							}
							
						});
						
					},
					onTabClick: function(tab, navigation, current, index, elem, forcePass) {
						var currentTab = current+1;
						var triggerTab = index+1;
						console.log(tab1, tab2, tab3);
						console.log(currentTab, triggerTab);
						current++;
						console.log(forcePass, tabChanged);
						if(triggerTab > currentTab){
							if(!readonly){
								switch(triggerTab){
								case 2:
									if(tab1){
										if(tabChanged == true){
											bootbox.confirm({
				    						    title: "Confirmation",
				    						    message: "<spring:message code='W00022' />",
				    						    callback: function(result){
				    						    	if(result === true){
				    						    		tabChanged = false;
				    						    		console.log(triggerTab);
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
										if(tabChanged == true){
											wizard = $(this)[0];
											bootbox.confirm({
				    						    title: "Confirmation",
				    						    message: "<spring:message code='W00022' />",
				    						    callback: function(result){
				    						    	if(result === true){
				    						    		tabChanged = false;
				    						    		console.log(triggerTab);
				    						    		$('#rootwizard').bootstrapWizard('show', triggerTab-1);
				    						    	}
				    						    }
				    						});
											return false;
										}
									}
									return tab2;								}
							}else{
								$(this)[0].onNext(tab, navigation, current);
							}
						}else{
							$form =  $("form", "#tab"+currentTab);
							$.ajax({
								type: 'POST',
								url: "<c:url value='/assignmentAllocationAndReallocation/AssignmentAllocation/submitTabForm'/>",
								data: "completeTab=false&index="+currentTab+"&"+$form.serialize(),
								async:false,
								success: function(result){
									return result;
								}
							});
						}
					},
					onNext: function(tab, navigation, index) {
						$form =  $("form", "#tab"+index);
						console.log($form.serialize())
						valid = $form.valid();
						if(valid){
							$.ajax({
								type: 'POST',
								url: "<c:url value='/assignmentAllocationAndReallocation/AssignmentAllocation/submitTabForm'/>",
								data: "completeTab=true&x=0&index="+index+"&"+$form.serialize(),
								async:false,
								success: function(result){

									var resultParameters = result.split("|");
									var errorCode = parseInt(resultParameters[0]);
									var errorMessages = resultParameters[1];

									if(errorCode == 0){
										valid = true;
									}else if(errorCode == 1){
										valid = false;
									}else if(errorCode > 1){
										switch(errorCode){
											case 10:
												bootbox.alert({
				        						    title: "Alert",
				        						    message: errorMessages
				        						});
												valid = false;	
												break;
											case 2:
												bootbox.confirm({
					    						    title: "Confirmation",
				        						    message: errorMessages,
					    						    callback: function(result){
					    						    	valid = result;
					    						    	if(index == 3 && valid == true){
					    									bootbox.confirm({
					    	        						    title: "Confirmation",
					    	        						    message: "<spring:message code='W00021' />",
					    	        						    callback: function(result){
					    	        						    	if(result === true){
					    	        						    		window.location =  "<c:url value='/assignmentAllocationAndReallocation/AssignmentAllocation/saveSession'/>";
					    	        						    	}
					    	        						    }
					    	        						});
					    								}
					    						    }
					    						});
												valid = false;	
												break;
										}
										
									}
									
								}
							});
							//console.log("valid: "+valid);
							
							if(valid && !readonly){
								switch(index){
									case 1:
										tab1 = true;
										tab2 = false;
										tab3 = false;
										break;
									case 2:
										tab1 = true;
										tab2 = true;
										tab3 = false;
										break;
									case 3:
										tab1 = true;
										tab2 = true;
										tab3 = true;
										break;
								}
							}
							if(index == 3 && valid == true){
								bootbox.confirm({
        						    title: "Confirmation",
        						    message: "<spring:message code='W00021' />",
        						    callback: function(result){
        						    	if(result === true){
        						    		window.location =  "<c:url value='/assignmentAllocationAndReallocation/AssignmentAllocation/saveSession'/>";
        						    	}
        						    }
        						});
							}
							
							return valid;
							
						}else{
							return valid;
						}
						
					},
					onPrevious: function(tab, navigation, index) {
						$form =  $("form", "#tab"+(index+2));
						if(!readonly){
							switch(index){
								case 1:
									tab1 = true;
									tab2 = false;
									tab3 = false;
									break;
								case 2:
									tab1 = true;
									tab2 = true;
									tab3 = false;
									break;
								case 3:
									tab1 = true;
									tab2 = true;
									tab3 = true;
									break;
							}
						}
						$.ajax({
							type: 'POST',
							url: "<c:url value='/assignmentAllocationAndReallocation/AssignmentAllocation/submitTabForm'/>",
							data: "completeTab=false&index="+(parseInt(index)+2)+"&"+$form.serialize(),
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
							url: "<c:url value='/assignmentAllocationAndReallocation/AssignmentAllocation/initSession'/>",
							data: "id=<c:out value='${id}'/>",
							async:false,
							success: function(result){
							}
						});
					}
				});
				
				$('#rootwizard .finish').click(function() {
					
				});
				
			});
			
			function calculatingManDays($userId){
				var availableManDays = parseFloat($("input.availableManDays[data-user='"+$userId+"']").val());
				var adjustedManDays = parseFloat($("input.adjustedManDay[data-user='"+$userId+"']").val());
				if(adjustedManDays == ""){
					adjustedManDays = 0.0;
				}
				
				availableManDays = Math.round(availableManDays * 10) / 10.0;
				adjustedManDays = Math.round(adjustedManDays * 10) / 10.0;
				
				var manDaysOut = 0;
				var manDaysIn = 0;
				
				$("input.fromUser[value='"+$userId+"']" ,$("#adjustmentTable")).each(function(){
					var row = $(this).data("row");
					manDaysOut = manDaysOut + parseFloat($("input.manDay[data-row='"+row+"']").val());
				});
				$("input.toUser[value='"+$userId+"']" ,$("#adjustmentTable")).each(function(){
					var row = $(this).data("row");
					manDaysIn = manDaysIn + parseFloat($("input.manDay[data-row='"+row+"']").val());
				});
				
				manDaysOut = Math.round(manDaysOut * 10) / 10.0;
				manDaysIn = Math.round(manDaysIn * 10) / 10.0;
				
				var manDayOfTransferInOut = manDaysOut - manDaysIn;
				manDayOfTransferInOut = Math.round(manDayOfTransferInOut * 10) / 10.0;
				console.log(manDayOfTransferInOut);
				$("input.manDayTransfer[data-user='"+$userId+"']").val(manDayOfTransferInOut).trigger('change');
				
				console.log(availableManDays);
				var balance = availableManDays-adjustedManDays+manDaysOut-manDaysIn;
				balance = Math.round(balance * 10) / 10.0;
				$("input.manDayOfBalance[data-user='"+$userId+"']").val(balance).trigger("change");
			}
			
			function addAdjustment(fromUser){
				var adjustmentFormModal = $("#adjustmentFormModal").modal();
				$("[name=fromUser]", adjustmentFormModal).val(fromUser).trigger("change");
				$("[name=toUser]", adjustmentFormModal).val("").trigger("change");
				$("[name=manDay]", adjustmentFormModal).val("").trigger("change");
				$("[name=remark]", adjustmentFormModal).val("").trigger("change");
				$('.searchOfficer', adjustmentFormModal).userLookup({
					selectedIdsCallback: function(selectedIds, singleRowData) {
						var id = selectedIds[0];
						this.$element.siblings('select').val(id).trigger("change");
						
					},
					queryDataCallback: function(model) {
						model.authorityLevel = 16;
					},
					multiple: false
				});

				$("form", adjustmentFormModal).validate({ignore: ""});
				$(".select2", adjustmentFormModal).select2({width: "100%"});
				$("form", adjustmentFormModal).off("submit");
				$("form", adjustmentFormModal).on("submit", vailadateAdjustment);
				adjustmentFormModal.modal("show");
			}
			
			function vailadateAdjustment(event){
				event.preventDefault();
				if($(this).valid()){
					var manDay = parseFloat($('[name="manDay"]', $(this)).val());
					if(isNaN(manDay)){
						$('[name="manDay"]', $(this)).addClass("error");
						return false;
					}
					
					manDay = Math.round(manDay * 10) / 10.0;
					
					var fromUserName = $('[name="fromUser"] option:checked', $(this)).html();
					var fromUser = $('[name="fromUser"] option:checked', $(this)).val();
					
					var toUserName = $('[name="toUser"] option:checked', $(this)).html();
					var toUser = $('[name="toUser"] option:checked', $(this)).val();
					
					if(toUser == fromUser){
						$('[name="toUser"]', $(this)).addClass("error");
						return false;
					}
					
					var remark = $('[name="remark"]', $(this)).val();
					
					$("tbody", "#adjustmentTable").append(
							"<tr class='hide'>"+
								"<td class='text-center'><span class='fromUserName'>"+fromUserName+"</span>"+
									"<input type='hidden' class='adjustment fromUser' data-row='"+adjustmentId+"' name='adjustment["+adjustmentId+"].fromUserId' value='"+fromUser+"'>"+
									"<input type='hidden' class='adjustment fromUserName' name='adjustment["+adjustmentId+"].fromUserName' value='"+fromUserName+"'>"+
								"</td>" +
								"<td class='text-center'><span class='toUserName'>"+toUserName+"</span>"+
									"<input type='hidden' class='adjustment toUser'data-row='"+adjustmentId+"' name='adjustment["+adjustmentId+"].toUserId' value='"+toUser+"'>"+
									"<input type='hidden' class='adjustment toUserName' name='adjustment["+adjustmentId+"].toUserName' value='"+toUserName+"'>"+
								"</td>" +
								"<td class='text-center'><span class='adjustment manDay'>"+manDay+"</span><input type='hidden' class='adjustment manDay' data-row='"+adjustmentId+"' name='adjustment["+adjustmentId+"].manDay' value='"+manDay+"'></td>" +
								"<td class='text-center'><span class='adjustment remark'>"+remark+"</span><input type='hidden' class='adjustment remark' name='adjustment["+adjustmentId+"].remark' value='"+remark+"'></td>" +
								"<td class='text-center'>"
									+"<a href='javascript:void(0)' onclick='updateRecords(this)' class='table-btn'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a>&nbsp;"
									+"<a href='javascript:void(0)' onclick='deleteRecordsWithConfirm(this)' class='table-btn btn-delete'><span class='fa fa-times' aria-hidden='true'></span></a>"
								+"</td>" +
							"</tr>"
					);

					calculatingManDays(fromUser);
					calculatingManDays(toUser);
					
					adjustmentId++;
					$("#adjustmentFormModal").modal("hide");
					
					if (!validateUserManDayOverflow(fromUser) || !validateUserManDayOverflow(toUser)) {
						bootbox.alert({
						    title: "Alert",
						    message: "<spring:message code='E00150' />"
						});
						$("#adjustmentTable tbody tr").last().remove();
						calculatingManDays(fromUser);
						calculatingManDays(toUser);
					} else {
						$("#adjustmentTable tbody tr").last().removeClass('hide');
					}
				}
				
				return false;
			}
			
			function updateRecords(elem){
				var $row = $(elem).parents("tr");
				row = $row;
				editFromUserId = $row.find('input.fromUser').val();
				editToUserId = $row.find('input.toUser').val();
				var adjustmentFormModal = $("#adjustmentFormModal").modal();
				$("[name=fromUser]", adjustmentFormModal).val($row.find('input.fromUser').val()).trigger("change");
				$("[name=toUser]", adjustmentFormModal).val($row.find('input.toUser').val()).trigger("change");
				$("[name=manDay]", adjustmentFormModal).val($row.find('input.manDay').val()).trigger("change");
				$("[name=remark]", adjustmentFormModal).val($row.find('input.remark').val()).trigger("change");
				$('.searchOfficer', adjustmentFormModal).userLookup({
					selectedIdsCallback: function(selectedIds, singleRowData) {
						var id = selectedIds[0];
						this.$element.siblings('select').val(id).trigger("change");
						
					},
					queryDataCallback: function(model) {
						model.authorityLevel = 16;
					},
					multiple: false
				});

				$("form", adjustmentFormModal).validate({ignore: ""});
				$(".select2", adjustmentFormModal).select2({width: "100%"});
				$("form", adjustmentFormModal).off("submit");
				$("form", adjustmentFormModal).on("submit", vailadateAdjustmentUpdate);
				adjustmentFormModal.modal("show");
			}
			
			function vailadateAdjustmentUpdate(event){
				event.preventDefault();
				if($(this).valid()){
					var manDay = parseFloat($('[name="manDay"]', $(this)).val());
					if(isNaN(manDay)){
						$('[name="manDay"]', $(this)).addClass("error");
						return false;
					}
					
					manDay = Math.round(manDay * 10) / 10.0;
					
					var fromUserName = $('[name="fromUser"] option:checked', $(this)).html();
					var fromUser = $('[name="fromUser"] option:checked', $(this)).val();
					
					var toUserName = $('[name="toUser"] option:checked', $(this)).html();
					var toUser = $('[name="toUser"] option:checked', $(this)).val();
					
					if(toUser == fromUser){
						$('[name="toUser"]', $(this)).addClass("error");
						return false;
					}
					
					var remark = $('[name="remark"]', $(this)).val();
					
					row.find("input.fromUser").val(fromUser);
					row.find("input.fromUserName").val(fromUserName);
					row.find("span.fromUserName").html(fromUserName);
					
					row.find("input.toUser").val(toUser);
					row.find("input.toUserName").val(toUserName);
					row.find("span.toUserName").html(toUserName);
					
					row.find("input.manDay").val(manDay);
					row.find("span.manDay").html(manDay);
					
					row.find("input.remark").val(remark);
					row.find("span.remark").html(remark);

					calculatingManDays(fromUser);
					calculatingManDays(toUser);
					calculatingManDays(editFromUserId);
					calculatingManDays(editToUserId);
					
					if (!validateUserManDayOverflow(fromUser) || !validateUserManDayOverflow(toUser)
							 || !validateUserManDayOverflow(editFromUserId) || !validateUserManDayOverflow(editToUserId)) {
						bootbox.alert({
						    title: "Alert",
						    message: "<spring:message code='E00150' />"
						});
					}
					
					$("#adjustmentFormModal").modal("hide");
				}
				
				return false;
			}
			
			function deleteRecordsWithConfirm(elem){
				var $elem = $(elem);
				bootbox.confirm({
				     title:"Confirmation",
				     message: "<spring:message code='W00001' />",
				     callback: function(result){
				         if (result){
				        	var $tr = $elem.parents("tr");
				        	 
				        	var fromUser = $("input.fromUser", $tr ).val();
				        	var toUser = $("input.toUser", $tr ).val();
				        	$tr.remove();
			        		calculatingManDays(fromUser);
							calculatingManDays(toUser);
				         }
				     }
				});
			}
			
			function validateUserManDayOverflow(userId) {
				var adjustedManDay = parseFloat($("input.adjustedManDay[data-user='" + userId + "']").val());
				var manDayTransfer = parseFloat($("input.manDayTransfer[data-user='" + userId + "']").val());
				return manDayTransfer <= adjustedManDay;
			}
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Assignment Allocation</h1>
        </section>
        
        <section class="content">
        	<div class="box" >
        		<div class="box-header with-border">
					<a class="btn btn-default" href="<c:url value='/assignmentAllocationAndReallocation/AssignmentAllocation/home'/>">Back</a>
				</div>
				<div class="box-body">
		        	<div class="row">
						<div class="col-md-12" >
						<!-- content -->
							<div id="rootwizard" class="nav-tabs-custom">
								<div class="navbar ">
									<div class="navbar-inner nav-tabs-custom">
								    	<div class="container nav nav-tabs">
											<ul>
											  	<li><a href="#tab1" data-toggle="tab">Select Survey Month</a></li>
												<li><a href="#tab2" data-toggle="tab">Set District Head</a></li>
												<li><a href="#tab3" data-toggle="tab">Adjustment Assignment</a></li>
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
									<ul class="pager wizard">
										<li class="previous"><a href="javascript:;">Previous</a></li>
									  	<li class="next"><a href="javascript:;">Next</a></li>
									  	<li class="next finish" style="display:none;"><a href="javascript:;">Finish</a></li>
									</ul>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
        </section>
	</jsp:body>
</t:layout>