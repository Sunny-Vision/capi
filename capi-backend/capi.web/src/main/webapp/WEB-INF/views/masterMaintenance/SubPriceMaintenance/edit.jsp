<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page session="false"%>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
	</jsp:attribute>
	<jsp:attribute name="header">
<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
<script src="<c:url value='/resources/js/Sortable.js'/>"></script>
<script src="<c:url value='/resources/js/underscore-min.js'/>"></script>

<style>
			.data-field-container .item .inner {
				border: solid 1px #000000;
				background: #ffffff;
				margin: 5px;
			}
			
			.data-field-container .item {
				color: #000000;
				cursor: pointer;
				font-size: 15pt;
			}
			
			.data-field-container .item .btn-delete {
				position: absolute;
				right: 25px;
				top: 5px;
				color: #000000;
			}
			
			.data-field-container.center-text .item {
				text-align: center;
			}
			
			.data-field-container.center-text .item .inner {
				padding: 5px 0;
			}
			
			#selectedPatternCotnainer {
				height: 200px;
				overflow: auto;
				border: solid 1px #ddd;
				margin-top: 10px;
				padding-top: 10px;
				margin-left: 10px;
				margin-right: 10px;
			}
			#selectedPatternCotnainer .error {
				border: solid 1px red;
			}
			.pattern-error-placement {
				margin-left: 10px;
				margin-right: 10px;
			}
			
			.form-control{
				vertical-align: middle;
			}
			
			.formula-label{
				text-align: center;
   				font-size: xx-large;
			}
			.formula-item{
				height: 100%;
				vertical-align: middle;
			}
		</style>
<script>

