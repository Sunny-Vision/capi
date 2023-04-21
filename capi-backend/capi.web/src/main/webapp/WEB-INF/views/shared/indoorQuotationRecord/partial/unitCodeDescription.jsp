<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%
/**
 * Display outlet section
 * 
 * Controller permission required:
 * /shared/General/getOutletImage
 */
%>
<script>
	$(function() {
		var $container = $('.unit-code-description');
		function updateNPercentageChange() {
			var currentPrice = $('[name="currentNPrice"]', $container).val();
			var previousPrice = $('[name="previousNPrice"]', $container).val();
			var $percent = $('.current-n-price-percent', $container);
			var result = calculatePercentageChange(currentPrice, previousPrice);
			if (result == null) {
				$percent.html('NA');
			} else {
				$percent.html(result + '%');
			}
		}

		function updateSPercentageChange() {
			var currentPrice = $('[name="currentSPrice"]', $container).val();
			var previousPrice = $('[name="previousSPrice"]', $container).val();
			var $percent = $('.current-s-price-percent', $container);
			var result = calculatePercentageChange(currentPrice, previousPrice);
			if (result == null) {
				$percent.html('NA');
			} else {
				$percent.html(result + '%');
			}
		}
		
		function calculatePercentageChange(currentPrice, previousPrice) {
			if (currentPrice.trim() == '' || previousPrice.trim() == '' || isNaN(+currentPrice) || isNaN(+previousPrice)) {
				return null;
			}
			var result = Math.round(((currentPrice - previousPrice) / previousPrice * 100.0) * 100.0) / 100.0;
			return result;
		}
		
		jQuery.validator.addMethod("FRRequire", function(value, element) {
			if($(element).is(":checked")){
				return ($('[name="currentNPrice"]').val() != "" && $('[name="currentSPrice"]').val() != "" && $('[name="fr"]').val() != "")
			}else{
				return true	
			}
			}, "Some Fields cannot be empty");
		$.validator.addClassRules("FRRequire", { FRRequire: true });
		
		jQuery.validator.addMethod("SpicingRequire", function(value, element) {
			if ($(element).is(":checked")) {
				return ($('[name="currentNPrice"]').val() != "" && $('[name="currentSPrice"]').val() != "");
			}else{
				return true	
			}
			}, "Some Fields cannot be empty");
		$.validator.addClassRules("SpicingRequire", { SpicingRequire: true });
		
		$("[name='currentNPrice']").on('change', function() {
			var nPrice = $(this).val();
			var noOfDecimal;
			if(nPrice.indexOf(".") >= 0) {
				noOfDecimal = (nPrice).split(".")[1].length;
			} else {
				noOfDecimal = 0;
			}
			var newNPrice;
			if(noOfDecimal == 2) {
				if((nPrice).split(".")[1] == "00") {
					newNPrice = Math.floor((+nPrice));
					$(this).val(newNPrice);
				}
			} else if(noOfDecimal > 2) {
				//newNPrice = (+nPrice).toFixed(2).toString();
				//newNPrice = (Math.round((+nPrice) * 100) / 100.0).toString();
				newNPrice = (Math.round(Math.round((+nPrice) * Math.pow(10, (2 || 0) + 1)) / 10) / Math.pow(10, (2 || 0))).toString();
				if(newNPrice.split(".")[1] == "00") {
					newNPrice = Math.floor((+newNPrice));
				}
				$(this).val(newNPrice);
			}
			updateNPercentageChange();
		});
		$("[name='currentSPrice']").on('change', function() {
			var sPrice = $(this).val();
			var noOfDecimal;
			if(sPrice.indexOf(".") >= 0) {
				noOfDecimal = (sPrice).split(".")[1].length;
			} else {
				noOfDecimal = 0;
			}
			var newSPrice;
			if(noOfDecimal == 2) {
				if((sPrice).split(".")[1] == "00") {
					newSPrice = Math.floor((+sPrice));
					$(this).val(newSPrice);
				}
			} else if(noOfDecimal > 2) {
				//newSPrice = (+sPrice).toFixed(2).toString();
				//newSPrice = (Math.round((+sPrice) * 100) / 100.0).toString();
				newSPrice = (Math.round(Math.round((+sPrice) * Math.pow(10, (2 || 0) + 1)) / 10) / Math.pow(10, (2 || 0))).toString();

				if(newSPrice.split(".")[1] == "00") {
					newSPrice = Math.floor((+newSPrice));
				}
				$(this).val(newSPrice);
			}
			updateSPercentageChange();
		});
		
		//Round previousNPrice 2 decimal point digit
		$("[name='previousNPrice']").on('change', function() {
			var nPrice = $(this).val();
			var noOfDecimal;
			if(nPrice.indexOf(".") >= 0) {
				noOfDecimal = (nPrice).split(".")[1].length;
			} else {
				noOfDecimal = 0;
			}
			var newNPrice;
			if(noOfDecimal == 2) {
				if((nPrice).split(".")[1] == "00") {
					newNPrice = Math.floor((+nPrice));
					$(this).val(newNPrice);
				}
			} else if(noOfDecimal > 2) {
				newNPrice = (Math.round(Math.round((+nPrice) * Math.pow(10, (2 || 0) + 1)) / 10) / Math.pow(10, (2 || 0))).toString();
				if(newNPrice.split(".")[1] == "00") {
					newNPrice = Math.floor((+newNPrice));
				}
				$(this).val(newNPrice);
			}
		});
		
		//Round previousSPrice 2 decimal point digit
		$("[name='previousSPrice']").on('change', function() {
			var sPrice = $(this).val();
			var noOfDecimal;
			if(sPrice.indexOf(".") >= 0) {
				noOfDecimal = (sPrice).split(".")[1].length;
			} else {
				noOfDecimal = 0;
			}
			var newSPrice;
			if(noOfDecimal == 2) {
				if((sPrice).split(".")[1] == "00") {
					newSPrice = Math.floor((+sPrice));
					$(this).val(newSPrice);
				}
			} else if(noOfDecimal > 2) {
				newSPrice = (Math.round(Math.round((+sPrice) * Math.pow(10, (2 || 0) + 1)) / 10) / Math.pow(10, (2 || 0))).toString();

				if(newSPrice.split(".")[1] == "00") {
					newSPrice = Math.floor((+newSPrice));
				}
				$(this).val(newSPrice);
			}
		});
		
		$('[name=nullCurrentNPrice],[name=nullCurrentSPrice],[name=isNullPreviousNPrice],[name=isNullPreviousSPrice]').change(function() {
			$(this).closest('.form-group').find('[type=text]').prop('disabled', $(this).prop('checked'));
			if ($(this).prop('checked')){
				$(this).closest('.form-group').find('[type=text]').val('').trigger('change');
			}
			if($('[name=isNullPreviousNPrice]').prop('checked')){
				$('[name=nullPreviousNPrice]').prop('checked', true);
			} else {
				$('[name=nullPreviousNPrice]').prop('checked', false);
			}
			if($('[name=isNullPreviousSPrice]').prop('checked')){
				$('[name=nullPreviousSPrice]').prop('checked', true);
			} else {
				$('[name=nullPreviousSPrice]').prop('checked', false);
			}
		}).each(function() {
			$(this).closest('.form-group').find('[type=text]').prop('disabled', $(this).prop('checked'));
			if ($(this).prop('checked')){
				$(this).closest('.form-group').find('[type=text]').val('');
			}
			if($('[name=isNullPreviousNPrice]').prop('checked')){
				$('[name=nullPreviousNPrice]').prop('checked', true);
			} else {
				$('[name=nullPreviousNPrice]').prop('checked', false);
			}
			if($('[name=isNullPreviousSPrice]').prop('checked')){
				$('[name=nullPreviousSPrice]').prop('checked', true);
			} else {
				$('[name=nullPreviousSPrice]').prop('checked', false);
			}
		});
		
		$('[name="previousNPrice"]').change(function() {
			updateNPercentageChange();
		});
		
		$('[name="previousSPrice"]').change(function() {
			updateSPercentageChange();
		});
		
		$('[name="spicing"]').change(function() {
			var lastNPrice = $("span.lastNPrice").html();
			var lastSPrice = $("span.lastSPrice").html();
			var currentNPrice = $('[name="currentNPrice"]').val();
			var currentSPrice = $('[name="currentSPrice"]').val();
			var previousNPrice = $('[name="previousNPrice"]').val();
			var previousSPrice = $('[name="previousSPrice"]').val();
			
			if($(this).is(":checked")){
				if (!$(this).valid()) {
					return;
				}
				
				// step 2
				if (previousNPrice != "") {
					// step 3
					if (lastNPrice != "" && lastSPrice != "") {
						// step 4
						if (!($('[name="previousNPrice"]').prop('disabled'))) {
							$('[name="previousNPrice"]').val(previousNPrice).trigger('change');
						}
						if (!($('[name="previousSPrice"]').prop('disabled'))) {
							$('[name="previousSPrice"]').val(roundUpto(previousNPrice*lastSPrice/lastNPrice, 2)).trigger('change');
						}
					} else {
						// step 5
						if (!($('[name="previousNPrice"]').prop('disabled'))) {
							$('[name="previousNPrice"]').val(previousNPrice).trigger('change');
						}
						if (!($('[name="previousSPrice"]').prop('disabled'))) {
							$('[name="previousSPrice"]').val(previousSPrice).trigger('change');
						}
					}
				} else {
					// step 7
					if (lastNPrice != "" && lastSPrice != "") {
						// step 8
						if (!($('[name="previousNPrice"]').prop('disabled'))) {
							$('[name="previousNPrice"]').val(currentNPrice).trigger('change');
						}
						if (!($('[name="previousSPrice"]').prop('disabled'))) {
							$('[name="previousSPrice"]').val(roundUpto(currentNPrice*lastSPrice/lastNPrice, 2)).trigger('change');
						}
					} else {
						// step 9
						if (!($('[name="previousNPrice"]').prop('disabled'))) {
							$('[name="previousNPrice"]').val(null).trigger('change');
						}
						if (!($('[name="previousSPrice"]').prop('disabled'))) {
							$('[name="previousSPrice"]').val(null).trigger('change');
						}
					}
				}
			}
		});

		$('[name="applyFR"]').change(function() {
			var lastNPrice = $("span.lastNPrice").html();
			var lastSPrice = $("span.lastSPrice").html();
			var currentNPrice = $('[name="currentNPrice"]').val();
			var currentSPrice = $('[name="currentSPrice"]').val();
			var fr = $('[name="fr"]').val();
			
			if($(this).is(":checked")){
				if (!$(this).valid()) {
					$(".frLastDateApplyLabel").html('');
					return;
				}
				
				var string = "(Last date applied FR: "+moment().format("DD-MM-YYYY")+") with FR="+$('[name="fr"]').val();
				if($('[name="fRPercentage"]').is(":checked")){
					string = string+"%";
				}
				$(".frLastDateApplyLabel").html(string);

				//cal fr
				// step 2
				if(lastNPrice != "" && lastSPrice != "" ){
					// step 3
					if($('[name="fRPercentage"]').is(":checked")){
						// step 4
						if (!($('[name="previousNPrice"]').prop('disabled'))) {
							$('[name="previousNPrice"]').val(roundUpto(currentNPrice/ (1+ (fr/100)), 2)).trigger('change');
						}
						if (!($('[name="previousSPrice"]').prop('disabled'))) {
							$('[name="previousSPrice"]').val(roundUpto((currentNPrice/ (1+ (fr/100)))*(lastSPrice/lastNPrice), 2)).trigger('change');
						}
					}else{
						// step 5
						if (!($('[name="previousNPrice"]').prop('disabled'))) {
							$('[name="previousNPrice"]').val(roundUpto(currentNPrice - fr, 2)).trigger('change');
						}
						if (!($('[name="previousSPrice"]').prop('disabled'))) {
							$('[name="previousSPrice"]').val(roundUpto((currentNPrice - fr) * (lastSPrice/lastNPrice), 2)).trigger('change');
						}
					}
				}else{
					// step 6
					if($('[name="fRPercentage"]').is(":checked")){
						// step 7
						if (!($('[name="previousNPrice"]').prop('disabled'))) {
							$('[name="previousNPrice"]').val(roundUpto(currentNPrice/ (1+ (fr/100)), 2)).trigger('change');
						}
						if (!($('[name="previousSPrice"]').prop('disabled'))) {
							$('[name="previousSPrice"]').val(roundUpto(currentNPrice/ (1+ (fr/100)), 2)).trigger('change');
						}
					}else{
						// step 8
						if (!($('[name="previousNPrice"]').prop('disabled'))) {
							$('[name="previousNPrice"]').val(roundUpto(currentNPrice - fr, 2)).trigger('change');
						}
						if (!($('[name="previousSPrice"]').prop('disabled'))) {
							$('[name="previousSPrice"]').val(roundUpto(currentNPrice - fr, 2)).trigger('change');
						}
					}
				}
				
				//end cal fr
				
				$('[name="spicing"]').prop("checked", "checked");
			}else{
				$('[name="fRPercentage"]').prop("checked", false);
				$('[name="fr"]').val('');
				
				$.ajax({
					  type: "POST",
					  url: "<c:url value='/dataConversion/QuotationRecordDataConversion/getFrString'/>",
					  data: {indoorQuotationRecordId: $('[name="indoorQuotationRecordId"]').val()},
					  success: function(result){
						  $(".frLastDateApplyLabel").html(result);
					  },
					});
			}
		});
		
		updateNPercentageChange();
		updateSPercentageChange();
	});
