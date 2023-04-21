<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:if test="${refMonth != null}">
	<c:forEach items="${purposeList}" var="purpose">
	<div class="form-group">
		<label for="" class="col-sm-2 control-label">No. of ${purpose.purposeName}</label>
		<div class="col-sm-4" style="padding-top: 7px;">
			<span><c:out value="${purpose.countOfIndoorQuotationRecord}"></c:out></span>
		</div>
	</div>
	</c:forEach>
	<div class="form-group">
		<label for="" class="col-sm-2 control-label">Purpose</label>
		<div class="col-sm-4">
			<select name="purposeId" class="select2 form-control">
				<c:forEach items="${purposeList}" var="purpose">
				<option value="${purpose.purposeId}">${purpose.purposeName}</option>
				</c:forEach>
			</select>
		</div>
	</div>
</c:if>