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
		<%@include file="/WEB-INF/views/includes/datejs.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/userLookup.jsp" %>
		<script>
			$(function() {
				var $mainForm = $('#mainForm');
				
				$mainForm.validate();
				
				$('#spotCheckDateId').select2ajax();
				
				<c:if test="${editable}">
				$('[name="fieldOfficer"],.searchUserId').userLookup({
					selectedIdsCallback: function(selectedIds, singleRowData) {
						var id = selectedIds[0];
						$('[name="fieldOfficer"]').val(singleRowData.staffCode
								+ ' - '
								+ singleRowData.chineseName);
						$('[id="fieldOfficerId"]').val(id);
						$.ajax({
							url: "<c:url value='/qualityControlManagement/SpotCheckSetupMaintenance/fieldOfficerChosen'/>",
							method: 'post',
							//dataType: 'json',
			      			//contentType: "application/json; charset=utf-8",
							data: {userId : id},
							success: function(response){
					            $('#supervisorLabel').text(response);
					        }
						});
					},
					queryDataCallback: function(model) {
						model.authorityLevel = 16;
					},					
					multiple: false
				});
				</c:if>
				
				$('#notificationDate').datepicker();
				<c:if test="${not editable and not (act eq 'add')}">
					$("input,select").attr("disabled","disabled")
				</c:if>
				
				<sec:authorize access="!(hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 256))">
					viewOnly();
				</sec:authorize>
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Spot Check Setup Maintenance</h1>
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
        	<form id="mainForm" action="<c:url value='/qualityControlManagement/SpotCheckSetupMaintenance/save'/>" method="post" role="form"
        		enctype="multipart/form-data">
        		<input name="spotCheckSetupId" value="<c:out value="${model.spotCheckSetupId}" />" type="hidden" />
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/qualityControlManagement/SpotCheckSetupMaintenance/home'/>">Back</a>
							</div>
	       					<div class="box-body">
	       						<div class="form-horizontal">
	       							<div class="col-sm-12">
	       								<div class="form-group">
											<label class="col-sm-2 control-label">Spot Check Date: </label>
											<div class="col-sm-4">
												<select class="form-control select2" id="spotCheckDateId" name="spotCheckDateId"
													data-ajax-url="<c:url value='/qualityControlManagement/SpotCheckSetupMaintenance/querySpotCheckDateSelect'/>" required>
													<c:if test="${model.spotCheckDateId != null}">
														<option value="<c:out value="${model.spotCheckDateId}" />" selected>${model.spotCheckDate}</option>
													</c:if>
												</select>
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-2 control-label">Field Officer: </label>
		       								<div class="col-sm-4">
		       									<div class="input-group">
													<input name="fieldOfficer" type="text" class="form-control" value="<c:out value="${model.fieldOfficer}" />" required readonly />
													<input id="fieldOfficerId" name="fieldOfficerId" value="<c:out value="${model.fieldOfficerId}" />" type="hidden" />
													<div class="input-group-addon searchUserId">
														<i class="fa fa-search"></i>
													</div>
												</div>
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-2 control-label">Notification Date: </label>
											<div class="col-sm-10">
												<label class="radio-inline">
													<input type="radio" name="notificationType" value="1" <c:if test="${model.notificationType == '1'}">checked</c:if>> Spot check date
												</label>
												<label class="radio-inline">
													<input type="radio" name="notificationType" value="2" <c:if test="${model.notificationType == '2'}">checked</c:if>> Previous date
												</label>
											</div>
										</div>
										<div class="form-group">
											<label class="col-sm-2 control-label">Supervisor: </label>
		       								<div class="col-sm-4">
		       									<p id="supervisorLabel" class="form-control-static">${model.supervisor}</p>
											</div>
										</div>
									</div>
								</div>
							</div>
							<c:if test="${editable}">
		       					<sec:authorize access="hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 256)">
									<div class="box-footer">
		        						<button type="submit" class="btn btn-info">Submit</button>	       						
		       						</div>
		       					</sec:authorize>
	       					</c:if>
						</div>
	        		</div>
	        	</div>
        	</form>
        </section>
	</jsp:body>
</t:layout>
