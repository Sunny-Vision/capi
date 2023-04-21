<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/jstree-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/unitLookup-css.jsp" %>
	</jsp:attribute>
	<jsp:attribute name="header">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/jstree.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/unitLookup.jsp" %>
		<style>
			#dataList .discount span {
				display: inline-block;
				margin: 0 2px;
			}
			#dataList .discount span.number {
				border: solid 1px #d2d6de;
				padding: 0 20px;
			}
			.input-group-addon:last-child {
				border-radius: 4px;
				border-left: 1px solid #d2d6de;
				width: 0%;
			}
		</style>
		<script>
			$(document).ready(function(){
				var $dataTable = $("#dataList");

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
				
				var buttons = [];				
				<sec:authorize access="hasPermission(#user, 256)">
					buttons.push({
						"text": "Add",
						"action": function( nButton, oConfig, flash ) {
							window.location = "<c:url value='/masterMaintenance/UnitMaintenance/edit'/>";
						}
					});
				</sec:authorize>
				$.fn.dataTable.addResponsiveButtons(buttons);
				
				var table = $dataTable.DataTable({
					"order": [[ 0, "asc" ]],
					"searching": true,
					"ordering": true,
					"buttons": buttons,
					"processing": true,
	                "serverSide": true,
	                "autoWidth": false,
	                "ajax": {
	                	url: "<c:url value='/masterMaintenance/UnitMaintenance/query'/>",
	                	data: function(d) {
	                		var formData = ['surveyTypeId','sectionId','groupId','subGroupId','itemId','outletTypeId','subItemId','productCategoryId','cpiBasePeriod' ];
	                		for (i = 0; i < formData.length; i++){
	                			var selectedValues = [];
	                			$('[name="'+formData[i]+'"] option:selected').each(function(){
	                				selectedValues.push(this.value);
	                			})
	                			d[formData[i]] = selectedValues.join(',');
	                		}	                		
	                		
	                		d.status = $('[name="status"]').val();
	                	},
	                	method: 'post'
	                },
	                "columns": [
	                            { "data": "unitCode" },
	                            { 
	                            	"data": "unitEnglishName",
	                            	"render" : function(data, type, full, meta){
	                            		//return "<span title='"+full.unitEnglishName+" "+full.unitChineseName+"'>"+data+"</span>";
	                            		
	                            		if(data == null) {
	                            			return "<span title='"+full.unitEnglishName+" "+full.unitChineseName+"'></span>";
	                            		} else {
	                            			return "<span title='"+full.unitEnglishName+" "+full.unitChineseName+"'>"+data+"</span>";
	                            		}
                            		}
	                            },
	                            { "data": "cpiBasePeriod" },
	                            { "data": "surveyType" },
	                            { "data": "sectionEnglishName" },
	                            { "data": "groupEnglishName" },
	                            { "data": "subGroupEnglishName" },
	                            { "data": "itemEnglishName" },
	                            { "data": "outletTypeEnglishName" },
	                            { "data": "subItemEnglishName" },
	                            { "data": "productCategory" },
	                            { "data": "status" },
	                            { "data": "sectionCode" },
	                            { "data": "sectionChineseName" },
	                            { "data": "groupCode" },
	                            /*{ 
	                            	"data": "groupEnglishName",
	                            	"render" : function(data, type, full, meta){
	                            		return "<span title='"+full.groupEnglishName+" "+full.groupChineseName+"'>"+data+"</span>";
                            		}
	                            	
	                            },*/
	                            { "data": "groupChineseName" },
	                            { "data": "subGroupCode" },
	                            /*{ 
	                            	"data": "subGroupEnglishName",
	                            	"render" : function(data, type, full, meta){
	                            		return "<span title='"+full.subGroupEnglishName+" "+full.subGroupChineseName+"'>"+data+"</span>";
                            		}
	                            },*/
	                            { "data": "subGroupChineseName" },
	                            { "data": "itemCode" },
	                            { 
	                            	"data": "itemChineseName",
	                            	"render" : function(data, type, full, meta){
	                            		return "<span title='"+full.itemEnglishName+" "+full.itemChineseName+"'>"+data+"</span>";
                            		}
	                            },
	                            { "data": "outletTypeCode" },
	                            /*{ 
	                            	"data": "outletTypeEnglishName",
	                            	"render" : function(data, type, full, meta){
	                            		return "<span title='"+full.outletTypeEnglishName+" "+full.outletTypeChineseName+"'>"+data+"</span>";
                            		}
	                            },*/
	                            { "data": "outletTypeChineseName" },
	                            { "data": "subItemCode" },
	                            { "data": "subItemChineseName" },
	                            { "data": "unitChineseName" },
	                            {
	                            	"data": "id",
	                            	"render" : function(data, type, full, meta){
	                            		var html = "<a href='<c:url value='/masterMaintenance/UnitMaintenance/edit?id='/>"+data+"' class='table-btn'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a>";
	                            		return html;
                            		}
                            	}
	                        ],
	                "columnDefs": columnDefs
				});
				
				
				$('.filters').change(function(){
					$dataTable.DataTable().ajax.reload();
				});
				
				$('.select2ajax').select2ajax({
					allowClear: true,
					placeholder: '',
					width:'100%',
					multiple: true,
					closeOnSelect: false
				});
				
				$('.select2ajax').hide();
				
				
				$('.lookup').each(function() {
					var bottomEntityClass = $(this).data('bottomEntityClass');
					var $select = $(this).closest('.input-group').find('select');
					var singleUrl = $select.data('singleUrl');
					$(this).unitLookup({
						selectedIdsCallback: function(selectedIds) {
							$select.empty();
							
							if(selectedIds.length == 0) {
								$(".input-group-addon:last-child").css("color", "#555");
								$(".input-group-addon:last-child").css("background-color", "");
								$(".input-group-addon:last-child").css("border-color", "#d2d6de");
								$dataTable.DataTable().ajax.reload();
								return;
							}
							
							/*$.ajax({
								url:singleUrl,
								data: { ids: selectedIds },
								method: "post",
								traditional: true,
								success:function(data){
									for (i=0; i< data.length; i++){
										var option = new Option(data[i].code +" - "+data[i].chineseName, data[i].id);
										option.selected = true;
										$select.append(option);
									}

									$select.trigger('change');
								}
							})*/
							/*
							$.get(singleUrl, { id: selectedIds }, function(data) {
								for (i=0; i< data.length; i++){
									var option = new Option(data[i].code +" - "+data[i].chineseName, data[i].id);
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
							
							$(".input-group-addon:last-child").css("color", "#ffffff");
							$(".input-group-addon:last-child").css("background-color", "#f0ad4e");
							$(".input-group-addon:last-child").css("border-color", "#eea236");
						},
						multiple: true,
						bottomEntityClass: bottomEntityClass
					});
					$select.hide();
				});
				
				$("#collapseBtn").click(function(){
					$("#filters").toggle();
					var icon = $(this).find('i');
					if (icon.hasClass('fa-minus')){
						icon.removeClass('fa-minus').addClass('fa-plus')
					}else{
						icon.removeClass('fa-plus').addClass('fa-minus')
					}
				});
				
				/*
				$.AdminLTE.menuToggleCallback = function(state){
					//table.destroy();
					//table.init();
					table.columns.adjust()
					table.responsive.recalc();
					//table.responsive.rebuild();
				}*/
				
				$('button[type="button"]').on('click', function() {
					console.log('Reset Button clicked.');
					
					$('.criteria').select2ajax('destroy');
					
					$('.criteria').find("option").remove();
					$('[name="subItemId"]').find("option").remove();
					$('[name="status"]').val("");
					
					$('.criteria').select2ajax({
						width: "100%"
					});
					
					var $jstree = $('div.jstree:hidden');
					$jstree.jstree('deselect_all');
					$jstree.jstree('close_all');
					$(".input-group-addon:last-child").css("color", "#555");
					$(".input-group-addon:last-child").css("background-color", "");
					$(".input-group-addon:last-child").css("border-color", "#d2d6de");
					
					$dataTable.DataTable().ajax.reload();
				});
				
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Variety Maintenance</h1>
        </section>
        
        <section class="content">
        	<div class="row">
				<div class="col-md-12">
				<!-- content -->
					<!-- Itinerary Planning Table -->
					<div class="box" >
						<div class="box-header with-border">
							&nbsp;
			              <div class="box-tools pull-right">
			                <button id="collapseBtn" class="btn btn-box-tool" type="button"><i class="fa fa-minus"></i></button>
			              </div>
			            </div>
						<div class="box-body">
							<div id="filters">
								<form class="form-horizontal">
									<div class="row">
										<label class="col-md-1 control-label">Purpose</label>
										<div class="col-md-2">
											<select name="surveyTypeId" class="form-control select2ajax filters criteria" multiple
												data-ajax-url="<c:url value='/masterMaintenance/UnitMaintenance/queryPurposeSelect2'/>"></select>
										</div>
										<%--
										<label class="col-md-1 control-label">Section</label>
										<div class="col-md-2">
											<select name="sectionId" class="form-control select2ajax filters"
												data-ajax-url="<c:url value='/masterMaintenance/UnitMaintenance/querySectionSelect2'/>"></select>
										</div>
										<label class="col-md-1 control-label">Group</label>
										<div class="col-md-2">
											<select name="groupId" class="form-control select2ajax filters"
												data-ajax-url="<c:url value='/masterMaintenance/UnitMaintenance/queryGroupSelect2'/>"></select>
										</div>
										<label class="col-md-1 control-label">Sub-Group</label>
										<div class="col-md-2">
											<select name="subGroupId" class="form-control select2ajax filters"
												data-ajax-url="<c:url value='/masterMaintenance/UnitMaintenance/querySubGroupSelect2'/>"></select>
										</div>
										--%>
										<label class="col-md-1 control-label">Outlet Type</label>
										<div class="col-md-2">
											<select name="outletTypeId" class="form-control select2ajax filters criteria"
													data-ajax-url="<c:url value='/masterMaintenance/UnitMaintenance/getOutletTypesSelect2'/>"></select>
										</div>
										<label class="col-md-1 control-label">Sub-item</label>
										<div class="col-md-2">
											<div class="input-group">
												<select name="subItemId" class="form-control filters"
													data-ajax-url="<c:url value='/masterMaintenance/UnitMaintenance/querySubItemSelect2'/>"
													data-single-url="<c:url value='/masterMaintenance/UnitMaintenance/getSubItemDetailByIds'/>"
													multiple></select>
												<div class="input-group-addon lookup"
													data-bottom-entity-class="SubItem"
													data-unitlookup-only-active="false">
													<i class="fa fa-search"></i>
												</div>
											</div>
										</div>
										<label class="col-md-1 control-label">Status</label>
										<div class="col-md-2">
											<select name="status" class="form-control filters">
												<option></option>
												<option>Active</option>
												<option>Inactive</option>
											</select>
										</div>
									</div>
									<%--
									<div class="row" style="margin-top: 10px;">
										<label class="col-md-1 control-label">Item</label>
										<div class="col-md-2">
											<div class="input-group">
												<select name="itemId" class="form-control select2ajax filters"
													data-ajax-url="<c:url value='/masterMaintenance/UnitMaintenance/queryItemSelect2'/>"
													data-single-url="<c:url value='/masterMaintenance/UnitMaintenance/getItemDetailByIds'/>"></select>
												<div class="input-group-addon lookup"
													data-bottom-entity-class="Item">
													<i class="fa fa-search"></i>
												</div>
											</div>
										</div>
										<label class="col-md-1 control-label">Outlet Type</label>
										<div class="col-md-2">
											<select name="outletTypeId" class="form-control select2ajax filters"
													data-ajax-url="<c:url value='/masterMaintenance/UnitMaintenance/getOutletTypesSelect2'/>"></select>
										</div>
										<label class="col-md-1 control-label">Sub-item</label>
										<div class="col-md-2">
											<div class="input-group">
												<select name="subItemId" class="form-control select2ajax filters"
													data-ajax-url="<c:url value='/masterMaintenance/UnitMaintenance/querySubItemSelect2'/>"
													data-single-url="<c:url value='/masterMaintenance/UnitMaintenance/getSubItemDetailByIds'/>"></select>
												<div class="input-group-addon lookup"
													data-bottom-entity-class="SubItem">
													<i class="fa fa-search"></i>
												</div>
											</div>
										</div>
										<label class="col-md-1 control-label">Status</label>
										<div class="col-md-2">
											<select name="status" class="form-control filters">
												<option></option>
												<option>Active</option>
												<option>Inactive</option>
											</select>
										</div>
									</div>
									--%>
									<div class="row" style="margin-top: 10px;">
										<label class="col-md-1 control-label">Product Type</label>
										<div class="col-md-2">
											<select name="productCategoryId" class="form-control select2ajax filters criteria"
												data-ajax-url="<c:url value='/masterMaintenance/UnitMaintenance/queryProductGroupSelect2'/>"></select>
										</div>
										<label class="col-md-1 control-label">CPI base period</label>
										<div class="col-md-2">
											<select name="cpiBasePeriod" class="form-control select2ajax filters criteria"
													data-ajax-url="<c:url value='/masterMaintenance/UnitMaintenance/queryCPIBasePeriodSelect2'/>"></select>
										</div>
										<div class="col-md-2 col-md-offset-1">
											<button type="button" class="btn btn-info">Clear</button>
										</div>
									</div>
								</form>
								<hr/>
							</div>
							
								<table class="table table-striped table-bordered table-hover responsive" id="dataList">
									<thead>
									<tr>
										<th>Variety Code</th>
										<th>Variety English Name</th>
										<th>CPI Base Period</th>
										<th>Purpose</th>
										<th>Section English Name</th>
										<th>Group English Name</th>
										<th>Sub-group English Name</th>
										<th>Item English Name</th>
										<th>Outlet Type English Name</th>
										<th>Sub-item English Name</th>
										<th>Product Type</th>
										<th>Status</th>
										<th>Section Code</th>
										<th>Section Chinese Name</th>
										<th>Group Code</th>
										<th>Group Chinese Name</th>
										<th>Sub-group Code</th>
										<th>Sub-group Chinese Name</th>
										<th>Item Code</th>
										<th>Item Chinese Name</th>
										<th>Outlet Type Code</th>
										<th>Outlet Type Chinese Name</th>
										<th>Sub-item Code</th>
										<th>Sub-item Chinese Name</th>
										<th>Variety Chinese Name</th>
									
									<!--
										<th>Variety Code</th>
										<th>Variety Name</th>
										<th>Purpose</th>
										<th>Group Name</th>
										<th>Sub-group Name</th>										
										<th>Item Name</th>
										<th>Outlet Type Name</th>
										<th>CPI Base Period</th>
										<th>Status</th>
										<th>Product Type</th>
										
										<th>Section Code</th>
										<th>Section Chinese Name</th>
										<th>Section English Name</th>
										<th>Group Code</th>
										<th>Group English Name</th>
										
										<th>Sub-group Code</th>		
										<th>Sub-group English Name</th>
										
										<th>Sub-item Code</th>
										<th>Sub-item English Name</th>
																		
										<th>Item Code</th>
										<th>Item English Name</th>
										
										<th>Outlet Type Code</th>
										<th>Outlet Type English Name</th>
									-->
									<!-- 
										<th>Variety Code</th>
										<th>Variety Chinese Name</th>
										<th>Variety English Name</th>
										<th>Purpose</th>
										<th>Section Code</th>
										<th>Section Chinese Name</th>
										<th>Section English Name</th>
										<th>Group Code</th>
										<th>Group Chinese Name</th>
										<th>Group English Name</th>
										<th>Sub-group Code</th>
										<th>Sub-group Chinese Name</th>
										<th>Sub-group English Name</th>
										<th>Item Code</th>
										<th>Item Chinese Name</th>
										<th>Item English Name</th>
										<th>Outlet Type Code</th>
										<th>Outlet Type Chinese Name</th>
										<th>Outlet Type English Name</th>
										<th>Sub-item Code</th>
										<th>Sub-item Chinese Name</th>
										<th>Sub-item English Name</th>
										<th>Product Category</th>
										<th>CPI Base Period</th>
									 -->
										<th class="text-center action" data-priority="1"></th>
									</tr>
									</thead>
								</table>
						</div>
					</div>
				</div>
			</div>
        </section>
	</jsp:body>
</t:layout>

