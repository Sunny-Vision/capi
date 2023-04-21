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
		
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-fixedHeader-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
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
		
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/userLookup.jsp" %>
		<script>
			$(function() {
				var $mainForm = $('#mainForm');
				
				$mainForm.validate({
					ignore: "",
					rules: {
						districtId: {
							required: {
								depends: function(e) {
									return !$('[name="isRUAAllDistrict"]').prop('checked') && $('[name="userId"]').val() == null;
								}
							}
						},
						userId: {
							required: {
								depends: function(e) {
									return !$('[name="isRUAAllDistrict"]').prop('checked') && $('[name="districtId"]').val() == null;
								}
							}
						}
					}
				});
				
				$('#districtId').select2ajax({
					allowClear: true,
					placeholder: '',
					width: '100%'
				});
				
				if($('[name="isRUAAllDistrict"]').prop('checked') == true) {
					$('#districtId').prop('disabled', true);
				}
				
				$('[name="isRUAAllDistrict"]').on('change', function() {
					var check = $(this);
					if (check.val() == "true"){
		    			$('#districtId').prop('disabled', true);
		    			$('#districtId').select2ajax('destroy');
		    			$('#districtId').find("option").remove();
		    			$('#districtId').select2ajax({
							allowClear: true,
							placeholder: '',
							width: '100%'
						});
		    		}
		    		else{
		    			$('#districtId').prop('disabled', false);
		    		}
					
					if (check.val() == "true") {
						$mainForm.valid();
					}
				});
				
				$("#userId").select2({closeOnSelect: false, width: "100%"});
				
				$('.searchStaffIds').userLookup({
					selectedIdsCallback: function(selectedIds) {
						$.post("<c:url value='/assignmentManagement/RUASetting/getStaffsName'/>", {ids: selectedIds}, function(result){
							var options = [];
							$('[name="userId"]').val(options);
							$(result).each(function(){
								options.push(this.userId);
							});
							$('[name="userId"]').val(options);
							$('[name="userId"]').trigger("change");
						});
					},
					alreadySelectedIdsCallback: function() {
						var ids = [];
						$("#userId option:selected").each(function(){
							ids.push(this.value)
						})
						return ids;
					},
					queryDataCallback: function (dataModel){
						dataModel.authorityLevel = 16;
					}
				});
				
				$('[name="districtId"],[name="userId"]').change(function () {
					$mainForm.valid();
				});
				
				<sec:authorize access="!(hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256))">
					viewOnly();
				</sec:authorize>
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>RUA Setting</h1>
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
        	<form id="mainForm" action="<c:url value='/assignmentManagement/RUASetting/save?act='/>${act}" method="post" role="form">
        		<input name="quotationId" value="<c:out value="${model.quotationId}" />" type="hidden" />
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/assignmentManagement/RUASetting/home'/>">Back</a>
							</div>
	       					<div class="box-body">
	       						<div class="form-horizontal">
	       							<div class="col-sm-12">
	       								<div class="col-sm-6">
			       							<div class="form-group">
			       								<label class="col-sm-3 control-label">Variety</label>
			       								<div class="col-sm-6 form-control-static">
													<p class="form inline">${model.unitCode}</p> - <p class="form inline">${model.unitName}</p>
													<input name="unitCode" value="<c:out value="${model.unitCode}" />" type="hidden" />
													<input name="unitName" value="<c:out value="${model.unitName}" />" type="hidden" />
												</div>
											</div>
			       							<div class="form-group">
			       								<label class="col-sm-3 control-label">Product</label>
			       								<div class="col-sm-6">
			       									<p class="form-control-static">${model.productAttribute}</p>
													<input name="productAttribute" value="<c:out value="${model.productAttribute}" />" type="hidden" />
												</div>
											</div>
											<div class="form-group">
			       								<label class="col-sm-3 control-label">Outlet</label>
			       								<div class="col-sm-6">
													<p class="form-control-static">${model.firmName}</p>
													<input name="firmName" value="<c:out value="${model.firmName}" />" type="hidden" />
												</div>
											</div>
											<div class="form-group">
			       								<label class="col-sm-3 control-label">Batch</label>
			       								<div class="col-sm-6">
													<p class="form-control-static">${model.batchCode}</p>
													<input name="batchCode" value="<c:out value="${model.batchCode}" />" type="hidden" />
												</div>
											</div>
											<div class="form-group">
												<label class="col-sm-3 control-label">RUA Setting</label>
												<div class="col-sm-6">
													<div class="">
														<label class="radio-inline">
															<input type="radio" name="isRUAAllDistrict" value="true" <c:if test="${model.isRUAAllDistrict == 'true'}">checked</c:if>> All District
														</label>
													</div>
													<div class="">
														<label class="radio-inline">
															<input type="radio" name="isRUAAllDistrict" value="false" <c:if test="${model.isRUAAllDistrict == 'false'}">checked</c:if>> District
														</label>
														<select class="form-control selec2" id="districtId" name="districtId"
															data-ajax-url="<c:url value='/assignmentManagement/RUASetting/queryDistrictSelect2'/>" >
															<c:if test="${model.districtId != null}">
																<option value="<c:out value="${model.districtId}" />" selected>${model.districtLabel}</option>
															</c:if>
														</select>
													</div>
												</div>
											</div>
											<div class="form-group">
			       								<label class="col-sm-3 control-label"></label>
			       								<div class="col-sm-6">
			       									<div class="input-group">
														<select class="form-control select2 searchStaffIds" name="userId" id="userId" multiple style="display:none" >
															<c:forEach items="${userFilterList}" var="userFilter">
																<option value="<c:out value="${userFilter.userId}" />"
																	<c:forEach var="id" items="${model.userId}">
																		<c:if test="${id == userFilter.userId}">selected</c:if>
																	</c:forEach>
																>${userFilter.staffCode} - ${userFilter.chineseName} (${userFilter.destination}) </option>
															</c:forEach>
														</select>
														<div class="input-group-addon searchStaffIds">
															<i class="fa fa-search"></i>
														</div>
													</div>
												</div>
											</div>
										</div>
										<div class="col-sm-6">
											<div class="form-group">
			       								<label class="col-sm-3 control-label">RUA since</label>
			       								<div class="col-sm-6">
													<p class="form-control-static">${model.ruaDate}</p>
													<input name="ruaDate" value="<c:out value="${model.ruaDate}" />" type="hidden" />
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
	       					<sec:authorize access="hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)">
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
