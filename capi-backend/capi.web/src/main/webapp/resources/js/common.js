"use strict";
(function($){
	
	if ($.fn != null && $.fn.dataTable != null){
		// always add buttons empty array when init datatable: "buttons": [],
		
		$.extend( true, $.fn.dataTable.defaults, {
			"dom": '<"row separator bottom"<"col-sm-12"T<"leftTool"B><"text-align"r><"rightTool"f><"rightTool"l>>>'+  
			't'+
			'<"row"<"col-sm-12"<"leftTool"i><"rightTool"p>>>'
		} );
		
		
		$.extend( true, $.fn.dataTable.defaults, {
		    "searching": false,
		    "ordering": false,
		    "autoWidth": false,
		    "processing" : true,
		    "language": {
		        "processing": "Loading data from server ...",
		        "loadingRecords": "Please wait - loading..."
		     },
		     "initComplete":function(settings, json){
		    	 if (settings.ajax != null && settings.ajax.url != '' && settings.ajax.url != null){
		    		 var $table = $(this);
		    		 var $search = $(this).parents('.dataTables_wrapper:first').find('input[type=search]');
			    	 $search.unbind('keyup.DT search.DT input.DT paste.DT cut.DT keypress.DT');
			    	 $search.bind('keypress.DT', function(e){
			    		 if ( e.keyCode == 13 ) {
			    			 $table.DataTable().search($search.val()).draw();
			    			return false;
						}
			    	 })
		    	 }		    	 
		     }
		} );

		$.fn.dataTable.addResponsiveButtons = function(buttons) {
			buttons.push({
				"text": "Expand All",
				"action": function(e, dt, node, config) {
					$(dt.table().body()).find("tr:not(.parent)").each(function(){
						$("td:first", this).click();
					})
				}
			});
			
			buttons.push({
				"text": "Collapse All",
				"action": function(e, dt, node, config) {
					$(dt.table().body()).find("tr.parent").each(function(){
						$("td:first", this).click();
					})
				}
			});
		};
		$(document).ready(function(){
			$("body").on($.support.transition.end, '.main-sidebar', function(event){
				// response on menu change
				var $tables = $('table.responsive');
				$tables.each(function() {
					var $table = $(this);
					$table.DataTable().columns.adjust();
					$table.DataTable().responsive.recalc();
				});
			});
			
			$('table.responsive').on('draw.dt', function() {
				var $table = $(this);
				$table.DataTable().columns.adjust();
				$table.DataTable().responsive.recalc();
			});
		});
		
		if ($.fn.dataTable.AutoFill != null) {
			$.fn.dataTable.AutoFill.initFillInput = function() {
				$.fn.dataTable.AutoFill.actions.fill =  {
					available: function ( dt, cells ) {
						return true;
					},

					option: function ( dt, cells ) {
						return '';
					},

					execute: function ( dt, cells, node ) {
						var value = $(dt.cell(cells[0][0].cell[0][0].row, 1).node()).find('input,select').val();

						for ( var i=0, ien=cells.length ; i<ien ; i++ ) {
							for ( var j=0, jen=cells[i].length ; j<jen ; j++ ) {
								$(dt.cell(cells[i][j].cell[0][0].row, 1).node()).find('input,select').val(value);
							}
						}
					}
				};
			};
		}
	}
	
	if ($.fn.datepicker != null){
	    $.extend( true, $.fn.datepicker.defaults, {
	        format: "dd-mm-yyyy"
	    } );
	}
	

	$(".select_all").change(function() {
		var checkboxes = $(this).closest('table').find(':checkbox');
		if($(this).is(':checked')) {
			checkboxes.prop('checked', true);
		} else {
			checkboxes.prop('checked', false);
		}
	});
	
	$("button[type='reset']").on("click", function(){
		var $btn = $(this);
		var $form = $btn.parents("form");
		// handel picker value reset. please donnot edit. or ask before edit.
		$("input[type='hidden']", $form).val("");
		// end;
		$("option:selected", $form).removeAttr("selected");
		$("select", $form).val("").trigger("change");
		
	});
	
	// auto hide bootstrap tooltip
	$(document).on('show.bs.tooltip', function (e) {
		if ($(e.target).data('trigger') == 'click') {
			var timeoutDataName = 'shownBsTooltipTimeout';
			if ($(e.target).data(timeoutDataName) != null) {
				clearTimeout($(e.target).data(timeoutDataName));
			}
			var timeout = setTimeout(function () {
				$(e.target).click();
			}, 5000);
			$(e.target).data(timeoutDataName, timeout);
		}
	});

	$(document).on('hide.bs.tooltip', function (e) {
		if ($(e.target).data('trigger') == 'click') {
			var timeoutDataName = 'shownBsTooltipTimeout';
			if ($(e.target).data(timeoutDataName) != null) {
				clearTimeout($(e.target).data(timeoutDataName));
			}
		}
	});
})(jQuery)

