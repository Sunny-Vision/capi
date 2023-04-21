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

	$mainForm.validate();

	<sec:authorize access="!hasPermission(#user, 512)">
		viewOnly();
	</sec:authorize>
});
</script>
</jsp:attribute>
	<jsp:body>
		<section class="content-header">
		<h1>Rank Maintenance</h1>
		</section>
		<section class="content">
		<form class="form-horizontal" action="<c:url value='/userAccountManagement/RankMaintenance/save?act='/>${act}" method="post" role="form" id="mainForm">
		    <input name="rankId" value="<c:out value="${model.rankId}" />" type="hidden" />
		    <div class="row">
				<div class="col-md-12">
					<!-- content -->
						<div class="box box-primary">
							<!-- /.box-header -->
							<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/userAccountManagement/RankMaintenance/home'/>">Back</a>
							</div>
							<!-- form start -->
								<div class="box-body">
       								<div class="form-group">
	       								<label class="col-sm-2 control-label">Rank Code</label>
	       								<div class="col-sm-4">
	       									<c:choose>
		       									<c:when test="${act eq 'add'}">
													<input name="code" type="text" class="form-control" value="<c:out value="${model.code}" />" maxlength="50" required />
												</c:when>
												<c:otherwise>
													<p class="form-control-static" >${model.code}</p>
													<input name="code" value="<c:out value="${model.code}" />" type="hidden" />
												</c:otherwise>
											</c:choose>
										</div>
									</div>
									<div class="form-group">
	       								<label class="col-sm-2 control-label">Rank Name</label>
	       								<div class="col-sm-4">
										<input name="name" type="text" class="form-control" value="<c:out value="${model.name}" />" maxlength="255" required/>
										</div>
									</div>
								</div>
							<!-- /.box-body -->
							<sec:authorize access="hasPermission(#user, 512)">
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
