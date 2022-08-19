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
		Payroll Employee Import 
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
    	<link rel="stylesheet" href="css/callNote.css" />
       	<link rel="stylesheet" href="css/lookup.css" />
    	<link rel="stylesheet" href="css/ticket.css" />
    	<link rel="stylesheet" href="css/document.css" />
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/addressUtils.js"></script>
    	<script type="text/javascript" src="js/lookup.js"></script> 
    	<script type="text/javascript" src="js/document.js"></script> 
    	<script type="text/javascript" src="js/callNote.js"></script>  
    
        <style type="text/css">
        	#confirm-modal {
        		display:none;
        	}
        	#display-div {
        		display:none;
        	}
        	#employee-modal {
        		display:none;
        	}
        	#table-container {
        		width:100%;
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
        	#save-all-modal {
        		display:none;
        	}
        	#save-all-table {
        		width:100%;
        	}
        	#workingtag {
        		display:none;
        		width:100%;
        		border:solid 1px #000000;
        		text-align: center;
        		font-size: 100px;
        	}
        	.action-link {
        		text-decoration:none;
        		cursor: pointer;
        	}
			.dataTables_wrapper {
				padding-top:10px;
			}	
			.details-control {
				cursor:pointer;
			}	
			.form-label {
				font-weight:bold;
			}
			.org-status-change {
				display:none;
				cursor:pointer;
			}
			.view-link {
				color:#404040;
			}	
			.highlight {
  				background-color: yellow !important;
			}
			.grayback {
				background-color:#CCCCCC;
			}
			.nograyback {
				background-color:transparent;
			}
        </style>
        
        <script type="text/javascript">    

        	$(document).ready(function(){
				;EMPLOYEE_IMPORT = {
					statusIsGood : '<webthing:checkmark>No Errors</webthing:checkmark>',
					statusIsBad : '<webthing:ban>Error</webthing:ban>',
					saveButton : '<webthing:save>Save</webthing:save>',
					view : '<webthing:view styleClass="details-control">Details</webthing:view>',
					employeeDict : {},
					pageVars : {},
					savedEditEmployee : {},
					updateErrorCount : 0,
					updateSuccessCount : 0,
					updateExpectedCount : 0,
                   			
                   			
					init : function() {
						EMPLOYEE_IMPORT.makeClickers();  
						EMPLOYEE_IMPORT.makeModals();
					},
                   	
                   	
					
					doEmployeeUpdate : function($employeeValue) {
						console.log("doEmployeeUpdate");
						EMPLOYEE_IMPORT.updateErrorCount = 0;
						EMPLOYEE_IMPORT.updateSuccessCount = 0;
						EMPLOYEE_IMPORT.updateExpectedCount = 0;						
						var $url = "payroll/employee";
						if ( $employeeValue.newEmployee == false ) {
							$url = $url + "/" + $employeeValue['employeeCode'];
						}
						var $unionMember = $employeeValue['unionMember'];
						if ( $unionMember == null || $unionMember == "" ) {
							$unionMember = 0;
						} else if ( $unionMember.toLowerCase() == "yes" ) {
							$unionMember = 1;
						} else {
							$unionMember = 0;
						}
						var $unionRate = $employeeValue['unionRate'].replaceAll("$","").replace(",","");
						var $outbound = {
							'validateOnly':false,
							'employeeCode':$employeeValue['employeeCode'],
							'companyCode':$employeeValue['companyCode'],
							'divisionId':$employeeValue['divisionId'],
							'firstName':$employeeValue['firstName'],
							'lastName':$employeeValue['lastName'],
							//'middleInitial':$employeeValue[''],
							'departmentDescription':$employeeValue['departmentDescription'],
							'status':$employeeValue['status'],
							'terminationDate':$employeeValue['terminationDate'],
							'unionMember':$unionMember,
							'unionCode':$employeeValue['unionCode'],
							'unionRate':$unionRate,
							//'notes':$employeeValue['notes'],
						};
						var $callbacks = {
							200:EMPLOYEE_IMPORT.doEmployeeUpdateSuccess,
							403:EMPLOYEE_IMPORT.doEmployeeUpdateFailure,
							404:EMPLOYEE_IMPORT.doEmployeeUpdateFailure,
							405:EMPLOYEE_IMPORT.doEmployeeUpdateFailure,
							500:EMPLOYEE_IMPORT.doEmployeeUpdateFailure,
						};
						var $passThruData = {
							'rowId':$employeeValue['rowId']
						};
						ANSI_UTILS.makeServerCall("POST", $url, JSON.stringify($outbound), $callbacks, $passThruData);
					},
					
					
					
					doEmployeeUpdateFailure : function($data, $passThruData) {
						var $rowId = $passThruData['rowId'];
						console.log("doEmployeeUpdateFailure " + $rowId);
						var $selector = "#save-all-table ." + $rowId;
						$($selector).html('<webthing:ban>Update Failure</webthing:ban>');
						EMPLOYEE_IMPORT.updateErrorCount = EMPLOYEE_IMPORT.updateErrorCount + 1;
						if ( EMPLOYEE_IMPORT.updateErrorCount + EMPLOYEE_IMPORT.updateSuccessCount >= EMPLOYEE_IMPORT.updateExpectedCount ) {
							$("#save-employee-cancel").prop('disabled',false);
						}
					},
					
					
					doEmployeeUpdateSuccess : function($data, $passThruData) {
						var $rowId = $passThruData['rowId'];
						console.log("doEmployeeUpdateSuccess " + $rowId);
						var $selector = "#save-all-table ." + $rowId;
						
						if ( $data.responseHeader.responseCode == 'SUCCESS' ) {
							$($selector).html('<webthing:checkmark>Success</webthing:checkmark>');
							EMPLOYEE_IMPORT.updateSuccessCount = EMPLOYEE_IMPORT.updateSuccessCount + 1;
						} else if ( $data.responseHeader.responseCode == 'EDIT_FAILURE' ) {
							EMPLOYEE_IMPORT.updateErrorCount = EMPLOYEE_IMPORT.updateErrorCount + 1;
							var $errFields = [];
							$.each( $data.data.webMessages, function($index, $value) {
								$errFields.push($index);
							});
							$($selector).html('<webthing:ban>Validation Errors: '+ $errFields.join("<br />")+'</webthing:ban>');
						} else {
							EMPLOYEE_IMPORT.updateErrorCount = EMPLOYEE_IMPORT.updateErrorCount + 1;
							$($selector).html('<webthing:ban>Unexpected Response ('+$data.responseHeader.responseCode+') Contact Support</webthing:ban>');
						}
						
						if ( EMPLOYEE_IMPORT.updateErrorCount + EMPLOYEE_IMPORT.updateSuccessCount >= EMPLOYEE_IMPORT.updateExpectedCount ) {
							$("#save-employee-cancel").prop('disabled',false);
						}
					},
					
					
					
                  
					makeClickers : function() {
						$("#save-button").click(function($event) {
							$("#prompt-div .err").html("");
							var file = document.getElementById('employee-file').files[0];
							var reader = new FileReader();
							if ( file == null ) { 
								$("#prompt-div .employeeFileErr").html("Required Value").fadeIn(10).fadeOut(4000);
							} else {
								reader.readAsText(file, 'UTF-8');	           				
								reader.onload = EMPLOYEE_IMPORT.saveFile;
								// reader.onprogress ...  (progress bar)
								$("#workingtag").show();
							}							
						});

						$("#display-div input[name='cancelButton']").click(function($event) {
							$("#display-div").hide();
							$("#prompt-div").show();
							$("#employee-display").hide();
							$("#workingtag").hide();
							$("#employee-file").val(null);
                   		});
						
						$("#display-div input[name='saveAllButton']").click(function($event){
							$("#confirm-modal").dialog("open");
						});
					},
                   		
                   		
                   		
                   	makeModals : function() {
						$( "#employee-modal" ).dialog({
							title:'Edit Employee',
							autoOpen: false,
							height: 600,
							width: 800,
							modal: true,
							closeOnEscape:true,
							//open: function(event, ui) {
							//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
							//},
                			buttons: [
                				{
                					id:  "employee-edit-cancel",
                					click: function($event) {
                						$( "#employee-modal" ).dialog("close");
                					}
                				},{
                					id:  "employee-edit-save",
                					click: function($event) {
                						EMPLOYEE_IMPORT.saveEmployee();
                					}
                				}
                			]
                		});	
                		$("#employee-edit-cancel").button('option', 'label', 'Cancel');  
                		$("#employee-edit-save").button('option', 'label', 'Confirm');
                			
                			
                			
               			$( "#confirm-modal" ).dialog({
               				title:'Confirm Save',
               				autoOpen: false,
               				height: 200,
               				width: 300,
               				modal: true,
               				closeOnEscape:true,
               				//open: function(event, ui) {
               				//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
               				//},
               				buttons: [
               					{
               						id:  "confirm-cancel",
               						click: function($event) {
              								$( "#confirm-modal" ).dialog("close");
               						}
               					},{
               						id:  "confirm-save",
               						click: function($event) {
               							EMPLOYEE_IMPORT.doConfirm();
               						}
               					}
               				]
               			});	
               			$("#confirm-cancel").button('option', 'label', 'Cancel');  
               			$("#confirm-save").button('option', 'label', 'Confirm');
               			
               			
               			
               			
               			
               			$( "#save-all-modal" ).dialog({
							title:'Save Employee Updates',
							autoOpen: false,
							height: 600,
							width: 800,
							modal: true,
							closeOnEscape:false,
							open: function(event, ui) {
								$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
							},
                			buttons: [
                				{
                					id:  "save-employee-cancel",
                					click: function($event) {
                						$( "#save-all-modal" ).dialog("close");
                						console.log("Errors: " + EMPLOYEE_IMPORT.updateErrorCount);
                						if ( EMPLOYEE_IMPORT.updateErrorCount == 0 ) {
                							// all updates are complete and successful so do a page reset
                							$("#globalMsg").html("Update Successful").show().fadeOut(6000);
                							$("#display-div input[name='cancelButton']").click();
                						} else {
                							// some updates failed, so reload the spreadsheet and see where we're at
                							$("#globalMsg").html("Update failed. Reloading").show().fadeOut(6000);
                							$("#save-button").click();
                						}
                					}
                				}
                			]
                		});	
                		$("#save-employee-cancel").button('option', 'label', 'Done');  
                  	},
            			
            			
                  	
                   	makeEmployeeTable : function() {
                  		console.log("makeEmployeeTable");
                  		$("#workingtag").hide();
                   			
                  		$data = Object.values(EMPLOYEE_IMPORT.employeeDict);
                		var $yes = '<webthing:checkmark>Yes</webthing:checkmark>';
                		var $no = '<webthing:ban>No</webthing:ban>';
               			var $unknown = '<webthing:questionmark>Invalid</webthing:questionmark>';
                			
               			$("#employeeImport").DataTable( {
                   			"aaSorting":		[[4,'asc'],[3,'asc']],
                   			"processing": 		true,
                   	        //"serverSide": 		true,
                   	        "autoWidth": 		false,
                   	        "deferRender": 		true,
                   	        "scrollCollapse": 	true,
                   	        "scrollX": 			true,
                   	     	//"pageLength":		50,
                   	        rowId: 				'rowId',
                   	        destroy : 			true,		// this lets us reinitialize the table
                   	        dom: 				'Bfrtip',
                   	        "searching": 		true,
                   	        "searchDelay":		800,
                			//lengthMenu: [
                			//	  [ 10, 50, 100, 500, 1000 ],
                			//    [ '10 rows', '50 rows', '100 rows', '500 rows', '1000 rows' ]
                			//],
                   	        buttons: [
                    	    //  'pageLength',
                    	    	'copy', 
                  	       		'csv', 
                         		'excel', 
               	        		{extend: 'pdfHtml5', orientation: 'landscape'}, 
               	        		'print',{extend: 'colvis',	label: function () {doFunctionBinding();$('#employeeImport').draw();}},
               	        	],
                   	        "columnDefs": [
                    	            { "orderable": true, "targets": -1 },
                    	            { className: "dt-head-center", "targets":[]},
                  	            { className: "dt-left", "targets": [0,1,2,3,4,5,6,7,8,10,12] },
                   	            { className: "dt-center", "targets": [9,13] },
                   	            { className: "dt-right", "targets": [11]}
                   	        ],
                    	    "paging": false,
            			    //"ajax": {
            			    //	  "url": "payroll/employeeImport",
            			    //   "type": "GET",
            			    //   "data": {},
            			    //},
							data: $data,
           			        columns: [
           			        	{ title: "Employee Code", width:"5%", searchable:true, "defaultContent": "<i>N/A</i>", data:'employeeCode' }, 
           			        	{ title: "Company Code", width:"5%", searchable:true, "defaultContent": "<i>N/A</i>",data:"companyCode"}, 
           			        	{ title: "Division", width:"5%", searchable:true, "defaultContent": "<i>N/A</i>", data:'div' },
           			        	{ title: "First Name", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'firstName' },
           			        	{ title: "Last Name", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'lastName' },
           			        	{ title: "MI", width:"5%", searchable:true, "defaultContent": "", data:'middleInitial' },
           			        	{ title: "Dept. Description", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'departmentDescription' },
           			        	{ title: "Status", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'status'},
           			        	{ title: "Termination", width:"10%", searchable:true, "defaultContent": "", data:'terminationDate' },
           			        	{ title: "Union", width:"5%", searchable:true, "defaultContent":"", 
            			        		data:function(row, type, set) {
            			        			var $value = $unknown;
            			        			if ( row.unionMember != null ) {
            			        				if ( row.unionMember == "Yes" ) {
            			        					$value = $yes;
            			        				}
            			        				if ( row.unionMember == '' ) {
            			        					$value = $no;
            			        				}
            			        			}            			        			
            		
            			        			return $value;
            			        		}
								}, 
           			        	{ title: "Union Code", width:"10%", searchable:true, "defaultContent": "", data:'unionCode' },
           			        	{ title: "Union Rate", width:"10%", searchable:true, "defaultContent":"", data:"unionRate" },
           			        	{ title: "Notes", width:"10%", searchable:true, "defaultContent": "", data:'notes' },    			        	
           			            { title: "Action", "orderable": false,  width:"5%", searchable:false,  
            			            	data: function ( row, type, set ) { 
            			            		var $editLink = '<ansi:hasPermission permissionRequired="PAYROLL_WRITE"><span class="action-link edit-link" data-id="'+row.rowId+'"><webthing:edit>Edit</webthing:edit></span></ansi:hasPermission>';
            			            		return $editLink;
            			            	}
								},            			         	 
							],
							"initComplete": function(settings, json) {
								var myTable = this;
								LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#employeeImport", EMPLOYEE_IMPORT.makeEmployeeTable);
							},
							drawCallback : function() {
								$(".edit-link").off("click");    	
								$(".edit-link").click(function($event) {
									var $id = $(this).attr("data-id");
									EMPLOYEE_IMPORT.displayEditModal($id);
								});
								for (var i = 0; i < $data.length; i++){                   				                 				
									if ($data[i].recordMatches == false){
										$("#display-div input[name='saveAllButton']").show();
										document.getElementById($data[i].rowId).classList.add("highlight");
									}
								}    
							}
						} );
					},
                	
					
                		
					displayEditModal : function($rowId) {
						console.log("displayEditModal");
						
                		var $row = EMPLOYEE_IMPORT.employeeDict[$rowId];                			
                		EMPLOYEE_IMPORT.localVars = $rowId;
               			EMPLOYEE_IMPORT.savedEditEmployee = $row
                			
               			var terminationDisplay = $row['terminationDate'];
               			console.log(terminationDisplay);
               			if (terminationDisplay !== "" && terminationDisplay != null){
               				const b = new Date(terminationDisplay);
               				terminationDisplay = b.toISOString().substring(0,10);
               				console.log("inside" + terminationDisplay);
               				
               			} else {
               				terminationDisplay = null;
               				console.log("outside"+ terminationDisplay);
               			}
                			
               			$("#employee-modal .err").html('');
               			$("#employee-modal input[name='rowId']").val($rowId);
               			$("#employee-modal input[name='employeeCode']").val($row['employeeCode']);
               			$("#employee-modal input[name='employeeCode']").prop('disabled',true);
               			$("#employee-modal select[name='companyCode']").val($row['companyCode']);
               			$("#employee-modal select[name='divisionId']").val($row['divisionId']);
               			$("#employee-modal input[name='firstName']").val($row['firstName']);
               			$("#employee-modal input[name='lastName']").val($row['lastName']);
               			$("#employee-modal input[name='departmentDescription']").val($row['departmentDescription']);
               			$("#employee-modal select[name='status']").val($row['status']); 
                			
               			if ( $row.unionMember == "Yes" ) {
       						$("#employee-modal input[name='unionMember']").prop("checked", true);
       						$("#employee-modal .unionInput").prop('disabled',false);
       					} else {
       						$("#employee-modal input[name='unionMember']").prop("checked", false);
       						$("#employee-modal .unionInput").prop('disabled',true);
       					}
        					       					
               			$("#employee-modal input[name='unionCode']").val($row['unionCode']);
               			$("#employee-modal input[name='unionRate']").val($row['unionRate']);
               			$("#employee-modal input[name='notes']").val($row['notes']);
               			
               			if ( $row.status == "ACTIVE"){
               				$("#employee-modal input[name='terminationDate']").prop('disabled',true);
               			} else {
       						
               				$("#employee-modal input[name='terminationDate']").prop('disabled',false);
       					}
               			$("#employee-modal input[name='terminationDate']").val(terminationDisplay);
               			$("#employee-modal").dialog("open");
               			
               			$("#employee-modal .attn").hide();
               			$.each($row.fieldList, function($index, $value) {
               				var $selector = "#employee-modal ." + $value + "Attn";
               				console.log($value + " " + $selector);
               				$($selector).show();
               			});
               			if ( $row.fieldList.length > 0 ) {
               				$("#employee-modal .footnote").show();
               			}
               			
               			$("#employee-modal input[name='unionMember']").click(function($clickEvent) {
               				if ( $("#employee-modal input[name='unionMember']").prop('checked') ) {
               					$("#employee-modal .unionInput").prop('disabled',false);
               				} else {
               					$("#employee-modal .unionInput").prop('disabled',true);
               				}
               			});
               			$("#employee-modal select[name='status']").change(function() {
               				
               				 if ( $("#employee-modal select[name='status'] option:selected").text() == "Active"  ) {
               					$("#employee-modal input[name='terminationDate']").val('');
               					$("#employee-modal input[name='terminationDate']").prop('disabled',true);
               				} else {
               					
               					$("#employee-modal input[name='terminationDate']").val(terminationDisplay);
               					$("#employee-modal input[name='terminationDate']").prop('disabled',false);
               				} 
               				
               			});
                			
               		},
                		
                		
                		
					doConfirm : function() {
						console.log("doConfirm")
						var $pendingTag = '<webthing:pending>Pending</webthing:pending>';								
						$("#confirm-modal").dialog("close");
						$("#save-all-table tbody").html("");
						$("#save-all-modal").dialog("open");						
						$.each( EMPLOYEE_IMPORT.employeeDict , function($employeeIndex, $employeeValue) {
							if ($employeeValue.recordMatches == false){ 
								var $rowId = $employeeIndex;
								var $row = $("<tr>");
								$row.addClass("save-emp-row");
								
								$.each( ['employeeCode','companyCode','div','firstName','lastName'], function($index, $key) {
									var $display = $employeeValue[$key];
									$row.append( $("<td>").append($display) );
								});
								
								var $statusTD = $("<td>").append($pendingTag);
								$statusTD.addClass($employeeIndex);
								$row.append( $statusTD );
								$("#save-all-table tbody").append($row);
								EMPLOYEE_IMPORT.doEmployeeUpdate($employeeValue);
							}
						});
						
						$("#save-all-table .save-emp-row").mouseover(function() { $(this).addClass("grayback"); });
						$("#save-all-table .save-emp-row").mouseout(function() { $(this).addClass("nograyback"); });
						$("#save-employee-cancel").prop('disabled',true);
               		}, 
                		
                		
               		
               		makeItRed : function($value, $bubbleHelp) {
            			var $helptext = $bubbleHelp.join("<br />");
            			return '<span class="red tooltip"><span class="tooltiptext">' + $helptext + '</span>' + $value + '</span>';
            		},
               		
               		
                		
                   	processUploadFailure : function($data) {
						console.log("processUploadFailure");
						$("#workingtag").hide();
						$("#prompt-div .err").html("");
						$.each($data.data.webMessages, function($index, $value) {
							var $selector = "#prompt-div ." + $index + "Err";
							$($selector).html($value[0]).fadeIn(10).fadeOut(4000);
                  		});
                   	},
                   	
                   	
					processUploadChanges : function($data, $passThruData){
						console.log("processUploadChanges");
                   			
                 		if ( $data.responseHeader.responseCode == 'SUCCESS' ) {        	        			
      	        			$( "#employee-modal" ).dialog("close");
      	        			var fixedNum = $data.data.employee.unionRate;                   			
                    		fixedNum = Number(fixedNum).toFixed(2);
                    		if ($data.data.employee.unionRate == null ){
                    			$data.data.employee.unionRate = "";
                   			} else {
                   				$data.data.employee.unionRate = "$" + fixedNum.toString();
                   			}
                   			if ($data.data.employee.unionMember == "0"){
               					$data.data.employee.unionMember = "";
               				} else {
               					$data.data.employee.unionMember = "Yes";
               				}  
                   			// create a dictionary for this empoloyee
                   			var $thisEmployee = {};
                   			$.each( $data.data.employee, function($empFieldName, $empValue) {
                   				$thisEmployee[$empFieldName] = $empValue;
                   			});
                   			// put this employee into the global employee dictionary
                    		EMPLOYEE_IMPORT.employeeDict[$passThruData['rowId']] = $thisEmployee
                    		// update the table display
                    		var $reportData = [];
                   			$.each( EMPLOYEE_IMPORT.employeeDict, function($index, $value) {
                   				$reportData.push($value);
                   			});
                    		
                   			EMPLOYEE_IMPORT.employeeDict = {};
                      		$.each($reportData, function($index, $value) {
                       			EMPLOYEE_IMPORT.employeeDict[$value.rowId] = $value;                   				
                       		});
                      		
                   			EMPLOYEE_IMPORT.makeEmployeeTable();
       	        			$("#globalMsg").html("Success").show().fadeOut(3000);
               			} else if ($data.responseHeader.responseCode == 'EDIT_FAILURE' ) {
       						$.each($data.data.webMessages, function($index, $value) {
       							var $selector = "#employee-modal ." + $index + "Err";
       							$($selector).html($value[0]);
       						});
               			} else {
       	        			$("#globalMsg").html("Invalid response code: " + $data.responseHeader.responseCode + ". Cntact Support").show();
               			}
                  			
					},
                   		
                   		
                   		
                   		
                   	processUploadSuccess : function($data) {
                  		console.log("processUploadSuccess");
                  		$("#prompt-div").hide();
						$("#display-div").show();
                 		$("#employee-display").show();
                  		$("#display-div .employeeFile").html($data.data.fileName);
                  		$("#display-div input[name='saveAllButton']").hide();
                  		EMPLOYEE_IMPORT.employeeDict = {};
                  		$.each($data.data.employeeRecords, function($index, $value) {
                   			EMPLOYEE_IMPORT.employeeDict[$value.rowId] = $value;                   				
                   		});
                  		
                  		EMPLOYEE_IMPORT.makeEmployeeTable();
                  		
                   		
                   			
						$("#organization-edit .org-status-change").on("click", function($event) {
               				console.log("changing status");
               				var $organizationId = $(this).attr("data-id");
               				var $classList = $(this).attr('class').split(" ");
               				var $newStatus = null;
               				$.each($classList, function($index, $value) {
               					if ( $value == "status-is-active") {
               						$newStatus = false;
               					} else if ( $value == "status-is-inactive") {
               						$newStatus = true;
               					} else if ( $value == "status-is-unknown") {
               						$newStatus = true;
               					}
               				});
               				var $url = "organization/" + ORGMAINT.orgType + "/" + $organizationId;
               				var $outbound = {"status":$newStatus};
               				var $callbacks = {
               					200 : ORGMAINT.statusChangeSuccess,
               				};
               				ANSI_UTILS.makeServerCall("POST", $url, JSON.stringify($outbound), $callbacks, {});
               			});
               			
                			
               			$("#organization-edit input[name='name']").on("blur", function($event) {
               				console.log("changing name");
               				var $organizationId = $(this).attr("data-id");
               				var $oldName = $(this).attr("data-name");
               				var $newName = $("#organization-edit input[name='name']").val();
                				
               				if ( $oldName != $newName) {  
               					$("#organization-edit input[name='name']").attr("data-name", $newName);
       	        				var $url = "organization/" + ORGMAINT.orgType + "/" + $organizationId;
       	        				var $outbound = {"name":$newName};
       	        				var $callbacks = {
       	        					200 : ORGMAINT.statusChangeSuccess,
       	        				};
       	     	  				ANSI_UTILS.makeServerCall("POST", $url, JSON.stringify($outbound), $callbacks, {});
              				} 
                		}); 
                	},
             
                	
                	
                	saveEmployee : function() {                   		
                		console.log("saveEmployee");
               			$("#employee-modal .err").html("");
               			var $unionMember = 0;
               			if ( $("#employee-modal input[name='unionMember']").prop("checked") == true ) {
               				$unionMember = 1; 
               			} 
               			var $outbound = {
               				'validateOnly' : true,
               				'employeeCode' : $("#employee-modal input[name='employeeCode']").val(),
       	        			'companyCode' : $("#employee-modal select[name='companyCode']").val(),
       	        			'divisionId' : $("#employee-modal select[name='divisionId']").val(),
       	        			'firstName' : $("#employee-modal input[name='firstName']").val(),
       	        			'lastName' : $("#employee-modal input[name='lastName']").val(),
       	        			'middleInitial' : $("#employee-modal input[name='middleInitial']").val(),
       	        			'departmentDescription' : $("#employee-modal input[name='departmentDescription']").val(),
       	        			'status' : $("#employee-modal select[name='status']").val(),
       	        			'terminationDate' : $("#employee-modal input[name='terminationDate']").val(),	        			
       	        			'unionMember' : $unionMember,
       	        			'unionCode' : $("#employee-modal input[name='unionCode']").val(),
       	        			'unionRate' : $("#employee-modal input[name='unionRate']").val(),
       	        			'processDate' : $("#employee-modal input[name='processDate']").val(),
       	        			'notes' : $("#employee-modal input[name='notes']").val(),
               			}
                			
               			var $passThruData = {
              					'rowId':$("#employee-modal input[name='rowId']").val(),
               			};
               			               			
               			var $url = "payroll/employee"
               			var $employeeCode = $("#employee-modal input[name='employeeCode']").val()
               			if ( $employeeCode != null && $employeeCode != "") {
               				$url = $url + "/" + $employeeCode
               			}
               			$outbound.unionRate = $outbound.unionRate.replace('$', '');
          					
               			ANSI_UTILS.makeServerCall("post", $url, JSON.stringify($outbound),{200:EMPLOYEE_IMPORT.processUploadChanges},  $passThruData);
               		},
               		
               		
               		
        			saveFile : function($event) {
        				console.log('saveFile');
	        			var results = $event.target.result;
	        			var fileName = document.getElementById('employee-file').files[0].name;
	        			var formData = new FormData();
	        			var file = document.getElementById('employee-file').files[0];
	        			formData.append('employeeFile',file, fileName);

	        			var xhr = new XMLHttpRequest();
        				xhr.open('POST',"payroll/employeeImport", true);

           				xhr.onload = function() {
	           				if ( xhr.status == 200 ) {
	           					var $data = JSON.parse(this.response);
	           					if ( $data.responseHeader.responseCode == "EDIT_FAILURE") {
	           						EMPLOYEE_IMPORT.processUploadFailure($data);
	           					} else if ( $data.responseHeader.responseCode == "SUCCESS" ) {
	           						EMPLOYEE_IMPORT.processUploadSuccess($data);
	           					} else {
	           						$("#globalMsg").html("Invalid response code " + $data.responseHeader.responseCode + ". Contact Support");
	           					}
	           				} else {
	           					$("#globalMsg").html("Response Code " + xhr.status + ". Contact Support");
	           				}
	           			};
        				xhr.send(formData);
        			}
        		};
        	
        		EMPLOYEE_IMPORT.init();
        	});
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Payroll Employee Import</h1> 
    	
		
   
	    	
	    <div id="prompt-div">
	   
	    <table>
    		
    		<tr>
    			<td><span class="formLabel">Paycom Import File:</span></td>
    			<td><input type="file" id="employee-file" name="files[]" /><br /></td>
    			<td><span class="employeeFileErr err"></span></td>
    		</tr>
    		<tr>
    			
    				<td colspan="2" style="text-align:center;"><input type="button" value="Open" id="save-button" /></td>
    			
    		</tr>
    	</table>
    	</div>
	    	
	    <div id="workingtag">
	    	<webthing:working />
	    </div>
    	
    	<div id="display-div">
    	 <webthing:lookupFilter filterContainer="filter-container" />
    	<table>
    		<tr id="makeedit">
   				<td><span class="form-label">Paycom Import File:</span></td>
   				<td><span class="employeeFile"></span></td>
   				<td rowspan="2"><input type="button" value="Cancel" name="cancelButton" class="action-button" /></td>
   				<td rowspan="2"><input type="button" value="Save All" name="saveAllButton" class="action-button" /></td>
   				
    		</tr>
    	</table>
    	</div>
		<div id="employee-display">
			<div class="employee-message err"></div>
			<table id="employeeImport">				
			</table>			
		</div>
		
