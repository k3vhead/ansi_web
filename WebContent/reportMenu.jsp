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


<%@ page import="java.lang.reflect.Method,
			com.ansi.scilla.web.report.common.ReportType,
			com.ansi.scilla.report.reportBuilder.AbstractReport" %>

<tiles:insert page="layout.jsp" flush="true">

    <tiles:put name="title" type="string">
        Report Menu
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
        	
        	;REPORT = {
        			reports : [],
        			
        	
        			init : function() {
        				<c:forEach var="row" items="${com_ansi_scilla_report_types}">
        					<webthing:hasPermission permissionRequired="${row.requiredPermission}">
    	    					REPORT.reports.push( {"type":"${row.reportType}","title":"${row.reportTitle}"} );
    	    				</webthing:hasPermission>
	        	    	</c:forEach>
        				
        				
        				
        				// put init stuff here
        				REPORT.doBindings();
        				REPORT.createTable()
        			},
        			
        			
        			
        			doBindings : function() {
        	     	  	$('.ScrollTop').click(function() {
        	      	   		 $('html, body').animate({scrollTop: 0}, 800);
        	      	  		return false;
        	     	    });
        				
        			},
        			
        			
        			
        			
        			
        			createTable : function() {
                		var dataTable = $('#reportTable').DataTable( {
                	        "order":[[0,"asc"]],
                	        "paging": false,
                	        "columnDefs": [
                	            { className: "dt-left", "targets": [0] },
                	            { className: "dt-center", "targets": [1] }
                	        ],
                			data: REPORT.reports,
                			columns: [
                				{title:"Report", data:function ( row, type, set ) {	
        			            	printText = '<a href="report.html?id=' + row.type + '" style="color:#000000; text-decoration:none;">'+ row.title +'</a>';
        			            	{return printText;}
        			            } },
                				{ title: "Action",  data: function ( row, type, set ) {	
        			            	printText = '<a href="report.html?id=' + row.type + '" style="color:#000000; text-decoration:none;"><i class="fa fa-print"></i></a>';
        			            	{return printText;}
        			            } }
                			]
       				    });
                	}
        	}

        	
            	       	
			REPORT.init();
        });
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Report Menu</h1>
    	
		
	<div style="text-align:center; width:45%;">
		<table id="reportTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt; width:45%;">
			<colgroup>
				<col style="width:90%;" />
				<col style="width:10%;" />
			</colgroup>        			
	        <thead>
	            <tr>
	                <th>Report</th>
	    			<th>Action</th>
	            </tr>
	        </thead>
	        <tfoot>
	            <tr>
	                <th>Report</th>
	    			<th>Action</th>
	            </tr>
	        </tfoot>
	    </table>
	</div>
	
    <div style="clear:both;">
    	&nbsp;
    </div>
   
	
    
    </tiles:put>
		
</tiles:insert>

