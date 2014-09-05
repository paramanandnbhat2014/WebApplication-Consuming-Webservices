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
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;

/**
 *
 * @author Pammy
 */
@Named(value = "reviewController")
@RequestScoped
public class ReviewController {

    private final static String REVIEW_PAGE_LIMIT = "5";
    private final static String SEARCH_PAGE_LIMIT = "5";
    private final static String REVIEW_PAGE = "1";
    private final static String SEARCH_PAGE = "1";
    private final static String COUNTRY = "us";
    private final static String RETTEN_TAMATOES_API_KEY = "93yb8zxyj59qnbyu7efrxr62";
    private final static String SEARCH_PROTECTED_URL = "http://api.rottentomatoes.com/api/public/v1.0/movies.json?page_limit=1&page=1&apikey=93yb8zxyj59qnbyu7efrxr62&q=";
    private final static String REVIEW_PROTECTED_URL_PART1 = "http://api.rottentomatoes.com/api/public/v1.0/movies/";
    private final static String REVIEW_PROTECTED_URL_PART2 = "/reviews.json?review_type=top_critic&page_limit=10&page=1&country=us&apikey=93yb8zxyj59qnbyu7efrxr62";
    private Review review;
    private ArrayList<Review> reviews;
    private String queryWord;
    private String movieID;
    private String movieName;
    private String resultMessage;

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public String getQueryWord() {
        return queryWord;
    }

    public void setQueryWord(String queryWord) {
        this.queryWord = queryWord;
    }

    public String getMovieID() {
        return movieID;
    }

    public void setMovieID(String movieID) {
        this.movieID = movieID;
    }

    /**
     * Creates a new instance of ReviewController
     */
    public ReviewController() {
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

    //get search movies and parse them
    public void getSearchMovie() throws MalformedURLException, IOException {
        String query = queryWord.replaceAll(" ", "+");
        String url = SEARCH_PROTECTED_URL + query;
        String jsonText = getJsonResponse(url);
        searchMovieJsonParser(jsonText);
    }

    //search parser
    public void searchMovieJsonParser(String str) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(new ByteArrayInputStream(str.getBytes()), "UTF-8"));
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("movies")) {
                movieID = readMoveID(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        reader.close();
    }

    //read the movie ids and title in terms of getting the review for each movie id in the next step
    public String readMoveID(JsonReader reader) throws IOException {
        String id = null;
        reader.beginArray();
        while (reader.hasNext()) {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("id")) {
                    id = reader.nextString();
                } else if (name.equals("title")) {
                    movieName = reader.nextString();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        }
        reader.endArray();
        return id;
    }

    //review parser
    public void getMovieReviews() throws MalformedURLException, IOException {
        //String url = "http://api.rottentomatoes.com/api/public/v1.0/lists/movies/upcoming.json?page_limit=2&page=1&country=us&apikey=93yb8zxyj59qnbyu7efrxr62";
        String url = REVIEW_PROTECTED_URL_PART1
                + movieID
                + REVIEW_PROTECTED_URL_PART2;
        String jsonText = getJsonResponse(url);
        MovieReviewJsonParser(jsonText);
    }

    public void MovieReviewJsonParser(String str) throws IOException {
        reviews = new ArrayList<Review>();
        JsonReader reader = new JsonReader(new InputStreamReader(new ByteArrayInputStream(str.getBytes()), "UTF-8"));
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("reviews")) {
                reviews = readMessages(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        reader.close();
    }

    //parse the review json response
    public ArrayList<Review> readMessages(JsonReader reader) throws IOException {
        ArrayList<Review> rs = new ArrayList<Review>();

        reader.beginArray();
        while (reader.hasNext()) {
            reader.beginObject();
            String critic = null;
            String date = null;
            String quote = null;
            while (reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                    case "critic":
                        critic = reader.nextString();
                        break;
                    case "date":
                        date = reader.nextString();
                        break;
                    case "quote":
                        quote = reader.nextString();
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }
            Review r = new Review();
            r.setCritic(critic);
            r.setDate(date);
            r.setQuote(quote);
            rs.add(r);
            reader.endObject();
        }
        reader.endArray();
        return rs;
    }

    //when user click the search button, the method will be called
    public void sendRequest() throws MalformedURLException, IOException {
        getSearchMovie();
        getMovieReviews();
        if (reviews.isEmpty()) {
            resultMessage = "Sorry, no comment is found.";
        }
    }
}
