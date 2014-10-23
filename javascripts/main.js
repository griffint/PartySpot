$(document).ready(function() {

	function getJSON() {

	}

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
			outputAlbumList.push(tempList);
		}

		for (i=0;i<artistJSON.length;i++) {
			tempList = [];
			tempList.push(artistJSON.items[i].name);
			tempList.push(artistJSON.items[i].images[2]);
			tempList.push(artistJSON.items[i].images[2]);
			outputArtistList.push(tempList);
		}

		for (i=0;i<trackJSON.length;i++) {
			tempList = [];
			tempList.push(trackJSON.items[i].name);
			tempList.push(trackJSON.items[i].artists.name);
			tempList.push(trackJSON.items[i].album.images[1].url);
			outputTrackList.push(tempList);
		}

		return [outputArtistList, outputTrackList, outputAlbumList]
	}

	function display(response) {
		
	}
})
