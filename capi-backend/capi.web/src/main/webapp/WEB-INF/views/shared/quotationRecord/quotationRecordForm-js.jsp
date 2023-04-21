<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script>
(function($, undefined){
	var Plugin = function(element, options) {
		this._process_options(options);

		this.$element = $(element);
		this.$mainForm = $('.quotation-record-main-form', this.$element);
		this.disabled = this.$element.prop('disabled') || this.$element.attr('disabled') == 'disabled' || this.o.quotationRecordFormContainerPlugin.isReadonly();
		this.$copyTarget = null;
		this.isBackNo = $('.is-back-no', this.$element).val() == 'true';

		var plugin = this;
		
		var isCurrent = this.$element.closest('.quotation-current-container').length > 0;
		
		var isProvideRemarkForNotAvailableQuotation = $('[name="provideRemarkForNotAvailableQuotation"]', this.$mainForm).val() == 'true';
		
		$("input[name$='.nPrice'],input[name$='.sPrice']", this.$mainForm).val(function(){
			var value = $(this).val();
			
			if (!( value == null || isNaN(value) || value.trim().length === 0 )) {
				var number = +value;
				
				if ( typeof number === "number" ) {
					if ( number % 1 !== 0 ) {
						return number.toFixed(4);
					} else {
						return number;
					}
				}
			}
			
			return value;
		});
		
		$('.btn-show-discount-calculator', this.$mainForm).discountCalculator({
			paramCallback: function(dataModel) {
				dataModel.nPrice = $('[name$=".nPrice"]', plugin.$mainForm).val();
				dataModel.sPrice = $('[name$=".sPrice"]', plugin.$mainForm).val();
				dataModel.discount = $('[name$=".discount"]', plugin.$mainForm).val();
				dataModel.discountRemark = $('[name$=".discountRemark"]', plugin.$mainForm).val();
			},
			resultCallback: function(dataModel) {
				$('[name$=".nPrice"]', plugin.$mainForm).val(dataModel.nPrice).trigger('change');
				$('[name$=".sPrice"]', plugin.$mainForm).val(dataModel.sPrice).trigger('change');
				$('[name$=".discount"]', plugin.$mainForm).val(dataModel.discount).trigger('change');
				$('[name$=".discountRemark"]', plugin.$mainForm).val(dataModel.discountRemark).trigger('change');
				
				$('.sub-price-dialog-container .datarow', plugin.$element).remove();
				$.proxy(plugin.updatePriceReadOnly, plugin)();
			},
			reasonLookupQueryDataCallback: function(dataModel) {
				if ($('[name="outletTypeShortCodeCSV"]', plugin.$mainForm).val() == '')
					dataModel.outletTypeId = [];
				else
					dataModel.outletTypeId = $('[name="outletTypeShortCodeCSV"]', plugin.$mainForm).val().split(',');
			},
			isProvideRemarkForNotAvailableQuotation:isProvideRemarkForNotAvailableQuotation
		});
		
		this.$subPriceDialog = $('.sub-price-lookup', this.$mainForm).subPriceDialog({
			resultCallback: function(dataModel, lastCalculationError) {
				$('[name$=".discount"]', plugin.$mainForm).val('').trigger('change');
				$('[name$=".discountRemark"]', plugin.$mainForm).val('').trigger('change');
				
				plugin.updatePriceReadOnly();
				if (dataModel == null) {
					if (lastCalculationError == 'empty_input') {
						return;
					}
					bootbox.alert({
						title: "Alert",
						message: "nPrice sPrice Calculation error " + (lastCalculationError != null ? lastCalculationError : '')
					});
					return;
				}
				$('[name$=".nPrice"]', plugin.$mainForm).val(dataModel.nPrice).trigger('change');
				$('[name$=".sPrice"]', plugin.$mainForm).val(dataModel.sPrice).trigger('change');
			}
		});
		
		if (this.disabled) {
			$('.price-table', this.$mainForm).DataTable({
				"buttons": [],
				"ordering": false,
				"serverSide": false,
				"searching": false,
				"processing": false,
				"paging": false,
				"info": false
			});
		} else {
			$.fn.dataTable.AutoFill.initFillInput();
			
			$('.price-table', this.$mainForm).DataTable({
				"buttons": [],
				"ordering": false,
				"serverSide": false,
				"searching": false,
				"processing": false,
				"paging": false,
				"info": false,
				"autoFill": {
					columns: [1]
				}
			});
			
			$('[name$=".SPricePeculiar"]', this.$mainForm).change($.proxy(this.SPricePeculiarChanged, this));
		}
		
		$('[name$=".consignmentCounter"]', this.$mainForm).change(function() {
			$('[name$=".consignmentCounterRemark"]', plugin.$mainForm).prop('readonly', !$(this).prop('checked'));
			$('[name$=".consignmentCounterName"]', plugin.$mainForm).prop('readonly', !$(this).prop('checked'));
			
			if ($(this).prop('checked')) {
				$('.consignmentCounterRemark-container', plugin.$mainForm).show();
			} else {
				$('.consignmentCounterRemark-container', plugin.$mainForm).hide();
				$('[name$=".consignmentCounterName"]', plugin.$mainForm).val('');
			}
			
			if (this.checked){
				$(".frFields", plugin.$mainForm).show();
			}
			else{
				var useFRAdmin = $('.useFRAdmin', plugin.$mainForm).val();
				if (useFRAdmin == "1"){
					$(".frFields", plugin.$mainForm).hide();
				}
			}
			
		});
		if ($('[name$=".consignmentCounter"]', this.$mainForm).prop('checked')) {
			$('.consignmentCounterRemark-container', plugin.$mainForm).show();
		} else {
			$('.consignmentCounterRemark-container', plugin.$mainForm).hide();
		}
		
		$('[name$=".nPrice"]', this.$mainForm).on('change triggerCalculatePercent', $.proxy(this._calculateNPricePercent, this));
		$('[name$=".sPrice"]', this.$mainForm).on('change triggerCalculatePercent', $.proxy(this._calculateSPricePercent, this));
		
		$('.btn-copy.copy-name-value', this.$mainForm).click(function() {
			var p = $(this).closest('.form-group').find('p');
			var name = p.attr('name');
			var val = p.html();
			if (val!==undefined){
				val = val.replace("&lt;", "<");       
				val = val.replace("&gt;", ">");
				val = val.replace("&amp;", "&");
			}
			$.proxy(plugin.o.copyNameValueCallback, plugin, name, val)();
			
			/*if ($input.prop('type') == 'checkbox') {
				$.proxy(plugin.o.copyNameValueCallback, plugin, name, $input.prop('checked'))();
			} else {
				$.proxy(plugin.o.copyNameValueCallback, plugin, name, val)();
			}*/
		});
		
		$('.btn-copy.copy-SPricePeculiar', this.$mainForm).click(function() {
			var $input = $(this).closest('.form-group').find('[name$="SPricePeculiar"]');
			$.proxy(plugin.o.copySPricePeculiarCallback, plugin, $input.prop('checked'))();
		});
		$('.btn-copy.copy-uom', this.$mainForm).click(function() {
			var $input = $(this).closest('.form-group').find('[name$=".uomValue"]');
			var uomValue = $input.val();
			var $input = $(this).closest('.form-group').find('[name$=".uomId"]');
			var uomId = $input.val();
			var uomIdLabel = $input.find(':selected').text();
			$.proxy(plugin.o.copyUOMCallback, plugin, uomValue, uomId, uomIdLabel)();
		});
		$('.btn-copy.copy-fr', this.$mainForm).click(function() {
			var $input = $(this).closest('.form-group').find('[name$=".fr"]');
			var fr = $input.val();
			var $input = $(this).closest('.form-group').find('[name$=".FRPercentage"]:checked');
			var FRPercentage = $input.val();
			$.proxy(plugin.o.copyFrCallback, plugin, fr, FRPercentage)();
		});
		$('.btn-copy.copy-discount', this.$mainForm).click(function() {
			var $subPriceDialogContainer = $('.sub-price-dialog-container', plugin.$element);
			var discount = $('[name$=".discount"]', plugin.$mainForm).val();
			var discountRemark = $('[name$=".discountRemark"]', plugin.$mainForm).val();
			$.proxy(plugin.o.copyDiscountCallback, plugin, $subPriceDialogContainer, discount, discountRemark)();
		});
		$('.btn-copy.copy-availability', this.$mainForm).click(function() {
			var $input = $(this).closest('.form-group').find('[name$="availability"]');
			$.proxy(plugin.o.copyAvailabilityCallback, plugin, $input.val())();
		});
		$('.btn-copy.copy-consignment-counter', this.$mainForm).click(function() {
			var $input = $(this).closest('.form-group').find('[name$="consignmentCounter"]');
			var $nameInput = $(this).closest('.form-group').find('[name$="consignmentCounterName"]');
			$.proxy(plugin.o.copyConsignmentCounterCallback, plugin, $input.prop('checked'), $nameInput.val())();
		});

		$('.btn-copy.copy-nprice', this.$mainForm).click(function() {
			var $input = $(this).closest('.form-group').find('[name$=".nPrice"]');
			var price = $input.val();
			$.proxy(plugin.o.copyNPriceCallback, plugin, price)();
		});

		$('.btn-copy.copy-sprice', this.$mainForm).click(function() {
			var $input = $(this).closest('.form-group').find('[name$=".sPrice"]');
			var price = $input.val();
			$.proxy(plugin.o.copySPriceCallback, plugin, price)();
		});
		
		var categoryIds = $('[name$=".uomId"]', this.$mainForm).data('categoryIds');
		if (categoryIds == '' || categoryIds == null) {
			categoryIds = null;
		} else {
			if (typeof categoryIds == 'string')
				categoryIds = categoryIds.split(',');
			else
				categoryIds = [categoryIds];
		}
		$('[name$=".uomId"]', this.$mainForm).select2ajax({
			ajax: {
			    data: function (params) {
			    	params['categoryIds'] = categoryIds;
			    	return params;
				},
				method: 'GET',
				contentType: 'application/json'
			}
		});
		
		$('[name$=".uomId"],[name$=".uomValue"]', this.$mainForm).change(function() {
			$('[name$=".nPrice"]', plugin.$mainForm).trigger('triggerCalculatePercent');
			$('[name$=".sPrice"]', plugin.$mainForm).trigger('triggerCalculatePercent');
		});
		
		$('.btn-copy-original', this.$mainForm).click(function() {
			var originalFormPlugin = plugin.o.$originalForm.quotationRecordForm('plugin');
			
			$.proxy(originalFormPlugin.setCopyTarget, originalFormPlugin, plugin.$element)();

			$('.btn-copy.copy-availability', this.$mainForm).trigger('click');
			
			$('.btn-copy', plugin.o.$originalForm).each(function() {
				$(this).trigger('click');
			});
			

			$.proxy(originalFormPlugin.setCopyTarget, originalFormPlugin, null)();
		});
		
		$('.btn-reason-lookup', this.$mainForm).priceReasonLookup({
			queryDataCallback: function(dataModel) {
				dataModel.reasonType = 'Price';
				if ($('[name="outletTypeShortCodeCSV"]', plugin.$mainForm).val() == '')
					dataModel.outletTypeId = [];
				else
					dataModel.outletTypeId = $('[name="outletTypeShortCodeCSV"]', plugin.$mainForm).val().split(',');
			},
			resultCallback: function(data) {
				var $input = this.$element.closest('.input-group').find('input:first');
				$input.val(data).trigger('change');
			}
		});
		
		if (this.o.$quotationHistoryContainer != null) {
			this.o.$quotationHistoryContainer.on('historyChanged', function() {
				if (plugin.$element.hasClass('active')) {
					$('[name$=".nPrice"]', plugin.$mainForm).trigger('triggerCalculatePercent');
					$('[name$=".sPrice"]', plugin.$mainForm).trigger('triggerCalculatePercent');
				}
			});
			this.o.$quotationHistoryContainer.on('backNoTabChanged', function() {
				if (plugin.$element.hasClass('active')) {
					$('[name$=".nPrice"]', plugin.$mainForm).trigger('triggerCalculatePercent');
					$('[name$=".sPrice"]', plugin.$mainForm).trigger('triggerCalculatePercent');
				}
			});
			
			if (this.o.quotationRecordFormContainerPlugin != null) {
				this.o.quotationRecordFormContainerPlugin.$element.on('backNoTabChanged', function() {
					if (plugin.$element.hasClass('active')) {
						$('[name$=".nPrice"]', plugin.$mainForm).trigger('triggerCalculatePercent');
						$('[name$=".sPrice"]', plugin.$mainForm).trigger('triggerCalculatePercent');
					}
				});
			}
		}
		
		function availabilityDisableFields(value, onload) {
			// TIR68 Point 1 Sub-price, Discount & Reason lookup button should be disabled for “Availability = 缺貨 / Not Suitable / 回倉 / 未返”.
			// TIR76 fields of Final N/S price should be disabled.
			// TIR90 Point 1.3 clear subprice when change availability
			if (value == 4 || value == 5 || value == 6 || value == 8 || value == 9) {
				// clear fields
				if (onload == undefined || onload == false) {
					$('[name$=".reason"],[name$=".discount"],[name$=".sPrice"],[name$=".nPrice"],[name$=".uomValue"],[name$=".uomId"]', plugin.$mainForm).val('').trigger('change').valid();
					plugin.$subPriceDialog.subPriceDialog('clearSource');
					plugin.updatePriceReadOnly();
				}

				$('[name$=".reason"],[name$=".discount"],.btn-reason-lookup,.btn-show-discount-calculator,.sub-price-lookup,[name$=".nPrice"],[name$=".sPrice"],[name$=".uomValue"],[name$=".uomId"]', plugin.$mainForm).prop('disabled', true);
			}
			if (onload == undefined || onload == false) {
				if (!(value == 4 || value == 5 || value == 6 || value == 8)) {
					$('[name$=".reason"],[name$=".discount"],.btn-reason-lookup,.btn-show-discount-calculator,.sub-price-lookup,[name$=".nPrice"],[name$=".sPrice"],[name$=".uomValue"],[name$=".uomId"]', plugin.$mainForm).prop('disabled', false);
					
				}
			}
			
			// On-spot Validation: If the quotation record is "Not available", no field except remarks should be filled.
			if (!isProvideRemarkForNotAvailableQuotation) return;
			if (plugin.isBackNo) return;
			
			var availability = value;
			var disable = !(availability == 1 || availability == 2 || availability == 3);
			
			if (disable) {
				var formDisplay = +$('[name="quotationRecord.formDisplay"]').val();
				
				if (formDisplay === 1) {
					$('[name$=".nPrice"]', plugin.$mainForm).val('').trigger('change').valid();
					$('[name$=".sPrice"]', plugin.$mainForm).val('').trigger('change').valid();
				}
					
				$('[name$=".reason"]', plugin.$mainForm).val('').trigger('change').valid();
				plugin.$subPriceDialog.subPriceDialog('clearSource');
				plugin.updatePriceReadOnly();

				$('input:visible,select,textarea'
						+ ',.btn-reason-lookup,.btn-show-discount-calculator,.sub-price-lookup'
						+ ',.btn-copy-original,btn-copy-n-to-sprice', plugin.$mainForm)
					.not('[name$="availability"],.availability-select,.availability-checkbox'
							+ ',[name$="remark"],[name$="visited"],[name$="verificationReply"]'
							+ ',[name$="productRemark"],[name$="productPosition"]', plugin.$mainForm)
					.prop('disabled', true);
				
				plugin.$element.trigger('disableAllTrue');
				plugin.o.quotationRecordFormContainerPlugin.setProductChange(false, true);
			} else {
				$('input:visible,select,textarea'
						+ ',.btn-reason-lookup,.btn-show-discount-calculator,.sub-price-lookup'
						+ ',.btn-copy-original,btn-copy-n-to-sprice', plugin.$mainForm)
					.not('[name$="availability"],.availability-select,.availability-checkbox'
							+ ',[name$="remark"]'
							+ ',[name$="productRemark"],[name$="productPosition"]', plugin.$mainForm)
					.prop('disabled', false);
				plugin.$element.trigger('disableAllFalse');
				plugin.o.quotationRecordFormContainerPlugin.showBackNoIfProductChange();
				plugin.updatePriceReadOnly();
			}
		}
		
		if (!this.disabled) {
			if (isCurrent) {
				availabilityDisableFields($('[name$="availability"]', this.$mainForm).val(), true);
			}
			
			$('.availability-checkbox', this.$mainForm).change(function() {
				var $formGroup = $(this).closest('.form-group');
				if ($(this).prop('checked')) {
					$('.availability-select', $formGroup).prop('disabled', true);
					$('[name$="availability"]', $formGroup).val($(this).val());
					$('[name$="availability"]', $formGroup).trigger('change');
				} else {
					$('.availability-select', $formGroup).prop('disabled', false);
					$('.availability-select', $formGroup).trigger('change');
				}
			});
			$('.availability-select', this.$mainForm).change(function() {
				var $formGroup = $(this).closest('.form-group');
				if (($(this).val() == '5') || ($(this).val() == '7')) {
					$quotationRecordForm = $(this).closest('.quotation-record-form');
					$priceTable = $('.price-table', $quotationRecordForm);
					$('input', $priceTable).prop('disabled', true);
					$('input', $priceTable).val('');
					$extraPriceTable = $('.extra-price-table', $quotationRecordForm);
					$('input', $extraPriceTable).prop('disabled', true);
					$('input', $extraPriceTable).val('');
				}
				$('[name$="availability"]', $formGroup).val($(this).val());
				$('[name$="availability"]', $formGroup).trigger('change');
				$.proxy(plugin.backNoChangeProductAvailabilityLogic, plugin)();
			});
			
			$('[name$="availability"]', this.$mainForm).change(function() {
				availabilityDisableFields($(this).val());
			});
		}

		if (!this.disabled) {
			$('.btn-copy-n-to-sprice', this.$mainForm).click(function() {
				if ($('[name$=".sPrice"]', plugin.$mainForm).prop('readonly')) return;
				$('[name$=".sPrice"]', plugin.$mainForm).val($('[name$=".nPrice"]', plugin.$mainForm).val());
				$('[name$=".sPrice"]', plugin.$mainForm).trigger('change');
			});
		}
		
		$('.text-viewer', this.$mainForm).textViewer();

		if (this.disabled) {
			return;
		}
		
		function firmStatusDisableFields() {
			var firmStatus = + $('.firm-status', plugin.$mainForm).val();
			if (firmStatus == 2 || firmStatus == 3 || firmStatus == 4 || firmStatus == 5 || firmStatus == 6 || firmStatus == 7
					|| firmStatus == 8 || firmStatus == 10) {
				var $fields = $('input:visible,select,textarea'
						+ ',.btn-reason-lookup,.btn-show-discount-calculator,.sub-price-lookup'
						+ ',.btn-copy-original,btn-copy-n-to-sprice', plugin.$mainForm)
						.not('[name$="remark"]', plugin.$mainForm);
				$fields.prop('disabled', true);
				plugin.$element.trigger('disableAllTrue');
			} else if (firmStatus == 9) {
				$fields = $('[name$="availability"],.availability-select,.availability-checkbox', plugin.$mainForm);
				$fields.prop('disabled', true);
			}
		}
		firmStatusDisableFields();
		
		$(".nPrice").on('change', function() {
			var nPrice = $(this).val();
			var noOfDecimal;
			if(nPrice.indexOf(".") >= 0) {
				noOfDecimal = (nPrice).split(".")[1].length;
			} else {
				noOfDecimal = 0;
			}
			var newNPrice;
			if(noOfDecimal == 4) {
				if((nPrice).split(".")[1] == "0000") {
					newNPrice = Math.floor((+nPrice));
					$(this).val(newNPrice);
				}
			} else if(noOfDecimal > 4) {
				//newNPrice = (+nPrice).toFixed(2).toString();
				newNPrice = (Math.round((+nPrice) * 10000) / 10000.0).toString();
				if(newNPrice.split(".")[1] == "0000") {
					newNPrice = Math.floor((+newNPrice));
				}
				$(this).val(newNPrice);
			}
		});
		$(".sPrice").on('change', function() {
			var sPrice = $(this).val();
			var noOfDecimal;
			if(sPrice.indexOf(".") >= 0) {
				noOfDecimal = (sPrice).split(".")[1].length;
			} else {
				noOfDecimal = 0;
			}
			var newSPrice;
			if(noOfDecimal == 4) {
				if((sPrice).split(".")[1] == "0000") {
					newSPrice = Math.floor((+sPrice));
					$(this).val(newSPrice);
				}
			} else if(noOfDecimal > 4) {
				//newSPrice = (+sPrice).toFixed(2).toString();
				newSPrice = (Math.round((+sPrice) * 10000) / 10000.0).toString();
				if(newSPrice.split(".")[1] == "0000") {
					newSPrice = Math.floor((+newSPrice));
				}
				$(this).val(newSPrice);
			}
		});
		
		$('.product-form').on('change notchange', $.proxy(plugin.productChanged, plugin));
		plugin.refreshAvailabilityContent(true);
		plugin.backNoChangeProductAvailabilityLogic(true);
	};
	Plugin.prototype = {
		constructor : Plugin,
		_process_options : function(opts) {
			this._o = $.extend({}, this._o, opts);
			var o = this.o = $.extend({}, this._o);
		},
		plugin: function(){return this;},
		updatePriceReadOnly: function() {
			var hasSubPrice = $('.sub-price-dialog-container .datarow', this.$element).length > 0;
			var hasDiscount = $('[name$=".discount"]', this.$mainForm).val() != '';
			var isPeculiar = $('[name$=".SPricePeculiar"]', this.$mainForm).prop('checked');
			
			if (hasSubPrice || hasDiscount || isPeculiar) {
				$('[name$=".nPrice"]', this.$mainForm).prop('readonly', true);
				$('[name$=".sPrice"]', this.$mainForm).prop('readonly', true);
			} else {
				$('[name$=".nPrice"]', this.$mainForm).prop('readonly', false);
				$('[name$=".sPrice"]', this.$mainForm).prop('readonly', false);
			}
			if (isPeculiar) {
				$('.btn-show-discount-calculator', this.$mainForm).discountCalculator('setDisablePriceInput', true);
				$('.btn-show-discount-calculator', this.$mainForm).discountCalculator('setDisableReasonInput', true);
				$('.sub-price-lookup', this.$mainForm).subPriceDialog('setDisabled2', true);
				$('[name$=".uomValue"]', this.$mainForm).prop('readonly', true);
				$('[name$=".uomId"]', this.$mainForm).prop('disabled', true);
				$('[name$=".reason"]', this.$mainForm).val('').trigger('change').prop('disabled', true);
				$('[name$=".discountRemark"]', this.$mainForm).val('').trigger('change').valid();
			} else {
				$('.btn-show-discount-calculator', this.$mainForm).discountCalculator('setDisablePriceInput', false);
				$('.btn-show-discount-calculator', this.$mainForm).discountCalculator('setDisableReasonInput', false);
				$('.sub-price-lookup', this.$mainForm).subPriceDialog('setDisabled2', false);
				$('[name$=".uomValue"]', this.$mainForm).prop('readonly', false);
				$('[name$=".uomId"]', this.$mainForm).prop('disabled', false);
				$('[name$=".reason"]', this.$mainForm).prop('disabled', false);
			}
		},
		_calculateNPricePercent: function() {
			var plugin = this;
			
			if (plugin.o.quotationRecordFormContainerPlugin.isProductChange()) {
				$('.nPricePercent', plugin.$mainForm).text('NA');
				return;
			}
			
			var current = +$('[name$=".nPrice"]', this.$mainForm).val();
			var $historyMainForm = $('.quotation-record-form.active .quotation-record-main-form', this.o.$quotationHistoryContainer);
			var history = +$('[name$=".nPrice"]', $historyMainForm).val();
			
			if (isNaN(current) || isNaN(history) || history == 0) {
				$('.nPricePercent', this.$mainForm).text('NA');
			} else {
				var currentQuotationRecordUnitId = $('[name="unitId"]', this.$mainForm).val();
				var historyQuotationRecordId = $('[name$=".quotationRecordId"]', $historyMainForm).val();
				var uomId = $('[name$=".uomId"]', this.$mainForm).val();
				var uomValue = $('[name$=".uomValue"]', this.$mainForm).val();
				if (uomId == undefined) uomId = null;
				if (uomValue == undefined) uomValue == null;
				
				if (historyQuotationRecordId == '' || currentQuotationRecordUnitId == '') return;
				
				$.post(plugin.o.calculatePercentageChangeUrl,
					{
						historyQuotationRecordId: historyQuotationRecordId,
						currentQuotationRecordUnitId: currentQuotationRecordUnitId,
						historyValue: history,
						newValue: current,
						uomId: uomId,
						uomValue : uomValue
					}, function(result) {
						if (result == null) {
							$('.nPricePercent', plugin.$mainForm).text('NA');
						} else {
							$('.nPricePercent', plugin.$mainForm).text(result + '%');
						}
					});
			}
		},
		_calculateSPricePercent: function() {
			var plugin = this;
			
			if (plugin.o.quotationRecordFormContainerPlugin.isProductChange()) {
				$('.sPricePercent', plugin.$mainForm).text('NA');
				return;
			}
			
			var current = +$('[name$=".sPrice"]', this.$mainForm).val();
			var $historyMainForm = $('.quotation-record-form.active .quotation-record-main-form', this.o.$quotationHistoryContainer);
			var history = +$('[name$=".sPrice"]', $historyMainForm).val();
			
			if (isNaN(current) || isNaN(history) || history == 0) {
				$('.sPricePercent', this.$mainForm).text('NA');
			} else {
				var currentQuotationRecordUnitId = $('[name="unitId"]', this.$mainForm).val();
				var historyQuotationRecordId = $('[name$=".quotationRecordId"]', $historyMainForm).val();
				var uomId = $('[name$=".uomId"]', this.$mainForm).val();
				var uomValue = $('[name$=".uomValue"]', this.$mainForm).val();
				if (uomId == undefined) uomId = null;
				if (uomValue == undefined) uomValue == null;
				
				if (historyQuotationRecordId == '' || currentQuotationRecordUnitId == '') return;
				
				$.post(plugin.o.calculatePercentageChangeUrl,
					{
						historyQuotationRecordId: historyQuotationRecordId,
						currentQuotationRecordUnitId: currentQuotationRecordUnitId,
						historyValue: history,
						newValue: current,
						uomId: uomId,
						uomValue : uomValue
					}, function(result) {
						if (result == null) {
							$('.sPricePercent', plugin.$mainForm).text('NA');
						} else {
							$('.sPricePercent', plugin.$mainForm).text(result + '%');
						}
					});
			}
		},
		setCopyTarget: function($newTarget) {
			this.$copyTarget = $newTarget;
		},
		showExtraRow: function(show) {
			if (show)
				$('.extra-row', this.$mainForm).removeClass('hide');
			else
				$('.extra-row', this.$mainForm).addClass('hide');
		},
		copyNPrice: function() {
			$('.btn-copy-nprice', this.$element).click();
		},
		copySPrice: function() {
			$('.btn-copy-sprice', this.$element).click();
		},
		calculateDiscount: function() {
			var nPrice = +$('[name$=".nPrice"]', this.$mainForm).val();
			var discount = $('[name$=".discount"]', this.$mainForm).val();
			var $sPrice = $('[name$=".sPrice"]', this.$mainForm);
			if (discount == '') return;
			
			$('.btn-show-discount-calculator', this.$mainForm).discountCalculator('adHocCalculate', nPrice, discount, 
				function(sPrice) {
					if (sPrice == null) {
						alert('formula error');
						$sPrice.val('');
					} else {
						$sPrice.val(sPrice);
					}
					$sPrice.trigger('change');
				}
			);
		},
		SPricePeculiarChanged: function() {
			var plugin = this;
			
			var isChecked = $('[name$=".SPricePeculiar"]', plugin.$mainForm).prop('checked');
			
			if (!isChecked) {
				$.proxy(plugin.updatePriceReadOnly, plugin)();
				return;
			}
			bootbox.confirm({
			    title: "Confirmation",
			    message: "<spring:message code='W00039' />",
			    callback: function(result) {
			    	if(result === true) {
						$('[name$=".nPrice"],[name$=".sPrice"],[name$=".uomValue"],[name$=".uomId"],[name$=".discount"]', plugin.$mainForm).val('').trigger('change').valid();
						
						plugin.$subPriceDialog.subPriceDialog('clearSource');
						$.proxy(plugin.updatePriceReadOnly, plugin)();
					} else {
						$('[name$=".SPricePeculiar"]', plugin.$mainForm).prop('checked', false);
					}
			    }
			});
		},
		refreshAvailabilityContent: function(isInit) {
			var plugin = this;
			
			/**
			 * TIR47 Point 6
			 * In Quotation Form, if "Change Product" is "True", "Availability" must be "Available".
			 */
			
			if (!plugin.o.quotationRecordFormContainerPlugin.isCurrent()) return;
			
			if (this.isBackNo) return;
			
			var oldAvailability = $('.availability-select', plugin.$mainForm).val();
			if (plugin.o.quotationRecordFormContainerPlugin.isProductChange()) {
				var $deletedOptions = $('.availability-select', plugin.$mainForm).find('option').not('[value="1"]');
				var deletedOptionList = $deletedOptions.map(function() {
					return { label: $(this).html(), value: $(this).val() };
				}).get();
				
				if (!isInit) { $('.availability-checkbox',plugin.$mainForm).prop('checked', false).trigger('change'); }
				
				$('.availability-select', plugin.$mainForm).data('deletedOptionList', deletedOptionList);
				$deletedOptions.remove();
				
				$('.availability-select', plugin.$mainForm).prop('disabled', false);
			} else {
				var deletedOptionList = $('.availability-select', plugin.$mainForm).data('deletedOptionList');
				if (deletedOptionList != null) {
					for (var i = 0; i < deletedOptionList.length; i++) {
						$('.availability-select', plugin.$mainForm).append($('<option value="' + deletedOptionList[i].value + '">' + deletedOptionList[i].label + '</option>'));
					}
				}
				$('.availability-select', plugin.$mainForm).data('deletedOptionList', null);
			}
			
			var newAvailability = $('.availability-select', plugin.$mainForm).val();
			if (oldAvailability != newAvailability) {
				$('.availability-select', plugin.$mainForm).trigger('change');
			}
		},
		productChanged: function() {
			var plugin = this;
			plugin.refreshAvailabilityContent();
			plugin.backNoChangeProductAvailabilityLogic();
			$('[name$=".nPrice"]', plugin.$mainForm).trigger('triggerCalculatePercent');
			$('[name$=".sPrice"]', plugin.$mainForm).trigger('triggerCalculatePercent');
		},
		backNoChangeProductAvailabilityLogic: function(isInit) {
			var plugin = this;
			
			if (!this.isBackNo) return;
			
			/**
			 * TIR47 Point 7
			 * For “Change Product” quotation records in Quotation Form, in the “Back no.” tab, if “Availability” is “缺貨/NEW”, values of N/S price and Reason should be cleared and the fields should be locked.
			 */
			if (plugin.o.quotationRecordFormContainerPlugin.isProductChange()) {
				var availability = $('[name$="availability"]', plugin.$mainForm).val();
				if (availability == 4 || availability == 9) {
					$('[name$=".nPrice"]', this.$mainForm).val('').trigger('change').prop('disabled', true);
					$('[name$=".sPrice"]', this.$mainForm).val('').trigger('change').prop('disabled', true);
					$('[name$=".reason"]', this.$mainForm).val('').trigger('change').prop('disabled', true);
				} else {
					if (isInit == undefined) {
						$('[name$=".nPrice"]', this.$mainForm).prop('disabled', false);
						$('[name$=".sPrice"]', this.$mainForm).prop('disabled', false);
						$('[name$=".reason"]', this.$mainForm).prop('disabled', false);
					}
				}
			}
		}
	};
	
	function opts_from_el(el, prefix) {
		// Derive options from element data-attrs
		var data = $(el).data(), out = {}, inkey, replace = new RegExp('^' + prefix.toLowerCase() + '([A-Z])');
		prefix = new RegExp('^' + prefix.toLowerCase());
		function re_lower(_, a) {
			return a.toLowerCase();
		}
		for ( var key in data)
			if (prefix.test(key)) {
				inkey = key.replace(replace, re_lower);
				out[inkey] = data[key];
			}
		return out;
	}
	
	$.fn.quotationRecordForm = function(option) {
		var pluginDataName = 'quotationRecordForm';
		var args = Array.apply(null, arguments);
		args.shift();
		var internal_return;
		this.each(function() {
			var $this = $(this),
				data = $this.data(pluginDataName),
				options = typeof option === 'object' && option;

			if (!data) {
				var elopts = opts_from_el(this, pluginDataName),
					opts = $.extend({}, defaults, elopts, options);
				$this.data(pluginDataName, (data = new Plugin(this, opts)));
			}
			if (typeof option === 'string' && typeof data[option] === 'function') {
				internal_return = data[option].apply(data, args);
				if (internal_return !== undefined)
					return false;
			}
		});
		if (internal_return !== undefined)
			return internal_return;
		else
			return this;
	}
	$.fn.quotationRecordForm.Constructor = Plugin;

	var defaults = $.fn.quotationRecordForm.defaults = {
		$originalForm: null,
		copyNameValueCallback: function(name, value){},
		copyUOMCallback: function(uomValue, uomId, uomIdLabel){},
		copyFrCallback: function(fr, FRPercentage){},
		copyDiscountCallback: function($subPriceDialogContainer, discount, discountRemark){},
		copyAvailabilityCallback: function(value){},
		copyConsignmentCounterCallback: function(consignmentCounter, consignmentCounterName){},
		copyNPriceCallback: function(price){},
		copySPriceCallback: function(price){},
		$quotationHistoryContainer: null,
		quotationRecordFormContainerPlugin: null,
		calculatePercentageChangeUrl: "<c:url value='/shared/General/calculatePercentageChange'/>"
	};
}(window.jQuery));


