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
        Ticket Status
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
    	<link rel="stylesheet" href="css/lookup.css" />
    	<link rel="stylesheet" href="css/ticket.css" />
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
        	;TICKETSTATUS = {
        		datatable : null,
        		
        		init : function() {
        			TICKETSTATUS.createTable();
        			TICKETSTATUS.makeClickers();
        			TICKETSTATUS.makeModals();
        		},
        		
        		
        		
				createTable : function() {
            		TICKETSTATUS.dataTable = $('#displayTable').DataTable( {
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
    			            { title: "Direct Labor", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.claimed_dl_amt != null){return (parseFloat(row.claimed_dl_amt).toFixed(2));}
    			            } },
    			            { title: "+ Expenses" , "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.claimed_dl_exp != null){return (parseFloat(row.claimed_dl_exp).toFixed(2));}
    			            } },
    			            { title: "= Total", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.claimed_dl_total != null){return (parseFloat(row.claimed_dl_total).toFixed(2)+"");}
    			            } },
    			            { title: "Total Volume", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.total_volume != null){return (parseFloat(row.total_volume).toFixed(2)+"");}
    			            } },
    			            { title: "Volume Claimed", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.claimed_volume != null){return (parseFloat(row.claimed_volume).toFixed(2)+"");}
    			            } },
    			            { title: "Passthru",  "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.passthru_volume != null){return (parseFloat(row.passthru_volume).toFixed(2)+"");}
    			            } },			            
    			            { title: "Volume Claimed Total", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.claimed_volume_total != null){return (parseFloat(row.claimed_volume_total).toFixed(2)+"");}
    			            } },
    			            { title: "Remaining Volume", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.volume_remaining != null){return (parseFloat(row.volume_remaining).toFixed(2)+"");}
    			            } },
    			            { title: "Invoiced Amount", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.billed_amount != null){return (parseFloat(row.billed_amount).toFixed(2)+"");}
    			            } },
    			            { title: "Diff CLM/BLD", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.claimed_vs_billed != null){return (parseFloat(row.claimed_vs_billed).toFixed(2)+"");}
    			            } },
    			            { title: "Amount Paid", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.paid_amt != null){return (parseFloat(row.paid_amt).toFixed(2)+"");}
    			            } },
    			            { title: "Amount Due", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.amount_due != null){return (parseFloat(row.amount_due).toFixed(2)+"");}
    			            } },
    			            { title: "<bean:message key="field.label.action" />",  searchable:false, data: function ( row, type, set ) {	
    			            	{
    				            	var $claim = '';
    				            	if (row.ticket_status=='D' || row.ticket_status=='C') {
    				            		$claim = '<a href="#" class="claimAction" data-id="'+row.ticket_id+'"><webthing:invoiceIcon styleClass="green">Budget Control</webthing:invoiceIcon></a>';
    				            	}
    			            		return "<ansi:hasPermission permissionRequired='CLAIMS_READ'>"+$claim+"</ansi:hasPermission>";
    			            	}
    			            	
    			            } }],
    			            "initComplete": function(settings, json) {
    			            	LOOKUPUTILS.makeFilters(this, "#filter-container", "#displayTable", TICKETSTATUS.createTable);
    			            	//TICKETSTATUS.doFunctionBinding();
    			            },
    			            "drawCallback": function( settings ) {
    			            	TICKETSTATUS.doFunctionBinding();
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
      	  	

        	TICKETSTATUS.init();
        	
        });
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Ticket Status</h1>
    	
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
    </tiles:put>
		
</tiles:insert>

