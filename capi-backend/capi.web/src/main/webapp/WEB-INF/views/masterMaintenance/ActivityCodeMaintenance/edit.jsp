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

$(document).ready(function(){
	
	var $mainForm = $('#mainForm');

	$mainForm.validate({
		rules : {
			manDay : {
				number: true,
				min :0
				
			}
		},
		messages: {
			manDay: {
				number: "<spring:message code='E00071' />",
				min: "<spring:message code='E00007' />"
				
			}
		}

	});

	<sec:authorize access="!hasPermission(#user, 256)">
		viewOnly();
	</sec:authorize>
});
</script>
</jsp:attribute>
	<jsp:body>
		<section class="content-header">
		<h1>Activity Code Maintenance</h1>		
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
		<form class="form-horizontal" action="<c:url value='/masterMaintenance/ActivityCodeMaintenance/save?act='/>${act}" method="post" role="form" id="mainForm">
		    <input name="activityCodeId" value="<c:out value="${model.activityCodeId}" />" type="hidden" />
		    <div class="row">
				<div class="col-md-12">
					<!-- content -->
						<div class="box box-primary">
							<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/masterMaintenance/ActivityCodeMaintenance/home'/>">Back</a>
							</div>
							<!-- /.box-header -->
							<!-- form start -->
								<div class="box-body">
	       							<div class="form-group">
	       								<label class="col-sm-2 control-label">Code</label>
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
									<div class="form-group">
										<label for="description" class="col-sm-2 control-label">Description</label>
										<div class="col-sm-4">
											<input type="text" name="description" class="form-control" maxlength="512" value="<c:out value="${model.description}" />">
										</div>
									</div>
									<div class="form-group">
	       								<label class="col-sm-2 control-label">Man-day</label>
	       								<div class="col-sm-4">
										<input name="manDay" type="text" class="form-control" value="<c:out value="${model.manDay}" />" maxlength="14" required/>
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
