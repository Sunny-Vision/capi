<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<script>
(function($, undefined){
	var Plugin = function(element, options) {
		this._process_options(options);
		this.$element = $(element);
		
		this.$popupModal = null;
		this.$popupTable = null;
		this.selectedIds = [];
		
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
			
			plugin._loadSettingsAlreadySelectedIds();
			var textModel = {
					productGroupText: plugin.o.productGroupText,
					productAttributeText: plugin.o.productAttributeText
				};
			plugin.o.queryDataCallback(textModel);

			if (this.$popupModal == null) {
				var loading = '<img class="loading" src="<c:url value='/resources/images/ui-anim_basic_16x16.gif' />" style="border:0px; position: absolute" />';
				plugin.$element.before(loading);
				plugin.$element.attr('disabled', 'disabled');
				
				$.get(plugin.o.popupUrl, function(html) {
					var $html = $(html);
					$(document.body).append($html);
					plugin.$popupModal = $html.modal('hide');
					
					$.proxy(plugin._initPopup, plugin)();
					plugin.$popupModal.modal('show');
					
					$("#productGroupText", plugin.$popupModal).text(textModel.productGroupText());
					$("#productAttributeText", plugin.$popupModal).html(textModel.productAttributeText());

					plugin.$element.parent().find('.loading').remove();
					plugin.$element.removeAttr('disabled');
				});
			} else {
				plugin.$popupModal.modal('show');
				$("#productGroupText", plugin.$popupModal).text(textModel.productGroupText());
				$("#productAttributeText", plugin.$popupModal).html(textModel.productAttributeText());

				plugin._refresh();
			}
		},

		_refresh: function() {
			this.$popupTable.DataTable().ajax.reload();
		},
		
		_loadSettingsAlreadySelectedIds: function() {
			var ids = $.proxy(this.o.alreadySelectedIdsCallback, this)();
			if (ids == null) ids = '';
			if (typeof(ids) == "string") {
				ids = ids.split(",");
			}
			this.selectedIds = [];
			
			for (var i = 0; i < ids.length; i++) {
				if (ids[i] == '') continue;
				this.selectedIds.push(+ids[i]);
			}
		},

		_initPopup: function() {
			var plugin = this;
			
			var productGroupId = plugin.o.productGroupId;
			var productAttributeId = plugin.o.productAttributeId;
			
			plugin.$popupTable = $(".datatable", plugin.$popupModal);
			
			var buttons = [];
			
			if (plugin.o.multiple) {
				buttons.push({
								"text": "Select",
								"action": function( nButton, oConfig, flash ) {
									$.proxy(plugin._callbackSelected, plugin, plugin.selectedIds)();
								}
							});
				buttons.push({
								"text": "Select All",
								"action": function( nButton, oConfig, flash ) {
									var dataModel = {
										search: this.settings().search(),
										productGroupId: plugin.o.productGroupId,
										productAttributeId: plugin.o.productAttributeId,
										productGroupText: plugin.o.productGroupText,
										productAttributeText: plugin.o.productAttributeText
									};
									$.proxy(plugin.o.queryDataCallback, plugin, dataModel)();
									$.get(plugin.o.selectAllUrl,
										dataModel,
										function(selectedIds) {
											$.proxy(plugin._callbackSelected, plugin, selectedIds)();
										}
									);
								}
							});
			}
			
			
			
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
			
			if (plugin.o.multiple) {
				columnDefs.push({ "visible": false, "targets": "select-single" });
			} else {
				columnDefs.push({ "visible": false, "targets": "select-multiple" });
			}
			
			plugin.$popupTable.DataTable({
				"ordering": true, 
				"order": plugin.o.defaultOrder,
				"searching": true,
				"buttons": buttons,
				"processing": true,
	            "serverSide": true,
	            "ajax": {
	            	url: plugin.o.queryUrl,
	            	data: function(dataModel) {
	            		if (plugin.o.productGroupId != null)
	            			dataModel.productGroupId = plugin.o.productGroupId;

	            		if (plugin.o.productAttributeId != null)
	            			dataModel.productAttributeId = plugin.o.productAttributeId;
	            		
	            		$.proxy(plugin.o.queryDataCallback, plugin, dataModel)();
	            	},
	            	method: "post"
	            },
	            "columns": [
	                        { "data": "productId",
	                        	"render" : function(data, type, full, meta){
	                        		var checked = plugin.selectedIds.indexOf(data) != -1;
                            		var html = '<input name="id" type="checkbox" value="' + data + '"' + (checked ? ' checked' : '') + '/>';
                            		return html;
                           		}
	                        },
	                        { "data": "productId" },
	                        { "data": "attributeValue" }
				],
	            "columnDefs": columnDefs,
	            "drawCallback": function() {
	            	$('.select_all', plugin.$popupTable).prop('checked', false);
	            }
			});
				
			$('.filters', plugin.$popupModal).change(function(){
				$.proxy(plugin._refresh, plugin)();
			});
			
			$('.select_all', plugin.$popupTable).click(function() {
				$('[name="id"]', plugin.$popupTable).prop('checked', $(this).prop('checked')).trigger('change');
			});
			
		    plugin.$popupTable.on('click', '.btn-select-single', function() {
			    var selectedIds = [$(this).data('id')];
			    $.proxy(plugin._callbackSelected, plugin, selectedIds)();
			});

		    plugin.$popupTable.on('change', '[name="id"]', function() {
		    	var id = +$(this).val();
		    	if ($(this).prop('checked')) {
		    		if (plugin.selectedIds.indexOf(id) == -1) {
		    			plugin.selectedIds.push(id);
		    		}
		    	} else {
		    		var index = plugin.selectedIds.indexOf(id);
		    		if (index != -1) {
		    			plugin.selectedIds.splice(index, 1);
		    		}
		    	}
		    });
		},
		
		_callbackSelected: function(selectedIds) {
			if (selectedIds.length == 0){
				bootbox.alert({
					title: "Alert",
					message:"<spring:message code='E00009' />"
				});
				return;
			}
			else if (selectedIds.length > 2000){
				bootbox.alert({
					title: "Alert",
					message:"<spring:message code='E00151' />"
				});
				return;
			}
			for (var i = 0; i < selectedIds.length; i++) {
				selectedIds[i] = +selectedIds[i];
			}
			$.proxy(this.o.selectedIdsCallback, this, selectedIds)();
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
	
	$.fn.productAttributeLookup = function(option) {
		var pluginDataName = 'productAttributeLookup';
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
	$.fn.productAttributeLookup.Constructor = Plugin;

	var defaults = $.fn.productAttributeLookup.defaults = {
		multiple: true,
		popupUrl: "<c:url value='/commonDialog/ProductAttributeLookup/home'/>",
		queryUrl: "<c:url value='/commonDialog/ProductAttributeLookup/query'/>",
		selectAllUrl: "<c:url value='/commonDialog/ProductAttributeLookup/getLookupTableSelectAll'/>",
		defaultOrder: [[ 1, "asc" ]],
		selectedIdsCallback: function(selectedIds){},
		queryDataCallback: function(dataModel){},
		alreadySelectedIdsCallback: function(){},
		productGroupId: null,
		productAttributeId: null
	};
}(window.jQuery));
</script>