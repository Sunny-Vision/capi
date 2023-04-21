<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
<jsp:useBean id="niceDate" class="java.util.Date"/>
<t:layout plainLayout="true">
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/timepicker-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
		<%@include file="/WEB-INF/views/shared/quotationRecord/page-css.jsp" %>
	</jsp:attribute>
	<jsp:attribute name="header">
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker.jsp"%>
		<%@include file="/WEB-INF/views/includes/timepicker.jsp" %>
		<%@include file="/WEB-INF/views/includes/handlebars.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/radioPhotos.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/imageViewer.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/productLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/changeListener.jsp"%>
		<%@include file="/WEB-INF/views/includes/commonDialog/textViewer.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/outletAttachmentDialog.jsp"%>
		<%@include file="/WEB-INF/views/shared/quotationRecord/page-js.jsp" %>
		<script>
			$(function() {
				var $mainForm = $('#mainForm');
				
				var quotationRecordFormContainerPlugin = $('.quotation-current-container .quotation-record-form-container').quotationRecordFormContainer('plugin');
				
				$mainForm.validate({
					ignore: $.proxy(quotationRecordFormContainerPlugin.formValidateIgnore, quotationRecordFormContainerPlugin),
					invalidHandler: $.proxy(quotationRecordFormContainerPlugin.formValidateInvalidHandler, quotationRecordFormContainerPlugin)
				});
				
				Datepicker();
				
				$('.btn-header-collapse').click(function() {
					var $btn = $(this);
					var $box = $btn.closest('.box-tools');
					if ($btn.find('.fa-plus').length > 0)
						$box.find('.header-mark').hide();
					else
						$box.find('.header-mark').show();
				});
				
				$('#mainForm').changeListener({
					backButtons: ['.btn-back'],
					changeCallback: function() {
						$('[name=dirty]').val('true');
					}
				});
				if ($('[name=dirty]').val() == 'true') {
					$('#mainForm').changeListener('setChanged');
				}
			});
		</script>
	</jsp:attribute>
	<jsp:body>
	<%--
		<section class="content-header">
          <h1>Quotation Record Maintenance</h1>
			<div class="breadcrumb form-horizontal" style="width:240px">
				<div class="form-group" style="margin-bottom:0px">
		        	<div class="col-sm-5">Created Date:</div>
		        	<div class="col-sm-7">${commonService.formatDateTime(model.quotationRecord.createdDate)}</div>
		        </div>
		        <div class="form-group" style="margin-bottom:0px">
		         	<div class="col-sm-5">Last Modified:</div>
		         	<div class="col-sm-7">${commonService.formatDateTime(model.quotationRecord.modifiedDate)}</div>
		         </div>
	      	</div>
        </section>
         --%>
        
        <section class="content">
        	
        		<input name="dirty" value="<c:out value="${model.dirty ? 'true' : 'false'}"/>" type="hidden"/>
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<div class="box box-primary ">
	        				<div class="box-header with-border">
	        					<h3 class="box-title">&nbsp;</h3>
								<c:if test="${not empty model.pointToNote || not empty model.verificationRemark || not empty model.rejectReason || not empty model.peCheckRemark}">
								<div class="box-tools pull-right">
									<i class="fa fa-exclamation-triangle header-mark"></i>
	        						<button class="btn btn-box-tool btn-header-collapse" type="button" data-widget="collapse"><i class="fa fa-plus"></i></button>
	        					</div>
								</c:if>
								<%-- hide Point to note row when empty, show ! after collapse if have content
	        					<div class="box-tools pull-right">
	        						<button class="btn btn-box-tool" type="button" data-widget="collapse"><i class="fa fa-minus"></i></button>
	        					</div>
	        					--%>
							</div>
							<c:if test="${model.pointToNote != null || model.verificationRemark != null || model.rejectReason != null || model.peCheckRemark != null}">
	       					<div class="box-body" >
	       						<div class="form-horizontal">
       								<div class="form-group">
	       								<label class="col-md-1 control-label">Point to note:</label>
	       								<div class="col-md-2">
											<p class="form-control-static">${model.pointToNote}</p>
										</div>
	       								<label class="col-md-1 control-label">Verification Remark:</label>
	       								<div class="col-md-2">
											<p class="form-control-static">${model.verificationRemark}</p>
										</div>
										<c:if test="${model.quotationRecord.status == 'Rejected'}">
	       								<label class="col-md-1 control-label">Reject Reason:</label>
	       								<div class="col-md-2">
											<p class="form-control-static">${model.rejectReason}</p>
										</div>
										</c:if>
	       								<label class="col-md-1 control-label">PE Check Remark:</label>
	       								<div class="col-md-2">
											<p class="form-control-static">${model.peCheckRemark}</p>
										</div>
									</div>
								</div>
							</div>
        					</c:if>
						</div>
						<%--
	        			<c:if test="${not model.hideOutlet}">
							<c:set var="outlet" value="${model.outlet}" scope="request" />
							<%@include file="/WEB-INF/views/shared/quotationRecord/outlet.jsp"%>
						</c:if>
						 --%>
						
						<div class="row">
							<div class="col-md-4">
								<c:set var="product" value="${model.product}" scope="request" />
								<%@include file="/WEB-INF/views/shared/quotationRecord/product.jsp"%>
							</div>
							<div class="col-md-8">
								<div class="quotation-current-container">
									<%@include file="/WEB-INF/views/shared/quotationRecord/editForm.jsp"%>
								</div>
							</div>							
						</div>
	        		</div>
	        	</div>
	        	
	        	
	        	<c:if test="${not empty subPrice }">
	        		<div class="box" >
						<div class="box-header with-border">
							<h3 class="box-title">Sub Price</h3>
						</div>
						<div class="box-body">				
							<table class="table 1table-striped1 table-bordered table-hover" >
								<thead>
								<tr>
									<c:forEach items="${subPrice.fieldList1}" var="field">
										<th>${field.fieldName}</th>
									</c:forEach>
									<c:if test="${!subPrice.type1.hideNPrice }" >
										<th>N Price</th>
									</c:if>
									<c:if test="${!subPrice.type1.hideSPrice }" >
										<th>S Price</th>
									</c:if>
									<c:if test="${!subPrice.type1.hideDiscount }" >
										<th>Discount</th>
									</c:if>
								</tr>
								</thead>
								<tbody>
									<c:forEach items="${subPrice.rows1}" var="row" >
										<tr>
											<c:forEach items="${row.columns}" var="column">
												<td>
													<c:choose>
														<c:when test="${column.subPriceFieldMapping.subPriceField.fieldType eq 'Date'}">
															<c:out value="${commonService.formatDate(column.columnValue)}" />
														</c:when>
														<c:when test="${column.subPriceFieldMapping.subPriceField.fieldType eq 'Time'}">
															<c:out value="${commonService.formatTime(column.columnValue)}" />
														</c:when>
														<c:when test="${column.subPriceFieldMapping.subPriceField.fieldType eq 'Checkbox'}">
															<c:choose>
																<c:when test="${column.columnValue eq '1' }" >Y</c:when>
																<c:otherwise>N</c:otherwise>
															</c:choose>
														</c:when>
														<c:otherwise>
															<c:out value="${column.columnValue}" />
														</c:otherwise>
													</c:choose>
												</td>
											</c:forEach>
											<c:if test="${!subPrice.type1.hideNPrice }" >
												<td><c:out value="${row.nPrice}" /></td>
											</c:if>
											<c:if test="${!subPrice.type1.hideSPrice }" >
												<td><c:out value="${row.sPrice}" /></td>
											</c:if>
											<c:if test="${!subPrice.type1.hideDiscount }" >
												<td><c:out value="${row.discount}" /></td>
											</c:if>
										</tr>
									</c:forEach>
								</tbody>
							</table>
							
						</div>
					</div>
				</c:if>
        	
        </section>
	</jsp:body>
</t:layout>

