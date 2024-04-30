package com.example.stocks;

import static java.lang.Double.parseDouble;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity implements StockArrowInterface{

    RequestQueue requestQueue;

    Toolbar toolbar;

    ActionBar actionBar;
    double wallet;
    TextView tvPortfolio, tvWorth, tvBalance, tvFavs, tvFooter;
    EditText etDate;
    ConstraintLayout portfolioStats;

    RecyclerView rvPortfolio, rvFavourites;
    double net_worth=0.0;

    ImageView ivArrow;

    ArrayList<portfolioStocks> portfolio_arraylist = new ArrayList<>();
    ArrayList<portfolioStocks> favourites_arraylist = new ArrayList<>();

    Bundle bundle = new Bundle();

//    ArrayList<StockPeers> peers_arraylist = new ArrayList<>();
    final DecimalFormat df = new DecimalFormat("0.00");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();


        requestQueue = Volley.newRequestQueue(this);


        //get wallet volley request
        String getWalletUrl = getString(R.string.gcp_url)+"api/wallet/get-wallet";
        JsonObjectRequest getwalletRequest = new JsonObjectRequest(Request.Method.GET, getWalletUrl,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject s) {
                Log.d("wallet", s.toString());
                try {
                    tvBalance.setText("Cash Balance \n$"+s.getString("wallet"));
                    wallet = parseDouble(s.getString("wallet"));
                    net_worth=net_worth+wallet;
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







        tvPortfolio = (TextView) findViewById(R.id.textViewPortfolio);
        tvPortfolio.setText("PORTFOLIO");
        tvPortfolio.setBackgroundColor(Color.parseColor("#E1E1E1"));

        etDate = (EditText) findViewById(R.id.editTextDate);
        String date = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date());
        etDate.setText(date);

        portfolioStats = (ConstraintLayout) findViewById(R.id.portfolioStatsCard);
        tvWorth = (TextView) portfolioStats.findViewById(R.id.textViewWorth);
        tvBalance = (TextView) portfolioStats.findViewById(R.id.textViewBalance);
//        tvWorth.setText("Net Worth \n$25000.00");
//        tvBalance.setText("Cash Balance \n$25000.00");
        requestQueue.add(getwalletRequest);

        tvFavs = (TextView) findViewById(R.id.textViewFavs);
        tvFavs.setBackgroundColor(Color.parseColor("#E1E1E1"));

        tvFooter = (TextView) findViewById(R.id.footer);

        tvFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.finnhub.io/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
//                startActivity(new Intent(MainActivity.this, StockDetails.class));
            }
        });


        rvPortfolio = (RecyclerView) findViewById(R.id.recyclerViewPortfolio);

        //get portfolio request
        String getPortfolioUrl= getString(R.string.gcp_url)+"api/portfolio/get-portfolio";
        JsonObjectRequest getPortfolioRequest = new JsonObjectRequest(Request.Method.GET, getPortfolioUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("portfolio", jsonObject.toString());
                try {
                    JSONArray responseArray = jsonObject.getJSONArray("portfolio");
                    for(int i=0;i<responseArray.length();i++){
                        portfolioStocks obj = new portfolioStocks();

                        JSONObject jsonObject1 = responseArray.getJSONObject(i);
                        int quantity = jsonObject1.getInt("quantity");
                        Log.d("jsonn object",jsonObject1.toString());
                        double cost_price = jsonObject1.getDouble("cost_price");
                        String ticker = jsonObject1.getString("ticker");

//                        portfolio_arraylist.add(obj);
                        String getStockQuoteUrl = getString(R.string.gcp_url)+"api/stocks/get-stock-quote";
                        JsonObjectRequest stockRequest = new JsonObjectRequest(Request.Method.POST, getStockQuoteUrl, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                try {
                                    Log.d("stock-quote", jsonObject.toString());
                                    double price = parseDouble(jsonObject.getString("last_price"));
                                    double change = parseDouble(jsonObject.getString("change"));
                                    double change_percentage = parseDouble(jsonObject.getString("change_percentage"));

                                    Log.d("price-in-response", String.valueOf(price));
                                    double market_value = price*quantity;
                                    net_worth=net_worth+market_value;
                                    tvWorth.setText("Net Worth \n$"+net_worth);

                                    df.format(market_value);
                                    obj.setPrice(market_value);
                                    obj.setTicker(ticker);
                                    obj.setShares(quantity);
                                    double change_to_display = (price-cost_price)*quantity;

                                    double total_cost_of_stock = cost_price*quantity;
                                    double change_per_to_display = (change_to_display/total_cost_of_stock)*100;
                                    //cuurentprice-costprice * quantity
                                    df.format(change_to_display);
                                    df.format(change_per_to_display);
                                    obj.setChange(Double.parseDouble(String.format("%.2f", change_to_display)));
                                    obj.setPercentageChange(Double.parseDouble(String.format("%.2f", change_per_to_display)));

                                    Log.d("object-quote",String.valueOf(obj.getPrice()));
                                    portfolio_arraylist.add(obj);
                                    rvPortfolio.getAdapter().notifyItemInserted(portfolio_arraylist.size());
//                                    Log.d("favrourites_array",String.valueOf(favourites_arraylist.get(i).getPrice()));

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
                                    reqBody.put("symbol", ticker);
                                    return reqBody.toString().getBytes("utf-8");
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                } catch (UnsupportedEncodingException e) {
                                    throw new RuntimeException(e);
                                }

                            }
                        };
//                        Portfolio_stocks_adapter portfolio_adapter = new Portfolio_stocks_adapter(getApplicationContext(),portfolio_arraylist, 0);
//                        rvPortfolio.setAdapter(portfolio_adapter);
//                        rvPortfolio.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        requestQueue.add(stockRequest);

                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("portfolio error",volleyError.toString());

            }
        });

        Portfolio_stocks_adapter portfolio_adapter = new Portfolio_stocks_adapter(getApplicationContext(),portfolio_arraylist, 0);
        rvPortfolio.setAdapter(portfolio_adapter);
        rvPortfolio.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        requestQueue.add(getPortfolioRequest);

        ItemTouchHelper itemTouchHelperPortfolio = new ItemTouchHelper(simpleCallbackPortfolio);
        itemTouchHelperPortfolio.attachToRecyclerView(rvPortfolio);

        rvFavourites = (RecyclerView) findViewById(R.id.recyclerViewFavourites);
        String getWatchlistUrl = getString(R.string.gcp_url)+"api/watchlist/get-watchlist";
        JsonObjectRequest getWatchlistRequest = new JsonObjectRequest(Request.Method.GET, getWatchlistUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("watchlist", jsonObject.toString());
                try {
                    JSONArray resArray = jsonObject.getJSONArray("watchlist");
                    for(int i=0;i<resArray.length();i++){
                        portfolioStocks obj = new portfolioStocks();
                        JSONObject jsonObject1 = resArray.getJSONObject(i);
                        String ticker = jsonObject1.getString("ticker");
                        String name = jsonObject1.getString("name");
                        final double[] price = new double[1];

                        String getStockQuoteUrl = getString(R.string.gcp_url)+"api/stocks/get-stock-quote";
                        JsonObjectRequest stockRequest = new JsonObjectRequest(Request.Method.POST, getStockQuoteUrl, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                try {
                                    Log.d("stock-quote", jsonObject.toString());
                                    price[0] = parseDouble(jsonObject.getString("last_price"));
                                    double change = parseDouble(jsonObject.getString("change"));
                                    double change_percentage = parseDouble(jsonObject.getString("change_percentage"));

                                    Log.d("price-in-response", String.valueOf(price[0]));
                                    obj.setPrice(price[0]);
                                    obj.setTicker(ticker);
                                    obj.setName(name);
                                    obj.setChange(change);
                                    obj.setPercentageChange(change_percentage);
                                    Log.d("object-quote",String.valueOf(obj.getPrice()));
                                    favourites_arraylist.add(obj);
                                    rvFavourites.getAdapter().notifyItemInserted(favourites_arraylist.size());
//                                    Log.d("favrourites_array",String.valueOf(favourites_arraylist.get(i).getPrice()));

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
                                    reqBody.put("symbol", ticker);
                                    return reqBody.toString().getBytes("utf-8");
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                } catch (UnsupportedEncodingException e) {
                                    throw new RuntimeException(e);
                                }

                            }
                        };

                        Portfolio_stocks_adapter favourites_adapter = new Portfolio_stocks_adapter(getApplicationContext(), favourites_arraylist, 1);
                        rvFavourites.setAdapter(favourites_adapter);
                        rvFavourites.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                        requestQueue.add(stockRequest);

                        Log.d("price[0]",String.valueOf(price[0]));


//                        rvFavourites.getAdapter().notifyItemInserted(i);


                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });

        requestQueue.add(getWatchlistRequest);

