package com.helen.search;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import org.apache.log4j.Logger;
import org.jibble.pircbot.Colors;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.helen.database.Configs;

public class YouTubeSearch {

	private final static Logger logger = Logger.getLogger(YouTubeSearch.class);
	
	public static String youtubeSearch(String searchTerm) {

		YouTube youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
			public void initialize(HttpRequest request) throws IOException {
			}
		}).setApplicationName("youtube-cmdline-search-sample").build();

		YouTube.Search.List search;
		try {
			search = youtube.search().list("id,snippet");

			search.setKey(Configs.getSingleProperty("apiKey").getValue());
			search.setQ(searchTerm.substring(3, searchTerm.length()));
			search.setType("video");
			search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
			search.setMaxResults(1l);

			SearchListResponse searchResponse = search.execute();
			List<SearchResult> searchResultList = searchResponse.getItems();
			if (searchResultList != null) {
				SearchResult video = searchResultList.get(0);

				StringBuilder str = new StringBuilder();

				YouTube.Videos.List videoRequest = youtube.videos().list("snippet, statistics, contentDetails");
				videoRequest.setId(video.getId().getVideoId());
				videoRequest.setKey(Configs.getSingleProperty("apiKey").getValue());
				VideoListResponse listResponse = videoRequest.execute();
				List<Video> videoList = listResponse.getItems();

				Video targetVideo = videoList.get(0);
				BigInteger views = BigInteger.valueOf(0l);;
				BigInteger rating = BigInteger.valueOf(0l);;
				BigInteger dislikes = BigInteger.valueOf(0l);;
				String time = targetVideo.getContentDetails().getDuration().split("PT")[1].toLowerCase();
				
				if(targetVideo.getStatistics() != null){
					views = targetVideo.getStatistics().getViewCount() == null ? BigInteger.valueOf(0l) : targetVideo.getStatistics().getViewCount();
					rating = targetVideo.getStatistics().getLikeCount() == null ? BigInteger.valueOf(0l) : targetVideo.getStatistics().getLikeCount();
					dislikes = targetVideo.getStatistics().getDislikeCount() == null ? BigInteger.valueOf(0l) : targetVideo.getStatistics().getDislikeCount();
				}
				String uploader = targetVideo.getSnippet().getChannelTitle();

				str.append(Colors.BOLD);
				str.append(video.getSnippet().getTitle());
				str.append(Colors.NORMAL);
				str.append(" -  length ");
				str.append(Colors.BOLD);
				str.append(time);
				str.append(Colors.NORMAL);
				str.append(" - ");
				str.append(rating);
				str.append("↑");
				str.append(dislikes);
				str.append("↓");
				str.append(" - ");
				str.append(Colors.BOLD);
				str.append(views);
				str.append(Colors.NORMAL);
				str.append(" views");
				str.append(" - ");
				str.append(Colors.BOLD);
				str.append(uploader);
				str.append(Colors.NORMAL);
				str.append(" - ");
				str.append("https://www.youtube.com/watch?v=" + video.getId().getVideoId());
				
				return str.toString();
			}

		} catch (IOException e) {
			logger.error("There was an exception attempting to youtube search",e);
		}
		
		return null;

	}

}
