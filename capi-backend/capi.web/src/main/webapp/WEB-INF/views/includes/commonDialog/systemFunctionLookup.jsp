<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script>
(function ($) {
	$.fn.systemFunctionLookup = function(opts) {
		var $input = this;
		var _def = {
			multiple: true,
			popupUrl: "<c:url value='/commonDialog/SystemFunctionLookup/home'/>",
			queryUrl: "<c:url value='/commonDialog/SystemFunctionLookup/query'/>",
			selectAllUrl: "<c:url value='/commonDialog/SystemFunctionLookup/getLookupTableSelectAll'/>",
			defaultOrder: [[ 1, "asc" ]],
			selectedIdsCallback: function(selectedIds, singleRowData){},
			queryDataCallback: function(dataModel){},
			alreadySelectedIdsCallback: function(){}
		};
		var settings = $.extend(_def, opts);

		var $popupModal;
		var $popupTable;
		var selectedIds = [];
		
		$input.click(function() {
			showPopup();
		});
		
		function showPopup() {
		    loadSettingsAlreadySelectedIds();
			if ($popupModal == null) {				
				var loading = '<img class="loading" src="<c:url value='/resources/images/ui-anim_basic_16x16.gif' />" style="border:0px; position: absolute" />';
				$input.before(loading);
				$input.attr('disabled', 'disabled');
				
				$.get(settings.popupUrl, function(html) {
					var $html = $(html);
					$(document.body).append($html);
					$popupModal = $html.modal('hide');

					initPopup();
					$popupModal.modal('show');
					
					$input.parent().find('.loading').remove();
					$input.removeAttr('disabled');
				});
			} else {
				$popupModal.modal('show');
				refresh();
			}
		}
		
		function refresh() {
			$popupTable.DataTable().ajax.reload();
		}
		
		function loadSettingsAlreadySelectedIds() {
			var ids = settings.alreadySelectedIdsCallback();
			if (ids == null) ids = '';
			if (typeof(ids) == "string") {
				ids = ids.split(",");
			}
			selectedIds = [];
			
			for (var i = 0; i < ids.length; i++) {
				if (ids[i] == '') continue;
				selectedIds.push(+ids[i]);
			}
		}
		
		function initPopup() {
			var status = $('[name="status"]'. $popupModal).val();
			var reviewed = $('[name="reviewed"]'. $popupModal).val();
			
			$popupTable = $(".datatable", $popupModal);
			
			var buttons = [];
			
			if (settings.multiple) {
				buttons.push({
								"text": "Select",
								"action": function( nButton, oConfig, flash ) {
									callbackSelected(selectedIds);
								}
							});
				buttons.push({
								"text": "Select All",
								"action": function( nButton, oConfig, flash ) {
									var dataModel = {
										search: this.settings().search()
									};
									settings.queryDataCallback(dataModel);
									$.get(settings.selectAllUrl,
										dataModel,
										function(selectedIds) {
											callbackSelected(selectedIds);
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
			
			if (settings.multiple) {
				columnDefs.push({ "visible": false, "targets": "select-single" });
			} else {
				columnDefs.push({ "visible": false, "targets": "select-multiple" });
			}
			
			if (1 == 0) { // put product read permission checking here
				columnDefs.push({ "visible": false, "targets": "view-product" });
			}
			
			$popupTable.DataTable({
				"ordering": true, 
				"order": settings.defaultOrder,
				"searching": true,
				"buttons": buttons,
				"processing": true,
	            "serverSide": true,
	            "ajax": {
	            	url: settings.queryUrl,
	            	data: function(dataModel) {
	            		settings.queryDataCallback(dataModel);
	            	},
	            	method: "post"
	            },
	            "columns": [
	                        { "data": "id",
	                        	"render" : function(data, type, full, meta){
	                        		var checked = selectedIds.indexOf(data) != -1;
                            		var html = '<input name="id" type="checkbox" value="' + data + '"' + (checked ? ' checked' : '') + '/>';
                            		return html;
                           		}
	                        },
	                        { "data": "code" },
	                        { "data": "description" },
	                        { "data": "id",
	                        	"render": function(data) {
	                        		return '<button class="btn-select-single btn btn-default" data-id="' + data + '">Select</button>';
	                        	}
	                        }
							],
	            "columnDefs": columnDefs,
	            "drawCallback": function() {
	            	$('.select_all', $popupTable).prop('checked', false);
	            }
			});
			
			$('.select_all', $popupTable).click(function() {
				$('[name="id"]', $popupTable).prop('checked', $(this).prop('checked')).trigger('change');
			});
			
		    $popupTable.on('click', '.btn-select-single', function() {
		    	var id = $(this).data('id');
			    var selectedIds = [id];
				var selectedRows = $popupTable.DataTable().rows(function(idx, data, node) {
					return data.id == id;
				}).data().toArray();
				
			    callbackSelected(selectedIds, selectedRows[0]);
			});
		    
		    $popupTable.on('change', '[name="id"]', function() {
		    	var id = +$(this).val();
		    	if ($(this).prop('checked')) {
		    		if (selectedIds.indexOf(id) == -1) {
		    			selectedIds.push(id);
		    		}
		    	} else {
		    		var index = selectedIds.indexOf(id);
		    		if (index != -1) {
		    			selectedIds.splice(index, 1);
		    		}
		    	}
		    });
		}
		
		function callbackSelected(selectedIds, singleRowData) {
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
			else{
				for (var i = 0; i < selectedIds.length; i++) {
					selectedIds[i] = +selectedIds[i];
				}
				settings.selectedIdsCallback(selectedIds, singleRowData);
			    $popupModal.modal('hide');
			}
		}
		
		return $input;
	};
}(jQuery));
</script>