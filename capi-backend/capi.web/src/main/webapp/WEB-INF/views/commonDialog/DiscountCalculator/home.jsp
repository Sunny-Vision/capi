<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div class="modal fade calculator" role="dialog">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">X</span></button>
				<h4 class="modal-title">Discount Calculator</h4>
			</div>
			<div class="modal-body">
				<!-- content -->
				<form class="form-horizontal calculator">
					<div class="form-group">
						<label class="col-sm-4 control-label">N price</label>
						<div class="col-sm-4">
							<input name="nPrice" type="text" class="form-control" data-rule-number="true" />
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-4 control-label">S price</label>
						<div class="col-sm-4">
							<input name="sPrice" type="text" class="form-control" data-rule-number="true" />
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-4 control-label">Discount</label>
						<div class="col-sm-4">
							<div class="input-group">
								<input name="discount" type="text" class="form-control" disabled/>
								<div class="input-group-addon text-green discount-found-msg hide" title="Formula found">
									<i class="fa fa-check"></i>
								</div>
								<div class="input-group-addon text-red discount-not-found-msg hide" title="Formula not found">
									<i class="fa fa-times"></i>
								</div>
								<div class="input-group-addon btn-formula-lookup">
									<i class="fa fa-search"></i>
								</div>
							</div>
						</div>
						<p class="help-block text-red discount-error-msg hide">Formula definition error</p>
					</div>
					<div class="row button-container">
						<div class="column">
							<c:forEach items="${itemlist.values}" var="value">
								<c:if test="${value != ''}">
								<button type="button" class="btn btn-info btn-square btn-input" value="${value}">${value}</button>
								</c:if>
							</c:forEach>
						</div>
						<div class="column">
							<button type="button" class="btn btn-info btn-square btn-input" value="7">7</button>
							<button type="button" class="btn btn-info btn-square btn-input" value="8">8</button>
							<button type="button" class="btn btn-info btn-square btn-input" value="9">9</button>
							<button type="button" class="btn btn-info btn-square btn-input" value="4">4</button>
							<button type="button" class="btn btn-info btn-square btn-input" value="5">5</button>
							<button type="button" class="btn btn-info btn-square btn-input" value="6">6</button>
							<button type="button" class="btn btn-info btn-square btn-input" value="1">1</button>
							<button type="button" class="btn btn-info btn-square btn-input" value="2">2</button>
							<button type="button" class="btn btn-info btn-square btn-input" value="3">3</button>
							<button type="button" class="btn btn-info btn-square btn-input" value="$">$</button>
							<button type="button" class="btn btn-info btn-square btn-input" value="0">0</button>
							<button type="button" class="btn btn-info btn-square btn-input" value=".">.</button>
						</div>
						<div class="special">
							<button type="button" class="btn btn-info btn-square btn-backspace"><i class="fa fa-arrow-left"></i></button>
							<br/>
							<button type="button" class="btn btn-info btn-square btn-input" value=" ">空格</button>
							<br/>
							<button type="button" class="btn btn-info btn-square btn-input" value=",">,</button>
						</div>
					</div>
					<div class="form-group discount-remark-container">
						<label class="control-label col-md-2">Discount Remark</label>
						<div class="col-md-10">
							<p class="form-control-static discountRemark" style="display: none"></p>
							<div class="input-group discount-remark-input">
								<input name="discountRemark" type="text" class="form-control"/>
								<div class="input-group-addon btn-reason-lookup">
									<i class="fa fa-search"></i>
								</div>
							</div>
						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default pull-left" data-dismiss="modal">Cancel</button>
				<button type="button" class="btn btn-primary modal-confirm">Confirm</button>
			</div>
		</div><!-- /.modal-content -->
	</div><!-- /.modal-dialog -->
</div>