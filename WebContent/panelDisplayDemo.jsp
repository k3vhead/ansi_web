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
        Panel Display Demo
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
        <script type="text/javascript" src="js/ansi_utils.js"></script>
        <script type="text/javascript" src="js/jobMaintenance.js"></script>
        <script type="text/javascript" src="js/quoteMaintenance.js"></script>
        <style type="text/css">

		.workPanel {
			width:95%;
			border:solid 1px #000000;
			display:none;
		}
		#selectPanel {
			width:95%;
			border:solid 1px #000000;
		}
        </style>
        
        <script type="text/javascript">   
			$( document ).ready(function() {
				$("#panelSelector").change(function($event) {
					$(".workPanel").hide();
					$("#monitor").html("");
					var $selectedPanel = $('#panelSelector option:selected').val();					
	        		if ($selectedPanel != '' ) {
	        			$selectedId = "#" + $selectedPanel;
	        			$($selectedId).fadeIn(1500);
	        			$("#monitor").html("Displaying " + $selectedPanel);
	        		} else {
	        			$("#monitor").html("Hiding Everything");
	        		}

				});
			});
        </script>        
    </tiles:put>
    
    
    <tiles:put name="content" type="string">
    	<h1>Panel Display Demo</h1>
		<div  id="selectPanel">
			<select id="panelSelector">
				<option value=""></option>
				<option value="panel1">Show Panel 1</option>
				<option value="panel2">Show Panel 2</option>
				<option value="panel3">Show Panel 3</option>
			</select>
			<span id="monitor"></span>
		</div>
		
		<div class="workPanel" id="panel1">
			<h2>THis is panel 1</h2>
		</div>
  	
		<div class="workPanel" id="panel2">
			<h2>THis is panel 2</h2>
		</div>

		<div class="workPanel" id="panel3">
			<h2>THis is panel 3</h2>
		</div>
		

    </tiles:put>

</tiles:insert>

