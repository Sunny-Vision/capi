<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="form-group">
	<div class="col-sm-11 col-sm-offset-1">
		<label class="checkbox-inline">
			<input type="checkbox" name="uom1Reported" value="True" <c:if test="${model.uom1Reported}">checked</c:if>> Unit of measurement (UOM1) should be reported
		</label>
	</div>
</div>
<div class="form-group">
	<div class="col-sm-11 col-sm-offset-1">
		<label class="checkbox-inline">
			<input type="checkbox" name="uom2GreaterZero" value="True" <c:if test="${model.uom2GreaterZero}">checked</c:if>> Unit of measurement (UOM2) > 0
		</label>
	</div>
</div>
<div class="form-group">
	<div class="col-sm-11 col-sm-offset-1">
		<label class="checkbox-inline">
			<input type="checkbox" name="NPriceGreaterZero" value="True" <c:if test="${model.NPriceGreaterZero}">checked</c:if>> Normal Price (N Price) > 0
		</label>
	</div>
</div>
<div class="form-group">
	<div class="col-sm-11 col-sm-offset-1">
		<label class="checkbox-inline">
			<input type="checkbox" name="SPriceGreaterZero" value="True" <c:if test="${model.SPriceGreaterZero}">checked</c:if>> Special Price (S Price) > 0
		</label>
	</div>
</div>
<div class="form-group form-inline">
	<div class="col-sm-11 col-sm-offset-1">
		<label class="checkbox-inline">
			<input type="checkbox" name="provideReasonPRNPrice" value="True" <c:if test="${model.provideReasonPRNPrice}">checked</c:if>> Provide Reason if percentage change of N Price > 
		</label>
		<input name="prNPriceThreshold" value="<c:out value="${model.prNPriceThreshold}" />" type="text" class="form-control input-num" data-rule-number="true"
			data-rule-required="[name='provideReasonPRNPrice']:checked" />
		%
	</div>
</div>
<div class="form-group form-inline">
	<div class="col-sm-11 col-sm-offset-1">
		<label class="checkbox-inline">
			<input type="checkbox" name="provideReasonPRNPriceLower" value="True" <c:if test="${model.provideReasonPRNPriceLower}">checked</c:if>> Provide Reason if percentage change of N Price < 
		</label>
		<input name="prNPriceLowerThreshold" value="<c:out value="${model.prNPriceLowerThreshold}" />" type="text" class="form-control input-num" data-rule-number="true"
			data-rule-required="[name='provideReasonPRNPriceLower']:checked" />
		%
	</div>
</div>
<div class="form-group form-inline">
	<div class="col-sm-11 col-sm-offset-1">
		<label class="checkbox-inline">
			<input type="checkbox" name="provideReasonPRSPrice" value="True" <c:if test="${model.provideReasonPRSPrice}">checked</c:if>> Provide Reason if percentage change of S Price > 
		</label>
		<input name="prSPriceThreshold" value="<c:out value="${model.prSPriceThreshold}" />" type="text" class="form-control input-num" data-rule-number="true"
			 data-rule-required="[name='provideReasonPRSPrice']:checked" />
		%
	</div>
</div>
<div class="form-group form-inline">
	<div class="col-sm-11 col-sm-offset-1">
		<label class="checkbox-inline">
			<input type="checkbox" name="provideReasonPRSPriceLower" value="True" <c:if test="${model.provideReasonPRSPriceLower}">checked</c:if>> Provide Reason if percentage change of S Price < 
		</label>
		<input name="prSPriceLowerThreshold" value="<c:out value="${model.prSPriceLowerThreshold}" />" type="text" class="form-control input-num" data-rule-number="true"
			data-rule-required="[name='provideReasonPRSPriceLower']:checked" />
		%
	</div>
</div>
<div class="form-group">
	<div class="col-sm-11 col-sm-offset-1">
		<label class="checkbox-inline">
			<input type="checkbox" name="provideReasonSPriceMaxMin" value="True" <c:if test="${model.provideReasonSPriceMaxMin}">checked</c:if>> Provide Reason if S price is > max or < min S price of the same Variety in the last month
		</label>
	</div>
</div>
<div class="form-group">
	<div class="col-sm-11 col-sm-offset-1">
		<label class="checkbox-inline">
			<input type="checkbox" name="provideReasonNPriceMaxMin" value="True" <c:if test="${model.provideReasonNPriceMaxMin}">checked</c:if>> Provide Reason if N price is > max or < min N price of the same Variety in the last month
		</label>
	</div>
</div>
<div class="form-group">
	<div class="col-sm-11 col-sm-offset-1">
		<label class="checkbox-inline">
			<input type="checkbox" name="nPriceGreaterSPrice" value="True" <c:if test="${model.nPriceGreaterSPrice}">checked</c:if>> Normal price (N Price) >= Special Price (S Price)
		</label>
	</div>
</div>
<div class="form-group">
	<div class="col-sm-11 col-sm-offset-1">
		<label class="checkbox-inline">
			<input type="checkbox" name="provideRemarkForNotSuitablePrice" value="True" <c:if test="${model.provideRemarkForNotSuitablePrice}">checked</c:if>> If "Not suitable" is chosen for N and S price, remarks have to be provided.
		</label>
	</div>
