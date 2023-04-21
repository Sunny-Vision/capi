<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
 <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page session="false" %>
<jsp:useBean id="niceDate" class="java.util.Date"/>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/timepicker-css.jsp" %>
		
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp" %>
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
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/timepicker.jsp" %>
		
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/userLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/roleLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/batchLookup.jsp" %>
		<script src="<c:url value='/resources/js/underscore-min.js'/>" ></script>
		<script>
			var isChanged = false;
			
			function deleteTableRow(obj) {
				bootbox.confirm({
					title:"Confirmation",
					message: "<spring:message code='W00001' />",
					callback: function(result){
						if (result){
							var deleteBtn = $(obj);
							var tr = deleteBtn.parents('tr:first');
							tr.remove();
							
							isChanged = true;
						}
					}
				})
			}
			
			
			/*
			$.validator.addMethod("pwcheckallowedchars", function (value) {
	    		return /^[a-zA-Z0-9`~!@#$%^&*()_=\[\]{};':"\\|,.<>\/?+-]+$/.test(value);
	    	}, "The password contains non-admitted characters");
	    	
	    	$.validator.addMethod("pwchecklower", function(value) {
				return /[a-z]/.test(value);
			}, "must include lower case");
	    	
	    	$.validator.addMethod("pwcheckupper", function(value) {
				return /[A-Z]/.test(value);
			}, "must include upper case");
	    	
	    	$.validator.addMethod("pwcheckdigit", function(value) {
				   return /\d/.test(value); 
			}, "must include digital number");
	    	 
	    	$.validator.addMethod("pwcheckspecial", function(value) {
				   return /[`~!@#$%^&*()_=\[\]{};':"\\|,.<>\/?+-]/.test(value)
			}, "must include special character");
	    	 
			var pwMinLength = ${pwMinLength};
			
			
			*/
			var pwMinLength = ${pwMinLength};
			var msgMinLength = "<spring:message code='E00162' arguments='${pwMinLength}' />";
			var validLength=0;
			var validUpper=0;
			var validLower=0;
			var validDigit=0;
			var validSpecial=0;
			var validTotal=0;

	    	 
	    	 function resetNewPasswordCheck() {
	    		validLength=0;
				validUpper=0;
				validLower=0;
				validDigit=0;
				validSpecial=0;
				refreshStatus();
	    	 }
	    	 
	    	 function refreshStatus() {
					console.log(
							"validLength:"+validLength+
							", validUpper:"+validUpper+
							", validLower:"+validLower+
							", validDigit:"+validDigit+
							", validSpecial:"+validSpecial);
					
					validTotal = validUpper+validLower+validDigit+validSpecial;
					
					
	    	 }
	    
				 
	    	 
			$.validator.addMethod("pwcheckvalidclass", function(value, element, param) {
				 var $otherElement = $(param);
			    if ($otherElement == '') {
			    	resetNewPasswordCheck();
			    };

			    console.log("value.length:"+value.length);
			    if (value.length >= pwMinLength) {
			    	validLength=1;
			    } else {
			    	validLength=0;
			    }
			    
				
				if (/[A-Z]/.test(value)) {
					validUpper=1;
				} else {
					validUpper=0;
				}
				
				
				if (/[a-z]/.test(value)) {
					validLower=1
				} else {
					validLower=0;
				}
				
				if (/\d/.test(value)) {
					validDigit=1;
				} else {
					validDigit=0;
				}
				
				
				//if (/[`~!@#$%^&*()_=\[\]{};':"\\|,.<>\/?+-]/.test(value)) {
				if(/[^0-9A-Za-z]/.test(value)){

					validSpecial=1;
				} else {
					validSpecial=0;
				}

				refreshStatus();					
				if (validTotal >=3 && validLength==1) {
					return true;
				}
				
				return false;
				
			}, function(params, element) {
				  
				var strengthMessage="";
	    		
				console.log("validLength:"+validLength);
	    		if (validLength==0) {
	    			strengthMessage+="- "+msgMinLength+"<br>";
	    		}
	    		
	    		if (validTotal <3) {
	    			strengthMessage+="- <spring:message code='E00163' /><br>";
	    		}

	    
	    		return strengthMessage;
				
			});
	    	 
			function passwordRequired() {
			    return $('#password').val().length > 0;
			}
			
			$(function() {
				var $mainForm = $('#mainForm');
				
				$mainForm.validate({
					<sec:authorize access="hasPermission(#user, 512)">
					ignore : "",
					</sec:authorize>
					rules : {
						password : {
	    		  			pwcheckvalidclass: {
	    		  				depends: passwordRequired
	    		  			}
	    		  		},
						officePhoneNo : {
							digits : true
						},
						dateOfLeaving : {
							checkEndDate: $('#dateOfEntry')
						}
					},
					invalidHandler : function(event, validator) {
						
						//var errorMessage = "";
						if(validator.errorList != null) {
							/*for(var i = 0; i < validator.errorList.length; i++) {
								var name = validator.errorList[i].element.name;
								errorMessage += name + " is empty.\n";
							}
							alert(errorMessage);*/
							var errorId = validator.errorList[0].element.id;
							var $tab = $("#"+errorId).closest("div[class^=tab-pane]")
							if(!$tab.hasClass("active")) {
								var tabId = $tab.attr('id');
								var successId = validator.successList[0].id;
								var successtabId = $("#"+successId).closest("div[class^=tab-pane]").attr("id");
								$("a[href^=#"+tabId+"]").tab('show');
							}
						}
					}
				});
												
				$('.nav-tabs a').on('click', function(e) {
					$(this).tab('show');
					e.preventDefault();
				});
				
				$('.searchRoleIds').roleLookup({
					selectedIdsCallback: function(selectedIds) {
						$('#roleList > tbody').children().remove();
						
						$.ajax({
							url: "<c:url value='/userAccountManagement/StaffProfileMaintenance/roleChosen'/>",
							method: 'post',
							dataType: 'json',
							traditional: true,
							data: {ids : selectedIds},
							success: function(json){
								for(var i = 0; i < json.length; i++) {
									var id = json[i].roleId;
									var code = json[i].code;
									var description = json[i].description;
									var tbody = $('#roleList').children('tbody');
									var deleteBtn = "<td><input type='hidden' value='"+id+"' name='userRoleIds' /><a href='javascript:void(0)' onclick='deleteTableRow(this)' class='table-btn btn-delete'><span class='fa fa-times' aria-hidden='true'></span></a></td>";
									var newRow = "<tr>"
													+ "<td>" + code + "</td>"
													+ "<td>" + $.trim(description) + "</td>"
													+ deleteBtn
													+ "</tr>";
									tbody.append(newRow);
								}
								isChanged = true;
					        }
						});
					},
					alreadySelectedIdsCallback: function(){
						var ids = [];
						$("input[name=userRoleIds]").each(function(){
							ids.push(this.value)
						})
						return ids;
					}
				});
				
				$('.datepicker').datepicker();
				
				if($('input[name="staffType"]:checked').val() == 1) {
					$('input[name="batchCode"]').val('');
					$('#batchCodeDIV').hide();
				} else {
					$('#batchCodeDIV').show();
				}
				
				$('input[type="radio"][name="staffType"]').on('click', function() {
					if($('input[type="radio"][name="staffType"]:checked').val() == 1) {
						//$('#batchCodeIds').select2('');
						$('#batchCodeDIV').hide();
					} else {
						$('#batchCodeDIV').show();
					}
				});
				
				$('#batchCodeIds').select2ajax({
					placeholder: "",
					closeOnSelect: false,
					width:"100%",
					allowClear: true
				});
				
				$('#rankId').select2ajax({
					placeholder: "Select a rank",
					allowClear: true,
					width:"100%"
				});
				
				$('[name="supervisor"],.searchSupervisorId').userLookup({
					selectedIdsCallback: function(selectedIds, singleRowData) {
						var id = selectedIds[0];
						$('[name="supervisor"]').val(singleRowData.staffCode
								+ " - " + singleRowData.chineseName);
						$('[id="supervisorId"]').val(id);
						isChanged = true;
					},
					multiple: false
				});
				
				$('input, select').change(function() {
					isChanged = true;
				});
				
				$("#backBtn").on('click', function() {
					if(isChanged) {
						bootbox.confirm({
							title:"Warning",
							message: "<spring:message code='W00003' />",
							callback: function(result){
								if (result){
									window.location.href = "<c:url value='/userAccountManagement/StaffProfileMaintenance/home'/>";
								}
							}
						});
					} else {
						window.location.href = "<c:url value='/userAccountManagement/StaffProfileMaintenance/home'/>";
					}
				});
				
				
				$('.searchBatchCode').batchLookup({
					selectedIdsCallback: function(selectedIds) {
						var singleUrl = $('#batchCodeIds').data('singleUrl');
						
						$.ajax({
							url:singleUrl,
							data: {ids:selectedIds},
							type: 'post',
							dataType: 'json',
							traditional: true,
							success: function (data){
								$('#batchCodeIds').empty();
								
								for (i = 0; i < data.length; i++){
									var option = new Option(data[i].value, data[i].key);
									option.selected = true;
									$('#batchCodeIds').append(option);
								}

								$('#batchCodeIds').trigger('change');
							}
							
						})
						
					},
					alreadySelectedIdsCallback: function(){
						var ids = [];
						$('#batchCodeIds').find('option:selected').each(function(){
							ids.push(this.value);
						})
						return ids;
					},
					multiple: true
				});
				$('#batchCodeIds').hide();
				/*
				$("#mainForm").on('submit',function(){
					$("#userRoleIds").val(userRoleIds_selected.join(','));
				});
				*/
				
				<sec:authorize access="!hasPermission(#user, 512)">
					viewOnly();
				</sec:authorize>
			});
		</script>
		<style>
			.suberror {padding-left:20px; font-weight:normal}
		</style>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Staff Profile Maintenance</h1>
          <c:if test="${act != 'add'}">
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
	      	</c:if>
        </section>
        
        <section class="content">
        	<form id="mainForm" action="<c:url value='/userAccountManagement/StaffProfileMaintenance/save?act='/>${act}" method="post" role="form" >
        		<input id="userId" name="userId" value="<c:out value="${model.userId}" />" type="hidden" />
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
								<%--<a id="backBtn" class="btn btn-default" href="<c:url value='/userAccountManagement/StaffProfileMaintenance/home'/>">Back</a>--%>
								<a id="backBtn" class="btn btn-default" href="#">Back</a>
							</div>
	       					<div class="box-body">
	       						<div class="form-horizontal">
	       							<div class="col-sm-12">
	       							
	       								<div class="nav-tabs-custom">
		       								<ul class="nav nav-tabs">
		       									<sec:authorize access="hasPermission(#user, 512)">
		       										<li class="active"><a data-toogle="tab" href="#tab_1">User Account</a></li>
		       										<li><a data-toogle="tab" href="#tab_2">Profile</a></li>
		       									</sec:authorize>
		       									<sec:authorize access="!hasPermission(#user, 512)">
		       										<li class="active"><a data-toogle="tab" href="#tab_2">Profile</a></li>
	       										</sec:authorize>
		       								</ul>
			       							<div class="tab-content">
			       								<sec:authorize access="hasPermission(#user, 512)">
				       							<div id="tab_1" class="tab-pane active">
					       							<div class="form-group">
					       								<label class="col-sm-2 control-label">User Name</label>
					       								<div class="col-sm-4">
															<c:choose>
						       									<c:when test="${act eq 'add'}">
																	<input id="username" name="username" type="text" class="form-control" value="<c:out value="${model.username}" />" maxlength="255" required />
																</c:when>
																<c:otherwise>
																	<p class="form-control-static" >${model.username}</p>
																	<input name="username" value="<c:out value="${model.username}" />" type="hidden" />
																</c:otherwise>
															</c:choose>
														</div>
													</div>
					       							<div class="form-group">
					       								<label class="col-sm-2 control-label">Password</label>
					       								<div class="col-sm-5">
															<input id="password" name="password" type="password" class="form-control" value="" maxlength="255" <c:if test="${act == 'add'}">required</c:if> />
														</div>
													</div>
													<div class="clearfix">&nbsp;</div>
													<div class="box" >
														<div class="box-header with-border">
															<h3 class="box-title">Role</h3>
														</div>
														<div class="box-body">
															<table class="table table-striped table-bordered table-hover" id="roleList">
																<thead>
																	<tr>
																		<td>Code</td>
																		<td>Description</td>
																		<td></td>
																	</tr>
																</thead>
																<tbody>
																	<c:if test="${act != 'add'}">
																	<c:if test="${fn:length(model.roles) gt 0}" >
																		<c:forEach var="role" items="${model.roles}" >
																			<tr>
																				<td>${role.name}</td>
																				<td>${role.description}</td>
																				<td><input type='hidden' value='${role.roleId}' name='userRoleIds' /><a href='javascript:void(0)' onclick='deleteTableRow(this)' class='table-btn btn-delete'><span class='fa fa-times' aria-hidden='true'></span></a></td>
																			</tr>
																		</c:forEach>
																	</c:if>
																	</c:if>
																</tbody>
															</table>
														</div>
														<%-- 
														<input id="userRoleIds" name="userRoleIds" type="hidden" />
														 --%>
														<div style="margin-left: 15px;">
															<a href="javascript:void(0)" class="searchRoleIds"><span class="glyphicon glyphicon-plus"></span>&nbsp;&nbsp;Add Role</a>															
														</div>
													</div>
													<div class="clearfix">&nbsp;</div>
													<div class="col-sm-12">
														<div class="form-group">
						       								<label class="col-sm-2 control-label">Status</label>
						       								<div class="col-sm-10">
																<label class="radio-inline">
																	<input type="radio" name="status" value="Active" <c:if test="${model.status == 'Active'}">checked</c:if>> Active
																</label>
																<label class="radio-inline">
																	<input type="radio" name="status" value="Inactive" <c:if test="${model.status == 'Inactive'}">checked</c:if>> Inactive
																</label>
															</div>
														</div>
													</div>
													<div class="form-group">
					       								<label class="col-sm-2 control-label">Date of Entry</label>
					       								<div class="col-sm-2">
					       									<div class="input-group">
						       									<input id="dateOfEntry" name="dateOfEntry" type="text" class="datepicker form-control" style="min-width:200px" value="<c:out value="${commonService.formatDate(model.dateOfEntry)}" />" />
																<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
															</div>
														</div>
													</div>
													<div class="form-group">
					       								<label class="col-sm-2 control-label">Date of Leaving</label>
					       								<div class="col-sm-2">
					       									<div class="input-group">
						       									<input id="dateOfLeaving"  name="dateOfLeaving" type="text" class="datepicker form-control" style="min-width:200px" value="<c:out value="${commonService.formatDate(model.dateOfLeaving)}" />" />
																<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
															</div>
														</div>
													</div>
												</div>
												</sec:authorize>
												<sec:authorize access="hasPermission(#user, 512)">
												<div id="tab_2" class="tab-pane">
												</sec:authorize>
												<sec:authorize access="!hasPermission(#user, 512)">
												<div id="tab_2" class="tab-pane active">
												</sec:authorize>
													<div class="form-group">
					       								<label class="col-sm-2 control-label">Staff Type</label>
					       								<div class="col-sm-10">
															<label class="radio-inline">
																<input type="radio" name="staffType" value="1" <c:if test="${model.staffType == '1'}">checked</c:if>> Field
															</label>
															<label class="radio-inline">
																<input type="radio" name="staffType" value="2" <c:if test="${model.staffType == '2'}">checked</c:if>> Indoor
															</label>
														</div>
													</div>
													<div class="form-group">
					       								<label class="col-sm-2 control-label">Staff Code</label>
					       								<div class="col-sm-4">
															<input id="staffCode" name="staffCode" type="text" class="form-control" value="<c:out value="${model.staffCode}" />" maxlength="50" required />
														</div>
													</div>
													<div class="form-group">
					       								<label class="col-sm-2 control-label">English Name</label>
					       								<div class="col-sm-4">
					       									<input name="englishName" type="text" class="form-control" value="<c:out value="${model.englishName}" />" maxlength="400" />
														</div>
													</div>
													<div class="form-group">
					       								<label class="col-sm-2 control-label">Chinese Name</label>
					       								<div class="col-sm-4">
					       									<input name="chineseName" type="text" class="form-control" value="<c:out value="${model.chineseName}" />" maxlength="400" />
														</div>
													</div>
													<div class="form-group">
					       								<label class="col-sm-2 control-label">Office Phone No.</label>
					       								<div class="col-sm-4">
					       									<input name="officePhoneNo" type="text" class="form-control" value="<c:out value="${model.officePhoneNo}" />" maxlength="20" />
														</div>
													</div>
													<div class="form-group">
					       								<label class="col-sm-2 control-label">Office</label>
					       								<div class="col-sm-4">
					       									<input name="office" type="text" class="form-control" value="<c:out value="${model.office}" />" maxlength="2000" />
														</div>
													</div>
													<div class="form-group">
					       								<label class="col-sm-2 control-label">Office 2</label>
					       								<div class="col-sm-4">
					       									<input name="office2" type="text" class="form-control" value="<c:out value="${model.office2}" />" maxlength="2000" />
														</div>
													</div>
													<div class="form-group">
					       								<label class="col-sm-2 control-label">Gender</label>
					       								<div class="col-sm-10">
															<label class="radio-inline">
																<input type="radio" name="gender" value="M" <c:if test="${model.gender == 'M'}">checked</c:if>> M
															</label>
															<label class="radio-inline">
																<input type="radio" name="gender" value="F" <c:if test="${model.gender == 'F'}">checked</c:if>> F
															</label>
														</div>
													</div>
													<div class="clearfix">&nbsp;</div>
													<div id="batchCodeDIV" class="form-group">
					       								<label class="col-sm-2 control-label">Batch Code</label>
					       								<div class="col-sm-4">
					       									<div class="input-group">
																<select class="form-control select2" id="batchCodeIds" name="batchCodeIds" multiple="multiple" 
																		data-single-url="<c:url value="/userAccountManagement/StaffProfileMaintenance/getBatchCodes" />" 
																		data-ajax-url="<c:url value='/userAccountManagement/StaffProfileMaintenance/queryBatchSelect2'/>" >
																		<c:if test="${act != 'add'}">
																		<c:forEach var="modelBatchCode" items="${model.batches}">
																			<option value="<c:out value="${modelBatchCode.id}" />" selected >
																				<c:out value="${modelBatchCode.code}" />
																			</option>
																		</c:forEach>
																		</c:if>
																</select>
																<div class="input-group-addon searchBatchCode">
																	<i class="fa fa-search"></i>
																</div>
															</div>
														</div>
													</div>
													<div class="form-group">
					       								<label class="col-sm-2 control-label">Rank</label>
					       								<div class="col-sm-4">
					       									<select class="form-control selec2" id="rankId" name="rankId" 
																data-ajax-url="<c:url value='/userAccountManagement/StaffProfileMaintenance/queryRankSelect'/>" >
																<c:if test="${act != 'add'}">
																<c:if test="${model.rank != null}">
																	<option value="<c:out value="${model.rank.rankId}" />" selected>${model.rank.code} - ${model.rank.name}</option>
																</c:if>
																</c:if>
															</select>
														</div>
													</div>
													<div class="form-group">
					       								<label class="col-sm-2 control-label">Designation</label>
					       								<div class="col-sm-4">
					       									<input name="destination" type="text" class="form-control" value="<c:out value="${model.destination}" />" maxlength="255" />
														</div>
													</div>
													<div class="form-group">
					       								<label class="col-sm-2 control-label">Supervisor</label>
					       								<div class="col-sm-4">
					       									<div class="input-group">
																<input type="text" name="supervisor" class="form-control" <c:if test="${act != 'add'}">value="<c:out value="${model.supervisor.staffCode} - ${model.supervisor.chineseName}" />"</c:if> readonly>
																<input id="supervisorId" name="supervisorId" <c:if test="${act != 'add'}">value="<c:out value="${model.supervisor.id}" />"</c:if> type="hidden" />
																<div class="input-group-addon searchSupervisorId">
																	<i class="fa fa-search"></i>
																</div>
															</div>
														</div>
													</div>
													
												</div>
											</div>
										</div>
									
									</div>
								</div>
							</div>
							<sec:authorize access="hasPermission(#user, 512)">
								<div class="box-footer">
		        					<button type="submit" class="btn btn-info">Submit</button>
		        					<c:if test="${act != 'add' and model.staffType == '1'}">
		        						<a id="viewExperienceBtn" class="btn btn-info" type="button" href="<c:url value='/userAccountManagement/FieldExperienceMaintenance/edit?id=${model.userId}' />">View Experience</a>
		        						<%--
		        						<button id="viewExperienceBtn" class="btn btn-info" type="button" onclick="editFieldExperience(${model.userId})">View Experience</button>
		        						 --%>
		        					</c:if>
		       					</div>
	       					</sec:authorize>
						</div>
	        		</div>
	        	</div>
        	</form>
        </section>
	</jsp:body>
</t:layout>
