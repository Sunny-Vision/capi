<script>
(function($, undefined){
	var Plugin = function(element, options) {
		this._process_options(options);
		this.$element = $(element);
		
		this.$popupModal;
		
		this.productId = null;
		this.productGroupId = null;
		this.newProductId = null;

		this.$element.click($.proxy(this._showPopup, this));
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
			if (this.$popupModal == null) {
				var dataModel = {};
				this.o.paramCallback(dataModel);
				this.productId = dataModel.productId;
				this.productGroupId = dataModel.productGroupId;
				this.quotationRecordId = dataModel.quotationRecordId;
				
				$.get(this.o.popupUrl, function(html) {
					var $html = $(html);
					$(document.body).append($html);
					var $form = $('form', $html);
					$form.validate();
					plugin.$popupModal = $html.modal('hide');

					var $container = $('.old-product-container', plugin.$popupModal);
					$.proxy(plugin._initPopup, plugin)();
					$.proxy(plugin._initBody, plugin, $container, plugin.productId, plugin.productGroupId,
						function() {
							$('[name$=".value"]', plugin.o.$productPostContainer).each(function(index) {
								var value = $(this).val();
								if ($('[name="product.attributes[' + index + '].value"] option:contains("' + value + '")', $container).length > 0) {
									$('[name="product.attributes[' + index + '].value"]', $container).val(value);
								} else {
									$('[name="product.attributes[' + index + '].value"]', $container).append($('<option selected="selected">' + $(this).val() + '</option>'));
								}
								$('[name="product.attributes[' + index + '].value"]', $container).trigger('change');
							});
							
							$('[name$=".countryOfOrigin"]', $container).select2('val', $('[name$=".countryOfOrigin"]', plugin.o.$productPostContainer).val());
							$('[name$=".barcode"]', $container).val($('[name$=".barcode"]', plugin.o.$productPostContainer).val());
						}
					)();

					plugin.$popupModal.modal('show');
				});
			} else {
				var $container = $('.old-product-container', plugin.$popupModal);
				$('[name$=".value"]', plugin.o.$productPostContainer).each(function(index) {
					var value = $(this).val();
					if ($('[name="product.attributes[' + index + '].value"]', $container).is('select')) {
						$('[name="product.attributes[' + index + '].value"]', $container).select2('val', value);
					} else {
						$('[name="product.attributes[' + index + '].value"]', $container).val(value);
					}
				});
				
				$('[name$=".countryOfOrigin"]', $container).select2('val', $('[name$=".countryOfOrigin"]', plugin.o.$productPostContainer).val());
				$('[name$=".barcode"]', $container).val($('[name$=".barcode"]', plugin.o.$productPostContainer).val());
				
				this.$popupModal.modal('show');
			}
		},
		
		_initPopup: function() {
			var plugin = this;
			$('.modal-submit', this.$popupModal).click($.proxy(plugin._callback, plugin));
			$('.modal-cancel', this.$popupModal).click(function() {
				plugin.newProductId = null;
				$('.new-product-container', plugin.$popupModal).empty().hide();
				$('.old-product-container', plugin.$popupModal).show();
			});
			this.$popupModal.submit(function(){
				return false;
			});
		},
		
		_initBody: function($container, productId, productGroupId, callback) {
			var plugin = this;
			$container.empty();
			
			$.get(this.o.bodyUrl + '?productId=' + productId + '&productGroupId=' + productGroupId, function(html) {
				var $bodyHtml = $(html);
				$container.append($bodyHtml);
				
				$('[name$=".value"]', $bodyHtml).each(function() {
					var $inputValue = $(this);
					var attributeType = $inputValue.data('attributeType');
					var productAttributeId = $inputValue.data('productAttributeId');
					var name = $inputValue.data('name');
					
					switch (attributeType) {
						case 1:
							$inputValue.select2ajax({
								placeholder : "Input a " + name,
								width : "100%",
								tags : true,
								allowClear : true,
								//minimumInputLength : 2,
								ajax : {
									quietMillis : 150,
									url : "<c:url value='/commonDialog/ChangeProductDialog/queryProdAttrValueSelect2'/>",
									dataType : "json",
									/* data : function(
											term,
											page) {
										console.log(page);
										return {
											productGroupId : productGroupId,
											productAttributeId : productAttributeId,
											term : term["term"],
											page : page["page"]
										};
									}, */
									data: function (term) {
						            	return $.extend(term, 
						            					{
						            						productGroupId: productGroupId,
						            						productAttributeId: productAttributeId
						            					});
						            }
								},
								createSearchChoice : function(term, data) {
									if ($(data).filter(
										function() {
											return this.text.localeCompare(term) === 0;
										}).length === 0) {
										return {
											id : term,
											text : term
										};
									}
								}
							});
							break;
						case 2:
							$inputValue.select2({
								placeholder : "Select a " + name,
								allowClear : true,
								width : "100%",
							});
							break;
						case 3:
							$inputValue.select2({
								placeholder : "Input a " + name,
								width : "100%",
								tags : true,
								allowClear : true,
								createSearchChoice : function(term, data) {
									if ($(data).filter(
										function() {
											return this.text.localeCompare(term) === 0;
										}).length === 0) {
										return {
											id : term,
											text : term
										};
									}
								}
							});
							break;
					}
				});
				
				$('.btn-match-product', $bodyHtml).productLookup({
					productGroupId: plugin.productGroupId,
					multiple: false,
					queryDataCallback: function(dataModel) {
						dataModel.barcode = $('[name$=".barcode"]', $bodyHtml).val();
						if (plugin.newProductId != null)
							dataModel.skipProductId = plugin.newProductId;
						else
							dataModel.skipProductId = plugin.productId;
					},
					selectedIdsCallback: function(ids) {
						$('.old-product-container', plugin.$popupModal).hide();
						
						plugin.newProductId = ids[0];
						
						$.proxy(plugin._initBody, plugin, $('.new-product-container', plugin.$popupModal), plugin.newProductId, plugin.productGroupId)();
					}
				});
				
				var selectedCountry = $bodyHtml.find('select.select2').parents().find('input[name="selectedCountry"]').val();
				
				var checking = true;
				$bodyHtml.find('select.select2').find('option').each(function() {
					var value = $(this).val();
					if(value == selectedCountry) {
						checking = false;
					}
				});
				
				var selectCountryOfOrigin = $('select[name="product.countryOfOrigin"]', $bodyHtml).select2({
					placeholder: "Select a Country of Origin" ,
					allowClear: true,
					width: "100%",
					tags: true
				});
				if(checking) {
					var option = $('<option></option>').attr('selected', true).text(selectedCountry);
					option.appendTo(selectCountryOfOrigin);
					selectCountryOfOrigin.trigger('change');
				}
				
				$container.show();
				
				if (callback != undefined) callback();
			});
		},

		_callback: function() {
			var plugin = this;
			
			if (this.newProductId != null) {
				$('.old-product-container', this.$popupModal).remove();
				$('.new-product-container', this.$popupModal).removeClass('new-product-container').addClass('old-product-container');
				$('<div class="new-product-container"></div>').appendTo('.modal-body', this.$popupModal);
				
				this.productId = this.newProductId;
				this.newProductId = null;
			}

			var $form = $('.old-product-container form', this.$popupModal);
			
			if(!$form.valid()){
				return false;
			};
			
			var dataModel = {};
			var queryChangeModel = { quotationRecordId: this.quotationRecordId };
			$('input,select', $form).each(function() {
				dataModel[$(this).attr('name')] = $(this).val();
				queryChangeModel[$(this).attr('name').replace('product.', '')] = $(this).val();
			});
			
			$.ajax({
				url: this.o.checkProductChangeUrl,
				data: queryChangeModel,
				method: 'post',
				traditional: true,
				success: function(data) {
					if (data.productChange) {
						plugin.o.resultCallback(dataModel, data, plugin.productId);
						plugin.$popupModal.modal('hide');
					} else {
						bootbox.alert({
							title: "Alert",
							message:"<spring:message code='E00169' />"
						});
					}
				}
			});
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
	
	$.fn.changeProductDialog = function(option) {
		var pluginDataName = 'changeProductDialog';
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
	$.fn.changeProductDialog.Constructor = Plugin;

	var defaults = $.fn.changeProductDialog.defaults = {
		popupUrl: "<c:url value='/commonDialog/ChangeProductDialog/home'/>",
		bodyUrl: "<c:url value='/commonDialog/ChangeProductDialog/body'/>",
		checkProductChangeUrl: "<c:url value='/commonDialog/ChangeProductDialog/checkProductChange'/>",
		$productPostContainer: null,
		paramCallback: function(dataModel) {
			dataModel.productId = 0;
			dataModel.productGroupId = 0;
			dataModel.quotationRecordId = 0;
		},
		resultCallback: function(dataModel, changeModel, newProductId) {}
	};
}(window.jQuery));
</script>