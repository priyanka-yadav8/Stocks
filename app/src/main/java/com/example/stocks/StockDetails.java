package com.example.stocks;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class StockDetails extends AppCompatActivity implements NewsRVInterface, PeersRVInterface{

    ConstraintLayout stockInfo;
    TextView tvTicker, tvName, tvPrice, tvChange, tvSharesOwned, tvAvgCost, tvTotalCost, tvChangePortfolio, tvMarketValue, tvOpenPrice, tvLowPrice, tvHighPrice, tvPrevClose, tvIPO, tvIndustry, tvWebpage, tvPeers, tvTotalMSRP, tvTotalChange, tvPositiveMSRP, tvPositiveChange, tvNegativeMSRP, tvNegativeChange;
    ImageView ivChange;
    ImageView ivCompanyIcon;

    RecyclerView rvNews, rvPeers;

    Dialog newsDialog, tradeDialog, buyDialog;

    //NewsModal
    TextView tvSource, tvDate, tvHeadline, tvDesc;
    ImageView ivChrome, ivX, ivFacebook;

    //TradeModal
    TextView tvTradeTitle, tvTradeCost, tvTradeBuy;
    EditText etQuantity;

    //BuyModal
    TextView tvStatus;

    Button buttonBuy, buttonSell, buttonDone, buttonTrade;

    //NewsFirst
    TextView tvSource1, tvTime1, tvHeadline1;
    ImageView ivNewsImage1;
    CardView cvFirstNews;


    ArrayList<StockNews> newsaArrayList = new ArrayList<>();
    ArrayList<StockPeers> peers_arraylist = new ArrayList<>();
    StockNews removedNews = new StockNews();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stock_details);

        stockInfo = (ConstraintLayout) findViewById(R.id.viewStockInfo);
        tvTicker = (TextView) stockInfo.findViewById(R.id.textViewTicker);
        tvName = (TextView) stockInfo.findViewById(R.id.textViewName);
        tvPrice = (TextView) stockInfo.findViewById(R.id.textViewPrice);
        tvChange = (TextView) stockInfo.findViewById(R.id.textViewChange);
        ivChange = (ImageView) stockInfo.findViewById(R.id.imageViewChange);
        ivCompanyIcon = (ImageView) stockInfo.findViewById(R.id.imageViewCompanyIcon);
        Glide.with(this).load("https://static2.finnhub.io/file/publicdatany/finnhubimage/stock_logo/TSLA.png").into(ivCompanyIcon);

        tvSharesOwned = (TextView) findViewById(R.id.textViewSharesOwned);
        tvAvgCost = (TextView) findViewById(R.id.textViewAvgCost);
        tvTotalCost = (TextView) findViewById(R.id.textViewTotalCost);
        tvChangePortfolio = (TextView) findViewById(R.id.textViewChangePortfolio);
        tvMarketValue = (TextView) findViewById(R.id.textViewMarketValue);
        tvOpenPrice = (TextView) findViewById(R.id.textViewOpenPrice);
        tvLowPrice = (TextView) findViewById(R.id.textViewLowPrice);
        tvHighPrice = (TextView) findViewById(R.id.textViewHighPrice);
        tvPrevClose = (TextView) findViewById(R.id.textViewPrevClose);
        tvIPO = (TextView) findViewById(R.id.textViewIPOStart);
        tvIndustry = (TextView) findViewById(R.id.textViewIndustry);
        tvWebpage = (TextView) findViewById(R.id.textViewWebPage);
//        tvPeers = (TextView) findViewById(R.id.textViewPeers);
        String htmlStr = "<a href='https://www.apple.com'>https://www.apple.com</a>";

        tvTotalMSRP = (TextView) findViewById(R.id.textViewTotalMSRP);
        tvTotalChange = (TextView) findViewById(R.id.textViewTotalChange);
        tvPositiveMSRP = (TextView) findViewById(R.id.textViewPositiveMSRP);
        tvPositiveChange = (TextView) findViewById(R.id.textViewPositiveChange);
        tvNegativeMSRP = (TextView) findViewById(R.id.textViewNegativeMSRP);
        tvNegativeChange = (TextView) findViewById(R.id.textViewNegativeChange);

        tvTicker.setText("AAPL");
        tvName.setText("Apple Inc.");
        tvChange.setText("$1.22(0.71%)");
        tvPrice.setText("$172.58");

        tvSharesOwned.setText("2");
        tvAvgCost.setText("$176.25");
        tvTotalCost.setText("$352.50");
        tvChangePortfolio.setText("$-0.04");

        tvMarketValue.setText("$352.46");

        tvOpenPrice.setText("$171.65");
        tvLowPrice.setText("$170.06");
        tvHighPrice.setText("$172.94");
        tvPrevClose.setText("$171.37");

        tvIPO.setText("12-11-1980");
        tvIndustry.setText("Technology");
        tvWebpage.setText(Html.fromHtml(htmlStr, Html.FROM_HTML_MODE_COMPACT));
