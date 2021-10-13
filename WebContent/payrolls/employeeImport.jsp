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
        		init : function() {
        			$("#save-button").click(function($event) {
        				var file = document.getElementById('employee-file').files[0];
        				var reader = new FileReader();
        				reader.readAsText(file, 'UTF-8');
        				reader.onload = EMPLOYEE_IMPORT.saveFile;
        				// reader.onprogress ...  (progress bar)
        			});
        			
        			
        		},
        		
        		
        		saveFile : function($event) {
        			var results = $event.target.result;
        			var fileName = document.getElementById('employee-file').files[0].name;
        			var formData = new FormData();
        			var file = document.getElementById('employee-file').files[0];
        			formData.append('employeeFile',file, fileName);
        			formData.append('divisionId', $("#prompt-div select[name='divisionId']").val());
        			
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
        	
        	
        	;ORGMAINT = {
        		orgType : '<c:out value="${ANSI_ORGANIZATION_TYPE}" />',
        		orgTypeDisplay : '<c:out value="${ANSI_ORGANIZATION_TYPE_DISPLAY}" />',        		
        		orgTable : null,
        		icons : {
        			"componentIsYes":'<webthing:component>Component</webthing:component>',
					"componentIsNo":'<webthing:componentNo>Reassign</webthing:componentNo>',
        		},
        		
        		init : function() {
        			$("h1 .organization-type-display").html(ORGMAINT.orgTypeDisplay);
        			ORGMAINT.getOrgs(); 
        			ORGMAINT.makeModals();
        			ORGMAINT.makeClickers();
        		},
        		
        		
        		getOrganizationDetail : function($action, $organizationId, $filter) {
        			// $filter says whether to retrieve all children (false) or just the children of this org (true)
        			console.log("getOrganizationDetail: " + $organizationId);
        			var $url = "organization/" + ORGMAINT.orgType + "/" + $organizationId;
        			var $callbacks = {
        				200 : ORGMAINT.showOrgDetail,
        				404 : ORGMAINT.getOrgsFail,
        			}        			
        			$outbound = {"filter":$filter};
        			ANSI_UTILS.makeServerCall("GET", $url, $outbound, $callbacks, {"action":$action});
        		},
        		
        		
        		
        		getOrgs : function() {
        			console.log("getOrgs");
        			var $url = "organization/" + ORGMAINT.orgType;
        			var $callbacks = {
        				200 : ORGMAINT.makeOrgTable,
        				404 : ORGMAINT.getOrgsFail,
        			}
        			var $passthru = {
       					"destination":"#org-table", 
       					"action":["view","edit"],
       					"source":"orgList",
        			}
        			ANSI_UTILS.makeServerCall("GET", $url, {}, $callbacks, $passthru);
        		},
        		
        		
        		
        		getOrgsFail : function($data, $passthru) {
        			console.log("getOrgsFail");
        			$("#globalMsg").html("Invalid organization type");        			
        		},
        		
        		
        		

           		makeClickers : function() {
           			$("#save-button").click(function($event) {
           				$("#prompt-div .err").html("");
           				var file = document.getElementById('timesheet-file').files[0];
           				var reader = new FileReader();
           				if ( file == null ) { 
							$("#prompt-div .timesheetFileErr").html("Required Value").show();
							if ( $("#prompt-div select[name='divisionId']").val().length == 0) { $("#prompt-div .divisionIdErr").html("Required Value").show(); }
							
           				} else {
	           				reader.readAsText(file, 'UTF-8');	           				
	           				reader.onload = EMPLOYEE_IMPORT.saveFile;
	           				// reader.onprogress ...  (progress bar)
           				}
           			});
           			
           			$("#display-div input[name='cancelButton']").click(function($event) {
           				$("#display-div").hide();
           				$("#prompt-div").show();
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
           			$("#display-div .divisionId").html($data.data.request.div);
           			}
        			
        			
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
        		
        		
        		
        		makeModals : function() {
        			$( "#organization-display" ).dialog({
        				title:'View ' + ORGMAINT.orgTypeDisplay,
        				autoOpen: false,
        				height: 500,
        				width: 1200,
        				modal: true,
        				closeOnEscape:true,
        				//open: function(event, ui) {
        				//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
        				//},
        				buttons: [
        					{
        						id:  "org_display_cancel",
        						click: function($event) {
       								$( "#organization-display" ).dialog("close");
        						}
        					}
        				]
        			});	
        			$("#org_display_cancel").button('option', 'label', 'Done');    
        			
        			$( "#organization-edit" ).dialog({
        				title:'Edit ' + ORGMAINT.orgTypeDisplay,
        				autoOpen: false,
        				height: 500,
        				width: 1200,
        				modal: true,
        				closeOnEscape:true,
        				//open: function(event, ui) {
        				//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
        				//},
        				buttons: [
        					{
        						id:  "org_edit_cancel",
        						click: function($event) {
       								$( "#organization-edit" ).dialog("close");
        						}
        					}
        				]
        			});	
        			$("#org_edit_cancel").button('option', 'label', 'Done');
        		},
        		
        		
        		
        		
        		
        		
        		makeOrgTable : function($data, $passthru) {
        			console.log("makeOrgTable");  
        			console.log($passthru);
        			var $active = '<webthing:checkmark>Active</webthing:checkmark>';
        			var $inactive = '<webthing:ban>Inactive</webthing:ban>';
        			var $unknown = '<webthing:questionmark>Unknown</webthing:questionmark>';
        			
        			var $edit = '<webthing:edit>Edit</webthing:edit>';
	       			var $delete = '<webthing:delete>Delete</webthing:delete></a>';
	       			var $view = '<webthing:view>View</webthing:view>';
	       			
	       			
					
					
					
        			var $buttonArray = [        	        	
        	        	'copy', 
        	        	{extend:'csv', filename:'*_' + ORGMAINT.orgTypeDisplay}, 
        	        	{extend:'excel', filename:'*_' + ORGMAINT.orgTypeDisplay}, 
        	        	{extend:'pdfHtml5', orientation: 'landscape', filename:'*_' + ORGMAINT.orgTypeDisplay}, 
        	        	'print',
        	        	{extend:'colvis', label: function () {
	        	        		doFunctionBinding();
	        	        		$('#org-table').draw();
        	        		}
        	        	}
        	        ];
        			
        			$($passthru['destination']).DataTable( {
		    			data : $data.data[$passthru['source']],
		    			"aaSorting":		[[1,'asc']],
		    			paging : false,
		    			autoWidth : false,
	        	        deferRender : true,
	        	        searching: false, 
	        	        scrollX : false,
	        	        rowId : 'organizationId',	// this sets an id for the row: <tr id="123"> ... </tr>   where 123 is the org id
	        	        dom : 'Bfrtip',
	        	        destroy : true,		// this lets us reinitialize the table for different tickets
            	        buttons: $buttonArray,
		    			columns : [
		    				{ width:"10%", title:"ID", className:"dt-center", orderable:true, data:'organizationId' },
		    				{ width:"25%", title:"Name", className:"dt-left", orderable:true, data:'name' },
		    				{ width:"25%", title:"Parent", className:"dt-left", orderable:true, data:'parentName' },
		    				{ width:"10%", title:"Parent Type", className:"dt-left", orderable:true, data:'parentType' },
		    				{ width:"10%", title:"Status", className:"dt-center", orderable:true, 
		    					data:function(row,type,set) {
		    						if ( row.status == 0 ) { $status = $inactive}
		    						else if ( row.status == 1 ) { $status = $active }
		    						else { $status = $unknown }
		    						return $status;
		    					}
		    				},
		    				{ width:"10%", title:"Action", className:"dt-center", orderable:true, visible:$passthru['action'].length > 0,
		    					data:function(row,type,set) {
		    						var $viewLink = "";
		    						var $editLink = "";
		    						var $parentLink = "";
		    						
		    						$.each($passthru['action'], function($index, $value) {
		    							if ( $value == "view" )  { $viewLink = '<a href="#" class="action-link view-link" data-id="'+row.organizationId+'"><webthing:view>View</webthing:view></a>'; }
		    							if ( $value == "edit" )  { $editLink = '<a href="#" class="action-link edit-link" data-id="'+row.organizationId+'"><webthing:edit>Edit</webthing:edit></a>'; }
		    							if ( $value == "parent") { 
		    								if ( row.parentId == $data.data.organization.organizationId) {
		    									$parentLink = ORGMAINT.icons['componentIsYes'];
			    							} else {
			    								$parentLink = '<a href="#" class="action-link parent-link" data-orgtype="'+row.type+'" data-id="'+row.organizationId+'">' + ORGMAINT.icons['componentIsNo'] + '</a>';
			    							}
		    							}
		    						});
	    							
		    						return $viewLink + '<ansi:hasPermission permissionRequired="SYSADMIN_WRITE">' + $editLink + $parentLink + '</ansi:hasPermission>';
		    					},		    					
		    				},
	    				],
	    				"drawCallback": function( settings ) {
	    					console.log("drawCallback");
	    					$(".action-link").off("click");
	    					$(".view-link").click(function($clickevent) {
	    						var $organizationId = $(this).attr("data-id");
	    						ORGMAINT.getOrganizationDetail("view", $organizationId, true);
	    					});
	    					$(".edit-link").click(function($clickevent) {
	    						var $organizationId = $(this).attr("data-id");
	    						ORGMAINT.getOrganizationDetail("edit", $organizationId, false);
	    					});
	    					$(".parent-link").click(function($clickevent) {	    						
	    						ORGMAINT.parentChange($(this));
	    					});
	    				},
        			});
        			
	       			// var myTable $($passthru['destination']).DataTable();
					// myTable.columns(columnNumber).visible(true);
        		},
        		
        		

        		parentChange : function($clickEvent) {
        			var $organizationId = $clickEvent.attr("data-id");
					var $organizationType = $clickEvent.attr("data-orgtype");
					var $newParent = $("#organization-edit input[name='name']").attr("data-id");
					console.log("changing parent: " + $organizationId + " " + $organizationType + " " + $newParent);
					var $url = "organization/" + $organizationType + "/" + $organizationId;
    				var $outbound = {"parentId":$newParent};
    				var $callbacks = {
    					200 : ORGMAINT.parentChangeSuccess,
    				};
    				ANSI_UTILS.makeServerCall("POST", $url, JSON.stringify($outbound), $callbacks, {"event":$clickEvent});

        		},
        		
        		
        		
        		
        		parentChangeSuccess : function($data, $passthru) {
        			console.log("parentChangeSuccess");
        			if ( $data.responseHeader.responseCode == "SUCCESS" ) {
        				$("#organization-edit .organization-msg").html("Update successful").show().fadeOut(3000);
        				$passthru["event"].html(ORGMAINT.icons['componentIsYes'])
            			//ORGMAINT.showOrgDetail($data, {"action":"edit"});
        			} else {
        				//this happens if the data attributes on the lookup are weird.
        				$("#organization-edit .organization-msg").html("Update error. Reload page and try again").show();
        			}
        		},
        		
        		
        		
        		
        		showOrgDetail : function($data, $passthru) {
        			console.log("showOrgDetail");
        			
        			var $organization = $data.data.organization;
        			var $active = '<webthing:checkmark>Active</webthing:checkmark>';
        			var $inactive = '<webthing:ban>Inactive</webthing:ban>';
        			var $unknown = '<webthing:questionmark>Unknown</webthing:questionmark>';

        			if ( $organization.status == 0 ) { 
        				$status = $inactive;
        			} else if ( $organization.status == 1 ) { 
        				$status = $active;
        			} else { 
        				$status = $unknown; 
       				}

        			// populate the display modal
        			$("#organization-display .organization-id").html($organization.organizationId);
        			$("#organization-display .name").html($organization.name);
        			$("#organization-display .status").html($status);
        			$("#organization-display .parent-name").html($organization.parentName);
        			$("#organization-display .parent-type").html($organization.parentType);
        			
        			// populate the edit modal
        			$("#organization-edit .organization-id").html($organization.organizationId);
        			$("#organization-edit input[name='name']").val($organization.name);
        			$("#organization-edit input[name='name']").attr("data-id", $organization.organizationId);
        			$("#organization-edit input[name='name']").attr("data-name", $organization.name);
        			$("#organization-edit .org-status-change").hide();
        			$("#organization-edit .org-status-change").attr("data-id", $organization.organizationId);
        			if ( $organization.status == 0 ) { 
        				$("#organization-edit .status-is-inactive").show();
        			} else if ( $organization.status == 1 ) { 
        				$("#organization-edit .status-is-active").show();
        			} else { 
        				$("#organization-edit .status-is-unknown").show();
       				}
        			$("#organization-edit .parent-name").html($organization.parentName);
        			$("#organization-edit .parent-type").html($organization.parentType);
        			
        			var $destination = {
           				"view" : "#organization-display",
           				"edit" : "#organization-edit",
           			};
            			
        			if ( $passthru["action"] == "view" ) {
        				$displayDetail = {
               				"destination":$destination[$passthru["action"]] + " .organization-children",
               				"action":[],
               				"source":"childList",
               			}
        			} else if ( $passthru["action"] == "edit" ) {
        				$displayDetail = {
               				"destination":$destination[$passthru["action"]] + " .organization-children",
               				"action":["parent"],
               				"source":"childList",
               			}
        			} else {
        				$("#globalMsg").html("System Error. Invalid action. Contact Support");
        			}
        			// populate the child list        			
        			ORGMAINT.makeOrgTable($data, $displayDetail)
        			
        			// display one of the modals
        			$($destination[$passthru["action"]]).dialog("open");
        		},
        		
        		
        		
        		
            	
        		statusChangeSuccess : function($data, $passthru) {
        			console.log("statusChangeSuccess");
        			if ( $data.responseHeader.responseCode == "SUCCESS" ) {
        				$("#organization-edit .organization-msg").html("Update Successful").show().fadeOut(3000);
        				$("#organization-edit .org-status-change").hide();
            			if ( $data.data.organization.status == 0 ) { 
            				$("#organization-edit .status-is-inactive").show();
            			} else if ( $data.data.organization.status == 1 ) { 
            				$("#organization-edit .status-is-active").show();
            			} else { 
            				$("#organization-edit .status-is-unknown").show();
           				}            			
            			ORGMAINT.getOrgs();
        			} else {
        				$("#organization-edit .organization-msg").html("System Error. Reload the page and try again").show();
        			}
        			
        			
        		},
            	
            	
            	
        	};
        	
        	// ORGMAINT.init();
        	
        	
        });
        		
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Payroll Employee Import</h1> 

   
	    	
	    <div id="prompt-div">
	    <table>
    		<tr>
    			<td><span class="formLabel">Division: </span></td>
    			<td>
    			<select name="divisionId">
    				<option value=""></option>
    				<ansi:selectOrganization type="DIVISION" active="true" />
    			</select>
    			</td>
    			<td><span class="divisionIdErr err"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formLabel">Paycom Import File:</span></td>
    			<td><input type="file" id="employee-file" name="files[]" /><br /></td>
    		</tr>
    		<tr>
    			<td></td>
    			<td><input type="button" value="Save" id="save-button" /></td>
    			
    		</tr>
    	</table>
    	</div>
	    	
    	
    	<table id="display-div">
    		<tr>
    			<td><span class="form-label">Company Code:</span></td>
    			<td><span class="form-label">Division:</span></td>
    			<td><span class="form-label">Employee Code:</span></td>
    			<td><span class="form-label">First:</span></td>
    			<td><span class="form-label">Last:</span></td>
    			<td><span class="form-label">Department Desc Status:</span></td>
    			<td><span class="form-label">Term Date:</span></td>
    			<td><span class="form-label">Union Member:</span></td>
    			<td><span class="form-label">Union Code:</span></td>
    			<td><span class="form-label">Union Rate:</span></td>
    			<td><span class="form-label">Process Date:</span></td>
    				
    			<td rowspan="2"><input type="button" value="Cancel" name="cancelButton" class="action-button" /></td>
    		</tr>
    	</table>

		
    </tiles:put>
		
</tiles:insert>

