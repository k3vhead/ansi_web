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
        AutoComplete Demo
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
    	<script type="text/javascript" src="jQuery/jquery.autocomplete.js"></script>
        <link href="jQuery/jquery.autocomplete.css" type="text/css" rel="stylesheet" />
		<style type="text/css">
			.ui-autocomplete-loading {
			  background: white url("images/ui-anim_basic_16x16.gif") right center no-repeat;
			}
		</style>
        
        <script type="text/javascript">        
        $(function() {                	
            $( "#searchTerm" ).autocomplete({
                source: "autoComplete",
                minLength: 2,
                appendTo: "#someElem",
                select: function( event, ui ) {
                  alert( "Selected: " + ui.item.id + " aka " + ui.item.label + " or " + ui.item.value );
                }
              });
        });
        </script>        
    </tiles:put>
    
    
    <tiles:put name="content" type="string">
    	<h1>Auto Complete Demo</h1>

		<form action="#" id="demoForm">
			Type Here: <input type="text" id="searchTerm" />
		</form>
    </tiles:put>

</tiles:insert>

