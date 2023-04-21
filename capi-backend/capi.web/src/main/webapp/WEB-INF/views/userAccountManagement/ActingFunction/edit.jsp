<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
<jsp:useBean id="niceDate" class="java.util.Date"/>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/timepicker-css.jsp" %>
		
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp" %>
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
		<script src="<c:url value='/resources/js/jquery.validate.custom.js'/>" ></script>
		<script>
			$(function() {
				var $mainForm = $('#mainForm');
				
				$mainForm.validate({
					rules: {
						endDate: {
							checkEndDate: $('#startDate')
						}
					}
				});
				
				$('.searchUserId').userLookup({
					selectedIdsCallback: function(selectedIds, singleRowData) {
						var id = selectedIds[0];
						console.log('id = ' + id);
						$('[name="staff"]').val(singleRowData.staffCode + " - "
								+ singleRowData.chineseName);
						$('[id="staffId"]').val(id);
					},
					/*queryDataCallback: function(model) {
						model.authorityLevel = 1;
					},*/
					multiple: false
				});
				
				$('.searchReplacementId').userLookup({
					selectedIdsCallback: function(selectedIds, singleRowData) {
						var id = selectedIds[0];
						$('[name="replacement"]').val(singleRowData.staffCode + " - "
								+ singleRowData.chineseName);
						$('[id="replacementId"]').val(id);
					},
					multiple: false
				});
				
				$('#startDate').datepicker({
					autoclose: true,
					todayBtn: 'linked'
				});
				
				$('#endDate').datepicker({
					autoclose: true,
					todayBtn: 'linked'
				});
				
				$('#roleId').select2ajax({
					placeholder: "Select a role",
					allowClear: true
				});
				
				$('#startDate').on('change', function() {
					$('#endDate').datepicker('setStartDate', $(this).val());
				});
				
				$('#endDate').on('change', function() {
					$('#startDate').datepicker('setEndDate', $(this).val());
				});
				
				if($('#startDate').datepicker().val() != null) {
					$('#endDate').datepicker('setStartDate', 
							$('#startDate').datepicker().val());
				}
				
				if($('#endDate').datepicker().val() != null) {
					$('#startDate').datepicker('setEndDate', 
							$('#endDate').datepicker().val());
				}
				
				<sec:authorize access="!hasPermission(#user, 512)">
					viewOnly();
				</sec:authorize>
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Acting Function</h1>
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
        	<form id="mainForm" action="<c:url value='/userAccountManagement/ActingFunction/save'/>" method="post" role="form"
        		enctype="multipart/form-data">
        		<input name="actingId" value="<c:out value="${model.actingId}" />" type="hidden" />
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/userAccountManagement/ActingFunction/home'/>">Back</a>
							</div>
	       					<div class="box-body">
	       						<div class="form-horizontal">
	       							<div class="col-sm-12">
		       							<div class="form-group">
		       								<label class="col-sm-2 control-label">Staff</label>
		       								<div class="col-sm-4">
		       									<div class="input-group searchUserId">
													<input name="staff" type="text" class="form-control" value="<c:out value="${model.staff}" />" required readonly />
													<input id="staffId" name="staffId" value="<c:out value="${model.staffId}" />" type="hidden" />
													<div class="input-group-addon">
														<i class="fa fa-search"></i>
													</div>
												</div>
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-2 control-label">Replacement</label>
		       								<div class="col-sm-4">
		       									<div class="input-group searchReplacementId">
													<input name="replacement" type="text" class="form-control" value="<c:out value="${model.replacement}" />" required readonly />
													<input id="replacementId" name="replacementId" value="<c:out value="${model.replacementId}" />" type="hidden" />
													<div class="input-group-addon">
														<i class="fa fa-search"></i>
													</div>
												</div>
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-2 control-label">Start Date</label>
		       								<div class="col-sm-2">
		       									<div class="input-group">
													<input id="startDate" name="startDate" type="text" class="form-control" style="width: 290px;" value="<c:out value="${model.startDate}" />" required />
													<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
												</div>
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-2 control-label">End Date</label>
		       								<div class="col-sm-2">
		       									<div class="input-group">
													<input id="endDate" name="endDate" type="text" class="form-control" style="width: 290px;" value="<c:out value="${model.endDate}" />" required />
													<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
												</div>
											</div>
										</div>
										<div class="form-group">
											<label class="col-sm-2 control-label">Role</label>
											<div class="col-sm-4">
												<select class="form-control selec2" id="roleId" name="roleId"
													data-ajax-url="<c:url value='/userAccountManagement/ActingFunction/queryRoleSelect'/>" required>
													<c:if test="${model.roleId != null}">
														<option value="<c:out value="${model.roleId}" />" selected>${model.roleName}</option>
													</c:if>
												</select>
											</div>
										</div>
									</div>
								</div>
							</div>
	       					<sec:authorize access="hasPermission(#user, 512)">
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

