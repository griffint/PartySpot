$(document).ready(function() {
	var searchService="Spotify";
	var currentObj = null;
	displaySearchTest();

	$("#submit").click(function() {
		getJSONData(document.getElementById('songSearch').value, searchService, 15, false);
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
			try {
				tempList.push(artistJSON.items[i].images[1].url);
			} catch(e) {
				tempList.push("");				
			}
			tempList.push(artistJSON.items[i].external_urls.spotify);
			outputArtistList.push(tempList);
		}

		for (i=0;i<trackJSON.items.length;i++) {
			tempList = [];
			tempList.push(trackJSON.items[i].name);
			tempList.push(trackJSON.items[i].artists[0].name);
			tempList.push(trackJSON.items[i].album.images[1].url);
			tempList.push(trackJSON.items[i].external_urls.spotify);
			outputTrackList.push(tempList);
		}

		if (!liveResults) {
			console.log('arrived');
			console.log(outputArtistList);
			displaySearch([outputArtistList, outputTrackList, outputAlbumList])
		} else {
			// display live results update here
		}
	}

	function autocomplete() {
		//need to make the ul appear when this is called
		artistList=["Luke Bryan","http://images1.mtv.com/uri/mgid:file:http:shared:/tsv2-production.s3.amazonaws.com/uploads/image/digital_asset/80170/widget_background_1135_3.jpg?width=315&height=210&enlarge=true&crop=true&matte=true&matteColor=black&quality=0.85"];
		trackList=[["Rain is good","Luke Bryan","http://images1.mtv.com/uri/mgid:file:http:shared:/tsv2-production.s3.amazonaws.com/uploads/image/digital_asset/80170/widget_background_1135_3.jpg?width=315&height=210&enlarge=true&crop=true&matte=true&matteColor=black&quality=0.85"],["generic Song","generic artist","http://static.refinedhype.com/uploads/insearchoflarge.gif"]];
		albumList=["Aaron Rodgers Sings","http://blogs.westword.com/latestword/aaron.rodgers.jpg"];
	}

	function displaySearchTest() {
		artistList=[["Luke Bryan","https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTnx9O1WF332PIKp8smgP5XiKQNAnZdpFOK_e4XdedeB1GWYQt5"],["Hayley Hansson", "data:image/jpeg;base64,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"], ["Chris Wallace", "data:image/jpeg;base64,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"]];
        displayArtist(artistList);
        trackList=[["Rain is good","Luke Bryan","http://images1.mtv.com/uri/mgid:file:http:shared:/tsv2-production.s3.amazonaws.com/uploads/image/digital_asset/80170/widget_background_1135_3.jpg?width=315&height=210&enlarge=true&crop=true&matte=true&matteColor=black&quality=0.85"],["generic Song","generic artist","http://static.refinedhype.com/uploads/insearchoflarge.gif"]];
   		displayTrack(trackList);
   		albumList=[["Aaron Rodgers Sings","http://blogs.westword.com/latestword/aaron.rodgers.jpg"], ["Aaron Rodgers Sings","http://blogs.westword.com/latestword/aaron.rodgers.jpg"]];
   		displayAlbum(albumList);
	}

	function displaySearch(info) {
		

		//artistList=[["Luke Bryan","https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTnx9O1WF332PIKp8smgP5XiKQNAnZdpFOK_e4XdedeB1GWYQt5"],["Hayley Hansson", "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAkGBwgHBgkIBwgKCgkLDRYPDQwMDRsUFRAWIB0iIiAdHx8kKDQsJCYxJx8fLT0tMTU3Ojo6Iys/RD84QzQ5OjcBCgoKDQwNGg8PGjclHyU3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3N//AABEIAIoAXAMBIgACEQEDEQH/xAAcAAADAQEBAQEBAAAAAAAAAAAEBQYHAwIBAAj/xAA4EAACAQMDAQYDBgUFAQEAAAABAgMABBEFEiExBhNBUWFxIoGhFDKRscHRByNCUvAVM0Ph8WIk/8QAGQEAAwEBAQAAAAAAAAAAAAAAAgMEAQUA/8QAIxEAAwEAAgEEAgMAAAAAAAAAAAECEQMhEhMiMUEEYTJCgf/aAAwDAQACEQMRAD8Agru0ZYhXbTbtRHsbGR4+dftSvY2g2ikMNxsfOaOfgKq7Q9ubNbuXccYo+00+KCPhRn1pRa3xPC5J9Kquzlg+pqGfcU8NoyPc0mnRTHglosFtLLMFRWx5gE1R6f2Ou5sTTTpHH5kkf+1UWFpbaeoa3hQSDrNINxHsK4a1qco2xxXO+TORFKnwH3wa8mkKtugO17BRrsIuoo0JO6UIoZvbj9a6aj/DyJ7djp80bNtzt2gBj6jH60XZ3c9wo+0xCPIxvhztPuOmK7RSz2E2YH2g87eqN7eR+lF6jF+npjfaDs9eaZO6yxKQo3N1BXny8vz6+NOuyOhrc2SSFcnPXHWtX1PTrHtXY91cKFvI+Uf+pT5e1SGgwtoMk9hdqA0bHHPhVX4zmq7Ecyco83XZ2IRHjGRUdeWv2W4aIrnFW+v9obeCA4ddw6VnGp64s920gPX0p3JESZF00Tz3Dvwx4r6hLHA60Pg5oiAAffJAPHC5xUejQ6wiEk6KNzc+HFbX2X08WWlxyTLtZ1B5PQevrUP/AA40GC4EurXq77W3OFz/AFkVRap2js7wyFe9lgiBLbH2oAPbrSea/pDuGG+xvqGoFnSKzHeyyEgHPwrjqf8AzrXez0KNh3t2xdz14AFcuzlnI0Au7lAskgG2MdEXwFPlZgKGF9sc19E5eaPPpjfatNeRgvWIsSMUdpWo22txy2m3ubtBkpnr6imwmyCrDNRuv7dO1m21G1AjlWQbscZB4IrK7NUDyF5YJcSHayY5z0P7H96VfxCje80lL+2jBuI/hJAGT4jr8x86fX0kbyW9wB/LuRhv/lqXxx97aXdrKQ64IAPUEc/nQRbmgbhVJgl7eXF0/wDOlZsdBjGPlQ+aaa/brbXMiOkEcynDiPgH2HhxjzpVmrNb+ST4DVgFdIrZpZUjQDcxwAepPtXgSUXpEjf6jAYyuQ6sS3hggk0YBqOpwjSey1vpVieSoDMOpJHP50j0K3jvdRtdNRAlupy3HMu3xPp6VSTst/Es+AAEPHvxn8KH0MJDqkJUJmEfHjqMgdfTJqHxbps6E0kkkMO0+qy6btSOyuJYhxmNtqj1zkUo0jtHeTX6IFuFhJGY5ufwarOa2gu0PfYI98Y+dCWthbB9kByoPPxbqb1h7Gnp9vtWisYg9y2D5YyTUxrmqW2oRJJDHMArD4mTAqg1myjv53jZA+BhRnFQms6BfWdz39uz28TNzCPuYJ6AZpax96NrrpIvLVhd6Bt3fHDtYef+Y/KusMYeTv1yGkxvx/d0J+fWkmh3LQvJFL9xogfwyCPwFOLRiE2E8gdande4zxfiYt2tEkerzqgZVjYqVJ8AfL5/v1qfOcnAxnwrUf4jaPNIBqtqxeLgyr1MZ8fkazMpzXQh+S1EFLHjDNRg+zykL93NeLJ2E6hVLAn4l8wKK1Zw8pxQkD91v5OMdBRoGs3o1rS5kfQjMJBICTlh0/zpSe3vJYu3cNusmIZkaNxjyBP5gfSjNMLp2RsnlA3XGXJAxkHAz/nnU9fzPBqy38Yy8Z3geeDnFSU8tot459iZa3csiyKk07x27EAsvmTgD50fFeyWSqmnLFNGOe6GAwrzB3N7YrPETJDOoYFTg4PI58D+tev9SjaHurs2V2Rnm6j2yZ9xgfT50yJXj8jn5VXS1Hye7vdrXn2cQODnZuzvHlQmoajFqf2eJAPiIOPbmhblVaUThjbwoDmOGR9jepz9OKC0GWO5vrmVAFWNSqDyH/lISzrRl1i7QxW2kilUkEAL18zzn86caeG7i2B++FCmu86xtayEKMg7B6eP6GuUBCzLzyvFJf8ALRW7JOds7j7Lpmor3edw7rY/Q5HX2xmstaAk5AAHlito7XaYmpWzHYdwOcKcZA8x9f8AOc/m0aNZCATiuhwSvEh5ntEm7mQ7jX6Ll9u0MT0B86HEmKK0uxvNVv4rPT4mluJThVXw9T5AedHovDUtcHdadZ28JGECIpAwOAP2pJfWciWktxJHiPaoDbh1xjp7mrSXQpYDpcWoOku1R3pRSQWA6efzpR2riivHSGywYIziaRejP4IvngZJ8qj8XrbL/PEkgbsNqB03SVjusmEkkA/081TrqmiXQL/ygcdSalxEPsaq55I5xxSW9soSGVTlvet7aGbMrQntf2ottzWemESN/UV6fjS/shdy2NwzT7mWYbpR5eX+e1ctN05jccRxNGvLbowc1TaPp8UUiu4XfMcRxsOrAeXljwrU0lgiqdPWXNjGklmrFsrcgd2x8GA4/H96V2W85kcHKnP1oT7bPFp91CXJkGwqc5PeBiKY6TN30hBXDZOfIipv7DsanQTtJeGyW3+LbkyHk9V25/6+VQV9rKpdyoAPgcr08jWhdu9NEumR3EaljbkqVz/xt979KxKYTNM7SK4dmLMCMEE10OGskh5O6F5Fap/BaK3itNRvCB9qaURBvFUAB+pP0rLytaF/By2vbnVbmGKOT7GVDSzY+FGHhnzINbSbWIyGk9Zrs1nDf2jyXe5IVzhg5X35HhU5NpMTqs0W0Qhf5YUcKvp70+1eYTAaZbqCpGH8lH70PfKggELHagXLMf6VFDyYl4oPiTfuf+ENfQNLK0EJbuUONyjlsfp60NJaJ3TJHjaqF2x8qY3Fyb+6MVqhS0VskAcufWvumxs11crIOJSIx8wxz9BSWhzoMstIjtdPgL/C8iqO9HVS2MH8cVx0612XP2i9VUnifvBADkRkEgn5g9PSn1yX/wBFeOIYlI7uIE9cHAP0zUrfXqm5uruP/mYrGMYyM8sfQ44oF1p5+7A2GCS5vHlVSI3mMnPj12/KqKO2+zhJE4TGG9PI/SlVrqMJhY/DtB+Ejyzx9KfwOs0GAcqy0prxHr36LtQvWglEF0WNpcBoyRkFDx0I6Zz+IPlWaa5pEllqDw3ChmABWRVx3q+DY8D7eVaTrKzT6fJFDI0cpVWjZW2ncvhkeBxg+9ZjqHa/WdOvri1gv5wkcrrhiCeCR1PtVPE97RLaUvsQaDpE2sazZ6dCADcShSx6KOrE/IGv6HgsLXs7o8emaLb9ymcDnJyerEnqaU9geyVloMcmquFku7sfCxH+0n9o9zyT7U9mcvcs7NkDgelW0vBMij31+jha2qW6+bHqx6k+dIO04uJVkSJsCRtnoABnn3OfpVAzl2wvU18udOWWMq5OSATUqh0+iyrmFrJfRtNSPc+8MRjcw/rP7V6ma0t7lXXeHDLIwHocfrTHTrNoLiWLbmINkHoRxQus24aZcYAdCOuTTPBenv2J9TeTAHVb+4ee4tIlEUsEYljweGHoalNWMs00lw4K3HwllA+EeGR7021G9MncTjHebe5f1rjp1szQ3IuDuCRoV3dcA5x+lRyWPVhx0mcpMofdg4wMZ5/wfWrPS3dI9igqCxKg+GaitPIN3gYwWzmq6yuM4oKWlMroY30WUjZQW2sFI8cHj9fzrJO0FrZS63fNGwOJ3DEhSNwY7sZPTOa2BJTwR51hXaqCJNduopP5RjcrtkBJ69c+OeufWm/jP5RL+VOY0f0Tf3piB3FFX+lRxn0oBp/hyep5rxqcsd0MqArISFLHOfGl63DPKsI5ZjjjmruZvSPgSz9j/TkXAmlIweEHiTTN2Ubhg8cE+vkKAhxaoiSgDA8T1omR+6XkDrnAGcceJo5lSsJ+byqjkY37sorkA80g7QLbwFZs5kC8jHjRt9eXsQ//ABskuOdrp935+NT91b316Xa5fcx5x6fKlc1PMSHcPH2m2RVx9qg3mFRLJM7Sd0R9wf3e9No72O6t+6Xh3QqzL86C3GOeUgkXS/EMeIHh+deFuYbiJmMTW827IdBlWPn6etSUkkXR5UwjRrGVZHLywtknbhvDwp+gMLAFlPsc1G294v8AqJEqAs/O9W2nI4OfOqi2KugIPWgr4K40e2suVFQ3brsXe6zrn27TyoV4VEmf7hkfkFqwtDgYNMAeBSptxWo3k41yTjOtzeWrYFvFtQ8HcT09OaFt4pYdXs7iPlFJDYODyMAYozUo4xcWuEUfd8Pahrknv+p/3U/Ou0lqxnD+O0GT95KnxqVBOFJ44HmPrXmG5kVZAsrYCkEE5Xb86LDukwKMykxtyDilnW4BPJzn50S7B+8OwuT3ajGBxgeFfpJtnxAg4bOP+qFjZlwAxAMPOD16Vwdi1vI7ElueT16Uq39FClLsitehlj1V5bc5feXAHiD4f57VytomcYhBYMfhB6g56GmN6AbnkdD+lF2wG1jgdRUFrWWTXjIo1fR5bOBLtY8orBdwPief3o/TJsxijtZAMKZHhSvT/vfh+VM5ZXimj349ttplFbv0o9JMr1pRD90e1HRfcFSNFqP/2Q=="], ["Chris Wallace", "data:image/jpeg;base64,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"]];
        artistList = info[0];
        displayArtist(artistList);
        //trackList=[["Rain is good","Luke Bryan","http://images1.mtv.com/uri/mgid:file:http:shared:/tsv2-production.s3.amazonaws.com/uploads/image/digital_asset/80170/widget_background_1135_3.jpg?width=315&height=210&enlarge=true&crop=true&matte=true&matteColor=black&quality=0.85"],["generic Song","generic artist","http://static.refinedhype.com/uploads/insearchoflarge.gif"]];
   		trackList = info[1];
   		displayTrack(trackList);
   		//albumList=[["Aaron Rodgers Sings","http://blogs.westword.com/latestword/aaron.rodgers.jpg"]];
   		albumList = info[2];
   		displayAlbum(albumList);
	}


	function displayArtist(artist_list) {
		var height = $(window).height()*.25;
		var width = $("#searchResults").width() 
		console.log(width);
		console.log(height);
		$('#artistResults').empty();		
		$("#artistResults").width(width).height(height);

		width = $("#artistResults").width();
		var parHeight = $(".artist-pic").parent().height();
		console.log(height);
		console.log(width/(height*1.1));
		if (artist_list.length < (Math.floor(width/(height*1.1)))){
			var bound = artist_list.length;
		}
		else{
			var bound = (Math.floor(width/(height*1.1)));
		}
        for (var i=0; i<bound; i++){
            var picture = '<img class="artist-pic" src="' + artist_list[i][1] + '">';
            var artist = '<div class="artist-name">' + artist_list[i][0] + '</div>';
            
            $('#artistResults').append('<a target="_blank" href="'+artist_list[i][2]+'"><div class="artist-result">'+picture+artist+'</div></a>');
        	
        }
        var margin = height*.05
        $(".artist-result").css({"margin":margin});
        $(".artist-result").width(height).height(height);

    }
 
    function displayTrack(track_list) {
	    $('#trackResults').empty();

        for (var i=0; i<track_list.length; i++){
            var picture = '<img class="track-pic" src="' + track_list[i][2] + '">';
            var track = '<div class="track-title">' + track_list[i][0] + '</div>';
            var artist = '<div class="track-artist">' + track_list[i][1] + '</div>';
            $('#trackResults').append('<a target="_blank" href="'+track_list[i][3]+'"><div class="track-result">'+picture+track+artist+'</div></a>');
        }

    }

    function displayAlbum(album_list) {
    	console.log(album_list);
		var height = $(window).height()*.17;
		num = Math.floor($(window).width()*.7/height*.17/.18);
	    $('#albumResults').empty();
	    if (album_list.length<num) {
	    	num = album_list.length;
	    }
        for (var i=0; i<num; i++){
            var picture = '<img class="album-pic" src="' + album_list[i][1] + '">';
            var name = '<div class="album-title">' + album_list[i][0] + '</div>';
            $('#albumResults').append('<a target="_blank" href="'+album_list[i][2]+'"><div class="album-result">'+picture+name+'</div></a>');
        }
        $(".album-result").width(height).height(height);
        $(".album-pic").width(10/17*height).height(10/17*height);
        $(".album-title").width(height).height(6/17*height);
		$(".album-pic").css({
			"left": 3.5/17*height
		});
		$(".album-title").css({
			"font-size": 2/17*height,
			"top": 11/17*height
		});
		$(".album-result").css({
			"margin-right": 1/17*height
		});
    }
})