<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:if test="${product == null }">
<c:set var="product" value="${model.product}" scope="request" />
</c:if>
<div class="box no-border" >
	<div class="box-body ">
		<div class="row" style="margin-left: 0px; margin-right: 0px;">
			<div class="form-horizontal product-form">
				<div class="col-md-2">
					<small>Standard</small>
					<div class="product-photo-upload-container <c:if test="${!product.lastPostbackChangeResult.newProduct}">hide</c:if>">
					<input name="photo1" type="file" style="width:100%" />
					<br />
					</div>
					<c:choose>
						<c:when test="${product.photo1Path != null}">
							<img class="img-responsive viewer product-form-photo1"
								src="<c:url value='/shared/General/getProductImage'/>?productId=${product.productId}&amp;photoIndex=1&amp;bust=${niceDate.time}" />
						</c:when>
						<c:otherwise>
							<img class="img-responsive viewer product-form-photo1"
								src="<c:url value='/resources/images/dummyphoto.png'/>" />
						</c:otherwise>
					</c:choose>
					<br />
					<small>Latest</small><br/>
					<c:choose>
						<c:when test="${product.photo2Path != null}">
							<img class="img-responsive viewer product-form-photo2"
								src="<c:url value='/shared/General/getProductImage'/>?productId=${product.productId}&amp;photoIndex=2&amp;bust=${niceDate.time}" />
						</c:when>
						<c:otherwise>
							<img class="img-responsive viewer product-form-photo2"
								src="<c:url value='/resources/images/dummyphoto.png'/>" />
						</c:otherwise>
					</c:choose>
				</div>
				<div class="col-md-10">
					<input name="product.productId" type="hidden" value="<c:out value="${product.productId}"/>" />
					<input name="product.productGroupId" type="hidden" value="<c:out value="${product.productGroupId}"/>" />
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
					<div class="form-group">
						<label class="col-md-4 control-label">Country of origin:</label>
						<div class="col-md-8">
							<p class="form-control-static countryOfOrigin">${product.countryOfOrigin}</p>
						</div>
					</div>
					<c:forEach items="${product.attributes}" var="attr" varStatus="attrLoop">
					<div class="form-group">
						<label class="col-md-4 control-label">${attr.name}:</label>
						<div class="col-md-8">
							<p class="form-control-static product-form-attribute">${attr.value}</p>
						</div>
					</div>
					</c:forEach>
					<div class="form-group">
						<label class="col-md-4 control-label">Barcode:</label>
						<div class="col-md-8">
							<p class="form-control-static barcode">${product.barcode}</p>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>