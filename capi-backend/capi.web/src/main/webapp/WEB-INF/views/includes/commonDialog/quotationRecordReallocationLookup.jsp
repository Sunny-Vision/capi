<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script>
(function ($) {
	$.fn.quotationRecordReallocationLookup = function(opts) {
		var $input = this;
		var _def = {
			multiple: true,
			popupUrl: "<c:url value='/commonDialog/QuotationRecordReallocationLookup/home'/>",
			queryUrl: "<c:url value='/commonDialog/QuotationRecordReallocationLookup/query'/>",
			selectAllUrl: "<c:url value='/commonDialog/QuotationRecordReallocationLookup/getLookupTableSelectAll'/>",
			defaultOrder: [[ 1, "asc" ]],
			selectedIdsCallback: function(selectedIds, singleRowData){},
			queryDataCallback: function(dataModel){},
			tpuIds: null,
			outletTypeId: null,
			districtId: null,
			batchId: null,
			collectionDate: null,
			category: null,
			quotationStatus: null,
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
											search: this.settings().search(),
											tpuIds: $('[name="tpuIds"]', $popupModal).val(),
											outletTypeId: $('[name="outletTypeId"]', $popupModal).val(),
											districtId: $('[name="districtId"]', $popupModal).val(),
											batchId: $('[name="batchId"]', $popupModal).val(),
											collectionDate: $('[name="collectionDate"]', $popupModal).val(),
											category:  $('[name="category"]', $popupModal).val(),
											quotationStatus: $('[name="quotationStatus"]', $popupModal).val()
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
	            		var status = $('[name="status"]', $popupModal).val();
	            		if (status != '')
	            			dataModel.status = status;

	            		var reviewed = $('[name="reviewed"]', $popupModal).val();
	            		if (reviewed != '')
	            			dataModel.reviewed = reviewed;
	            		
	            		if (settings.tpuIds != null)
	            			dataModel.tpuIds = settings.tpuIds;
	            		else
	            			dataModel.tpuIds = $('[name="tpuIds"]', $popupModal).val();
	            		
	            		if (settings.outletTypeId != null)
	            			dataModel.outletTypeId = settings.outletTypeId;
	            		else
	            			dataModel.outletTypeId = $('[name="outletTypeId"]', $popupModal).val();
	            		
	            		if (settings.districtId != null)
	            			dataModel.districtId = settings.districtId;
	            		else
	            			dataModel.districtId = $('[name="districtId"]', $popupModal).val();
	            		
	            		if (settings.batchId != null)
	            			dataModel.batchId = settings.batchId;
	            		else
	            			dataModel.batchId = $('[name="batchId"]', $popupModal).val();
	            		
	            		if (settings.collectionDate != null)
	            			dataModel.collectionDate = settings.collectionDate;
	            		else
	            			dataModel.collectionDate = $('[name="collectionDate"]', $popupModal).val();
	            		
	            		if (settings.category != null)
	            			dataModel.category = settings.category;
	            		else
	            			dataModel.category = $('[name="category"]', $popupModal).val();
	            		
	            		if (settings.quotationStatus != null)
	            			dataModel.quotationStatus = settings.quotationStatus;
	            		else
	            			dataModel.quotationStatus = $('[name="quotationStatus"]', $popupModal).val();
	            		
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
	                        { "data": "collectionDate" },
	                        { "data": "startDate" },
	                        { "data": "endDate" },
	                        { "data": "firm" },
	                        { "data": "district" },
	                        { "data": "tpu" },
	                        { "data": "batchCode" },
	                        { "data": "displayName" },
	                        { "data": "category" },
	                        { "data": "quotationStatus" },
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
			
			if (settings.tpuIds == null) {
				$('[name="tpuIds"]', $popupModal).select2();
			}
			
			if (settings.outletTypeId == null) {
				$('[name="outletTypeId"]', $popupModal).select2ajax({allowClear: true,placeholder: '',width: "100%"});
			}
			
			if (settings.districtId == null) {
				$('[name="districtId"]', $popupModal).select2ajax({allowClear: true,placeholder: '',width: "100%"});
			}
			
			if (settings.batchId == null) {
				$('[name="batchId"]', $popupModal).select2ajax({allowClear: true,placeholder: '',width: "100%"});
			}
			
			if (settings.collectionDate == null) {
				$('[name="collectionDate"]', $popupModal).datepicker();
			}
			
			if (settings.category == null) {
				$('[name="category"]', $popupModal).select2ajax({allowClear: true,placeholder: '',width: "100%"});
			}
			
			$('.select_all', $popupTable).click(function() {
				$('[name="id"]', $popupTable).prop('checked', $(this).prop('checked')).trigger('change');
			});
			
			$('.filters', $popupModal).change(function(){
				refresh();
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
		    
			var dataTableAPI = $popupTable.DataTable();
		    
		    // Enable or Disable the buttons "Select" and "Select All"
		    // in DataTables
		    dataTableAPI.on( "draw.dt", function( event, settings ) {
		    	// Enable or Disable "Select All" button
		    	var recordsFiltered = +settings.json.recordsFiltered;
		    	
		    	if ( !( $.isNumeric( recordsFiltered ) 
		    			&& Math.floor( recordsFiltered ) === recordsFiltered
		    			&& recordsFiltered >= 0 ) ) {
		    		recordsFiltered = 0;
		    	}
		    	
		    	//	button( 1 ) represent second button in DataTable 
		    	//	=> "Select All"
		    	dataTableAPI.button( 1 ).enable( recordsFiltered > 0 );
		    } );
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