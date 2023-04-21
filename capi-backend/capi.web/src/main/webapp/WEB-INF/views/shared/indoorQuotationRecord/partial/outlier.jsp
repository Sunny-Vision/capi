<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%
/**
 * Display outlet section
 * 
 * Controller permission required:
 * /shared/General/getOutletImage
 */
%>
<div class="box box-primary  collapsed-box">
	<div class="box-header with-border">
		<h3 class="box-title">
			Outlier
		</h3>
		<div class="box-tools pull-right">
			<button class="btn btn-box-tool btn-header-collapse" type="button" data-widget="collapse">
				<i class="fa fa-plus"></i>
			</button>
		</div>
	</div>
	<div class="box-body" style="display: none;">
		<div class="form-horizontal">
			<div class="row">
				<div class="col-md-12">
					<div class="form-group">
						<label class="col-md-2">
							<input type="checkbox" name="outlier" value="1" <c:if test="${indoorQuotationRecord.isOutlier()}">checked</c:if> /> Outlier
						</label>
						<div class="col-md-6">
							<input class="form-control" name="outlierRemark" value="<c:out value="${indoorQuotationRecord.outlierRemark}"/>" <c:if test="${!indoorQuotationRecord.isOutlier()}">disabled</c:if> ></input>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>