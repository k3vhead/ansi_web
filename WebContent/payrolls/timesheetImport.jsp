<%@ page contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib uri="/WEB-INF/c.tld" prefix="c"%>
<%@ taglib uri="/WEB-INF/sql.tld" prefix="sql"%>
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles"%>
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing"%>
<%@ taglib uri="/WEB-INF/theTagThing.tld" prefix="ansi"%>

<tiles:insert page="../layout.jsp" flush="true">

	<tiles:put name="title" type="string">
		Payroll Timesheet Import
    </tiles:put>

	<tiles:put name="headextra" type="string">
		<link rel="stylesheet" href="css/lookup.css" />
		<link rel="stylesheet" href="css/ticket.css" />
		<link rel="stylesheet" href="css/document.css" />
		<script type="text/javascript" src="js/ansi_utils.js"></script>
		<script type="text/javascript" src="js/addressUtils.js"></script>
		<script type="text/javascript" src="js/lookup.js"></script>
		<script type="text/javascript" src="js/document.js"></script>
		<script type="text/javascript" src="js/payroll.js"></script>

		<style type="text/css">
			#data-file {
				display: none;
			}
			#data-header {
				display: none;
			}
			#employee-modal {
				display: none;
			}
			#employee-modal #employee-data .employeeCode {
				width: 40px;
			}
			#employee-modal .productivity {
				text-aligh: right;
			}
			#employee-modal .total-hours {
				text-aligh: right;
			}
			#employee-modal #employee-data {
				margin-bottom: 20px;
			}
			#employee-modal #employee-data .spacer {
				width: *;
			}
			#employee-modal #employee-data .employeeCodeSpacer {
				width: 45px;
			}
			#employee-modal #employee-data .row-number-label {
				margin-left: 20px;
			}
			#employee-modal #time-calcs .row-label {
				width: 40px;
			}
			#employee-modal #time-calcs .err {
				/* border : dashed blue 1px; */
			}
			#employee-modal .row-label-col-2 {
				width: 100px;
			}
			#employee-modal #time-calcs .spacer-mid {
				width: 15px;
			}
			#employee-modal #time-calcs {
				width: 585px;
			}
			#employee-modal #time-calcs td.hours {
				width: 85px;
			}
			#employee-modal #time-calcs td.hours input.hours {
				width: 75px;
				text-align: right;
			}
			#employee-modal #time-calcs td.hoursErr {
				width: 40px;
			}
			td.money {
				width: 100px;
			}
			#employee-modal #time-calcs input.money {
				width: 80px;
				text-align: right;
			}
			.errorsFoundHighlight {
				background-color: yellow !important;
				border: 3px black solid; 
			}
			#employee-modal #time-calcs .percentage {
				/* width: 120px; */
				text-align: right;
				padding-right: 20px;
			}
			#employee-modal #time-calcs input.percentage {
				width: 80px;
				text-align: right;
				padding-right: 20px;
			}
			#employee-modal #time-calcs span.productivity {
				text-align: right;
				padding-right: 20px;
			}
			#employee-modal #time-calcs td.totalHours {
				text-align: right;
				padding-right: 20px;
			}
			#filter-container {
				width: 402px;
				float: right;
			}
			.col-heading {
				text-align: left;
				font-weight: bold;
			}
			#organization-display {
				display: none;
			}
			#organization-display table {
				width: 100%;
				border: solid 1px #404040;
			}
			#organization-display th {
				text-align: left;
			}
			#organization-edit table {
				width: 100%;
				border: solid 1px #404040;
			}
			#organization-edit th {
				text-align: left;
			}
			.action-link {
				text-decoration: none;
				cursor: pointer;
			}
			.dataTables_wrapper {
				padding-top: 10px;
			}
			.details-control {
				cursor: pointer;
			}
			.form-label {
				font-weight: bold;
				white-space: nowrap;
			}		
			.grayback {
				background-color:#CFCFCF;
			}	
			.orange-bold {
				color:#CC6600;
				font-weight:bold;
			}
			.org-status-change {
				display: none;
				cursor: pointer;
			}
			.red-bold {
				color:#FF0000;
				font-weight:bold;
			}
			.view-link {
				color: #404040;
			}
			.workingBox {
				width: 20px;
				display: inline-block;
			}
			.working {
				display: none;
			}
			.thinking {
				display: none;
			}
		</style>
		<script type="text/javascript">    
	       	$(document).ready(function(){
	           	;TIMESHEET_IMPORT = {
	           		statusIsGood : '<webthing:checkmark>No Errors</webthing:checkmark>',
	           		statusIsBad : '<webthing:ban>Error</webthing:ban>',
	           		statusAintGood : '<webthing:warning>Warning</webthing:warning>',
	           		saveButton : '<webthing:save>Save</webthing:save>',
	           		edit : '<webthing:edit>Edit</webthing:edit>',
	           		view : '<webthing:view styleClass="details-control">Details</webthing:view>',
	           		worksheetHeader : null,
	           		employeeMap : {},

	           		init : function() {
	           			TIMESHEET_IMPORT.makeClickers();  
	           			TIMESHEET_IMPORT.setupCityTypeAhead();	           				           			
	           		},	           	       
	           		
	           		
	           		calculateTotaHoursOnModal : function(){
           		 		var $totalHours = 0;
						var $regularHours = parseFloat($('[name="regularHours"]').val()); 
	           			var	$otHours = parseFloat($('[name="otHours"]').val());
	           			var $vacationHours = parseFloat($('[name="vacationHours"]').val());
	           			var $holidayHours = parseFloat($('[name="holidayHours"]').val()); 
        		           		
						$('[name="regularHours"]').val($regularHours.toFixed(2));
	           			$('[name="otHours"]').val($otHours.toFixed(2));
	           			$('[name="vacationHours"]').val($vacationHours.toFixed(2));
	           			$('[name="holidayHours"]').val($holidayHours.toFixed(2));
							           			
	           			$totalHours = $regularHours + $otHours + $vacationHours + $holidayHours;
		           		
       		 			$('#employee-modal .totalHours').text($totalHours.toFixed(2));
	           		},
	           		
	           		
	           		closeEmployeeModal : function() {
	           			//$rowNumber, $action
	           		},	  
	           		
	           		
	           		
	           		displayHeader : function($data) {
						console.log("displayHeader");
						TIMESHEET_IMPORT.worksheetHeader = $data.data.worksheetHeader;
						
						// this bit maps message keys to html fields (eg a "LOCALE" message gets displayed in a span with class "localeErr")
						var $messageFieldMap = {
							"LOCALE":"localeErr",
							"OPERATIONS_MANAGER":"operationsManagerNameErr",
							"WEEK_ENDING":"weekEndingErr",
							"DIVISION":"divisionErr",
							"FILENAME":"fileNameErr",
						}
						
	           			$("#data-header .err").html("");

	           			var $formattedWeekendingDate = null;
	           			if ( $data.data.worksheetHeader.weekEnding != null ) {
	           				$formattedWeekendingDate = new Date($data.data.worksheetHeader.weekEnding).toISOString().slice(0, 10);
	           			}
	           			    
	           			if($data.data.worksheetHeader.division != null) {
	           				$("input[name='divisionId']").val( $data.data.worksheetHeader.division.divisionId);
	           				$("#file-header-data .divisionId").html($data.data.worksheetHeader.division.divisionDisplay);
	           			}
	           			$('input[name="operationsManagerName"]').val($data.data.worksheetHeader.operationsManager);           			
	           			$('input[name="payrollDate"]').val($formattedWeekendingDate);
	           			
	           			if ( $data.data.worksheetHeader.locale != null ) {
		           			$("#file-header-data .state").html($data.data.worksheetHeader.locale.stateName);	           			
	           				if ( $data.data.worksheetHeader.localeTypeId != "STATE") {
	           					$("#file-header-data .city").html($data.data.worksheetHeader.locale.name);
	           				}
	           			}
	           			$("#data-header .timesheetFile").html($data.data.fileName);	       	           			

	           			$.each( $data.data.worksheetHeader.messages, function($fieldKey, $messageList) {
	           				if ( $messageList[0].errorMessage.errorLevel != "OK" ) {
	           					var $displayList = [];
	           					$.each( $messageList, function($index, $message) {
	           						$displayList.push($message.errorMessage.message)	
	           					});
	           					var $displayText = $displayList.join("<br />");
	           					var $selector = "#file-header-data ." + $messageFieldMap[$fieldKey];
	           					$($selector).html($displayText);
	           				}
	           			});
	           			$("#data-header").show();           					
	           		},	    
	           		
	           		

	           		
	           		
	           		displayEmployeeModalErrors : function($rowNumber) {
						var $employeeErrors = TIMESHEET_IMPORT.employeeMap[$rowNumber].messages;
	    				var $selector="";
	    				var $errMsg="";
	    				$("#employee-modal .err").html("");
	    				$("#employee-modal input").removeClass("errorsFoundHighlight");
       					       		
           				$.each(TIMESHEET_IMPORT.employeeMap[$rowNumber].messages, function($index, $value) {
							for(i=0;i<$value.length;i++){
	           					var $s="";
	           					
	           					if(!$value[i].ok){	               					
	               					$selector="#employee-modal ." + $index + "Err";
	               					$errMsg = $value[i].errorMessage.errorLevel + ": " + $value[i].errorMessage.message;
	               					$("input[name='" + $index + "']").addClass("errorsFoundHighlight");
	               					$s = $($selector).html();

	               					if($s){
	               						$s = $s + "<br/>" + $errMsg;
	               					}
	               					else 
	               						$s = $errMsg;
	               					
	               					//$($selector).html($($selector).html() + $errMsg);               					
	               					$($selector).html($s);
           				
	               				}
							}           					
           				});
	           		},
	           		
	           		
	           		
	           		formatDetail : function(row) {
	           			console.log("formatDetail");
	           			var $table = $("<table>");
	           			$table.attr("style","width:375px; margin-left:45px;");
	           			
	           			$expenseRow = $("<tr>");
	           			<!-- $expenseRow.append( $('<td>') );-->
	           			$expenseRow.append( $("<td>") );
	           			$expenseRow.append( $("<td>").append("Expenses (Amt) :"));
	           			$expenseRow.append( $("<td>").append(" ") );
	           			$expenseRow.append( $("<td aligh='right'>").append(row.expenses));
	           			$expenseRow.append( $("<td>").append(TIMESHEET_IMPORT.statusIsGood));           			
	           			$table.append($expenseRow);
           			
	           			$otRow = $("<tr>");
	           			$expenseRow.append( $('<td>') );
	           			$otRow.append( $("<td>") );
	           			$otRow.append( $("<td>").append("Overtime(Hrs|Pay) :"));
	           			$otRow.append( $("<td>").append(row.otHours));
	           			$otRow.append( $("<td aligh='right'>").append(row.otPay));
	           			$otRow.append( $("<td>").append(TIMESHEET_IMPORT.statusIsGood));           			
	           			$table.append($otRow);
           			
	           			$vacactionRow = $("<tr>");
	           			$expenseRow.append( $('<td>').append("&nbsp;") );
	           			$vacactionRow.append( $("<td>") );
	           			$vacactionRow.append( $("<td>").append("Vacation (Hrs|Pay)) :"));
	           			$vacactionRow.append( $("<td>").append(row.vacationHours));
	           			$vacactionRow.append( $("<td aligh='right'>").append(row.vacationPay));
	           			$vacactionRow.append( $("<td>").append(TIMESHEET_IMPORT.statusIsBad));           			
	           			$table.append($vacactionRow);
						
	           			return $table;
	
	           		},
	           		
	           	
	           		initializeDisplay: function(){
	           			console.log("TIMESHEET_IMPORT.initializeDisplay: function - begin");	           			

           				$("#prompt-div").show();
           				$("#prompt-div .timesheet-file").val("");

           				$("#data-header").hide();

	           			$("#open-button").on("click");
	           			$("#data-detail").hide();
           				$("#timesheet").hide();

           				$("#timesheet").DataTable().clear();

           				/*
           				$("#timesheet").DataTable().draw();           				
           				$("#timesheet").DataTable().destroy
           				*/
           				
           				var $el = $('#timesheet-file');
                        $el.wrap('<form>').closest('form').get(0).reset();
                        $el.unwrap();
           
	           		},	     
	           		
	           		
	           		makeClickers : function() {
	           			console.log("makeClickers");
	           			$("#open-button").click(
	           				function($event) { 
		           				TIMESHEET_IMPORT.openTheFile();
		           			});
	           			
	           			$("#data-header input[name='cancelButton']").click(function($event) {
	           				TIMESHEET_IMPORT.initializeDisplay();
	           			});
	           		},
	           		
	           		
	           		makeEmployeeValue : function ($value, $messageList) {
	           			// <span class="red tooltip"><span class="tooltiptext">Expense Claim<br />YTD Expense Ptc</span>$48.23</span>
	           			var $errorList = [];
	           			var $employeeValue = $value;
	           			var $errorLevel = "WARNING";  // set to warning because we don't use this if max error level is OK
	           			var $msgColor = {"WARNING":"orange-bold", "ERROR":"red-bold"};
	           			$.each( $messageList, function($index, $msgValue) {
	           				if ( $msgValue.ok == false ) {
	           					$errorList.push($msgValue.errorMessage.message);
	           					if ( $msgValue.errorMessage.errorLevel == "ERROR" ) {
	           						$errorLevel = "ERROR";
	           					} 
	           				}
	           			});
	           			if ( $errorList.length > 0 ) {
	           				var $message = $errorList.join("<br />");
	           				$employeeValue = '<span class="'+ $msgColor[$errorLevel]+' tooltip"><span class="tooltiptext">'+$message+'</span>'+$value+'</span>';
	           			}
	           			return $employeeValue;
	           		},
	           		
	           		
	           		
	           		openFile : function($event) {
	           			console.log("openFile");
	           			var results = $event.target.result;
	           			var file = document.getElementById('timesheet-file').files[0];
	           			var fileName = document.getElementById('timesheet-file').files[0].name;
	           			var formData = new FormData();
	           			
	           			formData.append('timesheetFile',file, fileName);
	           			
	           			var xhr = new XMLHttpRequest();
	           			xhr.open('POST',"payroll/timesheetImport", true);
	           			
	           			xhr.onload = function() {
	           				if ( xhr.status == 200 ) {
	           					var $data = JSON.parse(this.response);
								TIMESHEET_IMPORT.processUpload200($data);	           					
           					} else {
           						$("#globalMsg").html("Response Code " + xhr.status + ". Contact Support");
           					}
           				};
           			
           				xhr.send(formData);
	           		},
	           		
	           		
	           		
	           		openTheFile : function(){
	           			console.log("openTheFile");
           				
           				$("#prompt-div .err").html("");
           				var file = document.getElementById('timesheet-file').files[0];
           				
	           			if (typeof file !== 'undefined'){
	           				var fileName = document.getElementById('timesheet-file').files[0].name;
	           				var reader = new FileReader();

		           			reader.readAsText(file, 'UTF-8');	           				
	           				reader.onload = TIMESHEET_IMPORT.openFile;		           				
	           				$("#prompt-div").hide();
	           				$(".thinking").show();		           				
	           				// reader.onprogress ...  (progress bar)	  
	           			} else{
							$("#prompt-div .timesheetFileErr").html("Please select a file to open").show().fadeOut(3000);
	           			}
	           		},    		
	           		
	           		
	           		
	           		
	           		populateDataGrid : function($data){
	           			console.log("populateDataGrid");
	           			// create and populate dicttionary object for use in modal
	           			var dictionary = $data.data.employeeList;
					    var $errorFound = '<payroll:errorFound>Invalid Value</payroll:errorFound>';
					    TIMESHEET_IMPORT.employeeMap = {};
					    $.each($data.data.employeeList, function($index, $value) {
		            		TIMESHEET_IMPORT.employeeMap[$value.row] = $value;
		            	});  
	   
	           			// populate the visible table on-screen
	           			var $table = $("#timesheet").DataTable({
	           				aaSorting : [[0,'asc']],
	            			processing : true,
	           				data : $data.data.employeeList,
	           				searching : true,
	            	        searchDelay : 800,
	            	        paging: false,
	            	        destroy: true,
	            	        autoWidth: false,
	            	        "rowCallback": function(row, data, index){
	            	            if(row.children[17].children[0].children[0].childNodes[0].textContent=="Error"){
									$(row).addClass('errorsFoundHighlight');
								}
							},
	           				columnDefs : [
	             	            //{ className : "dt-head-center", "targets":[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16]},
	            	            { className : "dt-left", "targets": [2] },
	            	            { className : "dt-center", "targets": [0,1,16,17] },
	            	            { className : "dt-right", "targets": [3,4,5,6,7,8,9,10,11,12,13,14,15]},
	            	        ],
	           				columns : [
	           					{ title : "Row", searchable:true, "defaultContent": "", 
	           						data:function(row, type, set) { return TIMESHEET_IMPORT.makeEmployeeValue(row.row, row.messageList['row']);  }
	           					},
	           					{ title : "Code", searchable:true, "defaultContent": "", 
	           						data:function(row, type, set) { return TIMESHEET_IMPORT.makeEmployeeValue(row.employeeCode, row.messageList['globalMsg']);  }
	           					},
	           					{ title : "Employee Name", searchable:true, "defaultContent": "", 
	           						data:function(row, type, set) { return TIMESHEET_IMPORT.makeEmployeeValue(row.employeeName, row.messageList['employeeName']);  }
	           					},
	           					{ title : "Reg Hrs", searchable:true, "defaultContent": "",
           							data:function(row, type, set) { return TIMESHEET_IMPORT.makeEmployeeValue(row.regularHours.toFixed(2), row.messageList['regularHours']);  }
	           					},
	           					{ title : "Reg Pay", searchable:true, "defaultContent": "", 
           							data:function(row, type, set) { return TIMESHEET_IMPORT.makeEmployeeValue(row.regularPay.toFixed(2), row.messageList['regularPay']);  }
	           					},
	           					{ title : "Exp", searchable:true, "defaultContent": "", 
           							data:function(row, type, set) { return TIMESHEET_IMPORT.makeEmployeeValue(row.expenses.toFixed(2), row.messageList['expenses']);  }
	           					},
	           					{ title : "OT Hrs", searchable:true, "defaultContent": "", 
           							data:function(row, type, set) { return TIMESHEET_IMPORT.makeEmployeeValue(row.otHours.toFixed(2), row.messageList['otHours']);  }
	           					},
	           					{ title : "OT Pay", searchable:true, "defaultContent": "", 
           							data:function(row, type, set) { return TIMESHEET_IMPORT.makeEmployeeValue(row.otPay.toFixed(2), row.messageList['otPay']);  }
	           					},
	           					{ title : "Vac Pay", searchable:true, "defaultContent": "", 
           							data:function(row, type, set) { return TIMESHEET_IMPORT.makeEmployeeValue(row.vacationPay.toFixed(2), row.messageList['vacationPay']);  }
	           					},
	           					{ title : "Hol Pay", searchable:true, "defaultContent": "", 
           							data:function(row, type, set) { return TIMESHEET_IMPORT.makeEmployeeValue(row.holidayPay.toFixed(2), row.messageList['holidayPay']);  }
	           					},
	           					{ title : "Gross Pay", searchable:true, "defaultContent": "",  
           							data:function(row, type, set) { return TIMESHEET_IMPORT.makeEmployeeValue(row.grossPay.toFixed(2), row.messageList['grossPay']);  }
	           					},
	           					{ title : "Exp Smt'd", searchable:true, "defaultContent": "", 
           							data:function(row, type, set) { return TIMESHEET_IMPORT.makeEmployeeValue(row.expensesSubmitted.toFixed(2), row.messageList['expensesSubmitted']);  }
	           					},
	           					{ title : "Exp All'd", searchable:true, "defaultContent": "", 
           							data:function(row, type, set) { return TIMESHEET_IMPORT.makeEmployeeValue(row.expensesAllowed.toFixed(2), row.messageList['expensesAllowed']);  }
	           					},
	           					{ title : "Volume", searchable:true, "defaultContent": "", 
	           						data:function(row, type, set) { return TIMESHEET_IMPORT.makeEmployeeValue(row.volume.toFixed(2), row.messageList['volume']);  }
	           					},
	           					{ title : "Direct Labor", searchable:true, "defaultContent": "",
	           						data:function(row, type, set) { return TIMESHEET_IMPORT.makeEmployeeValue(row.directLabor.toFixed(2), row.messageList['directLabor']);  }
	           					},
	           					{ title : "Prod %", searchable:true, "defaultContent": "", 
	           						data:function(row, type, set) { return TIMESHEET_IMPORT.makeEmployeeValue(row.productivity.toFixed(2), row.messageList['productivity']);  }
	           					},
	           					{ title : "Status", "defaultContent":"", 
	           						data : function(row, type, set) {
										var $tag = TIMESHEET_IMPORT.statusIsGood;
										
	           							if ( row.errorLevel == "WARNING" ) { 
	           								$tag = TIMESHEET_IMPORT.statusAintGood;
	           							} else if ( row.errorLevel == "ERROR" ) {
											$tag = TIMESHEET_IMPORT.statusIsBad;	           								
	           								//$('td', row).css('background-color', 'Yellow');
           								}
	           							return $tag;
	           							
	           						}
	           					},
	           					{ title : "Action", 
	           						'orderable': false, 	           						
	    			            	data: function ( row, type, set ) { 
	    			            		var $editLink = '<span class="action-link edit-link" data-id="'+row.row+'">' + TIMESHEET_IMPORT.edit + '</span>';
	    			            		return  $editLink;	    			            		
	    			            		//var $viewLink = '<span class="action-link view-link" data-id="'+row.row+'">' + TIMESHEET_IMPORT.view + '</span>';
	    			            		//var $deleteLink = '<span class="action-link delete-link" data-id="'+row.employee_code+'" data-name="'+row.employee_name+'"><webthing:delete>Delete</webthing:delete></span>';
	    			            		//return  $editLink + TIMESHEET_IMPORT.saveButton;
	    			            	} 
	           					},
	           				],
	           				"initComplete": function(settings, json) {
       			            	var myTable = this;
       			            	LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#timesheet", TIMESHEET_IMPORT.populateDataGrid, null, $data);
       			            },
	           				drawCallback : function( settings ) {
	           					$(".edit-link").off("click");
	           					$(".edit-link").on("click", function() {
	           						var $rowNumber = $(this).attr('data-id');
	           						TIMESHEET_IMPORT.showEmployeeModal($rowNumber, "edit");
	           					});	           					
	           				}
	           			});
	           			$("#data-detail").show();
	           			$("#timesheet").show();
	           		},
	           		
	           		
	           		
	           		populateEmployeeModal : function($rowNumber) {
	        			console.log("populateEmployeeModal: " + $rowNumber);

	           			var $myEmployee = TIMESHEET_IMPORT.employeeMap[$rowNumber];
	           			console.log($myEmployee);
	           			console.log(TIMESHEET_IMPORT.worksheetHeader);
	           			$('#employee-modal select[name="divisionId"]').val(TIMESHEET_IMPORT.worksheetHeader.division.divisionId);
	           			$formattedWeekendingDate = new Date(TIMESHEET_IMPORT.worksheetHeader.weekEnding).toISOString().slice(0, 10);
	           			$('#employee-modal input[name="weekEnding"]').val($formattedWeekendingDate);
	           			$('#employee-modal input[name="employeeName"]').val($myEmployee.employeeName);
	           			$('#employee-modal input[name="employeeCode"]').val($myEmployee.employeeCode);
	           			if ( TIMESHEET_IMPORT.worksheetHeader.locale.localeTypeId == 'STATE' ) {
	           				$('#employee-modal input[name="city"]').val('');
	           			} else {
	           				$('#employee-modal input[name="city"]').val( TIMESHEET_IMPORT.worksheetHeader.locale.name );
	           			}
	           			$('#employee-modal select[name="state"]').val( TIMESHEET_IMPORT.worksheetHeader.locale.stateName );
	           			$('#employee-modal input[name="row"]').text($rowNumber);

						$('#employee-modal input[name="regularHours"]').val(TIMESHEET_IMPORT.formatFloat($myEmployee.regularHours));
	           			$('#employee-modal input[name="otHours"]').val(TIMESHEET_IMPORT.formatFloat($myEmployee.otHours));
	           			$('#employee-modal input[name="vacationHours"]').val(TIMESHEET_IMPORT.formatFloat($myEmployee.vacationHours));
	           			$('#employee-modal input[name="holidayHours"]').val(TIMESHEET_IMPORT.formatFloat($myEmployee.holidayHours));
						
						$('#employee-modal input[name="regularPay"]').val(TIMESHEET_IMPORT.formatFloat($myEmployee.regularPay));
	           			$('#employee-modal input[name="otPay"]').val(TIMESHEET_IMPORT.formatFloat($myEmployee.otPay));
	           			$('#employee-modal input[name="vacationPay"]').val(TIMESHEET_IMPORT.formatFloat($myEmployee.vacationPay));
	           			$('#employee-modal input[name="holidayPay"]').val(TIMESHEET_IMPORT.formatFloat($myEmployee.holidayPay));
	           			
						$('#employee-modal input[name="regularHours"]').blur(function() { TIMESHEET_IMPORT.calculateTotaHoursOnModal()});
						$('#employee-modal input[name="otHours"]').blur(function() { TIMESHEET_IMPORT.calculateTotaHoursOnModal()});
	           			$('#employee-modal input[name="vacationHours"]').blur(function() { TIMESHEET_IMPORT.calculateTotaHoursOnModal()});
	           			$('#employee-modal input[name="holidayHours"]').blur(function() { TIMESHEET_IMPORT.calculateTotaHoursOnModal()});
                        
	           			TIMESHEET_IMPORT.calculateTotaHoursOnModal();
	           			
	           			$('#employee-modal input[name="directLabor"]').val(TIMESHEET_IMPORT.formatFloat($myEmployee.directLabor));
	           			$('#employee-modal input[name="volume"]').val(TIMESHEET_IMPORT.formatFloat($myEmployee.volume));
	           			$('#employee-modal input[name="grossPay"]').val(TIMESHEET_IMPORT.formatFloat($myEmployee.grossPay));

	           			$('#employee-modal input[name="expenses"]').val(TIMESHEET_IMPORT.formatFloat($myEmployee.expenses));
	           			$('#employee-modal input[name="expensesAllowed"]').val(TIMESHEET_IMPORT.formatFloat($myEmployee.expensesAllowed));
	           			$('#employee-modal input[name="expensesSubmitted"]').val(TIMESHEET_IMPORT.formatFloat($myEmployee.expensesSubmitted));	           			
	           			$('#employee-modal .productivity').text(TIMESHEET_IMPORT.formatFloat($myEmployee.productivity));
	           			
	           			$.each(['select[name="divisionId"]','input[name="weekEnding"]','input[name="city"]','select[name="state"]'], function($index, $value) {
	           				var $selector = '#employee-modal ' + $value;
	           				$($selector).attr("disabled","");
	           			});
	           			//TIMESHEET_IMPORT.displayEmployeeModalErrors($rowNumber);
	           		},
	           		
	           		
	           		processEmployeeValidationSuccess : function($data, $passthru) {
	           			console.log("processEmployeeValidationSuccess");
	           			if ( $data.responseHeader.responseCode == 'EDIT_FAILURE' ) {
           					// check for header error messages first
           					if($data.header){    
	           					if($data.header.messages){
	    	           				$.each($data.data.webMessages, function($index, $value) {
	    	           					console.log($index + ' value is ' + $value);	           				
	    	           					var $selector = "#file-header-data ." + $index + "Err";
	    	           					console.log("selector is " + $selector);	           					           					
	    	           					$($selector).html($value[0]);
	    	           				});
	           					}
	           				}
	           			} else if ( $data.responseHeader.responseCode == 'SUCCESS' || $data.responseHeader.responseCode == 'EDIT_WARNING') {
	           				updateDataTableFromModal();
	               			if ($data.responseHeader.responseCode == 'SUCCESS') {
	               				$("#globalMsg").html("Success").show().fadeOut(3000);
	               			} else {
	               				ANSI_UTILS.showWarnings("timesheet_warnings", $data.header.messages);
	               			}
	           			} else {
	           				$("#employee-modal .timesheet-err").html("Unexpected response code: " + $data.responseHeader.responseCode + ". Contact Support").show();
	           			}
	           		},
	           		

	           		
	           		
	           		processEmployeeValidationErrors : function($data, $passthru) {
	           			console.log("processEmployeeValidationErrors");
	           			if ( $data.responseHeader.responseCode == 'EDIT_FAILURE' ) {
	           				$.each($data.data.webMessages, function($index, $value) {
	           					var $selector = "#employee-modal ." + $index + "Err";
	           					$($selector).html($value[0]);
	           				});
	           			} else if ( $data.responseHeader.responseCode == 'SUCCESS' || $data.responseHeader.responseCode == 'EDIT_WARNING') {
	               			$("#timesheetLookup").DataTable().ajax.reload();
	               			// close both modals because we don't know which one called this method, but only after they've been init'd
	               			if ( $("#edit-modal").hasClass("ui-dialog-content")) {
	               				$("#edit-modal").dialog("close");
	               			}
	               			if ( $("#confirmation-modal").hasClass("ui-dialog-content")) {
	               				$("#confirmation-modal").dialog("close");
	               			}
	               			if ($data.responseHeader.responseCode == 'SUCCESS') {
	               				$("#globalMsg").html("Success").show().fadeOut(3000);
	               			} else {
	               				ANSI_UTILS.showWarnings("timesheet_warnings", $data.data.webMessages);
	               			}
	           			}
	           		},	       
	           		
	           		
	           		
	           		processUpload200 : function($data) {
	           			console.log("processUpload200");
	           			$("#data-header .err").html("");
	           			
	           			// SUCCESS == File is OK & Header is OK; employee weirdnesses will be processed at a different level
	           			// WARNING == issue with the header, but we can still process (eg. duplicate upload)
	           			// FAILURE == catastrophic problem (eg not a timesheet, wrong format, etc)
	           			if ( $data.responseHeader.responseCode == "SUCCESS") {
	           				TIMESHEET_IMPORT.processUploadSuccess($data);
	           			} else if ( $data.responseHeader.responseCode == "EDIT_WARNING" ) {
	           				TIMESHEET_IMPORT.processUploadWarning($data);
	           			} else if ( $data.responseHeader.responseCode == "EDIT_FAILURE" ) {
	           				TIMESHEET_IMPORT.processUploadFailure($data);
	           			} else {
	           				$("#globalMsg").html("Unexpected HTTP Response " + $data.responseHeader.responseCode + ". Contact Support");
	           			}
	           			
	           		},	
	           			
	           		
	           		
	           		
	           		
	           		// FAILURE == catastrophic problem (eg not a timesheet, wrong format, etc)
       				// 1. hide thinking
       				// 2. Show prompt with messages
	           		processUploadFailure : function($data) {
	           			console.log("processUploadFailure");
	           			$("#prompt-div .err").html("");
	           			var fName = document.getElementById('timesheet-file').files[0].name;
	           			var $fileMessages = $data.data.webMessages.timesheetFile;
	           			$(".thinking").hide();		
	           			$("#prompt-div").show();  
	           			$("#prompt-div .timesheetFileErr").html($fileMessages[0]).show().fadeOut(10000);
	           		},
	           		
	           		
	           		// SUCCESS == File is OK & Header is OK; employee weirdnesses will be processed at a different level
       				// 1. hide thinking
       				// 2. clear header
       				// 3. display header
       				// 4. Show employee grid
	           		processUploadSuccess : function($data) {
	           			console.log("processUploadSuccess");
	           			$(".thinking").hide();		
						TIMESHEET_IMPORT.displayHeader($data);
       					TIMESHEET_IMPORT.populateDataGrid($data);   
	           		},

	           		// WARNING == issue with the header, but we can still process (eg. duplicate upload)
       				// 1. hide thinking
       				// 2. clear header
       				// 3. display header / messages
       				// 4. Show employee grid
	           		processUploadWarning : function($data) {
	           			console.log("processUploadWarning");
	           			//TIMESHEET_IMPORT.displayHeaderAndHeaderMessages($data);
	           			$(".thinking").hide();		
						TIMESHEET_IMPORT.displayHeader($data);
       					TIMESHEET_IMPORT.populateDataGrid($data);           					
	           		},
	           		
	           		
	           		
	           		
	           		setupCityTypeAhead : function(){
	           			console.log("setupCityTypeAhead");
	        			var $cityField = "#file-header-data  input[name='city']";
	        			var $stateField = "#file-header-data select[name='state']";
	        			
	        			var $localeComplete = $( $cityField ).autocomplete({
		    				source: function(request,response) {
		    					term = $($cityField).val();
		    					localeTypeId = null; 
		    					stateName = null; 
		    					if ( $( $stateField ).val() != null ) {
		    						stateName = $( $stateField ).val();	
		    					}
		    					$.getJSON("localeAutocomplete", {"term":term, "localeTypeId":localeTypeId, "stateId":stateName}, response);
		    				},
		                    minLength: 2,
		                    //select: function( event, ui ) {
		                    	//$("#addLocaleForm input[name='parentId']").val(ui.item.id);
		                    //	console.log("Got it: " + ui.item.id)
		                    //},
		                    response: function(event, ui) {
		                        if (ui.content.length === 0) {
		                        	$("#file-header-data .cityErr").html("No Matching Locale");
		                        	//$("#addLocaleForm input[name='parentId']").val("");
		                        } else {
		                        	$("#file-header-data .cityErr").html("");
		                        }
		                    }
		              	}).data('ui-autocomplete');	            		           			
	           		},	    
	           		
	           		
	           		
	           		          		
	           		
	           		
	           		showEmployeeModal : function($rowNumber, $action) {
	           			console.log("showEmployeeModal: " + $rowNumber + " " + $action);
	           			$edit = $action == 'edit';

	           			// if modal does not yet exist, then initialize it
	           			if ( ! $("#employee-modal").hasClass('ui-dialog-content') ) {
		        			PAYROLL_UTILS.initEditModal("#employee-modal",TIMESHEET_IMPORT.validateEmployeeModal);
	           			}
	        			$("#employee-modal").dialog("open");
	        			TIMESHEET_IMPORT.populateEmployeeModal($rowNumber);
	        		},
	        		
	        		
	        		
	        		formatFloat : function (numberAsString) {
						value = null;
						if (numberAsString == null || numberAsString == "") {
							value="";
						} else if ( isNaN(numberAsString) ) {
							value="";
						} else {
							x = parseFloat(numberAsString);
							value = x.toFixed(2);
						}
						return value;
					},
					
					
					
	        		
	           		
	           		updateDataTableFromModal : function() {
	           			console.log("saveEmployeeModal - grabbing values");
	           			var $rowNumber 			= $("#employee-modal [name='row']").val();	           			
	           			var $employeeName 		= $("#employee-modal [name='employeeName']").val();
	           			var $regularPay  		= $("#employee-modal [name='regularPay']").val();
	           			var $otPay 				= $("#employee-modal [name='otPay']").val();
	           			var $vacationPay  		= $("#employee-modal [name='vacationPay']").val();
	           			var $holidayPay  		= $("#employee-modal [name='holidayPay']").val();
	           			var $regularHours 		= $("#employee-modal [name='regularHours']").val();
	           			var $otHours  			= $("#employee-modal [name='otHours']").val();
	    	           	var $vacationHours 		= $("#employee-modal [name='vacationHours']").val();
	           			var $holidayHours  		= $("#employee-modal [name='holidayHours']").val();
	           			var $directLabor  		= $("#employee-modal [name='directLabor']").val();
	           			var $volume  			= $("#employee-modal [name='volume']").val();
	           			var $grossPay   		= $("#employee-modal [name='grossPay']").val();
	           			var $expenses  			= $("#employee-modal [name='expenses']").val();
	           			var $expensesAllowed 	= $("#employee-modal [name='expensesAllowed']").val();
	           			var $expensesSubmitted  = $("#employee-modal [name='expensesSubmitted']").val();
	           			var $productivity 		= $("#employee-modal [name='productivity']").val();
	           			
	           			var $idx = $rowNumber +1;
	           			$idx = $idx -1;
	           			
					    var table = $("#timesheet").DataTable();
					    
	           			TIMESHEET_IMPORT.employeeMap[$idx].employeeName = $employeeName;
	           			TIMESHEET_IMPORT.employeeMap[$idx].regularPay = $regularPay;
	           			TIMESHEET_IMPORT.employeeMap[$idx].otPay = $otPay;
	           			TIMESHEET_IMPORT.employeeMap[$idx].vacationPay = $vacationPay;
	           			TIMESHEET_IMPORT.employeeMap[$idx].holidayPay = $holidayPay;
	           			TIMESHEET_IMPORT.employeeMap[$idx].regularHours = $regularHours;
	           			TIMESHEET_IMPORT.employeeMap[$idx].otHours = $otHours;
	           			TIMESHEET_IMPORT.employeeMap[$idx].vacationHours = $vacationHours;
	           			TIMESHEET_IMPORT.employeeMap[$idx].holidayHours = $holidayHours;
	           			TIMESHEET_IMPORT.employeeMap[$idx].directLabor = $directLabor; 
	           			TIMESHEET_IMPORT.employeeMap[$idx].volume	 = $volume;
	           			TIMESHEET_IMPORT.employeeMap[$idx].grossPay = $grossPay;
	           			TIMESHEET_IMPORT.employeeMap[$idx].expenses = $expenses;
	           			TIMESHEET_IMPORT.employeeMap[$idx].expensesAllowed = $expensesAllowed;
	           			TIMESHEET_IMPORT.employeeMap[$idx].expensesSubmitted = $expensesSubmitted;
	           			TIMESHEET_IMPORT.employeeMap[$idx].productivity = $productivity;	           			
	           			TIMESHEET_IMPORT.employeeMap[$idx].directLabor = $directLabor;
	           			TIMESHEET_IMPORT.employeeMap[$idx].volume = $volume;
	           			TIMESHEET_IMPORT.employeeMap[$idx].grossPay = $grossPay;
	           			TIMESHEET_IMPORT.employeeMap[$idx].expenses = $expenses;
	           			TIMESHEET_IMPORT.employeeMap[$idx].expensesAllowed = $expensesAllowed;
	           			TIMESHEET_IMPORT.employeeMap[$idx].expensesSubmitted = $expensesSubmitted;
	           			TIMESHEET_IMPORT.employeeMap[$idx].productivity = $productivity;
	           			
					    table.row($rowNumber).data(TIMESHEET_IMPORT.employeeMap[$rowNumber]).draw();			    
	           		},
	           		
	           		
	           		validateEmployeeModal : function() {
	           			console.log("validate employee timesheet");
	           			$("#employee-modal .err").html("").show();
	           			var $outbound = {};
	           			var s;
	           			$.each( $("#employee-modal input"), function($index, $value) {
	           				s = $($value).val();
	           			   	s = s.replace(/\d+% ?/g, "");
	           			   	s = s.replace(/\d+, ?/g, "");	           			   	
	           				$outbound[$value.name] = s;
	           			});
	           			$.each( $("#employee-modal select"), function($index, $value) {
	           				$outbound[$value.name] = $($value).val();
	           			});
			           			           			
	           			$outbound["action"]="VALIDATE";

	           			var $callbacks = {
	        				200:TIMESHEET_IMPORT.processEmployeeValidationSuccess,
	        				403:TIMESHEET_IMPORT.processEmployeeValidationErrors,
	           				404:TIMESHEET_IMPORT.processEmployeeValidationErrors,
	           				405:TIMESHEET_IMPORT.processEmployeeValidationErrors,
	           				500:TIMESHEET_IMPORT.processEmployeeValidationErrors,
	        			};
	           			ANSI_UTILS.makeServerCall("POST", "payroll/timesheet", JSON.stringify($outbound), $callbacks, {});
	           		},
	           	};
	           	
	           	TIMESHEET_IMPORT.init();
	            	
	        });        		
        </script>
	</tiles:put>

	<tiles:put name="content" type="string">
		<h1>Payroll Timesheet Import</h1>
		<div id="prompt-div">
			<!-- <form id="file-selection" > -->
			<form id="file-selection" method="post" enctype="multipart/form-data">
				<table class="prompt">
					<tr>
						<td class="col1" id="file-picker-label"><span
							class="form-label">Payroll File: </span></td>
						<td class="col2" id="file-picker"><input type="file"
							id="timesheet-file" name="files[]" /></td>
						<td class="col3" id="open-button-cell"><input type="button"
							id="open-button" value="Open" /></td>
						<td class="col4" id="file-picker-err"><span
							class="timesheetFileErr err"> </span></td>
					</tr>
				</table>
			</form>
		</div>
		<div class="thinking">
			<webthing:thinking style="width:100%" />
		</div>


		<div id="data-header">
			<table id="data-file">
				<tr>
					<td id="payroll-file-label"><span class="form-label">Currently Viewing Payroll File :</span> <span class="timesheetFile"></span></td>
				</tr>
			</table>
			<table id="file-header-data">
				<tr class="label-row">
					<td class="col1" id="division-label"><span class="form-label">Division</span></td>
					<td class="col2" id="operations-manager-label"><span class="form-label">Operations Manager </span></td>
					<td class="col3" id="week-ending-label"><span class="form-label">Week Ending </span></td>
					<td class="col4" id="state-label"><span class="form-label">State</span></td>
					<td class="col5" id="city-label"><span class="form-label">City/Jurisdiction</span></td>
					<td class="col6" id="payroll-file-label"><span class="form-label">Payroll File </span></td>
					<td class="col7"><span class="form-label"> </span></td>
				</tr>
				<tr>
					<td class="col1">
						<input type="hidden" name="divisionId"/><span class="divisionId"></span>
					</td>
					<td class="col2"><span class="operationsManagerName"><input type="text" class="operationsManagerName" name="operationsManagerName" tabindex="2" /></span></td>
					<td class="col3"><span class="payrollDate"><input type="date" class="payrollDate" name="payrollDate" tabindex="3" /></span></td>
					<td class="col4"><span class="state"></span></td>
					<td class="col5"><span class="city"></span></td>
					<td class="col6"><span class="timesheetFile"></span></td>
					<td class="col7" id="cancel-save-buttons">
						<input type="button" value="Cancel" name="cancelButton" class="action-button" /> 
						<input type="button" value="Save" id="save-button" />
					</td>
				</tr>
				<tr class="message-row">
					<td class="col1"><span class="divisionErr err"></span></td>
					<td class="col2"><span class="operationsManagerNameErr err"></span></td>
					<td class="col3"><span class="weekEndingErr err"></span></td>
					<td colspan="2"><span class="localeErr err"></span></td>
					<td class="col6" colspan="2"><span class="fileNameErr err"></span></td>
				</tr>
			</table>
		</div>

		<div id="data-detail">
			<webthing:lookupFilter filterContainer="filter-container" />
			<table id="timesheet">
			</table>
		</div>


		<jsp:include page="timesheetEmployee.jsp">
			<jsp:param name="id" value="employee-modal" />
		</jsp:include>

		<%-- 
		<div id="employee-modal">
			<div style="width: 100%; height: 0px;">
				<span class="employeeEditErr err"></span>
			</div>
			<table id="employee-data">
				<tr>
					<td class="form-label">Employee Name :</td>
					<td class="employeeName"><span class="employeeName"> </span></td> 					
					<!--   <input class="employeeName" type="text" name="employeeName" tabindex="101" /> -->
					<td class="employeeNameSpacer"></td>
					<td class="form-label">Code :</td>
					<td class="employeeCode"><span class="employeeCode"> </span></td>
					
					<!--   <input class="employeeCode" type="text" name="employeeCode" tabindex="102" /></td> -->
					<td class="employeeCodeSpacer"></td>
					<td class="form-label">State:</td>
					<td class="state"><span class="state"> </span></td>
					<td class="stateSpacer"></td>
					<td class="form-label"><span
						class="form-label row-number-label">Row: </span></td>
					<td class="row"><span class="row"> </span></td>
				</tr>
				<tr>
					<td colspan="8"><span class="employeeNameErr DIVISIONErr stateErr employeeCodeErr rowErr err">
					</span></td>
					<!-- 
					<td colspan="3"><span
						class="employeeNameCodeErr err"> </span></td>
					<td colspan="3"><span class="stateErr err">
					</span></td>
					<td colspan="2"><span class="rowErr err"> </span>
					</td>
					 -->
				</tr>
			</table>
			<table id="time-calcs">
				<tr>
					<td class="row-label"></td>
					<td colspan="1" class="col-heading hours">Hours</td>
					<td colspan="2" class="col-heading pay">Pay</td>
					<td colspan="2" class="col-heading pay">Expenses</td>
				</tr>
				<tr>
					<td class="row-label">Regular:</td> 
					<td class="hours"><input class="hours" type="text"
						name="regularHours" tabindex="102" /></td>
					<td class="money"><input class="money" type="text"
						name="regularPay" tabindex="103" /></td>
					<td class="spacer-mid"></td>
					<td class="row-label-col-2">Submitted:</td>
					<td class="money"><input class="money" type="text"
						name="expensesSubmitted" tabindex="112" /></td>
				</tr>
				<tr>
					<td colspan="2" class="hoursErr">		<span class="regularHoursErr"></span></td>
					<td colspan="1" class="moneyErr">		<span class="regularPayErr err"></span></td>
					<td class="spacer-mid"></td>      
					<td colspan="2" class="moneyErr">	<span class="exp-ensesSubmittedErr err"> </span></td>
				</tr>
				<tr>
					<td class="row-label">Overtime:</td>
					<td class="hours"><input class="hours" type="text"
						name="otHours" tabindex="104" /></td>
					<td class="money"><input class="money" type="text"
						name="otPay" tabindex="105" /></td>
					<td class="spacer-mid"></td>
					<td class="row-label-col-2">Allowed:</td>
					<td class="money"><input class="money" type="text"
						name="expensesAllowed" tabindex="113" /></td>
				</tr>
				<tr>
					<td colspan="2" class="hoursErr">		<span class="otHoursErr err"> </span></td>
					<td colspan="1" class="moneyErr">		<span class="otPayErr err"> </span></td>
					<td class="spacer-mid"></td>     
					<td colspan="2" class="moneyErr">	<span class="expensesAllowedErr err"> </span></td>
				</tr>
				<tr>
					<td class="row-label"></td>
					<td class="hours total"></td>
					<td class="money"></td>
					<td class="spacer-mid"></td>
					<td colspan="2" class="form-label">Productivity</td>
				</tr>
				<tr>
					<td colspan="2" class="hoursErr"></td>
					<td class="moneyErr"></td>
					<td class="spacer-mid"></td>
				</tr>
				<tr>
					<td class="row-label">Vacation:</td>
					<td class="hours"><input class="hours" type="text"
						name="vacationHours" tabindex="106" /></td>
					<td class="money"><input class="money" type="text"
						name="vacationPay" tabindex="107" /></td>
					<td class="spacer-mid"></td>
					<td class="row-label-col-2">Direct Labor:</td>
					<td class="money"><input class="money" type="text"
						name="directLabor" tabindex="114" /></td>
				</tr>
				<tr>
					<td colspan="2" class="hoursErr">		<span class="vacationHoursErr"> </span></td>
					<td colspan="1" class="moneyErr">		<span class="vacationPayErr err"> </span></td>
					<td class="spacer-mid"></td> 
					<td colspan="2" class="moneyErr">		<span class="directLaborErr err"> </span></td>
				</tr>
				<tr>
					<td class="row-label">Holiday:</td>
					<td class="hours"><input class="hours" type="text"
						name="holidayHours" tabindex="108" /></td>
					<td class="money"><input class="money" type="text"
						name="holidayPay" tabindex="109" /></td>
					<td class="spacer-mid"></td>
					<td class="row-label-col-2">Volume :</td>
					<td class="money"><input class="money" type="text"
						name="volume" tabindex="115" /></td>
				</tr>
				<tr>
					<td colspan="2" class="hoursErr">		<span class="holidayHoursErr err"> </span></td>
					<td colspan="1" class="moneyErr">		<span class="holidayPayErr err"> </span></td>
					<td class="spacer-mid"></td>
					<td colspan="2" class="moneyErr">		<span class="volumeErr err"> </span></td>
				</tr>
				<tr>
					<td class="row-label">Total:</td>
					<!-- <td class="hours total">	<input 	class="hours" type="text" name="totalHours" 				tabindex="110" />		</td> -->
					<td class="totalHours"><span class="totalHours"> </span></td>
					<td class="money"><input class="money" type="text"
						name="grossPay" tabindex="111" /></td>
					<td class="spacer-mid"></td>
					<td class="row-label-col-2">Productivity:</td>
					<!--  <td class="percentage">		<input 	class="percentage" 	type="text" name="productivity"			tabindex="116" />		</td>  -->
					<td class="percentage"><span class="productivity"> </span></td>
				</tr>
				<tr>
					<td colspan="5" class="hoursErr">  			<span class="totalHoursErr grossPayErr productivityErr expensesAllowedErr expensesSubmittedErr err"> </span></td>
					<!-- 
					<td colspan="1" class="moneyErr">		<span class="grossPayErr err"> </span></td>
					<td class="spacer-mid"></td>
					<td colspan="2" class="percentageErr">	<span class="productivityErr err"> </span></td>
					 -->
				</tr>
			</table>
		</div>
		--%>
	</tiles:put>
</tiles:insert>




