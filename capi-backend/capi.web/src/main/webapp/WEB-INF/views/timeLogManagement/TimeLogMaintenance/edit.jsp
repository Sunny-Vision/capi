<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page session="false"%>
<sec:authentication property="details" var="userDetail" />
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/timepicker-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp"%>
	</jsp:attribute>

	<jsp:attribute name="header">
		<style>			
			.btn.pull-right {
				margin-left: 10px;
			}
			.ui-sortable tr {
			    cursor:pointer; 
			}
			#telEnuTable {
				width: 80%;
			}
			.glyphicon-pencil, .glyphicon-remove, .glyphicon-plus {
    			cursor: pointer;
			}
			.radio
			{
				margin-left: 5px;
				margin-right: 5px;
			}
			.sorting, .sorting_asc, .sorting_desc {
			    background : none!;
			}
			.row .reject-container{
				color: #ff0000;
				margin-left: 0;
				margin-right: 0;
			}
			.reject-btn{
				padding-left: 0;
			}
			.reject-label{
				text-align: right;
			}
		</style>
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp"%>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp"%>
		<%@include file="/WEB-INF/views/includes/datepicker.jsp"%>
		<%@include file="/WEB-INF/views/includes/datejs.jsp" %>
		<%@include file="/WEB-INF/views/includes/timepicker.jsp" %>
		<%@include file="/WEB-INF/views/includes/moment.jsp" %>
		<script src="<c:url value='/resources/js/underscore-min.js'/>" ></script>
		<script>
		var surveyType = [
			<c:forEach var="survey" items="${surveyCountList}">
				"<c:out value='${survey}'/>",
			</c:forEach>		                  
		];
		
		var telephoneTimeLogs = [];
		var telephoneCounter = 1;
		var fieldworkTimeLogs = [];
	    var fieldworkCounter = 1;
	    var deviationStatus = "";
		
		function editTelephoneTimeLog(index) {
			telephoneFormValidator.resetForm();
			setTelephoneFormValue(getTelephoneTimeLogByIndex(index));
			$("#telephoneAction").val('edit');
			$('#telephoneDialogLabel').text('Edit Telephone Time Log');
			$("#telephoneModal").modal("show");
		}
		
		function editFieldworkTimeLog(index) {
			fieldworkFormValidator.resetForm();
			setFieldworkFormValue(getFieldworkTimeLogByIndex(index));
			$("#fieldworkAction").val('edit');
			$('#fieldworkDialogLabel').text('Edit Fieldwork Time Log');
			$("#fieldworkModal").modal("show");
		}
		
		function getTelephoneTimeLogByIndex(index) {
			for (var i=0 ; i < telephoneTimeLogs.length ; i++) {
				if (telephoneTimeLogs[i].index == index)
					return telephoneTimeLogs[i];
			}
		}
		
		function getFieldworkTimeLogByIndex(index) {
			for (var i=0 ; i < fieldworkTimeLogs.length ; i++) {
				if (fieldworkTimeLogs[i].index == index)
					return fieldworkTimeLogs[i];
			}
		}
		
		function removeTelephoneTimeLogByIndex(index) {
			telephoneTimeLogs = jQuery.grep(telephoneTimeLogs, function(value) {
				  return value.index != index;
				});
		}
		
		function removeFieldworkTimeLogByIndex(index) {
			fieldworkTimeLogs = jQuery.grep(fieldworkTimeLogs, function(value) {
				  return value.index != index;
				});
		}
		
		function removeTelephoneTimeLog(index) {
			removeTelephoneTimeLogByIndex(index);
			telephoneTable.row('#'+index).remove().draw();
			calculateTelephoneCounts();
		}
		
		function removeFieldworkTimeLog(index) {
			removeFieldworkTimeLogByIndex(index);
			fieldworkTable.row('#'+index).remove().draw();
			calculateOT();
			calculateFieldworkCounts();
		}
		
		function resetTelephoneForm() {
			setTelephoneFormValue({});
			telephoneFormValidator.resetForm();
		}
		
		function resetFieldworkForm() {
			setFieldworkFormValue({});
			fieldworkFormValidator.resetForm();
		}
		
		function setTelephoneFormValue(newTelephoneTimeLog){
			$('#telephoneIndex').val(newTelephoneTimeLog.index);
			$('#telephoneId').val(newTelephoneTimeLog.id);
			$('#telephoneAssignmentId').val(newTelephoneTimeLog.assignmentId);
			$('#telephoneReferenceExist').val(newTelephoneTimeLog.referenceExist);
			$('#telephoneReferenceMonth').val(newTelephoneTimeLog.referenceMonth);
			if ($('#telephoneReferenceMonth').val() == "") {
				$('#telephoneCaseReferenceNo').attr('disabled','disabled');
			} else {
				$('#telephoneCaseReferenceNo').removeAttr('disabled');
			}
			$('#telephoneSurvey').val(newTelephoneTimeLog.survey).trigger('change');
			//console.log('setTelephoneFormValue-> newTelephoneTimeLog.caseReferenceNo : ' + newTelephoneTimeLog.caseReferenceNo);
			if (newTelephoneTimeLog.caseReferenceNo !== undefined)
				$("#telephoneCaseReferenceNo").append($("<option><option/>").attr("value", newTelephoneTimeLog.caseReferenceNo).text(newTelephoneTimeLog.caseReferenceNo));
			$('#telephoneCaseReferenceNo').val(newTelephoneTimeLog.caseReferenceNo).trigger('change');
			$('#telephoneCompletionQuotationCount').val(newTelephoneTimeLog.completionQuotationCount).trigger('change');
			$('#telephoneCompletionTotalQuotation').val(newTelephoneTimeLog.completionTotalQuotation).trigger('change');
			$('#telephoneDeletionQuotationCount').val(newTelephoneTimeLog.deletionQuotationCount).trigger('change');
			$('#telephoneDeletionTotalQuotation').val(newTelephoneTimeLog.deletionTotalQuotation).trigger('change');

			$('input:radio[name=telephoneSession]').attr('checked',false);
			if (newTelephoneTimeLog.session) {
			    var $radios = $('input:radio[name=telephoneSession]');
			    if($radios.is(':checked') === false) {
			        $radios.filter('[value='+newTelephoneTimeLog.session+']').prop('checked', true);
			    }
			}
		}
		
		function setFieldworkFormValue(newFieldworkTimeLog){
			$('#fieldworkIndex').val(newFieldworkTimeLog.index);
			$('#fieldworkId').val(newFieldworkTimeLog.id);
			$('#fieldworkAssignmentId').val(newFieldworkTimeLog.assignmentId);
			$('#fieldworkReferenceExist').val(newFieldworkTimeLog.referenceExist);
			$('#fieldworkReferenceMonth').val(newFieldworkTimeLog.referenceMonth).trigger('change').datepicker('update');
			$('#fieldworkStartTime').timepicker('setTime', newFieldworkTimeLog.startTime);  
			$('#fieldworkSurvey').val(newFieldworkTimeLog.survey).trigger('change');
			if ( newFieldworkTimeLog.caseReferenceNo !== undefined)
				$("#fieldworkCaseReferenceNo").append($("<option><option/>").attr("value", newFieldworkTimeLog.caseReferenceNo).text(newFieldworkTimeLog.caseReferenceNo));
			$('#fieldworkCaseReferenceNo').val(newFieldworkTimeLog.caseReferenceNo).trigger('change');
			//console.log('newFieldworkTimeLog.caseReferenceNo: '+newFieldworkTimeLog.caseReferenceNo);
			$('#fieldworkActivity').val(newFieldworkTimeLog.activity).trigger('change');
			$('#fieldworkEnumerationOutcome').val(newFieldworkTimeLog.enumerationOutcome).trigger('change');
			$('#fieldworkMarketQuotationCount').val(newFieldworkTimeLog.marketQuotationCount).trigger('change');
			$('#fieldworkMarketTotalQuotation').val(newFieldworkTimeLog.marketTotalQuotation).trigger('change');
			$('#fieldworkNonMarketQuotationCount').val(newFieldworkTimeLog.nonMarketQuotationCount).trigger('change');
			$('#fieldworkNonMarketTotalQuotation').val(newFieldworkTimeLog.nonMarketTotalQuotation).trigger('change');
			$('#fieldworkBuilding').val(newFieldworkTimeLog.building);
			$('#fieldworkDestination').val(newFieldworkTimeLog.destination);
			
			var transportSplitted = ((newFieldworkTimeLog.transport  != undefined )?newFieldworkTimeLog.transport.split("#"):['']);
			var modeOfTransport = ((transportSplitted.length>=1)?transportSplitted[0]:'');
			var modeOfTransportExtra = ((transportSplitted.length>=2)?transportSplitted[1]:'');
			$('#fieldworkTransport').val(modeOfTransport).trigger('change');
			$('#fieldworkTransportExtra').val(modeOfTransportExtra).trigger('change');
			
			$('#fieldworkExpenses').val(newFieldworkTimeLog.expenses);
			$('#fieldworkRemark').val(newFieldworkTimeLog.remark);
			
			$("#fieldworkDestinationFrom").val(newFieldworkTimeLog.fromLocation);
	    	$("#fieldworkDestinationTo").val(newFieldworkTimeLog.toLocation);
	    	
	    	if (newFieldworkTimeLog.activity == "TR"){
		    	$("#fieldworkTransportForm").attr("checked",newFieldworkTimeLog.includeInTransportForm);
		    	$("#fieldworkTransit").attr("checked",newFieldworkTimeLog.transit);
	    	}
	    	else{
	    		$("#fieldworkTransportForm").removeAttr("checked");
	    		$("#fieldworkTransit").removeAttr("checked");
	    	}
			
		}
		
		function getResultById(results, id) {
			for (var i=0 ; i < results.length ; i++) {
				if (results[i].id == id)
					return results[i];
			}
			return null;
		}
		
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
				if (j < 2) {
					$('#'+surveyType[j]+'_C_TI_count').text(count[j][0]+' / '+count[j][1]);
				} else {
					$('#'+surveyType[j]+'_C_TI_count').text(count[j][0]);
				}
			}
		}
		
		function updateTelephoneCountMode() {
			if ($('#telephoneCompletionQuotationCount').val() != "" || $('#telephoneCompletionTotalQuotation').val() != "") {
				$('#telephoneDeletionQuotationCount,#telephoneDeletionTotalQuotation').attr('disabled','disabled');
				
			} else {
				$('#telephoneDeletionQuotationCount,#telephoneDeletionTotalQuotation').removeAttr('disabled');
			}
			
			if ($('#telephoneDeletionQuotationCount').val() != "" || $('#telephoneDeletionTotalQuotation').val() != "") {
				$('#telephoneCompletionQuotationCount,#telephoneCompletionTotalQuotation').attr('disabled','disabled');
			} else {
				$('#telephoneCompletionQuotationCount,#telephoneCompletionTotalQuotation').removeAttr('disabled');
			}
		}

		function updateFieldworkCountMode() {
			var opt = $( "#fieldworkActivity option:selected" ).val();
			if ($('#fieldworkMarketQuotationCount').val() != "" || $('#fieldworkMarketTotalQuotation').val() != "") {
				$('#fieldworkNonMarketQuotationCount,#fieldworkNonMarketTotalQuotation').attr('disabled','disabled');
				
			} else {
				if(!(opt == "OD" || opt=="TR" || opt=="LD")){
					$('#fieldworkNonMarketQuotationCount,#fieldworkNonMarketTotalQuotation').removeAttr('disabled');
				}
				//$('#fieldworkNonMarketQuotationCount,#fieldworkNonMarketTotalQuotation').removeAttr('disabled');
			}
			
			if ($('#fieldworkNonMarketQuotationCount').val() != "" || $('#fieldworkNonMarketTotalQuotation').val() != "") {
				$('#fieldworkMarketQuotationCount,#fieldworkMarketTotalQuotation').attr('disabled','disabled');
			} else {
				if(!(opt == "OD" || opt=="TR" || opt=="LD")){
					$('#fieldworkMarketQuotationCount,#fieldworkMarketTotalQuotation').removeAttr('disabled');
				}
				//$('#fieldworkMarketQuotationCount,#fieldworkMarketTotalQuotation').removeAttr('disabled');
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
		
		function calculateOT() {
			if ($("#fieldworkTable").find(".odd:first td:eq(1)").text() != "") {
				var otBefore = '00:00';	
				//console.log('$("#fieldworkTable").find(".odd:first td:eq(1)").text():' +  $("#fieldworkTable").find(".odd:first td:eq(1)").text());
				if ($("#fieldworkTable").find(".odd:first td:eq(1)").text() < $('#otherWorkingSessionFrom').val()) {
					otBefore = timeRounding(timeDiff($("#fieldworkTable").find(".odd:first td:eq(1)").text(), $('#otherWorkingSessionFrom').val()));
				}
				//console.log('otBefore:'+otBefore);
				
				var otAfter = '00:00';
				//console.log('$("#fieldworkTable").find("tr:last td:eq(1)").text():' + $("#fieldworkTable").find("tr:last td:eq(1)").text());
				if ($("#fieldworkTable").find("tr:last td:eq(4)").text() == "OD") {
					if ($("#fieldworkTable").find("tr:last td:eq(1)").text() > $('#otherWorkingSessionTo').val()) {
						 otAfter = timeRounding(timeDiff($('#otherWorkingSessionTo').val(), $("#fieldworkTable").find("tr:last td:eq(1)").text()));
					}
				}
				//console.log('otAfter:'+otAfter);
				
				otTotal = timeSum(otBefore, otAfter);
				$("#otClaimed").timepicker('setTime', otTotal);
			}
		}
		
		function timeDiff(time1, time2) {
			var ary1=time1.split(':'),ary2=time2.split(':');
			var minsdiff=parseInt(ary2[0],10)*60+parseInt(ary2[1],10)-parseInt(ary1[0],10)*60-parseInt(ary1[1],10);
			return(String(100+Math.floor(minsdiff/60)).substr(1)+':'+String(100+minsdiff%60).substr(1));
		}
		
		function timeSum(time1, time2) {
			var ary1=time1.split(':'),ary2=time2.split(':');
			var minssum=parseInt(ary2[0],10)*60+parseInt(ary2[1],10)+parseInt(ary1[0],10)*60+parseInt(ary1[1],10);
			return(String(100+Math.floor(minssum/60)).substr(1)+':'+String(100+minssum%60).substr(1));
		}
		
		function timeRounding(time) {
			var ary=time.split(':');
			var outMinute;
			
			if (parseInt(ary[1]) >=  45 ) {
				outMinute = '45';
			} else if (parseInt(ary[1]) >=  30 ) {
				outMinute = '30';
			} else if (parseInt(ary[1]) >=  15 ) {
				if(parseInt(ary[0])>0)
					outMinute = '15';
			} else 
				outMinute = '00';
			return ary[0] + ':' + outMinute;
		}
		
		var telephoneFormValidator = null;
		var fieldworkFormValidator = null;
		
		$(document).ready(function() {
							
			$("#isTrainingAM").on('change', function() {
				if ($(this).is(":checked")) {
					$('#isVLSLAM').attr('disabled', 'disabled');
					$('#isVLSLAM').attr('readonly', 'readonly');
				} else {
					$('#isVLSLAM').removeAttr("readonly");
					$('#isVLSLAM').removeAttr("disabled");
				}
			})
				
			$("#isVLSLAM").on('change', function() {
				if ($(this).is(":checked")) {
					$('#isTrainingAM').attr('disabled','disabled');
					$('#isTrainingAM').attr('readonly','readonly');
				} else {
					$('#isTrainingAM').removeAttr("disabled");
					$('#isTrainingAM').removeAttr("readonly");
				}
			})
			
			$("#isVLSLPM").on('change', function() {
				if ($(this).is(":checked")) {
					$('#isTrainingPM').attr('disabled','disabled');
					$('#isTrainingPM').attr('readonly','readonly');
				} else {
					$('#isTrainingPM').removeAttr("readonly");
					$('#isTrainingPM').removeAttr("disabled");
				}
			})
			
			$("#isTrainingPM").on('change', function() {
				if ($(this).is(":checked")) {
					$('#isVLSLPM').attr('disabled', 'disabled');
					$('#isVLSLPM').attr('readonly', 'readonly');
				} else {
					$('#isVLSLPM').removeAttr("readonly");
					$('#isVLSLPM').removeAttr("disabled");
				}
			});
		
			$("#telephoneModal").modal("hide");
			$("#fieldworkModal").modal("hide");
			$("#remarkModal").modal("hide");
				 
			telephoneTable = $('#telEnuTable').DataTable( {
				"rowId" : 'index',
				"bPaginate": false,
			    "bInfo": false,
				"buttons" : [],
			    "order": [[ 2, "asc" ]],
			    "ordering" : true,
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
	                        	
	                        },
	                        {
	                        	"data" : "index",
                            	"render" : function(data, type, full, meta){
                            		var html = "<a onclick='editTelephoneTimeLog("+full.index+")' class='table-btn'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a>";
                            		html += "&nbsp;<a onclick='removeTelephoneTimeLog("+full.index+")' class='table-btn'><span class='glyphicon glyphicon-remove' aria-hidden='true'></span></a>";
                            		return html;
                           		}
	                        }

						],
                "columnDefs": [
		                	{
		                        "targets": [2],
		                        "className" : "text-center",
		                        "orderable": true,
		                        "orderSequence": [ "asc" ],
		                        
		                    },	
		                	{
		                		"targets": "_all",
		                   		"className" : "text-center",
		                       	"orderable": false,
		                   	},
                           ],
                "createdRow" : function (row, data, index) {
                	telephoneCounter++;
       			}

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
	                        { "data": "destination",
	                        	"render" : function(data, type, full, meta){
                            		if (full.activity == "TR"){
                            			return "From: "+full.fromLocation + "<br />To: " + full.toLocation
                            		}
                            		else{
                            			return data;
                            		}
                           		}
	                       	},
	                        { "data": "transport" },
	                        { "data": "expenses" },
	                        { "data": "includeInTransportForm",
	                        	"render": function (data, type, full, meta){
	                        		return data? "Y" : "N";
	                        	}
	                        },
	                        { "data": "transit",
	                        	"render": function (data, type, full, meta){
	                        		return data? "Y" : "N";
	                        	}
	                        },
	                        
	                        { "data": "remark" },
	                        {
	                        	"data" : "index",
                            	"render" : function(data, type, full, meta){
                            		var html = "<a onclick='editFieldworkTimeLog("+full.index+")' class='table-btn'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a>";
                            		html += "&nbsp;<a onclick='removeFieldworkTimeLog("+full.index+")' class='table-btn'><span class='glyphicon glyphicon-remove' aria-hidden='true'></span></a>";
                            		return html;
                           		}
	                        }

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
                           ],
					"drawCallback" : function (){
						$('#fieldworkTable').DataTable().columns.adjust();
						$('#fieldworkTable').DataTable().responsive.recalc();
					},
					"createdRow" : function (row, data, index) {
						fieldworkCounter++;
					}
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
			
			//fieldworkTable.columns.adjust();
          	//fieldworkTable.responsive.recalc();
			
			$('.timepicker').timepicker({
				showInputs: false,
				showMeridian: false,
				defaultTime: false,
				minuteStep: 1
	        });
				
			var timeLogRecords = [];
			getTimeLogDate();
			$('.date-picker:not([readonly])').datepicker({
				autoclose: true,
				beforeShowDay: function(date) {
			    	for (i = 0; i < timeLogRecords.length; i++){
			    		var createdDate = parseDate(timeLogRecords[i]);
			    		if (createdDate.equals(date)){
			    			return {enabled: false};
			    		}
			    	}
			    }
			});
			
			function getTimeLogDate() {
				$.ajax({
					url: "<c:url value='/timeLogManagement/TimeLogMaintenance/queryTimeLogDate'/>",
					type: 'POST',
					async: false,
					data: { 
						userId: $("#userId").val()
					},
					success: function (response) {
						timeLogRecords = response;
					}
				});
			}
			
			$('.select2').select2();
			
			$('.select2ajax').select2ajax();
			
			$('.othertime').hide();
			
			$('#workingSessionId').on('change', function () {
				//console.log("workingSessionId:" + $(this).val() );
				if ($(this).val() == "" || $(this).val() == "0") {
					//$('#otherWorkingSessionFrom').val('');
					//$('#otherWorkingSessionTo').val('');
					$('.othertime').show();
					$('[name="isOtherWorkingSession"]').val(true);
				}
				else
				{
					var timeRange = $("#workingSessionId option[value='"+$(this).val()+"']").text()
					if (timeRange != "") {
						$('#otherWorkingSessionFrom').val(timeRange.split('-')[0].trim());
						$('#otherWorkingSessionTo').val(timeRange.split('-')[1].trim());
					}
					$('.othertime').hide();
					$('[name="isOtherWorkingSession"]').val(false);
					calculateOT();
				}
			})
		    
			$('#otherWorkingSessionTo').on('change', function() {
				calculateOT();
			})
			
			
		    $('#newTelephoneTimeLogForm').on('click', function () {
		    	resetTelephoneForm();
		    	$('#telephoneIndex').val(telephoneCounter);
		    	$("#telephoneAction").val('new');
		    	$('#telephoneDialogLabel').text('Add New Telephone Time Log');
		    	$("#telephoneModal").modal("show");			
		    });
		    
		    $('#newFieldworkTimeLogForm').on('click', function () {
		    	resetFieldworkForm();
		    	$('#fieldworkIndex').val(fieldworkCounter);
		    	$('#fieldworkAction').val('new');
		    	$('#fieldworkDialogLabel').text('Add New Fieldwork Time Log');
		    	$("#fieldworkModal").modal("show");			
		    });
			    
		    $('#addTelephoneTimeLog').on( 'click', function () {
		    	($("#telephoneForm").valid());
		    });

		    $('#addFieldworkTimeLog').on( 'click', function () {
		    	($("#fieldworkForm").valid());
		    });
		    
		    $('#telephoneSurvey').on('change', function(){
		    	$('#telephoneCaseReferenceNo').select2("val", "");
		    });
		    
		    $('#fieldworkSurvey').on('change', function () {
				var enumOutcome = [];
				$.ajax({
					type: 'POST',
					url: "<c:url value='/timeLogManagement/TimeLogMaintenance/queryEnumerationOutcome'/>",
					data: { survey: $(this).val() },
					async:false,
					success: function(result){
						//console.log(result);
						enumOutcome = result.split(',');
					}
				});
				$('#fieldworkCaseReferenceNo').select2("val", "");
				$('#fieldworkEnumerationOutcome').select2("val", "");
				$('#fieldworkEnumerationOutcome option:gt(0)').remove();
				$.each(enumOutcome, function(key,value) {
				  $('#fieldworkEnumerationOutcome').append($("<option></option>")
				     .attr("value", value).text(value));
				});
			})
			
		    //var telephoneSelect2result = [];
	
			$('#telephoneCaseReferenceNo').select2ajax({
			        tags: true,
			        ajax: {
					    data: function (params) {
					    	params.referenceMonth = $('#telephoneReferenceMonth').val();
					    	params.survey = $('#telephoneSurvey').val();
					    	params.userId = $("#userId").val();
					    	return params;
						},
			            url: "<c:url value='/timeLogManagement/TimeLogMaintenance/queryTelephoneReferenceNoSelect2'/>",
			            dataType: "json",
						method: 'GET'/*,
				        success: function (data) {
				        	console.log("results:"+JSON.stringify(data.results));
				        	telephoneSelect2result = data.results;
				        }*/
			   	  	},
			        createSearchChoice: function(term, data) { 
	                    if ($(data).filter(function() { return this.text.localeCompare(term)===0; }).length===0) {
	                    	return {id:term, text:term};
	                    }
			        },
			});
			$('#telephoneCaseReferenceNo').attr('disabled','disabled');
			
			$('#telephoneReferenceMonth').on("change", function(e) {
				if ($('#telephoneReferenceMonth').val() == "") {
					$('#telephoneCaseReferenceNo').attr('disabled','disabled');
				} else {
					$('#telephoneCaseReferenceNo').removeAttr('disabled');
				}
				//console.log('telephoneReferenceExist:'+$('#telephoneReferenceExist').val());
				if ($('#telephoneReferenceExist').val() == 'true') {
					$('#telephoneCaseReferenceNo').val('').trigger('change');
				}
			});
			
			
			$('#telephoneCaseReferenceNo').on("change", function(e) {
				
				$.get("<c:url value='/timeLogManagement/TimeLogMaintenance/getTelephoneReferenceNoDetail'/>",
					{referenceMonth: $('#telephoneReferenceMonth').val(), referenceNo: $(this).val()},
					function (data) {
						if (data != null && (data.count)) {
				        	$('#telephoneStatus').val(data.firmStatus);
				        	if (data.firmStatus == 1) {
				        		$('#telephoneDeletionQuotationCount').val('').trigger('change');
								$('#telephoneDeletionTotalQuotation').val('').trigger('change');
								$('#telephoneCompletionQuotationCount').val(data.count).trigger('change');
								$('#telephoneCompletionTotalQuotation').val(data.total).trigger('change');
				        	} else if (data.firmStatus == 2 || data.firmStatus == 3 || data.firmStatus == 4 ||
				        			data.firmStatus == 5 || data.firmStatus == 6 || data.firmStatus == 10 ) {
								$('#telephoneCompletionQuotationCount').val('').trigger('change');
								$('#telephoneCompletionTotalQuotation').val('').trigger('change');
				        		$('#telephoneDeletionQuotationCount').val(data.count).trigger('change');
								$('#telephoneDeletionTotalQuotation').val(data.total).trigger('change');			
				        	}
				        	$('#telephoneAssignmentId').val(data.assignmentId);
				        	$('#telephoneReferenceExist').val(true);
			        	} else {
			        		if ($('#telephoneReferenceExist').val()=='true') {
				        		$('#telephoneDeletionQuotationCount').val('').trigger('change');
								$('#telephoneDeletionTotalQuotation').val('').trigger('change');
								$('#telephoneCompletionQuotationCount').val('').trigger('change');
								$('#telephoneCompletionTotalQuotation').val('').trigger('change');
								$('#telephoneAssignmentId').val('');
								$('#telephoneReferenceExist').val(false);
			        		}
			        	}
			        }
				);
				/*
	        	var result = getResultById(telephoneSelect2result, $(this).val());
	        	console.log("result:"+JSON.stringify(result));
	        	if (result != null && (result.count)) {
		        	$('#telephoneStatus').val(result.firmStatus);
		        	if (result.firmStatus == 1) {
		        		$('#telephoneDeletionQuotationCount').val('').trigger('change');
						$('#telephoneDeletionTotalQuotation').val('').trigger('change');
						$('#telephoneCompletionQuotationCount').val(result.count).trigger('change');
						$('#telephoneCompletionTotalQuotation').val(result.total).trigger('change');
		        	} else if (result.firmStatus == 2 || result.firmStatus == 3 || result.firmStatus == 4 ||
		        			result.firmStatus == 5 || result.firmStatus == 6 || result.firmStatus == 10 ) {
						$('#telephoneCompletionQuotationCount').val('').trigger('change');
						$('#telephoneCompletionTotalQuotation').val('').trigger('change');
		        		$('#telephoneDeletionQuotationCount').val(result.count).trigger('change');
						$('#telephoneDeletionTotalQuotation').val(result.total).trigger('change');			
		        	}
		        	$('#telephoneAssignmentId').val(result.assignmentId);
		        	$('#telephoneReferenceExist').val(true);
	        	} else {
	        		if ($('#telephoneReferenceExist').val()=='true') {
		        		$('#telephoneDeletionQuotationCount').val('').trigger('change');
						$('#telephoneDeletionTotalQuotation').val('').trigger('change');
						$('#telephoneCompletionQuotationCount').val('').trigger('change');
						$('#telephoneCompletionTotalQuotation').val('').trigger('change');
						$('#telephoneAssignmentId').val('');
						$('#telephoneReferenceExist').val(false);
	        		}
	        	}*/
	        });
			
			$('#telephoneCompletionQuotationCount,#telephoneDeletionQuotationCount,#telephoneCompletionTotalQuotation,#telephoneDeletionTotalQuotation').on('change', function(e) {
				updateTelephoneCountMode();
			});
			
			
			 //var fieldworkSelect2result = [];
			
			$('#fieldworkCaseReferenceNo').select2ajax({
		        tags: true,
		        ajax: {
				    data: function (params) {
				    	params.referenceMonth = $('#fieldworkReferenceMonth').val();
				    	params.survey = $('#fieldworkSurvey').val();
				    	params.userId = $("#userId").val();
				    	return params;
					},
		            url: "<c:url value='/timeLogManagement/TimeLogMaintenance/queryFieldworkReferenceNoSelect2'/>",
		            dataType: "json",
					method: 'GET'/*,
			        success: function (data) {
			        	console.log("results:"+JSON.stringify(data.results));
			        	fieldworkSelect2result = data.results;
			        }*/
		   	  	},
		        createSearchChoice: function(term, data) { 
                    if ($(data).filter(function() { return this.text.localeCompare(term)===0; }).length===0) {
                    	$('#fieldworkReferenceExist').val(false);
                    	return {id:term, text:term};
                    }
		        },
		        results: function (data, page) {
		        	//console.log("selected:"+JSON.Stringify(data));
		            return { results: data };
		         }
			});
			
			$('#fieldworkCaseReferenceNo').attr('disabled','disabled');
			
			$('#fieldworkReferenceMonth').on("change", function(e) {
				if ($('#fieldworkReferenceMonth').val() == "") {
					$('#fieldworkCaseReferenceNo').val('');
					$('#fieldworkCaseReferenceNo').attr('disabled','disabled');
				} else {
					$('#fieldworkCaseReferenceNo').removeAttr('disabled');
				}
				//console.log('fieldworkReferenceExist:'+$('#fieldworkReferenceExist').val());
				if ($('#fieldworkReferenceExist').val() == 'true') {
					$('#fieldworkCaseReferenceNo').val('').trigger('change');
				}
			});
			
			$('#fieldworkCaseReferenceNo').on("change", function(e) {
				$.get("<c:url value='/timeLogManagement/TimeLogMaintenance/getFieldworkReferenceNoDetail'/>",
					{referenceMonth: $('#fieldworkReferenceMonth').val(), referenceNo: $(this).val()},
					function (data) {
						if (data != null && data != '') {
				        	$('#fieldworkRecordType').val(data.marketType);
			        		if (data.marketType == 1) {
				        		$('#fieldworkNonMarketQuotationCount').val('').trigger('change');
								$('#fieldworkNonMarketTotalQuotation').val('').trigger('change');
								$('#fieldworkMarketQuotationCount').val(data.count).trigger('change');
								$('#fieldworkMarketTotalQuotation').val(data.total).trigger('change');
				        	} else if (data.marketType == 2 ) {
								$('#fieldworkMarketQuotationCount').val('').trigger('change');
								$('#fieldworkMarketTotalQuotation').val('').trigger('change');
				        		$('#fieldworkNonMarketQuotationCount').val(data.count).trigger('change');
								$('#fieldworkNonMarketTotalQuotation').val(data.total).trigger('change');
				        	}
			        		//$('#fieldworkActivity').val('FI').trigger('change');
				        	//$('#fieldworkBuilding').val(data.building);
				        	$('#fieldworkDestination').val(data.address);
				        	$('#fieldworkAssignmentId').val(data.assignmentId);
				        	$('#fieldworkReferenceExist').val(true);
			        	} else {
			        		if ($('#fieldworkReferenceExist').val()=='true') {
				        		$('#fieldworkMarketQuotationCount').val('').trigger('change');
								$('#fieldworkMarketTotalQuotation').val('').trigger('change');
								$('#fieldworkNonMarketQuotationCount').val('').trigger('change');
								$('#fieldworkNonMarketTotalQuotation').val('').trigger('change');
								$('#fieldworkBuilding').val('').trigger('change');
								$('#fieldworkDestination').val('');
								$('#fieldworkAssignmentId').val('');
								$('#fieldworkReferenceExist').val(false);
			        		}
			        	}
					}
				);
				/*
	        	var result = getResultById(fieldworkSelect2result, $(this).val());
	        	console.log("result:"+JSON.stringify(result));
	        	if (result != null && (result.count)) {
		        	$('#fieldworkRecordType').val(result.marketType);
	        		if (result.marketType == 1) {
		        		$('#fieldworkNonMarketQuotationCount').val('').trigger('change');
						$('#fieldworkNonMarketTotalQuotation').val('').trigger('change');
						$('#fieldworkMarketQuotationCount').val(result.count).trigger('change');
						$('#fieldworkMarketTotalQuotation').val(result.total).trigger('change');
		        	} else if (result.marketType == 2 ) {
						$('#fieldworkMarketQuotationCount').val('').trigger('change');
						$('#fieldworkMarketTotalQuotation').val('').trigger('change');
		        		$('#fieldworkNonMarketQuotationCount').val(result.count).trigger('change');
						$('#fieldworkNonMarketTotalQuotation').val(result.total).trigger('change');
		        	}
	        		$('#fieldworkActivity').val('FI').trigger('change');
		        	$('#fieldworkBuilding').val(result.building);
		        	$('#fieldworkDestination').val(result.address);
		        	$('#fieldworkAssignmentId').val(result.assignmentId);
		        	$('#fieldworkReferenceExist').val(true);
	        	} else {
	        		if ($('#fieldworkReferenceExist').val()=='true') {
		        		$('#fieldworkMarketQuotationCount').val('').trigger('change');
						$('#fieldworkMarketTotalQuotation').val('').trigger('change');
						$('#fieldworkNonMarketQuotationCount').val('').trigger('change');
						$('#fieldworkNonMarketTotalQuotation').val('').trigger('change');
						$('#fieldworkBuilding').val('').trigger('change');
						$('#fieldworkDestination').val('');
						$('#fieldworkAssignmentId').val('');
						$('#fieldworkReferenceExist').val(false);
	        		}
	        	}*/
	        });
			
			$('#fieldworkMarketQuotationCount,#fieldworkMarketTotalQuotation,#fieldworkNonMarketQuotationCount,#fieldworkNonMarketTotalQuotation').on('change', function(e) {
				updateFieldworkCountMode();
			});
			
			$('#fieldworkActivity').on('change', function(){
				var opt = $(this).val();
				
				//$(this).parents('form:first').find('input,select').removeAttr("disabled");
				
				
				if (opt == "FI" || opt=="TR" || opt=="OT" || opt=="/"){		
					$('#fieldworkSurvey').removeAttr('disabled');
				}
				else{
					$('#fieldworkSurvey').attr('disabled', true);
					$('#fieldworkSurvey').val("").trigger('change')
				}
				
				if (opt == "FI" || opt == "OT" || opt == "/"){
					$('#fieldworkReferenceMonth').removeAttr('disabled');
					//$('#fieldworkCaseReferenceNo').removeAttr('disabled');
					$('#fieldworkEnumerationOutcome').removeAttr('disabled');
					$('#fieldworkBuilding').removeAttr('disabled');
					$('#fieldworkNonMarketQuotationCount').removeAttr('disabled');
					$('#fieldworkNonMarketTotalQuotation').removeAttr('disabled');
					$('#fieldworkMarketQuotationCount').removeAttr('disabled');
					$('#fieldworkMarketTotalQuotation').removeAttr('disabled');
				}
				else{
					$('#fieldworkReferenceMonth').attr('disabled', true);
					$('#fieldworkCaseReferenceNo').attr('disabled', true);
					$('#fieldworkEnumerationOutcome').attr('disabled', true);
					$('#fieldworkBuilding').attr('disabled', true);
					$('#fieldworkNonMarketQuotationCount').attr('disabled', true);
					$('#fieldworkNonMarketTotalQuotation').attr('disabled', true);
					$('#fieldworkMarketQuotationCount').attr('disabled', true);
					$('#fieldworkMarketTotalQuotation').attr('disabled', true);	
										
					$('#fieldworkReferenceMonth').val("");
					$('#fieldworkCaseReferenceNo').val("").trigger('change');
					$('#fieldworkEnumerationOutcome').val("").trigger('change');
					$('#fieldworkBuilding').val("");
					$('#fieldworkNonMarketQuotationCount').val("");
					$('#fieldworkNonMarketTotalQuotation').val("");
					$('#fieldworkMarketQuotationCount').val("");
					$('#fieldworkMarketTotalQuotation').val("");
				}
				
				if (opt != "TR"){
					$(".singleDest").show();
					$(".multipleDest").hide();
					$(".multipleDest input").each(function(){
						$(this).val("")
					})
					$(".travelForm").hide();
					$(".travelForm input:checkbox").each(function(){
						$(this).removeAttr("checked");
					})
					$('#fieldworkTransport').attr('disabled', true);
					$('#fieldworkTransportExtra').attr('disabled', true);
					$("[name=fieldworkExpenses]").attr('disabled', true);
					$('#fieldworkTransport').val('').trigger('change');
					$('#fieldworkTransportExtra').val('').trigger('change');
					$("[name=fieldworkExpenses]").val('');
				}
				else{
					$(".singleDest").hide();
					$(".singleDest input").each(function(){
						$(this).val("")
					})
					$(".multipleDest").show();					
					$(".travelForm").show();
					$('#fieldworkTransport').removeAttr('disabled');
					$('#fieldworkTransportExtra').removeAttr('disabled');
					$("[name=fieldworkExpenses]").removeAttr('disabled');
				}
				if (opt === "TR"){
					$('#fieldworkTransportForm').prop('checked', true);
				}
			})
			
			
			$("#submitBtn").on('click', function() {
				$("#status").val("Submitted");
				$('#mainForm').submit();
			});
			$("#saveBtn").on('click',function(){
				$("#status").val("Draft");
			})
			
			$('#submitRemarkBtn').on('click', function() {
				$('#itineraryCheckRemark').val($('#checkRemark').val());
				$('#mainForm').submit();
			})
			
			$('#remarkForm').submit(function(event){
				return false;
			});
			
			telephoneFormValidator =$("#telephoneForm").validate({			
				rules : {
					ignore: [],
					telephoneCompletionQuotationCount : { number : true },
					telephoneCompletionTotalQuotation : { number : true },
					telephoneDeletionQuotationCount : { number : true },
					telephoneDeletionTotalQuotation : { number : true },
				},
				submitHandler: function(form) {
		    		var newTelephoneTimeLog = {};
		        	
			    	newTelephoneTimeLog.index = $('#telephoneIndex').val();
			    	newTelephoneTimeLog.telephoneTimeLogId = $('#telephoneId').val();
			    	newTelephoneTimeLog.telephoneAssignmentId = $('#telephoneAssignmentId').val();
			    	newTelephoneTimeLog.referenceMonth = $('#telephoneReferenceMonth').val();
			    	newTelephoneTimeLog.survey = $('#telephoneSurvey').val();
			    	newTelephoneTimeLog.caseReferenceNo = $('#telephoneCaseReferenceNo').val();
			    	newTelephoneTimeLog.completionQuotationCount = $('#telephoneCompletionQuotationCount').val();
			    	newTelephoneTimeLog.completionTotalQuotation = $('#telephoneCompletionTotalQuotation').val();
			    	newTelephoneTimeLog.deletionQuotationCount = $('#telephoneDeletionQuotationCount').val();
			    	newTelephoneTimeLog.deletionTotalQuotation = $('#telephoneDeletionTotalQuotation').val();
			    	newTelephoneTimeLog.referenceExist = $('#telephoneReferenceExist').val();
			    	newTelephoneTimeLog.status = $('#telephoneStatus').val();
			    	newTelephoneTimeLog.session = $('input[name=telephoneSession]:checked').val();
			    	if ($('#telephoneAction').val() == 'edit') {
			    		removeTelephoneTimeLog(newTelephoneTimeLog.index);
			    	}
			    	telephoneTable.row.add(newTelephoneTimeLog).draw();	    	
			    	telephoneTimeLogs.push(newTelephoneTimeLog);
			    	
			    	calculateTelephoneCounts();
			    	$("#telephoneModal").modal("hide");

					$('#telephoneReferenceMonth').val("").datepicker("update");
					$('#telephoneReferenceMonth').datepicker('clearDates');
				}
			});
			
			fieldworkFormValidator = $("#fieldworkForm").validate({			
				rules : {
					ignore: "",
					fieldworkMarketQuotationCount : { number : true },
					fieldworkMarketTotalQuotation : { number : true },
					fieldworkNonMarketQuotationCount : { number : true },
					fieldworkNonMarketTotalQuotation : { number : true },
					fieldworkExpenses : { number : true },
					fieldworkBuilding : { number : true },
					
					fieldworkReferenceMonth: {
						required : {
							depends : function (element) { 
									return ($('#fieldworkActivity').val() == 'FI' && 
											$('#fieldworkCaseReferenceNo').val() != '' && $('#fieldworkCaseReferenceNo').val() != null); 
							}
						}
					},					
					fieldworkSurvey : { 
						required : {
							depends : function (element) { 
								return $('#fieldworkActivity').val() == 'FI' &&
									$('#fieldworkCaseReferenceNo').val() != '' && 
									$('#fieldworkCaseReferenceNo').val() != null
									|| $('#fieldworkActivity').val() == 'TR'
									|| $('#fieldworkActivity').val() == 'SD'; 
							}
						}
					},
					fieldworkEnumerationOutcome : { 
						required : {
							depends : function (element) { 
									return $('#fieldworkActivity').val() == 'FI' &&
									$('#fieldworkCaseReferenceNo').val() != '' && 
									$('#fieldworkCaseReferenceNo').val() != null; 
							}
						}
					},
					
					/*
					fieldworkDestinationFrom: {
						required:{
							depends : function (element) { 
								return $('#fieldworkActivity').val() == 'TR' ;
							}
						}
					},*/
					fieldworkDestinationTo: {
						required:{
							depends : function (element) { 
								return ($('#fieldworkActivity').val() == 'TR');
							}
						}
					},
					fieldworkTransport : { 
						required : {
							depends : function (element) { 
									return ($('#fieldworkActivity').val() == 'TR'); 
							}
						}
					},
					fieldworkTransportExtra : { 
						required : {
							depends : function (element) { 
									if($('#fieldworkActivity').val() == 'TR'){
										if($('#fieldworkTransport').find(':selected').text() == 'MTR'
												|| $('#fieldworkTransport').find(':selected').text() == 'OF'
												|| $('#fieldworkTransport').find(':selected').text() == 'PRM'
												|| $('#fieldworkTransport').find(':selected').text() == 'TRM'
												|| $('#fieldworkTransport').find(':selected').text() == 'AR'
												|| $('#fieldworkTransport').find(':selected').text() == 'LRT'
												|| $('#fieldworkTransport').find(':selected').text() == 'F'
												|| $('#fieldworkTransport').find(':selected').text() == 'LB'
												|| $('#fieldworkTransport').find(':selected').text() == 'TX'){
											return false;
										}
										return true;
									} else {
										return false; 
									}
							}
						}
					},
					fieldworkRemark : { 
						required : {
							depends : function (element) { 
									return ($('#fieldworkEnumerationOutcome').val() == 'O' || 
											$('#fieldworkTransport').val() == 'O' || 
											$('#fieldworkActivity').val() == 'OT' ||
											$('#fieldworkActivity').val() == 'FI' && 
											($('#fieldworkCaseReferenceNo').val() == '' || $('#fieldworkCaseReferenceNo').val() == null))  ; 
							}
						}
					},
				},
				submitHandler: function(form) {
					var newFieldworkTimeLog = {};			    	
			    	newFieldworkTimeLog.index = $('#fieldworkIndex').val();
			    	newFieldworkTimeLog.id = $('#fieldworkId').val();
			    	newFieldworkTimeLog.assignmentId = $('#fieldworkAssignmentId').val();
			    	newFieldworkTimeLog.referenceMonth = $('#fieldworkReferenceMonth').val();
			    	newFieldworkTimeLog.startTime = $('#fieldworkStartTime').val();
			    	newFieldworkTimeLog.survey = $('#fieldworkSurvey').val();
			    	newFieldworkTimeLog.caseReferenceNo = $('#fieldworkCaseReferenceNo').val();
			    	newFieldworkTimeLog.activity = $('#fieldworkActivity').val();
			    	newFieldworkTimeLog.enumerationOutcome = $('#fieldworkEnumerationOutcome').val();
			    	newFieldworkTimeLog.marketQuotationCount = $('#fieldworkMarketQuotationCount').val();
			    	newFieldworkTimeLog.marketTotalQuotation = $('#fieldworkMarketTotalQuotation').val();
			    	newFieldworkTimeLog.nonMarketQuotationCount = $('#fieldworkNonMarketQuotationCount').val();
			    	newFieldworkTimeLog.nonMarketTotalQuotation = $('#fieldworkNonMarketTotalQuotation').val();
			    	newFieldworkTimeLog.building = $('#fieldworkBuilding').val();
			    	if (newFieldworkTimeLog.activity != "TR"){
			    		newFieldworkTimeLog.destination = $('#fieldworkDestination').val();
			    	}
			    	newFieldworkTimeLog.transport = $('#fieldworkTransport').val() + (($.trim($('#fieldworkTransportExtra').val()).length > 0)?('#'+$('#fieldworkTransportExtra').val()):'');
			    	newFieldworkTimeLog.expenses = $('#fieldworkExpenses').val();
			    	newFieldworkTimeLog.remark = $('#fieldworkRemark').val();
			    	newFieldworkTimeLog.referenceExist = $('#fieldworkReferenceExist').val();
			    	//newFieldworkTimeLog.recordType = $('#fieldworkRecordType').val();
			    	
			    	newFieldworkTimeLog.fromLocation = $("#fieldworkDestinationFrom").val();
			    	newFieldworkTimeLog.toLocation = $("#fieldworkDestinationTo").val();
			    	
			    	if (newFieldworkTimeLog.activity == "TR"){
				    	newFieldworkTimeLog.includeInTransportForm = $("#fieldworkTransportForm").is(":checked");
				    	newFieldworkTimeLog.transit = $("#fieldworkTransit").is(":checked");
			    	}
			    	else{
			    		newFieldworkTimeLog.includeInTransportForm = false;
			    		newFieldworkTimeLog.transit = false;
			    	}
			    	
			    	
			    	//if ($('#fieldworkRecordType').val() == "") {
			    		if (newFieldworkTimeLog.marketQuotationCount != "" || newFieldworkTimeLog.marketTotalQuotation != "") {
			    			$('#fieldworkRecordType').val('1');
			    		} else if (newFieldworkTimeLog.nonMarketQuotationCount != "" || newFieldworkTimeLog.nonMarketTotalQuotation != "") {
			    			$('#fieldworkRecordType').val('2');
			    		} else if (newFieldworkTimeLog.building != "") {
			    			$('#fieldworkRecordType').val('3');
			    		}
			    		newFieldworkTimeLog.recordType = $('#fieldworkRecordType').val();
			    	//}
			    	
			    	if ($('#fieldworkAction').val() == 'edit') {
			    		removeFieldworkTimeLog(newFieldworkTimeLog.index);
			    	}
			    	
			    	fieldworkTable.row.add(newFieldworkTimeLog).draw();		    	
			    	fieldworkTimeLogs.push(newFieldworkTimeLog);
			    	
			    	$("#fieldworkModal").modal("hide");
			    	
			    	calculateFieldworkCounts();
			    	
			    	//console.log("newFieldworkTimeLog.activity: " + newFieldworkTimeLog.activity)
			    	if (newFieldworkTimeLog.activity == 'OD' || newFieldworkTimeLog.startTime < $('#otherWorkingSessionFrom').val()) {
			    		calculateOT();
			    	}
			    	return;
				}
			});
			
			$("#mainForm").validate({
				rules : {
					ignore: [],  
					otClaimed : { 
						required : {
							depends : function (element) { 
								return $('[name="isClaimOT"]').is(":checked"); 
							}
						}
					},
					otherWorkingSessionFrom : { required : function() { $('[name="isOtherWorkingSession"]').val() == 'true' } },
					otherWorkingSessionTo : { required : function() { $('[name="isOtherWorkingSession"]').val() == 'true' } },
				},

				submitHandler: function(form) {
					if ($('#status').val() == 'Submitted') {
						
						/*if (($("#fieldworkTable").find("tr:last td:eq(4)").text() != "OD")) {
							bootbox.alert({
								title: "Alert",
								message: "<spring:message code='E00137' />"
							});
							return false;
						}*/
																							
						var cnt = _.countBy(fieldworkTimeLogs, function (item){
							return item.activity == "OD" ? "od" : "other"
						});							
						if (cnt.od > 1){
							// There can only be at most one OD on a day (either 0 or 1). 
							bootbox.alert({
								title: "Alert",
								message: "<spring:message code='E00142' />"
							});
							return false;
						}
						else if (cnt.od == 1){
							//  OD record, if any, must be at the last row.
							if (($("#fieldworkTable").find("tr:last td:eq(4)").text() != "OD")) {
								bootbox.alert({
									title: "Alert",
									message: "<spring:message code='E00137' />"
								});
								return false;
							}
						}
						
						var enCnt = _.countBy(fieldworkTimeLogs, function (item){
							if (item.caseReferenceNo != "" && item.caseReferenceNo != null){
								if (item.enumerationOutcome == "C"){
									return item.caseReferenceNo;
								}
							}
							return "others";
						});
						for (var key in enCnt){
							if (key != "others" && enCnt[key] > 1){
								//Only one row have the enumeration outcome = "C" for each reference no.
								bootbox.alert({
									title: "Alert",
									message: "<spring:message code='E00143' />"
								});
								return false;
							}
						}
						
						// Mandatory "Destination From" for first TR on that day
						for (i = 0; i < fieldworkTimeLogs.length; i++){
							if (fieldworkTimeLogs[i].activity == "TR"){
								if (fieldworkTimeLogs[i].fromLocation == "" || fieldworkTimeLogs[i].fromLocation == null){
									bootbox.alert({
										title: "Alert",
										message: "<spring:message code='E00141' />"
									});									
									return false;
								}
								break;
							}
						}
						
						// Provide warning if reference no. entered is not included in the itinerary
						var referenceNo = _.uniq(_.filter(_.map(fieldworkTimeLogs, function (item){
							return item.caseReferenceNo
						}), function (item){
							return item != "" && item != null;
						}))
						$.ajax({
							type: 'POST',
							url: "<c:url value='/timeLogManagement/TimeLogMaintenance/checkItineraryReferenceNo'/>",
							data: {
								referenceNo : referenceNo,
								userId: $("#userId").val(),
								date: $("#date").val()
							},
							success: function(result){
								if (!result){
									bootbox.confirm("<spring:message code='E00144'/>", function(ret) {
										  if (ret){
											  itineraryCheck();
											  submitForm(form);
										  }
									}); 
								}
								else{
									itineraryCheck(form);
									submitForm(form);
								}
							},
							error: function(){
								itineraryCheck(form);
								submitForm(form);
							}
						})
					}
					else{
						submitForm(form);
					}
				}
			});
			
			function itineraryCheck(form){
				$.ajax({
					type: 'POST',
					url: "<c:url value='/timeLogManagement/TimeLogMaintenance/validate'/>",
					data: {
						userId: $('#userId').val(),
						date: $('#date').val(),
						caseReferenceNo : function() {
							var caseReferences = [];
							var data = fieldworkTable.rows().data()
							for (var i=0 ; i < data.length ; i++) {
								if (data[i].caseReferenceNo != null && data[i].caseReferenceNo.lastIndexOf("NR-", 0) !== 0) {
									caseReferences.push(data[i].caseReferenceNo);
								}
							}
							return caseReferences;
						}
					},
					async:false,
					success: function(result){
						//console.log('validation result: ' + JSON.stringify(result));
						if (result.valid == true) {
							$('#status').val('Submitted');
						} else {
							$('#status').val('Voilated');
						}
						$('#assignmentDeviation').val(result.assignmentDeviation);
						$('#sequenceDeviation').val(result.sequenceDeviation);
						$('#tpuDeviation').val(result.tpuDeviation);
						deviationStatus = result.status;
					},
					error: function(){
						$('#status').val('Voilated');
					}
				});
			}
			
			
			function submitForm(form){
				if($('[name="isClaimOT"]').is(":checked") && $('#status').val() == 'Submitted'){
					$('#status').val('Voilated');
					deviationStatus = "OT";
				}
			
				<sec:authorize access="hasPermission(#user, 4)" >
					if(${userDetail.userId} == ${model.userId} && $('#status').val() == 'Voilated'){
						$('#status').val('Submitted');
					}
    	  		 </sec:authorize>
				
				if ($('#status').val() == 'Submitted' || $('#status').val() == 'Draft' || $('#itineraryCheckRemark').val() != '' ) {
					  
					$('#hiddenData').empty();
					for ( var i=0 ; i <= telephoneTimeLogs.length ; i++) {
						for ( var key in telephoneTimeLogs[i] ) {
		                	$('#hiddenData').append($("<input type='hidden' />")
		                		.attr("name","telephoneTimeLogs["+i+"]."+key).attr("value",telephoneTimeLogs[i][key]));
						}
					}
					
					for ( var i=0 ; i <= fieldworkTimeLogs.length ; i++) {
						for ( var key in fieldworkTimeLogs[i] ) {
		                	$('#hiddenData').append($("<input type='hidden' />")
		                		.attr("name","fieldworkTimeLogs["+i+"]."+key).attr("value",fieldworkTimeLogs[i][key]));
						}
					}
					form.submit();
				} else if($('#itineraryCheckRemark').val()==''){
					console.log("1. deviationStatus: "+deviationStatus);
					var remarkText = " Remark:";
					if(deviationStatus.substring(deviationStatus.length-remarkText.length) != remarkText){
						deviationStatus = deviationStatus + remarkText;
					}
					$('#deviationStatus').html(deviationStatus);
					$("#remarkModal").modal("show");
					$('#checkRemark').val($('#oldItineraryCheckRemark').val());
				} else {
					console.log("2. deviationStatus: "+deviationStatus);
					deviationStatus = deviationStatus + " Remark:";
					$('#deviationStatus').html(deviationStatus);
					$("#remarkModal").modal("show");
					$('#checkRemark').val($('#oldItineraryCheckRemark').val());
				}
			}
			
			

			<c:if test="${act == 'edit'}">
			
				$("#userId").append($("<option><option/>").attr("value", "<c:out value="${model.userId}"/>").text("<c:out value="${model.userCode}"/>"));
				$("#userId").val("<c:out value="${model.userId}"/>").trigger("change");
						
				$("#otherWorkingSessionFrom").val("<c:out value="${model.otherWorkingSessionFrom}"/>");
				$("#otherWorkingSessionTo").val("<c:out value="${model.otherWorkingSessionTo}"/>");
				$("#workingSessionId").val("<c:out value="${model.workingSessionId}"/>").trigger("change");
				
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
							telephoneTimeLogId : "<c:out value="${ti.telephoneTimeLogId}" />",
							assignmentId : "<c:out value="${ti.assignmentId}" />",
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
	     						fieldworkTimeLogId : "<c:out value="${fi.fieldworkTimeLogId}" />",
	     						assignmentId : "<c:out value="${fi.assignmentId}" />",
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
	     						fromLocation: "<c:out value='${fi.fromLocation}' />",
	     						toLocation: "<c:out value='${fi.fromLocation}' />",
	     						includeInTransportForm: <c:out value='${fi.includeInTransportForm}' />,
	     						transit: <c:out value='${fi.transit}' />
	     					},
	     				</c:forEach>
	     			];
	     			--%>
	
					fieldworkTable.rows.add(fieldworkTimeLogs).draw();
					calculateFieldworkCounts();
				</c:if>
			});
		</script>	
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
        	<h1>Time Log Maintenance</h1>
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
        	<form id="mainForm" action="<c:url value='/timeLogManagement/TimeLogMaintenance/save?act='/>${act}" method="post" role="form" >
        		<input name="timeLogId" value="<c:out value="${model.timeLogId}" />" type="hidden" >
        		<input name="assignmentDeviation" id="assignmentDeviation" value="<c:out value="${model.assignmentDeviation}" />" type="hidden" >
        		<input name="sequenceDeviation" id="sequenceDeviation" value="<c:out value="${model.sequenceDeviation}" />" type="hidden" >
        		<input name="tpuDeviation" id="tpuDeviation" value="<c:out value="${model.tpuDeviation}" />" type="hidden" >
        		<input name="oldItineraryCheckRemark" id="oldItineraryCheckRemark" value="<c:out value="${model.itineraryCheckRemark}" />" type="hidden" >
        		<input name="itineraryCheckRemark" id="itineraryCheckRemark" type="hidden" >
   				<input name="status" id="status" value="<c:out value="${model.status}" />" type="hidden">
				<input name="isPreApproval" id="isPreApproval" value="false" type="hidden">
				<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
								<div class="row reject-container">
									<div class="col-md-1 reject-btn">
										<a class="btn btn-default" href="<c:url value='/timeLogManagement/TimeLogMaintenance/home'/>">Back</a>
									</div>
									<c:if test="${model.status == 'Rejected'}">
										<label class="col-md-1 reject-label">Reject Reason:</label>
										<div class="col-md-9">
											<c:out value="${model.rejectReason}" escapeXml="false"/>
										</div>
									</c:if>
								</div>
								<div class="row reject-container">
									<c:if test="${model.status == 'Rejected'}">
										<label class="col-md-1 col-md-offset-1 reject-label">Rejected By:</label>
										<div class="col-md-9">
											<c:out value="${model.approvedBy}" escapeXml="false"/>
										</div>
									</c:if>
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
       							    		<label class="col-md-2 control-label">Training:</label>
	       							    	<div class="checkbox col-md-1">
											  <label><input id="isTrainingAM" name="isTrainingAM" type="checkbox" value="true" <c:if test="${model.isTrainingAM}">checked</c:if>>AM</label>
											</div>
											&nbsp;
											<div class="checkbox col-md-1">
											  <label><input id="isTrainingPM" name="isTrainingPM" type="checkbox" value="true" <c:if test="${model.isTrainingPM}">checked</c:if>>PM</label>
											</div>
	       							    </div>
	       							    <div class="form-group">
       							    		<label class="col-md-2 control-label">VL/SL:</label>
	       							    	<div class="checkbox col-md-1">
											  <label><input id="isVLSLAM" name="isVLSLAM" type="checkbox" value="true" <c:if test="${model.isVLSLAM}">checked</c:if>>AM</label>
											</div>
											&nbsp;
											<div class="checkbox col-md-1">
											  <label><input id="isVLSLPM" name="isVLSLPM" type="checkbox" value="true" <c:if test="${model.isVLSLPM}">checked</c:if>>PM</label>
											</div>
	       							    </div>
	       							    <div class="form-group">
	       							    	<label class="col-md-2 control-label">Field Officer:</label>
		       							    <div class="col-md-2">
		       							    	<input type="hidden" name="userId" id="userId" value="<c:out value="${model.userId}" />">
	       							    		<p class="form-control-static"><c:out value="${model.userCode}" /></p>
	       							    	</div>
	       							    </div>
	       							    <div class="form-group">
       							    		<label class="col-md-2 control-label">Date:</label>
	       							   		<div class="col-md-2">
	       							   			<div class="input-group">
													<input type="text" class="form-control date-picker" data-date-end-date="dateToday"
