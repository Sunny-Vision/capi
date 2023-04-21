<script>
(function($, undefined){
	var Plugin = function(element, options) {
		this._process_options(options);
		this.$element = $(element);
		
		this.$popupModal = null;
		this.$textarea = null;

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

			var $input = this.$element.closest('.input-group').find('input:first');
			if ($input.prop('disabled') || $input.prop('readonly')) {
				return;
			}
			
			if (this.$popupModal == null) {
				var dataModel = {};
				$.proxy(plugin.o.queryDataCallback, plugin, dataModel)();
				
				$.post(this.o.popupUrl,
					dataModel,
					function(html) {
						var $html = $(html);
						$(document.body).append($html);
						plugin.$popupModal = $html.modal('hide');
						plugin.$textarea = $(".reason-input", plugin.$popupModal);
	
						$.proxy(plugin._initPopup, plugin)();
						
						$.proxy(plugin._refresh, plugin)();
						plugin.$popupModal.modal('show');
				});
			} else {
				$.proxy(plugin._refresh, plugin)();
				this.$popupModal.modal('show');
			}
		},
		
		_refresh: function() {
			var text = $.proxy(this.o.getOriginalTextCallback, this);
			this.$textarea.val(text);
		},
		
		_initPopup: function() {
			var plugin = this;

			$(this.$popupModal).on('click', '.reason-container button', function() {
				var data = $(this).text();
				var newText = plugin.$textarea.val();
				if (newText.trim().length > 0) {
					newText += ', ';
				}
				plugin.$textarea.val(newText + data);
			});
			
			$('.modal-confirm', this.$popupModal).click(function() {
				var data = plugin.$textarea.val();
				$.proxy(plugin._callback, plugin, data)();
			});
		},

		_callback: function(data) {
			$.proxy(this.o.resultCallback, this, data)();
			this.$popupModal.modal('hide');
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
	
	$.fn.priceReasonLookup = function(option) {
		var pluginDataName = 'priceReasonLookup';
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
	$.fn.priceReasonLookup.Constructor = Plugin;

	var defaults = $.fn.priceReasonLookup.defaults = {
		popupUrl: "<c:url value='/commonDialog/PriceReasonLookup/home'/>",
		queryDataCallback: function(dataModel) {
			dataModel.reasonType = 0;
			dataModel.outletTypeId = [];
		},
		resultCallback: function(data) {
			var $input = this.$element.closest('.input-group').find('input:first');
			$input.val(data);
			$input.trigger('change');
		},
		getOriginalTextCallback: function() {
			var $input = this.$element.closest('.input-group').find('input:first');
			return $input.val();
		}
	};
}(window.jQuery));
</script>