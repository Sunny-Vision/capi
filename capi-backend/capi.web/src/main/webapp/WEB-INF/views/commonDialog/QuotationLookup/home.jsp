<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div class="modal fade" role="dialog">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">X</span></button>
				<h4 class="modal-title">Quotation Lookup</h4>
			</div>
			<div class="modal-body">
				<!-- content -->
				<form class="form-horizontal">
					<div class="row">
						<label class="col-xs-2 control-label">Purpose</label>
						<div class="col-xs-4">
							<select class=" form-control filters select2" name="purposeId" multiple="multiple">
								<c:forEach var="purpose" items="${purposes}">
									<option value="<c:out value="${purpose.id}" />">${purpose.code} - ${purpose.name}</option>
								</c:forEach>
							</select>
						</div>
						<label class="col-xs-2 control-label">Outlet Type</label>
						<div class="col-xs-4">
							<select class="col-xs-4 form-control filters select2" name="outletTypeId" multiple="multiple">
								<c:forEach var="outletType" items="${outletTypes}">
									<option value="<c:out value="${outletType.id}" />">${outletType.shortCode} - ${outletType.englishName}</option>
								</c:forEach>
							</select>
						</div>
					</div>
				</form>
				<hr/>
				
				<table class="table table-striped table-bordered table-hover datatable">
					<thead>
					<tr>
						<th class="text-center action select-multiple" ><input class="select_all" type="checkbox" /></th>
						<th>Quotation ID</th>
						<th>Survey</th>
						<th>Variety Code</th>
						<th>Variety Name</th>
						<th>Product ID</th>
						<th>Product Attribute 1</th>
						<th>Firm ID</th>
						<th>Firm Name</th>
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