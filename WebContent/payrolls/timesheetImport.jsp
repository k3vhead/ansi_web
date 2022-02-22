<%@ page contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/sql.tld" prefix="sql" %>
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld"  prefix="bean"  %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing" %>
<%@ taglib uri="/WEB-INF/theTagThing.tld" prefix="ansi" %>

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
    
        <style type="text/css">
		#display-div {
			display:none;
		}

		#employee-modal {
			display:none;
		}

		#employee-modal table, th, td {
			border: 0px solid red; 
		}

		#employee-modal input.hours {
			width: 100%;
			text-align: center; 
		}

		input[name="employeeName"] {
			width: 150px;
		}		

		td.money {
			width: 50px;
		}
		#employee-modal input.money {
			width: 100%;
			text-align: right; 
		}

		td.error {
			width: 20px;
			/* border: 10px solid red; */
		}

		td.hours {
			width: 50px;
			/* border: 2px dotted yellow; 	*/


		}

		.err {
			width: 20px;
		}

		#employee-modal input.hours {
			/* width: 100%; */
			text-align: right; 
		}
	
		
		.col-heading{
			text-align: left;
			font-weight: bold;
		}
		
		#employee-data{
			width: 100%;
		}

		#employee-data td.row{
		   width: 50px;
		}

		#employee-data td.state{
		   width: 50px;
		}

		#employee-data input {
			width: 100%;
		}

		#time-calcs{
			width: 50%;
			margin-top: 20px;
		}

		#time-calcs td.total{
			border-top : double 2px;
		}

		#prod-calcs{
			width: 100%;
			margin-top: 20px;
		}

		
		#prod-calcs input.money {
			width: 100%; 
			/* border: 10px solid green; 	*/
		}
		
		#prod-calcs td.money {
			width: 50px;
			/* border: 10px solid green; */
		}

		#prod-calcs td.percentage {
			width: 20px;
			/* border: 10px solid green; */
		}

		#prod-calcs input.percentage {
			width: 100%;
			/* border: 10px solid green; */
		}

		#filter-container {
			width:402px;
			float:right;
		}			
		#organization-display {
			display:none;
		}
		#organization-display table {
			width:100%;
			border:solid 1px #404040;
		}
		#organization-display th {
			text-align:left;
		}
		#organization-edit table {
			width:100%;
			border:solid 1px #404040;
		}
		#organization-edit th {
			text-align:left;
		}
		.action-link {
			text-decoration:none;
			cursor:pointer;
		}
		.dataTables_wrapper {
			padding-top:10px;
		}	
		.details-control {
			cursor:pointer;
		}
		.form-label {
			font-weight:bold;
			white-space: nowrap;
		}

		td .pay {
			margin: auto;
		}
		.org-status-change {
			display:none;
			cursor:pointer;
		}
		.view-link {
			color:#404040;
		}		
        </style>
        <script type="text/javascript">    
	       	$(document).ready(function(){
	           	;TIMESHEET_IMPORT = {
	           		statusIsGood : '<webthing:checkmark>No Errors</webthing:checkmark>',
	           		statusIsBad : '<webthing:ban>Error</webthing:ban>',
	           		saveButton : '<webthing:save>Save</webthing:save>',
	           		edit : '<webthing:edit>Edit</webthing:edit>',
	           		view : '<webthing:view styleClass="details-control">Details</webthing:view>',
	           		employeeMap : {},

	           		init : function() {
	           			TIMESHEET_IMPORT.makeClickers();            			
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
	
	           			
	   					//{ title : "Expenses", "defaultContent": "", data:'expenses' },
	   					//{ title : "OT Hours", "defaultContent": "", data:'otHours' },
	   					//{ title : "OT Pay", "defaultContent": "", data:'otPay' },
	   					//{ title : "Vacation Hours", "defaultContent": "", data:'vacationHours' },
	   					//{ title : "Vacation Pay", "defaultContent": "", data:'vacationPay' },
	   					//{ title : "Holiday Hours", "defaultContent": "", data:'holidayHours' },
	   					//{ title : "Holiday Pay", "defaultContent": "", data:'holidayPay' },
	   					//{ title : "Gross Pay", "defaultContent": "", data:'grossPay' },
	   					//{ title : "Expenses Submitted", "defaultContent": "", data:'expensesSubmitted' },
	   					//{ title : "Expenses Allowed", "defaultContent": "", data:'expensesAllowed' },
	   					//{ title : "Volume", "defaultContent": "", data:'volume' },
	   					//{ title : "Direct Labor", "defaultContent": "", data:'directLabor' },
	   					//{ title : "Productivity", "defaultContent": "", data:'productivity' },
	
	           		},
	           		       		
	           		makeClickers : function() {
	           			$("#open-button").click(function($event) {
	           				$("#prompt-div .err").html("");
	           				var file = document.getElementById('timesheet-file').files[0];
	           				var reader = new FileReader();
	           				if ( file == null ) { 
								$("#prompt-div .timesheetFileErr").html("Required Value").show();
								//if ( $("#prompt-div select[name='divisionId']").val().length == 0) { $("#prompt-div .divisionIdErr").html("Required Value").show(); }
								//if ( $("#prompt-div input[name='payrollDate']").val().length == 0 ) {$("#prompt-div .payrollDateErr").html("Required Value").show(); }
								//if ( $("#prompt-div select[name='state']").val().length == 0 ) {$("#prompt-div .stateErr").html("Required Value").show(); }
								//if ( $("#prompt-div input[name='city']").val().length == 0 ) {$("#prompt-div .cityErr").html("Required Value").show(); }
	           				} else {
		           				reader.readAsText(file, 'UTF-8');	           				
		           				reader.onload = TIMESHEET_IMPORT.saveFile;
		           				// reader.onprogress ...  (progress bar)
	           				}
	           			});
	           			
	           			$("#display-div input[name='cancelButton']").click(function($event) {
	           				$("#display-div").hide();
	           				$("#prompt-div").show();
	           				$("#prompt-div .timesheet-file").val('');
	           			});
	           		},
	           		           		           		
	           		processUploadFailure : function($data) {
	           			console.log("processUploadFailure");
	           			$("#prompt-div .err").html("");
	           			$.each($data.data.webMessages, function($index, $value) {
	           				var $selector = "#prompt-div ." + $index + "Err";
	           				$($selector).html($value[0]).show();
	           			});
	           		},
	           		           		
	           		processUploadSuccess : function($data) {
	           			console.log("processUploadSuccess");
	           			$("#prompt-div").hide();
	           			$("#display-div").show();
	           			console.log("showing display div.. ");
	           			console.log($data);
	           			console.log($data.data.division);
	           			$("#display-div .divisionId").html($data.data.division);
	           			$("#display-div .operationsManagerName").html($data.data.operationsManagerName);           			
	           			$("#display-div .payrollDate").html($data.data.weekEnding);
	           			$("#display-div .state").html($data.data.state);
	           			$("#display-div .city").html($data.data.city);
	           			$("#display-div .timesheetFile").html($data.data.fileName);
	
	           			// create and populate dicttionary object for use in model
	           			var dictionary = $data.data.employeeRecordList;
	   
	           			// populate the visible table on-screen
	           			var $table = $("#timesheet").DataTable({
	           				aaSorting : [[0,'asc']],
	            			processing : true,
	           				data : $data.data.employeeRecordList,
	           				searching : true,
	            	        searchDelay : 800,
	            	        paging: false,
	            	        destroy: true,
	           				columnDefs : [
	             	            { orderable : true, "targets": -1 },
	             	            //{ className : "dt-head-center", "targets":[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16]},
	            	            { className : "dt-left", "targets": [1] },
	            	            { className : "dt-center", "targets": [2,3,5,7] },
	            	            //{ className : "dt-right", "targets": [2,3,4,5,6,7,8,9,10,11,12,13,14,15,16]}
	            	            { className : "dt-right", "targets": [0,4,6,8]}
	            	         ],
	           				columns : [
	           					{ title : "Row", "defaultContent": "", data:'row' },
	           					{ title : "Employee Name", "defaultContent": "", data:'employeeName' },
	           					{ title : "Status", "defaultContent":"", 
	           						data : function(row, type, set) {
										var $tag = TIMESHEET_IMPORT.statusIsGood;          							
	           							if ( row.errorsFound == true ) { $tag = TIMESHEET_IMPORT.statusIsBad; }
	           							return $tag;
	           						}
	           					},
	           					{ title : "Reg Hrs", "defaultContent": "", data:'regularHours' },
	           					{ title : "Reg Pay", "defaultContent": "", data:'regularPay' },
	           					{ title : "OT Hrs", "defaultContent": "", data:'otHours' },
	           					{ title : "OT Pay", "defaultContent": "", data:'otPay' },
	           					{ title : "Vac Hrs", "defaultContent": "", data:'vacationHours' },
	           					{ title : "Vac Pay", "defaultContent": "", data:'vacationPay' },

	           					{ title : "Hol Hrs", "defaultContent": "", data:'holidayHours' },
	           					{ title : "Hol Pay", "defaultContent": "", data:'holidayPay' },
	           					{ title : "Direct Labor", "defaultContent": "", data:'directLabor' },
	           					{ title : "Volume", "defaultContent": "", data:'volume' },
	           					{ title : "Gross Pay", "defaultContent": "", data:'grossPay' },
	           					{ title : "Exp", "defaultContent": "", data:'expenses' },
	           					{ title : "Exp All'd", "defaultContent": "", data:'expensesAllowed' },
	           					{ title : "Exp Smt'd", "defaultContent": "", data:'expensesSubmitted' },
	           					{ title : "Prod %", "defaultContent": "", data:'productivity' },
	           					
	           					{ title : "Action", 
	    			            	data: function ( row, type, set ) { 
	    			            		var $editLink = '<span class="action-link edit-link" data-id="'+row.row+'">' + TIMESHEET_IMPORT.edit + '</span>';
	    			            		//var $viewLink = '<span class="action-link view-link" data-id="'+row.row+'">' + TIMESHEET_IMPORT.view + '</span>';
	    			            		//var $deleteLink = '<span class="action-link delete-link" data-id="'+row.employee_code+'" data-name="'+row.employee_name+'"><webthing:delete>Delete</webthing:delete></span>';
	    			            		return  $editLink + TIMESHEET_IMPORT.saveButton;
	    			            	} },
	           				],
	           				"initComplete": function(settings, json) {
       			            	var myTable = this;
       			            	// You're going to need this to add the field-level filters:
       			            	//LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#timesheetLookup", TIMESHEETLOOKUP.makeTimesheetLookup);

       			            	$.each($data.data.employeeRecordList, function($index, $value) {
       			            		
       			            		//console.log($value.row + ":" + $value.employeeName);
       			            		TIMESHEET_IMPORT.employeeMap[$value.row] = $value;
       			            		
       			            	});
       			            },
	           				drawCallback : function( settings ) {
	           					$(".edit-link").off("click");
	           					$(".edit-link").on("click", function() {
	           						var $rowNumber = $(this).attr('data-id');
	           						TIMESHEET_IMPORT.showEmployeeModal($rowNumber, "edit");
	           					});
	           				}
	           			});
	           		},
	        
	           		saveFile : function($event) {
	           			var results = $event.target.result;
	           			var fileName = document.getElementById('timesheet-file').files[0].name;
	           			var formData = new FormData();
	           			var file = document.getElementById('timesheet-file').files[0];
	           			formData.append('timesheetFile',file, fileName);
	           			//formData.append('divisionId', $("#prompt-div select[name='divisionId']").val());
	           			//formData.append('payrollDate', $("#prompt-div input[name='payrollDate']").val());
	           			//formData.append('state', $("#prompt-div select[name='state']").val());
	           			//formData.append('city', $("#prompt-div input[name='city']").val());
	           			
	           			var xhr = new XMLHttpRequest();
	           			xhr.open('POST',"payroll/timesheetImport", true);
	           			
	           			xhr.onload = function() {
	           				if ( xhr.status == 200 ) {
	           					var $data = JSON.parse(this.response);
	           					if ( $data.responseHeader.responseCode == "EDIT_FAILURE") {
	           						TIMESHEET_IMPORT.processUploadFailure($data);
	           					} else if ( $data.responseHeader.responseCode == "SUCCESS" ) {
	           						TIMESHEET_IMPORT.processUploadSuccess($data);
	           						console.log($data.data.Division);           						
	           					} else {
	           						$("#globalMsg").html("Invalid response code " + $data.responseHeader.responseCode + ". Contact Support");
	           					}
	           				} else {
	           					$("#globalMsg").html("Response Code " + xhr.status + ". Contact Support");
	           				}
	           			};
	           			
	           			xhr.send(formData);
	           		},	           		
	           		showEmployeeModal : function($rowNumber, $action) {
	           			console.log("showEmployeeModal: " + $rowNumber + " " + $action);
	           			$edit = $action == 'edit';
	           			//var $message = "";
	           			//$.each( TIMESHEET_IMPORT.employeeMap[$rowNumber], function($index, $value) {
	           			//	$message = $message + "\n" + $index + "|" + $value;
	           			//});
	           			//alert($message);
	      	           			
	        			$("#employee-modal").dialog({
	        				title:'Employee Timesheet',
	        				autoOpen: false,
	        				height: 420,
	        				width: 750,
	        				modal: true,
	        				closeOnEscape:true,
	        				open: function(event, ui) {
	        					$(".ui-dialog-titlebar-close", ui.dialog | ui).show();
	        				},
	        				buttons: [
	        					{
	        						id:  "employee-model-cancel-button",
	        						click: function($event) {
	        							$("#employee-modal").dialog("close");      							
	        						}
	        					},{
	        						id:  "employee-model-save-button",
	        						click: function($event) {
	        							TIMESHEET_IMPORT.saveEmployeeModal();
	        						}
	        					}
	        				]
	        			});
	        			$("#employee-modal").dialog("open");
	        			$("#employee-model-cancel-button").button('option', 'label', 'Cancel');
	        			$("#employee-model-save-button").button('option', 'label', 'Save');
						//$("#pe-lookup-cancel-button").button('option', 'label', 'Done');
	        			//$("#employee-modal").attr("tabIndex", "18");
	        			//$("#employee-modal").attr("tabIndex", "19");
	        			//BUDGETCONTROL.makeEmployeeAutoComplete("#bcr_new_claim_modal input[name='employee']");
	        			//BUDGETCONTROL.saveOnEnter();  
	        			TIMESHEET_IMPORT.populateEmployeeModal($rowNumber);
	        		},
	        		populateEmployeeModal : function($rowNumber) {
	           			console.log("populateEmployeeModal: " + $rowNumber + " ");
	           			console.log(TIMESHEET_IMPORT.employeeMap[$rowNumber])
	           			console.log(TIMESHEET_IMPORT.employeeMap[$rowNumber].employeeName);
	           			
	           			$('[name="row"]').val(TIMESHEET_IMPORT.employeeMap[$rowNumber].row);
	           			$('[name="employeeName"]').val(TIMESHEET_IMPORT.employeeMap[$rowNumber].employeeName);
						// status
	           			$('[name="regularHours"]').val(TIMESHEET_IMPORT.employeeMap[$rowNumber].regularHours);
	           			$('[name="regularPay"]').val(TIMESHEET_IMPORT.employeeMap[$rowNumber].regularPay);
	           			$('[name="otHours"]').val(TIMESHEET_IMPORT.employeeMap[$rowNumber].otHours);
	           			$('[name="otPay"]').val(TIMESHEET_IMPORT.employeeMap[$rowNumber].otPay);
	           			$('[name="vacationHours"]').val(TIMESHEET_IMPORT.employeeMap[$rowNumber].vacationHours);
	           			$('[name="vacationPay"]').val(TIMESHEET_IMPORT.employeeMap[$rowNumber].vacationPay);
						
	           			//these columns not on grid
	           			$('[name="holidayHours"]').val(TIMESHEET_IMPORT.employeeMap[$rowNumber].holidayHours);
	           			$('[name="holidayPay"]').val(TIMESHEET_IMPORT.employeeMap[$rowNumber].holidayPay);
	           			
	           			
	           			$('[name="directLabor"]').val(TIMESHEET_IMPORT.employeeMap[$rowNumber].directLabor);
	           			$('[name="volume"]').val(TIMESHEET_IMPORT.employeeMap[$rowNumber].volume);
	           			$('[name="grossPay"]').val(TIMESHEET_IMPORT.employeeMap[$rowNumber].grossPay);

	           			$('[name="expenses"]').val(TIMESHEET_IMPORT.employeeMap[$rowNumber].expenses);
	           			$('[name="expensesAllowed"]').val(TIMESHEET_IMPORT.employeeMap[$rowNumber].expensesAllowed);
	           			$('[name="expensesSubmitted"]').val(TIMESHEET_IMPORT.employeeMap[$rowNumber].expensesSubmitted);
	           			$('[name="productivity"]').val(TIMESHEET_IMPORT.employeeMap[$rowNumber].productivity);

	           			$('[name="state"]').val(TIMESHEET_IMPORT.employeeMap[$rowNumber].state);
	           			//blankRow: false
	           			//errorsFound: null
	           			//$rowNumber, $action
	           		},
	        		
	           		closeEmployeeModal : function() {
	           			//$rowNumber, $action
	           		},
	           		saveEmployeeModal : function() {
	           			alert("Do Stuff here to store the changes");
	           			console.log("saveEmployeeModal: ")
	           		},
	           	};
	           	
	           	TIMESHEET_IMPORT.init();
	            	
	        });        		
        </script>        
    </tiles:put>
    
    <tiles:put name="content" type="string">
    	<h1>Payroll Timesheet Import</h1> 
    	<div id="prompt-div">
    		<table>
    			<tr>
    				<td><span class="form-label">Payroll File:</span></td>
    				<td>
    					<input type="file" id="timesheet-file" name="files[]" />
    				</td>
    				<td><span class="timesheetFileErr err"></span></td>
    			</tr>
    			<tr>
    				<td colspan="2" style="text-align:center;"><input type="button" value="Open" id="open-button" /></td>
    			</tr>    			
    		</table>
    	</div>

		<div id="display-div">
			<table style="width:100%;">
    			<tr>
    				<td><span class="form-label">Division:</span></td>
    				<td><span class="form-label">Operations Manager:</span></td>
    				<td><span class="form-label">Week Ending:</span></td>
    				<td><span class="form-label">State:</span></td>
    				<td><span class="form-label">City/Jurisdiction:</span></td>
    				<td><span class="form-label">Payroll File:</span></td>
    				<td rowspan="2">
    					<input type="button" value="Cancel" name="cancelButton" class="action-button" />
    					<input type="button" value="Save" id="open-button" />
    				</td>
    			</tr>
    			<tr>
    				<td><span class="divisionId"></span></td>
    				<td><span class="operationsManagerName"></span></td>
    				<td><span class="payrollDate"></span></td>
    				<td><span class="state"></span></td>
    				<td><span class="city"></span></td>
    				<td><span class="timesheetFile"></span></td>
    			</tr>
    			<tr>
    			</tr>    			
    		</table>
			<table id="timesheet">
			</table>
		</div>
		
		
		
		
		<div id="employee-modal">
			<div style="width:100%; height:0px;">
				<span class="employeeEditErr err"></span>
			</div>	    	

			<table  id="employee-data">   
				<tr>
					<td class="form-label">Employee Name :</td>
					<td class="employeeName"><input type="text" class="employeeName" Name="employeeName"tabindex="1" /></td>
					<td class="err"><span class="employeeNameErr err"></span></td>

					<td class="form-label">State:</td>
					<td class="state"><input type="text" name="state" 												tabindex="2" /></td>
					<td class="err"><span class="stateErr err"></span></td>

					<td class="form-label"<span class="form-label">Row:</span></td>
					<td class="row"><input type="text" class="row" name="row" 							tabindex="3" /></td>
					<td class="err"><span class="rowErr err"></span></td>						
				</tr>
			</table>

			<table  id="time-calcs">   
				<!-- <table  id="time-calcs"style="width:100%;border:1px solid;">   -->
				<tr>
					<td class="form-label"></td>
					<td colspan="2" class="col-heading hours ">Hours</td>
					<td colspan="2" class="col-heading pay">Pay</td>
				</tr>
												
				<tr>
					<td class="form-label">Regular:</td>
					
					<td class="hours"><input class="hours" type="text" name="regularHours" 				tabindex="4" /></td>
					<td class="err"><span class="regularHoursErr  err"></span></td>
					
					<td class="money"><input  class="money" type="text" name="regularPay" 				tabindex="5" /></td>
					<td class="err"><span class="regularPayErr err"></span></td>
				</tr>
								
				<tr>
					<td class="form-label">Overtime :</td>

					<td class="hours"><input class="hours" type="text" name="otHours" 					tabindex="6" /></td>
					<td class="err"><span class="otHoursErr err"></span></td>
					
					<td class="money"><input class="money" type="text" name="otPay" 					tabindex="7" /></td>
					<td class="err"><span class="otPayErr err"></span></td>
				</tr>
				<tr>
					<td class="form-label">Vacation:</td>

					<td class="hours"><input class="hours" type="text" name="vacationHours"				tabindex="8" /></td>
					<td class="err"><span class="vacationHoursErr"></span></td>

					<td class="money"><input class="money" "type="text" name="vacationPay" 				tabindex="9" /></td>
					<td class="err"><span class="vacationPayErr err"></span></td>
				</tr>
								
				<tr>
					<td class="form-label">Holiday:</td>

					<td class="hours"><input class="hours" type="text" name="holidayHours"				tabindex="10" /></td>
					<td class="err"><span class="holidayHoursErr err"></span></td>

					<td class="money"><input class="money" type="text" name="holidayPay" 				tabindex="11" /></td>
					<td class="err"><span class="holidayPayErr err"></span></td>

				</tr>
								
				<tr>
					<td class="form-label">Total:</td>
					
					<td class="hours total"><input class="hours" type="text" name="totalHours" 				tabindex="12" /></td>
					<td class="err"><span class="totalHoursErr err"></span></td>

					<td class="money total"><input class="money" type="text" name="grossPay" 					tabindex="13" /></td>
					<td class="err"><span class="grossPayErr err"></span></td>
				</tr>							
			</table>

			<table id="prod-calcs">   
				<!-- <table id="prod-calcs" style="width:100%;border:1px solid;">   -->
				<tr>
					<td class="form-label">Direct Labor:</td>
					<td class="money"><input class="money" type="text" name="dlAmt" 					tabindex="14" /></td>
					<td class="err"><span class="dlAmtErr err"></span></td>

					<td class="form-label">Volume :</td>
					<td class="money"><input class="money" type="text" name="volume" 					tabindex="15" /></td>
					<td class="err"><span class="volumeErr err"></span></td>

					<td class="form-label">Productivity:</td>
					<td class="percentage"><input class="percentage" type="text" name="productivity"	tabindex="16" /></td>
					<td class="err"><span class="productivityErr err"></span></td>
				</tr>

				<tr>
					<td class="form-label">Expenses:</td>
					<td class="money"><input class="money" type="text" name="expenses" 					tabindex="17" /></td>
					<td class="err"><span class="expensesErr err"></span></td>

					<td class="form-label">Expenses Submitted:</td>
					<td class="money"><input class="money" type="text" name="expensesSubmitted" 		tabindex="18" /></td>
					<td class="err"><span class="expensesSubmittedErr err"></span></td>

					<td class="form-label">Expenses Allowed:</td>
					<td class="money"><input class="money" type="text" name="expensesAllowed" 			tabindex="19" /></td>
					<td class="err"><span class="dlAmtErr err"></span></td>
				</tr>
			</table>
		</div>				
    </tiles:put>
</tiles:insert>
