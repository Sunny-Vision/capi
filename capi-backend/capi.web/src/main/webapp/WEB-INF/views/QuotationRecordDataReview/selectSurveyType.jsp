<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page session="false" %>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/jstree-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/unitLookup-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp"%>
	</jsp:attribute>
	<jsp:attribute name="header">
		<style>
			#unitLookup.input-group-addon:last-child {
				border-radius: 4px;
				border-left: 1px solid #d2d6de;
				width: 0%;
			}
		</style>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/jstree.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/unitLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/productLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/outletLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/moment.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker.jsp"%>
		<script>
		
		function updateRevisitDateMode($form) {
			console.log($('[name="collectionDateStr"]', $form).val());
			if ($('[name="collectionDateStr"]', $form).val() != "") {
				$('[name="startDateStr"],[name="endDateStr"]', $form).attr('disabled','disabled');
			} else {
				$('[name="startDateStr"],[name="endDateStr"]', $form).removeAttr('disabled');
			}
			
			if ($('[name="startDateStr"]', $form).val() != "" || $('[name="endDateStr"]', $form).val() != "") {
				$('[name="collectionDateStr"]', $form).attr('disabled','disabled');
			} else {
				$('[name="collectionDateStr"]', $form).removeAttr('disabled');
			}
		}
		
		$(document).ready(function(){
			var $dataTable = $("#dataList");
			var cacheLastSearch = null;
			function prepareRequestModel() {
				return cacheLastSearch;
			}
			
			var refreshCount = function () {
				var $txtTotalNoOfOutlet = $('#txtTotalNoOfOutlet');
				var $txtTotalNoOfQuotation = $('#txtTotalNoOfQuotation');
				var referenceMonthStr = $('[name="referenceMonthStr"]').val();
				var purposeId = $('[name="purposeId"]').val();
				var url = '<c:url value='/QuotationRecordDataReview/getCount'/>';
				
				$.get(url, {
					referenceMonthStr: referenceMonthStr,
					purposeId: purposeId
				}, function (data) {
					$txtTotalNoOfOutlet.text(data.totalNoOfOutlet);
					$txtTotalNoOfQuotation.text(data.totalNoOfQuotation);
				});
			};
			
			<fmt:formatDate value="${referenceMonth}" var="fmtReferenceMonthStr" type="date" pattern="yyyy-MM-dd" />
			console.log('<c:out value="${fmtReferenceMonthStr}"/>');
			var refDate = moment('<c:out value="${fmtReferenceMonthStr}"/>');
			var currDate = moment();
			var delinkPeriod = '${delinkPeriod}';
			
			if (Math.floor(currDate.diff(refDate, 'months', true)) < delinkPeriod || refDate.isAfter(currDate)){
				$('[name="delink"]').hide();
			}
			
			$(".date-picker:not([readonly])").datepicker();
			
			$('[name="collectionDateStr"],[name="startDateStr"],[name="endDateStr"]').on('change', function(e) {
				updateRevisitDateMode($(this.form));
			});
			
			$('#revisitForm').validate({
	               rules: {
	                   collectionDateStr: {
	                       required : {
	                           depends : function(element){
	                               var $form = $(element).closest("form");
	                               return $('[name="collectionDateStr"]', $form).val() == '' && 
	                                   $('[name="startDateStr"]', $form).val() == '' && $('[name="endDateStr"]', $form).val() == '';
	                           }
	                       }
	                   },
	                   startDateStr:{
	                       required : {
	                           depends : function(element){
	                               var $form = $(element).closest("form");
	                               return $('[name="collectionDateStr"]', $form).val() == '' && 
	                                   $('[name="startDateStr"]', $form).val() == '';
	                           }
	                       }
	                   },
	                   endDateStr:{
	                       required : {
	                           depends : function(element){
	                               var $form = $(element).closest("form");
	                               return $('[name="collectionDateStr"]', $form).val() == '' && 
	                                   $('[name="endDateStr"]', $form).val() == '';
	                           }
	                       }
	                   }
	               },
	               messages: {
	                   collectionDateStr: "Some Fields cannot be empty",
	                   startDateStr: "Some Fields cannot be empty",
	                   endDateStr: "Some Fields cannot be empty"
	               }
	           });
	           
	           $('#revisitAllForm').validate({
	               rules: {
	                   collectionDateStr: {
	                       required : {
	                           depends : function(element){
	                               var $form = $(element).closest("form");
	                               return $('[name="collectionDateStr"]', $form).val() == '' && 
	                                   $('[name="startDateStr"]', $form).val() == '' && $('[name="endDateStr"]', $form).val() == '';
	                           }
	                       }
	                   },
	                   startDateStr:{
	                       required : {
	                           depends : function(element){
	                               var $form = $(element).closest("form");
	                               return $('[name="collectionDateStr"]', $form).val() == '' && 
	                                   $('[name="startDateStr"]', $form).val() == '';
	                           }
	                       }
	                   },
	                   endDateStr:{
	                       required : {
	                           depends : function(element){
	                               var $form = $(element).closest("form");
	                               return $('[name="collectionDateStr"]', $form).val() == '' && 
	                                   $('[name="endDateStr"]', $form).val() == '';
	                           }
	                       }
	                   }
	               },
	               messages: {
	                   collectionDateStr: "Some Fields cannot be empty",
	                   startDateStr: "Some Fields cannot be empty",
	                   endDateStr: "Some Fields cannot be empty"
	               }
	           });
	          /*
			jQuery.validator.addMethod("revisitRequire", function(value, element) {
				var $form = $(element).closest("form");
				if($('[name="collectionDateStr"]', $form).val() == ''){
					if($('[name="startDateStr"]', $form).val() == '' || $('[name="endDateStr"]', $form).val() == ''){
						return false;
					}else{
						return true;
					}
				}else{
					return true;
				}
				}, "Some Fields cannot be empty");
			$.validator.addClassRules("revisitRequire", { revisitRequire: true });*/
			
			
			$("#revisitForm").submit( function(e){
				if($(this).valid()){
					var data = $dataTable.find(':checked').serialize();
					var form = $(this).serialize();
					data = data+"&"+form;
					bootbox.confirm({
					    title: "Confirmation",
					    message: "<spring:message code='W00027' />",
					    callback: function(result){
					    	if(result === true){
					    		//console.log(data);
					    		$.post("<c:url value='/QuotationRecordDataReview/revisitSelected'/>",
										data,
										function(response) {
											$("#dataList").DataTable().ajax.reload(null,false);
											refreshCount();
											$("#MessageRibbon").html(response);
											$('#revisitDialog').modal('hide');
										}
									);
					    	}
					    }
					});
				}
				return false;
			});
			
			$("#revisitAllForm").submit( function(e){
				var $btn = $('#revisitAllForm .btn-primary');
				if($(this).valid()){
					var form = $(this).serialize();
					data = form;
					bootbox.confirm({
					    title: "Confirmation",
					    message: "<spring:message code='W00027' />",
					    callback: function(result){
					    	if(result === true){
					    		$btn.button('loading');
					    		//console.log(data);
					    		var d = prepareRequestModel();
								$.post("<c:url value='/QuotationRecordDataReview/fetchIdList'/>", d)
								.then(function() {
						    		$.post("<c:url value='/QuotationRecordDataReview/revisitAll'/>",
											data,
											function(response) {
												$("#dataList").DataTable().ajax.reload(null,false);
												refreshCount();
												$("#MessageRibbon").html(response);
												$btn.button('reset');
												$('#revisitAllDialog').modal('hide');
											}
										);
								});
					    	}
					    }
					});
				}
				return false;
			});
			
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
			
			var buttons = [{
				"text": "Flag",
				"action": function( nButton, oConfig, flash ) {
					var data = $dataTable.find(':checked').serialize();
					if (data == '') {
						bootbox.alert({
							title: "Alert",
							message: "<spring:message code='E00009' />"
						});
						//alert('<spring:message code="E00009" />');
						return;
					}
					data = data+"&flag=true";
					
					$.post("<c:url value='/QuotationRecordDataReview/flag'/>",
							data,
							function(response) {
								$("#dataList").DataTable().ajax.reload(null,false);
								refreshCount();
								$("#MessageRibbon").html(response);
							}
						);
				}
			},{
				"text": "UnFlag",
				"action": function( nButton, oConfig, flash ) {
					var data = $dataTable.find(':checked').serialize();
					if (data == '') {
						bootbox.alert({
							title: "Alert",
							message: "<spring:message code='E00009' />"
						});
						//alert('<spring:message code="E00009" />');
						return;
					}
					data = data+"&flag=false";
					
					$.post("<c:url value='/QuotationRecordDataReview/flag'/>",
							data,
							function(response) {
								$("#dataList").DataTable().ajax.reload(null,false);
								refreshCount();
								$("#MessageRibbon").html(response);
							}
						);
				}
			},{
				"text": "Reallocate Selected",
				"action": function( nButton, oConfig, flash ) {
					var data = $dataTable.find(':checked').serialize();
					if (data == '') {
						bootbox.alert({
							title: "Alert",
							message: "<spring:message code='E00009' />"
						});
						//alert('<spring:message code="E00009" />');
						return;
					}
					var total = $dataTable.find(':checked').not("#chkAll").length;
					
					bootbox.confirm({
					    title: "Confirmation",
					    message: "<spring:message code='W00025' />".replace('{0}', total),
					    callback: function(result){
					    	if(result === true){
					    		$.post("<c:url value='/QuotationRecordDataReview/reallocateIds'/>",
										data,
										function(response) {
											$("#dataList").DataTable().ajax.reload(null,false);
											refreshCount();
											$("#MessageRibbon").html(response);
										}
									);
					    	}
					    }
					});
				}
			},{
				"text": "Reallocate All",
				"action": function( nButton, oConfig, flash ) {
					var $btn = $(nButton.currentTarget);
					var total = $dataTable.DataTable().page.info().recordsDisplay; // with free text search filter
					bootbox.confirm({
					    title: "Confirmation",
					    message: "<spring:message code='W00026' />".replace('{0}', total),
					    callback: function(result){
					    	if(result === true){
					    		$btn.button('loading');
					    		
					    		var d = prepareRequestModel();

								$.post("<c:url value='/QuotationRecordDataReview/fetchIdList'/>", d)
								.then(function() {
						    		$.post("<c:url value='/QuotationRecordDataReview/reallocateAll'/>",
										function(response) {
											$("#dataList").DataTable().ajax.reload(null,false);
											refreshCount();
											$("#MessageRibbon").html(response);
											$btn.button('reset');
										}
									);
								});
					    	}
					    }
					});
				}
			},{
				"text": "Revisit Selected",
				"action": function( nButton, oConfig, flash ) {
					var data = $dataTable.find(':checked').serialize();
					if (data == '') {
						bootbox.alert({
							title: "Alert",
							message: "<spring:message code='E00009' />"
						});
						//alert('<spring:message code="E00009" />');
						return;
					}
					var total = $dataTable.find(':checked').not("#chkAll").length;
					$('#revisitDialog .total').html(total);
					$('#revisitDialog').modal('show');
					$("#revisitForm", $('#revisitDialog')).validate({ ignore: [] });
				}
			},{
				"text": "Revisit All",
				"action": function( nButton, oConfig, flash ) {
					var total = $dataTable.DataTable().page.info().recordsDisplay; // with free text search filter
					$('#revisitAllDialog .total').html(total);
					$('#revisitAllDialog').modal('show');
					$('#revisitAllForm', $('#revisitAllDialog')).validate({ ignore: [] });
				}
			},]
			
			$.fn.dataTable.addResponsiveButtons(buttons);
			
			var table = $dataTable.DataTable({
				"order": [[ 1, "desc" ]],
				"searching": true,
				"ordering": true,				
				"buttons": buttons,
				"processing": true,
                "serverSide": true,
                "ajax": {
                	url: "<c:url value='/QuotationRecordDataReview/query'/>",
                	data: function(d) {
                		d.search["purposeId"] = $('[name="purposeId"]').val();
                		d.search["referenceMonthStr"] = $('[name="referenceMonthStr"]').val();
                		d.search["unitId"] = $('[name="unitId"]').val() == null ? "" : $('[name="unitId"]').val().join();
                		d.search["subGroupId"] = $('[name="subGroupId"]').val();
                		d.search["outletId"] = $('[name="outletId"]').val();
                		d.search["outletTypeId"] = $('[name="outletTypeId"]').val() == null ? "" : $('[name="outletTypeId"]').val().join();
                		d.search["outletCategory"] = $('[name="outletCategory"]').val();
                		d.search["subPrice"] = $('[name="subPrice"]').val();
                		d.search["priceRemark"] = $('[name="priceRemark"]').val();
                		d.search["seasonalItem"] = $('[name="seasonalItem"]').val();
                		d.search["outletCategoryRemark"] = $('[name="outletCategoryRemark"]').val();
                		d.search["surveyForm"] = $('[name="surveyForm"]').val();
                		d.search["greaterThan"] = $('[name="greaterThan"]').val();
                		d.search["lessThan"] = $('[name="lessThan"]').val();
                		d.search["equal"] = $('[name="equal"]').val();
                		d.search["withPriceReason"] = $('[name="withPriceReason"]').val();
                		d.search["withProductRemark"] = $('[name="withProductRemark"]').val();
                		d.search["withDiscountRemark"] = $('[name="withDiscountRemark"]').val();
                		d.search["allocatedIndoorOfficer"] = $('[name="allocatedIndoorOfficer"]').val();
                		d.search["withOtherRemark"] = $('[name="withOtherRemark"]').val();
                		d.search["withFieldwork"] = $('[name="withFieldwork"]').val();
                		d.search["isPRNull"] = $('[name="isPRNull"]').val();
                		d.search["withIndoorConversionRemarks"] = $('[name="withIndoorConversionRemarks"]').val();
                		d.search["referenceDateCrit"] = $('[name="referenceDateCrit"]').val();
                		d.search["availability"] = $('[name="availability"]').val();
                		d.search["firmStatus"] = $('[name="firmStatus"]').val();
                		d.search["quotationId"] = $('[name="quotationId"]').val();
                		d.search["withDiscount"] = $('[name="withDiscount"]').val();
                		cacheLastSearch = d;
                	},
                	method: 'post'
                },
                "columns": [
                            { "data": "indoorQuotationRecordId",
			                	"render" : function(data, type, full, meta){
			                		var html = '<input name="id" value="'+data+'" type="checkbox" class="tblChk"]>';
			                		return html;
			                		},
			                	"orderable" : false
			                },
			                { "data": "referenceDate" },
                            { "data": "indoorQuotationRecordId" },
                            { "data": "quotationId" },
                            { "data": "isFlag",
                            	"render" : function(data, type, full, meta){
                            		//console.log(data)
                            		var html = data ? '<span class="glyphicon glyphicon-star" style="color:rgb(255, 204, 0)">&nbsp;</span>' : "";
                            		return html;
                            		} },
                            { "data": "subGroupEnglishName" },
                            { "data": "subGroupChineseName" },
                            { "data": "unitEnglishName" },
                            { "data": "unitChineseName" },
                            { "data": "editedCurrentSPrice" },
                            { "data": "editedPreviousSPrice" },
                            { "data": "pr",
                            	"render" : function(data, type, full, meta){
                            		if(data!=null)
                            			return data.toFixed(3);
                            		return data;
                            	}
                            },
                            { "data": "outletType" },
                            { "data": "outletName" },
                            { "data": "productAttribute1" },
                            { "data": "subPrice",
                            	"render" : function(data, type, full, meta){
                            		if(data){
                            			html = "<span class='glyphicon glyphicon-ok' aria-hidden='true'>";
                            		}else{
                            			html = "<span class='glyphicon glyphicon-remove' aria-hidden='true'>";
                            		}
                            		return html;
                            	}
                            },
                            { "data": "seasonalItem" },
                            { "data": "indoorRemark" },
                            { "data": "outletDiscountRemark" },
                            { "data": "priceRemarks" },
                            { "data": "otherRemarks" },
                            { "data": "discountRemark" },
                            { "data": "unitCategory" },
                            //{ "data": "quotationRecordStatus" },
                            { "data": "productRemarks" },
                            { "data": "indoorQuotationRecordId",
                            	"render" : function(data, type, full, meta){
                            		var html = "<a href='<c:url value='/QuotationRecordDataReview/edit?id='/>" + data + "' class='table-btn btn-edit'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a>";
                            		return html;
                        	}}
                        ],
                "columnDefs": columnDefs,
                "drawCallback": function() {
    				$(".tblChk").click(function (evt){
    					evt.stopPropagation();
    				})
    				$("#chkAll").click(function (evt){
    					evt.stopPropagation();
    				})
	            }
			});
			
			$('.filters').change(function(){
				$dataTable.DataTable().ajax.reload();
			});
			
			$('.select2ajax').select2ajax({
				allowClear: true,
				placeholder: '',
				width:"100%"
			});
			
			$('.select2ajax').hide();
			
			$('.ref-date-picker').datepicker({
				clearBtn: true
			}).on('changeDate', function(){
				$dataTable.DataTable().ajax.reload();
			});
			
			$("#chkAll").on('change', function(){
	    		if (this.checked){
	    			$('.tblChk').prop('checked', true);
	    		}
	    		else{
	    			$('.tblChk').prop('checked', false);
	    		}
	    	});
			
			$('#unitId,.searchUnitId').unitLookup({
				selectedIdsCallback: function(selectedIds) {
					var singleUrl = $('#unitId').data('singleUrl');
					var $select = $('#unitId');
					$('#unitId').empty();
					
					if(selectedIds.length == 0) {
						$("#unitLookup.input-group-addon:last-child").css("color", "#555");
						$("#unitLookup.input-group-addon:last-child").css("background-color", "");
						$("#unitLookup.input-group-addon:last-child").css("border-color", "#d2d6de");
						$dataTable.DataTable().ajax.reload();
						return;
					}
					
					/*$.post(singleUrl, { id: selectedIds }, 
						function(data) {
							$select.empty();
							for (var i = 0; i < data.length; i++) {
								var option = new Option(data[i].value, data[i].key);
								option.selected = true;
								$select.append(option);
							}
							$select.trigger('change');
						});*/
					for (var i = 0; i < selectedIds.length; i++) {
						var option = new Option(selectedIds[i], selectedIds[i]);
						option.selected = true;
						$select.append(option);
					}
					$select.trigger('change');
					
					$("#unitLookup.input-group-addon:last-child").css("color", "#ffffff");
					$("#unitLookup.input-group-addon:last-child").css("background-color", "#f0ad4e");
					$("#unitLookup.input-group-addon:last-child").css("border-color", "#eea236");
				},
				multiple: true
			});
			$('#unitId').hide();
			
			$('#outletId,.searchOutletId').outletLookup({
				selectedIdsCallback: function(selectedIds) {
					var singleUrl = $('#outletId').data('singleUrl');
					$('#outletId').empty();
					$.post(singleUrl, { id: selectedIds[0] }, 
						function(data) {
						var option = new Option(data, selectedIds[0]);
						option.selected = true;
						$('#outletId').append(option);
						$('#outletId').trigger('change');
					});
				},
				multiple: false
			});
			
			$('[name="delink"]').on("click", function(){
				bootbox.confirm({
				    title: "Confirmation",
				    message: "<spring:message code='W00030' />",
				    callback: function(result){
				    	if(result === true){
				    		normalFormPost("<c:url value='/QuotationRecordDataReview/delink'/>", { refMonthStr: $('[name="referenceMonthStr"]').val() });
				    	}
				    }
				});
			});
			
			$('#btnReset').on('click', function() {
				console.log('Reset Button clicked.');
				
				$('.criteria').select2ajax('destroy');
				
				$('.criteria').find("option").remove();
				$('[name="unitId"]').find("option").remove();
				$('[type="text"]').val("");
				$('.select2').val("");
				
				$('.criteria').select2ajax({
					width: "100%"
				});
				
				var $jstree = $('div.jstree:hidden');
				$jstree.jstree('deselect_all');
				$jstree.jstree('close_all');
				$("#unitLookup.input-group-addon:last-child").css("color", "#555");
				$("#unitLookup.input-group-addon:last-child").css("background-color", "");
				$("#unitLookup.input-group-addon:last-child").css("border-color", "#d2d6de");
				
				$dataTable.DataTable().ajax.reload();
			});
			
			refreshCount();
		});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Quotation Record Data Review</h1>
        </section>
        
        <section class="content">
        	<div class="row">
				<div class="col-md-12">
				<!-- content -->
					<!-- Quotation Table -->
					<div class="box" >
					<div class="box-header with-border">
							<a class="btn btn-default btn-back" href="<c:url value="/QuotationRecordDataReview/home"/>">Back</a>							
						</div>
						<div class="box-body">
							<form class="form-horizontal" id="filterForm">
								<div class="row">
									
									<label class="col-md-1 control-label">Outlet Type</label>
									<div class="col-md-2">
										<select name="outletTypeId" class="form-control select2ajax filters criteria" id="outletTypeId"  data-close-on-select="false" 
											multiple
											data-ajax-url="<c:url value='/QuotationRecordDataReview/queryOutletTypeSelect2'/>"></select>
									</div>
									<label class="col-md-1 control-label">Variety Category</label>
									<div class="col-md-2">
										<select name="outletCategory" class="form-control select2ajax filters criteria" id="outletCategory"
											data-ajax-url="<c:url value='/QuotationRecordDataReview/queryValidDistinctUnitCategorySelect2'/>"></select>
									</div>
									<label class="col-md-1 control-label">Outlet</label>
									<div class="col-md-2">
	   									<div class="input-group">
	   										<select name="outletId" class="form-control select2ajax filters criteria" id="outletId"
											data-ajax-url="<c:url value='/dataConversion/QuotationRecordDataConversion/queryOutletSelect2'/>"
											data-single-url="<c:url value='/QuotationRecordDataReview/queryOutletSelectSingle'/>"></select>	
											<div class="input-group-addon searchOutletId">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								<div class="row" style="margin-top: 10px;">
							
									<label class="col-md-1 control-label">Variety</label>
									<div class="col-md-2">
	   									<div class="input-group">
	   										<select name="unitId" class="form-control filters" id="unitId" multiple
	   										data-close-on-select="false"
	   										data-allow-clear="true"
											data-placeholder=""
											data-ajax-url="<c:url value='/QuotationRecordDataReview/queryUnitSelect2'/>"
											data-single-url="<c:url value='/QuotationRecordDataReview/queryUnitSelectSingle'/>"></select>	
											<div id="unitLookup" class="input-group-addon searchUnitId">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
									
									<label class="col-md-1 control-label">Sub Price</label>
									<div class="col-md-1">
										<select name="subPrice" class="form-control select2 filters" id="subPrice" >
											<option value=""> </option>
											<option value="1">Exist</option>
											<option value="0">Non-exist</option>
										</select>								
									</div>
									<label class="col-md-1 control-label">Price Remark</label>
									<div class="col-md-1">
										<select name="priceRemark" class="form-control select2 filters" id="priceRemark" >
											<option value=""> </option>
											<option value="1">Exist</option>
											<option value="0">Non-exist</option>
										</select>								
									</div>
									<label class="col-md-1 control-label">Seasonal Item</label>
									<div class="col-md-1">
										<select name="seasonalItem" class="form-control select2 filters" id="seasonalItem" >
											<option value=""> </option>
											<option value="1">All-time</option>
											<option value="2">Summer</option>
											<option value="3">Winter</option>
											<option value="4">Occasional</option>
										</select>								
									</div>
									<label class="col-md-2 control-label">Outlet Category Remark</label>
									<div class="col-md-1">
										<select name="outletCategoryRemark" class="form-control select2 filters" id="outletCategoryRemark" >
											<option value=""> </option>
											<option value="1">Exist</option>
											<option value="0">non-Exist</option>
										</select>								
									</div>
								</div>
								
								<div class="row" style="margin-top: 10px;">
									<label class="col-md-2 control-label">Edited Price Change (%):</label>
										<div class="col-md-5">
											<div class="input-group">
												<div class="input-group-addon">
													&gt;
												</div>
												<input type="text" class="form-control filters"	name="greaterThan" value="" />
												<div class="input-group-addon">
													Or
												</div>
												<div class="input-group-addon">
													&lt;
												</div>
												<input type="text" class="form-control filters"	name="lessThan" value="" />
												<div class="input-group-addon">
													Or
												</div>
												<div class="input-group-addon">
													=
												</div>
												<input type="text" class="form-control filters"	name="equal" value="" />
											</div>
										</div>
								 	<label class="col-md-1 control-label">Survey Form</label>
									<div class="col-md-2">
										<select name="surveyForm" class="form-control select2 filters" id="surveyForm" >
											<option value=""> </option>
											<c:forEach items="${surveyForms}" var="surveyForm">
												<option value="<c:out value='${surveyForm}' />"><c:out value='${surveyForm}' /></option>
											</c:forEach>
										</select>
									</div>
								</div>
								<div class="row" style="margin-top: 10px;">
									<label class="col-md-2 control-label">With Outlet Discount Remark</label>
									<div class="col-md-2">
										<select name="withOtherRemark" class="form-control select2 filters" id="withOtherRemark" >
											<option value=""> </option>
											<option value="1">Exists</option>
											<option value="0">Not Exists</option>
										</select>
									</div>
									<label class="col-md-2 control-label">With Product Remark</label>
									<div class="col-md-2">
										<select name="withProductRemark" class="form-control select2 filters" id="withProductRemark" >
											<option value=""> </option>
											<option value="1">Exists</option>
											<option value="0">Not Exists</option>
										</select>
									</div>
									<label class="col-md-2 control-label">With Discount Calculator Remark</label>
									<div class="col-md-2">
										<select name="withDiscountRemark" class="form-control select2 filters" id="withDiscountRemark" >
											<option value=""> </option>
											<option value="1">Exists</option>
											<option value="0">Not Exists</option>
										</select>
									</div>
								</div>
								<div class="row" style="margin-top: 10px;">
									<label class="col-md-2 control-label">With Price Reason</label>
									<div class="col-md-2">
										<select name="withPriceReason" class="form-control select2 filters" id="withPriceReason" >
											<option value=""> </option>
											<option value="1">Exists</option>
											<option value="0">Not Exists</option>
										</select>
									</div>
									<label class="col-md-2 control-label">Allocated Indoor Officer</label>
									<div class="col-md-2">
										<select name="allocatedIndoorOfficer" class="form-control select2 filters" id="allocatedIndoorOfficer" >
											<option value=""> </option>											
											<option value="0">Not Exists</option>
											<c:forEach items="${users}" var="user">
											   <option value="${user.userId }"><c:out value="${user.staffCode}"/> - <c:out value="${user.chineseName}"/></option>
											</c:forEach>
											
										</select>
									</div>
									<label class="col-md-2 control-label">Whether fieldwork has been conducted</label>
									<div class="col-md-2">
										<select name="withFieldwork" class="form-control select2 filters" id="withFieldwork" >
											<option value=""> </option>
											<option value="1">Y</option>
											<option value="0">N</option>
										</select>
									</div>
								</div>
								<div class="row" style="margin-top: 10px;">
									<label class="col-md-2 control-label">PR=null</label>
									<div class="col-md-2">
										<select name="isPRNull" class="form-control select2 filters" id="isPRNull" >
											<option value=""> </option>
											<option value="1">Y</option>
											<option value="0">N</option>
										</select>
									</div>
									<label class="col-md-2 control-label">With indoor conversion remarks</label>
									<div class="col-md-2">
										<select name="withIndoorConversionRemarks" class="form-control select2 filters" id="withIndoorConversionRemarks" >
											<option value=""> </option>
											<option value="1">Exists</option>
											<option value="0">Not Exists</option>
										</select>
									</div>
									<label class="col-md-2 control-label">Reference Date</label>
									<div class="col-md-2">
										<div class="input-group">
											<input type="text" name="referenceDateCrit" class="form-control ref-date-picker" id = "referenceDateCrit" >
											<div class="input-group-addon">
												<i class="fa fa-calendar"></i>
											</div>
										</div>
									</div>
								</div>
								<div class="row" style="margin-top: 10px;">
									<label class="col-md-2 control-label">Availability</label>
									<div class="col-md-2">
										<select name="availability" class="form-control select2 filters" id="availability" >
											<option value=""> </option>
											<option value="1">Available</option>
