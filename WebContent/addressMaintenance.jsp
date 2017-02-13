<%@ page contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib uri="WEB-INF/c.tld" prefix="c" %>
<%@ taglib uri="WEB-INF/sql.tld" prefix="sql" %>
<%@ taglib uri="WEB-INF/fmt.tld" prefix="fmt" %>
<%@ taglib uri="WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="WEB-INF/struts-bean.tld"  prefix="bean"  %>
<%@ taglib uri="WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing" %>
<%@ taglib uri="WEB-INF/theTagThing.tld" prefix="ansi" %>




<tiles:insert page="layout.jsp" flush="true">

    <tiles:put name="title" type="string">
        Address Maintenance
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
    <script type="text/javascript" src="js/ansi_utils.js"></script>
        <style type="text/css">
			#confirmDelete {
				display:none;
				background-color:#FFFFFF;
				color:#000000;
				width:300px;
				text-align:center;
				padding:15px;
			}
			#displayTable {
				width:90%;
			}
			#addFormDiv {
				display:none;
				background-color:#FFFFFF;
				color:#000000;
				width:400px;
				padding:15px;
			}
			#delData {
				margin-top:15px;
				margin-bottom:15px;
			}
			#state-menu {
			  max-height: 300px;
			}
			.prettyWideButton {
				height:30px;
				min-height:30px;
			}
			select	{
				width:80px !important;
				max-width:80px !important;
			}
			
			#addAddressForm{
				display: none;
			}
			
			#deleteErrorDialog{
				display: none;
			}
			
			#deleteConfirmDialog{
				display: none;
			}
		
			.dataTables_scrollBody, .dataTables_scrollHead, .display dataTable, .dataTables_scroll {
				width:1360px;
			}
			
			#ADDRESSPANEL_state-menu {max-height: 300px;}
			
        </style>
        
        <script type="text/javascript">        
        
        	$(document).ready(function() {
        	var dataTable = null;
        	var $addressPanelNamespace = "ADDRESSPANEL";
        	function createTable(){
        		var dataTable = $('#addressTable').DataTable( {
        			"processing": 		true,
        	        "serverSide": 		true,
        	        "autoWidth": 		false,
        	        "deferRender": 		true,
        	        "scrollCollapse": 	true,
        	        "scrollX": 			true,
        	        rowId: 				'dt_RowId',
        	        dom: 				'Bfrtip',
        	        "searching": 		true,
        	        lengthMenu: [
        	            [ 10, 25, 50, -1 ],
        	            [ '10 rows', '25 rows', '50 rows', 'Show all' ]
        	        ],
        	        buttons: [
        	        	'pageLength','copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print',{extend: 'colvis',	label: function () {doFunctionBinding();}}
        	        ],
        	        "paging": true,
			        "ajax": {
			        	"url": "addressTable",
			        	"type": "GET"
			        	},
			        columns: [
			            { title: "Id", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.addressId != null){return (row.addressId+"");}
			            } },
			            { title: "Name", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.name != null){return (row.name+"");}
			            } },
			            { title: "Status" , "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.status != null){return (row.status+"");}
			            } },
			            { title: "Address 1", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.address1 != null){return (row.address1+"");}
			            } },
			            { title: "Address 2",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.address2 != null){return (row.address2+"");}
			            } },
			            { title: "City", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.city != null){return (row.city+"");}
			            } },
			            { title: "County", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.county != null){return (row.county+"");}
			            } },
			            { title: "Country", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.country_code != null){return (row.country_code+"");}
			            } },
			            { title: "State", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) { 	
			            	if(row.state != null){return (row.state+"");}
			            } },
			            { title: "Zip", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.zip != null){return (row.zip+"");} 
			            } },
			            { title: "Action",  data: function ( row, type, set ) {	
			            	//console.log(row);
			            	if(row.count > 0)
			            		return "<ansi:hasPermission permissionRequired='SYSADMIN'><ansi:hasWrite><a href='#' class=\"editAction ui-icon ui-icon-pencil\" data-id='"+row.addressId+"'></a></ansi:hasWrite></ansi:hasPermission>";
			            	else
			            		return "<ansi:hasPermission permissionRequired='SYSADMIN'><ansi:hasWrite><a href='#' class=\"editAction ui-icon ui-icon-pencil\" data-id='"+row.addressId+"'></a>|<a href='#' data-id='"+row.addressId+"'  class='delAction ui-icon ui-icon-trash'></a></ansi:hasWrite></ansi:hasPermission>";
			            	
			            		
			            } }],
			            "initComplete": function(settings, json) {
			            	//console.log(json);
			            	doFunctionBinding();
			            },
			            "drawCallback": function( settings ) {
			            	doFunctionBinding();
			            }
			    } );
        	}
        	
        	$("#addButton").button().on( "click", function() {
        		$("#updateOrAdd").val("add");
        		clearAddForm();
        		$('#addFormButton').button('option', 'label', 'Add Address');
        	    $("#addAddressForm").dialog( "open" );
        	      	
            });
        	
        	
        	$( "#addAddressForm" ).dialog({
      	      autoOpen: false,
      	      height: 450,
      	      width: 500,
      	      modal: true,
      	      buttons: [{
      	    	id: 'addFormButton',
      	        click: function() {
      	        	addAddress();
      	        }
      	      },{
        	    	id: 'addFormCloseButton',
        	        click: function() {
        	        	$( "#addAddressForm" ).dialog( "close" );
        	        }
        	      }
      	      
      	      ],
      	      close: function() {
      	    	  $( "#addAddressForm" ).dialog( "close" );
      	        //allFields.removeClass( "ui-state-error" );
      	      }
      	    });
        	$('#addFormCloseButton').button('option', 'label', 'Close');
        	init();

        	function addAddress() {
        		$outbound = {};
        		$outbound["name"]		=	$("input[name="+$addressPanelNamespace+"_name]").val();
        		$outbound["status"]		=	$("input[name="+$addressPanelNamespace+"_status]").val();
        		$outbound["address1"]	=	$("input[name="+$addressPanelNamespace+"_address1]").val();
        		$outbound["address2"]	=	$("input[name="+$addressPanelNamespace+"_address2]").val();
        		$outbound["city"]		=	$("input[name="+$addressPanelNamespace+"_city]").val();
        		$outbound["county"]		=	$("input[name="+$addressPanelNamespace+"_county]").val();
        		$outbound["country_code"]	=	$("input[name="+$addressPanelNamespace+"country] option:selected").val();
        		$outbound["state"]		=	$("input[name="+$addressPanelNamespace+"state] option:selected").val();
        		$outbound["zip"]		=	$("input[name="+$addressPanelNamespace+"_zip]").val();
        		
        		if($("#updateOrAdd").val() =="add"){
				$url = "address/add";
				console.log($outbound);
				var jqxhr = $.ajax({
					type: 'POST',
					url: $url,
					data: JSON.stringify($outbound),
					success: function($data) {
						if ( $data.responseHeader.responseCode == 'SUCCESS') {

							clearAddForm();
							$( "#addAddressForm" ).dialog( "close" );
								if ( 'GLOBAL_MESSAGE' in $data.data.webMessages ) {
									$("#globalMsg").html($data.data.webMessages['GLOBAL_MESSAGE'][0]).fadeIn(10).fadeOut(6000);
								}
							
							$("#addressTable").DataTable().draw();
						} else if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {

							$.each($data.data.webMessages, function(key, messageList) {
								var identifier = "#" + key + "Err";
								msgHtml = "<ul>";
								$.each(messageList, function(index, message) {
									msgHtml = msgHtml + "<li>" + message + "</li>";
								});
								msgHtml = msgHtml + "</ul>";
								$(identifier).html(msgHtml);
							});		
							if ( 'GLOBAL_MESSAGE' in $data.data.webMessages ) {
								$("#addFormMsg").html($data.data.webMessages['GLOBAL_MESSAGE'][0]);
							}
							$( "#addAddressForm" ).dialog( "close" );
						} else {
							//alert("success other");
						}
					},
					error: function($data) {
						alert("fail");
						//console.log($data);
					},
					statusCode: {
						403: function($data) {
							$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
						} 
					},
					dataType: 'json'
				});
        		} else if($("#updateOrAdd").val() == "update"){
        			$url = "address/" + $("#aId").val();
        			
    				console.log($outbound);
    				var jqxhr = $.ajax({
    					type: 'POST',
    					url: $url,
    					data: JSON.stringify($outbound),
    					success: function($data) {
    						if ( $data.responseHeader.responseCode == 'SUCCESS') {
    							//alert("success");
    							console.log($data);
    							//createData();
    					
    							clearAddForm();
    							$( "#addAddressForm" ).dialog( "close" );
	    							if ( 'GLOBAL_MESSAGE' in $data.data.webMessages ) {
	    								$("#globalMsg").html($data.data.webMessages['GLOBAL_MESSAGE'][0]).fadeIn(10).fadeOut(6000);
	    							}
    							
    							$("#addressTable").DataTable().draw();
    						} else if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
    							//alert("success fail");
    							$.each($data.data.webMessages, function(key, messageList) {
    								var identifier = "#" + key + "Err";
    								msgHtml = "<ul>";
    								$.each(messageList, function(index, message) {
    									msgHtml = msgHtml + "<li>" + message + "</li>";
    								});
    								msgHtml = msgHtml + "</ul>";
    								$(identifier).html(msgHtml);
    							});		
    							if ( 'GLOBAL_MESSAGE' in $data.data.webMessages ) {
    								$("#addFormMsg").html($data.data.webMessages['GLOBAL_MESSAGE'][0]);
    							}
    							$( "#addAddressForm" ).dialog( "close" );
    						} else {
    							//alert("success other");
    						}
    					},
    					error: function($data) {
    						alert("fail");
    						//console.log($data);
    					},
    					statusCode: {
    						403: function($data) {
    							$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
    						} 
    					},
    					dataType: 'json'
    				});        			
        		}
				$( "#addAddressForm" ).dialog( "close" );
        	}
        	
        	
        	
            function clearAddForm() {
	        	$("input[name="+$addressPanelNamespace+"_name]").val("");
	        	$("input[name="+$addressPanelNamespace+"_status] option[value='']").attr('selected', true);
	        	$("input[name="+$addressPanelNamespace+"_address1]").val("");
	        	$("input[name="+$addressPanelNamespace+"_address2]").val("");
	        	$("input[name="+$addressPanelNamespace+"_city]").val("");
	        	$("input[name="+$addressPanelNamespace+"_county]").val("");
	        	$("#"+$addressPanelNamespace+"_country")[0].selectedIndex = 0;
	        	$("#"+$addressPanelNamespace+"_country").selectmenu("refresh");
	        	$("#"+$addressPanelNamespace+"_state")[0].selectedIndex = 0;
	        	$("#"+$addressPanelNamespace+"_state").selectmenu("refresh");
	        	$("input[name="+$addressPanelNamespace+"_zip]").val("");
            }
            
            
            
            function init(){
					
					
					$optionData = ANSI_UTILS.getOptions('COUNTRY');
					//console.log($optionData);
					var $countryList = $optionData.country;

					$jobSiteDetail = "";

					ADDRESSPANEL.init($addressPanelNamespace, $countryList, $jobSiteDetail);
					
					$.each($('input'), function () {
				        $(this).css("height","20px");
				        $(this).css("max-height", "20px");
				    });
					$("#"+$addressPanelNamespace+"_state").selectmenu({ width : '120px', maxHeight: '400 !important', style: 'dropdown'});
					$("#"+$addressPanelNamespace+"_country").selectmenu({ width : '80px', maxWidth: '80px', maxHeight: '400 !important', style: 'dropdown'});
					
					createTable();
            }
            
				
			function doFunctionBinding() {
				$( ".editAction" ).on( "click", function($clickevent) {
					$('#addFormButton').button('option', 'label', 'Update Address');
					 doEdit($clickevent);
				});
				$('.delAction').on('click', function($clickevent) {
					doDelete($clickevent);
				});
				
				$('#addressTable_next').on('click', function($clickevent) {
	        		window.scrollTo(0, 0);
	        	});
			}
			
			function doEdit($clickevent) {
				var $rowid = $clickevent.currentTarget.attributes['data-id'].value;

					var $url = 'address/' + $rowid;
					//console.log("YOU PASSED ROW ID:" + $rowid);
					var jqxhr = $.ajax({
						type: 'GET',
						url: $url,
						success: function($data) {
							//console.log($data);
							
			        		$("input[name="+$addressPanelNamespace+"_name]").val(($data.data.codeList[0]).name);
			        		$("input[name="+$addressPanelNamespace+"_status]").val(($data.data.codeList[0]).status);
			        		$("input[name="+$addressPanelNamespace+"_address1]").val(($data.data.codeList[0]).address1);
			        		$("input[name="+$addressPanelNamespace+"_address2]").val(($data.data.codeList[0]).address2);
			        		$("input[name="+$addressPanelNamespace+"_city]").val(($data.data.codeList[0]).city);
			        		$("input[name="+$addressPanelNamespace+"_county]").val(($data.data.codeList[0]).county);
			        		$("#"+$addressPanelNamespace+"country").val(($data.data.codeList[0]).country_code);
			        		$("#"+$addressPanelNamespace+"country").selectmenu("refresh");
			        		$("#"+$addressPanelNamespace+"state").val(($data.data.codeList[0]).state);
			        		$("#"+$addressPanelNamespace+"state").selectmenu("refresh");
			        		$("input[name="+$addressPanelNamespace+"_zip").val(($data.data.codeList[0]).zip);
			        		
			        		$("#aId").val(($data.data.codeList[0]).addressId);
			        		$("#updateOrAdd").val("update");
			        		$("#addAddressForm").dialog( "open" );
						},
						statusCode: {
							403: function($data) {
								$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
							} 
						},
						dataType: 'json'
					});
				//console.log("Edit Button Clicked: " + $rowid);
			}
			
			function doDelete($clickevent) {
				$clickevent.preventDefault();
				
				var $rowid = $clickevent.currentTarget.attributes['data-id'].value;
				
				
				 $( "#deleteConfirmDialog" ).dialog({
				      resizable: false,
				      height: "auto",
				      width: 400,
				      modal: true,
				      buttons: {
				        "Delete Address": function() {doDelete2($rowid);$( this ).dialog( "close" );},
				        Cancel: function() {
				          $( this ).dialog( "close" );
				        }
				      }
				    });
				 $( "#deleteConfirmDialog" ).dialog( "open" );
	            	
			}
			
			function doDelete2($rowid){
				$url = 'address/' + $rowid;
            	var jqxhr = $.ajax({
            	    type: 'delete',
            	    url: $url,
            	    success: function($data) {
            	    	$("#globalMsg").html($data.responseHeader.responseMessage).fadeIn(10).fadeOut(6000);
						if ( $data.responseHeader.responseCode == 'SUCCESS') {
							$("#addressTable").DataTable().row($rowid).remove();
							$("#addressTable").DataTable().draw();
						}
            	     },
            	     statusCode: {
            	    	403: function($data) {
            	    		$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
            	    	},
            	    	500: function($data) {
            	    		 $( "#deleteErrorDialog" ).dialog({
            	    		      modal: true,
            	    		      buttons: {
            	    		        Ok: function() {
            	    		          $( this ).dialog( "close" );
            	    		        }
            	    		      }
            	    		    });
            	    	} 
            	     },
            	     dataType: 'json'
            	});
			}
        });
        </script>        
    </tiles:put>
    
    <tiles:put name="content" type="string">
    	<h1>Address Maintenance</h1>
    	
 	<table id="addressTable" class="display" cellspacing="0" style="font-size:9pt;">
        <thead>
            <tr>
                <th>Id</th>
    			<th>Name</th>
    			<th>Status</th>
    			<th>Address 1</th>
    			<th>Address 2</th>
    			<th>City</th>
    			<th>County</th>
    			<th>Country</th>
    			<th>State</th>
    			<th>Zip</th>
    			<th>Action</th>
            </tr>
        </thead>
        <tfoot>
            <tr>
                <th>Id</th>
    			<th>Name</th>
    			<th>Status</th>
    			<th>Address 1</th>
    			<th>Address 2</th>
    			<th>City</th>
    			<th>County</th>
    			<th>Country</th>
    			<th>State</th>
    			<th>Zip</th>
    			<th>Action</th>
    			
            </tr>
        </tfoot>
    </table>
    <ansi:hasPermission permissionRequired="SYSADMIN">
			<ansi:hasWrite>
    			<div class="addButtonDiv">
    				<input type="button" id="addButton" class="prettyWideButton" value="New" />
    			</div>
			</ansi:hasWrite>
		</ansi:hasPermission>
    </tiles:put>

		<div id="deleteErrorDialog" title="Delete Failed!" class="ui-widget" style="display:none;">
		  <p>Address failed to delete. It may still be assigned to existing quotes.</p></input>
		</div>

		<div id="addAddressForm" title="Add/Update Address" class="ui-widget" style="display:none;">
		  <p class="validateTips">All form fields are required.</p>
		 
		  <form id="addForm">
			<webthing:addressPanel label="Name" namespace="ADDRESSPANEL" cssId="addressPanel" />
		  </form>
		</div>
		
	
		<div id="deleteConfirmDialog" title="Delete Address?" style="display:none;">
  			<p></span>Are you sure you would like to delete this address?</p>
		</div>
		
		<input  type="text" id="updateOrAdd" style="display:none"><input  type="text" id="aId" style="display:none">
</tiles:insert>

