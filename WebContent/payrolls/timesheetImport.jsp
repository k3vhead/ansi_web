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
        
        
        #data-detail{
			/* border: 2px solid red; */ 
			width:100%;
        }
        
        #data-detail #timesheet{
			/* border: 2px solid blue; */ 
			width:100%;
        }
        
        #data-file{
        	/* margin-bottom : 20px; */
        	display:none;
        }
        
		#data-header {
			display:none;
			width : 1000px;
			/* border: 2px solid red; */ 
		}		
		
		#data-header table {
			/* width: 100%; */
		}

		/* division */
		#data-header table td.col1{ 	
			width:60px;
			/* border: 2px solid yellow; */ 
		}		

		
		/* Operations Manager Name */
		#data-header table td.col2{
			width:150px;
			/* border: 2px solid red; */ 
		}		
		
		/* Week ending */
		#data-header table td.col3{
			width: 75x;
		}		

		input[name="payrollDate"] {
			width: 75px;
		}		


		/* State */
		#data-header table td.col4{
			width:50px; */
			/* text-align: right; */
			/* border: 2px solid blue;*/  
		}		
	
		/* City/Juristiction  */
		#data-header table td.col5{
			width:140px; 
			/* text-align: right; */
			/* border: 2px solid blue;*/  
		}		

		/* Payroll File */
		#data-header table td.col6{
			width:200px; 
			/* text-align: right; */
			/* border: 2px solid blue; */ 
		}		
	
		/* Buttons   */
		#data-header table td.col7{
			width: 150px; */
			text-align: right;
			/* border: 2px solid blue;*/  
		}		
	
		#file-header-data table td.col1{ 	
			width:60px;
			/* border: 2px solid yellow; */ 
		}		

	
		td #cancel-save-buttons{
			text-align: right;
		}	
	
		#data-header table td #operations-manager-label{
		 	/* width:200px; */
		}		
		
		#data-header table td #week-ending-label{
			/* width:200px; */
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

		input[name="divisionId"] {
			width: 50px;
		}		

		input[name="state"] {
			width: 50px;
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
		#prompt-div{
			width: 600px;
		}
		#prompt-div table.prompt{
			width: 900;
		}
		
		/* file-picker-label */
		#prompt-div table.prompt td.col1{
			width: 100;
		}
		
		/* file-picker-input control */
		#prompt-div table.prompt td.col2{
			width: 400;
		}
		
		/* open button */
		#prompt-div table.prompt td.col3{
			width: 100;
		}
		
		/* file-selection-error */
		#prompt-div table.prompt td.col4{
			width: 300;
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
		
		.workingBox {
			width:20px;
			display:inline-block;
		}
		.working {
			display:none;
		}
				
		.thinking {
			display:none;
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
	           		openTheFile : function(){
	           			console.log("openTheFile");
           				
           				$("#prompt-div .err").html("");
           				var file = document.getElementById('timesheet-file').files[0];
           				
	           			if (typeof file !== 'undefined'){
	           				var fileName = document.getElementById('timesheet-file').files[0].name;
	           				var reader = new FileReader();
							console.log("openTheFile kjw: Selected File was : " + file);
		           			console.log("openTheFile kjw: Selected File was : " + fileName);

		           			reader.readAsText(file, 'UTF-8');	           				
	           				reader.onload = TIMESHEET_IMPORT.openFile;		           				
	           				$("#prompt-div").hide();
	           				$(".thinking").show();		           				
		           			console.log(file);
	           				// reader.onprogress ...  (progress bar)	  
	           			} else{
		           			console.log("No file selected");
							$("#prompt-div .timesheetFileErr").html("Please select a file to open").show().fadeOut(3000);
							//if ( $("#prompt-div select[name='divisionId']").val().length == 0) { $("#prompt-div .divisionIdErr").html("Required Value").show(); }
							//if ( $("#prompt-div input[name='payrollDate']").val().length == 0 ) {$("#prompt-div .payrollDateErr").html("Required Value").show(); }
							//if ( $("#prompt-div select[name='state']").val().length == 0 ) {$("#prompt-div .stateErr").html("Required Value").show(); }
							//if ( $("#prompt-div input[name='city']").val().length == 0 ) {$("#prompt-div .cityErr").html("Required Value").show(); }
	           			}
	           		},    		
	           		makeClickers : function() {
	           			console.log("Creating Button Click events..");
	           			$("#open-button").click(
	           				function($event) { 
	    	           			console.log("this is inside the open-button click event");
		           				/* $("#open-button").off("click"); */
		           				TIMESHEET_IMPORT.openTheFile();
		           				/* $("#open-button").on("click"); */
		           			});
	           			
	           			$("#data-header input[name='cancelButton']").click(function($event) {
	           				TIMESHEET_IMPORT.initializeDisplay();
	           			});
	           		},
	           		           		           		
	           		processUploadFailure : function($data) {
	           			$("#prompt-div .err").html("");
	           			var fName = document.getElementById('timesheet-file').files[0].name;
	           			$(".thinking").hide();		           				
           				$("#prompt-div").show();
						$("#prompt-div .timesheetFileErr").html(fName + " is not a Timesheet File").show().fadeOut(10000);
           				
	           			 var $el = $('#timesheet-file');
	                     $el.wrap('<form>').closest(
	                       'form').get(0).reset();
	                     $el.unwrap();           				           				
	           		},
	           		           
	           		initializeDisplay: function(){
	           			console.log("TIMESHEET_IMPORT.initializeDisplay: function - begin");	           			

           				$("#prompt-div").show();
           				$("#prompt-div .timesheet-file").val('');

           				$("#data-header").hide();
	           			//$("#data-header .divisionId").html("");
	           			//$("#data-header .operationsManagerName").html("");           			
	           			//$("#data-header .payrollDate").html("");
	           			//$("#data-header .state").html("");
	           			//$("#data-header .city").html("");
	           			//$("#data-header .timesheetFile").html("");

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
	           		processUploadSuccess : function($data) {
	           			console.log("processUploadSuccess");
	           			$("#prompt-div").hide();
	           			$("#data-header").show();
	           			console.log("showing display div.. ");
	           			console.log($data);
	           			console.log($data.data.division);
           				$(".thinking").hide();

           				//$('select[name="divisionId"]').val($data.data.divisionId);
	           			$('select[name="divisionId"]').val("12-IL02").change();
	           			$('input[name="operationsManagerName"]').val($data.data.operationsManagerName);           			
	           			$('input[name="payrollDate"]').val($data.data.weekEnding);
	           			$('input[name="payrollDate"]').datepicker({
			                prevText:'&lt;&lt;',
			                nextText: '&gt;&gt;',
			                showButtonPanel:true
			            });	           			
	           			$('input[name="state"]').val($data.data.state);
	           			$('input[name="city"]').val($data.data.city);
	           			$("#data-header .timesheetFile").html($data.data.fileName);

	           			/*
	           			$("#data-header input .divisionId").html($data.data.division);
	           			$("#data-header .operationsManagerName").html($data.data.operationsManagerName);           			
	           			$("#data-header .payrollDate").html($data.data.weekEnding);
	           			$("#data-header .state").html($data.data.state);
	           			$("#data-header .city").html($data.data.city);
	           			$("#data-header .timesheetFile").html($data.data.fileName);
	           			*/
	           			
	           			// create and populate dicttionary object for use in modal
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
	            	        autoWidth: false,
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
	           			$("#data-detail").show();
	           			$("#timesheet").show();
	           		},	        
	           		openFile : function($event) {
	           			var results = $event.target.result;
	           			/*  var fileName = document.getElementById('timesheet-file').files[0].name; */
	           			var file = document.getElementById('timesheet-file').files[0];
	           			var fileName = document.getElementById('timesheet-file').files[0].name;
	           			var formData = new FormData();
	           			
	           			formData.append('timesheetFile',file, fileName);
						console.log("openFile kjw Selected File was : " + file);
	           			console.log("openFile kjw Selected File was : " + fileName);
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
	           			console.log("kjw - openFile :  FormData is : " + formData);
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
	        			console.log("populateEmployeeModal: " + $rowNumber);
	           			console.log(TIMESHEET_IMPORT.employeeMap[$rowNumber]);
	           			console.log(TIMESHEET_IMPORT.employeeMap[$rowNumber].employeeName);
	           			
	           			$('[name="row"]').val(TIMESHEET_IMPORT.employeeMap[$rowNumber].row);
	           			$('[name="employeeName"]').val(TIMESHEET_IMPORT.employeeMap[$rowNumber].employeeName);
						// status
	           			$('[name="regularPay"]').val(TIMESHEET_IMPORT.employeeMap[$rowNumber].regularPay);
	           			$('[name="otPay"]').val(TIMESHEET_IMPORT.employeeMap[$rowNumber].otPay);
	           			$('[name="vacationPay"]').val(TIMESHEET_IMPORT.employeeMap[$rowNumber].vacationPay);
	           			$('[name="holidayPay"]').val(TIMESHEET_IMPORT.employeeMap[$rowNumber].holidayPay);
	           				           		 		
	           			
	    				<!-- <table  id="time-calcs"style="width:100%;border:1px solid;">   -->

	           			
	           			
						
						$('[name="regularHours"]').val(TIMESHEET_IMPORT.employeeMap[$rowNumber].regularHours);
	           			$('[name="otHours"]').val(TIMESHEET_IMPORT.employeeMap[$rowNumber].otHours);
	           			$('[name="vacationHours"]').val(TIMESHEET_IMPORT.employeeMap[$rowNumber].vacationHours);
	           			//these columns not on grid
	           			$('[name="holidayHours"]').val(TIMESHEET_IMPORT.employeeMap[$rowNumber].holidayHours);
	           			
	           			console.log(TIMESHEET_IMPORT.employeeMap[$rowNumber].reglarHours);
        				console.log(TIMESHEET_IMPORT.employeeMap[$rowNumber].otHours);
             			console.log(TIMESHEET_IMPORT.employeeMap[$rowNumber].vacationHours);
                        console.log(TIMESHEET_IMPORT.employeeMap[$rowNumber].holidayHours);

	           			
	           		 	var totHours = 
	           		 		TIMESHEET_IMPORT.employeeMap[$rowNumber].reglarHours +
	           		 		TIMESHEET_IMPORT.employeeMap[$rowNumber].otHours +
	           		 		TIMESHEET_IMPORT.employeeMap[$rowNumber].vacationHours +
	           		 		TIMESHEET_IMPORT.employeeMap[$rowNumber].holidayHours;
	  
	           			$('[name="totalHours"]').val(totHours);
	           			$('[name="totalHours"]').val(37);
	           			
	           			
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
	           			console.log("saveEmployeeModal - grabbing values");
						//var $ticketId = $("#employee-modal").attr("ticketId");
	           				           		
						//<td class="employeeName"><input type="text" class="employeeName" Name="employeeName"tabindex="1" /></td>						
	           			var $rowNumber 		= $("#employee-modal [name='row']").val();	           			
	           			var $employeeName 	= $("#employee-modal [name='employeeName']").val();
	           			var $regularPay  	= $("#employee-modal [name='regularPay']").val();
	           			var $otPay 			= $("#employee-modal [name='otPay']").val();
	           			var $vacationPay  	= $("#employee-modal [name='vacationPay']").val();
	           			var $holidayPay  	= $("#employee-modal [name='holidayPay']").val();
	           			var $regularHours 	= $("#employee-modal [name='regularHours']").val();
	           			var $otHours  		= $("#employee-modal [name='otHours']").val();
	    	           	var $vacationHours 	= $("#employee-modal [name='vacationHours']").val();
	           			var $holidayHours  	= $("#employee-modal [name='holidayHours']").val();
	           			var $directLabor  	= $("#employee-modal [name='directLabor']").val();
	           			var $volume  		= $("#employee-modal [name='volume']").val();
	           			var $grossPay   	= $("#employee-modal [name='grossPay']").val();
	           			var $expenses  		= $("#employee-modal [name='expenses']").val();
	           			var $expensesAllowed = $("#employee-modal [name='expensesAllowed']").val();
	           			var $expensesSubmitted  = $("#employee-modal [name='expensesSubmitted']").val();
	           			var $productivity 	= $("#employee-modal [name='productivity']").val();

//	           			$rowNumber 		= 1;	           			
//	           			$employeeName 	= 2;
// 	           			$regularPay  	= 3;
// 	           			$otPay 			= 4;
// 	           			$vacationPay  	= 5;
// 	           			$holidayPay  	= 6;
// 	           			$regularHours 	= 7;
// 	           			$otHours  		= 8;
// 	    	           	$vacationHours 	= 9;
// 	           			$holidayHours  	= 10;
// 	           			$directLabor  	= 11;
// 	           			$volume  		= 12;
// 	           			$grossPay   	= 13;
// 	           			$expenses  		= 14;
// 	           			$expensesAllowed = 15;
// 	           			$expensesSubmitted  = 16;
// 	           			$productivity 	= 17;
	           				           			
	           			console.log("Edit Val for rowNumber = " 	+ $rowNumber);	           			
	    	           	console.log("Edit Val for employeeName = "   	+ $employeeName);
	    	           	console.log("Edit Val for regularPay = " 		+ $regularPay);
	    	           	console.log("Edit Val for otPay = " 			+ $otPay);
	    	           	console.log("Edit Val for vacationPay = " 		+ $vacationPay);
	    	           	console.log("Edit Val for holidayPay = " 		+ $holidayPay);
	    	           	console.log("Edit Val for regularHours = " 	+ $regularHours);
	    	           	console.log("Edit Val for otHours = " 			+ $otHours);
	    	           	console.log("Edit Val for vacationHours = " 	+ $vacationHours);
	    	           	console.log("Edit Val for holidayHours = " 	+ $holidayHours);
	    	           	console.log("Edit Val for directLabor = " 		+ $directLabor);
	    	           	console.log("Edit Val for volume = " 			+ $volume);
	    	           	console.log("Edit Val for grossPay" 		+ $grossPay);
	    	           	console.log("Edit Val for expenses" 		+ $expenses);
	    	           	console.log("Edit Val for expensesAllowed" 	+ $expensesAllowed);
	    	           	console.log("Edit Val for expensesSubmitted" + $expensesSubmitted);
	    	           	console.log("Edit Val for productivity" 	+ $productivity);
	           				    	           	
	           			console.log("saveEmployeeModal: ");
	           			
	           			var $idx = $rowNumber +1;
	           			$idx = $idx -1;

	           			console.log("Using index number = " + $idx);	           			

	           			
					    var table = $("#timesheet").DataTable();
					    
// 	           			console.log("Removing row = " + $idx);	           			
// 	           			table.row($idx).remove();
	           				           			
	           			console.log("re-adding row? = " + $idx);	           			
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
	           			
	           			//alert("Do Stuff here to store the changes");	           			
// 	           			console.log("Employee name from employeeRecordList = " + $data.data.employeeRecordList[$rowNumber].employeeName);
					    //var $test = "TIMESHEET_IMPORT.employeeMap[" + $rowNumber + "] = " + TIMESHEET_IMPORT.employeeMap[$rowNumber].directLabor;
					 				
					    table.row($rowNumber).data(TIMESHEET_IMPORT.employeeMap[$rowNumber]).draw();			    

					    console.log("test row update");
					    console.log(TIMESHEET_IMPORT.employeeMap[$rowNumber]);
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
    		<form id="file-selection" method="post" enctype="multipart/form-data" >
	    		<table class="prompt">
	    			<tr>
	    				<td class="col1" id="file-picker-label"> 	<span 	class="form-label">Payroll File:						</span>		</td>
	    				<td class="col2" id="file-picker"> 			<input 	type="file" 	id="timesheet-file" name="files[]" />				</td>
	    				<td class="col3" id="open-button-cell"> 	<input 	type="button" 	id="open-button"	value="Open"  /> 				</td>    				
	    				<td class="col4" id="file-picker-err">		<span 	class="timesheetFileErr err">							</span> 	</td>
	    			</tr>
	    		</table>
	    	</form>
    	</div>
 		<div class="thinking"><webthing:thinking style="width:100%" /></div>


  		<div id="data-header">
			<table id="data-file">
    			<tr>			
	  				<td id="payroll-file-label" >		
	  					<span class="form-label">Currently Viewing Payroll File :</span>
	  					<span class="timesheetFile"></span>
	  				</td>
    			</tr>  	
    		</table>
			<table id="file-header-data">
    			<tr class="label-row">
    				<td class="col1" id="division-label" >  			<span class="form-label">Division			</span>   	</td>
    				<td class="col2" id="operations-manager-label" > 	<span class="form-label">Operations Manager	</span>		</td>
    				<td class="col3" id="week-ending-label" >			<span class="form-label">Week Ending		</span> 	</td>
    				<td class="col4" id="state-label" > 				<span class="form-label">State				</span>		</td>
    				<td class="col5" id="city-label" >					<span class="form-label">City/Jurisdiction	</span> 	</td>
    				<td class="col6" id="payroll-file-label" >			<span class="form-label">Payroll File		</span> 	</td>
    				<td class="col7">									<span class="form-label">					</span>		</td>
    			</tr>
			    <tr>
    				<td class="col1">
    					<span class="divisionId">
    						<select name="divisionId">
								<option value=""></option>
								<ansi:divisionSelect format="select"/>
							</select>
    					</span>
    				</td>
						<!--								
    					<input type="text" class="division" 				name="divisionId"				tabindex="1" />
    					-->								
    				<td class="col2"><span class="operationsManagerName">	<input type="text" class="operationsManagerName" 	name="operationsManagerName"	tabindex="2" />					</span></td>
    				<td class="col3"><span class="payrollDate">				<input type="text" class="payrollDate" 				name="payrollDate"				tabindex="3" />							</span></td>
					<!-- 
   					<td class="col4"><span class="ansi:states">				<input type="text" class="state" 					name="state"					tabindex="4" />									</span></td>
					 -->
   					<td class="col4">
   						<span class="state">
		   					<select id="state">
		   						<webthing:states /> 
							</select>
   					     </span>
   					</td>
    				<td class="col5"><span class="city">					<input type="text" class="city" 					name="city"						tabindex="5" />							</span></td>
    				<td class="col6"><span class="timesheetFile"></span></td>
    				<td class="col7" id="cancel-save-buttons">
    					<input type="button" value="Cancel" name="cancelButton" class="action-button" />
    					<input type="button" value="Save" id="open-button" />
    				</td>
    			</tr>
<!--     			<tr>
    				<td class="col1"><span class="divisionId">				</span></td>
    				<td class="col2"><span class="operationsManagerName">	</span></td>
    				<td class="col3"><span class="payrollDate">				</span></td>
    				<td class="col4"><span class="state">					</span></td>
    				<td class="col5"><span class="city">					</span></td>
    				<td class="col6"><span class="timesheetFile">			</span></td>
    				<td class="col7" id="cancel-save-buttons">
    					<input type="button" value="Cancel" name="cancelButton" class="action-button" />
    					<input type="button" value="Save" id="open-button" />
    				</td>
    			</tr>
 -->    		</table>
		</div>

		<div id="data-detail">
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
					<td class="employeeName"><input type="text" class="employeeName" name="employeeName"tabindex="1" /></td>
					<td class="err"><span class="employeeNameErr err"></span></td>

					<td class="form-label">State:</td>
					<td class="state"><input type="text" name="state" 												tabindex="2" /></td>
					<td class="err"><span class="stateErr err"></span></td>

					<td class="form-label"><span class="form-label">Row:</span></td>
					<td class="row"><input type="text" class="row" name="row" 							tabindex="3" /></td>
					<td class="err"><span class="rowErr err"></span></td>						
				</tr>
			</table>

			<table  id="time-calcs">   
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

					<td class="money"><input class="money" type="text" name="vacationPay" 				tabindex="9" /></td>
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
					<td class="money"><input class="money" type="text" name="directLabor" 					tabindex="14" /></td>
					<td class="err"><span class="directLaborErr err"></span></td>

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
					<td class="err"><span class="expensesAllowedErr err"></span></td>
				</tr>
			</table>
		</div>				
    </tiles:put>
</tiles:insert>
