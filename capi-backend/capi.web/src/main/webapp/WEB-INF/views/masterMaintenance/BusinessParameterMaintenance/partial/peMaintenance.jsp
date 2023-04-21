<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div class="row">
	<form method="post" action="<c:url value='/masterMaintenance/BusinessParameterMaintenance/savePEParameters'/>" id="peForm" >
		<div class="col-md-12">
			<div class="form-horizontal">
				<h4>PE Check Setting</h4>
				<div class="col-sm-12">
					<div class="form-group">
						<label class="col-sm-2 control-label">Excluded outlet type</label>
						<div class="col-sm-4">
							<select class="form-control select2-addable col-sm-2" name="excludedOutletType" multiple">
								<c:forEach items="${outletTypes}" var="type">
									<option value="<c:out value="${type.id}" />">
									${type.shortCode} - <c:choose><c:when test="${empty type.chineseName}">${type.englishName}</c:when><c:otherwise>${type.chineseName}</c:otherwise></c:choose>
									</option>
								</c:forEach>
							</select>
						</div>
					</div>
					<div class="form-group">
						<label class="col-md-2 control-label">Purpose</label>
						<div class="col-md-4">
							<select name="purposeIds" class="form-control select2ajaxMultiple filters" multiple data-allow-clear="true"
								data-ajax-url="<c:url value='/masterMaintenance/BusinessParameterMaintenance/queryPurposeSelect2'/>">
								<c:forEach items="${displayModel.peIncludedPurpose}" var="purpose">
									<option value="<c:out value="${purpose.id}" />" selected>${purpose.code} - ${purpose.name}</option>
								</c:forEach>
								</select>
						</div>
					</div>
					
					<div class="form-group">
						<label class="col-sm-2 control-label">Variety Criteria</label>
						<div class="col-sm-8" id="unitCriteriaList">
							<c:forEach items="${displayModel.getpECheckUnitCriteriaList()}" var="unitCriteria" varStatus="counter">
							<div class="box box-primary" data-id="${unitCriteria.getId()}">
								<div class="box-body">
									<div class="form-group">
										<label class="col-sm-4 control-label">Item</label>
										<div class="col-sm-6">
											<div class="input-group">								
												<input type="hidden" class="form-control" name="unitCriteria[<c:out value="${counter.count-1}" />].unitCriteriaId" value="<c:out value="${unitCriteria.getId()}" />" required="">
												
												<select multiple name="unitCriteria[${counter.count-1}].itemIds" class="select2ajaxMultiple filters"
													data-allow-clear="true"
													data-placeholder=""
													data-ajax-url="<c:url value='/masterMaintenance/BusinessParameterMaintenance/queryItemSelect2'/>"
													data-get-key-value-by-ids-url="<c:url value='/masterMaintenance/BusinessParameterMaintenance/queryItemSelectMutiple'/>"
													>
													<c:forEach items="${unitCriteria.items}" var="item">
														<option value="<c:out value="${item.id}" />" selected>${item.code} - 
														<c:choose>
															<c:when test="${empty item.chineseName }">${item.englishName}</c:when>
															<c:otherwise>${item.chineseName}</c:otherwise>
														</c:choose>
														</option>
													</c:forEach>
													<%--
													<option value="<c:out value="${unitCriteria.getUnit().getId()}" />" selected>${unitCriteria.getUnit().getCode()} - ${unitCriteria.getUnit().getChineseName()}</option>
													 --%>
												</select>
												<div class="input-group-addon searchUnitId" data-related-id="${unitCriteria.getId()}" data-bottom-entity-class="Unit">
													<i class="fa fa-search"></i>
												</div>
											</div>
										</div>
									</div>
									<div class="form-group">
										<label class="col-sm-4 control-label">No of months</label>
										<div class="col-sm-6">
											<input type="text" class="form-control digits" name="unitCriteria[${counter.count -1}].noOfMonth" value="<c:out value="${unitCriteria.getNoOfMonth()}" />" required="">
										</div>
									</div>
									<div class="form-group">
										<label class="col-sm-4 control-label">Percentage of quotation</label>
										<div class="col-sm-6">
											<input type="text" class="form-control numbers" name="unitCriteria[${counter.count -1}].percentageOfQuotation" value="<c:out value="${unitCriteria.getQuotationPercentage()}" />" required="" >
										</div>
									</div>
									<div class="form-group">
										<label class="col-sm-4 control-label">PR</label>
										<div class="col-sm-2">
												<select class="form-control" name="unitCriteria[${counter.count -1}].prOperator" required>
													<option value="&gt;" <c:if test="${unitCriteria.getPrSymbol() == '>'}">selected</c:if>>&gt;</option>
													<option value="&lt;" <c:if test="${unitCriteria.getPrSymbol() == '<'}">selected</c:if>>&lt;</option>
													<option value="&gt;=" <c:if test="${unitCriteria.getPrSymbol() == '>='}">selected</c:if>>&gt;=</option>
													<option value="&lt;=" <c:if test="${unitCriteria.getPrSymbol() == '<='}">selected</c:if>>&lt;=</option>
													<option value="=" <c:if test="${unitCriteria.getPrSymbol() == '='}">selected</c:if>>=</option>
												</select>
										</div>
										<div class="col-sm-4">
											<input type="text" class="form-control numbers" name="unitCriteria[${counter.count -1}].prPercentage" value="<c:out value="${unitCriteria.getPrValue()}" />" required="">
										</div>
									</div>
								</div>
								<div class="box-footer">
									<button type="button" class="btn btn-info pull-right" onclick="deleteCriteria($(this))" data-id="${unitCriteria.getId()}">Delete</button>
								</div>
							</div>
							</c:forEach>
						</div>
					</div>
					<div class="form-group">
						<div class="col-md-offset-2">
							<button type="button" class="btn btn-info addCriteria" >Add Variety Criteria</button>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-2 control-label"> </label>
						<div class="col-sm-4">
							<div class="input-group">
								<div class="input-group-addon">
									Exclude assignments which has PE check within
								</div>
								<input type="text" class="form-control digits" name="peCheckMonth"  style="min-width: 78px;" required value="<c:out value="${displayModel.getPeExcludePECheckMonth()}" />"/>
								<div class="input-group-addon">
									months.
								</div>
							</div>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-2 control-label"> </label>
						<div class="col-sm-3">
							<div class="input-group">
								<div class="input-group-addon">
									Select
								</div>
								<input type="text" class="form-control numbers" name="pePercentage" style="min-width: 78px;" required value="<c:out value="${displayModel.getPeSelectPECheckPercentage()}" />"/>
								<div class="input-group-addon">
									% of assignment for PE check.
								</div>
							</div>
						</div>
					</div>
					<hr/>
					<h4>PE Certainty Case</h4>
					<div class="form-group">
							<label class="col-sm-2 control-label">Include new recruitment</label>
							<div class="col-sm-4">
								<input type="checkbox" class="" name="includeNewRecruitment" value="1" <c:if test="${displayModel.getPeIncludeNewRecruitment() == '1'}">checked</c:if>>
							</div>
					</div>
					<div class="form-group">
							<label class="col-sm-2 control-label">Include RUA Case</label>
							<div class="col-sm-4">
								<input type="checkbox" class="" name="IncludeRUACase" value="1" <c:if test="${displayModel.getPeIncludeRUACase() == '1'}">checked</c:if>>
							</div>
					</div>
					<sec:authorize access="hasPermission(#user, 256)">
						<hr/>
						<div class="form-group">
							<button type="submit" class="btn btn-info">Submit</button>
						</div>
					</sec:authorize>
				</div>
			</div>
		</div>
	</form>
</div>