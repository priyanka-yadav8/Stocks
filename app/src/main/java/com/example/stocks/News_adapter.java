package com.example.stocks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class News_adapter extends RecyclerView.Adapter<News_adapter.MyViewHolder>{

    Context context;
    ArrayList<StockNews> newsArray;
    NewsRVInterface newsRVInterface;
    public News_adapter(Context context, ArrayList<StockNews> newsArray, NewsRVInterface newsRVInterface){
        this.context = context;
        this.newsArray = newsArray;
        this.newsRVInterface = newsRVInterface;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.news_layout, parent, false);
//        view.setOnClickListener();
        return new News_adapter.MyViewHolder(view, newsRVInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvSource.setText(newsArray.get(position).getSource());
        holder.tvTime.setText(newsArray.get(position).getTime()+" hours ago");
        holder.tvHeadline.setText(newsArray.get(position).getHeadline());
        Glide.with(context)
                .load(newsArray.get(position).getImageUrl())
                .into(holder.ivNewsImage);

    }

    @Override
    public int getItemCount() {
        return newsArray.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvSource, tvTime, tvHeadline;
        ImageView ivNewsImage;


        public MyViewHolder(@NonNull View itemView, NewsRVInterface newsRVInterface) {
            super(itemView);
            tvSource = (TextView) itemView.findViewById(R.id.textViewSource);
            tvTime = (TextView) itemView.findViewById(R.id.textViewTime);
            tvHeadline =(TextView) itemView.findViewById(R.id.textViewDesc);
            ivNewsImage = (ImageView) itemView.findViewById(R.id.imageViewNews);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(newsRVInterface!=null){
                        int pos = getAbsoluteAdapterPosition();
                        if(pos!= RecyclerView.NO_POSITION){
                            newsRVInterface.onClick(pos);
                        }
                    }
                }
            });
        }
    }
}
