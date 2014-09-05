/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.monash.assg1;

import java.io.Serializable;
import javax.el.ELContext;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author Pammy
 */
@Named(value = "showMovieController")
@RequestScoped
public class ShowMovieController implements Serializable {

    private String movieId;
    private int selectedMovieID;
    private String pageFrom;

    public String getPageFrom() {
        return pageFrom;
    }

    public void setPageFrom(String pageFrom) {
        this.pageFrom = pageFrom;
    }

    public int getSelectedMovieID() {
        return selectedMovieID;
    }

    public void setSelectedMovieID(int selectedMovieID) {
        this.selectedMovieID = selectedMovieID;
    }
    private Movie movie;

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    /**
     * Creates a new instance of showMovieController
     */
    // Grab the form data that we require
    public ShowMovieController() {
        movieId = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap()
                .get("v");
        selectedMovieID = Integer.valueOf(FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap()
                .get("s"));
        pageFrom = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap()
                .get("pageFrom");

        movie = getMovie();
    }

    public Movie getMovie() {
        if (movie == null) {
            // Get application context bean MovieApplication
            ELContext context = FacesContext.getCurrentInstance().getELContext();
            MovieController app = (MovieController) FacesContext.getCurrentInstance()
                    .getApplication()
                    .getELResolver()
                    .getValue(context, null, "movieController");
            // -1 to movieId since we +1 in JSF (to always have positive movie id!)
            switch (pageFrom) {
                // if the perious page is the home page
                case "home":
                    return app.getPmovies().get(selectedMovieID);
                // if the perious page is the search page
                case "search":
                    return app.getMovies().get(selectedMovieID);
            }
        }
        return movie;
    }
}
