package com.example.stocks;

import static java.lang.Double.parseDouble;

import android.app.Dialog;
import android.app.appsearch.SearchResult;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

public class StockDetails extends AppCompatActivity implements NewsRVInterface, PeersRVInterface{
    final DecimalFormat df = new DecimalFormat("#.00");
    Toolbar toolbar;
    ActionBar actionBar;
    ConstraintLayout stockInfo;
    TextView tvTicker, tvName, tvPrice, tvChange, tvSharesOwned, tvAvgCost, tvTotalCost, tvChangePortfolio, tvMarketValue, tvOpenPrice, tvLowPrice, tvHighPrice, tvPrevClose, tvIPO, tvIndustry, tvWebpage, tvPeers, tvTotalMSRP, tvTotalChange, tvPositiveMSRP, tvPositiveChange, tvNegativeMSRP, tvNegativeChange, textViewTableName;
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

    RequestQueue requestQueue;

    String tickerGlobal;

    WebView wvRecommendation, wvSurprise;

    TabLayout tabLayout;
    ViewPager viewPager;

    MenuItem starIcon;

    LinearLayout linearLayout2;
    ProgressBar progressBar2;
    int color;

    double wallet = 0, current_price=0, avg_cost=0;
    int quanity_to_trade=0;
    int shares_owned=0;
    boolean isFav=false;

    String stockName;

    ArrayList<StockNews> newsaArrayList = new ArrayList<>();
    ArrayList<StockPeers> peers_arraylist = new ArrayList<>();
    StockNews removedNews = new StockNews();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stock_details);

        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
        linearLayout2 = (LinearLayout) findViewById(R.id.linearLayoutStockDetails);

        requestQueue = Volley.newRequestQueue(this);

        Bundle bundle = getIntent().getExtras();
        tickerGlobal = bundle.getString("ticker");

        String getWalletUrl = getString(R.string.gcp_url)+"api/wallet/get-wallet";
        JsonObjectRequest getwalletRequest = new JsonObjectRequest(Request.Method.GET, getWalletUrl,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject s) {
                Log.d("wallet", s.toString());
                try {
//                    tvBalance.setText("Cash Balance \n$"+s.getString("wallet"));
                    wallet = parseDouble(s.getString("wallet"));
//                    net_worth=net_worth+wallet;
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("wallet error",volleyError.toString());
            }
        });
        requestQueue.add(getwalletRequest);

        String getOneStockWatchlistUrl = getString(R.string.gcp_url)+"api/watchlist/get-stock-from-watchlist/"+tickerGlobal;
        JsonObjectRequest getOneStockWatchlist = new JsonObjectRequest(Request.Method.GET, getOneStockWatchlistUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                isFav = true;
                try{
                    starIcon.setIcon(isFav ? R.drawable.full_star : R.drawable.star_border);
                } catch (Exception e){

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });


        requestQueue.add(getOneStockWatchlist);
//        requestQueue.add(getOneStockPortfolio);

        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(tickerGlobal);

        stockInfo = (ConstraintLayout) findViewById(R.id.viewStockInfo);
        tvTicker = (TextView) stockInfo.findViewById(R.id.textViewTicker);
        tvName = (TextView) stockInfo.findViewById(R.id.textViewName);
        tvPrice = (TextView) stockInfo.findViewById(R.id.textViewPrice);
        tvChange = (TextView) stockInfo.findViewById(R.id.textViewChange);
        ivChange = (ImageView) stockInfo.findViewById(R.id.imageViewChange);
        ivCompanyIcon = (ImageView) stockInfo.findViewById(R.id.imageViewCompanyIcon);
