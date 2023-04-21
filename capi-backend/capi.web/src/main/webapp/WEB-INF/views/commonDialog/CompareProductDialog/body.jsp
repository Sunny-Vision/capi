<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="niceDate" class="java.util.Date"/>
<div class="row">
	<div class="col-md-6">
		<c:set var="product" value="${current}" scope="request" />
		<%@include file="/WEB-INF/views/commonDialog/CompareProductDialog/body-product.jsp"%>
	</div>
	<div class="col-md-6">
		<c:set var="product" value="${history}" scope="request" />
		<%@include file="/WEB-INF/views/commonDialog/CompareProductDialog/body-product.jsp"%>
	</div>
</div>