$(document).ready(function() {
	var searchService="Spotify";
	var currentObj = null;

	$("#submit").click(function() {
		getJSONData(document.getElementById('songSearch').value, searchService, 15, true);
		document.getElementById("songSearch").value = "";
	});

	$("#deezerSelect").click(function() {
		$("#deezerSelect").css({
			"background-color": "#E6E6E6",
			"color": "black"
		});
		$("#spotifySelect").css({
			"background-color": "white",
			"color": "#EAEAEA"
		});
		console.log("deezer shown");
		$("#deezerSearch").show();
		$("#spotifySearch").hide();
	})

	$("#spotifySelect").click(function() {
		$("#deezerSelect").css({
			"background-color": "white",
			"color": "#EAEAEA"
		});
		$("#spotifySelect").css({
			"background-color": "#00FF00",
			"color": "black"
		});
		console.log("spotify shown");
		$("#spotifySearch").show();
		$("#deezerSearch").hide();
		
	})

	function getJSONData(searchText,apiUse,limit,liveResults)  {
		console.log("searchText");
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

	function autocomplete() {
		//need to make the ul appear when this is called
		artistList=["Luke Bryan","http://images1.mtv.com/uri/mgid:file:http:shared:/tsv2-production.s3.amazonaws.com/uploads/image/digital_asset/80170/widget_background_1135_3.jpg?width=315&height=210&enlarge=true&crop=true&matte=true&matteColor=black&quality=0.85"];
		trackList=[["Rain is good","Luke Bryan","http://images1.mtv.com/uri/mgid:file:http:shared:/tsv2-production.s3.amazonaws.com/uploads/image/digital_asset/80170/widget_background_1135_3.jpg?width=315&height=210&enlarge=true&crop=true&matte=true&matteColor=black&quality=0.85"],["generic Song","generic artist","http://static.refinedhype.com/uploads/insearchoflarge.gif"]];
		albumList=["Aaron Rodgers Sings","http://blogs.westword.com/latestword/aaron.rodgers.jpg"];
	}



	function display(response) {
		
	}
})