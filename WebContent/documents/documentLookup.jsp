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
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/addressUtils.js"></script>
    	<script type="text/javascript" src="js/lookup.js"></script> 
    	<script type="text/javascript" src="js/ticket.js"></script> 
    
        <style type="text/css">
        	#new-document-modal {
        		display:none;
        	}
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
        		docTypeList : [],
        		
        		divisionFilter : '<c:out value="${ANSI_DIVISION_ID}" />',	// col 1
				jobFilter : '<c:out value="${ANSI_JOB_ID}" />', 			// col 7
				ticketFilter : '<c:out value="${ANSI_TICKET_ID}" />',   	//col 0
				washerFilter : '<c:out value="${ANSI_WASHER_ID}" />',		//col 9

        		
        		
        		init : function() {
        			DOCUMENTLOOKUP.makeTable();
        			DOCUMENTLOOKUP.makeModals();        			
        			DOCUMENTLOOKUP.makeClickers();
        			ANSI_UTILS.getOptionList("DOCUMENT_TYPE",DOCUMENTLOOKUP.makeDocTypeList);
        		},
        		
        		
        		
        		doFunctionBinding : function() {
    				//$(".ticket-clicker").on("click", function($clickevent) {
    				//	$clickevent.preventDefault();
    				//	var $ticketId = $(this).attr("data-id");
    				//	TICKETUTILS.doTicketViewModal("#ticket-modal",$ticketId);
    				//	$("#ticket-modal").dialog("open");
    				//});

    			},
    			
    			
    			
    			
    			
    			doSaveDocument : function() {
    				console.log("doSaveDocument");
    				var formData = new FormData();
    				var $file = $("#new-document-modal input[name='fileSelect']").val();
    				console.log($file);
    				formData.append('fileSelect',$file, $file.name);
    			},
    			
    			
    			
    			

        		makeClickers : function() {
        			$('.ScrollTop').click(function() {
        				$('html, body').animate({scrollTop: 0}, 800);
        				return false;
               	    });
        			
        			$("#new-document-button").click(function() {
        				$("#new-document-modal .err").html("");
        				$("#new-document-modal").dialog("open");
        			});
        		},
        		
        		
        		makeDocTypeList : function($data) {
        			console.log("makeDocTypeList");
        			var $select = $("#new-document-modal select[name='documentType']");
					$('option', $select).remove();
					$select.append(new Option("",""));
					$.each($data.documentType, function(index, val) {
					    $select.append(new Option(val.display, val.code));
					});	
        		},
        		
        		
        		
        		
        		makeModals : function() {
        			$("#new-document-modal" ).dialog({
						title:'New Document',
						autoOpen: false,
						height: 400,
						width: 600,
						modal: true,
						buttons: [
							{
								id: "new-document-cancel-button",
								click: function() {
									$("#new-document-modal").dialog( "close" );
								}
							},{
								id: "new-document-go-button",
								click: function($event) {
									console.log("Saving a new document");
									DOCUMENTLOOKUP.doSaveDocument();
								}
							}	      	      
						],
						close: function() {
							$("#new-document-modal").dialog( "close" );
							//allFields.removeClass( "ui-state-error" );
						}
					});        			
        			$('#new-document-cancel-button').button('option', 'label', 'Cancel');
        			$('#new-document-go-button').button('option', 'label', 'Save');
        				
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
    			            	//var $assign ='<ansi:hasPermission permissionRequired="TICKET_WRITE"><a href="ticketAssignment.html?ticketId='+row.view_ticket_id+'&divisionId='+row.division_id+'"><webthing:assign styleClass="orange">Assign Ticket</webthing:assign></a></ansi:hasPermission>';
    			            	//var $claimTkt = "";
    			            	//var $claimWasher = "";
    			            	
    			            	return '<a href="#" class="viewAction" data-id="'+row.xref_id+'"><webthing:view>View</webthing:view></a>';
    			            } },
    			            { width:"25%", title: " ", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	return ' ';
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
	
	    <div id="new-document-modal">
    		<table>
    			<tr>
    				<td><span class="form-label">ID:</span></td>
    				<td><input type="hidden" name="documentId" /><span class="documentId"></span></td>
    				<td><span class="documentId-err err"></span></td>
    			</tr>
    			<tr>
    				<td><span class="form-label">Description:</span></td>
    				<td><input type="text" name="description" /></td>
    				<td><span class="description-err err"></span></td>
    			</tr>
    			<tr>
    				<td><span class="form-label">Document Date:</span></td>
    				<td><input type="date" name="documentDate" /></td>
    				<td><span class="documentDate-err err"></span></td>
    			</tr>
    			<tr>
    				<td><span class="form-label">Expiration Date:</span></td>
    				<td><input type="date" name="expirationDate" /></td>
    				<td><span class="expirationDate-err err"></span></td>
    			</tr>
    			<tr>
    				<td><span class="form-label">Document Type:</span></td>
    				<td><select name="documentType"></select></td>
    				<td><span class="documentType-err err"></span></td>
    			</tr>
    			<tr>
    				<td><span class="form-label">Xref:</span></td>
    				<td><input type="text" name="xrefId" /></td>
    				<td><span class="xrefId-err err"></span></td>
    			</tr>
    			<tr>
    				<td><span class="form-label">File:</span></td>
    				<td><input type="file" name="fileSelect" id="fileSelect" /></td>
    				<td><span class="fileSelect-err err"></span></td>
    			</tr>
    		</table>
    	</div>

    </tiles:put>
		
</tiles:insert>

