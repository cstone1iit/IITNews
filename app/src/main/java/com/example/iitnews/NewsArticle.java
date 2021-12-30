package com.example.iitnews;

import android.util.JsonWriter;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;

public class NewsArticle implements Serializable {

    private String author;
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String publishDate;

    public NewsArticle (String author, String title, String description, String url, String urlToImage, String publishDate){
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.publishDate = publishDate;

    }

    String getAuthor(){return author;}
    String getTitle(){return title;}
    String getDescription(){return description;}
    String getUrl(){return url;}
    String getUrlToImage(){return urlToImage;}
    String getPublishDate(){return publishDate;}


    void setAuthor(String author){this.author = author;}
    void setTitle(String title){this.title = title;}
    void setDescription(String description){this.description = description;}
    void setUrl(String url){this.url = url;}
    void setUrlToImage(String urlToImage){this.urlToImage = urlToImage;}
    void setPublishDate(String publishDate){this.publishDate = publishDate;}


    @NonNull
    public String toString(){
        try{
            StringWriter sWriter = new StringWriter();
            JsonWriter jWriter = new JsonWriter(sWriter);
            jWriter.beginObject();

            jWriter.name("author").value(getAuthor());
            jWriter.name("title").value(getTitle());
            jWriter.name("description").value(getDescription());
            jWriter.name("url").value(getUrl());
            jWriter.name("urlToImage").value(getUrlToImage());
            jWriter.name("publishDate").value(getPublishDate());

            jWriter.endObject();
            jWriter.close();
            return sWriter.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";

    }

}
