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
        		datatable : null,
        		ticketFilter : '<c:out value="${BUDGET_CONTROL_TICKET_FILTER}" />',
        		
        		init : function() {
        			BUDGETCONTROL.createTable();
        			BUDGETCONTROL.makeClickers();
        			BUDGETCONTROL.makeModals();
        		},
        		

        		
        		createTable : function() {
        			var $url = "claims/budgetControlLookup";
        			if ( BUDGETCONTROL.ticketFilter != '' ) {
        				$url = $url + "/" + BUDGETCONTROL.ticketFilter;
        			}
        				
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
            	            { className: "dt-center", "targets": [3,14] },
            	            { className: "dt-right", "targets": [4,5,6,7,8,9,10,11,12,13]}
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": $url,
    			        	"type": "GET"
    			        	},
    			        columns: [
    			        	{ title: "Div", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.div != null){return (row.div+"");}
    			            } },
    			            { width:"20%", title: "Account", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.job_site_name != null){return (row.job_site_name+"");}
    			            } },
    			            { title: "Ticket", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.ticket_id != null){return ('<a href="#" data-id="'+row.ticket_id+'" class="ticket-clicker">'+row.ticket_id+'</a>');}
    			            } },
    			            { title: "Status", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.ticket_status != null){return ('<span class="tooltip">' + row.ticket_status + '<span class="tooltiptext">' + row.ticket_status_description + '</span></span>');}
    			            } },
    			            { title: "Claim Week", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.claim_week != null){return (row.claim_week+"");}
    			            } },
    			            { title: "Budget", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.budget != null){return (row.budget.toFixed(2));}
    			            } },
    			            { title: "Direct Labor", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.claimed_weekly_dl_amt != null){return (row.claimed_weekly_dl_amt.toFixed(2));}
    			            } },
    			            { title: "+ Expenses", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.claimed_weekly_dl_exp != null){return (row.claimed_weekly_dl_exp);}
    			            } },
    			            { title: "= Total", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.claimed_weekly_dl_total != null){return (row.claimed_weekly_dl_total.toFixed(2));}
    			            } },
    			            { title: "Total Volume (PPC)" , "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.total_volume != null){return (row.total_volume.toFixed(2));}
    			            } },
    			            { title: "Claimed DL Volume" , "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.claimed_volume != null){return (row.claimed_volume.toFixed(2));}
    			            } },
    			            { title: "Claimed Passthru Volume" , "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.passthru_volume != null){return (row.passthru_volume.toFixed(2));}
    			            } },
    			            { title: "Total Volume Claimed" , "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.claimed_volume_total != null){return (row.claimed_volume_total.toFixed(2));}
    			            } },
    			            { title: "Volume Remaining", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.volume_remaining != null){return ( row.volume_remaining.toFixed(2));}
    			            } },
    			            { title: "Billed Amount",  "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.billed_amount != null){return ( row.billed_amount.toFixed(2));}
    			            } },			            
    			            { title: "Diff CLM/BLD",  "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.claimed_vs_billed != null){return ( row.claimed_vs_billed.toFixed(2));}
    			            } },
    			            { 
    			            	title: "<bean:message key="field.label.action" />",
    			            	searchable:false,
    			            	data: function ( row, type, set ) {{
    				            	var $claim = '<a href="claimDetailLookup.html?id='+row.ticket_id+'" class="claimAction" data-id="'+row.ticket_id+'"><webthing:invoiceIcon styleClass="green">Claim Detail</webthing:invoiceIcon></a>';    				            	
    			            		return "<ansi:hasPermission permissionRequired='CLAIMS_READ'>"+$claim +"</ansi:hasPermission>";
    			            	}}
    			            }],
    			            "initComplete": BUDGETCONTROL.initComplete,
    			            "drawCallback": BUDGETCONTROL.drawCallback
    			    } );
            		//new $.fn.dataTable.FixedColumns( dataTable );
            	},
            	
            	
        		
            	initComplete : function() {
            		var myTable = this;
	            	//BUDGETCONTROL.doFunctionBinding();
	            	LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#displayTable", BUDGETCONTROL.createTable);
	            	if ( BUDGETCONTROL.ticketFilter != null &&  BUDGETCONTROL.ticketFilter !='' ) {
	            		console.log("Seting filter");
	            		LOOKUPUTILS.setFilterValue("#filter-container", 2, BUDGETCONTROL.ticketFilter);
	            		BUDGETCONTROL.ticketFilter='';
	            	}

            	},
	            
	            
	            drawCallback : function(settings, json) {
	            	BUDGETCONTROL.doFunctionBinding();
	            },
            	
	            
	            
            	doFunctionBinding : function () {
					$( ".editAction" ).on( "click", function($clickevent) {
						var $laborId = $(this).attr("data-id");
						BUDGETCONTROL.doGetLabor($laborId);
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
            	}

        	}
      	  	

        	BUDGETCONTROL.init();
        	
        });
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Budget Control</h1>
    	
    	
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

