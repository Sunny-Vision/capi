var markerColors = ["YellowGreen", "GoldenRod", "Tomato" , "SlateBlue", "Navy", "Red" , "Orange" , "LimeGreen" ,"HotPink" , "Blue", "Brown", "Teal", "DarkOrchid", "Grey" , "Salmon"];


function pinSymbol(color) {
	return {
	    path: 'M 0,0 C -2,-20 -10,-22 -10,-30 A 10,10 0 1,1 10,-30 C 10,-22 2,-20 0,0 z',
	    fillColor: color,
	    fillOpacity: 1,
	    strokeColor: '#000',
	    strokeWeight: 2,
	    scale: 1.3
	};
}

function assignMarkerColor(majorLocations) {
	var assignedColor = [];
	for (var i=0 ; i < majorLocations.length ; i++) {
		if (majorLocations[i].color != null) {
	 		assignedColor.push(majorLocations[i].color);
		}
	}
	for (var i=0 ; i < markerColors.length ; i++ ) {
		var used = false;
		for (var j=0 ; j < assignedColor.length ; j++) {
			if ( markerColors[i] == assignedColor[j]) {
				used = true;
				break;
			}
		}
		if (!used)
			return markerColors[i];					
	}
	
	return "SeaGreen";
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