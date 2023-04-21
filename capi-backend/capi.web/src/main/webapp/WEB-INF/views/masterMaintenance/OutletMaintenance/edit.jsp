<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
<jsp:useBean id="niceDate" class="java.util.Date"/>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/timepicker-css.jsp" %>
	</jsp:attribute>
	<jsp:attribute name="header">
		<style>
			#map { height: 500px }
			#attachmentContainer {
				min-height: 300px;
			}
			#attachmentContainer .attachment {
				margin-bottom: 10px;
			}
			.btn.pull-right {
				margin-left: 10px;
			}
		</style>
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/timepicker.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/imageViewer.jsp" %>
		<%@include file="/WEB-INF/views/includes/handlebars.jsp" %>
		<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=${googleBrowserKey}&libraries=places&sensor=false&language=zh-tw"></script>
		<script>
			$(function() {
				var $mainForm = $('#mainForm');
				var googleBrowserKey = '${googleBrowserKey}';
				var NUMBER_OF_ATTACHMENTS = 10;
				

				$.validator.addMethod('not_more_than5', function (value, element, param){
					return $(element).find("option:selected").length <=5;
				},'<spring:message code="E00114" />')
				
				$mainForm.validate();
				Modals.init();
				
				
				function queryGps(address, callback, place_id) {
					if (place_id != null && place_id != ''){
						var geocoder = new google.maps.Geocoder;
						geocoder.geocode({'placeId': place_id, region: "hk"}, function(results, status) {
							callback(results[0].geometry.location);
						});

					}
					else{
						$.get('https://maps.googleapis.com/maps/api/geocode/json',
							{
								address: address,
								region: "hk",
								key: googleBrowserKey
							},
							function(data) {
								if (data.status != 'OK') {
									callback(null);
									return;
								}
								// data.results[0].formatted_address
								callback(data.results[0].geometry.location);
							}
						);
					}
				}
				
				$('#btnGetLocation').click(function() {
					var $btn = $(this);
					$btn.prop('disabled', true);
					var address = $('#streetAddress').val();
					var place_id = $('#streetAddress').data('place_id');
					queryGps(address, function(gps) {
						$btn.prop('disabled', false);
						if (gps == null) {
							alert('Address cannot be located');
							return;
						}
						$('#latitude').val(gps.lat);
						$('#longitude').val(gps.lng);
					}, place_id);
				});

				var map;
				var building;
				var marker;
				$('#mapModal').on('show', function () {
					var modal = $(this);
					
					var lat = $('#latitude').val();
					var lng = $('#longitude').val();
					
					building = new google.maps.LatLng(lat, lng);

					var mapOptions = {
						center: building,
						zoom: 16,
						mapTypeId: google.maps.MapTypeId.ROADMAP
					};

					if (map == null) {
						map = new google.maps.Map(document.getElementById("map"), mapOptions);
					}
					map.setOptions(mapOptions);

					/* Pre-place marker */
					if (marker != null) marker.setMap(null);
					marker = new google.maps.Marker({
						position: building,
						map: map
					});
				}).on('shown', function() {
					google.maps.event.trigger(map, "resize");
					map.setCenter(building);
				});
				
				$('#outletTypeIds').select2({
					closeOnSelect: false
				});
				$('#districtId').select2ajax();
				$('#tpuId').select2ajax({
					ajax: {
					    data: function (params) {
					    	params.districtId = $('#districtId').val()
					    	return params;
						},
						method: 'GET',
						contentType: 'application/json'
					}
				});
				
				var attachmentTemplate = Handlebars.compile($('#attachmentTemplate').html());
				var $attachmentContainer = $('#attachmentContainer');
				$('#attachmentModal').on('show', function () {
					var modal = $(this);
					var outletId = $('[name="outletId"]').val();
					$.get('<c:url value='/masterMaintenance/OutletMaintenance/getAttachmentIds'/>?id=' + outletId,
						function(data) {
							$attachmentContainer.html('');
							var bindData = { attachments : [] };
							
							for (var i = 0; i < data.length; i++) {
								if(data[i]==null)
									bindData.attachments.push({ id : i, isDummy : true });
								else
									bindData.attachments.push({ id : data[i], isDummy : false });
							}
							/* for (var i = 0, len = NUMBER_OF_ATTACHMENTS - data.length; i < len; i++) {
								bindData.attachments.push({ id : i, isDummy : true });
							} */
							
							$attachmentContainer.html(attachmentTemplate(bindData));
							
							$('img', $attachmentContainer).imageViewer();
						}
					);
				});
				
				$(".timepicker").timepicker({
					showInputs: false,
					showMeridian: false,
					defaultTime: false,
					minuteStep: 1
		        });
				

				var options = {
				 // types: ['address'],
				  componentRestrictions: {country: 'hk'}
				};
				var autocomplete = new google.maps.places.Autocomplete($("#streetAddress")[0], options);
				 autocomplete.addListener('place_changed', function() {
					 var place = autocomplete.getPlace();
					 $("#streetAddress").data('place_id',place.place_id);
				 });
				$("#streetAddress").keypress(function(evt){
					if (evt.keyCode == 13){
						evt.stopPropagation();
						return false;
					}					
				});

				<sec:authorize access="!hasPermission(#user, 256)">
					viewOnly();
				</sec:authorize>
				
			});
			
			function deleteOutlet(){
				bootbox.confirm({
					title:"Confirmation",
					message: "<spring:message code='W00001' />",
					callback: function(result){
						if (result){
							window.location.href="<c:url value='/masterMaintenance/OutletMaintenance/delete'/>?id=${model.outletId}";
						}
					}
				});				
			}
			
			function deleteImg(i){
				$("#img"+i).attr("src","<c:url value='/resources/images/dummyphoto.png'/>");
				$("#deleteImage"+i).hide();
				$("[name='file"+i+"']").attr("name", "file"+i+"del");
			}
			
			function removeOutletImg(i){
			    $("#outletImg"+i).attr("src","<c:url value='/resources/images/dummyphoto.png'/>");
			    $("#deleteOutletImage"+i).hide();
			        if(i != "upload"){
			            $("[name='outletImagePath']").val('del');
			        } else {
			            $("[name='outletImagePath']").val('');
			        }
			}
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Outlet Maintenance</h1>
        </section>
        
        <section class="content">
        	<form id="mainForm" action="<c:url value='/masterMaintenance/OutletMaintenance/save'/>" method="post" role="form"
        		enctype="multipart/form-data">
        		<input name="outletId" value="<c:out value="${model.outletId}" />" type="hidden" />
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/masterMaintenance/OutletMaintenance/home'/>">Back</a>
							</div>
	       					<div class="box-body">
	       						<div class="form-horizontal">
	       							<div class="col-sm-6">
		       							<div class="form-group">
		       								<label class="col-sm-5 control-label">Outlet Code</label>
		       								<div class="col-sm-7">
												<p class="form-control-static">${model.firmCode}</p>
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-5 control-label">Outlet Name</label>
		       								<div class="col-sm-7">
												<input name="name" type="text" class="form-control" value="<c:out value="${model.name}" />" maxlength="50" required />
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-5 control-label">Outlet Type</label>
		       								<div class="col-sm-7">
												<select class="form-control select2" id="outletTypeIds" name="outletTypeIds" multiple="multiple" required data-rule-not_more_than5="true">
													<c:forEach var="outletType" items="${outletTypes}">
														<option value="<c:out value="${outletType.id}" />"
															<c:forEach var="modelOutletTypeId" items="${model.outletTypeIds}">
																<c:if test="${modelOutletTypeId == outletType.id}">selected</c:if>
															</c:forEach>
															>${outletType.shortCode} - ${outletType.englishName}</option>
													</c:forEach>
												</select>
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-5 control-label">BR Code</label>
		       								<div class="col-sm-7">
												<input name="brCode" type="text" class="form-control" value="<c:out value="${model.brCode}" />" maxlength="50"  />
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-5 control-label">District Code</label>
		       								<div class="col-sm-7">
												<select class="form-control select2" id="districtId" style="width: 300px"
													data-ajax-url="<c:url value='/masterMaintenance/OutletMaintenance/queryDistrictSelect2'/>">
													<c:if test="${model.districtId != null}">
														<option value="<c:out value="${model.districtId}" />" selected>${model.districtLabel}</option>
													</c:if>
												</select>
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-5 control-label">TPU</label>
		       								<div class="col-sm-7">
												<select class="form-control select2" id="tpuId" name="tpuId" style="width: 300px"
													data-ajax-url="<c:url value='/masterMaintenance/OutletMaintenance/queryTpuSingleDistrictSelect2'/>" data-msg-required="<spring:message code='E00016' />" required>
													<c:if test="${model.tpuId != null}">
														<option value="<c:out value="${model.tpuId}" />" selected>${model.tpuLabel}</option>
													</c:if>
												</select>
											</div>
										</div>
									</div>
	       							<div class="col-sm-6">
		       							<div class="form-group">
		       								<label class="col-sm-5 control-label">Created Date</label>
		       								<div class="col-sm-7">
												<p class="form-control-static">${model.createdDate}</p>
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-5 control-label">Modified Date</label>
		       								<div class="col-sm-7">
												<p class="form-control-static">${model.modifiedDate}</p>
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-5 control-label">No. of quotation</label>
		       								<div class="col-sm-7">
												<p class="form-control-static">${model.noOfQuotation}</p>
											</div>
										</div>
		       							<div class="form-group">
		       								<div class="col-sm-offset-2 col-sm-10">
												<c:if test="${model.outletImagePath != null}">