$(function() {
	var selectedGroup = '${model.groupByField.getId()}';
	var selectedDivided = '${model.dividedByField.getId()}';
	
	jQuery.validator.addMethod(
			"dividedvalidate",
			function(value, element) {
				var groupBy = $("select[name='groupByField']").val();
				if(groupBy != "" ){
					if(value == ""){
						return false;
					}
				}
				return true;
			},
			"<spring:message code='E00010' />");
	
	var $mainForm = $('#mainForm');
	$mainForm.validate();
	Modals.init();

	var $selectedPatternCotnainer = $('#selectedPatternCotnainer');
	var selectedItemTemplateHtml = $('#selectedItemTemplate').html();
	
	Sortable.create(
		$selectedPatternCotnainer.get(0),
		{
			sort: true,
			group: {
				pull: true,
				put: true
			},
			animation: 150,
			onAdd: function (/**Event*/evt) {
		        var $itemEl = $(evt.item);  // dragged HTMLElement
		        $itemEl.find('.btn-delete').show();
		    },
		}
	);
	
	$('#subPriceFieldModal').on('show.bs.modal', function (event) {
		var modal = $(this);
		var $dataTable = $("#dataList", modal);
		//console.log($dataTable);
		
		var selectedFields = [];
		var fieldsToBeAdded = [];
		
		$dataTable.DataTable({
			"ordering": true, 
			"order": [[ 1, "asc" ]],
			"searching": true,
			"buttons": [],
			"processing": true,
            "serverSide": true,
            "ajax": {
            	url: "<c:url value='/masterMaintenance/SubPriceFieldMaintenance/query'/>",
            	data: function(d) {
            		var subPriceFieldId = [];
            		$("input[name='subPriceFieldId[]']").each(function(){
            			subPriceFieldId.push($(this).val());
            		})
            		d.subPriceFieldId = subPriceFieldId;
            	},
            	method: "post"
            },
            "columns": [
                        { "data": function(data){
                        	/*if (_.find(selectedFields, function (ele){ return (+ele.value) == data.id })){
                        		return "<input type='checkbox' checked name='id' value='"+data.id+"' data-label='"+data.fieldName+"' data-type='"+data.fieldType+"' data-variable='"+data.variableName+"'/>";
                        	}
                        	else{
                        		return "<input type='checkbox' name='id' value='"+data.id+"' data-label='"+data.fieldName+"' data-type='"+data.fieldType+"' data-variable='"+data.variableName+"'/>";
                        	}*/
                        	//return "<input type='checkbox' name='id' value='"+data.id+"' data-label='"+data.fieldName+"' data-type='"+data.fieldType+"' data-variable='"+data.variableName+"'/>";
                        	var checked = false;
                        	for(var i = 0; i < fieldsToBeAdded.length; i++) {
                        		if(data.id == +fieldsToBeAdded[i].value) {
                        			checked = true;
                        			break;
                        		}
                        	}
                        	var html = "<input type='checkbox' name='id' value='"+data.id+"' data-label='"+data.fieldName+"' data-type='"+data.fieldType+"' data-variable='"+data.variableName+"'"+(checked ? " checked" : "")+" />";
                        	return html;
                        	} 
                        },
                        { "data": "fieldName" },
                        { "data": "fieldType" },
                    ],
            "columnDefs": [
                           {
                               "targets": [0],
                               "orderable": false,
                               "searchable": false,
                               "className" : "text-center"
                           },
                       ],
	        "initComplete": function(settings, json) {
            	$(this).on('click', '.btn-delete', function(e) {
            		e.preventDefault();
            		var id = $(this).data('id');
            		deleteRecordsWithConfirm('id=' + id);
            	});
            	
            	$(this).on('change', '[name="id"]', function(){
            		var self = this;
            		if (this.checked){
            			selectedFields.push({
            				value : self.value,
            				type: $(self).data("type"),
            				label : $(self).data("label"),
            				variable: $(self).data("variable")
            			});
            			fieldsToBeAdded.push({
            				value : self.value,
            				type: $(self).data("type"),
            				label : $(self).data("label"),
            				variable: $(self).data("variable")
            			});
            		}
            		else{
            			selectedFields = _.filter(selectedFields, function(ele){
            				return (+ele.value) != (+self.value)
            			});
            			fieldsToBeAdded = _.filter(fieldsToBeAdded, function(ele){
            				return (+ele.value) != (+self.value)
            			});
            		}
            	});
            }
		});
		
		$(".modal-add").on('click', modal, function(){
			//console.log($("input[name='id']:checked"));
			/*for (i = 0; i < selectedFields.length; i++){
				$html = $(selectedItemTemplateHtml);
				$input = $("input[name='subPriceFieldId[]']", $html);
				$input.val(selectedFields[i].value);
				$input.data("type", selectedFields[i].type);
				$label = $(".item-label", $html);
				$label.html(selectedFields[i].label+"("+selectedFields[i].variable+")");
				$("#selectedPatternCotnainer", document).append($html);
			}*/
			for (i = 0; i < fieldsToBeAdded.length; i++){
				$html = $(selectedItemTemplateHtml);
				$input = $("input[name='subPriceFieldId[]']", $html);
				$input.val(fieldsToBeAdded[i].value);
				$input.data("type", fieldsToBeAdded[i].type);
				$label = $(".item-label", $html);
				$label.html(fieldsToBeAdded[i].label+"("+fieldsToBeAdded[i].variable+")");
				$("#selectedPatternCotnainer", document).append($html);
			}
			fieldsToBeAdded = [];
			/*
			$("input[name='id']:checked").each(function(){
				//console.log($(this));
				$html = $(selectedItemTemplateHtml);
				$input = $("input[name='subPriceFieldId[]']", $html);
				$input.val($(this).val());
				$input.data("type", $(this).data("type"));
				$label = $(".item-label", $html);
				$label.html($(this).data("label")+"("+$(this).data("variable")+")");
				$("#selectedPatternCotnainer", document).append($html);
			});
			*/
			updateGroupByListDividedByList();
			modal.modal('hide');
		});
		
		$(".modal-close").on('click', modal, function(){
			selectedFields = [];
			fieldsToBeAdded = [];
			modal.modal('hide');
		});
	});
	
	$(".selectAll").change(function() {
		var checkboxes = $(this).closest('table').find(':checkbox');
		if($(this).is(':checked')) {
			checkboxes.not(':checked').trigger('click')
		} else {
			checkboxes.find(':checked').trigger('click')
		}
	});
	
	$selectedPatternCotnainer.on('click', '.btn-delete', function(e) {
		e.preventDefault();
		$(this).closest('.item').remove();
		updateGroupByListDividedByList();
	});
	
	
	$('#subPriceFieldModal').on('show', function (event) {
		var modal = $(this);
		var $dataTable = $("#dataList", modal);
		$dataTable.DataTable().ajax.reload();
	});
	
	function updateGroupByListDividedByList(){

		$groupBy = $("select[name='groupByField']");
		$dividedBy = $("select[name='dividedByField']");
		
		$groupBy.html("");
		$groupBy.append("<option></option>");
		$("input[name='subPriceFieldId[]']").each(function(){
			var selected = $(this).val() == selectedGroup ? "selected" : "";
			option = "<option value='"+$(this).val()+"' "+selected+">"+$(this).prev('.item-label').html()+"</option>";
			$groupBy.append(option);
		});

		$dividedBy.html("");

		$dividedBy.append("<option></option>");
		$("input[name='subPriceFieldId[]']").each(function(){
			if($(this).data("type") == "Number"){
				var selected = $(this).val() == selectedDivided ? "selected" : "";
				option = "<option value='"+$(this).val()+"' "+selected+">"+$(this).prev('.item-label').html()+"</option>";
				$dividedBy.append(option);
			}
		});
	} 
	$("[name=useMinNPrice],[name=useMaxNPrice]").click(function(){
		if (this.checked){
			if (this.name == "useMinNPrice" ){
				$("[name=useMaxNPrice]").removeAttr("checked");
			}
			else{
				$("[name=useMinNPrice]").removeAttr("checked");
			}
		}
	})
	
	$("[name=useMinSPrice],[name=useMaxSPrice]").click(function(){
		if (this.checked){
			if (this.name == "useMinSPrice" ){
				$("[name=useMaxSPrice]").removeAttr("checked");
			}
			else{
				$("[name=useMinSPrice]").removeAttr("checked");
			}
		}
	});
	
	<sec:authorize access="!hasPermission(#user, 256)">
		viewOnly();
	</sec:authorize>
});
</script>
</jsp:attribute>
	<jsp:body>
		<section class="content-header">
			<h1>Sub-Price Type Maintenance</h1>
			<c:if test="${not empty model.subPriceTypeId}">
				<div class="breadcrumb form-horizontal" style="width:240px">
					<div class="form-group" style="margin-bottom:0px">
			        	<div class="col-sm-5">Created Date:</div>
			        	<div class="col-sm-7">${commonService.formatDateTime(model.createdDate)}</div>
			        </div>
			        <div class="form-group" style="margin-bottom:0px">
			         	<div class="col-sm-5">Last Modified:</div>
			         	<div class="col-sm-7">${commonService.formatDateTime(model.modifiedDate)}</div>
			         </div>
		      	</div>
	      	</c:if>
		</section>
		<section class="content">
		<form class="form-horizontal" action="<c:url value='/masterMaintenance/SubPriceMaintenance/save'/>" method="post" role="form" id="mainForm">
		    <div class="row">
				<div class="col-md-12">
					<!-- content -->
						<!-- Itinerary Planning Table -->
						<div class="box box-primary">
							<!-- /.box-header -->
							<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/masterMaintenance/SubPriceMaintenance/home'/>">Back</a>
							</div>
							<!-- form start -->
								<div class="box-body">
									<div class="form-group">
										<label for="subPriceTypeId" class="col-sm-2 control-label">Name</label>
										<div class="col-sm-6">
											<input type="hidden" name="subPriceTypeId" value="<c:out value="${model.subPriceTypeId}" />" required>
											<input type="text" name="name" class="form-control" placeholder="Field Name" value="<c:out value="${model.name}" />" required>
										</div>
									</div>
									<div class="form-group">
										<label for="fieldName" class="col-sm-2 control-label">Category</label>
										<div class="col-sm-6">
											<input type="text" name="category" class="form-control" placeholder="Field Name" value="<c:out value="${model.category}" />" required>
										</div>
									</div>
	       							<div class="form-group">
	       								<label class="col-sm-2 control-label">Status</label>
	       								<div class="col-sm-5">
											<label class="radio-inline">
												<input type="radio" name="status" value="Enable" <c:if test="${model.status == 'Enable'}">checked</c:if> required> Enable
											</label>
											<label class="radio-inline">
												<input type="radio" name="status" value="Disable" <c:if test="${model.status == 'Disable'}">checked</c:if> required> Disable
											</label>
										</div>
									</div>
								</div>
							<!-- /.box-body -->
						</div>
				</div>
			</div>
			<div class="row">
				<div class="col-sm-12">
					<div class="box box-primary">
						<!-- /.box-header -->
							<div class="box-header with-border">
			                	<h3 class="box-title">Sub Price Fields</h3>
			                </div>	
						<!-- form start -->
							<div class="box-body">
								<div id="selectedPatternCotnainer" class="row sortable data-field-container center-text pattern-error-highlight">
									<c:forEach var="field" items="${fields}">
										<div class="item col-md-2">
											<a href="#" class="btn-delete"><i class="fa fa-times"></i></a>
											<div class="inner">
												<div class="item-label">${field.fieldName}(${field.variableName})</div>
												<input type="hidden" name="subPriceFieldId[]" value="<c:out value="${field.subPriceFieldId}" />" data-type="${field.fieldType}"/>
											</div>
										</div>
									</c:forEach>
								</div>
								<div class="pattern-error-placement"></div>
							</div>
						<!-- /.box-body -->
							<div class="box-footer">
								<a class="btn btn-default" data-toggle="modal" data-target="#subPriceFieldModal">Add</a>
							</div>
						<!-- /.box-footer -->
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-sm-12">
					<div class="box box-primary">
						<!-- /.box-header -->
							<div class="box-header with-border">
			                	<span class="box-title">Formula</span>
			                	<span class="pull-right">Variables: p Price, n Total no. of rows</span>
			                </div>	
						<!-- form start -->
							<div class="box-body">
								<div class="form-group">
									<div class="col-sm-12">
										<div class="col-sm-2 formula-label">Σ(
										</div>
										<div class="col-sm-3 formula-item">
											<input type="text" name="numeratorFormula" class="form-control" value="<c:out value="${model.numeratorFormula}" />" required/>
										</div>
										<div class="col-sm-2 formula-label">) / Σ(
										</div>
										<div class="col-sm-3 formula-item">
											<input type="text" name="denominatorFormula" class="form-control" value="<c:out value="${model.denominatorFormula}" />" required/>
										</div>
										<div class="col-sm-2 formula-label">)
										</div>
									</div>
								</div>
							</div>
						<!-- /.box-body -->
						<!-- /.box-footer -->
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-sm-12">
					<div class="box box-primary">
						<!-- /.box-header -->
							<div class="box-header with-border">
			                	<h3 class="box-title">Group</h3>
			                </div>	
						<!-- form start -->
							<div class="box-body">
								<div class="form-group">
									<label for="inputEmail3" class="col-sm-2 control-label">Group By</label>
									<div class="col-sm-6">
										<select class="form-control" name="groupByField">
											<option></option>
											<c:forEach var="field" items="${fields}">
												<option value="<c:out value="${field.subPriceFieldId}" />" <c:if test="${field.subPriceFieldId == model.groupByField.getId()}">selected</c:if>>${field.fieldName}(${field.variableName})</option>
											</c:forEach>
										</select>
									</div>
								</div>
								<div class="form-group">
									<label for="inputEmail3" class="col-sm-2 control-label">Divided By</label>
									<div class="col-sm-6">
										<select class="form-control" name="dividedByField" data-rule-dividedvalidate="true">
											<option></option>
											<c:forEach var="field" items="${fields}">
												<c:if test="${field.fieldType.equalsIgnoreCase('Number')}">
												<option value="<c:out value="${field.subPriceFieldId}" />" <c:if test="${field.subPriceFieldId == model.dividedByField.getId()}">selected</c:if>>${field.fieldName}(${field.variableName})</option>
												</c:if>
											</c:forEach>
										</select>
									</div>
								</div>
							</div>
						<!-- /.box-body -->
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-sm-12">
					<div class="box box-primary">
					
						<!-- /.box-header -->
							<div class="box-header with-border">
			                	<h3 class="box-title">Misc</h3>
			                </div>	
						<!-- form start -->
							<div class="box-body">
								<div class="form-group">
									<div class="col-sm-6">
										<div>
											<label>
												<input type="checkbox" name="hideNPrice" value="true" <c:if test="${model.hideNPrice}">checked</c:if> /> 
												<span>Hide N Price</span>
											</label>
											<label>
												<input type="checkbox" name="hideSPrice" value="true" <c:if test="${model.hideSPrice}">checked</c:if>/> 
												<span>Hide S Price</span>
											</label>
											<label>
												<input type="checkbox" name="hideDiscount" value="true" <c:if test="${model.hideDiscount}">checked</c:if> /> 
												<span>Hide Discount</span>
											</label>
										</div>
										
										<div>
											<label>
												<input type="checkbox" name="useMinNPrice" value="true" <c:if test="${model.useMinNPrice}">checked</c:if> /> 
												<span>Use Min N Price</span>
											</label>
											<label>
												<input type="checkbox" name="useMaxNPrice" value="true"  <c:if test="${model.useMaxNPrice}">checked</c:if> /> 
												<span>Use Max N Price</span>
											</label>
										</div>
										
										<div>
											<label>
												<input type="checkbox" name="useMinSPrice" value="true"  <c:if test="${model.useMinSPrice}">checked</c:if> /> 
												<span>Use Min S Price</span>
											</label>
											<label>
												<input type="checkbox" name="useMaxSPrice" value="true" <c:if test="${model.useMaxSPrice}">checked</c:if> /> 
												<span>Use Max S Price</span>
											</label>
										</div>
									</div>
								</div>
							</div>
						<!-- /.box-body -->
						<sec:authorize access="hasPermission(#user, 256)">
							<div class="box-footer">							
								<button type="submit" class="btn btn-info">Submit</button>							
							</div>
						</sec:authorize>
					<!-- /.box-footer -->
					</div>
				</div>
			</div>
					</form>
		</section>
		
		<script id="selectedItemTemplate" type="text/html">
			<div class="item col-md-2">
				<a href="#" class="btn-delete"><i class="fa fa-times"></i></a>
				<div class="inner">
					<div class="item-label"></div>
					<input type="hidden" name="subPriceFieldId[]"/>
				</div>
			</div>
		</script>
		
		<div class="modal fade" role="dialog" id="subPriceFieldModal">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close modal-close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">X</span></button>
						<h4 class="modal-title">Sub Price Fields</h4>
					</div>
					<div class="modal-body">
						<!-- content -->
						<!-- Itinerary Planning Table -->						
						<table class="table table-striped table-bordered table-hover" id="dataList">
							<thead>
							<tr>
								<th class="text-center action" ><input class="selectAll" type="checkbox" /></th>
								<th>Field</th>
								<th>Field Type</th>
							</tr>
							</thead>
							<tbody>
							</tbody>
						</table>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default modal-close pull-left" data-dismiss="modal">Close</button>
						<button type="button" class="btn btn-primary modal-add" >Add</button>
					</div>
				</div><!-- /.modal-content -->
			</div><!-- /.modal-dialog -->
		</div>	
	</jsp:body>
</t:layout>