(function($, undefined){
	var Plugin = function(element, options) {
		this._process_options(options);
		
		this.$element = $(element);
		
		var plugin = this;
		
		$('.btn-original', this.$element).click(function() {
			$('.btn-original', plugin.$element).addClass('active');
			$('.btn-back-no', plugin.$element).removeClass('active');
			$('.quotation-record-form.original', plugin.$element).addClass('active');
			$('.quotation-record-form.back-no', plugin.$element).removeClass('active');
			plugin.$element.trigger('backNoTabChanged');
		});
		
		$('.btn-back-no', this.$element).click(function() {
			$('.btn-back-no', plugin.$element).addClass('active');
			$('.btn-original', plugin.$element).removeClass('active');
			$('.quotation-record-form.back-no', plugin.$element).addClass('active');
			$('.quotation-record-form.original', plugin.$element).removeClass('active');
			plugin.$element.trigger('backNoTabChanged');
		});
		
		if ($('[name="quotationRecord.productChange"]', this.$element).val() == 'true' && $('[name="quotationRecord.formDisplay"]', this.$element).val() == 1
				&& $('[name="quotationRecord.backdateRequired"]', this.$element).val() == 'true') {
			$('.btn-back-no-container', this.$element).show();
		}

		$('.quotation-record-form', this.$element).quotationRecordForm({
			$originalForm: $('.quotation-record-form.original', this.$element),
			$quotationHistoryContainer: this.o.$quotationHistoryContainer,
			quotationRecordFormContainerPlugin: this,
			copyNameValueCallback: function(name, value) {
				var $target = this.$copyTarget != null ? $('.quotation-record-main-form', this.$copyTarget) : $('.quotation-record-form.active .quotation-record-main-form', plugin.o.currentQuotationRecordFormContainer);
				
				if ($('[name$="' + name + '"]', $target).prop('disabled')) return;
				$('[name$="' + name + '"]', $target).val(value).trigger('change');
				
				/*if ($('[name$="' + name + '"]', $target).prop('type') == 'checkbox') {
					$('[name$="' + name + '"]', $target).prop('checked', value).trigger('change');
				} else {
					$('[name$="' + name + '"]', $target).val(value).trigger('change');
				}*/
			},
			copySPricePeculiarCallback: function(value) {
				var $target = this.$copyTarget != null ? $('.quotation-record-main-form', this.$copyTarget) : $('.quotation-record-form.active .quotation-record-main-form', plugin.o.currentQuotationRecordFormContainer);
				
				if ($('[name$=".SPricePeculiar"]', $target).prop('disabled')) return;
				
				$('[name$=".SPricePeculiar"]', $target).prop('checked', value).trigger('change');
			},
			copyUOMCallback: function(uomValue, uomId, uomIdLabel) {
				var $target = this.$copyTarget != null ? $('.quotation-record-main-form', this.$copyTarget) : $('.quotation-record-form.active .quotation-record-main-form', plugin.o.currentQuotationRecordFormContainer);
				
				if ($('[name$=".uomValue"]', $target).prop('disabled')) return;
				
				$('[name$=".uomValue"]', $target).val(uomValue);
				if (uomId != '' && uomId != null) {
					$('[name$=".uomId"]', $target).html('<option value="' + uomId + '">' + uomIdLabel + '</option>').trigger('change');
				}
			},
			copyFrCallback: function(fr, FRPercentage) {
				var $target = this.$copyTarget != null ? $('.quotation-record-main-form', this.$copyTarget) : $('.quotation-record-form.active .quotation-record-main-form', plugin.o.currentQuotationRecordFormContainer);
				
				if ($('[name$=".fr"]', $target).prop('disabled')) return;
				$('[name$=".fr"]', $target).val(fr);
				$('[name$=".FRPercentage"]', $target).prop('checked', false);
				$('[name$=".FRPercentage"][value="' + FRPercentage + '"]', $target).prop('checked', true);
			},
			copyDiscountCallback: function($subPriceDialogContainer, discount, discountRemark) {
				var $target = this.$copyTarget != null ? this.$copyTarget : $('.quotation-record-form.active', plugin.o.currentQuotationRecordFormContainer);

				if ($('.quotation-record-main-form [name$=".discount"]', $target).prop('disabled')) return;
				
				var $targetSubPriceDialogContainer = $('.sub-price-dialog-container', $target);
				var isCopySubPrice = false;
				if ($('.modal .datatable input', $subPriceDialogContainer).length > 0) {
					var html = $('.modal .datatable', $subPriceDialogContainer).html();
					
					if ($('.modal .datatable [name^=backNoQuotationRecord]', $subPriceDialogContainer).length > 0) {
						if ($target.hasClass('original')) {
							html = html.replace(/backNoQuotationRecord/g, 'quotationRecord');
						}
					} else {
						if ($target.hasClass('back-no')) {
							html = html.replace(/quotationRecord/g, 'backNoQuotationRecord');
						}
					}
					$('.datatable', $targetSubPriceDialogContainer).html(html);
					
					$(':disabled', $targetSubPriceDialogContainer).prop('disabled', false);
					
					$('[name$=".subPriceRecordId"],[name$=".subPriceColumnId"]', $targetSubPriceDialogContainer).remove();
					
					isCopySubPrice = true;
				} else {
					$('.datatable tbody', $targetSubPriceDialogContainer).empty();
				}
				
				$('select[name="subPriceTypeId"]', $targetSubPriceDialogContainer).html($('select[name="subPriceTypeId"] option', $subPriceDialogContainer).clone());
				
				$('.quotation-record-main-form [name$=".discount"]', $target).val(discount).trigger('change');
				$('.quotation-record-main-form [name$=".discountRemark"]', $target).val(discountRemark).trigger('change');
				
				$target.quotationRecordForm('updatePriceReadOnly');
				
				if (isCopySubPrice) {
					this.copyNPrice();
					this.copySPrice();
				} else {
					$target.quotationRecordForm('calculateDiscount');
				}
			},
			copyAvailabilityCallback: function(value) {
				var $target = this.$copyTarget != null ? this.$copyTarget : $('.quotation-record-form.active', plugin.o.currentQuotationRecordFormContainer);
				
				if ($('.availability-checkbox', $target).prop('disabled')) return;
				
				if (value == 2) {
					$('.availability-checkbox', $target).prop('checked', true).trigger('change');
				} else {
					$('.availability-checkbox', $target).prop('checked', false).trigger('change');
					if ($('.availability-select', $target).find('[value="' + value + '"]').length > 0) {
						$('.availability-select', $target).val(value).trigger('change');
					}
				}
			},
			copyConsignmentCounterCallback: function(consignmentCounter, consignmentCounterName) {
				var $target = this.$copyTarget != null ? this.$copyTarget : $('.quotation-record-form.active', plugin.o.currentQuotationRecordFormContainer);
				
				if ($('[name$=".consignmentCounter"]', $target).prop('disabled')) return;
				
				$('[name$=".consignmentCounter"]', $target).prop('checked', consignmentCounter).trigger('change');
				$('[name$=".consignmentCounterName"]', $target).val(consignmentCounterName).trigger('change');
			},
			copyNPriceCallback: function(price) {
				var $target = this.$copyTarget != null ? this.$copyTarget : $('.quotation-record-form.active', plugin.o.currentQuotationRecordFormContainer);
				
				if ($('.quotation-record-main-form [name$=".nPrice"]', $target).prop('disabled')) return;
				
				if ($('.quotation-record-main-form [name$=".SPricePeculiar"]', $target).prop('checked')) return;
				
				$('.quotation-record-main-form [name$=".nPrice"]', $target).val(price).trigger('change');
			},
			copySPriceCallback: function(price) {
				var $target = this.$copyTarget != null ? this.$copyTarget : $('.quotation-record-form.active', plugin.o.currentQuotationRecordFormContainer);
				
				if ($('.quotation-record-main-form [name$=".sPrice"]', $target).prop('disabled')) return;
				
				if ($('.quotation-record-main-form [name$=".SPricePeculiar"]', $target).prop('checked')) return;
				
				$('.quotation-record-main-form [name$=".sPrice"]', $target).val(price).trigger('change');
			}
		});
		
		$('.btn-copy-all', this.$element).click(function() {
			bootbox.confirm({
			    title: "Confirmation",
			    message: "<spring:message code='W00037' />",
			    callback: function(result){
			    	if(result === true){
						var $activeQuotationRecordHistory = $('.quotation-record-form.active', plugin.o.$quotationHistoryContainer);
						if ($activeQuotationRecordHistory.length == 0) return;
						var quotationRecordFormPlugin = $activeQuotationRecordHistory.quotationRecordForm('plugin');
						$.proxy(quotationRecordFormPlugin.setCopyTarget, quotationRecordFormPlugin, $('.quotation-record-form.active', plugin.$element))();
						$('.copy-availability', quotationRecordFormPlugin.$element).click();
						$('.btn-copy', quotationRecordFormPlugin.$element).not('.copy-availability').each(function() {
							$(this).click();
						});
						$.proxy(quotationRecordFormPlugin.setCopyTarget, quotationRecordFormPlugin, null)();
			    	}
			    }
			});
		});
		
		if (plugin.o.$quotationHistoryContainer != null && !plugin.isReadonly()) {
			plugin.o.$quotationHistoryContainer.on('historyChanged', function() {
				$('.btn-copy-all', this.$element).removeClass('hide');
			});
			plugin.o.$quotationHistoryContainer.on('historyChanging', function() {
				$('.btn-copy-all', this.$element).addClass('hide');
			});
		}
		
		if (plugin.isReadonly()) {
			$('.btn-copy-all', this.$element).addClass('hide');
		}
	};
	Plugin.prototype = {
		constructor : Plugin,
		_process_options : function(opts) {
			this._o = $.extend({}, this._o, opts);
			var o = this.o = $.extend({}, this._o);
		},
		plugin: function(){return this;},
		formValidateIgnore: function(index, element) {
			if ($(element).prop('disabled')) return true;
			if ($(element).closest('.quotation-record-form.back-no').length == 1) {
				var $container = $(element).closest('.quotation-record-form-container');
				if ($('.btn-back-no-container', $container).is(':hidden')) return true;
			}
			return false;
		},
		formValidateInvalidHandler: function(event, validator) {
			var hasOriginalElement = false;
			var hasBackNumberElement = false;
			$(validator.errorList).each(function() {
				var $element = $(this.element);
				if ($element.closest('.quotation-record-form.back-no').length == 1)
					hasBackNumberElement = true;
				else if ($element.closest('.quotation-record-form.original').length == 1)
					hasOriginalElement = true;
			});
			
			var showingBackNo = $('.quotation-record-form.active', this.$element).hasClass('back-no');
			if (showingBackNo && hasOriginalElement && !hasBackNumberElement) {
				$('.btn-original', this.$element).click();
			} else if (!showingBackNo && !hasOriginalElement && hasBackNumberElement) {
				$('.btn-back-no', this.$element).click();
			}
		},
		setProductChange: function(productChange, onlyHideButton) {
			if (productChange) {
				if (onlyHideButton == undefined || onlyHideButton == false) {
					$('[name="quotationRecord.productChange"]', this.$element).val('true');
				}
				if ($('[name="quotationRecord.formDisplay"]', this.$element).val() == 1) {
					if ($('[name="quotationRecord.backdateRequired"]', this.$element).val() == 'true') {
						$('.btn-back-no-container', this.$element).show();
					}
				}
				$('.product-form').trigger('change');
				$('#btnResetChangeProduct', this.$mainForm).show();
				
			} else {
				if (onlyHideButton == undefined || onlyHideButton == false) {
					$('[name="quotationRecord.productChange"]', this.$element).val('false');
				}
				if ($('[name="quotationRecord.formDisplay"]', this.$element).val() == 1) {
					if ($('[name="quotationRecord.backdateRequired"]', this.$element).val() == 'true') {
						$('.btn-back-no-container', this.$element).hide();
					}
				}
				$('.product-form').trigger('notchange');
			}
		},
		showBackNoIfProductChange: function() {
			if ($('[name="quotationRecord.formDisplay"]', this.$element).val() == 1) {
				if ($('[name="quotationRecord.productChange"]', this.$element).val() == 'true') {
					if ($('[name="quotationRecord.backdateRequired"]', this.$element).val() == 'true') {
						$('.btn-back-no-container', this.$element).show();
					}
				}
			}
		},
		isReadonly: function() {
			return this.$element.hasClass('readonly-quotation-record-form-container');
		},
		isProductChange: function() {
			return $('[name="quotationRecord.productChange"]', this.$element).val() == 'true';
		},
		isCurrent: function() {
			return this.o.currentQuotationRecordFormContainer == null;
		}
	};
	
	function opts_from_el(el, prefix) {
		// Derive options from element data-attrs
		var data = $(el).data(), out = {}, inkey, replace = new RegExp('^' + prefix.toLowerCase() + '([A-Z])');
		prefix = new RegExp('^' + prefix.toLowerCase());
		function re_lower(_, a) {
			return a.toLowerCase();
		}
		for ( var key in data)
			if (prefix.test(key)) {
				inkey = key.replace(replace, re_lower);
				out[inkey] = data[key];
			}
		return out;
	}
	
	$.fn.quotationRecordFormContainer = function(option) {
		var pluginDataName = 'quotationRecordFormContainer';
		var args = Array.apply(null, arguments);
		args.shift();
		var internal_return;
		this.each(function() {
			var $this = $(this),
				data = $this.data(pluginDataName),
				options = typeof option === 'object' && option;

			if (!data) {
				var elopts = opts_from_el(this, pluginDataName),
					opts = $.extend({}, defaults, elopts, options);
				$this.data(pluginDataName, (data = new Plugin(this, opts)));
			}
			if (typeof option === 'string' && typeof data[option] === 'function') {
				internal_return = data[option].apply(data, args);
				if (internal_return !== undefined)
					return false;
			}
		});
		if (internal_return !== undefined)
			return internal_return;
		else
			return this;
	}
	$.fn.quotationRecordFormContainer.Constructor = Plugin;

	var defaults = $.fn.quotationRecordFormContainer.defaults = {
		currentQuotationRecordFormContainer: null,
		$quotationHistoryContainer: null
	};
}(window.jQuery));
</script>