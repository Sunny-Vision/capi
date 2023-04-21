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
			div .inline {
				display: inline;
				margin-right: 10px;
			}
		</style>
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/timepicker.jsp" %>
		<script>
			$(function() {
				var $mainForm = $('#mainForm');
				
				$mainForm.validate();
				/*$mainForm.validate({
					groups: {
						checkboxes: "isJan isFeb isMar isApr isMay isJun isJul isAug isSep isOct isNov isDec"
					},
					errorPlacement: function(error, element) {
						if (element.attr("name") == "isJan" || element.attr("name") == "isFeb" || element.attr("name") == "isMar" ||
								element.attr("name") == "isApr" || element.attr("name") == "isMay" || element.attr("name") == "isJun" ||
								element.attr("name") == "isJul" || element.attr("name") == "isAug" || element.attr("name") == "isSep" ||
								element.attr("name") == "isOct" || element.attr("name") == "isNov" || element.attr("name") == "isDec") {
							error.insertAfter("#errorMessagePlace");
						} else {
							error.insertAfter(element);
						}
					}
				});*/
				
				/*$('.require_one').each(function () {
					$(this).rules('add', {
						require_from_group: [1, ".require_one"],
						messages: {
				            require_from_group: "<spring:message code='E00098' />"
				        }
				    });
				});*/
				
				<sec:authorize access="!hasPermission(#user, 256)">
					viewOnly();
				</sec:authorize>
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Pricing Month Maintenance</h1>
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
        	<form id="mainForm" action="<c:url value='/masterMaintenance/PricingMonthMaintenance/save?act='/>${act}" method="post" role="form" >
        		<input name="pricingFrequencyId" value="<c:out value="${model.pricingFrequencyId}" />" type="hidden" />
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/masterMaintenance/PricingMonthMaintenance/home'/>">Back</a>
							</div>
	       					<div class="box-body">
	       						<div class="form-horizontal">
	       							<div class="col-sm-12">
		       							<div class="form-group">
		       								<label class="col-sm-3 control-label">Pricing Month Description</label>
		       								<div class="col-sm-4">
												<input name="name" type="text" class="form-control" value="<c:out value="${model.name}" />" maxlength="2000" required />
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-3 control-label">Months</label>
		       								<div class="col-sm-9">
		       									<div class="inline">
													<input type="checkbox" class="require_one" name="isJan" id="isJan" <c:if test="${model.isJan == 'true'}">checked</c:if>> 1
													<input type="hidden" value="on" name="_isJan"/>
												</div>
												<div class="inline">
													<input type="checkbox" class="require_one" name="isFeb" id="isFeb" <c:if test="${model.isFeb == 'true'}">checked</c:if>> 2
													<input type="hidden" value="on" name="_isFeb"/>
												</div>
												<div class="inline">
													<input type="checkbox" class="require_one" name="isMar" id="isMar" <c:if test="${model.isMar == 'true'}">checked</c:if>> 3
													<input type="hidden" value="on" name="_isMar"/>
												</div>
												<div class="inline">
													<input type="checkbox" class="require_one" name="isApr" id="isApr" <c:if test="${model.isApr == 'true'}">checked</c:if>> 4
													<input type="hidden" value="on" name="_isApr"/>
												</div>
												<div class="inline">
													<input type="checkbox" class="require_one" name="isMay" id="isMay" <c:if test="${model.isMay == 'true'}">checked</c:if>> 5
													<input type="hidden" value="on" name="_isMay"/>
												</div>
												<div class="inline">
													<input type="checkbox" class="require_one" name="isJun" id="isJun" <c:if test="${model.isJun == 'true'}">checked</c:if>> 6
													<input type="hidden" value="on" name="_isJun"/>
												</div>
												<div class="inline">
													<input type="checkbox" class="require_one" name="isJul" id="isJul" <c:if test="${model.isJul == 'true'}">checked</c:if>> 7
													<input type="hidden" value="on" name="_isJul"/>
												</div>
												<div class="inline">
													<input type="checkbox" class="require_one" name="isAug" id="isAug" <c:if test="${model.isAug == 'true'}">checked</c:if>> 8
													<input type="hidden" value="on" name="_isAug"/>
												</div>
												<div class="inline">
													<input type="checkbox" class="require_one" name="isSep" id="isSep" <c:if test="${model.isSep == 'true'}">checked</c:if>> 9
													<input type="hidden" value="on" name="_isSep"/>
												</div>
												<div class="inline">
													<input type="checkbox" class="require_one" name="isOct" id="isOct" <c:if test="${model.isOct == 'true'}">checked</c:if>> 10
													<input type="hidden" value="on" name="_isOct"/>
												</div>
												<div class="inline">
													<input type="checkbox" class="require_one" name="isNov" id="isNov" <c:if test="${model.isNov == 'true'}">checked</c:if>> 11
													<input type="hidden" value="on" name="_isNov"/>
												</div>
												<div class="inline">
													<input type="checkbox" class="require_one" name="isDec" id="isDec" <c:if test="${model.isDec == 'true'}">checked</c:if>> 12
													<p id="errorMessagePlace"></p>
													<input type="hidden" value="on" name="_isDec"/>
												</div>
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

