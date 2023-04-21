$(function() {
	$.fn.radioPhotos = function(opts) {
		var $containers = this;
		var _def = {
			photos: [],
			cssClass: 'img-responsive viewer',
			style: 'margin:auto; max-height: 100px; max-width: 200px'
		};
		
		$containers.each(function() {
			var $container = $(this);

			if ($container.data('photos') != null) {
				_def.photos = $container.data('photos').replace(/\s/g,'').split(',');
			}
			
			var settings = $.extend(_def, opts);
			
			var $btnContainer = $('<div style="text-align: center; margin-top: 3px"></div>');
			for (var i = 0, len = settings.photos.length; i < len; i++) {
				var photo = settings.photos[i];
				var style = settings.style;
				if (i > 0) style += '; display:none';
				
				$html = $('<img src="' + photo + '" class="' + settings.cssClass + '" style="' + style + '"/>');
				
				$container.append($html);
				
				$btn = $('<input data-index="' + i + '" type="radio" style="display: inline-block; margin: 0 5px"/>');
				$btnContainer.append($btn);
			}
			$container.append($btnContainer);
			
			$('input', $container).first().prop('checked', true);
			
			$('input', $container).click(function() {
				$('input', $container).prop('checked', false);
				$(this).prop('checked', true);
				$('img', $container).hide();
				var index = $(this).data('index');
				$($('img', $container)[index]).show();
			});
		})
		
		return $containers;
	};
});