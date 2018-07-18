<%@ page contentType="text/html"%>

<%@ taglib uri="/WEB-INF/c.tld" prefix="c"%>
<%@ taglib uri="/WEB-INF/sql.tld" prefix="sql"%>
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld"  prefix="bean"  %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing"%>
<%@ taglib tagdir="/WEB-INF/tags/quote" prefix="quote"%>
<%@ taglib uri="/WEB-INF/theTagThing.tld" prefix="ansi" %>

<html>
	<head>
	</head>
	<body>
		<table style="width:100%" class="jobInvoiceEditPanel">
			<colgroup>
				<col style="width:20%;" />
				<col style="width:30%;" />
				<col style="width:20%;" />
				<col style="width:30%;" />
			</colgroup>
			<tr>
				<td><span class="formLabel">PO #:</span></td>
				<td><input name="job-invoice-purchase-order" data-apiname="poNumber"></input></td>
				<td><span class="formLabel">Our Vendor #:</span></td>
				<td><input name="job-invoice-vendor-nbr" data-apiname="ourVendorNbr"></input></td>
			</tr>
			<tr>
				<td><span class="formLabel">Expire:</span></td>
				<td><input name="job-invoice-expire-date" data-apiname="expirationDate"></input></td>
				<td><span class="formLabel">Reason:</span></td>
				<td><input name="job-invoice-expire-reason" data-apiname="expirationReason"></input></td>
			</tr>
		</table>		
	</body>
</html>