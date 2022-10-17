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
        <bean:message key="page.label.batchLog" /> <bean:message key="page.label.lookup" />
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
        	;BATCHLOG_LOOKUP = {
        		dataTable : null,
        		
        		init : function() {
        			//BATCHLOG_LOOKUP.makeModals();
        			BATCHLOG_LOOKUP.makeTable();
        			BATCHLOG_LOOKUP.makeClickers();
        		},
        		
        		

        		makeClickers : function() {
        			$('.ScrollTop').click(function() {
        				$('html, body').animate({scrollTop: 0}, 800);
        				return false;
               	    });
        		},
        		
        		
        		makeTable : function(){
            		BATCHLOG_LOOKUP.dataTable = $('#display-table').DataTable( {
            			"aaSorting":		[[1,'desc']],
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
            	            { className: "dt-left", "targets": [1,2,3,4,5] },
            	            { className: "dt-center", "targets": [0] },
            	            { className: "dt-right", "targets": []}
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": "batch/batchLogLookup",
    			        	"type": "GET",
    			        	"data": {}
    			        	},
    			        columns: [
    			            { width:"10%", title: "<bean:message key="rpt.hdr.id" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.batch_log_id != null){return row.batch_log_id+"";}
    			            } },
    			            { width:"15%", title: '<bean:message key="rpt.hdr.start" />', "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.start_time_display != null){return row.start_time_display+"";}
    			            } },
    			            { width:"15%", title: '<bean:message key="rpt.hdr.end" />', "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.end_time_display != null){return row.end_time_display;}
    			            } },
    			            { width:"20%", title: '<bean:message key="rpt.hdr.type" />', "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.batch_type != null){return row.batch_type;}
    			            } },
    			            { width:"20%", title: '<bean:message key="rpt.hdr.detail" />', "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.batch_type_detail != null){return row.batch_type_detail;}
    			            } },
    			            { width:"20%", title: '<bean:message key="rpt.hdr.status" />' , "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {    			            	
    			            	if(row.status != null){return row.status;}
    			            } }
    			            //{ width:"8%", title: "PPC", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            //	if(row.price_per_cleaning != null){return (row.price_per_cleaning.toFixed(2)+"");}
    			            //} },
    			            //{ width:"8%", title: "<bean:message key="field.label.jobId" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            //	if(row.view_job_id != null){return '<ansi:hasPermission permissionRequired="QUOTE_READ"><a href="jobMaintenance.html?id='+ row.view_job_id +'" class="jobLink"></ansi:hasPermission>'+row.view_job_id+'<ansi:hasPermission permissionRequired="QUOTE_READ"></a></ansi:hasPermission>';}
    			            //} },
    			            //{ width:"7%", title: "Washer",  "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            //	if(row.washer != null){return row.washer;}
    			            //} },
    			            //{ width:"5%", title: "<bean:message key="field.label.action" />",  data: function ( row, type, set ) {
    			            //	var $assign ='<ansi:hasPermission permissionRequired="TICKET_WRITE"><a href="ticketAssignment.html?ticketId='+row.view_ticket_id+'&divisionId='+row.division_id+'"><webthing:assign styleClass="orange">Assign Ticket</webthing:assign></a></ansi:hasPermission>';
    			            //	var $claimTkt = "";
    			            //	var $claimWasher = "";
    			            	
    			            //	return $assign + " " + $claimTkt + " " + $claimWasher;
    			            //} }
    			            ],
    			            "initComplete": function(settings, json) {
    			            	console.log("initComplete");
    			            	//console.log(json);
    			            	//doFunctionBinding();
    			            	var myTable = this;
    			            	LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#display-table", BATCHLOG_LOOKUP.makeTable);
    			            },
    			            "drawCallback": function( settings ) {    			            	
    			            	//BATCHLOG_LOOKUP.doFunctionBinding();
    			            }
    			    } );
            	},
            	
        	};
        	
        	BATCHLOG_LOOKUP.init();

        });
        		
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1><bean:message key="page.label.batchLog" /> <bean:message key="page.label.lookup" /></h1> 

	 	<webthing:lookupFilter filterContainer="filter-container" />
		<div id="table-container">
		 	<table id="display-table" class="display" cellspacing="0" style="table-layout: fixed; font-size:9pt;min-width:1300px; max-width:1300px;width:1300px;">
		        <thead></thead>
		        <tbody></tbody>
		        <tfoot></tfoot>
		    </table>
	    </div>
	    
	    <webthing:scrolltop />	    

    </tiles:put>
		
</tiles:insert>

