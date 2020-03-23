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
        <bean:message key="page.label.divisionClose" />
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
    	<link rel="stylesheet" href="css/lookup.css" />
    	<link rel="stylesheet" href="css/callNote.css" />
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/lookup.js"></script> 
        <style type="text/css">
        	#confirm-modal {
        		display:none;
        	}
        	#filter-container {
        		width:402px;
        		float:right;
        	}
        	.action-link {
        		cursor:pointer;
        	}
			.modal-label {
				width:100%;
				text-align:center;
				font-weight:bold;
			}
        </style>
        
        <script type="text/javascript">
        
        $(document).ready(function() {
        	;DIVISION_CLOSE = {
        		datatable : null,
        		callTypeList : null,
        		unclose: {},
        		
        		init : function() {
        			DIVISION_CLOSE.makeTable();
        			DIVISION_CLOSE.makeClickers();
        			DIVISION_CLOSE.makeModal();
        			DIVISION_CLOSE.makeUncloseModal();
        		},
        		
        		
        		
        		doCloseDivision : function() {
					console.log("doCloseDivision")
					var $url = 'divisionClose';
					var $outbound = { "divisionId":$("#confirm-modal input[name='divisionId']").val() };
					console.log($outbound);
					var jqxhr = $.ajax({
						type: 'POST',
						url: $url,
						data : JSON.stringify($outbound),
						statusCode: {
							200 : function($data) {
								console.log($data);
								if ( $data.responseHeader.responseCode == 'SUCCESS') {
									DIVISION_CLOSE.unclose = {};
									$('#displayTable').DataTable().ajax.reload();
									$("#globalMsg").html("Success").show().fadeOut(3000);
									$("#confirm-modal").dialog("close");
								} else if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
									$("#globalMsg").html("Invalid system state. Reload page and try again").show();
									$("#confirm-modal").dialog("close");
								} else {
									$("#globalMsg").html("System Error invalid response code ("+$data.responseHeader.responseCode+"): Contact Support").show();
									$("#confirm-modal").dialog("close");
								}	
								$('html, body').animate({scrollTop: 0}, 800);
							},
							403: function($data) {
								$("#confirm-modal").dialog("close");
								$('html, body').animate({scrollTop: 0}, 800);
								$("#globalMsg").html("Session Timeout. Log in and try again").show();
							},
							404: function($data) {
								$("#confirm-modal").dialog("close");
								$('html, body').animate({scrollTop: 0}, 800);
								$("#globalMsg").html("Invalid expense id. Reload and try again").show();
							},
							405: function($data) {
								$("#confirm-modal").dialog("close");
								$('html, body').animate({scrollTop: 0}, 800);
								$("#globalMsg").html("System Error 405. Contact Support").show();
							},
							500: function($data) {
								$("#confirm-modal").dialog("close");
								$('html, body').animate({scrollTop: 0}, 800);
								$("#globalMsg").html("System Error 500. Contact Support").show();
							},
						},
						dataType: 'json'
					});
        		},
        		
        		doUncloseDivision : function() {
					console.log("doUncloseDivision")
					var $url = 'divisionUnclose';
					var $outbound = { "divisionId":$("#confirm-unclose-modal input[name='divisionId']").val(), "actCloseDate":$("#confirm-modal input[name='actCloseDate']").val() };
					console.log($outbound);
					var jqxhr = $.ajax({
						type: 'POST',
						url: $url,
						data : JSON.stringify($outbound),
						statusCode: {
							200 : function($data) {
								console.log($data);
								if ( $data.responseHeader.responseCode == 'SUCCESS') {
									$('#displayTable').DataTable().ajax.reload();
									$("#globalMsg").html("Success").show().fadeOut(3000);
									$("#confirm-unclose").dialog("close");
								} else if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
									$("#globalMsg").html("Invalid system state. Reload page and try again").show();
									$("#confirm-unclose").dialog("close");
								} else {
									$("#globalMsg").html("System Error invalid response code ("+$data.responseHeader.responseCode+"): Contact Support").show();
									$("#confirm-unclose").dialog("close");
								}	
								$('html, body').animate({scrollTop: 0}, 800);
							},
							403: function($data) {
								$("#confirm-unclose").dialog("close");
								$('html, body').animate({scrollTop: 0}, 800);
								$("#globalMsg").html("Session Timeout. Log in and try again").show();
							},
							404: function($data) {
								$("#confirm-unclose").dialog("close");
								$('html, body').animate({scrollTop: 0}, 800);
								$("#globalMsg").html("Invalid expense id. Reload and try again").show();
							},
							405: function($data) {
								$("#confirm-unclose").dialog("close");
								$('html, body').animate({scrollTop: 0}, 800);
								$("#globalMsg").html("System Error 405. Contact Support").show();
							},
							500: function($data) {
								$("#confirm-unclose").dialog("close");
								$('html, body').animate({scrollTop: 0}, 800);
								$("#globalMsg").html("System Error 500. Contact Support").show();
							},
						},
						dataType: 'json'
					});
        		},
        		
        		
        		doFunctionBinding : function () {
        			console.log("doFunctionBinding");
					$(".division-close").on("click", function($clickevent) {
						var $divisionId = $(this).attr("data-divisionid");
						console.log("Closing division: " + $divisionId);
						$("#confirm-modal input[name='divisionId']").val($divisionId);
						$("#confirm-modal").dialog("open");
					});
					$(".division-unclose").on("click", function($clickevent) {
						var $divisionId = $(this).attr("data-divisionid");
						var $actCloseDate = $(this).attr("data-actCloseDate");
						console.log("Unclose division: " + $divisionId);
						$("#confirm-unclose input[name='divisionId']").val($divisionId);
						$("#confirm-unclose input[name='actCloseDate']").val($actCloseDate);
						$("#confirm-unclose").dialog("open");
					});
				},

				
				
        		makeClickers : function() {
            		$('.ScrollTop').click(function() {
        				$('html, body').animate({scrollTop: 0}, 800);
              	  		return false;
              	    });
            	},

        		
        		
            	
        		makeModal : function() {
					$( "#confirm-modal" ).dialog({
						title:'Confirm Close',
						autoOpen: false,
						height: 150,
						width: 500,
						modal: true,
						closeOnEscape:true,
						//open: function(event, ui) {
						//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
						//},
						buttons: [
							{
								id: "confirm-cancel-button",
								click: function($event) {
									$( "#confirm-modal" ).dialog("close");
								}
							},{
								id: "confirm-save-button",
								click: function($event) {
									DIVISION_CLOSE.doCloseDivision();
								}
							}
						]
					});	
					$("#confirm-save-button").button('option', 'label', 'Save');
					$("#confirm-cancel-button").button('option', 'label', 'Cancel');
        		},

        		
        		
            	
        		makeUncloseModal : function() {
					$( "#confirm-unclose" ).dialog({
						title:'Confirm Unclose',
						autoOpen: false,
						height: 150,
						width: 500,
						modal: true,
						closeOnEscape:true,
						//open: function(event, ui) {
						//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
						//},
						buttons: [
							{
								id: "confirm-cancel-button",
								click: function($event) {
									$( "#confirm-modal" ).dialog("close");
								}
							},{
								id: "confirm-save-button",
								click: function($event) {
									DIVISION_CLOSE.doUncloseDivision();
								}
							}
						]
					});	
					$("#confirm-save-button").button('option', 'label', 'Save');
					$("#confirm-cancel-button").button('option', 'label', 'Cancel');
        		},
        		
        		
        		
        		makeTable : function() {
        			DIVISION_CLOSE.dataTable = $('#displayTable').DataTable( {
            			"aaSorting":		[[1,'asc']],
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
            	        "pageLength":       50,
            	        lengthMenu: [
            	        	[ 10, 50, 100, 500, 1000 ],
            	            [ '10 rows', '50 rows', '100 rows', '500 rows', '1000 rows' ]
            	        ],
            	        buttons: [
            	        	'pageLength','copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print',{extend: 'colvis',	label: function () {doFunctionBinding();$('#displayTable').draw();}}
            	        ],
            	        "columnDefs": [
             	            { "orderable": true, "targets": -1 },
            	            { className: "dt-left", "targets": [0,1,2,3,4,5,6] },
            	            { className: "dt-center", "targets": [] },
            	            { className: "dt-right", "targets": []}
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": "divisionClose",
    			        	"type": "GET"
    			        	},
    			        columns: [
    			        	{ title: '<bean:message key="rpt.hdr.id" />', width:"5%", searchable:true, "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.division_id != null){return (row.division_id+"");}
    			            } },
    			            { title: '<bean:message key="rpt.hdr.division" />', width:"15%", searchable:true, "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.div != null){return (row.div+"");}
    			            } },
    			            { title: '<bean:message key="rpt.hdr.description" />', width:"25%", searchable:true, "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
    			            	if(row.description != null ){return row.description;}
    			            } },
    			            { title: '<bean:message key="rpt.hdr.actCloseDate" />', width:"17%", searchable:true, "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.act_close_date_display != null){return row.act_close_date_display+"";}
    			            } },
    			            { title: '<bean:message key="rpt.hdr.lastCloseDate" />', width:"16%", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.last_close_date_display != null){return (row.last_close_date_display+"");}
    			            } },
    			            { title: '<bean:message key="rpt.hdr.nextCloseDate" />', width:"16%", searchable:true, "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.next_close_date_display != null){return (row.next_close_date_display+"");}
    			            } },
    			            { title: "<bean:message key="field.label.action" />", width:"5%", data: function ( row, type, set ) {	
    			            	{
    			            		var $closeLink = '<ansi:hasPermission permissionRequired="DIVISION_CLOSE_WRITE"><span class="action-link division-close" data-divisionid="'+row.division_id+'"><webthing:close>Close Division</webthing:close></span></ansi:hasPermission>';
    			            		var $uncloseLink = '<span class="action-link division-unclose" data-divisionid="'+row.division_id+'" data-actCloseDate="'+row.act_close_date_display+'"><webthing:undo>Unclose Division</webthing:undo></span>';
    			            		var $notAllowed = '<webthing:ban>Not Allowed</webthing:ban>';
//    				            	var $edit = '<a href="#" class="editAction" data-id="'+row.expenseId+'"><webthing:edit>Edit</webthing:edit></a>';
//    			            		return "<ansi:hasPermission permissionRequired='CLAIMS_WRITE'>"+$edit+"</ansi:hasPermission>";
//    								return '<span class="call-note-detail" data-callnoteid="'+row.call_log_id+'"><webthing:view styleClass="green">View</webthing:view></span>';
    								DIVISION_CLOSE.unclose = {};
    				            		if ( DIVISION_CLOSE.unclose > 0 ) {
    				            			return $closeLink + $uncloseLink;
    				            		} else {
    				            			if ( row.can_close == true ) {
    											return $closeLink;
    										} else {
    											return $notAllowed;									
    										}
    				            		}
    			            	}
    			            	
    			            } }
    			            ],
    			            "initComplete": function(settings, json) {
    			            	var myTable = this;
    			            	LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#displayTable", DIVISION_CLOSE.makeTable);
    			            },
    			            "drawCallback": function( settings ) {
    			            	DIVISION_CLOSE.doFunctionBinding();
    			            }
    			    } );
            		//new $.fn.dataTable.FixedColumns( dataTable );
            	},
	    		
        	}
      	  	

        	DIVISION_CLOSE.init();
        	
        });
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1><bean:message key="page.label.divisionClose" /></h1>
    	
    <webthing:lookupFilter filterContainer="filter-container" />
    
 	<table id="displayTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:1300px;width:1300px;">       	
    </table>
    <%-- we'll add this functionality later (maybe)
    <input type="button" value="New" class="prettyWideButton" id="new-note-button" />
     --%>
     
    <webthing:scrolltop />
    
    <div id="confirm-modal">
    	<div style="width:100%; text-align:center">
    		<input type="hidden" value="" name="divisionId" />
    		Close This Division?
    	</div>
    </div>
    
    <div id="confirm-unclose-modal">
    	<div style="width:100%; text-align:center">
    		<input type="hidden" value="" name="divisionId" />
    		<input type="hidden" value="" name="actCloseDate" />
    		Unclose This Division?
    	</div>
    </div>
    </tiles:put>
		
</tiles:insert>

