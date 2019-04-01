<%@ tag description="" body-content="scriptless" %>
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing" %> 
<%@ attribute name="filterContainer" required="true" rtexprvalue="true" %>
<%@ attribute name="styleId" required="false" rtexprvalue="true" %>
<%@ attribute name="style" required="false" rtexprvalue="true" %>
<%
    String filterContainer = (String)jspContext.getAttribute("filterContainer"); 
%>
<div id="<%= filterContainer %>">
    <div class="filter-banner">
        <div class="panel-button-container"> 
        	<webthing:ban styleClass="clear-filter-button red">Clear</webthing:ban>
		</div>
		<div class="filter-hider">
			<span class="filter-data-closed"><i class="fas fa-caret-right"></i></span>
			<span class="filter-data-open"><i class="fas fa-caret-down"></i></span>
            &nbsp;
            <div class="filter-banner-filter-div">
            	<span class="formLabel">Filter</span>
            	<webthing:checkmark styleClass="green is-filtered">Filter is Active</webthing:checkmark>
            </div>
        </div>
   	</div>
   	<div class="filter-div">
   	</div>
</div>
<div  style="margin-bottom:5px; width:100%;">&nbsp;</div>
  	
  	