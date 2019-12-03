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
        Call Notes Lookup
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
    	<link rel="stylesheet" href="css/lookup.css" />
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/lookup.js"></script> 
        <style type="text/css">
        	#filter-container {
        		width:402px;
        		float:right;
        	}
        	#new-address-modal {
        		display:none;
        	}
        	#new-contact-modal {
        		display:none;
        	}
        	#note-crud-form {
        		display:none;
        		background-color:#FFFFFF;
				color:#000000;
        	}
			.modal-label {
				width:100%;
				text-align:center;
				font-weight:bold;
			}
			
			.prettyWideButton {
				height:30px;
				min-height:30px;
			}
			
        </style>
        
        <script type="text/javascript">
        
        $(document).ready(function() {
        	
        	
        	
        	;CALL_NOTE_LOOKUP = {
        		datatable : null,
        		callTypeList : null,
        		
        		init : function() {
        			CALL_NOTE_LOOKUP.makeTable();
        			ANSI_UTILS.getCodeList('call_log','contact_type',CALL_NOTE_LOOKUP.makeCallTypeList);
        			CALL_NOTE_LOOKUP.makeClickers();
        			CALL_NOTE_LOOKUP.makeAutoComplete();
        			
        			
        			//CALL_NOTE_LOOKUP.makeOptionList('EXPENSE_TYPE', CALL_NOTE_LOOKUP.populateOptionList);
        			//CALL_NOTE_LOOKUP.makeDivisionList();
        			
        		},
        		
        		
        		
        		
        		clearForm : function() {
        			$.each( $("#note-crud-form input"), function($index, $value) {        				
        				var $selector = '#note-crud-form input[name="' + $value.name + '"]';
        				$($selector).val("");        				
        			});
        			
        			$("#note-crud-form texatarea").html("");
        			
					var $select = $("#note-crud-form select[name='contactType']");
					$('option', $select).remove();

					$select.append(new Option("",""));
					$.each(CALL_NOTE_LOOKUP.callTypeList, function(index, val) {
						console.log(val);
					    $select.append(new Option(val.displayValue, val.value));
					});

        		},
        		
        		
        		
        		makeAutoComplete : function() {
					var $addressDisplaySelector = '#note-crud-form input[name="address"]';
            		$( $addressDisplaySelector ).autocomplete({
						'source':"addressTypeAhead?term=",
						position:{my:"left top", at:"left bottom",collision:"none"},
						appendTo:"#node-crud-form",
						select: function( event, ui ) {
							$('#note-crud-form input[name="addressId"]').val(ui.item.id);							
							if ( ui.item.value == null || ui.item.value.trim() == "" ) {
								$($contactDisplaySelector).val(ui.item.label)
							}
       			      	},
       			      	response: function(event, ui) {
							if (ui.content.length === 0) {
								$('#note-crud-form input[name="addressId"]').val("");	
								$("#new-address-modal input").val("");
								$("#new-address-modal .err").html("");
								$("#new-address-modal input[name='name']").val($($addressDisplaySelector).val());
								$("#new-address-modal").dialog("open");
							}
						}
       			 	});
            		
            		
            		var $contactDisplaySelector = '#note-crud-form input[name="contact"]';
            		$( $contactDisplaySelector ).autocomplete({
						'source':"contactTypeAhead?term=",
						position:{my:"left top", at:"left bottom",collision:"none"},
						appendTo:"#node-crud-form",
						select: function( event, ui ) {
							$('#note-crud-form input[name="contactId"]').val(ui.item.id);
							if ( ui.item.value == null || ui.item.value.trim() == "" ) {
								$($contactDisplaySelector).val(ui.item.label);
							}
       			      	},
       			      	response: function(event, ui) {
							if (ui.content.length === 0) {
								$('#note-crud-form input[name="contactId"]').val("");
								$("#new-contact-modal input").val("");
								$("#new-contact-modal .err").html("");
								var $name = $($contactDisplaySelector).val().split(" ");
								$("#new-contact-modal input[name='firstName']").val($name[0]);
								if ( $name.length > 1 ) {
									$("#new-contact-modal input[name='lastName']").val($name[1]);
								}
								$("#new-contact-modal").dialog("open");
							}
						}
       			 	});
            		
            		
            		var $ansiDisplaySelector = '#note-crud-form input[name="ansi"]';
            		$( $ansiDisplaySelector ).autocomplete({
						'source':"ansiUserAutoComplete?",
						position:{my:"left top", at:"left bottom",collision:"none"},
						appendTo:"#node-crud-form",
						select: function( event, ui ) {
							$('#note-crud-form input[name="userId"]').val(ui.item.id);
							if ( ui.item.value == null || ui.item.value.trim() == "" ) {
								$($contactDisplaySelector).val(ui.item.label);
							}
       			      	},
       			      	response: function(event, ui) {
							if (ui.content.length === 0) {
								alert("Nobody here by that name");
								$('#note-crud-form input[name="userId"]').val("");
							}
						}
       			 	});
        		},
        		
        		
        		// this is a list of call types from the code table
        		// we "makeModal" here to ensure that we have a list of call types before continuing
        		makeCallTypeList : function($data) {
        			CALL_NOTE_LOOKUP.callTypeList = $data.codeList;  
        			CALL_NOTE_LOOKUP.makeModal();
        		},
        		
        		
        		
        		
        		makeClickers : function() {
            		$('.ScrollTop').click(function() {
        				$('html, body').animate({scrollTop: 0}, 800);
              	  		return false;
              	    });
            		
            		$("#new-note-button").click(function($event) {
            			CALL_NOTE_LOOKUP.clearForm();
            			$( "#note-crud-form" ).dialog("open");
            		});
            		
            		$('.dateField').datepicker({
                        prevText:'&lt;&lt;',
                        nextText: '&gt;&gt;',
                        showButtonPanel:true
                    });
            		
            		
            	},
            	
            	
        		makeModal : function() {
					$( "#note-crud-form" ).dialog({
						title:'Call Note',
						autoOpen: false,
						height: 500,
						width: 700,
						modal: true,
						closeOnEscape:true,
						//open: function(event, ui) {
						//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
						//},
						buttons: [
							{
								id: "note-cancel-button",
								click: function($event) {
									$( "#note-crud-form" ).dialog("close");
								}
							},{
								id: "note-save-button",
								click: function($event) {
									CALL_NOTE_LOOKUP.xxx();
								}
							}
						]
					});	
					$("#note-save-button").button('option', 'label', 'Save');
					$("#note-cancel-button").button('option', 'label', 'Cancel');
					
					
					
					
					$( "#new-address-modal" ).dialog({
						title:'New Address',
						autoOpen: false,
						height: 500,
						width: 700,
						modal: true,
						closeOnEscape:true,
						//open: function(event, ui) {
						//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
						//},
						buttons: [
							{
								id: "address-cancel-button",
								click: function($event) {
									$( "#new-address-modal" ).dialog("close");
								}
							},{
								id: "address-save-button",
								click: function($event) {
									CALL_NOTE_LOOKUP.xxx();
								}
							}
						]
					});	
					$("#address-save-button").button('option', 'label', 'Save');
					$("#address-cancel-button").button('option', 'label', 'Cancel');
					
					
					
					
					
					
					$( "#new-contact-modal" ).dialog({
						title:'New Contact',
						autoOpen: false,
						height: 500,
						width: 700,
						modal: true,
						closeOnEscape:true,
						//open: function(event, ui) {
						//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
						//},
						buttons: [
							{
								id: "contact-cancel-button",
								click: function($event) {
									$( "#new-contact-modal" ).dialog("close");
								}
							},{
								id: "contact-save-button",
								click: function($event) {
									CALL_NOTE_LOOKUP.xxx();
								}
							}
						]
					});	
					$("#contact-save-button").button('option', 'label', 'Save');
					$("#contact-cancel-button").button('option', 'label', 'Cancel');
        		},
        		
        		
        		makeTable : function() {
            		var dataTable = $('#displayTable').DataTable( {
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
            	        	'pageLength','copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print',{extend: 'colvis',	label: function () {doFunctionBinding();$('#displayTable').draw();}}
            	        ],
            	        "columnDefs": [
             	            { "orderable": false, "targets": -1 },
            	            { className: "dt-left", "targets": [0,1,2,3,4,5,6] },
            	            { className: "dt-center", "targets": [7] },
            	            { className: "dt-right", "targets": []}
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": "callNote/callNoteLookup",
    			        	"type": "GET"
    			        	},
    			        columns: [
    			        	{ title: "ID", width:"5%", searchable:true, "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.call_log_id != null){return (row.call_log_id+"");}
    			            } },
    			            { title: "Address", width:"20%", searchable:true, "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.address_name != null){return (row.address_name+"");}
    			            } },
    			            { title: "Name", width:"20%", searchable:true, "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
    			            	if(row.contact_name != null ){return row.contact_name;}
    			            } },
    			            { title: "Summary", width:"20%", searchable:true, "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.summary != null){return (row.summary+"");}
    			            } },
    			            { title: "ANSI", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.ansi_contact != null){return (row.ansi_contact+"");}
    			            } },
    			            { title: "Start", width:"10%", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.start_time != null){return (row.start_time+"");}
    			            } },
    			            { title: "Type",  width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.contact_type != null){return (row.contact_type+"");}
    			            } },			            
    			            { title: "<bean:message key="field.label.action" />", width:"5%", data: function ( row, type, set ) {	
    			            	{
//    				            	var $edit = '<a href="#" class="editAction" data-id="'+row.expenseId+'"><webthing:edit>Edit</webthing:edit></a>';
//    			            		return "<ansi:hasPermission permissionRequired='CLAIMS_WRITE'>"+$edit+"</ansi:hasPermission>";
									return "xxxxx";
    			            	}
    			            	
    			            } }
    			            ],
    			            "initComplete": function(settings, json) {
    			            	var myTable = this;
    			            	LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#displayTable", CALL_NOTE_LOOKUP.makeTable);
    			            },
    			            "drawCallback": function( settings ) {
    			            	//CALL_NOTE_LOOKUP.doFunctionBinding();
    			            }
    			    } );
            		//new $.fn.dataTable.FixedColumns( dataTable );
            	},
        		
        		
        		
        		
        		
        		
        		
        		
        		
            	
            	
            	
            	
            	
            	
        		
        		
        		
        		
        		
        		
        		
        		
        		
        		

        		
        		
            	
            	doFunctionBinding : function () {
					$( ".editAction" ).on( "click", function($clickevent) {
						var $expenseId = $(this).attr("data-id");
						CALL_NOTE_LOOKUP.doGetLabor($expenseId);
					});
				},
            	
            	
            	
				
				doGetLabor : function($expenseId) {
					console.log("getting labor: " + $expenseId)
					var $url = 'claims/employeeExpense/' + $expenseId;
					var jqxhr = $.ajax({
						type: 'GET',
						url: $url,
						statusCode: {
							200 : function($data) {
								$("#ndl-crud-form").attr("data-expenseid",$expenseId);
								$.each( $("#ndl-crud-form input"), function($index, $value) {
									var $name = $($value).attr("name");
									var $selectorName = '#ndl-crud-form input[name="' + $name + '"]';
									$($selectorName).val($data.data.item[$name]);
			            		});
								$('#ndl-crud-form input[name="washerName"]').val($data.data.item.firstName + " " + $data.data.item.lastName);
			            		$.each( $("#ndl-crud-form select"), function($index, $value) {
			            			var $name = $($value).attr("name");
									var $selectorName = '#ndl-crud-form select[name="' + $name + '"]';
									$($selectorName).val($data.data.item[$name]);
			            		});
								$("#ndl-crud-form").dialog("open");
							},
							403: function($data) {
								$("#globalMsg").html("Session Timeout. Log in and try again").show();
							},
							404: function($data) {
								$("#globalMsg").html("Invalid expense id. Reload and try again").show();
							},
							405: function($data) {
								$("#globalMsg").html("System Error 405. Contact Support").show();
							},
							500: function($data) {
								$("#globalMsg").html("System Error 500. Contact Support").show();
							},
						},
						dataType: 'json'
					});
				},
				
				
            	
            	doPost : function() {
            		console.log("doPost");
        			var $expenseId = $( "#ndl-crud-form ").attr("data-expenseid");
					var $url = 'claims/employeeExpense/' + $expenseId;
        			
            		var $outbound = {};
            		$.each( $("#ndl-crud-form input"), function($index, $value) {
            			$outbound[$($value).attr("name")] = $($value).val();
            		});
            		$.each( $("#ndl-crud-form select"), function($index, $value) {
            			$outbound[$($value).attr("name")] = $($value).val();
            		});
            		var jqxhr3 = $.ajax({
						type: 'POST',
						url: $url,
						data: JSON.stringify($outbound),
						statusCode: {
							200:function($data) {
								$("#ndl-crud-form .err").html("");
								if ( $data.responseHeader.responseCode == 'SUCCESS') {
									$("#displayTable").DataTable().ajax.reload();
									$("#globalMsg").html("Success").show().fadeOut(3000);
									$("#ndl-crud-form").dialog("close");
								} else if ($data.responseHeader.responseCode == 'EDIT_FAILURE') {
									$.each($data.data.webMessages, function($index, $value) {
										var $errSelector = '#' + $index + "Err";
				        				$($errSelector).html($value[0]);
									});
								} else {
									$("#globalMsg").html("Invalid response code " + $data.responseHeader.responseCode + ". Contact Support").show();
									$("#ndl-crud-form").dialog("close");
								}

							},
							403: function($data) {								
								$("#globalMsg").html("Session Expired. Log In and try again").show();
							},
							404: function($data) {
								$("#globalMsg").html("System Error Division 404. Contact Support").show();
							},
							500: function($data) {
								$("#globalMsg").html("System Error Division 500. Contact Support").show();
							}
						},
						dataType: 'json'
					});
            		
            		
            	},
            	
            	
            	
            	makeAutoCompleteXXX : function() {
					var $displaySelector = '#note-crud-form input[name="address"]';
            		var $idSelector = '#note-crud-form input[name="addressId"]';
            		$( displaySelector ).autocomplete({
						'source':"addressTypeAhead?term=",
						select: function( event, ui ) {
							$($idSelector).val(ui)
       			      	},
       			      	response: function(event, ui) {
							if (ui.content.length === 0) {
								alert("No Matching Address")
							}
						}
       			 	});
            		
            		
            		
            		var $washerAutoComplete = $($displaySelector).autocomplete({
						source: "washerTypeAhead?",
						select: function( event, ui ) {
							$( $idSelector ).val(ui.item.id);								
   				      	},
						response: function(event, ui) {
							if (ui.content.length === 0) {
								$("#washerIdErr").html("No Washer Found");
					        } else {
					        	$("#washerIdErr").html("");
					        }
						}
					});	
            	},
            	
            	
            	
            	
            	
            	
            	
            	
            	makeDivisionList : function() {
					console.log("makeDivisionList");
					var jqxhr3 = $.ajax({
						type: 'GET',
						url: 'division/list',
						data: {},
						statusCode: {
							200:function($data) {
								var $select = $("#ndl-crud-form select[name='divisionId']");
								$('option', $select).remove();

								$select.append(new Option("",""));
								$.each($data.data.divisionList, function(index, val) {
								    $select.append(new Option(val.divisionNbr + "-" + val.divisionCode, val.divisionId));
								});
							},
							403: function($data) {								
								$("#globalMsg").html("Session Expired. Log In and try again").show();
							},
							404: function($data) {
								$("#globalMsg").html("System Error Division 404. Contact Support").show();
							},
							500: function($data) {
								$("#globalMsg").html("System Error Division 500. Contact Support").show();
							}
						},
						dataType: 'json',
						async:false
					});
				},
				
            	
            	
				
        		
        		
        		
        		
        		makeOptionList : function($optionList, $callBack) {
					console.log("getOptionList");
	    			var $returnValue = null;
	    			var jqxhr1 = $.ajax({
	    				type: 'GET',
	    				//url: 'options',
	    				url: 'code/employee_expense/expense_type',
	    				data: $optionList,			    				
	    				statusCode: {
	    					200: function($data) {
	    						console.log("Successful Log");
	    						console.log($data);
	    						$callBack($data.data);		    						
	    					},			    				
	    					403: function($data) {
	    						console.log("403");
	    						$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
	    					}, 
	    					404: function($data) {
	    						console.log("404");
	    						$("#globalMsg").html("System Error Option 404. Contact Support").show();
	    					}, 
	    					405: function($data) {
	    						console.log("405");
	    						$("#globalMsg").html("System Error Option 405. Contact Support").show();
	    					}, 
	    					500: function($data) {
	    						console.log("500");
	    						$("#globalMsg").html("System Error Option 500. Contact Support").show();
	    					}, 
	    				},
	    				dataType: 'json'
	    			});
	    		},
	    		
	    		
	    		
	    		populateOptionList : function($data) {
	    			var $select = $("#ndl-crud-form select[name='expenseType']");
					$('option', $select).remove();
					$select.append(new Option("",""));
					$.each($data.codeList, function(index, val) {
					    $select.append(new Option(val.value, val.displayValue));
					});
	    		},
	    		
        	}
      	  	

        	CALL_NOTE_LOOKUP.init();
        	
        });
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Call Notes</h1>
    	
    <webthing:lookupFilter filterContainer="filter-container" />
    
 	<table id="displayTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:1300px;width:1300px;">       	
    </table>
    <input type="button" value="New" class="prettyWideButton" id="new-note-button" />
    
    <webthing:scrolltop />
    
    <div id="note-crud-form" class="ui-front">
    	<table>
    		<tr>
    			<td><span class="formLabel">ID:</span></td>
    			<td><span id="call-note-id"></span><input type="hidden" name="callNoteId" /></td>
    			<td><span id="callNoteIdErr" class="err"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formLabel">Address:</span></td>
    			<td><input type="text" name="address" /><input type="hidden" name="addressId"/></td>
    			<td><span id="addressIdErr" class="err"></span></td>
    		</tr>    		
    		<tr>
    			<td><span class="formLabel">ANSI Contact:</span></td>
    			<td><input type="text" name="ansi" /><input type="hidden" name="userId"/></td>
    			<td><span id="userIdErr" class="err"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formLabel">Contact Name:</span></td>
    			<td><input type="text" name="contact" /><input type="hidden" name="contactId"/></td>
    			<td><span id="contactIdErr" class="err"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formLabel">Date/Time:</span></td>
    			<td><input type="date" name="startDate"  /> <input type="time" name="startTime" /></td>
    			<td><span id="startDateErr" class="err"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formLabel">Contact Type:</span></td>
    			<td><select name="contactType"></select></td>
    			<td><span id="contactTypeErr" class="err"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formLabel">Summary:</span></td>
    			<td><input type="text" name="summary" /></td>
    			<td><span id="summaryErr" class="err"></span></td>
    		</tr>
    		<tr>
    			<td style="vertical-align:top;"><span class="formLabel">Notes:</span></td>
    			<td><textarea rows="8" cols="45" name="notes"></textarea></td>
    			<td><span id="notesErr" class="err"></span></td>
    		</tr>
    	</table>
    </div>
    
    
    <div id="new-address-modal">
    	<div class="modal-label">No matching address. Create one?</div>
    	<table>
    		<tr>
    			<td><span class="formLabel">Name:</span></td>
    			<td><input type="text" name="name" /></td>
    			<td><span id="nameErr" class="err"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formLabel">Address1:</span></td>
    			<td><input type="text" name="address1" /></td>
    			<td><span id="address1Err" class="err"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formLabel">City:</span></td>
    			<td><input type="text" name="city"  /></td>
    			<td><span id="cityErr" class="err"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formLabel">State:</span></td>
    			<td><input type="text" name="state"  /></td>
    			<td><span id="stateErr" class="err"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formLabel">Zip:</span></td>
    			<td><input type="text" name="zip" /></td>
    			<td><span id="zipErr" class="err"></span></td>
    		</tr>    		
    	</table>
    </div>
    
    
    <div id="new-contact-modal">
    	<div class="modal-label">No matching contact. Create one?</div>
    	<table>
    		<tr>
    			<td><span class="formLabel">Name:</span></td>
    			<td><input type="text" name="firstName" placeholder="First"/> <input type="text" name="lastName" placeholder="Last" /></td>
    			<td><span id="nameErr" class="err"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formLabel">Phone:</span></td>
    			<td><input type="text" name="phone" /></td>
    			<td><span id="phoneErr" class="err"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formLabel">Email:</span></td>
    			<td><input type="text" name="email"  /></td>
    			<td><span id="emailErr" class="err"></span></td>
    		</tr>
    	</table>
    </div>
    </tiles:put>
		
</tiles:insert>