<%-- 													<img class="img-responsive viewer" src="<c:url value='/masterMaintenance/OutletMaintenance/getImage'/>?id=${model.outletId}&amp;bust=${niceDate.time}"/> --%>
													<img class="img-responsive viewer" id="outletImg${model.outletId}"
														src="<c:url value='/masterMaintenance/OutletMaintenance/getImage'/>?id=${model.outletId}&amp;bust=${niceDate.time}" />
													<button style="width: 40%" type="button"
														id="deleteOutletImage${model.outletId}"
														onclick="removeOutletImg(${model.outletId})">
													<span>Remove</span>
													</button>
												</c:if>
<!-- 												<input name="outletImage" type="file" /> -->
												<input name="outletImagePath" type="hidden" value =""/>
												<input name="outletImage" type="file" onchange="removeOutletImg('upload')"/>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
	        			<div class="box box-primary">
	       					<div class="box-body">
	       						<div class="form-horizontal">
									<div class="col-sm-12">
		       							<div class="form-group">
		       								<label class="col-sm-2 control-label">Map Address</label>
		       								<div class="col-sm-7">
												<input type="text" id="streetAddress" name="streetAddress" class="form-control" required value="<c:out value="${model.streetAddress}" />">
											</div>
											<div class="col-sm-3">
												<button id="btnGetLocation" type="button" class="btn btn-primary">Get Location</button>
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-2 control-label">Detail Address</label>
		       								<div class="col-sm-10">
												<textarea name="detailAddress" class="form-control">${model.detailAddress}</textarea>
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-2 control-label">Latitude</label>
		       								<div class="col-sm-7">
												<input id="latitude" name="latitude" type="text" class="form-control" value="<c:out value="${model.latitude}" />" maxlength="50" />
											</div>
											<div class="col-sm-3">
												<button id="btnShowMap" type="button" class="btn btn-primary" data-toggle="modal" data-target="#mapModal">View Map</button>
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-2 control-label">Longitude</label>
		       								<div class="col-sm-7">
												<input id="longitude" name="longitude" type="text" class="form-control" value="<c:out value="${model.longitude}" />" maxlength="50" />
											</div>
										</div>
										
		       							<div class="form-group">
		       								<label class="col-sm-2 control-label">Main Contact</label>
		       								<div class="col-sm-2">
												<input name="mainContact" type="text" class="form-control" value="<c:out value="${model.mainContact}" />" maxlength="255"/>
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-2 control-label">Last Contact</label>
		       								<div class="col-sm-10">
												<input name="lastContact" type="text" class="form-control" value="<c:out value="${model.lastContact}" />" maxlength="255"/>
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-2 control-label">Fax</label>
		       								<div class="col-sm-2">
												<input name="fax" type="text" class="form-control" value="<c:out value="${model.fax}" />" maxlength="50"/>
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-2 control-label">Telephone No.</label>
		       								<div class="col-sm-2">
												<input name="tel" type="text" class="form-control" value="<c:out value="${model.tel}" />" maxlength="50" />
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-2 control-label">Web Site</label>
		       								<div class="col-sm-10">
												<input name="webSite" type="text" class="form-control" value="<c:out value="${model.webSite}" />"/>
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-2 control-label">Major Location</label>
		       								<div class="col-sm-10">
												<input name="marketName" type="text" class="form-control" value="<c:out value="${model.marketName}" />" maxlength="255"/>
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-2 control-label">Market Name</label>
		       								<div class="col-sm-10">
												<input name="indoorMarketName" type="text" class="form-control" value="<c:out value="${model.indoorMarketName}" />" maxlength="255"/>
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-2 control-label">Remarks</label>
		       								<div class="col-sm-10">
												<input name="remark" type="text" class="form-control" value="<c:out value="${model.remark}" />"/>
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-2 control-label">Discount Remarks</label>
		       								<div class="col-sm-10">
												<input name="discountRemark" type="text" class="form-control" value="<c:out value="${model.discountRemark}" />"/>
											</div>
										</div>
		       							
		       							<div class="form-group">
		       								<label class="col-sm-2 control-label">Opening Hour</label>
		       								<div class="col-sm-2">
		       									<div class="input-group bootstrap-timepicker">
													<input name="openStart" type="text" class="form-control timepicker" value="<c:out value="${model.openStart}" />" />
													<div class="input-group-addon">
														<i class="fa fa-clock-o"></i>
													</div>
												</div>
											</div>
											<div class="col-sm-1">
												<p class="form-control-static text-center">To</p>
											</div>
		       								<div class="col-sm-2">
		       									<div class="input-group bootstrap-timepicker">
													<input name="openEnd" type="text" class="form-control timepicker" value="<c:out value="${model.openEnd}" />" />
													<div class="input-group-addon">
														<i class="fa fa-clock-o"></i>
													</div>
												</div>
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-2 control-label">Opening Hour 2</label>
		       								<div class="col-sm-2">
		       									<div class="input-group bootstrap-timepicker">
													<input name="openStart2" type="text" class="form-control timepicker" value="<c:out value="${model.openStart2}" />" />
													<div class="input-group-addon">
														<i class="fa fa-clock-o"></i>
													</div>
												</div>
											</div>
											<div class="col-sm-1">
												<p class="form-control-static text-center">To</p>
											</div>
		       								<div class="col-sm-2">
		       									<div class="input-group bootstrap-timepicker">
													<input name="openEnd2" type="text" class="form-control timepicker" value="<c:out value="${model.openEnd2}" />" />
													<div class="input-group-addon">
														<i class="fa fa-clock-o"></i>
													</div>
												</div>
											</div>
										</div>
										
										
		       							<div class="form-group">
		       								<label class="col-sm-2 control-label">Convenient Time</label>
		       								<div class="col-sm-2">
		       									<div class="input-group bootstrap-timepicker">
													<input name="convenientStart" type="text" class="form-control timepicker" value="<c:out value="${model.convenientStart}" />" />
													<div class="input-group-addon">
														<i class="fa fa-clock-o"></i>
													</div>
												</div>
											</div>
											<div class="col-sm-1">
												<p class="form-control-static text-center">To</p>
											</div>
		       								<div class="col-sm-2">
		       									<div class="input-group bootstrap-timepicker">
													<input name="convenientEnd" type="text" class="form-control timepicker" value="<c:out value="${model.convenientEnd}" />" />
													<div class="input-group-addon">
														<i class="fa fa-clock-o"></i>
													</div>
												</div>
											</div>
										</div>
										
										
										<div class="form-group">
		       								<label class="col-sm-2 control-label">Convenient Time 2</label>
		       								<div class="col-sm-2">
		       									<div class="input-group bootstrap-timepicker">
													<input name="convenientStart2" type="text" class="form-control timepicker" value="<c:out value="${model.convenientStart2}" />" />
													<div class="input-group-addon">
														<i class="fa fa-clock-o"></i>
													</div>
												</div>
											</div>
											<div class="col-sm-1">
												<p class="form-control-static text-center">To</p>
											</div>
		       								<div class="col-sm-2">
		       									<div class="input-group bootstrap-timepicker">
													<input name="convenientEnd2" type="text" class="form-control timepicker" value="<c:out value="${model.convenientEnd2}" />" />
													<div class="input-group-addon">
														<i class="fa fa-clock-o"></i>
													</div>
												</div>
											</div>
										</div>
										
		       							<div class="form-group">
		       								<label class="col-sm-2 control-label">Collection Method</label>
		       								<div class="col-sm-2">
												<select name="collectionMethod" class="form-control">
													<option value="1" <c:if test="${model.collectionMethod == 1}">selected</c:if>>Field Visit</option>
													<option value="2" <c:if test="${model.collectionMethod == 2}">selected</c:if>>Telephone</option>
													<option value="3" <c:if test="${model.collectionMethod == 3}">selected</c:if>>Fax</option>
													<option value="4" <c:if test="${model.collectionMethod == 4}">selected</c:if>>Others</option>
												</select>
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-2 control-label">Use FR (Admin)</label>
		       								<div class="col-sm-10">
		       									<div class="checkbox">
													<label>
														<input name="useFRAdmin" type="checkbox" value="True" ${model.useFRAdmin ? "checked" : ""}/>
													</label>
												</div>
											</div>
										</div>
										<div class="form-group">
		       								<label class="col-sm-2 control-label">Outlet Market Type</label>
		       								<div class="col-sm-10">
												<label class="radio-inline">
													<input type="radio" name="outletMarketType" value="1" <c:if test="${model.outletMarketType == 1}">checked</c:if>> Market
												</label>
												<label class="radio-inline">
													<input type="radio" name="outletMarketType" value="2" <c:if test="${model.outletMarketType == 2}">checked</c:if>> Non-market
												</label>
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-2 control-label">Status</label>
		       								<div class="col-sm-10">
												<label class="radio-inline">
													<input type="radio" name="status" value="Valid" <c:if test="${model.status == 'Valid'}">checked</c:if>> Valid
												</label>
												<label class="radio-inline">
													<input type="radio" name="status" value="Invalid" <c:if test="${model.status == 'Invalid'}">checked</c:if>> Invalid
												</label>
											</div>
										</div>
									</div>
	       						</div>
	       					</div>
	       					<div class="box-footer">
	       						<sec:authorize access="hasPermission(#user, 256)">
	       							<c:if test="${model.outletId != null}">
		        						<a class="btn btn-info pull-right" href="javascript:void(0)"
		        							onclick="deleteOutlet()">Delete</a>
		        					</c:if>
	       						</sec:authorize>
	       						<sec:authorize access="hasPermission(#user, 256)">
	        						<button type="submit" class="btn btn-info">Submit</button>
	       						</sec:authorize>
	       						<c:if test="${model.outletId != null}">
	       							<button type="button" class="btn btn-info" data-toggle="modal" data-target="#attachmentModal">Attachment Lookup</button>
	       						</c:if>
	       						
	       					</div>
	        			</div>
	        		</div>
	        	</div>
        	</form>
        </section>
        
        <div class="modal fade" role="dialog" id="mapModal">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">X</span></button>
						<h4 class="modal-title">View Map</h4>
					</div>
					<div class="modal-body">
						<!-- content -->
						<!-- Itinerary Planning Table -->
						
								<div class="container-fluid">
									<div class="row">
										<div class="col-md-12" >
											<div class="clearfix">&nbsp;</div>
											<div id="map"></div>
										</div>
									</div>
								</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default pull-left" data-dismiss="modal">Close</button>
					</div>
				</div><!-- /.modal-content -->
			</div><!-- /.modal-dialog -->
		</div>
		
        <div class="modal fade" role="dialog" id="attachmentModal">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<form id="mainForm" action="<c:url value='/masterMaintenance/OutletMaintenance/saveAttachment'/>" method="post" role="form"
						enctype="multipart/form-data">
						<input name="id" type="hidden" value="<c:out value="${model.outletId}" />"/>
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">X</span></button>
							<h4 class="modal-title">Attachment Lookup Dialog</h4>
						</div>
						<div class="modal-body">
							<div class="box">
								<div class="box-body">
									<div class="container-fluid">
										<div class="row">
											<div class="col-md-12">
												<div id="attachmentContainer" class="row"></div>
												<script id="attachmentTemplate" type="text/html">
												{{#each attachments}}
												<div class="col-sm-3 attachment">
													{{#if this.isDummy}}
													<img class="img-responsive" src="<c:url value='/resources/images/dummyphoto.png'/>"/>
													<input name="newAttachment{{this.id}}" type="file" style="width:100%" />
													{{else}}
													<img class="img-responsive" id="img{{this.id}}" src="<c:url value='/masterMaintenance/OutletMaintenance/getAttachment'/>?id={{this.id}}"/>
													<input name="file{{this.id}}" type="file" style="width:100%;" onchange="deleteImg({{this.id}})"/>
													<button style="width:40%" type="button" id="deleteImage{{this.id}}" onclick="deleteImg({{this.id}})"><span>Remove</span></button>
													{{/if}}
												</div>
												{{#moduloIf @index 4}}
												<div class="clearfix"></div>
												{{/moduloIf}}
												{{/each}}
												</script>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
						<sec:authorize access="hasPermission(#user, 256)">
							<div class="modal-footer">
								<button type="button" class="btn btn-default pull-left" data-dismiss="modal">Close</button>
								<button type="submit" class="btn btn-primary modal-submit">Submit</button>
							</div>
						</sec:authorize>
					</form>
				</div><!-- /.modal-content -->
			</div><!-- /.modal-dialog -->
		</div>
	</jsp:body>
</t:layout>

