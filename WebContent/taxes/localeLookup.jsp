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
        Locale Lookup
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
       	<link rel="stylesheet" href="css/lookup.css" />
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/lookup.js"></script> 
    	
    
        <style type="text/css">
			#displayTable {
				width:100%;
			}
			#addFormDiv {
				display:none;
				background-color:#FFFFFF;
				color:#000000;
				width:400px;
				padding:15px;
			}
			#filter-container {
        		width:402px;
        		float:right;
        	}
        	
			.prettyWideButton {
				height:30px;
				min-height:30px;
			}
			select	{
				width:80px !important;
				max-width:80px !important;
			}
			.print-link {
				cursor:pointer;
			}
			.editJob {
				cursor:pointer;
				text-decoration:underline;
			}
			.jobLink {
				color:#000000;
			}
			.overrideAction {
				cursor:pointer;
				text-decoration:none;
				color:#000000;
			}
			.dataTables_wrapper {
				padding-top:10px;
			}
			#ticket-modal {
				display:none;	
			}
			.ticket-clicker {
				color:#000000;
			}
        </style>
        
        <script type="text/javascript">    
        
        $(document).ready(function(){
        	
        	LOCALELOOKUP = {
                dataTable : null,

       			init : function() {
       				LOCALELOOKUP.createTable();  
       				LOCALELOOKUP.makeClickers();
                }, 
                
                
                createTable : function(){
            		var dataTable = $('#localeTable').DataTable( {
            			"aaSorting":		[[2,'asc']],
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
            	        	'pageLength','copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print',{extend: 'colvis',	label: function () {doFunctionBinding();}}
            	        ],
            	        
            	        "columnDefs": [
             	            { "orderable": false, "targets": -1 },
            	            { className: "dt-head-left", "targets": [0,1] },
            	            { className: "dt-body-center", "targets": [2,3,5] },
            	            { className: "dt-right", "targets": [4]}
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": "localeLookup",
    			        	"type": "GET",
    			        	"data": {}
    			        	},
    			        columns: [
    			            { width:"5%", title: "<bean:message key="field.label.localeId" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.locale_id != null){return ('<a href="#" data-id="'+row.locale_id+'" class="ticket-clicker">'+row.locale_id+'</a>');}
    			            } },
    			            { width:"20%", title: "<bean:message key="field.label.name" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.name != null){return ('<span class="tooltip">' + row.name+'<span class="tooltiptext">'+row.name+'</span></span>');}
    			            } },
    			            { width:"5%", title: "<bean:message key="field.label.state" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.state_name != null){return (row.state_name+"");}
    			            } },
    			            { width:"5%", title: "<bean:message key="field.label.abbreviation" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.abbreviation != null){return (row.abbreviation);}
    			            } },
    			            { width:"10%", title: "<bean:message key="field.label.localeType" />" , "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.locale_type_id != null){return (row.locale_type_id+"");}
    			            } },
    			            
    			            { width:"5%", title: "<bean:message key="field.label.action" />",  data: function ( row, type, set ) {	
    			            	//console.log(row);
    			            	var $actionData = "";
    			            	if ( row.locale_id != null ) {
//    				            	var $editLink = '<ansi:hasPermission permissionRequired="TAX_WRITE"><a href="localeReturn.html?id='+row.locale_id+'" class="editAction" data-id="'+row.locale_id+'"><webthing:edit>Edit</webthing:edit></a></ansi:hasPermission>&nbsp;';
//    				            	
//    		            			var $ticketData = 'data-id="' + row.locale_id + '"';
//    			            		$printLink = '<ansi:hasPermission permissionRequired="TAX_READ"><i class="print-link fa fa-print" aria-hidden="true" ' + $localeData + '></i></ansi:hasPermission>'
//    			            		var $claimLink = '';
//    			            		
//    				            	$actionData = $editLink + $printLink;
    			            	}
    			            	return $actionData;
    			            } }],
    			            "initComplete": function(settings, json) {
    			            	//console.log(json);
    			            	//doFunctionBinding();
    			            	var myTable = this;
    			            	LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#localeTable", LOCALELOOKUP.createTable);
    			            },
    			            "drawCallback": function( settings ) {
    			            	LOCALELOOKUP.doFunctionBinding();
    			            }
    			    } );
            	},
            	
            	
            	doFunctionBinding : function() {
    				$( ".editAction" ).on( "click", function($clickevent) {
    					 doEdit($clickevent);
    				});					
    				$(".print-link").on( "click", function($clickevent) {
    					doPrint($clickevent);
    				});
    				$(".locale-clicker").on("click", function($clickevent) {
    					$clickevent.preventDefault();
    					var $localeId = $(this).attr("data-id");
//    					TICKETUTILS.doTicketViewModal("#ticket-modal",$ticketId);
    					$("#locale-modal").dialog("open");
    				});

    			},
            	
            	
            	makeClickers : function() {
            		$('.ScrollTop').click(function() {
        				$('html, body').animate({scrollTop: 0}, 800);
        				return false;
               	    });
            	},
            	    
        	};
        
        	LOCALELOOKUP.init();
        	
				
			function doEdit($clickevent) {
				var $rowid = $clickevent.currentTarget.attributes['data-id'].value;
					var $url = 'localeTable/' + $rowid;
					//console.log("YOU PASSED ROW ID:" + $rowid);
					var jqxhr = $.ajax({
						type: 'GET',
						url: $url,
						success: function($data) {
							//console.log($data);
							
			        		$("#localeId").val(($data.data.codeList[0]).localeId);
			        		$("#name").val(($data.data.codeList[0]).name);
			        		$("#stateName").val(($data.data.codeList[0]).stateName);
			        		$("#abbreviation").val(($data.data.codeList[0]).abbreviation);
			        		$("#localeTypeId").val(($data.data.codeList[0]).localeTypeId);
			        		
			        		$("#updateOrAdd").val("update");
			        		$("#addLocaleForm").dialog( "open" );
						},
						statusCode: {
							403: function($data) {
								$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
							} 
						},
						dataType: 'json'
					});
				//console.log("Edit Button Clicked: " + $rowid);
			}
				
				
				
			function doPrint($clickevent) {
				var $localeId = $clickevent.currentTarget.attributes['data-id'].value;
				console.debug("ROWID: " + $localeId);
				var a = document.createElement('a');
                var linkText = document.createTextNode("Download");
                a.appendChild(linkText);
                a.title = "Download";
                a.href = "ticketPrint/" + $localeId;
                a.target = "_new";   // open in a new window
                document.body.appendChild(a);
                a.click();				
			}
        });
        		
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1><bean:message key="page.label.locale" /> <bean:message key="menu.label.lookup" /></h1> 
    	<c:if test="${not empty ANSI_JOB_ID}">
    		<span class="orange"><bean:message key="field.label.jobFilter" />: <c:out value="${ANSI_JOB_ID}" /></span><br />
    	</c:if>
    	<c:if test="${not empty ANSI_DIVISION_ID}">
    		<span class="orange"><bean:message key="field.label.divisionFilter" />: <c:out value="${ANSI_DIVISION_ID}" /></span><br />
    	</c:if>
    	<c:if test="${not empty ANSI_TICKET_LOOKUP_START_DATE}">
    		<span class="orange"><bean:message key="field.label.startDate" />: <c:out value="${ANSI_TICKET_LOOKUP_START_DATE}" /></span><br />
    	</c:if>
    	<c:if test="${not empty ANSI_TICKET_LOOKUP_STATUS}">
    		<span class="orange"><bean:message key="field.label.statusFilter" />: <c:out value="${ANSI_TICKET_LOOKUP_STATUS}" /></span><br />
    	</c:if>
    	  	
    	  	
 	<webthing:lookupFilter filterContainer="filter-container" />

 	<table id="localeTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:1300px;width:1300px;">
        <thead>
        </thead>
        <tfoot>
        </tfoot>
    </table>
    
    <webthing:scrolltop />


    </tiles:put>
		
</tiles:insert>

