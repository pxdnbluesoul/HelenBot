package com.helen.search;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.helen.database.framework.Config;
import com.helen.database.framework.Configs;
import org.apache.log4j.Logger;
import org.jibble.pircbot.Colors;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public class YouTubeSearch {

    private final static Logger logger = Logger.getLogger(YouTubeSearch.class);

    public static String youtubeFind(String id) {
        YouTube youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, request -> {
        }).setApplicationName("youtube-cmdline-search-sample").build();

        YouTube.Videos.List search;
        try {
            search = youtube.videos().list("snippet, statistics, contentDetails");
            Optional<Config> apiKey = Configs.getSingleProperty("apiKey");
            if (apiKey.isPresent()) {
                search.setKey(apiKey.get().getValue());
            } else {
                logger.error("There was an exception attempting to youtube search. API key was not found");
                return "There was a technical issue that Magnus needs to look at.";
            }

            search.setMaxResults(1L);
            search.setId(id);

            VideoListResponse searchResponse = search.execute();
            List<Video> searchResultList = searchResponse.getItems();
            Video video = searchResultList.get(0);

            StringBuilder str = new StringBuilder();

            str.append(getVideoInfo(video, str, video.getSnippet().getTitle()));
            str.append("https://www.youtube.com/watch?v=").append(video.getId());

            return str.toString();
        } catch (IOException e) {
            logger.error("There was an exception attempting to youtube search", e);
        }

        return null;
    }

    private static String getVideoInfo(Video video, StringBuilder str, String title) {
        BigInteger views = BigInteger.valueOf(0L);
        BigInteger rating = BigInteger.valueOf(0L);
        BigInteger dislikes = BigInteger.valueOf(0L);
        String time = video.getContentDetails().getDuration().split("PT")[1].toLowerCase();

        if (video.getStatistics() != null) {
            views = video.getStatistics().getViewCount() == null ? BigInteger.valueOf(0L) : video.getStatistics().getViewCount();
            rating = video.getStatistics().getLikeCount() == null ? BigInteger.valueOf(0L) : video.getStatistics().getLikeCount();
            dislikes = video.getStatistics().getDislikeCount() == null ? BigInteger.valueOf(0L) : video.getStatistics().getDislikeCount();
        }
        String uploader = video.getSnippet().getChannelTitle();

        str.append(Colors.BOLD);
        str.append(title);
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
        return str.toString();
    }


    public static String youtubeSearch(String searchTerm) {

        YouTube youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, request -> {
        }).setApplicationName("youtube-cmdline-search-sample").build();

        YouTube.Search.List search;
        try {
            search = youtube.search().list("id,snippet");

            Optional<Config> apiKey = Configs.getSingleProperty("apiKey");
            if (apiKey.isPresent()) {
                search.setKey(apiKey.get().getValue());
            } else {
                logger.error("There was an exception attempting to youtube search. API key was not found");
                return "There was a technical issue that Magnus needs to look at.";
            }
            search.setQ(searchTerm.substring(3));
            search.setType("video");
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
            search.setMaxResults(1L);

            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();
            if (searchResultList != null) {
                SearchResult video = searchResultList.get(0);

                StringBuilder str = new StringBuilder();

                YouTube.Videos.List videoRequest = youtube.videos().list("snippet, statistics, contentDetails");
                videoRequest.setId(video.getId().getVideoId());
                videoRequest.setKey(apiKey.get().getValue());
                VideoListResponse listResponse = videoRequest.execute();
                List<Video> videoList = listResponse.getItems();

                Video targetVideo = videoList.get(0);
                getVideoInfo(targetVideo, str, video.getSnippet().getTitle());
                str.append("https://www.youtube.com/watch?v=").append(video.getId().getVideoId());

                return str.toString();
            }

        } catch (IOException e) {
            logger.error("There was an exception attempting to youtube search", e);
        }

        return null;

    }

}
