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
        <bean:message key="page.label.batchLog" />
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
       	<link rel="stylesheet" href="css/lookup.css" />
    	<link rel="stylesheet" href="css/ticket.css" />
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/addressUtils.js"></script>
    	<script type="text/javascript" src="js/lookup.js"></script> 
    
        <style type="text/css">
        	#table-container {
        		width:100%;
        	}
			#filter-container {
        		width:402px;
        		float:right;
        	}			
        	#jobtag-crud-modal {
        		display:none;
        	}
        	#jobtag-delete-modal {
        		display:none;
        		text-align:center;
        	}
			.dataTables_wrapper {
				padding-top:10px;
			}	
			.form-label {
				font-weight:bold;
			}		
        </style>
        
        <script type="text/javascript">    
        
        $(document).ready(function(){
        	;BATCHLOG = {
        		dataTable : null,

        		init : function() {
        			BATCHLOG.makeTable();
        			BATCHLOG.makeClickers();   
//        			BATCHLOG.makeModal();
        		},
        		
        		
        		
        		
        		doFunctionBinding : function() {
        			$(".jobtag-edit-action-link").click( function($event) {
        				var $tagId = $(this).attr("data-id");
        				ANSI_UTILS.doServerCall("get", "jobtag/jobTag/"+$tagId, null, BATCHLOG.getTagSuccess, BATCHLOG.getTagFailure);
        			});
        			$(".jobtag-delete-action-link").click( function($event) {
        				var $tagId = $(this).attr("data-id");
        				$("#jobtag-delete-modal").attr("data-id", $tagId);
        				$("#jobtag-delete-modal").dialog("open");
        			});
    			},
    			

    			
    			
    			
    			makeClickers : function() {
    				$('.ScrollTop').click(function() {
    					$('html, body').animate({scrollTop: 0}, 800);
    					return false;
    	       	    });
    			},
        		
        		
        		
    			makeModal : function() {
    				$( "#jobtag-crud-modal" ).dialog({
						title:'Job Tag',
						autoOpen: false,
						height: 375,
						width: 500,
						modal: true,
						closeOnEscape:true,
						//open: function(event, ui) {
						//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
						//},
						buttons: [
							{
								id: "jobtag-cancel-button",
								click: function($event) {
									$( "#jobtag-crud-modal" ).dialog("close");
								}
							},
							{
								id: "jobtag-save-button",
								click: function($event) {
									BATCHLOG.saveTag();
								}
							},
							
						]
					});	
					$("#jobtag-save-button").button('option', 'label', 'Save');
					$("#jobtag-cancel-button").button('option', 'label', 'Cancel');
					
					
					
					
    			},
    			
    			
    			
    			
        		
        		makeTable : function(){
            		BATCHLOG.dataTable = $('#jobtag-lookup-table').DataTable( {
            			"aaSorting":		[[4,'desc']],
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
            	            { className: "dt-left", "targets": [1,2,3,4,5,6] },
            	            { className: "dt-center", "targets": [0] },
            	            { className: "dt-right", "targets": []}
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": "batch/log",
    			        	"type": "GET",
    			        	"data": {}
    			        	},
    			        columns: [
    			            { width:"5%", title: "ID", "defaultContent": "<i>N/A</i>", searchable:true, data: "batch_log_id" },
    			            { width:"5%", title: "Type", "defaultContent": "<i>N/A</i>", searchable:true, data: "batch_type" },
    			            { width:"10%", title: "Detail", "defaultContent": "<i>N/A</i>", searchable:true, data: "batch_type_detail"},
    			            { width:"45%", title: "Parms", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.parameters != null){return row.parameters;}
    			            } },
    			            { width:"15%", title: "Start", "defaultContent": "<i>N/A</i>", searchable:true, data: "start_time" },
    			            { width:"15%", title: "End", "defaultContent": "<i>N/A</i>", searchable:true, data: "end_time" },
    			            { width:"5%", title: "Status", "defaultContent": "<i>N/A</i>", searchable:true, data: "status" },
    			            ],
    			            "initComplete": function(settings, json) {
    			            	console.log("initComplete");
    			            	//console.log(json);
    			            	//doFunctionBinding();
    			            	var myTable = this;
    			            	LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#jobtag-lookup-table", BATCHLOG.makeTable);
    			            },
    			            "drawCallback": function( settings ) {    			            	
    			            	console.log("drawCallback");
    			            	//$("#searching-modal").dialog("close");
    			            	BATCHLOG.doFunctionBinding();
    			            }
    			    } );
            	},
            	
            	
            	
        	};
        	
        	BATCHLOG.init();
        	
        	
        });
        		
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1><bean:message key="page.label.batchLog" /></h1> 

    	
    	  	
	 	<webthing:lookupFilter filterContainer="filter-container" />
		<div id="table-container">
		 	<table id="jobtag-lookup-table" class="display" cellspacing="0" style="table-layout: fixed; font-size:9pt;min-width:1300px; max-width:1300px;width:1300px;">
		        <thead></thead>
		        <tbody></tbody>
		        <tfoot></tfoot>
		    </table>
	    </div>
	    
	    <webthing:scrolltop />
	

		
		
    </tiles:put>
		
</tiles:insert>

