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
        Ticket Print
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
        <link rel="stylesheet" href="css/datepicker.css" type="text/css" />
        <style type="text/css">
        	#printModal {
        		display:none;
        	}
			.action-link {
				cursor:pointer;
			}
			#pdfDownload {
				display:none;
			}
			#ticketTableContainer {
				display:none;
			}
        </style>
        
        <script type="text/javascript">        
        $( document ).ready(function() {
        	$('.dateField').datepicker({
                prevText:'&lt;&lt;',
                nextText: '&gt;&gt;',
                showButtonPanel:true
            });

        	$('#printDate').change(function($event) {
        		validateDate();
        	});
        	
//        	$('#printDate').focus(function($event) {
//        		$("#ticketTableContainer").fadeOut(1000);
//        	});
        	
        	$("#printDateButton").click(function($event) {
        		console.debug("clicking");
        		validateDate();
        	});
        	

        	function validateDate() {
        		if ( $('#printDate').val() == '' ) {
        			$("#printDateErr").html("Required input").show().fadeOut(6000);
        			$("#printDate").focus();
        		} else {
        			$("#printDateErr").fadeOut(6000);
        			makeInvoiceTable();
        			$("#ticketTableContainer").fadeIn(3000);
        		}
        	}
        
        	function makeInvoiceTable() {
        		var $outbound = {};
        		$outbound['printDate'] = $("#printDate").val();
		        var jqxhr = $.ajax({
		   			type: 'POST',
		   			url: 'ticketPrintLookup/',
		   			data: JSON.stringify($outbound),
		   			statusCode: {
			   			200: function($data) {   				
			   				populateDataTable($data.data);
			   			},
		   				403: function($data) {
		   					$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage).show();
		   				},
		   				404: function($data) {
		   					$("#globalMsg").html("System Error 404: Contact Support").show();
		   				},
		   				500: function($data) {
	        	    		$("#globalMsg").html("System Error 500: Contact Support").show();
	        	    	} 
		   			},
		   			dataType: 'json'
		   		});        	
        	}
        	
         
			$('#ticketTable').dataTable().fnDestroy();
			
	           
			var dataTable = null;
        	
			function populateDataTable($data) {
				var dataTable = $('#ticketTable').DataTable( {
	        			"bDestroy":			true,
	        			"processing": 		true,
	        			"autoWidth": 		false,
	        	        "scrollCollapse": 	true,
	        	        "scrollX": 			true,
	        	        rowId: 				'dt_RowId',
	        	        dom: 				'Bfrtip',
	        	        "searching": 		true,
	        	        //lengthMenu: [
	        	        //    [ 10, 50, 100, 500, 1000, -1 ],
	        	        //    [ '10 rows', '50 rows', '100 rows', '500 rows', '1000 rows', 'Show All' ],
	        	        //],
	        	        buttons: [
	        	        //	'pageLength','copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print',{extend: 'colvis',	label: function () {doFunctionBinding();}},
	        	        ],
	        	        "columnDefs": [
	        	        	{ "orderable": true, "targets": "_all" },
	        	            { className: "dt-left", "targets": [0] },
	        	            { className: "dt-center", "targets": [1,3] },
	        	            { className: "dt-right", "targets": [2] },
	        	         ],
	        	        "paging": false,
	        	        data: $data.ticketList,
				        columns: [
				            { title: "Division", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
				            	if(row.div != null){return (row.div+"");}
				            } },
				            { title: "Tickets", "defaultContent": "<i>0</i>", data: function ( row, type, set ) {
				            	if(row.ticketCount != null){return (row.ticketCount+"");}
				            } },
				            { title: "PPC", "defaultContent": "<i></i>", data: function ( row, type, set ) {
				            	if(row.totalPpc != null){return (row.totalPpc+"");}
				            } },
				            { title: "Action", "defaultContent": "<i></i>", data: function ( row, type, set ) {
				            	if(row.ticketCount == "0"){
				            		return "";
				            	} else {
				            		var $dataDiv = 'data-division="' + row.divisionId + '"';
				            		var $printLink = '<i class="action-link fa fa-print" aria-hidden="true" data-action="print" ' + $dataDiv + '></i>';
				            		return $printLink;
				            	}
				            } }
				            ],
				            "initComplete": function(settings, json) {
				            	doFunctionBinding();
				            },
				            "drawCallback": function( settings ) {
				            	doFunctionBinding();
				            }
				    } );
	        	}
        	
        		init();
        			
        	
	            function init(){
	            //	$.each($('input'), function () {
				//	        $(this).css("height","20px");
				//	        $(this).css("max-height", "20px");
				//	    });
						
	            	//populateDataTable();
	            };
	            
	            function initComplete (){
					var r = $('#ticketTable tfoot tr');
					r.find('th').each(function(){
						$(this).css('padding', 8);
					});
					$('#ticketTable thead').append(r);
	            }
            
				function doFunctionBinding() {
					$( ".action-link" ).on( "click", function($clickevent) {
			        	var $divisionId = $clickevent.currentTarget.attributes['data-division'].value;
			        	var $action = $clickevent.currentTarget.attributes['data-action'].value;
			        	if ( $action=='list') {
			        		location.href="ticketLookup.html?id=" + $divisionId;
			        	} else if ( $action=='print') {
			        		//printModal($divisionId);
			        		alert("Not quote ready to print yet");
			        	} else {
			        		$("#globalMessage").html("Invalid action. Reload the page and start again");
			        	}
					});
				}

				
				

		
				
				function printInvoices() {
        			var $divisionId = $("#goPrint").data('divisionId');
					var $printDate = $("#printDate").val();
					var $dueDate = $("#dueDate").val();
					console.debug("Getting ticket count");
		        	var $outbound = {'divisionId':$divisionId,'printDate':$printDate,'dueDate':$dueDate};
		            var jqxhr = $.ajax({
		    			type: 'POST',
		    			url: 'ticketPrint/',
		    			data: JSON.stringify($outbound),
		    			statusCode: {
			    			200: function($data) {
			    				if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
			    					$.each($data.data.webMessages, function (key, value) {
			    						var $selectorName = "#" + key + "Err";
			    						$($selectorName).show();
			    						$($selectorName).html(value[0]).fadeOut(4000);
			    					});
			    				} else {
			    					$("#printModal").dialog("close");
			    					var a = document.createElement('a');
			    					var linkText = document.createTextNode("Download");
			    					a.appendChild(linkText);
			    					a.title = "Download";
			    					a.href = $data.data.ticketFile;
			    					a.target = "_new";   // open in a new window
			    					document.body.appendChild(a);
			    					a.click();
			    				}
			    			},
		    				403: function($data) {
		    					$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
		    				},
		    				500: function($data) {
		         	    		$("#globalMsg").html("System Error: Contact Support").fadeIn(10);
		         	    	} 
		    			},
		    			dataType: 'json'
		    		});        	
		        }
        
			});

		</script>
    </tiles:put>
    
    
    <tiles:put name="content" type="string">    	
    	<h1>Ticket Print</h1>

		<span class="err" id="printDateErr"></span><br />
		<span class="formLabel">Print Tickets Through: </span>
		<%-- <input type="text" class="dateField" id="printDate"/> <input type="button" value="Go" id="printDateButton" />--%>
		<div id="printDate" class="dateField"></div>
		
    	
    	
    	<div id="ticketTableContainer">
	    	<table id="ticketTable" style="table-layout: fixed" class="display" cellspacing="0" width="100%" style="font-size:9pt;max-width:700px;width:1300px;">
	        	<colgroup>
		        	<col style="width:25%;" />
		        	<col style="width:25%;" />
		        	<col style="width:25%;" />
		        	<col style="width:25%;" />
		   		</colgroup>        
		        <thead>
		            <tr>
		                <th>Division</th>
		    			<th>Tickets</th>
		    			<th>PPC</th>
		    			<th>Action</th>
		            </tr>
		        </thead>
		        <tfoot>
		            <tr>
		                <th>Division</th>
		    			<th>Tickets</th>
		    			<th>PPC</th>
		    			<th>Action</th>
		            </tr>
		        </tfoot>
		    </table>
	    </div>
    
    
    </tiles:put>

</tiles:insert>

