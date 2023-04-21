(function ($) {
	$.fn.select2ajax = function(opts) {
		$input = this;
		var _def = {
			ajax: {
			    data: function (params) {
			    	return params;
				},
				method: 'GET'
			//	contentType: 'application/json'
			}
		};
		var settings = $.extend(_def, opts);
		return $input.select2(settings);
	};
}(jQuery));