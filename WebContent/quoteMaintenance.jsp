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
        Quote Maintenance
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
        <style type="text/css">

			#delData {
				margin-top:15px;
				margin-bottom:15px;
			}
			#jobSiteAddress {
				border:solid 1px #000000;
			}
			#billToAddress {
				border:solid 1px #000000;
			}
			#quoteTable {
				border:solid 1px #000000;
			}
			.quoteButton {
				background-color:#F0F0F0; 
				color:#000000; 
				width:70px;
				min-height:25px;
			}
			.quoteSelect {
				max-width: 100px;
			    min-width: 100px;
				width:100px !important;
			}
			.quoteSelect option{
				max-width: 100px;
			    min-width: 100px;
				width:100px !important;
			}
			.labelSpan {
				display: inline-block;
				width: 90px !important;
			}
			.labelSpanSmall {
				display: inline-block;
				width: 60px !important;
			}
			#jobDates {
				border:solid 1px #000000;
			}
			#jobActivation2 {
				border:solid 1px #FF0000;
			}
			td.jobTableCell {
				width:50%;
				vertical-align:top;
			}
			#delData {
				margin-top:15px;
				margin-bottom:15px;
			}
			#jobProposal {
				border:solid 1px #000000;
			}
			#jobActivation {
				border:solid 1px #000000;
				height:100%;
			}
			#billTo {
				border:solid 1px #000000;
			}
			#jobSite {
				border:solid 1px #000000;
			}
			#jobDates {
				border:solid 1px #000000;
			}
			#jobActivation2 {
				border:solid 1px #FF0000;
			}
			td.jobTableCell {
				width:50%;
				vertical-align:top;
			}
			#JOBSITEADDRESS.select	{
				width:80px !important;
				max-width:80px !important;
			
			}
			#division-menu {
			  max-height: 300px;
			}
			#JOBSITEADDRESS_state-menu {
			  max-height: 300px;
			}
			#BILLTOADDRESS_state-menu {
			  max-height: 300px;
			}
			#quoteTable {
				width:100%;
			}
        </style>
    </tiles:put>
    
    
    <tiles:put name="content" type="string">
    	<h1>Quote Maintenance</h1>
		<table id="quoteTable">
			<tr>
				<td colspan="2">
					<table >
						<tr>
							<td><input type="button" name="modifyButton" value="Modify" class="quoteButton"/></td>
							<td><span class="labelSpanSmall">Manager:</span>
								<input type="text" name="manager"  style="width:95px"/>
							</td>
							<td><span class="labelSpan">Division:</span>
								<select name="division" id="division" class="quoteSelect">
									<option value=""></option>
								</select>
							</td>
							<td align="center">Quote</td>
							<td align="center">Revision</td>
							<td rowspan="2" align="right" style="padding-right:10px;"><input type="button" name="printButton" value="Print" class="quoteButton"/></td>
						</tr>
						<tr>
							<td><input type="button" name="copyButton" value="Copy" class="quoteButton"/></td>
							<td><span class="labelSpanSmall">Lead Type:</span>
								<select name="leadType" class="quoteSelect">
									<option value=""></option>
								</select>
							</td>
							<td><span class="labelSpan">Account Type:</span>
								<select name="accountType" class="quoteSelect">
									<option value=""></option>
								</select>
							</td>
							<td>Q:&nbsp;&nbsp;<input type="text" name="quoteNumber"  style="width:80px"/></td>
							<td>R:&nbsp;&nbsp;<input type="text" name="revision"  style="width:40px"/></td>
						</tr>
						<tr>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
							
							<td><span class="labelSpan">Proposed Date:</span>
								<input type="text" name="proposalDate"  style="width:95px" disabled="disabled"/>
							</td>
							<td colspan="2">Print Date:&nbsp;&nbsp;<input type="text" name="printDate"  style="width:90px"/></td>
							<td>Print Count:&nbsp;&nbsp;<input type="text" name="printCount"  style="width:90px" disabled="disabled"/></td>
						</tr>
						<tr>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
							<td align="right"  style="padding-right:10px;"><a href="#" name="viewPrintHistory">View Print History</a></td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td style="width:50%;">
					<webthing:addressPanel label="Job Site" namespace="JOBSITEADDRESS" cssId="jobSiteAddress" />
				</td>
				<td>
					<webthing:addressPanel label="Bill To"  namespace="BILLTOADDRESS" cssId="billToAddress" />
				</td>
			</tr>
		</table>  
				
		<table id="jobTable" style="border:solid 1px #000000; margin-top:8px;width:100%;">
			<tr><td><webthing:jobPanel namespace='JOB1'  cssId='jobPanel' /></td></tr>
		</table>
				
  	
		
        <script type="text/javascript">   
		      $( document ).ready(function() {
						function init() {
							$("select[name='division']").selectmenu({ width : '100px'});
							$("select[name='leadType']").selectmenu({ width : '100px'});
							$("select[name='accountType']").selectmenu({ width : '100px'});
							
								
							var jqxhr1 = $.ajax({
				    				type: 'GET',
				    				url: 'division/list',
				    				data: {},
				    				success: function($data) {
				    					selectorName = "select[name='division']";
				    					
				    					var $select = $(selectorName);
				    					$('option', $select).remove();
	
				    					$select.append(new Option("",""));
				    					$.each($data.data.divisionList, function(index, val) {
				    					    $select.append(new Option(val.divisionCode, val.divisionId));
				    					});
				    					
				    					$select.selectmenu();
				    				},
				    				statusCode: {
				    					403: function($data) {
				    						$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
				    					} 
				    				},
				    				dataType: 'json'
				    			});
							}
					
						JOBSITEADDRESS.init();
						BILLTOADDRESS.init();
						init();
		        });
        </script>        
    </tiles:put>

</tiles:insert>

