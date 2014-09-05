/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.monash.assg1;

import com.google.gson.stream.JsonReader;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import javax.enterprise.context.RequestScoped;

/**
 *
 * @author Pammy
 */
@Named(value = "upcomingMovieController")
@RequestScoped
public class UpcomingMovieController implements Serializable {

    private final static String PAGE_LIMIT = "5";
    private final static String PAGE = "1";
    private final static String COUNTRY = "us";
    private final static String RETTEN_TAMATOES_API_KEY = "93yb8zxyj59qnbyu7efrxr62";
    private final static String PROTECTED_URL = "http://api.rottentomatoes.com/api/public/v1.0/lists/movies/upcoming.json?";
    private UpcomingMovie uMovie;
    private ArrayList<UpcomingMovie> uMovies;

    public UpcomingMovie getuMovie() {
        return uMovie;
    }

    public void setuMovie(UpcomingMovie uMovie) {
        this.uMovie = uMovie;
    }

    public ArrayList<UpcomingMovie> getuMovies() {
        return uMovies;
    }

    public void setuMovies(ArrayList<UpcomingMovie> uMovies) {
        this.uMovies = uMovies;
    }

    public UpcomingMovieController() throws MalformedURLException, IOException {
        getUpcomingMovie();
    }

    //get json response from a specified url
    public String getJsonResponse(String url) throws MalformedURLException, IOException {
        InputStream is = new URL(url).openStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        String jsonText = readJsonFile(br);
        return jsonText;
    }

    //append the string from bufferred reader line by line
    public String readJsonFile(BufferedReader br) throws IOException {
        StringBuilder builder = new StringBuilder();
        int c;
        while ((c = br.read()) != -1) {
            builder.append((char) c);
        }
        return builder.toString();
    }

    //get json response for a url with specified parameters
    public void getUpcomingMovie() throws MalformedURLException, IOException {
        //String url = "http://api.rottentomatoes.com/api/public/v1.0/lists/movies/upcoming.json?page_limit=2&page=1&country=us&apikey=93yb8zxyj59qnbyu7efrxr62";
        String url = PROTECTED_URL
                + "page_limit=" + PAGE_LIMIT
                + "&page=" + PAGE
                + "&country=" + COUNTRY
                + "&apikey=" + RETTEN_TAMATOES_API_KEY;
        String jsonText = getJsonResponse(url);
        upcomingMovieJsonParser(jsonText);
    }

    //grab the attributes we need
    public void upcomingMovieJsonParser(String str) throws IOException {
        uMovies = new ArrayList<UpcomingMovie>();
        JsonReader reader = new JsonReader(new InputStreamReader(new ByteArrayInputStream(str.getBytes()), "UTF-8"));
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("movies")) {
                uMovies = readMessages(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        reader.close();
    }

    public ArrayList<UpcomingMovie> readMessages(JsonReader reader) throws IOException {
        ArrayList<UpcomingMovie> ums = new ArrayList<UpcomingMovie>();

        reader.beginArray();
        while (reader.hasNext()) {
            reader.beginObject();
            String title = null;
            String theater = null;
            String audience_score = null;
            String thumbnail = null;
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("title")) {
                    title = reader.nextString();
                } else if (name.equals("release_dates")) {
                    theater = readRelease_dates(reader);
                } else if (name.equals("ratings")) {
                    audience_score = readRatings(reader);
                } else if (name.equals("posters")) {
                    thumbnail = readPosters(reader);
                } else {
                    reader.skipValue();
                }
            }
            UpcomingMovie um = new UpcomingMovie();
            um.setTitle(title);
            um.setTheater(theater);
            um.setAudience_score(audience_score);
            um.setThumbnail(thumbnail);
            ums.add(um);
            reader.endObject();
        }
        reader.endArray();
        return ums;
    }

    public String readRelease_dates(JsonReader reader) throws IOException {
        String theater = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("theater")) {
                theater = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return theater;
    }

    public String readRatings(JsonReader reader) throws IOException {
        String audience_score = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("audience_score")) {
                audience_score = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return audience_score;
    }

    public String readPosters(JsonReader reader) throws IOException {
        String thumbnail = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("thumbnail")) {
                thumbnail = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return thumbnail;
    }
}
