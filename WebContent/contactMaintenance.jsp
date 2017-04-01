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
        Contact <bean:message key="menu.label.maintenance" />
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
        		var dataTable = $('#contactTable').DataTable( {
        			"processing": 		true,
        	        "serverSide": 		true,
        	        "autoWidth": 		false,
        	        "deferRender": 		true,
        	        "scrollCollapse": 	true,
        	        "scrollX": 			true,
        	        rowId: 				'dt_RowId',
        	        dom: 				'Bfrtip',
        	        "searching": 		true,
        	        lengthMenu: [
        	        	[ 10, 50, 100, 500, 1000 ],
        	            [ '10 rows', '50 rows', '100 rows', '500 rows', '1000 rows' ]
        	        ],
        	        buttons: [
        	        	'pageLength','copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print',{extend: 'colvis',	label: function () {doFunctionBinding();}}
        	        ],
        	        "columnDefs": [
//         	            { "orderable": false, "targets": -1 },  // Need to re-add this when we add the action column back in
        	            { className: "dt-left", "targets": [0,1,2,3,4] },
        	            { className: "dt-center", "targets": [5] }
//        	            { className: "dt-right", "targets": [5,6,7,8,9]}
        	         ],
        	        "paging": true,
			        "ajax": {
			        	"url": "contactLookup",
			        	"type": "GET"
			        	},
			        columns: [
			        	
			            { title: "Last Name", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.lastName != null){return (row.lastName+"");}
			            } },
			            { title: "First Name", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.firstName != null){return (row.firstName+"");}
			            } },
			            { title: "Business Phone", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.businessPhone != null){return (row.businessPhone+"");}
			            } },
			            { title: "Fax" , "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.fax != null){return (row.fax+"");}
			            } },
			            { title: "Mobile" , "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.mobilePhone != null){return (row.mobilePhone+"");}
			            } },
			            { title: "Action",  data: function ( row, type, set ) {	
			            	{return "<ansi:hasPermission permissionRequired='SYSADMIN'><ansi:hasWrite><a href='jobMaintenance.html?id="+row.jobId+"' class=\"editAction ui-icon ui-icon-pencil\" data-id='"+row.jobId+"'></a></ansi:hasWrite></ansi:hasPermission>";}
			            } }
			            ],
			            "initComplete": function(settings, json) {
			            	//console.log(json);
			            	doFunctionBinding();
			            },
			            "drawCallback": function( settings ) {
			            	doFunctionBinding();
			            }
			    } );
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

					var $url = 'invoiceLookup/' + $rowid;
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
			        		$("#addinvoiceTableForm").dialog( "open" );
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
    	<h1>Contact <bean:message key="menu.label.lookup" /></h1>
    	
 	<table id="contactTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:800px;width:800px;">
       	<colgroup>
        	<col style="width:20%;" />
        	<col style="width:20%;" />
        	<col style="width:15%;" />
        	<col style="width:15%;" />
        	<col style="width:15%;" />
        	<col style="width:15%;" />
   		</colgroup>
        <thead>
            <tr>
                <th>Last Name</th>
    			<th>First Name</th>
    			<th>Business Phone</th>
    			<th>Fax</th>
    			<th>Mobile Phone</th>
    			<th>Action</th>
            </tr>
        </thead>
        <tfoot>
            <tr>
                <th>Last Name</th>
    			<th>First Name</th>
    			<th>Business Phone</th>
    			<th>Fax</th>
    			<th>Mobile Phone</th>
    			<th>Action</th>
            </tr>
        </tfoot>
    </table>
    
    <p align="center">
    	<br>
    	<a href="#" title="Scroll to Top" class="ScrollTop">Scroll To Top</a>
    	</br>
    </p>
    
    </tiles:put>
		
</tiles:insert>

