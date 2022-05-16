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
	        #data-file{
	        	/* margin-bottom : 20px; */
	        	display:none;
	        }
			#data-header {
				display:none;
			}		
			#employee-modal {
				display:none;
			}

			/* #employee-data */
			#employee-modal #employee-data {
				margin-bottom : 20px;
			 	 /* border : solid red 3px; */ 
			}

			#employee-modal #employee-data .spacer {
				width : *;
			}

			#employee-modal #employee-data .row-number-label{
				margin-left : 20px;
			}
												
			#employee-modal table #time-calcs .row-label{
			   Width = 100px;
			}
			
			#employee-modal .row-label-col-2{
			   Width : 110px;
			   /* border : dotted red 3px; */ 
			}
																				
			#employee-modal #time-calcs .spacer{
				width : 5px;
			 	/* border : dashed red 3px; */ 
			}
																	/* modal hours area - time calcs*/
			#employee-modal #time-calcs{
			 	width: 485px;			 	
			 	/* border : solid red 1px; */ 
			}		
			#employee-modal #time-calcs td.hours {
        		width:85px;
			}
			#employee-modal #time-calcs td.hours input.hours {
				width: 75px; 
				text-align: right; 
			}

			#employee-modal #time-calcs td.hoursErr {
        		width:40px;
			}			

																	/* modal money fields area - time calcs*/
			td.money {
        		width:  100px;
        		/* border: 1px black solid; */
			}

			#employee-modal #time-calcs input.money {
			    width: 80px;
			    text-align: right;
			}
			/*
			#employee-modal #time-calcs td.moneyErr {
        		 width:40px; 
			}
			*/
			
			#employee-modal #time-calcs td.percentage {
			    /* width: 120px; */
			    /* text-align: right; */ 
			    /* padding-right: 20px;*/			    
			}			
			/*
			#employee-modal #time-calcs td.percentageErr {
        		width:40px;
			}
			*/					
			#employee-modal #time-calcs input.percentage {
			    width: 80px;
			    text-align: right;
			}
						

			
			/* duplicate rule
			#employee-modal #time-calcs td.money input.money {
				width: 100%; 
				text-align: right; 
			}
			*/
			
			
			/*prod-calcs] */
			
			/* modal productivity and expense area */ 
			
			/* eliminated div 
			#employee-modal #prod-calcs td.money {
        		width:90px;
			}
			#employee-modal #prod-calcs td.money input.money {
				width: 100%; 
				text-align: right; 
			}
			#employee-modal #prod-calcs td.moneyErr {
        		width:40px;
			}
			
			*/
			
			#filter-container {
        		width:402px;
        		float:right;
        	}
			
			.col-heading{
				text-align: left;
				font-weight: bold;
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
	           			TIMESHEET_IMPORT.setupCityTypeAhead();	           				           			
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
	           		setupCityTypeAhead : function(){
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
	           		processUploadFailure : function($data) {
	           			console.log("processUploadFailure");
	           			$("#prompt-div .err").html("");
	           			var fName = document.getElementById('timesheet-file').files[0].name;
	           			$(".thinking").hide();		           				
           				$("#prompt-div").show();
           				
       					$("#data-header .err").html("");
           				var $fileMessages = $data.data.webMessages.timesheetFile;
           				if ( $fileMessages == null ) {
           					$("#prompt-div").hide();
           					TIMESHEET_IMPORT.processUploadWarning($data);
           					TIMESHEET_IMPORT.displayHeaderData($data);
           					TIMESHEET_IMPORT.populateDataGrid($data);           					
           				} else {           					
           					$("#prompt-div .timesheetFileErr").html($fileMessages[0]).show().fadeOut(10000);
	   	           			 //var $el = $('#timesheet-file');
	   	                     //$el.wrap('<form>').closest('form').get(0).reset();
	   	                     //$el.unwrap();	   	                     
           				}
           				
						           				           				
	           		},
	           		
	           		processUploadWarning : function($data) {
	           			console.log("processUploadWarning");
	           			
	           			$("#data-header").show();
	           			$("#file-header-data .err").html("");
	           			$.each($data.data.webMessages, function($index, $value) {
	           				var $destination = "#file-header-data ." + $index + "Err";
	           				var $message = $value[0];
	           				console.log($destination + " " + $message);
	           				$($destination).html($message);
	           			});	           			      				           				
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
	           		displayHeaderData : function($data) {
	           			console.log("displayHeaderData");
	           			var $disp_divisionId="";
	           			var $disp_operationsManagerName="";
	           			var $disp_weekEnding="";
	           			var $disp_state="";
	           			var $disp_city="";

	           			if(($data.data.normal.divisionId == null) && ($data.data.divisionId != null)){
	           				$disp_divisionId= $data.data.divisionId;
	           			} else {
	           				$disp_divisionId= $data.data.normal.divisionId;
	           			}
	           			
	           			if(($data.data.normal.operationsManagerName == null) && ($data.data.operationsManagerName != null)){
	           				$disp_operationsManagerName= $data.data.operationsManagerName;
	           			} else {
	           				$disp_operationsManagerName= $data.data.normal.operationsManagerName;
	           			}
	           			
	           			if(($data.data.normal.weekEnding == null) && ($data.data.weekEnding != null)){
	           				$disp_weekEnding= $data.data.weekEnding;
	           			} else {
	           				$disp_weekEnding= $data.data.normal.weekEnding;
	           			}
	           			
	           			if(($data.data.normal.state == null) && ($data.data.state != null)){
	           				$disp_state= $data.data.state;
	           			} else {
	           				$disp_state= $data.data.normal.state;
	           			}
	           			
	           			if(($data.data.normal.city == null) && ($data.data.city != null)){
	           				$disp_city= $data.data.city;
	           			} else {
	           				$disp_city= $data.data.normal.city;
	           			}
	           				 
	           			
	           			var $formattedWeekendingDate = new Date($disp_weekEnding).toISOString().slice(0, 10);	           			
	           			
	           			console.log("\n disp_divisionId = " + $disp_divisionId
           				+	"\n disp_operationsManagerName = " + $disp_operationsManagerName 
           				+	"\n disp_weekEnding=" + $formattedWeekendingDate
           				+	"\n disp_state=" +  $disp_state 
           				+	"\n disp_city=" +  $disp_city);
	           				           		
	           			$('select[name="divisionId"]').val($disp_divisionId)
	           			$('input[name="operationsManagerName"]').val($disp_operationsManagerName);           			
	           			$('input[name="payrollDate"]').val($formattedWeekendingDate);
	           			$('select[name="state"]').val($disp_state);
	           			$('input[name="city"]').val($disp_city);
	           			$("#data-header .timesheetFile").html($data.data.fileName);	           				           		
	           		},
	           		
	           		
	           		
	           		populateDataGrid : function($data){
	           			// create and populate dicttionary object for use in modal
	           			var dictionary = $data.data.employeeRecordList;
					    var $errorFound = '<payroll:errorFound>Invalid Value</payroll:errorFound>';
	           			
	   
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
	             	            //{ className : "dt-head-center", "targets":[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16]},
	            	            { className : "dt-left", "targets": [1] },
	            	            { className : "dt-center", "targets": [17,18] },
	            	            //{ className : "dt-right", "targets": [2,3,4,5,6,7,8,9,10,11,12,13,14,15,16]}
	            	            { className : "dt-right", "targets": [2,3,4,5,6,7,8,9,10,11,12,13,14,15,16]}
	            	            
	            	            	            	      	            	             
	            	         ],
	           				columns : [
	           					{ title : "Row", searchable:true, "defaultContent": "", data:'row' },
	           					{ title : "Employee Name", searchable:true, "defaultContent": "", data:'employeeName' },
	           					{ title : "Reg Hrs", searchable:true, "defaultContent": "", 
	           						data: function ( row, type, set ) {
	           						    value = null;
	           						    if ( row.regularHours == null || row.regularHours == "" ) {
	           						        value="";
	           						    } else if ( isNaN(row.regularHours) ) {
	           						        value=$errorFound;
	           						    } else {
	           						    	x = parseFloat(row.regularHours);
	           						        value = x.toFixed(2);
	           						    }
	           						    return value;
	           						}},
	           					{ title : "Reg Pay", searchable:true, "defaultContent": "", data:'regularPay' },
	           					{ title : "Exp", searchable:true, "defaultContent": "", data:'expenses' },
	           					{ title : "OT Hrs", searchable:true, "defaultContent": "", 
	           						data: function ( row, type, set ) {
	           						    value = null;
	           						    if ( row.otHours == null || row.otHours == "" ) {
	           						        value="";
	           						    } else if ( isNaN(row.otHours) ) {
	           						        value=$errorFound;
	           						    } else {
	           						    	x = parseFloat(row.otHours);
	           						        value = x.toFixed(2);
	           						    }
	           						    return value;
	           						}},
	           					{ title : "OT Pay", searchable:true, "defaultContent": "", data:'otPay' },
	           					{ title : "Vac Hrs", searchable:true, "defaultContent": "", 
	           						data: function ( row, type, set ) {
	           						    value = null;
	           						    if ( row.vacationHours == null || row.vacationHours == ""  ) {
	           						        value="";
	           						    } else if ( isNaN(row.vacationHours) ) {
	           						        value=$errorFound;
	           						    } else {
	           						    	x = parseFloat(row.vacationHours);
	           						        value = x.toFixed(2);
	           						    }
	           						    return value;
	           						} },
	           					{ title : "Vac Pay", searchable:true, "defaultContent": "", data:'vacationPay' },
	           					{ title : "Hol Hrs", searchable:true, "defaultContent": "", 
	           						data: function ( row, type, set ) {
	           						    value = null;
	           						    if ( row.holidayHours == null || row.holidayHours == "") {
	           						        value="";
	           						    } else if ( isNaN(row.holidayHours) ) {
	           						        value=$errorFound;
	           						    } else {
	           						    	x = parseFloat(row.holidayHours);
	           						        value = x.toFixed(2);
	           						    }
	           						    return value;
	           						} },
	           					{ title : "Hol Pay", searchable:true, "defaultContent": "", data:'holidayPay' },
	           					{ title : "Gross Pay", searchable:true, "defaultContent": "", data:'grossPay' },
	           					{ title : "Exp Smt'd", searchable:true, "defaultContent": "", data:'expensesSubmitted' },
	           					{ title : "Exp All'd", searchable:true, "defaultContent": "", data:'expensesAllowed' },
	           					{ title : "Volume", searchable:true, "defaultContent": "", data:'volume' },
	           					{ title : "Direct Labor", searchable:true, "defaultContent": "", data:'directLabor' },
	           					{ title : "Prod %", searchable:true, "defaultContent": "", data:'productivity' },
	           					{ title : "Status", "defaultContent":"", 
	           						data : function(row, type, set) {
										var $tag = TIMESHEET_IMPORT.statusIsGood;          							
	           							if ( row.errorsFound == true ) { $tag = TIMESHEET_IMPORT.statusIsBad; }
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
	    			            	} },
	           				],
	           				"initComplete": function(settings, json) {
       			            	var myTable = this;
       			            	LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#timesheet", TIMESHEET_IMPORT.populateDataGrid, null, $data);

       			            	$.each($data.data.employeeRecordList, function($index, $value) {
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
	           		processUploadSuccess : function($data) {
	           			console.log("processUploadSuccess");
	           			$("#prompt-div").hide();
	           			$("#file-header-data .err").html();
	           			$("#data-header").show();
	           			console.log("showing display div.. ");
	           			console.log($data);
	           			console.log($data.data.division);
           				$(".thinking").hide();
           				
           				TIMESHEET_IMPORT.displayHeaderData($data);
           				TIMESHEET_IMPORT.populateDataGrid($data);

           				/*
           				$('select[name="divisionId"]').val($data.data.normal.divisionId);
	           			$('input[name="operationsManagerName"]').val($data.data.operationsManagerName);           			
	           			$('input[name="payrollDate"]').val($data.data.normal.weekEndingDisplay);
	           			$('select[name="state"]').val($data.data.normal.state);
	           			$('input[name="city"]').val($data.data.normal.city);
	           			$("#data-header .timesheetFile").html($data.data.fileName);
	           			*/

           				console.log($data.data.normal.divisionId);
	           			
	           			
	           			/*
	           			$("#data-header input .divisionId").html($data.data.division);
	           			$("#data-header .operationsManagerName").html($data.data.operationsManagerName);           			
	           			$("#data-header .payrollDate").html($data.data.weekEnding);
	           			$("#data-header .state").html($data.data.state);
	           			$("#data-header .city").html($data.data.city);
	           			$("#data-header .timesheetFile").html($data.data.fileName);
	           			*/
	           			
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
	           					$("#data-header .err").html("");
	           					if ( $data.responseHeader.responseCode == "EDIT_FAILURE") {
	           						TIMESHEET_IMPORT.processUploadFailure($data);
	           					} else if ( $data.responseHeader.responseCode == "EDIT_WARNING" ) {
		           						TIMESHEET_IMPORT.processUploadWarning($data);
		           						TIMESHEET_IMPORT.processUploadSuccess($data); 
		           						console.log($data.data.Division);  
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
	        				height: 350,
	        				width: 500,
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
	        						id:  "employee-model-continue-button",
	        						click: function($event) {
	        							TIMESHEET_IMPORT.validateEmployeeModal();
	        						}
	        					}
	        				]
	        			});
	        			$("#employee-modal").dialog("open");
	        			$("#employee-model-cancel-button").button('option', 'label', 'Cancel');
	        			$("#employee-model-continue-button").button('option', 'label', 'Continue');
	        			TIMESHEET_IMPORT.populateEmployeeModal($rowNumber);
	        		},
	        		StringToFloatString : function (NumberAsString){
   						    value = null;
   						    if (NumberAsString == null || NumberAsString == "") {
   						        value="";
   						    } else if ( isNaN(NumberAsString) ) {
   						        value="";
   						    } else {
   						    	x = parseFloat(NumberAsString);
   						        value = x.toFixed(2);
   						    }
   						    return value;
   						},
	        		populateEmployeeModal : function($rowNumber) {
	        			console.log("populateEmployeeModal: " + $rowNumber);
	           			console.log(TIMESHEET_IMPORT.employeeMap[$rowNumber]);
	           			console.log(TIMESHEET_IMPORT.employeeMap[$rowNumber].employeeName);
	           			
	           			$('[name="row"]').val(			TIMESHEET_IMPORT.employeeMap[$rowNumber].row);
	           			$('[name="employeeName"]').val(	TIMESHEET_IMPORT.employeeMap[$rowNumber].employeeName);
						// status

						$('[name="regularHours"]').val(	TIMESHEET_IMPORT.StringToFloatString(TIMESHEET_IMPORT.employeeMap[$rowNumber].regularHours));
	           			$('[name="otHours"]').val(		TIMESHEET_IMPORT.StringToFloatString(TIMESHEET_IMPORT.employeeMap[$rowNumber].otHours));
	           			$('[name="vacationHours"]').val(TIMESHEET_IMPORT.StringToFloatString(TIMESHEET_IMPORT.employeeMap[$rowNumber].vacationHours));
	           			$('[name="holidayHours"]').val(	TIMESHEET_IMPORT.StringToFloatString(TIMESHEET_IMPORT.employeeMap[$rowNumber].holidayHours));
						
						$('[name="regularPay"]').val(	TIMESHEET_IMPORT.employeeMap[$rowNumber].regularPay);
	           			$('[name="otPay"]').val(		TIMESHEET_IMPORT.employeeMap[$rowNumber].otPay);
	           			$('[name="vacationPay"]').val(	TIMESHEET_IMPORT.employeeMap[$rowNumber].vacationPay);
	           			$('[name="holidayPay"]').val(	TIMESHEET_IMPORT.employeeMap[$rowNumber].holidayPay);
	           			
	    				<!-- <table  id="time-calcs"style="width:100%;border:1px solid;">   -->
						
	           			
           		 		var $totalHours = 
           		 			parseFloat(TIMESHEET_IMPORT.employeeMap[$rowNumber].regularHours)
           		 			+ parseFloat(TIMESHEET_IMPORT.employeeMap[$rowNumber].otHours)
           		 			+ parseFloat(TIMESHEET_IMPORT.employeeMap[$rowNumber].vacationHours)
           		 			+ parseFloat(TIMESHEET_IMPORT.employeeMap[$rowNumber].holidayHours)
           		 	           		 	
                        
           				//$('[name="totalHours"]').val($totalHours.toFixed(2));
	           			$('#employee-modal .totalHours').text($totalHours.toFixed(2));
	           			
	           			$('[name="directLabor"]').val(TIMESHEET_IMPORT.employeeMap[$rowNumber].directLabor);
	           			$('[name="volume"]').val(TIMESHEET_IMPORT.employeeMap[$rowNumber].volume);
	           			$('[name="grossPay"]').val(TIMESHEET_IMPORT.employeeMap[$rowNumber].grossPay);

	           			$('[name="expenses"]').val(TIMESHEET_IMPORT.employeeMap[$rowNumber].expenses);
	           			$('[name="expensesAllowed"]').val(TIMESHEET_IMPORT.employeeMap[$rowNumber].expensesAllowed);
	           			$('[name="expensesSubmitted"]').val(TIMESHEET_IMPORT.employeeMap[$rowNumber].expensesSubmitted);	           			
	           			//$('[name="productivity"]').val(TIMESHEET_IMPORT.employeeMap[$rowNumber].productivity);
	           			$('#employee-modal .productivity').text(TIMESHEET_IMPORT.employeeMap[$rowNumber].productivity);

	           			$('#employee-modal .state').text($('select[name="state"]').val());
	           			$('#employee-modal .row').text($rowNumber);
	           			//blankRow: false
	           			//errorsFound: null
	           			//$rowNumber, $action
	           		},
	        		
	           		closeEmployeeModal : function() {
	           			//$rowNumber, $action
	           		},	           		
	           		validateEmployeeModal : function($data) {
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
	        				200:TIMESHEET_IMPORT.processEmployeeValidationSuccess($data),
	        				403:TIMESHEET_IMPORT.processEmployeeValidationErrors,
	           				404:TIMESHEET_IMPORT.processEmployeeValidationErrors,
	           				405:TIMESHEET_IMPORT.processEmployeeValidationErrors,
	           				500:TIMESHEET_IMPORT.processEmployeeValidationErrors,
	        			};
	           			ANSI_UTILS.makeServerCall("POST", "payroll/timesheet", JSON.stringify($outbound), $callbacks, {});
	           		},
	           		// processEmployeeValidationSuccess : function($data, $passthru) {
	           		processEmployeeValidationSuccess : function($data) {
	           			console.log("processEmployeeValidationSuccess");
	           			if ( $data.responseHeader.responseCode == 'EDIT_FAILURE' ) {
	           				$.each($data.data.webMessages, function($index, $value) {
	           					var $selector = "#edit-modal ." + $index + "Err";
	           					$($selector).html($value[0]);
	           				});
	           			} else if ( $data.responseHeader.responseCode == 'SUCCESS' || $data.responseHeader.responseCode == 'EDIT_WARNING') {
	           				updateDataTableFromModal();
	           				//$("#timesheetLookup").DataTable().ajax.reload();
	               			// close both modals because we don't know which one called this method, but only after they've been init'd
	               			
							// 	               			if ( $("#edit-modal").hasClass("ui-dialog-content")) {
							// 	               				$("#edit-modal").dialog("close");
							// 	               			}
							// 	               			if ( $("#confirmation-modal").hasClass("ui-dialog-content")) {
							// 	               				$("#confirmation-modal").dialog("close");
							// 	               			}
	               			if ($data.responseHeader.responseCode == 'SUCCESS') {
	               				$("#globalMsg").html("Success").show().fadeOut(3000);
	               			} else {
	               				ANSI_UTILS.showWarnings("timesheet_warnings", $data.data.webMessages);
	               			}
	           			} else {
	           				$("#employee-modal .timesheet-err").html("Unexpected response code: " + $data.responseHeader.responseCode + ". Contact Support").show();
	           			}
	           		},
	           		
	           		processEmployeeValidationErrors : function($data, $passthru) {
	           			console.log("processEmployeeValidationErrors kevin");
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
	           		updateDataTableFromModal : function() {
	           			console.log("saveEmployeeModal - grabbing values");
						//var $ticketId = $("#employee-modal").attr("ticketId");
	           				           		
						//<td class="employeeName"><input type="text" class="employeeName" Name="employeeName"tabindex="1" /></td>						
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
	           			console.log("saveEmployeeModal: ");
	           			
	           			var $idx = $rowNumber +1;
	           			$idx = $idx -1;
	           			
					    var table = $("#timesheet").DataTable();
					    
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

					    
// 						click: function($event) {
// 								TIMESHEETLOOKUP.saveTimesheet();
					    
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
    				<td class="col3"><span class="payrollDate">				<input type="date" class="payrollDate" 				name="payrollDate"				tabindex="3" />							</span></td>
					<!-- 
   					<td class="col4"><span class="ansi:states">				<input type="text" class="state" 					name="state"					tabindex="4" />									</span></td>
					 -->
   					<td class="col4">
	   					<select id="state" name="state">
	   						<option value=""></option>
	   						<webthing:states /> 
						</select>
   					</td>
    				<td class="col5"><span class="city">			<input type="text" class="city" 					name="city"						tabindex="5" />							</span></td>
    				<td class="col6"><span class="timesheetFile"></span></td>
    				<td class="col7" id="cancel-save-buttons">
    					<input type="button" value="Cancel" name="cancelButton" class="action-button" />
    					<input type="button" value="Save" id="open-button" />
    				</td>
    			</tr>
				<tr class="message-row">
    				<td class="col1"><span class="divisionErr err"></span></td>
    				<td class="col2"><span class="operationsManagerNameErr err"></span></td>
    				<td class="col3"><span class="weekEndingErr err"></span></td>
    				<td class="col4"><span class="stateErr err"></span></td>
    				<td class="col5"><span class="cityErr err"></span></td>
    				<td class="col6" colspan="2"><span class="fileNameErr err"></span></td>
    			</tr>
    		</table>
		</div>

		<div id="data-detail">
			<webthing:lookupFilter filterContainer="filter-container" />
			<table id="timesheet">
			</table>
		</div>
		
		<div id="employee-modal">
			<div style="width:100%; height:0px;">
				<span class="employeeEditErr err"></span>
			</div>	    	
			<table  id="employee-data">   
				<tr>
					<td class="form-label">Employee Name :																					</td>
					<td class="employeeName">	<input 	class="employeeName" 	type="text" 	name="employeeName"	tabindex="101" />		</td>
					<td class="err">			<span 	class="employeeNameErr err">												</span>	</td>
					<td class="spacer">																										</td>
					<td class="form-label">		State:																						</td>
					<td class="state">			<span 	class="state">																</span>	</td>
					<td class="err">			<span 	class="stateErr err">														</span>	</td>
					<td class="form-label">		<span 	class="form-label row-number-label">Row:									</span>	</td>
					<td class="row">			<span 	class="row">																</span>	</td>
					<td class="err">			<span 	class="rowErr err">															</span> </td>						
				</tr>
			</table>
			<table  id="time-calcs">   
				<tr>
					<td class="form-label">																									</td>
					<td colspan="1" class="col-heading hours">	Hours																		</td>
					<td colspan="2" class="col-heading pay">	Pay																			</td>
					<td colspan="2" class="col-heading pay">	Expenses																	</td>
				</tr>
				<tr>
					<td class="row-label">		Regular:																					</td>
					<td class="hours">			<input 	class="hours" 	type="text" name="regularHours" 			tabindex="102" />			</td>
					<td class="money">			<input  class="money" 	type="text" name="regularPay" 				tabindex="103" />			</td>
					<td class="spacer"></td>
					<td class="row-label-col-2">		Submitted:																					</td>
					<td class="money">			<input 	class="money" 	type="text" name="expensesSubmitted" 		tabindex="112" />		</td>
				</tr>
				<tr>
					<td colspan="2" class="hoursErr err">	<span 	class="regularHoursErr  err">												</span>	</td>
					<td colspan="1" class="moneyErr err">	<span 	class="regularPayErr err">													</span>	</td>
					<td class="spacer"></td>
					<td colspan="2" class="moneyErr err">	<span 	class="expensesSubmittedErr err">											</span>	</td>
				</tr>
				<tr>
					<td class="row-label">		Overtime:																					</td>
					<td class="hours">			<input 	class="hours" 	type="text" name="otHours" 					tabindex="104" />			</td>
					<td class="money">			<input 	class="money" 	type="text" name="otPay" 					tabindex="105" />			</td>
					<td class="spacer"></td>
					<td class="row-label-col-2">		Allowed:																					</td>
					<td class="money">			<input 	class="money" 	type="text" name="expensesAllowed" 			tabindex="113" />		</td>
				</tr>
				<tr>
					<td colspan="2" class="hoursErr err">	<span 	class="otHoursErr err">														</span>	</td>
					<td colspan="1" class="moneyErr err">	<span 	class="otPayErr err">														</span>	</td>
					<td class="spacer"></td>
					<td colspan="2" class="moneyErr err">	<span 	class="expensesAllowedErr err">												</span></td>
				</tr>
				<tr>
					<td class="row-label">																									</td>					
					<td class="hours total">																								</td>
					<td class="money">																										</td>
					<td class="spacer"></td>
					<td colspan="2" class="form-label">Productivity																			</td>
				</tr>
				<tr>
					<td colspan="2" class="hoursErr err">																								</td>
					<td class="moneyErr err">																								</td>
					<td class="spacer"></td>
				</tr>
				<tr>
					<td class="row-label">		Vacation:</td>
					<td class="hours">			<input 	class="hours" type="text" name="vacationHours"				tabindex="106" />		</td>
					<td class="money"> 			<input 	class="money" type="text" name="vacationPay" 				tabindex="107" />		</td>
					<td class="spacer"></td>
					<td class="row-label-col-2">Direct Labor:																				</td>
					<td class="money">			<input 	class="money" type="text" name="directLabor" 				tabindex="114" />		</td>
				</tr>
				<tr>
					<td colspan="2" class="hoursErr err">	<span 	class="vacationHoursErr">													</span>	</td>
					<td colspan="1" class="moneyErr err">	<span 	class="vacationPayErr err">													</span>	</td>
					<td class="spacer"></td>
					<td colspan="2" class="moneyErr err">	<span 	class="directLaborErr err">													</span>	</td>
				</tr>
				<tr>
					<td class="row-label">		Holiday:																					</td>
					<td class="hours">			<input	class="hours" type="text" name="holidayHours"				tabindex="108" />		</td>
					<td class="money">			<input	class="money" type="text" name="holidayPay" 				tabindex="109" />		</td>
					<td class="spacer"></td>
					<td class="row-label-col-2">		Volume :																					</td>
					<td class="money">			<input 	class="money" type="text" name="volume" 					tabindex="115" />		</td>
				</tr>							
				<tr>
					<td colspan="2" class="hoursErr err">	<span 	class="holidayHoursErr err">												</span>	</td>
					<td colspan="1" class="moneyErr err">	<span 	class="holidayPayErr err">													</span>	</td>
					<td class="spacer"></td>
					<td colspan="2" class="moneyErr err">	<span 	class="volumeErr err">														</span>	</td>
				</tr>
				<tr>
					<td class="row-label">		Total:																						</td>
					<!-- <td class="hours total">	<input 	class="hours" type="text" name="totalHours" 				tabindex="110" />		</td> -->
					<td class="hours total">	<span 	class="totalHours">											</span>		</td>
					<td class="money">			<input 	class="money" type="text" name="grossPay" 					tabindex="111" />		</td>
					<td class="spacer"></td>
					<td class="row-label-col-2">Productivity:																				</td>
					<!--  <td class="percentage">		<input 	class="percentage" 	type="text" name="productivity"			tabindex="116" />		</td>  -->
					<td class="percentage">		<span 	class="productivity">											</span>		</td>
				</tr>							
				<tr>							
					<td colspan="2" class="hoursErr err">		<span 	class="totalHoursErr err">													</span>	</td>
					<td colspan="1" class="moneyErr err">		<span 	class="grossPayErr err">													</span>	</td>
					<td class="spacer"></td>
					<td colspan="2" class="percentageErr err">	<span 	class="productivityErr err">												</span>	</td>
				</tr>							
			</table>
		</div>				
    </tiles:put>
</tiles:insert>




