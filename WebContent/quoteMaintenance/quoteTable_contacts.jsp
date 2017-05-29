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


					<table style="width:100%;">
						<tr>
							<td align="left" style="width:480px;border:1px solid #000000; white-space:nowrap;">
							<span style="display: inline-block;width:51px;"><b>Bill&nbsp;To:</b></span> <input type="text" name="billTo_name" style="width:425px" />
							<i id="billToAddressIdErr" aria-hidden="true"></i>	
							</td>
							<td align="left" style="width:480px;border:1px solid #000000; white-space:nowrap;">
								<span style="display: inline-block;width:51px;"><b>Job&nbsp;Site:</b></span> <input type="text" name="jobSite_name" style="width:425px" />
								<i id="jobSiteAddressIdErr" aria-hidden="true"></i>
							</td>
						</tr>
						<tr>
							<td align="left" style="width:480px;">
								<!--<webthing:addressPanel label="Bill To"  namespace="billTo" cssId="billTo"  page="bill"/>-->
								<webthing:addressDisplayPanel cssId="billTo" />
							</td>
							<td align="left" style="width:480px;">
							<!--<webthing:addressPanel label="Job Site" namespace="jobSite" cssId="jobSite" page="job"/>-->
								<webthing:addressDisplayPanel cssId="jobSite" />
							</td>
						</tr>
						<tr>
							<td align="left" style="width:480px;">
								<table style="width:100%;border:1px solid #000000">
									<tr>
										<td><span id="billToC1">Cont Contact:</span></td>
										<td style="width:140px;"><input type="text" name="billTo_contractContactName" style="width:125px" placeholder="<name>"/></td>
										<td colspan="2"><span name="billTo_contractContactInfo" style="display: inline-block;width:170px;"></span><i id="contractContactIdErr" aria-hidden="true"></i></td>
									</tr>
									<tr>
										<td><span id="billToC2">Billing Contact:</span></td>
										<td style="width:140px;"><input type="text" name="billTo_billingContactName" style="width:125px" placeholder="<name>"/></td>
										<td colspan="2"><span name="billTo_billingContactInfo" style="display: inline-block;width:170px;"></span><i id="billingContactIdErr" aria-hidden="true"></i></td>
									</tr>
								</table>
							</td>
							<td align="left" style="width:480px;">
								<table style="width:100%;border:1px solid #000000">
									<tr>
										<td><span id="jobSiteC1">Job Contact:</span></td>
										<td style="width:140px;"><input type="text" name="jobSite_jobContactName" style="width:125px" placeholder="<name>"/></td>
										<td colspan="2"><span name="jobSite_jobContactInfo" style="display: inline-block;width:170px;"></span><i id="jobContactIdErr" aria-hidden="true"></i></td>
									</tr>
									<tr>
										<td><span id="jobSiteC2">Site Contact:</span></td>
										<td style="width:140px;"><input type="text" name="jobSite_siteContactName" style="width:125px" placeholder="<name>"/></td>
										<td colspan="2"><span name="jobSite_siteContactInfo" style="display: inline-block;width:170px;"></span><i id="siteContactErr" aria-hidden="true"></i></td>
									</tr>
								</table>
							</td>
						</tr>
					</table>