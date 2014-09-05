/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.monash.assg1;

import com.google.gson.stream.JsonReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

/**
 *
 * @author Pammy
 */
@Named(value = "twitterController")
@SessionScoped
public class TwitterController implements Serializable {

    private OAuthService service;
    private Token requestToken;
    private String verifier;
    private ArrayList<Tweet> tweets;

    public ArrayList<Tweet> getTweets() {
        return tweets;
    }

    public void setTweets(ArrayList<Tweet> tweets) {
        this.tweets = tweets;
    }

    public String getVerifier() {
        return verifier;
    }

    public void setVerifier(String verifier) {
        this.verifier = verifier;
    }
    /**
     * Creates a new instance of TwitterController
     */
    public TwitterController() {
    }
    
    // send a request, and get the auth url
     public String getTwitterAuth() {
        service = new ServiceBuilder()
                .provider(TwitterApi.class)
                .apiKey("UkLbh0kLEQiwxQecPaHfKQ")
                .apiSecret("urVAVu3rbbTsD89qYieVSBn7QIPu8uuF0N9wSEsUnk")
                .build();
        requestToken = service.getRequestToken();
        String authUrl = service.getAuthorizationUrl(requestToken);
        return authUrl;
    }

     //confirm the verifier that the user inputs and send the sign request with the accessToken for the json response
    public void confirmVerifier() throws IOException {
        Token accessToken = service.getAccessToken(requestToken, new Verifier(this.verifier));
        OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/statuses/user_timeline.json");
        service.signRequest(accessToken, request);
        Response response = request.send();
        String body = response.getBody();
        tweetJsonParser(body);
    }

    //parse the response
    public void tweetJsonParser(String str) throws IOException {
        tweets = new ArrayList<Tweet>();
        JsonReader reader = new JsonReader(new InputStreamReader(new ByteArrayInputStream(str.getBytes()), "UTF-8"));
        reader.beginArray();
        while (reader.hasNext()) {
            tweets.add(readMessage(reader));
        }
        reader.endArray();
        reader.close();
    }

    public Tweet readMessage(JsonReader reader) throws IOException {
        Tweet tweet = null;
        String text = null;
        String favorite_count = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("user")) {
                tweet = readUser(reader);
            } else if (name.equals("text")) {
                text = reader.nextString();
            } else if (name.equals("favorite_count")) {
                favorite_count = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        tweet.setText(text);
        tweet.setFavorite_count(favorite_count);
        reader.endObject();
        return tweet;
    }

    public Tweet readUser(JsonReader reader) throws IOException {
        String created_at = null;
        String text = null;
        String screen_name = null;
        String favorite_count = null;
        String profile_image_url = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("created_at")) {
                created_at = reader.nextString();
            } else if (name.equals("screen_name")) {
                screen_name = reader.nextString();
            } else if (name.equals("profile_image_url")) {
                profile_image_url = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        Tweet t = new Tweet();
        t.setCreated_at(created_at);
        t.setProfile_image_url(profile_image_url);
        t.setScreen_name(screen_name);
        reader.endObject();
        return t;
    }
}
