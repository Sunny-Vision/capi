<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script>
(function($, undefined){
	var Plugin = function(element, options) {
		this._process_options(options);
		this.$element = $(element);
		
		this.$popupModal = null;
		this.$popupTable = null;

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
					plugin.$popupTable = $(".datatable", plugin.$popupModal);

					$.proxy(plugin._initPopup, plugin)();

					plugin.$popupModal.modal('show');
				});
			} else {
				this.$popupModal.modal('show');
			}
		},
		
		_initPopup: function() {
			var plugin = this;

			var buttons = [];
			
			var columnDefs = [
	                           {
	                               "targets": "action",
	                               "orderable": false,
	                               "searchable": false
	                           },
	                           {
	                        	   "targets": "_all",
	                        	   "className" : "text-center"
	                           }
								];
			
			this.$popupTable.DataTable({
				"ordering": true,
				"searching": true,
				"buttons": buttons,
				"processing": true,
	            "serverSide": true,
	            "ajax": {
	            	url: plugin.o.queryUrl,
	            	data: function(dataModel) {
	            		$.proxy(plugin.o.queryDataCallback, plugin, dataModel)();
	            	},
	            	method: "post"
	            },
	            "columns": [
	                        { "data": "id", "visible": true },
	                        {
	                        	"data": "displayPattern",
                            	"render": function(data) {
                            		var patterns = data.split("|");
                            		var html = '';
                            		for (var i = 0; i < patterns.length; i++) {
                            			var pattern = patterns[i];
                            			if (pattern == '') continue;
                            			
                            			if (pattern.indexOf('數字') >= 0) {
                            				html += '<span class="number">' + pattern + '</span>';
                            			} else {
                            				html += '<span>' + pattern + '</span>';
                            			}
                            		}
                            		return html;
                            	},
                            	"className" : "discount text-left"
	                        },
	                        { "data": "formula", "visible": false },
	                        { "data": "status", "visible": false  }
							],
	            "columnDefs": columnDefs,
	            "drawCallback": function() {}
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
	
	$.fn.discountFormulaLookup = function(option) {
		var pluginDataName = 'discountFormulaLookup';
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
	$.fn.discountFormulaLookup.Constructor = Plugin;

	var defaults = $.fn.discountFormulaLookup.defaults = {
		popupUrl: "<c:url value='/commonDialog/DiscountFormulaLookup/home'/>",
		queryUrl: "<c:url value='/commonDialog/DiscountFormulaLookup/query'/>",
		queryDataCallback: function(dataModel) {}
	};
}(window.jQuery));
</script>