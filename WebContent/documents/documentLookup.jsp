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
        <bean:message key="page.label.document" /> <bean:message key="menu.label.lookup" />
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
			.dataTables_wrapper {
				padding-top:10px;
			}	
			.form-label {
				font-weight:bold;
			}		
        </style>
        
        <script type="text/javascript">    
        
        $(document).ready(function(){
        	;DOCUMENTLOOKUP = {
        		dataTable : null,
        		uploadMessage : '<c:out value="${ansi_upload_message}" />',
        		
        		init : function() {
        			DOCUMENTLOOKUP.makeTable();
        			DOCUTILS.init('#document-lookup-table');        			
        			DOCUMENTLOOKUP.makeClickers();  
        			if ( DOCUMENTLOOKUP.uploadMessage != null && DOCUMENTLOOKUP.uploadMessage != '') {
        				$("#globalMsg").html(DOCUMENTLOOKUP.uploadMessage).show().fadeOut(3000);
        			}
        		},
        		
        		
        		
        		doFunctionBinding : function() {
    				

    			},
    			
    			
    			
    			
    			
    			
    			
    			makeClickers : function() {
    				$('.ScrollTop').click(function() {
    					$('html, body').animate({scrollTop: 0}, 800);
    					return false;
    	       	    });
    				
    				$("#new-document-button").click(function() {
    					DOCUTILS.showAddModal()
    				});
    			},
        		
        		
        		
        		
        		
        		
        		
        		makeTable : function(){
            		DOCUMENTLOOKUP.dataTable = $('#document-lookup-table').DataTable( {
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
            	            { className: "dt-left", "targets": [1,4] },
            	            { className: "dt-center", "targets": [0,2,3,5] },
            	            { className: "dt-right", "targets": []}
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": "documents/documentLookup",
    			        	"type": "GET",
    			        	"data": {}
    			        	},
    			        columns: [
    			            { width:"10%", title: "<bean:message key="field.label.documentId" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.document_id != null){return row.document_id;}
    			            } },
    			            { width:"10%", title: "<bean:message key="field.label.documentType" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.xref_type_display != null){return row.xref_type_display;}
    			            } },
    			            { width:"25%", title: "<bean:message key="field.label.description" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.description != null){return row.description;}
    			            } },
    			            { width:"10%", title: "<bean:message key="field.label.documentDate" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.document_date != null){return row.document_date;}
    			            } },
    			            { width:"10%", title: "<bean:message key="field.label.documentExpirationDate" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.expiration_date != null){return (row.expiration_date+"");}
    			            } },
    			            { width:"10%", title: "<bean:message key="field.label.xrefDisplay" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.xref_display != null){return (row.xref_display);}
    			            } },
    			            { width:"10%", title: "<bean:message key="field.label.action" />",  data: function ( row, type, set ) {
								var $viewLink = '<a href="#" class="document-view-action-link" data-id="'+row.document_id+'"><webthing:view>View</webthing:view></a>';
    			            	var $editLink = '<a href="#" class="document-edit-action-link" data-id="'+row.document_id+'"><webthing:edit>Edit</webthing:edit></a>';
    			            	var $deleteLink = '<a href="#" class="document-delete-action-link" data-id="'+row.document_id+'"><webthing:delete>Delete</webthing:delete></a>';
    			            	return $viewLink + $editLink + $deleteLink
    			            } }],
    			            "initComplete": function(settings, json) {
    			            	console.log("initComplete");
    			            	//console.log(json);
    			            	//doFunctionBinding();
    			            	var myTable = this;
    			            	LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#document-lookup-table", DOCUMENTLOOKUP.makeTable);
    			            },
    			            "drawCallback": function( settings ) {    			            	
    			            	console.log("drawCallback");
    			            	//$("#searching-modal").dialog("close");
    			            	DOCUMENTLOOKUP.doFunctionBinding();
    			            	DOCUTILS.documentLink();
    			            }
    			    } );
            	},
            	
            	
            	
            	
            	
        	};
        	
        	DOCUMENTLOOKUP.init();
        	
        	
        });
        		
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1><bean:message key="page.label.document" /> <bean:message key="menu.label.lookup" /></h1> 

    	
		<%--    	
    	<c:if test="${not empty ANSI_JOB_ID}">
    		<span class="orange"><bean:message key="field.label.jobFilter" />: <c:out value="${ANSI_JOB_ID}" /></span><br />
    	</c:if>
    	<c:if test="${not empty ANSI_DIVISION_ID}">
    		<span class="orange"><bean:message key="field.label.divisionFilter" />: <c:out value="${ANSI_DIVISION_ID}" /></span><br />
    	</c:if>
    	 --%>
    	  	
	 	<webthing:lookupFilter filterContainer="filter-container" />
		<div id="table-container">
		 	<table id="document-lookup-table" class="display" cellspacing="0" style="table-layout: fixed; font-size:9pt;min-width:1300px; max-width:1300px;width:1300px;">
		        <thead></thead>
		        <tbody></tbody>
		        <tfoot></tfoot>
		    </table>
		    <input type="button" class="prettyWideButton" id="new-document-button" value="New" />
	    </div>
	    
	    <webthing:scrolltop />
	
	    <webthing:documentModals />


		
    </tiles:put>
		
</tiles:insert>

