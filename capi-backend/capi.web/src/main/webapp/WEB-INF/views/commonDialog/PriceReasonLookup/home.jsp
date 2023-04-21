<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div class="modal fade" role="dialog">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">X</span></button>
				<h4 class="modal-title">
					<c:choose><c:when test="${lookuptitle =='Discount'}">Discount Remark</c:when><c:otherwise>Reason Lookup</c:otherwise></c:choose> 
				</h4>
			</div>
			<div class="modal-body reason-lookup">
				<!-- content -->
				<div class="reason-container">
					<c:forEach items="${list}" var="reason" varStatus="reasonLoop">
						<c:if test="${reasonLoop.index % 6 == 0}">
						<div class="row">
						</c:if>
						<div class="col-md-2">
							<button class="btn btn-info"><c:out value="${reason.description}"/></button>
						</div>
						<c:if test="${(reasonLoop.index + 1) % 6 == 0}">
						</div>
						</c:if>
					</c:forEach>
					<c:if test="${fn:length(list) > 0 && (fn:length(list) % 6) != 0}">
						</div>
					</c:if>
				</div>
				
				<div class="form-horizontal">
					<div class="form-group discount-remark-container">
						<label class="control-label col-md-1">
						<c:choose><c:when test="${lookuptitle =='Discount'}">Discount Remark</c:when><c:otherwise>Reason</c:otherwise></c:choose> 
						</label>
						<div class="col-md-11">
							<input type="text" class="form-control reason-input"/>
						</div>
					</div>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default pull-left modal-cancel" data-dismiss="modal">Cancel</button>
				<button type="button" class="btn btn-primary modal-confirm">Confirm</button>
			</div>
		</div><!-- /.modal-content -->
	</div><!-- /.modal-dialog -->
</div>