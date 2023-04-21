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
		<script src="<c:url value='/resources/js/jquery.validate.custom.js'/>" ></script>
		<script>
			$(function() {
				var $mainForm = $('#mainForm');
				
				$mainForm.validate();
				
				$('#districtId').select2ajax();
				
				<sec:authorize access="!hasPermission(#user, 256)">
					viewOnly();
				</sec:authorize>
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>TPU Maintenance</h1>
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
        	<form id="mainForm" action="<c:url value='/masterMaintenance/TpuMaintenance/save?act='/>${act}" method="post" role="form">
        		<input name="tpuId" value="<c:out value="${model.tpuId}" />" type="hidden" />
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/masterMaintenance/TpuMaintenance/home'/>">Back</a>
							</div>
	       					<div class="box-body">
	       						<div class="form-horizontal">
	       							<div class="col-sm-12">
		       							<div class="form-group">
		       								<label class="col-sm-2 control-label">TPU Code</label>
		       								<div class="col-sm-4">
		       									<c:choose>
			       									<c:when test="${act eq 'add'}">
														<input name="code" type="text" class="form-control" value="<c:out value="${model.code}" />" maxlength="50" required />
													</c:when>
													<c:otherwise>
														<p class="form-control-static">${model.code}</p>
														<input name="code" value="<c:out value="${model.code}" />" type="hidden" />
													</c:otherwise>
												</c:choose>
											</div>
										</div>
										<%--
		       							<div class="form-group">
		       								<label class="col-sm-2 control-label">Description</label>
		       								<div class="col-sm-4">
												<input name="description" type="text" class="form-control" value="<c:out value="${model.description}" />" maxlength="4000" />
											</div>
										</div>
										 --%>
										<div class="form-group">
		       								<label class="col-sm-2 control-label">District Council District</label>
		       								<div class="col-sm-4">
												<input name="councilDistrict" type="text" class="form-control" value="<c:out value="${model.councilDistrict}" />" maxlength="2000" />
											</div>
										</div>
										<div class="form-group">
											<label class="col-sm-2 control-label">District</label>
											<div class="col-sm-4">
												<select class="form-control selec2" id="districtId" name="districtId"
													data-ajax-url="<c:url value='/masterMaintenance/TpuMaintenance/queryDistrictSelect'/>" required>
													<c:if test="${model.districtId != null}">
														<option value="<c:out value="${model.districtId}" />" selected>${model.districtLabel}</option>
													</c:if>
												</select>
											</div>
										</div>
									</div>
								</div>
							</div>
	       					<sec:authorize access="hasPermission(#user, 256)">
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

