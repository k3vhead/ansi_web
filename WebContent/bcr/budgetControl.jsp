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
        Budget Control
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
    	<link rel="stylesheet" href="css/lookup.css" />
    	<link rel="stylesheet" href="css/ticket.css" />
    	<link rel="stylesheet" href="css/callNote.css" />
    	<link rel="stylesheet" href="css/accordion.css" />
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/addressUtils.js"></script>
    	<script type="text/javascript" src="js/lookup.js"></script> 
    	<script type="text/javascript" src="js/ticket.js"></script> 
        <style type="text/css">
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
        	;BUDGETCONTROL = {        		
        		init : function() {
        			ANSI_UTILS.makeDivisionList(BUDGETCONTROL.makeDivListSuccess, BUDGETCONTROL.makeDivListFail);
        			BUDGETCONTROL.makeAccordion();
        			BUDGETCONTROL.makeClickers();
        		},
        		
        		
        		
        		
        		makeAccordion : function() {
        			$('ul.accordionList').accordion({
						//autoHeight: true,
						heightStyle: "content",
						alwaysOpen: true,
						header: 'h4',
						fillSpace: false,
						collapsible: true,
						active: true
					});
        		},
        		
        		
        		
        		makeClickers : function() {
        			
        		},
        		
        		
        		
        		makeDivListFail : function($data) {
        			console.log("makeDivListFail");
        			$("#globalMsg").html("Failure Retrieving Divisions. Contact Support");
        		},
        		
        		
        		
        		makeDivListSuccess : function($data) {
        			console.log("makeDivListSuccess");
        			var $divisionField = $("#bcr_title_prompt select[name='divisionId']");
        			$divisionField.append(new Option("",""));
       				$.each($data.data.divisionList, function(index, val) {
       					var $displayValue = val.divisionNbr + "-" + val.divisionCode;
       					$divisionField.append(new Option($displayValue, val.divisionId));
       				});
       				
        			BUDGETCONTROL.makeModals();
        			$( "#bcr_title_prompt" ).dialog("open");
        			$("#workWeekSelector").change(function() {
        				console.log("Date changed");
        			});
        		},
        		
        		
        		
        		
        		makeModals : function() {
        			TICKETUTILS.makeTicketViewModal("#ticket-modal");
        			
        			
        			$( "#bcr_title_prompt" ).dialog({
        				title:'Budget Control',
        				autoOpen: false,
        				height: 600,
        				width: 950,
        				modal: true,
        				closeOnEscape:false,
        				open: function(event, ui) {
        					$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
        				},
        				buttons: [
        					{
        						id:  "bcr_title_prompt_save",
        						click: function($event) {
        							$("#bcr_title_prompt").dialog("close");
        						}
        					}
        				]
        			});	
        			$("#bcr_title_prompt_save").button('option', 'label', 'Go');
        			
        			
        		}
        	};
        	
        	
        	
        	
        	;BCR_PLACEHOLDER = {
        		datatable : null,
        		
        		init : function() {
        			BUDGETCONTROL.createTable();
        			BUDGETCONTROL.makeClickers();
        			BUDGETCONTROL.makeModals();
        			CALLNOTE.init();
        		},
        		
        		
        		
				createTable : function() {
            		BUDGETCONTROL.dataTable = $('#displayTable').DataTable( {
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
            	            { className: "dt-left", "targets": [0,1,2] },
            	            { className: "dt-center", "targets": [3,16] },
            	            { className: "dt-right", "targets": [4,5,6,7,8,9,10,11,12,13,14,15]}
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": "claims/ticketStatusLookup",
    			        	"type": "GET"
    			        	},
    			        columns: [
    			        	
    			            { width:"4%", title: "Div", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.div != null){return (row.div+"");}
    			            } },
    			            { width:"23%", title: "Account", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.job_site_name != null){return (row.job_site_name);}
    			            } },
    			            { title: "Ticket", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.ticket_id != null){return ('<a href="#" data-id="'+row.ticket_id+'" class="ticket-clicker">'+row.ticket_id+'</a>');}
    			            } },
    			            { title: "Status", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.ticket_status != null){return ('<span class="tooltip">' + row.ticket_status + '<span class="tooltiptext">' + row.ticket_status_description + '</span></span>');}
    			            } },
    			            { title: "Direct Labor", "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
    			            	if(row.claimed_dl_amt != null){return (parseFloat(row.claimed_dl_amt).toFixed(2));}
    			            } },
    			            { title: "+ Expenses" , "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
    			            	if(row.claimed_dl_exp != null){return (parseFloat(row.claimed_dl_exp).toFixed(2));}
    			            } },
    			            { title: "= Total", "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
    			            	if(row.claimed_dl_total != null){return (parseFloat(row.claimed_dl_total).toFixed(2)+"");}
    			            } },
    			            { title: "Total Volume", "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
    			            	if(row.total_volume != null){return (parseFloat(row.total_volume).toFixed(2)+"");}
    			            } },
    			            { title: "Volume Claimed", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.claimed_volume != null){return (parseFloat(row.claimed_volume).toFixed(2)+"");}
    			            } },
    			            { title: "Passthru",  "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
    			            	if(row.passthru_volume != null){return (parseFloat(row.passthru_volume).toFixed(2)+"");}
    			            } },			            
    			            { title: "Volume Claimed Total", "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
    			            	if(row.claimed_volume_total != null){return (parseFloat(row.claimed_volume_total).toFixed(2)+"");}
    			            } },
    			            { title: "Remaining Volume", "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
    			            	if(row.volume_remaining != null){return (parseFloat(row.volume_remaining).toFixed(2)+"");}
    			            } },
    			            { title: "Invoiced Amount", "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
    			            	if(row.billed_amount != null){return (parseFloat(row.billed_amount).toFixed(2)+"");}
    			            } },
    			            { title: "Diff CLM/BLD", "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
    			            	if(row.claimed_vs_billed != null){return (parseFloat(row.claimed_vs_billed).toFixed(2)+"");}
    			            } },
    			            { title: "Amount Paid", "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
    			            	if(row.paid_amt != null){return (parseFloat(row.paid_amt).toFixed(2)+"");}
    			            } },
    			            { title: "Amount Due", "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
    			            	if(row.amount_due != null){return (parseFloat(row.amount_due).toFixed(2)+"");}
    			            } },
    			            { title: "<bean:message key="field.label.action" />",  searchable:false, data: function ( row, type, set ) {	
    			            	{
    				            	var $claim = '';
    				            	if (row.ticket_status=='D' || row.ticket_status=='C') {
    				            		$claim = '<ansi:hasPermission permissionRequired='CLAIMS_READ'><a href="#" class="claimAction" data-id="'+row.ticket_id+'">Ticket Note<webthing:invoiceIcon styleClass="green">Budget Control</webthing:invoiceIcon></a></ansi:hasPermission>';
    				            	}
    				            	var $notesLink = '<webthing:notes xrefType="TICKET" xrefId="'+row.ticket_id+'">Ticket Note</webthing:notes>';
    				            	console.log($notesLink);
    			            		return $claim + $notesLink;
    			            	}
    			            	
    			            } }],
    			            "initComplete": function(settings, json) {
    			            	LOOKUPUTILS.makeFilters(this, "#filter-container", "#displayTable", BUDGETCONTROL.createTable);
    			            	//BUDGETCONTROL.doFunctionBinding();
    			            },
    			            "drawCallback": function( settings ) {
    			            	BUDGETCONTROL.doFunctionBinding();
    			            	CALLNOTE.lookupLink();
    			            }
    			    } );
            		//new $.fn.dataTable.FixedColumns( dataTable );
            	},
        		
        		
            	
            	doFunctionBinding : function () {
					$( ".claimAction" ).on( "click", function($clickevent) {
						var $ticketId = $(this).attr("data-id");
						location.href="budgetControlLookup.html?id="+$ticketId
					});
					$(".ticket-clicker").on("click", function($clickevent) {
						$clickevent.preventDefault();
						var $ticketId = $(this).attr("data-id");
						TICKETUTILS.doTicketViewModal("#ticket-modal",$ticketId);
						$("#ticket-modal").dialog("open");
					});
				},
            	
            	
            	makeClickers : function() {
            		$('.ScrollTop').click(function() {
        				$('html, body').animate({scrollTop: 0}, 800);
              	  		return false;
              	    });
            		
            		
            	},
            	
            	
            	
            	makeModals : function() {
            		TICKETUTILS.makeTicketViewModal("#ticket-modal")
            	},
	    		
        	}
      	  	

        	BUDGETCONTROL.init();
        	
        });
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Budget Control</h1>
    	
    	<ul class="accordionList">
    		<li class="accordionItem">    			
        		<h4 class="accHdr">Title</h4>
        		<div id="bcr_summary">
        			<webthing:thinking style="width:100%" />
        		</div>
       		</li>
    		<li class="accordionItem">    			
        		<h4 class="accHdr">Actual Direct Labor Totals</h4>
        		<div id="bcr_totals">
        			<webthing:thinking style="width:100%" />
        		</div>
       		</li>
    		<li class="accordionItem">    			
        		<h4 class="accHdr">Budget Control Totals</h4>
        		<div id="bcr_tools">
        			<webthing:thinking style="width:100%" />
        		</div>
       		</li>
    		<li class="accordionItem">    			
        		<h4 class="accHdr">Employees</h4>
        		<div id="bcr_employees">
        			<webthing:thinking style="width:100%" />
        		</div>
       		</li>
       		<li class="accordionItem">    			
        		<h4 class="accHdr">Tickets</h4>
        		<div id="bcr_tickets">
        			<webthing:thinking style="width:100%" />
        		</div>
       		</li>
   		</ul>
   		
   	    <webthing:lookupFilter filterContainer="filter-container" />
    	
	 	<table id="displayTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:1300px;width:1300px;">
	       	<thead>
	        </thead>
	        <tfoot>
	        </tfoot>
	    </table>
	    <input type="button" value="New" class="prettyWideButton" id="new-NDL-button" />
	    
	    <webthing:scrolltop />
    
	    <webthing:ticketModal ticketContainer="ticket-modal" />
	    
	    
	    <div id="bcr_title_prompt">
	    	<table>
	    		<tr>
	    			<td><span class="form-label">Division:</span></td>
	    			<td><select name="divisionId"></select></td>
	    		</tr>
	    		<tr>
	    			<td><span class="form-label">Date:</span></td>
	    			<td><input id="workWeekSelector" type="date" name="workWeek" /></td>	    			
	    		</tr>
	    		<tr>
	    			<td colspan="2"><span class="workWeekDisplay"></span></td>
	    		</tr>
	    	</table>
	    </div>
    </tiles:put>
		
</tiles:insert>