//        tvPeers.setText("AAPL, DELL, AAPL, SMCI");

        tvTotalMSRP.setText("-100.00");
        tvTotalChange.setText("-2765634.0");
        tvPositiveMSRP.setText("200.0");
        tvPositiveChange.setText("8764522.0");
        tvNegativeMSRP.setText("-854.26");
        tvNegativeChange.setText("-3540118.0");

        rvNews = (RecyclerView) findViewById(R.id.recyclerViewNews);
        createNewsArray();
        News_adapter news_adapter = new News_adapter(this, newsaArrayList, this);
        rvNews.setAdapter(news_adapter);
        rvNews.setLayoutManager(new LinearLayoutManager(this));

        buttonTrade = (Button) findViewById(R.id.buttonTrade);
        buttonTrade.setBackgroundColor(Color.parseColor("#0fa028"));

        newsDialog = new Dialog(StockDetails.this);
        newsDialog.setContentView(R.layout.news_popup);
        //tvSource, tvDate, tvHeadline, tvDesc
        tvSource = (TextView) newsDialog.findViewById(R.id.textViewSourcePopup);
        tvDate = (TextView) newsDialog.findViewById(R.id.textViewDate);
        tvHeadline = (TextView) newsDialog.findViewById(R.id.textViewHeadline);
        tvDesc = (TextView) newsDialog.findViewById(R.id.textViewDescription);
        ivChrome = (ImageView) newsDialog.findViewById(R.id.imageViewChrome);


        //tvTradeTitle, tvTradeCost, tvTradeBuy;
        tradeDialog = new Dialog(StockDetails.this);
        tradeDialog.setContentView(R.layout.trade_dialog);
        tvTradeTitle = (TextView) tradeDialog.findViewById(R.id.textViewTradeDialogTitle);
        tvTradeCost = (TextView) tradeDialog.findViewById(R.id.textViewTradeDialogCost);
        tvTradeBuy = (TextView) tradeDialog.findViewById(R.id.textViewTradeDialogToBuy);
        etQuantity = (EditText) tradeDialog.findViewById(R.id.editTextNumberTradeDialog);
        buttonBuy = (Button) tradeDialog.findViewById(R.id.buttonTradeDialogBuy);
        buttonSell = (Button) tradeDialog.findViewById(R.id.buttonTradeDialogSell);
        buttonBuy.setBackgroundColor(Color.parseColor("#0fa028"));
        buttonSell.setBackgroundColor(Color.parseColor("#0fa028"));

        newsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        buyDialog = new Dialog(StockDetails.this);
        buyDialog.setContentView(R.layout.buy_dialog);
        buttonDone = (Button) buyDialog.findViewById(R.id.buttonTradeDialogDone);
        tvStatus = (TextView) buyDialog.findViewById(R.id.textViewTradeDialogStatus);

        buttonTrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvTradeTitle.setText("Trade Apple Shares");
                tvTradeCost.setText("000");
                tvTradeBuy.setText("$25000 to buy AAPL");

                tradeDialog.show();
                buttonBuy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvStatus.setText("Bought");
                        tradeDialog.dismiss();

                        buyDialog.show();
                    }
                });
                buttonSell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvStatus.setText("Sold");
                        tradeDialog.dismiss();
                        buyDialog.show();

                    }
                });
            }
        });

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyDialog.dismiss();
            }
        });

        buttonDone.setBackgroundColor(Color.parseColor("#0fa028"));
//        First News Card

        cvFirstNews = (CardView) findViewById(R.id.viewFirstNews);

        tvSource1 = (TextView) findViewById(R.id.textViewNewsSource);
        tvTime1 = (TextView) findViewById(R.id.textViewNewsHours);
        tvHeadline1 = (TextView) findViewById(R.id.textViewNewsTitle);
        ivNewsImage1 = (ImageView) findViewById(R.id.imageViewNewsImage);

        tvSource1.setText(newsaArrayList.get(0).getSource());
        tvTime1.setText(newsaArrayList.get(0).getTime());
        tvHeadline1.setText(newsaArrayList.get(0).getHeadline());
        Glide.with(this)
                .load(newsaArrayList.get(0).getImageUrl())
                .into(ivNewsImage1);
        removedNews = newsaArrayList.get(0);
        newsaArrayList.remove(0);
        cvFirstNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSource.setText(removedNews.getSource());
                tvDate.setText(removedNews.getNewsDate());
                tvHeadline.setText(removedNews.getHeadline());
                tvDesc.setText(removedNews.getDesc());
                tvDate.setText(removedNews.getNewsDate());
                newsDialog.show();
            }
        });

        rvPeers = (RecyclerView) findViewById(R.id.recyclerViewPeers);
        createPeersList();
        Peers_adapter peers_adapter = new Peers_adapter(this,peers_arraylist, this);
        rvPeers.setAdapter(peers_adapter);
        rvPeers.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }

    public void createNewsArray(){
        for(int i = 0; i<5;i++){
            StockNews obj = new StockNews();
            obj.setSource("Yahoo"+i);
            obj.setTime("5");
            obj.setNewsDate("25 April, 2024");
            obj.setHeadline("Hello blah blah blah");
            obj.setImageUrl("https://www.macworld.com/wp-content/uploads/2023/12/apple-wonderlust-event-no-words-iphone-15-1.jpg?quality=50&strip=all");
            obj.setDesc("This is the description of the news.This is the description of the news.This is the description of the news.This is the description of the news.This is the description of the news.This is the description of the news.This is the description of the news.");
            newsaArrayList.add(obj);
        }
    };

    public void createPeersList(){
        for(int i=0;i<4;i++){
            StockPeers obj = new StockPeers();
            obj.setPeer("AAPL"+i);
            peers_arraylist.add(obj);
        }
    };

    //tvSource, tvDate, tvHeadline, tvDesc
    @Override
    public void onClick(int position) {
//        Toast.makeText(StockDetails.this, position+"", Toast.LENGTH_SHORT).show();
        tvSource.setText(newsaArrayList.get(position).getSource());
        tvDate.setText(newsaArrayList.get(position).getNewsDate());
        tvHeadline.setText(newsaArrayList.get(position).getHeadline());
        tvDesc.setText(newsaArrayList.get(position).getDesc());
        tvDate.setText(newsaArrayList.get(position).getNewsDate());

        newsDialog.show();
        ivChrome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.finnhub.io/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onPeerClick(int position) {
        Toast.makeText(StockDetails.this, position+"", Toast.LENGTH_SHORT).show();

    }
}