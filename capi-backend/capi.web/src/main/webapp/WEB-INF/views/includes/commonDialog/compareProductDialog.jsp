<script>
(function($, undefined){
	var Plugin = function(element, options) {
		this._process_options(options);
		this.$element = $(element);

		this.$popupModal = null;
		this.quotationRecordId = null;
		this.historyQuotationRecordId = null;
		
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
				$.get(this.o.popupUrl, function(html) {
					var $html = $(html);
					$(document.body).append($html);
					plugin.$popupModal = $html.modal('hide');

					$.proxy(plugin._initPopup, plugin)();
					$.proxy(plugin._initBody, plugin)();
					plugin.$popupModal.modal('show');
				});
			} else {
				$.proxy(plugin._initBody, plugin)();
				plugin.$popupModal.modal('show');
			}
		},
		_initPopup: function() {
			
		},
		_initBody: function() {
			var plugin = this;
			$('.modal-body', this.$popupModal).empty();
			
			var dataModel = {};
			$.proxy(this.o.paramCallback, this, dataModel)();
			this.quotationRecordId = dataModel.quotationRecordId;
			this.historyQuotationRecordId = dataModel.historyQuotationRecordId;
			
			$.get(this.o.bodyUrl + '?quotationRecordId=' + this.quotationRecordId + '&historyQuotationRecordId=' + this.historyQuotationRecordId, function(html) {
				var $html = $(html);
				$('.modal-body', plugin.$popupModal).append($html);
				$('.radio-photo').radioPhotos();
				$('img.viewer', $html).imageViewer();
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
	
	$.fn.compareProductDialog = function(option) {
		var pluginDataName = 'compareProductDialog';
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
	$.fn.compareProductDialog.Constructor = Plugin;

	var defaults = $.fn.compareProductDialog.defaults = {
		popupUrl: "<c:url value='/commonDialog/CompareProductDialog/home'/>",
		bodyUrl: "<c:url value='/commonDialog/CompareProductDialog/body'/>",
		paramCallback: function(dataModel) {
			dataModel.quotationRecordId = 0;
			dataModel.historyQuotationRecordId = 0;
		}
	};
}(window.jQuery));
</script>