package com.example.stocks;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class HourlyPriceChartFragment extends Fragment {


    private WebView webView;
    int color;
    String ticker;
    public HourlyPriceChartFragment(String ticker, int color) {
        // Required empty public constructor
        this.ticker = ticker;
        this.color = color;
    }




    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hourly_price_chart, container, false);
        webView = (WebView) view.findViewById(R.id.hourlyPriceChartWebView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("file:///android_asset/HourlyPrice.html");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webView.evaluateJavascript("javascript:loadChartData('" + ticker + "','" + color + "')", null);
            }
        });
        WebSettings objWeb = webView.getSettings();
        objWeb.setAllowUniversalAccessFromFileURLs(true);
        return view;
    }
}