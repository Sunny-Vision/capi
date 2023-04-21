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
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp"%>
	</jsp:attribute>
	<jsp:attribute name="header">
<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
<%@include file="/WEB-INF/views/includes/datepicker.jsp"%>
<script src="<c:url value='/resources/js/Sortable.js'/>"></script>

		<style>
			.btn.pull-right {
				margin-left: 10px;
			}
		</style>
<script>

$(document).ready(function(){
	
	var $mainForm = $('#mainForm');
	

	$mainForm.validate();
	
	Datepicker();
	
	$('.month-picker').datepicker({
		format: monthFormat,
		startView: "months", 
	    minViewMode: "months",
	    autoclose: true
	});

	<c:if test="${connectedSurveyMonth}">
		viewOnly();
	</c:if>
	
	<sec:authorize access="!hasPermission(#user, 256)">
		viewOnly();
	</sec:authorize>
});
</script>
</jsp:attribute>
	<jsp:body>
		<section class="content-header">
		<h1>Closing Date Maintenance</h1>
		<c:if test="${act != 'add'}">
			<div class="breadcrumb form-horizontal" style="width:240px">
				<div class="form-group" style="margin-bottom:0px">
		        	<div class="col-sm-5">Created Date:</div>
		        	<div class="col-sm-7">${model.createdDate}</div>
		        </div>
		        <div class="form-group" style="margin-bottom:0px">
		         	<div class="col-sm-5">Last Modified:</div>
		         	<div class="col-sm-7">${model.modifiedDate}</div>
		         </div>
	      	</div>
      	</c:if>
		</section>
		<section class="content">
		<form class="form-horizontal" action="<c:url value='/masterMaintenance/ClosingDateMaintenance/save?act='/>${act}" method="post" role="form" id="mainForm">
		    <input name="closingDateId" value="<c:out value="${model.closingDateId}" />" type="hidden" />
		    <div class="row">
				<div class="col-md-12">
					<!-- content -->
						<div class="box box-primary">
							<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/masterMaintenance/ClosingDateMaintenance/home'/>">Back</a>
							</div>
							<!-- /.box-header -->
							<!-- form start -->
								<div class="box-body">
									<div class="form-group">
										<label for="referenceMonth" class="col-sm-2 control-label">Reference Month</label>
										<div class="col-sm-4">
										 <c:choose>
	       									<c:when test="${act eq 'add'}">
	       										<div class="input-group">
													<input type="text" name="referenceMonth" class="form-control month-picker" maxlength="7" value="<c:out value="${model.referenceMonth}" />" required>
													<div class="input-group-addon">
														<i class="fa fa-calendar"></i>
													</div>
												</div>
											</c:when>
											<c:otherwise>
												<p class="form-control-static" >${model.referenceMonth}</p>
												<input name="referenceMonth" value="<c:out value="${model.referenceMonth}" />" type="hidden" />
											</c:otherwise>
										</c:choose>
												
											</div>
									</div>
									<div class="form-group">
										<label for="closingDate" class="col-sm-2 control-label">Closing Date</label>
										<div class="col-sm-4">
											<div class="input-group">
												<input type="text" name="closingDate" class="form-control date-picker" maxlength="10" value="<c:out value="${model.closingDate}" />" required>
												<div class="input-group-addon">
													<i class="fa fa-calendar"></i>
												</div>
											</div>
										</div>
									</div>
									<div class="form-group">
	       								<label class="col-sm-2 control-label">Publish Date</label>
	       								<div class="col-sm-4">
	       									<div class="input-group">
												<input name="publishDate" type="text" class="form-control date-picker" value="<c:out value="${model.publishDate}" />" maxlength="10" required/>
												<div class="input-group-addon">
													<i class="fa fa-calendar"></i>
												</div>
											</div>
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
