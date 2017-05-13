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
    <script type="text/javascript" src="js/addressUtils.js"></script>
        <style type="text/css">
        	td { border:solid 1px #FF000;}
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
			
			.formLabel {
				font-weight:bold;
			}
			#ADDRESSPANEL_state-menu {max-height: 300px;}
			.viewAction {
				cursor:pointer;
				text-decoration:none;
				color:#000000;
			}
			#addressView {
				display:none;
			}
        </style>
        
        <script type="text/javascript">        
        
        	$(document).ready(function() {
        		var $ansiModal = '<c:out value="${ANSI_MODAL}" />';
        		
        		var dataTable = null;
        		// var $addressPanelNamespace = "ADDRESSPANEL";
        		
        		
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
	        	        "columnDefs": [
	        	            { "orderable": false, "targets": -1 }
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
				            	if(row.address_status != null){return (row.address_status+"");}
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
				            	if(row.countryCode != null){return (row.countryCode+"");}
				            } },
				            { title: "State", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) { 	
				            	if(row.state != null){return (row.state+"");}
				            } },
				            { title: "Zip", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
				            	if(row.zip != null){return (row.zip+"");} 
				            } },
				            { title: "Action",  data: function ( row, type, set ) {	
				            	if(row.count > 0)
				            		return "<a href=\"#\" class=\"viewAction fa fa-search-plus\" aria-hidden=\"true\" data-id='"+row.addressId+"'></a> | <ansi:hasPermission permissionRequired='SYSADMIN'><ansi:hasWrite><a href='#' class=\"editAction ui-icon ui-icon-pencil\" data-id='"+row.addressId+"'></a></ansi:hasWrite></ansi:hasPermission>";
				            	else
				            		return "<a href=\"#\" class=\"viewAction fa fa-search-plus\" aria-hidden=\"true\" data-id='"+row.addressId+"'></a> | <ansi:hasPermission permissionRequired='SYSADMIN'><ansi:hasWrite><a href='#' class=\"editAction ui-icon ui-icon-pencil\" data-id='"+row.addressId+"'></a>|<a href='#' data-id='"+row.addressId+"'  class='delAction ui-icon ui-icon-trash'></a></ansi:hasWrite></ansi:hasPermission>";
				            	
				            		
				            } }],
				            "initComplete": function(settings, json) {
				            	doFunctionBinding();
				            },
				            "drawCallback": function( settings ) {
				            	doFunctionBinding();
				            }
				    	} );
        		}
        	
    			function markValid($item) {
    	    		$item.removeClass("fa");
    	    		$item.removeClass("fa-ban");
    				$item.removeClass("inputIsInvalid");

    				$item.addClass("fa");
    	    		$item.addClass("fa-check-square-o");
    				$item.addClass("inputIsValid");
    			}
    			
    			function markInvalid($item) {
    				$item.removeClass("fa");
    	    		$item.removeClass("fa-check-square-o");
    				$item.removeClass("inputIsValid");

    				$item.addClass("fa");
    	    		$item.addClass("fa-ban");
    				$item.addClass("inputIsInvalid");
    			}

	        	$("#addButton").button().on( "click", function() {
	        		$("#updateOrAdd").val("add");
	        		clearAddForm();
	        		$('#addFormButton').button('option', 'label', 'Add Address');
	        	    $("#addAddressForm").dialog( "open" );
	        	      	
	            });
        	
        	
        		$( "#addAddressForm" ).dialog({
					autoOpen: false,
					height: "auto",
					width: 520,
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
					}],
					close: function() {
						$( "#addAddressForm" ).dialog( "close" );
						//allFields.removeClass( "ui-state-error" );
					}
				});
        		
        		
        		$( "#addressView" ).dialog({
					title:"View Address",        			
					autoOpen: false,
					height: "auto",
					width: 520,
					modal: true,
					buttons: [{
						id: 'closeViewButton',
						click: function() {
							$( "#addressView" ).dialog( "close" );
						}
					}],
					close: function() {
						$( "#addressView" ).dialog( "close" );
						//allFields.removeClass( "ui-state-error" );
					}
				});
        		
        		
				$('#addFormCloseButton').button('option', 'label', 'Close');
				$('#closeViewButton').button('option', 'label', 'Close');
				init();

        		function addAddress() {
        			clearErrIcons();
	        		$outbound = {};
	        		$.each($("#addForm input"), function($idx, $value) {
	        			$outbound[$(this).attr("name")] = $(this).val();
	        		});
	        		$.each($("#addForm select"), function($idx, $value) {
	        			$outbound[$(this).attr("name")] = $(this).val();
	        		});
        		
        			if($("#updateOrAdd").val() =="add"){
						$url = "address/add";
        			} else if($("#updateOrAdd").val() == "update"){
        				$url = "address/" + $("#aId").val();
        			}

					var jqxhr = $.ajax({
						type: 'POST',
						url: $url,
						data: JSON.stringify($outbound),
						statusCode: {
							200: function($data) {
								if ( $data.responseHeader.responseCode == 'SUCCESS') {
									clearAddForm();
									$( "#addAddressForm" ).dialog( "close" );
									if ( 'GLOBAL_MESSAGE' in $data.data.webMessages ) {
										$("#globalMsg").html($data.data.webMessages['GLOBAL_MESSAGE'][0]).fadeIn(10).fadeOut(6000);
									}
									$("#addressTable").DataTable().draw();
									$( "#addAddressForm" ).dialog( "close" );
								} else if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
									$.each($data.data.webMessages, function(key, messageList) {
										var $identifier = "#" + key + "Err";
										markInvalid( $($identifier) );
									});		
									if ( 'GLOBAL_MESSAGE' in $data.data.webMessages ) {
										$("#addFormMsg").html($data.data.webMessages['GLOBAL_MESSAGE'][0]).fadeOut(6000);
									} else {
										$("#addFormMsg").html("Invalid/Missing Data").show().fadeOut(6000);
									}
								} else {
									$("#addFormMsg").html("System Error: Contact Support").show();
								}
							},
							403: function($data) {
								$("#addFormMsg").html("Session Timeout. Log in and try again").show();
							}, 
							404: function($data) {
								$("#addFormMsg").html("System Error 404: Contact Support").show();
							}, 
							405: function($data) {
								$("#addFormMsg").html("System Error 405: Contact Support").show();
							}, 
							500: function($data) {
								$("#addFormMsg").html("System Error 500: Contact Support").show();
							} 
						},
						dataType: 'json'
					});
        		}
        	
        	
        	
	            function clearAddForm() {
	            	$("#addForm input").val("");
		        	$("#addForm select")[0].selectedIndex = 0;
		        	clearErrIcons();
	            }
	            
	            function clearErrIcons() {
		        	$("#addForm .errIcon").removeClass("fa");
		        	$("#addForm .errIcon").removeClass("fa-ban");
		        	$("#addForm .errIcon").removeClass("inputIsInvalid");
		        	$("#addForm .errIcon").removeClass("fa-check-square-o");
		        	$("#addForm .errIcon").removeClass("inputIsValid");
		        	
	            }
            
            
            
	            function init(){
					$optionData = ANSI_UTILS.getOptions('COUNTRY');
					var $countryList = $optionData.country;
					$jobSiteDetail = "";

					//ADDRESSPANEL.init($addressPanelNamespace, $countryList, $jobSiteDetail);
					$('option', "#addAddressForm select[name='countryCode']").remove();
					$('option', "#addAddressForm select[name='state']").remove();
					$("#addAddressForm select[name='countryCode']").append(new Option("", ""));
					$("#addAddressForm select[name='state']").append(new Option("", ""));
	                $.each($countryList, function($index, $value) {
	                	$("#addAddressForm select[name='countryCode']").append(new Option($value.display, $value.abbrev));
	                	
	                	var $optGroup = $("<optgroup>");
	                	$optGroup.attr("label",$value.display);
	                	$.each($value.stateList, function($stateIndex, $stateValue) {
	                		$optGroup.append(new Option($stateValue.display, $stateValue.abbreviation));
	                	});
	                	$("#addAddressForm select[name='state']").append($optGroup);
	                });

					
					$.each($('input'), function () {
				        $(this).css("height","20px");
				        $(this).css("max-height", "20px");
				    });
					//$("#"+$addressPanelNamespace+"_state").selectmenu({ width : '120px', maxHeight: '400 !important', style: 'dropdown'});
					//$("#"+$addressPanelNamespace+"_country").selectmenu({ width : '80px', maxWidth: '80px', maxHeight: '400 !important', style: 'dropdown'});
					
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
					$('.viewAction').on('click', function($clickevent) {
						doView($clickevent);
					});
				}
	            
	            function doView($clickEvent) {
	            	$clickEvent.preventDefault();
					var $addressId = $clickEvent.currentTarget.attributes['data-id'].value;
					ADDRESS_UTILS.getAddress($addressId, "#addressView");
					$("#addressView").dialog("open");
	            }
			
				function doEdit($clickevent) {
					var $rowid = $clickevent.currentTarget.attributes['data-id'].value;

					var $url = 'address/' + $rowid;
					var jqxhr = $.ajax({
						type: 'GET',
						url: $url,
						statusCode: {
							200: function($data) {
								clearAddForm();
								var $address = $data.data.addressList[0];  // we're only getting one address
								$.each($address, function($fieldName, $value) {									
									$selector = "#addForm input[name=" + $fieldName + "]";
									if ( $($selector).length > 0 ) {
										$($selector).val($value);
									}
		        				});
								$("#addForm select[name=countryCode]").val($address.countryCode);
								$("#addForm select[name=state]").val($address.state);
								
				        		$("#aId").val($address.addressId);
				        		$("#updateOrAdd").val("update");
				        		$("#addAddressForm").dialog( "open" );
							},
							403: function($data) {
								$("#globalMsg").html("Session Timeout. Log in and try again");
							},
							404: function($data) {
								$("#globalMsg").html("System Error 404: Contact Support");
							},
							405: function($data) {
								$("#globalMsg").html("System Error 405: Contact Support");
							},
							500: function($data) {
								$("#globalMsg").html("System Error 500: Contact Support");
							} 
						},
						dataType: 'json'
					});
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
            	    			$("#globalMsg").html("Session Timeout. Log in and try again");
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
			
				if ( $ansiModal != '' ) {
					$("#addButton").click();
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

		<div id="deleteErrorDialog" title="Delete Failed!" class="ui-widget" style="display:none;">
		  <p>Address failed to delete. It may still be assigned to existing quotes.</p></input>
		</div>

		<div id="addAddressForm" title="Add/Update Address" class="ui-widget" style="display:none;">
			<div>
			&nbsp;<span class="err" id="addFormMsg"></span>
			</div>
			<form id="addForm">
		  	<%--
		  	This commented for 1.0.  We'll need to reassess using the tag when the form needs to work on multiple pages
			<webthing:addressPanel label="Name" namespace="ADDRESSPANEL" cssId="addressPanel" page="job"/>
			--%>
				<table>
					<tr>
						<td><span class="formLabel">Name:</span></td>
						<td colspan="3">
							<input type="text" name="name" style="width:315px" />
							<i id="nameErr" class="fa errIcon" aria-hidden="true"></i>
						</td>
					</tr>
					<tr>
						<td><span class="formLabel">Address:</span></td>
						<td colspan="3">
							<input type="text" name="address1" style="width:315px" />
							<i id="address1Err" class="fa errIcon" aria-hidden="true"></i>
						</td>						
					</tr>
					<tr>
						<td><span class="formLabel">Address 2:</span></td>
						<td colspan="3">
							<input type="text" name="address2" style="width:315px" />
							<i id="address2Err" class="fa errIcon" aria-hidden="true"></i>
						</td>						
					</tr>
					<tr>
						<td colspan="4" style="padding:0; margin:0;">
							<table style="border-collapse: collapse;padding:0; margin:0;">
								<tr>
									<td><span class="formLabel">City/State/Zip:</span></td>
									<td>
										<input type="text" name="city" style="width:90px;" />
										<i id="cityErr" class="fa errIcon" aria-hidden="true"></i>
									</td>
									<td>
										<select name="state" id="state" style="width:85px !important;max-width:85px !important;"></select>
										<i id="stateErr" class="fa errIcon" aria-hidden="true"></i>
									</td>
									<td>
										<input type="text" name="zip" style="width:47px !important" />
										<i id="zipErr" class="fa errIcon" aria-hidden="true"></i>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td><span class="formLabel">County:</span></td>
						<td>
							<input type="text" name="county" id="county" style="width:90%" />
							<i id="countyErr" class="fa errIcon" aria-hidden="true"></i>
						</td>
						<td colspan="2">
							<table>
								<tr>
									<td><span class="formLabel">Country:</span></td>
									<td align="right">
										<select name="countryCode" id="countryCode"></select>
										<i id="countryCodeErr" class="fa errIcon" aria-hidden="true"></i>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</form>
		</div>
		
	
		<div id="deleteConfirmDialog" title="Delete Address?" style="display:none;">
  			<p>Are you sure you would like to delete this address?</p>
		</div>
				
		<%-- 
		with optional label:
		<webthing:addressDisplayPanel cssId="addressView" label="Keegan's mansion"/>
		--%>				
		<webthing:addressDisplayPanel cssId="addressView" />
		
		<input  type="text" id="updateOrAdd" style="display:none"><input  type="text" id="aId" style="display:none">
    </tiles:put>	
</tiles:insert>

