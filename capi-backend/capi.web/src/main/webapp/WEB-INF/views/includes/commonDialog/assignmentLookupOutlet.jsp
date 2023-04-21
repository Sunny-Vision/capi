<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<script>
(function ($) {
	$.fn.assignmentLookupOutlet = function(opts) {
		var $input = this;
		var _def = {
			multiple: true,
			popupUrl: "<c:url value='/commonDialog/AssignmentLookup/outletHome'/>",
			queryUrl: "<c:url value='/commonDialog/AssignmentLookup/outletQuery'/>",
			selectAllUrl: "<c:url value='/commonDialog/AssignmentLookup/getLookupTableSelectAll'/>",
			defaultOrder: [[ 1, "asc" ]],
			selectedIdsCallback: function(selectedIds){},
			queryDataCallback: function(dataModel){},
			outletTypeId: null,
			districtId: null,
			tpuId: null,
			officerId: null,
			selectionChangedCallback: function(selectedIds){},
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
			metricSelected();
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
								"text": "OK",
								"className": "btn-danger",
								"action": function( nButton, oConfig, flash ) {
									callbackSelected(selectedIds);
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
			
			$popupTable.DataTable({
				"ordering": true, 
				"order": settings.defaultOrder,
				"searching": true,
				"buttons": buttons,
				"processing": true,
	            "serverSide": true,
	            "ajax": {
	            	url: settings.queryUrl,
	            	method: "post",
	            	data: function(dataModel) {
	            		dataModel.status = $('[name="status"]', $popupModal).val();
	            		dataModel.outletTypeId = $('[name="outletTypeId"]', $popupModal).val();
	            		dataModel.districtId = $('[name="districtId"]', $popupModal).val();
	            		dataModel.tpuId = $('[name="tpuId"]', $popupModal).val();
	            		if (settings.officerId != null)
	            			dataModel.officerId = settings.officerId;
	            		
	            		if (settings.outletTypeId != null)
	            			dataModel.outletTypeId = settings.outletTypeId;
	            		else
	            			dataModel.outletTypeId = $('[name="outletTypeId"]', $popupModal).val();
	            		
	            		if (settings.districtId != null)
	            			dataModel.districtId = settings.districtId;
	            		else
	            			dataModel.districtId = $('[name="districtId"]', $popupModal).val();
	            		
	            		if (settings.tpuId != null)
	            			dataModel.tpuId = settings.tpuId;
	            		else
	            			dataModel.tpuId = $('[name="tpuId"]', $popupModal).val();
	            		
	            		settings.queryDataCallback(dataModel);
	            	}
	            },
	            "columns": [
	                        { "data": "outletId",
	                        	"render" : function(data, type, full, meta){
	                        		var checked = selectedIds.indexOf(data) != -1;
                            		var html = '<input name="id" type="checkbox" value="' + data + '"' + (checked ? ' checked' : '') + '/>';
                            		return html;
                           		}
	                        },
	                        { "data": "referenceNo"},
	                        { "data": "batchCode"},
	                        { "data": "firm" },
	                        { "data": "district" },
	                        { "data": "tpu" },
	                        { "data": "deadline" },
	                        { "data": "address" },
	                        { "data": "noOfQuotation" },
	                        { "data": "convenientTime" },
	                        { "data": "outletRemark" }
						],
	            "columnDefs": columnDefs,
	            "drawCallback": function() {
	            	$('.select_all', $popupTable).prop('checked', false);
	            }
			});
			
			$('.filters', $popupModal).change(function(){
				refresh();
			});
			
			$('.select_all', $popupTable).click(function() {
				$('[name="id"]', $popupTable).prop('checked', $(this).prop('checked')).trigger('change');
			});
			
		    $popupTable.on('click', '.btn-select-single', function() {
			    var selectedIds = [$(this).data('id')];
			    callbackSelected(selectedIds);
			});

			$('[name="outletTypeId"]', $popupModal).select2({
				width:'100%',
				closeOnSelect: false
			});
			$('[name="districtId"]', $popupModal).select2ajax({
				width:'100%',
				closeOnSelect: false
			});
			$('[name="tpuId"]', $popupModal).select2ajax({
				width:'100%',
				ajax: {
				    data: function (params) {
				    	params.districtId = $('[name="districtId"]', $popupModal).val()
				    	return params;
					},
					method: 'GET',
					contentType: 'application/json'
				},
				closeOnSelect: false
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
		    	
		    	metricSelected();
		    });
		    metricSelected();
		    var dtBtn = $('.dt-buttons', $popupModal);
		    dtBtn.addClass('pull-right');
		    dtBtn.css('padding-left', '10px');
		    var okBtn = dtBtn.closest('div.dt-buttons', $popupModal).find('a');
		    okBtn.removeClass('btn-default');
		    var selectBtn = $('.dt-buttons', $popupModal).detach();
		    selectBtn.insertBefore($('.modal-footer', $popupModal).find('button'));
		}
		
		function callbackSelected(selectedIds) {
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
			settings.selectedIdsCallback(selectedIds);
		    $popupModal.modal('hide');
		}
		
		function metricSelected(){
	    	var model = settings.selectionChangedCallback(selectedIds);
	    	
	    	$.ajax({
				type: 'POST',
				url: "<c:url value='/commonDialog/AssignmentLookup/metricForAssignmentSelectionPopup'/>",
				data: {
					officerId : model.officerId, 
					date : model.date,
					excludedAssignmentIds : model.excludedAssignmentIds,
					excludedOutletIds : model.excludedOutletIds
				},
				async:false,
				success: function(result){
					$('#selectedAssignments', $popupModal).html(result.selectedAssignments);
					$('#selectedQuotations', $popupModal).html(result.selectedQuotations);
					$('#selectedBuildings', $popupModal).html(result.selectedBuildings);
				}
			});
		}
		
		return $input;
	};
}(jQuery));
</script>