<!-- 											<option value="2"></option> -->
<!-- 											<option value="3">有價無貨</option> -->
											<option value="4">缺貨</option>
											<option value="5">Not Suitable</option>
											<option value="6">回倉</option>
											<option value="7">無團</option>
											<option value="8">未返</option>
											<option value="9">New</option>
										</select>
									</div>
									<label class="col-md-2 control-label">Firm status</label>
									<div class="col-md-2">
										<select name="firmStatus" class="form-control select2 filters" id="firmStatus">
											<option value=""> </option>
											<option value="1">Enumeration (EN)</option>
											<option value="2">Closed (CL)</option>
											<option value="3">Move (MV)</option>
											<option value="4">Not Suitable (NS)</option>
											<option value="5">Refusal (NR)</option>
											<option value="6">Wrong Outlet (WO)</option>
											<option value="7">Door Lock (DL)</option>
											<option value="8">Non-contact (NC)</option>
											<option value="9">In Progress (IP)</option>
											<option value="10">Duplication (DU)</option>
										</select>
									</div>
									<label class="col-md-2 control-label">Quotation Id</label>
									<div class="col-md-2">
										<select name="quotationId" class="form-control select2ajax filters criteria" id="quotationId" 
											data-ajax-url="<c:url value='/QuotationRecordDataReview/queryQuotationIdSelect2'/>"></select>
									</div>
								</div>
								<div class="row" style="margin-top: 10px;">
									<label class="col-md-2 control-label">Discount</label>
									<div class="col-md-2">
										<select name="withDiscount" class="form-control select2 filters" id="withDiscount" >
											<option value=""> </option>
											<option value="1">Exists</option>
											<option value="0">Not Exists</option>
										</select>
									</div>
								</div>
								<div class="row" style="margin-top: 10px;">
									<div class="col-md-offset-2 col-md-2">
										<button id="btnReset" type="button" class="btn btn-info" <%--onclick="$('#dataList').DataTable().ajax.reload();"--%>>Clear</button>
										<%-- updated 2020-10-14: CR12 removed authority levels: 64 (indoor review), 256 (business data administrator), on user request (Ken) --%>
										<sec:authorize access="hasPermission(#user, 1024)">
										<button type="button" class="btn btn-info" name="delink">Delink</button>
										</sec:authorize>
									</div>
									<div class="col-md-offset-6 col-md-2">
										<span>
											Reference Month: ${referenceMonthStr}<br/>
											Purpose: ${purpose.name}<br/>
											Total no. of Outlets: <span id="txtTotalNoOfOutlet"></span><br/>
											Total no. of Quotations: <span id="txtTotalNoOfQuotation"></span>
										</span>
									</div>
								</div>
							</form>
							<input type="hidden" name="referenceMonthStr" value="${referenceMonthStr}" readonly disabled/>
							<input type="hidden" name="purposeId" value="${purpose.purposeId}" readonly disabled/>
							<hr/>
							<table class="table table-striped table-bordered table-hover responsive" id="dataList">
								<thead>
								<tr>
									<th><input type="checkbox" id="chkAll" /></th>
									<th>Reference Date</th>
									<th>Indoor Quotation Record ID</th>
									<th>Quotation ID</th>
									<th class="text-center">Flag</th>
									<th>Sub-Group English Name</th>
									<th>Sub-Group Chinese Name</th>
									<th>Variety English Name</th>
									<th>Variety Chinese Name</th>
									<th>Edited Current S Price</th>
									<th>Edited Previous S Price</th>
									<th>PR</th>
									<th>Outlet Type</th>
									<th>Outlet Name</th>
									<th>Product Attribute 1</th>
									<th>Sub Price</th>
									<th>Seasonal Item</th>
									<th>Indoor Remark</th>
									<th>Outlet Discount Remark</th>
									<th>Field Price Reason</th>
									<th>Field Price Remark</th>
									<th>Field Discount Remark</th>
									<th>Unit Category Remark</th>
									<!--<th>Quotation Record Status</th>-->
									<th>Product Remarks</th>
									<th class="text-center action" data-priority="1"></th>
								</tr>
								</thead>
							</table>
						</div>
					</div>
				</div>
			</div>
			
			<div class="modal fade" id="revisitDialog" tabindex="-1" role="dialog" aria-labelledby="dialogLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content">
						<div  class="clearfix overlay" data-bind="loading:isLoading, visible:isLoading" ></div>
						<form action="<c:url value='/QuotationRecordDataReview/revisit'/>" method="post" id="revisitForm" >
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
								<h4 class="modal-title" id="dialogLabel" data-bind="text:formTitle">Revisit</h4>
							</div>
							<div class="modal-body form-horizontal">
								<div class="form-group">
									<div class="col-md-4 control-label">Total</div>
									<div class="col-md-6">
										<p class="form-control-static total"></p>
									</div>
								</div>
								<div class="form-group">
									<div class="col-md-4 control-label">Start Date</div>
									<div class="col-md-6">
										<div class="input-group date date-picker">
											<input name="startDateStr" class="form-control revisitRequire"/>
											<div class="input-group-addon">
												<i class="fa fa-calendar"></i>
											</div>
										</div>
									</div>
								</div>
								<div class="form-group">
									<div class="col-md-4 control-label">End Date</div>
									<div class="col-md-6">
										<div class="input-group date date-picker">
											<input name="endDateStr" class="form-control revisitRequire"/>
											<div class="input-group-addon">
												<i class="fa fa-calendar"></i>
											</div>
										</div>
									</div>
								</div>
								<div class="form-group">
									<div class="col-md-4 control-label">Collection Date</div>
									<div class="col-md-6">
										<div class="input-group date date-picker">
											<input name="collectionDateStr" class="form-control revisitRequire"/>
											<div class="input-group-addon">
												<i class="fa fa-calendar"></i>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
								<button type="submit" class="btn btn-primary" data-loading-text="Loading...">Submit</button>
							</div>
						</form>
					</div>
				</div>
			</div>
			
			<div class="modal fade" id="revisitAllDialog" tabindex="-1" role="dialog" aria-labelledby="dialogLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content">
						<div  class="clearfix overlay" data-bind="loading:isLoading, visible:isLoading" ></div>
						<form action="<c:url value='/QuotationRecordDataReview/revisitAll'/>" method="post" id="revisitAllForm" >
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
								<h4 class="modal-title" id="dialogLabel" data-bind="text:formTitle">Revisit</h4>
							</div>
							<div class="modal-body form-horizontal">
								<div class="form-group">
									<div class="col-md-4 control-label">Total</div>
									<div class="col-md-6">
										<p class="form-control-static total"></p>
									</div>
								</div>
								<div class="form-group">
									<div class="col-md-4 control-label">Start Date</div>
									<div class="col-md-6">
										<div class="input-group date date-picker">
											<input name="startDateStr" class="form-control revisitRequire"/>
											<div class="input-group-addon">
												<i class="fa fa-calendar"></i>
											</div>
										</div>
									</div>
								</div>
								<div class="form-group">
									<div class="col-md-4 control-label">End Date</div>
									<div class="col-md-6">
										<div class="input-group date date-picker">
											<input name="endDateStr" class="form-control revisitRequire"/>
											<div class="input-group-addon">
												<i class="fa fa-calendar"></i>
											</div>
										</div>
									</div>
								</div>
								<div class="form-group">
									<div class="col-md-4 control-label">Collection Date</div>
									<div class="col-md-6">
										<div class="input-group date date-picker">
											<input name="collectionDateStr" class="form-control revisitRequire"/>
											<div class="input-group-addon">
												<i class="fa fa-calendar"></i>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
								<button type="submit" class="btn btn-primary" data-loading-text="Loading...">Submit</button>
							</div>
						</form>
					</div>
				</div>
			</div>
        </section>
	</jsp:body>
</t:layout>

		