<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
<t:layout>
	<jsp:attribute name="header">
		<style>
		.custom-grid .form-control {
			height: 100px;
			margin-bottom: 10px;
			text-align: center;
			font-size: 20pt;
		}
		</style>
		<script>
			$(function() {
				<sec:authorize access="!hasPermission(#user, 256)">
					viewOnly();
				</sec:authorize>
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Discount Calculator Maintenance</h1>
        </section>
        
        <section class="content">
        	<div class="row">
        		<div class="col-md-12">
        			<!-- general form elements -->
        			<div class="box box-primary">
        				<%--
        				<div class="box-header with-border">
        					<div class="box-title"></div>
        				</div><!-- /.box-header -->
        				--%>
        				<form action="<c:url value='/masterMaintenance/DiscountCalculatorMaintenance/save'/>" method="post" role="form">
        					<div class="box-body">
				        		<div class="row custom-grid">
				        			<c:forEach var="value" items="${itemlist.values}">
					        			<div class="col-md-4">
					        				<input name="values" type="text" class="form-control" value="<c:out value="${value}" />" />
					        			</div>
				        			</c:forEach>
				        		</div>
        					</div><!-- /.box-body -->
        					<sec:authorize access="hasPermission(#user, 256)">
	        					<div class="box-footer">
	        						<button type="submit" class="btn btn-info">Submit</button>
	        					</div>
        					</sec:authorize>
			        	</form>
        			</div><!-- /.box -->
        		</div>
        	</div>
        </section>		
	</jsp:body>
</t:layout>

