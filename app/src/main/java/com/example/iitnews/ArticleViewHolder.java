package com.example.iitnews;

import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ArticleViewHolder extends RecyclerView.ViewHolder {

    TextView title;
    TextView date;
    TextView author;
    TextView description;
    ImageView image;
    TextView pageNumber;




    public ArticleViewHolder(@NonNull View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.titleTextView);
        date = itemView.findViewById(R.id.dateTextView);
        author = itemView.findViewById(R.id.authorTextView);
        description = itemView.findViewById(R.id.descriptionTextView);
        image = itemView.findViewById(R.id.imageView);
        pageNumber = itemView.findViewById(R.id.pageNumber);



    }
}
