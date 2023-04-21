<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${FAIL_MESSAGE != null}">
	<div style="overflow: auto">
		<div class="alert alert-danger alert-dismissable" style="margin: 15px 15px 0 15px">
			<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
			<h4>
				<i class="icon fa fa-warning"></i> Alert!
			</h4>
			${FAIL_MESSAGE}
		</div>
	</div>
</c:if>
<c:if test="${SUCCESS_MESSAGE != null}">
	<div style="overflow: auto">
		<div class="alert alert-success alert-dismissable" style="margin: 15px 15px 0 15px">
			<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
			<h4>
				<i class="icon fa fa-check"></i> Information
			</h4>
			${SUCCESS_MESSAGE}
		</div>
	</div>
</c:if>