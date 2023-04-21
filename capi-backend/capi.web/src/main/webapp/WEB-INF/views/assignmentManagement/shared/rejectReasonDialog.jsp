<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<div class="modal fade" id="rejectReasonDialog" tabindex="-1" role="dialog" aria-labelledby="dialogLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div  class="clearfix overlay" data-bind="loading:isLoading, visible:isLoading" ></div>
			<form>
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
					<h4 class="modal-title" id="dialogLabel" data-bind="text:formTitle"></h4>
				</div>
				<div class="modal-body form-horizontal">
					<div class="form-group">
						<div class="col-md-3 control-label">Reject Reason : </div>
					</div>
					<div class="form-group">
						<div class="col-md-10 col-md-offset-1">
							<input name="rejectReason" type="text" class="form-control" value="" maxlength="4000" required />
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
					<button type="submit" class="btn btn-primary" data-loading-text="Loading...">Submit</button>
				</div>
			</form>
		</div>
	</div>
</div>