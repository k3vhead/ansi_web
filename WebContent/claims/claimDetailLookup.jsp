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
        Claim Detail
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
			#searching-modal {
				display:none;
			}
			#ticket-modal {
				display:none;	
			}
			
			.prettyWideButton {
				height:30px;
				min-height:30px;
			}
			.ticket-clicker {
				color:#000000;
			}
        </style>
        
        <script type="text/javascript">
        
        $(document).ready(function() {
        	;CLAIMDETAIL = {
        		datatable : null,
        		ticketFilter : '<c:out value="${CLAIM_DETAIL_TICKET_FILTER}" />',
        		
        		init : function() {
        			CLAIMDETAIL.createTable();
        			CLAIMDETAIL.makeClickers();
        			CLAIMDETAIL.makeModals();
        		},
        		
        		
        		
				createTable : function() {
        			var $url = "claims/claimDetailLookup";
        			//if ( CLAIMDETAIL.ticketFilter != '' && CLAIMDETAIL.ticketFilter != null) {
        			//	$url = $url + "/" + CLAIMDETAIL.ticketFilter;
        			//}
        				
            		CLAIMDETAIL.dataTable = $('#displayTable').DataTable( {
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
            	            { className: "dt-left", "targets": [3,6] },
            	            { className: "dt-center", "targets": [0,1,2,4,5,19] },
            	            { className: "dt-right", "targets": [7,8,9,10,11,12,13,14,15,16,17,18]}
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
    			            { title: "Claim Week", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.claim_week != null){return (row.claim_week+"");}
    			            } },
    			            { title: "Work Date", "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "MM/dd/YYYY", data: function ( row, type, set ) {	
    			            	if(row.work_date != null){return (row.work_date+"");}
    			            } },
    			            { width:"12%", title: "Account", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.job_site_name != null){return (row.job_site_name+"");}
    			            } },
    			            { title: "Ticket", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.ticket_id != null){return ('<a href="#" data-id="'+row.ticket_id+'" class="ticket-clicker">'+row.ticket_id+'</a>');}
    			            } },
    			            { title: "Status", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.ticket_status != null){return ('<span class="tooltip">' + row.ticket_status + '<span class="tooltiptext">' + row.ticket_status_description + '</span></span>');}
    			            } },
    			            { width:"8%", title: "Washer", "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "Last Name, First Name", data: function ( row, type, set ) {	
    			            	if(row.washer_name != null){return (row.washer_name+"");}
    			            } },
    			            { title: "Total Volume" , "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
    			            	if(row.total_volume != null){return (row.total_volume.toFixed(2));}
    			            } },
    			            { title: "Budget" , "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
    			            	if(row.budget != null){return (row.budget.toFixed(2));}
    			            } },
    			            { title: "Direct Labor", "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
    			            	if(row.ticket_claim_dl_amt != null){return (row.ticket_claim_dl_amt.toFixed(2));}
    			            } },
    			            { title: "+ Expenses", "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "#", data: function ( row, type, set ) {
    			            	if(row.ticket_claim_dl_exp != null){return (row.ticket_claim_dl_exp);}
    			            } },
    			            { title: "= Total", "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
    			            	if(row.ticket_claim_dl_total != null){return (row.ticket_claim_dl_total.toFixed(2));}
    			            } },
    			            { title: "Hours", "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
    			            	if(row.ticket_claim_dl_hours != null){return (row.ticket_claim_dl_hours.toFixed(2));}
    			            } },
    			            { title: "DL Volume Claimed" , "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
    			            	if(row.claimed_volume != null){return (row.claimed_volume.toFixed(2));}
    			            } },
    			            { title: "Passthru Volume Claimed" , "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
    			            	if(row.passthru_volume != null){return (row.passthru_volume.toFixed(2));}
    			            } },
    			            { title: "Total Volume Claimed" , "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
    			            	if(row.claimed_volume_total != null){return (row.claimed_volume_total.toFixed(2));}
    			            } },
    			            { title: "Unclaimed", "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
    			            	if(row.volume_remaining != null){return ( row.volume_remaining.toFixed(2));}
    			            } },
    			            { title: "Total DL Claimed", "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
    			            	if(row.claimed_dl_total != null){return ( row.claimed_dl_total.toFixed(2));}
    			            } },
    			            { title: "DL Remaining", "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
    			            	if(row.dl_remaining != null){return ( row.dl_remaining.toFixed(2));}
    			            } },
    			            { title: "<bean:message key="field.label.action" />",  searchable:false, data: function ( row, type, set ) {	
    			            	{
    				            	var $claim = '<a href="claimEntry.html?id='+row.ticket_id+'" class="claimAction" data-id="'+row.ticket_id+'"><webthing:invoiceIcon styleClass="green">Claim Entry</webthing:invoiceIcon></a>';
    			            		return "<ansi:hasPermission permissionRequired='CLAIMS_WRITE'>"+$claim+"</ansi:hasPermission>";
    			            	}
    			            	
    			            } }],
    			            "initComplete": function(settings, json) {
    			            	console.log("initComplete");
    			            	LOOKUPUTILS.makeFilters(this, "#filter-container", "#displayTable", CLAIMDETAIL.createTable);
    			            	if ( CLAIMDETAIL.ticketFilter != null &&  CLAIMDETAIL.ticketFilter !='' ) {
    			            		console.log("Setting filter");
    			            		$("#searching-modal").dialog("open");
    			            		LOOKUPUTILS.setFilterValue("#filter-container", 4, CLAIMDETAIL.ticketFilter); //set value in filters
    			            		setTimeout(function() {
    			            			console.log("filtering for : " + CLAIMDETAIL.ticketFilter);
    			            			var dataTable = $('#displayTable').DataTable();
    			        				myColumn = dataTable.columns(4);
    			       					myColumn.search(CLAIMDETAIL.ticketFilter).draw();
    			       					CLAIMDETAIL.ticketFilter = null;
    			            		},100)
    			            	}
    			            },
    			            "drawCallback": function( settings ) {
    			            	console.log("drawCallback");
    			            	$("#searching-modal").dialog("close");
    			            	CLAIMDETAIL.doFunctionBinding();
    			            }
    			    } );
            		//new $.fn.dataTable.FixedColumns( dataTable );
            	},
        		
        		
            	
            	doFunctionBinding : function () {
					$( ".editAction" ).on( "click", function($clickevent) {
						var $laborId = $(this).attr("data-id");
						alert("Some sort of action needs to go here");
						//CLAIMDETAIL.doGetLabor($laborId);
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
            		
            		
            		$("#searching-modal" ).dialog({
						autoOpen: false,
						height: "auto",
						width: 300,
						modal: true,
						closeOnEscape:false,
						title:"Searching",
						open: function(event, ui) {
							$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
						}
					});
            	},
            	
        	}
      	  	

        	CLAIMDETAIL.init();
        	
        });
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Claim Detail</h1>

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
    	
    	<div id="searching-modal">
    		<webthing:thinking style="width:100%" />
    	</div>
    </tiles:put>
		
</tiles:insert>


