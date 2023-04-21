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
		<%@include file="/WEB-INF/views/includes/select2-css.jsp" %>
	</jsp:attribute>
	<jsp:attribute name="header">
<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
<%@include file="/WEB-INF/views/includes/select2.jsp" %>
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
			quotationPerManDay : {
				number: true,
				greaterThan :0
				
			}
		},
		messages: {
			quotationPerManDay: {
				number: "<spring:message code='E00071' />",
				greaterThan :	"<spring:message code='E00102' arguments='0' />"
			}
		}

	});
	
	$('#outletTypeId').select2();
	$('#districtId').select2ajax();
	
	
});
</script>
</jsp:attribute>
	<jsp:body>
		<section class="content-header">
		<h1>Quotation Loading Maintenance</h1>
			<c:if test="${not empty model.id}">
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
		<form class="form-horizontal" action="<c:url value='/masterMaintenance/QuotationLoadingMaintenance/save?act='/>${act}" method="post" role="form" id="mainForm">
		    <input name="quotationLoadingId" value="<c:out value="${model.quotationLoadingId}" />" type="hidden" />
		    <div class="row">
				<div class="col-md-12">
					<!-- content -->
						<div class="box box-primary">
							<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/masterMaintenance/QuotationLoadingMaintenance/home'/>">Back</a>
							</div>
							<!-- /.box-header -->
							<!-- form start -->
								<div class="box-body">
	       							<div class="form-group">
										<label for="districtId" class="col-sm-2 control-label">District</label>
		       								<div class="col-sm-7">
												<select class="form-control select2" id="districtId" name="districtId" style="width: 300px"
													data-ajax-url="<c:url value='/masterMaintenance/QuotationLoadingMaintenance/queryDistrictSelect2'/>" required>
													<c:if test="${model.districtId != null}">
														<option value="<c:out value="${model.districtId}" />" selected>${model.districtLabel}</option>
													</c:if>
												</select>
											</div>
									</div>
									<div class="form-group">
										<label for="shortCode" class="col-sm-2 control-label">Outlet Type</label>
		       								<div class="col-sm-7">
		       								<select class="form-control select2 filters" id="outletTypeId" name="outletTypeId" style="width: 300px" required>
		       								<c:if test="${act eq 'add'}">
													<option value=""></option>
											</c:if>
												<c:forEach var="outletType" items="${outletTypes}">
													<option value="<c:out value="${outletType.id}" />"
														<c:if test="${model.outletTypeId == outletType.shortCode}">selected</c:if>>
														${outletType.shortCode} - ${outletType.englishName}</option>
												</c:forEach>
											</select>
											</div>
									</div>
									<div class="form-group">
	       								<label class="col-sm-2 control-label">No. of quotations per man-day</label>
	       								<div class="col-sm-4">
											<input name="quotationPerManDay" type="text" class="form-control" value="<c:out value="${model.quotationPerManDay}" />" maxlength="14" required/>
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
