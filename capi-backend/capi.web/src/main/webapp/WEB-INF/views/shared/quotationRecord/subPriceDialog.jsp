<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div class="sub-price-dialog-container">
<div class="modal fade sub-price-dialog" role="dialog">
	<div class="modal-dialog modal-xlg">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">X</span></button>
				<h4 class="modal-title">Sub Price</h4>
			</div>
			<div class="modal-body" style="min-height: 500px">
				<!-- content -->
				<div class="form-horizontal">
					<div class="form-group">
						<label class="col-sm-2 control-label">Price Type:</label>
						<div class="col-sm-4">
							<c:choose>
							<c:when test="${quotationRecord.unitSubPriceType}">
								<p class="form-control-static">${quotationRecord.subPriceTypeName}</p>
								<input type="hidden" name="subPriceTypeId" value="<c:out value="${quotationRecord.subPriceTypeId}" />" ${model.readonly ? 'disabled' : ''}/>
							</c:when>
							<c:otherwise>
								<select name="subPriceTypeId" class="form-control select2"
									data-ajax-url="<c:url value='/shared/General/querySubPriceTypeSelect2'/>" ${model.readonly ? 'disabled' : ''}>
									<c:if test="${quotationRecord.subPriceTypeId != null}">
										<option value="<c:out value="${quotationRecord.subPriceTypeId}" />">${quotationRecord.subPriceTypeName}</option>
									</c:if>
								</select>
							</c:otherwise>
							</c:choose>
						</div>
					</div>
					<div class="row">
						<div class="col-md-12" style="overflow-x: auto">
							<table class="table table-striped table-bordered table-hover datatable">
								<c:if test="${not empty quotationRecord.fields}">
									<thead>
									<tr>
										<c:forEach var="field" items="${quotationRecord.fields}">
											<th class="field-column">${field.fieldName}</th>
										</c:forEach>
										<th class="nprice-column">N price</th>
										<th class="sprice-column">S price</th>
										<th class="discount-column">Discount</th>
										<th></th>
									</tr>
									</thead>
									<tbody class="databody">
										<c:forEach var="subPrice" items="${quotationRecord.subPrices}" varStatus="recordLoop">
										<tr class="datarow">
											<c:forEach var="column" items="${subPrice.subPriceColumns}" varStatus="columnLoop">
												<td class="field-column">
													<input type="hidden" name="${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}subPrices[${recordLoop.index}].subPriceColumns[${columnLoop.index}].subPriceFieldId" value="<c:out value="${column.subPriceFieldId}" />" ${model.readonly ? 'disabled' : ''}/>
													<c:if test="${not model.readonly}">
														<input type="hidden" name="${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}subPrices[${recordLoop.index}].subPriceColumns[${columnLoop.index}].subPriceColumnId" value="<c:out value="${column.subPriceColumnId}" />" ${model.readonly ? 'disabled' : ''}/>
													</c:if>
													<input type="hidden" name="${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}subPrices[${recordLoop.index}].subPriceColumns[${columnLoop.index}].fieldType" value="<c:out value="${column.fieldType}" />" ${model.readonly ? 'disabled' : ''}/>
													<input type="hidden"
														name="${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}subPrices[${recordLoop.index}].subPriceColumns[${columnLoop.index}].variableName" value="<c:out value="${column.variableName}" />" ${model.readonly ? 'disabled' : ''}/>
													<c:choose>
														<c:when test="${column.fieldType == 'Text'}">
															<input type="text"
																name="${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}subPrices[${recordLoop.index}].subPriceColumns[${columnLoop.index}].columnValue" class="form-control" value="<c:out value="${column.columnValue}" />"
																style="min-width: 70px" ${model.readonly ? 'disabled' : ''}/>
														</c:when>
			
														<c:when test="${column.fieldType == 'Date'}">
															<div class="input-group date date-picker" style="width: 130px">
							       								<input type="text"
																	name="${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}subPrices[${recordLoop.index}].subPriceColumns[${columnLoop.index}].columnValue" class="form-control" value="<c:out value="${column.columnValue}" />" ${model.readonly ? 'disabled' : ''}>
							       								<div class="input-group-addon">
							       									<i class="fa fa-calendar"></i>
							       								</div>
							       							</div>
						       							</c:when>
			
														<c:when test="${column.fieldType == 'Number'}">
															<input type="text"
																name="${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}subPrices[${recordLoop.index}].subPriceColumns[${columnLoop.index}].columnValue" class="form-control" value="<c:out value="${column.columnValue}" />"
																data-rule-number="true" style="min-width: 70px" ${model.readonly ? 'disabled' : ''}/>
														</c:when>
			
														<c:when test="${column.fieldType == 'Time'}">
							       							<div class="input-group bootstrap-timepicker" style="width: 100px">
																<input
																	name="${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}subPrices[${recordLoop.index}].subPriceColumns[${columnLoop.index}].columnValue" type="text" class="form-control time-picker" value="<c:out value="${column.columnValue}" />" ${model.readonly ? 'disabled' : ''}/>
																<div class="input-group-addon">
																	<i class="fa fa-clock-o"></i>
																</div>
															</div>
														</c:when>
			
														<c:when test="${column.fieldType == 'Checkbox'}">
															<input type="checkbox"
																name="${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}subPrices[${recordLoop.index}].subPriceColumns[${columnLoop.index}].columnValue" value="1" ${column.columnValue == "1" ? "checked" : ""} ${model.readonly ? 'disabled' : ''}/>
														</c:when>
													</c:choose>
												</td>
											</c:forEach>
											<td class="nprice-column ${quotationRecord.subPriceTypeDataModel.hideNPrice ? 'hide' : ''}"><input
												name="${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}subPrices[${recordLoop.index}].nPrice" type="text" class="form-control" value="<c:out value="${subPrice.nPrice}" />"
												data-rule-number="true"
												style="min-width: 70px"
												${quotationRecord.subPriceTypeDataModel.hideNPrice ? '' : 'required'}
												${model.readonly ? 'disabled' : ''}/></td>
											<td class="sprice-column ${quotationRecord.subPriceTypeDataModel.hideSPrice ? 'hide' : ''}"><input
												name="${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}subPrices[${recordLoop.index}].sPrice" type="text" class="form-control" value="<c:out value="${subPrice.sPrice}" />"
												data-rule-number="true"
												style="min-width: 70px"
												${quotationRecord.subPriceTypeDataModel.hideSPrice ? '' : 'required'}
												${model.readonly ? 'disabled' : ''}/></td>
											<td class="discount-column ${quotationRecord.subPriceTypeDataModel.hideDiscount ? 'hide' : ''}">
												<div class="input-group btn-sub-price-show-discount-calculator" style="min-width: 100px">
													<input
														type="text" name="${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}subPrices[${recordLoop.index}].discount" class="form-control" value="<c:out value="${subPrice.discount}" />" readonly
														 ${model.readonly ? 'disabled' : ''} />
													<div class="input-group-addon">
														<i class="fa fa-search"></i>
													</div>
												</div>
											</td>
											<td><a class="btn-delete" href="javascript:void(0)"><span class="fa fa-times" aria-hidden="true"></span></a>
												<input name="${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}subPrices[${recordLoop.index}].subPriceTypeId" type="hidden" value="<c:out value="${quotationRecord.subPriceTypeId}" />"/>
												<c:if test="${not model.readonly}">
													<input name="${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}subPrices[${recordLoop.index}].subPriceRecordId" type="hidden" value="<c:out value="${subPrice.subPriceRecordId}" />"/>
												</c:if>
											</td>
										</tr>
										</c:forEach>
									</tbody>
								</c:if>
							</table>
							<script class="tableHeaderTemplate" type="text/html">
							<thead>
							<tr>
								{{#each fields}}
									<th class="field-column" data-field-id="{{this.subPriceFieldId}}">{{this.fieldName}}</th>
								{{/each}}
								<th class="nprice-column {{#if subPriceTypeDataModel.hideNPrice}}hide{{/if}}">N price</th>
								<th class="sprice-column {{#if subPriceTypeDataModel.hideSPrice}}hide{{/if}}">S price</th>
								<th class="discount-column {{#if subPriceTypeDataModel.hideDiscount}}hide{{/if}}">Discount</th>
								<th></th>
							</tr>
							</thead>
							<tbody class="databody"></tbody>
							</script>
							<script class="tableRowTemplate" type="text/html">
							<tr class="datarow">
								{{#each fields}}
									<td class="field-column">
										<input type="hidden" name="${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}subPrices[{{../index}}].subPriceColumns[{{@index}}].subPriceFieldId" value="{{subPriceFieldId}}"/>
										<input type="hidden" name="${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}subPrices[{{../index}}].subPriceColumns[{{@index}}].fieldType" value="{{fieldType}}"/>
										<input type="hidden"
											name="${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}subPrices[{{../index}}].subPriceColumns[{{@index}}].variableName" value="{{variableName}}"/>
									{{#if_eq fieldType 'Text'}}
										<input type="text"
											name="${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}subPrices[{{../index}}].subPriceColumns[{{@index}}].columnValue" class="form-control"
											style="min-width: 70px" />
									{{/if_eq}}

									{{#if_eq fieldType 'Date'}}
										<div class="input-group date date-picker" style="width: 130px">
		       								<input type="text"
												name="${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}subPrices[{{../index}}].subPriceColumns[{{@index}}].columnValue" class="form-control"/>
		       								<div class="input-group-addon">
		       									<i class="fa fa-calendar"></i>
		       								</div>
		       							</div>
									{{/if_eq}}

									{{#if_eq fieldType 'Number'}}
										<input type="text"
											name="${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}subPrices[{{../index}}].subPriceColumns[{{@index}}].columnValue" class="form-control"
											data-rule-number="true" style="min-width: 70px"/>
									{{/if_eq}}

									{{#if_eq fieldType 'Time'}}
		       							<div class="input-group bootstrap-timepicker" style="width: 100px">
											<input
												name="${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}subPrices[{{../index}}].subPriceColumns[{{@index}}].columnValue" type="text" class="form-control time-picker" />
											<div class="input-group-addon">
												<i class="fa fa-clock-o"></i>
											</div>
										</div>
									{{/if_eq}}

									{{#if_eq fieldType 'Checkbox'}}
										<input type="checkbox"
											name="${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}subPrices[{{../index}}].subPriceColumns[{{@index}}].columnValue" value="1" />
									{{/if_eq}}
									</td>
								{{/each}}
								<td class="nprice-column {{#if subPriceTypeDataModel.hideNPrice}}hide{{/if}}"><input
									name="${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}subPrices[{{index}}].nPrice" type="text" class="form-control"
									data-rule-number="true"
									style="min-width: 70px"
									{{#unless subPriceTypeDataModel.hideNPrice}}required{{/unless}}/></td>
								<td class="sprice-column {{#if subPriceTypeDataModel.hideSPrice}}hide{{/if}}"><input
									name="${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}subPrices[{{index}}].sPrice" type="text" class="form-control"
									data-rule-number="true"
									style="min-width: 70px"
									{{#unless subPriceTypeDataModel.hideSPrice}}required{{/unless}}/></td>
								<td class="discount-column {{#if subPriceTypeDataModel.hideDiscount}}hide{{/if}}">
									<div class="input-group btn-sub-price-show-discount-calculator" style="min-width: 100px">
										<input
											type="text" name="${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}subPrices[{{index}}].discount" class="form-control" readonly
											 />
										<div class="input-group-addon">
											<i class="fa fa-search"></i>
										</div>
									</div>
								</td>
								<td><a class="btn-delete" href="javascript:void(0)"><span class="fa fa-times" aria-hidden="true"></span></a>
									<input name="${quotationRecord.backNo ? 'backNoQuotationRecord.' : 'quotationRecord.'}subPrices[{{index}}].subPriceTypeId" type="hidden" value="{{subPriceTypeId}}"/>
								</td>
							</tr>
							</script>
						</div>
					</div>
					<c:if test="${not model.readonly}">
					<div class="form-group">
						<div class="col-sm-4">
							<button type="button" class="btn btn-default btn-add"><i class="fa fa-plus"></i> Add</button>
						</div>
					</div>
					</c:if>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default pull-left" data-dismiss="modal">Close</button>
				<c:if test="${not model.readonly}">
				<button type="button" class="btn btn-primary modal-confirm">Confirm</button>
				<button type="button" class="btn btn-primary modal-clear">Clear</button>
				</c:if>
			</div>
		</div><!-- /.modal-content -->
	</div><!-- /.modal-dialog -->
</div>
</div>