<%-- 													    <c:if test="${act == 'edit'}">readonly</c:if> --%>
														id="date" name="date" size="10" value="<c:out value="${model.date}" />" required />
													<div class="input-group-addon">
														<i class="fa fa-calendar"></i>
													</div>
												</div>
											</div>
	       							    </div>
	       							    <div class="form-group">
	       							    	<label class="col-md-2 control-label">Working Sessions:</label>
	       							    	<div class="col-md-2">
												<select class="select2" id="workingSessionId" name="workingSessionId" required>
												    <option></option>
													<c:forEach var="workingSessionSetting" items="${workingSessionSettings}">
														<option value="<c:out value='${workingSessionSetting.id}' />" <c:if test='${workingSessionSetting.id} ==  ${model.workingSessionId}'>selected="selected"</c:if>><c:out value="${commonService.formatTime(workingSessionSetting.fromTime)} - ${commonService.formatTime(workingSessionSetting.toTime)}"/></option>
													</c:forEach>
													<option value='0'>Other</option>
												</select>
												<input type='hidden' name='isOtherWorkingSession' value='false'>
											</div>
											<div class="col-md-2">
								                <div class='input-group bootstrap-timepicker othertime' id='sessionStartTime'>
								                    <input id='otherWorkingSessionFrom' name='otherWorkingSessionFrom' type='text' class="form-control timepicker" value="${model.otherWorkingSessionFrom}" />
								                    <span class="input-group-addon">
								                        <span class="glyphicon glyphicon-time"></span>
								                    </span>
								                </div>
							                </div>
							                <div class="col-md-1 othertime">
							                	<p class="form-control-static text-center">to</p>
							                </div>
											<div class="col-md-2 othertime">
								                <div class='input-group bootstrap-timepicker' id='sessionEndTime'>
								                    <input id='otherWorkingSessionTo' name='otherWorkingSessionTo' type='text' class="form-control timepicker" value="${model.otherWorkingSessionTo}" />
								                    <span class="input-group-addon">
								                        <span class="glyphicon glyphicon-time"></span>
								                    </span>
								                </div>
							                </div>
	       							    </div>
	       							    <div class="form-group">
       							    		<label class="col-md-2 control-label">OT claimed:</label>
       							    		<div class="col-md-2">
	       							           <div class='input-group bootstrap-timepicker'>
								                    <input id='otClaimed' name='otClaimed' type='text' class="form-control timepicker" value="<c:out value='${model.otClaimed}'/>"  />
								                    <span class="input-group-addon">
								                        <span class="glyphicon glyphicon-time"></span>
								                    </span>
								                </div>
	       							    	</div>
	       							    	<div class="checkbox col-md-2">
											  <label><input name='isClaimOT' type="checkbox" value="true" <c:if test="${model.isClaimOT}">checked</c:if>>Claim OT</label>
											</div>
       							    	</div>
       							    	<div class="form-group">
       							    		<label class="col-md-2 control-label">Timeoff taken:</label>
       							    		<div class="col-md-2">
	       							           <div class='input-group bootstrap-timepicker' id='sessionEndTime'>
								                    <input id='timeoffTaken' name='timeoffTaken' type='text' class="form-control timepicker" value="<c:out value='${model.timeoffTaken}'/>" />
								                    <span class="input-group-addon">
								                        <span class="glyphicon glyphicon-time"></span>
								                    </span>
								                </div>
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
										                <th data-priority="1"></th>
										            </tr>
										     	</thead>
										     	<tbody>	
										     	</tbody>
										     </table>
										     <button type="button" class="btn btn-default" id="newTelephoneTimeLogForm">Add</button>
										     </div>
										     </div>
		       							</div>
		       							<div class="form-group">
			       							<label class="col-sm-2 control-label">Fieldwork Activities:</label>
			       							<div class="col-sm-10">&nbsp;</div>
			       							<div class="col-sm-12">
				       								<table id="fieldworkTable" class="table nowrap responsive" style="width:100%">
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
												                <th>Destination</th>
												                <th>Mode of Transport</th>
												                <th>Expense<br>HK$</th>
												                <th>Include in Transport Form</th>
												                <th>Transit</th>
												                <th>Remark</th>
												                <th data-priority="1"></th>
												            </tr>
												     	</thead>
												     	<tbody>
												     	</tbody>
												     </table>
												     <button type="button" class="btn btn-default" id="newFieldworkTimeLogForm">Add</button>
												
											</div>
										</div>
										<div id="hiddenData">
										</div>
									</div>
								</div>
							</div>
							<sec:authorize  access="hasPermission(#user, 16) or hasPermission(#user, 4)">
								<c:if test="${model.status != 'Submitted' && model.status != 'Approved' && model.status != 'Voilated'}">
									<div class="box-footer">
		        						<button id="saveBtn" type="submit" class="btn btn-info">Save</button>
		        						<button id="submitBtn" type="button" class="btn btn-info">Submit</button>       						
		       						</div>
		       					</c:if>
       						</sec:authorize>
       						<sec:authorize  access="hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4)">
								<c:if test="${model.status == 'Submitted' || model.status == 'Approved'}">
									<div class="box-footer">
		        						<button id="submitBtn" type="button" class="btn btn-info">Submit</button>       						
		       						</div>
		       					</c:if>
       						</sec:authorize>
						</div>
	        		</div>
	        	</div>
        	</form>
      		<!-- Add new Telephone Time Log Dialog -->
			<div class="modal fade" id="telephoneModal" tabindex="-1" role="dialog" aria-labelledby="dialogLabel" aria-hidden="true">
			  <form id="telephoneForm">
			  <div class="modal-dialog">
				<div class="modal-content">
					<div  class="clearfix overlay" data-bind="loading:isLoading, visible:isLoading" ></div>
					  <div class="modal-header">
						<h4 class="modal-title" id="telephoneDialogLabel" data-bind="text:formTitle">Add New Telephone Time Log</h4>
						<input type="hidden" id="telephoneAction">
						<input type="hidden" id="telephoneIndex">
						<input type="hidden" id="telephoneId">
						<input type="hidden" id="telephoneStatus">
						<input type="hidden" id="telephoneAssignmentId">
						<input type="hidden" id="telephoneReferenceExist" value="false">
					  </div>
					  	<div class="modal-body form-horizontal">
							<div class="form-group">
								<div class="col-md-3 control-label">Reference Month:</div>
								<div class="col-md-7">
									<div class="input-group">
										<input type="text" class="form-control date-picker" maxlength="7" data-date-min-view-mode="months" data-date-format="mm-yyyy"
										id="telephoneReferenceMonth" name="telephoneReferenceMonth" required />
										<div class="input-group-addon">
											<i class="fa fa-calendar"></i>
										</div>
									</div>
								</div>
							</div>
							<div class="form-group">
								<div class="col-md-3 control-label">Survey:</div>
								<div class="col-md-7">
									<select class="select2" id="telephoneSurvey" name="telephoneSurvey" required >
										<option value=""></option>
										<c:forEach var="survey" items="${surveyList}">
											<option value="<c:out value="${survey}" />">${survey}</option>
										</c:forEach>
									</select>
								</div>
							</div>	
							<div class="form-group">
								<div class="col-md-3 control-label">Case Reference No.:</div>
								<div class="col-md-7">
									<select class="form-control" id="telephoneCaseReferenceNo" name="telephoneCaseReferenceNo" ></select>
								</div>
							</div>				
							<div class="form-group">
								<div class="col-md-3 control-label">Completion:</div>
								<div class="col-md-2">
									<input id="telephoneCompletionQuotationCount" name="telephoneCompletionQuotationCount" type="text" class="form-control" /> 
								</div>
								<div class="col-md-1 control-label text-center">
									/ 
								</div>
								<div class="col-md-2">
									<input id="telephoneCompletionTotalQuotation" name="telephoneCompletionTotalQuotation" type="text" class="form-control" /> 
								</div>
							</div>	
							<div class="form-group">
								<div class="col-md-3 control-label">Deletion:</div>
								<div class="col-md-2">
									<input id="telephoneDeletionQuotationCount" name="telephoneDeletionQuotationCount" type="text" class="form-control" /> 
								</div>
								<div class="col-md-1 control-label text-center">
									/ 
								</div>
								<div class="col-md-2">
									<input id="telephoneDeletionTotalQuotation" name="telephoneDeletionTotalQuotation" type="text" class="form-control" /> 
								</div>
							</div>
							<div class="form-group">
								<div class="col-md-3 control-label">Session:</div>
								<div class="col-md-7">
									<div class="radio"><label><input type="radio" id="telephoneSession" name="telephoneSession" value="A" class="optradio"/>AM</label></div>
		                			<div class="radio"><label><input type="radio" id="telephoneSession" name="telephoneSession" value="P" class="optradio"/>PM</label></div>
								</div>
							</div>	
							<div style="margin-top: 30px;" class="col-md-3 control-label"></div>
							<div style="margin-top: 30px;" class="col-md-2">
								<button type="submit" class="btn btn-primary" data-loading-text="Loading..." id="addTelephoneTimeLog">Submit</button>
							</div>
							<div style="margin-top: 30px;" class="col-md-5">
								<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
							</div>
						</div>
					  </div>
				</div>
				</form>
			  </div>	        	
      		<!-- Add new Fieldwork Time Log Dialog -->
			<div class="modal fade" id="fieldworkModal" tabindex="-1" role="dialog" aria-labelledby="dialogLabel" aria-hidden="true">
			  <form id="fieldworkForm">
			  <div class="modal-dialog">
				<div class="modal-content">
					<div  class="clearfix overlay" data-bind="loading:isLoading, visible:isLoading" ></div>
					  <div class="modal-header">
						<h4 class="modal-title" id="fieldworkDialogLabel" data-bind="text:formTitle">Add New Fieldwork Time Log</h4>
						<input type="hidden" id="fieldworkAction">
						<input type="hidden" id="fieldworkIndex">
						<input type="hidden" id="fieldworkId">
						<input type="hidden" id="fieldworkRecordType">
						<input type="hidden" id="fieldworkAssignmentId">
						<input type="hidden" id="fieldworkReferenceExist" value="false">
					  </div>
					  	<div class="modal-body form-horizontal">
					  		<div class="form-group">
								<div class="col-md-3 control-label">Activity:</div>
								<div class="col-md-7">
									<select class="select2" id="fieldworkActivity" name="fieldworkTimeLogModel.activity" required>
										<option value=""></option>
										<c:forEach var="activity" items="${activityList}">
											<option value="<c:out value="${activity}" />" ><c:out value="${activity}" /></option>
										</c:forEach>
									</select>
								</div>
							</div>
							<div class="form-group">
								<div class="col-md-3 control-label">Start Time:</div>
								<div class="col-md-7">
									<div class='input-group bootstrap-timepicker'>
					                    <input id='fieldworkStartTime' name="fieldworkStartTime" type='text' size="20" class="form-control timepicker" data-orientation="top" required/>
					                    <span class="input-group-addon">
					                        <span class="glyphicon glyphicon-time"></span>
					                    </span>
					                </div>
								</div>
							</div>
							<div class="form-group">
								<div class="col-md-3 control-label">Reference Month:</div>
								<div class="col-md-7">
									<div class="input-group">
										<input type="text" class="form-control date-picker" data-orientation="top" data-date-min-view-mode="months" data-date-format="mm-yyyy"
										id="fieldworkReferenceMonth" name="fieldworkReferenceMonth" size="10" />
										<div class="input-group-addon">
											<i class="fa fa-calendar"></i>
										</div>
									</div>
								</div>
							</div>
							<div class="form-group">
								<div class="col-md-3 control-label">Survey:</div>
								<div class="col-md-7">
									<select class="select2" id="fieldworkSurvey" name="fieldworkSurvey" >
										<option value=""></option>
										<c:forEach var="survey" items="${surveyList}">
											<option value="<c:out value="${survey}" />">${survey}</option>
										</c:forEach>
									</select>
								</div>
							</div>	
							<div class="form-group">
								<div class="col-md-3 control-label">Case Reference No.:</div>
								<div class="col-md-7">
									<select class="form-control select2" id="fieldworkCaseReferenceNo" name="fieldworkCaseReferenceNo" ></select>
								</div>
							</div>	
							<div class="form-group">
								<div class="col-md-3 control-label">Enumeration Outcome:</div>
								<div class="col-md-7">
									<select class="select2" id="fieldworkEnumerationOutcome" name="fieldworkEnumerationOutcome" >
										<option></option>
									</select>
								</div>
							</div>				
							<div class="form-group">
								<div class="col-md-3 control-label">Market:</div>
								<div class="col-md-2">
									<input id="fieldworkMarketQuotationCount" name="fieldworkMarketQuotationCount" type="text" class="form-control" /> 
								</div>
								<div class="col-md-1 control-label text-center">
									/ 
								</div>
								<div class="col-md-2">
									<input id="fieldworkMarketTotalQuotation" name="fieldworkMarketTotalQuotation" type="text" class="form-control" /> 
								</div>
							</div>	
							<div class="form-group">
								<div class="col-md-3 control-label">Non-Market:</div>
								<div class="col-md-2">
									<input id="fieldworkNonMarketQuotationCount" name="fieldworkNonMarketQuotationCount" type="text" class="form-control" /> 
								</div>
								<div class="col-md-1 control-label text-center">
									/ 
								</div>
								<div class="col-md-2">
									<input id="fieldworkNonMarketTotalQuotation" name="fieldworkNonMarketTotalQuotation" type="text" class="form-control" /> 
								</div>
							</div>
							<div class="form-group">
								<div class="col-md-3 control-label">Building:</div>
								<div class="col-md-7">
									<input id="fieldworkBuilding" name="fieldworkBuilding" type="text" class="form-control" />
								</div>
							</div>
							<div class="form-group">
								<div class="col-md-3 control-label">Destination:</div>
								<div class="col-md-7 singleDest">
									<input id="fieldworkDestination" name="fieldworkDestination" type="text" class="form-control" />
								</div>
								<div class="col-md-7 multipleDest" style="padding:0px">
									<div class="col-md-12">
										<input id="fieldworkDestinationFrom" name="fieldworkDestinationFrom" placeholder="From" type="text" class="form-control" />
									</div>
									<div class="col-md-12">
										<input id="fieldworkDestinationTo" name="fieldworkDestinationTo" placeholder="To"  type="text" class="form-control" />
									</div>
								</div>								
							</div>
							<div class="form-group">
								<div class="col-md-3 control-label">Mode of Transport:</div>
								<div class="col-md-7" style="padding:0px">
									<div class="col-md-12">
										<select class="select2" id="fieldworkTransport" name="fieldworkTransport" >
											<option value=""></option>
											<c:forEach var="transport" items="${transportList}">
												<option value="<c:out value="${transport}" />" >${transport}</option>
											</c:forEach>
										</select>
									</div>
									<div class="col-md-12">
										<input id="fieldworkTransportExtra" name="fieldworkTransportExtra" type="text" class="form-control" />
									</div>
								</div>
							</div>
							<div class="form-group">
								<div class="col-md-3 control-label">Expense HK$:</div>
								<div class="col-md-7">
									<input id="fieldworkExpenses" name="fieldworkExpenses" type="text" class="form-control" />
								</div>
							</div>
							<div class="form-group travelForm" >
								<div class="col-md-3 control-label">Include in Transport Form:</div>
								<div class="col-md-7 form-control-static">
									<input id="fieldworkTransportForm" name="fieldworkTransportForm" type="checkbox" value="true"/>
								</div>
							</div>	
							<div class="form-group travelForm" >
								<div class="col-md-3 control-label">Transit:</div>
								<div class="col-md-7 form-control-static">
									<input id="fieldworkTransit" name="fieldworkTransit" type="checkbox" value="true" />
								</div>
							</div>	
							<div class="form-group">
								<div class="col-md-3 control-label">Remark:</div>
								<div class="col-md-7">
									<input id="fieldworkRemark" name="fieldworkRemark" type="text" class="form-control" />
								</div>
							</div>	
							<div style="margin-top: 30px;" class="col-md-3 control-label"></div>
							<div style="margin-top: 30px;" class="col-md-2">
								<button type="submit" class="btn btn-primary" data-loading-text="Loading..." id="addFieldworkTimeLog">Submit</button>
							</div>
							<div style="margin-top: 30px;" class="col-md-5">
								<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
							</div>
						</div>
					  </div>
					</div>
				</form>
		  	</div>
	  		<!-- Add Check Remark Dialog -->
			<div class="modal fade" id="remarkModal" tabindex="-1" role="dialog"
				aria-labelledby="dialogLabel" aria-hidden="true">
				<form id="remarkForm">
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="clearfix overlay"
								data-bind="loading:isLoading, visible:isLoading"></div>
						 	<div class="modal-header">
								<h4 class="modal-title" data-bind="text:formTitle">Itinerary Check Remark</h4>
						  	</div>
						  	<div class="modal-body form-horizontal">
								<div class="form-group">
									<div class="col-md-3 control-label" id="deviationStatus">Remark:</div>
									<div class="col-md-7">
										<input id="checkRemark" name="checkRemark" type="text"
										class="form-control" />
									</div>
								</div>								
								<div style="margin-top: 30px;" class="col-md-3 control-label"></div>
								<div style="margin-top: 30px;" class="col-md-2">
									<button type="button" class="btn btn-primary"
										data-loading-text="Loading..." id="submitRemarkBtn">Submit</button>
								</div>
								<div style="margin-top: 30px;" class="col-md-5">
									<button type="button" class="btn btn-default"
										data-dismiss="modal">Cancel</button>
								</div>
							</div>
						</div>
					</div>
				</form>
			</div>	
        </section>
	</jsp:body>
</t:layout>

