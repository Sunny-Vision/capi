<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:if test="${outlet == null}">
	<c:set var="outlet" value="${model.outlet}" scope="request" />
</c:if>
<script>
	$(function() { 
		if ($.fn.outletAttachmentDialog != null) {
			$('.btn-attachment-lookup-readonly').outletAttachmentDialog();
		}
	});
</script>
<div class="box no-border" >
	<div class="box-body ">
		<div class="row" style="margin-left: 0px; margin-right: 0px;">
			<div class="col-md-2">
				<c:choose>
					<c:when test="${outlet.outletImagePath != null}">
						<img class="img-responsive viewer"
							src="<c:url value='/shared/General/getOutletImage'/>?id=${outlet.outletId}&amp;bust=${niceDate.time}" />
					</c:when>
					<c:otherwise>
						<img class="img-responsive"
							src="<c:url value='/resources/images/dummyphoto.png'/>" />
					</c:otherwise>
				</c:choose>
			</div>
			<div class="col-md-10">
				<div class="form-group">
					<label class="col-md-2 control-label">Reference No:</label>
					<div class="col-sm-4">
						<p class="form-control-static">${outlet.outletId}</p>
					</div>
					<label class="col-md-2 control-label">Last Contact:</label>
					<div class="col-sm-4">
						<p class="form-control-static">${outlet.lastContact}</p>
					</div>
				</div>
				<div class="form-group">
					<label class="col-md-2 control-label">Firm Code:</label>
					<div class="col-sm-4">
						<p class="form-control-static">${outlet.firmCode}</p>
					</div>
					<label class="col-md-2 control-label">Telephone No.:</label>
					<div class="col-sm-4">
						<p class="form-control-static">${outlet.tel}</p>
					</div>
				</div>
				<div class="form-group">
					<label class="col-md-2 control-label">Name:</label>
					<div class="col-sm-4">
						<p class="form-control-static">${outlet.name}</p>
					</div>
					<label class="col-md-2 control-label">Fax No.:</label>
					<div class="col-sm-4">
						<p class="form-control-static">${outlet.fax}</p>
					</div>
				</div>
				<div class="form-group">
					<label class="col-md-2 control-label">Outlet Type:</label>
					<div class="col-sm-4">
						<p class="form-control-static">${outlet.outletTypeFormatted}</p>
					</div>
					<label class="col-md-2 control-label">Opening Hours:</label>
					<div class="col-sm-4">
						<p class="form-control-static">
							<c:set var="openingStartTime" value="${fn:substring(outlet.openingStartTime, 0, 5)}" />
							<c:set var="openingEndTime" value="${fn:substring(outlet.openingEndTime, 0, 5)}" />
							${openingStartTime} - ${openingEndTime}
							<c:if test="${outlet.openingStartTime2 != null}">
								<c:set var="openingStartTime" value="${fn:substring(outlet.openingStartTime2, 0, 5)}" />
								<c:set var="openingEndTime" value="${fn:substring(outlet.openingEndTime2, 0, 5)}" />
								, ${openingStartTime} - ${openingEndTime}
							</c:if>
						</p>
					</div>
				</div>
				<div class="form-group">
					<label class="col-md-2 control-label">District:</label>
					<div class="col-sm-4">
						<p class="form-control-static">${outlet.districtFormatted}</p>
					</div>
					<label class="col-md-2 control-label">Convenient Time:</label>
					<div class="col-sm-4">
						<p class="form-control-static">
							<c:set var="convenientStartTime" value="${fn:substring(outlet.convenientStartTime, 0, 5)}" />
							<c:set var="convenientEndTime" value="${fn:substring(outlet.convenientEndTime, 0, 5)}" />
							${convenientStartTime} - ${convenientEndTime}
							<c:if test="${outlet.convenientStartTime2 != null}">
								<c:set var="convenientStartTime" value="${fn:substring(outlet.convenientStartTime2, 0, 5)}" />
								<c:set var="convenientEndTime" value="${fn:substring(outlet.convenientEndTime2, 0, 5)}" />
								, ${convenientStartTime} - ${convenientEndTime}
							</c:if>
						</p>
					</div>
				</div>
				<div class="form-group">
					<label class="col-md-2 control-label">TPU:</label>
					<div class="col-sm-4">
						<p class="form-control-static">${outlet.tpu.code} - ${outlet.tpu.description}</p>
					</div>
					<label class="col-md-2 control-label">Website:</label>
					<div class="col-sm-4">
						<p class="form-control-static"><a href="${outlet.webSite}" target="_blank">${outlet.webSite}</a></p>
					</div>
				</div>
				<div class="form-group">
					<label class="col-md-2 control-label">Map Address:</label>
					<div class="col-sm-4">
						<p class="form-control-static">${outlet.streetAddress}</p>
					</div>
					<label class="col-md-2 control-label">Detail Address:</label>
					<div class="col-sm-4">
						<p class="form-control-static">${outlet.detailAddress}</p>
					</div>
				</div>
				
				<div class="form-group">
					<label class="col-md-2 control-label">BR Code:</label>
					<div class="col-sm-4">
						<p class="form-control-static">${outlet.brCode}</p>
					</div>
				</div>
				<div class="form-group">
					<label class="col-md-2 control-label">Outlet Remark:</label>
					<div class="col-sm-4">
						<p class="form-control-static">${outlet.remark}</p>
					</div>
				</div>
			</div>
		</div>
		<div class="box-footer">
			<button type="button" class="btn btn-info btn-attachment-lookup-readonly"
				data-outletattachmentdialog-outlet-id="${outlet.id}"
				data-outletattachmentdialog-readonly="true">Attachment Lookup</button>
		</div>
	</div>
</div>