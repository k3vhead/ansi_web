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
<%@ taglib tagdir="/WEB-INF/tags/document" prefix="document" %>
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
  	    <jsp:include page="documents/documentViewJS.jsp" /> <%-- We do this way so the custom tags in the JS will get parsed properly --%>
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
			.documentAction {
				cursor:pointer;
			}
        </style>
        
        <script type="text/javascript">      
        
        $(document).ready(function(){
        	;QUOTELOOKUP = {
        		init : function() {
                	CALLNOTE.init();
                	QUOTE_PRINT.init_modal(<%= "\"#" + quotePrintModal + "\"" %>);
        			QUOTELOOKUP.makeClickers();
    				DOCVIEW.init();
    				QUOTELOOKUP.createTable();
        		},
        		
        		
        		
        		
        		createTable : function(){
            		var dataTable = $('#quoteTable').DataTable( {
            			"aaSorting":		[[0,'desc']],
            			"processing": 		true,
            	        "serverSide": 		true,
            	        "autoWidth": 		true,
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
            	            { className: "dt-left", "targets": [2,3,4,5,6,10] },
            	            { className: "dt-center", "targets": [0,1,7,8] },
            	            { className: "dt-right", "targets": [9]}
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": "quoteTable",
    			        	"type": "GET"
    			        	},
    			        columns: [
    			            { title: "<bean:message key="field.label.quoteId" />", width:"5%", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
    			            	if(row.quoteId != null){return (row.quoteId+"");}
    			            } },
    			            { title: "<bean:message key="field.label.quoteCode" />", width:"8%", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.quoteCode != null){return (row.quoteCode+"");}
    			            } },
    			            { title: "<bean:message key="field.label.divisionNbr" />", width:"6%", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.divisionNbr != null){return (row.divisionNbr+"-"+row.divisionCode);}
    			            } },
    			            { title: "<bean:message key="field.label.billToName" />" , width:"14%", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
    			            	if(row.billToName != null){return (row.billToName+"");}
    			            } },
    			            { title: "<bean:message key="field.label.jobSiteName" />", width:"14%", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.jobSiteName != null){return (row.jobSiteName+"");}
    			            } },
    			            { title: "<bean:message key="field.label.jobSiteAddress" />",  width:"14%", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.jobSiteAddress != null){return (row.jobSiteAddress+"");}
    			            } },
    			            { title: "<bean:message key="field.label.managerName" />", width:"10%", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.managerName != null){return (row.managerName+"");}
    			            } },
    			            { title: "<bean:message key="field.label.proposalDate" />", width:"7%", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.proposalDate != null){return (row.proposalDate+"");}
    			            } },
    			            { title: "<bean:message key="field.label.quoteJobCount" />",width:"5%", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) { 	
    			            	if(row.quoteJobCount != null){return (row.quoteJobCount+"");}
    			            } },
    			            { title: "<bean:message key="field.label.quotePpcSum" />",width:"8%",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
    			            	if(row.quotePpcSum != null){return (row.quotePpcSum+"");} 
    			            } },
    			            { title: "<bean:message key="field.label.action" />", width:"8%",  data: function ( row, type, set ) {	
    			            	//console.log(row);
    			            	editText = '<a href="quoteMaintenance.html?id='+row.quoteId+'" class="editAction" data-id="'+row.quoteId+'"><webthing:edit>Edit</webthing:edit></a>';
    			            	viewText = '<a href="quoteMaintenance.html?id='+row.quoteId+'" class="editAction" data-id="'+row.quoteId+'"><webthing:view style="color:#404040;">View</webthing:view></a>';
    			            	printText = '<i class="fa fa-print orange quotePrint" aria=hidden="true" data-id="'+row.quoteId+'" data-quotenumber="'+ row.quoteCode + '"></i>';
    			            	copyText = '<i class="far fa-copy copyQuote" aria-hidden="true" data-id="'+row.quoteId+'"></i>';
    			            	var $noteLink = '<webthing:notes xrefType="QUOTE" xrefId="' + row.quoteId + '">Quote Notes</webthing:notes>'
    			            	var $docLink = "";;
    			            	if ( row.documentCount > 0 ) {
    			            		$docLink = '<webthing:document styleClass="documentAction" xrefType="SIGNED_CONTRACT" xrefId="'+row.quoteId+'">Signed Quote</webthing:document>';
    			            	}
    			            	{return '<ansi:hasPermission permissionRequired="QUOTE_READ" maxLevel="true">'+ viewText + '&nbsp;</ansi:hasPermission><ansi:hasPermission permissionRequired="QUOTE_CREATE">'+ editText +'</ansi:hasPermission>&nbsp;' + printText + '&nbsp;<ansi:hasPermission permissionRequired="QUOTE_CREATE">' + copyText + '</ansi:hasPermission> ' + $noteLink + $docLink;}
    			            	
    			            } }],
    			            //"initComplete": function(settings, json) {
    			            	//console.log(json);
    			            //	doFunctionBinding();
    			            //},
    			            "drawCallback": function( settings ) {
    			            	QUOTELOOKUP.doFunctionBinding();
    			            	CALLNOTE.lookupLink();
    			            	DOCVIEW.lookupLink("documentAction");
    			            }
    				    } );
           		},
        		
           		
           		
           		doCopy : function($quoteId) {
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
    							$("#globalMsg").html("System error copy 404. Contact Support").show();
    						},
    						500: function($data) {
    							$("#globalMsg").html("System error copy 500. Contact Support").show();
    						}
    					},
    					dataType: 'json'
    				});
    			},
    			
    			
           		
    			doEdit : function($clickevent) {
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
    						} 
    					},
    					dataType: 'json'
    				});				
    			},
    			
    			
    			
           		doFunctionBinding : function() {
    				$( ".editAction" ).on( "click", function($clickevent) {
    					 QUOTELOOKUP.doEdit($clickevent);
    				});
    				$( ".quotePrint" ).on( "click", function($clickevent) {
    					 QUOTE_PRINT.showQuotePrint(<%= "\"#" + quotePrintModal + "\"" %>, $(this).data("id"), $(this).data("quotenumber"), "PREVIEW");
    				});
    				$( ".copyQuote" ).on( "click", function($clickevent) {
    					QUOTELOOKUP.doCopy($(this).data("id"));
    				});
    			},
           		
           		
           		
        		
        		makeClickers : function() {
        			$('.ScrollTop').click(function() {
        				$('html, body').animate({scrollTop: 0}, 800);
             	  		return false;
        			});           	      

                	$('.dateField').datepicker({
                        prevText:'&lt;&lt;',
                        nextText: '&gt;&gt;',
                        showButtonPanel:true
                    });

        		},
        	};
        	
        	QUOTELOOKUP.init();
        	
        	
		});
        </script>        
    </tiles:put>
    
    
    
    

    
    
    
    
    
    
   <tiles:put name="content" type="string">
    	<h1>Quote Lookup</h1>
    	
	 	<table id="quoteTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:1300px;width:1300px;">	        
	    </table>
	    
	    <webthing:scrolltop />
    
    	<webthing:quotePrint modalName="<%= quotePrintModal %>" />
    	<webthing:callNoteModals />
  		<document:documentViewModals />
    </tiles:put>
		
</tiles:insert>

