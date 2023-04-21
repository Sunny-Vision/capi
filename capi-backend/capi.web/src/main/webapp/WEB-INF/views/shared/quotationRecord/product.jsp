<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
/**
 * Display product section
 * 
 * Controller permission required:
 * /shared/General/getProductImage
 */
%>
<div class="box box-primary">
	<div class="box-header with-border">
		<h3 class="box-title">${product.displayName}</h3>
	</div>
	<div class="box-body">
		<div class="form-horizontal product-form">
			<input name="product.productId" type="hidden" value="<c:out value="${product.productId}"/>" />
			<input name="product.productGroupId" type="hidden" value="<c:out value="${product.productGroupId}"/>" />
			<input name="product.allowProductChange" type="hidden" value="<c:out value="${product.allowProductChange}"/>" />
			<div class="hidden-product-post-container">
				<input name="product.barcode" type="hidden" value="<c:out value="${product.barcode}"/>"/>
				<input name="product.countryOfOrigin" type="hidden" value="<c:out value="${product.countryOfOrigin}"/>"/>
				<c:forEach items="${product.attributes}" var="attr" varStatus="attrLoop">
					<input name="product.attributes[${attrLoop.index}].productAttributeId" type="hidden" value="<c:out value="${attr.productAttributeId}"/>"/>
					<input name="product.attributes[${attrLoop.index}].name" type="hidden" value="<c:out value="${attr.name}"/>"/>
					<input name="product.attributes[${attrLoop.index}].mandatory" type="hidden" value="<c:out value="${attr.mandatory}"/>"/>
					<input name="product.attributes[${attrLoop.index}].attributeType" type="hidden" value="<c:out value="${attr.attributeType}"/>"/>
					<input name="product.attributes[${attrLoop.index}].sequence" type="hidden" value="<c:out value="${attr.sequence}"/>"/>
					<input name="product.attributes[${attrLoop.index}].option" type="hidden" value="<c:out value="${attr.option}"/>"/>
					<input name="product.attributes[${attrLoop.index}].value" type="hidden" value="<c:out value="${attr.value}"/>"/>
				</c:forEach>
			</div>
			<c:forEach items="${product.attributes}" var="attr" varStatus="attrLoop">
			<c:if test="${attrLoop.index < 3}">
			<div class="form-group">
				<label class="col-md-6 control-label">${attr.name}:</label>
				<div class="col-md-6">
					<p class="form-control-static product-form-attribute">${attr.value}</p>
				</div>
			</div>
			</c:if>
			</c:forEach>
			<div class="form-group">
				<label class="col-md-6 control-label">Country of origin:</label>
				<div class="col-md-6">
					<p class="form-control-static countryOfOrigin">${product.countryOfOrigin}</p>
				</div>
			</div>
			<div class="form-group">
				<label class="col-md-6 control-label">Barcode:</label>
				<div class="col-md-6">
					<p class="form-control-static barcode">${product.barcode}</p>
				</div>
			</div>
			<c:if test="${product.attributes.size() > 3}">
			<div class="form-group">
				<div class="col-md-12">
					<p class="form-control-status help-block"><span class="fa fa-info"></span> click compare for more</p>
				</div>
			</div>
			</c:if>
			
			
			<c:if test="${empty printView || !printView}" >
			<div class="form-group">
				<c:if test="${not model.readonly and product.allowProductChange}">
				<div class="col-md-6 align-center">
					<button class="btn btn-xs btn-info btn-change-product" type="button">Change product</button>
				</div>
				</c:if>
				<div class="col-md-6 align-center">
					<button class="btn btn-xs btn-info btn-compare-product" type="button">Compare</button>
				</div>
			</div>
			</c:if>
			
			<c:if test="${product.productId != null and product.showSpecDialog}">
			<div class="form-group">
				<div class="col-md-6 align-center">
					<button class="btn btn-xs btn-info btn-product-spec" type="button"
						data-productSpecDialog-product-id="${product.productId}">View Full Specification</button>
				</div>
			</div>
			</c:if>
			
			<div class="form-group">
				<div class="col-md-12">
					<small>Standard</small><br/>
					<c:choose>
						<c:when test="${product.photo1Path != null}">
							<img class="img-responsive viewer product-form-photo1"
								style="max-height: 50px"
								src="<c:url value='/shared/General/getProductImage'/>?productId=${product.productId}&amp;photoIndex=1&amp;bust=${niceDate.time}" />
						</c:when>
						<c:otherwise>
							<img class="img-responsive viewer product-form-photo1"
								style="max-height: 50px;"
								src="<c:url value='/resources/images/dummyphoto.png'/>" />
						</c:otherwise>
					</c:choose>
					<br />
					<c:if test="${not model.readonly}">
					<div class="product-photo-upload-container <c:if test="${!product.lastPostbackChangeResult.newProduct}">hide</c:if>">
					<input name="photo1" type="file" style="width:100%" />
					<br />
					</div>
					</c:if>
					
					<small>Latest</small><br/>
					<c:choose>
						<c:when test="${product.photo2Path != null}">
							<img class="img-responsive viewer product-form-photo2"
								style="max-height: 50px"
								src="<c:url value='/shared/General/getProductImage'/>?productId=${product.productId}&amp;photoIndex=2&amp;bust=${niceDate.time}" />
						</c:when>
						<c:otherwise>
							<img class="img-responsive viewer product-form-photo2"
								style="max-height: 50px"
								src="<c:url value='/resources/images/dummyphoto.png'/>" />
						</c:otherwise>
					</c:choose>
					<c:if test="${not model.readonly}">
					<br />
					<input name="photo2" type="file" style="width:100%" />
					</c:if>
				</div>
			</div>
		</div>
	</div>
</div>