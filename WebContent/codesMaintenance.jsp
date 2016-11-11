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
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="ansi" %>



<tiles:insert page="layout.jsp" flush="true">

    <tiles:put name="title" type="string">
        Codes Maintenance
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
        <style type="text/css">

        </style>
        <script type="text/javascript">
        $(function() {        	
            $("#goButton").click(function($event) {
            	$event.preventDefault();
            	$userid = $("input[name='userid']").val();
            	$password = $("input[name='password']").val();
            	$outbound = JSON.stringify({'userid':$userid, 'password':$password});
            	console.debug($outbound);
            	var jqxhr = $.ajax({
            	     type: 'POST',
            	     url: 'login',
            	     data: $outbound,
            	     success: function($data) {
            	    	 location.href="dashboard.html";
            	     },
            	     statusCode: {
            	    	403: function($data) {
            	    		$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
            	    	} 
            	     },
            	     dataType: 'json'
            	});
            });
            
            
			var jqxhr = $.ajax({
				type: 'GET',
				url: 'codes/getList.json',
				data: {},
				success: function($data) {
					console.debug("Success")
				},
				statusCode: {
					403: function($data) {
						$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
					} 
				},
				dataType: 'json'
			});
        });
        </script>        
    </tiles:put>
    
    
    <tiles:put name="content" type="string">
    	<h1>Codes Maintenance</h1>
    	
    	<table id="displayTable">
    		<tr>
    			<th>Table</th>
    			<th>Field</th>
    			<th>Value</th>
    			<th>Display</th>
    			<th>Seq</th>
    			<th>Description</th>
    			<th>Status</th>
    		</tr>
    	</table>
    </tiles:put>

</tiles:insert>

