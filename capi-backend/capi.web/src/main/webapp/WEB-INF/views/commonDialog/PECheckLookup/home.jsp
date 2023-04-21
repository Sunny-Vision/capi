<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div class="modal modal-wide fade" role="dialog">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">X</span></button>
				<h4 class="modal-title">PE Check Lookup</h4>
			</div>
			<div class="modal-body">
				<!-- content -->
				<div class="form-horizontal">
					<div class="row" style="margin-bottom: 5px">
						<label class="col-xs-1 control-label">Outlet Type</label>
						<div class="col-xs-3">
							<select class="form-control select2 filters" name="outletTypeId" multiple="multiple"
								data-ajax-url="<c:url value='/commonDialog/PECheckLookup/queryOutletTypeSelect2'/>"></select>
						</div>
						<label class="col-xs-1 control-label">District</label>
						<div class="col-xs-3">
							<select class="form-control select2 filters" name="districtId" multiple="multiple"
								data-ajax-url="<c:url value='/commonDialog/PECheckLookup/queryDistrictSelect2'/>"></select>
						</div>
						<label class="col-xs-1 control-label">TPU</label>
						<div class="col-xs-3">
							<select class="form-control select2 filters" name="tpuId" multiple="multiple"
								data-ajax-url="<c:url value='/commonDialog/PECheckLookup/queryTpuSelect2'/>"></select>
						</div>

					</div>
				</div>
				<hr/>
				<table class="table table-striped table-bordered table-hover datatable">
					<thead>
					<tr>
						<th class="text-center action select-multiple" ><input class="select_all" type="checkbox" /></th>
						<th>Firm</th>
						<th>District</th>
						<th>TPU</th>
						<th>Deadline</th>
						<th>Address</th>
						<th>No. of Quotation</th>
						<th>Convenient Time</th>
						<th>Outlet Remark</th>
						<th>Field Officer Code</th>
						<th>Chinese Name</th>
						<th>English Name</th>
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