package com.mendix.demo.web.model;

import java.util.List;

/**
 * Created by naseers on 15/04/2018.
 */
public class Result {

    private String title;
    private String[] categories;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getCategories() {
        return categories;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }
}
