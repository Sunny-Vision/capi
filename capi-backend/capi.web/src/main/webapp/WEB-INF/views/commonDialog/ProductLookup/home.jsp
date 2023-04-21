<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div class="modal fade" role="dialog">
	<div class="modal-dialog modal-xlg">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">X</span></button>
				<h4 class="modal-title">Product Lookup</h4>
			</div>
			<div class="modal-body">
				<!-- content -->
				<form class="form-horizontal table-filter">
					<div class="row">
						<label class="col-md-1 control-label">Reviewed</label>
						<div class="col-md-2">
							<select class="form-control filters" name="reviewed">
								<option value=""></option>
								<option value="true">Yes</option>
								<option value="false">No</option>
							</select>
						</div>
						
						<div class="product-group-container" style="display:none">
							<label class="col-md-2 control-label">Product Type</label>
							<div class="col-md-3">
								<select class="form-control filters" name="productGroup" style="width:100%" data-allow-clear="true"
									data-placeholder="Select"
									data-ajax-url="<c:url value='/commonDialog/ProductLookup/queryProductGroupSelect2'/>"></select>
							</div>
						</div>
					</div>
				</form>
				<hr/>
				
				<table class="table table-striped table-bordered table-hover datatable">
					<thead>
					<tr>
						<th class="text-center action select-multiple" ><input class="select_all" type="checkbox" /></th>
						<th>Product ID</th>
						<th>Barcode</th>
						<th>No. of quotations</th>
						<th>Attributes</th>
						<th>Remarks</th>
						<th>Status</th>
						<th>Reviewed</th>
						<th>Product Group</th>
						<th class="action view-product"></th>
						<th class="action select-single"></th>
					</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default pull-left" data-dismiss="modal">Close</button>
			</div>
		</div><!-- /.modal-content -->
	</div><!-- /.modal-dialog -->
</div>