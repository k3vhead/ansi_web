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
		Timesheet Lookup
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
       	<link rel="stylesheet" href="css/lookup.css" />
    	<link rel="stylesheet" href="css/ticket.css" />
    	<link rel="stylesheet" href="css/document.css" />
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/addressUtils.js"></script>
    	<script type="text/javascript" src="js/lookup.js"></script> 
    	<script type="text/javascript" src="js/document.js"></script> 
    
        <style type="text/css">
        	#table-container {
        		width:100%;
        	}
			#filter-container {
        		width:402px;
        		float:right;
        	}			
        	#organization-display {
        		display:none;
        	}
        	#organization-display table {
        		width:100%;
        		border:solid 1px #404040;
        	}
        	#organization-display th {
        		text-align:left;
        	}
        	#organization-edit table {
        		width:100%;
        		border:solid 1px #404040;
        	}
        	#organization-edit th {
        		text-align:left;
        	}
        	.action-link {
        		text-decoration:none;
        	}
			.dataTables_wrapper {
				padding-top:10px;
			}	
			.form-label {
				font-weight:bold;
			}
			.org-status-change {
				display:none;
				cursor:pointer;
			}
			.view-link {
				color:#404040;
			}		
        </style>
        
        <script type="text/javascript">    
        
        $(document).ready(function(){
        	;TIMESHEETLOOKUP = {
        			init : function() {
        				TIMESHEETLOOKUP.makeTimesheetLookup(); 
            		},
            		
            		makeTimesheetLookup : function() {
            			$("#timesheetLookup").DataTable( {
                			"aaSorting":		[[0,'asc'],[1,'asc'],[2,'asc']],
                			"processing": 		true,
                	        "serverSide": 		true,
                	        "autoWidth": 		false,
                	        "deferRender": 		true,
                	        "scrollCollapse": 	true,
                	        "scrollX": 			true,
                	        "pageLength":		50,
                	        rowId: 				'dt_RowId',
                	        destroy : 			true,		// this lets us reinitialize the table
                	        dom: 				'Bfrtip',
                	        "searching": 		true,
                	        "searchDelay":		800,
                	        lengthMenu: [
                	        	[ 10, 50, 100, 500, 1000 ],
                	            [ '10 rows', '50 rows', '100 rows', '500 rows', '1000 rows' ]
                	        ],
                	        buttons: [
                	        		'pageLength',
                	        		'copy', 
                	        		'csv', 
                	        		'excel', 
                	        		{extend: 'pdfHtml5', orientation: 'landscape'}, 
                	        		'print',{extend: 'colvis',	label: function () {doFunctionBinding();$('#timesheetLookup').draw();}},
                	        	],
                	        "columnDefs": [
                 	            { "orderable": true, "targets": -1 },
                 	            { className: "dt-head-center", "targets":[]},
                	            { className: "dt-left", "targets": [0,1,2,3,4,5,6,7,8,9,10] },
                	            { className: "dt-center", "targets": [] },
                	            { className: "dt-right", "targets": []}
                	         ],
                	        "paging": true,
        			        "ajax": {
        			        	"url": "payroll/timesheetLookup",
        			        	"type": "GET",
        			        	"data": {},
        			        	},
        			        columns: [
        			        	{ title: "Division", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'div' },
        			        	{ title: "Week Ending", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'week_ending' },
        			        	{ title: "State", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'state' },
        			        	{ title: "City", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'city' },
        			        	{ title: "Employee Code", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'employee_code' },
        			        	{ title: "Employee Name", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'employee_name' },
        			            { title: "Regular Hours",  width:"10%", searchable:false, data: function ( row, type, set ) { return row.regular_hours.toFixed(2); } },
        			            { title: "Regular Pay",  width:"10%", searchable:false, data: function ( row, type, set ) { return row.regular_pay.toFixed(2); } },
        			            { title: "Expenses",  width:"10%", searchable:false, data: function ( row, type, set ) { return row.expenses.toFixed(2); } },
        			            { title: "OT Hours",  width:"10%", searchable:false, data: function ( row, type, set ) { return row.ot_hours.toFixed(2); } },
        			            { title: "OT Pay",  width:"10%", searchable:false, data: function ( row, type, set ) { return row.ot_pay.toFixed(2); } },
        			            { title: "Vacation Hours",  width:"10%", searchable:false, data: function ( row, type, set ) { return row.vacation_hours.toFixed(2); } },
        			            { title: "Vacation Pay",  width:"10%", searchable:false, data: function ( row, type, set ) { return row.vacation_pay.toFixed(2); } },
        			            { title: "Holiday Hours",  width:"10%", searchable:false, data: function ( row, type, set ) { return row.holiday_hours.toFixed(2); } },
        			            { title: "Holiday Pay",  width:"10%", searchable:false, data: function ( row, type, set ) { return row.holiday_pay.toFixed(2); } },
        			            { title: "Gross Pay",  width:"10%", searchable:false, data: function ( row, type, set ) { return row.gross_pay.toFixed(2); } },
        			            { title: "Expenses Submitted",  width:"10%", searchable:false, data: function ( row, type, set ) { return row.expenses_submitted.toFixed(2); } },
        			            { title: "Expenses Allowed",  width:"10%", searchable:false, data: function ( row, type, set ) { return row.expenses_allowed.toFixed(2); } },
        			            { title: "Volume",  width:"10%", searchable:false, data: function ( row, type, set ) { return row.volume.toFixed(2); } },
        			            { title: "Direct Labor",  width:"10%", searchable:false, data: function ( row, type, set ) { return row.direct_labor.toFixed(2); } },
        			            { title: "Productivity",  width:"10%", searchable:false, data: function ( row, type, set ) { return row.productivity.toFixed(2); } },
        			            ],
        			            "initComplete": function(settings, json) {
        			            	var myTable = this;
        			            	LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#timesheetLookup", TIMESHEETLOOKUP.makeTimesheetLookup);
        			            },
        			            "drawCallback": function( settings ) {
        			            	// nothing to do here
        			            }
        			    } );
            		},
            		
            		
            		
            	
            	
            	
        	};
        	
        	TIMESHEETLOOKUP.init();
        	
        	
        });
        		
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Timesheet Lookup</h1> 

    	<webthing:lookupFilter filterContainer="filter-container" />
		<table id="timesheetLookup"></table>
		
    </tiles:put>
		
</tiles:insert>

