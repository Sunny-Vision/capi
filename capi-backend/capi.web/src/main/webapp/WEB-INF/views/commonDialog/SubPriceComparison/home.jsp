<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
<t:layout plainLayout="true">
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
	</jsp:attribute>
	<jsp:attribute name="header">
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>	
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Sub Price Comparison</h1>
        </section>
        
        <section class="content">
        	<div class="row">
				<div class="col-md-12" >
				<!-- content -->
					<div class="box" >
						<div class="box-header with-border">
							<h3 class="box-title">Date: <c:out value="${model.date1}"/></h3>
						</div>
						<div class="box-body">				
							<table class="table 1table-striped1 table-bordered table-hover" >
								<thead>
								<tr>
									<c:forEach items="${model.fieldList1}" var="field">
										<th>${field.fieldName}</th>
									</c:forEach>
									<c:if test="${!model.type1.hideNPrice }" >
										<th>N Price</th>
									</c:if>
									<c:if test="${!model.type1.hideSPrice }" >
										<th>S Price</th>
									</c:if>
									<c:if test="${!model.type1.hideDiscount }" >
										<th>Discount</th>
									</c:if>
								</tr>
								</thead>
								<tbody>
									<c:forEach items="${model.rows1}" var="row" >
										<tr>
											<c:forEach items="${row.columns}" var="column">
												<td class="text-center">
													<c:choose>
														<c:when test="${column.subPriceFieldMapping.subPriceField.fieldType eq 'Date'}">
															<c:out value="${commonService.formatDate(column.columnValue)}" />
														</c:when>
														<c:when test="${column.subPriceFieldMapping.subPriceField.fieldType eq 'Time'}">
															<c:out value="${commonService.formatTime(column.columnValue)}" />
														</c:when>
														<c:when test="${column.subPriceFieldMapping.subPriceField.fieldType eq 'Checkbox'}">
															<c:choose>
																<c:when test="${column.columnValue eq '1' }" >Y</c:when>
																<c:otherwise>N</c:otherwise>
															</c:choose>
														</c:when>
														<c:otherwise>
															<c:out value="${column.columnValue}" />
														</c:otherwise>
													</c:choose>
												</td>
											</c:forEach>
											<c:if test="${!model.type1.hideNPrice }" >
												<td class="text-center"><c:out value="${row.nPrice}" /></td>
											</c:if>
											<c:if test="${!model.type1.hideSPrice }" >
												<td class="text-center"><c:out value="${row.sPrice}" /></td>
											</c:if>
											<c:if test="${!model.type1.hideDiscount }" >
												<td class="text-center"><c:out value="${row.discount}" /></td>
											</c:if>
										</tr>
									</c:forEach>
								</tbody>
							</table>
							
						</div>
					</div>
					
					
					
					<div class="box" >
						<div class="box-header with-border">
							<h3 class="box-title">Date: <c:out value="${model.date2}"/></h3>
						</div>
						<div class="box-body">				
							<table class="table 1table-striped1 table-bordered table-hover" >
								<thead>
								<tr>
									<c:forEach items="${model.fieldList2}" var="field">
										<th>${field.fieldName}</th>
									</c:forEach>
									<c:if test="${!model.type2.hideNPrice }" >
										<th>N Price</th>
									</c:if>
									<c:if test="${!model.type2.hideSPrice }" >
										<th>S Price</th>
									</c:if>
									<c:if test="${!model.type2.hideDiscount }" >
										<th>Discount</th>
									</c:if>
								</tr>
								</thead>
								<tbody>
									<c:forEach items="${model.rows2}" var="row" >
										<tr>
											<c:forEach items="${row.columns}" var="column">
												<td class="text-center">
													<c:choose>
														<c:when test="${column.subPriceFieldMapping.subPriceField.fieldType eq 'Date'}">
															<c:out value="${commonService.formatDate(column.columnValue)}" />
														</c:when>
														<c:when test="${column.subPriceFieldMapping.subPriceField.fieldType eq 'Time'}">
															<c:out value="${commonService.formatTime(column.columnValue)}" />
														</c:when>
														<c:when test="${column.subPriceFieldMapping.subPriceField.fieldType eq 'Checkbox'}">
															<c:choose>
																<c:when test="${column.columnValue eq '1' }" >Y</c:when>
																<c:otherwise>N</c:otherwise>
															</c:choose>
														</c:when>
														<c:otherwise>
															<c:out value="${column.columnValue}" />
														</c:otherwise>
													</c:choose>
												</td>
											</c:forEach>
											<c:if test="${!model.type2.hideNPrice }" >
												<td class="text-center"><c:out value="${row.nPrice}" /></td>
											</c:if>
											<c:if test="${!model.type2.hideSPrice }" >
												<td class="text-center"><c:out value="${row.sPrice}" /></td>
											</c:if>
											<c:if test="${!model.type2.hideDiscount }" >
												<td class="text-center"><c:out value="${row.discount}" /></td>
											</c:if>
										</tr>
									</c:forEach>
								</tbody>
							</table>
							
						</div>
					</div>
					
					
					
					
					
				</div>
			</div>
        </section>		
	</jsp:body>
</t:layout>