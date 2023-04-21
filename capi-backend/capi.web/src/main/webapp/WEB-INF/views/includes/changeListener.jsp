<script>
(function($, undefined){
	var Plugin = function(element, options) {
		this._process_options(options);
		this.$element = $(element);
		
		this.changed = false;
		
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
			plugin.$element.change(function() {
				$.proxy(plugin.setChanged, plugin)();
			});
			
			for (var i = 0, len = plugin.o.backButtons.length; i < len; i++) {
				var $btn = $(plugin.o.backButtons[i]);
				
				$btn.click(function(e) {
					if (!plugin.changed) return;
					e.preventDefault();
					var href = $(this).attr('href');
					bootbox.confirm({
						title:"Confirmation",
						message: plugin.o.confirmMessage,
						callback: function(result){
							if (result){
								location = href;
							}
						}
					});
				});
			}
		},
		
		setChanged: function() {
			this.changed = true;
			$.proxy(this.o.changeCallback, this)();
		},
		
		isChanged : function() {
			return this.changed;
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
	
	$.fn.changeListener = function(option) {
		var pluginDataName = 'changeListener';
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
	$.fn.changeListener.Constructor = Plugin;

	var defaults = $.fn.changeListener.defaults = {
		backButtons: [],
		confirmMessage: '<spring:message code='W00022' />',
		changeCallback: function(){}
	};
}(window.jQuery));
</script>