$(document).ready(function() {
	var currentObj = null;

	getJSONData("WIZ Khalifa","Spotify",15,true)
	
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
		outputArtistList = []
		outputAlbumList = []
		outputTrackList = []

		artistJSON = response.artists;
		albumJSON = response.albums;
		trackJSON = response.tracks;

		for (i=0;i<albumJSON.items.length;i++) {
			tempList = [];
			tempList.push(albumJSON.items[i].name);
			tempList.push(albumJSON.items[i].images[1].url);
			tempList.push(albumJSON.items[i].external_urls.spotify);
			outputAlbumList.push(tempList);
		}

		for (i=0;i<artistJSON.items.length;i++) {
			tempList = [];
			tempList.push(artistJSON.items[i].name);
			tempList.push(albumJSON.items[i].images[1].url);
			tempList.push(artistJSON.items[i].external_urls.spotify);
			outputArtistList.push(tempList);
		}

		for (i=0;i<trackJSON.items.length;i++) {
			tempList = [];
			tempList.push(trackJSON.items[i].name);
			tempList.push(trackJSON.items[i].artists.name);
			tempList.push(trackJSON.items[i].album.images[1].url);
			tempList.push(albumJSON.items[i].external_urls.spotify);
			outputTrackList.push(tempList);
		}

		console.log([outputArtistList, outputTrackList, outputAlbumList]);
		return [outputArtistList, outputTrackList, outputAlbumList];
	}

	function display(response) {
		
	}
})