<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%
/**
 * Display outlet section
 * 
 * Controller permission required:
 * /shared/General/getOutletImage
 */
%>
<c:if test="${outlet == null}">
<c:set var="outlet" value="${model.outlet}" scope="request" />
</c:if>
<c:if test="${quotationRecord == null }">
<c:set var="quotationRecord" value="${model.quotationRecord}" scope="request" />
</c:if>
<c:set var="openOutlet" value="${false}" scope="request" />
<c:if test="${quotationRecord.isNewRecruitment() || quotationRecord.isNewOutlet()}">
<c:set var="openOutlet" value="${true}" scope="request" />
</c:if>
<c:choose>
	<c:when test="${openOutlet}">
<div class="box box-primary">
	</c:when>
	<c:otherwise>
<div class="box box-primary collapsed-box">
	</c:otherwise>
</c:choose>
	<div class="box-header with-border">
		<h3 class="box-title">
			Outlet - 
			<span class="outlet-header-name">
				Name: <strong>${outlet.name}</strong>, 
				Outlet Type: <strong>${outlet.quotationRecordOutletType}</strong>,
				Outlet Discount Remark: <strong>${outlet.discountRemark}</strong>,
			</span>
		</h3>
		<div class="box-tools pull-right">
		 	<c:if test="${openOutlet}"><i class="fa fa-exclamation-triangle header-mark"></i></c:if>
			<button class="btn btn-box-tool btn-header-collapse" type="button" data-widget="collapse">
			<c:choose>
				<c:when test="${openOutlet}">
			<i class="fa fa-minus"></i>
				</c:when>
				<c:otherwise>
			<i class="fa fa-plus"></i>
				</c:otherwise>
			</c:choose>
			</button>
		</div>
	</div>
	<div class="box-body" <c:if test="${!openOutlet}">style="display: none;"</c:if>>
		<div class="form-horizontal">
			<div class="row">
				<div class="col-md-6">
					<%@include file="/WEB-INF/views/shared/indoorQuotationRecord/partial/outletDetails.jsp" %>
				</div>
				<div class="col-md-6 outletHistory">
				</div>
			</div>
		</div>
	</div>
</div>