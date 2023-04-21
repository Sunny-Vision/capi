<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<script>
(function($, undefined){
	var Plugin = function(element, options) {
		this._process_options(options);
		this.$element = $(element);

		this.$popupModal;
		this.$popupTable = null;
		this.selectedIds = [];

		this.$element.click($.proxy(this._showPopup, this));
	};
	
	 function escapeRegExp(str) {
		  str = str.replace(/&/g, "&amp;");
		  str = str.replace(/>/g, "&gt;");
		  str = str.replace(/</g, "&lt;");
		  str = str.replace(/"/g, "&;");
		  str = str.replace(/'/g, "&#039;");
		  
		  return str;
	    }
	
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
			if (plugin.$popupModal == null) {
				var loading = '<img class="loading" src="<c:url value='/resources/images/ui-anim_basic_16x16.gif' />" style="border:0px; position: absolute" />';
				plugin.$element.before(loading);
				plugin.$element.attr('disabled', 'disabled');
				
				$.get(plugin.o.popupUrl, function(html) {
					var $html = $(html);
					$(document.body).append($html);
					plugin.$popupModal = $html.modal('hide');

					$.proxy(plugin._initPopup, plugin)();
					plugin.$popupModal.modal('show');
					
					plugin.$element.parent().find('.loading').remove();
					plugin.$element.removeAttr('disabled');
				});
			} else {
				plugin.$popupModal.modal('show');
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
			
			var status = $('[name="status"]', plugin.$popupModal).val();
			var reviewed = $('[name="reviewed"]', plugin.$popupModal).val();
			
			plugin.$popupTable = $(".datatable", plugin.$popupModal);
			
			var buttons = [];
			
			var columnDefs = [
	                           {
	                               "targets": "action",
	                               "orderable": false,
	                               "searchable": false,
	                        	   "className" : "text-center"
	                           },
	                           {
	                        	   "targets": "_all",
	                        	   "className" : "text-center"
	                           }
								];
			
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
	            		var purposeId = $('[name="purposeId"]', plugin.$popupModal).val();
	            		if (purposeId != '')
	            			dataModel.purposeId = purposeId;

	            		var outletTypeId = $('[name="outletTypeId"]', plugin.$popupModal).val();
	            		if (outletTypeId != '')
	            			dataModel.outletTypeId = outletTypeId;
	            		
	            		$.proxy(plugin.o.queryDataCallback, plugin, dataModel)();
	            	},
	            	method: "post"
	            },
	            "fnRowCallback": function( nRow, aData, iDisplayIndex, iDisplayIndexFull ) {
	            	if ( aData.isProductChange )
	            		$(nRow).addClass( "productChange" );
            		return nRow;
	            },
	            "columns": [
	                        { "data": "productId",
	                        	"render" : function(data, type, full, meta){
                            		var html = 
                            			'<button class="btn btn-xs btn-info btn-product-spec" '+
                            			'type="button" data-productspecdialog-product-id="'+data+'"><i class="fa fa-search"></i></button>';
                            		return html;
                           		}
	                        },
	                        { "data": "submissionDateStr" },
	                        { "data": "referenceMonthStr" },
	                        { "data": "collectedNPrice" },
	                        { "data": "collectedSPrice" },
	                        { "data": "subPriceId",
	                        	"render" : function(data, type, full, meta){
                            		var html = '';
                            		if(typeof data != undefined && data != null && data != '')
                            			html = '<a class="btn btn-xs btn-info" '+
                            			'href="<c:url value="/commonDialog/SubPriceDetails/home?quotationRecordId1="/>'+full.quotationRecordId+'" target="_blank"><i class="fa fa-search"></i></a>';
                            		return html;
                           		} },
	                        { "data": "discount" },
	                        { "data": "availability",
	                        	"render" : function(data, type, full, meta){
	                        		/**
	                        		 * 1- Available
	                        		 * 2- IP
	                        		 * 3- 有價無貨
	                        		 * 4- 無價無貨 -> 缺貨
	                        		 * 5- Not Suitable
	                        		 * 6- 回倉
	                        		 * 7- 無團
	                        		 * 8- 未返
	                        		 * 9- New
	                        		 */
	                        		var text = ["-", "Available",  "IP", "有價無貨", "缺貨", "Not Suitable", "回倉", "無團", "未返", "New"];
	                        		return  text[data == null ? 0 : data];
	                        	}},
	                        { "data": "fr" },
	                        { "data": "previousNPrice" },
	                        { "data": "previousSPrice" },
	                        { "data": "currentNPrice" },
	                        { "data": "currentSPrice" },
	                        { "data": "remark",
	                        	"render" : function(data, type, full, meta){
                            		var html = '';

                            		if(full.isFlag){
                            			html = html + '<span class="glyphicon glyphicon-star" style="color:rgb(255, 204, 0)">&nbsp;</span>';
                            		}
                            		if(typeof data == "string" && data != ""){
                            			//console.log("Remark Information : "+ data);
                            			html = html + "<a href='javascript:void(0)' data-toggle='tooltip' title='"+escapeRegExp(data)+"'>Remarks</a>";  
                            			
                            		}
                            		return html;
                           		} },
	                        { "data": "max" },
	                        { "data": "min" },
	                        { "data": "average" },
	                       
							],
	            "columnDefs": columnDefs,
	            "drawCallback": function() {
	            	$('.select_all', plugin.$popupTable).prop('checked', false);
	            	$('[data-toggle="tooltip"]').tooltip({
	            		placement: 'right'
	            	});
	            	if ($.fn.productSpecDialog != null) {
	        			$('.btn-product-spec', this.$element).productSpecDialog();
	        		}
	            }
			});
			
			plugin.$popupTable.on('click', '.btn-select-single', function() {
			    var selectedIds = [$(this).data('id')];
			    $.proxy(plugin._callbackSelected, plugin, selectedIds)();
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
	
	$.fn.QuotationRecordHistoryDialog = function(option) {
		var pluginDataName = 'QuotationRecordHistoryDialog';
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
	$.fn.QuotationRecordHistoryDialog.Constructor = Plugin;

	var defaults = $.fn.QuotationRecordHistoryDialog.defaults = {
		multiple: true,
		popupUrl: "<c:url value='/commonDialog/QuotationRecordHistoryDialog/home'/>",
		queryUrl: "<c:url value='/commonDialog/QuotationRecordHistoryDialog/query'/>",
		defaultOrder: [[ 1, "desc" ]],
		selectedIdsCallback: function(selectedIds){},
		queryDataCallback: function(dataModel){},
		productGroupId: null,
		alreadySelectedIdsCallback: function(){}
	};
}(window.jQuery));
</script>
<style>
	/* .table-striped>tbody>tr:nth-child(odd).productChange:hover{
		background-color: #f9a9a9;
	}
	.table-striped>tbody>tr:nth-child(odd).productChange{
		background-color: #f9c9c9;
	}
	.table-striped>tbody>tr:nth-child(even).productChange{
		background-color: #fff9f9;
	}
	.table-striped>tbody>tr:nth-child(even).productChange:hover{
		background-color: #ffc9c9;
	} */
	.table-striped>tbody>tr.productChange{
		background-color: #f9c9c9;
	}
</style>