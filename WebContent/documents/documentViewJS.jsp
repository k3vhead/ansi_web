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

<script type="text/javascript">
$(function() {    	
	;DOCVIEW = {
		docTypeList : [],
		dataTable : null,
		
		init : function($lookupTableSelect) {
			DOCVIEW.makeModals();			
		},
		
		
		
		// when the document view button is in a datatables lookup table, call this function
		// in "drawcallback". (the same place you do function binding)
		lookupLink : function($selector) {
			var $actionSelector = "." + $selector;
			$($actionSelector).click(function($event) {
				var $xrefType = $(this).attr("data-xreftype");
				var $xrefId = $(this).attr("data-xrefid");
				//$("#document-list-table").html("Show docs for " + $xrefType + " " + $xrefId);
				$("#documentListModal").dialog("open");
				if ( DOCVIEW.dataTable != null ) {
					DOCVIEW.dataTable.destroy();
					$("#document-list-table").html("");
					DOCVIEW.dataTable = null;
				}
				DOCVIEW.makeTable($xrefType, $xrefId);
			});
			
		},
		
		
		
		documentLink : function() {
			$(".document-view-action-link").click(function($event) {
				var $xrefId = $(this).attr("data-id");
				var $url = "documentViewer.html?id=" + $xrefId;
				
				var jqxhr = $.ajax({
					type: 'GET',
					url: $url,
					statusCode: {
						200: function($data) {
							$("#documentViewer").html($data);
							$("#documentViewer").dialog("open");
						},
						403: function($data) {
							$("#globalMsg").html("Session expired. Log in and try again").show();
						},
						404: function($data) {
							$("#globalMsg").html("System Error: Document Viewer 404. Contact Support").show();
						},
						405: function($data) {
							$("#globalMsg").html("System Error: Document Viewer 405. Contact Support").show();
						},
						500: function($data) {
							$("#globalMsg").html("System Error: Document Viewer 500. Contact Support").show();
						} 
					},
					dataType: 'html'
				});	
			});
		},
		
		

		doFunctionBinding : function() {
			// place holder
		},
		
		
		
		makeModals : function() {
			$("#documentViewer" ).dialog({
				title:'Document Viewer',
				autoOpen: false,
				height: 800,
				width: 1000,
				modal: true,
				buttons: [
					{
						id: "document-viewer-cancel-button",
						click: function() {
							$("#documentViewer").dialog( "close" );
						}
					}	      	      
				],
				close: function() {
					$("#documentViewer").dialog( "close" );
					//allFields.removeClass( "ui-state-error" );
				}
			});        			
			$('#document-viewer-cancel-button').button('option', 'label', 'OK');
			
			
			
			
			$("#documentListModal" ).dialog({
				title:'Document List',
				autoOpen: false,
				height: 400,
				width: 1200,
				modal: true,
				buttons: [
					{
						id: "document-list-cancel-button",
						click: function() {
							$("#documentListModal").dialog( "close" );
						}
					}	      	      
				],
				close: function() {
					$("#documentListModal").dialog( "close" );
					//allFields.removeClass( "ui-state-error" );
				}
			});        			
			$('#document-list-cancel-button').button('option', 'label', 'OK');
		},
		
		
		
		
		makeTable : function($xrefType, $xrefId){
			var $url = "document/viewLookup/" + $xrefType + "/" + $xrefId;
			
			DOCVIEW.dataTable = $('#document-list-table').DataTable( {
    			"aaSorting":		[[0,'desc']],
    			"processing": 		true,
    	        "serverSide": 		true,
    	        "autoWidth": 		true,
    	        "deferRender": 		true,
    	        "scrollCollapse": 	true,
    	        "scrollX": 			false,
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
    	        	'pageLength','copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print',{extend: 'colvis',	label: function () {DOCVIEW.doFunctionBinding();}}
    	        ],
    	        
    	        "columnDefs": [
     	            { "orderable": false, "targets": -1 },
    	            { className: "dt-left", "targets": [1,4] },
    	            { className: "dt-center", "targets": [0,2,3,5] },
    	            { className: "dt-right", "targets": []}
    	         ],
    	        "paging": true,
		        "ajax": {
		        	"url": $url,
		        	"type": "GET",
		        	"data": {}
		        	},
		        columns: [
		            { width:"5%", title: "Document ID", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
		            	if(row.document_id != null){return row.document_id;}
		            } },
		            { width:"10%", title: "Document Type", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
		            	if(row.xref_type_display != null){return row.xref_type_display;}
		            } },
		            { width:"28%", title: "Description", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
		            	if(row.description != null){return row.description;}
		            } },
		            { width:"10%", title: "Document Date", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
		            	if(row.document_date != null){return row.document_date;}
		            } },
		            { width:"10%", title: "Expiration Date", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
		            	if(row.expiration_date != null){return (row.expiration_date+"");}
		            } },
		            { width:"27%", title: "Reference", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
		            	if(row.xref_display != null){return (row.xref_display);}
		            } },
		            { width:"10%", title: "Action",  data: function ( row, type, set ) {
						var $viewLink = '<a href="#" class="document-view-action-link" data-id="'+row.document_id+'"><webthing:view>View</webthing:view></a>';
		            	return $viewLink
		            } }],
		            "initComplete": function(settings, json) {
		            	console.log("initComplete");
		            	//console.log(json);
		            	//doFunctionBinding();
		            	var myTable = this;
		            	//LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#document-lookup-table", DOCVIEW.makeTable);
		            },
		            "drawCallback": function( settings ) {    			            	
		            	console.log("drawCallback");
		            	//$("#searching-modal").dialog("close");
		            	DOCVIEW.doFunctionBinding();
		            	DOCVIEW.documentLink();
		            }
		    } );
    	},
	}
	
});
</script>