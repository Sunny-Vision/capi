<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div class="modal fade" role="dialog">
	<div class="modal-dialog modal-xlg">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">X</span></button>
				<h4 class="modal-title">Outlet Lookup</h4>
			</div>
			<div class="modal-body">
				<!-- content -->
				<div class="form-horizontal">
					<div class="row" style="margin-bottom: 5px">
						<label class="col-xs-1 control-label">Outlet Type</label>
						<div class="col-xs-3">
							<select class="form-control select2 filters " name="outletTypeId" multiple="multiple"
							data-ajax-url="<c:url value='/commonDialog/OutletLookup/queryOutletTypeSelect2'/>">
							<%--
								<c:forEach var="outletType" items="${outletTypes}">
									<option value="<c:out value="${outletType.id}" />">${outletType.shortCode} - ${outletType.englishName}</option>
								</c:forEach>
							 --%>
							</select>
						</div>
						<label class="col-xs-1 control-label">District</label>
						<div class="col-xs-2">
							<select class="form-control select2 filters" name="districtId" multiple="multiple"
								data-ajax-url="<c:url value='/commonDialog/OutletLookup/queryDistrictSelect2'/>"></select>
						</div>
						<label class="col-xs-1 control-label">TPU</label>
						<div class="col-xs-3">
							<select class="form-control select2 filters" name="tpuId" multiple="multiple"
								data-ajax-url="<c:url value='/commonDialog/OutletLookup/queryTpuSelect2'/>"></select>
						</div>
					</div>
				</div>
				<hr/>
				<table class="table table-striped table-bordered table-hover datatable">
					<thead>
					<tr>
						<th class="text-center action select-multiple" ><input class="select_all" type="checkbox" /></th>
						<th>Firm Code</th>
						<th>Firm Name</th>
						<th>District Code</th>
						<th>TPU</th>
						<th>Active Outlet</th>
						<th>No. of Quotation</th>
						<th>Street Address</th>
						<th>Detail Address</th>
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