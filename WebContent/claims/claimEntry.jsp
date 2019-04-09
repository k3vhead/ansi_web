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




<tiles:insert page="../layout.jsp" flush="true">

    <tiles:put name="title" type="string">
        Claim Entry
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
    	<link rel="stylesheet" href="css/ticket.css" />
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/addressUtils.js"></script>
    	<script type="text/javascript" src="js/ticket.js"></script> 
    
        <style type="text/css">
        	#direct-labor-container {
				margin-top:20px;
				margin-left:40px;
        	}
			#passthru-expense-container {
				margin-top:20px;
				margin-left:40px;
			}			
        	#ticketDetailContainer .ticket-detail-table {
        		width:100%;
        	}
			#ticket-modal {
				display:none;	
			}
			.dt-center {
				text-align:center;
			}
			.dt-left { 
				text-align:left;
			}
			.dt-right {
				text-align:right;
			}
			.spacer-row {
				border:0;
			}
			.table-label-text {
				font-weight:bold;
			}
			.ticket-clicker {
				color:#000000;
				text-decoration:underline;
				cursor:pointer;
			}	
			.ticket-detail-hdr {
				font-weight:bold;
				text-align:center;
				border-top:solid 2px #000000;
				border-left:solid 1px #000000;
				border-right:solid 1px #000000;
				border-bottom:solid 1px #000000;
			}		
			.ticket-detail-data {
				border:solid 1px #000000;
				padding-left:3px;
				padding-right:3px;
			}
			
        </style>
        
        <script type="text/javascript">
        
        $(document).ready(function() {
        	;CLAIMENTRY = {
        		datatable : null,
        		ticketFilter : '<c:out value="${CLAIM_ENTRY_TICKET_ID}" />',
        		
        		init : function() {
        			CLAIMENTRY.getDetail();
        			//CLAIMENTRY.createTable();
        			//CLAIMENTRY.makeClickers();
        			CLAIMENTRY.makeModals();
        		},
        		
        		
        		
        		getDetail : function() {
        			var $url = "claims/claimEntry/" + CLAIMENTRY.ticketFilter;
        			var jqxhr = $.ajax({
						type: 'GET',
						url: $url,
						data: null,
						statusCode: {
							200: function($data) {
								console.log($data);
								if ( $data.responseHeader.responseCode == 'SUCCESS') {
									CLAIMENTRY.populateDetail($data.data);
									CLAIMENTRY.populatePassthru($data.data);
								} else if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
								} else {
									$("#globalMsg").html("System Error: Contact Support").show();
								}
							},
							403: function($data) {
								$("#globalMsg").html("Session Timeout. Log in and try again").show();
							}, 
							404: function($data) {
								$("#globalMsg").html("System Error 404: Contact Support").show();
							}, 
							405: function($data) {
								$("#globalMsg").html("System Error 405: Contact Support").show();
							}, 
							500: function($data) {
								$("#globalMsg").html("System Error 500: Contact Support").show();
							} 
						},
						dataType: 'json'
					});
        		},

        		
        		
        		makeModals : function () {
        			TICKETUTILS.makeTicketViewModal("#ticket-modal")
        		},
        		
        		
        		populateDetail : function($data) {
        			var $stringList = ["jobSiteAddress",
								"ticketId",
								"ticketStatus",
								"omNotes"];
        			var $numberList = [
								"totalVolume",
								"claimedVolume",
								"availableVolume",
								"budget",
								"claimedDirectLaborAmt",
								"availableDirectLabor"]
								
        			$.each($stringList, function($idx, $val) {
        				var $selector = "#ticketDetailContainer .ticket-detail-table ." + $val;
        				if ( $data[$val] == null ) {
        					$($selector).html(null);
        				} else {
        					$($selector).html( $data[$val]);
        				}
        			});
        			$.each($numberList, function($idx, $val) {
        				var $selector = "#ticketDetailContainer .ticket-detail-table ." + $val;
        				if ( $data[$val] == null ) {
        					$($selector).html(null);
        				} else {
        					$($selector).html( $data[$val].toFixed(2));
        				}
        			});
        			
        			$("#ticketDetailContainer .ticket-detail-table .ticketId").attr("data-id", $data['ticketId']);
        			$(".ticket-clicker").on("click", function($clickevent) {
						$clickevent.preventDefault();
						var $ticketId = $(this).attr("data-id");
						TICKETUTILS.doTicketViewModal("#ticket-modal",$ticketId);
						$("#ticket-modal").dialog("open");
					});
        		},
            	
	            
	            
            	
            	

            	makeClickers : function() {
            		$('.ScrollTop').click(function() {
        				$('html, body').animate({scrollTop: 0}, 800);
              	  		return false;
              	    });
            	},
            	
            	
            	
            	makeModals : function() {
            		TICKETUTILS.makeTicketViewModal("#ticket-modal")
            	},
            	
            	
            	
            	populatePassthru : function($data) {
            		$.each($data.expenseList, function($idx, $val) {
            			var $row = $("<tr>");
            			
                		var $dateTd = $('<td class="ticket-detail-data dt-center">');
                		$dateTd.append($val.workDate);
                		var $typeTd =$('<td class="ticket-detail-data dt-center">');
                		$typeTd.append($val.passthruExpenseType);
                		var $volumeTd = $('<td class="ticket-detail-data dt-right">');
                		$volumeTd.append($val.passthruExpenseVolume);
                		var $notesTd = $('<td class="ticket-detail-data dt-left">');
                		$notesTd.append($val.notes);
                		var $nameTd = $('<td class="ticket-detail-data dt-left">');
                		$nameTd.append($val.washerLastName + ", " + $val.washerFirstName);
                		
                		$row.append($dateTd);
                		$row.append($typeTd);
                		$row.append($volumeTd);
                		$row.append($nameTd);
                		$row.append($notesTd);
                		$("#passthru-expense-table").append($row);
            		});
            		
            	},

        	}
      	  	

        	CLAIMENTRY.init();
        	
        });
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Claim Entry</h1>
    	
		<div id="ticketDetailContainer">
			<table class="ticket-detail-table" cellSpacing="0" cellPadding="0">
				<tr>
					<td class="ticket-detail-hdr">Account (Job Site)</td>
					<td class="ticket-detail-hdr">Ticket</td>
					<td class="ticket-detail-hdr">Status</td>
					<td class="ticket-detail-hdr">Total Volume</td>
					<td class="ticket-detail-hdr">Claimed Volume</td>
					<td class="ticket-detail-hdr">Available Volume</td>
					<td class="ticket-detail-hdr">Budget</td>
					<td class="ticket-detail-hdr">Claimed DL</td>
					<td class="ticket-detail-hdr">Available DL</td>
					<td class="ticket-detail-hdr">OM Notes</td>
				</tr>
				<tr>
					<td class="ticket-detail-data dt-left"><span class="jobSiteAddress"></span></td>
					<td class="ticket-detail-data dt-center"><span class="ticketId ticket-clicker"></span></td>
					<td class="ticket-detail-data dt-center"><span class="ticketStatus"></span></td>
					<td class="ticket-detail-data dt-right"><span class="totalVolume"></span></td>
					<td class="ticket-detail-data dt-right"><span class="claimedVolume"></span></td>
					<td class="ticket-detail-data dt-right"><span class="availableVolume"></span></td>
					<td class="ticket-detail-data dt-right"><span class="budget"></span></td>
					<td class="ticket-detail-data dt-right"><span class="claimedDirectLaborAmt"></span></td>
					<td class="ticket-detail-data dt-right"><span class="availableDirectLabor"></span></td>
					<td class="ticket-detail-data dt-left"><span class="omNotes"></span></td>
				</tr>				
				<tr>
					<td class="spacer-row" colspan="10"><br /></td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td class="ticket-detail-hdr dt-right" colspan="2">Volume to Claim:</td>
					<td class="ticket-detail-data dt-right"><span class="volumeToClaim">0.00</span></td>
					<td class="ticket-detail-hdr dt-right" colspan="2">DL to Claim:</td>
					<td class="ticket-detail-data dt-right"><span class="dlToClaim">0.00</span></td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td class="ticket-detail-hdr dt-right" colspan="2">Unapplied Volume:</td>
					<td class="ticket-detail-data dt-right"><span class="uappliedVolume">0.00</span></td>
					<td class="ticket-detail-hdr dt-right" colspan="2">Unapplied DL:</td>
					<td class="ticket-detail-data dt-right"><span class="unappliedDL">0.00</span></td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
				</tr>
			</table>
		</div>    	

		<div id="direct-labor-container">
			<span class="table-label-text">Direct Labor</span><br />
			Coming Soon...
		</div>
		
		<div id="passthru-expense-container">
			<span class="table-label-text">Passthru Expense</span>
			<table id="passthru-expense-table" cellSpacing="0" cellPadding="0">
				<tr>
					<td class="ticket-detail-hdr">Date</td>
					<td class="ticket-detail-hdr">Type</td>
					<td class="ticket-detail-hdr">Volume $</td>
					<td class="ticket-detail-hdr">Washer</td>
					<td class="ticket-detail-hdr">Notes</td>
				</tr>
			</table>
		</div>
    
	    <webthing:scrolltop />
	    
	    <webthing:ticketModal ticketContainer="ticket-modal" />
    
   
    </tiles:put>
		
</tiles:insert>

