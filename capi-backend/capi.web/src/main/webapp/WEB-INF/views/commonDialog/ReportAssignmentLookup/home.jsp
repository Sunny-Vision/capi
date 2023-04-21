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
							<select class="form-control select2 filters" id="tpuIds" name="tpuIds" multiple="multiple">
								<c:forEach var="tpu" items="${tpus}">
									<option value="<c:out value="${tpu.tpuId}" />">${tpu.code}</option>
								</c:forEach>
							</select>
						</div>
						<label class="col-md-2 control-label">Outlet Type</label>
						<div class="col-md-3">
							<select name="outletTypeId" class="form-control select2ajax filters"
								data-ajax-url="<c:url value='/commonDialog/ReportAssignmentLookup/queryOutletTypeSelect2'/>"
							></select>
						</div>
					</div>
					<div class="row" style="margin-top: 10px;">
						<label class="col-md-2 control-label">District</label>
						<div class="col-md-3">
							<select name="districtId" class="form-control select2ajax filters" id="districtId"
								data-ajax-url="<c:url value='/commonDialog/ReportAssignmentLookup/queryDistrictSelect2'/>"/></select>
						</div>
						<label class="col-md-2 control-label">Batch Code</label>
						<div class="col-md-3">
							<select name="batchId" class="form-control select2ajax filters" id="batchId"
								data-ajax-url="<c:url value='/commonDialog/ReportAssignmentLookup/queryBatchSelect2'/>"/></select>
						</div>
					</div>
				</form>
				
				<hr/>
				
				<table class="table table-striped table-bordered table-hover datatable">
					<thead>
					<tr>
						<th class="text-center action select-multiple" ><input class="select_all" type="checkbox" /></th>
						<th>Assignment Id</th>
						<th>Collection Date</th>
						<th>Start Date</th>
						<th>End Date</th>
						<th>Firm</th>
						<th>District</th>
						<th>Tpu</th>
						<th>No. of Quotation</th>
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