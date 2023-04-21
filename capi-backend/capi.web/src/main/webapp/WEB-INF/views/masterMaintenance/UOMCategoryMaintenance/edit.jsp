<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page session="false"%>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
	</jsp:attribute>
	<jsp:attribute name="header">
<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
<script src="<c:url value='/resources/js/Sortable.js'/>"></script>

		<style>
			.btn.pull-right {
				margin-left: 10px;
			}
		</style>
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
			<h1>UOM Category Maintenance</h1>
			<c:if test="${not empty model.uomCategoryId}">
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
		<form class="form-horizontal" action="<c:url value='/masterMaintenance/UOMCategoryMaintenance/save'/>" method="post" role="form" id="mainForm">
		    <div class="row">
				<div class="col-md-12">
					<!-- content -->
						<div class="box box-primary">
							<!-- /.box-header -->
							<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/masterMaintenance/UOMCategoryMaintenance/home'/>">Back</a>
							</div>
							<!-- form start -->
								<div class="box-body">
									<div class="form-group">
										<label for="uomCategoryId" class="col-sm-2 control-label">Code</label>
										<div class="col-sm-6">
											<p class="form-control-static">${model.uomCategoryId}</p>
											<input type="hidden" name="uomCategoryId" value="<c:out value="${model.uomCategoryId}" />" />
										</div>
									</div>
									<div class="form-group">
										<label for="description" class="col-sm-2 control-label">Description</label>
										<div class="col-sm-6">
											<input type="text" name="description" class="form-control" maxlength="2000" value="<c:out value="${model.description}" />" required>
										</div>
									</div>
								</div>
							<!-- /.box-body -->
							<sec:authorize access="hasPermission(#user, 256)">
								<div class="box-footer">
									<button type="submit" class="btn btn-info">Submit</button>							
								</div>
							</sec:authorize>
						</div>
				</div>
			</div>



						
					<!-- /.box-footer -->

					</form>
		</section>
		
		

	</jsp:body>
</t:layout>
