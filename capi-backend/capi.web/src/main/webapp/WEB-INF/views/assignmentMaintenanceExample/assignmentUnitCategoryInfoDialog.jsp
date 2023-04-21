<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div class="modal fade" role="dialog">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<form method="post" action="<c:url value="/assignmentMaintenanceExample/saveAssignmentUnitCategory"/>">
			<input name="tab" type="hidden" value="Normal"/>
			<input name="assignmentId" type="hidden" value="<c:out value="${assignmentId}"/>"/>
			<input name="userId" type="hidden" value="<c:out value="${userId}"/>"/>
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">X</span></button>
				<h4 class="modal-title">Assignment Unit Category</h4>
			</div>
			<div class="modal-body">
				<!-- content -->
				<div class="content-container"></div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default pull-left" data-dismiss="modal">Close</button>
				<sec:authorize access="hasPermission(#user, 256)">
				<button type="submit" class="btn btn-primary modal-submit">Submit</button>
				</sec:authorize>
			</div>
			</form>
		</div><!-- /.modal-content -->
	</div><!-- /.modal-dialog -->
</div>