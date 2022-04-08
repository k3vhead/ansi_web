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
        Locale Lookup
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
       	<link rel="stylesheet" href="css/lookup.css" />
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/lookup.js"></script> 
    	
    
        <style type="text/css">
			#displayTable {
				width:100%;
			}
			#addFormDiv {
				display:none;
				background-color:#FFFFFF;
				color:#000000;
				width:400px;
				padding:15px;
			}
			#filter-container {
        		width:402px;
        		float:right;
        	}
        	
			#addLocaleForm {
				display:none;
			}
			#confirm-modal {
        		display:none;
        	}
			.action-link {
				cursor:pointer;
			}
			.formHdr {
				font-weight:bold;
			}
			.prettyWideButton {
				height:30px;
				min-height:30px;
			}
			.print-link {
				cursor:pointer;
			}
			.editJob {
				cursor:pointer;
				text-decoration:underline;
			}
			.jobLink {
				color:#000000;
			}
			.overrideAction {
				cursor:pointer;
				text-decoration:none;
				color:#000000;
			}
			.dataTables_wrapper {
				padding-top:10px;
			}
			#ticket-modal {
				display:none;	
			}
			.ticket-clicker {
				color:#000000;
			}
        </style>
        
        <script type="text/javascript">    
        
        $(document).ready(function(){
        	
        	LOCALELOOKUP = {
                dataTable : null,

       			init : function() {
       				LOCALELOOKUP.clearAddForm();  
       				LOCALELOOKUP.createTable();  
       				LOCALELOOKUP.makeClickers();
       				LOCALELOOKUP.makeAliasTable();
       				LOCALELOOKUP.markValid();  
       				LOCALELOOKUP.makeEditPanel();
       				LOCALELOOKUP.makeLocaleTypeList();
       				LOCALELOOKUP.showNew();
                }, 
				
    			
    			clearAddForm : function () {
    				$.each( $('#addLocaleForm').find("input"), function(index, $inputField) {
    					$fieldName = $($inputField).attr('name');
    					if ( $($inputField).attr("type") == "text" ) {
    						$($inputField).val("");
    						LOCALELOOKUP.markValid($inputField);
    					}
    				});
    				$("#addLocaleForm select").val("");
    				$('.err').html("");
    				$('#addLocaleForm').data('rownum',null);
                },
                
                
                createTable : function(){
            		var dataTable = $('#localeTable').DataTable( {
            			"aaSorting":		[[2,'asc']],
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
            	            { className: "dt-head-left", "targets": [0,1,4,5,6] },
            	            { className: "dt-body-center", "targets": [2,3,7] },
            	            { className: "dt-right", "targets": []}
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": "locale/lookup",
    			        	"type": "GET",
    			        	"data": {}
    			        	},
    			        columns: [
    			            { width:"5%", title: "<bean:message key="field.label.localeId" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.locale_id != null){return ('<a href="#" data-id="'+row.locale_id+'" class="ticket-clicker">'+row.locale_id+'</a>');}
    			            } },
    			            { width:"20%", title: "<bean:message key="field.label.name" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.name != null){return ('<span class="tooltip">' + row.name+'<span class="tooltiptext">'+row.name+'</span></span>');}
    			            } },
    			            { width:"5%", title: "<bean:message key="field.label.state" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.state_name != null){return (row.state_name+"");}
    			            } },
    			            { width:"5%", title: "<bean:message key="field.label.abbreviation" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.abbreviation != null){return (row.abbreviation);}
    			            } },
    			            { width:"10%", title: "<bean:message key="field.label.localeType" />" , "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.locale_type_id != null){return (row.locale_type_id+"");}
    			            } },
    			            { width:"10%", title: "Parent" , "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.parent_name != null){return (row.parent);}
    			            } },
    			            { width:"10%", title:"Payroll Tax", "defaultContent":"", searchable:true, data:"profile_desc"},
    			            { width:"5%", title: "<bean:message key="field.label.action" />",  data: function ( row, type, set ) {	
    			            	//console.log(row);
    			            	var $actionData = "";
    			            	if ( row.locale_id != null ) {
    				            	var $editLink = '<ansi:hasPermission permissionRequired="TAX_WRITE"><a href="#" class="editAction" data-id="'+row.locale_id+'" data-name="'+row.locale_id+'"><webthing:edit>Edit</webthing:edit></a></ansi:hasPermission>&nbsp;';
    				            	var $aliasLink = '<span class="action-link alias-link" data-id="'+row.locale_id+'"><webthing:view>Alias</webthing:view></span>';
									var $taxLink = '' //'<ansi:hasPermission permissionRequired="TAX_WRITE"><a href="taxRateLookup.html?id='+row.locale_id+'"><webthing:taxes>Taxes</webthing:taxes></a></ansi:hasPermission>&nbsp;';

//    		            			var $ticketData = 'data-id="' + row.locale_id + '"';
//    			            		$printLink = '<ansi:hasPermission permissionRequired="TAX_READ"><i class="print-link fa fa-print" aria-hidden="true" ' + $localeData + '></i></ansi:hasPermission>'
//    			            		var $claimLink = '';
//    			            		
    				            	$actionData = $editLink + $aliasLink + $taxLink;
    			            	}
    			            	return $actionData;
    			            } }],
    			            "initComplete": function(settings, json) {
    			            	//console.log(json);
    			            	//doFunctionBinding();
    			            	var myTable = this;
    			            	LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#localeTable", LOCALELOOKUP.createTable);
    			            },
    			            "drawCallback": function( settings ) {
    			            	LOCALELOOKUP.doFunctionBinding();
    			            	$(".alias-link").off("click");
    			            	$(".alias-link").click(function($clickevent) {
    			            		var $localeId = $(this).attr("data-id");
    			            		LOCALELOOKUP.showAliasTable($localeId);
    			            	});
    			            }
    			    } );
            	},
            	
            	
            	
            	doConfirm : function() {
            		console.log("doConfirm");
        			var $function = $("#confirm-save").attr("data-function");
        			if ( $function == "deleteAlias" ) {
        				var $localeAliasId = $("#confirm-save").attr("data-id");
        				var $url = "locale/alias/" + $localeAliasId;
        				ANSI_UTILS.makeServerCall("DELETE", $url, {}, {200:LOCALELOOKUP.doConfirmAliasSuccess}, {});
        			} else if ( $function == "deleteLocale") {
        				var $localeId = $("#confirm-save").attr("data-id");
        				var $url = "locale/" + $employeeCode;
        				ANSI_UTILS.makeServerCall("DELETE", $url, {}, {200:LOCALELOOKUP.doConfirmLocaleSuccess}, {});
        			} else {
        				$("#confirm-modal").dialog("close");
        				console.log("Function: " + $function);
        				$("#globalMsg").html("Invalid System State. Reload and try again").show();
        			}
        		},
        		
        		
        		doConfirmAliasSuccess : function($data, $passthru) {
        			$("#alias-lookup").DataTable().ajax.reload();
        			$("#confirm-modal").dialog("close");
        			$("#alias-display .alias-message").html("Success").show().fadeOut(3000);
        		},
        		
        		
        		
        		doConfirmEmployeeSuccess : function($data, $passthru) {
        			$("#employeeLookup").DataTable().ajax.reload();
        			$("#confirm-modal").dialog("close");
        			$("#globalMsg").html("Success").show().fadeOut(3000);
        		},
        		
        		
            	
	            doFunctionBinding : function() {
	    			$( ".editAction" ).on( "click", function($clickevent) {
	    				var $name = $(this).attr("data-name");
	    				 LOCALELOOKUP.showEdit($clickevent);
	    			});					
	    			$(".print-link").on( "click", function($clickevent) {
	    				doPrint($clickevent);
	    			});
	    			$(".locale-clicker").on("click", function($clickevent) {
	    				$clickevent.preventDefault();
	    				var $locale_id = $(this).attr("data-id");
	//    				TICKETUTILS.doTicketViewModal("#ticket-modal",$ticketId);
	    				$("#locale-modal").dialog("open");
	    			});
	
	    		},
            	
	    		
	    		
	    		doNewAliasSuccess : function($data, $passthru) {
	    			console.log("doNewAliasSuccess");
	    			if ( $data.responseHeader.responseCode == "EDIT_FAILURE") {
	    				$("#alias-display .alias-message").html($data.data.webMessages['aliasName'][0]).show().fadeOut(3000);
	    			} else if ( $data.responseHeader.responseCode == "SUCCESS") {
	    				$("#alias-display .alias-message").html("Success").show().fadeOut(3000);
	    				$("#alias-display input").val("");
	    				$("#alias-lookup").DataTable().ajax.reload();
	    			} else {
	    				$("#alias-display .alias-message").html("Invalid response: " + $data.responseHeader.responseCode + ". Contact Support");
	    			}
	    			
	    		},
	    		
	    		
	    		
	    		makeAliasTable : function() {
	    			$( "#alias-display" ).dialog({
        				title:'View Locale Alias',
        				autoOpen: false,
        				height: 500,
        				width: 600,
        				modal: true,
        				closeOnEscape:true,
        				//open: function(event, ui) {
        				//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
        				//},
        				buttons: [
        					{
        						id:  "alias_display_cancel",
        						click: function($event) {
       								$( "#alias-display" ).dialog("close");
        						}
        					}
        				]
        			});	
        			$("#alias_display_cancel").button('option', 'label', 'Done');  
	    		},
	    		
	    		
	    		
            	
	            makeClickers : function() {
	            	$('.ScrollTop').click(function() {
	        			$('html, body').animate({scrollTop: 0}, 800);
	        			return false;
	               	});
	            	
	            	
	            	var $localeComplete = $( "#parentName" ).autocomplete({
	    				source: function(request,response) {
	    					term = $("#addLocaleForm input[name='parentName']").val();
	    					localeTypeId = $("#addLocaleForm select[name='localeTypeId']").val();
	    					stateName = $("#addLocaleForm select[name='stateName']").val();
	    					$.getJSON("localeAutocomplete", {"term":term, "localeTypeId":localeTypeId, "stateName":stateName}, response);
	    				},
	                    minLength: 2,
	                    select: function( event, ui ) {
	                    	$("#addLocaleForm input[name='parentId']").val(ui.item.id);
	                    },
	                    response: function(event, ui) {
	                        if (ui.content.length === 0) {
	                        	$("#globalMsg").html("No Matching Locale");
	                        	$("#addLocaleForm input[name='parentId']").val("");
	                        } else {
	                        	$("#globalMsg").html("");
	                        }
	                    }
	              	}).data('ui-autocomplete');	            	
	                
	    			$localeComplete._renderMenu = function( ul, items ) {
	    				var that = this;
	    				$.each( items, function( index, item ) {
	    					that._renderItemData( ul, item );
	    				});
	    				if ( items.length == 1 ) {
	    					$("#addLocaleForm input[name='parentId']").val(items[0].id);
	    					$("#parentName").autocomplete("close");
	    				}
	    			}
	            },
	            
	            
	            
	            makeLocaleTypeList: function (){ 			
	    			
	    			var jqxhr = $.ajax({
	    				type: 'GET',
	    				url: "options?LOCALE_TYPE",
	    				success: function($data) {
	    					var $select = $("#addLocaleForm select[name='localeTypeId']")
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
			
				
				makeEditPanel : function() {	
					$("#addLocaleForm" ).dialog({
						autoOpen: false,
						height: 350,
						width: 500,
						modal: true,
						buttons: [
							{
								id: "closeAddLocaleForm",
								click: function() {
									$("#addLocaleForm").dialog( "close" );
								}
							},{
								id: "goEdit",
								click: function($event) {
									LOCALELOOKUP.updateLocale();
								}
							}	      	      
						],
						close: function() {
							LOCALELOOKUP.clearAddForm();
							$("#addLocaleForm").dialog( "close" );
							//allFields.removeClass( "ui-state-error" );
						}
					});		
					
					
					
					$( "#confirm-modal" ).dialog({
        				title:'Confirm Delete',
        				autoOpen: false,
        				height: 200,
        				width: 300,
        				modal: true,
        				closeOnEscape:true,
        				//open: function(event, ui) {
        				//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
        				//},
        				buttons: [
        					{
        						id:  "confirm-cancel",
        						click: function($event) {
       								$( "#confirm-modal" ).dialog("close");
        						}
        					},{
        						id:  "confirm-save",
        						click: function($event) {
       								LOCALELOOKUP.doConfirm();
        						}
        					}
        				]
        			});	
        			$("#confirm-cancel").button('option', 'label', 'Cancel');  
        			$("#confirm-save").button('option', 'label', 'Confirm');
				},
					
						
				
				showAliasTable : function($localeId) {
					console.log("showAliasTable: " + $localeId);
					var $url = "locale/aliasLookup/" + $localeId;
					
        			$("#alias-display").dialog("open");
        			$("#alias-lookup").DataTable( {
        				"aaSorting":		[[1,'asc']],
            			"processing": 		true,
            	        "serverSide": 		true,
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
            	        lengthMenu: [
            	        	[ 10, 50, 100, 500, 1000 ],
            	            [ '10 rows', '50 rows', '100 rows', '500 rows', '1000 rows' ]
            	        ],
            	        buttons: [
            	        		'pageLength',
            	        		'copy', 
            	        		'csv', 
            	        		'excel', 
            	        		{extend: 'pdfHtml5', orientation: 'landscape'}, 
            	        		'print',{extend: 'colvis',	label: function () {doFunctionBinding();$('#displayTable').draw();}},
            	        	],
            	        "columnDefs": [
             	            { "orderable": true, "targets": -1 },
             	            { className: "dt-head-center", "targets":[]},
            	            { className: "dt-left", "targets": [1,2] },
            	            { className: "dt-center", "targets": [0] },
            	            { className: "dt-right", "targets": []}
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": $url,
    			        	"type": "GET",
    			        	"data": null,
    			        	},
    			        columns: [
    			        	{ title: "ID", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'locale_alias_id' },
    			        	{ title: "Locale Alias", width:"50%", searchable:true, "defaultContent": "<i>N/A</i>", data:'locale_name' }, 
    			            { title: "Action",  width:"10%", searchable:false, orderable:false, 
    			            	data: function ( row, type, set ) { 
    			            		//var $editLink = '<span class="action-link edit-link" data-id="'+row.employee_code+'" data-name="'+row.employee_name+'"><webthing:edit>Edit</webthing:edit></span>';
    			            		var $deleteLink = '<span class="action-link delete-link" data-id="'+row.locale_alias_id+'"><webthing:delete>Delete</webthing:delete></span>';
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
    			            		var $localeId = $(this).attr("data-id");
    			            		$("#confirm-save").attr("data-function","deleteAlias");
    			            		$("#confirm-save").attr("data-id",$localeId);
									$("#confirm-modal").dialog("open");
    			            	});
    			            	$("#alias-display .cancel-new-alias").click( function($event) {
    			            		$("#alias-display input[name='employeeName']").val("");
    			            	});
    			            	$("#alias-display .save-new-alias").click( function($event) {    			            		
    			            		var $localeId = $(this).attr("data-id");
    			            		var $aliasName = null
    			            		$.each( $("#alias-display input"), function($index, $value) {
    			            			// for some reason, datatables is putting two input boxes, so get the one that is populated
    			            			var $thisValue = $($value).val();
    			            			if ( $thisValue != null ) {
    			            				$aliasName = $thisValue;
    			            			}
    			            		});
    			            		var $url = "locale/alias/" + $localeId;
    			            		$("#alias-display .err").html("");
    			            		console.log("new alias name: " + $aliasName);
    			            		ANSI_UTILS.makeServerCall("POST", $url, {"aliasName":$aliasName}, {200:LOCALELOOKUP.doNewAliasSuccess}, {});
    			            	});

    			            },
    			            "footerCallback" : function( row, data, start, end, display ) {
    			            	console.log("alias footerCallback");
    			            	var api = this.api();
    			            	var $saveLink = '<span class="action-link save-new-alias" data-id="'+$localeId+'"><webthing:checkmark>Save</webthing:checkmark></span>';
    			            	var $cancelLink = '<span class="action-link cancel-new-alias"><webthing:ban>Cancel</webthing:ban></span>';
    			            	var $aliasInput = '<ansi:hasPermission permissionRequired="TAX_WRITE"><input type="text" name="employeeName" placeholder="New Alias" /></ansi:hasPermission>';
    			            	var $aliasError = '<span class="employeeNameErr err"></span>';
    			            	$( api.column(1).footer() ).html($aliasInput + " " + $aliasError);
    			            	$( api.column(2).footer() ).html('<ansi:hasPermission permissionRequired="PAYROLL_WRITE">' + $saveLink + $cancelLink + '</ansi:hasPermission>');
    			            }
    			    } );
				},
				
				
				
	            showEdit : function ($clickevent) {
	            	
	            //	$name = $("#addLocaleForm").attr("data-name");
			    //    var $name = $(this).attr("data-name");
	            	
	        /*    	$state_name = $("#addLocaleForm").attr("data-state_name");
			        var $state_name = $(this).attr("data-state_name");            	
	            	$abbreviation = $("#addLocaleForm").attr("data-abbreviation");
			        var $abbreviation = $(this).attr("data-abbreviation");            	
	            	$locale_type_id = $("#addLocaleForm").attr("data-locale_type_id");
			        var $locale_type_id = $(this).attr("data-locale_type_id");
			        */
					var $localeId = $clickevent.currentTarget.attributes['data-id'].value;
					$("#goEdit").data("localeId: " + $localeId);
	        		$('#goEdit').button('option', 'label', 'Save');
	        		$('#closeAddLocaleForm').button('option', 'label', 'Close');
	        		
	        	//	LOCALELOOKUP.populateOptions();
	        		
					var $url = 'locale/' + $localeId;
					var jqxhr = $.ajax({
						type: 'GET',
						url: $url,
						statusCode: {
							200: function($data) {								
								var $locale = $data.data;
								$.each($locale, function($fieldName, $value) {									
									$selector = "#addLocaleForm input[name=" + $fieldName + "]";
									if ( $($selector).length > 0 ) {
										$($selector).val($value);
									}
		        				}); 
							//	LOCALELOOKUP.makeLocaleTypeList();
    			        		$("#localeId").val(($data.data).localeId);
    			        		$("#name").val(($data.data).name);
    			        		$("#stateName").val(($data.data).stateName);
    			        		$("#abbreviation").val(($data.data).abbreviation);
    			        		$("#localeTypeId").val(($data.data).localeTypeId);
    			        		$("#addLocaleForm select[name='profileId']").val($data.data.profileId);
				        		$("#addLocaleForm  .err").html("");
				        		$("#addLocaleForm ").dialog("option","title", "Edit Locale").dialog("open");
							},
							403: function($data) {
								$("#globalMsg").html("Session Timeout. Log in and try again");
							},
							404: function($data) {
								$("#globalMsg").html("Invalid Request");
							},
							500: function($data) {
								$("#globalMsg").html("System Error; Contact Support");
							}
						},
						dataType: 'json'
					});
				},
	
					
				showNew : function () {
					$(".showNew").click(function($event) {
						$('#goEdit').data("localeId",null);
		        		$('#goEdit').button('option', 'label', 'Save');
		        		$('#closeAddLocaleForm').button('option', 'label', 'Close');
		        		
		 //       		$("#editPanel display[name='']").val("");
						$("#addLocaleForm  input[name='name']").val("");
						$("#addLocaleForm  select[name='stateName']").val("");
						$("#addLocaleForm  input[name='abbreviation']").val("");	
						$("#addLocaleForm  select[name='localeTypeId']").val("");	
						$("#addLocaleForm  select[name='profileId']").val("");
		        		$("#addLocaleForm  .err").html("");
		        		$("#addLocaleForm ").dialog("option","title", "Add New Locale").dialog("open");
					});
				},
					
	        	
	
				updateLocale : function () {
					console.debug("Updating Locale");
					var $localeId = $("#addLocaleForm input[name='localeId']").val();
					console.debug("localeId: " + $localeId);
					
	
					if ( $localeId == null || $localeId == '') {
						$url = 'locale';
					} else {
						$url = 'locale/' + $localeId;
					}
					console.debug($url);
						
					var $outbound = {};
					//$outbound['localeId'] = $("#addLocaleForm input[name='localeId']").val();
					$outbound['name'] = $("#addLocaleForm input[name='name']").val();
					$outbound['stateName'] = $("#addLocaleForm select[name='stateName']").val();	
					$outbound['abbreviation'] = $("#addLocaleForm input[name='abbreviation']").val();
					$outbound['localeTypeId'] = $("#addLocaleForm select[name='localeTypeId']").val();	
					$outbound['parentId'] = $("#addLocaleForm input[name='parentId']").val();
					$outbound['profileId'] = $("#addLocaleForm select[name='profileId']").val();
					console.debug($outbound);
					
					var jqxhr = $.ajax({
						type: 'POST',
						url: $url,
						data: JSON.stringify($outbound),
						statusCode: {
							200: function($data) {
			    				if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
			    					$.each($data.data.webMessages, function (key, value) {
			    						var $selectorName = "#" + key + "Err";
			    						$($selectorName).show();
			    						$($selectorName).html(value[0]).show().fadeOut(3000);
			    					});
			    				} else {	    				
			    					$("#addLocaleForm").dialog("close");
			    					$('#localeTable').DataTable().ajax.reload();		
			    					LOCALELOOKUP.clearAddForm();		    					
			    					$("#globalMsg").html("Update Successful").show().fadeOut(10000);
			    				}
							},
							403: function($data) {
								$("#globalMsg").html("Session Timeout. Log in and try again").show().fadeOut(100000);
							},
							404: function($data) {
								$("#globalMsg").html("Invalid Selection").show().fadeOut(100000);
							},
							500: function($data) {
								$("#globalMsg").html("System Error; Contact Support").show().fadeOut(100000);
							}
						},
						dataType: 'json'
					});
				},
					
					
					
			/*	function doPrint($clickevent) {
					var $locale_id = $clickevent.currentTarget.attributes['data-id'].value;
					console.debug("ROWID: " + $locale_id);
					var a = document.createElement('a');
	                var linkText = document.createTextNode("Download");
	                a.appendChild(linkText);
	                a.title = "Download";
	                a.href = "ticketPrint/" + $locale_id;
	                a.target = "_new";   // open in a new window
	                document.body.appendChild(a);
	                a.click();				
				}*/
				
	        	}
	        
	        	LOCALELOOKUP.init();
	        });
	        
	        		
	        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1><bean:message key="page.label.locale" /> <bean:message key="menu.label.lookup" /></h1>
    	
    	
    	  	
    	  	
 	<webthing:lookupFilter filterContainer="filter-container" />

 	<table id="localeTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:1300px;width:1300px;">
        <thead>
        </thead>
        <tfoot>
        </tfoot>
    </table>
    
	    
    <div id="addLocaleForm">
    	<table class="ui-front">
    		<tr>
    			<td><span class="formHdr">ID</span></td>
    			<td><input type="text" name="localeId" style="border-style:hidden;" readonly="readonly"/></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">Name</span></td>
    			<td><input type="text" name="name" /></td>
    			<td><span class="err" id="nameErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">State Name</span></td>
    			<td><select name="stateName" id="stateName"><webthing:states /></select></td>
    			<td><span class="err" id="stateNameErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">Abbreviation</span></td>
    			<td><input type="text" name="abbreviation" /></td>
    			<td><span class="err" id="abbreviationErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">Locale Type</span></td>
    			<td><select name="localeTypeId" id="localeTypeId"></select></td>
    			<td><span class="err" id="localeTypeIdErr"></span></td>
    		</tr>		
    		<tr>
    			<td><span class="formHdr">Parent</span></td>
    			<td><input type="text" name="parentName" id="parentName" /><input type="hidden" name="parentId" /></td>
    			<td><span class="err" id="parentIdErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">Payroll Tax Profile</span></td>
    			<td>
    				<select name="profileId" id="profileId">
    					<option value=""></option>
    					<ansi:taxProfileSelect format="select" />
    				</select>
				</td>
    			<td><span class="err" id="profileIdErr"></span></td>
    		</tr>
    	</table>
    </div>
    
	    
    <input type="button" class="prettyWideButton showNew" value="New" />
    
    <webthing:scrolltop />

	<div id="alias-display">
		<div class="alias-message err"></div>
		<table id="alias-lookup">
			<thead></thead>
			<tbody></tbody>
			<tfoot>
				<tr>
					<td></td>
					<td></td>
					<td></td>
				</tr>
			</tfoot>
		</table>			
	</div>
	
	<div id="confirm-modal">			
		<h2>Are you sure?</h2>
	</div>
    </tiles:put>
		
</tiles:insert>

