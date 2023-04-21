<script>
(function($, undefined){
	var Plugin = function(element, options) {
		this._process_options(options);
		this.$element = $(element);
		
		this._init();
		
	};
	Plugin.prototype = {
		constructor : Plugin,
		_process_options : function(opts) {
			this._o = $.extend({}, this._o, opts);
			var o = this.o = $.extend({}, this._o);
		},
		plugin: function(){return this;},
		
		_init: function() {
			var plugin = this;
			var outletId = $('[name="outletId"]', this.$element).val();
			var redirectUrl = plugin.o.redirectUrl == null ? (window.location.pathname + window.location.search) : plugin.o.redirectUrl;
			$('.btn-attachment-lookup', this.$element).outletAttachmentDialog(
					{
						outletId:outletId,
						redirectUrl: redirectUrl,
						submitUrl: plugin.o.attachmentSubmitUrl,
						readonly: plugin.o.attachmentSubmitUrl != null ? plugin.o.readonly : true
					});
			
			if ($.fn.outletBatchCodeDialog != null) {
				$('.btn-batch-code', this.$element).removeClass('hide').outletBatchCodeDialog({outletId:outletId});
			}
			
			$('.btn-outlet-collapse', this.$element).click(function() {
				var $btn = $(this);
				var $box = $btn.closest('.box');
				if ($btn.find('.fa-plus').length > 0)
					$box.find('.outlet-header-name').hide();
				else
					$box.find('.outlet-header-name').show();
			});
			
			$('.select2ajax', this.$element).select2ajax();
			
			$('[name="firmStatus"]', this.$element).change($.proxy(plugin._updateFirmStatusDisplay, plugin));
			this._updateFirmStatusDisplay();
			
			if (plugin.o.readonly) {
				$('.btn-save', this.$element).hide();
				$('input,select', this.$element).prop('disabled', true);
			}
			
			if (plugin.o.isNewRecruitment) {
				$('[name="firmStatus"] option', this.$element).not('[value="1"]').remove();
				$('.btn-map-outlet', this.$element).removeClass('hide');
				$.proxy(plugin._initMapOutletButton, plugin)();
			}
			
			if (plugin.o.isNewOutlet) {
				$('.new-outlet').removeClass('hide');
			}
			
			if (plugin.o.hideImageUpload) {
				$('input[type="file"]').hide();
			}
		},
		
		_initMapOutletButton: function() {
			var plugin = this;
			$('.btn-map-outlet', this.$element).outletLookup({
				multiple: false,
				queryDataCallback: function(data) {
					data.name = $('[name="name"]', plugin.$element).val();
					data.tel = $('[name="tel"]', plugin.$element).val();
				},
				selectedIdsCallback: function(ids) {
					var id = ids[0];
					$.post(plugin.o.prepareMapOutletUrl, {id:id}, function(data) {
						$('[name="outletId"]', plugin.$element).val(data.outletId);
						$('.outletId', plugin.$element).text(data.outletId);
						$('[name="mainContact"]', plugin.$element).val(data.mainContact);
						
						$('.firmCode', plugin.$element).text(data.firmCode == null ? '' : data.firmCode);
						$('[name="tel"]', plugin.$element).val(data.tel);
						
						$('[name="name"]', plugin.$element).val(data.name);
						$('.name', plugin.$element).text(data.name);
						$('[name="fax"]', plugin.$element).val(data.fax);
						
						$('.outletTypeFormatted', plugin.$element).text(data.outletTypeFormatted == null ? '' : data.outletTypeFormatted);
						$('[name="openingStartTime"]', plugin.$element).val(data.openingStartTime);
						$('[name="openingEndTime"]', plugin.$element).val(data.openingEndTime);
						
						$('.districtFormatted', plugin.$element).text(data.districtFormatted == null ? '' : data.districtFormatted);
						$('[name="openingStartTime2"]', plugin.$element).val(data.openingStartTime2);
						$('[name="openingEndTime2"]', plugin.$element).val(data.openingEndTime2);
						
						$('[name="tpuId"] option', plugin.$element).remove();
						if (data.tpuId > 0) {
							$('[name="tpuId"]', plugin.$element).append('<option value="' + data.tpuId + '" selected>' + data.tpuName + '</option>');
							$('[name="tpuId"] option', plugin.$element).trigger('change');
						}
						$('[name="convenientStartTime"]', plugin.$element).val(data.convenientStartTime);
						$('[name="convenientEndTime"]', plugin.$element).val(data.convenientEndTime);
						
						$('[name="detailAddress"]', plugin.$element).val(data.detailAddress);
						$('[name="webSite"]', plugin.$element).val(data.webSite);
						$('[name="brCode"]', plugin.$element).val(data.brCode);
						
						if (data.outletMarketType == 1) {
							$('[name="outletMarketType"][value="1"]', plugin.$element).prop('checked', true);
						} else {
							$('[name="outletMarketType"][value="2"]', plugin.$element).prop('checked', true);
						}
						
						$('[name="remark"]', plugin.$element).val(data.remark);
						$('[name="discountRemark"]', plugin.$element).val(data.discountRemark);
						
						if (data.collectionMethod > 0)
							$('[name="collectionMethod"]', plugin.$element).val(data.collectionMethod);
						else
							$('[name="collectionMethod"]', plugin.$element).val(1);
						
						if (data.outletImagePath != null) {
							$('.outlet-image', plugin.$element).attr('src', '<c:url value='/shared/General/getOutletImage'/>?id=' + data.outletId + '&bust=' + +(new Date()));
						} else {
							$('.outlet-image', plugin.$element).attr('src', '<c:url value='/resources/images/dummyphoto.png'/>');
						}
					});
				}
			});
		},
		
		_updateFirmStatusDisplay: function() {
			var firmStatusName = $('[name="firmStatus"] option:selected', this.$element).text();
			$('.outlet-header-name .firm-status', this.$element).text(firmStatusName);
		},
		
		disableCollectionMethod: function(disabled) {
			if (this.o.readonly) return;
			$('[name="collectionMethod"]', this.$element).prop('disabled', disabled);
		},
		
		disableFirmStatus: function(disabled) {
			if (this.o.readonly) return;
			$('[name="firmStatus"]', this.$element).prop('disabled', disabled);
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
	
	$.fn.outletEdit = function(option) {
		var pluginDataName = 'outletEdit';
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
	$.fn.outletEdit.Constructor = Plugin;

	var defaults = $.fn.outletEdit.defaults = {
		redirectUrl: null,
		readonly: false,
		attachmentSubmitUrl: null,
		prepareMapOutletUrl: null,
		isNewRecruitment: false,
		isNewOutlet: false,
		hideImageUpload: false
	};
	
}(window.jQuery));
		
		(function ($) {
			 $(document).ready(function(){
			   $('#deleteOutletImage').click(function(){
			     var $form = $(this).closest('form');
			     updateOutletImg($form);
			   });
			    $('[name="outletImage"]').on('change', function(){
			      var $form = $(this).closest('form');
			     updateOutletImg($form);
			     });
			   function updateOutletImg($form){
			     $(".outlet-image", $form).attr('src', '<c:url value='/resources/images/dummyphoto.png'/>');
			     $("#deleteOutletImage", $form).hide();
			     $("[name='idenfityDel']", $form).val("del");
			   }
			 });
			}(jQuery));
</script>