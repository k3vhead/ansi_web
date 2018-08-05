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
        <bean:message key="field.label.userLookupTitle" />
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
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
        </style>
        
        <script type="text/javascript">    
        
        $(document).ready(function(){
        	
        	;USERLOOKUP = {
        		permissionGroupId : '<c:out value="${ANSI_PERMISSION_GROUP_ID}" />',
        		permissionGroupName : '<c:out value="${ANSI_PERMISSION_GROUP_NAME}" />',
        		datatable : null,
        		
        		init : function() {
        			USERLOOKUP.enableClicks();
        			USERLOOKUP.createTable();
        		},
        		
        		doFunctionBinding : function() {
					$( ".editAction" ).on( "click", function($clickevent) {
						 doEdit($clickevent);
					});					
					$(".print-link").on( "click", function($clickevent) {
						doPrint($clickevent);
					});
					//$(".editJob").on( "click", function($clickevent) {
					//	console.debug("clicked a job")
					//	var $jobId = $(this).data("jobid");
					//	location.href="jobMaintenance.html?id=" + $jobId;
					//});
				},
				
        		enableClicks : function() {
        			$('.ScrollTop').click(function() {
        				$('html, body').animate({scrollTop: 0}, 800);
        				return false;
               	    });
        			
        			$("#clearFilter").click(function($event) {
        				USERLOOKUP.permissionGroupId='';
        				USERLOOKUP.permissionGroupName='';
        				$("#filterLabel").html('');
        				USERLOOKUP.dataTable.destroy();
        				USERLOOKUP.createTable();
        			});
        		},
        		
        		
        		createTable : function() {
            		USERLOOKUP.dataTable = $('#userTable').DataTable( {
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
            	        	'pageLength','copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print',{extend: 'colvis',	label: function () {USERLOOKUP.doFunctionBinding();}}
            	        ],
            	        
            	        "columnDefs": [
             	            { "orderable": false, "targets": -1 },
            	            { className: "dt-head-left", "targets": [1,2,3,4,5,6] },
            	            { className: "dt-body-center", "targets": [0,7,8] },
            	            { className: "dt-right", "targets": []}
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": "userLookup",
    			        	"type": "GET",
    			        	"data": {"permissionGroupId":USERLOOKUP.permissionGroupId}
    			        	},
    			        aaSorting:[1],
    			        columns: [
    			        	{ title: "<bean:message key="field.label.userId" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
    			            	if(row.userId != null){return (row.userId+"");}
    			            } },
    			            { title: "<bean:message key="field.label.lastName" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.lastName != null){return (row.lastName+"");}
    			            } },
    			            { title: "<bean:message key="field.label.firstName" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.firstName != null){return (row.firstName+"");}
    			            } },
    			            { title: "<bean:message key="field.label.email" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.email != null){return (row.email+"");}
    			            } },
    			            { title: "<bean:message key="field.label.phone" />" , "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
    			            	if(row.phone != null){return (row.phone+"");}
    			            } },
    			            { title: "<bean:message key="field.label.cityUL" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.city != null){return (row.city+", " + row.state);}
    			            } },
    			            { title: "<bean:message key="field.label.permissionGroupName" />",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.permissionGroupName != null){return (row.permissionGroupName+"");}
    			            } },
    			            { title: "<bean:message key="field.label.status" />",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	var status = '<span style="font-style:italic;">N/A</span>';    			            	
    			            	if(row.userStatus != null){
    			            		if ( row.userStatus == 1 ) {
    			            			status = '<webthing:checkmark>Active</webthing:checkmark>';
    			            		}
    			            		if ( row.userStatus == 0 ) {
    			            			status = '<webthing:ban>Inactive</webthing:ban>';
    			            		}
    			            	}
    			            	return status;
    			            } },
    			            { title: "<bean:message key="field.label.action" />",  data: function ( row, type, set ) {	
    			            	//console.log(row);
    			            	if ( row.ticketId == null ) {
    			            		$actionData = "";
    			            	} else {
    				            	//var $editLink = "<a href='ticketReturn.html?id="+row.ticketId+"' class=\"editAction fas fa-pencil-alt\" data-id='"+row.ticketId+"'></a>";
    				            	var $editLink = '<a href="ticketReturn.html?id='+row.ticketId+'" class="editAction" data-id="'+row.ticketId+'"><webthing:edit>Edit</webthing:edit></a>';
    				            	//var $overrideLink = "<a href='ticketOverride.html?id="+row.ticketId+"' class=\"overrideAction fa fa-magic\" data-id='"+row.ticketId+"'></a>";
    				            	if ( row.ticketStatus == 'F' ) {
    				            		var $overrideLink = "";
    				            	} else {
    				            		var $overrideLink = '<a href="ticketOverride.html?id='+row.ticketId+'" class="overrideAction" data-id="'+row.ticketId+'"><webthing:override>Override</webthing:override></a>';
    				            	}
    				            	if ( row.ticketStatus == 'N' || row.ticketStatus == 'D' 
    				            			|| row.ticketStatus == 'C' || row.ticketStatus == 'I' || row.ticketStatus == 'P') {
    				            		var $ticketData = 'data-id="' + row.ticketId + '"';
    				            		$printLink = '<i class="print-link fa fa-print" aria-hidden="true" ' + $ticketData + '></i>'
    				            	} else {
    				            		$printLink = "";
    				            	}
    				            	$actionData = "<ansi:hasPermission permissionRequired='SYSADMIN'><ansi:hasWrite>" + $editLink + ' ' + $printLink + ' ' + $overrideLink + "</ansi:hasWrite></ansi:hasPermission>"
    			            	}
    			            	return $actionData;
    			            } }],
    			            "initComplete": function(settings, json) {
    			            	//console.log(json);
    			            	USERLOOKUP.doFunctionBinding();
    			            },
    			            "drawCallback": function( settings ) {
    			            	USERLOOKUP.doFunctionBinding();
    			            }
    			    } );
            	},
        	};
        	
        	USERLOOKUP.init();

        });
        		
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1><bean:message key="page.label.user" /><bean:message key="menu.label.lookup" /></h1>    	
    	<c:if test="${not empty ANSI_PERMISSION_GROUP_NAME}">
    		<div id="filterLabel">
	    		<span class="orange"><bean:message key="field.label.permissionGroupFilter" />: <c:out value="${ANSI_PERMISSION_GROUP_NAME}" /></span>
	    		<span id="clearFilter"><i class="fa fa-ban inputIsInvalid this_is_a_link" aria-hidden="true"></i></span><br />
    		</div>
    	</c:if>
	 	<table id="userTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:1300px;width:1300px;">
	        <colgroup>
	        	<col style="width:5%;" />
	    		<col style="width:13%;" />
	    		<col style="width:13%;" />
	    		<col style="width:13%;" />
	    		<col style="width:13%;" />
	    		<col style="width:13%;" />
	    		<col style="width:13%;" />
	    		<col style="width:5%;" />
	    		<col style="width:8%;" />
	    	</colgroup>
	        <thead>
	            <tr>
	                <th><bean:message key="field.label.userId" /></th>
	    			<th><bean:message key="field.label.lastName" /></th>
	    			<th><bean:message key="field.label.firstName" /></th>
	    			<th><bean:message key="field.label.email" /></th>
	    			<th><bean:message key="field.label.phone" /></th>
	    			<th><bean:message key="field.label.cityUL" /></th>
	    			<th><bean:message key="field.label.permissionGroupName" /></th>
	    			<th><bean:message key="field.label.status" /></th>
	    			<th><bean:message key="field.label.action" /></th>
	            </tr>
	        </thead>
	        <tfoot>
	            <tr>
	                <th><bean:message key="field.label.userId" /></th>
	    			<th><bean:message key="field.label.lastName" /></th>
	    			<th><bean:message key="field.label.firstName" /></th>
	    			<th><bean:message key="field.label.email" /></th>
	    			<th><bean:message key="field.label.phone" /></th>
	    			<th><bean:message key="field.label.cityUL" /></th>
	    			<th><bean:message key="field.label.permissionGroupName" /></th>
	    			<th><bean:message key="field.label.status" /></th>
	    			<th><bean:message key="field.label.action" /></th>
	            </tr>
	        </tfoot>
	    </table>
	    
	    <webthing:scrolltop />

    </tiles:put>
		
</tiles:insert>

