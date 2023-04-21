<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
<jsp:useBean id="niceDate" class="java.util.Date"/>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/timepicker-css.jsp" %>
	</jsp:attribute>
	<jsp:attribute name="header">
		<style>
			#map { height: 500px }
			#attachmentContainer {
				min-height: 300px;
			}
			#attachmentContainer .attachment {
				margin-bottom: 10px;
			}
			.btn.pull-right {
				margin-left: 10px;
			}
		</style>
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/timepicker.jsp" %>
		<script>
			$(function() {
				
				var $mainForm = $('#mainForm');
				
				$mainForm.validate({
					rules : {
						sequence : {
							digits : true
						},
						outletTypeIds : {
							required : {
								depends : function(e) {
									return (!$('#allOutletType').prop('checked'));
								}
							}
						}
					}
				});
				
				$('#outletTypeIds').select2({
					closeOnSelect: false
				});
				
				if($('#allOutletType').prop('checked')) {
	    			$('#outletTypeIds').prop('disabled', true);
				}
				
				$("#allOutletType").on('change', function(){
		    		if (this.checked){
		    			$('#outletTypeIds').select2("val", "");
		    			$('#outletTypeIds').prop('disabled', true);
		    		}
		    		else{
		    			$('#outletTypeIds').prop('disabled', false);
		    		}
		    	});
				
				<sec:authorize access="!hasPermission(#user, 256)">
					viewOnly();
				</sec:authorize>
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Price Reason Maintenance</h1>
          <c:if test="${act != 'add'}">
				<div class="breadcrumb form-horizontal" style="width:240px">
					<div class="form-group" style="margin-bottom:0px">
			        	<div class="col-sm-5">Created Date:</div>
			        	<div class="col-sm-7">${commonService.formatDateTime(model.createdDate)}</div>
			        </div>
			        <div class="form-group" style="margin-bottom:0px">
			         	<div class="col-sm-5">Last Modified:</div>
			         	<div class="col-sm-7">${commonService.formatDateTime(model.modifiedDate)}</div>
			         </div>
		      	</div>
	      	</c:if>
        </section>
        
        <section class="content">
        	<form id="mainForm" action="<c:url value='/masterMaintenance/PriceReasonMaintenance/save'/>" method="post" role="form">
        		<input name="priceReasonId" value="<c:out value="${model.priceReasonId}" />" type="hidden" />
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/masterMaintenance/PriceReasonMaintenance/home'/>">Back</a>
							</div>
	       					<div class="box-body">
	       						<div class="form-horizontal">
	       							<div class="col-sm-12">
		       							<div class="form-group">
		       								<label class="col-sm-2 control-label">Price Reason Id</label>
		       								<div class="col-sm-4">
		       									<p class="form-control-static">${model.priceReasonId}</p>
		       									<input name="priceReasonId" value="<c:out value="${model.priceReasonId}" />" type="hidden" />
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-2 control-label">Description</label>
		       								<div class="col-sm-4">
												<input name="description" type="text" class="form-control" value="<c:out value="${model.description}" />" maxlength="2000" />
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-2 control-label">Reason Type</label>
											<div class="col-sm-10">
												<label class="radio-inline">
													<input type="radio" name="reasonType" value="1" <c:if test="${model.reasonType == '1'}">checked</c:if>> Price
												</label>
												<label class="radio-inline">
													<input type="radio" name="reasonType" value="2" <c:if test="${model.reasonType == '2'}">checked</c:if>> Discount
												</label>
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-2 control-label">Display Sequence</label>
		       								<div class="col-sm-4">
												<input name="sequence" type="number" class="form-control" value="<c:out value="${model.sequence}" />" required />
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-2 control-label">Outlet Type</label>
		       								<div class="col-sm-4">
												<select class="form-control select2" id="outletTypeIds" name="outletTypeIds" multiple="multiple" >
													<c:forEach var="outletType" items="${outletTypes}">
														<option value="<c:out value="${outletType.id}" />"
															<c:forEach var="modelOutletTypeId" items="${model.outletTypeIds}">
																<c:if test="${modelOutletTypeId == outletType.id}">selected</c:if>
															</c:forEach>
															>${outletType.shortCode} - ${outletType.englishName}</option>
													</c:forEach>
												</select>
											</div>
											<div class="col-sm-2">
												<label class="checkbox-inline">
													<input type="checkbox" name="allOutletType" id="allOutletType" <c:if test="${model.allOutletType == 'true'}">checked</c:if>> All
													<input type="hidden" value="on" name="_allOutletType"/>
												</label>
											</div>
										</div>
									</div>
								</div>
							</div>
	       					<sec:authorize access="hasPermission(#user, 256)">
								<div class="box-footer">
		        					<button type="submit" class="btn btn-info">Submit</button>
		       					</div>
	       					</sec:authorize>
						</div>
	        		</div>
	        	</div>
        	</form>
        </section>
	</jsp:body>
</t:layout>

