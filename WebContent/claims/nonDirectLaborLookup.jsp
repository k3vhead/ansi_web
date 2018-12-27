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
        Non-Direct Labor
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
        		var dataTable = $('#displayTable').DataTable( {
        			"aaSorting":		[[0,'asc']],
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
        	        	'pageLength','copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print',{extend: 'colvis',	label: function () {doFunctionBinding();$('#displayTable').draw();}}
        	        ],
        	        "columnDefs": [
         	            { "orderable": false, "targets": -1 },
        	            { className: "dt-left", "targets": [4,5,6,11] },
        	            { className: "dt-center", "targets": [0,1,2,3,7,8,10,12,-1] },
        	            { className: "dt-right", "targets": [9]}
        	         ],
        	        "paging": true,
			        "ajax": {
			        	"url": "claims/nonDirectLaborLookup",
			        	"type": "GET"
			        	},
			        columns: [
			        	
			            { title: "Div", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.division != null){return (row.division+"");}
			            } },
			            { title: "Week", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.week != null){return (row.week);}
			            } },
			            { title: "Date", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.workDate != null){return (row.workDate+"");}
			            } },
			            { title: "Washer", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.washer_id != null){return (row.lastName+", "+row.firstName);}
			            } },
			            { title: "Hrs Type" , "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.hoursType != null){return (row.hoursType+"");}
			            } },
			            { title: "Hours", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.hours != null){return (row.hours+"");}
			            } },
			            { title: "Notes",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.notes != null){return (row.notes+"");}
			            } },			            
			            { title: "<bean:message key="field.label.action" />",  data: function ( row, type, set ) {	
			            	//console.log(row);
			            	{
				            	var $edit = '<a href="jobMaintenance.html?id='+row.jobId+'" class="editAction" data-id="'+row.jobId+'"><webthing:edit>View</webthing:edit></a>';
			            		return "<ansi:hasPermission permissionRequired='QUOTE_READ'>"+$edit+"</ansi:hasPermission>";
			            		//return "<ansi:hasPermission permissionRequired='SYSADMIN'><ansi:hasWrite><a href='jobMaintenance.html?id="+row.jobId+"' class=\"editAction fas fa-pencil-alt\" data-id='"+row.jobId+"'></a></ansi:hasWrite></ansi:hasPermission>";
			            	}
			            	
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
    	<h1>Non Direct Labor</h1>
    	
 	<table id="displayTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:1300px;width:1300px;">
       	<colgroup>
        	<col style="width:5%;" />
        	<col style="width:5%;" />
    		<col style="width:5%;" />    		
    		<col style="width:10%;" />
    		<col style="width:10%;" />
    		<col style="width:5%;" />
    		<col style="width:50%;" />
    		<col style="width:10%;" />
   		</colgroup>
        <thead>
            <tr>
                <th>Div</th>
                <th>Week</th>
    			<th>Date</th>
    			<th>Washer</th>
    			<th>Hours Type</th>
    			<th>Hours</th>
    			<th>Notes</th>
    			<th>Action</th>    			
            </tr>
        </thead>
        <tfoot>
            <tr>
                <th>Div</th>
                <th>Week</th>
    			<th>Date</th>
    			<th>Washer</th>
    			<th>Hours Type</th>
    			<th>Hours</th>
    			<th>Notes</th>   
    			<th>Action</th>  			
            </tr>
        </tfoot>
    </table>
    
    <webthing:scrolltop />
    
    </tiles:put>
		
</tiles:insert>

