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
			#spotCheckPhoneCallList > tbody > tr > td {
				text-align: center;
			}
		</style>
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/timepicker.jsp" %>		
		<%@include file="/WEB-INF/views/includes/datepicker.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/userLookup.jsp" %>
		<script src="<c:url value='/resources/js/jquery.validate.custom.js'/>" ></script>
		<script>
			var spotCheckPhoneCallListLength = ${fn:length(model.spotCheckPhoneCallList)};
			var spotCheckResultListLength = ${fn:length(model.spotCheckResultList)};
			
			function reorderSpotCheckPhoneCall(rowIdx) {
				var tbody = $('#spotCheckPhoneCallList').children('tbody');
				tbody.children('tr:gt(0)').each(function(){
					var tr = $(this);
					var td = tr.children('td:first');
					var td0 = td.contents();
					//var td1 = td0.contents();
					var td1 = td0.find('input');
					var curIdxStr = td1.attr('name').substr(23, 1);
					var curIdx = parseInt(curIdxStr);
					if(curIdx > rowIdx) {
						td1.attr('name', 'spotCheckPhoneCallList['+(curIdx-1)+'].phoneCallTime');
						var td2 = td.next().contents();
						td2.attr('name', 'spotCheckPhoneCallList['+(curIdx-1)+'].phoneCallResult');
						var td3 = td.next().next().contents();
						td3.attr('name', 'spotCheckPhoneCallList['+(curIdx-1)+'].phoneCallResult');
						var td4 = td.next().next().next().contents();
						td4.attr('name', 'spotCheckPhoneCallList['+(curIdx-1)+'].phoneCallResult');
						//var td5 = td.next().next().next().next().contents();
						//td5[0].name = 'spotCheckPhoneCallList['+(curIdx-1)+'].spotCheckPhoneCallId';
						var td5 = td.next().next().next().next().find('input');
						td5.attr('name', 'spotCheckPhoneCallList['+(curIdx-1)+'].spotCheckPhoneCallId');
					}
				});
			}
			
			function reorderSpotCheckResult(rowIdx) {
				var tbody = $('#spotCheckResultList').children('tbody');
				tbody.children('tr:gt(0)').each(function(){
					var tr = $(this);
					var td = tr.children('td:first');
					var td1 = td.contents().children();
					var curIdxStr = td1.attr('name').substr(20, 1);
					var curIdx = parseInt(curIdxStr);
					if(curIdx > rowIdx) {
						td1.attr('name', 'spotCheckResultList['+(curIdx-1)+'].result');
						var td2 = td.next().contents().children();
						td2.attr('name', 'spotCheckResultList['+(curIdx-1)+'].result');
						var td3 = td.next().next().contents().children();
						td3.attr('name', 'spotCheckResultList['+(curIdx-1)+'].result');
						var td4 = td.next().next().next().contents().children();
						td4.attr('name', 'spotCheckResultList['+(curIdx-1)+'].result');
						var td5 = td.next().next().next().next().contents().children();
						td5.attr('name', 'spotCheckResultList['+(curIdx-1)+'].result');
						var td6 = td.next().next().next().next().next().contents().children();
						td6.attr('name', 'spotCheckResultList['+(curIdx-1)+'].result');
						var td7 = td.next().next().next().next().next().next().contents().children();
						td7.attr('name', 'spotCheckResultList['+(curIdx-1)+'].result');
						var td8 = td.next().next().next().next().next().next().next().contents().children();
						td8.attr('name', 'spotCheckResultList['+(curIdx-1)+'].result');
						var td9 = td.next().next().next().next().next().next().next().next().contents();
						td9.attr('name', 'spotCheckResultList['+(curIdx-1)+'].otherRemark');
						var td10 = td.next().next().next().next().next().next().next().next().next().contents();
						td10.attr('name', 'spotCheckResultList['+(curIdx-1)+'].referenceNo');
						var td11 = td.next().next().next().next().next().next().next().next().next().next().contents();
						td11.attr('name', 'spotCheckResultList['+(curIdx-1)+'].spotCheckResultId');
					}
				});
			}
			
			function deleteSpotCheckPhoneCallRow(obj) {
				bootbox.confirm({
					title:"Confirmation",
					message: "<spring:message code='W00001' />",
					callback: function(result){
						if (result){
							var deleteBtn = $(obj);
							var tr = deleteBtn.parents('tr:first');
							var rowIdx = tr.index();
							tr.remove();
							spotCheckPhoneCallListLength--;
							reorderSpotCheckPhoneCall(rowIdx);
						}
					}
				});
			}
			
			function deleteSpotCheckResultRow(obj) {
				bootbox.confirm({
					title:"Confirmation",
					message: "<spring:message code='W00001' />",
					callback: function(result){
						if (result){
							var deleteBtn = $(obj);
							var tr = deleteBtn.parents('tr:first');
							var rowIdx = tr.index();
							tr.remove();
							spotCheckResultListLength--;
							reorderSpotCheckResult(rowIdx);
						}
					}
				});
			}
			
			$(function() {
				
				var status;
				var $mainForm = $('#mainForm');
				
				$("[name='submitBtn']").on("click", function(){
					status = $(this).val();
				});
				
				$mainForm.validate({
					rules: {
						timeCallback: {
							conditionRequired: function() {
								return status == 'submit';
							}
						},
						activityBeingPerformed: {
							conditionRequired: function() {
								return status == 'submit';
							}
						},
						irregular: {
							conditionRequired: function() {
								return status == 'submit';
							}
						},
						successful: {
							conditionRequired: function() {
								return status == 'submit';
							}
						},
						verCheck1IsIrregular: {
							conditionRequired: function() {
								return $("#verCheck1RefenceNo").val() != "" && status == 'submit';
							}
						},
						verCheck2IsIrregular: {
							conditionRequired: function() {
								return $("#verCheck2RefenceNo").val() != "" && status == 'submit';
							}
						},
						session: {
							conditionRequired: function() {
								return status == 'submit';
							}
						},
						survey: {
							conditionRequired: function() {
								return status == 'submit';
							}
						},
						submitTo: {
							conditionRequired: function() {
								return status == 'submit';
							}
						},
						turnUpTime: {
							conditionRequired: function() {
								return status == 'submit';
							}
						}
					}
				});
				
				$("input[name='spotCheckPhoneCallList[0].phoneCallTime'").rules("add", "required");
				$("input[name='spotCheckPhoneCallList[0].phoneCallResult'").rules("add", "required");
				//$("input[name='spotCheckResultList[0].result'").rules("add", "required");
				
				$(".select2-addable").select2({
					 	tags: true,
					 	multiple: false,
				});
				
				$(".timepicker").timepicker({
					showInputs: false,
					showMeridian: false,
					defaultTime: false,
					minuteStep: 1
		        });
				
				$("[name='activityBeingPerformed']").on('change', function(){
					if($(this).val() == 3){
						$("[name='interviewReferenceNo']").removeAttr("disabled");
					}else{
						$("[name='interviewReferenceNo']").attr("disabled", "disabled");
					}
				});
				
				// check if less than 3 rows, add more rows until 3 rows
				/*for(var i = spotCheckPhoneCallListLength; i < 3; i++) {
					var tbody = $('#spotCheckPhoneCallList').children('tbody');
					var deleteBtn = "<td><input type='hidden' value='' name='spotCheckPhoneCallList["+spotCheckPhoneCallListLength+"].spotCheckPhoneCallId' /><a href='javascript:void(0)' onclick='deleteSpotCheckPhoneCallRow(this)' class='table-btn btn-delete'><span class='fa fa-times' aria-hidden='true'></span></a></td>";
					var newRow = "<tr>"
									+ "<td>" + "<div class='input-group col-sm-2 bootstrap-timepicker'>"
									+ "<input class='timepicker' type='text' value='' name='spotCheckPhoneCallList["+spotCheckPhoneCallListLength+"].phoneCallTime' />" 
									+ "<div class='input-group-addon'>"
									+ "<i class='fa fa-clock-o'></i>"
									+ "</div>"
									+ "</div>"
									+ "</td>"
									+ "<td>" + "<input type='radio' name='spotCheckPhoneCallList["+spotCheckPhoneCallListLength+"].phoneCallResult' value='1' />" + "</td>"
									+ "<td>" + "<input type='radio' name='spotCheckPhoneCallList["+spotCheckPhoneCallListLength+"].phoneCallResult' value='2' />" + "</td>"
									+ "<td>" + "<input type='radio' name='spotCheckPhoneCallList["+spotCheckPhoneCallListLength+"].phoneCallResult' value='3' />" + "</td>"
									+ deleteBtn
									+ "</tr>";
					tbody.append(newRow);
					
					$("input[name='spotCheckPhoneCallList["+spotCheckPhoneCallListLength+"].phoneCallTime'").rules("add", "required");
					$("input[name='spotCheckPhoneCallList["+spotCheckPhoneCallListLength+"].phoneCallResult'").rules("add", "required");
					
					$("input[name='spotCheckPhoneCallList["+spotCheckPhoneCallListLength+"].phoneCallTime'").timepicker({
						showInputs: false,
						showMeridian: false,
						defaultTime: false,
						minuteStep: 1
			        });
					
					spotCheckPhoneCallListLength++;
				}*/
				
				// handle add spot check phone call
				$('.addSpotCheckPhoneCall').on('click', function() {
					if(spotCheckPhoneCallListLength < 5) {
						var tbody = $('#spotCheckPhoneCallList').children('tbody');
						var deleteBtn = "<td><input type='hidden' value='' name='spotCheckPhoneCallList["+spotCheckPhoneCallListLength+"].spotCheckPhoneCallId' /><a href='javascript:void(0)' onclick='deleteSpotCheckPhoneCallRow(this)' class='table-btn btn-delete'><span class='fa fa-times' aria-hidden='true'></span></a></td>";
						var newRow = "<tr>"
										+ "<td>" + "<div class='input-group col-sm-2 bootstrap-timepicker'>"
										+ "<input class='timepicker' type='text' value='' name='spotCheckPhoneCallList["+spotCheckPhoneCallListLength+"].phoneCallTime' />"
										+ "<div class='input-group-addon'>"
										+ "<i class='fa fa-clock-o'></i>"
										+ "</div>"
										+ "</div>"
										+ "</td>"
										+ "<td>" + "<input type='radio' name='spotCheckPhoneCallList["+spotCheckPhoneCallListLength+"].phoneCallResult' value='1' />" + "</td>"
										+ "<td>" + "<input type='radio' name='spotCheckPhoneCallList["+spotCheckPhoneCallListLength+"].phoneCallResult' value='2' />" + "</td>"
										+ "<td>" + "<input type='radio' name='spotCheckPhoneCallList["+spotCheckPhoneCallListLength+"].phoneCallResult' value='3' />" + "</td>"
										+ deleteBtn
										+ "</tr>";
						tbody.append(newRow);
						
						$("input[name='spotCheckPhoneCallList["+spotCheckPhoneCallListLength+"].phoneCallTime'").rules("add", "required");
						$("input[name='spotCheckPhoneCallList["+spotCheckPhoneCallListLength+"].phoneCallResult'").rules("add", "required");
						
						$("input[name='spotCheckPhoneCallList["+spotCheckPhoneCallListLength+"].phoneCallTime'").timepicker({
							showInputs: false,
							showMeridian: false,
							defaultTime: false,
							minuteStep: 1
				        });
						
						spotCheckPhoneCallListLength++;
					}
				});
				
				// handle add spot check result
				$('.addSpotCheckResult').on('click', function() {
					if(spotCheckResultListLength < 4) {
						var tbody = $('#spotCheckResultList').children('tbody');
						
						var deleteBtn = "<td><input type='hidden' value='' name='spotCheckResultList["+spotCheckResultListLength+"].spotCheckResultId' /><a href='javascript:void(0)' onclick='deleteSpotCheckResultRow(this)' class='table-btn btn-delete'><span class='fa fa-times' aria-hidden='true'></span></a></td>";
						var newRow = "<tr>"
										+ "<td>" + "<label class='radio-inline'><input type='radio' name='spotCheckResultList["+spotCheckResultListLength+"].result' value='P' /> P</label>" + "</td>"
										+ "<td>" + "<label class='radio-inline'><input type='radio' name='spotCheckResultList["+spotCheckResultListLength+"].result' value='C' /> C</label>" + "</td>"
										+ "<td>" + "<label class='radio-inline'><input type='radio' name='spotCheckResultList["+spotCheckResultListLength+"].result' value='N' /> N</label>" + "</td>"
										+ "<td>" + "<label class='radio-inline'><input type='radio' name='spotCheckResultList["+spotCheckResultListLength+"].result' value='R' /> R</label>" + "</td>"
										+ "<td>" + "<label class='radio-inline'><input type='radio' name='spotCheckResultList["+spotCheckResultListLength+"].result' value='D' /> D</label>" + "</td>"
										+ "<td>" + "<label class='radio-inline'><input type='radio' name='spotCheckResultList["+spotCheckResultListLength+"].result' value='L' /> L</label>" + "</td>"
										+ "<td>" + "<label class='radio-inline'><input type='radio' name='spotCheckResultList["+spotCheckResultListLength+"].result' value='M' /> M</label>" + "</td>"
										+ "<td>" + "<label class='radio-inline'><input class='enableInput' type='radio' name='spotCheckResultList["+spotCheckResultListLength+"].result' value='Others' /> Others(pl. specify)</label>" + "</td>"
										+ "<td style='width:20%'>" + "<input name='spotCheckResultList["+spotCheckResultListLength+"].otherRemark' type='text' class='form-control' value='' maxlength='4000' />" + "</td>"
										+ "<td style='width:20%'>"
										+ "<select class='form-control select2-addable col-sm-2' name='spotCheckResultList["+spotCheckResultListLength+"].referenceNo' >"
										+ "<option value=''></option>"
										+ "<c:forEach items='${model.referenceNoList}' var='referenceNo'>"
										+ "<option value='<c:out value='${referenceNo}' />' >${referenceNo}</option>"
										+ "</c:forEach>"
										+ "</select>"
										+ "</td>"
										+ deleteBtn
										+ "</tr>";
										
						tbody.append(newRow);
						
						$("input[name='spotCheckResultList["+spotCheckResultListLength+"].result']").rules("add", "required");
						$("select[name='spotCheckResultList["+spotCheckResultListLength+"].referenceNo']").rules("add", "required");
						$("select[name='spotCheckResultList["+spotCheckResultListLength+"].referenceNo']").select2({tags: true,multiple: false});
						
						$("[name='spotCheckResultList["+spotCheckResultListLength+"].otherRemark']").attr("disabled", true);
						
						$("input[name='spotCheckResultList["+spotCheckResultListLength+"].result']").on('click', function(){
							var rowNo = $(this).attr('name').substr(20, 1);
							if($(this).val() != 'Others'){
								$("[name='spotCheckResultList["+rowNo+"].otherRemark']").attr("disabled", true);
							}else{
								$("[name='spotCheckResultList["+rowNo+"].otherRemark']").removeAttr("disabled");
							}
						});
						
						spotCheckResultListLength++;
					}
				});
				
				if(spotCheckResultListLength != "") {
					for(var i = 0; i < spotCheckResultListLength; i++) {
						if($("[name='spotCheckResultList["+i+"].result']:checked").val() != 'Others') {
							$("[name='spotCheckResultList["+i+"].otherRemark']").attr("disabled", true);
						}
					}
				}
				
				$('[name$="result"]').on('click', function(){
					var rowNo = $(this).attr('name').substr(20, 1);
					if($(this).val() != 'Others'){
						$("[name='spotCheckResultList["+rowNo+"].otherRemark']").attr("disabled", true);
					}else{
						$("[name='spotCheckResultList["+rowNo+"].otherRemark']").removeAttr("disabled");
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
				
				$("[name='successful']").on('change', function(){
					if($(this).val() == "false"){
						$("[name='unSuccessfulRemark']").removeAttr("disabled");
					}else{
						$("[name='unSuccessfulRemark']").attr("disabled", "disabled");
					}
				});
				
				$("[name='interviewReferenceNo']").on('change', function() {
					var referenceNo = $(this).val();
					$.get("<c:url value='/qualityControlManagement/SpotCheckMaintenance/getLocationByReferenceNo'/>", 
						{referenceNo: referenceNo}, 
						function(data){
							$("[name='location']").val(data);
					});
				});
				
				<sec:authorize access="!(hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256))">
					viewOnly();
				</sec:authorize>
				
				$(".btn-clear").on('click', function(){
					if ($(this).val()=="1"){
						$("#verCheck1RefenceNo").select2("val", "");
						$("[name='verCheck1IsIrregular']").prop("checked", false);
						$("[name='verCheck1Remark']").val('');
					} else {
						$("#verCheck2RefenceNo").select2("val", "");
						$("[name='verCheck2IsIrregular']").prop("checked", false);
						$("[name='verCheck2Remark']").val('');
					}
				});
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Spot Check Maintenance</h1>
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
        	<form id="mainForm" action="<c:url value='/qualityControlManagement/SpotCheckMaintenance/save'/>" method="post" role="form">
        		<input name="spotCheckFormId" value="<c:out value="${model.spotCheckFormId}" />" type="hidden" />
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/qualityControlManagement/SpotCheckMaintenance/home'/>">Back</a>
							</div>
	       					<div class="box-body">
	       						<div class="form-horizontal">
	       							<div class="col-sm-6">
	       								<div class="form-group">
	       									<label class="col-sm-4 control-label">Field Officer:</label>
											<label class="col-sm-4 control-label" style="text-align:left;">${model.officerCode}</label>
	       								</div>
	       							</div>
	       							<div class="col-sm-6">
	       								<div class="form-group">
											<label class="col-sm-4 control-label">Spot Check Date:</label>
											<label class="col-sm-4 control-label" style="text-align:left;">${model.spotCheckDate}</label>
										</div>
										<div class="form-group radio-group">
											<label class="col-sm-4 control-label">Session:</label>
											<div class="col-sm-4">
												<label class="radio-inline">
													<input type="radio" name="session" value="1" <c:if test="${model.session == '1'}">checked</c:if>> Day
												</label>
												<label class="radio-inline">
													<input type="radio" name="session" value="2" <c:if test="${model.session == '2'}">checked</c:if>> Night
												</label>
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-4 control-label">Survey:</label>
											<div class="col-sm-4">
												<select class="form-control filters" name="survey" id="survey">
													<option value=""></option>
													<c:forEach var="surveyValue" items="${model.surveyList}">
														<option value="<c:out value="${surveyValue}" />" <c:if test="${model.survey != null && model.survey == surveyValue}">selected</c:if>>${surveyValue}</option>
													</c:forEach>
												</select>
											</div>
										</div>
	       							</div>
									<div class="col-sm-12">
		       							<div class="box" style="padding:1px;">
		       								<div class="box-header with-border">
		       									<h3 class="box-title">Attendance Check:</h3>
		       								</div>
		       							
											<div class="box-body">
												<table class="table table-striped table-bordered table-hover" id="spotCheckPhoneCallList">
													<thead>
														<tr>
															<th>Time of phone call</th>
															<th>Contacted</th>
															<th>Voice mail</th>
															<th>Others</th>
															<th class="text-center action"></th>
														</tr>
													</thead>
													<tbody>
														<c:if test="${fn:length(model.spotCheckPhoneCallList) gt 0}" >
															<c:forEach var="spotCheckPhoneCall" items="${model.spotCheckPhoneCallList}" varStatus="status">
																<tr>
																	<td><div class="input-group col-sm-2 bootstrap-timepicker">
																		<input class="timepicker" type="text" value="<c:out value="${spotCheckPhoneCall.phoneCallTime}" />" name="spotCheckPhoneCallList[${status.index}].phoneCallTime" required />
																			<div class="input-group-addon">
																				<i class="fa fa-clock-o"></i>
																			</div>
																		</div>
																	</td>
																	<td><input type="radio" name="spotCheckPhoneCallList[${status.index}].phoneCallResult" value="1" <c:if test="${spotCheckPhoneCall.phoneCallResult == '1'}">checked</c:if> /></td>
																	<td><input type="radio" name="spotCheckPhoneCallList[${status.index}].phoneCallResult" value="2" <c:if test="${spotCheckPhoneCall.phoneCallResult == '2'}">checked</c:if> /></td>
																	<td><input type="radio" name="spotCheckPhoneCallList[${status.index}].phoneCallResult" value="3" <c:if test="${spotCheckPhoneCall.phoneCallResult == '3'}">checked</c:if> /></td>
																	<td>
																		<input type="hidden" value="<c:out value="${spotCheckPhoneCall.spotCheckPhoneCallId}" />" name="spotCheckPhoneCallList[${status.index}].spotCheckPhoneCallId" />
																		<c:if test="${status.index > 0}">
																			<a href="javascript:void(0)" onclick="deleteSpotCheckPhoneCallRow(this)" class="table-btn btn-delete">
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
													<a href="javascript:void(0)" class="addSpotCheckPhoneCall"><span class="glyphicon glyphicon-plus"></span>&nbsp;&nbsp;Add</a>															
												</div>
											</sec:authorize>
		       								<div class="form-group radio-group">
			       								<label class="col-sm-2 control-label">Time call Back:</label>
												<div class="col-sm-4">
													<label class="radio-inline">
														<input type="radio" name="timeCallback" value="1" <c:if test="${model.timeCallback == '1'}">checked</c:if>> Reasonable
													</label>
													<label class="radio-inline">
														<input type="radio" name="timeCallback" value="2" <c:if test="${model.timeCallback == '2'}">checked</c:if>> Not Reasonable
													</label>
													<label class="radio-inline">
														<input type="radio" name="timeCallback" value="3" <c:if test="${model.timeCallback == '3'}">checked</c:if>> N.A.
													</label>
													<p id="timeCallbackRequired"></p>
												</div>
											</div>
											<div class="form-group radio-group">
			       								<label class="col-sm-2 control-label">Activity being performed:</label>
												<div class="col-sm-4">
													<label class="radio-inline">
														<input type="radio" name="activityBeingPerformed" value="1" <c:if test="${model.activityBeingPerformed == '1'}">checked</c:if>> On the way to outlet
													</label>
													<label class="radio-inline">
														<input type="radio" name="activityBeingPerformed" value="2" <c:if test="${model.activityBeingPerformed == '2'}">checked</c:if>> Living quarters
													</label>
													<label class="radio-inline">
														<input type="radio" name="activityBeingPerformed" value="3" <c:if test="${model.activityBeingPerformed == '3'}">checked</c:if>> Conducting interview
													</label>
													<%--&nbsp;&nbsp;&nbsp;(Ref. No.--%>
												</div>
												<div class="col-sm-2">
													(Ref. No.
													<select class="form-control select2-addable col-sm-2" name="interviewReferenceNo" required <c:if test="${model.activityBeingPerformed != 3}">disabled</c:if> required>
														<option value=""></option>
														<c:forEach items="${model.referenceNoList}" var="referenceNo">
															<option value="<c:out value="${referenceNo}" />" 
																<c:if test="${model.interviewReferenceNo != null && model.activityBeingPerformed == '3' && model.interviewReferenceNo == referenceNo}">
																	selected
																</c:if>>
																${referenceNo}
															</option>
														</c:forEach>
													</select>
													)
												</div>
												<div class="col-sm-2">
													<%--)&nbsp;&nbsp;&nbsp;--%>
													<label class="radio-inline">
														<input type="radio" name="activityBeingPerformed" value="4" <c:if test="${model.activityBeingPerformed == '4'}">checked</c:if>> Others
													</label>
												</div>
												<p id="activityBeingPerformedRequired"></p>
											</div>
			       							<div class="form-group">
			       								<label class="col-sm-2 control-label">Location: </label>
			       								<div class="col-sm-5">
			       									<input name="location" type="text" class="form-control" value="<c:out value="${model.location}" />" maxlength="4000" />
												</div>
											</div>
											<div class="form-group">
			       								<label class="col-sm-2 control-label">Enumeration Result of Last Case(s) (If any)</label>
												<div class="col-sm-8">
													<table class="table table-striped table-bordered table-hover" id="spotCheckResultList">
														<tbody>
															<c:if test="${fn:length(model.spotCheckResultList) gt 0}" >
																<c:forEach var="spotCheckResult" items="${model.spotCheckResultList}" varStatus="status">
																	<tr>
																		<td><label class="radio-inline"><input type="radio" name="spotCheckResultList[${status.index}].result" value="P" <c:if test="${spotCheckResult.result == 'P'}">checked</c:if> /> P</label></td>
																		<td><label class="radio-inline"><input type="radio" name="spotCheckResultList[${status.index}].result" value="C" <c:if test="${spotCheckResult.result == 'C'}">checked</c:if> /> C</label></td>
																		<td><label class="radio-inline"><input type="radio" name="spotCheckResultList[${status.index}].result" value="N" <c:if test="${spotCheckResult.result == 'N'}">checked</c:if> /> N</label></td>
																		<td><label class="radio-inline"><input type="radio" name="spotCheckResultList[${status.index}].result" value="R" <c:if test="${spotCheckResult.result == 'R'}">checked</c:if> /> R</label></td>
																		<td><label class="radio-inline"><input type="radio" name="spotCheckResultList[${status.index}].result" value="D" <c:if test="${spotCheckResult.result == 'D'}">checked</c:if> /> D</label></td>
																		<td><label class="radio-inline"><input type="radio" name="spotCheckResultList[${status.index}].result" value="L" <c:if test="${spotCheckResult.result == 'L'}">checked</c:if> /> L</label></td>
																		<td><label class="radio-inline"><input type="radio" name="spotCheckResultList[${status.index}].result" value="M" <c:if test="${spotCheckResult.result == 'M'}">checked</c:if> /> M</label></td>
																		<td><label class="radio-inline"><input class='enableInput' type="radio" name="spotCheckResultList[${status.index}].result" value="Others" <c:if test="${spotCheckResult.result == 'Others'}">checked</c:if> /> Others(pl. specify)</label></td>
																		<td style="width:20%">
																			<input name="spotCheckResultList[${status.index}].otherRemark" type="text" class="form-control" value="<c:out value="${spotCheckResult.otherRemark}" />" maxlength="4000" />
																		</td>
																		<td style="width:20%">
																			<select class="form-control select2-addable col-sm-2" name="spotCheckResultList[${status.index}].referenceNo" >
																				<option value=""></option>
																				<c:set var="varCheck" value="false" />
																				<c:forEach items="${model.referenceNoList}" var="referenceNo">
																					<option value="<c:out value="${referenceNo}" />" 
																						<c:if test="${spotCheckResult.referenceNo != null && spotCheckResult.referenceNo == referenceNo}">
																							<c:set var="varCheck" value="true" />
																							selected
																						</c:if>>
																						${referenceNo}
																					</option>
																				</c:forEach>
																				<c:if test="${(not varCheck) && spotCheckResult.referenceNo != null}">
																					<option value="<c:out value="${spotCheckResult.referenceNo}" />" selected>${spotCheckResult.referenceNo}</option>
																				</c:if>
																			</select>
																		</td>
																		<td>
																			<input type="hidden" value="<c:out value="${spotCheckResult.spotCheckResultId}" />" name="spotCheckResultList[${status.index}].spotCheckResultId" />
																			<c:if test="${status.index > 0}">
																				<a href="javascript:void(0)" onclick="deleteSpotCheckResultRow(this)" class="table-btn btn-delete">
																				<span class="fa fa-times" aria-hidden="true"></span></a>
																			</c:if>
																		</td>
																	</tr>
																</c:forEach>
															</c:if>
														</tbody>
													</table>
												</div>
												
											</div>
											<div class="form-group">
			       								<label class="col-sm-2 control-label"></label>
			       								<div class="col-sm-5">
			       									<sec:authorize access="hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)">
				       									<div style="margin-b: 15px;">
															<a href="javascript:void(0)" class="addSpotCheckResult"><span class="glyphicon glyphicon-plus"></span>&nbsp;&nbsp;Add</a>															
														</div>
													</sec:authorize>
												</div>
											</div>
											<%--<div class="form-group">
			       								<label class="col-sm-2 control-label">Case ref No: </label>
			       								<div class="col-sm-3">
			       									<select class="form-control select2-addable col-sm-2" name="caseReferenceNo" required>
														<option value=""></option>
														<c:forEach items="${model.referenceNoList}" var="referenceNo">
															<option value="<c:out value="${referenceNo}" />" <c:if test="${model.caseReferenceNo != null && model.caseReferenceNo == referenceNo}">selected</c:if>>${referenceNo}</option>
														</c:forEach>
													</select>
													<p id="caseReferenceNoRequired"></p>
												</div>
											</div>--%>
											<div class="form-group">
			       								<label class="col-sm-2 control-label">Remarks: </label>
			       								<div class="col-sm-5">
													<input name="remarksForNonContact" type="text" class="form-control" value="<c:out value="${model.remarksForNonContact}" />" maxlength="4000" />
												</div>
											</div>
											<div class="form-group">
			       								<label class="col-sm-2 control-label">Scheduled Place: </label>
			       								<div class="col-sm-5">
			       									<input name="scheduledPlace" type="text" class="form-control" value="<c:out value="${model.scheduledPlace}" />" maxlength="4000" />
												</div>
											</div>
											<div class="form-group">
			       								<label class="col-sm-2 control-label">Scheduled Time: </label>
			       								<div class="col-sm-3 input-group bootstrap-timepicker">
			       									<input name="scheduledTime" type="text" class="form-control timepicker" value="<c:out value="${model.scheduledTime}" />" maxlength="4000" />
			       									<div class="input-group-addon">
														<i class="fa fa-clock-o"></i>
													</div>
												</div>
											</div>
											<div class="form-group">
			       								<label class="col-sm-2 control-label">Turn up time: </label>
			       								<div class="col-sm-3 input-group bootstrap-timepicker">
			       									<input name="turnUpTime" type="text" class="form-control timepicker" value="<c:out value="${model.turnUpTime}" />" maxlength="4000" />
			       									<div class="input-group-addon">
														<i class="fa fa-clock-o"></i>
													</div>
												</div>
												<div class="col-sm-7 radio-group">
													hrs&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
													<label class="radio-inline">
														<input type="radio" name="reasonable" value="true" <c:if test="${model.reasonable == 'true'}">checked</c:if>> Reasonable
													</label>
													<label class="radio-inline">
														<input type="radio" name="reasonable" value="false" <c:if test="${model.reasonable == 'false'}">checked</c:if>> Non Reasonable
													</label>
												</div>
											</div>
											<div class="form-group radio-group">
			       								<label class="col-sm-2 control-label">Time-log vs Pre-app Itinerary:</label>
												<div class="col-sm-5">
													<label class="radio-inline">
														<input type="radio" name="irregular" value="false" <c:if test="${model.irregular == 'false'}">checked</c:if>> No Irregularities
													</label>
													<label class="radio-inline">
														<input type="radio" name="irregular" value="true" <c:if test="${model.irregular == 'true'}">checked</c:if>> Irregularities found
													</label>
													<p id="irregularRequired"></p>
												</div>
											</div>
											<div class="form-group">
			       								<label class="col-sm-2 control-label">
			       									Remarks for turn up time not reasonable (the scheduled meeting > 20 mins.) / irregular found: 
			       								</label>
			       								<div class="col-sm-5">
			       									<input name="remarkForTurnUpTime" type="text" class="form-control" value="<c:out value="${model.remarkForTurnUpTime}" />" maxlength="4000" />
												</div>
											</div>
										</div>
									</div>
									<div class="col-sm-12">
										<div class="box" style="padding:1px;">
											<div class="box-header with-border">
		       									<h3 class="box-title">Verification Check:</h3>
		       								</div>
										
											<div class="form-group">
			       								<label class="col-sm-1 control-label text-left">1)</label>
											</div>
		       								<div class="form-group">
			       								<label class="col-sm-2 control-label">Ref. No.</label>
			       								<div class="col-sm-3">
			       									<select class="form-control select2-addable col-sm-2" name="verCheck1RefenceNo" id="verCheck1RefenceNo">
														<option value=""></option>
														<c:forEach items="${model.referenceNoList}" var="referenceNo">
															<option value="<c:out value="${referenceNo}" />" <c:if test="${model.verCheck1RefenceNo != null && model.verCheck1RefenceNo == referenceNo}">selected</c:if>>${referenceNo}</option>
														</c:forEach>
													</select>
												</div>
											</div>
			       							<div class="form-group radio-group">
			       								<label class="col-sm-2 control-label">Verification:</label>
												<div class="col-sm-5">
													<label class="radio-inline">
														<input type="radio" name="verCheck1IsIrregular" value="false" <c:if test="${model.verCheck1IsIrregular == 'false'}">checked</c:if>> No irregularities
													</label>
													<label class="radio-inline">
														<input type="radio" name="verCheck1IsIrregular" value="true" <c:if test="${model.verCheck1IsIrregular == 'true'}">checked</c:if>> irregularities found
													</label>
													<p id="verCheck1IsIrregularRequired"></p>
												</div>
											</div>
											<div class="form-group">
			       								<label class="col-sm-2 control-label">Remark: </label>
			       								<div class="col-sm-5">
			       									<input name="verCheck1Remark" type="text" class="form-control" value="<c:out value="${model.verCheck1Remark}" />" maxlength="4000" />
												</div>
												<div class="col-sm-3">
													<button type="button" class="btn btn-info btn-clear" value="1">Clear</button>
												</div>
											</div>
											<hr/>
											
											<div class="form-group">
			       								<label class="col-sm-1 control-label">2)</label>
											</div>
		       								<div class="form-group">
			       								<label class="col-sm-2 control-label">Ref. No.</label>
			       								<div class="col-sm-3">
			       									<select class="form-control select2-addable col-sm-2" name="verCheck2RefenceNo" id="verCheck2RefenceNo">
														<option value=""></option>
														<c:forEach items="${model.referenceNoList}" var="referenceNo">
															<option value="<c:out value="${referenceNo}" />" <c:if test="${model.verCheck2RefenceNo != null && model.verCheck2RefenceNo == referenceNo}">selected</c:if>>${referenceNo}</option>
														</c:forEach>
													</select>
												</div>
											</div>
			       							<div class="form-group radio-group">
			       								<label class="col-sm-2 control-label">Verification:</label>
												<div class="col-sm-5">
													<label class="radio-inline">
														<input type="radio" name="verCheck2IsIrregular" value="false" <c:if test="${model.verCheck2IsIrregular == 'false'}">checked</c:if>> No irregularities
													</label>
													<label class="radio-inline">
														<input type="radio" name="verCheck2IsIrregular" value="true" <c:if test="${model.verCheck2IsIrregular == 'true'}">checked</c:if>> irregularities found
													</label>
													<p id="verCheck2IsIrregularRequired"></p>
												</div>
											</div>
											<div class="form-group">
			       								<label class="col-sm-2 control-label">Remark: </label>
			       								<div class="col-sm-5">
			       									<input name="verCheck2Remark" type="text" class="form-control" value="<c:out value="${model.verCheck2Remark}" />" maxlength="4000" />
												</div>
												<div class="col-sm-3">
													<button type="button" class="btn btn-info btn-clear" value="2">Clear</button>
												</div>
											</div>
	       								</div>
	       							</div>
	       							
	       							<hr/>
	       							<div class="form-group">
	       								<label class="col-sm-2 control-label">Submit To: </label>
	       								<div class="col-sm-3">
											<div class="input-group">
												<input name="submitTo" type="text" class="form-control" value="<c:out value="${model.submitTo}" />" readonly />
												<input id="submitToId" name="submitToId" value="<c:out value="${model.submitToId}" />" type="hidden" />
												<div class="input-group-addon searchSubmitToId">
													<i class="fa fa-search"></i>
												</div>
											</div>
										</div>
									</div>
									<c:if test="${not empty model.rejectReason}">
										<div class="form-group">
		       								<label class="col-sm-2 control-label">Reject Reason: </label>
		       								<label class="col-sm-5 control-label" style="text-align:left;">${model.rejectReason}</label>
										</div>
									</c:if>
									<div class="radio-group">
	       								<div class="form-group col-sm-12">
		       								<div class="col-sm-2" style="text-align:right;">
		       									<label class="radio-inline">
													<input type="radio" name="successful" value="true" <c:if test="${model.successful == 'true'}">checked</c:if>> Successful
												</label>
											</div>
											<div class="col-sm-2">&nbsp;</div>
										</div>
	       								<div class="form-group col-sm-12">
		       								<div class="col-sm-2" style="text-align:right;">
		       									<label class="radio-inline">
													<input type="radio" name="successful" value="false" <c:if test="${model.successful == 'false'}">checked</c:if>> Unsuccessful
												</label>
											</div>
											<div class="col-sm-6">
												<input name="unSuccessfulRemark" type="text" class="form-control" value="<c:out value="${model.unSuccessfulRemark}" />" <c:if test="${model.successful != false}">disabled</c:if> maxlength="4000" />
											</div>
										</div>
									</div>
								</div>
							</div>
	       					<sec:authorize access="hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)">
								<div class="box-footer">
									<c:if test="${!(model.status == 'Approved' || model.status == 'Submitted')}">
										<button type="submit" class="btn btn-info" name="submitBtn" value="save">Save</button>
									</c:if>
	        							<button type="submit" class="btn btn-info" name="submitBtn" value="submit">Submit</button>
	       						</div>
	       					</sec:authorize>
						</div>
	        		</div>
	        	</div>
        	</form>
        </section>
	</jsp:body>
</t:layout>
