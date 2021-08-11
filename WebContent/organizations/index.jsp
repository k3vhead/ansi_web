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
        	.action-link {
        		text-decoration:none;
        	}
			.dataTables_wrapper {
				padding-top:10px;
			}	
			.form-label {
				font-weight:bold;
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
        		
        		init : function() {
        			$("h1 .organization-type-display").html(ORGMAINT.orgTypeDisplay);
        			ORGMAINT.getOrgs(); 
        			ORGMAINT.makeModals();
        		},
        		
        		
        		getOrganizationDetail : function($organizationId, $filter) {
        			// $filter says whether to retrieve all children (false) or just the children of this org (true)
        			console.log("viewOrg: " + $organizationId);
        			var $url = "organization/" + ORGMAINT.orgType + "/" + $organizationId;
        			var $callbacks = {
        				200 : ORGMAINT.showOrgDetail,
        				404 : ORGMAINT.getOrgsFail,
        			}        			
        			$outbound = {"filter":$filter};
        			ANSI_UTILS.makeServerCall("GET", $url, $outbound, $callbacks, {});
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
       					"edit":true,
       					"source":"orgList",
        			}
        			ANSI_UTILS.makeServerCall("GET", $url, {}, $callbacks, $passthru);
        		},
        		
        		
        		
        		getOrgsFail : function($data, $passthru) {
        			console.log("getOrgsFail");
        			$("#globalMsg").html("Invalid organization type");        			
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
        		},
        		
        		
        		
        		
        		
        		
        		makeOrgTable : function($data, $passthru) {
        			console.log("makeOrgTable");  
        			
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
		    				{ width:"10%", title:"Action", className:"dt-center", orderable:true, visible:$passthru['edit'],
		    					data:function(row,type,set) {
	    							$viewLink = '<a href="#" class="action-link view-link" data-id="'+row.organizationId+'"><webthing:view>View</webthing:view></a>';
		    						return $viewLink;
		    					},		    					
		    				},
	    				],
	    				"drawCallback": function( settings ) {
	    					console.log("drawCallback");
	    					$(".action-link").off("click");
	    					$(".view-link").click(function($clickevent) {
	    						var $organizationId = $(this).attr("data-id");
	    						ORGMAINT.getOrganizationDetail($organizationId, true);
	    					});
	    					$(".edit-link").click(function($clickevent) {
	    						var $organizationId = $(this).attr("data-id");
	    						ORGMAINT.getOrganizationDetail($organizationId, false);
	    					});
	    				},
        			});
        			
	       			// var myTable $($passthru['destination']).DataTable();
					// myTable.columns(columnNumber).visible(true);
        		},
        		
        		
        		showOrgDetail : function($data, $passthru) {
        			console.log("showOrgDetail");
        			
        			var $organization = $data.data.organization;
        			var $active = '<webthing:checkmark>Active</webthing:checkmark>';
        			var $inactive = '<webthing:ban>Inactive</webthing:ban>';
        			var $unknown = '<webthing:questionmark>Unknown</webthing:questionmark>';

        			if ( $organization.status == 0 ) { $status = $inactive}
					else if ( $organization.status == 1 ) { $status = $active }
					else { $status = $unknown }

        			$("#organization-display .organization-id").html($organization.organizationId);
        			$("#organization-display .name").html($organization.name);
        			$("#organization-display .status").html($status);
        			$("#organization-display .parent-name").html($organization.parentName);
        			$("#organization-display .parent-type").html($organization.parentType);
        			
        			var $displayDetail = {
        				"destination":"#organization-display .organization-children",
        				"edit":false,
        				"source":"childList",
        			}
        			ORGMAINT.makeOrgTable($data, $displayDetail)
        			
        			
        			$("#organization-display").dialog("open");
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
			<div style="width:100%; height:12px;" class="organization-msg"></div>
			<table>
				<tr>
					<th>ID</th>
					<th>Name</th>
					<th>Parent</th>
					<th>Parent Type</th>
					<th>Status</th>					
				</tr>
				<tr>
					<td><span class="organization-id"></span></td>
					<td><span class="name"></span></td>
					<td><span class="parent-name"></span></td>
					<td><span class="parent-type"></span></td>					
					<td><span class="status"></span></td>
				</tr>
			</table>
			<table class="organization-children">
			</table>
		</div>
		
    </tiles:put>
		
</tiles:insert>

