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
			Imputation
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
						<label class="col-md-2 control-label">
							Last Imputed Average Quotation Price:
						</label>
						<label class="col-md-2 form-control-static">
							${preiq.price}
						</label>
					</div>
					<div class="form-group">
						<label class="col-md-2 control-label">
							Imputed Average Quotation Price:
						</label>
						<div class="col-md-6">
							<input class="form-control" name="imputeQuotationPrice" value="${iq.price }"></input>
						</div>
					</div>
					<div class="form-group">
						<label class="col-md-2 control-label">
							Imputed Average Quotation Price Remark:
						</label>
						<div class="col-md-6">
							<input class="form-control" name="imputeQuotationPriceRemark" value="<c:out value="${iq.remark }"/>" ></input>
						</div>
					</div>
					<div class="form-group">
						<label class="col-md-2 control-label">
							Last Imputed Average Unit price:
						</label>
						<label class="col-md-2 form-control-static">
							${preiu.price}
						</label>
					</div>
					<div class="form-group">
						<label class="col-md-2 control-label">
							Imputed Average Unit price:
						</label>
						<div class="col-md-6">
							<input class="form-control" name="imputeUnitPrice" value="${iu.price}"></input>
						</div>
					</div>
					<div class="form-group">
						<label class="col-md-2 control-label">
							Imputed Average Unit Remark:
						</label>
						<div class="col-md-6">
							<input class="form-control" name="imputeUnitPriceRemark" value="<c:out value="${iu.remark }"/>"></input>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>