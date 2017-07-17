// css classes that you really care about:
// ansi-date-not-available -- dates in the past, you can't select these
// ansi-date-available -- future dates that can be selected 
// ansi-date-not-selected -- future dates that are available, but not selected
// ansi-date-selected -- future dates that are available and selected
$(function() {             
	;ANSI_CALENDAR = {
		DATA: {
			jobId:null,
			nameSpace:null,        			
			monthsToDisplay:12,
			today:new Date(),
			monthNames:['January','February','March','April','May','June','July','August','September','October','November','December'],
		},		
	
		init:function($nameSpace, $monthsToDisplay, $label) {
			ANSI_CALENDAR.DATA['nameSpace']=$nameSpace;			
			ANSI_CALENDAR.DATA['monthsToDisplay']=$monthsToDisplay;
			ANSI_CALENDAR.makeDisplayTable($nameSpace, $monthsToDisplay);
			ANSI_CALENDAR.makeCalendars($nameSpace);
			ANSI_CALENDAR.bindVariables($nameSpace);
			ANSI_CALENDAR.makeModal($nameSpace, $monthsToDisplay, $label);
		},
		
		
		go:function($jobId) {
			console.debug($jobId);
			ANSI_CALENDAR.DATA['jobId']=$jobId;			
			var $modalId = "#" + ANSI_CALENDAR.DATA['nameSpace'] + "_dateTable";
			var $url = "jobSchedule/" +$jobId;
			var jqxhr = $.ajax({
				type: 'GET',
				url: $url,
				data: null,
				statusCode: {
					200: function($data) {
						if ( $data.responseHeader.responseCode == 'SUCCESS') {
							// clear existing selected dates
							$selector = $modalId + " .ansi-date-selected";
							$($selector).removeClass("ansi-date-selected");

							// set days prior to today to unavailable
							var $today = new Date();
							
							var $monthString = ("0" + ($today.getMonth()+1)).slice (-2);

							
							var $dayOfMonth = 1;
							while ( $dayOfMonth < $today.getDay() ) {
								var $dayString = ("0" + $dayOfMonth).slice (-2);
								var $selector = $modalId + " .day-" + $monthString + "-" + $dayString + "-" + $today.getFullYear();
								$($selector).addClass("ansi-date-not-available");
								$dayOfMonth++;
							}
				    		
				    		
							// set already-scheduled days to "selected"
							$.each($data.data.ticketList, function(index, $ticket) {
								var $dateString = $ticket.startDate.replace(/\//g,"-");
								$selector = $modalId + " .day-" + $dateString;
								$($selector).removeClass("ansi-date-not-selected");
								$($selector).removeClass("ansi-date-not-available");
								$($selector).addClass("ansi-date-selected");	
								if ( $ticket.ticketId != null ) {
									$($selector).removeClass("ansi-date-available");
									$($selector).addClass("ansi-date-dispatched");
								}
							});
							$($modalId).dialog( "open" );
						} else if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
							$("#globalMsg").html("Edit error").show().fadeOut(6000);
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
		
		
		makeDisplayTable($nameSpace, $monthsToDisplay) {
        	var index = 0;
        	var $htmlString = ''
        	var $row = 1;
        	var $col = 1;
        	while ( index < $monthsToDisplay ) {
    			var $cellId = "cell_" + $row + "_" + $col;
				if ( index == 0 ) {
					$htmlString = $htmlString + "<tr>\n";						
				}
				$htmlString = $htmlString + '<td class="ansi-calendar-cell ' + $cellId + '"></td>\n';
				if ( index != 0 && (index + 1) % 4 == 0 ) {
					$htmlString = $htmlString + "</tr>\n<tr>\n";	
				}
    			$col++;
    			if ( index != 0 && (index + 1) % 4 == 0 ) {
    				$row++;
    				$col = 1;
    			}
    			index++;
        	}
			$htmlString = $htmlString + '</tr>\n';
			var $selector = "#" + $nameSpace + " .dateTable";
			$($selector).append($htmlString);
		},
		
		
		makeCalendars: function($nameSpace) {					
        	var $monthList = [];
        	var $monthCount = 0;
        	var $monthStart = new Date(ANSI_CALENDAR.DATA['today'].getFullYear(), ANSI_CALENDAR.DATA['today'].getMonth(), 1, 0, 0, 0);
        	while ( $monthCount < ANSI_CALENDAR.DATA['monthsToDisplay'] ) {
        		$month = ANSI_CALENDAR.makeMonth($monthStart);
        		$monthList.push($month);
        		$monthStart.setMonth( $monthStart.getMonth() + 1);
        		$monthCount++;
        	}

        	
        	var $row = 1;
        	var $col = 1;
			$.each($monthList, function(index, $month) {
				var $cellId = "#" + $nameSpace + " .dateTable .cell_" + $row + "_" + $col;
				$($cellId).html($month);
				$col++;
				if ( index != 0 && (index + 1) % 4 == 0 ) {
					$row++;
					$col = 1;
				}
			});
		},
		
		
		
		
		makeModal: function($nameSpace, $monthsToDisplay, $title) {
			var $modalId = "#" + $nameSpace + "_dateTable";
			var $buttonId = "#" + $nameSpace + "_close";
			var $rows = $monthsToDisplay/4; //we always go 4 wide
			var $height = Math.min(620, (Math.ceil($rows) * 207) + 20);
			$( $modalId ).dialog({
				title:$title,
				autoOpen: false,
				height: $height,
				width: 700,
				modal: true,
				closeOnEscape:false,
//				open: function(event, ui) {
//					$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
//				},
				buttons: [
					{
						id: $buttonId,
						click: function() {
							$($modalId).dialog( "close" );
						}
					}
				]
				//,close: function() {
				//	$("#paymentModal").dialog( "close" );
	      	    //    //allFields.removeClass( "ui-state-error" );
				//}
			});

			$( $modalId ).dialog('option', 'buttons', {
				"Done":function() {
					$($modalId).dialog( "close" );
				}
			});

			
		},
		
		
		
		
		makeMonth: function($monthStart) {
			var $today = new Date();
			$today.setHours(0);
			$today.setMinutes(0);
			$today.setSeconds(0);
			$today.setMilliseconds(0); //we want midnight this morning
    		var $currentDate = new Date($monthStart.getFullYear(), $monthStart.getMonth(), $monthStart.getDate(), 0, 0, 0);
    		var $monthList = [];
			var $htmlString = '';				
			$htmlString = $htmlString + '<table>';
			$htmlString = $htmlString + '<thead>';
			$htmlString = $htmlString + '<tr>';
			$htmlString = $htmlString + '<td colspan="7" class="ansi-date-month">';
			$htmlString = $htmlString + '<span class="ansi-date-month-text">'
			$htmlString = $htmlString + ANSI_CALENDAR.DATA['monthNames'][$monthStart.getMonth()];
			$htmlString = $htmlString + " "
			$htmlString = $htmlString + $monthStart.getFullYear();
			$htmlString = $htmlString + '</span>';
			$htmlString = $htmlString + '</td>';
			$htmlString = $htmlString + '</tr>\n';
			$htmlString = $htmlString + '<tr>';
			$htmlString = $htmlString + '<td class="ansi-date-dayname"><span class="ansi-date-dayname-text">Su</span></td>';
			$htmlString = $htmlString + '<td class="ansi-date-dayname"><span class="ansi-date-dayname-text">Mo</span></td>';
			$htmlString = $htmlString + '<td class="ansi-date-dayname"><span class="ansi-date-dayname-text">Tu</span></td>';
			$htmlString = $htmlString + '<td class="ansi-date-dayname"><span class="ansi-date-dayname-text">We</span></td>';
			$htmlString = $htmlString + '<td class="ansi-date-dayname"><span class="ansi-date-dayname-text">Th</span></td>';
			$htmlString = $htmlString + '<td class="ansi-date-dayname"><span class="ansi-date-dayname-text">Fr</span></td>';
			$htmlString = $htmlString + '<td class="ansi-date-dayname"><span class="ansi-date-dayname-text">Sa</span></td>';
			$htmlString = $htmlString + '</tr>\n';
			$htmlString = $htmlString + '</thead>';
			$htmlString = $htmlString + '<tbody>';
			$col = 0;
			$dayOfMonth = 1;
			while ( $col < $monthStart.getDay() ) {
				$monthList.push("<td>&nbsp;</td>");
				$col++;
			}
			count = 0;
			while ( $currentDate.getMonth() == $monthStart.getMonth() && count < 100) {
				var $monthString = ("0" + ($currentDate.getMonth()+1)).slice (-2);
				var $dayString = ("0" + $currentDate.getDate()).slice (-2);
				var $dateString = $monthString + "/" + $dayString + "/" + $currentDate.getFullYear(); 
				if ( $currentDate < $today ) {
					$selectable = "ansi-date-not-available";
				} else {
					$selectable = "ansi-date-available ansi-date-not-selected";
				}
				var $dateClass = "day-" + $monthString + "-" + $dayString + "-" + $currentDate.getFullYear();
				$monthList.push('<td class="ansi-date-day ' + $selectable +' ' + $dateClass + '" data-date="'+$dateString+'"><span class="ansi-date-day-text">' + $dayOfMonth + '</span></td>');					
				$dayOfMonth++;
				count++;
				$currentDate.setDate($currentDate.getDate() + 1);
			}
			$.each($monthList, function(index, $day) {
				if ( index == 0 ) {
					$htmlString = $htmlString + "<tr>";						
				}
				$htmlString = $htmlString + $day;
				if ( index != 0 && (index + 1) % 7 == 0 ) {
					$htmlString = $htmlString + "</tr><tr>";	
				}
				
    		});
			$htmlString = $htmlString + "</tr>";
			$htmlString = $htmlString + '</tbody>';
			$htmlString = $htmlString + '</table>';
			return $htmlString;
    	},
    	
    	
    	
    	bindVariables: function($nameSpace) {
    		var $selector = "#" + $nameSpace + " .ansi-date-available";
        	$($selector).mouseover(function($event) {
        		$(this).removeClass("ansi-date-lowlight");
        		$(this).addClass("ansi-date-highlight");
        	});
        	$($selector).mouseout(function($event) {
        		$(this).removeClass("ansi-date-highlight");
        		$(this).addClass("ansi-date-lowlight");
        	});
        	
        	$($selector).click(function($clickEvent) {
        		$clickEvent.preventDefault();
        		ANSI_CALENDAR.clickDate($(this));
        	});
        	
        	var $dispatched = "#" + $nameSpace + " ansi-date-dispatched";
        	$($dispatched).click(function($event) {
        		alert("Already dispatched");
        	});
        	var $showTrigger = "#" + $nameSpace + " .ansi-date-show-calendar";
        	var $modalSelector = "#" + $nameSpace + " .modal-calendar";
    	},
    	
    	
    	
    	
    	
    	
    	clickDate : function($clickDate) {
    		var $myDate = $(this);
        	var $dateString = $clickDate.data("date");        	
        	console.debug($dateString);
        	var $outbound = {'jobDate':$dateString}
			var $url = "jobSchedule/" + ANSI_CALENDAR.DATA['jobId'];
			var $modalId = "#" + ANSI_CALENDAR.DATA['nameSpace'] + "_dateTable";
			var $messageDisplay = $modalId + " .ansi-calendar-msg";
			var jqxhr = $.ajax({
				type: 'POST',
				url: $url,
				data: JSON.stringify($outbound),
				statusCode: {
					200: function($data) {
						if ( $data.responseHeader.responseCode == 'SUCCESS') {
							// everything is good: toggle the selection
				        	if ( $clickDate.hasClass("ansi-date-selected") ) {
				        		$clickDate.switchClass("ansi-date-selected","ansi-date-not-selected",10);
				        	} else {
				        		$clickDate.switchClass("ansi-date-not-selected","ansi-date-selected",10);
				        	}
				        	$($messageDisplay).html("Success").show().fadeOut(6000);
						} else if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
							// something went wrong: display a message
							var $errMessage = "";
							if ( $data.data.webMessages.length  > 1 ) {
								$errMessage = "<ul>";
								$.each( $data.data.webMessages['GLOBAL_MESSAGE'], function(index, value) {
									$errMessage = $errMessage + "<li>" + value + "</li>";
								});
								$errMessage = $errMessage + "</ul>";
							} else {
								$errMessage = $data.data.webMessages['GLOBAL_MESSAGE'][0];
							}
							$($messageDisplay).html($errMessage).show().fadeOut(4000);
						} else {
							// something went really wrong: call for help
							$($messageDisplay).html("System Error: Contact Support").show();
						}
					},
					403: function($data) {
						$($modalId).dialog("close");
						$("#globalMsg").html("Session Timeout. Log in and try again").show();
					}, 
					404: function($data) {
						$($modalId).dialog("close");
						$("#globalMsg").html("System Error 404: Contact Support").show();
					}, 
					405: function($data) {
						$($modalId).dialog("close");
						$("#globalMsg").html("System Error 405: Contact Support").show();
					}, 
					500: function($data) {
						$($modalId).dialog("close");
						$("#globalMsg").html("System Error 500: Contact Support").show();
					} 
				},
				dataType: 'json'
			});
    	}
    	
    	
	}
});