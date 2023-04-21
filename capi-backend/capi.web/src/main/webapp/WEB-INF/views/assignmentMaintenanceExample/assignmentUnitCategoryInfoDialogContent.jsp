<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:forEach items="${model}" var="item" varStatus="recordLoop">
	<div class="box box-primary">
		<c:choose>
		<c:when test="${item.verifyQuotation == 0}">
			<input name="categories[${recordLoop.index}].assignmentUnitCategoryInfoId" type="hidden" value="<c:out value="${item.assignmentUnitCategoryInfoId}"/>" />
			<input name="categories[${recordLoop.index}].unitCategory" type="hidden" value="<c:out value="${item.unitCategory}"/>" />
			<div class="box-header with-border">
				<h3 class="box-title"><i class="fa fa-arrows drag-handle"></i> ${item.unitCategory}</h3>
			</div>
			<div class="box-body">
				<div class="form-horizontal">
					<div class="form-group">
						<label class="col-md-2 control-label">Contact Person</label>
						<div class="col-md-10">
							<input name="categories[${recordLoop.index}].contactPerson" value="<c:out value="${item.contactPerson}"/>" class="form-control" />
						</div>
					</div>
					<div class="form-group">
						<label class="col-md-2 control-label">Remark</label>
						<div class="col-md-10">
							<input name="categories[${recordLoop.index}].remark" value="<c:out value="${item.remark}"/>" class="form-control" />
						</div>
					</div>
				</div>
			</div>
		</c:when>
		<c:otherwise>
			<div class="box-header with-border">
				<h3 class="box-title"><i class="fa fa-arrows drag-handle"></i> ${item.unitCategory}</h3>
			</div>
			<div class="box-body">
				<div class="form-horizontal">
					<div class="form-group">
						<label class="col-md-2 control-label">Contact Person</label>
						<div class="col-md-10">
							<p class="form-control-static"><c:out value="${item.contactPerson}"/></p>
						</div>
					</div>
					<div class="form-group">
						<label class="col-md-2 control-label">Remark</label>
						<div class="col-md-10">
							<p class="form-control-static"><c:out value="${item.remark}"/></p>
						</div>
					</div>
				</div>
			</div>
		</c:otherwise>
		</c:choose>
	</div>
</c:forEach>