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
        Job Note <bean:message key="menu.label.lookup" />
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
       	<link rel="stylesheet" href="css/lookup.css" />
    	<link rel="stylesheet" href="css/ticket.css" />
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/lookup.js"></script> 
      	<script type="text/javascript" src="js/ticket.js"></script> 
      	<script type="text/javascript" src="js/addressUtils.js"></script>
      	
    	
    
        <style type="text/css">
        	#table-container {
        		width:100%;
        	}
			#filter-container {
        		width:402px;
        		float:right;
        	}			
			#table-container {
				display:none;
			}
			#ticket-modal {
				display:none;
			}
			.showChild {
				cursor:pointer;
			}
			.dataTables_wrapper {
				padding-top:10px;
			}	
			.jobLink {
				color:#000000;
			}
			.label {
				font-weight:bold;
			}
			.ticket-clicker {
				color:#000000;
			}
        </style>
        
        <script type="text/javascript">    
        
        $(document).ready(function(){
        	;JOBNOTELOOKUP = {
        		dataTable : null,
        		
        		init : function() {
        			JOBNOTELOOKUP.makeClickers();
        			TICKETUTILS.makeTicketViewModal("#ticket-modal");
        		},
        		
        		
        		
        		
        		doFunctionBinding : function() {
        			$(".ticket-clicker").off("click");
        			$(".ticket-clicker").on("click", function($clickevent) {
    					$clickevent.preventDefault();
    					var $ticketId = $(this).attr("data-id");
    					TICKETUTILS.doTicketViewModal("#ticket-modal",$ticketId);
    					$("#ticket-modal").dialog("open");
    				});
        			
        			$(".showChild").off("click");
            		$(".showChild").click(function($event) {
            			console.log("row clicked")
            			var tr = $(this).closest('tr');
            			var row = JOBNOTELOOKUP.dataTable.row(tr);
            			
            			if ( row.child.isShown()) {
            				row.child.hide();
            				tr.removeClass('shown');
            			} else {
            				row.child(JOBNOTELOOKUP.makeChildRow(row.data())).show();
            				tr.addClass('shown');
            			}
            		});
    			},
    			

    			
    			
				doSelectChange : function() {
					var $divisionId = $("select[name='divisionId']").val();
					var $startMonth = $("select[name='startMonth']").val();
					if ( $divisionId == null || $divisionId == '' || $startMonth == null || $startMonth == '') {
						$("#table-container").hide();
					} else {
						JOBNOTELOOKUP.makeTable();
					}
				},
    			
    			
    			
    			
    			makeClickers : function() {
    				$('.ScrollTop').click(function() {
    					$('html, body').animate({scrollTop: 0}, 800);
    					return false;
    	       	    });
    				
    				$("select[name='divisionId']").change(function($event) {
    					JOBNOTELOOKUP.doSelectChange()
    				});
    				$("select[name='startMonth']").change(function($event) {
    					JOBNOTELOOKUP.doSelectChange()
    				});
    				$("input[name='goButton']").click(function($event) {
    					JOBNOTELOOKUP.doSelectChange()
    				});
    				$("input[name='notesOnly']").click(function($event) {
    					JOBNOTELOOKUP.doSelectChange()
    				});
    			},
        		
        		
    			
    			makeChildRow : function(row) {
    				var $notes = [];
    				if ( row.om_notes != null && row.om_notes != '' ) {
    					$notes.push('<span class="label">OM Notes:</span> ' + row.om_notes);
    				};
    				if ( row.washer_notes != null && row.Washer_notes != '' ) {
    					$notes.push('<span class="label">Washer Notes:</span> ' + row.washer_notes);
    				};
    				if ( row.billing_notes != null && row.billing_notes != '' ) {
    					$notes.push('<span class="label">Billing Notes:</span> ' + row.billing_notes);
    				};
    				var $noteContent = $notes.join("<br />");
    				
    				return (
    					'<table><tr>' +
    					'<td>&nbsp;</td>' +
    					'<td colspan="12">' + $noteContent + '</td>' +
    					'<tr></table>'
    				);
    			},
        		
    			
        		
        		makeTable : function(){
        			$("#table-container").show();
        			
        			var $divisionId = $("select[name='divisionId']").val();
					var $startMonth = $("select[name='startMonth']").val();
					var $notesOnly = $("input[name='notesOnly']").prop("checked");
					var $outbound = {
						"divisionId":$divisionId,
						"startMonth":$startMonth,
						"notesOnly":$notesOnly,
					};
            		JOBNOTELOOKUP.dataTable = $('#jobnote-lookup-table').DataTable( {
            			"aaSorting":		[[1,'asc']],
            			"destroy":			true,
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
            	            { className: "dt-left", "targets": [2,3,4,12] },
            	            { className: "dt-center", "targets": [0,1,5,6,7,8] },
            	            { className: "dt-right", "targets": [9,10,11]}
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": "jobInfo/jobNoteLookup",
    			        	"type": "GET",
    			        	"data": $outbound
    			        	},
    			        columns: [
    			        	{ width:"3%", className:'showChild',orderable:false,data:null, defaultContent:'<webthing:addNew>Notes</webthing:addNew>'},
    			            { width:"5%", title: "ID", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.ticket_id != null){return ('<a href="#" data-id="'+row.ticket_id+'" class="ticket-clicker">'+row.ticket_id+'</a>');}
    			            } },
    			            { width:"5%", title: "Status", searchable:true, data: "ticket_status"},
    			            { width:"17%", title: "Name", searchable:true, data: "name" },
    			            { width:"17%", title: "Address", searchable:true, data: "address1" },
    			            { width:"17%", title: "City", searchable:true, data: "city" },
    			            { width:"5%", title: "Last Done", searchable:true, data: "last_done"},
    			            { width:"5%", title: "Start Date", searchable:true, data: "start_date" },
    			            { width:"5%", title: "Job", searchable:true, data: "job_nbr"},
    			            { width:"5%", title: "Frequency",  searchable:true, data: "job_frequency" },
    			            { width:"5%", title: "Budget",  searchable:true, data: function ( row, type, set ) { 
    			            	var $returnValue = null;
    			            	if ( row.budget != null ) {
    			            		$returnValue = row.budget.toFixed(2);
    			            	}
    			            	return $returnValue;
    			            } },
    			            { width:"5%", title: "PPC",  searchable:true, data: function ( row, type, set ) { 
    			            	var $returnValue = null;
    			            	if ( row.budget != null ) {
    			            		$returnValue = row.price_per_cleaning.toFixed(2);
    			            	}
    			            	return $returnValue;
    			            } },
    			            { width:"5%", title: "COD",  searchable:true, data: "invoice_style" },
    			            { width:"5%", title: "Job ID",  searchable:true, data: function ( row, type, set ) {	
    			            	if(row.job_id != null){
    			            		return ('<ansi:hasPermission permissionRequired="QUOTE_READ"><a href="jobMaintenance.html?id='+ row.job_id +'" class="jobLink"></ansi:hasPermission>'+row.job_id+'<ansi:hasPermission permissionRequired="QUOTE_READ"></a></ansi:hasPermission>');
    			            	}
    			            } },
    			            ],
    			            "initComplete": function(settings, json) {
    			            	console.log("initComplete");
    			            	//console.log(json);
    			            	//doFunctionBinding();
    			            	var myTable = this;
    			            	LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#jobnote-lookup-table", JOBNOTELOOKUP.makeTable);
    			            },
    			            "drawCallback": function( settings ) {    			            	
    			            	console.log("drawCallback");
    			            	//$("#searching-modal").dialog("close");
    			            	JOBNOTELOOKUP.doFunctionBinding();
    			            	
    			            	
    			            }
    			    } );
            		
            		
            		
            		
            	},
            	
            	
            	
            	
        	};
        	
        	JOBNOTELOOKUP.init();
        	
        	
        });
        		
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Job Note <bean:message key="menu.label.lookup" /></h1> 

    	<select name="divisionId">
    	<option value=""></option>
    	<ansi:divisionSelect format="SELECT" />
    	</select>
    	<select name="startMonth">
    	<option value=""></option>
    	<webthing:monthSelect />
    	</select>
    	<input type="button" value="Go" name="goButton" /><br />
    	<input type="checkbox" name="notesOnly" /><span class="label">Notes Only</span>
    	
    	
	 	<webthing:lookupFilter filterContainer="filter-container" />
	 	<div id="table-container">
		 	<table id="jobnote-lookup-table"> 
		        <thead></thead>
		        <tbody></tbody>
		        <tfoot></tfoot>
		    </table>
	    </div>
	    
	    <webthing:scrolltop />
	    
	    <webthing:ticketModal ticketContainer="ticket-modal" />
	    
    </tiles:put>
		
</tiles:insert>

