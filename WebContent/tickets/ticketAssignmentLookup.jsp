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
        	#searching-modal {
        		display:none;
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
        		
        		divisionFilter : '<c:out value="${ANSI_DIVISION_ID}" />',	// col 1
				jobFilter : '<c:out value="${ANSI_JOB_ID}" />', 			// col 7
				ticketFilter : '<c:out value="${ANSI_TICKET_ID}" />',   	//col 0
				washerFilter : '<c:out value="${ANSI_WASHER_ID}" />',		//col 9

        		
        		
        		init : function() {
        			TICKETASSIGNMENTLOOKUP.makeModals();
        			TICKETASSIGNMENTLOOKUP.makeTable();
        			TICKETASSIGNMENTLOOKUP.makeClickers();
        		},
        		
        		
        		
        		doFunctionBinding : function() {
    				$(".ticket-clicker").on("click", function($clickevent) {
    					$clickevent.preventDefault();
    					var $ticketId = $(this).attr("data-id");
    					TICKETUTILS.doTicketViewModal("#ticket-modal",$ticketId);
    					$("#ticket-modal").dialog("open");
    				});

    			},
    			
    			
    			
    			doTableFilter : function($colNbr, $filterValue) {
	            	if ( $filterValue != null &&  $filterValue !='' ) {
	            		console.log("Setting  ticketfilter");
	        			console.log("filtering for : " + $filterValue);
	            		LOOKUPUTILS.setFilterValue("#filter-container", $colNbr, $filterValue); //set value in filters
	        			var dataTable = $('#ticket-assignment-table').DataTable();
	    				myColumn = dataTable.columns($colNbr);
	   					myColumn.search($filterValue).draw();
	            	}
    			},
    			
    			
    			
    			isFiltered : function() {
    				var $isFiltered = false;
    				
    				if (TICKETASSIGNMENTLOOKUP.divisionFilter != null && TICKETASSIGNMENTLOOKUP.divisionFilter != '') {
    					$isFiltered = true;
    				} else if (TICKETASSIGNMENTLOOKUP.jobFilter != null && TICKETASSIGNMENTLOOKUP.jobFilter != '') {
    					$isFiltered = true;
    				} else if (TICKETASSIGNMENTLOOKUP.ticketFilter != null && TICKETASSIGNMENTLOOKUP.ticketFilter != '') {
    					$isFiltered = true;
    				} else if (TICKETASSIGNMENTLOOKUP.washerFilter != null && TICKETASSIGNMENTLOOKUP.washerFilter != '') {
    					$isFiltered = true;
    				}
    				
    				return $isFiltered;
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
						height: 190,
						width: 300,
						modal: true,
						closeOnEscape:false,
						title:"Searching",
						open: function(event, ui) {
							$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
						}
					});	
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
            	            { className: "dt-left", "targets": [2,3,8] },
            	            { className: "dt-center", "targets": [0,1,4,5,7,9] },
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
    			            { width:"4%", title: "Start Date", "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "YYYY-MM-dd", data: function ( row, type, set ) {
    			            	if(row.view_start_date != null){return (row.view_start_date);}
    			            } },
    			            { width:"8%", title: "Freq" , "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {    			            	
    			            	if(row.job_frequency != null){return '<span class="tooltip">' + row.job_frequency+'<span class="tooltiptext">'+row.frequency_desc+'</span></span>';}
    			            } },
    			            { width:"8%", title: "PPC", "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
    			            	if(row.price_per_cleaning != null){return (row.price_per_cleaning.toFixed(2)+"");}
    			            } },
    			            { width:"8%", title: "<bean:message key="field.label.jobId" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.view_job_id != null){return '<ansi:hasPermission permissionRequired="QUOTE_READ"><a href="jobMaintenance.html?id='+ row.view_job_id +'" class="jobLink"></ansi:hasPermission>'+row.view_job_id+'<ansi:hasPermission permissionRequired="QUOTE_READ"></a></ansi:hasPermission>';}
    			            } },
    			            { width:"7%", title: "Washer",  "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "Last, First Name", data: function ( row, type, set ) {
    			            	if(row.washer != null){return row.washer;}
    			            } },
    			            { width:"5%", title: "<bean:message key="field.label.action" />",  data: function ( row, type, set ) {
    			            	var $assign ='<ansi:hasPermission permissionRequired="TICKET_WRITE"><a href="ticketAssignment.html?ticketId='+row.view_ticket_id+'&divisionId='+row.division_id+'"><webthing:assign styleClass="orange">Assign Ticket</webthing:assign></a></ansi:hasPermission>';
    			            	var $claimTkt = "";
    			            	var $claimWasher = "";
    			            	
    			            	return $assign + " " + $claimTkt + " " + $claimWasher;
    			            } }],
    			            "initComplete": function(settings, json) {
    			            	console.log("initComplete");
    			            	//console.log(json);
    			            	//doFunctionBinding();
    			            	var myTable = this;
    			            	LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#ticket-assignment-table", TICKETASSIGNMENTLOOKUP.makeTable);
    			            	
			            		setTimeout(function() {
			            			if ( TICKETASSIGNMENTLOOKUP.isFiltered()) {
			            				$("#searching-modal").dialog("open");
	    			            		TICKETASSIGNMENTLOOKUP.doTableFilter(0, TICKETASSIGNMENTLOOKUP.ticketFilter);
	    			            		TICKETASSIGNMENTLOOKUP.doTableFilter(1, TICKETASSIGNMENTLOOKUP.divisionFilter);
	    			            		TICKETASSIGNMENTLOOKUP.doTableFilter(7, TICKETASSIGNMENTLOOKUP.jobFilter);
	    			            		TICKETASSIGNMENTLOOKUP.doTableFilter(8, TICKETASSIGNMENTLOOKUP.washerFilter);
	    			            		//clear filters so we don't reuse them 
	    			            		TICKETASSIGNMENTLOOKUP.ticketFilter = null;
	    			            		TICKETASSIGNMENTLOOKUP.divisionFilter = null;
	    			            		TICKETASSIGNMENTLOOKUP.jobFilter = null;
	    			            		TICKETASSIGNMENTLOOKUP.washerFilter = null;
			            			}
			            		},100)
    			            },
    			            "drawCallback": function( settings ) {    			            	
    			            	console.log("drawCallback");
    			            	$("#searching-modal").dialog("close");
    			            	TICKETASSIGNMENTLOOKUP.doFunctionBinding();
    			            }
    			    } );
            	},
            	
            	
            	
            	
            	
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

    	
		<%--    	
    	<c:if test="${not empty ANSI_JOB_ID}">
    		<span class="orange"><bean:message key="field.label.jobFilter" />: <c:out value="${ANSI_JOB_ID}" /></span><br />
    	</c:if>
    	<c:if test="${not empty ANSI_DIVISION_ID}">
    		<span class="orange"><bean:message key="field.label.divisionFilter" />: <c:out value="${ANSI_DIVISION_ID}" /></span><br />
    	</c:if>
    	 --%>
    	  	
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
	    
	    <div id="searching-modal">
    		<webthing:thinking style="width:100%" />
    	</div>

    </tiles:put>
		
</tiles:insert>

