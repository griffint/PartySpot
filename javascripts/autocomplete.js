$(document).ready(function() {
    var availableTags = [
      "Livin on a prayer",
      "The penis song",
      "java - macklemore",
      "latch"
      
    ];
    $( "#songSearch" ).autocomplete({
      source: availableTags
    });
  });