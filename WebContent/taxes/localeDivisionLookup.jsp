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
        Nexus Taxed Lookup
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
       	<link rel="stylesheet" href="css/lookup.css" />
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/lookup.js"></script> 
    	
    
        <style type="text/css">
        	#add_modal {
        		display:none;
        	}
			#delete_modal {
				display:none;
			}
			#displayTable {
				width:100%;
			}
			#filter-container {
        		width:402px;
        		float:right;
        	}
			#editEndDateModal {
				display:none;	
			}
        	
			.dataTables_wrapper {
				padding-top:10px;
			}
			.editJob {
				cursor:pointer;
				text-decoration:underline;
			}
			.formHdr {
				font-weight:bold;
			}
			.jobLink {
				color:#000000;
			}
			.overrideAction {
				cursor:pointer;
				text-decoration:none;
				color:#000000;
			}
			.prettyWideButton {
				height:30px;
				min-height:30px;
			}
        </style>
        
        <script type="text/javascript">    
        
        $(document).ready(function(){
        	
        	LOCALEDIVISIONLOOKUP = {
                dataTable : null,

       			init : function() {
       				LOCALEDIVISIONLOOKUP.createTable();  
       				LOCALEDIVISIONLOOKUP.makeClickers();
       				LOCALEDIVISIONLOOKUP.populateDivisionSelect();
       				LOCALEDIVISIONLOOKUP.makeLocaleTypeList();
       				LOCALEDIVISIONLOOKUP.markValid();  
       				LOCALEDIVISIONLOOKUP.makeModals();
       				LOCALEDIVISIONLOOKUP.showNew();
       				
       				$('.dateField').datepicker({
                        prevText:'&lt;&lt;',
                        nextText: '&gt;&gt;',
                        showButtonPanel:true
                    });
                }, 
                
                clearAddForm : function () {
    				$.each( $('#editEndDateModal').find("input"), function(index, $inputField) {
    					$fieldName = $($inputField).attr('name');
    					if ( $($inputField).attr("type") == "text" ) {
    						$($inputField).val("");
    						LOCALEDIVISIONLOOKUP.markValid($inputField);
    					}
    				});
    				$('.err').html("");
    				$('#editEndDateModal').data('rownum',null);
                },
                
                createTable : function(){
            		var dataTable = $('#localeDivisionTable').DataTable( {
            			"aaSorting":		[[0,'asc']],
            			"processing": 		true,
            	        "serverSide": 		true,
            	        "autoWidth": 		false,
            	        "deferRender": 		true,
            	        "scrollCollapse": 	true,
            	        "scrollX": 			true,
            	        rowId: 				'dt_RowId',
            	        dom: 				'Bfrtip',
            	        "searching": 		true,
            	        "searchDelay":		800,
            	        lengthMenu: [
            	        	[ 10, 50, 100, 500, 1000 ],
            	            [ '10 rows', '50 rows', '100 rows', '500 rows', '1000 rows' ]
            	        ],
            	        buttons: [
            	        	'pageLength','copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print',{extend: 'colvis',	label: function () {doFunctionBinding();}}
            	        ],
            	        
            	        "columnDefs": [
             	            { "orderable": false, "targets": -1 },
            	            { className: "dt-head-left", "targets": [0,1,5] },
            	            { className: "dt-body-center", "targets": [3,4] },
            	            { className: "dt-right", "targets": [2]}
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": "localeDivisionLookup",
    			        	"type": "GET",
    			        	"data": {}
    			        	},
    			        columns: [	//divisionId, localeId, effectiveStartDate, effectiveStopDate, addressId
    			            { width:"10%", title: "<bean:message key="field.label.divisionNbr" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.division_display != null){return (row.division_display+"");}
    			            } },
    			            { width:"15%", title: "<bean:message key="field.label.name" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.name != null){return (row.name+"");}
    			            } },
//    			            { width:"10%", title: "<bean:message key="field.label.localeType" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
//    			            	if(row.locale_type_id != null){return (row.locale_type_id+"");}
//    			            } },
    			            { width:"8%", title: "<bean:message key="field.label.stateName" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.state_name != null){return (row.state_name+"");}
    			            } },
    			            { width:"10%", title: "<bean:message key="field.label.startDate" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.effective_start_date != null){return (row.effective_start_date+"");}
    			            } },
    			            { width:"10%", title: "<bean:message key="field.label.stopDate" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.effective_stop_date != null){return (row.effective_stop_date+"");}
    			            } },
    			            { width:"20%", title: "<bean:message key="field.label.address" />" , "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.address1 != null){return (row.address1+"\n");}
    			            	if(row.address2 != null){return (row.address2+"\n");}
    			            	if(row.city != null){return (row.city+" ");}
    			            	if(row.state != null){return (row.state+", ");}
    			            	if(row.zip != null){return (row.zip+"");}
    			            } },
    			            
    			            { width:"5%", title: "<bean:message key="field.label.action" />",  data: function ( row, type, set ) {	
    			            	var $actionData = "";
    			            	if ( row.locale_id != null ) {
    				            	// var $editLink = '<ansi:hasPermission permissionRequired="TAX_WRITE"><a href="#" class="edit_action" data-divisionid="'+row.division_id+'" data-localeid="'+row.locale_id+'"><webthing:edit>Edit</webthing:edit></a></ansi:hasPermission>&nbsp;';
    				            	var $deleteLink = '<ansi:hasPermission permissionRequired="TAX_WRITE"><a href="#" class="delete_action" data-divisionid="'+row.division_id+'" data-localeid="'+row.locale_id+'" data-effectivestartdate="'+row.effective_start_date+'"><webthing:delete>Delete</webthing:delete></a></ansi:hasPermission>';
    				            	var $endLink = '<ansi:hasPermission permissionRequired="TAX_WRITE"><a href="#" class="edit_action" data-divisionid="'+row.division_id+'" data-localeid="'+row.locale_id+'" data-effectivestartdate="'+row.effective_start_date+'"><webthing:schedule>Set End Date</webthing:schedule></a></ansi:hasPermission>&nbsp;';
    				            	$actionData = $endLink + "&nbsp;" + $deleteLink;
    			            	}
    			            	return $actionData;
    			            } }],
    			            "initComplete": function(settings, json) {
    			            	var myTable = this;
    			            	//LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#localeDivisionTable", LOCALEDIVISIONLOOKUP.createTable);
    			            },
    			            "drawCallback": function( settings ) {
    			            	LOCALEDIVISIONLOOKUP.doFunctionBinding();
    			            }
    			    } );
            	},
            	
            	
            	doFunctionBinding : function() {
            		$( ".edit_action" ).on( "click", function($clickevent) {
            			console.log("doFunctionBinding: getCode");
	    				var $divisionId = $(this).attr("data-divisionId");
	    				var $localeId = $(this).attr("data-localeId");
	    				var $effectiveStartDate = $(this).attr("data-effectivestartdate");
	    				LOCALEDIVISIONLOOKUP.getCode($divisionId, $localeId, $effectiveStartDate, LOCALEDIVISIONLOOKUP.goEdit);
	    			});		
            		
            		
            		$( ".delete_action" ).on( "click", function($clickevent) {
	    				var $divisionId = $(this).attr("data-divisionid");
	    				var $localeId = $(this).attr("data-localeid");
	    				var $startDate = $(this).attr("data-startdate");
	    				var $effectiveStartDate = $(this).attr("data-effectivestartdate");
	    				LOCALEDIVISIONLOOKUP.getCode($divisionId, $localeId, $effectiveStartDate, LOCALEDIVISIONLOOKUP.goDelete);
	    			});
  	    		},
    			
    			
  	    		
  	    		
  	    		getCode : function($divisionId, $localeId, $effectiveStartDate, $goFunction) {
    				$url = "localeDivision/" + $divisionId;
    				$outbound = {"localeId":$localeId, "effectiveStartDate":$effectiveStartDate};
    				
    				var jqxhr = $.ajax({
    					type: 'GET',
    					url: $url,
    					data: $outbound,
    					statusCode: {
    						200: function($data) {
    							$goFunction($divisionId, $localeId, $effectiveStartDate, $data);    							
    						},
    						400: function($data) {
    							$("#globalMsg").html("System Error 400. Contact Support");
    						},    						
    						403: function($data) {
    							$("#globalMsg").html("Session Timeout. Log in and try again");
    						},
    						404: function($data) {
    							$("#globalMsg").html("Invalid Selection").show().fadeOut(4000);
    						},
    						405: function($data) {
    							$("#globalMsg").html("Insufficient Permissions").show().fadeOut(4000);
    						},
    						500: function($data) {
    							$("#globalMsg").html("System Error; Contact Support");
    						}
    					},
    					dataType: 'json'
    				});
    				
				},
				

				
    			
  	    		
  	    		goDelete : function($divisionId, $localeId, $effectiveStartDate, $data) {
  	    			$('#goDelete').data("localeId", $localeId);
	        		$('#goDelete').button('option', 'label', 'Confirm');
	        		$('#closeDeleteModal').button('option', 'label', 'Cancel');
	        		
	        		$("#delete_modal  input[name='action']").val("delete");
	        		
	        		$("#delete_modal  input[name='localeId']").val($data.data.localeId);
	        		$("#delete_modal  input[name='addressId']").val($data.data.addressId);	        		
					$("#delete_modal  input[name='divisionId']").val($divisionId);
					
					$("#delete_modal .division_name").html($data.data.divisionNbr + "-" + $data.data.divisionCode);
					$("#delete_modal .locale_name").html($data.data.name);
					$("#delete_modal .state_name").html($data.data.stateName);
					$("#delete_modal .effective_start_date").html($data.data.effectiveStartDate);	
					$("#delete_modal .effective_stop_date").html($data.data.effectiveStopDate);
					$("#delete_modal .address_name").html($data.data.addressName);
					$("#delete_modal .nexus_address1").html($data.data.address1);
					if ( $data.data.address2 == null || $data.data.address2 == "" ) {
						$("#delete_modal .address2_container").hide();
					} else {
						$("#delete_modal .address2_container").show();
					}
					$("#delete_modal .nexus_address2").html($data.data.address2);
					$("#delete_modal .nexus_city").html($data.data.city);
					$("#delete_modal .nexus_state").html($data.data.state);
					$("#delete_modal .nexus_zip").html($data.data.zip);
	        		$("#delete_modal .err").html("");
	        		$("#delete_modal ").dialog("option","title", "Delete Nexus").dialog("open");
  	    		},
  	    		
  	    		
  	    		
  	    		goEdit : function($divisionId, $localeId, $effectiveStartDate, $data) {
  	    			$('#goEdit').data("localeId", $localeId);
	        		$('#goEdit').button('option', 'label', 'Save');
	        		$('#closeLocaleDivisionModal').button('option', 'label', 'Close');
	        		
	        		$("#editEndDateModal  input[name='action']").val("update");
	        		
	        		$("#editEndDateModal  input[name='localeId']").val($data.data.localeId);
	        		$("#editEndDateModal  input[name='addressId']").val($data.data.addressId);	        		
					$("#editEndDateModal  select[name='divisionId']").val($divisionId);
					
					$("#editEndDateModal .division_name").html($data.data.divisionNbr + "-" + $data.data.divisionCode);
					$("#editEndDateModal .locale_name").html($data.data.name);
					$("#editEndDateModal .state_name").html($data.data.stateName);
					$("#editEndDateModal .effective_start_date").html($data.data.effectiveStartDate);
					$("#editEndDateModal  input[name='effectiveStopDate']").val($data.data.effectiveStopDate);
					$("#editEndDateModal .address_name").html($data.data.addressName);
					$("#editEndDateModal .nexus_address1").html($data.data.address1);
					if ( $data.data.address2 == null || $data.data.address2 == "" ) {
						$("#editEndDateModal .address2_container").hide();
					} else {
						$("#editEndDateModal .address2_container").show();
					}
					$("#editEndDateModal .nexus_address2").html($data.data.address2);
					$("#editEndDateModal .nexus_city").html($data.data.city);
					$("#editEndDateModal .nexus_state").html($data.data.state);
					$("#editEndDateModal .nexus_zip").html($data.data.zip);
	        		$("#editEndDateModal  .err").html("");
	        		$("#editEndDateModal ").dialog("option","title", "Update Nexus").dialog("open");
  	    		},
  	    		
  	    		
  	    		
  	    		
  	    		makeModals : function() {
       				LOCALEDIVISIONLOOKUP.makeEditPanel();
       				LOCALEDIVISIONLOOKUP.makeDeletePanel();
       				LOCALEDIVISIONLOOKUP.makeAddPanel();
  	    		},
  	    		
                
                
				makeAddPanel : function() {	
    				console.log("make edit panel");
    				$("#add_modal").dialog({
    					autoOpen: false,
    					height: 400,
    					width: 600,
    					modal: true,
    					buttons: [
    						{
    							id: "closeAddModal",
    							click: function() {
    								$("#add_modal").dialog( "close" );
    							}
    						},{
    							id: "goAdd",
    							click: function($event) {
    								console.log("make edit panel: updateLocaleDivision");
    								LOCALEDIVISIONLOOKUP.updateLocaleDivision();
    							}
    						}	      	      
    					],
    					close: function() {
    						$("#add_modal").dialog( "close" );
    					}
    				});
    			},

                makeDeletePanel : function() {	
    				console.log("make delete panel");
    				$("#delete_modal").dialog({
    					autoOpen: false,
    					height: 400,
    					width: 600,
    					modal: true,
    					buttons: [
    						{
    							id: "closeDeleteModal",
    							click: function() {
    								$("#delete_modal").dialog( "close" );
    							}
    						},{
    							id: "goDelete",
    							click: function($event) {
    								LOCALEDIVISIONLOOKUP.updateLocaleDivision();
    							}
    						}	      	      
    					],
    					close: function() {
    						$("#delete_modal").dialog( "close" );
    						//allFields.removeClass( "ui-state-error" );
    					}
    				});
    			},
    			
    			
    			
    			
    			
				makeEditPanel : function() {	
    				console.log("make edit panel");
    				$("#editEndDateModal").dialog({
    					autoOpen: false,
    					height: 400,
    					width: 600,
    					modal: true,
    					buttons: [
    						{
    							id: "closeLocaleDivisionModal",
    							click: function() {
    								console.log("make edit panel: close");
    								$("#editEndDateModal").dialog( "close" );
    							}
    						},{
    							id: "goEdit",
    							click: function($event) {
    								console.log("make edit panel: updateLocaleDivision");
    								LOCALEDIVISIONLOOKUP.updateLocaleDivision();
    							}
    						}	      	      
    					],
    					close: function() {
    						console.log("make edit panel: clear add form");
    						LOCALEDIVISIONLOOKUP.clearAddForm();
    						$("#editEndDateModal").dialog( "close" );
    						//allFields.removeClass( "ui-state-error" );
    					}
    				});
    			},
    			
    			
    			
    			
  	    		markValid : function ($inputField) {
                	$fieldName = $($inputField).attr('name');
                	$fieldGetter = "input[name='" + $fieldName + "']";
                	$fieldValue = $($fieldGetter).val();
                	$valid = '#' + $($inputField).data('valid');
    	            var re = /.+/;	            	 
                	if ( re.test($fieldValue) ) {
                		$($valid).removeClass("fa");
                		$($valid).removeClass("fa-ban");
                		$($valid).removeClass("inputIsInvalid");
                		$($valid).addClass("far");
                		$($valid).addClass("fa-check-square");
                		$($valid).addClass("inputIsValid");
                	} else {
                		$($valid).removeClass("far");
                		$($valid).removeClass("fa-check-square");
                		$($valid).removeClass("inputIsValid");
                		$($valid).addClass("fa");
                		$($valid).addClass("fa-ban");
                		$($valid).addClass("inputIsInvalid");
                	}
                },
                
                

    			
    			updateLocaleDivision : function () {
    				console.debug("Updating Locale Division");
    				
    				$("#editEndDateModal .err").html("");
    				
    				var $divisionId = $("#editEndDateModal select[name='divisionId']").val();
    				console.debug("divisionId: " + $divisionId);
    				
    				var $localeId = $("#editEndDateModal input[name='localeId']").val();
    				console.debug("localeId: " + $localeId);
    				
    				console.log("localeId: " + $localeId);
    				
    				if ( $divisionId == null || $divisionId == '') {
    					$url = 'localeDivision';
    				} else {
    					$url = 'localeDivision/' + $divisionId;
    				}
    				console.debug($url);
    						
    				var $outbound = {};
    				$outbound['action'] = $("#editEndDateModal input[name='action']").val();
    				$outbound['divisionId'] = $("#editEndDateModal select[name='divisionId']").val();
    				$outbound['localeId'] = $("#editEndDateModal input[name='localeId']").val();
    				$outbound['addressId'] = $("#editEndDateModal input[name='addressId']").val();
    				$outbound['effectiveStartDate'] = $("#editEndDateModal input[name='effectiveStartDate']").val();
    				$outbound['effectiveStopDate'] = $("#editEndDateModal input[name='effectiveStopDate']").val();
    				
    				
    				console.debug($outbound);
    				
    				var jqxhr = $.ajax({
    					type: 'POST',
    					url: $url,
    					data: JSON.stringify($outbound),
    					statusCode: {
    						200: function($data) {
    			    			if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
    			    				$.each($data.data.webMessages, function (key, value) {
    			    					if ( key == "GLOBAL_MESSAGE" ) {
    			    						$("#globalMsg").html(value[0]).show().fadeOut(6000);
    			    						$("#addTaxRateForm").dialog("close");
    			    					} else {
	    			    					var $selectorName = "#" + key + "Err";
											console.log($selectorName);
											console.log(value[0])
	    			    					$($selectorName).html(value[0]).show();
    			    					}
    			    				});
    			    				
    			   				} else {	    				
    			    				$("#editEndDateModal").dialog("close");
    			    				$('#localeDivisionTable').DataTable().ajax.reload();		
    			    				LOCALEDIVISIONLOOKUP.clearAddForm();		    					
    			    				$("#globalMsg").html("Update Successful").show().fadeOut(6000);
    			    			}
    						},
    						403: function($data) {
    							$("#globalMsg").html("Session Timeout. Log in and try again");
    						},
    						404: function($data) {
    							$("#divisionIdErr").html("Invalid Selection").show().fadeOut(6000);
    						},
    						405: function($data) {
    							$("#globalMsg").html("Insufficient Permissions").show();
    						},
    						500: function($data) {
    							$("#globalMsg").html("System Error; Contact Support");
    						}
    					},
    					dataType: 'json'
    				});
    			},
            	

    			
    			makeLocaleTypeList: function (){ 
	    			var jqxhr = $.ajax({
	    				type: 'GET',
	    				url: "options?LOCALE_TYPE",
	    				success: function($data) {
	    					var $select = $("#editEndDateModal select[name='localeTypeId']")
	    					$('option', $select).remove();
	    					$select.append(new Option("",""));
	    					$.each($data.data.localeType, function(index, val){
	    						$select.append(new Option(val.display, val.name));
	    					});
	    				},
	    				statusCode: {
	    					403: function($data) {
	    						$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
	    					}
	    				},
	    				dataType: 'json'
	    			});
	    		},	  
            	
            	makeClickers : function() {
            		$('.ScrollTop').click(function() {
        				$('html, body').animate({scrollTop: 0}, 800);
        				return false;
               	    });
            		
            		
            		var $localeComplete = $("#add_modal input[name='localeName']" ).autocomplete({
    					source: function(request,response) {
    						term = $("#add_modal input[name='localeName']").val();
    						localeTypeId = null;
    						stateName = null;
    						$.getJSON("localeAutocomplete", {"term":term, "localeTypeId":localeTypeId, "stateName":stateName}, response);
    					},
                    	minLength: 2,
                    	select: function( event, ui ) {
                    		$("#add_modal input[name='localeId']").val(ui.item.id);
                    	},
                    	response: function(event, ui) {
                        	if (ui.content.length === 0) {
                        		$("#globalMsg").html("No Matching Locale");
                        		$("#add_modal input[name='localeId']").val("");
                        	} else {
                        		$("#globalMsg").html("");
                        	}
                    	}
              		}).data('ui-autocomplete');	
            		
            		
            		
            		
            		
            		var $jobsiteBillTo = $( "#add_modal input[name='addressName']" ).autocomplete({
						'source':"addressTypeAhead?",
						select: function( event, ui ) {
							$( "#add_modal input[name='addressId']" ).val(ui.item.id);
							
							addressPieceList = ui.item.label.split(":");
							$("#add_modal .nexus_address1").html(addressPieceList[2]);
							$("#add_modal .nexus_address2").html(addressPieceList[3]);
							$("#add_modal .nexus_city").html(addressPieceList[4]);
							$("#add_modal .nexus_state").html(addressPieceList[5]);
							$("#add_modal .nexus_zip").html(addressPieceList[6]);
   				      	},
						response: function(event, ui) {
							if (ui.content.length === 0) {
								$( "#add_modal input[name='jobsiteBilltoAddressDefault']" ).val(-1); //server side will see this and mark it invalid
								$("#noAddress").show();
								$("#noContact").hide();
								$( "#noMatchModal" ).dialog("open");
							}
						}
					}).data('ui-autocomplete');
            		
            		
            		
            	},
            	 
            	
            	
    			populateDivisionSelect : function() {
                	$data = ANSI_UTILS.getDivisionList();
                	console.log("populateDivSelect");
                	var $select = $("#add_modal select[name='divisionId']");
        			$('option', $select).remove();
        			$select.append(new Option("",null));
        			$.each($data, function($index, $val) {
        				var $display = $val.divisionNbr + "-" + $val.divisionCode;
        				$select.append(new Option($display, $val.divisionId));
        			});	
                },
                

            	
    			showNew : function () {
					$(".showNew").click(function($event) {
						$('#goAdd').data("localeId", null);
		        		$('#goAdd').button('option', 'label', 'Save');
		        		$('#closeAddModal').button('option', 'label', 'Close');
		        		
		        		$("#add_modal  input[name='action']").val("add");
		        		
		        		$("#add_modal  input[name='localeId']").val("");
		        		$("#add_modal  input[name='addressId']").val("");
		        		
						$("#add_modal  select[name='divisionId']").val("");
						$("#add_modal  input[name='localeName']").val("");
						$("#add_modal  select[name='stateName']").val("");
						$("#add_modal  input[name='effectiveStartDate']").val("");	
						$("#add_modal  input[name='effectiveStopDate']").val("");
						$("#add_modal  input[name='addressName']").val("");
						$("#nexus_address1").html(null);
						$("#nexus_address2").html(null);
						$("#nexus_city").html(null);
						$("#nexus_state").html(null);
						$("#nexus_zip").html(null);
		        		$("#add_modal  .err").html("");
		        		$("#add_modal ").dialog("option","title", "Add New Nexus").dialog("open");
					});
				},
    			
				
				
    			
        	};
        
        	LOCALEDIVISIONLOOKUP.init();
			
        });
        		
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1><bean:message key="page.label.localedivision" /> <bean:message key="menu.label.lookup" /></h1> 
    	<c:if test="${not empty ANSI_JOB_ID}">
    		<span class="orange"><bean:message key="field.label.jobFilter" />: <c:out value="${ANSI_JOB_ID}" /></span><br />
    	</c:if>
    	<c:if test="${not empty ANSI_DIVISION_ID}">
    		<span class="orange"><bean:message key="field.label.divisionFilter" />: <c:out value="${ANSI_DIVISION_ID}" /></span><br />
    	</c:if>
    	<c:if test="${not empty ANSI_TICKET_LOOKUP_START_DATE}">
    		<span class="orange"><bean:message key="field.label.startDate" />: <c:out value="${ANSI_TICKET_LOOKUP_START_DATE}" /></span><br />
    	</c:if>
    	<c:if test="${not empty ANSI_TICKET_LOOKUP_STATUS}">
    		<span class="orange"><bean:message key="field.label.statusFilter" />: <c:out value="${ANSI_TICKET_LOOKUP_STATUS}" /></span><br />
    	</c:if>
    	  	
    	  	
 	<webthing:lookupFilter filterContainer="filter-container" />


 	<table id="localeDivisionTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:1300px;width:1300px;">
        <thead>
        </thead>
        <tfoot>
        </tfoot>
    </table>
    <input type="button" class="prettyWideButton showNew" value="New" />
    <webthing:scrolltop />
    
    
    
    <div id="add_modal">
     	<input type="hidden" name="action" />
	    <div class="modal-header">
	    	<h5 class="modal-title" id="name"></h5>
	    </div>
    	<table class="ui-front">
    		<tr>
    			<td><span class="formHdr">Division</span></td>
    			<td>
    				<select name="divisionId" />
    				
    			</td>
    			<td><span class="err" id="divisionIdErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">Locale Name</span></td>
    			<td><input type="text" name="localeName" /><input type="hidden" name="localeId" /></td>
    			<td><span class="err" id="localeNameErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">Address</span></td>
    			<td><input type="text" name="addressName" /><input type="hidden" name="addressId" /></td>
    			<td><span class="err" id="addressIdErr"></span></td>
    		</tr>
    		<tr>
    			<td>&nbsp;</td>
    			<td>
    				<span class="nexus_address1"></span><br />
    				<span class="address2_container"><span class="nexus_address2"></span><br /></span>
    				<span class="nexus_city"></span>, <span class="nexus_state"></span> <span class="nexus_zip"></span>
    			</td>
    			<td>&nbsp;</td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">Effective Start Date</span></td>
    			<td><input type="text" name="effectiveStartDate" class="dateField" /></td>
    			<td><span class="err" id="effectiveStartDateErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">Effective Stop Date</span></td>
    			<td><input type="text" name="effectiveStopDate" class="dateField" /></td>
    			<td><span class="err" id="effectiveStopDateErr"></span></td>
    		</tr>
    		
    	</table>
    </div>
    
    
    
    
    
    
    <div id="editEndDateModal">
     	<input type="hidden" name="action" />
	    <div class="modal-header">
	    	<h5 class="modal-title" id="name"></h5>
	    </div>
    	<table class="ui-front">
    		<tr>
    			<td><span class="formHdr">Division</span></td>
    			<td><input type="hidden" name="divisionId"/><span class="division_name" ></span></td>
    			<td><span class="err" id="divisionIdErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">Locale Name</span></td>
    			<td><input type="hidden" name="localeId" /><span class="locale_name" /></td>
    			<td><span class="err" id="localeIdErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">Address</span></td>
    			<td><input type="hidden" name="addressId" /><span class="address_name" /></td>
    			<td><span class="err" id="addressIdErr"></span></td>
    		</tr>
    		<tr>
    			<td>&nbsp;</td>
    			<td>
    				<span class="nexus_address1"></span><br />
    				<span class="address2_container"><span class="nexus_address2"></span><br /></span>
    				<span class="nexus_city"></span>, <span class="nexus_state"></span> <span class="nexus_zip"></span>
    			</td>
    			<td>&nbsp;</td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">Effective Start Date</span></td>
    			<td><span class="effective_start_date" /></td>
    			<td><span class="err" id="effectiveStartDateErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">Effective Stop Date</span></td>
    			<td><input type="text" name="effectiveStopDate" class="dateField" /></td>
    			<td><span class="err" id="effectiveStopDateErr"></span></td>
    		</tr>
    		
    	</table>
    </div>
    
    
    
    <div id="delete_modal">
     	<input type="hidden" name="action" />
	    <div class="modal-header">
	    	<h5 class="modal-title" id="name"></h5>
	    </div>
    	<table class="ui-front">
    		<tr>
    			<td><span class="formHdr">Division</span></td>
    			<td><input type="hidden" name="divisionId"/><span class="division_name" ></span></td>
    			<td><span class="err" id="divisionIdErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">Locale Name</span></td>
    			<td><input type="hidden" name="localeId" /><span class="locale_name" /></td>
    			<td><span class="err" id="localeIdErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">Address</span></td>
    			<td><input type="hidden" name="addressId" /><span class="address_name" /></td>
    			<td><span class="err" id="addressIdErr"></span></td>
    		</tr>
    		<tr>
    			<td>&nbsp;</td>
    			<td>
    				<span class="nexus_address1"></span><br />
    				<span class="address2_container"><span class="nexus_address2"></span><br /></span>
    				<span class="nexus_city"></span>, <span class="nexus_state"></span> <span class="nexus_zip"></span>
    			</td>
    			<td>&nbsp;</td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">Effective Start Date</span></td>
    			<td><span class="effective_start_date" /></td>
    			<td><span class="err" id="effectiveStartDateErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">Effective Stop Date</span></td>
    			<td><span class="effective_stop_date" /></td>
    			<td><span class="err" id="effectiveStopDateErr"></span></td>
    		</tr>
    		
    	</table>
    </div>
    
    
    


    </tiles:put>
		
</tiles:insert>