//        Glide.with(this).load("https://static2.finnhub.io/file/publicdatany/finnhubimage/stock_logo/TSLA.png").into(ivCompanyIcon);

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
        textViewTableName = (TextView) findViewById(R.id.textViewTableName);

        rvPeers = (RecyclerView) findViewById(R.id.recyclerViewPeers);

        //get portfolio request
        String getPortfolioUrl= getString(R.string.gcp_url)+"api/portfolio/get-one-stock-portfolio/"+tickerGlobal.toUpperCase();

        JsonObjectRequest getPortfolioRequest = new JsonObjectRequest(Request.Method.GET, getPortfolioUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    tvSharesOwned.setText(jsonObject.getString("quantity")+"");
                    tvAvgCost.setText("$"+jsonObject.getString("cost_price"));

                    double quantity = Double.parseDouble(jsonObject.getString("quantity"));
                    double cost_price = Double.parseDouble(jsonObject.getString("cost_price"));
                    double total_cost = quantity*cost_price;
                    df.format(total_cost);
                    tvTotalCost.setText(String.format("%.2f",total_cost));
                    double current_price = Double.parseDouble(tvPrice.getText().toString());
                    double change = current_price - cost_price;
                    df.format(change);
                    tvChangePortfolio.setText("$"+String.format("%.2f", change));
                    double market_value = current_price*quantity;
                    tvMarketValue.setText("$"+String.format("%.2f",market_value));
                    if(change>0){
                        tvChangePortfolio.setTextColor(Color.GREEN);
                        tvMarketValue.setTextColor(Color.GREEN);

                    } else {
                        tvChangePortfolio.setTextColor(Color.RED);
                        tvMarketValue.setTextColor(Color.RED);
                    }

                    shares_owned = (int) quantity;
                    avg_cost=cost_price;

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });


        //get stock details API
        String getStockDetailsUrl = getString(R.string.gcp_url)+"api/stocks/get-stock-details";
        JsonObjectRequest getStockDetailsRequest = new JsonObjectRequest(Request.Method.POST, getStockDetailsUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    JSONObject stock_details = jsonObject.getJSONObject("stock_details");
                    Log.d("stock-data-get",stock_details.getString("ticker"));
                    tvTicker.setText(stock_details.getString("ticker"));
                    tvName.setText(stock_details.getString("name"));
                    stockName = stock_details.getString("name");
                    textViewTableName.setText(stock_details.getString("name"));
                    tvPrice.setText(stock_details.getString("last_price"));

                    current_price = Double.parseDouble(stock_details.getString("last_price"));
                    String change = stock_details.getString("change")+"("+stock_details.getString("change_percentage")+")";
                    tvChange.setText(change);
                    if(Double.parseDouble(stock_details.getString("change"))>0){
                        color =1;
                    } else {
                        color =0;
                    }
                    tabLayout = (TabLayout) findViewById(R.id.tabLayout);
                    viewPager = (ViewPager) findViewById(R.id.viewPager);
                    tabLayout.setupWithViewPager(viewPager);
                    Fragment_adapter fragmentAdapter = new Fragment_adapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
                    fragmentAdapter.addToFragment(new HourlyPriceChartFragment(tickerGlobal, color));
                    fragmentAdapter.addToFragment(new HistoricalChartFragment(tickerGlobal));
                    viewPager.setAdapter(fragmentAdapter);
                    tabLayout.getTabAt(0).setIcon(R.drawable.chart_hour);
                    tabLayout.getTabAt(1).setIcon(R.drawable.chart_historical);

                    requestQueue.add(getPortfolioRequest);

                    double changeCheck = Double.parseDouble(stock_details.getString("change"));
                    if(changeCheck>=0){
                        ivChange.setImageResource(R.drawable.trending_up);
                        tvChange.setTextColor(Color.parseColor("#00A300"));

                    } else {
                        ivChange.setImageResource(R.drawable.trending_down);
                        tvChange.setTextColor(Color.parseColor("#D10000"));

                    }
                    Glide.with(getApplicationContext()).load(stock_details.getString("logo")).into(ivCompanyIcon);

                    JSONObject summary = jsonObject.getJSONObject("summary");
                    tvHighPrice.setText("$"+summary.getString("high_price"));
                    tvLowPrice.setText("$"+summary.getString("low_price"));
                    tvOpenPrice.setText("$"+summary.getString("open_price"));
                    tvPrevClose.setText("$"+summary.getString("prev_close"));

                    JSONObject company_details = jsonObject.getJSONObject("company_details");
                    tvIPO.setText(company_details.getString("ipo_start_date"));
                    tvIndustry.setText(company_details.getString("industry"));
                    String htmlWeb = company_details.getString("webpage");
                    tvWebpage.setText(Html.fromHtml(htmlWeb, Html.FROM_HTML_MODE_COMPACT));
                    tvWebpage.setTextColor(Color.parseColor("#4343FF"));
                    tvWebpage.setPaintFlags(tvWebpage.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                    tvWebpage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri uri = Uri.parse(htmlWeb);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                    });



                    JSONArray peers = company_details.getJSONArray("company_peers");
                    for(int i=0;i<peers.length();i++){
                        StockPeers obj = new StockPeers();
                        obj.setPeer(peers.getString(i));
                        peers_arraylist.add(obj);
                        rvPeers.getAdapter().notifyItemInserted(peers_arraylist.size());

                    }

                    progressBar2.setVisibility(View.INVISIBLE);
                    linearLayout2.setVisibility(View.VISIBLE);




                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                JSONObject reqBody = new JSONObject();
                try {
                    reqBody.put("symbol", tickerGlobal);
                    return reqBody.toString().getBytes("utf-8");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        requestQueue.add(getStockDetailsRequest);

        String getInsightsUrl = getString(R.string.gcp_url)+"api/stocks/get-insights";
        JsonObjectRequest getInsightsRequest = new JsonObjectRequest(Request.Method.POST, getInsightsUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    JSONObject insider_sentiments = jsonObject.getJSONObject("insider_sentiments");

                    tvTotalMSRP.setText(insider_sentiments.getString("total_mspr"));
                    tvPositiveMSRP.setText(insider_sentiments.getString("positive_mspr"));
                    tvNegativeMSRP.setText(insider_sentiments.getString("negative_mspr"));
                    tvTotalChange.setText(insider_sentiments.getString("total_change"));
                    tvPositiveChange.setText(insider_sentiments.getString("positive_change"));
                    tvNegativeChange.setText(insider_sentiments.getString("negative_change"));



                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";

            }

            @Override
            public byte[] getBody() {
                JSONObject reqBody = new JSONObject();
                try {
                    reqBody.put("symbol", tickerGlobal);
                    return reqBody.toString().getBytes("utf-8");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        requestQueue.add(getInsightsRequest);



        rvNews = (RecyclerView) findViewById(R.id.recyclerViewNews);


        String getNewsUrl = getString(R.string.gcp_url)+"api/stocks/get-company-news";
        JsonArrayRequest getNewsRequest = new JsonArrayRequest(Request.Method.POST, getNewsUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                for(int i=0;i<jsonArray.length();i++){
                    StockNews obj = new StockNews();
                    try {
                        JSONObject newsObj = jsonArray.getJSONObject(i);
                        obj.setSource(newsObj.getString("source"));
                        obj.setNewsDate(newsObj.getString("datetime"));
                        obj.setHeadline(newsObj.getString("headline"));
                        obj.setDesc(newsObj.getString("summary"));
                        obj.setImageUrl(newsObj.getString("image"));
                        obj.setUrl(newsObj.getString("url"));
                        Random rand = new Random();

                        // Generate random integers in range 0 to 999
                        int rand_int1 = rand.nextInt(5);
                        obj.setTime(rand_int1+i+"");
                        newsaArrayList.add(obj);
                        rvNews.getAdapter().notifyItemInserted(newsaArrayList.size());

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                cvFirstNews = (CardView) findViewById(R.id.viewFirstNews);

                tvSource1 = (TextView) findViewById(R.id.textViewNewsSource);
                tvTime1 = (TextView) findViewById(R.id.textViewNewsHours);
                tvHeadline1 = (TextView) findViewById(R.id.textViewNewsTitle);
                ivNewsImage1 = (ImageView) findViewById(R.id.imageViewNewsImage);

                tvSource1.setText(newsaArrayList.get(0).getSource());
                tvTime1.setText(newsaArrayList.get(0).getTime()+" hours ago");
                tvHeadline1.setText(newsaArrayList.get(0).getHeadline());
                Glide.with(getApplicationContext())
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
                        ivChrome.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Uri uri = Uri.parse(removedNews.getUrl());
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            }
                        });
                        ivX.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String link = removedNews.getUrl();
                                Uri uri = Uri.parse("https://twitter.com/intent/tweet?text=Check out this link:  &url="+link);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);

                            }
                        });

                        ivFacebook.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String link = removedNews.getUrl();
                                String urlToShare = "https://www.facebook.com/sharer/sharer.php?u=" + Uri.encode(link);

                                Uri uri = Uri.parse(urlToShare);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);

                            }
                        });
                    }
                });



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }){

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                JSONObject reqBody = new JSONObject();
                try {
                    reqBody.put("symbol", tickerGlobal);
                    return reqBody.toString().getBytes("utf-8");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        };


        News_adapter news_adapter = new News_adapter(this, newsaArrayList, this);
        rvNews.setAdapter(news_adapter);
        rvNews.setLayoutManager(new LinearLayoutManager(this));
        requestQueue.add(getNewsRequest);


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
        ivX = (ImageView) newsDialog.findViewById(R.id.imageViewX);
        ivFacebook = (ImageView) newsDialog.findViewById(R.id.imageViewFacebook);



        //tvTradeTitle, tvTradeCost, tvTradeBuy;
        tradeDialog = new Dialog(StockDetails.this);
        tradeDialog.setContentView(R.layout.trade_dialog);
        tradeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
        buyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        buyDialog.setContentView(R.layout.buy_dialog);
        buttonDone = (Button) buyDialog.findViewById(R.id.buttonTradeDialogDone);
        tvStatus = (TextView) buyDialog.findViewById(R.id.textViewTradeDialogStatus);

        buttonTrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvTradeTitle.setText("Trade "+tickerGlobal+" shares");
                tvTradeCost.setText("0*$"+String.format("%.2f",current_price)+"/share = 0.00");
                tvTradeBuy.setText("$"+String.format("%.2f",wallet)+" to buy "+tickerGlobal.toUpperCase());
                etQuantity.setText(String.valueOf(0));
                tradeDialog.show();
                buttonBuy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        tvStatus.setText("Bought");
                        if(quanity_to_trade<1){
                            Toast.makeText(StockDetails.this, "Cannot buy non-positive shares", Toast.LENGTH_SHORT).show();
                        } else if(current_price*quanity_to_trade>wallet){
                            Toast.makeText(StockDetails.this, "Not enough money to buy", Toast.LENGTH_SHORT).show();
                        } else {
                            double nayaWallet = wallet - (quanity_to_trade*current_price);
                            String updateWalletUrl = getString(R.string.gcp_url)+"api/wallet/update-wallet";
                            JsonObjectRequest updateWalletRequest = new JsonObjectRequest(Request.Method.PATCH, updateWalletUrl, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject jsonObject) {
                                    tvTradeBuy.setText("$"+String.format("%.2f", nayaWallet)+" to buy "+ tickerGlobal);
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {

                                }
                            }){
                                @Override
                                public String getBodyContentType() {
                                    return "application/json; charset=utf-8";
                                }

                                @Override
                                public byte[] getBody() {
                                    JSONObject reqBody = new JSONObject();
                                    try {
                                        reqBody.put("cash_balance", nayaWallet);
                                        return reqBody.toString().getBytes("utf-8");
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    } catch (UnsupportedEncodingException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            };
                            requestQueue.add(updateWalletRequest);
                            if(shares_owned == 0){
                                shares_owned = quanity_to_trade;
                                avg_cost = current_price;
//                            tvTotalCost.setText("$"+String.format("%.2f",avg_cost*shares_owned));
                                String addToPortfolioUrl = getString(R.string.gcp_url)+"api/portfolio/add-to-portfolio";
                                JSONObject requestBody = new JSONObject();
                                try {
                                    requestBody.put("ticker", tickerGlobal.toUpperCase());
                                    requestBody.put("name", stockName);
                                    requestBody.put("quantity", quanity_to_trade);
                                    requestBody.put("cost_price", current_price);

                                } catch (Exception e){
                                    Log.d("update-portfolio-error", e.getMessage());
                                }
                                JsonObjectRequest addToPortfolioRequest = new JsonObjectRequest(Request.Method.POST, addToPortfolioUrl, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject jsonObject) {
                                        tradeDialog.dismiss();
                                        tvStatus.setText("You have Successfully bough "+quanity_to_trade+ " shares of "+tickerGlobal);
                                        tvSharesOwned.setText(shares_owned+"");
                                        tvAvgCost.setText("$"+String.format("%.2f",avg_cost));
                                        tvTotalCost.setText("$"+String.format("%.2f", avg_cost*shares_owned));
                                        tvChangePortfolio.setText("$"+String.format("%.2f", current_price - avg_cost));
                                        tvMarketValue.setText("$"+String.format("%.2f", current_price*shares_owned));
                                        if(current_price - avg_cost > 0){
                                            tvChangePortfolio.setTextColor(Color.GREEN);
                                            tvMarketValue.setTextColor(Color.GREEN);

                                        } else{
                                            tvChangePortfolio.setTextColor(Color.RED);
                                            tvMarketValue.setTextColor(Color.RED);

                                        }



                                        buyDialog.show();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError volleyError) {

                                    }
                                }){

                                    @Override
                                    public String getBodyContentType() {
                                        return "application/json; charset=utf-8";
                                    }

                                    @Override
                                    public byte[] getBody() {
                                        try {
                                            return requestBody.toString().getBytes("utf-8");
                                        } catch (UnsupportedEncodingException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                };
                                requestQueue.add(addToPortfolioRequest);
                            } else {
                                String updatePortfolioUrl = getString(R.string.gcp_url)+"api/portfolio/update-portfolio/"+tickerGlobal.toUpperCase();
                                int new_quantity = shares_owned + quanity_to_trade;
                                double new_cost_price = (avg_cost + current_price)/2;
                                shares_owned = new_quantity;
                                avg_cost = new_cost_price;

                                JSONObject requestBody = new JSONObject();
                                try {
                                    requestBody.put("quantity", new_quantity);
                                    requestBody.put("cost_price",new_cost_price);
                                } catch (Exception e){
                                    Log.d("update-portfolio-error", String.valueOf(e));
                                }
                                JsonObjectRequest updatePortfolioRequest = new JsonObjectRequest(Request.Method.PATCH, updatePortfolioUrl, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject jsonObject) {
                                        tradeDialog.dismiss();
                                        tvStatus.setText("You have Successfully bought "+quanity_to_trade+ " shares of "+tickerGlobal);
                                        tvSharesOwned.setText(shares_owned+"");
                                        tvAvgCost.setText("$"+String.format("%.2f",avg_cost));
                                        tvTotalCost.setText("$"+String.format("%.2f", avg_cost*shares_owned));
                                        tvChangePortfolio.setText("$"+String.format("%.2f",current_price-avg_cost));
                                        tvMarketValue.setText("$"+String.format("%.2f", current_price* shares_owned));
                                        if(current_price - avg_cost > 0){
                                            tvChangePortfolio.setTextColor(Color.GREEN);
                                            tvMarketValue.setTextColor(Color.GREEN);

                                        } else{
                                            tvChangePortfolio.setTextColor(Color.RED);
                                            tvMarketValue.setTextColor(Color.RED);

                                        }

                                        buyDialog.show();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError volleyError) {

                                    }
                                }){

                                    @Override
                                    public String getBodyContentType() {
                                        return "application/json; charset=utf-8";
                                    }

                                    @Override
                                    public byte[] getBody() {
                                        try {
                                            return requestBody.toString().getBytes("utf-8");
                                        } catch (UnsupportedEncodingException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                };
                                requestQueue.add(updatePortfolioRequest);

                            }
                        }




//                        buyDialog.show();
                    }
                });
                buttonSell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        tvStatus.setText("Bought");
                        if(quanity_to_trade<1){
                            Toast.makeText(StockDetails.this, "Cannot sell non-positive shares", Toast.LENGTH_SHORT).show();
                        } else if(quanity_to_trade>shares_owned){
                            Toast.makeText(StockDetails.this, "Not enough shares to sell", Toast.LENGTH_SHORT).show();
                        } else {
                            double nayaWallet = wallet + (quanity_to_trade*current_price);
                            String updateWalletUrl = getString(R.string.gcp_url)+"api/wallet/update-wallet";
                            JsonObjectRequest updateWalletRequest = new JsonObjectRequest(Request.Method.PATCH, updateWalletUrl, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject jsonObject) {
                                    tvTradeBuy.setText("$"+String.format("%.2f",nayaWallet)+" to buy "+ tickerGlobal);

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {

                                }
                            }){
                                @Override
                                public String getBodyContentType() {
                                    return "application/json; charset=utf-8";
                                }

                                @Override
                                public byte[] getBody() {
                                    JSONObject reqBody = new JSONObject();
                                    try {
                                        reqBody.put("cash_balance", nayaWallet);
                                        return reqBody.toString().getBytes("utf-8");
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    } catch (UnsupportedEncodingException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            };
                            requestQueue.add(updateWalletRequest);

                            if(shares_owned == quanity_to_trade){
                                shares_owned = 0;
                                avg_cost = 0.00;
                                String deletePortfolioUrl = getString(R.string.gcp_url)+"api/portfolio/remove-from-portfolio/"+tickerGlobal.toUpperCase();

                                JsonObjectRequest deletePortfolioRequest = new JsonObjectRequest(Request.Method.DELETE, deletePortfolioUrl, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject jsonObject) {
                                        tradeDialog.dismiss();
                                        tvStatus.setText("You have Successfully sold "+quanity_to_trade+ " shares of "+tickerGlobal);
                                        tvSharesOwned.setText(shares_owned+"");
                                        tvAvgCost.setText("$"+String.format("%.2f",avg_cost));
                                        tvTotalCost.setText("$"+ String.format("%.2f", shares_owned*avg_cost));
                                        tvChangePortfolio.setText("$"+0);
                                        tvMarketValue.setText("$"+String.format("%.2f", current_price*shares_owned));
                                        buyDialog.show();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError volleyError) {

                                    }
                                });
                                requestQueue.add(deletePortfolioRequest);
                            } else {
                                String updatePortfolioUrl = getString(R.string.gcp_url)+"api/portfolio/update-portfolio/"+tickerGlobal.toUpperCase();
                                int new_quantity = shares_owned - quanity_to_trade;
//                            double new_cost_price = (avg_cost + current_price)/2;
                                shares_owned = new_quantity;
//                            avg_cost = new_cost_price;

                                JSONObject requestBody = new JSONObject();
                                try {
                                    requestBody.put("quantity", new_quantity);
                                    requestBody.put("cost_price",avg_cost);
                                } catch (Exception e){
                                    Log.d("update-portfolio-error", String.valueOf(e));
                                }
                                JsonObjectRequest updatePortfolioRequest = new JsonObjectRequest(Request.Method.PATCH, updatePortfolioUrl, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject jsonObject) {
                                        tradeDialog.dismiss();
                                        tvStatus.setText("You have Successfully sold "+quanity_to_trade+ " shares of "+tickerGlobal);
                                        tvSharesOwned.setText(shares_owned+"");
                                        tvAvgCost.setText("$"+String.format("%.2f",avg_cost));
                                        tvTotalCost.setText("$"+String.format("%.2f", shares_owned*avg_cost));
                                        tvChangePortfolio.setText("$"+String.format("%.2f", current_price - avg_cost));
                                        tvMarketValue.setText("$"+String.format("%.2f",current_price*shares_owned));
                                        if(current_price - avg_cost > 0){
                                            tvChangePortfolio.setTextColor(Color.GREEN);
                                            tvMarketValue.setTextColor(Color.GREEN);

                                        } else{
                                            tvChangePortfolio.setTextColor(Color.RED);
                                            tvMarketValue.setTextColor(Color.RED);

                                        }

                                        buyDialog.show();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError volleyError) {

                                    }
                                }){

                                    @Override
                                    public String getBodyContentType() {
                                        return "application/json; charset=utf-8";
                                    }

                                    @Override
                                    public byte[] getBody() {
                                        try {
                                            return requestBody.toString().getBytes("utf-8");
                                        } catch (UnsupportedEncodingException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                };
                                requestQueue.add(updatePortfolioRequest);

                            }

                        }



