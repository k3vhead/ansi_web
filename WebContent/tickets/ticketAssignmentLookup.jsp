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
        Ticket Assignment <bean:message key="menu.label.lookup" />
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
       	<link rel="stylesheet" href="css/lookup.css" />
    	<link rel="stylesheet" href="css/ticket.css" />
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/addressUtils.js"></script>
    	<script type="text/javascript" src="js/lookup.js"></script> 
    	<script type="text/javascript" src="js/ticket.js"></script> 
    
        <style type="text/css">
	        #division-description {
        		text-align:center;
        		font-weight:bold;
        	}
        	#table-container {
        		width:100%;
        	}
			#filter-container {
        		width:402px;
        		float:right;
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
        	;TICKETASSIGNMENTLOOKUP = {
        		dataTable : null,
        		//divisionMap : {},
        		//division : null,
        		
        		divisionId : '<c:out value="${ANSI_DIVISION_ID}" />',
				jobId : '<c:out value="${ANSI_JOB_ID}" />',
				ticketId : '<c:out value="${ANSI_TICKET_ID}" />',
				washerId : '<c:out value="${ANSI_WASHER_ID}" />',

        		
        		
        		init : function() {
        			TICKETASSIGNMENTLOOKUP.makeTable();
        			TICKETASSIGNMENTLOOKUP.makeClickers();
               		TICKETUTILS.makeTicketViewModal("#ticket-modal")
               		//ANSI_UTILS.makeDivisionList(TICKETASSIGNMENTLOOKUP.makeDivListSuccess, TICKETASSIGNMENTLOOKUP.makeDivListFailure);               		
        		},
        		
        		
        		
        		doFunctionBinding : function() {
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
        		
        		
        		makeDivListFailure : function($data) {
					$("#globalMsg").html("Failed to load Division List. Contact Support");
	       		},

	       		
	       		
        		makeDivListSuccess : function($data) {
   			 		console.log("Div List Success");
   			 		var $select = $("select[name='divisionId']");
					$('option', $select).remove();

					$select.append(new Option("",""));
					$.each($data.data.divisionList, function(index, val) {
    					$select.append(new Option(val.divisionNbr + "-" + val.divisionCode, val.divisionId));
    					TICKETASSIGNMENTLOOKUP.divisionMap[val.divisionId] = val;
					});
					
					$select.change(function() {
						TICKETASSIGNMENTLOOKUP.processDivChange();						
					});
					
					if ( TICKETASSIGNMENTLOOKUP.divisionId != '' ) {
						console.log("Switching to div: " + TICKETASSIGNMENTLOOKUP.divisionId);
						$($select).val(TICKETASSIGNMENTLOOKUP.divisionId);
						TICKETASSIGNMENTLOOKUP.processDivChange();						
					}
        		},
        		
        		
        		
        		makeTable : function(){
            		TICKETASSIGNMENTLOOKUP.dataTable = $('#ticket-assignment-table').DataTable( {
            			"aaSorting":		[[0,'desc']],
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
            	        "pageLength":		50,
            	        lengthMenu: [
            	        	[ 10, 50, 100, 500, 1000 ],
            	            [ '10 rows', '50 rows', '100 rows', '500 rows', '1000 rows' ]
            	        ],
            	        buttons: [
            	        	'pageLength','copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print',{extend: 'colvis',	label: function () {doFunctionBinding();}}
            	        ],
            	        
            	        "columnDefs": [
             	            { "orderable": false, "targets": -1 },
            	            { className: "dt-left", "targets": [2,3,7] },
            	            { className: "dt-center", "targets": [0,1,4,5,8] },
            	            { className: "dt-right", "targets": [6]}
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": "ticket/ticketAssignmentLookup",
    			        	"type": "GET",
    			        	"data": {}
    			        	},
    			        columns: [
    			            { width:"5%", title: "<bean:message key="field.label.ticketId" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.view_ticket_id != null){return ('<a href="#" data-id="'+row.view_ticket_id+'" class="ticket-clicker">'+row.view_ticket_id+'</a>');}
    			            } },
    			            { width:"5%", title: "Div", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.div != null){return row.div;}
    			            } },
    			            { width:"10%", title: "Job Site", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.job_site_name != null){return row.job_site_name;}
    			            } },
    			            { width:"10%", title: "Address", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.job_site_address != null){return (row.job_site_address+"");}
    			            } },
    			            { width:"4%", title: "Start Date", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.view_start_date != null){return (row.view_start_date);}
    			            } },
    			            { width:"8%", title: "Freq" , "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {    			            	
    			            	if(row.job_frequency != null){return '<span class="tooltip">' + row.job_frequency+'<span class="tooltiptext">'+row.frequency_desc+'</span></span>';}
    			            } },
    			            { width:"8%", title: "PPC", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.price_per_cleaning != null){return (row.price_per_cleaning.toFixed(2)+"");}
    			            } },
    			            { width:"7%", title: "Washer",  "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.last_name != null){return (row.last_name+", "+row.first_name);}
    			            } },
    			            { width:"5%", title: "<bean:message key="field.label.action" />",  data: function ( row, type, set ) {	
    			            	$actionData = "claim links"
    			            	return "";
    			            } }],
    			            "initComplete": function(settings, json) {
    			            	$("#table-container").show();
    			            	//console.log(json);
    			            	//doFunctionBinding();
    			            	var myTable = this;
    			            	LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#ticket-assignment-table", TICKETASSIGNMENTLOOKUP.makeTable);
    			            },
    			            "drawCallback": function( settings ) {
    			            	TICKETASSIGNMENTLOOKUP.doFunctionBinding();
    			            }
    			    } );
            	},
            	
            	
            	
            	
            	//processDivChange : function() {
            	//	var $selectedDiv = TICKETASSIGNMENTLOOKUP.divisionMap[$("select[name='divisionId']").val()];
				//	$("#division-description").html( $selectedDiv.description + " (" +  $selectedDiv.divisionNbr + "-" + $selectedDiv.divisionCode + ")");
				//	if ( TICKETASSIGNMENTLOOKUP.dataTable != null ) {
				//		TICKETASSIGNMENTLOOKUP.dataTable.destroy();
				//	}
				//	// we do it this way so the load methods can be called later without knowing the division
				//	TICKETASSIGNMENTLOOKUP.division = $selectedDiv;
				//	TICKETASSIGNMENTLOOKUP.makeTable();
            	//}
        	};
        	
        	TICKETASSIGNMENTLOOKUP.init();
        	
        	
			

        	

        	        	
        			
        			
            
            
				
				
			function doEdit($clickevent) {
				var $rowid = $clickevent.currentTarget.attributes['data-id'].value;
					var $url = 'ticketTable/' + $rowid;
					//console.log("YOU PASSED ROW ID:" + $rowid);
					var jqxhr = $.ajax({
						type: 'GET',
						url: $url,
						success: function($data) {
							//console.log($data);
							
			        		$("#ticketId").val(($data.data.codeList[0]).ticketId);
			        		$("#ticketStatus").val(($data.data.codeList[0]).ticketStatus);
			        		$("#divisionNbr").val(($data.data.codeList[0]).divisionNbr);
			        		$("#billToName").val(($data.data.codeList[0]).billToName);
			        		$("#jobSiteName").val(($data.data.codeList[0]).jobSiteName);
			        		$("#jobSiteAddress").val(($data.data.codeList[0]).jobSiteAddress);
			        		$("#startDate").val(($data.data.codeList[0]).startDate);
			        		$("#jobFreq").val(($data.data.codeList[0]).jobFreq);
			        		$("#pricePerCleaning").val(($data.data.codeList[0]).pricePerCleaning);
			        		$("#jobNbr").val(($data.data.codeList[0]).jobNbr);
			        		$("#jobId").val(($data.data.codeList[0]).jobId);
			        		$("#serviceDescription").val(($data.data.codeList[0]).serviceDescription);
			        		$("#processDate").val(($data.data.codeList[0]).processDate);
			        		$("#invoiceId").val(($data.data.codeList[0]).invoiceId);
			        		$("#amountDue").val(($data.data.codeList[0]).amountDue);
			        		
			        		$("#tId").val(($data.data.codeList[0]).ticketId);
			        		$("#updateOrAdd").val("update");
			        		$("#addTicketTableForm").dialog( "open" );
						},
						statusCode: {
							403: function($data) {
								$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
							} 
						},
						dataType: 'json'
					});
				//console.log("Edit Button Clicked: " + $rowid);
			}
				
				
				
			function doPrint($clickevent) {
				var $ticketId = $clickevent.currentTarget.attributes['data-id'].value;
				console.debug("ROWID: " + $ticketId);
				var a = document.createElement('a');
                var linkText = document.createTextNode("Download");
                a.appendChild(linkText);
                a.title = "Download";
                a.href = "ticketPrint/" + $ticketId;
                a.target = "_new";   // open in a new window
                document.body.appendChild(a);
                a.click();				
			}
        });
        		
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Ticket Assignment <bean:message key="menu.label.lookup" /></h1> 

    	
    	
    	<c:if test="${not empty ANSI_JOB_ID}">
    		<span class="orange"><bean:message key="field.label.jobFilter" />: <c:out value="${ANSI_JOB_ID}" /></span><br />
    	</c:if>
    	<c:if test="${not empty ANSI_DIVISION_ID}">
    		<span class="orange"><bean:message key="field.label.divisionFilter" />: <c:out value="${ANSI_DIVISION_ID}" /></span><br />
    	</c:if>
    	  	
    	  	
 	<webthing:lookupFilter filterContainer="filter-container" />
	<div id="table-container">
	 	<table id="ticket-assignment-table" class="display" cellspacing="0" style="table-layout: fixed; font-size:9pt;min-width:1300px; max-width:1300px;width:1300px;">
	        <thead></thead>
	        <tbody></tbody>
	        <tfoot></tfoot>
	    </table>
    </div>
    
    <webthing:scrolltop />

    <webthing:ticketModal ticketContainer="ticket-modal" />

    </tiles:put>
		
</tiles:insert>

