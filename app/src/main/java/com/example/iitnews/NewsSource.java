package com.example.iitnews;

import android.util.JsonWriter;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;

public class NewsSource implements Comparable<NewsSource>, Serializable {

    private String language;
    private int langID;
    private String topicsCategory;
    private String country;
    private String newsID;
    private String newsName;

    public NewsSource (String language, String topicsCategory, String country, String newsID, String newsName){
        this.language = language;
        this.topicsCategory = topicsCategory;
        this.country = country;
        this.newsID = newsID;
        this.newsName = newsName;
    }

    String getLanguage(){return language;}
    String getTopicsCategory(){return topicsCategory;}
    String getCountry(){return country;}
    String getNewsID(){return newsID;}
    String getNewsName(){return newsName;}

    void setLanguage(String language){this.language = language;}
    void setTopicsCategory(String topicsCategory){this.topicsCategory = topicsCategory;}
    void setCountry(String country){this.country = country;}
    void setNewsID(String newsID){this.newsID = newsID;}
    void setNewsName(String newsName){this.newsName = newsName;}


    @NonNull
    public String toString() {
        return newsName;
    }


    @Override
    public int compareTo(NewsSource newsSource) {
        {
            return newsName.compareTo(newsSource.newsName);
        }
    }
}
