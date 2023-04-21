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
	        			
	        			<c:if test="${not model.hideOutlet}">
							<c:set var="outlet" value="${model.outlet}" scope="request" />
							<%@include file="/WEB-INF/views/shared/quotationRecord/outlet.jsp"%>
						</c:if>
						
	        		</div>
	        	</div>
	        	
        </section>
	</jsp:body>
</t:layout>

