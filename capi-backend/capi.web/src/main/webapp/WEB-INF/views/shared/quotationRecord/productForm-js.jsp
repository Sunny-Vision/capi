<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
/**
 * Display product form
 * 
 * Controller permission required:
 * /shared/General/getProductHasPhoto
 * /shared/General/getProductImage
 */
%>
<script>
(function($, undefined){
	var Plugin = function(element, options) {
		this._process_options(options);
		this.$element = $(element);
		
		this.$productPostContainer = this.$element.find('.hidden-product-post-container');

		var plugin = this;
		
		$('.btn-change-product', this.$element).changeProductDialog({
			$productPostContainer: this.$productPostContainer,
			paramCallback: function(dataModel) {
				dataModel.productId = +$('[name$=".productId"]', plugin.$element).val();
				dataModel.productGroupId = +$('[name$=".productGroupId"]', plugin.$element).val();
				dataModel.quotationRecordId = plugin.o.quotationRecordId;
			},
			resultCallback: function(dataModel, changeModel, newProductId) {
				if (newProductId != null) {
					$('[name$=".productId"]', plugin.$element).val(newProductId);
				}
				plugin.$productPostContainer.empty();
				for (var key in dataModel) {
					var $hidden = $('<input name="' + key + '" type="hidden"/>');
					plugin.$productPostContainer.append($hidden);
					$hidden.val(dataModel[key]);
				}
				
				var index = 0;
				for (var key in dataModel) {
					if (key == 'product.countryOfOrigin' || key == 'product.barcode' ||  key.indexOf('.value') == -1) continue;
					$($('.product-form-attribute', plugin.$element)[index]).text(dataModel[key]);
					index++;
				}
				
				$('.countryOfOrigin', plugin.$element).text(dataModel['product.countryOfOrigin']);
				$('.barcode', plugin.$element).text(dataModel['product.barcode']);
				
				if (changeModel.productChange) {
					if (changeModel.newProduct) {
						if ($('[name$="productId"]', plugin.$element).val() == null) {
							$('.product-form-photo1', plugin.$element).attr('src', plugin.o.dummyPhotoUrl);
							$('.product-form-photo2', plugin.$element).attr('src', plugin.o.dummyPhotoUrl);
						} else {
							$.get(plugin.o.getProductHasPhotoUrl, { id : $('[name$="productId"]', plugin.$element).val() }, function(data) {
								var url = plugin.o.getProductImageUrl + '?productId=' + $('[name$="productId"]', plugin.$element).val() + '&photoIndex=';
								if (data.photo1) {
									$('.product-form-photo1', plugin.$element).attr('src', url + 1 + '&bust=' + (new Date()).getTime());
								} else {
									$('.product-form-photo1', plugin.$element).attr('src', plugin.o.dummyPhotoUrl);
								}
								if (data.photo2) {
									$('.product-form-photo2', plugin.$element).attr('src', url + 2 + '&bust=' + (new Date()).getTime());
								} else {
									$('.product-form-photo2', plugin.$element).attr('src', plugin.o.dummyPhotoUrl);
								}
							});
						}
					} else {
						$.get(plugin.o.getProductHasPhotoUrl, { id : changeModel.sameAttributeProductId }, function(data) {
							var url = plugin.o.getProductImageUrl + '?productId=' + changeModel.sameAttributeProductId + '&photoIndex=';
							if (data.photo1) {
								$('.product-form-photo1', plugin.$element).attr('src', url + 1 + '&bust=' + (new Date()).getTime());
							} else {
								$('.product-form-photo1', plugin.$element).attr('src', plugin.o.dummyPhotoUrl);
							}
							if (data.photo2) {
								$('.product-form-photo2', plugin.$element).attr('src', url + 2 + '&bust=' + (new Date()).getTime());
							} else {
								$('.product-form-photo2', plugin.$element).attr('src', plugin.o.dummyPhotoUrl);
							}
						});
					}
					plugin.$element.closest('.box').find('.box-title:first').addClass('required');
				} else {
					$.get(plugin.o.getProductHasPhotoUrl, { id : $('[name$="productId"]', plugin.$element).val() }, function(data) {
						var url = plugin.o.getProductImageUrl + '?productId=' + $('[name$="productId"]', plugin.$element).val() + '&photoIndex=';
						if (data.photo1) {
							$('.product-form-photo1', plugin.$element).attr('src', url + 1 + '&bust=' + (new Date()).getTime());
						} else {
							$('.product-form-photo1', plugin.$element).attr('src', plugin.o.dummyPhotoUrl);
						}
						if (data.photo2) {
							$('.product-form-photo2', plugin.$element).attr('src', url + 2 + '&bust=' + (new Date()).getTime());
						} else {
							$('.product-form-photo2', plugin.$element).attr('src', plugin.o.dummyPhotoUrl);
						}
					});
					plugin.$element.closest('.box').find('.box-title:first').removeClass('required');
				}
				
				if (changeModel.newProduct) {
					$('.product-photo-upload-container', plugin.$element).removeClass('hide');
				} else {
					$('.product-photo-upload-container', plugin.$element).addClass('hide');
				}
				
				$.proxy(plugin.o.productChangeCallback, plugin, changeModel)();
			}
		});
		
		$('.btn-compare-product', this.$element).compareProductDialog({
			paramCallback: function(dataModel) {
				dataModel.quotationRecordId = plugin.o.quotationRecordId;
				dataModel.historyQuotationRecordId = $.proxy(plugin.o.historyQuotationRecordIdCallback, plugin)();
			}
		});
		
		plugin.o.$quotationCurrentContainer.on('disableAllTrue', function() {
			$('.btn-change-product,input[type="file"]', plugin.$element).prop('disabled', true);
			$('[name="quotationRecord.productRemark"],[name="quotationRecord.productPosition"]', plugin.$element).prop('readonly', true);
		});

		plugin.o.$quotationCurrentContainer.on('disableAllFalse', function() {
			$('.btn-change-product,input[type="file"]', plugin.$element).prop('disabled', false);
			$('[name="quotationRecord.productRemark"],[name="quotationRecord.productPosition"]', plugin.$element).prop('readonly', false);
		});
		
		if ($.fn.productSpecDialog != null) {
			$('.btn-product-spec', this.$element).productSpecDialog();
		}
		
		$(document.body).ready(function() {
			var isProductChange = $('.quotation-record-form-container,.readonly-quotation-record-form-container', plugin.$quotationCurrentContainer).quotationRecordFormContainer('isProductChange');
			if (isProductChange) {
				plugin.$element.closest('.box').find('.box-title:first').addClass('required');
			}
		});
	};
	Plugin.prototype = {
		constructor : Plugin,
		_process_options : function(opts) {
			this._o = $.extend({}, this._o, opts);
			var o = this.o = $.extend({}, this._o);
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
	
	$.fn.productForm = function(option) {
		var pluginDataName = 'productForm';
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
	$.fn.productForm.Constructor = Plugin;

	var defaults = $.fn.productForm.defaults = {
		getProductHasPhotoUrl: "<c:url value='/shared/General/getProductHasPhoto'/>",
		getProductImageUrl: "<c:url value='/shared/General/getProductImage'/>",
		dummyPhotoUrl: "<c:url value='/resources/images/dummyphoto.png'/>",
		quotationRecordId: null,
		historyQuotationRecordIdCallback: function() {
			return 0;
		},
		productChangeCallback: function(changeModel) {},
		$quotationCurrentContainer: null
	};
}(window.jQuery));
</script>