</script>
<div class="box box-primary unit-code-description">
	<div class="box-header with-border">
		<h3 class="box-title">
			Unit code & Description: <c:out value="${unit.code}"></c:out> - <c:out value="${unit.displayName}"></c:out> - 
			<c:if test="${unit.seasonality == 1}">All-time</c:if>
			<c:if test="${unit.seasonality == 2}">Summer</c:if>
			<c:if test="${unit.seasonality == 3}">Winter</c:if>
			<c:if test="${unit.seasonality == 4}">Occasional</c:if>
		</h3>
		<div class="box-tools pull-right">
			<button class="btn btn-box-tool btn-header-collapse" type="button" data-widget="collapse">
				<i class="fa fa-minus"></i>
			</button>
		</div>
	</div>
	<div class="box-body">
		<div class="form-horizontal">
			<div class="row">
				<div class="col-md-12">
					<div class="form-group">
						<div class="col-md-12">
						<c:if test="${unit.uomValue != null}"><label class="col-md-12">*Input Unit: ${unit.uomValue} ${unit.standardUOM.englishName}</label></c:if>
						<c:if test="${indoorQuotationRecord.isNewRecruitment()}"><label class="static-control pull-right">New Recruitment</label></c:if>
						<c:if test="${quotation.status == 'RUA' && quotation.ruaDate != null}"><label class="static-control pull-right">RUA Since <fmt:formatDate value="${quotation.ruaDate}" var="eventDateStr" type="date" pattern="dd-MM-yyyy" />${eventDateStr}</label></c:if>
						<input type="hidden" class="form-control" name="indoorQuotationRecordId" value="${indoorQuotationRecord.id}">
						</div>
					</div>
					<div class="form-group">
						<div class="col-md-4">
							<div class="form-group">
								<label class="col-md-3 control-label">Current N Price:</label>
								<div class=" col-md-3 nopadding">
									<fmt:formatNumber value="${indoorQuotationRecord.currentNPrice}" var="cnp" type="number" groupingUsed = "false" maxFractionDigits="2"/>
									<input type="text" class="form-control" name="currentNPrice" value="${cnp}">	
								</div>
								<div class="col-md-2 nopadding">
									<div class="btn-copy copy-nPrice "><i class="fa fa-copy"></i></div>
								</div>
								<label class="col-md-2 control-label nopadding">(<span class="current-n-price-percent"></span>)</label>
								<label class="col-md-2 control-label nopadding"><input type="checkbox" name="nullCurrentNPrice" value="True" <c:if test="${indoorQuotationRecord.isNullCurrentNPrice()}">checked</c:if>>Is Null</label>
							</div>
							<div class="form-group">
								<label class="col-md-3 control-label">Current S Price:</label>
								<div class=" col-md-3 nopadding">
									<fmt:formatNumber value="${indoorQuotationRecord.currentSPrice}" var="csp" type="number" groupingUsed = "false" maxFractionDigits="2"/>
									<input type="text" class="form-control" name="currentSPrice" value="${csp}">
								</div>
								<div class="col-md-2 nopadding">
									<div class="btn-copy copy-sPrice "><i class="fa fa-copy"></i></div>
								</div>
								<label class="col-md-2 control-label nopadding">(<span class="current-s-price-percent"></span>)</label>
								<label class="col-md-2 control-label nopadding"><input type="checkbox" name="nullCurrentSPrice" value="True" <c:if test="${indoorQuotationRecord.isNullCurrentSPrice()}">checked</c:if>>Is Null</label>
							</div>
							<c:if test="${unit.isFrRequired()}">
							<div class="form-group">
								<label class="col-md-3 control-label">FR</label>
								<div class=" col-md-4">
									<input type="text" class="form-control" name="fr" value="${indoorQuotationRecord.fr}" <c:if test="${isFRLocked}">disabled</c:if>>
								</div>
								<label class="col-md-1 control-label"><input type="checkbox" name="fRPercentage" value="True" <c:if test="${indoorQuotationRecord.isFRPercentage()}">checked</c:if>  <c:if test="${isFRLocked}">disabled</c:if>>%</label>
								<label class="col-md-2 control-label"><input type="checkbox" class="FRRequire" name="applyFR" value="True" <c:if test="${indoorQuotationRecord.isApplyFR()}">checked</c:if>  <c:if test="${isFRLocked}">disabled</c:if> >Apply FR</label>
								<c:if test="${indoorQuotationRecord.isApplyFR()}">
									<label class="col-md-offset-3 col-md-10 frLastDateApplyLabel">(Last date applied: <fmt:formatDate pattern="yyyy-MM-dd" value="${quotation.lastFRAppliedDate}" />) With Fr= ${indoorQuotationRecord.fr}<c:if test="${indoorQuotationRecord.isFRPercentage()}">%</c:if></label>
								</c:if>
								<c:if test="${!indoorQuotationRecord.isApplyFR()}">
									<c:if test="${quotation.isUseFRAdmin()}">
										<label class="col-md-offset-3 col-md-10 frLastDateApplyLabel">(Last date applied: <fmt:formatDate pattern="yyyy-MM-dd" value="${quotation.lastFRAppliedDate}" />) With Fr= ${quotation.frAdmin}<c:if test="${quotation.getIsFRAdminPercentage()}">%</c:if></label>
									</c:if>
									<c:if test="${!quotation.isUseFRAdmin()}">
										<label class="col-md-offset-3 col-md-10 frLastDateApplyLabel">(Last date applied: <fmt:formatDate pattern="yyyy-MM-dd" value="${quotation.lastFRAppliedDate}" />) With Fr= ${quotation.frField}<c:if test="${quotation.getIsFRFieldPercentage()}">%</c:if></label>
									</c:if>
								</c:if>
							</div>
							</c:if>
						</div>
						<div class="col-md-4">
							<div class="form-group">
								<label class="col-md-3 control-label">Previous N Price:</label>
								<div class=" col-md-4">
									<fmt:formatNumber value="${indoorQuotationRecord.previousNPrice}" var="pnp" type="number" groupingUsed = "false" maxFractionDigits="2"/>
									<input type="text" class="form-control" name="previousNPrice" value="${pnp}" <c:if test="${!unit.isAllowEditPMPrice()}">disabled</c:if>>
									<!-- <input type="text" class="form-control" name="previousNPrice" value="${indoorQuotationRecord.previousNPrice}" <c:if test="${!unit.isAllowEditPMPrice()}">disabled</c:if> /> -->
								</div>
								<div class="col-md-1">
									<div class="btn-copy copy-nPrice "><i class="fa fa-copy"></i></div>
								</div>
								<label class="col-md-2 control-label">
									<input type="checkbox"  name="isNullPreviousNPrice" value="True" <c:if test="${indoorQuotationRecord.isNullPreviousNPrice() or !unit.isAllowEditPMPrice()}">checked</c:if>  <c:if test="${!unit.isAllowEditPMPrice()}">disabled</c:if> >
									<input type="checkbox" name="nullPreviousNPrice" value="True" <c:if test="${indoorQuotationRecord.isNullPreviousNPrice() or !unit.isAllowEditPMPrice()}">checked</c:if> hidden/> 
									Is Null
								</label>
							</div>
							<div class="form-group">
								<label class="col-md-3 control-label">Previous S Price:</label>
								<div class=" col-md-4">
									<fmt:formatNumber value="${indoorQuotationRecord.previousSPrice}" var="psp" type="number" groupingUsed = "false" maxFractionDigits="2"/>
									<input type="text" class="form-control" name="previousSPrice" value="${psp}" <c:if test="${!unit.isAllowEditPMPrice()}">disabled</c:if>>
									<!-- <input type="text" class="form-control" name="previousSPrice" value="${indoorQuotationRecord.previousSPrice}" <c:if test="${!unit.isAllowEditPMPrice()}">disabled</c:if>> -->
								</div>
								<div class="col-md-1">
									<div class="btn-copy copy-sPrice "><i class="fa fa-copy"></i></div>
								</div>
								<label class="col-md-2 control-label">
									<input type="checkbox" name="isNullPreviousSPrice" value="True" <c:if test="${indoorQuotationRecord.isNullPreviousSPrice() or !unit.isAllowEditPMPrice()}">checked</c:if>  <c:if test="${!unit.isAllowEditPMPrice()}">disabled</c:if>>
									<input type="checkbox" name="nullPreviousSPrice" value="True" <c:if test="${indoorQuotationRecord.isNullPreviousSPrice() or !unit.isAllowEditPMPrice()}">checked</c:if> hidden/>
									Is Null
								</label>
							</div>
						</div>
						<div class="col-md-2">
							<div class="form-group">
								<label class="col-md-12" style="padding-top: 7px;">Last N Price: <span class="lastNPrice"><c:out value="${indoorQuotationRecord.lastNPrice}"/></span></label>
							</div>
							<div class="form-group">
								<label class="col-md-12" style="padding-top: 7px;">Last S Price: <span class="lastSPrice"><c:out value="${indoorQuotationRecord.lastSPrice}"/></span></label>
							</div>
						</div>
						<div class="col-md-2">
							<c:if test="${unit.isSpicingRequired()}">
							<div class="form-group">
								<label class="col-md-12 control-label" style="text-align:left"><input type="checkbox" class="SpicingRequire" name="spicing" value="True" <c:if test="${indoorQuotationRecord.isSpicing()}">checked</c:if> >Splicing</label>
							</div>
							</c:if>
							<c:if test="${unit.dataTransmissionRule == 'C'}">
							<div class="form-group">
								<label class="col-md-12 control-label" style="text-align:left"><input type="checkbox" <c:if test="${indoorQuotationRecord.isCurrentPriceKeepNo}">checked</c:if> disabled>Current Price Keep Record</label>
							</div>
							</c:if>
						</div>
					</div>
					<div class="form-group">
						<label class="col-md-1">Remarks:</label>
						<div class="col-md-6">
							<div class="input-group">
								<input class="form-control" name="remark" value="<c:out value="${indoorQuotationRecord.remark}"></c:out>"/>
								<div class="input-group-addon btn-reason-lookup" data-unitid="${unit.unitId}">
									<i class="fa fa-search"></i>
								</div>
							</div>
						</div>
					</div>
					<div class="form-group">
						<label class="col-md-1">Indoor Remarks:</label>
						<div class="col-md-6">
							
								<c:if test="${indoorQuotationRecord.status == 'Complete'}">
									<input class="form-control" name="indoorRemark" value="<c:out value="${quotation.oldFormSequence}"></c:out>"/>
								</c:if>
								<c:if test="${indoorQuotationRecord.status != 'Complete'}">
									<label>${quotation.oldFormSequence}</label>
								</c:if>
							
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>