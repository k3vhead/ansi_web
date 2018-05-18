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
        
        $(document).ready(function(){
      	  	$('.ScrollTop').click(function() {
				$('html, body').animate({scrollTop: 0}, 800);
      	  		return false;
      	    });

        	var dataTable = null;
        	
        	function createTable(){
        		var dataTable = $('#jobTable').DataTable( {
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
        	            { className: "dt-left", "targets": [0,2,3,4,5,6,10,11] },
        	            { className: "dt-center", "targets": [1,7,9,12,-1] },
        	            { className: "dt-right", "targets": [8]}
        	         ],
        	        "paging": true,
			        "ajax": {
			        	"url": "jobTable",
			        	"type": "GET"
			        	},
			        columns: [
			        	
			            { title: "<bean:message key="field.label.jobId" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.jobId != null){return (row.jobId+"");}
			            } },
			            { title: "<bean:message key="field.label.quoteName" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.quoteId != null){return ('<ansi:hasPermission permissionRequired="QUOTE"><a href="quoteMaintenance.html?id='+ row.quoteId+ '" style="color:#404040"></ansi:hasPermission>' + row.quoteNumber + row.revision +'<ansi:hasPermission permissionRequired="QUOTE"></ansi:hasPermission>');}
			            } },
			            { title: "<bean:message key="field.label.jobStatus" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.jobStatus != null){return (row.jobStatus+"");}
			            } },
			            { title: "<bean:message key="field.label.divisionNbr" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.divisionNbr != null){return (row.divisionNbr+"-"+row.divisionCode);}
			            } },
			            { title: "<bean:message key="field.label.billToName" />" , "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.billToName != null){return (row.billToName+"");}
			            } },
			            { title: "<bean:message key="field.label.jobSiteName" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.jobSiteName != null){return (row.jobSiteName+"");}
			            } },
			            { title: "<bean:message key="field.label.jobSiteAddress" />",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.jobSiteAddress != null){return (row.jobSiteAddress+", " + row.jobSiteCity + ", " + row.jobSiteState );}
			            } },
			            { title: "<bean:message key="field.label.startDate" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.startDate != null){return (row.startDate+"");}
			            } },
			            { title: "<bean:message key="field.label.jobFrequency" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.jobFrequency != null){return (row.jobFrequency+"");}
			            } },
			            { title: "<bean:message key="field.label.pricePerCleaning" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.pricePerCleaning != null){return (row.pricePerCleaning+"");}
			            } },
			            { title: "<bean:message key="field.label.jobNbr" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) { 	
			            	if(row.jobNbr != null){return (row.jobNbr+"");}
			            } },
			            { title: "<bean:message key="field.label.serviceDescription" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.serviceDescription != null){return (row.serviceDescription+"");}
			            } },
			            { title: "<bean:message key="field.label.poNumber" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.poNumber != null){return (row.poNumber+"");}
			            } },
			            { title: "<bean:message key="field.label.action" />",  data: function ( row, type, set ) {	
			            	//console.log(row);
			            	{return "<ansi:hasPermission permissionRequired='SYSADMIN'><ansi:hasWrite><a href='jobMaintenance.html?id="+row.jobId+"' class=\"editAction fas fa-pencil-alt\" data-id='"+row.jobId+"'></a></ansi:hasWrite></ansi:hasPermission>";}
			            	
			            } }],
			            "initComplete": function(settings, json) {
			            	//console.log(json);
			            	doFunctionBinding();
			            },
			            "drawCallback": function( settings ) {
			            	doFunctionBinding();
			            }
			    } );
        		//new $.fn.dataTable.FixedColumns( dataTable );
        	}
        	        	
        	init();
        			
            
            function init(){
					$.each($('input'), function () {
				        $(this).css("height","20px");
				        $(this).css("max-height", "20px");
				    });
					
					createTable();
            }; 
				
				function doFunctionBinding() {
					$( ".editAction" ).on( "click", function($clickevent) {
						 doEdit($clickevent);
					});
				}
				
				function doEdit($clickevent) {
					var $rowid = $clickevent.currentTarget.attributes['data-id'].value;

						var $url = 'jobTable/' + $rowid;
						//console.log("YOU PASSED ROW ID:" + $rowid);
						var jqxhr = $.ajax({
							type: 'GET',
							url: $url,
							success: function($data) {
								//console.log($data);
								
				        		$("#jobId").val(($data.data.codeList[0]).jobId);
				        		$("#jobStatus").val(($data.data.codeList[0]).jobStatus);
				        		$("#divisionNbr").val(($data.data.codeList[0]).divisionNbr);
				        		$("#billToName").val(($data.data.codeList[0]).billToName);
				        		$("#jobSiteName").val(($data.data.codeList[0]).jobSiteName);
				        		$("#jobSiteAddress").val(($data.data.codeList[0]).jobSiteAddress);
				        		$("#startDate").val(($data.data.codeList[0]).startDate);
				        		$("#jobFrequency").val(($data.data.codeList[0]).startDate);
				        		$("#pricePerCleaning").val(($data.data.codeList[0]).pricePerCleaning);
				        		$("#jobNbr").val(($data.data.codeList[0]).jobNbr);
				        		$("#serviceDescription").val(($data.data.codeList[0]).serviceDescription);
				        		$("#poNumber").val(($data.data.codeList[0]).processDate);
				        		
				        		$("#jId").val(($data.data.codeList[0]).jobId);
				        		$("#updateOrAdd").val("update");
				        		$("#addJobTableForm").dialog( "open" );
							},
							statusCode: {
								403: function($data) {
									$("#useridMsg").html("Session Timeout. Log in and try again");
								} 
							},
							dataType: 'json'
						});
					//console.log("Edit Button Clicked: " + $rowid);
				}
        });
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Job Lookup</h1>
    	
 	<table id="jobTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:1300px;width:1300px;">
       	<colgroup>
        	<col style="width:4%;" />
        	<col style="width:4%;" />
    		<col style="width:5%;" />    		
    		<col style="width:6%;" />
    		<col style="width:10%;" />
    		<col style="width:10%;" />
    		<col style="width:10%;" />
    		<col style="width:6%;" />
    		<col style="width:5%;" />
    		<col style="width:7%;" />
    		<col style="width:5%;" />
    		<col style="width:24%;" />
    		<col style="width:4%;" />
    		<col style="width:6%;" />
   		</colgroup>
        <thead>
            <tr>
                <th><bean:message key="field.label.jobId" /></th>
                <th><bean:message key="field.label.quoteName" /></th>
    			<th><bean:message key="field.label.jobStatus" /></th>
    			<th><bean:message key="field.label.divisionNbr" /></th>
    			<th><bean:message key="field.label.billToName" /></th>
    			<th><bean:message key="field.label.jobSiteName" /></th>
    			<th><bean:message key="field.label.jobSiteAddress" /></th>
    			<th><bean:message key="field.label.startDate" /></th>
    			<th><bean:message key="field.label.jobFrequency" /></th>
    			<th><bean:message key="field.label.pricePerCleaning" /></th>
    			<th><bean:message key="field.label.jobNbr" /></th>
    			<th><bean:message key="field.label.serviceDescription" /></th>
    			<th><bean:message key="field.label.poNumber" /></th>
    			<th><bean:message key="field.label.action" /></th>
            </tr>
        </thead>
        <tfoot>
            <tr>
                <th><bean:message key="field.label.jobId" /></th>
                <th><bean:message key="field.label.quoteName" /></th>
    			<th><bean:message key="field.label.jobStatus" /></th>
    			<th><bean:message key="field.label.divisionNbr" /></th>
    			<th><bean:message key="field.label.billToName" /></th>
    			<th><bean:message key="field.label.jobSiteName" /></th>
    			<th><bean:message key="field.label.jobSiteAddress" /></th>
    			<th><bean:message key="field.label.startDate" /></th>
    			<th><bean:message key="field.label.jobFrequency" /></th>
    			<th><bean:message key="field.label.pricePerCleaning" /></th>
    			<th><bean:message key="field.label.jobNbr" /></th>
    			<th><bean:message key="field.label.serviceDescription" /></th>
    			<th><bean:message key="field.label.poNumber" /></th>
    			<th><bean:message key="field.label.action" /></th>
            </tr>
        </tfoot>
    </table>
    
    <webthing:scrolltop />
    
    </tiles:put>
		
</tiles:insert>

