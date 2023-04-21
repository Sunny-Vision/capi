<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
<jsp:useBean id="niceDate" class="java.util.Date"/>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>		
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/jstree-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/unitLookup-css.jsp" %>
		<style>
			.form-inline .form-control.input-num {
				width: 5em;
			}
			.top-buffer { 
				margin-top:50px; 
			}
		</style>
	</jsp:attribute>
	<jsp:attribute name="header">
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker.jsp" %>
		<%@include file="/WEB-INF/views/includes/jstree.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/unitLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/productLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/outletLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/userLookup.jsp" %>
		<script>
			
			$(function() {
				
				var $mainForm = $('#mainForm');
				
				$mainForm.validate({
					ignore: [],  
					rules : {
						unitId: {
							required: true
						},
						batchId: {
							required: true
						},
						outletId: {
							required: {
								depends: function (e){
									return ($('[name="status"]').val() == 'Active');
								}
							}
						},
						quotationLoading: {
							number: true
						},
						frAdmin: {
							number: true
						},
						frField: {
							number: true
						},
						"ruaSettingEditModel.districtId": {
							required: {
								depends: function (e){
									return (!$('[name="ruaSettingEditModel.isRUAAllDistrict"]').prop('checked') && $('[name="ruaSettingEditModel.userId"]').val() == null);
								}
							}
						},
						"ruaSettingEditModel.userId": {
							required: {
								depends: function (e){
									return (!$('[name="ruaSettingEditModel.isRUAAllDistrict"]').prop('checked')  && $('[name="ruaSettingEditModel.districtId"]').val() == null);
								}
							}
						}
					},
					messages: {
						unitId: {
							required: "<spring:message code='E00010' />",
						},
						batchId: {
							required: "<spring:message code='E00010' />",
						},
						outletId: {
							required: "<spring:message code='E00010' />",
						},
						quotationLoading: {
							number: "<spring:message code='E00071' />"
						},
						frAdmin: {
							number: "<spring:message code='E00071' />"
						},
						frField: {
							number: "<spring:message code='E00071' />"
						}
					}
				});
				
				
				Modals.init();
				Datepicker();
				
				$('#unitId').select2ajax({
					allowClear: false,
					placeholder: '',
					width:"100%"
				});
				
				$('#unitId,.searchUnitId').unitLookup({
					selectedIdsCallback: function(selectedIds) {
						var singleUrl = $('#unitId').data('singleUrl');
						$('#unitId').empty();
						$.get(singleUrl, { id: selectedIds[0] }, 
							function(data) {
							var option = new Option(data, selectedIds[0]);
							option.selected = true;
							$('#unitId').append(option);
							$('#unitId').trigger('change');
						});
					},
					multiple: false
				});
				
				$('#unitId').change(function () {
					if (!(typeof($('[name="unitId"],.searchUnitId').select2('data')[0]) === 'undefined') ) {
						loadUnit($('[name="unitId"],.searchUnitId').select2('data')[0]["id"]);
						$('#productId').select2("val", "");
						$('#productId').prop('disabled', false);
						$('.searchProductId').off('click');
						$('.searchProductId').productLookup({
							selectedIdsCallback: function(selectedIds) {
								var singleUrl = $('#productId').data('singleUrl');
								$('#productId').empty();
								var option = new Option(selectedIds[0], selectedIds[0]);
								option.selected = true;
								$('#productId').append(option);
								$('#productId').trigger('change');
							},
							productGroupId: productGroupId,
							multiple: false
						});
						if ($('[name="outletId"],.searchOutletId').data('select2') && !(typeof($('[name="outletId"],.searchOutletId').select2('data')[0]) === 'undefined') )
							loadQuotationLoading($('[name="unitId"],.searchUnitId').select2('data')[0]["id"],
									$('[name="outletId"],.searchoutletId').select2('data')[0]["id"] );
					} else {
						$('#productId').select2("val", "");
						$('#productId').prop('disabled', true);
						$('.searchProductId').off('click');
					}
				});

				$('#productId').select2ajax({
					allowClear: true,
					placeholder: '',
					ajax: {
					    /*data: function (term, page) {
					        return {
					        	term: term.term,
					            productGroupId: productGroupId
					        }
					    }*/
					    data: function(params) {
					    	params.productGroupId = productGroupId;
					    	return params;
					    }
					},
					width:"100%"
				});
				$('#productId').prop('disabled', true);

				if ("${model.unitId}" != "" ) {
				   	$.ajax({
				        url: $('#unitId').data('singleUrl'),
				        data : { id: "${model.unitId}" },
				        success: function(data) {
							var option = new Option(data,  "${model.unitId}" );
							option.selected = true;
							$('#unitId').append(option);
							$('#unitId').trigger('change');
						},
				        async: false
				    });
				}
				
				if ("${model.productId}" != "") {
					$('#productId').empty();
						var option = new Option("${model.productId}", "${model.productId}");
						option.selected = true;
						$('#productId').append(option);
						$('#productId').trigger('change');
				}
				
				$('#outletId').select2ajax({
					allowClear: true,
					placeholder: '',
					width:"100%"
				});
				
				$('#outletId,.searchOutletId').outletLookup({
					selectedIdsCallback: function(selectedIds) {
						var singleUrl = $('#outletId').data('singleUrl');
						$('#outletId').empty();
						$.post(singleUrl, { id: selectedIds[0] }, 
							function(data) {
							var option = new Option(data, selectedIds[0]);
							option.selected = true;
							$('#outletId').append(option);
							$('#outletId').trigger('change');
						});
					},
					multiple: false
				});
				
				if ("${model.outletId}" != "" ) {
					$.ajax({
				        url: $('#outletId').data('singleUrl'),
				        data : { id: "${model.outletId}" },
				        success: function(data) {
							var option = new Option(data,  "${model.outletId}" );
							option.selected = true;
							$('#outletId').append(option);
							$('#outletId').trigger('change');
						},
				        async: false
					});
				}
				
				$('#outletId').change(function () {
					if (!(typeof($('[name="unitId"],.searchUnitId').select2('data')[0]) === 'undefined') && 
						!(typeof($('[name="outletId"],.searchOutletId').select2('data')[0]) === 'undefined'))
						loadQuotationLoading($('[name="unitId"],.searchUnitId').select2('data')[0]["id"],
								$('[name="outletId"],.searchoutletId').select2('data')[0]["id"] );
					$(this).select2("close");
				});
			
				$('#batchId').select2ajax({
					placeholder: "",
					width:"100%"
				});
				if ("${model.batchId}" != "" ) {
					$.get($('#batchId').data('singleUrl'), { id: "${model.batchId}" }, 
						function(data) {
						var option = new Option(data, "${model.batchId}");
						option.selected = true;
						$('#batchId').append(option);
						$('#batchId').trigger('change');
					});
				}
				//$('.select2ajax').hide();
				$('.select2ajax').css('visibility','hidden');
				
				$('.month-picker').datepicker({
					format: "mm-yyyy",
					startView: "months", 
				    minViewMode: "months"
				});

				//================= RUA ==================
				if($('[name="status"]').val() == 'RUA') {
					$('#ruaStatusDIV').show();
					$('#mainForm').validate().settings.ignore = [];
				} else {
					$('#ruaStatusDIV').hide();
					$('#mainForm').validate().settings.ignore = ":hidden";
				}
				
				$('[name="status"]').on('change', function() {
					if($('[name="status"]').val() == 'RUA') {
						$('#ruaStatusDIV').show();
						$('#mainForm').validate().settings.ignore = [];
					} else {
						$('#ruaStatusDIV').hide();
						$('#mainForm').validate().settings.ignore = ":hidden";
					}
				});
				
				$('[name="ruaSettingEditModel.districtId"]').select2ajax({
					allowClear: true,
					placeholder: '',
					width: '100%'
				});
				
				if($('[name="ruaSettingEditModel.isRUAAllDistrict"]').prop('checked') == true) {
					$('[name="ruaSettingEditModel.districtId"]').prop('disabled', true);
				} else {
					$('[name="ruaSettingEditModel.districtId"]').prop('disabled', false);
				}
				
				$('[name="ruaSettingEditModel.isRUAAllDistrict"]').on('change', function() {
					var check = $(this);
					if (check.val() == "true"){
		    			$('[name="ruaSettingEditModel.districtId"]').prop('disabled', true);
		    			$('[name="ruaSettingEditModel.districtId"]').select2ajax('destroy');
		    			$('[name="ruaSettingEditModel.districtId"]').find("option").remove();
		    			$('[name="ruaSettingEditModel.districtId"]').select2ajax({
							allowClear: true,
							placeholder: '',
							width: '100%'
						});
		    		}
		    		else{
		    			$('[name="ruaSettingEditModel.districtId"]').prop('disabled', false);
		    		}
				});
				
				$("[name='ruaSettingEditModel.userId']").select2({closeOnSelect: false, width: "100%"});
				
				$('.searchStaffIds').userLookup({
					selectedIdsCallback: function(selectedIds) {
						$.post("<c:url value='/assignmentManagement/RUASetting/getStaffsName'/>", {ids: selectedIds}, function(result){
							var options = [];
							$('[name="ruaSettingEditModel.userId"]').val(options);
							$(result).each(function(){
								options.push(this.userId);
							});
							$('[name="ruaSettingEditModel.userId"]').val(options);
							$('[name="ruaSettingEditModel.userId"]').trigger("change");
						});
					},
					alreadySelectedIdsCallback: function() {
						var ids = [];
						$("[name='ruaSettingEditModel.userId'] option:selected").each(function(){
							ids.push(this.value)
						})
						return ids;
					},
					queryDataCallback: function (dataModel){
						dataModel.authorityLevel = 16;
					}
				});
				//==========================================

				<sec:authorize access="!hasPermission(#user, 256)">
					viewOnly();
				</sec:authorize>
			});
			
			function loadUnit(unitId) {
			    $.ajax({
			        url:'<c:url value='/assignmentManagement/QuotationMaintenance/queryUnitData/'/>',
			        async: false,
			        type:'get',
			        data:'unitId='+unitId,
			        success:function(data){;
			            $("#pricingMonth").html(data["pricingMonth"]);
			            $("#seasonality").html(data["seasonality"]);
			            $("#cpiBasePeriod").html(data["cpiBasePeriod"]);
			            if (data["productGroupId"] != null) {
			            	productGroupId = eval(data["productGroupId"]);
			            } else {
			            	productGroupId = null;
			            }
			            enabledFRRelatedField(data.seasonalityCode != 1 && data.isFrRequired);
			        }
			    });
			}
			
			function enabledFRRelatedField( isFrRequired ) {
				if (isFrRequired == null) {
					isFrRequired = false;
				}
				
				var disable = !isFrRequired;
				
				var $mainForm = $("#mainForm");
				var $frAdminInput = $("input[name='frAdmin']", $mainForm);
				var $isFRAdminPercentageInput = $("input[name='isFRAdminPercentage']", $mainForm);
				var $frFieldInput = $("input[name='frField']", $mainForm);
				var $isFRFieldPercentageInput = $("input[name='isFRFieldPercentage']", $mainForm);
				var $useFRAdminInput = $("input[name='useFRAdmin']", $mainForm);
				var $isTempIsUseFRAdminInput = $("input[name='isTempIsUseFRAdmin']", $mainForm);
				var $seasonalWithdrawalMonthInput = $("input[name='seasonalWithdrawalMonth']", $mainForm);
				var $isFRAppliedInput = $("input[name='isFRApplied']", $mainForm);
				var $isTempIsFRAppliedInput = $("input[name='isTempIsFRApplied']", $mainForm);
				var $isReturnGoodsInput = $("input[name='isReturnGoods']", $mainForm);
				var $isTempIsReturnGoodsInput = $("input[name='isTempIsReturnGoods']", $mainForm);
				var $isReturnNewGoodsInput = $("input[name='isReturnNewGoods']", $mainForm);
				var $isTempIsReturnNewGoodsInput = $("input[name='isTempIsReturnNewGoods']", $mainForm);
				var $isLastSeasonReturnGoods = $("input[name='isLastSeasonReturnGoods']", $mainForm);
				
				
				$frAdminInput.prop("disabled", disable);
				$isFRAdminPercentageInput.prop("disabled", disable);
				$frFieldInput.prop("disabled", disable);
				$isFRFieldPercentageInput.prop("disabled", disable);
				$useFRAdminInput.prop("disabled", disable);
				$isTempIsUseFRAdminInput.prop("disabled", disable);
				$seasonalWithdrawalMonthInput.prop("disabled", disable);
				$isFRAppliedInput.prop("disabled", disable);
				$isTempIsFRAppliedInput.prop("disabled", disable);
				$isReturnGoodsInput.prop("disabled", disable);
				$isTempIsReturnGoodsInput.prop("disabled", disable);
				$isReturnNewGoodsInput.prop("disabled", disable);
				$isTempIsReturnNewGoodsInput.prop("disabled", disable);
				$isLastSeasonReturnGoods.prop("disabled", disable);
				
				if ( disable ) {
					var $frAdminInputWrapper = $frAdminInput.closest("div");
					$frAdminInputWrapper.removeClass("has-error");
					$frAdminInputWrapper.find(".help-block").remove();
					
					var $frFieldInputWrapper = $frFieldInput.closest("div");
					$frFieldInputWrapper.removeClass("has-error");
					$frFieldInputWrapper.find(".help-block").remove();
				}
			}
			
			function loadQuotationLoading(unitId, outletId ) 
			{
			    $.ajax({
			        url:'<c:url value='/assignmentManagement/QuotationMaintenance/queryQuotationLoading/'/>',
			        type:'post',
			        data: {
			        	unitId: unitId,
			        	outletId: outletId
			        },
			        success:function(data){
			            $("#quotationLoading").val(data);
			        }
			    });
			}
			
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Quotation Maintenance</h1>
          <c:if test="${model.quotationId != null}">
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
        	<form id="mainForm" action="<c:url value='/assignmentManagement/QuotationMaintenance/save'/>" method="post" role="form" class="form-horizontal">
        		<input name="id" value="<c:out value="${model.quotationId}" />" type="hidden" />
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->	
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/assignmentManagement/QuotationMaintenance/home'/>">Back</a>
							</div>
							<div class="box-body">
	        				    <div class="form-group">
	      							<label class="col-sm-2 control-label">Quotation Id</label>
	      							<div class="col-sm-3">
	      								<c:if test="${model.quotationId != null}">
	   										<p class="form-control-static" >${model.quotationId}</p>
	     									<input name="quotationId" value="<c:out value="${model.quotationId}" />" type="hidden"/>
										</c:if>
									</div>
								</div>
	   							<div class="form-group">
	   								<label class="col-sm-2 control-label">Variety</label>
	   								<div class="col-sm-3">
	   									<div class="input-group">
	   										<select name="unitId" class="form-control select2ajax" id="unitId"
											data-ajax-url="<c:url value='/assignmentManagement/QuotationMaintenance/queryUnitSelect2'/>"
											data-single-url="<c:url value='/assignmentManagement/QuotationMaintenance/queryUnitSelectSingle'/>"/>
											</select>	
											<div class="input-group-addon searchUnitId">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								<div class="form-group">
	    							<label class="col-sm-2 control-label">CPI Base Period</label>
	    							<div class="col-sm-3">
	    								<p class="form-control-static" id="cpiBasePeriod"></p>
									</div>
								</div>
	    						<div class="form-group">
	    							<label class="col-sm-2 control-label">Pricing Month</label>
	    							<div class="col-sm-3">
	    								<p class="form-control-static" id="pricingMonth"></p>
									</div>
								</div>
	     							<div class="form-group">
	     								<label class="col-sm-2 control-label">Seasonality</label>
	     								<div class="col-sm-3">
	     								<p class="form-control-static" id="seasonality"></p>
									</div>
								</div>
	      						<div class="form-group">
	      							<label class="col-sm-2 control-label">Product</label>
	      							<div class="col-sm-3">
										<div class="input-group">
											<select name="productId" class="form-control select2ajax" id="productId"
												data-ajax-url="<c:url value='/assignmentManagement/QuotationMaintenance/queryProductSelect2'/>"
												data-single-url="<c:url value='/assignmentManagement/QuotationMaintenance/queryProductSelectSingle'/>"/></select>	
											<div class="input-group-addon searchProductId">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
	      							<div class="form-group">
	      								<label class="col-sm-2 control-label">Outlet</label>
	      								<div class="col-sm-3">
										<div class="input-group">
											<select name="outletId" class="form-control select2ajax" id="outletId"
												data-ajax-url="<c:url value='/assignmentManagement/QuotationMaintenance/queryOutletSelect2'/>"
												data-single-url="<c:url value='/assignmentManagement/QuotationMaintenance/queryOutletSelectSingle'/>"/>
												
												</select>	
											<div class="input-group-addon searchOutletId">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
	
		       					</div>
		       					 <div class="form-group">
	      							<label class="col-sm-2 control-label">Batch</label>
	      							<div class="col-sm-3">
										<select name="batchId" class="form-control select2" id="batchId"
											data-ajax-url="<c:url value='/assignmentManagement/QuotationMaintenance/queryBatchSelect2'/>"
											data-single-url="<c:url value='/assignmentManagement/QuotationMaintenance/queryBatchSelectSingle'/>"/></select>								</div>
								</div>
								<div class="top-buffer">
								</div>							
								<div class="form-group">
	      							<label class="col-sm-2 control-label">Quotation Loading</label>
	      							<div class="col-sm-3">
										<input id="quotationLoading" name="quotationLoading" value="<c:out value="${model.quotationLoading}" />" type="text" class="form-control" maxlength="255" />
									</div>
								</div>
								<div class="form-group">
	      							<label class="col-sm-2 control-label">Status</label>
	      							<div class="col-sm-3">
										<select class="form-control" id="status" name="status">
											<option value="RUA" <c:if test="${model.status == 'RUA'}"> selected </c:if> >RUA</option>
											<option value="Active" <c:if test="${model.status == 'Active'}"> selected </c:if> >Active</option>
											<option value="Inactive" <c:if test="${model.status == 'Inactive'}"> selected </c:if> >Inactive</option>
										</select>
									</div>
								</div>
								
								<!-- RUA Setting -->
								<div id="ruaStatusDIV" class="RUA">
									<div class="form-group">
										<label class="col-sm-2 control-label">RUA Setting</label>
										<div class="col-sm-3">
											<div class="">
												<label class="radio-inline">
													<input type="radio" name="ruaSettingEditModel.isRUAAllDistrict" value="true" <c:if test="${model.ruaSettingEditModel.isRUAAllDistrict == 'true'}">checked</c:if>> All District
												</label>
											</div>
											<div class="">
												<label class="radio-inline">
													<input type="radio" name="ruaSettingEditModel.isRUAAllDistrict" value="false" <c:if test="${model.ruaSettingEditModel.isRUAAllDistrict == 'false' || model.ruaSettingEditModel.isRUAAllDistrict == null}">checked</c:if>> District
												</label>
												<select class="form-control selec2" id="ruaSettingEditModel.districtId" name="ruaSettingEditModel.districtId"
													data-ajax-url="<c:url value='/assignmentManagement/RUASetting/queryDistrictSelect2'/>" >
													<c:if test="${model.ruaSettingEditModel.districtId != null}">
														<option value="<c:out value="${model.ruaSettingEditModel.districtId}" />" selected>${model.ruaSettingEditModel.districtLabel}</option>
													</c:if>
												</select>
											</div>
										</div>
									</div>
									<div class="form-group">
	       								<label class="col-sm-2 control-label"></label>
	       								<div class="col-sm-3">
	       									<div class="input-group">
												<select class="form-control select2 searchStaffIds" name="ruaSettingEditModel.userId" id="ruaSettingEditModel.userId" multiple style="display:none" >
													<c:forEach items="${userFilterList}" var="userFilter">
														<option value="<c:out value="${userFilter.userId}" />"
															<c:forEach var="id" items="${model.ruaSettingEditModel.userId}">
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
									<br/>
								</div>
								
								<div class="form-group">
	      							<label class="col-sm-2 control-label">Indoor Allocation Code</label>
	      							<div class="col-sm-3">
										<input name="indoorAllocationCode" value="<c:out value="${model.indoorAllocationCode}" />" type="text" class="form-control" maxlength="255" />
									</div>
								</div>
								<div class="form-group">
	      							<label class="col-sm-2 control-label">ICP</label>
	      							<div class="col-sm-1">
										<label class="radio-inline"><input name="ICP" type="Radio" value="true" <c:if test="${model.ICP}"> checked </c:if> /> Y </label>
									</div>
									<div class="col-sm-1">
										<label class="radio-inline"><input name="ICP" type="Radio" value="false" <c:if test="${not model.ICP}"> checked </c:if> /> N </label>							
									</div>
								</div>
								<div class="top-buffer">
								</div>							
								<div class="form-group">
	      							<label class="col-sm-2 control-label">CPI Compilation Series</label>
	      							<div class="col-sm-3">
										<input name="cpiCompilationSeries" value="<c:out value="${model.cpiCompilationSeries}" />" type="text" class="form-control" maxlength="255" />
									</div>
								</div>
								<div class="form-group">
	      							<label class="col-sm-2 control-label">Old Form Bar Serial</label>
	      							<div class="col-sm-3">
										<input name="oldFormBarSerial" value="<c:out value="${model.oldFormBarSerial}" />" type="text" class="form-control" maxlength="255" />
									</div>
								</div>	
								<div class="form-group">
	      							<label class="col-sm-2 control-label">Indoor Remarks</label>
	      							<div class="col-sm-3">
										<input name="oldFormSequence" value="<c:out value="${model.oldFormSequence}" />" type="text" class="form-control" maxlength="255" />
									</div>
								</div>
								<div class="form-group">
	      							<label class="col-sm-2 control-label">ICP Product Code</label>
	      							<div class="col-sm-3">
										<input name="icpProductCode" value="<c:out value="${model.icpProductCode}" />" type="text" class="form-control" maxlength="255" />
									</div>
								</div>
								<div class="form-group">
	      							<label class="col-sm-2 control-label">FR (Admin)</label>
	      							<div class="col-sm-3">
										<input name="frAdmin" value="<c:out value="${model.frAdmin}" />" type="text" class="form-control" maxlength="255" 
											<c:if test="${not model.frRequired}">disabled</c:if> />
									</div>
									<div class="col-sm-5 form-control-static">
										<label>
											<input type="radio" name="isFRAdminPercentage" value="false" <c:if test="${not model.isFRAdminPercentage}">checked</c:if> 
												<c:if test="${not model.frRequired}">disabled</c:if> />$
										</label>
										&nbsp;
										<label>
											<input type="radio" name="isFRAdminPercentage" value="true" <c:if test="${model.isFRAdminPercentage}">checked</c:if> 
												<c:if test="${not model.frRequired}">disabled</c:if> />%
										</label>
									</div>
								</div>
								<div class="form-group">
	      							<label class="col-sm-2 control-label">FR (Field)</label>
	      							<div class="col-sm-3">
										<input name="frField" value="<c:out value="${model.frField}" />" type="text" class="form-control" maxlength="255" 
											<c:if test="${not model.frRequired}">disabled</c:if> />
									</div>
									<div class="col-sm-5 form-control-static">
										<label>
											<input type="radio" name="isFRFieldPercentage" value="false" <c:if test="${not model.isFRFieldPercentage}">checked</c:if> 
												<c:if test="${not model.frRequired}">disabled</c:if> />$
										</label>
										&nbsp;
										<label>
											<input type="radio" name="isFRFieldPercentage" value="true" <c:if test="${model.isFRFieldPercentage}">checked</c:if> 
												<c:if test="${not model.frRequired}">disabled</c:if> />%
										</label>
									</div>
								</div>
								<div class="form-group">
	      							<label class="col-sm-2 control-label">Use FR (Admin)</label>
	      							<!--<div class="col-sm-3">
	    								<p class="form-control-static" id="useFRAdmin">
			    							<c:if test="${not empty model.useFRAdmin}">
			    								<c:if test="${model.useFRAdmin==true}"> Y </c:if>
			    								<c:if test="${model.useFRAdmin==false}"> N </c:if>
		    								</c:if>
	    								</p>
									</div>-->
									<div class="col-sm-1">
										<label class="radio-inline"><input name="useFRAdmin" type="Radio" value="true" <c:if test="${model.useFRAdmin == true}"> checked </c:if> 
											<c:if test="${not model.frRequired}">disabled</c:if> /> Y </label>
									</div>
									<div class="col-sm-1">
										<label class="radio-inline"><input name="useFRAdmin" type="Radio" value="false" <c:if test="${model.useFRAdmin == false}"> checked </c:if> 
											<c:if test="${not model.frRequired}">disabled</c:if> /> N </label>							
									</div>
									<div class="col-sm-1">
										<label class="radio-inline"><input name="useFRAdmin" type="Radio" value="" <c:if test="${model.useFRAdmin == null}"> checked </c:if> 
											<c:if test="${not model.frRequired}">disabled</c:if> /> Null </label>							
									</div>
									
									<label class="col-sm-2 control-label">Temp Use FR (Admin)</label>
									<div class="col-sm-1">
										<label class="radio-inline"><input name="isTempIsUseFRAdmin" type="Radio" value="true" <c:if test="${model.isTempIsUseFRAdmin == true}"> checked </c:if> 
											<c:if test="${not model.frRequired}">disabled</c:if> /> Y </label>
									</div>
									<div class="col-sm-1">
										<label class="radio-inline"><input name="isTempIsUseFRAdmin" type="Radio" value="false" <c:if test="${model.isTempIsUseFRAdmin == false}"> checked </c:if> 
											<c:if test="${not model.frRequired}">disabled</c:if> /> N </label>							
									</div>
									<div class="col-sm-1">
										<label class="radio-inline"><input name="isTempIsUseFRAdmin" type="Radio" value="" <c:if test="${model.isTempIsUseFRAdmin == null}"> checked </c:if> 
											<c:if test="${not model.frRequired}">disabled</c:if> /> Null </label>							
									</div>
								</div>
								<div class="top-buffer">
								</div>
								<div class="form-group">
	      							<label class="col-sm-2 control-label">Seasonal Withdrawal</label>
	      							<div class="col-sm-3">
	      								<div class="input-group">
											<input name="seasonalWithdrawalMonth" value="<c:out value="${model.seasonalWithdrawalMonth}" />" type="text" class="form-control month-picker" maxlength="10" 
												<c:if test="${not model.frRequired}">disabled</c:if> />
											<div class="input-group-addon">
												<i class="fa fa-calendar"></i>
											</div>
										</div>
									</div>
								</div>
								<div class="form-group">
	      							<label class="col-sm-2 control-label">FR Applied</label>
	      							<div class="col-sm-1">
										<label class="radio-inline"><input name="isFRApplied" type="Radio" value="true" <c:if test="${model.isFRApplied}"> checked </c:if> 
											<c:if test="${not model.frRequired}">disabled</c:if> /> Y </label>
									</div>
									<div class="col-sm-1">
										<label class="radio-inline"><input name="isFRApplied" type="Radio" value="false" <c:if test="${not model.isFRApplied}"> checked </c:if> 
											<c:if test="${not model.frRequired}">disabled</c:if> /> N </label>							
									</div>
									
									<label class="col-sm-2 col-sm-offset-1 control-label">Temp FR Applied</label>
	      							<div class="col-sm-1">
										<label class="radio-inline"><input name="isTempIsFRApplied" type="Radio" value="true" <c:if test="${model.isTempIsFRApplied}"> checked </c:if> 
											<c:if test="${not model.frRequired}">disabled</c:if> /> Y </label>
									</div>
									<div class="col-sm-1">
										<label class="radio-inline"><input name="isTempIsFRApplied" type="Radio" value="false" <c:if test="${not model.isTempIsFRApplied}"> checked </c:if> 
											<c:if test="${not model.frRequired}">disabled</c:if> /> N </label>							
									</div>
								</div>
								
								<div class="form-group">
	      							<label class="col-sm-2 control-label">Return Goods</label>
	      							<div class="col-sm-1">
										<label class="radio-inline"><input name="isReturnGoods" type="Radio" value="true" <c:if test="${model.isReturnGoods}"> checked </c:if> 
											<c:if test="${not model.frRequired}">disabled</c:if> /> Y </label>
									</div>
									<div class="col-sm-1">
										<label class="radio-inline"><input name="isReturnGoods" type="Radio" value="false" <c:if test="${not model.isReturnGoods}"> checked </c:if> 
											<c:if test="${not model.frRequired}">disabled</c:if> /> N </label>							
									</div>
									
									<label class="col-sm-2 col-sm-offset-1 control-label">Temp Return Goods</label>
	      							<div class="col-sm-1">
										<label class="radio-inline"><input name="isTempIsReturnGoods" type="Radio" value="true" <c:if test="${model.isTempIsReturnGoods}"> checked </c:if> 
											<c:if test="${not model.frRequired}">disabled</c:if> /> Y </label>
									</div>
									<div class="col-sm-1">
										<label class="radio-inline"><input name="isTempIsReturnGoods" type="Radio" value="false" <c:if test="${not model.isTempIsReturnGoods}"> checked </c:if> 
											<c:if test="${not model.frRequired}">disabled</c:if> /> N </label>							
									</div>
								</div>
								<div class="form-group">
	      							<label class="col-sm-2 control-label">Return New Goods</label>
	      							<div class="col-sm-1">
										<label class="radio-inline"><input name="isReturnNewGoods" type="Radio" value="true" <c:if test="${model.isReturnNewGoods}"> checked </c:if> 
											<c:if test="${not model.frRequired}">disabled</c:if> /> Y </label>
									</div>
									<div class="col-sm-1">
										<label class="radio-inline"><input name="isReturnNewGoods" type="Radio" value="false" <c:if test="${not model.isReturnNewGoods}"> checked </c:if> 
											<c:if test="${not model.frRequired}">disabled</c:if> /> N </label>							
									</div>
									
									<label class="col-sm-2 col-sm-offset-1 control-label">Temp Return New Goods</label>
	      							<div class="col-sm-1">
										<label class="radio-inline"><input name="isTempIsReturnNewGoods" type="Radio" value="true" <c:if test="${model.isTempIsReturnNewGoods}"> checked </c:if> 
											<c:if test="${not model.frRequired}">disabled</c:if> /> Y </label>
									</div>
									<div class="col-sm-1">
										<label class="radio-inline"><input name="isTempIsReturnNewGoods" type="Radio" value="false" <c:if test="${not model.isTempIsReturnNewGoods}"> checked </c:if> 
											<c:if test="${not model.frRequired}">disabled</c:if> /> N </label>							
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label">Last FR Applied</label>
	      							<div class="col-sm-3">
										 <p class="form-control-static" >${commonService.formatDate(model.lastFRAppliedDate)}</p>
									</div>
									
									<label class="col-sm-2 control-label">Last Season Return Goods</label>
	      							<div class="col-sm-1">
										<label class="radio-inline"><input name="isLastSeasonReturnGoods" type="Radio" value="true" <c:if test="${model.isLastSeasonReturnGoods}"> checked </c:if> 
											<c:if test="${not model.frRequired}">disabled</c:if> /> Y </label>
									</div>
									<div class="col-sm-1">
										<label class="radio-inline"><input name="isLastSeasonReturnGoods" type="Radio" value="false" <c:if test="${not model.isLastSeasonReturnGoods}"> checked </c:if> 
											<c:if test="${not model.frRequired}">disabled</c:if> /> N </label>							
									</div>
								</div>	
								
								
								<div class="form-group">
	      							<label class="col-sm-2 control-label">ICP Type</label>
	      							<div class="col-sm-3">
										<input name="icpType" value="<c:out value="${model.icpType}" />" type="text" class="form-control" maxlength="2000" />
									</div>
								</div>	
								<div class="form-group">
	      							<label class="col-sm-2 control-label">ICP Product Name</label>
	      							<div class="col-sm-3">
										<input name="icpProductName" value="<c:out value="${model.icpProductName}" />" type="text" class="form-control" maxlength="2000" />
									</div>
								</div>
								
								<div class="form-group">
	      							<label class="col-sm-2 control-label">Form Type</label>
	      							<div class="col-sm-3">
										<input name="formType" value="<c:out value="${model.formType}" />" type="text" class="form-control" maxlength="255" />
									</div>
								</div>	
								
								<div class="top-buffer">
								</div>	
	       					<div class="box-footer">	
	       						<sec:authorize access="hasPermission(#user, 256)">
	       							<button type="submit" class="btn btn-info pull-left">Submit</button>
	       						</sec:authorize>
	      					</div>
	      				</div>
      					</div>
	        		</div>
	        	</div>
        	</form>
        </section>
	</jsp:body>
</t:layout>

