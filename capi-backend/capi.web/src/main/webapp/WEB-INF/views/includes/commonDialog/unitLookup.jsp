<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<script>
(function($, undefined){
	var Plugin = function(element, options) {
		this._process_options(options);
		this.$element = $(element);
		
		this.$popupModal = null;
		this.$initTree = null;
		this.lastCustomQueryData = null;
		
		var plugin = this;
		
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
			
			if (plugin._isLastCustomQueryDataChanged()) {
				plugin._destroyAndInitTree();
			}
			
			if (this.$popupModal == null) {
				var loading = '<img class="loading" src="<c:url value='/resources/images/ui-anim_basic_16x16.gif' />" style="border:0px; position: absolute" />';
				this.$element.before(loading);
				this.$element.attr('disabled', 'disabled');
				
				$.get(this.o.popupUrl, function(html) {
					var $html = $(html);
					$(document.body).append($html);
					plugin.$popupModal = $html.modal('hide');

					$.proxy(plugin._initPopup, plugin)();
					plugin.$popupModal.modal('show');
					
					plugin.$element.parent().find('.loading').remove();
					plugin.$element.removeAttr('disabled');
				});
			} else {
				this.$popupModal.modal('show');
			}
		},
		
		_extractEntityClassFromUrl: function(url) {
			var queryUrl = url.substring(this.o.queryUrl.length);
			var parentEntityClass = null;
			if (queryUrl.indexOf('?') != -1)
				entityClass = queryUrl.substring('/query'.length, queryUrl.indexOf('?'));
			else
				entityClass = queryUrl.substring('/query'.length);
			return entityClass;
		},
		
		_determineUrl: function(node) {
			var url = null;
			if (node.data == undefined)
				url = this.o.queryUrl + '/queryCpiBasePeriod';
			else {
				switch(node.data.entityClass) {
					case 'CpiBasePeriod':
						url = this.o.queryUrl + '/querySection';
						break;
					case 'Section':
						url = this.o.queryUrl + '/queryGroup';
						break;
					case 'Group':
						url = this.o.queryUrl + '/querySubGroup';
						break;
					case 'SubGroup':
						url = this.o.queryUrl + '/queryItem';
						break;
					case 'Item':
						url = this.o.queryUrl + '/queryOutletType';
						break;
					case 'OutletType':
						url = this.o.queryUrl + '/querySubItem';
						break;
					case 'SubItem':
						url = this.o.queryUrl + '/queryUnit';
						break;
					default:
						return;
				}
			}
			return url;
		},
		
		_initPopup: function() {
			var plugin = this;
			if (!this.o.multiple) {
				this.$popupModal.find('.modal-select').hide();
			}
			this._initTree();
			
			this.$popupModal.on('click', '.modal-select', function() {
				var postData = [];
				postData.push({ name: 'bottomEntityClass', value : plugin.o.bottomEntityClass });
				$(plugin.$jstree.jstree(true).get_top_checked(true)).each(function(i, node) {
					switch (node.data.entityClass) {
						case 'CpiBasePeriod':
							postData.push({ name: 'cpiBasePeriods', value: node.data.entityId });
							break;
						case 'Section':
							postData.push({ name: 'sectionIds', value: node.data.entityId });
							break;
						case 'Group':
							postData.push({ name: 'groupIds', value: node.data.entityId });
							break;
						case 'SubGroup':
							postData.push({ name: 'subGroupIds', value: node.data.entityId });
							break;
						case 'Item':
							postData.push({ name: 'itemIds', value: node.data.entityId });
							break;
						case 'OutletType':
							postData.push({ name: 'outletTypeIds', value: node.data.entityId });
							break;
						case 'SubItem':
							postData.push({ name: 'subItemIds', value: node.data.entityId });
							break;
						case 'Unit':
							postData.push({ name: 'unitIds', value: node.data.entityId });
							break;
					}
				});
				
				$.post(plugin.o.getBottomEntityIdsUrl, postData, function(ids) {
					if (ids == null) ids = [];
					$.proxy(plugin._callbackSelected, plugin, ids)();
				});
			});
		},
		
		_initTree: function() {
			var plugin = this;
			
			if (this.$jstree == null)
				this.$jstree = $(".jstree", this.$popupModal);
			
			var plugins = [];
			if (this.o.multiple)
				plugins.push("checkbox");
			
			this.$jstree.jstree({
				plugins: plugins,
				core: {
					data: function (node, callback) {
						var queryUrl = $.proxy(plugin._determineUrl, plugin, node)();
						
						var queryData = { onlyActive: plugin.o.onlyActive };
						if (node.data != undefined) {
							$.extend(queryData, { id : node.data.entityId });
						}
						
						var customQueryData = {};
						
						$.proxy(plugin.o.queryDataCallback, plugin, customQueryData)();
						
						plugin.lastCustomQueryData = customQueryData;
						
						$.extend(queryData, customQueryData);
						
						$.get(queryUrl, queryData, function(response) {
							if (response == null) return;
							
							var entityClass = $.proxy(plugin._extractEntityClassFromUrl, plugin, this.url)();
							
							var nodes = [];
							for (var i = 0, len = response.length; i < len; i++) {
								nodes.push({
									data: {
										entityId: response[i].id,
										entityClass: entityClass
									},
									text: response[i].label,
									children: plugin.o.bottomEntityClass == entityClass ? false : response[i].children
								});
							}
							callback(nodes);
						});
					}
				}
			});
			
			if (!this.o.multiple) {
				this.$jstree.on('select_node.jstree', function(e, data) {
					if (data.selected.length == 0) return;
					if (plugin.o.bottomEntityClass == data.node.data.entityClass) {
						$.proxy(plugin._callbackSelected, plugin, [data.node.data.entityId])();
					}
				});
			}
		},
		
		_callbackSelected: function(selectedIds) {
			if (!this.o.multiple && selectedIds.length == 0){
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

			if (this.o.bottomEntityClass == 'CpiBasePeriod') {
				for (var i = 0; i < selectedIds.length; i++) {
					selectedIds[i] = selectedIds[i];
				}
			} else {
				for (var i = 0; i < selectedIds.length; i++) {
					selectedIds[i] = +selectedIds[i];
				}
			}
			$.proxy(this.o.selectedIdsCallback, this, selectedIds)();
		    this.$popupModal.modal('hide');
		},
		
		_isLastCustomQueryDataChanged: function() {
			var plugin = this;
			
			if (plugin.lastCustomQueryData == null) return false;
			
			var customQueryData = {};
			$.proxy(plugin.o.queryDataCallback, plugin, customQueryData)();
			
			if (JSON.stringify(customQueryData) === JSON.stringify(plugin.lastCustomQueryData))
				return false;
			else
				return true;
		},
		
		_destroyAndInitTree: function() {
			var plugin = this;
			
			plugin.lastCustomQueryData = null;
			
			if (this.$jstree == null) return;
			
			plugin.$jstree.jstree('destroy');
			
			plugin._initTree();
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
	
	$.fn.unitLookup = function(option) {
		var pluginDataName = 'unitLookup';
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
	$.fn.unitLookup.Constructor = Plugin;

	var defaults = $.fn.unitLookup.defaults = {
		multiple: true,
		popupUrl: "<c:url value='/commonDialog/UnitLookup/home'/>",
		queryUrl: "<c:url value='/commonDialog/UnitLookup'/>",
		getBottomEntityIdsUrl: "<c:url value='/commonDialog/UnitLookup/getBottomEntityIds'/>",
		selectedIdsCallback: function(selectedIds){},
		queryDataCallback: function(dataModel){},
		/**
		 *  Bottom expanded entity class: CapiBasePeriod, Section, Group, SubGroup, Item, OutletType, SubItem, Unit
		 */
		bottomEntityClass: 'Unit',
		onlyActive: true
	};
}(window.jQuery));
</script>