$(function() {             
	;ANSI_CALENDAR = {
		DATA: {
			nameSpace:null,        			
			monthsToDisplay:12,
			today:new Date(),
			monthNames:['January','February','March','April','May','June','July','August','September','October','November','December'],
		},		
	
		init:function($nameSpace, $monthsToDisplay) {
			ANSI_CALENDAR.DATA['nameSpace']=$nameSpace;
			ANSI_CALENDAR.DATA['monthsToDisplay']=$monthsToDisplay;
			ANSI_CALENDAR.makeDisplayTable($nameSpace, $monthsToDisplay);
			ANSI_CALENDAR.makeCalendars($nameSpace);
			ANSI_CALENDAR.bindVariables($nameSpace);
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
		
		
		
		
		makeMonth: function($monthStart) {
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
				$monthList.push('<td class="ansi-date-day ansi-date-notselected" data-date="'+$dateString+'"><span class="ansi-date-day-text">' + $dayOfMonth + '</span></td>');					
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
    		var $selector = "#" + $nameSpace + " .ansi-date-day";
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
	        	//var $dateString = $clickEvent.currentTarget.attributes['data-date'].value;
	        	$dateString = $(this).data("date");
	        	if ( $(this).hasClass("ansi-date-selected") ) {
	        		$(this).switchClass("ansi-date-selected","ansi-date-notselected",10);
	        	} else {
	        		$(this).switchClass("ansi-date-notselected","ansi-date-selected",10);
	        	}
        	});
    	}
	}
});