<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<script>
(function($, undefined){
	var Plugin = function(element, options) {
		this._process_options(options);
		this.$element = $(element);
		
		this.$popupModal = null;
		this.$popupForm = null;
		this.$container = null;
		
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
				var loading = '<img class="loading" src="<c:url value='/resources/images/ui-anim_basic_16x16.gif' />" style="border:0px; position: absolute" />';
				plugin.$element.before(loading);
				plugin.$element.attr('disabled', 'disabled');
				
				$.get(plugin.o.popupUrl, function(html) {
					var $html = $(html);
					$(document.body).append($html);
					plugin.$popupModal = $html.modal('hide');

					$.proxy(plugin._initPopup, plugin)();
					$.proxy(plugin._loadContent, plugin)();
					plugin.$popupModal.modal('show');
					
					plugin.$element.parent().find('.loading').remove();
					plugin.$element.removeAttr('disabled');
				});
			} else {
				plugin._loadContent();
				plugin.$popupModal.modal('show');
			}
		},
		
		_initPopup: function() {
			var plugin = this;
			
			plugin.$form = $("form", plugin.$popupModal);
			plugin.$container = $('.content-container', plugin.$popupModal);
			
			if (plugin.o.allowSorting) {
				Sortable.create(this.$container[0],
					{
						handle: '.drag-handle',
					    onSort: function(evt) {
					    	$.proxy(plugin._refreshFieldNameIndex, plugin, $(evt.target))();
					    }
					}
				);
			}
		},
		_loadContent: function() {
			var plugin = this;
			
			var contentUrl = plugin.o.getContentUrl();
			var queryParam = plugin.o.getContentQueryParam();
			
			$.post(contentUrl,
				queryParam,
				function(html) {
					plugin.$container.html(html);
					$.proxy(plugin._initContent, plugin)();
				}
			);
		},
		_initContent: function() {
			if (!this.o.allowSorting) {
				$('.drag-handle', this.$container).hide();
			}
		},
		_refreshFieldNameIndex: function($target) {
			var plugin = this;
			var index = 0;
			$('.box', this.$container).each(function() {
				$('[name^="categories"]', this).each(function() {
					var newName = $(this).attr('name').replace(/[0-9]+/, index);
					$(this).attr('name', newName);
				});
				index++;
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
	
	$.fn.assignmentUnitCategoryInfoDialog = function(option) {
		var pluginDataName = 'assignmentUnitCategoryInfoDialog';
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
	$.fn.assignmentUnitCategoryInfoDialog.Constructor = Plugin;

	var defaults = $.fn.assignmentUnitCategoryInfoDialog.defaults = {
		multiple: true,
		popupUrl: "<c:url value='/assignmentManagement/PEViewAssignmentMaintenance/assignmentUnitCategoryInfoDialog'/>",
		getContentUrl: function(){return '';},
		getContentQueryParam: function(){return {};},
		allowSorting: true
	};
}(window.jQuery));
</script>