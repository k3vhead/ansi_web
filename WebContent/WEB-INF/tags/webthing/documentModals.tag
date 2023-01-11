<%@ tag 
    description="" 
    body-content="empty" 
%>

<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld"  prefix="bean"  %>
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing" %>
<%@ taglib uri="/WEB-INF/theTagThing.tld" prefix="ansi" %>

<div id="new-document-modal">
	<form id="new-document-form" action="documentUpload" method="post" enctype="multipart/form-data">
		<table>
   			<tr>
   				<td><span class="form-label">ID:</span></td>
   				<td><input type="hidden" name="documentId" /><span class="documentId"></span></td>
   				<td><span class="documentId-err err"></span></td>
   			</tr>
   			<tr>
   				<td><span class="form-label">Description:</span></td>
   				<td><input type="text" name="description" /></td>
   				<td><span class="description-err err"></span></td>
   			</tr>
   			<tr>
   				<td><span class="form-label">Document Date:</span></td>
   				<td><input type="date" name="documentDate" /></td>
   				<td><span class="documentDate-err err"></span></td>
   			</tr>
   			<tr>
   				<td><span class="form-label">Expiration Date:</span></td>
   				<td><input type="date" name="expirationDate" /></td>
   				<td><span class="expirationDate-err err"></span></td>
   			</tr>
   			<tr class="addRow">
   				<td><span class="form-label">Document Type:</span></td>
   				<td><select name="documentType"></select></td>
   				<td><span class="documentType-err err"></span></td>
   			</tr>
   			<tr class="addRow">
   				<td><span class="form-label">Xref:</span></td>
   				<td><input type="text" name="xrefName" /><input type="hidden" name="xrefId" /></td>
   				<td><span class="xrefId-err err"></span></td>
   			</tr>
   			<tr class="addRow">
   				<td><span class="form-label">File:</span></td>
   				<td><input type="file" name="fileSelect" id="fileSelect" accept="pdf" /></td>
   				<td><span class="fileSelect-err err"></span></td>
   			</tr>
   		</table>
 	</form>
 </div>
 
 <div id="document-upload-message-modal">
 </div>
 
 <div id="documentViewer">
 </div>
 
 <div id="documentConfirmation">
 	<span class="document-message">Delete this document?</span>
 </div>

