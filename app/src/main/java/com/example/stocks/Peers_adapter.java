package com.example.stocks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Peers_adapter extends RecyclerView.Adapter<Peers_adapter.MyViewHolder>{

    Context context;
    ArrayList<StockPeers> stockPeers;
    PeersRVInterface peersRVInterface;

    public Peers_adapter(Context context, ArrayList<StockPeers> stockPeers, PeersRVInterface peersRVInterface) {
        this.context = context;
        this.stockPeers = stockPeers;
        this.peersRVInterface = peersRVInterface;
    }

    @NonNull
    @Override
    public Peers_adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.peers_layout, parent, false);
        return new Peers_adapter.MyViewHolder(view, peersRVInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull Peers_adapter.MyViewHolder holder, int position) {
        holder.tvPeer.setText(stockPeers.get(position).getPeer()+", ");
    }

    @Override
    public int getItemCount() {
        return stockPeers.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvPeer;
        public MyViewHolder(@NonNull View itemView, PeersRVInterface peersRVInterface) {

            super(itemView);
            tvPeer = (TextView) itemView.findViewById(R.id.textViewPeerList);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(peersRVInterface!=null){
                        int pos = getAbsoluteAdapterPosition();
                        if(pos!= RecyclerView.NO_POSITION){
                            peersRVInterface.onPeerClick(pos);
                        }
                    }
                }
            });
        }
    }
}
