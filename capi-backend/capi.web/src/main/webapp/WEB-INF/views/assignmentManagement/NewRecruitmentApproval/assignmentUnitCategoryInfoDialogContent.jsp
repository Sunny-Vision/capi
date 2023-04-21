<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:forEach items="${model}" var="item" varStatus="recordLoop">
	<div class="box box-primary">
			<div class="box-header with-border">
				<h3 class="box-title">${item.unitCategory}</h3>
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
	</div>
</c:forEach>