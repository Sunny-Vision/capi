<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
<t:layout>
	<jsp:attribute name="header">
		<style>
			.data-field-container .item .inner {
				border: solid 1px #000000;
				background: #ffffff;
				margin: 5px;
				height: 100px;
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
				padding: 20px 0;
			}
			
			@media (min-width: 992px) {
				#possiblePatternContainer .col-md-2 {
					width: 33.33333333%;
				}
			}
			
			#selectedPatternCotnainer {
				height: 250px;
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
		</style>
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<script src="<c:url value='/resources/js/Sortable.js'/>"></script>
		<script>
			$(function() {
				var lastVarId = 0;
				var $mainForm = $('#mainForm');
				var $selectedPatternCotnainer = $('#selectedPatternCotnainer');
				var selectedItemTemplateHtml = $('#selectedItemTemplate').html();
				
				function isNumberItem(value) {
					return value.indexOf('數字') >= 0;
				}
				
				function getNextVarId() {
					lastVarId = lastVarId + 1;
					return lastVarId;
				}
				
				function autoResetLastId() {
					var variable = $mainForm.find('[name="variable"]').val();
					if (variable == '')
						lastVarId = 0;
				}
				
				function genDisplayPattern($container) {
					var displayPattern = '';
					
					$container.find('.item-label').each(function() {
						var value = $(this).html();
						if (displayPattern != '') displayPattern += '|';
						
						displayPattern += value;
					});
					
					return displayPattern;
				}
				
				function genPattern($container) {
					var pattern = '';
					
					$container.find('.item-label').each(function() {
						var value = $(this).html();
						value = escapeRegExp(value);
						value = value.replace(/ /g, '');
						
						if (value.indexOf('數字\\+') >= 0)
							pattern += '(\\d+)';
						else if (value.indexOf('數字') >= 0)
							pattern += '(\\d)';
						else
							pattern += value;
					});
					
					return pattern;
				}
				
				function genVariable($container) {
					var variable = '';
					
					$container.find('.item-label-var').each(function() {
						var varValue = $(this).html();
						if (varValue == '') return true;
						
						if (variable != '') variable += ',';
						
						varValue = varValue.replace('(', '').replace(')', '');
						variable += varValue;
					});
					
					return variable;
				}
				
				function refreshHiddenFields($container) {
					$mainForm.find('[name="displayPattern"]').val(genDisplayPattern($container));
					$mainForm.find('[name="pattern"]').val(genPattern($container));
					$mainForm.find('[name="variable"]').val(genVariable($container));
					
					autoResetLastId();
				}
				
				function initSelectedPattern() {
					var displayPattern = $mainForm.find('[name="displayPattern"]').val();
					var displayValues = displayPattern.split('|');
					var variable = $mainForm.find('[name="variable"]').val();
					var variables = variable.split(',');
					
					var variableIndex = 0;
					for (var i = 0; i < displayValues.length; i++) {
						var $html = $(selectedItemTemplateHtml);
						var value = displayValues[i];
						if (value == '') continue;
						
						$html.find('.item-label').html(value);
						if (isNumberItem(value)) {
							$html.find('.item-label-var').html('(' + variables[variableIndex] + ')');
							lastVarId = +variables[variableIndex].substr(1);
							variableIndex++;
						}
						$selectedPatternCotnainer.append($html);
					}
				}
				
				<sec:authorize access="hasPermission(#user, 256)">
					$('#possiblePatternContainer .inner-container').each(function(container) {
						Sortable.create(
								this,
								{
									sort: false,
									group: {
										pull: 'clone',
										put: false
									},
									animation: 150
								}
						);
					});
				</sec:authorize>
				;
				<sec:authorize access="hasPermission(#user, 256)">
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
							        
							        var value = $itemEl.find('.item-label').html();
							        if (isNumberItem(value)) {
							        	var varId = getNextVarId();
							        	$itemEl.find('.item-label-var').html('(v' + varId + ')');
							        }
							    	refreshHiddenFields($(evt.target));
							    },
							    onSort: function(evt) {
							    	refreshHiddenFields($(evt.target));
							    }
							}
						);
				</sec:authorize>
				
				$selectedPatternCotnainer.on('click', '.btn-delete', function(e) {
					e.preventDefault();
					$(this).closest('.item').remove();
					refreshHiddenFields($selectedPatternCotnainer);
				});
				
				initSelectedPattern();
				
				$.validator.addMethod('validformula', function (value, element, param){
					return value != null && /NP/g.test(value);
				},'<spring:message code="E00119" />')
				
				// validate all fields include hidden field
				$mainForm.validate({
					ignore: ""
				});
				
				<sec:authorize access="!hasPermission(#user, 256)">
					viewOnly();
					$('.box-body').find('.btn-delete').remove();
				</sec:authorize>
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Discount Formula Maintenance</h1>
          <c:if test="${not empty model.discountFormulaId}">
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
        	<form id="mainForm" action="<c:url value='/masterMaintenance/DiscountFormulaMaintenance/save'/>" method="post" role="form">
        		<input name="discountFormulaId" value="<c:out value="${model.discountFormulaId}" />" type="hidden" />
        		<input name="displayPattern" value="<c:out value="${model.displayPattern}" />" type="hidden" />
        		<input name="pattern" value="<c:out value="${model.pattern}" />" type="hidden" required />
        		<input name="variable" value="<c:out value="${model.variable}" />" type="hidden" />
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/masterMaintenance/DiscountFormulaMaintenance/home'/>">Back</a>
							</div>
	        				<!-- /.box-header -->
        					
        					<script id="selectedItemTemplate" type="text/html">
								<div class="item col-md-2">
									<a href="#" class="btn-delete"><i class="fa fa-times"></i></a>
									<div class="inner">
										<div class="item-label"></div>
										<div class="item-label-var"></div>
									</div>
								</div>
							</script>
        					
        					<div class="box-body">
        						<div class="form-control-static">Pattern:</div>
				        		<div id="possiblePatternContainer" class="row data-field-container center-text">
				        			<!-- calculator maintenance items -->
									<div class="col-md-6">
										<div class="row inner-container">
											<c:forEach var="value" items="${itemlist.values}">
											<c:if test="${value != ''}">
											<div class="item col-md-2">
												<a href="#" class="btn-delete" style="display:none"><i class="fa fa-times"></i></a>
												<div class="inner">
													<div class="item-label" data-toggle="popover" data-content="hi">${value}</div>
													<div class="item-label-var"></div>
												</div>
											</div>
											</c:if>
											</c:forEach>
										</div>
									</div>
									
									<!-- hard coded items -->
									<div class="col-md-6">
										<div class="row inner-container">
											<div class="item col-md-2">
												<a href="#" class="btn-delete" style="display:none"><i class="fa fa-times"></i></a>
												<div class="inner">
													<div class="item-label">數字+</div>
													<div class="item-label-var"></div>
												</div>
											</div>
											<div class="item col-md-2">
												<a href="#" class="btn-delete" style="display:none"><i class="fa fa-times"></i></a>
												<div class="inner">
													<div class="item-label">數字</div>
													<div class="item-label-var"></div>
												</div>
											</div>
											<div class="item col-md-2">
												<a href="#" class="btn-delete" style="display:none"><i class="fa fa-times"></i></a>
												<div class="inner">
													<div class="item-label">$</div>
													<div class="item-label-var"></div>
												</div>
											</div>
											<div class="item col-md-2">
												<a href="#" class="btn-delete" style="display:none"><i class="fa fa-times"></i></a>
												<div class="inner">
													<div class="item-label">.</div>
													<div class="item-label-var"></div>
												</div>
											</div>
											<div class="item col-md-2">
												<a href="#" class="btn-delete" style="display:none"><i class="fa fa-times"></i></a>
												<div class="inner">
													<div class="item-label">,</div>
													<div class="item-label-var"></div>
												</div>
											</div>
										</div>
									</div>
								</div>
								
								<div id="selectedPatternCotnainer" class="row sortable data-field-container center-text pattern-error-highlight"></div>
								<div class="pattern-error-placement"></div>
								
        					</div><!-- /.box-body -->
	        			</div><!-- /.box -->
	        		</div>
	        	</div>
	        			
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<div class="box box-primary">
	       					<div class="box-body">
	       						<div class="form-horizontal">
	       							<div class="form-group">
	       								<label class="col-sm-2 control-label">Formula</label>
	       								<div class="col-sm-5">
											<input name="formula" type="text" class="form-control" value="<c:out value="${model.formula}" />" required data-rule-validformula="true" />
											<p class="form-control-static">Variables: NP = N Price</p>
										</div>
									</div>
	       							<div class="form-group">
	       								<label class="col-sm-2 control-label">Status</label>
	       								<div class="col-sm-5">
											<label class="radio-inline">
												<input type="radio" name="status" value="Enable" <c:if test="${model.status == 'Enable'}">checked</c:if>> Enable
											</label>
											<label class="radio-inline">
												<input type="radio" name="status" value="Disable" <c:if test="${model.status == 'Disable'}">checked</c:if>> Disable
											</label>
										</div>
									</div>
	       						</div>
	       					</div>
	       					<sec:authorize access="hasPermission(#user, 256)">
		       					<div class="box-footer">
		        					<button type="submit" class="btn btn-info">Submit</button>
	       						</div>
	       					</sec:authorize>
	        			</div>
	        		</div>
	        	</div>
        	</form>
        </section>
	</jsp:body>
</t:layout>

