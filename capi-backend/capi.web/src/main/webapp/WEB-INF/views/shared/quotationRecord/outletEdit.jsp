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
<div class="box box-primary outlet-edit">
	<div class="box-header with-border">
		<h3 class="box-title">
			Outlet 
			<span class="new-outlet hide text-danger">(New Firm)</span>
			<span class="outlet-header-name" style="display:none">
				<strong class="name">Name:</strong> ${outlet.name}
				<strong class="outletTypeFormatted">Outlet Type:</strong> ${outlet.outletTypeFormatted}
				<strong>Firm Status:</strong> <span class="firm-status"></span>
			</span>
		</h3>
		<div class="box-tools pull-right">
			<button class="btn btn-box-tool btn-outlet-collapse" type="button" data-widget="collapse">
				<i class="fa fa-minus"></i>
			</button>
		</div>
	</div>
	<div class="box-body">
		<div class="form-horizontal">
			<div class="row">
				<div class="col-md-2">
					<c:choose>
						<c:when test="${outlet.outletImagePath != null}">
							<img class="outlet-image img-responsive viewer"
								src="<c:url value='/shared/General/getOutletImage'/>?id=${outlet.outletId}&amp;bust=${niceDate.time}" />
							<button style="width: 60%" type="button" id="deleteOutletImage" >
								<span>Remove</span>
							</button>
						</c:when>
						<c:otherwise>
							<img class="outlet-image img-responsive"
								src="<c:url value='/resources/images/dummyphoto.png'/>" />
						</c:otherwise>
					</c:choose>
					<input name="outletImage" type="file" />
				</div>
				<div class="col-md-10">
					<input name="outletId" type="hidden" value="<c:out value="${outlet.outletId}"/>"/>
					<div class="form-group">
						<label class="col-md-2 control-label"></label>
						<div class="col-sm-4">
							<p class="form-control-static outletId"></p>
						</div>
						<label class="col-md-2 control-label">Last Contact:</label>
						<div class="col-sm-4">
							<input name="lastContact" class="form-control" value="<c:out value="${outlet.lastContact}"/>"/>
						</div>
					</div>
					<div class="form-group">
						<label class="col-md-2 control-label">Reference No:</label>
						<div class="col-sm-4">
							<p class="form-control-static outletId">${outlet.outletId}</p>
						</div>
						<label class="col-md-2 control-label">Main Contact:</label>
						<div class="col-sm-4">
							<input name="mainContact" class="form-control" value="<c:out value="${outlet.mainContact}"/>"/>
						</div>
					</div>
					<div class="form-group">
						<label class="col-md-2 control-label">Firm Code:</label>
						<div class="col-sm-4">
							<p class="form-control-static firmCode">${outlet.firmCode}</p>
						</div>
						<label class="col-md-2 control-label">Telephone No.:</label>
						<div class="col-sm-4">
							<input name="tel" class="form-control" value="<c:out value="${outlet.tel}"/>"/>
						</div>
					</div>
					<div class="form-group">
						<label class="col-md-2 control-label">Name:</label>
						<div class="col-sm-4">
							<input id="language" name="name" class="form-control" value="<c:out value="${outlet.name}"/>" required/>
						</div>
						<label class="col-md-2 control-label">Fax No.:</label>
						<div class="col-sm-4">
							<input name="fax" class="form-control" value="<c:out value="${outlet.fax}"/>"/>
						</div>
					</div>
					<div class="form-group">
						<label class="col-md-2 control-label">Outlet Type:</label>
						<div class="col-sm-4">
							<p class="form-control-static outletTypeFormatted">${outlet.outletTypeFormatted}</p>
						</div>
						<label class="col-md-2 control-label">Opening Hours:</label>
						<c:set var="openingStartTime" value="${fn:substring(outlet.openingStartTime, 0, 5)}" />
						<c:set var="openingEndTime" value="${fn:substring(outlet.openingEndTime, 0, 5)}" />
						<div class="col-sm-2">
							<div class="input-group bootstrap-timepicker">
								<input name="openingStartTime" type="text" class="form-control timepicker" value="<c:out value="${openingStartTime}" />" />
								<div class="input-group-addon">
									<i class="fa fa-clock-o"></i>
								</div>
							</div>
						</div>
						<div class="col-sm-2">
							<div class="input-group bootstrap-timepicker">
								<input name="openingEndTime" type="text" class="form-control timepicker" value="<c:out value="${openingEndTime}" />" />
								<div class="input-group-addon">
									<i class="fa fa-clock-o"></i>
								</div>
							</div>
						</div>
					</div>
					<div class="form-group">
						<label class="col-md-2 control-label">District:</label>
						<div class="col-sm-4">
							<p class="form-control-static districtFormatted">${outlet.districtFormatted}</p>
						</div>
						<label class="col-md-2 control-label">Opening Hours 2:</label>
						<c:set var="openingStartTime" value="${fn:substring(outlet.openingStartTime2, 0, 5)}" />
						<c:set var="openingEndTime" value="${fn:substring(outlet.openingEndTime2, 0, 5)}" />
						<div class="col-sm-2">
							<div class="input-group bootstrap-timepicker">
								<input name="openingStartTime2" type="text" class="form-control timepicker" value="<c:out value="${openingStartTime}" />" />
								<div class="input-group-addon">
									<i class="fa fa-clock-o"></i>
								</div>
							</div>
						</div>
						<div class="col-sm-2">
							<div class="input-group bootstrap-timepicker">
								<input name="openingEndTime2" type="text" class="form-control timepicker" value="<c:out value="${openingEndTime}" />" />
								<div class="input-group-addon">
									<i class="fa fa-clock-o"></i>
								</div>
							</div>
						</div>
					</div>
					<div class="form-group">
						<label class="col-md-2 control-label">TPU:</label>
						<div class="col-sm-4">
							<select name="tpuId" class="form-control select2ajax"
								data-ajax-url="<c:url value='/shared/General/queryTpuSingleDistrictSelect2'/>?districtId=<c:out value="${outlet.districtId}"/>">
								<option value="<c:out value="${outlet.tpu.id}"/>">${outlet.tpu.code}</option>
							</select>
						</div>
						<label class="col-md-2 control-label">Convenient Time:</label>
						<c:set var="convenientStartTime" value="${fn:substring(outlet.convenientStartTime, 0, 5)}" />
						<c:set var="convenientEndTime" value="${fn:substring(outlet.convenientEndTime, 0, 5)}" />
						<div class="col-sm-2">
							<div class="input-group bootstrap-timepicker">
								<input name="convenientStartTime" type="text" class="form-control timepicker" value="<c:out value="${convenientStartTime}" />" />
								<div class="input-group-addon">
									<i class="fa fa-clock-o"></i>
								</div>
							</div>
						</div>
						<div class="col-sm-2">
							<div class="input-group bootstrap-timepicker">
								<input name="convenientEndTime" type="text" class="form-control timepicker" value="<c:out value="${convenientEndTime}" />" />
								<div class="input-group-addon">
									<i class="fa fa-clock-o"></i>
								</div>
							</div>
						</div>
					</div>
					<div class="form-group">
						<label class="col-md-2 control-label">Map Address:</label>
						<div class="col-sm-4">
							<input name="streetAddress" class="form-control" value="<c:out value="${outlet.streetAddress}"/>" required/>
						</div>
						<label class="col-md-2 control-label">Convenient Time 2:</label>
						<c:set var="convenientStartTime" value="${fn:substring(outlet.convenientStartTime2, 0, 5)}" />
						<c:set var="convenientEndTime" value="${fn:substring(outlet.convenientEndTime2, 0, 5)}" />
						<div class="col-sm-2">
							<div class="input-group bootstrap-timepicker">
								<input name="convenientStartTime2" type="text" class="form-control timepicker" value="<c:out value="${convenientStartTime}" />" />
								<div class="input-group-addon">
									<i class="fa fa-clock-o"></i>
								</div>
							</div>
						</div>
						<div class="col-sm-2">
							<div class="input-group bootstrap-timepicker">
								<input name="convenientEndTime2" type="text" class="form-control timepicker" value="<c:out value="${convenientEndTime}" />" />
								<div class="input-group-addon">
									<i class="fa fa-clock-o"></i>
								</div>
							</div>
						</div>
					</div>
					<div class="form-group">
						<label class="col-md-2 control-label">Detail Address:</label>
						<div class="col-sm-4">
							<input name="detailAddress" class="form-control" value="<c:out value="${outlet.detailAddress}"/>"/>
						</div>
						<label class="col-md-2 control-label">Website:</label>
						<div class="col-sm-4">
							<input name="webSite" class="form-control" value="<c:out value="${outlet.webSite}"/>"/>
						</div>
					</div>
					<div class="form-group">
						<label class="col-md-2 control-label">BR Code:</label>
						<div class="col-sm-4">
							<input name="brCode" class="form-control" value="<c:out value="${outlet.brCode}"/>"/>
						</div>
						<label class="col-md-2 control-label">Market Type:</label>
						<div class="col-sm-4">
							<p class="form-control-static">
								<c:choose>
								<c:when test="${outlet.outletMarketType == 1}">Market</c:when>
								<c:when test="${outlet.outletMarketType == 2}">Non-Market</c:when>
								</c:choose>
								<input type="hidden" name="outletMarketType" value="${outlet.outletMarketType}" />
							</p>							
						</div>
					</div>
					<div class="form-group">
						<label class="col-md-2 control-label">Outlet Remarks:</label>
						<div class="col-sm-4">
							<input name="remark" class="form-control" value="<c:out value="${outlet.remark}"/>"/>
						</div>
						<label class="col-md-2 control-label">Discount Remarks:</label>
						<div class="col-sm-4">
							<input name="discountRemark" class="form-control" value="<c:out value="${outlet.discountRemark}"/>"/>
						</div>
					</div>
					<div class="form-group">
						<label class="col-md-2 control-label">Firm Status:</label>
						<div class="col-sm-4">
							<select name="firmStatus" class="form-control">
								<option value="1" selected>Enumeration (EN)</option>
								<option value="2">Closed (CL)</option>
								<option value="3">Move (MV)</option>
								<option value="4">Not Suitable (NS)</option>
								<option value="5">Refusal (NR)</option>
								<option value="6">Wrong Outlet (WO)</option>
								<option value="7">Door Lock (DL)</option>
								<option value="8">Non-contact (NC)</option>
								<option value="9">In Progress (IP)</option>
								<option value="10">Duplication (DU)</option>
							</select>
						</div>
						<label class="col-md-2 control-label">Collection Method:</label>
						<div class="col-sm-4">
							<select name="collectionMethod" class="form-control">
								<option value="1" <c:if test="${outlet.collectionMethod == 1}">selected</c:if>>Field Visit</option>
								<option value="2" <c:if test="${outlet.collectionMethod == 2}">selected</c:if>>Telephone</option>
								<option value="3" <c:if test="${outlet.collectionMethod == 3}">selected</c:if>>Fax</option>
								<option value="4" <c:if test="${outlet.collectionMethod == 4}">selected</c:if>>Others</option>
							</select>
						</div>
					</div>
				</div>
			</div>
		</div>
		
		<div class="box-footer">
			<button type="button" class="btn btn-info btn-map-outlet hide">Map Outlet</button>
			<button type="submit" class="btn btn-info btn-save">Save Outlet</button>
			<button type="button" class="btn btn-info btn-attachment-lookup">Attachment Lookup</button>
			<button type="button" class="btn btn-info btn-batch-code hide">Show existing Batch code in the firm</button>
		</div>
	</div>
</div>