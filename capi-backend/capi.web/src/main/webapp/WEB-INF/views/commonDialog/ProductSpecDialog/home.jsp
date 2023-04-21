<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div class="modal fade" role="dialog">
	<div class="modal-dialog">
		<div class="modal-content">
			<form action="" method="post" role="form"
				enctype="multipart/form-data">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">X</span></button>
				<h4 class="modal-title">Product Specification</h4>
			</div>
			<div class="modal-body">
				<!-- content -->
				<div class="form-horizontal">
					<div class="form-group">
						<label class="col-sm-6 control-label">Country of origin:</label>
						<div class="col-sm-6">
							<p class="form-control-static">${product.countryOfOrigin}</p>
						</div>
					</div>
					
					<c:forEach items="${product.attributes}" var="attr" varStatus="attrLoop">
					<div class="form-group">
						<label class="col-md-6 control-label">${attr.name}:</label>
						<div class="col-md-6">
							<p class="form-control-static product-form-attribute">${attr.value}</p>
						</div>
					</div>
					</c:forEach>
					
					<div class="form-group">
						<label class="col-md-6 control-label">Barcode:</label>
						<div class="col-md-6">
							<p class="form-control-static barcode">${product.barcode}</p>
						</div>
					</div>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default pull-left" data-dismiss="modal">Close</button>
			</div>
			</form>
		</div><!-- /.modal-content -->
	</div><!-- /.modal-dialog -->
</div>