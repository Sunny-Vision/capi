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
			factor : {
				number: true,
				min :0
				
			}
		},
		messages: {
			factor: {
				number: "<spring:message code='E00071' />"
				
			}
		}

	});

	$('#baseUOMId').select2ajax();
	$('#targetUOMId').select2ajax();
	
	<sec:authorize access="!hasPermission(#user, 256)">
		viewOnly();
	</sec:authorize>
});
</script>
</jsp:attribute>
	<jsp:body>
		<section class="content-header">
		<h1>UOM Conversion Maintenance</h1>
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
		<form class="form-horizontal" action="<c:url value='/masterMaintenance/UOMConversionMaintenance/save?act='/>${act}" method="post" role="form" id="mainForm">
		    <input name="uomConversionId" value="<c:out value="${model.uomConversionId}" />" type="hidden" />
		    <div class="row">
				<div class="col-md-12">
					<!-- content -->
						<div class="box box-primary">
							<!-- /.box-header -->
							<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/masterMaintenance/UOMConversionMaintenance/home'/>">Back</a>
							</div>
							<!-- form start -->
								<div class="box-body">
	       							<div class="form-group">
										<label for="baseUOMId" class="col-sm-2 control-label">Base UOM</label>
		       								<div class="col-sm-7">
												<select class="form-control select2" id="baseUOMId" name="baseUOMId" style="width: 300px"
													data-ajax-url="<c:url value='/masterMaintenance/UOMConversionMaintenance/queryUomSelect2'/>" required>
													<c:if test="${model.baseUOMId != null}">
														<option value="<c:out value="${model.baseUOMId}" />" selected>${model.baseUOMId} - ${model.baseUOMChineseName}</option>
													</c:if>
												</select>
											</div>
									</div>
									<div class="form-group">
										<label for="targetUOMId" class="col-sm-2 control-label">Target UOM</label>
		       								<div class="col-sm-7">
												<select class="form-control select2" id="targetUOMId" name="targetUOMId" style="width: 300px"
													data-ajax-url="<c:url value='/masterMaintenance/UOMConversionMaintenance/queryUomSelect2'/>" required>
													<c:if test="${model.targetUOMId != null}">
														<option value="<c:out value="${model.targetUOMId}" />" selected>${model.targetUOMId} - ${model.targetUOMChineseName}</option>
													</c:if>
												</select>
											</div>
									</div>
									<div class="form-group">
	       								<label class="col-sm-2 control-label">Factor</label>
	       								<div class="col-sm-4">
										<input name="factor" type="text" class="form-control" value="<c:out value="${model.factor}" />" maxlength="14" required/>
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
