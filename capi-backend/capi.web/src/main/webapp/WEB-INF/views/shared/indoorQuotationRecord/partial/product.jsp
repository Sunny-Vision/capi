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
<c:if test="${product == null }">
<c:set var="product" value="${model.product}" scope="request" />
</c:if>
<c:if test="${quotationRecord == null }">
<c:set var="quotationRecord" value="${model.quotationRecord}" scope="request" />
</c:if>
<c:choose>
	<c:when test="${quotationRecord.isProductChange()}">
<div class="box box-primary">
	</c:when>
	<c:otherwise>
<div class="box box-primary collapsed-box">
	</c:otherwise>
</c:choose>
	<div class="box-header with-border">
		<h3 class="box-title">
			Product - 
			Name: <strong>${product.displayName}</strong>,
			<c:forEach items="${product.attributes}" var="attr" varStatus="attrLoop">
			<c:if test="${attrLoop.index <= 1}">
				Attribute<c:out value="${attrLoop.index+1}"/>: <strong><c:out value="${attr.name}"/> - <c:out value="${attr.value}"/></strong>,
			</c:if>
			</c:forEach>
		</h3>
		<div class="box-tools pull-right">
			<c:if test="${quotationRecord.isProductChange()}">
			<i class="fa fa-exclamation-triangle header-mark"></i>
			</c:if>
			<button class="btn btn-box-tool btn-header-collapse" type="button" data-widget="collapse">
			<c:choose>
				<c:when test="${quotationRecord.isProductChange()}">
			<i class="fa fa-minus"></i>
				</c:when>
				<c:otherwise>
			<i class="fa fa-plus"></i>
				</c:otherwise>
			</c:choose>
			</button>
		</div>
	</div>
	<div class="box-body" <c:choose>
				<c:when test="${quotationRecord.isProductChange()}"></c:when>
				<c:otherwise>style="display: none;"</c:otherwise>
			</c:choose> >
		<div class="form-horizontal">
			<div class="row">
				<div class="col-md-6">
					<%@include file="/WEB-INF/views/shared/indoorQuotationRecord/partial/productDetails.jsp" %>
				</div>
				<div class="col-md-6 productHistory">
					
				</div>
			</div>
		</div>
	</div>
</div>