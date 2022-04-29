$(function() {    	
	;LOOKUPUTILS = {
		makeFilters : function($myTable, $filterContainerName, $dataTableName, $tableMakerFunction, $tableMakerCallbackFunction, $tableMakerData) {
			console.log("makeFilters");
			// parameters
			//     myTable - the datatables constructor. Easiest way to get is to you "this" from inside initComplete function
			//     filterContainerName - the CSS selector for the div that will contain the filter table
			//     dataTableName - the CSS selector for the datatables table
			//	   tableMakerFunction - method that will reinitalize the table
			$($filterContainerName + " .filter-div").html("");  // first - clear out whatever is in there now
			var $filterTable = $("<table>");
        	
        	var dataTable = $($dataTableName).DataTable();
        	var columns = $myTable.api().init().columns;

        	$($filterContainerName + " .filter-div").append($filterTable);

        	dataTable.columns().every( function(colIdx) {
        		var $column = this;
        		//console.log($column);
        		//console.log($column.header().innerText + " " + columns[colIdx].searchable+ " " + columns[colIdx].visible);
        		if ( columns[colIdx].searchable == true) {
            		var $filterRow = $("<tr>");
            		var $titleCell = $("<td>");
            		var $fieldCell = $("<td>");
            		
            		$titleCell.append($column.header().innerText);
            		$inputField = $('<input type="text">')
            		$fieldName = "columns["+colIdx+"][search][value]";
            		$inputField.attr("name", $fieldName);
            		if(columns[colIdx].searchFormat != null){
            			$inputField.attr("placeholder", columns[colIdx].searchFormat);
            		}
            		$fieldCell.append($inputField);
            		
            		$filterRow.append($titleCell);
            		$filterRow.append($fieldCell);
            		$filterTable.append($filterRow);
            		
            		var $selector = $filterContainerName + ' .filter-div input[name="' + $fieldName + '"]';
//            		console.log("Selector: " + $selector);
            		$($selector).on('keyup change', function() {
            			console.log($dataTableName + "\tSearching column: " + colIdx + " for " + columns[colIdx].title + " " + this.value);
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
        		$tableMakerFunction($tableMakerData);
        	});
        	
        	if ( $tableMakerCallbackFunction != null ) {
        		$tableMakerCallbackFunction();
        	}
		},
		
		
		
		setFilterValue : function($filterContainerName, $colIdx, $value) {
			console.log("setFilterValue: " + $filterContainerName +  " " + $colIdx + " " + $value)
			$selector = $filterContainerName + " input[name='columns["+$colIdx+"][search][value]']"
			console.log($selector);
			$($selector).val($value);
			$($filterContainerName + " .filter-banner .is-filtered").show();
		}
			
			
	}

});