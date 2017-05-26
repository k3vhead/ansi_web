<%@ page contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/sql.tld" prefix="sql" %>
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld"  prefix="bean"  %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing" %>
<%@ taglib uri="/WEB-INF/theTagThing.tld" prefix="ansi" %>

					<table style="width:1100px;">
						<tr>
							<td><input type="button" name="modifyQuoteButton" value="Modify" class="quoteButton"/></td>
							<td><span class="labelSpanSmall" id="managerLabel">Manager:</span>
								<select name="manager" id="manager" class="quoteSelect">
									<option value=""></option>
								</select>
								<i id="managerIdErr" aria-hidden="true"></i>
							</td>
							<td><span class="labelSpan"  id="divisionLabel">Division:</span>
								<select name="division" id="division" class="quoteSelect">
									<option value=""></option>
								</select>
							</td>
							<td align="center"><span  id="quoteLabel">Quote</span></td>
							<td align="center"><span  id="revisionLabel">Revision</span></td>
							<td rowspan="2" align="right" style="padding-right:10px;">
								<span class="fa-stack fa-2x tooltip" style="color:#444444;">
									<i class="fa fa-print fa-stack-2x" id="printButton" aria=hidden="true"><span class="tooltiptext">Print</span></i>
								</span>
								<%-- <i class="fa fa-list-alt fa-3x" id="viewPrintHistory" aria=hidden="true"></i>--%>
								<span class="fa-stack fa-2x tooltip" id="viewPrintHistory" style="color:#444444;">
    								<i class="fa fa-list-alt fa-stack-2x"><span class="tooltiptext">Print History<br />Print Count</span></i>
    								<i class="fa fa-stack-1x"><span style="color:#FFFFFF; text-shadow:-1px -1px 0 #000,1px -1px 0 #000,-1px 1px 0 #000, 1px 1px 0 #000; font-weight:bold;" id="printCount">N/A</span></i>
								</span>
							</td>
						</tr>
						<tr>
							<td><input type="button" name="copyQuoteButton" value="Copy" class="quoteButton"/></td>
							<td><span class="labelSpanSmall" id="leadTypeLabel">Lead Type:</span>
								<select name="leadType" class="quoteSelect">
									<option value=""></option>
								</select>
								<i id="leadTypeErr" aria-hidden="true"></i>
							</td>
							<td><span class="labelSpan" id="accountTypeLabel">Account Type:</span>
								<select name="accountType" id="accountType" class="quoteSelect">
									<option value=""></option>
								</select>
								<i id="accountTypeErr" aria-hidden="true"></i>
							</td>
							<td>Q:&nbsp;&nbsp;<input type="text" name="quoteNumber"  style="width:80px" value="<c:out value="${ANSI_QUOTE_ID}" />"/></td>
							<td>R:&nbsp;&nbsp;<input type="text" name="revision"  style="width:40px"/></td>
						</tr>
						<tr>
							<td><input type="button" name="newQuoteButton" value="New" class="quoteButton"/></td>
							<td>Signed By:&nbsp;&nbsp;<input type="text" name="signedBy"  style="width:170px" disabled="disabled"/></td>
							
							<td><span class="labelSpan">Proposed Date:</span>
								<input type="text" name="proposalDate"  style="width:95px" disabled="disabled"/>
							</td>
							<%--
							<td colspan="2">Print Date:&nbsp;&nbsp;<input type="text" name="printDate"  style="width:90px"/></td>
							<td>Print Count:&nbsp;&nbsp;<input type="text" name="printCount"  style="width:90px" disabled="disabled"/></td>
							 --%>
						</tr>
						<%--
						<tr>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
							<td align="right"  style="padding-right:10px;"><a href="#" name="viewPrintHistory" id="viewPrintHistory">View Print History</a></td>
						</tr>
						 --%>
					</table>