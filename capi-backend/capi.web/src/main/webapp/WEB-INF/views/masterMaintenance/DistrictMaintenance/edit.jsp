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
		<script>
			$(function() {
				var $mainForm = $('#mainForm');
				
				$mainForm.validate();
				
				<sec:authorize access="!hasPermission(#user, 256)">
					viewOnly();
				</sec:authorize>
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>District Maintenance</h1>
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
        	<form id="mainForm" action="<c:url value='/masterMaintenance/DistrictMaintenance/save?act='/>${act}" method="post" role="form" >
        		<input name="districtId" value="<c:out value="${model.districtId}" />" type="hidden" />
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/masterMaintenance/DistrictMaintenance/home'/>">Back</a>
							</div>
	       					<div class="box-body">
	       						<div class="form-horizontal">
	       							<div class="col-sm-12">
		       							<div class="form-group">
		       								<label class="col-sm-2 control-label">District Code</label>
		       								<div class="col-sm-4">
		       									<%--<c:choose>
			       									<c:when test="${act eq 'add'}">
														<input name="code" type="text" class="form-control" value="<c:out value="${model.code}" />" maxlength="50" required />
													</c:when>
													<c:otherwise>
														<p class="form-control-static" >${model.code}</p>
														<input name="code" value="<c:out value="${model.code}" />" type="hidden" />
													</c:otherwise>
												</c:choose>--%>
												<input name="code" type="text" class="form-control" value="<c:out value="${model.code}" />" maxlength="50" required />
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-2 control-label">Chinese Name</label>
		       								<div class="col-sm-4">
												<input name="chineseName" type="text" class="form-control" value="<c:out value="${model.chineseName}" />" maxlength="2000" />
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-2 control-label">English Name</label>
		       								<div class="col-sm-4">
												<input name="englishName" type="text" class="form-control" value="<c:out value="${model.englishName}" />" maxlength="2000" />
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-2 control-label">Coverage</label>
		       								<div class="col-sm-4">
												<input name="coverage" type="text" class="form-control" value="<c:out value="${model.coverage}" />" maxlength="2000" />
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

