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

<div id="note-crud-form" class="ui-front">
	<input type="hidden" name="xrefType" />
	<input type="hidden" name="xrefId" />
	<table>
		<tr>
  			<td><span class="formLabel">ID:</span></td>
  			<td><span id="call-note-id"></span><input type="hidden" name="callNoteId" /></td>
  			<td><span class="callNoteIdErr err"></span></td>
  		</tr>
  		<tr>
  			<td><span class="formLabel">Address:</span></td>
  			<td><input type="text" name="address" /><input type="hidden" name="addressId"/></td>
  			<td><span class="addressIdErr err"></span></td>
  		</tr>  
  		<ansi:hasPermission permissionRequired="CALL_NOTE_OVERRIDE">  		
  		<tr>
  			<td><span class="formLabel">ANSI Contact:</span></td>
  			<td>  			
  				<input type="text" name="ansi" />
  				<input type="hidden" name="userId"  />
  			</td>
  			<td><span class="userIdErr err"></span></td>
  		</tr>
  		</ansi:hasPermission>
  		<tr>
  			<td><span class="formLabel">Date/Time:</span></td>
  			<td><input type="date" name="startDate"  /> <input type="time" name="startTime" /></td>
  			<td><span class="startDateErr err"></span></td>
  		</tr>
  		<tr>
  			<td><span class="formLabel">Contact Type:</span></td>
  			<td><select name="contactType"></select></td>
  			<td><span class="contactTypeErr err"></span></td>
  		</tr>
  		<tr>
  			<td><span class="formLabel">Contact Name:</span></td>
  			<td><input type="text" name="contact" /><input type="hidden" name="contactId"/></td>
  			<td><span class="contactIdErr err"></span></td>
  		</tr>
  		<tr>
  			<td><span class="formLabel">Summary:</span></td>
  			<td><input type="text" name="summary" /></td>
  			<td><span class="summaryErr err"></span></td>
  		</tr>
  		<tr>
  			<td style="vertical-align:top;"><span class="formLabel">Notes:</span></td>
  			<td><textarea rows="8" cols="45" name="notes" id="crud-modal-notes"></textarea></td>
  			<td style="vertical-align:top;"><span class="notesErr err"></span></td>
  		</tr>
	</table>
</div>
  
  
<div id="new-address-modal">
  	<div class="modal-label">No matching address. Create one?</div>
  	<table>
  		<tr>
  			<td><span class="formLabel">Name:</span></td>
  			<td><input type="text" name="name" /></td>
  			<td><span class="nameErr err"></span></td>
  		</tr>
  		<tr>
  			<td><span class="formLabel">Address:</span></td>
  			<td><input type="text" name="address1" /></td>
  			<td><span class="address1Err err"></span></td>
  		</tr>
  		<tr>
  			<td><span class="formLabel">Address 2:</span></td>
  			<td><input type="text" name="address2" /></td>
  			<td><span class="address2Err err"></span></td>
  		</tr>
  		
  		<tr>
  			<td><span class="formLabel">City:</span></td>
  			<td><input type="text" name="city"  /></td>
  			<td><span class="cityErr err"></span></td>
  		</tr>
  		<tr>
  			<td><span class="formLabel">State:</span></td>
  			<td><input type="text" name="state"  /></td>
  			<td><span class="stateErr err"></span></td>
  		</tr>
  		<tr>
  			<td><span class="formLabel">Zip:</span></td>
  			<td><input type="text" name="zip" /></td>
  			<td><span class="zipErr err"></span></td>
  		</tr> 
  		<tr>
  			<td><span class="formLabel">County:</span></td>
  			<td><input type="text" name="county" /></td>
  			<td><span class="countyErr err"></span></td>
  		</tr>    		
  	</table>
</div>
  
  
<div id="new-contact-modal">
  	<div class="modal-label">No matching contact. Create one?</div>
  	<table>
  		<tr>
  			<td><span class="formLabel">Name:</span></td>
  			<td><input type="text" name="firstName" placeholder="First"/> <input type="text" name="lastName" placeholder="Last" /></td>
  			<td><span class="nameErr err"></span></td>
  		</tr>
  		<tr>
  			<td><span class="formLabel">Phone:</span></td>
  			<td>
  				<input type="text" name="phone" /> 
  				Preferred: <input type="radio" name="preferredContact" value="phone" />
  				Type: <select name="phone_type"><option value="business_phone">Business</option><option value="mobile_phone">Mobile</option></select>
  			</td>
  			<td><span class="phoneErr err"></span></td>
  		</tr>
  		<tr>
  			<td><span class="formLabel">Email:</span></td>
  			<td>
  				<input type="text" name="email"  /> 
  				Preferred: <input type="radio" name="preferredContact" value="email" />
  			</td>
  			<td><span class="emailErr err"></span></td>
  		</tr>
  	</table>
</div>


<div id="call-note-list-modal">
	<ul class="note-list">
	</ul>
</div>



<div id="call-note-detail-modal">
	<table>
		<tr>
			<td class="call-note-detail-text" rowspan="2">
				<span class="formLabel">Call Id:</span> <span class="callLogId"></span><br />
				<span class="formLabel">Xref:</span> <span class="xref"></span><br />
				<span class="formLabel">Time:</span> <span class="startTime"></span>
			</td>
			<td class="call-note-detail-text" style="width:62%;">
				<span class="formLabel">Summary:</span> <span class="summary"></span>
			</td>
		</tr>	
		<tr>
			<td class="call-note-detail-text" style="width:62%;">
				<span class="content"></span>
			</td>
		</tr>
	</table>
	<table>
		<tr>
			<td class="call-note-detail-text" style="width:33%;">
				<span class="formLabel">Contact</span><br />
				<span class="formLabel">Name:</span> <span class="contactName"></span><br />
				<span class="formLabel">Method:</span> <span class="contactType"></span>
			</td>
			<td class="call-note-detail-text" style="width:33%;">
				<span class="formLabel">Address</span><br />
				<span class="addressName"></span><br />
				<span class="address1"></span><br />
				<span class="address2container"><span class="address2"></span><br /></span>
				<span class="city"></span>, <span class="state"></span> <span class="zip"></span>
			</td>
			<td class="call-note-detail-text" style="width:33%;">
				<span class="formLabel">ANSI:</span><br />
				<span class="ansiContact"></span><br />
				<webthing:phoneIcon /> <span class="ansiPhone"></span><br />
				<webthing:emailIcon /> <span class="ansiEmail"></span>
			</td>
		</tr>
	</table>
</div>

