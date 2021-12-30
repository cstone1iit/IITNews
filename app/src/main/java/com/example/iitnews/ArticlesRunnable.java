package com.example.iitnews;

import android.net.Uri;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

public class ArticlesRunnable implements Runnable{

    private final MainActivity mainActivity;
    private final String source;

    private static final String URL = "https://newsapi.org/v2/top-headlines";
    private static final String myAPIKey = "0d390ea776a64ff8b84e21df54c94a80";



    ArticlesRunnable(MainActivity mainActivity, String source){
        this.mainActivity = mainActivity;
        this.source = source;
    }

    @Override
    public void run() {


        Uri.Builder buildURL = Uri.parse(URL).buildUpon();
        buildURL.appendQueryParameter("sources", source);
        buildURL.appendQueryParameter("apiKey", myAPIKey);
        String urlUseable = buildURL.build().toString();

        StringBuilder stringBuilder = new StringBuilder();
        try {
            java.net.URL url = new URL(urlUseable);

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("User-Agent","");
            connection.connect();

            if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) { //connection not ok, error
                InputStream inputStream = connection.getErrorStream();
                BufferedReader bufferedReader = new BufferedReader((new InputStreamReader(inputStream)));

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append('\n');
                }
                errorHandle(stringBuilder.toString());
                return;
            }

            InputStream inputStream = connection.getInputStream(); //connection ok
            BufferedReader bufferedReader = new BufferedReader((new InputStreamReader(inputStream)));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }


        } catch (Exception exception) {
            resultHandle(null);
            return;
        }
        resultHandle(stringBuilder.toString());

    }

    public void errorHandle(String string){
        String message = "Error: ";
        try{
            JSONObject jsonObject = new JSONObject(string);
            message += jsonObject.getString("message");

        } catch (JSONException exception){
            message += exception.getMessage();
        }
        String errorMessage = message;
        mainActivity.runOnUiThread(() -> mainActivity.errorHandle(errorMessage));
    }

    public void resultHandle(final String jString){
        if (jString == null){
            mainActivity.runOnUiThread(mainActivity::downloadFail);
            return;
        }
        final ArrayList<NewsArticle> articlesList = parseJson(jString);
        mainActivity.runOnUiThread(() -> {
            if(articlesList != null){
                Toast.makeText(mainActivity, "Loaded " + articlesList.size() + " articles", Toast.LENGTH_SHORT).show();
            }
            mainActivity.dataUpdateArticles(articlesList);
        });

    }

    private ArrayList<NewsArticle> parseJson(String string){

        ArrayList<NewsArticle> articlesList = new ArrayList<>();

        try{
            JSONObject jsonObject = new JSONObject(string);

            //Get Sources array
            JSONArray articleArray = jsonObject.getJSONArray("articles");

            for (int i = 0; i< articleArray.length(); i++){
                JSONObject article = (JSONObject) articleArray.get(i);

                String author;
                if(article.has("author")){
                    author = article.getString("author");
                    if(author.equals("null")) {
                        author = "";
                    }
                }else{
                    author = "";
                }

                String title;
                if(article.has("title")){
                    title = article.getString("title");
                    if(title.equals("null")) {
                        title = "";
                    }
                }else{
                    title = "";
                }

                String description;
                if(article.has("description")){
                    description = article.getString("description");
                    if(description.equals("null")) {
                        description = "";
                    }
                }else{
                    description = "";
                }

                String url;
                if(article.has("url")){
                    if(article.isNull("url") == false) {
                        url = article.getString("url");
                    } else{
                        url = "";
                    }
                }else{
                    url = "";
                }

                String urlToImage;
                if(article.has("urlToImage")){
                    urlToImage = article.getString("urlToImage");
                    if(urlToImage.equals("null")){
                        urlToImage = null;
                    }
                }else{
                    urlToImage = null;
                }

                String publishDate;
                if(article.has("publishedAt")){
                    if(article.isNull("publishedAt") == false) {
                        publishDate = article.getString("publishedAt");
                        publishDate = convertDate(publishDate);
                    } else{
                        publishDate = "";
                    }
                }else{
                    publishDate = "";
                }

                NewsArticle newsArticle = new NewsArticle(author, title, description, url, urlToImage, publishDate);
                articlesList.add(newsArticle);


            }
            return articlesList;

        }catch (Exception exception){
            exception.printStackTrace();
        }
        return null;
    }

    private String convertDate(String date){
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat output = new SimpleDateFormat("MMM d, yyyy HH:mm");

        String [] returnString = date.split(":");
        String endStr = returnString[2];
        endStr = endStr.substring(0,2) + "Z"; //Truncate the seconds off the date/time
        String newString = returnString[0] + ":" + returnString[1] + ":" + endStr;

        Date d = null;
        try
        {
            d = input.parse(newString);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return output.format(d);
    }

}
