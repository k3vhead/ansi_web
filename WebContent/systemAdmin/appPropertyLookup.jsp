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
<%@ taglib tagdir="/WEB-INF/tags/bcr" prefix="bcr" %>
<%@ taglib uri="/WEB-INF/theTagThing.tld" prefix="ansi" %>




<tiles:insert page="../layout.jsp" flush="true">

    <tiles:put name="title" type="string">
        Application Properties
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
    	<link rel="stylesheet" href="css/lookup.css" />
    	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.9.4/Chart.min.js"></script>
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/lookup.js"></script> 
        <style type="text/css">
        	#confirmation_modal {
        		display:none;
        	}
        	#filter-container {
        		width:402px;
        		float:right;
        	}
			.action-link {
				cursor:pointer;
				text-decoration:none;
			}
        	.aligned-center {
        		text-align:center;
        	}
        	.aligned-right {
        		text-align:right;
        	}
			.column-header {
				text-align:center;
				font-weight:bold;
				background-color:#CCCCCC;
			}
			.column-subheader {
				text-align:center;
				font-weight:bold;
				background-color:#EEEEEE;
			}
        	.form-label {
				font-weight:bold;
			}
			.this-is-a-modal {
				display:none;
			}
			.view-link {
				color:#000000;
			}
        </style>
        
        <script type="text/javascript">
        
        $(document).ready(function() {
        	;APP_PROPERTIES = {      
        		propertiesTable : null,
        		propertyMap : {},
        		
        		init : function() {
        			ANSI_UTILS.makeServerCall("GET", "sysAdmin/appPropertyLookup", {}, {200:APP_PROPERTIES.makeLookupTable}, {});
        		},
        		
        		
        		
        		editProperty : function($propertyId) {
        			console.log("editProperty")
        			console.log(APP_PROPERTIES.propertyMap[$propertyId]);
        			
        			if ( ! $("#edit_modal").hasClass('ui-dialog-content') ) {
        				$( "#edit_modal" ).dialog({
            				title:'Edit Property',
            				autoOpen: false,
            				height: 270,
            				width: 900,
            				modal: true,
            				closeOnEscape:true,
            				//open: function(event, ui) {
            				//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
            				//},
            				buttons: [
            					{
            						id:  "edit_modal_cancel",
            						click: function($event) {
           								$( "#edit_modal" ).dialog("close");
            						}
            					},
            					{
            						id:  "edit_modal_save",
            						click: function($event) {
           								var $outbound = {
            								"property_id":$("#edit_modal input[name='property_id']").val(),
           									"value":$("#edit_modal input[name='value']").val(),
            							}
            							var $callbacks = {
            								200 : APP_PROPERTIES.savePropertySuccess
            							}
            							ANSI_UTILS.makeServerCall("POST", "sysAdmin/appProperty", $outbound, $callbacks, {});
            						}
            					}
            				]
            			});	
            			$("#edit_modal_cancel").button('option', 'label', 'Cancel'); 
            			$("#edit_modal_save").button('option', 'label', 'Save'); 
        			}
        			var $yesTag = '<webthing:checkmark>Yes</webthing:checkmark>';
        			var $noTag = '<webthing:ban>No</webthing:ban>';
        			
        			$("#edit_modal .property_id").html($propertyId);
   	    			$("#edit_modal .desc").html(APP_PROPERTIES.propertyMap[$propertyId]['desc']);
   	    			$("#edit_modal .format").html(APP_PROPERTIES.propertyMap[$propertyId]['format']);
   	    			if ( APP_PROPERTIES.propertyMap[$propertyId]['enum'] == true ) {
   	    				$("#edit_modal .enum").html($yesTag);
   	    			} else {
   	    				$("#edit_modal .enum").html($noTag);
   	    			}
   	    			if ( APP_PROPERTIES.propertyMap[$propertyId]['db'] == true ) {
   	    				$("#edit_modal .db").html($yesTag);
   	    			} else {
   	    				$("#edit_modal .db").html($noTag);
   	    			}
   	    			$("#edit_modal .err").html("");
   	    			
   	    			var $propFormat = APP_PROPERTIES.propertyMap[$propertyId]['format'];
   	    			if ( $propFormat == 'String' ) {
   	    				$("#edit_modal input[name='value']").attr("type","text");
   	    				$("#edit_modal input[name='value']").removeAttr("step");
   	    				$("#edit_modal input[name='value']").removeAttr("placeholder");
   	    			} else if ( $propFormat == 'Integer' ) {
   	    				$("#edit_modal input[name='value']").attr("type", "number");
   	    				$("#edit_modal input[name='value']").attr("step","1");
   	    				$("#edit_modal input[name='value']").attr("placeholder","123");
   	    			} else if ( $propFormat == 'Decimal' ) {
   	    				$("#edit_modal input[name='value']").attr("type", "number");
   	    				$("#edit_modal input[name='value']").attr("step",".01");
   	    				$("#edit_modal input[name='value']").attr("placeholder","123.45");
   	    			} else if ( $propFormat == 'Date' ) {
   	    				$("#edit_modal input[name='value']").attr("type", "datetime-local");
   	    				$("#edit_modal input[name='value']").removeAttr("step");
   	    				$("#edit_modal input[name='value']").removeAttr("placeholder");
   	    			}
   	    			
   	    			$("#edit_modal input[name='property_id']").val($propertyId);
   	    			$("#edit_modal input[name='value']").val(APP_PROPERTIES.propertyMap[$propertyId]['value']);
   	    			
        			$("#edit_modal").dialog("open");
        		},
        		
        		
        		
        		
        		makeLookupTable : function($data, $passthru) {
        			console.log("makeLookupTable");
        			
        			APP_PROPERTIES.propertyMap = {};
        			$.each($data.data.propertyList, function($index, $property) {
        				APP_PROPERTIES.propertyMap[$property["property_id"]] = $property;
        			});
        			
        			var $fileName = "appProperties";
        			var $buttonArray = [
        	        	'pageLength',
        	        	'copy', 
        	        	{extend:'csv', filename:'* ' + $fileName}, 
        	        	{extend:'excel', filename:'* ' + $fileName}, 
        	        	{extend:'pdfHtml5', orientation: 'landscape', filename:'* ' + $fileName}, 
        	        	'print'
        	        ];
        			        		
        			var $editTag = '<webthing:edit>Edit</webthing:edit>';
        			var $dbYes = '<webthing:checkmark>DB</webthing:checkmark>';
        			var $enumYes = '<webthing:checkmark>Enum</webthing:checkmark>';
        			var $dbNo = '<webthing:ban>DB</webthing:ban>';
        			var $enumNo = '<webthing:ban>Enum</webthing:ban>';
        			
        			
        			APP_PROPERTIES.propertiesTable = $("#propertiesTable").DataTable( {
        				data:               $data.data.propertyList,
            			"aaSorting":		[[0,'asc']],
            			"processing": 		true,
            	        "serverSide": 		false,
            	        "autoWidth": 		false,
            	        "deferRender": 		true,
            	        "scrollCollapse": 	true,
            	        "scrollX": 			true,
            	        "pageLength":		50,
            	        rowId: 				'dt_RowId', 
            	        destroy : 			true,		// this lets us reinitialize the table
            	        dom: 				'Bfrtip',
            	        "searching": 		true,
            	        "searchDelay":		800,
            	        buttons: $buttonArray,
            	        "columnDefs": [
             	            { "orderable": true, "targets": -1 },
             	            { className: "dt-head-center", "targets":[]},
            	            { className: "dt-left", "targets": [] },
            	            { className: "dt-center", "targets": [] },
            	            { className: "dt-right", "targets": []}
            	         ],
            	        "paging": false,    			        
    			        columns: [
    			        	{ title: "ID", width:"25%", searchable:true, "defaultContent": "", data:'property_id' }, 
    			            { title: "Value", width:"20%", searchable:true, "defaultContent": "", data:'value_trunc' },
    			            { title: "Format", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'format' },
    			            { title: "Description", width:"25%", searchable:true, "defaultContent": "<i>N/A</i>", data:'desc_trunc' },
    			            { title: "Enum|DB", width:"5%", searchable:false, "defaultContent":"",
    			            	data: function ( row, type, set ) {
    			            		if ( row.db == true ) {
    			            			$dbTag = $dbYes;
    			            		} else {
										$dbTag = $dbNo;
    			            		}
    			            		if ( row.enum == true ) {
    			            			$enumTag = $enumYes;
    			            		} else {
										$enumTag = $enumNo;
    			            		}
    			            		return $enumTag + "|" + $dbTag;
    			            	}
    			            },
    			            { title: "action", width:"5%", searchable:false, 
    			            	data: function ( row, type, set ) {
    			            		var $viewLink = '<a href="#" class="action-link view-link" data-id="'+row.property_id+'"><webthing:view>View</webthing:view></a>';
        			            	var $editLink = '<ansi:hasPermission permissionRequired="SYSADMIN_WRITE"><a href="#" class="action-link edit-link" data-id="'+row.property_id+'"><webthing:edit>Edit</webthing:edit></a></ansi:hasPermission>';
       			            		return $viewLink + $editLink;
    			            	} 
    			            },
    			            ],
    			            "initComplete": function(settings, json) {
    			            	var myTable = this;
    			            	//LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#ticketTable", CALL_NOTE_LOOKUP.makeTable);
    			            },
    			            "drawCallback": function( settings ) {
    			            	//APP_PROPERTIES.doFunctionBinding();
    			            	$("#propertiesTable .view-link").off("click");
    			            	$("#propertiesTable .view-link").click(function($clickevent) {
    			            		$clickevent.preventDefault()
    			            		var $propertyId = $(this).attr("data-id");
    			            		APP_PROPERTIES.viewProperty($propertyId);
    			            	});
    			            	
    			            	$("#propertiesTable .edit-link").off("click");
    			            	$("#propertiesTable .edit-link").click(function($clickevent) {
    			            		$clickevent.preventDefault()
    			            		var $propertyId = $(this).attr("data-id");
    			            		APP_PROPERTIES.editProperty($propertyId);
    			            	});
    			            }
    			    } );
        		},
        		
        		
        		
        		savePropertySuccess : function($data, $passthru) {
        			console.log("savePropertySuccess")
					if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
						if ( $data.data.webMessages['value'] != null ) {
							$("#edit_modal .value_err").html( $data.data.webMessages['value'][0]);
						}
						if ( $data.data.webMessages['property_id'] != null ) {
							$("#edit_modal .property_id_err").html( $data.data.webMessages['property_id'][0]);
						}
					} else if ( $data.responseHeader.responseCode == 'SUCCESS') {
						$("#globalMsg").html("Success!").show().fadeOut(4000);
						$("#edit_modal").dialog("close");
						APP_PROPERTIES.makeLookupTable($data, $passthru);
					} else {
						$("#globalMsg").html("Unexpected response: " + $data.responseHeader.responseCode + ". Contact Support").show();
					}
        		},
        		
        		
        		viewProperty : function($propertyId) {
        			console.log("viewProperty")
        			console.log(APP_PROPERTIES.propertyMap[$propertyId]);
        			
        			if ( ! $("#view_modal").hasClass('ui-dialog-content') ) {
        				$( "#view_modal" ).dialog({
            				title:'View Property',
            				autoOpen: false,
            				height: 270,
            				width: 600,
            				modal: true,
            				closeOnEscape:true,
            				//open: function(event, ui) {
            				//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
            				//},
            				buttons: [
            					{
            						id:  "view_modal_cancel",
            						click: function($event) {
           								$( "#view_modal" ).dialog("close");
            						}
            					}
            				]
            			});	
            			$("#view_modal_cancel").button('option', 'label', 'Done');  
        			}
        			
        			var $yesTag = '<webthing:checkmark>Yes</webthing:checkmark>';
        			var $noTag = '<webthing:ban>No</webthing:ban>';

        			$("#view_modal .property_id").html($propertyId);
   	    			$("#view_modal .desc").html(APP_PROPERTIES.propertyMap[$propertyId]['desc']);
   	    			$("#view_modal .value").html(APP_PROPERTIES.propertyMap[$propertyId]['value']);
   	    			$("#view_modal .format").html(APP_PROPERTIES.propertyMap[$propertyId]['format']);
   	    			if ( APP_PROPERTIES.propertyMap[$propertyId]['enum'] == true ) {
   	    				$("#view_modal .enum").html($yesTag);
   	    			} else {
   	    				$("#view_modal .enum").html($noTag);
   	    			}
   	    			if ( APP_PROPERTIES.propertyMap[$propertyId]['db'] == true ) {
   	    				$("#view_modal .db").html($yesTag);
   	    			} else {
   	    				$("#view_modal .db").html($noTag);
   	    			}
        			$("#view_modal").dialog("open");
        		}
        	};
        	

        	APP_PROPERTIES.init();
        	
        });
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Application Properties</h1>
    	
    	<div id="propertiesTableContainer">
    		<table id="propertiesTable">
    		</table>
    	</div>
   		
   	    <webthing:lookupFilter filterContainer="filter-container" />
    	
	    <webthing:scrolltop />
    

	    
	    
	    <div id="confirmation_modal">
	    	<div style="width:100%; text-align:center;">
	    		<h2>Are you sure?</h2>
	    	</div>
	    </div>
	    
	    
	    <div id="view_modal" class="this-is-a-modal">
	    	<table>
	    		<tr>
	    			<td><span class="form-label">Property Id:</span></td>
	    			<td><span class="property_id"></span></td>
	    		</tr>
	    		<tr>
	    			<td><span class="form-label">Description:</span></td>
	    			<td><span class="desc"></span></td>
	    		</tr>
	    		<tr>
	    			<td><span class="form-label">Value:</span></td>
	    			<td><span class="value"></span></td>
	    		</tr>
	    		<tr>
	    			<td><span class="form-label">Format:</span></td>
	    			<td><span class="format"></span></td>
	    		</tr>
	    		<tr>
	    			<td><span class="form-label">Valid Enum:</span></td>
	    			<td><span class="enum"></span></td>
	    		</tr>
	    		<tr>
	    			<td><span class="form-label">Database:</span></td>
	    			<td><span class="db"></span></td>
	    		</tr>
	    	</table>
	    </div>
	    
	    
	    
	    <div id="edit_modal" class="this-is-a-modal">
	    	<input type="hidden" name="property_id" />
	    	<table>
	    		<tr>
	    			<td><span class="form-label">Property Id:</span></td>
	    			<td><span class="property_id"></span></td>
	    			<td><span class="property_id_err err"></span></td>
	    		</tr>
	    		<tr>
	    			<td><span class="form-label">Description:</span></td>
	    			<td><span class="desc"></span></td>
	    			<td><span class="desc_err err"></span></td>
	    		</tr>
	    		<tr>
	    			<td><span class="form-label">Value:</span></td>
	    			<td><input name="value" /></td>
	    			<td><span class="value_err err"></span></td>
	    		</tr>
	    		<tr>
	    			<td><span class="form-label">Format:</span></td>
	    			<td><span class="format"></span></td>
	    			<td><span class="format_err err"></span></td>
	    		</tr>
	    		<tr>
	    			<td><span class="form-label">Valid Enum:</span></td>
	    			<td><span class="enum"></span></td>
	    			<td><span class="enum_err err"></span></td>
	    		</tr>
	    		<tr>
	    			<td><span class="form-label">Database:</span></td>
	    			<td><span class="db"></span></td>
	    			<td><span class="db_err err"></span></td>
	    		</tr>
	    	</table>
	    </div>

			
    </tiles:put>
		
</tiles:insert>

