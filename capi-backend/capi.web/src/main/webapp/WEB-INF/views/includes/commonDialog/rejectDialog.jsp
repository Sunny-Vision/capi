<script id="rejectDialogModalTemplate" type="text/html">
<div class="modal fade" role="dialog">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">X</span></button>
				<h4 class="modal-title">Reject Dialog</h4>
			</div>
			<div class="modal-body">
				<div class="form-horizontal">
					<div class="form-group">
						<label class="col-md-12 control-label" style="text-align: left">Reject Reason</label>
					</div>
					<div class="form-group">
						<div class="col-md-12">
							<textarea class="form-control remark" style="height: 100px"></textarea>
						</div>
					</div>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-info pull-left btn-submit">Submit</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
			</div>
		</div><!-- /.modal-content -->
	</div><!-- /.modal-dialog -->
</div>
</script>
<script>
(function($, undefined){
	var Plugin = function(element, options) {
		this._process_options(options);
		this.$element = $(element);
		
		this.$popupModal = null;

		this.$element.click($.proxy(this._showPopup, this));
	};
	Plugin.prototype = {
		constructor : Plugin,
		_process_options : function(opts) {
			this._o = $.extend({}, this._o, opts);
			var o = this.o = $.extend({}, this._o);
		},
		plugin: function(){return this;},
		_showPopup: function(src) {
			var plugin = this;
			
			if (this.$popupModal == null) {
				var html = $('#rejectDialogModalTemplate').html();
				var $html = $(html);
				$(document.body).append($(html));
				this.$popupModal = $html.modal('hide');
				$.proxy(this._initPopup, this)();
				
				plugin.$element.parent().find('.loading').remove();
				plugin.$element.removeAttr('disabled');
			}
			this.$popupModal.modal('show');
			$('.btn-submit', this.$popupModal).click($.proxy(plugin._callback, plugin));
		},
		
		_initPopup: function() {
			var plugin = this;
		},
		
		_callback: function() {
			var plugin = this;
			var remark = $('.remark', plugin.$popupModal).val();
			$.proxy(plugin.o.submitCallback, plugin, remark)();
			plugin.$popupModal.modal('hide');
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
	
	$.fn.rejectDialog = function(option) {
		var pluginDataName = 'rejectDialog';
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
	$.fn.rejectDialog.Constructor = Plugin;

	var defaults = $.fn.rejectDialog.defaults = {
		submitCallback: function(remark){}
	};
}(window.jQuery));
</script>