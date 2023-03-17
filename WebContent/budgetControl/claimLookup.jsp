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
        Ticket Claim Lookup
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
    	<link rel="stylesheet" href="css/lookup.css" />
    	<link rel="stylesheet" href="css/ticket.css" />
    	<link rel="stylesheet" href="css/callNote.css" />
    	<link rel="stylesheet" href="css/accordion.css" />
    	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.9.4/Chart.min.js"></script>
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/addressUtils.js"></script>
    	<script type="text/javascript" src="js/lookup.js"></script> 
    	<script type="text/javascript" src="js/ticket.js"></script> 
        <style type="text/css">
        	#bcr_delete_confirmation_modal {
        		display:none;
        	}
        	#bcr_edit_modal {
        		display:none;
        	}
        	#bcr_new_claim_modal {
        		display:none;
        	}
        	#bcr_quick_claim_modal {
        		display:none;
        	}
        	#bcr_title_prompt {
        		display:none;
        	}
			#bcr_panels .display {
				display:none;
			}
        	#filter-container {
        		width:402px;
        		float:right;
        	}
        	#ndl-crud-form {
        		display:none;
        		background-color:#FFFFFF;
				color:#000000;
				width:400px;
				padding:15px;
        	}
			#session_expire_modal {
				display:none;
			}
			.action-link {
				cursor:pointer;
			}
			.actual-saving {
				display:none;
			}
        	.aligned-center {
        		text-align:center;
        	}
        	.aligned-right {
        		text-align:right;
        	}
        	.all-ticket-spreadsheet {
        		text-decoration:none;
        	}
        	.bcr_employees_display .column-header {
				font-weight:bold;
			}
			.bcr_employees_display th {
				text-align:right;
			}
			.bcr_employees_display .border-set {
				border-right:solid 1px #404040;
				padding-right:4px;
			}
			.bcr_totals_display .column-header {
				font-weight:bold;
			}
			.bcr_totals_display th {
				text-align:right;
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
			.expenseDetails {
				display:none;
			}	
			
			.field-container {
				cursor:pointer;
			}
        	.form-label {
				font-weight:bold;
			}	
			.new-bcr {
				cursor:pointer;
			}
			.newExpenseItem {
				display:none;
			}	
			.priorityCheck {
				float:right;
				width:5%;
				margin-right:8px;
			}
			.table-header {
				text-align:center;
				font-weight:bold;
				font-size:110%;
			}
			.ticket-week-display {
				display:none;
			} 
			.ticket-note {
				text-decoration:underline;
				cursor:pointer;
			}
			.quick-claim {
				cursor:pointer;
			}
			.quick_claim_err {
				float:right;
				margin-right:8px;
				display:none;
			}
			.prettyWideButton {
				height:30px;
				min-height:30px;
			}
			
			#ticket-modal {
				display:none;	
			}
			.ticket-clicker {
				color:#000000;
			}
        </style>
        
        <script type="text/javascript">
        
        $(document).ready(function() {
        	;CLAIM_LOOKUP = {
        		init : function() {
        			console.log("Claim lookup init");
        			CLAIM_LOOKUP.doTicketLookup();
        		},
        		
        		doTicketLookup : function() {
					var $fileName = "claim";
					
        			var $buttonArray = [
        	        	'pageLength',
        	        	'copy', 
        	        	{extend:'csv', filename:'* ' + $fileName}, 
        	        	{extend:'excel', filename:'* ' + $fileName}, 
        	        	{extend:'pdfHtml5', orientation: 'landscape', filename:'* ' + $fileName}, 
        	        	'print',
        	        	//{extend:'colvis', label: function () {doFunctionBinding();$('#ticketTable').draw();}}
        	        ];
        			

        			
        			
        			var $jobEditTag = '<webthing:edit>No Services Defined. Revise the Job</webthing:edit>';
        			
        			$("#claimLookup").DataTable( {
            			"aaSorting":		[[0,'asc']],
            			"processing": 		true,
            	        "serverSide": 		true,
            	        "autoWidth": 		false,
            	        "deferRender": 		true,
            	        "scrollCollapse": 	true,
            	        "scrollX": 			true,
            	        "pageLength":		50,
            	        rowId: 				'dt_RowId', // 'claim_id', //'dt_RowId',
            	        destroy : 			true,		// this lets us reinitialize the table
            	        dom: 				'Bfrtip',
            	        "searching": 		true,
            	        "searchDelay":		800,
            	        lengthMenu: [
            	        	[ 10, 50, 100, 500, 1000 ],
            	            [ '10 rows', '50 rows', '100 rows', '500 rows', '1000 rows' ]
            	        ],
            	        buttons: $buttonArray,
            	        "columnDefs": [
             	            { "orderable": true, "targets": -1 },
             	            { className: "dt-head-center", "targets":[0,1,2,3,4,5,6,7,8,9,10,11,12,13]},
            	            { className: "dt-left", "targets": [0,1,2,3,4,9,13] },
            	            { className: "dt-center", "targets": [12] },
            	            { className: "dt-right", "targets": [5,6,7,8,10,11]}
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": "bcr/claimLookup",
    			        	"type": "GET",
    			        	"data": {},
    			        	},
    			        columns: [
    			        	{ title: "Account", width:"15%", searchable:true, "defaultContent": "<i>N/A</i>", data:'job_site_name' }, 
    			        	{ title: "Div", width:"5%", searchable:true, "defaultContent": "<i>N/A</i>", data:'div' },
    			            { title: "Ticket Number", width:"6%", searchable:true, "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.ticket_id != null){return ('<a href="#" data-id="'+row.ticket_id+'" class="ticket-clicker">'+row.ticket_id+'</a>');}
    			            } },
    			            { title: "Claim Week", width:"5%", searchable:true, searchFormat: "nnnn-nn",  data: "claim_week" },
    			            { title: "D/L", width:"6%", searchable:true, "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.dl_amt != null){return (row.dl_amt.toFixed(2)+"");}
    			            } },
    			       //     { title: "+Exp", width:"6%", searchable:true, searchFormat: "First Last Name", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			       //     	//if(row.ansi_contact != null){return (row.ansi_contact+"");}
    			       //     	return '<input type="text" style="width:20px;"/>';
    			       //     } },
    			       //     { title: "Total D/L", width:"6%", searchable:true, searchFormat: "YYYY-MM-dd hh:mm", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			       //     	//if(row.start_time != null){return (row.start_time+"");}
    			       //     	return 'x';
    			       //     } },
    			            { title: "Total Volume",  width:"6%", searchable:true, searchFormat: "###.##", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.total_volume != null){return (row.total_volume.toFixed(2)+"");}
    			            } },		
    			            { title: "Volume Claimed",  width:"6%", searchable:true, searchFormat: "###.##", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.volume_claimed != null){return (row.volume_claimed.toFixed(2)+"");}
    			            } },
    			      //      { title: "Volume Remaining",  width:"6%", searchable:true, searchFormat: "Name #####", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			      //      	if(row.volume_remaining != null){return (row.volume_remaining.toFixed(2)+"");}
    			      //      } },
    			      //      { title: "Expense Volume", width:"6%", searchable:true, "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			      //      	if(row.dl_expenses != null){return (row.dl_expenses.toFixed(2)+"");}
    			      //      } },
        			        { title: "Expense Volume", width:"6%", searchable:true, "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
        			          	if(row.passthru_volume != null){return (row.passthru_volume.toFixed(2)+"");}
    			            } },
    			            { title: "Volume Remaining",  width:"6%", searchable:true, searchFormat: "###.##", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.volume_remaining != null){return (row.volume_remaining.toFixed(2)+"");}
    			            } },
							{ title: "Notes",  width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	var $displayNote = '';
    			            	if(row.notes != null && row.notes != ''){$displayNote = '<span class="tooltip ticket-note">'+row.notes_display+'<span class="tooltiptext">'+row.notes+'</span></span>';}
    			            	return $displayNote;
    			            } },
    			            { title: "Billed Amount",  width:"6%", searchable:true, searchFormat: "###.##", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.billed_amount != null){return (row.billed_amount.toFixed(2)+"");}
    			            } },
    			            { title: "Diff Clm/Bld",  width:"6%", searchable:true, searchFormat: "###.##", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.claimed_vs_billed != null){return (row.claimed_vs_billed.toFixed(2)+"");}
    			            } },
    			            { title: "Ticket Status",  width:"4%", searchable:true, "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.ticket_status != null){return (row.ticket_status+"");}
    			            } },
    			            { title: "Service",  width:"4%", searchable:true,  
    			            	data: function ( row, type, set ) {
    			            		if ( row.service_tag_id == null ) {
    			            			$display = '<a href="jobMaintenance.html?id=' + row.job_id +'">' + $jobEditTag + '</a>';
    			            		} else {
    			            			$display = row.service_tag_id;
    			            		}
    			            		return $display;
    			            	} 
    			            },
    			            { title: "Equipment",  width:"4%", searchable:true,  
    			            	data: function ( row, type, set ) {
    			            		var $display = [];
    			            		if ( row.equipment_tags != null ) {
	    			            		var $equipmentList = row.equipment_tags.split(",");
	    			            		var $unclaimedList = row.unclaimed_equipment.split(",");
	    			            		$.each($equipmentList, function($index, $value) {
	    			            			if ( $unclaimedList.includes($value) ) {
	    			            				$display.push('<span class="jobtag-display">'+ $value + '</span>');
	    			            			} else {
	    			            				$display.push('<span class="jobtag-display jobtag-selected">'+ $value + '</span>');
	    			            			}
	    			            		});
    			            		}
    			            		return $display.join("");
    			            	}
    			            },
    			            { title: "Employee",  width:"13%", searchable:true, data:'employee' }
    			            ],
    			            "initComplete": function(settings, json) {
    			            	var myTable = this;    			            	
    			            	LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#claimLookup", CLAIM_LOOKUP.doTicketLookup);
    			            },
    			            "drawCallback": function( settings ) {
    			            	CLAIM_LOOKUP.doFunctionBinding();
    			            	$("#doScroll").off("click");
    			            	$("#doScroll").click( function($event) {
    								$('html, body').animate({
    									scrollTop:$("#ticketTable .row243").offset().top
    								}, 2000);    								
    			            	});    			          
    			            },
    			            "createdRow": function(nRow, aData, iDataIndex ) {
								// as each row is created, add a class to the <tr> tags
    			            	$(nRow).addClass("row"+aData.claim_id);
    			            }
    			    } );
        		},
        		
        		
        		
        		
        		doFunctionBinding : function() {
        			console.log("doFunctionBinding");  
        			// make sure a click only does stuff one time
        			$(".ticket-clicker").off("click");
        			
        			// make sure a click actually does something (but only once)
        			$(".ticket-clicker").on("click", function($clickevent) {
    					$clickevent.preventDefault();
    					var $ticketId = $(this).attr("data-id");
    					
    					if ( ! $("#ticket-modal").hasClass('ui-dialog-content') ) {
    						TICKETUTILS.makeTicketViewModal("#ticket-modal");
    					}
    					
    					TICKETUTILS.doTicketViewModal("#ticket-modal",$ticketId);
    					$("#ticket-modal").dialog("open");
    				});	

        		},
        	};
        	
        	CLAIM_LOOKUP.init();
        	
        	
        });
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Ticket Claim Lookup</h1>
    	
   		<webthing:lookupFilter filterContainer="filter-container" />
    	
	    <table id="claimLookup">
   		</table>
    	
   	    <webthing:scrolltop />
    
	    <webthing:ticketModal ticketContainer="ticket-modal" />
	    	    
    </tiles:put>
		
</tiles:insert>

