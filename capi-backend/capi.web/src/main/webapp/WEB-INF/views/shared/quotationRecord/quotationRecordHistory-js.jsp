<%
/**
 * quotation record history tabs
 * 
 * Controller permission required:
 * /shared/General/getHistoryQuotationRecord
 */
%>
<script>
(function($, undefined){
	var Plugin = function(element, options) {
		this._process_options(options);
		this.$element = $(element);
		this.$tabPane = $('.tab-pane', this.$element);
		
		var plugin = this;
		
		$('.history-date', this.$element).click(function() {
			plugin.$element.trigger('historyChanging');
			$('.history-date', plugin.$element).removeClass('active');
			$(this).addClass('active');
			plugin._loadHistory($(this).data('id'), plugin.$tabPane);
		});
		
		if ($('.history-date', this.$element).first().length > 0) {
			var id = $('.history-date', this.$element).first().data('id');
			this._loadHistory(id, this.$tabPane);
		}
	};
	Plugin.prototype = {
		constructor : Plugin,
		_process_options : function(opts) {
			this._o = $.extend({}, this._o, opts);
			var o = this.o = $.extend({}, this._o);
		},
		_loadHistory: function(id, $targetContainer) {
			var plugin = this;
			$targetContainer.empty();
			$.get(this.o.getHistoryQuotationRecordUrl, {id: id}, function(html) {
				var $html = $(html);
				$targetContainer.append($html);
				
				$.proxy(plugin._initHistory, plugin, $targetContainer)();
				
				plugin.$element.trigger('historyChanged');
			});
		},
		_initHistory: function($targetContainer) {
			var plugin = this;
			$('.readonly-quotation-record-form-container', $targetContainer).quotationRecordFormContainer({
				currentQuotationRecordFormContainer: plugin.o.$currentQuotationRecordFormContainer
			});
			
			if (plugin.o.$currentQuotationRecordFormContainer != null) {
				var isCurrentReadonly = plugin.o.$currentQuotationRecordFormContainer.quotationRecordFormContainer('isReadonly');
				if (isCurrentReadonly) {
					$('.btn-copy', $targetContainer).hide();
				}
			}
			
			if ($('.history-date', this.$element).first().length > 0){
				var isHiddenFieldOfficer = ($('.history-date').attr('data-isHiddenFieldOfficer') !== "" 
											&& !_.isUndefined($('.history-date').attr('data-isHiddenFieldOfficer'))) ? $('.history-date').attr('data-isHiddenFieldOfficer') : true ;
				if(isHiddenFieldOfficer === true){
					$('.fieldOfficer', $targetContainer).hide();
				}
			}
			
			function onBackNoTabTabChanged() {
				if (plugin.o.$currentQuotationRecordFormContainer.find('.quotation-record-form.back-no.active').length > 0) {
					$targetContainer.find('.quotation-record-form.active').quotationRecordForm('showExtraRow', true);
				} else {
					$targetContainer.find('.quotation-record-form.active').quotationRecordForm('showExtraRow', false);
				}

				if (plugin.o.$currentQuotationRecordFormContainer != null) {
					var isCurrentReadonly = plugin.o.$currentQuotationRecordFormContainer.quotationRecordFormContainer('isReadonly');
					if (isCurrentReadonly) {
						$('.btn-copy', $targetContainer).hide();
					}
				}
			}
			onBackNoTabTabChanged();
			
			$targetContainer.on('backNoTabChanged', onBackNoTabTabChanged);
			
			plugin.o.$currentQuotationRecordFormContainer.on('backNoTabChanged', onBackNoTabTabChanged);
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
	
	$.fn.quotationRecordHistory = function(option) {
		var pluginDataName = 'quotationRecordHistory';
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
	$.fn.quotationRecordHistory.Constructor = Plugin;

	var defaults = $.fn.quotationRecordHistory.defaults = {
		getHistoryQuotationRecordUrl: "<c:url value='/shared/General/getHistoryQuotationRecord'/>",
		$currentQuotationRecordFormContainer: null
	};
}(window.jQuery));
</script>