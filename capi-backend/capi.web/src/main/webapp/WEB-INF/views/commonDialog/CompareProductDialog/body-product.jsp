<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div class="form-horizontal">
	<div class="form-group">
		<label class="col-md-12 text-center">${product.collectionDate}</label>
	</div>
	<div class="form-group">
		<div class="col-md-12">
			<div class="radio-photo" data-photos="
			<c:choose>
				<c:when test="${product.photo1Path != null}"><c:url value='/shared/General/getProductImage'/>?productId=${product.productId}&amp;photoIndex=1&amp;bust=${niceDate.time}</c:when>
				<c:otherwise><c:url value='/resources/images/dummyphoto.png'/></c:otherwise>
			</c:choose>,
			<c:choose>
				<c:when test="${product.photo2Path != null}"><c:url value='/shared/General/getProductImage'/>?productId=${product.productId}&amp;photoIndex=2&amp;bust=${niceDate.time}</c:when>
				<c:otherwise><c:url value='/resources/images/dummyphoto.png'/></c:otherwise>
			</c:choose>"></div>
		</div>
	</div>
	<div class="form-group">
		<label class="col-md-12 text-center">${product.displayName}</label>
	</div>
	<c:forEach items="${product.attributes}" var="attr">  
	<div class="form-group">
		<label class="col-md-6 control-label">${attr.name}:</label>
		<div class="col-md-6">
			<p class="form-control-static product-form-attribute">${attr.value}</p>
		</div>
	</div>
	</c:forEach>
	<div class="form-group">
		<label class="col-md-6 control-label">Country of origin</label>
		<div class="col-md-6">
			<p class="form-control-static">${product.countryOfOrigin}</p>
		</div>
	</div>
	<div class="form-group">
		<label class="col-md-6 control-label">Barcode</label>
		<div class="col-md-6">
			<p class="form-control-static">${product.barcode}</p>
		</div>
	</div>
	<div class="form-group">
		<label class="col-md-6 control-label">Product Remark</label>
		<div class="col-md-6">
			<p class="form-control-static">${product.productRemark}</p>
		</div>
	</div>
</div>