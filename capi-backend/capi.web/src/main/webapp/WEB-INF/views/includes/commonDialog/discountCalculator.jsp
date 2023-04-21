<script>
(function($, undefined){
	var Plugin = function(element, options) {
		this._process_options(options);
		this.$element = $(element);
		
		this.$popupModal = null;
		this.discountFormulas = null;
		this.$nPrice = null;
		this.$sPrice = null;
		this.$discount = null;
		this.$discountRemark = null;
		this.disabled = false;
		this.disablePriceInput = false;
		this.disableReasonInput = false;

		this.$element.click($.proxy(this._showPopup, this));
		
		$.proxy(this._initInput, this)();
	};
	Plugin.prototype = {
		constructor : Plugin,
		_process_options : function(opts) {
			this._o = $.extend({}, this._o, opts);
			var o = this.o = $.extend({}, this._o);
		},
		plugin: function(){return this;},
		_showPopup: function() {
			var plugin = this;
			this.disabled = this.$element.prop('disabled') || this.$element.attr('disabled') == 'disabled';
			
			if (this.$popupModal == null) {
				$.get(this.o.popupUrl, function(html) {
					var $html = $(html);
					$(document.body).append($html);
					plugin.$popupModal = $html.modal('hide');

					$.proxy(plugin._initPopup, plugin)();
					$.proxy(plugin._disableFields, plugin)();
					
					plugin.$popupModal.modal('show');
				});
			} else {
				this._refresh();
				this.$discount.trigger("change");
				this._disableFields();
				this.$popupModal.modal('show');
			}
		},
		_disableFields: function() {
			if (this.disabled) {
				this.$nPrice.prop('disabled', true);
				this.$sPrice.prop('disabled', true);
				this.$discount.prop('disabled', true);
				
				
				
				$('.btn-formula-lookup', this.$popupModal).hide();
				if (this.o.isProvideRemarkForNotAvailableQuotation == false) {
					this.$discountRemark.prop('disabled', true);
					$('.modal-confirm', this.$popupModal).addClass('hide');
					$('.discount-remark-input', this.$popupModal).hide();
					$('.discountRemark', this.$popupModal).show();
				} else {
					this.$discountRemark.prop('disabled', false);
					$('.modal-confirm', this.$popupModal).removeClass('hide');
					$('.discount-remark-input', this.$popupModal).show();
					$('.discountRemark', this.$popupModal).hide();
				}
			} else {
				this.$nPrice.prop('disabled', false);
				this.$sPrice.prop('disabled', false);
				this.$discount.prop('disabled', false);
				this.$discountRemark.prop('disabled', false);
				$('.modal-confirm', this.$popupModal).removeClass('hide');
				
				$('.btn-formula-lookup', this.$popupModal).show();
				
				$('.discount-remark-input', this.$popupModal).show();
				$('.discountRemark', this.$popupModal).hide();
			}
			
			if (this.disablePriceInput) {
				this.$nPrice.prop('disabled', true);
				this.$sPrice.prop('disabled', true);
				this.$discount.prop('disabled', true);
				$('.btn-formula-lookup', this.$popupModal).hide();
			}
			
			if (this.disableReasonInput) {
				this.$discountRemark.prop('disabled', true);
				$('.modal-confirm', this.$popupModal).addClass('hide');
				$('.discount-remark-input', this.$popupModal).hide();
				$('.discountRemark', this.$popupModal).show();
			}
			
			
		},
		
		_refresh: function() {
			this._fillParam();
		},
		
		_fillParam: function() {
			var dataModel = {};
			this.o.paramCallback(dataModel);
			this.$nPrice.val(dataModel.nPrice);
			this.$sPrice.val(dataModel.sPrice);
			this.$discount.val(dataModel.discount);
			this.$discountRemark.val(dataModel.discountRemark);
			$('.discountRemark', this.$popupModal).text(dataModel.discountRemark);
		},
		
		_searchFormula: function(discount) {
			for (var i=0, len = this.discountFormulas.length; i < len; i++) {
				var reg = new RegExp('^' + this.discountFormulas[i].pattern + '$', "g");
				if (reg.test(discount))
					return this.discountFormulas[i];
			}
			return null;
		},
		
		_getDiscountValue: function(pattern, inStr, variables, formula, NP) {
			try {
				var reg = new RegExp(pattern, "g");
				var matches = reg.exec(inStr);
	
				for (i = 0; i < variables.length; i++) {
					eval(variables[i]+"="+matches[i+1]);
				}
				
				var result = eval(formula);
				if ((result + '').search(/^\-*\d+(\.\d+)*$/) != -1)
					return result;
				else
					return null;
			} catch(Exception){}
			return null;
		},
		
		_calculate: function(formula, discount, NP) {
			var variables = formula.variable.split(',');
			var sPrice = this._getDiscountValue(formula.pattern, discount, variables, formula.formula, NP);
			
			var sPriceStr = sPrice.toString();
			var noOfDecimal;
			if(sPriceStr.indexOf(".") >= 0) {
				noOfDecimal = sPriceStr.split(".")[1].length;
			} else {
				noOfDecimal = 0;
			}
			if(noOfDecimal == 4) {
				if((sPriceStr).split(".")[1] == "0000") {
					sPrice = Math.floor((sPrice));
				}
			} else if(noOfDecimal > 4) {
				//sPrice = sPrice.toFixed(2);
				sPrice = Math.round(sPrice * 10000) / 10000.0;
				if(sPrice.toString().split(".")[1] == "0000") {
					sPrice = Math.floor((sPrice));
				}
			}
			
			return sPrice;
		},
		
		_onDiscountChange: function() {
			if (!this.$popupModal.find('form').valid()) {
				return;
			}
			
			var nPrice = this.$nPrice.val();
			var discount = this.$discount.val();
			discount = discount.replace(/ /g, '');
			var formula = this._searchFormula(discount);
			$('.discount-found-msg', this.$popupModal).addClass('hide');
			$('.discount-not-found-msg', this.$popupModal).addClass('hide');
			if (formula == null) {
				$('.discount-not-found-msg', this.$popupModal).removeClass('hide');
				this.$sPrice.prop('readonly', false);
				return;
			} else {
				$('.discount-found-msg', this.$popupModal).removeClass('hide');
				this.$sPrice.prop('readonly', true);
			}
			var sPrice = this._calculate(formula, discount, nPrice);
			$('.discount-error-msg', this.$popupModal).addClass('hide');
			if (sPrice == null) {
				$('.discount-error-msg', this.$popupModal).removeClass('hide');
				return;
			}
			this.$sPrice.val(sPrice);
		},
		
		_initPopup: function() {
			var plugin = this;
			this.$nPrice = $('[name="nPrice"]', this.$popupModal);
			this.$sPrice = $('[name="sPrice"]', this.$popupModal);
			this.$discount = $('[name="discount"]', this.$popupModal);
			this.$discountRemark = $('[name="discountRemark"]', this.$popupModal);
			this.$popupModal.find('form').validate();
			
			if (this.o.hideRemark) $('.discount-remark-container', this.$popupModal).hide();
			
			this._fillParam();
			
			if (this.disabled) {
				this.$nPrice.prop('disabled', true);
				this.$sPrice.prop('disabled', true);
				this.$discount.prop('disabled', true);
				this.$discountRemark.prop('disabled', true);
				$('.modal-confirm', this.$popupModal).addClass('hide');
			}
			
			$.get(this.o.getAllEnabledFormulaUrl, function(data) {
				plugin.discountFormulas = data;
				
				plugin.$discount.change($.proxy(plugin._onDiscountChange, plugin));
				plugin.$discount.trigger('change');
				if (!plugin.disabled && !plugin.disablePriceInput)
					plugin.$discount.prop('disabled', false);
				
				$('.btn-input').click(function() {
					if (plugin.disabled || plugin.disablePriceInput) return;
					var value = $(this).val();
					plugin.$discount.val(plugin.$discount.val() + '' + value);
					plugin.$discount.trigger('change');
				});
				$('.btn-backspace', plugin.$popupModal).click(function() {
					if (plugin.disabled || plugin.disablePriceInput) return;
					if (plugin.$discount.val().length == 0) return;
					plugin.$discount.val(plugin.$discount.val().substring(0, plugin.$discount.val().length - 1));
					plugin.$discount.trigger('change');
				});
				
				$('.modal-confirm', plugin.$popupModal).click($.proxy(plugin._callback, plugin));
				
				$('[name="nPrice"]', plugin.$popupModal).on('change', function() {
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
				
				$('[name="nPrice"]', plugin.$popupModal).change($.proxy(plugin._onDiscountChange, plugin));
			});
			
			$('.btn-reason-lookup', this.$popupModal).priceReasonLookup({
				queryDataCallback: function(dataModel) {
					dataModel.reasonType = 'Discount';
					$.proxy(plugin.o.reasonLookupQueryDataCallback, plugin, dataModel)();
				}
			});
			
			$('.btn-formula-lookup', this.$popupModal).discountFormulaLookup();
		},
		
		_callback: function() {
			if (!this.$popupModal.find('form').valid()) {
				return;
			}
			
			var sPrice = this.$sPrice.val();
			var nPrice = this.$nPrice.val();
			var discount = this.$discount.val();
			var discountRemark = this.$discountRemark.val();
			var dataModel = {
				sPrice: sPrice,
				nPrice: nPrice,
				discount: discount,
				discountRemark: discountRemark
			};
			$.proxy(this.o.resultCallback, this,dataModel)();
		    this.$popupModal.modal('hide');
		},
		
		_initInput: function() {
			var plugin = this;
			
			if ($('.discount-found-msg', plugin.$element).length > 0) {
				var discount = $('input', plugin.$element).val();
				if (discount != '') {
					plugin.adHocSearchFormula(discount, function(formula) {
						if (formula != null) {
							$('.discount-found-msg', plugin.$element).removeClass('hide');
						} else {
							$('.discount-not-found-msg', plugin.$element).removeClass('hide');
						}
					});
				}
			}
		},
		
		adHocCalculate: function(nPrice, discount, callback) {
			var plugin = this;
			
			function calculate(nPrice, discount) {
				var formula = plugin._searchFormula(discount);
				if (formula == null) return null;
				var sPrice = plugin._calculate(formula, discount, nPrice);
				return sPrice;
			}
			
			if (plugin.discountFormulas == null) {
				$.get(this.o.getAllEnabledFormulaUrl, function(data) {
					plugin.discountFormulas = data;
					var sPrice = calculate(nPrice, discount);
					$.proxy(callback, plugin, sPrice)();
				});
			} else {
				var sPrice = calculate(nPrice, discount);
				$.proxy(callback, plugin, sPrice)();
			}
		},
		
		adHocSearchFormula: function(discount, callback) {
			var plugin = this;
			
			if (plugin.discountFormulas == null) {
				$.get(this.o.getAllEnabledFormulaUrl, function(data) {
					plugin.discountFormulas = data;
					var formula = $.proxy(plugin._searchFormula, plugin, discount)();
					$.proxy(callback, plugin, formula)();
				});
			} else {
				var formula = $.proxy(plugin._searchFormula, plugin, discount)();
				$.proxy(callback, plugin, formula)();
			}
		},
		
		setDisablePriceInput: function(value) {
			this.disablePriceInput = value;
		},
		
		setDisableReasonInput: function(value) {
			this.disableReasonInput = value;
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
	
	$.fn.discountCalculator = function(option) {
		var pluginDataName = 'discountCalculator';
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
	$.fn.discountCalculator.Constructor = Plugin;

	var defaults = $.fn.discountCalculator.defaults = {
		popupUrl: "<c:url value='/commonDialog/DiscountCalculator/home'/>",
		getAllEnabledFormulaUrl: "<c:url value='/commonDialog/DiscountCalculator/getAllEnabledFormula'/>",
		paramCallback: function(dataModel) {
			dataModel.nPrice = 0;
			dataModel.sPrice = 0;
			dataModel.discount = '';
			dataModel.discountRemark = '';
		},
		resultCallback: function(dataModel) {
			dataModel.nPrice;
			dataModel.sPrice;
			dataModel.discount;
			dataModel.discountRemark;
		},
		hideRemark: false,
		reasonLookupQueryDataCallback: function(dataModel){},
		isProvideRemarkForNotAvailableQuotation:false
	};
}(window.jQuery));
</script>