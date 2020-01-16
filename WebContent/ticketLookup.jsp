<%@ page contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib uri="WEB-INF/c.tld" prefix="c" %>
<%@ taglib uri="WEB-INF/sql.tld" prefix="sql" %>
<%@ taglib uri="WEB-INF/fmt.tld" prefix="fmt" %>
<%@ taglib uri="WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="WEB-INF/struts-bean.tld"  prefix="bean"  %>
<%@ taglib uri="WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing" %>
<%@ taglib uri="WEB-INF/theTagThing.tld" prefix="ansi" %>




<tiles:insert page="layout.jsp" flush="true">

    <tiles:put name="title" type="string">
        <bean:message key="page.label.ticket" /> <bean:message key="menu.label.lookup" />
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
       	<link rel="stylesheet" href="css/lookup.css" />
    	<link rel="stylesheet" href="css/ticket.css" />
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/addressUtils.js"></script>
    	<script type="text/javascript" src="js/lookup.js"></script> 
    	<script type="text/javascript" src="js/ticket.js"></script> 
    
        <style type="text/css">
			#displayTable {
				width:100%;
			}
			#addFormDiv {
				display:none;
				background-color:#FFFFFF;
				color:#000000;
				width:400px;
				padding:15px;
			}
			#filter-container {
        		width:402px;
        		float:right;
        	}
        	
			.prettyWideButton {
				height:30px;
				min-height:30px;
			}
			select	{
				width:80px !important;
				max-width:80px !important;
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
        	
	        ;TICKETLOOKUP = {
	 			   dataTable : null,
	    	   	
				
	    		init : function() {	
    				$.each($('input'), function () {
    			        $(this).css("height","20px");
    			        $(this).css("max-height", "20px");
    			    });    				
    				TICKETLOOKUP.createTable();    				
               		TICKETUTILS.makeTicketViewModal("#ticket-modal");
			    },
			   	

        	
			    
        	
        	createTable : function () {
				var $jobId = '<c:out value="${ANSI_JOB_ID}" />';
				var $divisionId = '<c:out value="${ANSI_DIVISION_ID}" />';
				var $startDate = '<c:out value="${ANSI_TICKET_LOOKUP_START_DATE}" />';
				var $statusFilter = '<c:out value="${ANSI_TICKET_LOOKUP_STATUS}" />';

        		var dataTable = $('#ticketTable').DataTable( {
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
        	        lengthMenu: [
        	        	[ 10, 50, 100, 500, 1000 ],
        	            [ '10 rows', '50 rows', '100 rows', '500 rows', '1000 rows' ]
        	        ],
        	        buttons: [
        	        	'pageLength','copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print',{extend: 'colvis',	label: function () {doFunctionBinding();}}
        	        ],
        	        
        	        "columnDefs": [
         	            { "orderable": false, "targets": -1 },
        	            { className: "dt-head-left", "targets": [4,5,6,12] },
        	            { className: "dt-body-center", "targets": [0,1,2,3,7,8,10,11,13,14] },
        	            { className: "dt-right", "targets": [9,15]}
        	         ],
        	        "paging": true,
			        "ajax": {
			        	"url": "ticketTable",
			        	"type": "GET",
			        	"data": {"jobId":$jobId,"divisionId":$divisionId,"startDate":$startDate,"status":$statusFilter}
			        	},
			        columns: [
			            { width:"5%", title: "<bean:message key="field.label.ticketId" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
			            	if(row.ticket_id != null){return ('<a href="#" data-id="'+row.ticket_id+'" class="ticket-clicker">'+row.ticket_id+'</a>');}
			            } },
			            { width:"5%", title: "<bean:message key="field.label.ticketStatus" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
			            	if(row.view_ticket_status != null){return ('<span class="tooltip">' + row.view_ticket_status +'<span class="tooltiptext">'+row.ticket_status_desc+'</span></span>');}
			            } },
			            { width:"5%", title: "<bean:message key="field.label.ticketType" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
			            	if(row.ticket_type_desc != null){return (row.ticket_type_desc+"");}
			            } },
			            { width:"4%", title: "<bean:message key="field.label.divisionNbr" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
			            	if ( row.tkt_act_div == null || row.tkt_act_div=='-' ) {
			            		div = row.div;
			            	} else {
			            		div = row.tkt_act_div;
			            	}
			            	return div;
			            } },
			            { width:"8%", title: "<bean:message key="field.label.billToName" />" , "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
			            	if(row.bill_to_name != null){return (row.bill_to_name+"");}
			            } },
			            { width:"8%", title: "<bean:message key="field.label.jobSiteName" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
			            	if(row.job_site_name != null){return (row.job_site_name+"");}
			            } },
			            { width:"7%", title: "<bean:message key="field.label.jobSiteAddress" />",  "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
			            	if(row.job_site_address != null){return (row.job_site_address+"");}
			            } },
			            { width:"6%", title: "<bean:message key="field.label.startDate" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
			            	//if(row.start_date != null){return (row.start_date+"");}
			            	//if(row.view_start_date != null){return (row.view_start_date+"");}
			            	if ( row.display_start_date != null ) { return row.display_start_date; }
			            } },
			            { width:"5%", title: "<bean:message key="field.label.jobFrequency" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
			            	if(row.job_frequency != null){return (row.job_frequency+"");}
			            } },
			            { width:"5%", title: "<bean:message key="field.label.pricePerCleaning" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
			            	if(row.price_per_cleaning != null){return (row.price_per_cleaning.toFixed(2)+"");}
			            } },
			            { width:"5%", title: "<bean:message key="field.label.jobNbr" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) { 	
			            	if(row.job_nbr != null){return (row.job_nbr+"");}
			            } },
			            { width:"4%", title: "<bean:message key="field.label.jobId" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
			            	//if(row.jobId != null){return ('<span class="editJob" data-jobid="'+ row.jobId +'">'+row.jobId+"</span>");} 
			            	if(row.view_job_id != null){return ('<ansi:hasPermission permissionRequired="QUOTE_READ"><a href="jobMaintenance.html?id='+ row.view_job_id +'" class="jobLink"></ansi:hasPermission>'+row.view_job_id+'<ansi:hasPermission permissionRequired="QUOTE_READ"></a></ansi:hasPermission>');}
			            } },
			            { width:"16%", title: "<bean:message key="field.label.serviceDescription" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
			            	if(row.service_description != null){return (row.service_description+"");}
			            } },
			            { width:"6%", title: "<bean:message key="field.label.processDate" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) { 	
			            	if(row.process_date != null){return (row.process_date+"");}
			            } },
			            { width:"6%", title: "<bean:message key="field.label.invoiceId" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
			            	if(row.invoice_id != null){return (row.invoice_id+"");} 
			            } },
			            { width:"6%", title: "<bean:message key="field.label.amountDue" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
			            	if(row.amount_due != null){return (row.amount_due.toFixed(2)+"");} 
			            } },
			            { width:"5%", title: "<bean:message key="field.label.action" />",  data: function ( row, type, set ) {	
			            	//console.log(row);
			            	if ( row.ticket_id == null ) {
			            		$actionData = "";
			            	} else {
				            	var $editLink = '<ansi:hasPermission permissionRequired="TICKET_WRITE"><a href="ticketReturn.html?id='+row.ticket_id+'" class="editAction" data-id="'+row.ticket_id+'" ><webthing:edit>Edit</webthing:edit></a></ansi:hasPermission>&nbsp;';
				            	if ( row.ticket_status == 'F' ) {
				            		var $overrideLink = "";
				            	} else {
				            		var $overrideLink = '<ansi:hasPermission permissionRequired="TICKET_OVERRIDE"><a href="ticketOverride.html?id='+row.ticket_id+'" class="overrideAction" data-id="'+row.ticket_id+'"><webthing:override>Override</webthing:override></a></ansi:hasPermission>&nbsp;';
				            	}
		            			var $ticketData = 'data-id="' + row.ticket_id + '"';
								if ( row.view_ticket_status == 'N' ) {
				            		var $printLink = '<ansi:hasPermission permissionRequired="TICKET_WRITE"><i class="print-link fa fa-print" aria-hidden="true" ' + $ticketData + '></i></ansi:hasPermission>'
				            	} else {
				            		var $printLink = '<ansi:hasPermission permissionRequired="TICKET_READ"><i class="print-link fa fa-print" aria-hidden="true" ' + $ticketData + '></i></ansi:hasPermission>'
				            	}
			            		var $claimLink = '';
			            		if ( row.view_ticket_type == 'run' || row.view_ticket_type=='job') {
			            			$claimLink = '<ansi:hasPermission permissionRequired="CLAIMS_WRITE"><a href="budgetControlLookup.html?id='+row.ticket_id+'"><webthing:invoiceIcon styleClass="green">Budget Control</webthing:invoiceIcon></a></ansi:hasPermission>';
			            		}
				            	$actionData = $editLink + $printLink + $overrideLink + $claimLink;
			            	}
			            	return $actionData;
			            } }],
			            "initComplete": function(settings, json) {
			            	//console.log(json);
			            	//doFunctionBinding();
			            	var myTable = this;
			            	LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#ticketTable", TICKETLOOKUP.createTable);
			            },
			            "drawCallback": function( settings ) {
			            	TICKETLOOKUP.doFunctionBinding();
			            }
			    } );
        	},
				
			doFunctionBinding : function () {
			/*	$( ".editAction" ).on( "click", function($clickevent) {
					var $ticketid = $(this).attr("data-id");
					TICKETLOOKUP.doEdit($clickevent);
				});				*/	
				$(".print-link").on( "click", function($clickevent) {
					TICKETLOOKUP.doPrint($clickevent);
				});
				$(".ticket-clicker").on("click", function($clickevent) {
					$clickevent.preventDefault();
					var $ticketId = $(this).attr("data-id");
					TICKETUTILS.doTicketViewModal("#ticket-modal",$ticketId);
					$("#ticket-modal").dialog("open");
				});

			},
				
			/* doEdit : function ($clickevent) {
				var $ticketId = $clickevent.currentTarget.attributes['data-id'].value;
					var $url = 'ticketTable/' + $ticketId;
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
			}, */
				
				
				
			doPrint : function ($clickevent) {
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
			},
			
	        }
			
			TICKETLOOKUP.init();
        });
        		
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1><bean:message key="page.label.ticket" /> <bean:message key="menu.label.lookup" /></h1> 
    	<c:if test="${not empty ANSI_JOB_ID}">
    		<span class="orange"><bean:message key="field.label.jobFilter" />: <c:out value="${ANSI_JOB_ID}" /></span><br />
    	</c:if>
    	<c:if test="${not empty ANSI_DIVISION_ID}">
    		<span class="orange"><bean:message key="field.label.divisionFilter" />: <c:out value="${ANSI_DIVISION_ID}" /></span><br />
    	</c:if>
    	<c:if test="${not empty ANSI_TICKET_LOOKUP_START_DATE}">
    		<span class="orange"><bean:message key="field.label.startDate" />: <c:out value="${ANSI_TICKET_LOOKUP_START_DATE}" /></span><br />
    	</c:if>
    	<c:if test="${not empty ANSI_TICKET_LOOKUP_STATUS}">
    		<span class="orange"><bean:message key="field.label.statusFilter" />: <c:out value="${ANSI_TICKET_LOOKUP_STATUS}" /></span><br />
    	</c:if>
    	  	
    	  	
 	<webthing:lookupFilter filterContainer="filter-container" />

 	<table id="ticketTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:1300px;width:1300px;">
        <thead>
        </thead>
        <tfoot>
        </tfoot>
    </table>
    
    <webthing:scrolltop />

    <webthing:ticketModal ticketContainer="ticket-modal" />

    </tiles:put>
		
</tiles:insert>

