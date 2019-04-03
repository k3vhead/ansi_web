$(function() {    	
	;LOOKUPUTILS = {
		makeFilters : function($myTable, $filterContainerName, $dataTableName, $tableMakerFunction) {
			// parameters
			//     myTable - the datatables constructor. Easiest way to get is to you "this" from inside initComplete function
			//     filterContainerName - the CSS selector for the div that will contain the filter table
			//     dataTableName - the CSS selector for the datatables table
			var $filterTable = $("<table>");
        	
        	var dataTable = $($dataTableName).DataTable();
        	var columns = $myTable.api().init().columns;

        	$($filterContainerName + " .filter-div").append($filterTable);

        	dataTable.columns().every( function(colIdx) {
        		var $column = this;
		
        		if ( columns[colIdx].searchable ) {
            		var $filterRow = $("<tr>");
            		var $titleCell = $("<td>");
            		var $fieldCell = $("<td>");
            		
            		$titleCell.append($column.header().innerText);
            		$inputField = $('<input type="text">')
            		$fieldName = "columns["+colIdx+"][search][value]";
            		$inputField.attr("name", $fieldName);
            		$fieldCell.append($inputField);
            		
            		
            		$filterRow.append($titleCell);
            		$filterRow.append($fieldCell);
            		$filterTable.append($filterRow);
            		
            		var $selector = $filterContainerName + ' .filter-div input[name="' + $fieldName + '"]';
            		$($selector).on('keyup change', function() {
        				var dataTable = $($dataTableName).DataTable();
        				myColumn = dataTable.columns(colIdx);
       					myColumn.search(this.value).draw();
       					
       					var isFiltered = false;
       					$($filterContainerName + " .filter-div input").each(function(idx, field) {
       						if ( $(field).val().length > 0 ) {
       							isFiltered = true;
       						}
       					});
       					if ( isFiltered == true ) {
       						$($filterContainerName + " .filter-banner .is-filtered").show();
       					} else {
       						$($filterContainerName + " .filter-banner .is-filtered").hide();
       					}
        			});
        		}
        	});
        	
        	$($filterContainerName + " .filter-banner").show();
        	$($filterContainerName + " .filter-banner .filter-hider .filter-data-closed").click(function($event) {
        		$($filterContainerName + " .filter-banner .filter-hider .filter-data-closed").hide();
        		$($filterContainerName + " .filter-banner .filter-hider .filter-data-open").show();
        		$($filterContainerName + " .filter-div").fadeIn(1000);	            		
        		$($filterContainerName + " .filter-banner .panel-button-container").fadeIn(1000); 
        	});
        	$($filterContainerName + " .filter-banner .filter-hider .filter-data-open").click(function($event) {
        		$($filterContainerName + " .filter-banner .panel-button-container").fadeOut(1000); 
        		$($filterContainerName + " .filter-banner .filter-hider .filter-data-open").hide();
        		$($filterContainerName + " .filter-banner .filter-hider .filter-data-closed").show();
        		$($filterContainerName + " .filter-div").fadeOut(1000);
        	});
        	$($filterContainerName + " .filter-banner .panel-button-container .clear-filter-button").click(function($event) {
        		$($filterContainerName + " .filter-div input").val("");
        		$($filterContainerName + " .filter-banner .is-filtered").hide();
        		$($filterContainerName + " .filter-banner .filter-hider .filter-data-open").click();
        		//recreate the table from scratch
        		$($dataTableName).DataTable().destroy(false);
        		$tableMakerFunction();
        	});
		}
			
			
	}

});