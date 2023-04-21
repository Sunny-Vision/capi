<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page session="false"%>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>

	</jsp:attribute>

	<jsp:attribute name="header">
		<style>
			.btn.pull-right {
				margin-left: 10px;
			}
			#telEnuTable {
				width: 80%;
			}
			.glyphicon-pencil, .glyphicon-remove, .glyphicon-plus {
    			cursor: pointer;
			}
			.sorting, .sorting_asc, .sorting_desc {
			    background : none!;
			}
			.map_canvas {
				height: 100%;
				width:100% 
			}
			.maplabels {
				color: white;
				font-family: "Lucida Grande", "Arial", sans-serif;
				font-size: 12px;
				text-align: center;
				width: 16px;
				white-space: nowrap;
			}
			.modal.modal-wide .modal-dialog {
			  width: 90%;
			}
			.modal-wide .modal-body {
			  overflow-y: auto;
			}
		</style>
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp"%>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=${googleBrowserKey}&libraries=places&language=zh-tw"></script>
		<script src="<c:url value='/resources/js/markerwithlabel.js'/>"></script>
		<%-- map config --%>
		<script src="<c:url value='/resources/js/map.js' />"></script>
		
		<script src="<c:url value='/resources/js/underscore-min.js'/>"></script>
		<script src="<c:url value='/resources/js/promise.js' />"></script>
		
		<script>
		
		var surveyType = ${commonService.jsonEncode(surveyCountList)};
		surveyType = surveyType || [];
		<%--
		var surveyType = [
			<c:forEach var="survey" items="${surveyCountList}">
				"<c:out value='${survey}'/>",
			</c:forEach>		                  
		];
		--%>
		
		var telephoneTimeLogs = [];
		var fieldworkTimeLogs = [];
		

		function calculateTelephoneCounts() {
			var count = new Array(surveyType.length);
			for (var i=0 ; i < count.length ; i++) {
				count[i] = new Array(2);
				count[i][0] = 0;
				count[i][1] = 0;
			}
			
			for (var i=0 ; i < telephoneTimeLogs.length ; i++) {
				for (var j=0 ; j < surveyType.length ; j++) {
					if (telephoneTimeLogs[i].survey == surveyType[j]) {
						count[j][0] += Number(telephoneTimeLogs[i].completionQuotationCount);
						count[j][1] += Number(telephoneTimeLogs[i].completionTotalQuotation);
						break;
					} 
				}
			}
			
			for (var j=0 ; j < surveyType.length ; j++) {
				$('#'+surveyType[j]+'_C_TI_count').text(count[j][0]+' / '+count[j][1]);
			}	
		}
		

		function calculateFieldworkCounts() {
			
			var count = new Array(surveyType.length);
			for (var i=0 ; i < count.length ; i++) {
				count[i] = new Array(2);
				count[i][0] = 0;
				count[i][1] = 0;
			}
			
			for (var i=0 ; i < fieldworkTimeLogs.length ; i++) {
				if (fieldworkTimeLogs[i].survey == surveyType[0] && fieldworkTimeLogs[i].enumerationOutcome == 'C' ) {
					if (fieldworkTimeLogs[i].marketQuotationCount != '')
						count[0][0] += eval(fieldworkTimeLogs[i].marketQuotationCount);
					if (fieldworkTimeLogs[i].nonMarketQuotationCount != '')
						count[0][0] += eval(fieldworkTimeLogs[i].nonMarketQuotationCount);
					if (fieldworkTimeLogs[i].marketTotalQuotation != '')
						count[0][1] += eval(fieldworkTimeLogs[i].marketTotalQuotation);
					if (fieldworkTimeLogs[i].nonMarketTotalQuotation != '')
						count[0][1] += eval(fieldworkTimeLogs[i].nonMarketTotalQuotation);
				} else if (fieldworkTimeLogs[i].survey == surveyType[1] && fieldworkTimeLogs[i].activity == 'FI' ) {
					var compareEnumerationOutcome = ['C', 'D', 'U', 'ND', 'O'];
										
					if (compareEnumerationOutcome.includes(fieldworkTimeLogs[i].enumerationOutcome)){
						count[1][0] ++;
					}
					count[1][1]++;
				} else if (fieldworkTimeLogs[i].survey == surveyType[2] && fieldworkTimeLogs[i].activity == 'FI') {
					count[2][0] ++;
				}
			}
			
			for (var j=0 ; j < surveyType.length ; j++) {
				if (j < 2) {
					$('#'+surveyType[j]+'_C_FI_count').text(count[j][0]+' / '+count[j][1]);
				} else {
					$('#'+surveyType[j]+'_C_FI_count').text(count[j][0]);
				}
			}
		}
		
		function approveRecordsWithConfirm() {
			bootbox.confirm({
				title:"Confirmation",
				message: "<spring:message code='W00004' />",
				callback: function(result) {
					if (result) {
						$("#mainForm").attr("action", "<c:url value='/timeLogManagement/ItineraryCheckingApproval/approveView'/>");
						$("#mainForm").submit();
					}
				}
			})
		}
		
		
		
		function rejectRecordsWithConfirm() {
			$("#rejectModal").modal('show');
			$("#rejectConfirmBtn").unbind( "click" ).on('click', function() {
				$("#rejectReason").val($("#reason").val());
				rejectRecordsWithReason();
				$("#reason").val("");
				$("#rejectModal").modal('hide');
			});
			/*
			bootbox.confirm({
				title:"Confirmation",
				message: "<spring:message code='W00005' />",
				callback: function(result){
					if (result){				
						$("#rejectModal").modal('show');
						$("#rejectConfirmBtn").unbind( "click" ).on('click', function() {
							$("#rejectReason").val($("#reason").val());
							rejectRecordsWithReason();
							$("#reason").val("");
							$("#rejectModal").modal('hide');
						});
					}
				}
			})*/
		}
		
		function rejectRecordsWithReason() {
			$("#mainForm").attr("action", "<c:url value='/timeLogManagement/ItineraryCheckingApproval/rejectView'/>");
			$("#mainForm").submit();
		}
		
		// Google Map START

		var plannedDirectionsDisplay;
		var visitedDirectionsDisplay;
		var directionsService = new google.maps.DirectionsService();
		var map;

		var plannedRouteMakerList = [];
		var visitedRouteMakerList = [];
		
		function toggleInfoWindowMarker(open){
			toogleInfoWindow(map,plannedRouteMakerList.concat(visitedRouteMakerList),open);
		}

		function getInfoText(assignment) {
			return assignment.firm + "<BR>";
		}
		
		function createMarker(map, assignment, location, labelContent, color) {
			
        	var infoText = getInfoText(assignment);
        	var myinfowindow = new google.maps.InfoWindow({
        		    content: infoText
        		});
        	;
        	console.log("marker location: " + location);
        	console.log("marker labelContent: " + labelContent);
			console.log("marker color: " + color);
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

		function setRouteMarkerMap(routeMarkerList, map) {

			for(var i = 0; i < routeMarkerList.length; i++) {
				routeMarkerList[i].setMap(map);
			}
		}
		
		function cleanRouteMarker(routeMarkerList) {

			for(var i = 0; i < routeMarkerList.length; i++) {
				routeMarkerList[i].setMap(null);
			}
			routeMarkerList.splice(0,routeMarkerList.length)
		}

		function drawRoute(assignments, routeMarkerList, directionsDisplay, locations, color) {
			console.log("1: locations:"+JSON.stringify(locations));
			
			
			if (locations.length > 1) {
				routeCallback(assignments, routeMarkerList, locations, status);
				
				
				
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
			            		  if (typeof combined.routes !== 'undefined') {

			            		  combined.routes[0].legs = combined.routes[0].legs.concat(results[i][1].routes[0].legs);
			            		  combined.routes[0].overview_path = combined.routes[0].overview_path.concat(results[i][1].routes[0].overview_path);

			            		  combined.routes[0].bounds = combined.routes[0].bounds.extend(results[i][1].routes[0].bounds.getNorthEast());
			            		  combined.routes[0].bounds = combined.routes[0].bounds.extend(results[i][1].routes[0].bounds.getSouthWest());
			            	  
			            		  }
			            	  }
		            	  }
		            	  if (typeof combined.routes !== 'undefined') {
		            	      directionsDisplay.setDirections(combined);
		            	  }
		              }
		          );
				
				
				/*
				var request;
				if ( locations.length > 2) {
					var waypts = [];
					for (var i = 1; i < locations.length -1; i++) {
						waypts.push({
							location: locations[i],
							stopover: true
						});
					}
					console.log("waypts:" + JSON.stringify(waypts));
					request = {
							origin: locations[0],
							destination: locations[locations.length - 1],
							waypoints: waypts,
							travelMode: google.maps.TravelMode.WALKING,
							avoidHighways: true,
						};
				} else {
					request = {
							origin: locations[0],
							destination: locations[locations.length - 1],
							travelMode: google.maps.TravelMode.WALKING,
							avoidHighways: true,
						};
				}

				directionsService.route(request, function(result, status) {
					if (status == google.maps.DirectionsStatus.OK) {
						directionsDisplay.setDirections(result);
						routeCallback(assignments, routeMarkerList, locations, result, status)
					
					}
					
				});
				*/

			} else {
				assignment = assignments[0]; //getAssignmentByPosition(assignments, locations[0]);	
				console.log("createMarker 1 :" + assignment.color);
				routeMarkerList.push(createMarker(map, assignment, assignment.position, 1, assignment.color));
			}
		}
		
		function callDirectionService(i, request){
			 var p = new promise.Promise();
			directionsService.route(request, function(result, status) {
				routeMakerList = [];
				routeMakerList.splice(0,routeMakerList.length)
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
		
		
		
		function routeCallback(assignments, routeMarkerList, locations, status) {
			// draw custom marker					
			//var legs = result.routes[0].legs;
			//console.log("legs:"+JSON.stringify(legs));
			console.log("locations:"+JSON.stringify(locations));
			console.log("assignments:"+JSON.stringify(assignments));
			for(var i = 0; i < locations.length ; i++) {
				assignment = assignments[i]; //getAssignmentByPosition(assignments, locations[i]);
				for (var j = 0; j < routeMarkerList.length ; j++) {			
					if (routeMarkerList[j].streetAddress == assignment.address) {
						routeMarkerList[j].labelContent = "(N)";
						//routeMarkerList[j].infowindow.content = routeMarkerList[j].infowindow.content + getInfoText(assignment);
						var content = routeMarkerList[j].infowindow.content + assignment.displaySeq + ") "+ getInfoText(assignment);;
						routeMarkerList[j].infowindow.content = content;
						routeMarkerList[j].infowindow.setContent(content);
						routeMarkerList[j].title = routeMarkerList[j].title +","+ assignment.firm;
						break;
					}
				}
				if (routeMarkerList.length == 0 || j == routeMarkerList.length) {
					console.log("createMarker 2");
					var marker = createMarker(map, assignment, locations[i], assignment.displaySeq, assignment.color);
					var content = assignment.displaySeq+") "+marker.infowindow.content ;
					marker.infowindow.content = content;
					marker.infowindow.setContent(content);
					routeMarkerList.push(marker);
					//routeMarkerList.push(createMarker(map, assignment, legs[i].start_location, assignment.displaySeq, assignment.color));
					
				}
			}
			
			/*
			assignment = assignments[legs.length]; //getAssignmentByPosition(assignments, locations[legs.length]);
			for (var j = 0; j < routeMarkerList.length ; j++) {			
				if (routeMarkerList[j].streetAddress == assignment.address) {
					routeMarkerList[j].labelContent = "(N)";
					routeMarkerList[j].infowindow.content = routeMakerList[j].infowindow.content + assignment.displaySeq + ") "+ getInfoText(assignment);
					routeMarkerList[j].title = routeMakerList[j].title +","+ assignment.firm;
					//routeMarkerList[j].infowindow.content = routeMarkerList[j].infowindow.content + getInfoText(assignment);
					//routeMarkerList[j].title = routeMarkerList[j].title +","+ assignment.firm;
					break;
				}
			}
			if (routeMarkerList.length == 0 || j == routeMarkerList.length) {
				console.log("createMarker 3");
				var marker = createMarker(map, assignment, legs[legs.length - 1].end_location, assignment.displaySeq, assignment.color);
				marker.infowindow.content = assignment.displaySeq+") "+marker.infowindow.content ;
				routeMarkerList.push(marker);
				//routeMarkerList.push(createMarker(map, assignment, legs[legs.length - 1].end_location, assignment.displaySeq, assignment.color));
			}	
			*/
		}
		
		function getMajorLocationById(majorLocations, id) {
			for (var i=0 ; i < majorLocations.length ; i++) {
				if (majorLocations[i].majorLocationId == id) {
					return majorLocations[i];
				}
			}
			return null;
		}
		
		function getAssignmentByPosition(assignments, position) {
			for (var i=0 ; i < assignments.length ; i++) {
				if (assignments[i].position == position) {
					return assignments[i];
				}
			}
		}
		
		function getNewLocationsSequence(majorLocations, data) {
			var newLocationSeq=[];
			for(var i = 0; i < data.length; i++) {
				console.log("data[i]:"+data[i]);
				if (data[i] != "") {
					majorLocation = getMajorLocationById(majorLocations, eval(data[i]));
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
		
		function getAssignments(majorLocations) {
			var assignments = [];
			var displaySeq = 1;
			for (var i=0 ; i < majorLocations.length ; i++) {
				majorLocation = majorLocations[i];
				if (!majorLocation.isNewRecruitmentTask && !majorLocation.isFreeEntryTask){
					majorLocation.color = assignMarkerColor(majorLocations);
					console.log("majorLocation.color " + i + " : " + majorLocation.color);
				}
				if (majorLocation.itineraryPlanOutlets.length > 0) {
					for (var j=0 ; j < majorLocation.itineraryPlanOutlets.length ; j++) {
						itineraryPlanOutlet = majorLocation.itineraryPlanOutlets[j];
						var latLng = new google.maps.LatLng( Number(itineraryPlanOutlet.latitude),  Number(itineraryPlanOutlet.longitude));
						itineraryPlanOutlet.position = latLng;
						itineraryPlanOutlet.color = majorLocation.color;
						itineraryPlanOutlet.displaySeq = displaySeq;
						displaySeq++;
						console.log("itineraryPlanOutlet.color " + j + " : " + itineraryPlanOutlet.color);
						assignments.push(itineraryPlanOutlet);
					}					
				}
			}
			return assignments
		}
		
		function getMapdata(majorLocations) {
			mapdata = [];
			for (var i=0 ; i < majorLocations.length ; i++) {
				majorLocation = majorLocations[i];
				
				if (majorLocation.isNewRecruitmentTask == false || 
						majorLocation.isFreeEntryTask == false) {
					mapdata.push(majorLocation.majorLocationId);
	
				}
			}
			return mapdata;
		}
		// Google Map END
		
		
		
		// Plan Data
		// Create an array of Major location
		
		majorLocations = ${commonService.jsonEncode(planned.majorLocations)};
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
			<c:forEach items="${planned.majorLocations}" var="mj" varStatus="i">
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
						district : "<c:out value="${assignment.district}" />",
						tpu : "<c:out value="${assignment.tpu}" />",
						address : "<c:out value="${assignment.address}" />",
						detailAddress : "<c:out value="${assignment.detailAddress}" />",
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
		
			visitedMajorLocations = ${commonService.jsonEncode(visited.majorLocations)};
			visitedMajorLocations = visitedMajorLocations || [];
			
			for (i = 0; i < visitedMajorLocations.length; i++){
				var omj = visitedMajorLocations[i];
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
			visitedMajorLocations = [
			      			<c:forEach items="${visited.majorLocations}" var="mj" varStatus="i">
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
			      						district : "<c:out value="${assignment.district}" />",
			      						tpu : "<c:out value="${assignment.tpu}" />",
			      						address : "<c:out value="${assignment.address}" />",
			      						detailAddress : "<c:out value="${assignment.detailAddress}" />",
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

		$(document).ready(function() {
			
			
			$('#itineraryPlanBtn').on('click', function() {
				$("#itineraryPlanCheckingModal").modal('show');
			});
			
			var initMap = false;
			$('#itineraryPlanCheckingModal').on('shown', function () {
				if (initMap == false) {
					plannedLocationSeq = getNewLocationsSequence(majorLocations, plannedMapdata);
					console.log("plannedLocationSeq:"+JSON.stringify(plannedLocationSeq));
					if (plannedLocationSeq.length > 0) {
						drawRoute(plannedAssignments, plannedRouteMakerList, plannedDirectionsDisplay, plannedLocationSeq, 'red');
						if (plannedLocationSeq.length > 1) {
							plannedDirectionsDisplay.setMap(map);
						} else {
							plannedDirectionsDisplay.setMap(null);
						}
					}

					visitedLocationSeq = getNewLocationsSequence(visitedMajorLocations, visitedMapdata);
					if (visitedLocationSeq.length > 0) {
						drawRoute(visitedAssignments, visitedRouteMakerList, visitedDirectionsDisplay, visitedLocationSeq, 'green');
						if (visitedLocationSeq.length > 1) {
							visitedDirectionsDisplay.setMap(map);
						} else {
							visitedDirectionsDisplay.setMap(null);
						}
					}

					$('#plannedSequence').prop('checked', true);
					$('#visitedSequence').prop('checked', true);
					fixBound(map);
					try{
				    	google.maps.event.trigger(map, "resize");
					}
					catch(e){}
			    	initMap = true;
				}
			    
			});
			
			$('#plannedSequence').on('click', function() {
				if ($(this).is(':checked')) {
					plannedDirectionsDisplay.setMap(map);
					setRouteMarkerMap(plannedRouteMakerList,map);
				} else {
					plannedDirectionsDisplay.setMap(null);
					setRouteMarkerMap(plannedRouteMakerList,null);
				}

				fixBound(map)
			})
			
			$('#visitedSequence').on('click', function() {
				if ($(this).is(':checked')) {
					visitedDirectionsDisplay.setMap(map);
					setRouteMarkerMap(visitedRouteMakerList,map);
				} else {
					visitedDirectionsDisplay.setMap(null);
					setRouteMarkerMap(visitedRouteMakerList,null);
				}

				fixBound(map)
			})
			
			$('#rejectBtn').on('click', function() {
				rejectRecordsWithConfirm();
			})
			
			$('#approveBtn').on('click', function() {
				approveRecordsWithConfirm();
			})
							
			telephoneTable = $('#telEnuTable').DataTable( {
				"rowId" : 'index',
				"bPaginate": false,
			    "bInfo": false,
				"buttons" : [],
	            "columns": [
	                        { "data": "referenceMonth" },
	                        { "data": "survey" },
	                        { "data": "caseReferenceNo" },
	                        { 
	                        	"data": "completionQuotationCount",
                            	"render" : function(data, type, full, meta){
                            		var html = full.completionQuotationCount + " / " + full.completionTotalQuotation;
                            		return html;
                           		}
	                        },
	                        { 
	                        	"data": "deletionQuotationCount",
                            	"render" : function(data, type, full, meta){
                            		var html = full.deletionQuotationCount + " / " + full.deletionTotalQuotation;
                            		return html;
                           		}
	                        },
	                        { 
	                        	"data": "session",
	                        	"render" : function(data, type, full, meta){
                            		if (data == 'A')
                            			return 'AM';
                            		else if (data == 'P')
                            			return 'PM';
                            		else
                            			return '';
                           		}
	                        	
	                        }

						],
                "columnDefs": [
                            	{
                            		"targets": "_all",
                               		"className" : "text-center"
                               	},
                           ]

			} );
			
			var buttons = [];	
			$.fn.dataTable.addResponsiveButtons(buttons);
				
			fieldworkTable = $('#fieldworkTable').DataTable( {
				"rowId" : 'index',
			    "bPaginate": false,
			    "bInfo": false,
			    "order": [[ 1, "asc" ]],
			    "ordering" : true,
				"buttons" : buttons,
				"autoWidth": false,
	            "columns": [
	                        { "data": "referenceMonth" },
	                        { "data": "startTime" },
	                        { "data": "survey" },
	                        { "data": "caseReferenceNo" },
	                        { "data": "activity" },
	                        { "data": "enumerationOutcome" },
	                        { "data": "marketQuotationCount",
                            	"render" : function(data, type, full, meta){
                            		var html = full.marketQuotationCount + " / " + full.marketTotalQuotation;
                            		return html;
                           		}
	                        },
	                        { "data": "nonMarketQuotationCount",
                            	"render" : function(data, type, full, meta){
                            		var html = full.nonMarketQuotationCount + " / " + full.nonMarketTotalQuotation;
                            		return html;
                           		}
	                        },
	                        { "data": "building" },
	                        { "data": "destination" },
	                        { "data": "transport" },
	                        { "data": "expenses" },
	                        { "data": "remark" }

						],
                "columnDefs": [
                               {
                                   "targets": [1],
                                   "className" : "text-center",
                                   "orderable": true,
                                   "orderSequence": [ "asc" ],
                                   
                               },	 	
                               {
                            		"targets": "_all",
                               		"className" : "text-center",
                               		"orderable": false,
                               },
                           ]
				} );
			$("#collapseBtn").click(function(){
				$("#filters").toggle();
				var icon = $(this).find('i');
				if (icon.hasClass('fa-minus')){
					icon.removeClass('fa-minus').addClass('fa-plus')
				}else{
					icon.removeClass('fa-plus').addClass('fa-minus')
				}
			})
			
			telephoneTimeLogs = ${commonService.jsonEncode(model.telephoneTimeLogs)};
			telephoneTimeLogs = telephoneTimeLogs || [];
			for (i = 0; i < telephoneTimeLogs.length; i++){
				var p = telephoneTimeLogs[i];
				p.index = i + 1;
				p.completionQuotationCount = p.completionQuotationCount == null? "" : p.completionQuotationCount;
				p.completionTotalQuotation = p.completionTotalQuotation == null? "" : p.completionTotalQuotation;
				p.deletionQuotationCount = p.deletionQuotationCount == null? "" : p.deletionQuotationCount;
				p.deletionTotalQuotation = p.deletionTotalQuotation == null? "" : p.deletionTotalQuotation;
			}
			
			<%--
			telephoneTimeLogs = [
			<c:forEach items="${model.telephoneTimeLogs}" var="ti" varStatus="i">
				{
					telephoneTimeLogId : <c:out value="${ti.telephoneTimeLogId}" />,
					index : "<c:out value='${i.index+1}' />",
					referenceMonth : "<c:out value='${ti.referenceMonth}' />",
					survey : "<c:out value='${ti.survey}' />",
					caseReferenceNo : "<c:out value='${ti.caseReferenceNo}' />",
					status : "<c:out value='${ti.status}' />",
					completionQuotationCount : "<c:out value='${ti.completionQuotationCount}' />",
					completionTotalQuotation : "<c:out value='${ti.completionTotalQuotation}' />",
					deletionQuotationCount : "<c:out value='${ti.deletionQuotationCount}' />",
					deletionTotalQuotation : "<c:out value='${ti.deletionTotalQuotation}' />",
					session : "<c:out value='${ti.session}' />",

				},
			</c:forEach>
			];
			--%>
			
			telephoneTable.rows.add(telephoneTimeLogs).draw();
			calculateTelephoneCounts();
			
			fieldworkTimeLogs = ${commonService.jsonEncode(model.fieldworkTimeLogs)};
			fieldworkTimeLogs = fieldworkTimeLogs || [];
			for (i = 0; i < fieldworkTimeLogs.length; i++){
				var p = fieldworkTimeLogs[i];
				p.index = i + 1;
				p.marketQuotationCount = p.marketQuotationCount == null? "" : p.marketQuotationCount;
				p.marketTotalQuotation = p.marketTotalQuotation == null? "" : p.marketTotalQuotation;
				p.nonMarketQuotationCount = p.nonMarketQuotationCount == null? "" : p.nonMarketQuotationCount;
				p.nonMarketTotalQuotation = p.nonMarketTotalQuotation == null? "" : p.nonMarketTotalQuotation;
				p.building = p.building == null? "" : p.building;
			}
			
			<%--
			fieldworkTimeLogs = [
    				<c:forEach items="${model.fieldworkTimeLogs}" var="fi" varStatus="i">
    					{
    						fieldworkTimeLogId : <c:out value="${fi.fieldworkTimeLogId}" />,
    						index : "<c:out value='${i.index+1}' />",
    						referenceMonth : "<c:out value='${fi.referenceMonth}' />",
    						startTime : "<c:out value='${fi.startTime}' />",
    						survey : "<c:out value='${fi.survey}' />",
    						caseReferenceNo : "<c:out value='${fi.caseReferenceNo}' />",
    						activity : "<c:out value='${fi.activity}' />",
    						enumerationOutcome : "<c:out value='${fi.enumerationOutcome}' />",
    						marketQuotationCount : "<c:out value='${fi.marketQuotationCount}' />",
    						marketTotalQuotation : "<c:out value='${fi.marketTotalQuotation}' />",
    						nonMarketQuotationCount : "<c:out value='${fi.nonMarketQuotationCount}' />",
    						nonMarketTotalQuotation : "<c:out value='${fi.nonMarketTotalQuotation}' />",
    						building : "<c:out value='${fi.building}' />",
    						destination : "<c:out value='${fi.destination}' />",
    						transport : "<c:out value='${fi.transport}' />",
    						remark : "<c:out value='${fi.remark}' />",
    						expenses : "<c:out value='${fi.expenses}' />",
    						recordType : "<c:out value='${fi.recordType}' />",
    					},
    				</c:forEach>
    			];
    		--%>

			fieldworkTable.rows.add(fieldworkTimeLogs).draw();
			calculateFieldworkCounts();
			
			plannedAssignments = getAssignments(majorLocations);
			visitedAssignments = getAssignments(visitedMajorLocations);
			

			function fixBound(map){
				var latLngs = [];
				if ($("#plannedSequence").is(":checked")){
					for (var i=0; i < plannedAssignments.length; i++) {
						var latLng = new google.maps.LatLng( Number(plannedAssignments[i].latitude),  Number(plannedAssignments[i].longitude));
						latLngs.push(latLng);
					}
				}
				
				if ($("#visitedSequence").is(":checked")){
					for (var i=0; i < visitedAssignments.length; i++) {
						var latLng = new google.maps.LatLng( Number(visitedAssignments[i].latitude),  Number(visitedAssignments[i].longitude));
						latLngs.push(latLng);
					}
				}
				
				if (latLngs.length > 0) {			
					var bounds = new google.maps.LatLngBounds();
					for(i=0;i<latLngs.length;i++) {
						bounds.extend(latLngs[i]);
					}
					//console.log(bounds);
					map.setCenter(bounds.getCenter());
					map.fitBounds(bounds);
				}
			}
			

			plannedMapdata = getMapdata(majorLocations);
			visitedMapdata = getMapdata(visitedMajorLocations);

			if (visitedAssignments.length > 0){
				var position = new google.maps.LatLng( Number(visitedAssignments[0].latitude),  Number(visitedAssignments[0].longitude));
				
				if (map == null) {
					var mapOptions = {
							zoom: 16,
							center: position,
							mapTypeId: google.maps.MapTypeId.ROADMAP
						};
					map = new google.maps.Map(document.getElementById("map_canvas"), mapOptions);
				}
			}
			
			
			plannedDirectionsDisplay = new google.maps.DirectionsRenderer({
				suppressMarkers: true,
				preserveViewport: false,
                    polylineOptions: {
                        strokeWeight: 4,
                        strokeOpacity: 0.8,
                        strokeColor: "red"
                    },
			});
			
			visitedDirectionsDisplay = new google.maps.DirectionsRenderer({
				suppressMarkers: true,
				preserveViewport: false,
                    polylineOptions: {
                        strokeWeight: 4,
                        strokeOpacity: 0.8,
                        strokeColor: "green"
                    },
			});
			
		});
		</script>	
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
        	<h1>Reconciliation Checking Approval</h1>
        	<c:if test="${not empty model.timeLogId}">
				<div class="breadcrumb form-horizontal" style="width:240px">
					<div class="form-group" style="margin-bottom:0px">
			        	<div class="col-sm-5">Created Date:</div>
			        	<div class="col-sm-7">${model.createdDateDisplay}</div>
			        </div>
			        <div class="form-group" style="margin-bottom:0px">
			         	<div class="col-sm-5">Last Modified:</div>
			         	<div class="col-sm-7">${model.modifiedDateDisplay}</div>
			         </div>
		      	</div>
	      	</c:if>
        </section>

        <section class="content">
        	<form id="mainForm"  method="post" role="form" action="<c:url value='/timeLogManagement/ItineraryCheckingApproval/approveView'/>"
        		enctype="multipart/form-data">
        		<input name="timeLogId" value="<c:out value="${model.timeLogId}" />" type="hidden" />
        		<input name="rejectReason" id="rejectReason" value="<c:out value="${model.rejectReason}" />" type="hidden" />
        		<input id="assignmentDeviation" value="<c:out value="${model.timeLogId}" />" type="hidden" />
        		<input id="sequenceDeviation" value="<c:out value="${model.timeLogId}" />" type="hidden" />
        		<input id="tpuDeviation" value="<c:out value="${model.timeLogId}" />" type="hidden" />
        		<input type="hidden" name="status" id="status" value="<c:out value="${model.status}" />">
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
	        					<div class="row">
	        						<div class="col-md-1">
										<a class="btn btn-default" href="<c:url value='/timeLogManagement/ItineraryCheckingApproval/home'/>">Back</a>
									</div>
									<div class="col-md-11">
										<div class="form-horizontal">
											<div class="form-group" style="margin-bottom: 0px">
												<label class="col-md-1 control-label">Remark:</label>
												<div class="col-md-11">
													<p class="form-control-static"><c:out value="${model.itineraryCheckRemark}" /></p>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
	       					<div class="box-body">
	       						<div class="form-horizontal">
	       							<div class="col-sm-12">
	       								<c:forEach var="survey" items="${surveyCountList}">						
	       							    <div class="form-group">
	       							    	<label class="col-md-2 control-label"><c:out value="${survey}" /> - C(FI):</label>
	       							    	<div class="col-md-1">
	       							    		<p id="<c:out value="${survey}" />_C_FI_count" class="form-control-static">0</p>
	       							    	</div>
	       							    	<label class="col-md-1 control-label"><c:out value="${survey}" /> - C(TI):</label>
	       							    	<div class="col-md-1">
	       							    		<p id="<c:out value="${survey}" />_C_TI_count" class="form-control-static">0</p>
	       							    	</div>
	       							    </div>
       							        </c:forEach>
	       							    <div class="form-group">
       							    		<label class="col-md-2 control-label">Pre-Approval:</label>
	       							    	<div class="checkbox col-md-1">
												<label><input id="isPreApproval" name="isPreApproval" type="checkbox" value="true" <c:if test="${model.isPreApproval}">checked</c:if>></label>
											</div>
	       							    </div>
	       							    <div class="form-group">
       							    		<label class="col-md-2 control-label">Training:</label>
	       							    	<div class="checkbox col-md-1">
											  <label><input id="isTrainingAM" name="isTrainingAM" type="checkbox" value="true" readonly disabled <c:if test="${model.isTrainingAM}">checked</c:if>>AM</label>
											</div>
											&nbsp;
											<div class="checkbox col-md-1">
											  <label><input id="isTrainingPM" name="isTrainingPM" type="checkbox" value="true" readonly disabled <c:if test="${model.isTrainingPM}">checked</c:if>>PM</label>
											</div>
	       							    </div>
	       							    <div class="form-group">
       							    		<label class="col-md-2 control-label">VL/SL:</label>
	       							    	<div class="checkbox col-md-1">
											  <label><input id="isVLSLAM" name="isVLSLAM" type="checkbox" value="true" readonly disabled <c:if test="${model.isVLSLAM}">checked</c:if>>AM</label>
											</div>
											&nbsp;
											<div class="checkbox col-md-1">
											  <label><input id="isVLSLPM" name="isVLSLPM" type="checkbox" value="true" readonly disabled <c:if test="${model.isVLSLPM}">checked</c:if>>PM</label>
											</div>
	       							    </div>
	       							    <div class="form-group">
	       							    	<label class="col-md-2 control-label">Field Officer:</label>
		       							    <div class="col-md-2">
	       							    			<p class="form-control-static"><c:out value="${model.userCode}" /></p>
	       							    	</div>
	       							    </div>
	       							    <div class="form-group">
       							    		<label class="col-md-2 control-label">Date:</label>
	       							   		<div class="col-md-2">
	       							   			<p class="form-control-static"><c:out value="${model.date}" /></p>
											</div>
	       							    </div>
	       							    <div class="form-group">
	       							    	<label class="col-md-2 control-label">Working Sessions:</label>
	       							    	<div class="col-md-2">
												<p class="form-control-static"><c:out value='${model.workingSessionText}' /></p>
											</div>
	       							    </div>
	       							    <div class="form-group">
       							    		<label class="col-md-2 control-label">OT claimed:</label>
       							    		<div class="col-md-2">
	       							           <p class="form-control-static"><c:out value='${model.otClaimed}'/></p>
	       							    	</div>
	       							    	<div class="checkbox col-md-2">
											  <label><input name='isClaimOT' type="checkbox" value="true" readonly disabled <c:if test="${model.isClaimOT}">checked</c:if>>Claim OT</label>
											</div>
       							    	</div>
       							    	<div class="form-group">
       							    		<label class="col-md-2 control-label">Timeoff taken:</label>
       							    		<div class="col-md-2">
	       							           <p class="form-control-static"><c:out value='${model.timeoffTaken}'/></p>
	       							    	</div>
       							    	</div>	 						    	
		       							<div class="form-group">
			       							<label class="col-sm-2 control-label">Telephone Enumeration:</label>
			       							<div class="col-sm-10">&nbsp;</div>
			       							<div class="col-sm-12">
				       							<div class="col-sm-1">&nbsp;</div>
				       							<div class="col-sm-11">
				       								<table id="telEnuTable" class="table nowrap">
												        <thead>
												            <tr>
												                <th>Reference<br>Month</th>
												                <th>Survey</th>
												                <th>Case Reference No.</th>
												                <th>Completion</th>
												                <th>Deletion</th>
												                <th>Section</th>
												            </tr>
												     	</thead>
												     	<tbody>	
												     	</tbody>
												     </table>
											     </div>
										     </div>
		       							</div>
		       							<div class="form-group">
			       							<label class="col-sm-2 control-label">Fieldwork Activities:</label>
		       								<div class="col-sm-10"><button id="itineraryPlanBtn" type="button" class="pull-right btn btn-info">Details of Itinerary Planning</button></div>
				       						<div class="col-sm-12">
					       					<div class="col-sm-1">&nbsp;</div>
					       						<div class="col-sm-11">
						       						<table id="fieldworkTable" class="table nowrap responsive">
												        <thead>
												            <tr>
												                <th>Reference<br>Month</th>
												                <th>Start time</th>
												                <th>Survey</th>
												                <th>Case Reference No. </th>
												                <th>Activity</th>
												                <th>Enumeration<br>Outcome</th>
												                <th>Market</th>
												                <th>Non-Market</th>
												                <th>Building</th>
												                <th>Designation</th>
												                <th>Mode of Transport</th>
												                <th>Expense<br>HK$</th>
												                <th>Remark</th>
												            </tr>
												     	</thead>
												     	<tbody>
												     	</tbody>
												     </table>
												 </div>
											 </div>
										</div>
	       							    <div class="form-group">
	       							    	<c:if test="${model.status} == SystemContant.TIMELOG_STATUS_REJECTED">
		       							    	<label class="col-md-2 control-label">Reject Reason:</label>
			       							    <div class="col-md-8">
		       							    		<p class="form-control-static"><c:out value="${model.rejectReason}" /></p>
		       							    	</div>
	       							    	</c:if>
	       							    </div>
									</div>
								</div>
							</div>
							<sec:authorize access='hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4)'>
							<div class="box-footer">
        						<button id="approveBtn" type="button" class="btn btn-info">Approve</button>
        						<button id="rejectBtn" type="button" class="btn btn-info">Reject</button>       						
       						</div>
       						</sec:authorize>
						</div>		
	        		</div>
	        	</div>
	        	<!-- Reject Reason Dialog -->
				<div class="modal fade" id="rejectModal" tabindex="-1" role="dialog" aria-labelledby="dialogLabel" aria-hidden="true">
				  <div class="modal-dialog">
					<div class="modal-content">
						<div  class="clearfix overlay" data-bind="loading:isLoading, visible:isLoading" ></div>
						  <div class="modal-header">
							<h4 class="modal-title" id="dialogLabel" data-bind="text:formTitle">Reject Dialog</h4>
						  </div>
						  <div class="modal-body form-horizontal">
							<div class="form-group">
								<div class="col-md-offset-1 col-md-10 ">Reject Reason</div>
								<div class="col-md-offset-1 col-md-10 ">
									<textarea class="form-control" rows="5" name="reason" id="reason"></textarea>
								</div>
								<div style="margin-top: 30px;" class="col-md-3 control-label"></div>
								<div style="margin-top: 30px;" class="col-md-3">
									<button type="submit" class="btn btn-primary" data-loading-text="Loading..." id="rejectConfirmBtn">Submit</button>
								</div>
								<div style="margin-top: 30px;" class="col-md-6">
									<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
								</div>
							</div>
						  </div>
					</div>
				  </div>
				</div>
				<!-- Itinerary Plan Checking Dialog -->
				<div class="modal modal-wide fade" id="itineraryPlanCheckingModal" tabindex="-1" role="dialog" aria-labelledby="dialogLabel" aria-hidden="true">
				  <div class="modal-dialog">
					<div class="modal-content">
						<div  class="clearfix overlay" data-bind="loading:isLoading, visible:isLoading" ></div>
						  <div class="modal-header">
							<h4 class="modal-title" id="dialogLabel" data-bind="text:formTitle">Itinerary Comparison</h4>
						  </div>
						  	<div class="modal-body form-horizontal">
								<div class="form-group">
      							    <div class="checkbox col-md-2">
										<label><input id="plannedSequence" name="plannedSequence" type="checkbox" checked>Planned Sequence</label>
									</div>
									&nbsp;
									<div class="checkbox col-md-2">
									  <label><input id="visitedSequence" name="visitedSequence" type="checkbox" checked>Visited Sequence</label>
									</div>
	       						</div>
								<!--  Map -->
								<div class="col-sm-12" >
									<div  style="height: 500px;">
									<div id="map_canvas" class="map_canvas"></div>
									</div>
									<div>
										<button class="btn btn-info" type="button" onclick="toggleInfoWindowMarker(true)">Open All Info Window</button>
										<button class="btn btn-info" type="button" onclick="toggleInfoWindowMarker(false)">Close All Info Window</button>
									</div>
									<div style="margin-top: 30px;" class="col-md-3 control-label"></div>
									<div style="margin-top: 30px;" class="col-md-3">
									</div>
									<div style="margin-top: 30px;" class="col-md-6">
										<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
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