<%-- 		 <div id="employee-modal">
			<input type="hidden" name="rowId"/>
			<table>
			
				<tr>
					<td><span class="formLabel" >Employee Code:</span></td>
					<td><input name="employeeCode" disabled="" /></td>
					<td><span class="err employeeCodeErr"></span></td>
				</tr>
					<tr>
					<td><span class="formLabel">Company Code:</span></td>
					<td><input name="companyCode" /></td>
					<td><span class="err companyCodeErr"></span></td>
				</tr>
				<tr>
					<td class="form-label">Division:</td>
					<td>
						<select name="divisionId">
							<option value=""></option>
							
							<ansi:selectOrganization active="true" type="DIVISION" />
						</select>
					</td>
					<td><span class="divisionIdErr err"></span></td>
				</tr>
					<tr>
					<td><span class="formLabel">First Name:</span></td>
					<td><input name="firstName" /></td>
					<td><span class="err firstNameErr"></span></td>
				</tr>
			
				<tr>
					<td><span class="formLabel">Last Name:</span></td>
					<td><input name="lastName" /></td>
					<td><span class="err lastNameErr"></span></td>
				</tr>
					<tr>
					<td><span class="formLabel">Department Description:</span></td>
					<td><input name="departmentDescription" /></td>
					<td><span class="err departmentDescriptionErr"></span></td>
				</tr>		
				<tr>
					<td><span class="formLabel">Status:</span></td>
					<td>
						<select name="status">
							
							<webthing:employeeStatus />
						</select>
					</td>
					<td><span class="err statusErr"></span></td>
				</tr>
				<tr>
					<td><span class="formLabel">Termination Date:</span></td>
					<td><input type="date" name="terminationDate" /></td>
					<td><span class="err terminationErr"></span></td>
				</tr>
					<tr>
					
					<td class="form-label">Union Member:</td>
					<td><input name="unionMember"  type="checkbox" value="1" /></td>
					<td><span class="err unionMemberErr"></span></td>
			
				</tr>
				<tr>
					<td><span class="formLabel">Union Code:</span></td>
					<td><input name="unionCode" class="unionInput" type="text" /></td>
					<td><span class="err unionCodeErr"></span></td>
				</tr>
				<tr>
					<td><span class="formLabel">Union Rate:</span></td>
					<td><input name="unionRate" class="unionInput" type="text"/></td>
					<td><span class="err unionRateErr"></span></td>
				</tr>
				<tr>
					<td><span class="formLabel">Notes:</span></td>
					<td><input name="notes" /></td>
					<td><span class="err notesErr"></span></td>
				</tr>
			</table>
			
		</div> --%> 
		<jsp:include page="employeeCrudForm.jsp">
			<jsp:param name="id" value="employee-modal" />
		</jsp:include>
	
		<div id="confirm-modal">			
			<h2>Are you sure?</h2>
		</div>
		
		<div id="save-all-modal">
			<table id="save-all-table">
				<thead>
					<tr>
						<td><span class="form-label">Employee Code</span></td>
						<td><span class="form-label">Company Code</span></td>
						<td><span class="form-label">Division</span></td>
						<td><span class="form-label">First Name</span></td>
						<td><span class="form-label">Last Name</span></td>
						<td><span class="form-label">Status</span></td>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
		</div>
    </tiles:put>
		
</tiles:insert>