//                        buyDialog.show();
                    }
                });
            }
        });

        etQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s==null || s==""|| s=="0"){
                    quanity_to_trade = 0;

                } else{
                    try{
                        quanity_to_trade = Integer.parseInt(s.toString());

                    } catch (Exception e){
                        quanity_to_trade =0;
                    }
                }
                tvTradeCost.setText(quanity_to_trade+"*$"+String.format("%.2f",current_price)+"/share = "+String.format("%.2f", current_price*quanity_to_trade));
            }

            @Override
            public void afterTextChanged(Editable s) {

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

//        cvFirstNews = (CardView) findViewById(R.id.viewFirstNews);
//
//        tvSource1 = (TextView) findViewById(R.id.textViewNewsSource);
//        tvTime1 = (TextView) findViewById(R.id.textViewNewsHours);
//        tvHeadline1 = (TextView) findViewById(R.id.textViewNewsTitle);
//        ivNewsImage1 = (ImageView) findViewById(R.id.imageViewNewsImage);
//
//        tvSource1.setText(newsaArrayList.get(0).getSource());
//        tvTime1.setText(newsaArrayList.get(0).getTime());
//        tvHeadline1.setText(newsaArrayList.get(0).getHeadline());
//        Glide.with(this)
//                .load(newsaArrayList.get(0).getImageUrl())
//                .into(ivNewsImage1);
//        removedNews = newsaArrayList.get(0);
//        newsaArrayList.remove(0);
//        cvFirstNews.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                tvSource.setText(removedNews.getSource());
//                tvDate.setText(removedNews.getNewsDate());
//                tvHeadline.setText(removedNews.getHeadline());
//                tvDesc.setText(removedNews.getDesc());
//                tvDate.setText(removedNews.getNewsDate());
//                newsDialog.show();
//            }
//        });

//        createPeersList();
        Peers_adapter peers_adapter = new Peers_adapter(this,peers_arraylist, this);
        rvPeers.setAdapter(peers_adapter);
        rvPeers.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));


        wvRecommendation = (WebView) findViewById(R.id.webViewRecommendationChart);
        wvRecommendation.getSettings().setJavaScriptEnabled(true);
        wvRecommendation.setWebViewClient(new WebViewClient());
        wvRecommendation.loadUrl("file:///android_asset/RecommendationChart.html");
        wvRecommendation.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                wvRecommendation.evaluateJavascript("javascript:loadChartData('" + tickerGlobal + "')", null);
            }
        });
        WebSettings objWeb = wvRecommendation.getSettings();
        objWeb.setAllowUniversalAccessFromFileURLs(true);

        wvSurprise = (WebView) findViewById(R.id.webViewSurpriseChart);
        wvSurprise.getSettings().setJavaScriptEnabled(true);
        wvSurprise.setWebViewClient(new WebViewClient());
        wvSurprise.loadUrl("file:///android_asset/SurpriseChart.html");
        wvSurprise.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                wvSurprise.evaluateJavascript("javascript:loadChartData('" + tickerGlobal + "')", null);
            }
        });
        WebSettings objWeb2 = wvSurprise.getSettings();
        objWeb2.setAllowUniversalAccessFromFileURLs(true);

