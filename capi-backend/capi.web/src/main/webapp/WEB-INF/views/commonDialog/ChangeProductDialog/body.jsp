<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="niceDate" class="java.util.Date"/>
<form class="form-horizontal">
	<c:forEach items="${model.attributes}" var="attr" varStatus="attrLoop">
	<div class="form-group">
		<input name="product.attributes[${attrLoop.index}].productAttributeId" type="hidden" value="<c:out value="${attr.productAttributeId}" />"/>
		<input name="product.attributes[${attrLoop.index}].sequence" type="hidden" value="<c:out value="${attr.sequence}" />"/>
		<label class="col-sm-3 control-label">${attr.name}</label>
		<div class="col-sm-9">
			<select name="product.attributes[${attrLoop.index}].value"
				data-attribute-type="${attr.attributeType}"
				data-product-attribute-id="${attr.productAttributeId}"
				data-name="${attr.name}"
				class="form-control" ${attr.isMandatory ? 'required' : ''}>
				<c:choose>
					<c:when test="${attr.attributeType == 1}">
						<option value="<c:out value="${attr.value}" />">${attr.value}</option>
					</c:when>
					<c:when test="${attr.attributeType == 2}">
						<option></option>
						<c:set var="attrValue" value="${fn:trim(attr.value)}"/>
						<c:set var="options" value="${fn:split(attr.option, ';')}"/>
						<c:forEach items="${options}" var="option">
							<c:set var="option" value="${fn:trim(option)}"/>
							<option ${attrValue == option ? 'selected' : ''}>${option}</option>
						</c:forEach>
					</c:when>
					<c:when test="${attr.attributeType == 3}">
						<option></option>
						<c:set var="attrValue" value="${fn:trim(attr.value)}"/>
						<c:set var="options" value="${fn:split(attr.option, ';')}"/>
						<c:set var="selected" value="false"/>
						<c:forEach items="${options}" var="option">
							<c:set var="option" value="${fn:trim(option)}"/>
							<option ${attrValue == option ? 'selected' : ''}>${option}</option>
							<c:if test="${attrValue == option}">
								<c:set var="selected" value="true"/>
							</c:if>
						</c:forEach>
						<c:if test="${not selected}">
							<option value="<c:out value="${attr.value}" />" selected>${attr.value}</option>
						</c:if>
					</c:when>
				</c:choose>
			</select>
		</div>
	</div>
	</c:forEach>
	<div class="form-group">
		<label class="col-sm-3 control-label">Country of origin</label>
		<input name="selectedCountry" type="hidden" value="<c:out value="${model.countryOfOrigin}" />" />
		<div class="col-sm-9">
			<select name="product.countryOfOrigin" class="form-control select2" required>
				<c:forEach items="${model.countryOfOrigins}" var="country">
					<option ${model.countryOfOrigin == country ? 'selected' : ''}>${country}</option>
				</c:forEach>
			</select>
		</div>
	</div>
	<div class="form-group">
		<label class="col-sm-3 control-label">Barcode</label>
		<div class="col-sm-9">
			<input name="product.barcode" class="form-control" value="<c:out value="${model.barcode}" />" />
		</div>
	</div>
	<div class="form-group">
		<label class="col-sm-3 control-label"></label>
		<div class="col-sm-9">
			<button class="btn btn-primary btn-match-product" type="button">Match product</button>
		</div>
	</div>
</form>
<div class="row">
	<div class="col-md-6">
		<c:choose>
			<c:when test="${model.hasPhoto1}">
				<img class="img-responsive viewer"
					src="<c:url value='/shared/General/getProductImage'/>?productId=${model.productId}&amp;photoIndex=1&amp;bust=${niceDate.time}" />
			</c:when>
			<c:otherwise>
				<img class="img-responsive"
					src="<c:url value='/resources/images/dummyphoto.png'/>" />
			</c:otherwise>
		</c:choose>
	</div>
	<div class="col-md-6">
		<c:choose>
			<c:when test="${model.hasPhoto2}">
				<img class="img-responsive viewer"
					src="<c:url value='/shared/General/getProductImage'/>?productId=${model.productId}&amp;photoIndex=2&amp;bust=${niceDate.time}" />
			</c:when>
			<c:otherwise>
				<img class="img-responsive"
					src="<c:url value='/resources/images/dummyphoto.png'/>" />
			</c:otherwise>
		</c:choose>
	</div>
</div>