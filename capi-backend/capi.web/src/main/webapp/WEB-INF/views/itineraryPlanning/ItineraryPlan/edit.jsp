<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
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
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp" %>
	</jsp:attribute>
	<jsp:attribute name="header">
		<style>
		    body {
                overflow-x: hidden;
            }
			.modal.modal-wide .modal-dialog {
			  width: 90%;
			}
			.modal-wide .modal-body {
			  overflow-y: auto;
			}
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
			.bg_drop {	
				min-height: 100px;
				padding-right: 0px;
				margin-bottom: 4px;	
			}
			.locationSortable {	
				min-height: 10px;	
			}
			.taskpool {	
				padding-right: 0px;
				margin-bottom: 2px;	
			}
			.maplabels {
				color: white;
				font-family: "Lucida Grande", "Arial", sans-serif;
				font-size: 12px;
				text-align: center;
				width: 16px;
				white-space: nowrap;
			}
			.btnRemoveTask {
			    float: right; 
			    padding: 2px 0px 6px 2px;
			}
			.checklist {
				margin: 5px 1% 5px 1%;
				width: 31.33%
			}
			.assigned {
				margin: 5px 1% 5px 1%;
				width: 98%
			}
			.hidden {
				display: none;
			}
			.glyphicon-pencil, .glyphicon-remove, .glyphicon-plus {
    			cursor: pointer;
			}
			.textlink {
    			cursor: pointer;
			}
			li.invalidTime label{
				color: red;
			}

		</style>
		
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp"%>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp"%>
		<%@include file="/WEB-INF/views/includes/bootstrap-wizard.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp"%>
		<%@include file="/WEB-INF/views/includes/commonDialog/assignmentLookupOutlet.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/assignmentLookupBuilding.jsp" %>
		<%@include file="/WEB-INF/views/includes/datejs.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker.jsp" %>		
		<script src="<c:url value='/resources/js/jquery-ui.min.js'/>"></script>
		<script src="<c:url value='/resources/js/jquery.validate.custom.js'/>"></script>
		<script src="<c:url value='/resources/js/jquery.tmpl.min.js'/>"></script>
		<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=${googleBrowserKey}&libraries=places&language=zh-tw"></script>
		<script src="<c:url value='/resources/js/markerwithlabel.js'/>"></script>
		<script src="<c:url value='/resources/js/underscore-min.js' />"></script>
		<script src="<c:url value='/resources/js/jquery.fireOnDisable.js' />"></script>
		<script src="<c:url value='/resources/js/promise.js' />"></script>
		<script src="<c:url value='/resources/js/underscore-min.js' />"></script>
				
		<%-- map config --%>
		<script src="<c:url value='/resources/js/map.js' />"></script>
		<script>

			// Google Map START

			var directionsDisplay;
			var directionsService = new google.maps.DirectionsService();
			var map1;
			var map2;
			var map3;
			var markers1 = [];
			var markers2 = markers1;
			var routeMakerList = markers1;
			/*
			var routeMakerList = [];
			var markers1 = [];
			var markers2 = [];
			*/
			//var markers3 = [];
			var infoWindows2 = [];
			var majorLocationSeq = [];
			
			var plannedDates = <c:choose><c:when test="${not empty planDates}">${planDates}</c:when><c:otherwise>[]</c:otherwise></c:choose>;
			var nonWorkingDates = <c:choose><c:when test="${not empty nonWorkingDates}">${nonWorkingDates}</c:when><c:otherwise>[]</c:otherwise></c:choose>;

			// tab 1
			function toggleInfoWindowMarker1(open){
				toogleInfoWindow(map1, markers1, open);
			}
			
			// tab 2
			function toggleInfoWindowMarker2(open){
				toogleInfoWindow(map2, markers2, open);
			}
			
			// tab 3
			function toggleInfoWindowMarker3(open){
				toogleInfoWindow(map3, routeMakerList, open);
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
			
			function fixBound(map){
				var latLngs = [];
				for (var i=0; i < assignments.length; i++) {
					var latLng = new google.maps.LatLng( Number(assignments[i].latitude),  Number(assignments[i].longitude));
					latLngs.push(latLng);
				}
				if (latLngs.length > 0) {			
					var bounds = new google.maps.LatLngBounds();
					for(i=0;i<latLngs.length;i++) {
						bounds.extend(latLngs[i]);
					}
					map.setCenter(bounds.getCenter());
					map.fitBounds(bounds);
				}
			}
			
			function setMarkersOnMap(map, markers, label) {
				
				// Clear all markers
				for (var i = 0; i < markers.length; i++) {
					markers[i].setMap(null);
				}
				//markers = [];
				// remove all markers in array
				markers.splice(0,markers.length)
				
				fixBound(map);
				
				genMarker(map, markers, label)
				//fitMarkers(map, markers);
				
			}
			
			function genMarker(map, markers, label) {
				// Add markers
				for (var i=0; i < assignments.length; i++) {
					var assignment = assignments[i];			
					for ( j = 0; j < markers.length; j++) {
						if (markers[j].streetAddress == assignment.address) {
							//if (label == true ){
								markers[j].labelContent = "(M)";
							//}
							var content = markers[j].infowindow.content + getInfoText(assignment);;
							markers[j].infowindow.content = content;
							markers[j].infowindow.setContent(content);
							markers[j].title = markers[j].title +","+ assignment.firm;
							break;
						}
					}
		            if (markers.length == 0 || j == markers.length) {  
		                var latLng = new google.maps.LatLng( Number(assignment.latitude),  Number(assignment.longitude));
		                if (label) {
		                	//markerLabel = assignment.seq.toString();
		                	markerLabel = "";
		                } else {
		                	markerLabel = "";
		                }
		                if ( label == true){
		                	var ml = getMajorLocationByIndex(assignment.group);
		                	if (ml != null){
		                		markers.push(createMarker(map, assignment, latLng, markerLabel, getMajorLocationByIndex(assignment.group).color));
		                	}
		                	else{
		                		markers.push(createMarker(map, assignment, latLng, markerLabel, "black"));
		                	}
		                }
		                else 
		                	markers.push(createMarker(map, assignment, latLng, markerLabel, "Red"));
		            }
				}
			}
			
			
			
			function fitMarkers(map, markers) {
				if (map != null && markers.length > 0) {			
					var bounds = new google.maps.LatLngBounds();
					for(i=0;i<markers.length;i++) {
						bounds.extend(markers[i].getPosition());
					}
					map.fitBounds(bounds);
				}				
			}
			
			
			function getInfoText(assignment) {
				return assignment.firm + "<BR>";
			}
			
			function createMarker(map, assignment, location, labelContent, color, content) {
				var text = content || getInfoText(assignment);
            	var myinfowindow = new google.maps.InfoWindow({
            		    content: text,
            		});
            	;

	            var marker = new MarkerWithLabel({
					position: location,
					map: map,
					title: assignment.firm,
					animation: google.maps.Animation.DROP,
				    labelContent: labelContent,
				    labelAnchor: new google.maps.Point(8, 48),
				    labelClass: "maplabels", // the CSS class for the label
				    labelInBackground: false,
				    icon: pinSymbol(color),
				    streetAddress: assignment.address,
					infowindow : myinfowindow,
		         });	
	            /*marker.addListener('click', function() {
	            	myinfowindow.open(map, marker);
	              });*/

	            
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
				//routeMakerList = [];
			}
			
			function highlight(id,color) {
				  var a = document.getElementById(id);
				  a.style.backgroundColor = color;
			}

			function drawRoute(directionsDisplay, locations) {

				if (locations.length > 1) {
					
					fixBound(map3);
					
					var seq = 1;
					var markerList = [];
					
					for(var i = 0; i < locations.length ; i++) {
						assignment = getAssignmentByGroupAndSeq(locations[i].assignmentGroup, locations[i].assignmentSeq);
						var find = false;
						for (var j = 0; j < markerList.length ; j++) {			
							if (markerList[j].streetAddress == assignment.address) {
								markerList[j].labelContent = "(M)";
								//markerList[j].setLabel("(M)");
								var content = markerList[j].infowindowContent + (seq++) + ") " + getInfoText(assignment);;
								markerList[j].infowindowContent = content;
								//markerList[j].infowindow.setContent(content);
								markerList[j].title = markerList[j].title +","+ assignment.firm;
								find = true;
								break;
							}
						}
						if (!find) {
							//var marker = createMarker(map3, assignment, legs[i].start_location, i+1, getMajorLocationByIndex(assignment.group).color);
							var content = (seq++)+ ") " + getInfoText(assignment);
							markerList.push({
								infowindowContent:content,
								streetAddress: assignment.address,
								labelContent: i+1,
								title: assignment.firm,
								start_location: locations[i].position,//legs[i].start_location,
								assignment: assignment,
								color: getMajorLocationByIndex(assignment.group).color
							})
							
							//markerList.push(marker);
						}
					}
										
					for(var i = 0; i < markerList.length ; i++) {
						var marker = createMarker(map3, markerList[i].assignment, markerList[i].start_location, markerList[i].labelContent, markerList[i].color, markerList[i].infowindowContent)
						routeMakerList.push(marker);
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
									location: locations[y].position,
									stopover: true
								});
							}
							request = {
								origin: locations[startIndex-1].position,
								destination: locations[endIndex + 1].position,
								waypoints: waypts,
								travelMode: google.maps.TravelMode.WALKING,
								avoidHighways: true
							};
							requests.push(callDirectionService(i, request));
						}						
						
					} else {
						request = {
								origin: locations[0].position,
								destination: locations[locations.length - 1].position,
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
					assignment = getAssignmentByGroupAndSeq(locations[0].assignmentGroup, locations[0].assignmentSeq);
		            var latLng = new google.maps.LatLng( Number(assignment.latitude),  Number(assignment.longitude));
		            var marker = createMarker(map3, assignment, latLng, 1, getMajorLocationByIndex(assignment.group).color);
		           // marker.infowindow.content = (seq++) + ") " + getInfoText(assignment);
					routeMakerList.push(marker);
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
			
			
			
			function timeValidation(majorLocatioIndex, sessionId){
				//if (majorLocatioIndex > majorLocations.length -1)
				//	return;
				
				//var majorLocation = majorLocations[majorLocatioIndex];
				var majorLocation = _.find(majorLocations, function(loc){
					return loc.index == majorLocatioIndex;
				});
				if (majorLocation == null) return true;
				var assignments = majorLocation.assignments;				
				if (assignments == null) return true;
				
				var session = $("#"+sessionId+"_label").html().split(" ")[0];
				for (i = 0; i < assignments.length; i++){
					var assignment = assignments[i];
					
					var hasValue1 = true;
					var hasValue2 = true;
					var valid1 = true;
					var valid2 = true;
					
					if (assignment!=null && assignment !== undefined && assignment.convenientTime != null 
							&& assignment.convenientTime !== undefined && assignment.convenientTime.length == 11) {
						var times = assignment.convenientTime.split('-');						
						startTime = times[0];
						endTime = times[1];
						
						if (!timeCheck(session, startTime, endTime)) {
							valid1 = false;
						}
					}
					else{
						hasValue1= false;
					}
					
					if (assignment!=null && assignment !== undefined && assignment.convenientTime2 != null 
							&& assignment.convenientTime2 !== undefined && assignment.convenientTime2.length == 11) {
						var times = assignment.convenientTime2.split('-');
						startTime = times[0];
						endTime = times[1];
						
						if (!timeCheck(session, startTime, endTime)) {
							valid2 = false;
						}
					}
					else{
						hasValue2 = false;
					}
					
					if (hasValue1&&!valid1 && hasValue2&&!valid2 || !hasValue2&&hasValue1&&!valid1 || !hasValue1&&hasValue2&&!valid2){
						bootbox.alert({
						    title: "Alert",
						    message: "<spring:message code='W00023' />"
						});
						return false;
					}
				}
				
				return true;
			}
			
			function timeCheck(session, startTime, endTime){

				switch (session) {
				case "A":
					sessionStartTime = "06:00";
					sessionEndTime = "12:30";
					break;
				case "P":
					sessionStartTime = "12:30";
					sessionEndTime = "18:00";
					break;
				case "E":
					sessionStartTime = "18:00";
					sessionEndTime = "06:00";
					break;		
				}
				if (session == 'E') {
					if ( !(sessionEndTime < startTime && endTime < sessionStartTime) )
						return true;
					else
						return false;			
				} else {
					if (( startTime > sessionStartTime && startTime < sessionEndTime) || (endTime > sessionStartTime && endTime < sessionEndTime)) 
						return true;
					else
						return false;
				}
				
			}
			
			function loadRoute() {
				cleanRouteMarker();
				var data = [];
				
				data = data.concat($("#sortable_session1").sortable('toArray', { attribute: 'majorlocationRouteIndex' }));
				data = data.concat($("#sortable_session2").sortable('toArray', { attribute: 'majorlocationRouteIndex' }));
				data = data.concat($("#sortable_session3").sortable('toArray', { attribute: 'majorlocationRouteIndex' }));
				
				taskIds = $("#sortable_taskList").sortable('toArray', { attribute: 'id' });
				session1Ids = $("#sortable_session1").sortable('toArray', { attribute: 'id' });
				session2Ids = $("#sortable_session2").sortable('toArray', { attribute: 'id' });
				session3Ids = $("#sortable_session3").sortable('toArray', { attribute: 'id' });

				var i = 1;
				i = setTaskSessionWithSeq(taskIds, "", i);
				i = setTaskSessionWithSeq(session1Ids, "A", i);
				i = setTaskSessionWithSeq(session2Ids, "P", i);
				i = setTaskSessionWithSeq(session3Ids, "E", i);
				
				newLocationSeq = getNewLocationsSequence(data);
				if (newLocationSeq.length > 0) {
					drawRoute(directionsDisplay1, newLocationSeq);
					if (newLocationSeq.length > 1) {
						directionsDisplay1.setMap(map3);
					} else {
						directionsDisplay1.setMap(null);
					}
				}
			}
			// Google Map END
			
			// Assignment
			
			var selectedIds = [];

		    // Create an array of assignments
		    
		    var assignments = [];
		    
			var assignmentIndex = assignments.length + 1;	
		    var tpus = [];
		    var districts = [];
		    var outletIds = [];
		    
			function updateCounts() {
				noOfAssignment = 0;
				noOfQuotation = 0;
			    tpus = [];
			    districts = [];
			    outletIds = [];

				for (i = 0; i < assignments.length; i++) {
					noOfQuotation += assignments[i].noOfQuotation;
					tpus.push(assignments[i].tpu);
					districts.push(assignments[i].district);
					outletIds.push(assignments[i].outletId);
					assignments[i].seq = i + 1;
				}
				
				noOfAssignment = outletIds.length;
				tpus.sort();
				tpus = $.unique(tpus);
				districts.sort();
				districts = $.unique(districts);
				
				$("#noOfAssignment").text(noOfAssignment);
				$("#noOfQuotation").text(noOfQuotation);
				$("#noOfTpu").text(tpus.length);
				$("#noOfDistrict").text(districts.length);			
			}
			
			function getAssignmentByIndex(index) {
				for (var i=0 ; i < assignments.length ; i++) {
					if (assignments[i].index == index)
						return assignments[i];
				}
			}
			
			function getAssignmentByGroupAndSeq(group, seq) {
				for (var i=0 ; i < assignments.length ; i++) {
					if (assignments[i].group == group && assignments[i].seq == seq)
						return assignments[i];
				}
			}
			
			function addNewAssignmentToMajorLocation(majorLocation, newAssignment ) {
				
	    		$("#assignmentTemplate").tmpl(newAssignment).appendTo("#sortable_"+majorLocation.index);
	    		$("#removeMajorLocationAssignment_"+newAssignment.seq).on('click', function() {
	    			removeMajorLocationAssignment(newAssignment.group, newAssignment.seq);
	    			setMarkersOnMap(map2, markers2, true);
	    		});
			}
			
			function setAssignmentGroup (seq, group) {
				for (var i=0 ; i < assignments.length ; i++) {
					if (assignments[i].seq == seq) {
						assignments[i].group = group;
						break;
					}
				}
			}
			
			function assignmentIdExist(assignment) {
				for (var i = 0 ; i < assignments.length ; i++) {
					if (assignment.planType != assignments[i].planType)
						continue;
					if (assignment.planType == "1") {
						if (assignments[i].outletId == assignment.outletId) {
							return true;
						}
					} else {
						if (assignments[i].assignmentIds[0] == assignment.assignmentIds[0]) {
							return true;
						}
					}
				}
				return false;
			}
			
			function loadAssignments(newAssignments, removable, loadOnly) {
				for (i in newAssignments) {
					var assignment = newAssignments[i];
					
					assignment.index = assignmentIndex;
					newAssignments[i].index = assignmentIndex;
					if (removable != null){
						assignment.removable = removable;						
					}
					if ( !assignmentIdExist(assignment) ){

			            var latLng = new google.maps.LatLng( Number(assignment.latitude),  Number(assignment.longitude));

						if (map1 == null) {
							var mapOptions = {
									zoom: 16,
									center: latLng,
									mapTypeId: google.maps.MapTypeId.ROADMAP
								};
							map1 = new google.maps.Map(document.getElementById("map_canvas1"), mapOptions);
							google.maps.event.addListener(map1, "idle", function(){
								if (mapChange){									
									fixBound(map1);
									mapChange = false;
								}
						    });
						}
											
						/*
			            var marker = new google.maps.Marker({
			              position: latLng,
			              map: map1,
			              animation: google.maps.Animation.DROP,
			              title: assignment.firm,
			            });		            
			            //map1.setCenter(latLng)
			            markers1.push(marker);*/
			           
			            if (assignment.planType == 2) {
			            	assignment.assignmentId = assignment.assignmentIds[0];
			            }
			           
			            assignments.push(assignment);
			            assignmentIndex++;
			            
					}
				}
				if (loadOnly){
					return;
				}
				setMarkersOnMap(map1, markers1, false);
				updateDataTable1();
				updateCounts();
			}
			
			function updateMarkers(marker, assignments)
			{
				
			}
			
			function removeAssignment1Row(data) {
				removeAssignements(data);
				updateDataTable1();
				updateCounts();
				setMarkersOnMap(map1, markers1, false);
			}
			
			function removeAssignements(data) {
				for (i = assignments.length; i > 0 ; i--) {
					if (data < 0) {
						assignments.splice(i-1, 1);
					}
					else 
					{
						if (assignments[i-1].index == data) {
							if (assignments[i-1].group !== undefined && assignments[i-1].group > 0) {
								removeMajorLocationAssignment(assignments[i-1].group, assignments[i-1].seq);	
							}
							assignments.splice(i-1, 1);
							break;
						}
					}
				}
			}
			
			// Major Location 
			
			var majorLocations = [];
		    var majorLocationIndex = majorLocations.length + 1;
			
			function addMajorLocation() {
				$("#addMajorLocBtn").unbind( "click" );
				$("#addMajorLocBtn").on('click', function() {
					if ($(this).parents("form:first").valid()){
						var majorLocation = {};
						var majorLocaitonAssignments = [];
						majorLocation.color = assignMarkerColor(majorLocations);
						majorLocation.taskName = $("#majLocName").val();
						majorLocation.isFreeEntryTask = false;
						majorLocation.isNewRecruitmentTask = false;
						majorLocation.index = majorLocationIndex;
						majorLocation.assignments = majorLocaitonAssignments;			
						majorLocationIndex++;
						majorLocations.push(majorLocation);
						renderMajorLocation( majorLocation );
						
					
						$("#majorLocationForm").modal('hide');
						$("#majLocName").val("");
					}					
				});

				majorLocationValidator.resetForm();
				$("#majorLocationDialogLabel").text("Add Major Location");
				$("#majorLocationForm input:text").val("");
				$("#majorLocationForm").modal('show');
			}
			
			function renderMajorLocation(majorLocation) {

				$("#majorLocationTemplate").tmpl(majorLocation).insertBefore("#labelAddMajorLoc");
				highlight("majorLocationColor_"+majorLocation.index, majorLocation.color);
				$("#editMajorLocation_"+majorLocation.index).on('click', function() {
					editMajorLocation(majorLocation);
				});
				
				$("#removeMajorLocation_"+majorLocation.index).on('click', function() {
					removeMajorLocation(majorLocation.index);
					setMarkersOnMap(map2, markers2, true);
				});
				
				$("#selectAssignments_"+majorLocation.index).on('click', function() {
					var newAssignments = [];
					$("input:checked", table2.rows().nodes()).each( function() {

					    assignment = getAssignmentByIndex($(this).val());
						if (majorLocation.assignments.length > 0) {
							if (majorLocation.assignments[0].tpu != assignment.tpu) {
								bootbox.alert({
									title: "Alert",
									message: "<spring:message code='E00062' />"
								});
								return false;
							}
						}
					    majorLocation.assignments.push(assignment);
					    newAssignments.push(assignment);
					    assignment.group = majorLocation.index;
					    $(this).removeAttr( 'checked' );
					});
			    	for (var j = 0 ; j < newAssignments.length ; j++ ) {
			    		var newAssignment = newAssignments[j];
						addNewAssignmentToMajorLocation(majorLocation, newAssignment);
			    	}
			    	updateDataTable2();
			    	setMarkersOnMap(map2, markers2, true);
				});
				$("#sortable_"+majorLocation.index).sortable({
					connectWith: ".locationSortable",
					receive: function (event,ui) {
						var notified = false;
						var sortableId = event.target.id;
						var mlIndex = event.target.id.split("_")[1];
						//var majorLocation = majorLocations[mlIndex];
						var majorLocation = getMajorLocationByIndex(mlIndex);
						//var dragItem_id = ui.item[0].id;
						//var index = eval(dragItem_id.split("_")[1]);
						var index = $(ui.item[0]).data("index")
						var assignment = getAssignmentByIndex(index);
						//var originalMLId = ui.sender[0].id.split("_")[1];
						//var origMajorLocation = getMajorLocationByIndex(originalMLId);
						
											
						// check for tpu
						if (majorLocation.tpu == '' || majorLocation.tpu == null) {
							majorLocation.tpu = assignment.tpu;
							assignment.group = majorLocation.index;
							//majorLocation.assignments.push(assignment);
							/*
							origMajorLocation.assignments = _.filter(origMajorLocation.assignments, function(item){
								return item != assignment;
							});
							if (origMajorLocation.assignments.length == 0){
								origMajorLocation.tpu = '';
							}
							*/
						} else if ( majorLocation.tpu == assignment.tpu) {
							assignment.group = majorLocation.index;
							//majorLocation.assignments.push(assignment);
							/*
							origMajorLocation.assignments = _.filter(origMajorLocation.assignments, function(item){
								return item != assignment;
							});
							if (origMajorLocation.assignments.length == 0){
								origMajorLocation.tpu = '';
							}
							*/
						} else {
							bootbox.alert({
					            title: "Alert",
					            message: "<spring:message code='E00062' />"
					          });
							$(ui.sender).sortable('cancel');
						}
						
						/*for (var i = 0; i < majorLocations.length ; i++ ) {
							var newMajorLocationAssginments = [];
							var majorLocation = majorLocations[i];
							var $sortable = $("#sortable_"+ majorLocation.index + " li");
							var currentId = $sortable.attr('id');
							var index = eval(currentId.split("_")[1]);
							assignment = getAssignmentByIndex(index);
							if (majorLocation.tpu == '') {
								majorLocation.tpu = assignment.tpu;
								assignment.group = majorLocation.index;
								newMajorLocationAssginments.push(assignment);
							} else if ( majorLocation.tpu == assignment.tpu) {
								assignment.group = majorLocation.index;
								newMajorLocationAssginments.push(assignment);
							} else {
								if (!notified){
								 bootbox.alert({
							            title: "Alert",
							            message: "<spring:message code='E00062' />"
							          });
									$(ui.sender).sortable('cancel');
									
								}						       
								notified = true
							}
							
							
							if (newMajorLocationAssginments.length == 0) {
								majorLocation.tpu = '';
							}
							majorLocation.assignments = newMajorLocationAssginments;
						}*/
						setMarkersOnMap(map2, markers2, true);
					},
					update: function(event, ui){
						// reconstruct the assignment list (to ensure the order of assignments)
						for (i = 0; i < majorLocations.length; i++){
							var ml = majorLocations[i];
							ml.assignments = [];
							$("#sortable_"+ ml.index + " li").each(function(){
								//var id = this.id;
								//var index = eval(id.split("_")[1]);
								var index = $(this).data("index")
								var ass = getAssignmentByIndex(index);
								ml.assignments.push(ass);
							})
							if (ml.assignments.length == 0){
								// set tpu to empty if there is no assignment
								ml.tpu = ''
							}
						}
						
					},
					helper: 'clone'
				});
			}

			function editMajorLocation(majorLocation) {
				
				$("#majLocName").val(majorLocation.taskName);			
				$("#addMajorLocBtn").unbind( "click" );
				$("#addMajorLocBtn").on('click', function() {
					if ($(this).parents("form:first").valid()){
						majorLocation.taskName = $("#majLocName").val();
						$("#majorLocationName_"+majorLocation.index).text(majorLocation.taskName);
						$("#majorLocationForm").modal('hide');
						$("#majLocName").val("");
					}
				});

				majorLocationValidator.resetForm();
				$("#majorLocationDialogLabel").text("Edit Major Location");
				$("#majorLocationForm").modal('show');	
				
			}

			
			function getNewLocationsSequence(data, session) {
				var newLocationSeq=[];
				for(var i = 0; i < data.length; i++) {
					if (data[i] != "") {
						majorLocation = getMajorLocationByIndex(eval(data[i]));
						if (majorLocation.isFreeEntryTask == false) {
							for (var j=0 ; j < majorLocation.assignments.length ; j ++) {
					            var latLng = new google.maps.LatLng( Number(majorLocation.assignments[j].latitude),  Number(majorLocation.assignments[j].longitude));
								newLocationSeq.push({"position":latLng , "assignmentGroup":majorLocation.assignments[j].group, "assignmentSeq":majorLocation.assignments[j].seq });
							}
						}
					}
				}
				return newLocationSeq;
			}
			
			function getLocationsByMajoLocationIndex(index){
				var locations = [];
				majorLocation = getMajorLocationByIndex(eval(index));
				for (var j=0 ; j < majorLocation.assignments.length ; j ++) {
					var latLng = new google.maps.LatLng( Number(majorLocation.assignments[j].latitude),  Number(majorLocation.assignments[j].longitude));
					locations.push(latLng);
				}
				return locations;
			}
			
			function getMajorLocationByIndex(index) {
				for (var i=0 ; i < majorLocations.length ; i++) {
					if (majorLocations[i].index == index) {
						return majorLocations[i];
					}
				}
				return null;
			}
			
			function initMajorLocationGrouping(alwaysGroup) {
				
				var a;			
				var ungroup = {};
				for (var i = 0,l=assignments.length; i < l; i++) { 
					
				    a = assignments[i];
				    var grouped = false;
				    
				    if (a.group === undefined || a.group == 0) {
				    	for (var j = 0; j < majorLocations.length ; j++ ) {
				    		if (a.tpu == majorLocations[j].tpu) {
				    			if (majorLocations[j].marketName !=null && majorLocations[j].marketName != '' && majorLocations[j].marketName == a.marketName){
				    				majorLocations[j].assignments.push(a);
					    			assignments[i].group= majorLocations[j].index;
					    			grouped = true;
					    			break;
				    			}
				    			else if ((majorLocations[j].marketName ==null || majorLocations[j].marketName == '') && 
				    					majorLocations[j].address!=null && majorLocations[j].address != '' && majorLocations[j].address == a.address){
				    				majorLocations[j].assignments.push(a);
					    			assignments[i].group= majorLocations[j].index;
					    			grouped = true;
					    			break;
				    			}

				    		}
				    	}
				    	
				    	if (grouped == false) {
				    		if (a.marketName != null && a.marketName != '') {
				    			if (ungroup[a.marketName] == null){
				    				ungroup[a.marketName] = [];
				    			}
				    			ungroup[a.marketName].push(a);
		 					} else {
		 						if (ungroup[a.address] == null){
				    				ungroup[a.address] = [];
				    			}
				    			ungroup[a.address].push(a);
		 					}				    		
					    }
				    	
				    	/*
				    	for (var j = 0; j < majorLocations.length ; j++ ) {
				    		if (a.tpu == majorLocations[j].tpu) {
								for (var k=0 ; k < majorLocations[j].assignments.length ; k++) {
						    		if ((a.marketName != null && a.marketName != "" && a.marketName == majorLocations[j].assignments[k].marketName) || (a.address != null && a.address != "" && a.address == majorLocations[j].assignments[k].address)) {
						    			majorLocations[j].assignments.push(a);
						    			assignments[i].group= majorLocations[j].index;
						    			grouped = true;
						    			break;
						    		}
								}
				    		}
							if (grouped) {
								break;
							}

				    	}
					    	
					    if (grouped == false) {
		 					var newMajorLocation = {};
		 					var groupAssignments = [];
		 					groupAssignments.push(a);
		 					if (a.marktName != null) {
		 						newMajorLocation.taskName = a.marketName + " " + a.address;
		 						newMajorLocation.marketName = a.marketName;
		 					} else {
		 						newMajorLocation.taskName = a.address;
		 					}
		 					newMajorLocation.color = assignMarkerColor();
		 					newMajorLocation.tpu = a.tpu;
		 					newMajorLocation.address = a.address;
		 					newMajorLocation.assignments = groupAssignments;
		 					newMajorLocation.index = majorLocationIndex;
		 					newMajorLocation.isFreeEntryTask = false;
		 					newMajorLocation.isNewRecruitmentTask = false;
		 					majorLocationIndex++
		 					assignments[i].group = newMajorLocation.index;
		 					majorLocations.push(newMajorLocation);
					    }
					    */
				    }
				}
				
				for (group in ungroup){
					if (alwaysGroup || ungroup[group].length > 1 || ungroup[group][0].marketName != null && ungroup[group][0].marketName != ''){
						var newMajorLocation = {};
	 					var groupAssignments = [];
	 					if (ungroup[group][0].marketName != null && ungroup[group][0].marketName != '') {
	 						newMajorLocation.taskName = group;
	 						newMajorLocation.marketName = group;
	 					} else {
	 						newMajorLocation.firmName = ungroup[group][0].firm;
	 						newMajorLocation.taskName = group;
	 					}
	 					newMajorLocation.color = assignMarkerColor(majorLocations);
	 					newMajorLocation.tpu = ungroup[group][0].tpu;
	 					newMajorLocation.address = ungroup[group][0].address;
	 					newMajorLocation.index = majorLocationIndex;
	 					newMajorLocation.isFreeEntryTask = false;
	 					newMajorLocation.isNewRecruitmentTask = false;
	 					majorLocationIndex++	 					
	 					majorLocations.push(newMajorLocation);
	 					for (i = 0; i < ungroup[group].length; i++){
	 						var a = ungroup[group][i];
	 						a.group = newMajorLocation.index;
	 						groupAssignments.push(a);
		 					
	 					}

	 					newMajorLocation.assignments = groupAssignments;
	 					
					}
				}
			}
			
			
			function removeMajorLocation(index) {
				for (i = majorLocations.length; i > 0 ; i--) {
					if ( (index < 0 && majorLocations[i-1].assignments.length > 0) || majorLocations[i-1].index == index) {
						for (j = majorLocations[i-1].assignments.length -1 ; j >= 0 ; j--) {
							removeMajorLocationAssignment(majorLocations[i-1].assignments[j].group, majorLocations[i-1].assignments[j].seq);							
						}
						majorLocations.splice(i-1, 1);
						if (index > 0 ) {
							break;
						}
					}
				}
				$("#majorLocation_"+index).remove();
			}
					
			function removeMajorLocationAssignment(group, seq) {
				var majorLocation = getMajorLocationByIndex(group);
				for (var i = majorLocation.assignments.length; i > 0 ; i--) {
					if (majorLocation.assignments[i-1].seq == seq) {
						majorLocation.assignments.splice(i-1, 1);
						break;
					}
				}
				/*if (majorLocation.assignments.length == 0){
					removeMajorLocation(group);
				}*/
				$("#majorLocationAssignment_"+seq).remove();
				setAssignmentGroup(seq,0);
				updateDataTable2();
			}
			
			// Tasks 

			function addTask() {
				$("#taskDialogLabel").text("Add Task");
				$("#taskSubject").val('');
				$("#taskLocation").val('');
				$("#addTaskBtn").unbind( "click" );
				$("#addTaskBtn").on('click', function() {
					var majorLocation = {};
					
					majorLocation.taskName = $("#taskSubject").val();
					majorLocation.location = $("#taskLocation").val();
					majorLocation.isFreeEntryTask = true;
					majorLocation.isNewRecruitmentTask = false;
					majorLocation.remark = "";
					majorLocation.index = majorLocationIndex;		
					majorLocationIndex++;
					majorLocations.push(majorLocation);
					
					renderTask(majorLocation, "#sortable_taskList");
					$("#taskForm").modal("hide");
				});
				$("#taskForm").modal('show');
			}
			
			function editTask(majorLocation) {
				
				$("#taskSubject").val(majorLocation.taskName);
				$("#taskLocation").val(majorLocation.location);
				$("#addTaskBtn").unbind( "click" );
				$("#addTaskBtn").on('click', function() {
					majorLocation.taskName = $("#taskSubject").val();
					majorLocation.location = $("#taskLocation").val();
					$("#taskLabel_"+majorLocation.index).text(majorLocation.taskName);
					$("#taskLocation_"+majorLocation.index).text(majorLocation.location);
					$("#taskForm").modal('hide');
				});
				$("#taskDialogLabel").text("Edit Task");
				$("#taskForm").modal('show');	
				
			}
			
			function renderTask(task, placeHolder) {
    			$("#taskTemplate").tmpl(task).appendTo(placeHolder);
    			
				$("#editTask_"+task.index).on('click', function() {
					editTask(task);
				});
    			
    			$("#removeTask_"+task.index).on('click', function () {
    				removeTask($(this).attr('id').split('_')[1]);
    			}) 			
			}
			
			function removeTask(index) {
				for (i = majorLocationSeq.length; i > 0 ; i--) {
					if (majorLocationSeq[i-1] == index) {
						majorLocationSeq.splice(i-1, 1);
						break;
					}
				}
				for (i = majorLocations.length; i > 0 ; i--) {
					if (majorLocations[i-1].index == index) {
						majorLocations.splice(i-1, 1);
						break;
					}
				}
				$("#task_"+index).remove();
			}
			
			// New Recruitment 

			function addRecruitment() {
				$("#recruitmentDialogLabel").text("Add New Recruitment");
				$("#recruitmentDistrict").val('');
				$("#recruitmentTpu").val('');
				$("#recruitmentStreet").val('');

				$("#addRecruitmentBtn").unbind( "click" );
				$("#addRecruitmentBtn").on('click', function() {
					var majorLocation = {};
					
					majorLocation.taskName = "New Recruitment";
					majorLocation.district = $("#recruitmentDistrict").val();
					majorLocation.tpu = $("#recruitmentTpu").val();
					majorLocation.street = $("#recruitmentStreet").val();
					majorLocation.isFreeEntryTask = true;
					majorLocation.isNewRecruitmentTask  = true;
					majorLocation.remark = "";
					majorLocation.index = majorLocationIndex;		
					majorLocationIndex++;
					majorLocations.push(majorLocation);
					
					renderRecruitment(majorLocation, "#sortable_taskList");
					$("#recruitmentForm").modal("hide");

				});
				$("#recruitmentForm").modal('show');
			}
			
			function editRecruitment(majorLocation) {
				
				$("#recruitmentDistrict").val(majorLocation.district);
				$("#recruitmentTpu").val(majorLocation.tpu);
				$("#recruitmentStreet").val(majorLocation.street);
				$("#addRecruitmentBtn").unbind( "click" );
				$("#addRecruitmentBtn").on('click', function() {
					majorLocation.district = $("#recruitmentDistrict").val();
					majorLocation.tpu = $("#recruitmentTpu").val();
					majorLocation.street=$("#recruitmentStreet").val();
					$("#recruitmentDistrict_"+majorLocation.index).text(majorLocation.district);
					$("#recruitmentTpu_"+majorLocation.index).text(majorLocation.tpu);
					$("#recruitmentStreet_"+majorLocation.index).text(majorLocation.street);
					$("#recruitmentForm").modal('hide');
				});
				$("#recruitmentDialogLabel").text("Edit New Recruitment");
				$("#recruitmentForm").modal('show');	
				
			}
			
			function renderRecruitment(task, placeHolder) {
    			$("#recruitmentTemplate").tmpl(task).appendTo(placeHolder);
    			
				$("#editRecruitment_"+task.index).on('click', function() {
					editRecruitment(task);
				});
    			
    			$("#removeTask_"+task.index).on('click', function () {
    				removeTask($(this).attr('id').split('_')[1]);
    			}) 			
			}
				
			function loadTaskList() {
				for (var i=0 ; i < majorLocationSeq.length ; i++) {
					genTaskList(getMajorLocationByIndex(majorLocationSeq[i]));
				}
				for (var i=0 ; i < majorLocations.length ; i++) {
					var ret = _.find(majorLocationSeq, function(index){
						return index == majorLocations[i].index;
					});
					if (ret == null || ret == undefined){
						genTaskList(majorLocations[i]);
					}
				}
				
				/*
				if (majorLocationSeq.length == 0) {			
					for (var i=0 ; i < majorLocations.length ; i++) {
						genTaskList(majorLocations[i]);
					}
				} else {
					for (var i=0 ; i < majorLocationSeq.length ; i++) {
						genTaskList(getMajorLocationByIndex(majorLocationSeq[i]));
					}
				}*/
			}
			
			function genTaskList(majorLocation) {
				if (majorLocation == null) return;
				
				if (majorLocation.remark === undefined) {
					majorLocation.remark = "";
				}
				var placeHolder;
				
				if (majorLocation.session == "A" ) {
					placeHolder = "#sortable_session1";
				} else if ( majorLocation.session == "P") {
					placeHolder = "#sortable_session2";
				} else if ( majorLocation.session == "E") {
					placeHolder = "#sortable_session3";
				} 				
				else {
					placeHolder = "#sortable_taskList";
				}
				
				if (majorLocation.isNewRecruitmentTask == true) {
				
					renderRecruitment(majorLocation, placeHolder)
	    			
				} else if (majorLocation.isFreeEntryTask == true) {
				
					renderTask(majorLocation, placeHolder)
	    			
				} else {
				
					majorLocation.noOfAssignment = 0;
					for (var j=0 ; j < majorLocation.assignments.length ; j++) {
						majorLocation.noOfAssignment += majorLocation.assignments[j].noOfAssignment;
					}
	    			var tmpl = $("#majorTaskTemplate").tmpl(majorLocation)
	    			/*tmpl.find(".remark").blur(function(){
	    				var index = $(this).data("index");
	    				majorLocation = getMajorLocationByIndex(index);
	    				majorLocation.remark = $(this).val();
	    			})*/
	    			tmpl.appendTo(placeHolder);
	    			highlight("taskColor_"+majorLocation.index, majorLocation.color);
				}
    			/*$("#taskRemark_"+majorLocation.index).on("input", function() {
    				var index = eval($(this).attr("id").split("_")[1]);
    				majorLocation = getMajorLocationByIndex(index);
    				majorLocation.remark = $(this).val();
    			})*/
			}
			
			function setTaskSessionWithSeq(ids, session, seq) {
				var seq = seq || 1;
				for (var i=0 ; i < ids.length ;  i++) {
					var index = ids[i].split("_")[1];
					
					majorLocation = getMajorLocationByIndex(index);
					majorLocation.session = session;
					//majorLocation.sequence = i+1;
					majorLocation.sequence = (seq++);
					
					setItemStyle(index, majorLocation.session);
				}
				return seq;
			}
			
			function setItemStyle(index, session) {
				if (session == "") {
					//$("#task_"+index).attr('class', 'col-md-3 checklist list-group-item');
					$("#task_"+index).removeClass('col-md-12').removeClass('assigned').addClass('col-md-3').addClass('checklist');
					$("#taskName_"+index).attr('class', 'col-sm-11');
					$("#taskRemark_"+index).attr('class', 'hidden');
				} else {
					//$("#task_"+index).attr('class', 'col-md-12 assigned list-group-item');
					$("#task_"+index).removeClass('col-md-3').removeClass('checklist').addClass('col-md-12').addClass('assigned');
					$("#taskName_"+index).attr('class', 'col-sm-5');
					$("#taskRemark_"+index).attr('class', 'col-sm-6');
				}
			}

			// Data Table
			
			function updateDataTable1() {

				table1 = $('#assignmentTable1').DataTable( {
					"order" : [[1,'desc']],
					"bInfo" : false,
					"ordering": true,
					"destroy": true,
				    "paging": false,
				    "searching": false,
			        "aadata": assignments,
			        "buttons": [],
			        "columns": [
			            { "title": "Reference no", "data": "referenceNo" },
			            { "title": "Name", "data": "firm" },
			            { "title": "MRPS District", "data": "district" },
			            { "title": "TPU", "data": "tpu" },
			            { "title": "Full Address", "data": "detailAddress" },
			            { "title": "No. of Quotation", "data": "noOfQuotation" },
			            { "title": "Convenient Time", "data": "convenientTime" },
			            { "title": "Remark", "data": "outletRemark" },
			            { "title": "Deadline / Collection Date", "data": "deadline" },
			           // { "title": "Status", "data": "status" },
			            { "data": "index" }
			        ],
	                "columnDefs": [
	                               {
	                            	   "targets": [8],
	                            	   "orderable": false,
	                                   "searchable": false,
	                                   "render" : function(data, type, full, meta){
	                                	   if(full.countAssignmentDate > 1) {
	                                		   return "* " + data;
	                                	   } else {
	                                		   return data;
	                                	   }
	                                   }
	                               },
	                               {
	                                   "targets": [9],
	                                   "orderable": false,
	                                   "searchable": false,
	                                   "render" : function(data, type, full, meta){
	                                	   var html = "";
	                                	   //if (full.deadline != $('#inputDate').val()){
	                                		if (full.removable){
	                                		   html = "<a href='javascript:void(0)' onclick='removeAssignment1Row("+data+")' class='table-btn btn-delete' data-id='"+data+"'><span class='fa fa-times' aria-hidden='true'></span></a>";
	                                	   }
	                                	   <%--
	                                	   if (full.deadline != $('#inputDate').select2('val')) {
			             						html = "<a href='javascript:void(0)' onclick='removeAssignment1Row("+data+")' class='table-btn btn-delete' data-id='"+data+"'><span class='fa fa-times' aria-hidden='true'></span></a>";	
	                                	   }
	                                	   --%>
	                                	   return html;
	                                   },
	                                   "className" : "text-center"
	                               },
	                               {
	                            		"targets": "_all",
		                            	"orderable": true,
	                               		"searchable": true,
	                               		"className" : "text-center"
	                            	   
	                               }
	                               
	                           ]
			    } );
				$('#assignmentTable1').dataTable().fnClearTable();
				if (assignments.length > 0) {
					$('#assignmentTable1').dataTable().fnAddData( 
							assignments
							);
				}

			}
			
			function updateDataTable2() {

				table2 = $('#assignmentTable2').DataTable( {
					"bInfo" : false,
					"ordering": false,
					"destroy": true,
				    "paging": false,
				    "searching": true,
			        "data": assignments,
			        "buttons": [],
			        "columns": [
						{ "data": "index",
                          "render" : function(data, type, full, meta){
                        		var checked = selectedIds.indexOf(data) != -1;
                        		var html = "";
                        		if (full.group === undefined || full.group == 0 ) {
                        			html = '<input name="id" type="checkbox" value="' + data + '"' + (checked ? ' checked' : '') + '/>';
                        		}
                        		return html;
                       	  }
						},
						{ "data": "referenceNo" },
			            { "data": "firm" },
			            { "data": "district" },
			            { "data": "tpu" },
			            { "data": "detailAddress" },
			            { "data": "noOfQuotation" },
			            { "data": "convenientTime" },
			            { "data": "outletRemark" },
			            { "data": "marketName",
//                           "render" : function(data, type, full, meta){
//                         		if (full.planType == 2) return '';
                        		//return $("#defaultMLTemplate").tmpl(full).html();
//                         		return '';
//                        	  }
						},
			            
			            { "data": "deadline" },
			           // { "data": "status" },
			            { "data": "outletId", "visible": false },    
			        ],
			        "sDom": '<"top"i>rt<"bottom"lp><"clear">',
			    } );

				table2.columns().every( function () {
	                var column = this;
	                if (column.dataSrc() == "tpu" || column.dataSrc() == "district" ) {
		                $("#"+column.dataSrc()+"_filter", $('#tab_2')).unbind()
		                    .on( 'change', function () {
		                        var val = $.fn.dataTable.util.escapeRegex(
		                            $(this).val()
		                        );
		                        column
		                            .search( val ? '^'+val+'$' : '', true, false )
		                            .draw();
		                    } );
		                $('#'+column.dataSrc()+'_filter', $('#tab_2')).empty().append( '<option value=""></option>' )
		                column.data().sort().unique().each( function ( d, j ) {
		                	$('#'+column.dataSrc()+'_filter', $('#tab_2')).append($("<option></option>")
		                	         .attr("value",d)
		                	         .text(d)); 
			                } );
	                }
	            } );
				
				$('#outletType_filter', $('#tab_2')).select2().on( 'change', function () {
                    
					if ($(this).val()) {
						var val = $(this).val().join("|");
					}

                    table2.columns(11)
                        .search( val ? '('+val+')' : '', true, false )
                        .draw();
                } );
			}
			
			function activaTab(tab){
			    $('.nav-tabs a[href="#' + tab + '"]').tab('show');
			};
			
			function tabValidation(index) {
				if(index == 1){
					if(assignments.length == 0){
						bootbox.alert({
						    title: "Alert",
						    message: "<spring:message code='E00127' />"
						});
						return false;
					}
				}
				if(index == 2){
					
					for (var i=0 ; i < assignments.length ; i++ ) {			
						if(assignments[i].group == 0 || assignments[i].group == null){
							bootbox.confirm({
    						    title: "Confimration",
    						    message: "<spring:message code='W00024' />",
    						    callback: function (result){
    						    	if (result){
    									initMajorLocationGrouping(true);
    									//$("#rootwizard").bootstrapWizard('next');
    									$("#rootwizard").bootstrapWizard('last');
    						    	}
    						    }
    						});
							return false;
						}
					}
							
					
					/*
					for (var i=0 ; i < assignments.length ; i++ ) {			
						if(assignments[i].group == 0 || assignments[i].group == null){
							bootbox.alert({
    						    title: "Alert",
    						    message: "<spring:message code='E00128' />"
    						});
							return false;
						}						
					}*/
					if(majorLocations.length == 0){
						bootbox.alert({
						    title: "Alert",
						    message: "<spring:message code='E00129' />"
						});
						return false;
					};
					
					// remove empty major location
					majorLocations = _.filter(majorLocations, function(item){
						return item.isNewRecruitmentTask || item.isFreeEntryTask || item.assignments != null && item.assignments.length != 0;
					})
					/*
					for (var i=0 ; i < majorLocations.length ; i++ ) {
						
						if(!majorLocations[i].isFreeEntryTask && majorLocations[i].assignments.length == 0){
							bootbox.alert({
    						    title: "Alert",
    						    message: "<spring:message code='E00130' />"
    						});
							return false;
						}
					}*/
				}
			}
			
			// Onload
			
			function createDatePicker(){
				$("#inputDate").datepicker({
					beforeShowDay: function(date) {
					    if (plannedDates.length >= 3){
					    	return {enabled: false};
					    }
					    else if (Date.today().add({days:30}) <= date || date < Date.today()){
					    	return {enabled: false};
					    }
					    else {
					    	for (i = 0; i < plannedDates.length; i++){
					    		var planned = parseDate(plannedDates[i]);
					    		if (planned.equals(date)){
					    			return {enabled: false};
					    		}
					    	}
					    	
					    	for (i = 0; i < nonWorkingDates.length; i++){
					    		var planned = parseDate(nonWorkingDates[i]);
					    		if (planned.equals(date)){
					    			return {enabled: false};
					    		}
					    	}
					    }
					},
					autoclose : true,
					//datesDisabled: plannedDates
				}).on("changeDate", function(e){

					<c:if test="${act != 'edit'}"> 
					$("#addAssignmentButton").show();
					$(".toggleMapBtn").show();
					
					removeAssignements(-1);
					removeMajorLocation(-1);
					
					var newAssignments = [];
					$.ajax({
						url: "<c:url value='/itineraryPlanning/ItineraryPlan/queryAssignments'/>",
						type: 'POST',
						async: false,
						data: { 
							officerId: $('#hiddenUserId').val(),
							assignedCollectionDate: $('#inputDate').val(),
							},
						success: function (response) {
							newAssignments = response;
						}
					});
					loadAssignments(newAssignments, false);
					</c:if>
				});
				
			}
			
			var majorLocationValidator;
			var mapChange = false;
			$(document).ready(function() {
				createDatePicker();
				
				majorLocationValidator = $("#majorLocationFormDummy").validate();
				/*
				$('#inputDate').fireOnDisable().select2({
					ajax: {
					    data: function (params) {
					    	var query = {
					    		search: params.term,
					    		officerId: $("#hiddenUserId").val(),
					    	}
					      	return query;
					    }
					},
					width:'100%',
					minimumResultsForSearch: -1,
				});
				*/
				
				$("#btnSelectAll").click(function(){
					$("#sortable_taskList").find(":checkbox").each(function(){
						this.checked = true
					})
					
				})
				
				
				  	$('#rootwizard').bootstrapWizard({
				  		onTabShow: function(tab, navigation, index) {
							var $total = navigation.find('li').length;
							var $current = index+1;
							var $percent = ($current/$total) * 100;
							$('#rootwizard').find('.bar').css({width:$percent+'%'});
							
							if ($current == 1){
								$('#rootwizard').find('.pager .previous').hide();
							}
							else{
								$('#rootwizard').find('.pager .previous').show();
							}
							
							if($current >= $total) {
								$('#rootwizard').find('.pager .next').hide();
								$('#rootwizard').find('.pager .finish').show();
								$('#rootwizard').find('.pager .finish').removeClass('disabled');
							} else {
								$('#rootwizard').find('.pager .next').show();
								$('#rootwizard').find('.pager .finish').hide();
							}
							
							switch (index) {
								case 0:
									if ($("#map1").find("#map_canvas1").length == 0){
										mapChange = true;
										$("#map_canvas1").appendTo("#map1");
										google.maps.event.trigger(map1, 'resize');
										directionsDisplay1.setMap(null);
										setMarkersOnMap(map1, markers1, false);
									}
									break;
								case 1:
									mapChange = true;
									$("#map_canvas1").appendTo("#map2");
									google.maps.event.trigger(map1, 'resize');
									initMajorLocationGrouping();
									updateDataTable2();	
									
									var latLng = new google.maps.LatLng( Number(assignments[0].latitude),  Number(assignments[0].longitude));
									
									if (map2 == null) {
										/*var mapOptions = {
												zoom: 16,
												center: latLng,
												mapTypeId: google.maps.MapTypeId.ROADMAP
											};
										map2 = new google.maps.Map(document.getElementById("map_canvas2"), mapOptions);*/
										map2 = map1;
										
									}
									directionsDisplay1.setMap(null);
									setMarkersOnMap(map2, markers2, true);
									$("#majorLoc ul").remove();
	
									if (majorLocations.length > 0 ) {	
										for (var i = 0 ; i < majorLocations.length ; i++ ) {
											if (majorLocations[i].isFreeEntryTask == false) {
												renderMajorLocation(majorLocations[i])
									    		var mjAssignments = majorLocations[i].assignments;
										    	for (var j = 0 ; j < mjAssignments.length ; j++ ) {
										    		addNewAssignmentToMajorLocation(majorLocations[i], mjAssignments[j]);
										    	}
											}
										}
									}
									

									break;
								case 2:
									mapChange = true;
									$("#map_canvas1").appendTo("#map3");
									google.maps.event.trigger(map1, 'resize');
									$("#sortable_taskList").empty();
									$("#sortable_session1").empty();
									$("#sortable_session2").empty();
									$("#sortable_session3").empty();
									loadTaskList();	
									if (map3 == null) {
										/*var latLng = new google.maps.LatLng( Number(assignments[0].latitude),  Number(assignments[0].longitude));
										var mapOptions = {
												zoom: 16,
												center: latLng,
												mapTypeId: google.maps.MapTypeId.ROADMAP
											};
										map3 = new google.maps.Map(document.getElementById("map_canvas3"), mapOptions);*/
										map3=map1
									}
									loadRoute();
									
									//google.maps.event.trigger(map1, 'resize');
									break;
							}
						},
						onTabClick: function(tab, navigation, current, index) {
							if (index > current + 1) {
								//return false;
							}
							return tabValidation(index);
						},
						onNext: function(tab, navigation, index, manualTrigger) {
							return tabValidation(index);
						}
				  	});
				
				<c:if test="${act == 'add'}">
					$('#rootwizard').hide();
					$("#addAssignmentButton").hide();
					$(".toggleMapBtn").hide()
				</c:if>
				$('.nav-tabs a').on('click', function(e) {
					$(this).tab('show');
					e.preventDefault();
				});

				Modals.init();
				
				$("#tab_3").focusout(function(){
					$(this).find(".remark").each(function(){
						var index = $(this).data("index");
	    				majorLocation = getMajorLocationByIndex(index);
	    				majorLocation.remark = $(this).val();
					})
				});
				

				$('#addOutlet').assignmentLookupOutlet({
					selectedIdsCallback: function(outletIds) {
						var newAssignments = [];
						$.ajax({
							url: "<c:url value='/itineraryPlanning/ItineraryPlan/queryAssignments'/>",
							type: 'POST',
							async: false,
							data: { 
								officerId: $('#hiddenUserId').val(),
								outletIds: outletIds,
								planDate: $('#inputDate').val(),
								},
							success: function (response) {
								newAssignments = response;
							}
						});
						
						loadAssignments(newAssignments, true);					
					},
					multiple: true,
					queryDataCallback: function(model) {
						model.officerId= $('#hiddenUserId').val();
			    		model.date= $('#inputDate').val();
			    		model.excludedOutletIds = [];
			    		for (i =0; i <assignments.length; i++){
			    			if (assignments[i].planType == 1){
				    			model.excludedOutletIds.push(assignments[i].outletId)
			    			}
			    		}
					},
					selectionChangedCallback: function (ids){
						var model = [];
						model.officerId= $('#hiddenUserId').val();
			    		model.date= $('#inputDate').val();
			    		model.excludedOutletIds = [];
			    		model.excludedAssignmentIds = [];
			    		for (i =0; i <assignments.length; i++){
			    			if (assignments[i].planType == 1){
				    			model.excludedOutletIds.push(assignments[i].outletId)
			    			}
			    			if (assignments[i].planType == 2){
				    			model.excludedAssignmentIds.push(assignments[i].assignmentId)
			    			}
			    		}
			    		for (i =0; i <ids.length; i++){
			    			model.excludedOutletIds.push(ids[i]);
			    		}
						return model;
					}
				});

				$('#addBuilding').assignmentLookupBuilding({
					selectedIdsCallback: function(assignmentIds) {
						var newAssignments = [];
						$.ajax({
							url: "<c:url value='/itineraryPlanning/ItineraryPlan/queryImportedAssignments'/>",
							type: 'POST',
							async: false,
							data: { 
								officerId: $('#hiddenUserId').val(),
								assignmentIds: assignmentIds,
								planDate: $('#inputDate').val(),
								},
							success: function (response) {
								newAssignments = response;
							}
						});
						loadAssignments(newAssignments, true);					
					},
					multiple: true,
					queryDataCallback: function(model) {
						model.officerId= $('#hiddenUserId').val();
			    		model.date= $('#inputDate').val();
			    		model.excludedAssignmentIds = [];
			    		for (i =0; i <assignments.length; i++){
			    			if (assignments[i].planType == 2){
				    			model.excludedAssignmentIds.push(assignments[i].assignmentId)
			    			}
			    		}
					},
					selectionChangedCallback: function (ids){
						var model = [];
						model.officerId= $('#hiddenUserId').val();
			    		model.date= $('#inputDate').val();
			    		model.excludedOutletIds = [];
			    		model.excludedAssignmentIds = [];
			    		for (i =0; i <assignments.length; i++){
			    			if (assignments[i].planType == 1){
				    			model.excludedOutletIds.push(assignments[i].outletId);
			    			}
			    			if (assignments[i].planType == 2){
				    			model.excludedAssignmentIds.push(assignments[i].assignmentId);
			    			}
			    		}
			    		for (i =0; i <ids.length; i++){
			    			model.excludedAssignmentIds.push(ids[i]);
			    		}
						return model;
					}
				});

				$('#userId').fireOnDisable().select2ajax({
					width:'100%'
				});

				$('#userId').select2ajax().on("change", function() { 
					var userId = $('#userId').val();
					$('#hiddenUserId').val(userId);
					$('#userId').fireOnDisable().select2ajax();
					$('#userId').prop("disabled", true);
					$('#rootwizard').show();
					$.getJSON('<c:url value="/itineraryPlanning/ItineraryPlan/getUserPlannedDates" />', {userId: userId}, function(data){
						/*plannedDates = [];
						for (i = 0; i < data.length; i++){
							plannedDates.push(parseDate(data[i]));
						}*/
						plannedDates = data;
						//$("#inputDate").datepicker('setDatesDisabled', plannedDates);
						<c:if test="${act != 'edit'}" >
						$("#inputDate").datepicker('update', '');
						</c:if>
					})
				});

				$('#supervisorId').select2({
					ajax: {
					    data: function (params) {
					    	return $.extend(params, {officerId: $('#hiddenUserId').val()});
					    	/*
					    	var query = {
					    		search: params.term,
					    		officerId: $('#hiddenUserId').val(),
					    	}
					      	return query;*/
					    }
					},
					width:'100%',
				});
				

				// tab 2
				
				$('[name="outletTypeId"]', $('#tab_2')).select2({
					ajax: {
					    data: function (params) {
					    	
					    	return $.extend(params, {outletIds: $.unique(outletIds)})
					    	/*var query = {
					    		search: params.term,
					    		outletIds: $.unique(outletIds)
					    	}
					      	return query;*/
					    }
					},
					width:'100%',
				});
			
				// Handle click on "Select all" control
				$('#table2-select-all').on('click', function(){
				   // Get all rows with search applied
				   var rows = table2.rows({ 'search': 'applied' }).nodes();
				   // Check/uncheck checkboxes for all rows in the table
				   $('input[type="checkbox"]', rows).prop('checked', this.checked);
				});
				
				// tab 3		
				// {suppressMarkers: true} => hide the marker from directions service
				directionsDisplay1 = new google.maps.DirectionsRenderer({
					suppressMarkers: true,
					preserveViewport: true,
	                    polylineOptions: {
	                        strokeWeight: 4,
	                        strokeOpacity: 0.8,
	                        strokeColor: "red"
	                    },
					});
				directionsDisplay2 = new google.maps.DirectionsRenderer({
					suppressMarkers: true,
					preserveViewport: false,
                    polylineOptions: {
                        strokeWeight: 4,
                        strokeOpacity: 0.8,
                        strokeColor: "green"
                    },
				});
				
				var oldList, newList, item;
			    $("#sortable_taskList, #sortable_session1, #sortable_session2, #sortable_session3").sortable({
			    	connectWith: ".taskSortable",
			        start: function(event, ui) {
			            item = ui.item;
			            newList = oldList = ui.item.parent().parent().parent().parent();
			        },
					stop : function(event, ui){
						if (oldList.attr('id') != newList.attr('id')) {
			            	if (newList.attr('id') != "task") {
			            		var valid = timeValidation(item.attr('majorlocationindex') , newList.attr('id'));
			            		if (!valid){
			            			item.addClass('invalidTime');
			            		}
			            		else{
			            			item.removeClass('invalidTime');
			            		}
			            	}
			            	else{
			            		item.removeClass('invalidTime');
			            	}
						}
						majorLocationSeq.splice(0,majorLocationSeq.length)

						majorLocationSeq = majorLocationSeq.concat($("#sortable_taskList").sortable('toArray', { attribute: 'majorlocationIndex' }));
						majorLocationSeq = majorLocationSeq.concat($("#sortable_session1").sortable('toArray', { attribute: 'majorlocationIndex' }));
						majorLocationSeq = majorLocationSeq.concat($("#sortable_session2").sortable('toArray', { attribute: 'majorlocationIndex' }));
						majorLocationSeq = majorLocationSeq.concat($("#sortable_session3").sortable('toArray', { attribute: 'majorlocationIndex' }));
		
						loadRoute();
					},
			        change: function(event, ui) {  
			            if(ui.sender) newList = ui.placeholder.parent().parent().parent().parent();
			        },
			        helper: 'clone'
			    });
			   
			    $(".btn-selectedTask").click(function(){
			    	var sessionDiv = $(this).parents(".sessionDiv:first");
			    	$(".selectedTask:checked").each(function(){
			    		var taskItem = $(this).parents("li:first")
			    		var valid = timeValidation(taskItem.attr('majorlocationindex'), sessionDiv.attr('id'));
			    		sessionDiv.find(".taskSortable").append(taskItem);
			    		if (!valid){
			    			taskItem.addClass('invalidTime');
	            		}
	            		else{
	            			taskItem.removeClass('invalidTime');
	            		}
			    		
			    	}).removeAttr("checked");
			    	majorLocationSeq.splice(0,majorLocationSeq.length)

					majorLocationSeq = majorLocationSeq.concat($("#sortable_taskList").sortable('toArray', { attribute: 'majorlocationIndex' }));
					majorLocationSeq = majorLocationSeq.concat($("#sortable_session1").sortable('toArray', { attribute: 'majorlocationIndex' }));
					majorLocationSeq = majorLocationSeq.concat($("#sortable_session2").sortable('toArray', { attribute: 'majorlocationIndex' }));
					majorLocationSeq = majorLocationSeq.concat($("#sortable_session3").sortable('toArray', { attribute: 'majorlocationIndex' }));
	
					loadRoute();
			    })
			    
				// Change label for Sessoins
				$('input[type=radio][name=session]').on('change', function(){
					if (!this.checked) return;
				   // Get all rows with search applied
				   if ($(this).val() == "AP") {
					   $("#session1").show();
					   $("#session3").hide();					   
					   var majorLocationIndex = $("#sortable_session3").sortable('toArray', { attribute: 'majorlocationIndex' });
					   for (i = 0; i < majorLocationIndex.length; i++){
						   var ml = getMajorLocationByIndex(majorLocationIndex[i]);
						   ml.session="";
						   genTaskList(ml);
					   }
					   $("#sortable_session3").empty();
					   loadRoute();
				   }
				   else if ($(this).val() == "PE"){
					   $("#session1").hide();
					   $("#session3").show();
					   var majorLocationIndex = $("#sortable_session1").sortable('toArray', { attribute: 'majorlocationIndex' });
					   for (i = 0; i < majorLocationIndex.length; i++){
						   var ml = getMajorLocationByIndex(majorLocationIndex[i]);
						   ml.session="";
						   genTaskList(ml);
					   }
					   $("#sortable_session1").empty();
					   loadRoute();
				   }
				   else{
					   // APE
					   $("#session1").show();
					   $("#session3").show();
				   }
				   
				   /*
				   if ($(this).val() == "AP") {
					   $('#session1_label').text("A Session");
					   $('#session2_label').text("P Session");
					   
				   } else {
					   $('#session1_label').text("P Session");
					   $('#session2_label').text("E Session");	   
				   }
					session1Ids = $("#sortable_session1").sortable('toArray', { attribute: 'id' });
					session2Ids = $("#sortable_session2").sortable('toArray', { attribute: 'id' });

					setTaskSessionWithSeq(session1Ids, $("#session1_label").html().split(" ")[0]);
					setTaskSessionWithSeq(session2Ids, $("#session2_label").html().split(" ")[0]);
					*/
				});	
								
				$(".submitBtn").on('click', function() {
					$("#status").val("Submitted");
					$("#mainForm").submit();
				});
				$(".saveBtn").on('click',function(){
					$("#status").val("Draft");
				})
				
				$("#mainForm").validate({
					rules : {
						ignore: [],  
						supervisorId: {
							conditionRequired: function(){
								return $("#status").val() == "Submitted"
							},
						},
					},
					messages: {
						supervisorId: {
							required: "<spring:message code='E00010' />",
						},
					},
					  submitHandler: function(form) {
						  var taskListIds = $("#sortable_taskList").sortable('toArray', { attribute: 'id' });
							
							if (taskListIds.length > 0 && $("#status").val() == "Submitted") {
								bootbox.alert({
									title: "Alert",
									message: "<spring:message code='E00131' />"
								});
								return false;
							}
							
							var session1Ids = $("#sortable_session1").sortable('toArray', { attribute: 'id' });
							var session2Ids = $("#sortable_session2").sortable('toArray', { attribute: 'id' });
							var session3Ids = $("#sortable_session3").sortable('toArray', { attribute: 'id' });

							var i = 1;
							i = setTaskSessionWithSeq(session1Ids, "A", i);
							i = setTaskSessionWithSeq(session2Ids, "P", i);
							i = setTaskSessionWithSeq(session3Ids, "E", i);
							
							$("#tab_3").find(".remark").each(function(){
								var index = $(this).data("index");
			    				majorLocation = getMajorLocationByIndex(index);
			    				majorLocation.remark = $(this).val();
							});
							
							for (var i=0 ; i < majorLocations.length ; i++) {
								if (majorLocations[i].assignments != null) {
									for (var j=0 ; j < majorLocations[i].assignments.length ; j++) {
										majorLocations[i].assignments[j].sequence = j+1;
										delete majorLocations[i].assignments[j].marker;
									}
								}
							}
							for ( var i=0 ; i <= majorLocations.length ; i++) {
								for ( var key in majorLocations[i] ) {
									if (key != "assignments") {
					                	$('#majorLocationData').append($("<input type='hidden' />")
					                		.attr("name","majorLocations["+i+"]."+key).attr("value",majorLocations[i][key]));
									} else {
										if (majorLocations[i].assignments != null) {
											for (var j=0 ; j <= majorLocations[i].assignments.length ; j++) {
												for (var key2 in  majorLocations[i].assignments[j] ) {
													if (key2 != "convenientStartTime" && key2 != "convenientEndTime" && key2 != "convenientStartTime2" && key2 != "convenientEndTime2") {
								                		$('#majorLocationData').append($("<input type='hidden' />")
								                			.attr("name","majorLocations["+i+"].itineraryPlanOutletModels["+j+"]."+key2).attr("value",majorLocations[i].assignments[j][key2]));
													}
												}
											}
										}
										
									}	
								}
							}
							//$("#inputDate").prop("disabled",false);
							form.submit();
						  }
				});
				<c:if test="${not empty model.supervisorId}">
					$("#supervisorId").append($("<option><option/>").attr("value", "<c:out value="${model.supervisorId}"/>").text("<c:out value="${supervisorText}"/>"));
					$("#supervisorId").val("<c:out value="${model.supervisorId}"/>").trigger("change");
				</c:if>
				
				<c:if test="${act == 'edit'}">
					$("#userId").append($("<option><option/>").attr("value", "<c:out value="${model.userId}"/>").text("<c:out value="${userText}"/>"));
					$("#userId").val("<c:out value="${model.userId}"/>").trigger("change");
					$('#userId').fireOnDisable().select2ajax();
					$("#userId").prop("disabled","true");
					
					$('#rootwizard').show();
					
					$("#inputDate").datepicker("setDate", parseDate('<c:out value="${commonService.formatDate(model.date)}"/>'));
					$("#inputDate").prop("disabled","true");
					//$("#inputDate").append($("<option><option/>").attr("value", '<c:out value="${commonService.formatDate(model.date)}"/>').text('<c:out value="${commonService.formatDate(model.date)}" />'));
					//$("#inputDate").val('<c:out value="${commonService.formatDate(model.date)}"/>').trigger("change");
					//$("#inputDate").prop("disabled","true");
					
					oldMajorlocation = ${commonService.jsonEncode(model.majorLocations)};
					oldMajorlocation = oldMajorlocation || []
					for (i = 0; i < oldMajorlocation.length; i++){
						var omj = oldMajorlocation[i];
						omj.index = i + 1;
						omj.assignments = omj.itineraryPlanOutletModels;
						delete omj.itineraryPlanOutletModels;
						for (j = 0; j < omj.assignments.length; j++){
							var a = omj.assignments[j];
							a.index = j + 1;
							a.group = i + 1;
							a.outletId = a.outletId == null? 0 : a.outletId;
							a.noOfQuotation = a.noOfQuotation == null? 0 : a.noOfQuotation;
							a.noOfAssignment = a.noOfAssignment == null? 0 : a.noOfAssignment;
							a.removable = a.removable == null? true : a.removable;
						}
					}
					
					<%--
					oldMajorlocation = [
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
							marketName : "<c:out value="${mj.marketName}" />",
							address : "<c:out value="${mj.address}" />",
							assignments : [
							<c:forEach items="${mj.itineraryPlanOutletModels}" var="assignment" varStatus="j">
							{
								
								itineraryPlanOutletId : <c:out value="${assignment.itineraryPlanOutletId}" />,
								index : <c:out value="${j.index+1}" />,
								group : <c:out value="${i.index+1}" />,
								sequence : <c:out value="${assignment.sequence}" />,
								outletId : <c:out value="${assignment.outletId == null ? 0 : assignment.outletId }" />,
								firmCode : "<c:out value="${assignment.firmCode}" />",
								firm : "<c:out value="${assignment.firm}" />",
								marketName : "<c:out value="${assignment.marketName}" />",
								districtId : <c:out value="${assignment.districtId}" />,
								district : "<c:out value="${assignment.district}" />",
								tpu : "<c:out value="${assignment.tpu}" />",
								address : "<c:out value="${assignment.address}" />",
								detailAddress : "<c:out value="${assignment.detailAddress}" />",
								noOfQuotation : <c:out value="${assignment.noOfQuotation == null ? 0 : assignment.noOfQuotation }" />,
								noOfAssignment : <c:out value="${assignment.noOfAssignment == null ? 0 : assignment.noOfAssignment }" />,
								convenientTime : "<c:out value="${assignment.convenientTime}" />",
								convenientTime2 : "<c:out value="${assignment.convenientTime2}" />",
								latitude : <c:out value="${assignment.latitude}" />,
								longitude : <c:out value="${assignment.longitude}" />,
								outletRemark : "<c:out value="${assignment.outletRemark}" />",
								deadline : "<c:out value="${assignment.deadline}" />",
								status : "<c:out value="${assignment.status}" />",
								planType : "<c:out value="${assignment.planType}" />",
								outletType : "<c:out value="${assignment.outletType}" />",
								assignmentIds : <c:out value="${assignment.assignmentIds}" />,
								removable: <c:out value="${assignment.removable == null ? true: assignment.removable}" />,
							},
							</c:forEach>
							]
						},
					</c:forEach>
					];
					--%>
					
					
					majorLocations = majorLocations.concat(oldMajorlocation);
					majorLocations = majorLocations || [];
					majorLocationIndex = majorLocations.length+1;
					for (var i=0 ; i < majorLocations.length ; i++) {
						if (!majorLocations[i].isNewRecruitmentTask && !majorLocations[i].isFreeEntryTask){
							majorLocations[i].color = assignMarkerColor(majorLocations);							
						}
						loadAssignments(majorLocations[i].assignments, null, i!=majorLocations.length-1);
					}
					$("#addAssignmentButton").show();	
					$(".toggleMapBtn").show()
					//initMajorLocationGrouping();
				</c:if>
				
				<sec:authorize access="!hasPermission(#user, 4) && !hasPermission(#user, 256) && hasPermission(#user, 16)">
					$('#hiddenUserId').val(<c:out value="${userId}"/>);
					$('#rootwizard').show();
				</sec:authorize>
				
				
				if ($("input[name=session]:checked").val() == "AP") {
				   $("#session1").show();
				   $("#session3").hide();	
				}
				else if ($("input[name=session]:checked").val() == "PE"){
				   $("#session1").hide();
				   $("#session3").show();
			 	}
				else{
					   // APE
				   $("#session1").show();
				   $("#session3").show();
				}
				/*
				$('#inputDate').on("change", function(e) { 
					
					$("#addAssignmentButton").show();
					$(".toggleMapBtn").show();
					
					removeAssignements(-1);
					removeMajorLocation(-1);
					
					var newAssignments = [];
					$.ajax({
						url: "<c:url value='/itineraryPlanning/ItineraryPlan/queryAssignments'/>",
						type: 'POST',
						async: false,
						data: { 
							officerId: $('#hiddenUserId').val(),
							assignedCollectionDate: $('#inputDate').select2('val'),
							},
						success: function (response) {
							newAssignments = response;
						}
					});
					loadAssignments(newAssignments, false);
				});*/
				
				//$("input[name='session']").trigger("change")
			});
			
			function editDefaultML(btn){
				var cell = $(btn).parents("td:first");
				cell.find(".displayBlock").hide();
				cell.find(".editBlock").show();
				cell.find(".editBtn").show();
				var marketName = cell.find(".originalText").val();
				cell.find(".inputText").val(marketName);
			}
			function undoEditML(btn){
				var cell = $(btn).parents("td:first");
				cell.find(".displayBlock").show();
				cell.find(".editBlock").hide();
			}
			function saveDefaultML(btn){
				var cell = $(btn).parents("td:first");
				var editBlock = cell.find(".editBlock");
				var outletId = cell.find(".hiddenOutlet").val();
				var ml = cell.find(".inputText").val();
				var loadingIcon = '<img class="loading" src="<c:url value='/resources/images/ui-anim_basic_16x16.gif' />" style="border:0px; position: absolute;" />'
				editBlock.prepend(loadingIcon);
				editBlock.find(".editBtn").hide();				
				$.post("<c:url value='/itineraryPlanning/ItineraryPlan/updateDefaultMajorLocation'/>", {outletId: outletId, majorLocation: ml}, 
						function(ret){
							editBlock.find(".loading").remove();
							if (ret == "Success"){
								cell.find(".displayBlock").show();
								cell.find(".editBlock").hide();
								cell.find(".displayText").text(ml);
								cell.find(".originalText").val(ml);
								var ass = _.find(assignments, function(data){
									return data.outletId == outletId;
								});
								ass.marketName = ml;
								$("#majorLoc ul").remove();
								
								if (majorLocations.length > 0 ) {	
									for (var i = 0 ; i < majorLocations.length ; i++ ) {
										if (majorLocations[i].isFreeEntryTask == false) {
											renderMajorLocation(majorLocations[i])
								    		var mjAssignments = majorLocations[i].assignments;
									    	for (var j = 0 ; j < mjAssignments.length ; j++ ) {
									    		addNewAssignmentToMajorLocation(majorLocations[i], mjAssignments[j]);
									    	}
										}
									}
								}
							}
							else{
								bootbox.alert({
								    title: "Alert",
								    message: "<spring:message code='E00012' />"
								});
							}
							editBlock.find(".editBtn").show();	
						});
			}
	
		</script>
		
		<script id="defaultMLTemplate" type="text/x-jquery-tmpl">
			<div>
				<div class="displayBlock">
					<span class="displayText">\${marketName}</span>
					<a href='javascript:void(0)' onclick='editDefaultML(this)'><i class='fa fa-fw fa-edit'></i></a>
					<input type="hidden" value="\${marketName}"  class="originalText" />
				</div>
				<div style="display:none" class="editBlock">
					<input type="text" value="\${marketName}" class="inputText" />
					<input type="hidden" value="\${outletId}" class="hiddenOutlet" />
					<a href='javascript:void(0)' onclick='saveDefaultML(this)' class="editBtn" ><i class='fa fa-fw fa-save'></i></a>
					<a href='javascript:void(0)' onclick='undoEditML(this)' class="editBtn" ><i class='fa fa-fw fa-undo'></i></a>
				</div>
			</div>
		</script>		
	    <script id="majorLocationTemplate" type="text/x-jquery-tmpl">
			<ul id="majorLocation_\${index}" class="list-group">
				<li class="list-group-item col-sm-12">
					<span id="majorLocationColor_\${index}" class='glyphicon glyphicon-home' aria-hidden='true' style="color:white; padding: 3px 3px 3px 3px;"></span>
					<label id="majorLocationName_\${index}">\${taskName}</label> <span id="editMajorLocation_\${index}" class='glyphicon glyphicon-pencil' aria-hidden='true'></span>														
					<span style="float: right; padding: 5px 2px 2px 10px;" id="removeMajorLocation_\${index}" class="glyphicon glyphicon-remove mjLocRemove" ></span>
					<button style="float: right; " id="selectAssignments_\${index}" class="btn btn-info" type="button">Add Selected Assignments</button>
				<li class="list-group-item col-sm-12">
				<ul id="sortable_\${index}" class="list-group locationSortable" style="min-height:30px">
				</ul>
			</ul>
		</script>
        <script id="assignmentTemplate" type="text/x-jquery-tmpl">
			<li id="majorLocationAssignment_\${seq}" class="list-group-item" data-index="\${index}">
				<span style="float: right; padding: 2px 2px 2px 0;" id="removeMajorLocationAssignment_\${seq}" class="glyphicon glyphicon-remove mjLocRemove" ></span>
				<p>Firm: \${firm}</p>
				<p>Address: \${detailAddress}</p>
				<p>Default Major Location: \${marketName}</p>
			</li>
		</script>
		<script id="majorTaskTemplate" type="text/x-jquery-tmpl">
			<li id="task_\${index}" class="col-md-3 checklist list-group-item taskItem" majorlocationIndex="\${index}" majorlocationRouteIndex="\${index}">
				<div class="input-group col-sm-12">
                    <span id="taskColor_\${index}" class='glyphicon glyphicon-home pull-left' aria-hidden='true' style="color:white; padding: 3px 3px 3px 3px;"></span>
					<label id="taskName_\${index}" class="col-sm-11">	
					<input type="checkbox" class="selectedTask" /> {{if firmName}}\${firmName} | \${taskName} {{else}} \${taskName} {{/if}} (No. of Assignment: \${noOfAssignment} )</label>
					<div id="taskRemark_\${index}" class="hidden">
						<input type="text" id="taskRemark_\${index}" data-index="\${index}" class="form-control pull-right remark" value="\${remark}" placeholder="Remark">
					</div>
				</div>
			</li>
		</script>
		<script id="taskTemplate" type="text/x-jquery-tmpl">
			<li id="task_\${index}" class="col-md-3 checklist list-group-item taskItem" majorlocationIndex="\${index}">
				<div class="input-group col-sm-12">
					<span id="removeTask_\${index}" class="btnRemoveTask glyphicon glyphicon-remove" ></span>
					<div id="taskName_\${index}" class="col-sm-11" style="padding-left:20px">
						<input type="checkbox" class="selectedTask" style="float:left;margin-left:-20px" />
						<p><label>Task: </label> <label id="taskLabel_\${index}">\${taskName}</label> <span id="editTask_\${index}" class='glyphicon glyphicon-pencil' aria-hidden='true'></span><p>
						<p><label>Location: </label> <label id="taskLocation_\${index}">\${location}</label></p>
					</div>
					<div id="taskRemark_\${index}" class="hidden">
						<input type="text" id="taskRemark_\${index}" data-index="\${index}" class="form-control remark" value="\${remark}" placeholder="Remark">
					</div>
				</div>
			</li>
		</script>
		<script id="recruitmentTemplate" type="text/x-jquery-tmpl">
			<li id="task_\${index}" class="col-md-3 checklist list-group-item taskItem" majorlocationIndex="\${index}">
				<div class="input-group col-sm-12">
					<span id="removeTask_\${index}" class="btnRemoveTask glyphicon glyphicon-remove" ></span>
					<div id="taskName_\${index}" class="col-sm-11" style="padding-left:20px">
						<input type="checkbox" class="selectedTask" style="float:left;margin-left:-20px"  />
						<p><label>Task: </label> <label id="taskLabel_\${index}">\${taskName}</label> <span id="editRecruitment_\${index}" class='glyphicon glyphicon-pencil' aria-hidden='true'></span><p>
						<p><label>District: </label> <label id="recruitmentDistrict_\${index}">\${district}</label></p>
						<p><label>Tpu: </label> <label id="recruitmentTpu_\${index}">\${tpu}</label></p>
						<p><label>Street: </label> <label id="recruitmentStreet_\${index}">\${street}</label></p>
					</div>
					<div id="taskRemark_\${index}" class="hidden">
						<input type="text" id="taskRemark_\${index}"  data-index="\${index}" class="form-control remark" value="\${remark}" placeholder="Remark">
					</div>
				</div>
			</li>
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
        	<h1>Itinerary Plan</h1>
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
        	<form id="mainForm" method="post" role="form" action="<c:url value='/itineraryPlanning/ItineraryPlan/save'/>"
        		enctype="multipart/form-data">
        		<input id="itineraryPlanId" name="itineraryPlanId" value="<c:out value="${model.itineraryPlanId}" />" type="hidden" />
        		<input id="version" name="version" value="<c:out value="${model.version}" />" type="hidden" />
        		<input id="status" name="status" value="<c:out value="${model.status}" />" type="hidden" />
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/itineraryPlanning/ItineraryPlan/home'/>">Back To Main</a>
								<c:if test="${act == 'edit'}">
									<a href="<c:url value='/itineraryPlanning/ItineraryPlan/print?id='/><c:out value="${model.itineraryPlanId}" />" class="pull-right btn btn-default" target="_blank" >Print</a>								
								</c:if>
							</div>
	       					<div class="box-body">
	       						<div class="form-horizontal">
	       							<div class="col-sm-12">
	       							    <div class="col-md-5">
	       							    	<sec:authorize access="hasPermission(#user, 4) or hasPermission(#user, 256)">
		       							    	<div class="form-group">
			       							    	<div class="col-md-4">
			       							    		<label>Field Officer:</label>
			       							    	</div>
			       							    	<div class="col-md-8">
			       							    		<select class="form-control select2 " id="userId" name="userId"
															data-ajax-url="<c:url value='/itineraryPlanning/ItineraryPlan/queryOfficerSelect2'/>"></select>
			       							    	</div>
			       							    </div>
		       							    </sec:authorize>
	       							    	<input type="hidden" id="hiddenUserId" name="userId" value="<c:out value="${model.userId}" />">

		       							    
		       							    <div class="form-group">
		       							    	<div class="col-md-4">
		       							    		<label>Submit To:</label>
		       							    	</div>
		       							    	<div class="col-md-8">
		       							    		<select class="form-control select2" id="supervisorId" name="supervisorId" 
															data-ajax-url="<c:url value='/itineraryPlanning/ItineraryPlan/querySupervisorSelect2'/>"></select>
		       							    	</div>
		       							    </div>
		       							    <c:if test="${model.status == 'Rejected'}">
		       							    <div class="form-group">
		       							    	<div class="col-md-4">
		       							    		<label>Reject Reason:</label>
		       							    	</div>
		       							    	<div class="col-md-7">
		       							    		<label>${model.rejectReason }</label>
		       							    	</div>
		       							    </div>
		       							    </c:if>
	       							    </div>
		       							<div class="col-md-7">
														<p class="text-right">
														No. of outlets : <span id="noOfAssignment" class="badge">0</span>
	       							    				No. of quotations : <span id="noOfQuotation" class="badge">0</span>
	       							    				No. of TPU : <span id="noOfTpu" class="badge">0</span>
	       							    				No. of District : <span id="noOfDistrict" class="badge">0</span>
	       							    				</p>
	       							    	</div>
	       							    </div>
	       							   	<div class="form-horizontal" id="rootwizard">
	       									<div class="col-sm-12">
	       										<div class="navbar">
		       										<div class="navbar-inner nav-tabs-custom">
       													<ul>
														  	<li><a href="#tab_1" data-toggle="tab">Select Date &amp; Assignment</a></li>
															<li><a href="#tab_2" data-toggle="tab">Set Major Location</a></li>
															<li><a href="#tab_3" data-toggle="tab">Visiting Sequence</a></li>
														</ul>
			       									</div>
		       									</div>
		       									<div id="bar" class="progress progress-striped active">
													<div class="bar progress-bar progress-bar-primary progress-bar-striped"></div>
												</div>
			       									<div class="tab-content">
			       										<ul class="pager wizard">
			       										<%--
															<li class="previous first" style="display:none;"><a href="#">First</a></li>
														 --%>
															<li class="previous"><a href="#">Previous</a></li>
															<!-- 
															<li class="next last" style="display:none;"><a href="#">Last</a></li>
															 -->
														  	<li class="next"><a href="#">Next</a></li>
														  	<%--
														  	<sec:authorize access="hasPermission(#user, 4) or hasPermission(#user, 16) or hasPermission(#user, 256)">
															  	<li class="next finish"><button id="submitBtn" type="button" class="btn btn-info"  style="float:right">Submit</button></li>														  	
															  	<li class="next finish"><button id="saveBtn" type="submit" class="btn btn-info" style="float:right; margin-right:5px">Save</button></li>
													  		</sec:authorize>
														  	 --%>														  	
														  
														  	<sec:authorize access="!hasPermission(#user, 4) && !hasPermission(#user, 256) && hasPermission(#user, 16)">
														  	
														  		<c:if test="${model.status eq 'Draft' or model.status eq 'Rejected' or model.status eq 'Submitted' }">
																  	<li class="next finish"><button id="submitBtn" type="button" class="btn btn-info submitBtn"  style="float:right">Submit</button></li>														  	
																  	<li class="next finish"><button id="saveBtn" type="submit" class="btn btn-info saveBtn" style="float:right; margin-right:5px">Save</button></li>
													  			</c:if>
													  		</sec:authorize>
													  		<sec:authorize access="hasPermission(#user, 4) or hasPermission(#user, 256)">													  		
													  			<c:if test="${not(model.status eq 'Approved') or model.status eq 'Approved' and !timeLogApproved }">													  			
																  	<li class="next finish"><button id="submitBtn" type="button" class="btn btn-info submitBtn"  style="float:right">Submit</button></li>														  	
																  	<li class="next finish"><button id="saveBtn" type="submit" class="btn btn-info saveBtn" style="float:right; margin-right:5px">Save</button></li>
													  			</c:if>
													  		</sec:authorize>
														</ul>
			       									
				       									<div id="tab_1" class="tab-pane active">
					       									<div class="form-group">
							       								<label class="col-sm-1 control-label">Date</label>
							       								<div class="col-sm-3">
							       								<%--
							       									<select class="form-control select2 " id="inputDate" name="inputDate"
																		data-ajax-url="<c:url value='/itineraryPlanning/ItineraryPlan/queryDateSelect2'/>" required></select>	
																 --%>
																 
																 	<div class="input-group">
																 		<input type="text" <c:if test="${act != 'edit'}">name="inputDate"</c:if> id="inputDate" class="form-control datepicker" />	
																 		<div class="input-group-addon">
																			<i class="fa fa-calendar"></i>
																		</div>
																 	</div>	
																	 <c:if test="${act == 'edit'}">	
																	 	<input type="hidden" name="inputDate" value="<c:out value="${commonService.formatDate(model.date)}"/>" />	
																	 </c:if>														
																</div>
							       								<div class="col-sm-12">
																	<table class='text-center table table-hover table-striped table-bordered table-hover dataTable no-footer' id='assignmentTable1'>
																	</table>
																	<p id='addAssignmentButton'>
																		<label class="col-md-2 textlink" id="labelAddOutlet"><a id="addOutlet" ><span id="addOutletIcon" class="glyphicon glyphicon-plus"></span> Add CPI Outlet </a></label> 
																		<label class="col-md-2 textlink" id="labelAddBuilding"><a id="addBuilding" ><span id="addBuildingIcon" class="glyphicon glyphicon-plus"  ></span> Add Building &amp; GHS </a></label>
																	</p>
																</div>
															</div>
															<div class="form-group">
																<!--  Map -->
																<div class="col-sm-12" >
																	<div style="height: 500px;"  id="map1">
																		<div id="map_canvas1" class="map_canvas"></div>
																	</div>
																	<div class="toggleMapBtn">
																		<button class="btn btn-info" type="button" onclick="toggleInfoWindowMarker1(true)">Open All Info Window</button>
																		<button class="btn btn-info" type="button" onclick="toggleInfoWindowMarker1(false)">Close All Info Window</button>
																	</div>
																</div>
															</div>
														</div>
														<div id="tab_2" class="tab-pane">
														<div class="form-group">
							       								<label class="col-sm-1 control-label">Outlet Type:</label>
							       								<div class="col-sm-3">
																	<select class="form-control select2 " id="outletType_filter" name="outletTypeId" 
																		data-ajax-url="<c:url value='/itineraryPlanning/ItineraryPlan/queryOutletTypeFilterSelect2'/>" multiple="multiple">
																	</select>
																</div>
																<label class="col-sm-1 control-label">District:</label>
							       								<div class="col-sm-3">
																	<select class="form-control select2 " id="district_filter" name="districtId">
																	<option value=""></option>
																	</select>
																</div>
																<label class="col-sm-1 control-label">TPU:</label>
							       								<div class="col-sm-3">
																	<select class="form-control select2 filters"  id="tpu_filter" name="tpuId">
																	<option value=""></option>
																	</select>
																</div>
															</div>
						       								<div class="form-group">
							       								<div class="col-sm-12">
																	<table class='text-center table table-hover table-striped table-bordered table-hover dataTable no-footer' id='assignmentTable2'>
																		<thead>
																			<tr>
																				<th><input type="checkbox" id="table2-select-all" /></th>																				
																				<th>Reference no</th>
																				<th>Name</th>
																				<th>MRPS District</th>
																				<th>TPU</th>
																				<th>Full Address</th>
																				<th>No. of Quotation (MRPS only)</th>
																				<th>Convenient Time</th>
																				<th>Remark</th>
																				<th>Default Major Location</th>
																				<th>Deadline / Collection Date</th>
																				<!-- 
																				<th>Status</th>
																				 -->
																			</tr>
																		</thead>
																	</table>
																</div>
															</div>
															<div class="form-group" style="overflow-y:auto;overflow-x:hidden">
																<div id="majorLoc" class="col-sm-4">
																	<label class="col-md-10 textlink" id="labelAddMajorLoc">
																		<a id="addTask" onclick="addMajorLocation()" ><span class="glyphicon glyphicon-plus"></span> Add Major Location </a>
																	</label>
																</div>
																<!--  Map -->
																<div class="col-sm-8" >			
																	<div style="height: 500px;"  id="map2">			
																	<%--											
																		<div id="map_canvas2" class="map_canvas"></div>
																	 --%>
																	</div>
																	<div>
																		<button class="btn btn-info" type="button" onclick="toggleInfoWindowMarker2(true)">Open All Info Window</button>
																		<button class="btn btn-info" type="button" onclick="toggleInfoWindowMarker2(false)">Close All Info Window</button>
																	</div>
																</div>
															</div>
														</div>
														<div id="tab_3" class="tab-pane">
															<div class="form-group">
																<div class="col-sm-2">
																	<label>
																		<input type="radio" value="AP" name="session" id="session" <c:if test="${model.session != 'PE'}">checked</c:if> /> AP session
																	</label>
																</div>
																<div class="col-sm-2">
																	<label>
																		<input type="radio" value="PE" name="session" id="session" <c:if test="${model.session == 'PE'}">checked</c:if> /> PE session
																	</label>
																</div>
																<div class="col-sm-2">
																	<label>
																		<input type="radio" value="APE" name="session" id="session" <c:if test="${model.session == 'APE'}">checked</c:if> /> APE session
																	</label>
																</div>
																<div class="col-sm-4">
																	<button type="button" class="btn btn-primary" id="btnSelectAll">Select All</button>
																</div>
															</div>
															
															<div class="form-group" style="overflow-y:auto; overflow-x:hidden">
																
																<div id="task" class="col-sm-12">
																	<ul id="list_task" class="list-group taskpool">
																		<li id="task_title" class="list-group-item"><label>Activity List:</label></li>
																		<li class="list-group-item col-sm-12">
																			<ul id="sortable_taskList" class="list-group taskSortable bg_drop">
																			</ul>
																		</li>
																	</ul>
																	<label class="col-sm-2" id="labelAddTask"><a class="textlink" id="addTask" onclick="addTask()" ><span class="glyphicon glyphicon-plus"></span> Add Activity </a></label>
																	<label class="col-sm-2" id="labelAddRecruitment"><a class="textlink" id="addRecruitment" onclick="addRecruitment()" ><span class="glyphicon glyphicon-plus"></span> Add New Recruitment </a></label>		
																</div>
																<div class="row"> </div>
																<div id="session1" class="col-sm-12 sessionDiv" data-session="A">
																	<ul id="list_session1" class="list-group">
																		<li id="session1_title" class="list-group-item">
																			<label id='session1_label'>A session</label>
																			<input type="button" class="btn btn-primary btn-selectedTask" value="Add selected task" />
																		</li>
																		<li class="list-group-item col-sm-12">
																			<ul id="sortable_session1" class="list-group taskSortable bg_drop ">
																			</ul>
																		</li>
																	</ul>
																</div>
																
																<div id="session2" class="col-sm-12 sessionDiv"  data-session="P">
																	<ul id="list_session2" class="list-group">
																		<li id="Session2_title" class="list-group-item">
																			<label id='session2_label'>P session</label>
																			<input type="button" class="btn btn-primary btn-selectedTask" value="Add selected task" />
																		</li>
																		<li class="list-group-item col-sm-12">
																			<ul id="sortable_session2" class="list-group taskSortable bg_drop ">
																			</ul>
																		</li>
																	</ul>
																</div>
																
																<div id="session3" class="col-sm-12 sessionDiv" data-session="E">
																	<ul id="list_session3" class="list-group">
																		<li id="Session3_title" class="list-group-item">
																			<label id='session3_label'>E session</label>
																			<input type="button" class="btn btn-primary btn-selectedTask" value="Add selected task" />
																		</li>
																		<li class="list-group-item col-sm-12">
																			<ul id="sortable_session3" class="list-group taskSortable bg_drop ">
																			</ul>
																		</li>
																	</ul>
																</div>
															
													
																<!--  Map -->
																<div class="col-sm-12" >
																	<div style="height: 500px;" id="map3">		
																	<%--
																		<div id="map_canvas3" class="map_canvas"></div>
																	 --%>
																	</div>
																	<div>
																		<button class="btn btn-info" type="button" onclick="toggleInfoWindowMarker3(true)">Open All Info Window</button>
																		<button class="btn btn-info" type="button" onclick="toggleInfoWindowMarker3(false)">Close All Info Window</button>
																	</div>
																</div>
															</div>
															<div id="majorLocationData"></div>
																<!--
															<div class="box-footer">
																<div id="majorLocationData"></div>
															 
																<button id="submitBtn" type="button" class="btn btn-info pull-right">Submit</button>
								        						<button id="saveBtn" type="submit" class="btn btn-info">Save</button>	
								        						     						
							       							</div>
							       							 -->  
														</div>
														<ul class="pager wizard">
														<%--
															<li class="previous first" style="display:none;"><a href="#">First</a></li>
														 --%>
															<li class="previous"><a href="#">Previous</a></li>
															<!-- 
															<li class="next last" style="display:none;"><a href="#">Last</a></li>
															 -->
														  	<li class="next"><a href="#">Next</a></li>
														  	<%--
														  	<sec:authorize access="hasPermission(#user, 4) or hasPermission(#user, 16) or hasPermission(#user, 256)">
															  	<li class="next finish"><button id="submitBtn" type="button" class="btn btn-info"  style="float:right">Submit</button></li>														  	
															  	<li class="next finish"><button id="saveBtn" type="submit" class="btn btn-info" style="float:right; margin-right:5px">Save</button></li>
													  		</sec:authorize>
														  	 --%>														  	
														  
														  	<sec:authorize access="!hasPermission(#user, 4) && !hasPermission(#user, 256) && hasPermission(#user, 16)">
														  	
														  		<c:if test="${model.status eq 'Draft' or model.status eq 'Rejected' or model.status eq 'Submitted' }">
																  	<li class="next finish"><button id="submitBtn" type="button" class="btn btn-info submitBtn"  style="float:right">Submit</button></li>														  	
																  	<li class="next finish"><button id="saveBtn" type="submit" class="btn btn-info saveBtn" style="float:right; margin-right:5px">Save</button></li>
													  			</c:if>
													  		</sec:authorize>
													  		<sec:authorize access="hasPermission(#user, 4) or hasPermission(#user, 256)">													  		
													  			<c:if test="${not(model.status eq 'Approved') or model.status eq 'Approved' and !timeLogApproved }">													  			
																  	<li class="next finish"><button id="submitBtn" type="button" class="btn btn-info submitBtn"  style="float:right">Submit</button></li>														  	
																  	<li class="next finish"><button id="saveBtn" type="submit" class="btn btn-info saveBtn" style="float:right; margin-right:5px">Save</button></li>
													  			</c:if>
													  		</sec:authorize>
														</ul>
													</div>
											</div>
										</div>
									</div>
								</div>
							</div>
							
						</div>
						</div>
					</form>
					<!-- Add Major Location Dialog -->
					<div class="modal fade" id="majorLocationForm" tabindex="-1" role="dialog" aria-labelledby="dialogLabel" aria-hidden="true">
					  <div class="modal-dialog">
						<div class="modal-content">
							<form id="majorLocationFormDummy" >
								<div  class="clearfix overlay" data-bind="loading:isLoading, visible:isLoading" ></div>
								  <div class="modal-header">
									<h4 class="modal-title" id="majorLocationDialogLabel" data-bind="text:formTitle">Add Major Location</h4>
								  </div>
								  <div class="modal-body form-horizontal">
									<div class="form-group">
										<div class="col-md-3 control-label">Name</div>
										<div class="col-md-7">
											<input id="majLocName" type="text" class="form-control" required />
										</div>
										<div style="margin-top: 30px;" class="col-md-3 control-label"></div>
										<div style="margin-top: 30px;" class="col-md-2">
											<button type="submit" class="btn btn-primary" data-loading-text="Loading..." id="addMajorLocBtn">Submit</button>
										</div>
										<div style="margin-top: 30px;" class="col-md-5">
											<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
										</div>
									</div>
								  </div>
							  </form>
						</div>
					  </div>
					</div>
					<!-- Add Task Dialog -->
					<div class="modal fade" id="taskForm" tabindex="-1" role="dialog" aria-labelledby="dialogLabel" aria-hidden="true">
					  <div class="modal-dialog">
						<div class="modal-content">
							<div  class="clearfix overlay" data-bind="loading:isLoading, visible:isLoading" ></div>
							  <div class="modal-header">
								<h4 class="modal-title" id="taskDialogLabel" data-bind="text:formTitle">Add Task</h4>
							  </div>
							  	<div class="modal-body form-horizontal">
									<div class="form-group">
										<div class="col-md-3 control-label">Subject:</div>
										<div class="col-md-7">
											<input id="taskSubject" type="text" maxlength="512" class="form-control"/>
										</div>
									</div>
									<div class="form-group">
										<div class="col-md-3 control-label">Location:</div>
										<div class="col-md-7">
											<input id="taskLocation" type="text" maxlength="4000" class="form-control" />
										</div>
									</div>
									<div style="margin-top: 30px;" class="col-md-3 control-label"></div>
									<div style="margin-top: 30px;" class="col-md-2">
										<button type="submit" class="btn btn-primary" data-loading-text="Loading..." id="addTaskBtn">Submit</button>
									</div>
									<div style="margin-top: 30px;" class="col-md-5">
										<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
									</div>
								</div>
							  </div>
						</div>
					  </div>
					<!-- Add new recruitment Dialog -->
					<div class="modal fade" id="recruitmentForm" tabindex="-1" role="dialog" aria-labelledby="dialogLabel" aria-hidden="true">
					  <div class="modal-dialog">
						<div class="modal-content">
							<div  class="clearfix overlay" data-bind="loading:isLoading, visible:isLoading" ></div>
							  <div class="modal-header">
								<h4 class="modal-title" id="recruitmentDialogLabel" data-bind="text:formTitle">Add New Recruitment</h4>
							  </div>
							  	<div class="modal-body form-horizontal">
									<div class="form-group">
										<div class="col-md-3 control-label">District:</div>
										<div class="col-md-7">
											<input id="recruitmentDistrict" type="text" maxlength="512" class="form-control"/>
										</div>
									</div>
									<div class="form-group">
										<div class="col-md-3 control-label">Tpu:</div>
										<div class="col-md-7">
											<input id="recruitmentTpu" type="text" maxlength="512" class="form-control" />
										</div>
									</div>
									<div class="form-group">
										<div class="col-md-3 control-label">Street:</div>
										<div class="col-md-7">
											<input id="recruitmentStreet" type="text" maxlength="2000" class="form-control" />
										</div>
									</div>									
									<div style="margin-top: 30px;" class="col-md-3 control-label"></div>
									<div style="margin-top: 30px;" class="col-md-2">
										<button type="submit" class="btn btn-primary" data-loading-text="Loading..." id="addRecruitmentBtn">Submit</button>
									</div>
									<div style="margin-top: 30px;" class="col-md-5">
										<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
									</div>
								</div>
							  </div>
						</div>
					  </div>					  
        </section>
	</jsp:body>
</t:layout>

