package com.example.iitnews;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CountryCodeRunnable implements Runnable{

    private final MainActivity mainActivity;

    public CountryCodeRunnable(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {
        try{
            InputStream inputStream = mainActivity.getResources().openRawResource(R.raw.country_codes);
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder stringResult = new StringBuilder();
            for (String l; (l = bReader.readLine()) != null;){
                stringResult.append(l);
            }

            Map<String, String> countryCodeMap = new HashMap<>();
            JSONObject jObject = new JSONObject(stringResult.toString());
            JSONArray countriesArray = jObject.getJSONArray("countries");

            for (int i = 0; i < countriesArray.length(); i++){
                JSONObject country = countriesArray.getJSONObject(i);
                String countryCode = country.getString("code").toLowerCase(Locale.ROOT);
                String countryName = country.getString("name");

                countryCodeMap.put(countryCode, countryName);

            }


            mainActivity.runOnUiThread(()-> mainActivity.acceptCountryResults(countryCodeMap));

        }catch (Exception e){
            mainActivity.runOnUiThread(() -> mainActivity.acceptCountryResults(null));
        }


    }
}