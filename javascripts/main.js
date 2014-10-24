$(document).ready(function() {
	console.log("TESTING");
	getJSONData("WIZ Khalifa","Spotify",15,true)
	var currentObj = null;
	function getJSONData(searchText,apiUse,limit,liveResults)  {
		if (currentObj != null){
			currentObj.abort() //Kill all previous running queries
		}
		searchText = searchText.replace(' ', '%20');
		switch (apiUse){
			case "Spotify":
			 	var url = "https://api.spotify.com/v1/search?q=" + searchText + "&type=artist,track,album&limit=" + limit;
			 	break;
			case "Deezer":
				//THIS IS WHERE WE WOULD DETERMINE THE URL FOR THE DEEZER API
				break;
		}
		
		currentObj = $.getJSON(url,function(data) {
			parseJSON(data, liveResults)
		});
		

	}


	function parseJSON(response, liveResults) {
		console.log(response,liveResults);
	}

	function display(response) {
		
	}
})
