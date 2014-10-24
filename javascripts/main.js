$(document).ready(function() {

	function parseJSON(response) {
		outputArtistList = []
		outputAlbumList = []
		outputTrackList = []

		artistJSON = response.artists;
		albumJSON = response.albums;
		trackJSON = response.tracks;

		for (i=0;i<albumJSON.length;i++) {
			tempList = [];
			tempList.push(albumJSON.items[i].name);
			tempList.push(albumJSON.items[i].images[1].url);
			tempList.push(albumJSON.items[i].external_urls.spotify);
			outputAlbumList.push(tempList);
		}

		for (i=0;i<artistJSON.length;i++) {
			tempList = [];
			tempList.push(artistJSON.items[i].name);
			tempList.push(albumJSON.items[i].images[1].url);
			tempList.push(artistJSON.items[i].external_urls.spotify);
			outputArtistList.push(tempList);
		}

		for (i=0;i<trackJSON.length;i++) {
			tempList = [];
			tempList.push(trackJSON.items[i].name);
			tempList.push(trackJSON.items[i].artists.name);
			tempList.push(trackJSON.items[i].album.images[1].url);
			tempList.push(albumJSON.items[i].external_urls.spotify);
			outputTrackList.push(tempList);
		}

		return [outputArtistList, outputTrackList, outputAlbumList]
	}

	function display(response) {
		
	}

	$("#spotifySearch").html('<iframe src="https://embed.spotify.com/?uri=spotify:track:4th1RQAelzqgY7wL53UGQt" width='+$(window).width()+' height='+$(window).height()+' frameborder="0" allowtransparency="true"></iframe>');
})
