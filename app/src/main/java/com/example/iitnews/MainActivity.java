package com.example.iitnews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;




public class MainActivity extends AppCompatActivity {

    private ArrayList<String> sourcesList = new ArrayList<>();
    private ArrayList<String> categoryList = new ArrayList<>();
    private ArrayList<String> languageList = new ArrayList<>();
    private ArrayList<String> countryList = new ArrayList<>();



    private Map<String, NewsSource> sourcesMap = new HashMap<>();
    private Map<String, ArrayList<String>> categoryMap = new HashMap<>();
    private Map<String, ArrayList<String>> languageMap = new HashMap<>();
    private Map<String, ArrayList<String>> countryMap = new HashMap<>();

    private Map<String, String> countryCodesMap = new HashMap<>();
    private Map<String, String> languageCodesMap = new HashMap<>();

    private ArrayList<String> countryCodesList = new ArrayList<>();
    private ArrayList<String> languageCodesList = new ArrayList<>();


    private boolean connection;
    private Menu menu;
    private String sourceSelected;

    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private String selection;
    private ArrayAdapter arrayAdapter;

    private ArticleAdapter articleAdapter;
    private ArrayList<NewsArticle> articlesList = new ArrayList<>();
    private ViewPager2 viewPager;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connection = hasNetworkConnection();
        if (connection == true){
            //Toast.makeText(this, "Network connection established.", Toast.LENGTH_LONG).show();
            grabSources();

        }else{
            //setTitle("No Internet Connection");
            Toast.makeText(this, "No network connection.", Toast.LENGTH_LONG).show();
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerList = findViewById(R.id.drawer_list);


        drawerList.setOnItemClickListener((parent, view, position, id) -> drawerItemSelected(position));

        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open_drawer,R.string.close_drawer);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

        }

        CountryCodeRunnable countryCodeRunnable = new CountryCodeRunnable(this);
        new Thread(countryCodeRunnable).start();

        LanguageCodeRunnable languageCodeRunnable = new LanguageCodeRunnable(this);
        new Thread(languageCodeRunnable).start();


        viewPager = findViewById(R.id.viewpager);

        articleAdapter = new ArticleAdapter(this, articlesList);
        viewPager.setAdapter(articleAdapter);
        viewPager.setOrientation(viewPager.ORIENTATION_HORIZONTAL);


    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    public void grabSources(){
        connection = hasNetworkConnection();
        if (connection == true){
            //Toast.makeText(this, "Network connection established.", Toast.LENGTH_SHORT).show();
            SourcesRunnable sourcesRunnable = new SourcesRunnable(this);
            new Thread(sourcesRunnable).start();

        }else{
            //setTitle("No Internet Connection");
            Toast.makeText(this, "No network connection.", Toast.LENGTH_SHORT).show();
        }
    }


    public void dataUpdateSources(ArrayList<String> sourceList, ArrayList<String> categories, ArrayList<String> languages, ArrayList<String> countries,
                                  Map<String,ArrayList<String>> catMap, Map<String,ArrayList<String>> langMap, Map<String,ArrayList<String>> couMap, Map<String, NewsSource> sourceMap){
        sourcesList.clear();
        sourcesList.addAll(sourceList);

        categoryList.clear();
        categoryList.addAll(categories);

        languageList.clear();
        languageList.addAll(languages);



        countryList.clear();
        countryList.addAll(countries);


        categoryMap.clear();
        categoryMap.putAll(catMap);

        languageMap.clear();
        languageMap.putAll(langMap);

        countryMap.clear();
        countryMap.putAll(couMap);

        sourcesMap.clear();
        sourcesMap.putAll(sourceMap);

        //dayAdapter.notifyItemChanged(0,weatherList.size());
        setTitle("IIT News (" + sourcesList.size() + ")");

        menu.clear();

        SubMenu topicsMenu = menu.addSubMenu("Topics"); //Group-ID: 0
        SubMenu countriesMenu = menu.addSubMenu("Countries"); //Group-ID: 1
        SubMenu languagesMenu = menu.addSubMenu("Languages"); //Group-ID: 2

        for (int i = 0; i < categoryList.size(); i++){
            topicsMenu.add(0, i, i, categoryList.get(i));
        }
        for (int i = 1; i < countryList.size(); i ++){
            String countryName = countryCodesMap.get(countryList.get(i));
            int index = countryCodesList.indexOf(countryName);
            countriesMenu.add(1, i, index, countryName);
        }
        for (int i = 1; i < languageList.size(); i ++){
            String languageName = languageCodesMap.get(languageList.get(i));
            int index = languageCodesList.indexOf(languageName);
            languagesMenu.add(2, i, index, languageName);
        }
        countriesMenu.add(1, 0, 0, "all");
        languagesMenu.add(1, 0, 0, "all");

        arrayAdapter = new ArrayAdapter<>(this,R.layout.drawer_list_item, sourcesList);
        drawerList.setAdapter(arrayAdapter);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        menu = m;
        return true;
    }

    public void setupMenu(View v){
        menu.clear();

        SubMenu topics = menu.addSubMenu("Topics"); //Group-ID: 0
        SubMenu countries = menu.addSubMenu("Countries"); //Group-ID: 1
        SubMenu languages = menu.addSubMenu("Languages"); //Group-ID: 2

        for (int i = 0; i < categoryList.size(); i++){
            topics.add(0, i, i, categoryList.get(i));
        }
        for (int i = 0; i < countryList.size(); i ++){
            countries.add(1, i, i, countryList.get(i));
        }
        for (int i = 0; i < languageList.size(); i ++){
            languages.add(2, i, i, languageList.get(i));

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        ArrayList<String> currentSources = new ArrayList<>();

        currentSources.addAll(sourcesList);

        if(drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        else if (item.hasSubMenu()){
            return true;
        }
        int parentId = item.getGroupId();
        int menuItem = item.getItemId();
        if (parentId == 0){ //topics submenu selected
            selection = categoryList.get(menuItem);
            sourcesList.clear();
            if (selection == "all"){
                sourcesList.addAll(categoryMap.get("all"));
            }else {
                ArrayList<String> temp = categoryMap.get(selection);
                for (String s : temp) {
                    if (currentSources.contains(s)) { //the previous filter and current filter match
                        sourcesList.add(s);
                    }
                }
            }

            //sourcesList.addAll(temp);
            if (sourcesList.size() == 0){
                AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);
                adBuilder.setTitle("Error");
                adBuilder.setMessage("No News Sources fit this selection of Topic: " + item.toString() );

                adBuilder.setPositiveButton("OK, Restore", (dialog, id) -> {
                    sourcesList.addAll(categoryMap.get("all"));
                    arrayAdapter.notifyDataSetChanged();
                    setTitle("IIT News (" + sourcesList.size() + ")");

                });


                AlertDialog locationDialog = adBuilder.create();
                locationDialog.show();
                return true;

            }else{
                Collections.sort(sourcesList);
                arrayAdapter.notifyDataSetChanged();
                setTitle("IIT News (" + sourcesList.size() + ")");
                Toast.makeText(this, "Sources List size " + sourcesList.size(), Toast.LENGTH_SHORT).show();
            }



        }
        else if(parentId == 1){ //countries submenu selected
            selection = countryList.get(menuItem);
            sourcesList.clear();
            if (selection == "all") {
                sourcesList.addAll(categoryMap.get("all"));
            }else {
                ArrayList<String> temp = countryMap.get(selection);
                for (String s : temp) {
                    if (currentSources.contains(s)) { //the previous filter and current filter match
                        sourcesList.add(s);
                    }
                }
            }
            //sourcesList.addAll(temp);
            if (sourcesList.size() == 0){
                AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);
                adBuilder.setTitle("Error");
                adBuilder.setMessage("No News Sources fit this selection of Country: " + item.toString() );

                adBuilder.setPositiveButton("OK, Restore", (dialog, id) -> {
                    sourcesList.addAll(countryMap.get("all"));
                    arrayAdapter.notifyDataSetChanged();
                    setTitle("IIT News (" + sourcesList.size() + ")");
                });

                AlertDialog locationDialog = adBuilder.create();
                locationDialog.show();
                return true;

            }else{
                Collections.sort(sourcesList);
                arrayAdapter.notifyDataSetChanged();
                setTitle("IIT News (" + sourcesList.size() + ")");
                Toast.makeText(this, "Sources List size " + sourcesList.size(), Toast.LENGTH_SHORT).show();
            }
        }
        else if(parentId == 2){ //languages submenu selected
            selection = languageList.get(menuItem);
            sourcesList.clear();
            if (selection == "all"){
                sourcesList.addAll(languageMap.get("all"));
            }else {
                ArrayList<String> temp = languageMap.get(selection);
                for (String s : temp) {
                    if (currentSources.contains(s)) { //the previous filter and current filter match
                        sourcesList.add(s);
                    }
                }
            }
            //sourcesList.addAll(temp);
            if (sourcesList.size() == 0){

                AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);
                adBuilder.setTitle("Error");
                adBuilder.setMessage("No News Sources fit this selection of Language: " + item.toString() );

                adBuilder.setPositiveButton("OK, Restore", (dialog, id) -> {
                    sourcesList.addAll(languageMap.get("all"));
                    arrayAdapter.notifyDataSetChanged();
                    setTitle("IIT News (" + sourcesList.size() + ")");
                });

                AlertDialog locationDialog = adBuilder.create();
                locationDialog.show();
                return true;

            }else{
                Collections.sort(sourcesList);
                arrayAdapter.notifyDataSetChanged();
                setTitle("IIT News (" + sourcesList.size() + ")");
                Toast.makeText(this, "Sources List size " + sourcesList.size(), Toast.LENGTH_SHORT).show();
            }
        }


        return super.onOptionsItemSelected(item);
    }

    private void drawerItemSelected(int position){
        sourceSelected = sourcesList.get(position);
        NewsSource source = sourcesMap.get(sourceSelected);
        String sourceID = source.getNewsID();

        drawerLayout.closeDrawer(drawerList);

        grabArticles(sourceID);

    }



    public void grabArticles(String sourceChoice){
        connection = hasNetworkConnection();
        if (connection == true){
            //Toast.makeText(this, "Network connection established.", Toast.LENGTH_LONG).show();
            ArticlesRunnable articlesRunnable = new ArticlesRunnable(this, sourceChoice);
            new Thread(articlesRunnable).start();

        }else{
            //setTitle("No Network Connection");
            Toast.makeText(this, "No network connection.", Toast.LENGTH_LONG).show();
        }
    }



    public void errorHandle(String errorMsg){
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);
        adBuilder.setTitle("Problem with Data").setMessage(errorMsg)
                .setPositiveButton("OKAY", (dialogInterface, i) -> {}).create().show();

    }

    public void downloadFail(){

        Toast.makeText(this,"Download Failed", Toast.LENGTH_SHORT).show();
    }


    public void dataUpdateArticles(ArrayList<NewsArticle> articleList){
        articlesList.clear();
        articlesList.addAll(articleList);
        articleAdapter.notifyDataSetChanged();

        setTitle(sourceSelected);

        viewPager.setCurrentItem(0);
        drawerLayout.setBackgroundColor(getResources().getColor(R.color.white));
    }

    public void acceptCountryResults(Map<String, String> countryMap){
        countryCodesMap.clear();
        countryCodesMap.putAll(countryMap);
        countryCodesList.clear();
        for(String k : countryCodesMap.keySet()){
            countryCodesList.add(countryCodesMap.get(k));
        }
        Collections.sort(countryCodesList);
        countryCodesList.add(0,"all");

    }

    public void acceptLanguageResults(Map<String, String> languageMap){
        languageCodesMap.clear();
        languageCodesMap.putAll(languageMap);
        languageCodesList.clear();
        for(String k : languageCodesMap.keySet()){
            languageCodesList.add(languageCodesMap.get(k));
        }
        Collections.sort(languageCodesList);
        languageCodesList.add(0,"all");
    }

    private boolean hasNetworkConnection() {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

}
