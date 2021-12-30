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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class SourcesRunnable implements Runnable {

    private final MainActivity mainActivity;
    private final ArrayList<String> categoryList = new ArrayList<>();
    private final ArrayList<String> languageList = new ArrayList<>();
    private final ArrayList<String> countryList = new ArrayList<>();

    private Map<String, NewsSource> sourcesMap = new HashMap<>();
    private Map<String, ArrayList<String>> categoryMap = new HashMap<>();
    private Map<String, ArrayList<String>> languageMap = new HashMap<>();
    private Map<String, ArrayList<String>> countryMap = new HashMap<>();

    private boolean contains;

    private static final String URL = "https://newsapi.org/v2/sources";
    private static final String myAPIKey = "0d390ea776a64ff8b84e21df54c94a80";

    SourcesRunnable(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }



    @Override
    public void run() {


        Uri.Builder buildURL = Uri.parse(URL).buildUpon();

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
        final ArrayList<String> sourcesList = parseJson(jString);
        mainActivity.runOnUiThread(() -> {
            if(sourcesList != null){
                Toast.makeText(mainActivity, "Loaded " + sourcesList.size() + " sources", Toast.LENGTH_SHORT).show();
            }
            mainActivity.dataUpdateSources(sourcesList, categoryList, languageList, countryList, categoryMap, languageMap, countryMap, sourcesMap);
        });

    }

    private ArrayList<String> parseJson(String string){

        ArrayList<String> sourcesList = new ArrayList<>();

        try{
            JSONObject jsonObject = new JSONObject(string);

            //Get Sources array
            JSONArray sourceArray = jsonObject.getJSONArray("sources");

            for (int i = 0; i< sourceArray.length(); i++){
                JSONObject source = (JSONObject) sourceArray.get(i);

                String id = source.getString("id");
                String name = source.getString("name");

                String category = source.getString("category");
                contains = categoryList.contains(category);
                if (!contains){ //add category to category list
                    categoryList.add(category);
                    //Toast.makeText(mainActivity, category + " added to categories list", Toast.LENGTH_SHORT).show();
                }

                String language = source.getString("language");
                contains = languageList.contains(language);
                if (!contains){ //add category to category list
                    languageList.add(language);
                    //Toast.makeText(mainActivity, language + " added to categories list", Toast.LENGTH_SHORT).show();
                }

                String country = source.getString("country");
                contains = countryList.contains(country);
                if (!contains){ //add category to category list
                    countryList.add(country);
                    //Toast.makeText(mainActivity, country + " added to categories list", Toast.LENGTH_SHORT).show();
                }

                NewsSource newsSource = new NewsSource(language, category, country, id, name);

                sourcesList.add(newsSource.getNewsName());

                sourcesMap.put(newsSource.getNewsName().toString(), newsSource);

                if (!categoryMap.containsKey(newsSource.getTopicsCategory())){
                    categoryMap.put(newsSource.getTopicsCategory(),new ArrayList<>());
                }
                ArrayList<String> categoryTemp = categoryMap.get(newsSource.getTopicsCategory());
                categoryTemp.add(newsSource.getNewsName().toString());
                //Collections.sort(categoryTemp);
                categoryMap.put(newsSource.getTopicsCategory(), categoryTemp);


                if (!languageMap.containsKey(newsSource.getLanguage())){
                    languageMap.put(newsSource.getLanguage(),new ArrayList<>());
                }
                ArrayList<String> languageTemp = languageMap.get(newsSource.getLanguage());
                languageTemp.add(newsSource.getNewsName().toString());
                //Collections.sort(languageTemp);
                languageMap.put(newsSource.getLanguage(), languageTemp);

                if (!countryMap.containsKey(newsSource.getCountry())){
                    countryMap.put(newsSource.getCountry(),new ArrayList<>());
                }
                ArrayList<String> countryTemp = countryMap.get(newsSource.getCountry());
                countryTemp.add(newsSource.getNewsName().toString());
                //Collections.sort(countryTemp);
                countryMap.put(newsSource.getCountry(), countryTemp);


            }
            Collections.sort(sourcesList);

            categoryList.add("all");
            Collections.sort(categoryList);

            languageList.add("all");
            Collections.sort(languageList);

            countryList.add("all");
            Collections.sort(countryList);

            categoryMap.put("all", sourcesList);
            languageMap.put("all", sourcesList);
            countryMap.put("all", sourcesList);

            return sourcesList;

        }catch (Exception exception){
            exception.printStackTrace();
        }
        return null;
    }

}