var Modals = function () {
	
	var noticeTmpl =
		'<div class="modal fade" tabindex="-1">' +
			'<div class="modal-dialog" style="width: 1000px; z-index: 1;">' +
				'<div class="modal-content">' +
					'<div class="modal-header">' +
					'</div>' +
					'<div class="modal-body">' +
					'</div>' +
					'<div class="modal-footer">' +
					'</div>' +
				'</div>' +
			'</div>'
		'</div>';

	return {
        //main function to initiate the module
        init: function () {

            // general settings
            $.fn.modal.defaults.spinner = $.fn.modalmanager.defaults.spinner =
              '<div class="loading-spinner" style="width: 200px; margin-left: -100px;">' +
                '<div class="progress progress-striped active">' +
                  '<div class="progress-bar" style="width: 100%;"></div>' +
                '</div>' +
              '</div>';

            $.fn.modalmanager.defaults.resize = true;
            
            // ajax-modal
            $(document).on('click', '[data-toggle="ajax-modal"]', function (e) {
            	e.preventDefault();
            	
                $('body').modalmanager('loading');
                var target = $(this).data('target');
                var onshow = $(this).data('onshow');
                var onclose = $(this).data('onclose');
                var modal = $($(this).attr('href'));
                setTimeout(function () {
                	modal.load(target, '', function () {
                		if (typeof(onshow) != 'undefined' && typeof(eval(onshow)) == 'function') {
                			eval(onshow+'(this)');
                		}
                		if (typeof(onclose) != 'undefined' && typeof(eval(onclose)) == 'function') {
                			modal.on('hidden.bs.modal', function() {
                				eval(onclose+'()');
                			});
                		}
                		modal.modal();
                		App.updateUniform();
                		FormComponents.select2(modal.find('.select2'));
                    });
                }, 10);
            });
        },
        scrollableNotice: function(message, header, footer, callback) {
        	var modal = $(noticeTmpl);
        	var $header = $('<div class="bootbox-header" ></div>');
        	$header.html(header);
        	$header.append('<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>');
        	var $content = $('<div class="bootbox-body" ></div>');
        	$content.html(message);
        	var $footer = $('<div class="bootbox-footer" ></div>');
        	$footer.html(footer + '<button type="button" data-dismiss="modal" class="btn blue">OK</button>');
        	modal.find('.modal-body').append($content);
        	modal.find('.modal-header').append($header);
        	modal.find('.modal-footer').append($footer);
        	if (typeof(callback) != 'undefined' && typeof(eval(callback)) == 'function') {
        		modal.find('button[data-dismiss]').click(function(e) {
        			callback.call(this);
        		});
        	}
        	modal.modal();
        },
		startLoading: function(closeOnClick) {
			if (!jQuery().modalmanager) return;
			$('body').modalmanager('loading');
			if (!closeOnClick) $('.modal-scrollable').unbind('click');
		},
		endLoading: function() {
			if (!jQuery().modalmanager) return;
			$('body').modalmanager('removeLoading');
		}
    };
} ();

