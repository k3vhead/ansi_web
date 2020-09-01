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
    	<link rel="stylesheet" href="css/callNote.css" />
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
        	.call-note-detail {
        		cursor:pointer;
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
					    $select.append(new Option(val.displayValue, val.value));
					});

        		},
        		
        		
        		
        		
        		doFunctionBinding : function () {
					$(".call-note-detail").on("click", function($clickevent) {
						var $callNoteId = $(this).attr("data-callnoteid");
						CALL_NOTE_LOOKUP.doGetCallNoteDetail($callNoteId);
					});
				},
            	
            	
				
				doGetCallNoteDetail : function($callNoteId) {
					console.log("getting detail: " + $callNoteId)
					var $url = 'callNote/callNoteDetail/' + $callNoteId;
					var jqxhr = $.ajax({
						type: 'GET',
						url: $url,
						statusCode: {
							200 : function($data) {
								$("#call-note-detail-modal .callLogId").html($data.data.callLogId);
								$("#call-note-detail-modal .content").html($data.data.content);
								$("#call-note-detail-modal .summary").html($data.data.summary);
								$("#call-note-detail-modal .addressName").html($data.data.addressName);
								$("#call-note-detail-modal .address1").html($data.data.address1);
								if ( $data.data.address2 == null || $data.data.address2 == "" ) {
									$("#call-note-detail-modal .address2container").hide();
								} else {
									$("#call-note-detail-modal .address2").html($data.data.address2);
									$("#call-note-detail-modal .address2container").show();
								}
								$("#call-note-detail-modal .city").html($data.data.city);
								$("#call-note-detail-modal .state").html($data.data.state);
								$("#call-note-detail-modal .zip").html($data.data.zip);
								$("#call-note-detail-modal .contactName").html($data.data.contactName);
								$("#call-note-detail-modal .ansiContact").html($data.data.ansiContact);
								$("#call-note-detail-modal .ansiPhone").html($data.data.ansiPhone);
								$("#call-note-detail-modal .ansiEmail").html($data.data.ansiEmail);
								$("#call-note-detail-modal .startTime").html($data.data.startTime);
								$("#call-note-detail-modal .contactType").html($data.data.contactType);
								$("#call-note-detail-modal .xref").html($data.data.xref);
								
								$("#call-note-detail-modal").dialog("open");
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
					
					
					
					
					$( "#call-note-detail-modal" ).dialog({
						title:'Call Note Detail',
						autoOpen: false,
						height: 400,
						width: 850,
						modal: true,
						closeOnEscape:true,
						//open: function(event, ui) {
						//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
						//},
						buttons: [
							{
								id: "call-note-detail-cancel-button",
								click: function($event) {
									$( "#call-note-detail-modal" ).dialog("close");
								}
							}
						]
					});	
					$("#call-note-detail-cancel-button").button('option', 'label', 'Close');
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
             	            { "orderable": true, "targets": -1 },
            	            { className: "dt-left", "targets": [0,1,2,3,4,5,6, 8] },
            	            { className: "dt-center", "targets": [7] },
            	            { className: "dt-right", "targets": []}
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": "callNote/callNoteLookup",
    			        	"type": "GET"
    			        	},
    			        columns: [
    			        	{ title: "ID", width:"3%", searchable:true, "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.call_log_id != null){return (row.call_log_id+"");}
    			            } },
    			            { title: "Address", width:"20%", searchable:true, "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.address_name != null){return (row.address_name+"");}
    			            } },
    			            { title: "Name", width:"20%", searchable:true, searchFormat: "First Last Name", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
    			            	if(row.contact_name != null ){return row.contact_name;}
    			            } },
    			            { title: "Summary", width:"20%", searchable:true, "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.summary != null){return (row.summary+"");}
    			            } },
    			            { title: "ANSI", width:"10%", searchable:true, searchFormat: "First Last Name", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.ansi_contact != null){return (row.ansi_contact+"");}
    			            } },
    			            { title: "Start", width:"12%", searchable:true, searchFormat: "YYYY-MM-dd hh:mm", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.start_time != null){return (row.start_time+"");}
    			            } },
    			            { title: "Type",  width:"7%", searchable:true, searchFormat: "Type Name", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.contact_type != null){return (row.contact_type+"");}
    			            } },		
    			            { title: "Xref",  width:"10%", searchable:true, searchFormat: "Name #####", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.xref != null){return (row.xref+"");}
    			            } },
    			            { title: "<bean:message key="field.label.action" />", width:"5%", data: function ( row, type, set ) {	
    			            	{
//    				            	var $edit = '<a href="#" class="editAction" data-id="'+row.expenseId+'"><webthing:edit>Edit</webthing:edit></a>';
//    			            		return "<ansi:hasPermission permissionRequired='CLAIMS_WRITE'>"+$edit+"</ansi:hasPermission>";
									return '<span class="call-note-detail" data-callnoteid="'+row.call_log_id+'"><webthing:view styleClass="green">View</webthing:view></span>';
    			            	}
    			            	
    			            } }
    			            ],
    			            "initComplete": function(settings, json) {
    			            	var myTable = this;
    			            	LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#displayTable", CALL_NOTE_LOOKUP.makeTable);
    			            },
    			            "drawCallback": function( settings ) {
    			            	CALL_NOTE_LOOKUP.doFunctionBinding();
    			            }
    			    } );
            		//new $.fn.dataTable.FixedColumns( dataTable );
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
    <%-- we'll add this functionality later (maybe)
    <input type="button" value="New" class="prettyWideButton" id="new-note-button" />
     --%>
     
    <webthing:scrolltop />
    
    <webthing:callNoteModals />
    </tiles:put>
		
</tiles:insert>

