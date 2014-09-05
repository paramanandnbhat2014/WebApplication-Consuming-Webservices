/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.monash.assg1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

/**
 *
 * @author Pammy
 */
@Named(value = "flickrController")
@RequestScoped
public class FlickrController {

    private Flickr flickr;       //flickr object
    private ArrayList<Flickr> flickrs;  //store flickr objects parsed from the json response from photo.getSizes service
    private String queryImg;   //store the user input from the search textbox
    private String imgId;   // string object
    private ArrayList<String> imgIds;   //store string objects parsed from the json response from photo.search service
    private String resultMessage; //if no json response return, return this message to the web page

    public ArrayList<String> getImgIds() {
        return imgIds;
    }

    public void setImgIds(ArrayList<String> imgIds) {
        this.imgIds = imgIds;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public Flickr getFlickr() {
        return flickr;
    }

    public void setFlickr(Flickr flickr) {
        this.flickr = flickr;
    }

    public ArrayList<Flickr> getFlickrs() {
        return flickrs;
    }

    public void setFlickrs(ArrayList<Flickr> flickrs) {
        this.flickrs = flickrs;
    }

    public String getQueryImg() {
        return queryImg;
    }

    public void setQueryImg(String queryImg) {
        this.queryImg = queryImg;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public FlickrController() {

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
    
    //get photo ids and store them into imgIds in terms of getting photo url in the nest step
    public void getSearchImg() throws MalformedURLException, IOException {
        String query = queryImg.replaceAll(" ", "+");
        String url = "http://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=daff238b038ff3bf3faf9a7d37445d49&per_page=9&page=1&format=json&nojsoncallback=1&text=" + query;
        String jsonText = getJsonResponse(url);
        searchImgJsonParser(jsonText);   
    }

    //photo id parser
    public void searchImgJsonParser(String str) throws IOException {
        
        JsonObject json = Json.createReader(new StringReader(str)).readObject();
        JsonArray photo;
        photo = json.getJsonObject("photos").getJsonArray("photo");
        int size = photo.size();
        imgIds = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            // Create a new Tweet object for each result
            JsonObject row = photo.getJsonObject(i);
            String id = row.getString("id");
            imgIds.add(id);
        }
    }

    //According to the ids in the arraylist "imgIds", get the photo url for each photo id. 
    public void getPhotoUrl() throws MalformedURLException, IOException {
        flickrs = new ArrayList<>();
        for (int i = 0; i < imgIds.size(); i++) {
            String url = "http://api.flickr.com/services/rest/?method=flickr.photos.getSizes&api_key=daff238b038ff3bf3faf9a7d37445d49&format=json&nojsoncallback=1&photo_id=" + imgIds.get(i);
            String jsonText = getJsonResponse(url);
            String result = photoUrlJsonParser(jsonText);
            Flickr fr = new Flickr();
            fr.setUrl(result);
            flickrs.add(fr);
        }
    }

    //photo url parser
    public String photoUrlJsonParser(String str) throws IOException {
        String url = null;
        JsonObject json = Json.createReader(new StringReader(str)).readObject();
        JsonArray imgSize = json.getJsonObject("sizes").getJsonArray("size");
        int size = imgSize.size();

        for (int i = 0; i < size; i++) {
            // Create a new Tweet object for each result
            JsonObject row = imgSize.getJsonObject(i);
            if (row.getString("label").equals("Small 320")) {
                url = row.getString("source");
                break;
            }
        }
        return url;
    }

    /**
     *
     * @throws MalformedURLException
     * @throws IOException
     */
    //when user click the search button, the method will be called
    public void sendRequest() throws MalformedURLException, IOException {
        getSearchImg();
        getPhotoUrl();
        if (flickrs.isEmpty()) {
            resultMessage = "Sorry, no photo is found.";
        }
    }
}