var Datepicker = function(els, opts) {
	if (!jQuery().datepicker) return;
	if (!els) els = '.date-picker';
	
	function init() {
	$(els).each(function() {
		var $t = $(this);
		var _d = {
			format: 'dd-mm-yyyy',
			viewMode: 'days',
			minViewMode: 'days',
			autoclose: true,
			todayHighlight: true,
			todayBtn: 'linked',
			weekStart: 0,
			language: 'zh-TW',
		};
		if ($t.data('date-format')) _d.format = $t.data('date-format');
		if ($t.data('date-viewmode')) _d.viewMode = $t.data('date-viewmode');
		if ($t.data('date-minviewmode')) _d.minViewMode = $t.data('date-minviewmode');
		if ($t.data('date-clearbtn')) _d.clearBtn = true;
		if ($t.data('orientation')){ _d.orientation = $t.data('orientation');}
		if ($t.data('date-startdate')){ _d.startDate = $t.data('date-startdate');}
		if ($t.data('date-enddate')){ _d.endDate = $t.data('date-enddate');}
		if ($t.data('date-datesdisabled')){ 
			// array of milliseconds
			var splitString = $t.data('date-datesdisabled')+'';
			_d.datesDisabled = splitString.split(",");
			
			_d.beforeShowDay = function (date){
		    	for (var i = 0; i < _d.datesDisabled.length; i++){
		    		var disableDate = new Date(parseInt(_d.datesDisabled[i]));
		    		if (disableDate.getTime() === date.getTime()){
		    			return { "enabled": false };
		    		}
		    	}
    			return { "enabled": true };
		    }

			
		}
		if ($t.data('multidate')){ 
			_d.multidate = $t.data('multidate');
			_d.autoclose = false;

			var target = $t.attr("data-target");
			var input = $t.attr("data-input");
			
			$t.on("change", "input"+input, function(){
				if(target !== typeof undefined && target !== false && input !== typeof undefined && input !== false){
					target = $("select"+$t.data("target"));
					input = $($t.data("input"));
					target.html("");
					var datas = input.val().split(",");
					$(datas).each(function(){
						if(this != ""){
							target.append("<option selected>"+this+"</option>");
						}
					});
					//select2 fix;
					$("input.select2-search__field").val("");
					//end select2 fix;
					target.trigger("change");
					
					if ($.validator != null && input != null) {
						var $element = input;
			        	if ($element.closest('form').length > 0) {
			        		if ($element.closest('form').data('validator') != null) {
			        	    	$element.valid();
			        		}
			        	}
			        }
				}
				// select2 fix;
			})
			
		}
		
		$.extend(_d, opts);
		
		var datepickerelem = $t.datepicker(_d);
		$t.datepicker('update');
		
	});
	$('body').removeClass("modal-open"); // fix bug when inline picker is used in modal
	}

	init();
};

function normalFormPost(path, params, method) {
    method = method || "post"; // Set method to post by default if not specified.

    // The rest of this code assumes you are not using a library.
    // It can be made less wordy if you use one.
    var form = document.createElement("form");
    form.setAttribute("method", method);
    form.setAttribute("action", path);

    for(var key in params) {
        if(params.hasOwnProperty(key)) {
        	if (Array.isArray(params[key])) {
        		for (var i in params[key]) {
        			var hiddenField = document.createElement("input");
    	            hiddenField.setAttribute("type", "hidden");
    	            hiddenField.setAttribute("name", key);
    	            hiddenField.setAttribute("value", params[key][i]);

    	            form.appendChild(hiddenField);
        		}
        		continue;
        	}
            var hiddenField = document.createElement("input");
            hiddenField.setAttribute("type", "hidden");
            hiddenField.setAttribute("name", key);
            hiddenField.setAttribute("value", params[key]);

            form.appendChild(hiddenField);
         }
    }

    document.body.appendChild(form);
    form.submit();
}

function intToStringPadding(n, width, z) {
	  z = z || '0';
	  n = n + '';
	  return n.length >= width ? n : new Array(width - n.length + 1).join(z) + n;
}

function availabilityDisplay(data) {
	switch(data) {
		case 1:
			return 'Available';
		case 2:
			return 'IP';
		case 3:
			return '有價無貨';
		case 4:
			return '缺貨';
		case 5:
			return 'Not Suitable';
		case 6:
			return '回倉';
		case 7:
			return '無團';
		case 8:
			return '未返';
		case 9:
			return 'New';
	}
	
	return '';
}

function viewOnly(){
	$("select,input:text,input:radio,input:checkbox,textarea,input:file,input[type='number']")
	.unbind('change')
	.attr("disabled", true)
	.trigger('change');
	
	$(".box-body,.modal-body,.modal-footer").find("button,input:button,input:submit,input:reset").attr("disabled", true)
  
	$('.box-body,.modal-body').find('.fa-search').parent().css('visibility', 'hidden')
}

var roundUpto = function(number, upto){
    return Number(number.toFixed(upto));
}

//http://stackoverflow.com/questions/3446170/escape-string-for-use-in-javascript-regex
function escapeRegExp(str) {
	return str.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&");
}