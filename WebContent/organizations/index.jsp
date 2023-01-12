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
		Organizations
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
        	#organization-new {
        		display:none;
        	}
        	.action-link {
        		text-decoration:none;
        	}
        	.company-field {
        		display:none;
        	}
        	.company-specific {
        		display:none;
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
        			if ( ORGMAINT.orgType == 'COMPANY' ) {
        				$(".company-field").show();
        			} else {
        				$(".company-field").hide();
        			}
        			ORGMAINT.getOrgs(); 
        			ORGMAINT.makeModals();
        			ORGMAINT.makeClickers();
        			
        			if ( ORGMAINT.orgType == 'COMPANY' ) {
        				$(".company-specific").show();
        			} else {
        				$(".company-specific").hide();
        			}
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
        			
        			$("#organization-edit input[name='companyCode']").on("blur", function($event) {
        				console.log("changing company");
        				var $organizationId = $(this).attr("data-id");
        				var $oldName = $(this).attr("data-name");
        				var $newName = $("#organization-edit input[name='companyCode']").val();
        				
        				if ( $oldName != $newName) {  
        					$("#organization-edit input[name='name']").attr("data-name", $newName);
	        				var $url = "organization/" + ORGMAINT.orgType + "/" + $organizationId;
	        				var $outbound = {"companyCode":$newName};
	        				var $callbacks = {
	        					200 : ORGMAINT.statusChangeSuccess,
	        				};
	        				ANSI_UTILS.makeServerCall("POST", $url, JSON.stringify($outbound), $callbacks, {});
        				}
        			});
        			
        			
        			
        			$("#new-organization-button").click(function($event) {
        				console.log("new org click");
        				$("#organization-new input").val("");
        				$("#organization-new select").val("");
        				$("#organization-new .err").html("");
        				$("#organization-new").dialog("open");
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
        			
        			
        			
        			
        			$( "#organization-new" ).dialog({
        				title:'New ' + ORGMAINT.orgTypeDisplay,
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
        						id:  "org_new_cancel",
        						click: function($event) {
       								$( "#organization-new" ).dialog("close");
        						}
        					},{
        						id:  "org_new_save",
        						click: function($event) {
       								ORGMAINT.saveNewOrg();
        						}
        					}
        					
        				]
        			});	
        			$("#org_new_cancel").button('option', 'label', 'Cancel');
        			$("#org_new_save").button('option', 'label', 'Save');
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
		    				{ width:"15%", title:"Company Code", className:"dt-left", orderable:true, data:'companyCode' },
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
	    					
	    					// hide the company code column 
	    					var myTable = $($passthru['destination']).DataTable();
	    					if ( ORGMAINT.orgType != "COMPANY" ) {
    							myTable.columns(4).visible(false);
	    					}
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
        		
        		
        		
        		
        		saveNewOrg : function() {
        			console.log("saveNewOrg");
        			
        			var $url = "organization/" + ORGMAINT.orgType; // + "/" + $organizationId;
    				var $outbound = {};
    				$.each( $("#organization-new input"), function($index, $value) {
    					$outbound[$($value).attr("name")] = $($value).val();
    				});
    				$.each( $("#organization-new select"), function($index, $value) {
    					$outbound[$($value).attr("name")] = $($value).val();
    				});
    				// Status is a boolean
    				$outbound['status'] = $("#organization-new select[name='status']").val() == 1;
    				console.log(JSON.stringify($outbound));
    				var $callbacks = {
    					200 : ORGMAINT.saveNewOrgSuccess,
    				};
    				ANSI_UTILS.makeServerCall("POST", $url, JSON.stringify($outbound), $callbacks, {});
    				
        		},
        		
        		
        		
        		saveNewOrgSuccess : function($data, $passthru) {
        			console.log("saveNewOrgSuccess");
        			if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
        				$("#organization-new .err").html("");
        				$.each($data.data.webMessages, function($key, $value) {
        					var $errField = "#organization-new ."+$key+"Err";
        					console.log($errField);
        					console.log($value[0]);
        					$($errField).html($value[0]);
        				});
        			} else if ( $data.responseHeader.responseCode = 'SUCCESS') {
        				$( "#organization-new" ).dialog("close");
        				$("#globalMsg").html("Success").show().fadeOut(3000);
        				var $newOrgId = $data.data.organization.organizationId;
        				ORGMAINT.getOrgs();
        				ORGMAINT.getOrganizationDetail("edit", $newOrgId, false);
        			} else {
        				$("#globalMsg").html("System Error. Invalid response code. Contact Support").show();
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
        			$("#organization-display .company-code").html($organization.companyCode);
        			
        			// populate the edit modal
        			$("#organization-edit .organization-id").html($organization.organizationId);
        			$("#organization-edit input[name='name']").val($organization.name);
        			$("#organization-edit input[name='name']").attr("data-id", $organization.organizationId);
        			$("#organization-edit input[name='name']").attr("data-name", $organization.name);
        			$("#organization-edit input[name='companyCode']").val($organization.companyCode);
        			$("#organization-edit input[name='companyCode']").attr("data-id", $organization.organizationId);
        			$("#organization-edit input[name='companyCode']").attr("data-name", $organization.companyCode);
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
        	
        	ORGMAINT.init();
        	
        	
        });
        		
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Organizations: <span class="organization-type-display"></span></h1> 

    	
		<webthing:lookupFilter filterContainer="filter-container" />
		<div id="table-container">
		 	<table id="org-table" class="display" cellspacing="0" style="table-layout: fixed; font-size:9pt;min-width:1300px; max-width:1300px;width:1300px;">
		        <thead></thead>
		        <tbody></tbody>
		        <tfoot></tfoot>
		    </table>
		    <ansi:hasPermission permissionRequired="SYSADMIN_WRITE">
		    <input type="button" class="prettyWideButton" id="new-organization-button" value="New" />
			</ansi:hasPermission>	    
	    </div>
	    
	    <webthing:scrolltop />
	

		<div id="organization-display">
			<div style="width:100%; height:14px;"><span class="err organization-msg"></span></div>
			<table>
				<tr>
					<th>ID</th>
					<th>Name</th>
					<th>Parent</th>
					<th>Parent Type</th>
					<th class="company-field">Company Code</th>
					<th>Status</th>					
				</tr>
				<tr>
					<td><span class="organization-id"></span></td>
					<td><span class="name"></span></td>
					<td><span class="parent-name"></span></td>
					<td><span class="parent-type"></span></td>					
					<td class="company-field"><span class="company-code"></span></td>				
					<td><span class="status"></span></td>
				</tr>
			</table>
			<table class="organization-children">
			</table>
		</div>
		
		
		<div id="organization-edit">
			<div style="width:100%; height:14px;"><span class="err organization-msg"></span></div>
			<table>
				<tr>
					<th>ID</th>
					<th>Name</th>
					<th>Parent</th>
					<th>Parent Type</th>
					<th class="company-field">Company Code</th>
					<th>Status</th>					
				</tr>
				<tr>
					<td><span class="organization-id"></span></td>
					<td><input type="text" name="name" /></td>
					<td><span class="parent-name"></span></td>
					<td><span class="parent-type"></span></td>	
					<td class="company-field"><input type="text" name="companyCode" /></td>				
					<td><span class="status">					
						<webthing:checkmark styleClass="org-status-change status-is-active">Active</webthing:checkmark>
	        			<webthing:ban styleClass="org-status-change status-is-inactive">Inactive</webthing:ban>
	        			<webthing:questionmark styleClass="org-status-change status-is-unknown">Unknown</webthing:questionmark>					
					</span></td>
				</tr>
			</table>
			<table class="organization-children">
			</table>
		</div>
		
		
		
		<div id="organization-new">
			<table>
				<tr>
					<td><span class="form-label">Name:</span></td>
					<td><input type="text" name="name" /></td>
					<td><span class="err nameErr"></span></td>
				</tr>
				<tr class="company-specific">
					<td><span class="form-label">Parent:</span></td>
					<td>
						<select name="parentId">
							<option value=""></option>
							<ansi:selectOrganization active="true" type="REGION" />
						</select>
					</td>
					<td><span class="err nameErr"></span></td>
				</tr>
				<tr class="company-specific">
					<td><span class="form-label">Company Code:</span></td>
					<td><input type="text" name="companyCode" /></td>
					<td><span class="err companyCodeErr"></span></td>
				</tr>
				<tr>
					<td><span class="form-label">Status:</span></td>
					<td>
						<select name="status">
							<option value=""></option>
							<webthing:organizationStatusOptions />
						</select>
					</td>
					<td><span class="err statusErr"></span></td>
				</tr>
			</table>
		</div>
    </tiles:put>
		
</tiles:insert>

