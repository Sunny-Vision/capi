<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div class="modal fade" role="dialog">
	<div class="modal-dialog modal-xlg">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">X</span></button>
				<h4 class="modal-title">Search Assignment</h4>
			</div>
			<div class="modal-body">
				<!-- content -->
				<form class="form-horizontal table-filter">
					<div class="row" style="margin-top: 10px;">
						<label class="col-md-2 control-label">TPU</label>
						<div class="col-md-3">
							<select name="tpuId" class="form-control select2ajax filters"
								multiple="multiple"
								data-allow-clear="true"
								data-close-on-select="false"
								data-ajax-url="<c:url value='/commonDialog/AssignmentAllocationLookup/queryTpuSelect2'/>"
							></select>
						</div>
						<label class="col-md-2 control-label">Outlet Type</label>
						<div class="col-md-3">
							<select name="outletTypeId" class="form-control select2ajax filters"
								data-allow-clear="true"
								data-ajax-url="<c:url value='/commonDialog/AssignmentAllocationLookup/queryOutletTypeSelect2'/>"
							></select>
						</div>
					</div>
					<div class="row" style="margin-top: 10px;">
						<label class="col-md-2 control-label">District</label>
						<div class="col-md-3">
							<select name="districtId" class="form-control select2ajax filters"
								data-allow-clear="true"
								data-ajax-url="<c:url value='/commonDialog/AssignmentAllocationLookup/queryDistrictSelect2'/>"></select>
						</div>
						<label class="col-md-2 control-label">Batch Code</label>
						<div class="col-md-3">
							<select name="batchId" class="form-control select2ajax filters"
								data-allow-clear="true"
								data-ajax-url="<c:url value='/commonDialog/AssignmentAllocationLookup/queryBatchSelect2'/>"></select>
						</div>
					</div>
				</form>
				
				<hr/>
				
				<table class="table table-striped table-bordered table-hover datatable">
					<thead>
					<tr>
						<th class="text-center action select-multiple" ><input class="select_all" type="checkbox" /></th>
						<th>Reference No</th>
						<th>Batch Code</th>
						<th>Firm</th>
						<th>District</th>
						<th>TPU</th>
						<th>Address</th>
						<th>Start Date</th>
						<th>End Date</th>
						<th>No. of Quotation</th>
						<th>Required Man-Day</th>
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