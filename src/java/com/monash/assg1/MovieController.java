/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.monash.assg1;

import javax.inject.Named;
import javax.enterprise.context.ApplicationScoped;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FlickrApi;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

/**
 *
 * @author Pammy
 */
@Named(value = "movieController")
@ApplicationScoped
public class MovieController {

    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private static final long NUMBER_OF_VIDEOS_RETURNED = 10;
    private static final long NUMBER_OF_POPULARVIDEOS_RETURNED = 6;
    private static YouTube youtube;
    private String queryTerm;  //the content from the search box
    private ArrayList<Movie> movies;
    private ArrayList<Movie> pmovies;
    private String resultMessage;
    private String serverCheck;

    public String getServerCheck() {
        return serverCheck;
    }

    public void setServerCheck(String serverCheck) {
        this.serverCheck = serverCheck;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public ArrayList<Movie> getPmovies() {
        return pmovies;
    }

    public void setPmovies(ArrayList<Movie> pmovies) {
        this.pmovies = pmovies;
    }

    public ArrayList<Movie> getMovies() {
        return movies;
    }

    public void setMovies(ArrayList<Movie> movies) {
        this.movies = movies;
    }

    public String getQueryTerm() {
        return queryTerm;
    }

    public void setQueryTerm(String queryTerm) {
        this.queryTerm = queryTerm;
    }

    public MovieController() {
        this.getPopularMovies();
    }

    //get popular movires for the home page
    public void getPopularMovies() {
        pmovies = new ArrayList<Movie>();
        try {
            youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
                @Override
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("Movie Finder").build();
            YouTube.Videos.List searchPopularVideos = youtube.videos().list("id,snippet,contentDetails");
            String apiKey = "AIzaSyAEoFK2hvrd0uVCY6nUWayk-toZ0FdkFcE";
            searchPopularVideos.setKey(apiKey);
            searchPopularVideos.setChart("mostPopular");
            //searchPopularVideos.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/medium/url,snippet/thumbnails/default/url,snippet/description,snippet/publishedAt,snippet/channelTitle)");
            searchPopularVideos.setMaxResults(NUMBER_OF_POPULARVIDEOS_RETURNED);

            VideoListResponse searchResponse = searchPopularVideos.execute();

            List<Video> searchResultList = searchResponse.getItems();

            //deal with the response and store them into arraylist
            if (searchResultList != null) {
                Iterator<Video> iteratorSearchResults = searchResultList.iterator();
                while (iteratorSearchResults.hasNext()) {
                    Video video = iteratorSearchResults.next();
                    Movie m = new Movie();
                    m.setId(video.getId());
                    m.setTitle(video.getSnippet().getTitle());
                    m.setDesc(video.getSnippet().getDescription());
                    m.setMediumUrl(video.getSnippet().getThumbnails().getMedium().getUrl());
                    m.setDefaultUrl(video.getSnippet().getThumbnails().getDefault().getUrl());
                    //Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    //m.setPublishAt(formatter.format(video.getSnippet().getPublishedAt()));
                    m.setChannelTitle(video.getSnippet().getChannelTitle());
                    m.setDuration(video.getContentDetails().getDuration());
                    m.setDefinition(video.getContentDetails().getDefinition());
                    this.pmovies.add(m);
                }
            }
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
        }
    }

    //when the user click the search button inthe search page, this method will called
    public void sendRequest() {
        serverCheck = null;
        if (!queryTerm.contains("violence")) {
            movies = new ArrayList<Movie>();
            try {
                youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
                    @Override
                    public void initialize(HttpRequest request) throws IOException {
                    }
                }).setApplicationName("Movie Finder").build();
                YouTube.Search.List search = youtube.search().list("id,snippet");
                String apiKey = "AIzaSyAEoFK2hvrd0uVCY6nUWayk-toZ0FdkFcE";
                search.setKey(apiKey);
                search.setQ(queryTerm);

                search.setType("video");
                search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/medium/url,snippet/thumbnails/default/url,snippet/description,snippet/publishedAt,snippet/channelTitle)");
                search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

                SearchListResponse searchResponse = search.execute();

                List<SearchResult> searchResultList = searchResponse.getItems();

                if (searchResultList != null) {
                    Iterator<SearchResult> iteratorSearchResults = searchResultList.iterator();
                    while (iteratorSearchResults.hasNext()) {
                        SearchResult video = iteratorSearchResults.next();
                        Movie m = new Movie();
                        m.setId(video.getId().getVideoId());
                        m.setTitle(video.getSnippet().getTitle());
                        m.setDesc(video.getSnippet().getDescription());
                        m.setMediumUrl(video.getSnippet().getThumbnails().getMedium().getUrl());
                        m.setDefaultUrl(video.getSnippet().getThumbnails().getDefault().getUrl());
                        //Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        //m.setPublishAt(formatter.format(video.getSnippet().getPublishedAt()));
                        m.setChannelTitle(video.getSnippet().getChannelTitle());
                        this.movies.add(m);
                    }
                }

                if (movies.isEmpty()) {
                    resultMessage = "No result is found.";
                }
            } catch (GoogleJsonResponseException e) {
                System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                        + e.getDetails().getMessage());
            } catch (IOException e) {
                System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        else{
            serverCheck = "Sensitive word 'violence' not allowed. ";
        }
    }
    /*
     public String getPopularPhotos() {
     OAuthService service = new ServiceBuilder()
     .provider(FlickrApi.class)
     .apiKey("daff238b038ff3bf3faf9a7d37445d49")
     .apiSecret("bdde5cd03c01abc4")
     .build();
     Token requestToken = service.getRequestToken();
     String authUrl = service.getAuthorizationUrl(requestToken);
     return authUrl;
     }
     */
}
