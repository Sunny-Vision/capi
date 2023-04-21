<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/select2-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp"%>
	</jsp:attribute>
	<jsp:attribute name="header">
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker.jsp"%>
		<script>
			$(document).ready(function(){
				var $mainForm = $('#mainForm');
				$mainForm.validate();
				
				$('.month-picker').datepicker({
					format: "mm-yyyy",
					startView: "months", 
				    minViewMode: "months",
				    autoclose: true
				});
				
				$('.select2ajax').select2ajax({
					width:'100%'
				});
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Quotation Record Maintenance</h1>
        </section>
        
        <section class="content">
        	<form id="mainForm" action="<c:url value='/assignmentManagement/QuotationRecordMaintenance/list'/>" method="get" role="form">
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
								<h3 class="box-title">Purpose Selection</h3>
							</div>
	       					<div class="box-body">
	       						<div class="form-horizontal">
	       							<div class="col-sm-12">
		       							<div class="form-group">
		       								<label class="col-sm-2 control-label">Reference Month</label>
		       								<div class="col-sm-4">
		       									<div class="input-group">
		       										<input type="text" name="referenceMonth" class="form-control month-picker" maxlength="7" required>
		       										<div class="input-group-addon">
		       											<i class="fa fa-calendar"></i>
		       										</div>
		       									</div>
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-2 control-label">Purpose</label>
		       								<div class="col-sm-4">
												<select name="purposeId" class="form-control select2ajax"
													data-ajax-url="<c:url value='/assignmentManagement/QuotationRecordMaintenance/queryPurposeSelect2'/>"
													required></select>
											</div>
										</div>
									</div>
								</div>
							</div>
	       					<%--<sec:authorize access="hasPermission(#user, 256)">--%>
								<div class="box-footer">
	        						<button type="submit" class="btn btn-info">Submit</button>
	       						</div>
	       					<%--</sec:authorize>--%>
						</div>
	        		</div>
	        	</div>
        	</form>
        </section>
	</jsp:body>
</t:layout>

