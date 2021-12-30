package com.example.iitnews;

import android.Manifest;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleViewHolder> {

    private final MainActivity mainActivity;
    private final ArrayList<NewsArticle> articlesList;
    private Picasso picasso;


    public ArticleAdapter (MainActivity mainActivity, ArrayList<NewsArticle> articlesList){
        this.mainActivity = mainActivity;
        this.articlesList = articlesList;
    }


    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        picasso =  Picasso.get();
        return new ArticleViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.article_entry, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        NewsArticle newsArticle = articlesList.get(position);

        if (newsArticle.getTitle().equals("") || newsArticle.getTitle() == null){
            holder.title.setVisibility(View.GONE);
        }else {
            holder.title.setText(newsArticle.getTitle());
        }
        holder.title.setOnClickListener(view -> goToURL(newsArticle.getUrl()));


        if (newsArticle.getPublishDate().equals("") || newsArticle.getPublishDate() == null){
            holder.date.setVisibility(View.GONE);
        }else {
            holder.date.setText(newsArticle.getPublishDate());
        }
        holder.date.setOnClickListener(view -> goToURL(newsArticle.getUrl()));


        if (newsArticle.getAuthor().equals("") || newsArticle.getAuthor() == null){
            holder.author.setVisibility(View.GONE);
        }else {
            holder.author.setText(newsArticle.getAuthor());
        }
        holder.author.setOnClickListener(view -> goToURL(newsArticle.getUrl()));


        if (newsArticle.getDescription().equals("") || newsArticle.getDescription() == null){
            holder.description.setVisibility(View.GONE);
        }else {
            holder.description.setText(newsArticle.getDescription());
            holder.description.setMovementMethod(new ScrollingMovementMethod());
        }

        holder.description.setOnClickListener(view -> goToURL(newsArticle.getUrl()));



        if(newsArticle.getUrlToImage() == null) {
            holder.image.setImageResource(R.drawable.noimage);
        }else{
            boolean connection = hasNetworkConnection();
            if (connection == true){
                //Toast.makeText(this, "Network connection established.", Toast.LENGTH_LONG).show();
                picasso.load(newsArticle.getUrlToImage())
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.loading)
                        .into(holder.image);
            }else{
                //mainActivity.setTitle("No Network Connection");
                Toast.makeText(mainActivity, "No network connection.", Toast.LENGTH_LONG).show();
            }

        }

        holder.image.setOnClickListener(view -> goToURL(newsArticle.getUrl()));

        holder.pageNumber.setText(position+1 + " of " + articlesList.size());


    }

    @Override
    public int getItemCount() {
        return articlesList.size();
    }


    private void goToURL(String url){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        mainActivity.startActivity(intent);
    }

    private boolean hasNetworkConnection() {
        ConnectivityManager connectivityManager = mainActivity.getSystemService(ConnectivityManager.class);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }
}
