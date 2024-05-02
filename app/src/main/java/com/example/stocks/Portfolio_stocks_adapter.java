package com.example.stocks;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Portfolio_stocks_adapter extends RecyclerView.Adapter<Portfolio_stocks_adapter.MyViewHolder>{


    Context context;
    ArrayList<portfolioStocks> arrayList;
//    StockArrowInterface arrowInterface;
    int flag;
    public Portfolio_stocks_adapter(Context context,ArrayList<portfolioStocks> arrayList, int flag){
        this.context = context;
        this.arrayList = arrayList;
        this.flag=flag;
//        this.arrowInterface = arrowInterface;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.portfolio_row, parent, false);
        return new Portfolio_stocks_adapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        int pos = position;
        holder.tvPortfolioTicker.setText(arrayList.get(position).getTicker());
        if(flag==0){
            holder.tvPortfolioShares.setText(arrayList.get(position).getShares()+" shares");

        } else{
            holder.tvPortfolioShares.setText(arrayList.get(position).getName());

        }
        holder.tvPortfolioPrice.setText("$"+String.format("%.2f",arrayList.get(position).getPrice()));
        holder.tvPortfolioChange.setText("$"+String.format("%.2f",arrayList.get(position).getChange())+"( "+String.format("%.2f",arrayList.get(position).getPercentageChange())+"% )");
        holder.ivArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context.getApplicationContext(), pos+"", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(context, StockDetails.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle = new Bundle();
                bundle.putString("ticker",arrayList.get(pos).getTicker());
                i.putExtras(bundle);
                context.startActivity(i);

            }
        });
        if(arrayList.get(position).getChange()>=0){
            holder.ivChangePort.setImageResource(R.drawable.trending_up);
            holder.tvPortfolioChange.setTextColor(Color.parseColor("#00A300"));
        } else{
            holder.ivChangePort.setImageResource(R.drawable.trending_down);
            holder.tvPortfolioChange.setTextColor(Color.parseColor("#D10000"));

        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvPortfolioTicker, tvPortfolioShares, tvPortfolioPrice, tvPortfolioChange;
        ImageView ivArrow, ivChangePort;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPortfolioTicker = itemView.findViewById(R.id.textViewPortfolioTicker);
            tvPortfolioPrice = itemView.findViewById(R.id.textViewPortfolioPrice);
            tvPortfolioShares = itemView.findViewById(R.id.textViewPortfolioShares);
            tvPortfolioChange = itemView.findViewById(R.id.textViewPortfolioChange);

            ivArrow = itemView.findViewById(R.id.imageViewArrow);
            ivChangePort = itemView.findViewById(R.id.imageViewChangePort);
//            ivArrow.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(arrowInterface!=null){
//                        int position = getAbsoluteAdapterPosition();
//                        if(position!=RecyclerView.NO_POSITION){
//                            arrowInterface.onArrowClick(position);
//                        }
//                    }
//                }
//            });
        }
    }

}
