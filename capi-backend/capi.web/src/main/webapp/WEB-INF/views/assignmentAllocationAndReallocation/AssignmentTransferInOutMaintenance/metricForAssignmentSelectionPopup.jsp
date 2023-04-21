<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="col-md-12 bg-info" style="margin-bottom: 5px">
	<div class="row">
		<div class="col-md-2">
			Selected Assignments:
		</div>
		<div class="col-md-1"><c:out value="${model.selectedAssignments}"/></div>
		<div class="col-md-2">
			Selected Quotations:
		</div>
		<div class="col-md-1"><c:out value="${model.selectedQuotations}"/></div>
		<div class="col-md-3">
			Actual Release Man-Day:
		</div>
		<div class="col-md-2 actual-release-man-days"><c:out value="${model.actualReleaseManDaysBD}"/></div>
	</div>
</div>