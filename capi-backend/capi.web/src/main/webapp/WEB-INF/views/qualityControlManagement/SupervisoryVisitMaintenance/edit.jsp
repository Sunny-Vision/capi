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
		
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/datejs.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
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
		<%@include file="/WEB-INF/views/includes/datepicker.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/userLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/assignmentLookupSurveyMonth.jsp" %>
		<script src="<c:url value='/resources/js/jquery.validate.custom.js'/>" ></script>
		<script>
			var supervisoryVisitDetailListLength = ${fn:length(model.supervisoryVisitDetailList)};
			
			function reorderSupervisoryVisitDetail(rowIdx) {
				var tbody = $('#supervisoryVisitDetailList').children('tbody');
				tbody.children('tr:gt(0)').each(function(){
					var tr = $(this);
					var td = tr.children('td:first');
					
					var div_FromGroup = td.children('div:nth-child(1)');
					var div = div_FromGroup.find('div.col-sm-3');
					div.each(function() {
						var name = $(this).attr('name');
						
						var curIdxStr = name.substr(3, 1);
						var curIdx = parseInt(curIdxStr);
						
						if(curIdx >= rowIdx) {
							var names = name.split('_');
							$(this).attr('name', 'div'+(curIdx-1) + '_' + names[1]);
							console.log('name = ' + $(this).attr('name'));
						}
					});
					var select = div_FromGroup.find('select');
					select.each(function() {
						var name = $(this).attr('name');
						
						var curIdxStr = name.substr(27, 1);
						var curIdx = parseInt(curIdxStr);
						
						if(curIdx >= rowIdx) {
							var names = name.split('.');
							$(this).attr('name', 'supervisoryVisitDetailList['+(curIdx-1)+'].' + names[1]);
							console.log('name = ' + $(this).attr('name'));
						}
					});
					var spanClass = div_FromGroup.find('span.select2-selection.select2-selection--single');
					spanClass.each(function() {
						var name = $(this).attr('aria-labelledby');
						
						var curIdxStr = name.substr(35, 1);
						var curIdx = parseInt(curIdxStr);
						
						if(curIdx >= rowIdx) {
							var names = name.split('.');
							$(this).attr('aria-labelledby', 'select2-supervisoryVisitDetailList['+(curIdx-1)+'].' + names[1]);
							console.log('aria-labelledby = ' + $(this).attr('aria-labelledby'));
						}
						
						// for class of select2 - aria-owns: only show when select2 is clicked, hidden -> cannot change?
						/*var name2 = $(this).attr('aria-owns');
						
						if(curIdx >= rowIdx) {
							var names2 = name2.split('.');
							$(this).attr('aria-labelledby', 'select2-supervisoryVisitDetailList['+(curIdx-1)+'].' + names2[1]);
							console.log('aria-labelledby = ' + $(this).attr('aria-labelledby'));
						}*/
					});
					var spanId = div_FromGroup.find('span.select2-selection__rendered');
					spanId.each(function() {
						var id = $(this).attr('id');
						
						var curIdxStr = id.substr(35, 1);
						var curIdx = parseInt(curIdxStr);
						
						if(curIdx >= rowIdx) {
							var ids = id.split('.');
							$(this).attr('id', 'select2-supervisoryVisitDetailList['+(curIdx-1)+'].' + ids[1]);
							console.log('id = ' + $(this).attr('id'));
						}
					});
					
					var div_FromGroup2 = div_FromGroup.next();
					var input = div_FromGroup2.find('input');
					input.each(function() {
						var name = $(this).attr('name');
						
						var curIdxStr = name.substr(27, 1);
						var curIdx = parseInt(curIdxStr);
						
						if(curIdx >= rowIdx) {
							var names = name.split('.');
							$(this).attr('name', 'supervisoryVisitDetailList['+(curIdx-1)+'].' + names[1]);
							console.log('name = ' + $(this).attr('name'));
						}
					});
					
					var td2 = td.next();
					var idInput = td2.find('input');
					idInput.each(function() {
						var name = $(this).attr('name');
						
						var curIdxStr = name.substr(27, 1);
						var curIdx = parseInt(curIdxStr);
						
						if(curIdx >= rowIdx) {
							var names = name.split('.');
							$(this).attr('name', 'supervisoryVisitDetailList['+(curIdx-1)+'].' + names[1]);
							console.log('name = ' + $(this).attr('name'));
						}
					});
					
				});
			}
			
			function deleteSupervisoryVisitDetailRow(obj) {
				bootbox.confirm({
					title:"Confirmation",
					message: "<spring:message code='W00001' />",
					callback: function(result){
						if (result){
							var deleteBtn = $(obj);
							var tr = deleteBtn.parents('tr:first');
							var rowIdx = tr.index();
							tr.remove();
							supervisoryVisitDetailListLength--;
							reorderSupervisoryVisitDetail(rowIdx);
						}
					}
				});
			}
			
			$(function() {
				
				var $mainForm = $('#mainForm');
				
				$mainForm.validate({
					rules: {
						session: {
							required: true
						},
						knowledgeOfWorkResult: {
							required: true
						},
						interviewTechniqueResult: {
							required: true
						},
						handleDifficultInterviewResult: {
							required: true
						},
						dataRecordingResult: {
							required: true
						},
						localGeographyResult: {
							required: true
						},
						mannerWithPublicResult: {
							required: true
						},
						judgmentResult: {
							required: true
						},
						organizationOfWorkResult: {
							required: true
						},
						otherResult: {
							required: true
						}
					}
				});
				
				for(var i = 0; i < supervisoryVisitDetailListLength; i++) {
					$("input[name='supervisoryVisitDetailList["+i+"].result'").rules("add", "required");
					
					var value = $("input[name='supervisoryVisitDetailList["+i+"].result'").val()
					if(value = "3") {
						$("[name='supervisoryVisitDetailList["+i+"].otherRemark']").attr("disabled", false);
					} else {
						$("[name='supervisoryVisitDetailList["+i+"].otherRemark']").attr("disabled", true);
					}
				}
				
				$("input[name$='result'").on("change", function() {
					var value = $(this).val();
					if(value == "3") {
						$(this).parents("tr").find("[name$='otherRemark']").attr("disabled", false);
					} else {
						$(this).parents("tr").find("[name$='otherRemark']").attr("disabled", true);
						$(this).parents("tr").find("[name$='otherRemark']").val('');
					}
				});
				
				Datepicker();
				
				$(".timepicker").timepicker({
					showInputs: false,
					showMeridian: false,
					defaultTime: false,
					minuteStep: 1
		        });
				
				$(".select2-addable").select2({
					 	tags: true,
					 	multiple: false,
				});
				
				$('.select2ajax').select2ajax({
					allowClear: true,
					placeholder: '',
					width:'100%',
					multiple: false,
					closeOnSelect: true,
					tags: true
				});
				
				$('.select2ajax').hide();
				
				// check if less than 3 rows, add more rows until 3 rows
				/*for(var i = spotCheckPhoneCallListLength; i < 3; i++) {
					var tbody = $('#spotCheckPhoneCallList').children('tbody');
					var deleteBtn = "<td><input type='hidden' value='' name='spotCheckPhoneCallList["+spotCheckPhoneCallListLength+"].spotCheckPhoneCallId' /><a href='javascript:void(0)' onclick='deleteSpotCheckPhoneCallRow(this)' class='table-btn btn-delete'><span class='fa fa-times' aria-hidden='true'></span></a></td>";
					var newRow = "<tr>"
									+ "<td>" + "<input type='text' value='' name='spotCheckPhoneCallList["+spotCheckPhoneCallListLength+"].phoneCallTime' />" + "</td>"
									+ "<td>" + "<input type='radio' name='spotCheckPhoneCallList["+spotCheckPhoneCallListLength+"].phoneCallResult' value='1' />" + "</td>"
									+ "<td>" + "<input type='radio' name='spotCheckPhoneCallList["+spotCheckPhoneCallListLength+"].phoneCallResult' value='2' />" + "</td>"
									+ "<td>" + "<input type='radio' name='spotCheckPhoneCallList["+spotCheckPhoneCallListLength+"].phoneCallResult' value='3' />" + "</td>"
									+ deleteBtn
									+ "</tr>";
					tbody.append(newRow);
					
					$("input[name='spotCheckPhoneCallList["+spotCheckPhoneCallListLength+"].phoneCallTime'").rules("add", "required");
					$("input[name='spotCheckPhoneCallList["+spotCheckPhoneCallListLength+"].phoneCallResult'").rules("add", "required");
					
					spotCheckPhoneCallListLength++;
				}*/
				
				// handle add supervisory visit detail
				$('.addSupervisoryVisitDetail').on('click', function() {
					if(supervisoryVisitDetailListLength < 9) {
						var tbody = $('#supervisoryVisitDetailList').children('tbody');
						
						var deleteBtn = "<td><input type='hidden' value='' name='supervisoryVisitDetailList["+supervisoryVisitDetailListLength+"].supervisoryVisitDetailId' />"
							+	"<a href='javascript:void(0)' onclick='deleteSupervisoryVisitDetailRow(this)' class='table-btn btn-delete'><span class='fa fa-times' aria-hidden='true'></span></a></td>";
						
						var newAssignmentSelectScript = 
							"<select name='supervisoryVisitDetailList["+supervisoryVisitDetailListLength+"].assignmentId' class='form-control select2ajax' "
							+	"data-ajax-url='<c:url value="/qualityControlManagement/SupervisoryVisitMaintenance/queryAssignmentWithSurveyMonthSelect2"/>?surveyMonthId=${model.surveyMonthId}&fieldOfficerId=${model.fieldOfficerId}' "
							+	"data-single-url='<c:url value="/qualityControlManagement/SupervisoryVisitMaintenance/getAssignmentWithSurveyMonthById"/>' required >"
							+ 			"</select>";
											
						var newAssignmentSelect = $(newAssignmentSelectScript);
						
						var newAssignmentLookupScript = "<div class='input-group-addon searchAssignmentId_"+supervisoryVisitDetailListLength+"'>"
											+ 				"<i class='fa fa-search'></i>"
											+ 			"</div>";
		
						var newAssignmentLookup = $(newAssignmentLookupScript);
						
						var newSurveySelectScript = "<select class='form-control select2-addable col-sm-2' name='supervisoryVisitDetailList["+supervisoryVisitDetailListLength+"].survey' required >"
										+ 				"<option value=''></option>"
										+ 					"<c:forEach items='${model.detailSurveyList}' var='survey'>"
										+ 						"<option value='<c:out value='${survey}' />' >${survey}</option>"
										+ 					"</c:forEach>"
										+ 			"</select>";
						
						var newSurveySelect = $(newSurveySelectScript);
						
						var newRow = "<tr>"
										+ "<td>"
										+ 	"<div class='form-group'>"
       									+ 		"<label class='col-sm-2 control-label'>Assignment:</label>"
       									+ 		"<div class='col-sm-3' name='div"+supervisoryVisitDetailListLength+"_1'>"
										+			"<div class='input-group'>"
										//+ 				newAssignmentSelectScript
										+ 			"</div>"
										+ 		"</div>"
										+		"<label class='col-sm-2 control-label'>Survey:</label>"
       									+ 		"<div class='col-sm-3' name='div"+supervisoryVisitDetailListLength+"_2'>"
										//+ 			newSurveySelectScript
										+ 		"</div>"
       									+ 	"</div>"
       									+ 	"<div class='form-group'>"
										+ 		"<div class='col-sm-12'>"
										+ 			"<div class='col-sm-2'>"
										+ 				"<label class='radio-inline'>"
										+ 					"<input type='radio' name='supervisoryVisitDetailList["+supervisoryVisitDetailListLength+"].result' value='1' > Enumerated"
										+ 				"</label>"
										+ 			"</div>"
										+ 			"<div class='col-sm-2'>"
										+ 				"<label class='radio-inline'>"
										+ 					"<input type='radio' name='supervisoryVisitDetailList["+supervisoryVisitDetailListLength+"].result' value='2' > Non-contacted"
										+ 				"</label>"
										+ 			"</div>"
										+ 			"<div class='col-sm-2'>"
										+ 				"<label class='radio-inline'>"
										+ 					"<input type='radio' name='supervisoryVisitDetailList["+supervisoryVisitDetailListLength+"].result' value='3' > Others"
										+ 				"</label>"
										+ 			"</div>"
										+ 			"<div class='col-sm-6'>"
			       						+ 				"<input name='supervisoryVisitDetailList["+supervisoryVisitDetailListLength+"].otherRemark' type='text' class='form-control' value='' maxlength='4000' disabled />"
										+ 			"</div>"
										+ 		"</div>"
       									+ 	"</div>"
										+ "</td>"
										+ deleteBtn
										+ "</tr>";
						tbody.append(newRow);
						
						tbody.find("[name='div" +supervisoryVisitDetailListLength+"_1']").find("[class='input-group']").append(newAssignmentSelect);
						tbody.find("[name='div" +supervisoryVisitDetailListLength+"_1']").find("[class='input-group']").append(newAssignmentLookup);
						tbody.find("[name='div" +supervisoryVisitDetailListLength+"_2']").append(newSurveySelect);
						
						$("input[name='supervisoryVisitDetailList["+supervisoryVisitDetailListLength+"].result']").on("click", function() {
							var rowNo = $(this).attr('name').substr(27, 1);
							var value = $(this).val();
							if(value == "3") {
								$(this).parents("tr").find("[name='supervisoryVisitDetailList["+rowNo+"].otherRemark']").attr("disabled", false);
							} else {
								$(this).parents("tr").find("[name='supervisoryVisitDetailList["+rowNo+"].otherRemark']").attr("disabled", true);
							}
						});
						
						/*newAssignmentSelect.select2({
							 	tags: true,
							 	multiple: false,
						});*/
						newAssignmentSelect.select2ajax({
							allowClear: true,
							placeholder: '',
							width:'100%',
							multiple: false,
							closeOnSelect: true,
							tags: true
						});
						newAssignmentSelect.hide();
						
						newSurveySelect.select2({
							 	tags: true,
							 	multiple: false,
						});
						
						newAssignmentLookup.assignmentLookupSurveyMonth({
							selectedIdsCallback: function(selectedIds, singleRowData) {
								var id = selectedIds[0];
								console.log('assignmentId = ' + id);
							},
							queryDataCallback: function(model) {
								model.surveyMonthId = $('[name="surveyMonthId"]').val();
								model.officerId = $("[name='fieldOfficerId']").val();
							},
							multiple: false
						}, $("[name='supervisoryVisitDetailList["+supervisoryVisitDetailListLength+"].assignmentId']"));
						
						$("input[name='supervisoryVisitDetailList["+supervisoryVisitDetailListLength+"].result'").rules("add", "required");
						
						supervisoryVisitDetailListLength++;
					}
				});
				
				$('[name="submitTo"],.searchSubmitToId').userLookup({
					selectedIdsCallback: function(selectedIds, singleRowData) {
						var id = selectedIds[0];
						$('[name="submitTo"]').val(singleRowData.staffCode + " - "
								+ singleRowData.chineseName);
						$('[id="submitToId"]').val(id);
					},
					queryDataCallback: function(model) {
						model.authorityLevel = (1 | 2);
					},
					multiple: false
				});
				
				for(var i = 0; i < supervisoryVisitDetailListLength; i ++) {
					$('.searchAssignmentId_' + i).assignmentLookupSurveyMonth({
						selectedIdsCallback: function(selectedIds, singleRowData) {
							var id = selectedIds[0];
							console.log('assignmentId = ' + id);
						},
						queryDataCallback: function(model) {
							model.surveyMonthId = $('[name="surveyMonthId"]').val();
							model.officerId = $("[name='fieldOfficerId']").val();
						},
						multiple: false
					}, $('[name="supervisoryVisitDetailList['+i+'].assignmentId"]') );
				}
				
				<sec:authorize access="!(hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256))">
					viewOnly();
				</sec:authorize>
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Supervisory Visit Maintenance</h1>
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
        	<form id="mainForm" action="<c:url value='/qualityControlManagement/SupervisoryVisitMaintenance/save'/>" method="post" role="form">
        		<input name="supervisoryVisitFormId" value="<c:out value="${model.supervisoryVisitFormId}" />" type="hidden" />
        		<input name="surveyMonthId" value="<c:out value="${model.surveyMonthId}" />" type="hidden" />
        		<input name="fieldOfficerId" value="<c:out value="${model.fieldOfficerId}" />" type="hidden" />
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/qualityControlManagement/SupervisoryVisitMaintenance/home'/>">Back</a>
							</div>
	       					<div class="box-body">
	       						<div class="form-horizontal">
	       							<div class="form-group">
										<label class="col-sm-2 control-label"></label>
										<div class="col-sm-2 pull-right">
											<label class="radio-inline">
												<input type="radio" name="session" value="1" <c:if test="${model.session == '1'}">checked</c:if>> Day
											</label>
											<label class="radio-inline">
												<input type="radio" name="session" value="2" <c:if test="${model.session == '2'}">checked</c:if>> Night
											</label>
											<p id="sessionRequired"></p>
										</div>
										<label class="col-sm-2 control-label pull-right"></label>
										<br style="clear:both" />
									</div>
	       							<div class="form-group">
										<label class="col-sm-2 control-label">Field Officer:</label>
										<p class="col-sm-2 control-label">${model.fieldOfficer}</p>
										<label class="col-sm-3 control-label">Post:</label>
										<p class="col-sm-2 control-label">${model.fieldOfficerPost}</p>
									</div>
									<div class="form-group">
										<label class="col-sm-2 control-label">Supervisor:</label>
										<p class="col-sm-2 control-label">${model.supervisor}</p>
										<label class="col-sm-3 control-label">Post:</label>
										<p class="col-sm-2 control-label">${model.supervisorPost}</p>
									</div>
									<br/><br/>
									<div class="form-group">
										<label class="col-sm-3 control-label">Date / Time of supervisory visit:</label>
										<div class="col-sm-3">
											<div class="input-group">
												<input type="text" name="visitDate" class="form-control date-picker" maxlength="10" value="<c:out value="${model.visitDate}" />" required >
												<div class="input-group-addon">
													<i class="fa fa-calendar"></i>
												</div>
											</div>
										</div>
									</div>
									<div class="form-group">
										<label class="col-sm-3 control-label">From:</label>
										<div class="col-sm-2">
											<div class="input-group bootstrap-timepicker">
												<input name="fromTime" type="text" class="form-control timepicker" value="<c:out value="${model.fromTime}" />" required />
												<div class="input-group-addon">
													<i class="fa fa-clock-o"></i>
												</div>
											</div>
										</div>
										<label class="col-sm-2 control-label">To:</label>
										<div class="col-sm-2">
											<div class="input-group bootstrap-timepicker">
												<input name="toTime" type="text" class="form-control timepicker" value="<c:out value="${model.toTime}" />" required />
												<div class="input-group-addon">
													<i class="fa fa-clock-o"></i>
												</div>
											</div>
										</div>
										<div class="col-sm-1">(Time)</div>
									</div>
									<c:if test="${not empty model.rejectReason}">
										<div class="form-group">
		       								<label class="col-sm-2 control-label">Reject Reason: </label>
		       								<p class="col-sm-5 control-label" style="text-align:left;">${model.rejectReason}</p>
										</div>
									</c:if>
									<div class="form-group">
	       								<label class="col-sm-4 control-label" style="text-align: left;">A. Details of establishments / living quarters visited</label>
									</div>
									<div class="col-sm-12">
		       							<div class="box" style="padding-top:5px;">
		       								
		       								<div class="box-body">
												<table class="table table-striped table-bordered table-hover" id="supervisoryVisitDetailList">
													<thead>
														<tr>
															<th></th>
															<th class="text-center action"></th>
														</tr>
													</thead>
													<tbody>
														<c:if test="${fn:length(model.supervisoryVisitDetailList) gt 0}" >
															<c:forEach var="supervisoryVisitDetail" items="${model.supervisoryVisitDetailList}" varStatus="status">
																<tr>
																	<td>
																		<div class="form-group">
									       									<label class="col-sm-2 control-label">Assignment:</label>
									       									<div class="col-sm-3 " name="div${status.index}_1">
																				<div class="input-group">
																					<%--<select class="form-control select2-addable col-sm-2" name="supervisoryVisitDetailList[${status.index}].assignmentId" required>
																						<option value=""></option>
																						<c:forEach items="${model.detailAssignmentIdList}" var="detailAssignmentId">
																							<option value="<c:out value="${detailAssignmentId.assignmentId}" />" <c:if test="${supervisoryVisitDetail.rowNumber == (status.index+1) && supervisoryVisitDetail.assignmentId != null && supervisoryVisitDetail.assignmentId == detailAssignmentId.assignmentId}">selected</c:if>>${detailAssignmentId.referenceNo}</option>
																						</c:forEach>
																					</select>--%>
																					<select name="supervisoryVisitDetailList[${status.index}].assignmentId" class="form-control select2ajax"
																						data-ajax-url="<c:url value='/qualityControlManagement/SupervisoryVisitMaintenance/queryAssignmentWithSurveyMonthSelect2'/>?surveyMonthId=${model.surveyMonthId}&fieldOfficerId=${model.fieldOfficerId}"
																						data-single-url="<c:url value='/qualityControlManagement/SupervisoryVisitMaintenance/getAssignmentWithSurveyMonthById'/>" required >
																							<c:if test="${supervisoryVisitDetail.rowNumber == (status.index+1) && supervisoryVisitDetail.assignmentId != null}" >
																								<option value="<c:out value="${supervisoryVisitDetail.assignmentId}" />" selected >${supervisoryVisitDetail.referenceNo}</option>
																							</c:if>
																					</select>
																					<div class="input-group-addon searchAssignmentId_${status.index}">
																						<i class="fa fa-search"></i>
																					</div>
																				</div>
																			</div>
																			<label class="col-sm-2 control-label">Survey:</label>
									       									<div class="col-sm-3"name="div${status.index}_2">
										       									<select class="form-control select2-addable col-sm-2" name="supervisoryVisitDetailList[${status.index}].survey" required>
																					<option value=""></option>
																					<c:set var="varCheck" value="false" />
																					<c:forEach items="${model.detailSurveyList}" var="survey">
																						<option value="<c:out value="${survey}" />" 
																							<c:if test="${supervisoryVisitDetail.rowNumber == (status.index+1) && supervisoryVisitDetail.survey != null && supervisoryVisitDetail.survey == survey}">
																								<c:set var="varCheck" value="true" />
																								selected
																							</c:if>>
																							${survey}
																						</option>
																					</c:forEach>
																					<c:if test="${(not varCheck) && supervisoryVisitDetail.survey != null}">
																						<option value="<c:out value="${supervisoryVisitDetail.survey}" />" selected >${supervisoryVisitDetail.survey}</option>
																					</c:if>
																				</select>
																			</div>
									       								</div>
									       								<div class="form-group">
																			<div class="col-sm-12">
																				<c:if test="${supervisoryVisitDetail.rowNumber == (status.index + 1)}">
																					<div class="col-sm-2">
																						<label class="radio-inline">
																							<input type="radio" name="supervisoryVisitDetailList[${status.index}].result" value="1" <c:if test="${supervisoryVisitDetail.result == '1'}">checked</c:if>> Enumerated
																						</label>
																					</div>
																					<div class="col-sm-2">
																						<label class="radio-inline">
																							<input type="radio" name="supervisoryVisitDetailList[${status.index}].result" value="2" <c:if test="${supervisoryVisitDetail.result == '2'}">checked</c:if>> Non-contacted
																						</label>
																					</div>
																					<div class="col-sm-2">
																						<label class="radio-inline">
																							<input type="radio" name="supervisoryVisitDetailList[${status.index}].result" value="3" <c:if test="${supervisoryVisitDetail.result == '3'}">checked</c:if>> Others
																						</label>
																					</div>
																					<div class="col-sm-6">
												       									<input name="supervisoryVisitDetailList[${status.index}].otherRemark" type="text" class="form-control" value="<c:out value="${supervisoryVisitDetail.otherRemark}" />" maxlength="4000" disabled />
																					</div>
																				</c:if>
																			</div>
									       								</div>
																	</td>
																	<td>
																		<input type="hidden" value="<c:out value="${supervisoryVisitDetail.supervisoryVisitDetailId}" />" name="supervisoryVisitDetailList[${status.index}].supervisoryVisitDetailId" />
																		<c:if test="${status.index > 0}">
																			<a href="javascript:void(0)" onclick="deleteSupervisoryVisitDetailRow(this)" class="table-btn btn-delete">
																			<span class="fa fa-times" aria-hidden="true"></span></a>
																		</c:if>
																	</td>
																</tr>
															</c:forEach>
														</c:if>
													</tbody>
												</table>
											</div>
											<sec:authorize access="hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)">
												<div style="margin-left: 15px;">
													<a href="javascript:void(0)" class="addSupervisoryVisitDetail"><span class="glyphicon glyphicon-plus"></span>&nbsp;&nbsp;Add</a>															
												</div>
											</sec:authorize>
		       								
		       						</div>
		       						<div class="form-group">
	       								<label class="col-sm-4 control-label" style="text-align: left;">B. Record of discussion after supervisory visit</label>
									</div>
									<div class="form-group">
										<label class="col-sm-1 control-label">Date:</label>
	       								<div class="col-sm-3">
											<div class="input-group">
												<input type="text" name="discussionDate" class="form-control date-picker" maxlength="10" value="<c:out value="${model.discussionDate}" />" required >
												<div class="input-group-addon">
													<i class="fa fa-calendar"></i>
												</div>
											</div>
										</div>
									</div>
									<div class="form-group">
										<label class="col-sm-1 control-label">Remark:</label>
										<div class="col-sm-4">
											<input name="remark" type="text" class="form-control" value="<c:out value="${model.remark}" />" maxlength="4000" />
										</div>
									</div>
									<div class="form-group">
	       								<label class="col-sm-6 control-label" style="text-align: left;">C. Observations on the field officer's work performance</label>
									</div>
									<div class="form-group">
	       								<label class="col-sm-3 control-label">1. Knowledge of work</label>
									</div>
									<div class="col-sm-12">
										<div class="box" style="padding:1px;">
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
			       								<label class="radio-inline">
													<input type="radio" name="knowledgeOfWorkResult" value="1" <c:if test="${model.knowledgeOfWorkResult == '1'}">checked</c:if>> Merits observed / Areas for attention / Other remarks
												</label>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<div class="input-group col-sm-5">
													<input name="knowledgeOfWorkRemark" type="text" class="form-control" value="<c:out value="${model.knowledgeOfWorkRemark}" />" maxlength="4000" />
												</div>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<label class="radio-inline">
													<input type="radio" name="knowledgeOfWorkResult" value="2" <c:if test="${model.knowledgeOfWorkResult == '2'}">checked</c:if>> No comment
												</label>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<label class="radio-inline">
													<input type="radio" name="knowledgeOfWorkResult" value="3" <c:if test="${model.knowledgeOfWorkResult == '3'}">checked</c:if>> N / A
												</label>
											</div>
	       								</div>
	       							</div>
	       							<div class="form-group">
	       								<label class="col-sm-3 control-label">2. Interviewing techniques</label>
									</div>
									<div class="col-sm-12">
										<div class="box" style="padding:1px;">
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
			       								<label class="radio-inline">
													<input type="radio" name="interviewTechniqueResult" value="1" <c:if test="${model.interviewTechniqueResult == '1'}">checked</c:if>> Merits observed / Areas for attention / Other remarks
												</label>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<div class="input-group col-sm-5">
													<input name="interviewTechniqueRemark" type="text" class="form-control" value="<c:out value="${model.interviewTechniqueRemark}" />" maxlength="4000" />
												</div>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<label class="radio-inline">
													<input type="radio" name="interviewTechniqueResult" value="2" <c:if test="${model.interviewTechniqueResult == '2'}">checked</c:if>> No comment
												</label>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<label class="radio-inline">
													<input type="radio" name="interviewTechniqueResult" value="3" <c:if test="${model.interviewTechniqueResult == '3'}">checked</c:if>> N / A
												</label>
											</div>
	       								</div>
	       							</div>
	       							<div class="form-group">
	       								<label class="col-sm-4 control-label">3. Tact in handling difficult interviews</label>
									</div>
									<div class="col-sm-12">
										<div class="box" style="padding:1px;">
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
			       								<label class="radio-inline">
													<input type="radio" name="handleDifficultInterviewResult" value="1" <c:if test="${model.handleDifficultInterviewResult == '1'}">checked</c:if>> Merits observed / Areas for attention / Other remarks
												</label>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<div class="input-group col-sm-5">
													<input name="handleDifficultInterviewRemark" type="text" class="form-control" value="<c:out value="${model.handleDifficultInterviewRemark}" />" maxlength="4000" />
												</div>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<label class="radio-inline">
													<input type="radio" name="handleDifficultInterviewResult" value="2" <c:if test="${model.handleDifficultInterviewResult == '2'}">checked</c:if>> No comment
												</label>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<label class="radio-inline">
													<input type="radio" name="handleDifficultInterviewResult" value="3" <c:if test="${model.handleDifficultInterviewResult == '3'}">checked</c:if>> N / A
												</label>
											</div>
	       								</div>
	       							</div>
	       							<div class="form-group">
	       								<label class="col-sm-3 control-label">4. Data recording / Map reading</label>
									</div>
									<div class="col-sm-12">
										<div class="box" style="padding:1px;">
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
			       								<label class="radio-inline">
													<input type="radio" name="dataRecordingResult" value="1" <c:if test="${model.dataRecordingResult == '1'}">checked</c:if>> Merits observed / Areas for attention / Other remarks
												</label>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<div class="input-group col-sm-5">
													<input name="dataRecordingRemark" type="text" class="form-control" value="<c:out value="${model.dataRecordingRemark}" />" maxlength="4000" />
												</div>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<label class="radio-inline">
													<input type="radio" name="dataRecordingResult" value="2" <c:if test="${model.dataRecordingResult == '2'}">checked</c:if>> No comment
												</label>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<label class="radio-inline">
													<input type="radio" name="dataRecordingResult" value="3" <c:if test="${model.dataRecordingResult == '3'}">checked</c:if>> N / A
												</label>
											</div>
	       								</div>
	       							</div>
	       							<div class="form-group">
	       								<label class="col-sm-3 control-label">5. Knowledge of local geography</label>
									</div>
									<div class="col-sm-12">
										<div class="box" style="padding:1px;">
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
			       								<label class="radio-inline">
													<input type="radio" name="localGeographyResult" value="1" <c:if test="${model.localGeographyResult == '1'}">checked</c:if>> Merits observed / Areas for attention / Other remarks
												</label>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<div class="input-group col-sm-5">
													<input name="localGeographyRemark" type="text" class="form-control" value="<c:out value="${model.localGeographyRemark}" />" maxlength="4000" />
												</div>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<label class="radio-inline">
													<input type="radio" name="localGeographyResult" value="2" <c:if test="${model.localGeographyResult == '2'}">checked</c:if>> No comment
												</label>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<label class="radio-inline">
													<input type="radio" name="localGeographyResult" value="3" <c:if test="${model.localGeographyResult == '3'}">checked</c:if>> N / A
												</label>
											</div>
	       								</div>
	       							</div>
	       							<div class="form-group">
	       								<label class="col-sm-3 control-label">6. Manner with the public, attire</label>
									</div>
									<div class="col-sm-12">
										<div class="box" style="padding:1px;">
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
			       								<label class="radio-inline">
													<input type="radio" name="mannerWithPublicResult" value="1" <c:if test="${model.mannerWithPublicResult == '1'}">checked</c:if>> Merits observed / Areas for attention / Other remarks
												</label>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<div class="input-group col-sm-5">
													<input name="mannerWithPublicRemark" type="text" class="form-control" value="<c:out value="${model.mannerWithPublicRemark}" />" maxlength="4000" />
												</div>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<label class="radio-inline">
													<input type="radio" name="mannerWithPublicResult" value="2" <c:if test="${model.mannerWithPublicResult == '2'}">checked</c:if>> No comment
												</label>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<label class="radio-inline">
													<input type="radio" name="mannerWithPublicResult" value="3" <c:if test="${model.mannerWithPublicResult == '3'}">checked</c:if>> N / A
												</label>
											</div>
	       								</div>
	       							</div>
	       							<div class="form-group">
	       								<label class="col-sm-2 control-label">7. Judgment</label>
									</div>
									<div class="col-sm-12">
										<div class="box" style="padding:1px;">
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
			       								<label class="radio-inline">
													<input type="radio" name="judgmentResult" value="1" <c:if test="${model.judgmentResult == '1'}">checked</c:if>> Merits observed / Areas for attention / Other remarks
												</label>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<div class="input-group col-sm-5">
													<input name="judgmentRemark" type="text" class="form-control" value="<c:out value="${model.judgmentRemark}" />" maxlength="4000" />
												</div>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<label class="radio-inline">
													<input type="radio" name="judgmentResult" value="2" <c:if test="${model.judgmentResult == '2'}">checked</c:if>> No comment
												</label>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<label class="radio-inline">
													<input type="radio" name="judgmentResult" value="3" <c:if test="${model.judgmentResult == '3'}">checked</c:if>> N / A
												</label>
											</div>
	       								</div>
	       							</div>
	       							<div class="form-group">
	       								<label class="col-sm-3 control-label">8. Organization of work</label>
									</div>
									<div class="col-sm-12">
										<div class="box" style="padding:1px;">
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
			       								<label class="radio-inline">
													<input type="radio" name="organizationOfWorkResult" value="1" <c:if test="${model.organizationOfWorkResult == '1'}">checked</c:if>> Merits observed / Areas for attention / Other remarks
												</label>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<div class="input-group col-sm-5">
													<input name="organizationOfWorkRemark" type="text" class="form-control" value="<c:out value="${model.organizationOfWorkRemark}" />" maxlength="4000" />
												</div>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<label class="radio-inline">
													<input type="radio" name="organizationOfWorkResult" value="2" <c:if test="${model.organizationOfWorkResult == '2'}">checked</c:if>> No comment
												</label>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<label class="radio-inline">
													<input type="radio" name="organizationOfWorkResult" value="3" <c:if test="${model.organizationOfWorkResult == '3'}">checked</c:if>> N / A
												</label>
											</div>
	       								</div>
	       							</div>
	       							<div class="form-group">
	       								<label class="col-sm-2 control-label">9. Others</label>
									</div>
									<div class="col-sm-12">
										<div class="box" style="padding:1px;">
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
			       								<label class="radio-inline">
													<input type="radio" name="otherResult" value="1" <c:if test="${model.otherResult == '1'}">checked</c:if>> Merits observed / Areas for attention / Other remarks
												</label>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<div class="input-group col-sm-5">
													<input name="otherRemark" type="text" class="form-control" value="<c:out value="${model.otherRemark}" />" maxlength="4000" />
												</div>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<label class="radio-inline">
													<input type="radio" name="otherResult" value="2" <c:if test="${model.otherResult == '2'}">checked</c:if>> No comment
												</label>
											</div>
											<div class="form-group">
												<label class="col-sm-1 control-label"></label>
												<label class="radio-inline">
													<input type="radio" name="otherResult" value="3" <c:if test="${model.otherResult == '3'}">checked</c:if>> N / A
												</label>
											</div>
	       								</div>
	       							</div>
	       							<hr/>
	       							<div class="form-group">
	       								<label class="col-sm-2 control-label">Submit To: </label>
	       								<div class="col-sm-3">
											<div class="input-group">
												<input name="submitTo" type="text" class="form-control" value="<c:out value="${model.submitTo}" />" required readonly />
												<input id="submitToId" name="submitToId" value="<c:out value="${model.submitToId}" />" type="hidden" />
												<div class="input-group-addon searchSubmitToId">
													<i class="fa fa-search"></i>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
	       					<sec:authorize access="hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)">
								<div class="box-footer">
									<c:if test="${!(model.status == 'Approved' || model.status == 'Submitted')}">
										<div class="col-md-2">
											<button type="submit" class="btn btn-info" name="submitBtn" value="save">Save</button>
										</div>
									</c:if>
									<div class="col-md-2">
	        							<button type="submit" class="btn btn-info" name="submitBtn" value="submit">Submit</button>
	        						</div>
	       						</div>
	       					</sec:authorize>
						</div>
	        		</div>
	        	</div>
	        	</div>
        	</form>
        </section>
	</jsp:body>
</t:layout>
