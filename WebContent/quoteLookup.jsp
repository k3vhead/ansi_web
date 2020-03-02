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

<%
	String quotePrintModal = "printQuoteDiv";
%>

<tiles:insert page="layout.jsp" flush="true">

    <tiles:put name="title" type="string">
        Quote Lookup
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
		<link rel="stylesheet" href="css/callNote.css" />
    	<link rel="stylesheet" href="css/accordion.css" type="text/css" />
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/callNote.js"></script>     
  	    <script type="text/javascript" src="js/quotePrint.js"></script>
        <style type="text/css">
			#displayTable {
				width:90%;
			}
			#addFormDiv {
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
			select	{
				width:80px !important;
				max-width:80px !important;
			}
			.quotePrint {
				cursor:pointer;
			}
			.copyQuote {
				cursor:pointer;
			}
			#printQuoteDiv {
				display:none;
			}
        </style>
        
        <script type="text/javascript">      
        
        $(document).ready(function(){
        	
        	; QUOTE_LOOKUP = {
			
	     	     init : function () {
	     	    	QUOTE_LOOKUP.makeClickers(); 
	     	    	QUOTE_LOOKUP.createTable();
	            	CALLNOTE.init();
	            	QUOTE_PRINT.init_modal(<%= "\"#" + quotePrintModal + "\"" %>);
	     	      
	     	     },
	     	     
	     	     makeClickers : function () {
		        	$('.dateField').datepicker({
		                prevText:'&lt;&lt;',
		                nextText: '&gt;&gt;',
		                showButtonPanel:true
		            });
	     	     },

				doFunctionBinding : function () {
					$( ".editAction" ).on( "click", function($clickevent) {
						QUOTE_LOOKUP.doEdit($clickevent);
					});
					$( ".quotePrint" ).on( "click", function($clickevent) {
						 QUOTE_PRINT.showQuotePrint(<%= "\"#" + quotePrintModal + "\"" %>, $(this).data("id"), $(this).data("quotenumber"), "PREVIEW");
					});
					$( ".copyQuote" ).on( "click", function($clickevent) {
						QUOTE_LOOKUP.doCopy($(this).data("id"));
					});
				},

			
				doEdit : function ($clickevent) {
					var $rowid = $clickevent.currentTarget.attributes['data-id'].value;
					var $url = 'quoteTable/' + $rowid;
					var jqxhr = $.ajax({
						type: 'GET',
						url: $url,
						success: function($data) {
			        		$("#quoteId").val(($data.data.codeList[0]).quoteId);
			        		$("#quoteCode").val(($data.data.codeList[0]).quoteCode);
			        		$("#divisionNbr").val(($data.data.codeList[0]).divisionNbr);
			        		$("#billToName").val(($data.data.codeList[0]).billToName);
			        		$("#jobSiteName").val(($data.data.codeList[0]).jobSiteName);
			        		$("#jobSiteAddress").val(($data.data.codeList[0]).jobSiteAddress);
			        		$("#managerName").val(($data.data.codeList[0]).managerName);
			        		$("#proposalDate").val(($data.data.codeList[0]).proposalDate);
			        		$("#quoteJobCount").val(($data.data.codeList[0]).quoteJobCount);
			        		$("#quotePpcSum").val(($data.data.codeList[0]).quotePpcSum);
			        		
			        		$("#qId").val(($data.data.codeList[0]).quoteId);
			        		$("#updateOrAdd").val("update");
			        		$("#addQuoteTableForm").dialog( "open" );
						},
						statusCode: {
							403: function($data) {
								$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
							},
							404: function($data) {
								$("#globalMsg").html("System Error Copy 404. Contact Support");
							},
							500: function($data) {
								$("#globalMsg").html("System Error Copy 500. Contact Support");
							} 
						},
						dataType: 'json'
					});				
				},
				
				doCopy : function ($quoteId) {
					var $url = "quote/copy/" + $quoteId;
					var jqxhr = $.ajax({
						type: 'POST',
						url: $url,
						data: {},
						statusCode: {
							200: function($data) {
								$quoteId = $data.data.quote.quote.quoteId;
								location.href="quoteMaintenance.html?id=" + $quoteId;
							},					
							403: function($data) {
								$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
							},
							404: function($data) {
								$("#globalMsg").html("System Error Copy 404. Contact Support");
							},
							500: function($data) {
								$("#globalMsg").html("System Error Copy 500. Contact Support");
							}
						},
						dataType: 'json'
					});
				},
				
				createTable : function (){
	        		var dataTable = $('#quoteTable').DataTable( {
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
	        	        	'pageLength','copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print',{extend: 'colvis',	label: function () {doFunctionBinding();$('#jobTable').columns.adjust().draw();}}
	        	        ],
	        	        "columnDefs": [
	         	            { "orderable": false, "targets": -1 },
	        	            { className: "dt-left", "targets": [0,2,3,4,5] },
	        	            { className: "dt-center", "targets": [1,7,8,-1] },
	        	            { className: "dt-right", "targets": [9]}
	        	         ],
	        	        "paging": true,
				        "ajax": {
				        	"url": "quoteTable",
				        	"type": "GET"
				        	},
				        columns: [
				            { title: "<bean:message key="field.label.quoteId" />", "defaultContent": "<i>N/A</i>", width: '4%', data: function ( row, type, set ) {	
				            	if(row.quoteId != null){return (row.quoteId+"");}
				            } },
				            { title: "<bean:message key="field.label.quoteCode" />", "defaultContent": "<i>N/A</i>", width: '6%', data: function ( row, type, set ) {
				            	if(row.quoteCode != null){return (row.quoteCode+"");}
				            } },
				            { title: "<bean:message key="field.label.divisionNbr" />", "defaultContent": "<i>N/A</i>", width: '6%', data: function ( row, type, set ) {
				            	if(row.divisionNbr != null){return (row.divisionNbr+"-"+row.divisionCode);}
				            } },
				            { title: "<bean:message key="field.label.billToName" />" , "defaultContent": "<i>N/A</i>", width: '16%', data: function ( row, type, set ) {	
				            	if(row.billToName != null){return (row.billToName+"");}
				            } },
				            { title: "<bean:message key="field.label.jobSiteName" />", "defaultContent": "<i>N/A</i>", width: '16%', data: function ( row, type, set ) {
				            	if(row.jobSiteName != null){return (row.jobSiteName+"");}
				            } },
				            { title: "<bean:message key="field.label.jobSiteAddress" />",  "defaultContent": "<i>N/A</i>", width: '13%', data: function ( row, type, set ) {
				            	if(row.jobSiteAddress != null){return (row.jobSiteAddress+"");}
				            } },
				            { title: "<bean:message key="field.label.managerName" />", "defaultContent": "<i>N/A</i>", width: '10%', data: function ( row, type, set ) {
				            	if(row.managerName != null){return (row.managerName+"");}
				            } },
				            { title: "<bean:message key="field.label.proposalDate" />", "defaultContent": "<i>N/A</i>", width: '9%', data: function ( row, type, set ) {
				            	if(row.proposalDate != null){return (row.proposalDate+"");}
				            } },
				            { title: "<bean:message key="field.label.quoteJobCount" />", "defaultContent": "<i>N/A</i>", width: '5%', data: function ( row, type, set ) { 	
				            	if(row.quoteJobCount != null){return (row.quoteJobCount+"");}
				            } },
				            { title: "<bean:message key="field.label.quotePpcSum" />", "defaultContent": "<i>N/A</i>", width: '8%', data: function ( row, type, set ) {	
				            	if(row.quotePpcSum != null){return (row.quotePpcSum+"");} 
				            } },
				            { title: "<bean:message key="field.label.action" />",  width: '7%', data: function ( row, type, set ) {	
				            	//console.log(row);
				            	editText = '<a href="quoteMaintenance.html?id='+row.quoteId+'" class="editAction" data-id="'+row.quoteId+'"><webthing:edit>Edit</webthing:edit></a>';
				            	viewText = '<a href="quoteMaintenance.html?id='+row.quoteId+'" class="editAction" data-id="'+row.quoteId+'"><webthing:view style="color:#404040;">View</webthing:view></a>';
				            	printText = '<i class="fa fa-print orange quotePrint" aria=hidden="true" data-id="'+row.quoteId+'" data-quotenumber="'+ row.quoteCode + '"></i>';
				            	copyText = '<i class="far fa-copy copyQuote" aria-hidden="true" data-id="'+row.quoteId+'"></i>';
				            	var $noteLink = '<webthing:notes xrefType="QUOTE" xrefId="' + row.quoteId + '">Quote Notes</webthing:notes>'
				            	{return '<ansi:hasPermission permissionRequired="QUOTE_READ" maxLevel="true">'+ viewText + '&nbsp;</ansi:hasPermission><ansi:hasPermission permissionRequired="QUOTE_CREATE">'+ editText +'</ansi:hasPermission>&nbsp;' + printText + '&nbsp;<ansi:hasPermission permissionRequired="QUOTE_CREATE">' + copyText + '</ansi:hasPermission> ' + $noteLink;}
				            	
				            } }],
				            //"initComplete": function(settings, json) {
				            	//console.log(json);
				            //	doFunctionBinding();
				            //},
				            "drawCallback": function( settings ) {
				            	QUOTE_LOOKUP.doFunctionBinding();
				            	CALLNOTE.lookupLink();
				            }
					    } );
	        		},
	     	   	};
	     	 QUOTE_LOOKUP.init();
			});
        </script>        
    </tiles:put>
    
    
    
    

    
    
    
    
    
    
   <tiles:put name="content" type="string">
    	<h1>Quote Lookup</h1>
    	
	 	<table id="quoteTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:1300px;width:1300px;">
	       <thead>
	            <tr>
	                <th><bean:message key="field.label.quoteId" /></th>
	    			<th><bean:message key="field.label.quoteCode" /></th>
	    			<th><bean:message key="field.label.divisionNbr" /></th>
	    			<th><bean:message key="field.label.billToName" /></th>
	    			<th><bean:message key="field.label.jobSiteName" /></th>
	    			<th><bean:message key="field.label.jobSiteAddress" /></th>
	    			<th><bean:message key="field.label.managerName" /></th>
	    			<th><bean:message key="field.label.proposalDate" /></th>
	    			<th><bean:message key="field.label.quoteJobCount" /></th>
	    			<th><bean:message key="field.label.quotePpcSum" /></th>
	    			<th><bean:message key="field.label.action" /></th>
	            </tr>
	        </thead>
	        <tfoot>
	            <tr>
	                <th><bean:message key="field.label.quoteId" /></th>
	    			<th><bean:message key="field.label.quoteCode" /></th>
	    			<th><bean:message key="field.label.divisionNbr" /></th>
	    			<th><bean:message key="field.label.billToName" /></th>
	    			<th><bean:message key="field.label.jobSiteName" /></th>
	    			<th><bean:message key="field.label.jobSiteAddress" /></th>
	    			<th><bean:message key="field.label.managerName" /></th>
	    			<th><bean:message key="field.label.proposalDate" /></th>
	    			<th><bean:message key="field.label.quoteJobCount" /></th>
	    			<th><bean:message key="field.label.quotePpcSum" /></th>
	    			<th><bean:message key="field.label.action" /></th>
	    			
	            </tr>
	        </tfoot>
	    </table>
	    
	    <webthing:scrolltop />
    
    	<webthing:quotePrint modalName="<%= quotePrintModal %>" />
    	<webthing:callNoteModals />
  
    </tiles:put>
		
</tiles:insert>

