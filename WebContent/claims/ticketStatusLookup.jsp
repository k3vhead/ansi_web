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
    	<script type="text/javascript" src="js/lookup.js"></script> 
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
			
        </style>
        
        <script type="text/javascript">
        
        $(document).ready(function() {
        	;TICKETSTATUS = {
        		datatable : null,
        		
        		init : function() {
        			TICKETSTATUS.createTable();
        			TICKETSTATUS.makeClickers();
        		},
        		
        		
        		
				createTable : function() {
            		var dataTable = $('#displayTable').DataTable( {
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
    			        	
    			            { title: "Div", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.div != null){return (row.div+"");}
    			            } },
    			            { width:"23%", title: "Account", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.job_site_name != null){return (row.job_site_name);}
    			            } },
    			            { title: "Ticket", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.ticket_id != null){return (row.ticket_id+"");}
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
    				            	var $edit = '<a href="#" class="editAction" data-id="'+row.labor_id+'"><webthing:edit>Edit</webthing:edit></a>';
    			            		return "<ansi:hasPermission permissionRequired='CLAIMS_WRITE'>"+$edit+"</ansi:hasPermission>";
    			            	}
    			            	
    			            } }],
    			            "initComplete": function(settings, json) {
    			            	LOOKUPUTILS.makeFilters(this, "#filter-container", "#displayTable");
    			            	TICKETSTATUS.doFunctionBinding();
    			            },
    			            "drawCallback": function( settings ) {
    			            	TICKETSTATUS.doFunctionBinding();
    			            }
    			    } );
            		//new $.fn.dataTable.FixedColumns( dataTable );
            	},
        		
        		
            	
            	doFunctionBinding : function () {
					$( ".editAction" ).on( "click", function($clickevent) {
						var $laborId = $(this).attr("data-id");
						alert("Something useful goes here");
						//TICKETSTATUS.doGetLabor($laborId);
					});
				},
            	
            	
            	makeClickers : function() {
            		$('.ScrollTop').click(function() {
        				$('html, body').animate({scrollTop: 0}, 800);
              	  		return false;
              	    });
            		
            		
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
    
	    <div id="ndl-crud-form">
	    	<table>
	    		<tr>
	    			<td><span class="formLabel">Division</span></td>
	    			<td><select name="divisionId" class="calcPayTrigger"></select></td>
	    			<td><span id="divisionIdErr" class="err"></span></td>
	    		</tr>
	    		<tr>
	    			<td><span class="formLabel">Date</span></td>
	    			<td><input type="text" name="workDate" class="dateField calcPayTrigger" /></td>
	    			<td><span id="workDateErr" class="err"></span></td>
	    		</tr>
	    		<tr>
	    			<td><span class="formLabel">Washer</span></td>
	    			<td><input type="text" name="washerName" class="calcPayTrigger" /><input type="hidden" name="washerId" /></td>
	    			<td><span id="washerIdErr" class="err"></span></td>
	    		</tr>
	    		<tr>
	    			<td><span class="formLabel">Hours</span></td>
	    			<td><input type="text" name="hours" class="calcPayTrigger" /></td>
	    			<td><span id="hoursErr" class="err"></span></td>
	    		</tr>
	    		<tr>
	    			<td><span class="formLabel">Type</span></td>
	    			<td><select  name="hoursType" class="calcPayTrigger"></select></td>
	    			<td><span id="hoursTypeErr" class="err"></span></td>
	    		</tr>
	    		<tr>
	    			<td><span class="formLabel">Calculated Pay</span></td>
	    			<td><span  id="calcPayAmt"></span></td>
	    			<td>&nbsp;</td>
	    		</tr>
	    		<tr>
	    			<td><span class="formLabel">Actual Pay</span></td>
	    			<td><input type="text" name="actPayoutAmt" /></td>
	    			<td><span id="actPayAmtErr" class="err"></span></td>
	    		</tr>
	    		<tr>
	    			<td><span class="formLabel">Notes</span></td>
	    			<td><input type="text" name="notes" /></td>
	    			<td><span id="notesErr" class="err"></span></td>
	    		</tr>
	    	</table>
	    </div>
    </tiles:put>
		
</tiles:insert>

