<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/sql.tld" prefix="sql" %>
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld"  prefix="bean"  %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing" %>
<%@ taglib tagdir="/WEB-INF/tags/address" prefix="address" %>
<%@ taglib uri="/WEB-INF/theTagThing.tld" prefix="ansi" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Insert title here</title>
</head>
<body>
<table id="quoteDataContainer">
		    		<colgroup>
		    			<!-- Manager -->
			        	<col style="width:8px;" />  <!-- req -->
			        	<col style="width:120px;" />  <!-- label -->
			        	<col style="width:200px;" />  <!-- input -->
			        	<col style="width:10px;" />  <!-- err -->
			        	<col style="width:25px;" />	<!-- spacer -->
			        	
			    		<!-- Division -->
			        	<col style="width:8px;" />  <!-- req -->
			        	<col style="width:120px;" />  <!-- label -->
			        	<col style="width:200px;" />  <!-- input -->
			        	<col style="width:10px;" />  <!-- err -->
			        	<col style="width:25px;" />	<!-- spacer -->
			        	
			        	<!-- Quote -->
			        	<col style="width:8px;" />  <!-- req -->
			        	<col style="width:120px;" />  <!-- label -->
			        	<col style="width:356px;" />  <!-- input -->
			        	<col style="width:10px;" />  <!-- err -->
		    		</colgroup> 
	    			<tr>
	    				<td><span class="required">*</span></td>
	    				<td><span class="formLabel">Manager:</span></td>
	    				<td><select name="managerId"></select></td>
	    				<td><span class="err" id="managerIdErr"></span></td>
	    				<td>&nbsp;</td>
	    				
	    				<td><span class="required">*</span></td>
	    				<td><span class="formLabel">Division:</span></td>
	    				<td><select name="divisionId"></select></td>
	    				<td><span class="err" id="divisionIdErr"></span></td>
	    				<td>&nbsp;</td>
	    				
	    				<td><span class="required"></span></td>
	    				<td colspan="3">
	    					<span class="formLabel">Quote:</span> 
	    					<span id="quoteNbrDisplay">12345</span><span id="revisionDisplay">A</span>
	    				</td>
	    				
	    				
	    					<%--
	    				<td rowspan="3" style="text-align:center;">
	    					<span class="fa-stack fa-2x tooltip" style="color:#444444;">
								<i class="fa fa-print fa-stack-2x" id="printButton" aria=hidden="true"><span class="tooltiptext">Print</span></i>
							</span>
							<br />
							<span class="fa-stack fa-2x tooltip" id="viewPrintHistory" style="color:#444444;">
								<i class="fa fa-list-alt fa-stack-2x"><span class="tooltiptext">Print History<br />Print Count</span></i>
								<i class="fa fa-stack-1x"><span style="color:#FFFFFF; text-shadow:-1px -1px 0 #000,1px -1px 0 #000,-1px 1px 0 #000, 1px 1px 0 #000; font-weight:bold;" id="printCount">N/A</span></i>
							</span>
	    				</td>
							 --%>
	    			</tr>
	    			
	    			<tr>
	    				<td><span class="required">*</span></td>
	    				<td><span class="formLabel">Account Type:</span></td>
	    				<td><select name="accountType"></select></td>
	    				<td><span class="err" id="accountTypeErr"></span></td>
	    				<td>&nbsp;</td>
	    				
	    				<td><span class="required">*</span></td>
	    				<td><span class="formLabel"><bean:message key="field.label.invoice.terms" />:</span></td>
	    				<td><select name="invoiceTerms"></select></td>
	    				<td><span class="err" id="invoiceTermsErr"></span></td>   				
	    				<td>&nbsp;</td>
	    				
	    				<td><span class="required"></span></td>
	    				<td><span class="formLabel">Proposed Date:</span></td>
	    				<td><span id="proposedDate">N/A</span></td>
	    				<td><span class="err" id="proposedDateErr"></span></td>
	    			</tr>
	    			
	    			<tr>    				    				    				
	    				<td><span class="required">*</span></td>
	    				<td><span class="formLabel">Lead Type:</span></td>
	    				<td><select name="leadType"></select></td>
	    				<td><span class="err" id="leadTypeErr"></span></td>
	    				<td>&nbsp;</td>
	
	    				<td><span class="required">*</span></td>
	    				<td><span class="formLabel"><bean:message key="field.label.invoice.style" />:</span></td>
	    				<td><select name="invoiceStyle"></select></td>
	    				<td><span class="err" id="invoiceStyleErr"></span></td>
	    				<td>&nbsp;</td>
	    				
	    				<td><span class="required"></span></td>    				
	    				<td><span class="formLabel">Signed By:</span></td>
	    				<td><input type="text" name="signedBy" /></td>
	    				<td><span class="err" id="signedByErr"></span></td>
	    			</tr>
	    			
	    			<tr>
	    				<td><span class="required">*</span></td>
	    				<td><span class="formLabel">Building Type:</span></td>
						<td><select name="buildingType"></select></td>
	    				<td><span class="err" id="invoiceGroupingErr"></span></td>
	    				<td>&nbsp;</td>
						
						<td><span class="required">*</span></td>
	    				<td><span class="formLabel"><bean:message key="field.label.invoice.grouping" />:</span></td>
	    				<td><select name="invoiceGrouping"></select></td>
	    				<td><span class="err" id="invoiceGroupingErr"></span></td> 
	    				<td>&nbsp;</td>
	    				
	    				<td><span class="required"></span></td>
	    				<td colspan="3" style="whitespace:no-break;">
	    					<span class="formLabel">Batch:</span> <input type="checkbox" name="invoiceBatch" />
	    					<span class="formLabel">Tax Exempt:</span> <input type="checkbox" name="taxExempt" />
							<span class="formLabel">Reason:</span> <input type="text" name="taxExemptReason" />
						</td>
	    				<td><span class="err" id="invoiceTermsErr"></span></td>
	    			</tr>
	    		</table>
</body>
</html>