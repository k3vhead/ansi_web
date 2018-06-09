=<%@ page contentType="text/html; charset=ISO-8859-1"
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
        Tag Test
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
        <style type="text/css">
			.moreInfo {
	color:#3399FF;
	cursor:pointer;
}
        </style>
        
        <script type="text/javascript">              
        $(document).ready(function(){
		});
        </script>        
    </tiles:put>
    
    
    
    

    
    
    
    
    
    
   <tiles:put name="content" type="string">
    	<h1>Tag Test</h1>
    	
		<webthing:moreInfo styleClass="moreInfo">Description goes here</webthing:moreInfo>
  
    </tiles:put>
		
</tiles:insert>

