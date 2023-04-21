<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page session="false"%>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/commonDialog/unitLookup-css.jsp" %>
	</jsp:attribute>
	<jsp:attribute name="header">
		<style>
			.map_canvas {
				height: 100%;
				width:100% 
			}
			.btn.pull-right {
				margin-left: 10px;
			}
			.ui-sortable tr {
			    cursor:pointer; 
			}   
			.maplabels {
				color: white;
				font-family: "Lucida Grande", "Arial", sans-serif;
				font-size: 12px;
				text-align: center;
				width: 16px;
				white-space: nowrap;
			}
			.sortableItem {
				padding-right: 0px;
				margin-bottom: 4px;		
			}

		</style>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp"%>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp"%>
		<script src="<c:url value='/resources/js/jquery.validate.custom.js'/>"></script>
		<script src="<c:url value='/resources/js/jquery.tmpl.min.js'/>"></script>
		<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=${googleBrowserKey}&libraries=places&language=zh-tw"></script>
		<script src="<c:url value='/resources/js/markerwithlabel.js'/>"></script>
		
		<script src="<c:url value='/resources/js/promise.js' />"></script>
		<script src="<c:url value='/resources/js/underscore-min.js' />"></script>
		
		<%-- map config --%>
		<script src="<c:url value='/resources/js/map.js' />"></script>
		<script>

			// Google Map START

			var directionsDisplay;
			var directionsService = new google.maps.DirectionsService();
			var map;

			var routeMakerList = [];
			var markers2 = [];
			var infoWindows2 = [];
			
			function toggleInfoWindowMarker3(open){
				toogleInfoWindow(map, routeMakerList, open);
			}
			
			function toogleInfoWindow(map, markers, open){				
				for (var i = 0; i < markers.length; i++) {
					if (open){
						markers[i].infowindow.open(map, markers[i]);
					}
					else{
						markers[i].infowindow.close()
					}
				}
			}
			
			function getInfoText(assignment) {
				return assignment.firm+"<BR>";
			}
			
			function createMarker(map, assignment, location, labelContent, color) {
				
	        	var infoText = getInfoText(assignment);
            	var myinfowindow = new google.maps.InfoWindow({
            		    content: infoText
            		});
            	
	            var marker = new MarkerWithLabel({
					position: location,
					map: map,
					title: assignment.firm,
					//animation: google.maps.Animation.DROP,
				    labelContent: labelContent,
				    labelAnchor: new google.maps.Point(8, 48),
				    labelClass: "maplabels", // the CSS class for the label
				    labelInBackground: false,
				    icon: pinSymbol(color),
				    streetAddress: assignment.address,
					infowindow : myinfowindow,
		            });	
	            
	            google.maps.event.addListener(marker, 'click', function() {
	                this.infowindow.open(map, this);
	        	});
	            
	            return marker;
			}

			function cleanRouteMarker() {
				// Clear all marker and polyline on map 3
				for(var i = 0; i < routeMakerList.length; i++) {
					routeMakerList[i].setMap(null);
				}
				routeMakerList.splice(0,routeMakerList.length)
			}

			function drawRoute(directionsDisplay, locations, color) {

				if (locations.length > 1) {
					
					for(var i = 0; i < locations.length ; i++) {
						assignment = getAssignmentByPosition(locations[i]);
						for (var j = 0; j < routeMakerList.length ; j++) {			
							if (routeMakerList[j].streetAddress == assignment.address) {
								routeMakerList[j].labelContent = "(N)";
								var content = routeMakerList[j].infowindow.content + assignment.displaySeq + ") "+ getInfoText(assignment);
								routeMakerList[j].infowindow.content = content;
								routeMakerList[j].infowindow.setContent(content);
								routeMakerList[j].title = routeMakerList[j].title +","+ assignment.firm;
								break;
							}
						}
						if (routeMakerList.length == 0 || j == routeMakerList.length) {
							var marker = createMarker(map, assignment, locations[i], assignment.displaySeq, assignment.color);
							var content = assignment.displaySeq+") "+marker.infowindow.content ;
							marker.infowindow.content = content;
							marker.infowindow.setContent(content);
							routeMakerList.push(marker);
						}
					}
					
					

					var requests = [];
					if ( locations.length > 2) {						
						var batch = Math.ceil((locations.length-2) / 6);						
						for (var i = 0; i < batch; i++){
							var startIndex = i * 6 + 1;
							var endIndex = startIndex + 6;
							if (endIndex >= locations.length -1 ){
								endIndex = locations.length - 2
							}
							var request = {};
							var waypts = [];
							for (var y = startIndex; y <= endIndex; y++ ){
								waypts.push({
									location: locations[y],
									stopover: true
								});
							}
							request = {
								origin: locations[startIndex-1],
								destination: locations[endIndex + 1],
								waypoints: waypts,
								travelMode: google.maps.TravelMode.WALKING,
								avoidHighways: true
							};
							requests.push(callDirectionService(i, request));
						}						
						
					} else {
						request = {
								origin: locations[0],
								destination: locations[locations.length - 1],
								travelMode: google.maps.TravelMode.WALKING,
								avoidHighways: true
							};
						requests.push(callDirectionService(0, request));
					}			
					
					promise.join(requests).then(
			              function(results) {
			            	  var sortedDir = _.sortBy(results, function(ret){ return ret[0]; });
			            	  if (sortedDir.length == 0) return;
			            	  var combined;
			            	  for (i = 0; i < sortedDir.length; i++){
				            	  //directionsDisplay.setDirections(results[i][0]);
				            	  if (i == 0){
				            		  combined = results[i][1];
				            	  }
				            	  else{
				            		  combined.routes[0].legs = combined.routes[0].legs.concat(results[i][1].routes[0].legs);
				            		  combined.routes[0].overview_path = combined.routes[0].overview_path.concat(results[i][1].routes[0].overview_path);

				            		  combined.routes[0].bounds = combined.routes[0].bounds.extend(results[i][1].routes[0].bounds.getNorthEast());
				            		  combined.routes[0].bounds = combined.routes[0].bounds.extend(results[i][1].routes[0].bounds.getSouthWest());
				            	  }
			            	  }
			            	  directionsDisplay.setDirections(combined);
			              }
			          );

				} else {
					assignment = getAssignmentByPosition(locations[0]);	
					routeMakerList.push(createMarker(map, assignment, assignment.position, 1, assignment.displaySeq));
				}
			}
			
			
			function callDirectionService(i, request){
				 var p = new promise.Promise();
				directionsService.route(request, function(result, status) {
					//routeMakerList = [];
					//routeMakerList.splice(0,routeMakerList.length)
					if (status == google.maps.DirectionsStatus.OK) {
						p.done(i, result);
						//directionsDisplay.setDirections(result);
					}
					else{
						p.done(i, []);
					}
				});
				return p;
			}
			
			
			function getMajorLocationById(id) {
				for (var i=0 ; i < majorLocations.length ; i++) {
					if (majorLocations[i].majorLocationId == id) {
						return majorLocations[i];
					}
				}
				return null;
			}
			
			function getAssignmentByPosition(position) {
				for (var i=0 ; i < assignments.length ; i++) {
					if (assignments[i].position == position) {
						return assignments[i];
					}
				}
			}
			
			function getNewLocationsSequence(data, session) {
				var newLocationSeq=[];
				for(var i = 0; i < data.length; i++) {
					console.log("data[i]:"+data[i]);
					if (data[i] != "") {
						majorLocation = getMajorLocationById(eval(data[i]));
						console.log("majorLocation.isFreeEntryTask : " + majorLocation.isFreeEntryTask);
						if (majorLocation.isFreeEntryTask == false) {
							for (var j=0 ; j < majorLocation.itineraryPlanOutlets.length ; j ++) {
					            var latLng = new google.maps.LatLng( Number(majorLocation.itineraryPlanOutlets[j].latitude),  Number(majorLocation.itineraryPlanOutlets[j].longitude));
					            majorLocation.itineraryPlanOutlets[j].position = latLng;
					            newLocationSeq.push(latLng);
							}
						}
					}
				}
				return newLocationSeq;
			}
			// Google Map END
			
			// Assignment

		    // Create an array of Major location
		    
		    majorLocations = ${commonService.jsonEncode(model.majorLocations)};
		    majorLocations = majorLocations || [];
					
			for (i = 0; i < majorLocations.length; i++){
				var omj = majorLocations[i];
				omj.index = i + 1;
				omj.itineraryPlanOutlets = omj.itineraryPlanOutletModels;
				for (j = 0; j < omj.itineraryPlanOutlets.length; j++){
					var a = omj.itineraryPlanOutlets[j];
					a.index = j + 1;
					a.group = i + 1;
					a.outletId = a.outletId == null? 0 : a.outletId;
					a.noOfQuotation = a.noOfQuotation == null? 0 : a.noOfQuotation;
					a.noOfAssignment = a.noOfAssignment == null? 0 : a.noOfAssignment;
					a.removable = a.removable == null? true : a.removable;
				}
			}
		    
			<%--
			majorLocations = [
			<c:forEach items="${model.majorLocations}" var="mj" varStatus="i">
				{
					majorLocationId : <c:out value="${mj.majorLocationId}" />,
					index : <c:out value="${i.index+1}" />,
					remark : "<c:out value="${mj.remark}" />",
					sequence : <c:out value="${mj.sequence}" />,
					taskName : "<c:out value="${mj.taskName}" />",
					location : "<c:out value="${mj.location}" />",
					session : "<c:out value="${mj.session}" />",
					isFreeEntryTask : <c:out value="${mj.isFreeEntryTask}" />,
					isNewRecruitmentTask : <c:out value="${mj.isNewRecruitmentTask}" />,
					district : "<c:out value="${mj.district}" />",
					tpu : "<c:out value="${mj.tpu}" />",
					street : "<c:out value="${mj.street}" />",
					itineraryPlanOutlets : [
					<c:forEach items="${mj.itineraryPlanOutletModels}" var="assignment" varStatus="j">
					{
						itineraryPlanOutletId : <c:out value="${assignment.itineraryPlanOutletId}" />,
						index : <c:out value="${j.index+1}" />,
						group : <c:out value="${i.index+1}" />,
						sequence : <c:out value="${assignment.sequence}" />,
						outletId : <c:out value="${assignment.outletId == null ? 0 : assignment.outletId}" />,
						firmCode : "<c:out value="${assignment.firmCode}" />",
						firm : "<c:out value="${assignment.firm}" />",
						marketName : "<c:out value="${assignment.marketName}" />",
						districtId : <c:out value="${assignment.districtId}" />,
						district : "<c:out value="${assignment.district}" />",
						tpu : "<c:out value="${assignment.tpu}" />",
						address : "<c:out value="${assignment.address}" />",
						detailAddress : "<c:out value="${assignment.detailAddress}" />",
						noOfQuotation : <c:out value="${assignment.noOfQuotation == null ? 0 : assignment.noOfQuotation}" />,
						noOfAssignment : <c:out value="${assignment.noOfAssignment}" />,
						convenientTime : "<c:out value="${assignment.convenientTime}" />",
						latitude : "<c:out value="${assignment.latitude}" />",
						longitude : "<c:out value="${assignment.longitude}" />",
						outletRemark : "<c:out value="${assignment.outletRemark}" />",
						deadline : "<c:out value="${assignment.deadline}" />",
						status : "<c:out value="${assignment.status}" />",
						planType : "<c:out value="${assignment.planType}" />",
						outletType : "<c:out value="${assignment.outletType}" />",
						assignmentIds : <c:out value="${assignment.assignmentIds}" />,
					},
					</c:forEach>
					]
				},
			</c:forEach>
			];
			--%>
			
			var assignments = [];
		    var tpus = [];
		    var districts = [];
		    var outletIds = [];
			
			function updateCounts() {
				noOfAssignment = 0;
				noOfQuotation = 0;

				for (i = 0; i < assignments.length; i++) {
					noOfAssignment += assignments[i].noOfAssignment;
					noOfQuotation += assignments[i].noOfQuotation;
					tpus.push(assignments[i].tpu);
					districts.push(assignments[i].district);
					outletIds.push(assignments[i].outletId);
					assignments[i].seq = i + 1;
				}
				
				tpus.sort();
				tpus = $.unique(tpus);
				districts.sort();
				districts = $.unique(districts);
				
				$("#noOfAssignment").text(noOfAssignment);
				$("#noOfQuotation").text(noOfQuotation);
				$("#noOfTpu").text(tpus.length);
				$("#noOfDistrict").text(districts.length);			
			}

			function rejectRecordsWithReason(reason) {
				$("#reason").val(reason);
				$("#mainForm").attr("action", "<c:url value='/itineraryPlanning/ItineraryPlanApproval/rejectView'/>").submit();						
			}
			
			// Onload

			$(document).ready(function() {
				

				for (var i=0 ; i < majorLocations.length ; i++) {
					majorLocation = majorLocations[i];
					if (!majorLocation.isNewRecruitmentTask && !majorLocation.isFreeEntryTask){
						majorLocation.color = assignMarkerColor(majorLocations);							
					}
				}
				
				majorLocations = _.sortBy(majorLocations, function (item){
					return item.sequence;
				});
				for (var i=0 ; i < majorLocations.length ; i++) {
					majorLocation = majorLocations[i];
					if (majorLocation.itineraryPlanOutlets.length > 0) {
						for (var j=0 ; j < majorLocation.itineraryPlanOutlets.length ; j++) {
							itineraryPlanOutlet = majorLocation.itineraryPlanOutlets[j];
							var latLng = new google.maps.LatLng( Number(itineraryPlanOutlet.latitude),  Number(itineraryPlanOutlet.longitude));
							itineraryPlanOutlet.position = latLng;
							itineraryPlanOutlet.color = majorLocation.color;
							assignments.push(itineraryPlanOutlet);
						}					
					}
				}
				
				updateCounts();
				mapdata = [];
				var seq = 1;
				for (var i=0 ; i < majorLocations.length ; i++) {
					majorLocation = majorLocations[i];

					var placeHolder;
					
					if (majorLocation.session == "A" ) {
						placeHolder = "#session1";
					} else if (majorLocation.session == "P" ) {
						placeHolder = "#session2";
					}		
					else if (majorLocation.session == "E" ){
						placeHolder = "#session3";
					}		
					
					if (majorLocation.isNewRecruitmentTask == true) {
						
		    			$("#recruitmentTemplate").tmpl(majorLocation).appendTo(placeHolder);
		    			
					} else if (majorLocation.isFreeEntryTask == true) {
					
		    			$("#taskTemplate").tmpl(majorLocation).appendTo(placeHolder);
		    			
					} else {
				
						mapdata.push(majorLocation.majorLocationId);
		    			$("#majorLocationTemplate").tmpl(majorLocation).appendTo(placeHolder);
		    			
		    			console.log( i +":"+majorLocation.itineraryPlanOutlets.length)
						for (var j=0 ; j < majorLocation.itineraryPlanOutlets.length ; j++) {
							majorLocation.itineraryPlanOutlets[j].displaySeq = (seq++);
							$("#majorLocationTable_"+majorLocation.session+majorLocation.sequence+" tbody").append($("#outletTemplate").tmpl(majorLocation.itineraryPlanOutlets[j]));
						}
					}
				}
				$('table.display').DataTable({
						"ordering": false,
					    "paging": false,
				        "buttons": [],
				        "bInfo" : false
				});
				var position = new google.maps.LatLng( Number(assignments[0].latitude),  Number(assignments[0].longitude));
				
				if (map == null) {
					var mapOptions = {
							zoom: 16,
							center: position,
							mapTypeId: google.maps.MapTypeId.ROADMAP
						};
					map = new google.maps.Map(document.getElementById("map_canvas"), mapOptions);
				}
				
				directionsDisplay = new google.maps.DirectionsRenderer({
					suppressMarkers: true,
					preserveViewport: false,
	                    polylineOptions: {
	                        strokeWeight: 4,
	                        strokeOpacity: 0.8,
	                        strokeColor: "red"
	                    },
					});
				
				newLocationSeq = getNewLocationsSequence(mapdata);
				if (newLocationSeq.length > 0) {
					drawRoute(directionsDisplay, newLocationSeq, 'red');
					if (newLocationSeq.length > 1) {
						directionsDisplay.setMap(map);
					} else {
						directionsDisplay.setMap(null);
					}
				}
				
				$("#Approve").on('click', function() {
					bootbox.confirm({
						title:"Confirmation",
						message: "<spring:message code='W00004' />",
						callback: function(result){
							if (result){
								$("#mainForm").attr("action", "<c:url value='/itineraryPlanning/ItineraryPlanApproval/approveView'/>").submit();						
							}
						}
					})
				});
				
				$("#Reject").on('click', function() {
					bootbox.confirm({
						title:"Confirmation",
						message: "<spring:message code='W00005' />",
						callback: function(result){
							if (result){
								
								$("#rejectForm").modal('show');
								$("#rejectBtn").unbind( "click" ).on('click', function() {
									$("#rejectForm").modal('hide');
									var reason = $("#rejectReason").val();
									rejectRecordsWithReason(reason);
									$("#rejectReason").val("");
								});
							}
						}
					})
				})

			});
			
		</script>
	    <script id="majorLocationTemplate" type="text/x-jquery-tmpl">
			<ul id="majorLocation_\${session}\${sequence}" class="list-group sortableItem">
				<li class="list-group-item">
					<div class="row">
					<div class="col-sm-4">
						<span class='glyphicon glyphicon-home' aria-hidden='true' style="background-color:\${color};color:white; padding: 3px 3px 3px 3px;"></span>
						<label>\${taskName}</label>
					</div>
					<div class="col-sm-1"> <label >Remark: </label></div>
					<div class="col-sm-7">\${remark}</div>
					</div>
				</li>
				<li class="list-group-item" >
					<table id="majorLocationTable_\${session}\${sequence}" class="text-center table table-hover table-striped table-bordered table-hover dataTable no-footer" cellspacing="0" width="100%">
       					<thead>
           					<tr>
								<th>Seq No.</th>
								<th>Reference no</th>
		                		<th>Name</th>
        		        		<th>MRPS District</th>
                				<th>TPU</th>
   		             			<th>Full Address</th>
        		        		<th>No. of Quotation (MRPS only)</th>
								<th>Convenient Time</th>
								<th>Remark</th>
								<th>Deadline / Collection Date</th>
          			  		</tr>
        				</thead>
						<tbody>
						</tbody>
					</table>
				</li>
			</ul>
		</script>
        <script id="outletTemplate" type="text/x-jquery-tmpl">
            <tr>
				<td>\${displaySeq}</td>
				<td>\${referenceNo}</td>
                <td>\${firm}</td>
                <td>\${district}</td>
                <td>\${tpu}</td>
                <td>\${detailAddress}</td>
                <td>\${noOfQuotation}</td>
				<td>\${convenientTime}</td>
				<td>\${outletRemark}</td>
				<td>\${deadline}</td>
            </tr>
		</script>
		<script id="taskTemplate" type="text/x-jquery-tmpl">
			<ul id="task_\${sequence}" class=" list-group sortableItem" majorlocationIndex="\${sequence}">
				<li class="list-group-item">
					<div class="row">
						<div class="col-sm-4"><label>\${taskName}</label></div>
					    <div class="col-sm-1"><label>Remark: </label></div>
						<div class="col-sm-7">\${remark}</div>
					</div>
					<div class="row">
						<div class="col-sm-1"><label>Location: </label></div>
						<div class="col-sm-11"> \${location} </div>
					</div>
				</li>
			</ul>
		</script>
		<script id="recruitmentTemplate" type="text/x-jquery-tmpl">
			<ul id="task_\${sequence}" class=" list-group sortableItem" majorlocationIndex="\${sequence}">
				<li class="list-group-item">
					<div class="row">
						<div class="col-xs-4"><label>\${taskName}</label></div>
					    <div class="col-xs-1"><label>Remark: </label></div>
						<div class="col-xs-7">\${remark}</div>
					</div>
					<div class="row">
						<div class="col-xs-1"><label>District: </label></div>
						<div class="col-xs-3"> \${district} </div>
						<div class="col-xs-1"><label>Tpu: </label></div>
						<div class="col-xs-3"> \${tpu} </div>
						<div class="col-xs-1"><label>Street: </label></div>
						<div class="col-xs-3"> \${street} </div>
					</div>
				</li>
			</ul>
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
        	<h1>Itinerary Plan Maintenance</h1>
        	<c:if test="${not empty model.itineraryPlanId}">
				<div class="breadcrumb form-horizontal" style="width:240px">
					<div class="form-group" style="margin-bottom:0px">
			        	<div class="col-sm-5">Created Date:</div>
			        	<div class="col-sm-7"><c:out value="${commonService.formatDateTime(model.createdDate)}" /></div>
			        </div>
			        <div class="form-group" style="margin-bottom:0px">
			         	<div class="col-sm-5">Last Modified:</div>
			         	<div class="col-sm-7"><c:out value="${commonService.formatDateTime(model.modifiedDate)}" /></div>
			         </div>
		      	</div>
	      	</c:if>
        </section>
        <section class="content">
        	<form id="mainForm" method="post" role="form" enctype="multipart/form-data">
        		<input id="id" name="id" value="<c:out value="${model.itineraryPlanId}" />_<c:out value="${model.version}" />" type="hidden" />
        		<input id="reason" name="reason" type="hidden" />
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/itineraryPlanning/ItineraryPlan/home'/>">Back</a>
							</div>
	       					<div class="box-body">
	       						<div class="form-horizontal">
	       							<div class="form-group">
	       								<div class="col-md-2"><label>Date:&nbsp;</label><c:out value="${commonService.formatDate(model.date)}" /></div>
	       								<div class="col-md-3"><label>Field Officer:&nbsp;</label><c:out value="${userText}" /></div>
	       								<div class="col-md-3"><label>Working Section:&nbsp;</label><c:out value="${model.session}" /> Session</div>
	       								<div class="col-md-3"><label>Submit To:&nbsp;</label><c:out value="${supervisorText}" /></div>
       								</div>
       								<div class="form-group">
		       							<div class="col-md-12 text-right">
												No. of outlets : <span id="noOfAssignment" class="badge">0</span>
							    				No. of quotations : <span id="noOfQuotation" class="badge">0</span>
							    				No. of TPU : <span id="noOfTpu" class="badge">0</span>
							    				No. of District : <span id="noOfDistrict" class="badge">0</span>
	       							    </div>
       							    </div>
  									<div class="form-group">
										<!--  Map -->
										<div class="col-sm-12" >
											<div style="height: 500px;">
												<div id="map_canvas" class="map_canvas"></div>
											</div>
											<div>
												<button class="btn btn-info" type="button" onclick="toggleInfoWindowMarker3(true)">Open All Info Window</button>
												<button class="btn btn-info" type="button" onclick="toggleInfoWindowMarker3(false)">Close All Info Window</button>
											</div>
										</div>
										
									</div>
       							   	<div class="col-md-12" id="sessions">
       							   		<label id="labelSession_1" class="control-label" <c:if test="${model.session == 'PE' }">style="display:none"</c:if>>A Session:</label>
       							   		 <ul class="list-group" <c:if test="${model.session eq 'PE' }">style="display:none"</c:if>>
       							   			<li class="col-md-12 list-group-item">
       							   				<ul id="session1" class="list-group sortableItem">
       											</ul>
       										</li>
       							   		</ul>
       							   		<label id="labelSession_2" class="control-label" >P Session:</label>
       							   		<ul class="list-group">
       							   			<li class="col-md-12 list-group-item">
       							   				<ul id="session2" class="list-group sortableItem">
       											</ul>
       										</li>
       							   		</ul>
       							   		<label id="labelSession_3" class="control-label" <c:if test="${model.session == 'AP' }">style="display:none"</c:if>>E Session:</label>
       							   		<ul class="list-group" <c:if test="${model.session eq 'AP' }">style="display:none"</c:if>>
       							   			<li class="col-md-12 list-group-item">
       							   				<ul id="session3" class="list-group sortableItem">
       											</ul>
       										</li>
       							   		</ul>
									</div>
								</div>
							</div>
						</div>
					</div>
	        	</div>
	        </form>
        </section>
	</jsp:body>
</t:layout>

