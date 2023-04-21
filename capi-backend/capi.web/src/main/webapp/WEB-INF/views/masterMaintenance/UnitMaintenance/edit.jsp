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
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/jstree-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/unitLookup-css.jsp" %>
		<style>
		.form-inline .form-control.input-num {
			width: 5em;
		}
		</style>
	</jsp:attribute>
	<jsp:attribute name="header">
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker.jsp"%>
		<%@include file="/WEB-INF/views/includes/jstree.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/unitLookup.jsp" %>
		<script>
			$(function() {
				var $mainForm = $('#mainForm');
				
				jQuery.validator.addMethod("select2MaxLength", function(value, element, params) {
						var maxlength = params;
						var val = $(element).val() == undefined ? '' : $(element).val();
						return val.length == maxlength;
					}, jQuery.validator.format('<spring:message code='E00097' />'));
				
				
				$mainForm.validate({
					invalidHandler: function(event, validator) {
						var errors = validator.numberOfInvalids();
						if(errors) {
							$('html, body').animate({
								scrollTop: $(validator.errorList[0].element).offset().top
							}, 1000);
						} else {
						}
					}
				});
				Modals.init();
				Datepicker();
				
				$('.lookup-onchange').each(function() {
					var $section = $(this);
					var url = $section.data('url');
					var $code = $section.find('.code');
					var $chineseName = $section.find('.chinese-name');
					var $englishName = $section.find('.english-name');
					var $compilationMethod = $section.find('.compilation-method');
					$code.change(function() {
						var code = $(this).val();
						if (code == null || code.length == 0) {
							$chineseName.val('');
							$englishName.val('');
							return;
						}
						$.get(url, { code : code, basePeriod: $("[name='cpiBasePeriod']").val() }, function(data) {
							if (data == null) return;
							$chineseName.val(data.chineseName);
							$englishName.val(data.englishName);
							if ($compilationMethod.length > 0) {
								$compilationMethod.val(data.compilationMethod);
							}
						});
					});
				});
				
				$('.select2ajax-selected-id').each(function() {
					var $select = $(this);
					var ajaxUrl = $select.data('ajaxUrl');
					var disableAboveCodeName = $select.data('disableAboveCodeName');
					var $target = $('[name="' + disableAboveCodeName + '"]:visible');
					$select.select2ajax({
						allowClear: true,
						placeholder: '',
						ajax:{
							data: function (params) {
						    	params.cpiBasePeriod = $("[name=cpiBasePeriod]").val();
						    	params.aboveCode = $target.val();
						    	return params;
							}
						}
					});
					var selectedId = $(this).data('selectedId');
					if (selectedId == '' || selectedId == null) return true;
					
					var singleUrl = $select.data('singleUrl');
					$.get(singleUrl,
						{
							id: +selectedId
						},
						function(data) {
							var option = new Option(data, selectedId);
							option.selected = true;
							$select.append(option);
							$select.trigger('change');
					});
				});
				
				$('.select2ajax-selected-ids').each(function() {
					var $select = $(this);
					$select.select2ajax({
						allowClear: true,
						placeholder: '',
						multiple: true
					});
					var selectedIds = $(this).data('selectedIds');
					if (selectedIds == '' || selectedIds == null) return true;

					var multipleUrl = $select.data('multipleUrl');
					$.get(multipleUrl,
						{
							ids: selectedIds
						},
						function(data) {
							for (var i = 0; i < data.results.length; i++) {
								var option = new Option(data.results[i].text, data.results[i].id);
								option.selected = true;
								$select.append(option);
							}
							$select.trigger('change');
						}
					);
				});
				
				$('.select2ajax-selected-id').css('visibility','hidden')
				
				var $purposeId = $('[name="purposeId"]');
				/*$('[name="MRPS"]').change(function() {
					if ($('[name="MRPS"]:checked').val() == 'True')
						$purposeId.prop('disabled', true);
					else
						$purposeId.prop('disabled', false);
				}).trigger('change');*/
				
				var $seasonality = $('[name="seasonality"]');
				$('[name="seasonality"]').change(function() {
					if ($('[name="seasonality"]:checked').val() == 4)
						$('.seasonality-occasional').show();
					else
						$('.seasonality-occasional').hide();
				}).trigger('change');
				

				$('.lookup').each(function() {
					var bottomEntityClass = $(this).data('bottomEntityClass');
					var $select = $(this).closest('.input-group').find('select');
					var singleUrl = $select.data('singleUrl');
					$(this).unitLookup({
						selectedIdsCallback: function(selectedIds) {
							$select.empty();
							$.get(singleUrl, { id: selectedIds[0] }, function(data) {
								var option = new Option(data.code, data.code);
								$("[name=cpiBasePeriod]").attr("readonly","readonly").val(data.cpiBasePeriod);
								option.selected = true;
								$select.append(option);
								$select.trigger('change');
							});
						},
						multiple: false,
						bottomEntityClass: bottomEntityClass
					});
				});
				
				$('.replace-by-code').each(function() {
					var $select = $(this);
					var singleUrl = $select.data('singleUrl');
					$select.on('select2:selecting', function(e) {
						if (e.params.args.data.id == e.params.args.data.text){
							if ($("[name=cpiBasePeriod]").val() != "")
								$("[name=cpiBasePeriod]").attr("readonly","readonly");
							return true;
						}
						
						var id = e.params.args.data.id;
						$.get(singleUrl, { id: id }, function(data) {
							var option = new Option(data.code, data.code);
							$("[name=cpiBasePeriod]").attr("readonly","readonly").val(data.cpiBasePeriod);
							
							option.selected = true;
							$select.append(option);
							$select.trigger('change');
						});
						$select.select2('close');
						return false;
					});
				});
				
				if ($('[name="id"]').val() == '') {
					$('.disable-above-code').change(function() {
						var disableAboveCodeUrl = $(this).data('disableAboveCodeUrl');
						var disableAboveCodeName = $(this).data('disableAboveCodeName');
						var code = $(this).val();
						var cpiBasePeriod = $("[name='cpiBasePeriod']").val();
						$.get(disableAboveCodeUrl, { code: code, cpiBasePeriod: cpiBasePeriod }, function(data) {
							if (data == null || data == '') return;
							var $target = $('[name="' + disableAboveCodeName + '"]:visible');
							$target.empty();
							$target.append('<option selected>' + data + '</option>');
							$target.trigger('change');
							$target.prop('disabled', true);
							
							var $hidden = $('[name="' + disableAboveCodeName + '"]:hidden');
							if ($hidden.length == 0) {
								$target.closest('.input-group').find('.input-group-addon').hide();
								$target.after('<input type="hidden" name="' + disableAboveCodeName + '" value="' + data + '"/>');
								$hidden = $('[name="' + disableAboveCodeName + '"]:hidden');
							}
							$hidden.val(data);
						});
					});
				}
				
				$("#clearBtn").click(function(){
					$(".categorization").find("input").val('').removeAttr('disabled')					
					$(".categorization").find("select").val('').removeAttr('disabled').trigger('change');
					$("[name=cpiBasePeriod]").removeAttr("readonly").val('')
					$(".categorization").find('.input-group-addon').show();
					$(".categorization").find(':hidden').filter(':not([name="subItemCompilationMethod"] option)').remove();
					$('[name="subItemCompilationMethod"] option:first').prop('selected', true);
				});
				
				<sec:authorize access="!hasPermission(#user, 256)">
					viewOnly();
				</sec:authorize>
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Variety Maintenance</h1>
          <c:if test="${model.id != null}">
				<div class="breadcrumb form-horizontal" style="width:240px">
					<div class="form-group" style="margin-bottom:0px">
			        	<div class="col-sm-5">Created Date:</div>
			        	<div class="col-sm-7">${model.displayCreatedDate}</div>
			        </div>
			        <div class="form-group" style="margin-bottom:0px">
			         	<div class="col-sm-5">Last Modified:</div>
			         	<div class="col-sm-7">${model.displayModifiedDate}</div>
			         </div>
		      	</div>
	      	</c:if>
        </section>
        
        <section class="content">
        	<form id="mainForm" action="<c:url value='/masterMaintenance/UnitMaintenance/save'/>" method="post" role="form" class="form-horizontal">
        		<input name="id" value="<c:out value="${model.id}" />" type="hidden" />
        		<input name="displayCreatedDate" value="<c:out value="${model.displayCreatedDate}" />" type="hidden" />
        		<input name="displayModifiedDate" value="<c:out value="${model.displayModifiedDate}" />" type="hidden" />
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<div class="row">
		        			<div class="col-md-12" style="margin-bottom: 10px">
		        				<a class="btn btn-default" href="<c:url value='/masterMaintenance/UnitMaintenance/home'/>">Back</a>
		        			</div>
	        			</div>
	        			
	        			<div class="row">
	        				<div class="col-md-12">
	        					<div class="box box-primary ">
			        				<div class="box-body ">			        					
		       							<div class="form-group">
		       								<label class="col-sm-2 control-label">CPI Base Period</label>
		       								<div class="col-sm-3">
												<input name="cpiBasePeriod" value="<c:out value="${model.cpiBasePeriod}" />" type="text" required class="form-control" <c:if test="${model.id != null}">readonly</c:if> />
											</div>
										</div>
			        				</div>
			        			</div>
			        		</div>
	        			</div>
	        			<div class="row">
		        			<div class="col-md-6">
			        			<div class="box box-primary categorization">
			        				<div class="box-header with-border">
			        					<h3 class="box-title">Section</h3>
			        					<c:if test="${empty model.id}">
				        					<div class="box-tools pull-right">
				        						<button class="btn btn-box-tool" type="button" id="clearBtn">
				        							<i class="fa fa-eraser"></i>
				        						</button>
				        					</div>
			        					</c:if>
			        				</div>
			       					<div class="box-body lookup-onchange"
			       						data-url="<c:url value='/masterMaintenance/UnitMaintenance/getSectionByCode'/>">
		       							<div class="form-group">
		       								<label class="col-sm-3 control-label">Code</label>
		       								<div class="col-sm-6">
		       									<c:if test="${model.id == null}">
		     										<select name="sectionCode" class="form-control select2ajax-selected-id code replace-by-code"
														data-ajax-url="<c:url value='/masterMaintenance/UnitMaintenance/querySectionSelect2'/>"
														data-single-url="<c:url value='/masterMaintenance/UnitMaintenance/querySectionCodeSelectSingle'/>"
														data-tags="true"
														data-multiple="false"
														required>
														<c:if test="${model.sectionCode != null}">
														<option>${model.sectionCode}</option>
														</c:if>
													</select>
		       									</c:if>
		       									<c:if test="${model.id != null}">
		     										<input value="<c:out value="${model.sectionCode}" />" type="text" class="form-control code" disabled />
		       										<input name="sectionCode" value="<c:out value="${model.sectionCode}" />" type="hidden"/>
												</c:if>
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-3 control-label">Chinese Name</label>
		       								<div class="col-sm-6">
												<input name="sectionChineseName" value="<c:out value="${model.sectionChineseName}" />" type="text" class="form-control chinese-name" maxlength="255" />
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-3 control-label">English Name</label>
		       								<div class="col-sm-6">
												<input name="sectionEnglishName" value="<c:out value="${model.sectionEnglishName}" />" type="text" class="form-control english-name" maxlength="255" />
											</div>
										</div>
			       					</div>
			        			</div>
		        			</div>	        			
	        				
		        			<div class="col-md-6">
			        			<div class="box box-primary categorization">
			        				<div class="box-header with-border">
			        					<h3 class="box-title">Group</h3>
			        				</div>
			       					<div class="box-body lookup-onchange"
			       						data-url="<c:url value='/masterMaintenance/UnitMaintenance/getGroupByCode'/>">
		       							<div class="form-group">
		       								<label class="col-sm-3 control-label">Code</label>
		       								<div class="col-sm-6">
		       									<c:if test="${model.id == null}">
		     										<select name="groupCode" class="form-control select2ajax-selected-id code replace-by-code disable-above-code"
														data-ajax-url="<c:url value='/masterMaintenance/UnitMaintenance/queryGroupSelect2'/>"
														data-single-url="<c:url value='/masterMaintenance/UnitMaintenance/queryGroupCodeSelectSingle'/>"
														data-tags="true"
														data-multiple="false"
														data-disable-above-code-url="<c:url value='/masterMaintenance/UnitMaintenance/getSectionCodeByGroupCode'/>"
														data-disable-above-code-name="sectionCode"
														required>
														<c:if test="${model.groupCode != null}">
														<option>${model.groupCode}</option>
														</c:if>
													</select>
		       									</c:if>
		       									<c:if test="${model.id != null}">
		     										<input value="<c:out value="${model.groupCode}" />" type="text" class="form-control code" disabled />
		       										<input name="groupCode" value="<c:out value="${model.groupCode}" />" type="hidden"/>
												</c:if>
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-3 control-label">Chinese Name</label>
		       								<div class="col-sm-6">
												<input name="groupChineseName" value="<c:out value="${model.groupChineseName}" />" type="text" class="form-control chinese-name" maxlength="255" />
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-3 control-label">English Name</label>
		       								<div class="col-sm-6">
												<input name="groupEnglishName" value="<c:out value="${model.groupEnglishName}" />" type="text" class="form-control english-name" maxlength="255" />
											</div>
										</div>
			       					</div>
			        			</div>
			        		</div>
	        			</div>
	        			
	        			<div class="row">
	        				<div class="col-md-6">
			        			<div class="box box-primary categorization" >
			        				<div class="box-header with-border">
			        					<h3 class="box-title">Sub Group</h3>
			        				</div>
			       					<div class="box-body lookup-onchange"
			       						data-url="<c:url value='/masterMaintenance/UnitMaintenance/getSubGroupByCode'/>">
		       							<div class="form-group">
		       								<label class="col-sm-3 control-label">Code</label>
		       								<div class="col-sm-6">
		       									<c:if test="${model.id == null}">
		     										<select name="subGroupCode" class="form-control select2ajax-selected-id code replace-by-code disable-above-code"
														data-ajax-url="<c:url value='/masterMaintenance/UnitMaintenance/querySubGroupSelect2'/>"
														data-single-url="<c:url value='/masterMaintenance/UnitMaintenance/querySubGroupCodeSelectSingle'/>"
														data-tags="true"
														data-multiple="false"
														data-disable-above-code-url="<c:url value='/masterMaintenance/UnitMaintenance/getGroupCodeBySubGroupCode'/>"
														data-disable-above-code-name="groupCode"
														required>
														<c:if test="${model.subGroupCode != null}">
														<option>${model.subGroupCode}</option>
														</c:if>
													</select>
		       									</c:if>
		       									<c:if test="${model.id != null}">
		     										<input value="<c:out value="${model.subGroupCode}" />" type="text" class="form-control code" disabled />
		       										<input name="subGroupCode" value="<c:out value="${model.subGroupCode}" />" type="hidden"/>
												</c:if>
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-3 control-label">Chinese Name</label>
		       								<div class="col-sm-6">
												<input name="subGroupChineseName" value="<c:out value="${model.subGroupChineseName}" />" type="text" class="form-control chinese-name" maxlength="255" />
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-3 control-label">English Name</label>
		       								<div class="col-sm-6">
												<input name="subGroupEnglishName" value="<c:out value="${model.subGroupEnglishName}" />" type="text" class="form-control english-name" maxlength="255" />
											</div>
										</div>
			       					</div>
			        			</div>
		        			</div>
		        			
		        			<div class="col-md-6">
			        			<div class="box box-primary categorization">
			        				<div class="box-header with-border">
			        					<h3 class="box-title">Item</h3>
			        				</div>
			       					<div class="box-body lookup-onchange"
			       						data-url="<c:url value='/masterMaintenance/UnitMaintenance/getItemByCode'/>">
		       							<div class="form-group">
		       								<label class="col-sm-3 control-label">Code</label>
		       								<div class="col-sm-6">
		       									<c:if test="${model.id == null}">
		       										<div class="input-group">
		       											<select name="itemCode" class="form-control select2ajax-selected-id code replace-by-code disable-above-code"
															data-ajax-url="<c:url value='/masterMaintenance/UnitMaintenance/queryItemSelect2'/>"
															data-single-url="<c:url value='/masterMaintenance/UnitMaintenance/queryItemCodeSelectSingle'/>"
															data-tags="true"
															data-multiple="false"
															data-disable-above-code-url="<c:url value='/masterMaintenance/UnitMaintenance/getSubGroupCodeByItemCode'/>"
															data-disable-above-code-name="subGroupCode"
															required>
															<c:if test="${model.itemCode != null}">
															<option>${model.itemCode}</option>
															</c:if>
														</select>
														<div class="input-group-addon lookup" data-bottom-entity-class="Item"
															data-unitlookup-only-active="false">
															<i class="fa fa-search"></i>
														</div>
		       										</div>
		       									</c:if>
		       									<c:if test="${model.id != null}">
		     										<input value="<c:out value="${model.itemCode}" />" type="text" class="form-control code" disabled />
		       										<input name="itemCode" value="<c:out value="${model.itemCode}" />" type="hidden"/>
												</c:if>
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-3 control-label">Chinese Name</label>
		       								<div class="col-sm-6">
												<input name="itemChineseName" value="<c:out value="${model.itemChineseName}" />" type="text" class="form-control chinese-name" maxlength="255" />
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-3 control-label">English Name</label>
		       								<div class="col-sm-6">
												<input name="itemEnglishName" value="<c:out value="${model.itemEnglishName}" />" type="text" class="form-control english-name" maxlength="255" />
											</div>
										</div>
			       					</div>
			        			</div>
		        			</div>
	        			</div>
	        			
	        			<div class="row">
	        				<div class="col-md-6">
			        			<div class="box box-primary categorization">
			        				<div class="box-header with-border">
			        					<h3 class="box-title">Outlet Type</h3>
			        				</div>
			       					<div class="box-body lookup-onchange"
			       						data-url="<c:url value='/masterMaintenance/UnitMaintenance/getOutletTypeByCode'/>">
		       							<div class="form-group">
		       								<label class="col-sm-3 control-label">Code</label>
		       								<div class="col-sm-6">
		       									<c:if test="${model.id == null}">
		       										<div class="input-group">
		       											<select name="outletTypeCode" class="form-control select2ajax-selected-id code replace-by-code disable-above-code"
															data-ajax-url="<c:url value='/masterMaintenance/UnitMaintenance/queryOutletTypeSelect2'/>"
															data-single-url="<c:url value='/masterMaintenance/UnitMaintenance/queryOutletTypeCodeSelectSingle'/>"
															data-tags="true"
															data-multiple="false"
															data-disable-above-code-url="<c:url value='/masterMaintenance/UnitMaintenance/getItemCodeByOutletTypeCode'/>"
															data-disable-above-code-name="itemCode"
															required>
															<c:if test="${model.outletTypeCode != null}">
															<option>${model.outletTypeCode}</option>
															</c:if>
														</select>
														<div class="input-group-addon lookup" data-bottom-entity-class="OutletType"
															data-unitlookup-only-active="false">
															<i class="fa fa-search"></i>
														</div>
		       										</div>
		       									</c:if>
		       									<c:if test="${model.id != null}">
		     										<input value="<c:out value="${model.outletTypeCode}" />" type="text" class="form-control code" disabled />
		       										<input name="outletTypeCode" value="<c:out value="${model.outletTypeCode}" />" type="hidden"/>
												</c:if>
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-3 control-label">Chinese Name</label>
		       								<div class="col-sm-6">
												<input name="outletTypeChineseName" value="<c:out value="${model.outletTypeChineseName}" />" type="text" class="form-control chinese-name" maxlength="255" />
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-3 control-label">English Name</label>
		       								<div class="col-sm-6">
												<input name="outletTypeEnglishName" value="<c:out value="${model.outletTypeEnglishName}" />" type="text" class="form-control english-name" maxlength="255" />
											</div>
										</div>
										<div class="form-group">
											<label class="col-sm-3 control-label">&nbsp;</label>
										</div>
			       					</div>
			        			</div>
		        			</div>
		        			
		        			<div class="col-md-6">
			        			<div class="box box-primary categorization">
			        				<div class="box-header with-border">
			        					<h3 class="box-title">Sub Item</h3>
			        				</div>
			       					<div class="box-body lookup-onchange"
			       						data-url="<c:url value='/masterMaintenance/UnitMaintenance/getSubItemByCode'/>">
		       							<div class="form-group">
		       								<label class="col-sm-3 control-label">Code</label>
		       								<div class="col-sm-6">
		       									<c:if test="${model.id == null}">
		       										<div class="input-group">
		       											<select name="subItemCode" class="form-control select2ajax-selected-id code replace-by-code disable-above-code"
															data-ajax-url="<c:url value='/masterMaintenance/UnitMaintenance/querySubItemSelect2'/>"
															data-single-url="<c:url value='/masterMaintenance/UnitMaintenance/querySubItemCodeSelectSingle'/>"
															data-tags="true"
															data-multiple="false"
															data-disable-above-code-url="<c:url value='/masterMaintenance/UnitMaintenance/getOutletTypeCodeBySubItemCode'/>"
															data-disable-above-code-name="outletTypeCode"
															required>
															<c:if test="${model.subItemCode != null}">
															<option>${model.subItemCode}</option>
															</c:if>
														</select>
														<div class="input-group-addon lookup" data-bottom-entity-class="SubItem"
															data-unitlookup-only-active="false">
															<i class="fa fa-search"></i>
														</div>
		       										</div>
		       									</c:if>
		       									<c:if test="${model.id != null}">
		     										<input value="<c:out value="${model.subItemCode}" />" type="text" class="form-control code" disabled />
		       										<input name="subItemCode" value="<c:out value="${model.subItemCode}" />" type="hidden"/>
												</c:if>
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-3 control-label">Chinese Name</label>
		       								<div class="col-sm-6">
												<input name="subItemChineseName" value="<c:out value="${model.subItemChineseName}" />" type="text" class="form-control chinese-name" maxlength="255" />
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-3 control-label">English Name</label>
		       								<div class="col-sm-6">
												<input name="subItemEnglishName" value="<c:out value="${model.subItemEnglishName}" />" type="text" class="form-control english-name" maxlength="255" />
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-3 control-label">Compilation Method</label>
		       								<div class="col-sm-6">
		       									<select name="subItemCompilationMethod" class="form-control compilation-method" required>
		       										<option value="1" ${model.subItemCompilationMethod == 1 ? 'selected': ''}>A.M. (Supermarket, fresh)</option>
		       										<option value="2" ${model.subItemCompilationMethod == 2 ? 'selected': ''}>A.M. (Supermarket, non-fresh)</option>
		       										<option value="3" ${model.subItemCompilationMethod == 3 ? 'selected': ''}>A.M. (Market)</option>
		       										<option value="4" ${model.subItemCompilationMethod == 4 ? 'selected': ''}>G.M. (Supermarket)</option>
		       										<option value="5" ${model.subItemCompilationMethod == 5 ? 'selected': ''}>G.M. (Batch)</option>
		       										<option value="6" ${model.subItemCompilationMethod == 6 ? 'selected': ''}>A.M. Batch</option>
		       									</select>
											</div>
										</div>
			       					</div>
			        			</div>
		        			</div>
	        			</div>
	        			
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
	        					<h3 class="box-title">Variety</h3>
	        				</div>
	       					<div class="box-body">
       							<div class="form-group">
       								<label class="col-sm-2 control-label">Purpose</label>
       								<%--
       								<div class="col-sm-2">
										<label class="radio-inline">
											<input type="radio" name="MRPS" value="True" <c:if test="${model.MRPS}">checked</c:if>> MRPS
										</label>
									</div>
       								<div class="col-sm-2">
										<label class="radio-inline">
											<input type="radio" name="MRPS" value="False" <c:if test="${not model.MRPS}">checked</c:if>> Others
										</label>
									</div>
									 --%>
									<div class="col-sm-3">
										<select name="purposeId" class="form-control select2ajax-selected-id"
											data-ajax-url="<c:url value='/masterMaintenance/UnitMaintenance/queryPurposeSelect2'/>"
											data-single-url="<c:url value='/masterMaintenance/UnitMaintenance/queryPurposeSelectSingle'/>"
											data-selected-id="${model.purposeId}"
											required></select>
									</div>
	       						</div>
       							<div class="form-group">
       								<label class="col-sm-2 control-label">Variety Code</label>
       								<div class="col-sm-3">
										<input name="code" value="<c:out value="${model.code}" />" type="text" class="form-control" maxlength="50"
											required
											${model.id != null ? 'disabled' : ''} />
       									<c:if test="${model.id != null}">
       										<input name="code" value="<c:out value="${model.code}" />" type="hidden"/>
										</c:if>
									</div>
	       						</div>
       							<div class="form-group">
       								<label class="col-sm-2 control-label">Chinese Name</label>
       								<div class="col-sm-3">
										<input name="chineseName" value="<c:out value="${model.chineseName}" />" type="text" class="form-control" maxlength="255" />
									</div>
								</div>
       							<div class="form-group">
       								<label class="col-sm-2 control-label">English Name</label>
       								<div class="col-sm-3">
										<input name="englishName" value="<c:out value="${model.englishName}" />" type="text" class="form-control" maxlength="255" />
									</div>
								</div>
       							<div class="form-group">
       								<label class="col-sm-2 control-label">Display Name</label>
       								<div class="col-sm-3">
										<input name="displayName" value="<c:out value="${model.displayName}" />" type="text" class="form-control" maxlength="255" />
									</div>
								</div>
       							<div class="form-group">
       								<label class="col-sm-2 control-label">Max. no. of quotations allowed in an outlet</label>
       								<div class="col-sm-3">
										<input name="maxQuotation" value="<c:out value="${model.maxQuotation}" />" type="text" class="form-control" data-rule-digits="true"
											data-rule-not_smaller_than_or_eq="[name='minQuotation']" />
									</div>
								</div>
       							<div class="form-group">
       								<label class="col-sm-2 control-label">Immediate Generation of Indoor Quotation Records</label>
       								<div class="col-sm-3">
       									<label class="checkbox-inline">
											<input name="minQuotation" type="checkbox" value="1" ${model.minQuotation == 1 ? "checked" : ""}/>
										</label>
										<%-- <input name="minQuotation" value="<c:out value="${model.minQuotation}" />" type="text" class="form-control" data-rule-digits="true"
											data-rule-smaller_than_or_eq="[name='maxQuotation']" /> --%>
									</div>
								</div>
       							<div class="form-group">
       								<label class="col-sm-2 control-label">Variety Category</label>
       								<div class="col-sm-3">
										<input name="unitCategory" value="<c:out value="${model.unitCategory}" />" type="text" class="form-control" maxlength="255" />
									</div>
								</div>
       							<div class="form-group">
       								<label class="col-sm-2 control-label">Standard UOM</label>
       								<div class="col-sm-3">
										<select name="standardUOMId" class="form-control select2ajax-selected-id"
											data-ajax-url="<c:url value='/masterMaintenance/UnitMaintenance/queryUomSelect2'/>"
											data-single-url="<c:url value='/masterMaintenance/UnitMaintenance/queryUomSelectSingle'/>"
											data-selected-id="${model.standardUOMId}"></select>
									</div>
								</div>
       							<div class="form-group">
       								<label class="col-sm-2 control-label">Standard value of UOM</label>
       								<div class="col-sm-3">
										<input name="uomValue" value="<c:out value="${model.uomValue}" />" type="text" class="form-control" data-rule-number="true"
											data-rule-required="[name='standardUOMId'] option:selected" />
									</div>
								</div>
       							<div class="form-group">
       								<label class="col-sm-2 control-label">UOM Category allowed</label>
       								<div class="col-sm-8">
										<select name="uomCategoryIds" class="form-control select2ajax-selected-ids"
											data-ajax-url="<c:url value='/masterMaintenance/UnitMaintenance/queryUOMCategorySelect2'/>"
											data-multiple-url="<c:url value='/masterMaintenance/UnitMaintenance/queryUomCategorySelectByIds'/>"
											data-selected-ids="${model.uomCategoryIds}" multiple="multiple" data-close-on-select="false"
											data-rule-required="[name='standardUOMId'] option:selected">
										</select>
									</div>
								</div>
       							<div class="form-group">
       								<label class="col-sm-2 control-label">Product Type</label>
       								<div class="col-sm-3">
										<select name="productCategoryId" class="form-control select2ajax-selected-id"
											data-ajax-url="<c:url value='/masterMaintenance/UnitMaintenance/queryProductGroupSelect2'/>"
											data-single-url="<c:url value='/masterMaintenance/UnitMaintenance/queryProductGroupSelectSingle'/>"
											data-selected-id="${model.productCategoryId}" required></select>
									</div>
								</div>
       							<div class="form-group">
       								<label class="col-sm-2 control-label">Sub-price type allowed</label>
       								<div class="col-sm-3">
										<select name=subPriceTypeId class="form-control select2ajax-selected-id"
											data-ajax-url="<c:url value='/masterMaintenance/UnitMaintenance/querySubPriceTypeSelect2'/>"
											data-single-url="<c:url value='/masterMaintenance/UnitMaintenance/querySubPriceTypeSelectSingle'/>"
											data-selected-id="${model.subPriceTypeId}"></select>
									</div>
								</div>
       							<div class="form-group">
       								<label class="col-sm-2 control-label">Obsolete Date</label>
       								<div class="col-sm-3">
										<div class="input-group">
											<input type="text" class="form-control date-picker" data-orientation="top"
												name="obsoleteDate" value="<c:out value="${model.obsoleteDate}" />" />
											<div class="input-group-addon">
												<i class="fa fa-calendar"></i>
											</div>
										</div>
									</div>
								</div>
       							<div class="form-group">
       								<label class="col-sm-2 control-label">Data Conversion Config</label>
       								<div class="col-sm-10">
										<label class="checkbox-inline">
											<input name="spicingRequired" type="checkbox" value="True" ${model.spicingRequired ? "checked" : ""}/> Splicing required
										</label>
										<label class="checkbox-inline">
											<input name="frRequired" type="checkbox" value="True" ${model.frRequired ? "checked" : ""}/> FR required
										</label>
									</div>
								</div>
       							<div class="form-group">
       								<label class="col-sm-2 control-label">Seasonality</label>
       								<div class="col-sm-10">
										<label class="radio-inline">
											<input name="seasonality" type="radio" value="1" ${model.seasonality == 1 ? "checked" : ""}/> All-time
										</label>
										<label class="radio-inline">
											<input name="seasonality" type="radio" value="2" ${model.seasonality == 2 ? "checked" : ""}/> Summer
										</label>
										<label class="radio-inline">
											<input name="seasonality" type="radio" value="3" ${model.seasonality == 3 ? "checked" : ""}/> Winter
										</label>
										<label class="radio-inline">
											<input name="seasonality" type="radio" value="4" ${model.seasonality == 4 ? "checked" : ""}/> Occasional
										</label>
									</div>
								</div>
       							<div class="form-group seasonality-occasional">
       								<label class="col-sm-2 control-label"></label>
       								<div class="col-sm-2">
										<select name="seasonStartMonth" class="form-control" data-rule-required="[name='seasonality'][value='4']:checked">
											<option></option>
											<option ${model.seasonStartMonth == 1 ? "selected" : ""}>1</option>
											<option ${model.seasonStartMonth == 2 ? "selected" : ""}>2</option>
											<option ${model.seasonStartMonth == 3 ? "selected" : ""}>3</option>
											<option ${model.seasonStartMonth == 4 ? "selected" : ""}>4</option>
											<option ${model.seasonStartMonth == 5 ? "selected" : ""}>5</option>
											<option ${model.seasonStartMonth == 6 ? "selected" : ""}>6</option>
											<option ${model.seasonStartMonth == 7 ? "selected" : ""}>7</option>
											<option ${model.seasonStartMonth == 8 ? "selected" : ""}>8</option>
											<option ${model.seasonStartMonth == 9 ? "selected" : ""}>9</option>
											<option ${model.seasonStartMonth == 10 ? "selected" : ""}>10</option>
											<option ${model.seasonStartMonth == 11 ? "selected" : ""}>11</option>
											<option ${model.seasonStartMonth == 12 ? "selected" : ""}>12</option>
										</select>
									</div>
									<div class="col-sm-1">
										<p class="form-control-static text-center">To</p>
									</div>
       								<div class="col-sm-2">
										<select name="seasonEndMonth" class="form-control" data-rule-required="[name='seasonality'][value='4']:checked">
											<option></option>
											<option ${model.seasonEndMonth == 1 ? "selected" : ""}>1</option>
											<option ${model.seasonEndMonth == 2 ? "selected" : ""}>2</option>
											<option ${model.seasonEndMonth == 3 ? "selected" : ""}>3</option>
											<option ${model.seasonEndMonth == 4 ? "selected" : ""}>4</option>
											<option ${model.seasonEndMonth == 5 ? "selected" : ""}>5</option>
											<option ${model.seasonEndMonth == 6 ? "selected" : ""}>6</option>
											<option ${model.seasonEndMonth == 7 ? "selected" : ""}>7</option>
											<option ${model.seasonEndMonth == 8 ? "selected" : ""}>8</option>
											<option ${model.seasonEndMonth == 9 ? "selected" : ""}>9</option>
											<option ${model.seasonEndMonth == 10 ? "selected" : ""}>10</option>
											<option ${model.seasonEndMonth == 11 ? "selected" : ""}>11</option>
											<option ${model.seasonEndMonth == 12 ? "selected" : ""}>12</option>
										</select>
									</div>
								</div>
       							<div class="form-group">
       								<label class="col-sm-2 control-label">Pricing Month</label>
       								<div class="col-sm-3">
										<select name=pricingFrequencyId class="form-control select2ajax-selected-id" required
											data-ajax-url="<c:url value='/masterMaintenance/UnitMaintenance/queryPricingFrequencySelect2'/>"
											data-single-url="<c:url value='/masterMaintenance/UnitMaintenance/queryPricingFrequencySelectSingle'/>"
											data-selected-id="${model.pricingFrequencyId}"></select>
									</div>
								</div>
       							<div class="form-group">
       								<label class="col-sm-2 control-label">RTN Period</label>
       								<div class="col-sm-3">
										<input name="rtnPeriod" value="<c:out value="${model.rtnPeriod}" />" type="text" class="form-control" data-rule-number="true" />
									</div>
								</div>
       							<div class="form-group">
       								<label class="col-sm-2 control-label">Backdated data required for new product</label>
       								<div class="col-sm-10">
       									<div class="checkbox">
											<label>
												<input name="backdateRequired" type="checkbox" value="True" ${model.backdateRequired ? "checked" : ""}/>
											</label>
										</div>
									</div>
								</div>
       							<div class="form-group">
       								<label class="col-sm-2 control-label">Allow edition of PM prices (conversion)</label>
       								<div class="col-sm-10">
       									<div class="checkbox">
											<label>
												<input name="allowEditPMPrice" type="checkbox" value="True" ${model.allowEditPMPrice ? "checked" : ""}/>
											</label>
										</div>
									</div>
								</div>
       							<div class="form-group">
       								<label class="col-sm-2 control-label">RUA allowed</label>
       								<div class="col-sm-10">
       									<div class="checkbox">
											<label>
												<input name="ruaAllowed" type="checkbox" value="True" ${model.ruaAllowed ? "checked" : ""}/>
											</label>
										</div>
									</div>
								</div>
       							<div class="form-group">
       								<label class="col-sm-2 control-label">Effective Date</label>
       								<div class="col-sm-3">
										<div class="input-group">
											<input type="text" class="form-control date-picker" data-orientation="top"
												name="effectiveDate" value="<c:out value="${model.effectiveDate}" />" />
											<div class="input-group-addon">
												<i class="fa fa-calendar"></i>
											</div>
										</div>
									</div>
								</div>
       							<div class="form-group">
       								<label class="col-sm-2 control-label">Fresh Item</label>
       								<div class="col-sm-10">
       									<div class="checkbox">
											<label>
												<input name="freshItem" type="checkbox" value="True" ${model.freshItem ? "checked" : ""}/>
											</label>
										</div>
									</div>
								</div>
       							<div class="form-group">
       								<label class="col-sm-2 control-label">Product Changed Allowed</label>
       								<div class="col-sm-10">
       									<div class="checkbox">
											<label>
												<input name="allowProductChange" type="checkbox" value="True" ${model.allowProductChange ? "checked" : ""}/>
											</label>
										</div>
									</div>
								</div>
       							<div class="form-group">
       								<label class="col-sm-2 control-label">Data collection form display</label>
       								<div class="col-sm-10">
										<label class="radio-inline">
											<input name="formDisplay" type="radio" value="1" ${model.formDisplay == 1 ? "checked" : ""}/> Normal
										</label>
										<label class="radio-inline">
											<input name="formDisplay" type="radio" value="2" ${model.formDisplay == 2 ? "checked" : ""}/> Tour
										</label>
									</div>
								</div>
       							<div class="form-group">
       								<label class="col-sm-2 control-label">Product Cycle</label>
       								<div class="col-sm-3">
										<input name="productCycle" value="<c:out value="${model.productCycle}" />" type="text" class="form-control" data-rule-digits="true" />
									</div>
								</div>
       							<div class="form-group">
       								<label class="col-sm-2 control-label">Status</label>
       								<div class="col-sm-10">
										<label class="radio-inline">
											<input type="radio" name="status" value="Active" <c:if test="${model.status == 'Active'}">checked</c:if>> Active
										</label>
										<label class="radio-inline">
											<input type="radio" name="status" value="Inactive" <c:if test="${model.status == 'Inactive'}">checked</c:if>> Inactive
										</label>
									</div>
	       						</div>
	       						<div class="form-group">
       								<label class="col-sm-2 control-label">Data Converison After Closing Date</label>
       								<div class="col-sm-10">
										<label class="radio-inline">
											<input type="radio" name="convertAfterClosingDate" value="true" <c:if test="${model.convertAfterClosingDate}">checked</c:if>> Yes
										</label>
										<label class="radio-inline">
											<input type="radio" name="convertAfterClosingDate" value="false" <c:if test="${!model.convertAfterClosingDate}">checked</c:if>> No
										</label>
									</div>
	       						</div>
       							<div class="form-group">
       								<label class="col-sm-2 control-label">Cross Check Group</label>
       								<div class="col-sm-3">
										<input name="crossCheckGroup" value="<c:out value="${model.crossCheckGroup}" />" type="text" class="form-control" />
									</div>
								</div>
       							<div class="form-group">
       								<label class="col-sm-2 control-label">CPI Quotation Type</label>
       								<div class="col-sm-3">
										<select name="cpiQoutationType" class="form-control">
											<option></option>
											<option value="1" ${model.cpiQoutationType == 1 ? "selected" : ""}>Market</option>
											<option value="2" ${model.cpiQoutationType == 2 ? "selected" : ""}>Supermarket</option>
											<option value="3" ${model.cpiQoutationType == 3 ? "selected" : ""}>Batch</option>
											<option value="4" ${model.cpiQoutationType == 4 ? "selected" : ""}>Others</option>
										</select>
									</div>
								</div>
       							<div class="form-group">
       								<label class="col-sm-2 control-label">Mandatory price for data collection</label>
       								<div class="col-sm-10">
										<label class="checkbox-inline">
											<input name="NPriceMandatory" type="checkbox" value="True" ${model.NPriceMandatory ? "checked" : ""}/> Mandatory N price
										</label>
										<label class="checkbox-inline">
											<input name="SPriceMandatory" type="checkbox" value="True" ${model.SPriceMandatory ? "checked" : ""}/> Mandatory S price
										</label>
									</div>
								</div>
								<div class="form-group">
       								<label class="col-sm-2 control-label">Data Transmission Rule</label>
       								<div class="col-sm-3">
										<select name="dataTransmissionRule" class="form-control" required>
											<option></option>
											<option value="A" ${model.dataTransmissionRule == "A" ? "selected" : ""}>A</option>
											<option value="B" ${model.dataTransmissionRule == "B" ? "selected" : ""}>B</option>
											<option value="C" ${model.dataTransmissionRule == "C" ? "selected" : ""}>C</option>
											<option value="D" ${model.dataTransmissionRule == "D" ? "selected" : ""}>D</option>
											<option value="E" ${model.dataTransmissionRule == "E" ? "selected" : ""}>E</option>
										</select>
									</div>
								</div>
								
								<div class="form-group">
       								<label class="col-sm-2 control-label">ICP Type</label>
       								<div class="col-sm-3">
										<input name="icpType" value="<c:out value="${model.icpType}" />" type="text" class="form-control" maxlength="2000" />
									</div>
								</div>
	       					</div>
	        			</div>
	        			
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
	        					<h3 class="box-title">On-spot Validation</h3>
	        				</div>
	       					<div class="box-body">
	       						<%@include file="/WEB-INF/views/masterMaintenance/UnitMaintenance/edit-on-spot-validation.jsp" %>
	       					</div>
	       					<div class="box-footer">
	       						<sec:authorize access="hasPermission(#user, 256)">
	        						<button type="submit" class="btn btn-info">Submit</button>
	       						</sec:authorize>
	       					</div>
       					</div>
	        		</div>
	        	</div>
        	</form>
        </section>
	</jsp:body>
</t:layout>

