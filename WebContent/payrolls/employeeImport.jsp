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
        	#workingtag {
        		display:none;
        		width:100%;
        		border:solid 1px #000000;
        		text-align: center;
        		font-size: 100px;
        	}
        	.action-link {
        		text-decoration:none;
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
        </style>
        
        <script type="text/javascript">    

        		$(document).ready(function(){
                   	;EMPLOYEE_IMPORT = {
                   		statusIsGood : '<webthing:checkmark>No Errors</webthing:checkmark>',
                   		statusIsBad : '<webthing:ban>Error</webthing:ban>',
                   		saveButton : '<webthing:save>Save</webthing:save>',
                   		view : '<webthing:view styleClass="details-control">Details</webthing:view>',
                   		employeeDict : {},
                   			
                   			
                   		init : function() {
                   			EMPLOYEE_IMPORT.makeClickers();  
                   			EMPLOYEE_IMPORT.makeModals();
                   		},
                   	
                   	
                  
                   		makeClickers : function() {
                   			$("#save-button").click(function($event) {
                   				$("#prompt-div .err").html("");
                   				var file = document.getElementById('employee-file').files[0];
                   				var reader = new FileReader();
                   				if ( file == null ) { 
        							$("#prompt-div .employeeFileErr").html("Required Value").show();
        						
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
                   			});
                   		},
                   		
                   		
                   		makeModals : function() {
                   			$( "#employee-modal" ).dialog({
                				title:'Edit Employee',
                				autoOpen: false,
                				height: 450,
                				width: 600,
                				modal: true,
                				closeOnEscape:true,
                				//open: function(event, ui) {
                				//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
                				//},
                				buttons: [
                					{
                						id:  "confirm-cancel",
                						click: function($event) {
                							$( "#employee-modal" ).dialog("close");
               								
                						}
                					},{
                						id:  "confirm-save",
                						click: function($event) {
                							EMPLOYEE_IMPORT.saveEmployee();
                						}
                					}
                				]
                			});	
                			$("#confirm-cancel").button('option', 'label', 'Cancel');  
                			$("#confirm-save").button('option', 'label', 'Confirm');
                   		},
            			
            			
                   		saveEmployee : function() {
                			console.log("saveEmployee");
                			$("#employee-modal .err").html("");
                			var $selectedEmployeeCode = $("#employee-modal input[name='selectedEmployeeCode']").val();
                			var $unionMember = 0;
                			if ( $("#employee-modal input[name='unionMember']").prop("checked") == true ) {
                				$unionMember = 1; 
                			} 
                			var $outbound = {
               					'selectedEmployeeCode' : $("#employee-modal input[name='selectedEmployeeCode']").val(),
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
                			var $url = "payroll/employee"
                			if ( $selectedEmployeeCode != null && $selectedEmployeeCode != "") {
                				$url = $url + "/" + $selectedEmployeeCode
                			}
                			ANSI_UTILS.makeServerCall("post", $url, JSON.stringify($outbound), {200:EMPLOYEE_IMPORT.saveEmployeeSuccess}, {});
                		},
             
                   		
                   		
                   		makeEmployeeTable : function($data) {
                   			$("#workingtag").hide();
                   			
                			var $yes = '<webthing:checkmark>Yes</webthing:checkmark>';
                			var $no = '<webthing:ban>No</webthing:ban>';
                			var $unknown = '<webthing:questionmark>Invalid</webthing:questionmark>';
                			
                			console.log($data.employeeRecords);
                			
                			$("#employeeImport").DataTable( {
                    			"aaSorting":		[[4,'asc'],[3,'asc']],
                    			"processing": 		true,
                    	        //"serverSide": 		true,
                    	        "autoWidth": 		false,
                    	        "deferRender": 		true,
                    	        "scrollCollapse": 	true,
                    	        "scrollX": 			true,
                    	     //   "pageLength":		50,
                    	        rowId: 				'rowId',
                    	        destroy : 			true,		// this lets us reinitialize the table
                    	        dom: 				'Bfrtip',
                    	        "searching": 		true,
                    	        "searchDelay":		800,
                //    	        lengthMenu: [
                //    	        	[ 10, 50, 100, 500, 1000 ],
                //    	            [ '10 rows', '50 rows', '100 rows', '500 rows', '1000 rows' ]
                //    	        ],
                    	        buttons: [
                    	     //   		'pageLength',
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
 //           			        "ajax": {
 //           			        	"url": "payroll/employeeImport",
 //           			        	"type": "GET",
 //           			        	"data": {},
 //           			        	},
 								//"data":JSON.stringify($data.employeeRecords), 	
 								data: $data.employeeRecords,
            			        columns: [
            			        	{ title: "Employee Code", width:"5%", searchable:true, "defaultContent": "<i>N/A</i>", data:'employeeCode' }, 
            			        	{ title: "Company Code", width:"5%", searchable:true, "defaultContent": "<i>N/A</i>", data:'companyCode' }, 
            			        	{ title: "Division", width:"5%", searchable:true, "defaultContent": "<i>N/A</i>", data:'divisionId' },
            			        	{ title: "First Name", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'firstName' },
            			        	{ title: "Last Name", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'lastName' },
            			        	{ title: "MI", width:"5%", searchable:true, "defaultContent": "", data:'middleInitial' },
            			        	{ title: "Dept. Description", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'departmentDescription' },
            			        	{ title: "Status", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'status' },
            			        	{ title: "Termination", width:"10%", searchable:true, "defaultContent": "", data:'terminationDate' },
            			        	{ title: "Union", width:"5%", searchable:true, "defaultContent":"", data:$unknown,
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
            			        	{ title: "Union Rate", width:"10%", searchable:true, "defaultContent":"", data:'unionRate' },
            			        		/* data:function(row, type, set) {
            			        			var $value = "";
            			        			if ( row.union_member == 1 ) {
            			        				$value = $unknown;
            			        				if ( row.union_rate != null ) {
            			        					$value = "$" + row.union_rate.toFixed(2);
            			        				}
            			        			}
            			        			return $value;
            			        		}
            			        	}, */
            			        	{ title: "Notes", width:"10%", searchable:true, "defaultContent": "", data:'notes' },    			        	
            			            { title: "Action",  width:"5%", searchable:false,  
            			            	data: function ( row, type, set ) { 
            			            		var $viewLink = '<span class="action-link view-link" data-id="'+row.rowId+'"><webthing:view>Alias</webthing:view></span>';
            			            		var $editLink = '<ansi:hasPermission permissionRequired="PAYROLL_WRITE"><span class="action-link edit-link" data-id="'+row.rowId+'"><webthing:edit>Edit</webthing:edit></span></ansi:hasPermission>';
            			            		var $deleteLink = '';
            			            		if ( row.timesheet_count == 0 ) {
            			            			$deleteLink = '<ansi:hasPermission permissionRequired="PAYROLL_WRITE"><span class="action-link delete-link" data-id="'+row.rowID+'"><webthing:delete>Delete</webthing:delete></span></ansi:hasPermission>';
            			            		}
            			            		var $actionLink = $viewLink + $editLink + $deleteLink;
            			            		return $actionLink;
            			            	}
            			         	   },
            			      		 ],
            			      		 "initComplete": function(settings, json) {
             			            	var myTable = this;
             			            	LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#employeeLookup", EMPLOYEE_IMPORT.makeEmployeeTable);
             			            },
             			            drawCallback : function() {
             			            	$(".edit-link").off("click");
             			            	
             			            	$(".edit-link").click(function($event) {
             			              		var $id = $(this).attr("data-id");
             			              		EMPLOYEE_IMPORT.displayEditModal($id);
             			              		//alert("it worked! " + $id);
             			          		});
             			            	
             			            
            			            }    
            			    } );
                		},
                		
                		
                		displayEditModal : function($rowId) {
                			var $row = EMPLOYEE_IMPORT.employeeDict[$rowId];
                			console.log($row);
                			
                			var terminationDisplay = $row['terminationDate'];
                			if (terminationDisplay !== ""){
                				const b = new Date(terminationDisplay);
                				terminationDisplay = b.toISOString().substring(0,10);
                			} 
                		
                		
                	
                			
                			var divisionDisplay = $row['divisionId'];
                			if (divisionDisplay == "00"){divisionDisplay ="100"}
                			if (divisionDisplay == "12"){divisionDisplay ="101"}
                			if (divisionDisplay == "15"){divisionDisplay ="102"}
                			if (divisionDisplay == "18"){divisionDisplay ="103"}
                			if (divisionDisplay == "19"){divisionDisplay ="104"}
                			if (divisionDisplay == "23"){divisionDisplay ="105"}
                			if (divisionDisplay == "31"){divisionDisplay ="106"}
                			if (divisionDisplay == "32"){divisionDisplay ="107"}
                			if (divisionDisplay == "33"){divisionDisplay ="108"}
                			if (divisionDisplay == "44"){divisionDisplay ="109"}
                			if (divisionDisplay == "65"){divisionDisplay ="110"}
                			if (divisionDisplay == "66"){divisionDisplay ="111"}
                			if (divisionDisplay == "67"){divisionDisplay ="112"}
                			if (divisionDisplay == "71"){divisionDisplay ="113"}
                			if (divisionDisplay == "72"){divisionDisplay ="114"}
                			if (divisionDisplay == "77"){divisionDisplay ="115"}
                			if (divisionDisplay == "78"){divisionDisplay ="116"}
                			if (divisionDisplay == "89"){divisionDisplay ="117"}
                			if (divisionDisplay == "81"){divisionDisplay ="118"}
                			
                			console.log(divisionDisplay);
                			
                			$("#employee-modal input[name='employeeCode']").val($row['employeeCode']);
                			$("#employee-modal input[name='companyCode']").val($row['companyCode']);
                			$("#employee-modal select[name='divisionId']").val(divisionDisplay);
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
                			
                			
                			$("#employee-modal input[name='terminationDate']").val(terminationDisplay);
                			$("#employee-modal").dialog("open");
                		},
                		
                		
                		 doConfirm : function() {
                			var $function = $("#confirm-save").attr("data-function");
                			if ( $function == "deleteAlias" ) {
                				var $employeeCode = $("#confirm-save").attr("data-id");
                				var $aliasName = $("#confirm-save").attr("data-name");
                				var $url = "payroll/alias/" + $employeeCode + "/" + $aliasName;
                				ANSI_UTILS.makeServerCall("DELETE", $url, {}, {200:EMPLOYEE_IMPORT.processUploadSuccess}, {});
                			} else if ( $function == "deleteEmployee") {
                				var $employeeCode = $("#confirm-save").attr("data-id");
                				var $url = "payroll/employee/" + $employeeCode;
                				ANSI_UTILS.makeServerCall("DELETE", $url, {}, {200:EMPLOYEE_IMPORT.processUploadSuccess}, {});
                			} else {
                				$("#confirm-modal").dialog("close");
                				console.log("Function: " + $function);
                				$("#globalMsg").html("Invalid System State. Reload and try again").show();
                			}
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
                   			$("#employee-display").show();
                   			$("#display-div .employeeFile").html($data.data.fileName);
                   			EMPLOYEE_IMPORT.makeEmployeeTable($data.data);
                   			
                   			$.each($data.data.employeeRecords, function($index, $value) {
                   				EMPLOYEE_IMPORT.employeeDict[$value.rowId] = $value;
                   			});
                   			
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
             
        		saveFile : function($event) {
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
    	
    	<table id="display-div">
    		<tr id="makeedit">
   				<td><span class="form-label">Paycom Import File:</span></td>
   				<td><span class="employeeFile"></span></td>
   				<td rowspan="2"><input type="button" value="Cancel" name="cancelButton" class="action-button" /></td>
   				
    		</tr>
    	</table>
    	
		<div id="employee-display">
			<div class="employee-message err"></div>
			<table id="employeeImport">				
			</table>			
		</div>
		
		<div id="employee-modal">
			<table>
				<tr>
					<td><span class="formLabel">Employee Code</span></td>
					<td><input name="employeeCode" /></td>
					<td><span class="err employeeCodeErr"></span></td>
				</tr>
					<tr>
					<td><span class="formLabel">Company Code</span></td>
					<td><input name="companyCode" /></td>
					<td><span class="err companyCodeErr"></span></td>
				</tr>
				<tr>
					<td class="form-label">Division</td>
					<td>
						<select name="divisionId">
							<option value=""></option>
							
							<ansi:selectOrganization active="true" type="DIVISION" />
						</select>
					</td>
					<td><span class="divisionIdErr err"></span></td>
				</tr>
					<tr>
					<td><span class="formLabel">First Name</span></td>
					<td><input name="firstName" /></td>
					<td><span class="err firstNameErr"></span></td>
				</tr>
			
				<tr>
					<td><span class="formLabel">Last Name</span></td>
					<td><input name="lastName" /></td>
					<td><span class="err lastNameErr"></span></td>
				</tr>
					<tr>
					<td><span class="formLabel">Department Description</span></td>
					<td><input name="departmentDescription" /></td>
					<td><span class="err departmentDescriptionErr"></span></td>
				</tr>		
				<tr>
					<td><span class="formLabel">Status</span></td>
					<td>
						<select name="status">
							
							<webthing:employeeStatus />
						</select>
					</td>
					<td><span class="err statusErr"></span></td>
				</tr>
				<tr>
					<td><span class="formLabel">Termination</span></td>
					<td><input type="date" name="terminationDate" /></td>
					<td><span class="err terminationErr"></span></td>
				</tr>
					<tr>
					
					<td class="form-label">Union Member:</td>
					<td><input name="unionMember"  type="checkbox" value="1" /></td>
					<td><span class="err unionMemberErr"></span></td>
				<!--  	<td><span class="formLabel">Union</span></td>
					
					<td><select name="unionMember">
						
						<option value="Yes">Yes</option>
						<option value=""></option>
						</select></td>
					<td><span class="err unionErr"></span></td> -->
				</tr>
				<tr>
					<td><span class="formLabel">Union Code</span></td>
					<td><input name="unionCode" class="unionInput" type="text" /></td>
					<td><span class="err unionCodeErr"></span></td>
				</tr>
				<tr>
					<td><span class="formLabel">Union Rate</span></td>
					<td><input name="unionRate" class="unionInput" type="text"/></td>
					<td><span class="err unionRateErr"></span></td>
				</tr>
				<tr>
					<td><span class="formLabel">Notes</span></td>
					<td><input name="notes" /></td>
					<td><span class="err notesErr"></span></td>
				</tr>
			</table>
		</div>
		
    </tiles:put>
		
</tiles:insert>

