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
                   			
                   			
                   		init : function() {
                   			EMPLOYEE_IMPORT.makeClickers();            			
                   		},
                   	
                   	
                  
                   		makeAliasTable : function($employeeCode) {
                			console.log("makeAliasTable: " + $employeeCode);
                			$("#alias-display").dialog("open");
                			$("#alias-lookup").DataTable( {
                    			"aaSorting":		[[0,'asc']],
                    			"processing": 		true,
                    	        "serverSide": 		true,
                    	        "autoWidth": 		false,
                    	        "deferRender": 		true,
                    	        "scrollCollapse": 	true,
                    	        "scrollX": 			true,
                    	       // "pageLength":		50, 
                    	        rowId: 				'dt_RowId',
                    	        destroy : 			true,		// this lets us reinitialize the table
                    	        dom: 				'Bfrtip',
                    	        "searching": 		true,
                    	        "searchDelay":		800,
                    	       /*   lengthMenu: [
                    	        	[ 10, 50, 100, 500, 1000 ],
                    	            [ '10 rows', '50 rows', '100 rows', '500 rows', '1000 rows' ]
                    	        ],  */
                    	        buttons: [
                    	        		//'pageLength',
                    	        		'copy', 
                    	        		'csv', 
                    	        		'excel', 
                    	        		{extend: 'pdfHtml5', orientation: 'landscape'}, 
                    	        		'print',{extend: 'colvis',	label: function () {doFunctionBinding();$('#displayTable').draw();}},
                    	        	],
                    	        "columnDefs": [
                     	            { "orderable": true, "targets": -1 },
                     	            { className: "dt-head-center", "targets":[]},
                    	            { className: "dt-left", "targets": [0] },
                    	            { className: "dt-center", "targets": [1] },
                    	            { className: "dt-right", "targets": []}
                    	         ],
                    	        "paging": false,
            			        /* "ajax": {
            			        	"url": "payroll/aliasLookup",
            			        	"type": "GET",
            			        	"data": {"employeeCode":$employeeCode},
            			        	}, */
            			        columns: [
            			        	{ title: "Employee Alias", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'employee_name' }, 
            			            { title: "Action",  width:"10%", searchable:true, searchFormat: "Equipment #####", 
            			            	data: function ( row, type, set ) { 
            			            		//var $editLink = '<span class="action-link edit-link" data-id="'+row.employee_code+'" data-name="'+row.employee_name+'"><webthing:edit>Edit</webthing:edit></span>';
            			            		var $deleteLink = '<span class="action-link delete-link" data-id="'+row.employee_code+'" data-name="'+row.employee_name+'"><webthing:delete>Delete</webthing:delete></span>';
            			            		return '<ansi:hasPermission permissionRequired="PAYROLL_WRITE">' + $deleteLink + '</ansi:hasPermission>'
            			            	}
            			            },
            			            ],
            			            "initComplete": function(settings, json) {
            			            	var myTable = this;
            			            	//LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#ticketTable", CALL_NOTE_LOOKUP.makeTable);
            			            },
            			            "drawCallback": function( settings ) {
            			            	console.log("alias drawCallback");
            			            	//$("#alias-lookup .edit-link").off("click");
            			            	$("#alias-lookup .delete-link").off("click");
            			            	$("#alias-lookup .cancel-new-alias").off("click");
            			            	$("#alias-lookup .save-new-alias").off("click");
            			            	$("#alias-lookup .delete-link").click(function($event) {
            			            		var $employeeCode = $(this).attr("data-id");
            			            		var $employeeName = $(this).attr("data-name");
            			            		$("#confirm-save").attr("data-function","deleteAlias");
            			            		$("#confirm-save").attr("data-id",$employeeCode);
            			            		$("#confirm-save").attr("data-name",$employeeName);
        									$("#confirm-modal").dialog("open");
            			            	});
            			            	$("#alias-display .cancel-new-alias").click( function($event) {
            			            		$("#alias-display input[name='employeeName']").val("");
            			            	});
            			            	$("#alias-display .save-new-alias").click( function($event) {    			            		
            			            		var $employeeCode = $(this).attr("data-id");
            			            		var $employeeName = null
            			            		$.each( $("#alias-display input"), function($index, $value) {
            			            			// for some reason, datatables is putting two input boxes, so get the one that is populated
            			            			var $thisValue = $($value).val();
            			            			if ( $thisValue != null ) {
            			            				$employeeName = $thisValue;
            			            			}
            			            		});
            			            		var $url = "payroll/alias/" + $employeeCode;
            			            		$("#alias-display .err").html("");
            			            		console.log("new alias name: " + $employeeName);
            			            		ANSI_UTILS.makeServerCall("POST", $url, {"employeeName":$employeeName}, {200:EMPLOYEELOOKUP.doNewAliasSuccess}, {});
            			            	});

            			            },
            			            "footerCallback" : function( row, data, start, end, display ) {
            			            	console.log("alias footerCallback");
            			            	var api = this.api();
            			            	var $saveLink = '<span class="action-link save-new-alias" data-id="'+$employeeCode+'"><webthing:checkmark>Save</webthing:checkmark></span>';
            			            	var $cancelLink = '<span class="action-link cancel-new-alias"><webthing:ban>Cancel</webthing:ban></span>';
            			            	var $aliasInput = '<ansi:hasPermission permissionRequired="PAYROLL_WRITE"><input type="text" name="employeeName" placeholder="New Alias" /></ansi:hasPermission>';
            			            	var $aliasError = '<span class="employeeNameErr err"></span>';
            			            	$( api.column(0).footer() ).html($aliasInput + " " + $aliasError);
            			            	$( api.column(1).footer() ).html('<ansi:hasPermission permissionRequired="PAYROLL_WRITE">' + $saveLink + $cancelLink + '</ansi:hasPermission>');
            			            }
            			    } );
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
                   				}
                   			});
                   			
                   			$("#display-div input[name='cancelButton']").click(function($event) {
                   				$("#display-div").hide();
                   				$("#prompt-div").show();
                   				$("#alias-display").hide();
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
                   			$("#alias-display").show();
                   			$("#display-div .employeeFile").html($data.data.fileName);
                   			EMPLOYEE_IMPORT.makeAliasTable();
                   			
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
    			
    				<td colspan="2" style="text-align:center;"><input type="button" value="Save" id="save-button" /></td>
    			
    		</tr>
    	</table>
    	</div>
	    	
    	
    	<table id="display-div">
    		<tr>
    				
    				

    				<td><span class="form-label">Paycom Import File:</span></td>
    				<td><span class="employeeFile"></span></td>
    				
    				
    			<td rowspan="2"><input type="button" value="Cancel" name="cancelButton" class="action-button" /></td>
    		</tr>
    		
    	</table>
    	
				<div id="alias-display">
			<div class="alias-message err"></div>
			<table id="alias-lookup">
				<thead></thead>
				<tbody></tbody>
				<tfoot>
					<tr>
						<td></td>
						<td></td>
					</tr>
				</tfoot>
			</table>			
		</div>
		
    </tiles:put>
		
</tiles:insert>

