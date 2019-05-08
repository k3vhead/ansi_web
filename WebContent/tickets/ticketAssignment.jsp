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
        Ticket Assignment
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
       	<link rel="stylesheet" href="css/lookup.css" />
    	<link rel="stylesheet" href="css/ticket.css" />
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/addressUtils.js"></script>
    	<script type="text/javascript" src="js/lookup.js"></script> 
    	<script type="text/javascript" src="js/claims.js"></script> 
    	<script type="text/javascript" src="js/ticket.js"></script> 
    
        <style type="text/css">
        	<%-- all 3 columns --%>
        	#column-container-a {	
        		display:none;
        	}
        	<%-- columns 2 and 3 columns --%>
        	#column-container-b { 
        		 width:66%;
        		 float:left; 
        	}
        	#division-description {
        		text-align:center;
        		font-weight:bold;
        	}
        	#loading-container {
        		width:100%;
        		border:solid 1px #000000;
        		display:none;
        	}
        	#message-list-container {
        		width:49%;
        		float:right; 
        	}
        	#ticket-list-container {
        		width:49%; 
        		float:left; 
        	}
        	#ticket-modal {
				display:none;	
			}
			#ticket-search-button {
				display:none;
			}
        	#user-list-container {
				width:33%;
				float:right;
			}
        	.ticket {
        		border:solid 1px #404040;
        		width:90%;
        	}
			.ticket-clicker {
				color:#000000;
			}
			.ui-droppable-hover {
				background-color:#CC6600;
			}
        	.user {
        		border:solid 1px #404040;
        	}
        </style>
        
        <script type="text/javascript">
        
        $(document).ready(function() {
        	;TICKETASSIGNMENT = {
        		divisionMap : {},
        		division : null,
        		usersLoaded : false,
        		ticketsLoaded : false,
        		userTable : null,
        		ticketTable : null,
        		
        		
        		init : function() {
        			ANSI_UTILS.makeDivisionList(TICKETASSIGNMENT.makeDivListSuccess, TICKETASSIGNMENT.makeDivListFailure);
        			TICKETUTILS.makeTicketViewModal("#ticket-modal");
        			TICKETASSIGNMENT.makeModals();
        			TICKETASSIGNMENT.makeClickers();
        		},

        		
        		
        		displayPanels : function() {
        			if ( TICKETASSIGNMENT.usersLoaded == true && TICKETASSIGNMENT.ticketsLoaded == true) {
    					$("#loading-container").hide();
        				$("#column-container-a").show();
        				$("#ticket-search-button").show();
        			} else {
        				setTimeout(function() {
	            			TICKETASSIGNMENT.displayPanels();
	            		},250);
        			}
        		},
        		
        		
        		
        		loadTickets : function() {
        			console.log("Loading tickets for " + TICKETASSIGNMENT.division.divisionId);
        			TICKETASSIGNMENT.ticketTable = $('#ticket-table').DataTable( {
            			"aaSorting":		[[0,'desc']],
            			"processing": 		true,
            	        "serverSide": 		true,
            	        "autoWidth": 		false,
            	        "deferRender": 		true,
            	        "scrollCollapse": 	true,
            	        "scrollX": 			true,
            	        rowId: 				'dt_RowId',
            	        dom: 				'Bfrtip',
            	        "searching": 		false,
            	        "searchDelay":		800,
            	        lengthMenu: [],
            	        buttons: [],
            	        "columnDefs": [
             	            { "orderable": false, "targets": -1 },
            	            { className: "dt-head-left", "targets": [1] },
            	            { className: "dt-body-center", "targets": [0,2,3] },
            	            { className: "dt-right", "targets": []}
            	         ],
             	        "pageLength":		50,
            	        "paging": true,
    			        "ajax": {
    			        	"url": "ticketTable",
    			        	"type": "GET",
    			        	"data": {"jobId":null,"divisionId":TICKETASSIGNMENT.division.divisionId,"startDate":null,"status":"D"}
    			        	},
    			        columns: [
    			            { width:"5%", title: "<bean:message key="field.label.ticketId" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.ticket_id != null){return ('<a href="#" data-id="'+row.ticket_id+'" class="ticket-clicker">'+row.ticket_id+'</a>');}
    			            } },
    			            { width:"8%", title: "<bean:message key="field.label.jobSiteName" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.job_site_name != null){return (row.job_site_name+"");}
    			            } },
    			            { width:"6%", title: "<bean:message key="field.label.startDate" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.start_date != null){return (row.start_date+"");}
    			            } },
    			            { width:"5%", title: "<bean:message key="field.label.jobFrequency" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.job_frequency != null){return (row.job_frequency+"");}
    			            } },
							],
    			            "initComplete": function(settings, json) {
    			            	//console.log(json);
    			            	//doFunctionBinding();
    			            	var myTable = this;
    			            	LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#ticket-table", TICKETASSIGNMENT.loadTickets);
    			            	$("#filter-container .filter-banner .filter-hider .filter-data-closed").click(); //open the filter container inside the modal
    			            	TICKETASSIGNMENT.ticketsLoaded = true; 
    			            	TICKETASSIGNMENT.displayPanels();
    			            },
    			            "fnRowCallback": function(nRow, aData, iDisplayIndex) {
    							$(nRow).addClass("ticket");
    						},
    			            "drawCallback": function( settings ) {
    			            	$(".ticket").draggable({
    		        				containment:"#column-container-a",
    		        				cursor:"move",
    		        				revert:true,
    		        				scope:"ticket2user"
    		        			});
    		        			$(".ticket").droppable({
    		        				accept:".user",
    		        				scope:"user2ticket",
    		        				drop:TICKETASSIGNMENT.ticketDrop
    		        			});
    		        			$(".ticket-clicker").on("click", function($clickevent) {
    		    					$clickevent.preventDefault();
    		    					var $ticketId = $(this).attr("data-id");
    		    					TICKETUTILS.doTicketViewModal("#ticket-modal",$ticketId);
    		    					$("#ticket-modal").dialog("open");
    		    				});
    			            }
    			    } );
        		},
        		
        		
        		
        		loadUsers : function() {
        			console.log("Loading users for " + TICKETASSIGNMENT.division.divisionId);
        			var $url = "userDivision/" + TICKETASSIGNMENT.division.divisionId;
        				
            		TICKETASSIGNMENT.userTable = $('#user-list-table').DataTable( {
            			"aaSorting":		[[0,'asc']],
            			"processing": 		true,
            	        "serverSide": 		true,
            	        "autoWidth": 		false,
            	        "deferRender": 		true,
            	        "scrollCollapse": 	true,
            	        "scrollX": 			true,
            	        rowId: 				'dt_RowId',
            	        dom: 				'Bfrtip',
            	        "searching": 		false,
            	        "searchDelay":		800,
            	        "pageLength":		100,
            	        lengthMenu: [],
            	        buttons: [],
            	        "columnDefs": [
             	            { "orderable": false, "targets": -1 },
            	            { className: "dt-left", "targets": [0] },
            	            { className: "dt-center", "targets": [] },
            	            { className: "dt-right", "targets": []},
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": $url,
    			        	"type": "GET"
    			        	},
    			        	columns: [
        			        	{ title: "Washer", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
        			            	if(row.user_id != null){return ('<div class="user" data-id="'+row.user_id+'" data-first="'+row.first_name+'" data-last="'+row.last_name+'">'+row.last_name+', '+row.first_name+'</div>');}
        			            } }
    							],
    						"fnRowCallback": function(nRow, aData, iDisplayIndex) {
    							$(nRow).addClass("user");
    						},
    			            "initComplete": function() { 
    			            	TICKETASSIGNMENT.usersLoaded = true; 
    			            	TICKETASSIGNMENT.displayPanels();
    			            },    			            
    			            "drawCallback": function() {
    		        			$(".user").draggable({
    		        				containment:"#column-container-a",
    		        				cursor:"move",
    		        				revert:true,
    		        				scope:"user2ticket"
    		        			});
    		        			$(".user").droppable({
    		        				containment:".ticket",
    		        				scope:"ticket2user",
    		        				drop:TICKETASSIGNMENT.userDrop
    		        			});
    			            },
    			    } );
        		},
        		
        		
        		
        		makeAssignment : function($ticketId, $userId, $firstName, $lastName) {
        			var $msg = $("<div>");
        			var $className = "assignment-" + $ticketId + "-" + $userId;
        			$msg.addClass($className);
        			$msg.append("Assigning ticket " + $ticketId + " to " + $firstName + " " + $lastName);
        			$("#message-list-container").append($msg);
        			$("." + $className).fadeOut(6000);
        		},
        		
        		
        		
        		makeClickers : function() {
        			$("#ticket-search-button").click(function() {
        				$("#ticket-search-modal").dialog("open");
        			});
        		},
        		
        		

        		makeDivListFailure : function($data) {
					$("#globalMsg").html("Failed to load Division List. Contact Support");
					$("#column-container-a").hide();
					$("#loading-container").hide();
	       		},

	       		
	       		
        		makeDivListSuccess : function($data) {
   			 		console.log("Div List Success");
   			 		var $select = $("select[name='divisionId']");
					$('option', $select).remove();

					$select.append(new Option("",""));
					$.each($data.data.divisionList, function(index, val) {
    					$select.append(new Option(val.divisionNbr + "-" + val.divisionCode, val.divisionId));
    					TICKETASSIGNMENT.divisionMap[val.divisionId] = val;
					});
					
					$select.change(function() {
						TICKETASSIGNMENT.processDivChange();						
					});
        		},
        		
        		
        		
        		
        		makeModals : function() {
        			$( "#ticket-search-modal" ).dialog({
						title:'Ticket Search',
						autoOpen: false,
						height: 325,
						width: 500,
						modal: true,
						closeOnEscape:true,
						//open: function(event, ui) {
						//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
						//},
						buttons: [
							{
								id: "ticket-search-cancel-button",
								click: function($event) {
									$( "#ticket-search-modal" ).dialog("close");
								}
							}
						]
					});	
					$("#ticket-search-cancel-button").button('option', 'label', 'Done');
        		},
        		
        		
        		
        		processDivChange : function() {
        			var $selectedDiv = TICKETASSIGNMENT.divisionMap[$("select[name='divisionId']").val()];
					$("#division-description").html( $selectedDiv.description + " (" +  $selectedDiv.divisionNbr + "-" + $selectedDiv.divisionCode + ")");
					$("#column-container-a").hide();
					$("#ticket-search-button").hide();
					$("#loading-container").show();
					TICKETASSIGNMENT.usersLoaded = false;
					TICKETASSIGNMENT.ticketsLoaded = false;
					if ( TICKETASSIGNMENT.userTable != null ) {
						TICKETASSIGNMENT.userTable.destroy();
					}
					if ( TICKETASSIGNMENT.ticketTable != null ) {
						TICKETASSIGNMENT.ticketTable.destroy();
					}
					// we do it this way so the load methods can be called later without knowing the division
					TICKETASSIGNMENT.division = $selectedDiv;
					TICKETASSIGNMENT.loadUsers();
					TICKETASSIGNMENT.loadTickets();
        		},
        		
        		
        		
        		userDrop : function($event, $ui) {
        			console.log("I dropped on a ticket");
        			var $ticketId = $ui.helper.attr("data-id");
        			var $userId = $(this).attr("data-id");
        			var $firstName = $(this).attr("data-first");
        			var $lastName = $(this).attr("data-last");
        			TICKETASSIGNMENT.makeAssignment($ticketId, $userId, $firstName, $lastName);
        		},
        		
        		
        		
        		ticketDrop: function($event, $ui) {
        			console.log("I dropped on a user");
        			var $userId = $ui.helper.attr("data-id");
        			var $ticketId = $(this).attr("data-id");
        			var $firstName = $ui.helper.attr("data-first");
        			var $lastName = $ui.helper.attr("data-last");
        			TICKETASSIGNMENT.makeAssignment($ticketId, $userId, $firstName, $lastName);
        		},
        	},
        	
        	TICKETASSIGNMENT.init();

        });
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Ticket Assignment</h1>
    	
    	<span class="formLabel">Division:</span>&nbsp;<select name="divisionId"></select>&nbsp;<webthing:view styleId="ticket-search-button">Ticket Search</webthing:view>
    	<div id="division-description"></div>
    	<div id="loading-container"><webthing:thinking style="width:100%" /></div>
    	<div id="column-container-a">
    		<div id="user-list-container" >
    			<table id="user-list-table">
    				<thead></thead>
    				<tbody></tbody>
    				<tfoot></tfoot>
    			</table>
    			<%-- 
    			<div class="user" data-id="1" data-first="F1" data-last="L1">User 1</div>
    			<div class="user" data-id="2" data-first="F2" data-last="L2">User 2</div>
    			<div class="user" data-id="3" data-first="F3" data-last="L3">User 3</div>
    			<div class="user" data-id="4" data-first="F4" data-last="L4">User 4</div>
    			<div class="user" data-id="5" data-first="F5" data-last="L5">User 5</div>
    			<div class="user" data-id="6" data-first="F6" data-last="L6">User 6</div>
    			 --%>
			</div>
			<div id="column-container-b">
				<div id="message-list-container" class="err">
									
				</div>
				<div id="ticket-list-container">
					<table id="ticket-table">
						<thead></thead>
						<tbody></tbody>
						<tfoot></tfoot>
					</table>
					<%-- 
					<div class="ticket" data-id="1">Ticket 1</div>
					<div class="ticket" data-id="2">Ticket 2</div>
					<div class="ticket" data-id="3">Ticket 3</div>
					<div class="ticket" data-id="4">Ticket 4</div>
					<div class="ticket" data-id="5">Ticket 5</div>
					<div class="ticket" data-id="6">Ticket 6</div>
					<div class="ticket" data-id="7">Ticket 7</div>
					<div class="ticket" data-id="8">Ticket 8</div>
					<div class="ticket" data-id="9">Ticket 9</div>
					<div class="ticket" data-id="10">Ticket 10</div>
					<div class="ticket" data-id="11">Ticket 11</div>
					 --%>
				</div>
				<div class="spacer">&nbsp;</div>
			</div>
			<div class="spacer">&nbsp;</div>
    	</div>
    	
    	
    	<div id="ticket-search-modal">
	    	<webthing:lookupFilter filterContainer="filter-container" />
	    </div>
	
    	<webthing:scrolltop />

	    <webthing:ticketModal ticketContainer="ticket-modal" />
		
   
   
    </tiles:put>
		
</tiles:insert>

