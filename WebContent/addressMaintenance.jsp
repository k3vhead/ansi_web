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
        <bean:message key="page.label.address" /> <bean:message key="menu.label.lookup" />
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
				border:solid 1px #404040;
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
			#addressViewModal {
				display:none;
				width:400px;
			}
			.ansi-address-label {
				font-weight:bold;
			}
			.ansi-address-label-container {
				width:120px;
			}
			.copyAction {
				text-decoration:none;
				color:#000000;
			}
			.button_is_active {
				text-align:center; 
				border:solid 1px #000000; 
				padding:5px; 
				background-color:#CCCCCC;"
			}
			.button_is_inactive {
				text-align:center; 
				border:solid 1px #000000; 
				padding:5px; 
				background-color:#FFFFFF;
			}
			.invoiceDetails {
				display:none;
			}
			#addressFieldContainer {
				cursor:pointer;
			}
			#invoiceFieldContainer {
				cursor:pointer;
			}
        </style>
        
        <script type="text/javascript">        
        
        	$(document).ready(function() {
				; ADDRESSMAINTENANCE = {
					ansiModal : '<c:out value="${ANSI_MODAL}" />',
					dataTable : null,
			
					init : function() {
						ADDRESSMAINTENANCE.makeOptionLists();
						ADDRESSMAINTENANCE.makeAddAddressModal();
						ADDRESSMAINTENANCE.makeViewAddressModal();
						ADDRESSMAINTENANCE.makeButtons();
						ADDRESSMAINTENANCE.createTable();
						if ( ADDRESSMAINTENANCE.ansiModal != '' ) {
							$(".addButton").click();
						}
					},	
					
					
					
					createTable : function(){
						ADDRESSMAINTENANCE.dataTable = $('#addressTable').DataTable( {
	        				"processing": 		true,
		        	        "serverSide": 		true,
		        	        "autoWidth": 		false,
		        	        "deferRender": 		true,
		        	        "scrollCollapse": 	true,
		        	        "scrollX": 			true,
		        	        rowId: 				'dt_RowId',
		        	        dom: 				'Bfrtip',
		        	        "searching": 		true,
		        	        order: [[ 1, "asc"]],
		        	        lengthMenu: [
	        	            	[ 10, 25, 50, -1 ],
	        	            	[ '10 rows', '25 rows', '50 rows', 'Show all' ]
	        	        	],
		        	        buttons: [
		        	        	'pageLength','copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print',{extend: 'colvis',	label: function () {ADDRESSMAINTENANCE.doFunctionBinding();}}
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
					            { title: "<bean:message key="field.label.addressId" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
					            	if(row.addressId != null){return (row.addressId+"");}
					            } },
					            { title: "<bean:message key="field.label.name" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
					            	if(row.name != null){return (row.name+"");}
					            } },
					            { title: "<bean:message key="field.label.address_status" />" , "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
					            	if(row.address_status != null){return (row.address_status+"");}
					            } },
					            { title: "<bean:message key="field.label.address1" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
					            	if(row.address1 != null){return (row.address1+"");}
					            } },
					            { title: "<bean:message key="field.label.address2" />",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
					            	if(row.address2 != null){return (row.address2+"");}
					            } },
					            { title: "<bean:message key="field.label.city" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
					            	if(row.city != null){return (row.city+"");}
					            } },
					            { title: "<bean:message key="field.label.county" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
					            	if(row.county != null){return (row.county+"");}
					            } },
					            { title: "<bean:message key="field.label.countryCode" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
					            	if(row.countryCode != null){return (row.countryCode+"");}
					            } },
					            { title: "<bean:message key="field.label.state" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) { 	
					            	if(row.state != null){return (row.state+"");}
					            } },
					            { title: "<bean:message key="field.label.zip" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
					            	if(row.zip != null){return (row.zip+"");} 
					            } },
					            
					            // Invoice default value columns:
					            { title: "<bean:message key="field.label.invoice.style" />", "defaultContent": "<i>N/A</i>", "visible":false, data: function ( row, type, set ) {	
					            	if(row.invoiceStyleDefault != null){return (row.invoiceStyleDefault+"");} 
					            } },
					            { title: "<bean:message key="field.label.invoice.grouping" />", "defaultContent": "<i>N/A</i>", "visible":false, data: function ( row, type, set ) {	
					            	if(row.invoiceGroupingDefault != null){return (row.invoiceGroupingDefault+"");} 
					            } },
					            { title: "<bean:message key="field.label.invoice.batch" />", "defaultContent": "<i>N/A</i>", "visible":false, data: function ( row, type, set ) {	
					            	if(row.invoiceBatchDefault != null){
					            		var $batchStatus = '<webthing:ban>Not Batched</webthing:ban>';
					            		if ( row.invoiceBatchDefault == 1 ) {
					            			$batchStatus = '<webthing:checkmark>Batch Invoice</webthing:checkmark>';
					            		}
					            		return ($batchStatus);
					            	} 
					            } },
					            { title: "<bean:message key="field.label.invoice.terms" />", "defaultContent": "<i>N/A</i>", "visible":false, data: function ( row, type, set ) {	
					            	if(row.invoiceTermsDefault != null){return (row.invoiceTermsDefault+"");} 
					            } },
					            { title: "<bean:message key="field.label.invoice.ourVendorNbr" />", "defaultContent": "<i>N/A</i>", "visible":false, data: function ( row, type, set ) {	
					            	if(row.ourVendorNbrDefault != null){return (row.ourVendorNbrDefault+"");} 
					            } },
					            
					            
					            { title: "<bean:message key="field.label.action" />",  data: function ( row, type, set ) {	
					            	$viewLink = '<a href="#" class="viewAction" data-id="'+row.addressId+'"><webthing:view>View</webthing:view></a>';
					            	$editLink = '<ansi:hasPermission permissionRequired="SYSADMIN"><ansi:hasWrite><a href="#" class="editAction" data-id="'+row.addressId+'"><webthing:edit>Edit</webthing:edit></a></ansi:hasWrite></ansi:hasPermission>';
					            	$copyLink = '<ansi:hasPermission permissionRequired="SYSADMIN"><ansi:hasWrite><a href="#" class="copyAction" data-id="'+row.addressId+'"><webthing:copy>Copy</webthing:copy></a></ansi:hasWrite></ansi:hasPermission>';
					            	$deleteLink = '<ansi:hasPermission permissionRequired="SYSADMIN"><ansi:hasWrite><a href="#" class="delAction" data-id="'+row.addressId+'"><webthing:delete>Delete</webthing:delete></a></ansi:hasWrite></ansi:hasPermission>';					            	
					            	
					            	$action = $viewLink + " " + $editLink + " " + $copyLink;
					            	if(row.count < 1) {
					            		$action = $action + " " + $deleteLink;
					            	}				            	
					            	return $action;	
					            } }],
					            "initComplete": function(settings, json) {
					            	ADDRESSMAINTENANCE.doFunctionBinding();
					            },
					            "drawCallback": function( settings ) {
					            	ADDRESSMAINTENANCE.doFunctionBinding();
					            }
					    	} );
	        		},
					
	        		
	        		addAddress : function() {
	        			ADDRESSMAINTENANCE.clearErrIcons();
		        		$outbound = {};
		        		$.each($("#addForm input"), function($idx, $value) {
		        			$outbound[$(this).attr("name")] = $(this).val();
		        		});
		        		$.each($("#addForm select"), function($idx, $value) {
		        			$outbound[$(this).attr("name")] = $(this).val();
		        		});
	        		
		        		if ( $("#addForm input[name='invoiceBatchDefault']").prop("checked") == true ) {
		        			$outbound['invoiceBatch'] = 1;
		        		} else {
		        			$outbound['invoiceBatch'] = 0;
		        		}
		        		
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
										ADDRESSMAINTENANCE.clearAddForm();
										$( "#addAddressForm" ).dialog( "close" );
										if ( 'GLOBAL_MESSAGE' in $data.data.webMessages ) {
											$("#globalMsg").html($data.data.webMessages['GLOBAL_MESSAGE'][0]).show().fadeOut(6000);
										}
										$("#addressTable").DataTable().draw();
										$( "#addAddressForm" ).dialog( "close" );
									} else if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
										$.each($data.data.webMessages, function(key, messageList) {
											var $identifier = "#" + key + "Err";
											ANSI_UTILS.markInvalid( $($identifier) );
										});		
										if ( 'GLOBAL_MESSAGE' in $data.data.webMessages ) {
											$("#addFormMsg").html($data.data.webMessages['GLOBAL_MESSAGE'][0]).show().fadeOut(6000);
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
	        		},
	        		
	        		
	        		clearAddForm : function() {
		            	$("#addForm input").val("");
			        	$.each($("#addForm select"), function($index, $value) {
			        		$value.selectedIndex = 0;
			        	});
			        	$.each($("#addForm input[type='checkbox']"), function($index, $value) {
			        		$($value).prop('checked', false);;
			        	});
			        	ADDRESSMAINTENANCE.clearErrIcons();
		            },
		            
		            clearErrIcons : function() {
			        	$("#addForm .errIcon").removeClass("fa");
			        	$("#addForm .errIcon").removeClass("fa-ban");
			        	$("#addForm .errIcon").removeClass("inputIsInvalid");
			        	$("#addForm .errIcon").removeClass("fa-check-square-o");
			        	$("#addForm .errIcon").removeClass("inputIsValid");
			        	
		            },
		            
		            
		            doCopy : function($clickEvent) {
		            	$clickEvent.preventDefault();
						var $rowid = $clickEvent.currentTarget.attributes['data-id'].value;
						ADDRESSMAINTENANCE.populateAddressForm($rowid, "copy");
					},
					
					doDelete : function($clickEvent) {
						$clickEvent.preventDefault();
						var $rowid = $clickEvent.currentTarget.attributes['data-id'].value;
						
						$( "#deleteConfirmDialog" ).dialog({
							resizable: false,
							height: "auto",
							width: 400,
							modal: true,
							buttons: {
								"Delete Address": function() {ADDRESSMAINTENANCE.doDelete2($rowid);$( this ).dialog( "close" );},
						        Cancel: function() {
									$( this ).dialog( "close" );
								}
							}
						});
						$( "#deleteConfirmDialog" ).dialog( "open" );	
					},
					
				
					doDelete2 : function($rowid){
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
					}, 
					
		            doEdit : function($clickEvent) {
		            	$clickEvent.preventDefault();
						var $rowid = $clickEvent.currentTarget.attributes['data-id'].value;
						ADDRESSMAINTENANCE.populateAddressForm($rowid, "edit");
					},
		            
					
					doFunctionBinding : function() {
						$( ".editAction" ).on( "click", function($clickEvent) {
			        		$("#addAddressForm").dialog( "option", "title", "Update Address" );
							 ADDRESSMAINTENANCE.doEdit($clickEvent);
						});
						$('.delAction').on('click', function($clickEvent) {
							ADDRESSMAINTENANCE.doDelete($clickEvent);
						});
						
						$('#addressTable_next').on('click', function($clickEvent) {
			        		window.scrollTo(0, 0);
			        	});
						$('.viewAction').on('click', function($clickEvent) {
							ADDRESSMAINTENANCE.doView($clickEvent);
						});
						$( ".copyAction" ).on( "click", function($clickEvent) {
			        		$("#addAddressForm").dialog( "option", "title", "Add Address" );
							 ADDRESSMAINTENANCE.doCopy($clickEvent);
						});
					},
					
		            doView : function($clickEvent) {
		            	$clickEvent.preventDefault();
						var $addressId = $clickEvent.currentTarget.attributes['data-id'].value;
						ADDRESS_UTILS.getAddress($addressId, "#addressViewModal");
						$("#addressViewModal").dialog("open");
		            },
		            
		            
	        		makeAddAddressModal : function() {
	        			$( "#addAddressForm" ).dialog({
	    					autoOpen: false,
	    					height: "auto",
	    					width: 550,
	    					modal: true,
	    					buttons: [{
	    						id: 'addFormCloseButton',
	    						click: function() {
	    							$( "#addAddressForm" ).dialog( "close" );
	    						}								
	    					},{
	    						id: 'addFormButton',
	    						click: function() {
	    							ADDRESSMAINTENANCE.addAddress();
	    						}
	    					}],
	    					close: function() {
	    						$( "#addAddressForm" ).dialog( "close" );
	    						//allFields.removeClass( "ui-state-error" );
	    					}
	    				});
	        			$('#addFormCloseButton').button('option', 'label', 'Close');
	        		},
	        		
	        		
	        		makeViewAddressModal : function() {
	        			$( "#addressViewModal" ).dialog({
	    					title:"View Address",        			
	    					autoOpen: false,
	    					height: "auto",
	    					width: 520,
	    					modal: true,
	    					buttons: [{
	    						id: 'closeViewButton',
	    						click: function() {
	    							$( "#addressViewModal" ).dialog( "close" );
	    						}
	    					}],
	    					close: function() {
	    						$( "#addressViewModal" ).dialog( "close" );
	    						//allFields.removeClass( "ui-state-error" );
	    					}
	    				});	        			
	    				$('#closeViewButton').button('option', 'label', 'Close');
	        		},
	        		
					
					makeButtons : function() {
						$("#invoiceFieldContainer").click(function($event) {
							$("#addressFieldContainer").removeClass("button_is_active");
							$("#addressFieldContainer").addClass("button_is_inactive");
							$("#invoiceFieldContainer").removeClass("button_is_inactive");
							$("#invoiceFieldContainer").addClass("button_is_active");
							$("#addAddressForm .addressDetails").hide();
							$("#addAddressForm .invoiceDetails").show();
						});
						
						$("#addressFieldContainer").click(function($event) {
							$("#invoiceFieldContainer").removeClass("button_is_active");
							$("#invoiceFieldContainer").addClass("button_is_inactive");
							$("#addressFieldContainer").removeClass("button_is_inactive");
							$("#addressFieldContainer").addClass("button_is_active");
							$("#addAddressForm .invoiceDetails").hide();
							$("#addAddressForm .addressDetails").show();
						});
						
						$(".addButton").button().on( "click", function() {
			        		$("#updateOrAdd").val("add");
			        		ADDRESSMAINTENANCE.clearAddForm();
			        		$("#addAddressForm").dialog( "option", "title", "Add Address" );
			        		$('#addFormButton').button('option', 'label', 'Save');
			        	    $("#addAddressForm").dialog( "open" );
			        	    $("#addressFieldContainer").click();
			            });
						
						var $billToNameComplete = $( "#addAddressForm input[name='name']" ).autocomplete({
							'source':"addressTypeAhead?",
							select: function( event, ui ) {
								$addressId = ui.item.id;
								ADDRESSMAINTENANCE.populateAddressForm($addressId, "edit");
						   	}
						}).data('ui-autocomplete');
					},
					
					
					makeOptionLists : function(){
						$optionData = ANSI_UTILS.getOptions('COUNTRY,INVOICE_GROUPING,INVOICE_STYLE,INVOICE_TERM');
						var $countryList = $optionData.country;
						//$jobSiteDetail = "";

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
		                
		                
		                ANSI_UTILS.setOptionList("#addAddressForm select[name='invoiceGroupDefault']", $optionData.invoiceGrouping, null);
		                ANSI_UTILS.setOptionList("#addAddressForm select[name='invoiceTermsDefault']", $optionData.invoiceTerm, null);
		                ANSI_UTILS.setOptionList("#addAddressForm select[name='invoiceStyleDefault']", $optionData.invoiceStyle,null);
		            },
		            
		            
		            
		            populateAddressForm : function($rowid, $action) {
						var $url = 'address/' + $rowid;
						var jqxhr = $.ajax({
							type: 'GET',
							url: $url,
							statusCode: {
								200: function($data) {
									ADDRESSMAINTENANCE.clearAddForm();
									var $address = $data.data.addressList[0];  // we're only getting one address
									$.each($address, function($fieldName, $value) {									
										$selector = "#addForm input[name=" + $fieldName + "]";
										if ( $($selector).length > 0 ) {
											$($selector).val($value);
										}
			        				});
									
									$("#addForm select[name=countryCode]").val($address.countryCode);
									$("#addForm select[name=state]").val($address.state);
									$("#addForm select[name='invoiceStyleDefault']").val($address.invoiceStyleDefault);
									$("#addForm select[name='invoiceGroupDefault']").val($address.invoiceGroupingDefault);
									$("#addForm select[name='invoiceTermsDefault']").val($address.invoiceTermsDefault);									
									$("#addForm input[name='invoiceourVendorNbrDefault']").val($address.ourVendorNbrDefault);
									
									if ( $address.invoiceBatchDefault == 1 ) {
										$("#addForm input[name='invoiceBatchDefault']").prop("checked", true);
									} else {
										$("#addForm input[name='invoiceBatchDefault']").prop("checked", false);
									}
									
									
									if ( $action == "copy" ) {
										$('#addFormButton').button('option', 'label', 'Add Address');
										$("#addAddressForm").dialog( "option", "title", "Add Address" );
										$("#updateOrAdd").val("add");
						        		$("#addAddressForm").dialog( "open" );
						        		$("#addressFieldContainer").click();
									} else if ( $action == "edit" ) {
						        		$("#aId").val($address.addressId);
						        		$("#updateOrAdd").val("update");
								    	$('#addFormButton').button('option', 'label', 'Save');
								    	$("#addAddressForm").dialog( "option", "title", "Update Address" );
						        		$("#addAddressForm").dialog( "open" );
						        		$("#addressFieldContainer").click();
									} else {
										$("#globalMsg").html("Invalid session state. Reload the page and try again");
									}
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
				};
        		
				
				ADDRESSMAINTENANCE.init();
	        });
        </script>        
    </tiles:put>
    
    <tiles:put name="content" type="string">
    	<h1><bean:message key="page.label.address" /> <bean:message key="menu.label.lookup" /></h1>    	
    	
	    <ansi:hasPermission permissionRequired="SYSADMIN">
			<ansi:hasWrite>
    			<div class="addButtonDiv">
    				<input type="button" class="addButton prettyWideButton" value="New" />
    			</div>
			</ansi:hasWrite>
		</ansi:hasPermission>
 		<table id="addressTable" class="display" cellspacing="0" style="font-size:9pt;">
	        <thead>
	            <tr>
	                <th><bean:message key="field.label.addressId" /></th>
	    			<th><bean:message key="field.label.name" /></th>
	    			<th><bean:message key="field.label.address_status" /></th>
	    			<th><bean:message key="field.label.address1" /></th>
	    			<th><bean:message key="field.label.address2" /></th>
	    			<th><bean:message key="field.label.city" /></th>
	    			<th><bean:message key="field.label.county" /></th>
	    			<th><bean:message key="field.label.countryCode" /></th>
	    			<th><bean:message key="field.label.state" /></th>
	    			<th><bean:message key="field.label.zip" /></th>
	    			<th><bean:message key="field.label.invoice.style" /></th>
	    			<th><bean:message key="field.label.invoice.grouping" /></th>
	    			<th><bean:message key="field.label.invoice.batch" /></th>
	    			<th><bean:message key="field.label.invoice.terms" /></th>
	    			<th><bean:message key="field.label.invoice.ourVendorNbr" /></th>
	    			<th><bean:message key="field.label.action" /></th>
	            </tr>
	        </thead>
	        <tfoot>
	            <tr>
	                <th><bean:message key="field.label.addressId" /></th>
	    			<th><bean:message key="field.label.name" /></th>
	    			<th><bean:message key="field.label.address_status" /></th>
	    			<th><bean:message key="field.label.address1" /></th>
	    			<th><bean:message key="field.label.address2" /></th>
	    			<th><bean:message key="field.label.city" /></th>
	    			<th><bean:message key="field.label.county" /></th>
	    			<th><bean:message key="field.label.countryCode" /></th>
	    			<th><bean:message key="field.label.state" /></th>
	    			<th><bean:message key="field.label.zip" /></th>
	    			<th><bean:message key="field.label.invoice.style" /></th>
	    			<th><bean:message key="field.label.invoice.grouping" /></th>
	    			<th><bean:message key="field.label.invoice.batch" /></th>
	    			<th><bean:message key="field.label.invoice.terms" /></th>
	    			<th><bean:message key="field.label.invoice.ourVendorNbr" /></th>
	    			<th><bean:message key="field.label.action" /></th>
	    			
	            </tr>
	        </tfoot>
	    </table>
	    <ansi:hasPermission permissionRequired="SYSADMIN">
			<ansi:hasWrite>
    			<div class="addButtonDiv">
    				<input type="button" class="addButton prettyWideButton" value="New" />
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
				<table style="width:500px;">
					<colgroup>
			        	<col style="width:50%;" />
			        	<col style="width:50%;" />
					</colgroup>
					<tr>
						<td id="addressFieldContainer" class="button_is_active"><i id="addressFieldButton" class="action-link far fa-address-card fa-lg tooltip" aria-hidden="true"><span class="tooltiptextleft">Address</span></i></td>
						<td id="invoiceFieldContainer" class="button_is_inactive"><i id="invoiceFieldButton" class="action-link far fa-money-bill-alt fa-lg tooltip" aria-hidden="true"><span class="tooltiptextleft">Invoice Defaults</span></i></td>
						<td>&nbsp;</td>
					</tr>
				</table>
				<div class="addressDetails">
					<table style="width:500px;">
						<tr>
							<td><span class="required">*</span></td>
							<td><span class="formLabel"><bean:message key="field.label.name" />:</span></td>
							<td colspan="3">
								<input type="text" name="name" style="width:315px" />
								<i id="nameErr" class="fa errIcon" aria-hidden="true"></i>
							</td>
						</tr>
						<tr>
							<td><span class="required">*</span></td>
							<td><span class="formLabel"><bean:message key="field.label.address1" />:</span></td>
							<td colspan="3">
								<input type="text" name="address1" style="width:315px" />
								<i id="address1Err" class="fa errIcon" aria-hidden="true"></i>
							</td>						
						</tr>
						<tr>
							<td><span class="required"></span></td>
							<td><span class="formLabel"><bean:message key="field.label.address2" />:</span></td>
							<td colspan="3">
								<input type="text" name="address2" style="width:315px" />
								<i id="address2Err" class="fa errIcon" aria-hidden="true"></i>
							</td>						
						</tr>
						<tr>
							<td><span class="required">*</span></td>
							<td colspan="4" style="padding:0; margin:0;">
								<table style="border-collapse: collapse;padding:0; margin:0;">
									<tr>
										<td><span class="formLabel"><bean:message key="field.label.city" />/<bean:message key="field.label.state" />/<bean:message key="field.label.zip" />:</span></td>
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
							<td><span class="required"></span></td>
							<td><span class="formLabel"><bean:message key="field.label.county" />:</span></td>
							<td>
								<input type="text" name="county" id="county" style="width:90%" />
								<i id="countyErr" class="fa errIcon" aria-hidden="true"></i>
							</td>
						</tr>
					</table>
				</div>
				<div class="invoiceDetails">
					<table style="width:500px;">
						<tr>
							<td><span class="required"></span></td>
							<td><span class="formLabel"><bean:message key="field.label.invoice.style" />:</span></td>
							<td colspan="3">
								<select name="invoiceStyleDefault"></select>
								<i id="invoiceStyleDefaultErr" class="fa errIcon" aria-hidden="true"></i>
							</td>
						</tr>
						<tr>
							<td><span class="required"></span></td>
							<td><span class="formLabel"><bean:message key="field.label.invoice.grouping" />:</span></td>
							<td colspan="3">
								<select name="invoiceGroupDefault"></select>
								<i id="invoiceGroupDefaultErr" class="fa errIcon" aria-hidden="true"></i>
							</td>
						</tr>
						<tr>
							<td><span class="required"></span></td>
							<td><span class="formLabel"><bean:message key="field.label.invoice.batch" />:</span></td>
							<td colspan="3">
								<input type="checkbox" name="invoiceBatchDefault" value="yes" />
								<i id="batchErr" class="fa errIcon" aria-hidden="true"></i>
							</td>
						</tr>
						<tr>
							<td><span class="required"></span></td>
							<td><span class="formLabel"><bean:message key="field.label.invoice.terms" />:</span></td>
							<td colspan="3">
								<select name="invoiceTermsDefault"></select>
								<i id="invoiceTermsDefaultErr" class="fa errIcon" aria-hidden="true"></i>
							</td>
						</tr>
						<tr>
							<td><span class="required"></span></td>
							<td><span class="formLabel"><bean:message key="field.label.invoice.ourVendorNbr" />:</span></td>
							<td colspan="3">
								<input type="text" name="invoiceourVendorNbrDefault" style="width:315px" />
								<i id="invoiceourVendorNbrDefaultErr" class="fa errIcon" aria-hidden="true"></i>
							</td>
						</tr>
					</table>
				</div>
			</form>
		</div>
		
	
		<div id="deleteConfirmDialog" title="Delete Address?" style="display:none;">
  			<p>Are you sure you would like to delete this address?</p>
		</div>
				
		<%-- 
		with optional label:
		<webthing:addressDisplayPanel cssId="addressView" label="Keegan's mansion"/>
		--%>
		<div id="addressViewModal">			
			<webthing:addressDisplayPanel cssId="addressView" />
			<webthing:addressInvoicePanel cssId="invoiceDefault" />
		</div>
		
		
		<input  type="text" id="updateOrAdd" style="display:none" />
		<input  type="text" id="aId" style="display:none" />
    </tiles:put>	
</tiles:insert>