//        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
//        viewPager = (ViewPager) findViewById(R.id.viewPager);
//        tabLayout.setupWithViewPager(viewPager);
//        Fragment_adapter fragmentAdapter = new Fragment_adapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
//        fragmentAdapter.addToFragment(new HourlyPriceChartFragment(tickerGlobal, color));
//        fragmentAdapter.addToFragment(new HistoricalChartFragment(tickerGlobal));
//        viewPager.setAdapter(fragmentAdapter);
//        tabLayout.getTabAt(0).setIcon(R.drawable.chart_hour);
//        tabLayout.getTabAt(1).setIcon(R.drawable.chart_historical);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }

    public void createNewsArray(){
        for(int i = 0; i<5;i++){

        }
    };

    public void createPeersList(){
        for(int i=0;i<4;i++){
            StockPeers obj = new StockPeers();
            obj.setPeer("AAPL"+i);
            peers_arraylist.add(obj);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stocks_menu, menu);
        starIcon = menu.findItem(R.id.infoStar);
        if(isFav){
            starIcon.setIcon(R.drawable.full_star);
        } else{
            starIcon.setIcon(R.drawable.star_border);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.infoStar){
            if(isFav){
                item.setIcon(R.drawable.star_border);
                isFav = false;
                Toast.makeText(this, tickerGlobal+" is removed from the favourites", Toast.LENGTH_SHORT).show();
            } else {
                String addToWatchlistUrl = getString(R.string.gcp_url)+"api/watchlist/add-to-watchlist";
                JSONObject requestBody = new JSONObject();
                try {
                    requestBody.put("ticker", tickerGlobal.toUpperCase());
                    requestBody.put("name", tvName.getText());


                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                JsonObjectRequest addToWatchlistRequest = new JsonObjectRequest(Request.Method.POST, addToWatchlistUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        item.setIcon(R.drawable.full_star);
                        isFav = true;
                        Toast.makeText(StockDetails.this, tickerGlobal+" is added to Watchlist", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                }){

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() {
                        try {

                            return requestBody.toString().getBytes("utf-8");
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };
                requestQueue.add(addToWatchlistRequest);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.finish();
        return super.onSupportNavigateUp();
    }

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
                Uri uri = Uri.parse(newsaArrayList.get(position).getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        ivX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = newsaArrayList.get(position).getUrl();
                Uri uri = Uri.parse("https://twitter.com/intent/tweet?text=Check out this link:  &url="+link);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

            }
        });

        ivFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = newsaArrayList.get(position).getUrl();
                String urlToShare = "https://www.facebook.com/sharer/sharer.php?u=" + Uri.encode(link);

                Uri uri = Uri.parse(urlToShare);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

            }
        });
    }

    @Override
    public void onPeerClick(int position) {
        Toast.makeText(StockDetails.this, position+"", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(StockDetails.this, StockDetails.class);
        Bundle bundle = new Bundle();
        bundle.putString("ticker", peers_arraylist.get(position).getPeer());
        i.putExtras(bundle);
        startActivity(i);

    }
}