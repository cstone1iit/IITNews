package com.example.iitnews;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LanguageCodeRunnable implements Runnable{

    private final MainActivity mainActivity;

    public LanguageCodeRunnable(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {
        try{
            InputStream inputStream = mainActivity.getResources().openRawResource(R.raw.language_codes);
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder stringResult = new StringBuilder();
            for (String l; (l = bReader.readLine()) != null;){
                stringResult.append(l);
            }

            Map<String, String> languageCodeMap = new HashMap<>();
            JSONObject jObject = new JSONObject(stringResult.toString());
            JSONArray languagesArray = jObject.getJSONArray("languages");

            for (int i = 0; i < languagesArray.length(); i++){
                JSONObject country = languagesArray.getJSONObject(i);
                String countryCode = country.getString("code").toLowerCase(Locale.ROOT);
                String countryName = country.getString("name");

                languageCodeMap.put(countryCode, countryName);

            }


            mainActivity.runOnUiThread(()-> mainActivity.acceptLanguageResults(languageCodeMap));

        }catch (Exception e){
            mainActivity.runOnUiThread(() -> mainActivity.acceptLanguageResults(null));
        }


    }
}