//        Portfolio_stocks_adapter favourites_adapter = new Portfolio_stocks_adapter(this, favourites_arraylist, 1, this);
//        rvFavourites.setAdapter(favourites_adapter);
//        rvFavourites.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper itemTouchHelperFavourites = new ItemTouchHelper(simpleCallbackFavourites);
        itemTouchHelperFavourites.attachToRecyclerView(rvFavourites);




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }

    ItemTouchHelper.SimpleCallback simpleCallbackPortfolio = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            Collections.swap(portfolio_arraylist,fromPosition, toPosition);
            rvPortfolio.getAdapter().notifyItemMoved(fromPosition,toPosition);
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };

    ItemTouchHelper.SimpleCallback simpleCallbackFavourites = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            Collections.swap(favourites_arraylist,fromPosition, toPosition);
            rvFavourites.getAdapter().notifyItemMoved(fromPosition,toPosition);
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
//            String deleteFavroutiesUrl = getString(R.string.gcp_url)+"api/watchlist/remove-from-watchlist";
//            JsonObjectRequest deleteFavRequest = new JsonObjectRequest(Request.Method.DELETE, deleteFavroutiesUrl, null, new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject jsonObject) {
//                    try {
//                        String res = jsonObject.getString("message");
//                        System.out.println(res+"response in delete");
//                    } catch (JSONException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError volleyError) {
//
//                }
//            }){
//                @Override
//                public String getBodyContentType() {
//                    return "application/json; charset=utf-8";
//                }
//
//                @Override
//                public byte[] getBody() {
//                    JSONObject reqBody = new JSONObject();
//                    try {
//                        System.out.println(favourites_arraylist.get(position).getTicker()+"delete this ticker");
//                        reqBody.put("symbol", favourites_arraylist.get(position).getTicker());
//                        return reqBody.toString().getBytes("utf-8");
//                    } catch (JSONException e) {
//                        throw new RuntimeException(e);
//                    } catch (UnsupportedEncodingException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            };
//            requestQueue.add(deleteFavRequest);
            favourites_arraylist.remove(position);
            rvFavourites.getAdapter().notifyItemRemoved(position);

        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(Color.parseColor("#eb3200"))
                            .addSwipeLeftActionIcon(R.drawable.delete)
                                    .create().decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem menuItem = menu.findItem(R.id.searchBar);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//               Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MainActivity.this, StockDetails.class);
                bundle.putString("ticker",query);
                i.putExtras(bundle);
                startActivity(i);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onArrowClick(int position) {
    }
}