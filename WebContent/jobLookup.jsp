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
        Job Lookup
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
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
			
        </style>
        
        <script type="text/javascript">
        
        $(document).ready(function() {
        	;JOBLOOKUP = {
       			dataTable : null,
       			lookupType : '<c:out value="${ANSI_JOB_LOOKUP_TYPE}" />',
       			pacColumns : [1,2,3],
       			contactColumns : [4,5,6],
       			jobColumns : [7,8,9],
       			
       			
       			
        		init : function() {
    				$.each($('input'), function () {
						$(this).css("height","20px");
						$(this).css("max-height", "20px");
					});
    					
    				$('.ScrollTop').click(function() {
    					$('html, body').animate({scrollTop: 0}, 800);
    	      	  		return false;
    	      	    });
    				
    				
					JOBLOOKUP.createTable();
                },
                
                
                
                createTable : function() {
            		var dataTable = $('#jobTable').DataTable( {
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
            	        	'pageLength','copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print',{extend: 'colvis',	label: function () {doFunctionBinding();$('#jobTable').draw();}}
            	        ],
            	        "columnDefs": [
             	            { "orderable": false, "targets": -1 },
            	            { className: "dt-left", "targets": [4,5,6,11] },
            	            { className: "dt-center", "targets": [0,1,2,3,7,8,10,12,-1] },
            	            { className: "dt-right", "targets": [9]}
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": "jobTable",
    			        	"type": "GET"
    			        	},
    			        columns: [
    			        	// if you change any of these, you'll need to change the jobColumns/pacColumns/contactColumns lists also
    			            { width: "6%", title: "<bean:message key="field.label.jobId" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
    			            	if(row.jobId != null){return (row.jobId+"");}
    			            } },
    			            { width: "6%", title: "<bean:message key="field.label.quoteName" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.quoteId != null){return ('<ansi:hasPermission permissionRequired="QUOTE"><a href="quoteMaintenance.html?id='+ row.quoteId+ '" style="color:#404040"></ansi:hasPermission>' + row.quoteNumber + row.revision +'<ansi:hasPermission permissionRequired="QUOTE"></ansi:hasPermission>');}
    			            } },
    			            { width: "4%", title: "<bean:message key="field.label.jobStatus" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.jobStatus != null){return (row.jobStatus+"");}
    			            } },
    			            { width: "6%", title: "<bean:message key="field.label.divisionNbr" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.divisionNbr != null){return (row.divisionNbr+"-"+row.divisionCode);}
    			            } },
    			            { width: "10%", title: "<bean:message key="field.label.billToName" />" , "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
    			            	if(row.billToName != null){return (row.billToName+"");}
    			            } },
    			            { width: "10%", title: "<bean:message key="field.label.jobSiteName" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.jobSiteName != null){return (row.jobSiteName+"");}
    			            } },
    			            { width: "10%", title: "<bean:message key="field.label.jobSiteAddress" />",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.jobSiteAddress != null){return (row.jobSiteAddress+", " + row.jobSiteCity + ", " + row.jobSiteState );}
    			            } },
    			            { width: "4%", title: "<bean:message key="field.label.startDate" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.startDate != null){return (row.startDate+"");}
    			            } },
    			            { width: "4%", title: "<bean:message key="field.label.jobFrequency" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.jobFrequency != null){return (row.jobFrequency+"");}
    			            } },
    			            { width: "4%", title: "<bean:message key="field.label.pricePerCleaning" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.pricePerCleaning != null){return (row.pricePerCleaning+"");}
    			            } },
    			            { width: "4%", title: "<bean:message key="field.label.jobNbr" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) { 	
    			            	if(row.jobNbr != null){return (row.jobNbr+"");}
    			            } },
    			            { width: "24%", title: "<bean:message key="field.label.serviceDescription" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.serviceDescription != null){return (row.serviceDescription+"");}
    			            } },
    			            { width: "4%", title: "<bean:message key="field.label.poNumber" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.poNumber != null){return (row.poNumber+"");}
    			            } },
    			            { width: "4%", title: "<bean:message key="field.label.action" />",  data: function ( row, type, set ) {	
    			            	//console.log(row);
    			            	{
    				            	var $edit = '<a href="jobMaintenance.html?id='+row.jobId+'" class="editAction" data-id="'+row.jobId+'"><webthing:edit>View</webthing:edit></a>';
    			            		return "<ansi:hasPermission permissionRequired='QUOTE_READ'>"+$edit+"</ansi:hasPermission>";
    			            		//return "<ansi:hasPermission permissionRequired='SYSADMIN'><ansi:hasWrite><a href='jobMaintenance.html?id="+row.jobId+"' class=\"editAction fas fa-pencil-alt\" data-id='"+row.jobId+"'></a></ansi:hasWrite></ansi:hasPermission>";
    			            	}
    			            	
    			            } }],
    			            "initComplete": function(settings, json) {
    			            	//console.log(json);
    			            	JOBLOOKUP.doFunctionBinding();
    			            },
    			            "drawCallback": function( settings ) {
    			            	JOBLOOKUP.doFunctionBinding();
    			            }
    			    } );
            	},
            	
				
            	
				doFunctionBinding : function() {
					if ( JOBTABLE.lookupType == 'JOB' ) {
						JOBTABLE.showJobColumns();
					}
				},
				
				
				showJobColumns : function() {
					var myTable = $('#jobTable').DataTable();
					$.each(pacColumns, function(index, columnNumber) {
						myTable.columns(columnNumber).visible(false);
					});
					$.each(contactColumns, function(index, columnNumber) {
						myTable.columns(columnNumber).visible(false);
					});
					$.each(jobColumns, function(index, columnNumber) {
						myTable.columns(columnNumber).visible(true);
					});
				}
        	} 
        	
        	JOBLOOKUP.init();
        			
        });
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Job Lookup</h1>
    	    	
	 	<table id="jobTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:1300px;width:1300px;">
	       	
	    </table>
	    
	    <webthing:scrolltop />
    
    </tiles:put>
		
</tiles:insert>