</div>
<div class="form-group">
	<div class="col-sm-11 col-sm-offset-1">
		<label class="checkbox-inline">
			<input type="checkbox" name="provideRemarkForNotAvailableQuotation" value="True" <c:if test="${model.provideRemarkForNotAvailableQuotation}">checked</c:if>> If the quotation record is "Not available", no field except remarks should be filled.
		</label>
	</div>
</div>
<div class="form-group">
	<div class="col-sm-11 col-sm-offset-1">
		<label class="checkbox-inline">
			<input type="checkbox" name="reminderForPricingCycle" value="True" <c:if test="${model.reminderForPricingCycle}">checked</c:if>> If the pricing cycle of a product is longer than the specified product cycle for different Variety, reminder will be provided.
		</label>
	</div>
</div>
<div class="form-group form-inline">
	<div class="col-sm-11 col-sm-offset-1">
		<label class="checkbox-inline">
			<input type="checkbox" name="provideReasonPRSPriceSD" value="True" <c:if test="${model.provideReasonPRSPriceSD}">checked</c:if>> Provide Reason if percentage change of S price exceed<br/>the ranges of
			
			(mean + 
			<input name="prSPriceSDPositive" value="<c:out value="${model.prSPriceSDPositive}" />" type="text" class="form-control input-num" data-rule-number="true"
				data-rule-required="[name='provideReasonPRSPriceSD']:checked" /> 
			/ - 
			<input name="prSPriceSDNegative" value="<c:out value="${model.prSPriceSDNegative}" />" type="text" class="form-control input-num" data-rule-number="true"
				data-rule-required="[name='provideReasonPRSPriceSD']:checked" /> 
			S.D.) in last
			<input name="prSPriceMonth" value="<c:out value="${model.prSPriceMonth}" />" type="text" class="form-control input-num" data-rule-digits="true"
				data-rule-required="[name='provideReasonPRSPriceSD']:checked" />
			months
		</label>
	</div>
</div>
<div class="form-group form-inline">
	<div class="col-sm-11 col-sm-offset-1">
		<label class="checkbox-inline">
			<input type="checkbox" name="provideReasonPRNPriceSD" value="True" <c:if test="${model.provideReasonPRNPriceSD}">checked</c:if>> Provide Reason if percentage change of N price exceed<br/>the ranges of
			
			(mean + 
			<input name="prNPriceSDPositive" value="<c:out value="${model.prNPriceSDPositive}" />" type="text" class="form-control input-num" data-rule-number="true"
				data-rule-required="[name='provideReasonPRNPriceSD']:checked" /> 
			/ - 
			<input name="prNPriceSDNegative" value="<c:out value="${model.prNPriceSDNegative}" />" type="text" class="form-control input-num" data-rule-number="true"
				data-rule-required="[name='provideReasonPRNPriceSD']:checked" /> 
			S.D.) in last
			<input name="prNPriceMonth" value="<c:out value="${model.prNPriceMonth}" />" type="text" class="form-control input-num" data-rule-digits="true"
				data-rule-required="[name='provideReasonPRNPriceSD']:checked" />
			months
		</label>
	</div>
</div>
<div class="form-group form-inline">
	<div class="col-sm-11 col-sm-offset-1">
		<label class="checkbox-inline">
			<input type="checkbox" name="provideReasonSPriceSD" value="True" <c:if test="${model.provideReasonSPriceSD}">checked</c:if>> Provide Reason if S price exceed the ranges of 
			
			(
			<input name="sPriceSDNegative" value="<c:out value="${model.sPriceSDNegative}" />" type="text" class="form-control input-num" data-rule-number="true"
				data-rule-required="[name='provideReasonSPriceSD']:checked"
				data-rule-smaller_than_or_eq="[name='sPriceSDPositive']" /> 			
			- 
			<input name="sPriceSDPositive" value="<c:out value="${model.sPriceSDPositive}" />" type="text" class="form-control input-num" data-rule-number="true"
				data-rule-required="[name='provideReasonSPriceSD']:checked"
				data-rule-not_smaller_than_or_eq="[name='sPriceSDNegative']" /> 
			)
		</label>
	</div>
</div>
<div class="form-group form-inline">
	<div class="col-sm-11 col-sm-offset-1">
		<label class="checkbox-inline">
			<input type="checkbox" name="provideReasonNPriceSD" value="True" <c:if test="${model.provideReasonNPriceSD}">checked</c:if>> Provide Reason if N price exceed the ranges of 
			(
			 <input name="nPriceSDNegative" value="<c:out value="${model.nPriceSDNegative}" />" type="text" class="form-control input-num" data-rule-number="true"
				data-rule-required="[name='provideReasonNPriceSD']:checked"
				data-rule-smaller_than_or_eq="[name='nPriceSDPositive']" /> 
			- 
			<input name="nPriceSDPositive" value="<c:out value="${model.nPriceSDPositive}" />" type="text" class="form-control input-num" data-rule-number="true"
				data-rule-required="[name='provideReasonNPriceSD']:checked"
				data-rule-not_smaller_than_or_eq="[name='nPriceSDNegative']" />
			)
		</label>
	</div>
</div>